package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.CommonFunctionsNew;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApprovePayrollNew extends ActionSupport implements ServletRequestAware, ServletResponseAware,SessionAware, IStatements {
	

	private static final long serialVersionUID = 1L;
	public Map session;
	String strEmpID;   
	String strUserType;  
	String strSessionEmpId;       
   
	String approvePC = null;
	String strAlpha = null;
	
	String org;
	String paycycle;
	List<String> wLocation;
	List<String> level;
	List<String> department;
	List<String> service; 
	List<String> chbox; 

	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillPayCycleDuration> paycycleDurationList;
	List<FillPayMode> paymentModeList;
	
	
	List<FillOrganisation> organisationList;
	
	String redirectUrl;
	String approve;
	String dtMin;
	String dtMax;
	
	String[] empID;
	String[] paymentMode;
	public CommonFunctions CF = null;

	private static Logger log = Logger.getLogger(ApprovePayroll.class);
	
	
public String execute() throws Exception {
		
		
		System.out.println("Request Recieved");
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;

		CommonFunctionsNew CF1=new CommonFunctionsNew();
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);
		
		
		String APPROVE = (String) request.getParameter("approve"); 
//		strAlpha = (String) request.getParameter("alphaValue");
//		setStrAlpha(strAlpha);

		request.setAttribute(TITLE, "Approve Compensation");
//		strEmpID = (String) request.getParameter("EMPID");

		String[] strPayCycleDates = null;
	
		if(getOrg()==null){
			setOrg((String)session.get(ORGID));
		}
		System.out.println("org===="+org);
		Connection con=null;
		Database db=new Database();
		con=db.makeConnection(con);
		if(org!=null && paycycle==null){
			strPayCycleDates=CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(),CF,org);
		}else if( paycycle!=null){
			strPayCycleDates = getPaycycle().split("-");
		}
		
		
		

		String paycycleStartDate = uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DBDATE);
		String paycycleEndDate =  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DBDATE);
		String strPC = strPayCycleDates[2];
		
		String strFinancialYearEnd = null;
		String strFinancialYearStart = null;
		String []strFinancialYear = CF1.getFinancialYear(con, paycycleEndDate, CF, uF);
		if(strFinancialYear!=null){
			strFinancialYearStart = strFinancialYear[0];
			strFinancialYearEnd = strFinancialYear[1];
		}
		
		
		
		
//		String referer = request.getHeader("Referer");
//
//		if (referer != null) {
//			int index1 = referer.indexOf(request.getContextPath());
//			int index2 = request.getContextPath().length();
//			referer = referer.substring(index1 + index2 + 1);
//		}
//		setRedirectUrl(referer);

		
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		
		
			request.setAttribute(PAGE, "/jsp/payroll/ApprovePayrollNew.jsp");
//			strEmpID = (String) session.get(EMPID);
			
//			if(getStrPaycycleDuration()==null || getStrPaycycleDuration().equals("")){
//				setStrPaycycleDuration("M");
//			}
			loadClockEntries(CF,uF);
			getSelectedFilter(uF);
			if (APPROVE != null) {

				request.setAttribute(PAGE, "/jsp/payroll/ApprovePayrollNew.jsp");
				request.setAttribute(MESSAGE, "Payroll generated");
				approvePayrollEntries( con,  CF, CF1, uF, paycycleStartDate , paycycleEndDate , strPC, strFinancialYearStart,  strFinancialYearEnd);
				
//				com.konnect.jpms.export.PaySlips p = new com.konnect.jpms.export.PaySlips();
//				p.setServletRequest(request);
//				p.execute(getDtMin(), getDtMax());

				return SUCCESS;
			}
			viewClockEntriesForPayrollApproval(con,CF,CF1,  paycycleStartDate, paycycleEndDate,strPC,strFinancialYearStart,strFinancialYearEnd,null);

//			viewClockEntriesForPayrollApproval(CF, null, paycycleStartDate, paycycleEndDate);
			
		
		System.out.println("Response dispatched");
		return LOAD;

	}
	public void approvePayrollEntries(Connection con, CommonFunctions CF,CommonFunctionsNew CF1,UtilityFunctions uF,String paycycleStartDate ,String paycycleEndDate ,String strPC,String strFinancialYearStart, String strFinancialYearEnd){
		String strChbox=getString(chbox, ",");
		System.out.println("strChbox===="+strChbox);
		viewClockEntriesForPayrollApproval(con,CF,CF1,  paycycleStartDate, paycycleEndDate,strPC,strFinancialYearStart,strFinancialYearEnd,null);

	}



	public Map<String,Map<String,String>> getApprovedPayRollData(Connection con, CommonFunctions CF,UtilityFunctions uF,String strPC,String strFinancialYearStart, String strFinancialYearEnd){
	


	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,Map<String,String>> hmEmpPaidSalary=new HashMap<String,Map<String,String>>();
	try {
		pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by salary_head_id, emp_id order by emp_id");
pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
pst.setInt(3, uF.parseToInt(strPC));		
	
	rs = pst.executeQuery(); 

	while (rs.next()) {
		
		Map<String, String> hmInner =hmEmpPaidSalary.get(rs.getString("emp_id"));
		if(hmInner==null)hmInner=new HashMap<String, String>();

		

		hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));

		hmEmpPaidSalary.put(rs.getString("emp_id"), hmInner);


	}
	
	
	} catch (Exception e) {
		e.printStackTrace();
	}
	return hmEmpPaidSalary;

	}

	public String loadClockEntries(CommonFunctions CF, UtilityFunctions uF) {
//		paycycleList = new FillPayCycles(getStrPaycycleDuration()).fillPayCycles(CF); 
		paycycleList = new FillPayCycles().fillPayCycles(CF, getOrg());
//		wLocationList = new FillWLocation().fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getOrg()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getOrg()));
		serviceList = new FillServices(request).fillServices(getOrg(),uF);
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		paymentModeList = new FillPayMode().fillPaymentMode();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getOrg(), (String)session.get(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.get(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getOrg());
		}
		
		return LOAD;
	}
	
//	Map hmEmpRosterLunchDeduction = new HashMap();
//	Map<String, Map<String, String>> hmLeavesMap = null;
//	Map<String, String> hmLeaves = null;
	

	public String viewClockEntriesForPayrollApproval(Connection con, CommonFunctions CF,CommonFunctionsNew CF1, String strPaycycleStart, String strPaycycleEnd,String strPC,String strFinancialYearStart,String strFinancialYearEnd,String strEmpIds) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			Map<String, String> hmLoanAmt=new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
