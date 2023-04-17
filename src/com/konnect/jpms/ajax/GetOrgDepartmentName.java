package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrgDepartmentName extends ActionSupport implements ServletRequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String departId;

	public String execute() throws Exception {
		getDepartName();
		return SUCCESS;
	}
	
	public String getDepartName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			 pst = con.prepareStatement("select dept_name from department_info where dept_id=?");
             pst.setInt(1, uF.parseToInt(getDepartId()));
             rs = pst.executeQuery();
             String departName = "Please select department";
             while (rs.next()) {
            	 departName = "Department: "+rs.getString("dept_name");
             }
             rs.close();
             pst.close();
             
			request.setAttribute("STATUS_MSG", departName);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	
	public String getDepartId() {
		return departId;
	}

	public void setDepartId(String departId) {
		this.departId = departId;
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	
}
