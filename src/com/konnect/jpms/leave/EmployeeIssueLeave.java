package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class EmployeeIssueLeave extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF;
	HttpSession session;
	private String orgId; 
	private String strLocation;
	private String strLevelId;
	
	private String isSandwich;
	private List<String> sandwichHoliday;
	private String policy;
	private String param;
	private List<FillLeaveType> leaveList;
	private List<FillLeaveType> leaveList1;
	private List<FillLeaveType> leaveList2;
	
	private String balance;
	private String balanceLimit;
	private List<String> combination;
	
	private List<String> prefix;
	private List<String> suffix;
	
	private String priorApply;
	private String futureApply;
	private String futureApplyMax;
	private String leaveLimit;
	private boolean NY;
	private String maternityFrequency;
	private boolean isMaternity;
	private String isCompensatory;
	private String isWorkFromHome;
	
	private String compensateWith;
	
	private String isLeaveEncashment;
	private String minLeavesRequiredEncashment;
	private String strEncashApplicable;
	private String noOfTimes;
	private String defaultSelectEncash;
	
	private String maxLeavesAppliedEncashment;
	private List<String> salaryHeadId;
	private List<FillSalaryHeads> salaryHeadList;
	
	private boolean isLongLeave;
	private String longLeaveLimit;
	private List<String> leaveAvailable;
	private String percentage;
	private String isLeaveAccrual;
	
	private String isLeaveOptHoliday;
	private String calendarYear;
	private String optionalLeaveLimit;
	private List<FillCalendarYears> calendarYearList;
	
	private String  strPolicyId;
	private String  effectiveFrom;
	private boolean ispaid;
	private boolean isCarryForward;	
	private String empLeaveTypeId;
	private String employeType;
//	String levelType;
	private List<String> levelType;
//	String typeOfLeave;
	private String noOfLeave;
	private String approvalDate;
	
	private String noOfLeaveMonthly;
	private String accrualSystem;
	private String accrualFrom;
	
	private String monthlyLeaveLimit;
	private String consLeaveLimit;
	private boolean isMonthlyCarryForward;
	private boolean isApproval;

	private List<FillUserType> userTypeList;
	private List<FillLevel> levelTypeList; 
	private List<FillLeaveType> empLeaveTypeList;
	private List<FillApproval> approvalList;
	
	private boolean isProrata;
	
	private String defaultAccrualType;
	private String accrualType;
	private String noOfAccrueDays; 
	
	private boolean isCarriedForwardLimit;
	private String carriedForwardLimit;
	private String defaultBalance;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	private String distributedMonth;   //added by parvez on 29-07-2021
	private String priorApplyOneDayLeave;   //added by parvez date: 15-09-2022
	private String lapsDays;				//added by parvez date: 16-09-2022
	
	boolean isTimePeriod;
	String[] strTimeFromDate;
	String[] strTimeToDate;
	boolean isActualCalDays;
	
	boolean isApplyLeaveLimit;
	private boolean isCarryForwardAccrualMonthly;
	
	private String isDocumentRequired;		//added by parvez date: 26-09-2022
	private String noDaysForDocument;		//added by parvez date: 26-09-2022
	
	private String joiningMonthDay;
	private String joiningMonthLeaveBalance;
	private String longLeaveGap;
	private String futureApply1;
	private String noOfLeave1;
	private String noOfLeave2;
	private String noOfLeave3;
	private String minimumLongLeaveLimit;
	private String monthlyApplyLimit;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN; 
		
		request.setAttribute(PAGE, PEmployeeIssueLeave);
		
		UtilityFunctions uF = new UtilityFunctions();  		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		getLeaveTypeDetails(uF.parseToInt(param));
		
		leaveList1=new FillLeaveType(request).fillLeaveType(uF.parseToInt(param));
		leaveList2=new FillLeaveType(request).fillLeaveTypeWithoutCompensatory(uF.parseToInt(param));
		
		request.setAttribute("param",param);
		leaveList=new ArrayList<FillLeaveType>();
		leaveList.add(new  FillLeaveType("-1","Weekly Off"));
		leaveList.add(new  FillLeaveType("-2","Holiday"));
		
		salaryHeadList = new FillSalaryHeads(request).fillSalaryHeadsByEmpLeaveTypeIdWithoutCTC(strId);
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		
		if (uF.parseToBoolean(CF.getIsWorkFlow())) {
			viewWorkFlowPolicyReport();
		}
		
		if (getAccrualSystem()  ==  null) {
			setAccrualSystem("1");
		}
		
		if (getAccrualFrom()  ==  null) {
			setAccrualFrom("1");
		}
		
//		System.out.println("EIL/175--operation="+operation);
		if (operation!=null && operation.equals("ULeaveBalance")) {
			return updateEmployeeLeaveBalance();
		}
		
		getLeaveEncash();
		
		if (operation!=null && operation.equals("D")) {
			return deleteEmployeeIssueLeave(strId);
		}		
		if (operation!=null && operation.equals("E")) {
			return viewEmployeeIssueLeave(strId);
		}		
		if (getEmpLeaveTypeId()!=null && getEmpLeaveTypeId().length()>0) {
			return updateEmployeeIssueLeave();
		}
		if (getLevelType()!=null && getLevelType().size()>0) {
			return insertEmployeeIssueLeave();
		}
		
		setDefaultSelectEncash("2");
		setDefaultAccrualType("1");
		setDefaultBalance("false");
		
		return loadEmployeeIssueLeave();
	}
	
	private String updateEmployeeLeaveBalance() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select no_of_leave from emp_leave_type where leave_type_id=? and level_id=? and org_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getParam()));
			pst.setInt(2, uF.parseToInt(getStrLevelId()));
			pst.setInt(3, uF.parseToInt(getOrgId()));
			pst.setInt(4, uF.parseToInt(getStrLocation()));
			System.out.println("EIL/225--pst=======>"+pst);
			rs = pst.executeQuery();
			double noOfLeave = 0.0d;
			while(rs.next()){
				noOfLeave = uF.parseToDouble(rs.getString("no_of_leave"));
			}
			rs.close();
			pst.close();
