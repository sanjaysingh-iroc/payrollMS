package com.konnect.jpms.ajax;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.EmployeeActivity;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ResetPassword extends ActionSupport implements IStatements, ServletRequestAware{


	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		if(getUser_id()!=null){
			resetPassword();
		}
		
		return SUCCESS;
	
	}

	
	String user_id;
	
	public void resetPassword(){

		Connection con = null;
		PreparedStatement pst = null, pst1 = null, pst2 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		StringBuilder sb = new StringBuilder();
		try {

			
			SecureRandom random = new SecureRandom();
			String password = new BigInteger(130, random).toString(32).substring(5, 13);
			
			
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select emp_id from user_details where user_id=?");
			pst.setInt(1, uF.parseToInt(getUser_id()));
			rs = pst.executeQuery();
			int nEmpId= 0;
			while(rs.next()){
				nEmpId = uF.parseToInt(rs.getString("emp_id"));
			}
            rs.close();
            pst.close();
			
			
			
			
			if(nEmpId>0){
				pst = con.prepareStatement("update user_details set password=?, reset_timestamp=? where user_id=?");
				pst.setString(1, password);
				pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(3, uF.parseToInt(getUser_id()));
				pst.execute();
	            pst.close();
				
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_RESET_PASSWORD, CF);				
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId(nEmpId+"");
				nF.setStrContextPath(request.getContextPath());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrNewPassword(password);
				nF.setEmailTemplate(true);				
				nF.sendNotifications();
				
				request.setAttribute("STATUS_MSG", "Password Reset");
			}else{
				request.setAttribute("STATUS_MSG", "Can not reset");
			}
			
			

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}

	}
	
	
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


}