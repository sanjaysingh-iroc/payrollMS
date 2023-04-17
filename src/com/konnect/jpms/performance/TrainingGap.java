package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;

public class TrainingGap implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	CommonFunctions CF;
	
	private String trainingFilter;
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> workLocationList;
	private List<FillLevel> levelList;
	private List<FillDepartment> departmentList;
	
	private String f_org;
	private String location;
	private String strDepart;
	private String strLevel;

	private String alertStatus;
	private String alert_type;
	
	private String alertID;
	private String fromPage;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		request.setAttribute(PAGE, "/jsp/performance/TrainingGap.jsp");
		request.setAttribute(TITLE, TTrainingGaps);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}

		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		if(strSessionUserType != null && strSessionUserType.equals(HRMANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(HR_LEARNING_GAPS_ALERT)){
//			updateUserAlerts();
//		} else if(strSessionUserType != null && strSessionUserType.equals(MANAGER) && getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(MANAGER_LEARNING_GAPS_ALERT)){
//			updateUserAlerts();
//		}
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		
		getAppraisalName();
		getAttributeName(); 
		getLocationName();
		getOrganization();
		getTrainingCount();
		
		getTrainingGapDetails();
		getSelectedFilter(uF);
		
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("LD")) {
			return LOAD;
		}
		return "success";
	}	

	