//			System.out.println("noOfLeave=======>"+noOfLeave);
			if(noOfLeave>0){
				pst = con.prepareStatement("select emp_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
						" and is_alive=true and org_id=? and wlocation_id=? and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
						"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id=?) order by emp_id ");
				pst.setInt(1, uF.parseToInt(getOrgId()));
				pst.setInt(2, uF.parseToInt(getStrLocation()));
				pst.setInt(3, uF.parseToInt(getStrLevelId()));
//				System.out.println("EIL/232--pst=======>"+pst);
				rs = pst.executeQuery();
				List<String> alEmpList =new ArrayList<String>();
				while(rs.next()){
					alEmpList.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
//				System.out.println("alEmpList=======>"+alEmpList.toString());
				boolean flag = false;
				for(String strEmp : alEmpList){
					pst=con.prepareStatement("update leave_register1 set balance=? where emp_id=? and _date=? and leave_type_id=? and _type=?");
					pst.setDouble(1, noOfLeave);
					pst.setInt(2, uF.parseToInt(strEmp));
					pst.setDate(3, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
					pst.setInt(4, uF.parseToInt(getParam()));
					pst.setString(5, "C");
//					System.out.println("EIL/249--update pst=====>"+pst);
					int x = pst.executeUpdate();
					pst.close();
					
					if(x == 0){
						pst=con.prepareStatement("insert into leave_register1(emp_id,_date,balance,leave_type_id,_type)values(?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmp));
						pst.setDate(2, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
						pst.setDouble(3, noOfLeave);
						pst.setInt(4, uF.parseToInt(getParam()));
						pst.setString(5, "C");
//						System.out.println("EIL/260--pst=====>"+pst);
						pst.execute();
						pst.close();
					}
					flag =true;
				}
				if(flag){
					session.setAttribute(MESSAGE, SUCCESSM+"Employee Leave Balance Updated Successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Employee leave balance not updated. Please check annual leave balance"+END);
				}
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Employee leave balance not updated. Please check annual leave balance"+END);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private void getLeaveEncash() {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from leave_type where leave_type_id=?");
			pst.setInt(1, uF.parseToInt(getParam()));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				setIsLeaveEncashment(rs.getString("is_leave_encashment"));
				setIsLeaveOptHoliday(rs.getString("is_leave_opt_holiday"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
	}
	
	public void getLeaveTypeDetails(int id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst=con.prepareStatement("select * from leave_type where leave_type_id=?");			
			pst.setInt(1, id);
			rs=pst.executeQuery();
			while (rs.next()) {
				isMaternity=uF.parseToBoolean(rs.getString("is_maternity"));
				setIsCompensatory(rs.getString("is_compensatory"));
				setIsWorkFromHome(rs.getString("is_work_from_home"));
			//===start parvez date: 26-09-2022===
				setIsDocumentRequired(rs.getString("is_document_required"));
			//===end parvez date: 26-09-2022===	
				
			}
			rs.close();
			pst.close();
//			System.out.println("IsDocumentRequired ===>> " + getIsDocumentRequired());
		} catch (Exception e) { 
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void viewWorkFlowPolicyReport() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		Map<String,String> hmOrg = getOrganization();
		Map<String,String> hmLocationName = getLocationName();
		
		
		try {

			List<List<String>> reportList=new ArrayList<List<String>>();		
							
			con = db.makeConnection(con);
			
			StringBuilder sbQuery=new StringBuilder();
			
			
			if (uF.parseToInt(getOrgId())>0 && uF.parseToInt(getStrLocation())>0) {
				sbQuery.append("select * from (select max(work_flow_policy_id)as work_flow_policy_id,policy_count " +
				" from work_flow_policy where trial_status=1 group by policy_count) a ,work_flow_policy b where a.work_flow_policy_id=b.work_flow_policy_id");
				sbQuery.append(" and org_id='"+getOrgId().trim()+"' ");
				sbQuery.append(" and location_id='"+getStrLocation().trim()+"'  ");
			 
				pst = con.prepareStatement(sbQuery.toString());
	//			System.out.println("pst  ==    ==  >"+pst);
				rs = pst.executeQuery(); 
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("work_flow_policy_id"));
					alInner.add(rs.getString("work_flow_member_id"));
					alInner.add(rs.getString("member_position"));
					alInner.add(rs.getString("policy_type"));
					alInner.add(rs.getString("trial_status"));
					alInner.add(rs.getString("added_by"));
					alInner.add(rs.getString("added_date")!=null?uF.getDateFormat(rs.getString("added_date"), DBDATE, DATE_FORMAT):"-");
					alInner.add(rs.getString("policy_count"));
					
					alInner.add(rs.getString("policy_name"));
					alInner.add(rs.getString("effective_date")!=null?uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT):"-");
					alInner.add(rs.getString("org_id")!=null ? hmOrg.get(rs.getString("org_id").trim()) : "");
					alInner.add(rs.getString("location_id")!=null ? hmLocationName.get(rs.getString("location_id").trim()) : "");
					
					alInner.add(rs.getString("policy_status"));
					
					reportList.add(alInner);
					
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("reportList", reportList);
			
			
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	public String loadValidateEmployeeIssueLeave() {
		
		UtilityFunctions uF = new UtilityFunctions();
		
		
		//request.setAttribute(PAGE, PEmployeeIssueLeave);
		//request.setAttribute(TITLE, TAddEmployeeIssueLeave);
		userTypeList = new FillUserType(request).fillUserType();
//		levelTypeList = new FillLevel(request).fillLevel(uF.parseToInt(getOrgId()));
		levelTypeList = new FillLevel(request).fillLevelForLeavePolicy(uF.parseToInt(getOrgId()),getParam(),getStrLocation());
		empLeaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt(getOrgId()));
		approvalList = new FillApproval().fillLeaveStartDate();
		return LOAD;
	}

	public String loadEmployeeIssueLeave() {
		setEmpLeaveTypeId("");
		setEmployeType("");
//		setTypeOfLeave("");
//		setNoOfLeave("");
		return LOAD;
	}

	public String insertEmployeeIssueLeave() {

//		System.out.println("inside insert..");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
			if (getCalendarYear() != null) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}

			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
//			System.out.println("getBalance()====>"+getBalance()+"---uF.parseToBoolean(getBalance())====>"+uF.parseToBoolean(getBalance()));
//			Map<String, String> hmEmpJoiningDateMap = CF.getEmpJoiningDateMap();			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			StringBuilder sbQuery = null;
//			pst = con.prepareStatement(insertEmployeeIssueLeave);
			for(String level:getLevelType()){
				sbQuery = new StringBuilder();
			//===start parvez date: 26-09-2022===
//				pst=con.prepareStatement("INSERT INTO emp_leave_type (level_id, leave_type_id, no_of_leave, is_paid, is_carryforward, " +
//					"entrydate, effective_date, user_id, effective_date_type, monthly_limit, consecutive_limit, is_monthly_carryforward, " +
//					"is_approval, org_id, no_of_leave_monthly, accrual_system, accrual_from,wlocation_id,balance_validation,validation_days," +
//					"leave_suffix,leave_prefix,prior_days,leave_limit,next_year_leave,maternity_type_frequency,combination_leave,compensate_with," +
//					"is_compensatory,is_sandwich,sandwich_leave_type,sandwich_type,min_leave_encashment,encashment_applicable,encashment_times," +
//					"max_leave_encash,salary_head_id,is_long_leave,long_leave_limit,leave_available,percentage,is_leave_accrual,is_prorata,accrual_type," +
//					"accrual_days,is_carryforward_limit,carryforward_limit,is_time_period,is_accrued_cal_days,is_apply_leave_limit," +
//					"is_carryforward_accrual_monthly,future_days,future_days_max,is_work_from_home,distributed_month,prior_days_for_one_day_leave,laps_days,no_of_days_for_document_upload) " +
//					"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				
				sbQuery.append("INSERT INTO emp_leave_type (level_id, leave_type_id, no_of_leave, is_paid, is_carryforward, " +
						"entrydate, effective_date, user_id, effective_date_type, monthly_limit, consecutive_limit, is_monthly_carryforward, " +
						"is_approval, org_id, no_of_leave_monthly, accrual_system, accrual_from,wlocation_id,balance_validation,validation_days," +
						"leave_suffix,leave_prefix,prior_days,leave_limit,next_year_leave,maternity_type_frequency,combination_leave,compensate_with," +
						"is_compensatory,is_sandwich,sandwich_leave_type,sandwich_type,min_leave_encashment,encashment_applicable,encashment_times," +
						"max_leave_encash,salary_head_id,is_long_leave,long_leave_limit,leave_available,percentage,is_leave_accrual,is_prorata,accrual_type," +
						"accrual_days,is_carryforward_limit,carryforward_limit,is_time_period,is_accrued_cal_days,is_apply_leave_limit," +
						"is_carryforward_accrual_monthly,future_days,future_days_max,is_work_from_home,distributed_month,prior_days_for_one_day_leave," +
						"laps_days,no_of_days_for_document_upload,min_long_leave_limit, monthly_apply_leave_limit ");
				
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_BALANCE_FOR_EMPLOYEE_JOINING_MONTH))){
					sbQuery.append(",joining_month_day_date,joining_month_balance");
				}
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_PRIOR_DAYS_NOTIFICATION))){
					sbQuery.append(", future_days_1, no_of_leaves1, no_of_leaves2, no_of_leaves3");
				}
				
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE))){
					sbQuery.append(", long_leave_gap");
				}
				sbQuery.append(") VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?");
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_BALANCE_FOR_EMPLOYEE_JOINING_MONTH))){
					sbQuery.append(","+uF.parseToInt(getJoiningMonthDay()));
					sbQuery.append(","+uF.parseToDouble(getJoiningMonthLeaveBalance()));
				}
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_PRIOR_DAYS_NOTIFICATION))){
					sbQuery.append(","+uF.parseToInt(getFutureApply1()));
					sbQuery.append(","+uF.parseToDouble(getNoOfLeave1()));
					sbQuery.append(","+uF.parseToDouble(getNoOfLeave2()));
					sbQuery.append(","+uF.parseToDouble(getNoOfLeave3()));
				}
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE))){
					sbQuery.append(","+uF.parseToInt(getLongLeaveGap()));
				}
				sbQuery.append(" )");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(level));
				pst.setInt(2, uF.parseToInt(getParam()));
				pst.setDouble(3, uF.parseToDouble(getNoOfLeave()));
				pst.setBoolean(4, getIspaid());
				pst.setBoolean(5, getIsCarryForward());
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				if (getApprovalDate().contains("CY")) {
					pst.setDate(7, uF.getDateFormat("01/01/"+uF.getYear()+"",DATE_FORMAT));
				} else if (getApprovalDate().contains("FY")) {
					pst.setDate(7, uF.getDateFormat(strFinancialYearStart+"",DATE_FORMAT));
				} else {
					pst.setDate(7, uF.getDateFormat("01/01/"+uF.getYear()+"", DATE_FORMAT));
				}
				pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setString(9, getApprovalDate());
				pst.setInt(10, uF.parseToInt(getMonthlyLeaveLimit()));
				pst.setInt(11, uF.parseToInt(getConsLeaveLimit()));
				pst.setBoolean(12, getIsMonthlyCarryForward());
				pst.setBoolean(13, getIsApproval());
				pst.setInt(14, uF.parseToInt(getOrgId()));
				pst.setDouble(15, uF.parseToDouble(getNoOfLeaveMonthly()));
				pst.setInt(16, uF.parseToInt(getAccrualSystem()));
				pst.setInt(17, uF.parseToInt(getAccrualFrom()));
				pst.setInt(18, uF.parseToInt(getStrLocation()));
				pst.setBoolean(19, uF.parseToBoolean(getBalance()));
				pst.setInt(20, uF.parseToInt(getBalanceLimit()));
				pst.setString(21, getData(getSuffix()));
				pst.setString(22,getData(getPrefix()));
				pst.setInt(23, uF.parseToInt(getPriorApply()));
				pst.setInt(24, uF.parseToInt(getLeaveLimit()));
				pst.setBoolean(25, getNY());
				pst.setInt(26, uF.parseToInt(getMaternityFrequency()));
				
				String combineLeave = getData(getCombination());
				pst.setString(27, combineLeave!=null ? getParam()+","+combineLeave : getParam());
				
				pst.setInt(28, uF.parseToInt(getCompensateWith()));
	            pst.setBoolean(29, uF.parseToBoolean(getIsCompensatory()));
				pst.setBoolean(30,false);
				pst.setString(31, getData(getSandwichHoliday()));
				pst.setInt(32, uF.parseToInt(getIsSandwich()));
				
				pst.setDouble(33, uF.parseToDouble(getMinLeavesRequiredEncashment()));
				pst.setInt(34, uF.parseToInt(getStrEncashApplicable()));
				pst.setInt(35, uF.parseToInt(getNoOfTimes()));
				pst.setDouble(36, uF.parseToDouble(getMaxLeavesAppliedEncashment()));
				
				pst.setString(37, getData(getSalaryHeadId()));
				pst.setBoolean(38, getIsLongLeave());
				pst.setDouble(39, getIsLongLeave() ? uF.parseToDouble(getLongLeaveLimit()) : 0.0d);
				pst.setString(40, getData1(getLeaveAvailable()));
				pst.setDouble(41, uF.parseToDouble(getPercentage()));
				pst.setBoolean(42, uF.parseToBoolean(getIsLeaveAccrual()));
				pst.setBoolean(43, getIsProrata());
				pst.setInt(44, uF.parseToInt(getAccrualType()));
				pst.setInt(45, uF.parseToInt(getAccrualType()) == 2 ? uF.parseToInt(getNoOfAccrueDays()) : 0);
				pst.setBoolean(46, getIsCarryForward() ? getIsCarriedForwardLimit() : false);
				pst.setDouble(47, getIsCarryForward() ? getIsCarriedForwardLimit() ? uF.parseToDouble(getCarriedForwardLimit()) : 0.0d :  0.0d);
				pst.setBoolean(48, getIsTimePeriod());
				pst.setBoolean(49, getIsActualCalDays());
				pst.setBoolean(50, getIsApplyLeaveLimit());
				pst.setBoolean(51, uF.parseToInt(getAccrualType()) == 1 ? getIsCarryForwardAccrualMonthly() : false);
				pst.setInt(52, uF.parseToInt(getFutureApply()));
				pst.setInt(53, uF.parseToInt(getFutureApplyMax()));
				pst.setBoolean(54, uF.parseToBoolean(getIsWorkFromHome()));
				pst.setInt(55, uF.parseToInt(getDistributedMonth()));
				pst.setInt(56, uF.parseToInt(getPriorApplyOneDayLeave()));
				pst.setInt(57, uF.parseToInt(getLapsDays()));
				pst.setInt(58, uF.parseToInt(getNoDaysForDocument()));
				pst.setDouble(59, getIsLongLeave() ? uF.parseToDouble(getMinimumLongLeaveLimit()) : 0.0d);
				pst.setDouble(60, uF.parseToDouble(getMonthlyApplyLimit()));
				int x = pst.executeUpdate();
