package com.konnect.jpms.employee;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.charts.LinearZMeter;
import com.konnect.jpms.charts.PieCharts;
import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;


public class ArticleDashboard implements IStatements {

	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	private static Logger log = Logger.getLogger(ArticleDashboard.class);

	public ArticleDashboard(HttpServletRequest request, HttpSession session, CommonFunctions CF, String strEmpId) {
		this.request = request;
		this.session = session;		
		this.CF = CF; 
		this.strEmpId = strEmpId;
	}
    
	public String loadDashboard() { 

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		CallableStatement cst = null;

		UtilityFunctions uF = new UtilityFunctions(); 
		PreparedStatement pst =null;

		try {
			String strUserType = (String)session.getAttribute(USERTYPE);
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map hmServices = CF.getServicesMap(con,true); 
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con,strUserType, strEmpId);
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getEmpDepartmentMap(con);
			
			Map<String, Map<String, String>> hmWlocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			

//			pst = con.prepareStatement(selectEmployee1V);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//
//			rs = pst.executeQuery();

			

//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_work_location_info(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectEmployee1V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			boolean isRosterDependent = false;
			String strJoiningDate = null;
			if (rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				request.setAttribute("DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
				request.setAttribute("IMAGE", ((rs.getString("emp_image") != null && rs.getString("emp_image").length() > 0) ? rs.getString("emp_image") : "avatar_photo.png"));
				request.setAttribute("DEPT", rs.getString("dept_name"));
				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
				request.setAttribute("EMAIL", rs.getString("emp_email"));
				request.setAttribute("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, "dd MMMM, yyyy"));
				strJoiningDate = rs.getString("joining_date");
				isRosterDependent = uF.parseToBoolean(rs.getString("is_roster"));
			}
			rs.close();
			pst.close();

			//cst.close();

			/**
			 * Query for feching Designation. 
			 * */
			
//			pst = con.prepareStatement(selectEmployeeDesig1);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_designation(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			
			pst = con.prepareStatement(selectEmployeeDesig1);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				request.setAttribute("DESIG_NAME", rs.getString("designation_name")+" ["+rs.getString("designation_code")+"]");
			}
			rs.close();
			pst.close();
			//cst.close();
			
			
			
//			pst = con.prepareStatement(selectEmployee3V);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_details(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectEmployee3V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("MANAGER", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();

			//cst.close();

//			pst = con.prepareStatement(selectMyClockEntries);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();

			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_clockentries(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectMyClockEntries);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			
			Map<String, Map<String, String>> hmMyAttendence = new LinkedHashMap<String, Map<String, String>>();
			Map hmMyAttendence1 = new LinkedHashMap();
			Map<String, String> hm = new HashMap<String, String>();

			String strDateNew = "";
			String strDateOld = "";
			String strServiceNewId = null;
			String strServiceOldId = null;
			List alServices = new ArrayList();
			while (rs.next()) {

				strDateNew = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
				strServiceNewId = rs.getString("service_id");
				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					hm = new HashMap();
				}

				if (strServiceNewId != null && !strServiceNewId.equalsIgnoreCase(strServiceOldId)) {
					hm = new HashMap();
				}

				if ("IN".equalsIgnoreCase(rs.getString("in_out"))) {
					hm.put("IN", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				} else {
					hm.put("OUT", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}
				hm.put("SERVICE", (String) hmServices.get(strServiceNewId));

				hmMyAttendence.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + strServiceNewId, hm);

				alServices = (List) hmMyAttendence1.get(strDateNew);
				if (alServices == null) {
					alServices = new ArrayList();
				}
				if (!alServices.contains(strServiceNewId)) {
					alServices.add(strServiceNewId);
				}

				hmMyAttendence1.put(strDateNew, alServices);

				strDateOld = strDateNew;
				strServiceOldId = strServiceNewId;

			}
			rs.close();
			pst.close();

			//cst.close();
			request.setAttribute("hmMyAttendence", hmMyAttendence);
			request.setAttribute("hmMyAttendence1", hmMyAttendence1);

			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			

			double[] pending = new double[1];
			double[] approved = new double[1];
			double[] denied = new double[1];
			String[] label = new String[] { "" };

//			pst = con.prepareStatement(selectApprovalsCount);
//			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
//			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
//			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();

			
			
			
			
			if(CF.isRosterDependency(con,(String) session.getAttribute("EMPID"))){
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_approval_count(?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
//				cst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
//				cst.setInt(4, uF.parseToInt((String) session.getAttribute("EMPID")));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectApprovalsCount);
				pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
				pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
				pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
				rs = pst.executeQuery();

				while (rs.next()) {

					if (uF.parseToInt(rs.getString("approved")) == 1) {
						request.setAttribute("EXCEP_APPROVED_COUNT", rs.getString("count"));
						approved[0] = uF.parseToInt(rs.getString("count"));
					} else if (uF.parseToInt(rs.getString("approved")) == -1) {
						request.setAttribute("EXCEP_DENIED_COUNT", rs.getString("count"));
						denied[0] = uF.parseToInt(rs.getString("count"));
					} else if (uF.parseToInt(rs.getString("approved")) == -2) {
						request.setAttribute("EXCEP_WAITING_COUNT", rs.getString("count"));
						pending[0] = uF.parseToInt(rs.getString("count"));
					}
				}
				rs.close();
				pst.close();

				//cst.close();
			}
			
			
			
			
			
//			pst = con.prepareStatement("select approval_2, count(approval_2) from emp_reimbursement where entry_date between ? AND ? and  emp_id = ? group By approval_2");
//			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
//			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
//			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
//
//			rs = pst.executeQuery();
//			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_reimbursement_count(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
//			cst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
//			cst.setInt(4, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("select approval_2, count(approval_2) from emp_reimbursement where entry_date between ? AND ? and  emp_id = ? group By approval_2");
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();

			while (rs.next()) {

				if (uF.parseToInt(rs.getString("approval_2")) == 1) {
					request.setAttribute("REIMB_APPROVED_COUNT", rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("approval_2")) == -1) {
					request.setAttribute("REIMB_DENIED_COUNT", rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("approval_2")) == 0) {
					request.setAttribute("REIMB_WAITING_COUNT", rs.getString("count"));
				}
			}
			rs.close();
			pst.close();

			//cst.close();
			
			
			
			
			
			
			

			request.setAttribute("CHART_APPROVALS", new BarChart().getMulitCharts(pending, approved, denied, label));

//			pst = con.prepareStatement(selectApprovalsCountForManager);
//			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
//			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
//			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_manager_approval_count(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
//			cst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
//			cst.setInt(4, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectApprovalsCountForManager);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			
			int total = 0;
			while (rs.next()) {

				if (uF.parseToInt(rs.getString("approved")) == 1) {
					request.setAttribute("EMP_APPROVED_COUNT", rs.getString("count"));
					total += uF.parseToInt(rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("approved")) == -1) {
					request.setAttribute("EMP_DENIED_COUNT", rs.getString("count"));
					total += uF.parseToInt(rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("approved")) == -2) {
					request.setAttribute("EMP_WAITING_COUNT", rs.getString("count"));
					total += uF.parseToInt(rs.getString("count"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("TOTAL", total + "");
			//cst.close();

//			pst = con.prepareStatement(selectApprovals);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
			List alReasons = new ArrayList();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_manager_exceptions(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectApprovals);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while (rs.next()) {

				if ("IN".equalsIgnoreCase(rs.getString("in_out"))) {
					if (rs.getDouble("early_late") > 0) {
						alReasons.add(rs.getString("emp_fname") + ", is late for office and the provided reason is " + rs.getString("reason") + " reason");
					} else if (rs.getDouble("early_late") < 0) {
						alReasons.add(rs.getString("emp_fname") + ", has come early because " + rs.getString("reason"));
					}

				} else {
					if (rs.getDouble("early_late") > 0) {
						alReasons.add(rs.getString("emp_fname") + ", has left late because " + rs.getString("reason"));
					} else if (rs.getDouble("early_late") < 0) {
						alReasons.add(rs.getString("emp_fname") + ", has left early because " + rs.getString("reason"));
					}
				}
			}
			rs.close();
			pst.close();
			//cst.close();

			request.setAttribute("alReasons", alReasons);

//			pst = con.prepareStatement(selectRosterEmployeeDetails);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//
//			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_summary(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectRosterEmployeeDetails);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			Map hmRoster = new LinkedHashMap();
			Map hmRoster1 = new LinkedHashMap();
			Map hm1 = new HashMap();
			String strOldDate = null;
			String strNewDate;
			String strServiceId = null;
			alServices = new ArrayList();

			while (rs.next()) {
				hm1 = new HashMap();
				strNewDate = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());
				strServiceId = rs.getString("service_id");

				if (strNewDate != null && !strNewDate.equalsIgnoreCase(strOldDate)) {
					hm1 = new HashMap();
				}
				hm1.put("FROM", uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put("TO", uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put("SERVICE", (String) hmServices.get(strServiceId));

				alServices = (List) hmRoster1.get(strNewDate);
				if (alServices == null) {
					alServices = new ArrayList();
				}
				if (!alServices.contains(strServiceId)) {
					alServices.add(strServiceId);
				}

				hmRoster1.put(strNewDate, alServices);

				hmRoster.put(strNewDate + "_" + strServiceId, hm1);
				strOldDate = strNewDate;
			}
			rs.close();
			pst.close();
			//cst.close();
			request.setAttribute("hmRoster", hmRoster);
			request.setAttribute("hmRoster1", hmRoster1);

			
			verifyClockDetails();
			
			getBirthdays(con, uF, hmEmpWlocationMap, hmWlocationMap, hmEmployeeMap, hmDepartmentMap, hmEmpDepartmentMap);
			getMailCount(con, uF);
			getTasksCount(con, uF);
			getPaySlipStatus(con, uF);
			getPendingExceptionCount(con, uF);
			getApprovedExceptionCount(con, uF);
			getRosterStatus(con, uF);
			getBusinessRuleStatus(con, uF);
//			getResignationStatus(con, uF);
			getProbationStatus(con, uF);
			getDayThought(con, uF);
			
			if(strJoiningDate!=null){
				uF.getTimeDuration(strJoiningDate, CF, uF, request);
			}
			getWorkedHours(con, uF);
			getEmpKPI(con, uF, (String) session.getAttribute("EMPID"));
			getRosterVsWorkedHours(con, uF);
			getEmpLeaveCounts(con, uF);
			getEmpServiceWorkingHourCounts(con,uF);
			getTaskDetails(con,uF, hmEmployeeMap);
			
			List alSkills = new ArrayList();
			List<String> strSkillsList = CF.selectEmpSkills(con,uF.parseToInt((String) session.getAttribute(EMPID)));
			
			
			pst = con.prepareStatement("select * from roster_details rd left join attendance_details ad on rd.emp_id = ad.emp_id and rd.service_id = ad.service_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') where rd.emp_id = ? and rd._date = ?");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
			
				if(rs.getString("in_out")==null && isRosterDependent){
					session.setAttribute("clock_inout_reminder", "Your roster time is "+uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeAM_PMFormat())+", please clock on now!<br/>");
				}
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strSkillsList", strSkillsList);
			request.setAttribute("alSkills", alSkills);
			
			
//			log.debug("strSkills==>"+strSkills);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(cst);
			db.closeConnection(con);
		}

		return LOAD;
	}
	
	private void getEmpServiceWorkingHourCounts(Connection con,	UtilityFunctions uF) {
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst =null;
		try {
			
			
			Map<String, String> hmThisWeek = new HashMap<String, String>();
			Map<String, String> hmLastWeek = new HashMap<String, String>();
			Map<String, String> hmLastLastWeek = new HashMap<String, String>();
			List<String> alServices = new ArrayList<String>();
			
			
			//This week
			
//			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details " +
//								"WHERE emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
//								"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 7));
//			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 0));
//			
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_service_working_hours_count(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 7));
//			cst.setDate(4, uF.getPrevDate(CF.getStrTimeZone(), 0));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details" +
					"WHERE emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
					"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id;") ; 
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 7));
			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 0));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				if(!alServices.contains(rs.getString("service_name"))){
					alServices.add(rs.getString("service_name"));
				}
				
				hmThisWeek.put(rs.getString("service_name"), rs.getString("hours_worked"));
				
			}
			rs.close();
			pst.close();
			
