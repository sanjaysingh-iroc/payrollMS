package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class DesignationReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String execute() throws Exception {
		
				
		request.setAttribute(PAGE, PReportDesignation);
		request.setAttribute(TITLE, TViewDesignation);
		
		
			viewDesignation();			
			return loadDesignation();

	}
	
	
	public String loadDesignation(){
		
		return LOAD;
	}
	
	public String viewDesignation(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectDesignationR);
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("desig_name"));
				alInner.add("<a href="+request.getContextPath()+"/AddDesignation.action?E="+rs.getString("desig_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/AddDesignation.action?D="+rs.getString("desig_id")+">Delete</a>");
				al.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
