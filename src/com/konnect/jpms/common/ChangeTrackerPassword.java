package com.konnect.jpms.common;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ChangeTrackerPassword extends ActionSupport implements ServletRequestAware, IStatements {

 	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strLoginType;
	
	public String execute() throws Exception {
		session = request.getSession(true);
		strLoginType = (String)session.getAttribute(LOGIN_TYPE);
		
	 	CF = (CommonFunctions)session.getAttribute(CommonFunctions);
	 	if(CF==null) {
	 		return LOGIN;
	 	}
		
//		System.out.println("getOldPassword() ===>> " + getOldPassword());
//		System.out.println("strLoginType ===>> " + strLoginType);
		
		if(getHideOldPassword() != null && getHideOldPassword().length()>0) {
			setOldPassword(getHideOldPassword());
		}
		
		if(getNewPassword() != null && getNewPassword().length()>0) {
//			updatePassword();
			return updatePassword();
		} else {
			SecureRandom random = new SecureRandom();
			String genFullPassword = new BigInteger(130, random).toString(32);
			String genPassword = genFullPassword.substring(5, 13);
			String gen16Password = genFullPassword.substring(1, 17);
//			System.out.println("genFullPassword ===>>>> " + genFullPassword + " genPassword ===>>>> "  + genPassword + " gen16Password ===>>>> " + gen16Password);
			setNewPassword(gen16Password);
//			setConfirmPassword(gen16Password);
		}
		
		return LOAD;
		
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
			
				pst = con.prepareStatement("Update settings set value=? where options=?");
				pst.setString(1, getNewPassword());
				pst.setString(2, O_TRACKER_PASSWORD);
				pst.execute();
				pst.close();
				request.setAttribute("STATUS_MSG", getNewPassword());
				
//				String strDomain = request.getServerName().split("\\.")[0];
//				Notifications nF = new Notifications(N_UPD_PASSWORD, CF);
//				nF.setDomain(strDomain);
//				nF.request = request;
//				nF.setStrEmpId((String)session.getAttribute(EMPID));
//				nF.setStrContextPath(request.getContextPath());
////				nF.setStrHostAddress(request.getRemoteHost());
//				nF.setStrHostAddress(CF.getStrEmailLocalHost());
//				nF.setStrHostPort(CF.getStrHostPort());
//				nF.setStrNewPassword(getNewPassword());
//				nF.setEmailTemplate(true);
//				nF.sendNotifications();
			

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	
	String hideOldPassword;
	String oldPassword;
	String newPassword;
	String confirmPassword;
	
//	public void validate() {
//		
//        if (getOldPassword()!=null && getOldPassword().length() == 0) {
//            addFieldError("oldPassword", "Please enter existing password");
//        } 
//        if (getNewPassword()!=null && getNewPassword().length() == 0) {
//            addFieldError("oldPassword", "Please enter new password");
//        }
//        if (getConfirmPassword()!=null && getConfirmPassword().length() == 0) {
//            addFieldError("oldPassword", "Please enter new password again to confirm");
//        }
//        if (getConfirmPassword()!=null && !getConfirmPassword().equals(getNewPassword())) {
//            addFieldError("oldPassword", "Password does not match, please confrim the password");
//        }
//    }

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getHideOldPassword() {
		return hideOldPassword;
	}

	public void setHideOldPassword(String hideOldPassword) {
		this.hideOldPassword = hideOldPassword;
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