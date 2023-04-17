package com.konnect.jpms.successionplan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddAndRemoveEmpFromSuccessionplan extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;  
	CommonFunctions CF;
	String empID;
	String type; 
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		if(getType() != null && getType().equals("Remove")) {
			remomveEmpFromSuccessionPlan();
		} else {
			
		}
			return SUCCESS;

	}
	
	private void remomveEmpFromSuccessionPlan() {
	
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update employee_official_details set remove_from_successionplan = ? where emp_id = ?");
			pst.setInt(1, 1);
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.executeUpdate();	
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}
	
}
