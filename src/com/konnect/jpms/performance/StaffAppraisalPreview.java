package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.sun.net.httpserver.Authenticator.Success;

public class StaffAppraisalPreview implements ServletRequestAware, IStatements, Runnable {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	private String id;
	private String empID;
	private String userType;
	private String appFreqId;
	private String role;
	private String submit;
	
	private String dataType;	//created by parvez: 13-07-2022
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/StaffAppraisalPreview.jsp");
		request.setAttribute(TITLE, "Review Preview");
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		UtilityFunctions uF = new UtilityFunctions();
//		String submit = request.getParameter("submit");
		
//		System.out.println("Roll ===>> " + getRole()+"---userType="+userType);
		if (getSubmit() == null) {
			getEmployeeAssignedKRAAndGoalTarget(uF);
			viewProfile(empID);
			getanswerTypeMap(uF);
			// getLevelStep(uF);
			getQuestionSubType(uF);
			getAppraisalDetail(uF);
		//===start parvez date: 07-07-2022===	
			getAppraisalReopenStatus(uF);
		//===end parvez date: 07-07-2022===
			getEmployyDetailsList(uF);
			getAppraisalQuestionMap(uF);
			getLevelStatus(uF);
			getCurrentLevelAnswer(uF);
			checkCurrentLevelExistForCurrentEmp(uF);
			getSummary(uF);
			
		//===start parvez date: 13-07-2022===	
			if(getDataType()!=null && getDataType().equals("Reviewer Feedback")){
				getReviewerFeedbackDetails(uF);
			}
		//===end parvez date: 13-07-2022===	
			
			return getLevelQuestion(uF);
		} else if (getSubmit()!=null && getSubmit().equals("Submit")) {
//			System.out.println("getDataType="+getDataType());
			if(getDataType()!=null && getDataType().equals("Reviewer Feedback")){
				return submitReviewerFeedback(uF);
			} else{
				return submitAllQuestionsAnswersOfReview(uF);
			}
//			return submitAllQuestionsAnswersOfReview(uF);
		} else {
			String strDomain = request.getServerName().split("\\.")[0];
			setDomain(strDomain);
			Thread th = new Thread(this);
			th.start();
			return "update";
		}
	}
	
	
	private void getEmployeeAssignedKRAAndGoalTarget(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);

			List<String> alKRAIds = new ArrayList<String>();
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_type in ("+kraTypes+") and emp_ids like '%,"+getEmpID()+",%'");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				alKRAIds.add(rs.getString("goal_kra_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alKRAIds", alKRAIds);
			
			List<String> alGoarTargetIds = new ArrayList<String>();
			Map<String, List<String>> alGoalTitle = new HashMap<String, List<String>>();			//===added by parvez date: 27-12-2021===
			
			pst = con.prepareStatement("select * from goal_details where emp_ids like '%,"+getEmpID()+",%'");
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				alGoarTargetIds.add(rs.getString("goal_id"));
		//===start parvez date: 27-12-2021===
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("goal_title"));		//0
				innerList.add(rs.getString("approve_status"));	//1
				innerList.add(rs.getString("emp_ids"));			//2
				alGoalTitle.put(rs.getString("goal_id"), innerList);
		//===end parvez date: 27-12-2021===
			}
			rs.close();
			pst.close();
			request.setAttribute("alGoarTargetIds", alGoarTargetIds);
		//===start parvez date: 27-12-2021===	
			request.setAttribute("alGoalTitle", alGoalTitle);
		//===end parvez date: 27-12-2021===
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void viewProfile(String strEmpIdReq) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			CF.getEmpProfileDetail(con, request, session, CF, uF, strSessionUserType, strEmpIdReq);
			request.setAttribute(TITLE, "Review Preview");
			
			CF.getElementList(con, request);
			CF.getAttributes(con, request, strEmpIdReq);
			
			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			request.setAttribute("alSkills", alSkills);
			
