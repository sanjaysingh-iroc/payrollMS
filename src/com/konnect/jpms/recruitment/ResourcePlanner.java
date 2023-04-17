package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ResourcePlanner extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	boolean isEmpUserType = false;
	String strOrgID = null;
	CommonFunctions CF = null;
	
	String strSessionEmpId = null;
	
	private String f_financialYearStart; 
	private String f_financialYearEnd;
	
	private List<FillFinancialYears> financialYearList;
	private List<FillWLocation> wLocationList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillDepartment> departmentList;
	private List<FillDesig> desigList;
	private List<FillOrganisation> orgList;
	
	private String f_level;
	private String f_strFinancialYear;
	private String f_department;
//	String f_service;
	private String f_strWLocation;
	private String f_desig;
	private String f_org;
	private String finansyr;
	private String orgid;
	private String wlocid;
	private String lvlid;
	private String deptid;
	private String currUserType;
	private String fromPage;
	private static Logger log = Logger.getLogger(ResourcePlanner.class);
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strOrgID = (String) session.getAttribute(ORGID);

		request.setAttribute(TITLE, "Workforce Plan");
		request.setAttribute(PAGE, "/jsp/recruitment/ResourcePlanner.jsp");
		if(getFromPage() == null || getFromPage().equals("") || getFromPage().equalsIgnoreCase("null")) {
			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"OrganisationalChart.action\" style=\"color: #3c8dbc;\"> Team</a></li>" +
				"<li class=\"active\">Workforce Plan</li>");
			} else {
				sbpageTitleNaviTrail.append("<li><i class=\"fa fa-user-circle-o\"></i><a href=\"RecruitmentDashboard.action\" style=\"color: #3c8dbc;\"> Recruitment</a></li>" +
				"<li class=\"active\">Workforce Plan</li>");
			}
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(getF_org()) == 0) {
			setF_org((String)session.getAttribute(ORGID));
		}
		loadData();
		getResourcePlannerdata();
		getDesignationEmpCount();
		getRequirementEmpCount();
		getInductEmpCount();
		getPlannedEmpCount();
		
		System.out.println("ResourcePlanner getFromPage() ===>> " + getFromPage());
		
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("WF")) {
			return LOAD;
		}
		return VIEW;

	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("FINANCIALYEAR");
		if(getF_strFinancialYear()!=null) {
			String strCal = "";
			for(int i=0;financialYearList!=null && i<financialYearList.size();i++) {
				if(getF_strFinancialYear().equals(financialYearList.get(i).getFinancialYearId())) {
					strCal = financialYearList.get(i).getFinancialYearName();
				}
			}
			if(strCal!=null && !strCal.equals("")) {
				hmFilter.put("FINANCIALYEAR", strCal);
			} else {
				hmFilter.put("FINANCIALYEAR", "-");
			}
		} else {
			hmFilter.put("FINANCIALYEAR", "-");
		}
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg = "";
				for(int i=0;orgList!=null && i<orgList.size();i++) {
					if(getF_org().equals(orgList.get(i).getOrgId())) {
						strOrg = orgList.get(i).getOrgName();
					}
				}
				if(strOrg!=null && !strOrg.equals("")) {
					hmFilter.put("ORGANISATION", strOrg);
				} else {
					hmFilter.put("ORGANISATION", "All Organisations");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisations");
			}
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	private void getPlannedEmpCount() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			/*if(getF_strWLocation()==null){
				Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
				setF_strWLocation(hmEmpLocation.get(strSessionEmpId));
			}*/
			
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			request.setAttribute("hmSkillName", hmSkillName);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select resource_requirement,pro_resource_req,designation_id,rmonth,ryear from resource_planner_details ");
			if (getF_strFinancialYear() != null){
				sbQuery.append(" where financial_year_from=? and financial_year_to=?");
			}
			sbQuery.append(" order by designation_id,rmonth");
			pst = con.prepareStatement(sbQuery.toString());
			if (getF_strFinancialYear() != null) {
	             pst.setDate(1, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
	             pst.setDate(2, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));
			}
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String,String> hmPlannedRequiredEmpCount=new HashMap<String, String>();
			while (rst.next()) {
				hmPlannedRequiredEmpCount.put("PLANNED_"+rst.getString("designation_id")+rst.getInt("rmonth")+rst.getInt("ryear"), rst.getString("resource_requirement"));
				hmPlannedRequiredEmpCount.put("REQUIRED_"+rst.getString("designation_id")+rst.getInt("rmonth")+rst.getInt("ryear"), rst.getString("pro_resource_req"));
				
			}
			rst.close();
			pst.close();
			request.setAttribute("hmPlannedRequiredEmpCount", hmPlannedRequiredEmpCount);

			sbQuery=new StringBuilder();
			sbQuery.append("select eod.emp_id,last_day_date,designation_id from emp_off_board eob, employee_official_details eod, grades_details gd " +
				" where gd.grade_id=eod.grade_id and eod.emp_id = eob.emp_id and approved_1=1 and approved_2=1 and last_day_date between ? and ? order by last_day_date");
			pst = con.prepareStatement(sbQuery.toString());
			if (getF_strFinancialYear() != null) {
	             pst.setDate(1, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
	             pst.setDate(2, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));
			}
			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmAttritionBackfillEmpCount = new HashMap<String, String>();
			while (rst.next()) {
				int intMonth = uF.parseToInt(uF.getDateFormat(rst.getString("last_day_date"), DBDATE, "MM"));
				int intYear = uF.parseToInt(uF.getDateFormat(rst.getString("last_day_date"), DBDATE, "yyyy"));
				String strEmpCnt = hmAttritionBackfillEmpCount.get(rst.getString("designation_id")+intMonth+intYear);
				int intEmpCnt = uF.parseToInt(strEmpCnt);
				intEmpCnt++;
				hmAttritionBackfillEmpCount.put(rst.getString("designation_id")+intMonth+intYear, intEmpCnt+"");
			}
			rst.close();
			pst.close();
			request.setAttribute("hmAttritionBackfillEmpCount", hmAttritionBackfillEmpCount);
			
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from resource_plan_request_details ");
			if (getF_strFinancialYear() != null){
				sbQuery.append(" where fy_start=? and fy_end=?");
			}
			sbQuery.append(" order by skill_id,req_month");
			pst = con.prepareStatement(sbQuery.toString());
			if (getF_strFinancialYear() != null) {
	             pst.setDate(1, uF.getDateFormat(getF_financialYearStart(), DATE_FORMAT));
	             pst.setDate(2, uF.getDateFormat(getF_financialYearEnd(), DATE_FORMAT));
			}
			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, Map<String, Map<String, String>>> hmRequiredDesigwiseSkillwiseEmpCount = new HashMap<String, Map<String, Map<String, String>>>();
			while (rst.next()) {
				
				Map<String, Map<String, String>> hmRequiredSkillwiseEmpCount = hmRequiredDesigwiseSkillwiseEmpCount.get(rst.getString("desig_id"));
				if(hmRequiredSkillwiseEmpCount==null) hmRequiredSkillwiseEmpCount = new HashMap<String, Map<String, String>>();
				
				Map<String, String> hmRequiredSkillMonthwiseEmpCount = hmRequiredSkillwiseEmpCount.get(rst.getString("skill_id"));
				if(hmRequiredSkillMonthwiseEmpCount==null) hmRequiredSkillMonthwiseEmpCount = new HashMap<String, String>();
				
				String strCnt = hmRequiredSkillMonthwiseEmpCount.get(rst.getString("skill_id")+rst.getInt("req_month")+rst.getInt("req_year"));
				int intCnt = uF.parseToInt(strCnt);
				intCnt++;
				
				System.out.println("intCnt === " + intCnt);
				hmRequiredSkillMonthwiseEmpCount.put(rst.getString("skill_id")+rst.getInt("req_month")+rst.getInt("req_year"), intCnt+"");
				hmRequiredSkillwiseEmpCount.put(rst.getString("skill_id"), hmRequiredSkillMonthwiseEmpCount);
				hmRequiredDesigwiseSkillwiseEmpCount.put(rst.getString("desig_id"), hmRequiredSkillwiseEmpCount);
				
			}
			rst.close();
			pst.close();
			request.setAttribute("hmRequiredDesigwiseSkillwiseEmpCount", hmRequiredDesigwiseSkillwiseEmpCount);
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void getRequirementEmpCount() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			if(getF_strWLocation()==null){
				Map<String, String> hmEmpLocation=CF.getEmpWlocationMap(con);
				setF_strWLocation(hmEmpLocation.get(strSessionEmpId));
			}
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_date,sum(no_position)as count,designation_id from recruitment_details where effective_date is not null ");
//			if(uF.parseToInt(getF_strWLocation())>0){
//				sbQuery.append(" and wlocation="+uF.parseToInt(getF_strWLocation()));
//			}
			/*if(uF.parseToInt(getF_service())>0){   
				sbQuery.append(" and services="+uF.parseToInt(getF_service()));	
			}*/
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and level_id="+uF.parseToInt(getF_level()));	
			}
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and dept_id = "+uF.parseToInt(getF_department()));
//			}
			sbQuery.append("  group by effective_date,designation_id,date_part('month',to_date(effective_date::text,'YYYY-MM-DD')) order by designation_id");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String,String> hmRequirementEmpCount=new HashMap<String, String>();
			while (rst.next()) {
				String effective_date=rst.getString("effective_date");
				String month=uF.getDateFormat(effective_date, DBDATE, "MM");
				String year=uF.getDateFormat(effective_date, DBDATE, "yyyy");
				
				hmRequirementEmpCount.put(rst.getString("designation_id")+uF.parseToInt(month)+uF.parseToInt(year),rst.getString("count"));				
			}
			rst.close();
			pst.close();
			request.setAttribute("hmRequirementEmpCount",hmRequirementEmpCount);
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		} 
		
	}

	private void getInductEmpCount() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			if(getF_strWLocation()==null){
				Map<String, String> hmEmpLocation=CF.getEmpWlocationMap(con);
				setF_strWLocation(hmEmpLocation.get(strSessionEmpId));
			}
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append(" select count(*)as count,designation_id,candidate_joining_date from candidate_application_details cad " +
					" inner join recruitment_details rd on cad.recruitment_id=rd.recruitment_id where candidate_status=1 " +
					" and candidate_final_status=1 and candidate_joining_date is not null ");
