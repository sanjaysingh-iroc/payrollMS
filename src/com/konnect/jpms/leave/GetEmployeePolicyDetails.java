package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EmployeeLeaveEntryReport;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmployeePolicyDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	String strBaseUserType = null;

	public CommonFunctions CF = null;
	
	private String leavetype;
	private String empid;
	private String strEmp;
	private String strD1;
	private String strD2;
	private String isCompensate;
	private String strSession;

	public String execute() {	
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
			return LOGIN;
		}				
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);

		if(getEmpid()==null || getEmpid().equals("")){
			setStrEmp(strSessionEmpId);
		}else{
			setStrEmp(getEmpid());
		}
		
		if (uF.parseToInt(getLeavetype())>0) {
			checkApplyLeaveLimit(uF);
			checkLeaveTimePeriod(uF);
			boolean flg=checkLeaveDates(uF);
			checkPayroll(uF);
			checkLongLeavelimit(uF);
			checkLeaveDetails(uF);
			if(!flg){
				boolean checkWeeklyOffHolidayApplyLeave = CF.getFeatureManagementStatus(request, uF, F_CHECK_WEEKLYOFF_HOLIDAY_APPLY_LEAVE);
				boolean isWeekOffHoliday = false; 
				if(checkWeeklyOffHolidayApplyLeave){
					isWeekOffHoliday = checkWeekOffHoliday(uF);
				}
				boolean checkApplyLeaveFreeze = CF.getFeatureManagementStatus(request, uF, F_ENABLE_APPLY_LEAVE_FREEZE);
				boolean isLeaveCanNotApply = false; 
				if(checkApplyLeaveFreeze) {
					isLeaveCanNotApply = checkApplyLeaveFreezeOrNot(uF);
				}
//				System.out.println("isWeekOffHoliday ===>> " + isWeekOffHoliday +" --- isLeaveCanNotApply ===>> " + isLeaveCanNotApply);
				
				if(!isWeekOffHoliday && !isLeaveCanNotApply && uF.parseToBoolean(CF.getIsWorkFlow())){
					getLeavePolicyMember(uF);
				}
			}
		}  
		
		return SUCCESS;
	}
	
	private boolean checkApplyLeaveFreezeOrNot(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		boolean isApplyLeaveFreeze = false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
			List<String> alFreezeDate = hmFeatureUserTypeId.get(F_ENABLE_APPLY_LEAVE_FREEZE+"_USER_IDS");
//			System.out.println("alFreezeDate ===>> " + alFreezeDate);
			String strFreezeDate = alFreezeDate.size()>0 ? alFreezeDate.get(0) : "";
//			System.out.println("strFreezeDate ===>> " + strFreezeDate);
			
			Date currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
			Date strtDate = uF.getDateFormat(getStrD1(), DATE_FORMAT);
			
			String strCurrMonth = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM");
			String strStrDtMonth = uF.getDateFormat(getStrD1(), DATE_FORMAT, "MM");
			String strDay = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "dd");
			
			String msgApplyLeaveFreeze = null;
			if(strtDate.before(currDate)) {
				msgApplyLeaveFreeze = "You can not apply leave. Leave apply option has been frozen for these dates.";
				isApplyLeaveFreeze = true;
			} else if(uF.parseToInt(strStrDtMonth) == uF.parseToInt(strCurrMonth)) {
				msgApplyLeaveFreeze = "You can not apply leave. Leave apply option has been frozen for these dates.";
				isApplyLeaveFreeze = true;
			} else if(uF.parseToInt(strDay)>= uF.parseToInt(strFreezeDate) && uF.parseToInt(strStrDtMonth) == (uF.parseToInt(strCurrMonth)+1)) {
				msgApplyLeaveFreeze = "You can not apply leave. Leave apply option has been frozen for these dates.";
				isApplyLeaveFreeze = true;
			}
			request.setAttribute("msgApplyLeaveFreeze", msgApplyLeaveFreeze);
			request.setAttribute("isApplyLeaveFreeze", ""+isApplyLeaveFreeze);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return isApplyLeaveFreeze;
	}

	
	private void checkApplyLeaveLimit(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strDateDiff = uF.dateDifference(getStrD1(), DATE_FORMAT, getStrD2(), DATE_FORMAT,CF.getStrTimeZone());
         	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
            double dblApplyLeavesDays = uF.parseToDouble(strDateDiff);
//            System.out.println("dblApplyLeavesDays=====>"+dblApplyLeavesDays);
			
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmp());
			String strWLocationId = CF.getEmpWlocationId(con, uF, getStrEmp());
			String strLevelId = CF.getEmpLevelId(con, getStrEmp());
			
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					"and lt.leave_type_id=? and level_id=? and lt.org_id=? and wlocation_id=? " +
					"and effective_date=(select max(effective_date) from emp_leave_type where leave_type_id=? and level_id=? and org_id=? " +
					"and wlocation_id=?) and elt.is_apply_leave_limit=true");
			pst.setInt(1, uF.parseToInt(getLeavetype()));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setInt(4, uF.parseToInt(strWLocationId));
			pst.setInt(5, uF.parseToInt(getLeavetype()));
			pst.setInt(6, uF.parseToInt(strLevelId));
			pst.setInt(7, uF.parseToInt(strOrgId));
			pst.setInt(8, uF.parseToInt(strWLocationId));
//			System.out.println("isApplyLeaveLimit pst=====>"+pst);
			rs = pst.executeQuery();
			boolean isApplyLeaveLimit = false;
			while(rs.next()){
				isApplyLeaveLimit = true;
			}
			rs.next();
			pst.close();
