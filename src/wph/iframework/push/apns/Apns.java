package wph.iframework.push.apns;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import org.apache.log4j.Logger;

class Apns implements Runnable
{
    private static final Logger           logger    = Logger.getLogger(Apns.class); // 日志打印
    private final String                  keystore;
    private final String                  password;
    private final boolean                 production;
    private final PushNotificationManager manager;
    private volatile boolean              isRunning = false;
    
    class Message
    {
        public String token;
        public String message;
    }
    
    private final LinkedBlockingQueue<Message> messages;
    
    public Apns(String path, String password, boolean production)
    {
        this.keystore = path;
        this.password = password;
        this.production = production;
        
        manager = new PushNotificationManager();
        manager.setRetryAttempts(1000);
        messages = new LinkedBlockingQueue<Message>();
    }
    
    /**
     * 开启APNS
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
     * 关闭APNS
     */
    public synchronized void shutdown()
    {
        if (isRunning)
        {
            isRunning = false;
            
            try
            {
                manager.stopConnection();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 发布消息
     * 
     * @param token
     *            设备
     * @param message
     *            消息
     */
    public void publish(String token, String message)
    {
        Message m = new Message();
        m.token = token;
        m.message = message;
        messages.add(m);
    }
    
    /**
     * 发布消息
     * 
     * @param tokens
     *            设备
     * @param message
     *            消息
     */
    public void publish(List<String> tokens, String message)
    {
        for (String token : tokens)
        {
            Message m = new Message();
            m.token = token;
            m.message = message;
            messages.add(m);
        }
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
                    PushNotificationPayload payload = new PushNotificationPayload();
                    payload.addAlert(m.message); // 消息内容
                    payload.addBadge(1); // iPhone应用图标上小红圈上的数值
                    payload.addSound("default"); // 铃音默认
                    
                    // 发送push消息
                    Device device = new BasicDevice(m.token);
                    
                    boolean ok = false;
                    
                    while (!ok)
                    {
                        try
                        {
                            PushedNotification pushed = manager.sendNotification(device, payload, false);
                            if (pushed.isSuccessful())
                            {
                                logger.debug(m.token);
                                logger.debug(m.message);
                                ok = true;
                            }
                            else
                            {
                                logger.debug("发送失败，接着重发。");
                                manager.initializePreviousConnection();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            manager.initializePreviousConnection();
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
        while (isRunning)
        {
            try
            {
                manager.initializeConnection(new AppleNotificationServerBasicImpl(keystore, password, production));
                
                logger.debug("Apns startup!");
                
                publish();
                
                logger.debug("Apns shutdown!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
    }
}
