package com.konnect.jpms.employee;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.konnect.jpms.charts.LinearZMeter;
import com.konnect.jpms.charts.PieCharts;
import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;


public class EmpDashboard implements IStatements {

	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	private static Logger log = Logger.getLogger(EmpDashboard.class);

	public EmpDashboard(HttpServletRequest request, HttpSession session, CommonFunctions CF, String strEmpId) {
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
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		String strUserType = (String)session.getAttribute(USERTYPE);
		String strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-dashboard\"></i><a href=\"Login.action?role=3\" style=\"color: #3c8dbc;\"> My Dashboard</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		try {
//			String strUserType = (String)session.getAttribute(USERTYPE);
			String strUserTypeId = (String)session.getAttribute(USERTYPEID);
//			System.out.println("strUserTypeId=====>"+strUserTypeId);
			request.setAttribute("strUserTypeId", strUserTypeId);
			
			con = db.makeConnection(con);
			boolean blnWebClockOnOff = CF.checkEmployeeClockOnOffAccess(con, uF, nEmpId, "WEB");
			request.setAttribute("blnWebClockOnOff", blnWebClockOnOff);
			List<String> alServices = new ArrayList<String>();
			
			Map hmServices = CF.getServicesMap(con,true);
			CF.getEmpUserTypeId(con,request,""+nEmpId);
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = CF.getEmpLevelMap(con);
			String []arrEnabledModules = CF.getArrEnabledModules();
			
			EmpDashboardData dashboardData = new EmpDashboardData(request, session, CF, uF, con, ""+ nEmpId);
			dashboardData.viewProfile(""+ nEmpId);
			dashboardData.getClockEntries();
			dashboardData.getEmpSkills(uF);
		
			if (arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_LEAVE_MANAGEMENT+"")>=0) { 
				dashboardData.getEmpLeaveStatus();
			}
			
			dashboardData.getPosition();
			
			dashboardData.getEmpRosterInfo(alServices, hmServices);
			
			if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_CAREER_DEV_PLANNING+"")>=0){
				dashboardData.getCertificates();
				dashboardData.getUpcomingTrainings();
				dashboardData.getMyLearningCompleted();
			}
			
			if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
				CF.getElementList(con, request);
				CF.getAttributes(con, request, ""+ nEmpId);
				dashboardData.getMyTeam();
				dashboardData.getMyTeamRating();
				dashboardData.getMyKRA();
				dashboardData.getMyGoalAchieve();
				dashboardData.getMyTeamGoalAchieve();
				dashboardData.getMyManagerGoalAchieve();
				dashboardData.getMyTargetAchieve();
//				dashboardData.getMyTeamTargetAchieve(); 
				
				if(strBaseUserType != null && (strBaseUserType.equals(MANAGER) || strBaseUserType.equals(CEO) || strBaseUserType.equals(HRMANAGER) || strBaseUserType.equals(ADMIN))) {
					dashboardData.getMyTeamReviews();
					dashboardData.getMyTeamKRAReview();
					dashboardData.getMyTeamTargetAchievedAndMissed();
					dashboardData.getMyTeamKRAAchievedStatus();
				}
			}
			
					
			if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_ONBOARDING+"")>=0){
				dashboardData.getInterviews();
			}
			
			
			if(arrEnabledModules != null && ArrayUtils.contains(arrEnabledModules, MODULE_PROJECT_MANAGEMENT+"")>=0){
				dashboardData.getTaskDetails();
				//getUpcomingTasks(con, uF);
				//getTasksCount(con, uF);
			}
			
//			dashboardData.getAchievements();
			//dashboardData.getUpcomingRequests(); 
			
//			dashboardData.verifyClockDetails();
			
			dashboardData.getPaySlipStatus();
			dashboardData.getPendingExceptionCount();
			dashboardData.getApprovedExceptionCount();
			dashboardData.getRosterStatus();
			dashboardData.getBusinessRuleStatus();
			dashboardData.getProbationStatus();
			
			dashboardData.getWorkedHours();
			dashboardData.getEmpKPI();
			dashboardData.getRosterVsWorkedHours();
//			getEmpLeaveCounts(con, uF);
			
			dashboardData.getEmpServiceWorkingHourCounts();
			
//			dashboardData.getDayThought();
//			dashboardData.getResignationStatus();
//			dashboardData.getMailCount();
//			dashboardData.getBirthdays();
			CF.getAlertUpdates(CF, strEmpId, request, strUserType);
//			dashboardData.getResignedEmployees();
			dashboardData.getPendingAttendanceIssues();
			
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_LEAVE_MANAGEMENT+"")>=0) {
				dashboardData.getUpcomingTeamLeaves(hmEmployeeMap);
			}
//			dashboardData.getTeamLeaveRequests(hmEmployeeMap,hmLevelMap);
//			dashboardData.getTeamReimbursementRequests(hmEmployeeMap);
			
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_COMPENSATION_MANAGEMENT+"")>=0) {
				dashboardData.getMyGrowthData();
			}
//			dashboardData.getTeamTravelRequests(hmEmployeeMap, hmLevelMap);
			
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_TIME_AND_ATTENDANCE+"")>=0) {
				dashboardData.getExceptionCount();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			
			db.closeStatements(cst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}

		return LOAD;
	}
	
	
	private void getUpcomingTasks(Connection con, UtilityFunctions uF) {

		PreparedStatement pst  = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select activity_name,ai.pro_id,pro_name,ai.completed,ai.deadline,ai.start_date," +
					"ai.already_work  from projectmntnc pmc, activity_info ai where pmc.pro_id=ai.pro_id and ai.emp_id = ? " +
					"and ai.approve_status='n' and ai.already_work = 0 and ai.deadline > ? order by ai.deadline limit 4");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			List<List<String>> upcomeTaskList = new ArrayList<List<String>>();
//			List<String> upcomeTaskInner = new ArrayList<String>();

			while(rs.next()){
//				Date currentDate = uF.getCurrentDate(CF.getStrTimeZone());
//				Date deadLineDate = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				//if(currentDate!=null && deadLineDate!=null && currentDate.before(deadLineDate) && uF.parseToDouble(rs.getString("already_work"))==0){
				List<String> upcomeTaskInner = new ArrayList<String>();
				upcomeTaskInner.add(rs.getString("activity_name"));
				upcomeTaskInner.add(rs.getString("pro_name"));
				upcomeTaskInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				
				upcomeTaskList.add(upcomeTaskInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("upcomeTaskList",upcomeTaskList);
		} catch (Exception e) {
			e.printStackTrace();
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
}