//			System.out.println("isApplyLeaveLimit=====>"+isApplyLeaveLimit);
			
			boolean isApplyLeaveLimitStatus = false;
			if(isApplyLeaveLimit){
				pst = con.prepareStatement("select sum(emp_no_of_leave) as emp_no_of_leave " +
						"from emp_leave_entry where emp_id=? and leave_type_id=? and is_approved=0");
				pst.setInt(1, uF.parseToInt(getStrEmp()));
				pst.setInt(2, uF.parseToInt(getLeavetype()));
//				System.out.println("isApplyLeaveLimit pst=====>"+pst);
				rs = pst.executeQuery();
				double dblNoofApplyLeave = 0.0d;
				while(rs.next()){
					dblNoofApplyLeave = uF.parseToDouble(rs.getString("emp_no_of_leave"));
				}
				rs.next();
				pst.close();
				
//				System.out.println("dblNoofApplyLeave=====>"+dblNoofApplyLeave);
				
				EmployeeLeaveEntryReport leaveEntryReport = new EmployeeLeaveEntryReport();
				leaveEntryReport.request = request;
				leaveEntryReport.session = session;
				leaveEntryReport.CF = CF;
				leaveEntryReport.setStrEmpId(getStrEmp());
				leaveEntryReport.setDataType("L");
				leaveEntryReport.viewEmployeeLeaveEntry1();
				 
				List<List<String>> leaveList = (List<List<String>>)request.getAttribute("leaveList");
				if(leaveList == null) leaveList = new ArrayList<List<String>>();
				
				int nLeaveListSize = leaveList.size();
				for (int j=0; j<nLeaveListSize; j++) {
					List<String> cinnerlist = (List<String>)leaveList.get(j);
					
					if(uF.parseToInt(cinnerlist.get(6)) == uF.parseToInt(getLeavetype())){
						double dblRemaining = uF.parseToDouble(cinnerlist.get(5));
						dblRemaining -=dblNoofApplyLeave;
//						System.out.println("dblRemaining=====>"+dblRemaining);
						if(dblRemaining <= 0.0 || dblApplyLeavesDays > dblRemaining){
							isApplyLeaveLimitStatus = true;
						}
					}					
				}
			}
