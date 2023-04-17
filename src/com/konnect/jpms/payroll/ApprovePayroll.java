package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set; 
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.employee.EmployeeActivity;
import com.konnect.jpms.export.GenerateSalarySlip;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class ApprovePayroll extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	

	/**
	 *   
	 */  
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strEmpID;   
	String strUserType;  
	String strSessionEmpId;       
    
	String strFrmD1 = null; 
	String strFrmD2 = null;  

	String approvePC = null;
	String strAlpha = null;

	public CommonFunctions CF = null;

	private String paycycle;
	private String []wLocation;
	private String []level;
	private String []f_department;
	private String []f_service; 
	
	private List<FillPayCycles> paycycleList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillWLocation> wLocationList;
	private List<FillPayCycleDuration> paycycleDurationList;
	private String strPaycycleDuration;
	
	private List<FillPayMode> paymentModeList;
	private String f_paymentMode;
	private String f_org;
	
	private List<FillOrganisation> organisationList;
	private String redirectUrl;
	private String approve;
	private String dtMin;
	private String dtMax;
	private String[] chbox;
	private String[] empID;
	private String[] paymentMode;
	
	private static Logger log = Logger.getLogger(ApprovePayroll.class);

	public String execute() throws Exception {

		session = request.getSession();
		if (session == null) return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId =  (String)session.getAttribute(EMPID);
//		strVeryEmpId = request.getParameter("strVeryEmpId");
		
		
		String APPROVE =  request.getParameter("approve"); 
		strAlpha =  request.getParameter("alphaValue");
		setStrAlpha(strAlpha);

		request.setAttribute(TITLE, "Approve Compensation");
		strEmpID =  request.getParameter("EMPID");
//		String strEmpType = (String) session.getAttribute("USERTYPE");
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

		String[] strPayCycleDates = null;
		
		if(uF.parseToInt(getF_org()) == 0) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrPaycycleDuration()==null || getStrPaycycleDuration().equals("") || getStrPaycycleDuration().equalsIgnoreCase("NULL")) {
			setStrPaycycleDuration("M");
		}
		
		if(getApprovePC()!=null && !getApprovePC().trim().equalsIgnoreCase("") && !getApprovePC().trim().equalsIgnoreCase("NULL") && getApprovePC().length()>0) {
//			System.out.println("if");
			strPayCycleDates = getApprovePC().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}else if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
//			System.out.println("else if");
			strPayCycleDates = getPaycycle().split("-");
//			strPayCycleDates = CF.getPrevPayCycle(strPayCycleDates[1], CF.getStrTimeZone(), CF);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
//			System.out.println("else"); 
//			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			strPayCycleDates = CF.getCurrentPayCycleUsingDurationByOrg(CF.getStrTimeZone(), CF, getF_org(),request,getStrPaycycleDuration());
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		if(getwLocation()!=null) {
			setwLocation(getwLocation());
		}
		
		if(getLevel()!=null) {
			setLevel(getLevel());
		}
		

		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		String strPC = strPayCycleDates[2];
		
//		System.out.println("strPC="+strPC +" strD1="+strD1+"   strD2="+strD2);
		
		String referer = request.getHeader("Referer");
		if (referer != null) {
			int index1 = referer.indexOf(request.getContextPath());
			int index2 = request.getContextPath().length();
			referer = referer.substring(index1 + index2 + 1);
		}
		setRedirectUrl(referer);
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		request.setAttribute(PAGE, PApprovePayroll);
		strEmpID =  (String)session.getAttribute(EMPID);
		
		
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE) {
			if (APPROVE != null) {
				request.setAttribute(PAGE, PReportClockManager);
				request.setAttribute(MESSAGE, "Payroll generated");
				approvePayrollEntriesByGrade(CF,strD1,strD2);
				
				loadClockEntries(uF);
				return SUCCESS;
			}
			viewClockEntriesForPayrollApprovalByGrade(CF, null, strD1, strD2,strPC);
		} else {
//			viewClockEntriesForPayrollApproval(CF, null, strD1, strD2);
			if (APPROVE != null) {
				request.setAttribute(PAGE, PReportClockManager);
				request.setAttribute(MESSAGE, "Payroll generated");
				approvePayrollEntries(CF,strD1,strD2);
				
				loadClockEntries(uF);
				return SUCCESS;
			}
//			System.out.println("AP/205---else");
			viewClockEntriesForPayrollApproval(CF, null, strD1, strD2,strPC);
		}
		
		loadClockEntries(uF);
		
		return LOAD;
	}

	private void viewClockEntriesForPayrollApprovalByGrade(CommonFunctions CF, String strReqEmpId, String strD1, String strD2, String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
//			this.strD1 = strD1;
//			this.strD2 = strD2;
//			System.out.println("strReqEmpId===="+strReqEmpId);
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int nTotalNumberOfDays = 0;
			if(uF.parseToBoolean(CF.getIsPaycycleAdjustment())) {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
				nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
			} else {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
				nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
			}
			
//			int nTotalNumberOfDays = uF.parseToInt(uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
			
			con = db.makeConnection(con);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if(strFinancialYear!=null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			double dblInvestmentExemption = 0.0d;
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));

				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
				
//				dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()) {
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
					
			 
//			System.out.println("pst===>"+pst);
			
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();
			
			
//			String strFinancialYearEnd = null;
//			String strFinancialYearStart = null;
//			double dblInvestmentExemption = 0.0d;
			double dblStandardHrs = 0.0d;
			
			Map<String, String> hmOrg= CF.getOrgDetails(con,uF,getF_org());
			if(hmOrg == null) hmOrg=new HashMap<String, String>();
			
			
			
			Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
			Map<String, String> hmEmpEndDateMap = new HashMap<String, String>();
//			Map<String, Map<String, String>> hmWlocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpStateMap = new HashMap<String, String>();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map<String, String> hmEmpJoiningMap = CF.getEmpJoiningDateMap(con, uF, hmEmpEndDateMap); 
			request.setAttribute("hmEmpJoiningMap", hmEmpJoiningMap);
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null); 
			Map<String, String> hmEmpCodeMap = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
			Map<String, String> hmEmpExemptionsMap = getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
			Map<String, String> hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, Map<String, String>> hmEmpPaidSalary = CF.getPaidSalary(con, strFinancialYearStart, strFinancialYearEnd, strPC);
			Map<String, String> hmEmpRentPaidMap = getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmEmpIncomeOtherSourcesMap = getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
//			Map<String, String> hmLeaveEncashment = CF.getLeaveEncashment(con, uF, strD1, strD2, strPC);

//			Map<String, Map<String, String>> hmOverTimeMap = CF.getOverTimeMap(con, CF);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con);

			Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, getF_org());
			Map<String, Map<String, String>> hmLeaveDays = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
			Map<String, String> hmMonthlyLeaves = new HashMap<String, String>();
			
//			hmLeaveDays = CF.getLeaveDates(con, strD1, strD2, CF, hmLeaveTypeDays, true, hmMonthlyLeaves);
			hmLeaveDays = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, hmLeaveTypeDays, true, hmMonthlyLeaves);
//			System.out.println("hmLeaveDays=====>"+hmLeaveDays);
//			System.out.println("hmLeaveTypeDays======>"+hmLeaveTypeDays);
//			System.out.println("hmMonthlyLeaves=======>"+hmMonthlyLeaves);
			
			String strTotalDays = nTotalNumberOfDays+"";
			
			Map<String, String> hmTotalDays = new HashMap<String, String>();
			
//			Map hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
//			Map<String, String> hmHolidays = new HashMap<String, String>();
//			Map<String, String> hmHolidayDates = new HashMap<String, String>();
//			CF.getHolidayList(con, strD1, strD2, CF, hmHolidayDates, hmHolidays, true);
//			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request, uF,strD1, strD2);
			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request, uF,strD1, strD2);
			
			Map<String, String> hmLongLeaves = getLongLeavesCount(con,uF,CF,strD1,strD2,strPC);
			if(hmLongLeaves == null) hmLongLeaves = new HashMap<String, String>();
			
			
//			System.out.println("hmHolidayDates="+hmHolidayDates);
//			System.out.println("hmHolidays="+hmHolidays);
//			System.out.println("hmWeekEnds="+hmWeekEnds);
			
			
			/*Calendar cal1  = GregorianCalendar.getInstance();
			cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
			cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
			String strDateNew = uF.getDateFormat("01/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
			
			System.out.println("strDateNew=="+strDateNew);
			System.out.println("strD1=="+strD1);
			System.out.println("strD2=="+strD2);
			*/
//			Map hmWeekEnds = CF.getWeekEndDateList(con, strDateNew, strD2, CF, uF);
			
			
			Map<String, String> hmAttendanceDependent = CF.getAttendanceDependency(con);
			Map<String, String> hmRosterDependent = CF.getRosterDependency(con);
			Map<String,Map<String,String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String,String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String,String> hmEmpPaymentMode = CF.getEmpPaymentMode(con, uF);
			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String,String> hmFixedExemptions = getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
  
			
			List<String> alPresentEmpId = new ArrayList<String>();
			List<String> alPresentDates = new ArrayList<String>();
			List<String> alPresentWeekEndDates = new ArrayList<String>();
			List<String> alHalfDaysDueToLatePolicy = new ArrayList<String>();
			List<String> alServices = new ArrayList<String>();
			Map<String, List<String>> hmPresentDays = new HashMap<String, List<String>>();
			Map<String, List<String>> hmPresentWeekEndDays = new HashMap<String, List<String>>();
			Map<String, List<String>> hmHalfDays = new HashMap<String, List<String>>();
			Map<String, String> hmPresentDays1 = new HashMap<String, String>();
			Map<String, String> hmPaidDays = new HashMap<String, String>();
			Map<String, List<String>> hmServices = new HashMap<String, List<String>>();
			Map<String, String> hmHoursWorked = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpHoursWorked = new HashMap<String, Map<String, String>>();
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			
			Map<String,String> hmEmpRosterHours = new HashMap<String,String>();
			Map<String, Map<String,String>> hmEmpOverTimeHours=CF.getEmpOverTimeHours(con,CF,uF,strD1,strD2,strPC);
			Map<String, Map<String,String>> hmEmpOverTimeLevelPolicy=CF.getEmpOverTimeLevelPolicy(con,CF,uF,strD1,strD2,strPC);
			
			Map<String, String> hmHolidayCount = CF.getHolidayCount(con,CF,uF,strD1,strD2,strPC);
			
			List<String> alFullDaysDueToLatePolicy = new ArrayList<String>();
			Map<String, List<String>> hmFullDays = new HashMap<String, List<String>>();
			
			List<String> alEmp = new ArrayList<String>();
			
			Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and (employment_end_date is null OR employment_end_date >= ?) and joining_date<= ? and emp_per_id > 0 ");
			
			if(getLevel()!=null && getLevel().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getLevel(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
            
            
			if(getStrPaycycleDuration()!=null) {
				sbQuery.append(" and paycycle_duration ='"+getStrPaycycleDuration()+"'");
			}
			
			if(uF.parseToInt(getF_paymentMode())>0) {
				sbQuery.append(" and payment_mode ="+uF.parseToInt(getF_paymentMode()));
			}
			
			if(getwLocation()!=null && getwLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getwLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" and emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? " +
					"and paycycle = ? group by emp_id)");
			sbQuery.append(" order by emp_fname, emp_lname"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(strPC));
//			System.out.println("pst=====>"+pst); 
			rs = pst.executeQuery();
			StringBuilder sbEmPId = new StringBuilder();
			Map<String, String> hmEmpPaycycleDuration = new HashMap<String, String>();
			Map<String, String> hmEmpSalCalStatus = new HashMap<String, String>();
			List<String> alEmpJoinDate = new ArrayList<String>();
			List<String> alGradeId = new ArrayList<String>();
			while(rs.next()) {
				alEmp.add(rs.getString("emp_per_id"));
				sbEmPId.append(rs.getString("emp_per_id")+",");
				
				hmEmpPaycycleDuration.put(rs.getString("emp_per_id"), rs.getString("paycycle_duration"));
				
				if(rs.getString("joining_date")!=null) { 
					Date date = uF.getDateFormatUtil(rs.getString("joining_date"), DBDATE);
					if(uF.isDateBetween(sDate, eDate, date) ) {
						alEmpJoinDate.add(rs.getString("emp_per_id"));
					}
				}
				
				if (!alGradeId.contains(rs.getString("grade_id"))) {
					alGradeId.add(rs.getString("grade_id"));
				}
				hmEmpSalCalStatus.put(rs.getString("emp_per_id"), rs.getString("is_disable_sal_calculate"));
			}
			rs.close();
			pst.close(); 
			request.setAttribute("alEmpJoinDate", alEmpJoinDate);
//			System.out.println("alEmpJoinDate=====>"+alEmpJoinDate);
			
			if(sbEmPId.length()>1) {
				sbEmPId.replace(0, sbEmPId.length(), sbEmPId.substring(0, sbEmPId.length()-1));
			}
			
			/*if(strReqEmpId!=null) {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and emp_id =? order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpId));
			}else if(sbEmPId.length()>1) {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and emp_id in ("+sbEmPId.toString()+") order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			} else {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			}*/
			if(strReqEmpId!=null) {
//				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and emp_id =? order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id =? " +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+
						"order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strPC));
			}else if(sbEmPId.length()>1) {
//				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and emp_id in ("+sbEmPId.toString()+") order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and ad.emp_id in ("+sbEmPId.toString()+") order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				// Remove In_out='OUT'
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id in ("+sbEmPId.toString()+")" +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+		
						" order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPC));
			} else {
				//pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  " +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+
						" order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPC));
			}
			Map<String, String> hmOverLappingHolidays = new HashMap<String, String>();
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();  
			double dblOverLappingHolidays = 0;
			String strPresentEmpIdNew = null;
			String strPresentEmpIdOld = null;
			Set<String> tempweeklyOffSet=null;
			String level=null;
			String location=null;
			Set<String> tempholidaysSet=null;
			while(rs.next()) {
				
				strPresentEmpIdNew = rs.getString("emp_id");
				if(strPresentEmpIdNew!=null && !strPresentEmpIdNew.equalsIgnoreCase(strPresentEmpIdOld)) {
					alPresentEmpId = new ArrayList<String>();
					alPresentDates = new ArrayList<String>();
					alPresentWeekEndDates = new ArrayList<String>();
					alServices = new ArrayList<String>();
					hmHoursWorked = new HashMap<String, String>();
					halfDayCountIN = 0;
					halfDayCountOUT = 0;
					dblOverLappingHolidays = 0;
					alHalfDaysDueToLatePolicy = new ArrayList<String>();
					location = hmEmpWlocationMap.get(strPresentEmpIdNew);
					level=hmEmpLevelMap.get(strPresentEmpIdNew);
//					tempweeklyOffSet=hmWeekEnds.get(level);
					if(alEmpCheckRosterWeektype.contains(strPresentEmpIdNew)) {
						tempweeklyOffSet = hmRosterWeekEndDates.get(strPresentEmpIdNew);
					} else {
						tempweeklyOffSet = hmWeekEnds.get(location);
					}
					if(tempweeklyOffSet==null)tempweeklyOffSet=new HashSet<String>();
					
					Set<String> temp=holidaysMp.get(location);
					if(temp==null)temp=new HashSet<String>();
					tempholidaysSet=new HashSet<String>(temp);	
					tempholidaysSet.removeAll(tempweeklyOffSet);
//					if(strPresentEmpIdNew.equals("118")) {
//						System.out.println("tempweeklyOffSet==="+tempweeklyOffSet);
//						System.out.println("tempholidaysSet==="+tempholidaysSet);
//						System.out.println("level==="+level);
//
//					}
					
					alFullDaysDueToLatePolicy = new ArrayList<String>();
					fullDayCountIN=0;
					fullDayCountOUT=0;
				}
				
				if(!alPresentEmpId.contains(strPresentEmpIdNew)) {
					alPresentEmpId.add(strPresentEmpIdNew);
				}
				
				hmEmpRosterHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("emp_id"), rs.getString("actual_hours"));
				
				String strDay = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
				strDay = strDay.toUpperCase();
				
//				double dblEarlyLate = rs.getDouble("early_late"); 
				double dblEarlyLate = uF.parseToInt(rs.getString("approved"))== -1 ? rs.getDouble("early_late") : 0.0d; 
				double dblHDHoursWoeked = uF.parseToInt(rs.getString("approved"))!= -1 ? rs.getDouble("hours_worked") : 0.0d; 
				 
//				if(uF.parseToInt(rs.getString("emp_id")) == 11) {
//					System.out.println("inout====="+rs.getString("in_out")+"----"+strDate+" approved====>"+uF.parseToInt(rs.getString("approved"))+ "====dblEarlyLate====>"+dblEarlyLate+"----dblHDEarlyLate====>"+dblHDEarlyLate);
//				} 
				
				String strINOUT = rs.getString("in_out");
				Map<String, String> hmLeaves = hmLeaveDays.get(strPresentEmpIdNew);
				if(hmLeaves==null)hmLeaves = new HashMap<String, String>();
				
//				if(strPresentEmpIdNew.equals("118")) {
//					System.out.println("strDate==="+strDate);
//				}
				if(!alPresentDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
					/**
					 * To avoid the over presence data
					 */
					
//					Map hmLeaves = hmLeaveDays.get(strPresentEmpIdNew);
//					if(hmLeaves==null)hmLeaves = new HashMap();
					
//					String strWeekEnd = hmWeekEnds.get(strDay+"_"+strLocation);
					String strWeekEnd = null;
					
					if(tempweeklyOffSet.contains(strDate)) {
						strWeekEnd = WEEKLYOFF_COLOR;
					}
					
					if(strWeekEnd==null ) { //&& !hmLeaves.containsKey(strDate)
//					if(strWeekEnd==null && !hmLeaves.containsKey(strDate)) {
						alPresentDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}else if(!alPresentWeekEndDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						alPresentWeekEndDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}
					if(tempholidaysSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						dblOverLappingHolidays++;
					}
					
				}
				
				boolean isRosterDependent = uF.parseToBoolean(hmRosterDependent.get(strPresentEmpIdNew));
				
				if(dblEarlyLate > 0.0d) {
					if(isHalfDay(strDate, dblEarlyLate, strINOUT, hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alHalfDaysDueToLatePolicy!=null
							&& isRosterDependent
							&& !alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))
							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
					
						alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}
					
					if(!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						if(isFullDay(strDate, dblEarlyLate, strINOUT, (String)hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alFullDaysDueToLatePolicy!=null
								&& isRosterDependent
								&& !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))
								&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							alFullDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						}
					}
				}
				
				double x = Math.abs(dblHDHoursWoeked);
				if(x > 0.0d && x <=5) {
					if(!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						String strWLocationId = hmEmpWlocation.get(rs.getString("emp_id"));
						Set<String> weeklyOffSet= hmWeekEnds.get(strWLocationId);
						if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
						
						Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
						if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
						
						Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_id"));
						if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
						if(alEmpCheckRosterWeektype.contains(rs.getString("emp_id"))) {
							if(!rosterWeeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
								alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
							}
						} else if(weeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							
						} else if(holidaysMp.containsKey(uF.getDateFormat(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)) {
							
						} else {
							alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						}				
					}
				}
				
//				if(uF.parseToInt(rs.getString("emp_id")) == 11) {
//					System.out.println("alHalfDaysDueToLatePolicy====="+alHalfDaysDueToLatePolicy);
//				} 
				
				
				if(!alServices.contains(rs.getString("service_id"))) {  
					alServices.add(rs.getString("service_id"));
				} 
				
				hmPresentDays.put(strPresentEmpIdNew, alPresentDates);
				hmPresentWeekEndDays.put(strPresentEmpIdNew, alPresentWeekEndDates);
				hmHalfDays.put(strPresentEmpIdNew, alHalfDaysDueToLatePolicy);
				hmFullDays.put(strPresentEmpIdNew, alFullDaysDueToLatePolicy);
				
				hmServices.put(strPresentEmpIdNew, alServices);
				
				if("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
					hmHoursWorked.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id"), rs.getString("hours_worked"));
				}
				hmEmpHoursWorked.put(strPresentEmpIdNew, hmHoursWorked);
				hmOverLappingHolidays.put(strPresentEmpIdNew, dblOverLappingHolidays+"");
				
				strPresentEmpIdOld = strPresentEmpIdNew;
			}
			rs.close();
			pst.close();
			
//				System.out.println("strDate== vcvx="+hmPresentDays.get("118"));

			
			
			log.debug("hmHoursWorked===>"+hmEmpHoursWorked);
			
			
			
//			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
//			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
//			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
//			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
//			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
////			pst = con.prepareStatement("select * from salary_details order by weight");
//			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+"," + REIMBURSEMENT_CTC + ") and org_id =? " +
//					"and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
//			pst.setInt(1, uF.parseToInt(getF_org()));
////			System.out.println("pst======>"+pst);
//			rs = pst.executeQuery();  
//			
//			while(rs.next()) {
//				
//				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
//					int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
////					System.out.println("earning_deduction====>"+rs.getString("earning_deduction"));
////					System.out.println("alEmpSalaryDetailsEarning====>"+alEmpSalaryDetailsEarning);
////					System.out.println("alEarningSalaryDuplicationTracer====>"+alEarningSalaryDuplicationTracer);
////					System.out.println("index====>"+index);
////					System.out.println("salary_head_id====>"+rs.getString("salary_head_id"));
//					if(index>=0) {
//						alEmpSalaryDetailsEarning.remove(index);
//						alEarningSalaryDuplicationTracer.remove(index);
////						alEmpSalaryDetailsEarning.add(index, rs.getString("salary_head_id"));
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					} else {
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					}
//					
//					alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
//					int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//					if(index>=0) {
//						alEmpSalaryDetailsDeduction.remove(index);
//						alDeductionSalaryDuplicationTracer.remove(index);
////						alEmpSalaryDetailsDeduction.add(index, rs.getString("salary_head_id"));
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					} else {
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					}
//					alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}
//				
//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
//			}
//			rs.close();
//			pst.close();
			
			Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
			
			if(alGradeId.size() > 0) {
				String strGradeIds = StringUtils.join(alGradeId.toArray(), ",");
				
				pst = con.prepareStatement("select * from salary_details where grade_id in("+strGradeIds+") " +
						"and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by grade_id, earning_deduction desc, salary_head_id, weight");
				rs = pst.executeQuery(); 
				while (rs.next()) {
					
					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(rs.getString("grade_id"));
					if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
					
					Map<String, String> hmInnerSal = new HashMap<String, String>();
					hmInnerSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
					hmInnerSal.put("SALARY_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmInnerSal.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
					hmInnerSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					hmInnerSal.put("IS_CTC_VARIABLE", ""+uF.parseToBoolean(rs.getString("is_ctc_variable")));
					hmInnerSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmInnerSal.put("IS_ALIGN_WITH_PERK", ""+uF.parseToBoolean(rs.getString("is_align_with_perk")));
					hmInnerSal.put("IS_DEFAULT_CAL_ALLOWANCE", ""+uF.parseToBoolean(rs.getString("is_default_cal_allowance")));
					hmInnerSal.put("SALARY_TYPE", rs.getString("salary_type"));
					
					hmSalInner.put(rs.getString("salary_head_id"), hmInnerSal);
					
					hmSalaryDetails1.put(rs.getString("grade_id"), hmSalInner);
					
					if(uF.parseToInt(rs.getString("salary_head_id")) != GROSS && uF.parseToInt(rs.getString("salary_head_id")) != CTC && uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT_CTC) {
						if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
							int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
	
							if (index >= 0) {
								alEmpSalaryDetailsEarning.remove(index);
								alEarningSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							} else {
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}
	
							alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
							int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
							if (index >= 0) {
								alEmpSalaryDetailsDeduction.remove(index);
								alDeductionSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							} else {
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}
							alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						}
	
						hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					}
	
				}
				rs.close();
				pst.close();
			}
			
			
			Map<String, Map<String, String>> hmEmpSalary = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmEmpSalaryInner = new LinkedHashMap<String, String>();
//			pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			if(uF.parseToInt(getF_org())>0) {
//				pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation2);
				pst = con.prepareStatement("select * from emp_salary_details esd, (select max(effective_date) as effective_date, emp_id from emp_salary_details " +
						"where effective_date<=? and is_approved= true group by emp_id) a where a.effective_date = esd.effective_date and esd.emp_id = a.emp_id " +
						"and a.emp_id in (select emp_id from employee_official_details where org_id = ?) " +
						"and esd.salary_head_id in (select salary_head_id from salary_details where org_id =? and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false)) order by a.emp_id, esd.earning_deduction desc");
				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(getF_org()));
				pst.setInt(3, uF.parseToInt(getF_org()));
			} else {
//				pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation1);
				pst = con.prepareStatement("select * from emp_salary_details esd, ( select max(effective_date) as effective_date, emp_id " +
						"from emp_salary_details where effective_date<=? and is_approved= true group by emp_id ) a where a.effective_date = esd.effective_date " +
						"and esd.emp_id = a.emp_id and esd.salary_head_id in (select salary_head_id from salary_details where (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false))" +
						" order by a.emp_id, esd.earning_deduction desc");
				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));	
			}
			rs = pst.executeQuery();
//			System.out.println("----------_>"+pst);
			String strEmpIdNew1 = null;
			String strEmpIdOld1 = null;
			while(rs.next()) {
//				strEmpIdNew1 = rs.getString("empl_id");
				strEmpIdNew1 = rs.getString("emp_id");
						
				if(!alEmp.contains(strEmpIdNew1))continue;		
						
						
				if(strEmpIdNew1!=null && !strEmpIdNew1.equalsIgnoreCase(strEmpIdOld1)) {
					hmEmpSalaryInner = new LinkedHashMap<String, String>();
				}
				
//				Map<String, String> hmEmpInner = new HashMap<String, String>();
				
				/*
				hmEmpInner.put("SALARY_HEAD_ID", rs.getString("salary_id"));
				hmEmpInner.put("SERVICE_ID", rs.getString("service_id"));
				hmEmpInner.put("AMOUNT", rs.getString("amount"));
				hmEmpInner.put("PAY_TYPE", rs.getString("pay_type"));
				hmEmpInner.put("SALARY_HEAD_NAME", rs.getString("salary_head_name"));
				hmEmpInner.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
				hmEmpInner.put("SALARY_HEAD_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
				hmEmpInner.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
				hmEmpInner.put("SUB_SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
				
				hmEmpSalaryInner.put(rs.getString("salary_id"), hmEmpInner);
//				hmEmpPresentDays.put(strEmpIdNew1, nPresentDays+"");
				*/
				if(strEmpIdNew1!=null && strEmpIdNew1.length()>0) {
					hmEmpSalary.put(strEmpIdNew1, hmEmpSalaryInner);
				}
				
				strEmpIdOld1 = strEmpIdNew1;
//				if(!hmSalaryDetails.containsKey(rs.getString("salary_id"))) {
					//hmSalaryDetails.put(rs.getString("salary_id"), rs.getString("salary_head_name"));
//				}
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
			if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
			
			Map<String, String> hmInnerTemp = new HashMap<String, String>();
			
			Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF); 
			Map<String, String> hmIncentives = CF.getIncentivesMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualBonus = CF.getIndividualBonusMap(con, uF, CF, strPC, strD1, strD2);
			if(hmIndividualBonus == null) hmIndividualBonus = new HashMap<String, String>();
			
			Map<String, String> hmIndividualOtherDeduction = CF.getIndividualOtherDeductionMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualOtherEarning = CF.getIndividualOtherEarningMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualOvertime = CF.getIndividualOvertimeMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualTravelReimbursement = CF.getIndividualTravelReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualMobileReimbursement = CF.getIndividualMobileReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualOtherReimbursement = CF.getIndividualOtherReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualMobileRecovery = CF.getIndividualMobileRecoveryMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmReimbursement = CF.getReimbursementMap(con, uF, CF, strD1, strD2);
			Map<String, String> hmVariables  = new HashMap<String, String>();
			Map<String, String> hmBreaks  = new HashMap<String, String>();
			Map<String, String> hmBreakPolicy  = new HashMap<String, String>();
			getVariableAmount(con, uF, hmVariables, strPC);
			getBreakDetails(con, uF, hmBreaks, hmBreakPolicy, strD1, strD2);
			
			Map<String, String> hmAnnualVariables = new HashMap<String, String>();
			getAnnualVariableAmount(con, uF, hmAnnualVariables, strD1, strD2, strPC);
			
			Map<String, String> hmAllowance  = new HashMap<String, String>();
			getAllowanceAmount(con, uF, hmAllowance, strD1, strD2, strPC);
			
			Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
			Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
			
			getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
			
			Map<String, String> hmAnnualVarPolicyAmount = CF.getAnnualVariablePolicyAmount(con, uF, strFinancialYearStart, strFinancialYearEnd);
			if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();
			
			Map<String, String> hmBasicSalaryMap = CF.getSpecificSalaryData(con, BASIC);
			Map<String, String> hmDASalaryMap = CF.getSpecificSalaryData(con, DA);
			Set<String> setLeaves = null;
			Iterator<String> it = null;
			
 			List<String> alProcessingEmployee = new ArrayList<String>();
			
			LinkedHashMap<String, Map<String, String>> hmTotalSalary = new LinkedHashMap<String, Map<String, String>>();
			LinkedHashMap<String, Map<String, String>> hmTotalSalaryisDisplay = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmLoanAmt=new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
			List<String> alLoans = new ArrayList<String>();
			
			Map<String, String> hmWoHLeaves  = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			Set<String> set0 = hmEmpSalary.keySet();
			Iterator<String> it0 = set0.iterator();
			while(it0.hasNext()) {
				String strEmpId = it0.next();
				String strOrgId = hmEmpOrgId.get(strEmpId);
				int nEmpId = uF.parseToInt(strEmpId);
				String strLocation=hmEmpWlocationMap.get(strEmpId);
//				String strLevel=hmEmpLevelMap.get(strEmpId);
				String strGrade=hmEmpGradeMap.get(strEmpId);
				
				String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
				
//				Set<String> weeklyOffSet=hmWeekEnds.get(strLevel);
//				if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
				
				Set<String> weeklyOffSet = null;
				if(alEmpCheckRosterWeektype.contains(strEmpId)) {
					weeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
				} else {
					weeklyOffSet = hmWeekEnds.get(strLocation);
				}
				if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
				
				Set<String> OriginalholidaysSet=holidaysMp.get(strLocation);	
				if(OriginalholidaysSet==null)OriginalholidaysSet=new HashSet<String>();
//				} else {
				
				Set<String> holidaysSet=new HashSet<String>(OriginalholidaysSet);	
				
				holidaysSet.removeAll(weeklyOffSet);
				
				if(!alProcessingEmployee.add(strEmpId)) {
					alProcessingEmployee.add(strEmpId);
				}
				
//				System.out.println("hmPresentDays====>"+hmPresentDays);
				
				List<String> alPresentTemp = hmPresentDays.get(strEmpId);
				if(alPresentTemp==null)alPresentTemp = new ArrayList<String>();
				
				List<String> alServiceTemp = hmServices.get(strEmpId);
				if(alServiceTemp==null)alServiceTemp = new ArrayList<String>();
				
				double  dblPresent = alPresentTemp.size() - uF.parseToDouble(hmHolidayCount.get(strEmpId));
				
				List<String> alHalfDaysDueToLatePolicyTemp =hmHalfDays.get(strEmpId);
				if(alHalfDaysDueToLatePolicyTemp==null)alHalfDaysDueToLatePolicyTemp = new ArrayList<String>();
//				if(uF.parseToInt(strEmpId) == 11) {
//					System.out.println("dblPresent====="+dblPresent+"---alHalfDaysDueToLatePolicyTemp====="+alHalfDaysDueToLatePolicyTemp);
//				} 
				dblPresent -=alHalfDaysDueToLatePolicyTemp.size() * 0.5;
//				if(uF.parseToInt(strEmpId) == 11) {
//					System.out.println("after dblPresent====="+dblPresent);
//				} 
				List<String> alFullDaysDueToLatePolicyTemp = (List<String>)hmFullDays.get(strEmpId);
				if(alFullDaysDueToLatePolicyTemp==null)alFullDaysDueToLatePolicyTemp = new ArrayList<String>();
				
				dblPresent -=alFullDaysDueToLatePolicyTemp.size() * 1;
				
				Map<String,String> hmLeaves = hmLeaveDays.get(strEmpId);
				if(hmLeaves==null)hmLeaves = new HashMap<String,String>();
				
				Map<String,String> hmLeavesType = hmLeaveTypeDays.get(strEmpId);
				if(hmLeavesType==null)hmLeavesType = new HashMap<String,String>();
				
//				System.out.println("strEmpId= 123 ==>"+strEmpId);
//				
//				System.out.println("hmLeaves===>"+hmLeaves);
//				System.out.println("hmLeavesType===>"+hmLeavesType);

				EmployeeActivity obj = new EmployeeActivity();
				obj.request = request;
				obj.session = session;
				obj.CF = CF;
				
//				Map hmBasicSalaryMap = CF.getSpecificSalaryData(BASIC);
//				Map hmDASalaryMap = CF.getSpecificSalaryData(DA);
				
				setLeaves = hmLeaves.keySet();
				it = setLeaves.iterator();
//				int nLeaves = 0;
				double nOverlappingHolidaysLeaves = 0;
				double nOverlappingWeekEndsLeaves = 0;
				while(it.hasNext()) {
					String strLeaveDate = it.next();
//					String strHolidayDate = hmHolidayDates.get(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strLocation);
//					holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat()));
					String strWeekEnd = null;
					
					if( weeklyOffSet.contains(strLeaveDate)) {
						strWeekEnd = WEEKLYOFF_COLOR;
					}
					
					String strLeaveType = hmLeavesType.get(strLeaveDate);
					
//					System.out.println("strLeaveType===>"+strLeaveType);
//					System.out.println("strLeaveDate===>"+strLeaveDate);
//					System.out.println("hmLeavesType===>"+hmLeavesType);
//					System.out.println("alPresentTemp===>"+alPresentTemp);
					
//					if(strLeaveDate!=null && !strLeaveDate.equals(strHolidayDate) && !alPresentTemp.contains(strLeaveDate)) {
//						nLeaves++;
//					}
					
//					if(strLeaveDate!=null && strLeaveDate.equals(strHolidayDate)) {
//						nOverlappingHolidaysLeaves++;
//					}
					
//					if(strLeaveDate!=null && holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat())) && "H".equalsIgnoreCase(strLeaveType)) {
					if(strLeaveDate!=null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
						nOverlappingHolidaysLeaves+=0.5;
//					}else if(strLeaveDate!=null && holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat()))) {
					}else if(strLeaveDate!=null && holidaysSet.contains(strLeaveDate)) {	
						nOverlappingHolidaysLeaves++;
					}
					
					if(strLeaveDate!=null && strWeekEnd!=null) {
						nOverlappingWeekEndsLeaves++;
					}
					  
//					if(uF.parseToInt(strEmpId) == 11) {
//						System.out.println(strLeaveType+"---before leave dblPresent====="+dblPresent);
//					} 
					if(strLeaveDate!=null && alPresentTemp.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
//					if(strLeaveDate!=null && "H".equalsIgnoreCase(strLeaveType)) {
						//dblPresent += -1 + 0.5;
					}
//					if(uF.parseToInt(strEmpId) == 11) {
//						System.out.println(strLeaveType+"---after leave dblPresent====="+dblPresent);
//					} 
				}
				
				int nHolidays = holidaysSet.size();
//				int nWeekEnds = uF.getWeekEndCount(hmWeekEnds,strLocation, strD1, strD2);
//				int nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
				int nWeekEnds = 0;
/*
				if("459".equals(strEmpId)) {
					System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap);
					System.out.println("contains="+hmEmpJoiningMap.containsKey(strEmpId));
					System.out.println("Date 1="+uF.getDateFormatUtil(strD1, DATE_FORMAT));
					System.out.println("Date 2="+uF.getDateFormatUtil(strD2, DATE_FORMAT));
					System.out.println("Date 3="+uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT));
					System.out.println("isBetween="+uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT)));
				}*/
				
				if(hmEmpEndDateMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT))) {
					Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
					Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con, strD1,hmEmpEndDateMap.get(strEmpId), CF, uF,hmWeekEndHalfDates1,null);
					List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
					Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
					CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1,hmEmpEndDateMap.get(strEmpId),alEmpCheckRosterWeektype1,hmRosterWeekEndDates1,hmWeekEnds1,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates1);
					
//					Set<String> weeklyOffEndDate= hmWeekEnds1.get(strLevel);
					Set<String> weeklyOffEndDate = null;
					if(alEmpCheckRosterWeektype1.contains(strEmpId)) {
						weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
					} else {
						weeklyOffEndDate = hmWeekEnds1.get(strLocation);
					}
					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					
					nWeekEnds = weeklyOffEndDate.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("1---nWeekEnds==="+nWeekEnds);
//					}
					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
					
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con, uF,strD1, hmEmpEndDateMap.get(strEmpId));
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,strD1, strD2); 
					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,strD1, hmEmpEndDateMap.get(strEmpId)); 
					Set<String> OriginalholidaysSet1=holidaysMp1.get(strLocation);	
					if(OriginalholidaysSet1==null)OriginalholidaysSet1=new HashSet<String>();
					Set<String> holidaysSet1=new HashSet<String>(OriginalholidaysSet1);	
					holidaysSet1.removeAll(weeklyOffEndDate);
					
					nHolidays = holidaysSet1.size();
					
//					System.out.println("end date nWeekEnds====>"+nWeekEnds);
//					System.out.println("end date hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("end date strEmpId=====>"+strEmpId);
//					System.out.println("end date nHolidays=====>"+nHolidays);
					
				}else if(hmEmpJoiningMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT))) {
					Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
					Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con,hmEmpJoiningMap.get(strEmpId),strD2, CF, uF,hmWeekEndHalfDates1,null);
//					Set<String> weeklyOffEndDate= hmWeekEnds1.get(strLevel);
//					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
					Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
					CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, hmEmpJoiningMap.get(strEmpId),strD2,alEmpCheckRosterWeektype1,hmRosterWeekEndDates1,hmWeekEnds1,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates1);
					
					Set<String> weeklyOffEndDate = null;
					if(alEmpCheckRosterWeektype1.contains(strEmpId)) {
						weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
					} else {
						weeklyOffEndDate = hmWeekEnds1.get(strLocation);
					}
					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					
					
					nWeekEnds = weeklyOffEndDate.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("2---nWeekEnds==="+nWeekEnds+"---hmEmpJoiningMap.get(strEmpId)=====>"+hmEmpJoiningMap.get(strEmpId));
//						System.out.println("2---alEmpCheckRosterWeektype1.contains(strEmpId)==="+alEmpCheckRosterWeektype1.contains(strEmpId)+"---weeklyOffEndDate=====>"+weeklyOffEndDate);
//						System.out.println("2---hmRosterWeekEndDates1==="+hmRosterWeekEndDates1.get(strEmpId)+"---hmWeekEnds1.get(strLocation)=====>"+hmWeekEnds1.get(strLocation));
//					}
//					if("554".equals(strEmpId)) {
//						System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap.get(strEmpId));
//
//						System.out.println("nWeekEnds="+nWeekEnds);
//						System.out.println("weeklyOffEndDate="+weeklyOffEndDate);
//					}
					
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds1,strLocation, hmEmpJoiningMap.get(strEmpId), strD2);
					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con, hmEmpJoiningMap.get(strEmpId), strD2, CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
					
					
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con, uF,hmEmpJoiningMap.get(strEmpId), strD2);
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request, uF,strD1, strD2);
					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,hmEmpJoiningMap.get(strEmpId), strD2);
					Set<String> OriginalholidaysSet1=holidaysMp1.get(strLocation);	
					if(OriginalholidaysSet1==null)OriginalholidaysSet1=new HashSet<String>();
					Set<String> holidaysSet1=new HashSet<String>(OriginalholidaysSet1);	
					holidaysSet1.removeAll(weeklyOffEndDate);
					if("554".equals(strEmpId)) {
//						System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap.get(strEmpId));

//						System.out.println("holidaysSet1="+holidaysSet1);
//						System.out.println("weeklyOffEndDate="+weeklyOffEndDate);
					}
					nHolidays =holidaysSet1.size();
					
//					System.out.println("start date nWeekEnds====>"+nWeekEnds);
//					System.out.println("start date hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("start date strEmpId=====>"+strEmpId);
//					System.out.println("start date nHolidays=====>"+nHolidays);
					
				} else {
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					
					nWeekEnds=weeklyOffSet.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("3---nWeekEnds==="+nWeekEnds);
//					}
//					System.out.println("nWeekEnds====>"+nWeekEnds);
//					System.out.println("hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("strEmpId=====>"+strEmpId);
//					System.out.println("strD1=====>"+strD1);
//					System.out.println("strD2====>"+strD2);
					
					
//					hmHolidays = new HashMap<String, String>();
//					hmHolidayDates = new HashMap<String, String>();
//					CF.getHolidayList(strD1, strD2, CF, hmHolidayDates, hmHolidays, hmWeekEnds, true);
//					nHolidays = uF.parseToInt(hmHolidays.get(strLocation));
				}
				
				
				/*int nWeekEnds = 0;
				if(strEmpId.equalsIgnoreCase("460")) {
					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
				}*/
				
				List<String> alWorkingWeekEnds = hmPresentWeekEndDays.get(strEmpId);
				if(alWorkingWeekEnds==null)alWorkingWeekEnds=new ArrayList<String>();
				int nWorkingWeekEnds = alWorkingWeekEnds.size();
				
				List<String> alOverlappingWeekEndDates = hmPresentWeekEndDays.get(strEmpId);
				if(alOverlappingWeekEndDates==null)alOverlappingWeekEndDates = new ArrayList<String>();
//				int nOverlappingWeekends = alOverlappingWeekEndDates.size();
				
				double dblOverlappingHolidays = uF.parseToDouble(hmOverLappingHolidays.get(strEmpId));
				
				double dblTotalLeaves =  uF.parseToDouble(hmLeavesType.get("COUNT"));
				double dblActualLeaves =  dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//				System.out.println(strEmpId+"====dblTotalLeaves======>"+dblTotalLeaves);
//				System.out.println(strEmpId+"====dblActualLeaves======>"+dblActualLeaves);
//				System.out.println(strEmpId+"====nOverlappingHolidaysLeaves======>"+nOverlappingHolidaysLeaves);
//				System.out.println(strEmpId+"====nOverlappingWeekEndsLeaves======>"+nOverlappingWeekEndsLeaves);
				
				/**
				 * Long Leave code start
				 * */
				double dblLongLeave = uF.parseToDouble(hmLongLeaves.get(strEmpId));
//				if(strEmpId.equals("459")) {
//					System.out.println("dblLongLeave======>"+dblLongLeave);
//					System.out.println("before dblPresent======>"+dblPresent);
//					System.out.println("before dblTotalLeaves======>"+dblTotalLeaves);
//					System.out.println("before dblActualLeaves======>"+dblActualLeaves);
//				}
				dblTotalLeaves = dblTotalLeaves - dblLongLeave; 
				hmLeavesType.put("COUNT", ""+dblTotalLeaves);
//				if(strEmpId.equals("459")) {
//					System.out.println("after dblTotalLeaves======>"+dblTotalLeaves);
//				}
				/**
				 * Long Leave code end
				 * */
				
				
//				double dblTotalPgetEmpServiceTaxresentDays = dblPresent + dblActualLeaves + nHolidays + nWeekEnds;
				
//				double dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves ;
//				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves) +"");
				double dblTotalPresentDays = 0.0d;
				if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
					dblTotalPresentDays = 0.0d;
					hmPresentDays1.put(strEmpId, "0");
				} else {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves ;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves) +"");
				}
//				if(strEmpId.equals("225"))      {               
//					System.out.println("1 dblPresent==="+dblPresent+"---nHolidays==="+nHolidays+"---dblOverlappingHolidays==="+dblOverlappingHolidays+"---nWeekEnds==="+nWeekEnds+"---dblTotalLeaves==="+dblTotalLeaves+"---nOverlappingHolidaysLeaves==="+nOverlappingHolidaysLeaves+"---nOverlappingWeekEndsLeaves==="+nOverlappingWeekEndsLeaves);
//					System.out.println("1 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("1 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}

				if(dblTotalPresentDays>nTotalNumberOfDays) {
					dblTotalPresentDays = nTotalNumberOfDays;
					
				}
//				if(strEmpId.equals("225"))      {               
//					System.out.println("2 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("2 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}
				
				/*https://www.arclab.com/en/amlc/list-of-smtp-and-pop3-servers-mailserver-list.html
				
				

				// hard coded condition and logic needs to be implemented for this condition. --Vipin 21-01-2013
				if(dblPresent>=22) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds)+"");
				}else if(dblPresent>=20) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +4 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +4- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=14) {
					dblTotalPresentDays = dblPresent +3 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent  +3- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=8) {
					dblTotalPresentDays = dblPresent +2 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +2- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=4) {
					dblTotalPresentDays = dblPresent  +1 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +1- nOverlappingHolidaysLeaves)+"");
				} else {
					dblTotalPresentDays = dblPresent  + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent  - nOverlappingHolidaysLeaves)+"");
				} getEmpServiceTax
				*/
				
				// FOR KP
				 
				/*// hard coded condition and logic needs to be implemented for this condition. --Vipin 21-01-2013
				if(dblPresent>15) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=8) {
					dblTotalPresentDays = dblPresent +5 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +5- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=4) {
					dblTotalPresentDays = dblPresent  +1 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +1- nOverlappingHolidaysLeaves)+"");
				} else {
					dblTotalPresentDays = dblPresent  + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent - nOverlappingHolidaysLeaves )+"");
				}
				
				*/
				
				
				//comment for solar start 
				
//				Calendar calW = GregorianCalendar.getInstance();
//				Calendar calW1 = GregorianCalendar.getInstance();
//				Calendar calW2 = GregorianCalendar.getInstance();
//				calW.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//				calW.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
//				calW.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//			
//				int nDiff = uF.parseToInt(uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
//				int nDeductionW = 0;
//				List alPW = hmPresentDays.get(strEmpId);
//				if(alPW==null)alPW=new ArrayList();
//				
//				List alPWE = hmPresentWeekEndDays.get(strEmpId);
//				if(alPWE==null)alPWE=new ArrayList();
//				
//				
//*/				 
//				
//				
//				for(int i=0; i<nDiff; i++) {
//					String strDW = uF.getDateFormat(calW.get(Calendar.DATE)+"/"+(calW.get(Calendar.MONTH)+1)+"/"+calW.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//					String strDH = uF.getDateFormat(calW.get(Calendar.DATE)+"/"+(calW.get(Calendar.MONTH)+1)+"/"+calW.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//					calW1 = (Calendar)calW.clone();
//					  
//					
//					
//					
//					
//					if(hmWeekEnds.containsKey(strDW+"_"+strLocation) && i>0) {
//						  
//						calW1.add(Calendar.DATE, -1);
//						strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//						
//						if(!hmWeekEnds.containsKey(strDW+"_"+strLocation) && !alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//							
//							calW1.add(Calendar.DATE, 2);
//							strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//							
//							
//							if(hmWeekEnds.containsKey(strDW+"_"+strLocation)) {
//								
//								calW1.add(Calendar.DATE, 1);
//								strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//								
//								if(!alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//									nDeductionW+=2;
//									
//									
//								}
//								
//							} else {
//								
//								if(!alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//									nDeductionW+=1;
//									
//								}
//							}
//							
//						}
//						
//						
//					}
//					
//					
//					
//					
//				/*	
//					
//					
//					calW1 = (Calendar)calW.clone();
//					if(hmHolidayDates.containsKey(strDH+"_"+strLocation) && alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && i>0) {
//						calW1.add(Calendar.DATE, -1);
//						strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//						
//						if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))  && !hmLeaves.containsKey(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && !hmHolidayDates.containsKey(strDH+"_"+strLocation) && !hmWeekEnds.containsKey(strDH+"_"+strLocation)) {
//							calW1.add(Calendar.DATE, 1);
//							strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//							if(hmHolidayDates!=null && hmHolidayDates.containsKey(strDH+"_"+strLocation) && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))) {
//								nDeductionW+=1;
//								
//								
//							}
//						}else if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && !hmLeaves.containsKey(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && hmHolidayDates!=null && hmHolidayDates.containsKey(strDH+"_"+strLocation)) {
//							calW1.add(Calendar.DATE, -1);
//							strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//							if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))) {
//								nDeductionW+=1;
//							  
//								
//							}  
//						}
//					}
//					*/
//					
//					
//					
//					
//					
//					calW2 = (Calendar)calW.clone();
//					strDH = uF.getDateFormat(calW2.get(Calendar.DATE)+"/"+(calW2.get(Calendar.MONTH)+1)+"/"+calW2.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//					
////					if(hmLeaves.containsKey(strDH) && hmWeekEnds.containsKey(strDH+"_"+strLocation)) {
////						dblOverlappingHolidays++;
////						/*if(strEmpId.equalsIgnoreCase("652")) {
////							System.out.println("strDH= 5 ="+strDH);
////						}*/
////					}
//					
//					
//					
//					calW.add(Calendar.DATE, 1);
//				}
//				  
//		
//				if(dblPresent>0 || dblActualLeaves>0) {
////					if(dblPresent>0 && (dblPresent+dblActualLeaves)<6) {
//					if(dblPresent>0 && (dblPresent+dblTotalLeaves)<6) {
//						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + dblActualLeaves +nWeekEnds;
//						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nOverlappingHolidaysLeaves +nWeekEnds)+"");
//						
//						dblTotalPresentDays = dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves + dblTotalLeaves - nDeductionW ;
//						hmPresentDays1.put(strEmpId, (dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves - nDeductionW)+"");
//						
//					}else if((dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves)>nDeductionW) {
////						dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves - nDeductionW ;
////						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds- nOverlappingHolidaysLeaves - nDeductionW)+"");
//						
//						dblTotalPresentDays = dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves + dblTotalLeaves - nDeductionW ;
//						hmPresentDays1.put(strEmpId, (dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves - nDeductionW)+"");
//					} else {
//						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + dblActualLeaves ;
//						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nOverlappingHolidaysLeaves )+"");
//					}
//					
//				} else {
//					dblTotalPresentDays = 0 ;
//					hmPresentDays1.put(strEmpId, 0+"");
//				}
//				
//				
//				
//				
//				
//				
//				
//				
//				// Exclusive condition for KPCA urgent basis
//				/**
//				 * Exclusive condition for KPCA urgent basis
//				 * Needs to be replaced with the sandwich leave logic
//				 * @author Vipin Razdan (28-Nov-2013)
//				 * 
//				 * */
//
				
				//comment for solar end
			
					  			
				
				/*
				if(true) { // if daily calculation employees
					dblTotalPresentDays = dblPresent + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
				}
				*/
				// For ANC all daily employees are calculated Overtime differently
				if(hmEmpPaycycleDuration.get(strEmpId)!=null && !hmEmpPaycycleDuration.get(strEmpId).equalsIgnoreCase("M")) {
//					dblTotalPresentDays = dblPresent + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
					if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
						dblTotalPresentDays = 0.0d;
						hmPresentDays1.put(strEmpId, "0");
					} else {
						dblTotalPresentDays = dblPresent + dblActualLeaves;
						hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
					}
				} 
//				if(strEmpId.equals("225"))      {               
//					System.out.println("3 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("3 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}
				
				if(uF.parseToBoolean(CF.getIsPaycycleAdjustment())) {
					Calendar calMonth1 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
					calMonth1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
					calMonth1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
					calMonth1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
					
					Calendar calMonth2 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
					calMonth2.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
					calMonth2.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
					calMonth2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
					
					int nMonth1 = calMonth1.getActualMaximum(Calendar.DATE);
					int nMonth2 = calMonth2.getActualMaximum(Calendar.DATE); 
					
					dblPresent += (nMonth2 - nMonth1);
					
					
//					dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves)+"");
					if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
						dblTotalPresentDays = 0.0d;
						hmPresentDays1.put(strEmpId, "0");
					} else {
						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves)+"");
					}
//					if(strEmpId.equals("225")) {               
//						System.out.println("4 dblTotalPresentDays==="+dblTotalPresentDays);
//						System.out.println("4 nTotalNumberOfDays==="+nTotalNumberOfDays);
//					}
				}
				   
			
				
				int nTotalNumberOfDaysForCalc = nTotalNumberOfDays; 
				
				/**   AWD  = Actual Working Days
				 * */
				
//				if("AWD".equalsIgnoreCase(CF.getStrOSalaryCalculationType())) {
				if("AWD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
//					dblTotalPresentDays = dblPresent + dblTotalLeaves;
					
					if(dblPresent>0) {
//						dblPresent = dblPresent + nHolidays ;
						dblPresent = dblPresent;
//					} else {
//						dblPresent = dblPresent ;
					}
					
					/**actual paid leaves
					 * */
					double dblWoHLwaves = dblTotalLeaves;
					hmWoHLeaves.put(strEmpId, ""+dblWoHLwaves);
					
					dblTotalLeaves = (dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves); 
					hmLeavesType.put("COUNT", ""+dblTotalLeaves);
					
					
					
					//dblPresent = (dblPresent - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves);
					
					dblTotalPresentDays = dblPresent + dblTotalLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent)+"");
					
//					strTotalDays = (nTotalNumberOfDays - nHolidays - nWeekEnds)+"";
//					System.out.println("hmLeaveTypeDays==="+hmLeavesType);
//					System.out.println("dblTotalLeaves==="+dblTotalLeaves+" dblActualLeaves="+dblActualLeaves+" nOverlappingHolidaysLeaves==="+nOverlappingHolidaysLeaves);
					
//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nHolidays - nWeekEnds; 

//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nWeekEnds;
//					int nWeekEnds1 = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					int nWeekEnds1 = weeklyOffSet.size();

//					System.out.println("strEmpId==="+strEmpId);
//					System.out.println("nTotalNumberOfDays==="+nTotalNumberOfDays);
//					System.out.println("nWeekEnds1==="+nWeekEnds1);
//					System.out.println("nHolidays==="+nHolidays);
					
//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nWeekEnds1; 
					nTotalNumberOfDaysForCalc = (nTotalNumberOfDays - nWeekEnds1) - nHolidays;
					
//					System.out.println("nWeekEnds1====>"+nWeekEnds1);
//					System.out.println("nWeekEnds====>"+nWeekEnds);
//					System.out.println("strEmpId====>"+strEmpId);
//					System.out.println("nTotalNumberOfDays====>"+nTotalNumberOfDays);
//					System.out.println("nTotalNumberOfDaysForCalc=====>"+nTotalNumberOfDaysForCalc);
					
					
					strTotalDays = nTotalNumberOfDaysForCalc +"";
					
					
//				}else if("AFD".equalsIgnoreCase(CF.getStrOSalaryCalculationType())) {
				}else if("AFD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
					if(dblPresent>0) {
						dblPresent = dblPresent + nHolidays ;
//					} else {
//						dblPresent = dblPresent ;
					}
//					nTotalNumberOfDaysForCalc = uF.parseToInt(CF.getStrOSalaryCalculationDays());
//					strTotalDays = CF.getStrOSalaryCalculationDays();
					nTotalNumberOfDaysForCalc = uF.parseToInt(hmOrg.get("ORG_SALARY_FIX_DAYS"));
					strTotalDays = uF.showData(hmOrg.get("ORG_SALARY_FIX_DAYS"), "");
					
//					int nWeekEnds1 = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					int daysDiff = nTotalNumberOfDays - nTotalNumberOfDaysForCalc; 	
					dblTotalPresentDays=dblTotalPresentDays-daysDiff;
					
//					if(strEmpId.equals("65")) {
//					System.out.println("daysDiff==="+daysDiff);
//					System.out.println("dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("dblPresent==="+dblPresent);
//					}
					hmPresentDays1.put(strEmpId,(uF.parseToDouble(hmPresentDays1.get(strEmpId))-daysDiff)+"");
//					hmPresentDays1.put(strEmpId, (dblPresent-daysDiff)+"");
				}
				
				hmTotalDays.put(strEmpId, strTotalDays);
//				System.out.println("dblTotalLeaves=>"+dblTotalLeaves);
//				System.out.println("nOverlappingHolidaysLeaves=>"+nOverlappingHolidaysLeaves);
//				System.out.println("nPresent=>"+dblPresent);
//				System.out.println("dblActualLeaves=>"+dblActualLeaves);
//				System.out.println("nHolidays=>"+nHolidays);
//				System.out.println("nWeekEnds=>"+nWeekEnds);
//				System.out.println("dblTotalPresentDays=>"+dblTotalPresentDays);
				
//				log.debug(" strEmpId= 123 =>"+strEmpId);
//				log.debug(" nHolidays==>"+nHolidays);
//				log.debug(" hmHolidays==>"+hmHolidays);
//				log.debug(" strLocation==>"+strLocation);
				
				
				
				/**
				 * 
				 * Prem Motors Break Policies
				 * 
				 * */
				
				int nBreaks = uF.parseToInt(hmBreaks.get(strEmpId+"_-2"));
//				double dblAmt = uF.parseToDouble(hmBreakPolicy.get("-2_"+getF_org()));
//				dblTotalPresentDays -= (nBreaks * dblAmt); 
//				if(nBreaks>0) {
//					hmPresentDays1.put(strEmpId, (dblTotalPresentDays)+"");
//				}
				
				
				
				
				
				/**
				 *   The attendance dependency calculation is for those employees who are not 
				 *   attendance dependent and will get the full salary irrespective they clocking on.
				 */
				
				boolean isAttendance = uF.parseToBoolean(hmAttendanceDependent.get(strEmpId));
				if(!isAttendance) {
//					dblTotalPresentDays = nTotalNumberOfDays;
					dblTotalPresentDays = nTotalNumberOfDaysForCalc;
//					if(strEmpId.equals("65")) {
//						System.out.println("dblTotalPresentDays"+dblTotalPresentDays);
//					}
//					if(strEmpId.equals("225")) {               
//						System.out.println("5 dblTotalPresentDays==="+dblTotalPresentDays);
//						System.out.println("5 nTotalNumberOfDays==="+nTotalNumberOfDays);
//					}
				}
				
				hmPaidDays.put(strEmpId, dblTotalPresentDays+"");
				
//				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays+nWeekEnds)+"");
				
//				double dblIncrement = 0;
				
				Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strGrade);
				if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>();
				
				double dblIncrementBasic = getIncrementCalculationBasic(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
				double dblIncrementDA = getIncrementCalculationDA(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
				
				String strLevel = hmEmpLevelMap.get(strEmpId);
				int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
				
				Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				
				if((dblIncrementBasic>0 || dblIncrementDA>0) && getApprovePC()!=null) {
					hmInner = CF.getSalaryCalculationByGrade(con,hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strGrade, uF, CF, strD2,hmSalInner, "0.0d", hmEmpSalCalStatus.get(strEmpId));
				} else {  
					hmInner = CF.getSalaryCalculationByGrade(con,hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strGrade, uF, CF, strD2,hmSalInner, "0.0d", hmEmpSalCalStatus.get(strEmpId));
				}
				
//				Map<String, Map<String,String>> hmInnerActualCTC = getSalaryCalculationByGrade(con,hmInnerisDisplay, nEmpId, nTotalNumberOfDaysForCalc, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strGrade, uF, CF, strD2);
				
				Map<String, String> hmPaidSalaryInner = hmEmpPaidSalary.get(strEmpId);
				
				Map<String, String> hmPerkAlignAmount = new HashMap<String, String>();
				CF.getPerkAlignAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel), hmPerkAlignAmount);
				
				double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
				double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
				
				hmHoursWorked = hmEmpHoursWorked.get(strEmpId);
				if(hmHoursWorked==null)hmHoursWorked = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
				
				
				if(hmIndividualOtherEarning.size()>0 && !hmInner.containsKey(OTHER_EARNING+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(OTHER_EARNING+"", hmInnerTemp);
				}
				
				if(hmIndividualOtherReimbursement.size()>0 && !hmInner.containsKey(OTHER_REIMBURSEMENT+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(OTHER_REIMBURSEMENT+"", hmInnerTemp);
				}
//				if(hmEmpServiceTaxMap.size()>0 && !hmInner.containsKey(SERVICE_TAX+"")) {
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(SERVICE_TAX+"", hmInnerTemp);
//					
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(SWACHHA_BHARAT_CESS+"", hmInnerTemp);
//					
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(KRISHI_KALYAN_CESS+"", hmInnerTemp);
//				}
				
				if (hmEmpServiceTaxMap.size() > 0 && !hmInner.containsKey(CGST + "")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(CGST + "", hmInnerTemp);

					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(SGST + "", hmInnerTemp);
				}
				
//				if(hmIndividualOtherDeduction.size()>0 && !hmInner.containsKey(OTHER_DEDUCTION+"")) {
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "D");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(OTHER_DEDUCTION+"", hmInnerTemp);
//				}
				
				if(hmInner.size()>0 && hmInner.containsKey(TDS+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp=hmInner.get(TDS+"");
					hmInnerTemp.put("AMOUNT", hmInnerTemp.get("AMOUNT"));
					hmInnerTemp.put("EARNING_DEDUCTION", "D");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.remove(TDS+"");
					hmInner.put(TDS+"", hmInnerTemp);
				}
				
				
				
				
				
				
//				System.out.println("hmPresentDays1====>"+hmPresentDays1);
//				System.out.println("nTotalPresentDays====>"+dblTotalPresentDays);
				
//				System.out.println("hmInner==>"+hmInner);
//				System.out.println("nTotalNumberOfDaysForCalc==>"+nTotalNumberOfDaysForCalc);
				
//				Map hm=new HashMap();
				Map<String, String> hmTotal=new HashMap<String, String>();
				
				
				Set<String> set1 = hmInner.keySet();
				Iterator<String> it1 = set1.iterator();
				
				double dblGrossPT = 0;
				double dblGross = 0;
				double dblGrossTDS = 0;
				double dblDeduction = 0;
//				boolean isDefinedEarningDeduction = false; 
				Set<String> setContriSalHead = new HashSet<String>();
				
				while(it1.hasNext()) {
					String strSalaryId = it1.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					String strMulCal = hm.get("MULTIPLE_CALCULATION");
					List<String> al = new ArrayList<String>();
					if(strMulCal != null && !strMulCal.equals("")) {
						al = Arrays.asList(strMulCal.trim().split(","));
					}
					if(al != null && al.contains(""+EMPLOYER_EPF)) {
						setContriSalHead.add(""+EMPLOYER_EPF);
					}
					if(al != null && al.contains(""+EMPLOYER_ESI)) {
						setContriSalHead.add(""+EMPLOYER_ESI);
					}
					if(al != null && al.contains(""+EMPLOYER_LWF)) {
						setContriSalHead.add(""+EMPLOYER_LWF);
					}
					
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							
							/*if(strEmpId.equals("459")) {
								System.out.println(nSalayHead+"  aa hmTotal=====>"+hmTotal);
							}*/
							
							switch(nSalayHead) {
							/**********  OVER TIME   *************/
								case OVER_TIME:  
								 
//									isDefinedEarningDeduction = true;
//									double dblOverTime = getOverTimeCalculationL(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC, hmOverTimeMap, hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime);
//									double dblOverTime = getOverTimeCalculationHours(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC,  hmEmpLevelMap, dblStandardHrs,  nTotalNumberOfDaysForCalc, hmIndividualOvertime,hmEmpOverTimeHours,hmEmpOverTimeLevelPolicy, hmEmpRosterHours, hmWlocationMap, nTotalNumberOfDays, nWeekEnds,nHolidays,dblPresent,dblTotalPresentDays,holidaysSet,weeklyOffSet,hmEmpWlocationMap);
									double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
									
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
//									dblOverTime = Math.round(dblOverTime);
									dblGross += dblOverTime;
									dblGrossTDS += dblOverTime;
									
								break;
								
								case LEAVE_ENCASHMENT:  
//									Map<String, String> basicHead=hmInner.get(BASIC+"");
//									double leaveEncashmentAmt = getLeaveEncashmentAmount(uF,hmLeaveEncashment.get(strEmpId),dblTotalPresentDays,basicHead.get("AMOUNT"));
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(leaveEncashmentAmt)));
									
//									double leaveEncashmentAmt = getLeaveEncashmentAmtDetails(con, uF, CF, strEmpId, uF.parseToDouble(hmLeaveEncashment.get(strEmpId)), nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, dblIncrementBasic, dblIncrementDA);
									double leaveEncashmentAmt = 0.0d; //getLeaveEncashmentAmtDetailsByGrade(con, uF, CF, strEmpId, 0.0d, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, hmEmpLevelMap.get(strEmpId), strGrade, dblIncrementBasic, dblIncrementDA);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),leaveEncashmentAmt));
								
									System.out.println("AP/1921--leaveEncashmentAmt="+leaveEncashmentAmt);
									dblGross += leaveEncashmentAmt;
									dblGrossTDS += leaveEncashmentAmt;
									
								break;
								
								case BONUS:
									/*// Bonus is paid independent of paycycle -- 
									
									if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())) {
//										isDefinedEarningDeduction = true;
										double dblBonusAmount = getBonusCalculation(con, uF, strEmpId,strD2, hmEmpLevelMap, hmInnerActualCTC, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap, CF, hmIndividualBonus);
										hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblBonusAmount)));
//										dblBonusAmount = Math.round(dblBonusAmount);
										dblGross += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;
									}*/
									double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
//									dblBonusAmount = Math.round(dblBonusAmount);
									dblGross += dblBonusAmount;
									dblGrossTDS += dblBonusAmount;
									
								break;
								
								case EXGRATIA:
									
									double dblExGratiaAmount = getExGratiaAmount(con,uF,CF,strEmpId,strD1,strD2,strPC);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
									dblGross += dblExGratiaAmount;
									dblGrossTDS += dblExGratiaAmount;
									
								break;
								
								case AREARS:

//									isDefinedEarningDeduction = true;
									double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
//									dblArearAmount = Math.round(dblArearAmount);
									dblGross += dblArearAmount;
									dblGrossTDS += dblArearAmount;
									
								break; 
								
								case INCENTIVES:
									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
//									dblIncentiveAmount += uF.parseToDouble(hm.get("AMOUNT"));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
									dblGross += dblIncentiveAmount;
									dblGrossTDS += dblIncentiveAmount;
								break;
								
								case REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
//									dblReimbursementAmount = Math.round(dblReimbursementAmount);
									dblGross += dblReimbursementAmount;
								break;
								
								case TRAVEL_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
//									dblTravelReimbursementAmount = Math.round(dblTravelReimbursementAmount);
									dblGross += dblTravelReimbursementAmount;
								break;
								
								case MOBILE_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
//									dblMobileReimbursementAmount = Math.round(dblMobileReimbursementAmount);
									dblGross += dblMobileReimbursementAmount;
								break;
								
								case OTHER_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
//									dblOtherReimbursementAmount = Math.round(dblOtherReimbursementAmount);
									dblGross += dblOtherReimbursementAmount;
								break;
								
								case OTHER_EARNING:
//									isDefinedEarningDeduction = true;
									double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
//									dblOtherEarningAmount = Math.round(dblOtherEarningAmount);
									dblGross += dblOtherEarningAmount;
								break;
								
								case SERVICE_TAX:
									
//									isDefinedEarningDeduction = true;
									double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
//									dblServiceTaxAmount = Math.round(dblServiceTaxAmount);

									/**
									 * @author Vipin
									 * 25-Mar-2014
									 * KP Condition
									 * @comment = service tax is not included while calculating TDS
									 * */
									
									dblGross += dblServiceTaxAmount;  
									dblGrossPT += dblServiceTaxAmount;
									dblGrossTDS += dblServiceTaxAmount;  
									  
									break;	
								
								case SWACHHA_BHARAT_CESS:
									double dblGrossAmt = dblGross;
									double dblServiceTaxAmt = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
									double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
									dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
									
									double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));
									
									dblGross += dblSwachhaBharatCess;  
									dblGrossPT += dblSwachhaBharatCess;
									dblGrossTDS += dblSwachhaBharatCess;  
									  
									break;
								
								case KRISHI_KALYAN_CESS:
									double dblGrossAmt1 = dblGross;
									double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
									double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
									dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
									
									double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
									
									dblGross += dblKrishiKalyanCess;  
									dblGrossPT += dblKrishiKalyanCess;
									dblGrossTDS += dblKrishiKalyanCess;  
									  
									break;	
									
								case CGST :
									double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									break;
								
								case SGST :
									double dblGrossAmt2 = dblGross;
									double dblCGSTAmt = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossAmt2 = dblGrossAmt2 - dblCGSTAmt;
									
									double dblSGSTAmount = calculateSGST(con, uF, strEmpId, dblGrossAmt2, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									break;	
								
								default:
									
									if(uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
										double dblPerkAlignAmount = 0.0d;
										if(hmPerkAlignAmount.containsKey(strSalaryId)) {
											dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
										}
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount)); 
										dblGross += dblPerkAlignAmount;
										dblGrossPT += dblPerkAlignAmount;
										dblGrossTDS += dblPerkAlignAmount;	
										
									} else if (!uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
										dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
									} else if (uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
										if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										}
									} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"))));
										dblGross += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
									} else if(uF.parseToInt(strSalaryId)!=GROSS) {
										boolean isMultipePerWithParticularHead = false;
										if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
											isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional);
										}
										if(!isMultipePerWithParticularHead) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT")))+""); 
											dblGross += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										}
										
									}
									/*if(strEmpId.equals("459")) {
										System.out.println("hmTotal=====>"+hmTotal);
									}  */ 
									
								break;
							}
							
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						
						/**********  EPF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								Map<String,String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF+"");
								
								double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblEEPF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
							}
							
							break;
							
						/**********  EPF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblERPF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
							}
							 
							break;
							
						case LOAN:
							
//							System.out.println("Calculating Loan===>");
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {   
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
								if(strEmpId.equalsIgnoreCase("686")) {
//									System.out.println(strEmpId+" paid dblLoan==>=>"+dblLoan);
//									System.out.println(strEmpId+"  1 dblGrossTDS==>=>"+dblGrossTDS);
//									System.out.println(strEmpId+"  1 paid loan==>=>"+hmTotal.get(strSalaryId));    
								}
								
								CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2,strEmpId);
							} else {
								
								double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
								dblDeduction += dblLoanAmt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
								
//								if(strEmpId.equalsIgnoreCase("683")) {
//									System.out.println(strEmpId+"  dblLoanAmt==>=>"+dblLoanAmt);
//									System.out.println(strEmpId+"  1 dblGrossTDS==>=>"+dblGrossTDS);
//									System.out.println(strEmpId+"  1 loan==>=>"+hmTotal.get(strSalaryId));    
//								}
								           
								if(true) {
//									dblGrossTDS = dblGross - dblLoanAmt;
									dblGrossTDS = dblGrossTDS - dblLoanAmt; 
								}
							}
							
							break;   
							
						case MOBILE_RECOVERY:
							
//							isDefinedEarningDeduction = true; 
							if(hmPaidSalaryInner!=null) {
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
							} else {
								double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
								dblDeduction += dblIndividualMobileRecoveryAmt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
							}
							
							break;		
							
						default:
							if(hmPaidSalaryInner!=null) {
								dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								
								/*if(nSalayHead==VOLUNTARY_EPF) {
									continue;
								}*/
								
								if(hmAllowance.containsKey(strEmpId+"_"+strSalaryId)) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId))));
									dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId));
								} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_D")) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"))));
									dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
								} else if(uF.parseToInt(strSalaryId)!=PROFESSIONAL_TAX && uF.parseToInt(strSalaryId)!=TDS) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
									dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));	
								}	
								
								
							}
							
							
							break;
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				
				
				/**
				 * Multiple cal start
				 * */
				Map<String, String> hmContriSalHeadAmt = new HashMap<String, String>();
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
//					double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
					double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap);
					hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
//					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
					hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
					double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
					hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
				}
				Iterator<String> itMulti = hmInner.keySet().iterator();
				while(itMulti.hasNext()) {
					String strSalaryId = itMulti.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblMulCalAmt));
							dblGross += dblMulCalAmt;
							dblGrossTDS += dblMulCalAmt;
						}
						
					} else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if(hmPaidSalaryInner!=null) {
							dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblDeduction += dblMulCalAmt;
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblGross));
				}
				/**
				 * Multiple cal end 
				 * */
				
				/**
				 * Other cal start
				 * */
				Iterator<String> itOther = hmInner.keySet().iterator();
				while(itOther.hasNext()) {
					String strSalaryId = itOther.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						/**********  	 TAX   *************/
						case PROFESSIONAL_TAX:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								/**
								 * KP Condition
								 * 
								 * */      
								
//								double dblPt = calculateProfessionalTax(con, uF,strD2, dblGrossPT, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId));
							 	double dblPt = calculateProfessionalTax(con, uF,strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
								dblDeduction +=dblPt;

								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							}
							
							break;
						
						/**********  TDS   *************/
						case TDS:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblTDS;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblTDS));
							} else {
								
								
//								double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//								double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//								double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
//								
//								String[] hraSalaryHeads = null;
//								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
//									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//								}
//								
//								double dblHraSalHeadsAmount = 0;
//								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
//									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
//								}
//								
//								Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
//								if(hmPaidSalaryDetails==null) {hmPaidSalaryDetails=new HashMap<String, String>();}
//								
//								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
								
								if(strEmpId.equalsIgnoreCase("686")) {
//									System.out.println(strEmpId+" before dblGrossTDS==>=>"+dblGrossTDS);
								}   
								 
								if(hmEmpServiceTaxMap.containsKey(strEmpId)) {
									dblGrossTDS = dblGross;
									
//									double dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
//									dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
//									
//									double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
//									
//									double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
									
									double dblCGST = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossTDS = dblGrossTDS - dblCGST;

									double dblSGST = uF.parseToDouble(hmTotal.get(SGST + ""));
									dblGrossTDS = dblGrossTDS - dblSGST;
									
								}
								
								/**
								 * (dblBasic + dblDA) we use dblHraSalHeadsAmount
								 * */
//								double dblTDS = calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//										nPayMonth,
//										strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, hmEmpGenderMap.get(strEmpId),  hmEmpAgeMap.get(strEmpId), hmEmpStateMap.get(strEmpId),
//										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//										hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
								double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
										strFinancialYearEnd, strEmpId, hmEmpLevelMap);
								
								dblDeduction += dblTDS;
																
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							}
							break;
							
							/**********  ESI EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYER CONTRIBUTION   *************/
							
							/**********  ESI EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction +=dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYEE CONTRIBUTION   *************/	
							
							/**********  LWF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							
							/**********  /LWF EMPLOYER CONTRIBUTION   *************/
							
							/**********  LWF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							/**********  /LWF EMPLOYEE CONTRIBUTION   *************/	
						
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				/**
				 * Other cal end 
				 * */
				
				
				String strCurrencyId = hmEmpCurrency.get(strEmpId);
				Map<String,String> hmCurrency = hmCurrencyDetails.get(strCurrencyId);
				if(hmCurrency==null)hmCurrency = new HashMap<String,String>();
				
				
//				if(strEmpId.equals("230") || strEmpId.equals("239")) {  
//					System.out.println(strEmpId+"----aa hmTotal=====>"+hmTotal+"----dblDeduction=====>"+dblDeduction+"-----dblGross=====>"+dblGross);
//				}				
				
				hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction))); 
				
				 
				hmTotalSalary.put(strEmpId, hmTotal);
				
				
				
				////=========================code for isdisplay false=======================

//				Map hmisDisplay=new HashMap();
				Map<String, String> hmTotalisDisplay=new HashMap<String, String>();
				Set<String> set2 = hmInnerisDisplay.keySet();
				Iterator<String> it2 = set2.iterator();
				dblGross = 0;
				dblGrossTDS = 0;
				dblDeduction = 0;
//				isDefinedEarningDeduction = false; 
				while(it2.hasNext()) {
					String strSalaryId = it2.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
					
//					isDefinedEarningDeduction = false;
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							/*if(strEmpId.equals("459")) {
								System.out.println(nSalayHead+"  aa hmTotal=====>"+hmTotal);
							}*/
							
							switch(nSalayHead) {
							/**********  OVER TIME   *************/
								case OVER_TIME:  
								 
//									isDefinedEarningDeduction = true;
//									double dblOverTime = getOverTimeCalculationL(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC, hmOverTimeMap, hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime);
//									double dblOverTime = getOverTimeCalculationHours(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInnerisDisplay, hmInnerActualCTC,  hmEmpLevelMap, dblStandardHrs,  nTotalNumberOfDaysForCalc, hmIndividualOvertime,hmEmpOverTimeHours,hmEmpOverTimeLevelPolicy, hmEmpRosterHours, hmWlocationMap, nTotalNumberOfDays, nWeekEnds,nHolidays,dblPresent,dblTotalPresentDays,holidaysSet,weeklyOffSet,hmEmpWlocationMap);
									double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
//									dblOverTime = Math.round(dblOverTime);
									dblGross += dblOverTime;
									dblGrossTDS += dblOverTime;
									
								break;
								
								case BONUS:
									
									/*if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())) {
//										isDefinedEarningDeduction = true;
										double dblBonusAmount = getBonusCalculation(con, uF, strEmpId,strD2, hmEmpLevelMap, hmInnerActualCTC, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap, CF, hmIndividualBonus);
										hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblBonusAmount)));
//										dblBonusAmount = Math.round(dblBonusAmount);
										dblGross += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;
									}*/
									double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
//									dblBonusAmount = Math.round(dblBonusAmount);
									dblGross += dblBonusAmount;
									dblGrossTDS += dblBonusAmount;
									
								break;
								
								case EXGRATIA:
									
									double dblExGratiaAmount = getExGratiaAmount(con,uF,CF,strEmpId,strD1,strD2,strPC);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
									dblGross += dblExGratiaAmount;
									dblGrossTDS += dblExGratiaAmount;
									
								break;
								
								case AREARS:

//									isDefinedEarningDeduction = true;
									double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
//									dblArearAmount = Math.round(dblArearAmount);
									dblGross += dblArearAmount;
									dblGrossTDS += dblArearAmount;
									
								break;
								
								case INCENTIVES:
//									isDefinedEarningDeduction = true;
//									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
//									dblIncentiveAmount += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
//									hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIncentiveAmount)));
////									dblIncentiveAmount = Math.round(dblIncentiveAmount);
//									dblGross += dblIncentiveAmount;
//									dblGrossTDS += dblIncentiveAmount;
									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
									dblGross += dblIncentiveAmount;
									dblGrossTDS += dblIncentiveAmount;
									
								break;
								
								case REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
//									dblReimbursementAmount = Math.round(dblReimbursementAmount);
									dblGross += dblReimbursementAmount;
								break;
								
								case TRAVEL_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
//									dblTravelReimbursementAmount = Math.round(dblTravelReimbursementAmount);
									dblGross += dblTravelReimbursementAmount;
								break;
								
								case MOBILE_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
//									dblMobileReimbursementAmount = Math.round(dblMobileReimbursementAmount);
									dblGross += dblMobileReimbursementAmount;
								break;
								
								case OTHER_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
//									dblOtherReimbursementAmount = Math.round(dblOtherReimbursementAmount);
									dblGross += dblOtherReimbursementAmount;
								break;
								
								case OTHER_EARNING:
//									isDefinedEarningDeduction = true;
									double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
//									dblOtherEarningAmount = Math.round(dblOtherEarningAmount);
									dblGross += dblOtherEarningAmount;
								break;
								
								case SERVICE_TAX:
									
//									isDefinedEarningDeduction = true;
									double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
//									dblServiceTaxAmount = Math.round(dblServiceTaxAmount);

									/**
									 * @author Vipin
									 * 25-Mar-2014
									 * KP Condition
									 * @comment = service tax is not included while calculating TDS
									 * */
									
									dblGross += dblServiceTaxAmount;  
									dblGrossPT += dblServiceTaxAmount;
									dblGrossTDS += dblServiceTaxAmount;  
									  
									break;	
								
								case SWACHHA_BHARAT_CESS:
									double dblGrossAmt = dblGross;
									double dblServiceTaxAmt = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX+""));
									dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
									double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
									dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
									
									double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId), hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));

									dblGross += dblSwachhaBharatCess;  
									dblGrossPT += dblSwachhaBharatCess;
									dblGrossTDS += dblSwachhaBharatCess;  
									  
									break;	
								
								case KRISHI_KALYAN_CESS:
									double dblGrossAmt1 = dblGross;
									double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
									double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
									dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
									
									double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
									
									dblGross += dblKrishiKalyanCess;  
									dblGrossPT += dblKrishiKalyanCess;
									dblGrossTDS += dblKrishiKalyanCess;  
									  
									break;	
								
								case CGST :
									double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									break;
								
								case SGST :
									double dblGrossAmt2 = dblGross;
									double dblCGSTAmt = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossAmt2 = dblGrossAmt2 - dblCGSTAmt;
									
									double dblSGSTAmount = calculateSGST(con, uF, strEmpId, dblGrossAmt2, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									break;	
								
								default:
									if(uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
										double dblPerkAlignAmount = 0.0d;
										if(hmPerkAlignAmount.containsKey(strSalaryId)) {
											dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
										}
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount)); 
										dblGross += dblPerkAlignAmount;
										dblGrossPT += dblPerkAlignAmount;
										dblGrossTDS += dblPerkAlignAmount;	
										
									} else if (!uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
										dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
									} else if (uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
										if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
									} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"))));
										dblGross += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
									} else if(uF.parseToInt(strSalaryId)!=GROSS) {
										boolean isMultipePerWithParticularHead = false;
										if(hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
											isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional);
										}
										if(!isMultipePerWithParticularHead) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT")))); 
											dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
										
										
									}
									/*if(strEmpId.equals("459")) {
										System.out.println("hmTotal=====>"+hmTotal);
									}  */ 
									
								break;
							}
							
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						
						/**********  EPF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								Map<String, String> hmVoluntaryPF = hmInnerisDisplay.get(VOLUNTARY_EPF+"");
								
								double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblEEPF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
							}
							
							break;
							
						/**********  EPF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblERPF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
							}
							
							break;
							
						case LOAN:
							
//							System.out.println("Calculating Loan===>");
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {   
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
								CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2,strEmpId);
							} else {
								
								double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
								dblDeduction += dblLoanAmt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
								           
								if(true) {
//									dblGrossTDS = dblGross - dblLoanAmt;
									dblGrossTDS = dblGrossTDS - dblLoanAmt; 
								}
							}
							
							break;  
							
						case MOBILE_RECOVERY:
							
//							isDefinedEarningDeduction = true; 
							if(hmPaidSalaryInner!=null) {
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
							} else {
								double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
								dblDeduction += dblIndividualMobileRecoveryAmt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblIndividualMobileRecoveryAmt));
							}
							
							break;		
							
							
						default:
							if(hmPaidSalaryInner!=null) {
								dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								
								/*if(nSalayHead==VOLUNTARY_EPF) {
									continue;
								}*/
								
								if(hmAllowance.containsKey(strEmpId+"_"+strSalaryId)) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId))));
									dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId));
								} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_D")) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"))));
									dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
								} else if(uF.parseToInt(strSalaryId)!=PROFESSIONAL_TAX && uF.parseToInt(strSalaryId)!=TDS) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
									dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));	
								}	
							}							
							
							break;
						}
					}
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross)));
				}
				
				/**
				 * Multiple calfor isDisplay false start 
				 * */
//				hmContriSalHeadAmt = new HashMap<String, String>();
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
////					double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
//					double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap);
//					hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
//				}
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
////					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
//					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
//					hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
//				}
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
//					double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth);
//					hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
//				}
				Iterator<String> itMultiIsDisplay = hmInnerisDisplay.keySet().iterator();
				while(itMultiIsDisplay.hasNext()) {
					String strSalaryId = itMultiIsDisplay.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
					
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblMulCalAmt)))));
							dblGross += dblMulCalAmt;
							dblGrossTDS += dblMulCalAmt;
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if(hmPaidSalaryInner!=null) {
							dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblDeduction += dblMulCalAmt;
						}		
					}					
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross)));
				}
				
				/**
				 * Multiple cal isDisplay false start
				 * */
				
				/**
				 * Other cal isDisplay start
				 * */
				Iterator<String> itIsDisplayOther = hmInnerisDisplay.keySet().iterator();
				while(itIsDisplayOther.hasNext()) {
					String strSalaryId = itIsDisplayOther.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						/**********  	 TAX   *************/
						case PROFESSIONAL_TAX:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								
								
								/**
								 * KP Condition
								 * 
								 * */     
								if(strEmpId.equals("459")) {
//									System.out.println("inner dblGrossPT=====>"+dblGrossPT+"-----dblGross=====>"+dblGross);
								}
								     
//								double dblPt = calculateProfessionalTax(con, uF, strD2,dblGrossPT, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId));
							 	double dblPt = calculateProfessionalTax(con, uF, strD2,dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
								dblDeduction +=dblPt;  
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							}
							
							break;
							
							
						/**********  TDS   *************/
						case TDS:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblTDS;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							} else {

//								double dblBasic = uF.parseToDouble(hmTotalisDisplay.get(BASIC+""));
//								double dblDA = uF.parseToDouble(hmTotalisDisplay.get(DA+""));
//								double dblHRA = uF.parseToDouble(hmTotalisDisplay.get(HRA+""));
//								
//								String[] hraSalaryHeads = null;
//								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
//									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//								}
//								
//								double dblHraSalHeadsAmount = 0;
//								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
//									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotalisDisplay.get(hraSalaryHeads[i]));
//								}
//
//								Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
//								if(hmPaidSalaryDetails==null) {hmPaidSalaryDetails=new HashMap<String, String>();}
//								
//								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
								 
								if(hmEmpServiceTaxMap.containsKey(strEmpId)) {
									dblGrossTDS = dblGross;
									
//									double dblServiceTaxAmount = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX+""));
//									dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
//									
//									double dblSwachhaBharatCess = uF.parseToDouble(hmTotalisDisplay.get(SWACHHA_BHARAT_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
//									
//									double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
									
									double dblCGST = uF.parseToDouble(hmTotalisDisplay.get(CGST + ""));
									dblGrossTDS = dblGrossTDS - dblCGST;

									double dblSGST = uF.parseToDouble(hmTotalisDisplay.get(SGST + ""));
									dblGrossTDS = dblGrossTDS - dblSGST;
								}
								
								  
								/**
								 * (dblBasic + dblDA) we use dblHraSalHeadsAmount
								 * */
//								double dblTDS = calculateTDS(con, uF, strD2,strD1,dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//										nPayMonth,
//										strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, hmEmpGenderMap.get(strEmpId),  hmEmpAgeMap.get(strEmpId), hmEmpStateMap.get(strEmpId),
//										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//										hmTotalisDisplay, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
								double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
										strFinancialYearEnd, strEmpId, hmEmpLevelMap);
								
								dblDeduction += dblTDS;
								
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							}
							break;
							
							/**********  ESI EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYER CONTRIBUTION   *************/
							
							/**********  ESI EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction +=dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYEE CONTRIBUTION   *************/	
							
							/**********  LWF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							
							/**********  LWF EMPLOYER CONTRIBUTION   *************/
							
							/**********  LWF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							/**********  /LWF EMPLOYEE CONTRIBUTION   *************/	
							
						
						}
					}
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross)));
				}
				/**
				 * Other cal isDisplay end 
				 * */
				
				hmTotalSalaryisDisplay.put(strEmpId, hmTotalisDisplay);
			////=========================end code for isdisplay false=======================
			}

			//			System.out.println("hmTotalSalary===>"+hmTotalSalary);
			
			List<String> alEmpIdPayrollG = new ArrayList<String>();
			pst = con.prepareStatement("select distinct(emp_id) from payroll_generation where paycycle=? and salary_head_id not in ("+BONUS+") ");
			pst.setInt(1, uF.parseToInt(strPC));
			rs = pst.executeQuery();
			while(rs.next()) {
				String strEmpId = rs.getString("emp_id");
				alEmpIdPayrollG.add(strEmpId);
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("pst==>"+getPaycycle().split("-")[2]);
//			System.out.println("pst==>"+pst);
//			System.out.println("alEmpIdPayrollG==>"+alEmpIdPayrollG);
//			System.out.println("hmTotalSalary Grade="+hmTotalSalary);
			
			
			request.setAttribute("hmTotalSalary", hmTotalSalary);
			request.setAttribute("hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
			request.setAttribute("hmEmpNameMap", hmEmpNameMap);
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
			request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmEmpSalary", hmEmpSalary);
			request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			request.setAttribute("alEmpIdPayrollG", alEmpIdPayrollG);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("alProcessingEmployee", alProcessingEmployee);
			
			request.setAttribute("hmPaidDays", hmPaidDays);
			request.setAttribute("hmPresentDays", hmPresentDays1);
			request.setAttribute("hmLeaveDays", hmLeaveDays);
			request.setAttribute("hmLeaveTypeDays", hmLeaveTypeDays);
			request.setAttribute("hmMonthlyLeaves", hmMonthlyLeaves);
//			System.out.println("hmTotalDays=====>"+hmTotalDays);
			request.setAttribute("strTotalDays", strTotalDays);
			request.setAttribute("hmTotalDays", hmTotalDays);
			request.setAttribute("hmEmpPaymentMode", hmEmpPaymentMode);
			request.setAttribute("hmPaymentModeMap", hmPaymentModeMap);
			request.setAttribute("hmLoanAmt", hmLoanAmt);
			request.setAttribute("hmEmpStateMap", hmEmpStateMap);
			request.setAttribute("hmVariables", hmVariables);
			request.setAttribute("hmHolidayCount", hmHolidayCount);
			request.setAttribute("hmWoHLeaves", hmWoHLeaves);
			
			request.setAttribute("strD1", strD1);
			request.setAttribute("strD2", strD2);
			

			session.setAttribute("AP_hmTotalSalary", hmTotalSalary);
			session.setAttribute("AP_hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
			session.setAttribute("AP_hmEmpNameMap", hmEmpNameMap);
			session.setAttribute("AP_hmEmpCodeMap", hmEmpCodeMap);
			session.setAttribute("AP_hmLoanPoliciesMap", hmLoanPoliciesMap);
			session.setAttribute("AP_hmSalaryDetails", hmSalaryDetails);
			session.setAttribute("AP_hmEmpSalary", hmEmpSalary);
			session.setAttribute("AP_alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			session.setAttribute("AP_alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			session.setAttribute("AP_alEmpIdPayrollG", alEmpIdPayrollG);
			session.setAttribute("AP_hmServices", hmServices);
			session.setAttribute("AP_alEmp", alEmp);
			session.setAttribute("AP_alProcessingEmployee", alProcessingEmployee);
			
			session.setAttribute("AP_hmPaidDays", hmPaidDays);
			session.setAttribute("AP_hmPresentDays", hmPresentDays1);
			session.setAttribute("AP_hmLeaveDays", hmLeaveDays);
			session.setAttribute("AP_hmLeaveTypeDays", hmLeaveTypeDays);
			session.setAttribute("AP_hmMonthlyLeaves", hmMonthlyLeaves);
			
			session.setAttribute("AP_strTotalDays", strTotalDays);
			session.setAttribute("AP_hmTotalDays", hmTotalDays);
			session.setAttribute("AP_hmEmpPaymentMode", hmEmpPaymentMode);
			session.setAttribute("AP_hmPaymentModeMap", hmPaymentModeMap);
			session.setAttribute("AP_hmLoanAmt", hmLoanAmt);
			session.setAttribute("AP_hmEmpStateMap", hmEmpStateMap);
			session.setAttribute("AP_hmVariables", hmVariables);
			session.setAttribute("AP_hmOtherTaxDetails", hmOtherTaxDetails);			
			session.setAttribute("AP_hmEmpLevelMap", hmEmpLevelMap);
			session.setAttribute("AP_hmHolidayCount", hmHolidayCount);
			
			session.setAttribute("AP_strD1", strD1);
			session.setAttribute("AP_strD2", strD2);
			session.setAttribute("AP_strPC", strPC);			
			session.setAttribute("AP_f_org", getF_org());
			session.setAttribute("AP_strPaycycleDuration", getStrPaycycleDuration());
			session.setAttribute("AP_hmArearAmountMap", hmArearAmountMap);
			session.setAttribute("AP_hmWoHLeaves", hmWoHLeaves);
			
			
//			System.out.println("alProcessingEmployee==="+alProcessingEmployee);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	private double getLeaveEncashmentAmtDetailsByGrade(Connection con, UtilityFunctions uF, CommonFunctions CF, String strEmpId, double dblEnashDays, int nTotalNumberOfDaysForCalc, String strD1, String strD2, String strPC, String strLevel, String strGrade, double dblIncrementBasic, double dblIncrementDA) {
		double dblEncashAmount = 0.0d;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {	
			pst = con.prepareStatement("select sum(no_days) as no_days,leave_type_id from emp_leave_encashment where emp_id=? " +
					"and paid_from= ? and paid_to=? and paycycle=? and is_approved=1 and is_paid=false group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strPC));
			System.out.println("AP/3334--pst===>>>>>>"+pst); 
			rs = pst.executeQuery();
			Map<String, String> hmLeaveEncashDays = new HashMap<String, String>();
			StringBuilder sbLeaveTypeId = null;
			while (rs.next()) {
				hmLeaveEncashDays.put(rs.getString("leave_type_id"), rs.getString("no_days"));
				
				if(sbLeaveTypeId == null) {
					sbLeaveTypeId = new StringBuilder();
					sbLeaveTypeId.append(rs.getString("leave_type_id"));
				} else {
					sbLeaveTypeId.append(","+rs.getString("leave_type_id"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmLeaveSalaryHeads = new HashMap<String, String>();
			Map<String, String> hmLeaveSHeadsPercetage = new HashMap<String, String>();
			if(sbLeaveTypeId !=null) {
				pst = con.prepareStatement("select leave_type_id,salary_head_id,percentage from emp_leave_type where leave_type_id in ("+sbLeaveTypeId.toString()+") and level_id=?");
				pst.setInt(1, uF.parseToInt(strLevel));
				rs = pst.executeQuery();
				while (rs.next()) {
					hmLeaveSalaryHeads.put(rs.getString("leave_type_id"), rs.getString("salary_head_id"));
					hmLeaveSHeadsPercetage.put(rs.getString("leave_type_id"), rs.getString("percentage"));
				}
				rs.close();
				pst.close();
			}
			
			Iterator<String> it1 = hmLeaveSalaryHeads.keySet().iterator();
			while(it1.hasNext()) {
				String strLeaveTypeId = it1.next();
				String strSalaryHeads = hmLeaveSalaryHeads.get(strLeaveTypeId);
				List<String> alsalaryHeads = Arrays.asList(strSalaryHeads.split(","));
				if(alsalaryHeads == null) alsalaryHeads = new ArrayList<String>();
				
				double dblPercentage = uF.parseToDouble(hmLeaveSHeadsPercetage.get(strLeaveTypeId));
				if(dblPercentage == 0.0d) {
					continue;
				}
				
				dblEnashDays = uF.parseToDouble(hmLeaveEncashDays.get(strLeaveTypeId));
				
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				Map<String, Map<String,String>> hmInnerActualCTC =  new HashMap<String, Map<String, String>>(); //CF.getSalaryCalculationByGrade(con,hmInnerisDisplay, uF.parseToInt(strEmpId), dblEnashDays, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strGrade, uF, CF, strD2,hmSalInner);
				
				double salaryGross=0;
				double salaryDeduction=0;
				Iterator<String> it = hmInnerActualCTC.keySet().iterator();
				while(it.hasNext()) {
					String strSalaryId = it.next();
					
					Map<String,String> hm = hmInnerActualCTC.get(strSalaryId);
					if(hm.get("EARNING_DEDUCTION").equals("E") && alsalaryHeads.contains(strSalaryId)) {
						salaryGross +=uF.parseToDouble(hm.get("AMOUNT"));
					}else if(hm.get("EARNING_DEDUCTION").equals("D") && alsalaryHeads.contains(strSalaryId)) {
						salaryDeduction +=uF.parseToDouble(hm.get("AMOUNT"));
					}
				}
				
				dblEncashAmount += ((salaryGross - salaryDeduction) * dblPercentage)/100;
			}
			
		} catch (Exception e) {
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
		return (dblEncashAmount > 0.0d ? dblEncashAmount : 0.0d);
	}

	private double getMultipleCalAmtDetailsByGrade(Connection con, UtilityFunctions uF, CommonFunctions CF, int nSalayHead,String strEmpId, double dblPresent,
			int nTotalNumberOfDays, String strD1, String strD2, String strPC, String strGrade, Map<String, String> hm, Map<String, String> hmTotal, 
			Map<String, String> hmAnnualVarPolicyAmount, double dblReimbursementCTC, Map<String, String> hmAllowance, Map<String, String> hmVariables, 
			double dblReimbursementCTCOptional, Map<String, String> hmContriSalHeadAmt) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblMulCalAmt = 0.0d;
		try {
			
			if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();
			if(hmAllowance == null) hmAllowance = new HashMap<String, String>();
			if(hmVariables== null) hmVariables = new HashMap<String, String>();
			
			String strMulCal = hm.get("MULTIPLE_CALCULATION");
			if(strMulCal != null && !strMulCal.trim().equals("") && !strMulCal.trim().equalsIgnoreCase("NULL")) {
				List<String> al = Arrays.asList(strMulCal.trim().split(","));
				if(al == null) al = new ArrayList<String>();
				int nAl = al.size();
				boolean flag = false;
				for(int i = 0; i < nAl; i++) {
					int nHeadId = uF.parseToInt(al.get(i));
					if(nHeadId > 0 && hmAnnualVarPolicyAmount!=null && hmAnnualVarPolicyAmount.containsKey(strEmpId+"_"+nHeadId)) {
						flag = true;
					} else if(nHeadId > 0 && hmAllowance!=null && hmAllowance.containsKey(strEmpId+"_"+nHeadId)) {
						flag = true;
					} else if(nHeadId > 0 && hmVariables!=null && hmVariables.containsKey(strEmpId + "_" + nHeadId + "_E")) {
						flag = true;
					} else if(nHeadId > 0 && hmVariables!=null && hmVariables.containsKey(strEmpId + "_" + nHeadId + "_D")) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == REIMBURSEMENT_CTC) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == EMPLOYEE_EPF) { 
						flag = true;
					} else if(nHeadId > 0 && nHeadId == INCENTIVES) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == AREARS) {
						flag = true;
					}
				}
				
				if(flag) {
					Map<String, String> hmSalaryType = new HashMap<String, String>();
					pst = con.prepareStatement("select * from salary_details where grade_id = ? and (is_delete is null or is_delete=false) order by salary_head_id, salary_id");
					pst.setInt(1, uF.parseToInt(strGrade));
					rs = pst.executeQuery();
					while (rs.next()) {
						hmSalaryType.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
					}
					rs.close();
					pst.close();
		
					pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? " +
							"and effective_date = (select max(effective_date) from emp_salary_details " +
							"where emp_id =? and effective_date <= ? and is_approved=true and grade_id = ?) " +
							"and salary_head_id in ("+CTC+") and salary_head_id in (select salary_head_id " +
							"from salary_details where (is_delete is null or is_delete=false) " +
							"and org_id in (select org_id from employee_personal_details epd, " +
							"employee_official_details eod where epd.emp_per_id=eod.emp_id " +
							"and eod.emp_id=?) and grade_id = ?) and grade_id = ?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strEmpId));
					pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strGrade));
					pst.setInt(5, uF.parseToInt(strEmpId));
					pst.setInt(6, uF.parseToInt(strGrade));
					pst.setInt(7, uF.parseToInt(strGrade));
					rs = pst.executeQuery();
					boolean isCtc = false;
					double dblCTC = 0.0d;
					while (rs.next()) {
						isCtc = true;
						dblCTC = uF.parseToDouble(rs.getString("amount"));
					}
					rs.close();
					pst.close();
		
					String strSalaryType = hmSalaryType.get("" + nSalayHead);
		
					if (isCtc) {
						if (strSalaryType != null && strSalaryType.equalsIgnoreCase("F")) {
							// dblCTC = dblCTC;
						} else if (strSalaryType != null && strSalaryType.equalsIgnoreCase("D")) {
							dblCTC = dblCTC * dblPresent;
						} else {
							dblCTC = dblCTC * (dblPresent / nTotalNumberOfDays);
						}
					}
					
					StringBuilder sbFormula = new StringBuilder();
					for(int i = 0; i < nAl; i++) {
						String str = al.get(i);
						if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")) {
							boolean isInteger = uF.isInteger(str.trim());
							if(isInteger) {
								double dblAmt = uF.parseToDouble(hmTotal.get(str.trim()));
								
								if (uF.parseToInt(str.trim()) == EMPLOYER_EPF && !hmTotal.containsKey(""+EMPLOYER_EPF)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_EPF));
								}
								if (uF.parseToInt(str.trim()) == EMPLOYER_ESI && !hmTotal.containsKey(""+EMPLOYER_ESI)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_ESI));
								}
								if (uF.parseToInt(str.trim()) == EMPLOYER_LWF && !hmTotal.containsKey(""+EMPLOYER_LWF)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_LWF));
								}
								if (uF.parseToInt(str.trim()) == CTC) {
									dblAmt = dblCTC;
								} else if (uF.parseToInt(str.trim()) == REIMBURSEMENT_CTC) {
									dblAmt = dblReimbursementCTC + dblReimbursementCTCOptional;
								} else if(hmAnnualVarPolicyAmount!=null && hmAnnualVarPolicyAmount.containsKey(strEmpId+"_"+str.trim())) {
									double amtAnnual = uF.parseToDouble(hmAnnualVarPolicyAmount.get(strEmpId+"_"+str.trim())); 
									dblAmt = amtAnnual > 0.0d ? (amtAnnual / 12.0d) : 0.0d;
								}
								sbFormula.append(""+dblAmt);
							} else {
								sbFormula.append(str.trim());
							}
						}
					}
					String strPercentage = hm.get("SALARY_PERCENTAGE");
					if(uF.parseToDouble(strPercentage) > 0.0d && sbFormula != null && sbFormula.length() > 0) {
						double dblPerAmount = uF.eval(sbFormula.toString());	
						dblMulCalAmt = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
					} else {
						dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
					}
					
				} else {
					dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
				}
			} else {
				dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblMulCalAmt;

	}

	private void approvePayrollEntriesByGrade(CommonFunctions CF, String strD1, String strD2) {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if(strFinancialYear!=null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			String []strApprovePayCycle = null;			
			if(getApprovePC()!=null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length()>0) {
				strApprovePayCycle = getApprovePC().split("-");
				setPaycycle(getApprovePC());
			} else {
				strApprovePayCycle = getPaycycle().split("-");
				setPaycycle(getPaycycle());
			}
			
//			System.out.println("getPaycycle=ApprovePayroll>>>"+getPaycycle());
			
			if(strApprovePayCycle==null) {
				strApprovePayCycle = new String[3];
			}
			
//			System.out.println("getStrPaycycleDuration=====>>>"+getStrPaycycleDuration());
			
			String AP_strD1 = (String) session.getAttribute("AP_strD1");
			String AP_strD2 = (String) session.getAttribute("AP_strD2");
			String AP_strPC = (String) session.getAttribute("AP_strPC");
			String AP_f_org = (String) session.getAttribute("AP_f_org");
			String AP_strPaycycleDuration = (String) session.getAttribute("AP_strPaycycleDuration");
			
//			System.out.println("AP_strD1=====>>>"+AP_strD1);
//			System.out.println("AP_strD2=====>>>"+AP_strD2);
//			System.out.println("AP_strPC=====>>>"+AP_strPC);
//			System.out.println("AP_f_org=====>>>"+AP_f_org);
//			System.out.println("AP_strPaycycleDuration=====>>>"+AP_strPaycycleDuration);
			
			Date  stD1 = uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT);
			Date  stApD1 = uF.getDateFormat(AP_strD1, DATE_FORMAT);
			
			Date  stD2 = uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT);
			Date  stApD2 = uF.getDateFormat(AP_strD2, DATE_FORMAT);
			
			boolean check = stD1.equals(stApD1);
			boolean check1 = stD2.equals(stApD2);
//			System.out.println("stD1=====>>>"+stD1);
//			System.out.println("stApD1=====>>>"+stApD1);
//			System.out.println("stD2=====>>>"+stD2);
//			System.out.println("stApD2=====>>>"+stApD2);
//			System.out.println("check=====>>>"+check); 
//			System.out.println("check1=====>>>"+check1);
//			System.out.println("getF_org()=====>>>"+getF_org());
//			System.out.println("getStrPaycycleDuration()=====>>>"+getStrPaycycleDuration());
			
			if(check && check1 && uF.parseToInt(strApprovePayCycle[2])== uF.parseToInt(AP_strPC) && uF.parseToInt(getF_org()) == uF.parseToInt(AP_f_org) 
					&& (getStrPaycycleDuration()!=null && getStrPaycycleDuration().equals(AP_strPaycycleDuration))) {
				
//				System.out.println("approve=====>>>");
				LinkedHashMap<String,Map<String,String>> hmTotalSalary = (LinkedHashMap<String,Map<String,String>>)session.getAttribute("AP_hmTotalSalary");
				LinkedHashMap<String,Map<String,String>> hmTotalSalaryisDisplay = (LinkedHashMap<String,Map<String,String>>)session.getAttribute("AP_hmTotalSalaryisDisplay");
				Map<String,String> hmEmpNameMap = (Map<String, String>)session.getAttribute("AP_hmEmpNameMap");
				Map<String,String> hmSalaryDetails = (Map<String, String>)session.getAttribute("AP_hmSalaryDetails");
				Map<String,String> hmEmpSalary = (Map<String, String>)session.getAttribute("AP_hmEmpSalary");
				Map<String, List<String>> hmServices = (Map<String, List<String>>)session.getAttribute("AP_hmServices");
				List<String> alEmp = (List<String>) session.getAttribute("AP_alEmp");
				List<String> alProcessingEmployee = (List<String>)session.getAttribute("AP_alProcessingEmployee");
				Map<String, String> hmPresentDays = (Map<String, String>)session.getAttribute("AP_hmPresentDays");
				Map<String, String> hmPaidDays = (Map<String, String>)session.getAttribute("AP_hmPaidDays");
				Map<String,Map<String,String>> hmLeaveDays 	= (Map<String, Map<String,String>>)session.getAttribute("AP_hmLeaveDays");
				Map<String,Map<String,String>> hmLeaveTypeDays 	=(Map<String, Map<String,String>>) session.getAttribute("AP_hmLeaveTypeDays");
				Map<String, String> hmMonthlyLeaves 	= (Map<String, String>)session.getAttribute("AP_hmMonthlyLeaves");
				Map<String, String> hmLoanAmt 	= (Map<String, String>)session.getAttribute("AP_hmLoanAmt");
				Map<String, String> hmEmpPaymentMode 	= (Map<String, String>)session.getAttribute("AP_hmEmpPaymentMode");
				Map<String, String> hmEmpStateMap 	= (Map<String, String>)session.getAttribute("AP_hmEmpStateMap");
				Map<String, String> hmVariables 	= (Map<String, String>)session.getAttribute("AP_hmVariables");
				
	//			double dbTotalDays = uF.parseToDouble((String)session.getAttribute("AP_strTotalDays"));
				Map<String, String> hmTotalDays 	= (Map<String, String>)session.getAttribute("AP_hmTotalDays");
				if(hmTotalDays==null) hmTotalDays = new HashMap<String, String>();
				
				List<String> alEmpSalaryDetailsEarning = (List<String>)session.getAttribute("AP_alEmpSalaryDetailsEarning");
				List<String> alEmpSalaryDetailsDeduction = (List<String>)session.getAttribute("AP_alEmpSalaryDetailsDeduction");
				
	
				Map<String, String> hmOtherTaxDetails 	= (Map<String, String>)session.getAttribute("AP_hmOtherTaxDetails");
				Map<String, String> hmEmpLevelMap 	= (Map<String, String>)session.getAttribute("AP_hmEmpLevelMap"); 
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				
				Map<String, Map<String, String>> hmArearAmountMap 	= (Map<String, Map<String, String>>)session.getAttribute("AP_hmArearAmountMap");
				if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
				
				String strDomain = request.getServerName().split("\\.")[0];
				GenerateSalarySlip gs = new GenerateSalarySlip();
				ApprovePayrollRunnable objRunnable = new ApprovePayrollRunnable(this, gs, con, uF, CF, strFinancialYearStart, strFinancialYearEnd, strApprovePayCycle, hmEmpStateMap, hmCurrencyDetails, hmEmpCurrency, hmVariables, request, response,hmOtherTaxDetails,hmEmpLevelMap, strDomain,hmArearAmountMap);
				
				List<String> checkPayrollList=new ArrayList<String>();
				pst=con.prepareStatement("select emp_id from payroll_generation where paycycle=? and month=? and year=? group by emp_id having emp_id>0 order by emp_id");
				pst.setInt(1, uF.parseToInt(strApprovePayCycle[2]));
				pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
				pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
				rs=pst.executeQuery();
				while(rs.next()) {
					checkPayrollList.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				if(hmUserTypeId == null) hmUserTypeId = new HashMap<String, String>();
				Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
				Set<String> set0 = hmTotalSalary.keySet();
				Iterator<String> it0 = set0.iterator();
				while(it0.hasNext()) {
					String strEmpId = it0.next();
					String strOrgId = hmEmpOrgId.get(strEmpId);
					if(!checkPayrollList.isEmpty() && checkPayrollList.contains(strEmpId)) {
						continue;
					}
					
					Map<String,String> hmTotal = hmTotalSalary.get(strEmpId);
					Map<String,String> hmTotalisDisplay = hmTotalSalaryisDisplay.get(strEmpId);
					List<String> alServices = hmServices.get(strEmpId);
					if(alServices==null)alServices=new ArrayList<String>();
					
					String arr[] = getChbox();
					int x = ArrayUtils.contains(arr, strEmpId);
					if(x<0)continue;
					
					Map<String, String> hmLeaves = hmLeaveDays.get(strEmpId);
					if(hmLeaves==null)hmLeaves = new HashMap<String, String>();
					
					Map<String, String> hmLeavesType = hmLeaveTypeDays.get(strEmpId);
					if(hmLeavesType==null)hmLeavesType = new HashMap<String, String>();
					
					double dbTotalDays = uF.parseToDouble(hmTotalDays.get(strEmpId));
					
					double dblPresentDays = uF.parseToDouble(hmPresentDays.get(strEmpId));
					double dblPaidLeaveDays = uF.parseToDouble(hmLeavesType.get("COUNT"));
					double dblPaidDays = uF.parseToDouble(hmPaidDays.get(strEmpId));
					
					double dblTotal = 0.0;
					
					for(int i=0;i<alEmpSalaryDetailsEarning.size(); i++) {
						String strSalaryId = alEmpSalaryDetailsEarning.get(i);
						
						
						// As Bonus is paid independent of paycycle...
//						if(uF.parseToInt(strSalaryId)==BONUS && !uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())) {
//							continue;
//						}
						
						
						if(hmTotal!=null && !hmTotal.containsKey(strSalaryId)) {
							if(hmTotalisDisplay!=null && hmTotalisDisplay.containsKey(strSalaryId)) {
								pst = con.prepareStatement("insert into payroll_generation_lta (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
								pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
								pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setInt(6, uF.parseToInt(strSalaryId));
								pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotalisDisplay.get(strSalaryId)))));
								pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
								pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
								pst.setInt(12, uF.parseToInt(((alServices.size()>0)?alServices.get(0):"0")));
								pst.setString(13,"E");
								pst.setString(14,getStrPaycycleDuration()); 
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
								
								pst.setDouble(18, dblPresentDays);
								pst.setDouble(19, dblPaidDays);
								pst.setDouble(20, dblPaidLeaveDays);
								pst.setDouble(21, dbTotalDays);
								pst.execute();
								pst.close();
							}
							
							continue;
						}
						
	//					pst = con.prepareStatement(insertPayrollGeneration);
						pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, " +
								"financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days,approve_by,approve_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
						pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
						pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
						pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
						pst.setInt(6, uF.parseToInt(strSalaryId));
						pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId)))));
						pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
						pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
						pst.setInt(12, uF.parseToInt(((alServices.size()>0)?alServices.get(0):"0")));
						pst.setString(13,"E");
						pst.setString(14,getStrPaycycleDuration()); 
						pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
						pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
						pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
						pst.setDouble(18, dblPresentDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPaidLeaveDays);
						pst.setDouble(21, dbTotalDays);
						pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.execute();
						pst.close();
						
						dblTotal += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						
						double dblAmt = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==REIMBURSEMENT && dblAmt>0) {
							pst = con.prepareStatement(updateReimbursementPayroll);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==TRAVEL_REIMBURSEMENT && dblAmt>0) {
							pst = con.prepareStatement(updateReimbursementPayroll1);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==MOBILE_REIMBURSEMENT && dblAmt>0) {
							pst = con.prepareStatement(updateReimbursementPayroll2);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
							
						}
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==OTHER_REIMBURSEMENT && dblAmt>0) {
							pst = con.prepareStatement(updateReimbursementPayroll3);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
							
						}
					}
					
					for(int i=0;i<alEmpSalaryDetailsDeduction.size(); i++) {
						String strSalaryId = alEmpSalaryDetailsDeduction.get(i);
	
						if(hmTotal!=null && !hmTotal.containsKey(strSalaryId)) {
							if(hmTotalisDisplay!=null && hmTotalisDisplay.containsKey(strSalaryId)) {
								pst = con.prepareStatement("insert into payroll_generation_lta (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
								pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
								pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setInt(6, uF.parseToInt(strSalaryId));
								pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotalisDisplay.get(strSalaryId)))));
								pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
								pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
								pst.setInt(12, uF.parseToInt(((alServices.size()>0)?alServices.get(0):"0")));
								pst.setString(13,"D");
								pst.setString(14,getStrPaycycleDuration()); 
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
								
								pst.setDouble(18, dblPresentDays);
								pst.setDouble(19, dblPaidDays);
								pst.setDouble(20, dblPaidLeaveDays);
								pst.setDouble(21, dbTotalDays);
								pst.execute();
								pst.close();
							}
							
							continue;
						}
						
						pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, " +
							"financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days,approve_by,approve_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
						pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
						pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
						pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
						pst.setInt(6, uF.parseToInt(strSalaryId));
						pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId)))));
						pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
						pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
						pst.setInt(12, uF.parseToInt(((alServices.size()>0)?alServices.get(0):"0")));
						pst.setString(13,"D");
						pst.setString(14,getStrPaycycleDuration());
						pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
						pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
						pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
						pst.setDouble(18, dblPresentDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPaidLeaveDays);
						pst.setDouble(21, dbTotalDays);
						pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.execute();
						pst.close();
						 
						
						dblTotal -= uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						
						double dblAmt = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==LOAN && dblAmt>0) {
							
							pst = con.prepareStatement(selectLoanPyroll2);
							pst.setInt(1, uF.parseToInt(strEmpId));
							rs = pst.executeQuery();
							
							double dblBalanceAmt = 0;
							int nLoanId = 0;
							int nLoanAppId = 0;
							while(rs.next()) {
								
								dblBalanceAmt = rs.getDouble("balance_amount");
								nLoanId = rs.getInt("loan_id");
								nLoanAppId = rs.getInt("loan_applied_id");
								
							
								double dblAmt1 = uF.parseToDouble(hmLoanAmt.get(nLoanAppId+""));
								dblBalanceAmt = dblBalanceAmt - dblAmt1;
								
								
								pst1 = con.prepareStatement(updateLoanPyroll1);
								
								
								pst1.setDouble(1, dblBalanceAmt);
								if(dblBalanceAmt>0) {
									pst1.setBoolean(2, false);
								} else {
									pst1.setBoolean(2, true);	
								}
								pst1.setInt(3, nLoanAppId);
								pst1.execute();
								pst1.close();
								
								
								
								pst1 = con.prepareStatement(insertLoanPyroll);
								pst1.setInt(1, uF.parseToInt(strEmpId));
								pst1.setInt(2, nLoanId);
								pst1.setDouble(3, dblAmt1);
								pst1.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
								pst1.setString(5, "S");   
								pst1.setInt(6, nLoanAppId);
								pst1.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
								pst1.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//								pst1.setDate(9, uF.getDateFormat(strD2, DATE_FORMAT));
								pst1.execute();
								pst1.close();
								
								
	//							System.out.println("Loan====insertLoanPyroll=====+>"+pst);
								
							}
							rs.close();
							pst.close();
						
						}
						
						
						
					}
					
					objRunnable.setData(hmTotal, strEmpId, dblTotal, strOrgId);
					objRunnable.run1();
//					Thread t = new Thread(objRunnable);
//					t.start();
					
					/**
					 * 
					 * UPDATE INFO for challans 
					 * 
					 * **/
					Map<String, String> hmInnerCurrencyDetails = (Map<String, String>)hmCurrencyDetails.get(hmEmpCurrency.get(strEmpId)) ;
					if(hmInnerCurrencyDetails==null)hmInnerCurrencyDetails=new HashMap<String, String>();
					
					
					/*Notifications nF = new Notifications(N_NEW_SALARY_APPROVED, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(strEmpId);
					nF.setStrSalaryAmount(uF.showData(hmInnerCurrencyDetails.get("LONG_CURR"),"")+""+uF.formatIntoTwoDecimal(dblTotal));
					nF.setStrPaycycle(strApprovePayCycle[0]+"-"+strApprovePayCycle[1]);
					nF.setEmailTemplate(true);
					nF.sendNotifications();*/
					
					
					List<String> alAccountant = CF.getEmpAccountantList(con,uF,strEmpId,hmUserTypeId);
					if(alAccountant == null) alAccountant = new ArrayList<String>();
					if(alAccountant.size() > 0) {
						int nAccountant = alAccountant.size();
						for(int i = 0; i < nAccountant; i++) {
							String strAccountant = alAccountant.get(i);
							String alertData = "<div style=\"float: left;\"> Salary, <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b> has been approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. PLease Check. </div>";
							String alertAction = "PayPayroll.action?pType=WR";
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strAccountant);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(ACCOUNTANT));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
					} else {
						List<String> alGlobalHR = CF.getGlobalHRList(con,uF,hmUserTypeId);
						if(alGlobalHR == null) alGlobalHR = new ArrayList<String>();
						int nGlobalHR = alGlobalHR.size();
						for(int i = 0; i < nGlobalHR; i++) {
							String strGlobalHR = alGlobalHR.get(i);
							String alertData = "<div style=\"float: left;\"> Salary, <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b> has been approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. PLease Check. </div>";
							String alertAction = "PayPayroll.action?pType=WR";
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strGlobalHR);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
					}
					
				}   
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadClockEntries(UtilityFunctions uF) {
//		paycycleList = new FillPayCycles(getStrPaycycleDuration()).fillPayCycles(CF); 
		paycycleList = new FillPayCycles(getStrPaycycleDuration(),request).fillPayCycles(CF, getF_org());
//		wLocationList = new FillWLocation().fillWLocation();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		paymentModeList = new FillPayMode().fillPaymentMode();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		getSelectedFilter(uF);
		
		return LOAD;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		alFilter.add("DURATION");
		if(getStrPaycycleDuration()!=null) {
			String payDuration="";
			int k=0;
			for(int i=0;paycycleDurationList!=null && i<paycycleDurationList.size();i++) {
				if(getStrPaycycleDuration().equals(paycycleDurationList.get(i).getPaycycleDurationId())) {
					if(k==0) {
						payDuration=paycycleDurationList.get(i).getPaycycleDurationName();
					} else {
						payDuration+=", "+paycycleDurationList.get(i).getPaycycleDurationName();
					}
					k++;
				}
			}
			if(payDuration!=null && !payDuration.equals("")) {
				hmFilter.put("DURATION", payDuration);
			} else {
				hmFilter.put("DURATION", "All Duration");
			}
		} else {
			hmFilter.put("DURATION", "All Duration");
		}

		alFilter.add("PAYCYCLE");	
		if(getPaycycle()!=null) {
			String strPayCycle="";
			int k=0;
			for(int i=0;paycycleList!=null && i<paycycleList.size();i++) {
				if(getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
					if(k==0) {
						strPayCycle=paycycleList.get(i).getPaycycleName();
					} else {
						strPayCycle+=", "+paycycleList.get(i).getPaycycleName();
					}
					k++;
				}
			}
			if(strPayCycle!=null && !strPayCycle.equals("")) {
				hmFilter.put("PAYCYCLE", strPayCycle);
			} else {
				hmFilter.put("PAYCYCLE", "All Paycycle");
			}
			
		}
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {			
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("PAYMODE");
		if(getF_paymentMode()!=null) {			
			String strPayMode="";
			int k=0;
			for(int i=0;paymentModeList!=null && i<paymentModeList.size();i++) {
				if(getF_paymentMode().equals(paymentModeList.get(i).getPayModeId())) {
					if(k==0) {
						strPayMode=paymentModeList.get(i).getPayModeName();
					} else {
						strPayMode+=", "+paymentModeList.get(i).getPayModeName();
					}
					k++;
				}
			}
			if(strPayMode!=null && !strPayMode.equals("")) {
				hmFilter.put("PAYMODE", strPayMode);
			} else {
				hmFilter.put("PAYMODE", "All Payment Mode");
			}
			
		} else {
			hmFilter.put("PAYMODE", "All Payment Mode");
		}
		
		
		alFilter.add("LOCATION");
		if(getwLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getwLocation().length;j++) {
					if(getwLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All Services");
			}
		} else {
			hmFilter.put("SERVICE", "All Services");
		}
		
		alFilter.add("LEVEL");
		if(getLevel()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getLevel().length;j++) {
					if(getLevel()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		String selectedFilter=CF.getSelectedFilter(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
//	List alWorkedDates = new ArrayList();
	Map<String, String> hmEmpRosterLunchDeduction = new HashMap<String, String>();
	Map<String, Map<String, String>> hmLeavesMap = null;
	Map<String, String> hmLeaves = null;
	
	public String approvePayrollEntries(CommonFunctions CF,String strD1,String strD2) {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if(strFinancialYear!=null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			String []strApprovePayCycle = null;
			
			if(getApprovePC()!=null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length()>0) {
				strApprovePayCycle = getApprovePC().split("-");
				setPaycycle(getApprovePC());
			} else {
				strApprovePayCycle = getPaycycle().split("-");
				setPaycycle(getPaycycle());
			}
			
//			System.out.println("getPaycycle=ApprovePayroll>>>"+getPaycycle());
			
			if(strApprovePayCycle==null) {
				strApprovePayCycle = new String[3];
			}
			
//			System.out.println("getStrPaycycleDuration=====>>>"+getStrPaycycleDuration());
			
			String AP_strD1 = (String) session.getAttribute("AP_strD1");
			String AP_strD2 = (String) session.getAttribute("AP_strD2");
			String AP_strPC = (String) session.getAttribute("AP_strPC");
			String AP_f_org = (String) session.getAttribute("AP_f_org");
			String AP_strPaycycleDuration = (String) session.getAttribute("AP_strPaycycleDuration");
			
//			System.out.println("AP_strD1=====>>>"+AP_strD1);
//			System.out.println("AP_strD2=====>>>"+AP_strD2);
//			System.out.println("AP_strPC=====>>>"+AP_strPC);
//			System.out.println("AP_f_org=====>>>"+AP_f_org);
//			System.out.println("AP_strPaycycleDuration=====>>>"+AP_strPaycycleDuration);
			
			Date  stD1 = uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT);
			Date  stApD1 = uF.getDateFormat(AP_strD1, DATE_FORMAT);
			
			Date  stD2 = uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT);
			Date  stApD2 = uF.getDateFormat(AP_strD2, DATE_FORMAT);
			
			boolean check = stD1.equals(stApD1);
			boolean check1 = stD2.equals(stApD2);
//			System.out.println("stD1=====>>>"+stD1);
//			System.out.println("stApD1=====>>>"+stApD1);
//			System.out.println("stD2=====>>>"+stD2);
//			System.out.println("stApD2=====>>>"+stApD2);
//			System.out.println("check=====>>>"+check); 
//			System.out.println("check1=====>>>"+check1);
//			System.out.println("getF_org()=====>>>"+getF_org());
//			System.out.println("getStrPaycycleDuration()=====>>>"+getStrPaycycleDuration());
			
			if(check && check1 && uF.parseToInt(strApprovePayCycle[2])== uF.parseToInt(AP_strPC) && uF.parseToInt(getF_org()) == uF.parseToInt(AP_f_org) 
					&& (getStrPaycycleDuration()!=null && getStrPaycycleDuration().equals(AP_strPaycycleDuration))) {
				
//				System.out.println("approve=====>>>");
			
				LinkedHashMap<String,Map<String,String>> hmTotalSalary = (LinkedHashMap<String,Map<String,String>>)session.getAttribute("AP_hmTotalSalary");
				LinkedHashMap<String,Map<String,String>> hmTotalSalaryisDisplay = (LinkedHashMap<String,Map<String,String>>)session.getAttribute("AP_hmTotalSalaryisDisplay");
				Map<String,String> hmEmpNameMap = (Map<String, String>)session.getAttribute("AP_hmEmpNameMap");
				Map<String,String> hmSalaryDetails = (Map<String, String>)session.getAttribute("AP_hmSalaryDetails");
				Map<String,String> hmEmpSalary = (Map<String, String>)session.getAttribute("AP_hmEmpSalary");
				Map<String, List<String>> hmServices = (Map<String, List<String>>)session.getAttribute("AP_hmServices");
				List<String> alEmp = (List<String>) session.getAttribute("AP_alEmp");
				List<String> alProcessingEmployee = (List<String>)session.getAttribute("AP_alProcessingEmployee");
				Map<String, String> hmPresentDays = (Map<String, String>)session.getAttribute("AP_hmPresentDays");
				Map<String, String> hmPaidDays = (Map<String, String>)session.getAttribute("AP_hmPaidDays");
				Map<String,Map<String,String>> hmLeaveDays 	= (Map<String, Map<String,String>>)session.getAttribute("AP_hmLeaveDays");
				Map<String,Map<String,String>> hmLeaveTypeDays 	=(Map<String, Map<String,String>>) session.getAttribute("AP_hmLeaveTypeDays");
				Map<String, String> hmMonthlyLeaves 	= (Map<String, String>)session.getAttribute("AP_hmMonthlyLeaves");
				Map<String, String> hmLoanAmt 	= (Map<String, String>)session.getAttribute("AP_hmLoanAmt");
				Map<String, String> hmEmpPaymentMode 	= (Map<String, String>)session.getAttribute("AP_hmEmpPaymentMode");
				Map<String, String> hmEmpStateMap 	= (Map<String, String>)session.getAttribute("AP_hmEmpStateMap");
				Map<String, String> hmVariables 	= (Map<String, String>)session.getAttribute("AP_hmVariables");
				
	//			double dbTotalDays = uF.parseToDouble((String)session.getAttribute("AP_strTotalDays"));
				Map<String, String> hmTotalDays 	= (Map<String, String>)session.getAttribute("AP_hmTotalDays");
				if(hmTotalDays==null) hmTotalDays = new HashMap<String, String>();
				
				List<String> alEmpSalaryDetailsEarning = (List<String>)session.getAttribute("AP_alEmpSalaryDetailsEarning");
				List<String> alEmpSalaryDetailsDeduction = (List<String>)session.getAttribute("AP_alEmpSalaryDetailsDeduction");
	
				Map<String, String> hmOtherTaxDetails 	= (Map<String, String>)session.getAttribute("AP_hmOtherTaxDetails");
				Map<String, String> hmEmpLevelMap 	= (Map<String, String>)session.getAttribute("AP_hmEmpLevelMap"); 
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				
				Map<String, Map<String, String>> hmArearAmountMap 	= (Map<String, Map<String, String>>)session.getAttribute("AP_hmArearAmountMap");
				if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
				
				String strDomain = request.getServerName().split("\\.")[0];
				GenerateSalarySlip gs = new GenerateSalarySlip();
				ApprovePayrollRunnable objRunnable = new ApprovePayrollRunnable(this, gs, con, uF, CF, strFinancialYearStart, strFinancialYearEnd, strApprovePayCycle, hmEmpStateMap, hmCurrencyDetails, hmEmpCurrency, hmVariables, request, response,hmOtherTaxDetails,hmEmpLevelMap, strDomain,hmArearAmountMap);
				
				List<String> checkPayrollList=new ArrayList<String>();
				pst=con.prepareStatement("select emp_id from payroll_generation where paycycle=? and month=? and year=? group by emp_id having emp_id>0 order by emp_id");
				pst.setInt(1, uF.parseToInt(strApprovePayCycle[2]));
				pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
				pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
				rs=pst.executeQuery();
				while(rs.next()) {
					checkPayrollList.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
//				System.out.println("ApPr/4417---hmTotalSalary=="+hmTotalSalary);
				Set<String> set0 = hmTotalSalary.keySet();
				Iterator<String> it0 = set0.iterator();
				while(it0.hasNext()) {
					String strEmpId = it0.next();
					String strOrgId = hmEmpOrgId.get(strEmpId);
					if(!checkPayrollList.isEmpty() && checkPayrollList.contains(strEmpId)) {
						continue;
					}
					
					Map<String,String> hmTotal = hmTotalSalary.get(strEmpId);
					Map<String,String> hmTotalisDisplay = hmTotalSalaryisDisplay.get(strEmpId);
					List<String> alServices = hmServices.get(strEmpId);
					if(alServices==null)alServices=new ArrayList<String>();
					
					String arr[] = getChbox();
					int x = ArrayUtils.contains(arr, strEmpId);
					if(x<0)continue;
					
					Map<String, String> hmLeaves = hmLeaveDays.get(strEmpId);
					if(hmLeaves==null)hmLeaves = new HashMap<String, String>();
					
					Map<String, String> hmLeavesType = hmLeaveTypeDays.get(strEmpId);
					if(hmLeavesType==null)hmLeavesType = new HashMap<String, String>();
					
					double dbTotalDays = uF.parseToDouble(hmTotalDays.get(strEmpId));
					
					double dblPresentDays = uF.parseToDouble(hmPresentDays.get(strEmpId));
					double dblPaidLeaveDays = uF.parseToDouble(hmLeavesType.get("COUNT"));
					double dblPaidDays = uF.parseToDouble(hmPaidDays.get(strEmpId));
					
					double dblTotal = 0.0;
					
					for(int i=0;i<alEmpSalaryDetailsEarning.size(); i++) {
						String strSalaryId = alEmpSalaryDetailsEarning.get(i);
						
						// As Bonus is paid independent of paycycle...
//						if(uF.parseToInt(strSalaryId)==BONUS && !uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())) {
//							continue;
//						}
						
						
						if(hmTotal!=null && !hmTotal.containsKey(strSalaryId)) {
							if(hmTotalisDisplay!=null && hmTotalisDisplay.containsKey(strSalaryId)) {
								pst = con.prepareStatement("insert into payroll_generation_lta (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
								pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
								pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setInt(6, uF.parseToInt(strSalaryId));
								pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotalisDisplay.get(strSalaryId)))));
								pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
								pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
								pst.setInt(12, uF.parseToInt(((alServices.size()>0)?alServices.get(0):"0")));
								pst.setString(13,"E");
								pst.setString(14,getStrPaycycleDuration()); 
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
								
								pst.setDouble(18, dblPresentDays);
								pst.setDouble(19, dblPaidDays);
								pst.setDouble(20, dblPaidLeaveDays);
								pst.setDouble(21, dbTotalDays);
								pst.execute();
								pst.close();
							}
							
							continue;
						}
						
	//					pst = con.prepareStatement(insertPayrollGeneration);
						pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, " +
								"financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days,approve_by,approve_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
						pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
						pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
						pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
						pst.setInt(6, uF.parseToInt(strSalaryId));
						pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId)))));
						pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
						pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
						pst.setInt(12, uF.parseToInt(((alServices.size()>0)?alServices.get(0):"0")));
						pst.setString(13,"E");
						pst.setString(14,getStrPaycycleDuration()); 
						pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
						pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
						pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
						
						pst.setDouble(18, dblPresentDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPaidLeaveDays);
						pst.setDouble(21, dbTotalDays);
						pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
						
						pst.execute();
						pst.close();
						
						
						dblTotal += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						
						double dblAmt = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==REIMBURSEMENT && dblAmt>0) {
							pst = con.prepareStatement(updateReimbursementPayroll);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==TRAVEL_REIMBURSEMENT && dblAmt>0) {
							
							pst = con.prepareStatement(updateReimbursementPayroll1);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
							
						}
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==MOBILE_REIMBURSEMENT && dblAmt>0) {
							
							pst = con.prepareStatement(updateReimbursementPayroll2);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
							
						}
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==OTHER_REIMBURSEMENT && dblAmt>0) {
							
							pst = con.prepareStatement(updateReimbursementPayroll3);
							
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
							
						}
						
						
					}
					
					for(int i=0;i<alEmpSalaryDetailsDeduction.size(); i++) {
						String strSalaryId = alEmpSalaryDetailsDeduction.get(i);
	
	
						if(hmTotal!=null && !hmTotal.containsKey(strSalaryId)) {
							
							if(hmTotalisDisplay!=null && hmTotalisDisplay.containsKey(strSalaryId)) {
								pst = con.prepareStatement("insert into payroll_generation_lta (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
								pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
								pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setInt(6, uF.parseToInt(strSalaryId));
								pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotalisDisplay.get(strSalaryId)))));
								pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
								pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
								pst.setInt(12, uF.parseToInt(((alServices.size()>0)?alServices.get(0):"0")));
								pst.setString(13,"D");
								pst.setString(14,getStrPaycycleDuration()); 
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
								
								pst.setDouble(18, dblPresentDays);
								pst.setDouble(19, dblPaidDays);
								pst.setDouble(20, dblPaidLeaveDays);
								pst.setDouble(21, dbTotalDays);
								pst.execute();
								pst.close();
							}
							
							
							continue;
						}
						
						pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, " +
						"financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days,approve_by,approve_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
						pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
						pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
						pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
						pst.setInt(6, uF.parseToInt(strSalaryId));
						pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId)))));
						pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
						pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
						pst.setInt(12, uF.parseToInt(((alServices.size()>0)?alServices.get(0):"0")));
						pst.setString(13,"D");
						pst.setString(14,getStrPaycycleDuration());
						pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
						pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
						pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
						pst.setDouble(18, dblPresentDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPaidLeaveDays);
						pst.setDouble(21, dbTotalDays);
						pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.execute();
						pst.close();
						 
						log.debug("Inserting  D  ==== "+pst);
						log.debug("Inserting  D  ==== "+hmTotal);
						
						dblTotal -= uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						
						double dblAmt = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						
						
						if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==LOAN && dblAmt>0) {
							
							pst = con.prepareStatement(selectLoanPyroll2);
							pst.setInt(1, uF.parseToInt(strEmpId));
							rs = pst.executeQuery();
							
							double dblBalanceAmt = 0;
							int nLoanId = 0;
							int nLoanAppId = 0;
							while(rs.next()) {
								
								dblBalanceAmt = rs.getDouble("balance_amount");
								nLoanId = rs.getInt("loan_id");
								nLoanAppId = rs.getInt("loan_applied_id");
								
							
								double dblAmt1 = uF.parseToDouble(hmLoanAmt.get(nLoanAppId+""));
								dblBalanceAmt = dblBalanceAmt - dblAmt1;
								
								
								pst1 = con.prepareStatement(updateLoanPyroll1);
								
								
								pst1.setDouble(1, dblBalanceAmt);
								if(dblBalanceAmt>0) {
									pst1.setBoolean(2, false);
								} else {
									pst1.setBoolean(2, true);	
								}
								pst1.setInt(3, nLoanAppId);
								pst1.execute();
								pst1.close();
								
								pst1 = con.prepareStatement(insertLoanPyroll);
								pst1.setInt(1, uF.parseToInt(strEmpId));
								pst1.setInt(2, nLoanId);
								pst1.setDouble(3, dblAmt1);
								pst1.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
								pst1.setString(5, "S");   
								pst1.setInt(6, nLoanAppId);
								pst1.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
								pst1.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//								pst1.setDate(9, uF.getDateFormat(strD2, DATE_FORMAT));
								pst1.execute();
								pst1.close();
								
								
	//							System.out.println("Loan====insertLoanPyroll=====+>"+pst);
								
							}
							rs.close();
							pst.close();
						
						}
					}
					
					objRunnable.setData(hmTotal, strEmpId, dblTotal, strOrgId);
					objRunnable.run1();
//					Thread t = new Thread(objRunnable);
//					t.start();
					
					/**
					 * 
					 * UPDATE INFO for challans 
					 * 
					 * **/
					
					Map<String, String> hmInnerCurrencyDetails = (Map<String, String>)hmCurrencyDetails.get(hmEmpCurrency.get(strEmpId)) ;
					if(hmInnerCurrencyDetails==null)hmInnerCurrencyDetails=new HashMap<String, String>();
					
					/*Notifications nF = new Notifications(N_NEW_SALARY_APPROVED, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(strEmpId);
					nF.setStrSalaryAmount(uF.showData(hmInnerCurrencyDetails.get("LONG_CURR"),"")+""+uF.formatIntoTwoDecimal(dblTotal));
					nF.setStrPaycycle(strApprovePayCycle[0]+"-"+strApprovePayCycle[1]);
					nF.setEmailTemplate(true);
					nF.sendNotifications();*/
					
					List<String> alAccountant = CF.getEmpAccountantList(con,uF,strEmpId,hmUserTypeId);
					if(alAccountant == null) alAccountant = new ArrayList<String>();
					if(alAccountant.size() > 0) {
						int nAccountant = alAccountant.size();
						for(int i = 0; i < nAccountant; i++) {
							String strAccountant = alAccountant.get(i);
							String alertData = "<div style=\"float: left;\"> Salary, <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b> has been approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. PLease Check. </div>";
							String alertAction = "PayPayroll.action?pType=WR";
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strAccountant);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(ACCOUNTANT));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
					} else {
						List<String> alGlobalHR = CF.getGlobalHRList(con,uF,hmUserTypeId);
						if(alGlobalHR == null) alGlobalHR = new ArrayList<String>();
						int nGlobalHR = alGlobalHR.size();
						for(int i = 0; i < nGlobalHR; i++) {
							String strGlobalHR = alGlobalHR.get(i);
							String alertData = "<div style=\"float: left;\"> Salary, <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b> has been approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. PLease Check. </div>";
							String alertAction = "PayPayroll.action?pType=WR";
							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strGlobalHR);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
							userAlerts.setStatus(INSERT_WR_ALERT);
							Thread t = new Thread(userAlerts);
							t.run();
						}
					}
					
				}   
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewClockEntriesForPayrollApproval(CommonFunctions CF, String strReqEmpId, String strD1, String strD2,String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
//			this.strD1 = strD1;
//			this.strD2 = strD2;
//			System.out.println("AP/4798--strReqEmpId===="+strReqEmpId);
			System.out.println("AP/4799--strD1="+strD1+"--strD2="+strD2+"--strPC="+strPC);
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int nTotalNumberOfDays = 0;
			if(uF.parseToBoolean(CF.getIsPaycycleAdjustment())) {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
				nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
			} else {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
				nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
			}
			
//			int nTotalNumberOfDays = uF.parseToInt(uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
			
			con = db.makeConnection(con);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if(strFinancialYear!=null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			double dblInvestmentExemption = 0.0d;
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));

				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
				
//				dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()) {
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			 
//			System.out.println("pst===>"+pst);
			
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();
			
//			String strFinancialYearEnd = null;
//			String strFinancialYearStart = null;
//			double dblInvestmentExemption = 0.0d;
			double dblStandardHrs = 0.0d;
			
			Map<String, String> hmOrg= CF.getOrgDetails(con,uF,getF_org());
			if(hmOrg == null) hmOrg=new HashMap<String, String>();
			
			Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
			Map<String, String> hmEmpEndDateMap = new HashMap<String, String>();
			Map<String, Map<String, String>> hmWlocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpStateMap = new HashMap<String, String>();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map<String, String> hmEmpJoiningMap = CF.getEmpJoiningDateMap(con, uF, hmEmpEndDateMap); 
			request.setAttribute("hmEmpJoiningMap", hmEmpJoiningMap);
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null); 
			Map<String, String> hmEmpCodeMap = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
			Map<String, String> hmEmpExemptionsMap = getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
			Map<String, String> hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, Map<String, String>> hmEmpPaidSalary = CF.getPaidSalary(con, strFinancialYearStart, strFinancialYearEnd, strPC);
			Map<String, String> hmEmpRentPaidMap = getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmEmpIncomeOtherSourcesMap = getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String, String> hmLeaveEncashment = CF.getLeaveEncashment(con, uF, strD1, strD2, strPC);
			
//			Map<String, Map<String, String>> hmOverTimeMap = CF.getOverTimeMap(con, CF);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);

			Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, getF_org());
			Map<String, Map<String, String>> hmLeaveDays = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
			Map<String, String> hmMonthlyLeaves = new HashMap<String, String>();
					
			
//			hmLeaveDays = CF.getLeaveDates(con, strD1, strD2, CF, hmLeaveTypeDays, true, hmMonthlyLeaves);
			hmLeaveDays = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, hmLeaveTypeDays, true, hmMonthlyLeaves);
//			System.out.println("hmLeaveDays=====>"+hmLeaveDays);
//			System.out.println("hmLeaveTypeDays======>"+hmLeaveTypeDays);
//			System.out.println("hmMonthlyLeaves=======>"+hmMonthlyLeaves);
			
			String strTotalDays = nTotalNumberOfDays+"";
			
			Map<String, String> hmTotalDays = new HashMap<String, String>();
			
//			Map hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
//			Map<String, String> hmHolidays = new HashMap<String, String>();
//			Map<String, String> hmHolidayDates = new HashMap<String, String>();
//			CF.getHolidayList(con, strD1, strD2, CF, hmHolidayDates, hmHolidays, true);
//			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request, uF,strD1, strD2);
			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request, uF,strD1, strD2);
			
			Map<String, String> hmLongLeaves = getLongLeavesCount(con,uF,CF,strD1,strD2,strPC);
			if(hmLongLeaves == null) hmLongLeaves = new HashMap<String, String>();
			
//			System.out.println("hmHolidayDates="+hmHolidayDates);
//			System.out.println("hmHolidays="+hmHolidays);
//			System.out.println("hmWeekEnds="+hmWeekEnds);
			
			/*Calendar cal1  = GregorianCalendar.getInstance();
			cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
			cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
			String strDateNew = uF.getDateFormat("01/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
			
			System.out.println("strDateNew=="+strDateNew);
			System.out.println("strD1=="+strD1);
			System.out.println("strD2=="+strD2);
			*/
//			Map hmWeekEnds = CF.getWeekEndDateList(con, strDateNew, strD2, CF, uF);
			
			Map<String, String> hmAttendanceDependent = CF.getAttendanceDependency(con);
			Map<String, String> hmRosterDependent = CF.getRosterDependency(con);
			Map<String,Map<String,String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String,String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String,String> hmEmpPaymentMode = CF.getEmpPaymentMode(con, uF);
			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String,String> hmFixedExemptions = getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
			List<String> alPresentEmpId = new ArrayList<String>();
			List<String> alPresentDates = new ArrayList<String>();
			List<String> alPresentWeekEndDates = new ArrayList<String>();
			List<String> alHalfDaysDueToLatePolicy = new ArrayList<String>();
			List<String> alServices = new ArrayList<String>();
			Map<String, List<String>> hmPresentDays = new HashMap<String, List<String>>();
			Map<String, List<String>> hmPresentWeekEndDays = new HashMap<String, List<String>>();
			Map<String, List<String>> hmHalfDays = new HashMap<String, List<String>>();
			Map<String, String> hmPresentDays1 = new HashMap<String, String>();
			Map<String, String> hmPaidDays = new HashMap<String, String>();
			Map<String, List<String>> hmServices = new HashMap<String, List<String>>();
			Map<String, String> hmHoursWorked = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpHoursWorked = new HashMap<String, Map<String, String>>();
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			
			Map<String,String> hmEmpRosterHours = new HashMap<String,String>();
			Map<String, Map<String,String>> hmEmpOverTimeHours=CF.getEmpOverTimeHours(con,CF,uF,strD1,strD2,strPC);
			Map<String, Map<String,String>> hmEmpOverTimeLevelPolicy=CF.getEmpOverTimeLevelPolicy(con,CF,uF,strD1,strD2,strPC);
			
			Map<String, String> hmHolidayCount = CF.getHolidayCount(con,CF,uF,strD1,strD2,strPC);
			
			List<String> alFullDaysDueToLatePolicy = new ArrayList<String>();
			Map<String, List<String>> hmFullDays = new HashMap<String, List<String>>();
			
			List<String> alEmp = new ArrayList<String>();
			
			Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and (employment_end_date is null OR employment_end_date >= ?) and joining_date<= ? and emp_per_id > 0 ");
			if(uF.parseToInt(strReqEmpId)>0) {
				sbQuery.append(" and emp_per_id = " + uF.parseToInt(strReqEmpId));
			}
			if(getLevel()!=null && getLevel().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getLevel(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
            
			if(getStrPaycycleDuration()!=null) {
				sbQuery.append(" and paycycle_duration ='"+getStrPaycycleDuration()+"'");
			}
			
			if(uF.parseToInt(getF_paymentMode())>0) {
				sbQuery.append(" and payment_mode ="+uF.parseToInt(getF_paymentMode()));
			}
			
			if(getwLocation()!=null && getwLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getwLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" and emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? " +
					"and paycycle = ? group by emp_id)");
			sbQuery.append(" order by emp_fname, emp_lname"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(strPC));
//			System.out.println("pst=====>"+pst); 
			rs = pst.executeQuery();
			StringBuilder sbEmPId = new StringBuilder();
			Map<String, String> hmEmpPaycycleDuration = new HashMap<String, String>();
			Map<String, String> hmEmpSalCalStatus = new HashMap<String, String>();
			List<String> alEmpJoinDate = new ArrayList<String>();
			List<String> alGradeId = new ArrayList<String>();
			while(rs.next()) {
				alEmp.add(rs.getString("emp_per_id"));
				sbEmPId.append(rs.getString("emp_per_id")+",");
				
				hmEmpPaycycleDuration.put(rs.getString("emp_per_id"), rs.getString("paycycle_duration"));
				
				if(rs.getString("joining_date")!=null) { 
					Date date = uF.getDateFormatUtil(rs.getString("joining_date"), DBDATE);
					if(uF.isDateBetween(sDate, eDate, date) ) {
						alEmpJoinDate.add(rs.getString("emp_per_id"));
					}
				}
				
				if (!alGradeId.contains(rs.getString("grade_id"))) {
					alGradeId.add(rs.getString("grade_id"));
				}
				hmEmpSalCalStatus.put(rs.getString("emp_per_id"), rs.getString("is_disable_sal_calculate"));
			}
			rs.close();
			pst.close(); 
			request.setAttribute("alEmpJoinDate", alEmpJoinDate);
//			System.out.println("alEmpJoinDate=====>"+alEmpJoinDate);
			
			if(sbEmPId.length()>1) {
				sbEmPId.replace(0, sbEmPId.length(), sbEmPId.substring(0, sbEmPId.length()-1));
			}
			
			/*if(strReqEmpId!=null) {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and emp_id =? order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpId));
			}else if(sbEmPId.length()>1) {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and emp_id in ("+sbEmPId.toString()+") order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			} else {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			}*/
//			System.out.println("strReqEmpId ===>> " + strReqEmpId);
			if(strReqEmpId!=null) {
//				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and emp_id =? order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id =? " +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+
						"order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strPC));
			}else if(sbEmPId.length()>1) {
//				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and emp_id in ("+sbEmPId.toString()+") order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and ad.emp_id in ("+sbEmPId.toString()+") order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				// Remove In_out='OUT'
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id in ("+sbEmPId.toString()+")" +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+		
						" order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPC));
			} else {
				//pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  " +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+
						" order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPC));
			}
			
			Map<String, String> hmOverLappingHolidays = new HashMap<String, String>();
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();  
			double dblOverLappingHolidays = 0;
			
			String strPresentEmpIdNew = null;
			String strPresentEmpIdOld = null;
			Set<String> tempweeklyOffSet=null;
			String level=null;
			String location=null;
			Set<String> tempholidaysSet=null;
			while(rs.next()) {
				strPresentEmpIdNew = rs.getString("emp_id");
				if(strPresentEmpIdNew!=null && !strPresentEmpIdNew.equalsIgnoreCase(strPresentEmpIdOld)) {
					alPresentEmpId = new ArrayList<String>();
					alPresentDates = new ArrayList<String>();
					alPresentWeekEndDates = new ArrayList<String>();
					alServices = new ArrayList<String>();
					hmHoursWorked = new HashMap<String, String>();
					halfDayCountIN = 0;
					halfDayCountOUT = 0;
					dblOverLappingHolidays = 0;
					alHalfDaysDueToLatePolicy = new ArrayList<String>();
					location = hmEmpWlocationMap.get(strPresentEmpIdNew);
					level=hmEmpLevelMap.get(strPresentEmpIdNew);
//					tempweeklyOffSet=hmWeekEnds.get(level);
					if(alEmpCheckRosterWeektype.contains(strPresentEmpIdNew)) {
						tempweeklyOffSet = hmRosterWeekEndDates.get(strPresentEmpIdNew);
					} else {
						tempweeklyOffSet = hmWeekEnds.get(location);
					}
					if(tempweeklyOffSet==null)tempweeklyOffSet=new HashSet<String>();
					
					Set<String> temp=holidaysMp.get(location);
					if(temp==null)temp=new HashSet<String>();
					tempholidaysSet=new HashSet<String>(temp);	
					tempholidaysSet.removeAll(tempweeklyOffSet);
//					if(strPresentEmpIdNew.equals("118")) {
//						System.out.println("tempweeklyOffSet==="+tempweeklyOffSet);
//						System.out.println("tempholidaysSet==="+tempholidaysSet);
//						System.out.println("level==="+level);
//
//					}
					
					alFullDaysDueToLatePolicy = new ArrayList<String>();
					fullDayCountIN=0;
					fullDayCountOUT=0;
				}
				
				if(!alPresentEmpId.contains(strPresentEmpIdNew)) {
					alPresentEmpId.add(strPresentEmpIdNew);
				}
				
				hmEmpRosterHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("emp_id"), rs.getString("actual_hours"));
				
				String strDay = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
				strDay = strDay.toUpperCase();
				
//				double dblEarlyLate = rs.getDouble("early_late"); 
				double dblEarlyLate = uF.parseToInt(rs.getString("approved"))== -1 ? rs.getDouble("early_late") : 0.0d; 
				double dblHDHoursWoeked = uF.parseToInt(rs.getString("approved"))!= -1 ? rs.getDouble("hours_worked") : 0.0d; 
				 
//				if(uF.parseToInt(rs.getString("emp_id")) == 11) {
//					System.out.println("inout====="+rs.getString("in_out")+"----"+strDate+" approved====>"+uF.parseToInt(rs.getString("approved"))+ "====dblEarlyLate====>"+dblEarlyLate+"----dblHDEarlyLate====>"+dblHDEarlyLate);
//				} 
				
				String strINOUT = rs.getString("in_out");
				Map<String, String> hmLeaves = hmLeaveDays.get(strPresentEmpIdNew);
				if(hmLeaves==null)hmLeaves = new HashMap<String, String>();
				
//				if(strPresentEmpIdNew.equals("118")) {
//					System.out.println("strDate==="+strDate);
//				}
				if(!alPresentDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
					/**
					 * To avoid the over presence data
					 */
					
//					Map hmLeaves = hmLeaveDays.get(strPresentEmpIdNew);
//					if(hmLeaves==null)hmLeaves = new HashMap();
					
//					String strWeekEnd = hmWeekEnds.get(strDay+"_"+strLocation);
					String strWeekEnd = null;
					
					if(tempweeklyOffSet.contains(strDate)) {
						strWeekEnd = WEEKLYOFF_COLOR;
					}
					
					if(strWeekEnd==null ) { //&& !hmLeaves.containsKey(strDate)
//					if(strWeekEnd==null && !hmLeaves.containsKey(strDate)) {
						alPresentDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}else if(!alPresentWeekEndDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						alPresentWeekEndDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}
					if(tempholidaysSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						dblOverLappingHolidays++;
					}
					
				}
				
				boolean isRosterDependent = uF.parseToBoolean(hmRosterDependent.get(strPresentEmpIdNew));
				
				if(dblEarlyLate > 0.0d) {
					if(isHalfDay(strDate, dblEarlyLate, strINOUT, hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alHalfDaysDueToLatePolicy!=null
							&& isRosterDependent
							&& !alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))
							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
					
						alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}
					
					if(!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						if(isFullDay(strDate, dblEarlyLate, strINOUT, (String)hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alFullDaysDueToLatePolicy!=null
								&& isRosterDependent
								&& !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))
								&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							alFullDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						}
					}
				}
				
				double x = Math.abs(dblHDHoursWoeked);
				if(x > 0.0d && x <=5) {
					if(!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						String strWLocationId = hmEmpWlocation.get(rs.getString("emp_id"));
						Set<String> weeklyOffSet= hmWeekEnds.get(strWLocationId);
						if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
						
						Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
						if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
						
						Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_id"));
						if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
						if(alEmpCheckRosterWeektype.contains(rs.getString("emp_id"))) {
							if(!rosterWeeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
								alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
							}
						} else if(weeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							
						} else if(holidaysMp.containsKey(uF.getDateFormat(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)) {
							
						} else {
							alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						}				
					}
				}
				
//				if(uF.parseToInt(rs.getString("emp_id")) == 11) {
//					System.out.println("alHalfDaysDueToLatePolicy====="+alHalfDaysDueToLatePolicy);
//				} 
				
				
				if(!alServices.contains(rs.getString("service_id"))) {  
					alServices.add(rs.getString("service_id"));
				} 
				
				hmPresentDays.put(strPresentEmpIdNew, alPresentDates);
				hmPresentWeekEndDays.put(strPresentEmpIdNew, alPresentWeekEndDates);
				hmHalfDays.put(strPresentEmpIdNew, alHalfDaysDueToLatePolicy);
				hmFullDays.put(strPresentEmpIdNew, alFullDaysDueToLatePolicy);
				
				hmServices.put(strPresentEmpIdNew, alServices);
				
				if("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
					hmHoursWorked.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id"), rs.getString("hours_worked"));
				}
				hmEmpHoursWorked.put(strPresentEmpIdNew, hmHoursWorked);
				hmOverLappingHolidays.put(strPresentEmpIdNew, dblOverLappingHolidays+"");
				
				strPresentEmpIdOld = strPresentEmpIdNew;
			}
			rs.close();
			pst.close();
			
//				System.out.println("strDate== vcvx="+hmPresentDays.get("118"));
			
			
			Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
			
			if(alGradeId.size() > 0) {
				String strGradeIds = StringUtils.join(alGradeId.toArray(), ",");
				pst = con.prepareStatement("select * from salary_details where level_id in (select level_id from level_details where " +
						"level_id in (select dd.level_id from designation_details dd, grades_details gd where dd.designation_id=gd.designation_id " +
						"and gd.grade_id in("+strGradeIds+"))) and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by level_id, earning_deduction desc, salary_head_id, weight");
				rs = pst.executeQuery(); 
				while (rs.next()) {
					
					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(rs.getString("level_id"));
					if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
					
					Map<String, String> hmInnerSal = new HashMap<String, String>();
					hmInnerSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
					hmInnerSal.put("SALARY_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmInnerSal.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
					hmInnerSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					hmInnerSal.put("IS_CTC_VARIABLE", ""+uF.parseToBoolean(rs.getString("is_ctc_variable")));
					hmInnerSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmInnerSal.put("IS_ALIGN_WITH_PERK", ""+uF.parseToBoolean(rs.getString("is_align_with_perk")));
					hmInnerSal.put("IS_DEFAULT_CAL_ALLOWANCE", ""+uF.parseToBoolean(rs.getString("is_default_cal_allowance")));
					hmInnerSal.put("SALARY_TYPE", rs.getString("salary_type"));
					
					hmSalInner.put(rs.getString("salary_head_id"), hmInnerSal);
					
					hmSalaryDetails1.put(rs.getString("level_id"), hmSalInner);
					
					if(uF.parseToInt(rs.getString("salary_head_id")) != GROSS && uF.parseToInt(rs.getString("salary_head_id")) != CTC && uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT_CTC) {
						if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
							int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
	
							if (index >= 0) {
								alEmpSalaryDetailsEarning.remove(index);
								alEarningSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							} else {
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}
	
							alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
							int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
							if (index >= 0) {
								alEmpSalaryDetailsDeduction.remove(index);
								alDeductionSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							} else {
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}
							alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						}
	
						hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					}
	
				}
				rs.close();
				pst.close();
			}
			
			
//			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
//			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
//			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
////			pst = con.prepareStatement("select * from salary_details order by weight");
//			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+"," + REIMBURSEMENT_CTC + ") and org_id =? and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
//			pst.setInt(1, uF.parseToInt(getF_org()));
////			System.out.println("pst======>"+pst);
//			rs = pst.executeQuery();  
//			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
//			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
//			while(rs.next()) {
//				
//				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
//					int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//					
////					System.out.println("earning_deduction====>"+rs.getString("earning_deduction"));
////					System.out.println("alEmpSalaryDetailsEarning====>"+alEmpSalaryDetailsEarning);
////					System.out.println("alEarningSalaryDuplicationTracer====>"+alEarningSalaryDuplicationTracer);
////					System.out.println("index====>"+index);
////					System.out.println("salary_head_id====>"+rs.getString("salary_head_id"));
//					
//					if(index>=0) {
//						alEmpSalaryDetailsEarning.remove(index);
//						alEarningSalaryDuplicationTracer.remove(index);
////						alEmpSalaryDetailsEarning.add(index, rs.getString("salary_head_id"));
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					} else {
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					}
//					
//					alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
//					int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//					if(index>=0) {
//						alEmpSalaryDetailsDeduction.remove(index);
//						alDeductionSalaryDuplicationTracer.remove(index);
////						alEmpSalaryDetailsDeduction.add(index, rs.getString("salary_head_id"));
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					} else {
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					}
//					alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}
//				
//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
//			}
//			rs.close();
//			pst.close();
			
//			pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			
			Map<String, Map<String, String>> hmEmpSalary = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmEmpSalaryInner = new LinkedHashMap<String, String>();
			if(uF.parseToInt(getF_org())>0) {
//				pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation2);
				sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_salary_details esd, (select max(effective_date) as effective_date, emp_id from emp_salary_details " +
					"where effective_date<=? and is_approved= true group by emp_id) a where a.effective_date = esd.effective_date and esd.emp_id = a.emp_id " +
					"and a.emp_id in (select emp_id from employee_official_details where org_id = ? ");
				if(uF.parseToInt(strReqEmpId)>0) {
					sbQuery.append(" and emp_id ="+uF.parseToInt(strReqEmpId));
				}
				sbQuery.append(" ) and esd.salary_head_id in (select salary_head_id from salary_details where org_id =? and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false)) " +
					"order by a.emp_id, esd.earning_deduction desc");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(getF_org()));
				pst.setInt(3, uF.parseToInt(getF_org()));
			} else {
//				pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation1);
				sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_salary_details esd, (select max(effective_date) as effective_date, emp_id " +
					"from emp_salary_details where effective_date<=? and is_approved= true group by emp_id ) a where a.effective_date = esd.effective_date " +
					"and esd.emp_id = a.emp_id ");
				if(uF.parseToInt(strReqEmpId)>0) {
					sbQuery.append(" and a.emp_id ="+uF.parseToInt(strReqEmpId));
				}
				sbQuery.append(" and esd.salary_head_id in (select salary_head_id from salary_details where (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false))" +
					" order by a.emp_id, esd.earning_deduction desc");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));	
			}
			rs = pst.executeQuery();
//			System.out.println("----------_>"+pst);
			String strEmpIdNew1 = null;
			String strEmpIdOld1 = null;
			while(rs.next()) {
//				strEmpIdNew1 = rs.getString("empl_id");
				strEmpIdNew1 = rs.getString("emp_id");
						
				if(!alEmp.contains(strEmpIdNew1))continue;		
						
				if(strEmpIdNew1!=null && !strEmpIdNew1.equalsIgnoreCase(strEmpIdOld1)) {
					hmEmpSalaryInner = new LinkedHashMap<String, String>();
				}
				
//				Map<String, String> hmEmpInner = new HashMap<String, String>();
				
				/*
				hmEmpInner.put("SALARY_HEAD_ID", rs.getString("salary_id"));
				hmEmpInner.put("SERVICE_ID", rs.getString("service_id"));
				hmEmpInner.put("AMOUNT", rs.getString("amount"));
				hmEmpInner.put("PAY_TYPE", rs.getString("pay_type"));
				hmEmpInner.put("SALARY_HEAD_NAME", rs.getString("salary_head_name"));
				hmEmpInner.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
				hmEmpInner.put("SALARY_HEAD_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
				hmEmpInner.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
				hmEmpInner.put("SUB_SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
				
				hmEmpSalaryInner.put(rs.getString("salary_id"), hmEmpInner);
//				hmEmpPresentDays.put(strEmpIdNew1, nPresentDays+"");
				*/
				if(strEmpIdNew1!=null && strEmpIdNew1.length()>0) {
					hmEmpSalary.put(strEmpIdNew1, hmEmpSalaryInner);
				}
				
				strEmpIdOld1 = strEmpIdNew1;
				
//				if(!hmSalaryDetails.containsKey(rs.getString("salary_id"))) {
					//hmSalaryDetails.put(rs.getString("salary_id"), rs.getString("salary_head_name"));
//				}
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
			if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
			
			Map<String, String> hmInnerTemp = new HashMap<String, String>();
			
			Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF); 
			Map<String, String> hmIncentives = CF.getIncentivesMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualBonus = CF.getIndividualBonusMap(con, uF, CF, strPC, strD1, strD2);
			if(hmIndividualBonus == null) hmIndividualBonus = new HashMap<String, String>();
			
			Map<String, String> hmIndividualOtherDeduction = CF.getIndividualOtherDeductionMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualOtherEarning = CF.getIndividualOtherEarningMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualOvertime = CF.getIndividualOvertimeMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualTravelReimbursement = CF.getIndividualTravelReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualMobileReimbursement = CF.getIndividualMobileReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualOtherReimbursement = CF.getIndividualOtherReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualMobileRecovery = CF.getIndividualMobileRecoveryMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmReimbursement = CF.getReimbursementMap(con, uF, CF, strD1, strD2);
			Map<String, String> hmVariables  = new HashMap<String, String>();
			Map<String, String> hmBreaks  = new HashMap<String, String>();
			Map<String, String> hmBreakPolicy  = new HashMap<String, String>();
			getVariableAmount(con, uF, hmVariables, strPC);
			getBreakDetails(con, uF, hmBreaks, hmBreakPolicy, strD1, strD2);
			
			Map<String, String> hmAnnualVariables = new HashMap<String, String>();
			getAnnualVariableAmount(con, uF, hmAnnualVariables, strD1, strD2, strPC);
			
			Map<String, String> hmAllowance  = new HashMap<String, String>();
			getAllowanceAmount(con, uF, hmAllowance, strD1, strD2, strPC);
			
			Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
			Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
			
			getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
			
			Map<String, String> hmAnnualVarPolicyAmount = CF.getAnnualVariablePolicyAmount(con, uF, strFinancialYearStart, strFinancialYearEnd);
			if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();
			
			Map<String, String> hmBasicSalaryMap = CF.getSpecificSalaryData(con, BASIC);
			Map<String, String> hmDASalaryMap = CF.getSpecificSalaryData(con, DA);
			Set<String> setLeaves = null;
			Iterator<String> it = null;
			
 			List<String> alProcessingEmployee = new ArrayList<String>();
			
			LinkedHashMap<String, Map<String, String>> hmTotalSalary = new LinkedHashMap<String, Map<String, String>>();
			LinkedHashMap<String, Map<String, String>> hmTotalSalaryisDisplay = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmLoanAmt=new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
			List<String> alLoans = new ArrayList<String>();
			
			Map<String, String> hmWoHLeaves  = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			Set<String> set0 = hmEmpSalary.keySet();
			Iterator<String> it0 = set0.iterator();
			while(it0.hasNext()) {
				String strEmpId = it0.next();
				String strOrgId = hmEmpOrgId.get(strEmpId);
				int nEmpId = uF.parseToInt(strEmpId);
				String strLocation=hmEmpWlocationMap.get(strEmpId);
				String strLevel=hmEmpLevelMap.get(strEmpId);
				
				String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
				
//				Set<String> weeklyOffSet=hmWeekEnds.get(strLevel);
//				if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
				
				Set<String> weeklyOffSet = null;
				if(alEmpCheckRosterWeektype.contains(strEmpId)) {
					weeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
				} else {
					weeklyOffSet = hmWeekEnds.get(strLocation);
				}
				if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
				
				Set<String> OriginalholidaysSet=holidaysMp.get(strLocation);	
				if(OriginalholidaysSet==null)OriginalholidaysSet=new HashSet<String>();
//				} else {
				
				Set<String> holidaysSet=new HashSet<String>(OriginalholidaysSet);	
				
				holidaysSet.removeAll(weeklyOffSet);
				
				if(!alProcessingEmployee.add(strEmpId)) {
					alProcessingEmployee.add(strEmpId);
				}
				
//				System.out.println("hmPresentDays====>"+hmPresentDays);
				
				List<String> alPresentTemp = hmPresentDays.get(strEmpId);
				if(alPresentTemp==null)alPresentTemp = new ArrayList<String>();
				
				List<String> alServiceTemp = hmServices.get(strEmpId);
				if(alServiceTemp==null)alServiceTemp = new ArrayList<String>();
				
				double  dblPresent = alPresentTemp.size() - uF.parseToDouble(hmHolidayCount.get(strEmpId));
				
				List<String> alHalfDaysDueToLatePolicyTemp =hmHalfDays.get(strEmpId);
				if(alHalfDaysDueToLatePolicyTemp==null)alHalfDaysDueToLatePolicyTemp = new ArrayList<String>();
//				if(uF.parseToInt(strEmpId) == 11) {
//					System.out.println("dblPresent====="+dblPresent+"---alHalfDaysDueToLatePolicyTemp====="+alHalfDaysDueToLatePolicyTemp);
//				} 
				dblPresent -=alHalfDaysDueToLatePolicyTemp.size() * 0.5;
//				if(uF.parseToInt(strEmpId) == 11) {
//					System.out.println("after dblPresent====="+dblPresent);
//				} 
				List<String> alFullDaysDueToLatePolicyTemp = (List<String>)hmFullDays.get(strEmpId);
				if(alFullDaysDueToLatePolicyTemp==null)alFullDaysDueToLatePolicyTemp = new ArrayList<String>();
				
				dblPresent -=alFullDaysDueToLatePolicyTemp.size() * 1;
				
				Map<String,String> hmLeaves = hmLeaveDays.get(strEmpId);
				if(hmLeaves==null)hmLeaves = new HashMap<String,String>();
				
				Map<String,String> hmLeavesType = hmLeaveTypeDays.get(strEmpId);
				if(hmLeavesType==null)hmLeavesType = new HashMap<String,String>();

//				System.out.println("strEmpId= 123 ==>"+strEmpId);
//				
//				System.out.println("hmLeaves===>"+hmLeaves);
//				System.out.println("hmLeavesType===>"+hmLeavesType);

				EmployeeActivity obj = new EmployeeActivity();
				obj.request = request;
				obj.session = session;
				obj.CF = CF;
				
//				Map hmBasicSalaryMap = CF.getSpecificSalaryData(BASIC);
//				Map hmDASalaryMap = CF.getSpecificSalaryData(DA);
				
				
				setLeaves = hmLeaves.keySet();
				it = setLeaves.iterator();
//				int nLeaves = 0;
				double nOverlappingHolidaysLeaves = 0;
				double nOverlappingWeekEndsLeaves = 0;
				while(it.hasNext()) {
					String strLeaveDate = it.next();
//					String strHolidayDate = hmHolidayDates.get(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strLocation);
//					holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat()));
					String strWeekEnd = null;
					
					
					if( weeklyOffSet.contains(strLeaveDate)) {
						strWeekEnd = WEEKLYOFF_COLOR;
					}
					
					
					String strLeaveType = hmLeavesType.get(strLeaveDate);
					
					
//					System.out.println("strLeaveType===>"+strLeaveType);
//					System.out.println("strLeaveDate===>"+strLeaveDate);
//					System.out.println("hmLeavesType===>"+hmLeavesType);
//					System.out.println("alPresentTemp===>"+alPresentTemp);
					
					
//					if(strLeaveDate!=null && !strLeaveDate.equals(strHolidayDate) && !alPresentTemp.contains(strLeaveDate)) {
//						nLeaves++;
//					}
						
//					if(strLeaveDate!=null && strLeaveDate.equals(strHolidayDate)) {
//						nOverlappingHolidaysLeaves++;
//					}
					
//					if(strLeaveDate!=null && holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat())) && "H".equalsIgnoreCase(strLeaveType)) {
					if(strLeaveDate!=null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
						nOverlappingHolidaysLeaves+=0.5;
//					}else if(strLeaveDate!=null && holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat()))) {
					}else if(strLeaveDate!=null && holidaysSet.contains(strLeaveDate)) {						
						nOverlappingHolidaysLeaves++;
					}
					
					if(strLeaveDate!=null && strWeekEnd!=null) {
						nOverlappingWeekEndsLeaves++;
					}
					
					  
//					if(uF.parseToInt(strEmpId) == 11) {
//						System.out.println(strLeaveType+"---before leave dblPresent====="+dblPresent);
//					} 
					if(strLeaveDate!=null && alPresentTemp.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
//					if(strLeaveDate!=null && "H".equalsIgnoreCase(strLeaveType)) {
						//dblPresent += -1 + 0.5;
					}
//					if(uF.parseToInt(strEmpId) == 11) {
//						System.out.println(strLeaveType+"---after leave dblPresent====="+dblPresent);
//					} 
					
				}
				
				
				
				int nHolidays = holidaysSet.size();
//				int nWeekEnds = uF.getWeekEndCount(hmWeekEnds,strLocation, strD1, strD2);
//				int nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
				int nWeekEnds = 0;
/*
				if("459".equals(strEmpId)) {
					System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap);
					System.out.println("contains="+hmEmpJoiningMap.containsKey(strEmpId));
					System.out.println("Date 1="+uF.getDateFormatUtil(strD1, DATE_FORMAT));
					System.out.println("Date 2="+uF.getDateFormatUtil(strD2, DATE_FORMAT));
					System.out.println("Date 3="+uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT));
					System.out.println("isBetween="+uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT)));
				}*/
				
				if(hmEmpEndDateMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT))) {
					Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
					Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con, strD1,hmEmpEndDateMap.get(strEmpId), CF, uF,hmWeekEndHalfDates1,null);
					List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
					Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
					CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1,hmEmpEndDateMap.get(strEmpId),alEmpCheckRosterWeektype1,hmRosterWeekEndDates1,hmWeekEnds1,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates1);
					
//					Set<String> weeklyOffEndDate= hmWeekEnds1.get(strLevel);
					Set<String> weeklyOffEndDate = null;
					if(alEmpCheckRosterWeektype1.contains(strEmpId)) {
						weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
					} else {
						weeklyOffEndDate = hmWeekEnds1.get(strLocation);
					}
					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					
					nWeekEnds = weeklyOffEndDate.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("1---nWeekEnds==="+nWeekEnds);
//					}
					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
					
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con, uF,strD1, hmEmpEndDateMap.get(strEmpId));
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,strD1, strD2); 
					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,strD1, hmEmpEndDateMap.get(strEmpId)); 
					Set<String> OriginalholidaysSet1=holidaysMp1.get(strLocation);	
					if(OriginalholidaysSet1==null)OriginalholidaysSet1=new HashSet<String>();
					Set<String> holidaysSet1=new HashSet<String>(OriginalholidaysSet1);	
					holidaysSet1.removeAll(weeklyOffEndDate);
					
					nHolidays = holidaysSet1.size();
					
//					System.out.println("end date nWeekEnds====>"+nWeekEnds);
//					System.out.println("end date hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("end date strEmpId=====>"+strEmpId);
//					System.out.println("end date nHolidays=====>"+nHolidays);
					
				}else if(hmEmpJoiningMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT))) {
					Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
					Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con,hmEmpJoiningMap.get(strEmpId),strD2, CF, uF,hmWeekEndHalfDates1,null);
//					Set<String> weeklyOffEndDate= hmWeekEnds1.get(strLevel);
//					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
					Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
					CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, hmEmpJoiningMap.get(strEmpId),strD2,alEmpCheckRosterWeektype1,hmRosterWeekEndDates1,hmWeekEnds1,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates1);
					
					Set<String> weeklyOffEndDate = null;
					if(alEmpCheckRosterWeektype1.contains(strEmpId)) {
						weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
					} else {
						weeklyOffEndDate = hmWeekEnds1.get(strLocation);
					}
					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					
					
					nWeekEnds = weeklyOffEndDate.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("2---nWeekEnds==="+nWeekEnds+"---hmEmpJoiningMap.get(strEmpId)=====>"+hmEmpJoiningMap.get(strEmpId));
//						System.out.println("2---alEmpCheckRosterWeektype1.contains(strEmpId)==="+alEmpCheckRosterWeektype1.contains(strEmpId)+"---weeklyOffEndDate=====>"+weeklyOffEndDate);
//						System.out.println("2---hmRosterWeekEndDates1==="+hmRosterWeekEndDates1.get(strEmpId)+"---hmWeekEnds1.get(strLocation)=====>"+hmWeekEnds1.get(strLocation));
//					}
//					if("554".equals(strEmpId)) {
//						System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap.get(strEmpId));
//
//						System.out.println("nWeekEnds="+nWeekEnds);
//						System.out.println("weeklyOffEndDate="+weeklyOffEndDate);
//					}
					
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds1,strLocation, hmEmpJoiningMap.get(strEmpId), strD2);
					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con, hmEmpJoiningMap.get(strEmpId), strD2, CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
					
					
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con, uF,hmEmpJoiningMap.get(strEmpId), strD2);
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request, uF,strD1, strD2);
					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,hmEmpJoiningMap.get(strEmpId), strD2);
					Set<String> OriginalholidaysSet1=holidaysMp1.get(strLocation);	
					if(OriginalholidaysSet1==null)OriginalholidaysSet1=new HashSet<String>();
					Set<String> holidaysSet1=new HashSet<String>(OriginalholidaysSet1);	
					holidaysSet1.removeAll(weeklyOffEndDate);
					if("554".equals(strEmpId)) {
//						System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap.get(strEmpId));

//						System.out.println("holidaysSet1="+holidaysSet1);
//						System.out.println("weeklyOffEndDate="+weeklyOffEndDate);
					}
					nHolidays =holidaysSet1.size();
					
//					System.out.println("start date nWeekEnds====>"+nWeekEnds);
//					System.out.println("start date hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("start date strEmpId=====>"+strEmpId);
//					System.out.println("start date nHolidays=====>"+nHolidays);
					
				} else {
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					
					nWeekEnds=weeklyOffSet.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("3---nWeekEnds==="+nWeekEnds);
//					}
//					System.out.println("nWeekEnds====>"+nWeekEnds);
//					System.out.println("hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("strEmpId=====>"+strEmpId);
//					System.out.println("strD1=====>"+strD1);
//					System.out.println("strD2====>"+strD2);
					
					
//					hmHolidays = new HashMap<String, String>();
//					hmHolidayDates = new HashMap<String, String>();
//					CF.getHolidayList(strD1, strD2, CF, hmHolidayDates, hmHolidays, hmWeekEnds, true);
//					nHolidays = uF.parseToInt(hmHolidays.get(strLocation));
				}
				
				/*int nWeekEnds = 0;
				if(strEmpId.equalsIgnoreCase("460")) {
					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
				}*/
				
				List<String> alWorkingWeekEnds = hmPresentWeekEndDays.get(strEmpId);
				if(alWorkingWeekEnds==null)alWorkingWeekEnds=new ArrayList<String>();
				int nWorkingWeekEnds = alWorkingWeekEnds.size();
				
				List<String> alOverlappingWeekEndDates = hmPresentWeekEndDays.get(strEmpId);
				if(alOverlappingWeekEndDates==null)alOverlappingWeekEndDates = new ArrayList<String>();
//				int nOverlappingWeekends = alOverlappingWeekEndDates.size();
				
				double dblOverlappingHolidays = uF.parseToDouble(hmOverLappingHolidays.get(strEmpId));
				
				double dblTotalLeaves =  uF.parseToDouble(hmLeavesType.get("COUNT"));
				double dblActualLeaves =  dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//				System.out.println(strEmpId+"====dblTotalLeaves======>"+dblTotalLeaves);
//				System.out.println(strEmpId+"====dblActualLeaves======>"+dblActualLeaves);
//				System.out.println(strEmpId+"====nOverlappingHolidaysLeaves======>"+nOverlappingHolidaysLeaves);
//				System.out.println(strEmpId+"====nOverlappingWeekEndsLeaves======>"+nOverlappingWeekEndsLeaves);
				
				/**
				 * Long Leave code start
				 * */
				double dblLongLeave = uF.parseToDouble(hmLongLeaves.get(strEmpId));
//				if(strEmpId.equals("459")) {
//					System.out.println("dblLongLeave======>"+dblLongLeave);
//					System.out.println("before dblPresent======>"+dblPresent);
//					System.out.println("before dblTotalLeaves======>"+dblTotalLeaves);
//					System.out.println("before dblActualLeaves======>"+dblActualLeaves);
//				}
				dblTotalLeaves = dblTotalLeaves - dblLongLeave; 
				hmLeavesType.put("COUNT", ""+dblTotalLeaves);
//				if(strEmpId.equals("459")) {
//					System.out.println("after dblTotalLeaves======>"+dblTotalLeaves);
//				}
				/**
				 * Long Leave code end
				 * */
				
//				double dblTotalPgetEmpServiceTaxresentDays = dblPresent + dblActualLeaves + nHolidays + nWeekEnds;
				
//				double dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves ;
//				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves) +"");
				double dblTotalPresentDays = 0.0d;
				if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
					dblTotalPresentDays = 0.0d;
					hmPresentDays1.put(strEmpId, "0");
				} else {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves ;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves) +"");
				}
//				if(strEmpId.equals("225"))      {               
//					System.out.println("1 dblPresent==="+dblPresent+"---nHolidays==="+nHolidays+"---dblOverlappingHolidays==="+dblOverlappingHolidays+"---nWeekEnds==="+nWeekEnds+"---dblTotalLeaves==="+dblTotalLeaves+"---nOverlappingHolidaysLeaves==="+nOverlappingHolidaysLeaves+"---nOverlappingWeekEndsLeaves==="+nOverlappingWeekEndsLeaves);
//					System.out.println("1 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("1 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}

				if(dblTotalPresentDays>nTotalNumberOfDays) {
					dblTotalPresentDays = nTotalNumberOfDays;
					
				}
//				if(strEmpId.equals("225"))      {               
//					System.out.println("2 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("2 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}
				
				/*https://www.arclab.com/en/amlc/list-of-smtp-and-pop3-servers-mailserver-list.html

				// hard coded condition and logic needs to be implemented for this condition. --Vipin 21-01-2013
				if(dblPresent>=22) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds)+"");
				}else if(dblPresent>=20) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +4 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +4- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=14) {
					dblTotalPresentDays = dblPresent +3 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent  +3- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=8) {
					dblTotalPresentDays = dblPresent +2 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +2- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=4) {
					dblTotalPresentDays = dblPresent  +1 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +1- nOverlappingHolidaysLeaves)+"");
				} else {
					dblTotalPresentDays = dblPresent  + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent  - nOverlappingHolidaysLeaves)+"");
				} getEmpServiceTax
				*/
				
				// FOR KP
				 
				/*// hard coded condition and logic needs to be implemented for this condition. --Vipin 21-01-2013
				if(dblPresent>15) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=8) {
					dblTotalPresentDays = dblPresent +5 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +5- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=4) {
					dblTotalPresentDays = dblPresent  +1 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +1- nOverlappingHolidaysLeaves)+"");
				} else {
					dblTotalPresentDays = dblPresent  + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent - nOverlappingHolidaysLeaves )+"");
				}
				
				*/
				
				//comment for solar start 
				
//				Calendar calW = GregorianCalendar.getInstance();
//				Calendar calW1 = GregorianCalendar.getInstance();
//				Calendar calW2 = GregorianCalendar.getInstance();
//				calW.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//				calW.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
//				calW.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//			
//				int nDiff = uF.parseToInt(uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
//				int nDeductionW = 0;
//				List alPW = hmPresentDays.get(strEmpId);
//				if(alPW==null)alPW=new ArrayList();
//				
//				List alPWE = hmPresentWeekEndDays.get(strEmpId);
//				if(alPWE==null)alPWE=new ArrayList();
//				
//				
//*/				 
//				
//				
//				for(int i=0; i<nDiff; i++) {
//					String strDW = uF.getDateFormat(calW.get(Calendar.DATE)+"/"+(calW.get(Calendar.MONTH)+1)+"/"+calW.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//					String strDH = uF.getDateFormat(calW.get(Calendar.DATE)+"/"+(calW.get(Calendar.MONTH)+1)+"/"+calW.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//					calW1 = (Calendar)calW.clone();
//					  
//					
//					
//					
//					
//					if(hmWeekEnds.containsKey(strDW+"_"+strLocation) && i>0) {
//						  
//						calW1.add(Calendar.DATE, -1);
//						strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//						
//						if(!hmWeekEnds.containsKey(strDW+"_"+strLocation) && !alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//							
//							calW1.add(Calendar.DATE, 2);
//							strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//							
//							
//							if(hmWeekEnds.containsKey(strDW+"_"+strLocation)) {
//								
//								calW1.add(Calendar.DATE, 1);
//								strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//								
//								if(!alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//									nDeductionW+=2;
//									
//									
//								}
//								
//							} else {
//								
//								if(!alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//									nDeductionW+=1;
//									
//								}
//							}
//							
//						}
//						
//						
//					}
//					
//					
//					
//					
//				/*	
//					
//					
//					calW1 = (Calendar)calW.clone();
//					if(hmHolidayDates.containsKey(strDH+"_"+strLocation) && alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && i>0) {
//						calW1.add(Calendar.DATE, -1);
//						strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//						
//						if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))  && !hmLeaves.containsKey(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && !hmHolidayDates.containsKey(strDH+"_"+strLocation) && !hmWeekEnds.containsKey(strDH+"_"+strLocation)) {
//							calW1.add(Calendar.DATE, 1);
//							strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//							if(hmHolidayDates!=null && hmHolidayDates.containsKey(strDH+"_"+strLocation) && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))) {
//								nDeductionW+=1;
//								
//								
//							}
//						}else if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && !hmLeaves.containsKey(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && hmHolidayDates!=null && hmHolidayDates.containsKey(strDH+"_"+strLocation)) {
//							calW1.add(Calendar.DATE, -1);
//							strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//							if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))) {
//								nDeductionW+=1;
//							  
//								
//							}  
//						}
//					}
//					*/
//					
//					
//					
//					
//					
//					calW2 = (Calendar)calW.clone();
//					strDH = uF.getDateFormat(calW2.get(Calendar.DATE)+"/"+(calW2.get(Calendar.MONTH)+1)+"/"+calW2.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//					
////					if(hmLeaves.containsKey(strDH) && hmWeekEnds.containsKey(strDH+"_"+strLocation)) {
////						dblOverlappingHolidays++;
////						/*if(strEmpId.equalsIgnoreCase("652")) {
////							System.out.println("strDH= 5 ="+strDH);
////						}*/
////					}
//					
//					
//					
//					calW.add(Calendar.DATE, 1);
//				}
//				  
//		
//				if(dblPresent>0 || dblActualLeaves>0) {
////					if(dblPresent>0 && (dblPresent+dblActualLeaves)<6) {
//					if(dblPresent>0 && (dblPresent+dblTotalLeaves)<6) {
//						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + dblActualLeaves +nWeekEnds;
//						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nOverlappingHolidaysLeaves +nWeekEnds)+"");
//						
//						dblTotalPresentDays = dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves + dblTotalLeaves - nDeductionW ;
//						hmPresentDays1.put(strEmpId, (dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves - nDeductionW)+"");
//						
//					}else if((dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves)>nDeductionW) {
////						dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves - nDeductionW ;
////						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds- nOverlappingHolidaysLeaves - nDeductionW)+"");
//						
//						dblTotalPresentDays = dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves + dblTotalLeaves - nDeductionW ;
//						hmPresentDays1.put(strEmpId, (dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves - nDeductionW)+"");
//					} else {
//						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + dblActualLeaves ;
//						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nOverlappingHolidaysLeaves )+"");
//					}
//					
//				} else {
//					dblTotalPresentDays = 0 ;
//					hmPresentDays1.put(strEmpId, 0+"");
//				}
//				
//				
//				
//				
//				
//				
//				
//				
//				// Exclusive condition for KPCA urgent basis
//				/**
//				 * Exclusive condition for KPCA urgent basis
//				 * Needs to be replaced with the sandwich leave logic
//				 * @author Vipin Razdan (28-Nov-2013)
//				 * 
//				 * */
//
				//comment for solar end
				
				/*
				if(true) { // if daily calculation employees
					dblTotalPresentDays = dblPresent + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
				}
				*/
				// For ANC all daily employees are calculated Overtime differently
				if(hmEmpPaycycleDuration.get(strEmpId)!=null && !hmEmpPaycycleDuration.get(strEmpId).equalsIgnoreCase("M")) {
//					dblTotalPresentDays = dblPresent + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
					if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
						dblTotalPresentDays = 0.0d;
						hmPresentDays1.put(strEmpId, "0");
					} else {
						dblTotalPresentDays = dblPresent + dblActualLeaves;
						hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
					}
				} 
//				if(strEmpId.equals("225"))      {               
//					System.out.println("3 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("3 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}
				
				if(uF.parseToBoolean(CF.getIsPaycycleAdjustment())) {
					Calendar calMonth1 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
					calMonth1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
					calMonth1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
					calMonth1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
					
					Calendar calMonth2 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
					calMonth2.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
					calMonth2.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
					calMonth2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
					
					int nMonth1 = calMonth1.getActualMaximum(Calendar.DATE);
					int nMonth2 = calMonth2.getActualMaximum(Calendar.DATE); 
					
					dblPresent += (nMonth2 - nMonth1);
					
//					dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves)+"");
					if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
						dblTotalPresentDays = 0.0d;
						hmPresentDays1.put(strEmpId, "0");
					} else {
						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves)+"");
					}
//					if(strEmpId.equals("225")) {               
//						System.out.println("4 dblTotalPresentDays==="+dblTotalPresentDays);
//						System.out.println("4 nTotalNumberOfDays==="+nTotalNumberOfDays);
//					}
				}
				
				int nTotalNumberOfDaysForCalc = nTotalNumberOfDays; 
				
				/**   AWD  = Actual Working Days
				 * */
				
//				if("AWD".equalsIgnoreCase(CF.getStrOSalaryCalculationType())) {
				if("AWD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
//					dblTotalPresentDays = dblPresent + dblTotalLeaves;
					
					if(dblPresent>0) {
//						dblPresent = dblPresent + nHolidays ;
						dblPresent = dblPresent;
//					} else {
//						dblPresent = dblPresent ;
					}
					
					/**actual paid leaves
					 * */
					double dblWoHLwaves = dblTotalLeaves;
					hmWoHLeaves.put(strEmpId, ""+dblWoHLwaves);
					
					dblTotalLeaves = (dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves); 
					hmLeavesType.put("COUNT", ""+dblTotalLeaves);
					
					//dblPresent = (dblPresent - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves);
					
					dblTotalPresentDays = dblPresent + dblTotalLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent)+"");
					
//					strTotalDays = (nTotalNumberOfDays - nHolidays - nWeekEnds)+"";
//					System.out.println("hmLeaveTypeDays==="+hmLeavesType);
//					System.out.println("dblTotalLeaves==="+dblTotalLeaves+" dblActualLeaves="+dblActualLeaves+" nOverlappingHolidaysLeaves==="+nOverlappingHolidaysLeaves);
					
//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nHolidays - nWeekEnds; 

//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nWeekEnds;
//					int nWeekEnds1 = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					int nWeekEnds1 = weeklyOffSet.size();

//					System.out.println("strEmpId==="+strEmpId);
//					System.out.println("nTotalNumberOfDays==="+nTotalNumberOfDays);
//					System.out.println("nWeekEnds1==="+nWeekEnds1);
//					System.out.println("nHolidays==="+nHolidays);
					
//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nWeekEnds1; 
					nTotalNumberOfDaysForCalc = (nTotalNumberOfDays - nWeekEnds1) - nHolidays;
					
//					System.out.println("nWeekEnds1====>"+nWeekEnds1);
//					System.out.println("nWeekEnds====>"+nWeekEnds);
//					System.out.println("strEmpId====>"+strEmpId);
//					System.out.println("nTotalNumberOfDays====>"+nTotalNumberOfDays);
//					System.out.println("nTotalNumberOfDaysForCalc=====>"+nTotalNumberOfDaysForCalc);
					
					strTotalDays = nTotalNumberOfDaysForCalc +"";
					
//				}else if("AFD".equalsIgnoreCase(CF.getStrOSalaryCalculationType())) {
				}else if("AFD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
					if(dblPresent>0) {
						dblPresent = dblPresent + nHolidays ;
//					} else {
//						dblPresent = dblPresent ;
					}
//					nTotalNumberOfDaysForCalc = uF.parseToInt(CF.getStrOSalaryCalculationDays());
//					strTotalDays = CF.getStrOSalaryCalculationDays();
					nTotalNumberOfDaysForCalc = uF.parseToInt(hmOrg.get("ORG_SALARY_FIX_DAYS"));
					strTotalDays = uF.showData(hmOrg.get("ORG_SALARY_FIX_DAYS"), "");
					
//					int nWeekEnds1 = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					int daysDiff = nTotalNumberOfDays - nTotalNumberOfDaysForCalc; 	
					dblTotalPresentDays=dblTotalPresentDays-daysDiff;
					
//					if(strEmpId.equals("65")) {
//					System.out.println("daysDiff==="+daysDiff);
//					System.out.println("dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("dblPresent==="+dblPresent);
//					}
					hmPresentDays1.put(strEmpId,(uF.parseToDouble(hmPresentDays1.get(strEmpId))-daysDiff)+"");
//					hmPresentDays1.put(strEmpId, (dblPresent-daysDiff)+"");
				}
				
				hmTotalDays.put(strEmpId, strTotalDays);
//				System.out.println("dblTotalLeaves=>"+dblTotalLeaves);
//				System.out.println("nOverlappingHolidaysLeaves=>"+nOverlappingHolidaysLeaves);
//				System.out.println("nPresent=>"+dblPresent);
//				System.out.println("dblActualLeaves=>"+dblActualLeaves);
//				System.out.println("nHolidays=>"+nHolidays);
//				System.out.println("nWeekEnds=>"+nWeekEnds);
//				System.out.println("dblTotalPresentDays=>"+dblTotalPresentDays);
				
//				log.debug(" strEmpId= 123 =>"+strEmpId);
//				log.debug(" nHolidays==>"+nHolidays);
//				log.debug(" hmHolidays==>"+hmHolidays);
//				log.debug(" strLocation==>"+strLocation);
				
				/**
				 * 
				 * Prem Motors Break Policies
				 * 
				 * */
				
				int nBreaks = uF.parseToInt(hmBreaks.get(strEmpId+"_-2"));
//				double dblAmt = uF.parseToDouble(hmBreakPolicy.get("-2_"+getF_org()));
//				dblTotalPresentDays -= (nBreaks * dblAmt); 
//				if(nBreaks>0) {
//					hmPresentDays1.put(strEmpId, (dblTotalPresentDays)+"");
//				}
				
				/**
				 *   The attendance dependency calculation is for those employees who are not 
				 *   attendance dependent and will get the full salary irrespective they clocking on.
				 */
				
				boolean isAttendance = uF.parseToBoolean(hmAttendanceDependent.get(strEmpId));
				if(!isAttendance) {
//					dblTotalPresentDays = nTotalNumberOfDays;
					dblTotalPresentDays = nTotalNumberOfDaysForCalc;
//					if(strEmpId.equals("65")) {
//						System.out.println("dblTotalPresentDays"+dblTotalPresentDays);
//					}
//					if(strEmpId.equals("225")) {               
//						System.out.println("5 dblTotalPresentDays==="+dblTotalPresentDays);
//						System.out.println("5 nTotalNumberOfDays==="+nTotalNumberOfDays);
//					}
				}
				
				hmPaidDays.put(strEmpId, dblTotalPresentDays+"");
				
//				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays+nWeekEnds)+"");
				
//				double dblIncrement = 0;
				
				Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strLevel);
				if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>();
				
				double dblIncrementBasic = getIncrementCalculationBasic(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
				double dblIncrementDA = getIncrementCalculationDA(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
				
				Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				
				if((dblIncrementBasic>0 || dblIncrementDA>0) && getApprovePC()!=null) {
//					hmInner = CF.getSalaryCalculation(nEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, 0, strLevel, uF, CF);
					hmInner = CF.getSalaryCalculation(con,hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2,hmSalInner, null, hmEmpSalCalStatus.get(strEmpId));
//					System.out.println("AP/6305--if");
					//obj.processActivity(con,1, uF.parseToInt(strEmpId), uF.getDateFormat(strD2, DATE_FORMAT, DBDATE), CF,uF,"");
					//obj.insertEmpActivity(uF, 1, strEmpId);
				} else {  
//					System.out.println("AP/6305--else");
					hmInner = CF.getSalaryCalculation(con,hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2,hmSalInner, null, hmEmpSalCalStatus.get(strEmpId));
				}
				
//				Map<String, Map<String,String>> hmInnerActualCTC = CF.getSalaryCalculation(con,hmInnerisDisplay, nEmpId, nTotalNumberOfDaysForCalc, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2);
				
				Map<String, String> hmPaidSalaryInner = hmEmpPaidSalary.get(strEmpId);
				
				int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
				Map<String, String> hmPerkAlignAmount = new HashMap<String, String>();
				CF.getPerkAlignAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel), hmPerkAlignAmount);
				
				double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
				double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
				
				hmHoursWorked = hmEmpHoursWorked.get(strEmpId);
				if(hmHoursWorked==null)hmHoursWorked = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
				
				if(hmIndividualOtherEarning.size()>0 && !hmInner.containsKey(OTHER_EARNING+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(OTHER_EARNING+"", hmInnerTemp);
				}
				
				if(hmIndividualOtherReimbursement.size()>0 && !hmInner.containsKey(OTHER_REIMBURSEMENT+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(OTHER_REIMBURSEMENT+"", hmInnerTemp);
				}
//				if(hmEmpServiceTaxMap.size()>0 && !hmInner.containsKey(SERVICE_TAX+"")) {
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(SERVICE_TAX+"", hmInnerTemp);
//					
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(SWACHHA_BHARAT_CESS+"", hmInnerTemp);
//					
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(KRISHI_KALYAN_CESS+"", hmInnerTemp);
//				}
				
				if (hmEmpServiceTaxMap.size() > 0 && !hmInner.containsKey(CGST + "")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(CGST + "", hmInnerTemp);

					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(SGST + "", hmInnerTemp);
				}
				
//				if(hmIndividualOtherDeduction.size()>0 && !hmInner.containsKey(OTHER_DEDUCTION+"")) {
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "D");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(OTHER_DEDUCTION+"", hmInnerTemp);
//				}
				
				if(hmInner.size()>0 && hmInner.containsKey(TDS+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp=hmInner.get(TDS+"");
					hmInnerTemp.put("AMOUNT", hmInnerTemp.get("AMOUNT"));
					hmInnerTemp.put("EARNING_DEDUCTION", "D");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.remove(TDS+"");
					hmInner.put(TDS+"", hmInnerTemp);
				}
				
//				System.out.println("hmPresentDays1====>"+hmPresentDays1);
//				System.out.println("nTotalPresentDays====>"+dblTotalPresentDays);
				
//				System.out.println("hmInner==>"+hmInner);
//				System.out.println("nTotalNumberOfDaysForCalc==>"+nTotalNumberOfDaysForCalc);
				
//				Map hm=new HashMap();
				Map<String, String> hmTotal=new HashMap<String, String>();
				
				Set<String> set1 = hmInner.keySet();
				Iterator<String> it1 = set1.iterator();
				
				double dblGrossPT = 0;
				double dblGross = 0;
				double dblGrossTDS = 0;
				double dblDeduction = 0;
//				boolean isDefinedEarningDeduction = false;
				Set<String> setContriSalHead = new HashSet<String>();
				
				while(it1.hasNext()) {
					String strSalaryId = it1.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
//					System.out.println("AP/6415---nSalayHead="+nSalayHead);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					
					String strMulCal = hm.get("MULTIPLE_CALCULATION");
					List<String> al = new ArrayList<String>();
					if(strMulCal != null && !strMulCal.equals("")) {
						al = Arrays.asList(strMulCal.trim().split(","));
					}
					if(al != null && al.contains(""+EMPLOYER_EPF)) {
						setContriSalHead.add(""+EMPLOYER_EPF);
					}
					if(al != null && al.contains(""+EMPLOYER_ESI)) {
						setContriSalHead.add(""+EMPLOYER_ESI);
					}
					if(al != null && al.contains(""+EMPLOYER_LWF)) {
						setContriSalHead.add(""+EMPLOYER_LWF);
					}
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							
							/*if(strEmpId.equals("459")) {
								System.out.println(nSalayHead+"  aa hmTotal=====>"+hmTotal);
							}*/
							
							switch(nSalayHead) {
							/**********  OVER TIME   *************/
								case OVER_TIME:  
								 
//									isDefinedEarningDeduction = true;
//									double dblOverTime = getOverTimeCalculationL(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC, hmOverTimeMap, hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime);
//									double dblOverTime = getOverTimeCalculationHours(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC,  hmEmpLevelMap, dblStandardHrs,  nTotalNumberOfDaysForCalc, hmIndividualOvertime,hmEmpOverTimeHours,hmEmpOverTimeLevelPolicy, hmEmpRosterHours, hmWlocationMap, nTotalNumberOfDays, nWeekEnds,nHolidays,dblPresent,dblTotalPresentDays,holidaysSet,weeklyOffSet,hmEmpWlocationMap);
									double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
									
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
//									dblOverTime = Math.round(dblOverTime);
									dblGross += dblOverTime;
									dblGrossTDS += dblOverTime;
									
								break;
								
								case LEAVE_ENCASHMENT:  
//									Map<String, String> basicHead=hmInner.get(BASIC+"");
//									double leaveEncashmentAmt = getLeaveEncashmentAmount(uF,hmLeaveEncashment.get(strEmpId),dblTotalPresentDays,basicHead.get("AMOUNT"));
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(leaveEncashmentAmt)));
									
//									double leaveEncashmentAmt = getLeaveEncashmentAmtDetails(con, uF, CF, strEmpId, hmLeaveEncashment, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, dblIncrementBasic, dblIncrementDA);
									double leaveEncashmentAmt = 0.0d; //getLeaveEncashmentAmtDetails(con, uF, CF, strEmpId, 0.0d, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, dblIncrementBasic, dblIncrementDA);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),leaveEncashmentAmt));
									
									dblGross += leaveEncashmentAmt;
									dblGrossTDS += leaveEncashmentAmt;
									
								break;
								
								case BONUS:
									/*// Bonus is paid independent of paycycle -- 
									
									if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())) {
//										isDefinedEarningDeduction = true;
										double dblBonusAmount = getBonusCalculation(con, uF, strEmpId,strD2, hmEmpLevelMap, hmInnerActualCTC, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap, CF, hmIndividualBonus);
										hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblBonusAmount)));
//										dblBonusAmount = Math.round(dblBonusAmount);
										dblGross += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;
									}*/
									double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
//									dblBonusAmount = Math.round(dblBonusAmount);
									dblGross += dblBonusAmount;
									dblGrossTDS += dblBonusAmount;
									
								break;
								
								case EXGRATIA:
									
									double dblExGratiaAmount = getExGratiaAmount(con,uF,CF,strEmpId,strD1,strD2,strPC);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
									dblGross += dblExGratiaAmount;
									dblGrossTDS += dblExGratiaAmount;
									
								break;
								
								case AREARS:

//									isDefinedEarningDeduction = true;
									double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
//									dblArearAmount = Math.round(dblArearAmount);
									dblGross += dblArearAmount;
									dblGrossTDS += dblArearAmount;
									
								break; 
								
								case INCENTIVES:
									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
//									dblIncentiveAmount += uF.parseToDouble(hm.get("AMOUNT"));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
									dblGross += dblIncentiveAmount;
									dblGrossTDS += dblIncentiveAmount;
								break;
								
								case REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
//									dblReimbursementAmount = Math.round(dblReimbursementAmount);
									dblGross += dblReimbursementAmount;
								break;
								
								case TRAVEL_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
//									dblTravelReimbursementAmount = Math.round(dblTravelReimbursementAmount);
									dblGross += dblTravelReimbursementAmount;
								break;
								
								case MOBILE_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
//									dblMobileReimbursementAmount = Math.round(dblMobileReimbursementAmount);
									dblGross += dblMobileReimbursementAmount;
								break;
								
								case OTHER_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
//									dblOtherReimbursementAmount = Math.round(dblOtherReimbursementAmount);
									dblGross += dblOtherReimbursementAmount;
								break;
								
								case OTHER_EARNING:
//									isDefinedEarningDeduction = true;
									double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
//									dblOtherEarningAmount = Math.round(dblOtherEarningAmount);
									dblGross += dblOtherEarningAmount;
								break;
								
								case SERVICE_TAX:
									
//									isDefinedEarningDeduction = true;
									double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
//									dblServiceTaxAmount = Math.round(dblServiceTaxAmount);

									/**
									 * @author Vipin
									 * 25-Mar-2014
									 * KP Condition
									 * @comment = service tax is not included while calculating TDS
									 * */
									
									dblGross += dblServiceTaxAmount;  
									dblGrossPT += dblServiceTaxAmount;
									dblGrossTDS += dblServiceTaxAmount;  
									  
									break;	
								
								case SWACHHA_BHARAT_CESS:
									double dblGrossAmt = dblGross;
									double dblServiceTaxAmt = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
									double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
									dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
									
									double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));
									
									dblGross += dblSwachhaBharatCess;  
									dblGrossPT += dblSwachhaBharatCess;
									dblGrossTDS += dblSwachhaBharatCess;  
									  
									break;
								
								case KRISHI_KALYAN_CESS:
									double dblGrossAmt1 = dblGross;
									double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
									double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
									dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
									
									double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
									
									dblGross += dblKrishiKalyanCess;  
									dblGrossPT += dblKrishiKalyanCess;
									dblGrossTDS += dblKrishiKalyanCess;  
									  
									break;	
									
								case CGST :
									double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									break;
								
								case SGST :
									double dblGrossAmt2 = dblGross;
									double dblCGSTAmt = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossAmt2 = dblGrossAmt2 - dblCGSTAmt;
									
									double dblSGSTAmount = calculateSGST(con, uF, strEmpId, dblGrossAmt2, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									break;	
								
								default:
									
									if(uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
										double dblPerkAlignAmount = 0.0d;
										if(hmPerkAlignAmount.containsKey(strSalaryId)) {
											dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
										}
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount)); 
										dblGross += dblPerkAlignAmount;
										dblGrossPT += dblPerkAlignAmount;
										dblGrossTDS += dblPerkAlignAmount;	
										
									} else if (!uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
										dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
									} else if (uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
										if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										}
									} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"))));
										dblGross += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
									} else if(uF.parseToInt(strSalaryId)!=GROSS) {
										boolean isMultipePerWithParticularHead = false;
										if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
											isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional);
										}
										if(!isMultipePerWithParticularHead) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT")))); 
											dblGross += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										}
									}
									/*if(strEmpId.equals("459")) {
										System.out.println("hmTotal=====>"+hmTotal);
									}  */ 
									
								break;
							}
							
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						
						/**********  EPF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								Map<String,String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF+"");
								double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblEEPF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
							}
							
							break;
							
						/**********  EPF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblERPF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
							}
							 
							break;
							
						case LOAN:
							
//							System.out.println("Calculating Loan===>");
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {   
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
								if(strEmpId.equalsIgnoreCase("686")) {
//									System.out.println(strEmpId+" paid dblLoan==>=>"+dblLoan);
//									System.out.println(strEmpId+"  1 dblGrossTDS==>=>"+dblGrossTDS);
//									System.out.println(strEmpId+"  1 paid loan==>=>"+hmTotal.get(strSalaryId));    
								}
								
								CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2,strEmpId);
							} else {
								
								double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
								dblDeduction += dblLoanAmt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
								
//								if(strEmpId.equalsIgnoreCase("683")) {
//									System.out.println(strEmpId+"  dblLoanAmt==>=>"+dblLoanAmt);
//									System.out.println(strEmpId+"  1 dblGrossTDS==>=>"+dblGrossTDS);
//									System.out.println(strEmpId+"  1 loan==>=>"+hmTotal.get(strSalaryId));    
//								}
								           
								if(true) {
//									dblGrossTDS = dblGross - dblLoanAmt;
									dblGrossTDS = dblGrossTDS - dblLoanAmt; 
								}
							}
							
							break;   
							
						case MOBILE_RECOVERY:
							
//							isDefinedEarningDeduction = true; 
							if(hmPaidSalaryInner!=null) {
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
							} else {
								double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
								dblDeduction += dblIndividualMobileRecoveryAmt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
							}
							
							break;		
							
						
							
						default:
							if(hmPaidSalaryInner!=null) {
								dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								
								/*if(nSalayHead==VOLUNTARY_EPF) {
									continue;
								}*/
								
								if(hmAllowance.containsKey(strEmpId+"_"+strSalaryId)) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId))));
									dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId));
								} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_D")) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"))));
									dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
								} else if(uF.parseToInt(strSalaryId)!=PROFESSIONAL_TAX && uF.parseToInt(strSalaryId)!=TDS) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
									dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));	
								}	
							}
							break;
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross));
				}
				
				
				/**
				 * Multiple cal start
				 * */
				Map<String, String> hmContriSalHeadAmt = new HashMap<String, String>();
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
//					double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
					double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap);
					hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
//					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
					hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
					double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
					hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
				}
				Iterator<String> itMulti = hmInner.keySet().iterator();
				while(itMulti.hasNext()) {
					String strSalaryId = itMulti.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						}else {
							double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblGross += dblMulCalAmt;
							dblGrossTDS += dblMulCalAmt;
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if(hmPaidSalaryInner!=null) {
							dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblDeduction += dblMulCalAmt;
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross));
				}
				/**
				 * Multiple cal end 
				 * */
				
				/**
				 * Other cal start
				 * */
				Iterator<String> itOther = hmInner.keySet().iterator();
				while(itOther.hasNext()) {
					String strSalaryId = itOther.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						/**********  	 TAX   *************/
							case EMPLOYEE_EPF:
								if(hmPaidSalaryInner!=null) {
									double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblDeduction += dblPt;
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
								} else {
									double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, null, null, false, hmArearAmountMap);
									dblDeduction += dblEEPF;
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
								}
								break;
						case PROFESSIONAL_TAX:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								/**
								 * KP Condition
								 * 
								 * */      
								
//								double dblPt = calculateProfessionalTax(con, uF,strD2, dblGrossPT, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId));
							 	double dblPt = calculateProfessionalTax(con, uF,strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
								dblDeduction +=dblPt;

								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							}
							
							break;
						
						/**********  TDS   *************/
						case TDS:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblTDS;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							} else {
//								double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//								double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//								double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
//								
//								String[] hraSalaryHeads = null;
//								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
//									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//								}
//								
//								double dblHraSalHeadsAmount = 0;
//								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
//									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
//								}
//								
//								Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
//								if(hmPaidSalaryDetails==null) {hmPaidSalaryDetails=new HashMap<String, String>();}
//								
//								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
								
								if(strEmpId.equalsIgnoreCase("686")) {
//									System.out.println(strEmpId+" before dblGrossTDS==>=>"+dblGrossTDS);
								}   
								 
								if(hmEmpServiceTaxMap.containsKey(strEmpId)) {
									dblGrossTDS = dblGross;
									
//									double dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
//									dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
//									
//									double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
//									
//									double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
									
									double dblCGST = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossTDS = dblGrossTDS - dblCGST;

									double dblSGST = uF.parseToDouble(hmTotal.get(SGST + ""));
									dblGrossTDS = dblGrossTDS - dblSGST;
								}
								
								/**
								 * (dblBasic + dblDA) we use dblHraSalHeadsAmount
								 * */
//								double dblTDS = calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//										nPayMonth,
//										strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, hmEmpGenderMap.get(strEmpId),  hmEmpAgeMap.get(strEmpId), hmEmpStateMap.get(strEmpId),
//										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//										hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
								double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
										strFinancialYearEnd, strEmpId, hmEmpLevelMap);
								
								dblDeduction += dblTDS;
																
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							}
							break;
							
							/**********  ESI EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYER CONTRIBUTION   *************/
							
							/**********  ESI EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction +=dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYEE CONTRIBUTION   *************/	
							
							/**********  LWF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							
							/**********  /LWF EMPLOYER CONTRIBUTION   *************/
							
							/**********  LWF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							/**********  /LWF EMPLOYEE CONTRIBUTION   *************/	
						
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				/**
				 * Other cal end 
				 * */
				
				
				String strCurrencyId = hmEmpCurrency.get(strEmpId);
				Map<String,String> hmCurrency = hmCurrencyDetails.get(strCurrencyId);
				if(hmCurrency==null)hmCurrency = new HashMap<String,String>();
				
				
//				if(strEmpId.equals("230") || strEmpId.equals("239")) {  
//					System.out.println(strEmpId+"----aa hmTotal=====>"+hmTotal+"----dblDeduction=====>"+dblDeduction+"-----dblGross=====>"+dblGross);
//				}				
				
				hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction))); 
//				System.out.println("hmTotal 1: "+hmTotal);
				hmTotalSalary.put(strEmpId, hmTotal);
//				System.out.println("hmTotalSalary 1: "+hmTotalSalary);
				
				////=========================code for isdisplay false=======================

//				Map hmisDisplay=new HashMap();
				Map<String, String> hmTotalisDisplay=new HashMap<String, String>();
				Set<String> set2 = hmInnerisDisplay.keySet();
				Iterator<String> it2 = set2.iterator();
				dblGross = 0;
				dblGrossTDS = 0;
				dblDeduction = 0;
//				isDefinedEarningDeduction = false;
				while(it2.hasNext()) {
					String strSalaryId = it2.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
//					isDefinedEarningDeduction = false;
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, hmPaidSalaryInner.get(strSalaryId));
						} else {
							/*if(strEmpId.equals("459")) {
								System.out.println(nSalayHead+"  aa hmTotal=====>"+hmTotal);
							}*/
							
							switch(nSalayHead) {
							/**********  OVER TIME   *************/
								case OVER_TIME:  
								 
//									isDefinedEarningDeduction = true;
//									double dblOverTime = getOverTimeCalculationL(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC, hmOverTimeMap, hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime);
//									double dblOverTime = getOverTimeCalculationHours(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInnerisDisplay, hmInnerActualCTC,  hmEmpLevelMap, dblStandardHrs,  nTotalNumberOfDaysForCalc, hmIndividualOvertime,hmEmpOverTimeHours,hmEmpOverTimeLevelPolicy, hmEmpRosterHours, hmWlocationMap, nTotalNumberOfDays, nWeekEnds,nHolidays,dblPresent,dblTotalPresentDays,holidaysSet,weeklyOffSet,hmEmpWlocationMap);
									double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
//									dblOverTime = Math.round(dblOverTime);
									dblGross += dblOverTime;
									dblGrossTDS += dblOverTime;
									
								break;
								
								case BONUS:
									
									/*if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())) {
//										isDefinedEarningDeduction = true;
										double dblBonusAmount = getBonusCalculation(con, uF, strEmpId,strD2, hmEmpLevelMap, hmInnerActualCTC, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap, CF, hmIndividualBonus);
										hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblBonusAmount)));
//										dblBonusAmount = Math.round(dblBonusAmount);
										dblGross += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;
									}*/
									double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
//									dblBonusAmount = Math.round(dblBonusAmount);
									dblGross += dblBonusAmount;
									dblGrossTDS += dblBonusAmount;
									
								break;
								
								case EXGRATIA:
									
									double dblExGratiaAmount = getExGratiaAmount(con,uF,CF,strEmpId,strD1,strD2,strPC);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
									dblGross += dblExGratiaAmount;
									dblGrossTDS += dblExGratiaAmount;
									
								break;
								
								case AREARS:

//									isDefinedEarningDeduction = true;
									double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
//									dblArearAmount = Math.round(dblArearAmount);
									dblGross += dblArearAmount;
									dblGrossTDS += dblArearAmount;
									
								break;
								
								case INCENTIVES:
//									isDefinedEarningDeduction = true;
//									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
//									dblIncentiveAmount += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
//									hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIncentiveAmount)));
////									dblIncentiveAmount = Math.round(dblIncentiveAmount);
//									dblGross += dblIncentiveAmount;
//									dblGrossTDS += dblIncentiveAmount;
									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
									dblGross += dblIncentiveAmount;
									dblGrossTDS += dblIncentiveAmount;
									
								break;
								
								case REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
//									dblReimbursementAmount = Math.round(dblReimbursementAmount);
									dblGross += dblReimbursementAmount;
								break;
								
								case TRAVEL_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
//									dblTravelReimbursementAmount = Math.round(dblTravelReimbursementAmount);
									dblGross += dblTravelReimbursementAmount;
								break;
								
								case MOBILE_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
//									dblMobileReimbursementAmount = Math.round(dblMobileReimbursementAmount);
									dblGross += dblMobileReimbursementAmount;
								break;
								
								case OTHER_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
//									dblOtherReimbursementAmount = Math.round(dblOtherReimbursementAmount);
									dblGross += dblOtherReimbursementAmount;
								break;
								
								case OTHER_EARNING:
//									isDefinedEarningDeduction = true;
									double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
//									dblOtherEarningAmount = Math.round(dblOtherEarningAmount);
									dblGross += dblOtherEarningAmount;
								break;
								
								case SERVICE_TAX:
									
//									isDefinedEarningDeduction = true;
									double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
//									dblServiceTaxAmount = Math.round(dblServiceTaxAmount);

									/**
									 * @author Vipin
									 * 25-Mar-2014
									 * KP Condition
									 * @comment = service tax is not included while calculating TDS
									 * */
									
									dblGross += dblServiceTaxAmount;  
									dblGrossPT += dblServiceTaxAmount;
									dblGrossTDS += dblServiceTaxAmount;  
									  
									break;	
								
								case SWACHHA_BHARAT_CESS:
									double dblGrossAmt = dblGross;
									double dblServiceTaxAmt = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX+""));
									dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
									double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
									dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
									
									double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId), hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));

									dblGross += dblSwachhaBharatCess;  
									dblGrossPT += dblSwachhaBharatCess;
									dblGrossTDS += dblSwachhaBharatCess;  
									  
									break;	
								
								case KRISHI_KALYAN_CESS:
									double dblGrossAmt1 = dblGross;
									double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
									double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
									dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
									
									double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
									
									dblGross += dblKrishiKalyanCess;  
									dblGrossPT += dblKrishiKalyanCess;
									dblGrossTDS += dblKrishiKalyanCess;  
									  
									break;	
									
								case CGST :
									double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									break;
								
								case SGST :
									double dblGrossAmt2 = dblGross;
									double dblCGSTAmt = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossAmt2 = dblGrossAmt2 - dblCGSTAmt;
									
									double dblSGSTAmount = calculateSGST(con, uF, strEmpId, dblGrossAmt2, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									break;	
								
								default:
									if(uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
										double dblPerkAlignAmount = 0.0d;
										if(hmPerkAlignAmount.containsKey(strSalaryId)) {
											dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
										}
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount)); 
										dblGross += dblPerkAlignAmount;
										dblGrossPT += dblPerkAlignAmount;
										dblGrossTDS += dblPerkAlignAmount;	
									} else if (!uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
										dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
									} else if (uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
										if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
									} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"))));
										dblGross += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
									} else if(uF.parseToInt(strSalaryId)!=GROSS) {
										boolean isMultipePerWithParticularHead = false;
										if(hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
											isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional);
										}
										if(!isMultipePerWithParticularHead) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT")))); 
											dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
										
									}
									/*if(strEmpId.equals("459")) {
										System.out.println("hmTotal=====>"+hmTotal);
									}  */ 
									
								break;
							}
							
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						
						/**********  EPF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblPt));
							} else {
								Map<String, String> hmVoluntaryPF = hmInnerisDisplay.get(VOLUNTARY_EPF+"");
								double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblEEPF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblEEPF));
							}
							
							break;
							
						/**********  EPF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblERPF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
							}
							
							break;
							
						case LOAN:
							
//							System.out.println("Calculating Loan===>");
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {   
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
								CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2,strEmpId);
							} else {
								double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
								dblDeduction += dblLoanAmt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
								           
								if(true) {
//									dblGrossTDS = dblGross - dblLoanAmt;
									dblGrossTDS = dblGrossTDS - dblLoanAmt; 
								}
							}
							
							break;  
							
						case MOBILE_RECOVERY:
							
//							isDefinedEarningDeduction = true; 
							if(hmPaidSalaryInner!=null) {
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
							} else {
								double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
								dblDeduction += dblIndividualMobileRecoveryAmt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
							}
							
							break;		
							
						default:
							if(hmPaidSalaryInner!=null) {
								dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								
								/*if(nSalayHead==VOLUNTARY_EPF) {
									continue;
								}*/
								
								if(hmAllowance.containsKey(strEmpId+"_"+strSalaryId)) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId))));
									dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId));
								} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_D")) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"))));
									dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
								} else if(uF.parseToInt(strSalaryId)!=PROFESSIONAL_TAX && uF.parseToInt(strSalaryId)!=TDS) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
									dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));	
								}	
							}
							break;
						}
					}
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				
				/**
				 * Multiple calfor isDisplay false start 
				 * */
//				hmContriSalHeadAmt = new HashMap<String, String>();
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
////					double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
//					double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap);
//					hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
//				}
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
////					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
//					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
//					hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
//				}
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
//					double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth);
//					hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
//				}
				Iterator<String> itMultiIsDisplay = hmInnerisDisplay.keySet().iterator();
				while(itMultiIsDisplay.hasNext()) {
					String strSalaryId = itMultiIsDisplay.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblGross += dblMulCalAmt;
							dblGrossTDS += dblMulCalAmt;
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if(hmPaidSalaryInner!=null) {
							dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
//							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
//							dblDeduction += dblMulCalAmt;
							//Started By Dattatray Date:14-12-21
							if(hmTotal.containsKey(strSalaryId) && setContriSalHead != null && (setContriSalHead.contains(EMPLOYEE_EPF+"") || setContriSalHead.contains(EMPLOYER_EPF+"") || setContriSalHead.contains(EMPLOYEE_ESI+"") || setContriSalHead.contains(EMPLOYER_EPF+"") || setContriSalHead.contains(EMPLOYEE_LWF+"") || setContriSalHead.contains(EMPLOYER_LWF+""))) {
								dblDeduction -= uF.parseToDouble(hmTotal.get(strSalaryId));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
								dblDeduction += dblMulCalAmt;
							}//Ended By Dattatray Date:14-12-21
							
						}		
					}
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross)));
				}
				
				/**
				 * Multiple cal isDisplay false start
				 * */
				
				/**
				 * Other cal isDisplay start
				 * */
				Iterator<String> itIsDisplayOther = hmInnerisDisplay.keySet().iterator();
				while(itIsDisplayOther.hasNext()) {
					String strSalaryId = itIsDisplayOther.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					System.out.println("AP/7575--nSalayHead="+nSalayHead);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						/**********  	 TAX   *************/
						case PROFESSIONAL_TAX:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								
								
								/**
								 * KP Condition
								 * 
								 * */     
								if(strEmpId.equals("459")) {
//									System.out.println("inner dblGrossPT=====>"+dblGrossPT+"-----dblGross=====>"+dblGross);
								}
								     
//								double dblPt = calculateProfessionalTax(con, uF, strD2,dblGrossPT, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId));
							 	double dblPt = calculateProfessionalTax(con, uF, strD2,dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
								dblDeduction +=dblPt;  
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							}
							
							break;
							
							
						/**********  TDS   *************/
						case TDS:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblTDS;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							} else {

//								double dblBasic = uF.parseToDouble(hmTotalisDisplay.get(BASIC+""));
//								double dblDA = uF.parseToDouble(hmTotalisDisplay.get(DA+""));
//								double dblHRA = uF.parseToDouble(hmTotalisDisplay.get(HRA+""));
//								
//								String[] hraSalaryHeads = null;
//								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
//									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//								}
//								
//								double dblHraSalHeadsAmount = 0;
//								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
//									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotalisDisplay.get(hraSalaryHeads[i]));
//								}
//								
//								Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
//								if(hmPaidSalaryDetails==null) {hmPaidSalaryDetails=new HashMap<String, String>();}
//								
//								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
								 
								if(hmEmpServiceTaxMap.containsKey(strEmpId)) {
									dblGrossTDS = dblGross;
									
//									double dblServiceTaxAmount = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX+""));
//									dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
//									
//									double dblSwachhaBharatCess = uF.parseToDouble(hmTotalisDisplay.get(SWACHHA_BHARAT_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
//									
//									double dblKrishiKalyanCess = uF.parseToDouble(hmTotalisDisplay.get(KRISHI_KALYAN_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
									
									double dblCGST = uF.parseToDouble(hmTotalisDisplay.get(CGST + ""));
									dblGrossTDS = dblGrossTDS - dblCGST;

									double dblSGST = uF.parseToDouble(hmTotalisDisplay.get(SGST + ""));
									dblGrossTDS = dblGrossTDS - dblSGST;
								}
								
								/**
								 * (dblBasic + dblDA) we use dblHraSalHeadsAmount
								 * */
//								double dblTDS = calculateTDS(con, uF, strD2,strD1,dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//										nPayMonth,
//										strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, hmEmpGenderMap.get(strEmpId),  hmEmpAgeMap.get(strEmpId), hmEmpStateMap.get(strEmpId),
//										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//										hmTotalisDisplay, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
								double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
										strFinancialYearEnd, strEmpId, hmEmpLevelMap);
								dblDeduction += dblTDS;
								
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							}
							break;
							
							/**********  ESI EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYER CONTRIBUTION   *************/
							
							/**********  ESI EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction +=dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYEE CONTRIBUTION   *************/	
							
							/**********  LWF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							
							/**********  /LWF EMPLOYER CONTRIBUTION   *************/
							
							/**********  LWF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							/**********  /LWF EMPLOYEE CONTRIBUTION   *************/
						}
					}
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				/**
				 * Other cal isDisplay end 
				 * */
				
				hmTotalSalaryisDisplay.put(strEmpId, hmTotalisDisplay);
			////=========================end code for isdisplay false=======================
			}

			//			System.out.println("hmTotalSalary===>"+hmTotalSalary);
			
			List<String> alEmpIdPayrollG = new ArrayList<String>();
			pst = con.prepareStatement("select distinct(emp_id) from payroll_generation where paycycle=? and salary_head_id not in ("+BONUS+") ");
			pst.setInt(1, uF.parseToInt(strPC));			
			rs = pst.executeQuery();
			while(rs.next()) {
				String strEmpId = rs.getString("emp_id");
				alEmpIdPayrollG.add(strEmpId);
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("pst==>"+getPaycycle().split("-")[2]);
//			System.out.println("pst==>"+pst);
//			System.out.println("alEmpIdPayrollG==>"+alEmpIdPayrollG);
			
			
//			System.out.println("hmTotalSalary Approval="+hmTotalSalary);
			
			request.setAttribute("hmTotalSalary", hmTotalSalary);
			request.setAttribute("hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
			request.setAttribute("hmEmpNameMap", hmEmpNameMap);
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
			request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmEmpSalary", hmEmpSalary);
			request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			request.setAttribute("alEmpIdPayrollG", alEmpIdPayrollG);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("alProcessingEmployee", alProcessingEmployee);
			
			request.setAttribute("hmPaidDays", hmPaidDays);
			request.setAttribute("hmPresentDays", hmPresentDays1);
			request.setAttribute("hmLeaveDays", hmLeaveDays);
			request.setAttribute("hmLeaveTypeDays", hmLeaveTypeDays);
			request.setAttribute("hmMonthlyLeaves", hmMonthlyLeaves);
//			System.out.println("hmTotalDays=====>"+hmTotalDays);
			request.setAttribute("strTotalDays", strTotalDays);
			request.setAttribute("hmTotalDays", hmTotalDays);
			request.setAttribute("hmEmpPaymentMode", hmEmpPaymentMode);
			request.setAttribute("hmPaymentModeMap", hmPaymentModeMap);
			request.setAttribute("hmLoanAmt", hmLoanAmt);
			request.setAttribute("hmEmpStateMap", hmEmpStateMap);
			request.setAttribute("hmVariables", hmVariables);
			request.setAttribute("hmHolidayCount", hmHolidayCount);
			request.setAttribute("hmWoHLeaves", hmWoHLeaves);
			
			request.setAttribute("strD1", strD1);
			request.setAttribute("strD2", strD2);

			session.setAttribute("AP_hmTotalSalary", hmTotalSalary);
			session.setAttribute("AP_hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
			session.setAttribute("AP_hmEmpNameMap", hmEmpNameMap);
			session.setAttribute("AP_hmEmpCodeMap", hmEmpCodeMap);
			session.setAttribute("AP_hmLoanPoliciesMap", hmLoanPoliciesMap);
			session.setAttribute("AP_hmSalaryDetails", hmSalaryDetails);
			session.setAttribute("AP_hmEmpSalary", hmEmpSalary);
			session.setAttribute("AP_alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			session.setAttribute("AP_alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			session.setAttribute("AP_alEmpIdPayrollG", alEmpIdPayrollG);
			session.setAttribute("AP_hmServices", hmServices);
			session.setAttribute("AP_alEmp", alEmp);
			session.setAttribute("AP_alProcessingEmployee", alProcessingEmployee);
			
			session.setAttribute("AP_hmPaidDays", hmPaidDays);
			session.setAttribute("AP_hmPresentDays", hmPresentDays1);
			session.setAttribute("AP_hmLeaveDays", hmLeaveDays);
			session.setAttribute("AP_hmLeaveTypeDays", hmLeaveTypeDays);
			session.setAttribute("AP_hmMonthlyLeaves", hmMonthlyLeaves);
			
			session.setAttribute("AP_strTotalDays", strTotalDays);
			session.setAttribute("AP_hmTotalDays", hmTotalDays);
			session.setAttribute("AP_hmEmpPaymentMode", hmEmpPaymentMode);
			session.setAttribute("AP_hmPaymentModeMap", hmPaymentModeMap);
			session.setAttribute("AP_hmLoanAmt", hmLoanAmt);
			session.setAttribute("AP_hmEmpStateMap", hmEmpStateMap);
			session.setAttribute("AP_hmVariables", hmVariables);
			session.setAttribute("AP_hmOtherTaxDetails", hmOtherTaxDetails);			
			session.setAttribute("AP_hmEmpLevelMap", hmEmpLevelMap);
			session.setAttribute("AP_hmHolidayCount", hmHolidayCount);
			
			session.setAttribute("AP_strD1", strD1);
			session.setAttribute("AP_strD2", strD2);
			session.setAttribute("AP_strPC", strPC);			
			session.setAttribute("AP_f_org", getF_org());
			session.setAttribute("AP_strPaycycleDuration", getStrPaycycleDuration());
			session.setAttribute("AP_hmArearAmountMap", hmArearAmountMap);
			session.setAttribute("AP_hmWoHLeaves", hmWoHLeaves);
			
			
//			System.out.println("alProcessingEmployee==="+alProcessingEmployee);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
//===start parvez date: 14-04-2022===
	
	public String viewClockEntriesForPayrollApprovalExitForm(CommonFunctions CF, String strReqEmpId, String strD1, String strD2,String strPC) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
//			this.strD1 = strD1;
//			this.strD2 = strD2;
//			System.out.println("AP/4798--strReqEmpId===="+strReqEmpId);
//			System.out.println("AP/4799--strD1="+strD1+"--strD2="+strD2+"--strPC="+strPC);
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int nTotalNumberOfDays = 0;
			if(uF.parseToBoolean(CF.getIsPaycycleAdjustment())) {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
				nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
			} else {
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
				nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
			}
			
//			int nTotalNumberOfDays = uF.parseToInt(uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
			
			con = db.makeConnection(con);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if(strFinancialYear!=null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			double dblInvestmentExemption = 0.0d;
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));

				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
				
//				dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()) {
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			 
//			System.out.println("pst===>"+pst);
			
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();
			
//			String strFinancialYearEnd = null;
//			String strFinancialYearStart = null;
//			double dblInvestmentExemption = 0.0d;
			double dblStandardHrs = 0.0d;
			
			Map<String, String> hmOrg= CF.getOrgDetails(con,uF,getF_org());
			if(hmOrg == null) hmOrg=new HashMap<String, String>();
			
			Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
			Map<String, String> hmEmpEndDateMap = new HashMap<String, String>();
			Map<String, Map<String, String>> hmWlocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpStateMap = new HashMap<String, String>();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map<String, String> hmEmpJoiningMap = CF.getEmpJoiningDateMap(con, uF, hmEmpEndDateMap); 
			request.setAttribute("hmEmpJoiningMap", hmEmpJoiningMap);
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null); 
			Map<String, String> hmEmpCodeMap = CF.getEmpCodeMap(con);
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
			Map<String, String> hmEmpExemptionsMap = getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
			Map<String, String> hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, Map<String, String>> hmEmpPaidSalary = CF.getPaidSalary(con, strFinancialYearStart, strFinancialYearEnd, strPC);
			Map<String, String> hmEmpRentPaidMap = getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmEmpIncomeOtherSourcesMap = getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
//			Map<String, String> hmLeaveEncashment = CF.getLeaveEncashment(con, uF, strD1, strD2, strPC);
			
//			Map<String, Map<String, String>> hmOverTimeMap = CF.getOverTimeMap(con, CF);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);

			Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, getF_org());
			Map<String, Map<String, String>> hmLeaveDays = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
			Map<String, String> hmMonthlyLeaves = new HashMap<String, String>();
			
	//===start parvez date: 07-04-2022===		
			Map<String, String> hmLeaveEncashment = CF.getLeaveEncashmentNew(con, uF, CF, session , getF_org(),hmEmpLevelMap,hmEmpWlocationMap,strReqEmpId);
//			Map<String, Map<String, String>> hmEmpInfoMap = CF.getEmpInfoMap(con, false);
			
			pst = con.prepareStatement("select last_day_date from emp_off_board where approved_1=1 and approved_2=1 and emp_id=?");
			pst.setInt(1, uF.parseToInt(strReqEmpId));
			rs = pst.executeQuery();
			String empResignDate = null;
			while (rs.next()) {
				empResignDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			String[] empFinalPay = CF.getPayCycleByOrg(con, empResignDate,  CF.getStrTimeZone(), getF_org());
			String srtFD1 = empFinalPay[0];
			String strFD2  = empFinalPay[1];
			String empFinalPC = empFinalPay[2];
	//===end parvez date: 07-04-2022===		
			
//			hmLeaveDays = CF.getLeaveDates(con, strD1, strD2, CF, hmLeaveTypeDays, true, hmMonthlyLeaves);
			hmLeaveDays = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, hmLeaveTypeDays, true, hmMonthlyLeaves);
//			System.out.println("hmLeaveDays=====>"+hmLeaveDays);
//			System.out.println("hmLeaveTypeDays======>"+hmLeaveTypeDays);
//			System.out.println("hmMonthlyLeaves=======>"+hmMonthlyLeaves);
			
			String strTotalDays = nTotalNumberOfDays+"";
			
			Map<String, String> hmTotalDays = new HashMap<String, String>();
			
//			Map hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
//			Map<String, String> hmHolidays = new HashMap<String, String>();
//			Map<String, String> hmHolidayDates = new HashMap<String, String>();
//			CF.getHolidayList(con, strD1, strD2, CF, hmHolidayDates, hmHolidays, true);
//			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request, uF,strD1, strD2);
			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request, uF,strD1, strD2);
			
			Map<String, String> hmLongLeaves = getLongLeavesCount(con,uF,CF,strD1,strD2,strPC);
			if(hmLongLeaves == null) hmLongLeaves = new HashMap<String, String>();
			
//			System.out.println("hmHolidayDates="+hmHolidayDates);
//			System.out.println("hmHolidays="+hmHolidays);
//			System.out.println("hmWeekEnds="+hmWeekEnds);
			
			/*Calendar cal1  = GregorianCalendar.getInstance();
			cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
			cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
			String strDateNew = uF.getDateFormat("01/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
			
			System.out.println("strDateNew=="+strDateNew);
			System.out.println("strD1=="+strD1);
			System.out.println("strD2=="+strD2);
			*/
//			Map hmWeekEnds = CF.getWeekEndDateList(con, strDateNew, strD2, CF, uF);
			
			Map<String, String> hmAttendanceDependent = CF.getAttendanceDependency(con);
			Map<String, String> hmRosterDependent = CF.getRosterDependency(con);
			Map<String,Map<String,String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
			Map<String,String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String,String> hmEmpPaymentMode = CF.getEmpPaymentMode(con, uF);
			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String,String> hmFixedExemptions = getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
			List<String> alPresentEmpId = new ArrayList<String>();
			List<String> alPresentDates = new ArrayList<String>();
			List<String> alPresentWeekEndDates = new ArrayList<String>();
			List<String> alHalfDaysDueToLatePolicy = new ArrayList<String>();
			List<String> alServices = new ArrayList<String>();
			Map<String, List<String>> hmPresentDays = new HashMap<String, List<String>>();
			Map<String, List<String>> hmPresentWeekEndDays = new HashMap<String, List<String>>();
			Map<String, List<String>> hmHalfDays = new HashMap<String, List<String>>();
			Map<String, String> hmPresentDays1 = new HashMap<String, String>();
			Map<String, String> hmPaidDays = new HashMap<String, String>();
			Map<String, List<String>> hmServices = new HashMap<String, List<String>>();
			Map<String, String> hmHoursWorked = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpHoursWorked = new HashMap<String, Map<String, String>>();
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			
			Map<String,String> hmEmpRosterHours = new HashMap<String,String>();
			Map<String, Map<String,String>> hmEmpOverTimeHours=CF.getEmpOverTimeHours(con,CF,uF,strD1,strD2,strPC);
			Map<String, Map<String,String>> hmEmpOverTimeLevelPolicy=CF.getEmpOverTimeLevelPolicy(con,CF,uF,strD1,strD2,strPC);
			
			Map<String, String> hmHolidayCount = CF.getHolidayCount(con,CF,uF,strD1,strD2,strPC);
			
			List<String> alFullDaysDueToLatePolicy = new ArrayList<String>();
			Map<String, List<String>> hmFullDays = new HashMap<String, List<String>>();
			
			List<String> alEmp = new ArrayList<String>();
			
			Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and (employment_end_date is null OR employment_end_date >= ?) and joining_date<= ? and emp_per_id > 0 ");
			if(uF.parseToInt(strReqEmpId)>0) {
				sbQuery.append(" and emp_per_id = " + uF.parseToInt(strReqEmpId));
			}
			if(getLevel()!=null && getLevel().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getLevel(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
            
			if(getStrPaycycleDuration()!=null) {
				sbQuery.append(" and paycycle_duration ='"+getStrPaycycleDuration()+"'");
			}
			
			if(uF.parseToInt(getF_paymentMode())>0) {
				sbQuery.append(" and payment_mode ="+uF.parseToInt(getF_paymentMode()));
			}
			
			if(getwLocation()!=null && getwLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getwLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" and emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? " +
					"and paycycle = ? group by emp_id)");
			sbQuery.append(" order by emp_fname, emp_lname"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(strPC));
//			System.out.println("pst=====>"+pst); 
			rs = pst.executeQuery();
			StringBuilder sbEmPId = new StringBuilder();
			Map<String, String> hmEmpPaycycleDuration = new HashMap<String, String>();
			Map<String, String> hmEmpSalCalStatus = new HashMap<String, String>();
			List<String> alEmpJoinDate = new ArrayList<String>();
			List<String> alGradeId = new ArrayList<String>();
			while(rs.next()) {
				alEmp.add(rs.getString("emp_per_id"));
				sbEmPId.append(rs.getString("emp_per_id")+",");
				
				hmEmpPaycycleDuration.put(rs.getString("emp_per_id"), rs.getString("paycycle_duration"));
				
				if(rs.getString("joining_date")!=null) { 
					Date date = uF.getDateFormatUtil(rs.getString("joining_date"), DBDATE);
					if(uF.isDateBetween(sDate, eDate, date) ) {
						alEmpJoinDate.add(rs.getString("emp_per_id"));
					}
				}
				
				if (!alGradeId.contains(rs.getString("grade_id"))) {
					alGradeId.add(rs.getString("grade_id"));
				}
				hmEmpSalCalStatus.put(rs.getString("emp_per_id"), rs.getString("is_disable_sal_calculate"));
			}
			rs.close();
			pst.close(); 
			request.setAttribute("alEmpJoinDate", alEmpJoinDate);
//			System.out.println("alEmpJoinDate=====>"+alEmpJoinDate);
			
			if(sbEmPId.length()>1) {
				sbEmPId.replace(0, sbEmPId.length(), sbEmPId.substring(0, sbEmPId.length()-1));
			}
			
			/*if(strReqEmpId!=null) {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and emp_id =? order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpId));
			}else if(sbEmPId.length()>1) {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and emp_id in ("+sbEmPId.toString()+") order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			} else {
				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			}*/
//			System.out.println("strReqEmpId ===>> " + strReqEmpId);
			if(strReqEmpId!=null) {
//				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and emp_id =? order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id =? " +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+
						"order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strReqEmpId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strPC));
			}else if(sbEmPId.length()>1) {
//				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and emp_id in ("+sbEmPId.toString()+") order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and ad.emp_id in ("+sbEmPId.toString()+") order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				// Remove In_out='OUT'
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id in ("+sbEmPId.toString()+")" +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+		
						" order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPC));
			} else {
				//pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  " +
						"and ad.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? and paycycle = ? group by emp_id) "+
						" order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPC));
			}
			
			Map<String, String> hmOverLappingHolidays = new HashMap<String, String>();
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();  
			double dblOverLappingHolidays = 0;
			
			String strPresentEmpIdNew = null;
			String strPresentEmpIdOld = null;
			Set<String> tempweeklyOffSet=null;
			String level=null;
			String location=null;
			Set<String> tempholidaysSet=null;
			while(rs.next()) {
				strPresentEmpIdNew = rs.getString("emp_id");
				if(strPresentEmpIdNew!=null && !strPresentEmpIdNew.equalsIgnoreCase(strPresentEmpIdOld)) {
					alPresentEmpId = new ArrayList<String>();
					alPresentDates = new ArrayList<String>();
					alPresentWeekEndDates = new ArrayList<String>();
					alServices = new ArrayList<String>();
					hmHoursWorked = new HashMap<String, String>();
					halfDayCountIN = 0;
					halfDayCountOUT = 0;
					dblOverLappingHolidays = 0;
					alHalfDaysDueToLatePolicy = new ArrayList<String>();
					location = hmEmpWlocationMap.get(strPresentEmpIdNew);
					level=hmEmpLevelMap.get(strPresentEmpIdNew);
//					tempweeklyOffSet=hmWeekEnds.get(level);
					if(alEmpCheckRosterWeektype.contains(strPresentEmpIdNew)) {
						tempweeklyOffSet = hmRosterWeekEndDates.get(strPresentEmpIdNew);
					} else {
						tempweeklyOffSet = hmWeekEnds.get(location);
					}
					if(tempweeklyOffSet==null)tempweeklyOffSet=new HashSet<String>();
					
					Set<String> temp=holidaysMp.get(location);
					if(temp==null)temp=new HashSet<String>();
					tempholidaysSet=new HashSet<String>(temp);	
					tempholidaysSet.removeAll(tempweeklyOffSet);
//					if(strPresentEmpIdNew.equals("118")) {
//						System.out.println("tempweeklyOffSet==="+tempweeklyOffSet);
//						System.out.println("tempholidaysSet==="+tempholidaysSet);
//						System.out.println("level==="+level);
//
//					}
					
					alFullDaysDueToLatePolicy = new ArrayList<String>();
					fullDayCountIN=0;
					fullDayCountOUT=0;
				}
				
				if(!alPresentEmpId.contains(strPresentEmpIdNew)) {
					alPresentEmpId.add(strPresentEmpIdNew);
				}
				
				hmEmpRosterHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("emp_id"), rs.getString("actual_hours"));
				
				String strDay = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
				strDay = strDay.toUpperCase();
				
//				double dblEarlyLate = rs.getDouble("early_late"); 
				double dblEarlyLate = uF.parseToInt(rs.getString("approved"))== -1 ? rs.getDouble("early_late") : 0.0d; 
				double dblHDHoursWoeked = uF.parseToInt(rs.getString("approved"))!= -1 ? rs.getDouble("hours_worked") : 0.0d; 
				 
//				if(uF.parseToInt(rs.getString("emp_id")) == 11) {
//					System.out.println("inout====="+rs.getString("in_out")+"----"+strDate+" approved====>"+uF.parseToInt(rs.getString("approved"))+ "====dblEarlyLate====>"+dblEarlyLate+"----dblHDEarlyLate====>"+dblHDEarlyLate);
//				} 
				
				String strINOUT = rs.getString("in_out");
				Map<String, String> hmLeaves = hmLeaveDays.get(strPresentEmpIdNew);
				if(hmLeaves==null)hmLeaves = new HashMap<String, String>();
				
//				if(strPresentEmpIdNew.equals("118")) {
//					System.out.println("strDate==="+strDate);
//				}
				if(!alPresentDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
					/**
					 * To avoid the over presence data
					 */
					
//					Map hmLeaves = hmLeaveDays.get(strPresentEmpIdNew);
//					if(hmLeaves==null)hmLeaves = new HashMap();
					
//					String strWeekEnd = hmWeekEnds.get(strDay+"_"+strLocation);
					String strWeekEnd = null;
					
					if(tempweeklyOffSet.contains(strDate)) {
						strWeekEnd = WEEKLYOFF_COLOR;
					}
					
					if(strWeekEnd==null ) { //&& !hmLeaves.containsKey(strDate)
//					if(strWeekEnd==null && !hmLeaves.containsKey(strDate)) {
						alPresentDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}else if(!alPresentWeekEndDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						alPresentWeekEndDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}
					if(tempholidaysSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						dblOverLappingHolidays++;
					}
					
				}
				
				boolean isRosterDependent = uF.parseToBoolean(hmRosterDependent.get(strPresentEmpIdNew));
				
				if(dblEarlyLate > 0.0d) {
					if(isHalfDay(strDate, dblEarlyLate, strINOUT, hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alHalfDaysDueToLatePolicy!=null
							&& isRosterDependent
							&& !alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))
							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
					
						alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
					}
					
					if(!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						if(isFullDay(strDate, dblEarlyLate, strINOUT, (String)hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alFullDaysDueToLatePolicy!=null
								&& isRosterDependent
								&& !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))
								&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							alFullDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						}
					}
				}
				
				double x = Math.abs(dblHDHoursWoeked);
				if(x > 0.0d && x <=5) {
					if(!alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)) && !alFullDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
						String strWLocationId = hmEmpWlocation.get(rs.getString("emp_id"));
						Set<String> weeklyOffSet= hmWeekEnds.get(strWLocationId);
						if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
						
						Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
						if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
						
						Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_id"));
						if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
						if(alEmpCheckRosterWeektype.contains(rs.getString("emp_id"))) {
							if(!rosterWeeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
								alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
							}
						} else if(weeklyOffSet.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))) {
							
						} else if(holidaysMp.containsKey(uF.getDateFormat(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)) {
							
						} else {
							alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
						}				
					}
				}
				
//				if(uF.parseToInt(rs.getString("emp_id")) == 11) {
//					System.out.println("alHalfDaysDueToLatePolicy====="+alHalfDaysDueToLatePolicy);
//				} 
				
				
				if(!alServices.contains(rs.getString("service_id"))) {  
					alServices.add(rs.getString("service_id"));
				} 
				
				hmPresentDays.put(strPresentEmpIdNew, alPresentDates);
				hmPresentWeekEndDays.put(strPresentEmpIdNew, alPresentWeekEndDates);
				hmHalfDays.put(strPresentEmpIdNew, alHalfDaysDueToLatePolicy);
				hmFullDays.put(strPresentEmpIdNew, alFullDaysDueToLatePolicy);
				
				hmServices.put(strPresentEmpIdNew, alServices);
				
				if("OUT".equalsIgnoreCase(rs.getString("in_out"))) {
					hmHoursWorked.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id"), rs.getString("hours_worked"));
				}
				hmEmpHoursWorked.put(strPresentEmpIdNew, hmHoursWorked);
				hmOverLappingHolidays.put(strPresentEmpIdNew, dblOverLappingHolidays+"");
				
				strPresentEmpIdOld = strPresentEmpIdNew;
			}
			rs.close();
			pst.close();
			
//				System.out.println("strDate== vcvx="+hmPresentDays.get("118"));
			
			
			Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
			
			if(alGradeId.size() > 0) {
				String strGradeIds = StringUtils.join(alGradeId.toArray(), ",");
				pst = con.prepareStatement("select * from salary_details where level_id in (select level_id from level_details where " +
						"level_id in (select dd.level_id from designation_details dd, grades_details gd where dd.designation_id=gd.designation_id " +
						"and gd.grade_id in("+strGradeIds+"))) and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by level_id, earning_deduction desc, salary_head_id, weight");
				rs = pst.executeQuery(); 
				while (rs.next()) {
					
					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(rs.getString("level_id"));
					if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
					
					Map<String, String> hmInnerSal = new HashMap<String, String>();
					hmInnerSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
					hmInnerSal.put("SALARY_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmInnerSal.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
					hmInnerSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					hmInnerSal.put("IS_CTC_VARIABLE", ""+uF.parseToBoolean(rs.getString("is_ctc_variable")));
					hmInnerSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmInnerSal.put("IS_ALIGN_WITH_PERK", ""+uF.parseToBoolean(rs.getString("is_align_with_perk")));
					hmInnerSal.put("IS_DEFAULT_CAL_ALLOWANCE", ""+uF.parseToBoolean(rs.getString("is_default_cal_allowance")));
					hmInnerSal.put("SALARY_TYPE", rs.getString("salary_type"));
					
					hmSalInner.put(rs.getString("salary_head_id"), hmInnerSal);
					
					hmSalaryDetails1.put(rs.getString("level_id"), hmSalInner);
					
					if(uF.parseToInt(rs.getString("salary_head_id")) != GROSS && uF.parseToInt(rs.getString("salary_head_id")) != CTC && uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT_CTC) {
						if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
							int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
	
							if (index >= 0) {
								alEmpSalaryDetailsEarning.remove(index);
								alEarningSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							} else {
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}
	
							alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
							int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
							if (index >= 0) {
								alEmpSalaryDetailsDeduction.remove(index);
								alDeductionSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							} else {
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}
							alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						}
	
						hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					}
	
				}
				rs.close();
				pst.close();
			}
			
			
//			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
//			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
//			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
////			pst = con.prepareStatement("select * from salary_details order by weight");
//			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+"," + REIMBURSEMENT_CTC + ") and org_id =? and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
//			pst.setInt(1, uF.parseToInt(getF_org()));
////			System.out.println("pst======>"+pst);
//			rs = pst.executeQuery();  
//			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
//			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
//			while(rs.next()) {
//				
//				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
//					int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//					
////					System.out.println("earning_deduction====>"+rs.getString("earning_deduction"));
////					System.out.println("alEmpSalaryDetailsEarning====>"+alEmpSalaryDetailsEarning);
////					System.out.println("alEarningSalaryDuplicationTracer====>"+alEarningSalaryDuplicationTracer);
////					System.out.println("index====>"+index);
////					System.out.println("salary_head_id====>"+rs.getString("salary_head_id"));
//					
//					if(index>=0) {
//						alEmpSalaryDetailsEarning.remove(index);
//						alEarningSalaryDuplicationTracer.remove(index);
////						alEmpSalaryDetailsEarning.add(index, rs.getString("salary_head_id"));
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					} else {
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					}
//					
//					alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
//					int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//					if(index>=0) {
//						alEmpSalaryDetailsDeduction.remove(index);
//						alDeductionSalaryDuplicationTracer.remove(index);
////						alEmpSalaryDetailsDeduction.add(index, rs.getString("salary_head_id"));
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					} else {
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					}
//					alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}
//				
//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
//			}
//			rs.close();
//			pst.close();
			
//			pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation);
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			
			Map<String, Map<String, String>> hmEmpSalary = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmEmpSalaryInner = new LinkedHashMap<String, String>();
			if(uF.parseToInt(getF_org())>0) {
//				pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation2);
				sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_salary_details esd, (select max(effective_date) as effective_date, emp_id from emp_salary_details " +
					"where effective_date<=? and is_approved= true group by emp_id) a where a.effective_date = esd.effective_date and esd.emp_id = a.emp_id " +
					"and a.emp_id in (select emp_id from employee_official_details where org_id = ? ");
				if(uF.parseToInt(strReqEmpId)>0) {
					sbQuery.append(" and emp_id ="+uF.parseToInt(strReqEmpId));
				}
				sbQuery.append(" ) and esd.salary_head_id in (select salary_head_id from salary_details where org_id =? and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false)) " +
					"order by a.emp_id, esd.earning_deduction desc");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(2, uF.parseToInt(getF_org()));
				pst.setInt(3, uF.parseToInt(getF_org()));
			} else {
//				pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation1);
				sbQuery = new StringBuilder();
				sbQuery.append("select * from emp_salary_details esd, (select max(effective_date) as effective_date, emp_id " +
					"from emp_salary_details where effective_date<=? and is_approved= true group by emp_id ) a where a.effective_date = esd.effective_date " +
					"and esd.emp_id = a.emp_id ");
				if(uF.parseToInt(strReqEmpId)>0) {
					sbQuery.append(" and a.emp_id ="+uF.parseToInt(strReqEmpId));
				}
				sbQuery.append(" and esd.salary_head_id in (select salary_head_id from salary_details where (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false))" +
					" order by a.emp_id, esd.earning_deduction desc");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));	
			}
			rs = pst.executeQuery();
//			System.out.println("----------_>"+pst);
			String strEmpIdNew1 = null;
			String strEmpIdOld1 = null;
			while(rs.next()) {
//				strEmpIdNew1 = rs.getString("empl_id");
				strEmpIdNew1 = rs.getString("emp_id");
						
				if(!alEmp.contains(strEmpIdNew1))continue;		
						
				if(strEmpIdNew1!=null && !strEmpIdNew1.equalsIgnoreCase(strEmpIdOld1)) {
					hmEmpSalaryInner = new LinkedHashMap<String, String>();
				}
				
//				Map<String, String> hmEmpInner = new HashMap<String, String>();
				
				/*
				hmEmpInner.put("SALARY_HEAD_ID", rs.getString("salary_id"));
				hmEmpInner.put("SERVICE_ID", rs.getString("service_id"));
				hmEmpInner.put("AMOUNT", rs.getString("amount"));
				hmEmpInner.put("PAY_TYPE", rs.getString("pay_type"));
				hmEmpInner.put("SALARY_HEAD_NAME", rs.getString("salary_head_name"));
				hmEmpInner.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
				hmEmpInner.put("SALARY_HEAD_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
				hmEmpInner.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
				hmEmpInner.put("SUB_SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
				
				hmEmpSalaryInner.put(rs.getString("salary_id"), hmEmpInner);
//				hmEmpPresentDays.put(strEmpIdNew1, nPresentDays+"");
				*/
				if(strEmpIdNew1!=null && strEmpIdNew1.length()>0) {
					hmEmpSalary.put(strEmpIdNew1, hmEmpSalaryInner);
				}
				
				strEmpIdOld1 = strEmpIdNew1;
				
//				if(!hmSalaryDetails.containsKey(rs.getString("salary_id"))) {
					//hmSalaryDetails.put(rs.getString("salary_id"), rs.getString("salary_head_name"));
//				}
			}
			rs.close();
			pst.close();

			Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
			if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
			
			Map<String, String> hmInnerTemp = new HashMap<String, String>();
			
			Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF); 
			Map<String, String> hmIncentives = CF.getIncentivesMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualBonus = CF.getIndividualBonusMap(con, uF, CF, strPC, strD1, strD2);
			if(hmIndividualBonus == null) hmIndividualBonus = new HashMap<String, String>();
			
			Map<String, String> hmIndividualOtherDeduction = CF.getIndividualOtherDeductionMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualOtherEarning = CF.getIndividualOtherEarningMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualOvertime = CF.getIndividualOvertimeMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualTravelReimbursement = CF.getIndividualTravelReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualMobileReimbursement = CF.getIndividualMobileReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			Map<String, String> hmIndividualOtherReimbursement = CF.getIndividualOtherReimbursementMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmIndividualMobileRecovery = CF.getIndividualMobileRecoveryMap(con, uF, CF, strPC, strD1, strD2);
			
			Map<String, String> hmReimbursement = CF.getReimbursementMap(con, uF, CF, strD1, strD2);
			Map<String, String> hmVariables  = new HashMap<String, String>();
			Map<String, String> hmBreaks  = new HashMap<String, String>();
			Map<String, String> hmBreakPolicy  = new HashMap<String, String>();
			getVariableAmount(con, uF, hmVariables, strPC);
			getBreakDetails(con, uF, hmBreaks, hmBreakPolicy, strD1, strD2);
			
			Map<String, String> hmAnnualVariables = new HashMap<String, String>();
			getAnnualVariableAmount(con, uF, hmAnnualVariables, strD1, strD2, strPC);
			
			Map<String, String> hmAllowance  = new HashMap<String, String>();
			getAllowanceAmount(con, uF, hmAllowance, strD1, strD2, strPC);
			
			Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
			Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
			
			getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
			
			Map<String, String> hmAnnualVarPolicyAmount = CF.getAnnualVariablePolicyAmount(con, uF, strFinancialYearStart, strFinancialYearEnd);
			if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();
			
			Map<String, String> hmBasicSalaryMap = CF.getSpecificSalaryData(con, BASIC);
			Map<String, String> hmDASalaryMap = CF.getSpecificSalaryData(con, DA);
			Set<String> setLeaves = null;
			Iterator<String> it = null;
			
 			List<String> alProcessingEmployee = new ArrayList<String>();
			
			LinkedHashMap<String, Map<String, String>> hmTotalSalary = new LinkedHashMap<String, Map<String, String>>();
			LinkedHashMap<String, Map<String, String>> hmTotalSalaryisDisplay = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmLoanAmt=new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
			List<String> alLoans = new ArrayList<String>();
			
			Map<String, String> hmWoHLeaves  = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			Set<String> set0 = hmEmpSalary.keySet();
			Iterator<String> it0 = set0.iterator();
			while(it0.hasNext()) {
				String strEmpId = it0.next();
				String strOrgId = hmEmpOrgId.get(strEmpId);
				int nEmpId = uF.parseToInt(strEmpId);
				String strLocation=hmEmpWlocationMap.get(strEmpId);
				String strLevel=hmEmpLevelMap.get(strEmpId);
				
				String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
				
//				Set<String> weeklyOffSet=hmWeekEnds.get(strLevel);
//				if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
				
				Set<String> weeklyOffSet = null;
				if(alEmpCheckRosterWeektype.contains(strEmpId)) {
					weeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
				} else {
					weeklyOffSet = hmWeekEnds.get(strLocation);
				}
				if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
				
				Set<String> OriginalholidaysSet=holidaysMp.get(strLocation);	
				if(OriginalholidaysSet==null)OriginalholidaysSet=new HashSet<String>();
//				} else {
				
				Set<String> holidaysSet=new HashSet<String>(OriginalholidaysSet);	
				
				holidaysSet.removeAll(weeklyOffSet);
				
				if(!alProcessingEmployee.add(strEmpId)) {
					alProcessingEmployee.add(strEmpId);
				}
				
//				System.out.println("hmPresentDays====>"+hmPresentDays);
				
				List<String> alPresentTemp = hmPresentDays.get(strEmpId);
				if(alPresentTemp==null)alPresentTemp = new ArrayList<String>();
				
				List<String> alServiceTemp = hmServices.get(strEmpId);
				if(alServiceTemp==null)alServiceTemp = new ArrayList<String>();
				
				double  dblPresent = alPresentTemp.size() - uF.parseToDouble(hmHolidayCount.get(strEmpId));
				
				List<String> alHalfDaysDueToLatePolicyTemp =hmHalfDays.get(strEmpId);
				if(alHalfDaysDueToLatePolicyTemp==null)alHalfDaysDueToLatePolicyTemp = new ArrayList<String>();
//				if(uF.parseToInt(strEmpId) == 11) {
//					System.out.println("dblPresent====="+dblPresent+"---alHalfDaysDueToLatePolicyTemp====="+alHalfDaysDueToLatePolicyTemp);
//				} 
				dblPresent -=alHalfDaysDueToLatePolicyTemp.size() * 0.5;
//				if(uF.parseToInt(strEmpId) == 11) {
//					System.out.println("after dblPresent====="+dblPresent);
//				} 
				List<String> alFullDaysDueToLatePolicyTemp = (List<String>)hmFullDays.get(strEmpId);
				if(alFullDaysDueToLatePolicyTemp==null)alFullDaysDueToLatePolicyTemp = new ArrayList<String>();
				
				dblPresent -=alFullDaysDueToLatePolicyTemp.size() * 1;
				
				Map<String,String> hmLeaves = hmLeaveDays.get(strEmpId);
				if(hmLeaves==null)hmLeaves = new HashMap<String,String>();
				
				Map<String,String> hmLeavesType = hmLeaveTypeDays.get(strEmpId);
				if(hmLeavesType==null)hmLeavesType = new HashMap<String,String>();

//				System.out.println("strEmpId= 123 ==>"+strEmpId);
//				
//				System.out.println("hmLeaves===>"+hmLeaves);
//				System.out.println("hmLeavesType===>"+hmLeavesType);

				EmployeeActivity obj = new EmployeeActivity();
				obj.request = request;
				obj.session = session;
				obj.CF = CF;
				
//				Map hmBasicSalaryMap = CF.getSpecificSalaryData(BASIC);
//				Map hmDASalaryMap = CF.getSpecificSalaryData(DA);
				
				
				setLeaves = hmLeaves.keySet();
				it = setLeaves.iterator();
//				int nLeaves = 0;
				double nOverlappingHolidaysLeaves = 0;
				double nOverlappingWeekEndsLeaves = 0;
				while(it.hasNext()) {
					String strLeaveDate = it.next();
//					String strHolidayDate = hmHolidayDates.get(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strLocation);
//					holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat()));
					String strWeekEnd = null;
					
					
					if( weeklyOffSet.contains(strLeaveDate)) {
						strWeekEnd = WEEKLYOFF_COLOR;
					}
					
					
					String strLeaveType = hmLeavesType.get(strLeaveDate);
					
					
//					System.out.println("strLeaveType===>"+strLeaveType);
//					System.out.println("strLeaveDate===>"+strLeaveDate);
//					System.out.println("hmLeavesType===>"+hmLeavesType);
//					System.out.println("alPresentTemp===>"+alPresentTemp);
					
					
//					if(strLeaveDate!=null && !strLeaveDate.equals(strHolidayDate) && !alPresentTemp.contains(strLeaveDate)) {
//						nLeaves++;
//					}
						
//					if(strLeaveDate!=null && strLeaveDate.equals(strHolidayDate)) {
//						nOverlappingHolidaysLeaves++;
//					}
					
//					if(strLeaveDate!=null && holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat())) && "H".equalsIgnoreCase(strLeaveType)) {
					if(strLeaveDate!=null && holidaysSet.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
						nOverlappingHolidaysLeaves+=0.5;
//					}else if(strLeaveDate!=null && holidaysSet.contains(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat()))) {
					}else if(strLeaveDate!=null && holidaysSet.contains(strLeaveDate)) {						
						nOverlappingHolidaysLeaves++;
					}
					
					if(strLeaveDate!=null && strWeekEnd!=null) {
						nOverlappingWeekEndsLeaves++;
					}
					
					  
//					if(uF.parseToInt(strEmpId) == 11) {
//						System.out.println(strLeaveType+"---before leave dblPresent====="+dblPresent);
//					} 
					if(strLeaveDate!=null && alPresentTemp.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)) {
//					if(strLeaveDate!=null && "H".equalsIgnoreCase(strLeaveType)) {
						//dblPresent += -1 + 0.5;
					}
//					if(uF.parseToInt(strEmpId) == 11) {
//						System.out.println(strLeaveType+"---after leave dblPresent====="+dblPresent);
//					} 
					
				}
				
				
				
				int nHolidays = holidaysSet.size();
//				int nWeekEnds = uF.getWeekEndCount(hmWeekEnds,strLocation, strD1, strD2);
//				int nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
				int nWeekEnds = 0;
/*
				if("459".equals(strEmpId)) {
					System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap);
					System.out.println("contains="+hmEmpJoiningMap.containsKey(strEmpId));
					System.out.println("Date 1="+uF.getDateFormatUtil(strD1, DATE_FORMAT));
					System.out.println("Date 2="+uF.getDateFormatUtil(strD2, DATE_FORMAT));
					System.out.println("Date 3="+uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT));
					System.out.println("isBetween="+uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT)));
				}*/
				
				if(hmEmpEndDateMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT))) {
					Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
					Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con, strD1,hmEmpEndDateMap.get(strEmpId), CF, uF,hmWeekEndHalfDates1,null);
					List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
					Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
					CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1,hmEmpEndDateMap.get(strEmpId),alEmpCheckRosterWeektype1,hmRosterWeekEndDates1,hmWeekEnds1,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates1);
					
//					Set<String> weeklyOffEndDate= hmWeekEnds1.get(strLevel);
					Set<String> weeklyOffEndDate = null;
					if(alEmpCheckRosterWeektype1.contains(strEmpId)) {
						weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
					} else {
						weeklyOffEndDate = hmWeekEnds1.get(strLocation);
					}
					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					
					nWeekEnds = weeklyOffEndDate.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("1---nWeekEnds==="+nWeekEnds);
//					}
					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
					
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con, uF,strD1, hmEmpEndDateMap.get(strEmpId));
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,strD1, strD2); 
					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,strD1, hmEmpEndDateMap.get(strEmpId)); 
					Set<String> OriginalholidaysSet1=holidaysMp1.get(strLocation);	
					if(OriginalholidaysSet1==null)OriginalholidaysSet1=new HashSet<String>();
					Set<String> holidaysSet1=new HashSet<String>(OriginalholidaysSet1);	
					holidaysSet1.removeAll(weeklyOffEndDate);
					
					nHolidays = holidaysSet1.size();
					
//					System.out.println("end date nWeekEnds====>"+nWeekEnds);
//					System.out.println("end date hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("end date strEmpId=====>"+strEmpId);
//					System.out.println("end date nHolidays=====>"+nHolidays);
					
				}else if(hmEmpJoiningMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpJoiningMap.get(strEmpId), DATE_FORMAT))) {
					Map<String, Set<String>> hmWeekEndHalfDates1 = new HashMap<String, Set<String>>();
					Map<String, Set<String>> hmWeekEnds1 = CF.getWeekEndDateList(con,hmEmpJoiningMap.get(strEmpId),strD2, CF, uF,hmWeekEndHalfDates1,null);
//					Set<String> weeklyOffEndDate= hmWeekEnds1.get(strLevel);
//					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					List<String> alEmpCheckRosterWeektype1 = new ArrayList<String>();
					Map<String, Set<String>> hmRosterWeekEndDates1 = new HashMap<String, Set<String>>();
					CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, hmEmpJoiningMap.get(strEmpId),strD2,alEmpCheckRosterWeektype1,hmRosterWeekEndDates1,hmWeekEnds1,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates1);
					
					Set<String> weeklyOffEndDate = null;
					if(alEmpCheckRosterWeektype1.contains(strEmpId)) {
						weeklyOffEndDate = hmRosterWeekEndDates1.get(strEmpId);
					} else {
						weeklyOffEndDate = hmWeekEnds1.get(strLocation);
					}
					if(weeklyOffEndDate==null)weeklyOffEndDate=new HashSet<String>();
					
					
					nWeekEnds = weeklyOffEndDate.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("2---nWeekEnds==="+nWeekEnds+"---hmEmpJoiningMap.get(strEmpId)=====>"+hmEmpJoiningMap.get(strEmpId));
//						System.out.println("2---alEmpCheckRosterWeektype1.contains(strEmpId)==="+alEmpCheckRosterWeektype1.contains(strEmpId)+"---weeklyOffEndDate=====>"+weeklyOffEndDate);
//						System.out.println("2---hmRosterWeekEndDates1==="+hmRosterWeekEndDates1.get(strEmpId)+"---hmWeekEnds1.get(strLocation)=====>"+hmWeekEnds1.get(strLocation));
//					}
//					if("554".equals(strEmpId)) {
//						System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap.get(strEmpId));
//
//						System.out.println("nWeekEnds="+nWeekEnds);
//						System.out.println("weeklyOffEndDate="+weeklyOffEndDate);
//					}
					
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds1,strLocation, hmEmpJoiningMap.get(strEmpId), strD2);
					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con, hmEmpJoiningMap.get(strEmpId), strD2, CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
					
					
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con, uF,hmEmpJoiningMap.get(strEmpId), strD2);
//					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request, uF,strD1, strD2);
					Map<String,Set<String>> holidaysMp1=CF.getHolidayList(con,request,uF,hmEmpJoiningMap.get(strEmpId), strD2);
					Set<String> OriginalholidaysSet1=holidaysMp1.get(strLocation);	
					if(OriginalholidaysSet1==null)OriginalholidaysSet1=new HashSet<String>();
					Set<String> holidaysSet1=new HashSet<String>(OriginalholidaysSet1);	
					holidaysSet1.removeAll(weeklyOffEndDate);
					if("554".equals(strEmpId)) {
//						System.out.println("hmEmpJoiningMap="+hmEmpJoiningMap.get(strEmpId));

//						System.out.println("holidaysSet1="+holidaysSet1);
//						System.out.println("weeklyOffEndDate="+weeklyOffEndDate);
					}
					nHolidays =holidaysSet1.size();
					
//					System.out.println("start date nWeekEnds====>"+nWeekEnds);
//					System.out.println("start date hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("start date strEmpId=====>"+strEmpId);
//					System.out.println("start date nHolidays=====>"+nHolidays);
					
				} else {
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					
					nWeekEnds=weeklyOffSet.size();
//					if(strEmpId.equals("225"))      {               
//						System.out.println("3---nWeekEnds==="+nWeekEnds);
//					}
//					System.out.println("nWeekEnds====>"+nWeekEnds);
//					System.out.println("hmWeekEnds====>"+hmWeekEnds);
//					System.out.println("strEmpId=====>"+strEmpId);
//					System.out.println("strD1=====>"+strD1);
//					System.out.println("strD2====>"+strD2);
					
					
//					hmHolidays = new HashMap<String, String>();
//					hmHolidayDates = new HashMap<String, String>();
//					CF.getHolidayList(strD1, strD2, CF, hmHolidayDates, hmHolidays, hmWeekEnds, true);
//					nHolidays = uF.parseToInt(hmHolidays.get(strLocation));
				}
				
				/*int nWeekEnds = 0;
				if(strEmpId.equalsIgnoreCase("460")) {
					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
				}*/
				
				List<String> alWorkingWeekEnds = hmPresentWeekEndDays.get(strEmpId);
				if(alWorkingWeekEnds==null)alWorkingWeekEnds=new ArrayList<String>();
				int nWorkingWeekEnds = alWorkingWeekEnds.size();
				
				List<String> alOverlappingWeekEndDates = hmPresentWeekEndDays.get(strEmpId);
				if(alOverlappingWeekEndDates==null)alOverlappingWeekEndDates = new ArrayList<String>();
//				int nOverlappingWeekends = alOverlappingWeekEndDates.size();
				
				double dblOverlappingHolidays = uF.parseToDouble(hmOverLappingHolidays.get(strEmpId));
				
				double dblTotalLeaves =  uF.parseToDouble(hmLeavesType.get("COUNT"));
				double dblActualLeaves =  dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//				System.out.println(strEmpId+"====dblTotalLeaves======>"+dblTotalLeaves);
//				System.out.println(strEmpId+"====dblActualLeaves======>"+dblActualLeaves);
//				System.out.println(strEmpId+"====nOverlappingHolidaysLeaves======>"+nOverlappingHolidaysLeaves);
//				System.out.println(strEmpId+"====nOverlappingWeekEndsLeaves======>"+nOverlappingWeekEndsLeaves);
				
				/**
				 * Long Leave code start
				 * */
				double dblLongLeave = uF.parseToDouble(hmLongLeaves.get(strEmpId));
//				if(strEmpId.equals("459")) {
//					System.out.println("dblLongLeave======>"+dblLongLeave);
//					System.out.println("before dblPresent======>"+dblPresent);
//					System.out.println("before dblTotalLeaves======>"+dblTotalLeaves);
//					System.out.println("before dblActualLeaves======>"+dblActualLeaves);
//				}
				dblTotalLeaves = dblTotalLeaves - dblLongLeave; 
				hmLeavesType.put("COUNT", ""+dblTotalLeaves);
//				if(strEmpId.equals("459")) {
//					System.out.println("after dblTotalLeaves======>"+dblTotalLeaves);
//				}
				/**
				 * Long Leave code end
				 * */
				
//				double dblTotalPgetEmpServiceTaxresentDays = dblPresent + dblActualLeaves + nHolidays + nWeekEnds;
				
//				double dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves ;
//				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves) +"");
				double dblTotalPresentDays = 0.0d;
				if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
					dblTotalPresentDays = 0.0d;
					hmPresentDays1.put(strEmpId, "0");
				} else {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves ;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves) +"");
				}
//				if(strEmpId.equals("225"))      {               
//					System.out.println("1 dblPresent==="+dblPresent+"---nHolidays==="+nHolidays+"---dblOverlappingHolidays==="+dblOverlappingHolidays+"---nWeekEnds==="+nWeekEnds+"---dblTotalLeaves==="+dblTotalLeaves+"---nOverlappingHolidaysLeaves==="+nOverlappingHolidaysLeaves+"---nOverlappingWeekEndsLeaves==="+nOverlappingWeekEndsLeaves);
//					System.out.println("1 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("1 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}

				if(dblTotalPresentDays>nTotalNumberOfDays) {
					dblTotalPresentDays = nTotalNumberOfDays;
					
				}
//				if(strEmpId.equals("225"))      {               
//					System.out.println("2 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("2 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}
				
				/*https://www.arclab.com/en/amlc/list-of-smtp-and-pop3-servers-mailserver-list.html

				// hard coded condition and logic needs to be implemented for this condition. --Vipin 21-01-2013
				if(dblPresent>=22) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds)+"");
				}else if(dblPresent>=20) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +4 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +4- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=14) {
					dblTotalPresentDays = dblPresent +3 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent  +3- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=8) {
					dblTotalPresentDays = dblPresent +2 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +2- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=4) {
					dblTotalPresentDays = dblPresent  +1 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +1- nOverlappingHolidaysLeaves)+"");
				} else {
					dblTotalPresentDays = dblPresent  + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent  - nOverlappingHolidaysLeaves)+"");
				} getEmpServiceTax
				*/
				
				// FOR KP
				 
				/*// hard coded condition and logic needs to be implemented for this condition. --Vipin 21-01-2013
				if(dblPresent>15) {
					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=8) {
					dblTotalPresentDays = dblPresent +5 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +5- nOverlappingHolidaysLeaves)+"");
				}else if(dblPresent>=4) {
					dblTotalPresentDays = dblPresent  +1 + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent +1- nOverlappingHolidaysLeaves)+"");
				} else {
					dblTotalPresentDays = dblPresent  + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent - nOverlappingHolidaysLeaves )+"");
				}
				
				*/
				
				//comment for solar start 
				
//				Calendar calW = GregorianCalendar.getInstance();
//				Calendar calW1 = GregorianCalendar.getInstance();
//				Calendar calW2 = GregorianCalendar.getInstance();
//				calW.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//				calW.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
//				calW.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//			
//				int nDiff = uF.parseToInt(uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
//				int nDeductionW = 0;
//				List alPW = hmPresentDays.get(strEmpId);
//				if(alPW==null)alPW=new ArrayList();
//				
//				List alPWE = hmPresentWeekEndDays.get(strEmpId);
//				if(alPWE==null)alPWE=new ArrayList();
//				
//				
//*/				 
//				
//				
//				for(int i=0; i<nDiff; i++) {
//					String strDW = uF.getDateFormat(calW.get(Calendar.DATE)+"/"+(calW.get(Calendar.MONTH)+1)+"/"+calW.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//					String strDH = uF.getDateFormat(calW.get(Calendar.DATE)+"/"+(calW.get(Calendar.MONTH)+1)+"/"+calW.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//					calW1 = (Calendar)calW.clone();
//					  
//					
//					
//					
//					
//					if(hmWeekEnds.containsKey(strDW+"_"+strLocation) && i>0) {
//						  
//						calW1.add(Calendar.DATE, -1);
//						strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//						
//						if(!hmWeekEnds.containsKey(strDW+"_"+strLocation) && !alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//							
//							calW1.add(Calendar.DATE, 2);
//							strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//							
//							
//							if(hmWeekEnds.containsKey(strDW+"_"+strLocation)) {
//								
//								calW1.add(Calendar.DATE, 1);
//								strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//								
//								if(!alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//									nDeductionW+=2;
//									
//									
//								}
//								
//							} else {
//								
//								if(!alPW.contains(strDW) && !hmLeaves.containsKey(strDW)) {
//									nDeductionW+=1;
//									
//								}
//							}
//							
//						}
//						
//						
//					}
//					
//					
//					
//					
//				/*	
//					
//					
//					calW1 = (Calendar)calW.clone();
//					if(hmHolidayDates.containsKey(strDH+"_"+strLocation) && alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && i>0) {
//						calW1.add(Calendar.DATE, -1);
//						strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//						
//						if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))  && !hmLeaves.containsKey(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && !hmHolidayDates.containsKey(strDH+"_"+strLocation) && !hmWeekEnds.containsKey(strDH+"_"+strLocation)) {
//							calW1.add(Calendar.DATE, 1);
//							strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//							if(hmHolidayDates!=null && hmHolidayDates.containsKey(strDH+"_"+strLocation) && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))) {
//								nDeductionW+=1;
//								
//								
//							}
//						}else if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && !hmLeaves.containsKey(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && hmHolidayDates!=null && hmHolidayDates.containsKey(strDH+"_"+strLocation)) {
//							calW1.add(Calendar.DATE, -1);
//							strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
//							if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))) {
//								nDeductionW+=1;
//							  
//								
//							}  
//						}
//					}
//					*/
//					
//					
//					
//					
//					
//					calW2 = (Calendar)calW.clone();
//					strDH = uF.getDateFormat(calW2.get(Calendar.DATE)+"/"+(calW2.get(Calendar.MONTH)+1)+"/"+calW2.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//					
////					if(hmLeaves.containsKey(strDH) && hmWeekEnds.containsKey(strDH+"_"+strLocation)) {
////						dblOverlappingHolidays++;
////						/*if(strEmpId.equalsIgnoreCase("652")) {
////							System.out.println("strDH= 5 ="+strDH);
////						}*/
////					}
//					
//					
//					
//					calW.add(Calendar.DATE, 1);
//				}
//				  
//		
//				if(dblPresent>0 || dblActualLeaves>0) {
////					if(dblPresent>0 && (dblPresent+dblActualLeaves)<6) {
//					if(dblPresent>0 && (dblPresent+dblTotalLeaves)<6) {
//						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + dblActualLeaves +nWeekEnds;
//						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nOverlappingHolidaysLeaves +nWeekEnds)+"");
//						
//						dblTotalPresentDays = dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves + dblTotalLeaves - nDeductionW ;
//						hmPresentDays1.put(strEmpId, (dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves - nDeductionW)+"");
//						
//					}else if((dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves)>nDeductionW) {
////						dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves - nDeductionW ;
////						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds- nOverlappingHolidaysLeaves - nDeductionW)+"");
//						
//						dblTotalPresentDays = dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves + dblTotalLeaves - nDeductionW ;
//						hmPresentDays1.put(strEmpId, (dblPresent + nHolidays - nOverlappingHolidaysLeaves - dblOverlappingHolidays + nWeekEnds - nOverlappingWeekEndsLeaves - nDeductionW)+"");
//					} else {
//						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + dblActualLeaves ;
//						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nOverlappingHolidaysLeaves )+"");
//					}
//					
//				} else {
//					dblTotalPresentDays = 0 ;
//					hmPresentDays1.put(strEmpId, 0+"");
//				}
//				
//				
//				
//				
//				
//				
//				
//				
//				// Exclusive condition for KPCA urgent basis
//				/**
//				 * Exclusive condition for KPCA urgent basis
//				 * Needs to be replaced with the sandwich leave logic
//				 * @author Vipin Razdan (28-Nov-2013)
//				 * 
//				 * */
//
				//comment for solar end
				
				/*
				if(true) { // if daily calculation employees
					dblTotalPresentDays = dblPresent + dblActualLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
				}
				*/
				// For ANC all daily employees are calculated Overtime differently
				if(hmEmpPaycycleDuration.get(strEmpId)!=null && !hmEmpPaycycleDuration.get(strEmpId).equalsIgnoreCase("M")) {
//					dblTotalPresentDays = dblPresent + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
					if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
						dblTotalPresentDays = 0.0d;
						hmPresentDays1.put(strEmpId, "0");
					} else {
						dblTotalPresentDays = dblPresent + dblActualLeaves;
						hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
					}
				} 
//				if(strEmpId.equals("225"))      {               
//					System.out.println("3 dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("3 nTotalNumberOfDays==="+nTotalNumberOfDays);
//				}
				
				if(uF.parseToBoolean(CF.getIsPaycycleAdjustment())) {
					Calendar calMonth1 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
					calMonth1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
					calMonth1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
					calMonth1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
					
					Calendar calMonth2 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
					calMonth2.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
					calMonth2.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
					calMonth2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
					
					int nMonth1 = calMonth1.getActualMaximum(Calendar.DATE);
					int nMonth2 = calMonth2.getActualMaximum(Calendar.DATE); 
					
					dblPresent += (nMonth2 - nMonth1);
					
//					dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves)+"");
					if(dblPresent == 0.0d &&  dblTotalLeaves == 0.0d) {
						dblTotalPresentDays = 0.0d;
						hmPresentDays1.put(strEmpId, "0");
					} else {
						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves)+"");
					}
//					if(strEmpId.equals("225")) {               
//						System.out.println("4 dblTotalPresentDays==="+dblTotalPresentDays);
//						System.out.println("4 nTotalNumberOfDays==="+nTotalNumberOfDays);
//					}
				}
				
				int nTotalNumberOfDaysForCalc = nTotalNumberOfDays; 
				
				/**   AWD  = Actual Working Days
				 * */
				
//				if("AWD".equalsIgnoreCase(CF.getStrOSalaryCalculationType())) {
				if("AWD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
//					dblTotalPresentDays = dblPresent + dblTotalLeaves;
					
					if(dblPresent>0) {
//						dblPresent = dblPresent + nHolidays ;
						dblPresent = dblPresent;
//					} else {
//						dblPresent = dblPresent ;
					}
					
					/**actual paid leaves
					 * */
					double dblWoHLwaves = dblTotalLeaves;
					hmWoHLeaves.put(strEmpId, ""+dblWoHLwaves);
					
					dblTotalLeaves = (dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves); 
					hmLeavesType.put("COUNT", ""+dblTotalLeaves);
					
					//dblPresent = (dblPresent - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves);
					
					dblTotalPresentDays = dblPresent + dblTotalLeaves;
					hmPresentDays1.put(strEmpId, (dblPresent)+"");
					
//					strTotalDays = (nTotalNumberOfDays - nHolidays - nWeekEnds)+"";
//					System.out.println("hmLeaveTypeDays==="+hmLeavesType);
//					System.out.println("dblTotalLeaves==="+dblTotalLeaves+" dblActualLeaves="+dblActualLeaves+" nOverlappingHolidaysLeaves==="+nOverlappingHolidaysLeaves);
					
//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nHolidays - nWeekEnds; 

//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nWeekEnds;
//					int nWeekEnds1 = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					int nWeekEnds1 = weeklyOffSet.size();

//					System.out.println("strEmpId==="+strEmpId);
//					System.out.println("nTotalNumberOfDays==="+nTotalNumberOfDays);
//					System.out.println("nWeekEnds1==="+nWeekEnds1);
//					System.out.println("nHolidays==="+nHolidays);
					
//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nWeekEnds1; 
					nTotalNumberOfDaysForCalc = (nTotalNumberOfDays - nWeekEnds1) - nHolidays;
					
//					System.out.println("nWeekEnds1====>"+nWeekEnds1);
//					System.out.println("nWeekEnds====>"+nWeekEnds);
//					System.out.println("strEmpId====>"+strEmpId);
//					System.out.println("nTotalNumberOfDays====>"+nTotalNumberOfDays);
//					System.out.println("nTotalNumberOfDaysForCalc=====>"+nTotalNumberOfDaysForCalc);
					
					strTotalDays = nTotalNumberOfDaysForCalc +"";
					
//				}else if("AFD".equalsIgnoreCase(CF.getStrOSalaryCalculationType())) {
				}else if("AFD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
					if(dblPresent>0) {
						dblPresent = dblPresent + nHolidays ;
//					} else {
//						dblPresent = dblPresent ;
					}
//					nTotalNumberOfDaysForCalc = uF.parseToInt(CF.getStrOSalaryCalculationDays());
//					strTotalDays = CF.getStrOSalaryCalculationDays();
					nTotalNumberOfDaysForCalc = uF.parseToInt(hmOrg.get("ORG_SALARY_FIX_DAYS"));
					strTotalDays = uF.showData(hmOrg.get("ORG_SALARY_FIX_DAYS"), "");
					
//					int nWeekEnds1 = uF.getWeekEndDateCount(hmWeekEnds,strLocation, strD1, strD2);
					int daysDiff = nTotalNumberOfDays - nTotalNumberOfDaysForCalc; 	
					dblTotalPresentDays=dblTotalPresentDays-daysDiff;
					
//					if(strEmpId.equals("65")) {
//					System.out.println("daysDiff==="+daysDiff);
//					System.out.println("dblTotalPresentDays==="+dblTotalPresentDays);
//					System.out.println("dblPresent==="+dblPresent);
//					}
					hmPresentDays1.put(strEmpId,(uF.parseToDouble(hmPresentDays1.get(strEmpId))-daysDiff)+"");
//					hmPresentDays1.put(strEmpId, (dblPresent-daysDiff)+"");
				}
				
				hmTotalDays.put(strEmpId, strTotalDays);
//				System.out.println("dblTotalLeaves=>"+dblTotalLeaves);
//				System.out.println("nOverlappingHolidaysLeaves=>"+nOverlappingHolidaysLeaves);
//				System.out.println("nPresent=>"+dblPresent);
//				System.out.println("dblActualLeaves=>"+dblActualLeaves);
//				System.out.println("nHolidays=>"+nHolidays);
//				System.out.println("nWeekEnds=>"+nWeekEnds);
//				System.out.println("dblTotalPresentDays=>"+dblTotalPresentDays);
				
//				log.debug(" strEmpId= 123 =>"+strEmpId);
//				log.debug(" nHolidays==>"+nHolidays);
//				log.debug(" hmHolidays==>"+hmHolidays);
//				log.debug(" strLocation==>"+strLocation);
				
				/**
				 * 
				 * Prem Motors Break Policies
				 * 
				 * */
				
				int nBreaks = uF.parseToInt(hmBreaks.get(strEmpId+"_-2"));
//				double dblAmt = uF.parseToDouble(hmBreakPolicy.get("-2_"+getF_org()));
//				dblTotalPresentDays -= (nBreaks * dblAmt); 
//				if(nBreaks>0) {
//					hmPresentDays1.put(strEmpId, (dblTotalPresentDays)+"");
//				}
				
				/**
				 *   The attendance dependency calculation is for those employees who are not 
				 *   attendance dependent and will get the full salary irrespective they clocking on.
				 */
				
				boolean isAttendance = uF.parseToBoolean(hmAttendanceDependent.get(strEmpId));
				if(!isAttendance) {
//					dblTotalPresentDays = nTotalNumberOfDays;
					dblTotalPresentDays = nTotalNumberOfDaysForCalc;
//					if(strEmpId.equals("65")) {
//						System.out.println("dblTotalPresentDays"+dblTotalPresentDays);
//					}
//					if(strEmpId.equals("225")) {               
//						System.out.println("5 dblTotalPresentDays==="+dblTotalPresentDays);
//						System.out.println("5 nTotalNumberOfDays==="+nTotalNumberOfDays);
//					}
				}
				
				hmPaidDays.put(strEmpId, dblTotalPresentDays+"");
				
//				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays+nWeekEnds)+"");
				
//				double dblIncrement = 0;
				
				Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strLevel);
				if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>();
				
				double dblIncrementBasic = getIncrementCalculationBasic(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
				double dblIncrementDA = getIncrementCalculationDA(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
				
				Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				
				if((dblIncrementBasic>0 || dblIncrementDA>0) && getApprovePC()!=null) {
//					hmInner = CF.getSalaryCalculation(nEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, 0, strLevel, uF, CF);
					hmInner = CF.getSalaryCalculation(con,hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2,hmSalInner, null, hmEmpSalCalStatus.get(strEmpId));
//					System.out.println("AP/6305--if");
					//obj.processActivity(con,1, uF.parseToInt(strEmpId), uF.getDateFormat(strD2, DATE_FORMAT, DBDATE), CF,uF,"");
					//obj.insertEmpActivity(uF, 1, strEmpId);
				} else {  
//					System.out.println("AP/6305--else");
					hmInner = CF.getSalaryCalculation(con,hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2,hmSalInner, null, hmEmpSalCalStatus.get(strEmpId));
				}
				
//				Map<String, Map<String,String>> hmInnerActualCTC = CF.getSalaryCalculation(con,hmInnerisDisplay, nEmpId, nTotalNumberOfDaysForCalc, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2);
				
				Map<String, String> hmPaidSalaryInner = hmEmpPaidSalary.get(strEmpId);
				
				int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
				Map<String, String> hmPerkAlignAmount = new HashMap<String, String>();
				CF.getPerkAlignAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel), hmPerkAlignAmount);
				
				double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
				double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
				
				hmHoursWorked = hmEmpHoursWorked.get(strEmpId);
				if(hmHoursWorked==null)hmHoursWorked = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
				
				if(hmIndividualOtherEarning.size()>0 && !hmInner.containsKey(OTHER_EARNING+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(OTHER_EARNING+"", hmInnerTemp);
				}
				
				if(hmIndividualOtherReimbursement.size()>0 && !hmInner.containsKey(OTHER_REIMBURSEMENT+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(OTHER_REIMBURSEMENT+"", hmInnerTemp);
				}
//				if(hmEmpServiceTaxMap.size()>0 && !hmInner.containsKey(SERVICE_TAX+"")) {
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(SERVICE_TAX+"", hmInnerTemp);
//					
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(SWACHHA_BHARAT_CESS+"", hmInnerTemp);
//					
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(KRISHI_KALYAN_CESS+"", hmInnerTemp);
//				}
				
				if (hmEmpServiceTaxMap.size() > 0 && !hmInner.containsKey(CGST + "")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(CGST + "", hmInnerTemp);

					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(SGST + "", hmInnerTemp);
				}
				
//				if(hmIndividualOtherDeduction.size()>0 && !hmInner.containsKey(OTHER_DEDUCTION+"")) {
//					hmInnerTemp = new HashMap<String, String>();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "D");
//					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//					hmInner.put(OTHER_DEDUCTION+"", hmInnerTemp);
//				}
				
				if(hmInner.size()>0 && hmInner.containsKey(TDS+"")) {
					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp=hmInner.get(TDS+"");
					hmInnerTemp.put("AMOUNT", hmInnerTemp.get("AMOUNT"));
					hmInnerTemp.put("EARNING_DEDUCTION", "D");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.remove(TDS+"");
					hmInner.put(TDS+"", hmInnerTemp);
				}
				
//				System.out.println("hmPresentDays1====>"+hmPresentDays1);
//				System.out.println("nTotalPresentDays====>"+dblTotalPresentDays);
				
//				System.out.println("hmInner==>"+hmInner);
//				System.out.println("nTotalNumberOfDaysForCalc==>"+nTotalNumberOfDaysForCalc);
				
//				Map hm=new HashMap();
				Map<String, String> hmTotal=new HashMap<String, String>();
				
				Set<String> set1 = hmInner.keySet();
				Iterator<String> it1 = set1.iterator();
				
				double dblGrossPT = 0;
				double dblGross = 0;
				double dblGrossTDS = 0;
				double dblDeduction = 0;
//				boolean isDefinedEarningDeduction = false;
				Set<String> setContriSalHead = new HashSet<String>();
				
				while(it1.hasNext()) {
					String strSalaryId = it1.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
//					System.out.println("AP/6415---nSalayHead="+nSalayHead);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					
					String strMulCal = hm.get("MULTIPLE_CALCULATION");
					List<String> al = new ArrayList<String>();
					if(strMulCal != null && !strMulCal.equals("")) {
						al = Arrays.asList(strMulCal.trim().split(","));
					}
					if(al != null && al.contains(""+EMPLOYER_EPF)) {
						setContriSalHead.add(""+EMPLOYER_EPF);
					}
					if(al != null && al.contains(""+EMPLOYER_ESI)) {
						setContriSalHead.add(""+EMPLOYER_ESI);
					}
					if(al != null && al.contains(""+EMPLOYER_LWF)) {
						setContriSalHead.add(""+EMPLOYER_LWF);
					}
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							
							/*if(strEmpId.equals("459")) {
								System.out.println(nSalayHead+"  aa hmTotal=====>"+hmTotal);
							}*/
							
							switch(nSalayHead) {
							/**********  OVER TIME   *************/
								case OVER_TIME:  
								 
//									isDefinedEarningDeduction = true;
//									double dblOverTime = getOverTimeCalculationL(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC, hmOverTimeMap, hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime);
//									double dblOverTime = getOverTimeCalculationHours(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC,  hmEmpLevelMap, dblStandardHrs,  nTotalNumberOfDaysForCalc, hmIndividualOvertime,hmEmpOverTimeHours,hmEmpOverTimeLevelPolicy, hmEmpRosterHours, hmWlocationMap, nTotalNumberOfDays, nWeekEnds,nHolidays,dblPresent,dblTotalPresentDays,holidaysSet,weeklyOffSet,hmEmpWlocationMap);
									double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
									
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
//									dblOverTime = Math.round(dblOverTime);
									dblGross += dblOverTime;
									dblGrossTDS += dblOverTime;
									
								break;
								
								case LEAVE_ENCASHMENT:  
							
							//===start parvze date: 15-04-2022===		
									if(uF.parseToInt(empFinalPC) == uF.parseToInt(strPC)){
										double leaveEncashmentAmt = getLeaveEncashmentAmtDetailsNew(con, uF, CF, strEmpId, hmLeaveEncashment, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, dblIncrementBasic, dblIncrementDA);
	//									double leaveEncashmentAmt = 0.0d; getLeaveEncashmentAmtDetails(con, uF, CF, strEmpId, 0.0d, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, dblIncrementBasic, dblIncrementDA);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),leaveEncashmentAmt));
										
										dblGross += leaveEncashmentAmt;
										dblGrossTDS += leaveEncashmentAmt;
									}
							//===end parvez date: 15-04-2022===		
									
								break;
								
								case BONUS:
									/*// Bonus is paid independent of paycycle -- 
									
									if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())) {
//										isDefinedEarningDeduction = true;
										double dblBonusAmount = getBonusCalculation(con, uF, strEmpId,strD2, hmEmpLevelMap, hmInnerActualCTC, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap, CF, hmIndividualBonus);
										hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblBonusAmount)));
//										dblBonusAmount = Math.round(dblBonusAmount);
										dblGross += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;
									}*/
									double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
//									dblBonusAmount = Math.round(dblBonusAmount);
									dblGross += dblBonusAmount;
									dblGrossTDS += dblBonusAmount;
									
								break;
								
								case EXGRATIA:
									
									double dblExGratiaAmount = getExGratiaAmount(con,uF,CF,strEmpId,strD1,strD2,strPC);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
									dblGross += dblExGratiaAmount;
									dblGrossTDS += dblExGratiaAmount;
									
								break;
								
								case AREARS:

//									isDefinedEarningDeduction = true;
									double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
//									dblArearAmount = Math.round(dblArearAmount);
									dblGross += dblArearAmount;
									dblGrossTDS += dblArearAmount;
									
								break; 
								
								case INCENTIVES:
									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
//									dblIncentiveAmount += uF.parseToDouble(hm.get("AMOUNT"));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
									dblGross += dblIncentiveAmount;
									dblGrossTDS += dblIncentiveAmount;
								break;
								
								case REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
//									dblReimbursementAmount = Math.round(dblReimbursementAmount);
									dblGross += dblReimbursementAmount;
								break;
								
								case TRAVEL_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
//									dblTravelReimbursementAmount = Math.round(dblTravelReimbursementAmount);
									dblGross += dblTravelReimbursementAmount;
								break;
								
								case MOBILE_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
//									dblMobileReimbursementAmount = Math.round(dblMobileReimbursementAmount);
									dblGross += dblMobileReimbursementAmount;
								break;
								
								case OTHER_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
//									dblOtherReimbursementAmount = Math.round(dblOtherReimbursementAmount);
									dblGross += dblOtherReimbursementAmount;
								break;
								
								case OTHER_EARNING:
//									isDefinedEarningDeduction = true;
									double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
//									dblOtherEarningAmount = Math.round(dblOtherEarningAmount);
									dblGross += dblOtherEarningAmount;
								break;
								
								case SERVICE_TAX:
									
//									isDefinedEarningDeduction = true;
									double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
//									dblServiceTaxAmount = Math.round(dblServiceTaxAmount);

									/**
									 * @author Vipin
									 * 25-Mar-2014
									 * KP Condition
									 * @comment = service tax is not included while calculating TDS
									 * */
									
									dblGross += dblServiceTaxAmount;  
									dblGrossPT += dblServiceTaxAmount;
									dblGrossTDS += dblServiceTaxAmount;  
									  
									break;	
								
								case SWACHHA_BHARAT_CESS:
									double dblGrossAmt = dblGross;
									double dblServiceTaxAmt = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
									double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
									dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
									
									double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));
									
									dblGross += dblSwachhaBharatCess;  
									dblGrossPT += dblSwachhaBharatCess;
									dblGrossTDS += dblSwachhaBharatCess;  
									  
									break;
								
								case KRISHI_KALYAN_CESS:
									double dblGrossAmt1 = dblGross;
									double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
									double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
									dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
									
									double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
									
									dblGross += dblKrishiKalyanCess;  
									dblGrossPT += dblKrishiKalyanCess;
									dblGrossTDS += dblKrishiKalyanCess;  
									  
									break;	
									
								case CGST :
									double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									break;
								
								case SGST :
									double dblGrossAmt2 = dblGross;
									double dblCGSTAmt = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossAmt2 = dblGrossAmt2 - dblCGSTAmt;
									
									double dblSGSTAmount = calculateSGST(con, uF, strEmpId, dblGrossAmt2, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									break;	
								
								default:
									
									if(uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
										double dblPerkAlignAmount = 0.0d;
										if(hmPerkAlignAmount.containsKey(strSalaryId)) {
											dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
										}
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount)); 
										dblGross += dblPerkAlignAmount;
										dblGrossPT += dblPerkAlignAmount;
										dblGrossTDS += dblPerkAlignAmount;	
										
									} else if (!uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
										dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
									} else if (uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
										if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										}
									} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"))));
										dblGross += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
									} else if(uF.parseToInt(strSalaryId)!=GROSS) {
										boolean isMultipePerWithParticularHead = false;
										if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
											isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional);
										}
										if(!isMultipePerWithParticularHead) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT")))); 
											dblGross += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										}
									}
									/*if(strEmpId.equals("459")) {
										System.out.println("hmTotal=====>"+hmTotal);
									}  */ 
									
								break;
							}
							
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						
						/**********  EPF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								Map<String,String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF+"");
								double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblEEPF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
							}
							
							break;
							
						/**********  EPF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblERPF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
							}
							 
							break;
							
						case LOAN:
							
//							System.out.println("Calculating Loan===>");
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {   
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
								if(strEmpId.equalsIgnoreCase("686")) {
//									System.out.println(strEmpId+" paid dblLoan==>=>"+dblLoan);
//									System.out.println(strEmpId+"  1 dblGrossTDS==>=>"+dblGrossTDS);
//									System.out.println(strEmpId+"  1 paid loan==>=>"+hmTotal.get(strSalaryId));    
								}
								
								CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2,strEmpId);
							} else {
								
								double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
								dblDeduction += dblLoanAmt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
								
//								if(strEmpId.equalsIgnoreCase("683")) {
//									System.out.println(strEmpId+"  dblLoanAmt==>=>"+dblLoanAmt);
//									System.out.println(strEmpId+"  1 dblGrossTDS==>=>"+dblGrossTDS);
//									System.out.println(strEmpId+"  1 loan==>=>"+hmTotal.get(strSalaryId));    
//								}
								           
								if(true) {
//									dblGrossTDS = dblGross - dblLoanAmt;
									dblGrossTDS = dblGrossTDS - dblLoanAmt; 
								}
							}
							
							break;   
							
						case MOBILE_RECOVERY:
							
//							isDefinedEarningDeduction = true; 
							if(hmPaidSalaryInner!=null) {
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
							} else {
								double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
								dblDeduction += dblIndividualMobileRecoveryAmt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
							}
							
							break;		
							
						
							
						default:
							if(hmPaidSalaryInner!=null) {
								dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								
								/*if(nSalayHead==VOLUNTARY_EPF) {
									continue;
								}*/
								
								if(hmAllowance.containsKey(strEmpId+"_"+strSalaryId)) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId))));
									dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId));
								} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_D")) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"))));
									dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
								} else if(uF.parseToInt(strSalaryId)!=PROFESSIONAL_TAX && uF.parseToInt(strSalaryId)!=TDS) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
									dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));	
								}	
							}
							break;
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross));
				}
				
				
				/**
				 * Multiple cal start
				 * */
				Map<String, String> hmContriSalHeadAmt = new HashMap<String, String>();
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
//					double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
					double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap);
					hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
//					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
					hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
					double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
					hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
				}
				Iterator<String> itMulti = hmInner.keySet().iterator();
				while(itMulti.hasNext()) {
					String strSalaryId = itMulti.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						}else {
							double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblGross += dblMulCalAmt;
							dblGrossTDS += dblMulCalAmt;
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if(hmPaidSalaryInner!=null) {
							dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblDeduction += dblMulCalAmt;
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross));
				}
				/**
				 * Multiple cal end 
				 * */
				
				/**
				 * Other cal start
				 * */
				Iterator<String> itOther = hmInner.keySet().iterator();
				while(itOther.hasNext()) {
					String strSalaryId = itOther.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					Map<String,String> hm = hmInner.get(strSalaryId);
					if(hm==null) {  
						hm = new HashMap<String,String>();
					}
//					isDefinedEarningDeduction = false;
					String str_E_OR_D = hm.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						/**********  	 TAX   *************/
							case EMPLOYEE_EPF:
								if(hmPaidSalaryInner!=null) {
									double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblDeduction += dblPt;
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
								} else {
									double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, null, null, false, hmArearAmountMap);
									dblDeduction += dblEEPF;
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
								}
								break;
						case PROFESSIONAL_TAX:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								/**
								 * KP Condition
								 * 
								 * */      
								
//								double dblPt = calculateProfessionalTax(con, uF,strD2, dblGrossPT, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId));
							 	double dblPt = calculateProfessionalTax(con, uF,strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
								dblDeduction +=dblPt;

								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							}
							
							break;
						
						/**********  TDS   *************/
						case TDS:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblTDS;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							} else {
//								double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//								double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//								double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
//								
//								String[] hraSalaryHeads = null;
//								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
//									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//								}
//								
//								double dblHraSalHeadsAmount = 0;
//								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
//									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
//								}
//								
//								Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
//								if(hmPaidSalaryDetails==null) {hmPaidSalaryDetails=new HashMap<String, String>();}
//								
//								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
								
								if(strEmpId.equalsIgnoreCase("686")) {
//									System.out.println(strEmpId+" before dblGrossTDS==>=>"+dblGrossTDS);
								}   
								 
								if(hmEmpServiceTaxMap.containsKey(strEmpId)) {
									dblGrossTDS = dblGross;
									
//									double dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
//									dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
//									
//									double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
//									
//									double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
									
									double dblCGST = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossTDS = dblGrossTDS - dblCGST;

									double dblSGST = uF.parseToDouble(hmTotal.get(SGST + ""));
									dblGrossTDS = dblGrossTDS - dblSGST;
								}
								
								/**
								 * (dblBasic + dblDA) we use dblHraSalHeadsAmount
								 * */
//								double dblTDS = calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//										nPayMonth,
//										strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, hmEmpGenderMap.get(strEmpId),  hmEmpAgeMap.get(strEmpId), hmEmpStateMap.get(strEmpId),
//										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//										hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
								double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
										strFinancialYearEnd, strEmpId, hmEmpLevelMap);
								
								dblDeduction += dblTDS;
																
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							}
							break;
							
							/**********  ESI EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYER CONTRIBUTION   *************/
							
							/**********  ESI EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction +=dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYEE CONTRIBUTION   *************/	
							
							/**********  LWF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							
							/**********  /LWF EMPLOYER CONTRIBUTION   *************/
							
							/**********  LWF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							/**********  /LWF EMPLOYEE CONTRIBUTION   *************/	
						
						}
					}
					
					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				/**
				 * Other cal end 
				 * */
				
				
				String strCurrencyId = hmEmpCurrency.get(strEmpId);
				Map<String,String> hmCurrency = hmCurrencyDetails.get(strCurrencyId);
				if(hmCurrency==null)hmCurrency = new HashMap<String,String>();
				
				
//				if(strEmpId.equals("230") || strEmpId.equals("239")) {  
//					System.out.println(strEmpId+"----aa hmTotal=====>"+hmTotal+"----dblDeduction=====>"+dblDeduction+"-----dblGross=====>"+dblGross);
//				}				
				
				hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction))); 
//				System.out.println("hmTotal 1: "+hmTotal);
				hmTotalSalary.put(strEmpId, hmTotal);
//				System.out.println("hmTotalSalary 1: "+hmTotalSalary);
				
				////=========================code for isdisplay false=======================

//				Map hmisDisplay=new HashMap();
				Map<String, String> hmTotalisDisplay=new HashMap<String, String>();
				Set<String> set2 = hmInnerisDisplay.keySet();
				Iterator<String> it2 = set2.iterator();
				dblGross = 0;
				dblGrossTDS = 0;
				dblDeduction = 0;
//				isDefinedEarningDeduction = false;
				while(it2.hasNext()) {
					String strSalaryId = it2.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
//					isDefinedEarningDeduction = false;
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, hmPaidSalaryInner.get(strSalaryId));
						} else {
							/*if(strEmpId.equals("459")) {
								System.out.println(nSalayHead+"  aa hmTotal=====>"+hmTotal);
							}*/
							
							switch(nSalayHead) {
							/**********  OVER TIME   *************/
								case OVER_TIME:  
								 
//									isDefinedEarningDeduction = true;
//									double dblOverTime = getOverTimeCalculationL(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC, hmOverTimeMap, hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime);
//									double dblOverTime = getOverTimeCalculationHours(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInnerisDisplay, hmInnerActualCTC,  hmEmpLevelMap, dblStandardHrs,  nTotalNumberOfDaysForCalc, hmIndividualOvertime,hmEmpOverTimeHours,hmEmpOverTimeLevelPolicy, hmEmpRosterHours, hmWlocationMap, nTotalNumberOfDays, nWeekEnds,nHolidays,dblPresent,dblTotalPresentDays,holidaysSet,weeklyOffSet,hmEmpWlocationMap);
									double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
//									dblOverTime = Math.round(dblOverTime);
									dblGross += dblOverTime;
									dblGrossTDS += dblOverTime;
									
								break;
								
								case BONUS:
									
									/*if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())) {
//										isDefinedEarningDeduction = true;
										double dblBonusAmount = getBonusCalculation(con, uF, strEmpId,strD2, hmEmpLevelMap, hmInnerActualCTC, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap, CF, hmIndividualBonus);
										hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblBonusAmount)));
//										dblBonusAmount = Math.round(dblBonusAmount);
										dblGross += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;
									}*/
									double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
//									dblBonusAmount = Math.round(dblBonusAmount);
									dblGross += dblBonusAmount;
									dblGrossTDS += dblBonusAmount;
									
								break;
								
								case EXGRATIA:
									
									double dblExGratiaAmount = getExGratiaAmount(con,uF,CF,strEmpId,strD1,strD2,strPC);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
									dblGross += dblExGratiaAmount;
									dblGrossTDS += dblExGratiaAmount;
									
								break;
								
								case AREARS:

//									isDefinedEarningDeduction = true;
									double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
//									dblArearAmount = Math.round(dblArearAmount);
									dblGross += dblArearAmount;
									dblGrossTDS += dblArearAmount;
									
								break;
								
								case INCENTIVES:
//									isDefinedEarningDeduction = true;
//									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
//									dblIncentiveAmount += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
//									hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIncentiveAmount)));
////									dblIncentiveAmount = Math.round(dblIncentiveAmount);
//									dblGross += dblIncentiveAmount;
//									dblGrossTDS += dblIncentiveAmount;
									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
									dblGross += dblIncentiveAmount;
									dblGrossTDS += dblIncentiveAmount;
									
								break;
								
								case REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
//									dblReimbursementAmount = Math.round(dblReimbursementAmount);
									dblGross += dblReimbursementAmount;
								break;
								
								case TRAVEL_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
//									dblTravelReimbursementAmount = Math.round(dblTravelReimbursementAmount);
									dblGross += dblTravelReimbursementAmount;
								break;
								
								case MOBILE_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
//									dblMobileReimbursementAmount = Math.round(dblMobileReimbursementAmount);
									dblGross += dblMobileReimbursementAmount;
								break;
								
								case OTHER_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
									double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
//									dblOtherReimbursementAmount = Math.round(dblOtherReimbursementAmount);
									dblGross += dblOtherReimbursementAmount;
								break;
								
								case OTHER_EARNING:
//									isDefinedEarningDeduction = true;
									double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
//									dblOtherEarningAmount = Math.round(dblOtherEarningAmount);
									dblGross += dblOtherEarningAmount;
								break;
								
								case SERVICE_TAX:
									
//									isDefinedEarningDeduction = true;
									double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
//									dblServiceTaxAmount = Math.round(dblServiceTaxAmount);

									/**
									 * @author Vipin
									 * 25-Mar-2014
									 * KP Condition
									 * @comment = service tax is not included while calculating TDS
									 * */
									
									dblGross += dblServiceTaxAmount;  
									dblGrossPT += dblServiceTaxAmount;
									dblGrossTDS += dblServiceTaxAmount;  
									  
									break;	
								
								case SWACHHA_BHARAT_CESS:
									double dblGrossAmt = dblGross;
									double dblServiceTaxAmt = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX+""));
									dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
									double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
									dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
									
									double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId), hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));

									dblGross += dblSwachhaBharatCess;  
									dblGrossPT += dblSwachhaBharatCess;
									dblGrossTDS += dblSwachhaBharatCess;  
									  
									break;	
								
								case KRISHI_KALYAN_CESS:
									double dblGrossAmt1 = dblGross;
									double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
									double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
									dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
									
									double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
									
									dblGross += dblKrishiKalyanCess;  
									dblGrossPT += dblKrishiKalyanCess;
									dblGrossTDS += dblKrishiKalyanCess;  
									  
									break;	
									
								case CGST :
									double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

									break;
								
								case SGST :
									double dblGrossAmt2 = dblGross;
									double dblCGSTAmt = uF.parseToDouble(hmTotal.get(CGST + ""));
									dblGrossAmt2 = dblGrossAmt2 - dblCGSTAmt;
									
									double dblSGSTAmount = calculateSGST(con, uF, strEmpId, dblGrossAmt2, hmEmpStateMap.get(strEmpId), hmTotal,
											strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
									dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

									break;	
								
								default:
									if(uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
										double dblPerkAlignAmount = 0.0d;
										if(hmPerkAlignAmount.containsKey(strSalaryId)) {
											dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
										}
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount)); 
										dblGross += dblPerkAlignAmount;
										dblGrossPT += dblPerkAlignAmount;
										dblGrossTDS += dblPerkAlignAmount;	
									} else if (!uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
										dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
									} else if (uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
										if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
									} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"))));
										dblGross += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
										dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
									} else if(uF.parseToInt(strSalaryId)!=GROSS) {
										boolean isMultipePerWithParticularHead = false;
										if(hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
											isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional);
										}
										if(!isMultipePerWithParticularHead) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT")))); 
											dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
										
									}
									/*if(strEmpId.equals("459")) {
										System.out.println("hmTotal=====>"+hmTotal);
									}  */ 
									
								break;
							}
							
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						
						/**********  EPF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblPt));
							} else {
								Map<String, String> hmVoluntaryPF = hmInnerisDisplay.get(VOLUNTARY_EPF+"");
								double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblEEPF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblEEPF));
							}
							
							break;
							
						/**********  EPF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_EPF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap);
								dblDeduction += dblERPF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
							}
							
							break;
							
						case LOAN:
							
//							System.out.println("Calculating Loan===>");
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {   
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
								CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2,strEmpId);
							} else {
								double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
								dblDeduction += dblLoanAmt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
								           
								if(true) {
//									dblGrossTDS = dblGross - dblLoanAmt;
									dblGrossTDS = dblGrossTDS - dblLoanAmt; 
								}
							}
							
							break;  
							
						case MOBILE_RECOVERY:
							
//							isDefinedEarningDeduction = true; 
							if(hmPaidSalaryInner!=null) {
								double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLoan;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
							} else {
								double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
								dblDeduction += dblIndividualMobileRecoveryAmt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
							}
							
							break;		
							
						default:
							if(hmPaidSalaryInner!=null) {
								dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								
								/*if(nSalayHead==VOLUNTARY_EPF) {
									continue;
								}*/
								
								if(hmAllowance.containsKey(strEmpId+"_"+strSalaryId)) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId))));
									dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId+"_"+strSalaryId));
								} else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_D")) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"))));
									dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
								} else if(uF.parseToInt(strSalaryId)!=PROFESSIONAL_TAX && uF.parseToInt(strSalaryId)!=TDS) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
									dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));	
								}	
							}
							break;
						}
					}
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				
				/**
				 * Multiple calfor isDisplay false start 
				 * */
//				hmContriSalHeadAmt = new HashMap<String, String>();
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
////					double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
//					double dblERPF = calculateERPF(con,CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap);
//					hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
//				}
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
////					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
//					double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
//					hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
//				}
//				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
//					double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth);
//					hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
//				}
				Iterator<String> itMultiIsDisplay = hmInnerisDisplay.keySet().iterator();
				while(itMultiIsDisplay.hasNext()) {
					String strSalaryId = itMultiIsDisplay.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E") && (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {

						if(hmPaidSalaryInner!=null) {
							dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblGross += dblMulCalAmt;
							dblGrossTDS += dblMulCalAmt;
						}
						
					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D") && (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if(hmPaidSalaryInner!=null) {
							dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
						} else {
							double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
//							hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
//							dblDeduction += dblMulCalAmt;
							//Started By Dattatray Date:14-12-21
							if(hmTotal.containsKey(strSalaryId) && setContriSalHead != null && (setContriSalHead.contains(EMPLOYEE_EPF+"") || setContriSalHead.contains(EMPLOYER_EPF+"") || setContriSalHead.contains(EMPLOYEE_ESI+"") || setContriSalHead.contains(EMPLOYER_EPF+"") || setContriSalHead.contains(EMPLOYEE_LWF+"") || setContriSalHead.contains(EMPLOYER_LWF+""))) {
								dblDeduction -= uF.parseToDouble(hmTotal.get(strSalaryId));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
								dblDeduction += dblMulCalAmt;
							}//Ended By Dattatray Date:14-12-21
							
						}		
					}
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross)));
				}
				
				/**
				 * Multiple cal isDisplay false start
				 * */
				
				/**
				 * Other cal isDisplay start
				 * */
				Iterator<String> itIsDisplayOther = hmInnerisDisplay.keySet().iterator();
				while(itIsDisplayOther.hasNext()) {
					String strSalaryId = itIsDisplayOther.next();
					int nSalayHead = uF.parseToInt(strSalaryId);
					System.out.println("AP/7575--nSalayHead="+nSalayHead);
					
					 Map<String,String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
					if(hmisDisplay==null) {  
						hmisDisplay = new HashMap<String,String>();
					}
					
					String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")&& (hmisDisplay.get("SALARY_AMOUNT_TYPE")!=null 
							&& !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						
						
						/**
						 * 			TAX CALCULATION STARTS HERE
						 * 
						 * */
						
						switch(nSalayHead) {
						/**********  	 TAX   *************/
						case PROFESSIONAL_TAX:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								
								
								/**
								 * KP Condition
								 * 
								 * */     
								if(strEmpId.equals("459")) {
//									System.out.println("inner dblGrossPT=====>"+dblGrossPT+"-----dblGross=====>"+dblGross);
								}
								     
//								double dblPt = calculateProfessionalTax(con, uF, strD2,dblGrossPT, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId));
							 	double dblPt = calculateProfessionalTax(con, uF, strD2,dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
								dblDeduction +=dblPt;  
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							}
							
							break;
							
							
						/**********  TDS   *************/
						case TDS:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblTDS;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							} else {

//								double dblBasic = uF.parseToDouble(hmTotalisDisplay.get(BASIC+""));
//								double dblDA = uF.parseToDouble(hmTotalisDisplay.get(DA+""));
//								double dblHRA = uF.parseToDouble(hmTotalisDisplay.get(HRA+""));
//								
//								String[] hraSalaryHeads = null;
//								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
//									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//								}
//								
//								double dblHraSalHeadsAmount = 0;
//								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
//									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotalisDisplay.get(hraSalaryHeads[i]));
//								}
//								
//								Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(strEmpId);
//								if(hmPaidSalaryDetails==null) {hmPaidSalaryDetails=new HashMap<String, String>();}
//								
//								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
								 
								if(hmEmpServiceTaxMap.containsKey(strEmpId)) {
									dblGrossTDS = dblGross;
									
//									double dblServiceTaxAmount = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX+""));
//									dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
//									
//									double dblSwachhaBharatCess = uF.parseToDouble(hmTotalisDisplay.get(SWACHHA_BHARAT_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
//									
//									double dblKrishiKalyanCess = uF.parseToDouble(hmTotalisDisplay.get(KRISHI_KALYAN_CESS+""));
//									dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
									
									double dblCGST = uF.parseToDouble(hmTotalisDisplay.get(CGST + ""));
									dblGrossTDS = dblGrossTDS - dblCGST;

									double dblSGST = uF.parseToDouble(hmTotalisDisplay.get(SGST + ""));
									dblGrossTDS = dblGrossTDS - dblSGST;
								}
								
								/**
								 * (dblBasic + dblDA) we use dblHraSalHeadsAmount
								 * */
//								double dblTDS = calculateTDS(con, uF, strD2,strD1,dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//										nPayMonth,
//										strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, hmEmpGenderMap.get(strEmpId),  hmEmpAgeMap.get(strEmpId), hmEmpStateMap.get(strEmpId),
//										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//										hmTotalisDisplay, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
								double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
										strFinancialYearEnd, strEmpId, hmEmpLevelMap);
								dblDeduction += dblTDS;
								
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
							}
							break;
							
							/**********  ESI EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYER CONTRIBUTION   *************/
							
							/**********  ESI EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_ESI:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction +=dblPt;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
							} else {
								double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId);
								dblESI = Math.ceil(dblESI);
								
								dblDeduction += dblESI;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
							}
							
							break;
							
							
							/**********  /ESI EMPLOYEE CONTRIBUTION   *************/	
							
							/**********  LWF EMPLOYER CONTRIBUTION   *************/
						case EMPLOYER_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							
							/**********  /LWF EMPLOYER CONTRIBUTION   *************/
							
							/**********  LWF EMPLOYEE CONTRIBUTION   *************/
						case EMPLOYEE_LWF:
//							isDefinedEarningDeduction = true;
							if(hmPaidSalaryInner!=null) {
								double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							} else {
								double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, strOrgId);
								dblDeduction += dblLWF;
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
							}
							
							break;
							
							/**********  /LWF EMPLOYEE CONTRIBUTION   *************/
						}
					}
					
					hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
					hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				/**
				 * Other cal isDisplay end 
				 * */
				
				hmTotalSalaryisDisplay.put(strEmpId, hmTotalisDisplay);
			////=========================end code for isdisplay false=======================
			}

			//			System.out.println("hmTotalSalary===>"+hmTotalSalary);
			
			List<String> alEmpIdPayrollG = new ArrayList<String>();
			pst = con.prepareStatement("select distinct(emp_id) from payroll_generation where paycycle=? and salary_head_id not in ("+BONUS+") ");
			pst.setInt(1, uF.parseToInt(strPC));			
			rs = pst.executeQuery();
			while(rs.next()) {
				String strEmpId = rs.getString("emp_id");
				alEmpIdPayrollG.add(strEmpId);
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("pst==>"+getPaycycle().split("-")[2]);
//			System.out.println("pst==>"+pst);
//			System.out.println("alEmpIdPayrollG==>"+alEmpIdPayrollG);
			
			
//			System.out.println("hmTotalSalary Approval="+hmTotalSalary);
			
			request.setAttribute("hmTotalSalary", hmTotalSalary);
			request.setAttribute("hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
			request.setAttribute("hmEmpNameMap", hmEmpNameMap);
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
			request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmEmpSalary", hmEmpSalary);
			request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			request.setAttribute("alEmpIdPayrollG", alEmpIdPayrollG);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("alProcessingEmployee", alProcessingEmployee);
			
			request.setAttribute("hmPaidDays", hmPaidDays);
			request.setAttribute("hmPresentDays", hmPresentDays1);
			request.setAttribute("hmLeaveDays", hmLeaveDays);
			request.setAttribute("hmLeaveTypeDays", hmLeaveTypeDays);
			request.setAttribute("hmMonthlyLeaves", hmMonthlyLeaves);
//			System.out.println("hmTotalDays=====>"+hmTotalDays);
			request.setAttribute("strTotalDays", strTotalDays);
			request.setAttribute("hmTotalDays", hmTotalDays);
			request.setAttribute("hmEmpPaymentMode", hmEmpPaymentMode);
			request.setAttribute("hmPaymentModeMap", hmPaymentModeMap);
			request.setAttribute("hmLoanAmt", hmLoanAmt);
			request.setAttribute("hmEmpStateMap", hmEmpStateMap);
			request.setAttribute("hmVariables", hmVariables);
			request.setAttribute("hmHolidayCount", hmHolidayCount);
			request.setAttribute("hmWoHLeaves", hmWoHLeaves);
			
			request.setAttribute("strD1", strD1);
			request.setAttribute("strD2", strD2);

			session.setAttribute("AP_hmTotalSalary", hmTotalSalary);
			session.setAttribute("AP_hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
			session.setAttribute("AP_hmEmpNameMap", hmEmpNameMap);
			session.setAttribute("AP_hmEmpCodeMap", hmEmpCodeMap);
			session.setAttribute("AP_hmLoanPoliciesMap", hmLoanPoliciesMap);
			session.setAttribute("AP_hmSalaryDetails", hmSalaryDetails);
			session.setAttribute("AP_hmEmpSalary", hmEmpSalary);
			session.setAttribute("AP_alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			session.setAttribute("AP_alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			session.setAttribute("AP_alEmpIdPayrollG", alEmpIdPayrollG);
			session.setAttribute("AP_hmServices", hmServices);
			session.setAttribute("AP_alEmp", alEmp);
			session.setAttribute("AP_alProcessingEmployee", alProcessingEmployee);
			
			session.setAttribute("AP_hmPaidDays", hmPaidDays);
			session.setAttribute("AP_hmPresentDays", hmPresentDays1);
			session.setAttribute("AP_hmLeaveDays", hmLeaveDays);
			session.setAttribute("AP_hmLeaveTypeDays", hmLeaveTypeDays);
			session.setAttribute("AP_hmMonthlyLeaves", hmMonthlyLeaves);
			
			session.setAttribute("AP_strTotalDays", strTotalDays);
			session.setAttribute("AP_hmTotalDays", hmTotalDays);
			session.setAttribute("AP_hmEmpPaymentMode", hmEmpPaymentMode);
			session.setAttribute("AP_hmPaymentModeMap", hmPaymentModeMap);
			session.setAttribute("AP_hmLoanAmt", hmLoanAmt);
			session.setAttribute("AP_hmEmpStateMap", hmEmpStateMap);
			session.setAttribute("AP_hmVariables", hmVariables);
			session.setAttribute("AP_hmOtherTaxDetails", hmOtherTaxDetails);			
			session.setAttribute("AP_hmEmpLevelMap", hmEmpLevelMap);
			session.setAttribute("AP_hmHolidayCount", hmHolidayCount);
			
			session.setAttribute("AP_strD1", strD1);
			session.setAttribute("AP_strD2", strD2);
			session.setAttribute("AP_strPC", strPC);			
			session.setAttribute("AP_f_org", getF_org());
			session.setAttribute("AP_strPaycycleDuration", getStrPaycycleDuration());
			session.setAttribute("AP_hmArearAmountMap", hmArearAmountMap);
			session.setAttribute("AP_hmWoHLeaves", hmWoHLeaves);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
//===end parvez date: 14-04-2022===	
	
	
	private double calculateSGST(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId, Map<String, String> hmTotal,
			String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherTaxDetails,
			Map<String, String> hmEmpServiceTaxMap, CommonFunctions CF) {
		double dblSGSTAmount = 0;
		try {

			if (!hmEmpServiceTaxMap.containsKey(strEmpId)) {
				return 0;
			}	
			double dblSGST = uF.parseToDouble(hmOtherTaxDetails.get(strStateId + "_SGST"));
//			System.out.println("dblSGST==>"+dblSGST+"--dblGross==>"+dblGross);
			dblSGSTAmount = (dblGross * dblSGST) / 100;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblSGSTAmount;
	}

	private double calculateCGST(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId, Map<String, String> hmTotal,
			String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherTaxDetails,
			Map<String, String> hmEmpServiceTaxMap, CommonFunctions CF) {
		double dblCGSTAmount = 0;
		try {

			if (!hmEmpServiceTaxMap.containsKey(strEmpId)) {
				return 0;
			}
			
			double dblCGST = uF.parseToDouble(hmOtherTaxDetails.get(strStateId + "_CGST"));
//			System.out.println("dblCGST==>"+dblCGST+"--dblGross==>"+dblGross);
			dblCGSTAmount = (dblGross * dblCGST) / 100;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblCGSTAmount;
	}
	
	public void getAnnualVariableAmount(Connection con, UtilityFunctions uF, Map<String, String> hmAnnualVariables, String strD1, String strD2, String strPC) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from annual_variable_individual_details where paid_from=? and paid_to=? and pay_paycycle=? and is_approved = 1");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPC));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAnnualVariables.put(rs.getString("emp_id") + "_" + rs.getString("salary_head_id"), rs.getString("pay_amount"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean checkMultipleCalPerWithParticularHead(Connection con, UtilityFunctions uF, CommonFunctions CF, int nSalayHead, String strEmpId, double dblPresent,
			int nTotalNumberOfDays, String strD1, String strD2, String strPC, String strLevel, Map<String, String> hm, Map<String, String> hmTotal,
			Map<String, String> hmAnnualVarPolicyAmount, double dblReimbursementCTC,Map<String, String> hmAllowance, Map<String, String> hmVariables, double dblReimbursementCTCOptional) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
			
			if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();
			if(hmAllowance == null) hmAllowance = new HashMap<String, String>();
			if(hmVariables== null) hmVariables = new HashMap<String, String>();
			
			String strMulCal = hm.get("MULTIPLE_CALCULATION");
			if(strMulCal != null && !strMulCal.trim().equals("") && !strMulCal.trim().equalsIgnoreCase("NULL")) {
				List<String> al = Arrays.asList(strMulCal.trim().split(","));
				if(al == null) al = new ArrayList<String>();
				int nAl = al.size();
				
				for(int i = 0; i < nAl; i++) {
					int nHeadId = uF.parseToInt(al.get(i));
					if(nHeadId > 0 && hmAnnualVarPolicyAmount!=null && hmAnnualVarPolicyAmount.containsKey(strEmpId+"_"+nHeadId)) {
						flag = true;
					} else if(nHeadId > 0 && hmAllowance!=null && hmAllowance.containsKey(strEmpId+"_"+nHeadId)) {
						flag = true;
					} else if(nHeadId > 0 && hmVariables!=null && hmVariables.containsKey(strEmpId + "_" + nHeadId + "_E")) {
						flag = true;
					} else if(nHeadId > 0 && hmVariables!=null && hmVariables.containsKey(strEmpId + "_" + nHeadId + "_D")) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == REIMBURSEMENT_CTC) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == EMPLOYEE_EPF) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == INCENTIVES) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == AREARS) {
						flag = true;
					}
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;

	}

	public double getMultipleCalAmtDetails(Connection con, UtilityFunctions uF, CommonFunctions CF, int nSalayHead, String strEmpId, double dblPresent,
			int nTotalNumberOfDays, String strD1, String strD2, String strPC, String strLevel, Map<String, String> hm, Map<String, String> hmTotal,
			Map<String, String> hmAnnualVarPolicyAmount, double dblReimbursementCTC,Map<String, String> hmAllowance, Map<String, String> hmVariables, 
			double dblReimbursementCTCOptional, Map<String, String> hmContriSalHeadAmt) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblMulCalAmt = 0.0d;
		try {
			
			if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();
			if(hmAllowance == null) hmAllowance = new HashMap<String, String>();
			if(hmVariables== null) hmVariables = new HashMap<String, String>();
			
			String strMulCal = hm.get("MULTIPLE_CALCULATION");
			if(strMulCal != null && !strMulCal.trim().equals("") && !strMulCal.trim().equalsIgnoreCase("NULL")) {
				List<String> al = Arrays.asList(strMulCal.trim().split(","));
				if(al == null) al = new ArrayList<String>();
				int nAl = al.size();
				boolean flag = false;
				for(int i = 0; i < nAl; i++) {
					int nHeadId = uF.parseToInt(al.get(i));
					if(nHeadId > 0 && hmAnnualVarPolicyAmount!=null && hmAnnualVarPolicyAmount.containsKey(strEmpId+"_"+nHeadId)) {
						flag = true;
					} else if(nHeadId > 0 && hmAllowance!=null && hmAllowance.containsKey(strEmpId+"_"+nHeadId)) {
						flag = true;
					} else if(nHeadId > 0 && hmVariables!=null && hmVariables.containsKey(strEmpId + "_" + nHeadId + "_E")) {
						flag = true;
					} else if(nHeadId > 0 && hmVariables!=null && hmVariables.containsKey(strEmpId + "_" + nHeadId + "_D")) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == REIMBURSEMENT_CTC) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == EMPLOYEE_EPF) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == INCENTIVES) {
						flag = true;
					} else if(nHeadId > 0 && nHeadId == AREARS) {
						flag = true;
					}
				}
				
				if(flag) {
					Map<String, String> hmSalaryType = new HashMap<String, String>();
					pst = con.prepareStatement("select * from salary_details where level_id = ? and (is_delete is null or is_delete=false) order by salary_head_id, salary_id");
					pst.setInt(1, uF.parseToInt(strLevel));
					rs = pst.executeQuery();
					while (rs.next()) {
						hmSalaryType.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
					}
					rs.close();
					pst.close();
		
					pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? " +
							"and effective_date = (select max(effective_date) from emp_salary_details " +
							"where emp_id =? and effective_date <= ? and is_approved=true and level_id = ?) " +
							"and salary_head_id in ("+CTC+") and salary_head_id in (select salary_head_id " +
							"from salary_details where (is_delete is null or is_delete=false) " +
							"and org_id in (select org_id from employee_personal_details epd, " +
							"employee_official_details eod where epd.emp_per_id=eod.emp_id " +
							"and eod.emp_id=?) and level_id = ?) and level_id = ?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strEmpId));
					pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strLevel));
					pst.setInt(5, uF.parseToInt(strEmpId));
					pst.setInt(6, uF.parseToInt(strLevel));
					pst.setInt(7, uF.parseToInt(strLevel));
					rs = pst.executeQuery();
					boolean isCtc = false;
					double dblCTC = 0.0d;
					while (rs.next()) {
						isCtc = true;
						dblCTC = uF.parseToDouble(rs.getString("amount"));
					}
					rs.close();
					pst.close();
		
					String strSalaryType = hmSalaryType.get("" + nSalayHead);
		
					if (isCtc) {
						if (strSalaryType != null && strSalaryType.equalsIgnoreCase("F")) {
							// dblCTC = dblCTC;
						} else if (strSalaryType != null && strSalaryType.equalsIgnoreCase("D")) {
							dblCTC = dblCTC * dblPresent;
						} else {
							dblCTC = dblCTC * (dblPresent / nTotalNumberOfDays);
						}
					}
					
					StringBuilder sbFormula = new StringBuilder();
					for(int i = 0; i < nAl; i++) {
						String str = al.get(i);
						if(str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")) {
							boolean isInteger = uF.isInteger(str.trim());
							if(isInteger) {
								double dblAmt = uF.parseToDouble(hmTotal.get(str.trim()));
								
								if (uF.parseToInt(str.trim()) == EMPLOYER_EPF && !hmTotal.containsKey(""+EMPLOYER_EPF)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_EPF));
								}
								if (uF.parseToInt(str.trim()) == EMPLOYER_ESI && !hmTotal.containsKey(""+EMPLOYER_ESI)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_ESI));
								}
								if (uF.parseToInt(str.trim()) == EMPLOYER_LWF && !hmTotal.containsKey(""+EMPLOYER_LWF)) {
									dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_LWF));
								}
								if (uF.parseToInt(str.trim()) == CTC) {
									dblAmt = dblCTC;
								} else if (uF.parseToInt(str.trim()) == REIMBURSEMENT_CTC) {
									dblAmt = dblReimbursementCTC + dblReimbursementCTCOptional;
								} else if(hmAnnualVarPolicyAmount!=null && hmAnnualVarPolicyAmount.containsKey(strEmpId+"_"+str.trim())) {
									double amtAnnual = uF.parseToDouble(hmAnnualVarPolicyAmount.get(strEmpId+"_"+str.trim())); 
									dblAmt = amtAnnual > 0.0d ? (amtAnnual / 12.0d) : 0.0d;
								}
								sbFormula.append(""+dblAmt);
							} else {
								sbFormula.append(str.trim());
							}
						}
					}
					String strPercentage = hm.get("SALARY_PERCENTAGE");
					if(uF.parseToDouble(strPercentage) > 0.0d && sbFormula != null && sbFormula.length() > 0) {
						double dblPerAmount = uF.eval(sbFormula.toString());	
						dblMulCalAmt = (uF.parseToDouble(strPercentage) * dblPerAmount)/100;
					} else {
						dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
					}
					
				} else {
					dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
				}
			} else {
				dblMulCalAmt = uF.parseToDouble(hm.get("AMOUNT"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblMulCalAmt;

	}

	public void getAllowanceAmount(Connection con, UtilityFunctions uF, Map<String, String> hmAllowance, String strD1, String strD2, String strPC) {
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
//			pst = con.prepareStatement("select * from allowance_individual_details where is_approved=1 and pay_paycycle=? and paid_from=? and paid_to=? ");
			pst = con.prepareStatement("select sum(pay_amount) as pay_amount,emp_id,salary_head_id from allowance_individual_details " +
				"where is_approved=1 and pay_paycycle=? and paid_from=? and paid_to=? group by emp_id,salary_head_id");
			pst.setInt(1, uF.parseToInt(strPC)); 
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmAllowance.put(rs.getString("emp_id")+"_"+rs.getString("salary_head_id"), rs.getString("pay_amount"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
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
	}

	private double calculateSwachhaBharatCess(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherTaxDetails, Map<String, String> hmEmpServiceTaxMap, CommonFunctions CF) {
		
		double dblSwachhaBharatCessAmount = 0;
		
		try {
			
			if(!hmEmpServiceTaxMap.containsKey(strEmpId))return 0;
			
			double dblSwachhaBharatCess = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_SWACHHA_BHARAT_CESS"));
			dblSwachhaBharatCessAmount = (dblGross * dblSwachhaBharatCess)/100;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dblSwachhaBharatCessAmount;
	}
	

	private double calculateKrishiKalyanCess(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherTaxDetails,
			Map<String, String> hmEmpServiceTaxMap, CommonFunctions CF) {

		double dblKrishiKalyanCessAmount = 0;

		try {

			if (!hmEmpServiceTaxMap.containsKey(strEmpId))
				return 0;

			double dblKrishiKalyanCess = uF.parseToDouble(hmOtherTaxDetails.get(strStateId + "_KRISHI_KALYAN_CESS"));
			dblKrishiKalyanCessAmount = (dblGross * dblKrishiKalyanCess) / 100;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblKrishiKalyanCessAmount;
	}

	private Map<String, Map<String, String>> getActualLeaveDates(Connection con, com.konnect.jpms.util.CommonFunctions cF2, UtilityFunctions uF, String strD1,
			String strD2, Map<String, Map<String, String>> hmLeaveTypeDays, boolean b, Map<String, String> hmMonthlyLeaves) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmLeaveDates = new HashMap<String, Map<String, String>>();		
		try {
			pst = con.prepareStatement("select emp_id,_date,leave_no,lt.leave_type_id,lt.leave_type_code from leave_application_register lar, leave_type lt " +
					"where lar.leave_type_id = lt.leave_type_id and _date between ? and ? and _type = ? and is_paid = ? " +
					"and is_modify= false and lar.leave_type_id not in (select leave_type_id from leave_type where is_compensatory = true) " +
					"and lar.leave_id in (select leave_id from emp_leave_entry) order by emp_id");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setBoolean(3, true);
			pst.setBoolean(4, true);
			rs = pst.executeQuery();
			String strEmpId = null;
			String strLeaveDate = null;
			String strLeaveNo = null;
			String strLeaveTypeId = null;
			String strLeaveTypeCode = null;
			double dblLeaveCount = 0;
			while (rs.next()) {
				strEmpId = rs.getString("emp_id");
				strLeaveDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
				strLeaveNo = rs.getString("leave_no");
				strLeaveTypeId = rs.getString("leave_type_id");
				strLeaveTypeCode = rs.getString("leave_type_code");
				
				/**
				 * Leave Dates
				 * */
				Map<String, String> hmLeaveDateTemp = (Map<String, String>) hmLeaveDates.get(strEmpId);
				if (hmLeaveDateTemp == null) hmLeaveDateTemp = new HashMap<String, String>();
				hmLeaveDateTemp.put(strLeaveDate, strLeaveTypeCode);
				hmLeaveDates.put(strEmpId, hmLeaveDateTemp);
				
				/**
				 * Leave Type (Half day or Full day)
				 * */
				Map<String, String> hmTempType = (Map<String, String>) hmLeaveTypeDays.get(strEmpId);
				if (hmTempType == null) { 
					hmTempType = new HashMap<String, String>();
					dblLeaveCount = 0;
				}
				if(uF.parseToDouble(strLeaveNo) == 0.5) {
					hmTempType.put(strLeaveDate, "H");
					dblLeaveCount += 0.5;
					hmTempType.put("COUNT", dblLeaveCount+ "");
				} else if(uF.parseToDouble(strLeaveNo) == 1) {
					hmTempType.put(strLeaveDate, "F");
					dblLeaveCount += 1;
					hmTempType.put("COUNT", dblLeaveCount+ "");
				}
				hmLeaveTypeDays.put(strEmpId, hmTempType);
				
				/**
				 * Monthly leave type count 
				 * */				
				double dblMontlycount = uF.parseToDouble(hmMonthlyLeaves.get(strEmpId + "_" + strLeaveTypeId));
				if(uF.parseToDouble(strLeaveNo) == 0.5) {
					dblMontlycount += 0.5;
				} else if(uF.parseToDouble(strLeaveNo) == 1) {
					dblMontlycount += 1;
				}
				hmMonthlyLeaves.put(strEmpId + "_" + strLeaveTypeId, dblMontlycount + "");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return hmLeaveDates;
	}

	private Map<String, String> getLongLeavesCount(Connection con, UtilityFunctions uF, CommonFunctions CF, String strD1, String strD2, String strPC) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmLongLeaves = new HashMap<String, String>();
		try {
			
			pst = con.prepareStatement("select sum(leave_no) as paid_leaves, emp_id from leave_application_register where _date between ? and ? and _type = ? and is_paid = ?  and is_long_leave=true and is_modify= false and leave_type_id not in (select leave_type_id from leave_type where is_compensatory = true) group by emp_id");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setBoolean(3, true);
			pst.setBoolean(4, true);
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLongLeaves.put(rs.getString("emp_id") , rs.getString("paid_leaves"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmLongLeaves=====>"+hmLongLeaves);
			
			
		} catch (Exception e) {
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
		return hmLongLeaves;
	}

	private double getLeaveEncashmentAmtDetails(Connection con, UtilityFunctions uF, CommonFunctions CF, String strEmpId, double dblEnashDays, int nTotalNumberOfDaysForCalc, String strD1, String strD2, String strPC, String strLevel, double dblIncrementBasic, double dblIncrementDA) {
		double dblEncashAmount = 0.0d;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {	
			pst = con.prepareStatement("select sum(no_days) as no_days,leave_type_id from emp_leave_encashment where emp_id=? " +
					"and paid_from= ? and paid_to=? and paycycle=? and is_approved=1 and is_paid=false group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strPC));
			System.out.println("AP/8389--pst===>>>>>>"+pst); 
			rs = pst.executeQuery();
			Map<String, String> hmLeaveEncashDays = new HashMap<String, String>();
			StringBuilder sbLeaveTypeId = null;
			while (rs.next()) {
				hmLeaveEncashDays.put(rs.getString("leave_type_id"), rs.getString("no_days"));
				
				if(sbLeaveTypeId == null) {
					sbLeaveTypeId = new StringBuilder();
					sbLeaveTypeId.append(rs.getString("leave_type_id"));
				} else {
					sbLeaveTypeId.append(","+rs.getString("leave_type_id"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmLeaveSalaryHeads = new HashMap<String, String>();
			Map<String, String> hmLeaveSHeadsPercetage = new HashMap<String, String>();
			if(sbLeaveTypeId !=null) {
				pst = con.prepareStatement("select leave_type_id,salary_head_id,percentage from emp_leave_type where leave_type_id in ("+sbLeaveTypeId.toString()+") and level_id=?");
				pst.setInt(1, uF.parseToInt(strLevel));
				System.out.println("AP/8417---pst="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					hmLeaveSalaryHeads.put(rs.getString("leave_type_id"), rs.getString("salary_head_id"));
					hmLeaveSHeadsPercetage.put(rs.getString("leave_type_id"), rs.getString("percentage"));
				}
				rs.close();
				pst.close();
			}
			
			Iterator<String> it1 = hmLeaveSalaryHeads.keySet().iterator();
			while(it1.hasNext()) {
				String strLeaveTypeId = it1.next();
				String strSalaryHeads = hmLeaveSalaryHeads.get(strLeaveTypeId);
				List<String> alsalaryHeads = Arrays.asList(strSalaryHeads.split(","));
				if(alsalaryHeads == null) alsalaryHeads = new ArrayList<String>();
				
				double dblPercentage = uF.parseToDouble(hmLeaveSHeadsPercetage.get(strLeaveTypeId));
				if(dblPercentage == 0.0d) {
					continue;
				}
				
				dblEnashDays = uF.parseToDouble(hmLeaveEncashDays.get(strLeaveTypeId));
				
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				Map<String, Map<String,String>> hmInnerActualCTC = new HashMap<String, Map<String, String>>(); // CF.getSalaryCalculation(con,hmInnerisDisplay, uF.parseToInt(strEmpId), dblEnashDays, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2);
				
				double salaryGross=0;
				double salaryDeduction=0;
				Iterator<String> it = hmInnerActualCTC.keySet().iterator();
				while(it.hasNext()) {
					String strSalaryId = it.next();
					
					Map<String,String> hm = hmInnerActualCTC.get(strSalaryId);
					if(hm.get("EARNING_DEDUCTION").equals("E") && alsalaryHeads.contains(strSalaryId)) {
						salaryGross +=uF.parseToDouble(hm.get("AMOUNT"));
					}else if(hm.get("EARNING_DEDUCTION").equals("D") && alsalaryHeads.contains(strSalaryId)) {
						salaryDeduction +=uF.parseToDouble(hm.get("AMOUNT"));
					}
				}
				
				dblEncashAmount += ((salaryGross - salaryDeduction) * dblPercentage)/100;
			}
			
		} catch (Exception e) {
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
		return (dblEncashAmount > 0.0d ? dblEncashAmount : 0.0d);
	}
	
	
	private double getLeaveEncashmentAmtDetailsNew(Connection con, UtilityFunctions uF, CommonFunctions CF, String strEmpId, Map<String, String> hmLeaveEncashment, int nTotalNumberOfDaysForCalc, String strD1, String strD2, String strPC, String strLevel, double dblIncrementBasic, double dblIncrementDA) {
		double dblEncashAmount = 0.0d;
		double dblEnashDays = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {	
			
			System.out.println("AP/8495--nTotalNumberOfDaysForCalc="+nTotalNumberOfDaysForCalc);
			System.out.println("AP/8495--hmLeaveEncashment="+hmLeaveEncashment);
			if(hmLeaveEncashment == null){
				hmLeaveEncashment = new HashMap<String, String>();
			}
				
			StringBuilder sbLeaveTypeId = null;
			Iterator<String> itr = hmLeaveEncashment.keySet().iterator();
			while(itr.hasNext()){
				String key = itr.next();
				
				if(sbLeaveTypeId == null) {
					sbLeaveTypeId = new StringBuilder();
					sbLeaveTypeId.append(key);
				} else {
					sbLeaveTypeId.append(","+key);
				}
			}
			
			Map<String, String> hmLeaveSalaryHeads = new HashMap<String, String>();
			Map<String, String> hmLeaveSHeadsPercetage = new HashMap<String, String>();
			if(sbLeaveTypeId !=null) {
				pst = con.prepareStatement("select leave_type_id,salary_head_id,percentage from emp_leave_type where leave_type_id in ("+sbLeaveTypeId.toString()+") and level_id=?");
				pst.setInt(1, uF.parseToInt(strLevel));
//				System.out.println("AP/8417---pst="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					hmLeaveSalaryHeads.put(rs.getString("leave_type_id"), rs.getString("salary_head_id"));
					hmLeaveSHeadsPercetage.put(rs.getString("leave_type_id"), rs.getString("percentage"));
				}
				rs.close();
				pst.close();
			}
			
			Iterator<String> it1 = hmLeaveSalaryHeads.keySet().iterator();
			while(it1.hasNext()) {
				String strLeaveTypeId = it1.next();
				String strSalaryHeads = hmLeaveSalaryHeads.get(strLeaveTypeId);
				List<String> alsalaryHeads = Arrays.asList(strSalaryHeads.split(","));
				if(alsalaryHeads == null) alsalaryHeads = new ArrayList<String>();
				
				double dblPercentage = uF.parseToDouble(hmLeaveSHeadsPercetage.get(strLeaveTypeId));
				if(dblPercentage == 0.0d) {
					continue;
				}
				
				dblEnashDays = uF.parseToDouble(hmLeaveEncashment.get(strLeaveTypeId));
				
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
//				Map<String, Map<String,String>> hmInnerActualCTC = new HashMap<String, Map<String, String>>(); // CF.getSalaryCalculation(con,hmInnerisDisplay, uF.parseToInt(strEmpId), dblEnashDays, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2);
				Map<String, Map<String,String>> hmInnerActualCTC = CF.getSalaryCalculation(con, uF.parseToInt(strEmpId), dblEnashDays, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, uF.getDateFormat(strD2, DATE_FORMAT, DBDATE));
//				System.out.println("AP/8546--hmInnerActualCTC="+hmInnerActualCTC);
				
				double salaryGross=0;
				double salaryDeduction=0;
				Iterator<String> it = hmInnerActualCTC.keySet().iterator();
				while(it.hasNext()) {
					String strSalaryId = it.next();
					
					Map<String,String> hm = hmInnerActualCTC.get(strSalaryId);
					if(hm.get("EARNING_DEDUCTION").equals("E") && alsalaryHeads.contains(strSalaryId)) {
						salaryGross +=uF.parseToDouble(hm.get("AMOUNT"));
					}else if(hm.get("EARNING_DEDUCTION").equals("D") && alsalaryHeads.contains(strSalaryId)) {
						salaryDeduction +=uF.parseToDouble(hm.get("AMOUNT"));
					}
				}
				
//				System.out.println("AP/8562---strSalaryHeads="+strSalaryHeads+"--salaryGross="+salaryGross+"---dblPercentage="+dblPercentage);
				
				/*if(salaryGross > 0 || salaryDeduction > 0){
					salaryGross = salaryGross/30;
					salaryDeduction = salaryDeduction/30;
				}*/
				
				dblEncashAmount += ((salaryGross - salaryDeduction) * dblPercentage)/100;
			}
			
		} catch (Exception e) {
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
		return (dblEncashAmount > 0.0d ? dblEncashAmount : 0.0d);
	}

	private double getExGratiaAmount(Connection con, UtilityFunctions uF, CommonFunctions CF, String strEmpId, String strD1, String strD2, String strPC) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblExGratiaAmount = 0.0d;
		
		try {
			
			pst = con.prepareStatement("select * from emp_exgratia_details where paid_from = ? and paid_to=? and pay_paycycle=? and emp_id=?");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPC));
			pst.setInt(4, uF.parseToInt(strEmpId));
//			System.out.println("pst===>" + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				dblExGratiaAmount = uF.parseToDouble(rs.getString("pay_amount"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
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
		return dblExGratiaAmount;
	}

	public Map<String, String> getEmpIncomeOtherSources(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmEmpExemptionsMap = new HashMap<String,String>();
		
		try {
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  " +
//					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true " +
//					"and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) " +
//					"like '%HOME% %INTEREST%') and isdisplay=false group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id =13 " +
					"and isdisplay=false and financial_year_start=? and financial_year_end=? group by emp_id, sd.section_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			double dblInvestmentLimit = 0;
			double dblInvestmentEmp = 0;
			while (rs.next()) {
//				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
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
		return hmEmpExemptionsMap;
	
	}


	public void getPrevEmpTdsAmount(Connection con, UtilityFunctions uF,String strFinancialYearStart, String strFinancialYearEnd,Map<String, String> hmPrevEmpTdsAmount,Map<String, String> hmPrevEmpGrossAmount) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			 
			pst = con.prepareStatement("select * from prev_earn_deduct_details where financial_start=? and financial_end=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			if(rs.next()) {
				double dblTDSAmt = rs.getDouble("tds_amount");
				double dblGrossAmt = rs.getDouble("gross_amount");
				
				hmPrevEmpGrossAmount.put(rs.getString("emp_id"), ""+dblGrossAmt);
				hmPrevEmpTdsAmount.put(rs.getString("emp_id"), ""+dblTDSAmt);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
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
	}
	private double getOverTimeCalculationHours(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List<String> alServices, 
			List<String> alPresentDays, List<String> alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, 
			Map<String,String> hmEmpLevelMap, double dblStandardHours, 
			int nTotalNumberOfDaysForCalc, Map<String, String> hmIndividualOvertime,Map<String, Map<String, String>> hmEmpOverTimeHours,Map<String, Map<String, String>> hmEmpOverTimeLevelPolicy, Map<String, String> hmEmpRosterHours, Map<String, Map<String, String>> hmWlocationMap, int nTotalNumberOfDays, int nWeekEnds,int nHolidays,double dblPresent,double dblTotalPresentDays,Set<String> hmHolidayDates, Set<String> hmWeekEnds,Map<String, String> hmEmpWlocationMap) {
		
		double dblTotalOverTimeAmount = 0.0d;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map hmFixedExemptions = new HashMap();
		
		try {
			
//			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
			String strLevelId = hmEmpLevelMap.get(strEmpId);
			String strOverTimeType = null;
//			double dblTotalOverTime = 0;
//			double dblTotalHoursWorked = 0;
			double dblOverTimeCalcHours = 0;
			double dblOverTimeCalcDays = 0;
			
//			double dblOvertimeFixedAmount = 0;
			
			
			Map<String,String> hmOvertimePolicy=new HashMap<String, String>();
			
			Map<String,String> hmEmpOvertime=hmEmpOverTimeHours.get(strEmpId);
			if(hmEmpOvertime==null) hmEmpOvertime=new HashMap<String, String>();
			Iterator<String> it=hmEmpOvertime.keySet().iterator();
			
			while(it.hasNext()) {
				String strDate = it.next();
				double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
				
				
				if(hmHolidayDates!=null && hmHolidayDates.contains(uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDateFormat()) )) {
					strOverTimeType = "PH";
				}else if(hmWeekEnds!=null && hmWeekEnds.contains(strDate)) {
					strOverTimeType = "BH";
				} else {
					strOverTimeType = "EH";
				}
						
				hmOvertimePolicy=hmEmpOverTimeLevelPolicy.get(strLevelId+"_"+strOverTimeType);
				if(hmOvertimePolicy==null) hmOvertimePolicy=new HashMap<String, String>();
				
				String salaryHeadId=hmOvertimePolicy.get("SALARY_HEAD_ID");
				List<String> salaryHeadList=null;
				if(salaryHeadId!=null) {
					salaryHeadList=Arrays.asList(salaryHeadId.split(","));
				}
				
				if("RH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))) {
					dblOverTimeCalcHours = uF.parseToDouble(hmEmpRosterHours.get(strDate+"_"+strEmpId));
				}else if("SWH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))) {
					dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
				} else {
					dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
				}
				
				if("MD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))) {
					dblOverTimeCalcDays = nTotalNumberOfDays; 
				}else if("AWD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))) {
					//dblOverTimeCalcDays = nTotalNumberOfDays - nWeekEnds;
					dblOverTimeCalcDays = dblTotalPresentDays;
				} else {
					dblOverTimeCalcDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));
				}
										
				double dblSubSalaryAmount = 0;
				double dblSubSalaryAmountActualCTC = 0;
				for(int i=0;salaryHeadList!=null && !salaryHeadList.isEmpty() && i<salaryHeadList.size();i++) {
												
					Map<String,String> hmSubSalaryDetails = hmCalculatedSalary.get(salaryHeadList.get(i).trim());
					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
					dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));					
												
					Map<String,String> hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(salaryHeadList.get(i).trim());
					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap<String,String>();
					dblSubSalaryAmountActualCTC  += uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));
					
				}
				
				
				String overtimePaymentType=hmOvertimePolicy.get("OVERTIME_PAYMENT_TYPE");
				String strCalcBasic =hmOvertimePolicy.get("CAL_BASIS");
				double dblAmount = uF.parseToDouble(hmOvertimePolicy.get("OVERTIME_PAYMENT_AMOUNT"));
				
				
				if(strCalcBasic!=null && strCalcBasic.equals("FD")) {
					if(overtimePaymentType!=null && overtimePaymentType.equals("A")) {
						dblTotalOverTimeAmount += dblAmount;
					} else {
						dblTotalOverTimeAmount += dblAmount * dblSubSalaryAmount/ 100;
					}
					
				}else if(dblOverTimeCalcHours>0) {
					if(overtimePaymentType!=null && overtimePaymentType.equals("A")) {
						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount / ( dblOverTimeCalcDays * dblOverTimeCalcHours);
					} else {
						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount * dblSubSalaryAmount/ (100 * dblOverTimeCalcDays * dblOverTimeCalcHours);								 
					}
				}
				
			}

			
			
			
			dblTotalOverTimeAmount += uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return uF.parseToDouble(uF.formatIntoComma(dblTotalOverTimeAmount));
		return dblTotalOverTimeAmount;
	}
	
	private double getLeaveEncashmentAmount(UtilityFunctions uF,String leavecount,double dblTotalPresentDays,String basicAmt) {
		double leaveEncashmentAmt=uF.parseToDouble(basicAmt)*uF.parseToDouble(leavecount)/dblTotalPresentDays;
		
		return leaveEncashmentAmt;
	}

	

	public String[] getwLocation() {
        return wLocation;
    }


    public void setwLocation(String[] wLocation) {
        this.wLocation = wLocation;
    }


    public String[] getLevel() {
        return level;
    }


    public void setLevel(String[] level) {
        this.level = level;
    }


    public String[] getF_department() {
        return f_department;
    }


    public void setF_department(String[] f_department) {
        this.f_department = f_department;
    }


    public String[] getF_service() {
        return f_service;
    }


    public void setF_service(String[] f_service) {
        this.f_service = f_service;
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

	public String[] getChbox() {
		return chbox;
	}

	public void setChbox(String[] chbox) {
		this.chbox = chbox;
	}

	public String[] getEmpID() {
		return empID;
	}

	public void setEmpID(String[] empID) {
		this.empID = empID;
	}

	public HttpServletRequest request;
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

	public String getStrFrmD1() {
		return strFrmD1;
	}

	public void setStrFrmD1(String strFrmD1) {
		this.strFrmD1 = strFrmD1;
	}

	public String getStrFrmD2() {
		return strFrmD2;
	}

	public void setStrFrmD2(String strFrmD2) {
		this.strFrmD2 = strFrmD2;
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

	public void setStrAlpha(String strAlpha) {
		this.strAlpha = strAlpha;
	}


	public String getApprovePC() {
		return approvePC;
	}


	public void setApprovePC(String approvePC) {
		this.approvePC = approvePC;
	}
	
	
	
	public double calculateProfessionalTax(Connection con, UtilityFunctions uF,String strD2, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, String strWLocationStateId, String strEmpGender) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblDeductionPayMonth = 0;
		
		
		try {
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strEmpGender);			
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			double dblDeductionAmount = 0;
			double dblDeductionPaycycleAmount = 0;
			while(rs.next()) {
				dblDeductionAmount = rs.getDouble("deduction_amount");
				dblDeductionPaycycleAmount = rs.getDouble("deduction_paycycle");
			}
			rs.close();
			pst.close();
			
			nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
			int nFinancialYearEndMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
			nFinancialYearEndMonth = nFinancialYearEndMonth - 1;

			if(nFinancialYearEndMonth==nPayMonth) {
				dblDeductionPayMonth = dblDeductionAmount - (11*dblDeductionPaycycleAmount);
			} else {
				dblDeductionPayMonth = dblDeductionPaycycleAmount;
			}
			
			
//			System.out.println("this.strD2="+strD2);
//			System.out.println("nPayMonth1="+uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
//			System.out.println("dblDeductionAmount="+dblDeductionAmount);
//			System.out.println("dblDeductionPaycycleAmount="+dblDeductionPaycycleAmount);
//			
//			System.out.println("nFinancialYearEndMonth="+nFinancialYearEndMonth);
//			System.out.println("nPayMonth="+nPayMonth);
////			System.out.println("dblDeductionPayMonth="+dblDeductionPayMonth);
//			System.out.println("strFinancialYearEnd="+strFinancialYearEnd);
			
			
			
				/*log.debug("strFinancialYearEnd="+strFinancialYearEnd);
			log.debug("nFinancialYearEndMonth="+nFinancialYearEndMonth);
			log.debug("nPayMonth="+nPayMonth);
			log.debug("dblDeductionPayMonth="+dblDeductionPayMonth);
			log.debug("dblDeductionAmount="+dblDeductionAmount);
			log.debug("dblDeductionPaycycleAmount="+dblDeductionPaycycleAmount);
			*/

			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblDeductionPayMonth;
		
	}
	
	
	public double calculateLOAN(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String,String> hmTotal, String strEmpId, CommonFunctions CF, Map<String,String> hmLoanAmt, Map<String,Map<String,String>> hmEmpLoan, List<String> alLoans) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		double dblTotalCalculatedAmount = 0;
		
		
		try {
			
//			Map hmLoanAmtInner = new HashMap();
			
			pst = con.prepareStatement(selectLoanPayroll2); 
			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			if(strEmpId.equalsIgnoreCase("683")) {
//				System.out.println("pst====>"+pst);
//			}
			rs = pst.executeQuery();
			double dblPrincipalAmt = 0;
			double dblBalAmt = 0;
			double dblROI = 0;
			double dblDuration = 0;
//			double dblTDSAmt = 0;
			String strApprovedDate = null;
			Map<String,String> hmEmpLoanInner = new HashMap<String,String>();
			while(rs.next()) {
				dblPrincipalAmt = rs.getDouble("amount_paid"); 
//				dblTDSAmt = rs.getDouble("tds_amount");
				dblBalAmt = rs.getDouble("balance_amount");
				dblROI = rs.getDouble("loan_interest");
				dblDuration = rs.getDouble("duration_months");
				
				strApprovedDate = rs.getString("approved_date");
//				dblPrincipalAmt += dblTDSAmt;
				if(strApprovedDate!=null) {
					Calendar calCurrent = GregorianCalendar.getInstance();
					calCurrent.setTime(uF.getCurrentDate(CF.getStrTimeZone()));
					
//					int nCurrentMonth = calCurrent.get(Calendar.MONTH);
					
					Calendar calApproved = GregorianCalendar.getInstance();
					calApproved.setTime(uF.getDateFormat(strApprovedDate, DBDATE));
					
//					int nApprovedMonth = calApproved.get(Calendar.MONTH);
					calApproved.add(Calendar.MONTH, (int)dblDuration);
					
//					int nLastMonth = calApproved.get(Calendar.MONTH);
					String strLastDate = calApproved.get(Calendar.DATE) +"/"+(calApproved.get(Calendar.MONTH)+1)+"/"+calApproved.get(Calendar.YEAR);
					int nBalanceMonths = uF.parseToInt(uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, strLastDate, DATE_FORMAT,CF.getStrTimeZone()));
					nBalanceMonths = (int)nBalanceMonths/30;
					
//					dblCalculatedAmount = uF.getEMI(dblBalAmt, dblROI, nBalanceMonths);
					dblCalculatedAmount = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
					dblCalculatedAmount = dblCalculatedAmount / dblDuration; 
					
					
					if(dblCalculatedAmount>=dblBalAmt) {
						dblCalculatedAmount = dblBalAmt;
					}
					if(dblCalculatedAmount>dblGross) {
						dblCalculatedAmount = dblGross;
					}
					dblTotalCalculatedAmount +=dblCalculatedAmount;
					hmLoanAmt.put(rs.getString("loan_applied_id"), uF.formatIntoTwoDecimal(dblCalculatedAmount));
					
					
					hmEmpLoanInner = hmEmpLoan.get(rs.getString("emp_id"));
					if(hmEmpLoanInner==null)hmEmpLoanInner=new HashMap<String,String>();
//					hmEmpLoanInner.put(rs.getString("loan_id"), uF.formatIntoTwoDecimal(dblCalculatedAmount));
					hmEmpLoanInner.put(rs.getString("loan_id"), uF.formatIntoTwoDecimal(dblTotalCalculatedAmount));
					hmEmpLoan.put(rs.getString("emp_id"), hmEmpLoanInner);
					
					if(!alLoans.contains(rs.getString("loan_id"))) {
						alLoans.add(rs.getString("loan_id"));
					}
					
				}     
			}
			rs.close();
			pst.close();
//			if(strEmpId.equalsIgnoreCase("683")) {
//				System.out.println("hmEmpLoan==>"+hmEmpLoan);
//				System.out.println("alLoans==>"+alLoans);
//			}
			request.setAttribute("hmEmpLoan", hmEmpLoan);
			request.setAttribute("alLoans", alLoans);
			 
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblTotalCalculatedAmount;
		
	}
	
public double calculateEEESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String,String> hmTotal, String strWLocationStateId, Map<String,String> hmVariables, String strEmpId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		try {
			String strLevelId = CF.getEmpLevelId(con, strEmpId);
//			System.out.println("strLevelId ===>> " + strLevelId);
//			pst = con.prepareStatement(selectESI);
			pst = con.prepareStatement("select * from esi_details where financial_year_start=? and financial_year_end=? and state_id=? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setInt(4, uF.parseToInt(strEmpId));
			pst.setInt(5, uF.parseToInt(strLevelId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			double dblEEESIAmount = 0;
			double dblESIMaxAmount = 0;
			String strSalaryHeads = null;
			String strEligibleSalaryHeads = null;
			while(rs.next()) {
				dblEEESIAmount = rs.getDouble("eesi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strEligibleSalaryHeads = rs.getString("eligible_salary_head_ids");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads != null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			List<String> alEligibleSalaryHeads = new ArrayList<String>();
			String[] arrEligibleSalaryHeads = null;
			if(strEligibleSalaryHeads != null) {
				arrEligibleSalaryHeads =strEligibleSalaryHeads.split(",");
				for (int i = 0; arrEligibleSalaryHeads != null && i < arrEligibleSalaryHeads.length; i++) {
					if (uF.parseToInt(arrEligibleSalaryHeads[i].trim()) > 0) {
						alEligibleSalaryHeads.add(arrEligibleSalaryHeads[i].trim());
					}
				}
			}
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				if (alEligibleSalaryHeads.contains(arrSalaryHeads[i].trim())) {
					dblAmountEligibility = dblAmountEligibility + uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
//					System.out.println("arrSalaryHeads[i] ===>> " + arrSalaryHeads[i] + " -- amount ===>> " + hmTotal.get(arrSalaryHeads[i].trim()));
				}
				dblAmount = dblAmount + uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
			}
			
			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
				dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
			}
//			System.out.println("dblAmountEligibility=" + dblAmountEligibility);
//			System.out.println("strSalaryHeads=" + strSalaryHeads);
//			System.out.println("dblCalculatedAmount=" + dblCalculatedAmount);
//			System.out.println("dblAmount=" + dblAmount);
//			System.out.println("dblESIMaxAmount=" + dblESIMaxAmount);
			
		} catch (Exception e) {
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
		return dblCalculatedAmount;
		
	}
	
	public double calculateERESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, 
			Map<String,String> hmTotal, String strWLocationStateId,String strEmpId, Map<String, String> hmVariables, Map<String, String> hmAnnualVariables) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		try {
			String strLevelId = CF.getEmpLevelId(con, strEmpId);
			
//			pst = con.prepareStatement(selectERESI);
			pst =con.prepareStatement("select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id =? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setInt(4, uF.parseToInt(strEmpId));
			pst.setInt(5, uF.parseToInt(strLevelId));
			rs = pst.executeQuery();
			double dblERESIAmount = 0;
			double dblESIMaxAmount = 0;
			String strSalaryHeads = null;
			String strEligibleSalaryHeads = null;
			while(rs.next()) {
				dblERESIAmount = rs.getDouble("ersi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strEligibleSalaryHeads = rs.getString("eligible_salary_head_ids");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			List<String> alEligibleSalaryHeads = new ArrayList<String>();
			String[] arrEligibleSalaryHeads = null;
			if(strEligibleSalaryHeads != null) {
				arrEligibleSalaryHeads =strEligibleSalaryHeads.split(",");
				for (int i = 0; arrEligibleSalaryHeads != null && i < arrEligibleSalaryHeads.length; i++) {
					if (uF.parseToInt(arrEligibleSalaryHeads[i].trim()) > 0) {
						alEligibleSalaryHeads.add(arrEligibleSalaryHeads[i].trim());
					}
				}
			}
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				if (alEligibleSalaryHeads.contains(arrSalaryHeads[i].trim())) {
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
			}
//			System.out.println("hmTotal==>"+hmTotal);
//			System.out.println("dblAmount==>"+dblAmount);
//			System.out.println("dblAmountEligibility==>"+dblAmountEligibility);
//			System.out.println("dblESIMaxAmount==>"+dblESIMaxAmount);
//			System.out.println("dblERESIAmount==>"+dblERESIAmount);
			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
				dblCalculatedAmount = ((dblERESIAmount * dblAmount) / 100);
			}
//			System.out.println("dblCalculatedAmount==>"+dblCalculatedAmount);
		} catch (Exception e) {
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
		return dblCalculatedAmount;
	}
		
	public double calculateCandiERESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String,String> hmTotal, String strWLocationStateId,String strEmpId, int nOrgId,int nLevelId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		try {
			
			pst =con.prepareStatement("select * from esi_details where financial_year_start=? and financial_year_end=? and state_id=? " +
					"and org_id=? and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setInt(4, nOrgId);
			pst.setInt(5, nLevelId);
			rs = pst.executeQuery();
			double dblERESIAmount = 0;
			double dblESIMaxAmount = 0;
			String strSalaryHeads = null;
			String strEligibleSalaryHeads = null;
			while(rs.next()) {
				dblERESIAmount = rs.getDouble("ersi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strEligibleSalaryHeads = rs.getString("eligible_salary_head_ids");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			List<String> alEligibleSalaryHeads = new ArrayList<String>();
			String[] arrEligibleSalaryHeads = null;
			if(strEligibleSalaryHeads != null) {
				arrEligibleSalaryHeads =strEligibleSalaryHeads.split(",");
				for (int i = 0; arrEligibleSalaryHeads != null && i < arrEligibleSalaryHeads.length; i++) {
					if (uF.parseToInt(arrEligibleSalaryHeads[i].trim()) > 0) {
						alEligibleSalaryHeads.add(arrEligibleSalaryHeads[i].trim());
					}
				}
			}
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				if (alEligibleSalaryHeads.contains(arrSalaryHeads[i].trim())) {
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
			}
			
			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
				dblCalculatedAmount = ((dblERESIAmount * dblAmount) / 100);
			}
//			System.out.println("dblAmount======>"+dblAmount+"---dblERESIAmount====>"+dblERESIAmount+"---dblCalculatedAmount====>"+dblCalculatedAmount);
		} catch (Exception e) {
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
		return dblCalculatedAmount;
	}
	
	public double calculateEELWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, String strWLocationStateId, Map<String, String> hmVariables, String strEmpId, int nPayMonth, String strOrgId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		try {
			
			pst = con.prepareStatement(selectLWF);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setInt(4, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			
//			double dblERLWFAmount = 0;
//			double dblLWFMaxAmount = 0;
			String strSalaryHeads = null;
			String strMonths = null;
			while(rs.next()) {
//				dblERLWFAmount = rs.getDouble("eelfw_contribution");
//				dblLWFMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strMonths  = rs.getString("months");
			}
			rs.close();
			pst.close();

			
			String []arrMonths = null;
			if(strMonths!=null) {
				arrMonths = strMonths.split(",");
			}
			
			if(ArrayUtils.contains(arrMonths, nPayMonth+"")>=0) {
				
				String []arrSalaryHeads = null;
				if(strSalaryHeads!=null) {
					arrSalaryHeads = strSalaryHeads.split(",");
				}
				
				
				double dblAmount = 0;
				for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
					dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
				}
				
				
				pst = con.prepareStatement(selectERLWFC);
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strWLocationStateId));
				pst.setDouble(4, dblAmount);
				pst.setDouble(5, dblAmount);
				pst.setInt(6, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				
				while(rs.next()) {
					dblCalculatedAmount = uF.parseToDouble(rs.getString("eelfw_contribution"));
				}
				rs.close();
				pst.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblCalculatedAmount;
	}
	
	public double calculateERLWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, String strWLocationStateId, int nPayMonth, String strOrgId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		
		try {
			
			pst = con.prepareStatement(selectERLWF);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setInt(4, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
//			double dblERLWFAmount = 0;
//			double dblLWFMaxAmount = 0;
			String strSalaryHeads = null;
			String strMonths = null;
			while(rs.next()) {
//				dblERLWFAmount = rs.getDouble("erlfw_contribution");
//				dblLWFMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strMonths  = rs.getString("months");
			}
			rs.close();
			pst.close();

			String []arrMonths = null;
			if(strMonths!=null) {
				arrMonths = strMonths.split(",");
			}
			
			if(ArrayUtils.contains(arrMonths, nPayMonth+"")>=0) {
				String []arrSalaryHeads = null;
				if(strSalaryHeads!=null) {
					arrSalaryHeads = strSalaryHeads.split(",");
				}
				
				
				double dblAmount = 0;
				for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
					dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
				}
				
				
				pst = con.prepareStatement(selectERLWFC);
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strWLocationStateId));
				pst.setDouble(4, dblAmount);
				pst.setDouble(5, dblAmount);
				pst.setInt(6, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				
				while(rs.next()) {
					dblCalculatedAmount = uF.parseToDouble(rs.getString("erlfw_contribution"));
				}
				rs.close();
				pst.close();
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblCalculatedAmount;
	}

	public double calculateEEPF(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, Map<String, String> hmVoluntaryPF, String strEmpId, String strMonth, String strPaycycle, boolean isInsert, Map<String, Map<String, String>> hmArearAmountMap) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);         
				con = db.makeConnection(con);
			}
			if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
			
			
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, " +
					"level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  " +
					"and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));	
			pst.setInt(4, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			double dblEEPFAmount = 0;
			double dblMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()) {
				dblEEPFAmount = rs.getDouble("eepf_contribution");
				dblMaxAmount = rs.getDouble("epf_max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();
//			System.out.println("dblEEPFAmount ===>> " + dblEEPFAmount + " -- dblMaxAmount ===>> " + dblMaxAmount+ " -- strSalaryHeads ===>> " + strSalaryHeads);
			
			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			Map<String,String> hmArearMap = hmArearAmountMap.get(strEmpId);
			if(hmArearMap==null)hmArearMap=new HashMap<String,String>();

			double dblArrearBasic = uF.parseToDouble(hmArearMap.get("BASIC_AMOUNT"));
			
			dblAmount += dblArrearBasic; 
			
			/**
			 * Change on 24-04-2012
			 */
			
			if(dblAmount>=dblMaxAmount) {
				dblAmount = dblMaxAmount;
				
			}
			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
			
//			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
//			if(dblCalculatedAmount>=dblMaxAmount) {
//				dblCalculatedAmount = dblMaxAmount;
//			}
			
//			System.out.println("dblCalculatedAmount===>"+dblCalculatedAmount);
			
			
			
			/**
			 * If VPF is to be calculated separately,
			 * the the below code needs to be commented
			 * 
			 * */
			
//			if(hmVoluntaryPF==null) {
//				hmVoluntaryPF = new HashMap();
//			}
//			dblCalculatedAmount += uF.parseToDouble(hmVoluntaryPF.get("AMOUNT"));
//			
			
			
			
			
			
			if(isInsert) {
				
				/*if(hmVoluntaryPF==null) {
					hmVoluntaryPF = new HashMap();
				}
				dblCalculatedAmount += uF.parseToDouble(hmVoluntaryPF.get("AMOUNT")); */
				double dblEVPF = uF.parseToDouble(hmTotal.get(VOLUNTARY_EPF+""));
					
				pst = con.prepareStatement("insert into emp_epf_details (financial_year_start, financial_year_end, salary_head_id, epf_max_limit, eepf_contribution, emp_id, paycycle, _month, evpf_contribution) values (?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
						
			//  pst.setDouble(4, Math.round(dblAmount));
			//  pst.setDouble(5, Math.round(dblCalculatedAmount)); 
				pst.setDouble(4, dblAmount);
				pst.setDouble(5, dblCalculatedAmount); 
				pst.setInt(6, uF.parseToInt(strEmpId));
				pst.setInt(7, uF.parseToInt(strPaycycle));
				pst.setInt(8, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
				//pst.setDouble(9, Math.round(dblEVPF));
				pst.setDouble(9,dblEVPF);
				pst.execute();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			if(db!=null) {
				db.closeConnection(con);
			}
			
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
		return dblCalculatedAmount;
		
	}
	
	public double calculateCandiEEPF(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, 
			String strFinancialYearEnd, Map<String, String> hmTotal, String strEmpId,int nLevelId, int nOrgId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);         
				con = db.makeConnection(con);
			}
			
			
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? and org_id=? and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, nOrgId);	
			pst.setInt(4, nLevelId);
			rs = pst.executeQuery();
			double dblEEPFAmount = 0;
			double dblMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()) {
				dblEEPFAmount = rs.getDouble("eepf_contribution");
				dblMaxAmount = rs.getDouble("epf_max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			/**
			 * Change on 24-04-2012
			 */
			
			if(dblAmount>=dblMaxAmount) {
				dblAmount = dblMaxAmount;
				
			}
			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null) {
				db.closeConnection(con);
			}
			
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
		return dblCalculatedAmount;
		
	}

	public double calculateERPF(Connection con,CommonFunctions CF, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal,  String strEmpId, String strMonth, String strPaycycle, boolean isInsert, Map<String, Map<String, String>> hmArearAmountMap) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		double dblEPS1 = 0;
		double dblEPS = 0;
		double dblEPF = 0;
		double dblEDLI = 0;
		
		double dblEPFAdmin = 0;
		double dblEDLIAdmin = 0;
		
		double dblTotalEPF = 0;
		double dblTotalEDLI = 0;
		
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
			
//			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? and org_id in (select org_id from employee_official_details where emp_id=?) ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(strEmpId));	
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, " +
					"level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  " +
					"and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));	
			pst.setInt(4, uF.parseToInt(strEmpId)); 
			rs = pst.executeQuery();
			
			double dblERPFAmount = 0;
			double dblERPSAmount = 0;
			double dblERDLIAmount = 0;
			double dblPFAdminAmount = 0;
			double dblEDLIAdminAmount = 0;
			double dblEPFMaxAmount = 0;
			double dblEPRMaxAmount = 0;
			double dblEPSMaxAmount = 0;
			double dblEDLIMaxAmount = 0;
			String strSalaryHeads = null;
			
			
			boolean erpfContributionchbox = false;
			boolean erpsContributionchbox = false;
			boolean pfAdminChargeschbox = false;
			boolean edliAdminChargeschbox = false;
			boolean erdliContributionchbox = false;
			
			while(rs.next()) {   
				
				dblERPFAmount = rs.getDouble("erpf_contribution");
				dblERPSAmount = rs.getDouble("erps_contribution");
				dblERDLIAmount = rs.getDouble("erdli_contribution");
				dblPFAdminAmount = rs.getDouble("pf_admin_charges");
				dblEDLIAdminAmount = rs.getDouble("edli_admin_charges");
				
				dblEPRMaxAmount = rs.getDouble("erpf_max_limit");
				dblEPFMaxAmount = rs.getDouble("epf_max_limit");
				dblEPSMaxAmount = rs.getDouble("eps_max_limit");
				dblEDLIMaxAmount = rs.getDouble("edli_max_limit");
				
				strSalaryHeads = rs.getString("salary_head_id");
				
				
				erpfContributionchbox = rs.getBoolean("is_erpf_contribution");
				erpsContributionchbox = rs.getBoolean("is_erps_contribution");
				pfAdminChargeschbox = rs.getBoolean("is_pf_admin_charges");
				edliAdminChargeschbox = rs.getBoolean("is_edli_admin_charges");
				erdliContributionchbox = rs.getBoolean("is_erdli_contribution");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			double dblAmountERPF = 0;
			double dblAmountEEPF = 0;
			double dblAmountERPS = 0;
			double dblAmountERPS1 = 0;
			double dblAmountEREDLI = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			
			Map<String,String> hmArearMap = hmArearAmountMap.get(strEmpId);
			if(hmArearMap==null)hmArearMap=new HashMap<String,String>();

			double dblArrearBasic = uF.parseToDouble(hmArearMap.get("BASIC_AMOUNT"));
			
			dblAmount += dblArrearBasic; 
			
			/**
			 * Changed on 24-04-2012
			 * 
			 */
			
			
			
//			System.out.println("strEmpID=========>"+strEmpID);
//			
//			
//			System.out.println("AP/13088---dblAmount===="+dblAmount+"--dblEDLIMaxAmount=="+dblEDLIMaxAmount); 
//			System.out.println("dblEPFMaxAmount===="+dblEPFMaxAmount);
//			
			
			if(dblAmount>=dblEPRMaxAmount) {
				dblAmountERPF = dblEPRMaxAmount;
			} else {
				dblAmountERPF = dblAmount;
			}
			
			if(dblAmount>=dblEPFMaxAmount) {
				dblAmountEEPF = dblEPFMaxAmount;
			} else {
				dblAmountEEPF = dblAmount;
			}
			
			
			dblAmountERPS1 = dblAmount;
			if(dblAmount>=dblEPSMaxAmount) {
				dblAmountERPS = dblEPSMaxAmount;
			} else {
				dblAmountERPS = dblAmount;
			}
			
			if(dblAmount>=dblEDLIMaxAmount) {
				dblAmountEREDLI = dblEDLIMaxAmount;
			} else {
				dblAmountEREDLI = dblAmount;
			}
			
			
			
			if(isInsert) {
//				System.out.println("isInsert====");
				dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
				dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
				
				dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
				
				
				dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
				dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
//				System.out.println("AP/13130---dblEDLIAdminAmount==>"+dblEDLIAdminAmount+"====dblAmountEREDLI==>"+dblAmountEREDLI);
				dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
			} else {
				if(erpfContributionchbox) {
//					System.out.println("erpfContributionchbox====");

					dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
				}
//				System.out.println("erpfContributionchbox====dblERPFAmount==>"+dblERPFAmount+"====dblAmountERPF==>"+dblAmountERPF+"====dblEPF==>"+dblEPF);
				if(erpsContributionchbox) {
					dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
					dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
				}
//				System.out.println("erpsContributionchbox====dblERPSAmount==>"+dblERPSAmount+"====dblAmountERPS==>"+dblAmountERPS+"====dblEPF==>"+dblEPS);	
				if(erdliContributionchbox) {
					dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
				}
//				System.out.println("erdliContributionchbox==>"+erdliContributionchbox+"====dblERDLIAmount==>"+dblERDLIAmount+"====dblAmountEREDLI==>"+dblAmountEREDLI+"====dblEDLI==>"+dblEDLI);
				
				if(edliAdminChargeschbox) {
					dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
//					System.out.println("AP/13150---else---dblEDLIAdminAmount==>"+dblEDLIAdminAmount+"====dblAmountEREDLI==>"+dblAmountEREDLI);
				}
//				System.out.println("edliAdminChargeschbox==>"+edliAdminChargeschbox+"====dblEDLIAdminAmount==>"+dblEDLIAdminAmount+"====dblAmountEREDLI==>"+dblAmountEREDLI+"====dblEDLIAdmin==>"+dblEDLIAdmin);
				
				if(pfAdminChargeschbox) {
					dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
				}
//				System.out.println("pfAdminChargeschbox==>"+pfAdminChargeschbox+"====dblPFAdminAmount==>"+dblPFAdminAmount+"====dblAmountEEPF==>"+dblAmountEEPF+"====dblEPFAdmin==>"+dblEPFAdmin);
			}
			

			if(CF.isEPF_Condition1()) {
//				System.out.println("isEPF_Condition1====");
				dblEPF += dblEPS1 - dblEPS;
			}
			
			
			
			dblTotalEDLI = dblEDLI + dblEDLIAdmin;
			dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;
//			System.out.println("dblEDLI==>"+dblEDLI+"====dblEDLIAdmin==>"+dblEDLIAdmin+"====dblTotalEDLI==>"+dblTotalEDLI);
//			System.out.println("dblEPF==>"+dblEPF+"====dblEPS==>"+dblEPS+"====dblEPFAdmin==>"+dblEPFAdmin+"====dblTotalEPF==>"+dblTotalEPF);
//			System.out.println("(dblTotalEPF + dblTotalEDLI)==>"+(dblTotalEPF + dblTotalEDLI));
			if(isInsert) {
		//===start parvez date: 14-01-2022===
				
				String pfStartDate = "";
				
				pst = con.prepareStatement("select pf_start_date from employee_personal_details where emp_per_id=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				rs = pst.executeQuery();
				
				while(rs.next()){
					pfStartDate = rs.getString("pf_start_date");
				}
				rs.close();
				pst.close();
				
				boolean isEnableEmployerPf = CF.getFeatureManagementStatus(request, uF, F_ENABLE_ADD_AMOUNT_IN_EMPLOYER_PF_ONLY);
				if(isEnableEmployerPf && uF.parseToInt(uF.getDateFormat(pfStartDate, DBDATE, "yyyy")) >= 2015){
					pst = con.prepareStatement("update emp_epf_details set  eps_max_limit=?, edli_max_limit=?, erpf_contribution=?, erps_contribution=?, erdli_contribution=?, pf_admin_charges=?, edli_admin_charges=?  where financial_year_start=? and financial_year_end=? and emp_id=? and paycycle=? and _month=?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmountERPS)));
					pst.setDouble(2, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmountEREDLI)));
					double totalErpf = dblEPF + dblEPS;
					pst.setDouble(3, totalErpf);
					pst.setDouble(4, 0);
					pst.setDouble(5,dblEDLI);
					pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEPFAdmin)));
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEDLIAdmin)));
					pst.setDate(8, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(10, uF.parseToInt(strEmpId));
					pst.setInt(11, uF.parseToInt(strPaycycle));
					pst.setInt(12, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM"))); 
					pst.execute();
					pst.close();
				} else{
					pst = con.prepareStatement("update emp_epf_details set  eps_max_limit=?, edli_max_limit=?, erpf_contribution=?, erps_contribution=?, erdli_contribution=?, pf_admin_charges=?, edli_admin_charges=?  where financial_year_start=? and financial_year_end=? and emp_id=? and paycycle=? and _month=?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmountERPS)));
					pst.setDouble(2, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmountEREDLI)));
					/*pst.setDouble(3, Math.round(dblEPF));
					pst.setDouble(4, Math.round(dblEPS));
					pst.setDouble(5, Math.round(dblEDLI));*/
					pst.setDouble(3, dblEPF);
					pst.setDouble(4,dblEPS);
					pst.setDouble(5,dblEDLI);
					pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEPFAdmin)));
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEDLIAdmin)));
					pst.setDate(8, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(10, uF.parseToInt(strEmpId));
					pst.setInt(11, uF.parseToInt(strPaycycle));
					pst.setInt(12, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM"))); 
					pst.execute();
					pst.close();
				}
		//===end parvez date: 14-01-2022===		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null) {
				db.closeConnection(con);
			}
			
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
		return (dblTotalEPF + dblTotalEDLI);
		
	}
	
	
public double calculateCandiERPF(Connection con,CommonFunctions CF, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, String strEmpId, int nLevelId, int nOrgId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		double dblEPS1 = 0;
		double dblEPS = 0;
		double dblEPF = 0;
		double dblEDLI = 0;
		
		double dblEPFAdmin = 0;
		double dblEDLIAdmin = 0;
		
		double dblTotalEPF = 0;
		double dblTotalEDLI = 0;
		
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? " +
					"and org_id=? and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, nOrgId);	
			pst.setInt(4, nLevelId); 
			rs = pst.executeQuery();
			
			double dblERPFAmount = 0;
			double dblERPSAmount = 0;
			double dblERDLIAmount = 0;
			double dblPFAdminAmount = 0;
			double dblEDLIAdminAmount = 0;
			double dblEPFMaxAmount = 0;
			double dblEPRMaxAmount = 0;
			double dblEPSMaxAmount = 0;
			double dblEDLIMaxAmount = 0;
			String strSalaryHeads = null;
			
			
			boolean erpfContributionchbox = false;
			boolean erpsContributionchbox = false;
			boolean pfAdminChargeschbox = false;
			boolean edliAdminChargeschbox = false;
			boolean erdliContributionchbox = false;
			
			while(rs.next()) {   
				
				dblERPFAmount = rs.getDouble("erpf_contribution");
				dblERPSAmount = rs.getDouble("erps_contribution");
				dblERDLIAmount = rs.getDouble("erdli_contribution");
				dblPFAdminAmount = rs.getDouble("pf_admin_charges");
				dblEDLIAdminAmount = rs.getDouble("edli_admin_charges");
				
				dblEPRMaxAmount = rs.getDouble("erpf_max_limit");
				dblEPFMaxAmount = rs.getDouble("epf_max_limit");
				dblEPSMaxAmount = rs.getDouble("eps_max_limit");
				dblEDLIMaxAmount = rs.getDouble("edli_max_limit");
				
				strSalaryHeads = rs.getString("salary_head_id");
				
				
				erpfContributionchbox = rs.getBoolean("is_erpf_contribution");
				erpsContributionchbox = rs.getBoolean("is_erps_contribution");
				pfAdminChargeschbox = rs.getBoolean("is_pf_admin_charges");
				edliAdminChargeschbox = rs.getBoolean("is_edli_admin_charges");
				erdliContributionchbox = rs.getBoolean("is_erdli_contribution");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			double dblAmountERPF = 0;
			double dblAmountEEPF = 0;
			double dblAmountERPS = 0;
			double dblAmountERPS1 = 0;
			double dblAmountEREDLI = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			if(dblAmount>=dblEPRMaxAmount) {
				dblAmountERPF = dblEPRMaxAmount;
			} else {
				dblAmountERPF = dblAmount;
			}
			
			if(dblAmount>=dblEPFMaxAmount) {
				dblAmountEEPF = dblEPFMaxAmount;
			} else {
				dblAmountEEPF = dblAmount;
			}
			
			
			dblAmountERPS1 = dblAmount;
			if(dblAmount>=dblEPSMaxAmount) {
				dblAmountERPS = dblEPSMaxAmount;
			} else {
				dblAmountERPS = dblAmount;
			}
			
			if(dblAmount>=dblEDLIMaxAmount) {
				dblAmountEREDLI = dblEDLIMaxAmount;
			} else {
				dblAmountEREDLI = dblAmount;
			}
			
			if(erpfContributionchbox) {
				dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
			}
			if(erpsContributionchbox) {
				dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
				dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
			}
			if(erdliContributionchbox) {
				dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
			}
			
			if(edliAdminChargeschbox) {
				dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
			}
			
			if(pfAdminChargeschbox) {
				dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
			}

			if(CF.isEPF_Condition1()) {
				dblEPF += dblEPS1 - dblEPS;
			}
			
			dblTotalEDLI = dblEDLI + dblEDLIAdmin;
			dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null) {
				db.closeConnection(con);
			}
			
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
		return (dblTotalEPF + dblTotalEDLI);
		
	}
	
	
public double calculateERPFandEPS(Connection con,CommonFunctions CF, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal,  String strEmpId, String strMonth, String strPaycycle, boolean isInsert, Map<String, Map<String, String>> hmArearAmountMap) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		double dblEPS1 = 0;
		double dblEPS = 0;
		double dblEPF = 0;
		
		double dblTotalEPF = 0;
		
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
			
//			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? and org_id in (select org_id from employee_official_details where emp_id=?) ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(strEmpId));	
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, " +
					"level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  " +
					"and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));	
			pst.setInt(4, uF.parseToInt(strEmpId)); 
			rs = pst.executeQuery();
			
			double dblERPFAmount = 0;
			double dblERPSAmount = 0;
			double dblERDLIAmount = 0;
			double dblPFAdminAmount = 0;
			double dblEDLIAdminAmount = 0;
			double dblEPFMaxAmount = 0;
			double dblEPRMaxAmount = 0;
			double dblEPSMaxAmount = 0;
			double dblEDLIMaxAmount = 0;
			String strSalaryHeads = null;
			
			
			boolean erpfContributionchbox = false;
			boolean erpsContributionchbox = false;
			boolean pfAdminChargeschbox = false;
			boolean edliAdminChargeschbox = false;
			boolean erdliContributionchbox = false;
			
			while(rs.next()) {   
				
				dblERPFAmount = rs.getDouble("erpf_contribution");
				dblERPSAmount = rs.getDouble("erps_contribution");
				dblERDLIAmount = rs.getDouble("erdli_contribution");
				dblPFAdminAmount = rs.getDouble("pf_admin_charges");
				dblEDLIAdminAmount = rs.getDouble("edli_admin_charges");
				
				dblEPRMaxAmount = rs.getDouble("erpf_max_limit");
				dblEPFMaxAmount = rs.getDouble("epf_max_limit");
				dblEPSMaxAmount = rs.getDouble("eps_max_limit");
				dblEDLIMaxAmount = rs.getDouble("edli_max_limit");
				
				strSalaryHeads = rs.getString("salary_head_id");
				
				
				erpfContributionchbox = rs.getBoolean("is_erpf_contribution");
				erpsContributionchbox = rs.getBoolean("is_erps_contribution");
				pfAdminChargeschbox = rs.getBoolean("is_pf_admin_charges");
				edliAdminChargeschbox = rs.getBoolean("is_edli_admin_charges");
				erdliContributionchbox = rs.getBoolean("is_erdli_contribution");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			double dblAmountERPF = 0;
			double dblAmountEEPF = 0;
			double dblAmountERPS = 0;
			double dblAmountERPS1 = 0;
			double dblAmountEREDLI = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			
			Map<String,String> hmArearMap = hmArearAmountMap.get(strEmpId);
			if(hmArearMap==null)hmArearMap=new HashMap<String,String>();

			double dblArrearBasic = uF.parseToDouble(hmArearMap.get("BASIC_AMOUNT"));
			
			dblAmount += dblArrearBasic; 
			
			
			if(dblAmount>=dblEPRMaxAmount) {
				dblAmountERPF = dblEPRMaxAmount;
			} else {
				dblAmountERPF = dblAmount;
			}
			
			if(dblAmount>=dblEPFMaxAmount) {
				dblAmountEEPF = dblEPFMaxAmount;
			} else {
				dblAmountEEPF = dblAmount;
			}
			
			
			dblAmountERPS1 = dblAmount;
			if(dblAmount>=dblEPSMaxAmount) {
				dblAmountERPS = dblEPSMaxAmount;
			} else {
				dblAmountERPS = dblAmount;
			}
			
			if(dblAmount>=dblEDLIMaxAmount) {
				dblAmountEREDLI = dblEDLIMaxAmount;
			} else {
				dblAmountEREDLI = dblAmount;
			}
			
			
			
			
			if(erpfContributionchbox) {
				dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
			}
			if(erpsContributionchbox) {
				dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
				dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
			}
				
			

			if(CF.isEPF_Condition1()) {
//				System.out.println("isEPF_Condition1====");
				dblEPF += dblEPS1 - dblEPS;
			}
			
			
			
			dblTotalEPF = dblEPF + dblEPS;
//			System.out.println("dblTotalEPF===="+dblTotalEPF); 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(db!=null) {
				db.closeConnection(con);
			}
			
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
		return dblTotalEPF;
		
	}
	
	
	public void calculateEESI(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map<String, String> hmVariables, boolean isInsert) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmountEESI = 0;
		double dblCalculatedAmountERSI = 0;
		
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			
			String strLevelId = CF.getEmpLevelId(con, strEmpId);
			
			pst = con.prepareStatement("select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id=? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
			pst.setInt(4, uF.parseToInt(strEmpId));
			pst.setInt(5, uF.parseToInt(strLevelId));
			rs = pst.executeQuery();			
			double dblEESIAmount = 0;
			double dblERSIAmount = 0;
			double dblMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()) {
				dblEESIAmount = rs.getDouble("eesi_contribution");
				dblERSIAmount = rs.getDouble("ersi_contribution");
				dblMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}			
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
//				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")) {
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
//				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			/**
			 * Change on 24-04-2012
			 */
			
			if(dblAmountEligibility > dblMaxAmount) {
				return;
			}
			
			dblCalculatedAmountEESI = ( dblEESIAmount * dblAmount ) / 100;
			dblCalculatedAmountERSI = ( dblERSIAmount * dblAmount ) / 100;
			
//			dblCalculatedAmountEESI = ( dblEESIAmount * dblAmountEligibility ) / 100;
//			dblCalculatedAmountERSI = ( dblERSIAmount * dblAmountEligibility ) / 100;
			
			
//			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
//			if(dblCalculatedAmount>=dblMaxAmount) {
//				dblCalculatedAmount = dblMaxAmount;
//			}
			
			if(isInsert) {
				pst = con.prepareStatement("insert into emp_esi_details (financial_year_start, financial_year_end, salary_head_id, esi_max_limit, " +
						"eesi_contribution, ersi_contribution, user_id, entry_timestamp, emp_id, paycycle, _month) values (?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
				pst.setDouble(4,dblAmount); 
				pst.setDouble(5, Math.ceil(dblCalculatedAmountEESI));
				pst.setDouble(6, Math.ceil(dblCalculatedAmountERSI));
				pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(8, null);
				pst.setInt(9, uF.parseToInt(strEmpId));
				pst.setInt(10, uF.parseToInt(strPaycycle));
				pst.setInt(11, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
				pst.execute();
				pst.close();				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			if(db!=null) {
				db.closeConnection(con);
			}
			
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
	}

	
	public void calculateELWF(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map<String, String> hmVariables, boolean isInsert, String strOrgId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmountEELWF = 0;
		double dblCalculatedAmountERLWF = 0;
		
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			
			pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? and org_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
			pst.setInt(4, uF.parseToInt(hmEmpStateMap.get(strOrgId)));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			
			String strSalaryHeads = null;
			while(rs.next()) {
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();
			
			String[] arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")) {
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			} 
			
			pst = con.prepareStatement("select * from lwf_details where financial_year_start= ? and financial_year_end = ? and state_id=? " +
					" and ? between min_limit and max_limit and org_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
			pst.setDouble(4,dblAmount);
			pst.setInt(5, uF.parseToInt(hmEmpStateMap.get(strOrgId)));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			
			double dblEELWFAmount = 0;
			double dblERLWFAmount = 0;
			double dblMaxAmount = 0;
			String lwfMonth=null;
			while(rs.next()) {
				dblEELWFAmount = rs.getDouble("eelfw_contribution");
				dblERLWFAmount = rs.getDouble("erlfw_contribution");
				dblMaxAmount = rs.getDouble("max_limit");
				lwfMonth=rs.getString("months");
			}
			rs.close();
			pst.close();			
			
			if(dblAmountEligibility>=dblMaxAmount) {
				return;
			}
			
			
			List<String> lwfMonthList=null;
			if(lwfMonth!=null) {
				lwfMonthList=Arrays.asList(lwfMonth.split(","));
			}
			
			
			int month=uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM"));
			if(lwfMonthList==null || !lwfMonthList.contains(""+month)) {
				return;
			}
			
			dblCalculatedAmountEELWF = dblEELWFAmount ;
			dblCalculatedAmountERLWF = dblERLWFAmount;
			
			
			if(isInsert) {
				pst = con.prepareStatement("insert into emp_lwf_details (financial_year_start, financial_year_end, salary_head_id, " +
						"lwf_max_limit, eelwf_contribution, erlwf_contribution, user_id, entry_timestamp, emp_id, paycycle, _month) " +
						"values (?,?,?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
				pst.setDouble(4, dblAmount); 
				pst.setDouble(5, dblCalculatedAmountEELWF);
				pst.setDouble(6, dblCalculatedAmountERLWF);
				pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(8, null);
				pst.setInt(9, uF.parseToInt(strEmpId));
				pst.setInt(10, uF.parseToInt(strPaycycle));
				pst.setInt(11, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
				pst.execute();
				pst.close();
//				System.out.println("pst====>"+pst);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			if(db!=null) {
				db.closeConnection(con);
			}
			
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
	}

//	public double calculateTDS(Connection con, UtilityFunctions uF,String strD2,String strD1, double dblGross, double dblCess1, double dblCess2, 
//			double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblBasicDA, int nPayMonth, String strPaycycleStart, 
//			String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge, String strWLocationStateId,
//			Map<String, String> hmEmpExemptionsMap, Map<String, String> hmEmpHomeLoanMap, Map<String, String> hmFixedExemptions, 
//			Map<String, String> hmEmpMertoMap, Map<String, String> hmEmpRentPaidMap, Map<String, String> hmPaidSalaryDetails,
//			Map<String, String> hmTotal, Map<String, String> hmSalaryDetails, Map<String, String> hmEmpLevelMap, CommonFunctions CF, 
//			Map<String, String> hmPrevEmpTdsAmount,Map<String, String> hmPrevEmpGrossAmount,Map<String, String> hmEmpIncomeOtherSourcesMap,
//			Map<String, String> hmOtherTaxDetails, Map<String, String> hmEmpStateMap) {
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblTDSMonth = 0;
//		
//		
//		try { 
//			
//			int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
//			
//			/**
//			 * TDS Projection
//			 * If there is any amount specified for TDS to be deducted in projection table, 
//			 * then it will consider that amount as a TDS for that particular month and else
//			 * it will calculate the TDS based on the actual calculations.
//			 * */
//			pst = con.prepareStatement("select * from tds_projections where emp_id =? and month=? and fy_year_from=? and fy_year_end=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, nPayMonth);
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			if(rs.next()) {
//				dblTDSMonth = rs.getDouble("amount");
//				return dblTDSMonth;
//			}
//			rs.close();
//			pst.close();
//			
//			if(uF.parseToBoolean(hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))) {
//				dblTDSMonth = dblGross * dblFlatTDS / 100;
////				dblTDSMonth += (dblCess1 * 0.01 * dblTDSMonth) + (dblCess2 * 0.01 * dblTDSMonth); 
//			} else {
//				pst = con.prepareStatement(selectTDS);
//				pst.setInt(1, TDS);
//				pst.setInt(2, uF.parseToInt(strEmpId));
//				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				rs = pst.executeQuery();
//				double dblTDSPaidAmount = 0;
//				while(rs.next()) {
//					dblTDSPaidAmount = rs.getDouble("tds");
//				}
//				rs.close();
//				pst.close();
//				
//				dblTDSPaidAmount+= uF.parseToDouble(hmPrevEmpTdsAmount.get(strEmpId));
//				
////				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation pg, salary_details sd where pg.salary_head_id=sd.salary_head_id and emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ?");
//				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ? and salary_head_id not in ("+REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+")");
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				rs = pst.executeQuery();
//				double dblGrossPaidAmount = 0;
//				while(rs.next()) {
//					dblGrossPaidAmount = rs.getDouble("amount");
//				}
//				rs.close();
//				pst.close();
//				
//				Map<String, String> hmReimbursementAmt = new HashMap<String, String>();
//				if(CF.getIsReceipt()) {
//					Map<String, String> hmOrg = CF.getOrgDetails(con, uF, ""+nEmpOrgId);
//					String[] firstArr = CF.getPayCycleFromDate(con, strFinancialYearStart, CF.getStrTimeZone(), CF, ""+nEmpOrgId);
//					String[] secondArr = null;
//					if(uF.parseToInt(uF.getDateFormat(hmOrg.get("ORG_START_PAYCYCLE"), DATE_FORMAT, "dd")) > 1) {
//						secondArr = CF.getPrevPayCycleByOrg(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, ""+nEmpOrgId);
//					} else {
//						secondArr = CF.getPayCycleFromDate(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, ""+nEmpOrgId);
//					}
//					pst = con.prepareStatement("select emp_id,sum(reimbursement_amount) as reimbursement_amount " +
//							"from emp_reimbursement where approval_1 =1 and ispaid=true and (ref_document is null or ref_document='' " +
//							"or upper(ref_document) ='NULL') and from_date>=? and to_date<=? and emp_id in ("+strEmpId+") group by emp_id");
//					pst.setDate(1, uF.getDateFormat(firstArr[0], DATE_FORMAT));
//					pst.setDate(2, uF.getDateFormat(secondArr[1], DATE_FORMAT));
//					rs = pst.executeQuery();
////					System.out.println("pst====>"+pst);
//					while(rs.next()) {
//						hmReimbursementAmt.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("reimbursement_amount")));
//					}
//					rs.close();
//					pst.close();
//				}
//				
//				dblGrossPaidAmount+= uF.parseToDouble(hmPrevEmpGrossAmount.get(strEmpId)) + uF.parseToDouble(hmReimbursementAmt.get(strEmpId));
//				
//				double dblPerkAlignTDSAmount = CF.getPerkAlignTDSAmount(con, CF,uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, nEmpOrgId, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
////				System.out.println("dblGrossPaidAmount====>"+dblGrossPaidAmount);
////				System.out.println("dblPerkAlignTDSAmount====>"+dblPerkAlignTDSAmount);
//				dblGrossPaidAmount = dblGrossPaidAmount - dblPerkAlignTDSAmount;
////				System.out.println("2 dblGrossPaidAmount====>"+dblGrossPaidAmount);
//				
//				String strMonthsLeft = uF.dateDifference(strPaycycleStart, DATE_FORMAT, strFinancialYearEnd, DATE_FORMAT);
//				int nMonthsLeft = (int) Math.round(uF.parseToInt(strMonthsLeft) / 30);
//				
//				
//				
//				/**
//				 * 			ALL EXEMPTION WILL COME HERE
//				 * **/
//				double dblInvestment = uF.parseToDouble(hmEmpExemptionsMap.get(strEmpId));
//				double dblHomeLoanExemtion = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));
//				
//				
//				double dblEEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(EMPLOYEE_EPF+""));
//				double dblVOLEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(VOLUNTARY_EPF+""));
//				double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble(hmTotal.get(EMPLOYEE_EPF+""));
//				
////				double dblEREPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(EMPLOYER_EPF+""));
////				double dblEREPFToBePaid = nMonthsLeft * uF.parseToDouble(hmTotal.get(EMPLOYER_EPF+""));
//				
//				double dbl80CC_New = 0;
//				double dbl80CC_Old = uF.parseToDouble(hmEmpExemptionsMap.get(strEmpId+"_3")); 
//				dbl80CC_New = dbl80CC_Old +  dblEEEPFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;
//				
//				if(dbl80CC_New>=dblDeclaredInvestmentExemption) {
//					dbl80CC_New = dblDeclaredInvestmentExemption;
//				}
//				
//				double dblTotalInvestment = dblInvestment - dbl80CC_Old + dbl80CC_New;
//				
////				double dblTotalInvestment = dblInvestment + dblEEEPFPaid + dblEEEPFToBePaid+ dblEREPFPaid + dblEREPFToBePaid;
////				double dblTotalInvestment = dblInvestment + dblEREPFPaid + dblEREPFToBePaid;
////				double dblTotalInvestment = dblInvestment + dblEEEPFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;
//				
//				
//				/*if(dblTotalInvestment>=dblDeclaredInvestmentExemption) {
//					dblTotalInvestment = dblDeclaredInvestmentExemption;
//				}*/
//				
//				
//				double dblHRAExemptions = getHRAExemptionCalculation(con, uF,strD1, hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap);
//				
//				double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions; 
//				
//				
//				
//				
//				Set<String> set = hmSalaryDetails.keySet();
//				Iterator<String> it = set.iterator();
//				while(it.hasNext()) {
//					String strSalaryHeadId = it.next();
//					String strSalaryHeadName = hmSalaryDetails.get(strSalaryHeadId);
//					
//					
//					if(hmFixedExemptions.containsKey(strSalaryHeadId)) {
//						
//						double dblIndividualExemption = uF.parseToDouble(hmFixedExemptions.get(strSalaryHeadId));
//					
////					if(hmFixedExemptions.containsKey(strSalaryHeadName)) {
////						
////						double dblIndividualExemption = uF.parseToDouble(hmFixedExemptions.get(strSalaryHeadName));
//						
//						
////						System.out.println(" dblIndividualExemption=========+>"+dblIndividualExemption);
//						
//						double dblTotalToBePaid = 0;
//						if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) {
//							int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
//							double dblCurrentMonthGross = uF.parseToDouble(hmTotal.get("GROSS"));
//							dblTotalToBePaid = (nMonthsLeft-1) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
//							dblTotalToBePaid += calculateProfessionalTax(con, uF,strD2, dblCurrentMonthGross, strFinancialYearEnd, nLastPayMonth, strWLocationStateId);
//						} else {
//							dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
//						}
//						
//						double dblTotalPaid = uF.parseToDouble(hmPaidSalaryDetails.get(strSalaryHeadId));
//						double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;  
////						double dblExmp = 0;
//						if(dblTotalPaidAmount >= dblIndividualExemption) {
//							dblExemptions += dblIndividualExemption;
////							dblExmp = dblIndividualExemption;
//						} else {
//							dblExemptions += dblTotalPaidAmount;
////							dblExmp = dblTotalPaidAmount;
//						}
//						
//						
//						
//					}
//				}
//				
////				double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross); 
//				double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross) + uF.parseToDouble(""+hmEmpIncomeOtherSourcesMap.get(strEmpId)); 
////				System.out.println("dblGrossPaidAmount=====>"+dblGrossPaidAmount);
////				System.out.println("nMonthsLeft=====>"+nMonthsLeft);
////				System.out.println("dblGross=====>"+dblGross);
////				System.out.println("uF.parseToDouble(hmEmpIncomeOtherSourcesMap.get(strEmpId)=====>"+uF.parseToDouble(""+hmEmpIncomeOtherSourcesMap.get(strEmpId)));
////				System.out.println("dblTotalGrossSalary=====>"+dblTotalGrossSalary);
//				
//				log.debug("Invest Exmp="+hmEmpExemptionsMap.get(strEmpId));
////				log.debug("HRA Exemp="+getHRAExemptionCalculation(hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap));
//				
//				log.debug("strMonthsLeft="+strMonthsLeft);
//				log.debug("strPaycycleStart="+strPaycycleStart);
//				log.debug("strFinalcialYearEnd="+strFinancialYearEnd);
//				
//				log.debug("dblGrossPaidAmount="+dblGrossPaidAmount);
//				log.debug("to be paid="+(nMonthsLeft * dblGross));
//				log.debug("dblExemptions="+dblExemptions );
//				log.debug("dblTotalGrossSalary="+dblTotalGrossSalary );
//				log.debug("dblTotalGrossSalary - Exemp="+(dblTotalGrossSalary - dblExemptions) );
//				
//				
//				
//				
//				
//				double dblTotalTaxableSalary = 0;
//				if(dblTotalGrossSalary>dblExemptions) {
//					dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
//				}else if(dblTotalGrossSalary>0 && dblExemptions>0 && dblTotalGrossSalary<=dblExemptions) {
//					dblTotalTaxableSalary = 0;
//				}
//				
//				
//				
//				
//				int countBug = 0;
//				double dblTotalTDSPayable = 0.0d;
//				double dblUpperDeductionSlabLimit = 0;
//				double dblLowerDeductionSlabLimit = 0;
//				double dblTotalNetTaxableSalary = 0; 
//					
//				do{
//					
//					
//					
//					
//					pst = con.prepareStatement(selectDeduction);
//					pst.setDouble(1, uF.parseToDouble(strAge));
//					pst.setDouble(2, uF.parseToDouble(strAge));
//					pst.setString(3, strGender);
//					pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//					pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//					pst.setDouble(6, dblTotalTaxableSalary);
//					pst.setDouble(7, dblUpperDeductionSlabLimit);
////					System.out.println("pst=====>"+pst);  
//					rs = pst.executeQuery();
//					double dblDeductionAmount = 0;
//					if(rs.next()) {
//						dblDeductionAmount = rs.getDouble("deduction_amount");
////						strDeductionType = rs.getString("deduction_type");
//						dblUpperDeductionSlabLimit = rs.getDouble("_to");
//						dblLowerDeductionSlabLimit = rs.getDouble("_from");
//					}
//					rs.close();
//					pst.close();
//					
//					if(countBug==0) {
//						dblTotalNetTaxableSalary = dblTotalTaxableSalary;
//					}
//					
//					if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit) {
//						dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
//					} else {
//						if(countBug==0) {
//							dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
//						}
//						
//						dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
//					}
//					
//					dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;
//
//					if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
//					countBug++;
//					
//				}while(dblTotalNetTaxableSalary>0);
//				
//				
//				// Service tax + Education cess
//			
//				
//				/**
//				 * @autor Vipin
//				 * Date: 25-Mar-2014
//				 * 87A Section for AY 2014-2015
//				 * */
////				double dblRebate = 0;
////				if(dblTotalTaxableSalary<500000) {
////					if(dblTotalTDSPayable>=2000) {
////						dblRebate = 2000;
////					}else if(dblTotalTDSPayable>0 && dblTotalTDSPayable<2000) {
////						dblRebate = dblTotalTDSPayable;
////					}
////				}
//				double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_MAX_TAX_INCOME"));
//				double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_REBATE_AMOUNT"));
//				double dblRebate = 0;
//				if(dblTotalTaxableSalary <= dblMaxTaxableIncome && dblTotalTDSPayable <= dblMaxTaxableIncome) {
//					if(dblTotalTDSPayable>=dblRebateAmt) {
//						dblRebate = dblRebateAmt;
//					}else if(dblTotalTDSPayable > 0 && dblTotalTDSPayable < dblRebateAmt) {
//						dblRebate = dblTotalTDSPayable;
//					}
//				}
//				
//				dblTotalTDSPayable = dblTotalTDSPayable - dblRebate;
//				
//				
////				System.out.println("dblTotalTDSPayable=======>"+dblTotalTDSPayable);
////				System.out.println("dblCess1=======>"+dblCess1);
////				System.out.println("dblCess2=======>"+dblCess2);
//				
//				double dblCess = dblTotalTDSPayable * ( dblCess1/100);
//				dblCess += dblTotalTDSPayable * ( dblCess2/100);
//				
//				dblTotalTDSPayable += dblCess;   
//				
//				if(strEmpId.equalsIgnoreCase("618")) {
////					System.out.println("strEmpId===="+strEmpId);
////					System.out.println("dblTotalTDSPayable===="+dblTotalTDSPayable);
////					System.out.println("dblTDSPaidAmount===="+dblTDSPaidAmount);
////					System.out.println("nMonthsLeft===="+nMonthsLeft);
////					System.out.println("count===="+count);
//				}	
//				
//				dblTDSMonth = dblTotalTDSPayable - dblTDSPaidAmount;
//				dblTDSMonth = dblTDSMonth/(nMonthsLeft);
//				
//				if(dblTDSMonth<0) {
//					dblTDSMonth = 0;
//				}
//				
//			
//				
////				System.out.println("dblTotalTDSPayable=======>"+dblTotalTDSPayable);
////				System.out.println("dblTDSPaidAmount=======>"+dblTDSPaidAmount);
////				System.out.println("dblTDSMonth=======>"+dblTDSMonth);
//				
//				
////				log.debug("dblExemptions="+dblExemptions);
////				log.debug("dblTotalGrossSalary="+dblTotalTaxableSalary);
////				log.debug("dblTotalTDSPayable="+dblTotalTDSPayable);
////				log.debug("dblTotalTDSPayable="+dblTotalTDSPayable);
////				log.debug("dblTDSPaidAmount="+dblTDSPaidAmount);
////				log.debug("dblTDSMonth="+dblTDSMonth);
////				log.debug("============="+strEmpId+"=================");
//				
//			}
//			
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			if(rs !=null) {
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			if(pst !=null) {
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return dblTDSMonth;
//	}
	
	public double calculateTDS(Connection con, CommonFunctions CF, UtilityFunctions uF, double dblGross, double dblFlatTDS, int nPayMonth, 
			String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, Map<String, String> hmEmpLevelMap) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblTDSMonth = 0;
		try {
			pst = con.prepareStatement("select * from tds_projections where emp_id =? and month=? and fy_year_from=? and fy_year_end=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, nPayMonth);
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				dblTDSMonth = rs.getDouble("amount");
				return dblTDSMonth;
			}
			rs.close();
			pst.close();

			if (uF.parseToBoolean(hmEmpLevelMap.get(strEmpId + "_FLAT_TDS_DEDEC"))) {
				dblTDSMonth = dblGross * dblFlatTDS / 100;
			} else {
				if(CF.getIsTDSAutoApprove()) {
					ViewEmpTDSProjection empTDSProjection = new ViewEmpTDSProjection();
					empTDSProjection.request = request;
					empTDSProjection.session = session;
					empTDSProjection.CF = CF;
					empTDSProjection.setStrEmpId(strEmpId);
					empTDSProjection.setStrFinancialYearStart(strFinancialYearStart);
					empTDSProjection.setStrFinancialYearEnd(strFinancialYearEnd);
					empTDSProjection.getEmpTDSProjection(uF);
					
					Map<String, String> hmTDSRemainMonth = (Map<String, String>)request.getAttribute("hmTDSRemainMonth");
					if(hmTDSRemainMonth == null) hmTDSRemainMonth = new LinkedHashMap<String, String>();
					
					String strMonth = uF.getMonth(nPayMonth);
	//				System.out.println("strMonth==>"+strMonth+"--hmTDSRemainMonth==>"+hmTDSRemainMonth);
					if(hmTDSRemainMonth != null && !hmTDSRemainMonth.isEmpty() && hmTDSRemainMonth.size() > 0 && hmTDSRemainMonth.get(strMonth) != null) {
						String strRemainTDS = hmTDSRemainMonth.get(strMonth);
						dblTDSMonth = Math.round(uF.parseToDouble(strRemainTDS));
					} else {
						dblTDSMonth = 0.0d;
					}
				} else {
					dblTDSMonth = 0.0d;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblTDSMonth;
	}
	
	public double calculateCandidateTDS(Connection con, UtilityFunctions uF, String strD2, String strD1, double dblGross, double dblCess1, double dblCess2,
			double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblHraSalHeadsAmount, int nPayMonth, String strPaycycleStart,
			String strFinancialYearStart, String strFinancialYearEnd, int candId, String strGender, double dblCandAge, String strStateId,
			Map<String, String> hmFixedExemptions, boolean isMetro, Map<String, String> hmTotal, Map<String, String> hmSalaryDetails, int nLevelId,
			CommonFunctions CF, Map<String, String> hmOtherTaxDetails, int nEmpOrgId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblTDSMonth = 0;
		
		try { 
			String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, candId+"", strFinancialYearStart, strFinancialYearEnd);
			int slabType = uF.parseToInt(strSlabType);
			
			pst = con.prepareStatement("select * from level_details where level_id = ?");
			pst.setInt(1, nLevelId);
			rs = pst.executeQuery();
			boolean isFlatTDS = false;
			if(rs.next()) {
				isFlatTDS = uF.parseToBoolean(rs.getString("flat_deduction"));
			}
			rs.close();
			pst.close();
			
			if(isFlatTDS) {
				dblTDSMonth = dblGross * dblFlatTDS / 100;
			} else {
				
				String strMonthsLeft = uF.dateDifference(strPaycycleStart, DATE_FORMAT, strFinancialYearEnd, DATE_FORMAT,CF.getStrTimeZone());
				int nMonthsLeft = (int) Math.round(uF.parseToInt(strMonthsLeft) / 30);
				
				/**
				 * 			ALL EXEMPTION WILL COME HERE
				 * **/
				double dblInvestment = 0.0d;
				double dblHomeLoanExemtion = 0.0d;
				
				
				double dblEEEPFPaid = 0.0d;
				double dblVOLEEPFPaid =0.0d;
				double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble(hmTotal.get(EMPLOYEE_EPF+""));
				
				double dbl80CC_New = 0;
				double dbl80CC_Old = 0.0d; 
				dbl80CC_New = dbl80CC_Old +  dblEEEPFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;
				
				if(dbl80CC_New>=dblDeclaredInvestmentExemption) {
					dbl80CC_New = dblDeclaredInvestmentExemption;
				}
				
				double dblTotalInvestment = dblInvestment - dbl80CC_Old + dbl80CC_New;
				
				double dblHRAExemptions = 0.0d;
				
				double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions; 
				
				Set<String> set = hmSalaryDetails.keySet();
				Iterator<String> it = set.iterator();
				while(it.hasNext()) {
					String strSalaryHeadId = it.next();
					String strSalaryHeadName = hmSalaryDetails.get(strSalaryHeadId);
					
					if(hmFixedExemptions.containsKey(strSalaryHeadId)) {
						
						double dblIndividualExemption = uF.parseToDouble(hmFixedExemptions.get(strSalaryHeadId));
					
						double dblTotalToBePaid = 0;
						if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) {
							int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
							double dblCurrentMonthGross = uF.parseToDouble(hmTotal.get("GROSS"));
							dblTotalToBePaid = (nMonthsLeft-1) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
							dblTotalToBePaid += calculateProfessionalTax(con, uF,strD2, dblCurrentMonthGross, strFinancialYearStart, strFinancialYearEnd, nLastPayMonth, strStateId, strGender);
						} else {
							dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
						}
						
						double dblTotalPaid = 0.0d;
						double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;  
						if(dblTotalPaidAmount >= dblIndividualExemption) {
							dblExemptions += dblIndividualExemption;
						} else {
							dblExemptions += dblTotalPaidAmount;
						}
					}
				}
				
				double dblTotalGrossSalary = ((nMonthsLeft) * dblGross); 
				double dblTotalTaxableSalary = 0;
				if(dblTotalGrossSalary>dblExemptions) {
					dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
				} else if(dblTotalGrossSalary>0 && dblExemptions>0 && dblTotalGrossSalary<=dblExemptions) {
					dblTotalTaxableSalary = 0;
				}
				
				int countBug = 0;
				double dblTotalTDSPayable = 0.0d;
				double dblUpperDeductionSlabLimit = 0;
				double dblLowerDeductionSlabLimit = 0;
				double dblTotalNetTaxableSalary = 0; 
					
				do {
					pst = con.prepareStatement(selectDeduction);
					pst.setDouble(1, dblCandAge);
					pst.setDouble(2, dblCandAge);
					pst.setString(3, strGender);
					pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDouble(6, dblTotalTaxableSalary);
					pst.setDouble(7, dblUpperDeductionSlabLimit);
					pst.setInt(8, slabType);
//					System.out.println("pst=====>"+pst);  
					rs = pst.executeQuery();
					double dblDeductionAmount = 0;
					if(rs.next()) {
						dblDeductionAmount = rs.getDouble("deduction_amount");
//						strDeductionType = rs.getString("deduction_type");
						dblUpperDeductionSlabLimit = rs.getDouble("_to");
						dblLowerDeductionSlabLimit = rs.getDouble("_from");
					}
					rs.close();
					pst.close();
					
					if(countBug==0) {
						dblTotalNetTaxableSalary = dblTotalTaxableSalary;
					}
					
					if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit) {
						dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
					} else {
						if(countBug==0) {
							dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
						}
						
						dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
					}
					
					dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;

					if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
					countBug++;
					
				}while(dblTotalNetTaxableSalary>0);
				
				
				// Service tax + Education cess
			
				
				/**
				 * @autor Vipin
				 * Date: 25-Mar-2014
				 * 87A Section for AY 2014-2015
				 * */
				double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_MAX_TAX_INCOME"));
				double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_REBATE_AMOUNT"));
				double dblRebate = 0;
				if(dblTotalTaxableSalary <= dblMaxTaxableIncome && dblTotalTDSPayable <= dblMaxTaxableIncome) {
					if(dblTotalTDSPayable>=dblRebateAmt) {
						dblRebate = dblRebateAmt;
					}else if(dblTotalTDSPayable > 0 && dblTotalTDSPayable < dblRebateAmt) {
						dblRebate = dblTotalTDSPayable;
					}
				}
				
				dblTotalTDSPayable = dblTotalTDSPayable - dblRebate;
				
				double dblCess = dblTotalTDSPayable * ( dblCess1/100);
				dblCess += dblTotalTDSPayable * ( dblCess2/100);
				
				dblTotalTDSPayable += dblCess;   
				
				dblTDSMonth = dblTotalTDSPayable;
				dblTDSMonth = dblTDSMonth/(nMonthsLeft);
				
				if(dblTDSMonth<0) {
					dblTDSMonth = 0;
				}
			}
		} catch (Exception e) {
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
		return dblTDSMonth;
	} 

	
	public double calculateTDSA(Connection con, UtilityFunctions uF,String strD1,String strD2, double dblGross, double dblCess1, double dblCess2, double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblBasicDA,
		int nPayMonth, String strPaycycleStart, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge, String strWLocationStateId,
		Map<String,String> hmEmpExemptionsMap, Map<String,String> hmEmpHomeLoanMap, Map<String,String> hmFixedExemptions, Map<String,String> hmEmpMertoMap, Map<String,String> hmEmpRentPaidMap, Map<String,String> hmPaidSalaryDetails,
		Map<String,String> hmTotal, Map<String,String> hmSalaryDetails, Map<String,String> hmEmpLevelMap, CommonFunctions CF, int nMonthsLeft,Map<String,String> hmEmpIncomeOtherSourcesMap,
		Map<String, String> hmOtherTaxDetails, Map<String, String> hmEmpStateMap) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblTDSMonth = 0;
		
		
		try {
			
			if(uF.parseToBoolean(hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))) {
				dblTDSMonth = dblGross * dblFlatTDS / 100;
//				dblTDSMonth += (dblCess1 * 0.01 * dblTDSMonth) + (dblCess2 * 0.01 * dblTDSMonth); 
				
			} else {
				
				String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
				int slabType = uF.parseToInt(strSlabType);
				
				String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
				
				pst = con.prepareStatement(selectTDS);
				pst.setInt(1, TDS);
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				double dblTDSPaidAmount = 0;
				while(rs.next()) {
					dblTDSPaidAmount = uF.parseToDouble(rs.getString("tds"));
				}
				rs.close();
				pst.close();
				
//				double dblTDSPaidAmount1 = 0;
				int count = 0; 
				
				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ? and salary_head_id not in ("+REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+ ","+CGST+","+SGST+")");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				double dblGrossPaidAmount = 0;
				while(rs.next()) {
					dblGrossPaidAmount = rs.getDouble("amount");
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmReimbursementAmt = new HashMap<String, String>();
				if(CF.getIsReceipt()) {
					int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
					Map<String, String> hmOrg = CF.getOrgDetails(con, uF, ""+nEmpOrgId);
					String[] firstArr = CF.getPayCycleFromDate(con, strFinancialYearStart, CF.getStrTimeZone(), CF, ""+nEmpOrgId);
					String[] secondArr = null;
					if(uF.parseToInt(uF.getDateFormat(hmOrg.get("ORG_START_PAYCYCLE"), DATE_FORMAT, "dd")) > 1) {
						secondArr = CF.getPrevPayCycleByOrg(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, ""+nEmpOrgId);
					} else {
						secondArr = CF.getPayCycleFromDate(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, ""+nEmpOrgId);
					}
					pst = con.prepareStatement("select emp_id,sum(reimbursement_amount) as reimbursement_amount " +
							"from emp_reimbursement where approval_1 =1 and ispaid=true and (ref_document is null or ref_document='' " +
							"or upper(ref_document) ='NULL') and from_date>=? and to_date<=? and emp_id in ("+strEmpId+") group by emp_id");
					pst.setDate(1, uF.getDateFormat(firstArr[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(secondArr[1], DATE_FORMAT));
					rs = pst.executeQuery();
//					System.out.println("pst====>"+pst);
					while(rs.next()) {
						hmReimbursementAmt.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("reimbursement_amount")));
					}
					rs.close();
					pst.close();
				}
				
				dblGrossPaidAmount+= uF.parseToDouble(hmReimbursementAmt.get(strEmpId));
				
				/**
				 * 			ALL EXEMPTION WILL COME HERE
				 * **/
				double dblInvestment = uF.parseToDouble(hmEmpExemptionsMap.get(strEmpId));
				double dblHomeLoanExemtion = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));
				
				
				double dblEEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(EMPLOYEE_EPF+""));
				double dblVOLEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(VOLUNTARY_EPF+""));
				double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble(hmTotal.get(EMPLOYEE_EPF+""));
				
				double dbl80CC_New = 0;
				double dbl80CC_Old = uF.parseToDouble(hmEmpExemptionsMap.get(strEmpId+"_3")); 
				dbl80CC_New = dbl80CC_Old +  dblEEEPFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;
				
				if(dbl80CC_New>=dblDeclaredInvestmentExemption) {
					dbl80CC_New = dblDeclaredInvestmentExemption;
				}
				
				double dblTotalInvestment = dblInvestment - dbl80CC_Old + dbl80CC_New;
				double dblHRAExemptions = getHRAExemptionCalculation(con, uF,strD1, hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap);
				double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions; 
				
				Set<String> set = hmSalaryDetails.keySet();
				Iterator<String> it = set.iterator();
				while(it.hasNext()) {
					String strSalaryHeadId = it.next();
					String strSalaryHeadName = hmSalaryDetails.get(strSalaryHeadId);
					
					if(hmFixedExemptions.containsKey(strSalaryHeadId)) {
						double dblIndividualExemption = uF.parseToDouble(hmFixedExemptions.get(strSalaryHeadId));
					
//					if(hmFixedExemptions.containsKey(strSalaryHeadName)) {
//						
//						double dblIndividualExemption = uF.parseToDouble(hmFixedExemptions.get(strSalaryHeadName));
						
						double dblTotalToBePaid = 0;
						if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) {
							int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
							double dblCurrentMonthGross = uF.parseToDouble(hmTotal.get("GROSS"));
							dblTotalToBePaid = (nMonthsLeft-1) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
							dblTotalToBePaid += calculateProfessionalTax(con, uF,strD2, dblCurrentMonthGross, strFinancialYearStart, strFinancialYearEnd, nLastPayMonth, strWLocationStateId, strEmpGender);
						} else {
							dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
						}
						
						double dblTotalPaid = uF.parseToDouble(hmPaidSalaryDetails.get(strSalaryHeadId));
						double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;  
//						double dblExmp = 0;
						if(dblTotalPaidAmount >= dblIndividualExemption) {
							dblExemptions += dblIndividualExemption;
//							dblExmp = dblIndividualExemption;
						} else {
							dblExemptions += dblTotalPaidAmount;
//							dblExmp = dblTotalPaidAmount;
						}
						
					}
				}
				
				double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross) + uF.parseToDouble(""+hmEmpIncomeOtherSourcesMap.get(strEmpId));
				
				double dblTotalTaxableSalary = 0;
				if(dblTotalGrossSalary>dblExemptions) {
					dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
				}else if(dblTotalGrossSalary>0 && dblExemptions>0 && dblTotalGrossSalary<=dblExemptions) {
					dblTotalTaxableSalary = 0;
				}
				
				int countBug = 0;
				double dblTotalTDSPayable = 0.0d;
				double dblUpperDeductionSlabLimit = 0;
				double dblLowerDeductionSlabLimit = 0;
				double dblTotalNetTaxableSalary = 0; 
					
				do{
					pst = con.prepareStatement(selectDeduction);
					pst.setDouble(1, uF.parseToDouble(strAge));
					pst.setDouble(2, uF.parseToDouble(strAge));
					pst.setString(3, strGender);
					pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setDouble(6, dblTotalTaxableSalary);
					pst.setDouble(7, dblUpperDeductionSlabLimit);
					pst.setInt(8, slabType);
					rs = pst.executeQuery();
//					System.out.println("pst=====>"+pst);  
					double dblDeductionAmount = 0;
//					String strDeductionType = null;
					if(rs.next()) {
						dblDeductionAmount = rs.getDouble("deduction_amount");
//						strDeductionType = rs.getString("deduction_type");
						dblUpperDeductionSlabLimit = rs.getDouble("_to");
						dblLowerDeductionSlabLimit = rs.getDouble("_from");
					}
					rs.close();
					pst.close();
					
					if(countBug==0) {
						dblTotalNetTaxableSalary = dblTotalTaxableSalary;
					}
					
					if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit) {
						dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
					} else {
						if(countBug==0) {
							dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
						}
						dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
					}
					
					dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;

					if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
					countBug++;
					
				}while(dblTotalNetTaxableSalary>0);
				
				// Service tax + Education cess
				
				/**
				 * @autor Vipin
				 * Date: 25-Mar-2014
				 * 87A Section for AY 2014-2015
				 * */
//				double dblRebate = 0;
//				if(dblTotalTaxableSalary<500000) {
//					if(dblTotalTDSPayable>=2000) {
//						dblRebate = 2000;
//					}else if(dblTotalTDSPayable>0 && dblTotalTDSPayable<2000) {
//						dblRebate = dblTotalTDSPayable;
//					}
//				}
				double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_MAX_TAX_INCOME"));
				double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_REBATE_AMOUNT"));
				double dblRebate = 0;
				if(dblTotalTaxableSalary <= dblMaxTaxableIncome && dblTotalTDSPayable <= dblMaxTaxableIncome) {
					if(dblTotalTDSPayable>=dblRebateAmt) {
						dblRebate = dblRebateAmt;
					}else if(dblTotalTDSPayable > 0 && dblTotalTDSPayable < dblRebateAmt) {
						dblRebate = dblTotalTDSPayable;
					}
				}
				
				dblTotalTDSPayable = dblTotalTDSPayable - dblRebate;
				
				
				double dblCess = dblTotalTDSPayable * ( dblCess1/100);
				dblCess += dblTotalTDSPayable * ( dblCess2/100);
				
				dblTotalTDSPayable += dblCess;   
				
				dblTDSMonth = dblTotalTDSPayable - dblTDSPaidAmount;
				if((nMonthsLeft - count)>0) {
					dblTDSMonth = dblTDSMonth/(nMonthsLeft - count);
//				} else {
//					dblTDSMonth = dblTDSMonth;
				}
				if(dblTDSMonth<0) {
					dblTDSMonth = 0;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblTDSMonth;
	}
	
	
	public Map<String, String> getEmpInvestmentExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, double dblDeclaredInvestmentExemption) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmEmpExemptionsMap = new HashMap<String,String>();
		
		try {
			
			Map<String,String> hmSectionLimitA = new HashMap<String,String>();
			Map<String,String> hmSectionLimitP = new HashMap<String,String>();
			
//			Map hmSectionLimitEmp = new HashMap();
			
			//pst = con.prepareStatement(selectSection);
//			pst = con.prepareStatement("SELECT * FROM section_details where isdisplay=true order by section_code");
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				if(rs.getString("section_limit_type").equalsIgnoreCase("A")) {
					hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				} else {
					hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				}
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 " +
					"and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and sd.section_id !=11 " +
					"and isdisplay=true and parent_section=0 group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			double dblInvestmentLimit = 0;
			double dblInvestmentEmp = 0;
//			String strEmpIdNew = null;
//			String strEmpIdOld = null;
			while (rs.next()) {
//				double dblInvestment = rs.getDouble("amount_paid");
//				if(dblInvestment>=dblDeclaredInvestmentExemption) {
//					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblDeclaredInvestmentExemption+"");
//				} else {
//					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestment+"");
//				}
				
				
//				strEmpIdNew = rs.getString("emp_id");
				
				/*if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					dblInvestmentEmp = 0;
				}
				*/
				
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				
				
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble(hmSectionLimitA.get(strSectionId));
				} else {
					dblInvestmentLimit = uF.parseToDouble(hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}
				
//				if(uF.parseToInt(strSectionId)==3) {
//					hmEmpExemptionsMap.put(rs.getString("emp_id")+"_"+strSectionId, dblInvestment+"");
//				}
				
				if(dblInvestment>=dblInvestmentLimit) {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					
					if(uF.parseToInt(strSectionId)==3) {
						hmEmpExemptionsMap.put(rs.getString("emp_id")+"_"+strSectionId, dblInvestmentLimit+"");
					}
				} else {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					if(uF.parseToInt(strSectionId)==3) {
						hmEmpExemptionsMap.put(rs.getString("emp_id")+"_"+strSectionId, dblInvestment+"");
					}
				}
				
			}
			rs.close();
			pst.close();
			
			
			
//			System.out.println("hmEmpExemptionsMap=="+hmEmpExemptionsMap);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return hmEmpExemptionsMap;
	
	}
	
	
	public Map<String, String> getEmpHomeLoanExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmEmpHomeLoanMap = new HashMap<String,String>();
		
		try {
			
//			pst = con.prepareStatement("select * from section_details where section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true");
			pst = con.prepareStatement("select * from section_details where section_id = 11 and financial_year_start=? and financial_year_end=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			double dblLoanExemptionLimit = 0;
			while (rs.next()) {
				dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
			}
			rs.close();
			pst.close();
			
			
			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? and status = true and  section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%' and isdisplay=true)  group by emp_id");
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? " +
//					"and status = true and  section_id = 11  group by emp_id");
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
							" and trail_status = 1 and parent_section=0 and  section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
							"and financial_year_end=?) group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit) {
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
				} else {
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
				}
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return hmEmpHomeLoanMap;
	
	}
	
	public Map<String, String> getEmpRentPaid(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmEmpRentPaidMap = new HashMap<String,String>();
		
		try {
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and agreed_date between ? and ? and section_code in ('HRA') and isdisplay=true group by emp_id ");
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd  " +
//					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and agreed_date between ? and ? a" +
//					"nd section_code in ('HRA') and isdisplay=true and status=true group by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			
//			rs = pst.executeQuery();          
//			while (rs.next()) {
//				hmEmpRentPaidMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
//			}
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, exemption_details ed " +
					"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to=? and status = true and ed.salary_head_id=? " +
					"and trail_status = 1 and ed.exemption_from=? and ed.exemption_to=? and parent_section = 0 group by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, HRA);
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();          
			while (rs.next()) {
				hmEmpRentPaidMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return hmEmpRentPaidMap;
	
	}
	
	
//	public Map getPaidSalary(String strFinancialYearStart, String strFinancialYearEnd) {
//		
//		Connection con=null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		Map<String,Map<String,String>> hmEmpPaidSalary = new HashMap<String,Map<String,String>>();
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from payroll_generation where paycycle =? and financial_year_from_date=? and financial_year_to_date =? order by emp_id");
//			pst.setInt(1, uF.parseToInt(strPC));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			
//			rs = pst.executeQuery();
//			
//			String strEmpIdNew=null;
//			String strEmpIdOld=null;
//			
//			Map hmInner = new HashMap();
//			while (rs.next()) {
//				
//				strEmpIdNew = rs.getString("emp_id");
//			
//				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
//					hmInner = new HashMap();
//				}
//				
//				hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
//			
//				hmEmpPaidSalary.put(rs.getString("emp_id"), hmInner);
//				
//				strEmpIdOld = strEmpIdNew;
//				
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//		return hmEmpPaidSalary;
//	
//	}
	
	
	
	
	/*public Map getHRAPaid(String strFinancialYearStart, String strFinancialYearEnd) {
		
		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		Map<String, String> hmEmpPaidHRA = new HashMap<String, String>();
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select sum(amount) as hra, emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date =? group by emp_id");
			pst.setInt(1, HRA);
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				hmEmpPaidHRA.put(rs.getString("emp_id"), rs.getString("hra"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rs);
		}
		return hmEmpPaidHRA;
	
	}
	
	public Map getBasicPaid(String strFinancialYearStart, String strFinancialYearEnd) {
		
		Connection con=null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		Map<String, String> hmEmpPaidBasic = new HashMap<String, String>();
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select sum(amount) as basic, emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date =? group by emp_id");
			pst.setInt(1, BASIC);
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				hmEmpPaidBasic.put(rs.getString("emp_id"), rs.getString("basic"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rs);
		}
		return hmEmpPaidBasic;
	
	}*/
	
	
	public Map<String, Map<String, String>> getEmpPaidAmountDetails(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmEmpPaidAmountDetails = new HashMap<String, Map<String, String>>();
		
		try {
			pst = con.prepareStatement("select sum(amount) as amount, emp_id, salary_head_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ? group by emp_id, salary_head_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			
			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			Map<String,String> hmInner = new HashMap<String,String>();
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				
				
				if(strEmpIdNew !=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hmInner = new HashMap<String,String>();
				}
				hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
				
				hmEmpPaidAmountDetails.put(rs.getString("emp_id"), hmInner);
				
				strEmpIdOld  = strEmpIdNew;
				
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return hmEmpPaidAmountDetails;
	
	}
	
	
	
	
	public double getHRAExemptionCalculation(Connection con, UtilityFunctions uF,String strD1, Map<String,String> hmPaidSalaryDetails, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, double dblHRA, double dblBasicDA, Map<String,String> hmEmpMertoMap, Map<String,String> hmEmpRentPaidMap) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblHRAExemption = 0;
		
		try {
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from = ? and financial_year_to =? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			
			double dblCondition1= 0;
			double dblCondition2= 0;
			double dblCondition3= 0;
			String strHraSalHeads = null;
			while (rs.next()) {
				dblCondition1= rs.getDouble("condition1");
				dblCondition2= rs.getDouble("condition2");
				dblCondition3= rs.getDouble("condition3");
				strHraSalHeads= rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();
			
			String[] hraSalaryHeads = null;
			if(strHraSalHeads!=null) {
				hraSalaryHeads = strHraSalHeads.split(",");
			}
			
			double dblHraSalHeadsAmount = 0;
			for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
				dblHraSalHeadsAmount += uF.parseToDouble((String)hmPaidSalaryDetails.get(hraSalaryHeads[i]));
			}
			
			boolean isMetro = uF.parseToBoolean(hmEmpMertoMap.get(strEmpId));
			
//			String strBasicPaidAmount = hmPaidSalaryDetails.get(BASIC+"");
			String strHRAPaidAmount = hmPaidSalaryDetails.get(HRA+"");
			
			String strMonthsLeft = uF.dateDifference(strD1, DATE_FORMAT, strFinancialYearEnd, DATE_FORMAT,CF.getStrTimeZone());
			int nMonthsLeft = uF.parseToInt(strMonthsLeft) / 30;
//			System.out.println("nMonthsLeft=="+nMonthsLeft);
			
			double dblBasicToBePaidAmount = nMonthsLeft * dblBasicDA;
			double dblHRAToBePaidAmount = nMonthsLeft * dblHRA;
			   
//			System.out.println("dblHraSalHeadsAmount ========>> " + dblHraSalHeadsAmount);
//			double dblTotalBasicDAAmount = uF.parseToDouble(strBasicPaidAmount) + dblBasicToBePaidAmount;
			double dblTotalBasicDAAmount = dblHraSalHeadsAmount + dblBasicToBePaidAmount;
			double dblTotalHRAAmount = uF.parseToDouble(strHRAPaidAmount) + dblHRAToBePaidAmount;
			
			double dblTotalRentPaid = uF.parseToDouble(hmEmpRentPaidMap.get(strEmpId));
			
//			double dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
			double dblRentPaidGreaterThanCondition1 = 0;

//			System.out.println("dblCondition1 ===>> " + dblCondition1);
//			System.out.println("dblCondition2 ===>> " + dblCondition2);
//			System.out.println("dblCondition3 ===>> " + dblCondition3);
//			System.out.println("dblTotalBasicDAAmount ===>> " + dblTotalBasicDAAmount + " -- dblTotalHRAAmount ===>> " + dblTotalHRAAmount + " -- dblTotalRentPaid ===>> " + dblTotalRentPaid);
//			System.out.println("dblRentPaidGreaterThanCondition1 ===>> " + dblRentPaidGreaterThanCondition1);
			
			if(dblTotalRentPaid>dblRentPaidGreaterThanCondition1) {
				
				dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
				
				dblRentPaidGreaterThanCondition1 = dblTotalRentPaid - dblRentPaidGreaterThanCondition1;
				
			} else if(dblTotalRentPaid>0) {
				dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
			}
			
			double dblRentPaidCondition23 = 0;
			
			if(isMetro) {
				dblRentPaidCondition23 = dblCondition2 * dblTotalBasicDAAmount /100;
			} else {
				dblRentPaidCondition23 = dblCondition3 * dblTotalBasicDAAmount /100;
			}
			
			dblHRAExemption = Math.min(dblTotalHRAAmount, dblRentPaidGreaterThanCondition1);
//			System.out.println("dblHRAExemption ===>> " + dblHRAExemption + " --- dblRentPaidCondition23 ===>> " + dblRentPaidCondition23);
			dblHRAExemption = Math.min(dblHRAExemption, dblRentPaidCondition23);
//			System.out.println("dblHRAExemption ===>> " + dblHRAExemption);
			if(dblTotalRentPaid>0) {
				dblHRAExemption = Math.min(dblHRAExemption, dblTotalRentPaid);
			}
//			System.out.println("dblHRAExemption 1 ===>> " + dblHRAExemption);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblHRAExemption;
	
	}
	
	public Map<String, String> getFixedExemption(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmFixedExemptions = new HashMap<String,String>();
		
		try {
			
			pst = con.prepareStatement("select * from exemption_details where exemption_from = ? and exemption_to =? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			
//			double dblCondition1= 0;   
//			double dblCondition2= 0;
//			double dblCondition3= 0;
			
			while (rs.next()) {
//				hmFixedExemptions.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
				hmFixedExemptions.put(rs.getString("salary_head_id"), rs.getString("exemption_limit"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return hmFixedExemptions;
	
	}
	
	
	public double getOverTimeCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List<String> alServices, List<String> alPresentDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, Map<String, Map<String, String>> hmOverTimeMap, String strLevelId, double dblStandardHours, Map<String, String> hmHolidays) {
		
		double dblOverTime = 0.0d;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map hmFixedExemptions = new HashMap();
		
		try {
			
					
			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
//			String strLevelId = hmEmpLevelMap.get(strEmpId);
			String strOverTimeType = null;
			
			for(int j=0; j<alPresentDays.size(); j++) {
				String strDate = alPresentDays.get(j);
				
				
				log.debug("hmHolidays===>"+hmHolidays);
				log.debug("strDate===>"+strDate);
				log.debug("strOverTimeType===>"+strOverTimeType);
				
				if(hmHolidays!=null && hmHolidays.containsKey(strDate)) {
					strOverTimeType = "PH";
				} else {
					strOverTimeType = "EH";
				}
				
				
				
//				System.out.println("hmHolidays="+hmHolidays);
//				System.out.println("strOverTimeType="+strOverTimeType);
//				System.out.println("hmOverTimeMap="+hmOverTimeMap);
				
				Map<String,String> hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
				if(hmTemp==null) hmTemp=new HashMap<String,String>();
				
				String strSalarySubHead = hmTemp.get("SALARY_HEAD_ID");
				String strPaymentType = hmTemp.get("PAYMENT_TYPE");
				double dblAmount = uF.parseToDouble(hmTemp.get("PAYMENT_AMOUNT"));
				
				Map<String,String> hmSubSalaryDetails = hmCalculatedSalary.get(strSalarySubHead);
				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
				double dblSubSalaryAmount = uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));
				
				
				
				Map<String,String> hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(strSalarySubHead);
				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap<String,String>();
//				double dblSubSalaryAmountActualCTC = uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));
				
				
				
				
				
				
				
				
				for(int k=0; k<alServices.size(); k++) {
					String strService = alServices.get(k);
					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));

					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")) {
						
						/**
						 *  IF condition is for additional hours worked during working days
						 * 
						 * **/
						
						
						
						
//						System.out.println("OT dblOverTime D ==>"+dblOverTime);
						
						if(hrsWorked > dblStandardHours) {
							dblOTHoursWorked = (hrsWorked - dblStandardHours);
							
							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
								dblOverTime += (dblAmount * dblSubSalaryAmount * dblOTHoursWorked)/ (dblStandardHours * 100);
							}else if(strPaymentType!=null) {
								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
							}
							
						}
						
						
						/*if(strEmpId!=null && strEmpId.equalsIgnoreCase("718")) {
							System.out.println("OT strDate==>"+strDate);
							System.out.println("OT dblOverTime==>"+dblOverTime);
							System.out.println("OT strPaymentType==>"+strPaymentType);
							
							System.out.println("OT dblAmount==>"+dblAmount);
							System.out.println("OT dblSubSalaryAmount==>"+dblSubSalaryAmount+" ActualCTC="+dblSubSalaryAmountActualCTC);
							System.out.println("OT dblOTHoursWorked==>"+dblOTHoursWorked);
							System.out.println("OT dblStandardHours==>"+dblStandardHours);
							
							System.out.println("OT hrsWorked==>"+hrsWorked+" dblStandardHours="+dblStandardHours);
							System.out.println("OT hmTemp==>"+hmTemp);
						}
						*/
					} else {
						
						/**
						 *  Else condition is for pubic holidays
						 * 
						 * **/
				
						
						
						
						
						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
							
							
//							System.out.println("====== A =======");
//							
//							System.out.println("dblSubSalaryAmount====="+dblSubSalaryAmount);
//							System.out.println("dblAmount====="+dblAmount);
//							System.out.println("dblOverTime====="+dblOverTime);
							
						}else if(strPaymentType!=null) {
							dblOverTime += dblAmount ;
						}
						
					}
					
					log.debug(strDate+"_"+strService+"===>"+hmHoursWorked.get(strDate+"_"+strService)); 
					
				}
			}
			
			log.debug("dblOTHoursWorked===>"+dblOTHoursWorked);
			
			/*
			if(strEmpId!=null && strEmpId.equalsIgnoreCase("718")) {
				System.out.println("dblOTHoursWorked====="+dblOTHoursWorked);
				System.out.println("dblOverTime====="+dblOverTime);
			}
			*/  
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblOverTime;
	}
	
	public double getOverTimeCalculationL(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List<String> alServices, List<String> alPresentDays, List<String> alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, Map<String, Map<String, String>> hmOverTimeMap, Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays, int nTotalNumberOfDaysForCalc, Map<String, String> hmIndividualOvertime) {
		
		double dblOverTime = 0.0d;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map hmFixedExemptions = new HashMap();
		
		try {
			
					
			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
			String strLevelId = hmEmpLevelMap.get(strEmpId);
			String strOverTimeType = null;
			
			double dblTotalHoursWorked = 0;
			
			
			double dblOvertimeFixedAmount = 0;
			
			
			
			
			
			for(int j=0; j<alPresentDays.size(); j++) {
				String strDate = alPresentDays.get(j);
				
				if(hmHolidays!=null && hmHolidays.containsKey(strDate)) {
					strOverTimeType = "PH";
				} else {
					strOverTimeType = "EH";
				}
				
				
				Map<String,String> hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
				if(hmTemp==null) hmTemp=new HashMap<String,String>();
				
				String strSalarySubHead = hmTemp.get("SALARY_HEAD_ID");
				String strPaymentType = hmTemp.get("PAYMENT_TYPE");
//				double dblAmount = uF.parseToDouble(hmTemp.get("PAYMENT_AMOUNT"));
				double dblAmount = 0;
				/**
				 * Loop For all amounts
				 * 
				 * **/
				
				List<String> headIDList = null;
				if(strSalarySubHead!=null) {
					headIDList=Arrays.asList(strSalarySubHead.split(","));
				}
				
				double dblSubSalaryAmount = 0;
				double dblSubSalaryAmountActualCTC = 0;
				for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++) {
					dblAmount += uF.parseToDouble(hmTemp.get("PAYMENT_AMOUNT"));
					
					Map<String,String> hmSubSalaryDetails = hmCalculatedSalary.get(headIDList.get(i).trim());
					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
					dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));
					
					
					
					Map<String,String> hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(headIDList.get(i).trim());
					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap<String,String>();
					dblSubSalaryAmountActualCTC  += uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));
					
					
				}
				
				
				/*Map hmSubSalaryDetails = hmCalculatedSalary.get(strSalarySubHead);
				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
				double dblSubSalaryAmount = uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));
				
				
				
				Map hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(strSalarySubHead);
				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
				double dblSubSalaryAmountActualCTC = uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));*/
				
				
				
				
				for(int k=0; k<alServices.size(); k++) {
					String strService = alServices.get(k);
					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));

					dblTotalHoursWorked +=  hrsWorked;
					
					
					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")) {
						
						/**
						 *  IF condition is for additional hours worked during working days
						 * 
						 * **/
						
						
						if(hrsWorked > dblStandardHours && dblStandardHours>0) {
							dblOTHoursWorked = (hrsWorked - dblStandardHours);
							
							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
							}else if(strPaymentType!=null) {
								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
							}
							
						}
						
					} else {
						
						/**
						 *  Else condition is for pubic holidays
						 * 
						 * **/
				
						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
							
						}else if(strPaymentType!=null) {
							dblOverTime += dblAmount ;
							dblOvertimeFixedAmount += dblAmount ;
						}
						
					}
				}
			}
			
			
			
			
			
			
			
			Map<String,String> hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_BH");
			if(hmTemp==null) hmTemp=new HashMap<String,String>();
			
			/*
			if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")) {
				System.out.println("hmOverTimeMap="+hmOverTimeMap);
				System.out.println("LEVEL_==="+"LEVEL_"+strLevelId+"_TYPE_BH");
				System.out.println("hmTemp="+hmTemp);
				
				System.out.println("alPresentWeekEndDays="+alPresentWeekEndDays);
			}
			*/
			
			for(int j=0; hmTemp.size()>0 && j<alPresentWeekEndDays.size(); j++) {
				String strDate = alPresentWeekEndDays.get(j);
				strOverTimeType = "BH";
				
				hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
				if(hmTemp==null) hmTemp=new HashMap<String,String>();
				
				String strSalarySubHead = hmTemp.get("SALARY_HEAD_ID");
				String strPaymentType = hmTemp.get("PAYMENT_TYPE");
				//double dblAmount = uF.parseToDouble(hmTemp.get("PAYMENT_AMOUNT"));
				
				
				
				/*Map hmSubSalaryDetails = hmCalculatedSalary.get(strSalarySubHead);
				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
				double dblSubSalaryAmount = uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));
				
				Map hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(strSalarySubHead);
				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
				double dblSubSalaryAmountActualCTC = uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));*/
				
				
				
				double dblAmount = 0;
				/**
				 * Loop For all amounts
				 * 
				 * **/
				
				List<String> headIDList= null;
				if(strSalarySubHead!=null) {
					headIDList=Arrays.asList(strSalarySubHead.split(","));
				}
				
				
				double dblSubSalaryAmount = 0;
				double dblSubSalaryAmountActualCTC = 0;
				for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++) {
					dblAmount += uF.parseToDouble(hmTemp.get("PAYMENT_AMOUNT"));
					
					Map<String,String> hmSubSalaryDetails = hmCalculatedSalary.get(headIDList.get(i).trim());
					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
					dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));
					
					
					Map<String,String> hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(headIDList.get(i).trim());
					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap<String,String>();
					dblSubSalaryAmountActualCTC  += uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));
					
					
				}
				
				
				
				

				/*
				if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")) {
					System.out.println("alServices="+alServices);
					System.out.println("hmTemp="+hmTemp);
				}
				*/  
				
				for(int k=0; k<alServices.size(); k++) {
					String strService = alServices.get(k);
					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));

					/*
					if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")) {
						System.out.println("strService="+strService);
						System.out.println("strDate="+strDate);
						System.out.println("hmHoursWorked="+hmHoursWorked);
						System.out.println("hrsWorked="+hrsWorked);
					}
					*/
					
					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")) {
						dblTotalHoursWorked +=  hrsWorked;
						/**
						 *  IF condition is for additional hours worked during working days
						 * 
						 * **/
						
						
						if(hrsWorked > dblStandardHours && dblStandardHours>0) {
							dblOTHoursWorked = (hrsWorked - dblStandardHours);
							
							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
							}else if(strPaymentType!=null) {
								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
							}
							
						}
						
					} else {
						
						/**
						 *  Else condition is for pubic holidays
						 * 
						 * **/
				
						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
							dblTotalHoursWorked +=  hrsWorked;
							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
							
						}else if(strPaymentType!=null) {
							dblOverTime += dblAmount ;
							dblOvertimeFixedAmount += dblAmount ;
						}
						
					}
				}
			}
			
			
			
			double dblStdOvertimeHours = uF.parseToDouble(hmEmpLevelMap.get(strEmpId+"_SOH"));
			if(dblStdOvertimeHours==0) {
				dblStdOvertimeHours = dblStandardHours;
			}
			
			
			
			
			
			
			
			hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
			if(hmTemp==null) hmTemp=new HashMap<String,String>();
			String strSalarySubHead = hmTemp.get("SALARY_HEAD_ID");
//			Map hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(strSalarySubHead);
//			if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//			double dblSubSalaryAmountActualCTC = uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));
			
			/**
			 * Loop For all amounts
			 * 
			 * **/
			
			List<String> headIDList=null;
			if(strSalarySubHead!=null) {
				headIDList=Arrays.asList(strSalarySubHead.split(","));
			}
			double dblSubSalaryAmountActualCTC = 0;
			for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++) {
								
				Map<String,String> hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(headIDList.get(i).trim());
				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap<String,String>();
				dblSubSalaryAmountActualCTC  += uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));
				
				
			}
			
			
			

			/*
			if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")) {
				
				System.out.println("dblTotalHoursWorked="+dblTotalHoursWorked);
				System.out.println("alPresentDays.size()="+alPresentDays.size());
				System.out.println("dblStandardHours="+dblStandardHours);
				System.out.println("dblStdOvertimeHours="+dblStdOvertimeHours);
				System.out.println("dblSubSalaryAmountActualCTC="+dblSubSalaryAmountActualCTC);
				System.out.println("nTotalNumberOfDaysForCalc="+nTotalNumberOfDaysForCalc);
				System.out.println("dblOvertimeFixedAmount="+dblOvertimeFixedAmount);
			}*/
			
			double dblOverTimeHours = (dblTotalHoursWorked - ( alPresentDays.size() * dblStandardHours));
			if(dblStdOvertimeHours>0) {
				dblOverTime = dblOverTimeHours * dblSubSalaryAmountActualCTC / (dblStdOvertimeHours * nTotalNumberOfDaysForCalc);
				dblOverTime += dblOvertimeFixedAmount;	
			}
			
			dblOverTime += uF.parseToDouble(hmIndividualOvertime.get(strEmpId)) ;

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblOverTime;
	}
	
	/*public double getOverTimeCalculationL(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List alServices, List alPresentDays, List alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, Map<String, Map<String, String>> hmOverTimeMap, Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays, int nTotalNumberOfDaysForCalc, Map<String, String> hmIndividualOvertime) {
		
		double dblOverTime = 0.0d;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map hmFixedExemptions = new HashMap();
		
		try {
			
					
			double dblOTHoursWorked = 0;
			double dblAdditionalHoursWorked = 0;
			String strLevelId = hmEmpLevelMap.get(strEmpId);
			String strOverTimeType = null;
			
			double dblTotalHoursWorked = 0;
			
			
			double dblOvertimeFixedAmount = 0;
			
			
			
			
			
			for(int j=0; j<alPresentDays.size(); j++) {
				String strDate = alPresentDays.get(j);
				
				if(hmHolidays!=null && hmHolidays.containsKey(strDate)) {
					strOverTimeType = "PH";
				} else {
					strOverTimeType = "EH";
				}
				
				
				Map hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
				if(hmTemp==null) hmTemp=new HashMap();
				
				String strSalarySubHead = hmTemp.get("SALARY_HEAD_ID");
				String strPaymentType = hmTemp.get("PAYMENT_TYPE");
				double dblAmount = uF.parseToDouble(hmTemp.get("PAYMENT_AMOUNT"));
				
				*//**
				 * Loop For all amounts
				 * 
				 * **//*
				
				
				
				
				
				Map hmSubSalaryDetails = hmCalculatedSalary.get(strSalarySubHead);
				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
				double dblSubSalaryAmount = uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));
				
				Map hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(strSalarySubHead);
				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
				double dblSubSalaryAmountActualCTC = uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));
				
				for(int k=0; k<alServices.size(); k++) {
					String strService = alServices.get(k);
					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));

					dblTotalHoursWorked +=  hrsWorked;
					
					
					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")) {
						
						*//**
						 *  IF condition is for additional hours worked during working days
						 * 
						 * **//*
						
						
						if(hrsWorked > dblStandardHours) {
							dblOTHoursWorked = (hrsWorked - dblStandardHours);
							
							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
							}else if(strPaymentType!=null) {
								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
							}
							
						}
						
					} else {
						
						*//**
						 *  Else condition is for pubic holidays
						 * 
						 * **//*
				
						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
							
						}else if(strPaymentType!=null) {
							dblOverTime += dblAmount ;
							dblOvertimeFixedAmount += dblAmount ;
						}
						
					}
				}
			}
			
			
			
			
			
			
			
			Map hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_BH");
			if(hmTemp==null) hmTemp=new HashMap();
			
			
			if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")) {
				System.out.println("hmOverTimeMap="+hmOverTimeMap);
				System.out.println("LEVEL_==="+"LEVEL_"+strLevelId+"_TYPE_BH");
				System.out.println("hmTemp="+hmTemp);
				
				System.out.println("alPresentWeekEndDays="+alPresentWeekEndDays);
			}
			
			
			for(int j=0; hmTemp.size()>0 && j<alPresentWeekEndDays.size(); j++) {
				String strDate = alPresentWeekEndDays.get(j);
				strOverTimeType = "BH";
				
				hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
				if(hmTemp==null) hmTemp=new HashMap();
				
				String strSalarySubHead = hmTemp.get("SALARY_HEAD_ID");
				String strPaymentType = hmTemp.get("PAYMENT_TYPE");
				double dblAmount = uF.parseToDouble(hmTemp.get("PAYMENT_AMOUNT"));
				
				Map hmSubSalaryDetails = hmCalculatedSalary.get(strSalarySubHead);
				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
				double dblSubSalaryAmount = uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));
				
				Map hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(strSalarySubHead);
				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
				double dblSubSalaryAmountActualCTC = uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));

				
				if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")) {
					System.out.println("alServices="+alServices);
					System.out.println("hmTemp="+hmTemp);
				}
				  
				
				for(int k=0; k<alServices.size(); k++) {
					String strService = alServices.get(k);
					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));

					
					if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")) {
						System.out.println("strService="+strService);
						System.out.println("strDate="+strDate);
						System.out.println("hmHoursWorked="+hmHoursWorked);
						System.out.println("hrsWorked="+hrsWorked);
					}
					
					
					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")) {
						dblTotalHoursWorked +=  hrsWorked;
						*//**
						 *  IF condition is for additional hours worked during working days
						 * 
						 * **//*
						
						
						if(hrsWorked > dblStandardHours) {
							dblOTHoursWorked = (hrsWorked - dblStandardHours);
							
							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
							}else if(strPaymentType!=null) {
								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
							}
							
						}
						
					} else {
						
						*//**
						 *  Else condition is for pubic holidays
						 * 
						 * **//*
				
						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")) {
							dblTotalHoursWorked +=  hrsWorked;
							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
							
						}else if(strPaymentType!=null) {
							dblOverTime += dblAmount ;
							dblOvertimeFixedAmount += dblAmount ;
						}
						
					}
				}
			}
			
			
			
			double dblStdOvertimeHours = uF.parseToDouble(hmEmpLevelMap.get(strEmpId+"_SOH"));
			if(dblStdOvertimeHours==0) {
				dblStdOvertimeHours = dblStandardHours;
			}
			
			
			
			
			
			
			
			hmTemp = hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
			if(hmTemp==null) hmTemp=new HashMap();
			String strSalarySubHead = hmTemp.get("SALARY_HEAD_ID");
			Map hmSubSalaryDetailsActualCTC = hmActualSalaryCTC.get(strSalarySubHead);
			if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
			double dblSubSalaryAmountActualCTC = uF.parseToDouble(hmSubSalaryDetailsActualCTC.get("AMOUNT"));
			

			
			if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")) {
				
				System.out.println("dblTotalHoursWorked="+dblTotalHoursWorked);
				System.out.println("alPresentDays.size()="+alPresentDays.size());
				System.out.println("dblStandardHours="+dblStandardHours);
				System.out.println("dblStdOvertimeHours="+dblStdOvertimeHours);
				System.out.println("dblSubSalaryAmountActualCTC="+dblSubSalaryAmountActualCTC);
				System.out.println("nTotalNumberOfDaysForCalc="+nTotalNumberOfDaysForCalc);
				System.out.println("dblOvertimeFixedAmount="+dblOvertimeFixedAmount);
			}
			
			double dblOverTimeHours = (dblTotalHoursWorked - ( alPresentDays.size() * dblStandardHours));
			dblOverTime = dblOverTimeHours * dblSubSalaryAmountActualCTC / (dblStdOvertimeHours * nTotalNumberOfDaysForCalc);
			dblOverTime += dblOvertimeFixedAmount;
			
			
			dblOverTime += uF.parseToDouble(hmIndividualOvertime.get(strEmpId)) ;
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblOverTime;
	}*/
	

	
	public double getBonusCalculation(Connection con, UtilityFunctions uF, String strEmpId,String strD2, Map<String, String> hmEmpLevelMap, Map<String, Map<String, String>> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmEmpJoiningMap, CommonFunctions CF, Map<String, String> hmIndividualBonus) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblBonusCalculatedAmount = 0;
		
		try {
			

			String strJoiningDate = hmEmpJoiningMap.get(strEmpId);
			String strCurrentDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			
//			System.out.println("strJoiningDate===>"+strJoiningDate);
//			System.out.println("strCurrentDate===>"+strCurrentDate);
			
			String strDays = uF.dateDifference(strJoiningDate, DATE_FORMAT, strCurrentDate, DBDATE,CF.getStrTimeZone());
			int nDays = uF.parseToInt(strDays);
			
			
			
			Map<String,String> hmTemp = hmTotal.get(BASIC+"");
			if(hmTemp==null)hmTemp = new HashMap<String,String>();
			double dblBasic = uF.parseToDouble(hmTemp.get("AMOUNT"));
			hmTemp = hmTotal.get(DA+"");
			if(hmTemp==null)hmTemp = new HashMap<String,String>();
			double dblDA = uF.parseToDouble(hmTemp.get("AMOUNT"));
			double dblTotalGross = dblBasic + dblDA;
			
			
			String strLevelId = hmEmpLevelMap.get(strEmpId);
			
			pst = con.prepareStatement(selectBonus1);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strLevelId));
			rs = pst.executeQuery();
			
			
			double dblMinimumBonus = 0.0;
			double dblMaximumBonus = 0.0;
			double dblBonusAmount = 0.0;
			double dblMinimumBonusDays = 0.0;
			String strBonusType = null;
			String strBonusPeriod = null;
			String strBonusSalary = null;
//			String strEffectiveFY = null;
			String strSalaryCalculation = null;
			
			if(rs.next()) {
				dblMinimumBonus = rs.getDouble("bonus_minimum");
				dblMaximumBonus = rs.getDouble("bonus_maximum");
				dblBonusAmount = rs.getDouble("bonus_amount");
				dblMinimumBonusDays = rs.getDouble("bonus_minimum_days");
				strBonusType = rs.getString("bonus_type");
				strBonusPeriod = rs.getString("bonus_period");
				strBonusSalary = rs.getString("salary_head_id");
//				strEffectiveFY = rs.getString("salary_effective_year");
				strSalaryCalculation = rs.getString("salary_calculation");
			}
			rs.close();
			pst.close();
			
			
			String []arrMonth = null;
			boolean isBonusCalculation = false;
			
			if(strBonusPeriod!=null) {
				strBonusPeriod = strBonusPeriod.replaceAll("\\[", "");
				strBonusPeriod = strBonusPeriod.replaceAll("\\]", "");
				strBonusPeriod = strBonusPeriod.replaceAll(", ", ",");
				arrMonth = strBonusPeriod.split(",");
				
				if(arrMonth!=null && ArrayUtils.contains(arrMonth, nPayMonth+"")>=0) {
					isBonusCalculation = true;
				}
			}
			
			if(strBonusSalary!=null) {
				int index = strBonusSalary.lastIndexOf(",");
				strBonusSalary = strBonusSalary.substring(0, index);
			}
			
			
			
			
			
			String []arrSalary = null;
			if(strBonusSalary!=null) {
				arrSalary = strBonusSalary.split(",");
			}
			
			double dblAmount = 0;
			double dblSalaryAmount = 0;
			
			
			if(uF.parseToInt(strSalaryCalculation)==2) { // 2 is for cumulative
				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where salary_head_id in ("+strBonusSalary+") and emp_id = ? and financial_year_from_date=? and financial_year_to_date=? and paid_to<=? and paid_to>=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				
				if(uF.parseToInt(strSalaryCalculation)==2) {// 2 is for previous year
					
					Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
					cal.add(Calendar.YEAR, -1);
					String strPrevDate = uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.DATE) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
					String []arrFinancialYear = CF.getFinancialYear(con, strPrevDate, CF, uF);
					pst.setDate(2, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strPrevDate, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT)); // This condittion  needs to be modified if Bonus is paid 2nd or 3rd time.
				} else {
					String []arrFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
					pst.setDate(2, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT)); // This condittion  needs to be modified if Bonus is paid 2nd or 3rd time.
				}
				
				while(rs.next()) {
					dblSalaryAmount = uF.parseToDouble(rs.getString("amount"));
				}
				rs.close();
				pst.close();
			} else {
				
				/**
				 * If current salary is considered for previous months, then the condition needs to be added here.
				 * */
				for(int i=0; arrSalary!=null && i<arrSalary.length; i++) {
					
					hmTemp = hmTotal.get(arrSalary[i]);
					if(hmTemp==null)hmTemp = new HashMap<String,String>();
					dblSalaryAmount  += uF.parseToDouble(hmTemp.get("AMOUNT"));
				}
			}
			
			
			
			
			
			if(isBonusCalculation && nDays>=dblMinimumBonusDays) {
			
				double dblCalculatedAmount = 0;
				if("A".equalsIgnoreCase(strBonusType)) {
					dblAmount = dblBonusAmount;
				} else {
	//				dblAmount = (dblBonusAmount * (dblBasic + dblDA)) / 100;
					dblAmount = (dblBonusAmount * dblSalaryAmount) / 100;
					
				}
	//			dblCalculatedAmount = 12 * dblAmount;
				dblCalculatedAmount = dblAmount;
				
				if(dblTotalGross<=dblMinimumBonus) {
					dblBonusCalculatedAmount = dblTotalGross;
				}else if(dblMinimumBonus<=dblTotalGross  && dblTotalGross<= dblMaximumBonus) {
					dblBonusCalculatedAmount = dblMinimumBonus;
				}
				
				
				if(dblCalculatedAmount> dblMaximumBonus) {
					dblBonusCalculatedAmount = dblMaximumBonus;
				} else {
					dblBonusCalculatedAmount = dblCalculatedAmount;
				}
				
				
	//			if(arrMonth!=null && arrMonth.length>0) {
	//				dblBonusCalculatedAmount = dblBonusCalculatedAmount / arrMonth.length;
	//			}
			}
			
			dblBonusCalculatedAmount += uF.parseToDouble(hmIndividualBonus.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblBonusCalculatedAmount;
	}
	
	public double getArearCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, Map<String, String>> hmArearAmountMap, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblMonthlyAmount = 0;
		
		try {
			

//			double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
			
			
			
			
			Map<String,String> hmArearMap = hmArearAmountMap.get(strEmpId);
			if(hmArearMap==null)hmArearMap=new HashMap<String,String>();
			
			
			double dblBalanceAmount = uF.parseToDouble(hmArearMap.get("AMOUNT_BALANCE"));
			dblMonthlyAmount = uF.parseToDouble(hmArearMap.get("MONTHLY_AREAR"));
			
			if((dblBalanceAmount-dblMonthlyAmount) >0 && (dblBalanceAmount-dblMonthlyAmount) < 1) {
				dblMonthlyAmount = dblBalanceAmount;
			}
				
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblMonthlyAmount;
	}
	
	
	public double getIncentivesCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIncentives, CommonFunctions CF) {
		
		double dblIncentiveAmount = 0;
		
		try {

			dblIncentiveAmount = uF.parseToDouble(hmIncentives.get(strEmpId)) ;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblIncentiveAmount;
	}
	
	public double getIndividualOtherDeductionCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIndividualOtherDeduction, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblIndividualOtherDeductionAmount = 0;
		
		try {
			

//			double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
			
			dblIndividualOtherDeductionAmount = uF.parseToDouble(hmIndividualOtherDeduction.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblIndividualOtherDeductionAmount;
	}
	
	public double calculateServiceTax(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherTaxDetails, Map<String, String> hmEmpServiceTaxMap, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblServiceTaxAmount = 0;
		double dblCess1Amount = 0;
		double dblCess2Amount = 0;
		
		try {
			
			if(!hmEmpServiceTaxMap.containsKey(strEmpId))return 0;
			
			double dblServiceTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_SERVICE_TAX"));
			double dblEduTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_EDU_TAX"));
			double dblSTDTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_STD_TAX"));
			
			dblServiceTaxAmount = (dblGross * dblServiceTax)/100;
//			dblCess1Amount = (dblServiceTaxAmount * dblEduTax)/100;
//			dblCess2Amount = (dblServiceTaxAmount * dblSTDTax)/100;
			
			dblServiceTaxAmount = dblServiceTaxAmount + dblCess1Amount + dblCess2Amount;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblServiceTaxAmount;
	}
	
	public double getIndividualOtherEarningCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIndividualOtherEarning, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblIndividualOtherEarningAmount = 0;
		
		try {
			

//			double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
			
			dblIndividualOtherEarningAmount = uF.parseToDouble(hmIndividualOtherEarning.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblIndividualOtherEarningAmount;
	}

	public double getReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmReimbursement, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblReimbursementAmount = 0;
		
		try {
			

			
			
//			double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
			
			dblReimbursementAmount = uF.parseToDouble(hmReimbursement.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblReimbursementAmount;
	}
	
	
	public double getMobileReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmMobileReimbursement, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblMobileReimbursementAmount = 0;
		
		try {
			

//			double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
			
			dblMobileReimbursementAmount = uF.parseToDouble(hmMobileReimbursement.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblMobileReimbursementAmount;
	}
	
	public double getTravelReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmTravelReimbursement, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblTravelReimbursementAmount = 0;
		
		try {
			

//			double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
			
			dblTravelReimbursementAmount = uF.parseToDouble(hmTravelReimbursement.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblTravelReimbursementAmount;
	}

	
	public double getOtherReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherReimbursement, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblOtherReimbursementAmount = 0;
		
		try {
			

//			double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
			
			dblOtherReimbursementAmount = uF.parseToDouble(hmOtherReimbursement.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblOtherReimbursementAmount;
	}


	public double getMobileRecoveryCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmMobileRecovery, CommonFunctions CF) {
		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
		double dblMobileRecoveryAmount = 0;
		
		try {
			

//			double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
			
			dblMobileRecoveryAmount = uF.parseToDouble(hmMobileRecovery.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
		return dblMobileRecoveryAmount;
	}

	public double getIncrementCalculationBasic(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmBasicSalaryMap, Map<String, String> hmDASalaryMap, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, CommonFunctions CF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIncrement = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble(hmBasicSalaryMap.get(strEmpId));
//			double dblDA = uF.parseToDouble(hmDASalaryMap.get(strEmpId));
//			double dblTotalGross = dblBasic + dblDA;
			
			
			pst = con.prepareStatement("select * from increment_details where increment_from <= ? and  ?<= increment_to and due_month =? ");
			pst.setDouble(1, dblBasic);
			pst.setDouble(2, dblBasic);
			pst.setInt(3, nPayMonth);
			rs = pst.executeQuery();
			
			
//			System.out.println("pst increment===>"+pst);
			
			while(rs.next()) {
				dblIncrement = rs.getDouble("increment_amount");
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblIncrement;
	}
	
	
	
	public double getIncrementCalculationDA(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmBasicSalaryMap, Map<String, String> hmDASalaryMap, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, CommonFunctions CF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIncrement = 0;
		
		try {
			

			double dblDA = uF.parseToDouble(hmDASalaryMap.get(strEmpId));
//			double dblTotalGross = dblBasic + dblDA;
			
			
			pst = con.prepareStatement("select * from increment_details_da where increment_from <= ? and  ?<= increment_to and due_month like ? ");
			pst.setDouble(1, dblDA);
			pst.setDouble(2, dblDA);
			pst.setString(3, "%"+nPayMonth+",%");
			rs = pst.executeQuery();
			
			while(rs.next()) {
				
				if("P".equalsIgnoreCase(rs.getString("increment_amount_type"))) {
					double dblIncr = rs.getDouble("increment_amount");
					dblIncrement = dblIncr * dblDA / 100;
				} else {
					dblIncrement = rs.getDouble("increment_amount");
				}
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
		return dblIncrement;
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
	
	public void getVariableAmount(Connection con, UtilityFunctions uF, Map<String,String> hmVariables, String strPC) {
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			pst = con.prepareStatement("select * from otherearning_individual_details where pay_paycycle = ? and is_approved = 1");
			pst.setInt(1, uF.parseToInt(strPC)); 
			rs = pst.executeQuery();
			while(rs.next()) {
				hmVariables.put(rs.getString("emp_id")+"_"+rs.getString("salary_head_id")+"_"+rs.getString("earning_deduction"), rs.getString("pay_amount"));
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
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
	}
	
	 
	public void getBreakDetails(Connection con, UtilityFunctions uF, Map<String,String> hmBreaks, Map<String,String> hmBreakPolicy, String strD1, String strD2) {
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			pst = con.prepareStatement("select count(*) as cnt, emp_id, break_type_id from break_application_register where _date between ? and ? and break_type_id in (select break_type_id from leave_break_type) group by emp_id, break_type_id");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmBreaks.put(rs.getString("emp_id")+"_"+rs.getString("break_type_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			if(hmBreaks!=null && hmBreaks.size()>0) {
				pst = con.prepareStatement("select * from leave_break_type");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmBreakPolicy.put(rs.getString("break_type_id")+"_"+rs.getString("org_id"), rs.getString("ded_amount"));
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
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
	}
	
	
	int halfDayCountIN=0;
	int halfDayCountOUT=0;
	
	public boolean isHalfDay(String strDate, double dblEarlyLate, String strINOUT, String strLocationId, UtilityFunctions uF, Connection con) {
		boolean isHalfDay = false;
		
		PreparedStatement pst=null;
		ResultSet rs = null;
		
		try {
			
			if(dblEarlyLate==0)return false;
			
//			double  dblValue = dblEarlyLate * 60;
			double  dblValue = dblEarlyLate;
			int days=0;
			
			   
			if("IN".equalsIgnoreCase(strINOUT)) {
				pst = con.prepareStatement("select * from roster_halfday_policy where time_value < ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes1(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			} else {
				pst = con.prepareStatement("select * from roster_halfday_policy where time_value > ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes1(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			}
//			System.out.println("half day rule pst======>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if("IN".equalsIgnoreCase(strINOUT)) {
					halfDayCountIN++;
				} else {
					halfDayCountOUT++;
				}
				days = rs.getInt("days");
			}
			rs.close();
			pst.close();
			
			
			if(days==halfDayCountIN && halfDayCountIN>0) {
				halfDayCountIN=0;
				isHalfDay = true;
			}
			
			if(days==halfDayCountOUT && halfDayCountOUT>0) {
				halfDayCountOUT=0;
				isHalfDay = true;
			}
			
		} catch (Exception e) {
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
		return isHalfDay;
	}
	
	int fullDayCountIN=0;
	int fullDayCountOUT=0;
	
	public boolean isFullDay(String strDate, double dblEarlyLate, String strINOUT, String strLocationId, UtilityFunctions uF, Connection con) {
		boolean isFullDay = false;
		
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {
			
			if(dblEarlyLate==0)return false;
			
//			double  dblValue = dblEarlyLate * 60;
			double  dblValue = dblEarlyLate;
			int days=0;
			
			
			if("IN".equalsIgnoreCase(strINOUT)) {
				pst = con.prepareStatement("select * from roster_fullday_policy where time_value < ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes1(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			} else {
				pst = con.prepareStatement("select * from roster_fullday_policy where time_value > ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value  limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes1(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			}

			rs = pst.executeQuery();
			while(rs.next()) {
				if("IN".equalsIgnoreCase(strINOUT)) {
					fullDayCountIN++;
				} else {
					fullDayCountOUT++;
				}
				days = rs.getInt("days");
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("pst==="+pst);
//			System.out.println("halfDayCountOUT==="+halfDayCountOUT);
//			System.out.println("halfDayCountIN==="+halfDayCountIN);
			
			if(days==fullDayCountIN && fullDayCountIN>0) {
				fullDayCountIN=0;
				isFullDay = true;
			}
			
			if(days==fullDayCountOUT && fullDayCountOUT>0) {
				fullDayCountOUT=0;
				isFullDay = true;
			}
			
		} catch (Exception e) {
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
		return isFullDay;
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
	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}
	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}
	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}
	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}
	public String getF_paymentMode() {
		return f_paymentMode;
	}
	public void setF_paymentMode(String f_paymentMode) {
		this.f_paymentMode = f_paymentMode;
	}
	public String[] getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String[] paymentMode) {
		this.paymentMode = paymentMode;
	}
//	public String getStrD1() {
//		return strD1;
//	}
//	public void setStrD1(String strD1) {
//		this.strD1 = strD1;
//	}
//	public String getStrD2() {
//		return strD2;
//	}
//	public void setStrD2(String strD2) {
//		this.strD2 = strD2;
//	}
//	public String getStrPC() {
//		return strPC;
//	}
//	public void setStrPC(String strPC) {
//		this.strPC = strPC;
//	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}


	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}


	public void calculateETDS(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map<String,String> hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map<String,String> hmVariables, boolean isInsert,Map<String, String> hmOtherTaxDetails,Map<String, String> hmEmpLevelMap) {
		
		PreparedStatement pst = null;
//		ResultSet rs = null;
		
		Database db = null;
		try {
			if(con==null) {
				db = new Database();
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			
			
			double dblEduCess = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
			double dblSTDCess = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
//			double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
			
			double dblTDSMonth = 0;
			double dblActual=0;
			double dblEDuTax=0;
			double dblSTDTax=0;
			double dblflatTds=0;
			
			if(uF.parseToBoolean(hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))) {
//				dblTDSMonth = dblGross * dblFlatTDS / 100;
				dblActual= uF.parseToDouble(hmTotal.get(TDS+""));
				dblTDSMonth=dblActual;
				dblflatTds=dblActual;
			} else {					
				dblActual= uF.parseToDouble(hmTotal.get(TDS+""));
								
				dblTDSMonth=dblActual/(1+(dblEduCess/100)+(dblSTDCess/100));
				
				dblEDuTax = dblTDSMonth * (dblEduCess/100);
				dblSTDTax = dblTDSMonth * (dblSTDCess/100);
//				System.out.println("dblEduCess====>"+(dblEduCess/100)+"------dblSTDCess---------"+(dblSTDCess/100));
				/*System.out.println("dblEDuTax====>"+dblEDuTax+"------dblSTDTax---------"+dblSTDTax+"\\\\\\dblTDSMonth"+dblTDSMonth);
				System.out.println("dblTDSMonth====>"+dblTDSMonth+"------dblActual---------"+dblActual);*/
			}
			
			if(isInsert) {
				pst = con.prepareStatement("insert into emp_tds_details (financial_year_start, financial_year_end, tds_amount," +
						" edu_tax_amount, std_tax_amount, user_id, entry_timestamp, emp_id, paycycle, _month,flat_tds_amount,actual_tds_amount) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDouble(3, uF.parseToDouble(uF.formatIntoTwoDecimal(dblTDSMonth))); 
				pst.setDouble(4, uF.parseToDouble(uF.formatIntoTwoDecimal(dblEDuTax)));
				pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(dblSTDTax)));
				pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setTimestamp(7, null);
				pst.setInt(8, uF.parseToInt(strEmpId));
				pst.setInt(9, uF.parseToInt(strPaycycle));
				pst.setInt(10, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
				pst.setDouble(11, uF.parseToDouble(uF.formatIntoTwoDecimal(dblflatTds)));
				pst.setDouble(12, uF.parseToDouble(uF.formatIntoTwoDecimal(dblActual))); 
				pst.execute();
				pst.close();
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);			
		}finally{
			if(db!=null) {
				db.closeConnection(con);
			}
			
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void setCF(CommonFunctions CF) {
		this.CF=CF;
	}

	public double calculateCandiEEESI(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, String strStateId, String candID,int nLevelId, int nOrgId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		try {
			
//			pst = con.prepareStatement(selectESI);
			pst = con.prepareStatement("select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id=? " +
					"and org_id=? and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strStateId));
			pst.setInt(4, nOrgId);
			pst.setInt(5, nLevelId);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			double dblEEESIAmount = 0;
			double dblESIMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()) {
				dblEEESIAmount = rs.getDouble("eesi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			double dblAmount = 0;
			double dblAmountEligibility = 0; 
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME) {
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));	
			}
			
			if(dblAmountEligibility<dblESIMaxAmount) {
				dblCalculatedAmount = (( dblEEESIAmount * dblAmount ) / 100);
			}
			
		} catch (Exception e) {
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
		return dblCalculatedAmount;
	}

	
}




class ApprovePayrollRunnable implements IConstants{
	
	ApprovePayroll objApprovePayroll;
	GenerateSalarySlip gs;
	
	Map<String,String> hmTotal;
	Connection con;
	UtilityFunctions uF;
	CommonFunctions CF;
	String strFinancialYearStart;
	String strFinancialYearEnd;
	String strEmpId;
	String strOrgId;
	String []strApprovePayCycle;
	Map<String,String> hmEmpStateMap;
	Map<String, Map<String, String>> hmCurrencyDetails;
	Map<String,String> hmEmpCurrency;
	Map<String,String> hmVariables;
	HttpServletRequest request;
	HttpServletResponse response;
	double dblTotal;
	
	Map<String, String> hmOtherTaxDetails;
	Map<String, String> hmEmpLevelMap;
	String strDomain;
	
	Map<String, Map<String, String>> hmArearAmountMap;
	
	public ApprovePayrollRunnable(ApprovePayroll objApprovePayroll, GenerateSalarySlip gs, Connection con, UtilityFunctions uF, CommonFunctions CF, String strFinancialYearStart, String strFinancialYearEnd, String []strApprovePayCycle, Map<String,String> hmEmpStateMap, Map<String, Map<String, String>> hmCurrencyDetails, Map<String,String> hmEmpCurrency, Map<String,String> hmVariables, HttpServletRequest request, HttpServletResponse response,Map<String, String> hmOtherTaxDetails,Map<String, String> hmEmpLevelMap, String strDomain,Map<String, Map<String, String>> hmArearAmountMap) {
		this.objApprovePayroll = objApprovePayroll;
		this.gs = gs;
		this.con = con;
		this.uF = uF;
		this.CF = CF;
		this.strFinancialYearStart = strFinancialYearStart;
		this.strFinancialYearEnd = strFinancialYearEnd;
		
		this.strApprovePayCycle = strApprovePayCycle;
		this.hmEmpStateMap = hmEmpStateMap;
		this.hmCurrencyDetails = hmCurrencyDetails;
		this.hmEmpCurrency = hmEmpCurrency;
		this.hmVariables = hmVariables;
		this.request = request;
		this.response = response;
		this.hmOtherTaxDetails=hmOtherTaxDetails;
		this.hmEmpLevelMap=hmEmpLevelMap;
		this.strDomain = strDomain;
		this.hmArearAmountMap = hmArearAmountMap;
	}
	
	
	public void setData(Map<String,String> hmTotal, String strEmpId, double dblTotal, String strOrgId) {
		this.hmTotal = hmTotal;
		this.strEmpId = strEmpId;
		this.dblTotal = dblTotal;
		this.strOrgId = strOrgId;
	}
	
	public void run1() {

		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_EPF+"")) {
			objApprovePayroll.calculateEEPF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true,hmArearAmountMap);
			objApprovePayroll.calculateERPF(con,CF, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true,hmArearAmountMap);
		}
		
		
		
		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_ESI+"")) {
			objApprovePayroll.calculateEESI(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true);
		}
		
		
		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_LWF+"")) {
			objApprovePayroll.calculateELWF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true, strOrgId);
		}
		
		if(hmTotal!=null && hmTotal.containsKey(TDS+"")) {
			objApprovePayroll.calculateETDS(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true,hmOtherTaxDetails,hmEmpLevelMap);
		}
		
		/*
		
		
		try {
			gs.setStrEmpId(strEmpId);
//			gs.setStrServiceId(((alServices.size()>0)?alServices.get(0):"0"));
			gs.setStrServiceId(0+"");
			gs.setStrMonth(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM"));
			gs.setStrPC(strApprovePayCycle[2]);
			gs.setStrFYS(strFinancialYearStart);
			gs.setStrFYE(strFinancialYearEnd);				
			gs.setAttachment(true);				
			gs.setServletRequest(request);
			gs.setServletResponse(response);
			gs.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}*/ 
		
	}
	
	
}