//				System.out.println("EIL/550---pst="+pst);
				pst.close();
				
				if(x > 0){
					if(getIsTimePeriod()){
						for(int i = 0; getStrTimeFromDate() != null && i < getStrTimeFromDate().length; i++){
							if(uF.isThisDateValid(getStrTimeFromDate()[i], DATE_FORMAT) && uF.isThisDateValid(getStrTimeToDate()[i], DATE_FORMAT)){
								pst = con.prepareStatement("insert into leave_time_period(time_from,time_to,leave_type_id,level_id,wlocation_id,org_id) " +
										"values(?,?,?,?, ?,?)");
								pst.setDate(1, uF.getDateFormat(getStrTimeFromDate()[i], DATE_FORMAT));
								pst.setDate(2, uF.getDateFormat(getStrTimeToDate()[i], DATE_FORMAT));
								pst.setInt(3, uF.parseToInt(getParam()));
								pst.setInt(4, uF.parseToInt(level));
								pst.setInt(5, uF.parseToInt(getStrLocation()));
								pst.setInt(6, uF.parseToInt(getOrgId()));
								pst.execute();
								pst.close();
							}
						}
					}
					
					setOptionalLeaveLimitData(con,uF,level,strCalendarYearStart,strCalendarYearEnd);
					
					String strDomain = request.getServerName().split("\\.")[0];
					AssignLeaveCron leaveCron = new AssignLeaveCron();
					leaveCron.request = request;
					leaveCron.session = session;
					leaveCron.CF = CF;
					leaveCron.strDomain = strDomain;
					leaveCron.strOrgId = getOrgId();
					leaveCron.strWlocationId = getStrLocation();
					leaveCron.strLevelId = level;
					leaveCron.strLeaveTypeId = getParam();
					leaveCron.leaveAvailable = getLeaveAvailable();
					leaveCron.setCronData();
				}
				
			}
			session.setAttribute(MESSAGE, SUCCESSM+"Leave policy saved successfully."+END);
