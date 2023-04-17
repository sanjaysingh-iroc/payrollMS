package com.konnect.jpms.leave;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeLeaveEntry extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;
	String strSessionOrgId = null;
	String strSWlocationId = null;

	private String policy_id; 
	
	private String isCompensate;
	private String isWorkFromHome;
	private String isConstant;
	private String type;
	
	
	private String strHolidayDate;
	private String isOptHolidayLeave;
	
	private String leaveId;
	
	private String userId;
	private String empId; 
	private String strEmpId;
	private String empName;
	private String reason;
	private String managerReason;
	private String empNoOfLeave;
	private String typeOfLeave;
	private String leaveFromTo;
	private String leaveToDate;
	private String extraWorkingFromTime;
	private String extraWorkingToTime;
	
	
	private String strCurrDate;
	private String approveLeaveFromTo;
	private String approveLeaveToDate;
	private String entrydate;
	
	private File requiredDoc;
	private String requiredDocFileName;
	
	private boolean isHalfDay;
	private String strSession;
	
	private int isapproved=0;
	
	private List<FillHalfDaySession> strWorkingSession;

	
	private List<FillUserType> userTypeList;
	private List<FillLeaveType> leaveTypeList;
	private List<FillEmployee> empList;
	
	private String strPaycycle;
	
	private String empRelation;		//added by parvez date: 27-09-2022
	private String backupEmp;		//added by parvez date: 18-03-2023
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, PEmployeeLeaveEntry);
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		
		String strIsHalfDayLeave = ""+CF.getIsHalfDayLeave();
		request.setAttribute("strIsHalfDayLeave", strIsHalfDayLeave);
		
		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
		request.setAttribute("hmFeatureStatus", hmFeatureStatus);
//		System.out.print("execute leave");
		getLeaveDocumentCondition();
		
		if(uF.parseToBoolean(getIsOptHolidayLeave())) {
			setLeaveFromTo(getStrHolidayDate());
			setLeaveToDate(getStrHolidayDate());
		}
		
		String strEdit = request.getParameter("E");
		String strDelete = request.getParameter("D");
//		System.out.println("strEdit::"+strEdit);
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
		
//		System.out.println("getType() ===>> " + getType());
		if (getLeaveId() != null && getLeaveId().length() > 0) {
			//updateLeaveEntry();
			request.setAttribute(TITLE, TEditEmployeeLeaveEntry);
			return UPDATE;
		} else if (getEmpId() != null && getEmpId().length() > 0 && getReason()!=null) {
//			System.out.print("insert leave");
			insertLeaveEntry();
			request.setAttribute(TITLE, TAddEmployeeLeaveEntry);
			
			/*if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE) && !strUserType.equalsIgnoreCase(ARTICLE) && !strUserType.equalsIgnoreCase(CONSULTANT)) {
				System.out.println("in view ");
				return VIEW;
			} else {
				System.out.println("in update ");
				return UPDATE;
			}		*/
//			System.out.println("in if getType() ===>> " + getType());
			if(getType()!=null && getType().equals("timesheet")) {
				return "updatesucess";
			} else if(strUserType != null && strUserType.equals(EMPLOYEE)) {
				return VIEW;
			} else {
				return UPDATE;
			}
		}
