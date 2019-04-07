package example.hulk.com.net;


import example.hulk.com.bean.Translation;
import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by fuzhi on 2019/4/7
 */
public interface GetRequstInterface {

    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20world")
    Observable<Translation> getCall();

    /**
     * 模拟注册的网络请求
     */
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20register")
    Observable<Translation> getRegister();

    /**
     * 模拟登录的网络请求
     */
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20login")
    Observable<Translation> getLogin();
}
