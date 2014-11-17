package wph.iframework.push.mqtt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import wph.iframework.startup.Config;

class Mqtt implements Runnable, MqttCallback
{
    private static final Logger logger    = Logger.getLogger(Mqtt.class);
    private volatile MqttClient mqttClient;
    private volatile boolean    isRunning = false;
    
    class Message
    {
        public String topic;
        public String message;
    }
    
    private final LinkedBlockingQueue<Message> messages;
    
    public Mqtt()
    {
        messages = new LinkedBlockingQueue<Message>();
    }
    
    /**
     * 开启MQTT
     */
    public synchronized void startup()
    {
        if (!isRunning)
        {
            // 考虑到异步情况，isRunning必须在线程开启之前设置
            isRunning = true;
            
            new Thread(this).start();
        }
    }
    
    /**
     * 关闭MQTT
     */
    public synchronized void shutdown()
    {
        if (isRunning)
        {
            isRunning = false;
            
            if (mqttClient != null && mqttClient.isConnected())
            {
                try
                {
                    mqttClient.disconnect(0);
                }
                catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 发布消息
     * 
     * @param topic
     *            主题
     * @param msg
     *            消息
     */
    public void publish(String topic, String msg)
    {
        Message m = new Message();
        m.topic = topic;
        m.message = msg;
        messages.add(m);
    }
    
    /**
     * 循环发布
     */
    private void publish()
    {
        while (isRunning)
        {
            try
            {
                Message m = null;
                while (null == (m = messages.poll(1, TimeUnit.SECONDS)) && isRunning)
                {
                    Thread.yield();
                }
                
                if (null != m)
                {
                    MqttTopic mqttTopic = mqttClient.getTopic(m.topic);
                    MqttMessage mm = new MqttMessage(m.message.getBytes("UTF-8"));
                    mm.setQos(1);
                    boolean ok = false;
                    while (!ok)
                    {
                        try
                        {
                            mqttTopic.publish(mm);
                            logger.debug(m.topic);
                            logger.debug(m.message);
                            ok = true;
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
    }
    
    @Override
    public void run()
    {
        // 连接到MQTT代理服务器
        connect();
        
        logger.info("Mqtt startup!");
        
        // 循环发布主题
        publish();
        
        logger.debug("Mqtt shutdown!");
    }
    
    @Override
    public void connectionLost(Throwable cause)
    {
        cause.printStackTrace();
        mqttClient = null;
        connect();
    }
    
    /**
     * 连接到MQTT代理服务器
     */
    private synchronized void connect()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        Random random = new Random();
        // 若ImOnlineMqtt服务正在运行，并且mqttClient没有连接成功，那么继续
        while (isRunning && null == mqttClient)
        {
            try
            {
                String format = "%s%03d";
                String id = String.format(format, dateFormat.format(new Date()), (random.nextInt() & 0xFFFF) % 1000);
                mqttClient = new MqttClient(Config.BROKER_URL, id, new MemoryPersistence());
                mqttClient.setCallback(this);
                // 连接选项
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setConnectionTimeout(5000); // 连接超时5秒
                options.setKeepAliveInterval(30000); // 心跳间隔为30秒
                // 连接
                mqttClient.connect(options);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                
                // 若已经连接，则断开连接
                try
                {
                    mqttClient.disconnect(0);
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
                mqttClient = null;
                
                // 抛出当前时间片
                Thread.yield();
            }
        }
    }
    
    @Override
    public void deliveryComplete(MqttDeliveryToken token)
    {
    }
    
    @Override
    public void messageArrived(MqttTopic mt, MqttMessage msg) throws Exception
    {
    }
}
