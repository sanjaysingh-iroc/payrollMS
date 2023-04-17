package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class UserNameValidation extends ActionSupport implements IStatements, ServletRequestAware {
	private String userName = null;
	private String userNameAMsg = null;
	private String userNameNAMsg = null;

	public String execute() throws Exception {

		//System.out.println("getUserName()===>"+getUserName());
		  
		if (getUserName() != null && !getUserName().equals("")) {
			if(validateCode(getUserName())){
				userNameNAMsg="Sorry, "+getUserName()+ " user already exists";
			}else{
				userNameAMsg=getUserName()+ " username is available";
			}
			return SUCCESS;
		} else {
			userNameNAMsg=null;
			userNameAMsg=null;
			return SUCCESS;
		}
	}


	public boolean validateCode(String strUserName){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		boolean isExist = false;
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectUserV2);			
			pst.setString(1, strUserName);			
			rst = pst.executeQuery();
			//System.out.println("pst===>"+pst);
			while(rst.next()){
				isExist = true;
			}
            rst.close();
            pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return isExist;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserNameAMsg() {
		return userNameAMsg;
	}


	public void setUserNameAMsg(String userNameAMsg) {
		this.userNameAMsg = userNameAMsg;
	}


	public String getUserNameNAMsg() {
		return userNameNAMsg;
	}


	public void setUserNameNAMsg(String userNameNAMsg) {
		this.userNameNAMsg = userNameNAMsg;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}


	
}
