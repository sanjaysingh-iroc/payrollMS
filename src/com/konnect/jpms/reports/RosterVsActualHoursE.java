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

import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RosterVsActualHoursE extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	boolean isEmpUserType = false; 
	CommonFunctions CF = null; 
	private static Logger log = Logger.getLogger(RosterVsActualHoursE.class);
	
	public String execute() throws Exception { 

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		request.setAttribute(TITLE, TViewRoster);
		isEmpUserType = false;

		request.setAttribute(PAGE, PReportRosterVsActualE);

		String str = employeeRosterActual();
		
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

		return LOAD;
	}

	

	public String employeeRosterActual() {

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
			List<String> empId = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map hmRosterHours = new HashMap();
			Map hmActualHours = new HashMap();

			// String []strPayCycleDates = new
			// CommonFunctions().getCurrentPayCycle(strTimeZone);
			String[] strPayCycleDates = null;

			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			}

			log.debug("Start ===> " + strPayCycleDates[0]);
			log.debug("End ===> " + strPayCycleDates[1]);

			pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				_allDates.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();

			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || 
					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) ||
					strUserType.equalsIgnoreCase(HRMANAGER) )) {
				
				pst = con.prepareStatement(selectEmployeeRosterHours);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				pst = con.prepareStatement(selectEmployeeRosterHoursManager);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			}else{
				return ACCESS_DENIED;
			}

			rs = pst.executeQuery();

			Map hm = new HashMap();

			while (rs.next()) {
				String strEmpId = rs.getString("emp_id");

				hm = (Map) hmRosterHours.get(strEmpId);
				if (hm == null) {
					hm = new HashMap();
				}
				hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), uF.formatIntoOneDecimal(rs.getDouble("roster_hours")));
				hmRosterHours.put(strEmpId, hm);

				if (empId != null && !empId.contains(strEmpId)) {
					empId.add(strEmpId);
				}
			}
			rs.close();
			pst.close();

			
			if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)){
				pst = con.prepareStatement(selectEmployeeActualHours);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				pst = con.prepareStatement(selectEmployeeActualHoursManager);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			}
			
			log.debug("for actual hours:::>"+pst);
			rs = pst.executeQuery();

			while (rs.next()) {
				String strEmpId = rs.getString("emp_id");

				hm = (Map) hmActualHours.get(strEmpId);
				if (hm == null) {
					hm = new HashMap();
				}
				hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), uF.formatIntoOneDecimal(rs.getDouble("hours_worked")));
				hmActualHours.put(strEmpId, hm);

				if (empId != null && !empId.contains(strEmpId)) {
					empId.add(strEmpId);
				}

			}
			rs.close();
			pst.close();
			request.setAttribute("paycycleDuration",CF.getStrPaycycleDuration());
			request.setAttribute("_allDates", _allDates);
			request.setAttribute("hmRosterHours", hmRosterHours);
			request.setAttribute("hmActualHours", hmActualHours);
			request.setAttribute("FROM", strPayCycleDates[0]);
			request.setAttribute("TO", strPayCycleDates[1]);
			request.setAttribute("empId", empId);
			request.setAttribute("hmEmpCodeName", CF.getEmpNameMap(con,strUserType, strSessionEmpId));

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

}
