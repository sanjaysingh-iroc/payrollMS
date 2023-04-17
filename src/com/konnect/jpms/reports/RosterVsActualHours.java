package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RosterVsActualHours extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(RosterVsActualHours.class);
	
	public String execute() throws Exception { 

		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		request.setAttribute(TITLE, TViewRoster);
		isEmpUserType = false;
		request.setAttribute(PAGE, PReportRosterVsActual);
		
		String str = totalRosterActual();
		if(str.equalsIgnoreCase(ACCESS_DENIED)){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}else{
			return loadRoster();
		}

	}

	public String loadRoster() {
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF);
		empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId);
		return LOAD;
	}

	public String totalRosterActual() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);

			Map<String, String> hmHolidays = new CommonFunctions(CF).getHolidayList(con,request);
			List<String> _alHolidays = new ArrayList<String>();
			List<String> _allDates = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String,String> hmRosterHours = new HashMap<String, String>();
			Map<String,String> hmRosterHoursE = new HashMap<String, String>();
			Map<String, String> hmActualHours = new HashMap<String, String>();
			Map<String, String> hmActualHoursE = new HashMap<String, String>();

			String[] strPayCycleDates = null;

			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			}			
			
			log.debug("Start ===> "+strPayCycleDates[0]);
			log.debug("End ===> "+strPayCycleDates[1]);
			
			
			pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			
			while(rs.next()){ 
				_allDates.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || 
					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) ||
					strUserType.equalsIgnoreCase(HRMANAGER) )) {
				
				pst = con.prepareStatement(selectRosterHours);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				
				
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				
				pst = con.prepareStatement(selectRosterHoursManager);
				pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				
			}else{
				return ACCESS_DENIED;
			}
			
			log.debug("pst-------::::>>>"+pst);
			
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmRosterHours.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), uF.formatIntoOneDecimal(rs.getDouble("roster_hours")));
			}
			rs.close();
			pst.close();
			
			//Calculate roster hours for employee

			pst = con.prepareStatement(selectRosterHoursE);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmployee()));
			
			rs = pst.executeQuery();
			
			while(rs.next()) {
				hmRosterHoursE.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), uF.formatIntoOneDecimal(rs.getDouble("roster_hours")));
			}
			rs.close();
			pst.close();
			 
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || 
					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) ||
					strUserType.equalsIgnoreCase(HRMANAGER) )) {
				pst = con.prepareStatement(selectActualHours);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				pst = con.prepareStatement(selectActualHoursManager);
				pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			}
			
			
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmActualHours.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), uF.formatIntoOneDecimal(rs.getDouble("hours_worked")));
			}
			rs.close();
			pst.close();
			
			//calculate actual hours for selected employee
			
			pst = con.prepareStatement(selectActualHoursE);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmployee()));
			
			rs = pst.executeQuery();
			
			while(rs.next()){
				hmActualHoursE.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), uF.formatIntoOneDecimal(rs.getDouble("hours_worked")));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpData = new HashMap<String, String>();

			pst = con.prepareStatement(selectEmployee1Details);
			pst.setInt(1, uF.parseToInt(getEmployee()));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				hmEmpData.put("NAME", rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpData", hmEmpData);
			request.setAttribute("_allDates",_allDates);
			request.setAttribute("hmRosterHours",hmRosterHours);
			request.setAttribute("hmRosterHoursE",hmRosterHoursE);
			request.setAttribute("hmActualHours",hmActualHours);
			request.setAttribute("hmActualHoursE",hmActualHoursE);
			request.setAttribute("FROM",strPayCycleDates[0]);
			request.setAttribute("TO",strPayCycleDates[1]);
			

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
	String employee;
	List<FillPayCycles> paycycleList;
	List<FillEmployee> empList;
	
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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}


}
