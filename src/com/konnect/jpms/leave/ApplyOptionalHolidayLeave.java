package com.konnect.jpms.leave;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillHalfDaySession;
import com.konnect.jpms.select.FillHoliday;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApplyOptionalHolidayLeave extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;
	String strSWlocationId = null;
	
	String leaveId;
	
	String userId;
	String empId; 
	String strEmpId;
	String empName;
	String reason;
	String managerReason;
	String empNoOfLeave;
	String typeOfLeave;
	String leaveFromTo;
	String leaveToDate;
	String approveLeaveFromTo;
	String approveLeaveToDate;
	String entrydate;
	
	File requiredDoc;
	String requiredDocFileName;
	
	boolean isHalfDay;
	String strSession;
	
	int isapproved=0;
	
	List<FillHalfDaySession> strWorkingSession;

	
	List<FillUserType> userTypeList;
	List<FillLeaveType> leaveTypeList;
	List<FillEmployee> empList;
	
	String isCompensate;
	String policy_id; 
	String isOptHolidayLeave;
	
	String strHolidayDate;
	List<FillHoliday> optHolidayList;
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, PEmployeeLeaveEntry);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strSWlocationId = (String)session.getAttribute(WLOCATIONID);
		UtilityFunctions uF = new UtilityFunctions();
		
		loadLaVidateLeaveEntry(uF);
			
		return loadLeaveEntry(uF);
	}	

	public String loadLaVidateLeaveEntry(UtilityFunctions uF) {
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			String strCalendarYearStart = strCalendarYearDates[0];
			String strCalendarYearEnd = strCalendarYearDates[1];
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			userTypeList = new FillUserType(request).fillUserType(); 
			if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)){
				leaveTypeList = new FillLeaveType(request).fillOptionalHolidayLeave(uF.parseToInt(strSessionOrgId));
			}else{
				leaveTypeList = new FillLeaveType(request).fillOptionalHolidayLeave(uF.parseToInt(hmEmpLevelMap.get(strSessionEmpId)), uF.parseToInt(strSessionEmpId),uF.parseToInt(strSessionOrgId), uF.parseToInt(strSWlocationId));
			}
					
			empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
			
			optHolidayList = new FillHoliday(request).fillOptionalHolidays(strSessionOrgId, strSWlocationId, strCalendarYearStart, strCalendarYearEnd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return LOAD;
	}

	public String loadLeaveEntry(UtilityFunctions uF) {
		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
		setLeaveId("");
		setEmpId(null);
		setEmpName((String)session.getAttribute("EMPNAME"));
		setEmpNoOfLeave("");
		setTypeOfLeave("");
		setReason("");
		setManagerReason("");
		setUserId("");
		setEntrydate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		setLeaveFromTo(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		setLeaveToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		return LOAD;
	}

	public String getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(String leaveId) {
		this.leaveId = leaveId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getManagerReason() {
		return managerReason;
	}
	public void setManagerReason(String managerReason) {
		this.managerReason = managerReason;
	}
	public String getEmpNoOfLeave() {
		return empNoOfLeave;
	}
	public void setEmpNoOfLeave(String empNoOfLeave) {
		this.empNoOfLeave = empNoOfLeave;
	}
	public String getTypeOfLeave() {
		return typeOfLeave;
	}
	public void setTypeOfLeave(String typeOfLeave) {
		this.typeOfLeave = typeOfLeave;
	}
	public String getLeaveFromTo() {
		return leaveFromTo;
	}
	public void setLeaveFromTo(String leaveFromTo) {
		this.leaveFromTo = leaveFromTo;
	}
	public String getLeaveToDate() {
		return leaveToDate;
	}
	public void setLeaveToDate(String leaveToDate) {
		this.leaveToDate = leaveToDate;
	}
	public String getApproveLeaveFromTo() {
		return approveLeaveFromTo;
	}

	public void setApproveLeaveFromTo(String approveLeaveFromTo) {
		this.approveLeaveFromTo = approveLeaveFromTo;
	}

	public String getApproveLeaveToDate() {
		return approveLeaveToDate;
	}

	public void setApproveLeaveToDate(String approveLeaveToDate) {
		this.approveLeaveToDate = approveLeaveToDate;
	}
	public String getEntrydate() {
		return entrydate;
	}

	public void setEntrydate(String entrydate) {
		this.entrydate = entrydate;
	}
	public int isIsapproved() {
		return isapproved;
	}
	public void setIsapproved(int isapproved) {
		this.isapproved = isapproved;
	}
	
	public List<FillUserType> getUserTypeList() {
		return userTypeList;
	}
	public void setUserTypeList(List<FillUserType> userTypeList) {
		this.userTypeList = userTypeList;
	}
	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}
	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public List<FillHalfDaySession> getStrWorkingSession() {
		return strWorkingSession;
	}

	public String getStrSession() {
		return strSession;
	}

	public void setStrSession(String strSession) {
		this.strSession = strSession;
	}

	public boolean getIsHalfDay() {
		return isHalfDay;
	}

	public void setIsHalfDay(boolean isHalfDay) {
		this.isHalfDay = isHalfDay;
	}

	public void setStrWorkingSession(List<FillHalfDaySession> strWorkingSession) {
		this.strWorkingSession = strWorkingSession;
	}

	public File getRequiredDoc() {
		return requiredDoc;
	}

	public void setRequiredDoc(File requiredDoc) {
		this.requiredDoc = requiredDoc;
	}

	public String getRequiredDocFileName() {
		return requiredDocFileName;
	}

	public void setRequiredDocFileName(String requiredDocFileName) {
		this.requiredDocFileName = requiredDocFileName;
	}

	public String getIsCompensate() {
		return isCompensate;
	}

	public void setIsCompensate(String isCompensate) {
		this.isCompensate = isCompensate;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public String getIsOptHolidayLeave() {
		return isOptHolidayLeave;
	}

	public void setIsOptHolidayLeave(String isOptHolidayLeave) {
		this.isOptHolidayLeave = isOptHolidayLeave;
	}

	public List<FillHoliday> getOptHolidayList() {
		return optHolidayList;
	}

	public void setOptHolidayList(List<FillHoliday> optHolidayList) {
		this.optHolidayList = optHolidayList;
	}

	public String getStrHolidayDate() {
		return strHolidayDate;
	}

	public void setStrHolidayDate(String strHolidayDate) {
		this.strHolidayDate = strHolidayDate;
	}
	
	
}
