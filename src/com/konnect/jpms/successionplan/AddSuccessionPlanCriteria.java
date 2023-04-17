package com.konnect.jpms.successionplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillTotExpYearsAndMonths;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillEducation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddSuccessionPlanCriteria extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strSessionEmpId = null;
	
	List<FillTotExpYearsAndMonths> totExpYearList;
	List<FillTotExpYearsAndMonths> totExpMonthList;
	
	List<FillTotExpYearsAndMonths> presentOrgExpYearList;
	List<FillTotExpYearsAndMonths> presentOrgExpMonthList;
	
	List<FillAttribute> potentialAttributeList;
	List<FillAttribute> performanceAttributeList;
	List<FillSkills> skillslist;
	List<FillEducation> educationList;
	
	List<FillWLocation> locationList;
	List<FillLevel> levelList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	
	String operation;
	String criteriaId;
	String desigName;
	
	List<String> potentialAttribID = new ArrayList<String>();
	List<String> performanceAttribID = new ArrayList<String>();
	List<String> skillsID = new ArrayList<String>();
	List<String> locationID = new ArrayList<String>();
//	List<String> levelID = new ArrayList<String>();
	List<String> departID = new ArrayList<String>();
	List<String> serviceID = new ArrayList<String>();
	
	CommonFunctions CF;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {

		request.setAttribute(PAGE, "/jsp/seccussionplan/AddSuccessionPlanCriteria.jsp");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
//		String operation = request.getParameter("operation");
		String submit = request.getParameter("submit");
		
		totExpYearList = new FillTotExpYearsAndMonths().fillTotExpYears();
		totExpMonthList = new FillTotExpYearsAndMonths().fillTotExpMonths();
		
		presentOrgExpYearList = new FillTotExpYearsAndMonths().fillTotExpYears();
		presentOrgExpMonthList = new FillTotExpYearsAndMonths().fillTotExpMonths();
		
		String levels = getOrgLevels((String)session.getAttribute(ORGID));
		potentialAttributeList = new FillAttribute(request).fillElementAttributeElementwise(levels, "1");
		performanceAttributeList = new FillAttribute(request).fillElementAttributeElementwise(levels, "2");
		
		skillslist = new FillSkills(request).fillSkills();
		educationList=new FillEducation(request).fillEducationWithId();
		
		departmentList = new FillDepartment(request).fillDepartment();
		levelList = new FillLevel(request).fillLevel();
		locationList = new FillWLocation(request).fillWLocation();
		serviceList = new FillServices(request).fillServices();
		
		System.out.println("operation ===>> " + operation);
		System.out.println("submit ===>> " + submit);
		System.out.println("criteriaId ===>> " + criteriaId);
		ViewDesigName();
		
		if (operation!=null && operation.equals("A") && submit != null) {
			
			return insertSuccessionPlanCriteria();
			
		} else if (operation!=null && operation.equals("D")) {

			return deleteSuccessionPlanCriteria(criteriaId);
			
		} else if (operation!=null && operation.equals("E")) {
			if(submit != null) { 
				return updateSuccessionPlanCriteria(criteriaId);
			}else {
			 return viewSuccessionPlanCriteria(criteriaId);
			}
		}
		
		
		
		return LOAD;
		
	}
	
	
	private void ViewDesigName() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmDesigName = CF.getDesigMap(con);
			setDesigName(hmDesigName.get(getDesigId()));
