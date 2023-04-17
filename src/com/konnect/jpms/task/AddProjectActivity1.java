package com.konnect.jpms.task;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
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
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.leave.ManagerLeaveApproval;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillTask;
import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserActivities;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author user
 * 
 */
public class AddProjectActivity1 extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;

	List<FillProjectList> projectdetailslist; 
	List<FillClients> clientlist;
	List<FillTask> tasklist;
	List<FillPayCycles> paycycleList;
	String strPaycycle;
	String clientId;
	String clientName;

	CommonFunctions CF;
	
	String frmDate;
	String toDate;
	List<String> strClient;
	List<String> strProject;
	String totalHours;
	String activityType;
	String strActivity;
	String strActivityTaskId;

	String type;
	String unlock; 
	int nDateCount = 0;

	String save;
	String submit1;

	HttpSession session;
	String strSessionEmpId = null;
	String strEmpOrgId = null;
	String strUserType = null;
	String strProductType =  null;

	String userlocation;

	String policy_id;
	String checkTask;
	String[] compOff;
	String[] compOffDate;
	
	String[] unPaidHolidays;
	String[] unPaidHolidaysDate;

	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillWeekDays> weekList;
	
	String strYear;
	String strMonth;
	String strWeek;
	String filterBy;
	
	String strMinDate;
	String strMaxDate;
	
	String pageType;
	String fillUserType;
	
	String submitType;

	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
//		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("getStrPaycycle() ===> " + getStrPaycycle());
//		System.out.println("getFillUserType() ===> " + getFillUserType());
		
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-tasks\"></i><a href=\"MyTasks.action\" style=\"color: #3c8dbc;\"> My Work</a></li>" +
				"<li class=\"active\">My Timesheet</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		request.setAttribute(PAGE, "/jsp/task/AddProjectActivity1.jsp");
		request.setAttribute(TITLE, "Timesheet");

//		boolean claimExtraWorkFlag = CF.getFeatureManagementStatus(request, uF, F_TASKRIG_CLAIM_EXTRA_WORKING);
		boolean claimExtraWorkFlag = true;
//		System.out.println("APA/145---claimExtraWorkFlag="+claimExtraWorkFlag);
		request.setAttribute("claimExtraWorkFlag", claimExtraWorkFlag);
		
		userlocation = getUserLocation();
		
		
		if (getStrEmpId() == null || (getStrEmpId() != null && getStrEmpId().equalsIgnoreCase("NULL"))) {
			setStrEmpId(strSessionEmpId);
		} else {
			setStrEmpId(getStrEmpId());
		}

//		System.out.println(" getFillUserType =========>>  " + getFillUserType());
		if(getFillUserType() == null) {
			setFillUserType("MY");
		}
//		if(getStrUserType() == null) {
//			setStrUserType(EMPLOYEE);
//		}
//		System.out.println(" getFillUserType 1 =========>>  " + getFillUserType());
		
		String strEmpOrgId = CF.getEmpOrgId(uF, getStrEmpId(), request);
		
		StringBuilder sbClientIds = null;
		for(int i=0; getStrClient() != null && i<getStrClient().size(); i++) {
			if(sbClientIds == null) {
				sbClientIds = new StringBuilder();
				sbClientIds.append(getStrClient().get(i));
			} else {
				sbClientIds.append(","+getStrClient().get(i));
			}
		}
		String strClientsIds = null;
		if(sbClientIds != null) {
			strClientsIds = sbClientIds.toString();
		}
		projectdetailslist = new FillProjectList(request).fillProjectDetailsByEmpWithOther(uF.parseToInt(getStrEmpId()),false, strClientsIds);
		clientlist = new FillClients(request).fillClientsWithOther(uF.parseToInt(getStrEmpId()));
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, strEmpOrgId);
		
		monthList = new FillMonth().fillMonth();
		weekList = new FillWeekDays().fillWeekNos();
		
		String[] strPayCycleDates = null;
//		System.out.println("getFilterBy() ===>> " + getFilterBy());
//		System.out.println("getStrPaycycle() ===>> " + getStrPaycycle());
		
		if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
			if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
				strPayCycleDates = getStrPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
				setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}
			setStrYear("");
			setStrMonth("");
			setStrWeek("");
		} else if(getFilterBy() != null && getFilterBy().equals("O")) {
			if(getStrWeek()==null || getStrWeek().equals("")) {
				String strDate = "01/"+getStrMonth()+"/"+getStrYear();
				String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
				monthminMaxDates = monthminMaxDates+"::::00";
				strPayCycleDates = monthminMaxDates.split("::::");
			} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
				
			}
			setStrPaycycle("");
		}
		
		setStrMinDate(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DATE_MM_DD_YYYY));
		setStrMaxDate(uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DATE_MM_DD_YYYY));
		
		/*String[] strPayCycleDates = null;
		if (getStrPaycycle() != null) {
			strPayCycleDates = getStrPaycycle().split("-");
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, strEmpOrgId, request);
			setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}*/

//		System.out.println("APA1/225--getStrPaycycle 1 ===>> " + getStrPaycycle());
		
//		System.out.println("getSave()  ===>> " + getSave());
		if (getSave() != null) {
			saveTaskData(uF);
		}

		if (getType() != null) {
			saveTypeData1(uF);
		}

		if (request.getParameter("D") != null && request.getParameter("D").equalsIgnoreCase("D")) {
			removeTaskData(uF, request.getParameter("strTaskId"));
		}

		if (getFrmDate() != null && getToDate() != null && !getFrmDate().equalsIgnoreCase("NULL") && !getToDate().equalsIgnoreCase("NULL")) {

		} else {
			setFrmDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT));
			setToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, DATE_FORMAT));
		}

		StringBuilder sbProIds = null;
		String otherProId = null;
		int proId = 0;
//		System.out.println("=========>> getStrProject 111 " + getStrProject());
		for(int i = 0; getStrProject() != null && !getStrProject().isEmpty() && i<getStrProject().size(); i++) {
			if(getStrProject().get(i) != null && !getStrProject().get(i).equals("") && !getStrProject().get(i).equals("null") && uF.parseToInt(getStrProject().get(i)) > 0) {
				if(sbProIds == null) {
					sbProIds = new StringBuilder();
					sbProIds.append(getStrProject().get(i));
				} else {
					sbProIds.append(","+getStrProject().get(i));
				}
			}
			if(getStrProject().get(i) != null && uF.parseToInt(getStrProject().get(i)) > 0 && !getStrProject().get(i).equals("-1")) {
				proId++;
			}
			otherProId = getStrProject().get(i);
		}
		if(sbProIds == null) {
			sbProIds = new StringBuilder();
		}
		
//		System.out.println("=========>> 1 ");
//		System.out.println("=========>> otherProId " + otherProId + " proId ===>> " + proId);
		if (otherProId != null && !otherProId.equals("-1") && sbProIds != null && sbProIds.toString().length()>0) {
//			System.out.println("=========>> 1 if");
			tasklist = new FillTask(request).fillTaskByMultiProjects(CF, sbProIds.toString(), uF.parseToInt(getStrEmpId()));
		} else if (proId == 0 && otherProId != null && otherProId.equals("-1")) {
//			System.out.println("=========>> 1 else if");
			tasklist = new FillTask(request).fillExtraActivity(CF, uF.parseToInt(getStrEmpId()));
		} else {
//			System.out.println("=========>> 1 else ");
			tasklist = new FillTask(request).fillAllTasksWithOtherOfEmployee(CF, uF.parseToInt(getStrEmpId()), sbProIds.toString(), strClientsIds);
		}
		fillTaskRows(uF);

		getTimesheetPolicyMember(); // submit timesheet workflow member
		
		getProjectManagerList(uF);
		getApprovalTaskStatus(uF);
		getApprovalStatus(uF);
		getMemCount();
		getData(uF);

		checkPayrollGeneration(uF);

		getCompLeaveStatus();

		getSelectedFilterEmpwise(uF);
//		System.out.println("getSubmitType() ========>> " + getSubmitType());
		if(getSubmitType() != null && getSubmitType().equalsIgnoreCase("LOAD")) {
			return LOAD;
		} else {
			return SUCCESS;
		}
	}

	private void getCompLeaveStatus() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
