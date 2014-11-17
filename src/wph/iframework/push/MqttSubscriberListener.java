package wph.iframework.push;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public interface MqttSubscriberListener
{
    public void messageArrived(MqttTopic mt, MqttMessage msg);
}
