package com.konnect.jpms.training;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLearningLocDeptLvlAndDesig extends ActionSupport implements
		ServletRequestAware,IStatements {

	private static final long serialVersionUID = 1L;
	
	private String lPlanID;
	private boolean boolPublished;
	
	List<String> strOrgList = new ArrayList<String>();;
	
	private List<FillDesig> desigList;
	
	private List<FillOrganisation> organisationList;
	private String f_org;
	
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF = null;
	
	private List<String> orgID = new ArrayList<String>();
	private List<String> locID = new ArrayList<String>();
	private List<String> departID = new ArrayList<String>();
	private List<String> levelID = new ArrayList<String>();
	private List<String> desigID = new ArrayList<String>();
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("getF_org ===> " + getF_org());
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		desigList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));
		
//		System.out.println("strOrgList ===> " + strOrgList);
		getSelectedOrgLocDeptLevelDesig();
		return SUCCESS;
	}


	private void getSelectedOrgLocDeptLevelDesig() {

		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("step ===> "+step);
		
		try {
			con = db.makeConnection(con);
				pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id = ?");
				pst.setInt(1, uF.parseToInt(getlPlanID()));
				rst = pst.executeQuery();
				while (rst.next()) {
					
					if(rst.getString("org_id") != null && !rst.getString("org_id").equals("")){
						List<String> orgValue = new ArrayList<String>();
						orgValue = Arrays.asList(rst.getString("org_id").split(","));
						for (int k = 0; k < orgValue.size(); k++) {
							if (orgValue.get(k) != null && !orgValue.get(k).equals("")) {
								orgID.add(orgValue.get(k).trim());
							}
						}
					}
					if (rst.getString("location_id") != null && !rst.getString("location_id").equals("")) {
						List<String> locationValue = new ArrayList<String>();
						locationValue = Arrays.asList(rst.getString("location_id").split(","));
						for (int k = 0; k < locationValue.size(); k++) {
							if (locationValue.get(k) != null && !locationValue.get(k).equals("")) {
								locID.add(locationValue.get(k).trim());
							}
						}
					}
					if (rst.getString("level_id") != null && !rst.getString("level_id").equals("")) {
						List<String> levelValue = new ArrayList<String>();
						levelValue = Arrays.asList(rst.getString("level_id").split(","));
						for (int k = 0; k < levelValue.size(); k++) {
							if (levelValue.get(k) != null && !levelValue.get(k).equals("")) {
								levelID.add(levelValue.get(k).trim());
							}
						}
					}
					if (rst.getString("desig_id") != null && !rst.getString("desig_id").equals("")) {
						List<String> desigValue = new ArrayList<String>();
						desigValue = Arrays.asList(rst.getString("desig_id").split(","));
						for (int k = 0; k < desigValue.size(); k++) {
							if (desigValue.get(k) != null && !desigValue.get(k).equals("")) {
								desigID.add(desigValue.get(k).trim());
							}
						}
					}
					if (rst.getString("depart_id") != null && !rst.getString("depart_id").equals("")) {
						List<String> gradeValue = new ArrayList<String>();
						gradeValue = Arrays.asList(rst.getString("depart_id").split(","));
						for (int k = 0; k < gradeValue.size(); k++) {
							if (gradeValue.get(k) != null && !gradeValue.get(k).equals("")) {
								departID.add(gradeValue.get(k).trim());
							}
						}
					}
				}
				rst.close();
				pst.close();
				
//				System.out.println("orgID ===>>>> " + orgID);
//				System.out.println("locID ===>>>> " + locID);
//				System.out.println("departID ===>>>> " + departID);
//				System.out.println("levelID ===>>>> " + levelID);
//				System.out.println("desigID ===>>>> " + desigID);
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<String> getStrOrgList() {
		return strOrgList;
	}

	public void setStrOrgList(List<String> strOrgList) {
		this.strOrgList = strOrgList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getlPlanID() {
		return lPlanID;
	}

	public void setlPlanID(String lPlanID) {
		this.lPlanID = lPlanID;
	}

	public boolean isBoolPublished() {
		return boolPublished;
	}

	public void setBoolPublished(boolean boolPublished) {
		this.boolPublished = boolPublished;
	}

	public List<String> getOrgID() {
		return orgID;
	}

	public void setOrgID(List<String> orgID) {
		this.orgID = orgID;
	}

	public List<String> getLocID() {
		return locID;
	}

	public void setLocID(List<String> locID) {
		this.locID = locID;
	}

	public List<String> getDepartID() {
		return departID;
	}

	public void setDepartID(List<String> departID) {
		this.departID = departID;
	}

	public List<String> getLevelID() {
		return levelID;
	}

	public void setLevelID(List<String> levelID) {
		this.levelID = levelID;
	}

	public List<String> getDesigID() {
		return desigID;
	}

	public void setDesigID(List<String> desigID) {
		this.desigID = desigID;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	
}
