package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

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
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BreakCard  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(BreakCard.class);
	
	
	public String execute() throws Exception {
 
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE); 
		 
		request.setAttribute(TITLE, TBreakCard);
		request.setAttribute(PAGE, "/jsp/leave/BreakCard.jsp");
		 
		
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		

		viewLeaveCard();

		return loadLeaveCard();

	}
	
	String empId;
	String paycycle;
	String strMonth;
	String strYear;
	String strPrevYear;
	String strWLocation;
	String department;
	String level;
	String service;
	
	String wLocation;
	String f_department;
	
	
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillWLocation> wLocationList;
	List<FillLevel> levelList;
	List<FillPayCycles> payCycleList; 
	List<FillOrganisation> orgList;
	String f_org;
	
	public String loadLeaveCard() {
		UtilityFunctions uF = new UtilityFunctions();
		
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 0, 2);
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		departmentList = new FillDepartment(request).fillDepartment();
		orgList = new FillOrganisation(request).fillOrganisation();
		serviceList = new FillServices(request).fillServices();
		levelList = new FillLevel(request).fillLevel();
		return LOAD;
	}
	
	
	public String viewLeaveCard() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		List reportListPrint = new ArrayList();
		try {
			
			String []strDate = null;
			if(getPaycycle()!=null){
				strDate = getPaycycle().split("-");
			}else{
//				strDate = CF.getCurrentPayCycle(con, CF.getStrTimeZone(), CF);
				strDate = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			
			
			
			Map<String, String> hmBreakType = CF.getBreakTypeMap(con);
			Map<String, String> hmBreakColour = new HashMap<String, String>(); 
			CF.getLeavesColour(con, hmBreakColour);
			
			
			

			pst = con.prepareStatement("select *, lr.emp_id as emp_id1, lr.break_type_id as break_type_id1 from break_register lr, (select min(_date) as mn_date, max(_date) as mx_date, emp_id, break_type_id from break_register where _date < ? group by emp_id, break_type_id ) lr2 where lr.emp_id = lr2.emp_id and lr.break_type_id = lr2.break_type_id and lr._date = lr2.mx_date and mx_date < ? order by emp_id1, break_type_id1, _date");
			pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate[0], DATE_FORMAT));
			rs = pst.executeQuery();
		
			Map<String, String> hmOpeningBalance = new HashMap<String, String>();
			Map<String, String> hmTakenPaid = new HashMap<String, String>();
			Map<String, String> hmTakenUnPaid = new HashMap<String, String>();
			while(rs.next()){
				hmOpeningBalance.put(rs.getString("emp_id1")+"_"+rs.getString("break_type_id1"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
			
			/*
			pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register where _date between ? and ? group by leave_type_id, emp_id, is_paid");
			pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
			rs = pst.executeQuery();
		
			System.out.println("pst= 1 ==>"+pst);
			
			while(rs.next()){
				if(uF.parseToBoolean(rs.getString("is_paid"))){
					hmTakenPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
				}else{
					hmTakenUnPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
				}
			}*/
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select *, lr1.emp_id as emp_id1, lr1.break_type_id as break_type_id1 from break_register lr1,(select min(_date) as mn_date, max(_date) as mx_date, emp_id, break_type_id from break_register where _date >= ? and _date <= ? group by emp_id, break_type_id ) lr2 where  lr1.emp_id = lr2.emp_id and lr1.break_type_id = lr2.break_type_id and lr1._date = lr2.mx_date and mx_date >= ? and mx_date <= ? ");
			if(uF.parseToInt(getwLocation())>0){
				sbQuery.append(" and lr1.emp_id in (select emp_id from employee_official_details where wlocation_id = "+uF.parseToInt(getwLocation())+")");
			}
			
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and lr1.emp_id in (select emp_id from employee_official_details where depart_id = "+uF.parseToInt(getF_department())+")");
			}
			sbQuery.append(" order by emp_id1, break_type_id1, _date");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strDate[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate[1], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strDate[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strDate[1], DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
			
				List<String> alInner = new ArrayList<String>();
				
				
				double dblClosing = uF.parseToDouble(rs.getString("balance"));
				double dblTaken = uF.parseToDouble(rs.getString("taken_paid"));
				
				alInner.add(hmEmpCode.get(rs.getString("emp_id")));
				alInner.add(hmEmpName.get(rs.getString("emp_id")));
				alInner.add(hmBreakColour.get(rs.getString("break_type_id1")));
				alInner.add(uF.showData(hmBreakType.get(rs.getString("break_type_id")), ""));
				alInner.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("taken_paid"));
				alInner.add(rs.getString("taken_unpaid"));
//				alInner.add(uF.showData(hmOpeningBalance.get(rs.getString("emp_id1")+"_"+rs.getString("break_type_id1")), "-"));
				alInner.add((dblClosing + dblTaken)+"");
				alInner.add(uF.showData(rs.getString("balance"), "0"));
				reportListPrint.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportListPrint", reportListPrint);
			
			
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


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getEmpId() {
		return empId;
	}


	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getStrPrevYear() {
		return strPrevYear;
	}
	public void setStrPrevYear(String strPrevYear) {
		this.strPrevYear = strPrevYear;
	}
	public String getwLocation() {
		return wLocation;
	}
	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}
	public String getF_department() {
		return f_department;
	}
	public void setF_department(String f_department) {
		this.f_department = f_department;
	}
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}
	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}
	public String getF_org() {
		return f_org;
	}
	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

}