//			String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());

			String[] arr = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					arr = getStrPaycycle().split("-");
				} else {
					arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(arr[0] + "-" + arr[1]+ "-" + arr[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					arr = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			
			/*String arr[] = null;
			if (getStrPaycycle() != null) {
				arr = getStrPaycycle().split("-");
			} else {
				arr = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strEmpOrgId);
				setStrPaycycle(arr[0] + "-" + arr[1] + "-" + arr[2]);
			}*/

			pst = con.prepareStatement("select * from leave_application_register where emp_id=? and to_date(_date::text,'yyyy-MM-dd') between ? and ? "
						+ " and leave_type_id in (select leave_type_id from emp_leave_type where is_compensatory=true or is_constant_balance=false)");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
//			System.out.println("compLeaveList pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> compLeaveList = new ArrayList<String>();
			while (rs.next()) {
				if (!uF.parseToBoolean(rs.getString("is_modify"))) {
					compLeaveList.add(uF.getDateFormat(rs.getString("_date"),DBDATE, DATE_FORMAT));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("compLeaveList", compLeaveList);
   
			StringBuilder sbQuery=new StringBuilder();
//			sbQuery.append("select * from emp_leave_entry where emp_id=? and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD')) " +
//				"and is_approved=0 and leave_type_id in (select leave_type_id from emp_leave_type where is_compensatory=true)");
			sbQuery.append("select * from emp_leave_entry where emp_id=? and (approval_from between ? and ? or approval_to_date between ? and ?) " +
					"and is_approved=0 and leave_type_id in (select leave_type_id from emp_leave_type where is_compensatory=true)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(arr[1], DATE_FORMAT));
//			System.out.println("applyLeaveList pst =====> " + pst);
			rs = pst.executeQuery();
			List<String> applyLeaveList = new ArrayList<String>();
			while (rs.next()) {
				applyLeaveList.add(uF.getDateFormat(rs.getString("approval_from"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			request.setAttribute("applyLeaveList", applyLeaveList);

//			pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and (approval_from, approval_to_date) "
//							+ " overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD')) and is_approved=-1");
			pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and (approval_from between ? and ? or approval_to_date between ? and ?)" +
					" and is_approved=-1");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> denyList = new ArrayList<String>();
			while (rs.next()) {
				// if(rs.getDate("approval_from").compareTo(rs.getDate("approval_from"))==0) {
				denyList.add(uF.getDateFormat(rs.getString("approval_from"),DBDATE, DATE_FORMAT));
				// }
			}
			rs.close();
			pst.close();
			request.setAttribute("denyList", denyList);
			
			pst = con.prepareStatement("select max(task_date) as task_date from task_activity where emp_id=? " +
					"and to_date(task_date::text,'yyyy-MM-dd') between ? and ? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			String maxTaskDate=null;
			while (rs.next()) {
				maxTaskDate=uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			request.setAttribute("maxTaskDate", maxTaskDate);
			
			List<String> holidayCountList=new ArrayList<String>();				
			pst=con.prepareStatement("select _date from holiday_count where emp_id=? and to_date(_date::text,'yyyy-MM-dd') between ? and ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs=pst.executeQuery();
			while(rs.next()) {
				holidayCountList.add(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			request.setAttribute("holidayCountList", holidayCountList);
			
			pst = con.prepareStatement("select min(task_date) as task_date from task_activity where emp_id=? " +
			"and to_date(task_date::text,'yyyy-MM-dd') between ? and ? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			String minTaskDate=null;
			while (rs.next()) {
				minTaskDate=uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			request.setAttribute("minTaskDate", minTaskDate);
 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void checkPayrollGeneration(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);

			/*String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			String arr[] = null;
			if (getStrPaycycle() != null) {
				arr = getStrPaycycle().split("-");
			} else {
				arr = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strEmpOrgId);
				setStrPaycycle(arr[0] + "-" + arr[1] + "-" + arr[2]);
			}*/

			String[] arr = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					arr = getStrPaycycle().split("-");
//					System.out.println("APA1/485--arr=="+getStrPaycycle());
				} else {
					arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(arr[0] + "-" + arr[1]+ "-" + arr[2]);
//					System.out.println("APA1/489--arr=="+arr);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					arr = monthminMaxDates.split("::::");
//					System.out.println("APA1/500--monthminMaxDates=="+monthminMaxDates);
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
				}
				setStrPaycycle("");
			}
//			System.out.println("APA1/505---arr[0]="+arr[0]);
			String[] strPaycycle = CF.getPayCycleFromDate(con, arr[0], CF.getStrTimeZone(), CF, strEmpOrgId);
			
			boolean flag = false;
			pst = con.prepareStatement("select emp_id from payroll_generation where paycycle=? and emp_id=? group by emp_id");
			pst.setInt(1, uF.parseToInt(strPaycycle[2]));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
			// System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("checkPayroll", flag);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public void getMemCount() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			/*String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			String arr[] = null;
			if (getStrPaycycle() != null) {
				arr = getStrPaycycle().split("-");
			} else {
				arr = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strEmpOrgId);
				setStrPaycycle(arr[0] + "-" + arr[1] + "-" + arr[2]);
			}*/
			
			String[] arr = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					arr = getStrPaycycle().split("-");
				} else {
					arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(arr[0] + "-" + arr[1]+ "-" + arr[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					arr = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			
			pst = con.prepareStatement("select count(*) as count1 from(select emp_id from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in(select task_id from task_activity where emp_id=? and task_date between ? and ?) group by emp_id)as a");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(arr[2]));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				request.setAttribute("cnt", rs1.getString("count1"));
			}
			rs1.close();
			pst.close();
			request.setAttribute("cycle", arr[2]);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void getApprovalTaskStatus(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);

			/*String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			String arr[] = null;
			if (getStrPaycycle() != null) {
				arr = getStrPaycycle().split("-");
			} else {
				arr = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strEmpOrgId);
				setStrPaycycle(arr[0] + "-" + arr[1] + "-" + arr[2]);
			}*/
			
			String[] arr = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					arr = getStrPaycycle().split("-");
				} else {
					arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(arr[0] + "-" + arr[1]+ "-" + arr[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					arr = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			
			pst = con.prepareStatement("select task_id,activity_name from activity_info where task_accept_status=1");
			rs = pst.executeQuery();
			Map<String, String> hmActivity = new HashMap<String, String>();
			while (rs.next()) {
				hmActivity.put(rs.getString("task_id"), rs.getString("activity_name"));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from task_activity where is_approved=1 and emp_id=? and task_date between ? and ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(arr[2]/));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmCheckTaskStatus = new HashMap<String, String>();
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("activity_id")) == 0) {
					hmCheckTaskStatus.put(rs.getString("activity"), rs.getString("is_approved"));
				} else {
					hmCheckTaskStatus.put(hmActivity.get(rs.getString("activity_id")), rs.getString("is_approved"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCheckTaskStatus", hmCheckTaskStatus);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private void getApprovalStatus(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			/*String arr[] = null;
			if (getStrPaycycle() != null) {
				arr = getStrPaycycle().split("-");
			}*/

			String[] arr = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					arr = getStrPaycycle().split("-");
				} else {
					arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(arr[0] + "-" + arr[1]+ "-" + arr[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					arr = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			String locationID = hmEmpWlocationMap.get(strSessionEmpId);

			
//			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' order by effective_id,member_position");
			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in (" +
				"select task_id from task_activity where task_date between ? and ?) order by effective_id,member_position");
			pst.setDate(1, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
			while (rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
				if (checkEmpList == null) checkEmpList = new ArrayList<String>();
				
				checkEmpList.add(rs.getString("emp_id"));

				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCheckEmp", hmCheckEmp);

			pst = con
					.prepareStatement("select ud.emp_id from user_details ud,employee_official_details eod,employee_personal_details epd where "
							+ " ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'");
			pst.setInt(1, uF.parseToInt(locationID));
			rs = pst.executeQuery();
			Map<String, String> hmEmpByLocation = new HashMap<String, String>();
			while (rs.next()) {
				hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpByLocation", hmEmpByLocation);

			pst = con.prepareStatement("select w.effective_id,w.is_approved from work_flow_details w,task_activity ta where w.is_approved=1 "
							+ " and w.emp_id=? and w.effective_type='"+WORK_FLOW_TIMESHEET+"' and w.effective_id=ta.task_id and ta.emp_id=? "
							+ " and ta.task_date between ? and ?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
//			pst.setInt(3, uF.parseToInt(arr[2]));
			pst.setDate(3, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			boolean flag = false;
			Map<String, String> hmCheckUserApproval = new HashMap<String, String>();
			while (rs.next()) {
				flag = true;
				hmCheckUserApproval.put(rs.getString("effective_id")+"_"+ strSessionEmpId, flag+"");
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCheckUserApproval", hmCheckUserApproval);

			request.setAttribute("emp", getStrEmpId());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private void saveTypeData1(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false;
		boolean isAttendFromTimesheetDetails = false;
		
		try {
			
//			System.out.println("APA1/794--saveTypeData1");

			con = db.makeConnection(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			String levelID = hmEmpLevelMap.get(getStrEmpId());

			pst = con.prepareStatement("select attend_from_timesheet_detail from project_information_display");
			rs = pst.executeQuery();
			while (rs.next()) {
				isAttendFromTimesheetDetails = rs.getBoolean("attend_from_timesheet_detail");
			}
			rs.close();
			pst.close();
				
			String[] arr = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					arr = getStrPaycycle().split("-");
				} else {
					arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(arr[0] + "-" + arr[1]+ "-" + arr[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					arr = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			
			Map hmWeekendMap = CF.getWeekEndDateList(con, arr[0], arr[1], CF,uF,null,null);
			String strWLocationId = hmEmpWLocation.get(getStrEmpId());

			pst = con.prepareStatement("select unblock_by from  task_activity ta where emp_id=? and ta.task_date between ? and ? "
							+ " and unblock_by is not null  group by unblock_by");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(arr[2]));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> unlockList = new ArrayList<String>();
			while (rs.next()) {
				unlockList.add(rs.getString("unblock_by"));
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpTaskActivityDateByhr = new LinkedHashMap<String, String>();
			pst = con.prepareStatement(" select * from task_activity where emp_id=? and task_date between ? and ? and is_approved=1 and activity_id=0 order by task_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + ":_:" + rs.getString("activity_id");
				hmEmpTaskActivityDateByhr.put(rs.getString("task_id"), task_data);
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpTaskActivityDateByEmp = new LinkedHashMap<String, String>();
			pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date between ? and ? and is_approved=0 order by task_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + ":_:" + rs.getString("activity_id");
				hmEmpTaskActivityDateByEmp.put(rs.getString("task_id"), task_data);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date between ? and ? and is_approved=2 order by task_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> approveTaskList = new ArrayList<String>();
			while (rs.next()) {
				approveTaskList.add(rs.getString("task_id"));
			}
			rs.close();
			pst.close();

//			Map<String, String> hmEmpActivity = new LinkedHashMap<String, String>();
//
//			pst = con.prepareStatement("select * from activity_info ai,projectmntnc pmt where ai.emp_id=? "
//							+ "and ai.pro_id=pmt.pro_id and pmt.added_by=?");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmEmpActivity.put(rs.getString("task_id"), rs.getString("task_id"));
//			}
//			rs.close();
//			pst.close();

//			System.out.println("getType() ===>> " + getType());
			String strDomain = request.getServerName().split("\\.")[0];
//			System.out.println("APA/906--claimExtraWorkFlag ===>> " + request.getAttribute("claimExtraWorkFlag"));
			if (arr != null && arr.length > 2 && getType() != null && getType().equalsIgnoreCase("submit")) {

//				System.out.println("APA/905--levelID ===>> " + levelID+ " -- arr[0] ===>> " + arr[0]);
				boolean claimExtraWorkFlag = (Boolean)request.getAttribute("claimExtraWorkFlag");
//				System.out.println("APA/911--claimExtraWorkFlag ===>> " + claimExtraWorkFlag);
				if(claimExtraWorkFlag) {
					
			//===start parvez date: 15-11-2021===
//					deleteCompOffLeave(con,uF, arr[0], arr[1],false,levelID);
//					System.out.println("APA1/916--getCompOff="+getCompOff());
					if(getCompOff()!=null){
						List<String> strCompOffDate = new ArrayList<String>(Arrays.asList(getCompOff()));
						deleteCompOffLeaveOnSubmit(con,uF, arr[0], arr[1],strCompOffDate,false,levelID);
					}
					
//					List<String> strCompOffDate = new ArrayList<String>(Arrays.asList(getCompOff()));
//					deleteCompOffLeaveOnSubmit(con,uF, arr[0], arr[1],strCompOffDate,false,levelID);
					
					
				}
				
//				Need to Change
				Map<String, String> hmProIds = getProjectIdMapOnTaskId(con);
			//===start parvez date: 12-10-2022===	
//				Map<String, String> hmProOwners = getProjectOwnerMap(con);
				Map<String, List<String>> hmProOwners = getProjectOwnerMap(con);
			//===end parvez date: 12-10-2022===	
				Map<String, Set<String>> hmProTl = getProjectTLMap(con, uF);
				
				
//				Need to Change
				
				
//				Iterator<String> it = hmEmpTaskActivityDateByEmp.keySet().iterator();
//				Set<String> setEmp = new HashSet<String>();
//				while (it.hasNext()) {
//					String task_id = (String) it.next(); // task_date
//
//					String[] tempData = hmEmpTaskActivityDateByEmp.get(task_id).split(":_:");
//					String task_date = tempData[0];
//					String activity_id = tempData[1];
//
//					pst = con.prepareStatement("update task_activity set is_approved = ?, unblock_by=null, submited_date=? where emp_id=? and task_id= ?");
//					pst.setInt(1, 1);
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(3, uF.parseToInt(getStrEmpId()));
//					pst.setInt(4, uF.parseToInt(task_id));
//					pst.execute();
//					pst.close();
//					
//					if (unlockList == null || unlockList.isEmpty()) {
//						if (uF.parseToBoolean(CF.getIsWorkFlow())) {
//							insertLeaveApprovalMember(con, pst, rs, task_id, uF);
//						}
//					}
//
//					if (getCompOff() != null && getCompOff().length > 0) {
//						Set<String> comOffDate = new HashSet<String>(Arrays.asList(getCompOff()));
//						if (comOffDate.contains(task_date)) {
//							insertCompensatoryLeave(task_date, con, uF, levelID, strWLocationId, hmEmpLevelMap);
//						}
//					}
//					
//					/**
//					 * Alerts
//					 * */ 
//					
//					if(uF.parseToInt(hmProIds.get(task_id)) > 0 && uF.parseToInt(hmProOwners.get(hmProIds.get(task_id))) > 0) {
//						setEmp.add(hmProOwners.get(hmProIds.get(task_id)));
//					}
//					
//					if(uF.parseToInt(hmProIds.get(task_id)) > 0) {
//						Set<String> setTl = hmProTl.get(hmProIds.get(task_id));
//						if(setTl != null) {
//							for(String strTl : setTl) {
//								if(uF.parseToInt(strTl) > 0) {
//									setEmp.add(strTl);
//								}
//							}
//						}
//					}
//					
//				}
				
				Iterator<String> it = hmEmpTaskActivityDateByEmp.keySet().iterator();
				Set<String> setEmp = new HashSet<String>();
				pst = con.prepareStatement("update task_activity set is_approved = ?, unblock_by=null, submited_date=? where emp_id=? and task_id= ?");
				while (it.hasNext()) {
					String task_id = (String) it.next(); // task_date

					String[] tempData = hmEmpTaskActivityDateByEmp.get(task_id).split(":_:");
					String task_date = tempData[0];
					String activity_id = tempData[1];

					pst.setInt(1, 1);
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(3, uF.parseToInt(getStrEmpId()));
					pst.setInt(4, uF.parseToInt(task_id));
//					System.out.println("pst ===>> " + pst);
					pst.addBatch();				
										
					/**
					 * Alerts
					 * */ 
					
				//===start parvez date: 12-10-2022==	
					/*if(uF.parseToInt(hmProIds.get(task_id)) > 0 && uF.parseToInt(hmProOwners.get(hmProIds.get(task_id))) > 0) {
						setEmp.add(hmProOwners.get(hmProIds.get(task_id)));
					}*/
					if(uF.parseToInt(hmProIds.get(task_id)) > 0 && hmProOwners!=null && hmProOwners.get(hmProIds.get(task_id)).size()>0) {
						List<String> proOwnerList = hmProOwners.get(hmProIds.get(task_id));
						for(int ii=0; ii<proOwnerList.size(); ii++) {
							if(uF.parseToInt(proOwnerList.get(ii))>0){
								setEmp.add(proOwnerList.get(ii));
							}
						}
						
					}
				//===end parvez date: 12-10-2022===	
					
					if(uF.parseToInt(hmProIds.get(task_id)) > 0) {
						Set<String> setTl = hmProTl.get(hmProIds.get(task_id));
						if(setTl != null) {
							for(String strTl : setTl) {
								if(uF.parseToInt(strTl) > 0) {
									setEmp.add(strTl);
								}
							}
						}
					}
					
				}
				int[] x = pst.executeBatch();
				pst.close();
//				System.out.println("x ===>> " + x.length);
				
				if(x.length > 0 && (unlockList == null || unlockList.isEmpty()) && uF.parseToBoolean(CF.getIsWorkFlow())){
					insertTimsheetApprovalMember(con, pst, rs, hmEmpTaskActivityDateByEmp, uF);
				}
//				System.out.println("getCompOff() ===>> " + getCompOff() != null ? getCompOff().length : 0 +" -- hmEmpTaskActivityDateByEmp ===>> " + hmEmpTaskActivityDateByEmp);
				if (x.length > 0 && getCompOff() != null && getCompOff().length > 0) {
					Set<String> comOffDate = new HashSet<String>(Arrays.asList(getCompOff()));
//					System.out.println("APA/1019--comOffDate="+comOffDate);
					Iterator<String> it1 = hmEmpTaskActivityDateByEmp.keySet().iterator();
					while (it1.hasNext()) {
						String task_id = (String) it1.next(); // task_date
//						System.out.println("task_id ===>> " + task_id);
						String[] tempData = hmEmpTaskActivityDateByEmp.get(task_id).split(":_:");
						String task_date = tempData[0];
//						System.out.println("task_date ===>> " + task_date + " -- comOffDate ===>> " + comOffDate);
						if (comOffDate.contains(task_date)) {
//							System.out.println("APA1/1040--in if contains ===>> ");
							insertCompensatoryLeave(task_date, con, uF, levelID, strWLocationId, hmEmpLevelMap);
						}
					}
				}
				
				
				if (unlockList == null || unlockList.isEmpty()) {
					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						
						pst = con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where policy_count=? and policy_type='1' " +
							"and wfp.work_flow_member_id = a.work_flow_member_id order by member_position");
						pst.setInt(1, uF.parseToInt(getPolicy_id()));
//						System.out.println("pst ==>> " + pst);
						rs = pst.executeQuery();

						Map<String, List<String>> hmMemberMap = new LinkedHashMap<String, List<String>>();
						while (rs.next()) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("member_type"));
							innerList.add(rs.getString("member_id"));
							innerList.add(rs.getString("member_position"));
							innerList.add(rs.getString("work_flow_mem"));
							innerList.add(rs.getString("work_flow_member_id"));

							hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
						}
						rs.close();
						pst.close();
						
						Iterator<String> itt = hmMemberMap.keySet().iterator();
//						System.out.println("hmMemberMap ==>> " + hmMemberMap);
						Set<String> setEmpp = new HashSet<String>(); 
						while (itt.hasNext()) {
							String work_flow_member_id = itt.next();
//							System.out.println("work_flow_member_id ==>> " + work_flow_member_id);
							List<String> innerList = hmMemberMap.get(work_flow_member_id);
							int memid = uF.parseToInt(innerList.get(1));

							if (uF.parseToInt(innerList.get(0)) == 3) {
								String[] empid = request.getParameterValues(innerList.get(3) + memid);
								for (int i = 0; empid != null && i < empid.length; i++) {
									if (empid[i] != null && !empid[i].equals("")) {
										setEmpp.add(empid[i]);
									}
								}
							} else {
								String empid = request.getParameter(innerList.get(3) + memid);
								if (empid != null && !empid.equals("")) {
									setEmpp.add(empid);
								}
							}
						}
						
						/**
						 * Alert
						 * */
						
						String alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> You have received a new timesheet, sent by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						String alertAction = "TimesheetsApproval.action";
						StringBuilder taggedWith = null;
						for(String strEmp : setEmpp) {
							if(taggedWith == null) {
								taggedWith = new StringBuilder();
								taggedWith.append(","+strEmp.trim()+",");
							} else {
								taggedWith.append(strEmp.trim()+",");
							}
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strEmp);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
//							userAlerts.set_type(TIMESHEET_RECEIVED_ALERT);
							userAlerts.setStatus(INSERT_TR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
						
						if(taggedWith == null) {
							taggedWith = new StringBuilder();
						}
						
						String activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px; margin-right: 2px;\"> T </span> You have received a new timesheet, sent by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
						UserActivities userAct = new UserActivities(con, uF, CF, request);
						userAct.setStrDomain(strDomain);
						userAct.setStrAlignWith("");
						userAct.setStrAlignWithId("");
						userAct.setStrTaggedWith(taggedWith.toString());
						userAct.setStrVisibilityWith(taggedWith.toString());
						userAct.setStrVisibility("2");
						userAct.setStrData(activityData);
						userAct.setStrSessionEmpId(strSessionEmpId);
						userAct.setStatus(INSERT_TR_ACTIVITY);
						Thread tt = new Thread(userAct);
						tt.run();
					}
				}
				
				
				
				/**
				 * Alert
				 * */
				String alertData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"> You have received a new timesheet, sent by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				String alertAction = "TeamTimesheetsApproval.action";
				StringBuilder taggedWith = null;
				for(String strEmp : setEmp) {
					if(taggedWith == null) {
						taggedWith = new StringBuilder();
						taggedWith.append(","+strEmp.trim()+",");
					} else {
						taggedWith.append(strEmp.trim()+",");
					}
					Notifications nF = new Notifications(N_TIMESHEET_SUBMITED, CF); 
					nF.setDomain(strDomain);
					
					nF.request = request;
					nF.setStrOrgId((String)session.getAttribute(ORGID));
					nF.setEmailTemplate(true);
					
					nF.setStrEmpId(strEmp);
					nF.setStrFromDate(arr[0]);
					nF.setStrToDate(arr[1]);
					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.sendNotifications();
					
					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmp);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
//					userAlerts.set_type(TIMESHEET_RECEIVED_ALERT);
					userAlerts.setStatus(INSERT_TR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
				}
				
				if(taggedWith == null) {
					taggedWith = new StringBuilder();
				}
				
				String activityData = "<div style=\"float: left; width: 100%; padding: 5px 0px;\"><span style=\"float: left; width: 20px; height: 20px; font-weight: bold; background-color: lightpink; text-align: center; font-size: 16px;  margin-right: 2px;\"> T </span> You have received a new timesheet, sent by <b> "+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>.</div>";
				UserActivities userAct = new UserActivities(con, uF, CF, request);
				userAct.setStrDomain(strDomain);
				userAct.setStrAlignWith("");
				userAct.setStrAlignWithId("");
				userAct.setStrTaggedWith(taggedWith.toString());
				userAct.setStrVisibilityWith(taggedWith.toString());
				userAct.setStrVisibility("2");
				userAct.setStrData(activityData);
				userAct.setStrSessionEmpId(strSessionEmpId);
				userAct.setStatus(INSERT_TR_ACTIVITY);
				Thread tt = new Thread(userAct);
				tt.run();
				
				session.setAttribute(MESSAGE,SUCCESSM+ "Your timesheet has been submitted successfully.\nNow you will not be able to edit your timesheet."+ END);

			}

//			 System.out.println("APA1/1208--getUnlock()=====>"+getUnlock());
//			 System.out.println("APA1/1209--getType()=====>"+getType());
			if (arr != null && arr.length > 2 && getType() != null && getType().equalsIgnoreCase("approve") && getUnlock() != null && !getUnlock().equals("")) {
				Map<String, String> hmEmpTaskActivityDate = new LinkedHashMap<String, String>();
				pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date between ? and ? order by task_date");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					String activity = rs.getString("activity") != null && !rs.getString("activity").equals("") ? rs.getString("activity") : "0";
					String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + ":_:" + rs.getString("activity_id") + ":_:" + activity;
					hmEmpTaskActivityDate.put(rs.getString("task_id"), task_data);
				}
				rs.close();
				pst.close();
				
				Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
				StringBuilder sbTaskId = null;
				while (it.hasNext()) {
					String task_id = (String) it.next(); // task_date
					if (approveTaskList != null && !approveTaskList.isEmpty() && approveTaskList.contains(task_id) && ((!strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)) || (getPageType() != null && getPageType().equals("MP")))) {
						continue;
					}
					if(sbTaskId == null){
						sbTaskId = new StringBuilder();
						sbTaskId.append(task_id);
					} else {
						sbTaskId.append(","+task_id);
					}
				}
//				System.out.println("APA1/1240--sbTaskId ===>> " + sbTaskId);
				if(sbTaskId != null){
					if (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN)) { 

						pst = con.prepareStatement("update task_activity set is_approved = ?,is_billable_approved = ?,approved_by=null,approved_date=null,unblock_by=null,unblock_time=null, submited_date=null where emp_id=? and task_id in ("+sbTaskId.toString()+")");
						pst.setInt(1, 0);
						pst.setInt(2, 0);
						pst.setInt(3, uF.parseToInt(getStrEmpId()));
//						System.out.println("in if pst ===>> " + pst);
						pst.execute();
						pst.close();
						
						pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in (select task_id " +
							" from task_activity where emp_id=? and task_id in ("+sbTaskId.toString()+"))");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.execute();
//						System.out.println("delete pst ===>> " + pst);
						pst.close();
						
					} else {
						pst = con.prepareStatement("update task_activity set is_approved = ?,is_billable_approved = ?,approved_by=null,approved_date=null,unblock_by=?, submited_date=null where emp_id=? and task_id in ("+sbTaskId.toString()+")");
						pst.setInt(1, 0);
						pst.setInt(2, 0);
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setInt(4, uF.parseToInt(getStrEmpId()));
//						System.out.println("in else pst ===>> " + pst);
						pst.execute();
						pst.close();
					}
					
					pst = con.prepareStatement("update task_activity set is_approved = ?,is_billable_approved = ?,approved_by=null,approved_date=null,unblock_by=null,unblock_time=null, submited_date=null where emp_id=? and task_id in ("+sbTaskId.toString()+") and is_approved != 2");
					pst.setInt(1, 0);
					pst.setInt(2, 0);
					pst.setInt(3, uF.parseToInt(getStrEmpId()));
					pst.execute();
					pst.close();

					pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in (select task_id " +
						" from task_activity where emp_id=? and task_id in ("+sbTaskId.toString()+") and is_approved != 2)");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.execute();
					pst.close();

				}
				
				/**
				 * Notification
				 * */
				
				Notifications nF = new Notifications(N_TIMESHEET_RE_OPENED, CF); 
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);
				
				nF.setStrEmpId(getStrEmpId());
				nF.setStrFromDate(arr[0]);
				nF.setStrToDate(arr[1]);
				nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.sendNotifications();
				
//				System.out.println("APA1/1305--strUserType="+strUserType);
//				System.out.println("APA1/1306--isAttendFromTimesheetDetails="+isAttendFromTimesheetDetails);
				if (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(ADMIN)) {
					if(isAttendFromTimesheetDetails) {
//						System.out.println("isAttendFromTimesheetDetails ===>> " + isAttendFromTimesheetDetails);
						boolean claimExtraWorkFlag = (Boolean)request.getAttribute("claimExtraWorkFlag");
						if(claimExtraWorkFlag) {
//							System.out.println("APA1/1306--claimExtraWorkFlag="+claimExtraWorkFlag);
							deleteCompOffLeave(con , uF, arr[0], arr[1],false,levelID);
							deleteHolidaysCount(con , uF, arr[0], arr[1]);
							deleteAttendanceDetails(con, uF, arr[0], arr[1]);
						}
					}
				} 

				session.setAttribute(MESSAGE,SUCCESSM+ "You have successfully unlocked the timesheet. Please ensure your team member resubmits the timesheet for your approval."+ END);

			} else if (arr != null && arr.length > 2 && getType() != null && getType().equalsIgnoreCase("approve") && (!strUserType.equalsIgnoreCase(HRMANAGER) || (strUserType.equalsIgnoreCase(HRMANAGER) && getPageType() != null && getPageType().equals("MP"))) && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
				
				List<String> checkTaskList = new ArrayList<String>();
				if (getCheckTask() != null && !getCheckTask().equals("")) {
					checkTaskList = Arrays.asList(getCheckTask().split(","));
				}
//				 System.out.println("getCheckTask() =====>"+getCheckTask());
				 
				Map<String, String> hmEmpTaskActivityDate = new LinkedHashMap<String, String>();
				
				
				for(int a=0; checkTaskList!=null && a<checkTaskList.size(); a++) {
					if(checkTaskList.get(a) != null && !checkTaskList.get(a).trim().equals("")) {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("select * from task_activity where emp_id=? and task_date between ? and ? ");
						if(uF.parseToInt(checkTaskList.get(a))>0) {
							sbQuery.append(" and activity_id = "+uF.parseToInt(checkTaskList.get(a))+" ");
						} else {
							sbQuery.append(" and activity = '"+checkTaskList.get(a)+"'");
						}
						sbQuery.append(" order by task_date");
						pst = con.prepareStatement(sbQuery.toString());
	//					pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date between ? and ? order by task_date");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
//						System.out.println("pst ===>> " + pst);
						rs = pst.executeQuery();
		
						while (rs.next()) {
							String activity = rs.getString("activity") != null && !rs.getString("activity").equals("") ? rs.getString("activity") : "0";
							String task_data = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT) + ":_:" + rs.getString("activity_id") + ":_:" + activity;
							hmEmpTaskActivityDate.put(rs.getString("task_id"), task_data);
						}
						rs.close();
						pst.close();
					}
				}
				
//				Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
//				while (it.hasNext()) {
//					String task_id = (String) it.next(); // task_date
//
//					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
//						
//						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && (getPageType() == null && !getPageType().equals("MP"))) {
//							boolean taskApproveFlag = false;
//							pst = con.prepareStatement("select is_approved from task_activity  where emp_id=? and task_id=? and is_approved = 2");
//							pst.setInt(1, uF.parseToInt(getStrEmpId()));
//							pst.setInt(2, uF.parseToInt(task_id));
//							rs = pst.executeQuery();
//							while(rs.next()) {
//								taskApproveFlag = true;
//							}
//							rs.close();
//							pst.close();
//							
//							if(!taskApproveFlag) {
//								pst = con.prepareStatement("update task_activity set approved_by=?, is_approved=?, is_billable_approved=?, approved_date=? where emp_id=? and task_id=? and is_approved = 1");
//								pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
//								pst.setInt(2, 2);
//								pst.setInt(3, 1);
//								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
//								pst.setInt(5, uF.parseToInt(getStrEmpId()));
//								pst.setInt(6, uF.parseToInt(task_id));
//								pst.execute();
//								pst.close();
//							}
//							flag = true;
//							
////							************ if global HR id is in work_flow_details ***************** 
//							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_TIMESHEET+"' and is_approved=0 and emp_id=? order by work_flow_id");
//							pst.setInt(1, uF.parseToInt(task_id));
//							pst.setInt(2, uF.parseToInt(strSessionEmpId));
//							rs = pst.executeQuery();
//							int work_id = 0;
//							while (rs.next()) {
//								work_id = rs.getInt("work_flow_id");
//								break;
//							}
//							rs.close();
//							pst.close();
//
//							boolean taskApproveByWFFlag = false;
//							pst = con.prepareStatement("select is_approved from work_flow_details where work_flow_id=? and is_approved = 1");
//							pst.setInt(1, work_id);
//							rs = pst.executeQuery();
//							while(rs.next()) {
//								taskApproveByWFFlag = true;
//							}
//							rs.close();
//							pst.close();
//							
//							if(!taskApproveByWFFlag) {
//								pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=? WHERE work_flow_id=?");
//								pst.setInt(1, 1);
//								pst.setInt(2, uF.parseToInt(strSessionEmpId));
//								pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
//								pst.setInt(4, work_id);
//								pst.execute();
//								pst.close();
//							}
////							************ if global HR id is in work_flow_details *****************
//
//						} else {
//							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_TIMESHEET+"' and is_approved=0 and emp_id=? order by work_flow_id");
//							pst.setInt(1, uF.parseToInt(task_id));
//							pst.setInt(2, uF.parseToInt(strSessionEmpId));
//							rs = pst.executeQuery();
//							int work_id = 0;
//							while (rs.next()) {
//								work_id = rs.getInt("work_flow_id");
//								break;
//							}
//							rs.close();
//							pst.close();
//
//							boolean taskApproveByWFFlag = false;
//							pst = con.prepareStatement("select is_approved from work_flow_details where work_flow_id=? and is_approved = 1");
//							pst.setInt(1, work_id);
//							rs = pst.executeQuery();
//							while(rs.next()) {
//								taskApproveByWFFlag = true;
//							}
//							rs.close();
//							pst.close();
//							
//							if(!taskApproveByWFFlag) {
//								pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=? WHERE work_flow_id=?");
//								pst.setInt(1, 1);
//								pst.setInt(2, uF.parseToInt(strSessionEmpId));
//								pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
//								pst.setInt(4, work_id);
//								pst.execute();
//								pst.close();
//							}
//							
//							
//							boolean taskApproveByLastMPFlag = false;
//							pst = con.prepareStatement("select is_approved from work_flow_details where member_position in(select max(member_position) as member_position from work_flow_details where effective_id=?) and effective_id=?");
//							pst.setInt(1, uF.parseToInt(task_id));
//							pst.setInt(2, uF.parseToInt(task_id));
//							rs = pst.executeQuery();
//							while(rs.next()) {
//								if(rs.getInt("is_approved") == 1) {
//									taskApproveByLastMPFlag = true;
//								}
//							}
//							rs.close();
//							pst.close();
//							
//							boolean taskApproveFlag = false;
//							pst = con.prepareStatement("select is_approved from task_activity where emp_id=? and task_id=? and is_approved = 2");
//							pst.setInt(1, uF.parseToInt(getStrEmpId()));
//							pst.setInt(2, uF.parseToInt(task_id));
//							rs = pst.executeQuery();
//							while(rs.next()) {
//								taskApproveFlag = true;
//							}
//							rs.close();
//							pst.close();
//							
//							if(!taskApproveFlag && taskApproveByLastMPFlag) {
//								pst = con.prepareStatement("update task_activity set approved_by =?, is_approved = ?,is_billable_approved = ?, approved_date=?  where emp_id=? and task_id=? and is_approved = 1");
//								pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
//								pst.setInt(2, 2);
//								pst.setInt(3, 1);
//								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
//								pst.setInt(5, uF.parseToInt(getStrEmpId()));
//								pst.setInt(6, uF.parseToInt(task_id));
//								pst.execute();
//								pst.close();
//							}
//						}
//					} else {
//						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && (getPageType() == null && !getPageType().equals("MP"))) {
//							boolean taskApproveFlag = false;
//							pst = con.prepareStatement("select is_approved from task_activity  where emp_id=? and task_id=? and is_approved = 2");
//							pst.setInt(1, uF.parseToInt(getStrEmpId()));
//							pst.setInt(2, uF.parseToInt(task_id));
//							rs = pst.executeQuery();
//							while(rs.next()) {
//								taskApproveFlag = true;
//							}
//							rs.close();
//							pst.close();
//							
//							if(!taskApproveFlag) {
//								pst = con.prepareStatement("update task_activity set approved_by =?, is_approved = ?,is_billable_approved = ?, approved_date=?  where emp_id=? and task_id=? and is_approved = 1");
//								pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
//								pst.setInt(2, 2);
//								pst.setInt(3, 1);
//								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
//								pst.setInt(5, uF.parseToInt(getStrEmpId()));
//								pst.setInt(6, uF.parseToInt(task_id));
//								pst.execute();
//								pst.close();
//							}
//							flag = true;
//						}
//					}
//
//				}
				
//				System.out.println("hmEmpTaskActivityDate ===>> " + hmEmpTaskActivityDate);
				Iterator<String> it = hmEmpTaskActivityDate.keySet().iterator();
				StringBuilder sbTaskId = null;
				List<String> alTaskId = new ArrayList<String>(); 
				while (it.hasNext()) {
					String task_id = (String) it.next(); // task_date
					if(!alTaskId.contains(task_id)){
						alTaskId.add(task_id);
					}	
					if(sbTaskId == null){
						sbTaskId = new StringBuilder();
						sbTaskId.append(task_id);
					} else {
						sbTaskId.append(","+task_id);
					}	
				}
				
				if(sbTaskId !=null) {
					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						
						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && (getPageType() == null || !getPageType().equals("MP"))) {
							pst = con.prepareStatement("select task_id from task_activity  where emp_id=? and task_id in ("+sbTaskId.toString()+") and is_approved = 2");
							pst.setInt(1, uF.parseToInt(getStrEmpId()));
//							System.out.println("select pst ===>> " + pst);
							rs = pst.executeQuery();
							while(rs.next()) {
								alTaskId.remove(rs.getString("task_id"));
							}
							rs.close();
							pst.close();
							
							String strTaskIds = StringUtils.join(alTaskId.toArray(),",");
//							System.out.println("strTaskIds ===>> " + strTaskIds);
							if(strTaskIds !=null && !strTaskIds.trim().equals("")) {
								pst = con.prepareStatement("update task_activity set approved_by=?, is_approved=?, is_billable_approved=?, approved_date=? where emp_id=? and task_id in ("+strTaskIds+") and is_approved = 1");
								pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setInt(2, 2);
								pst.setInt(3, 1);
								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(5, uF.parseToInt(getStrEmpId()));
//								System.out.println("update pst ===>> " + pst);
								pst.execute();
								pst.close();
							}
							flag = true;
//							System.out.println("flag 1 ===>> " + flag);
							
	//							************ if global HR id is in work_flow_details ***************** 
							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id in ("+sbTaskId.toString()+") and effective_type='"+WORK_FLOW_TIMESHEET+"' and is_approved=0 and emp_id=? order by work_flow_id");
							pst.setInt(1, uF.parseToInt(strSessionEmpId));
							rs = pst.executeQuery();
							StringBuilder sbWorkFlowId = null;
							while (rs.next()) {
								if(sbWorkFlowId == null){
									sbWorkFlowId = new StringBuilder();
									sbWorkFlowId.append(rs.getString("work_flow_id"));
								} else {
									sbWorkFlowId.append(","+rs.getString("work_flow_id"));
								}
							}
							rs.close();
							pst.close();
	
							if(sbWorkFlowId !=null) {
								pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=? WHERE work_flow_id in ("+sbWorkFlowId.toString()+")");
								pst.setInt(1, 1);
								pst.setInt(2, uF.parseToInt(strSessionEmpId));
								pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.execute();
								pst.close();
							}
	//							************ if global HR id is in work_flow_details *****************
	
						} else {
							pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id in ("+sbTaskId.toString()+") and effective_type='"+WORK_FLOW_TIMESHEET+"' and is_approved=0 and emp_id=? order by work_flow_id");
							pst.setInt(1, uF.parseToInt(strSessionEmpId));
//							System.out.println("else pst ===>> " + pst);
							rs = pst.executeQuery();
							StringBuilder sbWorkFlowId = null;
							while (rs.next()) {
								if(sbWorkFlowId == null){
									sbWorkFlowId = new StringBuilder();
									sbWorkFlowId.append(rs.getString("work_flow_id"));
								} else {
									sbWorkFlowId.append(","+rs.getString("work_flow_id"));
								}
							}
							rs.close();
							pst.close();
	
							if(sbWorkFlowId !=null) {
								pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=? WHERE work_flow_id in ("+sbWorkFlowId.toString()+")");
								pst.setInt(1, 1);
								pst.setInt(2, uF.parseToInt(strSessionEmpId));
								pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.execute();
								pst.close();
							}

							
							Map<String, String> hmTaskApproveByLastMP = new HashMap<String, String>();
							pst = con.prepareStatement("select effective_id from work_flow_details where member_position in(select max(member_position) as member_position " +
								"from work_flow_details where effective_id in ("+sbTaskId.toString()+")) and effective_id in ("+sbTaskId.toString()+") and is_approved = 1");
							rs = pst.executeQuery();
							while(rs.next()) {
								hmTaskApproveByLastMP.put(rs.getString("effective_id"), rs.getString("effective_id"));
							}
							rs.close();
							pst.close();
							
							
							pst = con.prepareStatement("select task_id from task_activity  where emp_id=? and task_id in ("+sbTaskId.toString()+") and is_approved = 2");
							pst.setInt(1, uF.parseToInt(getStrEmpId()));
							rs = pst.executeQuery();
							while(rs.next()) {
								alTaskId.remove(rs.getString("task_id"));
							}
							rs.close();
							pst.close();
							
							pst = con.prepareStatement("update task_activity set approved_by =?, is_approved = ?,is_billable_approved = ?, approved_date=?  where emp_id=? and task_id=? and is_approved = 1");
							boolean flag1 = false;
							for(String taskId : alTaskId) {
								if(uF.parseToInt(hmTaskApproveByLastMP.get(taskId)) > 0) {
									pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
									pst.setInt(2, 2);
									pst.setInt(3, 1);
									pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(5, uF.parseToInt(getStrEmpId()));
									pst.setInt(6, uF.parseToInt(taskId));
									pst.addBatch();
									flag1 = true;
								}
							}
							
							if(flag1) {
								int[] x = pst.executeBatch();
								pst.close();
							} else {
								if(pst != null) {
									pst.close();
								}
							}
						}
					} else {
						if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN) && (getPageType() == null && !getPageType().equals("MP"))) {
							pst = con.prepareStatement("select task_id from task_activity  where emp_id=? and task_id in ("+sbTaskId.toString()+") and is_approved = 2");
							pst.setInt(1, uF.parseToInt(getStrEmpId()));
							rs = pst.executeQuery();
							while(rs.next()) {
								alTaskId.remove(rs.getString("task_id"));
							}
							rs.close();
							pst.close();
							
							String strTaskIds = StringUtils.join(alTaskId.toArray(),",");
							if(strTaskIds !=null && !strTaskIds.trim().equals("")){
								pst = con.prepareStatement("update task_activity set approved_by=?, is_approved=?, is_billable_approved=?, approved_date=? where emp_id=? and task_id in ("+strTaskIds+") and is_approved = 1");
								pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setInt(2, 2);
								pst.setInt(3, 1);
								pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(5, uF.parseToInt(getStrEmpId()));
								pst.execute();
								pst.close();
							}
							flag = true;
//							System.out.println("flag 0 ===>> " + flag);
						}
					}
				}

				/**
				 * Notification
				 * */
				
				Notifications nF = new Notifications(N_TIMESHEET_APPROVED, CF); 
				nF.setDomain(strDomain);
				
				nF.request = request;
				nF.setStrOrgId((String)session.getAttribute(ORGID));
				nF.setEmailTemplate(true);
				
				nF.setStrEmpId(getStrEmpId());
				nF.setStrFromDate(arr[0]);
				nF.setStrToDate(arr[1]);
				nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.sendNotifications();
				
				
//				 System.out.println("getCompOff().length ===>> " + getCompOff().length);
				if (getCompOffDate() != null && getCompOffDate().length > 0 && getCompOff() != null && getCompOff().length > 0) {

					Set<String> comOffDateSet = new HashSet<String>(Arrays.asList(getCompOff()));
					Set<String> comOffDate = new HashSet<String>(Arrays.asList(getCompOffDate()));
					Iterator<String> iterator = comOffDate.iterator();
					ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
					while (iterator.hasNext()) {
						String coffDate = iterator.next();

//						pst = con.prepareStatement("select * from emp_leave_entry  where emp_id=? and is_approved in(1,-1) and is_compensate=true and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
						pst = con.prepareStatement("select * from emp_leave_entry  where emp_id=? and is_approved in(1,-1) and is_compensate=true " +
								"and ? between approval_from and approval_to_date ");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setDate(2, uF.getDateFormat(coffDate, DATE_FORMAT));
						rs = pst.executeQuery();
						boolean flag1 = false;
						while (rs.next()) {
							flag1 = true;
						}
						rs.close();
						pst.close();
						
						if (flag1) {
							continue;
						}

//						pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?, approval_from=?, approval_to_date=?, emp_no_of_leave=?, user_id=? "
//							+ " where emp_id=? and is_compensate=true and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
						pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?, approval_from=?, approval_to_date=?, emp_no_of_leave=?, user_id=? "
								+ " where emp_id=? and is_compensate=true and ? between approval_from and approval_to_date");
						String req = "Approve Request";
						if (!comOffDateSet.contains(coffDate)) {
							pst.setInt(1, -1);
							req = "Deny request";
						} else {
							pst.setInt(1, 1);
						}
						pst.setString(2, req);
						pst.setDate(3, uF.getDateFormat(coffDate, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(coffDate, DATE_FORMAT));
						int nAppliedDays = 0;

						nAppliedDays = uF.parseToInt(uF.dateDifference(coffDate, DATE_FORMAT, coffDate, DATE_FORMAT,CF.getStrTimeZone()));
						pst.setInt(5, nAppliedDays);

						pst.setInt(6, uF.parseToInt(strSessionEmpId));
						pst.setInt(7, uF.parseToInt(getStrEmpId()));
						pst.setDate(8, uF.getDateFormat(coffDate, DATE_FORMAT));
//						System.out.println("pst ===>> " + pst);
						pst.execute();
						pst.close();
						
//						System.out.println("comOffDateSet ===>> " + comOffDateSet + " -- coffDate ===>> " + coffDate);
						if (comOffDateSet.contains(coffDate)) {

//							pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and is_compensate=true "
//								+ "and (approval_from, approval_to_date) overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD'))");
							pst = con.prepareStatement("select * from emp_leave_entry where emp_id=? and is_compensate=true "
									+ "and ? between approval_from and approval_to_date");
							pst.setInt(1, uF.parseToInt(getStrEmpId()));
							pst.setDate(2,uF.getDateFormat(coffDate, DATE_FORMAT));
//							System.out.println("APA1/1779--pst ===>> " + pst);
							rs = pst.executeQuery();
							String leave_id = "";
							String typeOfLeave = "";
							while (rs.next()) {
								leave_id = rs.getString("leave_id");
								typeOfLeave = rs.getString("leave_type_id");
							}
							rs.close();
							pst.close();

//							System.out.println("APA1/1790--leave_id ===>> " + leave_id+"--typeOfLeave="+typeOfLeave);
							
							leaveApproval.setServletRequest(request);
							leaveApproval.setLeaveId(leave_id);
							leaveApproval.setTypeOfLeave(typeOfLeave);
							leaveApproval.setEmpId(getStrEmpId());
							leaveApproval.setIsapproved(1);
							leaveApproval.setApprovalFromTo(coffDate);
							leaveApproval.setApprovalToDate(coffDate);
							leaveApproval.insertLeaveBalance(con, pst, rs, uF,uF.parseToInt(hmEmpLevelMap.get(getStrEmpId())), CF);
						}

					}
				}
				
				boolean flag1 = true;
				pst = con.prepareStatement("select * from task_activity  where emp_id=? and is_approved in(0,1) and task_date between ? and ?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
//				pst.setInt(4, uF.parseToInt(arr[2]));	
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					flag1 = false;
				}
				rs.close();
				pst.close();
				
				if (flag1) {
					flag = true;
				}
//				System.out.println("flag ===>> " + flag);
				insertInHolidaysCount(con,uF,arr[0], arr[1]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		if (flag) {
//			System.out.println("in flag ===>> " + flag+ " -- isAttendFromTimesheetDetails ===>> " + isAttendFromTimesheetDetails);
			if(isAttendFromTimesheetDetails) {
				insertintoAttendance();
			}
		}
	}
	

	
	private void insertTimsheetApprovalMember(Connection con, PreparedStatement pst, ResultSet rs, Map<String, String> hmEmpTaskActivityDateByEmp, UtilityFunctions uF) {
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			
			pst = con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where policy_count=? and policy_type='1' " +
				"and wfp.work_flow_member_id = a.work_flow_member_id order by member_position");
			pst.setInt(1, uF.parseToInt(getPolicy_id()));
//			System.out.println("pst ==>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmMemberMap = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));

				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmMemberMap ===>> " + hmMemberMap);
			
			Iterator<String> it1 = hmEmpTaskActivityDateByEmp.keySet().iterator();
			pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
			while (it1.hasNext()) {
				String task_id = (String) it1.next(); // task_date

				pst.setInt(1, uF.parseToInt(task_id));
				pst.setString(2, WORK_FLOW_TIMESHEET);
				pst.addBatch();
			}
			int[] x = pst.executeBatch();
			pst.close();
			
			
			Iterator<String> it = hmMemberMap.keySet().iterator();
//			System.out.println("hmMemberMap ==>> " + hmMemberMap);
			while (it.hasNext()) {
				String work_flow_member_id = it.next();
//				System.out.println("work_flow_member_id ==>> " + work_flow_member_id);
				List<String> innerList = hmMemberMap.get(work_flow_member_id);
				int memid = uF.parseToInt(innerList.get(1));

				if (uF.parseToInt(innerList.get(0)) == 3) {

					String[] empid = request.getParameterValues(innerList.get(3) + memid);
					for (int i = 0; empid != null && i < empid.length; i++) {
						if (empid[i] != null && !empid[i].equals("")) {
							int userTypeId =  uF.parseToInt(hmEmpUserTypeId.get(empid[i]));
							boolean flag = false;
							pst = con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id) values (?,?,?,?, ?,?,?,?, ?)");
							
							Iterator<String> it2 = hmEmpTaskActivityDateByEmp.keySet().iterator();
							while (it2.hasNext()) {
								String task_id = (String) it2.next();
								
								pst.setInt(1, uF.parseToInt(empid[i]));
								pst.setInt(2, uF.parseToInt(task_id));
								pst.setString(3, WORK_FLOW_TIMESHEET);
								pst.setInt(4, uF.parseToInt(innerList.get(0)));
	//							pst.setInt(5, i + 1);
								pst.setInt(5, 1);
								pst.setInt(6, uF.parseToInt(innerList.get(4)));
								pst.setInt(7, 0);
								pst.setInt(8, 0);
								pst.setInt(9,userTypeId);
								pst.addBatch();
								flag = true;
							}
							if(flag){
								int[] x1 = pst.executeBatch();
								pst.close();
							} else {
								if(pst !=null){
									pst.close();
								}
							}
						}
					}
					
				} else {
//					System.out.println("innerList.get(3) ==>> " + innerList.get(3) +" -- memid ==>> "+ memid);
					String empid = request.getParameter(innerList.get(3) + memid);
//					System.out.println("empid  ==>> " + empid);
					
					if (empid != null && !empid.equals("")) {
						int userTypeId = memid;
						boolean flag = false;
						pst = con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position,"
								+ "work_flow_mem_id,is_approved,status,user_type_id) values(?,?,?,?, ?,?,?,?, ?)");
						Iterator<String> it2 = hmEmpTaskActivityDateByEmp.keySet().iterator();
						while (it2.hasNext()) {
							String task_id = (String) it2.next();
							
							pst.setInt(1, uF.parseToInt(empid));
							pst.setInt(2, uF.parseToInt(task_id));
							pst.setString(3, WORK_FLOW_TIMESHEET);
							pst.setInt(4, uF.parseToInt(innerList.get(0)));
							pst.setInt(5, uF.parseToInt(innerList.get(2)));
							pst.setInt(6, uF.parseToInt(innerList.get(4)));
							pst.setInt(7, 0);
							pst.setInt(8, 0);
							pst.setInt(9, userTypeId);
							pst.addBatch();
//							System.out.println("pst ===>> " + pst);
							flag = true;
						}
						if(flag) {
							try { 
								int[] x1 = pst.executeBatch(); 
							} catch(BatchUpdateException e) {
								throw e.getNextException(); 
							}  finally {
	//							int[] x1 = pst.executeBatch();
								pst.close();
							}
						} else {
							if(pst !=null){
								pst.close();
							}
						}
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
	private Map<String, String> getProjectIdMapOnTaskId(Connection con) {
		Map<String, String> hmProIds = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select task_id,pro_id from activity_info");
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProIds.put(rs.getString("task_id"), rs.getString("pro_id"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return hmProIds;
	}

	private Map<String, Set<String>> getProjectTLMap(Connection con, UtilityFunctions uF) {
		Map<String, Set<String>> hmProTl = new HashMap<String, Set<String>>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select emp_id, pro_id from project_emp_details where _isteamlead = true");
			rs = pst.executeQuery();
			Set<String> setTl = new HashSet<String>();
			while(rs.next()) {
				setTl = hmProTl.get(rs.getString("pro_id"));
				if(setTl == null) setTl = new HashSet<String>();
				if(uF.parseToInt(rs.getString("emp_id")) > 0) {
					setTl.add(rs.getString("emp_id"));
				}
				hmProTl.put(rs.getString("pro_id"), setTl);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return hmProTl;
	}
	
//===start parvez date: 12-10-2022===
	
	/*private Map<String, String> getProjectOwnerMap(Connection con) {
		Map<String, String> hmProOwner = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select pro_id,project_owner from projectmntnc");
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmProOwner.put(rs.getString("pro_id"), rs.getString("project_owner"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return hmProOwner;
	}*/
	
	private Map<String, List<String>> getProjectOwnerMap(Connection con) {
		Map<String, List<String>> hmProOwner = new HashMap<String, List<String>>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select pro_id,project_owners from projectmntnc");
			
//			System.out.println("APA1/2112---pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("project_owners")!=null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					List<String> ownersList1 = new ArrayList<String>();
					for(int j=0; j<tempList.size();j++){
						if(j>0){
							ownersList1.add(tempList.get(j));
						}
					}
					hmProOwner.put(rs.getString("pro_id"), ownersList1);
				}
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return hmProOwner;
	}
//===end parvez date: 12-10-2022===	
	

	public void deleteAttendanceDetails(Connection con, UtilityFunctions uF, String strD1, String strD2) {
		PreparedStatement pst=null;
		
		try {
			pst=con.prepareStatement("delete from attendance_details where emp_id=? and to_date(in_out_timestamp::text,'yyyy-MM-dd') between ? and ? ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("APA1/2090--delete attendance=====>"+pst);
			pst.execute();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	public void deleteHolidaysCount(Connection con, UtilityFunctions uF, String strD1, String strD2) {
		PreparedStatement pst=null;
		
		try {
			pst=con.prepareStatement("delete from holiday_count where emp_id=? and to_date(_date::text,'yyyy-MM-dd') between ? and ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("delete holiday_count=====>"+pst);
			pst.execute();
			pst.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertInHolidaysCount(Connection con, UtilityFunctions uF, String strD1, String strD2) {
		List<String> holidayCountList=new ArrayList<String>();
		if(getUnPaidHolidaysDate()!=null) {
			holidayCountList=Arrays.asList(getUnPaidHolidaysDate());
		}
		PreparedStatement pst=null;
		ResultSet rs=null;
		
		try {
			for(int i=0;holidayCountList!=null && i<holidayCountList.size();i++) {
				boolean flag=false;
				pst=con.prepareStatement("select * from holiday_count where emp_id=? and _date=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(holidayCountList.get(i), DATE_FORMAT));
				rs=pst.executeQuery();
				while(rs.next()) {
					flag=true;
				}
				rs.close();
				pst.close();
				
				if(!flag) {
					pst=con.prepareStatement("insert into holiday_count(emp_id,_date,approved_by,approved_date) values (?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setDate(2, uF.getDateFormat(holidayCountList.get(i), DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.execute();
					pst.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	public void deleteCompOffLeave(Connection con, UtilityFunctions uF,String std1,String std2, boolean flag,String levelID) {

		PreparedStatement pst=null;
		ResultSet rs=null;
		
		try {
//			System.out.println("APA1/2158--deleteCompOffLeave");
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from emp_leave_entry where emp_id=? and ((? between approval_from and approval_to_date) or " +
					"(? between approval_from and approval_to_date) or (approval_from >= ? and approval_from<=?))");
			sbQuery.append(" and leave_type_id in (select lt.leave_type_id from leave_type lt, " +
					"emp_leave_type elt where lt.leave_type_id=elt.leave_type_id " +
					"and lt.is_compensatory=true group by lt.leave_type_id)");
			if(flag) {
				sbQuery.append(" and is_approved=0");
			}
			pst = con.prepareStatement(sbQuery.toString());			
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(std1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(std2, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(std1, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(std2, DATE_FORMAT));
//			System.out.println("APA/2160--pst ===>> " + pst);
			rs = pst.executeQuery();
			String leave_id = null;
			int lBalanceCount = 0;
			int comLeaveid = 0;
			while (rs.next()) {
				if (leave_id == null) {
					leave_id = rs.getString("leave_id");
				} else {
					leave_id += "," + rs.getString("leave_id");
				}
				lBalanceCount++;
				comLeaveid = rs.getInt("leave_type_id");
			}
			rs.close();
			pst.close();
//			System.out.println("APA1/2191--lBalanceCount="+lBalanceCount);
			
			if (leave_id != null && comLeaveid > 0) {
				
				String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
				String locationID = CF.getEmpWlocationId(con, uF, getStrEmpId());
				
				pst = con.prepareStatement("delete from leave_application_register where emp_id=? and leave_id in (" + leave_id + ")");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				System.out.println("APA/2184--pst ===>> " + pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_LEAVE+"' and effective_id in " +
						"(select leave_id from emp_leave_entry where emp_id=? and leave_id in (" + leave_id + "))");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst ===>> " + pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from emp_leave_entry where emp_id=? and leave_id in (" + leave_id + ")");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst ===>> " + pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select * from emp_leave_type where level_id=? and leave_type_id=? and org_id=? and wlocation_id=? order by emp_leave_type_id desc limit 1");
				pst.setInt(1, uF.parseToInt(levelID));
				pst.setInt(2, comLeaveid);
				pst.setInt(3, uF.parseToInt(strOrgId));
				pst.setInt(4, uF.parseToInt(locationID));
//				System.out.println("APA1/2221---pst======>"+pst);
				rs = pst.executeQuery();
				int leaveId = 0;
				while (rs.next()) {
					leaveId = rs.getInt("compensate_with");
				}
				rs.close();
				pst.close();
				
				if(leaveId > 0) {
					pst = con.prepareStatement("delete from leave_register1 where emp_id=? and leave_type_id=? and _date between ? and ? and compensate_id=?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, leaveId);
					pst.setDate(3, uF.getDateFormat(std1, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(std2, DATE_FORMAT));
					pst.setInt(5, comLeaveid);
//					System.out.println("APA1/2237--pst ===>> " + pst);
					pst.execute();
					pst.close();
				}
				/*double balance = 0;
				double accured = 0;
				pst = con.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? order by register_id desc limit 1");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, leaveId);
				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					balance = rs.getDouble("balance");
					accured = rs.getDouble("accrued");
				}
				rs.close();
				pst.close();
	
				if (balance > 0 && leaveId > 0) {
					balance = balance - lBalanceCount;
					accured = accured - lBalanceCount;
	
					pst = con.prepareStatement("update leave_register1 set balance=?,accrued=? where emp_id=? and leave_type_id=?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, comLeaveid);
					pst.setDouble(3, balance);
					pst.setDouble(4, accured);
					System.out.println("pst ===>> " + pst);
					pst.execute();
					pst.close();
				}*/
				
				
				
		//===start parvez date: 16-10-2021===	
				/*for(int i=0; i<lBalanceCount; i++){*/
				
					
					double balance = 0;
					String leaveRegisterId = null;
					pst = con.prepareStatement("select * from leave_application_register where emp_id=? and leave_type_id=? " +
							"and leave_id in (select leave_id from emp_leave_entry where emp_id=? and ((? between approval_from and approval_to_date) or " +
							"(? between approval_from and approval_to_date) or (approval_from >= ? and approval_from<=?))" +
							") and (is_modify is null or is_modify=false) order by leave_register_id");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, leaveId);
					pst.setInt(3, uF.parseToInt(getStrEmpId()));
					pst.setDate(4, uF.getDateFormat(std1, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(std2, DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(std1, DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(std2, DATE_FORMAT));
//					System.out.println("APA1/2276--pst ===>> " + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
//						balance = rs.getDouble("balance");
						if (leaveRegisterId == null) {
							leaveRegisterId = rs.getString("leave_register_id");
						} else {
							leaveRegisterId += "," + rs.getString("leave_register_id");
						}
					}
					rs.close();
					pst.close();
		
					/*if (balance > 0 && leaveId > 0) {*/
						
		
						pst = con.prepareStatement("update leave_application_register set is_paid=false where emp_id=? and leave_type_id=? and leave_register_id in (" + leaveRegisterId + ")");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setInt(2, leaveId);
//						System.out.println("APA1/2305---pst ===>> " + pst);
						pst.execute();
						pst.close();
					/*}*/
				/*}*/
				/*if(leaveRegisterId != null){
					String[] newLeaveRegisterId = leaveRegisterId.split(",");		
					for(int i=0; newLeaveRegisterId!=null && i<newLeaveRegisterId.length; i++){
						pst = con.prepareStatement("select * from leave_application_register where leave_register_id=?");
						pst.setInt(1, uF.parseToInt(newLeaveRegisterId[i]));
						rs = pst.executeQuery();
						while(rs.next()){
							balance = rs.getDouble("balance");
						}
						rs.close();
						pst.close();
						
						if(balance > 0){
							balance -= 1;
							
							pst = con.prepareStatement("update leave_application_register set is_paid=false, balance=? where emp_id=? and leave_register_id=? ");
							pst.setDouble(1, balance);
							pst.setInt(2, uF.parseToInt(getStrEmpId()));
							pst.setInt(3, uF.parseToInt(newLeaveRegisterId[i]));
							System.out.println("APA1/2333---pst ===>> " + pst);
							pst.execute();
							pst.close();
						}else{
							pst = con.prepareStatement("update leave_application_register set is_paid=false where emp_id=? and leave_register_id=? ");
//							pst.setDouble(1, balance);
							pst.setInt(1, uF.parseToInt(getStrEmpId()));
							pst.setInt(2, uF.parseToInt(newLeaveRegisterId[i]));
							System.out.println("APA1/2341---pst ===>> " + pst);
							pst.execute();
							pst.close();
						}
					}
				}*/
				
				
		//===end parvez date: 16-10-2021===
				
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//====added by parvez date: 15-11-2021===
	//===start===
	public void deleteCompOffLeaveOnSubmit(Connection con, UtilityFunctions uF,String std1,String std2,List<String> strCompOffDate, boolean flag,String levelID) {

		PreparedStatement pst=null;
		ResultSet rs=null;
		
		try {
			
			String leave_id = null;
			int comLeaveid = 0;
			for(int i=0; i<strCompOffDate.size(); i++){
				leave_id = null;
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select * from emp_leave_entry where emp_id=? and ((? between approval_from and approval_to_date) or " +
						"(? between approval_from and approval_to_date) or (approval_from >= ? and approval_from<=?))");
				sbQuery.append(" and leave_type_id in (select lt.leave_type_id from leave_type lt, " +
						"emp_leave_type elt where lt.leave_type_id=elt.leave_type_id " +
						"and lt.is_compensatory=true group by lt.leave_type_id)");
				if(flag) {
					sbQuery.append(" and is_approved=0");
				}
				pst = con.prepareStatement(sbQuery.toString());			
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(strCompOffDate.get(i), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strCompOffDate.get(i), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strCompOffDate.get(i), DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strCompOffDate.get(i), DATE_FORMAT));
//				System.out.println("APA/2160--pst ===>> " + pst);
				rs = pst.executeQuery();
				
				int lBalanceCount = 0;
				
				while (rs.next()) {
					if (leave_id == null) {
						leave_id = rs.getString("leave_id");
					} else {
						leave_id += "," + rs.getString("leave_id");
					}
					lBalanceCount++;
					comLeaveid = rs.getInt("leave_type_id");
				}
				rs.close();
				pst.close();
			}
			
			
			
			if (leave_id != null && comLeaveid > 0) {
				
				String strOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
				String locationID = CF.getEmpWlocationId(con, uF, getStrEmpId());
				
				pst = con.prepareStatement("delete from leave_application_register where emp_id=? and leave_id in (" + leave_id + ")");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				System.out.println("APA/2184--pst ===>> " + pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from work_flow_details where effective_type='"+WORK_FLOW_LEAVE+"' and effective_id in " +
						"(select leave_id from emp_leave_entry where emp_id=? and leave_id in (" + leave_id + "))");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst ===>> " + pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from emp_leave_entry where emp_id=? and leave_id in (" + leave_id + ")");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst ===>> " + pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("select * from emp_leave_type where level_id=? and leave_type_id=? and org_id=? and wlocation_id=? order by emp_leave_type_id desc limit 1");
				pst.setInt(1, uF.parseToInt(levelID));
				pst.setInt(2, comLeaveid);
				pst.setInt(3, uF.parseToInt(strOrgId));
				pst.setInt(4, uF.parseToInt(locationID));
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				int leaveId = 0;
				while (rs.next()) {
					leaveId = rs.getInt("compensate_with");
				}
				rs.close();
				pst.close();
				
				if(leaveId > 0) {
					pst = con.prepareStatement("delete from leave_register1 where emp_id=? and leave_type_id=? and _date between ? and ? and compensate_id=?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, leaveId);
					pst.setDate(3, uF.getDateFormat(std1, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(std2, DATE_FORMAT));
					pst.setInt(5, comLeaveid);
//					pst.setInt(3, comLeaveid);
//					System.out.println("pst ===>> " + pst);
					pst.execute();
					pst.close();
				}
				/*double balance = 0;
				double accured = 0;
				pst = con.prepareStatement("select * from leave_register1 where emp_id=? and leave_type_id=? order by register_id desc limit 1");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, leaveId);
				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					balance = rs.getDouble("balance");
					accured = rs.getDouble("accrued");
				}
				rs.close();
				pst.close();
	
				if (balance > 0 && leaveId > 0) {
					balance = balance - lBalanceCount;
					accured = accured - lBalanceCount;
	
					pst = con.prepareStatement("update leave_register1 set balance=?,accrued=? where emp_id=? and leave_type_id=?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, comLeaveid);
					pst.setDouble(3, balance);
					pst.setDouble(4, accured);
					System.out.println("pst ===>> " + pst);
					pst.execute();
					pst.close();
				}*/
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//===end====

	
	public void insertintoAttendance() {
//		System.out.println("APA1/2411--in attendance");
		Connection con = null;
		PreparedStatement pst = null, pst1=null, pst2=null, pst3=null, pst4=null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			String fromDate = null;
			String toDate = null;
			String[] strPayCycleDates = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					strPayCycleDates = getStrPaycycle().split("-");
				} else {
					strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
				}
				fromDate = strPayCycleDates[0];
				toDate = strPayCycleDates[1];
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					strPayCycleDates = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				fromDate = strPayCycleDates[0];
				toDate = strPayCycleDates[1];
				setStrPaycycle("");
			}
			
//			String[] strPayCycleDates = null;
			
			StringBuilder sbQuery = new StringBuilder();

			sbQuery.append("select task_date,activity_id from task_activity where emp_id =? and to_date(task_date::text, 'YYYY-MM-DD') " +
					"between ? and ? and is_approved=2 group by task_date,activity_id order by task_date ");

			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(fromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(toDate, DATE_FORMAT));
			rst = pst.executeQuery();
			Map<String, String> hmTaskDateActivityID = new LinkedHashMap<String, String>();
			while (rst.next()) {
				String task_date = uF.getDateFormat(rst.getString("task_date"),DBDATE, DATE_FORMAT);
				hmTaskDateActivityID.put(task_date,rst.getString("activity_id"));
			}
			rst.close();
			pst.close();
			
			StringBuilder sumQuery = new StringBuilder();

			sumQuery.append("select sum(actual_hrs)as actual_hrs,task_date from task_activity where emp_id=? "
					+ "and to_date(task_date::text, 'YYYY-MM-DD') between ? and ? and is_approved=2 group by task_date order by task_date");

			pst = con.prepareStatement(sumQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(fromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(toDate, DATE_FORMAT));
			rst = pst.executeQuery();
			Map<String, String> hmTaskDateSum = new LinkedHashMap<String, String>();
			while (rst.next()) {
				String task_date = uF.getDateFormat(rst.getString("task_date"),DBDATE, DATE_FORMAT);
				hmTaskDateSum.put(task_date, rst.getString("actual_hrs"));
			}
			rst.close();
			pst.close();

			pst = con.prepareStatement("select * from roster_details where emp_id =? and  to_date(_date::text, 'YYYY-MM-DD') between ? and ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(fromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(toDate, DATE_FORMAT));
			rst = pst.executeQuery();
//			System.out.println("pst===>"+pst);
			Map<String, String> hmRoster = new HashMap<String, String>();
			while (rst.next()) {
				String roster_date = uF.getDateFormat(rst.getString("_date"),DBDATE, DATE_FORMAT);
				hmRoster.put(roster_date, rst.getString("_from")+ "::::" + rst.getString("_to"));
			}
			rst.close();
			pst.close();
			
//			System.out.println("hmRoster===>"+hmRoster);
			Map<String, String> hmEmpLocation = CF.getEmpWlocationMap(con);
			String userlocation = hmEmpLocation.get(strSessionEmpId);

			pst = con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
			pst.setInt(1, uF.parseToInt(userlocation));
			rst = pst.executeQuery();
			String locationstarttime = null;
			String locationendtime = null;
			while (rst.next()) {
				locationstarttime = rst.getString("wlocation_start_time");
				locationendtime = rst.getString("wlocation_end_time");
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select service_id from roster_details where emp_id =? order by _date desc limit 1");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rst = pst.executeQuery();
			String service_id = null;
			while (rst.next()) {
				service_id = rst.getString("service_id");
			}
			rst.close();
			pst.close();
			
			if(uF.parseToInt(service_id)==0) {
				pst = con.prepareStatement("select service_id from employee_official_details where emp_id=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				rst = pst.executeQuery();
				String serviceids = null;
				while (rst.next()) {
					serviceids = rst.getString("service_id");
				}
				rst.close();
				pst.close();
				
				if(serviceids!=null && !serviceids.equals("")) {
					String[] temp=serviceids.split(",");
					service_id=temp[1];
				}
			}
			
			
			int nDateDiff = uF.parseToInt(uF.dateDifference(fromDate, DATE_FORMAT, toDate ,DATE_FORMAT,CF.getStrTimeZone()));
			StringBuilder sbRosterQuery=new StringBuilder();
			sbRosterQuery.append("insert into roster_details (emp_id, _date, _from, _to, isapproved, user_id, service_id, actual_hours, " +
					"attended,is_lunch_ded,shift_id,entry_date,roster_weeklyoff_id) " +
					"values(?,?,?,?, ?,(select user_id from user_details where emp_id=?),?,?, ?,?,?,?, ?)");
			pst = con.prepareStatement(sbRosterQuery.toString()); 
//			System.out.println("nDateDiff===>"+nDateDiff);
			boolean flag = false;
			for(int i=0;i<nDateDiff;i++) {
				String _date = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(fromDate, DATE_FORMAT),i),DBDATE, DATE_FORMAT);
//				System.out.println("_date===>"+_date);
				if(hmRoster!=null && !hmRoster.containsKey(_date)) {
					Time t = uF.getTimeFormat(locationstarttime, DBTIME);
					long long_startTime = t.getTime();

					Time t1 = uF.getTimeFormat(locationendtime, DBTIME);
					long long_endTime = t1.getTime();

					double total_time = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime,long_endTime));
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2,  uF.getDateFormat(_date,DATE_FORMAT));
					pst.setTime(3, t);
					pst.setTime(4, t1);
					pst.setBoolean(5, true);
					pst.setInt(6, uF.parseToInt(strEmpId));
					pst.setInt(7, uF.parseToInt(service_id));
					pst.setDouble(8, total_time);
					pst.setInt(9, 0);
					pst.setBoolean(10, false);
					pst.setInt(11, 1);
					pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));	
					pst.setInt(13, 1);
					pst.addBatch();
					flag = true;
				}
			}
			
			if(flag) {
				int x[] = pst.executeBatch();
				pst.close();
			} else {
				if(pst != null) {
					pst.close();
				}
			}
			
			pst = con.prepareStatement("select * from roster_details where emp_id =? and  to_date(_date::text, 'YYYY-MM-DD') between ? and ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(fromDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(toDate, DATE_FORMAT));
			rst = pst.executeQuery();
			Map<String, String> hmRosterDetails = new HashMap<String, String>();
			while (rst.next()) {
				String roster_date = uF.getDateFormat(rst.getString("_date"), DBDATE, DATE_FORMAT);
				hmRosterDetails.put(roster_date, rst.getString("_from") + "::::" + rst.getString("_to"));
			}
			rst.close();
			pst.close();

//			System.out.println("hmTaskDateActivityID ===>> " + hmTaskDateActivityID);
			if (!hmTaskDateActivityID.isEmpty()) {
				
				pst1 = con.prepareStatement("update attendance_details set emp_id=?, in_out_timestamp=?, in_out_timestamp_actual=?,"
						+ " in_out=?, service_id=? where atten_id=?");
				pst2 = con.prepareStatement("update attendance_details set emp_id=?, in_out_timestamp=?, in_out_timestamp_actual=?,"
						+ " in_out=?, service_id=?,hours_worked=? where atten_id=?");
				pst3 = con.prepareStatement("insert into attendance_details(emp_id, in_out_timestamp, in_out_timestamp_actual,"
						+ " in_out, service_id,hours_worked) VALUES (?,?,?,?,?,?)");
				pst4 = con.prepareStatement("insert into attendance_details(emp_id, in_out_timestamp, in_out_timestamp_actual,"
						+ " in_out, service_id) VALUES (?,?,?,?,?)");
				
				boolean flag1 = false;
				boolean flag2 = false;
				boolean flag3 = false;
				boolean flag4 = false;
				
				Iterator<String> it = hmTaskDateActivityID.keySet().iterator();
				while (it.hasNext()) {
					String task_date = it.next();
					String tasktime = hmTaskDateSum.get(task_date);
					String activity_id = hmTaskDateActivityID.get(task_date);
//					System.out.println("task_date ===>> " + task_date);
//					System.out.println("tasktime ===>> " + tasktime+ " -- activity_id ===>> " + activity_id);
					
					double total_time = 0;

					pst = con.prepareStatement("select * from attendance_details where emp_id =? and in_out_timestamp_actual::text " +
							" LIKE '"+ uF.getDateFormat(task_date.trim(),DATE_FORMAT, DBDATE) + "%' and in_out in ('IN','OUT')");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					System.out.println("pst ============>> " +pst);
					rst = pst.executeQuery();
					boolean flag_IN = false;
					boolean flag_OUT = false;
					String atten_id_IN = null;
					String atten_id_OUT = null;
					while (rst.next()) {
						if (rst.getString("in_out") != null && rst.getString("in_out").equals("IN")) {
							atten_id_IN = rst.getString("atten_id");
							flag_IN = true;
						}
						if (rst.getString("in_out") != null && rst.getString("in_out").equals("OUT")) {
							atten_id_OUT = rst.getString("atten_id");
							flag_OUT = true;
						}
					}
					rst.close();
					pst.close();
//					System.out.println("flag_IN ===>> " + flag_IN);
//					System.out.println("flag_OUT ===>> " + flag_OUT);
					
					if (hmRosterDetails.get(task_date) != null) {

						String[] splittime = hmRosterDetails.get(task_date).trim().split("::::");
						String startTime = splittime[0];
						String endTime = splittime[1];

						Time timeStart = uF.getTimeFormat(startTime, DBTIME);
						long long_startTime = timeStart.getTime();

						Time timeEnd = uF.getTimeFormat(endTime, DBTIME);
						long long_endTime = timeEnd.getTime();

						// total_time=uF.getTimeDifference(long_startTime,
						// long_endTime);
						total_time = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime, long_endTime));

						if (flag_IN == true) {

							pst1.setInt(1, uF.parseToInt(getStrEmpId()));
							pst1.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(task_date, DATE_FORMAT) + "" + uF.getTimeFormat(startTime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst1.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(task_date, DATE_FORMAT) + "" + uF.getTimeFormat(startTime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst1.setString(4, "IN");
							pst1.setInt(5, uF.parseToInt(service_id));
							pst1.setInt(6, uF.parseToInt(atten_id_IN));
							pst1.addBatch();
							
							flag1 = true;
							java.util.Date addedtime = getAddedTime(task_date, startTime, hmTaskDateSum.get(task_date));

							if (flag_OUT) {
								pst2.setInt(1, uF.parseToInt(getStrEmpId()));
								pst2.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
								pst2.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
								pst2.setString(4, "OUT");
								pst2.setInt(5, uF.parseToInt(service_id));
								pst2.setDouble(6, uF.parseToDouble(tasktime));
								pst2.setInt(7, uF.parseToInt(atten_id_OUT));
								pst2.addBatch();
								
								flag2 = true;
							} else {
								pst3.setInt(1, uF.parseToInt(getStrEmpId()));
								pst3.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
								pst3.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
								pst3.setString(4, "OUT");
								pst3.setInt(5, uF.parseToInt(service_id));
								pst3.setDouble(6, uF.parseToDouble(tasktime));
								pst3.addBatch();
								
								flag3 = true;
							}

						} else {
							pst4.setInt(1, uF.parseToInt(getStrEmpId()));
							pst4.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(task_date, DATE_FORMAT) + "" + uF.getTimeFormat(startTime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst4.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(task_date, DATE_FORMAT) + "" + uF.getTimeFormat(startTime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst4.setString(4, "IN");
							pst4.setInt(5, uF.parseToInt(service_id));
//							System.out.println("pst4 ===>> " + pst4);
							pst4.addBatch();
							
							flag4 = true;
							java.util.Date addedtime = getAddedTime(task_date, startTime, hmTaskDateSum.get(task_date));

							pst3.setInt(1, uF.parseToInt(getStrEmpId()));
							pst3.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
							pst3.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
							pst3.setString(4, "OUT");
							pst3.setInt(5, uF.parseToInt(service_id));
							pst3.setDouble(6, uF.parseToDouble(tasktime));
//							System.out.println("pst3 ===>> " + pst3);
							pst3.addBatch();
							
							flag3 = true;
						}

					} else {

						Time t = uF.getTimeFormat(locationstarttime, DBTIME);
						long long_startTime = t.getTime();

						Time t1 = uF.getTimeFormat(locationendtime, DBTIME);
						long long_endTime = t1.getTime();

						total_time = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime,long_endTime));
						

						if (flag_IN == true) {
							
							pst1.setInt(1, uF.parseToInt(getStrEmpId()));
							pst1.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(task_date, DATE_FORMAT) + "" + uF.getTimeFormat(locationstarttime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst1.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(task_date, DATE_FORMAT) + "" + uF.getTimeFormat(locationstarttime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst1.setString(4, "IN");
							pst1.setInt(5, uF.parseToInt(service_id));
							pst1.setInt(6, uF.parseToInt(atten_id_IN));
							pst1.addBatch();
							
							flag1 = true;
							
							java.util.Date addedtime = getAddedTime(task_date, locationstarttime, hmTaskDateSum.get(task_date));

							if (flag_OUT) {
								pst2.setInt(1, uF.parseToInt(getStrEmpId()));
								pst2.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
								pst2.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
								pst2.setString(4, "OUT");
								pst2.setInt(5, uF.parseToInt(service_id));
								pst2.setDouble(6, uF.parseToDouble(tasktime));
								pst2.setInt(7, uF.parseToInt(atten_id_OUT));
								pst2.addBatch();

								flag2 = true; 
							} else {
								pst3.setInt(1, uF.parseToInt(getStrEmpId()));
								pst3.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
								pst3.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
								pst3.setString(4, "OUT");
								pst3.setInt(5, uF.parseToInt(service_id));
								pst3.setDouble(6, uF.parseToDouble(tasktime));
								pst3.addBatch();

								flag3 = true;
							}

						} else {
							pst4.setInt(1, uF.parseToInt(getStrEmpId()));
							pst4.setTimestamp(2, uF.getTimeStamp(uF.getDateFormat(task_date, DATE_FORMAT) + "" + uF.getTimeFormat(locationstarttime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst4.setTimestamp(3, uF.getTimeStamp(uF.getDateFormat(task_date, DATE_FORMAT) + "" + uF.getTimeFormat(locationstarttime, "HH:mm:ss"), "yyyy-MM-ddHH:mm:ss"));
							pst4.setString(4, "IN");
							pst4.setInt(5, uF.parseToInt(service_id));
//							System.out.println("pst4 else ===>> " + pst4);
							pst4.addBatch();

							flag4 = true;
							java.util.Date addedtime = getAddedTime(task_date, locationstarttime, hmTaskDateSum.get(task_date));

							pst3.setInt(1, uF.parseToInt(getStrEmpId()));
							pst3.setTimestamp(2, new java.sql.Timestamp(addedtime.getTime()));
							pst3.setTimestamp(3, new java.sql.Timestamp(addedtime.getTime()));
							pst3.setString(4, "OUT");
							pst3.setInt(5, uF.parseToInt(service_id));
							pst3.setDouble(6, uF.parseToDouble(tasktime));
//							System.out.println("pst3 else ===>> " + pst3);
							pst3.addBatch();

							flag3 = true;
						}
					}

					String convertTime = getConvertedTime(tasktime);

					double timeAvg = (uF.parseToDouble(tasktime) / total_time) * 100;

					double attendance = 0;

					if (timeAvg > 0 && timeAvg < 70) {
						attendance = 0.5;
					} else if (timeAvg > 70) {
						attendance = 1;
					}

					pst = con.prepareStatement("update attendance_payroll set _hour=?, attendance=?, attendance_type=? " +
							"where emp_id=? and _date=? ");
					pst.setTime(1, uF.getTimeFormat(convertTime, DBTIME));
					pst.setDouble(2, attendance);
					pst.setString(3, "");
					pst.setInt(4, uF.parseToInt(getStrEmpId()));
					pst.setDate(5, uF.getDateFormat(task_date, DATE_FORMAT));

					int x = pst.executeUpdate();
					pst.close();
					
					if (x == 0) {
						pst = con.prepareStatement("insert into attendance_payroll(emp_id, _date, _hour, attendance, attendance_type) " +
								"VALUES (?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(getStrEmpId()));
						pst.setDate(2, uF.getDateFormat(task_date, DATE_FORMAT));
						pst.setTime(3, uF.getTimeFormat(convertTime, DBTIME));
						pst.setDouble(4, attendance);
						pst.setString(5, "");
						pst.execute();
						pst.close();
					}
				}
				
//				System.out.println("flag1 ===>> " + flag1);
//				System.out.println("flag2 ===>> " + flag2);
//				System.out.println("flag4 ===>> " + flag4);
//				System.out.println("flag3 ===>> " + flag3);

				if(flag1){
					int[] x = pst1.executeBatch();
				} else {
					if(pst1 != null){
						pst1.close();
					}
				}
				if(flag2){
					int[] x = pst2.executeBatch();
				} else {
					if(pst2 != null){
						pst2.close();
					}
				}
				
				if(flag4){
					int[] x = pst4.executeBatch();
				} else {
					if(pst4 != null){
						pst4.close();
					}
				}
				
				if(flag3){
					int[] x = pst3.executeBatch();
				} else {
					if(pst3 != null){
						pst3.close();
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeStatements(pst2);
			db.closeStatements(pst3);
			db.closeStatements(pst4);
			db.closeConnection(con);
		}
	}

//	private void insertLeaveApprovalMember(Connection con, PreparedStatement pst, ResultSet rs, String task_id, UtilityFunctions uF) {
//		try {
//			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
//			
//			pst = con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where policy_count=? and policy_type='1' " +
//				"and wfp.work_flow_member_id = a.work_flow_member_id order by member_position");
//			pst.setInt(1, uF.parseToInt(getPolicy_id()));
////			System.out.println("pst ==>> " + pst);
//			rs = pst.executeQuery();
//			Map<String, List<String>> hmMemberMap = new LinkedHashMap<String, List<String>>();
//			while (rs.next()) {
//				List<String> innerList = new ArrayList<String>();
//				innerList.add(rs.getString("member_type"));
//				innerList.add(rs.getString("member_id"));
//				innerList.add(rs.getString("member_position"));
//				innerList.add(rs.getString("work_flow_mem"));
//				innerList.add(rs.getString("work_flow_member_id"));
//
//				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmMemberMap ===>> " + hmMemberMap);
//			
//			
//			pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
//			pst.setInt(1, uF.parseToInt(task_id));
//			pst.setString(2, WORK_FLOW_TIMESHEET);
//			pst.execute();
//			pst.close();
//			
//			Iterator<String> it = hmMemberMap.keySet().iterator();
////			System.out.println("hmMemberMap ==>> " + hmMemberMap);
//			while (it.hasNext()) {
//				String work_flow_member_id = it.next();
////			System.out.println("work_flow_member_id ==>> " + work_flow_member_id);
//				List<String> innerList = hmMemberMap.get(work_flow_member_id);
//				int memid = uF.parseToInt(innerList.get(1));
//
//				if (uF.parseToInt(innerList.get(0)) == 3) {
//
//					String[] empid = request.getParameterValues(innerList.get(3) + memid);
//
////					pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
////					pst.setInt(1, uF.parseToInt(task_id));
////					pst.setString(2, WORK_FLOW_TIMESHEET);
////					pst.execute();
////					pst.close();
//					
//					for (int i = 0; empid != null && i < empid.length; i++) {
//						if (empid[i] != null && !empid[i].equals("")) {
//							int userTypeId =  uF.parseToInt(hmEmpUserTypeId.get(empid[i]));
//							pst = con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
//								"work_flow_mem_id,is_approved,status,user_type_id) values (?,?,?,?, ?,?,?,?, ?)");
//							pst.setInt(1, uF.parseToInt(empid[i]));
//							pst.setInt(2, uF.parseToInt(task_id));
//							pst.setString(3, WORK_FLOW_TIMESHEET);
//							pst.setInt(4, uF.parseToInt(innerList.get(0)));
////							pst.setInt(5, i + 1);
//							pst.setInt(5, 1);
//							pst.setInt(6, uF.parseToInt(innerList.get(4)));
//							pst.setInt(7, 0);
//							pst.setInt(8, 0);
//							pst.setInt(9,userTypeId);
//							int x = pst.executeUpdate();
//							pst.close();
//						}
//					}
//				} else {
//
////					System.out.println("innerList.get(3) + memid ==>> " + innerList.get(3) + memid);
//					String empid = request.getParameter(innerList.get(3) + memid);
////					System.out.println("empid  ==>> " + empid);
//					
//					if (empid != null && !empid.equals("")) {
//						
//						int userTypeId = memid;
//						
////						pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
////						pst.setInt(1, uF.parseToInt(task_id));
////						pst.setString(2, WORK_FLOW_TIMESHEET);
////						pst.execute();
////						pst.close();
//
//						pst = con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position,"
//							+ "work_flow_mem_id,is_approved,status) values(?,?,?,?, ?,?,?,?, ?)");
//						pst.setInt(1, uF.parseToInt(empid));
//						pst.setInt(2, uF.parseToInt(task_id));
//						pst.setString(3, WORK_FLOW_TIMESHEET);
//						pst.setInt(4, uF.parseToInt(innerList.get(0)));
//						pst.setInt(5, uF.parseToInt(innerList.get(2)));
//						pst.setInt(6, uF.parseToInt(innerList.get(4)));
//						pst.setInt(7, 0);
//						pst.setInt(8, 0);
//						pst.setInt(9, userTypeId);
//						int x = pst.executeUpdate();
//						System.out.println("pst ==>> " + pst);
//						pst.close();
//					}
//				}
//			}
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

	private void getTimesheetPolicyMember() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

//		System.out.println("getLeavePolicyMember ====>> ");
		
		String policy_id = null;
		try {

			int strEmpID = 0;

			strEmpID = uF.parseToInt(strSessionEmpId);
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			String empLevelId = hmEmpLevelMap.get("" + strEmpID);
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			String locationID = hmEmpWlocationMap.get("" + strEmpID);

			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);

			pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_TIMESHEET+"' and level_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(locationID));
//			System.out.println("pst ======>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				policy_id = rs.getString("policy_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(policy_id) == 0){
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(locationID));
				rs = pst.executeQuery();
				while(rs.next()){
					policy_id=rs.getString("policy_count");
				}
				rs.close();
				pst.close();
			}

//			System.out.println("policy_id ======>> " + policy_id);
			
			if (uF.parseToInt(policy_id) > 0) {

				pst = con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where policy_count=? and policy_type='1' " +
						" and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1, uF.parseToInt(policy_id));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				Map<String, List<String>> hmMemberMap = new LinkedHashMap<String, List<String>>();
				while (rs.next()) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("member_type"));
					innerList.add(rs.getString("member_id"));
					innerList.add(rs.getString("member_position"));
					innerList.add(rs.getString("work_flow_mem"));
					innerList.add(rs.getString("work_flow_member_id"));

					hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
				}
				rs.close();
				pst.close();
//				System.out.println("hmMemberMap ===>> " + hmMemberMap);
				
				Map<String, String> hmUserType = new HashMap<String, String>();
				pst = con.prepareStatement("SELECT * FROM user_type");
				rs = pst.executeQuery();
				while (rs.next()) {
					hmUserType.put(rs.getString("user_type"), rs.getString("user_type_id"));
				}
				rs.close();
				pst.close();

				Map<String, String> hmMemberOption = new LinkedHashMap<String, String>();

				Iterator<String> it = hmMemberMap.keySet().iterator();
				while (it.hasNext()) {
					String work_flow_member_id = it.next();
					List<String> innerList = hmMemberMap.get(work_flow_member_id);
//					System.out.println("innerList ===>> " + innerList);
					if (uF.parseToInt(innerList.get(0)) == 1) {
						int memid = uF.parseToInt(innerList.get(1));

						switch (memid) {

						case 1:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname, epd.emp_lname from user_details ud,"
											+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
											+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'"
											+ "and ud.usertype_id!=? and ud.emp_id not in(?) order by epd.emp_fname"); //and eod.wlocation_id=?
//							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(1, uF.parseToInt(hmUserType.get(EMPLOYEE)));
							pst.setInt(2, strEmpID);
							rs = pst.executeQuery();
							List<List<String>> outerList = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname"));
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));

								outerList.add(alList);
							}
							rs.close();
							pst.close();
							
							if (outerList != null && !outerList.isEmpty()) {
								StringBuilder sbComboBox = new StringBuilder();
								sbComboBox.append("<select name=\"" + innerList.get(3) + memid + "\" id=\"" + innerList.get(3) + memid + "\" class=\"validateRequired\">");
								sbComboBox.append("<option value=\"\">Select " + innerList.get(3) + "</option>");
								for (int i = 0; i < outerList.size(); i++) {
									List<String> alList = outerList.get(i);
									sbComboBox.append("<option value=\"" + alList.get(0) + "\""+((i == 0) ? " selected" : "")+">" + alList.get(2) +alList.get(3)+ " " + alList.get(4) + "</option>");
								}
								sbComboBox.append("</select>");

								String optionTr = "<tr><td class=\"alignRight textcolorWhite\" style=\"font-size: 12px;\">" + innerList.get(3) + ":<sup>*</sup></td><td>" + sbComboBox.toString() + "</td></tr>";

								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;

						case 2:
							pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id=" + strEmpID
								+ " and supervisor_emp_id!=0) as a, employee_personal_details epd,user_details ud where " +
								" a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE' order by epd.emp_fname");
							// pst.setInt(1, uF.parseToInt(getStrEmp()));
							rs = pst.executeQuery();
//							System.out.println("mgr pst ===>> " + pst);
							List<List<String>> outerList11 = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname"));
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));

								outerList11.add(alList);
							}
							rs.close();
							pst.close();
//							System.out.println("outerList11 ===>> " + outerList11);
							
							if (outerList11 != null && !outerList11.isEmpty()) {
								StringBuilder sbComboBox11 = new StringBuilder();
								sbComboBox11.append("<select name=\"" + innerList.get(3) + memid + "\" id=\"" + innerList.get(3) + memid + "\" class=\"validateRequired\">");
								sbComboBox11.append("<option value=\"\">Select " + innerList.get(3) + "</option>");
								for (int i = 0; i < outerList11.size(); i++) {
									List<String> alList = outerList11.get(i);
									sbComboBox11.append("<option value=\"" + alList.get(0) + "\""+((i == 0) ? " selected" : "")+">" + alList.get(2) +alList.get(3)+ " " + alList.get(4) + "</option>");
								}
								sbComboBox11.append("</select>");

								String optionTr11 = "<tr><td class=\"alignRight textcolorWhite\" style=\"font-size: 12px;\">" + innerList.get(3) + ":<sup>*</sup></td><td>" + sbComboBox11.toString() + "</td></tr>";

								hmMemberOption.put(innerList.get(4), optionTr11);
//								System.out.println("hmMemberOption ===>> " + hmMemberOption);
							}

							break;

						case 3:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
											+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
											+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'"
											+ "and ud.usertype_id!=? and ud.emp_id not in(?) order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(2, uF.parseToInt(hmUserType.get(EMPLOYEE)));
							pst.setInt(3, strEmpID);
							rs = pst.executeQuery();
							List<List<String>> outerList1 = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname"));
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));

								outerList1.add(alList);
							}
							rs.close();
							pst.close();

							if (outerList1 != null && !outerList1.isEmpty()) {
								StringBuilder sbComboBox1 = new StringBuilder();
								sbComboBox1.append("<select name=\"" + innerList.get(3) + memid + "\" id=\"" + innerList.get(3) + memid + "\" class=\"validateRequired\">");
								sbComboBox1.append("<option value=\"\">Select " + innerList.get(3) + "</option>");
								for (int i = 0; i < outerList1.size(); i++) {
									List<String> alList = outerList1.get(i);
									sbComboBox1.append("<option value=\"" + alList.get(0) + "\""+((i == 0) ? " selected" : "")+">" + alList.get(2) +alList.get(3)+ " " + alList.get(4) + "</option>");
								}
								sbComboBox1.append("</select>");

								String optionTr1 = "<tr><td class=\"alignRight textcolorWhite\" style=\"font-size: 12px;\">" + innerList.get(3) + ":<sup>*</sup></td><td>" + sbComboBox1.toString() + "</td></tr>";

								hmMemberOption.put(innerList.get(4), optionTr1);
							}
							break;

						case 4:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
											+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
											+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'"
											+ "and ud.usertype_id!=? and ud.emp_id not in(?) order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(2, uF.parseToInt(hmUserType.get(EMPLOYEE)));
							pst.setInt(3, strEmpID);
							rs = pst.executeQuery();
							List<List<String>> outerList2 = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname"));
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));

								outerList2.add(alList);
							}
							rs.close();
							pst.close();
							
							if (outerList2 != null && !outerList2.isEmpty()) {
								StringBuilder sbComboBox2 = new StringBuilder();
								sbComboBox2.append("<select name=\"" + innerList.get(3) + memid + "\" id=\"" + innerList.get(3) + memid + "\" class=\"validateRequired\">");
								sbComboBox2.append("<option value=\"\">Select " + innerList.get(3) + "</option>");
								for (int i = 0; i < outerList2.size(); i++) {
									List<String> alList = outerList2.get(i);
									sbComboBox2.append("<option value=\"" + alList.get(0) + "\""+((i == 0) ? " selected" : "")+">" + alList.get(2) +alList.get(3)+ " " + alList.get(4) + "</option>");
								}
								sbComboBox2.append("</select>");

								String optionTr2 = "<tr><td class=\"alignRight textcolorWhite\" style=\"font-size: 12px;\">" + innerList.get(3) + ":<sup>*</sup></td><td>" + sbComboBox2.toString() + "</td></tr>";

								hmMemberOption.put(innerList.get(4), optionTr2);
							}
							break;

						case 5:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
											+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
											+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'"
											+ "and ud.usertype_id!=? and ud.emp_id not in(?) order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(2, uF.parseToInt(hmUserType.get(EMPLOYEE)));
							pst.setInt(3, strEmpID);
							rs = pst.executeQuery();
							List<List<String>> outerList3 = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname"));
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));

								outerList3.add(alList);
							}
							rs.close();
							pst.close();

							if (outerList3 != null && !outerList3.isEmpty()) {
								StringBuilder sbComboBox3 = new StringBuilder();
								sbComboBox3.append("<select name=\"" + innerList.get(3) + memid + "\" id=\"" + innerList.get(3) + memid + "\" class=\"validateRequired\">");
								sbComboBox3.append("<option value=\"\">Select " + innerList.get(3) + "</option>");
								for (int i = 0; i < outerList3.size(); i++) {
									List<String> alList = outerList3.get(i);
									sbComboBox3.append("<option value=\"" + alList.get(0) + "\""+((i == 0) ? " selected" : "")+">" + alList.get(2) +alList.get(3)+ " " + alList.get(4) + "</option>");
								}
								sbComboBox3.append("</select>");

								String optionTr3 = "<tr><td class=\"alignRight textcolorWhite\" style=\"font-size: 12px;\">" + innerList.get(3) + ":<sup>*</sup></td><td>" + sbComboBox3.toString() + "</td></tr>";

								hmMemberOption.put(innerList.get(4), optionTr3);
							}
							break;

						case 6:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
											+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
											+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'"
											+ "and ud.usertype_id!=? and ud.emp_id not in(?) order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(2, uF.parseToInt(hmUserType.get(EMPLOYEE)));
							pst.setInt(3, strEmpID);
							rs = pst.executeQuery();
							List<List<String>> outerList4 = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname"));
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));

								outerList4.add(alList);
							}
							rs.close();
							pst.close();

							if (outerList4 != null && !outerList4.isEmpty()) {
								StringBuilder sbComboBox4 = new StringBuilder();
								sbComboBox4.append("<select name=\"" + innerList.get(3) + memid + "\" id=\"" + innerList.get(3) + memid + "\" class=\"validateRequired\">");
								sbComboBox4.append("<option value=\"\">Select " + innerList.get(3) + "</option>");
								for (int i = 0; i < outerList4.size(); i++) {
									List<String> alList = outerList4.get(i);
									sbComboBox4.append("<option value=\"" + alList.get(0) + "\""+((i == 0) ? " selected" : "")+">" + alList.get(2) +alList.get(3)+ " " + alList.get(4) + "</option>");
								}
								sbComboBox4.append("</select>");

								String optionTr4 = "<tr><td class=\"alignRight textcolorWhite\" style=\"font-size: 12px;\">" + innerList.get(3) + ":<sup>*</sup></td><td>" + sbComboBox4.toString() + "</td></tr>";

								hmMemberOption.put(innerList.get(4), optionTr4);
							}
							break;

						case 7:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
											+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=7 "
											+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'"
											+ "and ud.usertype_id!=? and ud.emp_id not in(?) order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(2, uF.parseToInt(hmUserType.get(EMPLOYEE)));
							pst.setInt(3, strEmpID);
//							System.out.println("HR pst ===>> " + pst);
							rs = pst.executeQuery();
							List<List<String>> outerList5 = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList = new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname"));
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));

								outerList5.add(alList);
							}
							rs.close();
							pst.close();
//							System.out.println("outerList5 ===>> " + outerList5);
							if (outerList5 != null && !outerList5.isEmpty()) {
								StringBuilder sbComboBox5 = new StringBuilder();
								sbComboBox5.append("<select name=\"" + innerList.get(3) + memid + "\" id=\"" + innerList.get(3) + memid + "\" class=\"validateRequired\">");
								sbComboBox5.append("<option value=\"\">Select " + innerList.get(3) + "</option>");
								for (int i = 0; i < outerList5.size(); i++) {
									List<String> alList = outerList5.get(i);
									sbComboBox5.append("<option value=\"" + alList.get(0) + "\""+((i == 0) ? " selected" : "")+">" + alList.get(2) + alList.get(3)+" " + alList.get(4) + "</option>");
								}
								sbComboBox5.append("</select>");

								String optionTr5 = "<tr><td class=\"alignRight textcolorWhite\" style=\"font-size: 12px;\">"+ innerList.get(3)+ ":<sup>*</sup></td><td>"+ sbComboBox5.toString() + "</td></tr>";

								hmMemberOption.put(innerList.get(4), optionTr5);
//								System.out.println("hmMemberOption ===>> " + hmMemberOption);
							}
							break;

						}

					} else if (uF.parseToInt(innerList.get(0)) == 3) {
						int memid = uF.parseToInt(innerList.get(1));

						List<List<String>> outerList = new ArrayList<List<String>>();
						pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id " +
								"and se.policy_id = ? and se.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname"); 
						pst.setInt(1,uF.parseToInt(policy_id));
						pst.setInt(2, strEmpID);
//						System.out.println("pst ===>> " + pst);
						
						rs = pst.executeQuery();
						while (rs.next()) {
							List<String> alList = new ArrayList<String>();
							alList.add(rs.getString("emp_id"));
							outerList.add(alList);
						}
						rs.close();
						pst.close();

						if (outerList != null && !outerList.isEmpty()) {
							StringBuilder sbComboBox = new StringBuilder();
							sbComboBox.append("<select name=\"" + innerList.get(3) + memid + "\" id=\"" + innerList.get(3) + memid + "\" class=\"validateRequired text-input\" multiple=\"true\" size=\"4\" style=\"height: 85px !important;\">");
							// sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for (int i = 0; i < outerList.size(); i++) {
								List<String> alList = outerList.get(i);
								sbComboBox.append("<option value=\"" + alList.get(0) + "\""+((i == 0) ? " selected" : "")+">" + hmEmpCodeName.get(alList.get(0).trim()) + "</option>");
							}
							sbComboBox.append("</select>");

							String optionTr = "<tr><td valign=\"top\" class=\"alignRight textcolorWhite\" style=\"font-size: 12px;\">Your work flow:<sup>*</sup></td><td>" + sbComboBox.toString() + "</td></tr>";

							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
				
				
//				System.out.println("getFilterBy() ===>> " + getFilterBy());
//				System.out.println("getStrPaycycle() ===>> " + getStrPaycycle());
				
				String[] arr = null;
				if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
					if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
						arr = getStrPaycycle().split("-");
					} else {
						arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
						setStrPaycycle(arr[0] + "-" + arr[1]+ "-" + arr[2]);
					}
					setStrYear("");
					setStrMonth("");
					setStrWeek("");
				} else if(getFilterBy() != null && getFilterBy().equals("O")) {
					if(getStrWeek()==null || getStrWeek().equals("")) {
						String strDate = "01/"+getStrMonth()+"/"+getStrYear();
						String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
						monthminMaxDates = monthminMaxDates+"::::00";
						arr = monthminMaxDates.split("::::");
					} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
						
					}
					setStrPaycycle("");
				}
				
				
				Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con,arr[0],arr[1], CF, uF, hmWeekEndHalfDates, null);
//				System.out.println("hmWeekEnds ===>> " +hmWeekEnds);
				
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();   
				CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,arr[0],arr[1],alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);

				String strWLocationId = hmEmpWlocation.get(getStrEmpId());

				Set<String> weeklyOffEndDate = hmWeekEnds.get(strWLocationId);
				if(weeklyOffEndDate==null) weeklyOffEndDate = new HashSet<String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(getStrEmpId());
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				
				Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
				Map<String, String> hmHolidayDates = new HashMap<String, String>();
				List<String> empLeaveCountList = new ArrayList<String>();
				List<String> holidayDateList = new ArrayList<String>();
				
				if(alEmpCheckRosterWeektype!=null && alEmpCheckRosterWeektype.contains(getStrEmpId())) {
					CF.getHolidayListCount(con,request, arr[0],arr[1], CF, hmHolidayDates, hmHolidaysCnt, rosterWeeklyOffSet, true);
					empLeaveCountList = CF.getEmpLeaveCount(con, arr[0], arr[1], CF, hmHolidayDates, rosterWeeklyOffSet, getStrEmpId(), strWLocationId);
					holidayDateList = CF.getHolidayDateList(con, arr[0], arr[1], CF, rosterWeeklyOffSet, strWLocationId);
				} else {
					CF.getHolidayListCount(con,request, arr[0],arr[1], CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, true);
					empLeaveCountList = CF.getEmpLeaveCount(con, arr[0], arr[1], CF, hmHolidayDates, weeklyOffEndDate, getStrEmpId(), strWLocationId);
					holidayDateList = CF.getHolidayDateList(con, arr[0], arr[1], CF, weeklyOffEndDate, strWLocationId);
				}
				
				
				String diffInDays = uF.dateDifference(arr[0], DATE_FORMAT, arr[1], DATE_FORMAT, CF.getStrTimeZone());
				
				
//				Set<String> weeklyOffEndDate = hmWeekEnds.get(strLevel);
//				if(weeklyOffEndDate==null) weeklyOffEndDate = new HashSet<String>();
				
//				Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
//				Map<String, String> hmHolidayDates = new HashMap<String, String>();
//				CF.getHolidayListCount(con, arr[0], arr[1], CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, false);
				List<String> empTimesheetFilledDates = CF.getEmpTimesheetFilledDates(con, uF, getStrEmpId());
//				
//				String diffInDays = uF.dateDifference(arr[0], DATE_FORMAT, arr[1], DATE_FORMAT, CF.getStrTimeZone());
				
//				System.out.println("diffInDays ===>>>> " + diffInDays);
//				System.out.println("hmWeekEnds ===>>>> " + hmWeekEnds);
//				System.out.println("weeklyOffEndDate ===>>>> " + weeklyOffEndDate);
//				System.out.println("hmHolidayDates ===>>>> " + hmHolidayDates);

//				Iterator<String> woedIT = weeklyOffEndDate.iterator();
//				List<String> weekOffDateList = new ArrayList<String>();
//				while (woedIT.hasNext()) {
//					weekOffDateList.add(woedIT.next());
//				}
//				System.out.println("weekOffDateList ===>> " + weekOffDateList);
				
				StringBuilder sbRemainDate = null;
				int remainDateCnt = 0;
				Set<String> stExtraWorkDates = new HashSet<String>();
				boolean flagIsRoster = false;
				if(alEmpCheckRosterWeektype.contains(getStrEmpId())) {
					flagIsRoster = true;
				}
				for(int i=0; i< uF.parseToInt(diffInDays); i++) {
//					System.out.println("future Date ===>>>> " + uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(arr[0], DATE_FORMAT), i), DBDATE, DATE_FORMAT));
					String todayDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(arr[0], DATE_FORMAT), i), DBDATE, DATE_FORMAT);
					boolean flag = false;
					boolean flagTF = false;
					boolean flagOff = false;
//					for(int j=0; empTimesheetFilledDates != null && !empTimesheetFilledDates.isEmpty() && j<empTimesheetFilledDates.size(); j++) {
//						if()
//					}
					if(empTimesheetFilledDates != null && !empTimesheetFilledDates.isEmpty() && empTimesheetFilledDates.contains(todayDate)) {
						flagTF = true;
//						System.out.println("flagTF todayDate ===>> " + todayDate);
					}
					if(!flagIsRoster && weeklyOffEndDate != null && !weeklyOffEndDate.isEmpty() && weeklyOffEndDate.contains(todayDate)) {
						flagOff = true;
					} 
					if(flagIsRoster && rosterWeeklyOffSet != null && rosterWeeklyOffSet.contains(todayDate)) {
						flagOff = true;
					} 
					if(holidayDateList != null && !holidayDateList.isEmpty() && holidayDateList.contains(todayDate)) {
						flagOff = true;
					}
					
					if(empTimesheetFilledDates != null && !empTimesheetFilledDates.isEmpty() && empTimesheetFilledDates.contains(todayDate)) {
						flag = true;
					} else if(!flagIsRoster && weeklyOffEndDate != null && !weeklyOffEndDate.isEmpty() && weeklyOffEndDate.contains(todayDate)) {
						flag = true;
					} else if(flagIsRoster && rosterWeeklyOffSet != null && rosterWeeklyOffSet.contains(todayDate)) {
						flag = true;
					} else if(holidayDateList != null && !holidayDateList.isEmpty() && holidayDateList.contains(todayDate)) {
						flag = true;
					} else if(empLeaveCountList != null && !empLeaveCountList.isEmpty() && empLeaveCountList.contains(todayDate)) {
						flag = true;
					} else {
						remainDateCnt++;
						if(sbRemainDate == null) {
							sbRemainDate = new StringBuilder();
							sbRemainDate.append(todayDate);
						} else {
							sbRemainDate.append(", "+todayDate);
						}
					}
					if(flagTF && flagOff) {
						stExtraWorkDates.add(todayDate);
					}
				}
				if(sbRemainDate == null) {
					sbRemainDate = new StringBuilder();
				} 
				
				
//				request.setAttribute("sbRemainDate", sbRemainDate.toString());
				request.setAttribute("stExtraWorkDates", stExtraWorkDates);
				request.setAttribute("remainDateCnt", ""+remainDateCnt);
//				System.out.println("stExtraWorkDates ===>> " + stExtraWorkDates);
				
				String divpopup = "";
				StringBuilder sbModalBody = new StringBuilder();
				String modalTitle = "";
				
//				System.out.println("getIsWorkFlow ===>> " + uF.parseToBoolean(CF.getIsWorkFlow()));
//				System.out.println("hmMemberOption ===>> " + hmMemberOption);
				
				if (uF.parseToBoolean(CF.getIsWorkFlow())) {
					modalTitle = hmEmpCodeName.get(""+strEmpID);
					
//					sb.append("<div id=\"popup_name" + strEmpID + "\" class=\"popup_block\"> <h2 class=\"textcolorWhite\">TimeSheet of " + hmEmpCodeName.get("" + strEmpID) + "</h2> <table>");
					sbModalBody.append(" <table class=\"table table-hover\"> ");
					if (hmMemberOption != null && !hmMemberOption.isEmpty()) {
						Iterator<String> it1 = hmMemberOption.keySet().iterator();
						while (it1.hasNext()) {
							String memPosition = it1.next();
							String optiontr = hmMemberOption.get(memPosition);
							sbModalBody.append(optiontr);
						}
						sbModalBody.append("<tr><td>&nbsp;</td><td><input type=\"submit\" name=\"submit\" value=\"Submit\" class=\"btn btn-primary\"/></td></tr>");
					} else {
						sbModalBody.append("<tr><td colspan=\"2\">Your work flow is not defined. Please, speak to your hr for your work flow.</td></tr>");
					}
					sbModalBody.append("</table>");

					divpopup = "<input type=\"button\" name=\"submit1\" value=\"Submit your timesheet\" class=\"btn btn-primary\"  "
						+ "onclick=\"return checkTimeSheet('"+remainDateCnt+"')\"/>";
					
//					divpopup = "<input type=\"button\" name=\"submit1\" value=\"Submit your whole timesheet\" class=\"input_button\"  "
//							+ "onclick=\"return confirm('Are you sure you want to submit your whole timesheet?\nYou will be unable to modify it once you submit.\nClick Ok to submit and Cancel to modify your timesheet.')\"/>";

				} else {
					sbModalBody.append("");
					divpopup = "<input type=\"submit\" name=\"submit1\" value=\"Submit your timesheet\" class=\"btn btn-primary\" "
						+ "onclick=\"return confirm('Are you sure you want to submit your whole timesheet?\nYou will be unable to modify it once you submit.\nClick Ok to submit and Cancel to modify your timesheet.')\"/>";
				}
				
//				System.out.println("divpopup =======>>>>>> " + divpopup);
				
				request.setAttribute("hmMemberOption", hmMemberOption);
				request.setAttribute("policy_id", policy_id);
				request.setAttribute("divpopup", divpopup);
				
				request.setAttribute("modalTitle", modalTitle);
				request.setAttribute("modalBody", sbModalBody.toString());
				
				request.setAttribute("strEmpID", strEmpID);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}

	private String getUserLocation() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String location = "";

		con = db.makeConnection(con);
		try {
			pst = con.prepareStatement("select e.wlocation_id from employee_official_details e where e.emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst = pst.executeQuery();
			while (rst.next()) {
				location = rst.getString(1);
			}
			rst.close();
			pst.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return location;
	}

	private void getProjectManagerList(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		
		try {

			/*String arr[] = null;
			if (getStrPaycycle() != null) {
				arr = getStrPaycycle().split("-");
			}*/

			String[] arr = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					arr = getStrPaycycle().split("-");
				} else {
					arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(arr[0] + "-" + arr[1]+ "-" + arr[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					arr = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			/*
			 * pst = con.prepareStatement(
			 * "select task_id,pmt.pro_id,added_by from activity_info ai,projectmntnc pmt "
			 * +
			 * " where ai.emp_id=? and ai.pro_id=pmt.pro_id and ai.task_id in "
			 * +
			 * " (select activity_id from task_activity where task_date between ? and ? and is_approved > 0 order by task_date)"
			 * );
			 */
			/*
			 * pst = con.prepareStatement(
			 * "select approved_by from task_activity where emp_id=? and task_date between ? and ? "
			 * + " and is_approved=2 group by approved_by "); pst.setInt(1,
			 * uF.parseToInt(getStrEmpId())); pst.setDate(2,
			 * uF.getDateFormat(arr[0], DATE_FORMAT)); pst.setDate(3,
			 * uF.getDateFormat(arr[1], DATE_FORMAT));
			 */
			/*
			 * pst = con.prepareStatement(
			 * "select w.emp_id,w.approve_date from task_activity t,work_flow_details w where t.emp_id=? "
			 * +
			 * " and t.timesheet_paycycle=?  and w.is_approved=1 group by w.emp_id,w.approve_date"
			 * );
			 */
			pst = con.prepareStatement("select wfd.emp_id,wfd.approve_date,ta.submited_date from task_activity ta,work_flow_details wfd "
				+ " where ta.task_id=wfd.effective_id and effective_type='"+WORK_FLOW_TIMESHEET+"' and ta.emp_id=? and task_date between ? and ? "
				+ " and ta.is_approved > 0 and ta.submited_date is not null group by wfd.emp_id,wfd.approve_date,ta.submited_date " +
				" order by wfd.approve_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				pst.setInt(2, uF.parseToInt(arr[2]));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
//				System.out.println("pst ===>> " + pst);
			rst = pst.executeQuery();
			List<String> alEmpId = new ArrayList<String>(); 
			List<List<String>> mOuterList = new ArrayList<List<String>>();
			while (rst.next()) {
//				if(!alEmpId.contains(rst.getString("emp_id")) || (alEmpId.contains(rst.getString("emp_id")) && rst.getString("approve_date") != null)) {
					List<String> mInnerList = new ArrayList<String>();
					mInnerList.add(hmEmpName.get(rst.getString("emp_id")));
					mInnerList.add(rst.getString("emp_id"));
					mInnerList.add(uF.getDateFormat(rst.getString("submited_date"), DBDATE, DATE_FORMAT));
					
					if(rst.getString("approve_date") != null && !rst.getString("approve_date").equals("")) {
						mInnerList.add(uF.getDateFormat(rst.getString("approve_date"), DBDATE, DATE_FORMAT));
					} else {
						mInnerList.add("-");
					}
					mOuterList.add(mInnerList);
					alEmpId.add(rst.getString("emp_id"));
//				}
			}
			rst.close();
			pst.close();
			
			/*
			 * con = db.makeConnection(con); pst = con.prepareStatement(
			 * "select epd.emp_per_id from employee_personal_details epd join employee_official_details eod "
			 * +
			 * " on eod.emp_id=epd.emp_per_id and eod.wlocation_id=? join user_details ud on epd.emp_per_id=ud.emp_id and ud.usertype_id=7"
			 * ); pst.setInt(1, uF.parseToInt(getUserlocation()));
			 * rst=pst.executeQuery(); while(rst.next()) { List<String>
			 * mInnerList=new ArrayList<String>();
			 * mInnerList.add(hmEmpName.get(rst
			 * .getString("emp_per_id").trim())+" (HR)");
			 * mInnerList.add(rst.getString("emp_per_id"));
			 * mOuterList.add(mInnerList); }
			 */
			request.setAttribute("mOuterList", mOuterList);

			pst = con.prepareStatement("select unblock_by from task_activity ta where emp_id=? and ta.task_date between ? and ? "
							+ "  and is_approved=0 and unblock_by is not null group by unblock_by");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(arr[2]));
			pst.setDate(2, uF.getDateFormat(arr[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(arr[1], DATE_FORMAT));
			rst = pst.executeQuery();
			List<String> unlockList = new ArrayList<String>();
			while (rst.next()) {
				unlockList.add(rst.getString("unblock_by"));
			}
			rst.close();
			pst.close();
			// System.out.println("unlockList====>"+unlockList);
			request.setAttribute("unlockList", unlockList); 

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getGlobalHRApproveDate(Connection con, String approverId, String submitedDate, String stDate, String endDate) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		String approvedDate = "-";
		UtilityFunctions uF = new UtilityFunctions();
		try {
			pst = con.prepareStatement("select distinct(approved_date) as approved_date from task_activity where emp_id=? and task_date between ? and ? and submited_date=?"); // and approved_by=?
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(paycycleId));
			pst.setDate(2, uF.getDateFormat(stDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(endDate, DATE_FORMAT));
//			pst.setInt(4, uF.parseToInt(approverId));
			pst.setDate(4, uF.getDateFormat(submitedDate, DBDATE));
			rs = pst.executeQuery();
			while (rs.next()) {
				approvedDate = uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT) ;
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return approvedDate;
	}
	

	public void removeTaskData(UtilityFunctions uF, String strTaksId) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			
			String taskId = null;
			pst = con.prepareStatement("select activity_id from task_activity where task_id = ?");
			pst.setInt(1, uF.parseToInt(strTaksId));
			rs = pst.executeQuery();
			while (rs.next()) {
				taskId = rs.getString("activity_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("delete from  work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id= ?");
			pst.setInt(1, uF.parseToInt(strTaksId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from task_activity where task_id = ?");
			pst.setInt(1, uF.parseToInt(strTaksId));
			pst.execute();
			pst.close();
			
			insertintoActivityandProject(uF, taskId, con);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void saveTaskData(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		try {
//			String[] strPayCycleDates = null;
			con = db.makeConnection(con);
//			String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			/*if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getStrPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strEmpOrgId);
				setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}*/

			String[] strPayCycleDates = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					strPayCycleDates = getStrPaycycle().split("-");
				} else {
					strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					strPayCycleDates = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			
//			Map hmHolidays = new HashMap();
//			Map hmHolidayDates = new HashMap();
//			CF.getHolidayList(con, strPayCycleDates[0], strPayCycleDates[1], CF,hmHolidayDates, hmHolidays, true);
//			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);

//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			String levelID = hmEmpLevelMap.get(getStrEmpId());
//
//			Map hmWeekendMap = CF.getWeekEndDateList(con, strPayCycleDates[0],strPayCycleDates[1], CF, uF,null,null);
//			String strWLocationId = hmEmpWLocation.get(getStrEmpId());

			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
//			for (int i = 0; getStatusTaskId() != null && i < getStatusTaskId().length; i++) {
//				pst = con.prepareStatement("update activity_info set completed=? where task_id = ?");
//				pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(getStatus()[i]))));
//				pst.setInt(2, uF.parseToInt(getStatusTaskId()[i]));
//				pst.executeUpdate();
//				pst.close();
//				
//				
//				if(uF.parseToDouble(getStatus()[i])>= 100) {
////					Need to Change
//					Map<String, String> hmTaskProData = CF.getTaskProInfo(con, getStatusTaskId()[i], null);
//					
//					Map<String, String> hmEmpData = hmEmpInfo.get(strSessionEmpId);
//					String strDomain = request.getServerName().split("\\.")[0];
//					Notifications nF = new Notifications(N_TASK_COMPLETED, CF); 
//					nF.setDomain(strDomain);
//					
//					nF.request = request;
//					nF.setStrOrgId(strEmpOrgId);
//					nF.setEmailTemplate(true);
//					
//					nF.setStrEmpId(strSessionEmpId);
//					nF.setStrResourceFName(hmEmpData.get("FNAME"));
//					nF.setStrResourceLName(hmEmpData.get("LNAME"));
//					nF.setStrTaskName(hmTaskProData.get("TASK_NAME"));
//					nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
//					nF.setStrTeamLeader(hmTaskProData.get("TEAM_LEADER"));
//					nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
//					
//					nF.setStrHostAddress(CF.getStrEmailLocalHost());
//					nF.setStrHostPort(CF.getStrHostPort());
//					nF.setStrContextPath(request.getContextPath());
//					nF.sendNotifications();
//				}
//			}
			
			if(getStatusTaskId() != null && getStatusTaskId().length > 0){
				pst = con.prepareStatement("update activity_info set completed=? where task_id = ?");
				for (int i = 0; getStatusTaskId() != null && i < getStatusTaskId().length; i++) {
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(getStatus()[i]))));
					pst.setInt(2, uF.parseToInt(getStatusTaskId()[i]));
					pst.addBatch();
				}
				int[] x = pst.executeBatch();
				pst.close();
				
				if(x.length > 0){
					for (int i = 0; getStatusTaskId() != null && i < getStatusTaskId().length; i++) {
						if(uF.parseToDouble(getStatus()[i])>= 100) {
							Map<String, String> hmTaskProData = CF.getTaskProInfo(con, getStatusTaskId()[i], null);
							
							Map<String, String> hmEmpData = hmEmpInfo.get(strSessionEmpId);
							String strDomain = request.getServerName().split("\\.")[0];
							Notifications nF = new Notifications(N_TASK_COMPLETED, CF); 
							nF.setDomain(strDomain);
							
							nF.request = request;
							nF.setStrOrgId(strEmpOrgId);
							nF.setEmailTemplate(true);
							
							nF.setStrEmpId(strSessionEmpId);
							nF.setStrResourceFName(hmEmpData.get("FNAME"));
							nF.setStrResourceLName(hmEmpData.get("LNAME"));
							nF.setStrTaskName(hmTaskProData.get("TASK_NAME"));
							nF.setStrProjectName(hmTaskProData.get("PRO_NAME"));
							nF.setStrTeamLeader(hmTaskProData.get("TEAM_LEADER"));
							nF.setStrDoneBy(hmEmpName.get(strSessionEmpId));
							
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							nF.sendNotifications();
						}
					}
				}
			}
			

//			for (int i = 0; getTaskId() != null && i < getTaskId().length && uF.parseToInt(getStrEmpId()) > 0; i++) {
//
//				if (uF.parseToInt(getTaskId()[i]) > 0 ) { //&& uF.parseToDouble(getStrTime()[i]) > 0
//					pst = con.prepareStatement("update task_activity set activity_id=?, activity=?, task_date=?, emp_id=?, actual_hrs=?, start_time=?, " +
//						"end_time=?, total_time=?, is_billable=?, activity_description=?, task_location=?, billable_hrs=? where task_id=?");
//					pst.setInt(1, uF.parseToInt(getStrTask()[i]));
//					if (uF.parseToInt(getStrTask()[i]) > 0) {
//						pst.setString(2, "");
//					} else {
//						pst.setString(2, getStrTask()[i]);
//					}
//
//					pst.setDate(3, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
//					pst.setInt(4, uF.parseToInt(getStrEmpId()));
//					pst.setDouble(5, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
//					pst.setTime(6, uF.getTimeFormat("10:00", DBTIME));
//					pst.setTime(7, uF.getTimeFormat("18:00", DBTIME));
//					pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
//					pst.setBoolean(9, uF.parseToBoolean(strBillableYesNoT[i]));
//					pst.setString(10, ((getTaskDescription() != null) ? getTaskDescription()[i] : ""));
//					pst.setString(11,((strTaskOnOffSiteT[i].equalsIgnoreCase("1")) ? "ONS": "OFS"));
//					pst.setDouble(12, uF.parseToBoolean(strBillableYesNoT[i]) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrBillableTime()[i])) : 0.0d);
//					pst.setInt(13, uF.parseToInt(getTaskId()[i]));
//					pst.execute();
//					pst.close();
//					
//					session.setAttribute(MESSAGE, SUCCESSM + "Your task has been updated successfully." + END);
//
//				} else if (uF.parseToDouble(getStrTime()[i]) > 0 && strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY"))) && getStrEmpId().equals(strSessionEmpId)) {
//					String clentID = CF.getClientIdByProjectTaskId(con, uF, getStrTask()[i], getStrEmpId());
//					pst = con.prepareStatement("insert into task_activity (activity_id, activity, task_date, emp_id, actual_hrs, start_time, end_time, " +
//						"total_time, is_billable, activity_description, task_location,generated_date,timesheet_paycycle,is_approved, client_id, " +
//						"billable_hrs) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
//					pst.setInt(1, uF.parseToInt(getStrTask()[i]));
//					if (uF.parseToInt(getStrTask()[i]) > 0) {
//						pst.setString(2, "");
//					} else {
//						pst.setString(2, getStrTask()[i]);
//					}
//					pst.setDate(3, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
//					pst.setInt(4, uF.parseToInt(getStrEmpId()));
//					pst.setDouble(5, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
//					pst.setTime(6, uF.getTimeFormat("10:00", DBTIME));
//					pst.setTime(7, uF.getTimeFormat("18:00", DBTIME));
//					pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
//					pst.setBoolean(9, uF.parseToBoolean(strBillableYesNoT[i]));
//					pst.setString(10, ((getTaskDescription() != null) ? getTaskDescription()[i] : ""));
//					pst.setString(11,((strTaskOnOffSiteT[i].equalsIgnoreCase("1")) ? "ONS": "OFS"));
//					pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(13, uF.parseToInt(strPayCycleDates[2]));
//					pst.setInt(14, 0);
//					pst.setInt(15, uF.parseToInt(clentID));
//					pst.setDouble(16, uF.parseToBoolean(strBillableYesNoT[i]) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrBillableTime()[i])) : 0.0d);
//					pst.execute();
//					pst.close();
//
//					session.setAttribute(MESSAGE, SUCCESSM + "Your task has been saved successfully." + END);
//
//				} else if (uF.parseToDouble(getStrTime()[i]) > 0 && strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN)) && (getFillUserType() == null || !getFillUserType().equals("MY"))) {
//					String clentID = CF.getClientIdByProjectTaskId(con, uF, getStrTask()[i], getStrEmpId());
//					pst = con.prepareStatement("insert into task_activity (activity_id, activity, task_date, emp_id, actual_hrs, start_time, end_time, " +
//						"total_time, is_billable, activity_description, task_location,generated_date,timesheet_paycycle,is_approved, client_id, " +
//						"billable_hrs,submited_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
//					pst.setInt(1, uF.parseToInt(getStrTask()[i]));
//					if (uF.parseToInt(getStrTask()[i]) > 0) {
//						pst.setString(2, "");
//					} else {
//						pst.setString(2, getStrTask()[i]);
//					}
//					pst.setDate(3, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
//					pst.setInt(4, uF.parseToInt(getStrEmpId()));
//					pst.setDouble(5, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
//					pst.setTime(6, uF.getTimeFormat("10:00", DBTIME));
//					pst.setTime(7, uF.getTimeFormat("18:00", DBTIME));
//					pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
//					pst.setBoolean(9, uF.parseToBoolean(strBillableYesNoT[i]));
//					pst.setString(10, ((getTaskDescription() != null) ? getTaskDescription()[i] : ""));
//					pst.setString(11,((strTaskOnOffSiteT[i].equalsIgnoreCase("1")) ? "ONS": "OFS"));
//					pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(13, uF.parseToInt(strPayCycleDates[2]));
//					pst.setInt(14, 1);
//					pst.setInt(15, uF.parseToInt(clentID));
//					pst.setDouble(16, uF.parseToBoolean(strBillableYesNoT[i]) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrBillableTime()[i])) : 0.0d);
//					pst.setDate(17, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.execute();
//					pst.close();
//					
//					session.setAttribute(MESSAGE, SUCCESSM + "Your task has been saved successfully." + END);
//				}
//
//				insertintoActivityandProject(uF, getStrTask()[i], con);
//				
//			}
			/**
			 * update task
			 * */
//			System.out.println("getTaskId().length ====>>> " + getTaskId().length + " getStrEmpId() ===>> " + getStrEmpId());
			if(getTaskId() != null && getTaskId().length > 0 && uF.parseToInt(getStrEmpId()) > 0) {
				pst = con.prepareStatement("update task_activity set activity_id=?, activity=?, task_date=?, emp_id=?, actual_hrs=?, start_time=?, " +
					"end_time=?, total_time=?, is_billable=?, activity_description=?, task_location=?, billable_hrs=? where task_id=?");
				boolean flag = false;
				for (int i = 0; getTaskId() != null && i < getTaskId().length && uF.parseToInt(getStrEmpId()) > 0; i++) {
					if (uF.parseToInt(getTaskId()[i]) > 0) { //&& uF.parseToDouble(getStrTime()[i]) > 0
						
						pst.setInt(1, uF.parseToInt(getStrTask()[i]));
						if (uF.parseToInt(getStrTask()[i]) > 0) {
							pst.setString(2, "");
						} else {
							pst.setString(2, getStrTask()[i]);
						}
	
						pst.setDate(3, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
						pst.setInt(4, uF.parseToInt(getStrEmpId()));
						pst.setDouble(5, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
						pst.setTime(6, uF.getTimeFormat("10:00", DBTIME));
						pst.setTime(7, uF.getTimeFormat("18:00", DBTIME));
						pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
						pst.setBoolean(9, uF.parseToBoolean(strBillableYesNoT[i]));
						pst.setString(10, ((getTaskDescription() != null) ? getTaskDescription()[i] : ""));
					//===start parvez date: 29-12-2022===
						pst.setString(11,(strTaskOnOffSiteT!=null?((strTaskOnOffSiteT[i].equalsIgnoreCase("1")) ? "ONS": "OFS"):""));
					//===end parvez date: 29-12-2022===	
						pst.setDouble(12, uF.parseToBoolean(strBillableYesNoT[i]) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrBillableTime()[i])) : 0.0d);
						pst.setInt(13, uF.parseToInt(getTaskId()[i]));
						pst.addBatch();
						
						flag = true;
					}
					
				}
//				System.out.println("pst out ====>>> " + pst);
				if(flag){
//					System.out.println("pst ====>>> " + pst);
					int[] x = pst.executeBatch();
					pst.close();
					if(x.length > 0){
						for (int i = 0; getTaskId() != null && i < getTaskId().length && uF.parseToInt(getStrEmpId()) > 0; i++) {
							if (uF.parseToInt(getTaskId()[i]) > 0 ) {
								insertintoActivityandProject(uF, getStrTask()[i], con);
							}
						}
						session.setAttribute(MESSAGE, SUCCESSM + "Your task has been updated successfully." + END);
					}
				} else {
					if(pst !=null){
						pst.close();
					}
				}
				
				flag = false;
//				System.out.println("strUserType ===>> " + strUserType + " -- getFillUserType ===>> " + getFillUserType());
				pst = con.prepareStatement("insert into task_activity (activity_id, activity, task_date, emp_id, actual_hrs, start_time, end_time, " +
						"total_time, is_billable, activity_description, task_location,generated_date,timesheet_paycycle,is_approved, client_id, " +
						"billable_hrs) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
//				System.out.println("getTaskId=="+getTaskId().length);
				for (int i = 0; getTaskId() != null && i < getTaskId().length && uF.parseToInt(getStrEmpId()) > 0; i++) {
					if (uF.parseToInt(getTaskId()[i]) == 0 && uF.parseToDouble(getStrTime()[i]) > 0 && strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY"))) && getStrEmpId().equals(strSessionEmpId)) {
						String clentID = CF.getClientIdByProjectTaskId(con, uF, getStrTask()[i], getStrEmpId());
						pst.setInt(1, uF.parseToInt(getStrTask()[i]));
						if (uF.parseToInt(getStrTask()[i]) > 0) {
							pst.setString(2, "");
						} else {
							pst.setString(2, getStrTask()[i]);
						}
						pst.setDate(3, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
						pst.setInt(4, uF.parseToInt(getStrEmpId()));
						pst.setDouble(5, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
						pst.setTime(6, uF.getTimeFormat("10:00", DBTIME));
						pst.setTime(7, uF.getTimeFormat("18:00", DBTIME));
						pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
						pst.setBoolean(9, uF.parseToBoolean(strBillableYesNoT[i]));
						pst.setString(10, ((getTaskDescription() != null) ? getTaskDescription()[i] : ""));
					//===start parvez date: 29-12-2022===	
						pst.setString(11,(strTaskOnOffSiteT!=null ? ((strTaskOnOffSiteT[i].equalsIgnoreCase("1")) ? "ONS": "OFS"):""));
					//===end parvez date: 29-12-2022===	
						pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(13, uF.parseToInt(strPayCycleDates[2]));
						pst.setInt(14, 0);
						pst.setInt(15, uF.parseToInt(clentID));
						pst.setDouble(16, uF.parseToBoolean(strBillableYesNoT[i]) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrBillableTime()[i])) : 0.0d);
						pst.addBatch();
						flag = true;
//						session.setAttribute(MESSAGE, SUCCESSM + "Your task has been saved successfully." + END);
					}
				}
//				System.out.println("insert pst out ====>>> " + pst);
				
				if(flag) {
					int[] x = pst.executeBatch();
					pst.close();
					if(x.length > 0){
						for (int i = 0; getTaskId() != null && i < getTaskId().length && uF.parseToInt(getStrEmpId()) > 0; i++) {
							if (uF.parseToInt(getTaskId()[i]) == 0 && uF.parseToDouble(getStrTime()[i]) > 0 && strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY"))) && getStrEmpId().equals(strSessionEmpId)) {
								insertintoActivityandProject(uF, getStrTask()[i], con);
							}
						}
						session.setAttribute(MESSAGE, SUCCESSM + "Your task has been updated successfully." + END);
					}
				} else {
					if(pst !=null){
						pst.close();
					}
				}
				
				flag = false;
				pst = con.prepareStatement("insert into task_activity (activity_id, activity, task_date, emp_id, actual_hrs, start_time, end_time, " +
						"total_time, is_billable, activity_description, task_location,generated_date,timesheet_paycycle,is_approved, client_id, " +
						"billable_hrs,submited_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
//				System.out.println("insert pst ====>>> " + pst);
				for (int i = 0; getTaskId() != null && i < getTaskId().length && uF.parseToInt(getStrEmpId()) > 0; i++) {
					if (uF.parseToInt(getTaskId()[i]) == 0 && uF.parseToDouble(getStrTime()[i]) > 0 && strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN)) && (getFillUserType() == null || !getFillUserType().equals("MY"))) {
						String clentID = CF.getClientIdByProjectTaskId(con, uF, getStrTask()[i], getStrEmpId());
						
						pst.setInt(1, uF.parseToInt(getStrTask()[i]));
						if (uF.parseToInt(getStrTask()[i]) > 0) {
							pst.setString(2, "");
						} else {
							pst.setString(2, getStrTask()[i]);
						}
						pst.setDate(3, uF.getDateFormat(getStrDate()[i], DATE_FORMAT));
						pst.setInt(4, uF.parseToInt(getStrEmpId()));
						pst.setDouble(5, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
						pst.setTime(6, uF.getTimeFormat("10:00", DBTIME));
						pst.setTime(7, uF.getTimeFormat("18:00", DBTIME));
						pst.setDouble(8, uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrTime()[i])));
						pst.setBoolean(9, uF.parseToBoolean(strBillableYesNoT[i]));
						pst.setString(10, ((getTaskDescription() != null) ? getTaskDescription()[i] : ""));
						pst.setString(11,(strTaskOnOffSiteT!=null?((strTaskOnOffSiteT[i].equalsIgnoreCase("1")) ? "ONS": "OFS"):""));
						pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(13, uF.parseToInt(strPayCycleDates[2]));
						pst.setInt(14, 1);
						pst.setInt(15, uF.parseToInt(clentID));
						pst.setDouble(16, uF.parseToBoolean(strBillableYesNoT[i]) ? uF.parseToDouble(uF.getTotalTimeMinutes60To100(getStrBillableTime()[i])) : 0.0d);
						pst.setDate(17, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.addBatch();
						
						flag = true;
						
					}
				}
//				System.out.println("insert pst after ====>>> " + pst);
				if(flag){
					int[] x = pst.executeBatch();
//					System.out.println("insert pst after batch ====>>> " + pst);
					pst.close();
					if(x.length > 0){
						for (int i = 0; getTaskId() != null && i < getTaskId().length && uF.parseToInt(getStrEmpId()) > 0; i++) {
							if (uF.parseToInt(getTaskId()[i]) == 0 && uF.parseToDouble(getStrTime()[i]) > 0 && strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ADMIN)) && (getFillUserType() == null || !getFillUserType().equals("MY"))) {
								insertintoActivityandProject(uF, getStrTask()[i], con);
							}
						}
						session.setAttribute(MESSAGE, SUCCESSM + "Your task has been updated successfully." + END);
					}
				} else {
					if(pst !=null){
						pst.close();
					}
				}
			}
			setTaskId(null);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}

	
	private void insertCompensatoryLeave(String coffDate, Connection con, UtilityFunctions uF, String levelID, String strWLocationId,
			Map<String, String> hmEmpLevelMap) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("in insertCompensatoryLeave ===>> ");
			pst = con.prepareStatement("select a.*,b.is_modify as modify from(select * from emp_leave_entry where emp_id=? and ? between approval_from and approval_to_date " +
				"and is_approved in (0,1) )as a left join (select * from leave_application_register where emp_id=? and to_date(_date::text,'yyyy-MM-dd') between ? and ?)as b " +
				"on a.leave_id=b.leave_id where a.leave_type_id in (select leave_type_id from emp_leave_type where is_compensatory=true or is_constant_balance=false)");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(coffDate, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(coffDate, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			pst.setDate(4, uF.getDateFormat(coffDate, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(coffDate, DATE_FORMAT));
			rs = pst.executeQuery();
			boolean flag = false;
			while (rs.next()) {
				if (uF.parseToBoolean(rs.getString("modify"))) {
					flag = false;
				} else if (uF.parseToInt(rs.getString("is_approved")) == 1 || uF.parseToInt(rs.getString("is_approved")) == 0) {
					flag = true;
				}
			}
			rs.close();
			pst.close();
//			System.out.println("flag ===>> " + flag);
			
			
			if (!flag) {
				pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				rs = pst.executeQuery();
				String strAllowedLeaves = null;
				boolean isProbation = false;
				while (rs.next()) {
					strAllowedLeaves = rs.getString("leaves_types_allowed");
					isProbation = rs.getBoolean("is_probation");
				}
				rs.close();
				pst.close();

//				System.out.println("isProbation ===>> "+ isProbation +" -- strAllowedLeaves ===>> " + strAllowedLeaves);
				
				String arr[] = null;
				if (isProbation && strAllowedLeaves != null && strAllowedLeaves.length() > 0) {
					arr = strAllowedLeaves.split(",");
				}

				pst = con.prepareStatement("select org_id from employee_official_details where emp_id = ?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				rs = pst.executeQuery();
				int nOrgId = 0;
				while (rs.next()) {
					nOrgId = rs.getInt("org_id");
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id "
								+ " and level_id=? and lt.org_id=? and wlocation_id=? and effective_date = (select max(effective_date) from emp_leave_type where level_id = ? "
								+ " and is_compensatory = true and org_id=? and wlocation_id=?) and lt.is_compensatory = true");
				pst.setInt(1, uF.parseToInt(levelID));
				pst.setInt(2, nOrgId);
				pst.setInt(3, uF.parseToInt(strWLocationId));
				pst.setInt(4, uF.parseToInt(levelID));
				pst.setInt(5, nOrgId);
				pst.setInt(6, uF.parseToInt(strWLocationId));
//				System.out.println("pst compLeaveId ===>> " + pst);
				rs = pst.executeQuery();
				int compLeaveId = 0;
				while (rs.next()) {

//					System.out.println("leave_type_id ===>> "+rs.getInt("leave_type_id"));
					if (isProbation && arr != null && ArrayUtils.contains(arr, rs.getString("leave_type_id")) >= 0) {
						compLeaveId = rs.getInt("leave_type_id");
					} else if (!isProbation) {
						compLeaveId = rs.getInt("leave_type_id");
					}
				}
				rs.close();
				pst.close();
//				System.out.println("compLeaveId ===>> " + compLeaveId);
				pst = con.prepareStatement("select * from emp_leave_entry where emp_id = ? and leave_type_id=? and approval_from=? and approval_to_date=?");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, compLeaveId);
				pst.setDate(3, uF.getDateFormat(coffDate, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(coffDate, DATE_FORMAT));
				rs = pst.executeQuery();
				while (rs.next()) {
					return;
				}
				rs.close();
				pst.close();
				
//				System.out.println("compLeaveId ===>> " + compLeaveId);
				
				if (compLeaveId > 0) {

					pst = con.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ?");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, compLeaveId);
//					System.out.println("APA1/4381---pst="+pst);
					rs = pst.executeQuery();
					boolean isApproval = false;
					boolean isPaid = false;
					String policy_id = null;
					while (rs.next()) {
						isApproval = uF.parseToBoolean(rs.getString("is_approval"));
						isPaid = uF.parseToBoolean(rs.getString("is_paid"));
						policy_id = rs.getString("policy_id");
					}
					rs.close();
					pst.close();

					pst = con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,"
									+ "leave_type_id,reason,approval_from,approval_to_date, ishalfday, is_approved, ispaid,is_compensate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setDate(2, uF.getDateFormat(coffDate, DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(coffDate, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(coffDate, DATE_FORMAT));
					int nAppliedDays = 0;
					nAppliedDays = uF.parseToInt(uF.dateDifference(coffDate,DATE_FORMAT, coffDate, DATE_FORMAT,CF.getStrTimeZone()));
					pst.setInt(5, nAppliedDays);

					pst.setInt(6, compLeaveId);
					pst.setString(7, "Extra Working");
					pst.setDate(8, uF.getDateFormat(coffDate, DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(coffDate, DATE_FORMAT));
					pst.setBoolean(10, false);
					if (isApproval) {
						pst.setInt(11, 0);
					} else {
						pst.setInt(11, 1);
					}
					pst.setBoolean(12, isPaid);
					pst.setBoolean(13, true);
					pst.execute();
					pst.close();
					
					String leave_id = null;
					pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
					rs = pst.executeQuery();
					while (rs.next()) {
						leave_id = rs.getString("leave_id");
					}
					rs.close();
					pst.close();
					
					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
						
						pst = con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where "
										+ " policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
						pst.setInt(1, uF.parseToInt(policy_id));
						rs = pst.executeQuery();

						Map<String, List<String>> hmMemberMap = new LinkedHashMap<String, List<String>>();
						while (rs.next()) {
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("member_type"));
							innerList.add(rs.getString("member_id"));
							innerList.add(rs.getString("member_position"));
							innerList.add(rs.getString("work_flow_mem"));
							innerList.add(rs.getString("work_flow_member_id"));

							hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
						}
						rs.close();
						pst.close();

						Iterator<String> it = hmMemberMap.keySet().iterator();
						while (it.hasNext()) {
							String work_flow_member_id = it.next();
							List<String> innerList = hmMemberMap.get(work_flow_member_id);

							int memid = uF.parseToInt(innerList.get(1));
							String[] empid = request.getParameterValues(innerList.get(3) + memid);

							if (empid != null && empid.length > 0) { 
								int userTypeId = memid;
								if(uF.parseToInt(innerList.get(0)) == 3){
									userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
								}
								pst = con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position,"
									+ "work_flow_mem_id,is_approved,status,user_type_id) values(?,?,?,?, ?,?,?,?, ?)");
								pst.setInt(1, uF.parseToInt(empid[0]));
								pst.setInt(2, uF.parseToInt(leave_id));
								pst.setString(3, WORK_FLOW_LEAVE);
								pst.setInt(4, uF.parseToInt(innerList.get(0)));
								pst.setInt(5, (int) uF.parseToDouble(innerList.get(2)));
								pst.setInt(6, uF.parseToInt(innerList.get(4)));
								pst.setInt(7, 0);
								pst.setInt(8, 0);
								pst.setInt(9,userTypeId);
								pst.execute();
								pst.close();

							}
						}
					}

//					System.out.println("APA1/4579--isApproval ===>> " + isApproval);
//					System.out.println("APA1/4580--leave_id ===>> " + leave_id);
					if (!isApproval && uF.parseToInt(leave_id) > 0) {

//						System.out.println("APA1/4580--leave_id="+leave_id+"--compLeaveId="+compLeaveId);
						ManagerLeaveApproval leaveApproval = new ManagerLeaveApproval();
						leaveApproval.setServletRequest(request);
						leaveApproval.setLeaveId(leave_id);
						leaveApproval.setTypeOfLeave("" + compLeaveId);
						leaveApproval.setEmpId(getStrEmpId());
						leaveApproval.setIsapproved(1);
						leaveApproval.setApprovalFromTo(coffDate);
						leaveApproval.setApprovalToDate(coffDate);
//						leaveApproval.insertLeaveBalance(con, pst, rs, uF,hmEmpLevelMap, CF);
						leaveApproval.insertLeaveBalance(con, pst, rs, uF,uF.parseToInt(hmEmpLevelMap.get(getStrEmpId())), CF);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertintoActivityandProject(UtilityFunctions uF, String activityID, Connection con) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			
			if (uF.parseToInt(activityID) > 0) {
				StringBuilder sumActivityQuery = new StringBuilder();

				sumActivityQuery.append("select sum(actual_hrs)as actual_hrs, count (distinct(task_date)) as actual_days from task_activity where emp_id = ? "
								+ " and activity_id=?");
				pst = con.prepareStatement(sumActivityQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, uF.parseToInt(activityID));
				rst = pst.executeQuery();
				String actual_hrs = null;
				String actual_days = null;
				while (rst.next()) {
					actual_hrs = rst.getString("actual_hrs");
					actual_days = rst.getString("actual_days");
				}
				rst.close();
				pst.close();

				
				String pro_id = "";
				pst = con.prepareStatement("select task_id,pro_id from activity_info where task_id=?");
				pst.setInt(1, uF.parseToInt(activityID));
				rst = pst.executeQuery();
				while (rst.next()) {
					pro_id = rst.getString("pro_id");
				}
				rst.close();
				pst.close();
				

				pst = con.prepareStatement("update activity_info set already_work=?, already_work_days=? where task_id=?");
				pst.setDouble(1, uF.parseToDouble(actual_hrs));
				pst.setDouble(2, uF.parseToDouble(actual_days));
				pst.setInt(3, uF.parseToInt(activityID));
				pst.execute();
				pst.close();
				
				int parentTaskId = 0;
				pst = con.prepareStatement("select parent_task_id from activity_info where task_id = ? and parent_task_id != 0");
				pst.setInt(1, uF.parseToInt(activityID));
				rst = pst.executeQuery();
				while(rst.next()) {
					parentTaskId = rst.getInt("parent_task_id");
				}
				rst.close();
				pst.close();
				
				double dblAllCompleted = 0.0d;
				int subTaskCnt = 0;
				String taskActualHrs = null;
				String taskActualDays = null;
				pst = con.prepareStatement("select sum(completed) as completed, count(task_id) as count, sum(already_work)as already_work, " +
					"sum(already_work_days) as already_work_days from activity_info where parent_task_id = ?");
				pst.setInt(1, parentTaskId);
//				System.out.println("pst ==>> " + pst);
				rst = pst.executeQuery();
				while(rst.next()) {
					dblAllCompleted = rst.getDouble("completed");
					subTaskCnt = rst.getInt("count");
					taskActualHrs = rst.getString("already_work");
					taskActualDays = rst.getString("already_work_days");
				}
				rst.close();
				pst.close();
				
//				System.out.println("taskActualHrs ==>> " + taskActualHrs);
//				System.out.println("taskActualDays ==>> " + taskActualDays);
				
				double avgComplted = 0.0d;
				if(dblAllCompleted > 0 && subTaskCnt > 0) {
					avgComplted = dblAllCompleted / subTaskCnt;
				}
				
//				if(avgComplted > 0) {
					pst = con.prepareStatement("update activity_info set completed=?, already_work=?, already_work_days=? where task_id=?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(avgComplted)));
					pst.setDouble(2, uF.parseToDouble(taskActualHrs));
					pst.setDouble(3, uF.parseToDouble(taskActualDays));
					pst.setInt(4, parentTaskId);
					pst.execute();
					pst.close();
//				}
				
				pst = con.prepareStatement("select sum(already_work)as already_work, sum(already_work_days)as already_work_day, " +
					"sum(completed)/count(task_id) as avrg  from activity_info where pro_id=? and parent_task_id = 0");
				pst.setInt(1, uF.parseToInt(pro_id));
				rst = pst.executeQuery();
				String project_hrs = null;
				String project_days = null;
				String projectCompletePercent = null;
				while (rst.next()) {
					project_hrs = rst.getString("already_work");
					project_days = rst.getString("already_work_day");
					projectCompletePercent = uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rst.getString("avrg")));
				}
				rst.close();
				pst.close();

				pst = con.prepareStatement("update projectmntnc set already_work=?, already_work_days=?, completed=? where pro_id=? ");
				pst.setDouble(1, uF.parseToDouble(project_hrs));
				pst.setDouble(2, uF.parseToDouble(project_days));
				pst.setDouble(3, uF.parseToDouble(projectCompletePercent));
				pst.setInt(4, uF.parseToInt(pro_id));
				pst.execute();
				pst.close();
			}
		

//			if (uF.parseToInt(activityID) > 0) {
//				StringBuilder sumActivityQuery = new StringBuilder();
//
////				sumActivityQuery.append("select sum(actual_hrs)as actual_hrs,activity_id from task_activity where emp_id = ? "
////								+ " and activity_id in (select task_id from  activity_info where  pro_id in "
////								+ " (select pmt.pro_id from projectmntnc pmt)) and activity_id=? group by activity_id  order by activity_id");
//				sumActivityQuery.append("select sum(actual_hrs)as actual_hrs,activity_id from task_activity where emp_id = ? "
//						+ " and activity_id=?");
//				pst = con.prepareStatement(sumActivityQuery.toString());
//				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				pst.setInt(2, uF.parseToInt(activityID));
//				rst = pst.executeQuery();
//				String actual_hrs = null;
//				while (rst.next()) {
//					actual_hrs = rst.getString("actual_hrs");
//				}
//
//				pst = con.prepareStatement("select task_id,pro_id from  activity_info order by task_id");
//				rst = pst.executeQuery();
//				Map<String, String> hmActivityProID = new HashMap<String, String>();
//				while (rst.next()) {
//					hmActivityProID.put(rst.getString("task_id"), rst.getString("pro_id"));
//				}
//				String pro_id = hmActivityProID.get(activityID);
//
//				pst = con.prepareStatement("update activity_info set already_work=? where task_id=? ");
//				pst.setDouble(1, uF.parseToDouble(actual_hrs));
//				pst.setInt(2, uF.parseToInt(activityID));
//				pst.execute();
//
////				pst = con.prepareStatement("select sum(already_work)as already_work from  activity_info where pro_id in "
////								+ " (select pmt.pro_id from projectmntnc pmt where pmt.pro_id=?) group by pro_id");
//				pst = con.prepareStatement("select sum(already_work)as already_work from activity_info where pro_id=?");
//				pst.setInt(1, uF.parseToInt(pro_id));
//				rst = pst.executeQuery();
//				String project_hrs = null;
//				while (rst.next()) {
//					project_hrs = rst.getString("already_work");
//				}
//
//				pst = con.prepareStatement("update projectmntnc set already_work=? where pro_id=? ");
//				pst.setDouble(1, uF.parseToDouble(project_hrs));
//				pst.setInt(2, uF.parseToInt(pro_id));
//				pst.execute();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	String[] statusTaskId;
	String[] status;

	String[] taskId;
	String[] strDate;
	String[] strTask;
	String[] strTime;
	String[] strBillableTime;
	String[] taskDescription;
	String[] strTaskOnOffSite;
	String[] strTaskOnOffSiteT;
	String[] strBillableYesNo;
	String[] strBillableYesNoT;

	private String getConvertedTime(String tasktime) {
		int hour = 0;
		int minute = 0;
		UtilityFunctions uF = new UtilityFunctions();

		String convertedTime = null;

		if (tasktime != null && tasktime.contains(".")) {
			hour = (int) uF.parseToDouble(tasktime);
			double minustime = uF.parseToDouble(tasktime) - uF.parseToDouble("" + hour);
			double minutetime = uF.parseToDouble(uF.formatIntoTwoDecimal(minustime)) * 100;
			minute = (int) minutetime;
		} else if (tasktime != null) {
			hour = uF.parseToInt(tasktime);
			minute = 0;
		} else {
			hour = 0;
			minute = 0;
		}

		convertedTime = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);

		return convertedTime;
	}

	private Date getAddedTime(String task_date, String locationstarttime, String tasktime) {
		int hour = 0;
		int minute = 0;
		UtilityFunctions uF = new UtilityFunctions();
		if (tasktime != null && tasktime.contains(".")) {
			hour = (int) uF.parseToDouble(tasktime);
			double minustime = uF.parseToDouble(tasktime) - uF.parseToDouble("" + hour);
			double minutetime = uF.parseToDouble(uF.formatIntoTwoDecimal(minustime)) * 100;
			minute = (int) minutetime;
		} else if (tasktime != null) {
			hour = uF.parseToInt(tasktime);
			minute = 0;
		} else {
			hour = 0;
			minute = 0;
		}

		Date util_date = uF.getDateFormatUtil(task_date + " " + locationstarttime, "dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(util_date);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		cal.add(Calendar.MINUTE, minute);

		java.util.Date utilDate = cal.getTime();

		return utilDate;
	}

	
	
	public void fillTaskRows(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
//		System.out.println("=========>> 1 fillTaskRow ");
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
			
			pst = con.prepareStatement("select financial_year_from FROM financial_year_details order by financial_year_from limit 1");
			rs = pst.executeQuery();
			String fYear = "";
			while(rs.next()) {
				fYear = rs.getString("financial_year_from");
			}
			rs.close();
			pst.close();
			
			int fStartYear = uF.parseToInt(uF.getDateFormat(fYear, DBDATE, "yyyy"));
			yearList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()), ""+fStartYear); 
			
			
			String[] strPayCycleDates = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					strPayCycleDates = getStrPaycycle().split("-");
				} else {
					strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					strPayCycleDates = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			/*String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getStrPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strEmpOrgId);
				setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
			}*/
			
			pst = con.prepareStatement("select * from attendance_details where emp_id =? and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT'");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getFrmDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getToDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmAttendance = new HashMap<String, String>();
			while (rs.next()) {
				hmAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,DATE_FORMAT), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("hours_worked"))));
			}
			rs.close();
			pst.close();
//			System.out.println("hmAttendance ===>> " + hmAttendance);
			
			pst = con.prepareStatement("SELECT * FROM employee_official_details eod, employee_personal_details epd WHERE epd.emp_per_id=eod.emp_id and eod.emp_id=?  order by emp_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			String joiningDate=null; 
			while (rs.next()) {
				joiningDate= uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("joiningDate", joiningDate);
			
			pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs,task_date from task_activity where emp_id =? "
				+ "and to_date(task_date::text, 'YYYY-MM-DD') between ? and ? group by task_date order by task_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getFrmDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getToDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmTotalTime = new HashMap<String, String>();
			while (rs.next()) {
				hmTotalTime.put(uF.getDateFormat(rs.getString("task_date"),DBDATE, DATE_FORMAT), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("actual_hrs"))));
			}
			rs.close();
			pst.close();

			Map<String, String> hmClientMap = new HashMap<String, String>();
			Map<String, String> hmProjectMap = new HashMap<String, String>();
			pst = con.prepareStatement("select pro_id, pro_name, cd.client_id, client_name from projectmntnc pmc, client_details cd where pmc.client_id = cd.client_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmClientMap.put(rs.getString("pro_id"), rs.getString("client_name"));
				hmProjectMap.put(rs.getString("pro_id"), rs.getString("pro_name"));
			}
			rs.close();
			pst.close();

			/*
			 * if(uF.getDateFormat(strPayCycleDates[0],
			 * DATE_FORMAT).before(uF.getDateFormat(getFrmDate(),
			 * DATE_FORMAT))) { setFrmDate(strPayCycleDates[0]); }
			 * if(uF.getDateFormat(strPayCycleDates[1],
			 * DATE_FORMAT).before(uF.getDateFormat(getToDate(), DATE_FORMAT))) {
			 * setToDate(strPayCycleDates[1]); }
			 */

//			pst = con.prepareStatement("SELECT * FROM leave_type  where leave_type_id>0 order by leave_type_name");
//			rs = pst.executeQuery();
//			Map<String, String> hmLeaveTypeCode = new HashMap<String, String>();
//			while (rs.next()) {
//				hmLeaveTypeCode.put(rs.getString("leave_type_id"),rs.getString("leave_type_code"));
//			}
//			request.setAttribute("hmLeaveTypeCode", hmLeaveTypeCode);

//			pst = con.prepareStatement("select _date,leave_type_id from leave_application_register where  emp_id=? and _type=true and _date between ? and ?");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			rs = pst.executeQuery();
//
//			Map<String, String> hmEmpLeave = new HashMap<String, String>();
//			while (rs.next()) {
//				String leaveDate = uF.getDateFormat(rs.getString("_date"),DBDATE, DATE_FORMAT);
//				hmEmpLeave.put(leaveDate, rs.getString("leave_type_id"));
//			}
//
//			request.setAttribute("hmEmpLeave", hmEmpLeave);

			Map hmLeaveDays = new HashMap();
//			Map hmLeaveDatesType = new HashMap();
//			Map hmMonthlyLeaves = new HashMap();
			Map hmLeavesColour = new HashMap();
			CF.getLeavesColour(con, hmLeavesColour);
			
			Map hmLeaveCode = new HashMap();
			
			/*hmLeaveDays = CF.getLeaveDates(con, strPayCycleDates[0],strPayCycleDates[1], CF, hmLeaveDatesType, false,hmMonthlyLeaves);
			Map hmLeaves = (Map) hmLeaveDays.get(getStrEmpId());*/
			hmLeaveDays = getLeaveDetails(strPayCycleDates[0],strPayCycleDates[1],uF,hmLeaveCode);
			Map hmLeaves = (Map) hmLeaveDays.get(getStrEmpId());
			
			if (hmLeaves == null)
				hmLeaves = new HashMap();
			
		//===start parvez date: 17-06-2022===	
			Map hmAppliedLeaveDays = new HashMap();
			Map hmAppliedLeaveCode = new HashMap();
			
			hmAppliedLeaveDays = getAppliedLeaveDetails(strPayCycleDates[0],strPayCycleDates[1],uF,hmAppliedLeaveCode);
			Map hmAppliedLeaves = (Map) hmAppliedLeaveDays.get(getStrEmpId());
			if(hmAppliedLeaves==null) hmAppliedLeaves = new HashMap();
		//===end parvez date: 17-06-2022===	

			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekendMap = CF.getWeekEndDateList(con, strPayCycleDates[0],strPayCycleDates[1], CF, uF,hmWeekEndHalfDates,null);
//			System.out.println("hmWeekendMap ===>> " +hmWeekendMap);
			String strWLocationId = hmEmpWLocation.get(getStrEmpId()); 
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,strPayCycleDates[0],strPayCycleDates[1],alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekendMap,hmEmpLevelMap,hmEmpWLocation,hmWeekEndHalfDates);
//			System.out.println("hmWeekendMap 1111 ===>> " +hmWeekendMap);

			Map hmHolidays = new HashMap();
			Map hmHolidayDates = new HashMap();
			CF.getHolidayList(con,request,strPayCycleDates[0], strPayCycleDates[1], CF,hmHolidayDates, hmHolidays, true);

			Map hmLeaveConstant = new HashMap();
			pst = con.prepareStatement("select * from leave_application_register where leave_type_id in (select leave_type_id from emp_leave_type where is_constant_balance = true) and _date between ? and ? and emp_id =?");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLeaveConstant.put(uF.getDateFormat(rs.getString("_date"), DBDATE,CF.getStrReportDateFormat()),uF.getDateFormat(rs.getString("_date"), DBDATE,CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();

//			System.out.println("getFrmDate() before =====> " + getFrmDate() + " -- strPayCycleDates[0] ===>> " + strPayCycleDates[0]);
//			System.out.println("getToDate() before =====> " + getToDate() + " -- strPayCycleDates[1] ===>> " + strPayCycleDates[1]);
			
			if (uF.getDateFormat(getFrmDate(), DATE_FORMAT).before(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT))
					|| uF.getDateFormat(getFrmDate(), DATE_FORMAT).after(uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT))) {
				setFrmDate(strPayCycleDates[0]);
			}

			if (uF.getDateFormat(getToDate(), DATE_FORMAT).before(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT))
					|| uF.getDateFormat(getToDate(), DATE_FORMAT).after(uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT))) {
				setToDate(strPayCycleDates[1]);
			}

			nDateCount = uF.parseToInt(uF.dateDifference(getFrmDate(),DATE_FORMAT, getToDate(), DATE_FORMAT,CF.getStrTimeZone()));
//			System.out.println("nDateCount ===>> " + nDateCount);
			
			Map<String, String> hmTaskName = CF.getTaskNameMap(con);
//			System.out.println("getFrmDate() =====> " + getFrmDate());
//			System.out.println("getToDate() =====> " + getToDate());
			// pst = con.prepareStatement("select actual_hrs,activity_id, pro_id, task_date, activity_name  from task_activity ta, activity_info ai where ta.activity_id = ai.task_id and ta.emp_id =? and task_date between ? and ? order by activity_id");
			
			
//			pst = con.prepareStatement("select actual_hrs,activity_id, pro_id, task_date, activity_name, activity, task_location, billable_hrs, " +
//					"is_billable, is_approved, ta.task_id, ta._comment from task_activity ta left join activity_info ai on ta.activity_id = ai.task_id where ta.emp_id =? and task_date between ? and ? order by activity_id desc, activity");
//			pst = con.prepareStatement("select sum(a.actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, ai.parent_task_id, a.task_date, " +
//				"a.task_location,ai.pro_id from (select sum(actual_hrs) as actual_hrs,sum(billable_hrs) as billable_hrs,ta.activity_id," +
//				"a.task_date,ta.task_location from task_activity ta where ta.emp_id =? and task_date between ? and ? and ta.activity_id in (" +
//				"select task_id from activity_info where resource_ids like '%,"+getStrEmpId()+",%' and parent_task_id in (select task_id from " +
//				"activity_info where resource_ids like '%,"+getStrEmpId()+",%')) group by ta.activity_id,ta.task_date,ta.task_location) as a, " +
//				"activity_info ai where a.activity_id = ai.task_id group by ai.pro_id,ai.parent_task_id, ta.task_date, a.task_location ");
			
			pst = con.prepareStatement("select sum(a.actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs,ai.parent_task_id,a.task_date," +
				"a.task_location,ai.pro_id from (select sum(actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, ta.activity_id," +
				"ta.task_location,ta.task_date from task_activity ta where ta.emp_id = ? and task_date between ? and ? and ta.activity_id in (" +
				"select task_id from activity_info where resource_ids like '%,"+getStrEmpId()+",%' and parent_task_id in (select task_id from " +
				"activity_info where resource_ids like '%,"+getStrEmpId()+",%')) group by ta.activity_id,ta.task_date,ta.task_location) as a, " +
				"activity_info ai where a.activity_id = ai.task_id group by ai.pro_id,ai.parent_task_id,a.task_date, a.task_location order by ai.parent_task_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
		
			Map<String, String> hmDateT = new HashMap<String, String>();
//			Map<String, String> hmDateHrsIsApprovedT = new HashMap<String, String>();
//			Map<String, String> hmDateTaskIdT = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrsT = new HashMap<String, String>();
			Map<String, String> hmTaskIsBillableT = new HashMap<String, String>();
//			Map<String, String> hmTaskDescriT = new HashMap<String, String>();
			Map<String, Map<String, String>> hmProjectsT = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmProjectsHrsIsApprovedT = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmProjectsTaskIdT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmProjectsBillableHrsT = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmProjectsTaskDescriT = new HashMap<String, Map<String, String>>();
			Map<String, String> hmTasksT = new HashMap<String, String>();
			Map<String, Map<String, String>> hmProjectTasksT = new LinkedHashMap<String, Map<String, String>>();
		
			Map<String, String> hmProjectCountT = new HashMap<String, String>();
			Map<String, String> hmProjectBillCountT = new HashMap<String, String>();
			int nCountT = 0;
			int nBillCountT = 0;
			double dblTotalHrsT = 0;
			double dblTotalBillableHrsT = 0;
			boolean isBillableT = false;
			String strActivityIdNewT = null;
			String strActivityIdOldT = null;
			String strProjectIdNewT = null;
			String strProjectIdOldT = null;
			while (rs.next()) {
		
				// if(!hmProjectMap.containsKey(rs.getString("pro_id"))) {
				// continue;
				// }
		
				strActivityIdNewT = rs.getString("parent_task_id");
				strProjectIdNewT = rs.getString("pro_id");
				/*if (uF.parseToInt(strActivityIdNewT) == 0) {
					strActivityIdNewT = rs.getString("activity");
				}*/
		
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT)) {
					dblTotalHrsT = 0;
					dblTotalBillableHrsT = 0;
//					isBillableT = false;
					hmDateT = new HashMap<String, String>();
					hmDateBillableHrsT = new HashMap<String, String>();
//					hmDateHrsIsApprovedT = new HashMap<String, String>();
//					hmDateTaskIdT = new HashMap<String, String>();
					hmTasksT = new HashMap<String, String>();
				}
		
				if (strProjectIdNewT != null && !strProjectIdNewT.equalsIgnoreCase(strProjectIdOldT)) {
					nCountT = 0;
					nBillCountT = 0;
					isBillableT = false;
				} else if (strProjectIdNewT == null && strProjectIdOldT != null) {
					nCountT = 0;
					nBillCountT = 0;
					isBillableT = false;
				}
		
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT)) {
					nCountT++;
				}
		
				
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT) && uF.parseToDouble(rs.getString("billable_hrs")) > 0) {
					nBillCountT++;
					isBillableT = true;
				} else if (!isBillableT && strActivityIdNewT != null && strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT) && uF.parseToDouble(rs.getString("billable_hrs")) > 0) {
					nBillCountT++;
					isBillableT = true;
				}
				
//				if(!isBillableT) {
//					isBillableT = rs.getBoolean("is_billable");
//					if(isBillableT) {
//						nBillCountT++;
//					}
//				}
				
				double dblHrs = uF.parseToDouble((String) hmDateT.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalHrsT = dblHrs+ uF.parseToDouble(rs.getString("actual_hrs"));
		
				hmDateT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalHrsT));
				
				double dblBillableHrs = uF.parseToDouble((String) hmDateBillableHrsT.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalBillableHrsT = dblBillableHrs + uF.parseToDouble(rs.getString("billable_hrs"));
		
				hmDateBillableHrsT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalBillableHrsT));
				
//				hmDateHrsIsApprovedT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("is_approved"));
				
//				hmDateTaskIdT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("task_id"));
				
//				hmTaskDescriT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("_comment"));
				// hmProjects.put(rs.getString("pro_id"), hmDate);
				// hmProjects.put(rs.getString("activity_id"), hmDate);
		
				// hmTasks.put(rs.getString("activity_id")+"_T",
				// rs.getString("activity_name"));
				// hmTasks.put(rs.getString("activity_id")+"_P",
				// rs.getString("pro_id"));
				// hmProjectTasks.put(rs.getString("activity_id"), hmTasks);
		
				hmProjectsT.put(strActivityIdNewT, hmDateT);
		
		//		if(intActivityId > 0) {
					hmProjectsBillableHrsT.put(strActivityIdNewT, hmDateBillableHrsT);
		//		}
				
//				hmProjectsHrsIsApprovedT.put(strActivityIdNewT, hmDateHrsIsApprovedT);
//				hmProjectsTaskDescriT.put(strActivityIdNewT, hmTaskDescriT);
				
//				hmProjectsTaskIdT.put(strActivityIdNewT, hmDateTaskIdT);
				
				hmTaskIsBillableT.put(strActivityIdNewT, isBillableT+"");
				
//				if (uF.parseToInt(rs.getString("activity_id")) == 0) {
//					hmTasksT.put(strActivityIdNewT + "_T",rs.getString("activity"));
//				} else {
//					hmTasksT.put(strActivityIdNewT + "_T",rs.getString("activity_name"));
//				}
//		
				hmTasksT.put(strActivityIdNewT + "_T", hmTaskName.get(rs.getString("parent_task_id")));
					
				hmTasksT.put(strActivityIdNewT + "_P", rs.getString("pro_id"));
				hmProjectTasksT.put(strActivityIdNewT, hmTasksT);
		
				hmProjectCountT.put(rs.getString("pro_id"), nCountT + "");
				hmProjectBillCountT.put(rs.getString("pro_id"), nBillCountT + "");
				
		//		System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount); 
				
				strActivityIdOldT = strActivityIdNewT;
				strProjectIdOldT = strProjectIdNewT;
			}
			rs.close();
			pst.close();
		
//			System.out.println("hmProjectsT ===>> " + hmProjectsT);
//			System.out.println("hmProjectCountT ===>> " + hmProjectCountT);
//			System.out.println("hmProjectBillCountT ===>> " + hmProjectBillCountT);
			
			request.setAttribute("hmProjectsT", hmProjectsT);
			request.setAttribute("hmProjectsBillableHrsT", hmProjectsBillableHrsT);
//			request.setAttribute("hmProjectsHrsIsApprovedT", hmProjectsHrsIsApprovedT);
//			request.setAttribute("hmProjectsTaskDescriT", hmProjectsTaskDescriT);
//			request.setAttribute("hmProjectsTaskIdT", hmProjectsTaskIdT);
			request.setAttribute("hmTaskIsBillableT", hmTaskIsBillableT);
			request.setAttribute("hmProjectTasksT", hmProjectTasksT);
			
			request.setAttribute("hmProjectCountT", hmProjectCountT);
			request.setAttribute("hmProjectBillCountT", hmProjectBillCountT);
			
			pst = con.prepareStatement("select actual_hrs, activity_id, pro_id, task_date, activity_name, activity, task_location, billable_hrs, " +
				"ta.is_billable, is_approved, ta.task_id, ta.activity_description from task_activity ta left join activity_info ai on ta.activity_id = ai.task_id " +
				"where ta.emp_id =? and (ai.resource_ids like '%,"+getStrEmpId()+",%' or activity_id = 0) and task_date between ? and ? order by pro_id, activity_id desc, activity");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			Map<String, String> hmDate = new HashMap<String, String>();
			Map<String, String> hmDateHrsIsApproved = new HashMap<String, String>();
			Map<String, String> hmDateTaskId = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrs = new HashMap<String, String>();
			Map<String, String> hmTaskIsBillable = new HashMap<String, String>();
			Map<String, String> hmTaskDescri = new HashMap<String, String>();
			Map<String, Map<String, String>> hmProjects = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmProjectsHrsIsApproved = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmProjectsTaskId = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmProjectsBillableHrs = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmProjectsTaskDescri = new HashMap<String, Map<String, String>>();

			Map<String, String> hmTasks = new HashMap<String, String>();
			Map hmProjectTasks = new LinkedHashMap();

			Map<String, String> hmProjectCount = new HashMap<String, String>();
			Map<String, String> hmProjectBillCount = new HashMap<String, String>();

			int nCount = 0;
			int nBillCount = 0;
			double dblTotalHrs = 0;
			double dblTotalBillableHrs = 0;
			boolean isBillable = false;
			String strActivityIdNew = null;
			String strActivityIdOld = null;
			String strProjectIdNew = null;
			String strProjectIdOld = null;
			while (rs.next()) {

				// if(!hmProjectMap.containsKey(rs.getString("pro_id"))) {
				// continue;
				// }

				strActivityIdNew = rs.getString("activity_id");
				strProjectIdNew = rs.getString("pro_id");
				if (uF.parseToInt(strActivityIdNew) == 0) {
					strActivityIdNew = rs.getString("activity");
				}

				if (strActivityIdNew != null && !strActivityIdNew.equalsIgnoreCase(strActivityIdOld)) {
					dblTotalHrs = 0;
					dblTotalBillableHrs = 0;
					isBillable = false;
					hmDate = new HashMap<String, String>();
					hmDateBillableHrs = new HashMap<String, String>();
					hmDateHrsIsApproved = new HashMap<String, String>();
					hmDateTaskId = new HashMap<String, String>();
					hmTasks = new HashMap<String, String>();
				}

				if (strProjectIdNew != null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
					nCount = 0;
					nBillCount = 0;
				} else if (strProjectIdNew == null && strProjectIdOld != null) {
					nCount = 0;
					nBillCount = 0;
				}

				if (strActivityIdNew != null && !strActivityIdNew.equalsIgnoreCase(strActivityIdOld)) {
					nCount++;
				}
//				System.out.println("strActivityIdNew ===>> " + strActivityIdNew+" --- strActivityIdOld ===>> " + strActivityIdOld);
				
				if(!isBillable) {
					isBillable = rs.getBoolean("is_billable");
					if(isBillable) {
						nBillCount++;
					}
				}
				
				double dblHrs = uF.parseToDouble((String) hmDate.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				/*double dblHrs = 0;
				if(uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_ON_SITE_IN_TIMESHEET))){
					dblHrs = uF.parseToDouble((String) hmDate.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)));
				}else{
					dblHrs = uF.parseToDouble((String) hmDate.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				}*/
				
				dblTotalHrs = dblHrs+ uF.parseToDouble(rs.getString("actual_hrs"));

				hmDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalHrs));
				/*if(uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_ON_SITE_IN_TIMESHEET))){
					hmDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT), uF.formatIntoTwoDecimalWithOutComma(dblTotalHrs));
				} else{
					hmDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalHrs));
				}*/
				
				double dblBillableHrs = uF.parseToDouble((String) hmDateBillableHrs.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalBillableHrs = dblBillableHrs + uF.parseToDouble(rs.getString("billable_hrs"));

				hmDateBillableHrs.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimalWithOutComma(dblTotalBillableHrs));
				
				hmDateHrsIsApproved.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("is_approved"));
				
				hmDateTaskId.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("task_id"));
				
				hmTaskDescri.put(rs.getString("task_id")+"_"+uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("activity_description"));
				// hmProjects.put(rs.getString("pro_id"), hmDate);
				// hmProjects.put(rs.getString("activity_id"), hmDate);

				// hmTasks.put(rs.getString("activity_id")+"_T",
				// rs.getString("activity_name"));
				// hmTasks.put(rs.getString("activity_id")+"_P",
				// rs.getString("pro_id"));
				// hmProjectTasks.put(rs.getString("activity_id"), hmTasks);

				hmProjects.put(strActivityIdNew, hmDate);

//				if(intActivityId > 0) {
					hmProjectsBillableHrs.put(strActivityIdNew, hmDateBillableHrs);
//				}
				
				hmProjectsHrsIsApproved.put(strActivityIdNew, hmDateHrsIsApproved);
				hmProjectsTaskDescri.put(strActivityIdNew, hmTaskDescri);
				
				hmProjectsTaskId.put(strActivityIdNew, hmDateTaskId);
				
				hmTaskIsBillable.put(strActivityIdNew, isBillable+"");
				
				if (uF.parseToInt(rs.getString("activity_id")) == 0) {
					hmTasks.put(strActivityIdNew + "_T",rs.getString("activity"));
				} else {
					hmTasks.put(strActivityIdNew + "_T",rs.getString("activity_name"));
				}

				hmTasks.put(strActivityIdNew + "_P", rs.getString("pro_id"));
				hmProjectTasks.put(strActivityIdNew, hmTasks);

				hmProjectCount.put(rs.getString("pro_id"), nCount + "");
				hmProjectBillCount.put(rs.getString("pro_id"), nBillCount + "");
				
//				System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount); 
				
				strActivityIdOld = strActivityIdNew;
				strProjectIdOld = strProjectIdNew;
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmProjectsTaskDescri ===>>> " + hmProjectsTaskDescri);
//			System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount);

			request.setAttribute("hmProjects", hmProjects);
			request.setAttribute("hmProjectsBillableHrs", hmProjectsBillableHrs);
			request.setAttribute("hmProjectsHrsIsApproved", hmProjectsHrsIsApproved);
			request.setAttribute("hmProjectsTaskDescri", hmProjectsTaskDescri);
			request.setAttribute("hmProjectsTaskId", hmProjectsTaskId);
			request.setAttribute("hmTaskIsBillable", hmTaskIsBillable);
			request.setAttribute("hmProjectTasks", hmProjectTasks);
			
			request.setAttribute("hmProjectCount", hmProjectCount);
			request.setAttribute("hmProjectBillCount", hmProjectBillCount);
			
			
			
			pst = con.prepareStatement("select activity_id, activity from task_activity where emp_id =? and (activity_id is null or activity_id=0)and task_date between ? and ? order by activity");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> alTaskIds = new ArrayList<String>();
			while(rs.next()) {
				if(!alTaskIds.contains(rs.getString("activity"))) {
					alTaskIds.add(rs.getString("activity"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select task_id, pro_id, activity_name, parent_task_id from activity_info ai where " +
				"resource_ids like '%,"+getStrEmpId()+",%' order by pro_id");
			rs = pst.executeQuery();
			Map<String, List<String>> hmTaskAndSubTaskIds = new LinkedHashMap<String, List<String>>();
			List<String> alSubTaskIds = new ArrayList<String>();
//			List<String> alTaskIds = new ArrayList<String>();
			while(rs.next()) {
				if(rs.getInt("parent_task_id") > 0) {
					alSubTaskIds = hmTaskAndSubTaskIds.get(rs.getString("parent_task_id"));
					if(alSubTaskIds == null) alSubTaskIds = new ArrayList<String>();
					alSubTaskIds.add(rs.getString("task_id"));
					hmTaskAndSubTaskIds.put(rs.getString("parent_task_id"), alSubTaskIds);
				}
				if(rs.getInt("parent_task_id") == 0) {
					alTaskIds.add(rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alTaskIds", alTaskIds);
			request.setAttribute("hmTaskAndSubTaskIds", hmTaskAndSubTaskIds);
			
//			System.out.println("alTaskIds ===>> " + alTaskIds);
//			System.out.println("hmTaskAndSubTaskIds ===>> " + hmTaskAndSubTaskIds);
			
			
			
			
			StringBuilder sbProIds = null;
			String otherProId = null;
			int proId = 0;
			for(int i = 0; getStrProject() != null && !getStrProject().isEmpty() && !getStrProject().contains("") && i<getStrProject().size(); i++) {
				if(sbProIds == null) {
					sbProIds = new StringBuilder();
					sbProIds.append(getStrProject().get(i));
				} else {
					sbProIds.append(","+getStrProject().get(i));
				}
				if(getStrProject().get(i) != null && !getStrProject().get(i).equals("-1")) {
					proId++;
				} else {
					otherProId = getStrProject().get(i);
				}
			}
			
//			System.out.println("=========>> 1 fillTaskRow getStrProject " + getStrProject());
//			System.out.println("=========>> 1 fillTaskRow otherProId " + otherProId);
			
//			System.out.println("otherProId ===>>> " + otherProId);
			if(getStrProject() != null ) {
//				System.out.println("getStrProject size ===>>> " + getStrProject().size());
//				System.out.println("getStrProject ===>>> " + getStrProject().toString());
			}
			
//			System.out.println("=========>> 1 getStrActivity " + getStrActivity());
//			System.out.println("=========>> 1 getStrActivityTaskId " + getStrActivityTaskId());
			Map hmProjectDates = new HashMap();
			Map hmProjectDatesNaTask = new HashMap();
			List alProDates = new ArrayList();
			List<String> alProDatesNaTask = new ArrayList<String>();

			Map<String, String> hmTaskProjectId = new HashMap<String, String>();
			Map<String, String> hmTaskActivityId = new HashMap<String, String>();
			Map<String, String> hmTaskClientId = new HashMap<String, String>();
			Map<String, String> hmTaskHoursId = new HashMap<String, String>();
			Map<String, String> hmTaskBillableHoursId = new HashMap<String, String>();
			Map<String, String> hmTaskDescription = new HashMap<String, String>();
			Map<String, String> hmTaskOnSite = new HashMap<String, String>();
			Map<String, String> hmTaskIsBill = new HashMap<String, String>();
			
		if (getActivityType() != null && getActivityType().equals("E")) {
//			System.out.println("getActivityType() ===>> " + getActivityType());
			
			if (uF.parseToInt(getStrActivity()) > 0) {
				// pst =
				// con.prepareStatement("select actual_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ? and ai.pro_id = ? and activity_id =?");
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select a.actual_hrs, a.is_billable, a.billable_hrs, a.pro_id, a.task_date, a.activity_name, a.task_id, a.activity_id, a.activity_description, a.task_location, client_id from (select actual_hrs,is_billable, billable_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, ta.activity_description, ta.task_location, ta.is_approved from task_activity ta left join activity_info ai on ta.activity_id = ai.task_id  and ta.emp_id = ?) a left join projectmntnc pcmc on a.pro_id = pcmc.pro_id where task_date between ? and ? and a.activity_id =? ");
				if(sbProIds != null && !sbProIds.toString().equals("")) {
					sbQuery.append(" and a.pro_id in ("+sbProIds.toString()+") ");
				}
				if(strUserType != null && (strUserType.equals(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY")) )) {
					sbQuery.append(" and a.is_approved = 0 ");
				}
//				pst = con.prepareStatement("select a.actual_hrs, a.is_billable, a.billable_hrs, a.pro_id, a.task_date, a.activity_name, a.task_id, a.activity_id, a.activity_description, a.task_location, client_id from (select actual_hrs,is_billable, billable_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, ta.activity_description, ta.task_location from task_activity ta left join activity_info ai on ta.activity_id = ai.task_id  and ta.emp_id = ?) a left join projectmntnc pcmc on a.pro_id = pcmc.pro_id where task_date between ? and ? and a.pro_id = ? and a.activity_id =? ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(getStrActivity()));
//				System.out.println("pst uF.parseToInt(getStrActivity()) > 0 == 0 ===>>> " + pst);
				
			} else if (getStrProject() != null && getStrProject().size() > 0 && !getStrProject().contains("")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select actual_hrs,ta.is_billable, billable_hrs, activity_id, ai.pro_id, task_date, activity_name, ta.task_id, ta.activity_description, ta.task_location, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ? and ta.emp_id = ? ");
				if(sbProIds != null && !sbProIds.toString().equals("")) {
					sbQuery.append(" and ai.pro_id in ("+sbProIds.toString()+") ");
				}
				if(strUserType != null && (strUserType.equals(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY")) )) {
					sbQuery.append(" and is_approved = 0 ");
				}
//				pst = con.prepareStatement("select actual_hrs,is_billable, billable_hrs, activity_id, ai.pro_id, task_date, activity_name, ta.task_id, ta.activity_description, ta.task_location, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ? and ai.pro_id = ? and ta.emp_id = ?");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst getStrProject() != null && getStrProject().size() > 0 == 0 ===>>> " + pst);
				
			} else if (uF.parseToInt(getStrActivity()) == 0 && uF.parseToInt(getStrActivityTaskId()) > 0) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from task_activity where (activity_id = 0 or (activity_id > 0 and activity is not null and activity != '')) and task_date between ? and ? and emp_id = ? and activity =?"); // and task_id = ? 
				if(strUserType != null && (strUserType.equals(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY")) )) {
					sbQuery.append(" and is_approved = 0 ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrEmpId()));
				pst.setString(4, getStrActivity());
//				pst.setInt(4, uF.parseToInt(getStrActivityTaskId()));
//				System.out.println("pst uF.parseToInt(getStrActivity()) == 0 && uF.parseToInt(getStrActivityTaskId()) > 0 ===>>> " + pst);
			} else if ((getStrProject() == null || getStrProject().size() == 0) && uF.parseToInt(getStrActivity()) == 0) {
//				pst = con.prepareStatement("select * from task_activity where activity_id = 0 and task_date between ? and ? and emp_id = ?");
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select actual_hrs, ta.is_billable, billable_hrs, activity_id, ai.pro_id, task_date, activity_name, activity, ta.task_id, ta.activity_description, ta.task_location, activity_id from task_activity ta left join activity_info ai on  ta.activity_id = ai.task_id where task_date between ? and ? and ai.resource_ids like '%,"+getStrEmpId()+",%' ");
				if(strUserType != null && (strUserType.equals(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY")) )) {
					sbQuery.append(" and is_approved = 0 ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst (getStrProject() == null || getStrProject().size() == 0) && uF.parseToInt(getStrActivity()) == 0 ===>>> " + pst);
				
			} else if (otherProId != null && otherProId.equals("-1")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from task_activity where (activity_id = 0 or (activity_id > 0 and activity is not null and activity != '')) and task_date between ? and ? and emp_id = ? ");
				if(strUserType != null && (strUserType.equals(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY")) )) {
					sbQuery.append(" and is_approved = 0 ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst otherProId != null && otherProId.equals -1 ===>>> " + pst);
				
			} else if (uF.parseToInt(getStrActivity()) < 0) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from task_activity where (activity_id = 0 or (activity_id > 0 and activity is not null and activity != '')) and task_date between ? and ? and emp_id = ? ");
				if(strUserType != null && (strUserType.equals(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY")) )) {
					sbQuery.append(" and is_approved = 0 ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst uF.parseToInt(getStrActivity()) < 0 ===>>> " + pst);
			} else {
				// pst =
				// con.prepareStatement("select actual_hrs,activity_id, ai.pro_id, task_date, activity_name , ta.task_id, activity_id, pcmc.client_id from task_activity ta, activity_info ai, projectmntnc pcmc where  ta.activity_id = ai.task_id and pcmc.pro_id = ai.pro_id and task_date between ? and ?");
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select actual_hrs, ta.is_billable, billable_hrs, activity_id, ai.pro_id, task_date, activity_name, activity, ta.task_id, ta.activity_description, ta.task_location, activity_id from task_activity ta left join activity_info ai on  ta.activity_id = ai.task_id where task_date between ? and ? and ai.resource_ids like '%,"+getStrEmpId()+",%' and ai.pro_id is null ");
				if(strUserType != null && (strUserType.equals(EMPLOYEE) || (getFillUserType() != null && getFillUserType().equals("MY")) )) {
					sbQuery.append(" and is_approved = 0 ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst else ===>>> " + pst);
			}
//			System.out.println("pst out ===>>> " + pst);
			rs = pst.executeQuery();

			String strDateNew = null;
			String strDateOld = null;
			while (rs.next()) {
				strDateNew = uF.getDateFormat(rs.getString("task_date"),DBDATE, DATE_FORMAT);
//				System.out.println("strDateNew ===>> " + strDateNew);
				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alProDates = new ArrayList();
					alProDatesNaTask = new ArrayList<String>();
				}

				alProDates = (List) hmProjectDates.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT));
				if (alProDates == null)
					alProDates = new ArrayList();
				if(!alProDates.contains(rs.getString("task_id"))) {
					alProDates.add(rs.getString("task_id"));
				}
//				System.out.println("alProDates ===>> " + alProDates);
				
				hmProjectDates.put(uF.getDateFormat(rs.getString("task_date"),DBDATE, DATE_FORMAT), alProDates);
//				System.out.println("hmProjectDates ===>> " + hmProjectDates);
				
				alProDatesNaTask = (List<String>) hmProjectDatesNaTask.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT));
				if (alProDatesNaTask == null)
					alProDatesNaTask = new ArrayList<String>();
//				System.out.println("alProDatesNaTask before ===>>> " + alProDatesNaTask);
				if(!alProDatesNaTask.contains(rs.getString("task_id"))) {
					alProDatesNaTask.add(rs.getString("task_id"));
				}
//				System.out.println("alProDatesNaTask after ===>>> " + alProDatesNaTask);
				hmProjectDatesNaTask.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT),alProDatesNaTask);

				strDateOld = strDateNew;

				if (proId > 0 || uF.parseToInt(getStrActivity()) > 0) {
					hmTaskProjectId.put(rs.getString("task_id"),rs.getString("pro_id"));
					hmTaskClientId.put(rs.getString("task_id"), rs.getString("client_id"));
					
				}

//				System.out.println("proId ===>> " + proId);
				if (proId > 0 || uF.parseToInt(rs.getString("activity_id")) > 0) {
					hmTaskActivityId.put(rs.getString("task_id"), rs.getString("activity_id"));
				} else {
					hmTaskActivityId.put(rs.getString("task_id"), rs.getString("activity"));
				}

				hmTaskHoursId.put(rs.getString("task_id"), uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
				hmTaskBillableHoursId.put(rs.getString("task_id"), uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
				hmTaskDescription.put(rs.getString("task_id"),rs.getString("activity_description"));

				hmTaskOnSite.put(rs.getString("task_id"),rs.getString("task_location"));
				hmTaskIsBill.put(rs.getString("task_id"),rs.getString("is_billable"));
			}
			rs.close();
			pst.close();
			
			
			if (otherProId != null && otherProId.equals("-1")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from task_activity where (activity_id = 0 or (activity_id > 0 and activity is not null and activity != '')) and task_date between ? and ? and emp_id = ? ");
				if(strUserType != null && strUserType.equals(EMPLOYEE)) {
					sbQuery.append(" and is_approved = 0 ");
				}
				pst = con.prepareStatement("");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrEmpId()));
				rs = pst.executeQuery();

				while (rs.next()) {
					strDateNew = uF.getDateFormat(rs.getString("task_date"),DBDATE, DATE_FORMAT);
					if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
						alProDates = new ArrayList();
						alProDatesNaTask = new ArrayList();
					}
	
					alProDates = (List) hmProjectDates.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT));
					if (alProDates == null)
						alProDates = new ArrayList();
					if(!alProDates.contains(rs.getString("task_id"))) {
						alProDates.add(rs.getString("task_id"));
					}
					hmProjectDates.put(uF.getDateFormat(rs.getString("task_date"),DBDATE, DATE_FORMAT), alProDates);
	 
					alProDatesNaTask = (List) hmProjectDatesNaTask.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT));
					if (alProDatesNaTask == null)
						alProDatesNaTask = new ArrayList();
					if(!alProDatesNaTask.contains(rs.getString("task_id"))) {
						alProDatesNaTask.add(rs.getString("task_id"));
					}
					hmProjectDatesNaTask.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT),alProDatesNaTask);
	
					strDateOld = strDateNew;
	
					if (uF.parseToInt(rs.getString("activity_id")) > 0 || uF.parseToInt(getStrActivity()) > 0) {
	//					hmTaskProjectId.put(rs.getString("task_id"),rs.getString("pro_id"));
					}
	
					if (uF.parseToInt(rs.getString("activity_id")) > 0) {
	//					hmTaskClientId.put(rs.getString("task_id"),rs.getString("client_id"));
						hmTaskActivityId.put(rs.getString("task_id"),rs.getString("activity_id"));
					} else {
						hmTaskActivityId.put(rs.getString("task_id"),rs.getString("activity"));
					}
	
					hmTaskHoursId.put(rs.getString("task_id"), uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
					hmTaskBillableHoursId.put(rs.getString("task_id"), uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
					hmTaskDescription.put(rs.getString("task_id"),rs.getString("activity_description"));
	
					hmTaskOnSite.put(rs.getString("task_id"),rs.getString("task_location"));
					hmTaskIsBill.put(rs.getString("task_id"),rs.getString("is_billable"));
				}
				rs.close();
				pst.close();
			}
		}
		
		
			StringBuilder sbTasks = new StringBuilder();

			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "yyyy")));

			boolean taskBillableFlag = false;
			StringBuilder sbTaskList = new StringBuilder();
			for (int i = 0; i < tasklist.size(); i++) {
				if(i==0){
					pst = con.prepareStatement("select is_billable_task from activity_info ai where task_id=?");
					pst.setInt(1, uF.parseToInt(tasklist.get(i).getTaskId()));
					rs = pst.executeQuery();
					while(rs.next()){
						if(uF.parseToBoolean(rs.getString("is_billable_task"))){
							taskBillableFlag = true;
						}
					}
					rs.close();
					pst.close();
				}
				sbTaskList.append("<option value=\'"+tasklist.get(i).getTaskId()+"\'>"+tasklist.get(i).getTaskName()+"</option>");
			}
			request.setAttribute("strTaskList", sbTaskList.toString());
			request.setAttribute("taskBillableFlag", taskBillableFlag+"");
//			System.out.println("sbTaskList.toString() ===>> " + sbTaskList.toString());
			int i=0;
			int xyz=0;
//			System.out.println("nDateCount======>"+nDateCount);
//			System.out.println("joiningDate======>"+joiningDate);
			
			List<String> approvedDates = getTimesheetApprovedDates(con, uF, getFrmDate(), getToDate(), getStrEmpId());
			
			StringBuilder sbApproveDates = null;
			for(int ii=0; approvedDates!= null && !approvedDates.isEmpty() && ii<approvedDates.size(); ii++) {
				if(sbApproveDates == null) {
					sbApproveDates = new StringBuilder();
					sbApproveDates.append(approvedDates.get(ii));
				} else {
					sbApproveDates.append(", "+approvedDates.get(ii));
				}
			}
			
			if(approvedDates.size()>0 && getSubmit1() != null && nDateCount>0) {
				sbTasks.append("" + "<div id=\"approvedDateDIV\"><div id=\"row_task_00\" style=\"float:left; width:750px; padding-bottom:10px; color:red;\">");
				sbTasks.append("The following dates have already been approved. Dates: " + sbApproveDates.toString());
				sbTasks.append("</div>" + "</div>");
			}
			
//			System.out.println("getSubmit1 ======>> " + getSubmit1());
			for (i = 0; getSubmit1() != null && i < nDateCount; i++) {
				String taskDate=uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT);
				
//				System.out.println("taskDate ======> " + taskDate);
//				System.out.println("joiningDate ======> " + joiningDate);
				
				if(uF.getDateFormat(taskDate,DATE_FORMAT).compareTo(uF.getDateFormat(joiningDate,DATE_FORMAT))>=0) {
				List<String> alTaskId = (List) hmProjectDates.get(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT));

				Set<String> weeklyOffSet= hmWeekendMap.get(strWLocationId);
				if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(getStrEmpId());
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				if (alTaskId == null) {
					alTaskId = (List) hmProjectDatesNaTask.get(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1)+ "/" + cal.get(Calendar.YEAR),DATE_FORMAT, DATE_FORMAT));
				}

				if (alTaskId == null) {
					alTaskId = new ArrayList<String>();
					alTaskId.add("0");
				}
				
//				System.out.println("hmTaskActivityId ========>> " + hmTaskActivityId);
//				System.out.println("tasklist.size() ========>> " + tasklist.size());
//				System.out.println("alTaskId ========>> " + alTaskId);
				
					for (int j = 0; j < alTaskId.size() && tasklist.size() > 0; j++) {
//						System.out.println("alTaskId.get(j) ========>> " + alTaskId.get(j));
						String tdate = uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT);
	//					String leavecode = hmEmpLeave.get(tdate);
						/*
						 * if(leavecode!=null) { continue; }
						 */
						
						if(approvedDates.contains(tdate)) {
							
						} else {
						double dblVal = uF.parseToDouble(hmTaskHoursId.get(alTaskId.get(j)));
						double dblBillableVal = uF.parseToDouble(hmTaskBillableHoursId.get(alTaskId.get(j)));
						// double dblVal =
						// uF.parseToDouble(hmTotalTime.get(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR),
						// DATE_FORMAT, DATE_FORMAT)));
						if (dblVal == 0) {
							dblVal = uF.parseToDouble(hmAttendance.get(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT)));
						}
						if (dblBillableVal == 0) {
							dblBillableVal = uF.parseToDouble(hmAttendance.get(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT)));
						}
	//					System.out.println("date "+uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1)+ "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
						sbTasks.append("" + "<div id=\"" + uF.getDateFormat( cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" 
								+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)+"_"+j+"\">" +
								"<div id=\"row_task_"+i+"_"+j+"\" style=\"float:left;width:750px;padding-bottom:10px;\">");
						
	//	******************************** task date textfield ********************************************
						sbTasks.append("<div style=\"float:left;width:90px;\"><input type=\"hidden\" name=\"taskId\" value=\"" + alTaskId.get(j)
								+ "\"><input type=\"text\" style=\"width:82px !important;\" name=\"strDate\" id=\"strDate_"+i+"_"+j+"\" value=\"" 
								+ uF.getDateFormat( cal.get(Calendar.DATE) + "/"
								+ (cal.get(Calendar.MONTH) + 1) 
								+ "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)
								+ "\"></div>");
		
	//	******************************** task list select box ********************************************				
						sbTasks.append("<div style=\"float:left;width:225px;\">"
								+ "<select name=\"strTask\" class=\"validateRequired\" onchange=\"checkIsBillable(this.value, '"+i+"_"+j+"', '"+i+"_"+j+"');\">");
						boolean billableFlag = false;
						for (int c = 0; c < tasklist.size(); c++) {
							if(c == 0) {
								billableFlag = checkTaskIsBillable(con, uF, tasklist.get(0).getTaskId());
							}
							sbTasks.append("<option value=\'" + tasklist.get(c).getTaskId() + "\' "
									+ ((((String) tasklist.get(c).getTaskId()).equalsIgnoreCase(hmTaskActivityId.get(alTaskId.get(j)))) ? "selected": "") + ">"
									+ tasklist.get(c).getTaskName() + "</option>");
						}
						sbTasks.append("</select>" + "</div>");
						
	//	******************************** total hrs textfield ********************************************
				//===start parvez date: 16-06-2022===		
//						sbTasks.append("<div style=\"float:left;width:120px;\"><input onblur=\"checkHours('"+i+"_"+j+"');\" type=\"text\" style=\"width:62px !important;\" name=\"strTime\" id=\"strTime_"+i+"_"+j+"\" value=\""
//								+ uF.formatIntoTwoDecimal(dblVal) + "\" onkeypress=\"return isNumberKey(event)\" onkeyup=\"checkAndAddBillableTime('"+i+"_"+j+"','"+i+"_"+j+"');\">");
						List alLeaveList = (List)hmAppliedLeaves.get(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT));
//						System.out.println("APA1/5800--alLeaveList="+alLeaveList);
						if(hmAppliedLeaves.containsKey(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT))
								&& !uF.parseToBoolean(alLeaveList.get(1)+"")){
							sbTasks.append("<div style=\"float:left;width:120px;\"><input onblur=\"checkHours('"+i+"_"+j+"');\" type=\"text\" style=\"width:62px !important;\" name=\"strTime\" id=\"strTime_"+i+"_"+j+"\" value=\""
									+ uF.formatIntoTwoDecimal(dblVal) + "\" onkeypress=\"return isNumberKey(event)\" onkeyup=\"checkAndAddBillableTime('"+i+"_"+j+"','"+i+"_"+j+"');\" readonly>");
						} else{
							sbTasks.append("<div style=\"float:left;width:120px;\"><input onblur=\"checkHours('"+i+"_"+j+"');\" type=\"text\" style=\"width:62px !important;\" name=\"strTime\" id=\"strTime_"+i+"_"+j+"\" value=\""
									+ uF.formatIntoTwoDecimal(dblVal) + "\" onkeypress=\"return isNumberKey(event)\" onkeyup=\"checkAndAddBillableTime('"+i+"_"+j+"','"+i+"_"+j+"');\">");
						}
				//===end parvez date: 16-06-2022===		
						
						// "<div style=\"float:left;width:150px;\"><input onblur=\"checkHours();\" type=\"text\" style=\"width:62px\" name=\"strTime\" value=\""+uF.showData((String)hmAttendance.get(uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR),
						// DATE_FORMAT, DATE_FORMAT)),
						// uF.formatIntoTwoDecimal(dblVal))+"\">");
	
						if (hmHolidayDates.containsKey(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,CF.getStrReportDateFormat())+ "_" + (String) session.getAttribute(WLOCATIONID))) {
							sbTasks.append("<input type=\"hidden\" name=\"holiday\" value=\"H\"><span style=\"padding-left:10px\">H</span>");
						}
	//					if (hmWeekendMap.containsKey(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT)+ "_" + (String) session.getAttribute(WLOCATIONID))) {
	//						sbTasks.append("<input type=\"hidden\" name=\"weekend\" value=\"W/O\"><span style=\"padding-left:10px\">W/O</span>");
	//					}
						
						if(alEmpCheckRosterWeektype.contains(getStrEmpId())) {
							if(rosterWeeklyOffSet.contains(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT))) {
								sbTasks.append("<input type=\"hidden\" name=\"weekend\" value=\"W/O\"><span style=\"padding-left:10px\">W/O</span>");
							}
						} else if(weeklyOffSet.contains(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT))) {
							sbTasks.append("<input type=\"hidden\" name=\"weekend\" value=\"W/O\"><span style=\"padding-left:10px\">W/O</span>");
						}
						
				//===start parvez date: 16-06-2022===		
						if(hmAppliedLeaves.containsKey(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT))){
							if(uF.parseToBoolean(alLeaveList.get(1)+"")){
								sbTasks.append("<span style=\"padding-left:10px\">"+"HD/"+hmAppliedLeaveCode.get(alLeaveList.get(0)+"")+"</span>");
							} else{
								sbTasks.append("<span style=\"padding-left:10px\">"+hmAppliedLeaveCode.get(alLeaveList.get(0)+"")+"</span>");
							}
							
						}
				//===end parvez date: 16-06-2022===		
						sbTasks.append("</div>");
						
	// ******************************** billable true false checkbox ********************************************
						String strBillableChecked = "";
						String strBillableDisabled = "disabled=disabled";
						if(billableFlag) {
							strBillableChecked = "checked";
							strBillableDisabled = "";
						}
						sbTasks.append(((uF.parseToInt(alTaskId.get(j)) > 0) ?
								"<div style=\"float: left; width: 55px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\" id=\"strBillableYesNoT_"+i+"_"+j+"\" name=\"strBillableYesNoT\" " +
								 ((uF.parseToBoolean(hmTaskIsBill.get(alTaskId.get(j))) && billableFlag) ? "value=\"1\"": "value=\"0\"" ) + "" + ">" +
								"<span id=\"isBillableSapn"+i+"_"+j+"\"><input type=\"checkbox\" style=\"width: 30px;\"  onchange=\"setBillableValue('"+i+"_"+j+"','"+i+"_"+j+"')\" id=\"strBillableYesNo_"+i+"_"+j+"\" name=\"strBillableYesNo\" value=\""
								+ alTaskId.get(j) + "\" " + ((uF.parseToBoolean(hmTaskIsBill.get(alTaskId.get(j))) && billableFlag) ? "checked" : "") + "></span></div>"
								: "<div style=\"float: left; width: 55px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\" id=\"strBillableYesNoT_"+i+"_"+j+"\" name=\"strBillableYesNoT\" value=\""+((billableFlag) ? "1" : "0")+"\">" +
								"<span id=\"isBillableSapn"+i+"_"+j+"\"><input type=\"checkbox\" style=\"width: 30px;\" id=\"strBillableYesNo_"+i+"_"+j+"\" onchange=\"setBillableValue('"+i+"_"+j+"','"+i+"_"+j+"')\" name=\"strBillableYesNo\" value=\""
								+ alTaskId.get(j) + "\"  "+strBillableChecked+" "+strBillableDisabled+" ></span></div>"));
						
	//	******************************** bill hrs textfield ********************************************
				//===start parvez date: 16-06-2022===		
//						sbTasks.append("<div style=\"float:left;width:85px;\"><input onkeypress=\"return isNumberKey(event)\" onkeyup=\"checkBillHours('"+i+"_"+j+"');\" type=\"text\" style=\"width:62px !important;\" name=\"strBillableTime\" id=\"strBillableTime_"+i+"_"+j+"\" value=\""
//								+((uF.parseToBoolean(hmTaskIsBill.get(alTaskId.get(j)))) ? uF.formatIntoTwoDecimal(dblBillableVal) : "0") +  "\"></div>");
//						if(hmAppliedLeaves.containsKey(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT))){
						if(hmAppliedLeaves.containsKey(uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR), DATE_FORMAT,DATE_FORMAT))
								&& !uF.parseToBoolean(alLeaveList.get(1)+"")){
							sbTasks.append("<div style=\"float:left;width:85px;\"><input onkeypress=\"return isNumberKey(event)\" onkeyup=\"checkBillHours('"+i+"_"+j+"');\" type=\"text\" style=\"width:62px !important;\" name=\"strBillableTime\" id=\"strBillableTime_"+i+"_"+j+"\" value=\""
									+((uF.parseToBoolean(hmTaskIsBill.get(alTaskId.get(j)))) ? uF.formatIntoTwoDecimal(dblBillableVal) : "0") +  "\" readonly></div>");
						} else{
							sbTasks.append("<div style=\"float:left;width:85px;\"><input onkeypress=\"return isNumberKey(event)\" onkeyup=\"checkBillHours('"+i+"_"+j+"');\" type=\"text\" style=\"width:62px !important;\" name=\"strBillableTime\" id=\"strBillableTime_"+i+"_"+j+"\" value=\""
									+((uF.parseToBoolean(hmTaskIsBill.get(alTaskId.get(j)))) ? uF.formatIntoTwoDecimal(dblBillableVal) : "0") +  "\"></div>");
						}
				//===end parvez date: 16-06-2022===		
						
	//	******************************** onsite true false checkbox ********************************************
						if(!uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_ON_SITE_IN_TIMESHEET))){
							System.out.println("APA1/5990----");
							sbTasks.append(((uF.parseToInt(alTaskId.get(j)) > 0) ?
									"<div style=\"float: left; width: 50px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\" id=\"strTaskOnOffSiteT_"+i+"_"+j+"\" name=\"strTaskOnOffSiteT\" " +
									 (("ONS".equalsIgnoreCase(hmTaskOnSite.get(alTaskId.get(j)))) ? "value=\"1\"": "value=\"0\"" ) + "" + ">" +
									"<input type=\"checkbox\" style=\"width: 30px;\"  onchange=\"setValue('"+i+"_"+j+"')\" id=\"strTaskOnOffSite_"+i+"_"+j+"\" name=\"strTaskOnOffSite\" value=\""
									+ alTaskId.get(j) + "\" " + (("ONS".equalsIgnoreCase(hmTaskOnSite.get(alTaskId.get(j)))) ? "checked" : "") + "></div>"
									: "<div style=\"float: left; width: 50px; text-align: center;\"><input type=\"hidden\" style=\"width: 30px;\" id=\"strTaskOnOffSiteT_"+i+"_"+j+"\" name=\"strTaskOnOffSiteT\" value=\"1\">" +
									"<input type=\"checkbox\" style=\"width: 30px;\" id=\"strTaskOnOffSite_"+i+"_"+j+"\" onchange=\"setValue('"+i+"_"+j+"')\" name=\"strTaskOnOffSite\" value=\""
									+ alTaskId.get(j) + "\" checked></div>"));
						}
						
	//	***************************** Add remove new task buttons *******************************************
						if(uF.parseToBoolean(hmFeatureStatus.get(F_DISABLE_ON_SITE_IN_TIMESHEET))){
							sbTasks.append("<div style=\"float:left;\"><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addTask1('"
									+ uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1)+ "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)
									+ "_" + j + "')\" title=\"Add New Task\"></a></div>"
									+ "<div style=\"float:left;\">"
									+"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+request.getContextPath()+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Task\" onclick=\"removeTask('"
									+ uF.getDateFormat( cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)
									+ "_"+j+"','row_task_"+i+"_"+j+"','" + alTaskId.get(j) + "');\"/></div>");
						} else{
							sbTasks.append("<div style=\"float:left;\"><a href=\"javascript:void(0)\" class=\"add_lvl\" onclick=\"addTask('"
									+ uF.getDateFormat(cal.get(Calendar.DATE) + "/"+ (cal.get(Calendar.MONTH) + 1)+ "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)
									+ "_" + j + "')\" title=\"Add New Task\"></a></div>"
									+ "<div style=\"float:left;\">"
									+"<img border=\"0\" style=\"height: 16px; width: 16px;\" src=\""+request.getContextPath()+"/images1/icons/icons/close_button_icon.png\" title=\"Remove Task\" onclick=\"removeTask('"
									+ uF.getDateFormat( cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT)
									+ "_"+j+"','row_task_"+i+"_"+j+"','" + alTaskId.get(j) + "');\"/></div>");
						}
						
//								System.out.println("alTaskId.get(j) ===>> " + alTaskId.get(j));
								
	//	***************************** Add description buttons *******************************************				
						sbTasks.append("<div style=\"float:left; width:20px; padding-left:10px;\"><input type=\"hidden\" id=\"hidetaskDescription_"+i+"_"+j+"\" name=\"hidetaskDescription\" value=\"0\">"
								+"<a href=\"javascript: void(0);\" onclick=\"showHideDescription('"+i+"_"+j+"');\">"+
								"<span id=\"PdownarrowSpan_"+i+"_"+j+"\" style=\"float: left; margin-right: 3px;\"><i class=\"fa fa-angle-down\" style=\"font-weight: bold;\" aria-hidden=\"true\" title=\"Click here to add task description\"/></i></span>"+
								"<span id=\"PuparrowSpan_"+i+"_"+j+"\" style=\"display: none; float: left; margin-right: 3px;\"><i class=\"fa fa-angle-up\" style=\"font-weight: bold;\" aria-hidden=\"true\" title=\"Click here to hide task description\"/></i></span>"+
								"</a></div>");
						
	//	***************************** Add description textarea *******************************************				
						sbTasks.append("<div id=\"taskDescriptionDIV_"+i+"_"+j+"\" style=\"float: left; width: 100%; display: none;\">"
								+ "<div style=\"float: left; padding: 10px 0pt;\"><textarea name=\"taskDescription\" style=\"width: 75% !important;\" rows=\"2\" cols=\"100\" class=\"validateRequired\" placeholder=\"Add task description\">"
								+ uF.showData(hmTaskDescription.get(alTaskId.get(j)), "")
								+ "</textarea></div>" 
								+ "</div>");
	
						sbTasks.append("</div>" + "</div>");
					}
				}
				}
				cal.add(Calendar.DATE, 1);
				xyz++;
			}

			StringBuilder sbNoTasks = new StringBuilder(); 
			if (tasklist.size() == 0) {
				
				sbNoTasks.append("<div style=\"float: left; width: 700px; padding: 2px;\">"
						+ "<div class=\"msg nodata\"><span>No project selected</span></div>"
						+ "</div>");

			}
			
			
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			
//			System.out.println("sbTasks ========>> " + sbTasks.toString());
			
			request.setAttribute("currDate", currDate);
			
			request.setAttribute("sbNoTasks", sbNoTasks.toString());
			request.setAttribute("sbTasks", sbTasks.toString());
			request.setAttribute("hmClientMap", hmClientMap);
			request.setAttribute("hmProjectMap", hmProjectMap);
			
			request.setAttribute("i", i);
