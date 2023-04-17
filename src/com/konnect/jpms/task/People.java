package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.FillEducational;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillResourceType;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class People extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	String strSessionEmpId = null;
	
	String strLocation;
	String strSkill;
	String strEdu;
	String strExp;
	String strResType;
	
	String f_org;
	String[] f_strWLocation;
	String[] strSkills;
	String[] strEducation;
	String[] strExperience;
	String[] resourceType;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillSkills> skillsList;
	List<FillEducational> eduList;
	List<FillResourceType> resourceList;
	
	String alertStatus;
	String alert_type;
	String alertID;
	
	String operation;
	String strEmpId;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId= (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/task/People.jsp");
		request.setAttribute(TITLE, "People");
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

		if(getOperation()!=null && getOperation().equalsIgnoreCase("D")){
			deletePeople(uF);
			return "delete";
		}
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrSkill() != null && !getStrSkill().equals("")) {
			setStrSkills(getStrSkill().split(","));
		} else {
			setStrSkills(null);
		}
		if(getStrEdu() != null && !getStrEdu().equals("")) {
			setStrEducation(getStrEdu().split(","));
		} else {
			setStrEducation(null);
		}
		if(getStrExp() != null && !getStrExp().equals("")) {
			setStrExperience(getStrExp().split(","));
		} else {
			setStrExperience(null);
		}
		if(getStrResType() != null && !getStrResType().equals("")) {
			setResourceType(getStrResType().split(","));
		} else {
			setResourceType(null);
		}
		
