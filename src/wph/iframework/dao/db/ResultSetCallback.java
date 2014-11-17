package wph.iframework.dao.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetCallback
{
    public int handle(ResultSet rs) throws SQLException;
}
