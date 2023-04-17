package com.konnect.jpms.payroll;

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

public class ArrearApproveDeny extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;
	
	String arear_id;
	String actionType;
	String strApproveDenyComment;
	String operation;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getOperation() != null && getOperation().equalsIgnoreCase("U")) {
			updateApproveDenyStatus(uF);
			return SUCCESS;
		}
		
		return LOAD;
	}


	private void updateApproveDenyStatus(UtilityFunctions uF) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update arear_details set is_approved=?, approve_deny_comment=?, approved_by=?, approve_date=? where arear_id=?");
			pst.setInt(1, (getActionType() != null && getActionType().equals("A")) ? 1 : -1);
			pst.setString(2, getStrApproveDenyComment());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(getArear_id()));
			pst.executeUpdate();
//			System.out.println("pst====>"+pst);
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
	public String getArear_id() {
		return arear_id;
	}

	public void setArear_id(String arear_id) {
		this.arear_id = arear_id;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getStrApproveDenyComment() {
		return strApproveDenyComment;
	}

	public void setStrApproveDenyComment(String strApproveDenyComment) {
		this.strApproveDenyComment = strApproveDenyComment;
	}

}