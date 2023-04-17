package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateAttendance extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType;
	String strSessionEmpId;

	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	private String strGrade;
	private String strEmployeType;
	String fromPage;

	private String paycycle;
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_level;
	private String[] f_department;
	private String[] f_service;
	private String[] f_employeType;
	private String[] f_grade;

	private List<FillPayCycles> paycycleList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillWLocation> wLocationList;
	// private List<FillPayCycleDuration> paycycleDurationList;
	// // private List<FillPayMode> paymentModeList;
	private List<FillOrganisation> organisationList;

	private List<FillEmploymentType> employementTypeList;
	private List<FillGrade> gradeList;

	private int halfDayCountIN = 0;
	private int halfDayCountOUT = 0;
	private int fullDayCountIN = 0;
	private int fullDayCountOUT = 0;

	private String[] strEmpIds;
	private String[] strTotalDays;
	private String[] strPaidDays;
	private String[] strPresentDays;
	private String[] strLeaves;
	private String[] strAbsent;

	private String pageFrom;

	private String btnSubmit;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, "/jsp/tms/UpdateAttendance.jsp");
		request.setAttribute(TITLE, "Update Time Entries");

		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT) && !strUserType.equalsIgnoreCase(RECRUITER)
			&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
		 	
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-clock-o\"></i><a href=\"Attendance.action\" style=\"color: #3c8dbc;\"> Time</a></li>" +
			"<li><a href=\"TimeApprovals.action\" style=\"color: #3c8dbc;\"> Approvals</a></li><li class=\"active\">Update Time Entries</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		request.setAttribute("isCalLeaveInAttendanceDependantNo", CF.getIsCalLeaveInAttendanceDependantNo());

		if (getF_org() == null || getF_org().trim().equals("")) {
			setF_org((String) session.getAttribute(ORGID));
		}

		if (getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if (getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if (getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if (getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}

		if (getF_level() != null && getF_level().length > 0) {
			String level_id = "";
			for (int i = 0; i < getF_level().length; i++) {
				if (i == 0) {
					level_id = getF_level()[i];
					level_id.concat(getF_level()[i]);
				} else {
					level_id = level_id + "," + getF_level()[i];
				}
			}
			gradeList = new FillGrade(request).fillGrade(level_id, getF_org());
		} else {
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}

		if (getStrGrade() != null && !getStrGrade().equals("")) {
			setF_grade(getStrGrade().split(","));
		} else {
			setF_grade(null);
		}

		if (getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}

		String[] strPayCycleDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}

		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		String strPC = strPayCycleDates[2];

		// System.out.println("paycycle===>"+getPaycycle());
		String formType = (String) request.getParameter("formType");
		// System.out.println("formtype===>"+formType);
		if (formType != null && formType.trim().equalsIgnoreCase("approve")) {
			approveandCloseAttendance(uF, strD1, strD2, strPC);
			setBtnSubmit("Submit");
		}

		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());

		viewApproveAttendance(uF, strD1, strD2, strPC);

		return loadApproveAttendance(uF);
	}

	private void approveandCloseAttendance(UtilityFunctions uF, String strD1, String strD2, String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			con.setAutoCommit(false);

			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			/*int nTotalDays = getStrTotalDays() != null ? getStrTotalDays().length : 0;
			Map<String, String> hmTotalDays = new HashMap<String, String>();
			for (int i = 0; i < nTotalDays; i++) {
				String[] strTemp = getStrTotalDays()[i].split("::::");
				hmTotalDays.put(strTemp[0], strTemp[1]);
			}*/
			
			/*int nPaidDays = getStrPaidDays() != null ? getStrPaidDays().length : 0;
			Map<String, String> hmPaidDays = new HashMap<String, String>();
			for (int i = 0; i < nPaidDays; i++) {
				String[] strTemp = getStrPaidDays()[i].split("::::");
				hmPaidDays.put(strTemp[0], strTemp[1]);
			}
			int nPresentDays = getStrPresentDays() != null ? getStrPresentDays().length : 0;
			Map<String, String> hmPresentDays = new HashMap<String, String>();
			for (int i = 0; i < nPresentDays; i++) {
				String[] strTemp = getStrPresentDays()[i].split("::::");
				hmPresentDays.put(strTemp[0], strTemp[1]);
			}
			int nLeaves = getStrLeaves() != null ? getStrLeaves().length : 0;
			Map<String, String> hmLeaves = new HashMap<String, String>();
			for (int i = 0; i < nLeaves; i++) {
				String[] strTemp = getStrLeaves()[i].split("::::");
				hmLeaves.put(strTemp[0], strTemp[1]);
			}
			int nAbsent = getStrAbsent() != null ? getStrAbsent().length : 0;
			Map<String, String> hmAbsent = new HashMap<String, String>();
			for (int i = 0; i < nAbsent; i++) {
				String[] strTemp = getStrAbsent()[i].split("::::");
				hmAbsent.put(strTemp[0], strTemp[1]);
			}*/

			// String[] strEmpIds = request.getParameterValues("strEmpIds");
			// int nEmpIds = strEmpIds!=null ? strEmpIds.length : 0;
			// System.out.println("getStrEmpIds()=====>"+getStrEmpIds());
			
			int nEmpIds = getStrEmpIds() != null ? getStrEmpIds().length : 0;
			boolean flag = false;
			pst = con.prepareStatement("insert into approve_attendance (emp_id,financial_year_start,financial_year_end,approve_from,"
					+ "approve_to,paycycle,total_days,paid_days,present_days,paid_leaves,absent_days,approve_by,approve_date," +
					"sal_effective_date,effective_date_total_days) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
			if (nEmpIds > 0) {
				for (int a = 0; a < nEmpIds; a++) {
					if (uF.parseToInt(getStrEmpIds()[a]) > 0) {
						String strEmpId = getStrEmpIds()[a];

						String strPaidDays = request.getParameter("strPaidDays_"+strEmpId);
						String strPresentDays = request.getParameter("strPresentDays_"+strEmpId);
						String strLeaves = request.getParameter("strLeaves_"+strEmpId);
						String strAbsent = request.getParameter("strAbsent_"+strEmpId);
						
//						String strPaidDays = hmPaidDays.get(strEmpId);
//						String strPresentDays = hmPresentDays.get(strEmpId);
//						String strLeaves = hmLeaves.get(strEmpId);
//						String strAbsent = hmAbsent.get(strEmpId);
						String strTotalDays = request.getParameter("strTotalDays_"+strEmpId);
						
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setDate(4, uF.getDateFormat(strD1, DATE_FORMAT));
						pst.setDate(5, uF.getDateFormat(strD2, DATE_FORMAT));
						pst.setInt(6, uF.parseToInt(strPC));
						pst.setDouble(7, uF.parseToDouble(strTotalDays));
						pst.setDouble(8, uF.parseToDouble(strPaidDays));
						pst.setDouble(9, uF.parseToDouble(strPresentDays));
						pst.setDouble(10, uF.parseToDouble(strLeaves));
						pst.setDouble(11, uF.parseToDouble(strAbsent));
						pst.setInt(12, uF.parseToInt(strSessionEmpId));
						pst.setDate(13, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(14, uF.getDateFormat(strD1, DATE_FORMAT));
						pst.setDouble(15, uF.parseToDouble(strTotalDays));
						pst.addBatch();
						flag = true;
//						System.out.println("pst ===>> " + pst);
					}
				}
				if (flag) {
					int[] x = pst.executeBatch();
					if (x.length > 0) {
						con.commit();
						session.setAttribute(MESSAGE, SUCCESSM + "You have successfully approve and close time entries." + END);
					} else {
						con.rollback();
						session.setAttribute(MESSAGE, ERRORM + "Could not approve and close time entries. Please,try again." + END);
					}
				} else {
					con.rollback();
					session.setAttribute(MESSAGE, ERRORM + "Could not approve and close time entries. Please,try again." + END);
				}
				
			} else {
				con.rollback();
				session.setAttribute(MESSAGE, ERRORM + "Could not approve and close time entries. Please,try again." + END);
			}

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void viewApproveAttendance(UtilityFunctions uF, String strD1, String strD2, String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
			int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
			List<String> alActualDates = new ArrayList<String>();
			for (int i = 0; i < nTotalNumberOfDays; i++) {
				String strDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
				alActualDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);
			}
			// System.out.println("alActualDates==>"+alActualDates);
			con = db.makeConnection(con);
			List<List<String>> alPaycycleList = new ArrayList<List<String>>();
			for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size() > 6 && i < 6) || (i < paycycleList.size() && paycycleList.size() <= 6)); i++) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(paycycleList.get(i).getPaycycleId());
				innerList.add(paycycleList.get(i).getPaycycleName());
				String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
				List<String> alList = getAttendanceStatusWithCount(uF, strTmp[0], strTmp[1]);
				innerList.add(alList.get(0));
				innerList.add(alList.get(1));
				alPaycycleList.add(innerList);
			}
			request.setAttribute("alPaycycleList", alPaycycleList);

			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
			if (hmOrg == null)
				hmOrg = new HashMap<String, String>();

			Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);

			StringBuilder sbQuery = new StringBuilder(); 
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and epd.is_alive=true "
				+ "and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
//			sbQuery.append(" and eod.emp_id in (1475) ");
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if (getF_grade() != null && getF_grade().length > 0 && getF_level() != null && getF_level().length > 0) {
				sbQuery.append(" and grade_id in (" + StringUtils.join(getF_grade(), ",") + " ) ");
			} else {
				if (getF_level() != null && getF_level().length > 0) {
					sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( " + StringUtils.join(getF_level(), ",") + ") ) ");
				}
				if (getF_grade() != null && getF_grade().length > 0) {
					sbQuery.append(" and grade_id in (" + StringUtils.join(getF_grade(), ",") + " ) ");
				}
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}

			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" and emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
				+ "and paid_from = ? and paid_to=? group by emp_id) and emp_id not in (select emp_id from approve_attendance where approve_from=? and approve_to=?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
			// System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			Map<String, String> hmEmpPaycycleDuration = new HashMap<String, String>();
			List<String> alEmpJoinDate = new ArrayList<String>();
			Map<String, Map<String, String>> hmEmpData = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				alEmp.add(rs.getString("emp_per_id"));

				hmEmpPaycycleDuration.put(rs.getString("emp_per_id"), rs.getString("paycycle_duration"));
				if (rs.getString("joining_date") != null) {
					Date date = uF.getDateFormatUtil(rs.getString("joining_date"), DBDATE);
					if (uF.isDateBetween(sDate, eDate, date)) {
						alEmpJoinDate.add(rs.getString("emp_per_id"));
					}
				}

				Map<String, String> hmEmp = new HashMap<String, String>();
				hmEmp.put("EMP_CODE", rs.getString("empcode"));

				String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim() + " " : "";
				String strEmpName = rs.getString("emp_fname") + " " + uF.showData(strMiddleName, "") + rs.getString("emp_lname");
				hmEmp.put("EMP_NAME", strEmpName);
				hmEmp.put("EMP_PAYMENT_MODE", rs.getString("payment_mode"));

				hmEmpData.put(rs.getString("emp_per_id"), hmEmp);
			}
			rs.close();
			pst.close();
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("hmEmpPaycycleDuration", hmEmpPaycycleDuration);
			request.setAttribute("alEmpJoinDate", alEmpJoinDate);
			request.setAttribute("hmEmpData", hmEmpData);
			request.setAttribute("hmPaymentModeMap", hmPaymentModeMap);