//			List<String> alLoans = new ArrayList<String>();
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strPaycycleEnd, DBDATE, "MM"));
			
			
			
			
			Map<String,Map<String,String>> hmEmpPaidSalary=getApprovedPayRollData(con, CF, uF,  strPC, strFinancialYearStart, strFinancialYearEnd);
			
			double dblInvestmentExemption = 0.0d;
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				dblInvestmentExemption = 100000;
			}
			
			Map<String,String> hmEmpMertoMap = new HashMap<String,String>();
			Map<String, Set<String>> empHalfLeaveMp=new HashMap<String, Set<String>>();
			Map<String,Set<String>> hmHalfWeekEnds=new HashMap<String,Set<String>>();
			int totalDaysinPaycycle=uF.parseToInt(uF.dateDifference(strPaycycleStart, DBDATE, strPaycycleEnd, DBDATE));
			System.out.println("totalDaysinPaycycle===>"+totalDaysinPaycycle);
			
			Map<String, String> hmEmpLevelMap=CF1.getEmpLevelMap(con,strEmpIds);
			Map<String, String> hmAttendanceBonusLevelMap =CF.getAttendanceBonusMap(con,nPayMonth+"");
			Map<String, Map<String,String>> hmEmpLateComingLevelPolicy=CF.getEmpLateComingLevelPolicy(con);
			Map<String,String> hmEmpGenderMap =new HashMap<String,String>();
			Map<String,String> hmEmpAgeMap =new HashMap<String,String>();
			Map<String, String> hmAttendanceDependent =new HashMap<String,String>();
			Map<String,String> hmEmpExemptionsMap = CF1.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption,strEmpIds);
			Map<String,String> hmEmpHomeLoanMap = CF1.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd,strEmpIds);
			Map<String,String> hmFixedExemptions =CF1.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String,String> hmEmpRentPaidMap = CF1.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd,strEmpIds);
			Map<String,String> hmEmpIncomeOtherSourcesMap = CF1.getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd,strEmpIds);
			Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
			Map<String, String> hmPrevEmpTdsAmount=CF1.getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpGrossAmount,strEmpIds);
			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  CF1.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String,Set<String>> holidaysMp=CF1.getHolidayList(con, request,uF, strPaycycleStart, strPaycycleEnd);
			Map<String,Set<String>> hmWeekEnds = CF1.getWeekEndDateList(con, strPaycycleStart, strPaycycleEnd, CF, uF,hmHalfWeekEnds,null);
			Map<String,Set<String>> empLeaveMp =CF1.getLeaveDates(con, strPaycycleStart, strPaycycleEnd, CF,empHalfLeaveMp,strEmpIds);
			Map<String,List<List<Double>>> attendanceRosterPolicyWlocation=CF1.getAttendanceRosterPolicy(con);
			List<String> salaryHeadList=new ArrayList<String>();
			Map<String,String> salaryHeadsEarningDeductionDetails=new LinkedHashMap<String,String>();
			Map<String,String> salaryHeadsDetails=CF1.getSalaryHeadDetails(con,salaryHeadList,salaryHeadsEarningDeductionDetails);
			Map<String,Map<String,Map<String,String>>> levelhmSalaryDetails=CF1.getLevelSalaryDetails(con);
			Map<String,Map<String,Map<String,String>>> emphmSalaryDetails=CF1.getEmpSalaryDetails(con,uF,strPaycycleStart,strEmpIds);
			Map<String,String> empOvertimeMp=CF1.getOverTimehours(con,uF, strPaycycleStart, strPaycycleEnd,strEmpIds);
			Map<String, Map<String,String>> hmEmpOverTimeLevelPolicy=CF1.getEmpOverTimeLevelPolicy(con,CF,uF,strPaycycleStart,strPaycycleEnd,strPC);
			Map<String,Map<String,String>> bonusDetails=CF1.getBonusDetails(con,uF,strFinancialYearStart,strFinancialYearEnd);	
			Map<String, String> hmVariables  =CF1.getVariableAmount(con, uF, strPC,strEmpIds);
			Map<String, List<Map<String, String>>> hmLOANVariables  = CF1.getLoanDetails(con,uF,strPaycycleStart,strEmpIds);
			List<String> loanList=new ArrayList<String>();
			Map<String, String> hmLoanPoliciesMap = CF1.getLoanPoliciesMap(con, uF, getOrg(),loanList);
			Map<String, Map<String, String>> hmArearAmountMap = CF1.getArearDetails(con, uF, CF, strPaycycleEnd,strEmpIds);
			Map<String,String> wlocationMp=CF1.getEmpWlocationMap(con,hmEmpMertoMap);
			Map<String, String> hmEmpServiceTaxMap = CF1.getEmpServiceTax(con, uF, CF,strEmpIds);
			Map<String,String> EEPFMap=CF1.getEEPF(con,uF,strFinancialYearStart,strFinancialYearEnd);
			Map<String,String> ERPFMap=CF1.getERPF(con,uF,strFinancialYearStart,strFinancialYearEnd);
			Map<String,Map<String,String>> ERESIMap=CF1.getERESI(con,uF,strFinancialYearStart,strFinancialYearEnd);
			Map<String,Map<String,String>> EESIMap=CF1.getEEESI(con,uF,strFinancialYearStart,strFinancialYearEnd);
			Map<String,Map<String,String>> ERLWFMap=CF1.getERLWF(con,uF,strFinancialYearStart,strFinancialYearEnd, getOrg());
			Map<String,Map<String,String>> EELWFMap=CF1.getEELWF(con,uF,strFinancialYearStart,strFinancialYearEnd, getOrg());
