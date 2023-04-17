package com.konnect.jpms.recruitment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillRecruitmentTechnology;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.CustomEmailer;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RequirementRequestWithoutWorkflow extends ActionSupport implements ServletRequestAware, IStatements, Runnable {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;
	String strEmpOrgId = null;
	String strEmpWLocId = null;
		
	private String manlocation;
	private String recruitmentID;
	private String strInsert;
	private String organisation;
	private String strOrg;
	
	private String orgID;
	private String wlocID;
	private String desigID;
	private String checkStatus;
	private String fdate;
	private String tdate;
	private String strAddedBy;
	private String frmPage;
	
	private String strGrade;
	private String strDesignationUpdate;
	private String location;
	private String position;
	private String rdate;
	private String desig;
	private String[] skills;
    
	private String strDomain;
	private String strLevel;

	// addition of fields********
	private String minYear;
	private String minMonth;
	private String maxYear;
	private String maxMonth;
	private String[] hiringManager;
	private String strCustomer;
	private String strOtherCustomer;
	private String jobDescription;
	private String jobTitle;
	private String currUserType;
	private String priority;
	private String[] essentialSkills;
	
	private List<String> hrIDList;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeList;
	private List<FillWLocation> workLocationList;
	private List<FillSkills> skillslist;
	private List<FillSkills> essentialSkillsList;
	private List<FillOrganisation> organisationList;
	private List<FillClients> clientList;
	private List<FillLevel> levelslist;
	private List<FillEmployee> hrAndGlobalHrList;
	
	private List<FillWLocation> workList;
	private List<FillDepartment> departmentList;
	private List<FillOrganisation> orgList;
	private List<FillLevel> levelList;
	private List<FillDesig> designationList;
	
	private List<FillOrganisation> orgList1;
	private List<FillEmployee> empList1;
	private List<FillWLocation> workList1;
	private List<FillDepartment> departmentList1;
	private List<FillLevel> levelList1;
	private List<FillDesig> designationList1;
	private List<FillEmployee> empList;
	
	private List<FillEmploymentType> employmentList;
	private List<FillRecruitmentTechnology> technologyList;//Start Dattatray Date:21-08-21
	
	private String essentialSkillsText;
	private String desirableSkillsText;
	
	
	private String notes;
	private String services;
	private String strEmployment;
	private String strSex;
	private String strAge;
	private String strMinCTC;
	private String strMaxCTC;

	// addition of fields********
	private String targetdeadline;
//	private String assessThreshhold;
	private String strEmploymentType;
	private String tempOrCasualJastification;
	private String idealCandidate;
	private String vacancy;
	private String addtionalJastification;
	private String empselected;
	private String empselected1;
	private String reportToType;
	private String gender;
	private String minAge;
	//Start Dattatray Date:21-08-21
	private String strCategory;
	private String strTechnology;
	//End Dattatray Date:21-08-21
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID); 
		strEmpWLocId = (String) session.getAttribute(WLOCATIONID);
		
		request.setAttribute(TITLE, "Requirement Request");
		request.setAttribute(PAGE, "/jsp/recruitment/RequirementRequest.jsp");
		UtilityFunctions uF=new UtilityFunctions();
	
		setStrOrg(strEmpOrgId);
		setManlocation(strEmpWLocId);
		getElementList(uF);
		
		CF.getFormValidationFields(request, REQUIREMENT_REQUEST);
		
		if (recruitmentID != null && !recruitmentID.equals("") && getStrInsert()==null) {
			getRecruitmentRequest(recruitmentID, uF);
			getSelectEmployeeList(recruitmentID,uF);
			getSelectEmployeeList1(recruitmentID,uF);
		} else {
			desigList = new ArrayList<FillDesig>();
			gradeList = new ArrayList<FillGrade>();
			workLocationList = new FillWLocation(request).fillWLocation(strEmpOrgId);
			levelslist = new FillLevel(request).fillLevel(uF.parseToInt(strEmpOrgId));
	}
		hrAndGlobalHrList = new FillEmployee(request).fillGlobalHRAndHRName(strEmpOrgId);
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		skillslist = new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(getStrOrg()));
		essentialSkillsList = new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(getStrOrg()));
		clientList = new FillClients(request).fillClients(true);
		
		employmentList = new FillEmploymentType().fillEmploymentType(request);
		
	//===start parvez date: 19-10-2021===
//		empList = new FillEmployee(request).fillEmployeeNameByLocation(strEmpWLocId);
		empList = new FillEmployee(request).fillEmployeeNameByLocForRecruitment(strEmpWLocId);
	//===end parvez date: 19-10-2021===
		
		levelList = new FillLevel(request).fillLevel();
		designationList = new FillDesig(request).fillDesig();
		workList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		orgList = new FillOrganisation(request).fillOrganisation();
		
	//===start parvez date: 19-10-2021===	