//			alEmp = new ArrayList<String>();
//			alEmp.add("2764");
			if (alEmp.size() > 0) {
				String strEmpIds = StringUtils.join(alEmp.toArray(), ",");
				
				double dblOverLappingHolidays = 0;
				String strPresentEmpIdNew = null;
				String strPresentEmpIdOld = null;
				Set<String> tempweeklyOffSet = null;
				String level = null;
				String location = null;
				Set<String> tempholidaysSet = null;
				List<String> alPresentEmpId = new ArrayList<String>();
				List<String> alPresentDates = new ArrayList<String>();
				List<String> alPresentWeekEndDates = new ArrayList<String>();
				List<String> alServices = new ArrayList<String>();
				Map<String, String> hmHoursWorked = new HashMap<String, String>();
				List<String> alHalfDaysDueToLatePolicy = new ArrayList<String>();
				Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
				Map<String, String> hmEmpStateMap = new HashMap<String, String>();
				Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
				CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
				Map<String, Map<String, String>> hmHalfDayFullDayMinHrs = CF.getWorkLocationHalfDayFullDayMinHours(con, uF, strD2);
				if(hmHalfDayFullDayMinHrs ==null) hmHalfDayFullDayMinHrs = new HashMap<String, Map<String,String>>();
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);

				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF, hmWeekEndHalfDates, null);
				Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
				CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, strD1, strD2, alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEnds, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
				
				Map<String, Set<String>> holidaysMp = CF.getHolidayList(con, request, uF, strD1, strD2);
				List<String> alFullDaysDueToLatePolicy = new ArrayList<String>();
				Map<String, String> hmEmpRosterHours = new HashMap<String, String>();
				Map<String, Map<String, String>> hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
				Map<String, String> hmMonthlyLeaves = new HashMap<String, String>();
				Map<String, Map<String, String>> hmLeaveDays = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, hmLeaveTypeDays, true, hmMonthlyLeaves);
				if (hmLeaveDays == null) hmLeaveDays = new HashMap<String, Map<String, String>>();
				
				Map<String, String> hmAttendanceDependent = CF.getAttendanceDependency(con);
				Map<String, String> hmRosterDependent = CF.getRosterDependency(con);
				Map<String, List<String>> hmPresentDays = new HashMap<String, List<String>>();
				Map<String, List<String>> hmPresentWeekEndDays = new HashMap<String, List<String>>();
				Map<String, List<String>> hmHalfDays = new HashMap<String, List<String>>();
				Map<String, List<String>> hmFullDays = new HashMap<String, List<String>>();
				Map<String, List<String>> hmServices = new HashMap<String, List<String>>();
				Map<String, Map<String, String>> hmEmpHoursWorked = new HashMap<String, Map<String, String>>();
				Map<String, String> hmOverLappingHolidays = new HashMap<String, String>();
				List<String> alEmpIds = new ArrayList<String>();
				Map<String, String> hmEmpEndDateMap = new HashMap<String, String>();
				Map<String, String> hmEmpJoiningMap = CF.getEmpJoiningDateMap(con, uF, hmEmpEndDateMap);
				request.setAttribute("hmEmpJoiningMap", hmEmpJoiningMap);
				
				Map<String, String> hmLongLeaves = getLongLeavesCount(con, uF, CF, strD1, strD2, strPC);
				if (hmLongLeaves == null) hmLongLeaves = new HashMap<String, String>();
				
				Map<String, String> hmPresentDays1 = new HashMap<String, String>();
				Map<String, String> hmTotalDays = new HashMap<String, String>();
				String strTotalDays = nTotalNumberOfDays + "";
				Map<String, String> hmPaidDays = new HashMap<String, String>();

				Map<String, Map<String, String>> hmUnPaidLeaveTypeDays = new HashMap<String, Map<String, String>>();
				Map<String, String> hmUnPaidMonthlyLeaves = new HashMap<String, String>();
				Map<String, Map<String, String>> hmUnPaidLeaveDays = CF.getActualUnPaidLeaveDates(con, CF, uF, strD1, strD2, hmUnPaidLeaveTypeDays, hmUnPaidMonthlyLeaves);
				if (hmUnPaidLeaveDays == null) hmUnPaidLeaveDays = new HashMap<String, Map<String, String>>();
				
				Map<String, String> hmUnPaidAbsentDays = new HashMap<String, String>();

				Map<String, List<String>> hmEmpAbsentDates = new HashMap<String, List<String>>();
				Map<String, List<String>> hmEmpAttendanceDates = new HashMap<String, List<String>>();
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date "
					+ "and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id in (" + strEmpIds + ") "
					+ "and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =?  "
					+ "and paid_from=? and paid_to=? group by emp_id) order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//				 System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					strPresentEmpIdNew = rs.getString("emp_id");

					java.util.Date joiningDate = uF.getDateFormatUtil(hmEmpJoiningMap.get(strPresentEmpIdNew), DATE_FORMAT);
					java.util.Date attendanceDate = uF.getDateFormatUtil(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), DATE_FORMAT);
					if (attendanceDate.before(joiningDate)) {
						continue;
					}