//			System.out.println("isApplyLeaveLimitStatus=====>"+isApplyLeaveLimitStatus);
			
			request.setAttribute("isApplyLeaveLimit", ""+isApplyLeaveLimit);
			request.setAttribute("isApplyLeaveLimitStatus", ""+isApplyLeaveLimitStatus);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void checkLeaveTimePeriod(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmp());
			String strWLocationId = CF.getEmpWlocationId(con, uF, getStrEmp());
			String strLevelId = CF.getEmpLevelId(con, getStrEmp());
			
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					"and lt.leave_type_id=? and level_id=? and lt.org_id=? and wlocation_id=? " +
					"and effective_date=(select max(effective_date) from emp_leave_type where leave_type_id=? and level_id=? and org_id=? " +
					"and wlocation_id=?) and elt.is_time_period=true");
			pst.setInt(1, uF.parseToInt(getLeavetype()));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setInt(4, uF.parseToInt(strWLocationId));
			pst.setInt(5, uF.parseToInt(getLeavetype()));
			pst.setInt(6, uF.parseToInt(strLevelId));
			pst.setInt(7, uF.parseToInt(strOrgId));
			pst.setInt(8, uF.parseToInt(strWLocationId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			boolean isTimePeriod = false;
			while(rs.next()){
				isTimePeriod = true;
			}
			rs.next();
			pst.close();
			
			if(isTimePeriod){
				pst = con.prepareStatement("select * from leave_time_period where leave_type_id=? and level_id=? and wlocation_id=? " +
						"and org_id=? and ((? between time_from and time_to) and (? between time_from and time_to))");
				pst.setInt(1, uF.parseToInt(getLeavetype()));
				pst.setInt(2, uF.parseToInt(strLevelId));
				pst.setInt(3, uF.parseToInt(strWLocationId));
				pst.setInt(4, uF.parseToInt(strOrgId));
				pst.setDate(5, uF.getDateFormat(getStrD1(), DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(getStrD2(), DATE_FORMAT));
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				boolean isTimePeriodApplicable = false;
				while(rs.next()){
					isTimePeriodApplicable = true;
				}
				rs.close();
				pst.close();
				
				if(!isTimePeriodApplicable){
					pst = con.prepareStatement("select * from leave_time_period where leave_type_id=? and level_id=? and wlocation_id=? " +
							"and org_id=? order by time_from desc");
					pst.setInt(1, uF.parseToInt(getLeavetype()));
					pst.setInt(2, uF.parseToInt(strLevelId));
					pst.setInt(3, uF.parseToInt(strWLocationId));
					pst.setInt(4, uF.parseToInt(strOrgId));
//					System.out.println("pst=====>"+pst);
					rs = pst.executeQuery();
					List<Map<String, String>> alTimePeriod = new ArrayList<Map<String,String>>();
					while(rs.next()){
						Map<String, String> hmTimePeriod = new HashMap<String, String>();
						hmTimePeriod.put("TIME_FROM", uF.getDateFormat(rs.getString("time_from"), DBDATE, DATE_FORMAT));
						hmTimePeriod.put("TIME_TO", uF.getDateFormat(rs.getString("time_to"), DBDATE, DATE_FORMAT));
						
						alTimePeriod.add(hmTimePeriod);
					}
					rs.close();
					pst.close();
					request.setAttribute("alTimePeriod", alTimePeriod);
				}
				
				request.setAttribute("isTimePeriodApplicable", ""+isTimePeriodApplicable);
			}
			
			uF.getDateFormat(getStrD1(), DATE_FORMAT, CF.getStrReportDateFormat());
			uF.getDateFormat(getStrD2(), DATE_FORMAT, CF.getStrReportDateFormat());
				
			request.setAttribute("isTimePeriod", ""+isTimePeriod);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public boolean checkWeekOffHoliday(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		boolean isWeekOffHoliday = false;
		try {
			con = db.makeConnection(con);
			
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmp());
			String strWLocationId = CF.getEmpWlocationId(con, uF, getStrEmp());
			String strLevelId = CF.getEmpLevelId(con, getStrEmp());
			
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					"and lt.leave_type_id=? and level_id=? and lt.org_id=? and wlocation_id=? and effective_date=(select max(effective_date) " +
					"from emp_leave_type where leave_type_id=? and level_id = ? and is_compensatory = true and org_id=? and wlocation_id=?) " +
					"and elt.is_compensatory = true and lt.is_compensatory = true");
			pst.setInt(1, uF.parseToInt(getLeavetype()));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setInt(4, uF.parseToInt(strWLocationId));
			pst.setInt(5, uF.parseToInt(getLeavetype()));
			pst.setInt(6, uF.parseToInt(strLevelId));
			pst.setInt(7, uF.parseToInt(strOrgId));
			pst.setInt(8, uF.parseToInt(strWLocationId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			boolean isExtraWorkingLeave = false;
			while(rs.next()){
				isExtraWorkingLeave = true;
			}
			rs.next();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					"and lt.leave_type_id=? and level_id=? and lt.org_id=? and wlocation_id=? and effective_date=(select max(effective_date) " +
					"from emp_leave_type where leave_type_id=? and level_id=? and is_constant_balance=true and lt.org_id=? and wlocation_id=?) " +
					"and elt.is_constant_balance = true");
			pst.setInt(1, uF.parseToInt(getLeavetype()));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setInt(4, uF.parseToInt(strWLocationId));
			pst.setInt(5, uF.parseToInt(getLeavetype()));
			pst.setInt(6, uF.parseToInt(strLevelId));
			pst.setInt(7, uF.parseToInt(strOrgId));
			pst.setInt(8, uF.parseToInt(strWLocationId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			boolean isBalanceConstanLeave = false;
			while(rs.next()){
				isBalanceConstanLeave = true;
			}
			rs.next();
			pst.close();
			
			if(!isExtraWorkingLeave && !isBalanceConstanLeave){
				String msgWeekHoliday = null;
				boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getStrEmp(), getStrD1(), strLevelId, strWLocationId, strOrgId);
				if(isEmpRosterWeekOff){
					isWeekOffHoliday = true;
					msgWeekHoliday = "You can not apply leave for this date ("+getStrD1()+") owing to it's weekly off.";
				} else {
//					boolean isWeeklyOff = CF.checkWeeklyOff(con, CF, uF, getStrEmp(), getStrD1(), strLevelId, strWLocationId, strOrgId);
//					if(isWeeklyOff){
//						isWeekOffHoliday = true;
//						msgWeekHoliday = "You can not apply leave for this date ("+getStrD1()+") owing to it's weekly off.";
//					} else {
						boolean isHoliday = CF.checkHoliday(con, uF, getStrD1(), strWLocationId, strOrgId); 
						if(isHoliday){
							isWeekOffHoliday = true;
							msgWeekHoliday = "You can not apply leave for this date ("+getStrD1()+") owing to it's holiday.";
						} else {
							boolean isEmpRosterWeekOff2 = CF.checkEmpRosterWeeklyOff(con, CF, uF, getStrEmp(), getStrD2(), strLevelId, strWLocationId, strOrgId);
							if(isEmpRosterWeekOff2){
								isWeekOffHoliday = true;
								msgWeekHoliday = "You can not apply leave for this date ("+getStrD2()+") owing to it's weekly off.";
							} else {
//								boolean isWeeklyOff2 = CF.checkWeeklyOff(con, CF, uF, getStrEmp(), getStrD2(), strLevelId, strWLocationId, strOrgId);
//								if(isWeeklyOff2){
//									isWeekOffHoliday = true;
//									msgWeekHoliday = "You can not apply leave for this date ("+getStrD2()+") owing to it's holiday.";
//								} else {
									boolean isHoliday2 = CF.checkHoliday(con, uF, getStrD2(), strWLocationId, strOrgId); 
									if(isHoliday2){
										isWeekOffHoliday = true;
										msgWeekHoliday = "You can not apply leave for this date ("+getStrD2()+") owing to it's holiday.";
									} 
								}
//							}
						}
//					}
				}
				
				request.setAttribute("isWeekOffHoliday", ""+isWeekOffHoliday);
				request.setAttribute("msgWeekHoliday", msgWeekHoliday);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return isWeekOffHoliday;
	}

	public void checkPayroll(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
//			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			String paid_from="";
			String paid_to="";
			
			boolean flag = false;
//			pst = con.prepareStatement("select paid_from, paid_to from payroll_generation where emp_id=? and (paid_from, paid_to)" +
//					" overlaps(to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD')) ");
			pst = con.prepareStatement("select paid_from, paid_to from payroll_generation where emp_id=? and ((? between paid_from and paid_to) " +
				"or (? between paid_from and paid_to))");
			pst.setInt(1, uF.parseToInt(getStrEmp()));
			pst.setDate(2, uF.getDateFormat(getStrD1(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrD2(), DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst=====>"+pst);
			while(rs.next()) {
				flag = true;
				paid_from = uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat());
				paid_to = uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat());
			}
			rs.close();
			pst.close();
			if(uF.parseToBoolean(hmFeatureStatus.get(F_AFTER_PAYROLL_PROCESS_ACTIVE_ABSENCE_AND_TIME))) {
				flag=false;
			}
			int a=0;
			if(flag) {
				a=1;
			}
			request.setAttribute("checkPayroll", ""+a);
			request.setAttribute("paid_from", paid_from);
			request.setAttribute("paid_to", paid_to);
			
			flag=false;
			String approve_from="";
			String approve_to="";
			pst = con.prepareStatement("select * from approve_attendance where emp_id=? and ((? between approve_from and approve_to) " +
					"or (? between approve_from and approve_to))");
			pst.setInt(1, uF.parseToInt(getStrEmp()));
			pst.setDate(2, uF.getDateFormat(getStrD1(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrD2(), DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst=====>"+pst);
			while(rs.next()){
				flag = true;
				
				approve_from = uF.getDateFormat(rs.getString("approve_from"), DBDATE, CF.getStrReportDateFormat());
				approve_to = uF.getDateFormat(rs.getString("approve_to"), DBDATE, CF.getStrReportDateFormat());
			}
			rs.close();
			pst.close();
			
			if(uF.parseToBoolean(hmFeatureStatus.get(F_AFTER_PAYROLL_PROCESS_ACTIVE_ABSENCE_AND_TIME))) {
				flag = false;
			}
			
			a=0;
			if(flag){
				a=1;
			}
			request.setAttribute("checkAttendance", ""+a);
			request.setAttribute("approve_from", approve_from);
			request.setAttribute("approve_to", approve_to);
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public boolean checkLeaveDates(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select a.*,b.is_modify as modify,a.is_modify as modify1 from (select * from emp_leave_entry where emp_id=? " +
//					"and ((? between approval_from and approval_to_date) or (? between approval_from and approval_to_date)) " +
//					"and is_approved in (0,1)) a left join (select * from leave_application_register where emp_id=? " +
//					"and _date between ? and ?) b  on a.leave_id=b.leave_id");
//			pst = con.prepareStatement("select a.*,b.is_modify as modify,a.is_modify as modify1 from (select * from emp_leave_entry where emp_id=? " +
//					"and approval_from >= ? and approval_from<=? and is_approved in (0,1)) a left join (select * from leave_application_register where emp_id=? " +
//					"and _date between ? and ?) b  on a.leave_id=b.leave_id");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select a.*,b.is_modify as modify,a.is_modify as modify1 from (select * from emp_leave_entry where " +
				"emp_id=? and ((? between approval_from and approval_to_date) or (? between approval_from and approval_to_date) " +
				"or (approval_from >= ? and approval_from<=?)) and is_approved in (0,1) ");
			if(uF.parseToBoolean(getIsCompensate())) {
				sbQuery.append(" and leave_type_id>0 ");
			}
			sbQuery.append(" ) a left join (select * from leave_application_register where emp_id=? and _date between ? and ?) b on a.leave_id=b.leave_id ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrEmp()));
			pst.setDate(2, uF.getDateFormat(getStrD1(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrD2(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrD1(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrD2(), DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrEmp()));
			pst.setDate(7, uF.getDateFormat(getStrD1(), DATE_FORMAT)); 
			pst.setDate(8, uF.getDateFormat(getStrD2(), DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			double dblHalfDayCnt = 0.0d;
			while(rs.next()){
//				System.out.println("modify=====>"+uF.parseToBoolean(rs.getString("modify")));
//				System.out.println("modify1=====>"+uF.parseToBoolean(rs.getString("modify1")));
				if(uF.parseToBoolean(rs.getString("modify"))) {
					flag=false;
//					System.out.println("if flag=====>");
				} else if(!uF.parseToBoolean(rs.getString("modify1")) && (uF.parseToInt(rs.getString("is_approved"))==1 || uF.parseToInt(rs.getString("is_approved"))==0)){
				//===start parvez date: 21-02-2023===	
//					System.out.println("else if session_no==>"+rs.getString("session_no")+"---getStrSession=="+getStrSession());
					if(getStrSession()!=null && !getStrSession().equals("") && rs.getDouble("emp_no_of_leave")==0.5) {
				//===end parvez date: 21-02-2023===		
						
						if(rs.getString("session_no").equals(getStrSession())) {
							flag=true;
						} else {
							dblHalfDayCnt += rs.getDouble("emp_no_of_leave");
							if(dblHalfDayCnt>0.5) {
								flag=true;
//								System.out.println("GEPL/570---");
							} else {
								flag=false;
							}
						}
					} else {
						flag=true;
						break;
					}
//					System.out.println("else if flag=====>");
				}
			}
			rs.close();
			pst.close();
			
			int a=0;
			if(flag) {
				a=1;
			}
//			System.out.println("a flag=====>"+a);
			request.setAttribute("checkLeave", ""+a);
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return flag;
	}

	public void getLeavePolicyMember(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		boolean isApproval = false;
		String policy_id=null;
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			String empLevelId=hmEmpLevelMap.get(getStrEmp());
//			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
//			String locationID=hmEmpWlocationMap.get(getStrEmp());
			
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmp());
			String locationID = CF.getEmpWlocationId(con, uF, getStrEmp());
			String empLevelId = CF.getEmpLevelId(con, getStrEmp());
			
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			String strDiff = uF.dateDifference(getStrD1(),DATE_FORMAT, getStrD2(), DATE_FORMAT,CF.getStrTimeZone());
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
			
//			pst = con.prepareStatement("select policy_id,is_approval from emp_leave_type where level_id=? and leave_type_id=?");
			pst = con.prepareStatement("select policy_id,is_approval,is_period from emp_leave_type where level_id=? and leave_type_id=? and wlocation_id=? ");
			pst.setInt(1, uF.parseToInt(empLevelId));
			pst.setInt(2, uF.parseToInt(getLeavetype()));
			pst.setInt(3, uF.parseToInt(locationID));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			boolean isPeriod = false;
			while(rs.next()){
				if(!uF.parseToBoolean(rs.getString("is_period"))){
					policy_id=rs.getString("policy_id");
				}
				isApproval = uF.parseToBoolean(rs.getString("is_approval"));
				isPeriod = uF.parseToBoolean(rs.getString("is_period"));
				
			}
			rs.close();
			pst.close();
			request.setAttribute("isApproval", isApproval);
//			System.out.println("strDiff====>"+strDiff+"--policy_id====>"+policy_id+"--isPeriod====>"+isPeriod);
			
			if(uF.parseToInt(policy_id) == 0 && isPeriod){
				pst = con.prepareStatement("select * from workflow_policy_period where policy_type='Leave' and level_id=? and leave_type_id=?" +
						" and wlocation_id=? and ? between min_value and max_value");
				pst.setInt(1, uF.parseToInt(empLevelId));
				pst.setInt(2, uF.parseToInt(getLeavetype()));
				pst.setInt(3, uF.parseToInt(locationID));
				pst.setInt(4, uF.parseToInt(strDiff));
//				System.out.println("pst1====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					policy_id=rs.getString("policy_id");
				}
				rs.close();
				pst.close();
//				System.out.println("policy_id====>"+policy_id);
			}
			
			if(uF.parseToInt(policy_id) == 0 && isApproval){
				pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
						"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
				pst.setInt(1, uF.parseToInt(locationID));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()){
					policy_id = rs.getString("policy_count");
				}
				rs.close();
				pst.close();
//				System.out.println("policy_id ====>"+policy_id);
			}
			
			if(uF.parseToInt(policy_id)>0 && isApproval){
				
				pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
				pst.setInt(1,uF.parseToInt(policy_id));
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
				while(rs.next()){
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
//				System.out.println("hmMemberMap ===>> " + hmMemberMap);
				
				Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
				
				Iterator<String> it = hmMemberMap.keySet().iterator();
				while(it.hasNext()) {
					String work_flow_member_id = it.next();
					List<String> innerList = hmMemberMap.get(work_flow_member_id);
					
					if(uF.parseToInt(innerList.get(0))==1) {
						int memid = uF.parseToInt(innerList.get(1));
//						System.out.println("memid ===>> " + memid);
						switch(memid) {
						case 1:
					//===start parvez date: 13-09-2022===		
//							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_mname, epd.emp_fname,epd.emp_lname from user_details ud,"
//								+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id " +
//								"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname ");
//							System.out.println("strBaseUserType="+strBaseUserType);
							if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW)) && strBaseUserType!=null && !strBaseUserType.equals(ADMIN)){
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_mname, epd.emp_fname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id " +
										"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true and ud.user_id > 1 order by epd.emp_fname ");
							} else {
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_mname, epd.emp_fname,epd.emp_lname from user_details ud,"
										+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id " +
										"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname ");
							}
					//===end parvez date: 13-09-2022===		
							pst.setInt(1, uF.parseToInt(getStrEmp()));
//							System.out.println("pst ===>> " + pst);
							rs = pst.executeQuery();
							List<List<String>> outerList=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname"));
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));
								outerList.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList!=null && !outerList.isEmpty()){
								StringBuilder sbComboBox=new StringBuilder();
						////===Started by Ajinkya Date on 14-09-2022===		
//								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
								} else {
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								}
								
								sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								
								String strSelectValue = "";
								for(int i=0;i<outerList.size();i++){
									List<String> alList=outerList.get(i);
									sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									if(i == 0){
										strSelectValue = alList.get(0);
									}
								}
									
						//===Ended by Ajinkya Date on 13-09-2022===		
								sbComboBox.append("</select>");
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
								}
								
								String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;
							
						case 2:
							pst = con.prepareStatement("select * from (select distinct(supervisor_emp_id) as supervisor_emp_id from employee_official_details where emp_id=? and supervisor_emp_id!=0) as a," +
								"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(getStrEmp()));
							rs = pst.executeQuery();
//							System.out.println("===case2----pst="+pst);
							List<List<String>> outerList2 = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
//									alList.add(rs.getString("usertype_id"));
								alList.add(hmUserTypeIdMap.get(MANAGER));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));
								outerList2.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList2!=null && !outerList2.isEmpty()){
								StringBuilder sbComboBox=new StringBuilder();
								
						//===Started by Ajinkya Date on 13-09-2022===		
//								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								
								String strSelectValue = "";
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
								} else{
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								}
						//ended by Ajinkya Date on 13-09-2022		
								
								sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerList2.size();i++){
									List<String> alList=outerList2.get(i);
									sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									if(i == 0){
										strSelectValue = alList.get(0);
									}
								}	
								
								sbComboBox.append("</select>");	
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
								}
								
								String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;
							
						case 3:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
								+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 and ud.emp_id=eod.emp_id " +
								"and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and " +
								"ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname ");
							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(2, uF.parseToInt(getStrEmp()));
							rs = pst.executeQuery();
//							System.out.println("===case3----pst="+pst);
							List<List<String>> outerList1=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));
								outerList1.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList1!=null && !outerList1.isEmpty()){
								StringBuilder sbComboBox=new StringBuilder();
								
						//====Started by Ajinkya Date on 13-09-2022===		
								String strSelectValue = "";
//								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
								} else{
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								}
								
								sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerList1.size();i++){
									List<String> alList=outerList1.get(i);
									sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									if(i == 0){
										strSelectValue = alList.get(0);
									}
								}
						//ended by Ajinkya Date on 13-09-2022	
								
								sbComboBox.append("</select>");			
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
								}
								
								String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;
						
						case 4:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
								+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 and ud.emp_id=eod.emp_id " +
								"and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
								"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(getStrEmp()));
							rs = pst.executeQuery();