			//Last week
			
//			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details " +
//								"WHERE emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
//								"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 14));
//			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 7));
//			
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_service_working_hours_count(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 14));
//			cst.setDate(4, uF.getPrevDate(CF.getStrTimeZone(), 7));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details" +
					"WHERE emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
					"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id") ; 
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 14));
			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 7));
			rs = pst.executeQuery();
			while(rs.next()){
				if(!alServices.contains(rs.getString("service_name"))){
					alServices.add(rs.getString("service_name"));
				}
				
				hmLastWeek.put(rs.getString("service_name"), rs.getString("hours_worked"));
			}
			rs.close();
			pst.close();
			
			//Last to Last week
			
//			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details " +
//								"WHERE emp_id = ? " +
//								"and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
//								"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 21));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 14));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_service_working_hours_count(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 21));
//			cst.setDate(4, uF.getPrevDate(CF.getStrTimeZone(), 14));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM ( SELECT service_id, sum(hours_worked) as hours_worked FROM attendance_details" +
					"WHERE emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?" +
					"GROUP BY service_id) asd LEFT JOIN services sd ON asd.service_id = sd.service_id ") ; 
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 21));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 14));
			rs = pst.executeQuery();
			
			while(rs.next()) {
				if(!alServices.contains(rs.getString("service_name"))){
					alServices.add(rs.getString("service_name"));
				}
				
				hmLastLastWeek.put(rs.getString("service_name"), rs.getString("hours_worked"));
			}
			rs.close();
			pst.close();
			
			
			
			StringBuilder sb = new StringBuilder();

			sb.append("series:[");
			
			for(int i=0; i<alServices.size(); i++){
				String strServiceName = alServices.get(i);
				if(strServiceName==null){
					continue;
				}
				
				
				sb.append("{" +
						"name: '"+strServiceName+"'," +
						"data: ["+uF.parseToDouble(hmThisWeek.get(strServiceName))+", "+uF.parseToDouble(hmLastWeek.get(strServiceName))+", "+uF.parseToDouble(hmLastLastWeek.get(strServiceName))+"]" +
						"}");
				
				
				if(i!=alServices.size()-1){
					sb.append("," );
				}
				
			}
			sb.append("]");
			
			
			
			
