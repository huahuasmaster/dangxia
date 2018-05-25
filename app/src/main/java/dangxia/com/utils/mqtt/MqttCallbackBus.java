package dangxia.com.utils.mqtt;


import com.lichfaker.log.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 使用EventBus分发事件
 *
 * @author LichFaker on 16/3/25.
 * @Email lichfaker@gmail.com
 */
public class MqttCallbackBus implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        Logger.e(cause.getMessage());

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Logger.e(topic + "====" + message.toString() +
                "==== 当前队列长度:" + SubPubQueue.getMsgQueue().size());
//        Log.e("mqtt","新消息");
        MqttMsgBean bean = new MqttMsgBean(topic, message);
        SubPubQueue.getMsgQueue().add(bean);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