//	private void updateUserAlerts() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			if(strSessionUserType != null && strSessionUserType.equals(HRMANAGER)) {
//				userAlerts.set_type(HR_LEARNING_GAPS_ALERT);
//			} else if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
//				userAlerts.set_type(MANAGER_LEARNING_GAPS_ALERT);
//			}
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
	private void getTrainingCount() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int schedule = 0;
		int unSchedule = 0;
		int completed = 0;
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*)as count from training_gap_details where is_training_schedule=true and appraisal_id > 0 and " +
				"(attribute_id is not null or attribute_id > 0) and appraisal_id in (select appraisal_details_id from appraisal_details) ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+" ) ");
			}
			if(uF.parseToInt(getLocation())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt(getLocation())+" ) ");
			}
			if(uF.parseToInt(getStrDepart())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where depart_id="+uF.parseToInt(getStrDepart())+" ) ");
			}
			if(uF.parseToInt(getStrLevel())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where grade_id in (SELECT grade_id FROM " +
				" grades_details where designation_id in (SELECT designation_id FROM designation_details  WHERE  level_id="+uF.parseToInt(getStrLevel())+")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
			while(rs.next()){
				schedule = rs.getInt("count");
			}	
			rs.close();
			pst.close();			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*)as count from training_gap_details where is_training_schedule=false and appraisal_id > 0 and " +
				"(attribute_id is not null or attribute_id > 0) and appraisal_id in (select appraisal_details_id from appraisal_details) ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+" ) ");
			}
			if(uF.parseToInt(getLocation())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt(getLocation())+" ) ");
			}
			if(uF.parseToInt(getStrDepart())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where depart_id="+uF.parseToInt(getStrDepart())+" ) ");
			}
			if(uF.parseToInt(getStrLevel())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where grade_id in (SELECT grade_id FROM " +
				" grades_details where designation_id in (SELECT designation_id FROM designation_details  WHERE  level_id="+uF.parseToInt(getStrLevel())+")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
			while(rs.next()){
				unSchedule = rs.getInt("count");
			}
			rs.close();
			pst.close();			
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*)as count from training_gap_details where training_completed_status=true and appraisal_id > 0 and " +
				"(attribute_id is not null or attribute_id > 0) and appraisal_id in (select appraisal_details_id from appraisal_details) ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+" ) ");
			}
			if(uF.parseToInt(getLocation())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt(getLocation())+" ) ");
			}
			if(uF.parseToInt(getStrDepart())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where depart_id="+uF.parseToInt(getStrDepart())+" ) ");
			}
			if(uF.parseToInt(getStrLevel())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where grade_id in (SELECT grade_id FROM " +
				" grades_details where designation_id in (SELECT designation_id FROM designation_details  WHERE  level_id="+uF.parseToInt(getStrLevel())+")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
			while(rs.next()){
				completed = rs.getInt("count"); 
			}	
			rs.close();
			pst.close();			
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*)as count from training_gap_details where is_training_schedule=true and training_completed_status=false and learning_id >0 " +
				" and (learning_attribute_ids is not null or learning_attribute_ids !='') and learning_id in (select learning_plan_id from learning_plan_details) ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+" ) ");
			}
			if(uF.parseToInt(getLocation())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt(getLocation())+" ) ");
			}
			if(uF.parseToInt(getStrDepart())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where depart_id="+uF.parseToInt(getStrDepart())+" ) ");
			}
			if(uF.parseToInt(getStrLevel())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where grade_id in (SELECT grade_id FROM " +
				" grades_details where designation_id in (SELECT designation_id FROM designation_details  WHERE  level_id="+uF.parseToInt(getStrLevel())+")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
			while(rs.next()){
				schedule += rs.getInt("count");
			}	
			rs.close();
			pst.close();			
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*)as count from training_gap_details where is_training_schedule=false and training_completed_status=false and learning_id >0 " +
				" and (learning_attribute_ids is not null or learning_attribute_ids !='') and learning_id in (select learning_plan_id from learning_plan_details) ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+" ) ");
			}
			if(uF.parseToInt(getLocation())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt(getLocation())+" ) ");
			}
			if(uF.parseToInt(getStrDepart())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where depart_id="+uF.parseToInt(getStrDepart())+" ) ");
			}
			if(uF.parseToInt(getStrLevel())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where grade_id in (SELECT grade_id FROM " +
				" grades_details where designation_id in (SELECT designation_id FROM designation_details  WHERE  level_id="+uF.parseToInt(getStrLevel())+")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()){
				unSchedule += rs.getInt("count");
			}
			rs.close();
			pst.close();			
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(*)as count from training_gap_details where training_completed_status=true and learning_id >0 " +
				" and (learning_attribute_ids is not null or learning_attribute_ids !='') and learning_id in (select learning_plan_id from learning_plan_details) ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+" ) ");
			}
			if(uF.parseToInt(getLocation())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt(getLocation())+" ) ");
			}
			if(uF.parseToInt(getStrDepart())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where depart_id="+uF.parseToInt(getStrDepart())+" ) ");
			}
			if(uF.parseToInt(getStrLevel())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where grade_id in (SELECT grade_id FROM " +
				" grades_details where designation_id in (SELECT designation_id FROM designation_details  WHERE  level_id="+uF.parseToInt(getStrLevel())+")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
			while(rs.next()){
				completed += rs.getInt("count"); 
			}	
			rs.close();
			pst.close();	
			
			request.setAttribute("schedule", ""+schedule);
			request.setAttribute("unSchedule", ""+unSchedule);
			request.setAttribute("completed", ""+completed);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getOrganization() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select eod.emp_id,od.org_name from employee_official_details eod,org_details od where eod.org_id=od.org_id");
			rs=pst.executeQuery();
			Map<String,String> hmEmpOrg=new HashMap<String, String>();
			while(rs.next()){
				hmEmpOrg.put(rs.getString("emp_id"),rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpOrg", hmEmpOrg);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getTrainingGapDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		Database db = new Database();
		db.setRequest(request);
		List<List<String>> outerList=new ArrayList<List<String>>();
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con);
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			Map<String, String> hmEmpDepartMap = CF.getEmpDepartmentMap(con);
			Map<String, String> hmDepartMap = CF.getDeptMap(con);
			
			request.setAttribute("hmDesignation", hmDesignation);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpGradeMap", hmEmpGradeMap);
			request.setAttribute("hmGradeMap", hmGradeMap);
			request.setAttribute("hmEmpDepartMap", hmEmpDepartMap);
			request.setAttribute("hmDepartMap", hmDepartMap);
			
			Map<String, String> hmAppraisalName = (Map<String, String>) request.getAttribute("hmAppraisalName");
			if(hmAppraisalName == null) hmAppraisalName = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from appraisal_attribute");
			rs=pst.executeQuery();
			Map<String,String> hmAttributeName=new HashMap<String, String>();
			while(rs.next()){
				hmAttributeName.put(rs.getString("arribute_id"),rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from learning_plan_details");
			rs=pst.executeQuery();
			Map<String,String> hmLearnPlanName=new HashMap<String, String>();
			while(rs.next()){
				hmLearnPlanName.put(rs.getString("learning_plan_id"),rs.getString("learning_plan_name"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from training_gap_details where appraisal_id > 0 and (attribute_id is not null or attribute_id > 0) ");
			
			if(getTrainingFilter()!=null) {
				if(getTrainingFilter().equals("1")) {
					sbQuery.append(" and is_training_schedule=true and training_completed_status=false ");
				} else if(getTrainingFilter().equals("2")) {
					sbQuery.append(" and training_completed_status=true ");
				} else if(getTrainingFilter().equals("3")) {
					sbQuery.append(" and is_training_schedule=false ");
				} 
			} else {
				sbQuery.append(" and is_training_schedule=false ");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+" ) ");
			}
			if(uF.parseToInt(getLocation())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt(getLocation())+" ) ");
			}
			if(uF.parseToInt(getStrDepart())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where depart_id="+uF.parseToInt(getStrDepart())+" ) ");
			}
			if(uF.parseToInt(getStrLevel())>0) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where grade_id in " +
						"(SELECT grade_id FROM grades_details where designation_id in " +
						"(SELECT designation_id FROM designation_details  WHERE  level_id="+uF.parseToInt(getStrLevel())+")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst=======>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(!hmAppraisalName.containsKey(rs.getString("appraisal_id"))) {
					continue;
				}
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("training_gap_id"));
				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("designation_id"));
				innerList.add(rs.getString("wlocation_id"));
				innerList.add(uF.showData(hmAttributeName.get(rs.getString("attribute_id")), "-"));
				innerList.add(uF.showData(hmAppraisalName.get(rs.getString("appraisal_id")), "-")+ " (Aprraisal)");
				innerList.add(rs.getString("actual_score"));
				innerList.add(rs.getString("required_score"));
				innerList.add(rs.getString("is_training_schedule"));
				innerList.add(rs.getString("attribute_id"));
				
				double dblGapScore = 0.0d; 
				if(rs.getString("actual_score") != null && rs.getString("required_score") != null) {
					dblGapScore = rs.getDouble("actual_score") - rs.getDouble("required_score");
				}
				innerList.add(uF.formatIntoOneDecimal(dblGapScore));
				
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from training_gap_details where learning_id >0 and (learning_attribute_ids is not null or learning_attribute_ids !='') ");
			if(getTrainingFilter()!=null){
				if(getTrainingFilter().equals("1")){
					sbQuery.append(" and is_training_schedule=true and training_completed_status=false ");
				}else if(getTrainingFilter().equals("2")){
					sbQuery.append(" and training_completed_status=true ");
				}else if(getTrainingFilter().equals("3")){
					sbQuery.append(" and is_training_schedule=false ");
				} 
			}else{
				sbQuery.append(" and is_training_schedule=false ");
			}
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id="+uF.parseToInt(getF_org())+" ) ");
			}
			if(uF.parseToInt(getLocation())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt(getLocation())+" ) ");
			}
			if(uF.parseToInt(getStrDepart())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where depart_id="+uF.parseToInt(getStrDepart())+" ) ");
			}
			if(uF.parseToInt(getStrLevel())>0){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where grade_id in " +
						"(SELECT grade_id FROM grades_details where designation_id in " +
						"(SELECT designation_id FROM designation_details  WHERE  level_id="+uF.parseToInt(getStrLevel())+")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst=======>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(!hmLearnPlanName.containsKey(rs.getString("learning_id"))) {
					continue;
				}
				
				List<String> tempList = Arrays.asList(rs.getString("learning_attribute_ids").split(","));
				StringBuilder sbAttribute = null;
				StringBuilder sbAttributeId = null;
				for(int i=0; tempList!=null && i<tempList.size(); i++){
					if(hmAttributeName!=null && hmAttributeName.containsKey(tempList.get(i).trim())){
						if(sbAttribute == null){
							sbAttribute = new StringBuilder();
							sbAttribute.append(hmAttributeName.get(tempList.get(i).trim()));
							
							sbAttributeId = new StringBuilder();
							sbAttributeId.append(tempList.get(i).trim());
						} else {
							sbAttribute.append(","+hmAttributeName.get(tempList.get(i).trim()));
							sbAttributeId.append(","+tempList.get(i).trim());
						}
					}
				}
				if(sbAttribute != null && sbAttributeId!=null){
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("training_gap_id"));
					innerList.add(rs.getString("emp_id"));
					innerList.add(rs.getString("designation_id"));
					innerList.add(rs.getString("wlocation_id"));
					innerList.add(sbAttribute.toString());
					innerList.add(uF.showData(hmLearnPlanName.get(rs.getString("learning_id")), "-")+ " (Learning)");
					innerList.add(rs.getString("actual_score"));
					innerList.add(rs.getString("required_score"));
					innerList.add(rs.getString("is_training_schedule"));
					innerList.add(sbAttributeId.toString());
					double dblGapScore = 0.0d; 
					if(rs.getString("actual_score") != null && rs.getString("required_score") != null) {
						dblGapScore = rs.getDouble("actual_score") - rs.getDouble("required_score");
					}
					innerList.add(""+Math.round(dblGapScore));
					
					outerList.add(innerList);
				}		
			}
			rs.close();
			pst.close();
			
			Collections.sort(outerList, Collections.reverseOrder(new Comparator<List<String>>()
 			{
 					@Override
 					public int compare(List<String> a, List<String> b) {
 						return Integer.valueOf(a.get(0)).compareTo(Integer.valueOf(b.get(0)));
 					}
 			}));
			request.setAttribute("outerList", outerList);