//			sb.append(
//					"series: [{" +
//					"name: 'Accounts'," +
//					"data: [90, 45, 0]" +
//					"}, {" +
//					"name: 'KT'," +
//					"data: [40, 0, 45]" +
//					"}, {" +
//					"name: 'KM'," +
//					"data: [0, 0, 100]" +
//					"}]"
//					);
		   
		      request.setAttribute("ServiceWorkingHours", sb.toString());
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}  finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public static final String selectEmployeeLeaveEntryPerEmp =  "select * from ( Select ee.emp_id,ee.leave_type_id,et.no_of_leave, " +
								"et.effective_date_type, et.effective_date, sum(emp_no_of_leave)as taken, " +
								"is_approved  from emp_leave_entry ee right join emp_leave_type et	" +
								"on ee.leave_type_id=et.leave_type_id where emp_id = ? " +
								"group by ee.emp_id,et.no_of_leave,ee.leave_type_id, is_approved, et.effective_date_type, et.effective_date) a, leave_type lt " +
								"where a.leave_type_id = lt.leave_type_id " +
								"order by lt.leave_type_name, lt.leave_type_id";
	
	private void getEmpLeaveCounts(Connection con, UtilityFunctions uF) {

		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
		
			Map<String, String> hmEmpJoiningDateMap = CF.getEmpJoiningDateMap(con, uF);
			Map<String, String> hmEmployeeCarryForwardMap = new HashMap<String, String>();
			
			List<String> alLeaveType = new ArrayList<String>();
			
//			pst = con.prepareStatement(selectEmpLeaveType);	
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_leave_count()}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectEmpLeaveType);	
			rs = pst.executeQuery();
			while(rs.next()){
				if(!alLeaveType.contains(rs.getString("leave_type_id"))){
					alLeaveType.add(rs.getString("leave_type_id"));
				}
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement(selectEmployeeLeaveEntryPerEmp);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			log.debug("pst selectEmployeeLeaveEntryPerEmp=>"+pst);
//			rs = pst.executeQuery();
//			
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_leave_count(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.setInt(3, uF.parseToInt(hmEmpLevel.get((String) session.getAttribute("EMPID"))));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("select * from ( Select ee.emp_id,ee.leave_type_id,et.no_of_leave, et.effective_date_type, et.effective_date, " +
					"sum(emp_no_of_leave)as taken,is_approved  from emp_leave_entry ee right join emp_leave_type et on ee.leave_type_id=et.leave_type_id " +
					"where emp_id = ? and level_id = ? group by ee.emp_id,et.no_of_leave,ee.leave_type_id, is_approved, et.effective_date_type, " +
					"et.effective_date) a,leave_type lt where a.leave_type_id = lt.leave_type_id order by lt.leave_type_name, lt.leave_type_id");
			pst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setInt(3, uF.parseToInt(hmEmpLevel.get((String) session.getAttribute("EMPID"))));
			rs = pst.executeQuery();
			
			String strLeaveTypeNew = null;
			String strLeaveTypeOld = null;
			double dblPending 	= 0;
			double dblTotalPending 	= 0;
			double dblApproved 	= 0;
			double dblDenied 	= 0;
			double dblRemaining 	= 0;
			double dblTotal 	= 0;
			int count = -1;
			
			
			Map<String, String> hmLeaveBalance = CF.getBalanceLeaveMap(con,(String) session.getAttribute("EMPID"), CF);
			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			Map hmLeaveType = new HashMap();
			Map<String, String> hmInner = new HashMap<String, String>();
			
			while (rs.next()) {
				
				strLeaveTypeNew = rs.getString("leave_type_id");
				if(strLeaveTypeNew!=null && !strLeaveTypeNew.equalsIgnoreCase(strLeaveTypeOld)){
					
					count++;
					dblRemaining = 0;
					dblApproved = 0;
					dblPending = 0;
					dblDenied = 0;
				
					if(rs.getString("effective_date_type")!=null && rs.getString("effective_date_type").equalsIgnoreCase("CY")){ 
						dblTotal = CF.getLeavesCount(hmEmpJoiningDateMap.get(rs.getString("emp_id")), uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT), rs.getInt("no_of_leave"), uF.parseToBoolean(hmEmployeeCarryForwardMap.get(rs.getString("leave_type_name"))), CF);
					}else if(rs.getString("effective_date_type")!=null && rs.getString("effective_date_type").equalsIgnoreCase("FY")){ 
						dblTotal = CF.getLeavesCount(hmEmpJoiningDateMap.get(rs.getString("emp_id")), uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT), rs.getInt("no_of_leave"), uF.parseToBoolean(hmEmployeeCarryForwardMap.get(rs.getString("leave_type_name"))), CF);
					}
					
					hmInner = new HashMap<String, String>();
					
				}
				
				if(uF.parseToInt(rs.getString("is_approved")) == 0){
					dblPending = uF.parseToDouble(hmInner.get("PENDING")); 
					dblPending += rs.getDouble("taken");
					hmInner.put("PENDING", dblPending+"");
					dblTotalPending +=dblPending;
				}if(uF.parseToInt(rs.getString("is_approved")) == 1){
					dblApproved = uF.parseToDouble(hmInner.get("APPROVED")); 
					dblApproved += rs.getDouble("taken");
					hmInner.put("APPROVED", dblApproved+"");
				}if(uF.parseToInt(rs.getString("is_approved")) == -1){
					dblDenied = uF.parseToDouble(hmInner.get("DENIED")); 
					dblDenied += rs.getDouble("taken");
					hmInner.put("DENIED", dblDenied+"");
				}
				  
				dblRemaining =dblTotal- dblPending - dblApproved ;
				hmInner.put("BALANCE", dblRemaining+"");
			
				
				
				hmLeaveType.put(rs.getString("leave_type_id"), hmInner);
				
				
				strLeaveTypeOld = strLeaveTypeNew;
				
			}
			rs.close();
			pst.close();
			
		
			
			
			
			log.debug("hmLeaveType"+hmLeaveType);
			log.debug("alLeaveType"+alLeaveType);
			
			
