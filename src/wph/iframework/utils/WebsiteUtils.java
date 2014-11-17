package wph.iframework.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author WPH
 * 
 */
public final class WebsiteUtils
{
    private WebsiteUtils()
    {
    }
    
    /**
     * 返回网站的根路径，比如 http://localhost:8080/iFramework/
     * 
     * @param request
     * @return
     */
    public static String getRootPath(HttpServletRequest request)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(request.getScheme());
        sb.append("://");
        sb.append(request.getServerName());
        sb.append(":");
        sb.append(request.getServerPort());
        sb.append(request.getContextPath());
        sb.append("/");
        String rootPath = sb.toString();
        return rootPath;
    }
}
