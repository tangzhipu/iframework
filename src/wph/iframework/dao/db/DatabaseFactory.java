package wph.iframework.dao.db;

/**
 * 
 * @author WPH
 * 
 */
public class DatabaseFactory
{
    public static Database getDatabase()
    {
        try
        {
            return new SqlServer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
