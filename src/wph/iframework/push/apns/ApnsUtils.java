package wph.iframework.push.apns;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.json.JSONObject;

public final class ApnsUtils
{
    private static final Map<String, Apns> map = new HashMap<String, Apns>();
    
    static
    {
        configure();
    }
    
    private ApnsUtils()
    {
    }
    
    /**
     * 配置APNS
     */
    public static synchronized void configure()
    {
        shutdown();
        map.clear();
        
        ResourceBundle rb;
        String keystore;
        String password;
        boolean production;
        
        rb = ResourceBundle.getBundle("user_config");
        keystore = rb.getString("KEY_STORE");
        password = rb.getString("PASSWORD");
        production = "true".equalsIgnoreCase(rb.getString("PRODUCTION"));
        map.put("u", new Apns(keystore, password, production));
        
        rb = ResourceBundle.getBundle("rescue_car_config");
        keystore = rb.getString("KEY_STORE");
        password = rb.getString("PASSWORD");
        production = "true".equalsIgnoreCase(rb.getString("PRODUCTION"));
        map.put("r", new Apns(keystore, password, production));
    }
    
    /**
     * 开启APNS
     */
    public static synchronized void startup()
    {
        Iterator<Entry<String, Apns>> i = map.entrySet().iterator();
        while (i.hasNext())
        {
            Entry<String, Apns> e = i.next();
            e.getValue().startup();
        }
    }
    
    /**
     * 关闭APNS
     */
    public static synchronized void shutdown()
    {
        Iterator<Entry<String, Apns>> i = map.entrySet().iterator();
        while (i.hasNext())
        {
            Entry<String, Apns> e = i.next();
            e.getValue().shutdown();
        }
    }
    
    /**
     * 推送
     * 
     * @param group
     *            群体
     * @param token
     *            设备
     * @param message
     *            消息
     */
    public static synchronized void push(String group, String token, String message)
    {
        Apns apns = map.get(group);
        apns.publish(token, message);
    }
    
    /**
     * 推送
     * 
     * @param group
     *            群体
     * @param token
     *            设备
     * @param clazz
     *            类型
     * @param contents
     *            内容，json格式字符串
     */
    public static synchronized void push(String group, String token, String clazz, String contents)
    {
        try
        {
            JSONObject o = new JSONObject();
            o.put("class", clazz);
            o.put("contents", new JSONObject(contents));
            push(group, token, o.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 推送
     * 
     * @param group
     *            群体
     * @param tokens
     *            设备
     * @param message
     *            消息
     */
    public static synchronized void push(String group, List<String> tokens, String message)
    {
        Apns apns = map.get(group);
        apns.publish(tokens, message);
    }
    
    /**
     * 推送
     * 
     * @param group
     *            群体
     * @param tokens
     *            设备
     * @param clazz
     *            类型
     * @param contents
     *            内容，json格式字符串
     */
    public static synchronized void push(String group, List<String> tokens, String clazz, String contents)
    {
        try
        {
            JSONObject o = new JSONObject();
            o.put("class", clazz);
            o.put("contents", new JSONObject(contents));
            push(group, tokens, o.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
