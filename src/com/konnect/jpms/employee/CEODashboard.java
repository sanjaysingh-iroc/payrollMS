package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.text.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.konnect.jpms.performance.GoalSummary;
import com.konnect.jpms.reports.AttendanceReport;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class CEODashboard implements IStatements {

	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	private String strEmpId;
	private String strUserType;
	private String strBaseUserType;
	private String strBaseUserTypeId;

	private static Logger log = Logger.getLogger(CEODashboard.class);
	CEODashboard(HttpServletRequest request, HttpSession session, CommonFunctions CF, String strEmpId) {
		this.request = request;
		this.session = session;
		this.CF = CF;
		this.strEmpId = strEmpId;
	}

	public String loadDashboard() {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			strUserType = (String) session.getAttribute(USERTYPE);
			strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
			strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-dashboard\"></i><a href=\"Login.action?role=" + strBaseUserTypeId
					+ "\" style=\"color: #3c8dbc;\">");
			if (strUserType != null && strUserType.equals(ADMIN)) {
				sbpageTitleNaviTrail.append(" Global HR Dashboard");
			} else if (strUserType != null && strUserType.equals(HRMANAGER)) {
				sbpageTitleNaviTrail.append(" HR Dashboard");
			} else if (strUserType != null && strUserType.equals(RECRUITER)) {
				sbpageTitleNaviTrail.append(" Recruiter Dashboard");
			} else if (strBaseUserType != null && (strBaseUserType.equals(ADMIN) || strBaseUserType.equals(CEO)) && strUserType != null
					&& strUserType.equals(MANAGER)) {
				sbpageTitleNaviTrail.append(" CEO Dashboard");
			}
			sbpageTitleNaviTrail.append("</a></li>");
			

			request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
			// CF.getAlertUpdates(CF, strEmpId, request, strUserType);
			String[] arrEnabledModules = CF.getArrEnabledModules();
//			con = db.makeConnection(con);

			// Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null,
			// null);
			Map<String, String> hmDepartMap = CF.getDeptMap(con);
			Map<String, String> hmDepartmentMap = new HashMap<String, String>();
			Iterator<String> it = hmDepartMap.keySet().iterator();
			while (it.hasNext()) {
				String departId = it.next();
				String departName = hmDepartMap.get(departId);
				departName = departName.replace("'", "\\'");
				hmDepartmentMap.put(departId, departName);
			}
//			System.out.println("con1:"+con);
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);
//			System.out.println("hmWorkLocationMap1:"+hmWorkLocationMap);
			// getBirthday(con, uF, hmEmployeeMap);
			// CF.getDayThought(con, uF, CF, request);
			getWlocationEmployeeCount(con, uF);
			getDepartmentEmployeeCount(con, uF);
			getEmployeeSkill(con, uF, CF);
			checkTimeSystemHealth(con, uF, sbpageTitleNaviTrail);
			
			if (strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
				getInductionData(con, uF);
				getResignationData(con, uF);
				getConfirmationData(con, uF);
				getRetirementData(con, uF);
				getLiveEmpCountAndLimit(con, uF);
			}

			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT + "") >= 0) {
				getKRARatingAndCompletionStatus(uF);
				getCorporateDetails(uF);

				getTargetAchievedAndMissed(con, uF);
				getKRAAchievedStatus(con, uF);

				getLifecycleGaps(con, uF);
				if (strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
					// getPerformanceDetails(con, uF);
					getLearningGapDetails(con, uF);

					getHRReviews(con, uF);
					getKRAReview(con, uF);
				}
			}

			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_ONBOARDING + "") >= 0) {
				getHiringVsTermination(con, uF);
				getRecruitmentFulfilled(con, uF);

				if (strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
					// getRecruitmentDetails(con, uF);
					getJobProfileCounter(con, uF);
					getInterviews(con, uF);
				}
			}

			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_CAREER_DEV_PLANNING + "") >= 0) {
				getAwardedEmployee(con, uF);
				getLearningProgress(con, uF);

				if (strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN))) {
					// getLearningDetails(con, uF);
				}
			}

			if ((strUserType != null && (strUserType.equals(HRMANAGER) || strUserType.equals(ADMIN)))
					&& (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_TIME_AND_ATTENDANCE + "") >= 0)) {
				getAttendanceSummary(uF);
			}

			if (strUserType != null
					&& (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN))) {
				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_TIME_AND_ATTENDANCE + "") >= 0) {
					int nExceptionCount = CF.getExceptionCount(con, request, CF, uF, strUserType, strEmpId, session);
					request.setAttribute("exceptionCount", "" + nExceptionCount);
				}

				if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_LEAVE_MANAGEMENT + "") >= 0) {
					getLeaveSummary(con, uF, CF, strUserType);
				}
			}

			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_COMPENSATION_MANAGEMENT + "") >= 0) {
				getCompensationSummary(con, uF, CF);
			}
			// getDisciplinePresentAbsent(con, uF, CF, strUserType);

			// System.out.println("hmDepartmentMap ===>> " + hmDepartmentMap);
			if (strUserType != null && strUserType.equals(MANAGER) && strBaseUserType != null && strBaseUserType.equals(CEO)) {
				getPeopleDeshboardReport();
			}
			
			request.setAttribute("hmDepartmentMap", hmDepartmentMap);
			request.setAttribute("hmWorkLocationMap", hmWorkLocationMap);
			request.setAttribute("arrEnabledModules", arrEnabledModules);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}

	private void checkTimeSystemHealth(Connection con, UtilityFunctions uF, StringBuilder sbpageTitleNaviTrail) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strWLocationId = CF.getEmpWlocationId(con, uF, ""+strEmpId);
			String strLevelId = CF.getEmpLevelId(con, ""+strEmpId);
			
			List<String> alDates = new ArrayList<String>();
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			String strColor = "red";
			for(int i=0; i<5; i++) {
				String strDate = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), i)+"", DBDATE, DATE_FORMAT);
			
				boolean flag = CF.checkHoliday(con, uF, strDate, strWLocationId, strOrgId);
				boolean flag1 = CF.checkEmpRosterWeeklyOff(con, CF, uF, strEmpId, strDate, strLevelId, strWLocationId, strOrgId);
				if(flag || flag1) {
					continue;
				}
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'yyyy-MM-dd')=?"); // emp_id,
				pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
//				System.out.println("pst ==>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					strColor = "green";
				}
				rs.close();
				pst.close();
				break;
			}
			sbpageTitleNaviTrail.append("<div style=\"float: right;\" title=\"This provides you information about Time data received from various sources. If it is GREEN it means, you are receiving data, however if its RED you are not receiving any data from your source devices, in which case you should alert your administrator.\"><i class=\"fa fa-circle\" style=\"color: "+strColor+"; font-size: 24px;\"></i> Time System Health </div>");
			
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
//			request.setAttribute("attendFlag", ""+attendFlag);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private void getLiveEmpCountAndLimit(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		try {

			int liveEmpCnt = 0;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as empCnt from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive = ? and eod.emp_id >0 and epd.emp_per_id=eod.emp_id and approved_flag = ? ");
			sbQuery.append(" order by empcode, emp_status, emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id ) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
			rst = pst.executeQuery();
			while (rst.next()) {
				liveEmpCnt = rst.getInt("empCnt");
			}
			rst.close();
			pst.close();

			boolean isEmpLimit = false;
			// System.out.println("al.size() ===>> " + al.size() +
			// "uF.parseToInt(CF.getStrMaxEmployee()) ===>> " +
			// uF.parseToInt(CF.getStrMaxEmployee()));
			if (liveEmpCnt >= uF.parseToInt(CF.getStrMaxEmployee())) {
				isEmpLimit = true;
			}
			request.setAttribute("isEmpLimit", "" + isEmpLimit);
			request.setAttribute("strEmpLimit", CF.getStrMaxEmployee());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void getPeopleDeshboardReport() {
		List<List<String>> allWorkingPeopleReport = new ArrayList<List<String>>();
		List<List<String>> allEmploymentTypeReport = new ArrayList<List<String>>();
		List<List<String>> allGenderPeopleReport = new ArrayList<List<String>>();
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			getEmployeeSkill(con, uF, CF);
			getDepartmentEmployeeCount(con, uF);
			getWlocationEmployeeCount(con, uF);
			
			getEmployeeLeaveDetails(con,uF);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);
			
			Map<String, String> hmOrgName = new HashMap<String, String>();
			pst = con.prepareStatement("select org_id,org_name from org_details order by org_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				hmOrgName.put(rst.getString("org_id"), rst.getString("org_name"));
			}
			rst.close();
			pst.close();
			
			
			
			Map<String, String> hmEmpTypewiseEmp = getEmploymentTypewiseEmployeeOrgwise(con);
			Map<String, String> hmEmpStatuswiseEmp = getEmploymentStatuswiseEmployeeOrgwise(con);
			Map<String, String> hmGenderwiseEmp = getGenderwiseEmployeeOrgwise(con);
			
			Map<String, String> hmPendingEmp = getPendingEmployeeOrgwise(con);
//			Map<String, String> hmExEmp = getExEmployeeOrgwise(con);
//			Map<String, String> hmLiveUserEmp = getLiveUserCountOrgwise(con);
			
			pst = con.prepareStatement("select org_id from org_details order by org_id");
			rst = pst.executeQuery();
			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
//				List<String> allEmpreport = new ArrayList<String>();
//				allEmpreport = getEmpReport(con, rst.getString("org_id"));
//				int candiCnt = getCandidateOrgwiseCount(con, rst.getString("org_id"));
				
				List<String> innerWPList = new ArrayList<String>();
				innerWPList.add(rst.getString("org_id"));
				innerWPList.add(uF.showData(hmOrgName.get(rst.getString("org_id")), ""));
				innerWPList.add(hmPendingEmp.get(rst.getString("org_id"))); //Pending emp 2
				innerWPList.add(uF.showData(hmEmpStatuswiseEmp.get(rst.getString("org_id")+"_"+PROBATION), "0")); //Probation emp 3
				innerWPList.add(uF.showData(hmEmpStatuswiseEmp.get(rst.getString("org_id")+"_"+PERMANENT), "0")); //Permanent emp 4
				innerWPList.add(uF.showData(hmEmpStatuswiseEmp.get(rst.getString("org_id")+"_"+TEMPORARY), "0")); //Permanent emp 5
				innerWPList.add(uF.showData(hmEmpStatuswiseEmp.get(rst.getString("org_id")+"_"+RESIGNED), "0")); //Resigned emp 6
//				innerWPList.add(hmExEmp.get(rst.getString("org_id"))); //Ex emp 7
//				innerWPList.add(hmLiveUserEmp.get(rst.getString("org_id"))); // 8
				innerWPList.add("0"); //Ex emp 7
				innerWPList.add("0"); //8
//				innerPlpList.add(candiCnt + "");
				allWorkingPeopleReport.add(innerWPList);
				
				List<String> innerEmpTypeList = new ArrayList<String>();
				innerEmpTypeList.add(rst.getString("org_id"));
				innerEmpTypeList.add(uF.showData(hmOrgName.get(rst.getString("org_id")), ""));
				if(hmFeatureStatus != null ) {
					if(hmFeatureStatus.get(F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_GENERAL))) {
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_FULLTIME"), "0")); //Full time emp 2
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTTIME"), "0")); //Part time emp 3
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONSULTANT"), "0")); //Consultant emp 4
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONTRACTUAL"), "0")); //Contractual emp 5
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_INTERN"), "0")); //Intern emp 6
					}
					if(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE))) {
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_REGULAR"), "0")); //Regular emp 2
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONTRACT"), "0")); //Contract emp 3
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PROFESSIONAL"), "0")); //Professional emp 4
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_STIPEND"), "0")); //Stipend emp 5
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_SCHOLARSHIP"), "0")); //Scholarship emp 6
					}
					if(hmFeatureStatus.get(F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_FINANCE))) {
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_FULLTIME"), "0")); //Full time emp 2
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTTIME"), "0")); //Part time emp 3
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONSULTANT"), "0")); //Consultant emp 4
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_TEMPORARY"), "0")); //Temporary emp 5
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_ARTICLE"), "0")); //Article emp 6
						innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTNER"), "0")); //Partner emp 7
					}
				} else {
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_FULLTIME"), "0")); //Full time emp 2
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTTIME"), "0")); //Part time emp 3
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONSULTANT"), "0")); //Consultant emp 4
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_CONTRACTUAL"), "0")); //Contractual emp 5
					innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_INTERN"), "0")); //Intern emp 6
				}
//									***************** KPCA ************************
				/*innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_ARTICLE"), "0")); //ARTICLE emp 5
				innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_PARTNER"), "0")); //PARTNER emp 6
				innerEmpTypeList.add(uF.showData(hmEmpTypewiseEmp.get(rst.getString("org_id")+"_TEMPORARY"), "0")); //TEMPORARY emp 7 */
				
				allEmploymentTypeReport.add(innerEmpTypeList);
				
				List<String> innerGenderList = new ArrayList<String>();
				innerGenderList.add(rst.getString("org_id"));
				innerGenderList.add(uF.showData(hmOrgName.get(rst.getString("org_id")), ""));
				innerGenderList.add(uF.showData(hmGenderwiseEmp.get(rst.getString("org_id")+"_MALE"), "0")); //Male emp 2
				innerGenderList.add(uF.showData(hmGenderwiseEmp.get(rst.getString("org_id")+"_FEMALE"), "0")); //Female emp 3
				innerGenderList.add(uF.showData(hmGenderwiseEmp.get(rst.getString("org_id")+"_OTHER"), "0")); //Other emp 4
				allGenderPeopleReport.add(innerGenderList);
				
			}
			rst.close();
			pst.close();
			
			
//			request.setAttribute("unassignCandiCnt", ""+unassignCandiCnt);
			request.setAttribute("allWorkingPeopleReport", allWorkingPeopleReport);
			request.setAttribute("allEmploymentTypeReport", allEmploymentTypeReport);
			request.setAttribute("allGenderPeopleReport", allGenderPeopleReport);
			
			
			request.setAttribute("hmDepartmentMap", hmDepartmentMap);  
			request.setAttribute("hmWorkLocationMap", hmWorkLocationMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private Map<String, String> getGenderwiseEmployeeOrgwise(Connection con) {

		Map<String, String> hmGenderwiseEmps = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
//			pst = con.prepareStatement("select count(*) as count,emp_gender,org_id from employee_official_details eod, employee_personal_details epd where org_id>0 and " +
//				" eod.emp_id = epd.emp_per_id and epd.approved_flag = true and epd.is_alive = true and epd.emp_filled_flag = true group by emp_gender, org_id");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emp_gender,org_id from employee_official_details eod, employee_personal_details epd where org_id>0 and " +
				"eod.emp_id = epd.emp_per_id and epd.approved_flag = true and epd.is_alive = true and epd.emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by emp_gender, org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
				
				if (rst.getString("emp_gender") != null && rst.getString("emp_gender").trim().equals("M")) { //Male 
					hmGenderwiseEmps.put(rst.getString("org_id")+"_MALE", rst.getString("count"));
				} else if (rst.getString("emp_gender") != null && rst.getString("emp_gender").trim().equals("F")) { //Female 
					hmGenderwiseEmps.put(rst.getString("org_id")+"_FEMALE", rst.getString("count"));
				} else if (rst.getString("emp_gender") != null && !rst.getString("emp_gender").trim().equals("M") && !rst.getString("emp_gender").trim().equals("F")) { //Other
					hmGenderwiseEmps.put(rst.getString("org_id")+"_OTHER", rst.getString("count"));
				}
			}
			rst.close();
			pst.close();
//			 System.out.println("hmGenderwiseEmps ========= >> " + hmGenderwiseEmps);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmGenderwiseEmps;
	}
	
	

	private Map<String, String> getPendingEmployeeOrgwise(Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		Map<String, String> hmPendingEmp = new HashMap<String, String>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,org_id from employee_official_details eod, employee_personal_details epd where " +
				" eod.emp_id = epd.emp_per_id and epd.approved_flag = false and epd.is_alive = false and epd.emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
				hmPendingEmp.put(rst.getString("org_id"), rst.getInt("count")+"");
			}
			rst.close();
			pst.close();
//			 System.out.println("Pending count ========= >> " + count);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmPendingEmp;
	}
	
	
	
	private Map<String, String> getEmploymentStatuswiseEmployeeOrgwise(Connection con) {

		Map<String, String> hmEmpStatuswiseEmps = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emp_status,org_id from employee_official_details eod, employee_personal_details epd where org_id>0 and " +
				"eod.emp_id = epd.emp_per_id and epd.approved_flag = true and epd.is_alive = true and epd.emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by emp_status, org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
			// System.out.println("pst  ==== >>>> "+pst);
			while (rst.next()) {
				if (rst.getString("emp_status") != null && rst.getString("emp_status").trim().equals(PROBATION)) { 
					hmEmpStatuswiseEmps.put(rst.getString("org_id")+"_"+PROBATION, rst.getString("count"));
				} else if (rst.getString("emp_status") != null && rst.getString("emp_status").trim().equals(PERMANENT)) { 
					hmEmpStatuswiseEmps.put(rst.getString("org_id")+"_"+PERMANENT, rst.getString("count"));
				} else if (rst.getString("emp_status") != null && rst.getString("emp_status").trim().equals(TEMPORARY)) { 
					hmEmpStatuswiseEmps.put(rst.getString("org_id")+"_"+TEMPORARY, rst.getString("count"));
				} else if (rst.getString("emp_status") != null && rst.getString("emp_status").trim().equals(RESIGNED)) { 
					hmEmpStatuswiseEmps.put(rst.getString("org_id")+"_"+RESIGNED, rst.getString("count"));
				}
			}
			rst.close();
			pst.close();
//			 System.out.println("hmEmpStatuswiseEmps ========= >> " + hmEmpStatuswiseEmps);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpStatuswiseEmps;
	}

	
	private Map<String, String> getEmploymentTypewiseEmployeeOrgwise(Connection con) {
		Map<String, String> hmEmpTypewiseEmps = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as count,emptype,org_id from employee_official_details eod, employee_personal_details epd where org_id>0 and " +
				"eod.emp_id = epd.emp_per_id and epd.approved_flag = true and epd.is_alive = true and epd.emp_filled_flag = true ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by emptype, org_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(orgid));
			rst = pst.executeQuery();
//			System.out.println("pst  ====>>>> "+pst);
			while (rst.next()) {
				if(hmFeatureStatus != null ) {
					if(hmFeatureStatus.get(F_USERTYPE_GENERAL) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_GENERAL))) {
						if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("FT")) { //Full Time
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_FULLTIME", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("PT")) { //Part Time
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTTIME", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CO")) { //Consultant
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONSULTANT", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CON")) { //Contractual
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONTRACTUAL", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("I")) { //Intern
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_INTERN", rst.getString("count"));
						}
					}
					
					if(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_HEALTH_CARE))) {
						if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("R")) { //Regular
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_REGULAR", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CT")) { //Contract
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONTRACT", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("PF")) { //Professional
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PROFESSIONAL", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("ST")) { //Stipend
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_STIPEND", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("SCH")) { //Scholarship
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_SCHOLARSHIP", rst.getString("count"));
						}
					}
					
					if(hmFeatureStatus.get(F_USERTYPE_FINANCE) != null && uF.parseToBoolean(hmFeatureStatus.get(F_USERTYPE_FINANCE))) {
						if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("FT")) { //Full Time
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_FULLTIME", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("PT")) { //Part Time
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTTIME", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CO")) { //Consultant
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONSULTANT", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("C")) { //Temporary
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_TEMPORARY", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("AT")) { //Article
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_ARTICLE", rst.getString("count"));
						} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("P")) { //Partner
							hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTNER", rst.getString("count"));
						}
					}
					
				} else {
					if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("FT")) { //Full Time
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_FULLTIME", rst.getString("count"));
					} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("PT")) { //Part Time
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_PARTTIME", rst.getString("count"));
					} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CO")) { //Consultant
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONSULTANT", rst.getString("count"));
					} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("CON")) { //Contractual
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_CONTRACTUAL", rst.getString("count"));
					} else if (rst.getString("emptype") != null && rst.getString("emptype").trim().equals("I")) { //Intern
						hmEmpTypewiseEmps.put(rst.getString("org_id")+"_INTERN", rst.getString("count"));
					}
				}
			}
			rst.close();
			pst.close();