//							System.out.println("===case4----pst="+pst);
							List<List<String>> outerList4=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));
								outerList4.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList4!=null && !outerList4.isEmpty()){
								StringBuilder sbComboBox=new StringBuilder();
						//====Started by Ajinkya Date on 13-09-2022===		
								String strSelectValue = "";
//								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
								} else{
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								}
								sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerList4.size();i++){
									List<String> alList=outerList4.get(i);
									sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									if(i == 0){
										strSelectValue = alList.get(0);
									}
								}
								
							//	====ended by Ajinkya Date on 13-09-2022===
									
								sbComboBox.append("</select>");		
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
								}
								String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;
						
						case 5:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
								+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 and ud.emp_id=eod.emp_id " +
								"and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
								"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(getStrEmp()));
							rs = pst.executeQuery();
//							System.out.println("===case5----pst="+pst);
							List<List<String>> outerList5=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));
								outerList5.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList5!=null && !outerList5.isEmpty()){
								StringBuilder sbComboBox=new StringBuilder();
						//====Started by Ajinkya Date on 13-09-2022===	
								String strSelectValue = "";
//								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
								} else{
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								}
								sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerList5.size();i++){
									List<String> alList=outerList5.get(i);
									sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									if(i == 0){
										strSelectValue = alList.get(0);
									}
								}
								
						//====ended by Ajinkya Date on 13-09-2022===
								
								sbComboBox.append("</select>");	
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
								}
								String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;
							
						case 6:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud,"
								+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 and ud.emp_id=eod.emp_id " +
								"and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' " +
								"and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(locationID));
							pst.setInt(2, uF.parseToInt(getStrEmp()));
							rs = pst.executeQuery();
