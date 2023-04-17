package com.konnect.jpms.task.tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddProjectCategory extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	
	String operation;
	
	String strOrg;
	String strOrgName;
	
	String proCategoryId;
	String proCategory;
	String proDescription;
	
	
	List<FillOrganisation> orgList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		orgList = new FillOrganisation(request).fillOrganisation();
		
		if (getOperation()!=null && getOperation().equals("D")) {
			return deleteProjectCategory(getProCategoryId(), uF); 
		} 
		if (getOperation()!=null && getOperation().equals("E")) { 
			return viewProjectCategory(getProCategoryId(), uF);
		}
		if (getProCategoryId() != null && getProCategoryId().length()>0) { 
			return updateProjectCategory(uF);
		}
		
		if (getStrOrg() != null && getStrOrg().length()>0) {
			return insertProjectCategory(uF);
		}
		
		return LOAD;
	}

	public String insertProjectCategory(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("INSERT INTO project_category_details(org_id,project_category,project_description,added_by,entry_date) VALUES (?,?,?,?, ?)");
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setString(2, getProCategory());
			pst.setString(3, getProDescription());
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+"Project Category saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
	
	public String viewProjectCategory(String strId, UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from project_category_details where project_category_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()) {
				setStrOrg(rs.getString("org_id"));
				setStrOrgName(CF.getOrgNameById(con, rs.getString("org_id")));
				setProCategory(rs.getString("project_category"));
				setProDescription(rs.getString("project_description"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}
	
	

	public String updateProjectCategory(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update project_category_details set project_category=?,project_description=?,updated_by=?,update_date=? where project_category_id=?");
			pst.setString(1, getProCategory());
			pst.setString(2, getProDescription());
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(getProCategoryId()));
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Project Category updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String deleteProjectCategory(String strId,UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from project_category_details where project_category_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Project Category deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
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

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrOrgName() {
		return strOrgName;
	}

	public void setStrOrgName(String strOrgName) {
		this.strOrgName = strOrgName;
	}

	public String getProCategoryId() {
		return proCategoryId;
	}

	public void setProCategoryId(String proCategoryId) {
		this.proCategoryId = proCategoryId;
	}

	public String getProCategory() {
		return proCategory;
	}

	public void setProCategory(String proCategory) {
		this.proCategory = proCategory;
	}

	public String getProDescription() {
		return proDescription;
	}

	public void setProDescription(String proDescription) {
		this.proDescription = proDescription;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}
