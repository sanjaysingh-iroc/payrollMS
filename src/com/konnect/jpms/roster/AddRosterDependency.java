package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.PayPayroll;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddRosterDependency extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2009608472005407630L;
	String isRoster;
	String isRosterEmpId;
	
	private static Logger log = Logger.getLogger(AddRosterDependency.class);
	
	public String execute() {
		String operation = request.getParameter("operation");
		
		if(operation != null && operation.equals("U")) {
			return updateEmployeeRosterDependency();
		}
		
		return SUCCESS;
	}
	
	public String updateEmployeeRosterDependency() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		
		log.debug("emp_id=>"+request.getParameter("id"));
		log.debug("columnId==>"+columnId);
		log.debug("value==>>"+request.getParameter("value"));
		
		setIsRoster(request.getParameter("value"));
		setIsRosterEmpId(request.getParameter("id"));
		
		String columnName=null;
		
		switch(columnId) {
			case 5 : columnName = "is_roster"; break;
		}
		String updateEmployeeRoster = "UPDATE employee_official_details SET "+columnName+"=? WHERE emp_id=?";
		
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateEmployeeRoster);

			if(columnId==5) {
				
				pst.setBoolean(1, uF.parseToBoolean(getIsRoster()));
				pst.setInt(2, uF.parseToInt(getIsRosterEmpId()));
				int cnt = pst.executeUpdate();
				pst.close();
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		log.debug("Returning UPDATE after updation");
		return UPDATE;
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getIsRoster() {
		return isRoster;
	}

	public void setIsRoster(String isRoster) {
		this.isRoster = isRoster;
	}

	public String getIsRosterEmpId() {
		return isRosterEmpId;
	}

	public void setIsRosterEmpId(String isRosterEmpId) {
		this.isRosterEmpId = isRosterEmpId;
	}
	
}
