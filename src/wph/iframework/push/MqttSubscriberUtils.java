package wph.iframework.push;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.DatabaseFactory;

public final class MqttSubscriberUtils
{
    private static final List<MqttSubscriber> subscribers = new ArrayList<MqttSubscriber>();
    
    private MqttSubscriberUtils()
    {
    }
    
    static
    {
        configure();
    }
    
    public static synchronized void configure()
    {
        shutdown();
        subscribers.clear();
        List<String> topics;
        
        /**
         * 接线员在线
         */
        topics = new ArrayList<String>();
        topics.add("/o/ImOnline");
        subscribers.add(new MqttSubscriber(topics, new MqttSubscriberListener()
        {
            
            @Override
            public void messageArrived(MqttTopic mt, MqttMessage msg)
            {
                
            }
        }));
        
        /**
         * 救援车在线
         */
        topics = new ArrayList<String>();
        topics.add("/r/gps");
        subscribers.add(new MqttSubscriber(topics, new MqttSubscriberListener()
        {
            
            @Override
            public void messageArrived(MqttTopic mt, MqttMessage msg)
            {
                try
                {
                    byte[] bytes = msg.getPayload();
                    
                    if (bytes.length < 12)
                    {
                        return;
                    }
                    
                    ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
                    DataInputStream dos = new DataInputStream(baos);
                    try
                    {
                        int id = dos.readInt();
                        int lat = dos.readInt();
                        int lng = dos.readInt();
                        String orderId = new String(bytes, 12, bytes.length - 12);
                        
                        System.out.println("id =" + id);
                        System.out.println("lat =" + lat);
                        System.out.println("lng =" + lng);
                        System.out.println("orderId=" + orderId);
                        
                        // 数据转换
                        float longitude = (float) (lng / 1E6);
                        float latitude = (float) (lat / 1E6);
                        
                        System.out.println("lat =" + latitude);
                        System.out.println("lng =" + longitude);
                        // 调用Service或者Dao
                        
                        Database db = DatabaseFactory.getDatabase();
                        try
                        {
                            db.setAutoCommit(false);
//                            TaxiDao taxiDao = new TaxiDao(db);
                            // taxiDao.UpdateTaxiAddress(id, longitude,
                            // latitude, orderId);
                            db.commit();
                        }
                        catch (SQLException e)
                        {
                            e.printStackTrace();
                            try
                            {
                                db.rollback();
                            }
                            catch (SQLException e1)
                            {
                                e1.printStackTrace();
                            }
                        }
                        finally
                        {
                            if (db != null)
                            {
                                db.close();
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        dos.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    
                    try
                    {
                        dos.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }));
    }
    
    public static synchronized void startup()
    {
        for (MqttSubscriber sub : subscribers)
        {
            sub.startup();
        }
    }
    
    public static synchronized void shutdown()
    {
        for (MqttSubscriber sub : subscribers)
        {
            sub.shutdown();
        }
    }
    
}