//			System.out.println("hmLeaveType===>"+hmLeaveType);
			
			
			StringBuilder sbLeaveType = new StringBuilder();
			StringBuilder sbLeaveDenied = new StringBuilder();
			StringBuilder sbLeavePending = new StringBuilder();
			StringBuilder sbLeaveAppoved = new StringBuilder();
			StringBuilder sbLeaveBalance = new StringBuilder();
			
			for(int i=0; i<alLeaveType.size();i++){
				hmInner = (Map)hmLeaveType.get((String)alLeaveType.get(i));
				if(hmInner==null){
					hmInner = new HashMap();
				}
				
				sbLeaveType.append("'"+hmLeaveTypeMap.get((String)alLeaveType.get(i))+"'");
				
				
				sbLeaveDenied.append(uF.parseToDouble((String)hmInner.get("DENIED")));
				sbLeavePending.append(uF.parseToDouble((String)hmInner.get("PENDING")));
				sbLeaveAppoved.append(uF.parseToDouble((String)hmInner.get("APPROVED")));
				sbLeaveBalance.append(uF.parseToDouble((String)hmLeaveBalance.get((String) session.getAttribute("EMPID")+"_"+(String)alLeaveType.get(i))));
				if(i<alLeaveType.size()-1){
					sbLeaveType.append(",");
					sbLeaveDenied.append(",");
					sbLeavePending.append(",");
					sbLeaveAppoved.append(",");
					sbLeaveBalance.append(",");
				}
			}
			
			
			
			
			request.setAttribute("TYPE", sbLeaveType.toString());	
			request.setAttribute("DENIED", sbLeaveDenied.toString());
			request.setAttribute("PENDING", sbLeavePending.toString());
			request.setAttribute("APPROVED", sbLeaveAppoved.toString());
			request.setAttribute("BALANCE", sbLeaveBalance.toString());
			request.setAttribute("LEAVE_PENDING_COUNT", dblTotalPending+"");
			
			request.setAttribute("hmLeaveBalance", hmLeaveBalance);
			
			
			
//			pst = con.prepareStatement("select * from emp_leave_entry where emp_id = ? and approval_from>=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_leave_entry(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("select * from emp_leave_entry where emp_id = ? and approval_from>=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			int approvedLeaveCount=0;
			int deniedLeaveCount=0;
			int pendingLeaveCount=0;
			int totalLeaveCount=0;   
			while (rs.next()) {
					if(rs.getInt("is_approved")==1){
						approvedLeaveCount++;
						totalLeaveCount++;
					}
					if(rs.getInt("is_approved")==-1){
						deniedLeaveCount++;
						totalLeaveCount++;
					}
					if(rs.getInt("is_approved")==0){
						pendingLeaveCount++;
					}
			}
			rs.close();
			pst.close();
			session.setAttribute("LEAVE_APPROVAL_DENIED_COUNT", totalLeaveCount+"");
			request.setAttribute("LEAVE_APPROVED_COUNT", approvedLeaveCount+"");
			request.setAttribute("LEAVE_DENIED_COUNT", deniedLeaveCount+"");
			request.setAttribute("LEAVE_PENDING_COUNT", pendingLeaveCount+"");
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
		
	}
	
	public void getBirthdays(Connection con, UtilityFunctions uF, Map<String, String> hmEmpWlocationMap, Map<String, Map<String, String>> hmWlocationMap, Map<String, String> hmEmployeeMap, Map hmDepartmentMap, Map hmEmpDepartmentMap){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-dd");
			
			pst = con.prepareStatement(selectBirthDay);			
			
			pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			
			rs = pst.executeQuery();

			List alBirthDays = new ArrayList();
			while (rs.next()) {
				String strBDate = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM-dd");
				String strWlocationId = hmEmpWlocationMap.get(rs.getString("emp_per_id"));
				String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"", DBDATE, "MM-dd");
				String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2)+"", DBDATE, "MM-dd");
				
				Map hmWlocation = hmWlocationMap.get(strWlocationId);
				if(hmWlocation==null)hmWlocation=new HashMap();
				
				String strCity = (String)hmWlocation.get("WL_CITY");
				String gender = rs.getString("emp_gender");
				if(strBDate!=null && strBDate.equals(strToday1)){
					if(hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						if(gender!=null && gender.equalsIgnoreCase("M")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish him...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today wish him...!");
						}else if(gender!=null && gender.equalsIgnoreCase("F")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish her...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today wish her...!");
						}else{
						//	alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today...!");
						}
					}
				}
				
				if(strBDate!=null && strBDate.equals(strTomorrow)) {
					if(hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						if(gender!=null && gender.equalsIgnoreCase("M")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish him...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow wish him...!");
						}else if(gender!=null && gender.equalsIgnoreCase("F")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish her...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow wish her...!");
						}else{
						//	alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow...!");
						}
					}
				}
			}
			rs.close();
			pst.close();
//			cst.close();
			request.setAttribute("alBirthDays",alBirthDays);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getWorkedHours(Connection con, UtilityFunctions uF){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
//			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id =?");
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_worked_hours(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id =?");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			double hrsWorked = 0;
			while(rs.next()){
				hrsWorked = rs.getDouble("hours_worked");
			}
			rs.close();
			pst.close();
			request.setAttribute("HRS_WORKED",uF.formatIntoComma(hrsWorked));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getEmpKPI(Connection con, UtilityFunctions uF, String strEmpIdReq){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

//			pst = con.prepareStatement(selectPresentDays1);
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
//			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
//
//			rs = pst.executeQuery();
//			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpIdReq));
//			cst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
//			cst.setDate(4, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT count(distinct TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD')) as count FROM attendance_details " +
					"WHERE emp_id =? and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ?");
			pst.setInt(1, uF.parseToInt(strEmpIdReq));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
			rs = pst.executeQuery();

			double[] PRESENT_ABSENT_DATA = new double[2];
			String[] PRESENT_ABSENT_LABEL = new String[2];
			
			
			if (rs.next()) {
				request.setAttribute("PRESENT_COUNT", rs.getString("count"));
				request.setAttribute("ABSENT_COUNT", Day - uF.parseToInt(rs.getString("count")));

				PRESENT_ABSENT_DATA[0] = rs.getDouble("count");
				PRESENT_ABSENT_DATA[1] = Day - uF.parseToInt(rs.getString("count"));

				PRESENT_ABSENT_LABEL[0] = "Worked";
				PRESENT_ABSENT_LABEL[1] = "Absent";
			}
			rs.close();
			pst.close();

			request.setAttribute("CHART_WORKED_ABSENT", new PieCharts().get3DPieChart(PRESENT_ABSENT_DATA, PRESENT_ABSENT_LABEL));
			
			

//			pst = con.prepareStatement(selectPresentDays1);
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpIdReq));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			cst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			
			pst = con.prepareStatement("SELECT count(distinct TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD')) as count FROM attendance_details " +
					"WHERE emp_id =? and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ?");
			pst.setInt(1, uF.parseToInt(strEmpIdReq));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();

			double []PRESENT_ABSENT_DATA_MONTH = new double[2];
			String []PRESENT_ABSENT_LABEL_MONTH = new String[2];
			
			double dblPresentCount = 0;
			
			if (rs.next()) {
				
				dblPresentCount = uF.parseToDouble(rs.getString("count"));
//				
//				PRESENT_ABSENT_DATA_MONTH[0] = rs.getDouble("count");
//				PRESENT_ABSENT_DATA_MONTH[1] = 30 - uF.parseToInt(rs.getString("count"));
//				
//				PRESENT_ABSENT_LABEL_MONTH[0] = "Worked";
//				PRESENT_ABSENT_LABEL_MONTH[1] = "Absent";
			}
			rs.close();
			pst.close();
			
			
			