//			 System.out.println("allEmpTypewiseEmps ========= >> " + allEmpTypewiseEmps);  CON  CO Intern I
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpTypewiseEmps;
	}

	
	private void getDisciplinePresentAbsent(Connection con, UtilityFunctions uF, CommonFunctions CF, String strUserType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			int nPresentCnt = 0;
			int nAbsentCnt = 0;

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			String strDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
					+ cal.get(Calendar.YEAR);
			String strCurrentDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			long totDays = uF.getDateDiffinDays(uF.getDateFormatUtil(strDate, DATE_FORMAT), uF.getDateFormatUtil(strCurrentDate, DATE_FORMAT));
			totDays++;

			List<String> alDates = new ArrayList<String>();
			for (int i = 0; i < totDays; i++) {
				String strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
					+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
					+ cal.get(Calendar.YEAR);

				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);
			}

			StringBuilder sbDisciplinePie = new StringBuilder();
			sbDisciplinePie.append("{'Status':'Present', 'cnt': " + nPresentCnt + "},");
			sbDisciplinePie.append("{'Status':'Absent', 'cnt': " + nAbsentCnt + "},");
			if (sbDisciplinePie.length() > 1) {
				sbDisciplinePie.replace(0, sbDisciplinePie.length(), sbDisciplinePie.substring(0, sbDisciplinePie.length() - 1));
			}
			request.setAttribute("sbDisciplinePie", sbDisciplinePie.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getCompensationSummary(Connection con, UtilityFunctions uF, CommonFunctions CF) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		try {

			Map<String, String> hmDepartment = CF.getDepartmentMap(con, null, null);
			if (hmDepartment == null)
				hmDepartment = new HashMap<String, String>();

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DATE, 1);
			SimpleDateFormat dateFormat = new SimpleDateFormat(DBDATE);
			String strPreMonthDate = dateFormat.format(cal.getTime());
			int nMonth = uF.parseToInt(uF.getDateFormat("" + strPreMonthDate, DBDATE, "MM"));
			int nYear = uF.parseToInt(uF.getDateFormat("" + strPreMonthDate, DBDATE, "yyyy"));

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select earning_deduction,sum(amount) as amount,depart_id from payroll_generation pg, employee_official_details eod "
				+ "where pg.emp_id=eod.emp_id and month =? and year =? and is_paid=true and eod.depart_id in (select dept_id from department_info) "
				+ "group by depart_id,earning_deduction order by depart_id,earning_deduction desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, nMonth);
			pst.setInt(2, nYear);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEarningAmount = new LinkedHashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmDeductionAmount = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("E")) {
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("AMOUNT", rs.getString("amount"));

					hmEarningAmount.put(rs.getString("depart_id"), hmInner);
				} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("D")) {
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("AMOUNT", rs.getString("amount"));

					hmDeductionAmount.put(rs.getString("depart_id"), hmInner);
				}
			}
			rs.close();
			pst.close();

			Iterator<String> it = hmDepartment.keySet().iterator();
			List<List<String>> compensationSummaryList = new ArrayList<List<String>>();
			while (it.hasNext()) {
				String strDepartId = it.next();
				String strDepartName = hmDepartment.get(strDepartId);
				strDepartName = strDepartName.replace("'", "\\'");

				Map<String, String> hmEarning = hmEarningAmount.get(strDepartId);
				if (hmEarning == null)
					hmEarning = new HashMap<String, String>();
				Map<String, String> hmDeduction = hmDeductionAmount.get(strDepartId);
				if (hmDeduction == null)
					hmDeduction = new HashMap<String, String>();

				double dblNetSalary = uF.parseToDouble(hmEarning.get("AMOUNT")) - uF.parseToDouble(hmDeduction.get("AMOUNT"));

				List<String> innerList = new ArrayList<String>();
				innerList.add(strDepartName);
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblNetSalary));

				compensationSummaryList.add(innerList);
			}
			request.setAttribute("compensationSummaryList", compensationSummaryList);

			String compensationDate = "Compensation Summary (" + uF.getDateFormat("" + strPreMonthDate, DBDATE, "MMM") + " "
					+ uF.getDateFormat("" + strPreMonthDate, DBDATE, "yy") + ")";
			request.setAttribute("compensationDate", compensationDate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getLeaveSummary(Connection con, UtilityFunctions uF, CommonFunctions CF, String strUserType) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(leave_no) as leave_no,_date from leave_application_register where is_modify=false "
					+ "and leave_id in (select leave_id from emp_leave_entry) and _date between ? and ? ");
			if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id in ("
						+ (String) session.getAttribute(WLOCATION_ACCESS) + "))");
			} else if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER) && (String) session.getAttribute(WLOCATION_ACCESS) == null) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="
						+ uF.parseToInt((String) session.getAttribute(WLOCATIONID)) + ")");
			}
			sbQuery.append(" group by _date order by _date");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getFutureDate(CF.getStrTimeZone(), 7));
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmNoOfLeave = new HashMap<String, String>();
			while (rs.next()) {
				hmNoOfLeave.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_no"));
			}
			rs.close();
			pst.close();

			String strDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")));

			List<List<String>> leaveSummaryList = new ArrayList<List<String>>();
			for (int i = 0; i < 7; i++) {
				String strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.getDateFormat(strD1, DATE_FORMAT, "dd MMM"));
				innerList.add(uF.showData(hmNoOfLeave.get(strD1), "0"));
				leaveSummaryList.add(innerList);

				cal.add(Calendar.DATE, 1);
			}

			request.setAttribute("leaveSummaryList", leaveSummaryList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getAttendanceSummary(UtilityFunctions uF) {
		try {

			AttendanceReport objAR = new AttendanceReport();
			objAR.setServletRequest(request);
			objAR.setF_org((String) session.getAttribute(ORGID));
			objAR.setF_org((String) session.getAttribute(WLOCATIONID));
			// objAR.attendanceReport(CF, uF, strUserType, session);
			objAR.attendanceReportForOneWeek(CF, uF, strUserType, session);

			Map hmEmployeeAttendanceCount = (Map) request.getAttribute("hmEmployeeAttendanceCount");

			List alDates = (List) request.getAttribute("alDates");
			List alServicesTotal = (List) request.getAttribute("alServicesTotal");
			StringBuilder sbDatesAttendance = new StringBuilder();
			StringBuilder sbDatesAttendanceDate = new StringBuilder();

			sbDatesAttendance.append("{name: 'Came Early',data: [");
			for (int k = 0; k < alDates.size(); k++) {
				Map hm = (Map) hmEmployeeAttendanceCount.get((String) alDates.get(k));
				if (hm == null)
					hm = new HashMap();
				int nCount = 0;
				for (int s = 0; s < alServicesTotal.size(); s++) {
					String strServiceId = (String) alServicesTotal.get(s);
					nCount += uF.parseToInt((String) hm.get("EARLY_IN_" + strServiceId));
				}
				sbDatesAttendance.append(nCount);
				// sbDatesAttendance.append(uF.showData((String)hm.get("EARLY_IN_"+strServiceId),
				// "0"));

				sbDatesAttendanceDate.append("'" + uF.getDateFormat((String) alDates.get(k), DATE_FORMAT, "dd MMM") + "'");
				if (k < alDates.size() - 1) {
					sbDatesAttendance.append(",");
					sbDatesAttendanceDate.append(",");
				}
			}
			sbDatesAttendance.append("]},");

			sbDatesAttendance.append("{name: 'Left Early',data: [");
			for (int k = 0; k < alDates.size(); k++) {
				Map hm = (Map) hmEmployeeAttendanceCount.get((String) alDates.get(k));
				if (hm == null)
					hm = new HashMap();
				int nCount = 0;
				for (int s = 0; s < alServicesTotal.size(); s++) {
					String strServiceId = (String) alServicesTotal.get(s);
					nCount += uF.parseToInt((String) hm.get("EARLY_OUT_" + strServiceId));
				}
				sbDatesAttendance.append(nCount);
				if (k < alDates.size() - 1) {
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");

			sbDatesAttendance.append("{name: 'Came Late',data: [");
			for (int k = 0; k < alDates.size(); k++) {
				Map hm = (Map) hmEmployeeAttendanceCount.get((String) alDates.get(k));
				if (hm == null)
					hm = new HashMap();
				int nCount = 0;

				for (int s = 0; s < alServicesTotal.size(); s++) {
					String strServiceId = (String) alServicesTotal.get(s);

					nCount += uF.parseToInt((String) hm.get("LATE_IN_" + strServiceId));
				}
				sbDatesAttendance.append(nCount);
				if (k < alDates.size() - 1) {
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");

			sbDatesAttendance.append("{name: 'Left Late',data: [");
			for (int k = 0; k < alDates.size(); k++) {
				Map hm = (Map) hmEmployeeAttendanceCount.get((String) alDates.get(k));
				if (hm == null)
					hm = new HashMap();
				int nCount = 0;
				for (int s = 0; s < alServicesTotal.size(); s++) {
					String strServiceId = (String) alServicesTotal.get(s);
					nCount += uF.parseToInt((String) hm.get("LATE_OUT_" + strServiceId));
				}
				sbDatesAttendance.append(nCount);
				if (k < alDates.size() - 1) {
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");

			sbDatesAttendance.append("{name: 'Came Ontime',data: [");
			for (int k = 0; k < alDates.size(); k++) {
				Map hm = (Map) hmEmployeeAttendanceCount.get((String) alDates.get(k));
				if (hm == null)
					hm = new HashMap();
				int nCount = 0;
				for (int s = 0; s < alServicesTotal.size(); s++) {
					String strServiceId = (String) alServicesTotal.get(s);
					nCount += uF.parseToInt((String) hm.get("ONTIME_IN_" + strServiceId));
				}
				sbDatesAttendance.append(nCount);
				if (k < alDates.size() - 1) {
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");

			sbDatesAttendance.append("{name: 'Left Ontime',data: [");
			for (int k = 0; k < alDates.size(); k++) {
				Map hm = (Map) hmEmployeeAttendanceCount.get((String) alDates.get(k));
				if (hm == null)
					hm = new HashMap();
				int nCount = 0;
				for (int s = 0; s < alServicesTotal.size(); s++) {
					String strServiceId = (String) alServicesTotal.get(s);
					nCount += uF.parseToInt((String) hm.get("ONTIME_OUT_" + strServiceId));
				}
				sbDatesAttendance.append(nCount);
				if (k < alDates.size() - 1) {
					sbDatesAttendance.append(",");
				}
			}

			sbDatesAttendance.append("]}");

			request.setAttribute("sbDatesAttendance", sbDatesAttendance);
			request.setAttribute("sbDatesAttendanceDate", sbDatesAttendanceDate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getKRAReview(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			List<String> empList = getEmployeeList(uF);
			StringBuilder sbEmpId = null;
			for (int i = 0; empList != null && !empList.isEmpty() && i < empList.size(); i++) {
				if (sbEmpId == null) {
					sbEmpId = new StringBuilder();
					sbEmpId.append(empList.get(i));
				} else {
					sbEmpId.append("," + empList.get(i));
				}
			}

			Map<String, String> hmKRATaskStatus = new HashMap<String, String>();
			if (sbEmpId != null) {
				pst = con.prepareStatement("select gksrd.*, gk.goal_id from goal_kra_status_rating_details gksrd, goal_kras gk where "
						+ " gksrd.kra_id = gk.goal_kra_id and gksrd.emp_id in (" + sbEmpId.toString() + ") and complete_percent >= 100");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmKRATaskStatus.put(
							rs.getString("emp_id") + "_" + rs.getString("goal_id") + "_" + rs.getString("goal_freq_id") + "_" + rs.getString("kra_id"),
							rs.getString("complete_percent"));
				}
				rs.close();
				pst.close();
			}

			int kraFormCount = 0;
			for (int i = 0; empList != null && !empList.isEmpty() && i < empList.size(); i++) {
				String goalTyp = INDIVIDUAL_GOAL + "," + INDIVIDUAL_KRA + "," + INDIVIDUAL_TARGET + "," + PERSONAL_GOAL + "," + EMPLOYEE_KRA;
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select freq_end_date, freq_start_date, gd.*, gk.kra_description, gk.goal_kra_id, gk.is_assign, gk.kra_weightage, gdf.goal_freq_id, "
						+ " gdf.goal_freq_name from goal_details gd left join goal_kras gk on gd.goal_id=gk.goal_id left join goal_details_frequency gdf on gd.goal_id=gdf.goal_id "
						+ " where gd.emp_ids like '%," + empList.get(i) + ",%' and gd.goal_type in (" + goalTyp + ") and gd.is_close = false ");
				sbQuery.append(" order by freq_start_date");
				pst = con.prepareStatement(sbQuery.toString());
				// System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getInt("goal_type") == EMPLOYEE_KRA && rs.getString("is_assign") != null && rs.getString("is_assign").equals("f")) {
						continue;
					}

					if (rs.getString("freq_end_date") != null) {
						Date dtDeadLine = uF.getDateFormat(rs.getString("freq_end_date"), DBDATE);
						Date dtCurrDate = uF.getCurrentDate(CF.getStrTimeZone());
						// System.out.println("dtDeadLine ===>> " + dtDeadLine +
						// "  -- dtCurrDate ===>> " + dtCurrDate);
						if (dtCurrDate.after(dtDeadLine)
								|| (hmKRATaskStatus != null && uF.parseToInt(hmKRATaskStatus.get(empList.get(i) + "_" + rs.getString("goal_id") + "_"
										+ rs.getString("goal_freq_id") + "_" + rs.getString("goal_kra_id"))) >= 100)) {
							kraFormCount++;
							// System.out.println("kraFormCount ===>> " +
							// kraFormCount);
						}
					}
				}
				rs.close();
				pst.close();
			}

			request.setAttribute("kraFormCount", kraFormCount + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	List<String> getEmployeeList(UtilityFunctions uF) {
		List<String> al = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			if (strUserType != null && strUserType.equals(EMPLOYEE)) {
				al.add(strEmpId);
			} else {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id "
						+ "and is_alive = true ");
				if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(ORG_ACCESS) != null) {
					sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") ");
				}

				if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && (String) session.getAttribute(WLOCATION_ACCESS) != null) {
					sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ") ");
				}

				if (strUserType != null && strUserType.equals(HRMANAGER)) {
					sbQuery.append(" and eod.emp_hr = " + strEmpId + " ");
				}

				if (strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and (eod.supervisor_emp_id = " + strEmpId + " or eod.emp_id = " + strEmpId + ") ");
				}
				sbQuery.append(" order by epd.emp_fname");
				pst = con.prepareStatement(sbQuery.toString());
				// System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (!al.contains(rs.getString("emp_per_id"))) {
						al.add(rs.getString("emp_per_id"));
					}
				}
				rs.close();
				pst.close();

			}
			// System.out.println("al ===>> " + al);
			request.setAttribute("empList", al);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	private void getInterviews(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			// System.out.println("hmRecruitWiseRoundId ===> " +
			// hmRecruitWiseRoundId);
			pst = con.prepareStatement("select count(*) as cnt from candidate_interview_panel cip, candidate_application_details cad "
					+ " where cip.candidate_id = cad.candidate_id and cip.recruitment_id = cad.recruitment_id and cip.panel_user_id = ? and "
					+ " cip.interview_date >= ? and cip.interview_date < ? and status = 0 ");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(3, uF.getFutureDate(CF.getStrTimeZone(), 8));
			rs = pst.executeQuery();
			// System.out.println("pst ===> "+pst);
			int interviewCount = 0;
			while (rs.next()) {
				interviewCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();

			request.setAttribute("interviewCount", interviewCount + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getHRReviews(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		// System.out.println("in appraisal .......... ");
		try {
			String strUserTypeID = (String) request.getAttribute(USERTYPEID);
			StringBuilder sbQuery = new StringBuilder();
			// sbQuery.append("select * from appraisal_details where is_publish=true and is_close = false ");
			sbQuery.append("select * from appraisal_details a,appraisal_details_frequency adf  where a.appraisal_details_id = adf.appraisal_id "
					+ " and (adf.is_delete is null or adf.is_delete = false )  and is_appraisal_publish = true and appraisal_details_id > 0 ");
			if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN))) {
				sbQuery.append(" and hr_ids like '%," + strEmpId + ",%' ");
			} else if (strUserType != null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and supervisor_id like '%," + strEmpId + ",%' ");
			}

			/*
			 * else if (strUserType != null &&
			 * strUserType.equalsIgnoreCase(CEO)) {
			 * sbQuery.append(" and ceo_ids like '%,"+strEmpId+",%' "); } else
			 * if (strUserType != null && strUserType.equalsIgnoreCase(HOD)) {
			 * sbQuery.append(" and hod_ids like '%,"+strEmpId+",%' "); }
			 */

			sbQuery.append(" order by to_date DESC");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> appraisalDetails = new HashMap<String, Map<String, String>>();
			List<String> appraisalIdList = new ArrayList<String>();
			while (rs.next()) {
				Map<String, String> appraisalMp = new HashMap<String, String>();
				appraisalIdList.add(rs.getString("appraisal_details_id") + "_" + rs.getString("appraisal_freq_id"));
				appraisalMp.put("ID", rs.getString("appraisal_details_id"));// 0
				appraisalMp.put("APPRAISAL", rs.getString("appraisal_name"));// 1
				// appraisalMp.put("WLOCATION", rs.getString("wlocation_id"));
				appraisalMp.put("SELFID", rs.getString("self_ids"));// 2
				appraisalMp.put("ORIENTED_TYPE", rs.getString("oriented_type"));// 3

				appraisalDetails.put(rs.getString("appraisal_details_id") + "_" + rs.getString("appraisal_freq_id"), appraisalMp);
			}
			rs.close();
			pst.close();

			Map<String, Map<String, Map<String, String>>> appraisalStatusMp = getEmployeeStatus(con, uF);
			request.setAttribute("appraisalStatusMp", appraisalStatusMp);

			Map<String, String> hmOrientMemberID = getOrientMemberID(con);

			Map<String, Map<String, List<String>>> empMpDetails = new HashMap<String, Map<String, List<String>>>();

			Map<String, Integer> hmSectionCount = new HashMap<String, Integer>();
			// int sectionCount = 0;
			pst = con.prepareStatement("select count(main_level_id) as sectionCnt, appraisal_id from appraisal_main_level_details group by appraisal_id");
			// pst.setInt(1, uF.parseToInt(appraisalIdList.get(i)));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmSectionCount.put(rs.getString("appraisal_id"), rs.getInt("sectionCnt"));
				// sectionCount = rs.getInt("sectionCnt");
			}
			rs.close();
			pst.close();

			// System.out.println(appraisalIdList.get(i)+" ::: "+sectionCount);

			Map<String, Integer> hmExistSectionCount = new HashMap<String, Integer>();
			pst = con
					.prepareStatement("select count(distinct section_id) as existSectionCnt,emp_id, appraisal_id, appraisal_freq_id from appraisal_question_answer "
							+ "where user_id =? and user_type_id = ? group by appraisal_id,emp_id,appraisal_freq_id");
			// pst.setInt(1, uF.parseToInt(appraisalIdList.get(i)));
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strUserTypeID));
			rs = pst.executeQuery();
			// System.out.println("pst ::: "+pst);
			while (rs.next()) {
				hmExistSectionCount.put(rs.getString("appraisal_id") + "_" + rs.getString("appraisal_freq_id") + "_" + rs.getString("emp_id"),
						rs.getInt("existSectionCnt"));
			}
			rs.close();
			pst.close();
			// System.out.println(appraisalIdList.get(i)+" ::: "+hmExistSectionCount);
			Map<String, String> orientationMp = CF.getOrientationValue(con);

			int reviewEmpCount = 0;
			for (int i = 0; i < appraisalIdList.size(); i++) {

				Map<String, String> appraisalMp = appraisalDetails.get(appraisalIdList.get(i));
				Map<String, List<String>> empMp = new HashMap<String, List<String>>();

				Map<String, Map<String, String>> userTypeMp = appraisalStatusMp.get(appraisalIdList.get(i));
				if (userTypeMp == null)
					userTypeMp = new HashMap<String, Map<String, String>>();

				// Map<String, String> orientationMemberPositionMp =
				// getOrientPositions(appraisalIdList.get(i));
				// System.out.println("orientationMemberPositionMp ::::::::::: "+orientationMemberPositionMp);

				String self = appraisalMp.get("SELFID");
				self = self != null && !self.equals("") ? self.substring(1, self.length() - 1) : "";
				int oriented_type = uF.parseToInt(appraisalMp.get("ORIENTED_TYPE"));
				List<String> memberList = CF.getOrientationMemberDetails(con, oriented_type);
				List<String> employeeList = null;
				if (strUserType != null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN))) {
					if (hmOrientMemberID.get("HR") != null && memberList.contains(hmOrientMemberID.get("HR"))) {

						Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("HR"));
						if (empstatusMp == null)
							empstatusMp = new HashMap<String, String>();
						employeeList = new ArrayList<String>();
						pst = con
								.prepareStatement("select distinct(emp_per_id) from employee_official_details eod,employee_personal_details epd,appraisal_details ad"
										+ " where epd.emp_per_id=eod.emp_id  and emp_per_id in("
										+ self
										+ ") and ad.hr_ids like '%,"
										+ strEmpId
										+ ",%' order by emp_per_id"); // and
																		// eod.wlocation_id=?
						// pst.setInt(1,
						// uF.parseToInt(hmUserDetails.get("WLOCATION")));
						rs = pst.executeQuery();
						// System.out.println("pst HRManager =====>"+pst);
						while (rs.next()) {
							if (empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")) == null) {
								// System.out.println("Emp ID in if "+rs.getString("emp_per_id"));
								employeeList.add(rs.getString("emp_per_id"));
								reviewEmpCount++;
							} else if (hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
								// System.out.println("Emp ID in else if "+rs.getString("emp_per_id"));
								if (hmExistSectionCount.get(appraisalIdList.get(i) + "_" + rs.getString("emp_per_id")) != null
										&& hmSectionCount.get(appraisalMp.get("ID")) != null
										&& hmSectionCount.get(appraisalMp.get("ID")) != hmExistSectionCount.get(appraisalIdList.get(i) + "_"
												+ rs.getString("emp_per_id"))) {
									// System.out.println("Emp ID in else if if ::::: "+rs.getString("emp_per_id"));
									employeeList.add(rs.getString("emp_per_id"));
									reviewEmpCount++;
								}
							}
						}
						rs.close();
						pst.close();
						// ---------------------------------------------------------------------------------------
						empMp.put(hmOrientMemberID.get("HR"), employeeList);

					}

				} else if (strUserType != null && strUserType.equalsIgnoreCase(MANAGER)) {
					if (hmOrientMemberID.get("Manager") != null && memberList.contains(hmOrientMemberID.get("Manager"))) {
						Map<String, String> empstatusMp = userTypeMp.get(hmOrientMemberID.get("Manager"));
						if (empstatusMp == null)
							empstatusMp = new HashMap<String, String>();
						employeeList = new ArrayList<String>();

						pst = con
								.prepareStatement("select distinct(emp_per_id) from employee_official_details eod,employee_personal_details epd,appraisal_details ad"
										+ " where epd.emp_per_id=eod.emp_id  and emp_per_id in("
										+ self
										+ ") and ad.supervisor_id like '%,"
										+ strEmpId
										+ ",%' order by emp_per_id"); // and
																		// eod.wlocation_id=?
						// pst.setInt(1,
						// uF.parseToInt(hmUserDetails.get("WLOCATION")));
						rs = pst.executeQuery();
						// System.out.println("pst manager =====>"+pst);
						while (rs.next()) {
							if (empstatusMp != null && empstatusMp.get(rs.getString("emp_per_id")) == null) {
								employeeList.add(rs.getString("emp_per_id"));
								reviewEmpCount++;
							} else if (hmExistSectionCount != null && !hmExistSectionCount.isEmpty()) {
								// System.out.println("Emp ID in else if "+rs.getString("emp_per_id"));
								if (hmExistSectionCount.get(appraisalIdList.get(i) + "_" + rs.getString("emp_per_id")) != null
										&& hmSectionCount.get(appraisalMp.get("ID")) != null
										&& hmSectionCount.get(appraisalMp.get("ID")) != hmExistSectionCount.get(appraisalIdList.get(i) + "_"
												+ rs.getString("emp_per_id"))) {
									// System.out.println("Emp ID in else if if ::::: "+rs.getString("emp_per_id"));
									employeeList.add(rs.getString("emp_per_id"));
									reviewEmpCount++;
								}
							}
						}
						rs.close();
						pst.close();
						// -----------------------------------------------------------------------------------
						empMp.put(hmOrientMemberID.get("Manager"), employeeList);
						// }
					}
				}
				empMpDetails.put(appraisalIdList.get(i), empMp);

				// System.out.println("empMpDetails ::::: "+
				// appraisalIdList.get(i)+" - " + empMpDetails);
			}
			// System.out.println("hmRemainOrientDetailsForPeerAppWise ::::: "+hmRemainOrientDetailsForPeerAppWise);
			// System.out.println("empMpDetails ::::: "+empMpDetails);

			request.setAttribute("reviewEmpCount", "" + reviewEmpCount);
			request.setAttribute("empMpDetails", empMpDetails);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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
			// System.out.println("memberid=====>"+orientationMemberMp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orientationMemberMp;
	}

	public Map<String, Map<String, Map<String, String>>> getEmployeeStatus(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, Map<String, String>>> appraisalMp = new HashMap<String, Map<String, Map<String, String>>>();
		try {
			pst = con
					.prepareStatement("select emp_id,appraisal_id,user_type_id,user_id,appraisal_freq_id from appraisal_question_answer group by emp_id,appraisal_id,"
							+ " user_type_id,user_id,appraisal_freq_id order by emp_id");
			rs = pst.executeQuery();
			while (rs.next()) {

				Map<String, Map<String, String>> userTypeMp = appraisalMp.get(rs.getString("appraisal_id") + "_" + rs.getString("appraisal_freq_id"));
				if (userTypeMp == null)
					userTypeMp = new HashMap<String, Map<String, String>>();
				Map<String, String> empMp = userTypeMp.get(rs.getString("user_type_id"));
				if (empMp == null)
					empMp = new HashMap<String, String>();

				// empMp.put(rs.getString("emp_id")+""+rs.getString("user_id")+""+rs.getString("user_type_id"),
				// rs.getString("emp_id"));

				if (uF.parseToInt(rs.getString("user_type_id")) == 4 || uF.parseToInt(rs.getString("user_type_id")) == 10) {
					empMp.put(rs.getString("emp_id") + "_" + rs.getString("user_id"), rs.getString("emp_id"));
				} else {
					empMp.put(rs.getString("emp_id"), rs.getString("emp_id"));
				}
				userTypeMp.put(rs.getString("user_type_id"), empMp);
				// System.out.println("userTypeMp :: "+userTypeMp);
				appraisalMp.put(rs.getString("appraisal_id") + "_" + rs.getString("appraisal_freq_id"), userTypeMp);
				// System.out.println("appraisalMp :: "+appraisalMp);
			}
			rs.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return appraisalMp;
	}

	private void getInductionData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		try {

			int totalInduction = 0;
			java.sql.Date dayAfterTomorrowDate1 = uF.getFutureDate(CF.getStrTimeZone(), 2);
			java.sql.Date tomorrowDate1 = uF.getFutureDate(CF.getStrTimeZone(), 1);

			StringBuilder sbQuery = new StringBuilder();

			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id "
					+ "and (joining_date =? or joining_date =? or joining_date =?) and joining_date is not null ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}
			sbQuery.append(" order by emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, tomorrowDate1);
			pst.setDate(3, dayAfterTomorrowDate1);
			rst = pst.executeQuery();
			while (rst.next()) {
				totalInduction = totalInduction + 1;
			}
			rst.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details  epd,employee_official_details eod where epd.emp_per_id = eod.emp_id and (joining_date is null or joining_date < ?) and ((approved_flag=false and is_alive=false and emp_filled_flag=false) or (approved_flag=false and is_alive=false and emp_filled_flag=true)) ");
			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and (wlocation_id is null or wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + "))");
				} else {
					sbQuery.append(" and (wlocation_id is null or wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + "))");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and (org_id is null or org_id in (" + (String) session.getAttribute(ORG_ACCESS) + "))");
				} else {
					sbQuery.append(" and (org_id is null or org_id in (" + (String) session.getAttribute(ORGID) + "))");
				}
			}

			sbQuery.append(" order by emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rst = pst.executeQuery();
			while (rst.next()) {
				totalInduction = totalInduction + 1;
			}
			rst.close();
			pst.close();

			request.setAttribute("totalInduction", totalInduction + "");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	private void getRetirementData(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst=null;

		try {
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
					
			List<String> retirementEmpList = new ArrayList<String>();
			Map<String, String> hmwLocation = CF.getWLocationMap(con, null, null);
			
			Map<String, String> hmOrgRetirementAge = new HashMap<String, String>();
			pst = con.prepareStatement("select * from org_details");
			rst = pst.executeQuery();
			while(rst.next()) {
				hmOrgRetirementAge.put(rst.getString("org_id"), rst.getString("retirement_age"));
			}
			rst.close();
			pst.close();
//			System.out.println("hmOrgRetirementAge ===>> " + hmOrgRetirementAge);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and approved_flag=true and is_alive=true and emp_filled_flag=true and joining_date is not null ");
			
			 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
	        		sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
	        	} else {
	        		sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
	        	}
			}
	            
	        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
	        	if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
	        		sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
	        	} else {
	        		sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
	        	}
			}
	        sbQuery.append(" order by epd.emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			while (rst.next()) {
//				System.out.println(rst.getString("emp_per_id") + " -- emp_date_of_birth ===>> " + rst.getString("emp_date_of_birth"));
				if(rst.getString("emp_date_of_birth")==null) {
					continue;
				}
				String strEmpAge = uF.getTimeDurationBetweenDatesWithYearMonthDays(rst.getString("emp_date_of_birth"), DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF, uF, request);
//				System.out.println(rst.getString("emp_per_id") +" --- " + rst.getString("emp_fname") + " " + rst.getString("emp_lname") + " -- strEmpAge ===>> " + strEmpAge);
				if(strEmpAge != null) {
					String[] strTmpEmpAge = strEmpAge.split("::::");
					String strRetireAge = hmOrgRetirementAge.get(rst.getString("org_id"));
					if(uF.parseToInt(strTmpEmpAge[0]) >= uF.parseToInt(strRetireAge) || (uF.parseToInt(strTmpEmpAge[0]) == (uF.parseToInt(strRetireAge)-1) && uF.parseToInt(strTmpEmpAge[1]) == 11)) {
						StringBuilder sbRetirementList = new StringBuilder();
						String empimg = uF.showData(rst.getString("emp_image"), "avatar_photo.png");
						String supervisorName = CF.getEmpNameMapByEmpId(con, rst.getString("supervisor_emp_id"));
						String empDesignation = CF.getEmpDesigMapByEmpId(con, rst.getString("emp_per_id"));
						if(rst.getString("joining_date")!=null && !rst.getString("joining_date").equals("")) {
							String empDOBDate = uF.getDateFormat(rst.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT);
							String strDay = uF.getDateFormat(empDOBDate, DATE_FORMAT, "dd");
							String strMonth = uF.getDateFormat(empDOBDate, DATE_FORMAT, "MM");
							String strYear = uF.getDateFormat(empDOBDate, DATE_FORMAT, "yyyy");
							int intRetirementYr = uF.parseToInt(strYear)+uF.parseToInt(strRetireAge);
							String retirementDate = strDay+"/"+strMonth+"/"+intRetirementYr;		
							
							if(CF.getStrDocRetriveLocation()==null) { 
								sbRetirementList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
							} else { 
								sbRetirementList.append("<span style=\"float: left; width:20px; height:20px; \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rst.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
							}  
							sbRetirementList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
							
							String strEmpMName = "";
							if(flagMiddleName) {
								if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
									strEmpMName = " "+rst.getString("emp_mname");
								}
							}
							
							sbRetirementList.append(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
							sbRetirementList.append(" designation is "+uF.showData(empDesignation, "-"));
							sbRetirementList.append(", manager is "+uF.showData(supervisorName, "-"));
							sbRetirementList.append(", work location is "+uF.showData(hmwLocation.get(rst.getString("wlocation_id")), "-"));
							sbRetirementList.append(" and retirement date is "+retirementDate+".");
							sbRetirementList.append("</span>");
							
							retirementEmpList.add(sbRetirementList.toString());
						}
					}
				}
			}
			rst.close();
			pst.close();
			
//			System.out.println("retirementEmpList==>"+retirementEmpList.size());
			request.setAttribute("retirementCnt", retirementEmpList.size()+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void getResignationData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			String strUserTypeId = (String) session.getAttribute(USERTYPEID);
			String strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
			String strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);

			Map<String, String> hmEmpProbation = new HashMap<String, String>();
			java.util.Date tommorowDate = uF.getDateFormatUtil(
					uF.getDateFormat("" + uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1), DBDATE, DATE_FORMAT), DATE_FORMAT);
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(
					uF.getDateFormat("" + uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2), DBDATE, DATE_FORMAT), DATE_FORMAT);
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);

			// 1
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where "
					+ " ((is_approved=0 and effective_type='" + WORK_FLOW_RESIGN + "') or (is_approved=1 and effective_type='" + WORK_FLOW_TERMINATION + "'))"
					+ " and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from "
					+ " employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst1==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while (rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();

			// 2
			sbQuery = new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? "
					+ " and ((is_approved=0 and effective_type='" + WORK_FLOW_RESIGN + "') or (is_approved=1 and effective_type='" + WORK_FLOW_TERMINATION
					+ "')) "
					+ " and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from employee_personal_details epd, "
					+ " employee_official_details eod where epd.emp_per_id = eod.emp_id ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}
			sbQuery.append("))");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strUserTypeId));
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
			}

			// System.out.println("pst2==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while (rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id") + "_" + rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();

			// 3
			sbQuery = new StringBuilder();
			sbQuery.append("select off_board_id from emp_off_board where "
					+ "emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " + "where epd.emp_per_id = eod.emp_id ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}
			sbQuery.append(") and approved_1=-1 and approved_2=-1 ");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst3==>"+pst);
			rs = pst.executeQuery();
			List<String> deniedList = new ArrayList<String>();
			while (rs.next()) {
				if (!deniedList.contains(rs.getString("off_board_id"))) {
					deniedList.add(rs.getString("off_board_id"));
				}
			}
			rs.close();
			pst.close();

			// 4
			sbQuery = new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='" + WORK_FLOW_RESIGN + "' "
					+ "and effective_id in(select off_board_id from emp_off_board where "
					+ "emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " + "where epd.emp_per_id = eod.emp_id ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}
			sbQuery.append(")) group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst4==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();

			// 5
			sbQuery = new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " + " and (effective_type='"
					+ WORK_FLOW_RESIGN + "' or effective_type='" + WORK_FLOW_TERMINATION
					+ "') and effective_id in(select off_board_id from emp_off_board where "
					+ "emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " + "where epd.emp_per_id = eod.emp_id ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}
			sbQuery.append("))  group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst5==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();
			while (rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();

			// 6
			sbQuery = new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " + " and (effective_type='" + WORK_FLOW_RESIGN
					+ "' or effective_type='" + WORK_FLOW_TERMINATION + "') and effective_id in(select off_board_id from emp_off_board where "
					+ "emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " + "where epd.emp_per_id = eod.emp_id ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}
			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst6==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();
			Map<String, String> hmWorkFlowUserTypeId = new HashMap<String, String>();
			while (rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();

			// 7
			sbQuery = new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " + " and (effective_type='" + WORK_FLOW_RESIGN
					+ "' or effective_type='" + WORK_FLOW_TERMINATION + "') and effective_id in(select off_board_id from emp_off_board where "
					+ "emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " + "where epd.emp_per_id = eod.emp_id ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}

			sbQuery.append(")) group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst7==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();
			while (rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();

			// 8
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,effective_id from work_flow_details where (effective_type='"
					+ WORK_FLOW_RESIGN
					+ "' or effective_type='"
					+ WORK_FLOW_TERMINATION
					+ "')"
					+ " and effective_id in(select off_board_id from emp_off_board where emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod "
					+ " where epd.emp_per_id = eod.emp_id ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}

			sbQuery.append(")) ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strUserTypeId));
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			}
			// System.out.println("pst8==>"+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
			while (rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
				if (checkEmpList == null)
					checkEmpList = new ArrayList<String>();
				checkEmpList.add(rs.getString("emp_id"));

				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();

			// 9
			sbQuery = new StringBuilder();
			sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id "
					+ " and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id and epd.emp_per_id in (select emp_id from user_details where status != 'INACTIVE') ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and eod.wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and eod.org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}

			sbQuery.append(") e, work_flow_details wfd where e.off_board_id = wfd.effective_id and (wfd.effective_type = '" + WORK_FLOW_RESIGN
					+ "' or wfd.effective_type = '" + WORK_FLOW_TERMINATION + "') ");
			if (strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = " + uF.parseToInt(strEmpId) + " ");
				if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(CEO) || strBaseUserType.equalsIgnoreCase(HOD))) {
					sbQuery.append(" and (wfd.user_type_id = " + uF.parseToInt(strUserTypeId) + " or wfd.user_type_id = " + uF.parseToInt(strBaseUserTypeId)
							+ " ) ");
				} else {
					sbQuery.append(" and wfd.user_type_id = " + uF.parseToInt(strUserTypeId) + " ");
				}
			}
			sbQuery.append(" order by e.entry_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("cron resig pst==>"+pst);
			rs = pst.executeQuery();
			List<String> alList = new ArrayList<String>();

			int totalResignation = 0;
			int totalFD = 0;
			while (rs.next()) {

				List<String> checkEmpList = hmCheckEmp.get(rs.getString("off_board_id"));
				if (checkEmpList == null)
					checkEmpList = new ArrayList<String>();

				if (!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}

				String userType = rs.getString("user_type");
				if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))
						&& alList.contains(rs.getString("off_board_id"))) {
					continue;
				} else if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))
						&& !alList.contains(rs.getString("off_board_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("off_board_id"));
				}

				if (rs.getString("emp_status") != null && rs.getString("emp_status").equalsIgnoreCase(RESIGNED)) {
					if (rs.getString("entry_date") != null && !rs.getString("entry_date").equals("")) {
						String lastDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT);
						java.util.Date regDate = uF.getDateFormatUtil(lastDate, DATE_FORMAT);

						if (regDate != null && regDate.equals(currDate)) {
							totalResignation++;
						} else {
							// if(rs.getString("emp_status")!=null &&
							// !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")){
							if (rs.getBoolean("is_alive")) {
								totalResignation++;
							}
							// }
						}

					}
				}

				// getting Final day alert counter
				if (rs.getString("emp_status") != null
						&& (rs.getString("emp_status").equalsIgnoreCase(RESIGNED) || rs.getString("emp_status").equalsIgnoreCase(TERMINATED))) {
					if (rs.getString("last_day_date") != null && !rs.getString("last_day_date").equals("")) {
						String resignDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, DATE_FORMAT);
						java.util.Date resignationDate = uF.getDateFormatUtil(resignDate, DATE_FORMAT);

						if (rs.getInt("approved_1") == 1 && rs.getInt("approved_2") == 1) {
							if (resignationDate.equals(currDate) || resignationDate.equals(tommorowDate) || resignationDate.equals(dayAfterTomorrowDate)) {
								totalFD++;
							} else if (resignationDate.before(currDate)) {
								if (rs.getBoolean("is_alive") || rs.getString("emp_status").equalsIgnoreCase(TERMINATED)) {
									totalFD++;
								}
							}
						}
					}
				}
			}

			rs.close();
			pst.close();

			request.setAttribute("allResigCnt", totalResignation + "");
			request.setAttribute("allFinalDayCnt", totalFD + "");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void getConfirmationData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		try {

			Map<String, String> hmEmpProbation = new HashMap<String, String>();

			StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select * from probation_policy order by emp_id desc");
			pst = con.prepareStatement(sbQuery1.toString());
			rst = pst.executeQuery();
			while (rst.next()) {
				int probation = uF.parseToInt((String) rst.getString("probation_duration"))
						+ uF.parseToInt((String) rst.getString("extend_probation_duration"));
				hmEmpProbation.put((String) rst.getString("emp_id"), String.valueOf(probation));
			}
			rst.close();
			pst.close();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id "
					+ "and epd.emp_status = 'PROBATION' and approved_flag=true and is_alive=true and emp_filled_flag=true and joining_date is not null ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
				} else {
					sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + ")");
				}
			}
			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ")");
				} else {
					sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORGID) + ")");
				}
			}
			sbQuery.append(" order by epd.emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			int confirmCnt = 0;
			while (rst.next()) {
				if (rst.getString("joining_date") != null && !rst.getString("joining_date").trim().equals("")) {
					String joiningDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT);
					java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);
					java.util.Date startDate = uF.getDateFormatUtil(joiningDate, DATE_FORMAT);

					int probation = uF.parseToInt(hmEmpProbation.get((String) rst.getString("emp_per_id")));

					String futureDate = uF.getDateFormat("" + uF.getFutureDate(startDate, probation), DBDATE, DATE_FORMAT);
					java.util.Date confDate = null;
					if (probation > 0) {
						confDate = uF.getDateFormatUtil(futureDate, DATE_FORMAT);
					} else {
						confDate = uF.getDateFormatUtil(joiningDate, DATE_FORMAT);
					}
					java.util.Date tommorowDate = uF.getDateFormatUtil(uF.getDateFormat("" + uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1), DBDATE, DATE_FORMAT), DATE_FORMAT);
					java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat("" + uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2), DBDATE, DATE_FORMAT), DATE_FORMAT);

					if (confDate.equals(currDate) || confDate.equals(tommorowDate) || confDate.equals(dayAfterTomorrowDate) || confDate.before(currDate)) {
						confirmCnt++;
					}
				}
			}
			rst.close();
			pst.close();

			request.setAttribute("confirmCnt", confirmCnt + "");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void getLearningGapDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			int gapCount = 0;
			StringBuilder sbQuery = new StringBuilder();
			// sbQuery.append("select * from training_gap_details where appraisal_id > 0 and (attribute_id is not null or attribute_id > 0) and is_training_schedule=false ");
			sbQuery.append("select count(*) as gapCount from training_gap_details tgd, appraisal_details ad where tgd.appraisal_id = ad.appraisal_details_id and "
					+ " tgd.appraisal_id > 0 and (tgd.attribute_id is not null or tgd.attribute_id > 0) and tgd.is_training_schedule=false ");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				gapCount = rs.getInt("gapCount");
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			// sbQuery.append("select * from training_gap_details where learning_id >0 and (learning_attribute_ids is not null or learning_attribute_ids !='') and is_training_schedule=false ");
			sbQuery.append("select count(*) as gapCount from training_gap_details tgd, learning_plan_details lpd where lpd.learning_plan_id = tgd.learning_id and "
					+ " tgd.learning_id >0 and (tgd.learning_attribute_ids is not null or tgd.learning_attribute_ids !='') and tgd.is_training_schedule=false ");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				gapCount += rs.getInt("gapCount");
			}
			rs.close();
			pst.close();

			request.setAttribute("gapCount", gapCount + "");
			// System.out.println("outerList=====>"+outerList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getJobProfileCounter(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(r.*) as recruitCnt from recruitment_details r left join grades_details g using(grade_id) join work_location_info w "
					+ " on r.wlocation=w.wlocation_id left join employee_personal_details e on r.added_by=e.emp_per_id left join department_info di "
					+ " on r.dept_id=di.dept_id left  join designation_details d on r.designation_id=d.designation_id left join level_details l "
					+ " on r.level_id=l.level_id where r.status=1 and r.job_approval_status = 0 ");
			if (strBaseUserType != null && strBaseUserType.equals(HRMANAGER)) {
				sbQuery.append(" and r.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") and wlocation in ("
						+ (String) session.getAttribute(WLOCATION_ACCESS) + ") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			int strCurrJobProfiles = 0;
			rs = pst.executeQuery();
			// System.out.println("pst :::::::::: "+pst);
			while (rs.next()) {
				strCurrJobProfiles = rs.getInt("recruitCnt");
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(r.*) as recruitCnt from recruitment_details r left join grades_details g using(grade_id) join work_location_info w "
					+ " on r.wlocation=w.wlocation_id left join employee_personal_details e on r.added_by=e.emp_per_id left join department_info di "
					+ " on r.dept_id=di.dept_id left  join designation_details d on r.designation_id=d.designation_id left join level_details l "
					+ " on r.level_id=l.level_id where r.status=0 and r.job_approval_status = 0 ");
			if (strBaseUserType != null && strBaseUserType.equals(HRMANAGER)) {
				sbQuery.append(" and r.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") and wlocation in ("
						+ (String) session.getAttribute(WLOCATION_ACCESS) + ") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			int strNewRequisitons = 0;
			rs = pst.executeQuery();
			// System.out.println("pst :::::::::: "+pst);
			while (rs.next()) {
				strNewRequisitons = rs.getInt("recruitCnt");
			}
			rs.close();
			pst.close();
			request.setAttribute("strNewRequisitons", strNewRequisitons+ "");
			
			request.setAttribute("strCurrJobProfiles", strCurrJobProfiles + "");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// private void getLearningDetails(Connection con, UtilityFunctions uF) {
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// try {
	//
	// List<List<String>> learningDetails = new ArrayList<List<String>>();
	// // pst =
	// con.prepareStatement("select * from learning_plan_details where learning_plan_id in (select learning_plan_id from "
	// +
	// //
	// "learning_plan_stage_details where ? between from_date and to_date group by learning_plan_id)");
	// pst =
	// con.prepareStatement("select * from learning_plan_details where is_publish = true and is_close = false");
	// // pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
	// rs = pst.executeQuery();
	// //System.out.println("pst :::::::::: "+pst);
	// while(rs.next()) {
	// List<String> learningInner = new ArrayList<String>();
	// learningInner.add(rs.getString("learning_plan_name"));
	// // traininginner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE,
	// CF.getStrReportDateFormat()));
	// int totemp =
	// uF.parseToInt(getLearningEmpCount(rs.getString("learner_ids")));
	// int ongoingEmp = getLearningPlanOngoingEmpCount(con, uF,
	// rs.getString("learning_plan_id"));
	// int pendingEmp = totemp - ongoingEmp;
	// learningInner.add(totemp+"");
	// learningInner.add(""+ongoingEmp);
	// learningInner.add(pendingEmp+"");
	// learningDetails.add(learningInner);
	// }
	// rs.close();
	// pst.close();
	// request.setAttribute("learningDetails", learningDetails);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }

	public int getLearningPlanOngoingEmpCount(Connection con, UtilityFunctions uF, String lPlanId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			List<String> empIdList = new ArrayList<String>();
			// StringBuilder sbEmpIds = new StringBuilder();
			pst = con.prepareStatement("select count(distinct emp_id) as count, emp_id from course_read_details where learning_plan_id = ? group by emp_id");
			pst.setInt(1, uF.parseToInt(lPlanId));
			rst = pst.executeQuery();
			// System.out.println("pst course_read_details ===> " + pst);
			while (rst.next()) {
				if (!empIdList.contains(rst.getString("emp_id"))) {
					count += rst.getInt("count");
					empIdList.add(rst.getString("emp_id"));
					// sbEmpIds.append(rst.getString("emp_id")+",");
				}
			}
			rst.close();
			pst.close();

			pst = con
					.prepareStatement("select count(distinct emp_id) as count, tad.emp_id from training_attend_details tad where tad.learning_plan_id = ? "
							+ "and tad.emp_id not in(select crd.emp_id from course_read_details crd where crd.learning_plan_id =? group by crd.emp_id) group by tad.emp_id");
			pst.setInt(1, uF.parseToInt(lPlanId));
			pst.setInt(2, uF.parseToInt(lPlanId));
			rst = pst.executeQuery();
			// System.out.println("pst training_attend_details ===> " + pst);
			while (rst.next()) {
				if (!empIdList.contains(rst.getString("emp_id"))) {
					count += rst.getInt("count");
					empIdList.add(rst.getString("emp_id"));
					// sbEmpIds.append(rst.getString("emp_id")+",");
				}
			}
			rst.close();
			pst.close();

			String empIds = getAppendData(empIdList);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(distinct emp_id) as count from assessment_question_answer where learning_plan_id = ? ");
			if (!empIds.equals("")) {
				sbQuery.append("and emp_id not in(" + empIds + ")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(lPlanId));
			// System.out.println("pst assessment_question_answer 1 ===> " +
			// pst);
			rst = pst.executeQuery();
			// System.out.println("pst assessment_question_answer ===> " + pst);
			while (rst.next()) {
				count += rst.getInt("count");
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return count;
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

	// public void getRecruitmentDetails(Connection con, UtilityFunctions uF) {
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// Map<String, String> hmOfferAcceptCandiCount = new HashMap<String,
	// String>();
	// pst =
	// con.prepareStatement("select count(*) as offerAccept, rd.recruitment_id from candidate_application_details cpd, recruitment_details rd where cpd.recruitment_id  = rd.recruitment_id and candidate_status = 1 group by rd.recruitment_id");
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// hmOfferAcceptCandiCount.put(rs.getString("recruitment_id"),
	// rs.getString("offerAccept"));
	// }
	// rs.close();
	// pst.close();
	//
	// List<List<String>> alRecruitment = new ArrayList<List<String>>();
	// List<String> alInner = new ArrayList<String>();
	// pst =
	// con.prepareStatement("select no_position,recruitment_id,job_code from recruitment_details where close_job_status = false and job_approval_status = 1");
	// //? between effective_date and target_deadline and
	// // pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// alInner = new ArrayList<String>();
	//
	// alInner.add(rs.getString("job_code"));
	// alInner.add(""+uF.parseToInt(rs.getString("no_position")));
	// alInner.add(""+uF.parseToInt(hmOfferAcceptCandiCount.get(rs.getString("recruitment_id"))));
	// int remainingRequirement = uF.parseToInt(rs.getString("no_position"))
	// -uF.parseToInt(hmOfferAcceptCandiCount.get(rs.getString("recruitment_id")));
	// alInner.add(""+(remainingRequirement > 0 ? remainingRequirement : "0"));
	// alRecruitment.add(alInner);
	// }
	// rs.close();
	// pst.close();
	//
	// request.setAttribute("alRecruitment", alRecruitment);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }

	private void getPerformanceDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> freqNamehm = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while (rs.next()) {
				freqNamehm.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();

			List<List<String>> liveAppraisalDetails = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_details where is_publish = TRUE and is_close = false and " + // ?
																																// between
																																// from_date
																																// and
																																// to_date
																																// and
					"my_review_status = 0 order by appraisal_details_id");
			// pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			// System.out.println("pst :::::::::: "+pst);
			while (rs.next()) {
				List<String> appraisalinner = new ArrayList<String>();
				appraisalinner.add(rs.getString("appraisal_name"));
				appraisalinner.add(freqNamehm.get(rs.getString("frequency")));
				int totemp = uF.parseToInt(getLearningEmpCount(rs.getString("employee_id")));
				int totempfinal = uF.parseToInt(getEmpFinalAppraisal(con, rs.getString("appraisal_details_id")));
				int totunfinalemp = totemp - totempfinal;
				appraisalinner.add(totemp + "");
				appraisalinner.add(totempfinal + "");
				appraisalinner.add(totunfinalemp + "");
				liveAppraisalDetails.add(appraisalinner);
			}
			rs.close();
			pst.close();

			request.setAttribute("liveAppraisalDetails", liveAppraisalDetails);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getLearningEmpCount(String empList) {
		int empCount = 0;
		try {
			if (empList != null && !empList.equals("")) {
				UtilityFunctions uF = new UtilityFunctions();
				List<String> alEmp = Arrays.asList(empList.split(","));
				for (int i = 0; alEmp != null && i < alEmp.size(); i++) {
					if (uF.parseToInt(alEmp.get(i).trim()) == 0) {
						continue;
					}
					empCount++;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empCount + "";
	}

	private String getEmpFinalAppraisal(Connection con, String appraisalid) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String empfinalcount = "0";
		try {
			pst = con.prepareStatement("select count(*) as count from appraisal_final_sattlement where appraisal_id =? and if_approved = TRUE ");
			pst.setInt(1, uF.parseToInt(appraisalid));
			rs = pst.executeQuery();
			while (rs.next()) {
				empfinalcount = rs.getString("count");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return empfinalcount;
	}

	private void getLearningProgress(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select learning_plan_stage_id,lpd.learning_plan_id,lpd.learner_ids,from_date,to_date from "
					+ "learning_plan_stage_details lpsd, learning_plan_details lpd where lpd.learning_plan_id = lpsd.learning_plan_id "
					+ "and is_publish = true ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and org_id in ('" + (String) session.getAttribute(ORG_ACCESS) + "')");
			}
			sbQuery.append(" order by lpd.learning_plan_id");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int learningCount = 0;
			StringBuilder sbLearnStageIds = null;
			while (rs.next()) {
				if (rs.getString("from_date") != null && rs.getString("to_date") != null) {
					java.util.Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
					java.util.Date startDate = uF.getDateFormatUtil(rs.getString("from_date"), DBDATE);
					java.util.Date endDate = uF.getDateFormatUtil(rs.getString("to_date"), DBDATE);

					if ((currDate.after(startDate) || currDate.equals(startDate)) && (currDate.before(endDate) || currDate.equals(endDate))) {
						if (rs.getString("learner_ids") != null) {
							if (sbLearnStageIds == null) {
								sbLearnStageIds = new StringBuilder();
								sbLearnStageIds.append(rs.getString("learning_plan_stage_id"));
							} else {
								sbLearnStageIds.append("," + rs.getString("learning_plan_stage_id"));
							}
							List<String> alList = Arrays.asList(rs.getString("learner_ids").substring(1, rs.getString("learner_ids").length() - 1).split(","));
							learningCount += alList.size();
						}
					}
				}
			}
			rs.close();
			pst.close();
			// System.out.println("learningCount =====> "+learningCount);
			request.setAttribute("learningCount", learningCount + "");

			if (sbLearnStageIds != null) {
				sbQuery = new StringBuilder();
				sbQuery.append("select learning_type,learning_plan_stage_name_id from learning_plan_stage_details where learning_plan_stage_id " + "in ("
						+ sbLearnStageIds.toString() + ") ");
				pst = con.prepareStatement(sbQuery.toString());
				// System.out.println("pst======>"+pst);
				StringBuilder sbCourseId = null;
				StringBuilder sbAssessmentId = null;
				StringBuilder sbTrainingId = null;
				rs = pst.executeQuery();
				while (rs.next()) {
					if (rs.getString("learning_type") != null && rs.getString("learning_type").equals("Training")) {
						if (sbTrainingId == null) {
							sbTrainingId = new StringBuilder();
							sbTrainingId.append(rs.getString("learning_plan_stage_name_id"));
						} else {
							sbTrainingId.append("," + rs.getString("learning_plan_stage_name_id"));
						}
					} else if (rs.getString("learning_type") != null && rs.getString("learning_type").equals("Course")) {
						if (sbCourseId == null) {
							sbCourseId = new StringBuilder();
							sbCourseId.append(rs.getString("learning_plan_stage_name_id"));
						} else {
							sbCourseId.append("," + rs.getString("learning_plan_stage_name_id"));
						}
					} else if (rs.getString("learning_type") != null && rs.getString("learning_type").equals("Assessment")) {
						if (sbAssessmentId == null) {
							sbAssessmentId = new StringBuilder();
							sbAssessmentId.append(rs.getString("learning_plan_stage_name_id"));
						} else {
							sbAssessmentId.append("," + rs.getString("learning_plan_stage_name_id"));
						}
					}
				}
				rs.close();
				pst.close();

				int assessCourseTrainingCount = 0;
				if (sbTrainingId != null) {
					sbQuery = new StringBuilder();
					sbQuery.append("select count(*) as cnt from training_attend_details where training_id in (" + sbTrainingId.toString() + ") ");
					pst = con.prepareStatement(sbQuery.toString());
					// System.out.println("pst======>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						assessCourseTrainingCount = rs.getInt("cnt");
					}
					rs.close();
					pst.close();
				}

				if (sbCourseId != null) {
					sbQuery = new StringBuilder();
					sbQuery.append("select count(*) as cnt from course_read_details where course_id in (" + sbCourseId.toString() + ") ");
					pst = con.prepareStatement(sbQuery.toString());
					// System.out.println("pst======>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						assessCourseTrainingCount += rs.getInt("cnt");
					}
					rs.close();
					pst.close();
				}

				if (sbAssessmentId != null) {
					sbQuery = new StringBuilder();
					sbQuery.append("select count(*) as cnt from assessment_question_answer where assessment_details_id in (" + sbAssessmentId.toString() + ") ");
					pst = con.prepareStatement(sbQuery.toString());
					// System.out.println("pst======>"+pst);
					rs = pst.executeQuery();
					while (rs.next()) {
						assessCourseTrainingCount += rs.getInt("cnt");
					}
					rs.close();
					pst.close();
				}

				request.setAttribute("assessCourseTrainingCount", assessCourseTrainingCount + "");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getAwardedEmployee(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			List<String> recentAwardedEmpList = new ArrayList<String>();

			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image,lpd.learning_plan_id,training_id,assessment_id,certificate_status,"
					+ "thumbsup_status,lpfd.added_by,lpfd.entry_date from learning_plan_finalize_details lpfd, employee_personal_details epd, "
					+ "learning_plan_details lpd where lpfd.learning_plan_id =lpd.learning_plan_id and is_publish = true and lpfd.emp_id = epd.emp_per_id ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				strQuery.append(" and epd.emp_per_id in (select emp_id from employee_official_details where org_id in ("
						+ (String) session.getAttribute(ORG_ACCESS) + ") and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ") )");
			}
			strQuery.append("and lpfd.entry_date >=? and (certificate_status = true or thumbsup_status = true) order by emp_fname,emp_lname");
			pst = con.prepareStatement(strQuery.toString());
			// pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(1, uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), -30));
			// System.out.println("pst ====> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				StringBuilder sbNewjoineeList = new StringBuilder();

				String empimg = uF.showData(rs.getString("emp_image"), "avatar_photo.png");
				if (CF.getStrDocRetriveLocation() == null) {
					sbNewjoineeList
							.append("<span style=\"float: left; width:20px; height:20px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""
									+ DOCUMENT_LOCATION + empimg + "\" height=\"20\" width=\"20\"> </span>");
				} else {
					sbNewjoineeList
							.append("<span style=\"float: left; width:20px; height:20px;\"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""
									+ CF.getStrDocRetriveLocation()
									+ I_PEOPLE
									+ "/"
									+ I_IMAGE
									+ "/"
									+ rs.getString("emp_per_id")
									+ "/"
									+ I_22x22
									+ "/"
									+ empimg + "\" height=\"20\" width=\"20\"> </span>");
				}
				// sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #000 \"><img class=\"lazy\" src=\"userImages/avatar_photo.png\" data-original=\""+
				// CF.getStrDocRetriveLocation()
				// +empimg+"\" height=\"20\" width=\"20\"> </span>");

				sbNewjoineeList.append("<span style=\"float: left; width: 91%; margin-left: 5px;\">");
				sbNewjoineeList.append("<span style=\"float: left; width: 100%; \">");
				if (uF.parseToInt(strEmpId) != uF.parseToInt(rs.getString("emp_per_id"))) {
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					sbNewjoineeList.append(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				} else {
					sbNewjoineeList.append("You");
				}
				if (uF.parseToBoolean(rs.getString("certificate_status")) && uF.parseToBoolean(rs.getString("thumbsup_status"))) {
					sbNewjoineeList.append(" are awarded a certificate ");
					// sbNewjoineeList.append("<img src=\"images1/certificate_img.png\"/>");
					sbNewjoineeList.append("<a onclick=\"viewCertificate('" + rs.getString("emp_per_id") + "','" + rs.getString("learning_plan_id") + "')\" "
							+ "href=\"javascript:void(0)\"><img src=\"images1/certificate_img.png\"></a>");
					sbNewjoineeList.append(" and thumbs up ");
					/*
					 * sbNewjoineeList.append("<img src=\"images1/thumbs_up.png\"/>"
					 * );
					 */
					sbNewjoineeList.append("<i class=\"fa fa-thumbs-up\" aria-hidden=\"true\"></i>");

				} else if (uF.parseToBoolean(rs.getString("certificate_status"))) {
					sbNewjoineeList.append(" are awarded a certificate ");
					// sbNewjoineeList.append("<img src=\"images1/certificate_img.png\"/>");
					sbNewjoineeList.append("<a onclick=\"viewCertificate('" + rs.getString("emp_per_id") + "','" + rs.getString("learning_plan_id") + "')\" "
							+ "href=\"javascript:void(0)\"><img src=\"images1/certificate_img.png\"></a>");
				} else if (uF.parseToBoolean(rs.getString("thumbsup_status"))) {
					sbNewjoineeList.append(" are awarded a thumbs up ");
					/*
					 * sbNewjoineeList.append("<img src=\"images1/thumbs_up.png\"/>"
					 * );
					 */
					sbNewjoineeList.append("<i class=\"fa fa-thumbs-up\" aria-hidden=\"true\"></i>");
				}
				if (uF.parseToInt(rs.getString("training_id")) > 0) {
					String trainingName = CF.getTrainingNameByTrainingId(con, uF, rs.getString("training_id"));
					sbNewjoineeList.append(" for '" + trainingName + "' classroom training.");
				}
				if (uF.parseToInt(rs.getString("assessment_id")) > 0) {
					String assessmentName = CF.getAssessmentNameByAssessId(con, uF, rs.getString("assessment_id"));
					sbNewjoineeList.append(" for '" + assessmentName + "' assessment.");
				}
				sbNewjoineeList.append("</span>");
				sbNewjoineeList.append("<span style=\"float: left; width: 100%; font-size: 11px; font-style: italic;\">");
				String empName = CF.getEmpNameMapByEmpId(con, rs.getString("added_by"));
				sbNewjoineeList.append("Awarded by " + empName + " on " + uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				sbNewjoineeList.append("</span>");
				sbNewjoineeList.append("</span>");
				recentAwardedEmpList.add(sbNewjoineeList.toString());
			}
			rs.close();
			pst.close();

			request.setAttribute("recentAwardedEmpList", recentAwardedEmpList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getRecruitmentFulfilled(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(no_position) as no_position from recruitment_details where close_job_status = false ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") and wlocation in ("
						+ (String) session.getAttribute(WLOCATION_ACCESS) + ") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst Requirement======>"+pst);
			String strRequiredPosition = null;
			rs = pst.executeQuery();
			while (rs.next()) {
				strRequiredPosition = rs.getString("no_position");
			}
			rs.close();
			pst.close();

			// System.out.println("sbJoiningAndTermination =====> "+sbJoiningAndTermination.toString());
			request.setAttribute("strRequiredPosition", strRequiredPosition);

			sbQuery = new StringBuilder();
			sbQuery.append("select count(recruitment_id) as candi_cnt from candidate_application_details where candidate_joining_date is not null "
					+ "and candididate_emp_id > 0 and recruitment_id in (select recruitment_id from recruitment_details where close_job_status = false ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") and wlocation in ("
						+ (String) session.getAttribute(WLOCATION_ACCESS) + ") ");
			}
			sbQuery.append(") ");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst======>"+pst);
			String strFulfilledPosition = null;
			rs = pst.executeQuery();
			while (rs.next()) {
				strFulfilledPosition = rs.getString("candi_cnt");
			}
			rs.close();
			pst.close();

			request.setAttribute("strFulfilledPosition", strFulfilledPosition);
			// System.out.println("strFulfilledPosition =====> "+strFulfilledPosition);

			// System.out.println("sbLifecycleGaps ===> " +
			// sbLifecycleGaps.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getLifecycleGaps(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strMinMaxDates = uF.getNextORPrevMonthMinMaxDate(-5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT),
					DATE_FORMAT);
			String tempStrDates[] = strMinMaxDates.split("::::");

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select entry_date from training_gap_details where entry_date >= ? ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id " + "in ("
						+ (String) session.getAttribute(ORG_ACCESS) + ") and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ") )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(tempStrDates[0], DATE_FORMAT));
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMonthwiseLifecycleGap = new HashMap<String, String>();
			while (rs.next()) {

				if (rs.getString("entry_date") != null && !rs.getString("entry_date").equals("")) {
					String monthYear = uF.getDateFormat(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT), DATE_FORMAT, "MM/yy");
					int empCount = uF.parseToInt(hmMonthwiseLifecycleGap.get(monthYear));
					empCount++;
					hmMonthwiseLifecycleGap.put(monthYear, empCount + "");
				}
			}
			rs.close();
			pst.close();

			StringBuilder monthName = new StringBuilder();
			StringBuilder noOfEmp = new StringBuilder();
			StringBuilder sbLifecycleGaps = new StringBuilder();
			for (int i = -5; i <= 0; i++) {
				String strMinMaxDts = uF.getNextORPrevMonthMinMaxDate(i, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT),
						DATE_FORMAT);
				String tempStrDts[] = strMinMaxDts.split("::::");

				String monthYear = uF.getDateFormat(tempStrDts[0], DATE_FORMAT, "MM/yy");
				String strYear = uF.getDateFormat(tempStrDts[0], DATE_FORMAT, "yy");
				int intMonth = uF.parseToInt(uF.getDateFormat(tempStrDts[0], DATE_FORMAT, "MM"));

				String strLifecycleGap = uF.showData(hmMonthwiseLifecycleGap.get(monthYear), "0");

				monthName.append("'" + uF.getShortMonth(intMonth) + " " + strYear + "',");
				noOfEmp.append(uF.parseToInt(strLifecycleGap) + ",");

				sbLifecycleGaps.append("{'name':'" + uF.getShortMonth(intMonth) + " " + strYear + "', " + "'y': " + uF.parseToInt(strLifecycleGap) + "},");
			}

			if (monthName.length() > 1) {
				monthName.replace(0, monthName.length(), monthName.substring(0, monthName.length() - 1));
				noOfEmp.replace(0, noOfEmp.length(), noOfEmp.substring(0, noOfEmp.length() - 1));

				sbLifecycleGaps.replace(0, sbLifecycleGaps.length(), sbLifecycleGaps.substring(0, sbLifecycleGaps.length() - 1));
			}

			// System.out.println("sbJoiningAndTermination =====> "+sbJoiningAndTermination.toString());
			request.setAttribute("sbLifecycleGaps", sbLifecycleGaps.toString());

			// System.out.println("sbLifecycleGaps ===> " +
			// sbLifecycleGaps.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//Employee leave details

	private void getHiringVsTermination(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strMinMaxDates = uF.getNextORPrevMonthMinMaxDate(-5, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT),
					DATE_FORMAT);
			String tempStrDates[] = strMinMaxDates.split("::::");

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select joining_date from employee_personal_details where joining_date >= ? ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and emp_per_id in (select emp_id from employee_official_details where org_id " + "in ("
						+ (String) session.getAttribute(ORG_ACCESS) + ") and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ") )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(tempStrDates[0], DATE_FORMAT));
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMonthwiseJoining = new HashMap<String, String>();
			while (rs.next()) {

				if (rs.getString("joining_date") != null && !rs.getString("joining_date").equals("")) {
					String monthYear = uF.getDateFormat(uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT), DATE_FORMAT, "MM/yy");
					int empCount = uF.parseToInt(hmMonthwiseJoining.get(monthYear));
					empCount++;
					hmMonthwiseJoining.put(monthYear, empCount + "");
				}
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select last_day_date from emp_off_board where last_day_date>=? ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where org_id " + "in ("
						+ (String) session.getAttribute(ORG_ACCESS) + ") and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ") )");
			}
			pst = con.prepareStatement(sbQuery.toString());
			// pst =
			// con.prepareStatement("select last_day_date from emp_off_board where last_day_date>=? and emp_id in (select emp_id "
			// +
			// "from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") )");
			pst.setDate(1, uF.getDateFormat(tempStrDates[0], DATE_FORMAT));
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMonthwiseTermination = new HashMap<String, String>();
			while (rs.next()) {

				if (rs.getString("last_day_date") != null && !rs.getString("last_day_date").equals("")) {
					String monthYear = uF.getDateFormat(uF.getDateFormat(rs.getString("last_day_date"), DBDATE, DATE_FORMAT), DATE_FORMAT, "MM/yy");
					int empCount = uF.parseToInt(hmMonthwiseTermination.get(monthYear));
					empCount++;
					hmMonthwiseTermination.put(monthYear, empCount + "");
				}
			}
			rs.close();
			pst.close();

			/*
			 * StringBuilder sbJoiningAndTermination = new StringBuilder();
			 * for(int i= -5; i<=0; i++) { String strMinMaxDts =
			 * uF.getNextORPrevMonthMinMaxDate(i,
			 * uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"",
			 * DBDATE, DATE_FORMAT), DATE_FORMAT); String tempStrDts[] =
			 * strMinMaxDts.split("::::");
			 * 
			 * String monthYear = uF.getDateFormat(tempStrDts[0], DATE_FORMAT,
			 * "MM/yy"); String strYear = uF.getDateFormat(tempStrDts[0],
			 * DATE_FORMAT, "yy"); int intMonth =
			 * uF.parseToInt(uF.getDateFormat(tempStrDts[0], DATE_FORMAT,
			 * "MM"));
			 * 
			 * String strJoining =
			 * uF.showData(hmMonthwiseJoining.get(monthYear), "0"); String
			 * strTermination =
			 * uF.showData(hmMonthwiseTermination.get(monthYear), "0");
			 * 
			 * sbJoiningAndTermination.append("{'month':'"+uF.getShortMonth(intMonth
			 * )+" "+strYear+"', " +
			 * "'joining': "+uF.parseToDouble(strJoining)+"," +
			 * "'termination': "+uF.parseToDouble(strTermination)+"},"); }
			 */

			StringBuilder sbYearMonths = null;

			// StringBuilder chartCategories = new StringBuilder();
			StringBuilder sbHires = null;
			StringBuilder sbTerminations = null;

			for (int i = -5; i <= 0; i++) {
				String strMinMaxDts = uF.getNextORPrevMonthMinMaxDate(i, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT),
						DATE_FORMAT);
//				System.out.print("strMinMaxDts:"+strMinMaxDts);
				String tempStrDts[] = strMinMaxDts.split("::::");
//				System.out.print("tempStrDts:"+tempStrDts);
				
				//System.out.print("strMinMaxDts:"+System.out.print("strMinMaxDts:"+strMinMaxDts););
				String monthYear = uF.getDateFormat(tempStrDts[0], DATE_FORMAT, "MM/yy");
				String strYear = uF.getDateFormat(tempStrDts[0], DATE_FORMAT, "yy");
				int intMonth = uF.parseToInt(uF.getDateFormat(tempStrDts[0], DATE_FORMAT, "MM"));

				String strJoining = uF.showData(hmMonthwiseJoining.get(monthYear), "0");
				String strTermination = uF.showData(hmMonthwiseTermination.get(monthYear), "0");

				if (sbYearMonths == null) {
					sbYearMonths = new StringBuilder();
					sbYearMonths.append("['" + uF.getShortMonth(intMonth) + " " + strYear + "'");
				}
				sbYearMonths.append(", '" + uF.getShortMonth(intMonth) + " " + strYear + "'");
				if (i == 0) {
					sbYearMonths.append("]");
				}

				if (sbHires == null) {
					sbHires = new StringBuilder();
					sbHires.append("[" + uF.parseToDouble(strJoining) + "");
				}
				sbHires.append(", " + uF.parseToDouble(strJoining) + "");
				if (i == 0) {
					sbHires.append("]");
				}

				if (sbTerminations == null) {
					sbTerminations = new StringBuilder();
					sbTerminations.append("[" + uF.parseToDouble(strTermination) + "");
				}
				sbTerminations.append(", " + uF.parseToDouble(strTermination) + "");
				if (i == 0) {
					sbTerminations.append("]");
				}
				// chartCategories.append("'"+uF.getShortMonth(intMonth)+" "+strYear+"'");
			}

			// if(sbYearMonths.length()>1) {
			// sbYearMonths.replace(0, sbYearMonths.length(),
			// sbYearMonths.substring(0, sbYearMonths.length()-1));
			// }
			// System.out.println("sbJoiningAndTermination =====> "+sbJoiningAndTermination.toString());
			//System.out.println("sbYearMonths"+sbYearMonths.toString());
			//System.out.println("sbHires"+sbHires.toString());
//			System.out.println("sbTerminations"+sbTerminations.toString());
			
			request.setAttribute("sbYearMonths", sbYearMonths.toString());
			request.setAttribute("sbHires", sbHires.toString());
			request.setAttribute("sbTerminations", sbTerminations.toString());

			// System.out.println("tremainPercent ===> " + remainPercent);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getKRAAchievedStatus(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String goalTyp = INDIVIDUAL_GOAL + "," + INDIVIDUAL_KRA + "," + EMPLOYEE_KRA;
			// select sum(gksrd.complete_percent), count(*) as kra_count,
			// a.goal_id, a.goal_freq_id from (select gd.goal_id,
			// gdf.goal_freq_id from goal_details gd, goal_details_frequency gdf
			// where gd.goal_id = gdf.goal_id and gd.org_id in (1,2) and
			// gd.goal_type in (4,7,8) and gd.measure_kra='KRA' ) as a left join
			// goal_kra_status_rating_details gksrd on gksrd.goal_freq_id =
			// a.goal_freq_id group by a.goal_id, a.goal_freq_id, gksrd.kra_id
			// order by a.goal_id
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(gksrd.complete_percent) as complete_percent, count(*) as kra_count from goal_details gd, goal_kra_status_rating_details gksrd "
					+ " where gd.goal_type in (" + goalTyp + ") ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and gd.org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") ");
			}
			sbQuery.append(" and gd.measure_kra='KRA' and gksrd.goal_id = gd.goal_id group by gksrd.goal_id, gksrd.goal_freq_id, gksrd.kra_id ");
			pst = con.prepareStatement(sbQuery.toString());
			// pst.setInt(1, INDIVIDUAL_GOAL);
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			int slowCount = 0;
			int steadyCount = 0;
			int momentumCount = 0;
			while (rs.next()) {
				double dblCompletePer = rs.getDouble("complete_percent") / rs.getInt("kra_count");
				// System.out.println("dblCompletePer ===>> " + dblCompletePer);

				if (dblCompletePer < 33.33) {
					slowCount++;
				} else if (dblCompletePer >= 33.33 && dblCompletePer < 66.67) {
					steadyCount++;
				} else if (dblCompletePer >= 66.67) {
					momentumCount++;
				}
			}
			rs.close();
			pst.close();

			StringBuilder sbSlowSteadyMomentumPie = new StringBuilder();
			sbSlowSteadyMomentumPie.append("{'Status':'Slow', 'cnt': " + slowCount + "},");
			sbSlowSteadyMomentumPie.append("{'Status':'Steady', 'cnt': " + steadyCount + "},");
			sbSlowSteadyMomentumPie.append("{'Status':'Momentum', 'cnt': " + momentumCount + "},");
			if (sbSlowSteadyMomentumPie.length() > 1) {
				sbSlowSteadyMomentumPie
						.replace(0, sbSlowSteadyMomentumPie.length(), sbSlowSteadyMomentumPie.substring(0, sbSlowSteadyMomentumPie.length() - 1));
			}
			request.setAttribute("sbSlowSteadyMomentumPie", sbSlowSteadyMomentumPie.toString());

			// System.out.println("tremainPercent ===> " + remainPercent);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getTargetAchievedAndMissed(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String goalTyp = INDIVIDUAL_GOAL + "," + INDIVIDUAL_TARGET + "," + PERSONAL_GOAL;
			// pst=con.prepareStatement("select (percentage/target_count) as average from (select count(*) as target_count,sum(amt_percentage) as percentage "
			// +
			// " from (select max(target_id) as target_id from (select goal_id from goal_details where emp_ids like '%,"+strEmpId+",%' and "
			// +
			// " goal_type in ("+goalTyp+") and measure_kra='Measure') as a, target_details td where a.goal_id = td.goal_id group by a.goal_id) as a,"
			// +
			// " target_details td where a.target_id=td.target_id) as b");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select (percentage/target_count) as average from (select count(*) as target_count,sum(amt_percentage) as percentage "
					+ " from (select max(target_id) as target_id from (select goal_id from goal_details where goal_type in (" + goalTyp + ") ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") ");
			}
			sbQuery.append(" and measure_kra='Measure') as a, target_details td where a.goal_id = td.goal_id group by a.goal_id) as a,"
					+ " target_details td where a.target_id=td.target_id) as b");
			pst = con.prepareStatement(sbQuery.toString());
			// pst.setInt(1, INDIVIDUAL_GOAL);
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			double achievedAmt = 0.0d;
			while (rs.next()) {
				achievedAmt = rs.getDouble("average");
			}
			rs.close();
			pst.close();

			// pst=con.prepareStatement("select (percentage/goal_count) as average, goal_count from (select count(*) as goal_count, sum(measure_currency_value) as percentage "
			// +
			// "from goal_details where emp_ids like '%,"+strEmpId+",%' and goal_type = ? and measure_kra='Measure') as b");
			// pst.setInt(1, INDIVIDUAL_GOAL);
			sbQuery = new StringBuilder();
			sbQuery.append("select (percentage/goal_count) as average, goal_count from (select count(*) as goal_count, sum(measure_currency_value) as percentage "
					+ "from goal_details where goal_type in (" + goalTyp + ") ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") ");
			}
			sbQuery.append(" and measure_kra='Measure') as b");
			pst = con.prepareStatement(sbQuery.toString());
			// System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			double targetedAmt = 0.0d;
			int myTargetsCnt = 0;
			while (rs.next()) {
				targetedAmt = rs.getDouble("average");
				myTargetsCnt = rs.getInt("goal_count");
			}
			rs.close();
			pst.close();

			double achievedPercent = 0.0d;
			double remainAmt = 0.0d;
			if (targetedAmt > 0d) {
				achievedPercent = (achievedAmt * 100) / targetedAmt;
				remainAmt = targetedAmt - achievedAmt;
			}

			StringBuilder sbTargetMissedAchieved = new StringBuilder();
			sbTargetMissedAchieved.append("{'target':'Target Achieved', 'targetAmt': " + uF.parseToDouble(uF.formatIntoOneDecimalWithOutComma(achievedAmt))
					+ "},");
			sbTargetMissedAchieved.append("{'target':'Target Missed', 'targetAmt': " + uF.parseToDouble(uF.formatIntoOneDecimalWithOutComma(remainAmt)) + "},");

			// System.out.println("tachievedPercent ===> " + achievedPercent);

			String twoDeciTotProgressAvg = "0";
			String twoDeciTot = "0";
			String total = "100";
			double totalTarget = achievedPercent;

			if (totalTarget > new Double(100) && totalTarget <= new Double(150)) {
				double totalTarget1 = (totalTarget / 150) * 100;
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget1);
				total = "150";
			} else if (totalTarget > new Double(150) && totalTarget <= new Double(200)) {
				double totalTarget1 = (totalTarget / 200) * 100;
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget1);
				total = "200";
			} else if (totalTarget > new Double(200) && totalTarget <= new Double(250)) {
				double totalTarget1 = (totalTarget / 250) * 100;
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget1);
				total = "250";
			} else if (totalTarget > new Double(250) && totalTarget <= new Double(300)) {
				double totalTarget1 = (totalTarget / 300) * 100;
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget1);
				total = "300";
			} else if (totalTarget > new Double(300) && totalTarget <= new Double(350)) {
				double totalTarget1 = (totalTarget / 350) * 100;
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget1);
				total = "350";
			} else if (totalTarget > new Double(350) && totalTarget <= new Double(400)) {
				double totalTarget1 = (totalTarget / 400) * 100;
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget1);
				total = "400";
			} else if (totalTarget > new Double(400) && totalTarget <= new Double(450)) {
				double totalTarget1 = (totalTarget / 450) * 100;
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget1);
				total = "450";
			} else if (totalTarget > new Double(450) && totalTarget <= new Double(500)) {
				double totalTarget1 = (totalTarget / 500) * 100;
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget1);
				total = "500";
			} else {
				twoDeciTotProgressAvg = uF.formatIntoTwoDecimal(totalTarget);
				if (uF.parseToDouble(twoDeciTotProgressAvg) > 100) {
					twoDeciTotProgressAvg = "100";
					total = "" + Math.round(totalTarget);
				} else {
					total = "100";
				}
			}

			// double remainPercent = 100 -
			// Math.round(uF.parseToDouble(twoDeciTotProgressAvg));
			// StringBuilder sbTargetMissedAchieved = new StringBuilder();
			// sbTargetMissedAchieved.append("{'target':'Target Achieved', 'targetAmt': "+Math.round(uF.parseToDouble(twoDeciTotProgressAvg))+"},");
			// sbTargetMissedAchieved.append("{'target':'Target Missed', 'targetAmt': "+remainPercent+"},");

			// System.out.println("tremainPercent ===> " + remainPercent
			// +" -- Achieved ===>> " +
			// Math.round(uF.parseToDouble(twoDeciTotProgressAvg)));
			request.setAttribute("myTargetsCnt", "" + myTargetsCnt);
			request.setAttribute("sbTargetMissedAchieved", "" + sbTargetMissedAchieved.toString());
			request.setAttribute("targetedAmt", uF.formatIntoOneDecimalWithOutComma(targetedAmt));
			// request.setAttribute("myTargetachievedPercent",
			// ""+Math.round(uF.parseToDouble(twoDeciTotProgressAvg)));
			// request.setAttribute("myTargetremainPercent", ""+remainPercent);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void getEmployeeLeaveDetails(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		String totalEmpCount = null;
