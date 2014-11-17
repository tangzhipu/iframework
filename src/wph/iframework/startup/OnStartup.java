package wph.iframework.startup;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import wph.iframework.push.MqttSubscriberUtils;
import wph.iframework.push.apns.ApnsUtils;
import wph.iframework.push.mqtt.MqttUtils;

/**
 * Servlet implementation class OnStartup
 */
public class OnStartup extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OnStartup()
    {
        super();
    }
    
    @Override
    public void init() throws ServletException
    {
        super.init();
        Config.configure();
//        MqttUtils.startup();
//        ApnsUtils.startup();
//        MqttSubscriberUtils.startup();
    }
    
    @Override
    public void destroy()
    {
//        MqttUtils.shutdown();
//        ApnsUtils.shutdown();
//        MqttSubscriberUtils.startup();
        super.destroy();
    }
}
