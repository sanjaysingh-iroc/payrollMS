package com.konnect.jpms.ajax;

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

public class DeferBasicFitment extends ActionSupport implements ServletRequestAware, IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5947893602821384559L;
	String strUserType = null;
	String strSessionEmpId = null;
	
	HttpSession session;
	CommonFunctions CF; 

	String operation;
	String emp_id;
	String grade_from;
	String grade_to;
	String fitmentMonth;
	String fitmentYear;
	String deferDate;
	
	public String execute()	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getOperation()!=null && getOperation().trim().equalsIgnoreCase("U")){
			insertDefer(uF);
			return SUCCESS;
		}
		
		return LOAD;
	}
	
	
	private void insertDefer(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("insert into emp_defer_basic_fitment (emp_id,defer_date, grade_from, grade_to, fitment_month, " +
					"fitment_year, entry_date, approve_by, defer_status) values(?,?,?,?, ?,?,?,?, ?)");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setDate(2, uF.getDateFormat(getDeferDate(), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getGrade_from()));
			pst.setInt(4, uF.parseToInt(getGrade_to()));
			pst.setInt(5, uF.parseToInt(getFitmentMonth()));
			pst.setInt(6, uF.parseToInt(getFitmentYear()));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));		
			pst.setInt(8, uF.parseToInt(strSessionEmpId));
			pst.setBoolean(9, true);
			pst.executeUpdate();

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
		this.request = request;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getGrade_from() {
		return grade_from;
	}
	public void setGrade_from(String grade_from) {
		this.grade_from = grade_from;
	}
	public String getGrade_to() {
		return grade_to;
	}
	public void setGrade_to(String grade_to) {
		this.grade_to = grade_to;
	}
	public String getFitmentMonth() {
		return fitmentMonth;
	}
	public void setFitmentMonth(String fitmentMonth) {
		this.fitmentMonth = fitmentMonth;
	}
	public String getFitmentYear() {
		return fitmentYear;
	}
	public void setFitmentYear(String fitmentYear) {
		this.fitmentYear = fitmentYear;
	}
	public String getDeferDate() {
		return deferDate;
	}
	public void setDeferDate(String deferDate) {
		this.deferDate = deferDate;
	}
}