//			Map<String, String> hmLICAmountMap =CF1.getLICDetails(con,strEmpIds);
//			Map<String, String> hmoutDoorAmountMap =CF1.getOUTDOORLoanDetails(con,strEmpIds);

			
			pst = con.prepareStatement("select wlocation_id,emp_id,empcode,emp_fname,emp_mname, emp_lname,emp_gender,emp_date_of_birth from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id");
			rs=pst.executeQuery();
			Map<String,String> empLocationmp=new HashMap<String,String>();
			Map<String,String> empNamemp=new HashMap<String,String>();
			Map<String,String> empCodemp=new HashMap<String,String>();
			
			while(rs.next()){
				hmEmpGenderMap.put(rs.getString("emp_id"), ((rs.getString("emp_gender")!=null)?rs.getString("emp_gender"):"Not Specified"));
				empLocationmp.put(rs.getString("emp_id"),rs.getString("wlocation_id"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				empNamemp.put(rs.getString("emp_id"),rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				empCodemp.put(rs.getString("emp_id"),rs.getString("empcode"));
				String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE);
				double dblYears = uF.parseToDouble(strDays) / 365;
				hmEmpAgeMap.put(rs.getString("emp_id"), dblYears + "");
			}
			
			
			
			pst = con.prepareStatement("select *,cast(in_out_timestamp as date) as attendance_date from attendance_details where  cast(in_out_timestamp as date) between ? and ?");
			pst.setDate(1, uF.getDateFormat(strPaycycleStart, DBDATE));
			pst.setDate(2, uF.getDateFormat(strPaycycleEnd, DBDATE));
			rs=pst.executeQuery();
			Map<String,Map<String,String>> attendanceEmpMp=new HashMap<String,Map<String,String>>();
			Map<String,String> empHoursLateMap=new HashMap<String,String>();
//			Set<String> tempSet=new HashSet<String>();

			while(rs.next()){ 
				String wlocation=empLocationmp.get(rs.getString("emp_id"));
				List<List<Double>> attendanceRosterPolicyList=attendanceRosterPolicyWlocation.get(wlocation);
				if("OUT".equals(rs.getString("in_out"))){
					
					Map<String,String> empAttendanceMap=attendanceEmpMp.get(rs.getString("emp_id"));
					if(empAttendanceMap==null)empAttendanceMap=new HashMap<String,String>();
					
					double attendance=1;
					
						if(attendanceRosterPolicyList!=null){
							double deductionAmount=0;
							for(List<Double> innerList:attendanceRosterPolicyList){

								if(innerList.get(0)<=rs.getDouble("hours_worked_actual") && innerList.get(1)>=rs.getDouble("hours_worked_actual")){
									deductionAmount=innerList.get(2);
												break;
								}
							}
//							
							attendance-=deductionAmount;
							}
						empAttendanceMap.put(rs.getString("attendance_date"),attendance+"");
					
					attendanceEmpMp.put(rs.getString("emp_id"),empAttendanceMap);
				}
			}

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and (employment_end_date is null OR employment_end_date >= ?) and joining_date<= ? and emp_per_id > 0 ");
			if(getLevel()!=null && getLevel().size()>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+getString(getLevel(), ",")+") ) ");
            }
            if(getDepartment()!=null && getDepartment().size()>0){
                sbQuery.append(" and depart_id in ("+getString(getDepartment(), ",")+") ");
            }
            
            if(getService()!=null && getService().size()>0){
                sbQuery.append(" and (");
                for(int i=0; i<getService().size(); i++){
                    sbQuery.append(" eod.service_id like '%,"+getService().get(i)+",%'");
                    
                    if(i<getService().size()-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
			if(getwLocation()!=null && getwLocation().size()>0){
                sbQuery.append(" and wlocation_id in ("+getString(getwLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.get(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.get(WLOCATION_ACCESS)+")");
			}
            
			if(uF.parseToInt(getOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.get(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.get(ORG_ACCESS)+")");
			}
			
			sbQuery.append(" order by emp_fname, emp_lname");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strPaycycleStart, DBDATE));
			pst.setDate(2,  uF.getDateFormat(strPaycycleEnd, DBDATE));
			
			rs = pst.executeQuery();
			
			List<String> employeeList=new ArrayList<String>();
			while(rs.next()){
				employeeList.add(rs.getString("emp_per_id"));
				hmAttendanceDependent.put(rs.getString("emp_per_id"), rs.getString("is_attendance"));
			}
			
			
			Map<String,String> totalDaysEmpMap=new HashMap<String,String>();
			Map<String,String> presentDaysEmpMap=new HashMap<String,String>();
			Map<String,String> leaveEmpMap=new HashMap<String,String>();
			Map<String,Map<String,String>> SalaryCalculatedMp=new HashMap<String,Map<String,String>>();
//			List<String> temp=new ArrayList<String>();
			
			
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			for(String strEmpId:employeeList){
				String strOrgId = hmEmpOrgId.get(strEmpId);	
				String level=hmEmpLevelMap.get(strEmpId);
				String wlocation=empLocationmp.get(strEmpId);
				String stateId=wlocationMp.get(wlocation);
				
				double calculatedPresentDays=0;
				
				Set<String> holidayList1=holidaysMp.get(wlocation);
				if(holidayList1==null)holidayList1=new HashSet<String>();
//				int holidaycnt=holidayList1.size();
				Set<String> holidayList=new HashSet<String>(holidayList1); 
					
				Set<String> weekendList1=hmWeekEnds.get(wlocation);
				if(weekendList1==null)weekendList1=new HashSet<String>();
				int weekendcnt=weekendList1.size();
				Set<String> weekendList=new HashSet<String>(weekendList1); 
				
				Set<String> halfLeaveList=empHalfLeaveMp.get(strEmpId);
				if(halfLeaveList==null)halfLeaveList=new HashSet<String>();
				double halfLeavecnt=(double)halfLeaveList.size();

				Set<String> empLeaveList=empLeaveMp.get(strEmpId);
				if(empLeaveList==null)empLeaveList=new HashSet<String>();
				
				Map<String,String> empAttendanceList=attendanceEmpMp.get(strEmpId);
				if(empAttendanceList==null)empAttendanceList=new HashMap<String,String>();
				
				Set<String> halfWeeklyOfflist1=hmHalfWeekEnds.get(wlocation);
				if(halfWeeklyOfflist1==null)halfWeeklyOfflist1=new HashSet<String>();
				Set<String> halfWeeklyOfflist=new HashSet<String>(halfWeeklyOfflist1); 
				
				
				
				Map<String,String> mp=attendanceEmpMp.get(strEmpId);
				if(mp==null)mp=new HashMap<String,String>();
				
				Set<String> set=mp.keySet();
				Iterator<String> it=set.iterator();
				
				
				
				
//				double totalHalf=uF.parseToDouble(attendanceHalfEmp.get(strEmpId));
				
				leaveEmpMap.put(strEmpId, empLeaveList.size()+halfLeavecnt/2+"");
				
				
//				double actualattendance=uF.parseToDouble(actualAttendanceEmp.get(strEmpId));
				String strSalaryCalBasis=hmEmpLevelMap.get(strEmpId+"_SALARY_CAL_BASIS");
				
				
				
				if("AWD".equalsIgnoreCase(strSalaryCalBasis)){
					holidayList.removeAll(empLeaveList);
					double actualattendance=0.0;
					while(it.hasNext()){
						String date=it.next();
						
						
						
						
						if(empLeaveList.contains(date)){
//							holidayList.remove(date);
						}else if(holidayList.contains(date)){
							actualattendance++;
							holidayList.remove(date);
						}else if(halfLeaveList.contains(date)){
							if(uF.parseToDouble(mp.get(date))==1){
								actualattendance+=.5;
							}else{
								actualattendance+=uF.parseToDouble(mp.get(date));
							}
//							actualattendance++;
							
						}else{
							
							actualattendance+=uF.parseToDouble(mp.get(date));
						}
						if(strEmpId.equals("211")){
							System.out.println("date=="+date);
							System.out.println("actualattendance=="+actualattendance);

						}
						
					}
					actualattendance+=holidayList.size();
					if(strEmpId.equals("211")){
						System.out.println("strEmpId=="+strEmpId);
						System.out.println("empLeaveList=="+empLeaveList);
						System.out.println("halfLeaveList=="+halfLeaveList);

						System.out.println("holidayList=="+holidayList);
						System.out.println("mp=="+mp);

						System.out.println("att=="+actualattendance);
						System.out.println("leaveEmpMap.get(strEmpId)=="+leaveEmpMap.get(strEmpId));

					}
					
					int days=totalDaysinPaycycle-weekendcnt;
					
					totalDaysEmpMap.put(strEmpId, days+"");
					
					
					
					presentDaysEmpMap.put(strEmpId, actualattendance+"");
					calculatedPresentDays=actualattendance+uF.parseToDouble(leaveEmpMap.get(strEmpId));
					boolean isAttendance = uF.parseToBoolean((String)hmAttendanceDependent.get(strEmpId));
					if(!isAttendance){
						calculatedPresentDays = days;
					}
				}else if("AFD".equalsIgnoreCase(strSalaryCalBasis)){
					
				}else{
					double actualattendance=0.0;
					holidayList.removeAll(empLeaveList);
					weekendList.removeAll(empLeaveList);
					weekendList.removeAll(holidayList);

					halfWeeklyOfflist.removeAll(holidayList);
					halfWeeklyOfflist.removeAll(empLeaveList);
//					halfWeeklyOfflist.removeAll(holidayList);

					
					while(it.hasNext()){
						
						String date=it.next();
						

						
						
						if(empLeaveList.contains(date)){
						}else if(holidayList.contains(date)){
							actualattendance++;
							holidayList.remove(date);
							weekendList.remove(date);
						}else if(weekendList.contains(date)){
							actualattendance++;
							weekendList.remove(date);
						}else if(halfLeaveList.contains(date)){
							if(uF.parseToDouble(mp.get(date))==1){
								actualattendance+=.5;
							}else{
								if(halfWeeklyOfflist.contains(date) && uF.parseToDouble(mp.get(date))==0){
									actualattendance+=.5;
								}else{
									actualattendance+=uF.parseToDouble(mp.get(date));
								}
							}
						}else if(halfWeeklyOfflist.contains(date)){
							if(uF.parseToDouble(mp.get(date))==1){
								actualattendance+=uF.parseToDouble(mp.get(date));
								
							}else{
								actualattendance+=uF.parseToDouble(mp.get(date))+.5;
							}
							
							
//							actualattendance++;
							
						}else{
							actualattendance+=uF.parseToDouble(mp.get(date));
						}
						halfWeeklyOfflist.remove(date);
					}
					actualattendance+=weekendList.size()+holidayList.size()+(((double)halfWeeklyOfflist.size())/2);
					
//					if(strEmpId.equals("279")){
//						System.out.println("strEmpId=="+strEmpId);
//						System.out.println("empLeaveList=="+empLeaveList);
//						System.out.println("halfLeaveList=="+halfLeaveList);
//
//						System.out.println("weekendList=="+weekendList);
//						System.out.println("holidayList=="+holidayList);
//						System.out.println("mp=="+mp);
//
//						System.out.println("att=="+actualattendance);
//						System.out.println("leaveEmpMap.get(strEmpId)=="+leaveEmpMap.get(strEmpId));
//
//					}
					
					
					
					
					totalDaysEmpMap.put(strEmpId, totalDaysinPaycycle+"");
					calculatedPresentDays=actualattendance+uF.parseToDouble(leaveEmpMap.get(strEmpId));
					boolean isAttendance = uF.parseToBoolean((String)hmAttendanceDependent.get(strEmpId));
					if(!isAttendance){
						calculatedPresentDays = totalDaysinPaycycle;
					}
					presentDaysEmpMap.put(strEmpId, actualattendance+"");

				}
				///Days calculation ends 	
				
				Map<String,String> empSalaryCalculatedMp=null;
				if(hmEmpPaidSalary.get(strEmpId)!=null){
					
					empSalaryCalculatedMp=hmEmpPaidSalary.get(strEmpId);
					
				}else{
				
				
				Map<String,Map<String,String>> hmSalaryDetails =levelhmSalaryDetails.get(level);
				Map<String,Map<String,String>> hmEmpSalaryDetails =emphmSalaryDetails.get(strEmpId);
				
				if(hmSalaryDetails!=null && hmEmpSalaryDetails!=null){
					double overTimehours=uF.parseToDouble( empOvertimeMp.get(strEmpId));
					Map<String,String> hmOvertimePolicy=hmEmpOverTimeLevelPolicy.get(level);
					Map<String,String> levelBonusDetails = bonusDetails.get(level);
					 Map<String,String> lateComingPolicy=hmEmpLateComingLevelPolicy.get(level);
					 String empServiceTax=hmEmpServiceTaxMap.get(strEmpId);
					 List<Map<String, String>> loanEmplist=hmLOANVariables.get(strEmpId);
					 String hourslate= empHoursLateMap.get(strEmpId);
					 Map<String,String> hmArearMap=hmArearAmountMap.get(strEmpId);
					 Map<String,String> ERESI=ERESIMap.get(getOrg()+"_"+stateId);
					 Map<String,String> EESI=EESIMap.get(stateId);
					 Map<String,String> ERLWF=ERLWFMap.get(stateId);
					 Map<String,String> EELWF= EELWFMap.get(stateId);
					 Map<String,String> isDisplayempSalaryCalculatedMp=new HashMap<String,String>();
					 empSalaryCalculatedMp=CF1.getSalaryCalculation(CF,uF,strEmpId,level,uF.parseToDouble(totalDaysEmpMap.get(strEmpId)),calculatedPresentDays,hmSalaryDetails,hmEmpSalaryDetails,hmAttendanceBonusLevelMap,levelBonusDetails,hmVariables,lateComingPolicy,hourslate,hmArearMap,stateId,empServiceTax,hmOtherTaxDetails,EEPFMap,ERPFMap,ERESI,EESI,isDisplayempSalaryCalculatedMp,overTimehours);
					 if(strEmpId.equals("234")){
							System.out.println("empSalaryCalculatedMp=======>"+empSalaryCalculatedMp);
							System.out.println("isDisplayempSalaryCalculatedMp=======>"+isDisplayempSalaryCalculatedMp);

					 }
					if(empSalaryCalculatedMp.containsKey(OVER_TIME+"")){
						
						if(overTimehours>0){
							double dblGrossPT=uF.parseToDouble(empSalaryCalculatedMp.get("GROSS"));
							double dblOverTime = CF1.getOverTimeCalculationHours(uF,hmOvertimePolicy,empSalaryCalculatedMp,calculatedPresentDays,overTimehours);
							double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
							empSalaryCalculatedMp.put("NET",netAmount+dblOverTime+"");
							empSalaryCalculatedMp.put("GROSS",dblGrossPT+dblOverTime+"");
							empSalaryCalculatedMp.put(OVER_TIME+"",dblOverTime+"");
							
						}
					}
					
					
					
					if(empSalaryCalculatedMp.containsKey(LOAN+"") && loanEmplist!=null){
						double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
						double dblLoanAmt = CF1.calculateLOAN(loanEmplist, uF, netAmount, strEmpId, CF,  hmEmpLoan);
						empSalaryCalculatedMp.put("NET",netAmount-dblLoanAmt+"");
						
					}
//					if(empSalaryCalculatedMp.containsKey(LIC+"")){
//						
//						double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
//						double dblLICAmt = uF.parseToDouble(hmLICAmountMap.get(strEmpId));
//						empSalaryCalculatedMp.put(LIC+"",dblLICAmt+"");
//						empSalaryCalculatedMp.put("NET",netAmount-dblLICAmt+"");
//						 
//					}
//					if(empSalaryCalculatedMp.containsKey(OUTDOOR_LOAN+"")){
//						double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
//						double dblLICAmt = uF.parseToDouble(hmoutDoorAmountMap.get(strEmpId));
//						empSalaryCalculatedMp.put(OUTDOOR_LOAN+"",dblLICAmt+"");
//						empSalaryCalculatedMp.put("NET",netAmount-dblLICAmt+"");
//						
//					}
					if(empSalaryCalculatedMp.containsKey(PROFESSIONAL_TAX+"")){
						double dblGrossPT=uF.parseToDouble(empSalaryCalculatedMp.get("GROSS"));
						double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
						double dblPt = calculateProfessionalTax(con, uF, dblGrossPT, strFinancialYearEnd, nPayMonth, stateId);
						empSalaryCalculatedMp.put(PROFESSIONAL_TAX+"",dblPt+"");
						empSalaryCalculatedMp.put("NET",netAmount-dblPt+"");
						
					}
					
					if(empSalaryCalculatedMp.containsKey(EMPLOYEE_EPF+"")){
						
						if(EEPFMap!=null){
							double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
							
							double dblEEPF = CF1.calculateEEPF(uF,EEPFMap,empSalaryCalculatedMp);
							empSalaryCalculatedMp.put(EMPLOYEE_EPF+"",dblEEPF+"");
							empSalaryCalculatedMp.put("NET",netAmount-dblEEPF+"");
						}
						
					}
					
					if(empSalaryCalculatedMp.containsKey(EMPLOYER_EPF+"")){
						
						if(ERPFMap!=null){
							double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
							
							double dblERPF = CF1.calculateERPF( uF,CF,ERPFMap,empSalaryCalculatedMp);
							empSalaryCalculatedMp.put(EMPLOYER_EPF+"",dblERPF+"");
							empSalaryCalculatedMp.put("NET",netAmount-dblERPF+"");
						}
						
					}
					if(empSalaryCalculatedMp.containsKey(EMPLOYER_ESI+"")){
						
						if(ERPFMap!=null){
							double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
							
							double dblESI = CF1.calculateERESI(uF,ERESI,empSalaryCalculatedMp);
							empSalaryCalculatedMp.put(EMPLOYER_ESI+"",dblESI+"");
							empSalaryCalculatedMp.put("NET",netAmount-dblESI+"");
						}
						
					}
					if(empSalaryCalculatedMp.containsKey(EMPLOYER_LWF+"")){
						if(EELWF!=null){
//						double dblGrossPT=uF.parseToDouble(empSalaryCalculatedMp.get("GROSS"));
						double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
						
						double dblPt = CF1.calculateERLWF(con, uF, ERLWF, strFinancialYearStart, strFinancialYearEnd, empSalaryCalculatedMp, stateId, nPayMonth, strOrgId);
						empSalaryCalculatedMp.put(EMPLOYER_LWF+"",dblPt+"");
						empSalaryCalculatedMp.put("NET",netAmount-dblPt+"");
//						empSalaryCalculatedMp.put("GROSS",dblGrossPT-dblPt+"");
						}
						 
					}
					if(empSalaryCalculatedMp.containsKey(EMPLOYEE_LWF+"")){
						
						if(EELWF!=null){
//							double dblGrossPT=uF.parseToDouble(empSalaryCalculatedMp.get("GROSS"));
							double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
						double dblPt = CF1.calculateEELWF(con, uF, EELWF, strFinancialYearStart, strFinancialYearEnd, empSalaryCalculatedMp, stateId, nPayMonth, strOrgId);
						empSalaryCalculatedMp.put(EMPLOYEE_LWF+"",dblPt+"");
						empSalaryCalculatedMp.put("NET",netAmount-dblPt+"");
//						empSalaryCalculatedMp.put("GROSS",dblGrossPT-dblPt+"");
						}
						 
					}
					if(empSalaryCalculatedMp.containsKey(TDS+"")){
						
						double dblBasic = uF.parseToDouble(empSalaryCalculatedMp.get(BASIC+""));
						double dblDA = uF.parseToDouble(empSalaryCalculatedMp.get(DA+""));
						double dblHRA = uF.parseToDouble(empSalaryCalculatedMp.get(HRA+""));
						
						Map<String,String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
						double dblGrossPT=uF.parseToDouble(empSalaryCalculatedMp.get("GROSS"));
						
//						Map hmPaidSalaryDetails =  (Map)hmEmpPaidAmountDetails.get(empId);
//						if(hmPaidSalaryDetails==null){hmPaidSalaryDetails=new HashMap();}
						
						double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(stateId+"_EDU_TAX"));
						double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(stateId+"_STD_TAX"));
						double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(stateId+"_FLAT_TDS"));
						
						if(hmEmpServiceTaxMap.containsKey(strEmpId)){
//							dblGrossTDS = dblGross;
							double  dblServiceTaxAmount = uF.parseToDouble(empSalaryCalculatedMp.get(SERVICE_TAX+""));
							dblGrossPT = dblGrossPT - dblServiceTaxAmount;
						}
						
						double dblTDS = CF1.calculateTDS(con, uF, dblGrossPT, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, (dblBasic + dblDA),
								nPayMonth,
								strPaycycleStart, strFinancialYearStart, strFinancialYearEnd, strEmpId,hmEmpGenderMap.get(strEmpId),hmEmpAgeMap.get(strEmpId),stateId,
								hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap.get(wlocation), hmEmpRentPaidMap.get(strEmpId), hmPaidSalaryDetails,
								empSalaryCalculatedMp,salaryHeadsDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,strPaycycleEnd);
						
//						dblDeduction += dblTDS;
						
//						dblAmount=dblTDS;
						double netAmount=uF.parseToDouble(empSalaryCalculatedMp.get("NET"));
						empSalaryCalculatedMp.put("NET",netAmount-dblTDS+"");
						empSalaryCalculatedMp.put(TDS+"", dblTDS+"");
						
					}
					
					
				}
				}
				
				if(empSalaryCalculatedMp==null)empSalaryCalculatedMp=new HashMap<String,String>();
				SalaryCalculatedMp.put(strEmpId, empSalaryCalculatedMp);
			}
			
			
			request.setAttribute("employeeList", employeeList);
			request.setAttribute("totalDaysEmpMap", totalDaysEmpMap);
			request.setAttribute("empNamemp", empNamemp);
			request.setAttribute("empCodemp", empCodemp);
			request.setAttribute("leaveEmpMap", leaveEmpMap);
			request.setAttribute("presentDaysEmpMap", presentDaysEmpMap);
			request.setAttribute("SalaryCalculatedMp", SalaryCalculatedMp);
			request.setAttribute("salaryHeadsDetails", salaryHeadsDetails);

			request.setAttribute("salaryHeadsEarningDeductionDetails", salaryHeadsEarningDeductionDetails);
			request.setAttribute("hmEmpLoan", hmEmpLoan);
			request.setAttribute("salaryHeadList", salaryHeadList);
//			loanList
			request.setAttribute("alLoans", loanList);

			request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);

			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
		}
		return SUCCESS;

	}


	


	
	
