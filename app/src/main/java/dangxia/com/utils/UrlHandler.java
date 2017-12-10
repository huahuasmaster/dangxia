package dangxia.com.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import dangxia.com.application.ContextApplication;


/**
 * 记录、生成请求的辅助类
 * 所有网络请求地址在此处维护
 * Created by zhuang_ge on 2017/8/10.
 */

public class UrlHandler {


    private static String loginPort = "8081";
    private static String port = "8081";


    private static SharedPreferences loginSp = ContextApplication
            .getContext()
            .getSharedPreferences("login_data", Context.MODE_PRIVATE);

    //获取登录url
    public static String getLoginUrl() {
        return "http://" + getLoginIp() + ":" + loginPort + "/user/login";
    }

    //注销url
    public static String getLogoutUrl() {
        return "http://" + getLoginIp() + ":" + loginPort + "/smart-sso-server/app/logout";
    }

    //注册
    public static String getRegisterUrl() {
        return getHead() + "/user/register";
    }

    //设备列表
    public static String getDeviceList() {
        return getHead() + "/device";
    }

    //请求设备上线
    public static String getDeviceOnline() {
        return getHead() + "/device";
    }


    /**
     * 获取(除了登录接口)请求的开头ip与端口
     *
     * @return
     */
    public static String getHead() {
        return "http://" + getIp() + ":" + port;
    }

    //获取服务器ip
    public static String getIp() {
        return loginSp.getString("ip", "192.168.43.31");
    }

    //设置服务器ip
    public static void setIp(String ip) {
        loginSp.edit().putString("ip", ip).apply();
    }

    //获取登录ip
    public static String getLoginIp() {
        return loginSp.getString("login_ip", "192.168.43.31");
    }

    //设置登录ip
    public static void setLoginIp(String ip) {
        loginSp.edit().putString("login_ip", ip).apply();
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        UrlHandler.port = port;
    }

    public static String getLoginPort() {
        return loginPort;
    }

    public static void setLoginPort(String loginPort) {
        UrlHandler.loginPort = loginPort;
    }

    public static long getUserId() {
        return loginSp.getLong("user_id", -1L);
    }

    public static void setUserId(long userId) {
        loginSp.edit().putLong("user_id", userId).apply();
    }


}
