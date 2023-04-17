package com.konnect.jpms.leave;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillHalfDaySession;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeCompLeaveEntry extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;
	String strSWlocationId = null;
	
	String isCompensate;
	String isWorkFromHome;
	String policy_id; 

		
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, PEmployeeLeaveEntry);
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);

		String strIsHalfDayLeave = ""+CF.getIsHalfDayLeave();
		request.setAttribute("strIsHalfDayLeave", strIsHalfDayLeave);
		
		getLeaveDocumentCondition();
		
		
		String strEdit = request.getParameter("E");
		String strDelete = request.getParameter("D");
		if (strEdit != null) {			
			viewLeaveEntry(strEdit);
			request.setAttribute(TITLE, TViewEmployeeLeaveEntry);
			return SUCCESS;
		}
		
		if (strDelete != null) {			
			deleteLeaveEntry(strDelete);
			request.setAttribute(TITLE, TDeleteEmployeeLeaveEntry);
			return VIEW;
		}
		
		if (getLeaveId() != null && getLeaveId().length() > 0) {
			updateLeaveEntry();
			request.setAttribute(TITLE, TEditEmployeeLeaveEntry);
			return UPDATE;
		} else if (getEmpId() != null && getEmpId().length() > 0 && getReason()!=null){
			insertLeaveEntry();
			request.setAttribute(TITLE, TAddEmployeeLeaveEntry);
			
			if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE) && !strUserType.equalsIgnoreCase(ARTICLE) && !strUserType.equalsIgnoreCase(CONSULTANT)){
				return VIEW;
			}else{
				return UPDATE;
			}			
		}	
			
			
			return loadLeaveEntry();
	}	

	public String loadLaVidateLeaveEntry() {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PEmployeeLeaveEntry);
		request.setAttribute(TITLE, TAddEmployeeLeaveEntry);
		session = request.getSession();
		
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strSWlocationId = (String)session.getAttribute(WLOCATIONID);
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			userTypeList = new FillUserType(request).fillUserType();
//			System.out.println("getIsWorkFromHome() ===>> " + getIsWorkFromHome());
			if(uF.parseToBoolean(getIsWorkFromHome())) {
				if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
					leaveTypeList = new FillLeaveType(request).fillWorkFromHome(uF.parseToInt(strSessionOrgId));
				} else {
					leaveTypeList = new FillLeaveType(request).fillWorkFromHomeLeave(uF.parseToInt(hmEmpLevelMap.get(strSessionEmpId)), uF.parseToInt(strSessionEmpId), uF.parseToInt(strSessionOrgId), uF.parseToInt(strSWlocationId));
				}
			} else {
				if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
					leaveTypeList = new FillLeaveType(request).fillCompLeave(uF.parseToInt(strSessionOrgId));
				} else {
					leaveTypeList = new FillLeaveType(request).fillCompLeave(uF.parseToInt(hmEmpLevelMap.get(strSessionEmpId)), uF.parseToInt(strSessionEmpId), uF.parseToInt(strSessionOrgId), uF.parseToInt(strSWlocationId));
				}
			}
			empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return LOAD;
	}

	
	public void getLeaveDocumentCondition() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLeaveDoumentPolicy);
			pst.setBoolean(1, true);
			rs = pst.executeQuery();
			
			StringBuilder sbDocumentCondition = new StringBuilder();
			int count = 0;
			while(rs.next()){
				if(count++>0){
					sbDocumentCondition.append(" || ");	
				}
				sbDocumentCondition.append("id == '"+rs.getString("leave_type_id")+"'");
			}
			rs.close();
			pst.close();
			
			if(count==0){
				sbDocumentCondition.append("false");
			}

			request.setAttribute("idDocCondition", sbDocumentCondition.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String loadLeaveEntry() {
		request.setAttribute(PAGE, PEmployeeLeaveEntry);
		request.setAttribute(TITLE, TAddEmployeeLeaveEntry);
		
		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
		
		UtilityFunctions uF = new UtilityFunctions();
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

	public String insertLeaveEntry() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			String fileName = null;
			if(getRequiredDocFileName()!=null){
				fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getRequiredDoc(), getRequiredDocFileName());
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmLeaveType = CF.getLeaveTypeCode(con);
			pst = con.prepareStatement("select * from emp_leave_type where level_id = (select level_id from  designation_details dd, grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and leave_type_id = ?");
			pst.setInt	(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			rs = pst.executeQuery();
			boolean isApproval = false;
			while(rs.next()){
				isApproval = uF.parseToBoolean(rs.getString("is_approval"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(insertEmployeeLeaveEntry);
			pst.setInt	(1, uF.parseToInt(getEmpId()));
			if(getIsHalfDay()){
				pst.setDate	(2, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate	(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
			}else{
				pst.setDate	(2, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate	(3, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
			}
			pst.setDate	(4, uF.getDateFormat(getEntrydate(), DATE_FORMAT));
			int nAppliedDays = 0;
			if(getIsHalfDay()){
				pst.setDouble	(5, 0.5);
			}else{
				nAppliedDays = uF.parseToInt(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone()));
				pst.setInt	(5, nAppliedDays);
			}
			
			pst.setInt	(6, uF.parseToInt(getTypeOfLeave()));
			pst.setString(7, getReason());
			if(getIsHalfDay()){
				pst.setDate	(8, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate	(9, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
			}else{
				pst.setDate	(8, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate	(9, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
			}
			
			pst.setBoolean(10, getIsHalfDay());
			pst.setString(11, getStrSession());
			pst.setString(12, fileName);
			if(isApproval){
				pst.setInt	(13, 0);
			}else{
				pst.setInt	(13, 1);
			}
			pst.execute(); 
			pst.close();
			
			if(getIsHalfDay()){
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully applied for half day leave."+END);
			}else{
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully applied for "+uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(),DATE_FORMAT,CF.getStrTimeZone()) + " day(s) leave."+END);
			}
			
//			updateLeaveRegister();
			
			
			if(!isApproval){
				CF.updateLeaveRegister1(con, CF, uF, nAppliedDays, 0, getTypeOfLeave(), getEmpId());
			}
			
			/*String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_EMPLOYEE_LEAVE_REQUEST, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setStrEmpId(getEmpId());
//			nF.setStrHostAddress(request.getRemoteHost());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			if(getIsHalfDay()){
				nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveNoOfDays("0.5");
			}else{
				nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveNoOfDays(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT));
			}
			nF.setEmailTemplate(true);			
			nF.setStrEmpLeaveReason(getReason());
			nF.sendNotifications();*/
			
			
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_MANAGER_LEAVE_REQUEST, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setSupervisor(true);
//			nF.setStrHostAddress(request.getRemoteHost());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(getEmpId());
			if(getIsHalfDay()){
				nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveNoOfDays("0.5");	
			}else{
				nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveNoOfDays(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone()));
			}
			nF.setStrLeaveTypeName(uF.showData(hmLeaveType.get(getTypeOfLeave()), ""));
			nF.setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT_STR));
			nF.setEmailTemplate(true);
			nF.setStrEmpLeaveReason(getReason());
			nF.sendNotifications();
			request.setAttribute(MESSAGE, "Leave Applied Successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	public void updateLeaveRegister(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from leave_register where emp_id = ? and leave_type_id=? and from_date<=? and to_date>=? ");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			double dblTakenLeaves = 0;
			while(rs.next()){
				dblTakenLeaves = rs.getDouble("taken_leaves");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("update leave_register set taken_leaves=? where emp_id = ? and leave_type_id=? and from_date<=? and to_date>=? ");
			if(getIsHalfDay()){
				pst.setDouble(1, (dblTakenLeaves + 0.5 ));
			}else{
				pst.setDouble(1, (dblTakenLeaves + uF.parseToInt(uF.dateDifference(getLeaveFromTo(), DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone()))));
			}
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public String updateLeaveEntry() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateEmployeeLeaveEntry);
			if(getIsHalfDay()){
				pst.setDate	(1, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate	(2, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDouble	(3, 0.5);
			}else{
				pst.setDate	(1, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate	(2, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
				pst.setInt	(3, uF.parseToInt(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone())));
			}
			
			pst.setInt	(4, uF.parseToInt(getTypeOfLeave()));
			pst.setString(5, getReason());
			pst.setBoolean(6, getIsHalfDay());
			pst.setString(7, getStrSession());
			pst.setInt	(8, uF.parseToInt(getLeaveId()));
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM+"Leave updated successfully!"+END);
			
			updateLeaveRegister();
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	public String viewLeaveEntry(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmployeeLeaveEntry);
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				setLeaveId(rs.getString("leave_id"));
				setEmpName((String)session.getAttribute("EMPNAME"));
				setEmpId(rs.getString("emp_id"));
				setTypeOfLeave(rs.getString("leave_type_id"));
				setReason(rs.getString("reason"));
				setLeaveFromTo(uF.getDateFormat(rs.getString("leave_from"), DBDATE, DATE_FORMAT));
				setLeaveToDate(uF.getDateFormat(rs.getString("leave_to"), DBDATE, DATE_FORMAT));
				setIsapproved(rs.getInt("is_approved"));
				setUserId(rs.getString("user_id"));
				setEntrydate(uF.getDateFormat(rs.getString("entrydate"), DBDATE, DATE_FORMAT));
				setIsHalfDay(rs.getBoolean("ishalfday"));
				setStrSession(rs.getString("session_no"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		leaveTypeList = new FillLeaveType(request).fillLeave();
		userTypeList = new FillUserType(request).fillUserType();
		empList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
		return SUCCESS;

	}

	public String deleteLeaveEntry(String strDelete) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteEmployeeLeaveEntry);
			pst.setInt(1, uF.parseToInt(strDelete));
			pst.execute();
			pst.close();

			request.setAttribute(MESSAGE, "Deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
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
		
    
	public void validate() {
		
		
		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
		
		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		strSessionOrgId = (String)session.getAttribute(ORGID);
		strSWlocationId = (String)session.getAttribute(WLOCATIONID);
		
		setEmpName((String)session.getAttribute("EMPNAME"));
		
		if(getStrEmpId()==null) {
			setEmpId(strSessionEmpId);
		} else {
			setEmpId(getStrEmpId());
		}
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return;
		
		if (getUserId()!=null && uF.parseToInt(getUserId())== 0) {
            addFieldError("UserId", " UserId is required");
        } 
       
        if (getEmpId()!=null && uF.parseToInt(getEmpId()) == 0) {
            addFieldError("EmpId", " Please select an employee from the list");
        }
        
        if (getTypeOfLeave()!=null && uF.parseToInt(getTypeOfLeave())== 0) {
            addFieldError("TypeOfLeave", " Please select leave type");
        } 
        if (getReason()!=null && getReason().length() == 0) {
            addFieldError("Reason", "Please enter valid reason.");
        } 
         
        if (getLeaveFromTo()!=null && getLeaveFromTo().length() == 0) {
            addFieldError("LeaveFromTo", " LeaveFromTo is required");
        } 
        if (getLeaveToDate()!=null && getLeaveToDate().length() == 0) {
            addFieldError("LeaveToDate", " LeaveToDate is required");
        } 
        if (!getIsHalfDay() &&  getLeaveToDate()!=null && getLeaveFromTo()!=null && uF.getDateFormat(getLeaveToDate(),DATE_FORMAT).before(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT))) {
            addFieldError("FromBeforeTo", "Leave start date should be before leave end date.");
        } 
        
        Database db = new Database();
        db.setRequest(request);
        Connection con=null;
        PreparedStatement pst = null;
        ResultSet rs= null;
        
        try {
        	String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
        	con = db.makeConnection(con);
        	
        	Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
        	pst = con.prepareStatement("select * from leave_register where emp_id = ? and leave_type_id = ?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			rs = pst.executeQuery();
			
			double dblAccruedLeaves = 0;
			while(rs.next()){
				dblAccruedLeaves = rs.getDouble("accrued_leaves");
			}
			rs.close();
			pst.close();
			
			
        	pst = con.prepareStatement("select * from emp_leave_type where level_id = ?  and leave_type_id = ? order by emp_leave_type_id desc");
			pst.setInt(1, uF.parseToInt(hmEmpLevelMap.get(getEmpId())));
			pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			rs = pst.executeQuery();
			double dblMonthlyLimit = 0;
			double dblConsecutiveLimit = 0;
			boolean isMonthlyCarryForward = false;
			String strEffectiveDateType = null;
			while(rs.next()){
				dblMonthlyLimit = rs.getInt("monthly_limit");
				dblConsecutiveLimit = rs.getInt("consecutive_limit");
				isMonthlyCarryForward = rs.getBoolean("is_monthly_carryforward");
				strEffectiveDateType = rs.getString("effective_date_type");
			}
			rs.close();
			pst.close();
        	
			String strD1 = null;
			String strD2 = null;
			
			if(strEffectiveDateType!=null && strEffectiveDateType.equalsIgnoreCase("CY")){
				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				strD1 = "01/01/"+cal.get(Calendar.YEAR);
				strD2 = "31/12/"+cal.get(Calendar.YEAR);
			}else if(strEffectiveDateType!=null && strEffectiveDateType.equalsIgnoreCase("FY")){
				strD1 = strFinancialYearStart;
				strD2 = strFinancialYearEnd;
			}
			
			if(isMonthlyCarryForward){
				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nActualMinimum = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				int nActualMaximum = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				strD1 = nActualMinimum+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
				strD2 = nActualMaximum+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
				
				strD1 = uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT);
				strD2 = uF.getDateFormat(strD2, DATE_FORMAT, DATE_FORMAT);
			}
			
			
			Map hmLeaveDatesType = new HashMap();
			Map hmLeave = new HashMap();
//			hmLeave = CF.getLeaveDates(con, strD1, strD2, CF, hmLeaveDatesType, getStrEmpId(), getTypeOfLeave(), null);
			hmLeave = CF.getActualLeaveDates(con, CF, uF, getStrEmpId(), getTypeOfLeave(), strD1, strD2, hmLeaveDatesType, false, null);
        	
        	Map<String, String> hmTemp = (Map)hmLeaveDatesType.get(getStrEmpId());
        	if(hmTemp==null)hmTemp = new HashMap();
        	
        	Set set = hmTemp.keySet();
        	Iterator it = set.iterator();
        	double dblCount = 0;
        	while(it.hasNext()){
        		String strDate = (String)it.next();
        		String strTemp = (String)hmTemp.get(strDate);
        		if(strTemp!=null && strTemp.equalsIgnoreCase("F")){
        			dblCount = dblCount+1;
        		}else if(strTemp!=null && strTemp.equalsIgnoreCase("H")){
        			dblCount = dblCount+0.5;
        		}
        	}
        	
        	double dblLeaveApplied = 0; 
        	if(getIsHalfDay()){
        		dblLeaveApplied = 0.5;
        	}else{
        		dblLeaveApplied = uF.parseToInt(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone()));
        	}
			
			
			dblMonthlyLimit = dblMonthlyLimit - dblCount;
			double dblApplicableLeave = Math.min(dblMonthlyLimit, dblConsecutiveLimit);
			
        	/*
			if(uF.parseToInt(getEmpId())>0 && (dblApplicableLeave<dblLeaveApplied || dblApplicableLeave==0) && uF.parseToInt(getTypeOfLeave())>0){
				
				if(dblApplicableLeave==0){
					addFieldError("ExcessLeaveLimit", "You can not apply for any leave as you have finished the monthly limit.");
				}else{
					addFieldError("ExcessLeaveLimit", "You can not apply for more than "+dblApplicableLeave+" leaves.");
				}
			}*/
			
			dblAccruedLeaves = dblAccruedLeaves - dblCount;
			/*
			if(uF.parseToInt(getStrEmpId())>0 && dblLeaveApplied>dblAccruedLeaves){
				addFieldError("ExcessLeaveLimit", "You have only "+dblAccruedLeaves+" available leaves.<br/>" +
						"You can not apply for more than "+dblAccruedLeaves+" leaves.");
			}
			 */  
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

        loadLaVidateLeaveEntry();
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

	public String getIsWorkFromHome() {
		return isWorkFromHome;
	}

	public void setIsWorkFromHome(String isWorkFromHome) {
		this.isWorkFromHome = isWorkFromHome;
	}
	
}