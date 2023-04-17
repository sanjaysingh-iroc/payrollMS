package com.konnect.jpms.requsitions;

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

public class GetEmpClientProject extends ActionSupport implements ServletRequestAware, IStatements{


	
	String strSessionEmpId;
	HttpSession session;
	String empId;
	
	private static final long serialVersionUID = 2097050043213637496L;
	public String execute(){
		
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
	
		if(getClient_id()!=null && getEmpId()!=null){
			getProjectList(); 
		}
		

		return SUCCESS;
	}

	
	String client_id;
	
	public void getProjectList(){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		UtilityFunctions uF = new UtilityFunctions();
		
		System.out.println("====>ProjectList");
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<select name=\"strProject\" class=\"validateRequired\" onchange=\"getContent('myTask','GetProjectClientTask.action?project_id='+this.value)\">");
			sb.append("<option value=\"\">Select Project</option>");
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(pro.pro_id),pro.pro_name from activity_info ac,projectmntnc pro where ac.emp_id=? and pro.client_id= ?  and pro.pro_id= ac.pro_id and ac.approve_status='n' order by pro_id ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
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
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
}
