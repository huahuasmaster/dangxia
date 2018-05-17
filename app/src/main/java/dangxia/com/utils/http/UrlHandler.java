package dangxia.com.utils.http;

import android.content.Context;
import android.content.SharedPreferences;

import dangxia.com.application.ContextApplication;


/**
 * 记录、生成请求的辅助类
 * 所有网络请求地址在此处维护
 * Created by zhuang_ge on 2017/8/10.
 */

public class UrlHandler {


    private static String loginPort = "8081";
    private static String port = "8081";
    private static final String cloudIp = "140.143.225.154";
    private static final String localIp = "192.168.1.102";
    private static final boolean onCloud = false;


    private static SharedPreferences loginSp = ContextApplication
            .getContext()
            .getSharedPreferences("login_data", Context.MODE_PRIVATE);

    //获取登录url
    public static String getLoginUrl() {
        return "http://" + getLoginIp() + ":" + loginPort + "/user/login";
    }

    //根据id获取某个任务的详细信息

    //获取附近所有任务
    public static String getAllTask(double latitude, double longitude, double radius) {
        return getHead() + "/task/" + latitude + "/" + longitude + "/" + radius + "/nearby";
    }

    //获取附近的快速任务
    public static String getQuickTask(double latitude, double longitude, double radius) {
        return getHead() + "/task/" + latitude + "/" + longitude + "/" + radius + "/nearbyQuick";

    }

    //获取自己领取的任务
    public static String getMyTask() {
        return getHead() + "/task/" + getUserId() + "/accepted";
    }

    //发布任务
    public static String postTask() {
        return getHead() + "/task/" + getUserId();
    }

    //注册
    public static String getRegisterUrl() {
        return getHead() + "/user/register";
    }

    //获取与自己有关的消息
    public static String getConversationAboutMe() {
        return getHead() + "/conversation/" + getUserId() + "/list";
    }

    //查询预估价格
    public static String getPriceEvaluation() {
        return getHead() + "/task/evaluation";
    }

    //接单
    public static String takeOrder() {
        return getHead() + "/order";
    }

    public static String getOrderByTask(int taskId) {
        return getHead() + "/order/" + taskId + "/byTask";
    }

    public static String getCon(int id) {
        return getHead() + "/conversation/" + id;
    }

    //发出申请（会话）
    public static String initConversation(int taskId) {
        return getHead() + "/conversation/" + getUserId() + "/" + taskId;
    }

    //发送消息
    public static String pushMsg(int conId) {
        return getHead() + "/conversation/" + conId + "/push";
    }

    //获取会话中的聊天记录
    public static String getMsgList(int conId) {
        return getHead() + "/conversation/" + conId + "/msglist";
    }

    public static String finishOrder(int id) {
        return getHead() + "/order/" + id;
    }

    public static String getTaskClasses() {
        return getHead() + "/task/classes";
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
        return onCloud ? cloudIp : localIp;
    }

    //设置服务器ip
    public static void setIp(String ip) {
        loginSp.edit().putString("ip", ip).apply();
    }

    //获取登录ip
    public static String getLoginIp() {
        return onCloud ? cloudIp : localIp;
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

    public static int getUserId() {
        return loginSp.getInt("user_id", -1);
    }

    public static void setUserId(int userId) {
        loginSp.edit().putInt("user_id", userId).apply();
    }


}
