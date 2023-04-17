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

public class LateClockEntries extends ActionSupport implements ServletRequestAware, IStatements {

	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(LateClockEntries.class);

	public String execute() throws Exception { 
 
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		

		request.setAttribute(TITLE, TViewRoster);
		isEmpUserType = false;

		
		
		request.setAttribute(PAGE, PReportLateClockEntries);
		String str = lateClockEntries();
		
		if(str.equalsIgnoreCase(ACCESS_DENIED)){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}else{
			return loadLateClockEntries();
		}
	}

	public String loadLateClockEntries() {
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF);
		
		return LOAD;
	}

	public String lateClockEntries() {

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
			List<String> _allDays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String,String> hmOutLateCount = new HashMap<String, String>();
			Map<String, String> hmInLateCount = new HashMap<String, String>();

			String[] strPayCycleDates = null;

			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF, request);
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
				_allDays.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));
			}
			rs.close();
			pst.close();
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || 
					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) ||
					strUserType.equalsIgnoreCase(HRMANAGER) )) {
				
				pst = con.prepareStatement(selectLateHours);
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			}else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
				pst = con.prepareStatement(selectLateHoursManager);
				pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			}else{
				return ACCESS_DENIED;
			}
			
			
			rs = pst.executeQuery();
			
			while(rs.next()){
				if(rs.getString("in_out").equalsIgnoreCase("IN")){
					hmInLateCount.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), rs.getString("latecount"));
				}else{
					hmOutLateCount.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), rs.getString("latecount"));
				}
			}
			rs.close();
			pst.close();
			
			

			request.setAttribute("_allDates",_allDates);
			request.setAttribute("_allDays",_allDays);
			request.setAttribute("hmInLateCount",hmInLateCount);
			request.setAttribute("hmOutLateCount",hmOutLateCount);
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