//	private double getOverTimeCalculationHours(Connection con, UtilityFunctions uF, String strEmpId, 
//			List alPresentDays, List alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, 
//			String strLevelId, double dblStandardHours, Map<String, String> hmHolidays, 
//			int nTotalNumberOfDaysForCalc1, Map<String, String> hmIndividualOvertime,Map<String, Map<String, String>> hmEmpOverTimeHours,Map<String, Map<String, String>> hmEmpOverTimeLevelPolicy, Map<String, String> hmEmpRosterHours, Map<String, String> hmWlocationMap, int nTotalNumberOfDays, int nWeekEnds,int nHolidays,double dblPresent,double dblTotalPresentDays,Map<String, String> hmHolidayDates,Map hmWeekEnds,Map hmEmpWlocationMap) {
//		
//		double dblTotalOverTimeAmount = 0.0d;
//		
//		try {
//			
//			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
////			String strLevelId = hmEmpLevelMap.get(strEmpId);
//			String strOverTimeType = null;
//			double dblTotalOverTime = 0;
//			double dblTotalHoursWorked = 0;
//			double dblOverTimeCalcHours = 0;
//			double dblOverTimeCalcDays = 0;
//			
//			double dblOvertimeFixedAmount = 0;
//			
//			
////			Map<String,String> hmOvertimePolicy=new HashMap<String, String>();
//			
//			Map<String,String> hmEmpOvertime=hmEmpOverTimeHours.get(strEmpId);
//			if(hmEmpOvertime==null) hmEmpOvertime=new HashMap<String, String>();
//			Iterator<String> it=hmEmpOvertime.keySet().iterator();
//		
//			while(it.hasNext()){
//				String strDate = it.next();
//				double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
//				
//				
//				if(hmHolidayDates!=null && hmHolidayDates.containsKey(uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDateFormat()) )){
//					strOverTimeType = "PH";
//				}else if(hmWeekEnds!=null && hmWeekEnds.containsKey(strDate+"_"+hmEmpWlocationMap.get(strEmpId))){
//					strOverTimeType = "BH";
//				}else{
//					strOverTimeType = "EH";
//				}
//						
//				Map<String,String> hmOvertimePolicy=hmEmpOverTimeLevelPolicy.get(strLevelId+"_"+strOverTimeType);
//				if(hmOvertimePolicy==null) hmOvertimePolicy=new HashMap<String, String>();
//				
//				String salaryHeadId=hmOvertimePolicy.get("SALARY_HEAD_ID");
//				List<String> salaryHeadList=null;
//				if(salaryHeadId!=null){
//					salaryHeadList=Arrays.asList(salaryHeadId.split(","));
//				}
//				
//				if("RH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))){
//					dblOverTimeCalcHours = uF.parseToDouble(hmEmpRosterHours.get(strDate+"_"+strEmpId));
//				}else if("SWH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))){
//					dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
//				}else{
//					dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
//				}
//				
//				
//				
//				if("MD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))){
//					dblOverTimeCalcDays = nTotalNumberOfDays; 
//				}else if("AWD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))){
//					//dblOverTimeCalcDays = nTotalNumberOfDays - nWeekEnds;
//					dblOverTimeCalcDays = dblTotalPresentDays;
//				}else{
//					dblOverTimeCalcDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));
//				}
//				
//
//										
//				double dblSubSalaryAmount = 0;
//				double dblSubSalaryAmountActualCTC = 0;
//				for(int i=0;salaryHeadList!=null && !salaryHeadList.isEmpty() && i<salaryHeadList.size();i++){
//												
//					Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(salaryHeadList.get(i).trim());
//					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//					dblSubSalaryAmount += uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));					
//												
//					Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(salaryHeadList.get(i).trim());
//					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//					dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//					
//				}
//				
//				
//				String overtimePaymentType=hmOvertimePolicy.get("OVERTIME_PAYMENT_TYPE");
//				String strCalcBasic =hmOvertimePolicy.get("CAL_BASIS");
//				double dblAmount = uF.parseToDouble((String)hmOvertimePolicy.get("OVERTIME_PAYMENT_AMOUNT"));
//				
//				
//				
//				if(strCalcBasic!=null && strCalcBasic.equals("FD")){
//					if(overtimePaymentType!=null && overtimePaymentType.equals("A")){
//						dblTotalOverTimeAmount += dblAmount;
//					}else{
//						dblTotalOverTimeAmount += dblAmount * dblSubSalaryAmount/ 100;
//					}
//					
//
//					
//				}else if(dblOverTimeCalcHours>0){
//					if(overtimePaymentType!=null && overtimePaymentType.equals("A")){
//						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount / ( dblOverTimeCalcDays * dblOverTimeCalcHours);
//					}else{
//						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount * dblSubSalaryAmount/ (100 * dblOverTimeCalcDays * dblOverTimeCalcHours);								 
//					}
//				}
//			}
//
//			
//			
//			
//			dblTotalOverTimeAmount += uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		//return uF.parseToDouble(uF.formatIntoComma(dblTotalOverTimeAmount));
//		return dblTotalOverTimeAmount;
//	}

	

