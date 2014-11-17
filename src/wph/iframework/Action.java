package wph.iframework;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import wph.iframework.dao.db.Database;
import wph.iframework.dao.db.DatabaseFactory;

/**
 * 
 * @author WPH
 * 
 */
public abstract class Action
{
    protected final Logger logger = Logger.getLogger(Action.class);
    private ActionInfo     ai;
    private Database       database;
    
    public String doAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String ret = null;
        if (ai.isAutoCreateDatabase())
        {
            try
            {
                database = DatabaseFactory.getDatabase();
                if (ai.isTransactional())
                {
                    try
                    {
                        database.setAutoCommit(false);
                        try
                        {
                            ret = execute(request, response);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            try
                            {
                                database.rollback();
                            }
                            catch (SQLException e1)
                            {
                                e1.printStackTrace();
                                throw e1;
                            }
                            throw e;
                        }
                        database.commit();
                    }
                    catch (ServletException e)
                    {
                        e.printStackTrace();
                        throw e;
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        throw e;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        ret = "\f\f"; // 未知错误
                    }
                }
                else
                {
                    ret = execute(request, response);
                }
            }
            catch (ServletException e)
            {
                e.printStackTrace();
                throw e;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw e;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                ret = "\f\f"; // 未知错误类型
            }
            finally
            {
                if (database != null)
                {
                    database.close();
                }
            }
            
            return ret;
        }
        else
        {
            return execute(request, response);
        }
    }
    
    public abstract String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    
    public void setActionInfo(ActionInfo ai)
    {
        this.ai = ai;
    }
    
    public Database getDatabase()
    {
        return database;
    }
}