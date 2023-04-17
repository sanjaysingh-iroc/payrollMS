package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesignation;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddDesignaton extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	public String execute() throws Exception {
		

		request.setAttribute(PAGE, PAddDesignation);

		String strEdit = request.getParameter("E");
		String strDelete = request.getParameter("D");

		if (strEdit != null) {

			viewDesignation(strEdit);
			request.setAttribute(TITLE, TViewDesignation);
			return SUCCESS;
		}
		if (strDelete != null) {
			deleteDesignation(strDelete);
			request.setAttribute(TITLE, TDeleteDesignation);
			return VIEW;
		}

		if (getDesigId() != null && getDesigId().length() > 0) {
			updateDesignation();
			request.setAttribute(TITLE, TEditDesignation);
			return UPDATE;
		} else if (getDesigName() != null && getDesigName().length()>0) {
			insertDesignation();
			request.setAttribute(TITLE, TAddDesignation);
		}
		return loadDesignation();

	}

	private String loadValidateDesignation() {
		request.setAttribute(PAGE, PAddDesignation);
		request.setAttribute(TITLE, TAddDesignation);
		return "load";
	}
	
	private String loadDesignation() {
		request.setAttribute(PAGE, PAddDesignation);
		request.setAttribute(TITLE, TAddDesignation);
		setDesigName("");
		return "load";
	}

	private String insertDesignation() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertDesignation);
			pst.setString(1, getDesigName());
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, getDesigName()+" added successfully!");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}
	
	public String updateDesignation() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(updateDesignation);
			pst.setString(1, getDesigName());
			pst.setInt(2, uF.parseToInt(getDesigId()));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, getDesigName() + " updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
			return ERROR;
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewDesignation(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectDesignationV);
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			while (rs.next()) {
				setDesigId(rs.getString("desig_id"));
				setDesigName(rs.getString("desig_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteDesignation(String strDelete) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteDesignation);
			pst.setInt(1, uF.parseToInt(strDelete));
			pst.execute();
			pst.close();


			request.setAttribute(MESSAGE, "Deleted SuccessFully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
			return ERROR;
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	String desigId = null;
	String desigName = null;
	

	public void validate() {
		
        if (getDesigName()!=null && getDesigName().length() == 0) {
            addFieldError("desigName", "Designaton Name is required");
            loadValidateDesignation();
        } 
    }

	public String getDesigId() {
		return desigId;
	}

	public void setDesigId(String desigId) {
		this.desigId = desigId;
	}

	public String getDesigName() {
		return desigName;
	}

	public void setDesigName(String desigName) {
		this.desigName = desigName;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	

}
