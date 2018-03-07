package dangxia.com.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;

/**
 * Created by zhuang_ge on 2018/3/1.
 */

public class MsgReceiver extends MzPushMessageReceiver {

    private static final String TAG = "msgreceiver";

    @Override
    public void onRegister(Context context, String s) {
        Log.i(TAG, "onRegister: 注册成功啦");
    }

    @Override
    public void onUnRegister(Context context, boolean b) {

    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {

    }

    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {

    }

    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {

    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {

    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {

    }

    @Override
    public void onMessage(Context context, Intent intent) {
        super.onMessage(context, intent);
        Log.i(TAG, "onMessage: 收到消息啦");
    }
}
