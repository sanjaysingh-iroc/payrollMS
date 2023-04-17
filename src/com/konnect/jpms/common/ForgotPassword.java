package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class ForgotPassword extends ActionSupport implements IStatements, ServletRequestAware {

	private String userEmail;
	CommonFunctions CF = null;
	HttpSession session;

	public String execute() throws Exception {

		session = request.getSession(true);
		request.setAttribute(TITLE, TForgotPassword);
		request.setAttribute(PAGE, PForgotPassword);

		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			CF = new CommonFunctions();
			CF.setRequest(request);
			CF.getCommonFunctionsDetails(CF,request); 
		}
		
		if (getUserEmail() != null) {
			return validateEmail();
		}

		return loadForgot();
	}

	private String loadForgot() {
		setUserEmail("");

		return LOAD;
	}

	private String validateEmail() {

		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;

		try {

			con = db.makeConnection(con);
			if(getUserEmail() != null && !getUserEmail().equals("") && !getUserEmail().equalsIgnoreCase("null")) {
				pst = con.prepareStatement(selectForgotPassword);
				pst.setString(1, getUserEmail() != null ? getUserEmail().toUpperCase().trim() : "");
				pst.setString(2, getUserEmail() != null ? getUserEmail().toUpperCase().trim() : "");
				rs = pst.executeQuery();
				request.setAttribute(MESSAGE, E_WrongEmailAddress);
				while(rs.next()) {
					session.setAttribute("USERNAME", rs.getString("username"));
					session.setAttribute("PASSWORD", rs.getString("password"));
					session.setAttribute("EMAILADDRESS", rs.getString("emp_email"));
					request.setAttribute(MESSAGE, S_EmailAddress);
					
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_FORGOT_PASSWORD, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrEmpId(rs.getString("emp_per_id"));  
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setEmailTemplate(true); 
					nF.sendNotifications();   
				}
				rs.close();
				pst.close();
			} else {
				request.setAttribute(MESSAGE, E_WrongEmailAddress);
			}
		} catch (SQLException e) {
			request.setAttribute(MESSAGE, E_WrongEmailAddress);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	private HttpServletRequest request;
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String emailAddress) {
		this.userEmail = emailAddress;
	}
}