//			CF.updateLeaveRegister(getLevelType(), getNoOfLeave() , getApprovalDate(),  getTypeOfLeave(), getIsCarryForward(), CF);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private void setOptionalLeaveLimitData(Connection con, UtilityFunctions uF, String level, String strCalendarYearStart, String strCalendarYearEnd) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			boolean flag= false;
			pst = con.prepareStatement("select * from leave_type where org_id=? and leave_type_id=? and is_leave_opt_holiday=true");
			pst.setInt(1, uF.parseToInt(getOrgId()));
			pst.setInt(2, uF.parseToInt(getParam()));
			rs = pst.executeQuery(); 
			while (rs.next()) {
				flag= true;
			}
			if(flag){
				pst = con.prepareStatement("update leave_opt_holiday_details set leave_limit=?,added_by=?,entry_date=? " +
						"where leave_type_id=? and level_id=? and wlocation_id=? and org_id=? and calendar_year_from=? and calendar_year_to=?");
				pst.setInt(1, uF.parseToInt(getOptionalLeaveLimit()));
				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(getParam()));
				pst.setInt(5, uF.parseToInt(level));
				pst.setInt(6, uF.parseToInt(getStrLocation()));
				pst.setInt(7, uF.parseToInt(getOrgId()));
				pst.setDate(8, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));		
				pst.setDate(9, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
//				System.out.println("pst=====>"+pst);
				int x = pst.executeUpdate();
				pst.close();
				
				if(x == 0){
					pst = con.prepareStatement("insert into leave_opt_holiday_details (leave_type_id,level_id,wlocation_id,org_id,calendar_year_from,calendar_year_to,leave_limit,added_by,entry_date)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(getParam()));
					pst.setInt(2, uF.parseToInt(level));
					pst.setInt(3, uF.parseToInt(getStrLocation()));
					pst.setInt(4, uF.parseToInt(getOrgId()));
					pst.setDate(5, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));		
					pst.setDate(6, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
					pst.setInt(7, uF.parseToInt(getOptionalLeaveLimit()));
					pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
//					System.out.println("pst=====>"+pst);
					pst.execute();
					pst.close();
				}
				
				pst = con.prepareStatement("select emp_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
						" and is_alive=true and org_id=? and wlocation_id=? and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
						"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id=?) order by emp_id ");
				pst.setInt(1, uF.parseToInt(getOrgId()));
				pst.setInt(2, uF.parseToInt(getStrLocation()));
				pst.setInt(3, uF.parseToInt(level));
//				System.out.println("EIL/649--pst=======>"+pst);
				rs = pst.executeQuery();
				List<String> alEmpList =new ArrayList<String>();
				while(rs.next()){
					alEmpList.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
	//			System.out.println("alEmpList=======>"+alEmpList.toString());
				for(String strEmp : alEmpList){
					
			//===start parvez date: 20-11-2021===
					String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(strEmp));
					String strLeaveBalance = null;
					if(uF.parseToInt(getDistributedMonth()) > 0){
						java.util.Date empJoinDate = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
						java.util.Date dateCY1 = uF.getDateFormatUtil("01/01/" + uF.getYear(), DATE_FORMAT);
						if(empJoinDate.after(dateCY1)){
							int distributedMonth = 12/uF.parseToInt(getDistributedMonth());
	
							double leaveBalance1 = uF.parseToDouble(getOptionalLeaveLimit()) / distributedMonth;
							
							List<List<String>> distributedMonthList = new ArrayList<List<String>>();
							List<String> monthList = new ArrayList<String>();
							int mnthCnt=0;
							for(int i = 1; i <= 12; i++){

								monthList.add(uF.getMonth(i));

								mnthCnt++;
								if(mnthCnt == uF.parseToInt(getDistributedMonth())) {
									distributedMonthList.add(monthList);
									monthList = new ArrayList<String>();

									mnthCnt=0;
								}
							}
							
							int leaveDistCnt = 0;
							int joinMonth = empJoinDate.getMonth()+1;
							

							for(int i=0; distributedMonthList!=null && i<distributedMonthList.size(); i++) {
								List<String> innerList = distributedMonthList.get(i);

								if(innerList.contains(uF.getMonth(joinMonth))){
									leaveDistCnt = distributedMonthList.size()-i;
									break;
								}
							}
							
							strLeaveBalance = ""+(leaveBalance1 * leaveDistCnt);
							
						}
					}
			//===end parvez date: 20-11-2021===
					
				//===start parvez date: 20-11-2021===		

					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date>=? and leave_type_id=?");
			
					pst.setInt(1,  uF.parseToInt(strEmp));
					pst.setDate(2, uF.getDateFormat(strEmpJoiningDate, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(getParam()));
//					System.out.println("LR/150--pst="+pst);
					pst.execute();
					pst.close();
				//===end parvez date: 20-11-2021===
					
//					System.out.println("EIL/706--strLeaveBalance="+strLeaveBalance);
					pst=con.prepareStatement("update leave_register1 set balance=? where emp_id=? and _date=? and leave_type_id=? and _type=?");
//					pst.setDouble(1, uF.parseToDouble(getOptionalLeaveLimit()));
			//===start parvez date: 20-11-2021===
					if(uF.parseToDouble(strLeaveBalance) > 0){
						pst.setDouble(1, uF.parseToDouble(strLeaveBalance));
					} else{
						pst.setDouble(1, uF.parseToDouble(getOptionalLeaveLimit()));
					}
			//===end parvez date: 20-11-2021===
					pst.setInt(2, uF.parseToInt(strEmp));
			//===start parvez date: 23-11-2021===
					if(uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT).after(uF.getDateFormatUtil(strCalendarYearStart, DATE_FORMAT))){
						pst.setDate(3, uF.getDateFormat(strEmpJoiningDate, DATE_FORMAT));
					} else{
						pst.setDate(3, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
					}
			//===end parvez date: 23-11-2021===
//					pst.setDate(3, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(getParam()));
					pst.setString(5, "C");
//					System.out.println("EIL/727--pst--"+pst);
					int y = pst.executeUpdate();
					pst.close();
					
					if(y == 0){
						pst=con.prepareStatement("insert into leave_register1(emp_id,_date,balance,leave_type_id,_type)values(?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmp));
				//===start parvez date: 23-11-2021===
						if(uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT).after(uF.getDateFormatUtil(strCalendarYearStart, DATE_FORMAT))){
							pst.setDate(2, uF.getDateFormat(strEmpJoiningDate, DATE_FORMAT));
						} else{
							pst.setDate(2, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
						}
				//===end parvez date: 23-11-2021===
//						pst.setDate(2, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
//						pst.setDouble(3, uF.parseToDouble(getOptionalLeaveLimit()));
				//===start parvez date: 20-11-2021===
						if(uF.parseToDouble(strLeaveBalance) > 0){
							pst.setDouble(3, uF.parseToDouble(strLeaveBalance));
						} else{
							pst.setDouble(3, uF.parseToDouble(getOptionalLeaveLimit()));
						}
				//===end parvez date: 20-11-2021===
						pst.setInt(4, uF.parseToInt(getParam()));
						pst.setString(5, "C");
//						System.out.println("EIL/752--pst="+pst);
						pst.execute();
						pst.close();
					}
				}
		//===start parvez date: 24-11-2021===		
			} else{
//				updateEmployeeLeaveBalance1(level);
			}
		//===end parvez date: 24-11-2021===	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getData(List<String> list) {
		StringBuilder a=null;
        if (list != null) {
            for (String b : list) {
                if (a == null) {
                    a = new StringBuilder(b);
                } else {
                    a.append("," + b);
                }
            }
        }
        if (a == null)
            return null;
		return a.toString();
	}
	
	public String getData1(List<String> list) {
		StringBuilder a=null;
        if (list != null) {
            for (String b : list) {
                if (a == null) {
                    a = new StringBuilder();
                    a.append(","+b+",");
                } else {
                    a.append(b+",");
                }
            }
        }
        if (a == null)
            return null;
		return a.toString();
	}
	
	public List<String> getSuffix() {
		return suffix;
	}
	
	public void setSuffix(List<String> suffix) {
		this.suffix = suffix;
	}
	
	public List<String> getCombination() {
		return combination;
	}
	
	public void setCombination(List<String> combination) {
		this.combination = combination;
	}
	
	public List<FillLeaveType> getLeaveList() {
		return leaveList;
	}
	
	public void setLeaveList(List<FillLeaveType> leaveList) {
		this.leaveList = leaveList;
	}
	
	public String getParam() {
		return param;
	}
	
	public void setParam(String param) {
		this.param = param;
	}
	
	public List<FillLeaveType> getLeaveList1() {
		return leaveList1;
	}
	
	public void setLeaveList1(List<FillLeaveType> leaveList1) {
		this.leaveList1 = leaveList1;
	}
	
	public String getBalance() {
		return balance;
	}
	
	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	public String getBalanceLimit() {
		return balanceLimit;
	}
	
	public void setBalanceLimit(String balanceLimit) {
		this.balanceLimit = balanceLimit;
	}
	
	public List<String> getPrefix() {
		return prefix;
	}
	
	public void setPrefix(List<String> prefix) {
		this.prefix = prefix;
	}
	
	public String getPriorApply() {
		return priorApply;
	}
	
	public void setPriorApply(String priorApply) {
		this.priorApply = priorApply;
	}
	
	public String getLeaveLimit() {
		return leaveLimit;
	}
	
	public void setLeaveLimit(String leaveLimit) {
		this.leaveLimit = leaveLimit;
	}
	
	public boolean getNY() {
		return NY;
	}
	
	public void setNY(boolean NY) {
		this.NY = NY;
	}
	
	public String viewEmployeeIssueLeave(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			pst = con.prepareStatement("select * from emp_leave_type where emp_leave_type_id =?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
//			System.out.println("pst=====>"+pst);
//			String policy_id = "";
			int orgId = 0;
			int wlocationId = 0;
			int levelId = 0;
			int levelTypeId = 0;
			while (rs.next()) {
				List<String> levelType=new ArrayList<String>();
				levelType.add(rs.getString("level_id"));
				setLevelType(levelType);
				setParam(rs.getString("leave_type_id"));
				setNoOfLeave(rs.getString("no_of_leave"));
				setApprovalDate(rs.getString("effective_date_type"));
				setIsCarryForward(uF.parseToBoolean(rs.getString("is_carryforward")));
				setIspaid(uF.parseToBoolean(rs.getString("is_paid")));
				setEmpLeaveTypeId(rs.getString("emp_leave_type_id"));
				setIsMonthlyCarryForward(uF.parseToBoolean(rs.getString("is_monthly_carryforward")));
				setConsLeaveLimit(rs.getString("consecutive_limit"));
				setMonthlyLeaveLimit(rs.getString("monthly_limit"));
				setIsApproval(uF.parseToBoolean(rs.getString("is_approval")));
				setOrgId(rs.getString("org_id"));
//				policy_id=rs.getString("policy_id");
				setNoOfLeaveMonthly(rs.getString("no_of_leave_monthly"));
				setAccrualSystem(rs.getString("accrual_system"));
				setAccrualFrom(rs.getString("accrual_from"));
				
				String strBalanceSatus = uF.parseToBoolean(rs.getString("balance_validation")) ? "true" : "false";
				setBalance(strBalanceSatus);
				setDefaultBalance(strBalanceSatus);
				setBalanceLimit(rs.getString("validation_days"));
				if (rs.getString("leave_suffix")!=null){
					setSuffix(Arrays.asList(rs.getString("leave_suffix").split(",")));
				}
				if (rs.getString("leave_prefix")!=null){
					setPrefix(Arrays.asList(rs.getString("leave_prefix").split(",")));
				}
				setPriorApply(rs.getString("prior_days"));
				setFutureApply(rs.getString("future_days"));
				setFutureApplyMax(rs.getString("future_days_max"));
				setLeaveLimit(rs.getString("leave_limit"));
				setNY(uF.parseToBoolean(rs.getString("next_year_leave")));
				setMaternityFrequency(rs.getString("maternity_type_frequency"));
				
				if (rs.getString("combination_leave")!=null){
					setCombination(Arrays.asList(rs.getString("combination_leave").split(",")));
				}
				setCompensateWith(rs.getString("compensate_with"));
				
				setIsSandwich(rs.getString("sandwich_type"));
				if (rs.getString("sandwich_leave_type")!=null){
					setSandwichHoliday(Arrays.asList(rs.getString("sandwich_leave_type").split(",")));
				}
				
				setMinLeavesRequiredEncashment(uF.parseToDouble(rs.getString("min_leave_encashment"))+"");
				setDefaultSelectEncash(rs.getString("encashment_applicable"));
				setNoOfTimes(rs.getString("encashment_times"));
				
				setMaxLeavesAppliedEncashment(rs.getString("max_leave_encash"));
				
				if (rs.getString("salary_head_id")!=null){
					setSalaryHeadId(Arrays.asList(rs.getString("salary_head_id").split(",")));
				}
				
				setIsLongLeave(uF.parseToBoolean(rs.getString("is_long_leave")));
				setLongLeaveLimit(rs.getString("long_leave_limit"));
				
				request.setAttribute("longLeave", rs.getString("is_long_leave"));
				
				if (rs.getString("leave_available")!=null){
					setLeaveAvailable(Arrays.asList(rs.getString("leave_available").split(",")));
				}
				setPercentage(rs.getString("percentage"));
				
				setIsLeaveAccrual(uF.parseToBoolean(rs.getString("is_leave_accrual")) ? "true" : "false");
				
				orgId = uF.parseToInt(rs.getString("org_id"));
				wlocationId = uF.parseToInt(rs.getString("wlocation_id"));
				levelId = uF.parseToInt(rs.getString("level_id"));
				levelTypeId = uF.parseToInt(rs.getString("leave_type_id"));
				
				setIsProrata(uF.parseToBoolean(rs.getString("is_prorata")));
				
				setDefaultAccrualType(""+uF.parseToInt(rs.getString("accrual_type")));
				setNoOfAccrueDays(""+uF.parseToInt(rs.getString("accrual_days")));
				
//				,is_carryforward_limit,carryforward_limit
				if(uF.parseToBoolean(rs.getString("is_carryforward"))){
					setIsCarriedForwardLimit(uF.parseToBoolean(rs.getString("is_carryforward_limit")));
					if(uF.parseToBoolean(rs.getString("is_carryforward_limit"))){
						setCarriedForwardLimit(""+uF.parseToDouble(rs.getString("carryforward_limit")));
					}
				}
				
				setIsTimePeriod(uF.parseToBoolean(rs.getString("is_time_period")));
				setIsActualCalDays(uF.parseToBoolean(rs.getString("is_accrued_cal_days")));
				
				setIsApplyLeaveLimit(uF.parseToBoolean(rs.getString("is_apply_leave_limit")));
				
				boolean isCarriedAccrualMonthly = false;
				if(uF.parseToInt(rs.getString("accrual_type")) == 1){
					isCarriedAccrualMonthly = uF.parseToBoolean(rs.getString("is_carryforward_accrual_monthly"));
				}
				setIsCarryForwardAccrualMonthly(isCarriedAccrualMonthly);
				setDistributedMonth(rs.getString("distributed_month"));
			//===start parvez date: 26-09-2022===	
				setPriorApplyOneDayLeave(rs.getString("prior_days_for_one_day_leave"));
				setLapsDays(rs.getString("laps_days"));
				setNoDaysForDocument(rs.getString("no_of_days_for_document_upload"));
			//===end parvez date: 26-09-2022===
				
				if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_BALANCE_FOR_EMPLOYEE_JOINING_MONTH))){
					setJoiningMonthDay(rs.getString("joining_month_day_date"));
					setJoiningMonthLeaveBalance(rs.getString("joining_month_balance"));
				}
				
				if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_PRIOR_DAYS_NOTIFICATION))){
					setFutureApply1(rs.getString("future_days_1"));
					setNoOfLeave1(rs.getString("no_of_leaves1"));
					setNoOfLeave2(rs.getString("no_of_leaves2"));
					setNoOfLeave3(rs.getString("no_of_leaves3"));
				}
				if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE))){
					setLongLeaveGap(rs.getString("long_leave_gap"));
				}
				
				setMinimumLongLeaveLimit(rs.getString("min_long_leave_limit"));
				setMonthlyApplyLimit(rs.getString("monthly_apply_leave_limit"));
				
			}
			rs.close();
			pst.close();