//		if(getAlertStatus()!=null && getAlert_type()!=null){
		if(uF.parseToInt(getAlertID()) > 0) {
			updateUserAlerts(uF);
		}
		
		getPeopleData(uF);
		
		return loadPeople(uF);
		
	}

	private void deletePeople(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update employee_personal_details set is_alive=false and is_delete=true where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			int x = pst.executeUpdate();
			if(x > 0){
				request.setAttribute("STATUS_MSG", "Deleted");
			} else {
				request.setAttribute("STATUS_MSG", "Not Deleted");
			}
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Not Deleted");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void updateUserAlerts(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setAlertID(getAlertID()); 
			if(strUserType!=null && strUserType.equals(CUSTOMER)) {
				userAlerts.setStrOther("other");
			}
			userAlerts.setStatus(DELETE_TR_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	private void getPeopleData(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpEducations = CF.getEmpEducations(con);
			if(hmEmpEducations == null) hmEmpEducations = new HashMap<String, String>();
			
			StringBuilder sbEmp = null;
			if(getStrEducation()!=null && getStrEducation().length>0){
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select emp_id from education_details where education_id in ("+StringUtils.join(getStrEducation(), ",")+") and emp_id > 0 ");
				pst = con.prepareStatement(sbQuery1.toString()); 
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmp == null){
						sbEmp = new StringBuilder();
						sbEmp.append(rs.getString("emp_id"));
					} else {
						sbEmp.append(","+rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			if(getStrSkills()!=null && getStrSkills().length>0){
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select emp_id from skills_description where skill_id in ("+StringUtils.join(getStrSkills(), ",")+") and emp_id > 0 ");
				pst = con.prepareStatement(sbQuery1.toString()); 
				rs = pst.executeQuery();
				while (rs.next()) {
					if(sbEmp == null){
						sbEmp = new StringBuilder();
						sbEmp.append(rs.getString("emp_id"));
					} else {
						sbEmp.append(","+rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive=true and emp_id > 0");
			if(sbEmp !=null){
				sbQuery.append(" and eod.emp_id in("+sbEmp.toString()+") ");
			}
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(strUserType!=null && strUserType.equals(MANAGER)){
	            sbQuery.append(" and eod.supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
	        }
			if(getResourceType()!=null && getResourceType().length>0){
	            sbQuery.append(" and eod.emprofile in ("+StringUtils.join(getResourceType(), ",")+") ");
	        }
			sbQuery.append(" order by epd.emp_fname,epd.emp_lname");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alPeopleList = new ArrayList<Map<String,String>>();
			List<String> alEmp = new ArrayList<String>();
			while (rs.next()) {
				Map<String, String> hmPeople = new HashMap<String, String>();
				hmPeople.put("EMP_ID", rs.getString("emp_id"));
				/*String strMiddleName = "";
				if(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("") && !rs.getString("emp_mname").trim().equalsIgnoreCase("NULL")){
					strMiddleName = rs.getString("emp_mname")+" ";
				}*/
				
				
				String strContractor = "";
				if(uF.parseToInt(rs.getString("emp_contractor")) == 2) {
					strContractor = " (C)";
				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				hmPeople.put("EMP_NAME", uF.showData(rs.getString("emp_fname"), "")+ strEmpMName+" "+uF.showData(rs.getString("emp_lname")+strContractor, ""));
				hmPeople.put("EMP_IMAGE", uF.showData(rs.getString("emp_image"), ""));
				hmPeople.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
				hmPeople.put("EMP_EDUCATION", uF.showData(hmEmpEducations.get(rs.getString("emp_id")), ""));
				
//				if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) ){
					hmPeople.put("FACTSHEET","<a target=\"_blank\" class=\"factsheet\" href=\"PeopleProfile.action?empId=" + rs.getString("emp_id") + "\" > </a>");
//				}else{
//					hmPeople.put("FACTSHEET","");
//				}
				String strExperience = null;
				if(rs.getString("joining_date")!=null && !rs.getString("joining_date").trim().equals("") && !rs.getString("joining_date").trim().equalsIgnoreCase("NULL")){
					strExperience = uF.getTimeDurationBetweenDatesNoSpanSmall(rs.getString("joining_date"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF, uF, request);
				}
				hmPeople.put("EMP_EXPERIENCE", strExperience);
				
				alPeopleList.add(hmPeople); 
				
				alEmp.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alPeopleList", alPeopleList);
			
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
			String strCurrDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String strFutureDate = uF.getDateFormat(""+uF.getFutureDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			Map<String, Map<String, String>> hmLeaves = CF.getActualLeaveDates(con, CF, uF, strCurrDate, strFutureDate, hmLeaveDatesType, false, null);
			request.setAttribute("hmLeaves", hmLeaves);
			
			Map<String, List<List<String>>> hmEmpSkills = new HashMap<String, List<List<String>>>();
			Map<String,Map<String, String>> hmEmpProject = new HashMap<String, Map<String,String>>();
			Map<String, String> hmTaskAllocation  = new HashMap<String, String>();
			int empCnt = 0;
			for(int i =0; i < alEmp.size(); i++ ){
				String strEmpId = alEmp.get(i);
//				if(uF.parseToInt(strEmpId) != 450) {
//					continue;
//				}
				empCnt++;
				
//				List<List<String>> alSkills = CF.selectSkills(con, uF.parseToInt(strEmpId));
//				hmEmpSkills.put(strEmpId, alSkills);
				
				Map<String, String> hmProject = new HashMap<String, String>();
				getEmpProjectData(con,uF,strEmpId,hmProject);
				hmEmpProject.put(strEmpId, hmProject);
								
				pst = con.prepareStatement("select count(a.*) as task_no from (select task_id,activity_name,parent_task_id,pro_id,start_date,deadline," +
					"completed from activity_info where resource_ids like '%,"+strEmpId+",%' and task_id not in (select parent_task_id " +
					" from activity_info where resource_ids like '%,"+strEmpId+",%' and parent_task_id is not null)) a, projectmntnc pmc " +
					" where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids " +
					" like '%,"+strEmpId+",%') or parent_task_id = 0) and (a.completed < 100 or a.completed is null) and ((a.start_date >= ? and a.deadline <= ?) or " +
					" (a.start_date <= ? and a.deadline >= ?) or (a.start_date >= ? and a.start_date <= ?))");
				pst.setDate(1, uF.getDateFormat(strCurrDate, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFutureDate, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strCurrDate, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strCurrDate, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strCurrDate, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFutureDate, DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while(rs.next()) {
					if(uF.parseToInt(rs.getString("task_no")) > 5) {
						hmTaskAllocation.put(strEmpId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: red;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+strEmpId+"','"+strCurrDate+"','"+strFutureDate+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					} else if(uF.parseToInt(rs.getString("task_no")) >= 2) {
						hmTaskAllocation.put(strEmpId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: yellow;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+strEmpId+"','"+strCurrDate+"','"+strFutureDate+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					} else {
						hmTaskAllocation.put(strEmpId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+strEmpId+"','"+strCurrDate+"','"+strFutureDate+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					}
				}
				rs.close();
				pst.close();
			}
//			System.out.println("hmTaskAllocation ===>> "  +hmTaskAllocation);
			request.setAttribute("hmEmpSkills", hmEmpSkills);
			request.setAttribute("hmEmpProject", hmEmpProject);
			request.setAttribute("hmTaskAllocation", hmTaskAllocation);
			request.setAttribute("empCnt", ""+empCnt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private void getEmpProjectData(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmProject) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strLevelId = CF.getEmpLevelId(con, strEmpId);
			String strwLocId = CF.getEmpWlocationId(con, uF, strEmpId);
			String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			String currId = CF.getOrgCurrencyIdByOrg(con, strOrgId);
			
			Map<String, String> hmCurrToFromVal = CF.getCurrencyFromIdToIdValue(con);
			
			int nLastProId = 0;
			pst = con.prepareStatement("select pro_id from activity_info where resource_ids like '%,"+strEmpId+",%' and start_date <=? order by start_date desc limit 1");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst=====+>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				nLastProId = uF.parseToInt(rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
			if(nLastProId > 0){
				Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, ""+nLastProId);
				
				hmProject.put("PRO_ID", ""+nLastProId);
				hmProject.put("PRO_NAME", hmProDetails.get("PRO_NAME"));
			}
			
			
			/**
			 * rate
			 * */
			
			Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
			
			Map<String,String> hmLevelDayRateMap = new HashMap<String,String>();
			Map<String,String> hmLevelRateCurrIdMap = new HashMap<String,String>();
			Map<String,String> hmLevelMonthRateMap = new HashMap<String,String>();
			
			StringBuilder sbskillRateQuery = new StringBuilder();
			sbskillRateQuery.append("SELECT * FROM level_skill_rates ");
			pst = con.prepareStatement(sbskillRateQuery.toString());
//			System.out.println("=====+>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
//				hmLevelDayRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_day") );
//				hmLevelRateCurrIdMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("curr_id"));
				hmLevelDayRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_day") );
				hmLevelRateCurrIdMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("skill_id"), rs.getString("curr_id"));
				
				hmLevelMonthRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_month"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT eod.emp_id, skill_id FROM employee_official_details eod left join skills_description sd on eod.emp_id = sd.emp_id " +
					"where eod.emp_id in (select emp_per_id from employee_personal_details where is_alive=true) and eod.emp_id=? ");
			sbQuery.append(" order by eod.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			List<String> alSkill = new ArrayList<String>();
			rs = pst.executeQuery();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			Map<String, String> hmEmpSkills = new HashMap<String, String>();
			Map<String, String> hmEmpSkillsDayRate = new HashMap<String, String>();
			Map<String, String> hmEmpSkillDayCount = new HashMap<String, String>();
			Map<String, String> hmEmpSkillsMonthRate = new HashMap<String, String>();
			Map<String, String> hmEmpSkillMonthCount = new HashMap<String, String>();
			
			Map<String, String> hmEmpCurr = new HashMap<String, String>();
			String empSkills = "";
			while(rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				double dblSkillDayRate = uF.parseToDouble(hmEmpSkillsDayRate.get(rs.getString("emp_id")));
				double dblSkillMonthRate = uF.parseToDouble(hmEmpSkillsMonthRate.get(rs.getString("emp_id")));
				int intSkillDayCnt = uF.parseToInt(hmEmpSkillDayCount.get(rs.getString("emp_id")));
				int intSkillMonthCnt = uF.parseToInt(hmEmpSkillMonthCount.get(rs.getString("emp_id")));
				
				empSkills = hmEmpSkills.get(rs.getString("emp_id"));
				if(empSkills == null) empSkills = "";
				
				String strRateCurrId = (String)hmLevelRateCurrIdMap.get(strwLocId+"_"+strLevelId+"_"+rs.getString("skill_id"));
				String strDayRate = (String)hmLevelDayRateMap.get(strwLocId+"_"+strLevelId+"_"+rs.getString("skill_id"));
				String strMonthRate = (String)hmLevelMonthRateMap.get(strwLocId+"_"+strLevelId+"_"+rs.getString("skill_id"));
				
				if(uF.parseToDouble(strDayRate) > 0) {
					intSkillDayCnt++;
				}
				if(uF.parseToDouble(strMonthRate) > 0) {
					intSkillMonthCnt++;
				}
				
				dblSkillDayRate += uF.parseToDouble(strDayRate);
				dblSkillMonthRate += uF.parseToDouble(strMonthRate);
				
				if(empSkills != null && empSkills.equals("")) {
					empSkills = CF.getSkillNameBySkillId(con, rs.getString("skill_id"));
				} else {
					empSkills = empSkills + ", "+ CF.getSkillNameBySkillId(con, rs.getString("skill_id"));
				}
				hmEmpSkills.put(rs.getString("emp_id"), empSkills);
				
				hmEmpSkillsDayRate.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblSkillDayRate));
				hmEmpSkillDayCount.put(rs.getString("emp_id"), intSkillDayCnt+"");
				
				hmEmpSkillsMonthRate.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblSkillMonthRate));
				hmEmpSkillMonthCount.put(rs.getString("emp_id"), intSkillMonthCnt+"");
				
				hmEmpCurr.put(rs.getString("emp_id"), strRateCurrId);
				
			}
			rs.close();
			pst.close();
			
			String rateAmount = "0";
			
//			String rateDayAmount = "0";
//			double dblRateDayAmount = 0;
//			if(uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpId)) > 0) {
//				dblRateDayAmount = uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpId)) / uF.parseToDouble(hmEmpSkillDayCount.get(strEmpId));
//			}
//			rateDayAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateDayAmount);
//			rateAmount = rateDayAmount;
			
			String rateMonthAmount = "0";
			double dblRateMonthAmount = 0;
			if(uF.parseToDouble(hmEmpSkillsMonthRate.get(strEmpId)) > 0) {
				dblRateMonthAmount = uF.parseToDouble(hmEmpSkillsMonthRate.get(strEmpId)) / uF.parseToDouble(hmEmpSkillMonthCount.get(strEmpId));
			}
			rateMonthAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateMonthAmount);
			rateAmount = rateMonthAmount;
			
			Map<String, String> hmCurr = hmCurrencyDetailsMap.get(hmEmpCurr.get(strEmpId));
			if(hmCurr == null) hmCurr = new HashMap<String, String>();
			String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
			hmProject.put("RATE", strCurr+ uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rateAmount)));
			
			
			/**
			 * cost
			 * */
//			String[] strPayCycleDates =  CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strOrgId);
//			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, strEmpId);
//			PeopleProfile profile = new PeopleProfile();
//			profile.session = session;
//			profile.request = request;
//			profile.CF = CF;
//			profile.getSalaryHeadsforEmployee(con, uF, uF.parseToInt(strEmpId), hmEmpProfile);
//			
//			double grossAmount = 0.0d;
//			double grossYearAmount = 0.0d;
//			List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
//			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
//				List<String> innerList = salaryHeadDetailsList.get(i);
//					if(innerList.get(1).equals("E")) {
//						grossAmount +=uF.parseToDouble(innerList.get(2));
//						grossYearAmount +=uF.parseToDouble(innerList.get(3));
//					}
//			}
//			
			double grossAmount = 0.0d;
			pst = con.prepareStatement("select month_ctc from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				grossAmount = uF.parseToDouble(rs.getString("month_ctc"));
			}
			rs.close();
			pst.close();
			
			hmCurr = hmCurrencyDetailsMap.get(currId);
			if(hmCurr == null) hmCurr = new HashMap<String, String>();
			strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
			hmProject.put("COST", strCurr+ uF.formatIntoTwoDecimalWithOutComma(grossAmount));
			
			
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private String loadPeople(UtilityFunctions uF) {
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		skillsList=new FillSkills(request).fillSkillsWithId();
		eduList=new FillEducational(request).fillEducationalQual();
		resourceList = new FillResourceType().fillResourceType();
		
		getSelectedFilter(uF);    
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
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
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
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
		
		alFilter.add("SKILL");
		if(getStrSkills()!=null) {
			String strSkill="";
			int k=0;
			for(int i=0;skillsList!=null && i<skillsList.size();i++) {
				for(int j=0;j<getStrSkills().length;j++) {
					if(getStrSkills()[j].equals(skillsList.get(i).getSkillsId())) {
						if(k==0) {
							strSkill=skillsList.get(i).getSkillsName();
						} else {
							strSkill+=", "+skillsList.get(i).getSkillsName();
						}
						k++;
					}
				}
			}
			if(strSkill!=null && !strSkill.equals("")) {
				hmFilter.put("SKILL", strSkill);
			} else {
				hmFilter.put("SKILL", "All Skills");
			}
		} else {
			hmFilter.put("SKILL", "All Skills");
		}
		
		alFilter.add("EDUCATION");
		if(getStrEducation()!=null) {
			String strEducation="";
			int k=0;
			for(int i=0;eduList!=null && i<eduList.size();i++) {
				for(int j=0;j<getStrEducation().length;j++) {
					if(getStrEducation()[j].equals(eduList.get(i).getEduId())) {
						if(k==0) {
							strEducation=eduList.get(i).getEduName();
						} else {
							strEducation+=", "+eduList.get(i).getEduName();
						}
						k++;
					}
				}
			}
			if(strEducation!=null && !strEducation.equals("")) {
				hmFilter.put("EDUCATION", strEducation);
			} else {
				hmFilter.put("EDUCATION", "All Educations");
			}
		} else {
			hmFilter.put("EDUCATION", "All Educations");
		}
		
		alFilter.add("RESOURCE_TYPE");
		if(getResourceType()!=null) {
			String strResource="";
			int k=0;
			for(int i=0;resourceList!=null && i<resourceList.size();i++) {
				for(int j=0;j<getResourceType().length;j++) {
					if(getResourceType()[j].equals(resourceList.get(i).getResourceTypeId())) {
						if(k==0) {
							strResource=resourceList.get(i).getResourceTypeName();
						} else {
							strResource+=", "+resourceList.get(i).getResourceTypeName();
						}
						k++;
					}
				}
			}
			if(strResource!=null && !strResource.equals("")) {
				hmFilter.put("RESOURCE_TYPE", strResource);
			} else {
				hmFilter.put("RESOURCE_TYPE", "All Resources");
			}
		} else {
			hmFilter.put("RESOURCE_TYPE", "All Resources");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public String[] getStrSkills() {
		return strSkills;
	}


	public void setStrSkills(String[] strSkills) {
		this.strSkills = strSkills;
	}


	public String[] getStrEducation() {
		return strEducation;
	}


	public void setStrEducation(String[] strEducation) {
		this.strEducation = strEducation;
	}


	public String[] getStrExperience() {
		return strExperience;
	}


	public void setStrExperience(String[] strExperience) {
		this.strExperience = strExperience;
	}


	public List<FillSkills> getSkillsList() {
		return skillsList;
	}


	public void setSkillsList(List<FillSkills> skillsList) {
		this.skillsList = skillsList;
	}


	public List<FillEducational> getEduList() {
		return eduList;
	}


	public void setEduList(List<FillEducational> eduList) {
		this.eduList = eduList;
	}


	public String[] getResourceType() {
		return resourceType;
	}


	public void setResourceType(String[] resourceType) {
		this.resourceType = resourceType;
	}


	public List<FillResourceType> getResourceList() {
		return resourceList;
	}


	public void setResourceList(List<FillResourceType> resourceList) {
		this.resourceList = resourceList;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrSkill() {
		return strSkill;
	}

	public void setStrSkill(String strSkill) {
		this.strSkill = strSkill;
	}

	public String getStrEdu() {
		return strEdu;
	}

	public void setStrEdu(String strEdu) {
		this.strEdu = strEdu;
	}

	public String getStrExp() {
		return strExp;
	}

	public void setStrExp(String strExp) {
		this.strExp = strExp;
	}

	public String getStrResType() {
		return strResType;
	}

	public void setStrResType(String strResType) {
		this.strResType = strResType;
	}

}