//			request.setAttribute("alActivityDetails", alActivityDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
//		return SUCCESS;

	}
		/*

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		List<List<String>> alSkills = new ArrayList<List<String>>();
		List<List<String>> alHobbies;
		List<List<String>> alLanguages;
		List<List<String>> alEducation;
		List<List<Object>> alDocuments;
		List<List<String>> alActivityDetails;

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hm = new HashMap<String, String>();
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmGrades = CF.getGradeMap(con);
			Map<String, String> hmDeptMap = CF.getDeptMap(con);
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmDesigMap = CF.getDesigMap(con);
			Map<String, String> hmServices = new HashMap<String, String>();
			hmServices = CF.getServicesMap(con, true);
			
			
			pst = con.prepareStatement("Select * from ( Select * from ( Select * from employee_personal_details epd left join employee_official_details eod on epd.emp_per_id=eod.emp_id where epd.emp_per_id=?) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id");
			if (strEmpIdReq != null) {
				pst.setInt(1, uF.parseToInt(strEmpIdReq));
			} else {
				pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			}
			rs = pst.executeQuery();

			String strEmpOffId = null;
			String strEmpId = null;
			String[] strServices = null;
			while (rs.next()) {
				strEmpOffId = rs.getString("emp_off_id");
				strEmpId = rs.getString("emp_per_id");

				System.out.println("strUserType===>"+strUserType);
				
				if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE) || !strUserType.equalsIgnoreCase(ARTICLE) || !strUserType.equalsIgnoreCase(CONSULTANT)){
					request.setAttribute(TITLE, rs.getString("emp_fname") + " "+ rs.getString("emp_lname")+"'s Profile");
				}
				
				
				hm.put("EMPCODE", rs.getString("empcode"));
				hm.put("NAME", uF.showData(rs.getString("salutation"), "")+rs.getString("emp_fname") + " "+ rs.getString("emp_lname")); 
				hm.put("ADDRESS", rs.getString("emp_address1") + " "+ rs.getString("emp_address2"));
				hm.put("CITY", rs.getString("emp_city_id"));
				hm.put("STATE", rs.getString("state_name"));
				hm.put("COUNTRY", rs.getString("country_name"));
				hm.put("PINCODE", rs.getString("emp_pincode"));
				hm.put("CONTACT", rs.getString("emp_contactno"));
				hm.put("CONTACT_MOB", rs.getString("emp_contactno_mob"));
				hm.put("IMAGE", rs.getString("emp_image"));
				hm.put("EMAIL", rs.getString("emp_email"));
				hm.put("EMAIL_SEC", rs.getString("emp_email_sec"));
				hm.put("ORG_ID", rs.getString("org_id"));
				
				if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0){
					hm.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				}else{
					hm.put("EMP_EMAIL", rs.getString("emp_email"));
				}
				
				hm.put("SKYPE_ID", rs.getString("skype_id"));
				hm.put("DESIGNATION", hmEmpDesigMap.get(rs.getString("emp_per_id")));
				hm.put("LEVEL", hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))));
				hm.put("GRADE", hmGrades.get(rs.getString("grade_id")));
				hm.put("GENDER", rs.getString("emp_gender"));
				
				hm.put("PAN_NO", rs.getString("emp_pan_no"));
				hm.put("PF_NO", rs.getString("emp_pf_no"));
				
				hm.put("EMERGENCY_NAME", rs.getString("emergency_contact_name"));
				hm.put("EMERGENCY_NO", rs.getString("emergency_contact_no"));
				
				hm.put("DOB", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
				hm.put("MARITAL_STATUS", rs.getString("marital_status"));
				hm.put("PASSPORT_NO", rs.getString("passport_no"));
				hm.put("PASSPORT_EXPIRY", uF.getDateFormat(rs.getString("passport_expiry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				
				Map<String, String> hmWLocation = hmWorkLocationMap.get(rs.getString("wlocation_id"));
				if (hmWLocation == null) {
					hmWLocation = new HashMap<String, String>();
				}

				hm.put("WL_NAME", hmWLocation.get("WL_NAME"));
				hm.put("WL_CITY", hmWLocation.get("WL_CITY"));

				hm.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE,CF.getStrReportDateFormat()));
				hm.put("EMP_TYPE", uF.stringMapping(rs.getString("emptype")));

				hm.put("DEPT", hmDeptMap.get(rs.getString("depart_id")));
				hm.put("SUPER_ID", rs.getString("supervisor_emp_id"));
				
				String str = rs.getString("service_id");
				StringBuilder sbServices = new StringBuilder();
				
				
				if (str != null && str.length() > 0) {
					strServices = str.split(",");
					for (int i = 0; i < strServices.length; i++) {

						sbServices.append((String) hmServices.get(strServices[i]));
						if (i < strServices.length - 1)
							sbServices.append(", ");
					}
				}
				
				strServices = new String[1];
				strServices[0] = "0";

				hm.put("COST_CENTRE", sbServices.toString());
				hm.put("ROSTER_DEPENDENCY", uF.showYesNo(rs.getString("is_roster")));
				hm.put("ALLOWANCE", uF.showYesNo(rs.getString("first_aid_allowance")));

				if(rs.getString("joining_date")!=null){
					uF.getTimeDuration(rs.getString("joining_date"), CF, uF, request);
				}
			}
			
			pst = con.prepareStatement("select * from employee_activity_details where emp_id = ? and activity_id = 6 order by effective_date desc limit 2");
			if (strEmpIdReq != null) {
				pst.setInt(1, uF.parseToInt(strEmpIdReq));
			} else {
				pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			}
			rs = pst.executeQuery();
			int i=0;
			while(rs.next()){
				if(i==0){
					hm.put("PREV_PROMOTION", uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				}else{
					hm.put("PREV_DESIGNATION", hmDesigMap.get(rs.getString("desig_id")));
				}
				i++;
			}
			
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.emp_id = ?");
			pst.setInt(1, uF.parseToInt((String)hm.get("SUPER_ID")));
			rs = pst.executeQuery();
			while(rs.next()){
				hm.put("SUPER_CODE", rs.getString("empcode"));
				hm.put("SUPER_NAME", rs.getString("emp_fname") + " "+ rs.getString("emp_lname"));
			}
			
			request.setAttribute("myProfile", hm);

//			Map<String, Map<String, String>> hmPayrollPolicy = new HashMap<String, Map<String, String>>();
//			hmPayrollPolicy = CF.getDailyRates(con, (String) session.getAttribute("EMPID"));
//
//			request.setAttribute("hmPayrollPolicy", hmPayrollPolicy);
			request.setAttribute("hmServices", hmServices);

			int intEmpIdReq = uF.parseToInt(strEmpIdReq);
			
			request.setAttribute("myProfile", hm);
			
			
			EmpDashboard objEmpDashboard = new EmpDashboard(request, session, CF, strEmpId);
			objEmpDashboard.getEmpKPI(con, uF, strEmpIdReq);
			objEmpDashboard.getResignationStatus(con, uF);
			objEmpDashboard.getProbationStatus(con, uF);

			EmpDashboardData objEmpDashboard = new EmpDashboardData(request, session, CF, uF,con, strEmpIdReq);
			objEmpDashboard.getEmpKPI();
			objEmpDashboard.getResignationStatus();
			objEmpDashboard.getProbationStatus();
			
			alSkills = CF.selectSkills(con, intEmpIdReq);
			alHobbies = CF.selectHobbies(con, intEmpIdReq);
			alLanguages = CF.selectLanguages(con, intEmpIdReq);
			alEducation = CF.selectEducation(con, intEmpIdReq);

			String filePath = request.getRealPath("/userDocuments/");
			alDocuments = CF.selectDocuments(con, intEmpIdReq, filePath);
			
			alActivityDetails = CF.selectEmpActivityDetails(con, intEmpIdReq, uF, CF);

			CF.getOfficialFilledStatus(con, uF, intEmpIdReq);
			
			request.setAttribute("alSkills", alSkills);
			request.setAttribute("alHobbies", alHobbies);
			request.setAttribute("alLanguages", alLanguages);
			request.setAttribute("alEducation", alEducation);
			request.setAttribute("alDocuments", alDocuments);
			request.setAttribute("alActivityDetails", alActivityDetails);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	*///}
	
	
	private void checkCurrentLevelExistForCurrentEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		 
		try {
			List<String> sectionIDList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_question_answer where emp_id = ? and appraisal_id =? and user_type_id = ?" +
					" and user_id = ? and appraisal_freq_id = ?"); //section_id = ? and 
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getUserType()));
//			pst.setInt(4, uF.parseToInt(getCurrentLevel()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rst = pst.executeQuery();
//			System.out.println("pst === > "+pst);
			while (rst.next()) {
				sectionIDList.add(rst.getString("section_id"));
			}
			rst.close();
			pst.close();
			
			List<String> IsQueSectionIDList = new ArrayList<String>();
			pst = con.prepareStatement("select main_level_id from appraisal_level_details where appraisal_level_id in (select appraisal_level_id from appraisal_question_details where appraisal_id =?) "); //section_id = ? and 
			pst.setInt(1, uF.parseToInt(getId()));
			rst = pst.executeQuery();
		//	System.out.println("pst === > "+pst);
			while (rst.next()) {
				IsQueSectionIDList.add(rst.getString("main_level_id"));
			}
			rst.close();
			pst.close();
			
			
			String sectionCount="0";
			pst = con.prepareStatement("select count(distinct(section_id)) as section_id from appraisal_question_answer where emp_id = ? and appraisal_id =? and user_type_id = ?" +
			" and user_id = ? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getUserType()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rst = pst.executeQuery();
			while (rst.next()) {
				sectionCount = rst.getString("section_id");
			}
			rst.close();
			pst.close();
			
//			System.out.println("sectionCount ===> " + sectionCount);
			request.setAttribute("sectionCount", sectionCount);
			request.setAttribute("IsQueSectionIDList", IsQueSectionIDList);
			request.setAttribute("sectionIDList", sectionIDList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void getSummary(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			con = db.makeConnection(con);

			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
			// Map<String, String> hmEmpProbationEnd =
			// CF.getEmpProbationEndDateMap(con, uF);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			Map<String, String> hmOrientationMember = getOrientationMember(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			Map<String, String> hmEmpDetails = new HashMap<String, String>();

			while (rs.next()) {
				// empList.add(rs.getString("emp_per_id"));
				// empList.add(rs.getString("emp_fname") + " " +
				// rs.getString("emp_lname") + " [" + rs.getString("empcode") +
				// "]");
				// empList.add(uF.showData(mpdepart.get(hmEmpDepartment.get(rs.getString("emp_per_id"))),
				// ""));
				// empList.add(uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")),
				// ""));
				// empList.add(uF.showData(hmEmpJoiningDate.get(rs.getString("emp_per_id")),
				// ""));
				// empList.add(uF.showData(hmEmpProbationEnd.get(rs.getString("emp_per_id")),
				// ""));
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpDetails.put("EMP_NAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hmEmpDetails.put("EMP_CODE", rs.getString("empcode"));
				hmEmpDetails.put("EMP_ID", rs.getString("emp_per_id"));
				hmEmpDetails.put("DESIGNATION", uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));
				hmEmpDetails.put("LEVEL", uF.showData(hmLevelMap.get(hmEmpLevelMap.get(rs.getString("emp_per_id"))), ""));

				hmEmpDetails.put("DEAPRTMENT", uF.showData(hmDepartmentMap.get(rs.getString("depart_id")), ""));
				hmEmpDetails.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));

			}
			rs.close();
			pst.close();
			
			hmEmpDetails.put("ORIENTATION", hmOrientationMember.get(getUserType()));

			request.setAttribute("hmEmpDetails", hmEmpDetails);

			pst = con.prepareStatement("select appraisal_level_id from appraisal_question_answer  where appraisal_id=? and emp_id=? and user_id=? and user_type_id=? and appraisal_freq_id = ? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			
			pst = con
			.prepareStatement("select appraisal_level_id from kra_rating_details  where appraisal_id=? and emp_id=? and added_by=? and user_type_id=? and appraisal_freq_id = ? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
	
			pst = con.prepareStatement("select appraisal_level_id from target_details  where appraisal_id=? and emp_id=? and added_by=? and user_type_id=? and appraisal_freq_id = ? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			while (rs.next()) {
			innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}	
			rs.close();
			pst.close();
			
			request.setAttribute("LEVEL_STATUS", innerMp);

			List<String> levelList = new ArrayList<String>();
		//===start parvez date: 15-03-2023===	
//			pst = con.prepareStatement("select appraisal_level_id from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && ((getRole()!=null 
					&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD"))) 
					|| (uF.parseToInt(getUserType())==4 || uF.parseToInt(getUserType())==14 || uF.parseToInt(getUserType())==13))){
				pst = con.prepareStatement("select appraisal_level_id from appraisal_level_details where appraisal_id=? and appraisal_system=? order by appraisal_level_id");
				pst.setInt(2, (int)2);
			} else{
				pst = con.prepareStatement("select appraisal_level_id from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
			}
		//===end parvez date: 15-03-2023===	
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				levelList.add(rs.getString("appraisal_level_id"));
				// request.setAttribute( rs.getString("appraisal_level_id"),
				// rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("levelList", levelList);
//			System.out.println("levelList=======+>" + levelList);
			
			List<String> mainLevelList = new ArrayList<String>();
		//===start parvez date: 15-03-2023===	
//			pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && ((getRole()!=null 
					&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD")))
					|| (uF.parseToInt(getUserType())==4 || uF.parseToInt(getUserType())==14 || uF.parseToInt(getUserType())==13))){
				pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? " +
						" and main_level_id not in (select main_level_id from appraisal_level_details where appraisal_id=? and appraisal_system!=2)  order by main_level_id");
				pst.setInt(2, uF.parseToInt(getId()));
			} else{
				pst = con.prepareStatement("select main_level_id from appraisal_main_level_details where appraisal_id=? order by main_level_id");
			}
		//===end parvez date: 15-03-2023===	
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				mainLevelList.add(rs.getString("main_level_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("mainLevelList", mainLevelList);

			pst = con.prepareStatement("select * from reviewee_strength_improvements where emp_id=? and review_id=? and review_freq_id=? and user_id=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getAppFreqId()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
			while (rs.next()) {
				request.setAttribute("areasOfStrength", uF.showData(rs.getString("areas_of_strength"), ""));
				request.setAttribute("areasOfImprovement", uF.showData(rs.getString("areas_of_improvement"), ""));
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
	}

	// public boolean swapLevelId(UtilityFunctions uF){
	//
	// Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// Database db = new Database();
	// boolean flag=true;
	// try {
	// con = db.makeConnection(con);
	// pst = con
	// .prepareStatement("select appraisal_level_id from appraisal_level_details where appraisal_id=? order by appraisal_level_id");
	// pst.setInt(1,uF.parseToInt(getId()));
	// // pst.setInt(2, uF.parseToInt(getCurrentLevel()));
	// rs = pst.executeQuery();
	// while (rs.next()) {
	// flag=false;
	// setCurrentLevel(rs.getString("appraisal_level_id"));
	// break;
	// // request.setAttribute( rs.getString("appraisal_level_id"),
	// rs.getString("appraisal_level_id"));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeConnection(con);
	// db.closeResultSet(rs);
	// db.closeStatements(pst);
	// }
	// return flag;
	// }

	
	public String submitAllQuestionsAnswersOfReview(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? and user_id=? and user_type_id=? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			StringBuilder sbAppQueAnsIds = null;
			while (rs.next()) {
				if(sbAppQueAnsIds==null) {
					sbAppQueAnsIds = new StringBuilder();
					sbAppQueAnsIds.append(rs.getString("appraisal_question_answer_id"));
				} else {
					sbAppQueAnsIds.append(","+rs.getString("appraisal_question_answer_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbAppQueAnsIds==null) {
				sbAppQueAnsIds = new StringBuilder();
			}
			
			pst = con.prepareStatement("update appraisal_question_answer set is_submit=true where appraisal_question_answer_id in ("+sbAppQueAnsIds.toString()+")");
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute("sbMessage", SUCCESSM+"Reviewee submitted successfully!"+END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return "ajax";
	}
	
	
	public void getCurrentLevelAnswer(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? and user_id=? and user_type_id=? and appraisal_freq_id = ?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionanswerMp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("APP_QUE_ANS_ID", rs.getString("appraisal_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("answers_comment"));
				innerMp.put("LEVEL_COMMENT", rs.getString("section_comment"));
				if (uF.parseToInt(rs.getString("scorecard_id")) != 0){
					questionanswerMp.put(rs.getString("scorecard_id") + "question" + rs.getString("question_id"), innerMp);
				}else if (uF.parseToInt(rs.getString("other_id")) != 0){
					questionanswerMp.put(rs.getString("other_id") + "question" + rs.getString("question_id"), innerMp);
				}else {
					questionanswerMp.put("question" + rs.getString("question_id"), innerMp);
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("questionanswerMp", questionanswerMp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void getLevelStatus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			pst = con
					.prepareStatement("select appraisal_level_id from appraisal_question_answer where appraisal_id=? and emp_id=? and user_id=? and user_type_id=? and appraisal_freq_id = ? group by appraisal_level_id order by appraisal_level_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getEmpID()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
//			System.out.println("StApP/780--pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> innerMp = new HashMap<String, String>();
			while (rs.next()) {
				innerMp.put(rs.getString("appraisal_level_id"), rs.getString("appraisal_level_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("LEVEL_STATUS", innerMp); 

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public boolean getPreviousLevelData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);

			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
		
			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
			
			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? ");

			/*if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get(MANAGER))) {
				sb.append(" and manager !=0");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
				sb.append(" and hr !=0");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
				sb.append(" and  self!=0 ");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
				sb.append(" and peer !=0 ");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("CEO"))) {
				sb.append(" and ceo !=0 ");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HOD"))) {
				sb.append(" and hod !=0 ");
			}*/

			pst = con.prepareStatement(sb.toString());

			pst.setInt(1, uF.parseToInt(getId()));
			// pst.setInt(2, uF.parseToInt(getCurrentLevel()));

			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			
			while (rs.next()) {
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add("");
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				innerList.add("");
				innerList.add("");
				outerList.add(innerList);
				
			}
			rs.close();
			pst.close();

			request.setAttribute("questionList", outerList);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}

	// private void insertMarks(UtilityFunctions uF) {
	// Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// Database db = new Database();
	// con = db.makeConnection(con);
	// List<List<String>> outerList = (List<List<String>>)
	// request.getAttribute("questionList");
	// Map<String, List<String>> hmQuestion = (Map<String, List<String>>)
	// request.getAttribute("hmQuestion");
	//
	// try {
	//
	// pst = con
	// .prepareStatement("delete from appraisal_question_answer where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? ");
	// pst.setInt(1, uF.parseToInt(empID));
	// pst.setInt(2, uF.parseToInt(id));
	// pst.setInt(3, uF.parseToInt(strSessionEmpId));
	// pst.setInt(4, uF.parseToInt(getUserType()));
	// pst.execute();
	//
	// for (int i = 0; outerList != null && i < outerList.size(); i++) {
	// List<String> innerlist = (List<String>) outerList.get(i);
	// List<String> questioninnerList = hmQuestion.get(innerlist
	// .get(1));
	//
	// String weightage = innerlist.get(2);
	// String appraisal_level_id = innerlist.get(13);
	// String scorecard_id = innerlist.get(14);
	// String attribute = innerlist.get(11);
	// String givenAnswer = null;
	// String other_id= innerlist.get(15);
	// double marks = 0;
	// String remark = null;
	//
	// if (uF.parseToInt(questioninnerList.get(8)) == 1) {
	// String[] correct = request.getParameterValues("correct"+
	// innerlist.get(1));
	// remark = request.getParameter("" + innerlist.get(1));
	// String correctanswer = questioninnerList.get(6);
	// for (int k = 0;correct!=null && k < correct.length; k++) {
	// if (k == 0) {
	// givenAnswer = correct[k];
	// } else {
	// givenAnswer += "," + correct[k];
	// }
	// }
	// if (correctanswer != null && givenAnswer != null
	// && givenAnswer.contains(correctanswer)) {
	// marks = uF.parseToDouble(weightage);
	// }
	//
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
	// String[] correct = request.getParameterValues("correct"+
	// innerlist.get(1));
	// for (int k = 0;correct!=null && k < correct.length; k++) {
	// if (k == 0) {
	// givenAnswer = correct[k];
	// } else {
	// givenAnswer += "," + correct[k];
	// }
	// }
	// String correctanswer = questioninnerList.get(6);
	//
	// if (correctanswer != null && givenAnswer != null
	// && givenAnswer.contains(correctanswer)) {
	// marks = uF.parseToDouble(weightage);
	// } else {
	// marks = 0;
	// }
	//
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 3) {
	//
	// marks = uF.parseToDouble(request.getParameter("marks"+
	// innerlist.get(1)));
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
	//
	// givenAnswer = request.getParameter("" + innerlist.get(1));
	// marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(weightage)) /
	// 100;
	//
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
	// givenAnswer = request.getParameter("" + innerlist.get(1));
	// String answer = questioninnerList.get(6);
	// if (givenAnswer != null && answer != null &&
	// answer.contains(givenAnswer)) {
	// marks = uF.parseToDouble(weightage);
	// }
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
	// givenAnswer = request.getParameter("" + innerlist.get(1));
	// String answer = questioninnerList.get(6);
	// if (givenAnswer != null && answer != null &&
	// answer.contains(givenAnswer)) {
	// marks = uF.parseToDouble(weightage);
	// }
	//
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
	//
	// givenAnswer = request.getParameter("" + innerlist.get(1));
	// marks = uF.parseToDouble(request.getParameter("marks"+
	// innerlist.get(1)));
	// weightage = request.getParameter("outofmarks"+ innerlist.get(1));
	//
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
	//
	// givenAnswer = request.getParameter("correct"+ innerlist.get(1));
	// String correctanswer = questioninnerList.get(6);
	// if (givenAnswer != null && correctanswer != null &&
	// correctanswer.contains(givenAnswer)) {
	// marks = uF.parseToDouble(weightage);
	// }
	//
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
	//
	// String[] correct = request.getParameterValues("correct"+
	// innerlist.get(1));
	//
	// for (int k = 0;correct!=null && k < correct.length; k++) {
	// if (k == 0) {
	// givenAnswer = correct[k];
	// } else {
	// givenAnswer += "," + correct[k];
	// }
	// }
	//
	// String correctanswer = questioninnerList.get(6);
	//
	// if (correctanswer != null && givenAnswer != null&&
	// givenAnswer.contains(correctanswer)) {
	// marks = uF.parseToDouble(weightage);
	// }
	//
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
	//
	// marks = uF.parseToDouble(request.getParameter("marks"+
	// innerlist.get(1)));
	// String a = request.getParameter("a" + innerlist.get(1));
	// String b = request.getParameter("b" + innerlist.get(1));
	// String c = request.getParameter("c" + innerlist.get(1));
	// String d = request.getParameter("d" + innerlist.get(1));
	//
	// givenAnswer = uF.showData(a, "") + " :_:"
	// + uF.showData(b, "") + " :_:" + uF.showData(c, "")
	// + " :_: " + uF.showData(d, "");
	//
	// } else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
	// String rating = request.getParameter("gradewithrating"+
	// innerlist.get(1));
	//
	// marks = uF.parseToDouble(rating)* uF.parseToDouble(weightage) / 5;
	//
	// }
	//
	// pst = con
	// .prepareStatement("insert into appraisal_question_answer(emp_id,answer,appraisal_id,question_id,"
	// +
	// "user_id,user_type_id,attempted_on,weightage,marks,appraisal_level_id,scorecard_id,appraisal_attribute,remark,other_id)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	// pst.setInt(1, uF.parseToInt(empID));
	// pst.setString(2, givenAnswer);
	// pst.setInt(3, uF.parseToInt(id));
	// pst.setInt(4, uF.parseToInt(innerlist.get(1)));
	// pst.setInt(5, uF.parseToInt(strSessionEmpId));
	// pst.setInt(6, uF.parseToInt(getUserType()));
	// pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
	// pst.setDouble(8, uF.parseToDouble(weightage));
	// pst.setDouble(9, marks);
	// pst.setInt(10, uF.parseToInt(appraisal_level_id));
	// pst.setInt(11, uF.parseToInt(scorecard_id));
	// pst.setInt(12, uF.parseToInt(attribute));
	// pst.setString(13, remark);
	// pst.setInt(14, uF.parseToInt(other_id));
	// pst.execute();
	//
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeConnection(con);
	// db.closeResultSet(rs);
	// db.closeStatements(pst);
	// }
	// }

	private Map<String, String> getOrientMemberID(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> orientationMemberMp = new HashMap<String, String>();

		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				orientationMemberMp.put(rs.getString("member_name"), rs.getString("member_id"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}

	
	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void getAppraisalDetail(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);

		try {
			List<String> appraisalList = new ArrayList<String>();
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
			while (rs.next()) {
				appraisalList.add(uF.showData(rs.getString("appraisal_details_id"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_name"), ""));
				appraisalList.add(uF.showData(rs.getString("appraisal_description"), ""));
				appraisalList.add(uF.showData(rs.getString("oriented_type"), ""));
				appraisalList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				appraisalList.add(uF.showData(rs.getString("appraisal_instruction"), ""));
				appraisalList.add(uF.showData(rs.getString("user_types_for_feedback"), "")); //6
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalList", appraisalList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getEmployyDetailsList(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		con = db.makeConnection(con);
		Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
		Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
		Map<String, String> hmEmpJoiningDate = CF.getEmpJoiningDateMap(con, uF);
		Map<String, String> hmEmpProbationEnd = CF.getEmpProbationEndDateMap(con, uF);
		Map<String, String> mpdepart = CF.getDeptMap(con);
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
		request.setAttribute("hmFeatureStatus", hmFeatureStatus);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		try {
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			rs = pst.executeQuery();
			List<String> empList = new ArrayList<String>();
			Map<String, String> orientationMemberMp = getOrientationMember(con);

			while (rs.next()) {
				empList.add(rs.getString("emp_per_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empList.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname") + " [" + rs.getString("empcode") + "]");
				empList.add(uF.showData(mpdepart.get(hmEmpDepartment.get(rs.getString("emp_per_id"))), ""));
				empList.add(uF.showData(hmEmpCodeDesig.get(rs.getString("emp_per_id")), ""));
				empList.add(uF.showData(hmEmpJoiningDate.get(rs.getString("emp_per_id")), ""));
				empList.add(uF.showData(hmEmpProbationEnd.get(rs.getString("emp_per_id")), ""));
			}
			rs.close();
			pst.close();
			
			empList.add(orientationMemberMp.get(getUserType()));
			request.setAttribute("empList", empList);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public String getLevelQuestion(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrientMemberID = getOrientMemberID(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);	//added by parvez date: 15-03-2023

			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
			
			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? ");
			
			/*if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get(MANAGER))) {
				sb.append(" and manager !=0");

			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
				sb.append(" and hr !=0");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
				sb.append(" and  self!=0 ");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
				sb.append(" and peer !=0 ");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("CEO"))) {
				sb.append(" and ceo !=0 ");
			} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HOD"))) {
				sb.append(" and hod !=0 ");
			}*/
			sb.append(" order by appraisal_level_id");
			
			pst = con.prepareStatement(sb.toString());
			pst.setInt(1, uF.parseToInt(id));
			rs = pst.executeQuery();
//			System.out.println("pst 1 :::: "+pst);
			Map<String,List<List<String>>> hmLevelQuestion = new LinkedHashMap<String, List<List<String>>>();
			StringBuilder sbLevels = new StringBuilder();
		
			while (rs.next()) {
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
//				System.out.println("ASP/1248---rs.getInt(app_system_type)"+rs.getInt("app_system_type"));
			//===start parvez date: 15-03-2023===	
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && ((getRole()!=null 
						&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD"))) 
						|| (uF.parseToInt(getUserType())==4 || uF.parseToInt(getUserType())==14 || uF.parseToInt(getUserType())==13))
						&& (rs.getInt("app_system_type")!=0 && rs.getInt("app_system_type")!=2)){
					continue;
				}
			//===end parvez date: 15-03-2023===	
//				System.out.println("ASP/1246---app_system_type=="+rs.getInt("app_system_type"));
				List<List<String>> outerList=hmLevelQuestion.get(rs.getString("appraisal_level_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id")); //0
				innerList.add(rs.getString("question_id")); //1
				innerList.add(rs.getString("weightage")); //2
				innerList.add(""); //3
				innerList.add(rs.getString("appraisal_id")); //4
				innerList.add(rs.getString("plan_id")); //5
				innerList.add(""); //6
				innerList.add(""); //7
				innerList.add(""); //8
				innerList.add(""); //9
				innerList.add(rs.getString("measure_id")); //10
				innerList.add(rs.getString("attribute_id")); //11
				innerList.add(rs.getString("other_short_description")); //12
				innerList.add(rs.getString("appraisal_level_id")); //13
				innerList.add(rs.getString("scorecard_id")); //14
				innerList.add(rs.getString("other_id")); //15
				innerList.add(""); //16
				innerList.add(""); //17
				outerList.add(innerList);
				
				hmLevelQuestion.put(rs.getString("appraisal_level_id"), outerList);
			}
			rs.close();
			pst.close();

//			System.out.println("hmLevelQuestion :::: "+hmLevelQuestion);
			request.setAttribute("hmLevelQuestion", hmLevelQuestion);
			
			Map<String, List<List<String>>> hmSection = new LinkedHashMap<String, List<List<String>>>();
			 sb = new StringBuilder("select * from appraisal_level_details where main_level_id in(select main_level_id from " +
					"appraisal_main_level_details where appraisal_id=(select appraisal_details_id  from appraisal_details where " +
					"appraisal_details_id = ?) ");
				if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get(MANAGER))) {
					sb.append(" and manager !=0");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HR"))) {
					sb.append(" and hr !=0");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Self"))) {
					sb.append(" and  self!=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("Peer"))) {
					sb.append(" and peer !=0 ");
				} else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("CEO"))) {
					sb.append(" and ceo !=0 ");
				}  else if (uF.parseToInt(getUserType()) == uF.parseToInt(hmOrientMemberID.get("HOD"))) {
					sb.append(" and hod !=0 ");
				}
			//===start parvez date: 15-03-2023===	
//				sb.append(") order by main_level_id,appraisal_level_id");
				if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_GOAL_NOT_SHOWING_IN_PEER_AND_HOD)) && ((getRole()!=null 
						&& (getRole().equalsIgnoreCase("Peer") || getRole().equalsIgnoreCase("Other Peer") || getRole().equalsIgnoreCase("HOD")))
						|| (uF.parseToInt(getUserType())==4 || uF.parseToInt(getUserType())==14 || uF.parseToInt(getUserType())==13))){
					sb.append(") and appraisal_system=2 order by main_level_id,appraisal_level_id");
				} else{
					sb.append(") order by main_level_id,appraisal_level_id");
				}
			//===end parvez date: 15-03-2023===	
				pst = con.prepareStatement(sb.toString());
				pst.setInt(1, uF.parseToInt(id));
				rs = pst.executeQuery();
//				System.out.println("pst 2 ::::::::: "+pst);
				while (rs.next()) {
					List<List<String>> outerList = hmSection.get(rs.getString("main_level_id"));
					if(outerList == null) outerList = new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("appraisal_level_id"));
					innerList.add(rs.getString("level_title"));
					innerList.add(rs.getString("short_description"));
					innerList.add(rs.getString("long_description"));
					outerList.add(innerList);
					hmSection.put(rs.getString("main_level_id"), outerList);
					
					sbLevels.append(rs.getString("main_level_id")+",");
				}
				rs.close();
				pst.close();
				
			request.setAttribute("hmSection", hmSection);
//			System.out.println("hmSection :::: "+hmSection);
			
			Map<String, String> hmLevelDetails = new HashMap<String, String>();
			if(sbLevels.length()>1) {
				sbLevels.replace(0, sbLevels.length(), sbLevels.substring(0, sbLevels.length()-1));
				pst = con.prepareStatement("select * from appraisal_main_level_details where main_level_id in ("+sbLevels.toString()+") order by main_level_id");
				rs = pst.executeQuery();
//				System.out.println("SAP/1337--- pst : "+ pst);
				while(rs.next()) {
					hmLevelDetails.put(rs.getString("main_level_id")+"_TITLE", rs.getString("level_title"));
					hmLevelDetails.put(rs.getString("main_level_id")+"_SDESC", rs.getString("short_description"));
					hmLevelDetails.put(rs.getString("main_level_id")+"_LDESC", rs.getString("long_description"));
					hmLevelDetails.put(rs.getString("main_level_id")+"_LWEIGHTAGE", rs.getString("section_weightage"));	//added by parvez date: 27-02-2023===
				} 
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from appraisal_question_answer where appraisal_id=? and emp_id=? and user_id=? and " +
					"user_type_id=? and appraisal_freq_id=? and reviewer_or_appraiser=? order by section_id");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(getEmpID()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setInt(4, uF.parseToInt(getUserType()));
				pst.setInt(5, uF.parseToInt(getAppFreqId()));
				if(getRole() != null && getRole().equalsIgnoreCase("Reviewer")) {
					pst.setInt(6, 1);
				} else {
					pst.setInt(6, 0);
				}
				rs = pst.executeQuery();
//				System.out.println(" pst : "+ pst);
				String strOrgId = CF.getEmpOrgId(con, uF, strSessionEmpId);
				String mainPathWithOrg = CF.getRetriveProjectDocumentFolder()+strOrgId+"/Reviews"+"/"+getId()+"/"+getAppFreqId()+"/"+strSessionEmpId+"/"+getUserType();
				while(rs.next()) {
					String strFilePath = hmLevelDetails.get(rs.getString("section_id")+"_FILE_PATH");
					if(rs.getString("section_comment_file") != null && strFilePath == null) {
						strFilePath = mainPathWithOrg+"/"+rs.getString("section_id")+"/"+rs.getString("section_comment_file");
					}
					hmLevelDetails.put(rs.getString("section_id")+"_COMMENT", rs.getString("section_comment"));
					hmLevelDetails.put(rs.getString("section_id")+"_FILE_PATH", (rs.getString("section_comment_file") != null) ? strFilePath : "#");
					hmLevelDetails.put(rs.getString("section_id")+"_FILE_NAME", (rs.getString("section_comment_file") != null) ? rs.getString("section_comment_file") : "");
					hmLevelDetails.put(rs.getString("section_id")+"_QUE_ID", rs.getString("appraisal_question_answer_id"));
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmLevelDetails", hmLevelDetails);
			
		//===start parvez date: 27-02-2023===	
			pst = con.prepareStatement("select * from appraisal_other_question_type_details where appraisal_id=? order by level_id");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("SAp/941---pst===>"+pst);
			rs = pst.executeQuery();
			Map<String, String> othrQueType = new HashMap<String, String>();
			while (rs.next()) {
				othrQueType.put(rs.getString("level_id"), rs.getString("other_question_type"));
			}
			rs.close();
			pst.close();
			request.setAttribute("othrQueType", othrQueType);
		//===end parvez date: 27-02-2023===	

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}

	// public String getLevelId() {
	// return levelId;
	// }
	//
	// public void setLevelId(String levelId) {
	// this.levelId = levelId;
	// }

	public void getAppraisalQuestionMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		// Map<String, String> AppraisalQuestion = new HashMap<String,
		// String>();
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {
			con = db.makeConnection(con);
			
			List<String> alKRAIds = (List<String>)request.getAttribute("alKRAIds");
			List<String> alGoarTargetIds = (List<String>)request.getAttribute("alGoarTargetIds");
		
	//===start parvez date: 16-03-2022===		
//			pst = con.prepareStatement("select qb.*, aqd.app_system_type from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst = con.prepareStatement("select qb.*, aqd.app_system_type, aqd.goal_kra_target_id, aqd.other_short_description,aqd.weightage from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
	//===end parvez date: 16-03-2022===		
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();
			while (rs.next()) {
				if(rs.getInt("app_system_type")==4 && alKRAIds!=null && rs.getString("kra_id")!=null && !alKRAIds.contains(rs.getString("kra_id"))) {
					continue;
				}
				if((rs.getInt("app_system_type")==3 || rs.getInt("app_system_type")==5) && alGoarTargetIds!=null && rs.getString("goal_kra_target_id")!=null && !alGoarTargetIds.contains(rs.getString("goal_kra_target_id"))) {
					continue;
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));	//0
				innerList.add(rs.getString("question_text"));		//1
				innerList.add(rs.getString("option_a"));			//2
				innerList.add(rs.getString("option_b"));			//3
				innerList.add(rs.getString("option_c"));			//4
				innerList.add(rs.getString("option_d"));			//5
				innerList.add(rs.getString("correct_ans"));			//6
				innerList.add(rs.getString("is_add"));				//7
				innerList.add(rs.getString("question_type"));		//8
				innerList.add(rs.getString("option_e")); 			//9
			//===start parvez date: 29-12-2021===	
				innerList.add(rs.getString("app_system_type"));		//10 
				innerList.add(rs.getString("goal_kra_target_id"));		//11
			//===end parvez date: 29-12-2021===
				
			//===start parvez date: 10-03-2022===
				innerList.add(rs.getString("other_short_description"));		//12
			//===end parvez date: 10-03-2022===	
				
			//===start parvez date: 16-03-2022===
				innerList.add(rs.getString("weightage"));		//13
			//===end parvez date: 16-03-2022===	

				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);

			}
			rs.close();
			pst.close();
			request.setAttribute("hmQuestion", hmQuestion);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		// return AppraisalQuestion;
	}

	public void getanswerTypeMap(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, List<List<String>>> hmQuestionanswerType = new HashMap<String, List<List<String>>>();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_answer_type_sub ");
			rs = pst.executeQuery();
			while (rs.next()) {

				List<List<String>> outerList = hmQuestionanswerType.get(rs.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("score_label"));
				innerList.add(rs.getString("score"));
				outerList.add(innerList);
				hmQuestionanswerType.put(rs.getString("answer_type_id"), outerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmQuestionanswerType", hmQuestionanswerType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public Map<String, String> getLevelMap(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new HashMap<String, String>();
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name") + "[" + rs.getString("level_code") + "]");
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
		return hmLevelMap;
	}

	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		con = db.makeConnection(con);

		try {
			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> outerList = answertypeSub.get(rst.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("score"));
				innerList.add(rst.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rst.getString("answer_type_id"), outerList);
			}
			rst.close();
			pst.close();

			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	// public String getTab() {
	// return tab;
	// }
	//
	// public void setTab(String tab) {
	// this.tab = tab;
	// }

	// public String getCurrentLevel() {
	// return currentLevel;
	// }
	//
	// public void setCurrentLevel(String currentLevel) {
	// this.currentLevel = currentLevel;
	// }
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}


	private Map<String, String> getOrientationMember(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		Map<String, String> orientationMemberMp = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from orientation_member where status=true order by weightage");
			rs = pst.executeQuery();
			while (rs.next()) {
				//orientationMemberMp.put(rs.getString("orientation_member_id"), rs.getString("member_name"));
				orientationMemberMp.put(rs.getString("member_id"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
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
		return orientationMemberMp;
	}


	private Map<String, String> getReviewDetails(Connection con) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmReviewData = new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_details where appraisal_details_id=?");
			pst.setInt(1, uF.parseToInt(id));
			rst = pst.executeQuery();
			while (rst.next()) {
				hmReviewData.put("REVIEW_NAME", rst.getString("appraisal_name"));
				hmReviewData.put("REVIEW_SELFID", rst.getString("self_ids"));
				hmReviewData.put("REVIEW_PEERID", rst.getString("peer_ids"));
				hmReviewData.put("REVIEW_MANAGERID", rst.getString("supervisor_id"));
				hmReviewData.put("REVIEW_HRID", rst.getString("hr_ids"));
				hmReviewData.put("REVIEW_OTHERID", rst.getString("other_ids"));
				hmReviewData.put("REVIEW_STARTDATE", rst.getString("from_date"));
				hmReviewData.put("REVIEW_ENDDATE", rst.getString("to_date"));
				hmReviewData.put("REVIEW_CEOID", rst.getString("ceo_ids"));
				hmReviewData.put("REVIEW_HODID", rst.getString("hod_ids"));
//				hmReviewData.put("REVIEW_ENDDATE", rst.getString(""));
			}
			rst.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rst!= null) {
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
		return hmReviewData;
	}
	
	
//===start parvez date: 29-12-2021===
	public void getNewGoalDetails(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		// boolean flag = false;
		try {
			con = db.makeConnection(con);

			
			Map<String, String> alGoalTitle = new HashMap<String, String>();			//===added by parvez date: 27-12-2021===
			
			pst = con.prepareStatement("select * from goal_details gd,appraisal_question_details aqd where gd.goal_id = aqd.goal_kra_target_id and emp_ids like '%,"+getEmpID()+",%' and goal_type="+PERSONAL_GOAL);
			rs = pst.executeQuery();
//			System.out.println("SApP/1673--pst ===>> " + pst);
			while (rs.next()) {
				alGoalTitle.put(rs.getString("goal_id"), rs.getString("goal_title"));
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 27-12-2021===	
			request.setAttribute("alGoalTitle", alGoalTitle);
		//===end parvez date: 27-12-2021===
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
//===end parvez date: 29-12-2021===
	
//===created by parvez date: 06-07-2022===
	//===start===
	private void getAppraisalReopenStatus(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmUsersFeedbackReopenComment = new HashMap<String, String>();
			
			pst = con.prepareStatement("select reopen_comment from review_feedback_reopen_details where review_id=? and review_freq_id=? and emp_id=? and user_id =? and user_type_id=? " +
					" order by review_feedback_reopen_id");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
//			System.out.println("hmExistSectionCount pst2 ::: "+pst);
			while (rs.next()) {
				hmUsersFeedbackReopenComment.put("REOPEN_REASON", rs.getString("reopen_comment"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmUsersFeedbackReopenComment", hmUsersFeedbackReopenComment);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	//===end===
	
//===created by parvez date: 06-07-2022===
	//===start===
	private void getReviewerFeedbackDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUsersFeedbackDetails = new HashMap<String, String>();
			
			double weightage = 0;
			pst = con.prepareStatement("select section_weightage from appraisal_main_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while(rs.next()){
				weightage += uF.parseToDouble(rs.getString("section_weightage")); 
			}
			rs.close();
			pst.close();
			hmUsersFeedbackDetails.put("WEIGHTAGE", weightage+"");
			
			pst = con.prepareStatement("select * from reviewer_feedback_details where appraisal_id=? and appraisal_freq_id=? and emp_id=? and user_id=? and user_type_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppFreqId()));
			pst.setInt(3, uF.parseToInt(getEmpID()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> innerMap = new HashMap<String, String>();
				hmUsersFeedbackDetails.put("MARKS", rs.getString("reviewer_marks"));
				hmUsersFeedbackDetails.put("COMMENT", rs.getString("reviewer_comment"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmUsersFeedbackDetails", hmUsersFeedbackDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String submitReviewerFeedback(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update reviewer_feedback_details set is_submit=true where emp_id=? and appraisal_id=? and user_id=? and user_type_id=? and appraisal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpID()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setInt(4, uF.parseToInt(getUserType()));
			pst.setInt(5, uF.parseToInt(getAppFreqId()));
//			System.out.println("pst="+pst);
			pst.executeUpdate();
			pst.close();
			
			session.setAttribute("sbMessage", SUCCESSM+"Reviewee submitted successfully!"+END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return "ajax";
	}
	//===end===
	
	
	@Override
	public void run() {
	
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		Map<String, String> hmReviewData = getReviewDetails(con);
		
		Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);

//		 System.out.println("allIdList ==> "+ allIdList);
		// String strDomain = request.getServerName().split("\\.")[0];
//		for (int i = 0; allIdList != null && !allIdList.isEmpty() && i < allIdList.size(); i++) {
//			if(allIdList.get(i) != null && !allIdList.get(i).equals("")){
				/*Map<String, String> hmEmpInner = hmEmpInfo.get(strSessionEmpId);
				Map<String, String> hmRevieweeInner = hmEmpInfo.get(empID);
//				 System.out.println(i+" allIdList "+allIdList.get(i));
	//			String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_EMP_REVIW_SUBMITED, CF);
				nF.setDomain(getStrDomain());
				nF.request = request;
				nF.setStrEmpId(strSessionEmpId);
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
	
				if(empID != null && strSessionEmpId != null && empID.equals(strSessionEmpId)){
					nF.setStrRevieweeName("SELF");
				}else{
					nF.setStrRevieweeName(hmRevieweeInner.get("FNAME")+" "+hmRevieweeInner.get("LNAME"));
				}
				nF.setStrReviewName(hmReviewData.get("REVIEW_NAME"));
				nF.setStrReviewStartdate(uF.getDateFormat(hmReviewData.get("REVIEW_STARTDATE"), DBDATE, CF.getStrReportDateFormat()));
				nF.setStrReviewEnddate(uF.getDateFormat(hmReviewData.get("REVIEW_ENDDATE"), DBDATE, CF.getStrReportDateFormat()));
	
				nF.setStrEmpFname(hmEmpInner.get("FNAME"));
				nF.setStrEmpLname(hmEmpInner.get("LNAME"));
				nF.setEmailTemplate(true);
				nF.sendNotifications();*/
//			}
//		}
		db.closeConnection(con);
	}
	
	

//===start parvez date: 13-07-2022===
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
//===end parvez date: 13-07-2022===	

}
