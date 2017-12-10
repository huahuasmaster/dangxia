package dangxia.com.utils;


import android.util.Log;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/7/14.
 */

public abstract class HttpCallbackListener {

    public abstract void onFinish(String response);

    public void onError(Exception e) {
        if (e instanceof IOException) {
            // io 异常
            Log.e("网络错误", e.getMessage());
            return;
        }
    }
}