//							System.out.println("===case6----pst="+pst);
							List<List<String>> outerList6=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));
								outerList6.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList6!=null && !outerList6.isEmpty()){
								StringBuilder sbComboBox=new StringBuilder();
						//===Started by Ajinkya Date on 13-09-2022===	
								String strSelectValue = "";
//								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
								} else{
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								}
								sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerList6.size();i++){
									List<String> alList=outerList6.get(i);
									sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									if(i == 0){
										strSelectValue = alList.get(0);
									}
								}
						//===ended by Ajinkya Date on 13-09-2022===		
								
								sbComboBox.append("</select>");	
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
								}
								String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;
							
						case 7:
//							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
//								+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=7 "
//								+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
//								+ " and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
							if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
								pst= con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' " +
									"and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod,employee_personal_details epd " +
									"where epd.emp_per_id=eod.emp_id and eod.emp_id=?)");
								pst.setInt(1, uF.parseToInt(getStrEmp()));
								pst.setInt(2, uF.parseToInt(getStrEmp()));
							}else{
							
								pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod," +
									"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' " +
									"and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod,employee_personal_details epd " +
									"where epd.emp_per_id=eod.emp_id and eod.emp_id=?)" +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod,employee_personal_details epd " +
									"where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and " +
									"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true " +
									" union " +
									"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_mname,epd.emp_lname from user_details ud, employee_official_details eod,employee_personal_details epd " +
									"where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' " +
									"and ud.emp_id not in(?) and epd.is_alive=true");
								pst.setInt(1, uF.parseToInt(getStrEmp()));
								pst.setInt(2, uF.parseToInt(getStrEmp()));
								pst.setInt(3, uF.parseToInt(getStrEmp()));
								pst.setInt(4, uF.parseToInt(getStrEmp()));
							}
