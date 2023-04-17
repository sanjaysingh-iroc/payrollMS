package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddWlocationType extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {

		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		 
		
		if (operation!=null && operation.equals("D")) {
			return deleteWlocationType(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewWlocationType(strId);
		} 
		if (getWlocationTypeId()!=null && getWlocationTypeId().length()>0) { 
			return updateWlocationType();
		}
		if(getWlocationTypeCode()!=null && getWlocationTypeCode().length()>0){
			return insertWlocationType();
		}
		
		setOrgId(request.getParameter("param"));
		
		
		return LOAD;
	}

	public String loadValidateWlocationType() {
		return LOAD;
	}

	public String insertWlocationType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertWlocationType);
			pst.setString(1, getWlocationTypeCode());
			pst.setString(2, getWlocationTypeName());
			pst.setString(3, uF.showData(getWlocationTypeDesc(),""));
			pst.setInt(4, uF.parseToInt(getOrgId()));
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
	
	public String updateWlocationType() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String updateWlocationType = "UPDATE work_location_type SET wlocation_type_code=?, wlocation_type_name=?, wlocation_type_description=? WHERE wlocation_type_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateWlocationType);
			pst.setString(1, getWlocationTypeCode());
			pst.setString(2, getWlocationTypeName());
			pst.setString(3, getWlocationTypeDesc());
			pst.setInt(4, uF.parseToInt(getWlocationTypeId()));
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
	
	public String deleteWlocationType(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteWlocationType);
			pst.setInt(1, uF.parseToInt(strId));
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

	
	public String viewWlocationType(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from work_location_type where wlocation_type_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				setWlocationTypeCode(rs.getString("wlocation_type_code"));
				setWlocationTypeDesc(rs.getString("wlocation_type_description"));
				setWlocationTypeName(rs.getString("wlocation_type_name"));
				setWlocationTypeId(rs.getString("wlocation_type_id"));
				setOrgId(rs.getString("org_id"));
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
		return UPDATE;

	}
	
	String orgId;
	String wlocationTypeId;
	String wlocationTypeCode;
	String wlocationTypeName;
	String wlocationTypeDesc;
	

//	public void validate() {
//
//		
//		System.out.println("222=="+220);
//		
////		if (getWlocationTypeId() != null && getWlocationTypeId().length() == 0) {
////			addFieldError("wlocationTypeId", "WlocationType ID is required");
////			
////			System.out.println("222=="+221);
////		}
//		if (getWlocationTypeName() != null && getWlocationTypeName().length() == 0) {
//			addFieldError("wlocationTypeName", "WlocationType Name is required");
//			
//			System.out.println("222=="+222);
//		}
//		if (getWlocationTypeCode() != null && getWlocationTypeCode().length() == 0) {
//			addFieldError("wlocationTypeCode", "WlocationType Code is required");
//			
//			System.out.println("222=="+223);
//		}
////		loadValidateWlocationType();
//
//	} 

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getWlocationTypeId() {
		return wlocationTypeId;
	}

	public void setWlocationTypeId(String wlocationTypeId) {
		this.wlocationTypeId = wlocationTypeId;
	}

	public String getWlocationTypeName() {
		return wlocationTypeName;
	}

	public void setWlocationTypeName(String wlocationTypeName) {
		this.wlocationTypeName = wlocationTypeName;
	}

	public String getWlocationTypeDesc() {
		return wlocationTypeDesc;
	}

	public void setWlocationTypeDesc(String wlocationTypeDesc) {
		this.wlocationTypeDesc = wlocationTypeDesc;
	}

	public String getWlocationTypeCode() {
		return wlocationTypeCode;
	}

	public void setWlocationTypeCode(String wlocationTypeCode) {
		this.wlocationTypeCode = wlocationTypeCode;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
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