//			System.out.println("outerList=====>"+outerList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private void getLocationName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from work_location_info");
			rs=pst.executeQuery();
			Map<String,String> hmLocationName=new HashMap<String, String>();
			while(rs.next()){
				hmLocationName.put(rs.getString("wlocation_id"),rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmLocationName", hmLocationName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getAttributeName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_attribute");
			rs=pst.executeQuery();
			Map<String,String> hmAttributeName=new HashMap<String, String>();
			while(rs.next()){
				hmAttributeName.put(rs.getString("arribute_id"),rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAttributeName", hmAttributeName);
			
			pst = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate,learning_plan_id from learning_plan_stage_details group by learning_plan_id");
			rs = pst.executeQuery();
			Map<String, String> hmPlanStage = new HashMap<String, String>();
			while (rs.next()) {
				String minFromDate = rs.getString("minDate");
				String maxToDate = rs.getString("maxDate");
				hmPlanStage.put(rs.getString("learning_plan_id")+"_FROMDATE",minFromDate);
				hmPlanStage.put(rs.getString("learning_plan_id")+"_ENDDATE",maxToDate);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select * from learning_plan_details where group_or_condition=2 and is_close=false");
			rs=pst.executeQuery();
			StringBuilder sbAttributeID=new StringBuilder();
			int i=0;
			while(rs.next()){
				String minFromDate = hmPlanStage.get(rs.getString("learning_plan_id")+"_FROMDATE");
				String maxToDate = hmPlanStage.get(rs.getString("learning_plan_id")+"_ENDDATE"); 
				
				if(minFromDate != null && maxToDate != null && !minFromDate.equals("") && !maxToDate.equals("")){
					String fromDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, minFromDate, DBDATE);
					String toDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, maxToDate, DBDATE);
				
					if(uF.parseToInt(fromDateDiff) <= 1 && uF.parseToInt(toDateDiff) >= 1) {
						if(i==0){
							sbAttributeID.append(rs.getString("attribute_id"));
						}else{
							sbAttributeID.append(","+rs.getString("attribute_id"));
						}
						i++;
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("sbAttributeID ===> "+sbAttributeID); 
			
			List<String> attributeList = Arrays.asList(sbAttributeID.toString().split(","));
			Set<String> attributeSet = new HashSet<String>(attributeList);
			Iterator<String> itr = attributeSet.iterator();
			Map<String,String> checkAttribute=new HashMap<String, String>();
			while (itr.hasNext()) {
				String attributeId = (String) itr.next();
				if(uF.parseToInt(attributeId.trim()) == 0){
					continue;
				}
				checkAttribute.put(attributeId.trim(), attributeId.trim());
			}
//			System.out.println("checkAttribute ===> "+ checkAttribute);
			
			request.setAttribute("checkAttribute", checkAttribute);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getAppraisalName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_details");
			rs=pst.executeQuery();
			Map<String,String> hmAppraisalName=new HashMap<String, String>();
			while(rs.next()){
				hmAppraisalName.put(rs.getString("appraisal_details_id"),rs.getString("appraisal_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAppraisalName", hmAppraisalName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("STATUS");
		if(getTrainingFilter() != null && !getTrainingFilter().equals("")) {
			if(uF.parseToInt(getTrainingFilter())==1) { 
				hmFilter.put("STATUS", "Scheduled Training");
			} else if(uF.parseToInt(getTrainingFilter())== 2) {
				hmFilter.put("STATUS", "Completed Training");
			} else {
				hmFilter.put("STATUS", "All");
			}
		} else {
			hmFilter.put("STATUS", "All");
		}
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					strOrg=organisationList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getLocation()!=null) {
			String strLocation="";
			for(int i=0;workLocationList!=null && i<workLocationList.size();i++) {
				if(getLocation().equals(workLocationList.get(i).getwLocationId())) {
					strLocation=workLocationList.get(i).getwLocationName();
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}

	String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
	request.setAttribute("selectedFilter", selectedFilter);
}
	
	public String getTrainingFilter() {
		return trainingFilter;
	}

	public void setTrainingFilter(String trainingFilter) {
		this.trainingFilter = trainingFilter;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public String getStrDepart() {
		return strDepart;
	}

	public void setStrDepart(String strDepart) {
		this.strDepart = strDepart;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
	

}
