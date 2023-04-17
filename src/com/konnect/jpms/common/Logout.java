package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

/**
 * <p>
 * Validate a user login.
 * </p>
 */
public class Logout extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	private static Logger log = Logger.getLogger(Logout.class);
	
	public CommonFunctions CF = null;
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		response.setContentType("text/html");
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		//Delete the cookies 
		Cookie cookies[] = request.getCookies();
				
		for(int i=0; cookies!=null && i< cookies.length; i++) {
			
			if(cookies[i].getName().equals("PAYROLL_USERNAME")) {
				Cookie cookie = cookies[i];
				cookie.setMaxAge(0);
				cookie.setValue(null);
				cookie.setPath(request.getContextPath());
				response.addCookie(cookie);
				log.debug("deleted username cookie");
			}
			
			if(cookies[i].getName().equals("PAYROLL_PASSWORD")) {
				Cookie cookie = cookies[i];
				cookie.setMaxAge(0);
				cookie.setValue(null);
				cookie.setPath(request.getContextPath());
				response.addCookie(cookie);
				log.debug("deleted password cookie");
			}
			
			if(cookies[i].getName().equals("PAYROLL_LOGIN_TYPE")) {
				Cookie cookie = cookies[i];
				cookie.setMaxAge(0);
				cookie.setValue(null);
				cookie.setPath(request.getContextPath());
				response.addCookie(cookie);
				log.debug("deleted PAYROLL_LOGIN_TYPE cookie");
			}
			
		}
		
		insertLogoutTimeStamp();
		
		session.invalidate();
		
		return SUCCESS;
//		return LOGIN;

	}
	
	private void insertLogoutTimeStamp() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		UtilityFunctions uF=new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("update login_timestamp set logout_timestamp=?  where session_id=?");
			pst.setTimestamp(1, uF.getTimeStamp(""+uF.getCurrentDate(CF.getStrTimeZone())+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setString(2, session.getId());
			pst.execute();
			pst.close();

			
 
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

}

	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

}