//			request.setAttribute("policy_id", policy_id);
			
			if(levelTypeId> 0 && orgId > 0 && wlocationId > 0 && levelId > 0){
				
				if(getIsTimePeriod()){
					pst = con.prepareStatement("select * from leave_time_period where leave_type_id=? and level_id=? and wlocation_id=? " +
							"and org_id=?");
					pst.setInt(1, levelTypeId);
					pst.setInt(2, levelId);
					pst.setInt(3, wlocationId);
					pst.setInt(4, orgId);
					rs = pst.executeQuery();
					List<List<String>> alTimePeriod = new ArrayList<List<String>>();
					while(rs.next()){
						List<String> alInner = new ArrayList<String>();
						alInner.add(uF.getDateFormat(rs.getString("time_from"), DBDATE, DATE_FORMAT));
						alInner.add(uF.getDateFormat(rs.getString("time_to"), DBDATE, DATE_FORMAT));
						
						alTimePeriod.add(alInner);
					}
					rs.close();
					pst.close();
					
					request.setAttribute("alTimePeriod",alTimePeriod);
				}
				
				
				pst = con.prepareStatement("select * from leave_opt_holiday_details where leave_type_id=? and level_id=? and wlocation_id=? and org_id=? and ? between calendar_year_from and calendar_year_to");
				pst.setInt(1, levelTypeId);
				pst.setInt(2, levelId);
				pst.setInt(3, wlocationId);
				pst.setInt(4, orgId);
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				
				rs = pst.executeQuery();
				while(rs.next()){
					String strCalendarYearFrom = uF.getDateFormat(rs.getString("calendar_year_from"), DBDATE, DATE_FORMAT);
					String strCalendarYearTo = uF.getDateFormat(rs.getString("calendar_year_to"), DBDATE, DATE_FORMAT);
					setCalendarYear(strCalendarYearFrom + "-" + strCalendarYearTo);
					
					setOptionalLeaveLimit(rs.getString("leave_limit"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from leave_opt_holiday_details where leave_type_id=? and level_id=? and wlocation_id=? and org_id=? order by calendar_year_from desc");
				pst.setInt(1, levelTypeId);
				pst.setInt(2, levelId);
				pst.setInt(3, wlocationId);
				pst.setInt(4, orgId);
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alOptionalHoliday = new ArrayList<Map<String,String>>();
				while(rs.next()){
					String strCalendarYearFrom = uF.getDateFormat(rs.getString("calendar_year_from"), DBDATE, DATE_FORMAT);
					String strCalendarYearTo = uF.getDateFormat(rs.getString("calendar_year_to"), DBDATE, DATE_FORMAT);
					
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("CALENDAR_YEAR", strCalendarYearFrom + "-" + strCalendarYearTo);
					hmInner.put("LEAVE_LIMIT", ""+uF.parseToInt(rs.getString("leave_limit")));
					
					alOptionalHoliday.add(hmInner);
				}
				rs.close();
				pst.close();
//				System.out.println("alOptionalHoliday======>"+alOptionalHoliday);
				request.setAttribute("alOptionalHoliday", alOptionalHoliday);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst); 
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String updateEmployeeIssueLeave() {
		
		Connection con = null;
		PreparedStatement pst =null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
	//===start parvez date: 26-09-2022===
//		String updateEmpLeaveType = "UPDATE emp_leave_type SET level_id=?, leave_type_id=?, no_of_leave=?, is_paid=?, is_carryforward=?, entrydate=?, " +
//			"effective_date=?, effective_date_type=?, user_id=?, monthly_limit=?, consecutive_limit=?, is_monthly_carryforward=?, is_approval=?," +
//			"org_id=?, no_of_leave_monthly=?,accrual_system=?, accrual_from=?,wlocation_id=?,balance_validation=?,validation_days=?,leave_suffix=?," +
//			"leave_prefix=?,prior_days=?,leave_limit=?,next_year_leave=?,maternity_type_frequency=?,combination_leave=?,compensate_with=?," +
//			"is_compensatory=?,is_sandwich=?,sandwich_leave_type=?,sandwich_type=?,min_leave_encashment=?,encashment_applicable=?,encashment_times=?," +
//			"max_leave_encash=?,salary_head_id=?,is_long_leave=?,long_leave_limit=?,leave_available=?,percentage=?,is_leave_accrual=?,is_prorata=?," +
//			"accrual_type=?,accrual_days=?,is_carryforward_limit=?,carryforward_limit=?,is_time_period=?,is_accrued_cal_days=?,is_apply_leave_limit=?," +
//			"is_carryforward_accrual_monthly=?,future_days=?,future_days_max=?,is_work_from_home=?,distributed_month=?,prior_days_for_one_day_leave=?," +
//			"laps_days=?,no_of_days_for_document_upload=? WHERE emp_leave_type_id=?";
	//===end parvez date: 26-09-2022===
		try {
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
			if (getCalendarYear() != null) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
//			System.out.println("EIL/1126--getBalance()====>"+getBalance()+"---uF.parseToBoolean(getBalance())====>"+uF.parseToBoolean(getBalance()));
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			for(String level:getLevelType()){
//				pst = con.prepareStatement(updateEmpLeaveType);
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("UPDATE emp_leave_type SET level_id=?, leave_type_id=?, no_of_leave=?, is_paid=?, is_carryforward=?, entrydate=?, " +
						"effective_date=?, effective_date_type=?, user_id=?, monthly_limit=?, consecutive_limit=?, is_monthly_carryforward=?, is_approval=?," +
						"org_id=?, no_of_leave_monthly=?,accrual_system=?, accrual_from=?,wlocation_id=?,balance_validation=?,validation_days=?,leave_suffix=?," +
						"leave_prefix=?,prior_days=?,leave_limit=?,next_year_leave=?,maternity_type_frequency=?,combination_leave=?,compensate_with=?," +
						"is_compensatory=?,is_sandwich=?,sandwich_leave_type=?,sandwich_type=?,min_leave_encashment=?,encashment_applicable=?,encashment_times=?," +
						"max_leave_encash=?,salary_head_id=?,is_long_leave=?,long_leave_limit=?,leave_available=?,percentage=?,is_leave_accrual=?,is_prorata=?," +
						"accrual_type=?,accrual_days=?,is_carryforward_limit=?,carryforward_limit=?,is_time_period=?,is_accrued_cal_days=?,is_apply_leave_limit=?," +
						"is_carryforward_accrual_monthly=?,future_days=?,future_days_max=?,is_work_from_home=?,distributed_month=?,prior_days_for_one_day_leave=?," +
						"laps_days=?,no_of_days_for_document_upload=?,min_long_leave_limit=?,monthly_apply_leave_limit=? ");
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_BALANCE_FOR_EMPLOYEE_JOINING_MONTH))){
					sbQuery.append(", joining_month_day_date="+uF.parseToInt(getJoiningMonthDay()));
					sbQuery.append(", joining_month_balance="+uF.parseToDouble(getJoiningMonthLeaveBalance())+" ");
				}
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_LEAVE_PRIOR_DAYS_NOTIFICATION))){
					sbQuery.append(", future_days_1="+uF.parseToInt(getFutureApply1()));
					sbQuery.append(", no_of_leaves1="+uF.parseToInt(getNoOfLeave1())+" ");
					sbQuery.append(", no_of_leaves2="+uF.parseToInt(getNoOfLeave2())+" ");
					sbQuery.append(", no_of_leaves3="+uF.parseToInt(getNoOfLeave3())+" ");
				}
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GAP_BETWEEN_TWO_APPLIED_LONG_LEAVE))){
					sbQuery.append(", long_leave_gap="+uF.parseToInt(getLongLeaveGap()));
				}
				sbQuery.append(" WHERE emp_leave_type_id=?");
				pst = con.prepareStatement(sbQuery.toString());
