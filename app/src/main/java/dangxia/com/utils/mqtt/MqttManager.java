package dangxia.com.utils.mqtt;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.lichfaker.log.Logger;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import dangxia.com.R;
import dangxia.com.application.ContextApplication;
import dangxia.com.entity.MessageDto;
import dangxia.com.ui.MainActivity;

/**
 * 管理mqtt的连接,发布,订阅,断开连接, 断开重连等操作
 * <p>
 * 需要根据用户的id订阅对应的主题 用户信息储存在“login_data”sharedpreferences中
 *
 * @author LichFaker on 16/3/24.
 * @Email lichfaker@gmail.com
 */
public class MqttManager {


    //用于存取数据
    private static SharedPreferences sp;

    private static SharedPreferences.Editor editor;

    //mqtt服务器端口
    private static String port = "1883";

    //appId
    private static String appId = "1";

    // 单例
    private static MqttManager mInstance = null;

    public boolean needNotify = true;//是否需要notify通知

    // 回调
    private MqttCallback mCallback;

    // Private instance variables
    private MqttClient client;
    private MqttConnectOptions conOpt;
    private boolean clean = true;

    private MqttManager() {
        mCallback = new MqttCallbackBus();
    }

    public static MqttManager getInstance() {
        if (null == mInstance) {
            mInstance = new MqttManager();
            sp = ContextApplication
                    .getContext()
                    .getSharedPreferences("login_data", Context.MODE_PRIVATE);
        }
        return mInstance;
    }

    public void startQueue() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        final MqttMsgBean topicMessage = SubPubQueue.getMsgQueue().take();
                        Logger.e("广播消息:" + topicMessage.getMqttMessage().toString());
                        //广播消息
                        String msg = topicMessage.getMqttMessage().toString();
                        MessageDto messageDto = new Gson().fromJson(msg, MessageDto.class);
                        new Thread(() -> EventBus.getDefault().post(messageDto)).start();
//                            if (!needNotify) return;
//                            @SuppressLint("SimpleDateFormat") DateFormat dateFormat =
//                                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            Context mContext = ContextApplication.getContext();
//                            Logger.i("开始进行横幅通知");
//                            NotificationManager manager = (NotificationManager)
//                                    mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//                            Intent intent = new Intent(mContext, MainActivity.class);
//                            intent.putExtra("check_msg", true);
//                            PendingIntent pendingIntent = PendingIntent
//                                    .getActivity(mContext, 0, intent, 0);
//                            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher_round);
//                            Notification notification = new Notification.Builder(mContext)
//                                    .setContentTitle(messageDto.getSenderName())
//                                    .setContentText(messageDto.getContent())
//                                    .setWhen(dateFormat.parse(messageDto.getDate()).getTime())
//                                    .setAutoCancel(true)
//                                    .setContentIntent(pendingIntent)
//                                    .setDefaults(Notification.DEFAULT_ALL)
//                                    .setSmallIcon(R.mipmap.ic_launcher)
//                                    .setLargeIcon(bitmap)
//                                    .setPriority(Notification.PRIORITY_MAX)
//                                    .build();
//                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//                            manager.notify(2, notification);

                    } catch (InterruptedException /*| ParseException*/ e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

        };
        Logger.e("队列开始启动");
        thread.start();
    }

    /**
     * 释放单例, 及其所引用的资源
     */
    public static void release() {
        try {
            if (mInstance != null) {
                mInstance.disConnect();
                mInstance = null;
            }
        } catch (Exception e) {

        }
    }

    /**
     * 获取url
     *
     * @return
     */
    private static String getUrl() {
//        return "tcp://"+ UrlHandler.getIp()+":"+port;
        return "tcp://" + "121.40.140.223" + ":" + port;
    }