//							System.out.println("===case7----pst="+pst);
							rs = pst.executeQuery();
							List<List<String>> outerList7=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
//									alList.add(rs.getString("usertype_id"));
								alList.add(hmUserTypeIdMap.get(HRMANAGER));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));
								outerList7.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList7!=null && !outerList7.isEmpty()){
								StringBuilder sbComboBox=new StringBuilder();
								
						//===Started by Ajinkya Date on 13-09-2022===	
								String strSelectValue = "";
//								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
								} else{
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								}	
								sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerList7.size();i++){
									List<String> alList=outerList7.get(i);
									sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									if(i == 0){
										strSelectValue = alList.get(0);
									}
								}
						//===ended by Ajinkya Date on 13-09-2022===		
									
								sbComboBox.append("</select>");			
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
								}
								String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;		
						
						/*case 11:
							pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
								+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=11 and ud.emp_id=eod.emp_id " +
								"and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
								"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(getStrEmp()));
							rs = pst.executeQuery();
							List<List<String>> outerList11 = new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								alList.add(rs.getString("emp_lname"));
								outerList11.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList11!=null && !outerList11.isEmpty()) {
								StringBuilder sbComboBox3=new StringBuilder();
								sbComboBox3.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								sbComboBox3.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerList11.size();i++) {
									List<String> alList=outerList11.get(i);
									sbComboBox3.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+" "+alList.get(3)+"</option>");
								}
								sbComboBox3.append("</select>");								
								String optionTr3="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox3.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr3);
							}
							break;*/
								
						case 13:
							pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
								"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
								"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname");
							pst.setInt(1, uF.parseToInt(getStrEmp()));
