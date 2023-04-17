package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpTaskActivityHistory extends ActionSupport implements
		ServletRequestAware, ServletResponseAware, SessionAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	int proId;
	String strUsreType;
	String strSessionEmpId;
	String proType;
	String taskId; 
	
	CommonFunctions CF;
	private HttpServletRequest request;

	public String execute() {
			
			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if (CF == null)
				return LOGIN;
			strUsreType = (String)session.getAttribute(BASEUSERTYPE);
			strSessionEmpId = (String)session.getAttribute(EMPID);
			
//			System.out.println("getBtnSave() ===>> " + getBtnSave());
			getEmpTaskActivityData();

		return SUCCESS;
	}

	
	private void getEmpTaskActivityData() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			String percent = null;
			pst = con.prepareStatement("select completed from activity_info where task_id = ? ");
			pst.setInt(1, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			while(rs.next()) {
				percent = rs.getString("completed");
			}
//			System.out.println("percent ==>>> " + percent);
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from task_activity where emp_id = ? and activity_id = ? order by task_date desc");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(taskId));
			rs = pst.executeQuery();
			
			List<List<String>> alTaskActivity = new ArrayList<List<String>>();
			
			while(rs.next()) {
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("task_id"));
				alInner.add(uF.getDateFormat(rs.getString("task_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("start_time"), DBTIME, CF.getStrReportTimeFormat()));
				if(rs.getString("end_time")!=null) {
					alInner.add(uF.getDateFormat(rs.getString("end_time"), DBTIME, CF.getStrReportTimeFormat()));
				} else {
					alInner.add(null);
				}
				alInner.add(uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
				alInner.add(rs.getString("activity_description")!=null && !rs.getString("activity_description").trim().equals("") ? rs.getString("activity_description").trim() : "-");
				alInner.add(uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
				alInner.add((rs.getString("task_location")!= null && rs.getString("task_location").equals("ONS")) ? "Onsite" : "Offsite");
				
				alTaskActivity.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("percent", percent);
			request.setAttribute("alTaskActivity", alTaskActivity);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
	public int getProId() {
		return proId;
	}

	public void setProId(int proId) {
		this.proId = proId;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setSession(Map arg0) {
		// TODO Auto-generated method stub
	}
}
