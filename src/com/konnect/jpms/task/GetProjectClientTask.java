package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillTask;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetProjectClientTask extends ActionSupport implements ServletRequestAware, IStatements{

	String strSessionEmpId;
	HttpSession session;
	
	private static final long serialVersionUID = 2097050043213637496L;
	public String execute(){
		
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
	
		if(getClient_id()!=null) {
			if(getStrEmpId() == null || getStrEmpId().equals("")) {
				setStrEmpId(strSessionEmpId);
			}
			if(getType() != null && getType().equals("BULKEXP")) {
				getProjectListBULKEXP();
			} else if(getType() != null && getType().equals("R")) {
				getProjectListR();
			} else {
				getProjectList();
			}
		}
		if(getProject_id()!=null) {
			getTaskList();
		}
		return SUCCESS;
	}

	
	private void getProjectListBULKEXP() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<select name=\"strProject_project"+getCnt()+"\"  id=\"strProject_project"+getSaveType()+"_"+getParentId()+"_"+getCnt()+"\" style=\"float:left;margin-right: 10px;\" class=\"validateRequired\" required=\"true\" >");
			sb.append("<option value=''>Select Project</option>");
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pro.pro_id), pro.pro_name from activity_info ac, projectmntnc pro where ac.resource_ids like '%,"+ getStrEmpId() +",%' ");
			if(getClient_id() != null && getClient_id().length()>0) {
				sbQuery.append(" and pro.client_id in ("+getClient_id()+") ");
			}
			sbQuery.append(" and pro.pro_id= ac.pro_id and pro.approve_status='n' order by pro_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ====>>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()){
				sb.append("<option value=\""+rs.getString("pro_id")+"\">"+rs.getString("pro_name")+"</option>");				
			}
			rs.close();
			pst.close();
			sb.append("</select>");
			request.setAttribute("STATUS_MSG", sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	String client_id;
	String project_id;
	String type;
	String strEmpId;
	String parentId;
	String saveType;
	String cnt;
	
	
	public void getProjectList() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append("<select name=\"strProject\" id=\"strProject\" style=\"float:left;margin-right: 10px;\" multiple=\"true\" onchange=\"getTaskList()\">");
			sb.append("<option value=''>Select Project</option>");
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pro.pro_id), pro.pro_name from activity_info ac, projectmntnc pro where ac.resource_ids like '%,"+ getStrEmpId() +",%' ");
			if(getClient_id() != null && getClient_id().length()>0) {
				sbQuery.append(" and pro.client_id in ("+getClient_id()+") ");
			}
			sbQuery.append(" and pro.pro_id= ac.pro_id and pro.approve_status='n' order by pro_id");
			
			pst = con.prepareStatement(sbQuery.toString());
//			PreparedStatement pst = con.prepareStatement("  ");
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			pst.setInt(1, uF.parseToInt(getClient_id()));
			rs = pst.executeQuery();
			
			while(rs.next()) {
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
	
	
public void getProjectListR() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
//			String cnt = (String) request.getParameter("cnt");
			StringBuilder sb = new StringBuilder();
			if(uF.parseToInt(cnt) > 0){       
				sb.append("<select name=\"strProject"+getCnt()+"\"  id=\"strProject"+getCnt()+"\" style=\"float:left;margin-right: 10px;\" class=\"validateRequired\" required=\"true\"  >");
			} else {
				sb.append("<select name=\"strProject\" id=\"strProject\" style=\"float:left;margin-right: 10px;\" class=\"validateRequired\" required=\"true\" >");
			}
			sb.append("<option value=''>Select Project</option>");
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pro.pro_id), pro.pro_name from activity_info ac, projectmntnc pro where ac.resource_ids like '%,"+ getStrEmpId() +",%' ");
			if(getClient_id() != null && getClient_id().length()>0) {
				sbQuery.append(" and pro.client_id in ("+getClient_id()+") ");
			}
			sbQuery.append(" and pro.pro_id= ac.pro_id and pro.approve_status='n' order by pro_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ====>>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()){
				sb.append("<option value=\""+rs.getString("pro_id")+"\">"+rs.getString("pro_name")+"</option>");				
			}
			rs.close();
			pst.close();
//			sb.append("<option value=\"-1\">Other Activity</option>");
			sb.append("</select>");
			request.setAttribute("STATUS_MSG", sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
	public void getTaskList() {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append("<select name=\"strTask\" class=\"validateRequired\" >");
			sb.append("<option value=''>Select Task</option>");
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from activity_info where resource_ids like '%,"+ strSessionEmpId+",%' ");
			if(getProject_id() != null && getProject_id().length()>0) {
				sbQuery.append("and pro_id in("+getProject_id()+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//			pst.setInt(1, uF.parseToInt(getProject_id()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				if(rs.getString("activity_name") != null && !rs.getString("activity_name").equals("")) {
					String activityName = rs.getString("activity_name").replace(".", ". ");
					activityName = activityName.replace(",", ", ");
					String strTemp[] = activityName.split(" ");
					StringBuilder sbTaskName = new StringBuilder();
					for(int i=0; strTemp.length >0 && i<strTemp.length; i++) {
						if(strTemp[i].trim().length()>0) {
							sbTaskName.append(strTemp[i].trim()+" ");
						}
					}
					sb.append("<option value=\""+rs.getString("task_id")+"\">"+sbTaskName.toString().trim()+"</option>");
				}
				
			}
			rs.close();
			pst.close();
			
			if(getProject_id() != null && getProject_id().length() > 0){
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
				sb.append("<option value=\"a Training Session\">a Training Session</option>");
				sb.append("<option value=\"to Pantry\">to Pantry</option>");
				sb.append("<option value=\"a Coffee Break\">a Coffee Break</option>");
				sb.append("<option value=\"a Break\">a Break</option>");
				sb.append("<option value=\"Quality Control\">Quality Control</option>");
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public String getCnt() {
		return cnt;
	}

	public void setCnt(String cnt) {
		this.cnt = cnt;
	}

}
