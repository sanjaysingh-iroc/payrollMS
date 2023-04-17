package com.konnect.jpms.task;

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

public class ViewCustomerLoginDetail extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF; 
	
	String spocId;
	String username;
	String password; 
	
	public String execute() throws Exception {
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		getCustomerUserNamePassword();
		
		return SUCCESS;
	
	}
	
	
	public void getCustomerUserNamePassword() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
				pst = con.prepareStatement("select username, password from user_details_customer where emp_id=?");
				pst.setInt(1, uF.parseToInt(getSpocId()));
				rs = pst.executeQuery();
				while(rs.next()) {
					setUsername(rs.getString("username"));
					setPassword(rs.getString("password"));
				}
				rs.close();
				pst.close();
				
//				for(int i=0; tlEmpList!=null && !tlEmpList.isEmpty() && i<tlEmpList.size(); i++) {
//					String strDomain = request.getServerName().split("\\.")[0];
//					Notifications nF = new Notifications(N_PROJECT_RE_OPENED, CF); 
//					nF.setDomain(strDomain);
//					
//					nF.setStrEmpId(tlEmpList.get(i));
//					nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
//					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
//					nF.setStrHostAddress(CF.getStrEmailLocalHost());
//					nF.setStrHostPort(CF.getStrHostPort());
//					nF.setStrContextPath(request.getContextPath());
//					nF.sendNotifications();
//				}
				
//				if(uF.parseToInt(hmTaskProData.get("PROJECT_SPOC_ID")) > 0) {
//					
//					String strDomain = request.getServerName().split("\\.")[0];
//					Notifications nF = new Notifications(N_PROJECT_RE_OPENED, CF); 
//					nF.setDomain(strDomain);
//					
//					pst = con.prepareStatement("select * from client_poc where poc_id = ?");
//					pst.setInt(1, uF.parseToInt(hmTaskProData.get("PROJECT_SPOC_ID")));
//					rs = pst.executeQuery();
//					boolean flg=false;
//					while(rs.next()) {
//						nF.setStrEmpFname(rs.getString("contact_fname"));
//						nF.setStrEmpLname(rs.getString("contact_lname"));
//						nF.setStrEmpMobileNo(rs.getString("contact_number"));
//						if(rs.getString("contact_email")!=null && rs.getString("contact_email").indexOf("@")>0) {
//							nF.setStrEmpEmail(rs.getString("contact_email"));
//							nF.setStrEmailTo(rs.getString("contact_email"));
//						}
//						flg = true;
//					}
//					rs.close();
//					pst.close();
//					
//					if(flg) {
//						nF.setStrHostAddress(CF.getStrEmailLocalHost());
//						nF.setStrHostPort(CF.getStrHostPort());
//						nF.setStrContextPath(request.getContextPath());
//						nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
//						nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
//						nF.sendNotifications(); 
//					}
//				}
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getSpocId() {
		return spocId;
	}


	public void setSpocId(String spocId) {
		this.spocId = spocId;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

}