//			if(uF.parseToInt(getF_strWLocation())>0){
//				sbQuery.append(" and rd.wlocation="+uF.parseToInt(getF_strWLocation()));
//			}
			
			/*if(uF.parseToInt(getF_service())>0){   
				sbQuery.append(" and rd.services="+uF.parseToInt(getF_service()));	
			}*/
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and rd.level_id="+uF.parseToInt(getF_level()));	
			}
			
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and rd.dept_id = "+uF.parseToInt(getF_department()));
//			}
			sbQuery.append(" group by designation_id,candidate_joining_date,date_part('month',to_date(candidate_joining_date::text,'YYYY-MM-DD'))");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String,String> hmInductEmpCount=new HashMap<String, String>();
			while (rst.next()) {
				String joindate=rst.getString("candidate_joining_date");
				String month=uF.getDateFormat(joindate, DBDATE, "MM");
				String year=uF.getDateFormat(joindate, DBDATE, "yyyy");
				
				hmInductEmpCount.put(rst.getString("designation_id")+uF.parseToInt(month)+uF.parseToInt(year),rst.getString("count"));				
			}
			rst.close();
			pst.close();
			
			request.setAttribute("hmInductEmpCount",hmInductEmpCount);
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void getDesignationEmpCount() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		

		try {
			con = db.makeConnection(con);
			
			if(getF_strWLocation()==null){
				Map<String, String> hmEmpLocation=CF.getEmpWlocationMap(con);
				setF_strWLocation(hmEmpLocation.get(strSessionEmpId));
			}
			
			StringBuilder sbQuery=new StringBuilder();
			
/*			sbQuery.append("select count(*)as count,dd.designation_id,date_part('month',joining_date) as date " +
					"from grades_details gd, designation_details dd, level_details ld,employee_personal_details epd," +
					"employee_official_details eod where dd.designation_id = gd.designation_id and  epd.emp_per_id=eod.emp_id " +
					" and ld.level_id = dd.level_id and gd.grade_id = eod.grade_id and is_alive='t' ");

			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id="+uF.parseToInt(getF_strWLocation()));
			}
			
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and dd.level_id="+uF.parseToInt(getF_level()));	
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id="+uF.parseToInt(getF_department()));
			}
			
			sbQuery.append("group by dd.designation_id,date_part('month',joining_date) order by dd.designation_id,date");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("printing query pst==="+pst);
			rst = pst.executeQuery();
			Map<String,Map<String,String>> hmDesigEmpCount=new HashMap<String, Map<String,String>>();
			while (rst.next()) {
				Map<String,String> hmInner=new HashMap<String, String>();
				hmInner.put(rst.getString("date"), rst.getString("count"));
				hmDesigEmpCount.put(rst.getString("designation_id"),hmInner);				
			}

			request.setAttribute("hmDesigEmpCount",hmDesigEmpCount);*/
			
			sbQuery.append("select count(*)as count,dd.designation_id from grades_details gd, designation_details dd, " +
					"level_details ld, employee_official_details eod where dd.designation_id = gd.designation_id " +
					" and ld.level_id = dd.level_id and gd.grade_id = eod.grade_id ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and ld.org_id="+uF.parseToInt(getF_org()));
			}
