package wph.iframework;

/**
 * 
 * @author WPH
 * 
 */
public class ActionInfo
{
    private String   name;
    private Class<?> clazz;
    private String   type;
    private boolean  autoCreateDatabase;
    private boolean  transactional;
    
    public ActionInfo()
    {
        type = "text/plain";
        autoCreateDatabase = false;
        transactional = false;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Class<?> getClazz()
    {
        return clazz;
    }
    
    public void setClazz(Class<?> clazz)
    {
        this.clazz = clazz;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        // 返回的是html格式
        if ("html".equals(type))
        {
            this.type = "text/html";
        }
        // 返回的是xml格式
        else if ("xml".equals(type))
        {
            this.type = "text/xml";
        }
        // 返回的是html格式
        else if ("json".equals(type))
        {
            this.type = "text/json";
        }
        // 返回的是plain格式
        else
        {
            this.type = "text/plain";
        }
    }
    
    public boolean isAutoCreateDatabase()
    {
        return autoCreateDatabase;
    }
    
    public void setAutoCreateDatabase(boolean autoCreateDatabase)
    {
        this.autoCreateDatabase = autoCreateDatabase;
    }
    
    public boolean isTransactional()
    {
        return transactional;
    }
    
    public void setTransactional(boolean transactional)
    {
        this.transactional = transactional;
    }
}
