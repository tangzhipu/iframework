package wph.iframework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author 王培鹤
 * 
 */
public class Controller extends HttpServlet
{
    private static Log               logger           = LogFactory.getLog(Controller.class);
    private static final long        serialVersionUID = 1L;
    
    private Map<String, PackageInfo> actions          = Collections.synchronizedMap(new HashMap<String, PackageInfo>());
    private long                     lastModified     = 0;
    private String                   config           = null;
    private String                   suffix           = null;
    private long                     monitorPeriod    = 0;
    private Monitor                  monitor          = null;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller()
    {
        super();
    }
    
    @Override
    public void init() throws ServletException
    {
        super.init();
        String s = null;
        
        s = getInitParameter("config");
        if (s == null)
        {
            throw new NullPointerException();
        }
        config = s;
        
        s = getInitParameter("suffix");
        if (s == null)
        {
            throw new NullPointerException();
        }
        suffix = s;
        
        s = getInitParameter("monitorPeriod");
        if (s == null)
        {
            throw new NullPointerException();
        }
        monitorPeriod = Integer.valueOf(s);
        
        if (monitor != null)
        {
            monitor.stop();
            monitor = null;
        }
        
        monitor = new Monitor();
        
        new Thread(monitor).start();
    }
    
    @Override
    public void destroy()
    {
        super.destroy();
        
        if (monitor != null)
        {
            monitor.stop();
            monitor = null;
        }
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try
        {
            String uri = request.getRequestURI();
            
            logger.debug(uri);
            
            if (!uri.endsWith(suffix))
            {
                throw new ServletException("URL的后缀必须为" + suffix + "!");
            }
            
            uri = uri.substring(request.getContextPath().length());
            int end = uri.lastIndexOf("/") + 1;
            String namespace = uri.substring(0, end);
            
            int begin = end;
            end = uri.lastIndexOf(suffix);
            String name = uri.substring(begin, end);
            
            PackageInfo pi = actions.get(namespace);
            if (pi == null)
            {
                throw new ServletException("配置文件" + config + "中没有命名空间" + namespace + "!");
            }
            
            ActionInfo ai = pi.getAction(name);
            if (ai == null)
            {
                throw new ServletException("配置文件" + config + "中命名空间" + namespace + "下没有" + name + "!");
            }
            response.setContentType(ai.getType());
            
            Action a = (Action) ai.getClazz().newInstance();
            BeanUtils.populate(a, request.getParameterMap());
            a.setActionInfo(ai);
            String result = a.doAction(request, response);
            
            logger.debug(result);
            
            out.print(result);
        }
        catch (Exception e)
        {
            logger.debug(e.getMessage());
            out.print("\f\n"); // 没有找到对应Action
        }
    }
    
    public class Monitor implements Runnable
    {
        private volatile boolean done = false;
        
        public void stop()
        {
            done = true;
        }
        
        @Override
        public void run()
        {
            while (!done)
            {
                String filename = Controller.class.getResource(config).getFile();
                File file = new File(filename);
                long lm = file.lastModified();
                
                if (lm != lastModified)
                {
                    System.out.println();
                    lastModified = lm;
                    
                    try
                    {
                        InputStream is = new FileInputStream(file);
                        
                        updateActions(is);
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                        done = true;
                    }
                }
                
                try
                {
                    Thread.sleep(monitorPeriod);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void updateActions(InputStream is)
    {
        try
        {
            // 创建Document
            SAXReader reader = new SAXReader();
            reader.setEncoding("UTF-8");
            reader.setEntityResolver(new IfEntityResolver());
            Document document = reader.read(is);
            
            // 解析XML
            Element root = document.getRootElement();
            Iterator<?> i1 = root.elementIterator("package");
            Map<String, PackageInfo> map = new HashMap<String, PackageInfo>(); // 存储新的Action
            while (i1.hasNext())
            {
                Element e1 = (Element) i1.next();
                String namespace = e1.attributeValue("namespace");
                // 错误的命名空间
                if (!namespace.startsWith("/") && !namespace.endsWith("/") && namespace.contains("//"))
                {
                    logger.warn("配置文件" + config + "中的命名空间" + namespace + "无效!");
                    continue;
                }
                
                PackageInfo pi = new PackageInfo();
                pi.setNamespace(namespace);
                Iterator<?> i2 = e1.elementIterator("action");
                while (i2.hasNext())
                {
                    Element e2 = (Element) i2.next();
                    String name = e2.attributeValue("name");
                    String clazz = e2.attributeValue("class");
                    if (clazz.startsWith(".") && clazz.endsWith("."))
                    {
                        logger.warn("配置文件" + config + "中命名空间" + namespace + "下的" + name + "无效!");
                        continue;
                    }
                    String type = e2.attributeValue("type");
                    boolean auto;
                    boolean transactional;
                    Element e3 = e2.element("database");
                    if (e3 != null)
                    {
                        auto = "true".equals(e3.attributeValue("auto"));
                        transactional = "true".equals(e3.attributeValue("transaction"));
                    }
                    else
                    {
                        auto = false;
                        transactional = false;
                    }
                    
                    ActionInfo ai = new ActionInfo();
                    ai.setName(name);
                    ai.setClazz(Class.forName(clazz));
                    ai.setType(type);
                    ai.setAutoCreateDatabase(auto);
                    ai.setTransactional(transactional);
                    pi.setAction(name, ai);
                }
                map.put(namespace, pi);
            }
            
            // 更新Action
            synchronized (actions)
            {
                actions.clear();
                actions.putAll(map);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    class IfEntityResolver implements EntityResolver
    {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
        {
            if ("-//WPH-IF".equals(publicId))
            {
                return new InputSource(Controller.class.getResourceAsStream("/if.dtd"));
            }
            else
            {
                return null;
            }
        }
    }
    
    public static void main(String[] args)
    {
        String file = Controller.class.getResource("/config.xml").getFile();
        System.out.println(file);
        File f = new File("/config.xml");
        System.out.println(f.getAbsolutePath());
    }
}
