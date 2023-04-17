package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.employee.EmployeeActivity;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveIncrement extends ActionSupport implements IStatements, ServletRequestAware{

	private String strEmpId; 
	private String strActivityId;
	private String strMonth;
	private String strYear;
	 
	HttpSession session;  
	CommonFunctions CF;
	 
	public String execute() throws Exception { 
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		
		strEmpId = request.getParameter("EMPID");
		strActivityId = request.getParameter("ACTIVITYID");
		strMonth = request.getParameter("MONTH");
		strYear = request.getParameter("YEAR");
		
		approveIncrements();
		
		return SUCCESS;
	
	}

	public void approveIncrements(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);   
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement(selectEmpIncrementDetails);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, uF.parseToInt(strActivityId));
			rs = pst.executeQuery();
			int nWLocation = 0;
			int nDepartment = 0;
			int nLevel = 0;
			int nDesignation = 0;
			int nGrade = 0;
			int nNewStatus = 0;
			String strReason = null;
			while(rs.next()){
				
				nWLocation = rs.getInt("wlocation_id");
				nDepartment = rs.getInt("department_id");
				nLevel = rs.getInt("level_id");
				nDesignation = rs.getInt("desig_id");
				nGrade = rs.getInt("grade_id");
				nNewStatus = rs.getInt("emp_status_id");
				strReason = rs.getString("reason");
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement(insertEmpActivity);
			pst.setInt(1, nWLocation);
			pst.setInt(2, nDepartment);
			pst.setInt(3, nLevel);
			pst.setInt(4, nDesignation);
			pst.setInt(5, nGrade);
			pst.setInt(6, nNewStatus);
			pst.setInt(7, uF.parseToInt(strActivityId));
			pst.setString(8, strReason);
			pst.setDate(9, uF.getDateFormat("01/"+strMonth+"/"+strYear, DATE_FORMAT));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt((String)session.getAttribute(USERID)));
			pst.setInt(12, uF.parseToInt(strEmpId));
			pst.execute();

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


}