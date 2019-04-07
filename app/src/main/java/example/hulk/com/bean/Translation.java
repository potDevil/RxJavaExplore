package example.hulk.com.bean;

import android.util.Log;

/**
 * Created by fuzhi on 2019/4/7
 */
public class Translation {

    private int status;
    private content content;

    private static class content{
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    public void show() {
        Log.i("fuzhi", content.out);
    }

    public String returnString() {
        return content.out;
    }
}