//				pst.setInt(1, uF.parseToInt(getLevelType()));
				pst.setInt(1, uF.parseToInt(level));
				pst.setInt(2, uF.parseToInt(getParam()));
				pst.setDouble(3, uF.parseToDouble(getNoOfLeave()));
				pst.setBoolean(4, getIspaid());
				pst.setBoolean(5, getIsCarryForward());
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				
				if (getApprovalDate().contains("CY")) {
					pst.setDate(7, uF.getDateFormat("01/01/"+uF.getYear()+"",DATE_FORMAT));
				} else if (getApprovalDate().contains("FY")) {
					pst.setDate(7, uF.getDateFormat(strFinancialYearStart+"",DATE_FORMAT));
				} else {
					pst.setDate(7, uF.getDateFormat("01/01/"+uF.getYear()+"", DATE_FORMAT));
				}
				pst.setString(8, getApprovalDate());
				pst.setInt(9, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setInt(10, uF.parseToInt(getMonthlyLeaveLimit()));
				pst.setInt(11, uF.parseToInt(getConsLeaveLimit()));
				pst.setBoolean(12, getIsMonthlyCarryForward());
				pst.setBoolean(13, getIsApproval());
				pst.setInt(14, uF.parseToInt(getOrgId()));
				pst.setDouble(15, uF.parseToDouble(getNoOfLeaveMonthly()));
				pst.setInt(16, uF.parseToInt(getAccrualSystem()));
				pst.setInt(17, uF.parseToInt(getAccrualFrom()));
				pst.setInt(18, uF.parseToInt(getStrLocation()));
				pst.setBoolean(19, uF.parseToBoolean(getBalance()));
				pst.setInt(20, uF.parseToInt(getBalanceLimit()));
				pst.setString(21, getData(getSuffix()));
				pst.setString(22,getData(getPrefix()));
				pst.setInt(23, uF.parseToInt(getPriorApply()));
				pst.setInt(24, uF.parseToInt(getLeaveLimit()));
				pst.setBoolean(25, getNY());
				pst.setInt(26, uF.parseToInt(getMaternityFrequency()));
				
				String combineLeave = getData(getCombination());
				pst.setString(27, combineLeave!=null ? getParam()+","+combineLeave : getParam());
				
				pst.setInt(28, uF.parseToInt(getCompensateWith()));
	            pst.setBoolean(29, uF.parseToBoolean(getIsCompensatory()));
				pst.setBoolean(30,false);
				pst.setString(31, getData(getSandwichHoliday()));
				pst.setInt(32, uF.parseToInt(getIsSandwich()));
				pst.setDouble(33, uF.parseToDouble(getMinLeavesRequiredEncashment()));
				pst.setInt(34, uF.parseToInt(getStrEncashApplicable()));
				pst.setInt(35, uF.parseToInt(getNoOfTimes()));
				pst.setDouble(36, uF.parseToDouble(getMaxLeavesAppliedEncashment()));
				
				pst.setString(37, getData(getSalaryHeadId()));
				pst.setBoolean(38, getIsLongLeave());
				pst.setDouble(39, getIsLongLeave() ? uF.parseToDouble(getLongLeaveLimit()) : 0.0d);
				
				pst.setString(40, getData1(getLeaveAvailable()));
				pst.setDouble(41, uF.parseToDouble(getPercentage()));
				pst.setBoolean(42, uF.parseToBoolean(getIsLeaveAccrual())); 
				pst.setBoolean(43, getIsProrata());
				pst.setInt(44, uF.parseToInt(getAccrualType()));
				pst.setInt(45, uF.parseToInt(getAccrualType()) == 2 ? uF.parseToInt(getNoOfAccrueDays()) : 0);
				pst.setBoolean(46, getIsCarryForward() ? getIsCarriedForwardLimit() : false);
				pst.setDouble(47, getIsCarryForward() ? getIsCarriedForwardLimit() ? uF.parseToDouble(getCarriedForwardLimit()) : 0.0d :  0.0d);
				pst.setBoolean(48, getIsTimePeriod());
				pst.setBoolean(49, getIsActualCalDays());
				pst.setBoolean(50, getIsApplyLeaveLimit());
				pst.setBoolean(51, uF.parseToInt(getAccrualType()) == 1 ? getIsCarryForwardAccrualMonthly() : false);
				pst.setInt(52, uF.parseToInt(getFutureApply()));
				pst.setInt(53, uF.parseToInt(getFutureApplyMax()));
				pst.setBoolean(54, uF.parseToBoolean(getIsWorkFromHome()));
				pst.setInt(55, uF.parseToInt(getDistributedMonth()));
//				pst.setInt(56, uF.parseToInt(getEmpLeaveTypeId()));
				pst.setInt(56, uF.parseToInt(getPriorApplyOneDayLeave()));
				pst.setInt(57, uF.parseToInt(getLapsDays()));
				pst.setInt(58, uF.parseToInt(getNoDaysForDocument()));
				pst.setDouble(59, getIsLongLeave() ? uF.parseToDouble(getMinimumLongLeaveLimit()) : 0.0d);
				pst.setDouble(60, uF.parseToDouble(getMonthlyApplyLimit()));
				pst.setInt(61, uF.parseToInt(getEmpLeaveTypeId()));
				
//				System.out.println("EIL/1201--pst=====>"+pst);
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0){
					if(getIsTimePeriod()){
						pst = con.prepareStatement("delete from leave_time_period where leave_type_id=? and level_id=? and wlocation_id=? " +
								"and org_id=?");
						pst.setInt(1, uF.parseToInt(getParam()));
						pst.setInt(2, uF.parseToInt(level));
						pst.setInt(3, uF.parseToInt(getStrLocation()));
						pst.setInt(4, uF.parseToInt(getOrgId()));
						pst.execute();
						pst.close();
						
						for(int i = 0; getStrTimeFromDate() != null && i < getStrTimeFromDate().length; i++){
							if(uF.isThisDateValid(getStrTimeFromDate()[i], DATE_FORMAT) && uF.isThisDateValid(getStrTimeToDate()[i], DATE_FORMAT)){
								pst = con.prepareStatement("insert into leave_time_period(time_from,time_to,leave_type_id,level_id,wlocation_id,org_id) " +
										"values(?,?,?,?, ?,?)");
								pst.setDate(1, uF.getDateFormat(getStrTimeFromDate()[i], DATE_FORMAT));
								pst.setDate(2, uF.getDateFormat(getStrTimeToDate()[i], DATE_FORMAT));
								pst.setInt(3, uF.parseToInt(getParam()));
								pst.setInt(4, uF.parseToInt(level));
								pst.setInt(5, uF.parseToInt(getStrLocation()));
								pst.setInt(6, uF.parseToInt(getOrgId()));
								pst.execute();
								pst.close();
							}
						}
					}
					
					setOptionalLeaveLimitData(con,uF,level,strCalendarYearStart,strCalendarYearEnd);
					
					String strDomain = request.getServerName().split("\\.")[0];
					AssignLeaveCron leaveCron = new AssignLeaveCron();
					leaveCron.request = request;
					leaveCron.session = session;
					leaveCron.CF = CF;
					leaveCron.strDomain = strDomain;
					leaveCron.strOrgId = getOrgId();
					leaveCron.strWlocationId = getStrLocation();
					leaveCron.strLevelId = level;
					leaveCron.strLeaveTypeId = getParam();
					leaveCron.leaveAvailable = getLeaveAvailable();
					leaveCron.setCronData();
				}
			}
			session.setAttribute(MESSAGE, SUCCESSM+"Leave policy updated successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con); 
		}
		return SUCCESS;
	}

	public String deleteEmployeeIssueLeave(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
        Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteEmployeeIssueLeave);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE, SUCCESSM+"Leave policy deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public void validate() {
		
	    loadValidateEmployeeIssueLeave();
	}
	
	private Map<String, String> getLocationName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
				
		Map<String,String> hmLocationName=new HashMap<String, String>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from work_location_info");
			rs=pst.executeQuery();
			while (rs.next()) {
				hmLocationName.put(rs.getString("wlocation_id"),rs.getString("wlocation_name"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmLocationName;
	}
	
	private Map<String, String> getOrganization() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		Map<String,String> hmOrg=new HashMap<String, String>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select org_id,org_name from org_details");
			rs=pst.executeQuery();
			while (rs.next()) {
				hmOrg.put(rs.getString("org_id"),rs.getString("org_name"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmOrg;
	}
	
//===start parvez date: 24-11-2021===	
	/*private String updateEmployeeLeaveBalance1(String level) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select no_of_leave from emp_leave_type where leave_type_id=? and level_id=? and org_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getParam()));
			pst.setInt(2, uF.parseToInt(level));
			pst.setInt(3, uF.parseToInt(getOrgId()));
			pst.setInt(4, uF.parseToInt(getStrLocation()));
//			System.out.println("EIL/1379--pst=======>"+pst);
			rs = pst.executeQuery();
			double noOfLeave = 0.0d;
			while(rs.next()){
				noOfLeave = uF.parseToDouble(rs.getString("no_of_leave"));
			}
			rs.close();
			pst.close();
//			System.out.println("noOfLeave=======>"+noOfLeave);
			if(noOfLeave>0){
				pst = con.prepareStatement("select emp_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
						" and is_alive=true and org_id=? and wlocation_id=? and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
						"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id=?) order by emp_id ");
				pst.setInt(1, uF.parseToInt(getOrgId()));
				pst.setInt(2, uF.parseToInt(getStrLocation()));
				pst.setInt(3, uF.parseToInt(level));
//				System.out.println("EIL/1395--pst=======>"+pst);
				rs = pst.executeQuery();
				List<String> alEmpList =new ArrayList<String>();
				while(rs.next()){
					alEmpList.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
//				System.out.println("alEmpList=======>"+alEmpList.toString());
				boolean flag = false;
				for(String strEmp : alEmpList){
			
					String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(strEmp));
					String strCurrentDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
					
					double dblTotal = 0;
					
					if (uF.parseToBoolean(getIsLeaveAccrual())) {
						if (uF.parseToInt(getAccrualType()) == 2 && getIsActualCalDays()) {
							if ("CY".equalsIgnoreCase(getApprovalDate())) {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil("01/01/" + uF.getYear(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/01/" + uF.getYear();
								}
								dblTotal = getAccuralLeavesCount(CF, uF, strJoinDate, strCurrentDate, uF.parseToDouble(getNoOfLeaveMonthly()));
//								System.out.println("CF/31263--dblTotal="+dblTotal);
							} else if ("FY".equalsIgnoreCase(getApprovalDate())) {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil(CF.getStrFinancialYearTo(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/04/" + uF.getYear();
								}
								dblTotal = getAccuralLeavesCount(CF, uF, strJoinDate, strCurrentDate, uF.parseToDouble(getNoOfLeaveMonthly()));
//								 System.out.println("CF/31271--FY dblTotal ========>> "+dblTotal+"--uF.getYear()==>"+uF.getYear()+"--no_of_leave==>"+rs.getDouble("no_of_leave"));
							} else {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil("01/01/" + uF.getYear(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/01/" + uF.getYear();
								}
								dblTotal = getAccuralLeavesCount(CF, uF, strJoinDate, strCurrentDate, uF.parseToDouble(getNoOfLeaveMonthly()));
//								System.out.println("CF/31280--dblTotal="+dblTotal);
							}
						} else if (uF.parseToInt(getAccrualType()) == 1) {
							if ("CY".equalsIgnoreCase(getApprovalDate())) {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil("01/01/" + uF.getYear(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/01/" + uF.getYear();
								}
								dblTotal = getLeavesCount1(strJoinDate, strCurrentDate, uF.parseToDouble(getNoOfLeave()), getIsCarryForward(), CF);
//								System.out.println("CF/31292--dblTotal="+dblTotal);
							} else if ("FY".equalsIgnoreCase(getApprovalDate())) {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil(CF.getStrFinancialYearTo(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/04/" + uF.getYear();
								}
								dblTotal = getLeavesCount1(strJoinDate, strCurrentDate, uF.parseToDouble(getNoOfLeave()), getIsCarryForward(), CF);
//								System.out.println("CF/31301--dblTotal="+dblTotal);
							} else {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil("01/01/" + uF.getYear(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/01/" + uF.getYear();
								}
								dblTotal = getLeavesCount1(strJoinDate, strCurrentDate, uF.parseToDouble(getNoOfLeave()), getIsCarryForward(), CF);
//								System.out.println("CF/31310--dblTotal="+dblTotal);
							}
						}
					} else {
						
						if (getIsProrata()) {
							if ("CY".equalsIgnoreCase(getApprovalDate())) {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil("01/01/" + uF.getYear(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/01/" + uF.getYear();
								}
								dblTotal = getLeavesCount1(strJoinDate, uF.getDateFormat("31/12/" + uF.getYear(), DATE_FORMAT, DATE_FORMAT),
										uF.parseToDouble(getNoOfLeave()), getIsCarryForward(), CF);
								
							} else if ("FY".equalsIgnoreCase(getApprovalDate())) {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil(CF.getStrFinancialYearTo(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/04/" + uF.getYear();
								}
								dblTotal = getLeavesCount1(strJoinDate, CF.getStrFinancialYearTo(), uF.parseToDouble(getNoOfLeave()),
										getIsCarryForward(), CF);
								
							} else {
								java.util.Date dateJoin = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
								java.util.Date dateCY = uF.getDateFormatUtil("01/01/" + uF.getYear(), DATE_FORMAT);
								String strJoinDate = strEmpJoiningDate;
								if (dateJoin.before(dateCY)) {
									strJoinDate = "01/01/" + uF.getYear();
								}
								dblTotal = getLeavesCount1(strJoinDate, uF.getDateFormat("31/12/" + uF.getYear(), DATE_FORMAT, DATE_FORMAT),
										uF.parseToDouble(getNoOfLeave()), getIsCarryForward(), CF);
								
							}
						} else {
							
							if (!uF.parseToBoolean(getIsLeaveAccrual())) {
								dblTotal = rs.getDouble("no_of_leave");
							}
						}
					}
			//===end parvez date: 20-11-2021===
					
					pst=con.prepareStatement("update leave_register1 set balance=? where emp_id=? and _date=? and leave_type_id=? and _type=?");
					pst.setDouble(1, dblTotal);
					pst.setInt(2, uF.parseToInt(strEmp));
					pst.setDate(3, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
					pst.setInt(4, uF.parseToInt(getParam()));
					pst.setString(5, "C");
//					System.out.println("EIL/1412--update pst=====>"+pst);
					int x = pst.executeUpdate();
					pst.close();
					
					if(x == 0){
						pst=con.prepareStatement("insert into leave_register1(emp_id,_date,balance,leave_type_id,_type)values(?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmp));
						pst.setDate(2, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
						pst.setDouble(3, dblTotal);
						pst.setInt(4, uF.parseToInt(getParam()));
						pst.setString(5, "C");
//						System.out.println("EIL/260--pst=====>"+pst);
						pst.execute();
						pst.close();
					}
					flag =true;
				}
				if(flag){
					session.setAttribute(MESSAGE, SUCCESSM+"Employee Leave Balance Updated Successfully."+END);
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Employee leave balance not updated. Please check annual leave balance"+END);
				}
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Employee leave balance not updated. Please check annual leave balance"+END);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in insertion");
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
		
		private double getAccuralLeavesCount(CommonFunctions CF, UtilityFunctions uF, String strJoinDate, String strCurrentDate, double nNumberOfLeaves) {

			double empAnnualyLeaveBal = 0;
			try {
				int nDays = uF.parseToInt(uF.dateDifference(strJoinDate, DATE_FORMAT, strCurrentDate, DATE_FORMAT, CF.getStrTimeZone()));

				empAnnualyLeaveBal = nNumberOfLeaves * nDays;
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			return empAnnualyLeaveBal;
		}
		
		
		private double getLeavesCount1(String strJoiningtDate, String strYearEndDate, double nNumberOfLeaves, boolean isCaryForward, CommonFunctions cF) {

			UtilityFunctions uF = new UtilityFunctions();
			double empAnnualyLeaveBal = 0;
			try {
				// System.out.println("strJoiningtDate=>"+strJoiningtDate+"--strYearEndDate=>"+strYearEndDate);
				double monthDiffCount = uF.getMonthsDifference(uF.getDateFormatUtil(strJoiningtDate, DATE_FORMAT),
						uF.getDateFormatUtil(strYearEndDate, DATE_FORMAT));
				double monthlyLeaveBal = nNumberOfLeaves / 12;

				empAnnualyLeaveBal = monthlyLeaveBal * monthDiffCount;
//				 System.out.println("CF/9016---nNumberOfLeaves=>"+nNumberOfLeaves+"--monthDiffCount=>"+monthDiffCount+"--monthlyLeaveBal==>"+monthlyLeaveBal+"--empAnnualyLeaveBal==>"+empAnnualyLeaveBal);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return empAnnualyLeaveBal;
		}*/
		
//===end parvez date: 24-11-2021===		
	
	public String getEmpLeaveTypeId() {
		return empLeaveTypeId;
	}
	
	public void setEmpLeaveTypeId(String empLeaveTypeId) {
		this.empLeaveTypeId = empLeaveTypeId;
	}
	
	public String getEmployeType() {
		return employeType;
	}
	
	public void setEmployeType(String employeType) {
		this.employeType = employeType;
	}
	
//	public String getTypeOfLeave() {
//		return typeOfLeave;
//	}
	
//	public void setTypeOfLeave(String typeOfLeave) {
//		this.typeOfLeave = typeOfLeave;
//	}
	
	public String getNoOfLeave() {
		return noOfLeave;
	}
	
	public void setNoOfLeave(String noOfLeave) {
		this.noOfLeave = noOfLeave;
	}
	
	public List<FillUserType> getUserTypeList() {
		return userTypeList;
	}

	public List<FillLeaveType> getEmpLeaveTypeList() {
		return empLeaveTypeList;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public List<FillApproval> getApprovalList() {
		return approvalList;
	}
	
	public void setApprovalList(List<FillApproval> approvalList) {
		this.approvalList = approvalList;
	}
	
	public String getEffectiveFrom() {
		return effectiveFrom;
	}
	
	public void setEffectiveFrom(String effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	
	public String getMaternityFrequency() {
		return maternityFrequency;
	}
	
	public void setMaternityFrequency(String maternityFrequency) {
		this.maternityFrequency = maternityFrequency;
	}
	
	public String getApprovalDate() {
		return approvalDate;
	}
	
	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}
	
	public void setUserTypeList(List<FillUserType> userTypeList) {
		this.userTypeList = userTypeList;
	}
	
	public void setEmpLeaveTypeList(List<FillLeaveType> empLeaveTypeList) {
		this.empLeaveTypeList = empLeaveTypeList;
	}
	
	public List<FillLevel> getLevelTypeList() {
		return levelTypeList;
	}
	
	public List<String> getLevelType() {
		return levelType;
	}
	
	public void setLevelType(List<String> levelType) {
		this.levelType = levelType;
	}
	
	public boolean getIspaid() {
		return ispaid;
	}
	
	public void setIspaid(boolean ispaid) {
		this.ispaid = ispaid;
	}
	
	public boolean getIsCarryForward() {
		return isCarryForward;
	}
	
	public void setIsCarryForward(boolean isCarryForward) {
		this.isCarryForward = isCarryForward;
	}
	
	public String getStrPolicyId() {
		return strPolicyId;
	}
	
	public void setStrPolicyId(String strPolicyId) {
		this.strPolicyId = strPolicyId;
	}
	
	public String getMonthlyLeaveLimit() {
		return monthlyLeaveLimit;
	}
	
	public void setMonthlyLeaveLimit(String monthlyLeaveLimit) {
		this.monthlyLeaveLimit = monthlyLeaveLimit;
	}
	
	public String getConsLeaveLimit() {
		return consLeaveLimit;
	}
	
	public void setConsLeaveLimit(String consLeaveLimit) {
		this.consLeaveLimit = consLeaveLimit;
	}
	
	public boolean getIsMonthlyCarryForward() {
		return isMonthlyCarryForward;
	}
	
	public void setIsMonthlyCarryForward(boolean isMonthlyCarryForward) {
		this.isMonthlyCarryForward = isMonthlyCarryForward;
	}
	
	public boolean getIsApproval() {
		return isApproval;
	}
	
	public void setIsApproval(boolean isApproval) {
		this.isApproval = isApproval;
	}
	
	public String getOrgId() {
		return orgId;
	}
	
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public String getStrLocation() {
		return strLocation;
	}
	
	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}
	
	public String getPolicy() {
		return policy;
	}
	
	public void setPolicy(String policy) {
		this.policy = policy;
	}
	
	public String getNoOfLeaveMonthly() {
		return noOfLeaveMonthly;
	}
	
	public void setNoOfLeaveMonthly(String noOfLeaveMonthly) {
		this.noOfLeaveMonthly = noOfLeaveMonthly;
	}
	
	public String getAccrualSystem() {
		return accrualSystem;
	}
	
	public void setAccrualSystem(String accrualSystem) {
		this.accrualSystem = accrualSystem;
	}
	
	public String getAccrualFrom() {
		return accrualFrom;
	}
	
	public void setAccrualFrom(String accrualFrom) {
		this.accrualFrom = accrualFrom;
	}
	
	public List<FillLeaveType> getLeaveList2() {
		return leaveList2;
	}
	
	public void setLeaveList2(List<FillLeaveType> leaveList2) {
		this.leaveList2 = leaveList2;
	}
	
	public String getIsCompensatory() {
		return isCompensatory;
	}
	
	public void setIsCompensatory(String isCompensatory) {
		this.isCompensatory = isCompensatory;
	}
	
	public String getCompensateWith() {
		return compensateWith;
	}
	
	public void setCompensateWith(String compensateWith) {
		this.compensateWith = compensateWith;
	}
    
	public String getIsSandwich() {
        return isSandwich;
    }
    
    public void setIsSandwich(String isSandwich) {
        this.isSandwich = isSandwich;
    }
    
    public List<String> getSandwichHoliday() {
        return sandwichHoliday;
    }
    
    public void setSandwichHoliday(List<String> sandwichHoliday) {
        this.sandwichHoliday = sandwichHoliday;
    }
	
	public String getIsLeaveEncashment() {
		return isLeaveEncashment;
	}
	
	public void setIsLeaveEncashment(String isLeaveEncashment) {
		this.isLeaveEncashment = isLeaveEncashment;
	}
	
	public String getMinLeavesRequiredEncashment() {
		return minLeavesRequiredEncashment;
	}
	
	public void setMinLeavesRequiredEncashment(String minLeavesRequiredEncashment) {
		this.minLeavesRequiredEncashment = minLeavesRequiredEncashment;
	}
	
	public String getNoOfTimes() {
		return noOfTimes;
	}
	
	public void setNoOfTimes(String noOfTimes) {
		this.noOfTimes = noOfTimes;
	}
	
	public String getDefaultSelectEncash() {
		return defaultSelectEncash;
	}
	
	public void setDefaultSelectEncash(String defaultSelectEncash) {
		this.defaultSelectEncash = defaultSelectEncash;
	}
	
	public String getStrEncashApplicable() {
		return strEncashApplicable;
	}
	
	public void setStrEncashApplicable(String strEncashApplicable) {
		this.strEncashApplicable = strEncashApplicable;
	}
	
	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}
	
	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}
	
	public String getMaxLeavesAppliedEncashment() {
		return maxLeavesAppliedEncashment;
	}
	
	public void setMaxLeavesAppliedEncashment(String maxLeavesAppliedEncashment) {
		this.maxLeavesAppliedEncashment = maxLeavesAppliedEncashment;
	}
	
	public boolean getIsLongLeave() {
		return isLongLeave;
	}
	
	public void setIsLongLeave(boolean isLongLeave) {
		this.isLongLeave = isLongLeave;
	}
	
	public String getLongLeaveLimit() {
		return longLeaveLimit;
	}
	
	public void setLongLeaveLimit(String longLeaveLimit) {
		this.longLeaveLimit = longLeaveLimit;
	}
	
	public String getStrLevelId() {
		return strLevelId;
	}
	
	public void setStrLevelId(String strLevelId) {
		this.strLevelId = strLevelId;
	}
	
	public List<String> getSalaryHeadId() {
		return salaryHeadId;
	}
	
	public void setSalaryHeadId(List<String> salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}
	
	public List<String> getLeaveAvailable() {
		return leaveAvailable;
	}
	
	public void setLeaveAvailable(List<String> leaveAvailable) {
		this.leaveAvailable = leaveAvailable;
	}
	
	public String getPercentage() {
		return percentage;
	}
	
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	
	public String getIsLeaveAccrual() {
		return isLeaveAccrual;
	}
	
	public void setIsLeaveAccrual(String isLeaveAccrual) {
		this.isLeaveAccrual = isLeaveAccrual;
	}
	
	public String getIsLeaveOptHoliday() {
		return isLeaveOptHoliday;
	}
	
	public void setIsLeaveOptHoliday(String isLeaveOptHoliday) {
		this.isLeaveOptHoliday = isLeaveOptHoliday;
	}
	
	public String getCalendarYear() {
		return calendarYear;
	}
	
	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}
	
	public String getOptionalLeaveLimit() {
		return optionalLeaveLimit;
	}
	
	public void setOptionalLeaveLimit(String optionalLeaveLimit) {
		this.optionalLeaveLimit = optionalLeaveLimit;
	}
	
	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}
	
	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}
	
	public boolean getIsProrata() {
		return isProrata;
	}
	
	public void setIsProrata(boolean isProrata) {
		this.isProrata = isProrata;
	}
	
	public String getDefaultAccrualType() {
		return defaultAccrualType;
	}
	
	public void setDefaultAccrualType(String defaultAccrualType) {
		this.defaultAccrualType = defaultAccrualType;
	}
	
	public String getAccrualType() {
		return accrualType;
	}
	
	public void setAccrualType(String accrualType) {
		this.accrualType = accrualType;
	}
	
	public String getNoOfAccrueDays() {
		return noOfAccrueDays;
	}
	
	public void setNoOfAccrueDays(String noOfAccrueDays) {
		this.noOfAccrueDays = noOfAccrueDays;
	}
	
	public boolean getIsCarriedForwardLimit() {
		return isCarriedForwardLimit;
	}
	
	public void setIsCarriedForwardLimit(boolean isCarriedForwardLimit) {
		this.isCarriedForwardLimit = isCarriedForwardLimit;
	}
	
	public String getCarriedForwardLimit() {
		return carriedForwardLimit;
	}
	
	public void setCarriedForwardLimit(String carriedForwardLimit) {
		this.carriedForwardLimit = carriedForwardLimit;
	}
	
	public String getDefaultBalance() {
		return defaultBalance;
	}
	
	public void setDefaultBalance(String defaultBalance) {
		this.defaultBalance = defaultBalance;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
	public boolean getIsMaternity() {
		return isMaternity;
	}
	
	public void setIsMaternity(boolean isMaternity) {
		this.isMaternity = isMaternity;
	}	

	public boolean getIsTimePeriod() {
		return isTimePeriod;
	}

	public void setIsTimePeriod(boolean isTimePeriod) {
		this.isTimePeriod = isTimePeriod;
	}

	public String[] getStrTimeFromDate() {
		return strTimeFromDate;
	}

	public void setStrTimeFromDate(String[] strTimeFromDate) {
		this.strTimeFromDate = strTimeFromDate;
	}

	public String[] getStrTimeToDate() {
		return strTimeToDate;
	}

	public void setStrTimeToDate(String[] strTimeToDate) {
		this.strTimeToDate = strTimeToDate;
	}

	public boolean getIsActualCalDays() {
		return isActualCalDays;
	}

	public void setIsActualCalDays(boolean isActualCalDays) {
		this.isActualCalDays = isActualCalDays;
	}
	
	public boolean getIsApplyLeaveLimit() {
		return isApplyLeaveLimit;
	}

	public void setIsApplyLeaveLimit(boolean isApplyLeaveLimit) {
		this.isApplyLeaveLimit = isApplyLeaveLimit;
	}
	
	public boolean getIsCarryForwardAccrualMonthly() {
		return isCarryForwardAccrualMonthly;
	}
	
	public void setIsCarryForwardAccrualMonthly(boolean isCarryForwardAccrualMonthly) {
		this.isCarryForwardAccrualMonthly = isCarryForwardAccrualMonthly;
	}

	public String getFutureApply() {
		return futureApply;
	}

	public void setFutureApply(String futureApply) {
		this.futureApply = futureApply;
	}

	public String getFutureApplyMax() {
		return futureApplyMax;
	}

	public void setFutureApplyMax(String futureApplyMax) {
		this.futureApplyMax = futureApplyMax;
	}

	public String getIsWorkFromHome() {
		return isWorkFromHome;
	}

	public void setIsWorkFromHome(String isWorkFromHome) {
		this.isWorkFromHome = isWorkFromHome;
	}

	public String getDistributedMonth() {
		return distributedMonth;
	}

	public void setDistributedMonth(String distributedMonth) {
		this.distributedMonth = distributedMonth;
	}

	public String getPriorApplyOneDayLeave() {
		return priorApplyOneDayLeave;
	}

	public void setPriorApplyOneDayLeave(String priorApplyOneDayLeave) {
		this.priorApplyOneDayLeave = priorApplyOneDayLeave;
	}

	public String getLapsDays() {
		return lapsDays;
	}

	public void setLapsDays(String lapsDays) {
		this.lapsDays = lapsDays;
	}

	public String getIsDocumentRequired() {
		return isDocumentRequired;
	}

	public void setIsDocumentRequired(String isDocumentRequired) {
		this.isDocumentRequired = isDocumentRequired;
	}

	public String getNoDaysForDocument() {
		return noDaysForDocument;
	}

	public void setNoDaysForDocument(String noDaysForDocument) {
		this.noDaysForDocument = noDaysForDocument;
	}

	public String getJoiningMonthDay() {
		return joiningMonthDay;
	}

	public void setJoiningMonthDay(String joiningMonthDay) {
		this.joiningMonthDay = joiningMonthDay;
	}

	public String getJoiningMonthLeaveBalance() {
		return joiningMonthLeaveBalance;
	}

	public void setJoiningMonthLeaveBalance(String joiningMonthLeaveBalance) {
		this.joiningMonthLeaveBalance = joiningMonthLeaveBalance;
	}

	public String getLongLeaveGap() {
		return longLeaveGap;
	}

	public void setLongLeaveGap(String longLeaveGap) {
		this.longLeaveGap = longLeaveGap;
	}

	public String getFutureApply1() {
		return futureApply1;
	}

	public void setFutureApply1(String futureApply1) {
		this.futureApply1 = futureApply1;
	}

	public String getNoOfLeave1() {
		return noOfLeave1;
	}

	public void setNoOfLeave1(String noOfLeave1) {
		this.noOfLeave1 = noOfLeave1;
	}

	public String getNoOfLeave2() {
		return noOfLeave2;
	}

	public void setNoOfLeave2(String noOfLeave2) {
		this.noOfLeave2 = noOfLeave2;
	}

	public String getNoOfLeave3() {
		return noOfLeave3;
	}

	public void setNoOfLeave3(String noOfLeave3) {
		this.noOfLeave3 = noOfLeave3;
	}

	public String getMinimumLongLeaveLimit() {
		return minimumLongLeaveLimit;
	}

	public void setMinimumLongLeaveLimit(String minimumLongLeaveLimit) {
		this.minimumLongLeaveLimit = minimumLongLeaveLimit;
	}

	public String getMonthlyApplyLimit() {
		return monthlyApplyLimit;
	}

	public void setMonthlyApplyLimit(String monthlyApplyLimit) {
		this.monthlyApplyLimit = monthlyApplyLimit;
	}
	
}