//    /**
//     * 获取特定mqtt主题-设备信息
//     */
//    public static String getDeviceInfoTopic() {
//        return "/lpwa/app/"+appId+"/info/"+sp.getLong("user_id",-1L);
//    }

    /**
     * 获取特定mqtt主题-聊天消息
     */
    public static String getMsgTopic() {
        return "/dangxia/app/msg/" + sp.getInt("user_id", -1);
    }

    /**
     * 使用默认信息进行mqtt连接
     */
    public boolean creatConnect() {
        return creatConnect(getUrl(),
                /*sp.getString("account",""),*/
                /*sp.getString("password",""),*/
                null,
                null,
                String.valueOf(sp.getInt("user_id", -1)));
    }

    /**
     * 创建Mqtt 连接
     *
     * @param brokerUrl Mqtt服务器地址(tcp://xxxx:1863)
     * @param userName  用户名
     * @param password  密码
     * @param clientId  clientId
     * @return
     */
    public boolean creatConnect(String brokerUrl, String userName, String password, String clientId) {
        boolean flag = false;
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            // Construct the connection options object that contains connection parameters
            // such as cleanSession and LWT
            conOpt = new MqttConnectOptions();
            conOpt.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            conOpt.setCleanSession(clean);
            if (password != null) {
                conOpt.setPassword(password.toCharArray());
            }
            if (userName != null) {
                conOpt.setUserName(userName);
            }

            // Construct an MQTT blocking mode client
            client = new MqttClient(brokerUrl, clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(mCallback);
            flag = doConnect();
        } catch (MqttException e) {
            Logger.e(e.getMessage());
        }

        return flag;
    }

    /**
     * 建立连接
     *
     * @return
     */
    public boolean doConnect() {
        boolean flag = false;
        if (client != null) {
            try {
                client.connect(conOpt);
                Logger.e("Connected to " + client.getServerURI() + " with client ID " + client.getClientId());
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * Publish / send a message to an MQTT server
     *
     * @param topicName the name of the topic to publish to
     * @param qos       the quality of service to delivery the message at (0,1,2)
     * @param payload   the set of bytes to send to the MQTT server
     * @return boolean
     */
    public boolean publish(String topicName, int qos, byte[] payload) {

        boolean flag = false;

        if (client != null && client.isConnected()) {

            Logger.d("Publishing to topic \"" + topicName + "\" qos " + qos);

            // Create and configure a message
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos);

            // Send the message to the server, control is not returned until
            // it has been delivered to the server meeting the specified
            // quality of service.
            try {
                Logger.e("topic = " + topicName + "- msg = " + message);
                client.publish(topicName, message);
                flag = true;
            } catch (MqttException e) {

            }

        }

        return flag;
    }

    /**
     * 使用默认信息进行mqtt主题注册
     * 目前为根据用户id注册对应的data、info主题
     */
    public boolean subscribe() {
        Logger.i("开始订阅主题");
        return subscribe(getMsgTopic(), 0);
    }

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     *
     * @param topicName to subscribe to (can be wild carded)
     * @param qos       the maximum quality of service to receive messages at for this subscription
     * @return boolean
     */
    public boolean subscribe(String topicName, int qos) {

        boolean flag = false;

        if (client != null && client.isConnected()) {
            // Subscribe to the requested topic
            // The QoS specified is the maximum level that messages will be sent to the client at.
            // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
            // be downgraded to 1 when delivering to the client but messages published at 1 and 0
            // will be received at the same level they were published at.
            Logger.e("Subscribing to topic \"" + topicName + "\" qos " + qos);
            try {
                client.subscribe(topicName, qos);
                flag = true;
            } catch (MqttException e) {
                Logger.e(e.getMessage());
            }
        }

        return flag;

    }

    /**
     * 取消连接
     */
    public void disConnect() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    /**
     * 重新连接
     */
    public void reConnect() throws MqttException {
        disConnect();
        creatConnect();
    }

    /**
     * 是否连接
     */
    public boolean isConnecting() {
        return client != null && client.isConnected();
    }

    public boolean isNeedNotify() {
        return needNotify;
    }

    public void setNeedNotify(boolean needNotify) {
        this.needNotify = needNotify;
    }
}