//							System.out.println("===case13----pst="+pst);
							rs = pst.executeQuery();
							List<List<String>> outerList13=new ArrayList<List<String>>();
							while (rs.next()) {
								List<String> alList=new ArrayList<String>();
								alList.add(rs.getString("emp_id"));
								alList.add(rs.getString("usertype_id"));
								alList.add(rs.getString("emp_fname")); 
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								alList.add(strEmpMName);
								
								alList.add(rs.getString("emp_lname"));
								outerList13.add(alList);									
							}
							rs.close();
							pst.close();
							
							if(outerList13!=null && !outerList13.isEmpty()){
								StringBuilder sbComboBox=new StringBuilder();
						//===Started by Ajinkya Date on 13-09-2022===	
								String strSelectValue = "";
//								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
								} else{
									sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
								}
								sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
								for(int i=0;i<outerList13.size();i++){
									List<String> alList=outerList13.get(i);
									sbComboBox.append("<option value=\""+alList.get(0)+"\""+((i == 0) ? " selected" : "")+">"+alList.get(2)+alList.get(3)+" "+alList.get(4)+"</option>");
									if(i == 0){
										strSelectValue = alList.get(0);
									}
								}
						//===ended by Ajinkya Date on 13-09-2022===		
									
								sbComboBox.append("</select>");			
								if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
									sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
								}
								String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
								hmMemberOption.put(innerList.get(4), optionTr);
							}
							break;
						
						}						
						
					}/*else if(uF.parseToInt(innerList.get(0))==2){
						int memid=uF.parseToInt(innerList.get(1));						
						
						pst = con.prepareStatement("select emp_id from (select * from grades_details gd, designation_details dd, level_details ld, " +
								" employee_official_details eod where dd.designation_id = gd.designation_id and ld.level_id = dd.level_id " +
								" and gd.grade_id = eod.grade_id and dd.designation_id =? and eod.wlocation_id=? and eod.emp_id not in(?))as a," +
								" employee_personal_details epd where a.emp_id=epd.emp_per_id  order by epd.emp_fname ");
						pst.setInt(1, memid);
						pst.setInt(2, uF.parseToInt(locationID));
						pst.setInt(3, uF.parseToInt(getStrEmp())); 
						rs = pst.executeQuery(); and epd.is_alive=true
						List<List<String>> outerList=new ArrayList<List<String>>();
						while (rs.next()) {
							List<String> alList=new ArrayList<String>();
							alList.add(rs.getString("emp_id"));							
							outerList.add(alList);									
						}
						rs.close();
						pst.close();
						
						if(outerList!=null && !outerList.isEmpty()){
							StringBuilder sbComboBox=new StringBuilder();
							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validate[required]\">");
							sbComboBox.append("<option value=\"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++){
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");
							}
							sbComboBox.append("</select>");								
							
							String optionTr="<tr><td class=\"txtlabel alignRight\">"+innerList.get(3)+":<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
						
					}*/else if(uF.parseToInt(innerList.get(0))==3) {
						int memid=uF.parseToInt(innerList.get(1));
						
						List<List<String>> outerList=new ArrayList<List<String>>();
						pst = con.prepareStatement("select emp_id from specific_emp se,employee_personal_details epd where se.emp_id=epd.emp_per_id " +
								"and se.policy_id = ? and se.emp_id not in(?) and epd.is_alive=true order by epd.emp_fname"); 
						pst.setInt(1,uF.parseToInt(policy_id));
						pst.setInt(2, uF.parseToInt(getStrEmp()));
						rs = pst.executeQuery();
						while (rs.next()) {
							List<String> alList = new ArrayList<String>();
							alList.add(rs.getString("emp_id"));
							outerList.add(alList);
						}
						rs.close();
						pst.close();
						
						
						if(outerList!=null && !outerList.isEmpty()){
							StringBuilder sbComboBox=new StringBuilder();
							String strSelectValue = "";
//							sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\" disabled>");
							}else{
								sbComboBox.append("<select name=\""+innerList.get(3)+memid+"\" id=\""+innerList.get(3)+memid+"\" class=\"validateRequired\">");
							}
							String a="";
							sbComboBox.append("<option value=\""+a+"\">Select "+innerList.get(3)+"</option>");
							for(int i=0;i<outerList.size();i++){
								List<String> alList=outerList.get(i);
								sbComboBox.append("<option value=\""+alList.get(0)+"\">"+hmEmpCodeName.get(alList.get(0).trim())+"</option>");
								if(i == 0){
									strSelectValue = alList.get(0);
								}
							}
							sbComboBox.append("</select>");		
							if(uF.parseToBoolean(hmFeatureStatus.get(F_READ_ONLY_WORKFLOW))){
								sbComboBox.append("<input type=\"hidden\" name=\""+innerList.get(3)+memid+"\" value=\""+strSelectValue+"\">");
							}
							
							String optionTr="<tr><td class=\"txtlabel alignRight\">Your work flow:<sup>*</sup></td><td>"+sbComboBox.toString()+"</td></tr>";
							
							hmMemberOption.put(innerList.get(4), optionTr);
						}
					}
				}