//	private double getOverTimeCalculationHours(Connection con, UtilityFunctions uF, String strEmpId, 
//			List alPresentDays, List alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, 
//			String strLevelId, double dblStandardHours, Map<String, String> hmHolidays, 
//			int nTotalNumberOfDaysForCalc1, Map<String, String> hmIndividualOvertime,Map<String, Map<String, String>> hmEmpOverTimeHours,Map<String, Map<String, String>> hmEmpOverTimeLevelPolicy, Map<String, String> hmEmpRosterHours, Map<String, String> hmWlocationMap, int nTotalNumberOfDays, int nWeekEnds,int nHolidays,double dblPresent,double dblTotalPresentDays,Map<String, String> hmHolidayDates,Map hmWeekEnds,Map hmEmpWlocationMap) {
//		
//		double dblTotalOverTimeAmount = 0.0d;
//		
//		try {
//			
//			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
////			String strLevelId = hmEmpLevelMap.get(strEmpId);
//			String strOverTimeType = null;
//			double dblTotalOverTime = 0;
//			double dblTotalHoursWorked = 0;
//			double dblOverTimeCalcHours = 0;
//			double dblOverTimeCalcDays = 0;
//			
//			double dblOvertimeFixedAmount = 0;
//			
//			
////			Map<String,String> hmOvertimePolicy=new HashMap<String, String>();
//			
//			Map<String,String> hmEmpOvertime=hmEmpOverTimeHours.get(strEmpId);
//			if(hmEmpOvertime==null) hmEmpOvertime=new HashMap<String, String>();
//			Iterator<String> it=hmEmpOvertime.keySet().iterator();
//		
//			while(it.hasNext()){
//				String strDate = it.next();
//				double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
//				
//				
//				if(hmHolidayDates!=null && hmHolidayDates.containsKey(uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDateFormat()) )){
//					strOverTimeType = "PH";
//				}else if(hmWeekEnds!=null && hmWeekEnds.containsKey(strDate+"_"+hmEmpWlocationMap.get(strEmpId))){
//					strOverTimeType = "BH";
//				}else{
//					strOverTimeType = "EH";
//				}
//						
//				Map<String,String> hmOvertimePolicy=hmEmpOverTimeLevelPolicy.get(strLevelId+"_"+strOverTimeType);
//				if(hmOvertimePolicy==null) hmOvertimePolicy=new HashMap<String, String>();
//				
//				String salaryHeadId=hmOvertimePolicy.get("SALARY_HEAD_ID");
//				List<String> salaryHeadList=null;
//				if(salaryHeadId!=null){
//					salaryHeadList=Arrays.asList(salaryHeadId.split(","));
//				}
//				
//				if("RH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))){
//					dblOverTimeCalcHours = uF.parseToDouble(hmEmpRosterHours.get(strDate+"_"+strEmpId));
//				}else if("SWH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))){
//					dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
//				}else{
//					dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
//				}
//				
//				
//				
//				if("MD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))){
//					dblOverTimeCalcDays = nTotalNumberOfDays; 
//				}else if("AWD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))){
//					//dblOverTimeCalcDays = nTotalNumberOfDays - nWeekEnds;
//					dblOverTimeCalcDays = dblTotalPresentDays;
//				}else{
//					dblOverTimeCalcDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));
//				}
//				
//
//										
//				double dblSubSalaryAmount = 0;
//				double dblSubSalaryAmountActualCTC = 0;
//				for(int i=0;salaryHeadList!=null && !salaryHeadList.isEmpty() && i<salaryHeadList.size();i++){
//												
//					Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(salaryHeadList.get(i).trim());
//					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//					dblSubSalaryAmount += uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));					
//												
//					Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(salaryHeadList.get(i).trim());
//					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//					dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//					
//				}
//				
//				
//				String overtimePaymentType=hmOvertimePolicy.get("OVERTIME_PAYMENT_TYPE");
//				String strCalcBasic =hmOvertimePolicy.get("CAL_BASIS");
//				double dblAmount = uF.parseToDouble((String)hmOvertimePolicy.get("OVERTIME_PAYMENT_AMOUNT"));
//				
//				
//				
//				if(strCalcBasic!=null && strCalcBasic.equals("FD")){
//					if(overtimePaymentType!=null && overtimePaymentType.equals("A")){
//						dblTotalOverTimeAmount += dblAmount;
//					}else{
//						dblTotalOverTimeAmount += dblAmount * dblSubSalaryAmount/ 100;
//					}
//					
//
//					
//				}else if(dblOverTimeCalcHours>0){
//					if(overtimePaymentType!=null && overtimePaymentType.equals("A")){
//						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount / ( dblOverTimeCalcDays * dblOverTimeCalcHours);
//					}else{
//						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount * dblSubSalaryAmount/ (100 * dblOverTimeCalcDays * dblOverTimeCalcHours);								 
//					}
//				}
//			}
//
//			
//			
//			
//			dblTotalOverTimeAmount += uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		//return uF.parseToDouble(uF.formatIntoComma(dblTotalOverTimeAmount));
//		return dblTotalOverTimeAmount;
//	}

	
	
	


	public List<String> getwLocation() {
        return wLocation;
    }


    public void setwLocation(List<String> wLocation) {
        this.wLocation = wLocation;
    }


    

    public List<String> getLevel() {
		return level;
	}
	public void setLevel(List<String> level) {
		this.level = level;
	}
	public List<String> getDepartment() {
		return department;
	}
	public void setDepartment(List<String> department) {
		this.department = department;
	}
	public List<String> getService() {
		return service;
	}
	public void setService(List<String> service) {
		this.service = service;
	}
	public String getApprove() {
		return approve;
	}

	public void setApprove(String approve) {
		this.approve = approve;
	}   

	public String getDtMin() {
		return dtMin;
	}

	public void setDtMin(String dtMin) {
		this.dtMin = dtMin;
	}

	public String getDtMax() {
		return dtMax;
	}

	public void setDtMax(String dtMax) {
		this.dtMax = dtMax;
	}

	public List<String> getChbox() {
		return chbox;
	}

	public void setChbox(List<String> chbox) {
		this.chbox = chbox;
	}

	public String[] getEmpID() {
		return empID;
	}

	public void setEmpID(String[] empID) {
		this.empID = empID;
	}

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

