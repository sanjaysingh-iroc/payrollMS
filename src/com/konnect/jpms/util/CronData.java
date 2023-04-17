package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;

import com.konnect.jpms.master.OvertimeApproval;
import com.konnect.jpms.performance.GoalScheduler;
import com.konnect.jpms.task.ProjectScheduler;

public class CronData extends Thread implements IStatements, ServletRequestAware {

	public CommonFunctions CF;
	public HttpServletRequest request;
	public HttpSession session;
	public String strEmpId;
	public String strDomain;

	public CronData() {
		super();
	}

	public void setCronData() {
		if (!isAlive()) {
			start();
		}
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void run() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);

		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagAssignShiftOnBasisOfRules = uF.parseToBoolean(hmFeatureStatus.get(F_ASSIGN_SHIFT_ON_BASIS_OF_RULES));
			boolean flagEnableHDFDExceptionRule = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_HD_FD_EXCEPTION_RULE));
			boolean flagEnableOrgAttendanceApprovalStatus = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_ORG_ATTENDANCE_APPROVAL_STATUS_MAIL));
			boolean flagEnableOrgSalaryApprovalStatus = uF.parseToBoolean(hmFeatureStatus.get(F_ENABLE_ORG_SALARY_APPROVAL_STATUS_MAIL));
			
			setFinancialData(con, uF);
			carriedForwardLeave(con, uF);
			accrualLeave(con, uF);
			accrualEarnedLeave(con, uF);
			setReimbursementCTC(con, uF);
			if(uF.parseToBoolean(hmFeatureStatus.get(F_EXTRA_WORKING_LAPS_DAYS_LIMIT_FOR_COMPOFF_LEAVE))){
				extraWorkingLapsDays(con, uF);
			}
			
//			 setOvertimeMinuteAlertNotification(con,uF);
			// setPerkInSalaryDetails(con,uF);
			// addNewProject(uF);
			checkTodaysBirthdaysMarriageAndWorkAnniversary(con,uF);
			
			getInductionData(con, uF);
			getConfirmationData(con, uF);
			getResignationData(con, uF);

			if(flagEnableHDFDExceptionRule) {
				generateHalfDayFullDayException(con, uF);
			}
			
//			System.out.println("flagEnableOrgSalaryApprovalStatus ===>> " + flagEnableOrgSalaryApprovalStatus);
//			System.out.println("flagEnableOrgAttendanceApprovalStatus ===>> " + flagEnableOrgAttendanceApprovalStatus);
			
			if(flagEnableOrgAttendanceApprovalStatus || flagEnableOrgSalaryApprovalStatus) {
				sendMonthlyAttendanceAndSalaryApprovalPending(con, uF, flagEnableOrgAttendanceApprovalStatus, flagEnableOrgSalaryApprovalStatus);
			}
//			System.out.println("flagAssignShiftOnBasisOfRules ===>> " + flagAssignShiftOnBasisOfRules);
			if(flagAssignShiftOnBasisOfRules) {
//				assignShiftsOnBasisOfRules(con, uF);
//				assignShiftTransitionWeeklyOffOnBasisOfRules(con, uF);
//				assignWeeklyOffOnBasisOf7DaysStretch(con, uF);
//				assignWeeklyOffOnBasisOf3Days(con, uF);
//				assignWeeklyOffOnBasisOf7Days(con, uF);
				
				
//				assignWeeklyOffOnBasisOfRulesNew(con, uF);
//				assignWeeklyOffOnBasisOfRules(con, uF);
//				assignWeekEndWeeklyOffOnBasisOfRules(con, uF);
				
//				assignWeeklyOffOnBasisOfAdjustWithWorkingDays(con, uF);
//				assignWeeklyOffOnBasisOfAdjustWithWorkingDays_1(con, uF); 
			}
			
			// addGoalFrequency(uF);
		} catch (Exception e) {
			e.printStackTrace();																							
		} finally {
			db.closeConnection(con);
		}
	}
	
	
	private void assignShiftTransitionWeeklyOffOnBasisOfRules(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
//			sbQuery.append(" and emp_id=193 ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("hmEmpLastShiftAssignedDate ===>> " + hmEmpLastShiftAssignedDate);
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			pst = con.prepareStatement("select * from roster_details where _date between ? and ? order by emp_id,_date");
			pst.setDate(1, uF.getDateFormat(getDate(strPaycycleFrmDate, DATE_FORMAT, -1), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpwiseDatewiseShiftId = new LinkedHashMap<String, Map<String,String>>();
			while(rst.next()) {
				Map<String, String> hmDatewiseShiftId = hmEmpwiseDatewiseShiftId.get(rst.getString("emp_id"));
				if(hmDatewiseShiftId==null) hmDatewiseShiftId = new LinkedHashMap<String, String>();
				hmDatewiseShiftId.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT), rst.getString("shift_id"));
				hmEmpwiseDatewiseShiftId.put(rst.getString("emp_id"), hmDatewiseShiftId);
			}
			rst.close();
			pst.close();
//			System.out.println("hmEmpwiseDatewiseShiftId ===>> " + hmEmpwiseDatewiseShiftId);
			
			Map<String, List<Map<String, String>>> hmEmpwiseTransWeekOffDates = new LinkedHashMap<String, List<Map<String, String>>>();
			Iterator<String> it = hmEmpLastShiftAssignedDate.keySet().iterator();
			while (it.hasNext()) {
				String empId = it.next();
//				System.out.println("empId ===>> " + empId +" -- nOfdays ===>> " + nOfdays+" -- strPaycycleFrmDate ===>> " + strPaycycleFrmDate);
				Map<String, String> hmDatewiseShiftId = hmEmpwiseDatewiseShiftId.get(empId);
				if(hmDatewiseShiftId==null) hmDatewiseShiftId = new LinkedHashMap<String, String>();
				for(int j=0; j<nOfdays; j++) {
					String newDate1 = getDate(strPaycycleFrmDate, DATE_FORMAT, j);
					String strDay = uF.getDateFormat(newDate1, DATE_FORMAT, "E");
					strDay = strDay.toUpperCase();
					
					if(strPaycycleFrmDate.equals(newDate1) && strDay!=null && strDay.equals("SUN")) {
						String newPrevDate1 = getDate(newDate1, DATE_FORMAT, -1);
						String strTodayShiftId = hmDatewiseShiftId.get(newDate1);
						String strPrevDateShiftId = hmDatewiseShiftId.get(newPrevDate1);
						
						if(empId.equals("167") || empId.equals("170")) {
//							System.out.println("empId ===>> " + empId +" -- strPrevDateShiftId ===>> " + strPrevDateShiftId);
						}
						if(uF.parseToInt(strTodayShiftId)>0 && uF.parseToInt(strPrevDateShiftId)>0 && uF.parseToInt(strTodayShiftId)!=uF.parseToInt(strPrevDateShiftId)) {
							List<Map<String, String>> alShiftTrasWeekoffDates = hmEmpwiseTransWeekOffDates.get(empId);
							if(alShiftTrasWeekoffDates==null) alShiftTrasWeekoffDates = new ArrayList<Map<String,String>>();
							Map<String, String> hmDateShiftId = new HashMap<String, String>();
							hmDateShiftId.put(newDate1, strTodayShiftId);
							alShiftTrasWeekoffDates.add(hmDateShiftId);
							hmEmpwiseTransWeekOffDates.put(empId, alShiftTrasWeekoffDates);
						}
					} else if(strDay!=null && strDay.equals("SAT") ) {
						String newNextDate1 = getDate(newDate1, DATE_FORMAT, 1);
						String strTodayShiftId = hmDatewiseShiftId.get(newDate1);
						String strTomorrowShiftId = hmDatewiseShiftId.get(newNextDate1);
						
						if(strPaycycleToDate.equals(newDate1)) {
							String prev14DayDate = getDate(newDate1, DATE_FORMAT, -14);
							String prev7DayDate = getDate(newDate1, DATE_FORMAT, -7);
							String prev14DayShiftId = hmDatewiseShiftId.get(prev14DayDate);
							String prev7DayShiftId = hmDatewiseShiftId.get(prev7DayDate);
							if(empId.equals("167") || empId.equals("170")) {
//								System.out.println("empId ===>> " + empId +" -- prev14DayDate ===>> " + prev14DayDate);
							}
							if(uF.parseToInt(strTodayShiftId)>0 && uF.parseToInt(prev14DayShiftId)>0 && uF.parseToInt(strTodayShiftId)!=uF.parseToInt(prev14DayShiftId) && uF.parseToInt(prev7DayShiftId)!=uF.parseToInt(prev14DayShiftId)) {
								List<Map<String, String>> alShiftTrasWeekoffDates = hmEmpwiseTransWeekOffDates.get(empId);
								if(alShiftTrasWeekoffDates==null) alShiftTrasWeekoffDates = new ArrayList<Map<String,String>>();
								Map<String, String> hmDateShiftId = new HashMap<String, String>();
								hmDateShiftId.put(newDate1, strTodayShiftId);
								alShiftTrasWeekoffDates.add(hmDateShiftId);
								hmEmpwiseTransWeekOffDates.put(empId, alShiftTrasWeekoffDates);
							}
						} else if(uF.parseToInt(strTodayShiftId)>0 && uF.parseToInt(strTomorrowShiftId)>0 && uF.parseToInt(strTodayShiftId)!=uF.parseToInt(strTomorrowShiftId)) {
							List<Map<String, String>> alShiftTrasWeekoffDates = hmEmpwiseTransWeekOffDates.get(empId);
							if(alShiftTrasWeekoffDates==null) alShiftTrasWeekoffDates = new ArrayList<Map<String,String>>();
							Map<String, String> hmDateShiftId = new HashMap<String, String>();
							hmDateShiftId.put(newDate1, strTodayShiftId);
							hmDateShiftId.put(newNextDate1, strTomorrowShiftId);
							alShiftTrasWeekoffDates.add(hmDateShiftId);
							hmEmpwiseTransWeekOffDates.put(empId, alShiftTrasWeekoffDates);
						}
					}
				}
			}
			
//			System.out.println("hmEmpwiseTransWeekOffDates ===>> " + hmEmpwiseTransWeekOffDates);
			Iterator<String> itEmp = hmEmpwiseTransWeekOffDates.keySet().iterator();
			while (itEmp.hasNext()) {
				String empId = itEmp.next();
				List<Map<String, String>> alShiftTrasWeekoffDates = hmEmpwiseTransWeekOffDates.get(empId);
				for(int i=0; alShiftTrasWeekoffDates!=null && i<alShiftTrasWeekoffDates.size(); i++) {
					Map<String, String> hmDateShiftId = alShiftTrasWeekoffDates.get(i);
					Iterator<String> itDate = hmDateShiftId.keySet().iterator();
					while (itDate.hasNext()) {
						String strWeekOffDate = itDate.next();
						String strWeekOffShiftId = hmDateShiftId.get(strWeekOffDate);
						String strEmpServiceId = hmEmpServiceId.get(empId) != null ? hmEmpServiceId.get(empId).substring(1, hmEmpServiceId.get(empId).length()-1) : "0";
						insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, strWeekOffDate, empId, strEmpServiceId, strWeekOffShiftId);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void assignWeeklyOffOnBasisOfRulesNew(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
//			sbQuery.append(" and emp_id=193 ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
//			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpNoInShifts = new HashMap<String, String>();
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			List<String> alWeekEnds = uF.getWeekEnds();
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			int maleGenderCnt = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"));
					
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
			int intWeekEndOffInaMonth = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_WEEKEND_OFF_IN_MONTH"));
			intWeekEndOffInaMonth = (intWeekEndOffInaMonth * 2);
//			System.out.println("intWeekEndEmpTlGenderCnt ------------->> " + intWeekEndEmpTlGenderCnt);
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			
			Map<String, List<String>> hmDatewiseEmpAssignedWeekOffData = getAssignedWeekOffData(con, uF, strPaycycleFrmDate, strPaycycleToDate);
			if(hmDatewiseEmpAssignedWeekOffData == null) hmDatewiseEmpAssignedWeekOffData = new HashMap<String, List<String>>();
			
			List<String> alCurrMonthWeekEnds = new ArrayList<String>();
			Map<String, List<String>> hmEmpwiseWeekEnds = new LinkedHashMap<String, List<String>>();
			Map<String, Map<String, Map<String, String>>> hmDatewiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			Map<String, Map<String, Map<String, String>>> hmDatewiseTLData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			Map<String, Map<String, String>> hmShiftwiseTLData = new LinkedHashMap<String, Map<String, String>>();
			
			Map<String, List<String>> hmLeaveCntEmpwise = new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> hmTransWeekOffCntEmpwise = new LinkedHashMap<String, List<String>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpGender = CF.getEmpGenderMap(con);
			if(hmEmpGender == null) hmEmpGender = new HashMap<String, String>();
			
			
			
			
			pst = con.prepareStatement("select * from roster_details where _date between ? and ? order by emp_id,_date");
			pst.setDate(1, uF.getDateFormat(strPaycycleFrmDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpwiseDatewiseShiftId = new LinkedHashMap<String, Map<String,String>>();
			Map<String, String> hmEmpwiseWeekendNos = new HashMap<String, String>();
			Map<String, List<String>> hmWeekendNowiseDates = new HashMap<String, List<String>>();
			
			while(rst.next()) {
				
				String strDay = uF.getDateFormat(rst.getString("_date"), DBDATE, "E");
				strDay = strDay.toUpperCase();
				
				if(alWeekEnds.contains(strDay)) {
					Map<String, String> hmDatewiseShiftId = hmEmpwiseDatewiseShiftId.get(rst.getString("emp_id"));
					if(hmDatewiseShiftId==null) hmDatewiseShiftId = new LinkedHashMap<String, String>();
					int weekendNo = 0;
					if(strDay.equalsIgnoreCase("SAT")) {
						weekendNo = uF.parseToInt(hmEmpwiseWeekendNos.get(rst.getString("emp_id")));
						weekendNo++;
					}
					hmEmpwiseWeekendNos.put(rst.getString("emp_id"), weekendNo+"");
					List<String> alWeekendNowiseDate = hmWeekendNowiseDates.get(weekendNo+"");
					if(alWeekendNowiseDate==null) alWeekendNowiseDate = new ArrayList<String>();
					if(!alWeekendNowiseDate.contains(rst.getString("_date"))) {
						alWeekendNowiseDate.add(rst.getString("_date"));
					}
					hmWeekendNowiseDates.put(weekendNo+"", alWeekendNowiseDate);
					
					hmDatewiseShiftId.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT), rst.getString("roster_weeklyoff_id"));
					hmEmpwiseDatewiseShiftId.put(rst.getString("emp_id"), hmDatewiseShiftId);
					
				}
				
			}
			rst.close();
			pst.close();

			
			String newShiftId = "";
			Map<String, List<String>> hmEmpwiseDateLeaveData = getEmpApprovedLeaveDataEmpwise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmEmpwiseDateLeaveData == null) hmEmpwiseDateLeaveData = new HashMap<String, List<String>>();
			
//			System.out.println("hmDatewiseData ===>> " + hmDatewiseData);
//			System.out.println("hmDatewiseTLData ===>> " + hmDatewiseTLData);
			
			Map<String, Map<String, Map<String, String>>> hmDatewiseTLData1 = new LinkedHashMap<String, Map<String, Map<String, String>>>();
			Iterator<String> itt = hmDatewiseTLData.keySet().iterator();
//			Iterator<String> ittDT = hmDatewiseTLData.keySet().iterator();
			Map<String, Map<String, List<String>>> hmShiftEmpTLData = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmWeekOffShiftEmpTLData = new LinkedHashMap<String, Map<String, List<String>>>();
//			Map<String, String> hmAssignRosterCntEmpwise = new LinkedHashMap<String, String>();
//			Map<String, String> hmAssignWODaysCntEmpwise = new LinkedHashMap<String, String>();
//			Map<String, List<Integer>> hmLeaveEmpIds = new LinkedHashMap<String, List<Integer>>();
			while(itt.hasNext()) {
				String strDate = itt.next();
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
//				System.out.println("strDate ===>> " + strDate);
				Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseTLData.get(strDate);
//				System.out.println("hmShiftwiseData ===>> " + hmShiftwiseData);
				Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
				while(it1.hasNext()) {
					String strShiftId = it1.next();
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
					
					Map<String, List<String>> hmEmpTLData = hmShiftEmpTLData.get(strShiftId);
					if(hmEmpTLData==null) hmEmpTLData = new LinkedHashMap<String, List<String>>();
					
					Map<String, List<String>> hmWeekOffEmpTLData = hmWeekOffShiftEmpTLData.get(strShiftId);
					if(hmWeekOffEmpTLData==null) hmWeekOffEmpTLData = new LinkedHashMap<String, List<String>>();
					
					Iterator<String> it2 = hmInner.keySet().iterator();
					while(it2.hasNext()) {
						String strEmpId = it2.next();
//						System.out.println("strEmpId ===>> " + strEmpId);
						int intDateIndex = alCurrMonthWeekEnds.indexOf(strDate);
						String strNextDt1 = (alCurrMonthWeekEnds.size()>(intDateIndex+1)) ? alCurrMonthWeekEnds.get(intDateIndex+1) : null;
						String strNextDt2 = (alCurrMonthWeekEnds.size()>(intDateIndex+2)) ? alCurrMonthWeekEnds.get(intDateIndex+2) : null;
						String strNextDt3 = (alCurrMonthWeekEnds.size()>(intDateIndex+3)) ? alCurrMonthWeekEnds.get(intDateIndex+3) : null;
						String strPrevDt1 = (0<=(intDateIndex-1)) ? alCurrMonthWeekEnds.get(intDateIndex-1) : null;
						String strPrevDt2 = (0<=(intDateIndex-2)) ? alCurrMonthWeekEnds.get(intDateIndex-2) : null;
						String strPrevDt3 = (0<=(intDateIndex-3)) ? alCurrMonthWeekEnds.get(intDateIndex-3) : null;
						
						if((hmEmpTLData.get(strDate)==null || hmEmpTLData.get(strDate).size()<uF.parseToInt(strTlCnt)) ) {
							boolean assignRosterFlag = false;
							Iterator<String> it21 = hmInner.keySet().iterator();
							while(it21.hasNext()) {
								String strEmpIdInn = it21.next();
								List<String> alEmpLeaves = hmLeaveCntEmpwise.get(strEmpIdInn);
								if(alEmpLeaves==null) alEmpLeaves = new ArrayList<String>();
								List<String> alEmpTransWO = hmTransWeekOffCntEmpwise.get(strEmpIdInn);
								if(alEmpTransWO==null) alEmpTransWO = new ArrayList<String>();
//								System.out.println("strEmpIdInn ===>> " + strEmpIdInn + " -- alEmpLeaves ===>> " + alEmpLeaves + " -- alEmpTransWO ===>> " + alEmpTransWO);
								if(hmInner.size()==uF.parseToInt(strTlCnt)) {
									assignRosterFlag = true;
									
								} else {
									if(uF.parseToInt(strEmpId) != uF.parseToInt(strEmpIdInn) && strDay.equals("SAT") && 
										((strPrevDt1==null && strPrevDt2==null && strNextDt1!=null && strNextDt2!=null && strNextDt3!=null 
											&& !alEmpLeaves.contains(strNextDt1) && !alEmpLeaves.contains(strNextDt2) && !alEmpLeaves.contains(strNextDt3) 
											&& !alEmpTransWO.contains(strNextDt1) && !alEmpTransWO.contains(strNextDt2) && !alEmpTransWO.contains(strNextDt3)) 
										|| (strPrevDt1==null && strPrevDt2==null && strNextDt1!=null && strNextDt2!=null && strNextDt3==null && !alEmpLeaves.contains(strNextDt1) && !alEmpLeaves.contains(strNextDt2) 
											&& !alEmpTransWO.contains(strNextDt1) && !alEmpTransWO.contains(strNextDt2)) 
										|| (strPrevDt1==null && strPrevDt2==null && strNextDt1!=null && strNextDt2==null && strNextDt3==null && !alEmpLeaves.contains(strNextDt1) && !alEmpTransWO.contains(strNextDt1))
										 ) ) { //|| (strNextDt1==null && strNextDt2==null && strNextDt3==null)
										assignRosterFlag = true;
//										System.out.println("assignRosterFlag 1 if ===>> " + assignRosterFlag);
									} else if(uF.parseToInt(strEmpId) != uF.parseToInt(strEmpIdInn) && strDay.equals("SUN") && 
										((strPrevDt2==null && strPrevDt3==null && strNextDt1!=null && strNextDt2!=null && !alEmpLeaves.contains(strNextDt1) && !alEmpLeaves.contains(strNextDt2) 
											&& !alEmpTransWO.contains(strNextDt1) && !alEmpTransWO.contains(strNextDt2)) 
										|| (strPrevDt2==null && strPrevDt3==null && strNextDt1!=null && strNextDt2==null && !alEmpLeaves.contains(strNextDt1) && !alEmpTransWO.contains(strNextDt1)) 
										 ) ) { //|| (strNextDt1==null && strNextDt2==null && strNextDt3==null)
										assignRosterFlag = true;
//										System.out.println("assignRosterFlag 2 if ===>> " + assignRosterFlag);
									} else if(uF.parseToInt(strEmpId) == uF.parseToInt(strEmpIdInn) && strDay.equals("SAT") && 
										((strPrevDt1!=null && strPrevDt2!=null && hmEmpTLData.get(strPrevDt1) !=null && hmEmpTLData.get(strPrevDt2) !=null && !hmEmpTLData.get(strPrevDt1).contains(strEmpId) && !hmEmpTLData.get(strPrevDt2).contains(strEmpId))
										) ) {
										assignRosterFlag = true;
//										System.out.println("assignRosterFlag 3 if ===>> " + assignRosterFlag);
									} else if(uF.parseToInt(strEmpId) != uF.parseToInt(strEmpIdInn) && strDay.equals("SUN") && 
										((strPrevDt2!=null && strPrevDt3!=null && hmEmpTLData.get(strPrevDt2)!=null && hmEmpTLData.get(strPrevDt3)!=null && !hmEmpTLData.get(strPrevDt2).contains(strEmpId) && !hmEmpTLData.get(strPrevDt3).contains(strEmpId)) 
										) ) {
										assignRosterFlag = true;
//										System.out.println("assignRosterFlag 4 if ===>> " + assignRosterFlag);
									}
								}
							}
//							System.out.println("assignRosterFlag ===>> " + assignRosterFlag);
							
							if(assignRosterFlag) {
								List<String> alEmpIds = hmEmpTLData.get(strDate);
								if(alEmpIds==null) alEmpIds = new ArrayList<String>();
								alEmpIds.add(strEmpId);
								hmEmpTLData.put(strDate, alEmpIds);
								
							} else {
								List<String> alEmpIds = hmWeekOffEmpTLData.get(strDate);
								if(alEmpIds==null) alEmpIds = new ArrayList<String>();
								alEmpIds.add(strEmpId);
								hmWeekOffEmpTLData.put(strDate, alEmpIds);
							}
								
						} else {
							List<String> alEmpIds = hmWeekOffEmpTLData.get(strDate);
							if(alEmpIds==null) alEmpIds = new ArrayList<String>();
							alEmpIds.add(strEmpId);
							hmWeekOffEmpTLData.put(strDate, alEmpIds);
						}
					}
						hmShiftEmpTLData.put(strShiftId, hmEmpTLData);
						hmWeekOffShiftEmpTLData.put(strShiftId, hmWeekOffEmpTLData);
				}
			}
			
//			System.out.println("hmShiftEmpTLData ===>> " + hmShiftEmpTLData);
//			System.out.println("hmWeekOffShiftEmpTLData ===>> " + hmWeekOffShiftEmpTLData);
			
			
			
			
			/*boolean assignRosterFlag = false;
			Iterator<String> itEmp = hmLeaveEmpIds.keySet().iterator();
			while(itEmp.hasNext()) {
				String strEmpIdInn = itEmp.next();
				List<Integer> innList = hmLeaveEmpIds.get(strEmpIdInn);
				if(innList==null) innList = new ArrayList<Integer>();
				if(uF.parseToInt(strEmpId) == uF.parseToInt(strEmpIdInn) && alNextLeaveDays.size()>0 && (alNextLeaveDays.contains(2) || alNextLeaveDays.contains(3)) ) {
					assignRosterFlag = true;
				} else if((uF.parseToInt(strEmpId) != uF.parseToInt(strEmpIdInn) && (innList.size()==0 
					|| (innList.contains(0) && !innList.contains(1) && !innList.contains(2) && !innList.contains(3))
					|| (innList.contains(0) && innList.contains(1) && !innList.contains(2) && !innList.contains(3)) ) )
				) {
					assignRosterFlag = true;
				}
			}
			if(uF.parseToInt(strEmpId)==127 || uF.parseToInt(strEmpId)==137) {
//				System.out.println("1 -- " + strEmpId + " -- assignRosterFlag ===>> " + assignRosterFlag);
			}
			
			if(assignRosterFlag) {
				List<String> alEmpIds = hmEmpData.get(strDate+"_"+strShiftId);
				if(alEmpIds==null) alEmpIds = new ArrayList<String>();
				alEmpIds.add(strEmpId);
				hmEmpData.put(strDate+"_"+strShiftId, alEmpIds);
				
				int assignRosterCnt = uF.parseToInt(hmAssignRosterCntEmpwise.get(strEmpId));
				assignRosterCnt++;
				hmAssignRosterCntEmpwise.put(strEmpId, assignRosterCnt+"");
				
				int assignRosterDaysCnt = uF.parseToInt(hmAssignRosterDaysCntEmpwise.get(strEmpId));
				assignRosterDaysCnt++;
				hmAssignRosterDaysCntEmpwise.put(strEmpId, assignRosterDaysCnt+"");
			} else {
				List<String> alEmpIds = hmWeekOffEmpData.get(strDate+"_"+strShiftId);
				if(alEmpIds==null) alEmpIds = new ArrayList<String>();
				alEmpIds.add(strEmpId);
				hmWeekOffEmpData.put(strDate+"_"+strShiftId, alEmpIds);
			}*/
			
			
			/*Iterator<String> it21 = hmInner.keySet().iterator();
			while(it21.hasNext()) {
				String strEmpId = it21.next();
				List<String> alEmpOnLeaveForNextDates = hmEmpwiseDateLeaveData.get(strEmpId);
//				System.out.println("strEmpId ===>> " + strEmpId);
				List<Integer> alNextLeaveDays = hmLeaveEmpIds.get(strEmpId);
				if(alNextLeaveDays==null) alNextLeaveDays = new ArrayList<Integer>();
				for(int j=0; j<=(4-dtCnt); j++) {
					String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), j)+"", DBDATE, DATE_FORMAT);
					if(alEmpOnLeaveForNextDates!=null && alEmpOnLeaveForNextDates.contains(strNewDate)) {
						alNextLeaveDays.add(j);
					}
				}
				hmLeaveEmpIds.put(strEmpId, alNextLeaveDays);
			}*/
			
//			System.out.println("hmEmpTLData ===>> " + hmEmpTLData);
//			System.out.println("hmWeekOffEmpTLData ===>> " + hmWeekOffEmpTLData);
			
			
			
			/*
//			System.out.println("hmDatewiseData ===>> " + hmDatewiseData);
			Iterator<String> itt = hmDatewiseData.keySet().iterator();
			Map<String, List<String>> hmNotAddEmpInWeekOff = new LinkedHashMap<String, List<String>>();
			Map<String, Map<String, List<String>>> hmAddEmpInWeekOff = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmAssignShiftToEmp = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, String>> hmAddEmpwiseCountWeekNos = new LinkedHashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmRemoveEmpwiseCountWeekNos = new LinkedHashMap<String, Map<String, String>>();
			int dtCnt=0;
			int weekIncrement=1;
			boolean firstSunFlag = false;
			while(itt.hasNext()) {
				String strDate = itt.next();
//				System.out.println("strDate ===>> " + strDate);
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				if(dtCnt==0 && strDay.equals("SUN")) {
					firstSunFlag = true;
				}
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strPaycycleFrmDate, strDate, DATE_FORMAT);
				int intWeekNoOfTheDateInMonth = uF.parseToInt(weekNoOfTheDateInMonth);
				if(firstSunFlag && strDay.equals("SAT")) {
					intWeekNoOfTheDateInMonth += weekIncrement;
				}
				List<String> alEmpOnLeaveForDate = hmDatewiseEmpLeaveData.get(strDate);
				
				Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(strDate);
//				System.out.println("hmShiftwiseData ===>> " + hmShiftwiseData);
				Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
				while(it1.hasNext()) {
					String strShiftId = it1.next();
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
					Iterator<String> it2 = hmInner.keySet().iterator();
					while(it2.hasNext()) {
						String strEmpId = it2.next();
//						System.out.println("strEmpId ===>> " + strEmpId);
						if(alEmpOnLeaveForDate!=null && alEmpOnLeaveForDate.contains(strEmpId)) {
							List<String> innList = hmNotAddEmpInWeekOff.get(strDate);
							if(innList == null) innList = new ArrayList<String>();
							innList.add(strEmpId);
							hmNotAddEmpInWeekOff.put(strDate, innList);
							
							Map<String, String> hmWeekNoCnt = hmRemoveEmpwiseCountWeekNos.get(strEmpId);
							if(hmWeekNoCnt==null)hmWeekNoCnt = new LinkedHashMap<String, String>();
							int cnt = uF.parseToInt(hmWeekNoCnt.get(intWeekNoOfTheDateInMonth+""));
							cnt++;
							hmWeekNoCnt.put(intWeekNoOfTheDateInMonth+"", cnt+"");
							
							hmRemoveEmpwiseCountWeekNos.put(strEmpId, hmWeekNoCnt);
						}
					}
				}
				dtCnt++;
			}
			
//			System.out.println(" hmNotAddEmpInWeekOff ===>> " + hmNotAddEmpInWeekOff);
//			System.out.println(" hmAddEmpwiseCountWeekNos ===>> " + hmAddEmpwiseCountWeekNos);
			
			int tempIntWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt;
			dtCnt=0;
			firstSunFlag = false;
			Iterator<String> it = hmDatewiseData.keySet().iterator();
			while(it.hasNext()) {
				String strDate = it.next();
//				System.out.println("strDate ===>> " + strDate);
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				if(dtCnt==0 && strDay.equals("SUN")) {
					firstSunFlag = true;
				}
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strPaycycleFrmDate, strDate, DATE_FORMAT);
				int intWeekNoOfTheDateInMonth = uF.parseToInt(weekNoOfTheDateInMonth);
				if(firstSunFlag && strDay.equals("SAT")) {
					intWeekNoOfTheDateInMonth += weekIncrement;
				}
				List<String> alEmpOnLeaveForDate = hmDatewiseEmpLeaveData.get(strDate);
				
				Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(strDate);
//				System.out.println("hmShiftwiseData ===>> " + hmShiftwiseData);
				Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
				while(it1.hasNext()) {
					String strShiftId = it1.next();
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
					Iterator<String> it2 = hmInner.keySet().iterator();
					Iterator<String> it22 = hmInner.keySet().iterator();
					int tlCnt=0;
					int mGenderCnt=0;
					int tlCnt1=0;
					int mGenderCnt1=0;
					int shiftMemberCnt=0;
					
					List<String> empInnerList = new ArrayList<String>();
					while(it22.hasNext()) {
						String strEmpId = it22.next();
						if(alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strEmpId)) {
							empInnerList.add(strEmpId);
						}
					}
					if(strDate.equals("04/01/2020") || strDate.equals("11/01/2020")) {
//						System.out.println("empInnerList ===>> " + empInnerList);
					}
					
					int alEmpCnt = 0;
					while(it2.hasNext()) {
						int gCnt=0;
						int TLCnt=0;
						String strEmpId = it2.next();
						String strEmpLvl = hmEmpLevelId.get(strEmpId);
						String strEmpGender = hmEmpGender.get(strEmpId);

						if(strDate.equals("04/01/2020") || strDate.equals("11/01/2020")) {
//							System.out.println("strEmpId ===>> " + strEmpId +" --- strShiftId ===>> " + strShiftId +" --- strDate =====>> " + strDate);
						}
						Map<String, String> hmRemoveWeekNoCnt = hmRemoveEmpwiseCountWeekNos.get(strEmpId);
						if(hmRemoveWeekNoCnt==null)hmRemoveWeekNoCnt = new LinkedHashMap<String, String>();
						
						Map<String, String> hmWeekNoCnt1 = hmAddEmpwiseCountWeekNos.get(strEmpId);
						if(hmWeekNoCnt1==null)hmWeekNoCnt1 = new LinkedHashMap<String, String>();
						if(alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strEmpId)) {
							intWeekEndEmpTlGenderCnt = intWeekEndEmpCnt;
							
							if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
							}
							boolean addEmpFlag = true;
							if(empInnerList.size()>shiftMemberCnt) {
								int reqDiff = intWeekEndEmpTlGenderCnt - shiftMemberCnt;
								int remainEmp = empInnerList.size() - alEmpCnt;
								if(alTlLevels !=null && alTlLevels.contains(strEmpLvl) && uF.parseToInt(strTlCnt)==tlCnt1 && remainEmp>reqDiff) {
									addEmpFlag = false;
								}
								alEmpCnt++;
							}
						
							if(addEmpFlag && !hmRemoveWeekNoCnt.containsKey(intWeekNoOfTheDateInMonth+"") && (uF.parseToInt(intWeekNoOfTheDateInMonth+"") ==1 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==3 && hmWeekNoCnt1.containsKey(1+""))
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==3 && !hmWeekNoCnt1.containsKey(2+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==2 && !hmWeekNoCnt1.containsKey(1+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==4 && hmWeekNoCnt1.containsKey(2+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==4 && !hmWeekNoCnt1.containsKey(3+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==5 && !hmWeekNoCnt1.containsKey(4+""))) 
								&& shiftMemberCnt<intWeekEndEmpTlGenderCnt) {
								
								Map<String, List<String>> hmEmpShiftwise = hmAssignShiftToEmp.get(strDate);
								if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
								
								List<String> innList = hmEmpShiftwise.get(strShiftId);
								if(innList == null) innList = new ArrayList<String>();
								innList.add(strEmpId);
								hmEmpShiftwise.put(strShiftId, innList);
								hmAssignShiftToEmp.put(strDate, hmEmpShiftwise);
								if(strEmpGender !=null && strEmpGender.equalsIgnoreCase("M") && maleGenderCnt>mGenderCnt1) {
									mGenderCnt1++;
								}
								if(alTlLevels !=null && alTlLevels.contains(strEmpLvl) && uF.parseToInt(strTlCnt)>tlCnt1) {
									tlCnt1++;
								}
								gCnt++;
								TLCnt++;
								Map<String, String> hmWeekNoCnt = hmAddEmpwiseCountWeekNos.get(strEmpId);
								if(hmWeekNoCnt==null)hmWeekNoCnt = new LinkedHashMap<String, String>();
								int cnt = uF.parseToInt(hmWeekNoCnt.get(intWeekNoOfTheDateInMonth+""));
								cnt++;
								hmWeekNoCnt.put(intWeekNoOfTheDateInMonth+"", cnt+"");
								hmAddEmpwiseCountWeekNos.put(strEmpId, hmWeekNoCnt);
								shiftMemberCnt++;
								String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
								Map<String, String> shiftMap = hmShiftTime.get(strShiftId); 
								if(shiftMap == null) shiftMap = new HashMap<String, String>();
								
								String strShiftFrom = shiftMap.get("FROM");
								String strShiftTo = shiftMap.get("TO");
								if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
									double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
									insertUpdateRoster(con, strDate, strShiftFrom, strShiftTo, dblTimeDiff, strShiftId, strEmpId, strEmpServiceId, 0, uF);
								}
								if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
//									System.out.println("if strEmpId ===>> " + strEmpId + " --- mGenderCnt ===>> " + mGenderCnt +" --- tlCnt ===>> " + tlCnt);
								}
							} else {
								Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strDate);
								if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
								
								List<String> innList = hmEmpShiftwise.get(strShiftId);
								if(innList == null) innList = new ArrayList<String>();
								innList.add(strEmpId);
								hmEmpShiftwise.put(strShiftId, innList);
								hmAddEmpInWeekOff.put(strDate, hmEmpShiftwise);
								String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
//								insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, strDate, strEmpId, strEmpServiceId, strShiftId);
								if(gCnt==1) {
									mGenderCnt--;
								}
								if(TLCnt==1) {
									tlCnt--;
								}
								if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
//									System.out.println("else strEmpId ===>> " + strEmpId + " --- mGenderCnt ===>> " + mGenderCnt +" --- tlCnt ===>> " + tlCnt);
								}
							}
						}
					}
				}
				dtCnt++;
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	

	private void assignWeeklyOffOnBasisOfAdjustWithWorkingDays_1(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
//			sbQuery.append(" and emp_id=193 ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
//			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			List<String> alWeekDays = uF.getWeekDays();
			List<String> alWeekEnds = uF.getWeekEnds();
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_STRETCH_SHIFT", rst1.getString("no_of_days_for_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_NORMAL_WEEKOFF", rst1.getString("no_of_days_for_normal_weekoff"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strNormalDays = hmRosterPolicyRulesData.get("NO_OF_DAYS_FOR_NORMAL_WEEKOFF");
			
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			int maleGenderCnt = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"));
					
			int intWeekdaysEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlGenderCnt;
			int intWeekdaysEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT"));
			
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
//			System.out.println("intWeekdaysEmpTlGenderCnt ------------->> " + intWeekdaysEmpTlGenderCnt);
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			Map<String, List<String>> hmEmpwiseEmpLeaveData = getEmpApprovedLeaveDataEmpwise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmEmpwiseEmpLeaveData == null) hmEmpwiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmDatewiseEmpAssignedWeekoffData = getAssignedWeekOffData(con, uF, strPaycycleFrmDate, strPaycycleToDate);
			if(hmDatewiseEmpAssignedWeekoffData == null) hmDatewiseEmpAssignedWeekoffData = new HashMap<String, List<String>>();
			
			Map<String, Map<String, Map<String, String>>> hmEmpwiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			Map<String, Map<String, String>> hmEmpwiseDateShiftData = new LinkedHashMap<String, Map<String, String>>();
//			Map<String, Map<String, Map<String, String>>> hmDatewiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpGender = CF.getEmpGenderMap(con);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,emp_gender from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id " +
				" and epd.is_alive=true and joining_date<=? "); // //(121,122,125,127,128,131,133,134,136,137,140,141,145,146,147,149,152,154,157,162)
			sbQuery.append("and epd.emp_per_id in (121,122,125,127,128,131,134,137,140,141,145,146,147,149,152,157,162,163,164,165)");
//			sbQuery.append("and epd.emp_per_id in (127,133,134,137,145,146,149)");
//			sbQuery.append("and epd.emp_per_id in (125,131,147,152,154,157)");
//			sbQuery.append("and epd.emp_per_id in (121,122,128,136,140,141,162)");
			pst1 = con.prepareStatement(sbQuery.toString()); 
			pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			int monthWeekEndCnt = 0;
			List<String> alWeekEndCntDate = new ArrayList<String>();
			Map<String, String> hmEmpHolidayCnt = new HashMap<String, String>();
			while (rst1.next()) {
				String empId = rst1.getString("emp_per_id");
				String strEmpServiceId = hmEmpServiceId.get(empId) != null ? hmEmpServiceId.get(empId).substring(1, hmEmpServiceId.get(empId).length()-1) : "0";
				String strWLocationId = hmEmpWlocation.get(empId);
				List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(empId);
				if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
					empOrgId = alLastAssignedPaycycleData.get(4);
					strPaycycle = alLastAssignedPaycycleData.get(1);
					String strFrmDate = alLastAssignedPaycycleData.get(2);
					String strToDate = alLastAssignedPaycycleData.get(3);
					
					Map<String, String> hmHolidays = new HashMap<String, String>();
					Map<String, String> hmHolidayDates = new HashMap<String, String>();
					CF.getHolidayList(con,request, uF.getDateFormat(strFrmDate, DATE_FORMAT, DATE_FORMAT), uF.getDateFormat(strToDate, DATE_FORMAT, DATE_FORMAT), CF, hmHolidayDates, hmHolidays, true);
//					System.out.println("hmHolidayDates ===>> " + hmHolidayDates);
					
					Map<String, String> hmEmpCurrMonthRoster = getEmpCurrMonthRosterData(con, uF, empId, strFrmDate, strToDate, strEmpServiceId);
					for(int j=0; j<nOfdays; j++) {
						String newDate1 = getDate(strPaycycleFrmDate, DATE_FORMAT, j);
						
						if(!alWeekEndCntDate.contains(newDate1)) {
							String strDay = uF.getDateFormat(newDate1, DATE_FORMAT, "E");
							strDay = strDay.toUpperCase();
							if(alWeekEnds.contains(strDay)) {
								monthWeekEndCnt++;
								alWeekEndCntDate.add(newDate1);
							}
						}
						String newShiftId = hmEmpCurrMonthRoster.get(newDate1);
						if(newShiftId == null) {
							continue;
						}
						Map<String, Map<String, String>> hmShiftwiseData = hmEmpwiseData.get(empId);
						if(hmShiftwiseData==null) hmShiftwiseData = new LinkedHashMap<String, Map<String,String>>();
						
						Map<String, String> hmInner = hmShiftwiseData.get(newShiftId);
						if(hmInner==null)hmInner = new LinkedHashMap<String, String>();
						
						hmInner.put(newDate1, newDate1);
						hmShiftwiseData.put(newShiftId, hmInner);
						hmEmpwiseData.put(empId, hmShiftwiseData);
						
						
						Map<String, String> hmDatewiseShiftData = hmEmpwiseDateShiftData.get(empId);
						if(hmDatewiseShiftData==null) hmDatewiseShiftData = new LinkedHashMap<String, String>();
						hmDatewiseShiftData.put(newDate1, newShiftId);
						hmEmpwiseDateShiftData.put(empId, hmDatewiseShiftData);
						
						
						String strColour = (String)hmHolidayDates.get(uF.getDateFormat(newDate1, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
                    	if(strColour!=null) {
                    		int holidayCnt = uF.parseToInt(hmEmpHolidayCnt.get(empId));
                    		holidayCnt++;
                    		hmEmpHolidayCnt.put(empId, holidayCnt+"");
                    	}
                    	
					}
				}
			}
			rst1.close();
			pst1.close();
			
//			System.out.println("hmEmpwiseData ===>> " + hmEmpwiseData);
//			System.out.println("hmEmpHolidayCnt ===>> " + hmEmpHolidayCnt); 
//			System.out.println("nOfdays ===>> " + nOfdays + " --- monthWeekEndCnt ===>> " + monthWeekEndCnt);
			
			
			int actualMonthWorkDays = nOfdays - monthWeekEndCnt;
			Map<String, Map<String, List<String>>> hmAddEmpInWeekOff = new LinkedHashMap<String, Map<String, List<String>>>();
			
			Map<String, String> hmAdjustDays = new HashMap<String, String>();
			Map<String, String> hmEmpwisePrevDate1 = new HashMap<String, String>();
			Map<String, List<String>> hmRequiredEmpTlList = new HashMap<String, List<String>>();
			Map<String, List<String>> hmRequiredEmpNonTlList = new HashMap<String, List<String>>();
			Map<String, List<String>> hmDatewiseNewEmpForWeekOff = new HashMap<String, List<String>>();
			Iterator<String> it = hmEmpwiseDateShiftData.keySet().iterator();
			Map<String, String> hmTlEmpCnt = new HashMap<String, String>();
			while(it.hasNext()) {
				String strEmpId = it.next();
				String strEmpLvl = hmEmpLevelId.get(strEmpId);
				int actualMonthWorkingDays = actualMonthWorkDays - uF.parseToInt(hmEmpHolidayCnt.get(strEmpId));
				String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
				List<String> alEmpOnLeaveForDate = hmEmpwiseEmpLeaveData.get(strEmpId);
				List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(strEmpId);
				if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
					empOrgId = alLastAssignedPaycycleData.get(4);
					strPaycycle = alLastAssignedPaycycleData.get(1);
					String strFrmDate = alLastAssignedPaycycleData.get(2);
					String strToDate = alLastAssignedPaycycleData.get(3);
					
					Map<String, List<String>> hmDatewiseShiftwiseTlData = getDatewiseShiftwiseTlData(con, uF, strFrmDate, strToDate, alTlLevels, hmEmpLevelId);
					Map<String, String> hmDatewiseGeneralShiftEmpCnt = getDatewiseGeneralShiftEmpCntData(con, uF, strFrmDate, strToDate, strRemainingEmpShift, alTlLevels, hmEmpLevelId);
					
					int dayCount = getEmpCurrMonthRosterDayCnt(con, uF, strEmpId, strFrmDate, strToDate, strEmpServiceId);
					if(uF.parseToInt(strEmpId) == 127) {
//						System.out.println(strEmpId + " --- actualMonthWorkingDays ===>> " + actualMonthWorkingDays + " --- dayCount =====>> " + dayCount +" --- hmDatewiseGeneralShiftEmpCnt ===>> " + hmDatewiseGeneralShiftEmpCnt);
					}
	//				List<String> alEmpAssignedWeekOffForDate = hmDatewiseEmpAssignedWeekoffData.get(strDate);
					if(dayCount>actualMonthWorkingDays) {
						int requireWeekOffCnt = 0;
						int requireWeekOffDiff = dayCount - actualMonthWorkingDays;
						Map<String, String> hmDatewiseShiftData = hmEmpwiseDateShiftData.get(strEmpId);
						Iterator<String> it2 = hmDatewiseShiftData.keySet().iterator();
							while(it2.hasNext()) {
								String strDate = it2.next();
								String strShiftId = hmDatewiseShiftData.get(strDate);
								String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
								strDay = strDay.toUpperCase();
								List<String> alEmpAssignedWeekOffForDate = hmDatewiseEmpAssignedWeekoffData.get(strDate);
								if((alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strDate)) && (alEmpAssignedWeekOffForDate==null || !alEmpAssignedWeekOffForDate.contains(strEmpId))) {
		//							
									String strPrevDate = hmEmpwisePrevDate1.get(strEmpId);
									int daysDiff=0; 
									if(strPrevDate !=null) {
										daysDiff = uF.parseToInt(uF.dateDifference(strPrevDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
									}
									if(daysDiff==0 || daysDiff==2) {
										int stretchDayCnt = uF.parseToInt(hmAdjustDays.get(strEmpId));
										stretchDayCnt++;
										hmAdjustDays.put(strEmpId, stretchDayCnt+"");
									} else {
										hmAdjustDays.put(strEmpId, "1");
									}
									hmEmpwisePrevDate1.put(strEmpId, strDate);
									
									int shiftDayCnt=0;
									for(int j=1; j<8; j++) {
										String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), j)+"", DBDATE, DATE_FORMAT);
										int dayCnt = getEmpShiftAssignOrNotForDate(con, uF, strEmpId, strNewDate, strEmpServiceId);
										if(dayCnt==0) {
											break;
										}
										shiftDayCnt = shiftDayCnt + dayCnt;
										if(dayCnt==0) {
											break;
										}
									}
									
									if((strDate.equals("09/01/2020"))) { // && (uF.parseToInt(strEmpId) == 128 || uF.parseToInt(strEmpId) == 121)
//										System.out.println(strEmpId + " --- " + strDate + " === hmAdjustDays ===>> " + hmAdjustDays +" -- shiftDayCnt ===>> " + shiftDayCnt +" -- requireWeekOffDiff ===>> " + requireWeekOffDiff +" -- requireWeekOffCnt ===>> " + requireWeekOffCnt);
									}
									int startDaysDiff = uF.parseToInt(uF.dateDifference(strFrmDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
									String strTlCntForDate = hmDatewiseGeneralShiftEmpCnt.get(strDate+"_TL");
//									System.out.println(strDate + " --------- startDaysDiff ===>> " + startDaysDiff);
									if(startDaysDiff==1 && uF.parseToInt(hmAdjustDays.get(strEmpId)) == 1 && shiftDayCnt==0) {
//										&& (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) || 
//										(startDaysDiff==2 && uF.parseToInt(hmAdjustDays.get(strEmpId)) == 2 && shiftDayCnt==8 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
//										(startDaysDiff==2 && uF.parseToInt(hmAdjustDays.get(strEmpId)) == 2 && shiftDayCnt==7 && requireWeekOffCnt%2 == 1) || 
//										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 3 && shiftDayCnt==8 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
//										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 3 && shiftDayCnt==7 && requireWeekOffCnt%2 == 1) || 
//										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 4 && shiftDayCnt==7 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
//										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 4 && shiftDayCnt==6 && requireWeekOffCnt%2 == 1) || 
//										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 5 && shiftDayCnt==6 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
//										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 5 && shiftDayCnt==5 && requireWeekOffCnt%2 == 1)
										
										if(uF.parseToInt(strEmpId) == 127) {
//											System.out.println(strEmpId + " --- alTlLevels.contains(strEmpLvl) ===>> " + alTlLevels.contains(strEmpLvl) +" --- uF.parseToInt(hmTlEmpCnt.get(strDate)) ===>> " + uF.parseToInt(hmTlEmpCnt.get(strDate)) +" --- strTlCntForDate ===>> " + strTlCntForDate);
										}
										List<String> innerLST = hmDatewiseShiftwiseTlData.get(strDate+"_"+strShiftId);
										if(innerLST == null) innerLST = new ArrayList<String>();
										
										List<String> innerLIST = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
										if(innerLIST == null) innerLIST = new ArrayList<String>();
										
										List<String> innerLISTNonTl = hmRequiredEmpNonTlList.get(strDate+"_"+strShiftId);
										if(innerLISTNonTl == null) innerLISTNonTl = new ArrayList<String>();
										
//										uF.parseToInt(strShiftId)!=uF.parseToInt(strRemainingEmpShift) && 
										if(requireWeekOffCnt < requireWeekOffDiff && 
											(((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && !alTlLevels.contains(strEmpLvl)) 
											|| ((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && alTlLevels.contains(strEmpLvl) 
											&& (uF.parseToInt(hmTlEmpCnt.get(strDate))< uF.parseToInt(strTlCntForDate) || (innerLST.size()> innerLISTNonTl.size())) ) ) ) {

											if(alTlLevels.contains(strEmpLvl)) {
												int tlCnt = uF.parseToInt(hmTlEmpCnt.get(strDate));
												tlCnt++;
												hmTlEmpCnt.put(strDate, tlCnt+"");
											}
											if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1)> innerLISTNonTl.size()) {
												innerLISTNonTl.add(strEmpId);
												hmRequiredEmpNonTlList.put(strDate+"_"+strShiftId, innerLISTNonTl);
											} else if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1) == innerLISTNonTl.size()) {
												innerLIST.add(strEmpId);
												hmRequiredEmpTlList.put(strDate+"_"+strShiftId, innerLIST);
											}
											
											Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
											if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
											List<String> innList = hmEmpShiftwise.get(strShiftId);
											if(innList == null) innList = new ArrayList<String>();
											innList.add(strDate);
											hmEmpShiftwise.put(strShiftId, innList);
											hmAddEmpInWeekOff.put(strEmpId, hmEmpShiftwise);
											
											requireWeekOffCnt++;
											
											List<String> innLst = hmDatewiseNewEmpForWeekOff.get(strDate);
											if(innLst == null) innLst = new ArrayList<String>();
											innLst.add(strEmpId);
											hmDatewiseNewEmpForWeekOff.put(strDate, innLst);
										}
									} else if((uF.parseToInt(hmAdjustDays.get(strEmpId)) == 3 && shiftDayCnt==4 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 4 && shiftDayCnt==3 && requireWeekOffCnt%2 == 1)) {
										
										List<String> innerLST = hmDatewiseShiftwiseTlData.get(strDate+"_"+strShiftId);
										if(innerLST == null) innerLST = new ArrayList<String>();
										
										List<String> innerLIST = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
										if(innerLIST == null) innerLIST = new ArrayList<String>();
										
										List<String> innerLISTNonTl = hmRequiredEmpNonTlList.get(strDate+"_"+strShiftId);
										if(innerLISTNonTl == null) innerLISTNonTl = new ArrayList<String>();
										
//										System.out.println(strEmpId + " --- strDate ===>> " + strDate);
										if(requireWeekOffCnt < requireWeekOffDiff && 
											(((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && !alTlLevels.contains(strEmpLvl)) 
											|| ((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && alTlLevels.contains(strEmpLvl) 
											&& (uF.parseToInt(hmTlEmpCnt.get(strDate))< uF.parseToInt(strTlCntForDate) || (innerLST.size()> innerLISTNonTl.size())) ) ) ) {
											
											if(alTlLevels.contains(strEmpLvl)) {
												int tlCnt = uF.parseToInt(hmTlEmpCnt.get(strDate));
												tlCnt++;
												hmTlEmpCnt.put(strDate, tlCnt+"");
											}
											if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1)> innerLISTNonTl.size()) {
												innerLISTNonTl.add(strEmpId);
												hmRequiredEmpNonTlList.put(strDate+"_"+strShiftId, innerLISTNonTl);
											} else if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1) == innerLISTNonTl.size()) {
												innerLIST.add(strEmpId);
												hmRequiredEmpTlList.put(strDate+"_"+strShiftId, innerLIST);
											}
											
											Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
											if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
											List<String> innList = hmEmpShiftwise.get(strShiftId);
											if(innList == null) innList = new ArrayList<String>();
											innList.add(strDate);
											hmEmpShiftwise.put(strShiftId, innList);
											hmAddEmpInWeekOff.put(strEmpId, hmEmpShiftwise);
											
											requireWeekOffCnt++;
											
											List<String> innLst = hmDatewiseNewEmpForWeekOff.get(strDate);
											if(innLst == null) innLst = new ArrayList<String>();
											innLst.add(strEmpId);
											hmDatewiseNewEmpForWeekOff.put(strDate, innLst);
										}
									} else if((uF.parseToInt(hmAdjustDays.get(strEmpId)) == 4 && shiftDayCnt==3 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 5 && shiftDayCnt==2 && requireWeekOffCnt%2 == 1)) {
										
										List<String> innerLST = hmDatewiseShiftwiseTlData.get(strDate+"_"+strShiftId);
										if(innerLST == null) innerLST = new ArrayList<String>();
										
										List<String> innerLIST = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
										if(innerLIST == null) innerLIST = new ArrayList<String>();
										
										List<String> innerLISTNonTl = hmRequiredEmpNonTlList.get(strDate+"_"+strShiftId);
										if(innerLISTNonTl == null) innerLISTNonTl = new ArrayList<String>();
										
//										System.out.println(strEmpId + " --- strDate ===>> " + strDate);
										if(requireWeekOffCnt < requireWeekOffDiff && 
											(((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && !alTlLevels.contains(strEmpLvl)) 
											|| ((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && alTlLevels.contains(strEmpLvl) 
											&& (uF.parseToInt(hmTlEmpCnt.get(strDate))< uF.parseToInt(strTlCntForDate) || (innerLST.size()> innerLISTNonTl.size())) ) ) ) {
											
											if(alTlLevels.contains(strEmpLvl)) {
												int tlCnt = uF.parseToInt(hmTlEmpCnt.get(strDate));
												tlCnt++;
												hmTlEmpCnt.put(strDate, tlCnt+"");
											}
											if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1)> innerLISTNonTl.size()) {
												innerLISTNonTl.add(strEmpId);
												hmRequiredEmpNonTlList.put(strDate+"_"+strShiftId, innerLISTNonTl);
											} else if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1) == innerLISTNonTl.size()) {
												innerLIST.add(strEmpId);
												hmRequiredEmpTlList.put(strDate+"_"+strShiftId, innerLIST);
											}
											
											Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
											if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
											List<String> innList = hmEmpShiftwise.get(strShiftId);
											if(innList == null) innList = new ArrayList<String>();
											innList.add(strDate);
											hmEmpShiftwise.put(strShiftId, innList);
											hmAddEmpInWeekOff.put(strEmpId, hmEmpShiftwise);
											
											requireWeekOffCnt++;
											
											List<String> innLst = hmDatewiseNewEmpForWeekOff.get(strDate);
											if(innLst == null) innLst = new ArrayList<String>();
											innLst.add(strEmpId);
											hmDatewiseNewEmpForWeekOff.put(strDate, innLst);
										}
									} else if((uF.parseToInt(hmAdjustDays.get(strEmpId)) == 5 && shiftDayCnt==2 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 6 && shiftDayCnt==1 && requireWeekOffCnt%2 == 1)) {
										
										List<String> innerLST = hmDatewiseShiftwiseTlData.get(strDate+"_"+strShiftId);
										if(innerLST == null) innerLST = new ArrayList<String>();
										
										List<String> innerLIST = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
										if(innerLIST == null) innerLIST = new ArrayList<String>();
										
										List<String> innerLISTNonTl = hmRequiredEmpNonTlList.get(strDate+"_"+strShiftId);
										if(innerLISTNonTl == null) innerLISTNonTl = new ArrayList<String>();
										
//										System.out.println(strEmpId + " --- strDate ===>> " + strDate);
										if(requireWeekOffCnt < requireWeekOffDiff && 
											(((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && !alTlLevels.contains(strEmpLvl)) 
											|| ((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && alTlLevels.contains(strEmpLvl) 
											&& (uF.parseToInt(hmTlEmpCnt.get(strDate))< uF.parseToInt(strTlCntForDate) || (innerLST.size()> innerLISTNonTl.size())) ) ) ) {
											
											if(alTlLevels.contains(strEmpLvl)) {
												int tlCnt = uF.parseToInt(hmTlEmpCnt.get(strDate));
												tlCnt++;
												hmTlEmpCnt.put(strDate, tlCnt+"");
											}
											if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1)> innerLISTNonTl.size()) {
												innerLISTNonTl.add(strEmpId);
												hmRequiredEmpNonTlList.put(strDate+"_"+strShiftId, innerLISTNonTl);
											} else if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1) == innerLISTNonTl.size()) {
												innerLIST.add(strEmpId);
												hmRequiredEmpTlList.put(strDate+"_"+strShiftId, innerLIST);
											}
											
											Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
											if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
											List<String> innList = hmEmpShiftwise.get(strShiftId);
											if(innList == null) innList = new ArrayList<String>();
											innList.add(strDate);
											hmEmpShiftwise.put(strShiftId, innList);
											hmAddEmpInWeekOff.put(strEmpId, hmEmpShiftwise);
											
											requireWeekOffCnt++;
											
											List<String> innLst = hmDatewiseNewEmpForWeekOff.get(strDate);
											if(innLst == null) innLst = new ArrayList<String>();
											innLst.add(strEmpId);
											hmDatewiseNewEmpForWeekOff.put(strDate, innLst);
										}
									} 
									
								}
							}
					}
				}
				
			}
			
//			System.out.println(" hmAddEmpInWeekOff ===>> " + hmAddEmpInWeekOff);
			
			
			
			
			Iterator<String> iit = hmAddEmpInWeekOff.keySet().iterator();
			while(iit.hasNext()) {
				String strEmpId = (String) iit.next();
				String strEmpLvl = hmEmpLevelId.get(strEmpId);
				boolean isTlRequired = false;
//				if(alTlLevels.contains(strEmpLvl)) {
//					isTlRequired = true;
//				}
				String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
				Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
				if(hmEmpShiftwise==null) hmEmpShiftwise = new HashMap<String, List<String>>();
				
				Iterator<String> iit1 = hmEmpShiftwise.keySet().iterator();
				while (iit1.hasNext()) {
					String strShiftId = (String) iit1.next();
					List<String> innList = hmEmpShiftwise.get(strShiftId);
					if(innList == null) innList = new ArrayList<String>();
//					System.out.println("strDate ===>> " + strDate + " --- innList ===>> " + innList); 

					for(int i=0; i<innList.size(); i++) {
						String strDate = innList.get(i);
						List<String> innnList = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
						if(innnList !=null && innnList.contains(strEmpId)) {
							isTlRequired = true;
						}
						
						String streNewEmpId = getNewMemberForThisDateAndShift(con, uF, strDate, strRemainingEmpShift, isTlRequired, alTlLevels, hmEmpLevelId);
						if(uF.parseToInt(streNewEmpId)>0) {
							String strNewEmpServiceId = hmEmpServiceId.get(streNewEmpId) != null ? hmEmpServiceId.get(streNewEmpId).substring(1, hmEmpServiceId.get(streNewEmpId).length()-1) : "0";
							Map<String, String> shiftMap = hmShiftTime.get(strShiftId); 
							if(shiftMap == null) shiftMap = new HashMap<String, String>();
							String strShiftFrom = shiftMap.get("FROM");
							String strShiftTo = shiftMap.get("TO"); 
							if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
								double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
								insertUpdateRoster(con, strDate, strShiftFrom, strShiftTo, dblTimeDiff, strShiftId, streNewEmpId, strNewEmpServiceId, 0, uF);
								
								insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, strDate, strEmpId, strEmpServiceId, strShiftId);
							} 
						}
					}
				}
			}
			
//			System.out.println(" hmAddEmpInWeekOff1 ===>> " + hmAddEmpInWeekOff1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private void assignWeeklyOffOnBasisOfAdjustWithWorkingDays(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
//			sbQuery.append(" and emp_id=193 ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
//			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			List<String> alWeekDays = uF.getWeekDays();
			List<String> alWeekEnds = uF.getWeekEnds();
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_STRETCH_SHIFT", rst1.getString("no_of_days_for_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_NORMAL_WEEKOFF", rst1.getString("no_of_days_for_normal_weekoff"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strNormalDays = hmRosterPolicyRulesData.get("NO_OF_DAYS_FOR_NORMAL_WEEKOFF");
			
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			int maleGenderCnt = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"));
					
			int intWeekdaysEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlGenderCnt;
			int intWeekdaysEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT"));
			
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
//			System.out.println("intWeekdaysEmpTlGenderCnt ------------->> " + intWeekdaysEmpTlGenderCnt);
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			Map<String, List<String>> hmEmpwiseEmpLeaveData = getEmpApprovedLeaveDataEmpwise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmEmpwiseEmpLeaveData == null) hmEmpwiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmDatewiseEmpAssignedWeekoffData = getAssignedWeekOffData(con, uF, strPaycycleFrmDate, strPaycycleToDate);
			if(hmDatewiseEmpAssignedWeekoffData == null) hmDatewiseEmpAssignedWeekoffData = new HashMap<String, List<String>>();
			
			Map<String, Map<String, Map<String, String>>> hmEmpwiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
//			Map<String, Map<String, Map<String, String>>> hmDatewiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpGender = CF.getEmpGenderMap(con);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,emp_gender from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id " +
				" and epd.is_alive=true and joining_date<=? "); // //(121,122,125,127,128,131,133,134,136,137,140,141,145,146,147,149,152,154,157,162)
				sbQuery.append("and epd.emp_per_id in (121,122,125,127,128,131,134,137,140,141,145,146,147,149,152,157,162,163,164,165)");
//			sbQuery.append("and epd.emp_per_id in (127,133,134,137,145,146,149)");
//				sbQuery.append("and epd.emp_per_id in (125,131,147,152,154,157)");
//				sbQuery.append("and epd.emp_per_id in (121,122,128,136,140,141,162)");
			pst1 = con.prepareStatement(sbQuery.toString()); 
			pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//						System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			int monthWeekEndCnt = 0;
			List<String> alWeekEndCntDate = new ArrayList<String>();
			Map<String, String> hmEmpHolidayCnt = new HashMap<String, String>();
			while (rst1.next()) {
				String empId = rst1.getString("emp_per_id");
				String strEmpServiceId = hmEmpServiceId.get(empId) != null ? hmEmpServiceId.get(empId).substring(1, hmEmpServiceId.get(empId).length()-1) : "0";
				String strWLocationId = hmEmpWlocation.get(empId);
				List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(empId);
				if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
					empOrgId = alLastAssignedPaycycleData.get(4);
					strPaycycle = alLastAssignedPaycycleData.get(1);
					String strFrmDate = alLastAssignedPaycycleData.get(2);
					String strToDate = alLastAssignedPaycycleData.get(3);
					
					Map<String, String> hmHolidays = new HashMap<String, String>();
					Map<String, String> hmHolidayDates = new HashMap<String, String>();
					CF.getHolidayList(con,request, uF.getDateFormat(strFrmDate, DATE_FORMAT, DATE_FORMAT), uF.getDateFormat(strToDate, DATE_FORMAT, DATE_FORMAT), CF, hmHolidayDates, hmHolidays, true);
//					System.out.println("hmHolidayDates ===>> " + hmHolidayDates);
					
					Map<String, String> hmEmpCurrMonthRoster = getEmpCurrMonthRosterData(con, uF, empId, strFrmDate, strToDate, strEmpServiceId);
					
					for(int j=0; j<nOfdays; j++) {
						String newDate1 = getDate(strPaycycleFrmDate, DATE_FORMAT, j);
						
						if(!alWeekEndCntDate.contains(newDate1)) {
							String strDay = uF.getDateFormat(newDate1, DATE_FORMAT, "E");
							strDay = strDay.toUpperCase();
							if(alWeekEnds.contains(strDay)) {
								monthWeekEndCnt++;
								alWeekEndCntDate.add(newDate1);
							}
						}
						String newShiftId = hmEmpCurrMonthRoster.get(newDate1);
						if(newShiftId == null) {
							continue;
						}
						Map<String, Map<String, String>> hmShiftwiseData = hmEmpwiseData.get(empId);
						if(hmShiftwiseData==null) hmShiftwiseData = new LinkedHashMap<String, Map<String,String>>();
						
						Map<String, String> hmInner = hmShiftwiseData.get(newShiftId);
						if(hmInner==null)hmInner = new LinkedHashMap<String, String>();
						
						hmInner.put(newDate1, newDate1);
						hmShiftwiseData.put(newShiftId, hmInner);
						hmEmpwiseData.put(empId, hmShiftwiseData);
						
						String strColour = (String)hmHolidayDates.get(uF.getDateFormat(newDate1, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
                    	if(strColour!=null) {
                    		int holidayCnt = uF.parseToInt(hmEmpHolidayCnt.get(empId));
                    		holidayCnt++;
                    		hmEmpHolidayCnt.put(empId, holidayCnt+"");
                    	}
                    	
					}
				}
			}
			rst1.close();
			pst1.close();
			
//			System.out.println("hmEmpwiseData ===>> " + hmEmpwiseData);
//			System.out.println("hmEmpHolidayCnt ===>> " + hmEmpHolidayCnt); 
//			System.out.println("nOfdays ===>> " + nOfdays + " --- monthWeekEndCnt ===>> " + monthWeekEndCnt);
			
			
			int actualMonthWorkDays = nOfdays - monthWeekEndCnt;
			Iterator<String> itt = hmEmpwiseData.keySet().iterator();
			Map<String, Map<String, List<String>>> hmAddEmpInWeekOff = new LinkedHashMap<String, Map<String, List<String>>>();
//			Map<String, Map<String, Map<String, String>>> hmAddEmpInShiftTransWeekOff = new LinkedHashMap<String, Map<String, Map<String, String>>>();
//			Map<String, Map<String, List<String>>> hmAssignShiftToEmp = new LinkedHashMap<String, Map<String, List<String>>>();
			
			Map<String, String> hmAdjustDays = new HashMap<String, String>();
			Map<String, String> hmEmpwisePrevDate1 = new LinkedHashMap<String, String>();
			Map<String, List<String>> hmRequiredEmpTlList = new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> hmRequiredEmpNonTlList = new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> hmDatewiseNewEmpForWeekOff = new LinkedHashMap<String, List<String>>();
			Iterator<String> it = hmEmpwiseData.keySet().iterator();
			Map<String, String> hmTlEmpCnt = new LinkedHashMap<String, String>();
			while(it.hasNext()) {
				String strEmpId = it.next();
				String strEmpLvl = hmEmpLevelId.get(strEmpId);
				int actualMonthWorkingDays = actualMonthWorkDays - uF.parseToInt(hmEmpHolidayCnt.get(strEmpId));
				String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
				List<String> alEmpOnLeaveForDate = hmEmpwiseEmpLeaveData.get(strEmpId);
				List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(strEmpId);
				if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
					empOrgId = alLastAssignedPaycycleData.get(4);
					strPaycycle = alLastAssignedPaycycleData.get(1);
					String strFrmDate = alLastAssignedPaycycleData.get(2);
					String strToDate = alLastAssignedPaycycleData.get(3);
					
					Map<String, List<String>> hmDatewiseShiftwiseTlData = getDatewiseShiftwiseTlData(con, uF, strFrmDate, strToDate, alTlLevels, hmEmpLevelId);
					Map<String, String> hmDatewiseGeneralShiftEmpCnt = getDatewiseGeneralShiftEmpCntData(con, uF, strFrmDate, strToDate, strRemainingEmpShift, alTlLevels, hmEmpLevelId);
					
					int dayCount = getEmpCurrMonthRosterDayCnt(con, uF, strEmpId, strFrmDate, strToDate, strEmpServiceId);
					if(uF.parseToInt(strEmpId) == 127) {
//						System.out.println(strEmpId + " --- actualMonthWorkingDays ===>> " + actualMonthWorkingDays + " --- dayCount =====>> " + dayCount +" --- hmDatewiseGeneralShiftEmpCnt ===>> " + hmDatewiseGeneralShiftEmpCnt);
					}
	//				List<String> alEmpAssignedWeekOffForDate = hmDatewiseEmpAssignedWeekoffData.get(strDate);
					if(dayCount>actualMonthWorkingDays) {
						int requireWeekOffCnt = 0;
						int continueWeekOffCnt = 0;
						String strPrevDt = null;
						int requireWeekOffDiff = dayCount - actualMonthWorkingDays;
						Map<String, Map<String, String>> hmShiftwiseData = hmEmpwiseData.get(strEmpId);
						Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
						while(it1.hasNext()) {
							String strShiftId = it1.next();
							Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
							Iterator<String> it2 = hmInner.keySet().iterator();
							
							while(it2.hasNext()) {
								String strDate = it2.next();
								String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
								strDay = strDay.toUpperCase();
								List<String> alEmpAssignedWeekOffForDate = hmDatewiseEmpAssignedWeekoffData.get(strDate);
								if((alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strDate)) && (alEmpAssignedWeekOffForDate==null || !alEmpAssignedWeekOffForDate.contains(strEmpId))) {
		//							
									String strPrevDate = hmEmpwisePrevDate1.get(strEmpId);
									int daysDiff=0; 
									if(strPrevDate !=null) {
										daysDiff = uF.parseToInt(uF.dateDifference(strPrevDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
									}
									if(daysDiff==0 || daysDiff==2) {
										int stretchDayCnt = uF.parseToInt(hmAdjustDays.get(strEmpId));
										stretchDayCnt++;
										hmAdjustDays.put(strEmpId, stretchDayCnt+"");
									} else {
										hmAdjustDays.put(strEmpId, "1");
									}
									hmEmpwisePrevDate1.put(strEmpId, strDate);
									
									int shiftDayCnt=0;
									for(int j=1; j<15; j++) {
										String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), j)+"", DBDATE, DATE_FORMAT);
										int dayCnt = getEmpShiftAssignOrNotForDate(con, uF, strEmpId, strNewDate, strEmpServiceId);
										if(dayCnt==0) {
											break;
										}
										shiftDayCnt = shiftDayCnt + dayCnt;
										if(dayCnt==0) {
											break;
										}
									}
									
									if(uF.parseToInt(strEmpId) == 127) {
//										System.out.println(strDate + " === hmAdjustDays ===>> " + hmAdjustDays +" -- shiftDayCnt ===>> " + shiftDayCnt +" -- requireWeekOffDiff ===>> " + requireWeekOffDiff +" -- requireWeekOffCnt ===>> " + requireWeekOffCnt);
									}
									int startDaysDiff = uF.parseToInt(uF.dateDifference(strFrmDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
									String strTlCntForDate = hmDatewiseGeneralShiftEmpCnt.get(strDate+"_TL");
//									System.out.println(strDate + " --------- startDaysDiff ===>> " + startDaysDiff);
									if((startDaysDiff==1 && uF.parseToInt(hmAdjustDays.get(strEmpId)) == 1 && shiftDayCnt==8 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) || 
										(startDaysDiff==2 && uF.parseToInt(hmAdjustDays.get(strEmpId)) == 2 && shiftDayCnt==8 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(startDaysDiff==2 && uF.parseToInt(hmAdjustDays.get(strEmpId)) == 2 && shiftDayCnt==7 && requireWeekOffCnt%2 == 1) || 
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 3 && shiftDayCnt==8 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 3 && shiftDayCnt==7 && requireWeekOffCnt%2 == 1) || 
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 4 && shiftDayCnt==7 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 4 && shiftDayCnt==6 && requireWeekOffCnt%2 == 1) || 
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 5 && shiftDayCnt==6 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 5 && shiftDayCnt==5 && requireWeekOffCnt%2 == 1)) {
//										System.out.println("hmDatewiseGeneralShiftEmpCnt ===>> " + hmDatewiseGeneralShiftEmpCnt);
//										System.out.println("hmDatewiseNewEmpForWeekOff ===>> " + hmDatewiseNewEmpForWeekOff + " --- hmDatewiseNewEmpForWeekOff.get(strDate) ===>> " + hmDatewiseNewEmpForWeekOff.get(strDate));
										if(uF.parseToInt(strEmpId) == 127) {
//											System.out.println(strEmpId + " --- alTlLevels.contains(strEmpLvl) ===>> " + alTlLevels.contains(strEmpLvl) +" --- uF.parseToInt(hmTlEmpCnt.get(strDate)) ===>> " + uF.parseToInt(hmTlEmpCnt.get(strDate)) +" --- strTlCntForDate ===>> " + strTlCntForDate);
										}
										List<String> innerLST = hmDatewiseShiftwiseTlData.get(strDate+"_"+strShiftId);
										if(innerLST == null) innerLST = new ArrayList<String>();
										
										List<String> innerLIST = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
										if(innerLIST == null) innerLIST = new ArrayList<String>();
										
										List<String> innerLISTNonTl = hmRequiredEmpNonTlList.get(strDate+"_"+strShiftId);
										if(innerLISTNonTl == null) innerLISTNonTl = new ArrayList<String>();
										
										if(uF.parseToInt(strShiftId)!=uF.parseToInt(strRemainingEmpShift) && requireWeekOffCnt < requireWeekOffDiff && 
											(((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && !alTlLevels.contains(strEmpLvl)) 
											|| ((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && alTlLevels.contains(strEmpLvl) 
											&& (uF.parseToInt(hmTlEmpCnt.get(strDate))< uF.parseToInt(strTlCntForDate) || (innerLST.size()> innerLISTNonTl.size())) ) ) ) {
											/*String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), 1)+"", DBDATE, DATE_FORMAT);
											Map<String, Map<String, String>> hmDateShiftwiseData = hmDatewiseData.get(strDate);
											if(hmDateShiftwiseData == null) hmDateShiftwiseData = new HashMap<String, Map<String, String>>();
											
											Map<String, String> hmDateInner = hmDateShiftwiseData.get(strRemainingEmpShift);
											if(hmDateInner==null) hmDateInner = new HashMap<String, String>();*/
											if(alTlLevels.contains(strEmpLvl)) {
												int tlCnt = uF.parseToInt(hmTlEmpCnt.get(strDate));
												tlCnt++;
												hmTlEmpCnt.put(strDate, tlCnt+"");
											}
											if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1)> innerLISTNonTl.size()) {
												innerLISTNonTl.add(strEmpId);
												hmRequiredEmpNonTlList.put(strDate+"_"+strShiftId, innerLISTNonTl);
											} else if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1) == innerLISTNonTl.size()) {
												innerLIST.add(strEmpId);
												hmRequiredEmpTlList.put(strDate+"_"+strShiftId, innerLIST);
											}
											
											Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
											if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
											List<String> innList = hmEmpShiftwise.get(strShiftId);
											if(innList == null) innList = new ArrayList<String>();
											innList.add(strDate);
											hmEmpShiftwise.put(strShiftId, innList);
											hmAddEmpInWeekOff.put(strEmpId, hmEmpShiftwise);
											
											requireWeekOffCnt++;
											int dayDiff=0; 
											if(strPrevDt !=null) {
												dayDiff = uF.parseToInt(uF.dateDifference(strPrevDt, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
											}
											if(dayDiff==0 || dayDiff==2) {
												continueWeekOffCnt++;
											} else {
												continueWeekOffCnt=0;
											}
											
											strPrevDt = strDate;
											List<String> innLst = hmDatewiseNewEmpForWeekOff.get(strDate);
											if(innLst == null) innLst = new ArrayList<String>();
											innLst.add(strEmpId);
											hmDatewiseNewEmpForWeekOff.put(strDate, innLst);
										}
									} else if((uF.parseToInt(hmAdjustDays.get(strEmpId)) == 6 && shiftDayCnt==4 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 6 && shiftDayCnt==4 && requireWeekOffCnt%2 == 1) || 
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 7 && shiftDayCnt==4 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 7 && shiftDayCnt==3 && requireWeekOffCnt%2 == 1) || 
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 8 && shiftDayCnt==3 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 8 && shiftDayCnt==2 && requireWeekOffCnt%2 == 1)) {
										
										List<String> innerLST = hmDatewiseShiftwiseTlData.get(strDate+"_"+strShiftId);
										if(innerLST == null) innerLST = new ArrayList<String>();
										
										List<String> innerLIST = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
										if(innerLIST == null) innerLIST = new ArrayList<String>();
										
										List<String> innerLISTNonTl = hmRequiredEmpNonTlList.get(strDate+"_"+strShiftId);
										if(innerLISTNonTl == null) innerLISTNonTl = new ArrayList<String>();
										
										boolean flag = false;
										for(int j=1; j<=uF.parseToInt(hmAdjustDays.get(strEmpId)); j++) {
											String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), -(j+continueWeekOffCnt))+"", DBDATE, DATE_FORMAT);
											List<String> innList = hmDatewiseNewEmpForWeekOff.get(strNewDate);
											if(innList!=null && innList.contains(strEmpId)) {
												flag = true;
											}
										}
										
//										if(!flag && uF.parseToInt(strShiftId)!=uF.parseToInt(strRemainingEmpShift) && requireWeekOffCnt < requireWeekOffDiff && (hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate))) {
										if(!flag && uF.parseToInt(strShiftId)!=uF.parseToInt(strRemainingEmpShift) && requireWeekOffCnt < requireWeekOffDiff && 
											(((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && !alTlLevels.contains(strEmpLvl)) 
											|| ((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && alTlLevels.contains(strEmpLvl) 
											&& (uF.parseToInt(hmTlEmpCnt.get(strDate))< uF.parseToInt(strTlCntForDate) || (innerLST.size()> innerLISTNonTl.size())) ) ) ) {
											
											if(alTlLevels.contains(strEmpLvl)) {
												int tlCnt = uF.parseToInt(hmTlEmpCnt.get(strDate));
												tlCnt++;
												hmTlEmpCnt.put(strDate, tlCnt+"");
											}
											if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1)> innerLISTNonTl.size()) {
												innerLISTNonTl.add(strEmpId);
												hmRequiredEmpNonTlList.put(strDate+"_"+strShiftId, innerLISTNonTl);
											} else if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1) == innerLISTNonTl.size()) {
												innerLIST.add(strEmpId);
												hmRequiredEmpTlList.put(strDate+"_"+strShiftId, innerLIST);
											}
											
											Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
											if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
											List<String> innList = hmEmpShiftwise.get(strShiftId);
											if(innList == null) innList = new ArrayList<String>();
											innList.add(strDate);
											hmEmpShiftwise.put(strShiftId, innList);
											hmAddEmpInWeekOff.put(strEmpId, hmEmpShiftwise);
											
											requireWeekOffCnt++;
											int dayDiff=0; 
											if(strPrevDt !=null) {
												dayDiff = uF.parseToInt(uF.dateDifference(strPrevDt, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
											}
											if(dayDiff==0 || dayDiff==2) {
												continueWeekOffCnt++;
											} else {
												continueWeekOffCnt=1;
											}
											
											strPrevDt = strDate;
											List<String> innLst = hmDatewiseNewEmpForWeekOff.get(strDate);
											if(innLst == null) innLst = new ArrayList<String>();
											innLst.add(strEmpId);
											hmDatewiseNewEmpForWeekOff.put(strDate, innLst);
										}
									} else if((uF.parseToInt(hmAdjustDays.get(strEmpId)) == 5 && shiftDayCnt==4 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 5 && shiftDayCnt==4 && requireWeekOffCnt%2 == 1) || 
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 6 && shiftDayCnt==4 && (requireWeekOffCnt==0 || requireWeekOffCnt%2 == 0)) ||
										(uF.parseToInt(hmAdjustDays.get(strEmpId)) == 6 && shiftDayCnt==3 && requireWeekOffCnt%2 == 1)) {
											
										List<String> innerLST = hmDatewiseShiftwiseTlData.get(strDate+"_"+strShiftId);
										if(innerLST == null) innerLST = new ArrayList<String>();
										
										List<String> innerLIST = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
										if(innerLIST == null) innerLIST = new ArrayList<String>();
										
										List<String> innerLISTNonTl = hmRequiredEmpNonTlList.get(strDate+"_"+strShiftId);
										if(innerLISTNonTl == null) innerLISTNonTl = new ArrayList<String>();
										
										boolean flag = false;
										for(int j=1; j<=uF.parseToInt(hmAdjustDays.get(strEmpId)); j++) {
											String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), -(j+continueWeekOffCnt))+"", DBDATE, DATE_FORMAT);
											List<String> innList = hmDatewiseNewEmpForWeekOff.get(strNewDate);
											if(innList!=null && innList.contains(strEmpId)) {
												flag = true;
											}
										}
										
//										if(!flag && uF.parseToInt(strShiftId)!=uF.parseToInt(strRemainingEmpShift) && requireWeekOffCnt < requireWeekOffDiff && (hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate))) {
										if(!flag && uF.parseToInt(strShiftId)!=uF.parseToInt(strRemainingEmpShift) && requireWeekOffCnt < requireWeekOffDiff && 
											(((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && !alTlLevels.contains(strEmpLvl)) 
											|| ((hmDatewiseNewEmpForWeekOff.get(strDate) != null ? hmDatewiseNewEmpForWeekOff.get(strDate).size() : 0) < uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(strDate)) && alTlLevels.contains(strEmpLvl) 
											&& (uF.parseToInt(hmTlEmpCnt.get(strDate))< uF.parseToInt(strTlCntForDate) || (innerLST.size()> innerLISTNonTl.size())) ) ) ) {
												
											if(alTlLevels.contains(strEmpLvl)) {
												int tlCnt = uF.parseToInt(hmTlEmpCnt.get(strDate));
												tlCnt++;
												hmTlEmpCnt.put(strDate, tlCnt+"");
											}
											if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1)> innerLISTNonTl.size()) {
												innerLISTNonTl.add(strEmpId);
												hmRequiredEmpNonTlList.put(strDate+"_"+strShiftId, innerLISTNonTl);
											} else if(alTlLevels.contains(strEmpLvl) && (innerLST.size()-1) == innerLISTNonTl.size()) {
												innerLIST.add(strEmpId);
												hmRequiredEmpTlList.put(strDate+"_"+strShiftId, innerLIST);
											}
											
											Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
											if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
											List<String> innList = hmEmpShiftwise.get(strShiftId);
											if(innList == null) innList = new ArrayList<String>();
											innList.add(strDate);
											hmEmpShiftwise.put(strShiftId, innList);
											hmAddEmpInWeekOff.put(strEmpId, hmEmpShiftwise);
											
											requireWeekOffCnt++;
											int dayDiff=0; 
											if(strPrevDt !=null) {
												dayDiff = uF.parseToInt(uF.dateDifference(strPrevDt, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
											}
											if(dayDiff==0 || dayDiff==2) {
												continueWeekOffCnt++;
											} else {
												continueWeekOffCnt=1;
											}
											
											strPrevDt = strDate;
											List<String> innLst = hmDatewiseNewEmpForWeekOff.get(strDate);
											if(innLst == null) innLst = new ArrayList<String>();
											innLst.add(strEmpId);
											hmDatewiseNewEmpForWeekOff.put(strDate, innLst);
										}
									} else {
									continueWeekOffCnt=0;
								}
									
								}
							}
						}
					}
				}
				
			}
			
//			System.out.println(" hmAssignShiftToEmp ===>> " + hmAssignShiftToEmp);
//			System.out.println("assignWeeklyOffOnBasisOfAdjustWithWorkingDays hmAddEmpInWeekOff ===>> " + hmAddEmpInWeekOff);

			
			Iterator<String> iit = hmAddEmpInWeekOff.keySet().iterator();
			while(iit.hasNext()) {
				String strEmpId = (String) iit.next();
				boolean isTlRequired = false;
				String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
				Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
				if(hmEmpShiftwise==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
				
				Iterator<String> iit1 = hmEmpShiftwise.keySet().iterator();
				while (iit1.hasNext()) {
					String strShiftId = (String) iit1.next();
					List<String> innList = hmEmpShiftwise.get(strShiftId);
					if(innList == null) innList = new ArrayList<String>();
//					System.out.println("strDate ===>> " + strDate + " --- innList ===>> " + innList); 

					for(int i=0; i<innList.size(); i++) {
						String strDate = innList.get(i);
						List<String> innnList = hmRequiredEmpTlList.get(strDate+"_"+strShiftId);
						if(innnList !=null && innnList.contains(strEmpId)) {
							isTlRequired = true;
						}
						String streNewEmpId = getNewMemberForThisDateAndShift(con, uF, strDate, strRemainingEmpShift, isTlRequired, alTlLevels, hmEmpLevelId);
						if(uF.parseToInt(streNewEmpId)>0) {
							String strNewEmpServiceId = hmEmpServiceId.get(streNewEmpId) != null ? hmEmpServiceId.get(streNewEmpId).substring(1, hmEmpServiceId.get(streNewEmpId).length()-1) : "0";
							Map<String, String> shiftMap = hmShiftTime.get(strShiftId); 
							if(shiftMap == null) shiftMap = new HashMap<String, String>();
							String strShiftFrom = shiftMap.get("FROM");
							String strShiftTo = shiftMap.get("TO"); 
							if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
								double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
								insertUpdateRoster(con, strDate, strShiftFrom, strShiftTo, dblTimeDiff, strShiftId, streNewEmpId, strNewEmpServiceId, 0, uF);
								
								insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, strDate, strEmpId, strEmpServiceId, strShiftId);
							}
						}
					}
				}
			}
			
			
//			************************** General Shift Weekend WeekOff ********************************************
			hmAddEmpInWeekOff = new LinkedHashMap<String, Map<String, List<String>>>();
			Iterator<String> it11 = hmEmpwiseData.keySet().iterator();
			while(it11.hasNext()) {
				String strEmpId = it11.next();
				int actualMonthWorkingDays = actualMonthWorkDays - uF.parseToInt(hmEmpHolidayCnt.get(strEmpId));
				String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
				List<String> alEmpOnLeaveForDate = hmEmpwiseEmpLeaveData.get(strEmpId);
				List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(strEmpId);
				if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
					empOrgId = alLastAssignedPaycycleData.get(4);
					strPaycycle = alLastAssignedPaycycleData.get(1);
					String strFrmDate = alLastAssignedPaycycleData.get(2);
					String strToDate = alLastAssignedPaycycleData.get(3);
					
					int dayCount = getEmpCurrMonthRosterDayCnt(con, uF, strEmpId, strFrmDate, strToDate, strEmpServiceId);
					if(dayCount>actualMonthWorkingDays) {
						Map<String, Map<String, String>> hmShiftwiseData = hmEmpwiseData.get(strEmpId);
						Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
						while(it1.hasNext()) {
							String strShiftId = it1.next();
							Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
							Iterator<String> it2 = hmInner.keySet().iterator();
							
							while(it2.hasNext()) {
								String strDate = it2.next();
								String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
								strDay = strDay.toUpperCase();
								List<String> alEmpAssignedWeekOffForDate = hmDatewiseEmpAssignedWeekoffData.get(strDate);
								if((alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strDate)) && (alEmpAssignedWeekOffForDate==null || !alEmpAssignedWeekOffForDate.contains(strEmpId))) {
		//							
									if(uF.parseToInt(strShiftId) == uF.parseToInt(strRemainingEmpShift) && alWeekEnds.contains(strDay)) {
										Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
										if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
										List<String> innList = hmEmpShiftwise.get(strShiftId);
										if(innList == null) innList = new ArrayList<String>();
										innList.add(strDate);
										hmEmpShiftwise.put(strShiftId, innList);
										hmAddEmpInWeekOff.put(strEmpId, hmEmpShiftwise);
									}
								}
							}
						}
					}
				}
			}
			
//			System.out.println("assignWeeklyOffOnBasisOfAdjustWithWorkingDays general shift hmAddEmpInWeekOff ===>> " + hmAddEmpInWeekOff);

			
			Iterator<String> iitt1 = hmAddEmpInWeekOff.keySet().iterator();
			while(iitt1.hasNext()) {
				String strEmpId = (String) iitt1.next();
				String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
				Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strEmpId);
				if(hmEmpShiftwise==null) hmEmpShiftwise = new HashMap<String, List<String>>();
				
				Iterator<String> iit1 = hmEmpShiftwise.keySet().iterator();
				while (iit1.hasNext()) {
					String strShiftId = (String) iit1.next();
					List<String> innList = hmEmpShiftwise.get(strShiftId);
					if(innList == null) innList = new ArrayList<String>();
					for(int i=0; i<innList.size(); i++) {
						String strDate = innList.get(i);
						insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, strDate, strEmpId, strEmpServiceId, strShiftId);
						
					}
				}
			}
			
//			************************** General Shift Weekend WeekOff ********************************************
			
			
			
			
//			***************************** Shift Trans Logic ***********************************
			/*int tempIntWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt;
			int tempIntWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt;
			Map<String, String> hm3DaysNormal = new HashMap<String, String>();
			Map<String, String> hmEmpwisePrevDate = new HashMap<String, String>();
			Iterator<String> ittt = hmAddEmpInShiftTransWeekOff.keySet().iterator();
			while(ittt.hasNext()) {
				String strDate = ittt.next();
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				List<String> alEmpOnLeaveForDate = hmDatewiseEmpLeaveData.get(strDate);
				List<String> alEmpAssignedWeekOffForDate = hmDatewiseEmpAssignedWeekoffData.get(strDate);
				
				Map<String, Map<String, String>> hmShiftwiseData = hmAddEmpInShiftTransWeekOff.get(strDate);
				Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
				while(it1.hasNext()) {
					String strShiftId = it1.next();
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
					Iterator<String> it2 = hmInner.keySet().iterator();
					int tlCnt=0;
					int mGenderCnt=0;
					int shiftMemberCnt=0;
					
					while(it2.hasNext()) {
						int gCnt=0;
						int TLCnt=0;
						String strEmpId = it2.next();
						String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
						if((alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strEmpId)) && (alEmpAssignedWeekOffForDate==null || !alEmpAssignedWeekOffForDate.contains(strEmpId))) {
							if(hmEmpGender !=null && hmEmpGender.get(strEmpId) !=null && hmEmpGender.get(strEmpId).equalsIgnoreCase("M") && maleGenderCnt>mGenderCnt) {
								mGenderCnt++;
								gCnt++;
							}
							if(alTlLevels !=null && hmEmpLevelId!=null && alTlLevels.contains(hmEmpLevelId.get(strEmpId)) && uF.parseToInt(strTlCnt)>tlCnt) {
								tlCnt++;
								TLCnt++;
							}
							intWeekdaysEmpTlGenderCnt = tempIntWeekdaysEmpTlGenderCnt + (tlCnt+mGenderCnt);
							intWeekdaysEmpTlGenderCnt = (intWeekdaysEmpTlGenderCnt > intWeekdaysEmpCnt) ? intWeekdaysEmpCnt : intWeekdaysEmpTlGenderCnt;
							
							intWeekEndEmpTlGenderCnt = tempIntWeekEndEmpTlGenderCnt + (tlCnt+mGenderCnt);
							intWeekEndEmpTlGenderCnt = (intWeekEndEmpTlGenderCnt > intWeekEndEmpCnt) ? intWeekEndEmpCnt : intWeekEndEmpTlGenderCnt;
							
							String strPrevDate = hmEmpwisePrevDate.get(strEmpId);
							int daysDiff=0; 
							if(strPrevDate !=null) {
								daysDiff = uF.parseToInt(uF.dateDifference(strPrevDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
							}
							if(daysDiff==0 || daysDiff==2) {
								int stretchDayCnt = uF.parseToInt(hm3DaysNormal.get(strEmpId));
								stretchDayCnt++;
								hm3DaysNormal.put(strEmpId, stretchDayCnt+"");
							} else {
								hm3DaysNormal.put(strEmpId, "1");
							}
							hmEmpwisePrevDate.put(strEmpId, strDate);
							
							int shiftDayCnt=0;
							for(int j=1; j<5; j++) {
								String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), j)+"", DBDATE, DATE_FORMAT);
								int dayCnt = getEmpShiftAssignOrNotForDate(con, uF, strEmpId, strNewDate, strEmpServiceId);
								shiftDayCnt = shiftDayCnt +dayCnt;
								if(dayCnt==0) {
									break;
								}
							}
							if((alWeekDays.contains(strDay) && shiftMemberCnt < intWeekdaysEmpTlGenderCnt && (uF.parseToInt(hm3DaysNormal.get(strEmpId)) <= uF.parseToInt(strNormalDays)
							|| (uF.parseToInt(hm3DaysNormal.get(strEmpId)) == (uF.parseToInt(strNormalDays)+1) && shiftDayCnt<4) || (uF.parseToInt(hm3DaysNormal.get(strEmpId)) == (uF.parseToInt(strNormalDays)+2) && shiftDayCnt<3)	
							)) ||
							(alWeekEnds.contains(strDay) && shiftMemberCnt < intWeekEndEmpTlGenderCnt && (uF.parseToInt(hm3DaysNormal.get(strEmpId)) <= uF.parseToInt(strNormalDays)
							||(uF.parseToInt(hm3DaysNormal.get(strEmpId)) == (uF.parseToInt(strNormalDays)+1) && shiftDayCnt<4) || (uF.parseToInt(hm3DaysNormal.get(strEmpId)) == (uF.parseToInt(strNormalDays)+2) && shiftDayCnt<3)	
							))) {
								Map<String, List<String>> hmEmpShiftwise = hmAssignShiftToEmp.get(strDate);
								if(hmEmpShiftwise==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
								
								List<String> innList = hmEmpShiftwise.get(strShiftId);
								if(innList==null) innList = new ArrayList<String>();
								innList.add(strEmpId);
								hmEmpShiftwise.put(strShiftId, innList);
								hmAssignShiftToEmp.put(strDate, hmEmpShiftwise);
								gCnt++;
								TLCnt++;
//								if(strShiftId.equals("2") && strDate.equals("11/11/2019") || strDate.equals("12/11/2019")) {
//									System.out.println("add  strDate ===>> " + strDate + " --- strEmpId ====>> " + strEmpId);
//								}
								shiftMemberCnt++;
								if(uF.parseToInt(hm3DaysNormal.get(strEmpId))==(uF.parseToInt(strNormalDays)+2)) {
									hm3DaysNormal.put(strEmpId, "0");
								}
							} else if(uF.parseToInt(hm3DaysNormal.get(strEmpId))== (uF.parseToInt(strNormalDays)+1) || uF.parseToInt(hm3DaysNormal.get(strEmpId))== (uF.parseToInt(strNormalDays)+2)) {
								Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strDate);
								if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
//								System.out.println("remove  strDate ===>> " + strDate + " --- strEmpId ====>> " + strEmpId);
								
								List<String> innList = hmEmpShiftwise.get(strShiftId);
								if(innList == null) innList = new ArrayList<String>();
								innList.add(strEmpId);
								hmEmpShiftwise.put(strShiftId, innList);
								hmAddEmpInWeekOff.put(strDate, hmEmpShiftwise);
								if(gCnt==1) {
									mGenderCnt--;
								}
								if(TLCnt==1) {
									tlCnt--;
								}
								if(uF.parseToInt(hm3DaysNormal.get(strEmpId))==(uF.parseToInt(strNormalDays)+2)) {
									hm3DaysNormal.put(strEmpId, "0");
								}
							}
						}
					}
				}
			}
			
			
			Map<String, Map<String, List<String>>> hmAddEmpInWeekOff1 = new LinkedHashMap<String, Map<String, List<String>>>();
			Iterator<String> iiit = hmAssignShiftToEmp.keySet().iterator();
			Map<String, List<String>> hmEmpwise7DayWeekoffDates = new HashMap<String, List<String>>();
			while (iiit.hasNext()) {
				String strDate = (String) iiit.next();
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				Map<String, List<String>> hmShiftwiseData = hmAssignShiftToEmp.get(strDate);
				Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strDate);
				if(hmEmpShiftwise==null) hmEmpShiftwise = new HashMap<String, List<String>>();
				
				Iterator<String> iit1 = hmShiftwiseData.keySet().iterator();
				while (iit1.hasNext()) {
					String strShiftId = (String) iit1.next();
					List<String> innerList = hmShiftwiseData.get(strShiftId);
					if(innerList == null) innerList = new ArrayList<String>();
					List<String> innList = hmEmpShiftwise.get(strShiftId);
					if(innList == null) innList = new ArrayList<String>();
					
//					System.out.println("strDate ===>> " + strDate + " --- innList ===>> " + innList); 
					for(int i=0; i<innList.size(); i++) { 
						String strEmpId = innList.get(i);
						String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
						
						if((alWeekDays.contains(strDay) && innerList.size() < intWeekdaysEmpTlGenderCnt) ||
							alWeekEnds.contains(strDay) && innerList.size() < intWeekEndEmpTlGenderCnt) {
							innerList.add(strEmpId);
							
						} else {
							Map<String, List<String>> hmEmpShiftwise1 = hmAddEmpInWeekOff1.get(strDate);
							if(hmEmpShiftwise1 ==null) hmEmpShiftwise1 = new LinkedHashMap<String, List<String>>();
							List<String> innList1 = hmEmpShiftwise1.get(strShiftId);
							if(innList1 == null) innList1 = new ArrayList<String>();
							innList1.add(strEmpId);
							hmEmpShiftwise1.put(strShiftId, innList1);
							hmAddEmpInWeekOff1.put(strDate, hmEmpShiftwise1);
							
							List<String> alDates = hmEmpwise7DayWeekoffDates.get(strEmpId);
							if(alDates == null) alDates = new ArrayList<String>();
							alDates.add(strDate);
							hmEmpwise7DayWeekoffDates.put(strEmpId, alDates);
						}
					}
					
					for(int i=0; i<innerList.size(); i++) { 
						String strEmpId = innerList.get(i);
						String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
						Map<String, String> shiftMap = hmShiftTime.get(strShiftId); 
						if(shiftMap == null) shiftMap = new HashMap<String, String>();
						
						String strShiftFrom = shiftMap.get("FROM");
						String strShiftTo = shiftMap.get("TO");
						if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
							double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
//							insertUpdateRoster(con, strDate, strShiftFrom, strShiftTo, dblTimeDiff, strShiftId, strEmpId, strEmpServiceId, 0, uF);
//							if(uF.parseToInt(strEmpId) == 127) {
								System.out.println("insertUpdateRoster strDate ===>> " + strDate +" --- strShiftId ===>> " + strShiftId + " --- innerList ===>> " + innerList);
//							}
						}
						
					}
				}
			}
			
			Iterator<String> iitt = hmAddEmpInWeekOff1.keySet().iterator();
			while(iitt.hasNext()) {
				String strDate = (String) iitt.next();
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff1.get(strDate);
				if(hmEmpShiftwise==null) hmEmpShiftwise = new HashMap<String, List<String>>();
				
				Iterator<String> iit1 = hmEmpShiftwise.keySet().iterator();
				while (iit1.hasNext()) {
					String strShiftId = (String) iit1.next();
					List<String> innList = hmEmpShiftwise.get(strShiftId);
					if(innList == null) innList = new ArrayList<String>();
					
//					System.out.println("strDate ===>> " + strDate + " --- innList ===>> " + innList); 
					for(int i=0; i<innList.size(); i++) {
						String strEmpId = innList.get(i);
						String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
						String  strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), 1)+"", DBDATE, DATE_FORMAT);
						List<String> alDates = hmEmpwise7DayWeekoffDates.get(strEmpId);
						int dayCnt = 4;
						int dayStart = 1;
						if(alDates.contains(strNewDate)) {
							dayCnt = 5;
							dayStart = 2;
						}
						int shiftDayCnt = 0;
						for(int j=dayStart; j<dayCnt; j++) {
							strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), j)+"", DBDATE, DATE_FORMAT);
							shiftDayCnt = shiftDayCnt + getEmpShiftAssignOrNotForDate(con, uF, strEmpId, strNewDate, strEmpServiceId);
						}
						if(shiftDayCnt>=3) {
//							if(uF.parseToInt(strEmpId) == 127) {
								System.out.println(strEmpId + " --- strDate ===>> " + strDate);
//							}
//							insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, strDate, strEmpId, strEmpServiceId, strShiftId);
						}
					}
				}
			}*/
//			************************************* Shift Trans Logic End ************************************
			
//			System.out.println(" hmAddEmpInWeekOff1 ===>> " + hmAddEmpInWeekOff1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private Map<String, String> getDatewiseGeneralShiftEmpCntData(Connection con, UtilityFunctions uF, String strFrmDate, String strToDate, String strRemainingEmpShift, List<String> alTlLevels, Map<String, String> hmEmpLevelId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmDatewiseGeneralShiftEmpCnt = new LinkedHashMap<String, String>();
		try {
//			pst = con.prepareStatement("select count(shift_id) as cnt, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? group by shift_id order by _date");
			pst = con.prepareStatement("select emp_id, _date from roster_details where _date between ? and ? and shift_id=?");
			pst.setDate(1, uF.getDateFormat(strFrmDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strToDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strRemainingEmpShift));
			rs = pst.executeQuery();
			while(rs.next()) {
				String newDate1 = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
				int empCnt = uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(newDate1));
				empCnt++;
				hmDatewiseGeneralShiftEmpCnt.put(newDate1, empCnt+"");
				String strEmpLvl = hmEmpLevelId.get(rs.getString("emp_id"));
				if(alTlLevels.contains(strEmpLvl)) {
					int tlEmpCnt = uF.parseToInt(hmDatewiseGeneralShiftEmpCnt.get(newDate1+"_TL"));
					tlEmpCnt++;
					hmDatewiseGeneralShiftEmpCnt.put(newDate1+"_TL", tlEmpCnt+"");
				}
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return hmDatewiseGeneralShiftEmpCnt;
	}

	private List<String> getNewMemberWithRulesForThisDateAndShift(Connection con, UtilityFunctions uF, String strDate, String strShiftId, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Map<String, List<String>> hmAssignedWeekOffData = new HashMap<String, List<String>>();
		List<String> alEmpIds = new ArrayList<String>();
		try {
			
			pst = con.prepareStatement("select * from roster_details where emp_id!=? and _date=? and shift_id=? order by _date");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strShiftId));
			rs = pst.executeQuery();
			while(rs.next()) {
				alEmpIds.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("alEmpIds ===>> " + alEmpIds);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return alEmpIds;
	}
	
	
	private String getNewMemberForThisDateAndShift(Connection con, UtilityFunctions uF, String strDate, String strRemainingEmpShift, boolean isTlRequired, List<String> alTlLevels, Map<String, String> hmEmpLevelId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Map<String, List<String>> hmAssignedWeekOffData = new HashMap<String, List<String>>();
//		List<String> alEmpData = new ArrayList<String>();
		String strNewEmpId = null;
		try {
//			pst = con.prepareStatement("select * from roster_details where emp_id!=? and _date between ? and ? order by _date");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getDateFormat(strPaycycleFrmDate, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
//			rs = pst.executeQuery();
//			Map<String, Map<String, List<List<String>>>> hmAssignedShiftEmpwise = new HashMap<String, Map<String, List<List<String>>>>();
//			while(rs.next()) {
//				Map<String, List<List<String>>> hmAssignedShiftShiftwise = hmAssignedShiftEmpwise.get(rs.getString("emp_id"));
//				if(hmAssignedShiftShiftwise==null) hmAssignedShiftShiftwise = new HashMap<String, List<List<String>>>();
//				List<List<String>> alShiftData = hmAssignedShiftShiftwise.get(rs.getString("_date"));
//				if(alShiftData == null) alShiftData = new ArrayList<List<String>>();
//				
//				List<String> innerList = new ArrayList<String>();
//				innerList.add(rs.getString("shift_id"));
//				innerList.add(rs.getString("roster_weeklyoff_id"));
//				alShiftData.add(innerList);
//				
//				hmAssignedShiftShiftwise.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), alShiftData);
//				hmAssignedShiftEmpwise.put(rs.getString("emp_id"), hmAssignedShiftShiftwise);
//			}
//			rs.close();
//			pst.close();
//			System.out.println("hmAssignedShiftEmpwise ===>> " + hmAssignedShiftEmpwise);
			
			pst = con.prepareStatement("select * from roster_details where _date=? and shift_id=? and roster_weeklyoff_id=0");
			pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(strRemainingEmpShift));
			rs = pst.executeQuery();
//			List<String> alEmpIds = new ArrayList<String>();
			while(rs.next()) {
//				alEmpIds.add(rs.getString("emp_id"));
//				strNewEmpId = rs.getString("emp_id");
				String strEmpLvl = hmEmpLevelId.get(rs.getString("emp_id"));
				if(isTlRequired && alTlLevels.contains(strEmpLvl)) {
					strNewEmpId = rs.getString("emp_id");
					break;
				} else if(!isTlRequired && !alTlLevels.contains(strEmpLvl)) {
					strNewEmpId = rs.getString("emp_id");
					break;
				}
			}
			rs.close();
			pst.close();
//			System.out.println("alEmpIds ===>> " + alEmpIds);
			
//			for(int i=0; i<alEmpIds.size(); i++) {
//				Map<String, List<List<String>>> hmAssignedShiftShiftwise = hmAssignedShiftEmpwise.get(alEmpIds.get(i));
////				System.out.println("hmAssignedShiftShiftwise ===>> " + hmAssignedShiftShiftwise);
//				int shiftNextDayCnt = 0;
//				int shiftNextDayWeekOffCnt = 0;
//				int shiftNextDayJ0Cnt = 0;
//				int shiftPrevDayCnt = 0;
//				int shiftPrevDayWeekOffCnt = 0;
//				int shiftPrevDayJ0Cnt = 0;
//				for(int j=1; j<=8; j++) {
//					String strNextDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), j)+"", DBDATE, DATE_FORMAT);
//					String strPrevDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), -j)+"", DBDATE, DATE_FORMAT);
////					if(j==1) {
////						System.out.println(" --- strNextDate ===>> " + strNextDate + " --- strPrevDate ===>> " + strPrevDate);
////					}
//					List<List<String>> alNextDayData = hmAssignedShiftShiftwise.get(strNextDate);
//					if(alNextDayData!=null) {
//					List<String> innListNextDay = alNextDayData.get(0);
//						if(j==1 && uF.parseToInt(innListNextDay.get(0)) == uF.parseToInt(strShiftId) && uF.parseToInt(innListNextDay.get(1))>0 ) {
//							shiftNextDayJ0Cnt++;
//						} else if(uF.parseToInt(innListNextDay.get(1))==0 && shiftNextDayWeekOffCnt<2) {
//							shiftNextDayCnt++;
//						} else if(uF.parseToInt(innListNextDay.get(1))>0){
//							shiftNextDayWeekOffCnt++;
//						}
//					}
//					
//					List<List<String>> alPrevDayData = hmAssignedShiftShiftwise.get(strPrevDate);
//					if(alPrevDayData!=null) {
//						List<String> innListPrevDay = alPrevDayData.get(0);
//						if(j==1 && uF.parseToInt(innListPrevDay.get(0)) == uF.parseToInt(strShiftId) && uF.parseToInt(innListPrevDay.get(1))>0 ) {
//							shiftPrevDayJ0Cnt++;
//						} else if(uF.parseToInt(innListPrevDay.get(1))==0 && shiftPrevDayWeekOffCnt<2) {
//							shiftPrevDayCnt++;
//						} else if(uF.parseToInt(innListPrevDay.get(1))>0) {
//							shiftPrevDayWeekOffCnt++;
//						}
//					}
////					System.out.println(" --- alNextDayData ===>> " + alNextDayData + " --- alPrevDayData ===>> " + alPrevDayData);
//				}
//				
//				if(shiftPrevDayCnt>3 && shiftPrevDayCnt<7 && shiftNextDayCnt>3 && shiftNextDayCnt<7) {
//					alEmpData.add(alEmpIds.get(i));
//					alEmpData.add(shiftPrevDayCnt+"");
//					alEmpData.add(shiftNextDayCnt+"");
//				}
////				System.out.println(" --- strEmpId ===>> " + alEmpIds.get(i) + " -- shiftNextDayJ0Cnt ===>> " + shiftNextDayJ0Cnt + " --- shiftNextDayCnt ===>> " + shiftNextDayCnt +" --- shiftNextDayWeekOffCnt ===>> " + shiftNextDayWeekOffCnt + " -- shiftPrevDayJ0Cnt ===>> " + shiftPrevDayJ0Cnt + " --- shiftPrevDayCnt ===>> " + shiftPrevDayCnt +" --- shiftPrevDayWeekOffCnt ===>> " + shiftPrevDayWeekOffCnt);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return strNewEmpId;
	}
	
	

	private void assignWeeklyOffOnBasisOf3Days(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("hmEmpLastShiftAssignedDate ===>> " + hmEmpLastShiftAssignedDate);
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_STRETCH_SHIFT", rst1.getString("no_of_days_for_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_NORMAL_WEEKOFF", rst1.getString("no_of_days_for_normal_weekoff"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strStretchDays = hmRosterPolicyRulesData.get("NO_OF_DAYS_FOR_STRETCH_SHIFT");
			
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			int maleGenderCnt = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"));
					
			int intWeekdaysEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlGenderCnt;
			int intWeekdaysEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT"));
			
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
//			System.out.println("intWeekdaysEmpTlGenderCnt ------------->> " + intWeekdaysEmpTlGenderCnt);
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmDatewiseEmpAssignedWeekoffData = getAssignedWeekOffData(con, uF, strPaycycleFrmDate, strPaycycleToDate);
			if(hmDatewiseEmpAssignedWeekoffData == null) hmDatewiseEmpAssignedWeekoffData = new HashMap<String, List<String>>();
			
//			Map<String, Map<String, Map<String, String>>> hmDatewiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpGender = CF.getEmpGenderMap(con);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
			pst = con.prepareStatement("select * from roster_details where _date between ? and ? order by emp_id,_date");
			pst.setDate(1, uF.getDateFormat(getDate(strPaycycleFrmDate, DATE_FORMAT, -7), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			Map<String, Map<String, Map<String, String>>> hmDatewiseShiftwiseEmpData = new LinkedHashMap<String, Map<String, Map<String, String>>>();
			Map<String, Map<String, List<String>>> hmEmpwiseDatewiseShiftData = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> hmDatewiseShiftwiseWorkingEmpId = new LinkedHashMap<String, List<String>>();
			while(rst.next()) {
				String empLvlId = hmEmpLevelId.get(rst.getString("emp_id"));
				Map<String, Map<String, String>> hmShiftwiseEmpId = hmDatewiseShiftwiseEmpData.get(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT));
				if(hmShiftwiseEmpId==null) hmShiftwiseEmpId = new LinkedHashMap<String, Map<String, String>>();
				
				Map<String, String> hmEmpId = hmShiftwiseEmpId.get(rst.getString("shift_id"));
				if(hmEmpId==null) hmEmpId = new LinkedHashMap<String, String>();
				
				hmEmpId.put(rst.getString("emp_id"), rst.getString("emp_id"));
				hmShiftwiseEmpId.put(rst.getString("shift_id"), hmEmpId);
				
				hmDatewiseShiftwiseEmpData.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT), hmShiftwiseEmpId);
				
				
				Map<String, List<String>> hmDatewiseShiftData = hmEmpwiseDatewiseShiftData.get(rst.getString("emp_id"));
				if(hmDatewiseShiftData ==null) hmDatewiseShiftData = new LinkedHashMap<String, List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("shift_id"));
				innerList.add(rst.getString("roster_weeklyoff_id"));
				hmDatewiseShiftData.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT), innerList);
				
				hmEmpwiseDatewiseShiftData.put(rst.getString("emp_id"), hmDatewiseShiftData);
				
				
				if(uF.parseToInt(rst.getString("roster_weeklyoff_id"))==0) {
					List<String> innerEmpId = hmDatewiseShiftwiseWorkingEmpId.get(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rst.getString("shift_id"));
					if(innerEmpId==null) innerEmpId = new ArrayList<String>();
					innerEmpId.add(rst.getString("emp_id"));
					hmDatewiseShiftwiseWorkingEmpId.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rst.getString("shift_id"), innerEmpId);
				}
			}
			rst.close();
			pst.close();
//			System.out.println("hmDatewiseShiftwiseEmpData ===>> " + hmDatewiseShiftwiseEmpData);
//			System.out.println("hmDatewiseShiftwiseWorkingEmpId ===>> " + hmDatewiseShiftwiseWorkingEmpId);
			
			Iterator<String> it = hmEmpwiseDatewiseShiftData.keySet().iterator();
			Map<String, List<String>> hmDatewiseShiftwiseWOffEmpId = new LinkedHashMap<String, List<String>>();
			Map<String, List<List<String>>> hmEmpwiseWOffDates = new LinkedHashMap<String, List<List<String>>>();
			while (it.hasNext()) {
				String strEmpId = (String) it.next();
				Map<String, List<String>> hmDatewiseShiftData = hmEmpwiseDatewiseShiftData.get(strEmpId);
				Iterator<String> it1 = hmDatewiseShiftData.keySet().iterator();
				String strPrevDate = null;
				int stretchDayCnt = 0;
				while (it1.hasNext()) {
					String strDate = (String) it1.next();
					List<String> innerList = hmDatewiseShiftData.get(strDate);
					if(uF.parseToInt(innerList.get(1))>0) {
						continue;
					}
					int daysDiff=0; 
					if(strPrevDate !=null) {
						daysDiff = uF.parseToInt(uF.dateDifference(strPrevDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
					}
					if(daysDiff==0 || daysDiff==2) {
						stretchDayCnt++;
					} else {
						stretchDayCnt=1;
					}
					strPrevDate = strDate;
					if(strEmpId.equals("163") || strEmpId.equals("170")) {
//						System.out.println(strEmpId + " -- strDate ===>> " + strDate + " -- stretchDayCnt ===>> " + stretchDayCnt);
					}
					if(stretchDayCnt==4 || stretchDayCnt==5) {
						
						int monthEndDayDiff = uF.parseToInt(uF.dateDifference(strDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
						
						int nextShiftDays = 12-stretchDayCnt;
						int nextShiftDayCnt=0;
						for(int i=0; i<nextShiftDays; i++) {
							String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), i)+"", DBDATE, DATE_FORMAT);
							List<String> innList = hmDatewiseShiftData.get(strNewDate);
							if(innList==null){
								continue;
							}
							if(uF.parseToInt(innList.get(1))==0) {
								nextShiftDayCnt++;
							}
						}
						if(strEmpId.equals("163") || strEmpId.equals("170")) {
//							System.out.println(strEmpId + " ---- strDate ===>> " + strDate + " -- nextShiftDayCnt ===>> " + nextShiftDayCnt + " -- monthEndDayDiff ===>> " + monthEndDayDiff);
						}
						List<String> alWOffEmpIds = hmDatewiseShiftwiseWOffEmpId.get(strDate+"_"+innerList.get(0));
						if(alWOffEmpIds==null) alWOffEmpIds = new ArrayList<String>();
						if((stretchDayCnt==4 && nextShiftDayCnt==8) || (stretchDayCnt==5 && nextShiftDayCnt==7)) {
							alWOffEmpIds.add(strEmpId);
							hmDatewiseShiftwiseWOffEmpId.put(strDate+"_"+innerList.get(0), alWOffEmpIds);
						} else if((stretchDayCnt==4 && nextShiftDayCnt>1 && nextShiftDayCnt== monthEndDayDiff) || (stretchDayCnt==5 && nextShiftDayCnt>=1 && nextShiftDayCnt== monthEndDayDiff)) {
							alWOffEmpIds.add(strEmpId);
							hmDatewiseShiftwiseWOffEmpId.put(strDate+"_"+innerList.get(0), alWOffEmpIds);
						}
						
						List<String> alEmpIds = hmDatewiseShiftwiseWorkingEmpId.get(strDate+"_"+innerList.get(0));
						for (int i = 0; i < alWOffEmpIds.size(); i++) {
							alEmpIds.remove(alWOffEmpIds.get(i));
						}
						if(strEmpId.equals("163") || strEmpId.equals("170")) {
//							System.out.println(strEmpId + " ------ strDate ===>> " + strDate + " -- alWOffEmpIds ===>> " + alWOffEmpIds);
//							System.out.println(strEmpId + " ------ strDate ===>> " + strDate + " -- alEmpIds ===>> " + alEmpIds);
						}
						
						int tlCnt = 0;
						for (int i = 0; i < alEmpIds.size(); i++) {
							String empLevelId = hmEmpLevelId.get(alEmpIds.get(i));
							if(alTlLevels.contains(empLevelId)) {
								tlCnt++;
							}
						}
						if(strEmpId.equals("163") || strEmpId.equals("170")) {
//							System.out.println(strEmpId + " ------ strDate ===>> " + strDate + " -- alEmpIds.size() ===>> " + alEmpIds.size()+" -- tlCnt ===>> " + tlCnt +" --- strTlCnt ===>> " + strTlCnt);
						}
						if(alEmpIds.size() >= intWeekdaysEmpCnt && tlCnt >= uF.parseToInt(strTlCnt) && ((stretchDayCnt==4 && nextShiftDayCnt==8) 
							|| (stretchDayCnt==5 && nextShiftDayCnt==7)) ) {
							List<List<String>> datesList = hmEmpwiseWOffDates.get(strEmpId);
							if(datesList==null) datesList = new ArrayList<List<String>>();
							List<String> innList = new ArrayList<String>();
							innList.add(innerList.get(0));
							innList.add(strDate);
							datesList.add(innList);
							hmEmpwiseWOffDates.put(strEmpId, datesList);
							
						} else if(alEmpIds.size() >= intWeekdaysEmpCnt && tlCnt >= uF.parseToInt(strTlCnt) && ((stretchDayCnt==4 && nextShiftDayCnt>1 && nextShiftDayCnt== monthEndDayDiff) 
							|| (stretchDayCnt==5 && nextShiftDayCnt>=1 && nextShiftDayCnt== monthEndDayDiff)) ) {
							List<List<String>> datesList = hmEmpwiseWOffDates.get(strEmpId);
							if(datesList==null) datesList = new ArrayList<List<String>>();
							List<String> innList = new ArrayList<String>();
							innList.add(innerList.get(0));
							innList.add(strDate);
							datesList.add(innList);
							hmEmpwiseWOffDates.put(strEmpId, datesList);
						}
						
					}
					
				}
				
			}
//			System.out.println("hmEmpwiseWOffDates ===>> " + hmEmpwiseWOffDates);
			
			
			Iterator<String> iitt = hmEmpwiseWOffDates.keySet().iterator();
			while(iitt.hasNext()) {
				String strEmpId = (String) iitt.next();
				List<List<String>> datesList = hmEmpwiseWOffDates.get(strEmpId);
				if(datesList == null) datesList = new ArrayList<List<String>>();
//				System.out.println("strDate ===>> " + strDate + " --- innList ===>> " + innList); 
				for(int i=0; i<datesList.size(); i++) {
					List<String> innList = datesList.get(i);
					String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
					insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, innList.get(1), strEmpId, strEmpServiceId, innList.get(0));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	private void assignWeeklyOffOnBasisOf7Days(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("hmEmpLastShiftAssignedDate ===>> " + hmEmpLastShiftAssignedDate);
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_STRETCH_SHIFT", rst1.getString("no_of_days_for_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_NORMAL_WEEKOFF", rst1.getString("no_of_days_for_normal_weekoff"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strStretchDays = hmRosterPolicyRulesData.get("NO_OF_DAYS_FOR_STRETCH_SHIFT");
			
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			int maleGenderCnt = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"));
					
			int intWeekdaysEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlGenderCnt;
			int intWeekdaysEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT"));
			
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
//			System.out.println("intWeekdaysEmpTlGenderCnt ------------->> " + intWeekdaysEmpTlGenderCnt);
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmDatewiseEmpAssignedWeekoffData = getAssignedWeekOffData(con, uF, strPaycycleFrmDate, strPaycycleToDate);
			if(hmDatewiseEmpAssignedWeekoffData == null) hmDatewiseEmpAssignedWeekoffData = new HashMap<String, List<String>>();
			
//			Map<String, Map<String, Map<String, String>>> hmDatewiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpGender = CF.getEmpGenderMap(con);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
			pst = con.prepareStatement("select * from roster_details where _date between ? and ? order by emp_id,_date");
			pst.setDate(1, uF.getDateFormat(getDate(strPaycycleFrmDate, DATE_FORMAT, -7), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			Map<String, Map<String, Map<String, String>>> hmDatewiseShiftwiseEmpData = new LinkedHashMap<String, Map<String, Map<String, String>>>();
			Map<String, Map<String, List<String>>> hmEmpwiseDatewiseShiftData = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> hmDatewiseShiftwiseWorkingEmpId = new LinkedHashMap<String, List<String>>();
			while(rst.next()) {
				String empLvlId = hmEmpLevelId.get(rst.getString("emp_id"));
				Map<String, Map<String, String>> hmShiftwiseEmpId = hmDatewiseShiftwiseEmpData.get(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT));
				if(hmShiftwiseEmpId==null) hmShiftwiseEmpId = new LinkedHashMap<String, Map<String, String>>();
				
				Map<String, String> hmEmpId = hmShiftwiseEmpId.get(rst.getString("shift_id"));
				if(hmEmpId==null) hmEmpId = new LinkedHashMap<String, String>();
				
				hmEmpId.put(rst.getString("emp_id"), rst.getString("emp_id"));
				hmShiftwiseEmpId.put(rst.getString("shift_id"), hmEmpId);
				
				hmDatewiseShiftwiseEmpData.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT), hmShiftwiseEmpId);
				
				
				Map<String, List<String>> hmDatewiseShiftData = hmEmpwiseDatewiseShiftData.get(rst.getString("emp_id"));
				if(hmDatewiseShiftData ==null) hmDatewiseShiftData = new LinkedHashMap<String, List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("shift_id"));
				innerList.add(rst.getString("roster_weeklyoff_id"));
				hmDatewiseShiftData.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT), innerList);
				
				hmEmpwiseDatewiseShiftData.put(rst.getString("emp_id"), hmDatewiseShiftData);
				
				
				if(uF.parseToInt(rst.getString("roster_weeklyoff_id"))==0) {
					List<String> innerEmpId = hmDatewiseShiftwiseWorkingEmpId.get(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rst.getString("shift_id"));
					if(innerEmpId==null) innerEmpId = new ArrayList<String>();
					innerEmpId.add(rst.getString("emp_id"));
					hmDatewiseShiftwiseWorkingEmpId.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rst.getString("shift_id"), innerEmpId);
				}
			}
			rst.close();
			pst.close();
//			System.out.println("hmDatewiseShiftwiseEmpData ===>> " + hmDatewiseShiftwiseEmpData);
//			System.out.println("hmDatewiseShiftwiseWorkingEmpId ===>> " + hmDatewiseShiftwiseWorkingEmpId);
			
			Iterator<String> it = hmEmpwiseDatewiseShiftData.keySet().iterator();
			Map<String, List<String>> hmDatewiseShiftwiseWOffEmpId = new LinkedHashMap<String, List<String>>();
			Map<String, List<List<String>>> hmEmpwiseWOffDates = new LinkedHashMap<String, List<List<String>>>();
			while (it.hasNext()) {
				String strEmpId = (String) it.next();
				Map<String, List<String>> hmDatewiseShiftData = hmEmpwiseDatewiseShiftData.get(strEmpId);
				Iterator<String> it1 = hmDatewiseShiftData.keySet().iterator();
				String strPrevDate = null;
				int stretchDayCnt = 0;
				while (it1.hasNext()) {
					String strDate = (String) it1.next();
					List<String> innerList = hmDatewiseShiftData.get(strDate);
					if(uF.parseToInt(innerList.get(1))>0) {
						continue;
					}
					int daysDiff=0; 
					if(strPrevDate !=null) {
						daysDiff = uF.parseToInt(uF.dateDifference(strPrevDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
					}
					if(daysDiff==0 || daysDiff==2) {
						stretchDayCnt++;
					} else {
						stretchDayCnt=1;
					}
					strPrevDate = strDate;
					if(strEmpId.equals("163") || strEmpId.equals("170")) {
//						System.out.println(strEmpId + " -- strDate ===>> " + strDate + " -- stretchDayCnt ===>> " + stretchDayCnt);
					}
					
					if(stretchDayCnt==8) {
						
						int monthEndDayDiff = uF.parseToInt(uF.dateDifference(strDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
						
						int nextShiftDays = 12-stretchDayCnt;
						int nextShiftDayCnt=0;
						for(int i=0; i<nextShiftDays; i++) {
							String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), i)+"", DBDATE, DATE_FORMAT);
							List<String> innList = hmDatewiseShiftData.get(strNewDate);
							if(innList==null) {
								continue;
							}
							if(uF.parseToInt(innList.get(1))==0) {
								nextShiftDayCnt++;
							}
						}
						if(strEmpId.equals("167") || strEmpId.equals("140") || strEmpId.equals("146")) {
//							System.out.println(strEmpId + " ------ strDate ===>> " + strDate + " -- nextShiftDayCnt ===>> " + nextShiftDayCnt+ " ----- monthEndDayDiff ===>>>> " + monthEndDayDiff);
						}
						
						List<String> alWOffEmpIds = hmDatewiseShiftwiseWOffEmpId.get(strDate+"_"+innerList.get(0));
						if(alWOffEmpIds==null) alWOffEmpIds = new ArrayList<String>();
						if((stretchDayCnt==8 && nextShiftDayCnt==1 && nextShiftDayCnt== monthEndDayDiff)) {
							alWOffEmpIds.add(strEmpId);
							hmDatewiseShiftwiseWOffEmpId.put(strDate+"_"+innerList.get(0), alWOffEmpIds);
						}
						
						List<String> alEmpIds = hmDatewiseShiftwiseWorkingEmpId.get(strDate+"_"+innerList.get(0));
						for (int i = 0; i < alWOffEmpIds.size(); i++) {
							alEmpIds.remove(alWOffEmpIds.get(i));
						}
						
						
						int tlCnt = 0;
						for (int i = 0; i < alEmpIds.size(); i++) {
							String empLevelId = hmEmpLevelId.get(alEmpIds.get(i));
							if(alTlLevels.contains(empLevelId)) {
								tlCnt++;
							}
						}
						if(strEmpId.equals("167") || strEmpId.equals("140") || strEmpId.equals("146")) {
//							System.out.println(strEmpId + " ------ strDate ===>> " + strDate + " -- alEmpIds.size() ===>> " + alEmpIds.size()+" -- tlCnt ===>> " + tlCnt +" --- strTlCnt ===>> " + strTlCnt);
						}
						if(alEmpIds.size() >= intWeekdaysEmpCnt && tlCnt >= uF.parseToInt(strTlCnt) && stretchDayCnt==8 && nextShiftDayCnt==1 && nextShiftDayCnt== monthEndDayDiff) {
							List<List<String>> datesList = hmEmpwiseWOffDates.get(strEmpId);
							if(datesList==null) datesList = new ArrayList<List<String>>();
							List<String> innList = new ArrayList<String>();
							innList.add(innerList.get(0));
							innList.add(strDate);
							datesList.add(innList);
							hmEmpwiseWOffDates.put(strEmpId, datesList);
							
						}
						
					}
				}
				
			}
//			System.out.println("hmEmpwiseWOffDates ===>> " + hmEmpwiseWOffDates);
			
			
			Iterator<String> iitt = hmEmpwiseWOffDates.keySet().iterator();
			while(iitt.hasNext()) {
				String strEmpId = (String) iitt.next();
				List<List<String>> datesList = hmEmpwiseWOffDates.get(strEmpId);
				if(datesList == null) datesList = new ArrayList<List<String>>();
//				System.out.println("strDate ===>> " + strDate + " --- innList ===>> " + innList); 
				for(int i=0; i<datesList.size(); i++) {
					List<String> innList = datesList.get(i);
					String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
					insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, innList.get(1), strEmpId, strEmpServiceId, innList.get(0));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public void assignWeeklyOffOnBasisOf7DaysStretch(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("hmEmpLastShiftAssignedDate ===>> " + hmEmpLastShiftAssignedDate);
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_STRETCH_SHIFT", rst1.getString("no_of_days_for_stretch_shift"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_FOR_NORMAL_WEEKOFF", rst1.getString("no_of_days_for_normal_weekoff"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strStretchDays = hmRosterPolicyRulesData.get("NO_OF_DAYS_FOR_STRETCH_SHIFT");
			
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			int maleGenderCnt = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"));
					
			int intWeekdaysEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlGenderCnt;
			int intWeekdaysEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT"));
			
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
//			System.out.println("intWeekdaysEmpTlGenderCnt ------------->> " + intWeekdaysEmpTlGenderCnt);
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, List<String>> hmDatewiseEmpAssignedWeekoffData = getAssignedWeekOffData(con, uF, strPaycycleFrmDate, strPaycycleToDate);
			if(hmDatewiseEmpAssignedWeekoffData == null) hmDatewiseEmpAssignedWeekoffData = new HashMap<String, List<String>>();
			
//			Map<String, Map<String, Map<String, String>>> hmDatewiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpGender = CF.getEmpGenderMap(con);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
			pst = con.prepareStatement("select * from roster_details where _date between ? and ? order by emp_id,_date");
			pst.setDate(1, uF.getDateFormat(getDate(strPaycycleFrmDate, DATE_FORMAT, -15), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			Map<String, Map<String, Map<String, String>>> hmDatewiseShiftwiseEmpData = new LinkedHashMap<String, Map<String, Map<String, String>>>();
			Map<String, Map<String, List<String>>> hmEmpwiseDatewiseShiftData = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, List<String>> hmDatewiseShiftwiseWorkingEmpId = new LinkedHashMap<String, List<String>>();
			while(rst.next()) {
				String empLvlId = hmEmpLevelId.get(rst.getString("emp_id"));
				Map<String, Map<String, String>> hmShiftwiseEmpId = hmDatewiseShiftwiseEmpData.get(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT));
				if(hmShiftwiseEmpId==null) hmShiftwiseEmpId = new LinkedHashMap<String, Map<String, String>>();
				
				Map<String, String> hmEmpId = hmShiftwiseEmpId.get(rst.getString("shift_id"));
				if(hmEmpId==null) hmEmpId = new LinkedHashMap<String, String>();
				
				hmEmpId.put(rst.getString("emp_id"), rst.getString("emp_id"));
				hmShiftwiseEmpId.put(rst.getString("shift_id"), hmEmpId);
				
				hmDatewiseShiftwiseEmpData.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT), hmShiftwiseEmpId);
				
				
				Map<String, List<String>> hmDatewiseShiftData = hmEmpwiseDatewiseShiftData.get(rst.getString("emp_id"));
				if(hmDatewiseShiftData ==null) hmDatewiseShiftData = new LinkedHashMap<String, List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("shift_id"));
				innerList.add(rst.getString("roster_weeklyoff_id"));
				hmDatewiseShiftData.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT), innerList);
				
				hmEmpwiseDatewiseShiftData.put(rst.getString("emp_id"), hmDatewiseShiftData);
				
				
				if(uF.parseToInt(rst.getString("roster_weeklyoff_id"))==0) {
					List<String> innerEmpId = hmDatewiseShiftwiseWorkingEmpId.get(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rst.getString("shift_id"));
					if(innerEmpId==null) innerEmpId = new ArrayList<String>();
					innerEmpId.add(rst.getString("emp_id"));
					hmDatewiseShiftwiseWorkingEmpId.put(uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rst.getString("shift_id"), innerEmpId);
				}
				
			}
			rst.close();
			pst.close();
//			System.out.println("hmDatewiseShiftwiseEmpData ===>> " + hmDatewiseShiftwiseEmpData);
			
			Iterator<String> it = hmEmpwiseDatewiseShiftData.keySet().iterator();
			Map<String, List<String>> hmDatewiseShiftwiseWOffEmpId = new LinkedHashMap<String, List<String>>();
			Map<String, List<List<String>>> hmEmpwiseWOffDates = new LinkedHashMap<String, List<List<String>>>();
			while (it.hasNext()) {
				String strEmpId = (String) it.next();
				Map<String, List<String>> hmDatewiseShiftData = hmEmpwiseDatewiseShiftData.get(strEmpId);
				Iterator<String> it1 = hmDatewiseShiftData.keySet().iterator();
				String strPrevDate = null;
				int stretchDayCnt = 0;
				while (it1.hasNext()) {
					String strDate = (String) it1.next();
					String lastMnthEndDate = getDate(strPaycycleFrmDate, DATE_FORMAT, -1);
					int dayDiff = uF.parseToInt(uF.dateDifference(lastMnthEndDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
					List<String> innerList = hmDatewiseShiftData.get(strDate);
					if(dayDiff!=1 && uF.parseToInt(innerList.get(1))>0) {
						continue;
					}
					int daysDiff=0; 
					if(strPrevDate !=null) {
						daysDiff = uF.parseToInt(uF.dateDifference(strPrevDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
					}
					if(daysDiff==0 || daysDiff==2 || (stretchDayCnt==8 && daysDiff==3)) {
						stretchDayCnt++;
					} else {
						stretchDayCnt=1;
					}
					strPrevDate = strDate;
					if(strEmpId.equals("163") || strEmpId.equals("170")) {
//						System.out.println(strEmpId + " -- strDate ===>> " + strDate + " -- stretchDayCnt ===>> " + stretchDayCnt+" --- daysDiff ===>> " + daysDiff);
					}
					if(stretchDayCnt==8 || stretchDayCnt==9) {
						
						int monthEndDayDiff = uF.parseToInt(uF.dateDifference(strDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
						
						int nextShiftDays = 12-stretchDayCnt;
						int nextShiftDayCnt=0;
						for(int i=0; i<nextShiftDays; i++) {
							String strNewDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormat(strDate, DATE_FORMAT), i)+"", DBDATE, DATE_FORMAT);
							List<String> innList = hmDatewiseShiftData.get(strNewDate);
							if(innList==null) {
								continue;
							}
							if(uF.parseToInt(innList.get(1))==0) {
								nextShiftDayCnt++;
							}
						}
						if(strEmpId.equals("163") || strEmpId.equals("170")) {
//							System.out.println(strEmpId + " ------ strDate ===>> " + strDate + " -- nextShiftDayCnt ===>> " + nextShiftDayCnt+ " ----- monthEndDayDiff ===>>>> " + monthEndDayDiff);
						}
						
						List<String> alWOffEmpIds = hmDatewiseShiftwiseWOffEmpId.get(strDate+"_"+innerList.get(0));
						if(alWOffEmpIds==null) alWOffEmpIds = new ArrayList<String>();
						if((stretchDayCnt==8 && nextShiftDayCnt==4) || (stretchDayCnt==9 && nextShiftDayCnt==3)) {
							alWOffEmpIds.add(strEmpId);
							hmDatewiseShiftwiseWOffEmpId.put(strDate+"_"+innerList.get(0), alWOffEmpIds);
						} else if((stretchDayCnt==8 && nextShiftDayCnt>1 && nextShiftDayCnt== monthEndDayDiff) || (stretchDayCnt==9 && nextShiftDayCnt>=1 && nextShiftDayCnt== monthEndDayDiff)) {
							alWOffEmpIds.add(strEmpId);
							hmDatewiseShiftwiseWOffEmpId.put(strDate+"_"+innerList.get(0), alWOffEmpIds);
						}
						
						List<String> alEmpIds = hmDatewiseShiftwiseWorkingEmpId.get(strDate+"_"+innerList.get(0));
						for (int i = 0; i < alWOffEmpIds.size(); i++) {
							alEmpIds.remove(alWOffEmpIds.get(i));
						}
						
						
						int tlCnt = 0;
						for (int i = 0; i < alEmpIds.size(); i++) {
							String empLevelId = hmEmpLevelId.get(alEmpIds.get(i));
							if(alTlLevels.contains(empLevelId)) {
								tlCnt++;
							}
						}
						if(strEmpId.equals("163") || strEmpId.equals("170")) {
//							System.out.println(strEmpId + " ------ strDate ===>> " + strDate + " -- alEmpIds.size() ===>> " + alEmpIds.size()+" -- tlCnt ===>> " + tlCnt +" --- strTlCnt ===>> " + strTlCnt);
						}
						if(alEmpIds.size() >= intWeekdaysEmpCnt && tlCnt >= uF.parseToInt(strTlCnt) && ((stretchDayCnt==8 && nextShiftDayCnt==4) 
							|| (stretchDayCnt==9 && nextShiftDayCnt==3)) ) {
							List<List<String>> datesList = hmEmpwiseWOffDates.get(strEmpId);
							if(datesList==null) datesList = new ArrayList<List<String>>();
							List<String> innList = new ArrayList<String>();
							innList.add(innerList.get(0));
							innList.add(strDate);
							datesList.add(innList);
							hmEmpwiseWOffDates.put(strEmpId, datesList);
							
						} else if(alEmpIds.size() >= intWeekdaysEmpCnt && tlCnt >= uF.parseToInt(strTlCnt) && ((stretchDayCnt==8 && nextShiftDayCnt>1 && nextShiftDayCnt== monthEndDayDiff) 
							|| (stretchDayCnt==9 && nextShiftDayCnt>=1 && nextShiftDayCnt== monthEndDayDiff)) ) {
							List<List<String>> datesList = hmEmpwiseWOffDates.get(strEmpId);
							if(datesList==null) datesList = new ArrayList<List<String>>();
							List<String> innList = new ArrayList<String>();
							innList.add(innerList.get(0));
							innList.add(strDate);
							datesList.add(innList);
							hmEmpwiseWOffDates.put(strEmpId, datesList);
						}
						
					}
					
				}
				
			}
//			System.out.println("hmEmpwiseWOffDates ===>> " + hmEmpwiseWOffDates);
			
			
			Iterator<String> iitt = hmEmpwiseWOffDates.keySet().iterator();
			while(iitt.hasNext()) {
				String strEmpId = (String) iitt.next();
				List<List<String>> datesList = hmEmpwiseWOffDates.get(strEmpId);
				if(datesList == null) datesList = new ArrayList<List<String>>();
//				System.out.println("strDate ===>> " + strDate + " --- innList ===>> " + innList); 
				for(int i=0; i<datesList.size(); i++) {
					List<String> innList = datesList.get(i);
					String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
					insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, innList.get(1), strEmpId, strEmpServiceId, innList.get(0));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	/*public void assignWeeklyOffOnBasisOfRules(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
//			sbQuery.append(" and emp_id=193 ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
//			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpNoInShifts = new HashMap<String, String>();
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			List<String> alWeekDays = uF.getWeekDays();
			List<String> alWeekEnds = uF.getWeekEnds();
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			int maleGenderCnt = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"));
					
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
//			System.out.println("intWeekEndEmpTlGenderCnt ------------->> " + intWeekEndEmpTlGenderCnt);
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, Map<String, Map<String, String>>> hmDatewiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpGender = CF.getEmpGenderMap(con);
			if(hmEmpGender == null) hmEmpGender = new HashMap<String, String>();
			String newShiftId = "";
			for(int j=0; j<nOfdays; j++) {
				String newDate1 = getDate(strPaycycleFrmDate, DATE_FORMAT, j);
				String strDay = uF.getDateFormat(newDate1, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_per_id,emp_gender from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id " +
					" and epd.is_alive=true and joining_date<=? "); // //(121,122,125,127,128,131,133,134,136,137,140,141,145,146,147,149,152,154,157,162)
				sbQuery.append("and epd.emp_per_id in (121,122,125,127,128,131,134,137,140,141,145,146,147,149,152,157,162,163,164,165)");
//				sbQuery.append("and epd.emp_per_id in (127,133,134,137,145,146,149)");
//				sbQuery.append("and epd.emp_per_id in (125,131,147,152,154,157)");
//				sbQuery.append("and epd.emp_per_id in (121,122,128,136,140,141,162)");
				pst1 = con.prepareStatement(sbQuery.toString()); 
				pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst1 ===>> " + pst1);
				rst1 = pst1.executeQuery();
				while (rst1.next()) {
					String empId = rst1.getString("emp_per_id");
					String strEmpServiceId = hmEmpServiceId.get(empId) != null ? hmEmpServiceId.get(empId).substring(1, hmEmpServiceId.get(empId).length()-1) : "0";
					
					List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(empId);
					if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
						empOrgId = alLastAssignedPaycycleData.get(4);
						strPaycycle = alLastAssignedPaycycleData.get(1);
//						String strFrmDate = alLastAssignedPaycycleData.get(2);
//						String strToDate = alLastAssignedPaycycleData.get(3);
						
						String[] strPrevPayCycle = CF.getPayCycleDatesOnPaycycleId(con, (uF.parseToInt(strPaycycle)-1)+"", empOrgId, CF.getStrTimeZone(), CF, request);
						String strPrevFrmDate = strPrevPayCycle[0];
						String strPrevToDate = strPrevPayCycle[1];
						
						Map<String, String> hmEmpLastMonthRoster = getEmpLastMonthRosterData(con, uF, empId, strPrevFrmDate, strPrevToDate, strEmpServiceId, rotFirst, rotSecond, rotThird);
						String strExistShiftId  = hmEmpLastMonthRoster.get("LASTSHIFT_ID"); //innerList.get(1);
						
						if(!rotFirst.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotFirst) && uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))==0)) {
							newShiftId = rotSecond;
						} else if(!rotSecond.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotSecond) && uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))==0)) {
							newShiftId = rotThird;
						} else if(!rotThird.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotThird) && uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))==0)) {
							newShiftId = rotFirst;
						} else {
							newShiftId = strRemainingEmpShift;
						}
						
						if(j >= 15) {
//							System.out.println(empId+ " -- newShiftId ========================== ===>> " + newShiftId);
							if(!rotFirst.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotFirst)) {
								newShiftId = rotSecond;
							} else if(!rotSecond.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotSecond)) {
								newShiftId = rotThird;
							} else if(!rotThird.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotThird)) {
								newShiftId = rotFirst;
							}
//							System.out.println("newShiftId after ==================== ===>> " + newShiftId);
						}
						
						if(alWeekEnds.contains(strDay)) {
							
							Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(newDate1);
							if(hmShiftwiseData==null) hmShiftwiseData = new LinkedHashMap<String, Map<String,String>>();
							
							Map<String, String> hmInner = hmShiftwiseData.get(newShiftId);
							if(hmInner==null)hmInner = new LinkedHashMap<String, String>();
							
							hmInner.put(empId, empId);
							hmShiftwiseData.put(newShiftId, hmInner);
							hmDatewiseData.put(newDate1, hmShiftwiseData);
						}
					}
				
				}
				rst1.close();
				pst1.close();
			}
//			System.out.println("hmDatewiseData ===>> " + hmDatewiseData);
			Iterator<String> itt = hmDatewiseData.keySet().iterator();
			Map<String, List<String>> hmNotAddEmpInWeekOff = new LinkedHashMap<String, List<String>>();
			Map<String, Map<String, List<String>>> hmAddEmpInWeekOff = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmAssignShiftToEmp = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, String>> hmAddEmpwiseCountWeekNos = new LinkedHashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmRemoveEmpwiseCountWeekNos = new LinkedHashMap<String, Map<String, String>>();
			int dtCnt=0;
			int weekIncrement=1;
			boolean firstSunFlag = false;
			while(itt.hasNext()) {
				String strDate = itt.next();
//				System.out.println("strDate ===>> " + strDate);
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				if(dtCnt==0 && strDay.equals("SUN")) {
					firstSunFlag = true;
				}
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strPaycycleFrmDate, strDate, DATE_FORMAT);
				int intWeekNoOfTheDateInMonth = uF.parseToInt(weekNoOfTheDateInMonth);
				if(firstSunFlag && strDay.equals("SAT")) {
					intWeekNoOfTheDateInMonth += weekIncrement;
				}
				List<String> alEmpOnLeaveForDate = hmDatewiseEmpLeaveData.get(strDate);
				
				Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(strDate);
//				System.out.println("hmShiftwiseData ===>> " + hmShiftwiseData);
				Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
				while(it1.hasNext()) {
					String strShiftId = it1.next();
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
					Iterator<String> it2 = hmInner.keySet().iterator();
					while(it2.hasNext()) {
						String strEmpId = it2.next();
//						System.out.println("strEmpId ===>> " + strEmpId);
						if(alEmpOnLeaveForDate!=null && alEmpOnLeaveForDate.contains(strEmpId)) {
							List<String> innList = hmNotAddEmpInWeekOff.get(strDate);
							if(innList == null) innList = new ArrayList<String>();
							innList.add(strEmpId);
							hmNotAddEmpInWeekOff.put(strDate, innList);
							
							Map<String, String> hmWeekNoCnt = hmRemoveEmpwiseCountWeekNos.get(strEmpId);
							if(hmWeekNoCnt==null)hmWeekNoCnt = new LinkedHashMap<String, String>();
							int cnt = uF.parseToInt(hmWeekNoCnt.get(intWeekNoOfTheDateInMonth+""));
							cnt++;
							hmWeekNoCnt.put(intWeekNoOfTheDateInMonth+"", cnt+"");
							
							hmRemoveEmpwiseCountWeekNos.put(strEmpId, hmWeekNoCnt);
						}
					}
				}
				dtCnt++;
			}
			
//			System.out.println(" hmNotAddEmpInWeekOff ===>> " + hmNotAddEmpInWeekOff);
//			System.out.println(" hmAddEmpwiseCountWeekNos ===>> " + hmAddEmpwiseCountWeekNos);
			
			int tempIntWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt;
			dtCnt=0;
			firstSunFlag = false;
			Iterator<String> it = hmDatewiseData.keySet().iterator();
			while(it.hasNext()) {
				String strDate = it.next();
//				System.out.println("strDate ===>> " + strDate);
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				if(dtCnt==0 && strDay.equals("SUN")) {
					firstSunFlag = true;
				}
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strPaycycleFrmDate, strDate, DATE_FORMAT);
				int intWeekNoOfTheDateInMonth = uF.parseToInt(weekNoOfTheDateInMonth);
				if(firstSunFlag && strDay.equals("SAT")) {
					intWeekNoOfTheDateInMonth += weekIncrement;
				}
				List<String> alEmpOnLeaveForDate = hmDatewiseEmpLeaveData.get(strDate);
				
				Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(strDate);
//				System.out.println("hmShiftwiseData ===>> " + hmShiftwiseData);
				Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
				while(it1.hasNext()) {
					String strShiftId = it1.next();
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
					Iterator<String> it2 = hmInner.keySet().iterator();
					Iterator<String> it22 = hmInner.keySet().iterator();
					int tlCnt=0;
					int mGenderCnt=0;
					int tlCnt1=0;
					int mGenderCnt1=0;
					int shiftMemberCnt=0;
					
					List<String> empInnerList = new ArrayList<String>();
					while(it22.hasNext()) {
						String strEmpId = it22.next();
						if(alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strEmpId)) {
							empInnerList.add(strEmpId);
						}
					}
					if(strDate.equals("04/01/2020") || strDate.equals("11/01/2020")) {
//						System.out.println("empInnerList ===>> " + empInnerList);
					}
					
					int alEmpCnt = 0;
					while(it2.hasNext()) {
						int gCnt=0;
						int TLCnt=0;
						String strEmpId = it2.next();
						String strEmpLvl = hmEmpLevelId.get(strEmpId);
						String strEmpGender = hmEmpGender.get(strEmpId);

						if(strDate.equals("04/01/2020") || strDate.equals("11/01/2020")) {
//							System.out.println("strEmpId ===>> " + strEmpId +" --- strShiftId ===>> " + strShiftId +" --- strDate =====>> " + strDate);
						}
						Map<String, String> hmRemoveWeekNoCnt = hmRemoveEmpwiseCountWeekNos.get(strEmpId);
						if(hmRemoveWeekNoCnt==null)hmRemoveWeekNoCnt = new LinkedHashMap<String, String>();
						
						Map<String, String> hmWeekNoCnt1 = hmAddEmpwiseCountWeekNos.get(strEmpId);
						if(hmWeekNoCnt1==null)hmWeekNoCnt1 = new LinkedHashMap<String, String>();
//						System.out.println(strEmpId +" --- hmWeekNoCnt1 ===>> " + hmWeekNoCnt1);
//						System.out.println(strEmpId +" --- alEmpOnLeaveForDate ===>> " + alEmpOnLeaveForDate);
						if(alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strEmpId)) {
							if(hmEmpGender !=null && hmEmpGender.get(strEmpId) !=null && hmEmpGender.get(strEmpId).equalsIgnoreCase("M") && maleGenderCnt>mGenderCnt) {
								mGenderCnt++;
								gCnt++;
							}
							if(alTlLevels !=null && hmEmpLevelId!=null && alTlLevels.contains(hmEmpLevelId.get(strEmpId)) && uF.parseToInt(strTlCnt)>tlCnt) {
								tlCnt++;
								TLCnt++;
							} 
							
//							intWeekEndEmpTlGenderCnt = tempIntWeekEndEmpTlGenderCnt + (tlCnt+mGenderCnt);
//							intWeekEndEmpTlGenderCnt = (intWeekEndEmpTlGenderCnt > intWeekEndEmpCnt) ? intWeekEndEmpCnt : intWeekEndEmpTlGenderCnt;
							intWeekEndEmpTlGenderCnt = intWeekEndEmpCnt;
							
							if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
//								System.out.println("tempIntWeekEndEmpTlGenderCnt =====>> " + tempIntWeekEndEmpTlGenderCnt + " --- tlCnt ===>> " + tlCnt + " --- mGenderCnt ===>> " + mGenderCnt);
//								System.out.println(strEmpId + " --- weekNoOfTheDateInMonth ======>> " + weekNoOfTheDateInMonth+ " -- intWeekEndEmpTlGenderCnt ===>> " + intWeekEndEmpTlGenderCnt+ " -- shiftMemberCnt ===>> " + shiftMemberCnt);
//								System.out.println(strEmpId + " --- hmRemoveWeekNoCnt ======>> " + hmRemoveWeekNoCnt+ " -- hmWeekNoCnt1 ===>> " + hmWeekNoCnt1+ " -- intWeekNoOfTheDateInMonth ===>> " + intWeekNoOfTheDateInMonth);
							}
							boolean addEmpFlag = true;
							if(empInnerList.size()>shiftMemberCnt) {
								int reqDiff = intWeekEndEmpTlGenderCnt - shiftMemberCnt;
								int remainEmp = empInnerList.size() - alEmpCnt;
//								if(strEmpGender !=null && strEmpGender.equalsIgnoreCase("M") && maleGenderCnt>mGenderCnt) {
//									mGenderCnt++;
//								}
								if(alTlLevels !=null && alTlLevels.contains(strEmpLvl) && uF.parseToInt(strTlCnt)==tlCnt1 && remainEmp>reqDiff) {
									addEmpFlag = false;
								}
								alEmpCnt++;
							}
						
							if(addEmpFlag && !hmRemoveWeekNoCnt.containsKey(intWeekNoOfTheDateInMonth+"") && (uF.parseToInt(intWeekNoOfTheDateInMonth+"") ==1 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==3 && hmWeekNoCnt1.containsKey(1+""))
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==3 && !hmWeekNoCnt1.containsKey(2+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==2 && !hmWeekNoCnt1.containsKey(1+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==4 && hmWeekNoCnt1.containsKey(2+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==4 && !hmWeekNoCnt1.containsKey(3+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==5 && !hmWeekNoCnt1.containsKey(4+""))) 
								&& shiftMemberCnt<intWeekEndEmpTlGenderCnt) {
								
								Map<String, List<String>> hmEmpShiftwise = hmAssignShiftToEmp.get(strDate);
								if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
								
								List<String> innList = hmEmpShiftwise.get(strShiftId);
								if(innList == null) innList = new ArrayList<String>();
								innList.add(strEmpId);
								hmEmpShiftwise.put(strShiftId, innList);
								hmAssignShiftToEmp.put(strDate, hmEmpShiftwise);
								if(strEmpGender !=null && strEmpGender.equalsIgnoreCase("M") && maleGenderCnt>mGenderCnt1) {
									mGenderCnt1++;
								}
								if(alTlLevels !=null && alTlLevels.contains(strEmpLvl) && uF.parseToInt(strTlCnt)>tlCnt1) {
									tlCnt1++;
								}
								gCnt++;
								TLCnt++;
								Map<String, String> hmWeekNoCnt = hmAddEmpwiseCountWeekNos.get(strEmpId);
								if(hmWeekNoCnt==null)hmWeekNoCnt = new LinkedHashMap<String, String>();
								int cnt = uF.parseToInt(hmWeekNoCnt.get(intWeekNoOfTheDateInMonth+""));
								cnt++;
								hmWeekNoCnt.put(intWeekNoOfTheDateInMonth+"", cnt+"");
								hmAddEmpwiseCountWeekNos.put(strEmpId, hmWeekNoCnt);
								shiftMemberCnt++;
								String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
								Map<String, String> shiftMap = hmShiftTime.get(strShiftId); 
								if(shiftMap == null) shiftMap = new HashMap<String, String>();
								
								String strShiftFrom = shiftMap.get("FROM");
								String strShiftTo = shiftMap.get("TO");
								if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
									double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
									insertUpdateRoster(con, strDate, strShiftFrom, strShiftTo, dblTimeDiff, strShiftId, strEmpId, strEmpServiceId, 0, uF);
								}
								if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
//									System.out.println("if strEmpId ===>> " + strEmpId + " --- mGenderCnt ===>> " + mGenderCnt +" --- tlCnt ===>> " + tlCnt);
								}
							} else {
								Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strDate);
								if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
								
								List<String> innList = hmEmpShiftwise.get(strShiftId);
								if(innList == null) innList = new ArrayList<String>();
								innList.add(strEmpId);
								hmEmpShiftwise.put(strShiftId, innList);
								hmAddEmpInWeekOff.put(strDate, hmEmpShiftwise);
								String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
//								insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, strDate, strEmpId, strEmpServiceId, strShiftId);
								if(gCnt==1) {
									mGenderCnt--;
								}
								if(TLCnt==1) {
									tlCnt--;
								}
								if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
//									System.out.println("else strEmpId ===>> " + strEmpId + " --- mGenderCnt ===>> " + mGenderCnt +" --- tlCnt ===>> " + tlCnt);
								}
							}
						}
						
					
					}
				}
				dtCnt++;
			}
			
			
//			System.out.println("II hmAddEmpwiseCountWeekNos ===>> " + hmAddEmpwiseCountWeekNos);
//			System.out.println(" hmAssignShiftToEmp ===>> " + hmAssignShiftToEmp);
//			System.out.println(" hmAddEmpInWeekOff ===>> " + hmAddEmpInWeekOff);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	
	
	
	
	
	
	public void assignWeekEndWeeklyOffOnBasisOfRules(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date= (select max(paycycle_from_date) from assign_shift_dates) order by paycycle_from_date");
//			sbQuery.append(" and emp_id=193 ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			String strPaycycleFrmDate = null;
			String strPaycycleToDate = null;
			String strPaycycle = null;
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				strPaycycleFrmDate = uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT);
				strPaycycleToDate = uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT);
				strPaycycle = rst.getString("paycycle_no");
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
//			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpNoInShifts = new HashMap<String, String>();
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			List<String> alWeekEnds = uF.getWeekEnds();
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			int maleGenderCnt = uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"));
					
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
//			System.out.println("intWeekEndEmpTlGenderCnt ------------->> " + intWeekEndEmpTlGenderCnt);
			int nOfdays = uF.parseToInt(uF.dateDifference(strPaycycleFrmDate, DATE_FORMAT, strPaycycleToDate, DATE_FORMAT, CF.getStrTimeZone()));
			
			Map<String, List<String>> hmDatewiseEmpLeaveData = getEmpApprovedLeaveDataDatewise(con, uF, strPaycycleFrmDate, strPaycycleToDate, empOrgId);
			if(hmDatewiseEmpLeaveData == null) hmDatewiseEmpLeaveData = new HashMap<String, List<String>>();
			
			Map<String, Map<String, Map<String, String>>> hmDatewiseData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			Map<String, Map<String, Map<String, String>>> hmDatewiseCurrMonthData = new LinkedHashMap<String, Map<String,Map<String,String>>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpGender = CF.getEmpGenderMap(con);
			if(hmEmpGender == null) hmEmpGender = new HashMap<String, String>();
			String newShiftId = "";
			for(int j=0; j<nOfdays; j++) {
				String newDate1 = getDate(strPaycycleFrmDate, DATE_FORMAT, j);
				String strDay = uF.getDateFormat(newDate1, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_per_id,emp_gender,joining_date from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id " +
					" and epd.is_alive=true and is_roster=true and joining_date<=? "); //  //(121,122,125,127,128,131,133,134,136,137,140,141,145,146,147,149,152,154,157,162)
//				sbQuery.append("and epd.emp_per_id in (121,122,125,127,128,131,134,137,140,141,145,146,147,149,152,157,162,163,164,165)");
//				sbQuery.append("and epd.emp_per_id in (127,133,134,137,145,146,149)");
//				sbQuery.append("and epd.emp_per_id in (125,131,147,152,154,157)");
//				sbQuery.append("and epd.emp_per_id in (121,122,128,136,140,141,162)");
				pst1 = con.prepareStatement(sbQuery.toString()); 
				pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst1 ===>> " + pst1);
				rst1 = pst1.executeQuery();
				while (rst1.next()) {
					String empId = rst1.getString("emp_per_id");
					String strEmpServiceId = hmEmpServiceId.get(empId) != null ? hmEmpServiceId.get(empId).substring(1, hmEmpServiceId.get(empId).length()-1) : "0";
					
					List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(empId);
					if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
						empOrgId = alLastAssignedPaycycleData.get(4);
						strPaycycle = alLastAssignedPaycycleData.get(1);
						String strFrmDate = alLastAssignedPaycycleData.get(2);
						String strToDate = alLastAssignedPaycycleData.get(3);
						
						
						String[] strPrevPayCycle = CF.getPayCycleDatesOnPaycycleId(con, (uF.parseToInt(strPaycycle)-1)+"", empOrgId, CF.getStrTimeZone(), CF, request);
						String strPrevFrmDate = strPrevPayCycle[0];
						String strPrevToDate = strPrevPayCycle[1];
						
						Map<String, String> hmEmpLastMonthRoster = getEmpLastMonthRosterData(con, uF, empId, strPrevFrmDate, strPrevToDate, strEmpServiceId, rotFirst, rotSecond, rotThird);
						String strExistShiftId  = hmEmpLastMonthRoster.get("LASTSHIFT_ID"); //innerList.get(1);
						
						if(!rotFirst.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotFirst) && uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))==0)) {
							newShiftId = rotSecond;
						} else if(!rotSecond.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotSecond) && uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))==0)) {
							newShiftId = rotThird;
						} else if(!rotThird.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotThird) && uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))==0)) {
							newShiftId = rotFirst;
						} else {
							newShiftId = strRemainingEmpShift;
						}
						
						if(j >= 15) {
//							System.out.println(empId+ " -- newShiftId ========================== ===>> " + newShiftId);
							if(!rotFirst.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotFirst)) {
								newShiftId = rotSecond;
							} else if(!rotSecond.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotSecond)) {
								newShiftId = rotThird;
							} else if(!rotThird.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotThird)) {
								newShiftId = rotFirst;
							}
//							System.out.println("newShiftId after ==================== ===>> " + newShiftId);
						}
						
						if(alWeekEnds.contains(strDay)) {
							
							Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(newDate1);
							if(hmShiftwiseData==null) hmShiftwiseData = new LinkedHashMap<String, Map<String,String>>();
							
							Map<String, String> hmInner = hmShiftwiseData.get(newShiftId);
							if(hmInner==null)hmInner = new LinkedHashMap<String, String>();
							
							hmInner.put(empId, empId);
							hmShiftwiseData.put(newShiftId, hmInner);
							hmDatewiseData.put(newDate1, hmShiftwiseData);
						}
						
						
						
						Map<String, String> hmEmpCurrMonthRoster = getEmpCurrMonthRosterData(con, uF, empId, strFrmDate, strToDate, strEmpServiceId);
						
						String newShiftId1 = hmEmpCurrMonthRoster.get(newDate1);
						if(newShiftId1 == null) {
							continue;
						}
						
						if(alWeekEnds.contains(strDay)) {
							Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseCurrMonthData.get(newDate1);
							if(hmShiftwiseData==null) hmShiftwiseData = new LinkedHashMap<String, Map<String,String>>();
							
							Map<String, String> hmInner = hmShiftwiseData.get(newShiftId1);
							if(hmInner==null)hmInner = new LinkedHashMap<String, String>();
							
							hmInner.put(empId, empId);
							hmShiftwiseData.put(newShiftId1, hmInner);
							hmDatewiseCurrMonthData.put(newDate1, hmShiftwiseData);
						}
					}
				}
				rst1.close();
				pst1.close();
			}
			
			
//			System.out.println("hmDatewiseCurrMonthData ===>> " + hmDatewiseCurrMonthData);
//			System.out.println("hmDatewiseData ===>> " + hmDatewiseData);
			
			
			Iterator<String> ittt = hmDatewiseCurrMonthData.keySet().iterator();
			Map<String, Map<String, List<String>>> hmAddEmpInWeekOff = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmAssignShiftToEmp = new LinkedHashMap<String, Map<String, List<String>>>();
			int dtCnt=0;
			int weekIncrement=1;
			boolean firstSunFlag = false;
			while(ittt.hasNext()) {
				String strDate = ittt.next();
				String strDateAfter7Days = getDate(strDate, DATE_FORMAT, 7);
				String strDateBefore7Days = getDate(strDate, DATE_FORMAT, -7);
//				System.out.println("strDate ===>> " + strDate);
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				if(dtCnt==0 && strDay.equals("SUN")) {
					firstSunFlag = true;
				}
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strPaycycleFrmDate, strDate, DATE_FORMAT);
				int intWeekNoOfTheDateInMonth = uF.parseToInt(weekNoOfTheDateInMonth);
				if(firstSunFlag && strDay.equals("SAT")) {
					intWeekNoOfTheDateInMonth += weekIncrement;
				}
//				System.out.println(strDate + " -- intWeekNoOfTheDateInMonth ===>> " + intWeekNoOfTheDateInMonth);
				List<String> alEmpOnLeaveForDate = hmDatewiseEmpLeaveData.get(strDate);
				List<String> alEmpOnLeaveForDateAfter7Days = hmDatewiseEmpLeaveData.get(strDateAfter7Days);
				
				Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(strDate);
				Map<String, Map<String, String>> hmShiftwiseCurrMonthData = hmDatewiseCurrMonthData.get(strDate);
//				System.out.println("hmShiftwiseData ===>> " + hmShiftwiseData);
				Iterator<String> it1 = hmShiftwiseCurrMonthData.keySet().iterator();
				while(it1.hasNext()) {
					int wekkOffTlCnt=0;
					String strShiftId = it1.next();
					Map<String, String> hmInnerCurrMonth = hmShiftwiseCurrMonthData.get(strShiftId);
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
//					System.out.println(strDate + " -- strShiftId ===>> " + strShiftId + " -- hmInnerCurrMonth ===>> " + hmInnerCurrMonth + "-- hmInner ===>> " + hmInner);
					Iterator<String> it21 = hmInnerCurrMonth.keySet().iterator();
					Iterator<String> it22 = hmInner.keySet().iterator();
					Map<String, List<String>> hmReadyEmpListForToday = new HashMap<String, List<String>>();
					Map<String, List<String>> hmReadyEmpListForAfter7Days = new HashMap<String, List<String>>();
					Map<String, List<String>> hmExitstEmpListForToday = new HashMap<String, List<String>>();
					while(it22.hasNext()) {
						String strEmpId = it22.next();
						String strEmpLvl = hmEmpLevelId.get(strEmpId);
//						System.out.println("strEmpId ===>> " + strEmpId);
						if(alEmpOnLeaveForDate!=null && !alEmpOnLeaveForDate.contains(strEmpId) && !hmInnerCurrMonth.keySet().contains(strEmpId)) {
							
							List<String> alInner = hmReadyEmpListForToday.get(strShiftId);
							if(alInner==null)alInner = new ArrayList<String>();
							alInner.add(strEmpId);
							hmReadyEmpListForToday.put(strShiftId, alInner);
							
							if(alTlLevels!=null && alTlLevels.contains(strEmpLvl)) {
								alInner = hmReadyEmpListForToday.get(strShiftId+"_TL");
								if(alInner==null)alInner = new ArrayList<String>();
								alInner.add(strEmpId);
								hmReadyEmpListForToday.put(strShiftId+"_TL", alInner);
							}
						}
						if(alEmpOnLeaveForDateAfter7Days!=null && !alEmpOnLeaveForDateAfter7Days.contains(strEmpId) && !hmInnerCurrMonth.keySet().contains(strEmpId)) {
							List<String> alInner = hmReadyEmpListForAfter7Days.get(strShiftId);
							if(alInner==null)alInner = new ArrayList<String>();
							alInner.add(strEmpId);
							hmReadyEmpListForAfter7Days.put(strShiftId, alInner);
							
							if(alTlLevels!=null && alTlLevels.contains(strEmpLvl)) {
								alInner = hmReadyEmpListForAfter7Days.get(strShiftId+"_TL");
								if(alInner==null)alInner = new ArrayList<String>();
								alInner.add(strEmpId);
								hmReadyEmpListForAfter7Days.put(strShiftId+"_TL", alInner);
							}
						}
					}
					
					
					while(it21.hasNext()) {
						String strEmpId = it21.next();
						String strEmpLvl = hmEmpLevelId.get(strEmpId);
//						System.out.println("strEmpId ===>> " + strEmpId);
						if(alEmpOnLeaveForDate!=null && !alEmpOnLeaveForDate.contains(strEmpId) && !hmInnerCurrMonth.keySet().contains(strEmpId)) {
							
							List<String> alInner = hmExitstEmpListForToday.get(strShiftId);
							if(alInner==null)alInner = new ArrayList<String>();
							alInner.add(strEmpId);
							hmExitstEmpListForToday.put(strShiftId, alInner);
							
							if(alTlLevels!=null && alTlLevels.contains(strEmpLvl)) {
								alInner = hmExitstEmpListForToday.get(strShiftId+"_TL");
								if(alInner==null)alInner = new ArrayList<String>();
								alInner.add(strEmpId);
								hmExitstEmpListForToday.put(strShiftId+"_TL", alInner);
							}
						}
					}
					
					
					Iterator<String> it2 = hmInnerCurrMonth.keySet().iterator();
					while(it2.hasNext()) {
						String strEmpId = it2.next();
						String strEmpLvl = hmEmpLevelId.get(strEmpId);
//						System.out.println("strEmpId ===>> " + strEmpId);
						List<String> alInnerExistEmp = hmExitstEmpListForToday.get(strShiftId);
						List<String> alInnerTLExistEmp = hmExitstEmpListForToday.get(strShiftId+"_TL");
						
						List<String> alInner = hmReadyEmpListForToday.get(strShiftId);
						List<String> alInnerTL = hmReadyEmpListForToday.get(strShiftId+"_TL");
						
						List<String> alInnerAfter7Days = hmReadyEmpListForAfter7Days.get(strShiftId);
						List<String> alInnerTLAfter7Days = hmReadyEmpListForAfter7Days.get(strShiftId+"_TL");
						if((alTlLevels!=null && alTlLevels.contains(strEmpLvl) && ((alInnerTL!=null && alInnerTL.size()>0) ||(alInnerTLExistEmp!=null && alInnerTLExistEmp.size()>1 && wekkOffTlCnt==0))) 
							|| (alTlLevels!=null && !alTlLevels.contains(strEmpLvl))) {
							
							Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strDate);
							if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
							
							List<String> innList = hmEmpShiftwise.get(strShiftId);
							if(innList == null) innList = new ArrayList<String>();
							innList.add(strEmpId);
							hmEmpShiftwise.put(strShiftId, innList);
							hmAddEmpInWeekOff.put(strDate, hmEmpShiftwise);
							if(alTlLevels!=null && alTlLevels.contains(strEmpLvl)) {
								wekkOffTlCnt++;
							}
						}
						
						if(alTlLevels!=null && alTlLevels.contains(strEmpLvl)) {
							
						}
					}
				}
				dtCnt++;
			}
			
			
			
			
			Iterator<String> itt = hmDatewiseData.keySet().iterator();
			Map<String, List<String>> hmNotAddEmpInWeekOff = new LinkedHashMap<String, List<String>>();
//			Map<String, Map<String, List<String>>> hmAddEmpInWeekOff = new LinkedHashMap<String, Map<String, List<String>>>();
//			Map<String, Map<String, List<String>>> hmAssignShiftToEmp = new LinkedHashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, String>> hmAddEmpwiseCountWeekNos = new LinkedHashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmRemoveEmpwiseCountWeekNos = new LinkedHashMap<String, Map<String, String>>();
			dtCnt=0;
			weekIncrement=1;
			firstSunFlag = false;
			while(itt.hasNext()) {
				String strDate = itt.next();
//				System.out.println("strDate ===>> " + strDate);
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				if(dtCnt==0 && strDay.equals("SUN")) {
					firstSunFlag = true;
				}
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strPaycycleFrmDate, strDate, DATE_FORMAT);
				int intWeekNoOfTheDateInMonth = uF.parseToInt(weekNoOfTheDateInMonth);
				if(firstSunFlag && strDay.equals("SAT")) {
					intWeekNoOfTheDateInMonth += weekIncrement;
				}
				List<String> alEmpOnLeaveForDate = hmDatewiseEmpLeaveData.get(strDate);
				
				Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(strDate);
//				System.out.println("hmShiftwiseData ===>> " + hmShiftwiseData);
				Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
				while(it1.hasNext()) {
					String strShiftId = it1.next();
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
					Iterator<String> it2 = hmInner.keySet().iterator();
					while(it2.hasNext()) {
						String strEmpId = it2.next();
//						System.out.println("strEmpId ===>> " + strEmpId);
						if(alEmpOnLeaveForDate!=null && alEmpOnLeaveForDate.contains(strEmpId)) {
							List<String> innList = hmNotAddEmpInWeekOff.get(strDate);
							if(innList == null) innList = new ArrayList<String>();
							innList.add(strEmpId);
							hmNotAddEmpInWeekOff.put(strDate, innList);
							
							Map<String, String> hmWeekNoCnt = hmRemoveEmpwiseCountWeekNos.get(strEmpId);
							if(hmWeekNoCnt==null)hmWeekNoCnt = new LinkedHashMap<String, String>();
							int cnt = uF.parseToInt(hmWeekNoCnt.get(intWeekNoOfTheDateInMonth+""));
							cnt++;
							hmWeekNoCnt.put(intWeekNoOfTheDateInMonth+"", cnt+"");
							
							hmRemoveEmpwiseCountWeekNos.put(strEmpId, hmWeekNoCnt);
						}
					}
				}
				dtCnt++;
			}
			
//			System.out.println(" hmNotAddEmpInWeekOff ===>> " + hmNotAddEmpInWeekOff);
//			System.out.println(" hmAddEmpwiseCountWeekNos ===>> " + hmAddEmpwiseCountWeekNos);
			
			int tempIntWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt;
			dtCnt=0;
			firstSunFlag = false;
			Iterator<String> it = hmDatewiseData.keySet().iterator();
			while(it.hasNext()) {
				String strDate = it.next();
//				System.out.println("strDate ===>> " + strDate);
				String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				if(dtCnt==0 && strDay.equals("SUN")) {
					firstSunFlag = true;
				}
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strPaycycleFrmDate, strDate, DATE_FORMAT);
				int intWeekNoOfTheDateInMonth = uF.parseToInt(weekNoOfTheDateInMonth);
				if(firstSunFlag && strDay.equals("SAT")) {
					intWeekNoOfTheDateInMonth += weekIncrement;
				}
				List<String> alEmpOnLeaveForDate = hmDatewiseEmpLeaveData.get(strDate);
				
				Map<String, Map<String, String>> hmShiftwiseData = hmDatewiseData.get(strDate);
//				System.out.println("hmShiftwiseData ===>> " + hmShiftwiseData);
				Iterator<String> it1 = hmShiftwiseData.keySet().iterator();
				while(it1.hasNext()) {
					String strShiftId = it1.next();
					Map<String, String> hmInner = hmShiftwiseData.get(strShiftId);
					Iterator<String> it2 = hmInner.keySet().iterator();
					Iterator<String> it22 = hmInner.keySet().iterator();
					int tlCnt=0;
					int mGenderCnt=0;
					int tlCnt1=0;
					int mGenderCnt1=0;
					int shiftMemberCnt=0;
					
					List<String> empInnerList = new ArrayList<String>();
					while(it22.hasNext()) {
						String strEmpId = it22.next();
						if(alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strEmpId)) {
							empInnerList.add(strEmpId);
						}
					}
					if(strDate.equals("04/01/2020") || strDate.equals("11/01/2020")) {
//						System.out.println("empInnerList ===>> " + empInnerList);
					}
					
					int alEmpCnt = 0;
					while(it2.hasNext()) {
						int gCnt=0;
						int TLCnt=0;
						String strEmpId = it2.next();
						String strEmpLvl = hmEmpLevelId.get(strEmpId);
						String strEmpGender = hmEmpGender.get(strEmpId);

						if(strDate.equals("04/01/2020") || strDate.equals("11/01/2020")) {
//							System.out.println("strEmpId ===>> " + strEmpId +" --- strShiftId ===>> " + strShiftId +" --- strDate =====>> " + strDate);
						}
						Map<String, String> hmRemoveWeekNoCnt = hmRemoveEmpwiseCountWeekNos.get(strEmpId);
						if(hmRemoveWeekNoCnt==null)hmRemoveWeekNoCnt = new LinkedHashMap<String, String>();
						
						Map<String, String> hmWeekNoCnt1 = hmAddEmpwiseCountWeekNos.get(strEmpId);
						if(hmWeekNoCnt1==null)hmWeekNoCnt1 = new LinkedHashMap<String, String>();
//						System.out.println(strEmpId +" --- hmWeekNoCnt1 ===>> " + hmWeekNoCnt1);
//						System.out.println(strEmpId +" --- alEmpOnLeaveForDate ===>> " + alEmpOnLeaveForDate);
						if(alEmpOnLeaveForDate==null || !alEmpOnLeaveForDate.contains(strEmpId)) {
							intWeekEndEmpTlGenderCnt = intWeekEndEmpCnt;
							
							if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
//								System.out.println("tempIntWeekEndEmpTlGenderCnt =====>> " + tempIntWeekEndEmpTlGenderCnt + " --- tlCnt ===>> " + tlCnt + " --- mGenderCnt ===>> " + mGenderCnt);
//								System.out.println(strEmpId + " --- weekNoOfTheDateInMonth ======>> " + weekNoOfTheDateInMonth+ " -- intWeekEndEmpTlGenderCnt ===>> " + intWeekEndEmpTlGenderCnt+ " -- shiftMemberCnt ===>> " + shiftMemberCnt);
//								System.out.println(strEmpId + " --- hmRemoveWeekNoCnt ======>> " + hmRemoveWeekNoCnt+ " -- hmWeekNoCnt1 ===>> " + hmWeekNoCnt1+ " -- intWeekNoOfTheDateInMonth ===>> " + intWeekNoOfTheDateInMonth);
							}
							boolean addEmpFlag = true;
							if(empInnerList.size()>shiftMemberCnt) {
								int reqDiff = intWeekEndEmpTlGenderCnt - shiftMemberCnt;
								int remainEmp = empInnerList.size() - alEmpCnt;
								if(alTlLevels !=null && alTlLevels.contains(strEmpLvl) && uF.parseToInt(strTlCnt)==tlCnt1 && remainEmp>reqDiff) {
									addEmpFlag = false;
								}
								alEmpCnt++;
							}
						
							if(addEmpFlag && !hmRemoveWeekNoCnt.containsKey(intWeekNoOfTheDateInMonth+"") && (uF.parseToInt(intWeekNoOfTheDateInMonth+"") ==1 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==3 && hmWeekNoCnt1.containsKey(1+""))
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==3 && !hmWeekNoCnt1.containsKey(2+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==2 && !hmWeekNoCnt1.containsKey(1+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==4 && hmWeekNoCnt1.containsKey(2+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==4 && !hmWeekNoCnt1.containsKey(3+"")) 
								|| (uF.parseToInt(intWeekNoOfTheDateInMonth+"")==5 && !hmWeekNoCnt1.containsKey(4+""))) 
								&& shiftMemberCnt<intWeekEndEmpTlGenderCnt) {
								
								Map<String, List<String>> hmEmpShiftwise = hmAssignShiftToEmp.get(strDate);
								if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
								
								List<String> innList = hmEmpShiftwise.get(strShiftId);
								if(innList == null) innList = new ArrayList<String>();
								innList.add(strEmpId);
								hmEmpShiftwise.put(strShiftId, innList);
								hmAssignShiftToEmp.put(strDate, hmEmpShiftwise);
								if(strEmpGender !=null && strEmpGender.equalsIgnoreCase("M") && maleGenderCnt>mGenderCnt1) {
									mGenderCnt1++;
								}
								if(alTlLevels !=null && alTlLevels.contains(strEmpLvl) && uF.parseToInt(strTlCnt)>tlCnt1) {
									tlCnt1++;
								}
								gCnt++;
								TLCnt++;
								Map<String, String> hmWeekNoCnt = hmAddEmpwiseCountWeekNos.get(strEmpId);
								if(hmWeekNoCnt==null)hmWeekNoCnt = new LinkedHashMap<String, String>();
								int cnt = uF.parseToInt(hmWeekNoCnt.get(intWeekNoOfTheDateInMonth+""));
								cnt++;
								hmWeekNoCnt.put(intWeekNoOfTheDateInMonth+"", cnt+"");
								hmAddEmpwiseCountWeekNos.put(strEmpId, hmWeekNoCnt);
								shiftMemberCnt++;
								String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
								Map<String, String> shiftMap = hmShiftTime.get(strShiftId); 
								if(shiftMap == null) shiftMap = new HashMap<String, String>();
								
								String strShiftFrom = shiftMap.get("FROM");
								String strShiftTo = shiftMap.get("TO");
								if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
									double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
									insertUpdateRoster(con, strDate, strShiftFrom, strShiftTo, dblTimeDiff, strShiftId, strEmpId, strEmpServiceId, 0, uF);
								}
								if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
//									System.out.println("if strEmpId ===>> " + strEmpId + " --- mGenderCnt ===>> " + mGenderCnt +" --- tlCnt ===>> " + tlCnt);
								}
							} else {
								Map<String, List<String>> hmEmpShiftwise = hmAddEmpInWeekOff.get(strDate);
								if(hmEmpShiftwise ==null) hmEmpShiftwise = new LinkedHashMap<String, List<String>>();
								
								List<String> innList = hmEmpShiftwise.get(strShiftId);
								if(innList == null) innList = new ArrayList<String>();
								innList.add(strEmpId);
								hmEmpShiftwise.put(strShiftId, innList);
								hmAddEmpInWeekOff.put(strDate, hmEmpShiftwise);
								String strEmpServiceId = hmEmpServiceId.get(strEmpId) != null ? hmEmpServiceId.get(strEmpId).substring(1, hmEmpServiceId.get(strEmpId).length()-1) : "0";
//								insertUpdateRosterWeekendWeeklyOff(con, uF, strPaycycle, strPaycycleFrmDate, strDate, strEmpId, strEmpServiceId, strShiftId);
								if(gCnt==1) {
									mGenderCnt--;
								}
								if(TLCnt==1) {
									tlCnt--;
								}
								if(strShiftId.equals("3") && (strDate.equals("04/01/2020") || strDate.equals("11/01/2020"))) {
//									System.out.println("else strEmpId ===>> " + strEmpId + " --- mGenderCnt ===>> " + mGenderCnt +" --- tlCnt ===>> " + tlCnt);
								}
							}
						}
						
					
					}
				}
				dtCnt++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public void assignShiftsOnBasisOfRules(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {

			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from assign_shift_dates where paycycle_from_date<=? order by paycycle_from_date desc limit 1");
			sbQuery.append("select * from assign_shift_dates order by paycycle_from_date desc limit 1");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst);
			rst = pst.executeQuery();
			List<String> alLastAssignedPaycycleData = new ArrayList<String>();
			while (rst.next()) {
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
//				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
			
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			int intWeekdaysEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlGenderCnt;
			
			int intWeekdaysEmpTlCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - uF.parseToInt(strTlCnt));
			intWeekdaysEmpTlCnt = intWeekdaysEmpTlCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlCnt;
			
			int intWeekdaysEmpGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")));
			intWeekdaysEmpGenderCnt = intWeekdaysEmpGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpGenderCnt;
			
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			
			int intWeekEndEmpTlCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - uF.parseToInt(strTlCnt));
			intWeekEndEmpTlCnt = intWeekEndEmpTlCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlCnt;
			
			int intWeekEndEmpGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")));
			intWeekEndEmpGenderCnt = intWeekEndEmpGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpGenderCnt;
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
//			System.out.println("rotFirst ===>> " + rotFirst + " --- rotSecond ===>> " + rotSecond + " --- rotThird ===>> " + rotThird);
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
//			System.out.println("hmShiftTime ===>> " + hmShiftTime);
			double dblTimeDiff = 0;
			int nOfdays = 0;
			
			
			
			String[] strPayCycle = CF.getPayCycleFromDate(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF.getStrTimeZone(), CF, empOrgId);
			String strFrmDate = strPayCycle[0];
			String strToDate = strPayCycle[1];
			
			if(alLastAssignedPaycycleData !=null && alLastAssignedPaycycleData.size()>0) {
				String strLastPaycycleId = alLastAssignedPaycycleData.get(1);
				strFrmDate = alLastAssignedPaycycleData.get(2);
				strToDate = alLastAssignedPaycycleData.get(3);
	//			Date dtLastFromDt = uF.getDateFormat(strLastFromDt, DATE_FORMAT);
	//			Date dtCurrDt = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				strPayCycle = CF.getPayCycleDatesOnPaycycleId(con, (uF.parseToInt(strLastPaycycleId)+1)+"", empOrgId, CF.getStrTimeZone(), CF, request);
//				strFrmDate = strPayCycle[0];
//				strToDate = strPayCycle[1]; 
			}
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,emp_gender,joining_date from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id " +
				" and epd.is_alive=true and is_roster=true and joining_date<=? "); //  //(121,122,125,127,128,131,133,134,136,137,140,141,145,146,147,149,152,154,157,162)
//			sbQuery.append("and epd.emp_per_id in (121,122,125,127,128,131,134,137,140,141,145,146,147,149,152,157,162,163,164,165)");
//			sbQuery.append("and epd.emp_per_id in (170,166)");
			sbQuery.append(" order by emp_per_id");
			pst1 = con.prepareStatement(sbQuery.toString()); 
			pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			Map<String, List<String>> hmEmpData = new LinkedHashMap<String, List<String>>();
			while (rst1.next()) {
				List<String> innerList = new ArrayList<String>();
				String empId = rst1.getString("emp_per_id");
				String empGender = rst1.getString("emp_gender");
				String empJoiningDate = rst1.getString("joining_date");
				String empLevelId = hmEmpLevelId.get(empId);
				
				innerList.add(empId); //0
				innerList.add(empGender); //1
				innerList.add(empJoiningDate); //2
				innerList.add(empLevelId); //3
				innerList.add(empOrgId); //4
				innerList.add(strPayCycle[0]); //5
				innerList.add(strPayCycle[1]); //6
				innerList.add(strFrmDate); //7
				innerList.add(strToDate); //8
				innerList.add(strPayCycle[2]); //9
				
				hmEmpData.put(empId, innerList);
			}
			rst1.close();
			pst1.close();
//			System.out.println("hmEmpData ===>> " + hmEmpData);
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select max(emp_shift_trans_id) as emp_shift_trans_id,emp_id from emp_shift_trans_details where shift_trans_date between ? and ?");
//			sbQuery.append(" and emp_id in (170,166)");
			sbQuery.append(" group by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFrmDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strToDate, DATE_FORMAT));
//			System.out.println("pst1 ===>> " + pst);
			StringBuilder sbTransId = null; 
			rst = pst.executeQuery();
			while (rst.next()) {
				if(sbTransId==null) {
					sbTransId = new StringBuilder();
					sbTransId.append(rst.getString("emp_shift_trans_id"));
				} else {
					sbTransId.append(","+rst.getString("emp_shift_trans_id"));
				}
			}
			rst.close();
			pst.close();
			
			Map<String, List<String>> hmEmpLastShiftTransData = new LinkedHashMap<String, List<String>>();
			if(sbTransId!=null) {
				sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_shift_trans_details where emp_shift_trans_id in ("+sbTransId.toString()+")");
//				sbQuery.append(" and emp_id in (170,166)");
				sbQuery.append(" order by emp_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst1 ===>> " + pst);
				rst = pst.executeQuery();
				while (rst.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(uF.getDateFormat(rst.getString("shift_trans_date"), DBDATE, DATE_FORMAT));
					innerList.add(rst.getString("shift_id"));
					innerList.add(rst.getString("next_shift_id"));
					innerList.add(rst.getString("paycycle_no"));
					hmEmpLastShiftTransData.put(rst.getString("emp_id"), innerList);
				}
				rst.close();
				pst.close();
			}
//			System.out.println("hmEmpLastShiftTransData ===>> " + hmEmpLastShiftTransData);
			
			
			Iterator<String> it = hmEmpData.keySet().iterator();
			Map<String, Map<String, String>> hmEmpwiseDateAndShiftId = new LinkedHashMap<String, Map<String, String>>();
			Map<String, List<List<String>>> hmEmpwiseShiftTransDates = new LinkedHashMap<String, List<List<String>>>();
			while (it.hasNext()) {
				String empId = (String) it.next();
				List<String> innerList = hmEmpData.get(empId);
				String strFromDate = innerList.get(5);
				
				List<String> alEmpLastShiftTransData = hmEmpLastShiftTransData.get(empId);
				if(alEmpLastShiftTransData==null) {
					continue;
				}
				nOfdays = uF.parseToInt(uF.dateDifference(innerList.get(5), DATE_FORMAT, innerList.get(6), DATE_FORMAT, CF.getStrTimeZone()));
				int rosterDaysCnt=0;
				for(int j=0; j<nOfdays; j++) {
					String newDate1 = getDate(strFromDate, DATE_FORMAT, j);
					rosterDaysCnt = uF.parseToInt(uF.dateDifference(alEmpLastShiftTransData.get(0), DATE_FORMAT, newDate1, DATE_FORMAT, CF.getStrTimeZone()));
					if(rosterDaysCnt>14) {
						String strNextShiftId = alEmpLastShiftTransData.get(2);
						List<List<String>> alShiftTransData = hmEmpwiseShiftTransDates.get(empId);
						if(alShiftTransData==null) alShiftTransData = new ArrayList<List<String>>();
						
						alEmpLastShiftTransData = new ArrayList<String>();
						alEmpLastShiftTransData.add(newDate1);
						alEmpLastShiftTransData.add(strNextShiftId);
						String newShiftId = "";
						if(!rotFirst.equals("") && uF.parseToInt(strNextShiftId) == uF.parseToInt(rotFirst)) {
							newShiftId = rotSecond;
						} else if(!rotSecond.equals("") && uF.parseToInt(strNextShiftId) == uF.parseToInt(rotSecond)) {
							newShiftId = rotThird;
						} else if(!rotThird.equals("") && uF.parseToInt(strNextShiftId) == uF.parseToInt(rotThird)) {
							newShiftId = rotFirst;
						}
						alEmpLastShiftTransData.add(newShiftId);
						alEmpLastShiftTransData.add(innerList.get(9));
						
						alShiftTransData.add(alEmpLastShiftTransData);
						hmEmpwiseShiftTransDates.put(empId, alShiftTransData);
					}
					
					Map<String, String> hmDatewiseShiftId = hmEmpwiseDateAndShiftId.get(empId);
					if(hmDatewiseShiftId==null) hmDatewiseShiftId = new LinkedHashMap<String, String>();
					
					hmDatewiseShiftId.put(newDate1, alEmpLastShiftTransData.get(1));
					
					hmEmpwiseDateAndShiftId.put(empId, hmDatewiseShiftId);
					
				}
				
			}
//			System.out.println("hmEmpwiseShiftTransDates =====>>>> " + hmEmpwiseShiftTransDates);
			
			
			boolean insertUpdateFlag = false;
			Iterator<String> itEmp = hmEmpwiseDateAndShiftId.keySet().iterator();
			while(itEmp.hasNext()) {
				String empId = itEmp.next();
				List<String> innerList = hmEmpData.get(empId);
				
				String strEmpServiceId = hmEmpServiceId.get(empId) != null ? hmEmpServiceId.get(empId).substring(1, hmEmpServiceId.get(empId).length()-1) : "0";
				Map<String, String> hmDatewiseShiftId = hmEmpwiseDateAndShiftId.get(empId);
				Iterator<String> itDate = hmDatewiseShiftId.keySet().iterator();
				while(itDate.hasNext()) {
					String strDate = itDate.next();
					String shiftId = hmDatewiseShiftId.get(strDate);
					Map<String, String> shiftMap = hmShiftTime.get(shiftId); 
					if(shiftMap == null) shiftMap = new HashMap<String, String>();
					
					String strShiftFrom = shiftMap.get("FROM");
					String strShiftTo = shiftMap.get("TO");
					
					int updateValue = updateRoster(con, strDate, DATE_FORMAT, strShiftFrom, strShiftTo, dblTimeDiff, shiftId, empId, strEmpServiceId, uF);
					insertUpdateFlag = true;
					if(updateValue == 0) {
//						System.out.println("============== updateValue ===========>> " + updateValue);
						insertRoster(con, empId, strDate, DATE_FORMAT, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, shiftId, 0, uF);
					}
				}
				
				pst = con.prepareStatement("select * from assign_shift_dates where emp_id=? and paycycle_no=? and paycycle_from_date=? and paycycle_to_date=? and org_id=?");
				pst.setInt(1, uF.parseToInt(empId));
				pst.setInt(2, uF.parseToInt(innerList.get(9)));
				pst.setDate(3, uF.getDateFormat(innerList.get(5), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(innerList.get(6), DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(empOrgId));
//				System.out.println("pst1 ===>> " + pst);
				rst = pst.executeQuery();
				boolean flag = false; 
				while(rst.next()) {
					flag = true;
				}
				rst.close();
				pst.close();
				
				if(!flag && insertUpdateFlag) {
					pst = con.prepareStatement("insert into assign_shift_dates (emp_id,paycycle_no,paycycle_from_date,paycycle_to_date,org_id,added_by,entry_date) values (?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(empId));
					pst.setInt(2, uF.parseToInt(innerList.get(9)));
					pst.setDate(3, uF.getDateFormat(innerList.get(5), DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(innerList.get(6), DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(empOrgId));
					pst.setInt(6, uF.parseToInt(strEmpId));
					pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
//					System.out.println("pst1 ===>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
				
				List<List<String>> alShiftTransData = hmEmpwiseShiftTransDates.get(empId);
				for(int i=0; alShiftTransData!=null && i<alShiftTransData.size(); i++) {
					List<String> innList = alShiftTransData.get(i);
					pst = con.prepareStatement("insert into emp_shift_trans_details (emp_id,paycycle_no,shift_trans_date,shift_id,next_shift_id,added_by,entry_date) values (?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(empId));
					pst.setInt(2, uF.parseToInt(innList.get(3)));
					pst.setDate(3, uF.getDateFormat(innList.get(0), DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(innList.get(1)));
					pst.setInt(5, uF.parseToInt(innList.get(2)));
					pst.setInt(6, uF.parseToInt(strEmpId));
					pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.executeUpdate();
					pst.close();
				}
			}
			
//			System.out.println("hmEmpwiseDatewiseShiftId ===>> " + hmEmpwiseDatewiseShiftId);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/*public void assignShiftsOnBasisOfRulesOFWeeklyOffForOneDay(Connection con, UtilityFunctions uF, String strDate, String strPaycycle, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {
			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
//			List<String> alEmpIds = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date<=? order by paycycle_from_date");
//			sbQuery.append(" and emp_id=193 ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
//			System.out.println("pst1 =============== ===>> " + pst);
			rst = pst.executeQuery();
			
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
			
//			System.out.println(strEmpId + " --- assignShiftsOnBasisOfRulesOFWeeklyOffForOneDay =======================>> " + strDate);
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			int intWeekdaysEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlGenderCnt;
			
			int intWeekdaysEmpTlCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - uF.parseToInt(strTlCnt));
			intWeekdaysEmpTlCnt = intWeekdaysEmpTlCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlCnt;
			
			int intWeekdaysEmpGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")));
			intWeekdaysEmpGenderCnt = intWeekdaysEmpGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpGenderCnt;
			
			int intWeekdaysEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT"));
			
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			
			int intWeekEndEmpTlCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - uF.parseToInt(strTlCnt));
			intWeekEndEmpTlCnt = intWeekEndEmpTlCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlCnt;
			
			int intWeekEndEmpGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")));
			intWeekEndEmpGenderCnt = intWeekEndEmpGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpGenderCnt;
			
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
//			Map<String, String> hmEmpNoInShifts = new HashMap<String, String>();
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			List<String> alWeekDays = uF.getWeekDays();
			List<String> alWeekEnds = uF.getWeekEnds();
			
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
//			System.out.println("hmShiftTime ===>> " + hmShiftTime);
			double dblTimeDiff = 0;
			int nOfdays = 0;
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,emp_gender from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id " +
				" and epd.is_alive=true and joining_date<=?  and epd.emp_per_id in (125,131,147,152,154,157) " +
				" and epd.emp_per_id not in ("+strEmpId+")"); // (121,122,125,127,128,131,133,134,136,137,140,141,145,146,147,149,152,154,157,162)  
			pst1 = con.prepareStatement(sbQuery.toString()); 
			pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			int cnt=0;
			while (rst1.next()) {
				cnt++;
				String empId = rst1.getString("emp_per_id");
//				System.out.println("SECOND CALL empId ========================================= ===>> " + empId);
				String empGender = rst1.getString("emp_gender");
				String empLevelId = hmEmpLevelId.get(empId);
				String[] strPayCycle = CF.getPayCycleDatesOnPaycycleId(con, strPaycycle, empOrgId, CF.getStrTimeZone(), CF, request);
				String strFrmDate = strDate;
				String strToDate = strDate; 
				
				List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(empId);
				if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
					empOrgId = alLastAssignedPaycycleData.get(4);
//					strFrmDate = alLastAssignedPaycycleData.get(2);
//					strToDate = alLastAssignedPaycycleData.get(3);
				} 
				String[] strPrevPayCycle = CF.getPayCycleDatesOnPaycycleId(con, (uF.parseToInt(strPaycycle)-1)+"", empOrgId, CF.getStrTimeZone(), CF, request);
				String strPrevFrmDate = strPrevPayCycle[0];
				String strPrevToDate = strPrevPayCycle[1];
				
				
				List<String> alEmpLeaveDates = getEmpApprovedLeaveData(con, uF, empId, strDate, strDate, empOrgId);
				if(alEmpLeaveDates == null) alEmpLeaveDates = new ArrayList<String>();
				
				Map<String, Map<String, String>> hmRosterAssignedEmpCntData = getRosterAssignedEmpCountData(con, uF, empId, strFrmDate, strToDate, empOrgId, alTlLevels);
				
				Map<String, String> hmExistEmpCnt = hmRosterAssignedEmpCntData.get("EMP_CNT");
				Map<String, String> hmExistEmpGenderwiseCnt = hmRosterAssignedEmpCntData.get("EMP_GENDERWISE_CNT");
				Map<String, String> hmExistTlEmpCnt = hmRosterAssignedEmpCntData.get("TL_EMP_CNT");
				
				String strEmpServiceId = hmEmpServiceId.get(empId) != null ? hmEmpServiceId.get(empId).substring(1, hmEmpServiceId.get(empId).length()-1) : "0";
				Map<String, String> hmEmpLastMonthRoster = getEmpLastMonthRosterData(con, uF, empId, strPrevFrmDate, strPrevToDate, strEmpServiceId, rotFirst, rotSecond, rotThird);
				if(cnt==1) { // || uF.parseToInt(empId) == 154
//					System.out.println("assignShiftsOnBasisOfRulesOFWeeklyOffForOneDay -- hmRosterAssignedEmpCntData ===>> " + hmRosterAssignedEmpCntData);
//					System.out.println("hmEmpLastMonthRoster ===>> " + hmEmpLastMonthRoster);
				}
				
				nOfdays = uF.parseToInt(uF.dateDifference(strDate, DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
				String strExistShiftId  = hmEmpLastMonthRoster.get("LASTSHIFT_ID"); //innerList.get(1);
				
				int diffNoOfDays = uF.parseToInt(uF.dateDifference(strPayCycle[0], DATE_FORMAT, strDate, DATE_FORMAT, CF.getStrTimeZone()));
//				System.out.println(empId + "nOfdays ===>> " + nOfdays +" --- strExistShiftId ===>> " + strExistShiftId);
				String strFromDate = strDate;
				String newShiftId = "";
				if(!rotFirst.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotFirst) && uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))==0)) {
					newShiftId = rotSecond;
				} else if(!rotSecond.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotSecond) && uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))==0)) {
					newShiftId = rotThird;
				} else if(!rotThird.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotThird) && uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))==0)) {
					newShiftId = rotFirst;
				} else {
//					newShiftId = rotFirst;
				}
				
				boolean insertUpdateFlag = false;
//				System.out.println(empId + " newShiftId ===>> " + newShiftId + " --- strEmpServiceId ===>> " + strEmpServiceId);
				Map<String, String> shiftMap = hmShiftTime.get(newShiftId);
				if(shiftMap == null) shiftMap = new HashMap<String, String>();
				
				String strShiftFrom = shiftMap.get("FROM");
				String strShiftTo = shiftMap.get("TO");
				if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
					dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
					int rosterDaysCnt = 0;
					for(int j=0; j<nOfdays; j++) {
						
						if(diffNoOfDays > 15) {
//							System.out.println("newShiftId ========================== ===>> " + newShiftId);
							if(!rotFirst.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotFirst)) {
								newShiftId = rotSecond;
							} else if(!rotSecond.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotSecond)) {
								newShiftId = rotThird;
							} else if(!rotThird.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotThird)) {
								newShiftId = rotFirst;
							}
							rosterDaysCnt = 0;
							shiftMap = hmShiftTime.get(newShiftId);
							if(shiftMap == null) shiftMap = new HashMap<String, String>();
							
							strShiftFrom = shiftMap.get("FROM");
							strShiftTo = shiftMap.get("TO");
//							System.out.println("newShiftId after ==================== ===>> " + newShiftId);
						}
						
						String newDate1 = getDate(strFromDate, DATE_FORMAT, j);
						String strDay = uF.getDateFormat(newDate1, DATE_FORMAT, "E");
						strDay = strDay.toUpperCase();

						int intExistCnt = uF.parseToInt(hmExistEmpCnt.get(newShiftId+"_"+newDate1));
						int intExistMaleGenderCnt = uF.parseToInt(hmExistEmpGenderwiseCnt.get(newShiftId+"_"+newDate1+"_M"));
						int intTlExistCnt = uF.parseToInt(hmExistTlEmpCnt.get(newShiftId+"_"+newDate1));
						
						int intWeekEndEmpTlGenderCnt1 = intWeekEndEmpTlGenderCnt;
						int intWeekdaysEmpTlGenderCnt1 = intWeekdaysEmpTlGenderCnt;
						int intWeekEndEmpGenderCnt1 = intWeekEndEmpGenderCnt;
						int intWeekdaysEmpGenderCnt1 = intWeekdaysEmpGenderCnt;
						int intWeekEndEmpTlCnt1 = intWeekEndEmpTlCnt;
						int intWeekdaysEmpTlCnt1 = intWeekdaysEmpTlCnt;
						if(!alEmpLeaveDates.contains(newDate1) && empGender !=null && empGender.equalsIgnoreCase("M") 
							&& intExistMaleGenderCnt < uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) ) {
							intWeekEndEmpTlGenderCnt1 += (uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) - intExistMaleGenderCnt);
							intWeekdaysEmpTlGenderCnt1 += (uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) - intExistMaleGenderCnt);
							intWeekEndEmpGenderCnt1 += (uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) - intExistMaleGenderCnt);
							intWeekdaysEmpGenderCnt1 += (uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) - intExistMaleGenderCnt);
//							if((j==1 || j==2 || j==29) && uF.parseToInt(newShiftId)==2) {
//								System.out.println(empId +" ADD GENDER CNT --- " + j + " ---------- intWeekEndEmpTlGenderCnt ===>> " + intWeekEndEmpTlGenderCnt +" -- intWeekEndEmpTlCnt ===>> " + intWeekEndEmpTlCnt + " -- intWeekdaysEmpCnt ===>> " +intWeekdaysEmpCnt);
//							}
							if(intWeekEndEmpTlGenderCnt1 > intWeekEndEmpCnt) {
								intWeekEndEmpTlGenderCnt1 = intWeekEndEmpCnt;
							}
							if(intWeekdaysEmpTlGenderCnt1 > intWeekdaysEmpCnt) {
								intWeekdaysEmpTlGenderCnt1 = intWeekdaysEmpCnt;
							}
							if(intWeekEndEmpGenderCnt1 > intWeekEndEmpCnt) {
								intWeekEndEmpGenderCnt1 = intWeekEndEmpCnt;
							}
							if(intWeekdaysEmpGenderCnt1 > intWeekdaysEmpCnt) {
								intWeekdaysEmpGenderCnt1 = intWeekdaysEmpCnt;
							}
						}
						if(!alEmpLeaveDates.contains(newDate1) && alTlLevels !=null && empLevelId!=null && alTlLevels.contains(empLevelId) 
							&& intTlExistCnt < uF.parseToInt(strTlCnt) ) {
							intWeekEndEmpTlGenderCnt1 += (uF.parseToInt(strTlCnt) - intTlExistCnt);
							intWeekdaysEmpTlGenderCnt1 += (uF.parseToInt(strTlCnt) - intTlExistCnt);
							intWeekEndEmpTlCnt1 += (uF.parseToInt(strTlCnt) - intTlExistCnt);
							intWeekdaysEmpTlCnt1 += (uF.parseToInt(strTlCnt) - intTlExistCnt);
//							if((j==1 || j==2 || j==29) && uF.parseToInt(newShiftId)==2) {
//								System.out.println(empId +" ADD TL CNT --- " + j + " ---------- intWeekEndEmpTlGenderCnt1 ===>> " + intWeekEndEmpTlGenderCnt1 +" -- intWeekEndEmpTlCnt1 ===>> " + intWeekEndEmpTlCnt1 + " -- intWeekEndEmpGenderCnt1 ===>> " +intWeekEndEmpGenderCnt1);
//							}
							if(intWeekEndEmpTlGenderCnt1 > intWeekEndEmpCnt) {
								intWeekEndEmpTlGenderCnt1 = intWeekEndEmpCnt;
							}
							if(intWeekdaysEmpTlGenderCnt1 > intWeekdaysEmpCnt) {
								intWeekdaysEmpTlGenderCnt1 = intWeekdaysEmpCnt;
							}
							if(intWeekEndEmpTlCnt1 > intWeekEndEmpCnt) {
								intWeekEndEmpTlCnt1 = intWeekEndEmpCnt;
							}
							if(intWeekdaysEmpTlCnt1 > intWeekdaysEmpCnt) {
								intWeekdaysEmpTlCnt1 = intWeekdaysEmpCnt;
							}
						}
						if(j==0) {
//							System.out.println(empId+" assignShiftsOnBasisOfRulesOFWeeklyOffForOneDay ---------- intExistCnt ===>> " + intExistCnt +" -- intExistMaleGenderCnt ===>> " + intExistMaleGenderCnt + " -- intTlExistCnt ===>> " +intTlExistCnt);
						}
						
						if(!alEmpLeaveDates.contains(newDate1) && intTlExistCnt < uF.parseToInt(strTlCnt) 
								&& intExistMaleGenderCnt < uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) 
								&& ((intExistCnt < intWeekdaysEmpTlGenderCnt1 && alWeekDays.contains(strDay)) 
									|| (intExistCnt < intWeekEndEmpTlGenderCnt1 && alWeekEnds.contains(strDay)) )  
							) {
//								System.out.println(empId + " --- " + j + " --- ============== IF 1 ===========>> ");
								int updateValue = updateRoster(con, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, dblTimeDiff, newShiftId, empId, strEmpServiceId, 0, uF);
								insertUpdateFlag = true;
								if(updateValue == 0) {
//									System.out.println("============== updateValue ===========>> " + updateValue);
									insertRoster(con, empId, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, newShiftId, 0, uF);
								}
						} else if(!alEmpLeaveDates.contains(newDate1) && intTlExistCnt < uF.parseToInt(strTlCnt) 
								&& intExistMaleGenderCnt >= uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) 
								&& ((intExistCnt < intWeekdaysEmpTlCnt1 && alWeekDays.contains(strDay)) 
									|| (intExistCnt < intWeekEndEmpTlCnt1 && alWeekEnds.contains(strDay)))  
							) {
//							System.out.println(empId + " --- " + j + " --- ============== IF 2 ===========>> ");
								int updateValue = updateRoster(con, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, dblTimeDiff, newShiftId, empId, strEmpServiceId, 0, uF);
								insertUpdateFlag = true;
								if(updateValue == 0) {
//											System.out.println("============== updateValue ===========>> " + updateValue);
									insertRoster(con, empId, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, newShiftId, 0, uF);
								}
						} else if(!alEmpLeaveDates.contains(newDate1) && intTlExistCnt >= uF.parseToInt(strTlCnt) 
								&& intExistMaleGenderCnt < uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) 
								&& ((intExistCnt < intWeekdaysEmpGenderCnt1 && alWeekDays.contains(strDay)) 
									|| (intExistCnt < intWeekEndEmpGenderCnt1 && alWeekEnds.contains(strDay))) 
							) {
//							System.out.println(empId + " --- " + j + " --- ============== IF 3 ===========>> ");
								int updateValue = updateRoster(con, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, dblTimeDiff, newShiftId, empId, strEmpServiceId, 0, uF);
								insertUpdateFlag = true;
								if(updateValue == 0) {
//											System.out.println("============== updateValue ===========>> " + updateValue);
									insertRoster(con, empId, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, newShiftId, 0, uF);
								}
						} else if(!alEmpLeaveDates.contains(newDate1) && intTlExistCnt >= uF.parseToInt(strTlCnt) 
								&& intExistMaleGenderCnt >= uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) 
								&& ((intExistCnt < intWeekdaysEmpCnt && alWeekDays.contains(strDay)) 
									|| (intExistCnt < intWeekEndEmpCnt && alWeekEnds.contains(strDay)))
							) {
//							System.out.println(empId + " --- " + j + " --- ============== IF 4 ===========>> ");
								int updateValue = updateRoster(con, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, dblTimeDiff, newShiftId, empId, strEmpServiceId, -1, uF);
								insertUpdateFlag = true;
//								System.out.println("============== updateValue ===========>> " + updateValue);
								if(updateValue == 0) {
									insertRoster(con, empId, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, newShiftId, 0, uF);
								}
						}
						rosterDaysCnt++;
//						System.out.println("rosterDaysCnt --------------------------------------------------- >> " + rosterDaysCnt);
						}
				}
				
				pst = con.prepareStatement("select * from assign_shift_dates where emp_id=? and paycycle_no=? and paycycle_from_date=? and paycycle_to_date=? and org_id=?");
				pst.setInt(1, uF.parseToInt(empId));
				pst.setInt(2, uF.parseToInt(strPayCycle[2]));
				pst.setDate(3, uF.getDateFormat(strPayCycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPayCycle[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(empOrgId));
//				System.out.println("SECOND CALL pst1 ===>> " + pst);
				rst = pst.executeQuery();
				boolean flag = false; 
				while(rst.next()) {
					flag = true;
				}
				rst.close();
				pst.close();
				
				if(!flag && insertUpdateFlag) {
					pst = con.prepareStatement("insert into assign_shift_dates (emp_id,paycycle_no,paycycle_from_date,paycycle_to_date,org_id,added_by,entry_date) values (?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(empId));
					pst.setInt(2, uF.parseToInt(strPayCycle[2]));
					pst.setDate(3, uF.getDateFormat(strPayCycle[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strPayCycle[1], DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(empOrgId));
					pst.setInt(6, uF.parseToInt(strEmpId));
					pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					if(uF.parseToInt(empId) == 125) {
//						System.out.println("SECOND CALL INSERT pst1 ===>> " + pst);
					}
					pst.executeUpdate();
					pst.close();
				}
			}
			rst1.close();
			pst1.close();
//			System.out.println("hmEmpNoInShifts ===>> " + hmEmpNoInShifts);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	
	
	
	/*public void assignShiftsOnBasisOfRulesOFWeeklyOff(Connection con, UtilityFunctions uF, String strDate, String strPaycycle) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		try {
			Map<String, List<String>> hmEmpLastShiftAssignedDate = new HashMap<String, List<String>>();
//			List<String> alEmpIds = new ArrayList<String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date<=? order by paycycle_from_date");
//			sbQuery.append(" and emp_id=193 ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
			System.out.println("pst1 =============== ===>> " + pst);
			rst = pst.executeQuery();
			
			while (rst.next()) {
				List<String> alLastAssignedPaycycleData = new ArrayList<String>();
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("last_assigned_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("paycycle_no"));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_from_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(uF.getDateFormat(rst.getString("paycycle_to_date"), DBDATE, DATE_FORMAT));
				alLastAssignedPaycycleData.add(rst.getString("org_id"));
				
				hmEmpLastShiftAssignedDate.put(rst.getString("emp_id"), alLastAssignedPaycycleData);
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst1 = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			String strRotationOfShift = "";
			while (rst1.next()) {
				strRotationOfShift = rst1.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
			}
			rst1.close();
			pst1.close();
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			int intWeekdaysEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekdaysEmpTlGenderCnt = intWeekdaysEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlGenderCnt;
			
			int intWeekdaysEmpTlCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - uF.parseToInt(strTlCnt));
			intWeekdaysEmpTlCnt = intWeekdaysEmpTlCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpTlCnt;
			
			int intWeekdaysEmpGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) - uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")));
			intWeekdaysEmpGenderCnt = intWeekdaysEmpGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT")) : intWeekdaysEmpGenderCnt;
			
			int intWeekdaysEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT"));
			
			int intWeekEndEmpTlGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - (uF.parseToInt(strTlCnt)+uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT"))));
			intWeekEndEmpTlGenderCnt = intWeekEndEmpTlGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlGenderCnt;
			
			int intWeekEndEmpTlCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - uF.parseToInt(strTlCnt));
			intWeekEndEmpTlCnt = intWeekEndEmpTlCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpTlCnt;
			
			int intWeekEndEmpGenderCnt = (uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) - uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")));
			intWeekEndEmpGenderCnt = intWeekEndEmpGenderCnt<=0 ? uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND")) : intWeekEndEmpGenderCnt;
			
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
			
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
//			Map<String, String> hmEmpNoInShifts = new HashMap<String, String>();
			String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			List<String> alWeekDays = uF.getWeekDays();
			List<String> alWeekEnds = uF.getWeekEnds();
			
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTime(con);
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
//			System.out.println("hmShiftTime ===>> " + hmShiftTime);
			double dblTimeDiff = 0;
			int nOfdays = 0;
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,emp_gender from employee_personal_details epd, employee_official_details eod where eod.emp_id = epd.emp_per_id " +
				" and epd.is_alive=true and joining_date<=? and epd.emp_per_id in (121,122,125,127,128,131,133,134,136,137,140,141,145,146,147,149,152,154,157,162)"); 
			pst1 = con.prepareStatement(sbQuery.toString()); 
			pst1.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			System.out.println("pst1 ===>> " + pst1);
			rst1 = pst1.executeQuery();
			int cnt=0;
			while (rst1.next()) {
				cnt++;
				String empId = rst1.getString("emp_per_id");
				System.out.println("SECOND CALL empId ===>> " + empId);
				String empGender = rst1.getString("emp_gender");
				String empLevelId = hmEmpLevelId.get(empId);
				String[] strPayCycle = CF.getPayCycleDatesOnPaycycleId(con, strPaycycle, empOrgId, CF.getStrTimeZone(), CF, request);
				String strFrmDate =strPayCycle[0];
				String strToDate =strPayCycle[1]; 
				
				List<String> alLastAssignedPaycycleData = hmEmpLastShiftAssignedDate.get(empId);
				if(alLastAssignedPaycycleData != null && !alLastAssignedPaycycleData.isEmpty() && alLastAssignedPaycycleData.size()>0) {
					empOrgId = alLastAssignedPaycycleData.get(4);
					strFrmDate = alLastAssignedPaycycleData.get(2);
					strToDate = alLastAssignedPaycycleData.get(3);
				} 
				String[] strPrevPayCycle = CF.getPayCycleDatesOnPaycycleId(con, (uF.parseToInt(strPaycycle)-1)+"", empOrgId, CF.getStrTimeZone(), CF, request);
				String strPrevFrmDate = strPrevPayCycle[0];
				String strPrevToDate = strPrevPayCycle[1];
				
				
				List<String> alEmpLeaveDates = getEmpApprovedLeaveData(con, uF, empId, strPayCycle[0], strPayCycle[1], empOrgId);
				if(alEmpLeaveDates == null) alEmpLeaveDates = new ArrayList<String>();
				
				Map<String, Map<String, String>> hmRosterAssignedEmpCntData = getRosterAssignedEmpCountData(con, uF, empId, strFrmDate, strToDate, empOrgId, alTlLevels);
				if(cnt==1) {
					System.out.println("hmRosterAssignedEmpCntData ===>> " + hmRosterAssignedEmpCntData);
				}
				Map<String, String> hmExistEmpCnt = hmRosterAssignedEmpCntData.get("EMP_CNT");
				Map<String, String> hmExistEmpGenderwiseCnt = hmRosterAssignedEmpCntData.get("EMP_GENDERWISE_CNT");
				Map<String, String> hmExistTlEmpCnt = hmRosterAssignedEmpCntData.get("TL_EMP_CNT");
				
				String strEmpServiceId = hmEmpServiceId.get(empId) != null ? hmEmpServiceId.get(empId).substring(1, hmEmpServiceId.get(empId).length()-1) : "0";
				Map<String, String> hmEmpLastMonthRoster = getEmpLastMonthRosterData(con, uF, empId, strPrevFrmDate, strPrevToDate, strEmpServiceId, rotFirst, rotSecond, rotThird);
				
				
				nOfdays = uF.parseToInt(uF.dateDifference(strPayCycle[0], DATE_FORMAT, strPayCycle[1], DATE_FORMAT, CF.getStrTimeZone()));
				String strExistShiftId  = hmEmpLastMonthRoster.get("LASTSHIFT_ID"); //innerList.get(1);
				
//				System.out.println(empId + "nOfdays ===>> " + nOfdays +" --- strExistShiftId ===>> " + strExistShiftId);
				String strFromDate = strPayCycle[0];
				String newShiftId = "";
				if(!rotFirst.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotFirst) && uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))<=3 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))>=8 || uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))==0)) {
					newShiftId = rotSecond;
				} else if(!rotSecond.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotSecond) && uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))<=3 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))>=8 || uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))==0)) {
					newShiftId = rotThird;
				} else if(!rotThird.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotThird) && uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))<=3 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))>=8 || uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))==0)) {
					newShiftId = rotFirst;
				} else {
//					newShiftId = rotFirst;
				}
				
				boolean insertUpdateFlag = false;
//				System.out.println(empId + " newShiftId ===>> " + newShiftId + " --- strEmpServiceId ===>> " + strEmpServiceId);
				Map<String, String> shiftMap = hmShiftTime.get(newShiftId);
				if(shiftMap == null) shiftMap = new HashMap<String, String>();
				
				String strShiftFrom = shiftMap.get("FROM");
				String strShiftTo = shiftMap.get("TO");
				if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
					dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
					int rosterDaysCnt = 0;
					for(int j=0; j<nOfdays; j++) {
						
						if(rosterDaysCnt == 15) {
//							System.out.println("newShiftId ========================== ===>> " + newShiftId);
							if(!rotFirst.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotFirst)) {
								newShiftId = rotSecond;
							} else if(!rotSecond.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotSecond)) {
								newShiftId = rotThird;
							} else if(!rotThird.equals("") && uF.parseToInt(newShiftId) == uF.parseToInt(rotThird)) {
								newShiftId = rotFirst;
							}
							rosterDaysCnt = 0;
							shiftMap = hmShiftTime.get(newShiftId);
							if(shiftMap == null) shiftMap = new HashMap<String, String>();
							
							strShiftFrom = shiftMap.get("FROM");
							strShiftTo = shiftMap.get("TO");
//							System.out.println("newShiftId after ==================== ===>> " + newShiftId);
						}
						
						String newDate1 = getDate(strFromDate, DATE_FORMAT, j);
						String strDay = uF.getDateFormat(newDate1, DATE_FORMAT, "E");
						strDay = strDay.toUpperCase();

						int intExistCnt = uF.parseToInt(hmExistEmpCnt.get(newShiftId+"_"+newDate1));
						int intExistMaleGenderCnt = uF.parseToInt(hmExistEmpGenderwiseCnt.get(newShiftId+"_"+newDate1+"_M"));
						int intTlExistCnt = uF.parseToInt(hmExistTlEmpCnt.get(newShiftId+"_"+newDate1));
						
						int intWeekEndEmpTlGenderCnt1 = intWeekEndEmpTlGenderCnt;
						int intWeekdaysEmpTlGenderCnt1 = intWeekdaysEmpTlGenderCnt;
						int intWeekEndEmpGenderCnt1 = intWeekEndEmpGenderCnt;
						int intWeekdaysEmpGenderCnt1 = intWeekdaysEmpGenderCnt;
						int intWeekEndEmpTlCnt1 = intWeekEndEmpTlCnt;
						int intWeekdaysEmpTlCnt1 = intWeekdaysEmpTlCnt;
						if(!alEmpLeaveDates.contains(newDate1) && empGender !=null && empGender.equalsIgnoreCase("M") 
							&& intExistMaleGenderCnt < uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) ) {
							intWeekEndEmpTlGenderCnt1 += (uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) - intExistMaleGenderCnt);
							intWeekdaysEmpTlGenderCnt1 += (uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) - intExistMaleGenderCnt);
							intWeekEndEmpGenderCnt1 += (uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) - intExistMaleGenderCnt);
							intWeekdaysEmpGenderCnt1 += (uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) - intExistMaleGenderCnt);
//							if((j==1 || j==2 || j==29) && uF.parseToInt(newShiftId)==2) {
//								System.out.println(empId +" ADD GENDER CNT --- " + j + " ---------- intWeekEndEmpTlGenderCnt ===>> " + intWeekEndEmpTlGenderCnt +" -- intWeekEndEmpTlCnt ===>> " + intWeekEndEmpTlCnt + " -- intWeekdaysEmpCnt ===>> " +intWeekdaysEmpCnt);
//							}
							if(intWeekEndEmpTlGenderCnt1 > intWeekEndEmpCnt) {
								intWeekEndEmpTlGenderCnt1 = intWeekEndEmpCnt;
							}
							if(intWeekdaysEmpTlGenderCnt1 > intWeekdaysEmpCnt) {
								intWeekdaysEmpTlGenderCnt1 = intWeekdaysEmpCnt;
							}
							if(intWeekEndEmpGenderCnt1 > intWeekEndEmpCnt) {
								intWeekEndEmpGenderCnt1 = intWeekEndEmpCnt;
							}
							if(intWeekdaysEmpGenderCnt1 > intWeekdaysEmpCnt) {
								intWeekdaysEmpGenderCnt1 = intWeekdaysEmpCnt;
							}
						}
						if(!alEmpLeaveDates.contains(newDate1) && alTlLevels !=null && empLevelId!=null && alTlLevels.contains(empLevelId) 
							&& intTlExistCnt < uF.parseToInt(strTlCnt) ) {
							intWeekEndEmpTlGenderCnt1 += (uF.parseToInt(strTlCnt) - intTlExistCnt);
							intWeekdaysEmpTlGenderCnt1 += (uF.parseToInt(strTlCnt) - intTlExistCnt);
							intWeekEndEmpTlCnt1 += (uF.parseToInt(strTlCnt) - intTlExistCnt);
							intWeekdaysEmpTlCnt1 += (uF.parseToInt(strTlCnt) - intTlExistCnt);
//							if((j==1 || j==2 || j==29) && uF.parseToInt(newShiftId)==2) {
//								System.out.println(empId +" ADD TL CNT --- " + j + " ---------- intWeekEndEmpTlGenderCnt1 ===>> " + intWeekEndEmpTlGenderCnt1 +" -- intWeekEndEmpTlCnt1 ===>> " + intWeekEndEmpTlCnt1 + " -- intWeekEndEmpGenderCnt1 ===>> " +intWeekEndEmpGenderCnt1);
//							}
							if(intWeekEndEmpTlGenderCnt1 > intWeekEndEmpCnt) {
								intWeekEndEmpTlGenderCnt1 = intWeekEndEmpCnt;
							}
							if(intWeekdaysEmpTlGenderCnt1 > intWeekdaysEmpCnt) {
								intWeekdaysEmpTlGenderCnt1 = intWeekdaysEmpCnt;
							}
							if(intWeekEndEmpTlCnt1 > intWeekEndEmpCnt) {
								intWeekEndEmpTlCnt1 = intWeekEndEmpCnt;
							}
							if(intWeekdaysEmpTlCnt1 > intWeekdaysEmpCnt) {
								intWeekdaysEmpTlCnt1 = intWeekdaysEmpCnt;
							}
						}
						if(j==0) {
//							System.out.println(empId+" ---------- intExistCnt ===>> " + intExistCnt +" -- intExistMaleGenderCnt ===>> " + intExistMaleGenderCnt + " -- intTlExistCnt ===>> " +intTlExistCnt);
						}
						
						if(!alEmpLeaveDates.contains(newDate1) && intTlExistCnt < uF.parseToInt(strTlCnt) 
								&& intExistMaleGenderCnt < uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) 
								&& ((intExistCnt < intWeekdaysEmpTlGenderCnt1 && alWeekDays.contains(strDay)) 
									|| (intExistCnt < intWeekEndEmpTlGenderCnt1 && alWeekEnds.contains(strDay)) )  
							) {
//								System.out.println(empId + " --- " + j + " --- ============== IF 1 ===========>> ");
								int updateValue = updateRoster(con, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, dblTimeDiff, newShiftId, empId, strEmpServiceId, 0, uF);
								insertUpdateFlag = true;
								if(updateValue == 0) {
//									System.out.println("============== updateValue ===========>> " + updateValue);
									insertRoster(con, empId, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, newShiftId, 0, uF);
								}
						} else if(!alEmpLeaveDates.contains(newDate1) && intTlExistCnt < uF.parseToInt(strTlCnt) 
								&& intExistMaleGenderCnt >= uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) 
								&& ((intExistCnt < intWeekdaysEmpTlCnt1 && alWeekDays.contains(strDay)) 
									|| (intExistCnt < intWeekEndEmpTlCnt1 && alWeekEnds.contains(strDay)))  
							) {
//							System.out.println(empId + " --- " + j + " --- ============== IF 2 ===========>> ");
								int updateValue = updateRoster(con, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, dblTimeDiff, newShiftId, empId, strEmpServiceId, 0, uF);
								insertUpdateFlag = true;
								if(updateValue == 0) {
//											System.out.println("============== updateValue ===========>> " + updateValue);
									insertRoster(con, empId, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, newShiftId, 0, uF);
								}
						} else if(!alEmpLeaveDates.contains(newDate1) && intTlExistCnt >= uF.parseToInt(strTlCnt) 
								&& intExistMaleGenderCnt < uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) 
								&& ((intExistCnt < intWeekdaysEmpGenderCnt1 && alWeekDays.contains(strDay)) 
									|| (intExistCnt < intWeekEndEmpGenderCnt1 && alWeekEnds.contains(strDay))) 
							) {
//							System.out.println(empId + " --- " + j + " --- ============== IF 3 ===========>> ");
								int updateValue = updateRoster(con, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, dblTimeDiff, newShiftId, empId, strEmpServiceId, 0, uF);
								insertUpdateFlag = true;
								if(updateValue == 0) {
//											System.out.println("============== updateValue ===========>> " + updateValue);
									insertRoster(con, empId, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, newShiftId, 0, uF);
								}
						} else if(!alEmpLeaveDates.contains(newDate1) && intTlExistCnt >= uF.parseToInt(strTlCnt) 
								&& intExistMaleGenderCnt >= uF.parseToInt(hmRosterPolicyRulesData.get("MIN_MALE_MEMBER_IN_SHIFT")) 
								&& ((intExistCnt < intWeekdaysEmpCnt && alWeekDays.contains(strDay)) 
									|| (intExistCnt < intWeekEndEmpCnt && alWeekEnds.contains(strDay)))
							) {
//							System.out.println(empId + " --- " + j + " --- ============== IF 4 ===========>> ");
								int updateValue = updateRoster(con, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, dblTimeDiff, newShiftId, empId, strEmpServiceId, 0, uF);
								insertUpdateFlag = true;
								if(updateValue == 0) {
//								System.out.println("============== updateValue ===========>> " + updateValue);
									insertRoster(con, empId, strFromDate, DATE_FORMAT, j, strShiftFrom, strShiftTo, strEmpServiceId, dblTimeDiff, newShiftId, 0, uF);
								}
						}
						rosterDaysCnt++;
//						System.out.println("rosterDaysCnt --------------------------------------------------- >> " + rosterDaysCnt);
						}
					
				}
				
				pst = con.prepareStatement("select * from assign_shift_dates where emp_id=? and paycycle_no=? and paycycle_from_date=? and paycycle_to_date=? and org_id=?");
				pst.setInt(1, uF.parseToInt(empId));
				pst.setInt(2, uF.parseToInt(strPayCycle[2]));
				pst.setDate(3, uF.getDateFormat(strPayCycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPayCycle[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(empOrgId));
//				System.out.println("SECOND CALL pst1 ===>> " + pst);
				rst = pst.executeQuery();
				boolean flag = false; 
				while(rst.next()) {
					flag = true;
				}
				rst.close();
				pst.close();
				
				if(!flag && insertUpdateFlag) {
					pst = con.prepareStatement("insert into assign_shift_dates (emp_id,paycycle_no,paycycle_from_date,paycycle_to_date,org_id,added_by,entry_date) values (?,?,?,?, ?,?,?)");
					pst.setInt(1, uF.parseToInt(empId));
					pst.setInt(2, uF.parseToInt(strPayCycle[2]));
					pst.setDate(3, uF.getDateFormat(strPayCycle[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strPayCycle[1], DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(empOrgId));
					pst.setInt(6, uF.parseToInt(strEmpId));
					pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					System.out.println("SECOND CALL INSERT pst1 ===>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			rst1.close();
			pst1.close();
			
//			System.out.println("hmEmpNoInShifts ===>> " + hmEmpNoInShifts);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	
	
	private Map<String, List<String>> getAssignedWeekOffData(Connection con, UtilityFunctions uF, String strFromDate, String strToDate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, List<String>> hmAssignedWeekOffData = new HashMap<String, List<String>>();
		try {
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select emp_id,weekoff_date from roster_weekly_off where weekoff_date between ? and ? order by weekoff_date,emp_id");
			sbQuery.append("select rwo.emp_id,rwo.weekoff_date, rd.* from roster_weekly_off rwo, roster_details rd where rwo.emp_id=rd.emp_id " +
				"and rwo.weekoff_date = rd._date and rwo.weekoff_date between ? and ? and rd.roster_weeklyoff_id=rwo.roster_weeklyoff_id " +
				"order by rwo.weekoff_date,rwo.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFromDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strToDate, DATE_FORMAT));
//			System.out.println("assigned roster pst ====>> " + pst); 
			rs=pst.executeQuery();
			while(rs.next()) {
				List<String> innerList = hmAssignedWeekOffData.get(uF.getDateFormat(rs.getString("weekoff_date"), DBDATE, DATE_FORMAT));
				if(innerList==null) innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_id"));
				
				hmAssignedWeekOffData.put(uF.getDateFormat(rs.getString("weekoff_date"), DBDATE, DATE_FORMAT), innerList);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return hmAssignedWeekOffData;
	}
	

	private Map<String, Map<String, String>> getRosterAssignedEmpCountData(Connection con, UtilityFunctions uF, String empId, String strFromDate, String strToDate, String orgId, List<String> alTlLevels) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmRosterAssignedEmpCntData = new HashMap<String, Map<String, String>>();
		try {
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, String> hmShiftAssignedEmpCountDatewise = new LinkedHashMap<String, String>();
			Map<String, String> hmShiftAssignedEmpCountGenderwiseDatewise = new LinkedHashMap<String, String>();
			Map<String, String> hmShiftAssignedTlCountLevelwiseDatewise = new LinkedHashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select emp_per_id,emp_gender,rd._date,rd.shift_id from employee_personal_details epd, employee_official_details eod, roster_details rd " +
					"where eod.emp_id = epd.emp_per_id and epd.is_alive=true and joining_date<=? and eod.emp_id = rd.emp_id and rd._date between ? and ? and rd.roster_weeklyoff_id=0 order by shift_id,_date,rd.emp_id");
//			sbQuery.append("select count(emp_id) as empCnt, _date from roster_details where _date between ? and ? group by _date order by _date");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getDateFormat(strFromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strToDate, DATE_FORMAT));
//			System.out.println("assigned roster pst ====>> " + pst); 
			rs=pst.executeQuery();
			while(rs.next()) {
				String empLevelId = hmEmpLevelId.get(rs.getString("emp_per_id"));
				int intCnt = uF.parseToInt(hmShiftAssignedEmpCountDatewise.get(rs.getString("shift_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)));
				intCnt++;
				hmShiftAssignedEmpCountDatewise.put(rs.getString("shift_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), intCnt+"");
				
				int intGenderCnt = uF.parseToInt(hmShiftAssignedEmpCountGenderwiseDatewise.get(rs.getString("shift_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rs.getString("emp_gender")));
				intGenderCnt++;
				hmShiftAssignedEmpCountGenderwiseDatewise.put(rs.getString("shift_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rs.getString("emp_gender"), intGenderCnt+"");
				
				if(alTlLevels.contains(empLevelId)) {
					int intTlEmpLevelCnt = uF.parseToInt(hmShiftAssignedTlCountLevelwiseDatewise.get(rs.getString("shift_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)));
					intTlEmpLevelCnt++;
					hmShiftAssignedTlCountLevelwiseDatewise.put(rs.getString("shift_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), intTlEmpLevelCnt+"");
				}
			}
			rs.close();
			pst.close();
				
			hmRosterAssignedEmpCntData.put("EMP_CNT", hmShiftAssignedEmpCountDatewise);
			hmRosterAssignedEmpCntData.put("EMP_GENDERWISE_CNT", hmShiftAssignedEmpCountGenderwiseDatewise);
			hmRosterAssignedEmpCntData.put("TL_EMP_CNT", hmShiftAssignedTlCountLevelwiseDatewise);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return hmRosterAssignedEmpCntData;
	}

	
	private Map<String, List<String>> getEmpApprovedLeaveDataEmpwise(Connection con, UtilityFunctions uF, String strFromDate, String strToDate, String orgId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, List<String>> alDatewiseEmpLeaveData = new HashMap<String, List<String>>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select lar.* from emp_leave_entry ele, leave_type lt, leave_application_register lar where ele.leave_id=lar.leave_id and " +
				"ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and encashment_status=false and is_approved=1 "); //and ele.emp_id in("+strEmpIds+")
			sbQuery.append(" and lar.is_paid = true and lar.is_modify= false and ele.leave_id in (select leave_id from leave_application_register where leave_id > 0 ");
			if(strFromDate!=null && !strFromDate.equals("") && !strFromDate.equalsIgnoreCase("null")) {
				sbQuery.append(" and _date between '"+uF.getDateFormat(strFromDate, DATE_FORMAT, DBDATE)+"' " +
					"and '"+uF.getDateFormat(strToDate, DATE_FORMAT, DBDATE)+"' ");	
			}
			sbQuery.append(") order by emp_id,_date");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("LEAVE pst ====>> " + pst); 
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> innerList = alDatewiseEmpLeaveData.get(rs.getString("emp_id"));
				if(innerList==null) innerList = new ArrayList<String>();
				innerList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
				
				alDatewiseEmpLeaveData.put(rs.getString("emp_id"), innerList);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return alDatewiseEmpLeaveData;
	}
	
	
	private Map<String, List<String>> getEmpApprovedLeaveDataDatewise(Connection con, UtilityFunctions uF, String strFromDate, String strToDate, String orgId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, List<String>> alDatewiseEmpLeaveData = new HashMap<String, List<String>>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select lar.* from emp_leave_entry ele, leave_type lt, leave_application_register lar where ele.leave_id=lar.leave_id and " +
				"ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and encashment_status=false and is_approved=1 "); //and ele.emp_id in("+strEmpIds+")
			sbQuery.append(" and lar.is_modify= false and ele.leave_id in (select leave_id from leave_application_register where leave_id > 0 "); // and lar.is_paid = true
			if(strFromDate!=null && !strFromDate.equals("") && !strFromDate.equalsIgnoreCase("null")) {
				sbQuery.append(" and _date between '"+uF.getDateFormat(strFromDate, DATE_FORMAT, DBDATE)+"' " +
					"and '"+uF.getDateFormat(strToDate, DATE_FORMAT, DBDATE)+"' ");	
			}
			sbQuery.append(") order by emp_id,_date"); 
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("LEAVE pst ====>> " + pst); 
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> innerList = alDatewiseEmpLeaveData.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
				if(innerList==null) innerList = new ArrayList<String>();
				innerList.add(rs.getString("emp_id"));
				
				alDatewiseEmpLeaveData.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), innerList);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return alDatewiseEmpLeaveData;
	}
	
	
	private List<String> getEmpApprovedLeaveData(Connection con, UtilityFunctions uF, String empId, String strFromDate, String strToDate, String orgId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> alEmpLeaveDates = new ArrayList<String>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select lar.* from emp_leave_entry ele, leave_type lt, leave_application_register lar where ele.leave_id=lar.leave_id and " +
				"ele.leave_type_id=lt.leave_type_id and ele.leave_type_id > 0 and encashment_status=false and is_approved=1 and lar.emp_id=? "); //and ele.emp_id in("+strEmpIds+")
			sbQuery.append(" and lar.is_paid = true and lar.is_modify= false and ele.leave_id in (select leave_id from leave_application_register where leave_id > 0 ");
			if(strFromDate!=null && !strFromDate.equals("") && !strFromDate.equalsIgnoreCase("null")) {
				sbQuery.append(" and _date between '"+uF.getDateFormat(strFromDate, DATE_FORMAT, DBDATE)+"' " +
					"and '"+uF.getDateFormat(strToDate, DATE_FORMAT, DBDATE)+"' ");	
			}
			sbQuery.append(") order by emp_id,_date");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(empId));
//			System.out.println("LEAVE pst ====>> " + pst); 
			rs = pst.executeQuery();
			while(rs.next()) {
				alEmpLeaveDates.add(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return alEmpLeaveDates;
	}
	
	
	private String getEmpAssignedShiftIdForDate(Connection con, UtilityFunctions uF, String empId, String strDate, String strEmpServiceId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String strShiftId = null;
		try {

			pst = con.prepareStatement("select _date, shift_id from roster_details where emp_id=? and _date=? and service_id=? and roster_weeklyoff_id=0");
			pst.setInt(1, uF.parseToInt(empId));
			pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpServiceId));
			rs = pst.executeQuery();
			while(rs.next()) {
				strShiftId = rs.getString("shift_id");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return strShiftId;
	}
	
	
	private int getEmpShiftAssignOrNotForDate(Connection con, UtilityFunctions uF, String empId, String strDate, String strEmpServiceId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int intCnt = 0;
		try {

			pst = con.prepareStatement("select _date, shift_id from roster_details where emp_id=? and _date=? and service_id=? and roster_weeklyoff_id=0");
			pst.setInt(1, uF.parseToInt(empId));
			pst.setDate(2, uF.getDateFormat(strDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpServiceId));
			rs = pst.executeQuery();
			while(rs.next()) {
				intCnt=1;
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return intCnt;
	}
	
	private int getEmpCurrMonthRosterDayCnt(Connection con, UtilityFunctions uF, String empId, String strPaycycleFromDate, String strPaycycleToDate, String strEmpServiceId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int dayCount=0;
		try {

			pst = con.prepareStatement("select count(_date) as dayCnt from roster_details where emp_id=? and _date between ? and ? and service_id=? and roster_weeklyoff_id=0");
			pst.setInt(1, uF.parseToInt(empId));
			pst.setDate(2, uF.getDateFormat(strPaycycleFromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strEmpServiceId));
			rs = pst.executeQuery();
			while(rs.next()) {
				dayCount = rs.getInt("dayCnt");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return dayCount;
	}
	
	
	private Map<String, List<String>> getDatewiseShiftwiseTlData(Connection con, UtilityFunctions uF, String strPaycycleFromDate, String strPaycycleToDate, List<String> alTlLevels, Map<String, String> hmEmpLevelId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, List<String>> hmDatewiseData = new LinkedHashMap<String, List<String>>();
		try {
//			pst = con.prepareStatement("select count(shift_id) as cnt, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? group by shift_id order by _date");
			pst = con.prepareStatement("select _date, shift_id,emp_id from roster_details where _date between ? and ? and roster_weeklyoff_id=0 order by _date");
			pst.setDate(1, uF.getDateFormat(strPaycycleFromDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				String newDate1 = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
				String newShiftId = rs.getString("shift_id");
				String empId = rs.getString("emp_id");
				String strEmpLvl = hmEmpLevelId.get(empId);
				if(alTlLevels.contains(strEmpLvl)) {
					List<String> innerList = hmDatewiseData.get(newDate1+"_"+newShiftId);
					if(innerList == null) innerList = new ArrayList<String>();
					innerList.add(empId);
					hmDatewiseData.put(newDate1+"_"+newShiftId, innerList);
				}
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return hmDatewiseData;
	}
	
	
	private Map<String, Map<String, String>> getEmpwiseCurrMonthRosterData(Connection con, UtilityFunctions uF, String strPaycycleFromDate, String strPaycycleToDate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmEmpRosterData = new LinkedHashMap<String, Map<String, String>>();
		try {
//			pst = con.prepareStatement("select count(shift_id) as cnt, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? group by shift_id order by _date");
			pst = con.prepareStatement("select emp_id,_date, shift_id from roster_details where _date between ? and ? and roster_weeklyoff_id=0 order by emp_id,_date");
//			pst.setInt(1, uF.parseToInt(empId));
			pst.setDate(1, uF.getDateFormat(strPaycycleFromDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
//			pst.setInt(4, uF.parseToInt(strEmpServiceId));
			rs = pst.executeQuery();
			while(rs.next()) {
				Map<String, String> hmInner = hmEmpRosterData.get(rs.getString("emp_id"));
				if(hmInner==null) hmInner = new LinkedHashMap<String, String>();
				
				hmInner.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("shift_id"));
				
				hmEmpRosterData.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return hmEmpRosterData;
	}
	
	
	private Map<String, String> getEmpCurrMonthRosterData(Connection con, UtilityFunctions uF, String empId, String strPaycycleFromDate, String strPaycycleToDate, String strEmpServiceId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmEmpRosterData = new HashMap<String, String>();
		try {
//			pst = con.prepareStatement("select count(shift_id) as cnt, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? group by shift_id order by _date");
			pst = con.prepareStatement("select _date, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? and roster_weeklyoff_id=0 order by _date");
			pst.setInt(1, uF.parseToInt(empId));
			pst.setDate(2, uF.getDateFormat(strPaycycleFromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strEmpServiceId));
			rs = pst.executeQuery();
			if(uF.parseToInt(empId) == 145) {
//				System.out.println("pst ===>> " + pst);
			}
			while(rs.next()) {
				hmEmpRosterData.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("shift_id"));
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(empId) == 145) {
//				System.out.println(empId + " --- hmEmpLastRosterData ===============>> " + hmEmpLastRosterData + " --- hmEmpRosterData ===============>> " + hmEmpRosterData);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return hmEmpRosterData;
	}
	
	
	private Map<String, String> getEmpLastMonthRosterData(Connection con, UtilityFunctions uF, String empId, String strPaycycleFromDate, String strPaycycleToDate, String strEmpServiceId, String rotFirst, String rotSecond, String rotThird) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmEmpRosterData = new HashMap<String, String>();
		try {

			String strShifts = rotFirst+","+rotSecond+","+rotThird;
			Date strLast15DaysShift = uF.getFutureDate(uF.getDateFormatUtil(strPaycycleFromDate, DATE_FORMAT), 15);
			Date strLast7DaysShift = uF.getFutureDate(uF.getDateFormatUtil(strPaycycleToDate, DATE_FORMAT), -7);
//			pst = con.prepareStatement("select count(shift_id) as cnt, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? group by shift_id order by _date");
			pst = con.prepareStatement("select _date, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? and shift_id in ("+strShifts+") and roster_weeklyoff_id=0 order by _date");
			pst.setInt(1, uF.parseToInt(empId));
			pst.setDate(2, uF.getDateFormat(strPaycycleFromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strEmpServiceId));
			rs = pst.executeQuery();
			if(uF.parseToInt(empId) == 170 || uF.parseToInt(empId) == 166) {
//				System.out.println("pst ===>> " + pst);
			}
			Map<String, String> hmEmpLastRosterData = new HashMap<String, String>();
			Map<String, String> hmEmpLast7DaysRosterData = new HashMap<String, String>();
			while(rs.next()) {
				int rosterCnt = uF.parseToInt(hmEmpRosterData.get(rs.getString("shift_id")));
				rosterCnt++;
				hmEmpRosterData.put(rs.getString("shift_id"), rosterCnt+"");
				if(strLast15DaysShift.before(uF.getDateFormatUtil(rs.getString("_date"), DBDATE))) {
					int lastRosterCnt = uF.parseToInt(hmEmpLastRosterData.get(rs.getString("shift_id")));
					lastRosterCnt++;
					hmEmpLastRosterData.put(rs.getString("shift_id"), lastRosterCnt+"");
				}
				if(strLast7DaysShift.before(uF.getDateFormatUtil(rs.getString("_date"), DBDATE))) {
					int lastRosterCnt = uF.parseToInt(hmEmpLast7DaysRosterData.get(rs.getString("shift_id")));
					lastRosterCnt++;
					hmEmpLast7DaysRosterData.put(rs.getString("shift_id"), lastRosterCnt+"");
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println(empId + " -- hmEmpRosterData ===>> " + hmEmpRosterData +" -- hmEmpLastRosterData ===>> " + hmEmpLastRosterData);
			int shiftCnt = 0;
			String strShiftId = null;
			Iterator<String> it = hmEmpLastRosterData.keySet().iterator();
			while(it.hasNext()) {
				String shiftId = it.next();
				int intCnt = uF.parseToInt(hmEmpLastRosterData.get(shiftId));
				int int7DayCnt = uF.parseToInt(hmEmpLast7DaysRosterData.get(shiftId));
				if(shiftCnt==0) {
					shiftCnt = intCnt;
					strShiftId = shiftId;
				} else if(shiftCnt<intCnt) {
					shiftCnt = intCnt;
					strShiftId = shiftId;
				} else if(shiftCnt==intCnt && intCnt==int7DayCnt) {
					shiftCnt = intCnt;
					strShiftId = shiftId;
				}
			}
			hmEmpRosterData.put("LASTSHIFT_ID", strShiftId); 
			
			if(uF.parseToInt(empId) == 145) {
//				System.out.println(empId + " --- hmEmpLastRosterData ===============>> " + hmEmpLastRosterData + " --- hmEmpRosterData ===============>> " + hmEmpRosterData);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return hmEmpRosterData;
	}
	

	/*private void deleteRosterWeelyOff(Connection con, UtilityFunctions uF, String strFromDate, String strDateformat, int valueOfJ, String strEmpId,
		String costCenterName, int nRosterWOff, Map<String, Map<String, String>> hmRosterWeeklyoff, String strShiftId) {
		PreparedStatement pst=null;
		try {
			pst = con.prepareStatement("delete from roster_weekly_off where emp_id=? and weekoff_date=? and service_id =?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
			pst.setInt(3, uF.parseToInt(costCenterName));
			pst.execute();	
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}*/
	
	
	private void insertUpdateRosterWeekendWeeklyOff(Connection con, UtilityFunctions uF, String strPaycycle, String strFromDate, String strWeekOffDate, 
			String strEmpId, String strEmpServiceId, String strShiftId) {
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				
				Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con, CF, uF);
				if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
				
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strFromDate, strWeekOffDate, DATE_FORMAT);
				String strDay = uF.getDateFormat(strWeekOffDate, DATE_FORMAT, "EEEE");
				if(strDay!=null) strDay = strDay.toUpperCase();
				
				String rosterWOffId = null;
				Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
				while(it.hasNext()) {
					String strRosterWOffId = it.next();
					Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
					List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
					List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
//						System.out.println("weeklyOffDayList ============>> " + weeklyOffDayList + " --- weekNoList =====>> " + weekNoList);
					if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
						rosterWOffId = strRosterWOffId;
					}
				}
				pst = con.prepareStatement("update roster_weekly_off set roster_weeklyoff_id=?, shift_id=? where emp_id=? and weekoff_date=? and service_id =?");
				pst.setInt(1, uF.parseToInt(rosterWOffId));
				pst.setInt(2, uF.parseToInt(strShiftId));
				pst.setInt(3, uF.parseToInt(strEmpId));
				pst.setDate(4,  uF.getDateFormat(strWeekOffDate, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strEmpServiceId));
				int x = pst.executeUpdate();
//				System.out.println("weekoff update pst ===>> " + pst);
				pst.close();
				if (x == 0) {
					pst = con.prepareStatement("insert into roster_weekly_off (emp_id, weekoff_date, service_id, roster_weeklyoff_id, shift_id) values(?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2,  uF.getDateFormat(strWeekOffDate, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strEmpServiceId));
					pst.setInt(4, uF.parseToInt(rosterWOffId));
					pst.setInt(5, uF.parseToInt(strShiftId));
					pst.execute();
//					System.out.println("pst insert into roster_weekly_off ===>> " + pst);
					pst.close();
				}
				
				pst = con.prepareStatement("update roster_details set roster_weeklyoff_id=? where emp_id=? and _date=? and service_id=?"); 
				pst.setInt(1, uF.parseToInt(rosterWOffId));
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setDate(3,  uF.getDateFormat(strWeekOffDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strEmpServiceId));
				pst.executeUpdate();
//				System.out.println("pst update roster_details ===>> " + pst);
				pst.close();
					
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(rs !=null) {
					try {
						rs.close();
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
		}
	
	
	
	/*private List<String> insertUpdateRosterWeekendWeeklyOff(Connection con, UtilityFunctions uF, String strPaycycle, String strFromDate, String strDateformat, int valueOfJ, 
		String strEmpId, String strEmpServiceId, int weekendWeekOffCnt, String lastDate, int strechWeekOffCnt, int intDaysCnt, 
		int normalWeekOffCnt, int insertWeekOffCnt, int firstWeekEndCnt, int secWeekEndCnt, int thirdWeekEndCnt, int fourthWeekEndCnt, int fifthWeekEndCnt, 
		Map<String, String> hmExistEmpCnt, Map<String, String> hmExistEmpGenderwiseCnt, Map<String, String> hmExistTlEmpCnt) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rst1 = null;
		List<String> alReturnData = new ArrayList<String>();
		try {
			
			String strShiftId = null;
			pst = con.prepareStatement("select shift_id from roster_details where emp_id=? and _date=? and service_id =?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
			pst.setInt(3, uF.parseToInt(strEmpServiceId));
			rs = pst.executeQuery();
			while(rs.next()) {
				strShiftId = rs.getString("shift_id");
			}
			rs.close();
			pst.close();
			
			List<String> alWeekDays = uF.getWeekDaysFullName();
			List<String> alWeekEnds = uF.getWeekEndsFullName();
			
			Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con, CF, uF);
			if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
			
			String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strFromDate, getDate(strFromDate, strDateformat, valueOfJ), strDateformat);
			String strDay = uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat, "EEEE");
			if(strDay!=null) strDay = strDay.toUpperCase();
			if(uF.parseToInt(strEmpId) == 125) {
//				System.out.println("insertUpdateRosterWeeklyOff strDay ===>> " + strDay + " --- valueOfJ ===>> " + valueOfJ);
			}
			boolean weekEndFlag = false;
			if(weekendWeekOffCnt < 4 && alWeekEnds.contains(strDay)) {
				if(valueOfJ>=0 && valueOfJ<=6) {
					weekEndFlag = true;
					firstWeekEndCnt++;
					weekendWeekOffCnt++;
				} else if(firstWeekEndCnt==0 && valueOfJ>=7 && valueOfJ<=13) {
					weekEndFlag = true;
					secWeekEndCnt++;
					weekendWeekOffCnt++;
				} else if(firstWeekEndCnt==2 && valueOfJ>=14 && valueOfJ<=20) {
					weekEndFlag = true;
					thirdWeekEndCnt++;
					weekendWeekOffCnt++;
				} else if(secWeekEndCnt==2 && valueOfJ>=21 && valueOfJ<=27) {
					weekEndFlag = true;
					fourthWeekEndCnt++;
					weekendWeekOffCnt++;
				} else if(firstWeekEndCnt==2 && thirdWeekEndCnt==2 && valueOfJ>=28 && valueOfJ<=30) {
					weekEndFlag = true;
					fifthWeekEndCnt++;
					weekendWeekOffCnt++;
				}
			}
			
			if(uF.parseToInt(strEmpId) == 125) {
//				System.out.println("weekendWeekOffCnt ============>> " + weekendWeekOffCnt);
			}
			boolean insertFlag = false;
			String rosterWOffId = null;
			if(weekEndFlag) {
				Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
				while(it.hasNext()) {
					String strRosterWOffId = it.next();
					Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
					List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
					List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
//					System.out.println("weeklyOffDayList ============>> " + weeklyOffDayList + " --- weekNoList =====>> " + weekNoList);
					if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
						rosterWOffId = strRosterWOffId;
						insertFlag = true;
					}
				}
			} else {
				if(lastDate != null) {
					int nOfDaysDiff = uF.parseToInt(uF.dateDifference(lastDate, DATE_FORMAT, getDate(strFromDate, strDateformat, valueOfJ), DATE_FORMAT, CF.getStrTimeZone()));
					if(uF.parseToInt(strEmpId) == 125) {
					System.out.println("lastDate ===>> " + lastDate+ " -- nOfDaysDiff ===>> " + nOfDaysDiff + " -- normalWeekOffCnt ===>> " + normalWeekOffCnt + " -- strechWeekOffCnt ===>> " + strechWeekOffCnt);
					}
					if(nOfDaysDiff == 2 || (nOfDaysDiff == 3 && insertWeekOffCnt==1)) {
						intDaysCnt++;
						if(uF.parseToInt(strEmpId) == 125) {
						System.out.println("intDaysCnt ===>> " + intDaysCnt + " --- insertWeekOffCnt =========================>> " + insertWeekOffCnt);
						}
						if (normalWeekOffCnt<=1) {
							if(intDaysCnt == 3 || (intDaysCnt==4 && insertWeekOffCnt==1)) {
								Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
								while(it.hasNext()) {
									String strRosterWOffId = it.next();
									Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
									List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
									List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
									if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
										rosterWOffId = strRosterWOffId;
										insertFlag = true;
										normalWeekOffCnt++;
										insertWeekOffCnt++;
									}
								}
								if(insertWeekOffCnt == 2) {
									intDaysCnt=0;
									insertWeekOffCnt=0;
								}
							}
						} else if(strechWeekOffCnt<=1) {
							if(intDaysCnt == 7 || (intDaysCnt==8 && insertWeekOffCnt==1)) {
								Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
								while(it.hasNext()) {
									String strRosterWOffId = it.next();
									Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
									List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
									List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
									if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
										rosterWOffId = strRosterWOffId;
										insertFlag = true;
										strechWeekOffCnt++;
										insertWeekOffCnt++;
									}
								}
								if(insertWeekOffCnt == 2) {
									intDaysCnt=0;
									insertWeekOffCnt=0;
								}
							}
						} else if (normalWeekOffCnt<=3) {
							if(intDaysCnt == 3 || (intDaysCnt==4 && insertWeekOffCnt==1)) {
								Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
								while(it.hasNext()) {
									String strRosterWOffId = it.next();
									Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
									List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
									List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
									if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
										rosterWOffId = strRosterWOffId;
										insertFlag = true;
										normalWeekOffCnt++;
										insertWeekOffCnt++;
									}
								}
								if(insertWeekOffCnt == 2) {
									intDaysCnt=0;
									insertWeekOffCnt=0;
								}
							}
						}
					} else if(nOfDaysDiff>=3) {
						if(insertWeekOffCnt==1) {
							pst = con.prepareStatement("delete from roster_weekly_off where emp_id=? and weekoff_date=? and service_id =?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setDate(2,  uF.getDateFormat(lastDate, strDateformat));
							pst.setInt(3, uF.parseToInt(strEmpServiceId));
							int x = pst.executeUpdate();
	//						System.out.println("weekoff delete pst ===>> " + pst);
							pst.close();
							
							pst = con.prepareStatement("update roster_details set roster_weeklyoff_id=? where emp_id=? and _date=? and service_id=?");
							pst.setInt(1, 0);
							pst.setInt(2, uF.parseToInt(strEmpId));
							pst.setDate(3,  uF.getDateFormat(lastDate, strDateformat));
							pst.setInt(4, uF.parseToInt(strEmpServiceId));
							pst.executeUpdate();
	//						System.out.println(strEmpId + " ELSE update --- pst ===>> " + pst);
							pst.close();
							if(uF.parseToInt(strEmpId) == 125) {
								System.out.println("x =======>>>>>>>>> " + x);
							}
							if(x>0 && intDaysCnt==3) { normalWeekOffCnt--;}
							if(x>0 && intDaysCnt==7) { strechWeekOffCnt--;}
						}
						intDaysCnt=0;
						insertWeekOffCnt=0;
//						System.out.println("in ELSE normalWeekOffCnt ===>> " + normalWeekOffCnt + " -- strechWeekOffCnt ===>> " + strechWeekOffCnt + " -- intDaysCnt ===>> " + intDaysCnt);
					}
				}
				if (!insertFlag || (insertFlag && insertWeekOffCnt==1)) {
					lastDate = getDate(strFromDate, strDateformat, valueOfJ);
				}
			}
			
			if (insertFlag) {
				pst = con.prepareStatement("update roster_weekly_off set roster_weeklyoff_id=?, shift_id=? where emp_id=? and weekoff_date=? and service_id =?");
				pst.setInt(1, uF.parseToInt(rosterWOffId));
				pst.setInt(2, uF.parseToInt(strShiftId));
				pst.setInt(3, uF.parseToInt(strEmpId));
				pst.setDate(4,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
				pst.setInt(5, uF.parseToInt(strEmpServiceId));
				int x = pst.executeUpdate();
//				System.out.println("weekoff update pst ===>> " + pst);
				pst.close();
				
				if (x == 0) {
					pst = con.prepareStatement("insert into roster_weekly_off (emp_id, weekoff_date, service_id, roster_weeklyoff_id, shift_id) values(?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
					pst.setInt(3, uF.parseToInt(strEmpServiceId));
					pst.setInt(4, uF.parseToInt(rosterWOffId));
					pst.setInt(5, uF.parseToInt(strShiftId));
					pst.execute();
//					if(uF.parseToInt(strEmpId) == 125) {
//						System.out.println("weekoff insert pst ===>> " + pst);
//					}
					pst.close();
				}
				
				pst = con.prepareStatement("update roster_details set roster_weeklyoff_id=? where emp_id=? and _date=? and service_id=?"); 
				pst.setInt(1, uF.parseToInt(rosterWOffId));
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setDate(3,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
				pst.setInt(4, uF.parseToInt(strEmpServiceId));
				pst.executeUpdate();
//				if(uF.parseToInt(strEmpId) == 125) {
//					System.out.println(strEmpId + " update --- pst ===>> " + pst);
//				}
				pst.close();
				
				String newDate1 = getDate(strFromDate, strDateformat, valueOfJ);
//				System.out.println("strEmpId ====================================================>> " + strEmpId);
//				assignShiftsOnBasisOfRulesOFWeeklyOffForOneDay(con, uF, newDate1, strPaycycle, strEmpId);
				
				Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
				pst1 = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst1 ===>> " + pst1);
				rst1 = pst1.executeQuery();
				String strRotationOfShift = "";
				while (rst1.next()) {
					strRotationOfShift = rst1.getString("rotation_of_shift");
					hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
					hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
					hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
					hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
					hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
					hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
					hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
					hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
					hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
					hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
					hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
					hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
					hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
					hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
					hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
					hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
					hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
				}
				rst1.close();
				pst1.close();
//				System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
				
				String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
				String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
				String strTlCnt = null;
				String strTlLevel = null;
				List<String> alTlLevels = new ArrayList<String>();
				if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
					strTlCnt = strTmpTlRule[0];
					strTlLevel = strTmpTlRule[1];
					alTlLevels = Arrays.asList(strTlLevel.split(","));
				}
				
				
				String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
				Map<String, Map<String, String>> hmRosterAssignedEmpCntData = getRosterAssignedEmpCountData(con, uF, strEmpId, newDate1, newDate1, empOrgId, alTlLevels);
//				System.out.println("hmRosterAssignedEmpCntData ===>> " + hmRosterAssignedEmpCntData);
				Map<String, String> hmExistEmpCnt1 = hmRosterAssignedEmpCntData.get("EMP_CNT");
				Map<String, String> hmExistEmpGenderwiseCnt1 = hmRosterAssignedEmpCntData.get("EMP_GENDERWISE_CNT");
				Map<String, String> hmExistTlEmpCnt1 = hmRosterAssignedEmpCntData.get("TL_EMP_CNT");
				
				int intExistCnt = uF.parseToInt(hmExistEmpCnt.get(strShiftId+"_"+newDate1));
				int intExistMaleGenderCnt = uF.parseToInt(hmExistEmpGenderwiseCnt.get(strShiftId+"_"+newDate1+"_M"));
				int intTlExistCnt = uF.parseToInt(hmExistTlEmpCnt.get(strShiftId+"_"+newDate1));
				
				int intExistCnt1 = uF.parseToInt(hmExistEmpCnt1.get(strShiftId+"_"+newDate1));
				int intExistMaleGenderCnt1 = uF.parseToInt(hmExistEmpGenderwiseCnt1.get(strShiftId+"_"+newDate1+"_M"));
				int intTlExistCnt1 = uF.parseToInt(hmExistTlEmpCnt1.get(strShiftId+"_"+newDate1));
//				System.out.println(strEmpId +" -- strShiftId ===>> " + strShiftId + " == intExistCnt ===>> " + intExistCnt + " - " + intExistCnt1 + " -- intExistMaleGenderCnt ===>> " + intExistMaleGenderCnt + " - "+ intExistMaleGenderCnt1 + " --- intTlExistCnt ===>> " + intTlExistCnt + " - " + intTlExistCnt1);
				
				if(intExistCnt > intExistCnt1 || intExistMaleGenderCnt > intExistMaleGenderCnt1 || intTlExistCnt > intTlExistCnt1) {
					pst = con.prepareStatement("delete from roster_weekly_off where emp_id=? and weekoff_date=? and service_id=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2,  uF.getDateFormat(newDate1, strDateformat));
					pst.setInt(3, uF.parseToInt(strEmpServiceId));
					int x1 = pst.executeUpdate();
//					System.out.println("weekoff delete pst ===>> " + pst);
					pst.close();
					
					pst = con.prepareStatement("update roster_details set roster_weeklyoff_id=? where emp_id=? and _date=? and service_id=?");
					pst.setInt(1, 0);
					pst.setInt(2, uF.parseToInt(strEmpId));
					pst.setDate(3,  uF.getDateFormat(newDate1, strDateformat));
					pst.setInt(4, uF.parseToInt(strEmpServiceId));
					pst.executeUpdate();
//					System.out.println(strEmpId + " ELSE update --- pst ===>> " + pst);
					pst.close();
//					System.out.println("x1 =======>>>>>>>>> " + x1);
					
					if(x1>0 && intDaysCnt==3) { normalWeekOffCnt--;}
					if(x1>0 && intDaysCnt==7) { strechWeekOffCnt--;}
					
					if(valueOfJ>=0 && valueOfJ<=6) {
						firstWeekEndCnt--;
						weekendWeekOffCnt--;
					} else if(firstWeekEndCnt==0 && valueOfJ>=7 && valueOfJ<=13) {
						secWeekEndCnt--;
						weekendWeekOffCnt--;
					} else if(firstWeekEndCnt==2 && valueOfJ>=14 && valueOfJ<=20) {
						thirdWeekEndCnt--;
						weekendWeekOffCnt--;
					} else if(secWeekEndCnt==2 && valueOfJ>=21 && valueOfJ<=27) {
						fourthWeekEndCnt--;
						weekendWeekOffCnt--;
					} else if(firstWeekEndCnt==2 && thirdWeekEndCnt==2 && valueOfJ>=28 && valueOfJ<=30) {
						fifthWeekEndCnt--;
						weekendWeekOffCnt--;
					}
				}
			}
			
			alReturnData.add(""+weekendWeekOffCnt);
			alReturnData.add(""+strechWeekOffCnt); //+strechWeekOffCnt
			alReturnData.add(""+normalWeekOffCnt); //+normalWeekOffCnt
			alReturnData.add(""+intDaysCnt); //+intDaysCnt
			alReturnData.add(""+insertWeekOffCnt); //+insertWeekOffCnt
			alReturnData.add(lastDate); //lastDate
			alReturnData.add(""+firstWeekEndCnt);
			alReturnData.add(""+secWeekEndCnt);
			alReturnData.add(""+thirdWeekEndCnt);
			alReturnData.add(""+fourthWeekEndCnt);
			alReturnData.add(""+fifthWeekEndCnt);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		
		return alReturnData;
	}*/
	
	
	/*private List<String> insertUpdateRosterWeeklyOff(Connection con, UtilityFunctions uF, String strPaycycle, String strFromDate, String strDateformat, int valueOfJ, 
			String strEmpId, String strEmpServiceId, int weekendWeekOffCnt, String lastDate, int strechWeekOffCnt, int intDaysCnt, 
			int normalWeekOffCnt, int insertWeekOffCnt, int firstWeekEndCnt, int secWeekEndCnt, int thirdWeekEndCnt, int fourthWeekEndCnt, int fifthWeekEndCnt, 
			Map<String, String> hmExistEmpCnt, Map<String, String> hmExistEmpGenderwiseCnt, Map<String, String> hmExistTlEmpCnt) {
			PreparedStatement pst = null;
			PreparedStatement pst1 = null;
			ResultSet rs = null;
			ResultSet rst1 = null;
			List<String> alReturnData = new ArrayList<String>();
			try {
				
				String strShiftId = null;
				pst = con.prepareStatement("select shift_id from roster_details where emp_id=? and _date=? and service_id =?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
				pst.setInt(3, uF.parseToInt(strEmpServiceId));
				rs = pst.executeQuery();
				while(rs.next()) {
					strShiftId = rs.getString("shift_id");
				}
				rs.close();
				pst.close();
				
				List<String> alWeekDays = uF.getWeekDaysFullName();
				List<String> alWeekEnds = uF.getWeekEndsFullName();
				
				Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con, CF, uF);
				if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
				
				String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(strFromDate, getDate(strFromDate, strDateformat, valueOfJ), strDateformat);
				String strDay = uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat, "EEEE");
				if(strDay!=null) strDay = strDay.toUpperCase();
				if(uF.parseToInt(strEmpId) == 125) {
//					System.out.println("insertUpdateRosterWeeklyOff strDay ===>> " + strDay + " --- valueOfJ ===>> " + valueOfJ);
				}

				boolean insertFlag = false;
				String rosterWOffId = null;
					if(lastDate != null) {
						int nOfDaysDiff = uF.parseToInt(uF.dateDifference(lastDate, DATE_FORMAT, getDate(strFromDate, strDateformat, valueOfJ), DATE_FORMAT, CF.getStrTimeZone()));
//						if(uF.parseToInt(strEmpId) == 125) {
//						System.out.println("lastDate ===>> " + lastDate+ " -- nOfDaysDiff ===>> " + nOfDaysDiff + " -- normalWeekOffCnt ===>> " + normalWeekOffCnt + " -- strechWeekOffCnt ===>> " + strechWeekOffCnt);
//						}
						if(nOfDaysDiff == 2 || (nOfDaysDiff == 3 && insertWeekOffCnt==1)) {
							intDaysCnt++;
//							if(uF.parseToInt(strEmpId) == 125) {
//							System.out.println("intDaysCnt ===>> " + intDaysCnt + " --- insertWeekOffCnt =========================>> " + insertWeekOffCnt);
//							}
							if (normalWeekOffCnt<=1) {
								if(intDaysCnt == 3 || (intDaysCnt==4 && insertWeekOffCnt==1)) {
									Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
									while(it.hasNext()) {
										String strRosterWOffId = it.next();
										Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
										List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
										List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
										if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
											rosterWOffId = strRosterWOffId;
											insertFlag = true;
											normalWeekOffCnt++;
											insertWeekOffCnt++;
										}
									}
									if(insertWeekOffCnt == 2) {
										intDaysCnt=0;
										insertWeekOffCnt=0;
									}
								}
							} else if(strechWeekOffCnt<=1) {
								if(intDaysCnt == 7 || (intDaysCnt==8 && insertWeekOffCnt==1)) {
									Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
									while(it.hasNext()) {
										String strRosterWOffId = it.next();
										Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
										List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
										List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
										if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
											rosterWOffId = strRosterWOffId;
											insertFlag = true;
											strechWeekOffCnt++;
											insertWeekOffCnt++;
										}
									}
									if(insertWeekOffCnt == 2) {
										intDaysCnt=0;
										insertWeekOffCnt=0;
									}
								}
							} else if (normalWeekOffCnt<=3) {
								if(intDaysCnt == 3 || (intDaysCnt==4 && insertWeekOffCnt==1)) {
									Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
									while(it.hasNext()) {
										String strRosterWOffId = it.next();
										Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
										List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
										List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
										if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
											rosterWOffId = strRosterWOffId;
											insertFlag = true;
											normalWeekOffCnt++;
											insertWeekOffCnt++;
										}
									}
									if(insertWeekOffCnt == 2) {
										intDaysCnt=0;
										insertWeekOffCnt=0;
									}
								}
							}
						} else if(nOfDaysDiff>=3) {
							if(insertWeekOffCnt==1) {
								pst = con.prepareStatement("delete from roster_weekly_off where emp_id=? and weekoff_date=? and service_id =?");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setDate(2,  uF.getDateFormat(lastDate, strDateformat));
								pst.setInt(3, uF.parseToInt(strEmpServiceId));
								int x = pst.executeUpdate();
		//						System.out.println("weekoff delete pst ===>> " + pst);
								pst.close();
								
								pst = con.prepareStatement("update roster_details set roster_weeklyoff_id=? where emp_id=? and _date=? and service_id=?");
								pst.setInt(1, 0);
								pst.setInt(2, uF.parseToInt(strEmpId));
								pst.setDate(3,  uF.getDateFormat(lastDate, strDateformat));
								pst.setInt(4, uF.parseToInt(strEmpServiceId));
								pst.executeUpdate();
		//						System.out.println(strEmpId + " ELSE update --- pst ===>> " + pst);
								pst.close();
//								if(uF.parseToInt(strEmpId) == 125) {
//									System.out.println("x =======>>>>>>>>> " + x);
//								}
								if(x>0 && intDaysCnt==3) { normalWeekOffCnt--;}
								if(x>0 && intDaysCnt==7) { strechWeekOffCnt--;}
							}
							intDaysCnt=0;
							insertWeekOffCnt=0;
//							System.out.println("in ELSE normalWeekOffCnt ===>> " + normalWeekOffCnt + " -- strechWeekOffCnt ===>> " + strechWeekOffCnt + " -- intDaysCnt ===>> " + intDaysCnt);
						}
					}
					if (!insertFlag || (insertFlag && insertWeekOffCnt==1)) {
						lastDate = getDate(strFromDate, strDateformat, valueOfJ);
					}
				
				if (insertFlag) {
					pst = con.prepareStatement("update roster_weekly_off set roster_weeklyoff_id=?, shift_id=? where emp_id=? and weekoff_date=? and service_id =?");
					pst.setInt(1, uF.parseToInt(rosterWOffId));
					pst.setInt(2, uF.parseToInt(strShiftId));
					pst.setInt(3, uF.parseToInt(strEmpId));
					pst.setDate(4,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
					pst.setInt(5, uF.parseToInt(strEmpServiceId));
					int x = pst.executeUpdate();
//					System.out.println("weekoff update pst ===>> " + pst);
					pst.close();
					
					if (x == 0) {
						pst = con.prepareStatement("insert into roster_weekly_off (emp_id, weekoff_date, service_id, roster_weeklyoff_id, shift_id) values(?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
						pst.setInt(3, uF.parseToInt(strEmpServiceId));
						pst.setInt(4, uF.parseToInt(rosterWOffId));
						pst.setInt(5, uF.parseToInt(strShiftId));
						pst.execute();
//						if(uF.parseToInt(strEmpId) == 125) {
//							System.out.println("weekoff insert pst ===>> " + pst);
//						}
						pst.close();
					}
					
					pst = con.prepareStatement("update roster_details set roster_weeklyoff_id=? where emp_id=? and _date=? and service_id=?"); 
					pst.setInt(1, uF.parseToInt(rosterWOffId));
					pst.setInt(2, uF.parseToInt(strEmpId));
					pst.setDate(3,  uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat));
					pst.setInt(4, uF.parseToInt(strEmpServiceId));
					pst.executeUpdate();
//					if(uF.parseToInt(strEmpId) == 125) {
//						System.out.println(strEmpId + " update --- pst ===>> " + pst);
//					}
					pst.close();
					
					String newDate1 = getDate(strFromDate, strDateformat, valueOfJ);
//					System.out.println("strEmpId ====================================================>> " + strEmpId);
//					assignShiftsOnBasisOfRulesOFWeeklyOffForOneDay(con, uF, newDate1, strPaycycle, strEmpId);
					
					Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
					pst1 = con.prepareStatement(sbQuery.toString());
//					System.out.println("pst1 ===>> " + pst1);
					rst1 = pst1.executeQuery();
					String strRotationOfShift = "";
					while (rst1.next()) {
						strRotationOfShift = rst1.getString("rotation_of_shift");
						hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rst1.getString("roster_policy_rule_id"));
						hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("rule_type_id"));
						hmRosterPolicyRulesData.put("SHIFT_ID", rst1.getString("shift_id"));
						hmRosterPolicyRulesData.put("SHIFT_IDS", rst1.getString("shift_ids"));
						hmRosterPolicyRulesData.put("RULE_TYPE_ID", rst1.getString("no_of_days"));
						hmRosterPolicyRulesData.put("GENDER", rst1.getString("gender"));
						hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rst1.getString("min_no_of_member_in_shift"));
						hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rst1.getString("min_no_of_member_in_shift_at_weekend"));
						hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rst1.getString("no_of_leads_from_levels_for_no_of_member"));
						hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rst1.getString("min_weekend_off_per_month"));
						hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rst1.getString("max_no_of_shifts_per_member_per_month"));
						hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rst1.getString("min_days_off_between_shifts"));
						hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rst1.getString("member_location_associated_locations"));
						hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rst1.getString("min_male_member_in_shift"));
						hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rst1.getString("min_break_days_in_stretch_shift"));
						hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rst1.getString("rotation_of_shift"));
						hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rst1.getString("remaining_emp_shift"));
					}
					rst1.close();
					pst1.close();
//					System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
					
					String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
					String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
					String strTlCnt = null;
					String strTlLevel = null;
					List<String> alTlLevels = new ArrayList<String>();
					if(strTlRule != null && !strTlRule.equalsIgnoreCase("null")) {
						strTlCnt = strTmpTlRule[0];
						strTlLevel = strTmpTlRule[1];
						alTlLevels = Arrays.asList(strTlLevel.split(","));
					}
					
					
					String empOrgId = CF.getEmpOrgId(con, uF, strEmpId);
					Map<String, Map<String, String>> hmRosterAssignedEmpCntData = getRosterAssignedEmpCountData(con, uF, strEmpId, newDate1, newDate1, empOrgId, alTlLevels);
//					System.out.println("hmRosterAssignedEmpCntData ===>> " + hmRosterAssignedEmpCntData);
					Map<String, String> hmExistEmpCnt1 = hmRosterAssignedEmpCntData.get("EMP_CNT");
					Map<String, String> hmExistEmpGenderwiseCnt1 = hmRosterAssignedEmpCntData.get("EMP_GENDERWISE_CNT");
					Map<String, String> hmExistTlEmpCnt1 = hmRosterAssignedEmpCntData.get("TL_EMP_CNT");
					
					int intExistCnt = uF.parseToInt(hmExistEmpCnt.get(strShiftId+"_"+newDate1));
					int intExistMaleGenderCnt = uF.parseToInt(hmExistEmpGenderwiseCnt.get(strShiftId+"_"+newDate1+"_M"));
					int intTlExistCnt = uF.parseToInt(hmExistTlEmpCnt.get(strShiftId+"_"+newDate1));
					
					int intExistCnt1 = uF.parseToInt(hmExistEmpCnt1.get(strShiftId+"_"+newDate1));
					int intExistMaleGenderCnt1 = uF.parseToInt(hmExistEmpGenderwiseCnt1.get(strShiftId+"_"+newDate1+"_M"));
					int intTlExistCnt1 = uF.parseToInt(hmExistTlEmpCnt1.get(strShiftId+"_"+newDate1));
//					System.out.println(strEmpId +" -- strShiftId ===>> " + strShiftId + " == intExistCnt ===>> " + intExistCnt + " - " + intExistCnt1 + " -- intExistMaleGenderCnt ===>> " + intExistMaleGenderCnt + " - "+ intExistMaleGenderCnt1 + " --- intTlExistCnt ===>> " + intTlExistCnt + " - " + intTlExistCnt1);
					
					if(intExistCnt > intExistCnt1 || intExistMaleGenderCnt > intExistMaleGenderCnt1 || intTlExistCnt > intTlExistCnt1) {
						pst = con.prepareStatement("delete from roster_weekly_off where emp_id=? and weekoff_date=? and service_id=?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2,  uF.getDateFormat(newDate1, strDateformat));
						pst.setInt(3, uF.parseToInt(strEmpServiceId));
						int x1 = pst.executeUpdate();
//						System.out.println("weekoff delete pst ===>> " + pst);
						pst.close();
						
						pst = con.prepareStatement("update roster_details set roster_weeklyoff_id=? where emp_id=? and _date=? and service_id=?");
						pst.setInt(1, 0);
						pst.setInt(2, uF.parseToInt(strEmpId));
						pst.setDate(3,  uF.getDateFormat(newDate1, strDateformat));
						pst.setInt(4, uF.parseToInt(strEmpServiceId));
						pst.executeUpdate();
//						System.out.println(strEmpId + " ELSE update --- pst ===>> " + pst);
						pst.close();
//						System.out.println("x1 =======>>>>>>>>> " + x1);
						
						if(x1>0 && intDaysCnt==3) { normalWeekOffCnt--;}
						if(x1>0 && intDaysCnt==7) { strechWeekOffCnt--;}
						
						if(valueOfJ>=0 && valueOfJ<=6) {
							firstWeekEndCnt--;
							weekendWeekOffCnt--;
						} else if(firstWeekEndCnt==0 && valueOfJ>=7 && valueOfJ<=13) {
							secWeekEndCnt--;
							weekendWeekOffCnt--;
						} else if(firstWeekEndCnt==2 && valueOfJ>=14 && valueOfJ<=20) {
							thirdWeekEndCnt--;
							weekendWeekOffCnt--;
						} else if(secWeekEndCnt==2 && valueOfJ>=21 && valueOfJ<=27) {
							fourthWeekEndCnt--;
							weekendWeekOffCnt--;
						} else if(firstWeekEndCnt==2 && thirdWeekEndCnt==2 && valueOfJ>=28 && valueOfJ<=30) {
							fifthWeekEndCnt--;
							weekendWeekOffCnt--;
						}
					}
				}
				
				alReturnData.add(""); //+weekendWeekOffCnt
				alReturnData.add(""+strechWeekOffCnt);
				alReturnData.add(""+normalWeekOffCnt);
				alReturnData.add(""+intDaysCnt);
				alReturnData.add(""+insertWeekOffCnt);
				alReturnData.add(lastDate);
				alReturnData.add(""); //+firstWeekEndCnt
				alReturnData.add(""); //+secWeekEndCnt
				alReturnData.add(""); //+thirdWeekEndCnt
				alReturnData.add(""); //+fourthWeekEndCnt
				alReturnData.add(""); //+fifthWeekEndCnt
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(rs !=null) {
					try {
						rs.close();
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
			return alReturnData;
		}*/
	
	
	public int insertUpdateRoster(Connection con, String strFromDate, String strFromTime, String strToTime, double dblTimeDiff, String strShiftId, String strEmpId, String strServiceId, int nRosterWOff, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int xIn=0;
		try {
			pst = con.prepareStatement("UPDATE roster_details SET  _date= ?, _from= ?, _to= ?, actual_hours= ?, shift_id=?, entry_date=?,roster_weeklyoff_id=? where emp_id=? and _date=? and service_id =?");
			pst.setDate(1, uF.getDateFormat(strFromDate, DATE_FORMAT));
			pst.setTime(2, uF.getTimeFormat(strFromTime, TIME_FORMAT));
			pst.setTime(3, uF.getTimeFormat(strToTime, TIME_FORMAT));
			pst.setDouble(4, dblTimeDiff);
			pst.setInt(5, uF.parseToInt(strShiftId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, nRosterWOff);
			pst.setInt(8, uF.parseToInt(strEmpId));
			pst.setDate(9, uF.getDateFormat(strFromDate, DATE_FORMAT));
			pst.setInt(10, uF.parseToInt(strServiceId));
			xIn = pst.executeUpdate();
			pst.close();
				
			if(xIn==0) {
				pst = con.prepareStatement("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?,?,(select user_id from user_details where emp_id=?),?,?,?,?,?,?,?)"); 
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2,  uF.getDateFormat(strFromDate, DATE_FORMAT));
				pst.setTime(3, uF.getTimeFormat(strFromTime, TIME_FORMAT));
				pst.setTime(4, uF.getTimeFormat(strToTime, TIME_FORMAT));
				pst.setBoolean(5, false);
				pst.setInt(6, uF.parseToInt(strEmpId));
				pst.setInt(7, uF.parseToInt(strServiceId));
				pst.setDouble(8, dblTimeDiff);
				pst.setInt(9, 0);
				pst.setBoolean(10, false);
				pst.setInt(11, uF.parseToInt(strShiftId));
				pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(13, nRosterWOff);
				pst.executeUpdate();
//				System.out.println(strEmpId + " insert --- pst ===>> " + pst);
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return xIn;
	}
	
	
	public int updateRoster(Connection con, String strFromDate,String strDateformat,String strFromTime,String strToTime,double dblTimeDiff,String strShiftId,String strEmpId, String strServiceId, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		int xIn=0;
		try {
			pst = con.prepareStatement("UPDATE roster_details SET _date= ?, _from= ?, _to= ?, actual_hours= ?, shift_id=?, entry_date=? where emp_id=? and _date=? and service_id =?");
			pst.setDate(1, uF.getDateFormat(strFromDate, strDateformat));
			pst.setTime(2, uF.getTimeFormat(strFromTime, TIME_FORMAT));
			pst.setTime(3, uF.getTimeFormat(strToTime, TIME_FORMAT));
			pst.setDouble(4, dblTimeDiff);
			pst.setInt(5, uF.parseToInt(strShiftId));
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(7, nRosterWOff);
			pst.setInt(7, uF.parseToInt(strEmpId));
			pst.setDate(8, uF.getDateFormat(strFromDate, strDateformat));
			pst.setInt(9, uF.parseToInt(strServiceId));
			xIn = pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		return xIn;
	}
	
	
	public void insertRoster(Connection con, String strEmpId, String strFromDate, String strDateformat, String strFromTime, String strToTime, String strCostCenterName, double dblTimeDiff, String strShiftId, int nRosterWOff, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String insertRoster="insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?,?,(select user_id from user_details where emp_id=?),?,?,?,?,?,?,?)";
		try {
//			String strDay = uF.getDateFormat(getDate(strFromDate, strDateformat, valueOfJ), strDateformat, "EEEE");
//			if(strDay!=null) strDay = strDay.toUpperCase();
			pst = con.prepareStatement(insertRoster); 
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2,  uF.getDateFormat(strFromDate, strDateformat));
			pst.setTime(3, uF.getTimeFormat(strFromTime, TIME_FORMAT));
			pst.setTime(4, uF.getTimeFormat(strToTime, TIME_FORMAT));
			pst.setBoolean(5, false);
			pst.setInt(6, uF.parseToInt(strEmpId));
			pst.setInt(7, uF.parseToInt(strCostCenterName));
			pst.setDouble(8, dblTimeDiff);
			pst.setInt(9, 0);
			pst.setBoolean(10, false);
			pst.setInt(11, uF.parseToInt(strShiftId));
			pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(13, nRosterWOff);
			pst.executeUpdate();
//			System.out.println(strEmpId + " insert --- pst ===>> " + pst);
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
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
		
	}

	public String getDate(String userDate,String strFromat, int nDays )  {
		String date=userDate;
		SimpleDateFormat dateFormat= new SimpleDateFormat(strFromat);
		Calendar calendar=Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DATE,nDays);
		date = dateFormat.format(calendar.getTime());
		
		return date;
	}
	
	
	private void generateHalfDayFullDayException(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		PreparedStatement pst1 = null;
		ResultSet rst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rst2 = null;
		PreparedStatement pst3 = null;
		ResultSet rst3 = null;
		PreparedStatement pst4 = null;
		try {

			Map<String, String> hmEmpLastExceptionDate = new HashMap<String, String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select epd.emp_per_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
					+ "and epd.is_alive=true and joining_date<=? ");
//			sbQuery.append(" and epd.emp_per_id=193 ");
			sbQuery.append(" order by emp_per_id desc");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			// System.out.println("induction pst1==>"+pst);
			StringBuilder sbEmpIds = null;
			rst = pst.executeQuery();
			while (rst.next()) {
				if (sbEmpIds == null) {
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(rst.getString("emp_per_id"));
				} else {
					sbEmpIds.append("," + rst.getString("emp_per_id"));
				}
				hmEmpLastExceptionDate.put(rst.getString("emp_per_id"), "");
			}
			rst.close();
			pst.close();

			// attendance_details
			// exception_reason

			Map<String, String> hmEmpLastPayrollDate = new HashMap<String, String>();
			if (sbEmpIds != null) {
				sbQuery = new StringBuilder();
				sbQuery.append("select max(_date) as _date, emp_id from exception_reason where emp_id in (" + sbEmpIds.toString()
						+ ") and (in_out_type='HD' or in_out_type='FD') group by emp_id");
				pst1 = con.prepareStatement(sbQuery.toString());
				// System.out.println("induction pst2==>"+pst);
				rst1 = pst1.executeQuery();
				while (rst1.next()) {
					hmEmpLastExceptionDate.put(rst1.getString("emp_id"), rst1.getString("_date"));
				}
				rst1.close();
				pst1.close();
//				System.out.println("hmEmpLastExceptionDate ===>> " + hmEmpLastExceptionDate);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select max(sal_effective_date) as sal_effective_date, emp_id from payroll_generation where emp_id in ("+sbEmpIds.toString()+ ") group by emp_id");
				pst1 = con.prepareStatement(sbQuery.toString());
				// System.out.println("induction pst2==>"+pst);
				rst1 = pst1.executeQuery();
				while (rst1.next()) {
					hmEmpLastPayrollDate.put(rst1.getString("emp_id"), rst1.getString("sal_effective_date"));
				}
				rst1.close();
				pst1.close();
				
			}

			Map<String, Map<String, List<String>>> hmEmpHDFDExceptions = new HashMap<String, Map<String, List<String>>>();

			Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpStateMap = new HashMap<String, String>();
			Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map<String, List<String>> hmEmpExistException = new HashMap<String, List<String>>();
			
			if (hmEmpLastExceptionDate != null && !hmEmpLastExceptionDate.isEmpty()) {
				Iterator<String> it = hmEmpLastExceptionDate.keySet().iterator();
				while (it.hasNext()) {
					String strEmpId = it.next();
					String strLastDate = hmEmpLastExceptionDate.get(strEmpId);
					String strLastPayrollDate = hmEmpLastPayrollDate.get(strEmpId);
					if(strLastDate != null && !strLastDate.equals("") && strLastPayrollDate != null && uF.getDateFormat(strLastPayrollDate, DBDATE).before(uF.getDateFormat(strLastDate, DBDATE))) {
						strLastDate = strLastPayrollDate;
					}
					sbQuery = new StringBuilder();
					sbQuery.append("select hours_worked, emp_id,in_out_timestamp,service_id from attendance_details where emp_id=? and in_out='OUT' ");
					if (strLastDate != null && strLastDate.trim().length() > 0) {
						sbQuery.append(" and to_date(in_out_timestamp::text, 'YYYY-MM-DD')>? ");
					}
					sbQuery.append(" order by in_out_timestamp ");
					pst2 = con.prepareStatement(sbQuery.toString());
					pst2.setInt(1, uF.parseToInt(strEmpId));
					if (strLastDate != null && strLastDate.trim().length() > 0) {
						pst2.setDate(2, uF.getDateFormat(strLastDate, DBDATE));
					}
//					System.out.println("pst2 ===>> " + pst2);
					rst2 = pst2.executeQuery();
					while (rst2.next()) {
						String strDate = uF.getDateFormat(rst2.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
						// System.out.println("strDate =====>> " + strDate);
						Map<String, Map<String, String>> hmHalfDayFullDayMinHrs = CF.getWorkLocationHalfDayFullDayMinHours(con, uF, strDate);
						if(hmHalfDayFullDayMinHrs==null) hmHalfDayFullDayMinHrs = new HashMap<String, Map<String,String>>();
						
						// System.out.println("hmHalfDayFullDayMinHrs =====>> "
						// + hmHalfDayFullDayMinHrs);
						Map<String, String> hmHDFDMinHrs = hmHalfDayFullDayMinHrs.get(hmEmpWlocationMap.get(rst2.getString("emp_id")));
						if(hmHDFDMinHrs==null) hmHDFDMinHrs = new HashMap<String, String>();
						
						// System.out.println("hmHDFDMinHrs =====>> " +
						// hmHDFDMinHrs);
						// System.out.println("hours_worked =====>> " +
						// rst2.getDouble("hours_worked"));
						if (hmHDFDMinHrs != null && hmHDFDMinHrs.get("MIN_HRS_HD") != null && hmHDFDMinHrs.get("MIN_HRS_FD") != null && rst2.getDouble("hours_worked") >= uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_HD")) && rst2.getDouble("hours_worked") < uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_FD"))) {
							Map<String, List<String>> hmInner = hmEmpHDFDExceptions.get(rst2.getString("emp_id"));
							if (hmInner == null) hmInner = new HashMap<String, List<String>>();

							List<String> innerList = new ArrayList<String>();
							innerList.add(strDate);
							innerList.add(rst2.getString("hours_worked"));
							innerList.add(rst2.getString("service_id"));
							innerList.add("FD");
							innerList.add("Auto generated");
							hmInner.put(strDate, innerList);
							hmEmpHDFDExceptions.put(rst2.getString("emp_id"), hmInner);
						} else if (hmHDFDMinHrs != null && hmHDFDMinHrs.get("MIN_HRS_HD") != null && rst2.getDouble("hours_worked") < uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_HD"))) {
							Map<String, List<String>> hmInner = hmEmpHDFDExceptions.get(rst2.getString("emp_id"));
							if (hmInner == null) hmInner = new HashMap<String, List<String>>();

							List<String> innerList = new ArrayList<String>();
							innerList.add(strDate);// 0
							innerList.add(rst2.getString("hours_worked")); // 1
							innerList.add(rst2.getString("service_id")); // 2
							innerList.add("HD"); // 3
							innerList.add("Auto generated"); // 4
							hmInner.put(strDate, innerList);
							hmEmpHDFDExceptions.put(rst2.getString("emp_id"), hmInner);
						}
					}
					rst2.close();
					pst2.close();
					
					
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id,_date from exception_reason where emp_id=?");
					if (strLastDate != null && strLastDate.trim().length() > 0) {
						sbQuery.append(" and _date>? ");
					}
					sbQuery.append(" order by _date ");
					pst2 = con.prepareStatement(sbQuery.toString());
					pst2.setInt(1, uF.parseToInt(strEmpId));
					if (strLastDate != null && strLastDate.trim().length() > 0) {
						pst2.setDate(2, uF.getDateFormat(strLastDate, DBDATE));
					}
//					 System.out.println("pst2 ===>> " + pst2);
					rst2 = pst2.executeQuery();
					while (rst2.next()) {
						
						List<String> innerList = hmEmpExistException.get(rst2.getString("emp_id"));
						if(innerList==null) innerList = new ArrayList<String>();
						
						String strDate = uF.getDateFormat(rst2.getString("_date"), DBDATE, DATE_FORMAT);
						innerList.add(strDate);
						
						hmEmpExistException.put(rst2.getString("emp_id"), innerList);
					}
					rst2.close();
					pst2.close();
				}
			}
//			System.out.println("hmEmpExistException ===>> " + hmEmpExistException);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpSupervisorId = CF.getEmpSupervisorIdMap(con);
			
			if (hmEmpHDFDExceptions != null && !hmEmpHDFDExceptions.isEmpty()) {
				pst3 = con .prepareStatement("insert into exception_reason (_date,given_reason,emp_id,in_out_type,service_id,hours_worked,generated_date) values (?,?,?,?, ?,?,?)");
				Iterator<String> it = hmEmpHDFDExceptions.keySet().iterator();
				while (it.hasNext()) {
					String strEmpId = it.next();
					Map<String, List<String>> hmInner = hmEmpHDFDExceptions.get(strEmpId);
					List<String> innList = hmEmpExistException.get(strEmpId);
					if (hmInner != null) {
						Iterator<String> it1 = hmInner.keySet().iterator();
						while (it1.hasNext()) {
							String strDate = it1.next();
							if(innList!=null && innList.contains(strDate)) {
								continue;
							}
							List<String> innerList = hmInner.get(strDate);
							pst3.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
							pst3.setString(2, innerList.get(4));
							pst3.setInt(3, uF.parseToInt(strEmpId));
							pst3.setString(4, innerList.get(3));
							pst3.setInt(5, uF.parseToInt(innerList.get(2)));
							pst3.setDouble(6, uF.parseToDouble(innerList.get(1)));
							pst3.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
							// System.out.println("pst ===>> " + pst);
							pst3.addBatch();
							
							String alertData = "<div class=\"grow\" style=\"float: left;\"> Received a new "+uF.showData(innerList.get(3), "") +" Exception request from <b>"+hmEmpName.get(strEmpId)+"</b>, on date "+uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT_STR)+".["+MANAGER+"] </div>";
							String alertAction = "TeamTime.action?pType=WR&callFrom=MyDashTimeExceptions";
							UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(hmEmpSupervisorId.get(strEmpId));
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID("2");
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
							
							/*pst4 = con.prepareStatement("insert into workrig_user_alerts (emp_id, alert_data, alert_action, emp_user_type, employee_id, _date, " +
								"entry_date_time) values(?,?,?,?, ?,?,?)");
						 	pst4.setInt(1, uF.parseToInt(strEmpId));
							pst4.setString(2, alertData);
							pst4.setString(3, alertAction);
							pst4.setInt(4, 2);
							pst4.setInt(5, uF.parseToInt(strEmpId));
							pst4.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
							pst4.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) , DBDATE + DBTIME));
							System.out.println("pst4 ===> " + pst4);
							pst4.executeUpdate();
							if(pst4 != null) {
								try {
									pst4.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}*/
							
						}
					}
				}
				int[] x = pst3.executeBatch();
				pst3.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void sendMonthlyAttendanceAndSalaryApprovalPending(Connection con, UtilityFunctions uF, boolean flagEnableOrgAttendanceApprovalStatus, boolean flagEnableOrgSalaryApprovalStatus) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			List<String> alAttendaneStatus = new ArrayList<String>();
			
			pst = con.prepareStatement("select feature_name,feature_status,user_type_id,emp_ids from feature_management");
			rs = pst.executeQuery();
			Map<String, List<String>> hmFeatureUserTypeId = new HashMap<String, List<String>>();
			while (rs.next()) {
				List<String> innerList1 = new ArrayList<String>();
				if (rs.getString("emp_ids") != null) {
					innerList1 = Arrays.asList(rs.getString("emp_ids").split(","));
				}
				hmFeatureUserTypeId.put(rs.getString("feature_name") + "_USER_IDS", innerList1);
			}
			rs.close();
			pst.close();
			
//			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
//			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			
			String strCurrDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strCurrDate1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
//			System.out.println("strCurrDate==>> " + strCurrDate);
//			System.out.println("request =====>> " + request);
//			System.out.println("strDomain =====>> " + strDomain);
			String empOrgId = CF.getEmpOrgId(con, uF, (String)session.getAttribute(EMPID));
			String[] strCurrPaycycle = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), strCurrDate, CF, empOrgId, request);
			String[] strPrevPaycycle = CF.getPrevPayCycleByOrg(con, strCurrDate1, CF.getStrTimeZone(), CF, empOrgId);
//			String[] strFuturePaycycle = CF.getPrevPayCycle(strDate, strTimeZone, CF);
			
			strPrevPaycycle[0] = "01/04/2021";
			strPrevPaycycle[1] = "30/04/2021";
			strPrevPaycycle[2] = "13";
			
			int intMonth = uF.parseToInt(uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT, "MM"));
			int intYear = uF.parseToInt(uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT, "yyyy"));
			
			Date strFutureDate = uF.getDateFormatUtil(uF.getFutureDate(uF.getDateFormatUtil(strPrevPaycycle[1], DATE_FORMAT), 1)+"", DBDATE);
			Date dtCurrDate = uF.getDateFormatUtil(strCurrDate1, DATE_FORMAT);
//			System.out.println("strCurrPaycycle===>> " + strCurrPaycycle[0] +" -- "+ strCurrPaycycle[1]+" -- "+ strCurrPaycycle[2]);
//			System.out.println("strPrevPaycycle===>> " + strPrevPaycycle[0] +" -- "+ strPrevPaycycle[1]+" -- "+ strPrevPaycycle[2]);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strPrevPaycycle[1], CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from communication_1 where feed_type=? and org_attendance_approval_status_paycycle_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, FT_ACTIVITY);
			pst.setInt(2, uF.parseToInt(strPrevPaycycle[2]));
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			boolean insertedAttendanceFlag = false;
			while(rs.next()) {
				insertedAttendanceFlag = true;
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from communication_1 where feed_type=? and org_salary_approval_status_paycycle_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, FT_ACTIVITY);
			pst.setInt(2, uF.parseToInt(strPrevPaycycle[2]));
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			boolean insertedSalaryFlag = false;
			while(rs.next()) {
				insertedSalaryFlag = true;
			}
			rs.close();
			pst.close();
			
			int intTotAttendance = 0;
			if(flagEnableOrgAttendanceApprovalStatus && !insertedAttendanceFlag && dtCurrDate.equals(strFutureDate)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as emp_ids from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " + // and epd.is_alive=true
					"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
				sbQuery.append(" and emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
						+ "and paid_from = ? and paid_to=? group by emp_id) and emp_id not in (select emp_id from approve_attendance where "
						+ "approve_from=? and approve_to=?)");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(9, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(10, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				String strPendingEmpCount = "0";
				while (rs.next()) {
					strPendingEmpCount = rs.getString("emp_ids");
					intTotAttendance += rs.getInt("emp_ids");
				}
				rs.close();
				pst.close();
	
				sbQuery = new StringBuilder();
				sbQuery.append("select count(*) as emp_ids from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " + // and epd.is_alive=true
					"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
				sbQuery.append(" and (emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
					+ "and paid_from = ? and paid_to=? group by emp_id) or emp_id in (select emp_id from approve_attendance where approve_from=? and approve_to=?)) ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(9, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(10, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
	//			System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				String strApprovedEmpCount = "0";
				while (rs.next()) {
					strApprovedEmpCount = rs.getString("emp_ids");
					intTotAttendance += rs.getInt("emp_ids");
				}
				rs.close();
				pst.close();
	
				alAttendaneStatus.add(strApprovedEmpCount);
				alAttendaneStatus.add(strPendingEmpCount);
				
//				System.out.println("alAttendaneStatus ===>> " + alAttendaneStatus);
				
				List<String> alMailEmpId = hmFeatureUserTypeId.get(F_ENABLE_ORG_ATTENDANCE_APPROVAL_STATUS_MAIL+"_USER_IDS");
				if(alMailEmpId==null)alMailEmpId = new ArrayList<String>();
				boolean mailFlag = false;
				for(int i=0; i<alMailEmpId.size(); i++) {
					if(uF.parseToInt(alMailEmpId.get(i))>0) {
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_ORG_ATTENDANCE_APPROVAL_STATUS, CF);				
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(alMailEmpId.get(i));
						nF.setStrContextPath(request.getContextPath());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrPaycycleMonthAndYear(uF.getShortMonth(intMonth)+"-"+intYear);
						nF.setStrAttendanceApproveEmpCount(strApprovedEmpCount);
						nF.setStrAttendancePendingEmpCount(strPendingEmpCount);
						nF.setEmailTemplate(true);
						nF.sendNotifications();
						mailFlag = true;
					}
				}
				
				if(mailFlag) {
					sbQuery = new StringBuilder();
					sbQuery.append("insert into communication_1(created_by,create_time,feed_type,org_attendance_approval_status_paycycle_id) " +
							"values(?,?,?,?)");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setInt(3, FT_ACTIVITY);
					pst.setInt(4, uF.parseToInt(strPrevPaycycle[2]));
//					System.out.println("pst ===>>>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			
			if(flagEnableOrgSalaryApprovalStatus && !insertedSalaryFlag && dtCurrDate.equals(strFutureDate)) {
				List<String> alApprovePayStatus = new ArrayList<String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select count (*) as emp_ids from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
					+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
					+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
				sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
					+ "and paid_from = ? and paid_to=? group by emp_id)");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				rs = pst.executeQuery();
				String strPendingEmpCount = "0";
				while (rs.next()) {
					strPendingEmpCount = rs.getString("emp_ids");
				}
				rs.close();
				pst.close();
	
				sbQuery = new StringBuilder();
				sbQuery.append("select count (*) as emp_ids from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
					+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
					+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
				sbQuery.append(" and eod.emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
					+ "and paid_from = ? and paid_to=? group by emp_id)");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strPrevPaycycle[0], DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strPrevPaycycle[1], DATE_FORMAT));
	//			System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				String strApprovedEmpCount = "0";
				while (rs.next()) {
					strApprovedEmpCount = rs.getString("emp_ids");
				}
				rs.close();
				pst.close();
				
				int intSalaryApprovalPendingEmpCnt = intTotAttendance - uF.parseToInt(strApprovedEmpCount);
				strPendingEmpCount = intSalaryApprovalPendingEmpCnt+"";
				
				alApprovePayStatus.add(strApprovedEmpCount);
				alApprovePayStatus.add(strPendingEmpCount);
//				System.out.println("alApprovePayStatus ===>> " + alApprovePayStatus);
				
				List<String> alMailEmpId = hmFeatureUserTypeId.get(F_ENABLE_ORG_SALARY_APPROVAL_STATUS_MAIL+"_USER_IDS");
				if(alMailEmpId==null)alMailEmpId = new ArrayList<String>();
				boolean mailFlag = false;
				for(int i=0; i<alMailEmpId.size(); i++) {
					if(uF.parseToInt(alMailEmpId.get(i))>0) {
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_ORG_SALARY_APPROVAL_STATUS, CF);				
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(alMailEmpId.get(i));
						nF.setStrContextPath(request.getContextPath());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrPaycycleMonthAndYear(uF.getShortMonth(intMonth)+"-"+intYear);
						nF.setStrSalaryApproveEmpCount(strApprovedEmpCount);
						nF.setStrSalaryPendingEmpCount(strPendingEmpCount);
						nF.setEmailTemplate(true);				
						nF.sendNotifications();
						mailFlag = true;
					}
				}
				
				if(mailFlag) {
					sbQuery = new StringBuilder();
					sbQuery.append("insert into communication_1(created_by,create_time,feed_type,org_salary_approval_status_paycycle_id) " +
							"values(?,?,?,?)");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst.setInt(3, FT_ACTIVITY);
					pst.setInt(4, uF.parseToInt(strPrevPaycycle[2]));
//					System.out.println("pst ===>>>> " + pst);
					pst.executeUpdate();
					pst.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	public synchronized void setOvertimeMinuteAlertNotification(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			List<String> alEmployees = new ArrayList<String>();
			List alServices = new ArrayList();

			String strD1 = uF.getDateFormat("" + uF.getPrevDate(CF.getStrTimeZone(), 1), DBDATE, DATE_FORMAT);
			String strD2 = uF.getDateFormat("" + uF.getPrevDate(CF.getStrTimeZone(), 1), DBDATE, DATE_FORMAT);
			// String strD1 = "01/10/2017";
			// String strD2 = "31/10/2017";
			// System.out.println("strD1==>"+strD1);
			// System.out.println("strD2==>"+strD2);

			List<String> alOrg = new ArrayList<String>();
			List<String> alLevel = new ArrayList<String>();
			pst = con.prepareStatement("select * from overtime_details where ((? between date_from and date_to) "
					+ "or (? between date_from and date_to)) and overtime_id in (select overtime_id from overtime_minute_slab)");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			// System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (!alOrg.contains(rs.getString("org_id")) && uF.parseToInt(rs.getString("org_id")) > 0) {
					alOrg.add(rs.getString("org_id"));
				}
				if (!alLevel.contains(rs.getString("level_id")) && uF.parseToInt(rs.getString("level_id")) > 0) {
					alLevel.add(rs.getString("level_id"));
				}
			}
			rs.close();
			pst.close();

			// System.out.println("alOrg====>"+alOrg);
			// System.out.println("alLevel====>"+alLevel);
			int nOrg = alOrg.size();
			int nLevel = alLevel.size();

			if (nOrg > 0 && nLevel > 0) {
				String strOrgIds = StringUtils.join(alOrg.toArray(), ",");
				String strLevelIds = StringUtils.join(alLevel.toArray(), ",");

				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
						+ "and epd.is_alive = true and epd.employment_end_date is null");
				sbQuery.append(" and emp_id in (select distinct(ad.emp_id) from attendance_details ad, roster_details rd "
						+ "where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id "
						+ "and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?");
				sbQuery.append(" and ad.emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
						+ "and epd.is_alive = true and epd.employment_end_date is null");
				sbQuery.append(" and org_id in (" + strOrgIds + ")");
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,level_details ld, designation_details dd "
						+ "where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id in (" + strLevelIds + ")) ");
				sbQuery.append(")) and org_id in (" + strOrgIds + ")");
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,level_details ld, designation_details dd "
						+ "where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id in (" + strLevelIds + ")) ");
				sbQuery.append(" order by emp_fname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				// System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmpCodeMap = new HashMap<String, String>();
				Map<String, String> hmEmpName = new HashMap<String, String>();
				while (rs.next()) {
					alEmployees.add(rs.getString("emp_per_id"));

					/*String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname")
							.trim() + " " : "";*/
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
					hmEmpName.put(rs.getString("emp_id"), strEmpName);

					hmEmpCodeMap.put(rs.getString("emp_id"), rs.getString("empcode"));
				}
				rs.close();
				pst.close();
				// System.out.println("hmEmpCodeMap ===>> "+ hmEmpCodeMap);

				Map<String, String> hmLeaveColor = new HashMap<String, String>();
				CF.getLeavesAttributes(con, uF, hmLeaveColor, null);
				int nEmpSize = alEmployees != null ? alEmployees.size() : 0;
				List<String> alApproveDenyOT = new ArrayList<String>();
				Map<String, String> hmManager = new HashMap<String, String>();
				Map<String, String> hmHOD = new HashMap<String, String>();
				List<String> alGlobalHr = new ArrayList<String>();
				Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
				if (nEmpSize > 0) {
					String empIds = StringUtils.join(alEmployees.toArray(), ",");
					pst = con.prepareStatement("select * from overtime_emp_minute_status where ot_date between ? and ? " + "and emp_id in(" + empIds + ")");
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					rs = pst.executeQuery();
					while (rs.next()) {
						alApproveDenyOT.add(rs.getString("emp_id") + "_" + uF.getDateFormat(rs.getString("ot_date"), DBDATE, DATE_FORMAT));
					}
					rs.close();
					pst.close();

					pst = con
							.prepareStatement("select emp_id,supervisor_emp_id, hod_emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id "
									+ "and emp_id in(" + empIds + ")");
					rs = pst.executeQuery();
					while (rs.next()) {
						if (uF.parseToInt(rs.getString("supervisor_emp_id")) > 0) {
							hmManager.put(rs.getString("emp_id"), rs.getString("supervisor_emp_id"));
						}
						if (uF.parseToInt(rs.getString("hod_emp_id")) > 0) {
							hmHOD.put(rs.getString("emp_id"), rs.getString("hod_emp_id"));
						}
					}
					rs.close();
					pst.close();

					pst = con.prepareStatement("select eod.emp_id from employee_personal_details epd, employee_official_details eod, user_details ud "
							+ "where epd.emp_per_id = eod.emp_id and ud.emp_id = eod.emp_id and epd.emp_per_id = ud.emp_id and ud.usertype_id=1");
					rs = pst.executeQuery();
					while (rs.next()) {
						if (uF.parseToInt(rs.getString("emp_id")) > 0) {
							alGlobalHr.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
				}
				int nGlobalHr = alGlobalHr.size();

				// Map<String, String> hmOTHours = new HashMap<String,
				// String>();
				OvertimeApproval otApproval = new OvertimeApproval();
				otApproval.request = this.request;
				otApproval.session = this.session;
				otApproval.CF = this.CF;

				for (int a = 0; a < nEmpSize; a++) {
					String strEmpId = (String) alEmployees.get(a);

					otApproval.getClockEntries(con, uF, strEmpId, strD1, strD2, null);
					otApproval.getOverTimeDetails(con, uF, strEmpId, strD1, strD2, null);

					List alInOut = (List) request.getAttribute("alInOut");
					List alDate = (List) request.getAttribute("alDate");
					List alDay = (List) request.getAttribute("alDay");

					Map hmHours = (HashMap) request.getAttribute("hmHours");
					Map hmHoursActual = (HashMap) request.getAttribute("hmHoursActual");
					Map hmStart = (HashMap) request.getAttribute("hmStartClockEntries");
					Map hmEnd = (HashMap) request.getAttribute("hmEndClockEntries");

					Map hmActualStart = (HashMap) request.getAttribute("hmActualStartClockEntries");
					Map hmActualEnd = (HashMap) request.getAttribute("hmActualEndClockEntries");

					Map hmRosterStart = (HashMap) request.getAttribute("hmRosterStart");
					Map hmRosterEnd = (HashMap) request.getAttribute("hmRosterEnd");

					Map hmDailyRate = (HashMap) request.getAttribute("hmDailyRate");
					Map hmHoursRates = (HashMap) request.getAttribute("hmHoursRates");
					Map hmServicesWorkedFor = (HashMap) request.getAttribute("hmServicesWorkedFor");
					Map hmDateServices = (HashMap) request.getAttribute("hmDateServices_TS");

					Map hmExceptions = (Map) request.getAttribute("hmExceptions");

					if (hmDateServices == null)
						hmDateServices = new HashMap();

					String TOTALW1 = (String) request.getAttribute("TOTALW1");
					String TOTALW2 = (String) request.getAttribute("TOTALW2");
					String DEDUCTION = (String) request.getAttribute("DEDUCTION");
					String PAYW1 = (String) request.getAttribute("PAYTOTALW1");
					String PAYW2 = (String) request.getAttribute("PAYTOTALW2");

					String _TOTALRosterW1 = (String) request.getAttribute("_TOTALRosterW1");
					String _TOTALRosterW2 = (String) request.getAttribute("_TOTALRosterW2");
					String _ALLOWANCE = (String) request.getAttribute("ALLOWANCE");

					String strPayMode = (String) request.getAttribute("strPayMode");
					String strFIXED = (String) request.getAttribute("FIXED");

					Map<String, Set<String>> hmWeekEndList = (Map<String, Set<String>>) request.getAttribute("hmWeekEndList");
					if (hmWeekEndList == null)
						hmWeekEndList = new HashMap<String, Set<String>>();
					String strWLocationId = (String) request.getAttribute("strWLocationId");
					String strLevelId = (String) request.getAttribute("strLevelId");
					Set<String> weeklyOffSet = (Set<String>) hmWeekEndList.get(strWLocationId);
					if (weeklyOffSet == null)
						weeklyOffSet = new HashSet<String>();

					List<String> alEmpCheckRosterWeektype = (List<String>) request.getAttribute("alEmpCheckRosterWeektype");;
					if (alEmpCheckRosterWeektype == null)
						alEmpCheckRosterWeektype = new ArrayList<String>();

					Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>) request.getAttribute("hmRosterWeekEndDates");;
					if (hmRosterWeekEndDates == null)
						hmRosterWeekEndDates = new HashMap<String, Set<String>>();

					Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
					if (rosterWeeklyOffSet == null)
						rosterWeeklyOffSet = new HashSet<String>();

					Map hmWLocationHolidaysName = (Map) request.getAttribute("hmWLocationHolidaysName");
					Map hmHolidaysName = (Map) hmWLocationHolidaysName.get(strWLocationId);
					if (hmHolidaysName == null)
						hmHolidaysName = new HashMap();

					Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
					Map hmHolidayDates = (Map) request.getAttribute("hmHolidayDates");
					Map hmEarlyLateReporting = (Map) request.getAttribute("hmEarlyLateReporting");
					Map hmServices = (Map) request.getAttribute("hmServices");

					Map hmLeavesMap = (Map) request.getAttribute("hmLeaves");
					if (hmLeavesMap == null)
						hmLeavesMap = new HashMap();

					Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
					if (hmLeavesColour == null)
						hmLeavesColour = new HashMap();

					List _alHolidays = (List) request.getAttribute("_alHolidays");

					String strEmpName = (String) request.getAttribute("EMP_NAME");
					Map hmRosterHours = (Map) request.getAttribute("hmRosterHours");

					if (strEmpName == null) {
						strEmpName = "";
					}

					if (hmHours == null) {
						hmHours = new HashMap();
					}

					if (hmStart == null) {
						hmStart = new HashMap();
					}

					if (hmEnd == null) {
						hmEnd = new HashMap();
					}
					if (hmDailyRate == null) {
						hmDailyRate = new HashMap();
					}
					if (hmHoursRates == null) {
						hmHoursRates = new HashMap();
					}

					if (hmHoursRates == null) {
						hmHoursRates = new HashMap();
					}

					if (hmEarlyLateReporting == null) {
						hmEarlyLateReporting = new HashMap();
					}
					if (hmRosterHours == null) {
						hmRosterHours = new HashMap();
					}
					if (_hmHolidaysColour == null) {
						_hmHolidaysColour = new HashMap();
					}
					if (hmLeavesColour == null) {
						hmLeavesColour = new HashMap();
					}
					if (hmExceptions == null) {
						hmExceptions = new HashMap();
					}

					Map<String, String> hmBreakPolicy = (Map<String, String>) request.getAttribute("hmBreakPolicy");
					if (hmBreakPolicy == null)
						hmBreakPolicy = new HashMap<String, String>();
					Boolean flagBreak = (Boolean) request.getAttribute("flagBreak");

					Map<String, String> hmShiftBreak = (Map<String, String>) request.getAttribute("hmShiftBreak");
					if (hmShiftBreak == null)
						hmShiftBreak = new HashMap<String, String>();

					Map<String, String> hmRosterShiftId = (Map<String, String>) request.getAttribute("hmRosterShiftId");
					if (hmRosterShiftId == null)
						hmRosterShiftId = new HashMap<String, String>();

					String strDefaultLunchDeduction = (String) request.getAttribute("strDefaultLunchDeduction");

					Map<String, Map<String, String>> hmOvertimeType = (Map<String, Map<String, String>>) request.getAttribute("hmOvertimeType");
					if (hmOvertimeType == null)
						hmOvertimeType = new HashMap<String, Map<String, String>>();

					Map<String, List<Map<String, String>>> hmOvertimeMinuteSlab = (Map<String, List<Map<String, String>>>) request
							.getAttribute("hmOvertimeMinuteSlab");
					if (hmOvertimeMinuteSlab == null)
						hmOvertimeMinuteSlab = new HashMap<String, List<Map<String, String>>>();

					String locationstarttime = (String) request.getAttribute("locationstarttime");
					String locationendtime = (String) request.getAttribute("locationendtime");
					String userlocation = (String) request.getAttribute("userlocation");

					// System.out.println("alDate  ===>> " + alDate);
					for (int i = 0; i < alDate.size(); i++) {
						if (alApproveDenyOT.contains(strEmpId + "_" + (String) alDate.get(i))) {
							continue;
						}

						List alDateServices = (List) hmDateServices.get((String) alDate.get(i));
						if (alDateServices == null) {
							alDateServices = new ArrayList();
							alDateServices.add("-1");
						}

						int ii = 0;
						for (ii = 0; ii < alDateServices.size(); ii++) {

							if (uF.parseToInt((String) alDateServices.get(ii)) == 0)
								continue;

							double dblHrsAtten = uF.parseToDouble((String) hmHours.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)));
							double dblHrsAttenActual = uF.parseToDouble((String) hmHoursActual.get((String) alDate.get(i) + "_"
									+ (String) alDateServices.get(ii)));
							double dblHrsRoster = uF.parseToDouble((String) hmRosterHours.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)));

							double dblOtHrs = 0.0d;
							if (((String) hmStart.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)) != null)
									&& ((String) hmEnd.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)) != null)) {
								Map<String, String> hmOvertime = null;
								String day = uF.getDateFormat("" + uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat()), DBDATE, DATE_FORMAT);
								if (hmHolidayDates != null && hmHolidayDates.containsKey((String) alDate.get(i) + "_" + userlocation)) {
									hmOvertime = hmOvertimeType.get("PH");
								} else if (hmWeekEndList != null && hmWeekEndList.containsKey(day + "_" + userlocation)) {
									hmOvertime = hmOvertimeType.get("BH");
								} else {
									hmOvertime = hmOvertimeType.get("EH");
								}

								if (hmOvertime == null)
									hmOvertime = new HashMap<String, String>();
								// if(uF.parseToInt(strEmpId) == 126){
								// System.out.println("date==>"+(String)
								// alDate.get(i)+"==STANDARD_WKG_HRS==>"+hmOvertime.get("STANDARD_WKG_HRS"));
								// }
								if (hmOvertime.get("STANDARD_WKG_HRS") != null && hmOvertime.get("STANDARD_WKG_HRS").equals("RH")) {
									Time entryTime = uF
											.getTimeFormat(
													(String) alDate.get(i) + " "
															+ (String) hmStart.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)),
													CF.getStrReportDateFormat() + " " + DBTIME);
									Time rosterStartTime = uF.getTimeFormat(
											(String) alDate.get(i) + " "
													+ (String) hmRosterStart.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)),
											CF.getStrReportDateFormat() + " " + DBTIME);
									Time rosterEndTime = uF.getTimeFormat(
											(String) alDate.get(i) + " "
													+ (String) hmRosterEnd.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)),
											CF.getStrReportDateFormat() + " " + DBTIME);
									Time endTime = uF.getTimeFormat(
											(String) alDate.get(i) + " " + (String) hmEnd.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)),
											CF.getStrReportDateFormat() + " " + DBTIME);

									long milliseconds1 = entryTime.getTime();
									long milliseconds2 = rosterEndTime.getTime();
									long milliseconds3 = endTime.getTime();
									long milliseconds4 = rosterStartTime.getTime();

									if (milliseconds3 >= milliseconds2) {
										double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds4, milliseconds2));
										double actualTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
										double bufferTime = uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
										double ottime = (actualTime - dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF, CF.getStrTimeZone(),
												uF.formatIntoTwoDecimal(dbl), uF.formatIntoTwoDecimal(actualTime))) : 0.0d;
										// ottime = ottime > 0.0d ?
										// uF.convertHoursMinsInDouble(ottime) :
										// 0.0d;
										ottime = ottime > 0.0d ? uF.convertHoursIntoMinutes1(ottime) : 0.0d;
										bufferTime = bufferTime > 0.0d ? uF.convertHoursIntoMinutes1(bufferTime) : 0.0d;
										// if(uF.parseToInt(strEmpId) == 126){
										// System.out.println("date==>"+(String)
										// alDate.get(i)+"==dbl==>"+dbl+"==actualTime==>"+actualTime+"==ottime==>"+ottime+"==bufferTime==>"+bufferTime);
										// }

										if (ottime >= bufferTime) {
											double otTime = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma((ottime - bufferTime)));
											// if(uF.parseToInt(strEmpId) ==
											// 126){
											// System.out.println("date==>"+(String)
											// alDate.get(i)+"==otTime==>"+otTime);
											// }
											double dblHourOT = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.convertMinutesIntoHours(otTime)));

											// if(uF.parseToInt(strEmpId) ==
											// 126){
											// System.out.println("date==>"+(String)
											// alDate.get(i)+"==otTime==>"+otTime+"--dblHourOT==>"+dblHourOT);
											// }

											String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblHourOT);
											// if(uF.parseToInt(strEmpId) ==
											// 126){
											// System.out.println((String)alDate.get(i)+"--1--strTotal=====>"+strTotal);
											// }
											if (strTotal != null && strTotal.contains(".") && strTotal.indexOf(".") > 0) {
												String str11 = strTotal.replace(".", ":");
												String[] tempTotal = str11.split(":");
												double dblHr = uF.parseToDouble(tempTotal[1]);
												// if(uF.parseToInt(strEmpId) ==
												// 126){
												// System.out.println("date==>"+(String)
												// alDate.get(i)+"==dblHr==>"+dblHr);
												// }
												if (dblHr > 0 || (uF.parseToDouble(tempTotal[0]) > 0.0d && dblHr == 0.0d)) {
													List<Map<String, String>> alOtMinute = (List<Map<String, String>>) hmOvertimeMinuteSlab.get(hmOvertime
															.get("OVERTIME_ID"));
													if (alOtMinute == null)
														alOtMinute = new ArrayList<Map<String, String>>();
													int nRoundOffMinute = 0;
													int nAlOtMinuteSize = alOtMinute != null ? alOtMinute.size() : 0;
													// if(uF.parseToInt(strEmpId)
													// == 126){
													// System.out.println("date==>"+(String)
													// alDate.get(i)+"==OVERTIME_ID==>"+hmOvertime.get("OVERTIME_ID")+"--alOtMinute==>"+alOtMinute);
													// }
													for (int x = 0; x < nAlOtMinuteSize; x++) {
														Map<String, String> hmOvertimeMinute = alOtMinute.get(x);
														// if(uF.parseToInt(strEmpId)
														// == 126){
														// System.out.println("date==>"+(String)
														// alDate.get(i)
														// +"--OVERTIME_MIN_MINUTE==>"+uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE"))
														// +"--OVERTIME_MAX_MINUTE==>"+uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE")));
														// }
														if (uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblHr)
																&& ((int) dblHr) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))) {
															nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
															if (uF.parseToInt(strEmpId) == 126) {
																// System.out.println("date==>"+(String)
																// alDate.get(i)+"==OVERTIME_MIN_MINUTE==>"+hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")
																// +"--OVERTIME_MAX_MINUTE==>"+hmOvertimeMinute.get("OVERTIME_MAX_MINUTE")
																// +"--nRoundOffMinute==>"+nRoundOffMinute);
															}
															break;
														}
													}
													// if(uF.parseToInt(strEmpId)
													// == 126){
													// System.out.println("date==>"+(String)
													// alDate.get(i)+"--nRoundOffMinute==>"+nRoundOffMinute);
													// }
													double dblHour = 0.0d;
													if (nRoundOffMinute > 0) {
														dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
													}
													// if(uF.parseToInt(strEmpId)
													// == 126){
													// System.out.println("before Final date==>"+(String)
													// alDate.get(i)+"--dblHour==>"+dblHour+"--strTotal1==>"+uF.parseToDouble(tempTotal[0]));
													// }
													if (uF.parseToDouble(tempTotal[0]) != 0.0d || dblHour != 0.0d) {
														String strTotal1 = "" + (uF.parseToDouble(tempTotal[0]) + dblHour);
														// if(uF.parseToInt(strEmpId)
														// == 126){
														// System.out.println("Final date==>"+(String)
														// alDate.get(i)+"--dblHour==>"+dblHour+"--strTotal1==>"+strTotal1);
														// }
														dblOtHrs = uF.parseToDouble(strTotal1);

														String alertData = "<div style=\"float: left;\"> A new Overtime request has been generated for "
																+ "<strong>" + hmEmpName.get(strEmpId) + " [" + hmEmpCodeMap.get(strEmpId) + "]" + "</strong>,"
																+ "on <strong>" + day + "</strong>," + "for <strong>" + uF.showTime("" + dblOtHrs)
																+ "</strong> Hrs.</div>";
														String alertAction = "OvertimeApproval.action?pType=WR";

														boolean overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(hmManager.get(strEmpId)),
																uF.parseToInt(strEmpId), day);
														// System.out.println("overtimeFlag 1 ===>> "
														// + overtimeFlag);
														if (uF.parseToInt(hmManager.get(strEmpId)) > 0 && !overtimeFlag) {
															UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
															userAlerts.setStrDomain(strDomain);
															userAlerts.setStrEmpId(hmManager.get(strEmpId));
															userAlerts.setStrEmployeeId(strEmpId);
															userAlerts.setStrData(alertData);
															userAlerts.setStrAction(alertAction);
															userAlerts.setStrDate(day);
															userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(hmManager.get(strEmpId)) + "");
															userAlerts.setStatus(INSERT_WR_ALERT);
															Thread t = new Thread(userAlerts);
															t.run();
														}

														overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(hmHOD.get(strEmpId)), uF.parseToInt(strEmpId), day);

														if (uF.parseToInt(hmHOD.get(strEmpId)) > 0 && !overtimeFlag) {
															UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
															userAlerts.setStrDomain(strDomain);
															userAlerts.setStrEmpId(hmHOD.get(strEmpId));
															userAlerts.setStrEmployeeId(strEmpId);
															userAlerts.setStrData(alertData);
															userAlerts.setStrAction(alertAction);
															userAlerts.setStrDate(day);
															userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(hmHOD.get(strEmpId)) + "");
															userAlerts.setStatus(INSERT_WR_ALERT);
															Thread t = new Thread(userAlerts);
															t.run();
														}

														if (nGlobalHr > 0) {
															for (int j = 0; j < nGlobalHr; j++) {
																String strGobalHRId = alGlobalHr.get(j);
																overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(strGobalHRId), uF.parseToInt(strEmpId),
																		day);
																if (!overtimeFlag) {
																	UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
																	userAlerts.setStrDomain(strDomain);
																	userAlerts.setStrEmpId(strGobalHRId);
																	userAlerts.setStrEmployeeId(strEmpId);
																	userAlerts.setStrData(alertData);
																	userAlerts.setStrAction(alertAction);
																	userAlerts.setStrDate(day);
																	userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(strGobalHRId) + "");
																	userAlerts.setStatus(INSERT_WR_ALERT);
																	Thread t = new Thread(userAlerts);
																	t.run();
																}
															}
														}
													}
												}
											}
										}
									}
								} else if (hmOvertime.get("STANDARD_WKG_HRS") != null && hmOvertime.get("STANDARD_WKG_HRS").equals("SWH")) {
									Time entryTime = uF
											.getTimeFormat(
													(String) alDate.get(i) + " "
															+ (String) hmStart.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)),
													CF.getStrReportDateFormat() + " " + DBTIME);
									Time wlocationStartTime = uF.getTimeFormat((String) alDate.get(i) + " " + locationstarttime, CF.getStrReportDateFormat()
											+ " " + DBTIME);
									Time wlocationEndTime = uF.getTimeFormat((String) alDate.get(i) + " " + locationendtime, CF.getStrReportDateFormat() + " "
											+ DBTIME);
									Time endTime = uF.getTimeFormat(
											(String) alDate.get(i) + " " + (String) hmEnd.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)),
											CF.getStrReportDateFormat() + " " + DBTIME);

									long milliseconds1 = entryTime.getTime();
									long milliseconds2 = wlocationEndTime.getTime();
									long milliseconds3 = endTime.getTime();
									long milliseconds4 = wlocationStartTime.getTime();

									if (milliseconds3 >= milliseconds2) {
										double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds4, milliseconds2));
										dbl = dbl > 0.0d ? uF.convertHoursMinsInDouble(dbl) : 0.0d;
										double actualTime = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
										actualTime = actualTime > 0.0d ? uF.convertHoursMinsInDouble(actualTime) : 0.0d;
										double bufferTime = uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
										double ottime = actualTime - dbl;

										ottime = ottime > 0.0d ? uF.convertHoursIntoMinutes1(ottime) : 0.0d;
										bufferTime = bufferTime > 0.0d ? uF.convertHoursIntoMinutes1(bufferTime) : 0.0d;
										// if(uF.parseToInt(strEmpId) == 126){
										// System.out.println("date==>"+(String)
										// alDate.get(i)+"==ottime==>"+ottime+"==bufferTime==>"+bufferTime);
										// }

										if (ottime >= bufferTime) {
											double otTime = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma((ottime - bufferTime)));
											// if(uF.parseToInt(strEmpId) ==
											// 126){
											// System.out.println("date==>"+(String)
											// alDate.get(i)+"==otTime==>"+otTime);
											// }
											double dblHourOT = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.convertMinutesIntoHours(otTime)));

											// if(uF.parseToInt(strEmpId) ==
											// 126){
											// System.out.println("date==>"+(String)
											// alDate.get(i)+"==otTime==>"+otTime+"--dblHourOT==>"+dblHourOT);
											// }

											String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblHourOT);
											// if(uF.parseToInt(strEmpId) ==
											// 126){
											// System.out.println((String)alDate.get(i)+"--1--strTotal=====>"+strTotal);
											// }
											if (strTotal != null && strTotal.contains(".") && strTotal.indexOf(".") > 0) {
												String str11 = strTotal.replace(".", ":");
												String[] tempTotal = str11.split(":");
												double dblHr = uF.parseToDouble(tempTotal[1]);
												// if(uF.parseToInt(strEmpId) ==
												// 126){
												// System.out.println("date==>"+(String)
												// alDate.get(i)+"==dblHr==>"+dblHr);
												// }
												if (dblHr > 0 || (uF.parseToDouble(tempTotal[0]) > 0.0d && dblHr == 0.0d)) {
													List<Map<String, String>> alOtMinute = (List<Map<String, String>>) hmOvertimeMinuteSlab.get(hmOvertime
															.get("OVERTIME_ID"));
													if (alOtMinute == null)
														alOtMinute = new ArrayList<Map<String, String>>();
													int nRoundOffMinute = 0;
													int nAlOtMinuteSize = alOtMinute != null ? alOtMinute.size() : 0;
													// if(uF.parseToInt(strEmpId)
													// == 126){
													// System.out.println("date==>"+(String)
													// alDate.get(i)+"==OVERTIME_ID==>"+hmOvertime.get("OVERTIME_ID")+"--alOtMinute==>"+alOtMinute);
													// }
													for (int x = 0; x < nAlOtMinuteSize; x++) {
														Map<String, String> hmOvertimeMinute = alOtMinute.get(x);
														// if(uF.parseToInt(strEmpId)
														// == 126){
														// System.out.println("date==>"+(String)
														// alDate.get(i)
														// +"--OVERTIME_MIN_MINUTE==>"+uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE"))
														// +"--OVERTIME_MAX_MINUTE==>"+uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE")));
														// }
														if (uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblHr)
																&& ((int) dblHr) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))) {
															nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
															if (uF.parseToInt(strEmpId) == 126) {
																// System.out.println("date==>"+(String)
																// alDate.get(i)+"==OVERTIME_MIN_MINUTE==>"+hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")
																// +"--OVERTIME_MAX_MINUTE==>"+hmOvertimeMinute.get("OVERTIME_MAX_MINUTE")
																// +"--nRoundOffMinute==>"+nRoundOffMinute);
															}
															break;
														}
													}
													// if(uF.parseToInt(strEmpId)
													// == 126){
													// System.out.println("date==>"+(String)
													// alDate.get(i)+"--nRoundOffMinute==>"+nRoundOffMinute);
													// }
													double dblHour = 0.0d;
													if (nRoundOffMinute > 0) {
														dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
													}
													// if(uF.parseToInt(strEmpId)
													// == 126){
													// System.out.println("before Final date==>"+(String)
													// alDate.get(i)+"--dblHour==>"+dblHour+"--strTotal1==>"+uF.parseToDouble(tempTotal[0]));
													// }
													if (uF.parseToDouble(tempTotal[0]) != 0.0d || dblHour != 0.0d) {
														String strTotal1 = "" + (uF.parseToDouble(tempTotal[0]) + dblHour);
														// if(uF.parseToInt(strEmpId)
														// == 126){
														// System.out.println("Final date==>"+(String)
														// alDate.get(i)+"--dblHour==>"+dblHour+"--strTotal1==>"+strTotal1);
														// }
														dblOtHrs = uF.parseToDouble(strTotal1);

														String alertData = "<div style=\"float: left;\"> A new Overtime request has been generated for "
																+ "<strong>" + hmEmpName.get(strEmpId) + " [" + hmEmpCodeMap.get(strEmpId) + "]" + "</strong>,"
																+ "on <strong>" + day + "</strong>," + "for <strong>" + uF.showTime("" + dblOtHrs)
																+ "</strong> Hrs.</div>";
														String alertAction = "OvertimeApproval.action?pType=WR";

														boolean overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(hmManager.get(strEmpId)),
																uF.parseToInt(strEmpId), day);
														// System.out.println("overtimeFlag 2 ===>> "
														// + overtimeFlag);
														if (uF.parseToInt(hmManager.get(strEmpId)) > 0 && !overtimeFlag) {
															UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
															userAlerts.setStrDomain(strDomain);
															userAlerts.setStrEmpId(hmManager.get(strEmpId));
															userAlerts.setStrEmployeeId(strEmpId);
															userAlerts.setStrData(alertData);
															userAlerts.setStrAction(alertAction);
															userAlerts.setStrDate(day);
															userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(hmManager.get(strEmpId)) + "");
															userAlerts.setStatus(INSERT_WR_ALERT);
															Thread t = new Thread(userAlerts);
															t.run();
														}

														overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(hmManager.get(strEmpId)), uF.parseToInt(strEmpId),
																day);
														if (uF.parseToInt(hmHOD.get(strEmpId)) > 0 && !overtimeFlag) {
															UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
															userAlerts.setStrDomain(strDomain);
															userAlerts.setStrEmpId(hmHOD.get(strEmpId));
															userAlerts.setStrEmployeeId(strEmpId);
															userAlerts.setStrData(alertData);
															userAlerts.setStrAction(alertAction);
															userAlerts.setStrDate(day);
															userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(hmHOD.get(strEmpId)) + "");
															userAlerts.setStatus(INSERT_WR_ALERT);
															Thread t = new Thread(userAlerts);
															t.run();
														}

														if (nGlobalHr > 0) {
															for (int j = 0; j < nGlobalHr; j++) {
																String strGobalHRId = alGlobalHr.get(j);
																overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(strGobalHRId), uF.parseToInt(strEmpId),
																		day);
																if (!overtimeFlag) {
																	UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
																	userAlerts.setStrDomain(strDomain);
																	userAlerts.setStrEmpId(strGobalHRId);
																	userAlerts.setStrEmployeeId(strEmpId);
																	userAlerts.setStrData(alertData);
																	userAlerts.setStrAction(alertAction);
																	userAlerts.setStrDate(day);
																	userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(strGobalHRId) + "");
																	userAlerts.setStatus(INSERT_WR_ALERT);
																	Thread t = new Thread(userAlerts);
																	t.run();
																}
															}
														}
													}
												}
											}
										}
									}
								} else if (hmOvertime.get("STANDARD_WKG_HRS") != null && hmOvertime.get("STANDARD_WKG_HRS").equals("F")) {
									double ottime = uF.parseToDouble((String) hmHours.get((String) alDate.get(i) + "_" + (String) alDateServices.get(ii)));
									double bufferTime = uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
									// ottime = ottime > 0.0d ?
									// uF.convertHoursMinsInDouble(ottime) :
									// 0.0d;
									ottime = ottime > 0.0d ? uF.convertHoursIntoMinutes1(ottime) : 0.0d;
									bufferTime = bufferTime > 0.0d ? uF.convertHoursIntoMinutes1(bufferTime) : 0.0d;
									// if(uF.parseToInt(strEmpId) == 126){
									// System.out.println("date==>"+(String)
									// alDate.get(i)+"==ottime==>"+ottime+"==bufferTime==>"+bufferTime);
									// }

									if (ottime >= bufferTime) {
										double otTime = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma((ottime - bufferTime)));
										// if(uF.parseToInt(strEmpId) == 126){
										// System.out.println("date==>"+(String)
										// alDate.get(i)+"==otTime==>"+otTime);
										// }
										double dblHourOT = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.convertMinutesIntoHours(otTime)));

										// if(uF.parseToInt(strEmpId) == 126){
										// System.out.println("date==>"+(String)
										// alDate.get(i)+"==otTime==>"+otTime+"--dblHourOT==>"+dblHourOT);
										// }

										String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblHourOT);
										// if(uF.parseToInt(strEmpId) == 126){
										// System.out.println((String)alDate.get(i)+"--1--strTotal=====>"+strTotal);
										// }
										if (strTotal != null && strTotal.contains(".") && strTotal.indexOf(".") > 0) {
											String str11 = strTotal.replace(".", ":");
											String[] tempTotal = str11.split(":");
											double dblHr = uF.parseToDouble(tempTotal[1]);
											// if(uF.parseToInt(strEmpId) ==
											// 126){
											// System.out.println("date==>"+(String)
											// alDate.get(i)+"==dblHr==>"+dblHr);
											// }
											if (dblHr > 0 || (uF.parseToDouble(tempTotal[0]) > 0.0d && dblHr == 0.0d)) {
												List<Map<String, String>> alOtMinute = (List<Map<String, String>>) hmOvertimeMinuteSlab.get(hmOvertime
														.get("OVERTIME_ID"));
												if (alOtMinute == null)
													alOtMinute = new ArrayList<Map<String, String>>();
												int nRoundOffMinute = 0;
												int nAlOtMinuteSize = alOtMinute != null ? alOtMinute.size() : 0;
												// if(uF.parseToInt(strEmpId) ==
												// 126){
												// System.out.println("date==>"+(String)
												// alDate.get(i)+"==OVERTIME_ID==>"+hmOvertime.get("OVERTIME_ID")+"--alOtMinute==>"+alOtMinute);
												// }
												for (int x = 0; x < nAlOtMinuteSize; x++) {
													Map<String, String> hmOvertimeMinute = alOtMinute.get(x);
													// if(uF.parseToInt(strEmpId)
													// == 126){
													// System.out.println("date==>"+(String)
													// alDate.get(i)
													// +"--OVERTIME_MIN_MINUTE==>"+uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE"))
													// +"--OVERTIME_MAX_MINUTE==>"+uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE")));
													// }
													if (uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblHr)
															&& ((int) dblHr) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))) {
														nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
														if (uF.parseToInt(strEmpId) == 126) {
															// System.out.println("date==>"+(String)
															// alDate.get(i)+"==OVERTIME_MIN_MINUTE==>"+hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")
															// +"--OVERTIME_MAX_MINUTE==>"+hmOvertimeMinute.get("OVERTIME_MAX_MINUTE")
															// +"--nRoundOffMinute==>"+nRoundOffMinute);
														}
														break;
													}
												}
												// if(uF.parseToInt(strEmpId) ==
												// 126){
												// System.out.println("date==>"+(String)
												// alDate.get(i)+"--nRoundOffMinute==>"+nRoundOffMinute);
												// }
												double dblHour = 0.0d;
												if (nRoundOffMinute > 0) {
													dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
												}
												// if(uF.parseToInt(strEmpId) ==
												// 126){
												// System.out.println("before Final date==>"+(String)
												// alDate.get(i)+"--dblHour==>"+dblHour+"--strTotal1==>"+uF.parseToDouble(tempTotal[0]));
												// }
												if (uF.parseToDouble(tempTotal[0]) != 0.0d || dblHour != 0.0d) {
													String strTotal1 = "" + (uF.parseToDouble(tempTotal[0]) + dblHour);
													// if(uF.parseToInt(strEmpId)
													// == 126){
													// System.out.println("Final date==>"+(String)
													// alDate.get(i)+"--dblHour==>"+dblHour+"--strTotal1==>"+strTotal1);
													// }
													dblOtHrs = uF.parseToDouble(strTotal1);

													String alertData = "<div style=\"float: left;\"> A new Overtime request has been generated for "
															+ "<strong>" + hmEmpName.get(strEmpId) + " [" + hmEmpCodeMap.get(strEmpId) + "]" + "</strong>,"
															+ "on <strong>" + day + "</strong>," + "for <strong>" + uF.showTime("" + dblOtHrs)
															+ "</strong> Hrs.</div>";
													String alertAction = "OvertimeApproval.action?pType=WR";

													boolean overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(hmManager.get(strEmpId)),
															uF.parseToInt(strEmpId), day);
													// System.out.println("overtimeFlag ===>> "
													// + overtimeFlag);
													if (uF.parseToInt(hmManager.get(strEmpId)) > 0 && !overtimeFlag) {
														UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
														userAlerts.setStrDomain(strDomain);
														userAlerts.setStrEmpId(hmManager.get(strEmpId));
														userAlerts.setStrEmployeeId(strEmpId);
														userAlerts.setStrData(alertData);
														userAlerts.setStrAction(alertAction);
														userAlerts.setStrDate(day);
														userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(hmManager.get(strEmpId)) + "");
														userAlerts.setStatus(INSERT_WR_ALERT);
														Thread t = new Thread(userAlerts);
														t.run();
													}

													overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(hmHOD.get(strEmpId)), uF.parseToInt(strEmpId), day);
													if (uF.parseToInt(hmHOD.get(strEmpId)) > 0 && !overtimeFlag) {
														UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
														userAlerts.setStrDomain(strDomain);
														userAlerts.setStrEmpId(hmHOD.get(strEmpId));
														userAlerts.setStrEmployeeId(strEmpId);
														userAlerts.setStrData(alertData);
														userAlerts.setStrAction(alertAction);
														userAlerts.setStrDate(day);
														userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(hmHOD.get(strEmpId)) + "");
														userAlerts.setStatus(INSERT_WR_ALERT);
														Thread t = new Thread(userAlerts);
														t.run();
													}

													if (nGlobalHr > 0) {
														for (int j = 0; j < nGlobalHr; j++) {
															String strGobalHRId = alGlobalHr.get(j);
															overtimeFlag = checkOvertimeAlert(con, uF.parseToInt(strGobalHRId), uF.parseToInt(strEmpId), day);
															if (!overtimeFlag) {
																UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
																userAlerts.setStrDomain(strDomain);
																userAlerts.setStrEmpId(strGobalHRId);
																userAlerts.setStrEmployeeId(strEmpId);
																userAlerts.setStrData(alertData);
																userAlerts.setStrAction(alertAction);
																userAlerts.setStrDate(day);
																userAlerts.setCurrUserTypeID(hmEmpUserTypeId.get(strGobalHRId) + "");
																userAlerts.setStatus(INSERT_WR_ALERT);
																Thread t = new Thread(userAlerts);
																t.run();
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean checkOvertimeAlert(Connection con, int empId, int employeeId, String day) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		try {
			pst = con.prepareStatement("select * from workrig_user_alerts where emp_id=? and employee_id=? and _date=?");
			pst.setInt(1, empId);
			pst.setInt(2, employeeId);
			pst.setDate(3, uF.getDateFormat(day, DATE_FORMAT));
			// System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
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
		return flag;
	}

	public synchronized void getOverTimeDetails(Connection con, UtilityFunctions uF, String strEmpId, String strD1, String strD2, String strPC) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			String orgId = CF.getEmpOrgId(con, uF, strEmpId);
			String userlocation = CF.getEmpWlocationId(con, uF, strEmpId);
			String levelId = CF.getEmpLevelId(con, strEmpId);

			Map<String, Map<String, String>> hmOvertimeType = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from overtime_details where org_id=? and level_id=? and ((? between date_from and date_to) "
					+ "or (? between date_from and date_to)) and overtime_id in (select overtime_id from overtime_minute_slab)");
			pst.setInt(1, uF.parseToInt(orgId));
			pst.setInt(2, uF.parseToInt(levelId));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			// System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			StringBuffer sbOtId = null;
			while (rs.next()) {
				if (rs.getString("calculation_basis") != null && rs.getString("calculation_basis").trim().equalsIgnoreCase("M")) {
					if (sbOtId == null) {
						sbOtId = new StringBuffer();
						sbOtId.append(rs.getString("overtime_id"));
					} else {
						sbOtId.append("," + rs.getString("overtime_id"));
					}
				}
				if (rs.getString("overtime_type").equals("PH")) {
					Map<String, String> hmOvertime = new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME", rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS", rs.getString("calculation_basis"));

					hmOvertimeType.put("PH", hmOvertime);
				} else if (rs.getString("overtime_type").equals("BH")) {
					Map<String, String> hmOvertime = new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME", rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS", rs.getString("calculation_basis"));

					hmOvertimeType.put("BH", hmOvertime);
				} else if (rs.getString("overtime_type").equals("EH")) {
					Map<String, String> hmOvertime = new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME", rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS", rs.getString("calculation_basis"));

					hmOvertimeType.put("EH", hmOvertime);
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmOvertimeType", hmOvertimeType);
			// System.out.println("hmOvertimeType===>"+hmOvertimeType);

			if (sbOtId != null && sbOtId.length() > 0) {
				pst = con.prepareStatement("select * from overtime_minute_slab where overtime_id in (" + sbOtId.toString() + ")");
				// if(uF.parseToInt(strEmpId) == 126){
				// System.out.println("pst====>"+pst);
				// }
				rs = pst.executeQuery();
				Map<String, List<Map<String, String>>> hmOvertimeMinuteSlab = new HashMap<String, List<Map<String, String>>>();
				while (rs.next()) {
					List<Map<String, String>> alOtMinute = (List<Map<String, String>>) hmOvertimeMinuteSlab.get(rs.getString("overtime_id"));
					if (alOtMinute == null)
						alOtMinute = new ArrayList<Map<String, String>>();

					Map<String, String> hmOvertimeMinute = new HashMap<String, String>();
					hmOvertimeMinute.put("OVERTIME_MINUTE_ID", rs.getString("overtime_minute_id"));
					hmOvertimeMinute.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertimeMinute.put("OVERTIME_MIN_MINUTE", rs.getString("min_minute"));
					hmOvertimeMinute.put("OVERTIME_MAX_MINUTE", rs.getString("max_minute"));
					hmOvertimeMinute.put("ROUNDOFF_MINUTE", rs.getString("roundoff_minute"));

					alOtMinute.add(hmOvertimeMinute);

					hmOvertimeMinuteSlab.put(rs.getString("overtime_id"), alOtMinute);
				}
				rs.close();
				pst.close();

				request.setAttribute("hmOvertimeMinuteSlab", hmOvertimeMinuteSlab);
				// if(uF.parseToInt(strEmpId) == 126){
				// System.out.println("hmOvertimeMinuteSlab"+hmOvertimeMinuteSlab);
				// }
			}

			pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
			pst.setInt(1, uF.parseToInt(userlocation));
			// System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			String locationstarttime = null;
			String locationendtime = null;
			while (rs.next()) {
				locationstarttime = rs.getString("wlocation_start_time");
				locationendtime = rs.getString("wlocation_end_time");
			}
			rs.close();
			pst.close();
			request.setAttribute("locationstarttime", locationstarttime);
			request.setAttribute("locationendtime", locationendtime);
			request.setAttribute("userlocation", userlocation);

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

	public synchronized void getClockEntries(Connection con, UtilityFunctions uF, String strEmpId, String strD1, String strD2, String strPC) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			Map<String, String> hmHolidays = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			Map<String, Map<String, String>> hmWLocationHolidaysName = new HashMap<String, Map<String, String>>();
			Map<String, Set<String>> holidaysMp = CF.getHolidayList(con, request, uF, strD1, strD2);

			CF.getHolidayList(strD1, request, strD2, CF, hmHolidayDates, hmHolidays, true);
			// getWLocationHolidayList(strD1, strD2, CF,
			// hmWLocationHolidaysColour, hmWLocationHolidaysName,
			// hmWLocationHolidaysWeekEnd);
			CF.getWLocationHolidayList(con, uF, strD1, strD2, CF, null, hmWLocationHolidaysName, null);

			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndList = CF.getWeekEndDateList(con, strD1, strD2, CF, uF, hmWeekEndHalfDates, null);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, strD1, strD2, alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEndList, hmEmpLevelMap,
					hmEmpWlocation, hmWeekEndHalfDates);

			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);

			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, String> hmEmpType = CF.getEmpTypeMap(con);
			Map<String, String> hmServices = CF.getServicesMap(con, true);

			Map<String, String> hmLunchDeductionService = CF.getLunchDeductionServiceDetails(con);

			List _alDay = new ArrayList();
			List _alDate = new ArrayList();

			Map<String, String> hmLunchDeduction = new HashMap<String, String>();
			CF.getDeductionTime(con, hmLunchDeduction);

			java.sql.Date dtMin = null;
			java.sql.Date dtMax = null;
			int ii = 0;
			String s = uF.getDateFormat("" + uF.getDateFormat(strD1, DATE_FORMAT), DBDATE, DBDATE);
			String e = uF.getDateFormat("" + uF.getDateFormat(strD2, DATE_FORMAT), DBDATE, DBDATE);
			LocalDate start = LocalDate.parse(s);
			LocalDate end = LocalDate.parse(e);
			while (!start.isAfter(end)) {
				if (dtMin == null) {
					dtMin = new java.sql.Date(start.toDateTimeAtStartOfDay().toDate().getTime());
				}
				dtMax = new java.sql.Date(start.toDateTimeAtStartOfDay().toDate().getTime());

				_alDate.add(uF.getDateFormat("" + start, DBDATE, CF.getStrReportDateFormat()));
				_alDay.add(uF.getDateFormat("" + start, DBDATE, CF.getStrReportDayFormat()));

				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat("" + start, DBDATE, CF.getStrReportDateFormat()))) {
					_alHolidays.add(ii + "");
					_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat("" + start, DBDATE, CF.getStrReportDateFormat())));
				}
				ii++;
				start = start.plusDays(1);
			}

			if (dtMax == null) {
				dtMin = null;
				dtMax = null;
				String s1 = uF.getDateFormat("" + uF.getDateFormat(strD1, DATE_FORMAT), DBDATE, DBDATE);
				String e1 = uF.getDateFormat("" + uF.getDateFormat(strD2, DATE_FORMAT), DBDATE, DBDATE);
				LocalDate start1 = LocalDate.parse(s1);
				LocalDate end1 = LocalDate.parse(e1);
				while (!start1.isAfter(end1)) {
					if (dtMin == null) {
						dtMin = new java.sql.Date(start1.toDateTimeAtStartOfDay().toDate().getTime());
					}
					dtMax = new java.sql.Date(start1.toDateTimeAtStartOfDay().toDate().getTime());

					_alDate.add(uF.getDateFormat("" + start1, DBDATE, CF.getStrReportDateFormat()));
					_alDay.add(uF.getDateFormat("" + start1, DBDATE, CF.getStrReportDayFormat()));

					if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat("" + start1, DBDATE, CF.getStrReportDateFormat()))) {
						_alHolidays.add(ii + "");
						_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat("" + start1, DBDATE, CF.getStrReportDateFormat())));
					}
					ii++;
					start1 = start1.plusDays(1);
				}
			}

			Map<String, Map<String, String>> hmRosterHours = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmRosterServices = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getRosterHoursMap(con, dtMin, dtMax, hmRosterHours, hmRosterServices);
			Map hmRosterHoursEmp = (Map) hmRosterHours.get(strEmpId);
			if (hmRosterHoursEmp == null) {
				hmRosterHoursEmp = new HashMap();
			}

			String strSelectedEmpType = null;
			String strWLocationId = null;
			pst = con.prepareStatement(selectEmployee2Details);
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				strSelectedEmpType = rs.getString("emptype");
				strWLocationId = rs.getString("wlocation_id");
			}
			rs.close();
			pst.close();

			Map hmRosterLunchDeduction = new HashMap();
			Map hmRosterStart = new HashMap();
			Map hmRosterEnd = new HashMap();

			List alDateServices_CE = new ArrayList();
			Map hmDateServices_CE = new HashMap();

			Map<String, Map<String, String>> hmEarlyLateReporting = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getEarlyLateReporting(con, dtMin, dtMax, hmEarlyLateReporting);

			boolean isFixedAdded = false;
			String strOldEmpId = null;
			String strNewEmpId = null;
			String strServiceId = null;
			String strDateNew = null;
			String strDateOld = null;
			List alDateServices_TS = new ArrayList();
			Map hmDateServices_TS = new HashMap();
			List alHours = new ArrayList();
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
			// Map<String, Map<String, String>> hmLeavesMap =
			// CF.getLeaveDates(con, strD1, strD2, CF, hmLeaveDatesType, false,
			// null);
			Map<String, Map<String, String>> hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, hmLeaveDatesType, false, null);
			Map<String, String> hmLeaves = null;
			Map<String, String> hmLeavePaid = new HashMap<String, String>();
			Map<String, String> hmLeavesColour = new HashMap<String, String>();
			CF.getLeavesAttributes(con, uF, hmLeavesColour, null);
			hmLeaves = hmLeavesMap.get(strEmpId);
			if (hmLeaves == null) {
				hmLeaves = new HashMap<String, String>();
			}
			CF.getEmployeePaidMap(con, strEmpId, hmLeavePaid, null);

			pst = con.prepareStatement(selectRosterDetails1);
			pst.setDate(1, dtMin);
			pst.setDate(2, dtMax);
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {

				isFixedAdded = false;
				strNewEmpId = rs.getString("emp_id");
				strServiceId = rs.getString("service_id");
				strDateNew = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());

				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alDateServices_TS = new ArrayList();
				}
				if (strServiceId != null && !alDateServices_TS.contains(strServiceId) && !hmLeaves.containsKey(strDateNew)) {
					alDateServices_TS.add(strServiceId);
					hmDateServices_TS.put(strDateNew, alDateServices_TS);
				}

				hmRosterStart.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId,
						uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hmRosterEnd.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId,
						uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				hmRosterLunchDeduction.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId,
						rs.getString("is_lunch_ded"));

				String strTempDate = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());

				if (!alHours.contains(strTempDate)) {
					alHours.add(strTempDate);
				}

				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alDateServices_CE = new ArrayList();
				}
				if (strServiceId != null && !alDateServices_CE.contains(strServiceId)) {
					alDateServices_CE.add(strServiceId);
					hmDateServices_CE.put(strDateNew, alDateServices_CE);
				}

				strOldEmpId = strNewEmpId;
				strDateOld = strDateNew;
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpCodeDesig = new HashMap<String, String>();

			Map<String, Map<String, String>> hmPayrollFT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmPayrollPT = new HashMap<String, Map<String, String>>();

			CF.getDailyRates(con, hmPayrollFT, hmPayrollPT);

			List alDay = new ArrayList();
			List alDate = new ArrayList();

			List alInOut = new ArrayList();

			Map<String, String> hmHoursActual = new HashMap<String, String>();
			Map<String, String> hmHours = new HashMap<String, String>();
			Map<String, String> hmStart = new HashMap<String, String>();
			Map<String, String> hmStartClockEntries = new HashMap<String, String>();
			Map<String, String> hmActualStartClockEntries = new HashMap<String, String>();
			Map<String, String> hmEnd = new HashMap<String, String>();
			Map<String, String> hmEndClockEntries = new HashMap<String, String>();
			Map<String, String> hmActualEndClockEntries = new HashMap<String, String>();
			Map<String, String> hmHoursRates = new HashMap<String, String>();
			Map<String, String> hmDailyRate = new HashMap<String, String>();
			Map<String, String> hmServicesWorkedFor = new HashMap<String, String>();

			Map<String, String> hmExceptions = new HashMap<String, String>();

			pst = con.prepareStatement(selectClockEntries);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, dtMin);
			pst.setDate(3, dtMax);
			rs = pst.executeQuery();
			long _IN = 0L;
			long _OUT = 0L;
			long _INActual = 0L;
			long _OUTActual = 0L;
			double _TOTALW1 = 0L;
			double _TOTALW2 = 0L;
			double _TOTALRosterW1 = 0L;
			double _TOTALRosterW2 = 0L;
			double _PAYTOTALW1 = 0L;
			double _PAYTOTALW2 = 0L;
			double _PAYTOTAL = 0L;
			double _TOTAL = 0L;
			boolean isOut = false;
			boolean isIn = false;
			boolean isInOut = false;
			String strPayMode = null;
			int dayCount = -1;
			Map hmRate = null;
			java.util.Date strWeek1Date = null;
			int nServiceIdNew = 0;
			int nServiceIdOld = 0;
			while (rs.next()) {

				strDateNew = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
				String str = rs.getString("in_out");
				nServiceIdNew = rs.getInt("service_id");
				int nApproved = rs.getInt("approved");
				if (!alDate.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					alDate.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
					alDay.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDayFormat()));
					_IN = 0L;
					_OUT = 0L;
					_INActual = 0L;
					_OUTActual = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
					dayCount++;
				} else if (nServiceIdNew != nServiceIdOld) {
					_IN = 0L;
					_OUT = 0L;
					_INActual = 0L;
					_OUTActual = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
				}

				if (str != null && str.equalsIgnoreCase("IN") && !isIn) {
					_IN = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					_INActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					hmStart.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					hmStartClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_"
							+ nServiceIdNew, uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));

					hmActualStartClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_"
							+ nServiceIdNew, uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));

					isIn = true;

					if (nApproved == 1) {
						String strTemp = (String) hmRosterStart.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat())
								+ "_" + nServiceIdNew);
						hmRosterStart.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
								uF.showData(strTemp, ""));
					}

					hmExceptions.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew + "_IN",
							rs.getString("approved"));

				} else if (str != null && str.equalsIgnoreCase("OUT") && !isOut) {
					_OUT = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					_OUTActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					hmEnd.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					hmEndClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_"
							+ nServiceIdNew, uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					hmActualEndClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_"
							+ nServiceIdNew, uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));

					isOut = true;

					if (nApproved == 1) {
						String strTemp = (String) hmRosterEnd.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_"
								+ nServiceIdNew);
						hmRosterEnd.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
								uF.showData(strTemp, ""));
					}

					hmExceptions.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew + "_OUT",
							rs.getString("approved"));
				}

				if (nServiceIdNew > 0 && !alDateServices_TS.contains(nServiceIdNew + "")
						&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					alDateServices_TS.add(nServiceIdNew + "");
					hmDateServices_TS.put(strDateNew, alDateServices_TS);
				}

				if (_IN > 0 && _OUT > 0 && !isInOut) {

					int i = _alDate.indexOf(strDateNew);

					/**
					 * LUNCH HOUR CALCULATION FOR DEDUCTION
					 * 
					 * */

					double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(_IN, _OUT));
					double dblHoursWorkedActual = uF.parseToDouble(uF.getTimeDiffInHoursMins(_INActual, _OUTActual));
					double dblLunchTime = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT_TIME));
					double dblLunch = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT));

					boolean isLunchDeductionService = uF.parseToBoolean((String) hmLunchDeductionService.get(nServiceIdNew + ""));
					boolean isLunchDeduct = uF.parseToBoolean((String) hmRosterLunchDeduction.get(uF.getDateFormat(rs.getString("in_out_timestamp"),
							DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew));

					if (dblHoursWorked >= dblLunchTime && isLunchDeduct && isLunchDeductionService) {
						dblHoursWorked = dblHoursWorked - dblLunch;
					}

					hmHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.formatIntoTwoDecimal(dblHoursWorked));
					hmHoursActual.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.formatIntoTwoDecimal(dblHoursWorkedActual));

					String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
					String strDesig = (String) hmEmpCodeDesig.get(strEmpId);

					strServiceId = null;

					hmServicesWorkedFor.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							(String) hmServices.get(nServiceIdNew + ""));

					String strEmpType = (String) hmEmpType.get(strEmpId);

					if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
						hmRate = (HashMap) hmPayrollPT.get("D" + strDesig + "S" + nServiceIdNew);
					} else {
						hmRate = (HashMap) hmPayrollFT.get("D" + strDesig + "S" + nServiceIdNew);
					}

					String strRate = null;
					String strLoading = null;
					if (hmRate != null) {
						strRate = (String) hmRate.get(uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(), CF.getStrReportDayFormat())
								.toUpperCase());
						strLoading = (String) hmRate.get(CF.getLoadingWeekDayCode(uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(),
								CF.getStrReportDayFormat()).toUpperCase()));
					} else {
						hmRate = new HashMap();
					}
					double dblRate = uF.parseToDouble(strRate);

					strPayMode = (String) hmRate.get("PAYMODE");

					if (strPayMode != null && strPayMode.equalsIgnoreCase("H") && !hmLeaves.containsKey(strDate)) {

						double rate = uF.parseToDouble(strRate);
						double loading = uF.parseToDouble(strLoading);
						double rateLoading = 0.0;

						if (strEmpType != null && strEmpType.equalsIgnoreCase("FT")) {
							if (hmHolidayDates != null && hmHolidayDates.containsKey(strDate)) {
								rateLoading = rate + (rate * loading) / 100;
								hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(rateLoading));
							} else {
								rateLoading = rate;
								hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
							}
						} else {
							rateLoading = rate;
							hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
						}

						_PAYTOTAL += uF.convertHoursIntoMinutes(dblHoursWorked) * rateLoading;
						hmHoursRates.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
								uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(dblHoursWorked) * rateLoading) + "");
					} else if (!hmLeaves.containsKey(strDate)) {

						hmHoursRates.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
								"Fixed");
						if (!isFixedAdded) {
							_PAYTOTAL += uF.parseToDouble((String) hmRate.get("FIXED"));
							isFixedAdded = true;
						}

					}

					// _TOTAL = uF.parseToDouble(uF.getTimeDiffInHoursMins(_IN,
					// _OUT));
					_TOTAL = dblHoursWorked;

					isInOut = true;

					// if (_alDate != null && _alDate.size() >= 7) {
					// strWeek1Date = uF.getDateFormatUtil((String)
					// _alDate.get(7), CF.getStrReportDateFormat());
					// }

					if (_alDate != null && _alDate.size() >= 6) {
						strWeek1Date = uF.getDateFormatUtil((String) _alDate.get(6), CF.getStrReportDateFormat());
					}

					java.util.Date currentDate = uF.getDateFormatUtil(
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()), CF.getStrReportDateFormat());
					if (strWeek1Date != null && strWeek1Date.after(currentDate)
							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
						_TOTALW1 += _TOTAL;
						_TOTALRosterW1 += uF.parseToDouble((String) hmRosterHoursEmp.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,
								CF.getStrReportDateFormat())));
					} else if (!hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
						_TOTALW2 += _TOTAL;
						_TOTAL = 0;
						_TOTALRosterW2 += uF.parseToDouble((String) hmRosterHoursEmp.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,
								CF.getStrReportDateFormat())));
					}

				}

				nServiceIdOld = nServiceIdNew;
				strDateOld = strDateNew;

			}
			rs.close();
			pst.close();

			String[] label = new String[_alDate.size()];
			double[] workedHours = new double[_alDate.size()];
			double[] rosterHours = new double[_alDate.size()];

			for (int i = 0; i < _alDate.size(); i++) {
				label[i] = (String) _alDate.get(i);
				workedHours[i] = uF.parseToDouble((String) hmHours.get((String) _alDate.get(i)));
				rosterHours[i] = uF.parseToDouble((String) hmRosterHoursEmp.get((String) _alDate.get(i)));
			}

			/**
			 * START HOLIDAY PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
			 * 
			 * */

			if (strSelectedEmpType != null && strSelectedEmpType.equalsIgnoreCase("FT")) {
				pst = con.prepareStatement(selectSettings);
				rs = pst.executeQuery();
				double hrs = 0;
				while (rs.next()) {
					if (rs.getString("options").equalsIgnoreCase(O_STANDARD_FULL_TIME_HOURS)) {
						hrs = uF.parseToDouble(rs.getString("value"));
					}
				}
				rs.close();
				pst.close();

				Set set = hmHolidayDates.keySet();
				Iterator it = set.iterator();
				while (it.hasNext()) {
					String strDate = (String) it.next();
					Date utDate = uF.getDateFormatUtil(strDate, CF.getStrReportDateFormat());

					if (!hmHours.containsKey(strDate) && !hmLeaves.containsKey(strDate)) {

						hmStart.put(strDate + "_", PublicHoliday);

						hmHours.put(strDate + "_", uF.formatIntoTwoDecimal(hrs));

						strServiceId = "";
						String strEmpType = (String) hmEmpType.get(strEmpId);

						if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
							hmRate = (HashMap) hmPayrollPT.get("D" + strEmpId + "S" + strServiceId);
						} else {
							hmRate = (HashMap) hmPayrollFT.get("D" + strEmpId + "S" + strServiceId);
						}
						if (hmRate == null) {
							hmRate = new HashMap();
						}

						String strRate = (String) hmRate.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase());
						hmDailyRate.put(strDate + "_", uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
						hmHoursRates.put(strDate + "_", uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(hrs * uF.parseToDouble(strRate))));

						if (strWeek1Date != null && strWeek1Date.after(utDate)) {
							_TOTALW1 += hrs;
							_PAYTOTALW1 += hrs * uF.parseToDouble(strRate);
							_PAYTOTAL += hrs * uF.parseToDouble(strRate);
						} else {
							_TOTALW2 += hrs;
							_PAYTOTAL += hrs * uF.parseToDouble(strRate);
						}

					}
				}

				/**
				 * END HOLIDAY PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
				 * 
				 * */

				/**
				 * LEAVE MANAGEMENT
				 * */

				Set setLeaves = hmLeaves.keySet();
				Iterator itLeaves = setLeaves.iterator();
				while (itLeaves.hasNext()) {
					String strDate = (String) itLeaves.next();
					Date utDate = uF.getDateFormatUtil(strDate, DATE_FORMAT);

					String strLeaveType = (String) hmLeaves.get(strDate);
					boolean isPaidLeave = uF.parseToBoolean((String) hmLeavePaid.get(strLeaveType));

					hmStart.put(strDate + "_", (String) hmLeaves.get(strDate));
					hrs = LeaveHours; // Leaves standard hours are different
										// from holidays hours for Oracle CMS
										// client

					if (!isPaidLeave)
						continue;

					hmHours.put(strDate + "_", uF.formatIntoTwoDecimal(hrs));
					strServiceId = "";
					String strEmpType = (String) hmEmpType.get(strEmpId);

					if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
						hmRate = (HashMap) hmPayrollPT.get("D" + strEmpId + "S" + strServiceId);
					} else {
						hmRate = (HashMap) hmPayrollFT.get("D" + strEmpId + "S" + strServiceId);
					}
					if (hmRate == null) {
						hmRate = new HashMap();
					}

					// String strRate = (String)
					// hmRate.get(uF.getDateFormat(strDate,
					// CF.getStrReportDateFormat(),
					// CF.getStrReportDayFormat()).toUpperCase());

					String strRate = CF.getMinimumRateForPublicHolidays(con, strEmpId, uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDayFormat())) + "";
					hmDailyRate.put(strDate + "_", uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
					hmHoursRates.put(strDate + "_", uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(hrs * uF.parseToDouble(strRate))));

					if (strWeek1Date != null && strWeek1Date.after(utDate)) {
						_TOTALW1 += hrs;
						_PAYTOTALW1 += hrs * uF.parseToDouble(strRate);
						_PAYTOTAL += hrs * uF.parseToDouble(strRate);
					} else {
						_TOTALW2 += hrs;
						_PAYTOTAL += hrs * uF.parseToDouble(strRate);
					}
				}
			}

			/**
			 * END LEAVE PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
			 * 
			 * */

			// request.setAttribute("CHART_RSTER_VS_ACTUAL", new
			// BarChart().getChartWithMarks(workedHours, rosterHours, label));

			// System.out.println("hmHolidayDates=====>"+hmHolidayDates);
			// System.out.println("hmWeekEndList=====>"+hmWeekEndList);
			request.setAttribute("alInOut", alInOut);
			request.setAttribute("alDate", _alDate);
			request.setAttribute("alDay", _alDay);
			request.setAttribute("hmHours", hmHours);
			request.setAttribute("hmHoursActual", hmHoursActual);
			request.setAttribute("hmStart", hmStart);
			request.setAttribute("hmStartClockEntries", hmStartClockEntries);
			request.setAttribute("hmActualStartClockEntries", hmActualStartClockEntries);

			request.setAttribute("hmRosterStart", hmRosterStart);
			request.setAttribute("hmRosterEnd", hmRosterEnd);
			request.setAttribute("hmEnd", hmEnd);
			request.setAttribute("hmEndClockEntries", hmEndClockEntries);
			request.setAttribute("hmActualEndClockEntries", hmActualEndClockEntries);
			request.setAttribute("TOTALW1", _TOTALW1 + "");
			request.setAttribute("TOTALW2", _TOTALW2 + "");
			request.setAttribute("_TOTALRosterW1", _TOTALRosterW1 + "");
			request.setAttribute("_TOTALRosterW2", _TOTALRosterW2 + "");
			request.setAttribute("PAYTOTALW1", _PAYTOTALW1 + "");
			request.setAttribute("PAYTOTALW2", _PAYTOTALW2 + "");
			request.setAttribute("_PAYTOTAL", _PAYTOTAL + "");
			request.setAttribute("hmDateServices_CE", hmDateServices_CE);
			request.setAttribute("hmDateServices_TS", hmDateServices_TS);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("_alHolidays", _alHolidays);
			request.setAttribute("hmLeaves", hmLeaves);
			request.setAttribute("EMP_NAME", hmEmpNameMap.get(strEmpId));
			request.setAttribute("EMPID", strEmpId);
			request.setAttribute("hmLeavesColour", hmLeavesColour);
			request.setAttribute("hmExceptions", hmExceptions);

			// System.out.println("hmLeaves====>"+hmLeaves);

			Map<String, String> hmFirstAidAllowance = new HashMap<String, String>();
			CF.getAllowanceMap(con, hmFirstAidAllowance);

			double dblAllowance = 0;
			if (hmFirstAidAllowance.containsKey(strEmpId)) {
				dblAllowance = CF.getAllowanceValue(con, _TOTALW1 + _TOTALW2, uF.parseToInt(strEmpId));
			}

			request.setAttribute("ALLOWANCE", uF.formatIntoTwoDecimal(dblAllowance));
			request.setAttribute("DEDUCTION", CF.getDeductionAmountMap(con, _PAYTOTAL) + "");
			request.setAttribute("hmWeekEndList", hmWeekEndList);
			request.setAttribute("strWLocationId", strWLocationId);
			request.setAttribute("hmDailyRate", hmDailyRate);
			request.setAttribute("hmHoursRates", hmHoursRates);
			request.setAttribute("strPayMode", strPayMode);
			if (hmRate == null) {
				hmRate = new HashMap();
			}
			request.setAttribute("FIXED", (String) hmRate.get("FIXED"));

			request.setAttribute("hmWLocationHolidaysName", hmWLocationHolidaysName);
			request.setAttribute("_hmHolidaysColour", _hmHolidaysColour);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmServicesWorkedFor", hmServicesWorkedFor);
			request.setAttribute("hmRosterHours", hmRosterHoursEmp);

			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);

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

	private synchronized void setReimbursementCTC(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String currDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strFinancialDates = CF.getFinancialYear(con, currDate, CF, uF);

			// int nMonth = uF.parseToInt(uF.getDateFormat(""+currDate,
			// DATE_FORMAT, "MM"));
			int nYear = uF.parseToInt(uF.getDateFormat("" + currDate, DATE_FORMAT, "yyyy"));

			if (strFinancialDates != null && strFinancialDates.length > 0) {
				String strFinancialYearStart = strFinancialDates[0];
				String strFinancialYearEnd = strFinancialDates[1];

				pst = con.prepareStatement("select org_id from org_details order by org_id");
				rs = pst.executeQuery();
				List<String> alOrg = new ArrayList<String>();
				while (rs.next()) {
					alOrg.add(rs.getString("org_id"));
				}
				rs.close();
				pst.close();

				for (String strOrgId : alOrg) {
					int nOrgId = uF.parseToInt(strOrgId);
					String[] strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strOrgId);
					if (strPayCycleDates != null && strPayCycleDates.length > 0 && strPayCycleDates[0] != null && !strPayCycleDates[0].trim().equals("")
							&& !strPayCycleDates[0].trim().equalsIgnoreCase("NULL") && strPayCycleDates[1] != null && !strPayCycleDates[1].trim().equals("")
							&& !strPayCycleDates[1].trim().equalsIgnoreCase("NULL") && uF.parseToInt(strPayCycleDates[2]) > 0) {

						String strD1 = strPayCycleDates[0];
						String strD2 = strPayCycleDates[1];
						String strPC = strPayCycleDates[2];

						// String strMonth =
						// uF.parseToInt(uF.getDateFormat(""+strD2, DATE_FORMAT,
						// "MM")) != 0 &&
						// uF.parseToInt(uF.getDateFormat(""+strD2, DATE_FORMAT,
						// "MM")) <=9 ?
						// "0"+uF.parseToInt(uF.getDateFormat(""+strD2,
						// DATE_FORMAT, "MM")) :
						// ""+uF.parseToInt(uF.getDateFormat(""+strD2,
						// DATE_FORMAT, "MM"));
						// boolean isDateBetween =
						// uF.isDateBetween(uF.getDateFormatUtil(strD1,
						// DATE_FORMAT),
						// uF.getDateFormatUtil("10/"+strMonth+"/"+nYear,
						// DATE_FORMAT), uF.getDateFormatUtil(currDate,
						// DATE_FORMAT));
						// System.out.println("orgid====>"+strOrgId+"----isDateBetween====>"+isDateBetween+"----date====>"+"10/"+strMonth+"/"+nYear+"--strD1===>"+uF.getDateFormatUtil(strD1,
						// DATE_FORMAT)+"--strD2===>"+uF.getDateFormatUtil(strD2,
						// DATE_FORMAT)+"--currDate===>"+currDate);
						// if(isDateBetween){
						pst = con.prepareStatement("select level_id from level_details where org_id=? and level_id in (select level_id "
								+ "from reimbursement_ctc_details where org_id=?)");
						pst.setInt(1, nOrgId);
						pst.setInt(2, nOrgId);
						rs = pst.executeQuery();
						List<String> alLevel = new ArrayList<String>();
						while (rs.next()) {
							alLevel.add(rs.getString("level_id"));
						}
						rs.close();
						pst.close();

						for (String strLevelId : alLevel) {
							int nLevelId = uF.parseToInt(strLevelId);

							pst = con.prepareStatement("select eod.emp_id from employee_personal_details epd, employee_official_details eod "
									+ "where epd.emp_per_id = eod.emp_id and is_alive = true and eod.grade_id in (select grade_id "
									+ "from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id "
									+ "and dd.level_id = ld.level_id and ld.level_id=?) order by emp_id");
							pst.setInt(1, nLevelId);
							// System.out.println("pst====>"+pst);
							rs = pst.executeQuery();
							List<String> alEmp = new ArrayList<String>();
							while (rs.next()) {
								alEmp.add(rs.getString("emp_id"));
							}
							rs.close();
							pst.close();

							if (!alEmp.isEmpty() && alEmp.size() > 0) {
								Map<String, List<Map<String, String>>> hmReimbursementCTCHead = new HashMap<String, List<Map<String, String>>>();
								pst = con
										.prepareStatement("select rhd.reimbursement_ctc_id,rhd.reimbursement_head_id,rhad.amount,rhad.is_optimal "
												+ "from reimbursement_head_details rhd, reimbursement_head_amt_details rhad where rhd.reimbursement_head_id=rhad.reimbursement_head_id "
												+ "and rhd.level_id=? and rhd.org_id=? and rhad.financial_year_start=? and rhad.financial_year_end=?");
								pst.setInt(1, nLevelId);
								pst.setInt(2, nOrgId);
								pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								// System.out.println("pst====>"+pst);
								rs = pst.executeQuery();
								while (rs.next()) {
									List<Map<String, String>> outerList = hmReimbursementCTCHead.get(rs.getString("reimbursement_ctc_id"));
									if (outerList == null)
										outerList = new ArrayList<Map<String, String>>();

									Map<String, String> hmReimCTCHead = new HashMap<String, String>();
									hmReimCTCHead.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursement_head_id"));
									hmReimCTCHead.put("REIMBURSEMENT_HEAD_AMOUNT", uF.showData(rs.getString("amount"), ""));
									hmReimCTCHead.put("REIMBURSEMENT_HEAD_IS_OPTIMAL", "" + uF.parseToBoolean(rs.getString("is_optimal")));

									outerList.add(hmReimCTCHead);

									hmReimbursementCTCHead.put(rs.getString("reimbursement_ctc_id"), outerList);

								}
								rs.close();
								pst.close();

								Iterator<String> it = hmReimbursementCTCHead.keySet().iterator();
								while (it.hasNext()) {
									String strReimCTCId = it.next();
									//
									List<Map<String, String>> outerList = hmReimbursementCTCHead.get(strReimCTCId);
									if (outerList == null)
										outerList = new ArrayList<Map<String, String>>();
									int nOuterList = outerList.size();
									for (int i = 0; i < nOuterList; i++) {
										Map<String, String> hmReimCTCHead = outerList.get(i);
										String strReimCTCHeadId = hmReimCTCHead.get("REIMBURSEMENT_HEAD_ID");
										double dblAmount = uF.parseToDouble(hmReimCTCHead.get("REIMBURSEMENT_HEAD_AMOUNT"));
										boolean isOptimal = uF.parseToBoolean(hmReimCTCHead.get("REIMBURSEMENT_HEAD_IS_OPTIMAL"));

										for (String strEmpId : alEmp) {
											int nEmpId = uF.parseToInt(strEmpId);
											//
											pst = con
													.prepareStatement("select * from reimbursement_assign_head_details where emp_id=? and reimbursement_head_id=? "
															+ "and reimbursement_ctc_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? "
															+ "and paycycle_from=? and paycycle_to=? and paycycle=? and trail_status=?");
											pst.setInt(1, nEmpId);
											pst.setInt(2, uF.parseToInt(strReimCTCHeadId));
											pst.setInt(3, uF.parseToInt(strReimCTCId));
											pst.setInt(4, nLevelId);
											pst.setInt(5, nOrgId);
											pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
											pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
											pst.setDate(8, uF.getDateFormat(strD1, DATE_FORMAT));
											pst.setDate(9, uF.getDateFormat(strD2, DATE_FORMAT));
											pst.setInt(10, uF.parseToInt(strPC));
											pst.setBoolean(11, true);
											// if(nEmpId == 847){
											// System.out.println("pst==>"+pst);
											// }
											rs = pst.executeQuery();
											boolean assignStatus = false;
											if (rs.next()) {
												assignStatus = true;
											}
											rs.close();
											pst.close();
											//
											if (!assignStatus) {
												pst = con.prepareStatement("select * from reimbursement_assign_head_details where emp_id=? "
														+ "and level_id=? and org_id=? and reimbursement_ctc_id=? and reimbursement_head_id=? "
														+ "and financial_year_start=? and financial_year_end=? and trail_status=true "
														+ "and paycycle in (select max(paycycle) as paycycle from reimbursement_assign_head_details "
														+ "where emp_id=? and level_id=? and org_id=? and reimbursement_ctc_id=? "
														+ "and reimbursement_head_id=? and financial_year_start=? and financial_year_end=? "
														+ "and trail_status=true)");
												pst.setInt(1, nEmpId);
												pst.setInt(2, nLevelId);
												pst.setInt(3, nOrgId);
												pst.setInt(4, uF.parseToInt(strReimCTCId));
												pst.setInt(5, uF.parseToInt(strReimCTCHeadId));
												pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
												pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
												pst.setInt(8, nEmpId);
												pst.setInt(9, nLevelId);
												pst.setInt(10, nOrgId);
												pst.setInt(11, uF.parseToInt(strReimCTCId));
												pst.setInt(12, uF.parseToInt(strReimCTCHeadId));
												pst.setDate(13, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
												pst.setDate(14, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
												// if(nEmpId == 847){
												// System.out.println("pst==>"+pst);
												// }
												rs = pst.executeQuery();
												boolean statusFlag = false;
												boolean availableFlag = false;
												while (rs.next()) {
													statusFlag = uF.parseToBoolean(rs.getString("status"));
													availableFlag = true;
												}
												rs.close();
												pst.close();
												//
												if (availableFlag) {
													pst = con
															.prepareStatement("insert into reimbursement_assign_head_details (emp_id,reimbursement_head_id,"
																	+ "reimbursement_ctc_id,level_id,org_id,amount,financial_year_start,financial_year_end,status,trail_status,"
																	+ "update_by,update_date,paycycle_from,paycycle_to,paycycle) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
													pst.setInt(1, nEmpId);
													pst.setInt(2, uF.parseToInt(strReimCTCHeadId));
													pst.setInt(3, uF.parseToInt(strReimCTCId));
													pst.setInt(4, nLevelId);
													pst.setInt(5, nOrgId);
													pst.setDouble(6, dblAmount);
													pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
													pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
													pst.setBoolean(9, statusFlag);
													pst.setBoolean(10, true);
													pst.setInt(11, 1);
													pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
													pst.setDate(13, uF.getDateFormat(strD1, DATE_FORMAT));
													pst.setDate(14, uF.getDateFormat(strD2, DATE_FORMAT));
													pst.setInt(15, uF.parseToInt(strPC));
													// System.out.println("pst====>"+pst);
													pst.execute();
												} else {
													statusFlag = false;
													if (!isOptimal) {
														statusFlag = true;
													}

													pst = con
															.prepareStatement("insert into reimbursement_assign_head_details (emp_id,reimbursement_head_id,"
																	+ "reimbursement_ctc_id,level_id,org_id,amount,financial_year_start,financial_year_end,status,trail_status,"
																	+ "update_by,update_date,paycycle_from,paycycle_to,paycycle) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
													pst.setInt(1, nEmpId);
													pst.setInt(2, uF.parseToInt(strReimCTCHeadId));
													pst.setInt(3, uF.parseToInt(strReimCTCId));
													pst.setInt(4, nLevelId);
													pst.setInt(5, nOrgId);
													pst.setDouble(6, dblAmount);
													pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
													pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
													pst.setBoolean(9, statusFlag);
													pst.setBoolean(10, true);
													pst.setInt(11, 1);
													pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
													pst.setDate(13, uF.getDateFormat(strD1, DATE_FORMAT));
													pst.setDate(14, uF.getDateFormat(strD2, DATE_FORMAT));
													pst.setInt(15, uF.parseToInt(strPC));
													// System.out.println("pst====>"+pst);
													pst.execute();
												}
											}
										}
									}
								}
							}
						}
						// }
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void setPerkInSalaryDetails(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String currDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strFinancialDates = CF.getFinancialYear(con, currDate, CF, uF);

			// int nMonth = uF.parseToInt(uF.getDateFormat(""+currDate,
			// DATE_FORMAT, "MM"));
			int nYear = uF.parseToInt(uF.getDateFormat("" + currDate, DATE_FORMAT, "yyyy"));

			if (strFinancialDates != null && strFinancialDates.length > 0) {
				String strFinancialYearStart = strFinancialDates[0];
				String strFinancialYearEnd = strFinancialDates[1];

				pst = con.prepareStatement("select org_id from org_details order by org_id");
				rs = pst.executeQuery();
				List<String> alOrg = new ArrayList<String>();
				while (rs.next()) {
					alOrg.add(rs.getString("org_id"));
				}
				rs.close();
				pst.close();

				for (String strOrgId : alOrg) {
					int nOrgId = uF.parseToInt(strOrgId);
					String[] strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strOrgId);
					if (strPayCycleDates != null && strPayCycleDates.length > 0 && strPayCycleDates[0] != null && !strPayCycleDates[0].trim().equals("")
							&& !strPayCycleDates[0].trim().equalsIgnoreCase("NULL") && strPayCycleDates[1] != null && !strPayCycleDates[1].trim().equals("")
							&& !strPayCycleDates[1].trim().equalsIgnoreCase("NULL") && uF.parseToInt(strPayCycleDates[2]) > 0) {

						String strD1 = strPayCycleDates[0];
						String strD2 = strPayCycleDates[1];
						String strPC = strPayCycleDates[2];

						String strMonth = uF.parseToInt(uF.getDateFormat("" + strD2, DATE_FORMAT, "MM")) != 0
								&& uF.parseToInt(uF.getDateFormat("" + strD2, DATE_FORMAT, "MM")) <= 9 ? "0"
								+ uF.parseToInt(uF.getDateFormat("" + strD2, DATE_FORMAT, "MM")) : ""
								+ uF.parseToInt(uF.getDateFormat("" + strD2, DATE_FORMAT, "MM"));
						boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT),
								uF.getDateFormatUtil("10/" + strMonth + "/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
						// System.out.println("orgid====>"+strOrgId+"----isDateBetween====>"+isDateBetween+"----date====>"+"28/"+strMonth+"/"+nYear+"--strD1===>"+uF.getDateFormatUtil(strD1,
						// DATE_FORMAT)+"--strD2===>"+uF.getDateFormatUtil(strD2,
						// DATE_FORMAT)+"--currDate===>"+currDate);
						if (isDateBetween) {
							pst = con.prepareStatement("select level_id from level_details where org_id=? and level_id in (select level_id "
									+ "from salary_details where org_id=? and is_align_with_perk=true and (is_delete is null or is_delete=false))");
							pst.setInt(1, nOrgId);
							pst.setInt(2, nOrgId);
							rs = pst.executeQuery();
							List<String> alLevel = new ArrayList<String>();
							while (rs.next()) {
								alLevel.add(rs.getString("level_id"));
							}
							rs.close();
							pst.close();

							for (String strLevelId : alLevel) {
								int nLevelId = uF.parseToInt(strLevelId);

								pst = con.prepareStatement("select eod.emp_id from employee_personal_details epd, employee_official_details eod "
										+ "where epd.emp_per_id = eod.emp_id and is_alive = true and eod.grade_id in (select grade_id "
										+ "from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id "
										+ "and dd.level_id = ld.level_id and ld.level_id=?) order by emp_id");
								pst.setInt(1, nLevelId);
								rs = pst.executeQuery();
								List<String> alEmp = new ArrayList<String>();
								while (rs.next()) {
									alEmp.add(rs.getString("emp_id"));
								}
								rs.close();
								pst.close();

								if (!alEmp.isEmpty() && alEmp.size() > 0) {
									Map<String, List<Map<String, String>>> hmPerkAlignSalary = new HashMap<String, List<Map<String, String>>>();
									pst = con.prepareStatement("SELECT * FROM perk_salary_details where org_id=? and level_id=? and financial_year_start=? "
											+ "and financial_year_end=? and salary_head_id in (select salary_head_id from salary_details where org_id=? "
											+ "and level_id=? and (is_delete is null or is_delete =false))");
									pst.setInt(1, nOrgId);
									pst.setInt(2, nLevelId);
									pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
									pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
									pst.setInt(5, nOrgId);
									pst.setInt(6, nLevelId);
									rs = pst.executeQuery();
									while (rs.next()) {
										List<Map<String, String>> outerList = hmPerkAlignSalary.get(rs.getString("salary_head_id"));
										if (outerList == null)
											outerList = new ArrayList<Map<String, String>>();

										Map<String, String> hmPerkSalary = new HashMap<String, String>();
										hmPerkSalary.put("PERK_SALARY_ID", rs.getString("perk_salary_id"));
										hmPerkSalary.put("PERK_AMOUNT", uF.showData(rs.getString("amount"), ""));

										outerList.add(hmPerkSalary);

										hmPerkAlignSalary.put(rs.getString("salary_head_id"), outerList);

									}
									rs.close();
									pst.close();

									Iterator<String> it = hmPerkAlignSalary.keySet().iterator();
									while (it.hasNext()) {
										String strSalaryHeadId = it.next();

										List<Map<String, String>> outerList = hmPerkAlignSalary.get(strSalaryHeadId);
										if (outerList == null)
											outerList = new ArrayList<Map<String, String>>();
										int nOuterList = outerList.size();
										for (int i = 0; i < nOuterList; i++) {
											Map<String, String> hmPerkSalary = outerList.get(i);
											String strPerkSalaryId = hmPerkSalary.get("PERK_SALARY_ID");
											double dblAmount = uF.parseToDouble(hmPerkSalary.get("PERK_AMOUNT"));

											for (String strEmpId : alEmp) {
												int nEmpId = uF.parseToInt(strEmpId);

												pst = con
														.prepareStatement("select * from perk_assign_salary_details where emp_id=? and perk_salary_id=? "
																+ "and salary_head_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? "
																+ "and paycycle_from=? and paycycle_to=? and paycycle=? and trail_status=?");
												pst.setInt(1, nEmpId);
												pst.setInt(2, uF.parseToInt(strPerkSalaryId));
												pst.setInt(3, uF.parseToInt(strSalaryHeadId));
												pst.setInt(4, nLevelId);
												pst.setInt(5, nOrgId);
												pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
												pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
												pst.setDate(8, uF.getDateFormat(strD1, DATE_FORMAT));
												pst.setDate(9, uF.getDateFormat(strD2, DATE_FORMAT));
												pst.setInt(10, uF.parseToInt(strPC));
												pst.setBoolean(11, true);
												rs = pst.executeQuery();
												boolean assignStatus = false;
												if (rs.next()) {
													assignStatus = true;
												}
												rs.close();
												pst.close();

												if (!assignStatus) {
													pst = con.prepareStatement("select * from perk_assign_salary_details where emp_id=? "
															+ "and level_id=? and org_id=? and salary_head_id=? and perk_salary_id=? "
															+ "and financial_year_start=? and financial_year_end=? and trail_status=true "
															+ "and paycycle in (select max(paycycle) as paycycle from perk_assign_salary_details "
															+ "where emp_id=? and level_id=? and org_id=? and salary_head_id=? and perk_salary_id=? "
															+ "and financial_year_start=? and financial_year_end=? and trail_status=true)");
													pst.setInt(1, nEmpId);
													pst.setInt(2, nLevelId);
													pst.setInt(3, nOrgId);
													pst.setInt(4, uF.parseToInt(strSalaryHeadId));
													pst.setInt(5, uF.parseToInt(strPerkSalaryId));
													pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
													pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
													pst.setInt(8, nEmpId);
													pst.setInt(9, nLevelId);
													pst.setInt(10, nOrgId);
													pst.setInt(11, uF.parseToInt(strSalaryHeadId));
													pst.setInt(12, uF.parseToInt(strPerkSalaryId));
													pst.setDate(13, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
													pst.setDate(14, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
													rs = pst.executeQuery();
													boolean statusFlag = false;
													boolean availableFlag = false;
													while (rs.next()) {
														statusFlag = uF.parseToBoolean(rs.getString("status"));
														availableFlag = true;
													}
													rs.close();
													pst.close();

													if (availableFlag) {
														pst = con
																.prepareStatement("insert into perk_assign_salary_details (emp_id,perk_salary_id,salary_head_id,level_id,"
																		+ "org_id,amount,financial_year_start,financial_year_end,status,trail_status,update_by,"
																		+ "update_date,paycycle_from,paycycle_to,paycycle) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
														pst.setInt(1, nEmpId);
														pst.setInt(2, uF.parseToInt(strPerkSalaryId));
														pst.setInt(3, uF.parseToInt(strSalaryHeadId));
														pst.setInt(4, nLevelId);
														pst.setInt(5, nOrgId);
														pst.setDouble(6, dblAmount);
														pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
														pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
														pst.setBoolean(9, statusFlag);
														pst.setBoolean(10, true);
														pst.setInt(11, 1);
														pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
														pst.setDate(13, uF.getDateFormat(strD1, DATE_FORMAT));
														pst.setDate(14, uF.getDateFormat(strD2, DATE_FORMAT));
														pst.setInt(15, uF.parseToInt(strPC));
														// System.out.println("pst====>"+pst);
														pst.execute();
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void carriedForwardLeave(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// System.out.println("carriedForwardLeave====>");
			Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
			if (hmOrgMap == null)
				hmOrgMap = new HashMap<String, Map<String, String>>();

			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if (hmEmpLevelMap == null)
				hmEmpLevelMap = new HashMap<String, String>();
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			if (hmEmpWlocationMap == null)
				hmEmpWlocationMap = new HashMap<String, String>();

			// Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			String currDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			int nMonth = uF.parseToInt(uF.getDateFormat("" + currDate, DATE_FORMAT, "MM"));
			int nYear = uF.parseToInt(uF.getDateFormat("" + currDate, DATE_FORMAT, "yyyy"));

			Iterator<String> it = hmOrgMap.keySet().iterator();
			while (it.hasNext()) {
				String strOrgId = (String) it.next();
				Map<String, String> hmOrg = hmOrgMap.get(strOrgId);
//				 System.out.println("hmOrg ===>> " + hmOrg);
				if (hmOrg.get("ORG_START_PAYCYCLE") == null || hmOrg.get("ORG_START_PAYCYCLE").trim().equals("")
						|| hmOrg.get("ORG_START_PAYCYCLE").trim().equalsIgnoreCase("NULL") || hmOrg.get("ORG_START_PAYCYCLE").trim().equals("-")) {
					continue;
				}
//				 System.out.println("ORG_START_PAYCYCLE ===>> " + hmOrg.get("ORG_START_PAYCYCLE"));

				pst = con.prepareStatement("select * from emp_leave_type elt, leave_type lt where elt.leave_type_id=lt.leave_type_id and "
						+ "elt.leave_type_id in (select leave_type_id from leave_type where is_compensatory = false and org_id=?) "
						+ "and elt.is_constant_balance=false and elt.org_id=? and elt.is_carryforward=true and "
						+ "(lt.is_leave_opt_holiday is null or lt.is_leave_opt_holiday=false) order by elt.level_id");
				pst.setInt(1, uF.parseToInt(strOrgId));
				pst.setInt(2, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				List<String> alLeaveType = new ArrayList<String>();
				Map<String, String> hmCarryForwardLeavesBal = new HashMap<String, String>();
				Map<String, String> hmLeavesType = new HashMap<String, String>();
				Map<String, List<String>> hmLeaves = new HashMap<String, List<String>>();
				Map<String, Map<String, String>> hmCarriedLimit = new HashMap<String, Map<String, String>>();
				Map<String, String> hmLeavesAccrualStatus = new HashMap<String, String>();
				Map<String, String> hmTotalLeavesMonthly = new HashMap<String, String>();
				Map<String, String> hmLeavesAccrualDays = new HashMap<String, String>();
				while (rs.next()) {
					if (uF.parseToInt(rs.getString("leave_type_id")) > 0) {
						if (!alLeaveType.contains(rs.getString("leave_type_id"))) {
							alLeaveType.add(rs.getString("leave_type_id"));
						}
						hmCarryForwardLeavesBal.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("no_of_leave"));
						hmLeavesType.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("effective_date_type"));

						hmLeavesAccrualStatus.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("is_leave_accrual"));

						List<String> alLeave = hmLeaves.get(rs.getString("level_id") + "_" + rs.getString("wlocation_id"));
						if (alLeave == null)
							alLeave = new ArrayList<String>();
						alLeave.add(rs.getString("leave_type_id"));

						hmLeaves.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id"), alLeave);

						Map<String, String> hmCarriedForwordLimit = new HashMap<String, String>();
						hmCarriedForwordLimit.put("IS_CARRYFORWARD_LIMIT", rs.getString("is_carryforward_limit"));
						hmCarriedForwordLimit.put("CARRYFORWARD_LIMIT", rs.getString("carryforward_limit"));
						hmCarriedLimit.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								hmCarriedForwordLimit);

						hmLeavesAccrualDays.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("is_accrued_cal_days"));
						hmTotalLeavesMonthly.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("no_of_leave_monthly"));
					}
				}
				rs.close();
				pst.close();

//				System.out.println("CD/9996---in--carriedForwardLeave---alLeaveType ===>> " + alLeaveType);
				if (alLeaveType.size() > 0) {
					pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod, probation_policy pp "
							+ "where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id and is_alive = true "
							+ "and emp_per_id>0 and eod.org_id=? and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') "
							+ "and epd.joining_date is not null order by eod.emp_id");
					pst.setInt(1, uF.parseToInt(strOrgId));
					rs = pst.executeQuery();
					List<String> alEmp = new ArrayList<String>();
					Map<String, List<String>> hmEmpLeaves = new HashMap<String, List<String>>();
					while (rs.next()) {
						int nLevelId = uF.parseToInt(hmEmpLevelMap.get(rs.getString("emp_id")));

						List<String> alLeave = hmLeaves.get(nLevelId + "_" + rs.getString("wlocation_id"));
						if (alLeave == null)
							alLeave = new ArrayList<String>();

						String strAllowedLeaves = rs.getString("leaves_types_allowed");
						if (strAllowedLeaves != null && strAllowedLeaves.length() > 0) {
							List<String> al = Arrays.asList(strAllowedLeaves.split(","));
							for (String leaveTypeId : al) {
								if (uF.parseToInt(leaveTypeId) > 0 && alLeaveType.contains(leaveTypeId) && alLeave.contains(leaveTypeId)) {
									if (!alEmp.contains(rs.getString("emp_id"))) {
										alEmp.add(rs.getString("emp_id"));
									}

									List<String> alEmpLeave = hmEmpLeaves.get(rs.getString("emp_id"));
									if (alEmpLeave == null)
										alEmpLeave = new ArrayList<String>();
									alEmpLeave.add(leaveTypeId);

									hmEmpLeaves.put(rs.getString("emp_id"), alEmpLeave);
								}
							}
						}
					}
					rs.close();
					pst.close();

					// alEmp = new ArrayList<String>();
					// alEmp.add("168");

					int nAlEmp = alEmp.size();
					for (int i = 0; i < nAlEmp; i++) {
						int nEmpId = uF.parseToInt(alEmp.get(i));
						int nLevelId = uF.parseToInt(hmEmpLevelMap.get(alEmp.get(i)));
						int nLocationId = uF.parseToInt(hmEmpWlocationMap.get(alEmp.get(i)));
						List<String> alEmpLeave = hmEmpLeaves.get(alEmp.get(i));
						if (alEmpLeave == null)
							alEmpLeave = new ArrayList<String>();
						if (nEmpId == 143) {
							// System.out.println(nEmpId +
							// " -- alEmpLeave ===>> " + alEmpLeave);
						}
						for (int j = 0; j < alLeaveType.size(); j++) {
							int nLeaveTypeId = uF.parseToInt(alLeaveType.get(j));
							if (!hmCarryForwardLeavesBal.containsKey(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId)
									|| !alEmpLeave.contains("" + nLeaveTypeId)) {
								continue;
							}
							String strEffectiveLeaveType = hmLeavesType.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId);
//							if (nEmpId == 445) {
//								 System.out.println("CD/10058--in--carriedForwardLeave() -- strEffectiveLeaveType ===>> " + strEffectiveLeaveType);
//							}
							if (strEffectiveLeaveType != null && strEffectiveLeaveType.equals("CY")) {
								if (nMonth == 1) {
									String startDate = "01/01/" + nYear;

									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/01/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (isDateBetween && uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
										pst = con.prepareStatement("delete from leave_register1 where emp_id = ? and leave_type_id=? and _date = ?");
										pst.setInt(1, nEmpId);
										pst.setInt(2, nLeaveTypeId);
										pst.setDate(3, uF.getDateFormat(calStartDate, DATE_FORMAT));
										int x = pst.executeUpdate();
										pst.close();
										if (x > 0) {
											flag = false;
										}
									}

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}
											// System.out.println("dblCarryForwardBalance==>"+dblCarryForwardBalance);

											pst = con
													.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 "
															+ "where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?)"
															+ " group by emp_id,leave_type_id) and leave_type_id=?");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nLeaveTypeId);
											// System.out.println("pst==>"+pst);
											rs = pst.executeQuery();
											Map<String, String> hmMainBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
											}
											rs.close();
											pst.close();
											// System.out.println("hmMainBalance==>"+hmMainBalance);

											pst = con
													.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id "
															+ "and a.daa<=lr._date and lr.leave_type_id=? and lr._date<? group by a.leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											// System.out.println("pst==>"+pst);
											rs = pst.executeQuery();
											Map<String, String> hmAccruedBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));
											}
											rs.close();
											pst.close();
											// System.out.println("hmAccruedBalance==>"+hmAccruedBalance);
											// pst =
											// con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id "
											// +
											// "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
											// +
											// "group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) "
											// +
											// "and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=?) as a group by leave_type_id");
											// pst.setInt(1, nEmpId);
											// pst.setInt(2, nLeaveTypeId);
											// pst.setInt(3, nEmpId);
											// pst.setInt(4, nLeaveTypeId);
											pst = con
													.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' "
															+ "and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) group by emp_id,leave_type_id) and _date<? "
															+ "group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) "
															+ "and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=? and lar._date<?) as a group by leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(6, nEmpId);
											pst.setInt(7, nLeaveTypeId);
											pst.setDate(8, uF.getDateFormat(calStartDate, DATE_FORMAT));
											// System.out.println("pst==>"+pst);
											rs = pst.executeQuery();
											Map<String, String> hmPaidBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
											}
											rs.close();
											pst.close();
											// System.out.println("hmPaidBalance==>"+hmPaidBalance);

											double dblBalance = uF.parseToDouble(hmMainBalance.get("" + nLeaveTypeId));
											// System.out.println("1 dblBalance==>"+dblBalance);
											dblBalance += uF.parseToDouble(hmAccruedBalance.get("" + nLeaveTypeId));
											// System.out.println("2 dblBalance==>"+dblBalance);

											double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get("" + nLeaveTypeId));

											if (dblBalance > 0 && dblBalance >= dblPaidBalance) {
												dblBalance = dblBalance - dblPaidBalance;
											}
											// System.out.println("3 dblBalance==>"+dblBalance);

											Map<String, String> hmCarriedForwordLimit = hmCarriedLimit.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId);
											if (hmCarriedForwordLimit == null)
												hmCarriedForwordLimit = new HashMap<String, String>();

											if (uF.parseToBoolean(hmCarriedForwordLimit.get("IS_CARRYFORWARD_LIMIT"))) {
												if (dblBalance > uF.parseToDouble(hmCarriedForwordLimit.get("CARRYFORWARD_LIMIT"))) {
													dblBalance = uF.parseToDouble(hmCarriedForwordLimit.get("CARRYFORWARD_LIMIT"));
												}
											}
											// System.out.println("4 dblBalance==>"+dblBalance);
											double dblLeaveBalance = dblBalance + dblCarryForwardBalance;
											// System.out.println("1 dblLeaveBalance==>"+dblLeaveBalance);
											if (uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly
														.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId));
												// System.out.println("2 dblLeaveBalance==>"+dblLeaveBalance+"--dblAccrued==>"+dblAccrued);
												dblLeaveBalance += dblAccrued;
												// System.out.println("3 dblLeaveBalance==>"+dblLeaveBalance);
											}
											// System.out.println("4 dblLeaveBalance==>"+dblLeaveBalance);
											if (nEmpId == 445) {
												 System.out.println("CD/10210--in--carriedForwardLeave-- dblLeaveBalance ===>> " + dblLeaveBalance);
											}

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
							} else if (strEffectiveLeaveType != null && strEffectiveLeaveType.equals("FY")) {
								if (nEmpId == 143) {
//									System.out.println(nEmpId + " -- strEffectiveLeaveType ===>> " + strEffectiveLeaveType);
								}
								if (nMonth == 4) {
									String startDate = "01/04/" + nYear;
									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/04/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									if (nEmpId == 143) {
//										System.out.println("pst ===>> " + pst);
									}
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (isDateBetween && uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
										pst = con.prepareStatement("delete from leave_register1 where emp_id = ? and leave_type_id=? and _date = ?");
										pst.setInt(1, nEmpId);
										pst.setInt(2, nLeaveTypeId);
										pst.setDate(3, uF.getDateFormat(calStartDate, DATE_FORMAT));
										int x = pst.executeUpdate();
										pst.close();
										if (x > 0) {
											flag = false;
										}
									}

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											pst = con
													.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 "
															+ "where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?)"
															+ " group by emp_id,leave_type_id) and leave_type_id=?");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nLeaveTypeId);
											rs = pst.executeQuery();
											Map<String, String> hmMainBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id "
															+ "and a.daa<=lr._date and lr.leave_type_id=? and lr._date<? group by a.leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmAccruedBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' "
															+ "and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) group by emp_id,leave_type_id) and _date<? "
															+ "group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) "
															+ "and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=? and lar._date<?) as a group by leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(6, nEmpId);
											pst.setInt(7, nLeaveTypeId);
											pst.setDate(8, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmPaidBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
											}
											rs.close();
											pst.close();

											double dblBalance = uF.parseToDouble(hmMainBalance.get("" + nLeaveTypeId));
											dblBalance += uF.parseToDouble(hmAccruedBalance.get("" + nLeaveTypeId));

											double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get("" + nLeaveTypeId));

											if (dblBalance > 0 && dblBalance >= dblPaidBalance) {
												dblBalance = dblBalance - dblPaidBalance;
											}

											double dblLeaveBalance = dblBalance + dblCarryForwardBalance;

											if (uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly
														.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId));
												dblLeaveBalance += dblAccrued;
											}

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											if (nEmpId == 143) {
//												System.out.println("pst ===>> " + pst);
											}
											pst.close();
										}
									}
								}
							} else if (strEffectiveLeaveType != null && strEffectiveLeaveType.equals("CMY")) {
								if (nEmpId == 143) {
									// System.out.println(nEmpId +
									// " -- strEffectiveLeaveType ===>> " +
									// strEffectiveLeaveType);
								}
								if (nMonth == 1) {
									String startDate = "01/01/" + nYear;

									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/01/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (isDateBetween && uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
										pst = con.prepareStatement("delete from leave_register1 where emp_id = ? and leave_type_id=? and _date = ?");
										pst.setInt(1, nEmpId);
										pst.setInt(2, nLeaveTypeId);
										pst.setDate(3, uF.getDateFormat(calStartDate, DATE_FORMAT));
										int x = pst.executeUpdate();
										pst.close();
										if (x > 0) {
											flag = false;
										}
									}

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											pst = con
													.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 "
															+ "where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?)"
															+ " group by emp_id,leave_type_id) and leave_type_id=?");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nLeaveTypeId);
											rs = pst.executeQuery();
											Map<String, String> hmMainBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id "
															+ "and a.daa<=lr._date and lr.leave_type_id=? and lr._date<? group by a.leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmAccruedBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' "
															+ "and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) group by emp_id,leave_type_id) and _date<? "
															+ "group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) "
															+ "and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=? and lar._date<?) as a group by leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(6, nEmpId);
											pst.setInt(7, nLeaveTypeId);
											pst.setDate(8, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmPaidBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
											}
											rs.close();
											pst.close();

											double dblBalance = uF.parseToDouble(hmMainBalance.get("" + nLeaveTypeId));
											dblBalance += uF.parseToDouble(hmAccruedBalance.get("" + nLeaveTypeId));

											double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get("" + nLeaveTypeId));

											if (dblBalance > 0 && dblBalance >= dblPaidBalance) {
												dblBalance = dblBalance - dblPaidBalance;
											}

											Map<String, String> hmCarriedForwordLimit = hmCarriedLimit.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId);
											if (hmCarriedForwordLimit == null)
												hmCarriedForwordLimit = new HashMap<String, String>();

											if (uF.parseToBoolean(hmCarriedForwordLimit.get("IS_CARRYFORWARD_LIMIT"))) {
												if (dblBalance > uF.parseToDouble(hmCarriedForwordLimit.get("CARRYFORWARD_LIMIT"))) {
													dblBalance = uF.parseToDouble(hmCarriedForwordLimit.get("CARRYFORWARD_LIMIT"));
												}
											}

											double dblLeaveBalance = dblBalance + dblCarryForwardBalance;

											if (uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly
														.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId));
												dblLeaveBalance += dblAccrued;
											}

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								} else if (nMonth == 7) {
									String startDate = "01/07/" + nYear;

									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/07/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (isDateBetween && uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
										pst = con.prepareStatement("delete from leave_register1 where emp_id = ? and leave_type_id=? and _date = ?");
										pst.setInt(1, nEmpId);
										pst.setInt(2, nLeaveTypeId);
										pst.setDate(3, uF.getDateFormat(calStartDate, DATE_FORMAT));
										int x = pst.executeUpdate();
										pst.close();
										if (x > 0) {
											flag = false;
										}
									}

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											pst = con
													.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 "
															+ "where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?)"
															+ " group by emp_id,leave_type_id) and leave_type_id=?");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nLeaveTypeId);
											rs = pst.executeQuery();
											Map<String, String> hmMainBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id "
															+ "and a.daa<=lr._date and lr.leave_type_id=? and lr._date<? group by a.leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmAccruedBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' "
															+ "and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) group by emp_id,leave_type_id) and _date<? "
															+ "group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) "
															+ "and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=? and lar._date<?) as a group by leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(6, nEmpId);
											pst.setInt(7, nLeaveTypeId);
											pst.setDate(8, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmPaidBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
											}
											rs.close();
											pst.close();

											double dblBalance = uF.parseToDouble(hmMainBalance.get("" + nLeaveTypeId));
											dblBalance += uF.parseToDouble(hmAccruedBalance.get("" + nLeaveTypeId));

											double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get("" + nLeaveTypeId));

											if (dblBalance > 0 && dblBalance >= dblPaidBalance) {
												dblBalance = dblBalance - dblPaidBalance;
											}

											Map<String, String> hmCarriedForwordLimit = hmCarriedLimit.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId);
											if (hmCarriedForwordLimit == null)
												hmCarriedForwordLimit = new HashMap<String, String>();

											if (uF.parseToBoolean(hmCarriedForwordLimit.get("IS_CARRYFORWARD_LIMIT"))) {
												if (dblBalance > uF.parseToDouble(hmCarriedForwordLimit.get("CARRYFORWARD_LIMIT"))) {
													dblBalance = uF.parseToDouble(hmCarriedForwordLimit.get("CARRYFORWARD_LIMIT"));
												}
											}

											double dblLeaveBalance = dblBalance + dblCarryForwardBalance;

											if (uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly
														.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId));
												dblLeaveBalance += dblAccrued;
											}

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
							} else if (strEffectiveLeaveType != null && strEffectiveLeaveType.equals("FMY")) {
								if (nEmpId == 143) {
//									System.out.println(nEmpId + " -- strEffectiveLeaveType ===>> " + strEffectiveLeaveType);
								}
								if (nMonth == 4) {
									String startDate = "01/04/" + nYear;
									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/04/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (isDateBetween && uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
										pst = con.prepareStatement("delete from leave_register1 where emp_id = ? and leave_type_id=? and _date = ?");
										pst.setInt(1, nEmpId);
										pst.setInt(2, nLeaveTypeId);
										pst.setDate(3, uF.getDateFormat(calStartDate, DATE_FORMAT));
										int x = pst.executeUpdate();
										pst.close();
										if (x > 0) {
											flag = false;
										}
									}

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											pst = con
													.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 "
															+ "where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?)"
															+ " group by emp_id,leave_type_id) and leave_type_id=?");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nLeaveTypeId);
											rs = pst.executeQuery();
											Map<String, String> hmMainBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id "
															+ "and a.daa<=lr._date and lr.leave_type_id=? and lr._date<? group by a.leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmAccruedBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' "
															+ "and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) group by emp_id,leave_type_id) and _date<? "
															+ "group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) "
															+ "and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=? and lar._date<?) as a group by leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(6, nEmpId);
											pst.setInt(7, nLeaveTypeId);
											pst.setDate(8, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmPaidBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
											}
											rs.close();
											pst.close();

											double dblBalance = uF.parseToDouble(hmMainBalance.get("" + nLeaveTypeId));
											dblBalance += uF.parseToDouble(hmAccruedBalance.get("" + nLeaveTypeId));

											double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get("" + nLeaveTypeId));

											if (dblBalance > 0 && dblBalance >= dblPaidBalance) {
												dblBalance = dblBalance - dblPaidBalance;
											}

											double dblLeaveBalance = dblBalance + dblCarryForwardBalance;

											if (uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly
														.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId));
												dblLeaveBalance += dblAccrued;
											}

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
								if (nMonth == 10) {
									String startDate = "01/10/" + nYear;
									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/10/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (isDateBetween && uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
										pst = con.prepareStatement("delete from leave_register1 where emp_id = ? and leave_type_id=? and _date = ?");
										pst.setInt(1, nEmpId);
										pst.setInt(2, nLeaveTypeId);
										pst.setDate(3, uF.getDateFormat(calStartDate, DATE_FORMAT));
										int x = pst.executeUpdate();
										pst.close();
										if (x > 0) {
											flag = false;
										}
									}

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											pst = con
													.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in(select max(register_id) from leave_register1 "
															+ "where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?)"
															+ " group by emp_id,leave_type_id) and leave_type_id=?");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nLeaveTypeId);
											rs = pst.executeQuery();
											Map<String, String> hmMainBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id "
															+ "and a.daa<=lr._date and lr.leave_type_id=? and lr._date<? group by a.leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmAccruedBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));
											}
											rs.close();
											pst.close();

											pst = con
													.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id "
															+ "from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) "
															+ "and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' "
															+ "and leave_type_id in (select leave_type_id from leave_type where leave_type_id=?) group by emp_id,leave_type_id) and _date<? "
															+ "group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) "
															+ "and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date and lar.leave_type_id=? and lar._date<?) as a group by leave_type_id");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nLeaveTypeId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nLeaveTypeId);
											pst.setDate(5, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(6, nEmpId);
											pst.setInt(7, nLeaveTypeId);
											pst.setDate(8, uF.getDateFormat(calStartDate, DATE_FORMAT));
											rs = pst.executeQuery();
											Map<String, String> hmPaidBalance = new HashMap<String, String>();
											while (rs.next()) {
												hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
											}
											rs.close();
											pst.close();

											double dblBalance = uF.parseToDouble(hmMainBalance.get("" + nLeaveTypeId));
											dblBalance += uF.parseToDouble(hmAccruedBalance.get("" + nLeaveTypeId));

											double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get("" + nLeaveTypeId));

											if (dblBalance > 0 && dblBalance >= dblPaidBalance) {
												dblBalance = dblBalance - dblPaidBalance;
											}

											double dblLeaveBalance = dblBalance + dblCarryForwardBalance;

											if (uF.parseToBoolean(hmLeavesAccrualDays.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly
														.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId));
												dblLeaveBalance += dblAccrued;
											}

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
							}
						}
					}
				}

				pst = con
						.prepareStatement("select * from emp_leave_type elt, leave_type lt where elt.leave_type_id=lt.leave_type_id and "
								+ "elt.leave_type_id in (select leave_type_id from leave_type where is_compensatory = false and org_id=?) "
								+ "and elt.is_constant_balance=false and elt.org_id=? and elt.is_carryforward=false and (lt.is_leave_opt_holiday is null or lt.is_leave_opt_holiday=false) "
								+ "and (elt.is_accrued_cal_days is null or elt.is_accrued_cal_days=false) order by elt.level_id");
				pst.setInt(1, uF.parseToInt(strOrgId));
				pst.setInt(2, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				alLeaveType = new ArrayList<String>();
				hmCarryForwardLeavesBal = new HashMap<String, String>();
				hmLeavesType = new HashMap<String, String>();
				hmLeaves = new HashMap<String, List<String>>();
				hmCarriedLimit = new HashMap<String, Map<String, String>>();
				hmLeavesAccrualStatus = new HashMap<String, String>();
				while (rs.next()) {
					if (uF.parseToInt(rs.getString("leave_type_id")) > 0) {
						if (!alLeaveType.contains(rs.getString("leave_type_id"))) {
							alLeaveType.add(rs.getString("leave_type_id"));
						}
						hmCarryForwardLeavesBal.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("no_of_leave"));
						hmLeavesType.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("effective_date_type"));

						hmLeavesAccrualStatus.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("is_leave_accrual"));

						List<String> alLeave = hmLeaves.get(rs.getString("level_id") + "_" + rs.getString("wlocation_id"));
						if (alLeave == null)
							alLeave = new ArrayList<String>();
						alLeave.add(rs.getString("leave_type_id"));

						hmLeaves.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id"), alLeave);

						Map<String, String> hmCarriedForwordLimit = new HashMap<String, String>();
						hmCarriedForwordLimit.put("IS_CARRYFORWARD_LIMIT", rs.getString("is_carryforward_limit"));
						hmCarriedForwordLimit.put("CARRYFORWARD_LIMIT", rs.getString("carryforward_limit"));
						hmCarriedLimit.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								hmCarriedForwordLimit);
					}
				}
				rs.close();
				pst.close();

				if (alLeaveType.size() > 0) {
					pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod, probation_policy pp "
							+ "where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id and is_alive = true "
							+ "and emp_per_id>0 and eod.org_id=? and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') "
							+ "and epd.joining_date is not null order by eod.emp_id");
					pst.setInt(1, uF.parseToInt(strOrgId));
					rs = pst.executeQuery();
					List<String> alEmp = new ArrayList<String>();
					Map<String, List<String>> hmEmpLeaves = new HashMap<String, List<String>>();
					while (rs.next()) {
						int nLevelId = uF.parseToInt(hmEmpLevelMap.get(rs.getString("emp_id")));

						List<String> alLeave = hmLeaves.get(nLevelId + "_" + rs.getString("wlocation_id"));
						if (alLeave == null)
							alLeave = new ArrayList<String>();

						String strAllowedLeaves = rs.getString("leaves_types_allowed");
						if (strAllowedLeaves != null && strAllowedLeaves.length() > 0) {
							List<String> al = Arrays.asList(strAllowedLeaves.split(","));
							for (String leaveTypeId : al) {
								if (uF.parseToInt(leaveTypeId) > 0 && alLeaveType.contains(leaveTypeId) && alLeave.contains(leaveTypeId)) {
									if (!alEmp.contains(rs.getString("emp_id"))) {
										alEmp.add(rs.getString("emp_id"));
									}

									List<String> alEmpLeave = hmEmpLeaves.get(rs.getString("emp_id"));
									if (alEmpLeave == null)
										alEmpLeave = new ArrayList<String>();
									alEmpLeave.add(leaveTypeId);

									hmEmpLeaves.put(rs.getString("emp_id"), alEmpLeave);
								}
							}
						}
					}
					rs.close();
					pst.close();

					// alEmp = new ArrayList<String>();
					// alEmp.add("168");

					int nAlEmp = alEmp.size();
					for (int i = 0; i < nAlEmp; i++) {
						int nEmpId = uF.parseToInt(alEmp.get(i));
						int nLevelId = uF.parseToInt(hmEmpLevelMap.get(alEmp.get(i)));
						int nLocationId = uF.parseToInt(hmEmpWlocationMap.get(alEmp.get(i)));
						List<String> alEmpLeave = hmEmpLeaves.get(alEmp.get(i));
						if (alEmpLeave == null)
							alEmpLeave = new ArrayList<String>();
						if (nEmpId == 143) {
							// System.out.println(nEmpId +
							// " -- >> alEmpLeave ===>> " + alEmpLeave);
						}
						for (int j = 0; j < alLeaveType.size(); j++) {
							int nLeaveTypeId = uF.parseToInt(alLeaveType.get(j));
							if (!hmCarryForwardLeavesBal.containsKey(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId)
									|| !alEmpLeave.contains("" + nLeaveTypeId)) {
								continue;
							}
							String strEffectiveLeaveType = hmLeavesType.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId);
							if (nEmpId == 143) {
								// System.out.println(nEmpId +
								// " -- >> strEffectiveLeaveType ===>> " +
								// strEffectiveLeaveType);
							}
							if (strEffectiveLeaveType != null && strEffectiveLeaveType.equals("CY")) {
								if (nMonth == 1) {
									String startDate = "01/01/" + nYear;

									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/01/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}
											double dblLeaveBalance = dblCarryForwardBalance;

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
							} else if (strEffectiveLeaveType != null && strEffectiveLeaveType.equals("FY")) {
								if (nEmpId == 143) {
									// System.out.println(nEmpId +
									// " -- >> strEffectiveLeaveType ===>> " +
									// strEffectiveLeaveType);
								}
								if (nMonth == 4) {
									String startDate = "01/04/" + nYear;

									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/04/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}
											double dblLeaveBalance = dblCarryForwardBalance;

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
							} else if (strEffectiveLeaveType != null && strEffectiveLeaveType.equals("CMY")) {
								if (nEmpId == 143) {
									// System.out.println(nEmpId +
									// " -- >> strEffectiveLeaveType ===>> " +
									// strEffectiveLeaveType);
								}
								if (nMonth == 1) {
									String startDate = "01/01/" + nYear;

									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/01/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											double dblLeaveBalance = dblCarryForwardBalance;

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								} else if (nMonth == 7) {
									String startDate = "01/07/" + nYear;

									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/07/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											double dblLeaveBalance = dblCarryForwardBalance;

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
							} else if (strEffectiveLeaveType != null && strEffectiveLeaveType.equals("FMY")) {
								if (nEmpId == 143) {
									// System.out.println(nEmpId +
									// " -- >> strEffectiveLeaveType ===>> " +
									// strEffectiveLeaveType);
								}
								if (nMonth == 4) {
									String startDate = "01/04/" + nYear;
									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/04/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											double dblLeaveBalance = dblCarryForwardBalance;

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
								if (nMonth == 10) {
									String startDate = "01/10/" + nYear;
									String[] arrDate = CF.getPayCycleFromDate(con, startDate, CF.getStrTimeZone(), CF, strOrgId);
									String calStartDate = arrDate[0];

									boolean isDateBetween = uF.isDateBetween(uF.getDateFormatUtil(calStartDate, DATE_FORMAT),
											uF.getDateFormatUtil("10/10/" + nYear, DATE_FORMAT), uF.getDateFormatUtil(currDate, DATE_FORMAT));
									pst = con
											.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? and _date=? order by _date desc limit 1");
									pst.setInt(1, nEmpId);
									pst.setInt(2, nLeaveTypeId);
									pst.setString(3, "C");
									pst.setDate(4, uF.getDateFormat(calStartDate, DATE_FORMAT));
									rs = pst.executeQuery();
									boolean flag = false;
									while (rs.next()) {
										flag = true;
									}
									rs.close();
									pst.close();

									if (!flag) {
										if (isDateBetween) {
											double dblCarryForwardBalance = 0.0d;
											if (!uF.parseToBoolean(hmLeavesAccrualStatus.get(nLevelId + "_" + nLocationId + "_" + nLeaveTypeId))) {
												dblCarryForwardBalance = uF.parseToDouble(hmCarryForwardLeavesBal.get(nLevelId + "_" + nLocationId + "_"
														+ nLeaveTypeId));
											}

											double dblLeaveBalance = dblCarryForwardBalance;

											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, 0);
											pst.setInt(4, nEmpId);
											pst.setInt(5, nLeaveTypeId);
											pst.setDate(6, uF.getDateFormat(calStartDate, DATE_FORMAT));
											pst.setInt(7, 1);
											pst.setString(8, "C");
											pst.setDouble(9, dblLeaveBalance);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
							}
						}
					}
				}
			}
			// System.out.println("carriedForwardLeave end====>");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void accrualEarnedLeave(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			// System.out.println("accrualEarnedLeave ...");
			Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
			if (hmOrgMap == null)
				hmOrgMap = new HashMap<String, Map<String, String>>();

			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if (hmEmpLevelMap == null)
				hmEmpLevelMap = new HashMap<String, String>();
//			System.out.println("CD/11395---hmOrgMap=="+hmOrgMap);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);

			Iterator<String> it = hmOrgMap.keySet().iterator();
			while (it.hasNext()) {
				String strOrgId = (String) it.next();
//				System.out.println("CD/11395---strOrgId=="+strOrgId);
				Map<String, String> hmOrg = hmOrgMap.get(strOrgId);
				// System.out.println("hmOrg ===>> " + hmOrg);
				if (hmOrg.get("ORG_START_PAYCYCLE") == null || hmOrg.get("ORG_START_PAYCYCLE").trim().equals("")
						|| hmOrg.get("ORG_START_PAYCYCLE").trim().equalsIgnoreCase("NULL") || hmOrg.get("ORG_START_PAYCYCLE").trim().equals("-")) {
					continue;
				}
				// System.out.println("ORG_START_PAYCYCLE ===>> " +
				// hmOrg.get("ORG_START_PAYCYCLE"));
				pst = con
						.prepareStatement("select distinct(compensate_with) from leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id and lt.is_compensatory = true and compensate_with>0 and lt.org_id=?");
				pst.setInt(1, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				List<String> compensateWithList = new ArrayList<String>();
				while (rs.next()) {
					compensateWithList.add(rs.getString("compensate_with"));
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("select * from emp_leave_type where leave_type_id in (select leave_type_id from leave_type "
						+ "where is_compensatory = false and org_id=?) and is_constant_balance=false and org_id=? and is_leave_accrual=true "
						+ "and accrual_type=2 and accrual_days > 0 and (is_accrued_cal_days is null or is_accrued_cal_days=false)");
				pst.setInt(1, uF.parseToInt(strOrgId));
				pst.setInt(2, uF.parseToInt(strOrgId));
//				System.out.println("CD/11420--accrualEarnedLeave()---pst ===>> " + pst);
				rs = pst.executeQuery();
				List<String> alLeaveType = new ArrayList<String>();
				Map<String, String> hmTotalLeaves = new HashMap<String, String>();
				Map<String, String> hmTotalLeavesMonthly = new HashMap<String, String>();
				// Map<String, String> hmCarryForwardMonthly = new
				// HashMap<String, String>();
				// Map<String, String> hmAccrualFrom = new HashMap<String,
				// String>();
				Map<String, String> hmAccrualSystem = new HashMap<String, String>();
				Map<String, String> hmCalculationDate = new HashMap<String, String>();
				Map<String, String> hmAccruType = new HashMap<String, String>();
				Map<String, String> hmAccruDays = new HashMap<String, String>();
				List<String> alAvailableLeave = new ArrayList<String>();
				while (rs.next()) {
					if (uF.parseToInt(rs.getString("leave_type_id")) > 0 && compensateWithList != null
							&& !compensateWithList.contains(rs.getString("leave_type_id"))) {
						if (!alLeaveType.contains(rs.getString("leave_type_id"))) {
							alLeaveType.add(rs.getString("leave_type_id"));
						}
						hmTotalLeaves.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("no_of_leave"));
						hmTotalLeavesMonthly.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("no_of_leave_monthly"));
						// hmCarryForwardMonthly.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id")+"_"+rs.getString("leave_type_id"),
						// rs.getString("is_monthly_carryforward"));
						// hmAccrualFrom.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id")+"_"+rs.getString("leave_type_id"),
						// rs.getString("accrual_from"));
						hmCalculationDate.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("effective_date_type"));
						hmAccrualSystem.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("accrual_system"));

						hmAccruType.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("accrual_type"));
						hmAccruDays.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
								rs.getString("accrual_days"));

						if (!alAvailableLeave.contains(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"))) {
							alAvailableLeave.add(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"));
						}
					}
				}
				rs.close();
				pst.close();
				

				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod, probation_policy pp "
						+ "where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id and is_alive = true "
						+ "and emp_per_id>0 and eod.org_id=? and (pp.leaves_types_allowed is not null "
						+ "and pp.leaves_types_allowed !='') and epd.joining_date is not null");
				pst.setInt(1, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				List<String> alEmp = new ArrayList<String>();
				Map<String, String> hmEmpJoiningDate = new HashMap<String, String>();
				while (rs.next()) {
					String strAllowedLeaves = rs.getString("leaves_types_allowed");
					if (strAllowedLeaves != null && strAllowedLeaves.length() > 0) {
						List<String> al = Arrays.asList(strAllowedLeaves.split(","));
						for (String leaveTypeId : al) {
							if (uF.parseToInt(leaveTypeId) > 0 && alLeaveType.contains(leaveTypeId)) {
								alEmp.add(rs.getString("emp_id"));
								hmEmpJoiningDate.put(rs.getString("emp_id"), uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
							}
						}
					}
				}
				rs.close();
				pst.close();

				for (int i = 0; alEmp != null && i < alEmp.size(); i++) {
					int nLevelId = uF.parseToInt(hmEmpLevelMap.get(alEmp.get(i)));
					String strWLocationId = CF.getEmpWlocationId(con, uF, strEmpId);
					
					for (int j = 0; j < alLeaveType.size(); j++) {
						if (!alAvailableLeave.contains(nLevelId + "_" + strWLocationId + "_" + alLeaveType.get(j))) {
							continue;
						}
						int nAccruDays = uF.parseToInt(hmAccruDays.get(nLevelId + "_" + strWLocationId + "_" + alLeaveType.get(j)));
						if (nAccruDays > 0) {
							pst = con
									.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _date <=? and update_balance=1 order by _date desc limit 1");
							pst.setInt(1, uF.parseToInt(alEmp.get(i)));
							pst.setInt(2, uF.parseToInt(alLeaveType.get(j)));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							rs = pst.executeQuery();
							String strPrevAccruDate = null;
							while (rs.next()) {
								strPrevAccruDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
							}
							rs.close();
							pst.close();

							if (strPrevAccruDate != null && !strPrevAccruDate.trim().equals("") && !strPrevAccruDate.trim().equalsIgnoreCase("NULL")
									&& !strPrevAccruDate.trim().equalsIgnoreCase("-")) {
								// pst =
								// con.prepareStatement("select count(*) as cnt from (select distinct(to_date(in_out_timestamp::text,'yyyy-MM-dd')) "
								// +
								// "from attendance_details where emp_id=? and in_out=? and to_date(in_out_timestamp::text,'yyyy-MM-dd') between ? and ?) a");
								pst = con.prepareStatement("select count(*) as cnt from (select distinct(to_date(in_out_timestamp::text,'yyyy-MM-dd')) "
										+ "from attendance_details where emp_id=? and in_out=? and to_date(in_out_timestamp::text,'yyyy-MM-dd')>? "
										+ "and to_date(in_out_timestamp::text,'yyyy-MM-dd')<=?) a");
								pst.setInt(1, uF.parseToInt(alEmp.get(i)));
								pst.setString(2, "IN");
								pst.setDate(3, uF.getDateFormat(strPrevAccruDate, DATE_FORMAT));
								pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
								rs = pst.executeQuery();
								int nCalDays = 0;
								while (rs.next()) {
									nCalDays = uF.parseToInt(rs.getString("cnt"));
								}
								rs.close();
								pst.close();
								
								if (nAccruDays == nCalDays) {
									double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly.get(nLevelId + "_" + strWLocationId + "_" + alLeaveType.get(j)));

									if (dblAccrued > 0) {
										pst = con
												.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? order by _date desc limit 1");
										pst.setInt(1, uF.parseToInt(alEmp.get(i)));
										pst.setInt(2, uF.parseToInt(alLeaveType.get(j)));
										pst.setString(3, "C");
										rs = pst.executeQuery();
										boolean flag = false;
										while (rs.next()) {
											flag = true;
										}
										rs.close();
										pst.close();

										String strType = "C";
										if (flag) {
											strType = "A";
										}

										pst = con
												.prepareStatement("update leave_register1 set taken_paid=0, taken_unpaid=0, balance=0, accrued=?,update_balance=?,compensate_id=0 where update_balance=1 and emp_id = ? and leave_type_id=? and _date = ? and _type=?");
										pst.setDouble(1, (dblAccrued));
										pst.setInt(2, 1);
										pst.setInt(3, uF.parseToInt(alEmp.get(i)));
										pst.setInt(4, uF.parseToInt(alLeaveType.get(j)));
										pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.setString(6, strType);
										int x = pst.executeUpdate();
										pst.close();

										if (x == 0) {
											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?,?,?,?,?,?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, strType.equals("A") ? dblAccrued : 0.0d);
											pst.setInt(4, uF.parseToInt(alEmp.get(i)));
											pst.setInt(5, uF.parseToInt(alLeaveType.get(j)));
											pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setInt(7, 1);
											pst.setString(8, strType);
											pst.setDouble(9, strType.equals("C") ? dblAccrued : 0.0d);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
								
							} else {
								pst = con.prepareStatement("select count(*) as cnt from (select distinct(to_date(in_out_timestamp::text,'yyyy-MM-dd')) "
										+ "from attendance_details where emp_id=? and in_out=? and to_date(in_out_timestamp::text,'yyyy-MM-dd') <=?"
										+ " order by to_date(in_out_timestamp::text,'yyyy-MM-dd') desc limit ?) a");
								pst.setInt(1, uF.parseToInt(alEmp.get(i)));
								pst.setString(2, "IN");
								pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(4, nAccruDays);
								rs = pst.executeQuery();
								int nCalDays = 0;
								while (rs.next()) {
									nCalDays = uF.parseToInt(rs.getString("cnt"));
								}
								rs.close();
								pst.close();
								
								if (nAccruDays == nCalDays) {
									double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly.get(nLevelId + "_" + strWLocationId + "_" + alLeaveType.get(j)));

									if (dblAccrued > 0) {
										pst = con
												.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _type=? order by _date desc limit 1");
										pst.setInt(1, uF.parseToInt(alEmp.get(i)));
										pst.setInt(2, uF.parseToInt(alLeaveType.get(j)));
										pst.setString(3, "C");
										rs = pst.executeQuery();
										boolean flag = false;
										while (rs.next()) {
											flag = true;
										}
										rs.close();
										pst.close();

										String strType = "C";
										if (flag) {
											strType = "A";
										}

										pst = con
												.prepareStatement("update leave_register1 set taken_paid=0, taken_unpaid=0, balance=0, accrued=?,update_balance=?,compensate_id=0 where update_balance=1 and emp_id = ? and leave_type_id=? and _date = ? and _type=?");
										pst.setDouble(1, (dblAccrued));
										pst.setInt(2, 1);
										pst.setInt(3, uF.parseToInt(alEmp.get(i)));
										pst.setInt(4, uF.parseToInt(alLeaveType.get(j)));
										pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.setString(6, strType);
										int x = pst.executeUpdate();
										pst.close();

										if (x == 0) {
											pst = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
													+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?,?,?,?,?,?,?)");
											pst.setDouble(1, 0);
											pst.setDouble(2, 0);
											pst.setDouble(3, dblAccrued);
											pst.setInt(4, uF.parseToInt(alEmp.get(i)));
											pst.setInt(5, uF.parseToInt(alLeaveType.get(j)));
											pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setInt(7, 1);
											pst.setString(8, strType);
											pst.setInt(9, 0);
											pst.setInt(10, 0);
											pst.execute();
											pst.close();
										}
									}
								}
								
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private synchronized void addGoalFrequency(UtilityFunctions uF) {
		GoalScheduler gs = new GoalScheduler(request, session, CF, uF, strEmpId);
		gs.checkAndCreateNewGoal();
	}

	private synchronized void addNewProject(UtilityFunctions uF) {
		ProjectScheduler Pscheduler = new ProjectScheduler(request, session, CF, uF, strEmpId);
		Pscheduler.checkAndCreateNewProject();
	}

	public synchronized void checkTodaysBirthdaysMarriageAndWorkAnniversary(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String strUserType = (String) session.getAttribute(BASEUSERTYPE);
		try {
			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, "MM-dd");
			String dtTodayDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			Date dtCurrDate = uF.getDateFormat(dtTodayDate, DBDATE);
			
			// String strTomorrow =
			// uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"",
			// DBDATE, "MM-dd");

			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getEmpDepartmentMap(con);
			Map<String, Map<String, String>> hmWlocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);

			// *************** Birthday **************************

			pst = con.prepareStatement("select bday_emp_id from communication_1 where create_time between ? and ?");
			pst.setTimestamp(1, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + " " + "00:00:00", DBTIMESTAMP));
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + " " + "23:59:59", DBTIMESTAMP));
			// pst.setTimestamp(3,
			// uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+"00:00:00",
			// DBTIMESTAMP));
			// pst.setTimestamp(4,
			// uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+"23:59:59",
			// DBTIMESTAMP));
//			 System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> alEmpIds = new ArrayList<String>();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("bday_emp_id")) > 0) {
					alEmpIds.add(rs.getString("bday_emp_id"));
				}
			}
			rs.close();
			pst.close();
//			 System.out.println("alEmpIds ===>> " + alEmpIds);

			 String yrs = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy");
			 String strMinMax = uF.getCurrentMonthMinMaxDate("01/02/"+yrs, DATE_FORMAT);
//			 System.out.println("strMinMax ===>> " + strMinMax);
			 String[] tmpMinMax = strMinMax.split("::::");
			 String[] tmpMaxDay = tmpMinMax[1].split("/");
			pst = con.prepareStatement(selectBirthDay);
			if(uF.parseToInt(tmpMaxDay[0]) == 29) {
				pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 366));
			} else {
				pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			}
//			 System.out.println("CD/11751--selectBirthDay pst ===>> " + pst);
			rs = pst.executeQuery();
			// Map<String, List<String>> hmEmpBirthdayData = new HashMap<String,
			// List<String>>();
			List<List<String>> alBirthDays = new ArrayList<List<String>>();
			while (rs.next()) {
				String strBDate = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM-dd");
				String strWlocationId = hmEmpWlocationMap.get(rs.getString("emp_per_id"));
				
				Map hmWlocation = hmWlocationMap.get(strWlocationId);
				if (hmWlocation == null)
					hmWlocation = new HashMap();

				String strCity = (String) hmWlocation.get("WL_CITY");

				if (strBDate != null && strBDate.equals(strToday1) && hmWlocation != null && !alEmpIds.contains(rs.getString("emp_per_id"))) {
					Date dtBDate = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE);
					if (dtBDate.before(dtCurrDate) && hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("emp_per_id"));
						innerList.add("<div style=\"float: left;\"><img src=\"images1/bday.png\"/></div><div style=\"float: left; width: 95%; padding: 5px 0px 0px 5px;\"><strong>"
							+ hmEmployeeMap.get(rs.getString("emp_per_id"))+ "</strong>, in "+ strCity+ ", working for "
							+ hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id"))) + " has birthday today.</div>"); //float: left; 
						alBirthDays.add(innerList);
					}
				}

			}
			rs.close();
			pst.close();
//			 System.out.println("alBirthDays ===>> " + alBirthDays);

			// String strDomain = request.getServerName().split("\\.")[0];
			for (int i = 0; alBirthDays != null && !alBirthDays.isEmpty() && i < alBirthDays.size(); i++) {
				List<String> innerList = alBirthDays.get(i);
				String bDayEmpId = innerList.get(0);
				String activityData = innerList.get(1);
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				// userAct.setStrAlignWith(PROJECT+"");
				// userAct.setStrAlignWithId(proId);
				// userAct.setStrTaggedWith(taggedWith.toString());
				// userAct.setStrVisibilityWith(taggedWith.toString());
				userAct.setStrVisibility("0");
				userAct.setStrData(activityData);
				userAct.setStrOther("other");
				userAct.setActivityType("BDAY");
				userAct.setbDayEmpId(bDayEmpId);
				if (strUserType.equals(CUSTOMER)) {
					userAct.setStrUserType("C");
				}
				// if(uF.parseToInt(strUesrTypeId) == 1 &&
				// uF.parseToInt(strUesrTypeId) == 4 &&
				// uF.parseToInt(strUesrTypeId) == 7) {
				// userAct.setStrSessionEmpId(strEmpId);
				// }
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}

			// *************** Marriage Anniversary **************************

			pst = con.prepareStatement("select manniversary_emp_id from communication_1 where create_time between ? and  ?");
			pst.setTimestamp(1, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + " " + "00:00:00", DBTIMESTAMP));
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + " " + "23:59:59", DBTIMESTAMP));
			// pst.setTimestamp(3,
			// uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+"00:00:00",
			// DBTIMESTAMP));
			// pst.setTimestamp(4,
			// uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+"23:59:59",
			// DBTIMESTAMP));
			// System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			alEmpIds = new ArrayList<String>();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("manniversary_emp_id")) > 0) {
					alEmpIds.add(rs.getString("manniversary_emp_id"));
				}
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("SELECT emp_per_id, emp_date_of_marriage,emp_gender, EXTRACT( YEAR FROM AGE(emp_date_of_marriage) ) as marriage_years FROM employee_personal_details  WHERE  EXTRACT( YEAR FROM AGE(emp_date_of_marriage) ) < EXTRACT( YEAR FROM AGE(?, emp_date_of_marriage ) ) and is_alive=true ORDER BY marriage_years, emp_per_id");
			if(uF.parseToInt(tmpMaxDay[0]) == 29) {
				pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 366));
			} else {
				pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			}
//			 System.out.println("alMarriageAnniversary pst ===>> " + pst);
			rs = pst.executeQuery();
			// Map<String, List<String>> hmEmpBirthdayData = new HashMap<String,
			// List<String>>();
			List<List<String>> alMarriageAnniversary = new ArrayList<List<String>>();
			while (rs.next()) {
				String strMADate = uF.getDateFormat(rs.getString("emp_date_of_marriage"), DBDATE, "MM-dd");
				String strWlocationId = hmEmpWlocationMap.get(rs.getString("emp_per_id"));

				Map hmWlocation = hmWlocationMap.get(strWlocationId);
				if (hmWlocation == null)
					hmWlocation = new HashMap();

				String strCity = (String) hmWlocation.get("WL_CITY");

				if (strMADate != null && strMADate.equals(strToday1) && hmWlocation != null && !alEmpIds.contains(rs.getString("emp_per_id"))) {
					Date dtMADate = uF.getDateFormat(rs.getString("emp_date_of_marriage"), DBDATE);
					if (dtMADate.before(dtCurrDate) && hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("emp_per_id"));
						innerList.add("<div style=\"float: left;\"><img src=\"images1/bday.png\"/></div><div style=\"float: left; width: 95%; padding: 5px 0px 0px 5px;\"><strong>"
							+ hmEmployeeMap.get(rs.getString("emp_per_id"))+ "</strong>, in "+ strCity+ ", working for "
							+ hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id"))) + " has marriage anniversary today.</div>"); //float: left; 
						alMarriageAnniversary.add(innerList);
					}
				}

			}
			rs.close();
			pst.close();
			// System.out.println("alMarriageAnniversary ===>> " +
			// alMarriageAnniversary);

			for (int i = 0; alMarriageAnniversary != null && !alMarriageAnniversary.isEmpty() && i < alMarriageAnniversary.size(); i++) {
				List<String> innerList = alMarriageAnniversary.get(i);
				String maDayEmpId = innerList.get(0);
				String activityData = innerList.get(1);
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrVisibility("0");
				userAct.setStrData(activityData);
				userAct.setStrOther("other");
				userAct.setActivityType("MANNIVERSARY");
				userAct.setbDayEmpId(maDayEmpId);
				if (strUserType.equals(CUSTOMER)) {
					userAct.setStrUserType("C");
				}
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}

			// *************** Work Anniversary **************************

			pst = con.prepareStatement("select wanniversary_emp_id from communication_1 where create_time between ? and  ?");
			pst.setTimestamp(1, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + " " + "00:00:00", DBTIMESTAMP));
			pst.setTimestamp(2, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + " " + "23:59:59", DBTIMESTAMP));
			// pst.setTimestamp(3,
			// uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+"00:00:00",
			// DBTIMESTAMP));
			// pst.setTimestamp(4,
			// uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+" "+"23:59:59",
			// DBTIMESTAMP));
			// System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			alEmpIds = new ArrayList<String>();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("wanniversary_emp_id")) > 0) {
					alEmpIds.add(rs.getString("wanniversary_emp_id"));
				}
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("SELECT emp_per_id, joining_date,emp_gender, EXTRACT( YEAR FROM AGE(joining_date) ) as work_years FROM employee_personal_details  WHERE  EXTRACT( YEAR FROM AGE(joining_date) ) < EXTRACT( YEAR FROM AGE(?, joining_date ) ) and is_alive=true ORDER BY work_years, emp_per_id");
			if(uF.parseToInt(tmpMaxDay[0]) == 29) {
				pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 366));
			} else {
				pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			}
			// System.out.println("alWorkAnniversary pst ===>> " + pst);
			rs = pst.executeQuery();
			// Map<String, List<String>> hmEmpBirthdayData = new HashMap<String,
			// List<String>>();
			List<List<String>> alWorkAnniversary = new ArrayList<List<String>>();
			while (rs.next()) {
				String strWADate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, "MM-dd");
				String strWlocationId = hmEmpWlocationMap.get(rs.getString("emp_per_id"));

				Map hmWlocation = hmWlocationMap.get(strWlocationId);
				if (hmWlocation == null)
					hmWlocation = new HashMap();

				String strCity = (String) hmWlocation.get("WL_CITY");

				if (strWADate != null && strWADate.equals(strToday1) && hmWlocation != null && !alEmpIds.contains(rs.getString("emp_per_id"))) {
					Date dtMADate = uF.getDateFormat(rs.getString("joining_date"), DBDATE);
					if (dtMADate.before(dtCurrDate) && hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("emp_per_id"));
						innerList.add("<div style=\"float: left;\"><img src=\"images1/bday.png\"/></div><div style=\"float: left; width: 95%; padding: 5px 0px 0px 5px;\"><strong>"
							+ hmEmployeeMap.get(rs.getString("emp_per_id"))+ "</strong>, in "+ strCity+ ", working for "
							+ hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id"))) + " has work anniversary today.</div>"); //float: left; 
						alWorkAnniversary.add(innerList);
					}
				}
			}
			rs.close();
			pst.close();
			// System.out.println("alWorkAnniversary ===>> " +
			// alWorkAnniversary);

			for (int i = 0; alWorkAnniversary != null && !alWorkAnniversary.isEmpty() && i < alWorkAnniversary.size(); i++) {
				List<String> innerList = alWorkAnniversary.get(i);
				String waDayEmpId = innerList.get(0);
				String activityData = innerList.get(1);
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrVisibility("0");
				userAct.setStrData(activityData);
				userAct.setStrOther("other");
				userAct.setActivityType("WANNIVERSARY");
				userAct.setbDayEmpId(waDayEmpId);
				if (strUserType.equals(CUSTOMER)) {
					userAct.setStrUserType("C");
				}
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	private synchronized void accrualLeave(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String currDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strFinancialDates = CF.getFinancialYear(con, currDate, CF, uF);

			Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
			if (hmOrgMap == null)
				hmOrgMap = new HashMap<String, Map<String, String>>();
			// Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			// if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String,
			// String>();

			Map<String, String> hmEmpJoiningDate = CF.getEmpJoiningDateMap(con, uF);
			if (hmEmpJoiningDate == null)
				hmEmpJoiningDate = new HashMap<String, String>();

			Iterator<String> it = hmOrgMap.keySet().iterator();
			while (it.hasNext()) {
				String strOrgId = (String) it.next();
				Map<String, String> hmOrg = hmOrgMap.get(strOrgId);
				if (hmOrg.get("ORG_START_PAYCYCLE") == null || hmOrg.get("ORG_START_PAYCYCLE").trim().equals("")
						|| hmOrg.get("ORG_START_PAYCYCLE").trim().equalsIgnoreCase("NULL") || hmOrg.get("ORG_START_PAYCYCLE").trim().equals("-")) {
					continue;
				}

				String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
				if (strPayCycleDate != null) {
					String startDate = strPayCycleDate[0];
					String endDate = strPayCycleDate[1];
					String strPC = strPayCycleDate[2];

					pst = con
							.prepareStatement("select distinct(compensate_with) from leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id and lt.is_compensatory = true and compensate_with>0 and lt.org_id=?");
					pst.setInt(1, uF.parseToInt(strOrgId));
					rs = pst.executeQuery();
					List<String> compensateWithList = new ArrayList<String>();
					while (rs.next()) {
						compensateWithList.add(rs.getString("compensate_with"));
					}
					rs.close();
					pst.close();

					pst = con
							.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and is_alive = true and emp_per_id>0 and eod.org_id=?");
					pst.setInt(1, uF.parseToInt(strOrgId));
					rs = pst.executeQuery();
					List<String> alEmp = new ArrayList<String>();
					while (rs.next()) {
						alEmp.add(rs.getString("emp_id"));
					}
					rs.close();
					pst.close();

					pst = con
							.prepareStatement("select * from emp_leave_type where leave_type_id in (select leave_type_id from leave_type "
									+ "where is_compensatory = false and org_id=?) and is_constant_balance=false and org_id=? and is_leave_accrual=true and accrual_type=1");
					pst.setInt(1, uF.parseToInt(strOrgId));
					pst.setInt(2, uF.parseToInt(strOrgId));
//					System.out.println("CronData/12055---pst=="+pst);
					rs = pst.executeQuery();
					List<String> alLeaveType = new ArrayList<String>();
					Map<String, String> hmTotalLeaves = new HashMap<String, String>();
					Map<String, String> hmTotalLeavesMonthly = new HashMap<String, String>();
					Map<String, String> hmCarryForwardAccrualMonthly = new HashMap<String, String>();
					// Map<String, String> hmAccrualFrom = new HashMap<String,
					// String>();
					Map<String, String> hmAccrualSystem = new HashMap<String, String>();
					Map<String, String> hmAccrualFrom = new HashMap<String, String>();
					Map<String, String> hmCalculationDate = new HashMap<String, String>();
					List<String> alAvailableLeave = new ArrayList<String>();
					Map<String, String> hmAvailableLeave = new HashMap<String, String>();
					while (rs.next()) {
						if (uF.parseToInt(rs.getString("leave_type_id")) > 0) {

							if (compensateWithList != null && compensateWithList.contains(rs.getString("leave_type_id"))) {
								continue;
							}

							if (!alLeaveType.contains(rs.getString("leave_type_id"))) {
								alLeaveType.add(rs.getString("leave_type_id"));
							}
							hmTotalLeaves.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
									rs.getString("no_of_leave"));
							hmTotalLeavesMonthly.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
									rs.getString("no_of_leave_monthly"));
							hmCarryForwardAccrualMonthly.put(
									rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
									rs.getString("is_carryforward_accrual_monthly"));
							// hmAccrualFrom.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id")+"_"+rs.getString("leave_type_id"),
							// rs.getString("accrual_from"));
							hmCalculationDate.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
									rs.getString("effective_date_type"));
							hmAccrualSystem.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
									rs.getString("accrual_system"));
							hmAccrualFrom.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
									rs.getString("accrual_from"));
							hmAvailableLeave.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"),
									rs.getString("leave_available"));

							if (!alAvailableLeave.contains(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"))) {
								alAvailableLeave.add(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"));
							}
						}
					}
					rs.close();
					pst.close();
//					System.out.println("CD/12381--accrualLeave()---alLeaveType"+alLeaveType);

					int cnt = 0;
					for (int i = 0; alEmp != null && i < alEmp.size(); i++) {
						String strEmpId = alEmp.get(i);
						int nLevelId = uF.parseToInt(CF.getEmpLevelId(con, strEmpId));
						String strWLocationId = CF.getEmpWlocationId(con, uF, strEmpId);
						if (uF.parseToInt(strEmpId) == 143) {
							// System.out.println("accrualLeave alAvailableLeave ===>> "
							// + alAvailableLeave);
						}
						pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.emp_id=?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						rs = pst.executeQuery();
						String strEmpStatus = null;
						while (rs.next()) {
							strEmpStatus = rs.getString("emp_status");
						}
						rs.close();
						pst.close();
						if (uF.parseToInt(strEmpId) == 143) {
							// System.out.println("strEmpStatus ===>> " +
							// strEmpStatus);
						}
						if (strEmpStatus == null || strEmpStatus.trim().equals("") || strEmpStatus.trim().equalsIgnoreCase("NULL")) {
							continue;
						}

						int nEmpStatus = 0;
						if (strEmpStatus.trim().equalsIgnoreCase(PROBATION)) {
							nEmpStatus = 1;
						} else if (strEmpStatus.trim().equalsIgnoreCase(PERMANENT)) {
							nEmpStatus = 2;
						} else if (strEmpStatus.trim().equalsIgnoreCase(TEMPORARY)) {
							nEmpStatus = 3;
						} else if (strEmpStatus.trim().equalsIgnoreCase(NOTICE)) {
							nEmpStatus = 4;
						}

						if (nEmpStatus == 0) {
							continue;
						}
						if (uF.parseToInt(strEmpId) == 143) {
							// System.out.println("nEmpStatus ===>> " +
							// nEmpStatus);
						}
						pst = con.prepareStatement("select * from probation_policy where emp_id=?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						rs = pst.executeQuery();
						String strAssignedLeave = null;
						while (rs.next()) {
							strAssignedLeave = rs.getString("leaves_types_allowed");
						}
						rs.close();
						pst.close();

						List<String> alEmpLeaves = new ArrayList<String>();
						if (strAssignedLeave != null && !strAssignedLeave.trim().equals("") && !strAssignedLeave.trim().equalsIgnoreCase("NULL")) {
							String[] strTemp = strAssignedLeave.trim().split(",");
							for (int x = 0; strTemp != null && x < strTemp.length; x++) {
								if (uF.parseToInt(strTemp[x].trim()) > 0 && !alEmpLeaves.contains(strTemp[x].trim())) {
									alEmpLeaves.add(strTemp[x].trim());
								}
							}
						}
						if (uF.parseToInt(strEmpId) == 143) {
							// System.out.println("alEmpLeaves ===>> " +
							// alEmpLeaves);
						}

						if (alEmpLeaves.size() == 0) {
							continue;
						}

						for (int j = 0; j < alLeaveType.size(); j++) {

							if (!alEmpLeaves.contains(alLeaveType.get(j))) {
								continue;
							}
							if (!alAvailableLeave.contains(nLevelId + "_" + strWLocationId + "_" + alLeaveType.get(j))) {
								continue;
							}

							String strAvailableLeaves = hmAvailableLeave.get(nLevelId + "_" + strWLocationId + "_" + alLeaveType.get(j));
							if (uF.parseToInt(strEmpId) == 143) {
								// System.out.println("strAvailableLeaves ===>> "
								// + strAvailableLeaves);
							}
							if (strAvailableLeaves != null && !strAvailableLeaves.trim().equals("") && !strAvailableLeaves.trim().equalsIgnoreCase("NULL")) {
								List<String> alAvailableLeavesFor = new ArrayList<String>();
								if (strAvailableLeaves != null && !strAvailableLeaves.trim().equals("") && !strAvailableLeaves.trim().equalsIgnoreCase("NULL")) {
									String[] strTemp = strAvailableLeaves.trim().split(",");
									for (int x = 0; strTemp != null && x < strTemp.length; x++) {
										if (!alAvailableLeavesFor.contains(strTemp[x].trim())) {
											alAvailableLeavesFor.add(strTemp[x].trim());
										}
									}
								}
								if (uF.parseToInt(strEmpId) == 145) {
//									 System.out.println("CD/12480---alAvailableLeavesFor ===>> " + alAvailableLeavesFor);
								}
								if (alAvailableLeavesFor.contains("" + nEmpStatus) || alAvailableLeavesFor.contains("0")) {

									String strAccrualSystem = hmAccrualSystem.get(nLevelId + "_" + strWLocationId + "_" + alLeaveType.get(j));
									String strAccrualFrom = hmAccrualFrom.get(nLevelId + "_" + strWLocationId + "_" + alLeaveType.get(j));
									if (uF.parseToInt(strEmpId) == 143) {
										// System.out.println("strAccrualSystem ===>> "
										// + strAccrualSystem);
										// System.out.println("strAccrualFrom ===>> "
										// + strAccrualFrom);
									}
									if (uF.parseToInt(strAccrualSystem) == 1
											&& ((uF.parseToInt(strAccrualFrom) == 2 && nEmpStatus != 1) || uF.parseToInt(strAccrualFrom) == 1)) {
										// String strType = "A";
										// if(!uF.parseToBoolean(hmCarryForwardAccrualMonthly.get(nLevelId+"_"+strWLocationId+"_"+alLeaveType.get(j)))){
										// strType = "C";
										// }
										pst = con
												.prepareStatement("select * from leave_register1  where emp_id = ? and leave_type_id=? and _date between ? and ?"
														+ " and update_balance=? and _type='A'"); // and
																									// _type=?
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(alLeaveType.get(j)));
										pst.setDate(3, uF.getDateFormat(startDate, DATE_FORMAT));
										pst.setDate(4, uF.getDateFormat(endDate, DATE_FORMAT));
										// pst.setString(5, strType);
										pst.setInt(5, 1);
										if (uF.parseToInt(strEmpId) == 143) {
											// System.out.println("pst==>"+pst);
										}
										rs = pst.executeQuery();
										boolean flag = false;
										while (rs.next()) {
											flag = true;
										}
										rs.close();
										pst.close();
										/*if (uF.parseToInt(strEmpId) == 145) {
											System.out.println("flag ===>> " + flag);
										}*/

										if (!flag) {
											double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly.get(nLevelId + "_" + strWLocationId + "_"
													+ alLeaveType.get(j)));
											if (dblAccrued > 0) {
												boolean carryForwardFlag = false;
												if (!uF.parseToBoolean(hmCarryForwardAccrualMonthly.get(nLevelId + "_" + strWLocationId + "_"
														+ alLeaveType.get(j)))) {
													// strType = "C";
													carryForwardFlag = true;
												}
												if (uF.parseToInt(strEmpId) == 143) {
													// System.out.println("carryForwardFlag ===>> "
													// + carryForwardFlag);
												}
												if (carryForwardFlag) {
													pst = con
															.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
																	+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
													pst.setDouble(1, 0.0d);
													pst.setDouble(2, 0.0d);
													pst.setDouble(3, 0.0d);
													pst.setInt(4, uF.parseToInt(strEmpId));
													pst.setInt(5, uF.parseToInt(alLeaveType.get(j)));
													pst.setDate(6, uF.getDateFormat(startDate, DATE_FORMAT));
													pst.setInt(7, 1);
													pst.setString(8, "C");
													pst.setDouble(9, 0.0d);
													pst.setInt(10, 0);
													if (uF.parseToInt(strEmpId) == 143) {
														// System.out.println("pst==>"+pst);
													}
													pst.execute();
													pst.close();
												}

												pst = con
														.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
																+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?,?,?,?,?,?,?)");
												pst.setDouble(1, 0.0d);
												pst.setDouble(2, 0.0d);
												pst.setDouble(3, dblAccrued);
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(alLeaveType.get(j)));
												pst.setDate(6, uF.getDateFormat(startDate, DATE_FORMAT));
												pst.setInt(7, 1);
												pst.setString(8, "A");
												pst.setDouble(9, 0.0d);
												pst.setInt(10, 0);
												if (uF.parseToInt(strEmpId) == 143) {
													// System.out.println("pst==>"+pst);
												}
												pst.execute();
												pst.close();
											}
										}
									} else if (uF.parseToInt(strAccrualSystem) == 2
											&& ((uF.parseToInt(strAccrualFrom) == 2 && nEmpStatus != 1) || uF.parseToInt(strAccrualFrom) == 1)) {
										// String strType = "A";
										// if(!uF.parseToBoolean(hmCarryForwardAccrualMonthly.get(nLevelId+"_"+strWLocationId+"_"+alLeaveType.get(j)))){
										// strType = "C";
										// }
										pst = con.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? and _date between ? and ?"
												+ " and update_balance=? and _type='A'"); // and
																							// _type=?
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(alLeaveType.get(j)));
										pst.setDate(3, uF.getDateFormat(startDate, DATE_FORMAT));
										pst.setDate(4, uF.getDateFormat(endDate, DATE_FORMAT));
										// pst.setString(5, strType);
										pst.setInt(5, 1);
										rs = pst.executeQuery();
										boolean flag = false;
										while (rs.next()) {
											flag = true;
										}
										rs.close();
										pst.close();
										if (uF.parseToInt(strEmpId) == 145) {
											System.out.println("CD/12600---flag="+flag);
										}

										if (!flag) {
											double dblAccrued = uF.parseToDouble(hmTotalLeavesMonthly.get(nLevelId + "_" + strWLocationId + "_"
													+ alLeaveType.get(j)));
											if (dblAccrued > 0) {
												boolean carryForwardFlag = false;
												if (!uF.parseToBoolean(hmCarryForwardAccrualMonthly.get(nLevelId + "_" + strWLocationId + "_"
														+ alLeaveType.get(j)))) {
													// strType = "C";
													carryForwardFlag = true;
												}

												if (carryForwardFlag) {
													pst = con
															.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
																	+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
													pst.setDouble(1, 0.0d);
													pst.setDouble(2, 0.0d);
													pst.setDouble(3, 0.0d);
													pst.setInt(4, uF.parseToInt(strEmpId));
													pst.setInt(5, uF.parseToInt(alLeaveType.get(j)));
													pst.setDate(6, uF.getDateFormat(endDate, DATE_FORMAT));
													pst.setInt(7, 1);
													pst.setString(8, "C");
													pst.setDouble(9, 0.0d);
													pst.setInt(10, 0);
													pst.execute();
													pst.close();
												}

												pst = con
														.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, accrued, emp_id,leave_type_id,"
																+ " _date,update_balance,_type,balance,compensate_id) values (?,?,?,?, ?,?,?,?, ?,?)");
												pst.setDouble(1, 0);
												pst.setDouble(2, 0);
												pst.setDouble(3, dblAccrued);
												pst.setInt(4, uF.parseToInt(alEmp.get(i)));
												pst.setInt(5, uF.parseToInt(alLeaveType.get(j)));
												pst.setDate(6, uF.getDateFormat(endDate, DATE_FORMAT));
												pst.setInt(7, 1);
												pst.setString(8, "A");
												pst.setDouble(9, 0.0d);
												pst.setInt(10, 0);
												pst.execute();
												pst.close();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private synchronized void setFinancialData(Connection con, UtilityFunctions uF) {
		Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
		String startDate = null;
		String endDate = null;
		int nMonth = uF.parseToInt(uF.getDateFormat("" + currDate, DBDATE, "MM"));
		int nYear = uF.parseToInt(uF.getDateFormat("" + currDate, DBDATE, "yyyy"));

		if (nMonth >= 4) {
			startDate = "01/04/" + nYear;
			endDate = "31/03/" + (nYear + 1);
		} else if (nMonth >= 1 && nMonth < 4) {
			startDate = "01/04/" + (nYear - 1);
			endDate = "31/03/" + nYear;
		}

		String calStartDate = "01/01/" + nYear;
		String calEndDate = "31/12/" + nYear;
		calendar_year_details(con, uF, calStartDate, calEndDate);

//		 System.out.println("CD/12416--startDate=====>"+startDate);
//		 System.out.println("CD/12417--endDate=====>"+endDate);
		if (startDate != null && endDate != null) {
			financial_year_details(con, uF, startDate, endDate);
			deduction_Tax_Details(con, uF, startDate, endDate);
			deduction_details_india(con, uF, startDate, endDate);
			epf_details(con, uF, startDate, endDate);
			esi_details(con, uF, startDate, endDate);
			exemption_details(con, uF, startDate, endDate);
			hra_exemption_details(con, uF, startDate, endDate);
			deduction_tax_misc_details(con, uF, startDate, endDate);
			section_details(con, uF, startDate, endDate);
		//===start parvez date: 30-07-2022===	
			regimeTaxSalb(con, uF, startDate, endDate);
		//===end parvez date: 30-07-2022===	
			// System.out.println("Data Successfully updated for financial year");
		}
	}

	private synchronized void calendar_year_details(Connection con, UtilityFunctions uF, String calStartDate, String calEndDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		try {

			pst = con.prepareStatement("select * from calendar_year_details where calendar_year_from =? and calendar_year_to=?");
			pst.setDate(1, uF.getDateFormat(calStartDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(calEndDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				// System.out.println("calendar year Detail alerady present !!!!!");
			} else {
				// System.out.println("Not data for financial....");

				pst1 = con.prepareStatement("insert into calendar_year_details(calendar_year_from,calendar_year_to)values(?,?)");
				pst1.setDate(1, uF.getDateFormat(calStartDate, DATE_FORMAT));
				pst1.setDate(2, uF.getDateFormat(calEndDate, DATE_FORMAT));
				pst1.executeUpdate();
				pst1.close();
				// System.out.println("calendar Year inserted Successfull!!!!!");

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void financial_year_details(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from financial_year_details where financial_year_from =? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				Date fystart = rs.getDate("financial_year_from");
				Date fyend = rs.getDate("financial_year_to");

				// System.out.println("Financial year Detail alerady present !!!!!");
			} else {
				// System.out.println("Not data for financial....");

				pst1 = con.prepareStatement("insert into financial_year_details(financial_year_from,financial_year_to)values(?,?)");
				pst1.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
				pst1.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
				pst1.executeUpdate();
				pst1.close();
				// System.out.println("Financial Year inserted Successfull!!!!!");

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void deduction_Tax_Details(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {
			pst = con.prepareStatement("select * from deduction_tax_details where financial_year_from =? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				// System.out.println("deduction_Tax_Details Data alerady present for this year!!!!!");
			} else {
				// System.out.println("deduction_Tax_Details Not data for financial....");

				java.util.Date preStartDate = getyear(uF, startDate, -1);
				java.util.Date PreEndDate = getyear(uF, endDate, -1);

				pst1 = con.prepareStatement("select * from deduction_tax_details where financial_year_from =? and financial_year_to=?");
				pst1.setDate(1, (java.sql.Date) preStartDate);
				pst1.setDate(2, (java.sql.Date) PreEndDate);

				rus = pst1.executeQuery();
				while (rus.next()) {

					double _from = rus.getDouble("_from");
					double _to = rus.getDouble("_to");
					String gender = rus.getString("gender");
					int age_from = rus.getInt("age_from");
					int age_to = rus.getInt("age_to");
					double deduction_amount = rus.getDouble("deduction_amount");
					String deduction_type = rus.getString("deduction_type");
					// Date entry_date = rus.getDate(12);
					int user_id = rus.getInt("user_id");
					int slab_type = rus.getInt("slab_type");

					pst2 = con.prepareStatement("insert into deduction_tax_details(_from,_to,gender,age_from,age_to,deduction_amount,deduction_type,financial_year_from," +
						" financial_year_to,entry_date,user_id,slab_type)values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst2.setDouble(1, _from);
					pst2.setDouble(2, _to);
					pst2.setString(3, gender);
					pst2.setInt(4, age_from);
					pst2.setInt(5, age_to);
					pst2.setDouble(6, deduction_amount);
					pst2.setString(7, deduction_type);
					pst2.setDate(8, uF.getDateFormat(startDate, DATE_FORMAT));
					pst2.setDate(9, uF.getDateFormat(endDate, DATE_FORMAT));
					pst2.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
					pst2.setInt(11, user_id);
					pst2.setInt(12, slab_type);
					pst2.executeUpdate();
					pst2.close();
				}
				rus.close();
				pst1.close();

				// System.out.println("deduction_Tax_Details inserted Successfull!!!!!");

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public synchronized void deduction_details_india(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {

			pst = con.prepareStatement("delete from deduction_details_india where "
					+ "financial_year_from =? and financial_year_to=? and (gender is null or gender ='')");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			pst.execute();
			pst.close();

			pst = con.prepareStatement("select * from deduction_details_india where financial_year_from =? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				double _from = rs.getDouble(1);
				double _to = rs.getDouble(2);

				// System.out.println("deduction_details_india Data alerady present for this year!!!!!");

			} else {
				// System.out.println("deduction_Tax_Details Not data for financial....");

				java.util.Date preStartDate = getyear(uF, startDate, -1);
				java.util.Date PreEndDate = getyear(uF, endDate, -1);

				pst1 = con.prepareStatement("select * from deduction_details_india where financial_year_from =? "
						+ "and financial_year_to=? and (gender is not null and gender !='')");
				pst1.setDate(1, (java.sql.Date) preStartDate);
				pst1.setDate(2, (java.sql.Date) PreEndDate);
				rus = pst1.executeQuery();
				while (rus.next()) {

					// Integer deduction_id = rus.getInt(1);
					double income_from = rus.getDouble("income_from");
					double income_to = rus.getDouble("income_to");
					int state_id = rus.getInt("state_id");
					double deduction_amount = rus.getDouble("deduction_amount");
					String amount_type = rus.getString("amount_type");
					int user_id = rus.getInt("user_id");
					double deduction_paycycle = rus.getDouble("deduction_paycycle");
					String gender = rus.getString("gender");
					// Date entry_date = rus.getDate(11);

					pst2 = con.prepareStatement("insert into deduction_details_india(income_from,income_to,"
							+ "state_id,deduction_amount,amount_type,user_id,deduction_paycycle," + "financial_year_from,financial_year_to,entry_date,gender)"
							+ "values(?,?,?,?, ?,?,?,?, ?,?,?)");
					pst2.setDouble(1, income_from);
					pst2.setDouble(2, income_to);
					pst2.setInt(3, state_id);
					pst2.setDouble(4, deduction_amount);
					pst2.setString(5, amount_type);
					pst2.setInt(6, user_id);
					pst2.setDouble(7, deduction_paycycle);
					pst2.setDate(8, uF.getDateFormat(startDate, DATE_FORMAT));
					pst2.setDate(9, uF.getDateFormat(endDate, DATE_FORMAT));
					pst2.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
					pst2.setString(11, gender);
					pst2.executeUpdate();
					pst2.close();

				}
				rus.close();
				pst1.close();
				// System.out.println("deduction_details_india Record inserted Successfull!!!!!");

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst2 != null) {
				try {
					pst2.close();
					pst2 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void epf_details(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {
			pst = con.prepareStatement("select * from level_details");
			rs = pst.executeQuery();
			List<String> alLevel = new ArrayList<String>();
			while (rs.next()) {
				alLevel.add(rs.getString("level_id"));
			}
			rs.close();
			pst.close();

			for (int i = 0; alLevel != null && i < alLevel.size(); i++) {
				pst = con.prepareStatement("select * from epf_details where financial_year_start =? and financial_year_end=? and level_id=?");
				pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
				pst.setInt(3, Integer.parseInt(alLevel.get(i)));
				rs = pst.executeQuery();
				if (rs.next()) {
					// System.out.println("level_id =" + alLevel.get(i));
					// System.out.println("epf_details Data alerady present for this year!!!!!");

				} else {
					// System.out.println("epf_details Not data for financial....");

					java.util.Date preStartDate = getyear(uF, startDate, -1);
					java.util.Date PreEndDate = getyear(uF, endDate, -1);

					pst1 = con.prepareStatement("select * from epf_details where financial_year_start =? and financial_year_end=? and level_id=?");
					pst1.setDate(1, (java.sql.Date) preStartDate);
					pst1.setDate(2, (java.sql.Date) PreEndDate);
					pst1.setInt(3, Integer.parseInt(alLevel.get(i)));
					rus = pst1.executeQuery();
					while (rus.next()) {

						String salary_head_id = rus.getString("salary_head_id");
						double epf_max_limit = rus.getDouble("epf_max_limit");
						double eepf_contribution = rus.getDouble("eepf_contribution");
						double erpf_contribution = rus.getDouble("erpf_contribution");
						double erps_contribution = rus.getDouble("erps_contribution");
						double erdli_contribution = rus.getDouble("erdli_contribution");
						double pf_admin_charges = rus.getDouble("pf_admin_charges");
						double edli_admin_charges = rus.getDouble("edli_admin_charges");
						int user_id = rus.getInt("user_id");
						// Timestamp entry_timestamp =
						// rus.getTimestamp("entry_timestamp");
						double eps_max_limit = rus.getDouble("eps_max_limit");
						double edli_max_limit = rus.getDouble("edli_max_limit");
						boolean is_erpf_contribution = rus.getBoolean("is_erpf_contribution");
						boolean is_erps_contribution = rus.getBoolean("is_erps_contribution");
						boolean is_erdli_contribution = rus.getBoolean("is_erdli_contribution");
						boolean is_pf_admin_charges = rus.getBoolean("is_pf_admin_charges");
						boolean is_edli_admin_charges = rus.getBoolean("is_edli_admin_charges");
						double erpf_max_limit = rus.getDouble("erpf_max_limit");
						int org_id = rus.getInt("org_id");
						int level_id = rus.getInt("level_id");

						pst2 = con.prepareStatement("insert into epf_details(financial_year_start,financial_year_end,salary_head_id,epf_max_limit,"
								+ "eepf_contribution,erpf_contribution,erps_contribution,erdli_contribution,pf_admin_charges,"
								+ "edli_admin_charges,user_id,entry_timestamp,eps_max_limit,edli_max_limit,is_erpf_contribution,"
								+ "is_erps_contribution,is_erdli_contribution,is_pf_admin_charges,is_edli_admin_charges,erpf_max_limit,org_id,level_id)"
								+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						pst2.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
						pst2.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
						pst2.setString(3, salary_head_id);
						pst2.setDouble(4, epf_max_limit);
						pst2.setDouble(5, eepf_contribution);
						pst2.setDouble(6, erpf_contribution);
						pst2.setDouble(7, erps_contribution);
						pst2.setDouble(8, erdli_contribution);
						pst2.setDouble(9, pf_admin_charges);
						pst2.setDouble(10, edli_admin_charges);
						pst2.setInt(11, user_id);
						pst2.setTimestamp(12,
								uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE + DBTIME));
						pst2.setDouble(13, eps_max_limit);
						pst2.setDouble(14, edli_max_limit);
						pst2.setBoolean(15, is_erpf_contribution);
						pst2.setBoolean(16, is_erps_contribution);
						pst2.setBoolean(17, is_erdli_contribution);
						pst2.setBoolean(18, is_pf_admin_charges);
						pst2.setBoolean(19, is_edli_admin_charges);
						pst2.setDouble(20, erpf_max_limit);
						pst2.setInt(21, org_id);
						pst2.setInt(22, level_id);
						pst2.executeUpdate();
						pst2.close();
					}
					rus.close();
					pst1.close();
					// System.out.println("epf_details Record inserted Successfull!!!!!");

				}
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst2 != null) {
				try {
					pst2.close();
					pst2 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void esi_details(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {
			pst = con.prepareStatement("select * from org_details");
			rs = pst.executeQuery();
			List<String> alOrg = new ArrayList<String>();
			while (rs.next()) {
				alOrg.add(rs.getString("org_id"));
			}
			rs.close();
			pst.close();

			for (int i = 0; alOrg != null && i < alOrg.size(); i++) {

				pst = con.prepareStatement("select * from esi_details where financial_year_start =? and financial_year_end=? and org_id=?");
				pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
				pst.setInt(3, Integer.parseInt(alOrg.get(i)));
				rs = pst.executeQuery();
				if (rs.next()) {
					// System.out.println("esi_details Data alerady present for this year!!!!!");
				} else {
					// System.out.println("esi_details Not data for financial....");

					java.util.Date preStartDate = getyear(uF, startDate, -1);
					java.util.Date PreEndDate = getyear(uF, endDate, -1);

					pst1 = con.prepareStatement("select * from esi_details where financial_year_start =? and financial_year_end=? and org_id=?");
					pst1.setDate(1, (java.sql.Date) preStartDate);
					pst1.setDate(2, (java.sql.Date) PreEndDate);
					pst1.setInt(3, Integer.parseInt(alOrg.get(i)));
					rus = pst1.executeQuery();
					while (rus.next()) {

						String salary_head_id = rus.getString("salary_head_id");
						double eesi_contribution = rus.getDouble("eesi_contribution");
						double ersi_contribution = rus.getDouble("ersi_contribution");
						int user_id = rus.getInt("user_id");
						// Timestamp entry_timestamp =
						// rus.getTimestamp("entry_timestamp");
						double max_limit = rus.getDouble("max_limit");
						int state_id = rus.getInt("state_id");
						int nLevelId = rus.getInt("level_id");
						String eligible_salary_head_ids = rus.getString("eligible_salary_head_ids");

						pst2 = con.prepareStatement("insert into esi_details(salary_head_id,financial_year_start,financial_year_end,"
								+ "eesi_contribution,ersi_contribution,user_id,entry_timestamp,max_limit,state_id,org_id,level_id,"
								+ "eligible_salary_head_ids)values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
						pst2.setString(1, salary_head_id);
						pst2.setDate(2, uF.getDateFormat(startDate, DATE_FORMAT));
						pst2.setDate(3, uF.getDateFormat(endDate, DATE_FORMAT));
						pst2.setDouble(4, eesi_contribution);
						pst2.setDouble(5, ersi_contribution);
						pst2.setInt(6, user_id);
						pst2.setTimestamp(7,
								uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE + DBTIME));
						pst2.setDouble(8, max_limit);
						pst2.setInt(9, state_id);
						pst2.setInt(10, Integer.parseInt(alOrg.get(i)));
						pst2.setInt(11, nLevelId);
						pst2.setString(12, eligible_salary_head_ids);
						pst2.executeUpdate();
						pst2.close();
						// System.out.println("pst===>"+pst);

					}
					rus.close();
					pst1.close();
					// System.out.println("esi_details Record inserted Successfull!!!!!");

				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst2 != null) {
				try {
					pst2.close();
					pst2 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void exemption_details(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {
			pst = con.prepareStatement("select * from exemption_details where exemption_from =? and exemption_to=?");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				// System.out.println("exemption_details Data alerady present for this year!!!!!");
			} else {
				// System.out.println("exemption_details Not data for financial....");

				java.util.Date preStartDate = getyear(uF, startDate, -1);
				java.util.Date PreEndDate = getyear(uF, endDate, -1);

				pst1 = con.prepareStatement("select * from exemption_details where exemption_from =? and exemption_to=?");
				pst1.setDate(1, (java.sql.Date) preStartDate);
				pst1.setDate(2, (java.sql.Date) PreEndDate);
				rus = pst1.executeQuery();
				while (rus.next()) {

					String exemption_code = rus.getString("exemption_code");
					String exemption_name = rus.getString("exemption_name");
					String exemption_description = rus.getString("exemption_description");
					double exemption_limit = rus.getDouble("exemption_limit");
					// Date entry_date = rus.getDate("");
					int user_id = rus.getInt("user_id");

					int salary_head_id = rus.getInt("salary_head_id");
					int under_section = rus.getInt("under_section");
					boolean investment_form = rus.getBoolean("investment_form");
					int slab_type = rus.getInt("slab_type");

					pst2 = con.prepareStatement("insert into exemption_details(exemption_code,exemption_name,exemption_description,exemption_limit,"
						+ "exemption_from,exemption_to,entry_date,user_id,salary_head_id,under_section,investment_form,slab_type)values(?,?,?,?,?,?,?,?,?,?,?,?)");

					pst2.setString(1, exemption_code);
					pst2.setString(2, exemption_name);
					pst2.setString(3, exemption_description);
					pst2.setDouble(4, exemption_limit);
					pst2.setDate(5, uF.getDateFormat(startDate, DATE_FORMAT));
					pst2.setDate(6, uF.getDateFormat(endDate, DATE_FORMAT));
					pst2.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
					pst2.setInt(8, user_id);
					pst2.setInt(9, salary_head_id);
					pst2.setInt(10, under_section);
					pst2.setBoolean(11, investment_form);
					pst2.setInt(12, slab_type);
					pst2.executeUpdate();
					pst2.close();
				}
				rus.close();
				pst1.close();
				// System.out.println("Financial Record inserted Successfull!!!!!");

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst2 != null) {
				try {
					pst2.close();
					pst2 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void hra_exemption_details(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from =? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				// System.out.println("hra_exemption_details Data alerady present for this year!!!!!");
			} else {

				// System.out.println("hra_exemption_details Not data for financial....");

				java.util.Date preStartDate = getyear(uF, startDate, -1);
				java.util.Date PreEndDate = getyear(uF, endDate, -1);

				pst1 = con.prepareStatement("select * from hra_exemption_details where financial_year_from =? and financial_year_to=?");
				pst1.setDate(1, (java.sql.Date) preStartDate);
				pst1.setDate(2, (java.sql.Date) PreEndDate);
				rus = pst1.executeQuery();
				while (rus.next()) {

					double condition1 = rus.getDouble("condition1");
					String condition1_type = rus.getString("condition1_type");
					double condition2 = rus.getDouble("condition2");
					String condition2_type = rus.getString("condition2_type");
					double condition3 = rus.getDouble("condition3");
					String condition3_type = rus.getString("condition3_type");
					int user_id = rus.getInt("user_id");
					String strHraSalHeads = rus.getString("salary_head_id");

					pst2 = con.prepareStatement("insert into hra_exemption_details(condition1,condition1_type,condition2,condition2_type,condition3,"
							+ "condition3_type,financial_year_from,financial_year_to,entry_date,user_id,salary_head_id)values(?,?,?,?,?,?,?,?,?,?,?)");

					pst2.setDouble(1, condition1);
					pst2.setString(2, condition1_type);
					pst2.setDouble(3, condition2);
					pst2.setString(4, condition2_type);
					pst2.setDouble(5, condition3);
					pst2.setString(6, condition3_type);
					pst2.setDate(7, uF.getDateFormat(startDate, DATE_FORMAT));
					pst2.setDate(8, uF.getDateFormat(endDate, DATE_FORMAT));
					pst2.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
					pst2.setInt(10, user_id);
					pst2.setString(11, strHraSalHeads);
					pst2.executeUpdate();
					pst2.close();

				}
				rus.close();
				pst1.close();
				// System.out.println("Financial Record inserted Successfull!!!!!");

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst2 != null) {
				try {
					pst2.close();
					pst2 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void deduction_tax_misc_details(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from =? and financial_year_to=? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				double _from = rs.getDouble(1);
				double _to = rs.getDouble(2);

				// System.out.println("deduction_tax_misc_details Data alerady present for this year!!!!!");

			} else {
				// System.out.println("deduction_tax_misc_details Not data for financial....");

				java.util.Date preStartDate = getyear(uF, startDate, -1);
				java.util.Date PreEndDate = getyear(uF, endDate, -1);

				pst1 = con
						.prepareStatement("select * from deduction_tax_misc_details where financial_year_from =? and financial_year_to=? and trail_status = 1");
				pst1.setDate(1, (java.sql.Date) preStartDate);
				pst1.setDate(2, (java.sql.Date) PreEndDate);
				rus = pst1.executeQuery();
				while (rus.next()) {
					double standard_tax = rus.getDouble("standard_tax");
					double education_tax = rus.getDouble("education_tax");
					double flat_tds = rus.getDouble("flat_tds");
					double service_tax = rus.getDouble("service_tax");
					String deduction_type = rus.getString("deduction_type");
					int user_id = rus.getInt("user_id");
					int trail_status = rus.getInt("trail_status");
					int state_id = rus.getInt("state_id");

					double netTaxIncome = rus.getDouble("max_net_tax_income");
					double rebateAmt = rus.getDouble("rebate_amt");
					double swachhaBharatCess = rus.getDouble("swachha_bharat_cess");
					double krishiKalyanCess = rus.getDouble("krishi_kalyan_cess");
					double cgst = rus.getDouble("cgst");
					double sgst = rus.getDouble("sgst");

					// Timestamp entry_timestamp =
					// rus.getTimestamp("entry_timestamp");

					pst2 = con.prepareStatement("insert into deduction_tax_misc_details(standard_tax,education_tax,flat_tds,service_tax,deduction_type,"
							+ "financial_year_from,financial_year_to,_year,entry_timestamp,user_id,trail_status,state_id,max_net_tax_income,"
							+ "rebate_amt,swachha_bharat_cess,krishi_kalyan_cess,cgst,sgst)" + "values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst2.setDouble(1, standard_tax);
					pst2.setDouble(2, education_tax);
					pst2.setDouble(3, flat_tds);
					pst2.setDouble(4, service_tax);
					pst2.setString(5, deduction_type);
					pst2.setDate(6, uF.getDateFormat(startDate, DATE_FORMAT));
					pst2.setDate(7, uF.getDateFormat(endDate, DATE_FORMAT));
					pst2.setInt(8, uF.parseToInt(uF.getDateFormat(startDate, DATE_FORMAT, "yyyy")));
					pst2.setTimestamp(9,
							uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()) + "", DBDATE + DBTIME));
					pst2.setInt(10, user_id);
					pst2.setInt(11, trail_status);
					pst2.setInt(12, state_id);
					pst2.setDouble(13, netTaxIncome);
					pst2.setDouble(14, rebateAmt);
					pst2.setDouble(15, swachhaBharatCess);
					pst2.setDouble(16, krishiKalyanCess);
					pst2.setDouble(17, cgst);
					pst2.setDouble(18, sgst);
					pst2.executeUpdate();
					pst2.close();
				}
				rus.close();
				pst1.close();
				// System.out.println("deduction_tax_misc_details Financial Record inserted Successfull!!!!!");

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst2 != null) {
				try {
					pst2.close();
					pst2 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized void section_details(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {
			pst = con.prepareStatement("select * from section_details where financial_year_start =? and financial_year_end=?");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				// System.out.println("section_details Data alerady present for this year!!!!!");
			} else {
				// System.out.println("section_details Not data for financial....");

				java.util.Date preStartDate = getyear(uF, startDate, -1);
				java.util.Date PreEndDate = getyear(uF, endDate, -1);

				pst1 = con.prepareStatement("select * from section_details where financial_year_start =? and financial_year_end=?");
				pst1.setDate(1, (java.sql.Date) preStartDate);
				pst1.setDate(2, (java.sql.Date) PreEndDate);
				rus = pst1.executeQuery();
				while (rus.next()) {

					int section_id = rus.getInt("section_id");
					String section_code = rus.getString("section_code");
					String section_description = rus.getString("section_description");
					double section_exemption_limit = rus.getDouble("section_exemption_limit");
					String section_limit_type = rus.getString("section_limit_type");
					// Date entry_date = rus.getDate("entry_date");
					int user_id = rus.getInt("user_id");
					boolean isdisplay = rus.getBoolean("isdisplay");
					int under_section = rus.getInt("under_section");
					
					boolean is_pf_applicable = rus.getBoolean("is_pf_applicable");
					boolean is_ceiling_applicable = rus.getBoolean("is_ceiling_applicable");
					
					double ceiling_amount = rus.getDouble("ceiling_amount");
					
					String sub_section_1 = rus.getString("sub_section_1");
					double sub_section_1_amt = rus.getDouble("sub_section_1_amt");
					String sub_section_1_description = rus.getString("sub_section_1_description");
					String sub_section_1_limit_type = rus.getString("sub_section_1_limit_type");
					boolean sub_section_1_is_adjust_gross_income_limit = rus.getBoolean("sub_section_1_is_adjust_gross_income_limit");
					
					String sub_section_2 = rus.getString("sub_section_2");
					double sub_section_2_amt = rus.getDouble("sub_section_2_amt");
					String sub_section_2_description = rus.getString("sub_section_2_description");
					String sub_section_2_limit_type = rus.getString("sub_section_2_limit_type");
					boolean sub_section_2_is_adjust_gross_income_limit = rus.getBoolean("sub_section_2_is_adjust_gross_income_limit");
					
					String sub_section_3 = rus.getString("sub_section_3");
					double sub_section_3_amt = rus.getDouble("sub_section_3_amt");
					String sub_section_3_description = rus.getString("sub_section_3_description");
					String sub_section_3_limit_type = rus.getString("sub_section_3_limit_type");
					boolean sub_section_3_is_adjust_gross_income_limit = rus.getBoolean("sub_section_3_is_adjust_gross_income_limit");
					
					String sub_section_4 = rus.getString("sub_section_4");
					double sub_section_4_amt = rus.getDouble("sub_section_4_amt");
					String sub_section_4_description = rus.getString("sub_section_4_description");
					String sub_section_4_limit_type = rus.getString("sub_section_4_limit_type");
					boolean sub_section_4_is_adjust_gross_income_limit = rus.getBoolean("sub_section_4_is_adjust_gross_income_limit");
					
					String sub_section_5 = rus.getString("sub_section_5");
					double sub_section_5_amt = rus.getDouble("sub_section_5_amt");
					String sub_section_5_description = rus.getString("sub_section_5_description");
					String sub_section_5_limit_type = rus.getString("sub_section_5_limit_type");
					boolean sub_section_5_is_adjust_gross_income_limit = rus.getBoolean("sub_section_5_is_adjust_gross_income_limit");
					
					boolean is_adjusted_gross_income_limit = rus.getBoolean("is_adjusted_gross_income_limit");
					String include_sub_section = rus.getString("include_sub_section");
					String combine_sub_section = rus.getString("combine_sub_section");
					int slab_type = rus.getInt("slab_type");
					
					pst2 = con.prepareStatement("insert into section_details(section_id,section_code,section_description,section_exemption_limit,"
						+ "section_limit_type,user_id,isdisplay,under_section,financial_year_start,financial_year_end,sub_section_1,sub_section_1_amt," +
						"sub_section_1_description,sub_section_1_limit_type,sub_section_1_is_adjust_gross_income_limit,sub_section_2,sub_section_2_amt," +
						"sub_section_2_description,sub_section_2_limit_type,sub_section_2_is_adjust_gross_income_limit,sub_section_3,sub_section_3_amt," +
						"sub_section_3_description,sub_section_3_limit_type,sub_section_3_is_adjust_gross_income_limit,sub_section_4,sub_section_4_amt," +
						"sub_section_4_description,sub_section_4_limit_type,sub_section_4_is_adjust_gross_income_limit,sub_section_5,sub_section_5_amt," +
						"sub_section_5_description,sub_section_5_limit_type,sub_section_5_is_adjust_gross_income_limit,is_adjusted_gross_income_limit," +
						"include_sub_section,combine_sub_section,slab_type,is_pf_applicable,is_ceiling_applicable,ceiling_amount) " +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst2.setInt(1, section_id);
					pst2.setString(2, section_code);
					pst2.setString(3, section_description);
					pst2.setDouble(4, section_exemption_limit);
					pst2.setString(5, section_limit_type);
					pst2.setInt(6, user_id);
					pst2.setBoolean(7, isdisplay);
					pst2.setInt(8, under_section);
					pst2.setDate(9, uF.getDateFormat(startDate, DATE_FORMAT));
					pst2.setDate(10, uF.getDateFormat(endDate, DATE_FORMAT));
					
					pst2.setString(11, sub_section_1);
					pst2.setDouble(12, sub_section_1_amt);
					pst2.setString(13, sub_section_1_description);
					pst2.setString(14, sub_section_1_limit_type);
					pst2.setBoolean(15, sub_section_1_is_adjust_gross_income_limit);
					
					pst2.setString(16, sub_section_2);
					pst2.setDouble(17, sub_section_2_amt);
					pst2.setString(18, sub_section_2_description);
					pst2.setString(19, sub_section_2_limit_type);
					pst2.setBoolean(20, sub_section_2_is_adjust_gross_income_limit);
					
					pst2.setString(21, sub_section_3);
					pst2.setDouble(22, sub_section_3_amt);
					pst2.setString(23, sub_section_3_description);
					pst2.setString(24, sub_section_3_limit_type);
					pst2.setBoolean(25, sub_section_3_is_adjust_gross_income_limit);
					
					pst2.setString(26, sub_section_4);
					pst2.setDouble(27, sub_section_4_amt);
					pst2.setString(28, sub_section_4_description);
					pst2.setString(29, sub_section_4_limit_type);
					pst2.setBoolean(30, sub_section_4_is_adjust_gross_income_limit);
					
					pst2.setString(31, sub_section_5);
					pst2.setDouble(32, sub_section_5_amt);
					pst2.setString(33, sub_section_5_description);
					pst2.setString(34, sub_section_5_limit_type);
					pst2.setBoolean(35, sub_section_5_is_adjust_gross_income_limit);
					
					pst2.setBoolean(36, is_adjusted_gross_income_limit);
					pst2.setString(37, include_sub_section);
					pst2.setString(38, combine_sub_section);
					pst2.setInt(39, slab_type);
					pst2.setBoolean(40, is_pf_applicable);
					pst2.setBoolean(41, is_ceiling_applicable);
					pst2.setDouble(42, ceiling_amount);
					pst2.executeUpdate();
					pst2.close();
				}
				rus.close();
				pst1.close();
				// System.out.println("Financial Record inserted Successfull!!!!!");

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst2 != null) {
				try {
					pst2.close();
					pst2 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public synchronized String datetoformsat(java.util.Date date, String strFormat) {

		try {
			if (date == null) {
				return null;
			}
			SimpleDateFormat smft = new SimpleDateFormat(strFormat);

			return smft.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized java.util.Date getyear(UtilityFunctions uF, String isdate, int value) {

		Date actualdate = null;

		try {

			Calendar calendar = new GregorianCalendar();
			Date currentDate = uF.getDateFormat(isdate, DATE_FORMAT);
			calendar.setTime(currentDate);
			calendar.add(Calendar.YEAR, value);
			String nextYear = datetoformsat(calendar.getTime(), DATE_FORMAT);
			actualdate = uF.getDateFormat(nextYear, DATE_FORMAT);

			// System.out.format("Actual Financial Date :::::::::::::::::  %s\n",
			// actualdate);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return actualdate;

	}

	private synchronized void getInductionData(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {

			int totalInduction = 0;
			String strUserType = (String) session.getAttribute(BASEUSERTYPE);

			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), DATE_FORMAT);
			java.sql.Date dayAfterTomorrowDate1 = uF.getFutureDate(CF.getStrTimeZone(), 2);
			java.sql.Date tomorrowDate1 = uF.getFutureDate(CF.getStrTimeZone(), 1);
			java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat("" + tomorrowDate1, DBDATE, DATE_FORMAT), DATE_FORMAT);
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat("" + dayAfterTomorrowDate1, DBDATE, DATE_FORMAT), DATE_FORMAT);

			// get today,tomorrow and dayAfterTomorrow induction
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
			// System.out.println("induction pst1==>"+pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				String strJoiningDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT);
				java.util.Date joiningDate = uF.getDateFormatUtil(strJoiningDate, DATE_FORMAT);

				if (joiningDate.equals(currDate) || joiningDate.equals(tomorrowDate) || joiningDate.equals(dayAfterTomorrowDate)) {
					totalInduction++;
				}

			}
			rst.close();
			pst.close();

			// get pending induction
			StringBuilder sbQuery3 = new StringBuilder();
			sbQuery3.append("select * from employee_personal_details  epd,employee_official_details eod where epd.emp_per_id = eod.emp_id and (joining_date is null or joining_date < ?) and ((approved_flag=false and is_alive=false and emp_filled_flag=false) or (approved_flag=false and is_alive=false and emp_filled_flag=true)) ");

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) session.getAttribute(WLOCATION_ACCESS) != null && !((String) session.getAttribute(WLOCATION_ACCESS)).equals("")) {
					sbQuery3.append(" and (wlocation_id is null or wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + "))");
				} else {
					sbQuery3.append(" and (wlocation_id is null or wlocation_id in (" + (String) session.getAttribute(WLOCATIONID) + "))");
				}
			}

			if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER)) {
				if ((String) (String) session.getAttribute(ORG_ACCESS) != null && !((String) session.getAttribute(ORG_ACCESS)).equals("")) {
					sbQuery3.append(" and (org_id is null or org_id in (" + (String) session.getAttribute(ORG_ACCESS) + "))");
				} else {
					sbQuery3.append(" and (org_id is null or org_id in (" + (String) session.getAttribute(ORGID) + "))");
				}
			}

			sbQuery3.append(" order by emp_per_id desc");
			pst = con.prepareStatement(sbQuery3.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			// System.out.println("induction pst2==>"+pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				totalInduction++;
			}
			rst.close();
			pst.close();

			// System.out.println("cron totalInduction==>"+totalInduction);

			if (strUserType != null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER))) {
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmpId);
				userAlerts.setStrCount(totalInduction + "");
				userAlerts.set_type(NEW_JOINEES_ALERT);
				userAlerts.setStatus(INSERT_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void getResignationData(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// String strUserType = (String)session.getAttribute(BASEUSERTYPE);
			// String strUserTypeId = (String)
			// session.getAttribute(BASEUSERTYPEID);
			String strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
			String strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
			String strUserTypeId = (String) session.getAttribute(USERTYPEID);
			String strUserType = (String) session.getAttribute(USERTYPE);

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
			int a = 0, b = 0, c = 0;
			int totalResignation = 0;
			int totalFD = 0;
			while (rs.next()) {
				a++;
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("off_board_id"));
				if (checkEmpList == null)
					checkEmpList = new ArrayList<String>();

				if (!checkEmpList.contains(strEmpId) && !strBaseUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}

				String userType = rs.getString("user_type");
				if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(ADMIN) || strBaseUserType.equalsIgnoreCase(HRMANAGER))
						&& alList.contains(rs.getString("off_board_id"))) {
					continue;
				} else if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(ADMIN) || strBaseUserType.equalsIgnoreCase(HRMANAGER))
						&& !alList.contains(rs.getString("off_board_id"))) {
					userType = strBaseUserTypeId;
					alList.add(rs.getString("off_board_id"));
				}

				if (rs.getString("emp_status") != null && rs.getString("emp_status").equalsIgnoreCase(RESIGNED)) {
					if (rs.getString("entry_date") != null && !rs.getString("entry_date").equals("")) {
						String lastDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT);
						java.util.Date regDate = uF.getDateFormatUtil(lastDate, DATE_FORMAT);

						if (regDate != null && regDate.equals(currDate)) {
							totalResignation++;

						} else {
							if (rs.getBoolean("is_alive")) {
								// if(rs.getString("emp_status")!=null &&
								// !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")){
								totalResignation++;
								// }
							}
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

			// System.out.println("totalResignation==>"+totalResignation+"==>totalFD==>"+totalFD);

			if (strBaseUserType != null && (strBaseUserType.equals(ADMIN) || strBaseUserType.equals(HRMANAGER))) {
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmpId);
				userAlerts.setStrCount(totalResignation + "");
				userAlerts.set_type(EMP_RESIGNATIONS_ALERT);
				userAlerts.setStatus(INSERT_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}

			if (strBaseUserType != null && (strBaseUserType.equals(ADMIN) || strBaseUserType.equals(HRMANAGER))) {
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmpId);
				userAlerts.setStrCount(totalFD + "");
				userAlerts.set_type(EMP_FINAL_DAY_ALERT);
				userAlerts.setStatus(INSERT_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private synchronized void getConfirmationData(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			String strUserType = (String) session.getAttribute(BASEUSERTYPE);
			Map<String, String> hmEmpProbation = new HashMap<String, String>();
			java.util.Date tommorowDate = uF.getDateFormatUtil(
					uF.getDateFormat("" + uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1), DBDATE, DATE_FORMAT), DATE_FORMAT);
			java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(
					uF.getDateFormat("" + uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2), DBDATE, DATE_FORMAT), DATE_FORMAT);

			// get Map of probation,notice duration of employees
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

			// get confirmation employee count
			int totalConfirmation = 0;
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
			// System.out.println("induction confirmation pst==>"+pst);
			rst = pst.executeQuery();
			while (rst.next()) {
				// System.out.println("joining_date ===>>" +
				// rst.getString("joining_date"));
				if (rst.getString("joining_date") != null && !rst.getString("joining_date").trim().equals("")) {
					String joiningDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE, DATE_FORMAT);
					java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT),
							DATE_FORMAT);
					java.util.Date startDate = uF.getDateFormatUtil(joiningDate, DATE_FORMAT);

					int probation = uF.parseToInt(hmEmpProbation.get((String) rst.getString("emp_per_id")));

					String futureDate = uF.getDateFormat("" + uF.getFutureDate(startDate, probation), DBDATE, DATE_FORMAT);
					java.util.Date confDate = null;
					if (probation > 0) {
						confDate = uF.getDateFormatUtil(futureDate, DATE_FORMAT);
					} else {
						confDate = uF.getDateFormatUtil(joiningDate, DATE_FORMAT);
					}

					if (confDate.equals(currDate) || confDate.equals(tommorowDate) || confDate.equals(dayAfterTomorrowDate) || confDate.before(currDate)) {
						totalConfirmation++;
					}
				}
			}
			rst.close();
			pst.close();

			// System.out.println("totalConfirmation==>"+totalConfirmation);
			if (strUserType != null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER))) {
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(strEmpId);
				userAlerts.setStrCount(totalConfirmation + "");
				userAlerts.set_type(EMP_CONFIRMATIONS_ALERT);
				userAlerts.setStatus(INSERT_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
					rst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*public static void main(String[] args) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);

		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			CronData cd = new CronData();
			cd.assignShiftTransitionWeeklyOffOnBasisOfRules(con, uF);
		} catch (Exception e) {
			e.printStackTrace();																							
		} finally {
			db.closeConnection(con);
		}
	}*/
	
//===created parvez date: 30-07-2022===	
	//===start===
	public synchronized void regimeTaxSalb(Connection con, UtilityFunctions uF, String startDate, String endDate) {
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rus = null;
		try {
			pst = con.prepareStatement("select * from emp_it_slab_access_details where fyear_start =? and fyear_end=?");
			pst.setDate(1, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				
			} else {
				java.util.Date preStartDate = getyear(uF, startDate, -1);
				java.util.Date PreEndDate = getyear(uF, endDate, -1);

				pst1 = con.prepareStatement("select * from emp_it_slab_access_details where fyear_start =? and fyear_end=?");
				pst1.setDate(1, (java.sql.Date) preStartDate);
				pst1.setDate(2, (java.sql.Date) PreEndDate);

				rus = pst1.executeQuery();
				while (rus.next()) {
					
					int emp_id = rus.getInt("emp_id");
					int slab_type = rus.getInt("slab_type");

					pst2 = con.prepareStatement("insert into emp_it_slab_access_details (emp_id,slab_type,fyear_start,fyear_end,added_by,entry_time) values(?,?,?,?, ?,?)");
					pst2.setInt(1, emp_id);
					pst2.setInt(2, slab_type);
					pst2.setDate(3, uF.getDateFormat(startDate, DATE_FORMAT));
					pst2.setDate(4, uF.getDateFormat(endDate, DATE_FORMAT));
					pst2.setInt(5, 1);
					pst2.setTimestamp(6, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
					pst2.executeUpdate();
					pst2.close();
				}
				rus.close();
				pst1.close();

				// System.out.println("deduction_Tax_Details inserted Successfull!!!!!");

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rus != null) {
				try {
					rus.close();
					rus = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
					pst1 = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}
//===end===
	
//===created parvez date: 20-09-2022===	
	//===start===
	private synchronized void extraWorkingLapsDays(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("CD/14425--extraWorkingLapsDays()");
			String currDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strFinancialDates = CF.getFinancialYear(con, currDate, CF, uF);

			Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
			if (hmOrgMap == null)
				hmOrgMap = new HashMap<String, Map<String, String>>();
			// Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			// if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String,
			// String>();

			Map<String, String> hmEmpJoiningDate = CF.getEmpJoiningDateMap(con, uF);
			if (hmEmpJoiningDate == null)
				hmEmpJoiningDate = new HashMap<String, String>();

			Iterator<String> it = hmOrgMap.keySet().iterator();
			while (it.hasNext()) {
				String strOrgId = (String) it.next();
				Map<String, String> hmOrg = hmOrgMap.get(strOrgId);
				

				pst = con.prepareStatement("select * from emp_leave_type where leave_type_id in (select leave_type_id from leave_type where is_compensatory = true and org_id=?) and org_id=?");
				pst.setInt(1, uF.parseToInt(strOrgId));
				pst.setInt(2, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				Map<String,String> hmLapsDays = new HashMap<String, String>();
				while (rs.next()) {
					hmLapsDays.put(rs.getString("level_id") + "_" + rs.getString("wlocation_id") + "_" + rs.getString("leave_type_id"), rs.getString("laps_days"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmLapsDays=="+hmLapsDays);

				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and is_alive = true and emp_per_id>0 and eod.org_id=?");
				pst.setInt(1, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				List<String> alEmp = new ArrayList<String>();
				while (rs.next()) {
					alEmp.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();

				int cnt = 0;
				for (int i = 0; alEmp != null && i < alEmp.size(); i++) {
					String strEmpId = alEmp.get(i);
					String nLevelId = CF.getEmpLevelId(con, strEmpId);
					String strWLocationId = "";
					
					pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.emp_id=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();
					String strEmpStatus = null;
					while (rs.next()) {
						strEmpStatus = rs.getString("emp_status");
						strWLocationId = rs.getString("wlocation_id");
					}
					rs.close();
					pst.close();
					
					ArrayList<String> paidLeaveBalanceDateList = new ArrayList<String>();
					pst = con.prepareStatement("select * from leave_application_register where emp_id=? and leave_type_id in (select compensate_with from " +
							" emp_leave_type elt,leave_type lt where elt.leave_type_id=lt.leave_type_id and elt.is_compensatory=true and elt.org_id=? " +
							"and elt.level_id=? and elt.wlocation_id=?) and (is_modify is null or is_modify=false)");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strOrgId));
					pst.setInt(3, uF.parseToInt(nLevelId));
					pst.setInt(4, uF.parseToInt(strWLocationId));
					rs = pst.executeQuery();
					while(rs.next()){
						paidLeaveBalanceDateList.add(rs.getString("extra_working_date"));
					}
					rs.close();
					pst.close();
					
					Map<String,String> extraWorkingLapsDate = new HashMap<String, String>();
					pst = con.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id in (select compensate_with from " +
							" emp_leave_type elt,leave_type lt where elt.leave_type_id=lt.leave_type_id and elt.is_compensatory=true and elt.org_id=? " +
							"and elt.level_id=? and elt.wlocation_id=?) and compensate_id>0");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strOrgId));
					pst.setInt(3, uF.parseToInt(nLevelId));
					pst.setInt(4, uF.parseToInt(strWLocationId));
					rs = pst.executeQuery();
					while(rs.next()){
						if(!paidLeaveBalanceDateList.contains(rs.getString("_date"))){
							Date lapsDate = uF.getFutureDate(uF.getDateFormatUtil(rs.getString("_date"), DBDATE), uF.parseToInt(hmLapsDays.get(nLevelId+"_"+strWLocationId+"_"+rs.getString("compensate_id"))));
							/*if(uF.parseToInt(strEmpId) == 59){
								System.out.println("lapsDate=="+lapsDate+"---currDate=="+uF.getDateFormatUtil(currDate, DATE_FORMAT));
								System.out.println("lapsDate222=="+uF.getDateFormatUtil(lapsDate+"", DBDATE));
							}*/
							if(uF.getDateFormatUtil(currDate, DATE_FORMAT) .equals(uF.getDateFormatUtil(lapsDate+"", DBDATE)) || uF.getDateFormatUtil(currDate, DATE_FORMAT) .after(uF.getDateFormatUtil(lapsDate+"", DBDATE))){
								extraWorkingLapsDate.put(rs.getString("register_id"),rs.getString("_date"));
							}
						}
						
					}
					rs.close();
					pst.close();
					
//					System.out.println("LaspsDate=="+extraWorkingLapsDate);
					
					Iterator<String> it1 = extraWorkingLapsDate.keySet().iterator();
					while(it1.hasNext()){
						String leaveRegisterId = it1.next();
						pst = con
								.prepareStatement("select * from leave_register1 where emp_id=? and register_id=?");
						pst.setInt(1, uF.parseToInt(alEmp.get(i)));
						pst.setInt(2, uF.parseToInt(leaveRegisterId));
						rs = pst.executeQuery();
						boolean flag = false;
						double accrudeBalance = 0;
						while (rs.next()) {
							flag = true;
							accrudeBalance = uF.parseToInt(rs.getString("update_balance"));
						}
						rs.close();
						pst.close();
		
						String strType = "C";
						if (flag) {
							strType = "A";
						}
						
						if(accrudeBalance>0){
							accrudeBalance = accrudeBalance-1;
						}
						
						//taken_paid=0, taken_unpaid=0,
						pst = con
								.prepareStatement("update leave_register1 set balance=0, accrued=? where emp_id = ? and register_id=?");
						pst.setDouble(1, accrudeBalance);
						pst.setInt(2, uF.parseToInt(alEmp.get(i)));
						pst.setInt(3, uF.parseToInt(leaveRegisterId));
						int x = pst.executeUpdate();
						pst.close();
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
					pst = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
	//===end===
	
}