//		empList1 = new FillEmployee(request).fillEmployeeNameByLocation(strEmpWLocId);
		empList1 = new FillEmployee(request).fillEmployeeNameByLocForRecruitment(strEmpWLocId);
	//===end parvez date: 19-10-2021===
		
		levelList1 = new FillLevel(request).fillLevel();
		designationList1 = new FillDesig(request).fillDesig();
		workList1 = new FillWLocation(request).fillWLocation();
		departmentList1 = new FillDepartment(request).fillDepartment();
		orgList1 = new FillOrganisation(request).fillOrganisation();
		technologyList = new FillRecruitmentTechnology(request).fillRecruitmentTechnologies();//Created Dattatray Date:21-08-21
		
		if(recruitmentID != null && strInsert != null && uF.parseToInt(recruitmentID) == 0) {
			return insertRecruitmentRequst(uF);
		} else if (recruitmentID != null && strInsert != null && uF.parseToInt(recruitmentID) >= 0) {
			return editRecruitmentRequst(uF);
		}
		return LOAD;
	}


	public void getElementList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			request.setAttribute("hmFeatureUserTypeId", hmFeatureUserTypeId);
			
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null, null);
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			request.setAttribute("hmEmpLocation", hmEmpLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("hmEmpName", hmEmpName);
			
			String hiringManager = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
//			request.setAttribute("hiringManager", hiringManager);
			
			Map<String, String> hmOrgName = new HashMap<String, String>();
			pst = con.prepareStatement("select od.org_id,od.org_name from org_details od"); // where od.org_id = ?
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOrgName.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOrgName", hmOrgName);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

private void getSelectEmployeeList1(String recruitmentID,UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			con=db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			pst = con.prepareStatement("select reporting_to_person_ids from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(recruitmentID));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			while(rst.next()){		
				selectEmpIDs=rst.getString("reporting_to_person_ids");
			}
			rst.close();
			pst.close();
			
			List<String> selectEmpList1=new ArrayList<String>();
			Map<String,String> hmCheckEmpList1=new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
				
				List<String> tmpselectEmpList=Arrays.asList(selectEmpIDs.split(","));
				
				int i=0;
				if(tmpselectEmpList != null && !tmpselectEmpList.isEmpty()){
					for(String empId:tmpselectEmpList){
						if(empId.equals("0") || empId.equals("")){
							continue;
						}
						selectEmpList1.add(hmEmpName.get(empId));
						if(i==0){
							sb.append(empId);
							i++;
						}else{
							sb.append(","+empId);
						}
					hmCheckEmpList1.put(empId.trim(), empId.trim());
					}
				}
			}else{
				selectEmpList1=null;
			}
			request.setAttribute("selectEmpList1", selectEmpList1);
			request.setAttribute("hmCheckEmpList1", hmCheckEmpList1);
			request.setAttribute("empids", sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
private void getSelectEmployeeList(String recruitmentID,UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		try {
			con=db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			pst = con.prepareStatement("select replacement_person_ids from recruitment_details where recruitment_id=?");
			pst.setInt(1, uF.parseToInt(recruitmentID));
			rst=pst.executeQuery();
			String selectEmpIDs=null;
			while(rst.next()){		
				selectEmpIDs=rst.getString("replacement_person_ids");
			}
			rst.close();
			pst.close();
			
			List<String> selectEmpList=new ArrayList<String>();
			Map<String,String> hmCheckEmpList=new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			if(selectEmpIDs!=null && !selectEmpIDs.equals("")){
				
				List<String> tmpselectEmpList=Arrays.asList(selectEmpIDs.split(","));
				
				int i=0;
				if(tmpselectEmpList != null && !tmpselectEmpList.isEmpty()){
					for(String empId:tmpselectEmpList){
						if(empId.equals("0") || empId.equals("")){
							continue;
						}
						selectEmpList.add(hmEmpName.get(empId));
						if(i==0){
							sb.append(empId);
							i++;
						}else{
							sb.append(","+empId);
						}
					hmCheckEmpList.put(empId.trim(), empId.trim());
					}
				}
			}else{
				selectEmpList=null;
			}
			request.setAttribute("selectEmpList", selectEmpList);
			request.setAttribute("hmCheckEmpList", hmCheckEmpList);
			request.setAttribute("empids", sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}

	private void getRecruitmentRequest(String recruitmentID, UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			pst=con.prepareStatement("select * from designation_details");
			rst=pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			Map<String, String> hmDesigIdeal=new HashMap<String, String>();
			while(rst.next()){
				hmDesigIdeal.put(rst.getString("designation_id"),uF.showData(rst.getString("ideal_candidate"),""));
			}
			rst.close();
			pst.close();
			
			String query = "select * from recruitment_details where recruitment_id=?";
			pst = con.prepareStatement(query);
			pst.setInt(1, uF.parseToInt(recruitmentID));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
//			System.out.println("pst ===> "+pst);
			String empExistCount=null;
			while (rst.next()) {

				setStrDesignationUpdate(rst.getString("designation_id"));
				setStrOrg(rst.getString("org_id"));
				setStrGrade(rst.getString("grade_id"));

				setManlocation(rst.getString("wlocation"));
				// logic left ......
		//		skills = temp.split(", ");
				if(rst.getString("skills")!=null)
				setSkills(rst.getString("skills").split(","));
				setServices(rst.getString("services"));
				
				setStrLevel(rst.getString("level_id")) ;
				
				setLocation(rst.getString("wlocation"));
				setPriority(rst.getString("priority_job_int"));
				
				setNotes(rst.getString("comments"));
				
				setJobTitle(rst.getString("job_title"));
				setJobDescription(rst.getString("job_description"));
				
				setPosition(rst.getString("no_position"));
				setTargetdeadline(uF.getDateFormat(rst.getString("target_deadline"), DBDATE, DATE_FORMAT));
				setRdate(uF.getDateFormat(rst.getString("effective_date"), DBDATE, DATE_FORMAT));
				setOrganisation(rst.getString("org_id"));
				
				setIdealCandidate(rst.getString("ideal_candidate")!=null && !rst.getString("ideal_candidate").equals("") ? rst.getString("ideal_candidate") : hmDesigIdeal.get(rst.getString("designation_id"))!=null ? hmDesigIdeal.get(rst.getString("designation_id")) : "" );
				
				empExistCount = rst.getString("existing_emp_count");
//				setEssentialSkills(rst.getString("essential_skills"));
				setStrEmployment(rst.getString("type_of_employment"));
				setStrSex(getRequiredGender(rst.getString("sex")));
				setStrAge(getRequiredAge(rst.getString("age"), uF));
				setTempOrCasualJastification(rst.getString("temp_casual_give_jastification"));
				setVacancy(rst.getString("vacancy_type"));
				setAddtionalJastification(rst.getString("give_justification"));
				
				if(rst.getString("essential_skills")!=null){
					setEssentialSkills(rst.getString("essential_skills").split(","));
				}
				
				String minex;
				if (rst.getString("min_exp") == null || rst.getString("min_exp").equals("")) {
					minex = removeNUll("0.0");
				} else if (rst.getString("min_exp").contains(".1")) {
					minex = removeNUll(rst.getString("min_exp") + "0");
				} else if (rst.getString("min_exp").contains(".")) {
					minex = removeNUll(rst.getString("min_exp"));
				} else {
					minex = removeNUll(rst.getString("min_exp") + ".0");
				}
				String[] minTemp = splitString(minex);
				setMinYear(removeNUll(minTemp[0])); //8
				setMinMonth(removeNUll(minTemp[1])); //9

				String maxex;
				if (rst.getString("max_exp") == null || rst.getString("max_exp").equals("")) {
					maxex = removeNUll("0.0");
				} else if (rst.getString("max_exp").contains(".1")) {
					maxex = removeNUll(rst.getString("max_exp") + "0");
				} else if (rst.getString("max_exp").contains(".")) {
					maxex = removeNUll(rst.getString("max_exp"));
				} else {
					maxex = removeNUll(rst.getString("max_exp") + ".0");
				}
				String[] maxTemp = splitString(maxex);
				setMaxYear(removeNUll(maxTemp[0]));//10
				setMaxMonth(removeNUll(maxTemp[1]));//11
				
				setEmpselected(rst.getString("replacement_person_ids"));
				setEmpselected1(rst.getString("reporting_to_person_ids"));
				setReportToType(rst.getString("reportto_type"));
				
				setStrCustomer(rst.getString("customer_id"));
				setHiringManager(rst.getString("hiring_manager").split(","));
				setStrMinCTC(rst.getString("min_ctc"));
				setStrMaxCTC(rst.getString("max_ctc"));
				setEssentialSkillsText(rst.getString("essential_skills_text"));
				setDesirableSkillsText(rst.getString("desirable_skills_text"));
				// Started by Dattatray Date:21-08-21
				setStrCategory(rst.getString("jd_category"));
				setStrTechnology(rst.getString("technology_id"));
				// Ended by Dattatray Date:21-08-21
				
				
			}
			rst.close();
			pst.close();
			
			workLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
			levelslist = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
			
			if (getStrLevel() != null) {
				desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
			}
//			desigList.add(new FillDesig("0", "Add New Designation"));
			gradeList=new FillGrade(request).fillGradeFromDesignation(getStrDesignationUpdate());
			
			request.setAttribute("existingCount",empExistCount);
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select count(*)as count,dd.designation_id from grades_details gd, designation_details dd, level_details ld, employee_official_details eod " +
				" where dd.designation_id = gd.designation_id  and ld.level_id = dd.level_id and gd.grade_id = eod.grade_id ");
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
			
			String strExistCount=hmDesigEmpCount.get(getStrDesignationUpdate())!=null ? hmDesigEmpCount.get(getStrDesignationUpdate()) : "0";
//			System.out.println("strExistCount====>"+strExistCount);
			request.setAttribute("strExistCount",strExistCount);
			
			//====start parvez on 06-07-2021=====
			int output = 0;
			DateFormat formater = new SimpleDateFormat("MMM-yyyy");

	        Calendar beginCalendar = Calendar.getInstance();
	        Calendar finishCalendar = Calendar.getInstance();
	        try {
	        	if(getRdate()!=null || !getRdate().equals("") ){
	        		beginCalendar.setTime(formater.parse(uF.getDateFormat(getRdate(), DATE_FORMAT, "MMM-yyyy")));
	        	}
	        	if(getTargetdeadline()!=null || !getTargetdeadline().equals("") ){
	        		finishCalendar.setTime(formater.parse(uF.getDateFormat(getTargetdeadline(), DATE_FORMAT, "MMM-yyyy")));
	        	}
//	        	System.out.println("RRWW/528--getTargetdeadline()="+getTargetdeadline()+"--getRdate"+getRdate());
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        DateFormat formaterYd = new SimpleDateFormat("MM");
			DateFormat formaterYd1 = new SimpleDateFormat("yyyy");
			
				finishCalendar.add(Calendar.MONTH, 1);
				while (beginCalendar.before(finishCalendar)) {

		            String month = formaterYd.format(beginCalendar.getTime()).toUpperCase();
					String year = formaterYd1.format(beginCalendar.getTime()).toUpperCase();
//		            System.out.println("month="+month);
//					System.out.println("year="+year);
		            // Add One Month to get next Month
					if(getStrDesignationUpdate()!=null && !getStrDesignationUpdate().equals("") && uF.parseToInt(year)!=0 && uF.parseToInt(month)!=0){
						pst=con.prepareStatement("select resource_requirement from resource_planner_details where designation_id="+getStrDesignationUpdate()+" and" +
							" ryear="+uF.parseToInt(year)+" and rmonth="+uF.parseToInt(month)+"");
						rst=pst.executeQuery();
//						System.out.println("RRWW/559--pst===>"+pst);
						while(rst.next()){
							output = output + uF.parseToInt(rst.getString("resource_requirement"));
						}
						rst.close();
						pst.close();
						
					}
		            beginCalendar.add(Calendar.MONTH, 1);
		        }
			
			request.setAttribute("Output", output+"");
			
			//=====end parvez on 06-07-2021=====
			
		} catch (Exception e) {
		
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private String getRequiredAge(String age, UtilityFunctions uF) {
		StringBuilder data = new StringBuilder();
		data.append("<option value=\"0\">Select Age</option>");
		for (int i = 0; i <=42; i++) {
			int minAge = 18;
			minAge += i;
			if (minAge == uF.parseToInt(age)) {
				data.append("<option value=\"" + minAge + "\" selected=\"selected\">" + minAge + " Years</option>");
			}else {
				data.append("<option value=\"" + minAge + "\">" + minAge + " Years</option>");
			}
		}
		return data.toString();
	}

	
	private String getRequiredGender(String gender) {
		List<FillGender> genderList=new FillGender().fillGender();
		StringBuilder data = new StringBuilder();
		data.append("<option value=\"0\">Any</option>");
		for (int i = 0; i < genderList.size(); i++) {
			if (gender != null && genderList.get(i).getGenderId().equals(gender)) {
				data.append("<option value=\""+genderList.get(i).getGenderId()+"\" selected=\"selected\">"+genderList.get(i).getGenderName()+"</option>");
			} else {
				data.append("<option value=\""+genderList.get(i).getGenderId()+"\">"+genderList.get(i).getGenderName()+"</option>");
			}
		}
		return data.toString();
	}

	private String editRecruitmentRequst(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		int depart_id = 0;

		try {
			con = db.makeConnection(con);
	
			int intClientId = 0;
			if(getStrOtherCustomer() != null && getStrOtherCustomer().trim().length()>0) {
				pst = con.prepareStatement("insert into client_details (client_name) values(?)");
				pst.setString(1, getStrOtherCustomer());
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(client_id) as client_id from client_details");
				rst = pst.executeQuery();
				while(rst.next()) {
					intClientId = rst.getInt("client_id");
				}
				rst.close();
				pst.close();
			}
			
			String query1 = "select depart_id from employee_official_details where emp_id=?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				depart_id = rst.getInt(1);
			}
			
			rst.close();
			pst.close();
			
			if (depart_id > 0 && uF.parseToInt(getStrDesignationUpdate()) > 0) {
				
				StringBuilder sbEssentialSkills = new StringBuilder();
				if(getEssentialSkillsText().trim().length()>0) {
					List<String> alEssentialSkill = Arrays.asList(getEssentialSkillsText().trim().split(","));
					for(int i=0; alEssentialSkill!=null && i<alEssentialSkill.size(); i++) {
						pst = con.prepareStatement("select * from skills_details where skill_name=? and org_id=?");
						pst.setString(1, alEssentialSkill.get(i));
						pst.setInt(2, uF.parseToInt(getOrganisation()));
						rst = pst.executeQuery();
						boolean flag = false;
						while(rst.next()) {
							flag = true;
						}
						rst.close();
						pst.close();
						
						if(!flag) {
							pst = con.prepareStatement("insert into skills_details (skill_name, org_id) values (?,?)");
							pst.setString(1, alEssentialSkill.get(i));
							pst.setInt(2, uF.parseToInt(getOrganisation()));
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("select * from skills_details where skill_name=? and org_id=?");
							pst.setString(1, alEssentialSkill.get(i));
							pst.setInt(2, uF.parseToInt(getOrganisation()));
							rst = pst.executeQuery();
							while(rst.next()) {
								sbEssentialSkills.append(rst.getString("skill_id")+",");
							}
							rst.close();
							pst.close();
						}
					}
				}
				
				StringBuilder sbDesirableSkills = new StringBuilder();
				if(getDesirableSkillsText().trim().length()>0) {
					List<String> alDesirableSkill = Arrays.asList(getDesirableSkillsText().trim().split(","));
					for(int i=0; alDesirableSkill!=null && i<alDesirableSkill.size(); i++) {
						pst = con.prepareStatement("select * from skills_details where skill_name=? and org_id=?");
						pst.setString(1, alDesirableSkill.get(i));
						pst.setInt(2, uF.parseToInt(getOrganisation()));
						rst = pst.executeQuery();
						boolean flag = false;
						while(rst.next()) {
							flag = true;
						}
						rst.close();
						pst.close();
						
						if(!flag) {
							pst = con.prepareStatement("insert into skills_details (skill_name, org_id) values (?,?)");
							pst.setString(1, alDesirableSkill.get(i));
							pst.setInt(2, uF.parseToInt(getOrganisation()));
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("select * from skills_details where skill_name=? and org_id=?");
							pst.setString(1, alDesirableSkill.get(i));
							pst.setInt(2, uF.parseToInt(getOrganisation()));
							rst = pst.executeQuery();
							while(rst.next()) {
								sbDesirableSkills.append(rst.getString("skill_id")+",");
							}
							rst.close();
							pst.close();
						}
					}
				}
				
				pst = con.prepareStatement("select hiring_manager from recruitment_details where recruitment_id =? and (hiring_manager is not null or hiring_manager !='')");
				pst.setInt(1, uF.parseToInt(getRecruitmentID()));
				rst = pst.executeQuery();
				String strExistHiringManager = null;
				while(rst.next()){
					strExistHiringManager = rst.getString("hiring_manager");
				}
				rst.close();
				pst.close();
				
				
				String query = "update recruitment_details set designation_id=?,grade_id=?,no_position=?,effective_date=?,comments=?,wlocation=?," +
					"request_updated_date=?,request_updated_by=?,skills=?,services=?,dept_id=?,level_id=?,target_deadline=?,org_id=?," +
					"ideal_candidate=?,type_of_employment=?,sex=?,age=?," + //custum_designation=?,custum_grade=?,assessment_threshhold=?,
					"vacancy_type=?,give_justification=?,replacement_person_ids=?,temp_casual_give_jastification=?,essential_skills=?," +
					"reporting_to_person_ids=?,reportto_type=?,job_title=?,customer_id=?,hiring_manager=?,min_ctc=?,max_ctc=?,job_description=?," +
					"essential_skills_text=?,desirable_skills_text=?,min_exp=?,max_exp=?,priority_job_int=?,jd_category=?,technology_id=? where recruitment_id=?";
				pst = con.prepareStatement(query);
				pst.setInt(1, uF.parseToInt(getStrDesignationUpdate()));
				pst.setInt(2, uF.parseToInt(getStrGrade()));
				pst.setInt(3, uF.parseToInt(getPosition()));
				pst.setDate(4, uF.getDateFormat(getRdate(), DATE_FORMAT));
				pst.setString(5, getNotes());
				pst.setInt(6, uF.parseToInt(getLocation()));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
				pst.setString(9, getSkillsName(getSkills())+sbDesirableSkills.toString());
				pst.setInt(10, uF.parseToInt(getServices()));
				pst.setInt(11, depart_id);
				pst.setInt(12, uF.parseToInt(getStrLevel()));
				pst.setDate(13,uF.getDateFormat(getTargetdeadline(), DATE_FORMAT));
				pst.setInt(14,uF.parseToInt(getOrganisation()));
				pst.setString(15, getIdealCandidate());
//				pst.setString(16, getCustumdesignation());
//				pst.setString(17, getCustumgrade());
//				pst.setInt(18, uF.parseToInt(getAssessThreshhold()));
				pst.setString(16, getStrEmploymentType());
				pst.setString(17, getGender());
				pst.setString(18, getMinAge());
				pst.setInt(19, uF.parseToInt(getVacancy()));
				pst.setString(20, getAddtionalJastification());
				pst.setString(21, getEmpselected());
				pst.setString(22, getTempOrCasualJastification());
	//			pst.setString(26, getEssentialSkills());
				pst.setString(23, getSkillsName(getEssentialSkills())+sbEssentialSkills.toString());
				if(getReportToType() != null && getReportToType().equals("Other")) {
					pst.setString(24, getEmpselected1());
				} else {
					pst.setString(24, ","+strSessionEmpId+",");
				}
				pst.setString(25, getReportToType());
				pst.setString(26, getJobTitle());
				if(uF.parseToInt(getStrCustomer())>0) {
					pst.setInt(27, uF.parseToInt(getStrCustomer()));
				} else {
					pst.setInt(27, intClientId);
				}
				pst.setString(28, getSkillsName(getHiringManager()));
				pst.setDouble(29, uF.parseToDouble(getStrMinCTC()));
				pst.setDouble(30, uF.parseToDouble(getStrMaxCTC()));
				pst.setString(31, getJobDescription());
				pst.setString(32, getEssentialSkillsText().trim());
				pst.setString(33, getDesirableSkillsText().trim());
				double dblMinExp = uF.parseToDouble(getMinYear()+"."+getMinMonth());
				double dblMaxExp = uF.parseToDouble(getMaxYear()+"."+getMaxMonth());
				pst.setDouble(34, dblMinExp);
				pst.setDouble(35, dblMaxExp);
				//=====start parvez on 28/06/2021====
				pst.setInt(36, uF.parseToInt(getPriority()));
				//=====end parvez on 28/06/2021====
				pst.setInt(37, uF.parseToInt(getStrCategory()));//Created By Dattatray Date : 21-Aug-2021
				pst.setInt(38, uF.parseToInt(getStrTechnology()));// Created By Dattatray Date : 21-Aug-2021
				pst.setInt(39, uF.parseToInt(getRecruitmentID()));// Created By Dattatray Date : 12-July-21 Note: index correction
	//			System.out.println("prining query ==="+pst);
				int check = pst.executeUpdate();
				pst.close();
			
				String queryy = "select added_by from recruitment_details where recruitment_id=?";
				pst = con.prepareStatement(queryy);
				pst.setInt(1, uF.parseToInt(recruitmentID));
				rst = pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					setStrAddedBy(rst.getString("added_by"));
				}
				rst.close();
				pst.close();
				
				String designationName = getDesignationNameById(con, uF, recruitmentID);
				session.setAttribute(MESSAGE, SUCCESSM+""+designationName+" designation job requirement has been updated successfully."+END);
				
				String strDomain = request.getServerName().split("\\.")[0];
				setDomain(strDomain);
				Thread th = new Thread(this);
				th.start();
				
				if(getHiringManager() !=null){
					String strAddedBy = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
					if(hmEmpEmail == null) hmEmpEmail = new HashMap<String, String>();
					
					String strOrgName = CF.getOrgNameById(con, getOrganisation());
					
					List<String> alExistHrManager = new ArrayList<String>();
					if(strExistHiringManager!=null){
						String[] strTemp = strExistHiringManager.split(",");
						for(int i =0; strTemp!=null && i<strTemp.length; i++){
							if(uF.parseToInt(strTemp[i].trim())>0){
								alExistHrManager.add(strTemp[i].trim());
							}
						}
					}
					
					List<String> alHiringManager = Arrays.asList(getHiringManager());
					for (int i = 0; alHiringManager!=null && i < alHiringManager.size(); i++) {
						if(!alExistHrManager.contains(alHiringManager.get(i).trim()) && uF.parseToInt(alHiringManager.get(i).trim()) > 0 && hmEmpEmail.get(alHiringManager.get(i).trim()) !=null && hmEmpEmail.get(alHiringManager.get(i).trim()).indexOf("@") > 0){
							String strHiringManager = CF.getEmpNameMapByEmpId(con, alHiringManager.get(i).trim());
							String strHiringEmail = hmEmpEmail.get(alHiringManager.get(i).trim());
							String strSubject = "New Job Added";
							String strBody = "Dear "+uF.showData(strHiringManager, "")+"," + "<br><br>" + 
									"A job ("+uF.showData(getJobTitle(), "")+") has been added by "+uF.showData(strAddedBy, "")+"<br><br><br><br>" +
									"Thanks & Regards,<br>" +
									""+uF.showData(strOrgName, "")+"<br>" +
									"HR Team" + "<br>";
							CustomEmailer ce = new CustomEmailer(strHiringEmail, strSubject, strBody, strDomain);
							ce.sendCustomEmail();
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("in error return ERROR");
			return ERROR;
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
        
//		System.out.println("in error return success");
		return SUCCESS;

	}

	private String insertRecruitmentRequst(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		int depart_id = 0;

		try {
			con = db.makeConnection(con);

			int intClientId = 0;
			if(getStrOtherCustomer() != null && getStrOtherCustomer().trim().length()>0) {
				pst = con.prepareStatement("insert into client_details (client_name) values(?)");
				pst.setString(1, getStrOtherCustomer());
				pst.executeUpdate();
				pst.close();
				
				pst = con.prepareStatement("select max(client_id) as client_id from client_details");
				rst = pst.executeQuery();
				while(rst.next()) {
					intClientId = rst.getInt("client_id");
				}
				rst.close();
				pst.close();
			}
			
			String query1 = "select depart_id from employee_official_details where emp_id=?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
//			System.out.println("pst1 ===> "+pst1);
			while (rst.next()) {
				depart_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			if (depart_id > 0) {
				
				StringBuilder sbEssentialSkills = new StringBuilder();
				if(getEssentialSkillsText().trim().length()>0) {
					List<String> alEssentialSkill = Arrays.asList(getEssentialSkillsText().trim().split(","));
					for(int i=0; alEssentialSkill!=null && i<alEssentialSkill.size(); i++) {
						pst = con.prepareStatement("select * from skills_details where skill_name=? and org_id=?");
						pst.setString(1, alEssentialSkill.get(i));
						pst.setInt(2, uF.parseToInt(getOrganisation()));
						rst = pst.executeQuery();
						boolean flag = false;
						while(rst.next()) {
							flag = true;
						}
						rst.close();
						pst.close();
						
						if(!flag) {
							pst = con.prepareStatement("insert into skills_details (skill_name, org_id) values (?,?)");
							pst.setString(1, alEssentialSkill.get(i));
							pst.setInt(2, uF.parseToInt(getOrganisation()));
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("select * from skills_details where skill_name=? and org_id=?");
							pst.setString(1, alEssentialSkill.get(i));
							pst.setInt(2, uF.parseToInt(getOrganisation()));
							rst = pst.executeQuery();
							while(rst.next()) {
								sbEssentialSkills.append(rst.getString("skill_id")+",");
							}
							rst.close();
							pst.close();
						}
					}
				}
				
				StringBuilder sbDesirableSkills = new StringBuilder();
				if(getDesirableSkillsText().trim().length()>0) {
					List<String> alDesirableSkill = Arrays.asList(getDesirableSkillsText().trim().split(","));
					for(int i=0; alDesirableSkill!=null && i<alDesirableSkill.size(); i++) {
						pst = con.prepareStatement("select * from skills_details where skill_name=? and org_id=?");
						pst.setString(1, alDesirableSkill.get(i));
						pst.setInt(2, uF.parseToInt(getOrganisation()));
						rst = pst.executeQuery();
						boolean flag = false;
						while(rst.next()) {
							flag = true;
						}
						rst.close();
						pst.close();
						
						if(!flag) {
							pst = con.prepareStatement("insert into skills_details (skill_name, org_id) values (?,?)");
							pst.setString(1, alDesirableSkill.get(i));
							pst.setInt(2, uF.parseToInt(getOrganisation()));
							pst.execute();
							pst.close();
							
							pst = con.prepareStatement("select * from skills_details where skill_name=? and org_id=?");
							pst.setString(1, alDesirableSkill.get(i));
							pst.setInt(2, uF.parseToInt(getOrganisation()));
							rst = pst.executeQuery();
							while(rst.next()) {
								sbDesirableSkills.append(rst.getString("skill_id")+",");
							}
							rst.close();
							pst.close();
						}
					}
				}
				
				String query = "insert into recruitment_details(designation_id,grade_id,no_position,effective_date,comments,wlocation," +
				"entry_date,added_by,skills,services,dept_id,level_id,priority_job_int,target_deadline,org_id,ideal_candidate," +
				"requirement_status,type_of_employment," + //assessment_threshhold,sex,age,
				"vacancy_type,give_justification,replacement_person_ids,temp_casual_give_jastification,essential_skills," +
				"reporting_to_person_ids,reportto_type,sex,age,job_title,customer_id,hiring_manager,job_description," +
				"essential_skills_text,desirable_skills_text,status,job_approval_status,req_form_type,min_exp,max_exp,jd_category,technology_id)" + //
				"values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)"; //
				pst = con.prepareStatement(query);
				pst.setInt(1, uF.parseToInt(getStrDesignationUpdate()));
				pst.setInt(2, uF.parseToInt(getStrGrade()));
				pst.setInt(3, uF.parseToInt(getPosition()));
				pst.setDate(4, uF.getDateFormat(getRdate(), DATE_FORMAT));
				pst.setString(5, getNotes());
				pst.setInt(6, uF.parseToInt(getLocation()));
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
	//			System.out.println("IN insert getSkillName ===> "+getSkillsName(getSkills()));
				pst.setString(9, getSkillsName(getSkills())+sbDesirableSkills.toString());
				pst.setInt(10, uF.parseToInt(getServices()));
				pst.setInt(11, depart_id);
				pst.setInt(12, uF.parseToInt(getStrLevel()));
				pst.setInt(13, uF.parseToInt(getPriority()));
				pst.setDate(14,uF.getDateFormat(getTargetdeadline(), DATE_FORMAT));
				pst.setInt(15, uF.parseToInt(getOrganisation()));
				pst.setString(16, getIdealCandidate());
				pst.setString(17, "generate");
	//			pst.setInt(20, uF.parseToInt(getAssessThreshhold()));
				pst.setString(18, getStrEmploymentType());
	//			pst.setString(22, getSex());
	//			pst.setString(23, getAge());
				pst.setInt(19, uF.parseToInt(getVacancy()));
				pst.setString(20, getAddtionalJastification());
				pst.setString(21, getEmpselected());
				pst.setString(22, getTempOrCasualJastification());
	//			pst.setString(25, getEssentialSkills());
				pst.setString(23, getSkillsName(getEssentialSkills())+sbEssentialSkills.toString());
				if(getReportToType() != null && getReportToType().equals("Other")){
					pst.setString(24, getEmpselected1());
				} else {
					pst.setString(24, ","+strSessionEmpId+",");
				}
				pst.setString(25, getReportToType());
				pst.setString(26, getGender());
				pst.setString(27, getMinAge());
				pst.setString(28, getJobTitle());
				if(uF.parseToInt(getStrCustomer()) > 0) {
					pst.setInt(29, uF.parseToInt(getStrCustomer()));
				} else {
					pst.setInt(29, intClientId);
				}
				pst.setString(30, getSkillsName(getHiringManager()));
				pst.setString(31, getJobDescription());
				pst.setString(32, getEssentialSkillsText().trim());
				pst.setString(33, getDesirableSkillsText().trim());
				pst.setInt(34, 1);
				pst.setInt(35, 1);
				pst.setInt(36, 1);
				double dblMinExp = uF.parseToDouble(getMinYear()+"."+getMinMonth());
				double dblMaxExp = uF.parseToDouble(getMaxYear()+"."+getMaxMonth());
				pst.setDouble(37, dblMinExp);
				pst.setDouble(38, dblMaxExp);
				pst.setInt(39, uF.parseToInt(getStrCategory()));//Created By Dattatray Date : 21-Aug-2021
				pst.setInt(40, uF.parseToInt(getStrTechnology()));// Created By Dattatray Date : 21-Aug-2021
	//			System.out.println("pst insert recruitment==>"+pst);
				int affectedRows = pst.executeUpdate();
				pst.close();
				
				String recruitID = null;
				pst = con.prepareStatement("select max(recruitment_id) as recruitment_id from recruitment_details");
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				while (rst.next()) {
					recruitID = rst.getString("recruitment_id");
				}
				rst.close();
				pst.close();
				
				String jobCode = "";
				pst = con.prepareStatement("select wloacation_code,designation_code,custum_designation from recruitment_details " +
					" left join designation_details using (designation_id) join work_location_info on (wlocation_id = wlocation)" +
					" where recruitment_id=? ");
				pst.setInt(1, uF.parseToInt(recruitID));
				rst = pst.executeQuery();
//				System.out.println("new Date ===> " + new Date());
				if(rst.next()){
					if(rst.getString("designation_code")==null)
						jobCode+=rst.getString("wloacation_code")+"-NEW";
					else
						jobCode+=rst.getString("wloacation_code")+"-"+rst.getString("designation_code");
				}
				rst.close();
				pst.close();
				
				pst = con.prepareStatement("select count(*) as count from recruitment_details where job_code like '"+jobCode+"%'");
				rst = pst.executeQuery();
				
				while (rst.next()) {
					int count=uF.parseToInt(rst.getString("count"));
						count++;
						// Conversion to 3 decimal places
						DecimalFormat decimalFormat = new DecimalFormat();
						decimalFormat.setMinimumIntegerDigits(3);
					jobCode+="-"+decimalFormat.format(count);
				}
				rst.close();
				pst.close();
				
				pst = con.prepareStatement("update recruitment_details set job_code=? where recruitment_id=?");
				pst.setString(1, jobCode);
				pst.setInt(2, uF.parseToInt(recruitID));
				pst.execute();
				pst.close();
				
				int existingEmpCount=0;
				
				if(uF.parseToInt(getStrDesignationUpdate())!=0){
					pst=con.prepareStatement("Select count(*) as count from employee_official_details join grades_details" +
							" using (grade_id) where designation_id=?");
					pst.setInt(1, uF.parseToInt(getStrDesignationUpdate()));
					rst=pst.executeQuery();
//					System.out.println("new Date ===> " + new Date());
					while(rst.next()){
						existingEmpCount=rst.getInt("count");	
					}
					rst.close();
					pst.close();
				}
				
				pst=con.prepareStatement("update recruitment_details set existing_emp_count="+existingEmpCount+" where " +
						"recruitment_id=(select max(recruitment_id) from recruitment_details)");
				pst.execute();
				pst.close();
			
				String designationName = getDesignationNameById(con, uF, recruitID);
				session.setAttribute(MESSAGE, SUCCESSM+"New job requirement has been created successfully for "+designationName+" designation."+END);

				String strDomain = request.getServerName().split("\\.")[0];
				setDomain(strDomain);
				Thread th = new Thread(this);
				th.start();
				
				if(getHiringManager() !=null){
					String strAddedBy = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
					Map<String, String> hmEmpEmail = CF.getEmpEmailMap(con);
					if(hmEmpEmail == null) hmEmpEmail = new HashMap<String, String>();
					
					String strOrgName = CF.getOrgNameById(con, getOrganisation());
					
					List<String> alHiringManager = Arrays.asList(getHiringManager());
					for (int i = 0; alHiringManager!=null && i < alHiringManager.size(); i++) {
						if(uF.parseToInt(alHiringManager.get(i).trim()) > 0 && hmEmpEmail.get(alHiringManager.get(i).trim()) !=null && hmEmpEmail.get(alHiringManager.get(i).trim()).indexOf("@") > 0){
							String strHiringManager = CF.getEmpNameMapByEmpId(con, alHiringManager.get(i).trim());
							String strHiringEmail = hmEmpEmail.get(alHiringManager.get(i).trim());
							String strSubject = "New Job Added";
							String strBody = "Dear "+uF.showData(strHiringManager, "")+"," + "<br><br>" + 
									"A job ("+uF.showData(getJobTitle(), "")+") has been added by "+uF.showData(strAddedBy, "")+"<br><br><br><br>" +
									"Thanks & Regards,<br>" +
									""+uF.showData(strOrgName, "")+"<br>" +
									"HR Team" + "<br>";
							CustomEmailer ce = new CustomEmailer(strHiringEmail, strSubject, strBody, strDomain);
							ce.sendCustomEmail();
						}
					}
				}
			} 

		} catch (Exception e) {
//			System.out.println("in error return error");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		System.out.println("in error return success");
		return SUCCESS;

	}

	private String[] splitString(String st) {
		if (st.equals("") || st.equals("0")) {
			st = "0.0";
		}
		st = st.replace('.', '_');
		String str[] = st.split("_");
		return str;
	}

	private String removeNUll(String strNull) {

		if (strNull == null) {
			strNull = "";
		}
		return strNull;
	}
	
	private String getDesignationNameById(Connection con, UtilityFunctions uF, String recruitId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
//		String requirementName = null;
		String designationName = null;
		try {
			pst = con.prepareStatement("select job_code,designation_id from recruitment_details where recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(recruitId));
			rst = pst.executeQuery();
			Map<String, String> hmDesignation = CF.getDesigMap(con);
			String desigId = null;
			while (rst.next()) {
				desigId = rst.getString("designation_id");
//					requirementName = rst.getString("job_code");
			}
			rst.close();
			pst.close();
			
			designationName = hmDesignation.get(desigId);
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return designationName;
	}
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	@Override
	public void run() {
	
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		UtilityFunctions uF = new UtilityFunctions();
		if (recruitmentID != null && strInsert != null && uF.parseToInt(recruitmentID) == 0){

			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			String Level_name = getLevelName(con,getStrLevel());
	
			String designation_name = getDesignationName(con,getStrDesignationUpdate());
	
			String gradename = getGradeName(con,getStrGrade());
			String locationname = getLocationName(con,getLocation());
	
			for (int i = 0; hrIDList != null && i < hrIDList.size(); i++) {
				Map<String, String> hmEmpInner = hmEmpInfo.get(hrIDList.get(i));
//				System.out.println(i+" hrdlist "+hrIDList.get(i));
				Notifications nF = new Notifications(N_RECRUITMENT_REQUEST, CF);
				nF.setDomain(getStrDomain());
				nF.request = request;
				nF.setStrEmpId(hrIDList.get(i));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
	
				nF.setStrRecruitmentDesignation(designation_name);
				nF.setStrRecruitmentGrade(gradename);
				nF.setStrRecruitmentLevel(Level_name);
				nF.setStrRecruitmentPosition(getPosition());
				nF.setStrRecruitmentWLocation(locationname);
				nF.setStrEmpFname(hmEmpInner != null ? hmEmpInner.get("FNAME") : "");
				nF.setStrEmpLname(hmEmpInner != null ? hmEmpInner.get("LNAME") : "");
				nF.setEmailTemplate(true);
				nF.sendNotifications();
			}
			db.closeConnection(con);
		}else if (recruitmentID != null && strInsert != null && uF.parseToInt(recruitmentID) >= 0){
			
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			String Level_name = getLevelName(con,getStrLevel());
	
			String designation_name = getDesignationName(con,getStrDesignationUpdate());
			String gradename = getGradeName(con,getStrGrade());
			String locationname = getLocationName(con,getLocation());
	
			
			
//			for (int i = 0; hrIDList != null && i < hrIDList.size(); i++) {
				Map<String, String> hmEmpInner = hmEmpInfo.get(getStrAddedBy());
//				System.out.println(" Added By ===> "+getStrAddedBy());
				Notifications nF = new Notifications(N_RECRUITMENT_REQUEST_EDIT, CF);
				nF.setDomain(getStrDomain());
				nF.request = request;
				nF.setStrEmpId(getStrAddedBy());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
	
				nF.setStrRecruitmentDesignation(designation_name);
				nF.setStrRecruitmentGrade(gradename);
				nF.setStrRecruitmentLevel(Level_name);
				nF.setStrRecruitmentPosition(getPosition());
				nF.setStrRecruitmentWLocation(locationname);
				nF.setStrEmpFname(hmEmpInner != null ? hmEmpInner.get("FNAME") : "");
				nF.setStrEmpLname(hmEmpInner != null ? hmEmpInner.get("LNAME") : "");
				nF.setEmailTemplate(true);
				
				nF.sendNotifications();
//			}
			db.closeConnection(con);
		}
	}

	private String getLocationName(Connection con,String location2) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		String location_name = "";

		
		try {
			
			pst = con.prepareStatement("SELECT * FROM work_location_info where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(location2));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				location_name = rst.getString("wlocation_name");
			}
			rst.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return location_name;
	}

	
	private String getGradeName(Connection con,String empGrade2) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		String grade_name = "";

		try {
			pst = con.prepareStatement("select * from grades_details where grade_id=?");
			pst.setInt(1, uF.parseToInt(empGrade2));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				grade_name = "[" + rst.getString("grade_code") + "] "
						+ rst.getString("grade_name");
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return grade_name;
	}
	

	private String getDesignationName(Connection con,String strDesignationUpdate2) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		String desig_name = "";

		try {
			pst = con.prepareStatement("SELECT * FROM designation_details where designation_id=?");
			pst.setInt(1, uF.parseToInt(strDesignationUpdate2));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				desig_name = "[" + rst.getString("designation_code") + "] " + rst.getString("designation_name");
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return desig_name;
	}

	
	private String getLevelName(Connection con,String strLevel1) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		String level_name = "";

		try {
			pst = con.prepareStatement("SELECT * FROM level_details where level_id=?");
			pst.setInt(1, uF.parseToInt(strLevel1));
			rst = pst.executeQuery();
//			System.out.println("new Date ===> " + new Date());
			while (rst.next()) {
				level_name = "[" + rst.getString("level_code") + "] " + rst.getString("level_name");
			}
			rst.close();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} 
		return level_name;
	}

	
	private double parseDouble(String year, String month) {
		try {
			if ((year != null && year.length() > 0 && !year.equalsIgnoreCase("NULL")) && (month != null && month.length() > 0 && !month.equalsIgnoreCase("NULL"))) {
				year = year.trim();
				year = year.replaceAll(" ", "");

				month = month.trim();
				month = month.replaceAll(" ", "");
				if (year.length() == 1) {
					year = "0" + year;
				}

				if (month.length() == 1) {
					month = "0" + month;
				}
				return Double.parseDouble(year + "." + month);
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
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

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public List<FillSkills> getSkillslist() {
		return skillslist;
	}

	public void setSkillslist(List<FillSkills> skillslist) {
		this.skillslist = skillslist;
	}

	public List<FillLevel> getLevelslist() {
		return levelslist;
	}

	public void setLevelslist(List<FillLevel> levelslist) {
		this.levelslist = levelslist;
	}
	
	
	private String getSkillsName(String[] skills){
		UtilityFunctions uF =new UtilityFunctions();
		StringBuilder skillName = null;
		for(int i=0;skills!=null && i<skills.length;i++) {
			if(uF.parseToInt(skills[i].trim()) > 0) {
				if(skillName == null) {
					skillName = new StringBuilder();
					skillName.append(","+skills[i].trim()+","); 
				} else {
					skillName.append(skills[i].trim()+",");
				}
			}
		}
		if(skillName == null) {
			skillName = new StringBuilder();
		}
//	 System.out.println("skill name=="+skillName);
		return skillName.toString();
	}
	
	/*String getSkillsName(String[] skills){
		String skillName="";
		for(int i=0;skills!=null && i<skills.length;i++) {
			if(i==0)
				skillName=skills[i].trim();
			else
				skillName+=","+skills[i].trim();
		}
		return skillName;
	}*/

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
		
	
	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}
	
    
	public String[] getSkills() {
		return skills;
	}

	public void setSkills(String[] skills) {
		this.skills = skills;
	}
	
	public String[] getHiringManager() {
		return hiringManager;
	}

	public void setHiringManager(String[] hiringManager) {
		this.hiringManager = hiringManager;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getStrCustomer() {
		return strCustomer;
	}

	public void setStrCustomer(String strCustomer) {
		this.strCustomer = strCustomer;
	}

	public String getMinYear() {
		return minYear;
	}

	public void setMinYear(String minYear) {
		this.minYear = minYear;
	}

	public String getMinMonth() {
		return minMonth;
	}

	public void setMinMonth(String minMonth) {
		this.minMonth = minMonth;
	}

	public String getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(String maxYear) {
		this.maxYear = maxYear;
	}

	public String getMaxMonth() {
		return maxMonth;
	}

	public void setMaxMonth(String maxMonth) {
		this.maxMonth = maxMonth;
	}

	public String[] getEssentialSkills() {
		return essentialSkills;
	}

	public void setEssentialSkills(String[] essentialSkills) {
		this.essentialSkills = essentialSkills;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	

	public List<String> getHrIDList() {
		return hrIDList;
	}

	public void setHrIDList(List<String> hrIDList) {
		this.hrIDList = hrIDList;
	}

	public String getRecruitmentID() {
		return recruitmentID;
	}

	public void setRecruitmentID(String recruitmentID) {
		this.recruitmentID = recruitmentID;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}



	public String getManlocation() {
		return manlocation;
	}

	public void setManlocation(String manlocation) {
		this.manlocation = manlocation;
	}


	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}

	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getRdate() {
		return rdate;
	}

	public void setRdate(String rdate) {
		this.rdate = rdate;
	}

	public String getDesig() {
		return desig;
	}

	public void setDesig(String desig) {
		this.desig = desig;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getOrgID() {
		return orgID;
	}

	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}

	public String getWlocID() {
		return wlocID;
	}

	public void setWlocID(String wlocID) {
		this.wlocID = wlocID;
	}

	public String getDesigID() {
		return desigID;
	}

	public void setDesigID(String desigID) {
		this.desigID = desigID;
	}

	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getFdate() {
		return fdate;
	}

	public void setFdate(String fdate) {
		this.fdate = fdate;
	}

	public String getTdate() {
		return tdate;
	}

	public void setTdate(String tdate) {
		this.tdate = tdate;
	}

	public String getStrAddedBy() {
		return strAddedBy;
	}

	public void setStrAddedBy(String strAddedBy) {
		this.strAddedBy = strAddedBy;
	}

	public String getFrmPage() {
		return frmPage;
	}

	public void setFrmPage(String frmPage) {
		this.frmPage = frmPage;
	}

	public List<FillSkills> getEssentialSkillsList() {
		return essentialSkillsList;
	}

	public void setEssentialSkillsList(List<FillSkills> essentialSkillsList) {
		this.essentialSkillsList = essentialSkillsList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}
	
	public List<FillEmployee> getHrAndGlobalHrList() {
		return hrAndGlobalHrList;
	}

	public void setHrAndGlobalHrList(List<FillEmployee> hrAndGlobalHrList) {
		this.hrAndGlobalHrList = hrAndGlobalHrList;
	}

	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}
	public String getStrInsert() {
		return strInsert;
	}

	public void setStrInsert(String strInsert) {
		this.strInsert = strInsert;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getStrOtherCustomer() {
		return strOtherCustomer;
	}

	public void setStrOtherCustomer(String strOtherCustomer) {
		this.strOtherCustomer = strOtherCustomer;
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillDesig> getDesignationList() {
		return designationList;
	}

	public void setDesignationList(List<FillDesig> designationList) {
		this.designationList = designationList;
	}

	public List<FillOrganisation> getOrgList1() {
		return orgList1;
	}

	public void setOrgList1(List<FillOrganisation> orgList1) {
		this.orgList1 = orgList1;
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

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public List<FillEmploymentType> getEmploymentList() {
		return employmentList;
	}

	public void setEmploymentList(List<FillEmploymentType> employmentList) {
		this.employmentList = employmentList;
	}

	public String getEssentialSkillsText() {
		return essentialSkillsText;
	}

	public void setEssentialSkillsText(String essentialSkillsText) {
		this.essentialSkillsText = essentialSkillsText;
	}

	public String getDesirableSkillsText() {
		return desirableSkillsText;
	}

	public void setDesirableSkillsText(String desirableSkillsText) {
		this.desirableSkillsText = desirableSkillsText;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	public String getServices() {
		return services;
	}


	public void setServices(String services) {
		this.services = services;
	}


	public String getStrEmployment() {
		return strEmployment;
	}


	public void setStrEmployment(String strEmployment) {
		this.strEmployment = strEmployment;
	}


	public String getStrSex() {
		return strSex;
	}


	public void setStrSex(String strSex) {
		this.strSex = strSex;
	}


	public String getStrAge() {
		return strAge;
	}


	public void setStrAge(String strAge) {
		this.strAge = strAge;
	}


	public String getStrMinCTC() {
		return strMinCTC;
	}


	public void setStrMinCTC(String strMinCTC) {
		this.strMinCTC = strMinCTC;
	}


	public String getStrMaxCTC() {
		return strMaxCTC;
	}


	public void setStrMaxCTC(String strMaxCTC) {
		this.strMaxCTC = strMaxCTC;
	}


	public String getTargetdeadline() {
		return targetdeadline;
	}


	public void setTargetdeadline(String targetdeadline) {
		this.targetdeadline = targetdeadline;
	}


	public String getStrEmploymentType() {
		return strEmploymentType;
	}


	public void setStrEmploymentType(String strEmploymentType) {
		this.strEmploymentType = strEmploymentType;
	}


	public String getTempOrCasualJastification() {
		return tempOrCasualJastification;
	}


	public void setTempOrCasualJastification(String tempOrCasualJastification) {
		this.tempOrCasualJastification = tempOrCasualJastification;
	}


	public String getIdealCandidate() {
		return idealCandidate;
	}


	public void setIdealCandidate(String idealCandidate) {
		this.idealCandidate = idealCandidate;
	}


	public String getVacancy() {
		return vacancy;
	}


	public void setVacancy(String vacancy) {
		this.vacancy = vacancy;
	}


	public String getAddtionalJastification() {
		return addtionalJastification;
	}


	public void setAddtionalJastification(String addtionalJastification) {
		this.addtionalJastification = addtionalJastification;
	}


	public String getEmpselected() {
		return empselected;
	}


	public void setEmpselected(String empselected) {
		this.empselected = empselected;
	}


	public String getEmpselected1() {
		return empselected1;
	}


	public void setEmpselected1(String empselected1) {
		this.empselected1 = empselected1;
	}


	public String getReportToType() {
		return reportToType;
	}


	public void setReportToType(String reportToType) {
		this.reportToType = reportToType;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getMinAge() {
		return minAge;
	}


	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}


	public String getStrCategory() {
		return strCategory;
	}


	public void setStrCategory(String strCategory) {
		this.strCategory = strCategory;
	}


	public String getStrTechnology() {
		return strTechnology;
	}


	public void setStrTechnology(String strTechnology) {
		this.strTechnology = strTechnology;
	}


	public List<FillRecruitmentTechnology> getTechnologyList() {
		return technologyList;
	}


	public void setTechnologyList(List<FillRecruitmentTechnology> technologyList) {
		this.technologyList = technologyList;
	}
	
}