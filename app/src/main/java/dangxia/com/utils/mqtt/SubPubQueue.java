package dangxia.com.utils.mqtt;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息队列 mqtt消息统一存储在队列中
 */
public class SubPubQueue {
    private static BlockingQueue<MqttMsgBean> msgQueue = new LinkedBlockingQueue<>(100);

    public static BlockingQueue<MqttMsgBean> getMsgQueue() {
        return msgQueue;
    }
}
