package example.hulk.com.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import example.fastec.hulk.com.rxjava.R;
import example.hulk.com.bean.Translation;
import example.hulk.com.net.GetRequstInterface;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fuzhi on 2019/4/7
 * 实现网络轮询(无条件)                          参考:https://www.jianshu.com/p/11b3ec672812
 * 实现网络轮询(有条件)                          参考:https://www.jianshu.com/p/dbeaaa4afad5
 * 实现网络请求的嵌套(比如注册+登录一步实现)       参考:https://www.jianshu.com/p/5f5d61f04f96
 */
public class RxJavaUseActivity extends AppCompatActivity {

    private static final String TAG = "fuzhi";
    // 设置变量 = 模拟轮询服务器次数
    private int i = 0;

    private Observable<Translation> observable;
    private Observable<Translation> observableRegister;
    private Observable<Translation> observableLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNet();
//        noConditionPull();
//        conditionPull();
        nestRequest();
    }

    private void createNet() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 创建网络请求实例
        GetRequstInterface requstInterface = retrofit.create(GetRequstInterface.class);
        observable = requstInterface.getCall();
        observableRegister = requstInterface.getRegister();
        observableLogin = requstInterface.getLogin();
    }

    /**
     * 实现网络轮询(无条件)
     */
    private void noConditionPull() {
        Observable.interval(2, 1, TimeUnit.SECONDS)
                // 参数1:第1次延迟事件，参数2:间隔时间，参数3:时间单位
                //doOnNext表示每次发送数字前发送一次网络请求，从而实现轮询请求
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG, "第" + aLong + "轮询");

                        // 创建retrofit对象

                        observable.subscribeOn(Schedulers.io()) // 切换到io线程进行网络请求
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Translation>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Translation result) {
                                        result.show();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i(TAG, "失败");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long value) {

            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "对Complete事件作出响应");
            }
        });
    }

    /**
     * 实现网络轮询(有条件)
     */
    private void conditionPull() {
        observable.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        if (i > 3) {
                            return Observable.error(new Throwable("轮询结束"));
                        }
                        return Observable.just(1).delay(2000, TimeUnit.MILLISECONDS);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Translation result) {
                        result.show();
                        i++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "结束轮询");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 实现网络请求的嵌套
     */
    private void nestRequest() {
        observableRegister.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Translation>() {
                    @Override
                    public void accept(Translation translation) {
                        Log.d(TAG, "第一次网络请求成功");
                        translation.show();
                    }
                })
                .observeOn(Schedulers.io())                     // 切换到io线程进行第二次网络请求
                .flatMap(new Function<Translation, ObservableSource<Translation>>() {
                    @Override
                    public ObservableSource<Translation> apply(Translation translation) {
                        return observableLogin;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())      // 在主线程里处理第二次网络请求的数据
                .subscribe(new Consumer<Translation>() {
                    @Override
                    public void accept(Translation translation) {
                        Log.d(TAG, "第二次网络请求成功");
                        translation.show();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.d(TAG, "第二次网络请求失败");
                    }
                });
    }
}
