package wph.iframework.dao.db;

import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 
 * @author WPH
 * 
 */
public class DatabaseConfig
{
    public static final String CONFIG = "database_config.properties";
    public static String       DRIVER;                               // 驱动类
    public static String       URL;                                  // URL
    public static String       USER;                                 // 账号
    public static String       PASSWORD;                             // 密码
                                                                      
    static
    {
        configure();
    }
    
    private DatabaseConfig()
    {
    }
    
    public static void configure()
    {
        Properties ps = new Properties();
        try
        {
            ps.load(new InputStreamReader(DatabaseConfig.class.getClassLoader().getResourceAsStream(CONFIG), "UTF-8"));
            System.err.println("加载配置文件" + CONFIG + "成功！");
        }
        catch (Exception e)
        {
            System.err.println("加载配置文件" + CONFIG + "失败！");
            System.exit(0);
        }
        
        // 驱动类
        DRIVER = ps.getProperty("DRIVER");
        if (DRIVER == null || "".equals(DRIVER))
        {
            System.err.println("请重新配置DRIVER！");
            System.exit(0);
        }
        else
        {
            System.out.println("DRIVER=" + DRIVER);
        }
        // 账号
        URL = ps.getProperty("URL");
        if (URL == null || "".equals(URL))
        {
            System.err.println("请重新配置URL！");
            System.exit(0);
        }
        else
        {
            System.out.println("URL=" + URL);
        }
        
        // 账号
        USER = ps.getProperty("USER");
        if (USER == null || "".equals(USER))
        {
            System.err.println("请重新配置USER！");
            System.exit(0);
        }
        else
        {
            System.out.println("USER=" + USER);
        }
        
        // 密码
        PASSWORD = ps.getProperty("PASSWORD");
        if (PASSWORD == null || "".equals(PASSWORD))
        {
            System.err.println("请重新配置PASSWORD！");
            System.exit(0);
        }
        else
        {
            System.out.println("PASSWORD=" + PASSWORD);
        }
        
        System.err.println("配置文件" + CONFIG + "配置完成！");
    }
}
