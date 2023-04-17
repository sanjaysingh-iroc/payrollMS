package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.zkoss.util.logging.Log;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ExtraWork extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	String strUserType;
	
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ExtraWork.class);
	
	public String execute() throws Exception {

		UtilityFunctions uF = new UtilityFunctions();
		
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strEmpID = (String)session.getAttribute("EMPID");
		strUserType = (String)session.getAttribute(USERTYPE);
		
		
		request.setAttribute(PAGE, PExtraWork);
		request.setAttribute(TITLE, TExtraWork);

		String date = request.getParameter("DATE");
		String strDate = request.getParameter("strDate");

		
		viewEmployeeHours(date, strDate);
		return loadExtraWork(uF);
	}

	
	String paycycle;
	String strWLocation;
	String department;
	String service;
	String strMonth;
	String strYear;
	
	
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillPayCycles> payCycleList; 
	List<FillMonth> monthList;
	List<FillYears> yearList;
	
	
	public String loadExtraWork(UtilityFunctions uF) {
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF);
		wLocationList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		serviceList = new FillServices(request).fillServices();
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		
		return LOAD;
	}

	public String viewEmployeeHours(String date, String strDate) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		List al = new ArrayList();
		
		try {

			con = db.makeConnection(con);
			
			

			String strNewEmpId = null;
			String strOldEmpId = null;

			
			
			
			
			
			if(getStrMonth() ==null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
				setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
			}
			
			
			
			List alDates = new ArrayList();
			
			
			Calendar cal = GregorianCalendar.getInstance();
			
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			
			
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			
			String strD1 = null;
			for(int i=0; i<maxDays; i++){
				
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);
			}
			
			
			
			
			
			List alInner = new ArrayList();
			List alReport = new ArrayList();

			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			
			
			Map<String, String> hmEmpHours = new HashMap<String, String>();
			List<String> alEmp = new ArrayList<String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select hours_worked, actual_hours, ad.emp_id, _date from attendance_details ad, roster_details rd where rd.emp_id=ad.emp_id and rd.service_id = ad.service_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and in_out = 'OUT'");
			
			sbQuery.append(" and _date between '"+uF.getDateFormat((String)alDates.get(0), DATE_FORMAT)+"' and '"+uF.getDateFormat((String)alDates.get(alDates.size()-1), DATE_FORMAT)+"'");
			
			sbQuery.append(" order by ad.emp_id, rd._date");
			
			pst = con.prepareStatement(sbQuery.toString());
			
			log.debug(request.getContextPath()+ " : "+"alDates===>"+alDates);
			log.debug(request.getContextPath()+ " : "+"alDates===>"+uF.getDateFormat((String)alDates.get(0), DATE_FORMAT));
			log.debug(request.getContextPath()+ " : "+"pst==="+pst);
			
			rs = pst.executeQuery();
			while (rs.next()) {
			
				double dblHours = uF.parseToDouble(rs.getString("hours_worked")) - uF.parseToDouble(rs.getString("actual_hours"));
				
				if(dblHours>0){
					hmEmpHours.put(rs.getString("emp_id") +"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), uF.formatIntoTwoDecimal(dblHours));
					
					if(!alEmp.contains(rs.getString("emp_id"))){
						alEmp.add(rs.getString("emp_id"));
					}
				}
				
				
			}
			rs.close();
			pst.close();

			
			for(int i=0; i<alEmp.size(); i++){
				alInner = new ArrayList();
				
				alInner.add(hmEmpName.get((String)alEmp.get(i)));
				for(int k=0; k<alDates.size(); k++){
					alInner.add(uF.showData(hmEmpHours.get((String)alEmp.get(i)+"_"+(String)alDates.get(k)), "0"));	
				}
				alReport.add(alInner);
			}
			
			
			
			
			request.setAttribute("alReport", alReport);
			request.setAttribute("alDates", alDates);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(request.getContextPath()+ " : "+ e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		request.setAttribute("reportList", al);

		return SUCCESS;

	}

	

	String[] strEmpId;
	String[] strEmpIN;
	String[] strEmpOUT;
	String strDate;

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

	public String getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
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

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
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

}