//					System.out.println("attendanceDate ===>> " + attendanceDate);
					
					if (strPresentEmpIdNew != null && !strPresentEmpIdNew.equalsIgnoreCase(strPresentEmpIdOld)) {
						alPresentEmpId = new ArrayList<String>();
						alPresentDates = new ArrayList<String>();
						alPresentWeekEndDates = new ArrayList<String>();
						alServices = new ArrayList<String>();
						hmHoursWorked = new HashMap<String, String>();
						halfDayCountIN = 0;
						halfDayCountOUT = 0;
						dblOverLappingHolidays = 0;
						alHalfDaysDueToLatePolicy = new ArrayList<String>();
						location = hmEmpWlocationMap.get(strPresentEmpIdNew);
						level = hmEmpLevelMap.get(strPresentEmpIdNew);
						if (alEmpCheckRosterWeektype.contains(strPresentEmpIdNew)) {
							tempweeklyOffSet = hmRosterWeekEndDates.get(strPresentEmpIdNew);
						} else {
							tempweeklyOffSet = hmWeekEnds.get(location);
						}
						if (tempweeklyOffSet == null)
							tempweeklyOffSet = new HashSet<String>();

						Set<String> temp = holidaysMp.get(location);
						if (temp == null) temp = new HashSet<String>();
						tempholidaysSet = new HashSet<String>(temp);
						tempholidaysSet.removeAll(tempweeklyOffSet);

						alFullDaysDueToLatePolicy = new ArrayList<String>();
						fullDayCountIN = 0;
						fullDayCountOUT = 0;

						if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
							List<String> alEmpAbsentDates = new ArrayList<String>(alActualDates);
							hmEmpAbsentDates.put(strPresentEmpIdNew, alEmpAbsentDates);
						}
					}

					List<String> alEmpAbsentDates1 = (List<String>) hmEmpAbsentDates.get(strPresentEmpIdNew);
					if (alEmpAbsentDates1 == null)
						alEmpAbsentDates1 = new ArrayList<String>();

					if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
						int index = alEmpAbsentDates1.indexOf(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						if (index >= 0) {
							alEmpAbsentDates1.remove(index);
							hmEmpAbsentDates.put(strPresentEmpIdNew, alEmpAbsentDates1);
						}

						List<String> alEmpAttendanceDates = (List<String>) hmEmpAttendanceDates.get(strPresentEmpIdNew);
						if (alEmpAttendanceDates == null)
							alEmpAttendanceDates = new ArrayList<String>();
						if (!alEmpAttendanceDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							alEmpAttendanceDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
							hmEmpAttendanceDates.put(strPresentEmpIdNew, alEmpAttendanceDates);
						}
					}

					if (!alEmpIds.contains(rs.getString("emp_id"))) {
						alEmpIds.add(rs.getString("emp_id"));
					}
					if (!alPresentEmpId.contains(strPresentEmpIdNew)) {
						alPresentEmpId.add(strPresentEmpIdNew);
					}

					hmEmpRosterHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT) + "_" + rs.getString("emp_id"), rs.getString("actual_hours"));

					String strDay = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
					String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
					strDay = strDay.toUpperCase();

					double dblEarlyLate = uF.parseToInt(rs.getString("approved")) == -1 ? rs.getDouble("early_late") : 0.0d;
					double dblHDHoursWoeked = uF.parseToInt(rs.getString("approved")) != -1 ? rs.getDouble("hours_worked") : 0.0d;
					double dblActualHoursWorked = rs.getDouble("hours_worked");
					Map<String, String> hmHDFDMinHrs = hmHalfDayFullDayMinHrs.get(hmEmpWlocationMap.get(rs.getString("emp_id")));
					if(hmHDFDMinHrs==null)hmHDFDMinHrs = new HashMap<String, String>();
					
					String strINOUT = rs.getString("in_out");
					Map<String, String> hmLeaves = hmLeaveDays.get(strPresentEmpIdNew);
					if (hmLeaves == null)
						hmLeaves = new HashMap<String, String>();

					if (!alPresentDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && dblActualHoursWorked >= uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_HD"))) {
						String strWeekEnd = null;
						if (tempweeklyOffSet.contains(strDate)) {
							strWeekEnd = WEEKLYOFF_COLOR;
						}

						if (strWeekEnd == null) {
							alPresentDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						} else if (!alPresentWeekEndDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							alPresentWeekEndDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						}
						if (tempholidaysSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							dblOverLappingHolidays++;
						}
					}

					boolean isRosterDependent = uF.parseToBoolean(hmRosterDependent.get(strPresentEmpIdNew));

					if (dblEarlyLate > 0.0d) {
						if (isHalfDay(strDate, dblEarlyLate, strINOUT, hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alHalfDaysDueToLatePolicy != null && isRosterDependent && !alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						}

						if (!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							if (isFullDay(strDate, dblEarlyLate, strINOUT, (String) hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alFullDaysDueToLatePolicy != null && isRosterDependent && !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
								alFullDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
							}
						}
					}

					double x = Math.abs(dblHDHoursWoeked);
//					if (x > 0.0d && x <= 5) {
					if (dblActualHoursWorked >= uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_HD")) && dblActualHoursWorked < uF.parseToDouble(hmHDFDMinHrs.get("MIN_HRS_FD"))) {
						if (!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							String strWLocationId = hmEmpWlocation.get(rs.getString("emp_id"));
							Set<String> weeklyOffSet = hmWeekEnds.get(strWLocationId);
							if (weeklyOffSet == null) weeklyOffSet = new HashSet<String>();

							Set<String> halfDayWeeklyOffSet = hmWeekEndHalfDates.get(strWLocationId);
							if (halfDayWeeklyOffSet == null)
								halfDayWeeklyOffSet = new HashSet<String>();

							Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_id"));
							if (rosterWeeklyOffSet == null)
								rosterWeeklyOffSet = new HashSet<String>();
							if (alEmpCheckRosterWeektype.contains(rs.getString("emp_id"))) {
								if (!rosterWeeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
									alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
								}
							} else if (weeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {

							} else if (holidaysMp.containsKey(uF.getDateFormat(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), DATE_FORMAT, CF.getStrReportDateFormat()) + "_" + strWLocationId)) {

							} else {
								alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
							}
						}
					}

					if (!alServices.contains(rs.getString("service_id"))) {
						alServices.add(rs.getString("service_id"));
					}

					hmPresentDays.put(strPresentEmpIdNew, alPresentDates);
					hmPresentWeekEndDays.put(strPresentEmpIdNew, alPresentWeekEndDates);
					hmHalfDays.put(strPresentEmpIdNew, alHalfDaysDueToLatePolicy);
					hmFullDays.put(strPresentEmpIdNew, alFullDaysDueToLatePolicy);

					hmServices.put(strPresentEmpIdNew, alServices);

					if ("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
						hmHoursWorked.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT) + "_" + rs.getString("service_id"), rs.getString("hours_worked"));
					}
					hmEmpHoursWorked.put(strPresentEmpIdNew, hmHoursWorked);
					hmOverLappingHolidays.put(strPresentEmpIdNew, dblOverLappingHolidays + "");

					strPresentEmpIdOld = strPresentEmpIdNew;
				}
				rs.close();
				pst.close();

				Map<String, List<String>> hmEmpSalEffectiveDates = new LinkedHashMap<String, List<String>>();
				pst = con.prepareStatement("select max (effective_date) as effective_date, emp_id from emp_salary_details where is_approved = true and "
						+ "effective_date<=? and emp_id in (" + strEmpIds + ") group by emp_id order by emp_id");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while (rs.next()) {
					List<String> inneList = hmEmpSalEffectiveDates.get(rs.getString("emp_id"));
					if (inneList == null) inneList = new ArrayList<String>();
					inneList.add(rs.getString("effective_date"));

					hmEmpSalEffectiveDates.put(rs.getString("emp_id"), inneList);
//					if (rs.getInt("emp_id") == 2764) {
//						System.out.println(rs.getInt("emp_id") + " list ===>> " + inneList);
//					}
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("select effective_date, emp_id from emp_salary_details where is_approved = true and effective_date between ? and ? " +
					"and emp_id in ("+strEmpIds+") group by effective_date,emp_id order by effective_date,emp_id");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while (rs.next()) {
					List<String> inneList = hmEmpSalEffectiveDates.get(rs.getString("emp_id"));
					if (inneList == null) inneList = new ArrayList<String>();
					if(!inneList.contains(rs.getString("effective_date"))) {
						inneList.add(rs.getString("effective_date"));
						
						hmEmpSalEffectiveDates.put(rs.getString("emp_id"), inneList);
					}
//					System.out.println("between ==>>> " + rs.getInt("emp_id") + " list ===>> " + inneList);
				}
				rs.close();
				pst.close();

//				System.out.println("hmEmpSalEffectiveDates ===>> " + hmEmpSalEffectiveDates);

				// System.out.println("alEmpIds==>"+alEmpIds);
				List<String> alProcessingEmployee = new ArrayList<String>();
				Map<String, String> hmWoHLeaves = new HashMap<String, String>();
				// int nAlEmpIds = alEmpIds.size();
				Map hmEmpEffectiveDatesAllData = new HashMap();
				int nAlEmpIds = alEmp.size();
				for (int i = 0; i < nAlEmpIds; i++) {
					String strEmpId = alEmp.get(i);
					int nEmpId = uF.parseToInt(strEmpId);
					String strLocation = hmEmpWlocationMap.get(strEmpId);
					String strLevel = hmEmpLevelMap.get(strEmpId);

					Set<String> weeklyOffSet = null;
					if (alEmpCheckRosterWeektype.contains(strEmpId)) {
						weeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
					} else {
						weeklyOffSet = hmWeekEnds.get(strLocation);
					}
					if (weeklyOffSet == null)
						weeklyOffSet = new HashSet<String>();

					Set<String> OriginalholidaysSet = holidaysMp.get(strLocation);
					if (OriginalholidaysSet == null)
						OriginalholidaysSet = new HashSet<String>();

					Set<String> holidaysSet = new HashSet<String>(OriginalholidaysSet);
					holidaysSet.removeAll(weeklyOffSet);

					if (!alProcessingEmployee.add(strEmpId)) {
						alProcessingEmployee.add(strEmpId);
					}

					List<String> alPresentTemp = hmPresentDays.get(strEmpId);
					if (alPresentTemp == null)
						alPresentTemp = new ArrayList<String>();

					List<String> alServiceTemp = hmServices.get(strEmpId);
					if (alServiceTemp == null)
						alServiceTemp = new ArrayList<String>();

					double dblPresent = alPresentTemp.size();

					List<String> alHalfDaysDueToLatePolicyTemp = hmHalfDays.get(strEmpId);
					if (alHalfDaysDueToLatePolicyTemp == null)
						alHalfDaysDueToLatePolicyTemp = new ArrayList<String>();

					dblPresent -= alHalfDaysDueToLatePolicyTemp.size() * 0.5;

					List<String> alFullDaysDueToLatePolicyTemp = (List<String>) hmFullDays.get(strEmpId);
					if (alFullDaysDueToLatePolicyTemp == null)
						alFullDaysDueToLatePolicyTemp = new ArrayList<String>();

					dblPresent -= alFullDaysDueToLatePolicyTemp.size() * 1;

					Map<String, String> hmLeaves = hmLeaveDays.get(strEmpId);
					if (hmLeaves == null)
						hmLeaves = new HashMap<String, String>();

					Map<String, String> hmLeavesType = hmLeaveTypeDays.get(strEmpId);
					if (hmLeavesType == null)
						hmLeavesType = new HashMap<String, String>();

					double nOverlappingHolidaysLeaves = 0;
					double nOverlappingWeekEndsLeaves = 0;
					Iterator<String> it = hmLeaves.keySet().iterator();
					while (it.hasNext()) {
						String strLeaveDate = it.next();

						String strLeaveType = hmLeavesType.get(strLeaveDate);
						// if(strLeaveDate!=null &&
						// holidaysSet.contains(uF.getDateFormat(strLeaveDate,
						// DATE_FORMAT, CF.getStrReportDateFormat())) &&
						// "H".equalsIgnoreCase(strLeaveType)){
						if (strLeaveDate != null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
							nOverlappingHolidaysLeaves += 0.5;
							// }else if(strLeaveDate!=null &&
							// holidaysSet.contains(uF.getDateFormat(strLeaveDate,
							// DATE_FORMAT, CF.getStrReportDateFormat()))){
						} else if (strLeaveDate != null && holidaysSet.contains(strLeaveDate)) {
							nOverlappingHolidaysLeaves++;
						}

						// if(strLeaveDate!=null && strWeekEnd!=null){
						if (weeklyOffSet.contains(strLeaveDate)) {
							nOverlappingWeekEndsLeaves++;
						}

					}
					int nHolidays = holidaysSet.size();
					int nWeekEnds = 0;

					int nAbsent = 0;
					if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
						List<String> alEmpAbsentDates = (List<String>) hmEmpAbsentDates.get(strEmpId);
						if (alEmpAbsentDates == null) alEmpAbsentDates = new ArrayList<String>();

						List<String> alEmpAttendanceDates = (List<String>) hmEmpAttendanceDates.get(strEmpId);
						if (alEmpAttendanceDates == null) alEmpAttendanceDates = new ArrayList<String>();
						
						List<String> leaveDateList = (List<String>) CF.getEmpAllLeaves(con, uF, strD1, strD2, strEmpId);
						if (leaveDateList == null) leaveDateList = new ArrayList<String>();
						
						nAbsent = getAbsentDays(con, uF, strEmpId, alActualDates, alEmpAbsentDates, weeklyOffSet, holidaysSet, alEmpAttendanceDates, leaveDateList);
					}

					if (hmEmpEndDateMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT))) {
						Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
						Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, uF, hmWeekEndHalfDates1, null);
						List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
						Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
						CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, strD1, hmEmpEndDateMap.get(strEmpId), alEmpCheckRosterWeektype1, hmRosterWeekEndDates1, hmWeekEnds1, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates1);

						Set<String> weeklyOffEndDate = null;
						if (alEmpCheckRosterWeektype1.contains(strEmpId)) {
							weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
						} else {
							weeklyOffEndDate = hmWeekEnds1.get(strLocation);
						}
						if (weeklyOffEndDate == null) weeklyOffEndDate = new HashSet<String>();

						nWeekEnds = weeklyOffEndDate.size();
						Map<String, Set<String>> holidaysMp1 = CF.getHolidayList(con, request, uF, strD1, hmEmpEndDateMap.get(strEmpId));
						Set<String> OriginalholidaysSet1 = holidaysMp1.get(strLocation);
						if (OriginalholidaysSet1 == null)
							OriginalholidaysSet1 = new HashSet<String>();
						Set<String> holidaysSet1 = new HashSet<String>(OriginalholidaysSet1);
						holidaysSet1.removeAll(weeklyOffEndDate);

						nHolidays = holidaysSet1.size();

						nAbsent = 0;
						Calendar cal1 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
						cal1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
						cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM")) - 1);
						cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
						int nTotalNumberOfDays1 = cal1.getActualMaximum(Calendar.DATE);

						Date date2 = uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT);

						List<String> alActualDates1 = new ArrayList<String>();
						for (int j = 0; j < nTotalNumberOfDays1; j++) {
							String strDate = uF.zero(cal1.get(Calendar.DAY_OF_MONTH)) + "/" + uF.zero((cal1.get(Calendar.MONTH) + 1)) + "/" + cal1.get(Calendar.YEAR);
							Date date1 = uF.getDateFormatUtil(strDate, DATE_FORMAT);
							if (date1.after(date2)) {
								break;
							}
							alActualDates1.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
							cal1.add(Calendar.DATE, 1);
						}
						if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
							List<String> alEmpAbsentDates = (List<String>) hmEmpAbsentDates.get(strEmpId);
							if (alEmpAbsentDates == null)
								alEmpAbsentDates = new ArrayList<String>();

							List<String> alEmpAttendanceDates = (List<String>) hmEmpAttendanceDates.get(strEmpId);
							if (alEmpAttendanceDates == null)
								alEmpAttendanceDates = new ArrayList<String>();
							List<String> leaveDateList = (List<String>) CF.getEmpAllLeaves(con, uF, strD1, hmEmpEndDateMap.get(strEmpId), strEmpId);
							if (leaveDateList == null)
								leaveDateList = new ArrayList<String>();
							nAbsent = getAbsentDays(con, uF, strEmpId, alActualDates1, alEmpAbsentDates, weeklyOffSet, holidaysSet, alEmpAttendanceDates, leaveDateList);
						}

					} else if (hmEmpJoiningMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT))) {
						Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
						Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con, hmEmpJoiningMap.get(strEmpId), strD2, CF, uF, hmWeekEndHalfDates1, null);
						List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
						Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
						CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, hmEmpJoiningMap.get(strEmpId), strD2, alEmpCheckRosterWeektype1, hmRosterWeekEndDates1, hmWeekEnds1, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates1);

						Set<String> weeklyOffEndDate = null;
						if (alEmpCheckRosterWeektype1.contains(strEmpId)) {
							weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
						} else {
							weeklyOffEndDate = hmWeekEnds1.get(strLocation);
						}
						if (weeklyOffEndDate == null)
							weeklyOffEndDate = new HashSet<String>();

						nWeekEnds = weeklyOffEndDate.size();
						Map<String, Set<String>> holidaysMp1 = CF.getHolidayList(con, request, uF, hmEmpJoiningMap.get(strEmpId), strD2);
						Set<String> OriginalholidaysSet1 = holidaysMp1.get(strLocation);
						if (OriginalholidaysSet1 == null) OriginalholidaysSet1 = new HashSet<String>();
						
						Set<String> holidaysSet1 = new HashSet<String>(OriginalholidaysSet1);
						holidaysSet1.removeAll(weeklyOffEndDate);
						nHolidays = holidaysSet1.size();

						nAbsent = 0;
						Calendar cal1 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
						cal1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT, "dd")));
						cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT, "MM")) - 1);
						cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT, "yyyy")));
						int nTotalNumberOfDays1 = cal1.getActualMaximum(Calendar.DATE);

						List<String> alActualDates1 = new ArrayList<String>();
						for (int j = 0; j < nTotalNumberOfDays1; j++) {
							String strDate = uF.zero(cal1.get(Calendar.DAY_OF_MONTH)) + "/" + uF.zero((cal1.get(Calendar.MONTH) + 1)) + "/" + cal1.get(Calendar.YEAR);
							alActualDates1.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
							cal1.add(Calendar.DATE, 1);
						}

						if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
							List<String> alEmpAbsentDates = (List<String>) hmEmpAbsentDates.get(strEmpId);
							if (alEmpAbsentDates == null) alEmpAbsentDates = new ArrayList<String>();

							List<String> alEmpAttendanceDates = (List<String>) hmEmpAttendanceDates.get(strEmpId);
							if (alEmpAttendanceDates == null) alEmpAttendanceDates = new ArrayList<String>();
							
							List<String> leaveDateList = (List<String>) CF.getEmpAllLeaves(con, uF, hmEmpJoiningMap.get(strEmpId), strD2, strEmpId);
							if (leaveDateList == null) leaveDateList = new ArrayList<String>();
							
							nAbsent = getAbsentDays(con, uF, strEmpId, alActualDates1, alEmpAbsentDates, weeklyOffSet, holidaysSet, alEmpAttendanceDates, leaveDateList);
						}

					} else {
						nWeekEnds = weeklyOffSet.size();
					}

					List<String> alWorkingWeekEnds = hmPresentWeekEndDays.get(strEmpId);
					if (alWorkingWeekEnds == null) alWorkingWeekEnds = new ArrayList<String>();
					
					int nWorkingWeekEnds = alWorkingWeekEnds.size();

					List<String> alOverlappingWeekEndDates = hmPresentWeekEndDays.get(strEmpId);
					if (alOverlappingWeekEndDates == null) alOverlappingWeekEndDates = new ArrayList<String>();

					double dblOverlappingHolidays = uF.parseToDouble(hmOverLappingHolidays.get(strEmpId));

					double dblTotalLeaves = uF.parseToDouble(hmLeavesType.get("COUNT"));
					double dblActualLeaves = dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;

					/**
					 * Long Leave code start
					 * */
					double dblLongLeave = uF.parseToDouble(hmLongLeaves.get(strEmpId));
					dblTotalLeaves = dblTotalLeaves - dblLongLeave;
					hmLeavesType.put("COUNT", "" + dblTotalLeaves);
					/**
					 * Long Leave code end
					 * */

					/**
					 * Unpaid Sandwich Leave Start
					 * */
					
					Map<String, String> hmUnPaidLeavesType1 = hmUnPaidLeaveTypeDays.get(strEmpId);
					if (hmUnPaidLeavesType1 == null)
						hmUnPaidLeavesType1 = new HashMap<String, String>();

					Map<String, String> hmUnPaidLeaves1 = hmUnPaidLeaveDays.get(strEmpId);
					if (hmUnPaidLeaves1 == null)
						hmUnPaidLeaves1 = new HashMap<String, String>();

					double nOverlappingUnPaidSandwichLeaves = 0;
					Iterator<String> it2 = hmUnPaidLeaves1.keySet().iterator();
					while (it2.hasNext()) {
						String strLeaveDate = it2.next();

						String strLeaveType = hmUnPaidLeavesType1.get(strLeaveDate);
						if (strLeaveDate != null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
							nOverlappingUnPaidSandwichLeaves += 0.5;
						} else if (strLeaveDate != null && holidaysSet.contains(strLeaveDate)) {
							nOverlappingUnPaidSandwichLeaves++;
						}

						if (weeklyOffSet.contains(strLeaveDate)) {
							nOverlappingUnPaidSandwichLeaves++;
						}

					}

					/**
					 * Unpaid Sandwich Leave End
					 * */

					double dblTotalPresentDays = 0.0d;
					if (dblPresent == 0.0d && dblTotalLeaves == 0.0d) {
						dblTotalPresentDays = 0.0d;
						hmPresentDays1.put(strEmpId, "0");
					} else {
						dblTotalPresentDays = dblPresent + nHolidays - dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves - nAbsent - nOverlappingUnPaidSandwichLeaves;
						
						hmPresentDays1.put(strEmpId, (dblPresent + nHolidays - dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves - nAbsent - nOverlappingUnPaidSandwichLeaves) + "");
					}

					if (dblTotalPresentDays > nTotalNumberOfDays) {
						dblTotalPresentDays = nTotalNumberOfDays;
					}

					// For ANC all daily employees are calculated Overtime
					// differently
					if (hmEmpPaycycleDuration.get(strEmpId) != null && !hmEmpPaycycleDuration.get(strEmpId).equalsIgnoreCase("M")) {
						if (dblPresent == 0.0d && dblTotalLeaves == 0.0d) {
							dblTotalPresentDays = 0.0d;
							hmPresentDays1.put(strEmpId, "0");
						} else {
							dblTotalPresentDays = dblPresent + dblActualLeaves;
							hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds) + "");
						}
					}

					int nTotalNumberOfDaysForCalc = nTotalNumberOfDays;

					/**
					 * AWD = Actual Working Days
					 * */

					if ("AWD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {

						/**
						 * actual paid leaves
						 * */
						double dblWoHLwaves = dblTotalLeaves;
						hmWoHLeaves.put(strEmpId, "" + dblWoHLwaves);

						dblTotalLeaves = (dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves);
						hmLeavesType.put("COUNT", "" + dblTotalLeaves);

						dblTotalPresentDays = dblPresent + dblTotalLeaves;
						hmPresentDays1.put(strEmpId, (dblPresent) + "");

						int nWeekEnds1 = weeklyOffSet.size();

						nTotalNumberOfDaysForCalc = (nTotalNumberOfDays - nWeekEnds1) - nHolidays;

						strTotalDays = nTotalNumberOfDaysForCalc + "";

					} else if ("AFD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {

						if (dblPresent > 0) {
							int nWeekEnds1 = weeklyOffSet.size();
//							System.out.println("nWeekEnds1 =========>> " + nWeekEnds1);
							dblPresent = dblPresent + nHolidays + nWeekEnds1;
						}
//						System.out.println("dblPresent --- *AFD ===>> " + dblPresent);
						nTotalNumberOfDaysForCalc = uF.parseToInt(hmOrg.get("ORG_SALARY_FIX_DAYS"));
						strTotalDays = uF.showData(hmOrg.get("ORG_SALARY_FIX_DAYS"), "");
//						dblTotalPresentDays = dblPresent - daysDiff;
						dblPresent = (dblPresent / nTotalNumberOfDays) * uF.parseToDouble(strTotalDays);
//						System.out.println("dblPresent after calculation ===>> " + dblPresent +" Math.floor ===>> "+ Math.floor(dblPresent) +" Math.ceil ===>> "+ Math.ceil(dblPresent)  +" Math.round ===>> "+ Math.round(dblPresent));
						
						if(nTotalNumberOfDays >= 30) {
							dblTotalPresentDays = Math.round((dblTotalPresentDays / nTotalNumberOfDays) * uF.parseToDouble(strTotalDays));
							hmPresentDays1.put(strEmpId, Math.round(dblPresent)+"");
						} else {
							dblTotalPresentDays = Math.ceil((dblTotalPresentDays / nTotalNumberOfDays) * uF.parseToDouble(strTotalDays));
							hmPresentDays1.put(strEmpId, Math.ceil(dblPresent)+"");
						}
					}
					hmTotalDays.put(strEmpId, strTotalDays);

					/**
					 * The attendance dependency calculation is for those
					 * employees who are not attendance dependent and will get
					 * the full salary irrespective they clocking on.
					 */

					boolean isAttendance = uF.parseToBoolean(hmAttendanceDependent.get(strEmpId));
					if (!isAttendance) {
						if (CF.getIsCalLeaveInAttendanceDependantNo()) {
							Map<String, String> hmUnPaidLeavesType = hmUnPaidLeaveTypeDays.get(strEmpId);
							if (hmUnPaidLeavesType == null) hmUnPaidLeavesType = new HashMap<String, String>();

							Map<String, String> hmUnPaidLeaves = hmUnPaidLeaveDays.get(strEmpId);
							if (hmUnPaidLeaves == null) hmUnPaidLeaves = new HashMap<String, String>();

							double nOverlappingHolidaysUnPaidLeaves = 0;
							double nOverlappingWeekEndsUnPaidLeaves = 0;
							Iterator<String> it3 = hmUnPaidLeaves.keySet().iterator();
							while (it3.hasNext()) {
								String strLeaveDate = it3.next();

								String strLeaveType = hmUnPaidLeavesType.get(strLeaveDate);
								if (strLeaveDate != null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
									nOverlappingHolidaysUnPaidLeaves += 0.5;
								} else if (strLeaveDate != null && holidaysSet.contains(strLeaveDate)) {
									nOverlappingHolidaysUnPaidLeaves++;
								}

								if (weeklyOffSet.contains(strLeaveDate)) {
									nOverlappingWeekEndsUnPaidLeaves++;
								}

							}

							double dblTotalUnPaidLeaves = uF.parseToDouble(hmUnPaidLeavesType.get("COUNT"));
							double dblActualUnPaidLeaves = dblTotalUnPaidLeaves;
							if (dblActualUnPaidLeaves > 0.0d && nTotalNumberOfDaysForCalc > 0.0d) {
								double paidDays = nTotalNumberOfDaysForCalc - dblActualUnPaidLeaves;
								dblTotalPresentDays = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(paidDays));
								hmUnPaidAbsentDays.put(strEmpId, dblActualUnPaidLeaves + "");
							} else {
								dblTotalPresentDays = nTotalNumberOfDaysForCalc;
							}
						} else {
							// dblTotalPresentDays = nTotalNumberOfDays;
							dblTotalPresentDays = nTotalNumberOfDaysForCalc;
						}
					}
					hmPaidDays.put(strEmpId, dblTotalPresentDays + "");
					
					
					Map hmEmpEffectiveDatesData = new HashMap();
					List<String> alEffectiveDates = hmEmpSalEffectiveDates.get(strEmpId);