//		List<Double> listLeaveCount = new ArrayList<Double>();
//		List<Double> listPresentCount = new ArrayList<Double>();
//		StringBuilder sbLeaveDate = null;
		List<String> alDates = new ArrayList<String>();
		List<String> alLeaveDates = new ArrayList<String>();
		
		StringBuilder sbLeaveDateMonth = null;
		StringBuilder sbLeaveCount = null;
		StringBuilder sbTravelCount = null;
		StringBuilder sbPresentCount = null;
		StringBuilder sbExtaWorkingCount = null;
		StringBuilder sbAbsentCount = new StringBuilder();
		//List<Double> listLeave = new ArrayList<double>();
		
		try {
			pst = con.prepareStatement("select count(*) as totalEmpCount from employee_personal_details epd left join employee_official_details eod on eod.emp_id = epd.emp_per_id where approved_flag= true and is_alive = true and emp_filled_flag = true and emp_id > 0 ");
			rst = pst.executeQuery();
			while(rst.next()) {
				totalEmpCount = rst.getString("totalEmpCount");
			}
//			System.out.print("strTotalCount:"+totalEmpCount);
			rst.close();
			pst.close();
			
//			System.out.print("alDates:"+alDates);
			for(int i=0; i<7; i++) {
				alDates.add(uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), i)+"", DBDATE, DATE_FORMAT) );
			}	
				
			for(int i= 0;i < alDates.size();i++) {
				String strLeaveDate = alDates.get(i);
				//System.out.print("strLeaveDate::"+strLeaveDate);
				//System.out.print("strLeaveDate::"+uF.getDateFormat(strLeaveDate, DATE_FORMAT, "dd MMM"));
				if (sbLeaveDateMonth == null) {
					sbLeaveDateMonth = new StringBuilder();
					sbLeaveDateMonth.append( "'"+uF.getDateFormat(strLeaveDate, DATE_FORMAT, "dd MMM")+"'");
				} else {
					//System.out.print("sbLeaveDateMonth22::"+sbLeaveDateMonth);
					sbLeaveDateMonth.append(",");
					sbLeaveDateMonth.append(" '" + uF.getDateFormat(strLeaveDate, DATE_FORMAT, "dd MMM") + "'");
				}
			}
		
			pst = con.prepareStatement("select _date from leave_application_register where _date > current_date - interval '7 days'");
			rst = pst.executeQuery();
			while(rst.next()){
				alLeaveDates.add(rst.getString("_date"));
			}
			//System.out.println("alLeaveDates:"+alLeaveDates);
			pst.close();
			rst.close();
			
			for(int i = 0 ;i < alLeaveDates.size();i++) {
				double leaveTotal = 0.0;   
				pst = con.prepareStatement(" select leave_application_register.leave_no as Leavecount from leave_application_register,emp_leave_entry where leave_application_register.leave_id = emp_leave_entry.leave_id AND  leave_application_register._date = '"+alLeaveDates.get(i)+"' AND emp_leave_entry.is_work_from_home = false AND emp_leave_entry.istravel = false AND is_compensate = false ");
				//System.out.print("Leave count pst==>"+pst);
				rst = pst.executeQuery();
				while(rst.next()) {
					String strCount = rst.getString("Leavecount");
					if(strCount==null)
						strCount ="0.0";
					leaveTotal = leaveTotal + uF.parseToDouble(strCount);
					if(sbLeaveCount ==null) {
						sbLeaveCount = new StringBuilder();
						sbLeaveCount.append("["+leaveTotal);
					} else {
						sbLeaveCount.append(",");
						sbLeaveCount.append( +leaveTotal);
					}
				//	System.out.print("leaveTotal:"+leaveTotal);
					//listLeaveCount.add(leaveTotal);
					//System.out.print("listLeaveCount"+listLeaveCount);
				}
				pst.close();
				rst.close();
				
				pst = con.prepareStatement(" select count(*) as Travelcount from leave_application_register,emp_leave_entry where leave_application_register.leave_id = emp_leave_entry.leave_id AND  leave_application_register._date = '"+alLeaveDates.get(i)+"' AND emp_leave_entry.is_work_from_home = false AND emp_leave_entry.istravel = true AND is_compensate = false  ");
				//System.out.print("Travel count pst==>"+pst);
				rst = pst.executeQuery();
				while(rst.next()) {
					String Travelcount = rst.getString("Travelcount");
				//	System.out.print("strCount:"+Travelcount);
					if(Travelcount==null)
						Travelcount ="0.0";
					if(sbTravelCount ==null) {
						sbTravelCount = new StringBuilder();
						sbTravelCount.append("["+uF.parseToDouble(Travelcount));
					} else {
					//	System.out.print("Travelcount::"+Travelcount);
						sbTravelCount.append(",");
						sbTravelCount.append( + uF.parseToDouble(Travelcount));
					}
				}
				rst.close();
				pst.close();
				
				pst = con.prepareStatement("select count(*) as presentCount from attendance_details where in_out_timestamp_actual between '"+alLeaveDates.get(i)+" 00:00:00' AND '"+alLeaveDates.get(i)+" 24:00:00' ");
				//System.out.print("Present count pst==>"+pst);
				rst = pst.executeQuery();
				while(rst.next()) {
					String strPresentCount = rst.getString("presentCount");
					//System.out.print("presentCount:"+strPresentCount);
					if(strPresentCount==null)
						strPresentCount ="0.0";
					if(sbPresentCount ==null) {
						sbPresentCount = new StringBuilder();
						sbPresentCount.append("["+uF.parseToDouble(strPresentCount));
						double absentCount = uF.parseToDouble(totalEmpCount) - uF.parseToDouble(strPresentCount);
						sbAbsentCount.append("[" +absentCount);
					} else {
						sbPresentCount.append(",");
						sbAbsentCount.append(",");
						sbPresentCount.append( + uF.parseToDouble(strPresentCount));
						double absentCount = uF.parseToDouble(totalEmpCount) - uF.parseToDouble(strPresentCount);
						sbAbsentCount.append( +absentCount);
						//sbAbsentCount.append( +uF.parseToDouble(totalEmpCount - uF.parseToDouble(strPresentCount)));
					}
				}
				rst.close();
				pst.close();
				
				pst = con.prepareStatement("select count(*) as extaWorkingcount from leave_application_register,emp_leave_entry where leave_application_register.leave_id = emp_leave_entry.leave_id AND  leave_application_register._date = '"+alLeaveDates.get(i)+"' AND emp_leave_entry.is_work_from_home = false AND emp_leave_entry.istravel = false AND is_compensate = true ");
				//System.out.print("ExtaWorking count pst==>"+pst);
				rst = pst.executeQuery();
				while(rst.next()) {
					String strExtaWorkingCount = rst.getString("extaWorkingcount");
					//System.out.print("ExtaWorkingCount:"+strExtaWorkingCount);
					if(strExtaWorkingCount==null )
						strExtaWorkingCount = "0.0";
					if(sbExtaWorkingCount ==null) {
						sbExtaWorkingCount = new StringBuilder();
						sbExtaWorkingCount.append("["+uF.parseToDouble(strExtaWorkingCount));
	
					} else {
					//System.out.print("strExtaWorkingCount::"+strExtaWorkingCount);
					sbExtaWorkingCount.append(",");
					sbExtaWorkingCount.append( + uF.parseToDouble(strExtaWorkingCount));
					//System.out.print("End Else");
					}
					//System.out.print("end while11");
				}
				//System.out.print("end while");
				rst.close();
				pst.close();
			}
			if(sbLeaveCount ==null) {
				sbLeaveCount = new StringBuilder();
			} else {
				sbLeaveCount.append("]");
			}
			if(sbTravelCount ==null) {
				sbTravelCount = new StringBuilder();
			} else {
				sbTravelCount.append("]");
			}
			if(sbPresentCount ==null) {
				sbPresentCount = new StringBuilder();
			} else {
				sbPresentCount.append("]");
			}
			if(sbExtaWorkingCount ==null) {
				sbExtaWorkingCount = new StringBuilder();
			} else {
				sbExtaWorkingCount.append("]");
			}
			
			sbAbsentCount.append("]");
			
			
			//System.out.println("sbLeaveDate::"+sbLeaveDateMonth);
			request.setAttribute("sbLeaveDateMonth", sbLeaveDateMonth);
			
			//System.out.println("strCount::"+sbLeaveCount);
			request.setAttribute("sbLeaveCount", sbLeaveCount.toString());
			
			//System.out.println("Travelcount::"+sbTravelCount);
			request.setAttribute("sbTravelCount", sbTravelCount.toString());
			
			//System.out.println("sbPresentCount::"+sbPresentCount);
			request.setAttribute("sbPresentCount", sbPresentCount.toString());
			
			//System.out.println("sbExtaWorkingCount::"+sbExtaWorkingCount);
			request.setAttribute("sbExtaWorkingCount", sbExtaWorkingCount.toString());
			
			//System.out.println("sbAbsentCount::"+sbAbsentCount);
			request.setAttribute("sbAbsentCount", sbAbsentCount.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
//		System.out.print("End Function");
	}
	
	public void getKRARatingAndCompletionStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmGoalAndTargetRating = new HashMap<String, String>();
			pst = con
					.prepareStatement("select gksrd.*, gd.goal_id from goal_kra_status_rating_details gksrd, goal_details gd where gksrd.goal_id = gd.goal_id");
			rs = pst.executeQuery();
			while (rs.next()) {

				if ((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals(""))
						|| (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals(""))) {

					double strGoalTargetRating = uF.parseToDouble(hmGoalAndTargetRating.get(rs.getString("goal_id") + "_RATING"));
					int strGoalTargetCount = uF.parseToInt(hmGoalAndTargetRating.get(rs.getString("goal_id") + "_COUNT"));
					strGoalTargetCount++;
					double strGoalTargetCurrRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if (rs.getString("manager_rating") == null) {
						strGoalTargetCurrRating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if (rs.getString("hr_rating") == null) {
						strGoalTargetCurrRating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					strGoalTargetRating += strGoalTargetCurrRating;
					hmGoalAndTargetRating.put(rs.getString("goal_id") + "_RATING", strGoalTargetRating + "");
					hmGoalAndTargetRating.put(rs.getString("goal_id") + "_COUNT", strGoalTargetCount + "");
				}
			}
			rs.close();
			pst.close();
			// System.out.println("hmTargetRatingAndComment ===>> " +
			// hmTargetRatingAndComment);
			request.setAttribute("hmGoalAndTargetRating", hmGoalAndTargetRating);

			Map<String, String> hmKRATaskRating = new HashMap<String, String>();
			Map<String, String> hmKRARating = new HashMap<String, String>();
			Map<String, String> hmGoalRating = new HashMap<String, String>();

			Map<String, String> hmKRATaskStatus = new HashMap<String, String>();
			Map<String, String> hmKRAStatus = new HashMap<String, String>();
			Map<String, String> hmGoalStatus = new HashMap<String, String>();

			pst = con
					.prepareStatement("select gksrd.*, gk.goal_id from goal_kra_status_rating_details gksrd, goal_kras gk where gksrd.kra_id = gk.goal_kra_id ");
			rs = pst.executeQuery();
			while (rs.next()) {

				if ((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals(""))
						|| (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals(""))) {
					double strKRATaskRating = uF.parseToDouble(hmKRATaskRating.get(rs.getString("kra_task_id") + "_RATING"));
					int strKRATaskCount = uF.parseToInt(hmKRATaskRating.get(rs.getString("kra_task_id") + "_COUNT"));
					strKRATaskCount++;
					double strCurrKRATaskRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if (rs.getString("manager_rating") == null) {
						strCurrKRATaskRating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if (rs.getString("hr_rating") == null) {
						strCurrKRATaskRating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					strKRATaskRating += strCurrKRATaskRating;
					hmKRATaskRating.put(rs.getString("kra_task_id") + "_RATING", strKRATaskRating + "");
					hmKRATaskRating.put(rs.getString("kra_task_id") + "_COUNT", strKRATaskCount + "");

					double strKRARating = uF.parseToDouble(hmKRARating.get(rs.getString("kra_id") + "_RATING"));
					int strKRACount = uF.parseToInt(hmKRARating.get(rs.getString("kra_id") + "_COUNT"));
					strKRACount++;
					double strCurrKRARating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if (rs.getString("manager_rating") == null) {
						strCurrKRARating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if (rs.getString("hr_rating") == null) {
						strCurrKRARating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					strKRARating += strCurrKRARating;
					hmKRARating.put(rs.getString("kra_id") + "_RATING", strKRARating + "");
					hmKRARating.put(rs.getString("kra_id") + "_COUNT", strKRACount + "");

					double strGoalRating = uF.parseToDouble(hmGoalRating.get(rs.getString("goal_id") + "_RATING"));
					int strGoalCount = uF.parseToInt(hmGoalRating.get(rs.getString("goal_id") + "_COUNT"));
					strGoalCount++;
					double strGoalCurrRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
					if (rs.getString("manager_rating") == null) {
						strGoalCurrRating = uF.parseToDouble(rs.getString("hr_rating"));
					} else if (rs.getString("hr_rating") == null) {
						strGoalCurrRating = uF.parseToDouble(rs.getString("manager_rating"));
					}
					strGoalRating += strGoalCurrRating;
					hmGoalRating.put(rs.getString("goal_id") + "_RATING", strGoalRating + "");
					hmGoalRating.put(rs.getString("goal_id") + "_COUNT", strGoalCount + "");
				}

				double strKRATaskStatus = uF.parseToDouble(hmKRATaskStatus.get(rs.getString("kra_task_id") + "_STATUS"));
				int strKRATaskStatusCount = uF.parseToInt(hmKRATaskStatus.get(rs.getString("kra_task_id") + "_COUNT"));
				strKRATaskStatusCount++;
				double strCurrKRATaskStatus = uF.parseToDouble(rs.getString("complete_percent"));
				strKRATaskStatus += strCurrKRATaskStatus;
				hmKRATaskStatus.put(rs.getString("kra_task_id") + "_STATUS", strKRATaskStatus + "");
				hmKRATaskStatus.put(rs.getString("kra_task_id") + "_COUNT", strKRATaskStatusCount + "");

				double strKRAStatus = uF.parseToDouble(hmKRAStatus.get(rs.getString("kra_id") + "_STATUS"));
				int strKRAStatusCount = uF.parseToInt(hmKRAStatus.get(rs.getString("kra_id") + "_COUNT"));
				strKRAStatusCount++;
				double strCurrKRAStatus = uF.parseToDouble(rs.getString("complete_percent"));
				strKRAStatus += strCurrKRAStatus;
				hmKRAStatus.put(rs.getString("kra_id") + "_STATUS", strKRAStatus + "");
				hmKRAStatus.put(rs.getString("kra_id") + "_COUNT", strKRAStatusCount + "");

				double strGoalStatus = uF.parseToDouble(hmGoalStatus.get(rs.getString("goal_id") + "_STATUS"));
				int strGoalStatusCount = uF.parseToInt(hmGoalStatus.get(rs.getString("goal_id") + "_COUNT"));
				strGoalStatusCount++;
				double strGoalCurrStatus = uF.parseToDouble(rs.getString("complete_percent"));
				strGoalStatus += strGoalCurrStatus;
				hmGoalStatus.put(rs.getString("goal_id") + "_STATUS", strGoalStatus + "");
				hmGoalStatus.put(rs.getString("goal_id") + "_COUNT", strGoalStatusCount + "");
			}
			rs.close();
			pst.close();

			request.setAttribute("hmKRATaskStatus", hmKRATaskStatus);
			request.setAttribute("hmKRAStatus", hmKRAStatus);
			request.setAttribute("hmGoalStatus", hmGoalStatus);

			request.setAttribute("hmGoalRating", hmGoalRating);
			request.setAttribute("hmKRARating", hmKRARating);
			// System.out.println("hmKRATaskRating ====>>> " + hmKRATaskRating);

			request.setAttribute("hmKRATaskRating", hmKRATaskRating);

			List<String> alCorpGoalId = new ArrayList<String>();
			List<String> alMngrGoalId = new ArrayList<String>();
			List<String> alTeamGoalId = new ArrayList<String>();
			Map<String, List<String>> hmGoalIds = new HashMap<String, List<String>>();
			List<String> alGoalId = new ArrayList<String>();
			String goalType = CORPORATE_GOAL + "," + MANAGER_GOAL + "," + TEAM_GOAL;
			pst = con.prepareStatement("select goal_id,goal_type,goal_parent_id from goal_details "); // where
																										// goal_type
																										// in
																										// ("+goalType+")
			// System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getInt("goal_parent_id") > 0) {
					alGoalId = hmGoalIds.get(rs.getString("goal_parent_id"));
					if (alGoalId == null)
						alGoalId = new ArrayList<String>();

					alGoalId.add(rs.getString("goal_id"));
					hmGoalIds.put(rs.getString("goal_parent_id"), alGoalId);
				}

				if (rs.getInt("goal_type") == CORPORATE_GOAL) {
					alCorpGoalId.add(rs.getString("goal_id"));
				} else if (rs.getInt("goal_type") == MANAGER_GOAL) {
					alMngrGoalId.add(rs.getString("goal_id"));
				} else if (rs.getInt("goal_type") == TEAM_GOAL) {
					alTeamGoalId.add(rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();

			// System.out.println("alTeamGoalId ===>> " + alTeamGoalId);
			// System.out.println("hmGoalIds ===>> " + hmGoalIds);

			Map<String, String> hmCorpGoalRating = new HashMap<String, String>();
			Map<String, String> hmMngrGoalRating = new HashMap<String, String>();
			Map<String, String> hmTeamGoalRating = new HashMap<String, String>();

			for (int b = 0; alCorpGoalId != null && b < alCorpGoalId.size(); b++) {
				List<String> mngrGoalList = hmGoalIds.get(alCorpGoalId.get(b));
				double strCorpGoalRate = 0;
				int strCorpGoalCnt = 0;
				for (int a = 0; mngrGoalList != null && a < mngrGoalList.size(); a++) {
					List<String> teamGoalList = hmGoalIds.get(mngrGoalList.get(a));
					double strMngrGoalRate = 0;
					int strMngrGoalCnt = 0;
					for (int i = 0; teamGoalList != null && i < teamGoalList.size(); i++) {
						List<String> goalList = hmGoalIds.get(teamGoalList.get(i));
						double strTeamGoalRate = 0;
						int strTeamGoalCnt = 0;
						for (int j = 0; goalList != null && j < goalList.size(); j++) {
							double strGTRating = uF.parseToDouble(hmGoalAndTargetRating.get(goalList.get(j) + "_RATING"));
							int strGTCount = uF.parseToInt(hmGoalAndTargetRating.get(goalList.get(j) + "_COUNT"));
							if (strGTCount > 0) {
								strCorpGoalRate += strGTRating;
								strCorpGoalCnt += strGTCount;

								strMngrGoalRate += strGTRating;
								strMngrGoalCnt += strGTCount;

								strTeamGoalRate += strGTRating;
								strTeamGoalCnt += strGTCount;

								hmTeamGoalRating.put(teamGoalList.get(i) + "_RATING", strTeamGoalRate + "");
								hmTeamGoalRating.put(teamGoalList.get(i) + "_COUNT", strTeamGoalCnt + "");

								hmMngrGoalRating.put(mngrGoalList.get(a) + "_RATING", strMngrGoalRate + "");
								hmMngrGoalRating.put(mngrGoalList.get(a) + "_COUNT", strMngrGoalCnt + "");

								hmCorpGoalRating.put(alCorpGoalId.get(b) + "_RATING", strCorpGoalRate + "");
								hmCorpGoalRating.put(alCorpGoalId.get(b) + "_COUNT", strCorpGoalCnt + "");
							}

							double strGRating = uF.parseToDouble(hmGoalRating.get(goalList.get(j) + "_RATING"));
							int strGCount = uF.parseToInt(hmGoalRating.get(goalList.get(j) + "_COUNT"));
							if (strGCount > 0) {
								strCorpGoalRate += strGRating;
								strCorpGoalCnt += strGCount;

								strMngrGoalRate += strGRating;
								strMngrGoalCnt += strGCount;

								strTeamGoalRate += strGRating;
								strTeamGoalCnt += strGCount;

								hmTeamGoalRating.put(teamGoalList.get(i) + "_RATING", strTeamGoalRate + "");
								hmTeamGoalRating.put(teamGoalList.get(i) + "_COUNT", strTeamGoalCnt + "");

								hmMngrGoalRating.put(mngrGoalList.get(a) + "_RATING", strMngrGoalRate + "");
								hmMngrGoalRating.put(mngrGoalList.get(a) + "_COUNT", strMngrGoalCnt + "");

								hmCorpGoalRating.put(alCorpGoalId.get(b) + "_RATING", strCorpGoalRate + "");
								hmCorpGoalRating.put(alCorpGoalId.get(b) + "_COUNT", strCorpGoalCnt + "");
							}
						}
					}
				}
			}

			request.setAttribute("hmTeamGoalRating", hmTeamGoalRating);
			request.setAttribute("hmMngrGoalRating", hmMngrGoalRating);
			request.setAttribute("hmCorpGoalRating", hmCorpGoalRating);

			// System.out.println("hmTeamGoalRating ====>>> " +
			// hmTeamGoalRating);
			// System.out.println("hmMngrGoalRating ====>>> " +
			// hmMngrGoalRating);
			// System.out.println("hmCorpGoalRating ====>>> " +
			// hmCorpGoalRating);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void getCorporateDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, String>> hmManagerGoalCalDetailsCorporate = new HashMap<String, Map<String, String>>();
		try {

			GoalSummary gSummary = new GoalSummary();
			gSummary.session = session;
			gSummary.CF = CF;
			gSummary.request = request;

			con = db.makeConnection(con);

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, List<String>> hmCorporate = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmAttribute = CF.getAttributeMap(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from goal_details where goal_type=" + CORPORATE_GOAL + "  and is_close = false ");
			if (strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER))) {
				sbQuery.append(" and org_id in (" + (String) session.getAttribute(ORG_ACCESS) + ") ");
			}
			sbQuery.append(" order by goal_id desc");

			pst = con.prepareStatement(sbQuery.toString());
			// pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			// System.out.println("pst ===> "+pst);
			int corpGoalCount = 0;
			while (rs.next()) {
				corpGoalCount++;
				List<String> cinnerList = new ArrayList<String>();
				cinnerList.add(rs.getString("goal_id"));
				cinnerList.add(rs.getString("goal_type"));
				cinnerList.add(rs.getString("goal_parent_id"));
				cinnerList.add(rs.getString("goal_title"));
				cinnerList.add(rs.getString("goal_objective"));
				cinnerList.add(rs.getString("goal_description"));
				cinnerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")), ""));
				cinnerList.add(rs.getString("measure_type"));
				cinnerList.add(rs.getString("measure_currency_value"));
				cinnerList.add(rs.getString("measure_currency_id"));
				cinnerList.add(rs.getString("measure_effort_days"));
				cinnerList.add(rs.getString("measure_effort_hrs"));
				cinnerList.add(rs.getString("measure_type1"));
				cinnerList.add(rs.getString("measure_kra"));
				cinnerList.add(rs.getString("measure_currency_value1"));
				cinnerList.add(rs.getString("measure_currency1_id"));
				cinnerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()));
				cinnerList.add(rs.getString("is_feedback"));
				cinnerList.add(rs.getString("orientation_id"));
				cinnerList.add(rs.getString("weightage"));
				cinnerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				cinnerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				cinnerList.add(rs.getString("user_id"));
				cinnerList.add(rs.getString("is_measure_kra"));
				cinnerList.add(rs.getString("measure_kra_days"));
				cinnerList.add(rs.getString("measure_kra_hrs"));
				cinnerList.add(rs.getString("grade_id"));
				cinnerList.add(rs.getString("level_id"));
				cinnerList.add(rs.getString("kra"));

				cinnerList.add(rs.getString("emp_ids"));
				String priority = "";
				String pClass = "";
				if (rs.getString("priority") != null && !rs.getString("priority").equals("")) {
					if (rs.getString("priority").equals("1")) {
						pClass = "high";
						priority = "High";
					} else if (rs.getString("priority").equals("2")) {
						pClass = "medium";
						priority = "Medium";
					} else if (rs.getString("priority").equals("3")) {
						pClass = "low";
						priority = "Low";
					}
				}

				cinnerList.add(priority); // 30
				cinnerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat())); // 31
				cinnerList.add(pClass); // 32
				cinnerList.add(rs.getString("is_close")); // 33
				cinnerList.add(hmEmpName.get(rs.getString("user_id"))); // 34

				hmCorporate.put(rs.getString("goal_id"), cinnerList);
				Map<String, String> hmManagerGoalCalDetailsParentCorporate = new HashMap<String, String>();
				gSummary.getManagerGoalData(con, uF, rs.getString("goal_id"), hmManagerGoalCalDetailsParentCorporate);

				hmManagerGoalCalDetailsCorporate.put(rs.getString("goal_id"), hmManagerGoalCalDetailsParentCorporate);
			}
			rs.close();
			pst.close();

			// System.out.println("hmManagerGoalCalDetailsCorporate ===>> " +
			// hmManagerGoalCalDetailsCorporate);

			Map<String, String> hmCorpGoalRating = (Map<String, String>) request.getAttribute("hmCorpGoalRating");
			if (hmCorpGoalRating == null)
				hmCorpGoalRating = new HashMap<String, String>();

			Iterator<String> it = hmCorporate.keySet().iterator();

			double alltwoDeciTotProgressAvgCorporate = 0.0d;
			double alltotal100Corporate = 0.0d;
			double strtwoDeciTotCorporate = 0.0d;
			double avgCorpGoalRating = 0.0d;
			int count = 0;
			while (it.hasNext()) {
				String key = it.next();
				List<String> cinnerList = hmCorporate.get(key);
				// String alltwoDeciTotProgressAvgCorporate = "0";
				// String alltotal100Corporate = "100";
				// String strtwoDeciTotCorporate = "0";
				if (hmManagerGoalCalDetailsCorporate != null && !hmManagerGoalCalDetailsCorporate.isEmpty()) {
					Map<String, String> hmManagerGoalCalDetailsParentCorporate = hmManagerGoalCalDetailsCorporate.get(cinnerList.get(0));
					if (hmManagerGoalCalDetailsParentCorporate != null && !hmManagerGoalCalDetailsParentCorporate.isEmpty()) {
						alltwoDeciTotProgressAvgCorporate += uF.parseToDouble(hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0) + "_PERCENT"));
						// System.out.println("before -- alltotal100Corporate ===>> "
						// +alltotal100Corporate);
						alltotal100Corporate += uF.parseToDouble(hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0) + "_TOTAL"));
						// System.out.println("alltotal100Corporate ===>> "
						// +alltotal100Corporate);
						strtwoDeciTotCorporate += uF.parseToDouble(hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0) + "_STR_PERCENT"));
					}
				}

				String corpGoalRating = hmCorpGoalRating.get(cinnerList.get(0) + "_RATING");
				String corpGoalTaskCount = hmCorpGoalRating.get(cinnerList.get(0) + "_COUNT");
				if (uF.parseToInt(corpGoalTaskCount) > 0) {
					avgCorpGoalRating = uF.parseToDouble(corpGoalRating) / uF.parseToInt(corpGoalTaskCount);
				}
				count++;
			}

			// System.out.println("alltotal100Corporate ===>> " +
			// alltotal100Corporate + " -- count ===>> " + count);

			if (count > 0) {
				alltwoDeciTotProgressAvgCorporate = alltwoDeciTotProgressAvgCorporate / count;
				alltotal100Corporate = alltotal100Corporate / count;
				strtwoDeciTotCorporate = strtwoDeciTotCorporate / count;
				avgCorpGoalRating = avgCorpGoalRating / count;
			} else {
				alltotal100Corporate = 100;
			}

			request.setAttribute("alltwoDeciTotProgressAvgCorporate", uF.formatIntoOneDecimalWithOutComma(alltwoDeciTotProgressAvgCorporate));
			request.setAttribute("alltotal100Corporate", uF.formatIntoOneDecimalWithOutComma(alltotal100Corporate));
			request.setAttribute("strtwoDeciTotCorporate", uF.formatIntoOneDecimalWithOutComma(strtwoDeciTotCorporate));
			request.setAttribute("avgCorpGoalRating", uF.formatIntoOneDecimalWithOutComma(avgCorpGoalRating));

			request.setAttribute("corpGoalCount", "" + corpGoalCount);
			request.setAttribute("hmCorporate", hmCorporate);
			request.setAttribute("hmManagerGoalCalDetailsCorporate", hmManagerGoalCalDetailsCorporate);
			// System.out.println("hmCorporate =========> "+hmCorporate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private String getAppendData(Connection con, String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim()) + "(" + hmDesignation.get(temp[i].trim()) + ")");
					} else {
						sb.append("," + mp.get(temp[i].trim()) + "(" + hmDesignation.get(temp[i].trim()) + ")");
					}
				}
			} else {
				return mp.get(strID) + "(" + hmDesignation.get(strID) + ")";
			}

		} else {
			return null;
		}

		return sb.toString();
	}

	private void getEmployeeSkill(Connection con, UtilityFunctions uF, CommonFunctions CF) {

		ResultSet rs = null;
		PreparedStatement pst = null;
		try {

			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(emp_id) as count,skill_id from skills_description where skill_id is not null and emp_id in (select emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_alive=true");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(") group by skill_id");
			pst = con.prepareStatement(sbQuery.toString()); // emp_id,
			rs = pst.executeQuery();
			// List<List<String>> skillwiseEmpCountGraphList = new
			// ArrayList<List<String>>();
			// int i=0;
			// int otherCnt=0;
			// List<String> innerList = new ArrayList<String>();

			StringBuilder sbSkillwisePie = new StringBuilder();
			int skillCount = 0;
			while (rs.next()) {
				skillCount++;
				// innerList = new ArrayList<String>();
				// if(i<=8) {
				// innerList.add(CF.getSkillNameBySkillId(con,
				// rs.getString("skill_id")));
				// innerList.add(""+rs.getInt("count"));
				// } else {
				// otherCnt+=rs.getInt("count");
				// }
				// i++;
				// skillwiseEmpCountGraphList.add(innerList);

				sbSkillwisePie.append("{'Skill':'" + uF.showData(hmSkillName.get(rs.getString("skill_id")), "").replaceAll("[^a-zA-Z0-9]", "") + "', "
						+ "'cnt': " + rs.getInt("count") + "},");
			}
			rs.close();
			pst.close();

			// if(i>8) {
			// innerList = new ArrayList<String>();
			// innerList.add("Others");
			// innerList.add(""+otherCnt);
			// skillwiseEmpCountGraphList.add(innerList);
			// }
			if (sbSkillwisePie != null && sbSkillwisePie.length() > 1) {
				sbSkillwisePie.replace(0, sbSkillwisePie.length(), sbSkillwisePie.substring(0, sbSkillwisePie.length() - 1));
			}
			request.setAttribute("skillCount", "" + skillCount);
			request.setAttribute("sbSkillwisePie", sbSkillwisePie.toString());

			// request.setAttribute("skillwiseEmpCountGraphList",
			// skillwiseEmpCountGraphList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getDepartmentEmployeeCount(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			Map<String, String> hmDepartOrgName = CF.getOrgNameDepartIdwise(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*),depart_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_alive=true");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by depart_id");
			pst = con.prepareStatement(sbQuery.toString());
//			pst = con.prepareStatement(departmentEmployeeDashboardCount);
			rs = pst.executeQuery();
			int departCount = 0;
			Map<String, String> hmDepartmentEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				departCount++;
				hmDepartmentEmployeeCount.put(rs.getString("depart_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();

			request.setAttribute("departCount", "" + departCount);
			request.setAttribute("hmDepartmentEmployeeCount", hmDepartmentEmployeeCount);
			request.setAttribute("hmDepartOrgName", hmDepartOrgName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getWlocationEmployeeCount(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			Map<String, String> hmWLocOrgName = CF.getOrgNameWLocationIdwise(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(eod.emp_id) as count, wlocation_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and epd.is_alive=true and wlocation_id > 0 ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" group by wlocation_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int wLocationCount = 0;
			Map<String, String> hmWLocationEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				wLocationCount++;
				hmWLocationEmployeeCount.put(rs.getString("wlocation_id"), uF.formatIntoComma(rs.getDouble("count")));
			}
			rs.close();
			pst.close();
			request.setAttribute("wLocationCount", "" + wLocationCount);
			request.setAttribute("hmWLocationEmployeeCount", hmWLocationEmployeeCount);
			request.setAttribute("hmWLocOrgName", hmWLocOrgName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getBirthday(Connection con, UtilityFunctions uF, Map hmEmployeeMap) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, "MM-dd");
			String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1) + "", DBDATE, "MM-dd");
			String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2) + "", DBDATE, "MM-dd");

			pst = con.prepareStatement(selectBirthDay);

			pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			rs = pst.executeQuery();

			List<String> alBirthDays = new ArrayList<String>();
			while (rs.next()) {
				String strBDate = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM-dd");

				String gender = (String) rs.getString("emp_gender");
				if (strBDate != null && strBDate.equals(strToday1)) {
					if (hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						if (gender != null && gender.equalsIgnoreCase("M")) {
							alBirthDays.add("" + hmEmployeeMap.get(rs.getString("emp_per_id")) + ", has birthday today wish him...!");
						} else if (gender != null && gender.equalsIgnoreCase("F")) {
							alBirthDays.add("" + hmEmployeeMap.get(rs.getString("emp_per_id")) + ", has birthday today wish her...!");
						} else {
							alBirthDays.add("" + hmEmployeeMap.get(rs.getString("emp_per_id")) + ", has birthday today...!");
						}
					}
				}

				if (strBDate != null && strBDate.equals(strTomorrow)) {
					if (hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						if (gender != null && gender.equalsIgnoreCase("M")) {
							alBirthDays.add("" + hmEmployeeMap.get(rs.getString("emp_per_id")) + ", has birthday tomorrow wish him...!");
						} else if (gender != null && gender.equalsIgnoreCase("F")) {
							alBirthDays.add("" + hmEmployeeMap.get(rs.getString("emp_per_id")) + ", has birthday tomorrow wish her...!");
						} else {
							alBirthDays.add("" + hmEmployeeMap.get(rs.getString("emp_per_id")) + ", has birthday tomorrow...!");
						}
					}
				}
			}
			rs.close();
			pst.close();

			request.setAttribute("alBirthDays", alBirthDays);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ************************************* Old Data
	// *************************************************

	// public String loadDashboard() {
	//
	// Database db = new Database();
	// db.setRequest(request);
	// Connection con = null;
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// UtilityFunctions uF = new UtilityFunctions();
	// con = db.makeConnection(con);
	// try {
	// strUserType = (String)session.getAttribute(USERTYPE);
	//
	//
	//
	// Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con,null, null);
	// Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
	//
	// Map<String, String> hmEmpProfileImageMap = CF.getEmpProfileImage(con);
	// Map<String, String> hmServicesMap = CF.getServicesMap(con, false);
	//
	// Map<String, String> hmServicesDescMap = CF.getServicesMap(con, true);
	// Map<String, Map<String, String>> hmWorkLocationMap =
	// CF.getWorkLocationMap(con);
	//
	//
	// pst = con.prepareStatement(selectEmployee1V);
	// pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
	// rs = pst.executeQuery();
	// if (rs.next()) {
	// request.setAttribute("EMPCODE", rs.getString("empcode"));
	// request.setAttribute("EMPNAME", rs.getString("emp_fname") + " " +
	// rs.getString("emp_lname"));
	// request.setAttribute("DATE",
	// uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "",
	// "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
	// request.setAttribute("IMAGE", ((rs.getString("emp_image")!=null &&
	// rs.getString("emp_image").length()>0)?rs.getString("emp_image"):"avatar_photo.png"));
	// request.setAttribute("DEPT", rs.getString("dept_name"));
	// request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
	// request.setAttribute("EMAIL", rs.getString("emp_email"));
	// }
	// rs.close();
	// pst.close();
	//
	//
	//
	// /**
	// ********************** UPCOMING LEAVE REQUESTS END
	// ************************
	// *
	// * */
	//
	//
	// // getLeaveRequests(con, uF, hmEmployeeMap, hmLevelMap);
	// // getPendingExceptionCount(con, uF);
	// // getPendingReimbursementsCount(con, uF);
	// // getPendingRequisitionCount(con, uF);
	//
	// // getReimbursementRequests(con, uF, hmEmployeeMap);
	// // getRequisitionRequests(con, uF, hmEmployeeMap);
	//
	//
	//
	// getTasksCount(con, uF);
	// // getTodaysReportSentCount(con, uF);
	//
	//
	// getBestEmployee(con, uF);
	// getBestPartner(con, uF);
	// getServiceEmployeeCount(con, uF);
	// getWlocationEmployeeCount(con, uF);
	//
	//
	// getSkillsEmployeeCount(con, uF, 6);
	// // getResignedEmployees(con, uF, hmEmployeeMap);
	// getTaskDetails(con,uF, hmEmployeeMap);
	// getCompensation(con,uF, hmWorkLocationMap);
	// getTeamAllocation(uF);
	// getProjectExpension(con,uF,CF);
	//
	//
	// // ProjectPerformanceReportCP objPPR = new ProjectPerformanceReportCP();
	// // ProjectPerformanceReportWP objPPR = new ProjectPerformanceReportWP();
	// // objPPR.setServletRequest(request);
	// // objPPR.getProjectDetails(0, uF, CF, 10);
	// getProjectDetails(uF, CF, 10);
	//
	//
	// request.setAttribute("hmEmployeeMap",hmEmployeeMap);
	// request.setAttribute("hmServicesMap",hmServicesMap);
	// request.setAttribute("hmWorkLocationMap",hmWorkLocationMap);
	// request.setAttribute("hmEmpDesigMap",hmEmpDesigMap);
	// request.setAttribute("hmEmpProfileImageMap",hmEmpProfileImageMap);
	// request.setAttribute("hmServicesDescMap",hmServicesDescMap);
	//
	//
	// request.setAttribute("DOC_RETRIVE_LOCATION",
	// CF.getStrDocRetriveLocation());
	//
	//
	// /* RequirementApproval objReqApproval = new RequirementApproval();
	// objReqApproval.setServletRequest(request);
	// objReqApproval.getRquirementRequestsForManager();
	// */
	//
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeResultSet(rs);
	// db.closeStatements(pst);
	// db.closeConnection(con);
	// }
	//
	// return LOAD;
	// }

	// public void getResignedEmployees(Connection con, UtilityFunctions uF,
	// Map<String, String> hmEmployeeMap) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// pst =
	// con.prepareStatement("select * from emp_off_board where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and approved_1 is null order by entry_date desc");
	// pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
	// rs = pst.executeQuery();
	//
	// List<String> alResignedEmployees = new ArrayList<String>();
	// int count=0;
	// while (rs.next()) {
	//
	// StringBuilder sb = new StringBuilder();
	//
	// sb.append(hmEmployeeMap.get(rs.getString("emp_id"))
	// +" has resigned on "+rs.getString("entry_date"));
	//
	// sb.append("<div id=\"myDiv"+count+"\" style=\"float:right;margin-right:10px;\"> ");
	// sb.append("<a href=\"ResignationReport.action\">View</a> ");
	// sb.append("<a href=\"javascript:void(0)\" onclick=\"getContent('myDiv"+count+"','UpdateRequest.action?S=1&M=1&RID="+rs.getString("off_board_id")+"&T=REG');\" ><img src=\"images1/icons/approved.png\" title=\"Approve\" /></a> ");
	// sb.append("<a href=\"javascript:void(0)\" onclick=\"getContent('myDiv"+count+"','UpdateRequest.action?S=-1&M=1&RID="+rs.getString("off_board_id")+"&T=REG');\"><img src=\"images1/icons/denied.png\" title=\"Deny\" /></a> ");
	// sb.append("</div>");
	//
	// alResignedEmployees.add(sb.toString());
	// count++;
	// }
	// rs.close();
	// pst.close();
	//
	// request.setAttribute("alResignedEmployees", alResignedEmployees);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getLeaveRequests(Connection con, UtilityFunctions uF,
	// Map<String, String> hmEmployeeMap, Map<String, String> hmLevelMap) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// String strToday =
	// uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE,
	// DBDATE);
	// String strYesterday =
	// uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE,
	// DBDATE);
	//
	// // pst = con.prepareStatement(selectLeaveRequestManager);
	// // pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
	// // List alLeaveRequest = new ArrayList();
	// // rs = pst.executeQuery();
	// // while (rs.next()) {
	// // String strDate = rs.getString("entrydate");
	// //
	// // if(strDate!=null && strDate.equals(strToday)) {
	// // strDate = ", <span>today</span>";
	// // } else if(strDate!=null && strDate.equals(strYesterday)) {
	// // strDate = ", <span>yesterday</span>";
	// // } else {
	// // strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE,
	// "EEEE");
	// // strDate = "<span>"+strDate.toLowerCase()+"</span>";
	// // }
	// //
	// alLeaveRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"),
	// DBDATE,
	// "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"),
	// DBDATE, "dd MMM")+strDate+"</span>"+
	// //
	// "<span style=\"float: right;\"> <a style=\"float:right\" href=\"ManagerLeaveApproval.action?E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"\"><img src=\"images1/icons/approved.png\" alt=\"Approve\" title=\"Approve\" /></a></span>");
	// // }
	// // rs.close();
	// // pst.close();
	//
	// pst =
	// con.prepareStatement("SELECT leave_type_id,is_compensatory FROM leave_type  where leave_type_id>0 order by leave_type_name");
	// rs = pst.executeQuery();
	// Map<String,String> hmLeaveCompensate = new HashMap<String, String>();
	// while (rs.next()) {
	// hmLeaveCompensate.put(rs.getString("leave_type_id"),
	// rs.getString("is_compensatory"));
	// }
	// rs.close();
	// pst.close();
	//
	// StringBuilder sbQuery = new StringBuilder();
	// sbQuery.append("select ele.emp_id,ele.entrydate,ele.approval_from,ele.approval_to_date,ele.leave_id,ele.leave_type_id from emp_leave_entry ele,work_flow_details wft where wft.emp_id=? and ele.is_approved=0 "
	// +
	// "and ele.leave_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_LEAVE+"' and ele.entrydate >=? order by ele.leave_id desc");
	// pst = con.prepareStatement(sbQuery.toString());
	// pst.setInt(1, uF.parseToInt(strEmpId));
	// pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
	// rs = pst.executeQuery();
	// List<String> alLeaveRequest = new ArrayList<String>();
	// while(rs.next()) {
	// String strDate = rs.getString("entrydate");
	// if(strDate!=null && strDate.equals(strToday)) {
	// strDate = ", <span>today</span>";
	// } else if(strDate!=null && strDate.equals(strYesterday)) {
	// strDate = ", <span>yesterday</span>";
	// } else {
	// strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE,
	// "dd MMM");
	// strDate = "<span>"+strDate.toLowerCase()+"</span>";
	// }
	//
	// //
	// alLeaveRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"),
	// DBDATE,
	// "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"),
	// DBDATE, "dd MMM")+strDate+"</span>"+
	// //
	// "<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='ManagerLeaveApproval.action?type=type&apType=auto&apStatus=1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"';\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> "
	// +
	// //
	// " <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='ManagerLeaveApproval.action?type=type&apType=auto&apStatus=-1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"';\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" /></a></span>");
	// alLeaveRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"),
	// DBDATE,
	// "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"),
	// DBDATE, "dd MMM")+strDate+"</span>"+
	// "<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"');\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> "
	// +
	// " <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('-1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"');\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" /></a></span>");
	//
	// }
	// rs.close();
	// pst.close();
	//
	// request.setAttribute("alLeaveRequest",alLeaveRequest);
	// session.setAttribute("LEAVE_REQUEST_COUNT", alLeaveRequest.size()+"");
	//
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getReimbursementRequests(Connection con, UtilityFunctions uF,
	// Map<String, String> hmEmployeeMap) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// String strToday =
	// uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE,
	// DBDATE);
	// String strYesterday =
	// uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE,
	// DBDATE);
	//
	// Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
	// if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
	//
	// Map<String, Map<String, String>> hmCurrencyDetailsMap =
	// CF.getCurrencyDetailsForPDF(con);
	// if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new
	// HashMap<String, Map<String, String>>();
	//
	// // pst =
	// con.prepareStatement("select * from emp_reimbursement where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and (approval_1=0 or approval_1 is null)");
	// // pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
	// // rs = pst.executeQuery();
	// // List alReimbursementRequest = new ArrayList();
	// // while (rs.next()) {
	// // String strDate = rs.getString("entry_date");
	// //
	// // if(strDate!=null && strDate.equals(strToday)) {
	// // strDate = ", <span>today</span>";
	// // } else if(strDate!=null && strDate.equals(strYesterday)) {
	// // strDate = ", <span>yesterday</span>";
	// // } else {
	// // strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE,
	// "EEEE");
	// // strDate = "<span>"+strDate.toLowerCase()+"</span>";
	// // }
	// //
	// // String strCurrency = "";
	// // if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0) {
	// // Map<String, String> hmCurrency =
	// hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
	// // if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
	// // strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), "");
	// // }
	// //
	// //
	// alReimbursementRequest.add("<span style=\"width: 95%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp_id"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+rs.getString("reimbursement_amount")+"</strong> from "+uF.getDateFormat(rs.getString("from_date"),
	// DBDATE,
	// CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"),
	// DBDATE, CF.getStrReportDateFormat())+"</span>"+
	// //
	// "<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"),
	// "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"),
	// "")+"</p>"+
	// //
	// "<span style=\"float: right;\"> <a style=\"float:right\" href=\"Reimbursements.action\"><img src=\"images1/icons/approved.png\" alt=\"Approve\" title=\"Click to Approve/Deny\" /></a></span>");
	// // }
	// // rs.close();
	// // pst.close();
	//
	// pst =
	// con.prepareStatement("select  er.emp_id as emp,* from emp_reimbursement er,work_flow_details wft where wft.emp_id=? and wft.is_approved=0 "
	// +
	// " and er.reimbursement_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and er.entry_date>=? order by er.reimbursement_id desc");
	// pst.setInt(1, uF.parseToInt(strEmpId));
	// pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
	//
	// List<String> alReimbursementRequest = new ArrayList<String>();
	// rs = pst.executeQuery();
	// while (rs.next()) {
	// String strDate = rs.getString("entry_date");
	//
	// if(strDate!=null && strDate.equals(strToday)) {
	// strDate = ", <span>today</span>";
	// } else if(strDate!=null && strDate.equals(strYesterday)) {
	// strDate = ", <span>yesterday</span>";
	// } else {
	// strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE,
	// "dd MMM");
	// strDate = "<span>"+strDate.toLowerCase()+"</span>";
	// }
	//
	// String strCurrency = "";
	// if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp"))) > 0) {
	// Map<String, String> hmCurrency =
	// hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp")));
	// if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
	// strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), "");
	// }
	//
	// StringBuilder sb = new StringBuilder();
	// sb.append("<span style=\"width: 90%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("reimbursement_amount")))+"</strong> from "+uF.getDateFormat(rs.getString("from_date"),
	// DBDATE,
	// CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"),
	// DBDATE, CF.getStrReportDateFormat())+"</span>"+
	// "<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"),
	// "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"),
	// "")+"</p>");
	// sb.append("<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('1','"
	// + rs.getString("reimbursement_id")+
	// "');\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a>&nbsp;"
	// +
	// "<a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('-1','"
	// + rs.getString("reimbursement_id")+
	// "');\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" /></a>"
	// +
	// "</span>");
	// alReimbursementRequest.add(sb.toString());
	// }
	// rs.close();
	// pst.close();
	//
	// request.setAttribute("alReimbursementRequest",alReimbursementRequest);
	//
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getRequisitionRequests(Connection con, UtilityFunctions uF,
	// Map<String, String> hmEmployeeMap) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// String strToday =
	// uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE,
	// DBDATE);
	// String strYesterday =
	// uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE,
	// DBDATE);
	//
	// pst =
	// con.prepareStatement("select * from requisition_details where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and status=0 ");
	// pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
	//
	//
	// List alRequisitionRequest = new ArrayList();
	// rs = pst.executeQuery();
	// while (rs.next()) {
	// String strDate = rs.getString("requisition_date");
	//
	// if(strDate!=null && strDate.equals(strToday)) {
	// strDate = ", <span>today</span>";
	// } else if(strDate!=null && strDate.equals(strYesterday)) {
	// strDate = ", <span>yesterday</span>";
	// } else {
	// strDate = " on "+ uF.getDateFormat(rs.getString("requisition_date"),
	// DBDATE, "EEEE");
	// strDate = "<span>"+strDate.toLowerCase()+"</span>";
	// }
	//
	// String strRequest = null;
	// if("IR".equalsIgnoreCase(rs.getString("requisition_type"))) {
	// strRequest = "infrastructure";
	// } else if("OR".equalsIgnoreCase(rs.getString("requisition_type"))) {
	// strRequest = "other";
	// } else if("BF".equalsIgnoreCase(rs.getString("requisition_type"))) {
	// strRequest = "bonafide certificate";
	// }
	//
	// alRequisitionRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied for rquisition for "+strRequest+"</span>"+
	// "<span style=\"float: right;\"> <a style=\"float:right\" href=\"Requisitions.action\"><img src=\"images1/icons/approved.png\" alt=\"Approve\" title=\"Click to Approve/Deny\" /></a></span>");
	// }
	// rs.close();
	// pst.close();
	//
	// request.setAttribute("alRequisitionRequest", alRequisitionRequest);
	//
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getSkillsEmployeeCount(Connection con, UtilityFunctions uF,
	// int nLimit) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// try {
	// List<List<String>> skillwiseEmpCountList = new ArrayList<List<String>>();
	// pst =
	// con.prepareStatement("select skill_id, count(sd.emp_id) as count  from employee_official_details eod,employee_personal_details epd, skills_description sd where sd.emp_id = eod.emp_id  and sd.emp_id = epd.emp_per_id and epd.emp_per_id = eod.emp_id and is_alive = true and skill_id>0 group by skill_id"+((nLimit>0)?" limit "+nLimit:""));
	// System.out.println("pst=======>"+pst);
	// rs = pst.executeQuery();
	//
	// // Map<String, String> hmSkillsEmployeeCount = new HashMap<String,
	// String>();
	// while (rs.next()) {
	// List<String> innerList = new ArrayList<String>();
	// innerList.add(rs.getString("skill_id"));
	// innerList.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
	// innerList.add(uF.formatIntoComma(rs.getDouble("count")));
	// skillwiseEmpCountList.add(innerList);
	// // hmSkillsEmployeeCount.put(rs.getString("skill_id"),
	// uF.formatIntoComma(rs.getDouble("count")) );
	// }
	// rs.close();
	// pst.close();
	// request.setAttribute("skillwiseEmpCountList", skillwiseEmpCountList);
	// // request.setAttribute("hmSkillsEmployeeCount", hmSkillsEmployeeCount);
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getPendingExceptionCount(Connection con, UtilityFunctions uF)
	// {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	//
	// Calendar cal = GregorianCalendar.getInstance();
	// cal.set(Calendar.DATE,
	// uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"",
	// DBDATE, "dd")));
	// cal.set(Calendar.MONTH,
	// uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"",
	// DBDATE, "MM"))-1);
	// cal.set(Calendar.YEAR,
	// uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"",
	// DBDATE, "yyyy")));
	//
	// int nMinDate = cal.getActualMinimum(Calendar.DATE);
	// int nMaxDate = cal.getActualMaximum(Calendar.DATE);
	//
	// String strDate1 = nMinDate +"/"+(cal.get(Calendar.MONTH) +
	// 1)+"/"+cal.get(Calendar.YEAR);
	// String strDate2 = nMaxDate +"/"+(cal.get(Calendar.MONTH) +
	// 1)+"/"+cal.get(Calendar.YEAR);
	//
	// pst =
	// con.prepareStatement("select count(*) as count from attendance_details where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and approved = -2 ");
	// pst.setInt(1, uF.parseToInt(strEmpId));
	// pst.setDate(2, uF.getDateFormat(strDate1, DATE_FORMAT));
	// pst.setDate(3, uF.getDateFormat(strDate2, DATE_FORMAT));
	// rs = pst.executeQuery();
	// int nPendingExceptionCount = 0;
	// while (rs.next()) {
	// nPendingExceptionCount = rs.getInt("count");
	// }
	// rs.close();
	// pst.close();
	// session.setAttribute("PENDING_EXCEPTION_COUNT",
	// nPendingExceptionCount+"");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getPendingReimbursementsCount(Connection con,
	// UtilityFunctions uF) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// pst =
	// con.prepareStatement("select count(*) as count from emp_reimbursement where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and (approval_1 = 0  or approval_1 is null)");
	// pst.setInt(1, uF.parseToInt(strEmpId));
	// rs = pst.executeQuery();
	//
	// int nPendingReimbursementCount = 0;
	// while (rs.next()) {
	// nPendingReimbursementCount = rs.getInt("count");
	// }
	// rs.close();
	// pst.close();
	// session.setAttribute("PENDING_REIMBURSEMENT_COUNT",
	// nPendingReimbursementCount+"");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// public void getPendingRequisitionCount(Connection con, UtilityFunctions
	// uF) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// pst =
	// con.prepareStatement("select count(*) as count from requisition_details where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and status = 0");
	// pst.setInt(1, uF.parseToInt(strEmpId));
	// rs = pst.executeQuery();
	//
	// int nPendingRequisitionCount = 0;
	// while (rs.next()) {
	// nPendingRequisitionCount = rs.getInt("count");
	// }
	// rs.close();
	// pst.close();
	// session.setAttribute("PENDING_REQUISITION_COUNT",
	// nPendingRequisitionCount+"");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// public void getTasksCount(Connection con, UtilityFunctions uF) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// pst =
	// con.prepareStatement("select count(*) as count from activity_info where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and finish_task = 'y' and approve_status = 'n'");
	// pst.setInt(1, uF.parseToInt(strEmpId));
	// rs = pst.executeQuery();
	//
	//
	// int nCompletedTaskCount = 0;
	// while (rs.next()) {
	// nCompletedTaskCount = rs.getInt("count");
	// }
	// rs.close();
	// pst.close();
	// session.setAttribute("COMPLETED_TASK_COUNT", nCompletedTaskCount+"");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// public void getTodaysReportSentCount(Connection con, UtilityFunctions uF)
	// {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// pst =
	// con.prepareStatement("select count(*) as count from task_activity where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and issent_report = true and task_date=?");
	// pst.setInt(1, uF.parseToInt(strEmpId));
	// pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
	// rs = pst.executeQuery();
	// int nReportSentCount = 0;
	// while (rs.next()) {
	// nReportSentCount = rs.getInt("count");
	// }
	// rs.close();
	// pst.close();
	// session.setAttribute("REPORT_SENT_COUNT", nReportSentCount+"");
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// public void getBestEmployee(Connection con, UtilityFunctions uF) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// pst =
	// con.prepareStatement("select sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours, rd.emp_id from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' and rd._date between ? and ? group by rd.emp_id order by hours_worked desc limit 5");
	// pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 30));
	// pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
	//
	// rs = pst.executeQuery();
	//
	// double []WORKED_HOURS_DATA_MONTH = new double[2];
	// String []WORKED_HOURS_LABEL_MONTH = new String[2];
	// double dblWorkedHours = 0;
	// double dblActualHours = 0;
	// int count = 0;
	// Map<String, Map<String, String>> hmTopEmployees = new
	// LinkedHashMap<String, Map<String, String>>();
	//
	// while (rs.next()) {
	// if(count==0) {
	// dblWorkedHours = rs.getDouble("hours_worked");
	// dblActualHours = rs.getDouble("actual_hours");
	// }count ++;
	// Map<String, String> hmInner = new HashMap<String, String>();
	// hmInner.put("WORKED_HRS",
	// uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("hours_worked"))));
	// hmInner.put("ACTUAL_HRS",
	// uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("actual_hours"))));
	//
	// hmTopEmployees.put(rs.getString("emp_id"), hmInner);
	// }
	// rs.close();
	// pst.close();
	//
	// WORKED_HOURS_DATA_MONTH[0] = dblWorkedHours;
	// WORKED_HOURS_DATA_MONTH[1] = dblActualHours - dblWorkedHours;
	//
	// WORKED_HOURS_LABEL_MONTH[0] = "Worked";
	// WORKED_HOURS_LABEL_MONTH[1] = "Actual";
	//
	// request.setAttribute("KPI_BEST", new
	// SemiCircleMeter().getSemiCircleChart(WORKED_HOURS_DATA_MONTH,
	// WORKED_HOURS_LABEL_MONTH));
	// request.setAttribute("hmTopEmployees", hmTopEmployees);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getBestPartner(Connection con, UtilityFunctions uF) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// pst =
	// con.prepareStatement("select sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours, rd.emp_id from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' and rd._date between ? and ? group by rd.emp_id order by hours_worked desc limit 5");
	// pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 30));
	// pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
	// rs = pst.executeQuery();
	// double []WORKED_HOURS_DATA_MONTH = new double[2];
	// String []WORKED_HOURS_LABEL_MONTH = new String[2];
	// double dblWorkedHours = 0;
	// double dblActualHours = 0;
	// int count = 0;
	// Map<String, Map<String, String>> hmTopPartners = new
	// LinkedHashMap<String, Map<String, String>>();
	//
	// while (rs.next()) {
	// if(count==0) {
	// dblWorkedHours = rs.getDouble("hours_worked");
	// dblActualHours = rs.getDouble("actual_hours");
	// }count ++;
	// Map<String, String> hmInner = new HashMap<String, String>();
	// hmInner.put("WORKED_HRS",
	// uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("hours_worked"))));
	// hmInner.put("ACTUAL_HRS",
	// uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("actual_hours"))));
	//
	// hmTopPartners.put(rs.getString("emp_id"), hmInner);
	// }
	// rs.close();
	// pst.close();
	//
	// WORKED_HOURS_DATA_MONTH[0] = dblWorkedHours;
	// WORKED_HOURS_DATA_MONTH[1] = dblActualHours - dblWorkedHours;
	//
	// WORKED_HOURS_LABEL_MONTH[0] = "Worked";
	// WORKED_HOURS_LABEL_MONTH[1] = "Actual";
	//
	// request.setAttribute("KPI_BEST", new
	// SemiCircleMeter().getSemiCircleChart(WORKED_HOURS_DATA_MONTH,
	// WORKED_HOURS_LABEL_MONTH));
	// request.setAttribute("hmTopPartners", hmTopPartners);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getServiceEmployeeCount(Connection con, UtilityFunctions uF)
	// {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// // pst =
	// con.prepareStatement("select count(emp_id) as count, service_id from employee_personal_details epd, attendance_details ad where epd.emp_per_id = ad.emp_id and ad.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?)  and emp_status = 'ACTIVE' and ad.in_out = 'IN' and to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  group by service_id");
	// // pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
	// // pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
	// // pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
	// //
	// pst =
	// con.prepareStatement("select service_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive=true");
	// rs = pst.executeQuery();
	// Map<String, String> hmServicesEmployeeCount = new HashMap<String,
	// String>();
	// String []arrServices = null;
	// while (rs.next()) {
	// if(rs.getString("service_id")!=null) {
	// arrServices = rs.getString("service_id").split(",");
	// }
	//
	// for(int i=0; arrServices!=null && i<arrServices.length; i++) {
	// if(!arrServices[i].equals("")) {
	// int nCount = uF.parseToInt(hmServicesEmployeeCount.get(arrServices[i]));
	// hmServicesEmployeeCount.put(arrServices[i], (nCount+1)+"");
	// }
	// }
	// }
	// rs.close();
	// pst.close();
	// request.setAttribute("hmServicesEmployeeCount", hmServicesEmployeeCount);
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getWlocationEmployeeCount(Connection con, UtilityFunctions
	// uF) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// // pst =
	// con.prepareStatement("select count(eod.emp_id) as count, wlocation_id from employee_personal_details epd, attendance_details ad, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.emp_id = ad.emp_id and epd.emp_per_id = ad.emp_id and ad.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?)  and emp_status = 'ACTIVE' and ad.in_out = 'IN' and to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? group by wlocation_id");
	// // pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
	// // pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
	// // pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
	//
	// pst =
	// con.prepareStatement("select count(emp_per_id), wlocation_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive=true group by wlocation_id");
	// rs = pst.executeQuery();
	//
	// Map<String, String> hmWLocationEmployeeCount = new HashMap<String,
	// String>();
	// while (rs.next()) {
	// hmWLocationEmployeeCount.put(rs.getString("wlocation_id"),
	// uF.formatIntoComma(rs.getDouble("count")) );
	//
	// }
	// rs.close();
	// pst.close();
	// request.setAttribute("hmWLocationEmployeeCount",
	// hmWLocationEmployeeCount);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// public void getTaskDetails(Connection con, UtilityFunctions uF, Map
	// hmEmployeeMap) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// Map<String, String> hmProjectTeamLead = new HashMap<String, String>();
	// pst =
	// con.prepareStatement("select * from project_emp_details where _isteamlead = true");
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// hmProjectTeamLead.put(rs.getString("pro_id"), rs.getString("emp_id"));
	// }
	// rs.close();
	// pst.close();
	//
	// pst =
	// con.prepareStatement("select a.*, pmc.pro_name, pmc.completed, pmc.deadline, actual_calculation_type from (select pro_id, "
	// +
	// "sum(already_work) as already_work, sum(already_work_days) as already_work_days from activity_info ai group by pro_id order by "
	// +
	// "pro_id ) as a, projectmntnc pmc where pmc.pro_id = a.pro_id and pmc.approve_status='n' order by pmc.deadline limit 10");
	// rs = pst.executeQuery();
	//
	// List<List<String>> alTaskList = new ArrayList<List<String>>();
	// List<String> alTaskInner = new ArrayList<String>();
	//
	// while(rs.next()) {
	//
	// Date currentDate = uF.getCurrentDate(CF.getStrTimeZone());
	// Date deadLineDate = uF.getDateFormat(rs.getString("deadline"), DBDATE);
	// alTaskInner = new ArrayList<String>();
	// // alTaskInner.add(rs.getString("activity_name")
	// +" ["+rs.getString("pro_name")+"]");
	// alTaskInner.add(rs.getString("pro_name"));
	//
	// if(uF.parseToInt(rs.getString("completed"))>=100) {
	// alTaskInner.add("Completed");
	// } else {
	// if(currentDate!=null && deadLineDate!=null &&
	// currentDate.after(deadLineDate)) {
	// alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/denied.png\" border=\"0\"><span style=\"color:red\">Overdue</span>");
	// } else {
	// alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/approved.png\" border=\"0\"><span style=\"color:green\">Working</span>");
	// }
	// }
	//
	// alTaskInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE,
	// CF.getStrReportDateFormat()));
	// if(rs.getString("actual_calculation_type") != null &&
	// rs.getString("actual_calculation_type").equals("D")) {
	// alTaskInner.add(uF.showData(rs.getString("already_work_days"), "0") +
	// " days");
	// } else {
	// alTaskInner.add(uF.showData(rs.getString("already_work"), "0") + " hrs");
	// }
	// alTaskInner.add(uF.showData((String)hmEmployeeMap.get((String)hmProjectTeamLead.get(rs.getString("pro_id"))),
	// ""));
	// alTaskList.add(alTaskInner);
	// }
	// rs.close();
	// pst.close();
	// request.setAttribute("alTaskList", alTaskList);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	// public void getTeamAllocation(UtilityFunctions uF) {
	//
	// TeamAllocationReport obj=new TeamAllocationReport();
	// obj.setServletRequest(request);
	// obj.getProjectWiseProject(uF, 10);
	// List<List<String>> outerList = (List<List<String>>
	// )request.getAttribute("outerList");
	// // System.out.println("outerList ===>> " + outerList);
	//
	// StringBuilder sb=new StringBuilder();
	// for(int i=0;outerList != null && i<outerList.size();i++) {
	// List<String> innerList=outerList.get(i);
	// sb.append("['"+getContent(innerList.get(0))+"', "+innerList.get(1)+"],");
	// //
	// sb.append("{name: '"+getContent(innerList.get(0))+"', data:["+innerList.get(1)+"]},");
	// }
	// // System.out.println("pieChart ===>> " + sb.toString());
	// request.setAttribute("pieChart", sb.toString());
	// }
	//
	//
	// public void getProjectExpension(Connection con,UtilityFunctions
	// uF,CommonFunctions CF) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	// try {
	//
	// pst=con.prepareStatement("select * from payroll_generation where  is_paid=true and  paid_from=(select max(paid_from) from payroll_generation where  is_paid=true) and  paid_to=(select max(paid_to) from payroll_generation where  is_paid=true)  limit 1");
	// rs=pst.executeQuery();
	// String paycycle = null;
	// Date date = null;
	// while(rs.next()) {
	// paycycle = rs.getString("paycycle");
	// date = rs.getDate("paid_to");
	// }
	// rs.close();
	// pst.close();
	//
	// Calendar cal = Calendar.getInstance();
	// cal.setTime(date);
	// String month = uF.getMonth(cal.get(cal.MONTH)+1);
	//
	//
	// ExpenseReport obj = new ExpenseReport();
	// obj.setServletRequest(request);
	//
	// obj.setStrPC(paycycle);
	// obj.getExpenseReport(uF, 10,CF);
	// Map<String,String> expensiveMap = (Map<String,String>
	// )request.getAttribute("expensiveMap");
	//
	// StringBuilder sb = new StringBuilder();
	//
	// Set<String> set = expensiveMap.keySet();
	// Iterator<String> it = set.iterator();
	// int i=0;
	// List<String> list = new ArrayList<String>();
	// while(it.hasNext()) {
	// String key = it.next();
	//
	// if(uF.parseToDouble(expensiveMap.get(key))>0 && i<10) {
	// i++;
	// list.add(key);
	// sb.append("{name: '"+getContent(key)+"', data:["+expensiveMap.get(key)+"]},");
	// }
	//
	// }
	//
	// if(i<10) {
	// it=set.iterator();
	// while(it.hasNext()) {
	// String key=(String)it.next();
	//
	// if(!list.contains(key) && i<10) {
	// i++;
	// sb.append("{name: '"+getContent(key)+"', data:["+expensiveMap.get(key)+"]},");
	// }
	//
	// if(i==10) {
	// break;
	// }
	//
	// }
	// }
	//
	//
	// request.setAttribute("month", "'"+month+"'");
	// request.setAttribute("expensionAmount", sb.toString());
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// public void getCompensation(Connection con, UtilityFunctions uF,
	// Map<String, Map<String, String>> hmWorkLocationMap) {
	//
	// PreparedStatement pst = null;
	// ResultSet rs = null;
	//
	// try {
	//
	// Map<String, String> hmCompensation = new HashMap<String, String>();
	// List<String> alWolocations = new ArrayList<String>();
	//
	// pst =
	// con.prepareStatement("select sum(amount) as amount, month, year, wlocation_id from employee_official_details eod,(select sum(amount) as amount, emp_id, month, year  from payroll_generation where earning_deduction = 'E' group by emp_id, month, year) pd where eod.emp_id = pd.emp_id  group by month, year, wlocation_id order by wlocation_id, year desc, month");
	// // System.out.println("pst ====> " + pst);
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// hmCompensation.put(rs.getString("wlocation_id")+"_"+rs.getString("year")+"_"+rs.getString("month"),
	// uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
	//
	// if(!alWolocations.contains(rs.getString("wlocation_id"))) {
	// alWolocations.add(rs.getString("wlocation_id"));
	// }
	// }
	// rs.close();
	// pst.close();
	//
	// StringBuilder sb = new StringBuilder();
	// StringBuilder sbMonth = new StringBuilder();
	// for(int w=0; w<alWolocations.size(); w++) {
	//
	// Map<String, String> hmWLocationMap =
	// hmWorkLocationMap.get(alWolocations.get(w));
	// if(hmWLocationMap==null)hmWLocationMap = new HashMap<String, String>();
	//
	// Calendar cal = GregorianCalendar.getInstance();
	// sb.append("{name: \'"+getContent(uF.showData(hmWLocationMap.get("WL_NAME"),
	// ""))+"\', data:[");
	//
	// for(int i=0; i<4; i++) {
	// int nMonth = cal.get(Calendar.MONTH)+1;
	// int nYear = cal.get(Calendar.YEAR);
	//
	// sb.append(uF.showData(hmCompensation.get(alWolocations.get(w)+"_"+nYear+"_"+nMonth),
	// "0"));
	//
	// if(w==0) {
	// sbMonth.append("\'"+uF.getMonth(nMonth)+"\'");
	// if(i<4-1) {
	// sbMonth.append(",");
	// }
	// }
	//
	// cal.add(Calendar.MONTH, -1);
	// if(i<4-1) {
	// sb.append(",");
	// }
	//
	// }
	//
	// sb.append("]}");
	// if(w<alWolocations.size()-1) {
	// sb.append(",");
	// }
	//
	// }
	//
	// // System.out.println("sb.toString() ======>> " + sb.toString());
	// request.setAttribute("compensation", sb.toString());
	// request.setAttribute("compensationMonth", sbMonth.toString());
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// log.error(e.getClass() + ": " + e.getMessage(), e);
	// } finally {
	// if(rs !=null) {
	// try {
	// rs.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// if(pst !=null) {
	// try {
	// pst.close();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	//
	//
	//
	//
	// public void getProjectDetails(UtilityFunctions uF, CommonFunctions CF,
	// int nLimit) {
	//
	// Database db = new Database();
	// db.setRequest(request);
	// Connection con = null;
	// PreparedStatement pst=null;
	// ResultSet rs=null;
	// try {
	// con = db.makeConnection(con);
	//
	// // Map<String, String> hmDeadline = new HashMap<String, String>();
	// // Map<String, String> hmApprovedDate = new HashMap<String, String>();
	// // pst =
	// con.prepareStatement("select * from projectmntnc where approve_status = 'n' ");
	// // rs = pst.executeQuery();
	// // StringBuilder sbProjects = new StringBuilder();
	// // while(rs.next()) {
	// // hmDeadline.put(rs.getString("pro_id"), rs.getString("deadline"));
	// // hmApprovedDate.put(rs.getString("pro_id"),
	// rs.getString("approve_date"));
	// // sbProjects.append(rs.getString("pro_id")+",");
	// // }
	// //
	// // if(sbProjects.length() > 0) {
	// // sbProjects.replace(sbProjects.length()-1, sbProjects.length(), "");
	// // }
	//
	//
	// String strStartDate = null;
	// String strEndDate = null;
	// pst =
	// con.prepareStatement("select min(start_date), max(start_date) from projectmntnc pmntc where (approve_status = 'n' or approve_status = 'approved') and pro_id>0 ");
	// rs = pst.executeQuery();
	// while(rs.next()) {
	// if(rs.getString("min")!=null) {
	// strStartDate = uF.getDateFormat(rs.getString("min"), DBDATE,
	// DATE_FORMAT);
	// strEndDate = uF.getDateFormat(rs.getString("max"), DBDATE, DATE_FORMAT);
	// }
	// }
	// rs.close();
	// pst.close();
	//
	// Map<String, String> hmReimbursementAmountMap =
	// CF.getAllReimbursementAmount(con, "P", "P", "WC", strStartDate,
	// strEndDate, uF);
	// Map<String, String> hmEmpGrossAmountMapH = CF.getEmpNetSalary(uF, CF,
	// con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
	// Map<String, String> hmEmpGrossAmountMapD = CF.getEmpNetSalary(uF, CF,
	// con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D");
	//
	//
	// pst =
	// con.prepareStatement("select sum(variable_cost) as variable_cost, pro_id from variable_cost group by pro_id");
	// rs = pst.executeQuery();
	// Map<String, String> hmVariableCost = new HashMap<String, String>();
	// while(rs.next()) {
	// hmVariableCost.put(rs.getString("pro_id"),
	// rs.getString("variable_cost"));
	// }
	// rs.close();
	// pst.close();
	//
	// StringBuilder sbQuery1 = new StringBuilder();
	//
	// sbQuery1.append("select *, ai.emp_id as a_emp_id, ai.idealtime as a_idealtime, pmc.idealtime as pmc_idealtime, ai.already_work "
	// +
	// "as a_already_work, ai.already_work_days as a_already_work_days, pmc.actual_calculation_type from activity_info ai, "
	// +
	// "projectmntnc pmc where pmc.pro_id = ai.pro_id and (pmc.approve_status = 'n' or pmc.approve_status = 'approved') ");
	//
	// sbQuery1.append(" order by pmc.pro_id ");
	//
	// pst = con.prepareStatement(sbQuery1.toString());
	// rs = pst.executeQuery();
	//
	// // System.out.println("pst ==> " + pst);
	//
	// double dblBugedtedAmt = 0;
	// double dblActualAmt = 0;
	// double dblBillableAmt = 0;
	//
	// double dblActualTime = 0;
	//
	//
	// Map<String, String> hmProPerformaceProjectName = new HashMap<String,
	// String>();
	// Map<String, String> hmProPerformaceProjectProfit = new HashMap<String,
	// String>();
	//
	// Map<String, String> hmProName = new HashMap<String, String>();
	//
	// List<String> alProjectId = new ArrayList<String>();
	//
	// String strProjectIdNew=null;
	// String strProjectIdOld=null;
	//
	// double dblEmpRate = 0.0d;
	// StringBuilder sb = new StringBuilder();
	// StringBuilder sbMonth = new StringBuilder();
	//
	// while(rs.next()) {
	//
	// strProjectIdNew = rs.getString("pro_id");
	// double dblReimbursement =
	// uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
	//
	// hmProName.put(strProjectIdNew, rs.getString("pro_name"));
	//
	// if(strProjectIdNew!=null &&
	// !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
	// dblBugedtedAmt = 0;
	// dblActualAmt = 0;
	// dblBillableAmt = 0;
	//
	// dblActualTime = 0;
	// }
	//
	// if("F".equalsIgnoreCase(rs.getString("billing_type"))) {
	// dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
	// } else {
	// dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
	// }
	//
	// if("H".equalsIgnoreCase(rs.getString("actual_calculation_type"))) {
	// dblEmpRate =
	// uF.parseToDouble(hmEmpGrossAmountMapH.get(rs.getString("a_emp_id")));
	// dblActualTime += uF.parseToDouble(rs.getString("a_already_work"));
	// } else {
	// dblEmpRate =
	// uF.parseToDouble(hmEmpGrossAmountMapD.get(rs.getString("a_emp_id")));
	// dblActualTime += uF.parseToDouble(rs.getString("a_already_work_days"));
	// }
	//
	// dblBugedtedAmt += uF.parseToDouble(rs.getString("a_idealtime")) *
	// dblEmpRate;
	// dblActualAmt += dblActualTime * dblEmpRate;
	//
	//
	// hmProPerformaceProjectName.put(rs.getString("pro_id"),
	// rs.getString("pro_name"));
	//
	// double diff = 0;
	// if(dblBillableAmt > 0) {
	// diff = ((dblBillableAmt - (dblActualAmt +
	// dblReimbursement))/dblBillableAmt) * 100;
	// }
	// hmProPerformaceProjectProfit.put(rs.getString("pro_id"),
	// Math.round(diff)+"");
	//
	// if(!alProjectId.contains(rs.getString("pro_id"))) {
	// alProjectId.add(rs.getString("pro_id"));
	// }
	// strProjectIdOld = strProjectIdNew;
	//
	// if(alProjectId.size() >= nLimit && nLimit > 0) break;
	// }
	// rs.close();
	// pst.close();
	//
	//
	// for(int i=0; alProjectId != null && i<alProjectId.size(); i++) {
	//
	// sb.append("{name: '"+getContent(hmProName.get(alProjectId.get(i)))+"', data:[");
	//
	// sb.append(uF.showData(hmProPerformaceProjectProfit.get(alProjectId.get(i)),
	// "0")+"]},");
	// sbMonth.append("'"+getContent(hmProName.get(alProjectId.get(i)))+"'"+",");
	// }
	//
	// if(sb.length() > 0) {
	// sb.replace(sb.lastIndexOf(","), sb.length(), "");
	// sbMonth.replace(sbMonth.lastIndexOf(","), sbMonth.length(), "");
	// }
	//
	// // sb.append("]}");
	// // System.out.println("profitablitiy ===>> " + sb.toString());
	// // System.out.println("projects ===>> " + sbMonth.toString());
	//
	// request.setAttribute("profitablitiy", sb.toString());
	// request.setAttribute("projects", sbMonth.toString());
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// db.closeResultSet(rs);
	// db.closeStatements(pst);
	// db.closeConnection(con);
	// }
	// }

	public String getContent(String data) {
		if (data != null) {
			data = data.replace("'", "");
		} else {
			data = "";
		}
		return data;
	}
}