//			request.setAttribute("i", xyz);

			int nDateDiff = uF.parseToInt(uF.dateDifference(strPayCycleDates[0], DATE_FORMAT, strPayCycleDates[1],DATE_FORMAT,CF.getStrTimeZone()));

			cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, "yyyy")));
			List<String> alDates = new ArrayList<String>();
			for (i = 0; i < 32; i++) {
				alDates.add(uF.getDateFormat(cal.get(Calendar.DATE) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);

				if (uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT).equalsIgnoreCase(strPayCycleDates[1])) {
					alDates.add(uF.getDateFormat( cal.get(Calendar.DATE) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
					break;
				}
			}

			
			
			StringBuilder sbMultiProTaskQuery = new StringBuilder();
//			sbMultiProTaskQuery.append("select task_id, activity_name, completed from activity_info where emp_id = ? ");
			sbMultiProTaskQuery.append("select task_id,activity_name,parent_task_id,completed from activity_info where resource_ids like '%,"+getStrEmpId()+",%' and task_id not in (" +
					"select parent_task_id from activity_info where resource_ids like '%,"+getStrEmpId()+",%') and task_accept_status = 1 and (parent_task_id in (select task_id from activity_info " +
					"where resource_ids like '%,"+getStrEmpId()+",%') or parent_task_id = 0)");
			if(sbProIds != null && !sbProIds.toString().equals("")) {
				sbMultiProTaskQuery.append(" and pro_id in ("+sbProIds.toString()+") ");
			}
//			pst = con.prepareStatement("select task_id, activity_name, completed from activity_info where pro_id = ? and emp_id = ?");
			pst = con.prepareStatement(sbMultiProTaskQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();

			StringBuilder sbTaskStatus = new StringBuilder();
			sbTaskStatus.append("<div style=\"float:left;\">Task Completion Status</div>");
//			***************************** Show hide Tasks buttons *******************************************				
			sbTaskStatus.append("<div style=\"float:left;width:20px; padding-left: 10px;\"><input type=\"hidden\" id=\"hidetaskTasks\" name=\"hidetaskDescription\" value=\"0\">"
					+"<a href=\"javascript: void(0);\" onclick=\"showHideTasks();\">"+
					"<span id=\"PdownarrowSpan_Tasks\" style=\"float: left; margin-right: 3px;\"><i class=\"fa fa-angle-down\" aria-hidden=\"true\" title=\"Click here to show task\"/></i></span>"+
					"<span id=\"PuparrowSpan_Tasks\" style=\"display: none; float: left; margin-right: 3px;\"><i class=\"fa fa-angle-up\" aria-hidden=\"true\" title=\"Click here to hide task\"/></i></span>"+
					"</a></div>");
			sbTaskStatus.append("<div id=\"AllTasksDiv\" style=\"display: none; float: left; width: 750px; padding: 2px;\">");
			while (rs.next()) {
//				sbTaskStatus.append("Task Completion Status");
				sbTaskStatus.append("<div  id=\"taskCompletionDiv_"+rs.getString("task_id")+"\"style=\"float: left; width: 100%; padding: 2px;\">");
				
				sbTaskStatus.append("<div  id=\"taskNameDiv_"+rs.getString("task_id")+"\"style=\"float: left; width: 45%; padding: 2px;\">");
				sbTaskStatus.append(rs.getString("activity_name"));
				if(uF.parseToInt(rs.getString("parent_task_id")) > 0) {
					sbTaskStatus.append(" [ST]");
				}
				sbTaskStatus.append(":");
				sbTaskStatus.append("</div>");
				
				sbTaskStatus.append("<div  id=\"taskStatusDiv_"+rs.getString("task_id")+"\"style=\"float: left; padding: 2px;\">");
				sbTaskStatus.append("<input type=\"hidden\" name=\"statusTaskId\" value=\""
					+ rs.getString("task_id") + "\"><input type=\"text\" name=\"status\" value=\""
					+ uF.showData(rs.getString("completed"), "0") + "\" style=\"width: 62px !important;margin:0 10px;text-align:right;\">%");
				sbTaskStatus.append("</div>");
				sbTaskStatus.append("</div>");
			}
			rs.close();
			pst.close();
			sbTaskStatus.append("</div>");
			
			pst = con.prepareStatement("select leave_type_id from emp_leave_type where is_compensatory=false and is_constant_balance=false " +
					"and leave_type_id in (select leave_type_id from leave_type) group by leave_type_id order by leave_type_id");
			rs = pst.executeQuery();
			Map<String, String> hmCheckLeaveType=new HashMap<String, String>();
			while(rs.next()) {
				hmCheckLeaveType.put(rs.getString("leave_type_id"), rs.getString("leave_type_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmCheckLeaveType", hmCheckLeaveType);
			request.setAttribute("hmLeaves", hmLeaves);
			request.setAttribute("hmWeekendMap", hmWeekendMap);
			request.setAttribute("strWLocationId", strWLocationId);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmLeavesColour", hmLeavesColour);
			request.setAttribute("hmLeaveConstant", hmLeaveConstant);

			request.setAttribute("timesheet_title", "Timesheet details from " + strPayCycleDates[0] + " to " + strPayCycleDates[1]);
			request.setAttribute("alDates", alDates);

			
			
			request.setAttribute("strEmpId", getStrEmpId());

			request.setAttribute("sbTaskStatus", sbTaskStatus);
			request.setAttribute("hmLeaveCode", hmLeaveCode);
			
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
	private boolean checkTaskIsBillable(Connection con, UtilityFunctions uF, String taskId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
			pst = con.prepareStatement("select is_billable_task from activity_info where task_id=?");
			pst.setInt(1, uF.parseToInt(taskId));
			// System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getBoolean("is_billable_task")) {
					flag = true;
				} else {
					flag = false;
				}
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	private List<String> getTimesheetApprovedDates(Connection con, UtilityFunctions uF, String strDate1, String strDate2, String strEmpId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> approvedDateList = new ArrayList<String>();
		try {
			pst=con.prepareStatement("select task_date from task_activity where task_date between ? and ? and emp_id = ? and is_approved = 2 group by task_date order by task_date");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				approvedDateList.add(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return approvedDateList;
	}
	
	
	private List<String> getTimesheetFilledDates(Connection con, UtilityFunctions uF, String strDate1, String strDate2, String strEmpId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> filledDateList = new ArrayList<String>();
		try {
			pst=con.prepareStatement("select task_date from task_activity where task_date between ? and ? and emp_id = ? group by task_date order by task_date");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				filledDateList.add(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		return filledDateList;
	}

	
	
	private Map getLeaveDetails(String strDate1,String strDate2,UtilityFunctions uF,Map hmLeavesCode) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,Map<String,String>> getMap=new HashMap<String,Map<String,String>>();
		try{
			con=db.makeConnection(con);
			pst=con.prepareStatement("select lar.*,lt.leave_type_code from leave_application_register lar,leave_type lt where lar.leave_type_id=lt.leave_type_id and is_modify = false and _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				Map<String,String> a = getMap.get(rs.getString("emp_id"));
				if(a == null) a = new HashMap<String,String>(); 
				
//				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_code"));
				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_id"));
				getMap.put(rs.getString("emp_id"), a);
				hmLeavesCode.put(rs.getString("leave_type_id"), rs.getString("leave_type_code"));
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
		return getMap;
	}
	
//===Created parvez date: 17-06-2022===	
	//===start===
	private Map getAppliedLeaveDetails(String strDate1,String strDate2,UtilityFunctions uF,Map hmAppliedLeavesCode) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,Map<String,List<String>>> getMap=new HashMap<String,Map<String,List<String>>>();
		try{
			con=db.makeConnection(con);
			
			pst=con.prepareStatement("select ele.*,lt.leave_type_code from emp_leave_entry ele,leave_type lt where ele.leave_type_id=lt.leave_type_id and is_modify = false " +
					" and (leave_from between ? and ? or leave_to between ? and ?) order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strDate2, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				Map<String,List<String>> a = getMap.get(rs.getString("emp_id"));
				if(a == null) a = new HashMap<String,List<String>>(); 
				String strDateDifference = uF.dateDifference(rs.getString("leave_from"),DBDATE, rs.getString("leave_to"),DBDATE);
//				System.out.println("strDateDifference="+strDateDifference);
				for(int i=0; i<uF.parseToInt(strDateDifference);i++){
//					System.out.println("dates=="+uF.getDateFormatUtil(uF.getNextDate(uF.getDateFormatUtil(rs.getString("leave_from"), DBDATE), i), DATE_FORMAT));
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("leave_type_id"));		//0
					innerList.add(rs.getString("ishalfday"));			//1
//					a.put(uF.getDateFormatUtil(uF.getNextDate(uF.getDateFormatUtil(rs.getString("leave_from"), DBDATE), i), DATE_FORMAT)+"", rs.getString("leave_type_id"));
					a.put(uF.getDateFormatUtil(uF.getNextDate(uF.getDateFormatUtil(rs.getString("leave_from"), DBDATE), i), DATE_FORMAT)+"", innerList);
					
					/*if(uF.parseToBoolean(rs.getString("ishalfday"))){
						hmAppliedLeavesCode.put(rs.getString("leave_type_id"), "HD/"+rs.getString("leave_type_code"));
					} else {
						hmAppliedLeavesCode.put(rs.getString("leave_type_id"), rs.getString("leave_type_code"));
					}*/
				}
				hmAppliedLeavesCode.put(rs.getString("leave_type_id"), rs.getString("leave_type_code"));
//				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_id"));
				getMap.put(rs.getString("emp_id"), a);
				
				
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
		return getMap;
	}
//===end===
	
	public void getData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			/*String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			String[] strPayCycleDates = null;
			if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getStrPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, strEmpOrgId);
				setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}*/
			
			String[] strPayCycleDates = null;
			if(getFilterBy() == null || getFilterBy().equals("") || getFilterBy().equals("null") || getFilterBy().equals("P")) {
				if (getStrPaycycle() != null && !getStrPaycycle().equalsIgnoreCase("null")) {
					strPayCycleDates = getStrPaycycle().split("-");
				} else {
					strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
					setStrPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
				}
				setStrYear("");
				setStrMonth("");
				setStrWeek("");
			} else if(getFilterBy() != null && getFilterBy().equals("O")) {
				if(getStrWeek()==null || getStrWeek().equals("")) {
					String strDate = "01/"+getStrMonth()+"/"+getStrYear();
					String monthminMaxDates = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
					monthminMaxDates = monthminMaxDates+"::::00";
					strPayCycleDates = monthminMaxDates.split("::::");
				} else if(getStrWeek()!=null && !getStrWeek().equals("")) {
					
				}
				setStrPaycycle("");
			}
			
//			String arr[] = null;
//			if (getStrPaycycle() != null) {
//				arr = getStrPaycycle().split("-");
//			}

			/*
			 * pst = con.prepareStatement(
			 * "select * from activity_info ai, projectmntnc pmc where pmc.pro_id = ai.pro_id and ai.start_date<=? and ai.deadline>=? order by ai.start_date"
			 * ); pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			 * pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone())); ResultSet
			 * rs = pst.executeQuery();
			 * 
			 * if(rs.next()) { setStrClient(rs.getString("client_id"));
			 * setStrProject(rs.getString("pro_id")); }
			 */

			List<String> filledDateList = getTimesheetFilledDates(con, uF, strPayCycleDates[0], strPayCycleDates[1], getStrEmpId());
			request.setAttribute("filledDateList", filledDateList);
			
			Map<String, String> hmEmpEdit = new LinkedHashMap<String, String>();
			if (strUserType != null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
				pst = con.prepareStatement("select * from task_activity pt  where emp_id = ? and task_date between ? and ? and (is_approved is null or is_approved=0)");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || (getPageType() != null && getPageType().equals("MP")))) {
				pst = con.prepareStatement("select * from task_activity where emp_id=? and task_date between ? and ? "
								+ " and is_approved=1  and activity_id in (select task_id from activity_info where task_accept_status=1 and pro_id in "
								+ " (select pmt.pro_id from projectmntnc pmt where pmt.added_by = ?) ) order by generated_date");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
			} else if (strUserType != null && strUserType.equalsIgnoreCase(HRMANAGER) && (getPageType() == null || !getPageType().equals("MP"))) {
				pst = con.prepareStatement("select * from task_activity pt  where emp_id = ? and task_date between ? and ? and  is_approved=1 and activity_id=0");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			} else {
				pst = con.prepareStatement("select * from task_activity pt  where emp_id = ? and task_date between ? and ? and (is_approved is null or is_approved=0)");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			}
			rs = pst.executeQuery();

			while (rs.next()) {
				hmEmpEdit.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpEdit", hmEmpEdit);

			/*
			 * if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
			 * pst=con.prepareStatement(
			 * "select * from task_activity where emp_id = ? and timesheet_paycycle=? "
			 * +
			 * " and activity_id in (select task_id from  activity_info where  pro_id in "
			 * +
			 * " (select pmt.pro_id from projectmntnc pmt where pmt.added_by = ?) )  order by generated_date"
			 * ); pst.setInt(1, uF.parseToInt(getStrEmpId())); pst.setInt(2,
			 * uF.parseToInt(strPayCycleDates[2])); pst.setInt(3,
			 * uF.parseToInt((String)session.getAttribute(EMPID))); } else
			 * if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
			 * pst = con.prepareStatement(
			 * "select * from task_activity pt  where emp_id = ? and timesheet_paycycle=? and is_approved=0"
			 * ); pst.setInt(1, uF.parseToInt(getStrEmpId())); pst.setInt(2,
			 * uF.parseToInt(strPayCycleDates[2])); } else if(strUserType!=null
			 * && strUserType.equalsIgnoreCase(HRMANAGER)) { pst =
			 * con.prepareStatement(
			 * "select * from task_activity pt  where emp_id = ? and timesheet_paycycle=? and is_approved > 0"
			 * ); pst.setInt(1, uF.parseToInt(getStrEmpId())); pst.setInt(2,
			 * uF.parseToInt(strPayCycleDates[2])); } else {
			 */
			
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con,strPayCycleDates[0],strPayCycleDates[1], CF, uF, hmWeekEndHalfDates, null);
			
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,strPayCycleDates[0],strPayCycleDates[1],alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);

			String strWLocationId = CF.getEmpWlocationId(con, uF, getStrEmpId());

			Set<String> weeklyOffEndDate = hmWeekEnds.get(strWLocationId);
			if(weeklyOffEndDate==null) weeklyOffEndDate = new HashSet<String>();
			
			Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(getStrEmpId());
			if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
			
			Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			List<String> empLeaveCountList = new ArrayList<String>();
			List<String> holidayDateList = new ArrayList<String>();
			
			if(alEmpCheckRosterWeektype!=null && alEmpCheckRosterWeektype.contains(getStrEmpId())) {
				CF.getHolidayListCount(con,request, strPayCycleDates[0],strPayCycleDates[1], CF, hmHolidayDates, hmHolidaysCnt, rosterWeeklyOffSet, true);
				empLeaveCountList = CF.getEmpLeaveCount(con, strPayCycleDates[0], strPayCycleDates[1], CF, hmHolidayDates, rosterWeeklyOffSet, getStrEmpId(), strWLocationId);
				holidayDateList = CF.getHolidayDateList(con, strPayCycleDates[0], strPayCycleDates[1], CF, rosterWeeklyOffSet, strWLocationId);
			} else {
				CF.getHolidayListCount(con,request, strPayCycleDates[0],strPayCycleDates[1], CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, true);
				empLeaveCountList = CF.getEmpLeaveCount(con, strPayCycleDates[0], strPayCycleDates[1], CF, hmHolidayDates, weeklyOffEndDate, getStrEmpId(), strWLocationId);
				holidayDateList = CF.getHolidayDateList(con, strPayCycleDates[0], strPayCycleDates[1], CF, weeklyOffEndDate, strWLocationId);
			}
			
			String diffInDays = uF.dateDifference(strPayCycleDates[0], DATE_FORMAT, strPayCycleDates[1], DATE_FORMAT, CF.getStrTimeZone());
			
			int nWeekEnd = (alEmpCheckRosterWeektype!=null && alEmpCheckRosterWeektype.contains(getStrEmpId())) ? rosterWeeklyOffSet.size() : weeklyOffEndDate.size();
			int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strWLocationId));
			
			int nLeaveCnt = empLeaveCountList.size();
			double nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt - nLeaveCnt;
			
			
//			pst = con.prepareStatement("select * from task_activity pt  where emp_id = ? and timesheet_paycycle=? order by is_approved desc");
			pst = con.prepareStatement("select task_date from task_activity pt where emp_id = ? and task_date between ? and ? and is_approved=1 " +
				"group by task_date order by task_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			int intApproved = 0;
			int intSaved = 0;
			int intSubmited = 0;
			int intAllCnt = 0;
			int timesheetId = 0;
			while (rs.next()) {
				
				String todayDate = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT);
				boolean flag = false;
				if(weeklyOffEndDate != null && !weeklyOffEndDate.isEmpty() && weeklyOffEndDate.contains(todayDate)) {
					flag = true;
					intSubmited++;
				} else if(holidayDateList != null && !holidayDateList.isEmpty() && holidayDateList.contains(todayDate)) {
					flag = true;
					intSubmited++;
				} else if(empLeaveCountList != null && !empLeaveCountList.isEmpty() && empLeaveCountList.contains(todayDate)) {
					flag = true;
					intSubmited++;
				} else {
					intSubmited++;
					intAllCnt++;
					
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select task_date from task_activity pt  where emp_id = ? and task_date between ? and ? and is_approved = 0 " +
				"group by task_date order by task_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String todayDate = uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT);
				boolean flag = false;
				if(weeklyOffEndDate != null && !weeklyOffEndDate.isEmpty() && weeklyOffEndDate.contains(todayDate)) {
//					System.out.println("todayDate in weeklyOffEndDate =====>>>> ");
					intSaved++;
					flag = true;
				} else if(holidayDateList != null && !holidayDateList.isEmpty() && holidayDateList.contains(todayDate)) {
//					System.out.println("todayDate in holidayDateList =====>>>> ");
					intSaved++;
					flag = true;
				} else if(empLeaveCountList != null && !empLeaveCountList.isEmpty() && empLeaveCountList.contains(todayDate)) {
//					System.out.println("todayDate in empLeaveCountList =====>>>> ");
					intSaved++;
					flag = true;
				} else {
					intSaved++;
	//				intAllCnt++;
					
				}
			}
			rs.close();
			pst.close();
			
			int tsApproved=0;
			pst = con.prepareStatement("select task_date from task_activity pt  where emp_id = ? and task_date between ? and ? and is_approved = 2 " +
				"group by task_date order by task_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				tsApproved++;
			}
			rs.close();
			pst.close();
			
//			System.out.println("nWorkDays =====>>>> " + nWorkDays);
//			System.out.println("intAllCnt =====>>>> " + intAllCnt);
//			System.out.println("intSaved =====>>>> " + intSaved);
//			System.out.println("intSubmited =====>>>> " + intSubmited);
			
			int nAllCnt = 1;
			int nSaved = 1;
			int nSubmited = 1;
			if(nWorkDays != intAllCnt || (intSaved == 0 && intSubmited > 0)) {
				nAllCnt = 0;
			}
			if(intSaved > 0) {
				nSaved = 0;
			}
			if(intSubmited > 0) {
				nSubmited = 0;
			}
			
//			System.out.println("intSubmited =====>>>> " + intSubmited);
//			System.out.println("nSubmited =====>>>> " + nSubmited);
			
			request.setAttribute("nApproved", nAllCnt + "");
			request.setAttribute("nApproved1", nSaved + "");
			request.setAttribute("nSubmited", nSubmited + "");
			request.setAttribute("tsApproved", tsApproved + "");
			
			request.setAttribute("timesheetId", timesheetId + "");

			pst = con.prepareStatement("select is_approved from work_flow_details where emp_id=? and is_approved =0 and effective_type='"+WORK_FLOW_TIMESHEET+"' " +
				"and effective_id in(select task_id from task_activity where emp_id=? and task_date between ? and ? and is_approved=1 ) " +
				"group by is_approved");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(getStrEmpId()));
//			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();

//			List<String> approvedList = new ArrayList<String>();
			String empApproved = null;
			while (rs.next()) {
//				approvedList.add(rs.getString("is_approved"));
				empApproved = rs.getString("is_approved");
			}
			rs.close();
			pst.close();
			
//			request.setAttribute("approvedList", approvedList);
			request.setAttribute("empApproved", empApproved);

			
			pst = con.prepareStatement("select approved_by,approved_date from task_activity where emp_id = ? and task_date between ? and ? " +
				" and is_approved=2 group by approved_by,approved_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			int i = 0;
			StringBuilder sb = new StringBuilder();

			Map<String, String> hmApprovedDate = new HashMap<String, String>();
			while (rs.next()) {
				if (i == 0) {
					sb.append(hmEmpName.get(rs.getString("approved_by")));
				} else {
					sb.append("," + hmEmpName.get(rs.getString("approved_by")));
				}
				i++;
				hmApprovedDate.put(rs.getString("approved_by"), uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmApprovedDate", hmApprovedDate);
			request.setAttribute("approved_by", sb.toString());

			pst = con.prepareStatement("select t.activity_id,t.task_date,a.pro_id from task_activity t join activity_info a on " +
				"a.task_id=t.activity_id where t.emp_id = ? and t.task_date between ? and ? group by t.task_date,t.activity_id,a.pro_id ");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmActivityIDByDate = new HashMap<String, String>();
			Map<String, String> hmActivityProjectID = new HashMap<String, String>();
			Map<String, String> hmProjectColor = new HashMap<String, String>();
			Random random = new Random();
			while (rs.next()) {
				hmActivityIDByDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT), rs.getString("activity_id"));
				hmActivityProjectID.put(rs.getString("activity_id"), rs.getString("pro_id"));
				int red = (int) (random.nextFloat() * 255);
				int green = (int) (random.nextFloat() * 255);
				int blue = (int) (random.nextFloat() * 255);

				String color = "#" + Integer.toString(red, 16) + Integer.toString(green, 16) + Integer.toString(blue, 16);

				hmProjectColor.put(rs.getString("pro_id"), color);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmActivityIDByDate", hmActivityIDByDate);
			request.setAttribute("hmActivityProjectID", hmActivityProjectID);
			request.setAttribute("hmProjectColor", hmProjectColor);

//			Map<String, String> hmProject = new HashMap<String, String>();

//			pst = con.prepareStatement("select * from projectmntnc ");
//			rs = pst.executeQuery();
//
//			while (rs.next()) {
//				hmProject.put(rs.getString("pro_id"), rs.getString("pro_name"));
//			}
//
//			request.setAttribute("hmProject", hmProject);

			request.setAttribute("datefrom", strPayCycleDates[0]);
			request.setAttribute("dateto", strPayCycleDates[1]);
			request.setAttribute("empid", getStrEmpId());

			// pst =
			// con.prepareStatement("select emp_fname, emp_lname from employee_personal_details where emp_per_id = ?");
			pst = con.prepareStatement("select emp_id,submited_date from task_activity where emp_id=? and task_date between ? and ? and " +
					" submited_date is not null and is_approved > 0 group by emp_id,submited_date order by submited_date");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			String empNameTitle = null;
			if(uF.parseToInt(getStrEmpId()) == uF.parseToInt(strSessionEmpId)) {
				empNameTitle = "My Timesheet";
			} else {
				empNameTitle = hmEmpName.get(getStrEmpId())+"'s Timesheet";
			}
			request.setAttribute("empNameTitle", empNameTitle);
			while (rs.next()) {
				request.setAttribute("submitted_by", hmEmpName.get(rs.getString("emp_id")));
				request.setAttribute("submitted_on", uF.getDateFormat(rs.getString("submited_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select max(task_date) as task_date from task_activity where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				request.setAttribute("LAST_UPDATE", "Last updated on "+ uF.getDateFormat(rs.getString("task_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();

			getReimbursementDetails(con, uF, strPayCycleDates);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void getReimbursementDetails(Connection con, UtilityFunctions uF, String[] strPayCycleDates) {
		Map<String, String> hmReimbursementDetails = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(getStrEmpId())) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(getStrEmpId()));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
//			PreparedStatement pst = con.prepareStatement("select sum(reimbursement_amount) as total, reimbursement_type as pro_id from emp_reimbursement where emp_id =? and reimbursement_type1 = 'P' and (from_date, to_date) overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD') +1) group by reimbursement_type");
			pst = con.prepareStatement("select sum(reimbursement_amount) as total, reimbursement_type as pro_id from emp_reimbursement where emp_id =? and reimbursement_type1 = 'P' " +
					"and from_date >=? and from_date <=? and approval_1 =1 and approval_2=1 group by reimbursement_type");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				hmReimbursementDetails.put(rs.getString("pro_id"), "<a href=\"javascript:void(0);\" onclick=\"getReimbursementDetails('" + rs.getString("pro_id") + "','" + getStrEmpId() + "','" + strPayCycleDates[0]+ "','" + strPayCycleDates[1] + "')\">" + strCurrency + uF.formatIntoTwoDecimal(rs.getDouble("total")) + "</a>");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		request.setAttribute("hmReimbursementDetails", hmReimbursementDetails);

	}

	
	private void getSelectedFilterEmpwise(UtilityFunctions uF) {
		
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(getStrPaycycle() != null && !getStrPaycycle().equals("") && !getStrPaycycle().equalsIgnoreCase("null")) {
			String[] strPayCycleDates = null;
			alFilter.add("PAYCYCLE");
			strPayCycleDates = getStrPaycycle().split("-");
//			System.out.println("APA1/6812---getStrPaycycle=="+getStrPaycycle());
			String strPaycycle = "Cycle "+strPayCycleDates[2]+", "+ uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat())+" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat());
			hmFilter.put("PAYCYCLE", strPaycycle);
		}
		
		if(getStrYear()!=null && !getStrYear().equals("") && !getStrYear().equalsIgnoreCase("null")) {
			alFilter.add("YEAR");
			hmFilter.put("YEAR", getStrYear());
		}
		
		if(getStrMonth()!=null && !getStrMonth().equals("") && !getStrMonth().equalsIgnoreCase("null")) {
			alFilter.add("MONTH");
			hmFilter.put("MONTH", uF.getMonth(uF.parseToInt(getStrMonth())));
		}
		
		String selectedFilter = getSelectedFilter(uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	
	public String getSelectedFilter(UtilityFunctions uF, List<String> alFilter, Map<String, String> hmFilter) {
		StringBuilder sbFilter=new StringBuilder("<strong>Filter Summary: </strong>");
		
		for(int i=0;alFilter!=null && i<alFilter.size();i++) {
			if(i>0) {
					sbFilter.append(", ");
			} 
			
			if(alFilter.get(i).equals("PAYCYCLE")) {
				sbFilter.append("<strong>PAYCYCLE:</strong> ");
				sbFilter.append(hmFilter.get("PAYCYCLE"));
				
			} else if(alFilter.get(i).equals("YEAR")) {
				sbFilter.append("<strong>YEAR:</strong> ");
				sbFilter.append(hmFilter.get("YEAR"));
				
			} else if(alFilter.get(i).equals("MONTH")) {
				sbFilter.append("<strong>MONTH:</strong> ");
				sbFilter.append(hmFilter.get("MONTH"));
			} 
		}
		return sbFilter.toString();
	}
	
	
	String timesheetId;
	String strEmpId;

	public String getFrmDate() {
		return frmDate;
	}

	public void setFrmDate(String frmDate) {
		this.frmDate = frmDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
	public List<String> getStrClient() {
		return strClient;
	}

	public void setStrClient(List<String> strClient) {
		this.strClient = strClient;
	}

	public List<String> getStrProject() {
		return strProject;
	}

	public void setStrProject(List<String> strProject) {
		this.strProject = strProject;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public List<FillClients> getClientlist() {
		return clientlist;
	}

	public void setClientlist(List<FillClients> clientlist) {
		this.clientlist = clientlist;
	}

	public List<FillTask> getTasklist() {
		return tasklist;
	}

	public void setTasklist(List<FillTask> tasklist) {
		this.tasklist = tasklist;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getStrPaycycle() {
		return strPaycycle;
	}

	public void setStrPaycycle(String strPaycycle) {
		this.strPaycycle = strPaycycle;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}

	public String[] getStrDate() {
		return strDate;
	}

	public void setStrDate(String[] strDate) {
		this.strDate = strDate;
	}

	public String[] getStrTask() {
		return strTask;
	}

	public void setStrTask(String[] strTask) {
		this.strTask = strTask;
	}

	public String[] getStrTime() {
		return strTime;
	}

	public void setStrTime(String[] strTime) {
		this.strTime = strTime;
	}

	public String[] getTaskId() {
		return taskId;
	}

	public void setTaskId(String[] taskId) {
		this.taskId = taskId;
	}

	public String getStrActivity() {
		return strActivity;
	}

	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}

	public String getStrActivityTaskId() {
		return strActivityTaskId;
	}

	public void setStrActivityTaskId(String strActivityTaskId) {
		this.strActivityTaskId = strActivityTaskId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTimesheetId() {
		return timesheetId;
	}

	public void setTimesheetId(String timesheetId) {
		this.timesheetId = timesheetId;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getSubmit1() {
		return submit1;
	}

	public void setSubmit1(String submit1) {
		this.submit1 = submit1;
	}

	public String[] getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String[] taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String[] getStrBillableTime() {
		return strBillableTime;
	}

	public void setStrBillableTime(String[] strBillableTime) {
		this.strBillableTime = strBillableTime;
	}

	public String[] getStrBillableYesNo() {
		return strBillableYesNo;
	}

	public void setStrBillableYesNo(String[] strBillableYesNo) {
		this.strBillableYesNo = strBillableYesNo;
	}

	public String[] getStrBillableYesNoT() {
		return strBillableYesNoT;
	}

	public void setStrBillableYesNoT(String[] strBillableYesNoT) {
		this.strBillableYesNoT = strBillableYesNoT;
	}

	public String[] getStrTaskOnOffSite() {
		return strTaskOnOffSite;
	}

	public void setStrTaskOnOffSite(String[] strTaskOnOffSite) {
		this.strTaskOnOffSite = strTaskOnOffSite;
	}

	public String[] getStrTaskOnOffSiteT() {
		return strTaskOnOffSiteT;
	}

	public void setStrTaskOnOffSiteT(String[] strTaskOnOffSiteT) {
		this.strTaskOnOffSiteT = strTaskOnOffSiteT;
	}

	public String getUnlock() {
		return unlock;
	}

	public void setUnlock(String unlock) {
		this.unlock = unlock;
	}

	public String getUserlocation() {
		return userlocation;
	}

	public void setUserlocation(String userlocation) {
		this.userlocation = userlocation;
	}

	public String[] getStatusTaskId() {
		return statusTaskId;
	}

	public void setStatusTaskId(String[] statusTaskId) {
		this.statusTaskId = statusTaskId;
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public String getCheckTask() {
		return checkTask;
	}

	public void setCheckTask(String checkTask) {
		this.checkTask = checkTask;
	}

	public String[] getCompOff() {
		return compOff;
	}

	public void setCompOff(String[] compOff) {
		this.compOff = compOff;
	}

	public String[] getCompOffDate() {
		return compOffDate;
	}

	public void setCompOffDate(String[] compOffDate) {
		this.compOffDate = compOffDate;
	}

	public String[] getUnPaidHolidays() {
		return unPaidHolidays;
	}

	public void setUnPaidHolidays(String[] unPaidHolidays) {
		this.unPaidHolidays = unPaidHolidays;
	}

	public String[] getUnPaidHolidaysDate() {
		return unPaidHolidaysDate;
	}

	public void setUnPaidHolidaysDate(String[] unPaidHolidaysDate) {
		this.unPaidHolidaysDate = unPaidHolidaysDate;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
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

	public List<FillWeekDays> getWeekList() {
		return weekList;
	}

	public void setWeekList(List<FillWeekDays> weekList) {
		this.weekList = weekList;
	}

	public String getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrWeek() {
		return strWeek;
	}

	public void setStrWeek(String strWeek) {
		this.strWeek = strWeek;
	}

	public String getStrMinDate() {
		return strMinDate;
	}

	public void setStrMinDate(String strMinDate) {
		this.strMinDate = strMinDate;
	}

	public String getStrMaxDate() {
		return strMaxDate;
	}

	public void setStrMaxDate(String strMaxDate) {
		this.strMaxDate = strMaxDate;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getFillUserType() {
		return fillUserType;
	}

	public void setFillUserType(String fillUserType) {
		this.fillUserType = fillUserType;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public String getSubmitType() {
		return submitType;
	}

	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}
	
}