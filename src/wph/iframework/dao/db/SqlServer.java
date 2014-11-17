package wph.iframework.dao.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SqlServer extends Database
{
    
    public SqlServer() throws ClassNotFoundException, SQLException
    {
        this(DatabaseConfig.DRIVER, DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
    }
    
    public SqlServer(String driver, String url, String user, String password) throws ClassNotFoundException, SQLException
    {
        super(driver, url, user, password);
    }
    
    @Override
    public int executeQuery(String sql, int limit, int offset, ResultSetCallback callback)
    {
        if (sql == null)
        {
            throw new NullPointerException();
        }
        
        if (limit < 1 || offset < 0)
        {
            throw new IllegalArgumentException();
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ");
        sb.append("( ");
        sb.append("( ");
        sb.append("SELECT COUNT(*) AS _count FROM ");
        sb.append("( ");
        sb.append(sql);
        sb.append(" ");
        sb.append(") ");
        sb.append("AS _table_1 ");
        sb.append(") ");
        sb.append("AS _table_2 ");
        sb.append("JOIN ");
        sb.append("( ");
        sb.append(sql);
        sb.append(" ");
        sb.append("LIMIT ");
        sb.append(limit);
        sb.append(" ");
        sb.append("OFFSET ");
        sb.append(offset);
        sb.append(" ");
        sb.append(") ");
        sb.append("AS _table_3 ");
        sb.append("ON 1=1 ");
        sb.append(")");
        
        int ret = -1;
        try
        {
            ResultSet rs = executeQuery(sb.toString());
            ret = callback.handle(rs);
            rs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return ret;
    }
    
    @Override
    public int executeQuery(String sql, String col, int from, int to, ResultSetCallback callback)
    {
        if (sql == null)
        {
            throw new NullPointerException();
        }
        
        if (to < 1 || from < 0)
        {
            throw new IllegalArgumentException();
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ");
        sb.append("( ");
        sb.append("( ");
        sb.append("SELECT COUNT(*) AS __count FROM ");
        sb.append("( ");
        sb.append(sql);
        sb.append(" ");
        sb.append(") ");
        sb.append("AS __table_1 ");
        sb.append(") ");
        sb.append("AS __table_2 ");
        sb.append("JOIN ");
        sb.append("( ");
        sb.append("SELECT * FROM (SELECT row_number() over(ORDER BY ");
        sb.append(col);
        sb.append(") AS __row_number, * FROM (");
        sb.append(sql);
        sb.append(") AS __table_3) AS __table_4 WHERE __row_number BETWEEN ");
        sb.append(from);
        sb.append(" AND ");
        sb.append(to);
        sb.append(" ) AS __table_5");
        sb.append(" ON 1=1 ");
        sb.append(")");
        
        int ret = -1;
        try
        {
            ResultSet rs = executeQuery(sb.toString());
            ret = callback.handle(rs);
            rs.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return ret;
    }
    
    @Override
    public int executeQuery(String sql, String col, int from, int to, List<Object> list, ResultSetCallback callback)
    {
        if (sql == null)
        {
            throw new NullPointerException();
        }
        
        if (to < 1 || from < 0)
        {
            throw new IllegalArgumentException();
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM ");
        sb.append("( ");
        sb.append("( ");
        sb.append("SELECT COUNT(*) AS __count FROM ");
        sb.append("( ");
        sb.append(sql);
        sb.append(" ");
        sb.append(") ");
        sb.append("AS __table_1 ");
        sb.append(") ");
        sb.append("AS __table_2 ");
        sb.append("JOIN ");
        sb.append("( ");
        sb.append("SELECT * FROM (SELECT row_number() over(ORDER BY ");
        sb.append(col);
        sb.append(") AS __row_number, * FROM (");
        sb.append(sql);
        sb.append(") AS __table_3) AS __table_4 WHERE __row_number BETWEEN ");
        sb.append(from);
        sb.append(" AND ");
        sb.append(to);
        sb.append(" ) AS __table_5");
        sb.append(" ON 1=1 ");
        sb.append(")");
        
        return executeQuery(sb.toString(), list, callback);
    }
    
    @Override
    public int getPid(String tableName)
    {
        String sql = "select Ident_Current('" + tableName + "')";
        ResultSet rs = executeQuery(sql);
        try
        {
            logger.debug("[Info From DataBase:]Get Last Record PK From Table " + tableName);
            if (rs != null && rs.next())
            {
                return rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            logger.warn("[Error: ] " + e.getMessage());
        }
        return -1;
    }
    
}