//				System.out.println("hmMemberOption ===>> " + hmMemberOption);
				
				request.setAttribute("hmMemberOption",hmMemberOption);
				request.setAttribute("policy_id",policy_id);
				
			}
						
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	private void checkLongLeavelimit(UtilityFunctions uF) {

		//  System.out.println("checkLongLeavelimit=====>");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		boolean isLongLeave = false;
		double dblLongLeaveLimit = 0;
		boolean isLongLeaveLimitExceed = false;
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String strDateDiff = uF.dateDifference(getStrD1(), DATE_FORMAT, getStrD2(), DATE_FORMAT,CF.getStrTimeZone());
         	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
            double dblApplyLeavesDays = uF.parseToDouble(strDateDiff);
            
        //    System.out.println("dblApplyLeavesDays=====>"+dblApplyLeavesDays);
            
        	String strOrgId = CF.getEmpOrgId(con, uF, getStrEmp());
			String strWLocationId = CF.getEmpWlocationId(con, uF, getStrEmp());
			String strLevelId = CF.getEmpLevelId(con, getStrEmp());
            pst = con.prepareStatement("select * from emp_leave_type where leave_type_id=? and level_id=? and org_id=? and wlocation_id=?");

            pst.setInt(1, uF.parseToInt(getLeavetype()));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setInt(4, uF.parseToInt(strWLocationId));

			
//			System.out.println("isLongLeaveLimit pst=====>"+pst);
			rs = pst.executeQuery();
			
			while(rs.next())
			{
				isLongLeave = uF.parseToBoolean(rs.getString("is_long_leave"));
				dblLongLeaveLimit = uF.parseToDouble(rs.getString("long_leave_limit"));

			}
			
            if(isLongLeave && dblLongLeaveLimit > 0 && dblApplyLeavesDays > dblLongLeaveLimit){
            	isLongLeaveLimitExceed = true;
            }
            
            rs.close();
            pst.close();
           request.setAttribute("isLongLeaveLimitExceed", ""+isLongLeaveLimitExceed);
			
            
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
			
	
	}
	
//===start parvez date: 26-09-2022===
	//===start===
	private void checkLeaveDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs =null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			String strDateDiff = uF.dateDifference(getStrD1(), DATE_FORMAT, getStrD2(), DATE_FORMAT,CF.getStrTimeZone());
         	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
            double dblApplyLeavesDays = uF.parseToDouble(strDateDiff);
//            System.out.println("dblApplyLeavesDays=====>"+dblApplyLeavesDays);
			
			String strOrgId = CF.getEmpOrgId(con, uF, getStrEmp());
			String strWLocationId = CF.getEmpWlocationId(con, uF, getStrEmp());
			String strLevelId = CF.getEmpLevelId(con, getStrEmp());
			
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					"and lt.leave_type_id=? and level_id=? and lt.org_id=? and wlocation_id=? " +
					"and effective_date=(select max(effective_date) from emp_leave_type where leave_type_id=? and level_id=? and org_id=? " +
					"and wlocation_id=?) ");	//and lt.is_document_required=true
			pst.setInt(1, uF.parseToInt(getLeavetype()));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setInt(4, uF.parseToInt(strWLocationId));
			pst.setInt(5, uF.parseToInt(getLeavetype()));
			pst.setInt(6, uF.parseToInt(strLevelId));
			pst.setInt(7, uF.parseToInt(strOrgId));
			pst.setInt(8, uF.parseToInt(strWLocationId));
//			System.out.println("isApplyLeaveLimit pst=====>"+pst);
			rs = pst.executeQuery();
			boolean isDocumentRequired = false;
			boolean isBereavementLeave = false;
			boolean isDocumentMandatory = false;
			while(rs.next()){
//				System.out.println("GEmpPD/1457---dblApplyLeavesDays="+dblApplyLeavesDays+"---days=="+rs.getString("no_of_days_for_document_upload"));
				if(uF.parseToBoolean(rs.getString("is_document_required"))){
					if(dblApplyLeavesDays >= uF.parseToDouble(rs.getString("no_of_days_for_document_upload"))){
						isDocumentRequired = true;
					}
				}
				
				if(uF.parseToBoolean(rs.getString("is_bereavement_leave"))){
					isBereavementLeave = true;
				}
				
				if(uF.parseToBoolean(rs.getString("is_document_mandatory"))){
					isDocumentMandatory = true;
				}
			}
			rs.next();
			pst.close();
//			System.out.println("isDocumentRequired=====>"+isDocumentRequired);
			
			request.setAttribute("isDocumentRequired", ""+isDocumentRequired);
			request.setAttribute("isBereavementLeave", ""+isBereavementLeave);
			request.setAttribute("isDocumentMandatory", ""+isDocumentMandatory);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	//===end===

	public String getLeavetype() {
		return leavetype;
	}

	public void setLeavetype(String leavetype) {
		this.leavetype = leavetype;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getStrEmp() {
		return strEmp;
	}

	public void setStrEmp(String strEmp) {
		this.strEmp = strEmp;
	}


	public String getStrD1() {
		return strD1;
	}

	public void setStrD1(String strD1) {
		this.strD1 = strD1;
	}

	public String getStrD2() {
		return strD2;
	}

	public void setStrD2(String strD2) {
		this.strD2 = strD2;
	}

	public String getIsCompensate() {
		return isCompensate;
	}

	public void setIsCompensate(String isCompensate) {
		this.isCompensate = isCompensate;
	}

	public String getStrSession() {
		return strSession;
	}

	public void setStrSession(String strSession) {
		this.strSession = strSession;
	}

	public HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}
	
}