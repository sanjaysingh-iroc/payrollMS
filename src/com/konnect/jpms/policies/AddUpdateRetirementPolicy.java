package com.konnect.jpms.policies;

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

public class AddUpdateRetirementPolicy extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	CommonFunctions CF;
	HttpSession session;
	String strOrgId;
	String strSessionEmpId;
	
	String userscreen;
	String navigationId;
	String toPage;
	String operation;
	String retirementAge;
	String strOrgName;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		loadRetirementPolicy();
		System.out.println("getOperation ============>> " + getOperation());
		if (getOperation()!=null && getOperation().equals("E")) {
			return viewRetirementPolicy();
		} else if (getOperation()!=null && getOperation().equals("U")) {
			return updateRetirementPolicy();
		} else {
			setOperation("A");
		}
		return LOAD;
	}
	
	
	public String viewRetirementPolicy() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from org_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getStrOrgId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setOperation("U");
				setRetirementAge(rs.getString("retirement_age"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	public String updateRetirementPolicy() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update org_details set retirement_age=? where org_id=?");
			pst.setDouble(1, uF.parseToDouble(getRetirementAge()));
			pst.setInt(2, uF.parseToInt(getStrOrgId()));
			System.out.println("pst ===>> " + pst);
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+"Retirement age updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	public String loadRetirementPolicy() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			strOrgName = CF.getOrgNameById(con, strOrgId);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrOrgName() {
		return strOrgName;
	}

	public void setStrOrgName(String strOrgName) {
		this.strOrgName = strOrgName;
	}

	public String getRetirementAge() {
		return retirementAge;
	}

	public void setRetirementAge(String retirementAge) {
		this.retirementAge = retirementAge;
	}

}