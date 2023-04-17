package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddUserType extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {

		request.setAttribute(PAGE, PAddUserType);

		String strEdit = request.getParameter("E");
		String strDelete = request.getParameter("D");

		if (strEdit != null) {
 
			viewUserType(strEdit);
			request.setAttribute(TITLE, TViewUserType);
			return SUCCESS;
		}
		if (strDelete != null) {
			deleteUserType(strDelete);
			request.setAttribute(TITLE, TDeleteUserType);
			return VIEW;
		}

		if (getUserTypeId() != null && getUserTypeId().length() > 0) {
			updateUserType();
			request.setAttribute(TITLE, TEditUserType);
			return UPDATE;
		} else if (getUserType() != null && getUserType().length()>0) {
			insertUserType();
			request.setAttribute(TITLE, TAddUserType);
		}
		return loadUserType();

	}

	public String loadUserType() {
		setUserTypeId("");
		setUserType("");
		return "load";
	}

	public String insertUserType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertUserType);
			pst.setString(1, getUserType());
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, getUserType() + " added successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");			
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	public String updateUserType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(updateUserType);
			pst.setString(1, getUserType());
			pst.setInt(2, uF.parseToInt(getUserTypeId()));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, getUserType() + " updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");			
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewUserType(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectUserTypeV);
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			while (rs.next()) {
				setUserType(rs.getString("user_type"));
				setUserTypeId(rs.getString("user_type_id"));
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

	public String deleteUserType(String strDelete) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteUserType);
			pst.setInt(1, uF.parseToInt(strDelete));
			pst.execute();
			pst.close();


			request.setAttribute(MESSAGE, "Deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String userType;
	String userTypeId;

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserTypeId() {
		return userTypeId;
	}

	public void setUserTypeId(String userTypeId) {
		this.userTypeId = userTypeId;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
