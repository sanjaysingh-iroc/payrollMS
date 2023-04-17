package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EndTaskPopup extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements{

	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	
	String id;
	String taskId;
	int pro_id;
	int emp_id; 
	String fromPage;
	String isBillable;
	String taskLocation;
	String taskDescription;
	
	public String execute() {
		
		session = request.getSession();
		UtilityFunctions uF = new UtilityFunctions();
		
		emp_id = uF.parseToInt((String) session.getAttribute(EMPID));
//		System.out.println("pro_id ===>> " + pro_id);
//		System.out.println("id ===>> " + id);
//		System.out.println("fromPage ===>> " + fromPage);
		
		getTaskData();
		
		return SUCCESS;
	}
	
	private void getTaskData() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select is_billable,task_location,_comment from task_activity WHERE task_id=(select max(task_id) from task_activity " +
//				" where emp_id=? and activity_id=? )");
			pst = con.prepareStatement("select is_billable,task_location,_comment from task_activity WHERE task_id=?");
			pst.setInt(1, uF.parseToInt(getTaskId()));
//			pst.setInt(1, emp_id);
//			pst.setInt(2, uF.parseToInt(getId()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setIsBillable(rs.getString("is_billable"));
				setTaskLocation(rs.getString("task_location"));
				setTaskDescription(rs.getString("_comment"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getId() {
		return id;
	}

	public int getPro_id() {
		return pro_id;
	}
	
	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getIsBillable() {
		return isBillable;
	}

	public void setIsBillable(String isBillable) {
		this.isBillable = isBillable;
	}

	public String getTaskLocation() {
		return taskLocation;
	}

	public void setTaskLocation(String taskLocation) {
		this.taskLocation = taskLocation;
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

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}

}
