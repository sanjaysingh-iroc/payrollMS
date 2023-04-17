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
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ChangeUserName extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null; 
	
	String empid;
	String userid;
	String username;
	String newPassword; 
	
	String type;
	String fromPage; 
	String strAction =null;
	String strBaseUserType = null;
	public String execute() throws Exception {
		
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		if(CF==null)return LOGIN;
		
		//Created By Dattatray 10-6-2022
				strAction = request.getServletPath();
				if(strAction!=null) {
					strAction = strAction.replace("/","");
				}
				
//		System.out.println("getType() ===>> " + getType());
		if(getType()!=null && getType().equals("update")){
			updateUsername();
//			System.out.println("getFromPage() ===>> " + getFromPage());
			if(getFromPage()!=null && getFromPage().equalsIgnoreCase("people")){
//				System.out.println("getFromPage() in people ===>> " + getFromPage());
				return "people";
			} else {
				return SUCCESS;
			}
		} else if(getType()!=null && getType().equals("ajax")) {
			checkUsername();
			return "ajax";
		} 
		viewUSerData();
		return LOAD;
	}
	
	//Created By Dattatray 10-6-2022
		private void loadPageVisitAuditTrail(CommonFunctions CF,UtilityFunctions uF,String empId) {
			Connection con=null;
			Database db = new Database();
			db.setRequest(request);
			try {
				con = db.makeConnection(con);
				StringBuilder builder = new StringBuilder();
				builder.append("Username changed");
				builder.append("Emp Id :"+empId);
				
				CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				db.closeConnection(con);
			}
		}
	private void checkUsername() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("Select * from user_details where upper(username)=? and user_id != ? and emp_id != ?");
			pst.setString(1, getUsername().toUpperCase().trim());
			pst.setInt(2, uF.parseToInt(getUserid()));
			pst.setInt(3, uF.parseToInt(getEmpid()));
//			System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			boolean flag=false;
			while(rs.next()){
				flag=true;
			}
			rs.close();
			pst.close();
			
			int a=0;
			if(flag){
				a=1;
			}
			request.setAttribute("STATUS_MSG",""+a);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void updateUsername() {

		Connection con = null;		
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update user_details set username=?,password=? where user_id=? and emp_id=? ");
			pst.setString(1, getUsername());
			pst.setString(2, getNewPassword());
			pst.setInt(3, uF.parseToInt(getUserid()));
			pst.setInt(4, uF.parseToInt(getEmpid()));
			pst.execute();
			pst.close();
			
			loadPageVisitAuditTrail(CF, uF, getEmpid());//Created By Dattatray 10-06-2022
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void viewUSerData() {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from user_details where user_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getUserid()));
			pst.setInt(2, uF.parseToInt(getEmpid()));
			rs = pst.executeQuery();
			while(rs.next()){
				setUsername(uF.showData(rs.getString("username"), ""));
//				System.out.println("pwd====>"+rs.getString("password"));
				setNewPassword(uF.showData(rs.getString("password"), ""));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}