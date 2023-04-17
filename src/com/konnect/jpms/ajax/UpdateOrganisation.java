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

public class UpdateOrganisation extends ActionSupport implements ServletRequestAware, IStatements  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String empid;
	String superid;
	HttpSession session;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		
		CommonFunctions CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		updateOrganisation(CF);
		
		return SUCCESS;
	
	}

	public void updateOrganisation(CommonFunctions CF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst =null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
		
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update employee_official_details set supervisor_emp_id = ? where emp_id=?");			
			pst.setInt(1, uF.parseToInt(getSuperid()));
			pst.setInt(2, uF.parseToInt(getEmpid()));
			pst.execute();
            rs.close();
            pst.close();
			
			
			int  wlocation_id=0;
			int  department_id=0;
			int  level_id=0;
			int  desig_id=0;
			int  grade_id=0;
			int  emp_id=0;
			String  emp_status_code=null;
			int  probationPeriod=0;
			int  noticePeriod=0;
			
			pst = con.prepareStatement("select * from employee_activity_details where emp_id=? order by emp_activity_id desc limit 1");
			pst.setInt(1, uF.parseToInt(getSuperid()));
			rs = pst.executeQuery();
			while(rs.next()){
				wlocation_id = rs.getInt("wlocation_id");
				department_id = rs.getInt("department_id");
				level_id = rs.getInt("level_id");
				desig_id = rs.getInt("desig_id");
				grade_id = rs.getInt("grade_id");
				emp_id = rs.getInt("emp_id");
				emp_status_code = rs.getString("emp_status_code");
				noticePeriod = rs.getInt("notice_period");
				probationPeriod = rs.getInt("probation_period");
				
			}
            rs.close();
            pst.close();
			
			pst = con.prepareStatement(insertEmpActivity);

			pst.setInt(1, wlocation_id);
			pst.setInt(2, department_id);
			pst.setInt(3, level_id);
			pst.setInt(4, desig_id);
			pst.setInt(5, grade_id);
			pst.setString(6, emp_status_code);
			pst.setInt(7, 15);   // 15 = usertype change
			pst.setString(8, "Updated through Orgainsational Tree.");
			pst.setDate(9, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt((String)session.getAttribute(USERID)));
			pst.setInt(12, emp_id);
			pst.setInt(13, noticePeriod);
			pst.setInt(14, probationPeriod);
			
			pst.execute();
            pst.close();
			
			
			request.setAttribute(MESSAGE, SUCCESSM+"Tree updated successfully"+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, ERRORM+"Tree could not be updated. Please try again."+END);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getSuperid() {
		return superid;
	}

	public void setSuperid(String superid) {
		this.superid = superid;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	}
