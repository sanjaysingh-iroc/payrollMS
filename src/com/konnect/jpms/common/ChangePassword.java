package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ChangePassword extends ActionSupport implements ServletRequestAware, IStatements {

 	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strLoginType; 
	
	public String execute() throws Exception {
		session = request.getSession(true);
		strLoginType = (String)session.getAttribute(LOGIN_TYPE);
		
	 	CF = (CommonFunctions)session.getAttribute(CommonFunctions);
	 	if(CF==null){
	 		return LOGIN;
	 	}
		request.setAttribute(PAGE, PChangePassword);
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("getOldPassword() ===>> " + getOldPassword());
//		System.out.println("strLoginType ===>> " + strLoginType);
//		System.out.println("isLogin ===>> " + isLogin);
		
		if (getOldPassword() != null && getOldPassword().length() > 0) {
			if(uF.parseToInt(strLoginType) == 1) {
				updatePassword();
			} else if(uF.parseToInt(strLoginType) == 2) {
				updateCustomerPassword();
			}
			request.setAttribute(TITLE, TChangePassword);
			if(isLogin) {
				return "logout";
			}
			return SUCCESS;
			
		} else {
			return loadChangePassword();
			
		}
	}
	  
	public String loadValidateChangePassword() {
		request.setAttribute(PAGE, PChangePassword);
		request.setAttribute(TITLE, TChangePassword);
		return LOAD;
	}
	

	public String loadChangePassword() {
		request.setAttribute(PAGE, PChangePassword);
		request.setAttribute(TITLE, TChangePassword);
		setOldPassword(null);
		setNewPassword(null);
		setConfirmPassword(null);
		return LOAD;
	}

	boolean isLogin=false;
	
	
	public String updateCustomerPassword() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM user_details_customer udc, user_type ut, client_poc cp WHERE  ut.user_type_id = udc.usertype_id and udc.emp_id = cp.poc_id and upper(username)=? and password=?");
			pst.setString(1, ((String)session.getAttribute(USERNAME)).toUpperCase());
			pst.setString(2, getOldPassword());
//			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			boolean isUserExist = false;
			if(rst.next()) {
				isUserExist = true;
			} else {
				request.setAttribute(MESSAGE, E_WrongPassword);
			}
			rst.close();
			pst.close();
			
			if(isUserExist) {
				pst = con.prepareStatement("UPDATE user_details_customer SET password=? where emp_id=? and upper(username)=?");
				pst.setString(1, getNewPassword());
				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setString(3, ((String)session.getAttribute(USERNAME)).toUpperCase()); 
				pst.execute();
				pst.close();
				request.setAttribute(MESSAGE, "Password changed successfully!");
				
//				String strDomain = request.getServerName().split("\\.")[0];
//				Notifications nF = new Notifications(N_UPD_PASSWORD, CF);
//				nF.setDomain(strDomain);
//				nF.setStrEmpId((String)session.getAttribute(EMPID));
//				nF.setStrContextPath(request.getContextPath());
////				nF.setStrHostAddress(request.getRemoteHost());
//				nF.setStrHostAddress(CF.getStrEmailLocalHost());
//				nF.setStrHostPort(CF.getStrHostPort());
//				nF.setStrNewPassword(getNewPassword());
//				nF.setEmailTemplate(true);
//				nF.sendNotifications();
				
				if(CF.isForcePassword()) {
					pst = con.prepareStatement("update user_details_customer set is_forcepassword = ? where emp_id=? and upper(username)=?");
					pst.setBoolean(1, false);
					pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setString(3, ((String)session.getAttribute(USERNAME)).toUpperCase());
					pst.execute();
					pst.close();
					request.setAttribute(MESSAGE, "Password changed successfully! Please sign out and sign in again.");
					isLogin = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	public String updatePassword() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			
			String strEmpFname = null;
			String strEmail = null;
			String strContactNumber = null;
			pst = con.prepareStatement(selectUser);
			pst.setString(1, ((String)session.getAttribute(USERNAME)).toUpperCase());
			pst.setString(2, getOldPassword());
			rst = pst.executeQuery();
			boolean isUserExist = false;
			if(rst.next()) {
				isUserExist = true;
				strEmail = rst.getString("emp_email");
				strEmpFname = rst.getString("emp_fname");
				strContactNumber = rst.getString("emp_contactno");
			} else {
				request.setAttribute(MESSAGE, E_WrongPassword);
			}
			rst.close();
			pst.close();
			
			if(isUserExist) {
//				pst = con.prepareStatement(updatePassword);
//				pst.setString(1, getNewPassword());
//				pst.setString(2, (String)session.getAttribute(USERTYPE));
//				pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
//				pst.setString(4, ((String)session.getAttribute(USERNAME)).toUpperCase()); 
//				pst.execute();
				pst = con.prepareStatement("UPDATE user_details SET password=? where emp_id=? and upper(username)=?");
				pst.setString(1, getNewPassword());
				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setString(3, ((String)session.getAttribute(USERNAME)).toUpperCase()); 
				pst.execute();
				pst.close();
				session.setAttribute(MESSAGE, "<div class=\"msg savesuccess\" style=\"text-align: center;\"><span>" +"Password changed successfully!" + END);
//				System.out.println("MESSAGE ===>> " + (String)session.getAttribute(MESSAGE));
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_UPD_PASSWORD, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrEmpId((String)session.getAttribute(EMPID));
				nF.setStrContextPath(request.getContextPath());
//				nF.setStrHostAddress(request.getRemoteHost());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrNewPassword(getNewPassword());
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				if(CF.isForcePassword()) {
//					pst = con.prepareStatement("update user_details set is_forcepassword = ? where usertype_id = (SELECT user_type_id from user_type where user_type=?) and emp_id=? and upper(username)=?");
//					pst.setBoolean(1, false);
//					pst.setString(2, (String)session.getAttribute(USERTYPE));
//					pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
//					pst.setString(4, ((String)session.getAttribute(USERNAME)).toUpperCase());
//					pst.execute();
					pst = con.prepareStatement("update user_details set is_forcepassword = ? where emp_id=? and upper(username)=?");
					pst.setBoolean(1, false);
					pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setString(3, ((String)session.getAttribute(USERNAME)).toUpperCase());
					pst.execute();
					pst.close();
					request.setAttribute(MESSAGE, "<div class=\"msg savesuccess\" style=\"text-align: center;\"><span>" +"Password changed successfully! Please sign out and sign in again."+END);
					isLogin = true;
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	
	
	

	
	
	String oldPassword;
	String newPassword;
	String confirmPassword;
	
	public void validate() {
		
        if (getOldPassword()!=null && getOldPassword().length() == 0) {
            addFieldError("oldPassword", "Please enter existing password");
        } 
        if (getNewPassword()!=null && getNewPassword().length() == 0) {
            addFieldError("oldPassword", "Please enter new password");
        }
        if (getConfirmPassword()!=null && getConfirmPassword().length() == 0) {
            addFieldError("oldPassword", "Please enter new password again to confirm");
        }
        if (getConfirmPassword()!=null && !getConfirmPassword().equals(getNewPassword())) {
            addFieldError("oldPassword", "Password does not match, please confrim the password");
        }
        loadValidateChangePassword();
    }

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getOldPassword() {
		return oldPassword;
	}


	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}


	public String getNewPassword() {
		return newPassword;
	}


	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}


	public String getConfirmPassword() {
		return confirmPassword;
	}


	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}