package com.konnect.jpms.reports;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.roster.FillShift;
import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RosterOfEmployee extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
  
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	 
	String profileEmpId = null;
	String strAlphaValue = null;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(RosterReport.class);
	
	private String f_org;
	private String f_strWLocation;
	private String f_department;
	private String f_level;
	private String f_service;
	
	private String strMonth;
	private String strYear;
	private String strWLocation;
	
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	
	private List<FillMonth> monthList;
	private List<FillYears> yearList;
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	private List<FillShift> shiftList;
	
	private String calendarYear; 
	private List<FillCalendarYears> calendarYearList;
	  
	private String currUserType;
	private String exportType;
	
	private String strEmpId;
	private String strRosterId;
	private String strShiftChangeVal;
	private String strShiftChangeDate;
	private String strServiceId;
	private String strShiftId;
	private String operation;
	
	public String execute() throws Exception {  
		
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/	
		
		profileEmpId = (String)request.getParameter("profileEmpId");
		strAlphaValue = (String)request.getParameter("alphaValue");
		
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
//		System.out.println("getExportType() ===>> " + getExportType());
		if(getExportType() !=null && getExportType().trim().equalsIgnoreCase("EXCEL")) {
			generateRosterExcelReport(uF);
//			generateEmpDetailsExcelReport(uF);
			
			return null;
		}

		loadRoster(uF);
//		System.out.println("getOperation() ===>> " + getOperation());
		if(getOperation() !=null && getOperation().equals("SENDMAIL")) {
			sendMailToRosterAssignedUsers(uF);
			return "ajax";
		} else {
			viewRoster(uF);	
		}
		
		if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
			return LOAD;
		} else {
			return "ghrload";
		}
	}
	
	private void sendMailToRosterAssignedUsers(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con, CF, uF);
			if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
			
			String strMonth1 = ((uF.parseToInt(getStrMonth()) < 10) ? "0" + getStrMonth() : getStrMonth());
			String strTmpCY[] = getCalendarYear().split("-");
			String strYR = uF.getDateFormat(strTmpCY[0], DATE_FORMAT, "yyyy");
			String strSelectedMonthStrtDt = "01/" + strMonth1 +"/" + strYR;
			String strPaycycle[] = CF.getPayCycleFromDate(con, strSelectedMonthStrtDt, CF.getStrTimeZone(), CF, getF_org());
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from assign_shift_dates where paycycle_from_date=? and paycycle_to_date=? and paycycle_no=? and is_mail_sent=false");
			pst = con.prepareStatement(sbQuery.toString());
			
			pst.setDate(1, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPaycycle[2]));
