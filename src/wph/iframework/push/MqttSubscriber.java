package wph.iframework.push;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

class MqttSubscriber implements Runnable, MqttCallback
{
    private static final Logger             logger     = Logger.getLogger(MqttSubscriber.class);
    
    private volatile MqttClient             mqttClient = null;
    private volatile boolean                isRunning  = false;
    private volatile MqttSubscriberListener msl;
    private final List<String>              topics;
    private final ExecutorService           threadPool;
    
    /**
     * MQTT协议
     */
    public MqttSubscriber(List<String> topics, MqttSubscriberListener msl)
    {
        this.topics = new ArrayList<String>();
        this.topics.addAll(topics);
        this.msl = msl;
        threadPool = Executors.newCachedThreadPool();
    }
    
    /**
     * 启动MQTT订阅者
     */
    public synchronized void startup()
    {
        if (!isRunning)
        {
            isRunning = true;
            new Thread(this).start();
        }
    }
    
    /**
     * 关闭MQTT订阅者
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
    
    @Override
    public void run()
    {
        // 连接到MQTT代理服务器
        connect();
        
        logger.info("MqttSubscriber startup!");
        
        // 线程保活
        while (isRunning)
        {
            Thread.yield();
        }
        
        logger.info("MqttSubscriber shutdown!");
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
                mqttClient = new MqttClient(Config.IM_ONLINE_BROKER_URL, id, new MemoryPersistence());
                mqttClient.setCallback(this);
                // 连接选项
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setConnectionTimeout(5000); // 连接超时5秒
                options.setKeepAliveInterval(30000); // 心跳间隔为30秒
                // 连接
                mqttClient.connect(options);
                // 订阅主题
                for (String topic : topics)
                {
                    mqttClient.subscribe(topic, 1);
                }
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
    
    /**
     * mqtt回调方法，自动执行
     */
    @Override
    public void messageArrived(final MqttTopic mt, final MqttMessage msg) throws Exception
    {
        threadPool.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    msl.messageArrived(mt, msg);
                }
                catch (Exception e)
                {
                }
            }
        });
    }
}
