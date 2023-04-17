package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetTaskDescription extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String taskDescription;
	private String taskId;
	
	public String execute() {
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(getTaskId()) > 0) {
			checkTaskIsBillable(uF);
		}
		return SUCCESS;
	}
	
	
	private void checkTaskIsBillable(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			boolean flag = false;
			pst = con.prepareStatement("select is_billable_task from activity_info where task_id=?");
			pst.setInt(1, uF.parseToInt(getTaskId()));
			// System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getBoolean("is_billable_task")) {
					flag = true;
				} else {
					flag = false;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("isBillable", flag);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getTaskDescription() {
		return taskDescription;
	}
	
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