//		System.out.println("retutn ---- load ");
		return loadLeaveEntry();
	}	

	public String loadLaVidateLeaveEntry() {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PEmployeeLeaveEntry);
		request.setAttribute(TITLE, TAddEmployeeLeaveEntry);
		session = request.getSession();
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			

			strSessionOrgId = (String)session.getAttribute(ORGID);
			strSWlocationId = (String)session.getAttribute(WLOCATIONID);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con); 
			
			
			userTypeList = new FillUserType(request).fillUserType();
			
			Map<String, String> hmEmpGenderMap =CF.getEmpGenderMap(con);
			String gender=hmEmpGenderMap.get(strSessionEmpId);
			
			if(getType()!=null && getType().equals("timesheet")) {
				if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
//					System.out.println("strSessionOrgId::"+strUserType);

					leaveTypeList = new FillLeaveType(request).fillLeaveWithoutCompensetary(uF.parseToInt(strSessionOrgId),uF.parseToBoolean(getIsConstant()),uF.getCurrentDate(CF.getStrTimeZone()));
				} else {
//					System.out.println("else strSessionOrgId::"+strUserType);
					boolean flag=CF.getMaternityFrequency(con,request,uF.parseToInt(hmEmpLevelMap.get(strSessionEmpId)), uF.parseToInt(strSessionEmpId),uF.parseToInt(strSessionOrgId), uF.parseToInt(strSWlocationId));
					leaveTypeList = new FillLeaveType(request).fillLeaveWithoutCompensetary(gender!=null && gender.equalsIgnoreCase("M")?true:false,flag,uF.parseToInt(hmEmpLevelMap.get(strSessionEmpId)), uF.parseToInt(strSessionEmpId),uF.parseToInt(strSessionOrgId), uF.parseToInt(strSWlocationId),uF.getCurrentDate(CF.getStrTimeZone()),uF.parseToBoolean(getIsConstant()));
				}
			} else {
//				System.out.println("In else::");
				if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
					leaveTypeList = new FillLeaveType(request).fillLeaveWithoutCompensetary(uF.parseToInt(strSessionOrgId));
				} else {
					boolean flag=CF.getMaternityFrequency(con,request,uF.parseToInt(hmEmpLevelMap.get(strSessionEmpId)), uF.parseToInt(strSessionEmpId),uF.parseToInt(strSessionOrgId), uF.parseToInt(strSWlocationId));
					leaveTypeList = new FillLeaveType(request).fillLeaveWithoutCompensetary(gender!=null && gender.equalsIgnoreCase("M")?true:false,flag,uF.parseToInt(hmEmpLevelMap.get(strSessionEmpId)), uF.parseToInt(strSessionEmpId),uF.parseToInt(strSessionOrgId), uF.parseToInt(strSWlocationId),uF.getCurrentDate(CF.getStrTimeZone()));
				}
			}
	//		System.out.println("leaveTypeList::"+leaveTypeList);