//	public String getStrFrmD1() {
//		return strFrmD1;
//	}
//
//	public void setStrFrmD1(String strFrmD1) {
//		this.strFrmD1 = strFrmD1;
//	}
//
//	public String getStrFrmD2() {
//		return strFrmD2;
//	}
//
//	public void setStrFrmD2(String strFrmD2) {
//		this.strFrmD2 = strFrmD2;
//	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setStrAlpha(String strAlpha) {
		this.strAlpha = strAlpha;
	}


	public String getApprovePC() {
		return approvePC;
	}


	public void setApprovePC(String approvePC) {
		this.approvePC = approvePC;
	}
	
	
	
	public double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearEnd, int nPayMonth, String strWLocationStateId){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblDeductionPayMonth = 0;
		
		
		try {
			
			pst = con.prepareStatement("select top 1 * from deduction_details_india where income_from<= ? and income_to>= ? and state_id=? and financial_year_from = (select max(financial_year_from) from deduction_details_india)");
			
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			
			rs = pst.executeQuery();
			
			
			
			
			double dblDeductionAmount = 0;
			double dblDeductionPaycycleAmount = 0;
			while(rs.next()){
				dblDeductionAmount = rs.getDouble("deduction_amount");
				dblDeductionPaycycleAmount = rs.getDouble("deduction_paycycle");
			}
			
			int nFinancialYearEndMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DBDATE, "MM"));
			nFinancialYearEndMonth = nFinancialYearEndMonth - 1;

			if(nFinancialYearEndMonth==nPayMonth){
				dblDeductionPayMonth = dblDeductionAmount - (11*dblDeductionPaycycleAmount);
			}else{
				dblDeductionPayMonth = dblDeductionPaycycleAmount;
			}

			

			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
		}
		return dblDeductionPayMonth;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
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
	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}
	public void setPaycycleDurationList(List<FillPayCycleDuration> paycycleDurationList) {
		this.paycycleDurationList = paycycleDurationList;
	}
