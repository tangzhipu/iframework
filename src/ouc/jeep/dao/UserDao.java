package ouc.jeep.dao;

import wph.iframework.dao.Dao;
import wph.iframework.dao.db.Database;

public class UserDao extends Dao{

	public UserDao(Database db) {
		super(db);
	}

	public String register(String username, String password) {
		String sql = "insert into users values('hah','123')";
		
		return null;
	}
	
	
	

}