//			if(uF.parseToInt(getF_strWLocation())>0){
//				sbQuery.append(" and eod.wlocation_id="+uF.parseToInt(getF_strWLocation()));
//			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and dd.level_id="+uF.parseToInt(getF_level()));	
			}
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and eod.depart_id="+uF.parseToInt(getF_department()));
//			}
			sbQuery.append(" group by dd.designation_id order by dd.designation_id");
			pst = con.prepareStatement(sbQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String,String> hmDesigEmpCount=new HashMap<String, String>();
			while (rst.next()) {
				hmDesigEmpCount.put(rst.getString("designation_id"),rst.getString("count"));				
			}
			rst.close();
			pst.close();

			request.setAttribute("hmDesigEmpCount",hmDesigEmpCount);
			
            Calendar cal=Calendar.getInstance();
            int month=cal.get(cal.MONTH)+1;
            
            request.setAttribute("currentMonth", month);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private String getManagerDesigIds() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String desigIds = null;

		try {
			con = db.makeConnection(con);
			
			if(strUserType.equals(MANAGER)) {
				List<String> designList = new ArrayList<String>();
				pst=con.prepareStatement("select supervisor_emp_id,emp_id,dd.designation_id from employee_personal_details epd join " +
						"employee_official_details eod on(epd.emp_per_id = eod.emp_id) join grades_details gd on eod.grade_id=gd.grade_id " +
						"join designation_details dd on gd.designation_id=dd.designation_id where  is_alive= true and emp_per_id >0 and " +
						"(supervisor_emp_id = ? or emp_id = ?)"); //appraisal_attribute in ("+getCheckParam()+") and 
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				rst=pst.executeQuery();
				while(rst.next()){
					if(!designList.contains(rst.getString("designation_id"))) {
						designList.add(rst.getString("designation_id"));
					}
				}
				rst.close();
				pst.close();
				desigIds = getAppendData(designList);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return desigIds;
		
	}

	public String getAppendData(List<String> strID) {
		StringBuilder sb = new StringBuilder();
		if (strID != null) {
			for (int i = 0; i < strID.size(); i++) {
				if (i == 0) {
					sb.append(strID.get(i));
				} else {
					sb.append("," + strID.get(i));
				}
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	
	private void getResourcePlannerdata() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		

		try {
			con = db.makeConnection(con);
			if(getF_strWLocation()==null){
				Map<String, String> hmEmpLocation=CF.getEmpWlocationMap(con);
				setF_strWLocation(hmEmpLocation.get(strSessionEmpId));
			}

			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT * FROM designation_details ald JOIN level_details ld ON ald.level_id = ld.level_id where ald.level_id>0 ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and ald.level_id="+uF.parseToInt(getF_level()));
			}
//			if(uF.parseToInt(getF_desig())>0){
//				sbQuery.append(" and designation_id="+uF.parseToInt(getF_desig()));
//			}
			if(strUserType != null && strUserType.equals(MANAGER) && getCurrUserType() != null && !getCurrUserType().equals(strBaseUserType)) {
				String desigIds = getManagerDesigIds();
				sbQuery.append(" and designation_id in ("+desigIds+")");
			}
			sbQuery.append(" order by designation_name");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> " + pst);
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			List<List<String>> outerList=new ArrayList<List<String>>();
			while (rst.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rst.getString("designation_id"));
				innerList.add(rst.getString("designation_code"));
				innerList.add(rst.getString("designation_name"));
				innerList.add(rst.getString("designation_description"));
				innerList.add(rst.getString("level_id"));
				innerList.add(rst.getString("attribute_ids"));
				
				outerList.add(innerList);
				
			}
			rst.close();
			pst.close();
			
			request.setAttribute("designationList",outerList);
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void loadData() {
		
		UtilityFunctions uF=new UtilityFunctions();
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
//		levelList = new FillLevel(request).fillLevel();
		if(getF_org() == null) {
			if(uF.parseToInt(getOrgid())>0) {
				setF_org(getOrgid());
			} else {
				setF_org(strOrgID);
				setOrgid(strOrgID);
			}
		} else {
			setOrgid(getF_org());
		}
		if(strUserType.equals(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
			orgList = new FillOrganisation(request).fillOrganisation(getF_org());
		} else if(strUserType != null && !strUserType.equals(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
//		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
//		workList = new FillWLocation(request).fillWLocation(org);
//		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
//		serviceList = new FillServices(request).fillServices();
//		departmentList = new FillDepartment(request).fillDepartment();
//		desigList = new FillDesig(request).fillDesig();
		
		String[] strFinancialYearDates = null;
		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;

		if (getF_strFinancialYear() != null) {
			strFinancialYearDates = getF_strFinancialYear().split("-");
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];
		} else {
			strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			setF_strFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
			strFinancialYearStart = strFinancialYearDates[0];
			strFinancialYearEnd = strFinancialYearDates[1];			 
		}
		
		setF_financialYearStart(strFinancialYearStart);
		setF_financialYearEnd(strFinancialYearEnd);
		
		String monthStart=uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM");
		String yearStart=uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy");
		
		String monthEnd=uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM");
		String yearEnd=uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy");
		
		if(getFinansyr()!=null) {
			setF_strFinancialYear(getFinansyr());
		}
		
		if(getWlocid()!=null) {
			setF_strWLocation(getWlocid());
		}
		if(getDeptid()!=null) {
			setF_department(getDeptid());
		}
		if(getLvlid()!=null) {
			setF_level(getLvlid());
		}
		
		getSelectedFilter(uF);
		
		request.setAttribute("monthStart", ""+uF.parseToInt(monthStart));
		request.setAttribute("yearStart", ""+uF.parseToInt(yearStart));
		request.setAttribute("monthEnd", ""+uF.parseToInt(monthEnd));
		request.setAttribute("yearEnd", ""+uF.parseToInt(yearEnd));
	}

	public String getFinansyr() {
		return finansyr;
	}

	public void setFinansyr(String finansyr) {
		this.finansyr = finansyr;
	}

	public String getOrgid() {
		return orgid;
	}

	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}

	public String getWlocid() {
		return wlocid;
	}

	public void setWlocid(String wlocid) {
		this.wlocid = wlocid;
	}

	public String getLvlid() {
		return lvlid;
	}

	public void setLvlid(String lvlid) {
		this.lvlid = lvlid;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getF_strFinancialYear() {
		return f_strFinancialYear;
	}

	public void setF_strFinancialYear(String f_strFinancialYear) {
		this.f_strFinancialYear = f_strFinancialYear;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	/*public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}*/

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getF_desig() {
		return f_desig;
	}

	public void setF_desig(String f_desig) {
		this.f_desig = f_desig;
	}

	private HttpServletRequest request;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getF_financialYearStart() {
		return f_financialYearStart;
	}

	public void setF_financialYearStart(String f_financialYearStart) {
		this.f_financialYearStart = f_financialYearStart;
	}

	public String getF_financialYearEnd() {
		return f_financialYearEnd;
	}

	public void setF_financialYearEnd(String f_financialYearEnd) {
		this.f_financialYearEnd = f_financialYearEnd;
	}


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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
}
