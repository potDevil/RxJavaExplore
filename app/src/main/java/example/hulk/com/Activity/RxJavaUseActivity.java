package example.hulk.com.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import example.fastec.hulk.com.rxjava.R;
import example.hulk.com.bean.Translation;
import example.hulk.com.net.GetRequstInterface;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
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
 * 实现合并数据源&同时展示                      参考:https://www.jianshu.com/p/fc2e551b907c
 * 模拟三级缓存读取                             参考:https://www.jianshu.com/p/6f3b6b934787
 * 网络请求重新连接                             参考:https://www.jianshu.com/p/508c30aef0c1
 */
public class RxJavaUseActivity extends AppCompatActivity {

    private static final String TAG = "devil";
    // 设置变量 = 模拟轮询服务器次数
    private int i = 0;
    // 可重试次数
    private int maxConnectCount = 5;
    // 当前已重试次数
    private int currentRetryCount = 0;
    // 重试等待时间
    private int waitRetryTime = 0;

    private Observable<Translation> observable;
    private Observable<Translation> observableRegister;
    private Observable<Translation> observableLogin;
    private Observable<Translation> observableData;
    private Observable<Translation> observableInformation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNet();
//        noConditionPull();
//        conditionPull();
//        nestRequest();
//        mergeData();
//        threeCache();
        retryNetRequest();
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
        observableData = requstInterface.getData().subscribeOn(Schedulers.io());
        observableInformation = requstInterface.getInformation().subscribeOn(Schedulers.io());
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
            public ObservableSource<?> apply(Observable<Object> objectObservable) {
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) {
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
    @SuppressLint("CheckResult")
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

    /**
     * 实现合并数据源&同时展示
     */
    @SuppressLint("CheckResult")
    private void mergeData() {
        Observable.zip(observableData, observableInformation,
                new BiFunction<Translation, Translation, String>() {
                    @Override
                    public String apply(Translation translation, Translation translation2) {
                        return translation.returnString() + "&" + translation2.returnString();
                    }
                }).observeOn(AndroidSchedulers.mainThread())        // 在主线程里接收并处理数据
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        Log.i(TAG, "最终接收的数据结果为:" + s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.d(TAG, "网络请求失败");
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }, new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                });
    }

    /**
     * 模拟三级缓存的读取
     */
    private void threeCache() {
        final String memoryCache = null;
        final String diskCache = "从磁盘缓存中获取数据";

        Observable memory = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) {
                if (memoryCache != null) {
                    e.onNext(memoryCache);
                } else {
                    e.onComplete();
                }
            }
        });

        Observable disk = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) {
                if (diskCache != null) {
                    e.onNext(diskCache);
                } else {
                    e.onComplete();
                }
            }
        });

        Observable net = Observable.just("网络获取");

        Observable.concat(memory, disk, net)
                .subscribe(new Observer() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {
                        Log.i(TAG, value + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "失败");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "完成");
                    }
                });
    }

    /**
     * 网络重连
     */
    private void retryNetRequest() {
        observable.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) {
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) {
                        // 输出异常信息
                        Log.i(TAG, "发生异常 = " + throwable.toString());

                        if (throwable instanceof IOException) {
                            Log.i(TAG, "属于IO异常，需重试");

                            if (currentRetryCount < maxConnectCount) {
                                currentRetryCount++;
                                Log.d(TAG, "重试次数=" + currentRetryCount);
                                waitRetryTime = 1000 + currentRetryCount * 1000;
                                Log.i(TAG, "重试时间=" + waitRetryTime);
                                return Observable.just(1).delay(waitRetryTime, TimeUnit.MILLISECONDS);
                            } else {
                                return Observable.error(new Throwable("重试次数已经超过设置次数=" + currentRetryCount + "即不再重试"));
                            }
                        } else {
                            return Observable.error(new Throwable("发生了非网络异常(非IO异常)"));
                        }
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
                    public void onNext(Translation value) {
                        Log.i(TAG, "发送成功");
                        value.show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