//			System.out.println("pst1 ===>> " + pst);
			rs = pst.executeQuery();
			List<String> alEmpIds = new ArrayList<String>();
			while (rs.next()) {
				alEmpIds.add(rs.getString("emp_id"));
//				alEmpIds.add(rst.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			int cnt=0;
			
			for(int i=0; i<alEmpIds.size(); i++) {
				Notifications nF = new Notifications(N_NEW_ROSTER, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(alEmpIds.get(i));
				nF.setSupervisor(false);
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				pst = con.prepareStatement("update assign_shift_dates set is_mail_sent=true, mail_sent_date=? where emp_id=? " +
				"and paycycle_from_date=? and paycycle_to_date=? and paycycle_no=?");
				pst.setTimestamp(1, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone())+"", DBDATE+DBTIME));
				pst.setInt(2, uF.parseToInt(alEmpIds.get(i)));
				pst.setDate(3, uF.getDateFormat(strPaycycle[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPaycycle[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPaycycle[2]));
//				System.out.println("pst ===>> " + pst);
				pst.executeUpdate();
				pst.close();
				cnt++;
			}
			
			
			StringBuilder sbMsg = new StringBuilder();
			if(cnt == alEmpIds.size() && alEmpIds.size()>0) {
				sbMsg.append(SUCCESSM + "Mail sent successfully!" + END);
			} else {
				sbMsg.append(SUCCESSM + "Mail already sent!" + END);
			}
			request.setAttribute("STATUS_MSG", sbMsg.toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private void generateRosterExcelReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			if (getCalendarYear() != null && !getCalendarYear().trim().equals("") && !getCalendarYear().trim().equalsIgnoreCase("NULL")) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			if(getStrMonth() == null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			setStrYear(uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yyyy"))+"");
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			List<String> alDate = new ArrayList<String>();
			Map<String, String> hmDay = new HashMap<String, String>();
						
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			
			List<String> alWeekEnds = uF.getWeekEnds();
			int monthWeekEndCnt = 0;
			for(int ii=0; ii<maxDays; ii++) {
				String strD1 = uF.zero(cal.get(Calendar.DAY_OF_MONTH)) + "/" + uF.zero(cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR);
//				alDate.add(uF.getDateFormat(strD1, DATE_FORMAT, CF.getStrReportDayFormat()));
				alDate.add(strD1);
				hmDay.put(strD1, uF.getDateFormat(strD1, DATE_FORMAT, CF.getStrReportDayFormat()));
				
				String strDay = uF.getDateFormat(strD1, DATE_FORMAT, "E");
				strDay = strDay.toUpperCase();
				if(alWeekEnds.contains(strDay)) {
					monthWeekEndCnt++;
				}
				cal.add(Calendar.DATE, 1);
			}
			
			Map<String, String> hmHolidays = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			CF.getHolidayList(con,request, uF.getDateFormat((String)alDate.get(0), DATE_FORMAT, DATE_FORMAT), uF.getDateFormat((String)alDate.get(alDate.size()-1), DATE_FORMAT, DATE_FORMAT), CF, hmHolidayDates, hmHolidays, true);
			
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			String strRotationOfShift = "";
			while (rs.next()) {
				strRotationOfShift = rs.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rs.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rs.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rs.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rs.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rs.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rs.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rs.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rs.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rs.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rs.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rs.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rs.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rs.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rs.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rs.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rs.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rs.getString("remaining_emp_shift"));
			}
			rs.close();
			pst.close();
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			String remainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			
			List<String> alShiftIds = new ArrayList<String>();
		    alShiftIds.add(rotFirst);
		    alShiftIds.add(rotSecond);
		    alShiftIds.add(rotThird);
		    Collections.sort(alShiftIds);
		    
			String strTlRule = hmRosterPolicyRulesData.get("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER");
			String strTmpTlRule[] = strTlRule!=null ? strTlRule.split(":_:") : "".split("");
			String strTlCnt = null;
			String strTlLevel = null;
			List<String> alTlLevels = new ArrayList<String>();
			//===start parvez on 24-08-2021===
			if(strTlRule != null && !strTlRule.isEmpty() && !strTlRule.equalsIgnoreCase("null")) {
			//===end parvez on 24-08-2021===
				strTlCnt = strTmpTlRule[0];
				strTlLevel = strTmpTlRule[1];
				alTlLevels = Arrays.asList(strTlLevel.split(","));
			}
			
//			System.out.println("alDate=======>"+alDate);
//			System.out.println("hmDay=======>"+hmDay);
			Map<String, String> hmLevelIds = CF.getEmpLevelMap(con);
			
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, ((String)alDate.get(0)), ((String)alDate.get(alDate.size()-1)), hmLeaveDatesType, false, null);
			
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT  rd.emp_id,emp_fname, emp_mname, emp_lname, _date, shift_id,roster_weeklyoff_id FROM roster_details rd, " 
					+"employee_personal_details epd, employee_official_details eod WHERE eod.emp_id = epd.emp_per_id and eod.emp_id =rd.emp_id and epd.is_alive=true " 
					+" and  epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?))"
					+"and rd.emp_id = epd.emp_per_id and _date between ? and ? and rd.service_id>0 ");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
				sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			} else {
				if(uF.parseToInt(getF_level())>0) {
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+uF.parseToInt(getF_level())+")");
	            }
				
	            if(uF.parseToInt(getF_department())>0) {
	                sbQuery.append(" and depart_id ="+uF.parseToInt(getF_department()));
	            }
	            if(uF.parseToInt(getF_service())>0) {
	            	sbQuery.append(" and eod.service_id like '%,"+getF_service()+",%'");		                
	            } 
	            if(uF.parseToInt(getF_strWLocation())>0) {
	                sbQuery.append(" and wlocation_id ="+uF.parseToInt(getF_strWLocation()));
	            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
			}
			sbQuery.append(" order by emp_fname, rd.emp_id, _date desc, _from");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDate.get(alDate.size()-1), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDate.get(alDate.size()-1), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDate.get(0), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDate.get(alDate.size()-1), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(alDate.get(0), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(alDate.get(alDate.size()-1), DATE_FORMAT));
//			System.out.println("excel pst=======>"+pst);
			rs = pst.executeQuery();
			List<String> alEmpId = new ArrayList<String>();
			List<String> alTLEmpId = new ArrayList<String>();
			List<String> alTMemEmpId = new ArrayList<String>();
			Map<String, String> hmEmp = new LinkedHashMap<String, String>();
			List<String> alShiftId = new ArrayList<String>();
			Map<String,String> hmEmpShift = new HashMap<String, String>();
			while (rs.next()) {
				if(!alShiftId.contains(rs.getString("shift_id")) && uF.parseToInt(rs.getString("shift_id")) > 0){
					alShiftId.add(rs.getString("shift_id"));
				}
				String strEmpLvlId = hmLevelIds.get(rs.getString("emp_id"));
				if(!alTLEmpId.contains(rs.getString("emp_id")) && alTlLevels!=null && alTlLevels.contains(strEmpLvlId)) {
					alTLEmpId.add(rs.getString("emp_id"));
				} else {
					if (!alTMemEmpId.contains(rs.getString("emp_id")) && alTlLevels!=null && !alTlLevels.contains(strEmpLvlId)) {
						alTMemEmpId.add(rs.getString("emp_id"));
					}
				}
				if(!hmEmp.containsKey(rs.getString("emp_id"))) {
					//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
					hmEmp.put(rs.getString("emp_id"), strEmpName);
				}
				
				hmEmpShift.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_"+rs.getString("emp_id"), rs.getString("shift_id"));
			}
			rs.close();
			pst.close();
			alEmpId.addAll(alTLEmpId);
			alEmpId.addAll(alTMemEmpId);
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			
			List<List<List<String>>> reportData = new ArrayList<List<List<String>>>();
			List<List<String>> header = new ArrayList<List<String>>();
			if(alShiftId.size() > 0) {
				String strShiftIds = StringUtils.join(alShiftId.toArray(),",");
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				
				Map<String, Map<String, String>> hmShift = CF.getShiftTimeBaseOnShiftId(con);
				Map<String, String> hmShiftDetails = CF.getShiftMap(con);
				
				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, alDate.get(0), alDate.get(alDate.size()-1), CF, uF,hmWeekEndHalfDates,null);
				
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
				CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, alDate.get(0), alDate.get(alDate.size()-1), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEndDates, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
				 
				List<String> innHeader = new ArrayList<String>();
				innHeader .add("Name");
				innHeader .add("");
				header.add(innHeader);
				innHeader = new ArrayList<String>();
				innHeader .add("");
				innHeader .add("");
				header.add(innHeader);
				
				int nDateSize = alDate.size();
				for(int i = 0; i < nDateSize; i++) {
					innHeader = new ArrayList<String>();
					String strDate = alDate.get(i);
					String strDate1 = uF.getDateFormat(strDate, DATE_FORMAT, "dd-MM");
					String strDay = uF.getDateFormat(strDate, DATE_FORMAT, "E");
					strDay = strDay.toUpperCase();
					innHeader .add(uF.showData(strDate1, ""));
					if(strDay != null && alWeekEnds.contains(strDay)) {
						innHeader .add("#DCDCDC");
					} else {
						innHeader .add("#ffffff");
					}
					header.add(innHeader);
				}
				innHeader = new ArrayList<String>();
				innHeader .add("Leave");
				innHeader .add("#ffffff");
				header.add(innHeader);
				
				innHeader = new ArrayList<String>();
				innHeader .add("Total WD");
				innHeader .add("#ffffff");
				header.add(innHeader);
				
				for(int i=0; alShiftIds!=null && i<alShiftIds.size(); i++) {
					innHeader = new ArrayList<String>();
					innHeader .add(uF.showData(hmShiftDetails.get(alShiftIds.get(i)), "-"));
					innHeader .add("#ffffff");
					header.add(innHeader);
				}
				/*innHeader = new ArrayList<String>();
				innHeader .add(uF.showData(hmShiftDetails.get(rotFirst), ""));
				innHeader .add("#ffffff");
				header.add(innHeader);
				
				innHeader = new ArrayList<String>();
				innHeader .add(uF.showData(hmShiftDetails.get(rotSecond), ""));
				innHeader .add("#ffffff");
				header.add(innHeader);
				
				innHeader = new ArrayList<String>();
				innHeader .add(uF.showData(hmShiftDetails.get(rotThird), ""));
				innHeader .add("#ffffff");
				header.add(innHeader);*/
				
				innHeader = new ArrayList<String>();
				innHeader .add(hmShiftDetails.get(remainingEmpShift));
				innHeader .add("#ffffff");
				header.add(innHeader);
				
				innHeader = new ArrayList<String>();
				innHeader .add("WFH");
				innHeader .add("#ffffff");
				header.add(innHeader);
				
				Map<String, String> hmShiftEmpCntDaywise = new HashMap<String, String>();
                Map<String, String> hmShiftTLCntDaywise = new HashMap<String, String>();
                Map<String, String> hmShiftDayCntEmpwise = new HashMap<String, String>();
//				Iterator<String> it = hmEmp.keySet().iterator();
//				while(it.hasNext()) {
				for(int j=0; alEmpId!=null && j<alEmpId.size(); j++) {
					String strEmpId = alEmpId.get(j);
//					String strEmpId = it.next();
					String strEmpName = hmEmp.get(strEmpId);
					
					List<List<String>> innerReportList = new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(uF.showData(strEmpName, ""));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					String strTL = "";
            		if(hmEmpLevelId.get(strEmpId) !=null && alTlLevels !=null && alTlLevels.contains(hmEmpLevelId.get(strEmpId))) {
            			strTL = "L";
            		}
            		innerList = new ArrayList<String>();
					innerList.add(uF.showData(strTL, ""));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					Map hmLeaves = (Map)hmLeavesMap.get(strEmpId);
                	if(hmLeaves == null) hmLeaves = new HashMap();
                	/*int rotFirstCnt=0;
                	int rotSecondCnt=0;
                	int rotThirdCnt=0;
                	int rotRenainEmpShiftCnt=0;*/
                	int empShiftDaysCnt=0;
                	int leaveCnt=0;
                	int holidayCnt=0;
                	int rotWFHCnt=0;
					for(int i = 0; i < nDateSize; i++) {
						String strDate = alDate.get(i);
						innerList = new ArrayList<String>();
						
						String strWLocationId = hmEmpWlocation.get(strEmpId);
						Set<String> weeklyOffSet= hmWeekEndDates.get(strWLocationId);
						if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
						
						Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
						if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
						
						Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
						if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
						
						boolean flag = false;
						if(alEmpCheckRosterWeektype.contains(strEmpId)) {
							if(rosterWeeklyOffSet.contains(strDate)) {
								flag = true;
							}
						}/*else if(weeklyOffSet.contains(strDate)){
							flag = true;
						}else if(halfDayWeeklyOffSet.contains(strDate)){
							flag = true;
						}*/
						
						String strColour = (String)hmHolidayDates.get(uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
                    	if(strColour!=null) {
                    		holidayCnt++;
                    		strColour = null;
                    	}
                    	
						String strLeave = (String)hmLeaves.get(strDate);
						if(strLeave != null) {
							innerList.add("L");
							innerList.add("#DCDCDC");
							leaveCnt++;
                    	} else {
							if(flag) {
								innerList.add("0");
								innerList.add("#A2D7FF");
							} else {
								Map<String, String> hmShiftInner = hmShift.get(hmEmpShift.get(strDate+"_"+strEmpId));
								if(hmShiftInner == null) hmShiftInner = new HashMap<String, String>();
								String strShiftId = hmShiftInner.get("SHIFT_ID");
								innerList.add(uF.showData(hmShiftInner.get("SHIFT_NAME"), "-"));
								/*if(uF.parseToInt(rotFirst) == uF.parseToInt(strShiftId)) {
                            		rotFirstCnt++;
                            	} else if(uF.parseToInt(rotSecond) == uF.parseToInt(strShiftId)) {
                            		rotSecondCnt++;
                            	} else if(uF.parseToInt(rotThird) == uF.parseToInt(strShiftId)) {
                            		rotThirdCnt++;
                            	} else if(uF.parseToInt(hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT")) == uF.parseToInt(strShiftId)) {
                            		rotRenainEmpShiftCnt++;
                            	}*/
								innerList.add(uF.showData(hmShiftInner.get("SHIFT_COLOR"), "#ffffff"));
								
								if(uF.parseToInt(strShiftId)>0) {
                            		int shiftEmpCnt = uF.parseToInt(hmShiftEmpCntDaywise.get(strShiftId+"_"+strDate));
                            		shiftEmpCnt++;
                            		hmShiftEmpCntDaywise.put(strShiftId+"_"+strDate, ""+shiftEmpCnt);
                            		if(strTL !=null && strTL.equals("L")) {
                            			int shiftTLCnt = uF.parseToInt(hmShiftTLCntDaywise.get(strShiftId+"_"+strDate));
                            			shiftTLCnt++;
                                		hmShiftTLCntDaywise.put(strShiftId+"_"+strDate, ""+shiftTLCnt);
                            		}
                            		
                            		int shiftDayCnt = uF.parseToInt(hmShiftDayCntEmpwise.get(strShiftId+"_"+alEmpId.get(j)));
                            		shiftDayCnt++;
                            		hmShiftDayCntEmpwise.put(strShiftId+"_"+alEmpId.get(j), ""+shiftDayCnt);
                            		empShiftDaysCnt++;
                            	}
							}
                    	}
						innerReportList.add(innerList);
					}
					
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(""+leaveCnt, "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					int totWorkDays = (leaveCnt+empShiftDaysCnt); 
               		int totActualWorkDays = alDate.size() - (monthWeekEndCnt+holidayCnt);
               		if(totActualWorkDays < totWorkDays && leaveCnt>0) {
               			int diffWorkDays =  totWorkDays - totActualWorkDays;
               			if(leaveCnt>diffWorkDays) {
               				leaveCnt = leaveCnt - diffWorkDays;
               			} else {
               				leaveCnt = 0;
               			}
               		}
               		totActualWorkDays = (leaveCnt+empShiftDaysCnt);
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(totActualWorkDays+"", "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					for(int a=0; alShiftIds!=null && a<alShiftIds.size(); a++) {
						innerList = new ArrayList<String>();
						innerList.add(uF.showData(hmShiftDayCntEmpwise.get(alShiftIds.get(a)+"_"+alEmpId.get(j)), "0"));
						innerList.add("#ffffff");
						innerReportList.add(innerList);
					}
					/*innerList = new ArrayList<String>();
					innerList.add(uF.showData(rotFirstCnt+"", ""));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(rotSecondCnt+"", ""));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(rotThirdCnt+"", ""));
					innerList.add("#ffffff");
					innerReportList.add(innerList);*/
					
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftDayCntEmpwise.get(remainingEmpShift+"_"+alEmpId.get(j)), "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					innerList = new ArrayList<String>();
					innerList.add(uF.showData("", ""));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					reportData.add(innerReportList);
				}
				
				
				
//				***************** 1 L **************************
				for(int a=0; alShiftIds!=null && a<alShiftIds.size(); a++) {
					List<List<String>> innerReportList = new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftDetails.get(alShiftIds.get(a)), "-")+"(L)");
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					innerList = new ArrayList<String>();
					innerList.add("");
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					int shiftTLCnt=0;
					for(int i = 0; i < nDateSize; i++) {
						String strDate = alDate.get(i);
						shiftTLCnt += uF.parseToInt(hmShiftTLCntDaywise.get(alShiftIds.get(a)+"_"+strDate));
						innerList = new ArrayList<String>();
						innerList.add(uF.showData(hmShiftTLCntDaywise.get(alShiftIds.get(a)+"_"+strDate), "0"));
						innerList.add("#ffffff");
						innerReportList.add(innerList);
					}
					innerList = new ArrayList<String>();
					innerList.add(shiftTLCnt+"");
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					reportData.add(innerReportList);
				}
				
				
				/*List<List<String>> innerReportList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(hmShiftDetails.get(rotFirst), "")+"(L)");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				innerList = new ArrayList<String>();
				innerList.add("");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				int firstShiftTLCnt=0;
				for(int i = 0; i < nDateSize; i++) {
					String strDate = alDate.get(i);
					firstShiftTLCnt += uF.parseToInt(hmShiftTLCntDaywise.get(rotFirst+"_"+strDate));
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftTLCntDaywise.get(rotFirst+"_"+strDate), "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
				}
				innerList = new ArrayList<String>();
				innerList.add(firstShiftTLCnt+"");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				reportData.add(innerReportList);
				
//				***************** 2 L **************************
				innerReportList = new ArrayList<List<String>>();
				innerList = new ArrayList<String>();
				innerList.add(uF.showData(hmShiftDetails.get(rotSecond), "")+"(L)");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				innerList = new ArrayList<String>();
				innerList.add("");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				int secShiftTLCnt=0;
				for(int i = 0; i < nDateSize; i++) {
					String strDate = alDate.get(i);
					secShiftTLCnt += uF.parseToInt(hmShiftTLCntDaywise.get(rotSecond+"_"+strDate));
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftTLCntDaywise.get(rotSecond+"_"+strDate), "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
				}
				innerList = new ArrayList<String>();
				innerList.add(secShiftTLCnt+"");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				reportData.add(innerReportList);
				
//				***************** 3 L **************************
				innerReportList = new ArrayList<List<String>>();
				innerList = new ArrayList<String>();
				innerList.add(uF.showData(hmShiftDetails.get(rotThird), "")+"(L)");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				innerList = new ArrayList<String>();
				innerList.add("");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				int thirdShiftTLCnt=0;
				for(int i = 0; i < nDateSize; i++) {
					String strDate = alDate.get(i);
					thirdShiftTLCnt += uF.parseToInt(hmShiftTLCntDaywise.get(rotThird+"_"+strDate));
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftTLCntDaywise.get(rotThird+"_"+strDate), "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
				}
				innerList = new ArrayList<String>();
				innerList.add(thirdShiftTLCnt+"");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				reportData.add(innerReportList);*/
				
//				***************** 1 **************************
				for(int a=0; alShiftIds!=null && a<alShiftIds.size(); a++) {
					List<List<String>> innerReportList = new ArrayList<List<String>>();
					List<String> innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftDetails.get(alShiftIds.get(a)), "-"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					innerList = new ArrayList<String>();
					innerList.add("");
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					
					int shiftEmpCnt=0;
					for(int i = 0; i < nDateSize; i++) {
						String strDate = alDate.get(i);
						shiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(alShiftIds.get(a)+"_"+strDate));
						innerList = new ArrayList<String>();
						innerList.add(uF.showData(hmShiftEmpCntDaywise.get(alShiftIds.get(a)+"_"+strDate), "0"));
						innerList.add("#ffffff");
						innerReportList.add(innerList);
					}
					innerList = new ArrayList<String>();
					innerList.add(shiftEmpCnt+"");
					innerList.add("#ffffff");
					innerReportList.add(innerList);
					reportData.add(innerReportList);
				}
				
				
				/*innerReportList = new ArrayList<List<String>>();
				innerList = new ArrayList<String>();
				innerList.add(uF.showData(hmShiftDetails.get(rotFirst), ""));
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				innerList = new ArrayList<String>();
				innerList.add("");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				int firstShiftEmpCnt=0;
				for(int i = 0; i < nDateSize; i++) {
					String strDate = alDate.get(i);
					firstShiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(rotFirst+"_"+strDate));
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftEmpCntDaywise.get(rotFirst+"_"+strDate), "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
				}
				innerList = new ArrayList<String>();
				innerList.add(firstShiftEmpCnt+"");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				reportData.add(innerReportList);
				
//				***************** 2 **************************
				innerReportList = new ArrayList<List<String>>();
				innerList = new ArrayList<String>();
				innerList.add(uF.showData(hmShiftDetails.get(rotSecond), ""));
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				innerList = new ArrayList<String>();
				innerList.add("");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				int secShiftEmpCnt=0;
				for(int i = 0; i < nDateSize; i++) {
					String strDate = alDate.get(i);
					secShiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(rotSecond+"_"+strDate));
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftEmpCntDaywise.get(rotSecond+"_"+strDate), "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
				}
				innerList = new ArrayList<String>();
				innerList.add(secShiftEmpCnt+"");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				reportData.add(innerReportList);
				
//				***************** 3 **************************
				innerReportList = new ArrayList<List<String>>();
				innerList = new ArrayList<String>();
				innerList.add(uF.showData(hmShiftDetails.get(rotThird), ""));
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				innerList = new ArrayList<String>();
				innerList.add("");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				int thirdShiftEmpCnt=0;
				for(int i = 0; i < nDateSize; i++) {
					String strDate = alDate.get(i);
					thirdShiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(rotThird+"_"+strDate));
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftEmpCntDaywise.get(rotThird+"_"+strDate), "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
				}
				innerList = new ArrayList<String>();
				innerList.add(thirdShiftEmpCnt+"");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				reportData.add(innerReportList);*/
				
