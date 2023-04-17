package com.konnect.jpms.recruitment;

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

public class GetHiringFilters1 extends ActionSupport implements
		ServletRequestAware {

	String strOrg;
	String type;
	
	List<FillEmployee> empList1;
	List<FillWLocation> workList1;
	List<FillDepartment> departmentList1;
	List<FillLevel> levelList1;
	List<FillDesig> designationList1;
	
	UtilityFunctions uF = new UtilityFunctions();
	
	private static final long serialVersionUID = 1L;

	public String execute() {
//		System.out.println("strOrg ===> "+strOrg);
//		System.out.println("type ===> "+ type);
//		if(strOrg != null && type != null && type.equals("org")){
//			if(!strOrg.equals("0")){
//				workList1 = new FillWLocation(request).fillWLocation(strOrg);
//				departmentList1 = new FillDepartment(request).fillDepartment(uF.parseToInt(strOrg));
//				levelList1 = new FillLevel(request).fillLevel(uF.parseToInt(strOrg));
//				designationList1 = new FillDesig(request).fillDesig(uF.parseToInt(strOrg));
//			}else{
//				levelList1 = new FillLevel(request).fillLevel();
//				workList1 = new FillWLocation(request).fillWLocation();
//				departmentList1 = new FillDepartment(request).fillDepartment();
//				designationList1 = new FillDesig(request).fillDesig();
//			}
//		} else if(strOrg != null && type != null && type.equals("wloc")){
//			if(!strOrg.equals("0") && !strOrg.equals("")){
//				departmentList1 = new FillDepartment(request).fillDepartment(uF.parseToInt(strOrg));
//			}else{
//				departmentList1 = new FillDepartment(request).fillDepartment();
//			}
//			
//		} else if(strOrg != null && type != null && type.equals("level")){
//			if(!strOrg.equals("0") && !strOrg.equals("")){
//				designationList1 = new FillDesig(request).fillDesigFromLevel(strOrg);
//			}else{
//				designationList1 = new FillDesig(request).fillDesig();
//			}
//		}
		
		workList1 = new FillWLocation(request).fillWLocation(getStrOrg());
		levelList1 = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		departmentList1 = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
		
		String strLevels = getOrgwiseLevelId(uF, getStrOrg());
		designationList1 = new FillDesig(request).fillDesigFromLevel(strLevels);
		
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

	public List<FillEmployee> getEmpList1() {
		return empList1;
	}

	public void setEmpList1(List<FillEmployee> empList1) {
		this.empList1 = empList1;
	}

	public List<FillWLocation> getWorkList1() {
		return workList1;
	}

	public void setWorkList1(List<FillWLocation> workList1) {
		this.workList1 = workList1;
	}

	public List<FillDepartment> getDepartmentList1() {
		return departmentList1;
	}

	public void setDepartmentList1(List<FillDepartment> departmentList1) {
		this.departmentList1 = departmentList1;
	}

	public List<FillLevel> getLevelList1() {
		return levelList1;
	}

	public void setLevelList1(List<FillLevel> levelList1) {
		this.levelList1 = levelList1;
	}

	public List<FillDesig> getDesignationList1() {
		return designationList1;
	}

	public void setDesignationList1(List<FillDesig> designationList1) {
		this.designationList1 = designationList1;
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
