package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class PreEditProjectDetails extends ActionSupport implements ServletRequestAware,ServletResponseAware,IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int pro_id;
	String pro_name;
	String service;
	String description;
	String idealtime;
	String deadline;
	String filename;
	HttpSession session1;
	private HttpServletRequest request;
	CommonFunctions CF;
	
	public String execute()
	{
		session1 = request.getSession();
		CF = (CommonFunctions)session1.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		getProjectDetails();
		return SUCCESS;
	}
	
	public void getProjectDetails() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select * from projectmntnc where pro_id=?");
			pst.setInt(1,pro_id);
			rs=pst.executeQuery();
			while (rs.next()) {
				pro_name=rs.getString("pro_name");
				service=rs.getString("service");
				description=rs.getString("description");
				idealtime=rs.getString("idealtime");				
				deadline=rs.getString("deadline");
				
			}
			rs.close();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIdealtime() {
		return idealtime;
	}

	public void setIdealtime(String idealtime) {
		this.idealtime = idealtime;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}

}