//					System.out.println("alEffectiveDates ============>> " + alEffectiveDates.size());
					if(alEffectiveDates != null && alEffectiveDates.size()>1) {
//						System.out.println(strEmpId + " --- alEffectiveDates ============>> " + alEffectiveDates.size());
//						hmEmpEffectiveDatesData = getEmpApproveAttendance(con, pst, rs, uF, strEmpId, alEffectiveDates, strD1, strD2, strPC, strFinancialYearStart, 
//						strFinancialYearEnd, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap, hmEmpLevelMap, hmEmpWlocation, 
//						hmAttendanceDependent, hmRosterDependent, hmEmpJoiningMap, hmEmpEndDateMap, hmEmpPaycycleDuration);
						
					} else {
						List innerList = new ArrayList();
						innerList.add(hmTotalDays); //0
//						innerList.add(hmEmpJoiningMap);
						innerList.add(hmPaidDays); //1
						innerList.add(hmPresentDays1); //2
						innerList.add(hmLeaveDays); //3
						innerList.add(hmLeaveTypeDays); //4
						innerList.add(hmWoHLeaves); //5
//						innerList.add(hmAttendanceDependent);
						innerList.add(hmUnPaidAbsentDays); //6
						
						hmEmpEffectiveDatesData.put(strD1, innerList);
					}
					
					hmEmpEffectiveDatesAllData.put(strEmpId, hmEmpEffectiveDatesData);
				}

				request.setAttribute("hmTotalDays", hmTotalDays);
				request.setAttribute("hmEmpJoiningMap", hmEmpJoiningMap);
				request.setAttribute("hmPaidDays", hmPaidDays);
				request.setAttribute("hmPresentDays", hmPresentDays1);
				request.setAttribute("hmLeaveDays", hmLeaveDays);
				request.setAttribute("hmLeaveTypeDays", hmLeaveTypeDays);
				request.setAttribute("hmWoHLeaves", hmWoHLeaves);
				request.setAttribute("hmAttendanceDependent", hmAttendanceDependent);
				request.setAttribute("hmUnPaidAbsentDays", hmUnPaidAbsentDays);
				
				request.setAttribute("hmEmpEffectiveDatesAllData", hmEmpEffectiveDatesAllData);

			}
			request.setAttribute("strD1", strD1);
			request.setAttribute("strD2", strD2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	
//	private Map getEmpApproveAttendance(Connection con, PreparedStatement pst, ResultSet rs, UtilityFunctions uF, String strEmpId, 
//			List<String> alEffectiveDates, String strD1, String strD2, String strPC, String strFinancialYearStart, String strFinancialYearEnd, 
//			Map<String, String> hmEmpStateMap, Map<String, String> hmEmpWlocationMap, Map<String, String> hmEmpMertoMap, Map<String, String> hmEmpLevelMap,
//			Map<String, String> hmEmpWlocation, Map<String, String> hmAttendanceDependent, Map<String, String> hmRosterDependent, 
//			Map<String, String> hmEmpJoiningMap, Map<String, String> hmEmpEndDateMap, Map<String, String> hmEmpPaycycleDuration) {
//		
//		Map hmEmpEffectiveDatesData = new LinkedHashMap();
//		
//		try {
//			
//			String strPEndDt = strD2;
//			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
//			if (hmOrg == null) hmOrg = new HashMap<String, String>();
//
//			
//			for(int i=0; i<alEffectiveDates.size(); i++) {
//				Date effectiveDate = uF.getDateFormatUtil(alEffectiveDates.get(i), DBDATE);
//				Date paycycleStrtDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
//				if(effectiveDate.after(paycycleStrtDate)) {
//					strD1 = uF.getDateFormat(alEffectiveDates.get(i), DBDATE, DATE_FORMAT);
//				}
//				if(i<(alEffectiveDates.size()-1)) {
//					java.sql.Date sqlEDate = uF.getFutureDate(uF.getDateFormatUtil(alEffectiveDates.get(i+1), DBDATE), -1);
//					strD2 = uF.getDateFormat(sqlEDate+"", DBDATE, DATE_FORMAT);
//				} else {
//					strD2 = strPEndDt;
//				}
////				System.out.println(" strD1 ===>> " + strD1 + " -- strD2 ===>> " + strD2);
//				String strTotDays = uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT, CF.getStrTimeZone());
////				System.out.println("strTotDays =========>> " + strTotDays);
//				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM")) - 1);
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//				int nTotalNumberOfDays = uF.parseToInt(strTotDays);
//				int nMonthMaxDays = cal.getActualMaximum(Calendar.DATE);
//				List<String> alActualDates = new ArrayList<String>();
//				for (int j = 0; j < nTotalNumberOfDays; j++) {
//					String strDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
//					alActualDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
//					cal.add(Calendar.DATE, 1);
//				}
////				System.out.println("alActualDates ===>> " + alActualDates);
//				
//				Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
//				Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);
//				
//				double dblOverLappingHolidays = 0;
//				String strPresentEmpIdNew = null;
//				String strPresentEmpIdOld = null;
//				Set<String> tempweeklyOffSet = null;
//				String level = null;
//				String location = null;
//				Set<String> tempholidaysSet = null;
//				List<String> alPresentEmpId = new ArrayList<String>();
//				List<String> alPresentDates = new ArrayList<String>();
//				List<String> alPresentWeekEndDates = new ArrayList<String>();
//				List<String> alServices = new ArrayList<String>();
//				Map<String, String> hmHoursWorked = new HashMap<String, String>();
//				List<String> alHalfDaysDueToLatePolicy = new ArrayList<String>();
//				
//				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
//				Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF, hmWeekEndHalfDates, null);
//				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
//				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
//				CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, strD1, strD2, alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEnds, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
//				
//				Map<String, Set<String>> holidaysMp = CF.getHolidayList(con, request, uF, strD1, strD2);
//				List<String> alFullDaysDueToLatePolicy = new ArrayList<String>();
//				Map<String, String> hmEmpRosterHours = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
//				Map<String, String> hmMonthlyLeaves = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmLeaveDays = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, hmLeaveTypeDays, true, hmMonthlyLeaves);
//				if (hmLeaveDays == null) hmLeaveDays = new HashMap<String, Map<String, String>>();
//				
//				Map<String, List<String>> hmPresentDays = new HashMap<String, List<String>>();
//				Map<String, List<String>> hmPresentWeekEndDays = new HashMap<String, List<String>>();
//				Map<String, List<String>> hmHalfDays = new HashMap<String, List<String>>();
//				Map<String, List<String>> hmFullDays = new HashMap<String, List<String>>();
//				Map<String, List<String>> hmServices = new HashMap<String, List<String>>();
//				Map<String, Map<String, String>> hmEmpHoursWorked = new HashMap<String, Map<String, String>>();
//				Map<String, String> hmOverLappingHolidays = new HashMap<String, String>();
//				List<String> alEmpIds = new ArrayList<String>();
//				
//				Map<String, String> hmLongLeaves = getLongLeavesCount(con, uF, CF, strD1, strD2, strPC);
//				if (hmLongLeaves == null) hmLongLeaves = new HashMap<String, String>();
//				
//				Map<String, String> hmPresentDays1 = new HashMap<String, String>();
//				Map<String, String> hmTotalDays = new HashMap<String, String>();
//				String strTotalDays = nTotalNumberOfDays + "";
//				Map<String, String> hmPaidDays = new HashMap<String, String>();
//
//				Map<String, Map<String, String>> hmUnPaidLeaveTypeDays = new HashMap<String, Map<String, String>>();
//				Map<String, String> hmUnPaidMonthlyLeaves = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmUnPaidLeaveDays = CF.getActualUnPaidLeaveDates(con, CF, uF, strD1, strD2, hmUnPaidLeaveTypeDays, hmUnPaidMonthlyLeaves);
//				if (hmUnPaidLeaveDays == null) hmUnPaidLeaveDays = new HashMap<String, Map<String, String>>();
//				
//				Map<String, String> hmUnPaidAbsentDays = new HashMap<String, String>();
//
//				
//				Map<String, List<String>> hmEmpAbsentDates = new HashMap<String, List<String>>();
//				Map<String, List<String>> hmEmpAttendanceDates = new HashMap<String, List<String>>();
//				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date "
//					+ "and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and ad.emp_id in (" + strEmpId + ") "
//					+ "and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =?  "
//					+ "and paid_from=? and paid_to=? group by emp_id) order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD'), in_out");
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
////				 System.out.println("pst === -- =====>> " + pst);
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					strPresentEmpIdNew = rs.getString("emp_id");
//
//					java.util.Date joiningDate = uF.getDateFormatUtil(hmEmpJoiningMap.get(strPresentEmpIdNew), DATE_FORMAT);
//					java.util.Date attendanceDate = uF.getDateFormatUtil(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), DATE_FORMAT);
//					if (attendanceDate.before(joiningDate)) {
//						continue;
//					}
//					if (strPresentEmpIdNew != null && !strPresentEmpIdNew.equalsIgnoreCase(strPresentEmpIdOld)) {
//						alPresentEmpId = new ArrayList<String>();
//						alPresentDates = new ArrayList<String>();
//						alPresentWeekEndDates = new ArrayList<String>();
//						alServices = new ArrayList<String>();
//						hmHoursWorked = new HashMap<String, String>();
//						halfDayCountIN = 0;
//						halfDayCountOUT = 0;
//						dblOverLappingHolidays = 0;
//						alHalfDaysDueToLatePolicy = new ArrayList<String>();
//						location = hmEmpWlocationMap.get(strPresentEmpIdNew);
//						level = hmEmpLevelMap.get(strPresentEmpIdNew);
//						if (alEmpCheckRosterWeektype.contains(strPresentEmpIdNew)) {
//							tempweeklyOffSet = hmRosterWeekEndDates.get(strPresentEmpIdNew);
//						} else {
//							tempweeklyOffSet = hmWeekEnds.get(location);
//						}
//						if (tempweeklyOffSet == null)
//							tempweeklyOffSet = new HashSet<String>();
//
//						Set<String> temp = holidaysMp.get(location);
//						if (temp == null) temp = new HashSet<String>();
//						tempholidaysSet = new HashSet<String>(temp);
//						tempholidaysSet.removeAll(tempweeklyOffSet);
//
//						alFullDaysDueToLatePolicy = new ArrayList<String>();
//						fullDayCountIN = 0;
//						fullDayCountOUT = 0;
//
//						if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
//							List<String> alEmpAbsentDates = new ArrayList<String>(alActualDates);
//							hmEmpAbsentDates.put(strPresentEmpIdNew, alEmpAbsentDates);
//						}
//					}
//
//					List<String> alEmpAbsentDates1 = (List<String>) hmEmpAbsentDates.get(strPresentEmpIdNew);
//					if (alEmpAbsentDates1 == null)
//						alEmpAbsentDates1 = new ArrayList<String>();
//
//					if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
//						int index = alEmpAbsentDates1.indexOf(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//						if (index >= 0) {
//							alEmpAbsentDates1.remove(index);
//							hmEmpAbsentDates.put(strPresentEmpIdNew, alEmpAbsentDates1);
//						}
//
//						List<String> alEmpAttendanceDates = (List<String>) hmEmpAttendanceDates.get(strPresentEmpIdNew);
//						if (alEmpAttendanceDates == null)
//							alEmpAttendanceDates = new ArrayList<String>();
//						if (!alEmpAttendanceDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//							alEmpAttendanceDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//							hmEmpAttendanceDates.put(strPresentEmpIdNew, alEmpAttendanceDates);
//						}
//					}
//
//					if (!alEmpIds.contains(rs.getString("emp_id"))) {
//						alEmpIds.add(rs.getString("emp_id"));
//					}
//					if (!alPresentEmpId.contains(strPresentEmpIdNew)) {
//						alPresentEmpId.add(strPresentEmpIdNew);
//					}
//
//					hmEmpRosterHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT) + "_" + rs.getString("emp_id"), rs.getString("actual_hours"));
//
//					String strDay = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
//					String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
//					strDay = strDay.toUpperCase();
//
//					double dblEarlyLate = uF.parseToInt(rs.getString("approved")) == -1 ? rs.getDouble("early_late") : 0.0d;
//					double dblHDHoursWoeked = uF.parseToInt(rs.getString("approved")) != -1 ? rs.getDouble("hours_worked") : 0.0d;
//
//					String strINOUT = rs.getString("in_out");
//					Map<String, String> hmLeaves = hmLeaveDays.get(strPresentEmpIdNew);
//					if (hmLeaves == null)
//						hmLeaves = new HashMap<String, String>();
//
//					if (!alPresentDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//						String strWeekEnd = null;
//						if (tempweeklyOffSet.contains(strDate)) {
//							strWeekEnd = WEEKLYOFF_COLOR;
//						}
//
//						if (strWeekEnd == null) {
//							alPresentDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//						} else if (!alPresentWeekEndDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//							alPresentWeekEndDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//						}
//						if (tempholidaysSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//							dblOverLappingHolidays++;
//						}
//					}
//
//					boolean isRosterDependent = uF.parseToBoolean(hmRosterDependent.get(strPresentEmpIdNew));
//
//					if (dblEarlyLate > 0.0d) {
//						if (isHalfDay(strDate, dblEarlyLate, strINOUT, hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alHalfDaysDueToLatePolicy != null && isRosterDependent && !alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//							alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//						}
//
//						if (!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//							if (isFullDay(strDate, dblEarlyLate, strINOUT, (String) hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alFullDaysDueToLatePolicy != null && isRosterDependent && !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//								alFullDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//							}
//						}
//					}
//
//					double x = Math.abs(dblHDHoursWoeked);
//					if (x > 0.0d && x <= 5) {
//						if (!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//							String strWLocationId = hmEmpWlocation.get(rs.getString("emp_id"));
//							Set<String> weeklyOffSet = hmWeekEnds.get(strWLocationId);
//							if (weeklyOffSet == null) weeklyOffSet = new HashSet<String>();
//
//							Set<String> halfDayWeeklyOffSet = hmWeekEndHalfDates.get(strWLocationId);
//							if (halfDayWeeklyOffSet == null)
//								halfDayWeeklyOffSet = new HashSet<String>();
//
//							Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_id"));
//							if (rosterWeeklyOffSet == null)
//								rosterWeeklyOffSet = new HashSet<String>();
//							if (alEmpCheckRosterWeektype.contains(rs.getString("emp_id"))) {
//								if (!rosterWeeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//									alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//								}
//							} else if (weeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
//
//							} else if (holidaysMp.containsKey(uF.getDateFormat(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), DATE_FORMAT, CF.getStrReportDateFormat()) + "_" + strWLocationId)) {
//
//							} else {
//								alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//							}
//						}
//					}
//
//					if (!alServices.contains(rs.getString("service_id"))) {
//						alServices.add(rs.getString("service_id"));
//					}
//
//					hmPresentDays.put(strPresentEmpIdNew, alPresentDates);
//					hmPresentWeekEndDays.put(strPresentEmpIdNew, alPresentWeekEndDates);
//					hmHalfDays.put(strPresentEmpIdNew, alHalfDaysDueToLatePolicy);
//					hmFullDays.put(strPresentEmpIdNew, alFullDaysDueToLatePolicy);
//
//					hmServices.put(strPresentEmpIdNew, alServices);
//
//					if ("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
//						hmHoursWorked.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT) + "_" + rs.getString("service_id"), rs.getString("hours_worked"));
//					}
//					hmEmpHoursWorked.put(strPresentEmpIdNew, hmHoursWorked);
//					hmOverLappingHolidays.put(strPresentEmpIdNew, dblOverLappingHolidays + "");
//
//					strPresentEmpIdOld = strPresentEmpIdNew;
//				}
//				rs.close();
//				pst.close();
//				
//
//				
//				
//				
//				List<String> alProcessingEmployee = new ArrayList<String>();
//				Map<String, String> hmWoHLeaves = new HashMap<String, String>();
//				// int nAlEmpIds = alEmpIds.size();
//				
//					int nEmpId = uF.parseToInt(strEmpId);
//					String strLocation = hmEmpWlocationMap.get(strEmpId);
//					String strLevel = hmEmpLevelMap.get(strEmpId);
//
//					Set<String> weeklyOffSet = null;
//					if (alEmpCheckRosterWeektype.contains(strEmpId)) {
//						weeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
//					} else {
//						weeklyOffSet = hmWeekEnds.get(strLocation);
//					}
//					if (weeklyOffSet == null)
//						weeklyOffSet = new HashSet<String>();
//
//					Set<String> OriginalholidaysSet = holidaysMp.get(strLocation);
//					if (OriginalholidaysSet == null)
//						OriginalholidaysSet = new HashSet<String>();
//
//					Set<String> holidaysSet = new HashSet<String>(OriginalholidaysSet);
//					holidaysSet.removeAll(weeklyOffSet);
//
//					if (!alProcessingEmployee.add(strEmpId)) {
//						alProcessingEmployee.add(strEmpId);
//					}
//
//					List<String> alPresentTemp = hmPresentDays.get(strEmpId);
//					if (alPresentTemp == null)
//						alPresentTemp = new ArrayList<String>();
//
//					List<String> alServiceTemp = hmServices.get(strEmpId);
//					if (alServiceTemp == null)
//						alServiceTemp = new ArrayList<String>();
//
//					double dblPresent = alPresentTemp.size();
////					System.out.println("dblPresent 1 ===>> " + dblPresent);
//					List<String> alHalfDaysDueToLatePolicyTemp = hmHalfDays.get(strEmpId);
//					if (alHalfDaysDueToLatePolicyTemp == null)
//						alHalfDaysDueToLatePolicyTemp = new ArrayList<String>();
//
//					dblPresent -= alHalfDaysDueToLatePolicyTemp.size() * 0.5;
////					System.out.println("dblPresent 2 --0.5 ===>> " + dblPresent);
//					
//					List<String> alFullDaysDueToLatePolicyTemp = (List<String>) hmFullDays.get(strEmpId);
//					if (alFullDaysDueToLatePolicyTemp == null)
//						alFullDaysDueToLatePolicyTemp = new ArrayList<String>();
//
//					dblPresent -= alFullDaysDueToLatePolicyTemp.size() * 1;
////					System.out.println("dblPresent 3 --1 ===>> " + dblPresent);
//					
//					Map<String, String> hmLeaves = hmLeaveDays.get(strEmpId);
//					if (hmLeaves == null)
//						hmLeaves = new HashMap<String, String>();
//
//					Map<String, String> hmLeavesType = hmLeaveTypeDays.get(strEmpId);
//					if (hmLeavesType == null)
//						hmLeavesType = new HashMap<String, String>();
//
//					double nOverlappingHolidaysLeaves = 0;
//					double nOverlappingWeekEndsLeaves = 0;
//					Iterator<String> it = hmLeaves.keySet().iterator();
//					while (it.hasNext()) {
//						String strLeaveDate = it.next();
//
//						String strLeaveType = hmLeavesType.get(strLeaveDate);
//						if (strLeaveDate != null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
//							nOverlappingHolidaysLeaves += 0.5;
//						} else if (strLeaveDate != null && holidaysSet.contains(strLeaveDate)) {
//							nOverlappingHolidaysLeaves++;
//						}
//
//						if (weeklyOffSet.contains(strLeaveDate)) {
//							nOverlappingWeekEndsLeaves++;
//						}
//
//					}
//					int nHolidays = holidaysSet.size();
//					int nWeekEnds = 0;
//
//					int nAbsent = 0;
//					if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
//						List<String> alEmpAbsentDates = (List<String>) hmEmpAbsentDates.get(strEmpId);
//						if (alEmpAbsentDates == null) alEmpAbsentDates = new ArrayList<String>();
//
//						List<String> alEmpAttendanceDates = (List<String>) hmEmpAttendanceDates.get(strEmpId);
//						if (alEmpAttendanceDates == null) alEmpAttendanceDates = new ArrayList<String>();
//						
//						List<String> leaveDateList = (List<String>) CF.getEmpAllLeaves(con, uF, strD1, strD2, strEmpId);
//						if (leaveDateList == null) leaveDateList = new ArrayList<String>();
//						
//						nAbsent = getAbsentDays(con, uF, strEmpId, alActualDates, alEmpAbsentDates, weeklyOffSet, holidaysSet, alEmpAttendanceDates, leaveDateList);
//					}
//
//					if (hmEmpEndDateMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT))) {
//						Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
//						Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, uF, hmWeekEndHalfDates1, null);
//						List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
//						Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
//						CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, strD1, hmEmpEndDateMap.get(strEmpId), alEmpCheckRosterWeektype1, hmRosterWeekEndDates1, hmWeekEnds1, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates1);
//
//						Set<String> weeklyOffEndDate = null;
//						if (alEmpCheckRosterWeektype1.contains(strEmpId)) {
//							weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
//						} else {
//							weeklyOffEndDate = hmWeekEnds1.get(strLocation);
//						}
//						if (weeklyOffEndDate == null) weeklyOffEndDate = new HashSet<String>();
//
//						nWeekEnds = weeklyOffEndDate.size();
//						Map<String, Set<String>> holidaysMp1 = CF.getHolidayList(con, request, uF, strD1, hmEmpEndDateMap.get(strEmpId));
//						Set<String> OriginalholidaysSet1 = holidaysMp1.get(strLocation);
//						if (OriginalholidaysSet1 == null)
//							OriginalholidaysSet1 = new HashSet<String>();
//						Set<String> holidaysSet1 = new HashSet<String>(OriginalholidaysSet1);
//						holidaysSet1.removeAll(weeklyOffEndDate);
//
//						nHolidays = holidaysSet1.size();
//
//						nAbsent = 0;
//						Calendar cal1 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//						cal1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//						cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM")) - 1);
//						cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//						int nTotalNumberOfDays1 = cal1.getActualMaximum(Calendar.DATE);
//
//						Date date2 = uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT);
//
//						List<String> alActualDates1 = new ArrayList<String>();
//						for (int j = 0; j < nTotalNumberOfDays1; j++) {
//							String strDate = uF.zero(cal1.get(Calendar.DAY_OF_MONTH)) + "/" + uF.zero((cal1.get(Calendar.MONTH) + 1)) + "/" + cal1.get(Calendar.YEAR);
//							Date date1 = uF.getDateFormatUtil(strDate, DATE_FORMAT);
//							if (date1.after(date2)) {
//								break;
//							}
//							alActualDates1.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
//							cal1.add(Calendar.DATE, 1);
//						}
//						if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
//							List<String> alEmpAbsentDates = (List<String>) hmEmpAbsentDates.get(strEmpId);
//							if (alEmpAbsentDates == null)
//								alEmpAbsentDates = new ArrayList<String>();
//
//							List<String> alEmpAttendanceDates = (List<String>) hmEmpAttendanceDates.get(strEmpId);
//							if (alEmpAttendanceDates == null)
//								alEmpAttendanceDates = new ArrayList<String>();
//							List<String> leaveDateList = (List<String>) CF.getEmpAllLeaves(con, uF, strD1, hmEmpEndDateMap.get(strEmpId), strEmpId);
//							if (leaveDateList == null)
//								leaveDateList = new ArrayList<String>();
//							nAbsent = getAbsentDays(con, uF, strEmpId, alActualDates1, alEmpAbsentDates, weeklyOffSet, holidaysSet, alEmpAttendanceDates, leaveDateList);
//						}
//
//					} else if (hmEmpJoiningMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT))) {
//						Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
//						Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con, hmEmpJoiningMap.get(strEmpId), strD2, CF, uF, hmWeekEndHalfDates1, null);
//						List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
//						Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
//						CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, hmEmpJoiningMap.get(strEmpId), strD2, alEmpCheckRosterWeektype1, hmRosterWeekEndDates1, hmWeekEnds1, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates1);
//
//						Set<String> weeklyOffEndDate = null;
//						if (alEmpCheckRosterWeektype1.contains(strEmpId)) {
//							weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
//						} else {
//							weeklyOffEndDate = hmWeekEnds1.get(strLocation);
//						}
//						if (weeklyOffEndDate == null)
//							weeklyOffEndDate = new HashSet<String>();
//
//						nWeekEnds = weeklyOffEndDate.size();
//						Map<String, Set<String>> holidaysMp1 = CF.getHolidayList(con, request, uF, hmEmpJoiningMap.get(strEmpId), strD2);
//						Set<String> OriginalholidaysSet1 = holidaysMp1.get(strLocation);
//						if (OriginalholidaysSet1 == null) OriginalholidaysSet1 = new HashSet<String>();
//						
//						Set<String> holidaysSet1 = new HashSet<String>(OriginalholidaysSet1);
//						holidaysSet1.removeAll(weeklyOffEndDate);
//						nHolidays = holidaysSet1.size();
//
//						nAbsent = 0;
//						Calendar cal1 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//						cal1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT, "dd")));
//						cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT, "MM")) - 1);
//						cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT, "yyyy")));
//						int nTotalNumberOfDays1 = cal1.getActualMaximum(Calendar.DATE);
//
//						List<String> alActualDates1 = new ArrayList<String>();
//						for (int j = 0; j < nTotalNumberOfDays1; j++) {
//							String strDate = uF.zero(cal1.get(Calendar.DAY_OF_MONTH)) + "/" + uF.zero((cal1.get(Calendar.MONTH) + 1)) + "/" + cal1.get(Calendar.YEAR);
//							alActualDates1.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
//							cal1.add(Calendar.DATE, 1);
//						}
//
//						if (CF.getIsSandwichAbsent() && uF.parseToBoolean(hmAttendanceDependent.get(strPresentEmpIdNew))) {
//							List<String> alEmpAbsentDates = (List<String>) hmEmpAbsentDates.get(strEmpId);
//							if (alEmpAbsentDates == null) alEmpAbsentDates = new ArrayList<String>();
//
//							List<String> alEmpAttendanceDates = (List<String>) hmEmpAttendanceDates.get(strEmpId);
//							if (alEmpAttendanceDates == null) alEmpAttendanceDates = new ArrayList<String>();
//							
//							List<String> leaveDateList = (List<String>) CF.getEmpAllLeaves(con, uF, hmEmpJoiningMap.get(strEmpId), strD2, strEmpId);
//							if (leaveDateList == null) leaveDateList = new ArrayList<String>();
//							
//							nAbsent = getAbsentDays(con, uF, strEmpId, alActualDates1, alEmpAbsentDates, weeklyOffSet, holidaysSet, alEmpAttendanceDates, leaveDateList);
//						}
//
//					} else {
//						nWeekEnds = weeklyOffSet.size();
//					}
//
//					List<String> alWorkingWeekEnds = hmPresentWeekEndDays.get(strEmpId);
//					if (alWorkingWeekEnds == null) alWorkingWeekEnds = new ArrayList<String>();
//					
//					int nWorkingWeekEnds = alWorkingWeekEnds.size();
//
//					List<String> alOverlappingWeekEndDates = hmPresentWeekEndDays.get(strEmpId);
//					if (alOverlappingWeekEndDates == null) alOverlappingWeekEndDates = new ArrayList<String>();
//
//					double dblOverlappingHolidays = uF.parseToDouble(hmOverLappingHolidays.get(strEmpId));
//
//					double dblTotalLeaves = uF.parseToDouble(hmLeavesType.get("COUNT"));
//					double dblActualLeaves = dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//
//					/**
//					 * Long Leave code start
//					 * */
//					double dblLongLeave = uF.parseToDouble(hmLongLeaves.get(strEmpId));
//					dblTotalLeaves = dblTotalLeaves - dblLongLeave;
//					hmLeavesType.put("COUNT", "" + dblTotalLeaves);
//					/**
//					 * Long Leave code end
//					 * */
//
//					/**
//					 * Unpaid Sandwich Leave Start
//					 * */
//					Map<String, String> hmUnPaidLeavesType1 = hmUnPaidLeaveTypeDays.get(strEmpId);
//					if (hmUnPaidLeavesType1 == null)
//						hmUnPaidLeavesType1 = new HashMap<String, String>();
//
//					Map<String, String> hmUnPaidLeaves1 = hmUnPaidLeaveDays.get(strEmpId);
//					if (hmUnPaidLeaves1 == null)
//						hmUnPaidLeaves1 = new HashMap<String, String>();
//
//					double nOverlappingUnPaidSandwichLeaves = 0;
//					Iterator<String> it2 = hmUnPaidLeaves1.keySet().iterator();
//					while (it2.hasNext()) {
//						String strLeaveDate = it2.next();
//
//						String strLeaveType = hmUnPaidLeavesType1.get(strLeaveDate);
//						if (strLeaveDate != null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
//							nOverlappingUnPaidSandwichLeaves += 0.5;
//						} else if (strLeaveDate != null && holidaysSet.contains(strLeaveDate)) {
//							nOverlappingUnPaidSandwichLeaves++;
//						}
//
//						if (weeklyOffSet.contains(strLeaveDate)) {
//							nOverlappingUnPaidSandwichLeaves++;
//						}
//
//					}
//
//					/**
//					 * Unpaid Sandwich Leave End
//					 * */
//
//					double dblTotalPresentDays = 0.0d;
//					if (dblPresent == 0.0d && dblTotalLeaves == 0.0d) {
//						dblTotalPresentDays = 0.0d;
//						hmPresentDays1.put(strEmpId, "0");
//					} else {
////						System.out.println("nHolidays ===>> " + nHolidays +" -- dblOverlappingHolidays ===>> " + dblOverlappingHolidays + " -- nWeekEnds ===>> " + nWeekEnds +
////							" -- dblTotalLeaves ===>> " + dblTotalLeaves + " -- nOverlappingHolidaysLeaves ===>> " + nOverlappingHolidaysLeaves 
////							+ " -- nOverlappingWeekEndsLeaves ===>> " + nOverlappingWeekEndsLeaves + " -- nAbsent ===> " + nAbsent +" -- nOverlappingUnPaidSandwichLeaves ===>> " + nOverlappingUnPaidSandwichLeaves);
//						dblTotalPresentDays = dblPresent + nHolidays - dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves - nAbsent - nOverlappingUnPaidSandwichLeaves;
//						
//						hmPresentDays1.put(strEmpId, (dblPresent + nHolidays - dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves - nAbsent - nOverlappingUnPaidSandwichLeaves) + "");
////						System.out.println("dblTotalPresentDays ===>> " + dblTotalPresentDays);
////						System.out.println("hmPresentDays1 ===>> " + hmPresentDays1);
//					}
//
//					if (dblTotalPresentDays > nTotalNumberOfDays) {
//						dblTotalPresentDays = nTotalNumberOfDays;
//					}
//
//					// For ANC all daily employees are calculated Overtime
//					// differently
//					if (hmEmpPaycycleDuration.get(strEmpId) != null && !hmEmpPaycycleDuration.get(strEmpId).equalsIgnoreCase("M")) {
//						if (dblPresent == 0.0d && dblTotalLeaves == 0.0d) {
//							dblTotalPresentDays = 0.0d;
//							hmPresentDays1.put(strEmpId, "0");
//						} else {
//							dblTotalPresentDays = dblPresent + dblActualLeaves;
//							hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds) + "");
////							System.out.println("hmPresentDays1 --- * ===>> " + hmPresentDays1);
//						}
//					}
//
//					int nTotalNumberOfDaysForCalc = nTotalNumberOfDays;
//
//					/**
//					 * AWD = Actual Working Days
//					 * */
//
////					System.out.println("ORG_SALARY_CAL_BASIS ===>> "+uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""));
//					if ("AWD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
//
//						/**
//						 * actual paid leaves
//						 * */
//						
//						double dblWoHLwaves = dblTotalLeaves;
//						hmWoHLeaves.put(strEmpId, "" + dblWoHLwaves);
//
//						dblTotalLeaves = (dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves);
//						hmLeavesType.put("COUNT", "" + dblTotalLeaves);
//
//						dblTotalPresentDays = dblPresent + dblTotalLeaves;
//						hmPresentDays1.put(strEmpId, (dblPresent) + "");
////						System.out.println("hmPresentDays1 --- *AWD ===>> " + hmPresentDays1);
//
//						int nWeekEnds1 = weeklyOffSet.size();
//
//						nTotalNumberOfDaysForCalc = (nTotalNumberOfDays - nWeekEnds1) - nHolidays;
//
//						strTotalDays = nTotalNumberOfDaysForCalc + "";
//
//					} else if ("AFD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
//						if (dblPresent > 0) {
//							int nWeekEnds1 = weeklyOffSet.size();
////							System.out.println("nWeekEnds1 =========>> " + nWeekEnds1);
//							dblPresent = dblPresent + nHolidays + nWeekEnds1;
//						}
////						System.out.println("dblPresent --- *AFD ===>> " + dblPresent);
//						nTotalNumberOfDaysForCalc = uF.parseToInt(hmOrg.get("ORG_SALARY_FIX_DAYS"));
//						strTotalDays = uF.showData(hmOrg.get("ORG_SALARY_FIX_DAYS"), "");
//						dblPresent = (dblPresent / nMonthMaxDays) * uF.parseToDouble(strTotalDays);
////						System.out.println("dblPresent after calculation ===>> " + dblPresent +" Math.floor ===>> "+ Math.floor(dblPresent) +" Math.ceil ===>> "+ Math.ceil(dblPresent)  +" Math.round ===>> "+ Math.round(dblPresent));
//						
////						int daysDiff = nTotalNumberOfDays - nTotalNumberOfDaysForCalc;
////						System.out.println("nTotalNumberOfDays ===>> " + nTotalNumberOfDays);
////						System.out.println("nTotalNumberOfDaysForCalc ===>> " + nTotalNumberOfDaysForCalc);
////						System.out.println("daysDiff ===>> " + daysDiff);
////						dblTotalPresentDays = dblTotalPresentDays - daysDiff;
//
////						hmPresentDays1.put(strEmpId, (uF.parseToDouble(hmPresentDays1.get(strEmpId)) - daysDiff) + "");
//						if(nMonthMaxDays>= 30) {
//							dblTotalPresentDays = Math.round((dblTotalPresentDays / nMonthMaxDays) * uF.parseToDouble(strTotalDays));
//							hmPresentDays1.put(strEmpId, Math.round(dblPresent)+"");
//						} else {
//							dblTotalPresentDays = Math.ceil((dblTotalPresentDays / nMonthMaxDays) * uF.parseToDouble(strTotalDays));
//							hmPresentDays1.put(strEmpId, Math.ceil(dblPresent)+"");
//						}
//						
////						System.out.println("hmPresentDays1 --- *AFD ===>> " + hmPresentDays1);
//					}
//					hmTotalDays.put(strEmpId, strTotalDays);
//
//					boolean isAttendance = uF.parseToBoolean(hmAttendanceDependent.get(strEmpId));
//					if (!isAttendance) {
//						if (CF.getIsCalLeaveInAttendanceDependantNo()) {
//							Map<String, String> hmUnPaidLeavesType = hmUnPaidLeaveTypeDays.get(strEmpId);
//							if (hmUnPaidLeavesType == null) hmUnPaidLeavesType = new HashMap<String, String>();
//
//							Map<String, String> hmUnPaidLeaves = hmUnPaidLeaveDays.get(strEmpId);
//							if (hmUnPaidLeaves == null) hmUnPaidLeaves = new HashMap<String, String>();
//
//							double nOverlappingHolidaysUnPaidLeaves = 0;
//							double nOverlappingWeekEndsUnPaidLeaves = 0;
//							Iterator<String> it3 = hmUnPaidLeaves.keySet().iterator();
//							while (it3.hasNext()) {
//								String strLeaveDate = it3.next();
//
//								String strLeaveType = hmUnPaidLeavesType.get(strLeaveDate);
//								if (strLeaveDate != null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
//									nOverlappingHolidaysUnPaidLeaves += 0.5;
//								} else if (strLeaveDate != null && holidaysSet.contains(strLeaveDate)) {
//									nOverlappingHolidaysUnPaidLeaves++;
//								}
//
//								if (weeklyOffSet.contains(strLeaveDate)) {
//									nOverlappingWeekEndsUnPaidLeaves++;
//								}
//
//							}
//
//							double dblTotalUnPaidLeaves = uF.parseToDouble(hmUnPaidLeavesType.get("COUNT"));
//							// double dblActualUnPaidLeaves =
//							// dblTotalUnPaidLeaves -
//							// nOverlappingHolidaysUnPaidLeaves -
//							// nOverlappingWeekEndsUnPaidLeaves;
//							double dblActualUnPaidLeaves = dblTotalUnPaidLeaves;
////							System.out.println("dblActualUnPaidLeaves ===>> " + dblActualUnPaidLeaves);
////							System.out.println("nTotalNumberOfDaysForCalc ===>> " + nTotalNumberOfDaysForCalc);
//							if (dblActualUnPaidLeaves > 0.0d && nTotalNumberOfDaysForCalc > 0.0d) {
//								double paidDays = nTotalNumberOfDaysForCalc - dblActualUnPaidLeaves;
//								dblTotalPresentDays = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(paidDays));
//								hmUnPaidAbsentDays.put(strEmpId, dblActualUnPaidLeaves + "");
//							} else {
//								dblTotalPresentDays = nTotalNumberOfDaysForCalc;
//							}
//						} else {
//							// dblTotalPresentDays = nTotalNumberOfDays;
//							dblTotalPresentDays = nTotalNumberOfDaysForCalc;
//						}
//					}
//
//					hmPaidDays.put(strEmpId, dblTotalPresentDays + "");
//				
////					System.out.println(strD1 + " -- hmPresentDays ===>> " + hmPresentDays);
////					System.out.println(strD1 + " -- hmPresentWeekEndDays ===>> " + hmPresentWeekEndDays);
////					System.out.println(strD1 + " -- hmHalfDays ===>> " + hmHalfDays);
////					System.out.println(strD1 + " -- hmFullDays ===>> " + hmFullDays);
////					System.out.println(strD1 + " -- hmEmpHoursWorked ===>> " + hmEmpHoursWorked);
////					System.out.println(strD1 + " -- hmOverLappingHolidays ===>> " + hmOverLappingHolidays);
//					
//					
////					System.out.println(strD1 + " -- hmTotalDays ===>> " + hmTotalDays);
////					System.out.println(strD1 + " -- hmEmpJoiningMap ===>> " + hmEmpJoiningMap);
////					System.out.println(strD1 + " -- hmPaidDays ===>> " + hmPaidDays);
////					System.out.println(strD1 + " -- hmPresentDays1 ===>> " + hmPresentDays1);
////					System.out.println(strD1 + " -- hmLeaveDays ===>> " + hmLeaveDays);
////					System.out.println(strD1 + " -- hmLeaveTypeDays ===>> " + hmLeaveTypeDays);
////					System.out.println(strD1 + " -- hmWoHLeaves ===>> " + hmWoHLeaves);
////					System.out.println(strD1 + " -- hmAttendanceDependent ===>> " + hmAttendanceDependent);
////					System.out.println(strD1 + " -- hmUnPaidAbsentDays ===>> " + hmUnPaidAbsentDays);
//					
//				List innerList = new ArrayList();
//				innerList.add(hmTotalDays); //0
////				innerList.add(hmEmpJoiningMap);
//				innerList.add(hmPaidDays); //1
//				innerList.add(hmPresentDays1); //2
//				innerList.add(hmLeaveDays); //3
//				innerList.add(hmLeaveTypeDays); //4
//				innerList.add(hmWoHLeaves); //5
////				innerList.add(hmAttendanceDependent);
//				innerList.add(hmUnPaidAbsentDays); //6
//				
//				hmEmpEffectiveDatesData.put(strD1, innerList);
//			}
//			
////			System.out.println("hmEmpEffectiveDatesData ===>> " + hmEmpEffectiveDatesData);
//			
//			/*
//				String strEmpIds = StringUtils.join(alEmp.toArray(), ",");
//				
//				request.setAttribute("hmTotalDays", hmTotalDays);
//				request.setAttribute("hmEmpJoiningMap", hmEmpJoiningMap);
//				request.setAttribute("hmPaidDays", hmPaidDays);
//				request.setAttribute("hmPresentDays", hmPresentDays1);
//				request.setAttribute("hmLeaveDays", hmLeaveDays);
//				request.setAttribute("hmLeaveTypeDays", hmLeaveTypeDays);
//				request.setAttribute("hmWoHLeaves", hmWoHLeaves);
//				request.setAttribute("hmAttendanceDependent", hmAttendanceDependent);
//				request.setAttribute("hmUnPaidAbsentDays", hmUnPaidAbsentDays);*/
//
////			request.setAttribute("strD1", strD1);
////			request.setAttribute("strD2", strD2);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return hmEmpEffectiveDatesData;
//	}
	
	
	
	
	private List<String> getAttendanceStatusWithCount(UtilityFunctions uF, String strD1, String strD2) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alAttendaneStatus = new ArrayList<String>();
		try {
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
			int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
			List<String> alActualDates = new ArrayList<String>();
			for (int i = 0; i < nTotalNumberOfDays; i++) {
				String strDate = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/"
						+ (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
				alActualDates.add(uF.getDateFormat(strDate, DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);
			}
			// System.out.println("alActualDates==>"+alActualDates);
			con = db.makeConnection(con);

			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as emp_ids from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " + // and epd.is_alive=true
				"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if (getF_level() != null && getF_level().length > 0) {
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( " + StringUtils.join(getF_level(), ",") + ") ) ");
			}
			if (getF_grade() != null && getF_grade().length > 0) {
				sbQuery.append(" and grade_id in (" + StringUtils.join(getF_grade(), ",") + " ) ");
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}

			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" and emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
					+ "and paid_from = ? and paid_to=? group by emp_id) and emp_id not in (select emp_id from approve_attendance where "
					+ "approve_from=? and approve_to=?)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
			// System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			String strPendingEmpCount = "0";
			while (rs.next()) {
				strPendingEmpCount = rs.getString("emp_ids");
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as emp_ids from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " + // and epd.is_alive=true
				"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) and emp_per_id > 0 ");
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if (getF_level() != null && getF_level().length > 0) {
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
				+ StringUtils.join(getF_level(), ",") + ") ) ");
			}
			if (getF_grade() != null && getF_grade().length > 0) {
				sbQuery.append(" and grade_id in (" + StringUtils.join(getF_grade(), ",") + " ) ");
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}
			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}

			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" and (emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
				+ "and paid_from = ? and paid_to=? group by emp_id) or emp_id in (select emp_id from approve_attendance where approve_from=? and approve_to=?)) ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(strD2, DATE_FORMAT));
			// System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			String strApprovedEmpCount = "0";
			while (rs.next()) {
				strApprovedEmpCount = rs.getString("emp_ids");
			}
			rs.close();
			pst.close();

			alAttendaneStatus.add(strApprovedEmpCount);
			alAttendaneStatus.add(strPendingEmpCount);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alAttendaneStatus;
	}

	
	
	private int getAbsentDays(Connection con, UtilityFunctions uF, String strEmpId, List<String> alActualDates, List<String> alEmpAbsentDates,
			Set<String> weeklyOffSet, Set<String> holidaysSet, List<String> alEmpAttendanceDates, List<String> leaveDateList) {
		int nAbsent = 0;
		try {
			// System.out.println("alEmpAbsentDates==>"+alEmpAbsentDates);
			// System.out.println("weeklyOffSet==>"+weeklyOffSet);
			// System.out.println("holidaysSet==>"+holidaysSet);
			// System.out.println("alEmpAttendanceDates==>"+alEmpAttendanceDates);
			// System.out.println("last date==>"+alActualDates.get(alActualDates.size()-1));

			Date lastDate = uF.getDateFormatUtil(alActualDates.get(alActualDates.size() - 1), DATE_FORMAT);
			List<String> alAbsentDate = new ArrayList<String>(alEmpAbsentDates);
			List<String> alWeekOff = new ArrayList<String>();
			int nSize = alActualDates.size();
			for (int i = 0; i < nSize; i++) {
				String strDate = alActualDates.get(i);
				if (!alWeekOff.contains(strDate) && ((weeklyOffSet != null && weeklyOffSet.contains(strDate)) || (holidaysSet != null && holidaysSet.contains(strDate)))) {
					alWeekOff.add(strDate);

					int index = alAbsentDate.indexOf(strDate);
					if (index >= 0) {
						alAbsentDate.remove(index);
					}
				} else if (leaveDateList != null && leaveDateList.contains(strDate)) {
					int index = alAbsentDate.indexOf(strDate);
					if (index >= 0) {
						alAbsentDate.remove(index);
					}
				}
			}
			// System.out.println("alWeekOff==>"+alWeekOff);
			// System.out.println("alAbsentDate==>"+alAbsentDate);

			int nAlAbsentDate = alAbsentDate.size();
			String strFinalDate = null;
			for (int i = 0; i < nAlAbsentDate; i++) {
				String strDate = alAbsentDate.get(i);
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				java.util.Date date1 = sdf.parse(strDate);
				// System.out.println("strFinalDate==>"+strFinalDate);
				if (strFinalDate != null) {
					java.util.Date dateFinal = sdf.parse(strFinalDate);
					if (date1.before(dateFinal) || date1.equals(dateFinal)) {
						continue;
					}
				}
				String strToDate = getAbsentLastDate(uF, strDate, alAbsentDate, alEmpAttendanceDates, alWeekOff, leaveDateList, lastDate);
				// System.out.println("strDate==>"+strDate+"--strToDate==>"+strToDate);
				if (alWeekOff.contains(strToDate) && !lastDate.equals(sdf.parse(strToDate))) {
					// if(alWeekOff.contains(strToDate)){
					strToDate = uF.getDateFormat("" + uF.getPrevDate(uF.getDateFormatUtil(strToDate, DATE_FORMAT), 1), DBDATE, DATE_FORMAT);
				}
				// System.out.println("final strToDate==>"+strToDate);
				if (strToDate != null) {
					strFinalDate = strToDate;
					java.util.Date date2 = sdf.parse(strToDate);
					if (!date1.equals(date2)) {
						int nCnt = uF.parseToInt(uF.dateDifference(strDate, DATE_FORMAT, strToDate, DATE_FORMAT, CF.getStrTimeZone()));
						// System.out.println("final cnt strDate==>"+strDate+"--nCnt==>"+nCnt);
						for (int j = 0; j < nCnt; j++) {
							Date futureDate = uF.getFutureDate(uF.getDateFormatUtil(strDate, DATE_FORMAT), (j + 1));
							// System.out.println("final cnt futureDate==>"+futureDate);
							if (!futureDate.after(date2) && alWeekOff.contains(uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT))) {
								nAbsent++;
								// System.out.println("final cnt strDate==>"+strDate+"--nAbsent==>"+nAbsent);
							}
						}
					}
				}
			}
			// System.out.println("nAbsent==>"+nAbsent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nAbsent;
	}

	private String getAbsentLastDate(UtilityFunctions uF, String strDate, List<String> alAbsentDate, List<String> alEmpAttendanceDates, List<String> alWeekOff,
			List<String> leaveDateList, Date lastDate) {
		String strToDate = null;
		try {
			Date futureDate = uF.getFutureDate(uF.getDateFormatUtil(strDate, DATE_FORMAT), 1);
			if ((alWeekOff.contains(uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT)) && !leaveDateList.contains(uF.getDateFormat("" + futureDate,
					DBDATE, DATE_FORMAT))) && !alEmpAttendanceDates.contains(uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT))) {
				// System.out.println("main futureDate==>"+uF.getDateFormat(""+futureDate,
				// DBDATE, DATE_FORMAT));
				strToDate = checkAbsentLastDate(uF, uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT), alAbsentDate, alEmpAttendanceDates, alWeekOff, leaveDateList, lastDate);
			}

			if (lastDate.equals(futureDate) && (strToDate == null || strToDate.trim().equals("") || strToDate.trim().equalsIgnoreCase("NULL"))) {
				strToDate = uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT);
			} else if ((strToDate == null || strToDate.trim().equals("") || strToDate.trim().equalsIgnoreCase("NULL"))) {
				strToDate = strDate;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strToDate;
	}

	private String checkAbsentLastDate(UtilityFunctions uF, String strDate, List<String> alAbsentDate, List<String> alEmpAttendanceDates,
			List<String> alWeekOff, List<String> leaveDateList, Date lastDate) {
		String strToDate = null;
		try {
			Date futureDate = uF.getFutureDate(uF.getDateFormatUtil(strDate, DATE_FORMAT), 1);
			if ((alWeekOff.contains(uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT)) && !leaveDateList.contains(uF.getDateFormat("" + futureDate,
					DBDATE, DATE_FORMAT))) && !alEmpAttendanceDates.contains(uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT))) {
				// System.out.println("inner if futureDate==>"+uF.getDateFormat(""+futureDate,
				// DBDATE, DATE_FORMAT));
				strToDate = checkAbsentLastDate(uF, uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT), alAbsentDate, alEmpAttendanceDates, alWeekOff, leaveDateList, lastDate);
				if ((strToDate == null || strToDate.trim().equals("") || strToDate.trim().equalsIgnoreCase("NULL"))) {
					strToDate = uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT);
				}
			} else if (alAbsentDate.contains(uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT))) {
				// System.out.println("inner else if futureDate==>"+uF.getDateFormat(""+futureDate,
				// DBDATE, DATE_FORMAT));
				strToDate = checkAbsentLastDate(uF, uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT), alAbsentDate, alEmpAttendanceDates, alWeekOff, leaveDateList, lastDate);
				if ((strToDate == null || strToDate.trim().equals("") || strToDate.trim().equalsIgnoreCase("NULL"))) {
					strToDate = uF.getDateFormat("" + futureDate, DBDATE, DATE_FORMAT);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strToDate;
	}

	private Map<String, String> getLongLeavesCount(Connection con, UtilityFunctions uF, CommonFunctions CF, String strD1, String strD2, String strPC) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLongLeaves = new HashMap<String, String>();
		try {

			pst = con.prepareStatement("select sum(leave_no) as paid_leaves, emp_id from leave_application_register where _date between ? and ? and _type = ? and is_paid = ?  and is_long_leave=true and is_modify= false and leave_type_id not in (select leave_type_id from leave_type where is_compensatory = true) group by emp_id");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setBoolean(3, true);
			pst.setBoolean(4, true);
			// System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLongLeaves.put(rs.getString("emp_id"), rs.getString("paid_leaves"));
			}
			rs.close();
			pst.close();
			// System.out.println("hmLongLeaves=====>"+hmLongLeaves);

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
		return hmLongLeaves;
	}

	public boolean isHalfDay(String strDate, double dblEarlyLate, String strINOUT, String strLocationId, UtilityFunctions uF, Connection con) {
		boolean isHalfDay = false;

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			if (dblEarlyLate == 0)
				return false;
			// double dblValue = dblEarlyLate * 60;
			double dblValue = dblEarlyLate;
			int days = 0;

			if ("IN".equalsIgnoreCase(strINOUT)) {
				pst = con.prepareStatement("select * from roster_halfday_policy where time_value < ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes1(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			} else {
				pst = con.prepareStatement("select * from roster_halfday_policy where time_value > ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes1(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			}
			// System.out.println("half day rule pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if ("IN".equalsIgnoreCase(strINOUT)) {
					halfDayCountIN++;
				} else {
					halfDayCountOUT++;
				}
				days = rs.getInt("days");
			}
			rs.close();
			pst.close();

			if (days == halfDayCountIN && halfDayCountIN > 0) {
				halfDayCountIN = 0;
				isHalfDay = true;
			}

			if (days == halfDayCountOUT && halfDayCountOUT > 0) {
				halfDayCountOUT = 0;
				isHalfDay = true;
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
		return isHalfDay;
	}

	public boolean isFullDay(String strDate, double dblEarlyLate, String strINOUT, String strLocationId, UtilityFunctions uF, Connection con) {
		boolean isFullDay = false;

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			if (dblEarlyLate == 0)
				return false;

			// double dblValue = dblEarlyLate * 60;
			double dblValue = dblEarlyLate;
			int days = 0;

			if ("IN".equalsIgnoreCase(strINOUT)) {
				pst = con.prepareStatement("select * from roster_fullday_policy where time_value < ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes1(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			} else {
				pst = con.prepareStatement("select * from roster_fullday_policy where time_value > ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value  limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes1(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			}

			rs = pst.executeQuery();
			while (rs.next()) {
				if ("IN".equalsIgnoreCase(strINOUT)) {
					fullDayCountIN++;
				} else {
					fullDayCountOUT++;
				}
				days = rs.getInt("days");
			}
			rs.close();
			pst.close();

			// System.out.println("pst==="+pst);
			// System.out.println("halfDayCountOUT==="+halfDayCountOUT);
			// System.out.println("halfDayCountIN==="+halfDayCountIN);

			if (days == fullDayCountIN && fullDayCountIN > 0) {
				fullDayCountIN = 0;
				isFullDay = true;
			}

			if (days == fullDayCountOUT && fullDayCountOUT > 0) {
				fullDayCountOUT = 0;
				isFullDay = true;
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
		return isFullDay;
	}

	private String loadApproveAttendance(UtilityFunctions uF) {

		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);

		employementTypeList = new FillEmploymentType().fillEmploymentType(request);

		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}

		getSelectedFilter(uF);

		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if (getF_org() != null) {
			String strOrg = "";
			int k = 0;
			for (int i = 0; organisationList != null && i < organisationList.size(); i++) {
				if (getF_org().equals(organisationList.get(i).getOrgId())) {
					if (k == 0) {
						strOrg = organisationList.get(i).getOrgName();
					} else {
						strOrg += ", " + organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if (strOrg != null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}

		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}

		alFilter.add("PAYCYCLE");
		if (getPaycycle() != null) {
			String strPayCycle = "";
			int k = 0;
			for (int i = 0; paycycleList != null && i < paycycleList.size(); i++) {
				if (getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
					if (k == 0) {
						strPayCycle = paycycleList.get(i).getPaycycleName();
					} else {
						strPayCycle += ", " + paycycleList.get(i).getPaycycleName();
					}
					k++;
				}
			}
			if (strPayCycle != null && !strPayCycle.equals("")) {
				hmFilter.put("PAYCYCLE", strPayCycle);
			} else {
				hmFilter.put("PAYCYCLE", "All Paycycle");
			}

		}

		alFilter.add("LOCATION");
		if (getF_strWLocation() != null) {
			String strLocation = "";
			int k = 0;
			for (int i = 0; wLocationList != null && i < wLocationList.size(); i++) {
				for (int j = 0; j < getF_strWLocation().length; j++) {
					if (getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if (k == 0) {
							strLocation = wLocationList.get(i).getwLocationName();
						} else {
							strLocation += ", " + wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if (strLocation != null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}

		alFilter.add("DEPARTMENT");
		if (getF_department() != null) {
			String strDepartment = "";
			int k = 0;
			for (int i = 0; departmentList != null && i < departmentList.size(); i++) {
				for (int j = 0; j < getF_department().length; j++) {
					if (getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if (k == 0) {
							strDepartment = departmentList.get(i).getDeptName();
						} else {
							strDepartment += ", " + departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if (strDepartment != null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}

		alFilter.add("SERVICE");
		if (getF_service() != null) {
			String strService = "";
			int k = 0;
			for (int i = 0; serviceList != null && i < serviceList.size(); i++) {
				for (int j = 0; j < getF_service().length; j++) {
					if (getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if (k == 0) {
							strService = serviceList.get(i).getServiceName();
						} else {
							strService += ", " + serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if (strService != null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All Services");
			}
		} else {
			hmFilter.put("SERVICE", "All Services");
		}

		alFilter.add("LEVEL");
		if (getF_level() != null) {
			String strLevel = "";
			int k = 0;
			for (int i = 0; levelList != null && i < levelList.size(); i++) {
				for (int j = 0; j < getF_level().length; j++) {
					if (getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if (k == 0) {
							strLevel = levelList.get(i).getLevelCodeName();
						} else {
							strLevel += ", " + levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if (strLevel != null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}

		alFilter.add("GRADE");
		if (getF_grade() != null) {
			String strgrade = "";
			int k = 0;
			for (int i = 0; gradeList != null && i < gradeList.size(); i++) {
				for (int j = 0; j < getF_grade().length; j++) {
					if (getF_grade()[j].equals(gradeList.get(i).getGradeId())) {
						if (k == 0) {
							strgrade = gradeList.get(i).getGradeCode();
						} else {
							strgrade += ", " + gradeList.get(i).getGradeCode();
						}
						k++;
					}
				}
			}
			if (strgrade != null && !strgrade.equals("")) {
				hmFilter.put("GRADE", strgrade);
			} else {
				hmFilter.put("GRADE", "All Grade's");
			}
		} else {
			hmFilter.put("GRADE", "All Grade's");
		}

		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String strEmployeeType = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							strEmployeeType = employementTypeList.get(i).getEmpTypeName();
						} else {
							strEmployeeType += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (strEmployeeType != null && !strEmployeeType.equals("")) {
				hmFilter.put("EMPTYPE", strEmployeeType);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type's");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type's");
		}

		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String[] getStrEmpIds() {
		return strEmpIds;
	}

	public void setStrEmpIds(String[] strEmpIds) {
		this.strEmpIds = strEmpIds;
	}

	public String[] getStrTotalDays() {
		return strTotalDays;
	}

	public void setStrTotalDays(String[] strTotalDays) {
		this.strTotalDays = strTotalDays;
	}

	public String[] getStrPaidDays() {
		return strPaidDays;
	}

	public void setStrPaidDays(String[] strPaidDays) {
		this.strPaidDays = strPaidDays;
	}

	public String[] getStrPresentDays() {
		return strPresentDays;
	}

	public void setStrPresentDays(String[] strPresentDays) {
		this.strPresentDays = strPresentDays;
	}

	public String[] getStrLeaves() {
		return strLeaves;
	}

	public void setStrLeaves(String[] strLeaves) {
		this.strLeaves = strLeaves;
	}

	public String[] getStrAbsent() {
		return strAbsent;
	}

	public void setStrAbsent(String[] strAbsent) {
		this.strAbsent = strAbsent;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

}