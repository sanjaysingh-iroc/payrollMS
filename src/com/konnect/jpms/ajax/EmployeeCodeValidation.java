package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeCodeValidation extends ActionSupport implements ServletRequestAware, IStatements {
	private String empCode = null;
	private String empCodeAMsg = null;
	private String empCodeNAMsg = null;

	public String execute() throws Exception {
		  
		if (getEmpCode() != null && !getEmpCode().equals("")) {
			if(validateCode(getEmpCode())){
				empCodeNAMsg="Sorry, "+getEmpCode()+ " code already exists";
			}else{
				empCodeAMsg=getEmpCode()+ " code is available";
			}
			return SUCCESS;
		} else {
			empCodeNAMsg=null;
			empCodeAMsg=null;
			return SUCCESS;
		}
	}


	public boolean validateCode(String strEmpCode){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		boolean isExist = false;
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmployee_EmpCode);			
			pst.setString(1, strEmpCode);			
			rst = pst.executeQuery();
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


	public String getEmpCode() {
		return empCode;
	}


	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}


	public String getEmpCodeAMsg() {
		return empCodeAMsg;
	}


	public void setEmpCodeAMsg(String empCodeAMsg) {
		this.empCodeAMsg = empCodeAMsg;
	}


	public String getEmpCodeNAMsg() {
		return empCodeNAMsg;
	}


	public void setEmpCodeNAMsg(String empCodeNAMsg) {
		this.empCodeNAMsg = empCodeNAMsg;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
}