//			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id = ? and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ?");
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();

			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_worked_hours(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpIdReq));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			cst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked FROM attendance_details WHERE emp_id = ? and TO_DATE (in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ?");
			pst.setInt(1, uF.parseToInt(strEmpIdReq));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			double []WORKED_HOURS_DATA_MONTH = new double[2];
			double dblWorkedHours = 0;
			
			if (rs.next()) {
				
//				WORKED_HOURS_DATA_MONTH[0] = uF.parseToDouble(rs.getString("hours_worked"));
				dblWorkedHours = uF.parseToDouble(rs.getString("hours_worked"));
			}
			rs.close();
			pst.close();
			
			double dblStandardHours = 8;
			
			
//			pst = con.prepareStatement("SELECT sum(actual_hours) as actual_hours FROM roster_details WHERE emp_id =? and _date BETWEEN ? AND ?");
//			pst.setInt(1, uF.parseToInt(strEmpIdReq));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_hours(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpIdReq));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			cst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT sum(actual_hours) as actual_hours FROM roster_details WHERE emp_id =? and _date BETWEEN ? AND ?");
			pst.setInt(1, uF.parseToInt(strEmpIdReq));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			double []ACTUAL_HOURS_DATA_MONTH = new double[2];
			double dblActualHours = 0;
			if (rs.next()) {
				dblActualHours = uF.parseToDouble(rs.getString("actual_hours"));
				
				if(dblActualHours==0){
					dblActualHours = dblStandardHours * dblPresentCount;
				}
//				ACTUAL_HOURS_DATA_MONTH[0] = uF.parseToDouble(rs.getString("actual_hours"));
			}
			rs.close();
			pst.close();
			
			
			 
			
			
			PRESENT_ABSENT_DATA_MONTH[0] = dblWorkedHours;
			PRESENT_ABSENT_DATA_MONTH[1] = dblActualHours - dblWorkedHours;
			
			PRESENT_ABSENT_LABEL_MONTH[0] = "Worked";
			PRESENT_ABSENT_LABEL_MONTH[1] = "Actual";
			
			
			
			request.setAttribute("KPI", new SemiCircleMeter().getSemiCircleChart(PRESENT_ABSENT_DATA_MONTH, PRESENT_ABSENT_LABEL_MONTH));
			request.setAttribute("KPIZ", new LinearZMeter().getLinearChart(PRESENT_ABSENT_DATA_MONTH, PRESENT_ABSENT_LABEL_MONTH));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getRosterVsWorkedHours(Connection con, UtilityFunctions uF){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
//			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours, (hours_worked - actual_hours) as variance, to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date FROM attendance_details ad, roster_details rd WHERE rd.emp_id=ad.emp_id and to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') = rd._date and ad.service_id = rd.service_id and ad.emp_id =? and in_out = 'OUT' group by to_date(in_out_timestamp::text, 'YYYY-MM-DD') , variance order by to_date(in_out_timestamp::text, 'YYYY-MM-DD') desc limit 7");
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_actual_hours(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours,(hours_worked - actual_hours) as variance," +
					" to_date(in_out_timestamp::text, 'YYYY-MM-DD') as _date FROM attendance_details ad, roster_details rd WHERE rd.emp_id=ad.emp_id and " +
					"to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') = rd._date and ad.service_id = rd.service_id and ad.emp_id =? and in_out = 'OUT' " +
					"group by to_date(in_out_timestamp::text, 'YYYY-MM-DD') , variance order by to_date(in_out_timestamp::text, 'YYYY-MM-DD') desc limit 7");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			java.util.Date dtPrev7Days = uF.getPrevDate(CF.getStrTimeZone(), 7);
			java.util.Date dtCurrent = null;
			
			List<String> alWorkedHours = new ArrayList<String>();
			List<String> alRosterHours = new ArrayList<String>();
			List<String> alVarianceHoursE = new ArrayList<String>();
			List<String> alVarianceHoursL = new ArrayList<String>();
			List<String> alLabel = new ArrayList<String>();
			
			while(rs.next()){
				
				dtCurrent = uF.getDateFormatUtil(rs.getString("_date"), DBDATE);
				
				alWorkedHours.add(rs.getString("hours_worked"));
				alRosterHours.add(rs.getString("actual_hours"));
				if(rs.getDouble("variance")>=0){
					alVarianceHoursE.add(rs.getString("variance"));
					alVarianceHoursL.add("0");
				}else{
					alVarianceHoursE.add("0");
					alVarianceHoursL.add(rs.getString("variance"));
				}
				
				
				if(dtCurrent.before(dtPrev7Days)){
					alLabel.add("\'"+uF.getDateFormat(rs.getString("_date"), DBDATE, "dd/MMM")+"\'");
				}else{
					alLabel.add("\'"+uF.getDateFormat(rs.getString("_date"), DBDATE, "E")+"\'");	
				}
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alWorkedHours",alWorkedHours);
			request.setAttribute("alRosterHours",alRosterHours);
			request.setAttribute("alVarianceHoursE",alVarianceHoursE);
			request.setAttribute("alVarianceHoursL",alVarianceHoursL);
			request.setAttribute("alLabel",alLabel);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	} 
	
	public void getTaskDetails(Connection con, UtilityFunctions uF, Map hmEmployeeMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			
					
			Map hmProjectTeamLead = new HashMap();
			pst = con.prepareStatement("select * from project_emp_details where _isteamlead = true");
			rs = pst.executeQuery();
			while(rs.next()){
				hmProjectTeamLead.put(rs.getString("pro_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select activity_name,ai.pro_id,pro_name,ai.completed,ai.deadline,ai.already_work  from projectmntnc pmc, activity_info ai where pmc.pro_id=ai.pro_id and ai.emp_id = ? and ai.approve_status='n' order by ai.deadline limit 4");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			
			List alTaskList = new ArrayList();
			List alTaskInner = new ArrayList();
			
//			System.out.println("pst===>"+pst);
			
			while(rs.next()){
				
				Date currentDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date deadLineDate = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				
				alTaskInner = new ArrayList();
				
				alTaskInner.add(rs.getString("activity_name") +" ["+rs.getString("pro_name")+"]");
				
				if(uF.parseToInt(rs.getString("completed"))>=100){
					alTaskInner.add("Completed");
				}else{
					
					if(currentDate!=null && deadLineDate!=null && currentDate.after(deadLineDate)){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/denied.png\" border=\"0\"><span style=\"color:red\">Overdue</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i><span style=\"color:red\">Overdue</span>");
						
					}else{
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/approved.png\" border=\"0\"><span style=\"color:green\">Working</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i><span style=\"color:green\">Working</span>");
					}
				}
				
				alTaskInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				alTaskInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work"))));
				
				
				
				alTaskInner.add(uF.showData((String)hmEmployeeMap.get((String)hmProjectTeamLead.get(rs.getString("pro_id"))), ""));
				
				alTaskList.add(alTaskInner);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alTaskList",alTaskList);
//			System.out.println("alTaskList===>"+alTaskList);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getMailCount(Connection con, UtilityFunctions uF){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
//			pst = con.prepareStatement(getUnreadMailCount);			
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_unread_mail_count(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(getUnreadMailCount);			
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			int nMailCount = 0;
			while (rs.next()) {
				nMailCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			//cst.close();
			request.setAttribute("MAIL_COUNT",nMailCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getTasksCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select count(*) as count from activity_info where emp_id = ? and approve_status = 'n'");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			
			int nTaskCount = 0;
			while (rs.next()) {
				nTaskCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("TASK_COUNT",nTaskCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getPaySlipStatus(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select count(*) as count from payroll_generation where emp_id=? and entry_date between ? and ?");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 7));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			int nPaySlipGeneration = 0;
			while (rs.next()) {
				nPaySlipGeneration = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("PAYSLIP_GENERATION",nPaySlipGeneration+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}  
	}
	
	public void getRosterStatus(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select count(*) as count from roster_details where emp_id=? and entry_date = ?");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			int nRosterStatus = 0;
			while (rs.next()) {
				nRosterStatus = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("ROSTER_STATUS",nRosterStatus+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}  
	}
		
	public void getBusinessRuleStatus(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select count(*) as count from roster_policy where entry_date = ?");			
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			int nBusinessRuleStatus = 0;
			while (rs.next()) {
				nBusinessRuleStatus = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
			
			if(nBusinessRuleStatus==0){
				pst = con.prepareStatement("select count(*) as count from roster_halfday_policy where entry_date = ?");			
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				
				nBusinessRuleStatus = 0;
				while (rs.next()) {
					nBusinessRuleStatus = rs.getInt("count");
				}
				rs.close();
				pst.close();
			}
			
			
			session.setAttribute("BUSINESS_RULE_STATUS",nBusinessRuleStatus+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}  finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}

	
	

	public void getPendingExceptionCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			
			
			if(!CF.isRosterDependency(con,strEmpId))return;
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));

			int nMinDate = cal.getActualMinimum(Calendar.DATE);
			int nMaxDate = cal.getActualMaximum(Calendar.DATE);
			
			String strDate1 = nMinDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			String strDate2 = nMaxDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			
			
			
			
			
			Map<String, String> hmExceptionDates = new HashMap<String, String>();
			
			pst = con.prepareStatement("select * from exception_reason where _date between ? and ? and emp_id = ?");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				hmExceptionDates.put(rs.getString("_date"), rs.getString("status"));
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select * from roster_details rd left join ( select * from attendance_details where to_date(in_out_timestamp::text,'YYYY-MM-DD') between ? and ? and emp_id = ? order by to_date(in_out_timestamp::text,'YYYY-MM-DD'))a on rd.emp_id = a.emp_id and rd._date = to_date(a.in_out_timestamp::text,'YYYY-MM-DD') and rd.service_id = a.service_id where _date between ? and ? and rd.emp_id = ? order by _date desc");			
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setDate(4, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
//			System.out.println("pst======>"+pst);
			int nPendingExceptionCount = 0;
			int nWaitingExceptionCount = 0;
			
			String strCurrentDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			
			String strDateNew = null;
			String strDateOld = null;
			boolean isIn = false;
			boolean isOut = false;
			while (rs.next()) {
				
				strDateNew = rs.getString("_date");
				String strApproval  = rs.getString("approved");
				
				if(strDateNew!=null && strDateOld!=null && !strDateNew.equals(strDateOld)){
					if(!isIn || !isOut){
						int nStatus = uF.parseToInt((String)hmExceptionDates.get(strDateOld));
						
						if(nStatus==0 && hmExceptionDates.containsKey(strDateOld)){
							nPendingExceptionCount++;
						}else if(strCurrentDate.equals(strDateOld) && isIn){
							
						}else if(nStatus==0){
							nWaitingExceptionCount++;
						}
					}
					isIn = false;
					isOut = false;
				}
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					isIn = true;
				}else if("OUT".equalsIgnoreCase(rs.getString("in_out"))){
					isOut = true;
				}
				
				
				if(uF.parseToInt(strApproval)==-2){
					nPendingExceptionCount++;
//					System.out.println("Date= IN ==>"+rs.getString("_date")+" "+rs.getString("in_out"));
				}
				
				/*if(rs.getString("approved")==null || (rs.getString("approved")!=null && rs.getString("approved").length()==0)){
					int nStatus = uF.parseToInt((String)hmExceptionDates.get(rs.getString("_date")));
					if(nStatus==0){
						nPendingExceptionCount++;
						System.out.println("Date= IN ==>"+rs.getString("_date")+" "+rs.getString("in_out"));
					}
				}*/
				
				
				strDateOld = strDateNew;
				
			}
			rs.close();
			pst.close();
			
			
			if(strDateNew!=null && strDateOld!=null && strDateNew.equals(strDateOld)){
				if(!isIn || !isOut){
					int nStatus = uF.parseToInt((String)hmExceptionDates.get(strDateOld));
					
					if(nStatus==0 && hmExceptionDates.containsKey(strDateOld)){
						nPendingExceptionCount++;
					}else if(strCurrentDate.equals(strDateOld) && isIn){
						
					}else if(nStatus==0){
						nWaitingExceptionCount++;
					}
				}
				isIn = false;
				isOut = false;
			}
			
			
			
			session.setAttribute("PENDING_EXCEPTION_COUNT",(nPendingExceptionCount+nWaitingExceptionCount)+"");
			request.setAttribute("PENDING_EXCEPTION_COUNT",nPendingExceptionCount+"");
			request.setAttribute("WAITING_EXCEPTION_COUNT",nWaitingExceptionCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void getApprovedExceptionCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {

			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));

			int nMinDate = cal.getActualMinimum(Calendar.DATE);
			int nMaxDate = cal.getActualMaximum(Calendar.DATE);
			
			String strDate1 = nMinDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			String strDate2 = nMaxDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			
			pst = con.prepareStatement("select count(*) as count from attendance_details where emp_id = ? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and approved = 1");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strDate2, DATE_FORMAT));
			rs = pst.executeQuery();
			int nApprovedExceptionCount = 0;
			while (rs.next()) {
				nApprovedExceptionCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("APPROVED_EXCEPTION_COUNT",nApprovedExceptionCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void getResignationStatus(Connection con, UtilityFunctions uF){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst =null;
		try {
			
//			pst = con.prepareStatement("select * from emp_off_board where emp_id =? order by entry_date desc limit 1");			
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_resignation_status(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("select * from emp_off_board where emp_id =? order by entry_date desc limit 1");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			String strResignationStatus = null;
			String strResigDate = null;
			while (rs.next()) {
				
				if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1){
					if(rs.getString("off_board_type") != null && rs.getString("off_board_type").trim().equalsIgnoreCase("TERMINATED")) {
						strResignationStatus = "Terminated";
						
					}else if(rs.getString("off_board_type") != null && rs.getString("off_board_type").trim().equalsIgnoreCase("RESIGNED")) {
						strResignationStatus = "Your resignation has been accepted";
						
					}
					request.setAttribute("RESIG_STATUS", "1");
				}else if(rs.getInt("approved_1")==-1 || rs.getInt("approved_2")==-1){
					strResignationStatus = "Your resignation has been denied";
				}else if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==0){
					strResignationStatus = "Your resignation has been approved by your manager and is waiting for HR's approval";
				}else if(rs.getInt("approved_1")==0 && rs.getInt("approved_2")==1){
					strResignationStatus = "Your resignation has been approved by your HR and is waiting for manager's approval";
				}else if(rs.getString("off_board_type")!=null && rs.getString("off_board_type").equalsIgnoreCase("TERMINATED")){
					strResignationStatus = "Terminated";
					
				} else if((rs.getInt("approved_1")==0 || rs.getInt("approved_2")==0) && rs.getString("off_board_type")!=null && !rs.getString("off_board_type").equalsIgnoreCase("TERMINATED")) {
					strResignationStatus = "Resigned & waiting for approval";
					
				}
				
				if(rs.getString("entry_date")!=null){
					strResigDate = uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, DBDATE);
				}
			}
			rs.close();
			pst.close();
//			cst.close();
			
			
			/*pst = con.prepareStatement("select * from employee_activity_details where emp_id = ? and emp_activity_id =(select max(emp_activity_id) from employee_activity_details  where emp_id = ?)");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			int nNotice = 0;
			while(rs.next()){
				nNotice = rs.getInt("notice_period");
			}
			rs.close();
			pst.close();*/
			
			int nNotice = CF.getEmpNoticePeriod(con,strEmpId);
			
			int nDifference = 0;
			int nRemaining = 0;
			String lastDate = "";
			String resigData = "";
			if(strResigDate!=null){
				/*nDifference = uF.parseToInt(uF.dateDifference(strApprovedDate, DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
				nRemaining = nNotice - nDifference;*/
				String regDate = uF.getDateFormat(strResigDate, DBDATE,DATE_FORMAT);
								
				lastDate = uF.getDateFormat(""+uF.getBiweeklyDate(regDate, nNotice),DBDATE,CF.getStrReportDateFormat());
				String ldate = uF.getDateFormat(""+uF.getBiweeklyDate(regDate, nNotice),DBDATE,DATE_FORMAT);

				java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
				java.util.Date lstDate = uF.getDateFormatUtil(ldate,DATE_FORMAT );
				nDifference = uF.parseToInt(uF.dateDifference(strResigDate, DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
				
			
				if(lstDate.after(currDate)) {
					nRemaining = nNotice - nDifference;
					resigData = nRemaining + " days remaining";
				} else if(lstDate.before(currDate)) {
					resigData = " last day  "+lastDate;
				} else if(lstDate.equals(currDate)) {
					resigData = " Today is last day  ";
				}
			}
			
			
			request.setAttribute("RESIGNATION_STATUS",strResignationStatus);
			request.setAttribute("RESIGNATION_REMAINING",resigData);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void getProbationStatus(Connection con, UtilityFunctions uF){
		
		CallableStatement cst  = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			String strJoiningDate = null;
			while(rs.next()){
				strJoiningDate = rs.getString("joining_date");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_activity_details where emp_id = ? and emp_activity_id =(select max(emp_activity_id) from employee_activity_details  where emp_id = ?)");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			int nProbation = 0;
			while(rs.next()){
				nProbation = rs.getInt("probation_period");
			}
			rs.close();
			pst.close();
			 
			int nDifference = uF.parseToInt(uF.dateDifference(strJoiningDate, DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE,CF.getStrTimeZone()));
			int nRemaining = nProbation - nDifference;
			
			request.setAttribute("PROBATION_REMAINING",nRemaining+"");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void getDayThought(Connection con, UtilityFunctions uF){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
//			pst = con.prepareStatement(selectThought);			
//			pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_thought(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, cal.get(Calendar.DAY_OF_YEAR));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectThought);			
			pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
			rs = pst.executeQuery();
			
			String strThought = null;
			String strThoughtBy = null;
			while (rs.next()) {
				strThought = rs.getString("thought_text");
				strThoughtBy = rs.getString("thought_by"); 
			}
			rs.close();
			pst.close();
//			cst.close();
			request.setAttribute("DAY_THOUGHT_TEXT",strThought);
			request.setAttribute("DAY_THOUGHT_BY",strThoughtBy);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	public void verifyClockDetails(){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		CallableStatement cst = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			
			con = db.makeConnection(con);
			
			
			String strPrevRosterDate = null;
			Time tPrevFrom = null;
			Time tPrevTo = null;
			
			String strRosterDate = null;
			String strFrom = null;
			String strTo = null;
			
			String strRosterStartTime = null;
			String strRosterEndTime = null;
			String strPrevRosterStartTime = null;
			String strPrevRosterEndTime = null;
			
			
			int nPrevServiceId = 0;
			int nCurrServiceId = 0;
			int nCount = 0;
			
			
			
//			pst = con.prepareStatement(selectRoster_N_COUNT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_count(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectRoster_N_COUNT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nCount = rs.getInt("cnt");
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectRosterClockDetails_PREV_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				nPrevServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				nCurrServiceId = rs.getInt("service_id");
			}
			rs.close();
			pst.close();
			
			
			if(nCurrServiceId==0){
				if(nCount>1){
//					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					
					
//					con.setAutoCommit(false);
//					cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_in(?,?,?)}");
//					cst.registerOutParameter(1, Types.OTHER);
//					cst.setInt(2, uF.parseToInt(strEmpId));
//					cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//					cst.setTime(4, uF.getCurrentTime(CF.getStrTimeZone()));
					pst = con.prepareStatement(selectRosterClockDetails_N_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
					
				}else{
//					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					
					
//					con.setAutoCommit(false);
//					cst = con.prepareCall("{? = call sel_emp_roster(?,?)}");
//					cst.registerOutParameter(1, Types.OTHER);
//					cst.setInt(2, uF.parseToInt(strEmpId));
//					cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst = con.prepareStatement(selectRosterClockDetails_N1_IN);
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				}
				
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				rs = pst.executeQuery();
				while(rs.next()){
					nCurrServiceId = rs.getInt("service_id");
				}
				rs.close();
				pst.close();
			}
			
			
			if(nCurrServiceId==0){
				nCurrServiceId = nPrevServiceId;
			}
			
			
			
			
//			pst = con.prepareStatement(selectAttendenceClockDetails_N);
//			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//			pst.setString(2, "OUT");
//			pst.setInt(3, uF.parseToInt(strEmpId));
//			pst.setInt(4, nPrevServiceId);
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.setString(3, "OUT");
//			cst.setInt(4, uF.parseToInt(strEmpId));
//			cst.setInt(5, nPrevServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(selectAttendenceClockDetails_N);
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
			pst.setString(2, "OUT");
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, nPrevServiceId);
			rs = pst.executeQuery();

			boolean isPrevOut = false;
			boolean isPrevRoster = false;
			if(rs.next()){
				isPrevOut = true;
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement(selectAttendenceClockDetails_N);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			pst.setString(2, "IN");
//			pst.setInt(3, uF.parseToInt(strEmpId));
//			pst.setInt(4, nCurrServiceId);
//			rs = pst.executeQuery();

			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//			cst.setString(3, "IN");
//			cst.setInt(4, uF.parseToInt(strEmpId));
//			cst.setInt(5, nPrevServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);

			pst = con.prepareStatement(selectAttendenceClockDetails_N);
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
			pst.setString(2, "IN");
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, nPrevServiceId);
			rs = pst.executeQuery();
			
			boolean isCurrIn = false;
			if(rs.next()){
				isCurrIn = true;
			}
			rs.close();
			pst.close();
			
			if(!isCurrIn && !isPrevOut){
				
//				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				
				if(rs.next()){
					isPrevRoster = true;
					
					tPrevFrom = rs.getTime("_from");
					tPrevTo = rs.getTime("_to");
					strPrevRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
				
			}
			
			
			
			
			if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() < tPrevTo.getTime()){
				
//				pst = con.prepareStatement(selectRosterClockDetails_N_IN);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
//				rs = pst.executeQuery();

				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_in(?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				cst.setTime(4, uF.getCurrentTime(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectRosterClockDetails_N_IN);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setTime(3, uF.getCurrentTime(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				strRosterDate = null;
				strFrom = null;
				strTo = null;

				while (rs.next()) {
					strPrevRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strPrevRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
//				cst.close();
			}else if(tPrevFrom!=null && tPrevTo!=null && tPrevFrom.getTime() > tPrevTo.getTime()){
//				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster_clock_entry_out(?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectRosterClockDetails_N_OUT);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				strRosterDate = null;
				strFrom = null;
				strTo = null;

				
				while (rs.next()) {
					strPrevRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strPrevRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
//				cst.close();
			}else{
				
//				pst = con.prepareStatement(selectRosterClockDetails_N1);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setInt(3, nCurrServiceId);
//				rs = pst.executeQuery();
				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_roster(?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setInt(2, uF.parseToInt(strEmpId));
//				cst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//				cst.setInt(4, nCurrServiceId);
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectRosterClockDetails_N1);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, nCurrServiceId);
				rs = pst.executeQuery();
				
				strRosterDate = null;
				strFrom = null;
				strTo = null;

				while (rs.next()) {
					strRosterStartTime = uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat());
					strRosterEndTime = uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat());
					strRosterDate = rs.getString("_date");
				}
				rs.close();
				pst.close();
//				cst.close();
			}
			
			
			
			
			
			
//			pst = con.prepareStatement(selectAttendenceClockDetails1_N);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));			
//			pst.setInt(2, uF.parseToInt(strEmpId));
//			pst.setInt(3, nCurrServiceId);
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));			
//			cst.setInt(3, uF.parseToInt(strEmpId));
//			cst.setInt(4, nCurrServiceId);
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			
			pst = con.prepareStatement(selectAttendenceClockDetails1_N);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));			
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setInt(3, nCurrServiceId);
			rs = pst.executeQuery();
			
			
			boolean isIn=false;
			boolean isOut=false;
			
			while (rs.next()) {

				if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
					isIn=true;
				}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
					isOut=true;
				}
				
			}
			rs.close();
			pst.close();
			
			
			log.debug("isIn="+isIn+" isOut"+isOut);
			log.debug("strPrevRosterStartTime="+strPrevRosterStartTime);
			log.debug("isPrevOut="+isPrevOut+" isPrevOut="+isPrevOut);
			log.debug("isRosterDependency="+CF.isRosterDependency(con,strEmpId));
			
			
			
			if(isIn && isOut){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered end time was :"+strRosterEndTime:""));
			}else if(!isIn && !isOut && strRosterStartTime!=null){
				request.setAttribute("ROSTER_TIME", ((strRosterStartTime!=null)?"Your rostered start time is :"+strRosterStartTime:""));
			}else if(!isIn && !isOut && strPrevRosterStartTime!=null){
				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
//				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				pst.setInt(5, nPrevServiceId);
//				rs = pst.executeQuery();
//				
//				
				
//				con.setAutoCommit(false);
//				cst = con.prepareCall("{? = call sel_emp_attendance_clock_details(?,?,?,?,?)}");
//				cst.registerOutParameter(1, Types.OTHER);
//				cst.setDate(2, uF.getPrevDate(CF.getStrTimeZone()));
//				cst.setString(3, "IN");
//				cst.setString(4, "OUT");
//				cst.setInt(5, uF.parseToInt(strEmpId));
//				cst.setInt(6, nPrevServiceId);
//				cst.execute();
//				rs = (ResultSet) cst.getObject(1);
				pst = con.prepareStatement(selectAttendenceClockDetailsInOut_N);
				pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone()));
				pst.setString(2, "IN");
				pst.setString(3, "OUT");
				pst.setInt(4, uF.parseToInt(strEmpId));
				pst.setInt(5, nPrevServiceId);
				rs = pst.executeQuery();
				
				boolean isPrevIn = false;
				isPrevOut = false;
				
				while(rs.next()){
					
					if(rs.getString("in_out").equalsIgnoreCase("IN")){
						isPrevIn = true;
					}else if(rs.getString("in_out").equalsIgnoreCase("OUT")){
						isPrevOut = true;
					} 
				}
				rs.close();
				pst.close();
				
				if(isPrevIn && isPrevOut){
					request.setAttribute("ROSTER_TIME", "Your are not rostered for today");
				}else if(isPrevIn && !isPrevOut){
					request.setAttribute("ROSTER_TIME", ((strPrevRosterEndTime!=null)?"Your rostered end time is :"+strPrevRosterEndTime:""));
				}else if(!isPrevIn && !isPrevOut && CF.isRosterDependency(con,strEmpId)){
//					request.setAttribute("ROSTER_TIME", ("Your are not rostered for today"));
					request.setAttribute("ROSTER_TIME", ((strPrevRosterStartTime!=null)?"Your rostered start time is :"+strPrevRosterStartTime:""));
				}else if(!isPrevIn && !isPrevOut && !CF.isRosterDependency(con,strEmpId)){
					request.setAttribute("ROSTER_TIME", ((strPrevRosterStartTime!=null)?"Your rostered start time is :"+strPrevRosterStartTime:""));					
				}
				
				
			}else if(isIn){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered end time is :"+strRosterEndTime:""));
			}else if(isOut){
				request.setAttribute("ROSTER_TIME", ((strRosterEndTime!=null)?"Your rostered start time is :"+strRosterStartTime:""));
			}
			
			
			
			
			
			/**
			 *  IS Roster Dependent
			 */
			
//			pst = con.prepareStatement(selectRosterDependent);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
//			
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_emp_details(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt(strEmpId));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE eod.emp_id = epd.emp_per_id AND emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			String strEmpType = null;
			boolean isRoster = false;
			if(rs.next()){
				strEmpType = rs.getString("emptype");
				isRoster = uF.parseToBoolean(rs.getString("is_roster"));
			}
			rs.close();
			pst.close();
			
			
			if(strEmpType!=null && !isRoster){
				
//				pst = con.prepareStatement(selectAttendenceClockDetailsInOut);
//				pst.setDate(1, uF.getCurrentDate());
//				pst.setString(2, "IN");
//				pst.setString(3, "OUT");
//				pst.setInt(4, uF.parseToInt(strEmpId));
//				rs = pst.executeQuery();
//				
//				while(rs.next()){
//					
//					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
//						isIn = true;	
//					}else if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
//						isOut = true;
//					}
//				}
//				
//				
//				
				request.setAttribute("ROSTER_TIME", "");
				
				
			}
			
			
			
			request.setAttribute("CURRENT_DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "EEEE "+CF.getStrReportDateFormat()));
			request.setAttribute("CURRENT_TIME", uF.getDateFormat(uF.getCurrentTime(CF.getStrTimeZone())+"", DBTIME, CF.getStrReportTimeFormat()));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			
			db.closeStatements(cst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		
	}
}