//			empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId);
			if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
				empList = new FillEmployee(request).fillEmployeeName(strUserType, null, session);
			} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(CEO))) {
				empList = new FillEmployee(request).fillEmployeeNameByAccess(null, (String)session.getAttribute(ORG_ACCESS),null, (String)session.getAttribute(WLOCATION_ACCESS),strUserType,false);
			} else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
			} else {
				empList = new ArrayList<FillEmployee>();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	
	public void getLeaveDocumentCondition() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
//			setStrCurrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_MM_DD_YYYY));
			pst = con.prepareStatement(selectLeaveDoumentPolicy);
			pst.setBoolean(1, true);
			ResultSet rs = pst.executeQuery();
			StringBuilder sbDocumentCondition = new StringBuilder();
			int count = 0;
			while(rs.next()) {
				if(count++>0) {
					sbDocumentCondition.append(" || ");	
				}
				sbDocumentCondition.append("id == '"+rs.getString("leave_type_id")+"'");
			}
			rs.close();
			pst.close();
			
			if(count==0) {
				sbDocumentCondition.append("false");
			}
			request.setAttribute("idDocCondition", sbDocumentCondition.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
		setStrCurrDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_MM_DD_YYYY));
		
		return LOAD;
	}

	
	public String insertLeaveEntry() {

//		System.out.println("from time:"+getExtraWorkingFromTime()+"To time:"+getExtraWorkingToTime());
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			String fileName = null;
			System.out.println("getRequiredDocFileName()=="+getRequiredDocFileName());
			if(getRequiredDocFileName()!=null && CF.getStrDocSaveLocation()!=null) {
				fileName = uF.uploadFile(request, CF.getStrDocSaveLocation(), getRequiredDoc(), getRequiredDocFileName(), CF.getIsRemoteLocation(), CF);
			}else if(getRequiredDocFileName()!=null) {
				fileName = uF.uploadFile(request, DOCUMENT_LOCATION, getRequiredDoc(), getRequiredDocFileName(), CF.getIsRemoteLocation(), CF);
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmLeaveType = CF.getLeaveTypeCode(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String,String> hmEmpLevel = CF.getEmpLevelMap(con);
			String strWlocationId = CF.getEmpWlocationId(con, uF, getEmpId());
			
			pst = con.prepareStatement("select * from emp_leave_type where level_id=(select level_id from designation_details dd," +
					"grades_details gd where gd.designation_id = dd.designation_id and gd.grade_id = (select grade_id from " +
					"employee_official_details where emp_id = ?)) and leave_type_id = ? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			pst.setInt(3, uF.parseToInt(strWlocationId));
			rs = pst.executeQuery();
			boolean isApproval = false;
			boolean isPaid = false;
			while(rs.next()) {
				isApproval = uF.parseToBoolean(rs.getString("is_approval"));
				isPaid = uF.parseToBoolean(rs.getString("is_paid"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement(insertEmployeeLeaveEntry);
			StringBuilder sbQuery = new StringBuilder();
	//===start parvez date: 27-09-2022===		
			sbQuery.append("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,emp_no_of_leave,leave_type_id,reason," +
				"approval_from,approval_to_date, ishalfday, session_no,document_attached,is_approved,ispaid,is_compensate,is_work_from_home,emp_family_relation");
	//===end parvez date: 27-09-2022===		
			if(getExtraWorkingFromTime()!=null) {
				sbQuery.append(",from_time,to_time");
			}
		//===start parvez date: 18-03-2023===
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_BACKUP_EMPLOYEE_FOR_LEAVE))){
				sbQuery.append(",backup_emp_name");
			}
		//===end parvez date: 18-03-2023===	
//			sbQuery.append(" ) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?");
			sbQuery.append(" ) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?");
			if(getExtraWorkingFromTime()!=null) {
				sbQuery.append(",?,? ");
			}
		//===start parvez date: 18-03-2023===	
			if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_BACKUP_EMPLOYEE_FOR_LEAVE))){
				sbQuery.append(", '"+getBackupEmp()+"'");
			}
		//===end parvez date: 18-03-2023===	
			sbQuery.append(")");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getEmpId()));
			
			if(getIsHalfDay() && getExtraWorkingFromTime()!=null) {
				pst.setDate(2, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
			} else {
				pst.setDate(2, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
			}
			
			pst.setDate	(4, uF.getDateFormat(getEntrydate(), DATE_FORMAT));
			int nAppliedDays = 0;
			if(getIsHalfDay()) {
				pst.setDouble(5, 0.5);
			} else {
				nAppliedDays = uF.parseToInt(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone()));
				pst.setInt(5, nAppliedDays);
			}
			
			pst.setInt(6, uF.parseToInt(getTypeOfLeave()));
			pst.setString(7, getReason());
			if(getIsHalfDay()) {
				pst.setDate(8, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate(9, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
			} else {
				pst.setDate(8, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate(9, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
			}
			
			pst.setBoolean(10, getIsHalfDay());
			pst.setString(11, getStrSession());
			pst.setString(12, fileName);
			if(isApproval) {
				pst.setInt(13, 0);
			} else {
				pst.setInt(13, 1);
			}
			pst.setBoolean(14, isPaid);
			pst.setBoolean(15, uF.parseToBoolean(getIsCompensate()));
			pst.setBoolean(16, uF.parseToBoolean(getIsWorkFromHome()));
			pst.setString(17, getEmpRelation());
			/*if(getExtraWorkingFromTime()!=null) {
				pst.setTime(17, uF.getTimeFormat(getExtraWorkingFromTime(), TIME_FORMAT));
				pst.setTime(18, uF.getTimeFormat(getExtraWorkingToTime(), TIME_FORMAT));
			}*/
			if(getExtraWorkingFromTime()!=null) {
				pst.setTime(18, uF.getTimeFormat(getExtraWorkingFromTime(), TIME_FORMAT));
				pst.setTime(19, uF.getTimeFormat(getExtraWorkingToTime(), TIME_FORMAT));
			}
			
//			System.out.println("ELE/392---pst apply leave===>"+pst);
			pst.execute();
			pst.close();
			
//			System.out.println("getTypeOfLeave()===>"+getTypeOfLeave());
			String leave_id=null;
			pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
			rs=pst.executeQuery();
			while(rs.next()) {
				leave_id=rs.getString("leave_id");
			}
			rs.close();
			pst.close();
			
			List<String> alManagers = null;
			if(uF.parseToBoolean(CF.getIsWorkFlow())) {
				alManagers = insertLeaveApprovalMember(con,pst,rs,leave_id,uF);
			}
			
			if(!isApproval && uF.parseToInt(leave_id)>0) {
				//updateLeaveBalance(con,pst,rs,leave_id,uF,getLeaveFromTo(),getLeaveToDate());
				
				ManagerLeaveApproval leaveApproval=new ManagerLeaveApproval();
				leaveApproval.setServletRequest(request);
				leaveApproval.setLeaveId(leave_id);
				leaveApproval.setTypeOfLeave(getTypeOfLeave());
				leaveApproval.setEmpId(getEmpId());
				leaveApproval.setIsapproved(1);
				leaveApproval.setApprovalFromTo(getLeaveFromTo());
				leaveApproval.setApprovalToDate(getLeaveToDate());				
				leaveApproval.insertLeaveBalance(con, pst, rs, uF, uF.parseToInt(hmEmpLevel.get(getEmpId())), CF);
			}
			
//			pst.close();
			
			String strLeaveLbl = "leave";
			String strLeaveNotiLbl = N_MANAGER_LEAVE_REQUEST+"";
			if(uF.parseToBoolean(getIsCompensate())) {
				strLeaveLbl = "extra working";
				strLeaveNotiLbl = N_MANAGER_EXTRA_WORK_REQUEST+"";
			} else if(uF.parseToBoolean(getIsWorkFromHome())) {
				strLeaveLbl = "work from home";
			}
			if(getIsHalfDay()) {
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully applied for half day "+strLeaveLbl+"."+END);
			} else {
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully applied for "+uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(),DATE_FORMAT,CF.getStrTimeZone()) + " day(s) "+strLeaveLbl+"."+END);
			}
			
			if(isApproval && uF.parseToInt(leave_id)>0) {
//				System.out.println("alManagers==>"+alManagers);
				
				String strDomain = request.getServerName().split("\\.")[0];
				for(int i=0; alManagers!=null && i<alManagers.size();i++) {
					Notifications nF = new Notifications(uF.parseToInt(strLeaveNotiLbl), CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(getEmpId());
					nF.setSupervisor(false);
					nF.setEmailTemplate(true);
					
					pst = con.prepareStatement(selectEmpDetails1);
					pst.setInt(1, uF.parseToInt((String)alManagers.get(i)));
					rs = pst.executeQuery();
					boolean flg=false;
					while(rs.next()) {
						nF.setStrSupervisorEmail(rs.getString("emp_email"));					
					
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						nF.setStrEmpMobileNo(rs.getString("emp_contactno_mob"));
						nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
						if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("emp_email_sec"));
							nF.setStrEmailTo(rs.getString("emp_email_sec"));
						} else {
							nF.setStrEmpEmail(rs.getString("emp_email"));
							nF.setStrEmailTo(rs.getString("emp_email"));
						}
						flg=true;
					}
//					rs.close()
					if(flg) {
						if(getIsHalfDay()) {
							nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
							nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
							nF.setStrEmpLeaveNoOfDays("0.5");	
						} else {
							nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
							nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
							nF.setStrEmpLeaveNoOfDays(uF.dateDifference(getLeaveFromTo(), DATE_FORMAT, getLeaveToDate(), DATE_FORMAT, CF.getStrTimeZone()));
						}
						nF.setStrLeaveTypeName(uF.showData(hmLeaveType.get(getTypeOfLeave()), ""));
						nF.setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT_STR));
						nF.setStrEmpLeaveReason(getReason());
					//===start parvez date: 18-03-2023===	
						if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_BACKUP_EMPLOYEE_FOR_LEAVE))){
							nF.setStrLeaveEmpBackup(uF.showData(getBackupEmp(), ""));
						}
					//===end parvez date: 18-03-2023===	
//						System.out.println("nF.getStrEmpLeaveFrom() ===>> " + nF.getStrEmpLeaveFrom());
//						System.out.println("nF.getStrEmpLeaveTo() ===>> " + nF.getStrEmpLeaveTo());
						nF.sendNotifications();
					}
				}
			}
			
