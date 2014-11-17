package wph.iframework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wph.iframework.utils.WebsiteUtils;

/**
 * 过滤器：判断用户是否登录
 * 
 */
public class LoginFilter implements Filter
{
    private static Log logger = LogFactory.getLog(LoginFilter.class);
    
    /**
     * redirect to: Default constructor.
     */
    public LoginFilter()
    {
    }
    
    /**
     * @see Filter#destroy()
     */
    public void destroy()
    {
        logger.debug("destroy()");
    }
    
    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        session.setMaxInactiveInterval(30 * 60);
        req.setCharacterEncoding("UTF-8");
        
        Integer oid = (Integer) session.getAttribute("oid");
        if (oid == null)
        {
        }
        String uri = req.getRequestURI().toUpperCase();// 得到用户请求的URI
        
        System.out.println(uri);
        
        if (uri.contains("LOGIN.JSP"))
        {
            chain.doFilter(request, response);
        }
        else if (uri.contains("REGISTER.JSP"))
        {
            chain.doFilter(request, response);
        }
        else if (oid != null)
        {
            chain.doFilter(request, response);
        }
        else
        {
            res.sendRedirect( WebsiteUtils.getRootPath(req) + loginUri);
        }
    }
    
    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException
    {
        loginUri = fConfig.getInitParameter("LOGIN_URI");
        homeUri = fConfig.getInitParameter("HOME_URI");
        logger.debug("init()");
        if (null == loginUri || null == homeUri)
        {
            throw new ServletException("没有找到登录页面或主页");
        }
    }
    
    private String loginUri;
    private String homeUri;
}