//			session.setAttribute(MESSAGE, SUCCESSM+hmDesigName.get(getDesigId()) +"'s setting updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	public String getOrgLevels(String orgId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		StringBuilder sbLevelids = new StringBuilder();
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select level_id from level_details where org_id = ?");
			pst.setInt(1, uF.parseToInt(orgId));
			rs = pst.executeQuery();
			int cnt=0;
			while(rs.next()){
				if(cnt==0){	
					sbLevelids.append(rs.getString("level_id"));
					cnt++;
				}else{
					sbLevelids.append(","+rs.getString("level_id"));
				}
			}	
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return sbLevelids.toString();
	}


	private String deleteSuccessionPlanCriteria(String criteriaId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//			System.out.println("updateSuccessionPlanCriteria ===> " + criteriaId);
			//pst = con.prepareStatement(insertDesig);
			pst = con.prepareStatement("delete from successionplan_criteria_details where successionplan_criteria_id =?");
			pst.setInt(1, uF.parseToInt(criteriaId));
			pst.executeUpdate();	
			pst.close();
			
			Map<String, String> hmDesigName = CF.getDesigMap(con);
			session.setAttribute(MESSAGE, SUCCESSM+hmDesigName.get(getDesigId()) +"'s setting deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	private String updateSuccessionPlanCriteria(String criteriaId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//			System.out.println("updateSuccessionPlanCriteria ===> " + criteriaId);
			//pst = con.prepareStatement(insertDesig);
			String eduWeight = getEducationWeightage(con, getQualification());
			pst = con.prepareStatement("update successionplan_criteria_details set designation_id=?,qualification_id=?,qualification_weight=?," +
					"total_exp=?,precent_org_exp=?,potential_attribute=?,potential_threshold=?,performance_attribute=?,performance_threshold=?," +
					"skills=?,skills_threshold=?,department_ids=?,sbu_ids=?,geography_ids=?,levels_below=?,updated_by=?,update_date=? " +
					"where successionplan_criteria_id =?");
			pst.setInt(1, uF.parseToInt(getDesigId()));
			pst.setInt(2, uF.parseToInt(getQualification()));
			pst.setInt(3, uF.parseToInt(eduWeight));
			pst.setString(4, uF.showData(getTotExpYear()+":"+getTotExpMonth(), ""));
			pst.setString(5, uF.showData(getPresentOrgExpMonth()+":"+getPresentOrgExpYear(), ""));
			pst.setString(6, uF.showData(getAppendData(getPotentialAttribute()), ""));
			pst.setInt(7, uF.parseToInt(getPotentialAttThreshhold()));
			pst.setString(8, uF.showData(getAppendData(getPerformanceAttribute()), ""));
			pst.setInt(9, uF.parseToInt(getPerformanceAttThreshhold()));
			pst.setString(10, uF.showData(getAppendData(getSkills()), ""));
			pst.setInt(11, uF.parseToInt(getSkillThreshhold()));
			pst.setString(12, uF.showData(getAppendData(getDepartment()), ""));
			pst.setString(13, uF.showData(getAppendData(getSbu()), ""));
			pst.setString(14, uF.showData(getAppendData(getGeography()), ""));
			pst.setInt(15, uF.parseToInt(getLevelBelow()));
			pst.setInt(16, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(17, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.setInt(18, uF.parseToInt(criteriaId));
			pst.executeUpdate();	
			pst.close();
			
			Map<String, String> hmDesigName = CF.getDesigMap(con);
			session.setAttribute(MESSAGE, SUCCESSM+hmDesigName.get(getDesigId()) +"'s setting updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	private String viewSuccessionPlanCriteria(String criteriaId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//			System.out.println("insertSuccessionPLanCriteria ===> " + criteriaId);
			//pst = con.prepareStatement(insertDesig);
			pst = con.prepareStatement("select * from successionplan_criteria_details where successionplan_criteria_id =? ");
//			(designation_id,qualification_id,qualification_weight,total_exp," +
//					"precent_org_exp,potential_attribute,potential_threshold,performance_attribute,performance_threshold,skills,skills_threshold," +
//					"department_ids,sbu_ids,geography_ids,level_ids,added_by,entry_date) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			pst.setInt(1, uF.parseToInt(criteriaId));
			rs = pst.executeQuery();
			while (rs.next()) {
				setQualification(rs.getString("qualification_id"));
				setEduWeightage(rs.getString("qualification_weight"));
				setLevelBelow(rs.getString("levels_below"));
				if(rs.getString("total_exp").equals("0:0")) {
					setTotExpCheckboxStatus("");
				}else {
					setTotExpCheckboxStatus("checked");
				}
				if(rs.getString("precent_org_exp").equals("0:0")) {
					setPresentOrgExpCheckboxStatus("");
				}else {
					setPresentOrgExpCheckboxStatus("checked");
				}
				String totExp[] = rs.getString("total_exp").split(":");
//				System.out.println("totExp ===> " + totExp.length);
				setTotExpYear(totExp[0]);
				if(totExp.length > 1) {
					setTotExpMonth(totExp[1]);
				}
				String presentOrgExp[] = rs.getString("precent_org_exp").split(":");
				setPresentOrgExpYear(presentOrgExp[0]);
				if(totExp.length > 1) {
					setPresentOrgExpMonth(presentOrgExp[1]);
				}
				
				setPotentialAttThreshhold(rs.getString("potential_threshold"));
				setPerformanceAttThreshhold(rs.getString("performance_threshold"));
				setSkillThreshhold(rs.getString("skills_threshold"));
				
				if (rs.getString("potential_attribute") == null || rs.getString("potential_attribute").equals("")){
				} else {
					List<String> attributeValue1 = new ArrayList<String>();
					attributeValue1 = Arrays.asList(rs.getString("potential_attribute").split(","));
					for (int k = 0; k < attributeValue1.size(); k++) {
						if (attributeValue1.get(k) != null && !attributeValue1.get(k).equals("")) {
							potentialAttribID.add(attributeValue1.get(k).trim());
						}
					}
				}
				
				if (rs.getString("performance_attribute") == null || rs.getString("performance_attribute").equals("")){
				} else {
					List<String> attributeValue1 = new ArrayList<String>();
					attributeValue1 = Arrays.asList(rs.getString("performance_attribute").split(","));
					for (int k = 0; k < attributeValue1.size(); k++) {
						if (attributeValue1.get(k) != null && !attributeValue1.get(k).equals("")) {
							performanceAttribID.add(attributeValue1.get(k).trim());
						}
					}
				}
				
				if (rs.getString("geography_ids") == null || rs.getString("geography_ids").equals("")) {
//					locationList=null;
				} else {
					List<String> locationValue = new ArrayList<String>();
					locationValue = Arrays.asList(rs.getString("geography_ids").split(","));
					for (int k = 0; k < locationValue.size(); k++) {
						if (locationValue.get(k) != null && !locationValue.get(k).equals("")) {
							locationID.add(locationValue.get(k).trim());
						}
					}
				}
				
//				if (rs.getString("level_ids") == null || rs.getString("level_ids").equals("")) {
//				} else {
//					List<String> levelValue = new ArrayList<String>();
//					levelValue = Arrays.asList(rs.getString("level_ids").split(","));
//					for (int k = 0; k < levelValue.size(); k++) {
//						if (levelValue.get(k) != null && !levelValue.get(k).equals("")) {
//							levelID.add(levelValue.get(k).trim());
//						}
//					}
//				}
				
				if (rs.getString("skills") == null || rs.getString("skills").equals("")) {
//					locationList=null;
				} else {
					List<String> skillValue = new ArrayList<String>();
					skillValue = Arrays.asList(rs.getString("skills").split(","));
					for (int k = 0; k < skillValue.size(); k++) {
						if(skillValue.get(k) != null && !skillValue.get(k).equals("")){
							skillsID.add(skillValue.get(k).trim());
						}
					}
				}
				
				if (rs.getString("department_ids") == null || rs.getString("department_ids").equals("")) {
//					locationList=null;
				} else {
					List<String> departValue = new ArrayList<String>();
					departValue = Arrays.asList(rs.getString("department_ids").split(","));
					for (int k = 0; k < departValue.size(); k++) {
						if(departValue.get(k) != null && !departValue.get(k).equals("")){
							departID.add(departValue.get(k).trim());
						}
					}
				}
				
				if (rs.getString("sbu_ids") == null || rs.getString("sbu_ids").equals("")) {
//					locationList=null;
				} else {
					List<String> serviceValue = new ArrayList<String>();
					serviceValue = Arrays.asList(rs.getString("sbu_ids").split(","));
					for (int k = 0; k < serviceValue.size(); k++) {
						if(serviceValue.get(k) != null && !serviceValue.get(k).equals("")){
							serviceID.add(serviceValue.get(k).trim());
						}
					}
				}
			}	
			rs.close();
			pst.close();
			
			/*pst.setInt(1, uF.parseToInt(getDesigId()));
			pst.setInt(2, uF.parseToInt(getQualification()));
			pst.setInt(3, uF.parseToInt(getEduWeightage()));
			pst.setString(4, uF.showData(getTotExpYear()+"."+getTotExpMonth(), ""));
			pst.setString(5, uF.showData(getPresentOrgExpMonth()+":"+getPresentOrgExpYear(), ""));
			pst.setString(6, uF.showData(getAppendData(getPotentialAttribute()), ""));
			pst.setInt(7, uF.parseToInt(getPotentialAttThreshhold()));
			pst.setString(8, uF.showData(getAppendData(getPerformanceAttribute()), ""));
			pst.setInt(9, uF.parseToInt(getPerformanceAttThreshhold()));
			pst.setString(10, uF.showData(getAppendData(getSkills()), ""));
			pst.setInt(11, uF.parseToInt(getSkillThreshhold()));
			pst.setString(12, uF.showData(getAppendData(getDepartment()), ""));
			pst.setString(13, uF.showData(getAppendData(getSbu()), ""));
			pst.setString(14, uF.showData(getAppendData(getGeography()), ""));
			pst.setString(15, uF.showData(getAppendData(getLevelBelow()), ""));
			pst.setInt(16, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(17, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();*/
			
//			session.setAttribute(MESSAGE, SUCCESSM+getDesigCode()+" saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}

	private String insertSuccessionPlanCriteria() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
//			System.out.println("insertSuccessionPLanCriteria ===> ");
			//pst = con.prepareStatement(insertDesig);
			String eduWeight = getEducationWeightage(con, getQualification());
			pst = con.prepareStatement("INSERT INTO successionplan_criteria_details(designation_id,qualification_id,qualification_weight,total_exp," +
					"precent_org_exp,potential_attribute,potential_threshold,performance_attribute,performance_threshold,skills,skills_threshold," +
					"department_ids,sbu_ids,geography_ids,levels_below,added_by,entry_date) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			pst.setInt(1, uF.parseToInt(getDesigId()));
			pst.setInt(2, uF.parseToInt(getQualification()));
			pst.setInt(3, uF.parseToInt(eduWeight));
			pst.setString(4, uF.showData(getTotExpYear()+":"+getTotExpMonth(), ""));
			pst.setString(5, uF.showData(getPresentOrgExpMonth()+":"+getPresentOrgExpYear(), ""));
			pst.setString(6, uF.showData(getAppendData(getPotentialAttribute()), ""));
			pst.setInt(7, uF.parseToInt(getPotentialAttThreshhold()));
			pst.setString(8, uF.showData(getAppendData(getPerformanceAttribute()), ""));
			pst.setInt(9, uF.parseToInt(getPerformanceAttThreshhold()));
			pst.setString(10, uF.showData(getAppendData(getSkills()), ""));
			pst.setInt(11, uF.parseToInt(getSkillThreshhold()));
			pst.setString(12, uF.showData(getAppendData(getDepartment()), ""));
			pst.setString(13, uF.showData(getAppendData(getSbu()), ""));
			pst.setString(14, uF.showData(getAppendData(getGeography()), ""));
			pst.setInt(15, uF.parseToInt(getLevelBelow()));
			pst.setInt(16, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(17, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();	
			pst.close();
			
			Map<String, String> hmDesigName = CF.getDesigMap(con);
			session.setAttribute(MESSAGE, SUCCESSM+hmDesigName.get(getDesigId()) +"'s setting saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	
	
private String getEducationWeightage(Connection con, String qualiID) {

	PreparedStatement pst = null;
	ResultSet rs=null;
	UtilityFunctions uF = new UtilityFunctions();
	String weightage = "";
	try {
		pst = con.prepareStatement("select * from educational_details where edu_id =? ");
		pst.setInt(1, uF.parseToInt(qualiID));
		rs = pst.executeQuery();
		while (rs.next()) {
			weightage = rs.getString("weightage");
		}	
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	return weightage;
}


public String getAppendData(String strIds) {
		
		StringBuilder sb = new StringBuilder();
		if(strIds != null && !strIds.equals("")) {
			
			List<String> idsList = Arrays.asList(strIds.split(","));
			if (idsList != null && !idsList.isEmpty()) {
				
				for (int i = 0; i < idsList.size(); i++) {
					if (i == 0) {
						sb.append("," + idsList.get(i).trim() + ",");
					} else {
						sb.append(idsList.get(i).trim() + ",");
					}
				}
			} else {
				return null;
			}
		}
		return sb.toString();
	}
	
	
//	private void getAttributeDetails() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from attribute_details");
//			rs = pst.executeQuery();
//			List<List<String>> outerList=new ArrayList<List<String>>();
//			while(rs.next()){
//				List<String> innerList=new ArrayList<String>();
//				innerList.add(rs.getString("attribute_id"));
//				innerList.add(rs.getString("attribute_name"));
//				innerList.add(rs.getString("description"));
//				innerList.add(rs.getString("weightage"));
//				outerList.add(innerList);
//			}	
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("attributeList",outerList);
//			//System.out.println("attributeList=====>"+outerList);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//	}

	String orgId;
	public String loadValidateDesig() {
		
		request.setAttribute(PAGE, PAddDesignation);
		
		levelList = new FillLevel(request).fillLevel();
		
		if(levelList.size()!=0) {
			request.setAttribute("levelList", levelList);
			int levelId, i=0;
			String levelName;
			
			StringBuilder sbLevelList = new StringBuilder();
			sbLevelList.append("{");
			for(i=0; i<levelList.size()-1;i++ ) {
	    		levelId = Integer.parseInt((levelList.get(i)).getLevelId());
	    		levelName = levelList.get(i).getLevelCodeName();
	    		sbLevelList.append("\""+ levelId+"\":\""+levelName+"\",");
			}
			levelId = Integer.parseInt((levelList.get(i)).getLevelId());
			levelName = levelList.get(i).getLevelCodeName();
			sbLevelList.append("\""+ levelId+"\":\""+levelName+"\"");	
			sbLevelList.append("}");
			request.setAttribute("sbLevelList", sbLevelList.toString());
		}
		
		return LOAD;
	}

//	public String insertDesig() {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs=null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			//pst = con.prepareStatement(insertDesig);
//			pst = con.prepareStatement("INSERT INTO designation_details (designation_code, designation_name, " +
//					"designation_description, level_id,attribute_ids,ideal_candidate,profile) VALUES (?,?,?,?,?,?,?)");
//			pst.setString(1, getDesigCode());
//			pst.setString(2, getDesigName());
//			pst.setString(3, uF.showData(getDesigDesc(),""));
//			pst.setInt(4, uF.parseToInt(getDesiglevel()));
//			pst.setString(5, getAttributeid());
//			pst.setString(6, getIdealcandidate());
//			pst.setString(7, getProfile());
//			pst.execute();
//			
//			pst = con.prepareStatement("select max(designation_id)as desigid from designation_details");
//			rs=pst.executeQuery();
//			String desigid=null;
//			while(rs.next()){
//				desigid=rs.getString("desigid");
//			}
//			
//			pst = con.prepareStatement("select org_id from level_details where level_id=?");
//			pst.setInt(1,uF.parseToInt(getDesiglevel().trim()));
//			rs=pst.executeQuery();
//			String orgid=null;
//			while(rs.next()){
//				orgid=rs.getString("org_id");
//			}
// 
//			if(getAttributeid()!=null && !getAttributeid().equals("")){
//				List<String> attList=Arrays.asList(getAttributeid().split(","));
//					for(int i=0;attList!=null && !attList.isEmpty() && i<attList.size();i++){
//						String id=attList.get(i).trim();
//						String desig_value=null;
//						String value_type="";
//						boolean flag=false;
//						if(id.equals("1")){
//							desig_value=getEducation();
//							value_type=",";
//							flag=true;
//						}else if(id.equals("2")){
//							desig_value=getTotalexpYear()+"."+getTotalexpMonth();
//							value_type=",";
//							flag=true;
//						}else if(id.equals("3")){
//							desig_value=getRelevantYear()+"."+getRelevantMonth();
//							flag=true;
//						}else if(id.equals("4")){
//							desig_value=getExpusYear()+"."+getExpusMonth();
//							flag=true;
//						}else if(id.equals("5")){
//							desig_value=getSkill();
//							value_type=",";
//							flag=true;
//						}else if(id.equals("6")){
//							desig_value=getStrGender();
//							flag=true;
//						}
//						
//						if(flag){
//							pst=con.prepareStatement("insert into desig_attribute(desig_id,desig_value,_type,value_type)values(?,?,?,?)");
//							pst.setInt(1, uF.parseToInt(desigid));
//							pst.setString(2, desig_value);
//							pst.setInt(3, uF.parseToInt(id));
//							pst.setString(4, value_type);
//							pst.execute();
//							
//							if(id.equals("5")){
//								List<String> skillList=Arrays.asList(getSkill().split(","));
//								for(int j=0;skillList!=null && !skillList.isEmpty() && j<skillList.size();j++){
//									String skill=skillList.get(j).trim();
//									pst = con.prepareStatement("select * from skills_details where upper(skill_name) like ?");
//									pst.setString(1, skill.toUpperCase());
//									rs=pst.executeQuery();
//									boolean flg=false;
//									while(rs.next()){
//										flg=true;
//									}
//									if(!flg){
//										pst=con.prepareStatement("insert into skills_details(skill_name,org_id)values(?,?)");
//										pst.setString(1, skill);
//										pst.setInt(2, uF.parseToInt(orgid));
//										pst.execute();										
//									}
//								}
//							}else if(id.equals("1")){
//								List<String> eduList=Arrays.asList(getEducation().split(","));
//								for(int j=0;eduList!=null && !eduList.isEmpty() && j<eduList.size();j++){
//									String education=eduList.get(j).trim();
//									pst = con.prepareStatement("select * from educational_details where upper(education_name) like ?");
//									pst.setString(1, education.toUpperCase());
//									rs=pst.executeQuery();
//									boolean flg=false;
//									while(rs.next()){
//										flg=true;
//									}
//									if(!flg){
//										pst=con.prepareStatement("insert into educational_details(education_name,org_id)values(?,?)");
//										pst.setString(1, education);
//										pst.setInt(2, uF.parseToInt(orgid));
//										pst.execute();										
//									}
//								}
//							}
//							
//						}
//					}
//			}
//			
//			session.setAttribute(MESSAGE, SUCCESSM+getDesigCode()+" saved successfully."+END);
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//
//	}
	
	
//	public String deleteDesig(String strId) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(deleteDesig);
//			pst.setInt(1, uF.parseToInt(strId));
//			pst.execute();
//			
//			pst = con.prepareStatement(deleteGrade1);
//			pst.setInt(1, uF.parseToInt(strId));
//			pst.execute();
//			
//			session.setAttribute(MESSAGE, SUCCESSM+"Deleted successfully."+END);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ERROR;
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//
//	}

// local fields ------------
	
	String desigId;
	String qualification;
	String eduWeightage;
	String totExpCheckboxStatus;
	String totExpYear;
	String totExpMonth;
	String presentOrgExpCheckboxStatus;
	String presentOrgExpYear;
	String presentOrgExpMonth;
	String potentialAttribute;
	String potentialAttThreshhold;
	String performanceAttribute;
	String performanceAttThreshhold;
	String skills;
	String skillThreshhold;
	String department;
	String sbu;
	String geography;
	String levelBelow;
	
	
	public String getDesigId() {
		return desigId;
	}

	public void setDesigId(String desigId) {
		this.desigId = desigId;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getEduWeightage() {
		return eduWeightage;
	}

	public void setEduWeightage(String eduWeightage) {
		this.eduWeightage = eduWeightage;
	}

	public String getTotExpYear() {
		return totExpYear;
	}

	public void setTotExpYear(String totExpYear) {
		this.totExpYear = totExpYear;
	}

	public String getTotExpMonth() {
		return totExpMonth;
	}

	public void setTotExpMonth(String totExpMonth) {
		this.totExpMonth = totExpMonth;
	}

	public String getTotExpCheckboxStatus() {
		return totExpCheckboxStatus;
	}

	public void setTotExpCheckboxStatus(String totExpCheckboxStatus) {
		this.totExpCheckboxStatus = totExpCheckboxStatus;
	}

	public String getPresentOrgExpCheckboxStatus() {
		return presentOrgExpCheckboxStatus;
	}

	public void setPresentOrgExpCheckboxStatus(String presentOrgExpCheckboxStatus) {
		this.presentOrgExpCheckboxStatus = presentOrgExpCheckboxStatus;
	}

	public String getPresentOrgExpYear() {
		return presentOrgExpYear;
	}

	public void setPresentOrgExpYear(String presentOrgExpYear) {
		this.presentOrgExpYear = presentOrgExpYear;
	}

	public String getPresentOrgExpMonth() {
		return presentOrgExpMonth;
	}

	public void setPresentOrgExpMonth(String presentOrgExpMonth) {
		this.presentOrgExpMonth = presentOrgExpMonth;
	}

	public String getPotentialAttribute() {
		return potentialAttribute;
	}

	public void setPotentialAttribute(String potentialAttribute) {
		this.potentialAttribute = potentialAttribute;
	}

	public String getPotentialAttThreshhold() {
		return potentialAttThreshhold;
	}

	public void setPotentialAttThreshhold(String potentialAttThreshhold) {
		this.potentialAttThreshhold = potentialAttThreshhold;
	}

	public String getPerformanceAttribute() {
		return performanceAttribute;
	}

	public void setPerformanceAttribute(String performanceAttribute) {
		this.performanceAttribute = performanceAttribute;
	}

	public String getPerformanceAttThreshhold() {
		return performanceAttThreshhold;
	}

	public void setPerformanceAttThreshhold(String performanceAttThreshhold) {
		this.performanceAttThreshhold = performanceAttThreshhold;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getSkillThreshhold() {
		return skillThreshhold;
	}

	public void setSkillThreshhold(String skillThreshhold) {
		this.skillThreshhold = skillThreshhold;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSbu() {
		return sbu;
	}

	public void setSbu(String sbu) {
		this.sbu = sbu;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getLevelBelow() {
		return levelBelow;
	}

	public void setLevelBelow(String levelBelow) {
		this.levelBelow = levelBelow;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillAttribute> getPotentialAttributeList() {
		return potentialAttributeList;
	}

	public void setPotentialAttributeList(List<FillAttribute> potentialAttributeList) {
		this.potentialAttributeList = potentialAttributeList;
	}

	public List<FillAttribute> getPerformanceAttributeList() {
		return performanceAttributeList;
	}

	public void setPerformanceAttributeList(List<FillAttribute> performanceAttributeList) {
		this.performanceAttributeList = performanceAttributeList;
	}

	public List<FillSkills> getSkillslist() {
		return skillslist;
	}

	public void setSkillslist(List<FillSkills> skillslist) {
		this.skillslist = skillslist;
	}

	public List<FillEducation> getEducationList() {
		return educationList;
	}

	public void setEducationList(List<FillEducation> educationList) {
		this.educationList = educationList;
	}

	public List<FillWLocation> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<FillWLocation> locationList) {
		this.locationList = locationList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillTotExpYearsAndMonths> getTotExpYearList() {
		return totExpYearList;
	}

	public void setTotExpYearList(List<FillTotExpYearsAndMonths> totExpYearList) {
		this.totExpYearList = totExpYearList;
	}

	public List<FillTotExpYearsAndMonths> getTotExpMonthList() {
		return totExpMonthList;
	}

	public void setTotExpMonthList(List<FillTotExpYearsAndMonths> totExpMonthList) {
		this.totExpMonthList = totExpMonthList;
	}

	public List<FillTotExpYearsAndMonths> getPresentOrgExpYearList() {
		return presentOrgExpYearList;
	}

	public void setPresentOrgExpYearList(List<FillTotExpYearsAndMonths> presentOrgExpYearList) {
		this.presentOrgExpYearList = presentOrgExpYearList;
	}

	public List<FillTotExpYearsAndMonths> getPresentOrgExpMonthList() {
		return presentOrgExpMonthList;
	}

	public void setPresentOrgExpMonthList(List<FillTotExpYearsAndMonths> presentOrgExpMonthList) {
		this.presentOrgExpMonthList = presentOrgExpMonthList;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOperation() {
		return operation;
	}

	public String getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(String criteriaId) {
		this.criteriaId = criteriaId;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<String> getPotentialAttribID() {
		return potentialAttribID;
	}

	public void setPotentialAttribID(List<String> potentialAttribID) {
		this.potentialAttribID = potentialAttribID;
	}

	public List<String> getPerformanceAttribID() {
		return performanceAttribID;
	}

	public void setPerformanceAttribID(List<String> performanceAttribID) {
		this.performanceAttribID = performanceAttribID;
	}

	public List<String> getSkillsID() {
		return skillsID;
	}

	public void setSkillsID(List<String> skillsID) {
		this.skillsID = skillsID;
	}

	public List<String> getLocationID() {
		return locationID;
	}

	public void setLocationID(List<String> locationID) {
		this.locationID = locationID;
	}

	public List<String> getDepartID() {
		return departID;
	}

	public void setDepartID(List<String> departID) {
		this.departID = departID;
	}

	public List<String> getServiceID() {
		return serviceID;
	}

	public void setServiceID(List<String> serviceID) {
		this.serviceID = serviceID;
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

	public String getDesigName() {
		return desigName;
	}

	public void setDesigName(String desigName) {
		this.desigName = desigName;
	}


}