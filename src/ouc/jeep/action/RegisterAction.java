package ouc.jeep.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wph.iframework.Action;
import wph.iframework.dao.db.Database;

public class RegisterAction extends Action{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
//		String result = "-1";
//		Database db = null;
		
		logger.debug("username="+username+"  password="+password); 
		
		/*try {
			db = new SqlServer();
			UserDao uDao = new UserDao(db);
			result = uDao.register(username,password);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.close();
		}*/
		
		return "success";
	}

}
