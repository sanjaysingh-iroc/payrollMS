package com.konnect.jpms.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillColour;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddLoanInfo extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	
	public String execute() throws Exception {
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		if(getOperation()!=null && getOperation().equalsIgnoreCase("U")){
			updateLoan();
		}

		return loadLoan();
	}

	String operation;
	
	public String loadLoan() {
		return LOAD;
	}

	public void updateLoan(){
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateLoanDetails1);
			pst.setString(1, request.getParameter("value"));
			pst.setInt(2, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void validate() {
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}


}