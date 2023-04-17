package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCity;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ValidateTimeSheet implements ServletRequestAware {
	private String timesheet_paycycle;
	HttpServletRequest request;
	String emp_id;

	public String execute() throws Exception {

		checkTimeSheet();
		
			return "success";
	}

	public void checkTimeSheet(){

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select count(*) as count1 from(select emp_id from work_flow_details where is_approved=1 and effective_id in(" +
					"select task_id from task_activity where emp_id=? and timesheet_paycycle=? and is_approved=1) group by emp_id)as a");
			pst.setInt(1, uF.parseToInt(emp_id));
			pst.setInt(2, uF.parseToInt(timesheet_paycycle));
			rs = pst.executeQuery();
			while (rs.next()) {
				request.setAttribute("STATUS_MSG",rs.getString("count1"));
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
	
		
	}


	public String getTimesheet_paycycle() {
		return timesheet_paycycle;
	}

	public void setTimesheet_paycycle(String timesheet_paycycle) {
		this.timesheet_paycycle = timesheet_paycycle;
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	

	
}