//	public String getStrPaycycleDuration() {
//		return strPaycycleDuration;
//	}
//	public void setStrPaycycleDuration(String strPaycycleDuration) {
//		this.strPaycycleDuration = strPaycycleDuration;
//	}
	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}
	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}
//	public String getF_paymentMode() {
//		return f_paymentMode;
//	}
//	public void setF_paymentMode(String f_paymentMode) {
//		this.f_paymentMode = f_paymentMode;
//	}
	public String[] getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String[] paymentMode) {
		this.paymentMode = paymentMode;
	}


	public String getOrg() {
		return org;
	}


	public void setOrg(String org) {
		this.org = org;
	}


	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}


	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	
	
	public void setCF(CommonFunctions CF){
		this.CF=CF;
	}
	@Override
	public void setSession(Map session) {
		
		this.session=session;
		// TODO Auto-generated method stub
		
	}
	


	
	public String getString(List<String> list,String seperator){
		StringBuilder sb=null;
		if(list!=null){
			for(String a:list){
				if(sb==null){
					sb=new StringBuilder(a);
				}else{
					sb.append(seperator+a);
				}
				
			}
			return sb.toString();
		}
		return null;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		alFilter.add("DURATION");
//		if(getStrPaycycleDuration()!=null){
//			String payDuration="";
//			int k=0;
//			for(int i=0;paycycleDurationList!=null && i<paycycleDurationList.size();i++){
//				if(getStrPaycycleDuration().equals(paycycleDurationList.get(i).getPaycycleDurationId())){
//					if(k==0){
//						payDuration=paycycleDurationList.get(i).getPaycycleDurationName();
//					}else{
//						payDuration+=", "+paycycleDurationList.get(i).getPaycycleDurationName();
//					}
//					k++;
//				}
//			}
//			if(payDuration!=null && !payDuration.equals("")){
//				hmFilter.put("DURATION", payDuration);
//			}else{
//				hmFilter.put("DURATION", "All Duration");
//			}
//		}else{
//			hmFilter.put("DURATION", "All Duration");
//		}

		alFilter.add("PAYCYCLE");	
		if(getPaycycle()!=null){
			String strPayCycle="";
			int k=0;
			for(int i=0;paycycleList!=null && i<paycycleList.size();i++){
				if(getPaycycle().equals(paycycleList.get(i).getPaycycleId())){
					if(k==0){
						strPayCycle=paycycleList.get(i).getPaycycleName();
					}else{
						strPayCycle+=", "+paycycleList.get(i).getPaycycleName();
					}
					k++;
				}
			}
			if(strPayCycle!=null && !strPayCycle.equals("")){
				hmFilter.put("PAYCYCLE", strPayCycle);
			}else{
				hmFilter.put("PAYCYCLE", "All Paycycle");
			}
			
		}
		
		alFilter.add("ORGANISATION");
		if(getOrg()!=null){			
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getOrg().equals(organisationList.get(i).getOrgId())){
					if(k==0){
						strOrg=organisationList.get(i).getOrgName();
					}else{
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")){
				hmFilter.put("ORGANISATION", strOrg);
			}else{
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		}else{
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("PAYMODE");
//		if(getF_paymentMode()!=null){			
//			String strPayMode="";
//			int k=0;
//			for(int i=0;paymentModeList!=null && i<paymentModeList.size();i++){
//				if(getF_paymentMode().equals(paymentModeList.get(i).getPayModeId())){
//					if(k==0){
//						strPayMode=paymentModeList.get(i).getPayModeName();
//					}else{
//						strPayMode+=", "+paymentModeList.get(i).getPayModeName();
//					}
//					k++;
//				}
//			}
//			if(strPayMode!=null && !strPayMode.equals("")){
//				hmFilter.put("PAYMODE", strPayMode);
//			}else{
//				hmFilter.put("PAYMODE", "All Payment Mode");
//			}
//			
//		}else{
//			hmFilter.put("PAYMODE", "All Payment Mode");
//		}
		
		
		alFilter.add("LOCATION");
		if(getwLocation()!=null){
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++){
				for(int j=0;j<getwLocation().size();j++){
					if(getwLocation().get(j).equals(wLocationList.get(i).getwLocationId())){
						if(k==0){
							strLocation=wLocationList.get(i).getwLocationName();
						}else{
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")){
				hmFilter.put("LOCATION", strLocation);
			}else{
				hmFilter.put("LOCATION", "All Locations");
			}
		}else{
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getDepartment()!=null){
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++){
				for(int j=0;j<getDepartment().size();j++){
					if(getDepartment().get(j).equals(departmentList.get(i).getDeptId())){
						if(k==0){
							strDepartment=departmentList.get(i).getDeptName();
						}else{
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")){
				hmFilter.put("DEPARTMENT", strDepartment);
			}else{
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		}else{
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getService()!=null){
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++){
				for(int j=0;j<getService().size();j++){
					if(getService().get(j).equals(serviceList.get(i).getServiceId())){
						if(k==0){
							strService=serviceList.get(i).getServiceName();
						}else{
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")){
				hmFilter.put("SERVICE", strService);
			}else{
				hmFilter.put("SERVICE", "All Services");
			}
		}else{
			hmFilter.put("SERVICE", "All Services");
		}
		
		alFilter.add("LEVEL");
		if(getLevel()!=null){
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++){
				for(int j=0;j<getLevel().size();j++){
					if(getLevel().get(j).equals(levelList.get(i).getLevelId())){
						if(k==0){
							strLevel=levelList.get(i).getLevelCodeName();
						}else{
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")){
				hmFilter.put("LEVEL", strLevel);
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
		}else{
			hmFilter.put("LEVEL", "All Levels");
		}
		
		String selectedFilter=CF.getSelectedFilter(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
}



//
//class ApprovePayrollRunnableNEW implements Runnable, IConstants{
//	
//	ApprovePayrollNew objApprovePayroll;
//	GenerateSalarySlip gs;
//	
//	Map hmTotal;
//	Connection con;
//	UtilityFunctions uF;
//	CommonFunctions CF;
//	String strFinancialYearStart;
//	String strFinancialYearEnd;
//	String strEmpId;
//	String []strApprovePayCycle;
//	Map hmEmpStateMap;
//	Map hmCurrencyDetails;
//	Map hmEmpCurrency;
//	Map hmVariables;
//	HttpServletRequest request;
//	HttpServletResponse response;
//	double dblTotal;
//	
//	Map<String, String> hmOtherTaxDetails;
//	Map<String, String> hmEmpLevelMap;
//	String strDomain;
//	
//	public ApprovePayrollRunnableNEW(ApprovePayrollNew objApprovePayroll, GenerateSalarySlip gs, Connection con, UtilityFunctions uF, CommonFunctions CF, String strFinancialYearStart, String strFinancialYearEnd, String []strApprovePayCycle, Map hmEmpStateMap, Map hmCurrencyDetails, Map hmEmpCurrency, Map hmVariables, HttpServletRequest request, HttpServletResponse response,Map<String, String> hmOtherTaxDetails,Map<String, String> hmEmpLevelMap, String strDomain){
//		this.objApprovePayroll = objApprovePayroll;
//		this.gs = gs;
//		this.con = con;
//		this.uF = uF;
//		this.CF = CF;
//		this.strFinancialYearStart = strFinancialYearStart;
//		this.strFinancialYearEnd = strFinancialYearEnd;
//		
//		this.strApprovePayCycle = strApprovePayCycle;
//		this.hmEmpStateMap = hmEmpStateMap;
//		this.hmCurrencyDetails = hmCurrencyDetails;
//		this.hmEmpCurrency = hmEmpCurrency;
//		this.hmVariables = hmVariables;
//		this.request = request;
//		this.response = response;
//		this.hmOtherTaxDetails=hmOtherTaxDetails;
//		this.hmEmpLevelMap=hmEmpLevelMap;
//		this.strDomain = strDomain;
//	}
//	
//	
//	public void setData(Map hmTotal, String strEmpId, double dblTotal){
//		this.hmTotal = hmTotal;
//		this.strEmpId = strEmpId;
//		this.dblTotal = dblTotal;
//		
//	}
//	
//	public void run(){
//
////		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_EPF+"")){
////			objApprovePayroll.calculateEEPF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true);
////			objApprovePayroll.calculateERPF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true);
////		}
//		
//		
//		
////		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_ESI+"")){
////			objApprovePayroll.calculateEESI(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true);
////		}
//		
//		
////		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_LWF+"")){
////			objApprovePayroll.calculateELWF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true);
////		}
//		
////		if(hmTotal!=null && hmTotal.containsKey(TDS+"")){
////			objApprovePayroll.calculateETDS(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true,hmOtherTaxDetails,hmEmpLevelMap);
////		}
//		
//		
//	}
//	
//	
//}
