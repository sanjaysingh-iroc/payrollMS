package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddCategory extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {

		String operation = request.getParameter("operation");
		if (operation.equals("D")) {
			return deleteCategory();
		}
		if (operation.equals("U")) { 
				return updateCategory();
		}
		if (operation.equals("A")) {
				return insertCategory();
		}
		
		return SUCCESS;
		
	}

	public String loadValidateCategory() {
		return LOAD;
	}

	public String insertCategory() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertCategory);
			pst.setString(1, getCategoryCode());
			pst.setString(2, uF.showData(getCategoryDesc(),""));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateCategory() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			
			case 0 : columnName = "category_code"; break;
			case 1 : columnName = "category_description"; break;
		
		}
		String updateCategory = "UPDATE category_details SET "+columnName+"=? WHERE category_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateCategory);
			pst.setString(1, uF.showData((request.getParameter("value")),""));
			pst.setInt(2, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}
	
	public String deleteCategory() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteCategory);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String categoryId;
	String categoryCode;
	String CategoryDesc;

	public void validate() {

		if (getCategoryId() != null && getCategoryId().length() == 0) {
			addFieldError("categoryId", "Category ID is required");
		}
		if (getCategoryCode() != null && getCategoryCode().length() == 0) {
			addFieldError("categoryCode", "Category Code is required");
		}
		loadValidateCategory();

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryDesc() {
		return CategoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		CategoryDesc = categoryDesc;
	}

}