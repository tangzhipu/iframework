package i.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import wph.iframework.Action;

public class Login extends Action
{
    private Integer i;
    private String  s;
    
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Hi hi = new Hi();
        
        try
        {
            BeanUtils.populate(hi, request.getParameterMap());
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        
        return hi.getI() + hi.getS();
    }
    
    public Integer getI()
    {
        return i;
    }
    
    public void setI(Integer i)
    {
        this.i = i;
    }
    
    public String getS()
    {
        return s;
    }
    
    public void setS(String s)
    {
        this.s = s;
    }
}