//				***************** 4 **************************
				List<List<String>> innerReportList = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.showData(hmShiftDetails.get(remainingEmpShift), ""));
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				innerList = new ArrayList<String>();
				innerList.add("");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				
				int remainingShiftEmpCnt=0;
				for(int i = 0; i < nDateSize; i++) {
					String strDate = alDate.get(i);
					remainingShiftEmpCnt += uF.parseToInt(hmShiftEmpCntDaywise.get(remainingEmpShift+"_"+strDate));
					innerList = new ArrayList<String>();
					innerList.add(uF.showData(hmShiftEmpCntDaywise.get(remainingEmpShift+"_"+strDate), "0"));
					innerList.add("#ffffff");
					innerReportList.add(innerList);
				}
				innerList = new ArrayList<String>();
				innerList.add(remainingShiftEmpCnt+"");
				innerList.add("#ffffff");
				innerReportList.add(innerList);
				reportData.add(innerReportList);
				
				
			}		
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Roster");	
			
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
//			sheetDesign.generateRosterWithRulesExcelSheet(workbook,sheet,header,reportData);
			sheetDesign.createExcelMultiplicationTable(workbook, sheet, header, reportData); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=Roster_"+alDate.get(0)+"_"+alDate.get(alDate.size()-1)+".xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void generateEmpDetailsExcelReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmBranchData = CF.getBankMap(con, uF);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT  emp_fname, emp_mname, emp_lname, emptype,payment_mode,emp_bank_acct_nbr,emp_bank_name FROM employee_personal_details epd, employee_official_details eod " +
				"WHERE eod.emp_id = epd.emp_per_id and epd.is_alive=true");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			List<List<String>> alEmpData = new ArrayList<List<String>>();
			int cnt=0;
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				cnt++; 
				innerList.add(""+cnt); 
				innerList.add(rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				innerList.add(uF.showData(uF.stringMapping(rs.getString("emptype")), ""));
				innerList.add(uF.showData(uF.getPaymentMode(rs.getString("payment_mode")), ""));
				innerList.add(uF.showData(rs.getString("emp_bank_acct_nbr"), ""));
				Map<String, String> hmInner = hmBranchData.get(rs.getString("emp_bank_name"));
				if(hmInner==null) hmInner = new HashMap<String, String>();
				innerList.add(uF.showData(hmInner.get("BANK_BRANCH"), ""));
				alEmpData.add(innerList);
			}
			rs.close();
			pst.close();
			
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Emp Data");	
			
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
//			sheetDesign.generateRosterWithRulesExcelSheet(workbook,sheet,header,reportData);
			sheetDesign.createExcelEmpBankDetailsTable(workbook, sheet, alEmpData); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=Emp_Bank_details.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadRoster(UtilityFunctions uF) {
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());

		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		shiftList  = new FillShift(request).fillShiftNames(uF.parseToInt(getF_org()));
		return LOAD;
	}

	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
			if((strUserType != null && !strUserType.equals(MANAGER)) || (getCurrUserType() != null && getCurrUserType().equals(strBaseUserType))) {
				alFilter.add("ORGANISATION");
	//			System.out.println("getF_org() ===>> " + getF_org());
				if(getF_org()!=null) {
					String strOrg="";
					for(int i=0;organisationList!=null && i<organisationList.size();i++) {
	//					System.out.println("getF_org() 1===>> " + getF_org());
						if(getF_org().equals(organisationList.get(i).getOrgId())) {
	//						System.out.println("getF_org() 2===>> " + getF_org());
							strOrg=organisationList.get(i).getOrgName();
						}
					}
	//				System.out.println("strOrg ===>> " + strOrg);
					if(strOrg!=null && !strOrg.equals("")) {
						hmFilter.put("ORGANISATION", strOrg);
					} else {
						hmFilter.put("ORGANISATION", "All Organisations");
					}
				} else {
					hmFilter.put("ORGANISATION", "All Organisations");
				}
				
				alFilter.add("LOCATION");
				if(getF_strWLocation()!=null) {
					String strLocation="";
					for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
						if(getF_strWLocation().equals(wLocationList.get(i).getwLocationId())) {
							strLocation=wLocationList.get(i).getwLocationName();
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
				
				alFilter.add("DEPARTMENT");
				if(getF_department()!=null) {
					String strDepart="";
					for(int i=0;departmentList!=null && i<departmentList.size();i++) {
						if(getF_department().equals(departmentList.get(i).getDeptId())) {
							strDepart=departmentList.get(i).getDeptName();
						}
					}
					if(strDepart!=null && !strDepart.equals("")) {
						hmFilter.put("DEPARTMENT", strDepart);
					} else {
						hmFilter.put("DEPARTMENT", "All Departments");
					}
				} else {
					hmFilter.put("DEPARTMENT", "All Departments");
				}
				
				alFilter.add("SERVICE");
				if(getF_service()!=null) {
					String strService="";
					for(int i=0;serviceList!=null && i<serviceList.size();i++) {
						if(getF_service().equals(serviceList.get(i).getServiceId())) {
							strService=serviceList.get(i).getServiceName();
						}
					}
					if(strService!=null && !strService.equals("")) {
						hmFilter.put("SERVICE", strService);
					} else {
						hmFilter.put("SERVICE", "All Services");
					}
				} else {
					hmFilter.put("SERVICE", "All Services");
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
			}
			
			alFilter.add("CALENDARYEAR");
			if(getCalendarYear()!=null) {
				String strCal = "";
				for(int i=0;calendarYearList!=null && i<calendarYearList.size();i++) {
					if(getCalendarYear().equals(calendarYearList.get(i).getCalendarYearId())) {
						strCal = calendarYearList.get(i).getCalendarYearName();
					}
				}
				if(strCal!=null && !strCal.equals("")) {
					hmFilter.put("CALENDARYEAR", strCal);
				} else {
					hmFilter.put("CALENDARYEAR", "-");
				}
			} else {
				hmFilter.put("CALENDARYEAR", "-");
			}
			
			alFilter.add("MONTH");
			if(getStrMonth()!=null) {
				String strMonth = "";
				for(int i=0;monthList!=null && i<monthList.size();i++) {
					if(getStrMonth().equals(monthList.get(i).getMonthId())) {
						strMonth = monthList.get(i).getMonthName();
					}
				}
				if(strMonth!=null && !strMonth.equals("")) {
					hmFilter.put("MONTH", strMonth);
				} else {
					hmFilter.put("MONTH", "-");
				}
			} else {
				hmFilter.put("MONTH", "-");
			}
		} else {
			
			alFilter.add("CALENDARYEAR");
			if(getCalendarYear()!=null) {
				String strCal = "";
				for(int i=0;calendarYearList!=null && i<calendarYearList.size();i++) {
					if(getCalendarYear().equals(calendarYearList.get(i).getCalendarYearId())) {
						strCal = calendarYearList.get(i).getCalendarYearName();
					}
				}
				if(strCal!=null && !strCal.equals("")) {
					hmFilter.put("CALENDARYEAR", strCal);
				} else {
					hmFilter.put("CALENDARYEAR", "-");
				}
			} else {
				hmFilter.put("CALENDARYEAR", "-");
			}
			
			alFilter.add("MONTH");
			if(getStrMonth()!=null) {
				String strMonth = "";
				for(int i=0;monthList!=null && i<monthList.size();i++) {
					if(getStrMonth().trim().equals(""+monthList.get(i).getMonthId())) {
						strMonth = monthList.get(i).getMonthName();
					}
				}
				if(strMonth!=null && !strMonth.equals("")) {
					hmFilter.put("MONTH", strMonth);
				} else {
					hmFilter.put("MONTH", "-");
				}
			} else {
				hmFilter.put("MONTH", "-");
			}
		}
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String viewRoster(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			if (getCalendarYear() != null && !getCalendarYear().trim().equals("") && !getCalendarYear().trim().equalsIgnoreCase("NULL")) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			if(getStrMonth() == null) {
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "MM"));
			Calendar cal2 = GregorianCalendar.getInstance();
			cal2.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth) {
				cal2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "yyyy")));
			} else {
				cal2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, "yyyy")));
			}
			int nMonthStart = cal2.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal2.getActualMaximum(Calendar.DATE);
			
			String strDate1 =  nMonthStart+"/"+getStrMonth()+"/"+cal2.get(Calendar.YEAR);
			String strDate2 =  nMonthEnd+"/"+getStrMonth()+"/"+cal2.get(Calendar.YEAR);
			setPaycycle(strDate1 + "-" + strDate2);
			
			setStrYear(uF.parseToInt(uF.getDateFormat(strDate2, DATE_FORMAT, "yyyy"))+"");
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, Map<String, String>> hmServicesWorkrdFor = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmShiftId = new HashMap<String, Map<String, String>>();
			Map<String, String> hmServices = CF.getServicesMap(con, true);
			Map<String, String> hmWLocation = CF.getEmpWlocationMap(con);
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alDay = new ArrayList<String>();
			List<String> alDate = new ArrayList<String>();
			List<String> alEmpId = new ArrayList<String>();
			List<String> alTLEmpId = new ArrayList<String>();
			List<String> alTMemEmpId = new ArrayList<String>();

			List _alDay = new ArrayList();
			List _alDate = new ArrayList();

			String[] strDates = getPaycycle().split("-");
						
			
