package example.hulk.com.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import example.fastec.hulk.com.rxjava.R;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;

/**
 * Created by fuzhi on 2019/4/7
 * RxJava联合判断多个事件       参考:https://www.jianshu.com/p/2becc0eaedab
 */
public class RxJavaUniteJudge extends AppCompatActivity {
    private static final String TAG = "devil";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava_unite_judge);
        final EditText name, age, job;
        final Button commit;

        name = findViewById(R.id.et_name);
        age = findViewById(R.id.et_age);
        job = findViewById(R.id.et_job);
        commit = findViewById(R.id.bt_commit);

        /*
         * 步骤2：为每个EditText设置被观察者，用于发送监听事件
         * 说明：
         * 1. 此处采用了RxBinding：RxTextView.textChanges(name) = 对对控件数据变更进行监听（功能类似TextWatcher），需要引入依赖：compile 'com.jakewharton.rxbinding2:rxbinding:2.0.0'
         * 2. 传入EditText控件，点击任1个EditText撰写时，都会发送数据事件 = Function3（）的返回值（下面会详细说明）
         * 3. 采用skip(1)原因：跳过 一开始EditText无任何输入时的空值
         **/
        Observable<CharSequence> nameObservable = RxTextView.textChanges(name).skip(1);
        Observable<CharSequence> ageObservable = RxTextView.textChanges(age).skip(1);
        Observable<CharSequence> jobObservable = RxTextView.textChanges(job).skip(1);

        Observable.combineLatest(nameObservable, ageObservable, jobObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
                boolean isUserNameValid = !TextUtils.isEmpty(name.getText());
                boolean isUserAgeValid = !TextUtils.isEmpty(age.getText());
                boolean isUserJobValid = !TextUtils.isEmpty(job.getText());
                return isUserNameValid && isUserAgeValid && isUserJobValid;
            }
        }).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean b) {
                Log.i(TAG, "按钮是否可点击" + b);
                commit.setEnabled(b);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
