package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetProjectClientTask22 extends ActionSupport implements ServletRequestAware, IStatements{


	
	String strSessionEmpId;
	HttpSession session;
	String count;
	
	private static final long serialVersionUID = 2097050043213637496L;
	public String execute(){
		
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
	
		if(getClient_id()!=null){
			getProjectList();
		}
		if(getProject_id()!=null){
			getTaskList();
		}

		return SUCCESS;
	}

	
	String client_id;
	String project_id;
	
	public void getProjectList(){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		ResultSet rs =null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<select name=\"strProject\" class=\"validateRequired\" onchange=\"getContent('myTask__"+count+"','GetProjectClientTask12.action?project_id='+this.value)\">");
			sb.append("<option>Select Project</option>");
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(pro.pro_id),pro.pro_name from activity_info ac,projectmntnc pro where ac.emp_id=? and pro.client_id= ?  and pro.pro_id= ac.pro_id and ac.approve_status='n' order by pro_id ");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getClient_id()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				sb.append("<option value=\""+rs.getString("pro_id")+"\">"+rs.getString("pro_name")+"</option>");				
			}
			rs.close();
			pst.close();
			sb.append("<option value=\"-1\">Other Activity</option>");
			sb.append("</select>");
			request.setAttribute("STATUS_MSG", sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getTaskList(){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst =null;
		ResultSet rs = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<select name=\"strTask\" class=\"validateRequired\">");
			sb.append("<option>Select Task</option>");
			con = db.makeConnection(con);
			pst = con.prepareStatement(" select * from activity_info where emp_id =? and pro_id = ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getProject_id()));
			rs = pst.executeQuery();
			while(rs.next()){
				sb.append("<option value=\""+rs.getString("task_id")+"\">"+rs.getString("activity_name")+"</option>");
			}
			rs.close();
			pst.close();
			
//			System.out.println("getProject_id()===>"+uF.parseToInt(getProject_id()));
			
			if(uF.parseToInt(getProject_id())<0){
				sb.append("<option value=\"a Desk Job\">a Desk Job</option>");
				sb.append("<option value=\"a Call\">a Call</option>");
				sb.append("<option value=\"a Sales Call\">a Sales Call</option>");
				sb.append("<option value=\"a Conference Call\">a Conference Call</option>");
				sb.append("<option value=\"a Meeting with my Supervisor\">a Meeting with my Supervisor</option>");
				sb.append("<option value=\"a Meeting with my Subordinate\">a Meeting with my Subordinate</option>");
				sb.append("<option value=\"a Meeting with HR\">a Meeting with HR</option>");
				sb.append("<option value=\"a Team Meeting\">a Team Meeting</option>");
				sb.append("<option value=\"a Meeting with Client\">a Meeting with Client</option>");
				sb.append("<option value=\"a Client Demo\">a Client Demo</option>");
				sb.append("<option value=\"a Client Visit\">a Client Visit</option>");
				sb.append("<option value=\"a Field Visit\">a Field Visit</option>");
				sb.append("<option value=\"a Tranning Session\">a Tranning Session</option>");
				sb.append("<option value=\"to Pantry\">to Pantry</option>");
				sb.append("<option value=\"a Coffee Break\">a Coffee Break</option>");
				sb.append("<option value=\"a Break\">a Break</option>");
			}
			
			sb.append("</select>");
			request.setAttribute("STATUS_MSG", sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}




}