//			Map<String, String> hmWeekEnds = CF.getWeekEndList();
//			Map<String, String> hmWeekEnds = CF.getWeekEndDateList(con, strDates[0], strDates[1], CF, uF);
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strDates[0], strDates[1], CF, uF,null,null);
		
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap==null)hmEmpLevelMap = new HashMap<String,String>();	
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndList = CF.getWeekEndDateList(con, strDates[0], strDates[1], CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strDates[0], strDates[1], alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndList,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			
			/*pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			int i=0;
			while(rs.next()) {
				
				_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));
				_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				
				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))) {
					_alHolidays.add(i + "");
					_hmHolidaysColour.put(i + "", (String) hmHolidayDates.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)));
				}
				i++;
			}
			
			pst.close();*/
	
			
			String strStartDate = "01/"+(uF.parseToInt(getStrMonth())<10 ? "0"+getStrMonth() : getStrMonth())+"/"+getStrYear();
			Date startdate = uF.getDateFormat(strStartDate, DATE_FORMAT);
		    Calendar cal=Calendar.getInstance();
			cal.setTime(startdate);
			
//			System.out.println("getStrMonth() ===>> " + getStrMonth());
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1);
//			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
//			System.out.println("Date-time ===>> " + cal.getTime()+" === Calendar.DATE ===>> " + Calendar.DATE);
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			
			
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
//			System.out.println("maxDays ===>> " + maxDays);
			String strD1 = null;
			for(int ii=0; ii<maxDays; ii++) {
				
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				
				_alDay.add(uF.getDateFormat(strD1, DATE_FORMAT, CF.getStrReportDayFormat()));
				_alDate.add(uF.getDateFormat(strD1, DATE_FORMAT, CF.getStrReportDateFormat()));

				cal.add(Calendar.DATE, 1);
			}
			
			Map<String, String> hmHolidays = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			CF.getHolidayList(con,request, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), CF, hmHolidayDates, hmHolidays, true);
			
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
//			Map hmLeavesMap = CF.getLeaveDates(con, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), CF, hmLeaveDatesType, false, null);
			Map hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), hmLeaveDatesType, false, null);
			Map hmLeavesColour = new HashMap();
			Map hmLeavesName = new HashMap();
			CF.getLeavesAttributes(con, uF, hmLeavesColour, hmLeavesName);
			
			List<String> alWeekEnds = uF.getWeekEnds();
			int monthWeekEndCnt = 0;
			for(int ii=0; ii<_alDate.size(); ii++) {
				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat((String)_alDate.get(ii), CF.getStrReportDateFormat(), DATE_FORMAT))) {
					_alHolidays.add(ii + "");
					_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat((String)_alDate.get(ii), CF.getStrReportDateFormat(), DATE_FORMAT)));
				}
				String strDay = uF.getDateFormat((String)_alDate.get(ii), CF.getStrReportDateFormat(), "E");
				strDay = strDay.toUpperCase();
				if(alWeekEnds.contains(strDay)) {
					monthWeekEndCnt++;
				}
			}
			request.setAttribute("monthWeekEndCnt", monthWeekEndCnt+"");
			
			Map<String, String> hmAssignShiftDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select max(entry_date) as entry_date from roster_details where _date between ? and ?");
			pst.setDate(1, uF.getDateFormat(strDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmAssignShiftDetails.put("LAST_UPDATED_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT_STR));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from assign_shift_dates where paycycle_from_date=? and paycycle_to_date=?");
			pst.setDate(1, uF.getDateFormat(strDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmAssignShiftDetails.put("ALGO_RUN_DATE_TIME", uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT));
				hmAssignShiftDetails.put("APPROVED_STATUS", rs.getString("is_mail_sent"));
				hmAssignShiftDetails.put("APPROVED_DATE_TIME", uF.getDateFormat(rs.getString("mail_sent_date"), DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmAssignShiftDetails", hmAssignShiftDetails);
			
			Map<String, String> hmShiftColor = new HashMap<String, String>();
			Map<String, String> hmShiftDetails = CF.getShiftMap(con);
			Map<String, String> hmShiftName = CF.getShiftNameMap(con);
			pst = con.prepareStatement("SELECT * FROM shift_details order by shift_id");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmShiftColor.put(rs.getString("shift_id"), rs.getString("colour_code"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmShiftColor", hmShiftColor);
			request.setAttribute("hmShiftDetails", hmShiftDetails);
			request.setAttribute("hmShiftName", hmShiftName);
			
			Map<String, String> hmRosterPolicyRulesData = new HashMap<String, String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_policy_rules order by roster_policy_rule_id limit 1");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			String strRotationOfShift = "";
			while (rs.next()) {
				strRotationOfShift = rs.getString("rotation_of_shift");
				hmRosterPolicyRulesData.put("RULE_POLICY_RULE_ID", rs.getString("roster_policy_rule_id"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rs.getString("rule_type_id"));
				hmRosterPolicyRulesData.put("SHIFT_ID", rs.getString("shift_id"));
				hmRosterPolicyRulesData.put("SHIFT_IDS", rs.getString("shift_ids"));
				hmRosterPolicyRulesData.put("RULE_TYPE_ID", rs.getString("no_of_days"));
				hmRosterPolicyRulesData.put("GENDER", rs.getString("gender"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT", rs.getString("min_no_of_member_in_shift"));
				hmRosterPolicyRulesData.put("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND", rs.getString("min_no_of_member_in_shift_at_weekend"));
				hmRosterPolicyRulesData.put("NO_OF_TL_FROM_LEVELS_FOR_NO_OF_MEMBER", rs.getString("no_of_leads_from_levels_for_no_of_member"));
				hmRosterPolicyRulesData.put("MIN_WEEKEND_OFF_IN_MONTH", rs.getString("min_weekend_off_per_month"));
				hmRosterPolicyRulesData.put("MAX_NO_OF_SHIFTS_PER_MEMBER_PER_MONTH", rs.getString("max_no_of_shifts_per_member_per_month"));
				hmRosterPolicyRulesData.put("NO_OF_DAYS_OFF_BETWEEN_SHIFTS", rs.getString("min_days_off_between_shifts"));
				hmRosterPolicyRulesData.put("MEMBER_LOCATION_ASSOCIATED_LOCATIONS", rs.getString("member_location_associated_locations"));
				hmRosterPolicyRulesData.put("MIN_MALE_MEMBER_IN_SHIFT", rs.getString("min_male_member_in_shift"));
				hmRosterPolicyRulesData.put("MIN_BREAK_DAYS_IN_STRETCH_SHIFT", rs.getString("min_break_days_in_stretch_shift"));
				hmRosterPolicyRulesData.put("ROTATION_OF_SHIFT", rs.getString("rotation_of_shift"));
				hmRosterPolicyRulesData.put("REMAINING_EMP_SHIFT", rs.getString("remaining_emp_shift"));
			}
			rs.close();
			pst.close();
			String[] strRotOfShift = strRotationOfShift.split(":_:");
			String rotFirst = "";
			String rotSecond = "";
			String rotThird = "";
			
			if(strRotOfShift.length>2) {
				rotFirst = strRotOfShift[0];
				rotSecond = strRotOfShift[1];
				rotThird = strRotOfShift[2];
			}
			request.setAttribute("rotFirst", rotFirst);
			request.setAttribute("rotSecond", rotSecond);
			request.setAttribute("rotThird", rotThird);
			request.setAttribute("remainingEmpShift", hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT"));
			
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
			request.setAttribute("alTlLevels", alTlLevels);
			request.setAttribute("shiftList", shiftList);
			
			
			Map hm = new HashMap();
			Map<String, String> hm1 = new HashMap<String, String>();
			Map<String, String> hm2 = new HashMap<String, String>();
			Map<String, String> hm3 = new HashMap<String, String>();

			if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
//				pst = con.prepareStatement(selectRosterDetailsV);
				pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.joining_date<=? and epd.is_alive=true and "
					+" (employment_end_date is null or (employment_end_date >=? or employment_end_date between ? and ?)) and _date between ? and ? and rd.emp_id " +
					"in (select emp_id from assign_shift_dates where paycycle_from_date=? and paycycle_to_date=? and is_mail_sent=true) order by emp_fname desc"); // AND rd.emp_id=? 
				pst.setDate(1, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strDates[1], DATE_FORMAT));
//				pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID))); 
				pst.setDate(5, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strDates[1], DATE_FORMAT));
				
//			System.out.println("1 pst====>"+pst); 
			
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER) ) && strAlphaValue!=null) {
				pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.is_alive=true "
						+" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) "
						+" and _date between ? and ? and emp_fname like ? order by emp_fname, _date desc");
				pst.setDate(1, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setString(7, strAlphaValue+"%");
//				System.out.println("2 pst====>"+pst); 
			} else if (strUserType != null && ((strUserType.equalsIgnoreCase(MANAGER) && getCurrUserType() != null && getCurrUserType().equals(strBaseUserType)) || strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER) )) {
				if(uF.parseToInt(profileEmpId) > 0) {
					pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.is_alive=true "
							+"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?))"
							+" AND rd.emp_id=? and _date between ? and ? order by emp_fname");
					pst.setDate(1, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(3, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(4, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setInt(5, uF.parseToInt(profileEmpId)); 
					pst.setDate(6, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(7, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
//					System.out.println("3 pst====>"+pst); 
				} else {
					sbQuery = new StringBuilder();
					sbQuery.append("SELECT emp_per_id, rd.emp_id, rd.service_id, roster_id, _date, _from, _to, emp_fname, emp_mname, emp_lname, rd.shift_id FROM roster_details rd, employee_personal_details epd, " +
						"employee_official_details eod WHERE eod.emp_id = epd.emp_per_id and eod.emp_id =rd.emp_id and rd.emp_id = epd.emp_per_id and epd.is_alive=true and _date between ? and ? and rd.service_id>0 "
						+" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?))");
					
					if(uF.parseToInt(getF_level())>0) {
		                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+uF.parseToInt(getF_level())+")");
		            }
		            if(uF.parseToInt(getF_department())>0) {
		                sbQuery.append(" and depart_id ="+uF.parseToInt(getF_department()));
		            }
		            if(uF.parseToInt(getF_service())>0) {
		            	sbQuery.append(" and eod.service_id like '%,"+getF_service()+",%'");		                
		            } 
		            if(uF.parseToInt(getF_strWLocation())>0) {
		                sbQuery.append(" and wlocation_id ="+uF.parseToInt(getF_strWLocation()));
		            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
						sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					}
		            if(uF.parseToInt(getF_org())>0) {
						sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
					} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					}
					sbQuery.append(" order by emp_fname, rd.emp_id, _date desc, _from");
//					pst = con.prepareStatement(selectRosterDetails);
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(3, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(4, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(5, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(6, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
//					System.out.println("4 pst====>"+pst); 
				}
				
			} else if (strUserType != null && strUserType.equalsIgnoreCase(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
				
				if(uF.parseToInt(profileEmpId) > 0) {
					pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.is_alive=true "
							+" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) "
							+" AND rd.emp_id=? and _date between ? and ? order by emp_fname");
					pst.setDate(1, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strDates[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(profileEmpId)); 
					pst.setDate(6, uF.getDateFormat(strDates[0], DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(strDates[1], DATE_FORMAT));
//					System.out.println("5 pst====>"+pst); 
				} else {
					pst = con.prepareStatement("select * from (SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.is_alive=true " 
							+" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) "
							+ " and _date between ? and ? and emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) order by emp_id, _date desc) e order by emp_fname");
					pst.setDate(1, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strDates[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strDates[0], DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt((String) session.getAttribute("EMPID")));
//					System.out.println("6 pst====>"+pst); 
				}
			} else {
				return ACCESS_DENIED;
			}
//			System.out.println("pst====>"+pst); 
			
			rs = pst.executeQuery();
			StringBuilder sb = new StringBuilder();
			List alServices = new ArrayList();
			Map<String, String> hmLevelIds = CF.getEmpLevelMap(con);
			while (rs.next()) {
					
				hm3 = (HashMap) hm.get(rs.getString("emp_per_id")+"_"+rs.getString("service_id"));
				if(hm3==null) {
					hm3  = new HashMap();
				}
				String strEmpLvlId = hmLevelIds.get(rs.getString("emp_per_id"));
				if(!alTLEmpId.contains(rs.getString("emp_per_id")) && alTlLevels!=null && alTlLevels.contains(strEmpLvlId)) {
					alTLEmpId.add(rs.getString("emp_per_id"));
				} else {
					if (!alTMemEmpId.contains(rs.getString("emp_per_id")) && alTlLevels!=null && !alTlLevels.contains(strEmpLvlId)) {
						alTMemEmpId.add(rs.getString("emp_per_id"));
					}
				}
				if (hm1 == null) {
					hm1 = new HashMap();
					hm2 = new HashMap();
				}

				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				hm1.put("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hm1.put("EMP_LEVELID", hmLevelIds.get(rs.getString("emp_per_id")));
				hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "SHIFT_ID", rs.getString("shift_id"));
				hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "ROSTER_ID", rs.getString("roster_id"));
				
				hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM", uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO", uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));

				hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), (String) hmServices.get(rs.getString("service_id")));
				hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+rs.getString("service_id")+"ROSTER_ID", rs.getString("roster_id"));

				hmServicesWorkrdFor.put(rs.getString("emp_per_id"), hm2);
				
				hm3.put("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				hm3.put("EMP_LEVELID", hmLevelIds.get(rs.getString("emp_per_id")));
				hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "SHIFT_ID", rs.getString("shift_id"));
				hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "ROSTER_ID", rs.getString("roster_id"));
				
				hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM", uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO", uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				
				hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), (String) hmServices.get(rs.getString("service_id")));
				hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+rs.getString("service_id")+"ROSTER_ID", rs.getString("roster_id"));

				hm.put(rs.getString("emp_per_id")+"_"+rs.getString("service_id"), hm3);
				
				alServices = (List)hm.get(rs.getString("emp_per_id"));
				if(alServices==null) {
					alServices = new ArrayList();
				}
				
				if(!alServices.contains(rs.getString("service_id")) && hmServices.containsKey(rs.getString("service_id"))) {
					alServices.add(rs.getString("service_id"));
				}
				hm.put(rs.getString("emp_per_id"), alServices);
//				System.out.println("hm ===>> " + hm);
				
				sb.append(""+			
				"<div id=\"popup_name"+rs.getString("roster_id")+"\" class=\"popup_block posfix\">" +
				"<a href=\"javascript:void(0)\" onclick=\"hideBlock('popup_name"+rs.getString("roster_id")+"');\" class=\"close\"><img src=\""+request.getContextPath()+"/images/close_pop.png\" class=\"btn_close\" title=\"Close Window\" alt=\"Close\" /></a>"+
						
				"<h2 class=\"alignCenter\">Roster of "+rs.getString("emp_fname")+strEmpMName+" " + rs.getString("emp_lname")+" for "+uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"</h2><br/>"+
				"<form action=\"UpdateRosterReport.action\">"+
				"<input type=\"hidden\" name=\"f_org\" value=\""+ getF_org() +"\">"+
				"<input type=\"hidden\" name=\"f_strWLocation\" value=\""+ getF_strWLocation() +"\">"+
				"<input type=\"hidden\" name=\"f_department\" value=\""+ getF_department() +"\">"+
				"<input type=\"hidden\" name=\"f_service\" value=\""+ getF_service() +"\">"+
				"<input type=\"hidden\" name=\"f_level\" value=\""+ getF_level() +"\">"+
				"<input type=\"hidden\" name=\"strMonth\" value=\""+ getStrMonth() +"\">"+
				"<input type=\"hidden\" name=\"calendarYear\" value=\""+ getCalendarYear() +"\">"+
				"<table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"250px\">"+				
				"<tr>"+
				"<td class=\"reportHeading\">Start Time</td><td class=\"reportHeading\">End Time</td>"+
				"</tr><tr>"+
				"<td class=\"reportLabel\"><input style=\"width:100px\" name=\"_from\" type=\"text\" value=\""+uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()),"")+"\" ></td><td class=\"reportLabel\"><input style=\"width:100px\" name=\"_to\" type=\"text\" value=\""+uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()),"")+"\" ></td>"+
				"</tr><tr>"+
				"<td height=\"60px\"  class=\"reportLabel\" align=\"center\" colspan=\"2\"><input name=\"UPD\" type=\"submit\" value=\"Update Roster\" class=\"input_button\">&nbsp;" +
				"<input onclick=\"return confirm('Are you sure you want to delete this roster entry of "+rs.getString("emp_fname")+strEmpMName+" " + rs.getString("emp_lname")+" for "+uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"?')\" type=\"submit\" name=\"DEL\" value=\"Delete Roster\" class=\"input_button\">"+
				"<input name=\"roster_id\" type=\"hidden\" value=\""+rs.getString("roster_id")+"\" ></td>"+
				"</tr>"+
				"</table>"+
				"</form></div>");
			}
			rs.close();
			pst.close();

