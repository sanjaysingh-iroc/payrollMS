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

public class AcceptRescheduleReassign extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	String strSessionEmpId;
	
	HttpSession session;
	CommonFunctions CF;
	List<String> innerList;
	String TaskId;
	
	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		
		UtilityFunctions uF = new UtilityFunctions();
		getRequestData(uF);
		
		return LOAD;
	}
	
	public void getRequestData(UtilityFunctions uF){
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();	
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from activity_info where task_id=?");
			pst.setInt(1,uF.parseToInt(getTaskId()));
			rs = pst.executeQuery();
			while(rs.next()){
				innerList = new ArrayList<String>();
				if(rs.getString("r_start_date")!=null && rs.getString("r_deadline")!=null && rs.getString("task_reassign_reschedule_comment")!=null ){
					innerList.add(rs.getString("task_id"));
					innerList.add(rs.getString("requested_by"));
					innerList.add("");
					innerList.add(rs.getString("r_start_date"));
			    	innerList.add(rs.getString("r_deadline"));
			    	innerList.add(rs.getString("pro_id"));
			    	innerList.add(rs.getString("parent_task_id"));
			    	innerList.add(rs.getString("activity_name"));
				}
				else if(rs.getString("r_start_date")==null && rs.getString("r_deadline")==null && rs.getString("task_reassign_reschedule_comment")!=null){
					innerList.add(rs.getString("task_id"));
					innerList.add(rs.getString("requested_by"));
					innerList.add(rs.getString("task_reassign_reschedule_comment"));
					innerList.add("");
					innerList.add("");
					innerList.add(rs.getString("pro_id"));
					innerList.add(rs.getString("parent_task_id"));
					innerList.add(rs.getString("activity_name"));
				}
				alOuter.add(innerList);
		    }
//			System.out.println("alOuter=====>"+alOuter);
			request.setAttribute("alOuter", alOuter);
			rs.close();
			pst.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
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
		return TaskId;
	}

	public void setTaskId(String taskId) {
		TaskId = taskId;
	}
}
