package wph.iframework.dao.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 数据库
 * 
 * @author 王培鹤
 * 
 */
public abstract class Database
{
    protected static Log logger     = LogFactory.getLog(Database.class);
    
    protected String     driver     = null;
    protected String     url        = null;
    protected String     user       = null;
    protected String     password   = null;
    
    protected Connection connection = null;
    protected Statement  statement  = null;
    
    /**
     * 注册数据库驱动程序并创建连接.
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Database(String driver, String url, String user, String password) throws ClassNotFoundException, SQLException
    {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        
        Class.forName(driver);
        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
        logger.debug("Connection created");
    }
    
    /**
     * 返回数据连接 Connection
     * 
     * @return
     */
    public Connection getConnection()
    {
        return connection;
    }
    
    /**
     * 返回数据清单 Statement
     * 
     * @return
     */
    public Statement getStatement()
    {
        return statement;
    }
    
    /**
     * 获取预编译数据清单 PreparedStatement
     * 
     * @param sql
     *            SQL语句
     * @return
     */
    public PreparedStatement getPreparedStatement(String sql)
    {
        try
        {
            logger.debug("SQL String: " + sql);
            return connection.prepareStatement(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            logger.warn("Error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 获取调用存储过程的清单 CallableStatement
     * 
     * @param sql
     *            SQL语句
     * @return
     */
    public CallableStatement getCallableStatement(String sql)
    {
        try
        {
            logger.debug("SQL String: " + sql);
            return connection.prepareCall(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            logger.warn("Error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 对数据库执行插入、删除、修改等操作
     * 
     * @param sql
     *            SQL语句
     * @return 受影响的列数，操作失败返回-1.
     */
    public int executeUpdate(String sql)
    {
        try
        {
            logger.debug("Update SQL String: " + sql);
            
            if (statement != null)
            {
                return statement.executeUpdate(sql);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            logger.warn("Error: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * 对数据库执行查询操作
     * 
     * @param sql
     *            SQL语句
     * @return 结果集
     */
    public ResultSet executeQuery(String sql)
    {
        try
        {
            logger.debug("Query SQL String: " + sql);
            if (statement != null)
            {
                return statement.executeQuery(sql);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            logger.warn("Error: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 对数据库执行插入、删除、修改等操作
     * 
     * @param sql
     *            SQL语句
     * @return 受影响的列数，操作失败返回-1.
     */
    public int executeUpdate(String sql, List<Object> items)
    {
        int rows = 0;
        
        PreparedStatement ps = null;
        try
        {
            ps = connection.prepareStatement(sql);
            
            if (items != null)
            {
                for (int i = 0; i < items.size(); i++)
                {
                    ps.setObject(i + 1, items.get(i));
                }
            }
            
            rows = ps.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("[Error From Database:] ", e);
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return rows;
    }
    
    /**
     * 对数据库执行插入、删除、修改等操作
     * 
     * @param sql
     *            SQL语句
     * @return 受影响的列数，操作失败返回-1.
     */
    public int executeQuery(String sql, List<Object> items, ResultSetCallback callback)
    {
        int ret = -1;
        PreparedStatement ps = null;
        try
        {
            ps = connection.prepareStatement(sql);
            
            if (items != null)
            {
                for (int i = 0; i < items.size(); i++)
                {
                    ps.setObject(i + 1, items.get(i));
                }
            }
            
            ResultSet rs = ps.executeQuery();
            ret = callback.handle(rs);
            rs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("[Error From Database:] ", e);
        }
        finally
        {
            if (ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return ret;
    }
    
    /**
     * 执行分页查询SQL语句，注意 _count、_table_1、_table_2和_table_3是关键字，若想获得总数据条数，可以调用 int
     * count = rs.getInt("_count");
     * 
     * @param sql
     *            SQL语句，普通的查询SQL
     * @param limit
     *            一页的大小
     * @param offset
     *            偏移量
     * @return 结果集
     */
    public abstract int executeQuery(String sql, int limit, int offset, ResultSetCallback callback);
    
    public abstract int executeQuery(String sql, String col, int from, int to, ResultSetCallback callback);
    
    /**
     * 执行分页查询SQL语句，注意 _count、_table_1、_table_2和_table_3是关键字，若想获得总数据条数，可以调用 int
     * count = rs.getInt("_count");
     * 
     * @param sql
     *            SQL语句，普通的查询SQL
     * @param page
     *            分页bean
     */
    public int executeQuery(String sql, String col, Page<?> page, ResultSetCallback callback)
    {
        int limit = page.getPageSize();
        int offset = (page.getPageNumber() - 1) * limit;
        int from = offset + 1;
        int to = offset + limit;
        return executeQuery(sql, col, from, to, callback);
    }
    
    public abstract int executeQuery(String sql, String col, int from, int to, List<Object> list, ResultSetCallback callback);
    
    /**
     * 取得最后插入的纪录的主键.
     * 
     * @param tableName
     *            表名 return int 最后插入纪录的主键
     */
    public abstract int getPid(String tableName);
    
    /**
     * 设置是否允许自动提交
     * 
     * @param autoCommit
     *            true 代表允许自动提交
     * @throws SQLException
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
        connection.setAutoCommit(autoCommit);
    }
    
    /**
     * 提交
     * 
     * @throws SQLException
     */
    public void commit() throws SQLException
    {
        connection.commit();
    }
    
    /**
     * 回滚
     * 
     * @throws SQLException
     */
    public void rollback() throws SQLException
    {
        connection.rollback();
    }
    
    /**
     * 关闭数据库
     */
    public void close()
    {
        if (statement != null)
        {
            try
            {
                statement.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                logger.warn("Error: " + e.getMessage());
            }
        }
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                logger.warn("Error: " + e.getMessage());
            }
        }
        
        logger.debug("Connection closed");
    }
}