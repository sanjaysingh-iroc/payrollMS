package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrgwiseLocationDepartLevelDesigGrade extends ActionSupport implements ServletRequestAware{

	
	List<FillWLocation> workList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillDesig> desigList;
	List<FillGrade> gradeList;
	
	String type;
	String strOrgId;
	String page;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
	
		UtilityFunctions uF = new UtilityFunctions();
		
//		workList = new FillWLocation(request).fillWLocation(strOrgId);
//		levelList = new FillLevel(request).fillLevel(uF.parseToInt(strOrgId));
//		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(strOrgId));
//		
//		String strLevels = getOrgwiseLevelId(uF, strOrgId);
//		desigList = new FillDesig(request).fillDesigFromLevel(strLevels);
//		String strDesigs = getOrgwiseDesigId(uF, strLevels);
//		gradeList = new FillGrade(request).fillGradeFromMultipleDesignation(strDesigs);
		
		workList = new FillWLocation(request).fillWLocation(getStrOrgId());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrgId()));
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrgId()));
		
		String strLevels = getOrgwiseLevelId(uF, getStrOrgId());
		desigList = new FillDesig(request).fillDesigFromLevel(strLevels);
		String strDesigs = getOrgwiseDesigId(uF, strLevels);
		gradeList = new FillGrade(request).fillGradeFromMultipleDesignation(strDesigs);
		
		return SUCCESS;
		
	}

	private String getOrgwiseDesigId(UtilityFunctions uF, String strLevels) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbDesigIds = null;
		
		try {

			con = db.makeConnection(con);
			if(strLevels != null && !strLevels.equals("")) {
				pst = con.prepareStatement("select designation_id from designation_details where level_id in ("+strLevels+")");
			} else {
				pst = con.prepareStatement("select designation_id from designation_details");
			}
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(sbDesigIds == null) {
					sbDesigIds = new StringBuilder();
					sbDesigIds.append(rs.getString("designation_id"));
				} else {
					sbDesigIds.append("," + rs.getString("designation_id"));
				}
			}
            rs.close();
            pst.close();
			if(sbDesigIds != null) {
//				System.out.println("sbDesigIds ===> " + sbDesigIds.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return sbDesigIds.toString();
	}

	private String getOrgwiseLevelId(UtilityFunctions uF, String strOrgId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		StringBuilder sbLevelIds = null;
		
		try {

			con = db.makeConnection(con);
			
			if(uF.parseToInt(strOrgId)> 0) {
				pst = con.prepareStatement("select level_id from level_details where org_id = ?");
				pst.setInt(1, uF.parseToInt(strOrgId));
			} else {
				pst = con.prepareStatement("select level_id from level_details");
			}
			rs = pst.executeQuery();			
			while(rs.next()){
				if(sbLevelIds == null) {
					sbLevelIds = new StringBuilder();
					sbLevelIds.append(rs.getString("level_id"));
				} else {
					sbLevelIds.append("," + rs.getString("level_id"));
				}
			}
			if(sbLevelIds == null) {
				sbLevelIds = new StringBuilder();
			}
            rs.close();
            pst.close();
			if(sbLevelIds != null) {
//				System.out.println("sbLevelIds ===> " + sbLevelIds.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return sbLevelIds.toString();
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	
	public List<FillWLocation> getWorkList() {
		return workList;
	}
	
	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}
	
	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}
	
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	
	public List<FillDesig> getDesigList() {
		return desigList;
	}
	
	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}
	
	public List<FillGrade> getGradeList() {
		return gradeList;
	}
	
	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getStrOrgId() {
		return strOrgId;
	}
	
	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	
}
