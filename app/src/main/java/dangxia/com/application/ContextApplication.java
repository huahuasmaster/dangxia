package dangxia.com.application;

import android.app.Application;
import android.content.Context;

import com.mob.MobApplication;


/**
 * Created by zhuang_ge on 2017/8/23.
 */

public class ContextApplication extends MobApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//        LitePalApplication.initialize(context);
    }

    public static Context getContext() {
        return context;
    }
}
