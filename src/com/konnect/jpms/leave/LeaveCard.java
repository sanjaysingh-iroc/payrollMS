package com.konnect.jpms.leave;

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

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
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

public class LeaveCard  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(LeaveCard.class);
	
	
	public String execute() throws Exception {
  
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, TLeaveCard);
		request.setAttribute(PAGE, "/jsp/leave/LeaveCard.jsp");
		 
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
	
	
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillWLocation> wLocationList;
	List<FillLevel> levelList;
	List<FillPayCycles> payCycleList; 
	
	public String loadLeaveCard() {
		UtilityFunctions uF = new UtilityFunctions();
		
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF);
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 0, 2);
		wLocationList = new FillWLocation(request).fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment();
		serviceList = new FillServices(request).fillServices();
		levelList = new FillLevel(request).fillLevel();
		return LOAD;
	}
	
	
	/***
	 * This function calculates the leave card of an employee for the specific year.
	 * It also displays the information of leaves for the previous year, along with the current year. 
	 * The information displayed are Opening Leave Balances, Closing Leave Balances, Paid Leaves, Unpaid Leaves, Employee Code, Employee Name 
	 * @return String
	 * @param none
	 */

	
	public String viewLeaveCard() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		List reportListPrint = new ArrayList();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmDept = CF.getDeptMap(con);
			
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id =?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			
			int nGradeId = 0;
			int nLocationId = 0;
			Map<String, String> hmEmpDetails = new HashMap<String, String>();
			while(rs.next()){
				
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpDetails.put("EMP_NAME", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmEmpDetails.put("EMP_DEPT", hmDept.get(rs.getString("depart_id")));
				nGradeId = uF.parseToInt(rs.getString("grade_id"));
				nLocationId = uF.parseToInt(rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();
			
			
			
			Map<String, String> hmLeaveType = CF.getLeaveTypeMap(con);
			
			pst = con.prepareStatement("select * from (select * from emp_leave_entry where emp_id = ? and leave_type_id>0 and is_approved = 1 ) ele left join leave_register1 lr on lr.emp_id = ele.emp_id and ele.entrydate = lr._date and ele.leave_type_id = lr.leave_type_id where ele.emp_id = ? order by entrydate desc, ele.leave_type_id");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
			
				List<String> alInner = new ArrayList<String>();
				
				
				alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(((uF.parseToBoolean(rs.getString("ishalfday")))?"Yes":"No"));
				alInner.add(hmLeaveType.get(rs.getString("leave_type_id")));
				alInner.add(rs.getString("reason"));
				alInner.add(uF.showData(rs.getString("emp_no_of_leave"), "0")  +  ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Modified\" class=\"leftearly\">&nbsp;</div>":""));
				alInner.add(uF.showData(rs.getString("balance"), "0"));
				alInner.add(rs.getString("manager_reason"));
				
				
				reportListPrint.add(alInner);
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("reportListPrint", reportListPrint);
			request.setAttribute("hmEmpDetails", hmEmpDetails);
			
			
			if(getStrYear()==null){
				setStrYear(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"));
			}
			int nPrevYear = uF.parseToInt(getStrYear()) - 1; 
			setStrPrevYear(nPrevYear+"");
			setStrYear(getStrYear()+"");
			
			
			Map<String, String> hmLeaveMap = CF.getLeaveTypeMap(con);
			
			pst = con.prepareStatement("select * from leave_register1 lr, (select max(_date) as max_date, leave_type_id from leave_register1 where emp_id = ? and _date between ? and ? group by leave_type_id ) lr1 where lr._date = lr1.max_date and lr.leave_type_id = lr1.leave_type_id and emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setDate(2, uF.getDateFormat(nPrevYear+"-01-01", DBDATE));
			pst.setDate(3, uF.getDateFormat(nPrevYear+"-12-31", DBDATE));
			pst.setInt(4, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			
			List<String> alPrevLeaveDetails = new ArrayList<String>();
			
			while(rs.next()){
				alPrevLeaveDetails.add(hmLeaveMap.get(rs.getString("leave_type_id")));
				alPrevLeaveDetails.add(rs.getString("balance"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alPrevLeaveDetails", alPrevLeaveDetails);
			
			pst = con.prepareStatement("select * from emp_leave_type where effective_date between ? and ? and level_id = (select ld.level_id from level_details ld, designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and gd.grade_id = ? ) and wlocation_id  = ?");
			pst.setDate(1, uF.getDateFormat(getStrYear()+"-01-01", DBDATE));
			pst.setDate(2, uF.getDateFormat(getStrYear()+"-12-31", DBDATE));
			pst.setInt(3, nGradeId);
			pst.setInt(4, nLocationId);
			rs = pst.executeQuery();
			List<String> alLeaveEntitlementDetails = new ArrayList<String>();
//			System.out.println("pst===>"+pst);
			while(rs.next()){
				alLeaveEntitlementDetails.add(hmLeaveMap.get(rs.getString("leave_type_id")));
				alLeaveEntitlementDetails.add(rs.getString("no_of_leave"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alLeaveEntitlementDetails", alLeaveEntitlementDetails);
			
			pst = con.prepareStatement("select * from leave_register1 lr, (select max(_date) as max_date, leave_type_id from leave_register1 where emp_id = ? and _date between ? and ? group by leave_type_id ) lr1 where lr._date = lr1.max_date and lr.leave_type_id = lr1.leave_type_id and emp_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrYear()+"-01-01", DBDATE));
			pst.setDate(3, uF.getDateFormat(getStrYear()+"-12-31", DBDATE));
			pst.setInt(4, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			List<String> alLeaveDetails = new ArrayList<String>();
			while(rs.next()){
				alLeaveDetails.add(hmLeaveMap.get(rs.getString("leave_type_id")));
				alLeaveDetails.add(rs.getString("balance"));
				alLeaveDetails.add(rs.getString("accrued"));
				alLeaveDetails.add(rs.getString("taken_paid"));
				alLeaveDetails.add(rs.getString("taken_unpaid"));
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alLeaveDetails", alLeaveDetails);
			
//			System.out.println("alLeaveEntitlementDetails===>"+alLeaveEntitlementDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
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

}