//			System.out.println("hm ===>> " + hm);
			
			alEmpId.addAll(alTLEmpId);
			alEmpId.addAll(alTMemEmpId);
			
			System.out.println();
			Map hmRosterServiceName = new HashMap();
			Map hmRosterServiceId = new HashMap();
			List<String> alServiceId = new ArrayList<String>();
			
//			new CommonFunctions(CF).getRosterServicesIDList(strDates[0], strDates[1], hmRosterServiceName, hmRosterServiceId, alServiceId);
			CF.getRosterServicesIDList(con,CF,strDates[0], strDates[1], hmRosterServiceName, hmRosterServiceId, null);
//			hmRosterServiceName = new HashMap();
			hmRosterServiceName = CF.getServicesMap(con,true);
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				pst = con.prepareStatement("select service_id from employee_official_details where emp_id = ?");
				pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
				rs = pst.executeQuery();
				String []arrServiceId = null;
				if (rs.next()) {
					if(rs.getString("service_id")!=null) {
						arrServiceId = rs.getString("service_id").split(",");
					}
				}
				rs.close();
				pst.close();
				for(int ii=0; arrServiceId!=null && ii<arrServiceId.length; ii++) {
					if(!alServiceId.contains(arrServiceId[ii])) {
						alServiceId.add(arrServiceId[ii]);
					}
				}
			} else {
				pst = con.prepareStatement("select service_id,emp_id from employee_official_details");
				rs = pst.executeQuery();
				while(rs.next()) {
					alServiceId = (List)hm.get(rs.getString("emp_id"));
					if(alServiceId == null) alServiceId = new ArrayList();
					
					String []arrServiceId = null;
					if(rs.getString("service_id")!=null) {
						arrServiceId = rs.getString("service_id").split(",");
					}
					for(int ii=0; arrServiceId!=null && ii<arrServiceId.length; ii++) {
						if(!alServiceId.contains(arrServiceId[ii])) {
							alServiceId.add(arrServiceId[ii]);
						}
					}
					hm.put(rs.getString("emp_id"), alServiceId);
				}
				rs.close();
				pst.close();
			}
			
			
			Map<String, List<String>> hmShiftData = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from shift_details where org_id=?");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> shiftDetails = new ArrayList<String>();
				shiftDetails.add(rs.getString("colour_code"));	
				shiftDetails.add(rs.getString("shift_code"));
				shiftDetails.add(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				shiftDetails.add(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				shiftDetails.add(uF.getDateFormat(rs.getString("break_start"), DBTIME, CF.getStrReportTimeFormat()));
				shiftDetails.add(uF.getDateFormat(rs.getString("break_end"), DBTIME, CF.getStrReportTimeFormat()));
				
				hmShiftData.put(rs.getString("shift_id"), shiftDetails);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmShiftData", hmShiftData);
			
			
			request.setAttribute("alDay", _alDay);
			request.setAttribute("alDate", _alDate);
			request.setAttribute("alEmpId", alEmpId);
			request.setAttribute("hmList", hm);
			request.setAttribute("_alHolidays", _alHolidays);
			request.setAttribute("_hmHolidaysColour", _hmHolidaysColour);
			request.setAttribute("hmHolidays", hmHolidays);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmWeekEnds", hmWeekEnds);
			request.setAttribute("hmServicesWorkrdFor", hmServicesWorkrdFor);
			request.setAttribute("hmRosterServiceId", hmRosterServiceId);
			request.setAttribute("hmRosterServiceName", hmRosterServiceName);
			request.setAttribute("alServiceId", alServiceId);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("empRosterDetails", sb.toString());
			request.setAttribute("paycycleDuration",CF.getStrPaycycleDuration());
			
			request.setAttribute("hmLeavesMap",hmLeavesMap);
			request.setAttribute("hmLeavesColour",hmLeavesColour);
			request.setAttribute("hmLeavesName",hmLeavesName);
			request.setAttribute("CC", new FillServices(request).fillServicesHtml());
			request.setAttribute("hmEmpLevelMap", hmEmpLevelMap);
			

			request.setAttribute("hmWeekEndList", hmWeekEndList);
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);
			
//			System.out.println("hmWeekEndList======>"+hmWeekEndList);
			
			getSelectedFilter(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String paycycle;
	List<FillPayCycles> paycycleList;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public String getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}
	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public List<FillShift> getShiftList() {
		return shiftList;
	}

	public void setShiftList(List<FillShift> shiftList) {
		this.shiftList = shiftList;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrRosterId() {
		return strRosterId;
	}

	public void setStrRosterId(String strRosterId) {
		this.strRosterId = strRosterId;
	}

	public String getStrShiftChangeVal() {
		return strShiftChangeVal;
	}

	public void setStrShiftChangeVal(String strShiftChangeVal) {
		this.strShiftChangeVal = strShiftChangeVal;
	}

	public String getStrShiftChangeDate() {
		return strShiftChangeDate;
	}

	public void setStrShiftChangeDate(String strShiftChangeDate) {
		this.strShiftChangeDate = strShiftChangeDate;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getStrServiceId() {
		return strServiceId;
	}

	public void setStrServiceId(String strServiceId) {
		this.strServiceId = strServiceId;
	}

	public String getStrShiftId() {
		return strShiftId;
	}

	public void setStrShiftId(String strShiftId) {
		this.strShiftId = strShiftId;
	}
	
}