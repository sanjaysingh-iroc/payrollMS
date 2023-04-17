package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveBasicFitmentStructure extends ActionSupport implements ServletRequestAware, IStatements{

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
	
	public String execute()	{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		request.setAttribute(PAGE, "/jsp/reports/master/ApproveBasicFitment.jsp");
		request.setAttribute(TITLE, "Approve Basic Fitment");

		UtilityFunctions uF = new UtilityFunctions();
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		approveGrade(uF);
		
		return LOAD;
	}
	private void approveGrade(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from org_details where org_id in (select org_id from EMPLOYEE_OFFICIAL_DETAILS where emp_id=?)");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			rs = pst.executeQuery();
			int nIncrementBase = 0;
			while(rs.next()){
				nIncrementBase = rs.getInt("increment_type");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("insert into emp_basic_fitment_details (emp_id,grade_from,grade_to,fitment_month,fitment_year,entry_date," +
					"approve_by,approve_status,increment_type) values(?,?,?,?, ?,?,?,?, ?)");
			pst.setInt(1, uF.parseToInt(getEmp_id()));
			pst.setInt(2, uF.parseToInt(getGrade_from()));
			pst.setInt(3, uF.parseToInt(getGrade_to()));
			pst.setInt(4, uF.parseToInt(getFitmentMonth()));
			pst.setInt(5, uF.parseToInt(getFitmentYear()));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));		
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setInt(8, 1);
			pst.setInt(9, nIncrementBase);
			int x = pst.executeUpdate();
			
			if(x>0){
				pst = con.prepareStatement("update employee_official_details set grade_id = ? where emp_id=?");
				pst.setInt(1, uF.parseToInt(getGrade_to()));
				pst.setInt(2, uF.parseToInt(getEmp_id()));
				pst.execute();
				
				/*request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\""+ request.getContextPath()+ "/images1/icons/approved.png\" border=\"0\">"); */
				request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>"); 
			}
			
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
	
}
