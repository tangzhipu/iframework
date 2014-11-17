package wph.iframework.push.mqtt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

public final class MqttUtils
{
    private static final Map<String, Mqtt> map = new HashMap<String, Mqtt>();
    
    static
    {
        configure();
    }
    
    private MqttUtils()
    {
    }
    
    /**
     * 配置MQTT
     */
    public static synchronized void configure()
    {
        shutdown();
        map.clear();
        map.put("u", new Mqtt());
        map.put("r", new Mqtt());
        map.put("o", new Mqtt());
    }
    
    /**
     * 启动MQTT
     */
    public static synchronized void startup()
    {
        Iterator<Entry<String, Mqtt>> i = map.entrySet().iterator();
        while (i.hasNext())
        {
            Entry<String, Mqtt> e = i.next();
            e.getValue().startup();
        }
    }
    
    /**
     * 关闭MQTT
     */
    public static synchronized void shutdown()
    {
        Iterator<Entry<String, Mqtt>> i = map.entrySet().iterator();
        while (i.hasNext())
        {
            Entry<String, Mqtt> e = i.next();
            e.getValue().shutdown();
        }
    }
    
    /**
     * 推送
     * 
     * @param group
     *            群体
     * @param topic
     *            主题
     * @param message
     *            消息
     */
    public static synchronized void push(String group, String topic, String message)
    {
        Mqtt mqtt = map.get(group);
        mqtt.publish(topic, message);
    }
    
    /**
     * 推送
     * 
     * @param group
     *            群体
     * @param topic
     *            主题
     * @param clazz
     *            类型
     * @param contents
     *            内容，json格式字符串
     */
    public static synchronized void push(String group, String topic, String clazz, String contents)
    {
        try
        {
            JSONObject o = new JSONObject();
            o.put("class", clazz);
            o.put("contents", new JSONObject(contents));
            push(group, topic, o.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}