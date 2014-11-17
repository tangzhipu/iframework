package wph.iframework;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author WPH
 * 
 */
public class PackageInfo
{
    private String                  namespace;
    private Map<String, ActionInfo> actions = new HashMap<String, ActionInfo>();
    
    public PackageInfo()
    {
        
    }
    
    public String getNamespace()
    {
        return namespace;
    }
    
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }
    
    public ActionInfo getAction(String key)
    {
        return actions.get(key);
    }
    
    public void setAction(String key, ActionInfo action)
    {
        actions.put(key, action);
    }
}
