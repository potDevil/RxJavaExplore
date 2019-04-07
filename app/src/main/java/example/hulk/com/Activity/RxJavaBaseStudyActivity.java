package example.hulk.com.Activity;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import example.fastec.hulk.com.rxjava.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * rxJava学习参考：https://www.jianshu.com/p/e19f8ed863b1
 */
public class RxJavaBaseStudyActivity extends AppCompatActivity {

    private Observable<Integer> observable;
    private Observer<Integer> observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        rxJavaInduction();
//        rxJavaQuickCreate();
        rxJavaConversion();

    }

    /**
     * rxJava基本使用入门
     */
    private void rxJavaInduction() {
        createObservable();
        createObserver();
        observable.subscribe(observer);
    }

    /**
     * 基本使用入门-创建被观察者
     */
    private void createObservable() {
        // 创建一个被观察者
        observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });

        // 还可以通过以下方式创建观察者
        Observable<String> observable1 = Observable.just("A", "B", "C");
        String[] strings = {"A", "B", "C"};
        Observable<String> observable2 = Observable.fromArray(strings);
    }

    /**
     * 基本使用入门创建观察者
     */
    private void createObserver() {
        // 采用Observer作为观察者对象
        observer = new Observer<Integer>() {

            private Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                Log.i("fuzhi", "开始采用subscribe连接");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                Log.i("fuzhi", "对next事件作出响应" + integer);
                if (integer == 2) {
                    mDisposable.dispose();
                    Log.i("fuzhi", "已经切断了连接" + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i("fuzhi", "对error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.i("fuzhi", "对complete事件作出响应");
            }
        };

        // 采用subscriber作为观察者对象
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onSubscribe(Subscription s) {
                Log.i("fuzhi", "开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer integer) {
                Log.i("fuzhi", "对next事件作出响应" + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.i("fuzhi", "对error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.i("fuzhi", "对complete事件作出响应");
            }
        };

    }

    /**
     * 基本使用入门-链式调用
     */
    private void createCompleteMethod() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * rxJava的快速创建&发送事件
     */
    public void rxJavaQuickCreate() {

        // just 最多只能发送10个参数
//        Observable.just("1","2","3","4")
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.i("fuzhi", "just");
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        Log.i("fuzhi", "just:" + s);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i("fuzhi", "just:complete");
//                    }
//                });

//        Integer[] integers = {1, 2, 3, 4, 5};
//        // fromArray最多只能发射10个元素，直接传入一个数组，只当做一个元素
//        Observable.fromArray(integers)
//                .subscribe(new Observer<Object>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.i("fuzhi", "fromArray");
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        Log.i("fuzhi", "fromArray:" + o);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i("fuzhi", "fromArray:complete");
//                    }
//                });
//
//        List<Integer> list = new ArrayList<>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        // fromIterable最多只能发射10个元素，直接传入一个集合，只当做一个元素
//        Observable.fromIterable(list)
//                .subscribe(new Observer<Integer>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Integer integer) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });

        // 延时2秒发送指定事件，一般用于检测，5表示5，TimeUnit.SECONDS表示单位，所以为5秒
//        Observable.timer(5, TimeUnit.SECONDS)
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.i("fuzhi", "连接");
//                    }
//
//                    @Override
//                    public void onNext(Long value) {
//                        Log.i("fuzhi", "接收事件" + value);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.i("fuzhi", "事件失败");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i("fuzhi", "事件完成");
//                    }
//                });

        // range 第一个参数表示从2开始，一共执行5个参数
        Observable.range(2, 5)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("fuzhi", "连接");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i("fuzhi", "接收事件" + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.i("fuzhi", "事件完成");
                    }
                });

    }

    @SuppressLint("CheckResult")
    private void rxJavaConversion() {
        // map可以将被观察者发送的事件转换成任意类型的事件
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) {
//                e.onNext(1);
//                e.onNext(2);
//                e.onNext(3);
//            }
//        }).map(new Function<Integer, String>() {
//            @Override
//            public String apply(Integer integer) throws Exception {
//                return "从integer类型的" + integer + "转换成String类型的" + integer;
//            }
//        }).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                Log.i("fuzhi", s);
//            }
//        });

        // 将被观察者发送的事件序列进行拆分&单独转换，再合并成一个新的事件序列，再进行发送
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                e.onNext(1);
//                e.onNext(2);
//                e.onNext(3);
//            }
//        }).flatMap(new Function<Integer, ObservableSource<String>>() {
//
//            @Override
//            public ObservableSource<String> apply(Integer integer) throws Exception {
//                final List<String> list = new ArrayList<>();
//                for (int i = 0; i < 3; i++) {
//                    list.add("我是事件" + integer + "拆分后的子事件" + i);
//                }
//                return Observable.fromIterable(list);
//            }
//        }).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                Log.i("fuzhi", s);
//            }
//        });
    }
}
