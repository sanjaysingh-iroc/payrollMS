package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class GetProjectCode extends ActionSupport implements
		ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	
	HttpSession session;
	HttpServletRequest request;
	
	String strProjectCode;
	
	public String execute() {
		session = request.getSession();
	
		 
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			request.setAttribute("PCA", "<span style=\"color:green\">Project Code is available.</span>");
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from projectmntnc where project_code = ?");
			pst.setString(1, getStrProjectCode());
			rs = pst.executeQuery();
			while(rs.next()){
				request.setAttribute("PCA", "<span style=\"color:red\">This Project Code is already being used.<br/>Please choose different code.</span>");
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
		
		return SUCCESS;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request=request;
	}


	public String getStrProjectCode() {
		return strProjectCode;
	}


	public void setStrProjectCode(String strProjectCode) {
		this.strProjectCode = strProjectCode;
	}
	
}
