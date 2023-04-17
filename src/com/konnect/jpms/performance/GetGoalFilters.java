package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetGoalFilters extends ActionSupport implements
		ServletRequestAware {

	private String strOrg;
	private String type;
	
	private List<FillEmployee> empList;
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillDesig> desigList;
	UtilityFunctions uF = new UtilityFunctions();
	
	private static final long serialVersionUID = 1L;

	public String execute() {
//		System.out.println("strOrg ===> "+strOrg);
//		System.out.println("type ===> "+ type);
//		if(strOrg != null && type != null && type.equals("org")){
//			if(!strOrg.equals("0")){
//				workList = new FillWLocation(request).fillWLocation(strOrg);
//				departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(strOrg));
//				levelList = new FillLevel(request).fillLevel(uF.parseToInt(strOrg));
//				desigList = new FillDesig(request).fillDesig(uF.parseToInt(strOrg));
//			}else{
//				levelList = new FillLevel(request).fillLevel();
//				workList = new FillWLocation(request).fillWLocation();
//				departmentList = new FillDepartment(request).fillDepartment();
//				desigList = new FillDesig(request).fillDesig(); 
//			}
//		} else if(strOrg != null && type != null && type.equals("wloc")){
//			if(!strOrg.equals("0") && !strOrg.equals("")){
//				departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(strOrg));
//			}else{
//				departmentList = new FillDepartment(request).fillDepartment();
//			}
//			
//		} else if(strOrg != null && type != null && type.equals("level")){
//			if(!strOrg.equals("0") && !strOrg.equals("")){
//				desigList = new FillDesig(request).fillDesigFromLevel(strOrg);
//			}else{
//				desigList = new FillDesig(request).fillDesig();
//			}
//		}
		
		workList = new FillWLocation(request).fillWLocation(getStrOrg());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
		
		String strLevels = getOrgwiseLevelId(uF, getStrOrg());
		desigList = new FillDesig(request).fillDesigFromLevel(strLevels);
		
		return SUCCESS;

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

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
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

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
