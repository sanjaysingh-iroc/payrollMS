package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeclineRescheduleReassign extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements{

	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	String taskId;

	
	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		DeclineRequestData(uF);
		
		return LOAD;
	}
	public void DeclineRequestData(UtilityFunctions uF){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try{
			con = db.makeConnection(con);
			pst = con.prepareStatement("update activity_info set task_accept_status=0,r_start_date=?,r_deadline=?,task_reassign_reschedule_comment=?,requested_by=?,reschedule_reassign_request_status=? where task_id=?");
			pst.setDate(1,null);
			pst.setDate(2,null);
			pst.setString(3,null);
			pst.setInt(4,0);
			pst.setInt(5,-1);
			pst.setInt(6,uF.parseToInt(getTaskId()));
			pst.executeUpdate();
//			System.out.println("Decline pst=====>"+pst);
			pst.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