//			updateLeaveRegister();
			
			
/*	 		if(!isApproval) {
				CF.updateLeaveRegister1(con, CF, uF, nAppliedDays, 0, getTypeOfLeave(), getEmpId());
			}
			
			
			Notifications nF = new Notifications(N_EMPLOYEE_LEAVE_REQUEST, CF);
			nF.setStrEmpId(getEmpId());
			nF.setStrHostAddress(request.getRemoteHost());
			nF.setStrContextPath(request.getContextPath());
			if(getIsHalfDay()) {
				nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveNoOfDays("0.5");
			} else {
				nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveNoOfDays(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT));
			}
			
			
			nF.setStrEmpLeaveReason(getReason());
			nF.sendNotifications();
			
			
			nF = new Notifications(N_MANAGER_LEAVE_REQUEST, CF);
			nF.setSupervisor(true);
			nF.setStrHostAddress(request.getRemoteHost());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpId(getEmpId());
			if(getIsHalfDay()) {
				nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveNoOfDays("0.5");	
			} else {
				nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrEmpLeaveNoOfDays(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT));
			}
			
			nF.setStrEmpLeaveReason(getReason());
			nF.sendNotifications();
			*/
			
			
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

	private List<String> insertLeaveApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, String leave_id, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1,uF.parseToInt(getPolicy_id()));
			rs=pst.executeQuery();
			
			Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));
				
				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 18-10-2022===	
			/*if(uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_WORK_FLOW_REQUEST_FOR_SAME_MEMBER))){
				List<String> alChEmpIds = new ArrayList<String>();
				Map<String,String> hmWorkFlowMember = new HashMap<String, String>();
				String prevEmpId = null;
				String prevMemId = null;
				Iterator<String> it1=hmMemberMap.keySet().iterator();
				while(it1.hasNext()) {
					String work_flow_member_id = it1.next();
					List<String> innerList = hmMemberMap.get(work_flow_member_id);
					
					int memid = uF.parseToInt(innerList.get(1)); 
					String empid = request.getParameter(innerList.get(3)+memid);
					if(!alChEmpIds.contains(empid)){
						alChEmpIds.add(empid);
						hmWorkFlowMember.put(empid+"_"+memid, "true");
					}
					prevEmpId = empid;
					prevMemId = memid+"";
				}
				String strDomain = request.getServerName().split("\\.")[0];
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					int memid=uF.parseToInt(innerList.get(1)); 
					String empid=request.getParameter(innerList.get(3)+memid);
					
					if(empid!=null && !empid.equals("")) {
						int userTypeId = memid;
						if(uF.parseToInt(innerList.get(0)) == 3) {
							userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
						}
						pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
								"work_flow_mem_id,is_approved,status,user_type_id)" +
								"values(?,?,?,?, ?,?,?,?, ?)");
						pst.setInt(1,uF.parseToInt(empid));
						pst.setInt(2,uF.parseToInt(leave_id));
						pst.setString(3,WORK_FLOW_LEAVE);
						pst.setInt(4,uF.parseToInt(innerList.get(0)));
						//pst.setInt(5,uF.parseToInt(innerList.get(2)));
						pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
						pst.setInt(6,uF.parseToInt(innerList.get(4)));
						pst.setInt(7,0);
						pst.setInt(8,0);
						pst.setInt(9,userTypeId);
						pst.execute();
						pst.close();
	
						String date = "";
						if(getIsHalfDay()) {
							date = " date "+ uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to " + uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat());
						} else {
							date = " date "+ uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to " + uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat());
						}
						String strLeaveLbl = "Leave";
						if(uF.parseToBoolean(getIsCompensate())) {
							strLeaveLbl = "Extra Working";
						} else if(uF.parseToBoolean(getIsWorkFromHome())) {
							strLeaveLbl = "work from home";
						}
						
						String alertData = "<div class=\"grow\" style=\"float: left;\"> Received a new "+strLeaveLbl+" Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>"+date+". ["+hmUserType.get(userTypeId+"")+"] </div>";
						String strSubAction = "";
						String alertAction = "";
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
								strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
							}
							alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyLeave"+strSubAction;
						} else {
							alertAction = "Approvals.action?pType=WR&callFrom=NotiApplyLeave"+strSubAction;
						}
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(empid);
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(userTypeId+"");
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						
						if(!alManagers.contains(empid)) {
							alManagers.add(empid);
						}
					}
				}
				
			} else{
				
				String strDomain = request.getServerName().split("\\.")[0];
				Iterator<String> it=hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id=it.next();
					List<String> innerList=hmMemberMap.get(work_flow_member_id);
					
					int memid=uF.parseToInt(innerList.get(1)); 
					String empid=request.getParameter(innerList.get(3)+memid);
					
					if(empid!=null && !empid.equals("")) {
						int userTypeId = memid;
						if(uF.parseToInt(innerList.get(0)) == 3) {
							userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
						}
						pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
								"work_flow_mem_id,is_approved,status,user_type_id)" +
								"values(?,?,?,?, ?,?,?,?, ?)");
						pst.setInt(1,uF.parseToInt(empid));
						pst.setInt(2,uF.parseToInt(leave_id));
						pst.setString(3,WORK_FLOW_LEAVE);
						pst.setInt(4,uF.parseToInt(innerList.get(0)));
						//pst.setInt(5,uF.parseToInt(innerList.get(2)));
						pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
						pst.setInt(6,uF.parseToInt(innerList.get(4)));
						pst.setInt(7,0);
						pst.setInt(8,0);
						pst.setInt(9,userTypeId);
						pst.execute();
						pst.close();
	
						String date = "";
						if(getIsHalfDay()) {
							date = " date "+ uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to " + uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat());
						} else {
							date = " date "+ uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to " + uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat());
						}
						String strLeaveLbl = "Leave";
						if(uF.parseToBoolean(getIsCompensate())) {
							strLeaveLbl = "Extra Working";
						} else if(uF.parseToBoolean(getIsWorkFromHome())) {
							strLeaveLbl = "work from home";
						}
						
						String alertData = "<div class=\"grow\" style=\"float: left;\"> Received a new "+strLeaveLbl+" Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>"+date+". ["+hmUserType.get(userTypeId+"")+"] </div>";
						String strSubAction = "";
						String alertAction = "";
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
							if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
								strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
							}
							alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyLeave"+strSubAction;
						} else {
							alertAction = "Approvals.action?pType=WR&callFrom=NotiApplyLeave"+strSubAction;
						}
						
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(empid);
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(userTypeId+"");
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						
						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(empid);
						userAlerts.set_type(LEAVE_REQUEST_ALERT);
						userAlerts.setStatus(INSERT_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						if(!alManagers.contains(empid)) {
							alManagers.add(empid);
						}
					}
				}
			}*/
		//===end parvez date: 18-10-2022===	
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it=hmMemberMap.keySet().iterator();
			while(it.hasNext()) {
				String work_flow_member_id=it.next();
				List<String> innerList=hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")) {
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,uF.parseToInt(leave_id));
					pst.setString(3,WORK_FLOW_LEAVE);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					//pst.setInt(5,uF.parseToInt(innerList.get(2)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					pst.execute();
					pst.close();

					String date = "";
					if(getIsHalfDay()) {
						date = " date "+ uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to " + uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat());
					} else {
						date = " date "+ uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to " + uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat());
					}
					String strLeaveLbl = "Leave";
					if(uF.parseToBoolean(getIsCompensate())) {
						strLeaveLbl = "Extra Working";
					} else if(uF.parseToBoolean(getIsWorkFromHome())) {
						strLeaveLbl = "work from home";
					}
					
					String alertData = "<div class=\"grow\" style=\"float: left;\"> Received a new "+strLeaveLbl+" Request from <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>"+date+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyLeave"+strSubAction;
					} else {
						alertAction = "Approvals.action?pType=WR&callFrom=NotiApplyLeave"+strSubAction;
					}
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId+"");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					
					/*UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.set_type(LEAVE_REQUEST_ALERT);
					userAlerts.setStatus(INSERT_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();*/
					
					if(!alManagers.contains(empid)) {
						alManagers.add(empid);
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alManagers;
	}

	public void updateLeaveRegister() {
		
		Connection con = null;
		PreparedStatement pst = null;
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
			ResultSet rs = pst.executeQuery();
			double dblTakenLeaves = 0;
			while(rs.next()) {
				dblTakenLeaves = rs.getDouble("taken_leaves");
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("update leave_register set taken_leaves=? where emp_id = ? and leave_type_id=? and from_date<=? and to_date>=? ");
			
			if(getIsHalfDay()) {
				pst.setDouble(1, (dblTakenLeaves + 0.5 ));
			} else {
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
			
			
			if(getIsHalfDay()) {
				pst.setDate	(1, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDate	(2, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
				pst.setDouble	(3, 0.5);
			} else {
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
		
	//	System.out.println("leaveTypeList::"+leaveTypeList);
		userTypeList = new FillUserType(request).fillUserType();
//		empList = new FillEmployee(request).fillEmployeeCode(strUserType, strSessionEmpId);
		if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
			empList = new FillEmployee(request).fillEmployeeName(strUserType, null, session);
		} else if(strUserType!=null && (strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(CEO))) {
			empList = new FillEmployee(request).fillEmployeeNameByAccess(null, (String)session.getAttribute(ORG_ACCESS), null, (String)session.getAttribute(WLOCATION_ACCESS), strUserType, false);
		} else {
			empList = new ArrayList<FillEmployee>();
		}
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
    
	public void validate() {
		
		
		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
		
		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		
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
            addFieldError("LeaveFromTo", " Leave From Date is required");
        } 
        if (getLeaveToDate()!=null && getLeaveToDate().length() == 0) {
            addFieldError("LeaveToDate", " Leave To Date is required");
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
			while(rs.next()) {
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
			while(rs.next()) {
				dblMonthlyLimit = rs.getInt("monthly_limit");
				dblConsecutiveLimit = rs.getInt("consecutive_limit");
				isMonthlyCarryForward = rs.getBoolean("is_monthly_carryforward");
				strEffectiveDateType = rs.getString("effective_date_type");
			}
			rs.close();
			pst.close();
        	
			
			
			String strD1 = null;
			String strD2 = null;
			
			if(strEffectiveDateType!=null && strEffectiveDateType.equalsIgnoreCase("CY")) {
				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				strD1 = "01/01/"+cal.get(Calendar.YEAR);
				strD2 = "31/12/"+cal.get(Calendar.YEAR);
			}else if(strEffectiveDateType!=null && strEffectiveDateType.equalsIgnoreCase("FY")) {
				strD1 = strFinancialYearStart;
				strD2 = strFinancialYearEnd;
			}
			
			if(isMonthlyCarryForward) {
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
        	while(it.hasNext()) {
        		String strDate = (String)it.next();
        		
        		String strTemp = (String)hmTemp.get(strDate);
        		
        		if(strTemp!=null && strTemp.equalsIgnoreCase("F")) {
        			dblCount = dblCount+1;
        		}else if(strTemp!=null && strTemp.equalsIgnoreCase("H")) {
        			dblCount = dblCount+0.5;
        		}
        		
        	}
        	
        	
        	
        	double dblLeaveApplied = 0; 
        	
        	if(getIsHalfDay()) {
        		dblLeaveApplied = 0.5;
        	} else {
        		dblLeaveApplied = uF.parseToInt(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone()));
        	}
        	
			
			
			dblMonthlyLimit = dblMonthlyLimit - dblCount;
			double dblApplicableLeave = Math.min(dblMonthlyLimit, dblConsecutiveLimit);
			
			
        	/*
			if(uF.parseToInt(getEmpId())>0 && (dblApplicableLeave<dblLeaveApplied || dblApplicableLeave==0) && uF.parseToInt(getTypeOfLeave())>0) {
				
				if(dblApplicableLeave==0) {
					addFieldError("ExcessLeaveLimit", "You can not apply for any leave as you have finished the monthly limit.");
				} else {
					addFieldError("ExcessLeaveLimit", "You can not apply for more than "+dblApplicableLeave+" leaves.");
				}
			}*/
			
			
			dblAccruedLeaves = dblAccruedLeaves - dblCount;
			/*
			if(uF.parseToInt(getStrEmpId())>0 && dblLeaveApplied>dblAccruedLeaves) {
				addFieldError("ExcessLeaveLimit", "You have only "+dblAccruedLeaves+" available leaves.<br/>" +
						"You can not apply for more than "+dblAccruedLeaves+" leaves.");
			}
			 */  
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public String getIsCompensate() {
		return isCompensate;
	}

	public void setIsCompensate(String isCompensate) {
		this.isCompensate = isCompensate;
	}

	public String getIsConstant() {
		return isConstant;
	}

	public void setIsConstant(String isConstant) {
		this.isConstant = isConstant;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsOptHolidayLeave() {
		return isOptHolidayLeave;
	}

	public void setIsOptHolidayLeave(String isOptHolidayLeave) {
		this.isOptHolidayLeave = isOptHolidayLeave;
	}

	public String getStrHolidayDate() {
		return strHolidayDate;
	}

	public void setStrHolidayDate(String strHolidayDate) {
		this.strHolidayDate = strHolidayDate;
	}

	public String getStrPaycycle() {
		return strPaycycle;
	}

	public void setStrPaycycle(String strPaycycle) {
		this.strPaycycle = strPaycycle;
	}

	public String getStrCurrDate() {
		return strCurrDate;
	}

	public void setStrCurrDate(String strCurrDate) {
		this.strCurrDate = strCurrDate;
	}

	public String getIsWorkFromHome() {
		return isWorkFromHome;
	}

	public void setIsWorkFromHome(String isWorkFromHome) {
		this.isWorkFromHome = isWorkFromHome;
	}
	public String getExtraWorkingFromTime() {
		return extraWorkingFromTime;
	}

	public void setExtraWorkingFromTime(String extraWorkingFromTime) {
		this.extraWorkingFromTime = extraWorkingFromTime;
	}

	public String getExtraWorkingToTime() {
		return extraWorkingToTime;
	}

	public void setExtraWorkingToTime(String extraWorkingToTime) {
		this.extraWorkingToTime = extraWorkingToTime;
	}
//===start parvez date: 27-09-2022===
	public String getEmpRelation() {
		return empRelation;
	}

	public void setEmpRelation(String empRelation) {
		this.empRelation = empRelation;
	}
//===end parvez date: 27-09-2022===
//===start parvez date: 18-03-2023===
	public String getBackupEmp() {
		return backupEmp;
	}

	public void setBackupEmp(String backupEmp) {
		this.backupEmp = backupEmp;
	}
//===end parvez date: 18-03-2023===
}