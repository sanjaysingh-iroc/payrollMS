package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.leave.ManagerLeaveApproval;
import com.konnect.jpms.reports.RosterReport;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpShiftSwappingSuggestionBox extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
  
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	 
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(RosterReport.class);
	
	private String f_org;
	private String strEmpId;
	private String strRosterId;
	private String strShiftChangeVal;
	private String strShiftChangeDate;
	private String strServiceId;
	private String strShiftId;
	private String remainingEmpShift;
	private String operation;
	private String calendarYear;
	private String strMonth;
	private String suggestedEmpId;
	private List<FillShift> shiftList;
	
	public String execute() throws Exception {  
		
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/roster/EmpShiftSwappingSuggestionBox.jsp");
		request.setAttribute(TITLE, "Shift Swapping Suggestion");
		
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		shiftList = new FillShift(request).fillShiftNames(uF.parseToInt(getF_org()));
		
		if(getOperation() !=null && getOperation().equals("Update")) {
			changeAndUpdateShift(uF); 
			return LOAD;
		} else {
			getMostSuitableEmpForThisShift(uF);	
			return LOAD;
		}
	}
	
	private void changeAndUpdateShift(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con, CF, uF);
			if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpSupervisorId = CF.getEmpSupervisorIdMap(con);
			
			String minMaxDateOfMonth = uF.getCurrentMonthMinMaxDate(getStrShiftChangeDate(), DATE_FORMAT_STR);
			String[] tmpMinMaxDateOfMonth = minMaxDateOfMonth.split("::::");
			
			String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(tmpMinMaxDateOfMonth[0], getStrShiftChangeDate(), DATE_FORMAT_STR);
			String strDay = uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR, "EEEE");
			if(strDay!=null) strDay = strDay.toUpperCase();
			
			
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
			
			
			System.out.println("getStrShiftChangeVal ===>> " + getStrShiftChangeVal());
			System.out.println("getSuggestedEmpId ===>> " + getSuggestedEmpId());
			Map<String, Map<String, String>> hmShiftTime = CF.getShiftTimeBaseOnShiftId(con);
			Map<String, String> shiftMap = hmShiftTime.get(getStrShiftChangeVal());
			if(shiftMap == null) shiftMap = new HashMap<String, String>();
			
			String strShiftFrom = shiftMap.get("FROM");
			String strShiftTo = shiftMap.get("TO");

			String strDomain = request.getServerName().split("\\.")[0];
			if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
				double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
				pst = con.prepareStatement("UPDATE roster_details SET  _date= ?, _from= ?, _to= ?,  actual_hours= ?, shift_id=?, entry_date=?, roster_weeklyoff_id=? where emp_id=? and _date=? and service_id =?");
				pst.setDate(1, uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
				pst.setTime(2, uF.getTimeFormat(strShiftFrom, TIME_FORMAT));
				pst.setTime(3, uF.getTimeFormat(strShiftTo, TIME_FORMAT));
				pst.setDouble(4, dblTimeDiff);
				pst.setInt(5, uF.parseToInt(shiftMap.get("SHIFT_ID")));
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(7, 0);
				pst.setInt(8, uF.parseToInt(getStrEmpId()));
				pst.setDate(9,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
				pst.setInt(10, uF.parseToInt(getStrServiceId()));
				int x = pst.executeUpdate();
				System.out.println("update pst ===>> " + pst);
				pst.close();
				
				pst = con.prepareStatement("delete from roster_weekly_off where emp_id=? and weekoff_date=? ");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
				pst.executeUpdate();
				pst.close();
				
				if(x==0) {
					pst = con.prepareStatement("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?, ?,(select user_id from user_details where emp_id=?),?,?, ?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					pst.setTime(3, uF.getTimeFormat(strShiftFrom, TIME_FORMAT));
					pst.setTime(4, uF.getTimeFormat(strShiftTo, TIME_FORMAT));
					pst.setBoolean(5, false);
					pst.setInt(6, uF.parseToInt(getStrEmpId()));
					pst.setInt(7, uF.parseToInt(getStrServiceId()));
					pst.setDouble(8, dblTimeDiff);
					pst.setInt(9, 0);
					pst.setBoolean(10, false);
					pst.setInt(11, uF.parseToInt(shiftMap.get("SHIFT_ID")));
					pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(13, 0);
					pst.executeUpdate();
//					System.out.println(strEmpId + " insert --- pst ===>> " + pst);
					pst.close();
					

					/*String strLeaveRegisterId = null; 
					pst = con.prepareStatement("SELECT * FROM leave_application_register where emp_id=? and _date=?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					rs = pst.executeQuery();
					while(rs.next()){
						strLeaveRegisterId = rs.getString("leave_register_id");
					}	
					rs.close();
					pst.close();
					
					if(uF.parseToInt(strLeaveRegisterId)>0) {
						pst = con.prepareStatement("delete from leave_application_register where leave_register_id=?");
						pst.setInt(1, uF.parseToInt(strLeaveRegisterId));
						pst.executeUpdate();
						pst.close();
					}*/
				}
				
				pst = con.prepareStatement("delete from leave_application_register where emp_id=? and _date=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
				pst.execute();
				System.out.println("delete Leave pst ===>> " + pst);
				pst.close();
				
				
				Notifications nF = new Notifications(N_CHANGE_ROSTER, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(getStrEmpId());
				nF.setSupervisor(false);
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				pst = con.prepareStatement("SELECT * FROM roster_details where shift_id=? and _date=?");
				pst.setInt(1, uF.parseToInt(getStrShiftChangeVal()));
				pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
				rs = pst.executeQuery();
				List<String> alTlIds = new ArrayList<String>();
				while(rs.next()) {
					String strEmpLevel = hmEmpLevelId.get(rs.getString("emp_id"));
					if(alTlLevels !=null && alTlLevels.contains(strEmpLevel)) {
						alTlIds.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				for(int i=0; i<alTlIds.size(); i++) {
					nF = new Notifications(N_CHANGE_ROSTER, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(alTlIds.get(i));
					nF.setSupervisor(false);
					nF.setEmailTemplate(true);
					nF.sendNotifications();
				}
				
				nF = new Notifications(N_CHANGE_ROSTER, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(hmEmpSupervisorId.get(getStrEmpId()));
				nF.setSupervisor(false);
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
//				setStrShiftId(shiftMap.get("SHIFT_ID"));
				
			} else if(getStrShiftChangeVal() !=null && getStrShiftChangeVal().equals("0")) {
				String rosterWOffId = null;
				Iterator<String> it = hmRosterWeeklyoff.keySet().iterator();
				while(it.hasNext()) {
					String strRosterWOffId = it.next();
					Map<String, String> hmInner = (Map<String, String>) hmRosterWeeklyoff.get(strRosterWOffId);
					List<String> weeklyOffDayList = hmInner.get("WEEKLYOFF_DAY")!=null && !hmInner.get("WEEKLYOFF_DAY").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_DAY").trim().split(",")) : new ArrayList<String>();
					List<String> weekNoList = hmInner.get("WEEKLYOFF_WEEKNO")!=null && !hmInner.get("WEEKLYOFF_WEEKNO").equals("") ? Arrays.asList(hmInner.get("WEEKLYOFF_WEEKNO").trim().split(",")) : new ArrayList<String>();
					if(weekNoList !=null && weekNoList.size()==1 && weekNoList.contains(weekNoOfTheDateInMonth) && weeklyOffDayList != null && weeklyOffDayList.size()==1 && weeklyOffDayList.contains(strDay)) {
						rosterWOffId = strRosterWOffId;
					}
				}
				
				if(uF.parseToInt(rosterWOffId)>0) {
					
					if(uF.parseToInt(getStrShiftId())==0) {
						String strCurrMOnthMinMaxDates = uF.getCurrentMonthMinMaxDate(getStrShiftChangeDate(), DATE_FORMAT_STR);
						String arrCurrMOnthMinMaxDates[] = strCurrMOnthMinMaxDates.split("::::");
						
						String strPrevShiftId = null;
						String strNextShiftId = null;
						pst = con.prepareStatement("SELECT * FROM roster_details where emp_id=? and _date<? and _date>=? order by _date desc limit 1");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
						pst.setDate(3,  uF.getDateFormat(arrCurrMOnthMinMaxDates[0], DATE_FORMAT_STR));
						rs = pst.executeQuery();
						while(rs.next()) {
							strPrevShiftId = rs.getString("shift_id");
						}
						rs.close();
						pst.close();
						pst = con.prepareStatement("SELECT * FROM roster_details where emp_id=? and _date>? and _date<=? order by _date limit 1");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
						pst.setDate(3,  uF.getDateFormat(arrCurrMOnthMinMaxDates[1], DATE_FORMAT_STR));
						rs = pst.executeQuery();
						while(rs.next()) {
							strNextShiftId = rs.getString("shift_id");
						}
						rs.close();
						pst.close();
						
						String strDtDay = uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR, "dd");
						setStrShiftId(strPrevShiftId);
//						String strNewShiftId = strPrevShiftId;
						if(uF.parseToInt(strPrevShiftId) != uF.parseToInt(strNextShiftId) && uF.parseToInt(strNextShiftId)>0 && uF.parseToInt(strDtDay)>15) {
							setStrShiftId(strNextShiftId);
						}
					}
					
					
					pst = con.prepareStatement("update roster_weekly_off set roster_weeklyoff_id=?, shift_id=? where emp_id=? and weekoff_date=? and service_id =?");
					pst.setInt(1, uF.parseToInt(rosterWOffId));
					pst.setInt(2, uF.parseToInt(getStrShiftId()));
					pst.setInt(3, uF.parseToInt(getStrEmpId()));
					pst.setDate(4,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					pst.setInt(5, uF.parseToInt(getStrServiceId()));
					int x = pst.executeUpdate();
					System.out.println("weekoff update pst ===>> " + pst);
					pst.close();
					
					if (x == 0) {
						pst = con.prepareStatement("insert into roster_weekly_off (emp_id, weekoff_date, service_id, roster_weeklyoff_id, shift_id) values(?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
						pst.setInt(3, uF.parseToInt(getStrServiceId()));
						pst.setInt(4, uF.parseToInt(rosterWOffId));
						pst.setInt(5, uF.parseToInt(getStrShiftId()));
						pst.execute();
						System.out.println("weekoff insert pst ===>> " + pst);
						pst.close();
					}
					
					pst = con.prepareStatement("UPDATE roster_details SET roster_weeklyoff_id=? where roster_id=?");
					pst.setInt(1, uF.parseToInt(rosterWOffId));
					pst.setInt(2, uF.parseToInt(getStrRosterId()));
					int y = pst.executeUpdate();
					System.out.println("update roster_details pst ===>> " + pst);
					pst.close();
					
					if(y==0) {
						Map<String, String> shiftMap1 = hmShiftTime.get(getStrShiftId());
						if(shiftMap1 == null) shiftMap1 = new HashMap<String, String>();
						
						strShiftFrom = shiftMap1.get("FROM");
						strShiftTo = shiftMap1.get("TO");
						
						double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
						
						pst = con.prepareStatement("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?, ?,(select user_id from user_details where emp_id=?),?,?, ?,?,?,?, ?)");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
						pst.setTime(3, uF.getTimeFormat(strShiftFrom, TIME_FORMAT));
						pst.setTime(4, uF.getTimeFormat(strShiftTo, TIME_FORMAT));
						pst.setBoolean(5, false);
						pst.setInt(6, uF.parseToInt(getStrEmpId()));
						pst.setInt(7, uF.parseToInt(getStrServiceId()));
						pst.setDouble(8, dblTimeDiff);
						pst.setInt(9, 0);
						pst.setBoolean(10, false);
						pst.setInt(11, uF.parseToInt(getStrShiftId()));
						pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(13, uF.parseToInt(rosterWOffId));
						pst.executeUpdate();
						System.out.println(getStrEmpId() + " insert --- pst ===>> " + pst);
						pst.close();
					}
					
					pst = con.prepareStatement("delete from leave_application_register where emp_id=? and _date=?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setDate(2, uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					pst.execute();
					System.out.println("delete Leave pst ===>> " + pst);
					pst.close();
					
				}
			} else if(getStrShiftChangeVal()!=null && getStrShiftChangeVal().equals("L")) {
				pst = con.prepareStatement("delete from roster_weekly_off where emp_id=? and weekoff_date=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
				pst.execute();
				System.out.println("delete weekoff pst ===>> " + pst);
				pst.close();
				
				pst = con.prepareStatement("UPDATE roster_details SET roster_weeklyoff_id=? where roster_id=?");
				pst.setInt(1, 0);
				pst.setInt(2, uF.parseToInt(getStrRosterId()));
				int y = pst.executeUpdate();
				System.out.println("update roster_details pst ===>> " + pst);
				pst.close();
				
				/* pst = con.prepareStatement("delete from roster_details emp_id=? and _date=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
				pst.executeUpdate();
				System.out.println("delete roster_details pst ===>> " + pst);
				pst.close();*/
				
				String strWLocationId = CF.getEmpWlocationId(con, uF, getStrEmpId());
				String strLevelId = CF.getEmpLevelId(con, getStrEmpId());
				
				pst = con.prepareStatement("select * from probation_policy where emp_id=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				rs = pst.executeQuery();
				String strAllowedLeaves = null;
				while (rs.next()) {
					strAllowedLeaves = rs.getString("leaves_types_allowed");
				}
				rs.close();
				pst.close();
				List<String> alEmpLeaves = null;
				if (strAllowedLeaves != null && strAllowedLeaves.length() > 0) {
					alEmpLeaves = Arrays.asList(strAllowedLeaves.split(","));
				}
				
					if(alEmpLeaves !=null && alEmpLeaves.size()>0) {
					pst = con.prepareStatement("select * from emp_leave_type where level_id=(select level_id from designation_details dd," +
							"grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from " +
							"employee_official_details where emp_id = ?)) and leave_type_id = ? and wlocation_id=?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, uF.parseToInt(alEmpLeaves.get(0)));
					pst.setInt(3, uF.parseToInt(strWLocationId));
					rs = pst.executeQuery();
					System.out.println("pst ===>> " + pst);
					boolean isApproval = false;
					boolean isPaid = false;
					while(rs.next()) {
						isApproval = uF.parseToBoolean(rs.getString("is_approval"));
						isPaid = uF.parseToBoolean(rs.getString("is_paid"));
					}
					rs.close();
					pst.close();
					System.out.println("isApproval ===>> " + isApproval);
					
	//				pst = con.prepareStatement(insertEmployeeLeaveEntry);
					pst=con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,leave_type_id,reason," +
						"approval_from,approval_to_date, ishalfday, session_no,document_attached,is_approved,ispaid,is_compensate,is_work_from_home) " +
						"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setDate(2, uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					pst.setDate(3, uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					
					pst.setDate	(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, 1);
					pst.setInt(6, uF.parseToInt(alEmpLeaves.get(0)));
					pst.setString(7, "Leave applied from Roster ");
					pst.setDate(8, uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					pst.setDate(9, uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					pst.setBoolean(10, false);
					pst.setString(11, null);
					pst.setString(12, null);
					pst.setInt(13, 1);
					pst.setBoolean(14, true);
					pst.setBoolean(15, false);
					pst.setBoolean(16, false);
					pst.execute();
					System.out.println("pst===>> " + pst);
					pst.close();
					
					String strLeaveDate = uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR, DATE_FORMAT);
					System.out.println("strLeaveDate ===> " + strLeaveDate);
					String leave_id=null;
					pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
					rs=pst.executeQuery();
					while(rs.next()) {
						leave_id = rs.getString("leave_id");
					}
					rs.close();
					pst.close();
					System.out.println("leave_id ===> " + leave_id);
					
					if(!isApproval && uF.parseToInt(leave_id)>0) {
						//updateLeaveBalance(con, pst, rs, leave_id, uF, getLeaveFromTo(), getLeaveToDate());
						ManagerLeaveApproval leaveApproval=new ManagerLeaveApproval();
						leaveApproval.setServletRequest(request);
						leaveApproval.setLeaveId(leave_id);
						leaveApproval.setTypeOfLeave(alEmpLeaves.get(0));
						leaveApproval.setEmpId(getStrEmpId());
						leaveApproval.setIsapproved(1);
						leaveApproval.setApprovalFromTo(strLeaveDate);
						leaveApproval.setApprovalToDate(strLeaveDate);				
						leaveApproval.insertLeaveBalance(con, pst, rs, uF, uF.parseToInt(strLevelId), CF);
					}
				}
			}
			
			/*if(uF.parseToInt(getSuggestedEmpId())>0) {
				Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
				String strEmpServiceId = hmEmpServiceId.get(getSuggestedEmpId()) != null ? hmEmpServiceId.get(getSuggestedEmpId()).substring(1, hmEmpServiceId.get(getSuggestedEmpId()).length()-1) : "0";
				shiftMap = hmShiftTime.get(getStrShiftId());
				if(shiftMap == null) shiftMap = new HashMap<String, String>();
				
				strShiftFrom = shiftMap.get("FROM");
				strShiftTo = shiftMap.get("TO");
				
				if(strShiftFrom != null && !strShiftFrom.trim().equals("") && strShiftTo != null && !strShiftTo.trim().equals("")) {
					double dblTimeDiff = uF.parseToDouble((uF.getTimeDiffInHoursMins(uF.getTimeFormat(strShiftFrom, DBTIME).getTime(), uF.getTimeFormat(strShiftTo, DBTIME).getTime())));
					pst = con.prepareStatement("UPDATE roster_details SET  _date= ?, _from= ?, _to= ?,  actual_hours= ?, shift_id=?, entry_date=?, roster_weeklyoff_id=? where emp_id=? and _date=? and service_id =?");
					pst.setDate(1, uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					pst.setTime(2, uF.getTimeFormat(strShiftFrom, TIME_FORMAT));
					pst.setTime(3, uF.getTimeFormat(strShiftTo, TIME_FORMAT));
					pst.setDouble(4, dblTimeDiff);
					pst.setInt(5, uF.parseToInt(getStrShiftId()));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, 0);
					pst.setInt(8, uF.parseToInt(getSuggestedEmpId()));
					pst.setDate(9,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					pst.setInt(10, uF.parseToInt(strEmpServiceId));
					int x = pst.executeUpdate();
					System.out.println(getSuggestedEmpId() +" --- update pst ===>> " + pst);
					pst.close();
					if(x==0) {
						pst = con.prepareStatement("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) values(?,?,?,?, ?,(select user_id from user_details where emp_id=?),?,?, ?,?,?,?, ?)");
						pst.setInt(1, uF.parseToInt(getSuggestedEmpId()));
						pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
						pst.setTime(3, uF.getTimeFormat(strShiftFrom, TIME_FORMAT));
						pst.setTime(4, uF.getTimeFormat(strShiftTo, TIME_FORMAT));
						pst.setBoolean(5, false);
						pst.setInt(6, uF.parseToInt(getSuggestedEmpId()));
						pst.setInt(7, uF.parseToInt(strEmpServiceId));
						pst.setDouble(8, dblTimeDiff);
						pst.setInt(9, 0);
						pst.setBoolean(10, false);
						pst.setInt(11, uF.parseToInt(getStrShiftId()));
						pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(13, 0);
						pst.executeUpdate();
						System.out.println(getSuggestedEmpId() + " insert --- pst ===>> " + pst);
						pst.close();
						
					}
//					setStrShiftId(shiftMap.get("SHIFT_ID"));
					
					Notifications nF = new Notifications(N_CHANGE_ROSTER, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(getSuggestedEmpId());
					nF.setSupervisor(false);
					nF.setEmailTemplate(true);
					nF.sendNotifications();
					
					pst = con.prepareStatement("SELECT * FROM roster_details where shift_id=? and _date=?");
					pst.setInt(1, uF.parseToInt(getStrShiftId()));
					pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
					rs = pst.executeQuery();
					List<String> alTlIds = new ArrayList<String>();
					while(rs.next()) {
						String strEmpLevel = hmEmpLevelId.get(rs.getString("emp_id"));
						if(alTlLevels !=null && alTlLevels.contains(strEmpLevel)) {
							alTlIds.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					
					for(int i=0; i<alTlIds.size(); i++) {
						nF = new Notifications(N_CHANGE_ROSTER, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrEmpId(alTlIds.get(i));
						nF.setSupervisor(false);
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
					
					nF = new Notifications(N_CHANGE_ROSTER, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(hmEmpSupervisorId.get(getSuggestedEmpId()));
					nF.setSupervisor(false);
					nF.setEmailTemplate(true);
					nF.sendNotifications();
				}
			}*/
			
			
			
			String strDateShiftId = null;
			String strDateRosterId = null;
			String strDateWeekOffId = null;
			String strDateLeaveId = null;
			pst = con.prepareStatement("SELECT * FROM roster_details where emp_id=? and _date=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
			rs = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				strDateShiftId = rs.getString("shift_id");
				strDateRosterId = rs.getString("roster_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM roster_weekly_off where emp_id=? and weekoff_date=? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
			rs = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			while(rs.next()) {
				strDateWeekOffId = rs.getString("roster_weeklyoff_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM leave_application_register where emp_id=? and _date=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
			rs = pst.executeQuery();
			System.out.println("pst ===>> " + pst);
			while(rs.next()){
				strDateLeaveId = rs.getString("leave_register_id");
			}	
			rs.close();
			pst.close();
			
			Map<String, String> hmShiftColor = new HashMap<String, String>();
//			Map<String, String> hmShiftDetails = CF.getShiftMap(con);
			pst = con.prepareStatement("SELECT * FROM shift_details order by shift_id");
			rs = pst.executeQuery();
			while(rs.next()){
				hmShiftColor.put(rs.getString("shift_id"), rs.getString("colour_code"));
			}	
			rs.close();
			pst.close();
			
			String strColour = null;
			String shiftColor = hmShiftColor.get(strDateShiftId);
			System.out.println("shiftColor ===>> " + shiftColor + " -- hmShiftColor ===>> " + hmShiftColor);
			
			if(strDateLeaveId != null && uF.parseToInt(strDateLeaveId)>0) {
        		strColour = "#b5b5b5";
        		
        	}
			if(strDateWeekOffId !=null && uF.parseToInt(strDateWeekOffId)>0) {
				strColour = WEEKLYOFF_COLOR;
			}
			if(strColour == null && shiftColor != null) {
        		strColour = shiftColor;
        	}
			
			String strDateFormatDate = uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR, DATE_FORMAT);
			StringBuilder sbSelect = new StringBuilder();
			sbSelect.append("<select style=\"height: 25px !important; width: 41px !important; background-color: "+strColour+" \" " +
					"onchange=\"changeAndAssignNewShift(this.value, '"+getStrEmpId()+"','"+strDateRosterId+"', '"+getStrShiftChangeDate()+"', " +
					"'"+getStrServiceId()+"','"+strDateShiftId+"','"+getRemainingEmpShift()+"', '"+strDateFormatDate+"');\" >");
			sbSelect.append("<option value=\"-1\">-</option>");
			sbSelect.append("<option value=\"L\"");
			if(strDateLeaveId !=null && uF.parseToInt(strDateLeaveId)>0) { 
   				sbSelect.append("selected"); 
   			}
   			sbSelect.append(">L</option>");
			sbSelect.append("<option value=\"0\"");
			if(strDateWeekOffId !=null && uF.parseToInt(strDateWeekOffId)>0) { 
   				sbSelect.append("selected"); 
   			}
   			sbSelect.append(">0</option>");
			
			for(int a=0; shiftList!=null && a<shiftList.size(); a++) {
				sbSelect.append("<option value=\""+shiftList.get(a).getShiftId()+"\"");
       			if(uF.parseToInt(getStrShiftChangeVal())>0 && strDateShiftId != null && strDateShiftId.equals(shiftList.get(a).getShiftId())) { 
       				sbSelect.append("selected"); 
       			}
       			sbSelect.append(">"+shiftList.get(a).getShiftCode()+"</option>");
       		}
       		sbSelect.append("</select>");
       
			request.setAttribute("sbSelect", sbSelect.toString());
       		
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String loadRoster(UtilityFunctions uF) {

//		shiftList  = new FillShift(request).fillShiftNames(uF.parseToInt(getF_org()));
		return LOAD;
	}

	
	public void getMostSuitableEmpForThisShift(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		System.out.println("getMostSuitableEmpForThisShift =============>> ");
		try {
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmRosterWeeklyoff = CF.getRosterWeeklyOffDetails(con, CF, uF);
			if(hmRosterWeeklyoff == null) hmRosterWeeklyoff = new HashMap<String, Map<String,String>>();
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmShiftData = CF.getShiftMap(con);
			Map<String, String> hmEmpServiceId = CF.getEmpServiceMap(con);
			
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			
			String strTmpDate = uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR, DATE_FORMAT);
			String minMaxDateOfMonth = uF.getCurrentMonthMinMaxDate(strTmpDate, DATE_FORMAT);
			String[] tmpMinMaxDateOfMonth = minMaxDateOfMonth.split("::::");
			
			String weekNoOfTheDateInMonth = uF.getWeekNoOfTheDateInMonth(tmpMinMaxDateOfMonth[0], strTmpDate, DATE_FORMAT);
			String dayDiff = uF.dateDifference(tmpMinMaxDateOfMonth[0], DATE_FORMAT, strTmpDate, DATE_FORMAT);
			String strDay = uF.getDateFormat(strTmpDate, DATE_FORMAT, "EEEE");
			if(strDay!=null) strDay = strDay.toUpperCase();
			
			
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
			
			String strRemainingEmpShift = hmRosterPolicyRulesData.get("REMAINING_EMP_SHIFT");
			
			int intWeekEndEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT_AT_WEEKEND"));
			int intWeekdaysEmpCnt = uF.parseToInt(hmRosterPolicyRulesData.get("NO_OF_MEMBER_IN_SHIFT"));
			
			String strCurrEmpLevelId = hmEmpLevel.get(getStrEmpId());
			boolean tlFlag = false;
			if(alTlLevels.contains(strCurrEmpLevelId)) {
				tlFlag = true;
			}
			
			
			String empOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			String[] strPaycycle = CF.getPayCycleFromDate(con, strTmpDate, CF.getStrTimeZone(), CF, empOrgId);
			
			String[] strPrevPayCycle = CF.getPayCycleDatesOnPaycycleId(con, (uF.parseToInt(strPaycycle[2])-1)+"", empOrgId, CF.getStrTimeZone(), CF, request);
			String strPrevFrmDate = strPrevPayCycle[0];
			String strPrevToDate = strPrevPayCycle[1];
			
			Map<String, Map<String, String>> hmRosterAssignedEmpCntData = getRosterAssignedEmpCountData(con, uF, strTmpDate, strTmpDate, empOrgId, alTlLevels);
			Map<String, String> hmExistEmpCnt = hmRosterAssignedEmpCntData.get("EMP_CNT");
			Map<String, String> hmExistEmpGenderwiseCnt = hmRosterAssignedEmpCntData.get("EMP_GENDERWISE_CNT");
			Map<String, String> hmExistTlEmpCnt = hmRosterAssignedEmpCntData.get("TL_EMP_CNT");
			
			
			String strLeaveRegisterId = null; 
			pst = con.prepareStatement("SELECT * FROM roster_details where _date=? and shift_id=? ");
			pst.setDate(1,  uF.getDateFormat(getStrShiftChangeDate(), DATE_FORMAT_STR));
			pst.setInt(2, uF.parseToInt(getRemainingEmpShift()));
			rs = pst.executeQuery();
			List<String> alTlFPIds = new ArrayList<String>();
			List<String> alEmpFPIds = new ArrayList<String>();
			List<String> alTlSPIds = new ArrayList<String>();
			List<String> alEmpSPIds = new ArrayList<String>();
			while(rs.next()) {
				String strNewEmpId = rs.getString("emp_id");
				String strEmpServiceId = hmEmpServiceId.get(strNewEmpId) != null ? hmEmpServiceId.get(strNewEmpId).substring(1, hmEmpServiceId.get(strNewEmpId).length()-1) : "0";
				String strEmpLevelId = hmEmpLevel.get(strNewEmpId);
				
				Map<String, String> hmEmpLastMonthRoster = getEmpLastMonthRosterData(con, uF, strNewEmpId, strPrevFrmDate, strPrevToDate, strEmpServiceId, rotFirst, rotSecond, rotThird);
				String strExistShiftId = hmEmpLastMonthRoster.get("LASTSHIFT_ID"); //  innerList.get(1);
				System.out.println("strExistShiftId ===>> " + strExistShiftId);
				
				String newShiftId = "";
				if(!rotFirst.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotFirst) && uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))==0)) {
					newShiftId = rotSecond;
				} else if(!rotSecond.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotSecond) && uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))==0)) {
					newShiftId = rotThird;
				} else if(!rotThird.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotThird) && uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))==0)) {
					newShiftId = rotFirst;
				} else {
					newShiftId = strRemainingEmpShift;
				}
				
				if(uF.parseToInt(dayDiff) > 15) {
					if(!rotFirst.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotFirst) && uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))==0)) {
						newShiftId = rotSecond;
					} else if(!rotSecond.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotSecond) && uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotSecond))==0)) {
						newShiftId = rotThird;
					} else if(!rotThird.equals("") && uF.parseToInt(strExistShiftId) == uF.parseToInt(rotThird) && uF.parseToInt(hmEmpLastMonthRoster.get(rotFirst))<=15 && (uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))>=5 || uF.parseToInt(hmEmpLastMonthRoster.get(rotThird))==0)) {
						newShiftId = rotFirst;
					} else {
						newShiftId = strRemainingEmpShift;
					}
				}
				
				if(uF.parseToInt(getStrShiftId()) == uF.parseToInt(newShiftId)) {
					if(alTlLevels.contains(strEmpLevelId)) {
						alTlFPIds.add(strNewEmpId);
					} else {
						alEmpFPIds.add(strNewEmpId);
					}
				} else {
					if(alTlLevels.contains(strEmpLevelId)) {
						alTlSPIds.add(strNewEmpId);
					} else {
						alEmpSPIds.add(strNewEmpId);
					}
				}
			}
			rs.close();
			pst.close();
			
			System.out.println("alTlFPIds ===>> " + alTlFPIds);
			System.out.println("alEmpFPIds ===>> " + alEmpFPIds);
			System.out.println("alTlSPIds ===>> " + alTlSPIds);
			System.out.println("alEmpSPIds ===>> " + alEmpSPIds);
			
			request.setAttribute("alTlFPIds", alTlFPIds);
			request.setAttribute("alEmpFPIds", alEmpFPIds);
			request.setAttribute("alTlSPIds", alTlSPIds);
			request.setAttribute("alEmpSPIds", alEmpSPIds);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmShiftData", hmShiftData);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private Map<String, Map<String, String>> getRosterAssignedEmpCountData(Connection con, UtilityFunctions uF, String strFromDate, String strToDate, String orgId, List<String> alTlLevels) {
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
	
	
	private Map<String, String> getEmpLastMonthRosterData(Connection con, UtilityFunctions uF, String empId, String strPaycycleFromDate, String strPaycycleToDate, String strEmpServiceId, String rotFirst, String rotSecond, String rotThird) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmEmpRosterData = new HashMap<String, String>();
		try {

			String strShifts = rotFirst+","+rotSecond+","+rotThird;
			Date strLast7DaysShift = uF.getFutureDate(uF.getDateFormatUtil(strPaycycleToDate, DATE_FORMAT), -7);
//			pst = con.prepareStatement("select count(shift_id) as cnt, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? group by shift_id order by _date");
			pst = con.prepareStatement("select _date, shift_id from roster_details where emp_id=? and _date between ? and ? and service_id=? and shift_id in ("+strShifts+") order by _date");
			pst.setInt(1, uF.parseToInt(empId));
			pst.setDate(2, uF.getDateFormat(strPaycycleFromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPaycycleToDate, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strEmpServiceId));
			rs = pst.executeQuery();
			Map<String, String> hmEmpLastRosterData = new HashMap<String, String>();
			while(rs.next()) {
				int rosterCnt = uF.parseToInt(hmEmpRosterData.get(rs.getString("shift_id")));
				rosterCnt++;
				hmEmpRosterData.put(rs.getString("shift_id"), rosterCnt+"");
				if(strLast7DaysShift.before(uF.getDateFormatUtil(rs.getString("_date"), DBDATE))) {
					int lastRosterCnt = uF.parseToInt(hmEmpLastRosterData.get(rs.getString("shift_id")));
					lastRosterCnt++;
					hmEmpLastRosterData.put(rs.getString("shift_id"), lastRosterCnt+"");
				}
			}
			rs.close();
			pst.close();
			int shiftCnt = 0;
			String strShiftId = null;
			Iterator<String> it = hmEmpLastRosterData.keySet().iterator();
			while(it.hasNext()) {
				String shiftId = it.next();
				int intCnt = uF.parseToInt(hmEmpLastRosterData.get(shiftId));
				if(shiftCnt==0) {
					shiftCnt = intCnt;
					strShiftId = shiftId;
				} else if(shiftCnt<intCnt) {
					shiftCnt = intCnt;
					strShiftId = shiftId;
				}
			}
			hmEmpRosterData.put("LASTSHIFT_ID", strShiftId);
			
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

	public String getRemainingEmpShift() {
		return remainingEmpShift;
	}

	public void setRemainingEmpShift(String remainingEmpShift) {
		this.remainingEmpShift = remainingEmpShift;
	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getSuggestedEmpId() {
		return suggestedEmpId;
	}

	public void setSuggestedEmpId(String suggestedEmpId) {
		this.suggestedEmpId = suggestedEmpId;
	}

	public List<FillShift> getShiftList() {
		return shiftList;
	}

	public void setShiftList(List<FillShift> shiftList) {
		this.shiftList = shiftList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}
	
}