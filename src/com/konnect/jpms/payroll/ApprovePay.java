package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
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
import com.konnect.jpms.util.LogDetails;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApprovePay extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 *   
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType;
	String strSessionEmpId;

	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strGrade;
	
	String strEmployeType;
	
	String paycycle;
	String strPaycycleDuration;
	String f_paymentMode;
	String f_org;
	String[] f_strWLocation;
	String[] f_level;
	String[] f_department;
	String[] f_service;
	String[] f_grade;
	String[] f_employeType;

	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillPayCycleDuration> paycycleDurationList;
	List<FillPayMode> paymentModeList;
	List<FillOrganisation> organisationList;

	List<String> strEmpIds;
	String approvePC;

	String pageFrom;
	
	List<FillGrade> gradeList;
	List<FillEmploymentType> employementTypeList;
	
	String strProcess;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, "/jsp/payroll/ApprovePay.jsp");
		request.setAttribute(TITLE, "Approve Pay");
		
		Date startDate = new Date();
//		System.out.println("start time==>"+startDate);

		if (uF.parseToInt(getF_org()) == 0) {
			setF_org((String) session.getAttribute(ORGID));
		}
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(getF_level()!=null) {
			String level_id ="";
			for (int i = 0; i < getF_level().length; i++) {
				if(i==0) {
					level_id = getF_level()[i];
					level_id.concat(getF_level()[i]);
				} else {
					level_id =level_id+","+getF_level()[i];
				}
			}
			gradeList = new FillGrade(request).fillGrade(level_id, getF_org());
		} else {
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}
		
		if(getStrGrade() != null && !getStrGrade().equals("")) {
			setF_grade(getStrGrade().split(","));
		} else {
			setF_grade(null);
		}
		
		if(getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		
		if (getStrPaycycleDuration() == null || getStrPaycycleDuration().trim().equals("") || getStrPaycycleDuration().trim().equalsIgnoreCase("NULL")) {
			setStrPaycycleDuration("M");
		}

//		System.out.println("getPaycycle ===>> " + getPaycycle());
		String[] strPayCycleDates = null;
		if (getApprovePC() != null && !getApprovePC().trim().equalsIgnoreCase("") && !getApprovePC().trim().equalsIgnoreCase("NULL") && getApprovePC().length() > 0) {
			strPayCycleDates = getApprovePC().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}

		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		String strPC = strPayCycleDates[2];
//		System.out.println("strD2 ===>> " + strD2);
		
		String formType = (String) request.getParameter("formType");

		paycycleList = new FillPayCycles(getStrPaycycleDuration(), request).fillPayCycles(CF, getF_org());
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if (nSalaryStrucuterType == S_GRADE_WISE) {
			 if(formType != null && formType.trim().equalsIgnoreCase("revoke")) {
				 revokeandOpenClockEntries(uF, strD1, strD2, strPC);
			 } else if(formType != null && formType.trim().equalsIgnoreCase("approve")) {
				 approvePayrollEntriesByGrade(uF, strD1, strD2, strPC);
			 }
			 viewApprovePayByGrade(uF, strD1, strD2, strPC);
		} else {
			if (formType != null && formType.trim().equalsIgnoreCase("revoke")) {
				revokeandOpenClockEntries(uF, strD1, strD2, strPC);
			} else if (formType != null && formType.trim().equalsIgnoreCase("approve")) {
				approvePayrollEntries(uF, strD1, strD2, strPC);
			}
			viewApprovePay(uF, strD1, strD2, strPC);
		}
//		if(getStrProcess() == null || getStrProcess().trim().equals("") || getStrProcess().trim().equalsIgnoreCase("null")) {
//			Date endDate = new Date();
////			System.out.println("End time==>"+endDate);	
//			
//			DateTime dt1 = new DateTime(startDate);
//			DateTime dt2 = new DateTime(endDate);
//
////			System.out.print(Days.daysBetween(dt1, dt2).getDays() + " days, ");
////			System.out.print(Hours.hoursBetween(dt1, dt2).getHours() % 24 + " hours, ");
////			System.out.print(Minutes.minutesBetween(dt1, dt2).getMinutes() % 60 + " minutes, ");
////			System.out.print(Seconds.secondsBetween(dt1, dt2).getSeconds() % 60 + " seconds.");
//			
//			int nEmpCnt = uF.parseToInt((String) request.getAttribute("nEmpCnt"));
//			int timeCal = 1;
//			if(nEmpCnt > 60) {
//				double dbl = ((Seconds.secondsBetween(dt1, dt2).getSeconds() % 60) * nEmpCnt)/60;
////				System.out.print("dbl==>"+dbl);
//				timeCal = ((int) dbl)/2;
//			}
////			System.out.println("timecal==>"+timeCal+" to "+(timeCal + 2));
//			
//			String strTimeMsg = "<div class=\"nodata msg\" style=\"text-align: center;\"><span>" +
//					"Processing & Displaying<br/>The payroll processing ideally takes "+timeCal+" to "+(timeCal + 2)+" minutes for "+nEmpCnt+" employees." +
//					"</span></div>";
//			request.setAttribute("strTimeMsg", strTimeMsg);
//		}
			
		return loadApprovePay(uF);
	}

	private void approvePayrollEntriesByGrade(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);

			String AP_strD1 = (String) session.getAttribute("AP_strD1");
			String AP_strD2 = (String) session.getAttribute("AP_strD2");
			String AP_strPC = (String) session.getAttribute("AP_strPC");
			String AP_f_org = (String) session.getAttribute("AP_f_org");
			String AP_strPaycycleDuration = (String) session.getAttribute("AP_strPaycycleDuration");

			Date stD1 = uF.getDateFormat(strD1, DATE_FORMAT);
			Date stApD1 = uF.getDateFormat(AP_strD1, DATE_FORMAT);

			Date stD2 = uF.getDateFormat(strD2, DATE_FORMAT);
			Date stApD2 = uF.getDateFormat(AP_strD2, DATE_FORMAT);

			boolean check = stD1.equals(stApD1);
			boolean check1 = stD2.equals(stApD2);

			if (check && check1 && uF.parseToInt(strPC) == uF.parseToInt(AP_strPC) && uF.parseToInt(getF_org()) == uF.parseToInt(AP_f_org)
					&& (getStrPaycycleDuration() != null && getStrPaycycleDuration().equals(AP_strPaycycleDuration))) {
				String strDomain = request.getServerName().split("\\.")[0];

				Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
				if (hmCurrencyDetails == null) hmCurrencyDetails = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if (hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();

				String strFinancialYearEnd = null;
				String strFinancialYearStart = null;
				String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
				if (strFinancialYear != null) {
					strFinancialYearStart = strFinancialYear[0];
					strFinancialYearEnd = strFinancialYear[1];
				}

				String[] strApprovePayCycle = new String[3];
				strApprovePayCycle[0] = strD1;
				strApprovePayCycle[1] = strD2;
				strApprovePayCycle[2] = strPC;

				List<Map<String, String>> alEmp = (List<Map<String, String>>) session.getAttribute("AP_alEmp");
				if (alEmp == null) alEmp = new ArrayList<Map<String, String>>();
				Map<String, Map<String, String>> hmEmp = (Map<String, Map<String, String>>) session.getAttribute("AP_hmEmp");
				if (hmEmp == null) hmEmp = new HashMap<String, Map<String, String>>();
				Map<String, String> hmSalaryDetails = (Map<String, String>) session.getAttribute("AP_hmSalaryDetails");
				if (hmSalaryDetails == null) hmSalaryDetails = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmEmpSalary = (Map<String, Map<String, String>>) session.getAttribute("AP_hmEmpSalary");
//				if (hmEmpSalary == null) hmEmpSalary = new LinkedHashMap<String, Map<String, String>>();
				List<String> alEmpSalaryDetailsEarning = (List<String>) session.getAttribute("AP_alEmpSalaryDetailsEarning");
				if (alEmpSalaryDetailsEarning == null) alEmpSalaryDetailsEarning = new ArrayList<String>();
				List<String> alEmpSalaryDetailsDeduction = (List<String>) session.getAttribute("AP_alEmpSalaryDetailsDeduction");
				if (alEmpSalaryDetailsDeduction == null) alEmpSalaryDetailsDeduction = new ArrayList<String>();
				Map<String, String> hmLoanAmt = (Map<String, String>) session.getAttribute("AP_hmLoanAmt");
				if (hmLoanAmt == null) hmLoanAmt = new HashMap<String, String>();
				Map<String, String> hmLoanPoliciesMap = (Map<String, String>) session.getAttribute("AP_hmLoanPoliciesMap");
				if (hmLoanPoliciesMap == null) hmLoanPoliciesMap = new HashMap<String, String>();
				LinkedHashMap<String, Map<String, String>> hmTotalSalary = (LinkedHashMap<String, Map<String, String>>) session.getAttribute("AP_hmTotalSalary");
				if (hmTotalSalary == null) hmTotalSalary = new LinkedHashMap<String, Map<String, String>>();
				LinkedHashMap<String, Map<String, String>> hmTotalSalaryisDisplay = (LinkedHashMap<String, Map<String, String>>) session.getAttribute("AP_hmTotalSalaryisDisplay");
				if (hmTotalSalaryisDisplay == null) hmTotalSalaryisDisplay = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> hmEmpStateMap = (Map<String, String>) session.getAttribute("AP_hmEmpStateMap");
				if (hmEmpStateMap == null) hmEmpStateMap = new HashMap<String, String>();
				Map<String, String> hmEmpLevelMap = (Map<String, String>) session.getAttribute("AP_hmEmpLevelMap");
				if (hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
				Map<String, String> hmVariables = (Map<String, String>) session.getAttribute("AP_hmVariables");
				if (hmVariables == null) hmVariables = new HashMap<String, String>();
				Map<String, String> hmAnnualVariables = (Map<String, String>) session.getAttribute("AP_hmAnnualVariables");
				if (hmAnnualVariables == null) hmAnnualVariables = new HashMap<String, String>();
				
				Map<String, String> hmOtherTaxDetails = (Map<String, String>) session.getAttribute("AP_hmOtherTaxDetails");
				if (hmOtherTaxDetails == null) hmOtherTaxDetails = new HashMap<String, String>();
				Map<String, Map<String, String>> hmArearAmountMap = (Map<String, Map<String, String>>) session.getAttribute("AP_hmArearAmountMap");
				if (hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String, String>>();

				/**
				 * Arrear No of days
				 * */
				
				Map<String, List<Map<String, String>>> hmIncrementEmpArrear = (Map<String, List<Map<String,String>>>)session.getAttribute("AP_hmIncrementEmpArrear");
				if(hmIncrementEmpArrear == null) hmIncrementEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
				Map<String, Map<String, String>> hmIncrementArrearCalSalary = (Map<String, Map<String,String>>)session.getAttribute("AP_hmIncrementArrearCalSalary");
				if(hmIncrementArrearCalSalary == null)hmIncrementArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
				Map<String, List<String>> hmIncrementArrearEarningHead = (Map<String, List<String>>)session.getAttribute("AP_hmIncrementArrearEarningHead");
				if(hmIncrementArrearEarningHead == null)hmIncrementArrearEarningHead = new LinkedHashMap<String, List<String>>();
				Map<String, List<String>> hmIncrementArrearDeductionHead = (Map<String, List<String>>)session.getAttribute("AP_hmIncrementArrearDeductionHead");
				if(hmIncrementArrearDeductionHead == null)hmIncrementArrearDeductionHead = new LinkedHashMap<String, List<String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployeePF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmIncrementArrearEmployeePF");
				if(hmIncrementArrearEmployeePF == null)hmIncrementArrearEmployeePF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployerPF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmIncrementArrearEmployerPF");
				if(hmIncrementArrearEmployerPF == null)hmIncrementArrearEmployerPF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployerESI = (Map<String, Map<String, String>>)session.getAttribute("AP_hmIncrementArrearEmployerESI");
				if(hmIncrementArrearEmployerESI == null)hmIncrementArrearEmployerESI = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployeeLWF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmIncrementArrearEmployeeLWF");
				if(hmIncrementArrearEmployeeLWF == null)hmIncrementArrearEmployeeLWF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, List<String>> hmIncrementArrearPaycycle = (Map<String, List<String>>)session.getAttribute("AP_hmIncrementArrearPaycycle");
				if(hmIncrementArrearPaycycle == null)hmIncrementArrearPaycycle = new LinkedHashMap<String, List<String>>();
				
				Map<String, List<Map<String, String>>> hmEmpArrear = (Map<String, List<Map<String,String>>>)session.getAttribute("AP_hmEmpArrear");
				if(hmEmpArrear == null) hmEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
				Map<String, Map<String, String>> hmArrearCalSalary = (Map<String, Map<String,String>>)session.getAttribute("AP_hmArrearCalSalary");
				if(hmArrearCalSalary == null)hmArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
				Map<String, List<String>> hmArrearEarningHead = (Map<String, List<String>>)session.getAttribute("AP_hmArrearEarningHead");
				if(hmArrearEarningHead == null)hmArrearEarningHead = new LinkedHashMap<String, List<String>>();
				Map<String, List<String>> hmArrearDeductionHead = (Map<String, List<String>>)session.getAttribute("AP_hmArrearDeductionHead");
				if(hmArrearDeductionHead == null)hmArrearDeductionHead = new LinkedHashMap<String, List<String>>();
				Map<String, Map<String, String>> hmArrearEmployeePF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmArrearEmployeePF");
				if(hmArrearEmployeePF == null)hmArrearEmployeePF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployerPF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmArrearEmployerPF");
				if(hmArrearEmployerPF == null)hmArrearEmployerPF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployerESI = (Map<String, Map<String, String>>)session.getAttribute("AP_hmArrearEmployerESI");
				if(hmArrearEmployerESI == null)hmArrearEmployerESI = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployeeLWF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmArrearEmployeeLWF");
				if(hmArrearEmployeeLWF == null)hmArrearEmployeeLWF = new LinkedHashMap<String, Map<String, String>>();
				
				ApprovePayRunnable objRunnable = new ApprovePayRunnable(this, con, uF, CF, strFinancialYearStart, strFinancialYearEnd, strApprovePayCycle,
					hmEmpStateMap, hmCurrencyDetails, hmEmpCurrency, hmVariables, request, response, hmOtherTaxDetails, hmEmpLevelMap, strDomain,
					hmArearAmountMap,hmAnnualVariables,hmEmpArrear,hmArrearCalSalary,hmArrearEarningHead, hmArrearDeductionHead,hmArrearEmployeePF,
					hmArrearEmployerPF,hmArrearEmployerESI,hmArrearEmployeeLWF, hmIncrementEmpArrear,hmIncrementArrearCalSalary,hmIncrementArrearEarningHead,
					hmIncrementArrearDeductionHead,hmIncrementArrearEmployeePF,hmIncrementArrearEmployerPF,hmIncrementArrearEmployerESI,hmIncrementArrearEmployeeLWF,
					hmIncrementArrearPaycycle);

				List<String> checkPayrollList = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id,sal_effective_date from payroll_generation where paycycle=? and month=? and year=? group by emp_id,sal_effective_date having emp_id>0 order by emp_id,sal_effective_date");
				pst.setInt(1, uF.parseToInt(strApprovePayCycle[2]));
				pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
				pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
				rs = pst.executeQuery();
				while (rs.next()) {
					String strEffectiveDate = rs.getString("sal_effective_date") != null ? uF.getDateFormat(rs.getString("sal_effective_date"), DBDATE, DATE_FORMAT) : "";
					checkPayrollList.add(rs.getString("emp_id")+"_"+strEffectiveDate);
				}
				rs.close();
				pst.close();

				Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
				int nEmpSize = getStrEmpIds() != null ? getStrEmpIds().size() : 0;
				for (int ii = 0; hmTotalSalary != null && !hmTotalSalary.isEmpty() && ii < nEmpSize; ii++) {
					String strEmpIdWithEffectiveDate = getStrEmpIds().get(ii);
					String strTmp[] = strEmpIdWithEffectiveDate.split("_");
					String strEmpId = strTmp[0];
					String strOrgId = hmEmpOrgId.get(strEmpId);
					String strEmpSalEffectiveDate = strApprovePayCycle[0];
					if(strTmp.length>1) {
						strEmpSalEffectiveDate = strTmp[1];
					}
					if (!checkPayrollList.isEmpty() && checkPayrollList.contains(strEmpIdWithEffectiveDate)) {
						continue;
					}
//					System.out.println("strEmpIdWithEffectiveDate ===>> " + strEmpIdWithEffectiveDate);
//					System.out.println("hmEmp ===>> " + hmEmp);
					Map<String, String> hmEmpPay = hmEmp.get(strEmpIdWithEffectiveDate);
					if (hmEmpPay == null) hmEmpPay = new HashMap<String, String>();
//					System.out.println("hmEmpPay ===>> " + hmEmpPay);
					double dbTotalDays = uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"));
					double dblPaidDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"));
					double dblPresentDays = uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"));
					double dblPaidLeaveDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"));

					Map<String, String> hmTotal = hmTotalSalary.get(strEmpIdWithEffectiveDate);
					Map<String, String> hmTotalisDisplay = hmTotalSalaryisDisplay.get(strEmpIdWithEffectiveDate);
					int nEmpServiceId = uF.parseToInt(hmEmpPay.get("EMP_SERVICE_ID"));
					double dblTotal = 0.0;
					for (int i = 0; i < alEmpSalaryDetailsEarning.size(); i++) {
						String strSalaryId = alEmpSalaryDetailsEarning.get(i);

						if (hmTotal != null && !hmTotal.containsKey(strSalaryId)) {
							if (hmTotalisDisplay != null && hmTotalisDisplay.containsKey(strSalaryId)) {
								pst = con.prepareStatement("insert into payroll_generation_lta (emp_id,month,year,pay_date,entry_date," +
									"salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, " +
									"service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, " +
									"paid_leaves, total_days,sal_effective_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
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
								pst.setInt(12, nEmpServiceId);
								pst.setString(13, "E");
								pst.setString(14, getStrPaycycleDuration());
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
								pst.setDouble(18, dblPresentDays);
								pst.setDouble(19, dblPaidDays);
								pst.setDouble(20, dblPaidLeaveDays);
								pst.setDouble(21, dbTotalDays);
								pst.setDate(22, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst.execute();
								pst.close();
							}
							continue;
						}

						pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, "
							+ "financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, " +
							"payment_mode, present_days, paid_days, paid_leaves, total_days,approve_by,approve_date,sal_effective_date) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
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
						pst.setInt(12, nEmpServiceId);
						pst.setString(13, "E");
						pst.setString(14, getStrPaycycleDuration());
						pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
						pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
						pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
						pst.setDouble(18, dblPresentDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPaidLeaveDays);
						pst.setDouble(21, dbTotalDays);
						pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(24, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
//						System.out.println("pst ===>> " + pst);
						pst.execute();
						pst.close();

						dblTotal += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmTotal.get(strSalaryId))));

						double dblAmt = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmTotal.get(strSalaryId))));
						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == REIMBURSEMENT && dblAmt > 0) {
							pst = con.prepareStatement(updateReimbursementPayroll);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}

						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == TRAVEL_REIMBURSEMENT && dblAmt > 0) {
							pst = con.prepareStatement(updateReimbursementPayroll1);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}

						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == MOBILE_REIMBURSEMENT && dblAmt > 0) {
							pst = con.prepareStatement(updateReimbursementPayroll2);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}

						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == OTHER_REIMBURSEMENT && dblAmt > 0) {
							pst = con.prepareStatement(updateReimbursementPayroll3);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}
					}

					for (int i = 0; i < alEmpSalaryDetailsDeduction.size(); i++) {
						String strSalaryId = alEmpSalaryDetailsDeduction.get(i);

						if (hmTotal != null && !hmTotal.containsKey(strSalaryId)) {
							if (hmTotalisDisplay != null && hmTotalisDisplay.containsKey(strSalaryId)) {
								pst = con.prepareStatement("insert into payroll_generation_lta (emp_id,month,year,pay_date,entry_date," +
									"salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, " +
									"service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, " +
									"paid_leaves, total_days,sal_effective_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
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
								pst.setInt(12, nEmpServiceId);
								pst.setString(13, "D");
								pst.setString(14, getStrPaycycleDuration());
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
								pst.setDouble(18, dblPresentDays);
								pst.setDouble(19, dblPaidDays);
								pst.setDouble(20, dblPaidLeaveDays);
								pst.setDouble(21, dbTotalDays);
								pst.setDate(22, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst.execute();
								pst.close();
							}
							continue;
						}

						pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, "
							+ "financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, " +
							"paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days,approve_by,approve_date,sal_effective_date) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
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
						pst.setInt(12, nEmpServiceId);
						pst.setString(13, "D");
						pst.setString(14, getStrPaycycleDuration());
						pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
						pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
						pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
						pst.setDouble(18, dblPresentDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPaidLeaveDays);
						pst.setDouble(21, dbTotalDays);
						pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(24, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
						pst.execute();
						pst.close();

						dblTotal -= uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));

						double dblAmt = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == LOAN && dblAmt > 0) {
							double dblBalanceAmt = 0;
							int nLoanId = 0;
							int nLoanAppId = 0;
							pst = con.prepareStatement(selectLoanPyroll2);
							pst.setInt(1, uF.parseToInt(strEmpId));
							rs = pst.executeQuery();
							while (rs.next()) {
								dblBalanceAmt = rs.getDouble("balance_amount");
								nLoanId = rs.getInt("loan_id");
								nLoanAppId = rs.getInt("loan_applied_id");

								double dblAmt1 = uF.parseToDouble(hmLoanAmt.get(nLoanAppId + ""));
								dblBalanceAmt = dblBalanceAmt - dblAmt1;

								pst1 = con.prepareStatement(updateLoanPyroll1);

								pst1.setDouble(1, dblBalanceAmt);
								if (dblBalanceAmt > 0) {
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
								pst1.setDate(9, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst1.execute();
								pst1.close();
							}
							rs.close();
							pst.close();

							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, ""+Integer.parseInt(strEmpId));
							String strProcessMsg = uF.showData(strProcessByName, "")+" has approved salary of "+uF.showData(strEmpName, "") +" on " +
								""+uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat())+" " +
								""+uF.getTimeFormatStr(""+uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(Integer.parseInt(strEmpId));
							logDetails.setProcessType(L_APPROVE_SALARY);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(0);
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}
					
					/**
					 * Insert Arrear days salary
					 * */
					if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
						List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
						if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
						for(Map<String,String> hmApplyArear : alArrear) {
							String strArrearId = hmApplyArear.get("ARREAR_ID");
							double dblArrearDays = uF.parseToDouble(hmApplyArear.get("ARREAR_DAYS"));
							String strArrearPaycycleFrom = hmApplyArear.get("ARREAR_PAYCYCLE_FROM");
							String strArrearPaycycleTo = hmApplyArear.get("ARREAR_PAYCYCLE_TO");
							int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
							double dblArrearTotalDays = uF.parseToDouble(hmApplyArear.get("ARREAR_PAYCYCLE_TOTAL_DAYS"));
							
							List<String> alEarningHead = hmArrearEarningHead.get(strEmpId+"_"+nArrearPaycycle);
							if(alEarningHead == null) alEarningHead = new ArrayList<String>();
							List<String> alDeductionHead = hmArrearDeductionHead.get(strEmpId+"_"+nArrearPaycycle);
							if(alDeductionHead == null) alDeductionHead = new ArrayList<String>();
							Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
							if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
							
							for(String strSalaryId : alEarningHead) {
								pst = con.prepareStatement("insert into arrear_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, " +
									"financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, " +
									"payment_mode, present_days, paid_days, paid_leaves, total_days,approve_by,approve_date,arear_id,sal_effective_date) " +
									"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
								pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
								pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setInt(6, uF.parseToInt(strSalaryId));
								pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(strSalaryId)))));
								pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
								pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
								pst.setInt(12, nEmpServiceId);
								pst.setString(13, "E");
								pst.setString(14, getStrPaycycleDuration());
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
								pst.setDouble(18, dblArrearDays);
								pst.setDouble(19, dblArrearDays);
								pst.setDouble(20, 0.0d);
								pst.setDouble(21, dblArrearTotalDays);
								pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(24, uF.parseToInt(strArrearId));
								pst.setDate(25, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst.execute();
								pst.close();
							}
							
							for(String strSalaryId : alDeductionHead) {
								pst = con.prepareStatement("insert into arrear_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, " +
									"financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, " +
									"payment_mode, present_days, paid_days, paid_leaves, total_days,approve_by,approve_date,arear_id,sal_effective_date) " +
									"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
								pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
								pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setInt(6, uF.parseToInt(strSalaryId));
								pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(strSalaryId)))));
								pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
								pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
								pst.setInt(12, nEmpServiceId);
								pst.setString(13, "D");
								pst.setString(14, getStrPaycycleDuration());
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
								pst.setDouble(18, dblArrearDays);
								pst.setDouble(19, dblArrearDays);
								pst.setDouble(20, 0.0d);
								pst.setDouble(21, dblArrearTotalDays);
								pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(24, uF.parseToInt(strArrearId));
								pst.setDate(25, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst.execute();
								pst.close();
							}						
						}
					}
					/**
					 * 
					 * Insert Arrear days salary end
					 * 
					 * */
					
					objRunnable.setData(hmTotal, hmTotal, strEmpId, dblTotal, strOrgId);
					objRunnable.run1();
//					Thread t = new Thread(objRunnable);
//					t.start();

					Map<String, String> hmInnerCurrencyDetails = (Map<String, String>) hmCurrencyDetails.get(hmEmpCurrency.get(strEmpId));
					if (hmInnerCurrencyDetails == null) hmInnerCurrencyDetails = new HashMap<String, String>();
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
	
	

	private void viewApprovePayByGrade(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			List<List<String>> alPaycycleList = new ArrayList<List<String>>();
			for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>6 && i< 6) || (i < paycycleList.size() && paycycleList.size()<=6)); i++) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(paycycleList.get(i).getPaycycleId());
				innerList.add(paycycleList.get(i).getPaycycleName());
				String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
				List<String> alList = getApprovedEmpCount(uF, strTmp[0], strTmp[1]);
					innerList.add(alList.get(0));
					innerList.add(alList.get(1));
				alPaycycleList.add(innerList);
			}
			request.setAttribute("alPaycycleList", alPaycycleList);
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));

			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
			if (hmOrg == null) hmOrg = new HashMap<String, String>();

			Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);
			
			Map hmWorkLocationMap = CF.getWorkLocationMap(con);

			StringBuilder sbQuery = new StringBuilder();
//			if(getStrProcess() == null || getStrProcess().trim().equals("") || getStrProcess().trim().equalsIgnoreCase("null")) {
//				sbQuery.append("select count(eod.emp_id) as cnt from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
//						+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
//						+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
//
//				/*if (getF_level() != null && getF_level().length > 0) {
//					sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
//							+ StringUtils.join(getF_level(), ",") + ") ) ");
//				}*/
//				if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
//	            {
//	            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
//	            } else {
//	            	 if(getF_level()!=null && getF_level().length>0) {
//	                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//	                 }
//	            	 if(getF_grade()!=null && getF_grade().length>0) {
//	                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
//	                 }
//				}
//				
//				if (getF_employeType() != null && getF_employeType().length > 0) {
//					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
//				}
//				
//				if (getF_department() != null && getF_department().length > 0) {
//					sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
//				}
//
//				if (getF_service() != null && getF_service().length > 0) {
//					sbQuery.append(" and (");
//					for (int i = 0; i < getF_service().length; i++) {
//						sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
//
//						if (i < getF_service().length - 1) {
//							sbQuery.append(" OR ");
//						}
//					}
//					sbQuery.append(" ) ");
//
//				}
//				if (getStrPaycycleDuration() != null) {
//					sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
//				}
//
//				if (uF.parseToInt(getF_paymentMode()) > 0) {
//					sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
//				}
//				
//				
//				if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
//					sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
//				} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
//					sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
//				}
//
//				if (uF.parseToInt(getF_org()) > 0) {
//					sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
//				} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
//					sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
//				}
//				sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
//						+ "and paid_from = ? and paid_to=? group by emp_id) ");
//				pst = con.prepareStatement(sbQuery.toString());
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
////				System.out.println("pst==>"+pst);
//				rs = pst.executeQuery();
//				int nEmpCnt = 0;
//				while (rs.next()) {
//					nEmpCnt = uF.parseToInt(rs.getString("cnt"));					
//				}
//				rs.close();
//				pst.close();
//				request.setAttribute("nEmpCnt", ""+nEmpCnt);
//			}
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
					+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
					+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
//			sbQuery.append(" and at.emp_id = 2811 ");
			if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            } else {
            	 if(getF_level()!=null && getF_level().length>0) {
                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
                 }
            	 if(getF_grade()!=null && getF_grade().length>0) {
                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
                 }
			}
			
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
			}
			
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
			}

			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}
			
			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date=? "
				+ "and paid_from=? and paid_to=? group by emp_id) order by emp_fname, emp_lname, at.sal_effective_date");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
			System.out.println("ApPay/972--pst==>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alEmp = new ArrayList<Map<String, String>>();
			List<String> alEmpIds = new ArrayList<String>();
			Map<String, Map<String, String>> hmEmp = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmEmpGradeMap = new HashMap<String, String>();
			Map<String, String> hmEmpwiseEffectiveDateCnt = new HashMap<String, String>();
			List<String> alGradeId = new ArrayList<String>();
			while (rs.next()) {
				Map<String, String> hmEmpPay = new HashMap<String, String>();
				hmEmpPay.put("EMP_ID", rs.getString("emp_id"));
				hmEmpPay.put("EMPCODE", rs.getString("empcode"));

				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				/*String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()
						+ " " : "";*/
				
				String strEmpName = rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname");
				hmEmpPay.put("EMP_NAME", strEmpName);
				hmEmpPay.put("EMP_PAYMENT_MODE_ID", rs.getString("payment_mode"));
				hmEmpPay.put("EMP_PAYMENT_MODE", uF.showData(hmPaymentModeMap.get(rs.getString("payment_mode")), ""));
				hmEmpPay.put("EMP_BIRTH_DATE", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				hmEmpPay.put("EMP_JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));

				if (rs.getString("employment_end_date") != null) {
					hmEmpPay.put("EMP_END_DATE", uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
				}
				hmEmpPay.put("EMP_GENDER", rs.getString("emp_gender"));
				String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE,CF.getStrTimeZone());
				double dblYears = uF.parseToDouble(strDays) / 365;
				hmEmpPay.put("EMP_AGE", dblYears + "");

				hmEmpPay.put("EMP_IS_DISABLE_SAL_CALCULATE", rs.getString("is_disable_sal_calculate"));
				hmEmpPay.put("EMP_APPROVE_ATTENDANCE_ID", rs.getString("approve_attendance_id"));
				hmEmpPay.put("EMP_TOTAL_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("total_days"))));
				hmEmpPay.put("EMP_PAID_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_days"))));
				hmEmpPay.put("EMP_PRESENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("present_days"))));
				hmEmpPay.put("EMP_PAID_LEAVES", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_leaves"))));
				hmEmpPay.put("EMP_ABSENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("absent_days"))));
				String strEffectiveDate = rs.getString("sal_effective_date") != null ? uF.getDateFormat(rs.getString("sal_effective_date"), DBDATE, DATE_FORMAT) : "";
				hmEmpPay.put("SAL_EFFECTIVE_DATE", strEffectiveDate);

				if (rs.getString("service_id") != null) {
					String[] tempService = rs.getString("service_id").split(",");
					if (tempService.length > 0) {
						hmEmpPay.put("EMP_SERVICE_ID", tempService[0]);
					}
				}
				Map<String, String> hmWLocation = (Map)hmWorkLocationMap.get(rs.getString("wlocation_id"));
				hmEmpPay.put("EMP_WLOCATION_NAME", hmWLocation.get("WL_NAME"));
				alEmp.add(hmEmpPay);
				hmEmp.put(rs.getString("emp_id")+"_"+strEffectiveDate, hmEmpPay);

				int effectiveDtCnt = uF.parseToInt(hmEmpwiseEffectiveDateCnt.get(rs.getString("emp_id")));
				effectiveDtCnt++;
				hmEmpwiseEffectiveDateCnt.put(rs.getString("emp_id"), effectiveDtCnt+"");
				
				if (!alEmpIds.contains(rs.getString("emp_id"))) {
					alEmpIds.add(rs.getString("emp_id"));
				}
				
				hmEmpGradeMap.put(rs.getString("emp_id"), rs.getString("grade_id"));
				if (!alGradeId.contains(rs.getString("grade_id"))) {
					alGradeId.add(rs.getString("grade_id"));
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmp ===>> " + hmEmp);
			
			
			Map<String, String> hmEmpGradeWithEffectiveDate = new HashMap<String, String>();
			String strEmpActivity = ACTIVITY_PROMOTION_ID+","+ACTIVITY_INCREMENT_ID+","+ACTIVITY_CONFIRMATION_ID+","+ACTIVITY_DEMOTION_ID+","+ACTIVITY_LIFE_EVENT_ID;
			pst = con.prepareStatement("select * from employee_activity_details where activity_id in ("+strEmpActivity+") and effective_date between ? and ?");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				if (!alGradeId.contains(rs.getString("grade_id"))) {
					alGradeId.add(rs.getString("grade_id"));
				}
				hmEmpGradeWithEffectiveDate.put(rs.getString("emp_id")+"_"+rs.getString("effective_date"), rs.getString("grade_id"));
			}
			rs.close();
			pst.close();
			
			
//			 System.out.println("alEmp====>"+alEmp);
			int nGradeIdSize = alGradeId.size();
			if (alEmp.size() > 0 && alEmpIds.size() > 0 && nGradeIdSize > 0) {
				String strEmpIds = StringUtils.join(alEmpIds.toArray(), ",");
				String strGradeIds = StringUtils.join(alGradeId.toArray(), ",");
				
				Map<String, String> hmGradeVDARate = new HashMap<String, String>();
				pst = con.prepareStatement("select * from vda_rate_details vrd, grades_details gd where vrd.desig_id>0 and " +
					"gd.designation_id= vrd.desig_id and grade_id in("+strGradeIds+") and from_date=? and to_date=?");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()) {
					hmGradeVDARate.put(rs.getString("grade_id")+"_PROBATION", rs.getString("vda_amount_probation"));
					hmGradeVDARate.put(rs.getString("grade_id")+"_PERMANENT", rs.getString("vda_amount_permanent"));
					hmGradeVDARate.put(rs.getString("grade_id")+"_TEMPORARY", rs.getString("vda_amount_temporary"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpTypeStatus = new HashMap<String, String>();
				pst = con.prepareStatement("select emp_per_id,emp_status from employee_personal_details where is_alive is true and approved_flag = true");
				rs = pst.executeQuery();
				while(rs.next()) {
					hmEmpTypeStatus.put(rs.getString("emp_per_id"), rs.getString("emp_status"));
				}
				rs.close();
				pst.close();
				
				Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
				Map<String, String> hmSalaryDetails = new HashMap<String, String>();
				List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
				List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
				List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
				List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
				
				pst = con.prepareStatement("select * from salary_details where grade_id in("+strGradeIds+") and (is_delete is null or is_delete=false) " +
					"and (is_contribution is null or is_contribution=false) order by earning_deduction desc, weight, salary_head_id");
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
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
							if(!alEmpSalaryDetailsEarning.contains(rs.getString("salary_head_id"))) {
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}
							/*int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
	
							if (index >= 0) {
								alEmpSalaryDetailsEarning.remove(index);
								alEarningSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							} else {
								alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
							}
	
							alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));*/
						} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
							if(!alEmpSalaryDetailsDeduction.contains(rs.getString("salary_head_id"))) {
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}
							/*int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
							if (index >= 0) {
								alEmpSalaryDetailsDeduction.remove(index);
								alDeductionSalaryDuplicationTracer.remove(index);
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							} else {
								alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
							}
							alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));*/
						}
	
						hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
					}

				}
				rs.close();
				pst.close();
//				System.out.println("hmSalaryDetails1==>"+hmSalaryDetails1);

				Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
				Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
				Map<String, String> hmEmpStateMap = new HashMap<String, String>();
				CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
				Map<String, String> hmEmpFlatTDSDeduct = CF.getEmpFlatTDSDeduction(con, uF);
				
				Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
				pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				while (rs.next()) {
					hmOtherTaxDetails.put(rs.getString("state_id") + "_SERVICE_TAX", rs.getString("service_tax"));
					hmOtherTaxDetails.put(rs.getString("state_id") + "_EDU_TAX", rs.getString("education_tax"));
					hmOtherTaxDetails.put(rs.getString("state_id") + "_STD_TAX", rs.getString("standard_tax"));
					hmOtherTaxDetails.put(rs.getString("state_id") + "_FLAT_TDS", rs.getString("flat_tds"));

					hmOtherTaxDetails.put(rs.getString("state_id") + "_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
					hmOtherTaxDetails.put(rs.getString("state_id") + "_REBATE_AMOUNT", rs.getString("rebate_amt"));

					hmOtherTaxDetails.put(rs.getString("state_id") + "_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
					hmOtherTaxDetails.put(rs.getString("state_id") + "_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
					
					hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
					hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
					
					// dblInvestmentExemption = 100000;
				}
				rs.close();
				pst.close();

				pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				// System.out.println(" pst==>"+pst);
				Map<String, String> hmHRAExemption = new HashMap<String, String>();
				while (rs.next()) {
					hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
					hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
					hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
					hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
				}
				rs.close();
				pst.close();

				double dblInvestmentExemption = 0.0d;
				pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				// System.out.println(" pst==>"+pst);
				if (rs.next()) {
					dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
				}
				rs.close();
				pst.close();

				Map<String, Map<String, String>> hmEmpPaidSalary = new HashMap<String, Map<String, String>>();
				pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, emp_id from payroll_generation where financial_year_from_date=? " +
					" and financial_year_to_date =? and paycycle = ? and emp_id in (" + strEmpIds + ") group by salary_head_id, emp_id order by emp_id");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPC));
				rs = pst.executeQuery();
				String strEmpIdNew = null;
				String strEmpIdOld = null;
				Map<String, String> hmEmpPaidInner = new HashMap<String, String>();
				while (rs.next()) {
					strEmpIdNew = rs.getString("emp_id");

					if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
						hmEmpPaidInner = new HashMap<String, String>();
					}

					hmEmpPaidInner.put(rs.getString("salary_head_id"), rs.getString("amount"));

					hmEmpPaidSalary.put(rs.getString("emp_id"), hmEmpPaidInner);

					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();

//				Map<String, String> hmEmpExemptionsMap = getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//				Map<String, String> hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String, String> hmEmpRentPaidMap = getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String, String> hmEmpIncomeOtherSourcesMap = getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//				Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con);
				Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, getF_org());
				Map<String, String> hmLoanAmt = new HashMap<String, String>();
				List<String> alLoans = new ArrayList<String>();
				Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
//				Map<String, Map<String, String>> hmEmpPaidAmountDetails = getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
//				Map<String, String> hmFixedExemptions = getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);

//				Map<String, String> hmSalaryDetails = new HashMap<String, String>();
//				List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
//				List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
//				List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
//				List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
//				pst = con.prepareStatement("select * from salary_details where salary_head_id not in (" + GROSS + "," + CTC + "," + REIMBURSEMENT_CTC + ") and org_id =? "
//						+ "and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
//				pst.setInt(1, uF.parseToInt(getF_org()));
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
//						int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//
//						if (index >= 0) {
//							alEmpSalaryDetailsEarning.remove(index);
//							alEarningSalaryDuplicationTracer.remove(index);
//							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//						} else {
//							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//						}
//
//						alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//					} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
//						int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//						if (index >= 0) {
//							alEmpSalaryDetailsDeduction.remove(index);
//							alDeductionSalaryDuplicationTracer.remove(index);
//							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//						} else {
//							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//						}
//						alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//					}
//
//					hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
//				}
//				rs.close();
//				pst.close();

//				Map<String, Map<String, String>> hmEmpSalary = new LinkedHashMap<String, Map<String, String>>();
//				Map<String, String> hmEmpSalaryInner = new LinkedHashMap<String, String>();
//				pst = con.prepareStatement("select * from emp_salary_details esd, (select max(effective_date) as effective_date, emp_id from emp_salary_details "
//								+ "where effective_date<=? and is_approved= true group by emp_id) a where a.effective_date = esd.effective_date and esd.emp_id = a.emp_id "
//								+ "and a.emp_id in (select emp_id from employee_official_details where org_id = ? and emp_id in ("+ strEmpIds+ ")) "
//								+ "and esd.salary_head_id in (select salary_head_id from salary_details where org_id =? and (is_delete is null or is_delete=false)) order by a.emp_id, esd.earning_deduction desc");
//				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setInt(2, uF.parseToInt(getF_org()));
//				pst.setInt(3, uF.parseToInt(getF_org()));
//				// System.out.println("---------->"+pst);
//				rs = pst.executeQuery();
//				String strEmpIdNew1 = null;
//				String strEmpIdOld1 = null;
//				while (rs.next()) {
//					strEmpIdNew1 = rs.getString("emp_id");
//
//					if (!alEmp.contains(strEmpIdNew1)) {
//						continue;
//					}
//
//					if (strEmpIdNew1 != null && !strEmpIdNew1.equalsIgnoreCase(strEmpIdOld1)) {
//						hmEmpSalaryInner = new LinkedHashMap<String, String>();
//					}
//
//					if (strEmpIdNew1 != null && strEmpIdNew1.length() > 0) {
//						hmEmpSalary.put(strEmpIdNew1, hmEmpSalaryInner);
//					}
//
//					strEmpIdOld1 = strEmpIdNew1;
//				}
//				rs.close();
//				pst.close();

				Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
				if (hmArearAmountMap == null)hmArearAmountMap = new HashMap<String, Map<String, String>>();
				Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
				if (hmEmpServiceTaxMap == null)hmEmpServiceTaxMap = new HashMap<String, String>();
				Map<String, String> hmIncentives = CF.getIncentivesMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIncentives == null)hmIncentives = new HashMap<String, String>();
				Map<String, String> hmIndividualBonus = CF.getIndividualBonusMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIndividualBonus == null)hmIndividualBonus = new HashMap<String, String>();

				Map<String, String> hmIndividualOtherDeduction = CF.getIndividualOtherDeductionMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIndividualOtherDeduction == null)hmIndividualOtherDeduction = new HashMap<String, String>();
				Map<String, String> hmIndividualOtherEarning = CF.getIndividualOtherEarningMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIndividualOtherEarning == null)hmIndividualOtherEarning = new HashMap<String, String>();
				Map<String, String> hmIndividualOvertime = CF.getIndividualOvertimeMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIndividualOvertime == null)hmIndividualOvertime = new HashMap<String, String>();
				Map<String, String> hmIndividualTravelReimbursement = CF.getIndividualTravelReimbursementMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIndividualTravelReimbursement == null)hmIndividualTravelReimbursement = new HashMap<String, String>();
				Map<String, String> hmIndividualMobileReimbursement = CF.getIndividualMobileReimbursementMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIndividualMobileReimbursement == null)hmIndividualMobileReimbursement = new HashMap<String, String>();
				Map<String, String> hmIndividualOtherReimbursement = CF.getIndividualOtherReimbursementMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIndividualOtherReimbursement == null)hmIndividualOtherReimbursement = new HashMap<String, String>();
				Map<String, String> hmIndividualMobileRecovery = CF.getIndividualMobileRecoveryMap(con, uF, CF, strPC, strD1, strD2);
				if (hmIndividualMobileRecovery == null)hmIndividualMobileRecovery = new HashMap<String, String>();
				Map<String, String> hmReimbursement = CF.getReimbursementMap(con, uF, CF, strD1, strD2);
				if (hmReimbursement == null)hmReimbursement = new HashMap<String, String>();
				Map<String, String> hmVariables = new HashMap<String, String>();
				getVariableAmount(con, uF, hmVariables, strPC, strD1, strD2);
				
				Map<String, String> hmAnnualVariables = new HashMap<String, String>();
				getAnnualVariableAmount(con, uF, hmAnnualVariables, strD1, strD2, strPC);				
				
				Map<String, String> hmAllowance = new HashMap<String, String>();
				getAllowanceAmount(con, uF, hmAllowance, strD1, strD2, strPC);

				Map<String, String> hmPrevEmpTdsAmount = new HashMap<String, String>();
				Map<String, String> hmPrevEmpGrossAmount = new HashMap<String, String>();
				getPrevEmpTdsAmount(con, uF, strFinancialYearStart, strFinancialYearEnd, hmPrevEmpTdsAmount, hmPrevEmpGrossAmount);
				
				Map<String, String> hmAnnualVarPolicyAmount = CF.getAnnualVariablePolicyAmount(con, uF, strFinancialYearStart, strFinancialYearEnd);
				if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();

				Map<String, String> hmBasicSalaryMap = CF.getSpecificSalaryData(con, BASIC);
				if (hmBasicSalaryMap == null)hmBasicSalaryMap = new HashMap<String, String>();
				Map<String, String> hmDASalaryMap = CF.getSpecificSalaryData(con, DA);
				if (hmDASalaryMap == null)hmDASalaryMap = new HashMap<String, String>();
				LinkedHashMap<String, Map<String, String>> hmTotalSalary = new LinkedHashMap<String, Map<String, String>>();
				LinkedHashMap<String, Map<String, String>> hmTotalSalaryisDisplay = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> hmInnerTemp = new HashMap<String, String>();
				
//				pst = con.prepareStatement("select distinct(emp_id) as emp_id from arear_details where is_paid=false and arrear_type=1 and emp_id in ("+strEmpIds+") and paycycle<=?");
//				pst.setInt(1, uF.parseToInt(strPC));
				pst = con.prepareStatement("select distinct(emp_id) as emp_id from arear_details where is_paid=false " +
						"and ((arrear_type=1 and paycycle<=?) or (arrear_type=2 and is_approved=1 and effective_date<=?)) and emp_id in ("+strEmpIds+") ");
				pst.setInt(1, uF.parseToInt(strPC));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				rs = pst.executeQuery();
				List<String> alArrearEmp = new ArrayList<String>();
				while(rs.next()) {
					alArrearEmp.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
//				System.out.println("alArrearEmp==>"+alArrearEmp);
				
				Map<String, List<Map<String, String>>> hmEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
				Map<String, Map<String, String>> hmArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
				Map<String, List<String>> hmArrearEarningHead = new LinkedHashMap<String, List<String>>();
				Map<String, List<String>> hmArrearDeductionHead = new LinkedHashMap<String, List<String>>();
				Map<String, Map<String, String>> hmArrearEmployeePF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployerPF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployerESI = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployeeLWF = new LinkedHashMap<String, Map<String, String>>();
				
				Map<String, List<Map<String, String>>> hmIncrementEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
				Map<String, Map<String, String>> hmIncrementArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
				Map<String, List<String>> hmIncrementArrearEarningHead = new LinkedHashMap<String, List<String>>();
				Map<String, List<String>> hmIncrementArrearDeductionHead = new LinkedHashMap<String, List<String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployeePF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployerPF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployerESI = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployeeLWF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, List<String>> hmIncrementArrearPaycycle = new HashMap<String, List<String>>();
				
				int nAlEmp = alEmp.size();
				Map<String, String> hmEmpEffectDtCnt = new HashMap<String, String>();
				Map<String, String> hmEmpTotGross = new HashMap<String, String>();
				Map<String, String> hmEmpTotGrossIsDisplay = new HashMap<String, String>();
				Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
				for (int i = 0; i < nAlEmp; i++) {
					Map<String, String> hmEmpPay = alEmp.get(i);
					String strEmpId = hmEmpPay.get("EMP_ID");
					String strOrgId = hmEmpOrgId.get(strEmpId);
					String strSalEffectiveDate = hmEmpPay.get("SAL_EFFECTIVE_DATE");
					String strEmpJoiningDate = hmEmpPay.get("EMP_JOINING_DATE");
					String strSalCalStatus = hmEmpPay.get("EMP_IS_DISABLE_SAL_CALCULATE");
					int empEffectDtCnt = uF.parseToInt(hmEmpEffectDtCnt.get(strEmpId));
					empEffectDtCnt++;
					hmEmpEffectDtCnt.put(strEmpId, empEffectDtCnt+"");
					int nEmpwiseEffectDtCnt = uF.parseToInt(hmEmpwiseEffectiveDateCnt.get(strEmpId));
					
//					System.out.println("empEffectDtCnt ===>> " + empEffectDtCnt);
//					System.out.println("nEmpwiseEffectDtCnt ===>> " + nEmpwiseEffectDtCnt);
					
					int nEmpId = uF.parseToInt(strEmpId);
					String strLocation = hmEmpWlocationMap.get(strEmpId);
					String strLevel = hmEmpLevelMap.get(strEmpId);
					String strGrade = hmEmpGradeMap.get(strEmpId);
//					System.out.println("strGrade 0 ===>> " + strGrade);
					if(hmEmpGradeWithEffectiveDate != null && hmEmpGradeWithEffectiveDate.get(strEmpId+"_"+strSalEffectiveDate) != null) {
						strGrade = hmEmpGradeWithEffectiveDate.get(strEmpId+"_"+strSalEffectiveDate);
					}
//					System.out.println("strGrade 1 ===>> " + strGrade);
					String strEmpVDAAmount = hmGradeVDARate.get(strGrade+"_"+hmEmpTypeStatus.get(strEmpId));
					
					int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
					String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
					
					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strGrade);
					if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>();

					int nTotalNumberOfDaysForCalc = (int) uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"));
					double dblTotalPresentDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"));

					double dblIncrementBasic = getIncrementCalculationBasic(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart,
							strFinancialYearEnd, nPayMonth, CF);
					double dblIncrementDA = getIncrementCalculationDA(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart,
							strFinancialYearEnd, nPayMonth, CF);

					Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
					String strTmpDt = strD2;
					if(strSalEffectiveDate != null && !strSalEffectiveDate.equals("")) {
						Date dtSalEffectiveDate = uF.getDateFormatUtil(strSalEffectiveDate, DATE_FORMAT);
						Date dtEmpJoiningDate = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
						if(dtSalEffectiveDate.after(dtEmpJoiningDate)) {
							strTmpDt = strSalEffectiveDate;
						} else {
							strTmpDt = strEmpJoiningDate;
						}
					}
					hmInner = CF.getSalaryCalculationByGrade(con,hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strGrade, uF, CF, strTmpDt, hmSalInner, strEmpVDAAmount, strSalCalStatus);
//					System.out.println("hmInner ===>> " + hmInner);
					Map<String, String> hmPaidSalaryInner = hmEmpPaidSalary.get(strEmpId);

					Map<String, String> hmPerkAlignAmount = new HashMap<String, String>();
					CF.getPerkAlignAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel),hmPerkAlignAmount);

					double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
					double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
					
					if(alArrearEmp.contains(strEmpId) && hmInner!=null && hmInner.containsKey(""+AREARS)) {
						ArrearPay arrearPay = new ArrearPay();
						arrearPay.request = request;
						arrearPay.session = session;
						arrearPay.CF = CF;
						arrearPay.setStrEmpId(""+nEmpId);
						arrearPay.hmOrg = hmOrg;
						arrearPay.hmPaymentModeMap = hmPaymentModeMap;
						arrearPay.setStrLocation(strLocation);
						arrearPay.setStrLevel(strLevel);
						arrearPay.setStrEmpGender(strEmpGender);
						arrearPay.hmSalInner = hmSalInner;
						arrearPay.hmEmpServiceTaxMap = hmEmpServiceTaxMap;
						arrearPay.hmEmpStateMap = hmEmpStateMap;
						arrearPay.hmOtherTaxDetails = hmOtherTaxDetails;
						
						arrearPay.payArrearByGrade(con, uF, strD1, strD2, strPC,hmEmpArrear,hmArrearCalSalary,hmArrearEarningHead,
								hmArrearDeductionHead, hmArrearEmployeePF, hmArrearEmployerPF, hmArrearEmployerESI, hmArrearEmployeeLWF);
					}
					
					if (hmIndividualOtherEarning.size() > 0 && !hmInner.containsKey(OTHER_EARNING + "")) {
						hmInnerTemp = new HashMap<String, String>();
						hmInnerTemp.put("AMOUNT", "0");
						hmInnerTemp.put("EARNING_DEDUCTION", "E");
						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
						hmInner.put(OTHER_EARNING + "", hmInnerTemp);
					}

//					if (hmIndividualOtherReimbursement.size() > 0 && !hmInner.containsKey(OTHER_REIMBURSEMENT + "")) {
//						hmInnerTemp = new HashMap<String, String>();
//						hmInnerTemp.put("AMOUNT", "0");
//						hmInnerTemp.put("EARNING_DEDUCTION", "E");
//						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//						hmInner.put(OTHER_REIMBURSEMENT + "", hmInnerTemp);
//					}
					
//					if (hmEmpServiceTaxMap.size() > 0 && !hmInner.containsKey(SERVICE_TAX + "")) {
//						hmInnerTemp = new HashMap<String, String>();
//						hmInnerTemp.put("AMOUNT", "0");
//						hmInnerTemp.put("EARNING_DEDUCTION", "E");
//						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//						hmInner.put(SERVICE_TAX + "", hmInnerTemp);
//
//						hmInnerTemp = new HashMap<String, String>();
//						hmInnerTemp.put("AMOUNT", "0");
//						hmInnerTemp.put("EARNING_DEDUCTION", "E");
//						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//						hmInner.put(SWACHHA_BHARAT_CESS + "", hmInnerTemp);
//
//						hmInnerTemp = new HashMap<String, String>();
//						hmInnerTemp.put("AMOUNT", "0");
//						hmInnerTemp.put("EARNING_DEDUCTION", "E");
//						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
//						hmInner.put(KRISHI_KALYAN_CESS + "", hmInnerTemp);
//					}
					
					if (hmEmpServiceTaxMap.size() > 0 && !hmInner.containsKey(CGST + "")) {
						hmInnerTemp = new HashMap<String, String>();
						hmInnerTemp.put("AMOUNT", "0");
						hmInnerTemp.put("EARNING_DEDUCTION", "E");
						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
						hmInner.put(CGST + "", hmInnerTemp);
						
						if(!alEmpSalaryDetailsEarning.contains(""+CGST)) {
							alEmpSalaryDetailsEarning.add(""+CGST);
							hmSalaryDetails.put(""+CGST, "CGST");
						}

						hmInnerTemp = new HashMap<String, String>();
						hmInnerTemp.put("AMOUNT", "0");
						hmInnerTemp.put("EARNING_DEDUCTION", "E");
						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
						hmInner.put(SGST + "", hmInnerTemp);
						
						if(!alEmpSalaryDetailsEarning.contains(""+SGST)) {
							alEmpSalaryDetailsEarning.add(""+SGST);
							hmSalaryDetails.put(""+SGST, "SGST");
						}
					}

					if (hmInner.size() > 0 && hmInner.containsKey(TDS + "")) {
						hmInnerTemp = new HashMap<String, String>();
						hmInnerTemp = hmInner.get(TDS + "");
						hmInnerTemp.put("AMOUNT", hmInnerTemp.get("AMOUNT"));
						hmInnerTemp.put("EARNING_DEDUCTION", "D");
						hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
						hmInner.remove(TDS + "");
						hmInner.put(TDS + "", hmInnerTemp);
					}

					Map<String, String> hmTotal = new HashMap<String, String>();
					Iterator<String> it = hmInner.keySet().iterator();

					double dblGrossPT = 0;
					double dblGrossForPT = uF.parseToDouble(hmEmpTotGross.get(strEmpId));
					double dblGross = 0;
					double dblGrossTDS = 0;
					double dblDeduction = 0;
					// double dblPerkTDS = 0.0d;
					Set<String> setContriSalHead = new HashSet<String>();
					
					while (it.hasNext()) {
						String strSalaryId = it.next();
						int nSalayHead = uF.parseToInt(strSalaryId);

						Map<String, String> hm = hmInner.get(strSalaryId);
						if (hm == null) hm = new HashMap<String, String>();
						
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
//						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E") && (hm.get("SALARY_AMOUNT_TYPE") != null && !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")) {

							if (hmPaidSalaryInner != null) {
								dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossForPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {

								if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
									
									switch (nSalayHead) {
										case OVER_TIME :
	
											double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
											dblGross += dblOverTime;
											dblGrossForPT += dblOverTime;
											dblGrossTDS += dblOverTime;
	
											break;
	
										case LEAVE_ENCASHMENT :
											double leaveEncashmentAmt = 0.0d;//getLeaveEncashmentAmtDetailsByGrade(con, uF, CF, strEmpId, 0.0d, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, strGrade, dblIncrementBasic, dblIncrementDA);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),leaveEncashmentAmt));
	
											dblGross += leaveEncashmentAmt;
											dblGrossForPT += leaveEncashmentAmt;
											dblGrossTDS += leaveEncashmentAmt;
	
											break;
	
										case BONUS :
											double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
											//System.out.println("AP/1641---dblBonusAmount="+dblBonusAmount);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
											dblGross += dblBonusAmount;
											dblGrossForPT += dblBonusAmount;
											dblGrossTDS += dblBonusAmount;
	
											break;
	
										case EXGRATIA :
	
											double dblExGratiaAmount = getExGratiaAmount(con, uF, CF, strEmpId, strD1, strD2, strPC);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
											dblGross += dblExGratiaAmount;
											dblGrossForPT += dblExGratiaAmount;
											dblGrossTDS += dblExGratiaAmount;
	
											break;
	
										case AREARS :
	
											double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblArearAmount += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get("GROSS"))));
												}	
											}
											
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
											dblGross += dblArearAmount;
											dblGrossForPT += dblArearAmount;
											dblGrossTDS += dblArearAmount;
	
											break;
	
										case INCENTIVES :
											double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmIncentives, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
											dblGross += dblIncentiveAmount;
											dblGrossForPT += dblIncentiveAmount;
											dblGrossTDS += dblIncentiveAmount;
											break;
	
										case REIMBURSEMENT :
											double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
											dblGross += dblReimbursementAmount;
											dblGrossForPT += dblReimbursementAmount;
											break;
	
										case TRAVEL_REIMBURSEMENT :
											double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
											dblGross += dblTravelReimbursementAmount;
											dblGrossForPT += dblTravelReimbursementAmount;
											break;
	
										case MOBILE_REIMBURSEMENT :
											double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
											dblGross += dblMobileReimbursementAmount;
											dblGrossForPT += dblMobileReimbursementAmount;
											break;
	
										case OTHER_REIMBURSEMENT :
											double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
											dblGross += dblOtherReimbursementAmount;
											dblGrossForPT += dblOtherReimbursementAmount;
											break;
	
										case OTHER_EARNING :
											double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
											dblGross += dblOtherEarningAmount;
											dblGrossForPT += dblOtherEarningAmount;
											break;
	
										case SERVICE_TAX :
											double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
	
											/**
											 * @author Vipin 25-Mar-2014 KP
											 *         Condition
											 * @comment = service tax is not
											 *          included while calculating
											 *          TDS
											 * */
	
											dblGross += dblServiceTaxAmount;
											dblGrossForPT += dblServiceTaxAmount;
											dblGrossPT += dblServiceTaxAmount;
											dblGrossTDS += dblServiceTaxAmount;
	
											break;
	
										case SWACHHA_BHARAT_CESS :
											double dblGrossAmt = dblGross;
											double dblServiceTaxAmt = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
											dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
											double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
											dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
	
											double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId),
													hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));
	
											dblGross += dblSwachhaBharatCess;
											dblGrossForPT += dblSwachhaBharatCess;
											dblGrossPT += dblSwachhaBharatCess;
											dblGrossTDS += dblSwachhaBharatCess;
	
											break;
	
										case KRISHI_KALYAN_CESS :
											double dblGrossAmt1 = dblGross;
											double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
											dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
											double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS + ""));
											dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
	
											double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId),
													hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
	
											dblGross += dblKrishiKalyanCess;
											dblGrossForPT += dblKrishiKalyanCess;
											dblGrossPT += dblKrishiKalyanCess;
											dblGrossTDS += dblKrishiKalyanCess;
	
											break;
											
										case CGST :
											double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
	
											dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
											dblGrossForPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
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
											dblGrossForPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
											dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
											dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
	
											break;	
	
										default :
	
											if (uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
												double dblPerkAlignAmount = 0.0d;
												if (hmPerkAlignAmount.containsKey(strSalaryId)) {
													dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
												}
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount));
												dblGross += dblPerkAlignAmount;
												dblGrossForPT += dblPerkAlignAmount;
												dblGrossPT += dblPerkAlignAmount;
												dblGrossTDS += dblPerkAlignAmount;
											} else if (!uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossForPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											} else if (uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
												if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
													dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossForPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												} else {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
													dblGross += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossForPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
												}
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossForPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
												dblGross += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossForPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											} else if (uF.parseToInt(strSalaryId) != GROSS) {
												boolean isMultipePerWithParticularHead = false;
												if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
													isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
												}
//												System.out.println(strSalaryId + " -- isMultipePerWithParticularHead ===>> " + isMultipePerWithParticularHead);
												if(!isMultipePerWithParticularHead || uF.parseToBoolean(strSalCalStatus)) {
//													System.out.println("strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
													dblGross += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossForPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
												}
											}
	
											break;
										}
								} else {
									if (uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
										
									} else if (!uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
										
									} else if (uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
										if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											
										} else {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossForPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										}
									} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
										
									} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
										
									} else if (uF.parseToInt(strSalaryId) != GROSS) {
										boolean isMultipePerWithParticularHead = false;
										if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
											isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
										}
//										System.out.println(strSalaryId + " --- in else E isMultipePerWithParticularHead ===>> " + isMultipePerWithParticularHead);
										if(!isMultipePerWithParticularHead || uF.parseToBoolean(strSalCalStatus)) {
//											System.out.println("in else E strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossForPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										}
									}
									
								}
							}

//						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE") != null && !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {	
							/**
							 * TAX CALCULATION STARTS HERE
							 * 
							 * */
							if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
							
								switch (nSalayHead) {
									/********** EPF EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_EPF :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
	
											Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");
	
											double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
											
											if(uF.parseToInt(strEmpId)==116){
												System.out.println("AP/1939---dblEEPF=="+dblEEPF);
											}
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
												}	
											}
											
											dblDeduction += dblEEPF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
										}
	
										break;
	
									/********** EPF EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_EPF :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
											dblDeduction += dblERPF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
										}
	
										break;
	
									case LOAN :
										if (hmPaidSalaryInner != null) {
											double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblLoan;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
											CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, strEmpId);
										} else {
	
											double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF,
													hmLoanAmt, hmEmpLoan, alLoans, strD1);
											dblDeduction += dblLoanAmt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
	
											if (true) {
												// dblGrossTDS = dblGross -
												// dblLoanAmt;
												dblGrossTDS = dblGrossTDS - dblLoanAmt;
											}
										}
	
										break;
	
									case MOBILE_RECOVERY :
										if (hmPaidSalaryInner != null) {
											double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblLoan;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
										} else {
											double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
											dblDeduction += dblIndividualMobileRecoveryAmt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
										}
	
										break;
	
									default :
										if (hmPaidSalaryInner != null) {
											dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
										} else {
	
											if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												dblDeduction += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"))));
												dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"));
											} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
//												System.out.println("Deduction strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
												dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));
											}
										}
										break;
									}
							} else {
								
								switch (nSalayHead) {
									/********** EPF EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_EPF :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
	
											Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");
	
											double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
											
											if(uF.parseToInt(strEmpId)==116){
												System.out.println("AP/2052---dblEEPF=="+dblEEPF);
											}
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
												}	
											}
											
											dblDeduction += dblEEPF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
										}
	
										break;
	
									/********** EPF EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_EPF :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
											dblDeduction += dblERPF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
										}
	
										break;
	
									default :
										
									if (hmPaidSalaryInner != null) {
										dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
									} else {
	
										if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											
										} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
											
										} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
											
										} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
												&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
												&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
//											System.out.println("in else D Deduction strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
											dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));
										}
									}
								}
							}
						}
						hmTotal.put("SALARY_HEAD_ID", strSalaryId);
						hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
					}

					
					Map<String, String> hmContriSalHeadAmt = new HashMap<String, String>();
					if(!uF.parseToBoolean(strSalCalStatus)) {
						/**
						 * Multiple cal start
						 * */
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
							double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
							hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
						}
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
							double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC, hmTotal);
							hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
						}
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
							double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
							hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
						}
						Iterator<String> itMulti = hmInner.keySet().iterator();
						while (itMulti.hasNext()) {
							String strSalaryId = itMulti.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
							Map<String, String> hm = hmInner.get(strSalaryId);
							if (hm == null) {
								hm = new HashMap<String, String>();
							}
							String str_E_OR_D = hm.get("EARNING_DEDUCTION");
							List<String> alCheckVDAInFormula = new ArrayList<String>();
	//						System.out.println("MULTIPLE_CALCULATION ===>> " + hm.get("MULTIPLE_CALCULATION"));
							if(hm.get("MULTIPLE_CALCULATION") != null) {
								alCheckVDAInFormula = Arrays.asList(hm.get("MULTIPLE_CALCULATION").split(","));
							}
	//						System.out.println("alCheckVDAInFormula ===>> " + alCheckVDAInFormula);
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
								if (hmPaidSalaryInner != null) {
									dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossForPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
								} else {
	//								System.out.println("before -- multi head formula Earning strSalaryId ===>> " + strSalaryId);
									double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional, hmContriSalHeadAmt);
									if(!hmTotal.containsKey(strSalaryId) || alCheckVDAInFormula.contains(""+VDA)) {
	//									System.out.println("multi head formula Earning strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblMulCalAmt);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblMulCalAmt));
										dblGross += dblMulCalAmt;
										dblGrossForPT += dblMulCalAmt;
										dblGrossTDS += dblMulCalAmt;
									}
									
								}
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")
									&& (hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
								if (hmPaidSalaryInner != null) {
									dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional, hmContriSalHeadAmt);
									if(!hmTotal.containsKey(strSalaryId) || alCheckVDAInFormula.contains(""+VDA)) {
	//									System.out.println("multi head formula Deduction strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblMulCalAmt));
										dblDeduction += dblMulCalAmt;
									}
								}
							}
	
							hmTotal.put("SALARY_HEAD_ID", strSalaryId);
							hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross)));
						}
						/**
						 * Multiple cal end
						 * */
					}
					
					
					/**
					 * Other cal start
					 * */
					Iterator<String> itOther = hmInner.keySet().iterator();
					while (itOther.hasNext()) {
						String strSalaryId = itOther.next();
						int nSalayHead = uF.parseToInt(strSalaryId);

						Map<String, String> hm = hmInner.get(strSalaryId);
						if (hm == null) {
							hm = new HashMap<String, String>();
						}
						String str_E_OR_D = hm.get("EARNING_DEDUCTION");
//						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE") != null && !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
							/**
							 * TAX CALCULATION STARTS HERE
							 * 
							 * */

							switch (nSalayHead) {
								/********** TAX *************/
								case PROFESSIONAL_TAX :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									} else {
										/**
										 * KP Condition
										 * 
										 * */
										if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
											double dblPt = calculateProfessionalTax(con, uF, strD2, dblGrossForPT, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
											dblDeduction += dblPt;
//											System.out.println("PROFESSIONAL_TAX strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblPt);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblPt));
										}
									}

									break;

								/********** TDS *************/
								case TDS :
									if (hmPaidSalaryInner != null) {
										double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblTDS;
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
									} else {
										if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
	//										double dblBasic = uF.parseToDouble(hmTotal.get(BASIC + ""));
	//										double dblDA = uF.parseToDouble(hmTotal.get(DA + ""));
	//										double dblHRA = uF.parseToDouble(hmTotal.get(HRA + ""));
	//
	//										String[] hraSalaryHeads = null;
	//										if (((String) hmHRAExemption.get("SALARY_HEAD_ID")) != null) {
	//											hraSalaryHeads = ((String) hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
	//										}
	//
	//										double dblHraSalHeadsAmount = 0;
	//										for (int j = 0; hraSalaryHeads != null && j < hraSalaryHeads.length; j++) {
	//											dblHraSalHeadsAmount += uF.parseToDouble((String) hmTotal.get(hraSalaryHeads[j]));
	//										}
	//
	//										Map<String, String> hmPaidSalaryDetails = hmEmpPaidAmountDetails.get(strEmpId);
	//										if (hmPaidSalaryDetails == null) {
	//											hmPaidSalaryDetails = new HashMap<String, String>();
	//										}
	//
	//										double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_EDU_TAX"));
	//										double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_STD_TAX"));
											double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_FLAT_TDS"));
											String strEmpFlatTDS = hmEmpFlatTDSDeduct.get(strEmpId);
											if(strEmpFlatTDS!=null) {
												dblFlatTDS = uF.parseToDouble(strEmpFlatTDS);	
											}
											if (hmEmpServiceTaxMap.containsKey(strEmpId)) {
												dblGrossTDS = dblGross;
												
	//											double dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
	//											dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
	//
	//											double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS + ""));
	//											dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
	//
	//											double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
	//											dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
												
												double dblCGST = uF.parseToDouble(hmTotal.get(CGST + ""));
												dblGrossTDS = dblGrossTDS - dblCGST;
	
												double dblSGST = uF.parseToDouble(hmTotal.get(SGST + ""));
												dblGrossTDS = dblGrossTDS - dblSGST;
											}
	//
	//										/**
	//										 * (dblBasic + dblDA) we use
	//										 * dblHraSalHeadsAmount
	//										 * */
	//										double dblTDS = calculateTDS(con, uF, strD2, strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS,
	//												dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount, nPayMonth, strD1, strFinancialYearStart,
	//												strFinancialYearEnd, strEmpId, hmEmpPay.get("EMP_GENDER"), hmEmpPay.get("EMP_AGE"),
	//												hmEmpStateMap.get(strEmpId), hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap,
	//												hmEmpRentPaidMap, hmPaidSalaryDetails, hmTotal, hmSalaryDetails, hmEmpLevelMap, CF, hmPrevEmpTdsAmount,
	//												hmPrevEmpGrossAmount, hmEmpIncomeOtherSourcesMap, hmOtherTaxDetails, hmEmpStateMap);
											double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
													strFinancialYearEnd, strEmpId, hmEmpLevelMap);
	
											dblDeduction += dblTDS;
//											System.out.println("TDS strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblTDS);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblTDS));
										}
									}
									break;
									
									/********** ESI EMPLOYER CONTRIBUTION *************/
								case EMPLOYER_ESI :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
									} else {
										double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,
												hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC, hmTotal);
										
										dblESI = Math.ceil(dblESI);
										
										dblDeduction += dblESI;
//										System.out.println("EMPLOYER_ESI strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblESI);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
									}

									break;
								/********** /ESI EMPLOYER CONTRIBUTION *************/

								/********** ESI EMPLOYEE CONTRIBUTION *************/
								case EMPLOYEE_ESI :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									} else {
										double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId,hmAnnualVariables, strD1, strD2, strPC, hmTotal);
										
										if(uF.parseToInt(strEmpId)==384){
											System.out.println("AP/2342---dblESI=="+dblESI);
										}
										if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
											List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
											if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
											for(Map<String,String> hmApplyArear : alArrear) {
												int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
												
												Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
												if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
												
												dblESI += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_ESI))));
											}	
										}
										
										dblESI = Math.ceil(dblESI);
										
										dblDeduction += dblESI;
//										System.out.println("EMPLOYEE_ESI strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblESI);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
									}

									break;
								/********** /ESI EMPLOYEE CONTRIBUTION *************/

								/********** LWF EMPLOYER CONTRIBUTION *************/
								case EMPLOYER_LWF :
									if (hmPaidSalaryInner != null) {
										double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblLWF;
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
									} else {
										double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
										dblDeduction += dblLWF;
//										System.out.println("EMPLOYER_LWF strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblLWF);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
									}

									break;
								/********** /LWF EMPLOYER CONTRIBUTION *************/

								/********** LWF EMPLOYEE CONTRIBUTION *************/
								case EMPLOYEE_LWF :
									if (hmPaidSalaryInner != null) {
										double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblLWF;
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
									} else {
										double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, hmAnnualVariables, strOrgId);
//										System.out.println("EMPLOYER_LWF strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblLWF);
										if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
											List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
											if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
											for(Map<String,String> hmApplyArear : alArrear) {
												int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
												
												Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
												if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
												
												dblLWF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_LWF))));
											}	
										}
										
										dblDeduction += dblLWF;
//										System.out.println("EMPLOYEE_LWF strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + dblLWF);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
									}

									break;
								/********** /LWF EMPLOYEE CONTRIBUTION *************/	

							}
						}

						hmTotal.put("SALARY_HEAD_ID", strSalaryId);
						hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
					}
					/**
					 * Other cal end
					 * */

					String strCurrencyId = hmEmpCurrency.get(strEmpId);
					Map<String, String> hmCurrency = hmCurrencyDetails.get(strCurrencyId);
					if (hmCurrency == null) hmCurrency = new HashMap<String, String>();
					if(uF.parseToInt(strEmpId) == 419) {
						System.out.println("AP/2420--dblGross==>"+dblGross+"--dblDeduction==>"+dblDeduction);
					}
					hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction)));

					hmTotalSalary.put(strEmpId+"_"+strSalEffectiveDate, hmTotal);
//					System.out.println("strEmpId ===>> " + strEmpId + " == hmTotalSalary ===>> " + hmTotalSalary);
					hmEmpTotGross.put(strEmpId, ""+dblGrossForPT);
					
					
					// =========================code for isdisplay false=======================
					Map<String, String> hmTotalisDisplay = new HashMap<String, String>();
					Iterator<String> it2 = hmInnerisDisplay.keySet().iterator();
					dblGross = 0.0d;
					dblGrossForPT = uF.parseToDouble(hmEmpTotGrossIsDisplay.get(strEmpId)); 
					dblGrossTDS = 0.0d;
					dblDeduction = 0.0d;
					while (it2.hasNext()) {
						String strSalaryId = it2.next();
						int nSalayHead = uF.parseToInt(strSalaryId);

						Map<String, String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
						if (hmisDisplay == null) hmisDisplay = new HashMap<String, String>();

						String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");

//						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")) {
						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")) {

							if (hmPaidSalaryInner != null) {
								dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossForPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
									switch (nSalayHead) {
									/********** OVER TIME *************/
									case OVER_TIME :

										double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
										dblGross += dblOverTime;
										dblGrossForPT += dblOverTime;
										dblGrossTDS += dblOverTime;

										break;

									case BONUS :
										double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
										//System.out.println("AP/2462--dblBonusAmount="+dblBonusAmount);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
										dblGross += dblBonusAmount;
										dblGrossForPT += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;

										break;

									case EXGRATIA :
										double dblExGratiaAmount = getExGratiaAmount(con, uF, CF, strEmpId, strD1, strD2, strPC);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
										dblGross += dblExGratiaAmount;
										dblGrossForPT += dblExGratiaAmount;
										dblGrossTDS += dblExGratiaAmount;

										break;

									case AREARS :
										double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart,
												strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
										if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
											List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
											if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
											for(Map<String,String> hmApplyArear : alArrear) {
												int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
												
												Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
												if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
												
												dblArearAmount += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get("GROSS"))));
											}	
										}
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
										dblGross += dblArearAmount;
										dblGrossForPT += dblArearAmount;
										dblGrossTDS += dblArearAmount;

										break;

									case INCENTIVES :
										double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
												strFinancialYearEnd, nPayMonth, hmIncentives, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
										dblGross += dblIncentiveAmount;
										dblGrossForPT += dblIncentiveAmount;
										dblGrossTDS += dblIncentiveAmount;

										break;

									case REIMBURSEMENT :
										double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
										dblGross += dblReimbursementAmount;
										dblGrossForPT += dblReimbursementAmount;
										break;

									case TRAVEL_REIMBURSEMENT :
										double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
												hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
										dblGross += dblTravelReimbursementAmount;
										dblGrossForPT += dblTravelReimbursementAmount;
										break;

									case MOBILE_REIMBURSEMENT :
										double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
												hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
										dblGross += dblMobileReimbursementAmount;
										dblGrossForPT += dblMobileReimbursementAmount;
										break;

									case OTHER_REIMBURSEMENT :
										double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
												hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
										dblGross += dblOtherReimbursementAmount;
										dblGrossForPT += dblOtherReimbursementAmount;
										break;

									case OTHER_EARNING :
										double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
										dblGross += dblOtherEarningAmount;
										dblGrossForPT += dblOtherEarningAmount;
										break;

									case SERVICE_TAX :

										double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId),
												hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap,
												CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));

										/**
										 * @author Vipin 25-Mar-2014 KP
										 *         Condition
										 * @comment = service tax is not
										 *          included while calculating
										 *          TDS
										 * */

										dblGross += dblServiceTaxAmount;
										dblGrossForPT += dblServiceTaxAmount;
										dblGrossPT += dblServiceTaxAmount;
										dblGrossTDS += dblServiceTaxAmount;

										break;

									case SWACHHA_BHARAT_CESS :
										double dblGrossAmt = dblGross;
										double dblServiceTaxAmt = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
										dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
										double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
										dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;

										double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId),
												hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap,CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));

										dblGross += dblSwachhaBharatCess;
										dblGrossForPT += dblSwachhaBharatCess;
										dblGrossPT += dblSwachhaBharatCess;
										dblGrossTDS += dblSwachhaBharatCess;

										break;

									case KRISHI_KALYAN_CESS :
										double dblGrossAmt1 = dblGross;
										double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
										dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
										double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS + ""));
										dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;

										double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId),
												hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));

										dblGross += dblKrishiKalyanCess;
										dblGrossForPT += dblKrishiKalyanCess;
										dblGrossPT += dblKrishiKalyanCess;
										dblGrossTDS += dblKrishiKalyanCess;

										break;
									
									case CGST :
										double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

										dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
										dblGrossForPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
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
										dblGrossForPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
										dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
										dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

										break;	

									default :

										if (uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
											double dblPerkAlignAmount = 0.0d;
											if (hmPerkAlignAmount.containsKey(strSalaryId)) {
												dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
											}
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount));
											dblGross += dblPerkAlignAmount;
											dblGrossForPT += dblPerkAlignAmount;
											dblGrossPT += dblPerkAlignAmount;
											dblGrossTDS += dblPerkAlignAmount;
										} else if (!uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossForPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else if (uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
											if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossForPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											} else {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
												dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossForPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											}
										} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
											dblGrossForPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
										}  else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
											hmTotalisDisplay.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
											dblGross += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											dblGrossForPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
										} else if (uF.parseToInt(strSalaryId) != GROSS) {
											boolean isMultipePerWithParticularHead = false;
											if(hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
												isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
											}
											if(!isMultipePerWithParticularHead) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
												dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossForPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											}
											
										}

										break;
									}
								} else {
									if (uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
										
									} else if (!uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
										
									} else if (uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
										if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											
										} else {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossForPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
									} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
										
									}  else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
										
									} else if (uF.parseToInt(strSalaryId) != GROSS) {
										boolean isMultipePerWithParticularHead = false;
										if(hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
											isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
										}
										if(!isMultipePerWithParticularHead) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
											dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossForPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
									}
									
								}
							}

//						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D") && (hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
							if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
								/**
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */

								switch (nSalayHead) {
								/********** EPF EMPLOYEE CONTRIBUTION *************/
								case EMPLOYEE_EPF :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									} else {
										Map<String, String> hmVoluntaryPF = hmInnerisDisplay.get(VOLUNTARY_EPF + "");

										double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
										
										if(uF.parseToInt(strEmpId)==116){
											System.out.println("AP/2763---dblEEPF=="+dblEEPF);
										}
										
										if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
											List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
											if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
											for(Map<String,String> hmApplyArear : alArrear) {
												int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
												
												Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
												if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
												
												dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
											}	
										}
										
										dblDeduction += dblEEPF;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
									}

									break;

								/********** EPF EMPLOYER CONTRIBUTION *************/
								case EMPLOYER_EPF :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
									} else {
										double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
										dblDeduction += dblERPF;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
									}

									break;

								case LOAN :
									if (hmPaidSalaryInner != null) {
										double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblLoan;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
										CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, strEmpId);
									} else {
										double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans, strD1);
										dblDeduction += dblLoanAmt;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
										if (true) {
											// dblGrossTDS = dblGross -
											// dblLoanAmt;
											dblGrossTDS = dblGrossTDS - dblLoanAmt;
										}
									}

									break;

								case MOBILE_RECOVERY :
									if (hmPaidSalaryInner != null) {
										double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblLoan;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
									} else {
										double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap,
												hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
										dblDeduction += dblIndividualMobileRecoveryAmt;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
									}

									break;

								default :
									if (hmPaidSalaryInner != null) {
										dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										hmTotalisDisplay.put(strSalaryId, hmPaidSalaryInner.get(strSalaryId));
									} else {

										if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
											dblDeduction += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
										} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"))));
											dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"));
										} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
												&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
												&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
											dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
									}
									break;
								}
							} else {

								/**
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */

								switch (nSalayHead) {
								/********** EPF EMPLOYEE CONTRIBUTION *************/
								case EMPLOYEE_EPF :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									} else {
										Map<String, String> hmVoluntaryPF = hmInnerisDisplay.get(VOLUNTARY_EPF + "");

										double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
										
										if(uF.parseToInt(strEmpId)==116){
											System.out.println("AP/2876---dblEEPF=="+dblEEPF);
										}
										
										if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
											List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
											if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
											for(Map<String,String> hmApplyArear : alArrear) {
												int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
												
												Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
												if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
												
												dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
											}	
										}
										
										dblDeduction += dblEEPF;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
									}

									break;

								/********** EPF EMPLOYER CONTRIBUTION *************/
								case EMPLOYER_EPF :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
									} else {
										double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
										dblDeduction += dblERPF;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
									}

									break;


								default :
									if (hmPaidSalaryInner != null) {
										dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										hmTotalisDisplay.put(strSalaryId, hmPaidSalaryInner.get(strSalaryId));
									} else {

										if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
											dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
										} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
											dblDeduction += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
										} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"))));
											dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"));
										} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
												&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
												&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
											dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
										}
									}
									break;
								}
							
							}
						}

						hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
						hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
					}

					
					if(!uF.parseToBoolean(strSalCalStatus)) {
						/**
						 * Multiple cal for isDisplay false start
						 * */
	
						Iterator<String> itMultiIsDisplay = hmInnerisDisplay.keySet().iterator();
						while (itMultiIsDisplay.hasNext()) {
							String strSalaryId = itMultiIsDisplay.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
							Map<String, String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
							if (hmisDisplay == null) {
								hmisDisplay = new HashMap<String, String>();
							}
	
							String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")
									&& (hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
								if (hmPaidSalaryInner != null) {
									dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossForPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount, dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
									if(!hmTotalisDisplay.containsKey(strSalaryId)) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
										dblGross += dblMulCalAmt;
										dblGrossForPT += dblMulCalAmt;
										dblGrossTDS += dblMulCalAmt;
									}
								}
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")
									&& (hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
								if (hmPaidSalaryInner != null) {
									dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount, dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
									if(!hmTotalisDisplay.containsKey(strSalaryId)) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
										dblDeduction += dblMulCalAmt;
									}
								}
							}
	
							hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
							hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
	
						/**
						 * Multiple cal isDisplay false start
						 * */
					}
					
					
					/**
					 * Other cal isDisplay start
					 * */
					Iterator<String> itIsDisplayOther = hmInnerisDisplay.keySet().iterator();
					while (itIsDisplayOther.hasNext()) {
						String strSalaryId = itIsDisplayOther.next();
						int nSalayHead = uF.parseToInt(strSalaryId);

						Map<String, String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
						if (hmisDisplay == null) {
							hmisDisplay = new HashMap<String, String>();
						}

						String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
//						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D") && (hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && !hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
							/**
							 * TAX CALCULATION STARTS HERE
							 * 
							 * */

							switch (nSalayHead) {
								/********** TAX *************/
								case PROFESSIONAL_TAX :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									} else {
										/**
										 * KP Condition
										 * 
										 * */
										// double dblPt =
										// calculateProfessionalTax(con, uF,
										// strD2,dblGrossPT,
										// strFinancialYearEnd, nPayMonth,
										// hmEmpStateMap.get(strEmpId));
										if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
											double dblPt = calculateProfessionalTax(con, uF, strD2, dblGrossForPT, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										}
									}

									break;

								/********** TDS *************/
								case TDS :
									if (hmPaidSalaryInner != null) {
										double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblTDS;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
									} else {
										if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
	//										double dblBasic = uF.parseToDouble(hmTotalisDisplay.get(BASIC + ""));
	//										double dblDA = uF.parseToDouble(hmTotalisDisplay.get(DA + ""));
	//										double dblHRA = uF.parseToDouble(hmTotalisDisplay.get(HRA + ""));
	//
	//										String[] hraSalaryHeads = null;
	//										if (((String) hmHRAExemption.get("SALARY_HEAD_ID")) != null) {
	//											hraSalaryHeads = ((String) hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
	//										}
	//
	//										double dblHraSalHeadsAmount = 0;
	//										for (int j = 0; hraSalaryHeads != null && j < hraSalaryHeads.length; j++) {
	//											dblHraSalHeadsAmount += uF.parseToDouble((String) hmTotalisDisplay.get(hraSalaryHeads[j]));
	//										}
	//
	//										Map<String, String> hmPaidSalaryDetails = hmEmpPaidAmountDetails.get(strEmpId);
	//										if (hmPaidSalaryDetails == null) {
	//											hmPaidSalaryDetails = new HashMap<String, String>();
	//										}
	//
	//										double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_EDU_TAX"));
	//										double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_STD_TAX"));
											double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_FLAT_TDS"));
											String strEmpFlatTDS = hmEmpFlatTDSDeduct.get(strEmpId);
											if(strEmpFlatTDS!=null) {
												dblFlatTDS = uF.parseToDouble(strEmpFlatTDS);	
											}
											if (hmEmpServiceTaxMap.containsKey(strEmpId)) {
												dblGrossTDS = dblGross;
												
	//											double dblServiceTaxAmount = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX + ""));
	//											dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
	//
	//											double dblSwachhaBharatCess = uF.parseToDouble(hmTotalisDisplay.get(SWACHHA_BHARAT_CESS + ""));
	//											dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
	//
	//											double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
	//											dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
												
												double dblCGST = uF.parseToDouble(hmTotalisDisplay.get(CGST + ""));
												dblGrossTDS = dblGrossTDS - dblCGST;
	
												double dblSGST = uF.parseToDouble(hmTotalisDisplay.get(SGST + ""));
												dblGrossTDS = dblGrossTDS - dblSGST;
											}
	//
	//										/**
	//										 * (dblBasic + dblDA) we use
	//										 * dblHraSalHeadsAmount
	//										 * */
	//										double dblTDS = calculateTDS(con, uF, strD2, strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS,
	//												dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount, nPayMonth, strD1, strFinancialYearStart,
	//												strFinancialYearEnd, strEmpId, hmEmpPay.get("EMP_GENDER"), hmEmpPay.get("EMP_AGE"),
	//												hmEmpStateMap.get(strEmpId), hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap,
	//												hmEmpRentPaidMap, hmPaidSalaryDetails, hmTotalisDisplay, hmSalaryDetails, hmEmpLevelMap, CF,
	//												hmPrevEmpTdsAmount, hmPrevEmpGrossAmount, hmEmpIncomeOtherSourcesMap, hmOtherTaxDetails, hmEmpStateMap);
											
											double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
													strFinancialYearEnd, strEmpId, hmEmpLevelMap);
											
											dblDeduction += dblTDS;
	
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
										}
									}
									break;
								
									/********** ESI EMPLOYER CONTRIBUTION *************/
								case EMPLOYER_ESI :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
									} else {
										double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay,
												hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables, strD1, strD2, strPC, hmTotalisDisplay);
										dblESI = Math.ceil(dblESI);
										
										dblDeduction += dblESI;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
									}

									break;
								/********** /ESI EMPLOYER CONTRIBUTION *************/

								/********** ESI EMPLOYEE CONTRIBUTION *************/
								case EMPLOYEE_ESI :
									if (hmPaidSalaryInner != null) {
										double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblPt;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									} else {
										double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, hmAnnualVariables, strD1, strD2, strPC, hmTotalisDisplay);
										if(uF.parseToInt(strEmpId)==384){
											System.out.println("AP/3158---dblESI=="+dblESI);
										}
										if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
											List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
											if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
											for(Map<String,String> hmApplyArear : alArrear) {
												int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
												
												Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
												if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
												
												dblESI += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_ESI))));
											}	
										}
										
										dblESI = Math.ceil(dblESI);
										
										dblDeduction += dblESI;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
									}
									break;
								/********** /ESI EMPLOYEE CONTRIBUTION *************/

								/********** LWF EMPLOYER CONTRIBUTION *************/
								case EMPLOYER_LWF :
									if (hmPaidSalaryInner != null) {
										double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblLWF;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
									} else {
										double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
										
										dblDeduction += dblLWF;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
									}

									break;
								/********** /LWF EMPLOYER CONTRIBUTION *************/

								/********** LWF EMPLOYEE CONTRIBUTION *************/
								case EMPLOYEE_LWF :
									if (hmPaidSalaryInner != null) {
										double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblLWF;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
									} else {
										double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, hmAnnualVariables, strOrgId);
										
										if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
											List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
											if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
											for(Map<String,String> hmApplyArear : alArrear) {
												int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
												
												Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
												if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
												
												dblLWF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_LWF))));
											}	
										}
										
										dblDeduction += dblLWF;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
									}

									break;

								/********** /LWF EMPLOYEE CONTRIBUTION *************/	

							}
						}

						hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
						hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
					}
					/**
					 * Other cal isDisplay end
					 * */

					hmTotalSalaryisDisplay.put(strEmpId+"_"+strSalEffectiveDate, hmTotalisDisplay);
					hmEmpTotGrossIsDisplay.put(strEmpId, ""+dblGrossForPT);
					// //=========================end code for isdisplay
					// false=======================

				}
//				System.out.println("hmTotalSalary ===>> " + hmTotalSalary);
				
				request.setAttribute("alEmp", alEmp);
				request.setAttribute("hmEmp", hmEmp);
				request.setAttribute("hmSalaryDetails", hmSalaryDetails);
//				request.setAttribute("hmEmpSalary", hmEmpSalary);
				request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
				request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
				request.setAttribute("hmLoanAmt", hmLoanAmt);
				request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);
				request.setAttribute("hmTotalSalary", hmTotalSalary);
				request.setAttribute("hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);

				// System.out.println("hmTotalSalary===>"+hmTotalSalary);

				session.setAttribute("AP_alEmp", alEmp);
				session.setAttribute("AP_hmEmp", hmEmp);
				session.setAttribute("AP_hmSalaryDetails", hmSalaryDetails);
//				session.setAttribute("AP_hmEmpSalary", hmEmpSalary);
				session.setAttribute("AP_alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
				session.setAttribute("AP_alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
				session.setAttribute("AP_hmLoanAmt", hmLoanAmt);
				session.setAttribute("AP_hmLoanPoliciesMap", hmLoanPoliciesMap);
				session.setAttribute("AP_hmTotalSalary", hmTotalSalary);
				session.setAttribute("AP_hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);

				session.setAttribute("AP_strD1", strD1);
				session.setAttribute("AP_strD2", strD2);
				session.setAttribute("AP_strPC", strPC);
				session.setAttribute("AP_f_org", getF_org());
				session.setAttribute("AP_strPaycycleDuration", getStrPaycycleDuration());

				session.setAttribute("AP_hmEmpStateMap", hmEmpStateMap);
				session.setAttribute("AP_hmEmpLevelMap", hmEmpLevelMap);
				session.setAttribute("AP_hmVariables", hmVariables);
				session.setAttribute("AP_hmAnnualVariables", hmAnnualVariables);
				session.setAttribute("AP_hmOtherTaxDetails", hmOtherTaxDetails);
				session.setAttribute("AP_hmArearAmountMap", hmArearAmountMap);
				
				session.setAttribute("AP_hmEmpArrear", hmEmpArrear); 
				session.setAttribute("AP_hmArrearCalSalary", hmArrearCalSalary); 
				session.setAttribute("AP_hmArrearEarningHead", hmArrearEarningHead); 
				session.setAttribute("AP_hmArrearDeductionHead", hmArrearDeductionHead); 
				session.setAttribute("AP_hmArrearEmployeePF", hmArrearEmployeePF); 
				session.setAttribute("AP_hmArrearEmployerPF", hmArrearEmployerPF); 
				session.setAttribute("AP_hmArrearEmployerESI", hmArrearEmployerESI); 
				session.setAttribute("AP_hmArrearEmployeeLWF", hmArrearEmployeeLWF); 
				
				session.setAttribute("AP_hmIncrementEmpArrear", hmIncrementEmpArrear); 
				session.setAttribute("AP_hmIncrementArrearCalSalary", hmIncrementArrearCalSalary); 
				session.setAttribute("AP_hmIncrementArrearEarningHead", hmIncrementArrearEarningHead); 
				session.setAttribute("AP_hmIncrementArrearDeductionHead", hmIncrementArrearDeductionHead); 
				session.setAttribute("AP_hmIncrementArrearEmployeePF", hmIncrementArrearEmployeePF); 
				session.setAttribute("AP_hmIncrementArrearEmployerPF", hmIncrementArrearEmployerPF); 
				session.setAttribute("AP_hmIncrementArrearEmployerESI", hmIncrementArrearEmployerESI); 
				session.setAttribute("AP_hmIncrementArrearEmployeeLWF", hmIncrementArrearEmployeeLWF);
				session.setAttribute("AP_hmIncrementArrearPaycycle", hmIncrementArrearPaycycle);
				
//				System.out.println("hmEmpArrear==>"+hmEmpArrear);
//				System.out.println("hmArrearCalSalary==>"+hmArrearCalSalary);
//				System.out.println("hmArrearEarningHead==>"+hmArrearEarningHead);
//				System.out.println("hmArrearDeductionHead==>"+hmArrearDeductionHead);
//				System.out.println("hmArrearEmployeePF==>"+hmArrearEmployeePF);
//				System.out.println("hmArrearEmployerPF==>"+hmArrearEmployerPF);
//				System.out.println("hmArrearEmployerESI==>"+hmArrearEmployerESI);
//				System.out.println("hmArrearEmployeeLWF==>"+hmArrearEmployeeLWF);
			}

			request.setAttribute("strD1", strD1);
			request.setAttribute("strD2", strD2);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	double getMultipleCalAmtDetailsByGrade(Connection con, UtilityFunctions uF, CommonFunctions CF, int nSalayHead,String strEmpId, double dblPresent,
			int nTotalNumberOfDays, String strD1, String strD2, String strPC, String strGrade, Map<String, String> hm, Map<String, String> hmTotal, 
			Map<String, String> hmAnnualVarPolicyAmount, double dblReimbursementCTC, Map<String, String> hmAllowance, Map<String, String> hmVariables, 
			double dblReimbursementCTCOptional, Map<String, String> hmContriSalHeadAmt) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblMulCalAmt = 0.0d;
		try {
			if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();
			if(hmAllowance == null) hmAllowance = new HashMap<String, String>();
			if(hmVariables == null) hmVariables = new HashMap<String, String>();
			
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
					} else if(nHeadId > 0 && nHeadId == VDA) {
						flag = true;
					}
				}
				
				if(flag) {
					Map<String, String> hmSalaryType = new HashMap<String, String>();
					pst = con.prepareStatement("select * from salary_details where grade_id = ? and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by salary_head_id, salary_id");
					pst.setInt(1, uF.parseToInt(strGrade));
					rs = pst.executeQuery();
					while (rs.next()) {
						hmSalaryType.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
					}
					rs.close();
					pst.close();
		
					pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) from emp_salary_details " +
						"where emp_id =? and effective_date <= ? and is_approved=true and grade_id=?) and salary_head_id in ("+CTC+") and salary_head_id in (select salary_head_id " +
						"from salary_details where (is_delete is null or is_delete=false) and org_id in (select org_id from employee_personal_details epd, " +
						"employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.emp_id=?) and grade_id = ?) and grade_id = ?");
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
//								System.out.println("str ===>> " + str+" -- dblAmt ===>> " + dblAmt);
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
//				System.out.println("pst===>>>>>>"+pst); 
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
				Map<String, Map<String,String>> hmInnerActualCTC = new HashMap<String, Map<String, String>>();//CF.getSalaryCalculationByGrade(con,hmInnerisDisplay, uF.parseToInt(strEmpId), dblEnashDays, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strGrade, uF, CF, strD2);
				
				double salaryGross=0;
				double salaryDeduction=0;
				Iterator<String> it = hmInnerActualCTC.keySet().iterator();
				while(it.hasNext()) {
					String strSalaryId = it.next();
					
					Map<String,String> hm = hmInnerActualCTC.get(strSalaryId);
					if(hm.get("EARNING_DEDUCTION").equals("E") && alsalaryHeads.contains(strSalaryId)) {
						salaryGross +=uF.parseToDouble(hm.get("AMOUNT"));
					} else if(hm.get("EARNING_DEDUCTION").equals("D") && alsalaryHeads.contains(strSalaryId)) {
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

	
	private void approvePayrollEntries(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);

			String AP_strD1 = (String) session.getAttribute("AP_strD1");
			String AP_strD2 = (String) session.getAttribute("AP_strD2");
			String AP_strPC = (String) session.getAttribute("AP_strPC");
			String AP_f_org = (String) session.getAttribute("AP_f_org");
			String AP_strPaycycleDuration = (String) session.getAttribute("AP_strPaycycleDuration");

			Date stD1 = uF.getDateFormat(strD1, DATE_FORMAT);
			Date stApD1 = uF.getDateFormat(AP_strD1, DATE_FORMAT);

			Date stD2 = uF.getDateFormat(strD2, DATE_FORMAT);
			Date stApD2 = uF.getDateFormat(AP_strD2, DATE_FORMAT);

			boolean check = stD1.equals(stApD1);
			boolean check1 = stD2.equals(stApD2);

			if (check && check1 && uF.parseToInt(strPC) == uF.parseToInt(AP_strPC) && uF.parseToInt(getF_org()) == uF.parseToInt(AP_f_org)
					&& (getStrPaycycleDuration() != null && getStrPaycycleDuration().equals(AP_strPaycycleDuration))) {
				String strDomain = request.getServerName().split("\\.")[0];

				Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
				if (hmCurrencyDetails == null) hmCurrencyDetails = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if (hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();

				String strFinancialYearEnd = null;
				String strFinancialYearStart = null;
				String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
				if (strFinancialYear != null) {
					strFinancialYearStart = strFinancialYear[0];
					strFinancialYearEnd = strFinancialYear[1];
				}

				String[] strApprovePayCycle = new String[3];
				strApprovePayCycle[0] = strD1;
				strApprovePayCycle[1] = strD2;
				strApprovePayCycle[2] = strPC;

				List<Map<String, String>> alEmp = (List<Map<String, String>>) session.getAttribute("AP_alEmp");
				if (alEmp == null) alEmp = new ArrayList<Map<String, String>>();
				Map<String, Map<String, String>> hmEmp = (Map<String, Map<String, String>>) session.getAttribute("AP_hmEmp");
				if (hmEmp == null) hmEmp = new HashMap<String, Map<String, String>>();
				Map<String, String> hmSalaryDetails = (Map<String, String>) session.getAttribute("AP_hmSalaryDetails");
				if (hmSalaryDetails == null) hmSalaryDetails = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmEmpSalary = (Map<String, Map<String, String>>) session.getAttribute("AP_hmEmpSalary");
//				if (hmEmpSalary == null) hmEmpSalary = new LinkedHashMap<String, Map<String, String>>();
				List<String> alEmpSalaryDetailsEarning = (List<String>) session.getAttribute("AP_alEmpSalaryDetailsEarning");
				if (alEmpSalaryDetailsEarning == null) alEmpSalaryDetailsEarning = new ArrayList<String>();
				List<String> alEmpSalaryDetailsDeduction = (List<String>) session.getAttribute("AP_alEmpSalaryDetailsDeduction");
				if (alEmpSalaryDetailsDeduction == null) alEmpSalaryDetailsDeduction = new ArrayList<String>();
				Map<String, String> hmLoanAmt = (Map<String, String>) session.getAttribute("AP_hmLoanAmt");
				if (hmLoanAmt == null)hmLoanAmt = new HashMap<String, String>();
				Map<String, String> hmLoanPoliciesMap = (Map<String, String>) session.getAttribute("AP_hmLoanPoliciesMap");
				if (hmLoanPoliciesMap == null)hmLoanPoliciesMap = new HashMap<String, String>();
				LinkedHashMap<String, Map<String, String>> hmTotalSalary = (LinkedHashMap<String, Map<String, String>>) session.getAttribute("AP_hmTotalSalary");
				if (hmTotalSalary == null) hmTotalSalary = new LinkedHashMap<String, Map<String, String>>();
				LinkedHashMap<String, Map<String, String>> hmTotalESICSalary = (LinkedHashMap<String, Map<String, String>>) session.getAttribute("AP_hmTotalESICSalary");
				if (hmTotalSalary == null) hmTotalESICSalary = new LinkedHashMap<String, Map<String, String>>();
				LinkedHashMap<String, Map<String, String>> hmTotalSalaryisDisplay = (LinkedHashMap<String, Map<String, String>>) session.getAttribute("AP_hmTotalSalaryisDisplay");
				if (hmTotalSalaryisDisplay == null) hmTotalSalaryisDisplay = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> hmEmpStateMap = (Map<String, String>) session.getAttribute("AP_hmEmpStateMap");
				if (hmEmpStateMap == null) hmEmpStateMap = new HashMap<String, String>();
				Map<String, String> hmEmpLevelMap = (Map<String, String>) session.getAttribute("AP_hmEmpLevelMap");
				if (hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
				Map<String, String> hmVariables = (Map<String, String>) session.getAttribute("AP_hmVariables");
				if (hmVariables == null) hmVariables = new HashMap<String, String>();
				Map<String, String> hmAnnualVariables = (Map<String, String>) session.getAttribute("AP_hmAnnualVariables");
				if (hmAnnualVariables == null) hmAnnualVariables = new HashMap<String, String>();
				Map<String, String> hmOtherTaxDetails = (Map<String, String>) session.getAttribute("AP_hmOtherTaxDetails");
				if (hmOtherTaxDetails == null) hmOtherTaxDetails = new HashMap<String, String>();
				Map<String, Map<String, String>> hmArearAmountMap = (Map<String, Map<String, String>>) session.getAttribute("AP_hmArearAmountMap");
				if (hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String, String>>();
				
				/**
				 * Arrear No of days
				 * */
				Map<String, List<Map<String, String>>> hmIncrementEmpArrear = (Map<String, List<Map<String,String>>>)session.getAttribute("AP_hmIncrementEmpArrear");
				if(hmIncrementEmpArrear == null) hmIncrementEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
				Map<String, Map<String, String>> hmIncrementArrearCalSalary = (Map<String, Map<String,String>>)session.getAttribute("AP_hmIncrementArrearCalSalary");
				if(hmIncrementArrearCalSalary == null)hmIncrementArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
				Map<String, List<String>> hmIncrementArrearEarningHead = (Map<String, List<String>>)session.getAttribute("AP_hmIncrementArrearEarningHead");
				if(hmIncrementArrearEarningHead == null)hmIncrementArrearEarningHead = new LinkedHashMap<String, List<String>>();
				Map<String, List<String>> hmIncrementArrearDeductionHead = (Map<String, List<String>>)session.getAttribute("AP_hmIncrementArrearDeductionHead");
				if(hmIncrementArrearDeductionHead == null)hmIncrementArrearDeductionHead = new LinkedHashMap<String, List<String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployeePF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmIncrementArrearEmployeePF");
				if(hmIncrementArrearEmployeePF == null)hmIncrementArrearEmployeePF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployerPF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmIncrementArrearEmployerPF");
				if(hmIncrementArrearEmployerPF == null)hmIncrementArrearEmployerPF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployerESI = (Map<String, Map<String, String>>)session.getAttribute("AP_hmIncrementArrearEmployerESI");
				if(hmIncrementArrearEmployerESI == null)hmIncrementArrearEmployerESI = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmIncrementArrearEmployeeLWF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmIncrementArrearEmployeeLWF");
				if(hmIncrementArrearEmployeeLWF == null)hmIncrementArrearEmployeeLWF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, List<String>> hmIncrementArrearPaycycle = (Map<String, List<String>>)session.getAttribute("AP_hmIncrementArrearPaycycle");
				if(hmIncrementArrearPaycycle == null)hmIncrementArrearPaycycle = new LinkedHashMap<String, List<String>>();
				
				Map<String, List<Map<String, String>>> hmEmpArrear = (Map<String, List<Map<String,String>>>)session.getAttribute("AP_hmEmpArrear");
				if(hmEmpArrear == null) hmEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
				Map<String, Map<String, String>> hmArrearCalSalary = (Map<String, Map<String,String>>)session.getAttribute("AP_hmArrearCalSalary");
				if(hmArrearCalSalary == null)hmArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
				Map<String, List<String>> hmArrearEarningHead = (Map<String, List<String>>)session.getAttribute("AP_hmArrearEarningHead");
				if(hmArrearEarningHead == null)hmArrearEarningHead = new LinkedHashMap<String, List<String>>();
				Map<String, List<String>> hmArrearDeductionHead = (Map<String, List<String>>)session.getAttribute("AP_hmArrearDeductionHead");
				if(hmArrearDeductionHead == null)hmArrearDeductionHead = new LinkedHashMap<String, List<String>>();
				Map<String, Map<String, String>> hmArrearEmployeePF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmArrearEmployeePF");
				if(hmArrearEmployeePF == null)hmArrearEmployeePF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployerPF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmArrearEmployerPF");
				if(hmArrearEmployerPF == null)hmArrearEmployerPF = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployerESI = (Map<String, Map<String, String>>)session.getAttribute("AP_hmArrearEmployerESI");
				if(hmArrearEmployerESI == null)hmArrearEmployerESI = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmArrearEmployeeLWF = (Map<String, Map<String, String>>)session.getAttribute("AP_hmArrearEmployeeLWF");
				if(hmArrearEmployeeLWF == null)hmArrearEmployeeLWF = new LinkedHashMap<String, Map<String, String>>();
				

				ApprovePayRunnable objRunnable = new ApprovePayRunnable(this, con, uF, CF, strFinancialYearStart, strFinancialYearEnd, strApprovePayCycle,
					hmEmpStateMap, hmCurrencyDetails, hmEmpCurrency, hmVariables, request, response, hmOtherTaxDetails, hmEmpLevelMap, strDomain,
					hmArearAmountMap, hmAnnualVariables,hmEmpArrear,hmArrearCalSalary,hmArrearEarningHead,hmArrearDeductionHead,hmArrearEmployeePF,
					hmArrearEmployerPF,hmArrearEmployerESI,hmArrearEmployeeLWF, hmIncrementEmpArrear,hmIncrementArrearCalSalary,hmIncrementArrearEarningHead,
					hmIncrementArrearDeductionHead,hmIncrementArrearEmployeePF,hmIncrementArrearEmployerPF,hmIncrementArrearEmployerESI,hmIncrementArrearEmployeeLWF,
					hmIncrementArrearPaycycle);

				List<String> checkPayrollList = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id,sal_effective_date from payroll_generation where paycycle=? and month=? and year=? group by emp_id,sal_effective_date having emp_id>0 order by emp_id,sal_effective_date");
				pst.setInt(1, uF.parseToInt(strApprovePayCycle[2]));
				pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
				pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
				rs = pst.executeQuery();
				while (rs.next()) {
					String strEffectiveDate = rs.getString("sal_effective_date") != null ? uF.getDateFormat(rs.getString("sal_effective_date"), DBDATE, DATE_FORMAT) : "";
					checkPayrollList.add(rs.getString("emp_id")+"_"+strEffectiveDate);
				}
				rs.close();
				pst.close();

				// Iterator<String> it = hmTotalSalary.keySet().iterator();
				// System.out.println("hmTotalSalary===>"+hmTotalSalary);
				// while(it.hasNext()) {
				// String strEmpId = it.next();

				Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
				int nEmpSize = getStrEmpIds() != null ? getStrEmpIds().size() : 0;
				for (int ii = 0; hmTotalSalary != null && !hmTotalSalary.isEmpty() && ii < nEmpSize; ii++) {
					String strEmpIdWithEffectiveDate = getStrEmpIds().get(ii);
					String strTmp[] = strEmpIdWithEffectiveDate.split("_");
					String strEmpId = strTmp[0];
					String strOrgId = hmEmpOrgId.get(strEmpId);
					String strEmpSalEffectiveDate = strApprovePayCycle[0];
					if(strTmp.length>1) {
						strEmpSalEffectiveDate = strTmp[1];
					}
					if (!checkPayrollList.isEmpty() && checkPayrollList.contains(strEmpIdWithEffectiveDate)) {
						continue;
					}

					Map<String, String> hmEmpPay = hmEmp.get(strEmpId);
					
//					System.out.println("HmEmp===>"+hmEmp);
					if (hmEmpPay == null) hmEmpPay = new HashMap<String, String>();

					double dbTotalDays = uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"));
					double dblPaidDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"));
					double dblPresentDays = uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"));
					double dblPaidLeaveDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"));

					Map<String, String> hmTotal = hmTotalSalary.get(strEmpIdWithEffectiveDate);
					Map<String, String> hmTotalESIC = hmTotalESICSalary.get(strEmpIdWithEffectiveDate);
					Map<String, String> hmTotalisDisplay = hmTotalSalaryisDisplay.get(strEmpIdWithEffectiveDate);
					int nEmpServiceId = uF.parseToInt(hmEmpPay.get("EMP_SERVICE_ID"));
//					System.out.println("hmEmpPay===>"+hmEmpPay);
//					System.out.println("nEmpServiceId===>"+nEmpServiceId);
//					System.out.println("AP/3727--hmTotal="+hmTotal);
					double dblTotal = 0.0;
					for (int i = 0; i < alEmpSalaryDetailsEarning.size(); i++) {
						String strSalaryId = alEmpSalaryDetailsEarning.get(i);
						
						if (hmTotal != null && !hmTotal.containsKey(strSalaryId)) {
//							System.out.println("strSalaryId =====>> " + strSalaryId);
							if (hmTotalisDisplay != null && hmTotalisDisplay.containsKey(strSalaryId)) {
								pst = con.prepareStatement("insert into payroll_generation_lta (emp_id,month,year,pay_date,entry_date,salary_head_id," +
									"amount,paycycle,financial_year_from_date,financial_year_to_date,currency_id,service_id,earning_deduction," +
									"pay_mode,paid_from,paid_to,payment_mode,present_days,paid_days,paid_leaves,total_days,sal_effective_date) " +
									"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
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
								pst.setInt(12, nEmpServiceId);
								pst.setString(13, "E");
								pst.setString(14, getStrPaycycleDuration());
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
								pst.setDouble(18, dblPresentDays);
								pst.setDouble(19, dblPaidDays);
								pst.setDouble(20, dblPaidLeaveDays);
								pst.setDouble(21, dbTotalDays);
								pst.setDate(22, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst.execute();
//								System.out.println("pst =====>> " + pst);
								pst.close();
							}
							continue;
						}

						pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, "
							+ "financial_year_from_date,financial_year_to_date,currency_id,service_id,earning_deduction,pay_mode,paid_from,paid_to," +
							"payment_mode,present_days,paid_days,paid_leaves,total_days,approve_by,approve_date,sal_effective_date) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
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
						pst.setInt(12, nEmpServiceId);
						pst.setString(13, "E");
						pst.setString(14, getStrPaycycleDuration());
						pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
						pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
						pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
						pst.setDouble(18, dblPresentDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPaidLeaveDays);
						pst.setDouble(21, dbTotalDays);
						pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(24, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
						pst.execute();
						pst.close();

						dblTotal += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));

						double dblAmt = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == REIMBURSEMENT && dblAmt > 0) {
							pst = con.prepareStatement(updateReimbursementPayroll);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}

						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == TRAVEL_REIMBURSEMENT && dblAmt > 0) {
							pst = con.prepareStatement(updateReimbursementPayroll1);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}

						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == MOBILE_REIMBURSEMENT && dblAmt > 0) {
							pst = con.prepareStatement(updateReimbursementPayroll2);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}

						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == OTHER_REIMBURSEMENT && dblAmt > 0) {
							pst = con.prepareStatement(updateReimbursementPayroll3);
							pst.setBoolean(1, true);
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strEmpId));
							pst.execute();
							pst.close();
						}
					}

					for (int i = 0; i < alEmpSalaryDetailsDeduction.size(); i++) {
						String strSalaryId = alEmpSalaryDetailsDeduction.get(i);

						if (hmTotal != null && !hmTotal.containsKey(strSalaryId)) {
//							System.out.println("Deduction strSalaryId =====>> " + strSalaryId);
							if (hmTotalisDisplay != null && hmTotalisDisplay.containsKey(strSalaryId)) {
								pst = con.prepareStatement("insert into payroll_generation_lta (emp_id,month,year,pay_date,entry_date,salary_head_id," +
									"amount,paycycle,financial_year_from_date,financial_year_to_date,currency_id,service_id,earning_deduction," +
									"pay_mode,paid_from,paid_to,payment_mode,present_days,paid_days,paid_leaves,total_days,sal_effective_date) " +
									"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
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
								pst.setInt(12, nEmpServiceId);
								pst.setString(13, "D");
								pst.setString(14, getStrPaycycleDuration());
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
								pst.setDouble(18, dblPresentDays);
								pst.setDouble(19, dblPaidDays);
								pst.setDouble(20, dblPaidLeaveDays);
								pst.setDouble(21, dbTotalDays);
								pst.setDate(22, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst.execute();
//								System.out.println("Deduction pst =====>> " + pst);
								pst.close();
							}
							continue;
						}

						pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, "
							+ "financial_year_from_date,financial_year_to_date,currency_id,service_id,earning_deduction,pay_mode,paid_from,paid_to," +
							"payment_mode,present_days,paid_days,paid_leaves,total_days,approve_by,approve_date,sal_effective_date) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
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
						pst.setInt(12, nEmpServiceId);
						pst.setString(13, "D");
						pst.setString(14, getStrPaycycleDuration());
						pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
						pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
						pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
						pst.setDouble(18, dblPresentDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPaidLeaveDays);
						pst.setDouble(21, dbTotalDays);
						pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(24, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
						pst.execute();
						pst.close();

						dblTotal -= uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));

						double dblAmt = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmTotal.get(strSalaryId))));
						if (getApprovePC() != null && uF.parseToInt(strSalaryId) == LOAN && dblAmt > 0) {
							double dblBalanceAmt = 0;
							int nLoanId = 0;
							int nLoanAppId = 0;
							pst = con.prepareStatement(selectLoanPyroll2);
							pst.setInt(1, uF.parseToInt(strEmpId));
							rs = pst.executeQuery();
							while (rs.next()) {
								dblBalanceAmt = rs.getDouble("balance_amount");
								nLoanId = rs.getInt("loan_id");
								nLoanAppId = rs.getInt("loan_applied_id");

								double dblAmt1 = uF.parseToDouble(hmLoanAmt.get(nLoanAppId + ""));
								dblBalanceAmt = dblBalanceAmt - dblAmt1;

								pst1 = con.prepareStatement(updateLoanPyroll1);
								pst1.setDouble(1, dblBalanceAmt);
								if (dblBalanceAmt > 0) {
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
								pst1.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT)); // need to update with strEmpEffectiveDate
								pst1.execute();
								pst1.close();
							}
							rs.close();
							pst.close();

							/**
							 * Log Details
							 * */
							String strProcessByName = CF.getEmpNameMapByEmpId(con, strSessionEmpId);
							String strEmpName = CF.getEmpNameMapByEmpId(con, ""+Integer.parseInt(strEmpId));
							String strProcessMsg = uF.showData(strProcessByName, "")+" has approved salary of "+uF.showData(strEmpName, "") +" on " +
								""+uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, CF.getStrReportDateFormat())+" " +
								""+uF.getTimeFormatStr(""+uF.getCurrentTime(CF.getStrTimeZone()), DBTIME, CF.getStrReportTimeAM_PMFormat());
							LogDetails logDetails = new LogDetails();
							logDetails.session = session;
							logDetails.CF = CF;
							logDetails.request = request;
							logDetails.setProcessId(Integer.parseInt(strEmpId));
							logDetails.setProcessType(L_APPROVE_SALARY);
							logDetails.setProcessActivity(L_ADD);
							logDetails.setProcessMsg(strProcessMsg);
							logDetails.setProcessStep(0);
							logDetails.setProcessBy(uF.parseToInt(strSessionEmpId));
							logDetails.insertLog(con, uF);
						}
					}
					
					/**
					 * Insert Arrear days salary
					 * */
					if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
						List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
						if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
						for(Map<String,String> hmApplyArear : alArrear) {
							String strArrearId = hmApplyArear.get("ARREAR_ID");
							double dblArrearDays = uF.parseToDouble(hmApplyArear.get("ARREAR_DAYS"));
							String strArrearPaycycleFrom = hmApplyArear.get("ARREAR_PAYCYCLE_FROM");
							String strArrearPaycycleTo = hmApplyArear.get("ARREAR_PAYCYCLE_TO");
							int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
							double dblArrearTotalDays = uF.parseToDouble(hmApplyArear.get("ARREAR_PAYCYCLE_TOTAL_DAYS"));
							
							List<String> alEarningHead = hmArrearEarningHead.get(strEmpId+"_"+nArrearPaycycle);
							if(alEarningHead == null) alEarningHead = new ArrayList<String>();
							List<String> alDeductionHead = hmArrearDeductionHead.get(strEmpId+"_"+nArrearPaycycle);
							if(alDeductionHead == null) alDeductionHead = new ArrayList<String>();
							Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
							if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
							
							for(String strSalaryId : alEarningHead) {
								pst = con.prepareStatement("insert into arrear_generation (emp_id,month,year,pay_date,entry_date,salary_head_id," +
									"amount,paycycle,financial_year_from_date,financial_year_to_date,currency_id,service_id,earning_deduction," +
									"pay_mode,paid_from,paid_to,payment_mode,present_days,paid_days,paid_leaves,total_days,approve_by,approve_date," +
									"arear_id,sal_effective_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
								pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
								pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setInt(6, uF.parseToInt(strSalaryId));
								pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(strSalaryId)))));
								pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
								pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
								pst.setInt(12, nEmpServiceId);
								pst.setString(13, "E");
								pst.setString(14, getStrPaycycleDuration());
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
								pst.setDouble(18, dblArrearDays);
								pst.setDouble(19, dblArrearDays);
								pst.setDouble(20, 0.0d);
								pst.setDouble(21, dblArrearTotalDays);
								pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(24, uF.parseToInt(strArrearId));
								pst.setDate(25, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst.execute();
								pst.close();
							}
							
							for(String strSalaryId : alDeductionHead) {
								pst = con.prepareStatement("insert into arrear_generation (emp_id,month,year,pay_date,entry_date,salary_head_id," +
									"amount,paycycle,financial_year_from_date,financial_year_to_date,currency_id,service_id,earning_deduction," +
									"pay_mode,paid_from,paid_to,payment_mode,present_days,paid_days,paid_leaves,total_days,approve_by,approve_date," +
									"arear_id,sal_effective_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
								pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
								pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
								pst.setInt(6, uF.parseToInt(strSalaryId));
								pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(strSalaryId)))));
								pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
								pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
								pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
								pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
								pst.setInt(12, nEmpServiceId);
								pst.setString(13, "E");
								pst.setString(14, getStrPaycycleDuration());
								pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
								pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
								pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
								pst.setDouble(18, dblArrearDays);
								pst.setDouble(19, dblArrearDays);
								pst.setDouble(20, 0.0d);
								pst.setDouble(21, dblArrearTotalDays);
								pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
								pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(24, uF.parseToInt(strArrearId));
								pst.setDate(25, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
								pst.execute();
								pst.close();
							}						
						}
					}
					
					
					
					/**
					 * Insert Increment Arrear salary
					 * */
//					System.out.println("hmIncrementEmpArrear ===>> " + hmIncrementEmpArrear);
					if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
						List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
						if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
						for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
//							String strArrearId = hmIncrementApplyArear.get("ARREAR_ID");
//							double dblArrearDays = uF.parseToDouble(hmApplyArear.get("ARREAR_DAYS"));
//							String strArrearPaycycleFrom = hmApplyArear.get("ARREAR_PAYCYCLE_FROM");
//							String strArrearPaycycleTo = hmApplyArear.get("ARREAR_PAYCYCLE_TO");
//							int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
//							double dblArrearTotalDays = uF.parseToDouble(hmApplyArear.get("ARREAR_PAYCYCLE_TOTAL_DAYS"));
							int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
							List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
							
//							System.out.println("alArrearPaycycle ===>> " + alArrearPaycycle);
							for(String strPaycycle : alArrearPaycycle) {
								List<String> alArrearPaycyclewiseData = hmIncrementArrearPaycycle.get(nArrearId+"_"+strPaycycle+"_OTHER_INFO");
								if(alArrearPaycyclewiseData == null) alArrearPaycyclewiseData = new ArrayList<String>();
								
								List<String> alIncrementEarningHead = hmIncrementArrearEarningHead.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
								if(alIncrementEarningHead == null) alIncrementEarningHead = new ArrayList<String>();
								List<String> alIncrementDeductionHead = hmIncrementArrearDeductionHead.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
								if(alIncrementDeductionHead == null) alIncrementDeductionHead = new ArrayList<String>();
								Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
								if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
//								System.out.println("alIncrementEarningHead ===>> " + alIncrementEarningHead);
								
								for(String strSalaryId : alIncrementEarningHead) {
									pst = con.prepareStatement("insert into arrear_generation (emp_id,month,year,pay_date,entry_date,salary_head_id," +
										"amount,paycycle,financial_year_from_date,financial_year_to_date,currency_id,service_id,earning_deduction," +
										"pay_mode,paid_from,paid_to,payment_mode,present_days,paid_days,paid_leaves,total_days,approve_by,approve_date," +
										"arear_id,sal_effective_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
									pst.setInt(1, uF.parseToInt(strEmpId));
									pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
									pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
									pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
									pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
									pst.setInt(6, uF.parseToInt(strSalaryId));
									pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(strSalaryId)))));
									pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
									pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
									pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
									pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
									pst.setInt(12, nEmpServiceId);
									pst.setString(13, "E");
									pst.setString(14, getStrPaycycleDuration());
									pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
									pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
									pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
									pst.setDouble(18, uF.parseToDouble(alArrearPaycyclewiseData.get(2)));
									pst.setDouble(19, uF.parseToDouble(alArrearPaycyclewiseData.get(0)));
									pst.setDouble(20, uF.parseToDouble(alArrearPaycyclewiseData.get(1)));
									pst.setDouble(21, uF.parseToDouble(alArrearPaycyclewiseData.get(3)));
									pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
									pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(24, nArrearId);
									pst.setDate(25, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
									pst.execute();
									pst.close();
								}
//								System.out.println("alIncrementDeductionHead ===>> " + alIncrementDeductionHead);
								
								for(String strSalaryId : alIncrementDeductionHead) {
									pst = con.prepareStatement("insert into arrear_generation (emp_id,month,year,pay_date,entry_date,salary_head_id," +
										"amount,paycycle,financial_year_from_date,financial_year_to_date,currency_id,service_id,earning_deduction," +
										"pay_mode,paid_from,paid_to,payment_mode,present_days,paid_days,paid_leaves,total_days,approve_by,approve_date," +
										"arear_id,sal_effective_date) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
									pst.setInt(1, uF.parseToInt(strEmpId));
									pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
									pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
									pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
									pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
									pst.setInt(6, uF.parseToInt(strSalaryId));
									pst.setDouble(7, uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(strSalaryId)))));
									pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
									pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
									pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
									pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
									pst.setInt(12, nEmpServiceId);
									pst.setString(13, "E");
									pst.setString(14, getStrPaycycleDuration());
									pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
									pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
									pst.setInt(17, uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID")));
									pst.setDouble(18, uF.parseToDouble(alArrearPaycyclewiseData.get(2)));
									pst.setDouble(19, uF.parseToDouble(alArrearPaycyclewiseData.get(0)));
									pst.setDouble(20, uF.parseToDouble(alArrearPaycyclewiseData.get(1)));
									pst.setDouble(21, uF.parseToDouble(alArrearPaycyclewiseData.get(3)));
									pst.setInt(22, uF.parseToInt((String) session.getAttribute(EMPID)));
									pst.setDate(23, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(24, nArrearId);
									pst.setDate(25, uF.getDateFormat(strEmpSalEffectiveDate, DATE_FORMAT));
									pst.execute();
									pst.close();
								}						
							}
						}
					}
					
					
					
					/**
					 * Insert Arrear days salary end
					 * */

					objRunnable.setData(hmTotal, hmTotalESIC, strEmpId, dblTotal, strOrgId);
					objRunnable.run1();
////					Thread t = new Thread(objRunnable);
////					t.start();
//					
//
//					Map<String, String> hmInnerCurrencyDetails = (Map<String, String>) hmCurrencyDetails.get(hmEmpCurrency.get(strEmpId));
//					if (hmInnerCurrencyDetails == null) hmInnerCurrencyDetails = new HashMap<String, String>();
					

					/*
					 * Notifications nF = new
					 * Notifications(N_NEW_SALARY_APPROVED, CF);
					 * nF.setDomain(strDomain); nF.request = request;
					 * nF.setStrHostAddress(CF.getStrEmailLocalHost());
					 * nF.setStrHostPort(CF.getStrHostPort());
					 * nF.setStrContextPath(request.getContextPath());
					 * nF.setStrEmpId(strEmpId);
					 * nF.setStrSalaryAmount(uF.showData
					 * (hmInnerCurrencyDetails.get
					 * ("LONG_CURR"),"")+""+uF.formatIntoTwoDecimal(dblTotal));
					 * nF
					 * .setStrPaycycle(strApprovePayCycle[0]+"-"+strApprovePayCycle
					 * [1]); nF.setEmailTemplate(true); nF.sendNotifications();
					 */
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

	
	private void revokeandOpenClockEntries(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			String[] revokeEmpId = request.getParameterValues("revokeEmpId");
			int nEmpIds = revokeEmpId != null ? revokeEmpId.length : 0;

			if (nEmpIds > 0) {
				int cnt=0;
				for (int i = 0; i < nEmpIds; i++) {
//					System.out.println("revokeEmpId[i] ===>> " + revokeEmpId[i]);
					String strTemp[] = revokeEmpId[i].split("_");
//					System.out.println("strTemp[0] ===>> " + strTemp[0]);
					if (uF.parseToInt(strTemp[0]) > 0) {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("delete from approve_attendance where approve_from >=? and approve_to <=? and emp_id=?");
						if(strTemp.length > 1 && strTemp[1] != null && !strTemp[1].equalsIgnoreCase("null")) {
							sbQuery.append(" and sal_effective_date= '"+uF.getDateFormat(strTemp[1], DATE_FORMAT)+"'");
						}
						pst = con.prepareStatement(sbQuery.toString());
//						pst = con.prepareStatement("delete from approve_attendance where approve_from >=? and approve_to <=? and emp_id in (" + strEmpIds + ") ");
						pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
						pst.setInt(3, uF.parseToInt(strTemp[0]));
//						System.out.println("pst ===>> " + pst);
						int x = pst.executeUpdate();
						cnt++;
					}
				}
				
				if (cnt == nEmpIds) {
					session.setAttribute(MESSAGE, SUCCESSM + "You have successfully Revoke & Open Time Entries." + END);
				} else {
					session.setAttribute(MESSAGE, ERRORM + "Colud not Revoke & Open Time Entries. Please,try again." + END);
				}
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM + "Colud not Revoke & Open Time Entries. Please,try again." + END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	
	private void viewApprovePay(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

//			System.out.println("viewApprovePay ===>> ");
			List<List<String>> alPaycycleList = new ArrayList<List<String>>();
			for (int i = 0; paycycleList != null && ((i < paycycleList.size() && paycycleList.size()>6 && i< 6) || (i < paycycleList.size() && paycycleList.size()<=6)); i++) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(paycycleList.get(i).getPaycycleId());
				innerList.add(paycycleList.get(i).getPaycycleName());
				String[] strTmp = paycycleList.get(i).getPaycycleId().split("-");
				List<String> alList = getApprovedEmpCount(uF, strTmp[0], strTmp[1]);
					innerList.add(alList.get(0));
					innerList.add(alList.get(1));
				alPaycycleList.add(innerList);
			}
			request.setAttribute("alPaycycleList", alPaycycleList);
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));

			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
			if (hmOrg == null)
				hmOrg = new HashMap<String, String>();

			Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);
			
			Map hmWorkLocationMap = CF.getWorkLocationMap(con);

			StringBuilder sbQuery = new StringBuilder();
			
				sbQuery = new StringBuilder();
				sbQuery.append("select * from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
						+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
						+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
//				sbQuery.append(" and at.emp_id in (1175,1276,1296,1182,1157,1174,1273,1286,1287) ");
//				sbQuery.append(" and at.emp_id in (461,462,463) "); //395,427
				if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0) {
	            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	            } else {
	            	 if(getF_level()!=null && getF_level().length>0) {
	                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	                 }
	            	 if(getF_grade()!=null && getF_grade().length>0) {
	                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
	                 }
				}
				
				if (getF_employeType() != null && getF_employeType().length > 0) {
					sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
				}
				if (getF_department() != null && getF_department().length > 0) {
					sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
				}

				if (getF_service() != null && getF_service().length > 0) {
					sbQuery.append(" and (");
					for (int i = 0; i < getF_service().length; i++) {
						sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
						if (i < getF_service().length - 1) {
							sbQuery.append(" OR ");
						}
					}
					sbQuery.append(" ) ");
				}
				if (getStrPaycycleDuration() != null) {
					sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
				}

				if (uF.parseToInt(getF_paymentMode()) > 0) {
					sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
				}
				
				if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
					sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
				} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
					sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
				}

				if (uF.parseToInt(getF_org()) > 0) {
					sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
				} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
					sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
				}
				sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
						+ "and paid_from = ? and paid_to=? group by emp_id) order by emp_fname, emp_lname, at.sal_effective_date");
//				sbQuery.append("select * from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id in ("+sbSingleTimeEmpId.toString()+") order by emp_fname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("sbSingleTimeEmpId pst =====>> " + pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alEmp = new ArrayList<Map<String, String>>();
				List<String> alEmpIds = new ArrayList<String>();
				Map<String, Map<String, String>> hmEmp = new HashMap<String, Map<String, String>>();
				Map<String, String> hmEmpGradeMap = new HashMap<String, String>();
				Map<String, String> hmEmpwiseEffectiveDateCnt = new HashMap<String, String>();
				List<String> alGradeId = new ArrayList<String>();
				while (rs.next()) {
					Map<String, String> hmEmpPay = new HashMap<String, String>();
					hmEmpPay.put("EMP_ID", rs.getString("emp_id"));
					hmEmpPay.put("EMPCODE", rs.getString("empcode"));
	
					//String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim() + " " : "";
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					String strEmpName = rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname");
					hmEmpPay.put("EMP_NAME", strEmpName);
					
					hmEmpPay.put("EMP_PAYMENT_MODE_ID", rs.getString("payment_mode"));
					hmEmpPay.put("EMP_PAYMENT_MODE", uF.showData(hmPaymentModeMap.get(rs.getString("payment_mode")), ""));
					hmEmpPay.put("EMP_BIRTH_DATE", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
					hmEmpPay.put("EMP_JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
	
					if (rs.getString("employment_end_date") != null) {
						hmEmpPay.put("EMP_END_DATE", uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
					}
					hmEmpPay.put("EMP_GENDER", rs.getString("emp_gender"));
					String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE,CF.getStrTimeZone());
					double dblYears = uF.parseToDouble(strDays) / 365;
					hmEmpPay.put("EMP_AGE", dblYears + "");
	
					hmEmpPay.put("EMP_IS_DISABLE_SAL_CALCULATE", rs.getString("is_disable_sal_calculate"));
					hmEmpPay.put("EMP_APPROVE_ATTENDANCE_ID", rs.getString("approve_attendance_id"));
					hmEmpPay.put("EMP_TOTAL_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("total_days"))));
					hmEmpPay.put("EMP_PAID_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_days"))));
					hmEmpPay.put("EMP_PRESENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("present_days"))));
					hmEmpPay.put("EMP_PAID_LEAVES", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_leaves"))));
					hmEmpPay.put("EMP_ABSENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("absent_days"))));
					String strEffectiveDate = rs.getString("sal_effective_date") != null ? uF.getDateFormat(rs.getString("sal_effective_date"), DBDATE, DATE_FORMAT) : "";
					hmEmpPay.put("SAL_EFFECTIVE_DATE", strEffectiveDate);
	
					if (rs.getString("service_id") != null) {
						// ----------------------------------Condition's for Multiple Sbu against single Employee 
					/*	String[] tempService = (rs.getString("service_id") != null && rs.getString("service_id").length()>1) ? (rs.getString("service_id").substring(1, rs.getString("service_id").length()-1)).split(",") : "".split(",");
						if (tempService.length > 0) {
							hmEmpPay.put("EMP_SERVICE_ID", tempService[0]);
						}*/
						
					    rs.getString("service_id").split(",");
						String tempService = rs.getString("service_id").replace(",", "");
						hmEmpPay.put("EMP_SERVICE_ID",tempService.trim());
					}
					
					Map<String, String> hmWLocation = (Map)hmWorkLocationMap.get(rs.getString("wlocation_id"));
					hmEmpPay.put("EMP_WLOCATION_NAME", hmWLocation.get("WL_NAME"));
	
					alEmp.add(hmEmpPay);
					hmEmp.put(rs.getString("emp_id"), hmEmpPay);
	
					int effectiveDtCnt = uF.parseToInt(hmEmpwiseEffectiveDateCnt.get(rs.getString("emp_id")));
					effectiveDtCnt++;
					hmEmpwiseEffectiveDateCnt.put(rs.getString("emp_id"), effectiveDtCnt+"");
					
					if (!alEmpIds.contains(rs.getString("emp_id"))) {
						alEmpIds.add(rs.getString("emp_id"));
					}
					
					hmEmpGradeMap.put(rs.getString("emp_id"), rs.getString("grade_id"));
					if (!alGradeId.contains(rs.getString("grade_id"))) {
						alGradeId.add(rs.getString("grade_id"));
					}
				}
				rs.close();
				pst.close();
//				System.out.println("hmEmpPay ====> " + hmEmp);
//				
				Map<String, String> hmEmpGradeWithEffectiveDate = new HashMap<String, String>();
				String strEmpActivity = ACTIVITY_PROMOTION_ID+","+ACTIVITY_INCREMENT_ID+","+ACTIVITY_CONFIRMATION_ID+","+ACTIVITY_DEMOTION_ID+","+ACTIVITY_LIFE_EVENT_ID;
				pst = con.prepareStatement("select * from employee_activity_details where activity_id in ("+strEmpActivity+") and effective_date between ? and ?");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()) {
					if (!alGradeId.contains(rs.getString("grade_id"))) {
						alGradeId.add(rs.getString("grade_id"));
					}
					hmEmpGradeWithEffectiveDate.put(rs.getString("emp_id")+"_"+rs.getString("effective_date"), rs.getString("grade_id"));
				}
				rs.close();
				pst.close();
				
//				System.out.println("alEmp ====> " + alEmp);
				int nGradeIdSize = alGradeId.size();
				if (alEmp.size() > 0 && alEmpIds.size() > 0 && nGradeIdSize > 0) {
					String strEmpIds = StringUtils.join(alEmpIds.toArray(), ",");
					String strGradeIds = StringUtils.join(alGradeId.toArray(), ",");	
					
					Map<String, String> hmGradeVDARate = new HashMap<String, String>();
					pst = con.prepareStatement("select * from vda_rate_details vrd, grades_details gd where vrd.desig_id>0 and " +
						"gd.designation_id= vrd.desig_id and grade_id in("+strGradeIds+") and from_date=? and to_date=?");
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					rs = pst.executeQuery();
					while(rs.next()) {
						hmGradeVDARate.put(rs.getString("grade_id")+"_PROBATION", rs.getString("vda_amount_probation"));
						hmGradeVDARate.put(rs.getString("grade_id")+"_PERMANENT", rs.getString("vda_amount_permanent"));
						hmGradeVDARate.put(rs.getString("grade_id")+"_TEMPORARY", rs.getString("vda_amount_temporary"));
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmEmpTypeStatus = new HashMap<String, String>();
					pst = con.prepareStatement("select emp_per_id,emp_status from employee_personal_details where is_alive is true and approved_flag = true");
					rs = pst.executeQuery();
					while(rs.next()) {
						hmEmpTypeStatus.put(rs.getString("emp_per_id"), rs.getString("emp_status"));
					}
					rs.close();
					pst.close();
					
					Map<String, Map<String, Map<String, String>>> hmSalaryDetails1 = new HashMap<String, Map<String, Map<String, String>>>();
					Map<String, String> hmSalaryDetails = new HashMap<String, String>();
					List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
					List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
					List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
					List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
					
					pst = con.prepareStatement("select * from salary_details where level_id in (select level_id from level_details where " +
						"level_id in (select dd.level_id from designation_details dd, grades_details gd where dd.designation_id=gd.designation_id " +
						"and gd.grade_id in("+strGradeIds+"))) and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false)" +
						" order by level_id, earning_deduction desc, salary_head_id, weight");
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
								if(!alEmpSalaryDetailsEarning.contains(rs.getString("salary_head_id"))) {
									alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
								}
								/*int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
								if (index >= 0) {
									alEmpSalaryDetailsEarning.remove(index);
									alEarningSalaryDuplicationTracer.remove(index);
									alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
								} else {
									alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
								}
		
								alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));*/
							} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
								if(!alEmpSalaryDetailsDeduction.contains(rs.getString("salary_head_id"))) {
									alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
								}
								/*int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
								if (index >= 0) {
									alEmpSalaryDetailsDeduction.remove(index);
									alDeductionSalaryDuplicationTracer.remove(index);
									alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
								} else {
									alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
								}
								alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));*/
							}
		
							hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
						}
	
					}
					rs.close();
					pst.close();
//					System.out.println("ApP/hmSalaryDetails==="+hmSalaryDetails);
	
					Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
					Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
					Map<String, String> hmEmpStateMap = new HashMap<String, String>();
					CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
					Map<String, String> hmEmpFlatTDSDeduct = CF.getEmpFlatTDSDeduction(con, uF);
	
					Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
					pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
					while (rs.next()) {
						hmOtherTaxDetails.put(rs.getString("state_id") + "_SERVICE_TAX", rs.getString("service_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id") + "_EDU_TAX", rs.getString("education_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id") + "_STD_TAX", rs.getString("standard_tax"));
						hmOtherTaxDetails.put(rs.getString("state_id") + "_FLAT_TDS", rs.getString("flat_tds"));
	
						hmOtherTaxDetails.put(rs.getString("state_id") + "_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
						hmOtherTaxDetails.put(rs.getString("state_id") + "_REBATE_AMOUNT", rs.getString("rebate_amt"));
	
						hmOtherTaxDetails.put(rs.getString("state_id") + "_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
						hmOtherTaxDetails.put(rs.getString("state_id") + "_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
						
						hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
						hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
						
						// dblInvestmentExemption = 100000;
					}
					rs.close();
					pst.close();
	
					pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
					// System.out.println(" pst==>"+pst);
					Map<String, String> hmHRAExemption = new HashMap<String, String>();
					while (rs.next()) {
						hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
						hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
						hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
						hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					}
					rs.close();
					pst.close();
	
					double dblInvestmentExemption = 0.0d;
					pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					rs = pst.executeQuery();
					// System.out.println(" pst==>"+pst);
					if (rs.next()) {
						dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
					}
					rs.close();
					pst.close();
	
					Map<String, Map<String, String>> hmEmpPaidSalary = new HashMap<String, Map<String, String>>();
					pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, emp_id from payroll_generation where "
						+ "financial_year_from_date=? and financial_year_to_date=? and paycycle=? and emp_id in (" + strEmpIds
						+ ") group by salary_head_id, emp_id order by emp_id");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strPC));
					rs = pst.executeQuery();
					String strEmpIdNew = null;
					String strEmpIdOld = null;
					Map<String, String> hmEmpPaidInner = new HashMap<String, String>();
					while (rs.next()) {
						strEmpIdNew = rs.getString("emp_id");
	
						if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
							hmEmpPaidInner = new HashMap<String, String>();
						}
	
						hmEmpPaidInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
	
						hmEmpPaidSalary.put(rs.getString("emp_id"), hmEmpPaidInner);
//	Need to add sal_effective_date logic for checking salary generated or not
						strEmpIdOld = strEmpIdNew;
					}
					rs.close();
					pst.close();
	
	//				Map<String, String> hmEmpExemptionsMap = getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
	//				Map<String, String> hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
	//				Map<String, String> hmEmpRentPaidMap = getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
	//				Map<String, String> hmEmpIncomeOtherSourcesMap = getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
					Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
					Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, getF_org());
					Map<String, String> hmLoanAmt = new HashMap<String, String>();
					List<String> alLoans = new ArrayList<String>();
					Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
					Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
	//				Map<String, Map<String, String>> hmEmpPaidAmountDetails = getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
	//				Map<String, String> hmFixedExemptions = getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
	
	//				Map<String, String> hmSalaryDetails = new HashMap<String, String>();
	//				List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
	//				List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
	//				List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
	//				List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
	//				pst = con.prepareStatement("select * from salary_details where salary_head_id not in (" + GROSS + "," + CTC + "," + REIMBURSEMENT_CTC + ") and org_id =? "
	//						+ "and (is_delete is null or is_delete=false) order by earning_deduction desc, salary_head_id, weight");
	//				pst.setInt(1, uF.parseToInt(getF_org()));
	//				rs = pst.executeQuery();
	//				while (rs.next()) {
	//					if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
	//						int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
	//
	//						if (index >= 0) {
	//							alEmpSalaryDetailsEarning.remove(index);
	//							alEarningSalaryDuplicationTracer.remove(index);
	//							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
	//						} else {
	//							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
	//						}
	//
	//						alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
	//					} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
	//						int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
	//						if (index >= 0) {
	//							alEmpSalaryDetailsDeduction.remove(index);
	//							alDeductionSalaryDuplicationTracer.remove(index);
	//							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
	//						} else {
	//							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
	//						}
	//						alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
	//					}
	//
	//					hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
	//				}
	//				rs.close();
	//				pst.close();
	
//					Map<String, Map<String, String>> hmEmpSalary = new LinkedHashMap<String, Map<String, String>>();
//					Map<String, String> hmEmpSalaryInner = new LinkedHashMap<String, String>();
//					pst = con.prepareStatement("select * from emp_salary_details esd, (select max(effective_date) as effective_date, emp_id from emp_salary_details "
//						+ "where effective_date<=? and is_approved= true group by emp_id) a where a.effective_date = esd.effective_date and esd.emp_id = a.emp_id "
//						+ "and a.emp_id in (select emp_id from employee_official_details where org_id = ? and emp_id in ("+ strEmpIds+ ")) " 
//						+ "and esd.salary_head_id in (select salary_head_id from salary_details where org_id =? and (is_delete is null or is_delete=false)) order by a.emp_id, esd.earning_deduction desc");
//					pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
//					pst.setInt(2, uF.parseToInt(getF_org()));
//					pst.setInt(3, uF.parseToInt(getF_org()));
//					// System.out.println("---------->"+pst);
//					rs = pst.executeQuery();
//					String strEmpIdNew1 = null;
//					String strEmpIdOld1 = null;
//					while (rs.next()) {
//						strEmpIdNew1 = rs.getString("emp_id");
//	
//						if (!alEmp.contains(strEmpIdNew1)) {
//							continue;
//						}
//	
//						if (strEmpIdNew1 != null && !strEmpIdNew1.equalsIgnoreCase(strEmpIdOld1)) {
//							hmEmpSalaryInner = new LinkedHashMap<String, String>();
//						}
//	
//						if (strEmpIdNew1 != null && strEmpIdNew1.length() > 0) {
//							hmEmpSalary.put(strEmpIdNew1, hmEmpSalaryInner);
//						}
//	
//						strEmpIdOld1 = strEmpIdNew1;
//					}
//					rs.close();
//					pst.close();
	
					Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
					if (hmArearAmountMap == null)hmArearAmountMap = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmIncrementArearAmountMap = CF.getIncrementArearDetails(con, uF, CF, strD2);
					if (hmIncrementArearAmountMap == null)hmIncrementArearAmountMap = new HashMap<String, Map<String, String>>();
					
					Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
					if (hmEmpServiceTaxMap == null) hmEmpServiceTaxMap = new HashMap<String, String>();
					Map<String, String> hmIncentives = CF.getIncentivesMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIncentives == null) hmIncentives = new HashMap<String, String>();
					Map<String, String> hmIndividualBonus = CF.getIndividualBonusMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIndividualBonus == null) hmIndividualBonus = new HashMap<String, String>();
	
					Map<String, String> hmIndividualOtherDeduction = CF.getIndividualOtherDeductionMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIndividualOtherDeduction == null) hmIndividualOtherDeduction = new HashMap<String, String>();
					Map<String, String> hmIndividualOtherEarning = CF.getIndividualOtherEarningMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIndividualOtherEarning == null) hmIndividualOtherEarning = new HashMap<String, String>();
					Map<String, String> hmIndividualOvertime = CF.getIndividualOvertimeMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIndividualOvertime == null) hmIndividualOvertime = new HashMap<String, String>();
					Map<String, String> hmIndividualTravelReimbursement = CF.getIndividualTravelReimbursementMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIndividualTravelReimbursement == null) hmIndividualTravelReimbursement = new HashMap<String, String>();
					Map<String, String> hmIndividualMobileReimbursement = CF.getIndividualMobileReimbursementMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIndividualMobileReimbursement == null) hmIndividualMobileReimbursement = new HashMap<String, String>();
					Map<String, String> hmIndividualOtherReimbursement = CF.getIndividualOtherReimbursementMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIndividualOtherReimbursement == null) hmIndividualOtherReimbursement = new HashMap<String, String>();
	//				System.out.println();
					Map<String, String> hmIndividualMobileRecovery = CF.getIndividualMobileRecoveryMap(con, uF, CF, strPC, strD1, strD2);
					if (hmIndividualMobileRecovery == null) hmIndividualMobileRecovery = new HashMap<String, String>();
					Map<String, String> hmReimbursement = CF.getReimbursementMap(con, uF, CF, strD1, strD2);
					if (hmReimbursement == null) hmReimbursement = new HashMap<String, String>();
					Map<String, String> hmVariables = new HashMap<String, String>();
					getVariableAmount(con, uF, hmVariables, strPC, strD1, strD2);
					
					Map<String, String> hmAnnualVariables = new HashMap<String, String>();
					getAnnualVariableAmount(con, uF, hmAnnualVariables, strD1, strD2, strPC);
					
					Map<String, String> hmAllowance = new HashMap<String, String>();
					getAllowanceAmount(con, uF, hmAllowance, strD1, strD2, strPC);
	
					Map<String, String> hmPrevEmpTdsAmount = new HashMap<String, String>();
					Map<String, String> hmPrevEmpGrossAmount = new HashMap<String, String>();
					getPrevEmpTdsAmount(con, uF, strFinancialYearStart, strFinancialYearEnd, hmPrevEmpTdsAmount, hmPrevEmpGrossAmount);
					
					Map<String, String> hmAnnualVarPolicyAmount = CF.getAnnualVariablePolicyAmount(con, uF, strFinancialYearStart, strFinancialYearEnd);
					if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>(); 
	
					Map<String, String> hmBasicSalaryMap = CF.getSpecificSalaryData(con, BASIC);
					if (hmBasicSalaryMap == null) hmBasicSalaryMap = new HashMap<String, String>();
					Map<String, String> hmDASalaryMap = CF.getSpecificSalaryData(con, DA);
					if (hmDASalaryMap == null) hmDASalaryMap = new HashMap<String, String>();
					LinkedHashMap<String, Map<String, String>> hmTotalSalary = new LinkedHashMap<String, Map<String, String>>();
					LinkedHashMap<String, Map<String, String>> hmTotalESICSalary = new LinkedHashMap<String, Map<String, String>>();
					LinkedHashMap<String, Map<String, String>> hmTotalSalaryisDisplay = new LinkedHashMap<String, Map<String, String>>();
					Map<String, String> hmInnerTemp = new HashMap<String, String>();
					
					pst = con.prepareStatement("select distinct(emp_id) as emp_id from arear_details where is_paid=false and " +
						"((arrear_type=1 and paycycle<=?) or (arrear_type=2 and is_approved=1 and effective_date<=?)) and emp_id in ("+strEmpIds+") ");
					pst.setInt(1, uF.parseToInt(strPC));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					rs = pst.executeQuery();
					List<String> alArrearEmp = new ArrayList<String>();
					while(rs.next()) {
						alArrearEmp.add(rs.getString("emp_id"));
					}
					rs.close();
					pst.close();
//					System.out.println("alArrearEmp==>"+alArrearEmp);
					
					Map<String, List<Map<String, String>>> hmEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
					Map<String, Map<String, String>> hmArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
					Map<String, List<String>> hmArrearEarningHead = new LinkedHashMap<String, List<String>>();
					Map<String, List<String>> hmArrearDeductionHead = new LinkedHashMap<String, List<String>>();
					Map<String, Map<String, String>> hmArrearEmployeePF = new LinkedHashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmArrearEmployerPF = new LinkedHashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmArrearEmployerESI = new LinkedHashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmArrearEmployeeLWF = new LinkedHashMap<String, Map<String, String>>();
					
					Map<String, List<Map<String, String>>> hmIncrementEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
					Map<String, Map<String, String>> hmIncrementArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
					Map<String, List<String>> hmIncrementArrearEarningHead = new LinkedHashMap<String, List<String>>();
					Map<String, List<String>> hmIncrementArrearDeductionHead = new LinkedHashMap<String, List<String>>();
					Map<String, Map<String, String>> hmIncrementArrearEmployeePF = new LinkedHashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmIncrementArrearEmployerPF = new LinkedHashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmIncrementArrearEmployerESI = new LinkedHashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmIncrementArrearEmployeeLWF = new LinkedHashMap<String, Map<String, String>>();
					Map<String, List<String>> hmIncrementArrearPaycycle = new HashMap<String, List<String>>();
					
					int nAlEmp = alEmp.size();
					Map<String, String> hmEmpEffectDtCnt = new HashMap<String, String>();
					Map<String, String> hmEmpTotGross = new HashMap<String, String>();
					Map<String, String> hmEmpTotGrossIsDisplay = new HashMap<String, String>();
					Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
					for (int i = 0; i < nAlEmp; i++) {
						Map<String, String> hmEmpPay = alEmp.get(i);
						String strEmpId = hmEmpPay.get("EMP_ID");
						String strOrgId = hmEmpOrgId.get(strEmpId);
						String strSalEffectiveDate = hmEmpPay.get("SAL_EFFECTIVE_DATE");
						String strEmpJoiningDate = hmEmpPay.get("EMP_JOINING_DATE");
						String strSalCalStatus = hmEmpPay.get("EMP_IS_DISABLE_SAL_CALCULATE");
						int empEffectDtCnt = uF.parseToInt(hmEmpEffectDtCnt.get(strEmpId));
						empEffectDtCnt++;
						hmEmpEffectDtCnt.put(strEmpId, empEffectDtCnt+"");
						int nEmpwiseEffectDtCnt = uF.parseToInt(hmEmpwiseEffectiveDateCnt.get(strEmpId));
						int nEmpId = uF.parseToInt(strEmpId);
						String strLocation = hmEmpWlocationMap.get(strEmpId);
						String strLevel = hmEmpLevelMap.get(strEmpId);
						String strGrade = hmEmpGradeMap.get(strEmpId);
						if(hmEmpGradeWithEffectiveDate != null && hmEmpGradeWithEffectiveDate.get(strEmpId+"_"+strSalEffectiveDate) != null) {
							strGrade = hmEmpGradeWithEffectiveDate.get(strEmpId+"_"+strSalEffectiveDate);
						}
//						System.out.println("strGrade 1 ===>> " + strGrade);
						String strEmpVDAAmount = hmGradeVDARate.get(strGrade+"_"+hmEmpTypeStatus.get(strEmpId));
						
						int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
						String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
	
						Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strLevel);
						if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
						
						int nTotalNumberOfDaysForCalc = (int) uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"));
	//					System.out.println("nTotalNumberOfDaysForCalc----->"+nTotalNumberOfDaysForCalc);
						double dblTotalPresentDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"));
	//					System.out.println("dblTotalPresentDays----->"+dblTotalPresentDays); 
	
						double dblIncrementBasic = getIncrementCalculationBasic(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
						double dblIncrementDA = getIncrementCalculationDA(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
	
						Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
						Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
	
//						System.out.println("strSalEffectiveDate ===>> " + strSalEffectiveDate);
						String strTmpDt = strD2;
						
						if(strSalEffectiveDate != null && !strSalEffectiveDate.equals("")) {
							Date dtSalEffectiveDate = uF.getDateFormatUtil(strSalEffectiveDate, DATE_FORMAT);
							Date dtEmpJoiningDate = uF.getDateFormatUtil(strEmpJoiningDate, DATE_FORMAT);
							if(dtSalEffectiveDate.after(dtEmpJoiningDate)) {
								strTmpDt = strSalEffectiveDate;
							} else {
								strTmpDt = strEmpJoiningDate;
							}
						}
//						System.out.println("Ap/4924---hmSalInner=="+hmSalInner);
						hmInner = CF.getSalaryCalculation(con, hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strTmpDt, hmSalInner, strEmpVDAAmount, strSalCalStatus);
//						System.out.println("ApP/4926---SGST="+hmInner.get(CGST+"")+"---SGST="+hmInner.get(SGST+""));
						Map<String, String> hmPaidSalaryInner = hmEmpPaidSalary.get(strEmpId);
	
						Map<String, String> hmPerkAlignAmount = new HashMap<String, String>();
						CF.getPerkAlignAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel),hmPerkAlignAmount);
	
						double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
						double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
						
//						System.out.println();
						/*if(uF.parseToInt(strEmpId) == 421) {
							System.out.println("AP/4937--Arrears--"+hmInner.get("20027"));
							System.out.println("AP/4938--Arrears--"+hmInner.containsKey(""+AREARS)+"---"+alArrearEmp.contains(strEmpId));
						}*/
						if(alArrearEmp.contains(strEmpId) && hmInner!=null && hmInner.containsKey(""+AREARS)) {
							ArrearPay arrearPay = new ArrearPay();
							arrearPay.request = request;
							arrearPay.session = session;
							arrearPay.CF = CF;
							arrearPay.setStrEmpId(""+nEmpId);
							arrearPay.hmOrg = hmOrg;
							arrearPay.hmPaymentModeMap = hmPaymentModeMap;
							arrearPay.setStrLocation(strLocation);
							arrearPay.setStrLevel(strLevel);
							arrearPay.setStrEmpGender(strEmpGender);
							arrearPay.hmSalInner = hmSalInner;
							arrearPay.hmEmpServiceTaxMap = hmEmpServiceTaxMap;
							arrearPay.hmEmpStateMap = hmEmpStateMap;
							arrearPay.hmOtherTaxDetails = hmOtherTaxDetails;
							
							arrearPay.payArrear(con, uF, strD1, strD2, strPC,nTotalNumberOfDaysForCalc, hmEmpArrear, hmArrearCalSalary, hmArrearEarningHead,
								hmArrearDeductionHead, hmArrearEmployeePF, hmArrearEmployerPF, hmArrearEmployerESI, hmArrearEmployeeLWF);
							
//							System.out.println("payIncrementArrear ----------->> ");
							arrearPay.payIncrementArrear(con, uF, strD1, strD2, strPC, hmIncrementEmpArrear, hmIncrementArrearCalSalary, hmIncrementArrearEarningHead, 
								hmIncrementArrearDeductionHead, hmIncrementArrearEmployeePF, hmIncrementArrearEmployerPF, hmIncrementArrearEmployerESI, 
								hmIncrementArrearEmployeeLWF, hmIncrementArrearPaycycle);
						}
//						System.out.println("hmIncrementEmpArrear ===>> " + hmIncrementEmpArrear + " -- hmIncrementArrearCalSalary ===>> " + hmIncrementArrearCalSalary);
						
						
						if (hmIndividualOtherEarning.size() > 0 && !hmInner.containsKey(OTHER_EARNING + "")) {
							hmInnerTemp = new HashMap<String, String>();
							hmInnerTemp.put("AMOUNT", "0");
							hmInnerTemp.put("EARNING_DEDUCTION", "E");
							hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
							hmInner.put(OTHER_EARNING + "", hmInnerTemp);
						}
	
						if (hmEmpServiceTaxMap.size() > 0 && !hmInner.containsKey(CGST + "")) {
							hmInnerTemp = new HashMap<String, String>();
							hmInnerTemp.put("AMOUNT", "0");
							hmInnerTemp.put("EARNING_DEDUCTION", "E");
							hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
							hmInner.put(CGST + "", hmInnerTemp); 
							
							if(!alEmpSalaryDetailsEarning.contains(""+CGST)) {
								alEmpSalaryDetailsEarning.add(""+CGST);
								hmSalaryDetails.put(""+CGST, "CGST");
							}
	
							hmInnerTemp = new HashMap<String, String>();
							hmInnerTemp.put("AMOUNT", "0");
							hmInnerTemp.put("EARNING_DEDUCTION", "E");
							hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
							hmInner.put(SGST + "", hmInnerTemp);
							
							if(!alEmpSalaryDetailsEarning.contains(""+SGST)) {
								alEmpSalaryDetailsEarning.add(""+SGST);
								hmSalaryDetails.put(""+SGST, "SGST");
							}
						}
	
						if (hmInner.size() > 0 && hmInner.containsKey(TDS + "")) {
							hmInnerTemp = new HashMap<String, String>();
							hmInnerTemp = hmInner.get(TDS + "");
							hmInnerTemp.put("AMOUNT", hmInnerTemp.get("AMOUNT"));
							hmInnerTemp.put("EARNING_DEDUCTION", "D");
							hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
							hmInner.remove(TDS + "");
							hmInner.put(TDS + "", hmInnerTemp);
						}
						
				//===start parvez date: 20-01-2023===		
						if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_GRATUITY_PAID_ON_APPROVED_AMOUNT))){
							
							if (hmInner.size() > 0 && !hmInner.containsKey(GRATUITY + "")) {
								hmInnerTemp = new HashMap<String, String>();
								double gratuityAmount = getAppliedGratuityAmount(con,uF,strPC,strEmpId);
								hmInnerTemp.put("AMOUNT", gratuityAmount + "");
								hmInnerTemp.put("EARNING_DEDUCTION", "E");
								hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
								hmInner.put(GRATUITY + "", hmInnerTemp);
							}
						}
				//===end parvez date: 20-01-2023===		
	
						Map<String, String> hmTotal = new HashMap<String, String>();
						Map<String, String> hmTotalESIC = new HashMap<String, String>();
						Iterator<String> it = hmInner.keySet().iterator();
	
						double dblGrossPT = 0;
						double dblGrossForPT = uF.parseToDouble(hmEmpTotGross.get(strEmpId));
						double dblGross = 0;
						double dblGrossTDS = 0;
						double dblDeduction = 0;
						// double dblPerkTDS = 0.0d;
						Set<String> setContriSalHead = new HashSet<String>();
						
//						System.out.println("hmPaidSalaryInner ===>> " + hmPaidSalaryInner);
						
						while (it.hasNext()) {
							String strSalaryId = it.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
//							System.out.println("APay4988--nSalayHead="+nSalayHead);
	
							Map<String, String> hm = hmInner.get(strSalaryId);
							if (hm == null)
								hm = new HashMap<String, String>();
	
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
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")) {
								
								if (hmPaidSalaryInner != null) {
									dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossForPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
									/*if(uF.parseToInt(strEmpId) == 361) {
										System.out.println("AP/5070--strSalaryId--"+strSalaryId+"---Amount==="+hmPaidSalaryInner.get(strSalaryId));
									}*/
								} else {
	
									if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
										
									switch (nSalayHead) {
										case OVER_TIME :
	
											double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
											dblGross += dblOverTime;
											dblGrossForPT += dblOverTime;
											dblGrossTDS += dblOverTime;
	
											break;
	
										case LEAVE_ENCASHMENT :
//											double leaveEncashmentAmt = 0.0d;//getLeaveEncashmentAmtDetails(con, uF, CF, strEmpId, 0.0d, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, dblIncrementBasic, dblIncrementDA);
									//===start parvez date: 15-04-2022===		
											double leaveEncashmentAmt = getLeaveEncashmentAmtDetails(con, uF, CF, strEmpId, 0.0d, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, dblIncrementBasic, dblIncrementDA);
									//===end parvez date: 15-04-2022===		
//											System.out.println("APay/5036--leaveEncashmentAmt="+leaveEncashmentAmt);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),leaveEncashmentAmt));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),leaveEncashmentAmt));
											dblGross += leaveEncashmentAmt;
											dblGrossTDS += leaveEncashmentAmt;
	
											break;
	
										case BONUS :
											if(hmIndividualBonus != null && !hmIndividualBonus.isEmpty()) {
												double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
												//System.out.println("AP/5050--dblBonusAmount="+dblBonusAmount);
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
												dblGross += dblBonusAmount;
												dblGrossForPT += dblBonusAmount;
												dblGrossTDS += dblBonusAmount;
											}
											break;
	
										case EXGRATIA :
	
											double dblExGratiaAmount = getExGratiaAmount(con, uF, CF, strEmpId, strD1, strD2, strPC);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
											dblGross += dblExGratiaAmount;
											
											dblGrossForPT += dblExGratiaAmount;
											dblGrossTDS += dblExGratiaAmount;
	
											break;
	
										case AREARS :
	
											double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
											/*if(uF.parseToInt(strEmpId) == 421) {
												System.out.println("AP/5130--Arrears--dblArearAmount==>"+dblArearAmount);
											}*/
											
											dblArearAmount += CF.getIncrementArearCalculation(uF, strEmpId, hmIncrementArearAmountMap);
											
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblArearAmount += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get("GROSS"))));
													/*if(uF.parseToInt(strEmpId) == 421) {
														System.out.println("AP/5148--Arrears--dblArearAmount==>"+hmArrearTotal.get("GROSS"));
													}*/
												}	
											}
											
	//										System.out.println("dblArearAmount 1 ===>> " + dblArearAmount);
											/*if(uF.parseToInt(strEmpId) == 421) {
												System.out.println("AP/5155--Arrears--dblArearAmount==>"+dblArearAmount+"---strSalaryId=="+strSalaryId);
											}*/
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
											
											dblGross += dblArearAmount;
											dblGrossForPT += dblArearAmount;
											dblGrossTDS += dblArearAmount;
	
											break;
	
										case INCENTIVES :
											double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmIncentives, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
											dblGross += dblIncentiveAmount;
											dblGrossForPT += dblIncentiveAmount;
											dblGrossTDS += dblIncentiveAmount;
											break;
	
										case REIMBURSEMENT :
											double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
											dblGross += dblReimbursementAmount;
											dblGrossForPT += dblReimbursementAmount;
											break;
	
										case TRAVEL_REIMBURSEMENT :
											double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
											dblGross += dblTravelReimbursementAmount;
											dblGrossForPT += dblTravelReimbursementAmount;
											break;
	
										case MOBILE_REIMBURSEMENT :
											double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
											dblGross += dblMobileReimbursementAmount;
											dblGrossForPT += dblMobileReimbursementAmount;
											break;
	
										case OTHER_REIMBURSEMENT :
											double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
											dblGross += dblOtherReimbursementAmount;
											dblGrossForPT += dblOtherReimbursementAmount;
											break;
	
										case OTHER_EARNING :
											double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
											dblGross += dblOtherEarningAmount;
											dblGrossForPT += dblOtherEarningAmount;
											break;
	
										case SERVICE_TAX :
											double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
	
											/**
											 * @author Vipin 25-Mar-2014 KP
											 *         Condition
											 * @comment = service tax is not
											 *          included while calculating
											 *          TDS
											 * */
	
											dblGross += dblServiceTaxAmount;
											dblGrossForPT += dblServiceTaxAmount;
											dblGrossPT += dblServiceTaxAmount;
											dblGrossTDS += dblServiceTaxAmount;
	
											break;
	
										case SWACHHA_BHARAT_CESS :
											double dblGrossAmt = dblGross;
											double dblServiceTaxAmt = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
											dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
											double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
											dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
	
											double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId),
													hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));
											dblGross += dblSwachhaBharatCess;
											dblGrossForPT += dblSwachhaBharatCess;
											dblGrossPT += dblSwachhaBharatCess;
											dblGrossTDS += dblSwachhaBharatCess;
	
											break;
	
										case KRISHI_KALYAN_CESS :
											double dblGrossAmt1 = dblGross;
											double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
											dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
											double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS + ""));
											dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
	
											double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId),
													hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
											
											dblGross += dblKrishiKalyanCess;
											dblGrossForPT += dblKrishiKalyanCess;
											dblGrossPT += dblKrishiKalyanCess;
											dblGrossTDS += dblKrishiKalyanCess;
	
											break;
											
										case CGST :
											double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
	
											dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
											dblGrossForPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
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
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
	
											dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
											dblGrossForPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
											dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
											dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
	
											break;
	
										default :
	
											if (uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
												double dblPerkAlignAmount = 0.0d;
												if (hmPerkAlignAmount.containsKey(strSalaryId)) {
													dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
												}
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount));
												dblGross += dblPerkAlignAmount;
												dblGrossForPT += dblPerkAlignAmount;
												dblGrossPT += dblPerkAlignAmount;
												dblGrossTDS += dblPerkAlignAmount;
												
											} else if (!uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossForPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												
											} else if (uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
												if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
													hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
													dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossForPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													
												} else {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
													hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT_ACTUAL"))));
													dblGross += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossForPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
													
												}
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossForPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
												
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
												dblGross += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossForPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												
											} else if (uF.parseToInt(strSalaryId) != GROSS) {
												boolean isMultipePerWithParticularHead = false;
												if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
													isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
												}
												if(!isMultipePerWithParticularHead || uF.parseToBoolean(strSalCalStatus)) {
//													System.out.println("strSalaryId ===>> " + strSalaryId + " -- Amount ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
													hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT_ACTUAL"))));
													/*if(uF.parseToInt(strEmpId) == 419) {
														System.out.println("AP/5348--strSalaryId=="+strSalaryId+"--hm.get(AMOUNT)==>"+hm.get("AMOUNT"));
													}*/
													dblGross += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossForPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
												}
											}
											
											break;
										}
									} else {
										if (uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
											
										} else if (!uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											
										} else if (uF.parseToBoolean(hm.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
											if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												
											} else {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT_ACTUAL"))));
												dblGross += uF.parseToDouble(hm.get("AMOUNT"));
												dblGrossForPT += uF.parseToDouble(hm.get("AMOUNT"));
												dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
												dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
											}
										} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
											
										} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
											
										} else if (uF.parseToInt(strSalaryId) != GROSS) {
											boolean isMultipePerWithParticularHead = false;
											if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
												isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
											}
//											System.out.println(strSalaryId + " --- in else E isMultipePerWithParticularHead ===>> " + isMultipePerWithParticularHead);
											if(!isMultipePerWithParticularHead || uF.parseToBoolean(strSalCalStatus)) {
//												System.out.println("in else E strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT_ACTUAL"))));
												dblGross += uF.parseToDouble(hm.get("AMOUNT"));
												dblGrossForPT += uF.parseToDouble(hm.get("AMOUNT"));
												dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
												dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
											}
										}
										
									}
									
								}
	
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
								/**
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */
								if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
									switch (nSalayHead) {
		
										/********** EPF EMPLOYEE CONTRIBUTION *************/
										case EMPLOYEE_EPF :
		//									System.out.println("EMPLOYEE_EPF ====>> ");
											if (hmPaidSalaryInner != null) {
												double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											} else {
		
												Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");
												double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null, null, null, null);
												
												if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
													List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
													if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
													for(Map<String,String> hmApplyArear : alArrear) {
														int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
														
														Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
														if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
														
														dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
													}	
												}
												
												if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
													List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
													if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
													for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
														int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
														List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
														for(String strPaycycle : alArrearPaycycle) {
															Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
															if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
															
															dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+EMPLOYEE_EPF))));
														}
													}	
												}
												/*if(uF.parseToInt(strEmpId)==370){
													System.out.println("Ap/5476---dblEEPF 1 ===>> " + dblEEPF);
												}*/
												
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
											}
		
											break;
		
										/********** EPF EMPLOYER CONTRIBUTION *************/
										case EMPLOYER_EPF :
											if (hmPaidSalaryInner != null) {
												double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											} else {
												double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null, null, null, null);
												/*if(uF.parseToInt(strEmpId)==370){
													System.out.println("ApP/5493---dblERPF="+dblERPF);
												}*/
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
											}
		
											break;
		
										case LOAN :
											if (hmPaidSalaryInner != null) {
												double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
												CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, strEmpId);
											} else {
		
												double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans, strD1);
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
												
												if (true) {
													// dblGrossTDS = dblGross -
													// dblLoanAmt;
//													dblGrossTDS = dblGrossTDS - dblLoanAmt;
												}
											}
		
											break;
		
										case MOBILE_RECOVERY :
											if (hmPaidSalaryInner != null) {
												double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
											} else {
												double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
														strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
											}
		
											break;
		
										default :
											if (hmPaidSalaryInner != null) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
											} else {
												if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
													hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
													hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"))));
													hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"))));
												} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
														&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
														&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
													hmTotal.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
													hmTotalESIC.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT_ACTUAL"))));
												}
											}
											break;
									}
								} else {

									switch (nSalayHead) {
										/********** EPF EMPLOYEE CONTRIBUTION *************/
										case EMPLOYEE_EPF :
											if (hmPaidSalaryInner != null) {
												double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											} else {
		
												Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");
												double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
												
												/*if(uF.parseToInt(strEmpId)==370){
													System.out.println("AP/5574---dblEEPF=="+dblEEPF);
												}*/
												
												if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
													List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
													if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
													for(Map<String,String> hmApplyArear : alArrear) {
														int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
														
														Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
														if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
														
														dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
													}	
												}
												
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
											}
		
											break;
		
										/********** EPF EMPLOYER CONTRIBUTION *************/
										case EMPLOYER_EPF :
											if (hmPaidSalaryInner != null) {
												double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											} else {
												double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
												
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
											}
		
											break;
		
										default :
											
										if (hmPaidSalaryInner != null) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
											hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
										} else {
		
											if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
												
											} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
//												System.out.println("in else D Deduction strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
												hmTotalESIC.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT_ACTUAL"))));
											}
										}
									}
								
								}
							}
							hmTotal.put("SALARY_HEAD_ID", strSalaryId);
							hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
//						System.out.println("dblGross 0 ===>> " + dblGross);
						
						
						
						Map<String, String> hmContriSalHeadAmt = new HashMap<String, String>();
						if(!uF.parseToBoolean(strSalCalStatus)) {
						/**
						 * First time Multiple cal start
						 * */
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
							double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null, null, null, null);
							
							hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
						}
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
							double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC, hmTotalESIC);
							hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
						}
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
							double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
							hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
						}
						Iterator<String> itMulti = hmInner.keySet().iterator();
						while (itMulti.hasNext()) {
							String strSalaryId = itMulti.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
							Map<String, String> hm = hmInner.get(strSalaryId);
							if (hm == null) {
								hm = new HashMap<String, String>();
							}
//							List<String> alMultiCal = new ArrayList<String>();
//							if(hm.get("MULTIPLE_CALCULATION") != null) {
//								String strMulCal = hm.get("MULTIPLE_CALCULATION");
//								alMultiCal = Arrays.asList(strMulCal.trim().split(","));
//							}
							
							String str_E_OR_D = hm.get("EARNING_DEDUCTION");
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
								if (hmPaidSalaryInner != null) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
									
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt, true);
//									System.out.println("strSalaryId ===>> " + strSalaryId + " -- dblMulCalAmt ===>> " + dblMulCalAmt);
									if(!hmTotal.containsKey(strSalaryId)) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
										dblGross += dblMulCalAmt;
										dblGrossForPT += dblMulCalAmt;
										dblGrossTDS += dblMulCalAmt;
									}
//									if(hmTotal.containsKey(strSalaryId) && alMultiCal != null && (alMultiCal.contains(EMPLOYEE_EPF+"") || alMultiCal.contains(EMPLOYER_EPF+"") || alMultiCal.contains(EMPLOYEE_ESI+"") || alMultiCal.contains(EMPLOYER_ESI+"") || alMultiCal.contains(EMPLOYEE_LWF+"") || alMultiCal.contains(EMPLOYER_LWF+""))) {
//										dblGross += dblMulCalAmt;
//										dblGrossForPT += dblMulCalAmt;
//										dblGrossTDS += dblMulCalAmt;
//									}
								}
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")
									&& (hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
								if (hmPaidSalaryInner != null) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt, true);
//									System.out.println("D strSalaryId ===>> " + strSalaryId + " -- dblMulCalAmt ===>> " + dblMulCalAmt);
									if(!hmTotal.containsKey(strSalaryId)) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
										dblDeduction += dblMulCalAmt;
									}
								}
							}
	
							hmTotal.put("SALARY_HEAD_ID", strSalaryId);
							hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
						/**
						 * Multiple cal end
						 * */
						}
						
//						System.out.println("dblGross 00 ===>> " + dblGross);
						
						/**
						 * Other cal start
						 * */
						Iterator<String> itOther = hmInner.keySet().iterator();
						while (itOther.hasNext()) {
							String strSalaryId = itOther.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
							Map<String, String> hm = hmInner.get(strSalaryId);
							if (hm == null) {
								hm = new HashMap<String, String>();
							}
							String str_E_OR_D = hm.get("EARNING_DEDUCTION");
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
	
								/**
								 * Second Time TAX CALCULATION STARTS HERE
								 * 
								 * */
								if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
									switch (nSalayHead) {
		
										/********** EPF EMPLOYEE CONTRIBUTION *************/
										case EMPLOYEE_EPF :
//											System.out.println("EMPLOYEE_EPF ====>> Second Time ");
											if (hmPaidSalaryInner != null) {
												double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												dblDeduction += dblPt;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											} else {
		
												Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");
		
												double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null, null, null, null);
												
		//										System.out.println("dblEEPF ===>> " + dblEEPF);
												if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
													List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
													if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
													for(Map<String,String> hmApplyArear : alArrear) {
														int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
														
														Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
														if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
														
														dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
													}	
												}
		//										System.out.println("dblEEPF 0 ===>> " + dblEEPF);
												if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
													List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
													if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
													for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
														int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
														List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
														for(String strPaycycle : alArrearPaycycle) {
															Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
															if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
															
															dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+EMPLOYEE_EPF))));
														}
													}	
												}
//												System.out.println("dblEEPF 1 ===>> " + dblEEPF);
												
												dblDeduction += dblEEPF;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
											}
		
											break;
		
										/********** EPF EMPLOYER CONTRIBUTION *************/
										case EMPLOYER_EPF :
											if (hmPaidSalaryInner != null) {
												double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												dblDeduction += dblPt;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											} else {
												double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null, null, null, null);
												
												dblDeduction += dblERPF;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
											}
		
											break;
		
										case LOAN :
											if (hmPaidSalaryInner != null) {
												double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												dblDeduction += dblLoan;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
												CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, strEmpId);
											} else {
//												System.out.println("in Loan");
												double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans, strD1);
//												System.out.println("in dblLoanAmt ===>> " + dblLoanAmt);
												dblDeduction += dblLoanAmt;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
		
												if (true) {
													// dblGrossTDS = dblGross -
													// dblLoanAmt;
													dblGrossTDS = dblGrossTDS - dblLoanAmt;
												}
											}
		
											break;
		
										case MOBILE_RECOVERY :
											if (hmPaidSalaryInner != null) {
												double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												dblDeduction += dblLoan;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
											} else {
												double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
														strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
												dblDeduction += dblIndividualMobileRecoveryAmt;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
											}
		
											break;
		
										default :
											if (hmPaidSalaryInner != null) {
												dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
											} else {
												if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
													dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
													dblDeduction += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
												} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"))));
													dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"));
												} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
														&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
														&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
													hmTotal.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
													dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));
												}
											}
											break;
									}
								} else {

									switch (nSalayHead) {
										/********** EPF EMPLOYEE CONTRIBUTION *************/
										case EMPLOYEE_EPF :
											if (hmPaidSalaryInner != null) {
												double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												dblDeduction += dblPt;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											} else {
		
												Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");
		
												double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
												
												
												if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
													List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
													if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
													for(Map<String,String> hmApplyArear : alArrear) {
														int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
														
														Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
														if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
														
														dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
													}	
												}
												
												dblDeduction += dblEEPF;
												/*if(uF.parseToInt(strEmpId)==116){
													System.out.println("AP/5810--dblEEPF="+dblEEPF);
												}*/
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
											}
		
											break;
		
										/********** EPF EMPLOYER CONTRIBUTION *************/
										case EMPLOYER_EPF :
											if (hmPaidSalaryInner != null) {
												double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
												dblDeduction += dblPt;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											} else {
												double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
												
												dblDeduction += dblERPF;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
											}
		
											break;
		
										default :
											
										if (hmPaidSalaryInner != null) {
											dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
										} else {
		
											if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
												
											} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
//												System.out.println("in else D Deduction strSalaryId ===>> " + strSalaryId + " AMOUNT ===>> " + uF.parseToDouble(hm.get("AMOUNT")));
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
												dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));
											}
										}
									}
								
								}
								
								
								/**
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */
								switch (nSalayHead) {
									/********** TAX *************/
									case PROFESSIONAL_TAX :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											/**
											 * KP Condition
											 * 
											 * */
											if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
												double dblPt = calculateProfessionalTax(con, uF, strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
												dblDeduction += dblPt;
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											}
										}
	
										break;
	
									/********** TDS *************/
									case TDS :
										if (hmPaidSalaryInner != null) {
											double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblTDS;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
										} else {
//											System.out.println("in TDS 0 ===========>> ");
											if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
//												System.out.println("in TDS 1 ===========>> ");
												double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_FLAT_TDS"));
												String strEmpFlatTDS = hmEmpFlatTDSDeduct.get(strEmpId);
												if(strEmpFlatTDS!=null) {
													dblFlatTDS = uF.parseToDouble(strEmpFlatTDS);	
												}
//												System.out.println("in TDS 2 dblFlatTDS ===========>> " + dblFlatTDS);
												if (hmEmpServiceTaxMap.containsKey(strEmpId)) {
													dblGrossTDS = dblGross;
													
													double dblCGST = uF.parseToDouble(hmTotal.get(CGST + ""));
													dblGrossTDS = dblGrossTDS - dblCGST;
		
													double dblSGST = uF.parseToDouble(hmTotal.get(SGST + ""));
													dblGrossTDS = dblGrossTDS - dblSGST;
												}
		
												double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
														strFinancialYearEnd, strEmpId, hmEmpLevelMap);
//												System.out.println("in TDS 3 dblTDS===========>> " + dblTDS);
												/*if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
													List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
													if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
													for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
														int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
														List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
														for(String strPaycycle : alArrearPaycycle) {
															Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
															if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
															
															dblTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+TDS))));
														}
													}	
												}*/
												
												dblDeduction += dblTDS;
		
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
											}
										}
										break;
										
										/********** ESI EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_ESI :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC, hmTotalESIC);
											dblESI = Math.ceil(dblESI);
											
											dblDeduction += dblESI;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
										}
	
										break;
									/********** /ESI EMPLOYER CONTRIBUTION *************/
	
									/********** ESI EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_ESI :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											
											double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, hmAnnualVariables, strD1, strD2, strPC, hmTotalESIC);
											/*if(uF.parseToInt(strEmpId) == 384){
												System.out.println("AP/6041--- dblESI ===>> " + dblESI);
											}*/
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblESI += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_ESI))));
												}	
											}
											
											if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
												List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
												if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
													int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
													List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
													for(String strPaycycle : alArrearPaycycle) {
														Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
														if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
														
														dblESI += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+EMPLOYEE_ESI))));
													}
												}	
											}
											
											dblESI = Math.ceil(dblESI);
											
											dblDeduction += dblESI;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
										}
	
										break;
									/********** /ESI EMPLOYEE CONTRIBUTION *************/	
										
									/********** LWF EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_LWF :
										if (hmPaidSalaryInner != null) {
											double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblLWF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										} else {
											double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
											dblDeduction += dblLWF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										}
	
										break;
									/********** /LWF EMPLOYER CONTRIBUTION *************/
	
									/********** LWF EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_LWF :
										if (hmPaidSalaryInner != null) {
											double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblLWF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										} else {
											double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, hmAnnualVariables, strOrgId);
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblLWF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_LWF))));
												}
											}
											
											if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
												List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
												if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
													int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
													List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
													for(String strPaycycle : alArrearPaycycle) {
														Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
														if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
														
														dblLWF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+EMPLOYEE_LWF))));
													}
												}
											}
											
											dblDeduction += dblLWF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										}
	
										break;
									/********** /LWF EMPLOYEE CONTRIBUTION *************/	
	
								}
							}
	
							hmTotal.put("SALARY_HEAD_ID", strSalaryId);
							hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), Math.round(dblGross)));
						}
						/**
						 * Other cal end
						 * */
	
//						System.out.println("dblGross 1 ===>> " + dblGross);
//						System.out.println("hmTotal 1 ===>> " + hmTotal);
						if(!uF.parseToBoolean(strSalCalStatus)) {
						/**
						 * Second Time Multiple cal start
						 * */
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
							double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null, null, null, null);
							
							hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
						}
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
							double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC, hmTotalESIC);
							hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
						}
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
							double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
							hmContriSalHeadAmt.put(""+EMPLOYER_LWF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblLWF));
						}
						Iterator<String> itMulti = hmInner.keySet().iterator();
						while (itMulti.hasNext()) {
							String strSalaryId = itMulti.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
							Map<String, String> hm = hmInner.get(strSalaryId);
							if (hm == null) {
								hm = new HashMap<String, String>();
							}
							List<String> alMultiCal = new ArrayList<String>();
							if(hm.get("MULTIPLE_CALCULATION") != null) {
								String strMulCal = hm.get("MULTIPLE_CALCULATION");
								alMultiCal = Arrays.asList(strMulCal.trim().split(","));
							}
//							System.out.println(strSalaryId +" -- MULTIPLE_CALCULATION ===>> " + hm.get("MULTIPLE_CALCULATION") +" -- alMultiCal ===>> "  +alMultiCal);
							String str_E_OR_D = hm.get("EARNING_DEDUCTION");
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
								if (hmPaidSalaryInner != null) {
									dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossForPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
//									System.out.println("E dblGross ===>> " + dblGross);
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt, true);
//									if(uF.parseToInt(strSalaryId) == 474) {
//										System.out.println("alMultiCal.contains(EMPLOYEE_EPF) ====>>> " + alMultiCal.contains(EMPLOYEE_EPF+""));
//										System.out.println("dblMulCalAmt ====>>> " + dblMulCalAmt);
//									}
//									System.out.println("2 strSalaryId ===>> " + strSalaryId + " -- dblMulCalAmt ===>> " + dblMulCalAmt);
									if(hmTotal.containsKey(strSalaryId) && alMultiCal != null && (alMultiCal.contains(EMPLOYEE_EPF+"") || alMultiCal.contains(EMPLOYER_EPF+"") || alMultiCal.contains(EMPLOYEE_ESI+"") || alMultiCal.contains(EMPLOYER_ESI+"") || alMultiCal.contains(EMPLOYEE_LWF+"") || alMultiCal.contains(EMPLOYER_LWF+""))) {
//										System.out.println(strSalaryId+ " -- E dblGross else ===>> " + hmTotal.get(strSalaryId));
										dblGross -= uF.parseToDouble(hmTotal.get(strSalaryId));
										dblGrossForPT -= uF.parseToDouble(hmTotal.get(strSalaryId));
										dblGrossTDS -= uF.parseToDouble(hmTotal.get(strSalaryId));
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblMulCalAmt));
										dblGross += dblMulCalAmt;
										dblGrossForPT += dblMulCalAmt;
										dblGrossTDS += dblMulCalAmt;
//										System.out.println("E dblGross else ===>> " + dblGross);
									}
								}
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
								if (hmPaidSalaryInner != null) {
									dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt, true);
//									System.out.println("2 D strSalaryId ===>> " + strSalaryId + " -- dblMulCalAmt ===>> " + dblMulCalAmt);
									if(hmTotal.containsKey(strSalaryId) && alMultiCal != null && (alMultiCal.contains(EMPLOYEE_EPF+"") || alMultiCal.contains(EMPLOYER_EPF+"") || alMultiCal.contains(EMPLOYEE_ESI+"") || alMultiCal.contains(EMPLOYER_EPF+"") || alMultiCal.contains(EMPLOYEE_LWF+"") || alMultiCal.contains(EMPLOYER_LWF+""))) {
										dblDeduction -= uF.parseToDouble(hmTotal.get(strSalaryId));
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
										dblDeduction += dblMulCalAmt;
									}
								}
							}
	
							hmTotal.put("SALARY_HEAD_ID", strSalaryId);
							hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
						/**
						 * Multiple cal end
						 * */
						}
//						System.out.println("dblGross 2 ===>> " + dblGross);
						
						String strCurrencyId = hmEmpCurrency.get(strEmpId);
						Map<String, String> hmCurrency = hmCurrencyDetails.get(strCurrencyId);
						if (hmCurrency == null)
							hmCurrency = new HashMap<String, String>();
						
						hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction)));
//						System.out.println("strEmpId ===>> " + strEmpId + " == hmTotal ===>> " + hmTotal);
						
						hmTotalSalary.put(strEmpId+"_"+strSalEffectiveDate, hmTotal);
						hmTotalESICSalary.put(strEmpId+"_"+strSalEffectiveDate, hmTotalESIC);
						hmEmpTotGross.put(strEmpId, ""+dblGrossForPT);
						
						
						/**
						 * code for CTC Variable isdisplay true 
						 * */
						Map<String, String> hmTotalisDisplay = new HashMap<String, String>();
						Iterator<String> it2 = hmInnerisDisplay.keySet().iterator();
						dblGross = 0.0d;
						dblGrossForPT = 0.0d;
						dblGrossTDS = 0.0d;
						dblDeduction = 0.0d;
						while (it2.hasNext()) {
							String strSalaryId = it2.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
							Map<String, String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
							if (hmisDisplay == null)
								hmisDisplay = new HashMap<String, String>();
	
							String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
	
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")) {
	
								if (hmPaidSalaryInner != null) {
									dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossForPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
								} else {
									if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
										
									switch (nSalayHead) {
										/********** OVER TIME *************/
										case OVER_TIME :
	
											double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
											dblGross += dblOverTime;
											dblGrossForPT += dblOverTime;
											dblGrossTDS += dblOverTime;
	
											break;
	
										case BONUS :
											if(hmIndividualBonus != null && !hmIndividualBonus.isEmpty()) {
												double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
												//System.out.println("AP/6205--dblBonusAmount="+dblBonusAmount);
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
												dblGross += dblBonusAmount;
												dblGrossForPT += dblBonusAmount;
												dblGrossTDS += dblBonusAmount;
											}
											break;
	
										case EXGRATIA :
											double dblExGratiaAmount = getExGratiaAmount(con, uF, CF, strEmpId, strD1, strD2, strPC);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
											dblGross += dblExGratiaAmount;
											dblGrossForPT += dblExGratiaAmount;
											dblGrossTDS += dblExGratiaAmount;
	
											break;
	
										case AREARS :
											double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
	//										System.out.println("dblArearAmount 0 ===>> " + dblArearAmount);
											
											dblArearAmount += CF.getIncrementArearCalculation(uF, strEmpId, hmIncrementArearAmountMap);
	//										System.out.println("dblArearAmount 2 ===>> " + dblArearAmount);
											/*if(uF.parseToInt(strEmpId) == 361) {
												System.out.println("AP/6347--Arrears--dblArearAmount==>"+dblArearAmount);
											}*/
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblArearAmount += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get("GROSS"))));
												}	
											}
	//										System.out.println("dblArearAmount 1 ===>> " + dblArearAmount);
											
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
											dblGross += dblArearAmount;
											dblGrossForPT += dblArearAmount;
											dblGrossTDS += dblArearAmount;
	
											break;
	
										case INCENTIVES :
											double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmIncentives, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
											dblGross += dblIncentiveAmount;
											dblGrossForPT += dblIncentiveAmount;
											dblGrossTDS += dblIncentiveAmount;
	
											break;
	
										case REIMBURSEMENT :
											double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
											dblGross += dblReimbursementAmount;
											dblGrossForPT += dblReimbursementAmount;
											break;
	
										case TRAVEL_REIMBURSEMENT :
											double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
											dblGross += dblTravelReimbursementAmount;
											dblGrossForPT += dblTravelReimbursementAmount;
											break;
	
										case MOBILE_REIMBURSEMENT :
											double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
											dblGross += dblMobileReimbursementAmount;
											dblGrossForPT += dblMobileReimbursementAmount;
											break;
	
										case OTHER_REIMBURSEMENT :
											double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
											dblGross += dblOtherReimbursementAmount;
											dblGrossForPT += dblOtherReimbursementAmount;
											break;
	
										case OTHER_EARNING :
											double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
											dblGross += dblOtherEarningAmount;
											dblGrossForPT += dblOtherEarningAmount;
											break;
	
										case SERVICE_TAX :
	
											double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId),
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap,CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblServiceTaxAmount));
	
											/**
											 * @author Vipin 25-Mar-2014 KP
											 *         Condition
											 * @comment = service tax is not
											 *          included while calculating
											 *          TDS
											 * */
	
											dblGross += dblServiceTaxAmount;
											dblGrossForPT += dblServiceTaxAmount;
											dblGrossPT += dblServiceTaxAmount;
											dblGrossTDS += dblServiceTaxAmount;
	
											break;
	
										case SWACHHA_BHARAT_CESS :
											double dblGrossAmt = dblGross;
											double dblServiceTaxAmt = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX + ""));
											dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
											double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
											dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;
	
											double dblSwachhaBharatCess = calculateSwachhaBharatCess(con, uF, strEmpId, dblGrossAmt, hmEmpStateMap.get(strEmpId),
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));
	
											dblGross += dblSwachhaBharatCess;
											dblGrossForPT += dblSwachhaBharatCess;
											dblGrossPT += dblSwachhaBharatCess;
											dblGrossTDS += dblSwachhaBharatCess;
	
											break;
	
										case KRISHI_KALYAN_CESS :
											double dblGrossAmt1 = dblGross;
											double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
											dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
											double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS + ""));
											dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;
	
											double dblKrishiKalyanCess = calculateKrishiKalyanCess(con, uF, strEmpId, dblGrossAmt1, hmEmpStateMap.get(strEmpId),
													hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));
	
											dblGross += dblKrishiKalyanCess;
											dblGrossForPT += dblKrishiKalyanCess;
											dblGrossPT += dblKrishiKalyanCess;
											dblGrossTDS += dblKrishiKalyanCess;
	
											break;
										
										case CGST :
											double dblCGSTAmount = calculateCGST(con, uF, strEmpId, dblGross, hmEmpStateMap.get(strEmpId), hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
	
											dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
											dblGrossForPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
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
											dblGrossForPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
											dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
											dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
	
											break;	
	
										default :
											
											if (uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
												double dblPerkAlignAmount = 0.0d;
												if (hmPerkAlignAmount.containsKey(strSalaryId)) {
													dblPerkAlignAmount = uF.parseToDouble(hmPerkAlignAmount.get(strSalaryId));
												}
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAlignAmount));
												dblGross += dblPerkAlignAmount;
												dblGrossForPT += dblPerkAlignAmount;
												dblGrossPT += dblPerkAlignAmount;
												dblGrossTDS += dblPerkAlignAmount;
											} else if (!uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossForPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											} else if (uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
												if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
													hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
													dblGross += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossForPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossPT += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
													dblGrossTDS += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
												} else {
													hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
													dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
													dblGrossForPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
													dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
													dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												}
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossForPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
												dblGross += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossForPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											} else if (uF.parseToInt(strSalaryId) != GROSS) {
												boolean isMultipePerWithParticularHead = false;
												if(hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
													isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
												}
												
												if(!isMultipePerWithParticularHead) {
													hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))) + "");
													dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
													dblGrossForPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
													dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
													dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												}
												
												/*if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_GRATUITY_PAID_ON_APPROVED_AMOUNT))){
													if (uF.parseToInt(strSalaryId) != GRATUITY) {
														double gratuityAmount = getAppliedGratuityAmount(con,uF,strPC,strEmpId,strSalaryId);
														hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),gratuityAmount) + "");
														dblGross += gratuityAmount;
														dblGrossForPT += gratuityAmount;
														dblGrossPT += gratuityAmount;
														dblGrossTDS += gratuityAmount;
													}
												}*/
											}
											
											/*if(uF.parseToInt(strEmpId)==361){
												System.out.println("AP/6555--strSalaryId=="+strSalaryId+"---amount=="+hmisDisplay.get("AMOUNT"));
											}*/
	
											break;
										}
									} else {
										if (uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
											
										} else if (!uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE")) && hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
											
										} else if (uF.parseToBoolean(hmisDisplay.get("IS_DEFAULT_CAL_ALLOWANCE"))) {
											if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												
											} else {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
												dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossForPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											}
										} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
											
										}  else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
											
										} else if (uF.parseToInt(strSalaryId) != GROSS) {
											boolean isMultipePerWithParticularHead = false;
											if(hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
												isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
											}
											if(!isMultipePerWithParticularHead) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
												dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossForPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											}
										}
										
									}
								}  
	
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
	
								if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
								/**
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */
	
								switch (nSalayHead) {
									/********** EPF EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_EPF :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											Map<String, String> hmVoluntaryPF = hmInnerisDisplay.get(VOLUNTARY_EPF + "");
	
											double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null, null, null, null);
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
												}
											}
											
	//										System.out.println("dblEEPF 0 ===>> " + dblEEPF);
											if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
												List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
												if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
													int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
													List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
													for(String strPaycycle : alArrearPaycycle) {
														Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
														if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
														
														dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+EMPLOYEE_EPF))));
													}
												}	
											}
	//										System.out.println("dblEEPF 1 ===>> " + dblEEPF);
											
											
											dblDeduction += dblEEPF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
										}
	
										break;
	
									/********** EPF EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_EPF :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap,null,null, null, null, null);
											
											dblDeduction += dblERPF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
										}
	
										break;
	
									case LOAN :
										if (hmPaidSalaryInner != null) {
											double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblLoan;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
											CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, strEmpId);
										} else {
											double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans, strD1);
											dblDeduction += dblLoanAmt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
											if (true) {
												// dblGrossTDS = dblGross - dblLoanAmt;
												dblGrossTDS = dblGrossTDS - dblLoanAmt;
											}
										}
	
										break;
	
									case MOBILE_RECOVERY :
										if (hmPaidSalaryInner != null) {
											double dblLoan = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblLoan;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
										} else {
											double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap,
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
											dblDeduction += dblIndividualMobileRecoveryAmt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIndividualMobileRecoveryAmt));
										}
	
										break;
	
									default :
										if (hmPaidSalaryInner != null) {
											dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
										} else {
	
											if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												dblDeduction += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"))));
												dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"));
											} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
												dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											}
										}
										break;
									}
								} else {

									/**
									 * TAX CALCULATION STARTS HERE
									 * 
									 * */

									switch (nSalayHead) {
									/********** EPF EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_EPF :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											Map<String, String> hmVoluntaryPF = hmInnerisDisplay.get(VOLUNTARY_EPF + "");

											double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
											/*if(uF.parseToInt(strEmpId)==370){
												System.out.println("AP/6762---dblEEPF="+dblEEPF);
											}*/
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
												}	
											}
											
											dblDeduction += dblEEPF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
										}

										break;

									/********** EPF EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_EPF :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
										} else {
											double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap,null,null,null,null,null);
											/*if(uF.parseToInt(strEmpId)==370){
												System.out.println("ApP/6786---dblERPF="+dblERPF);
											}*/
											dblDeduction += dblERPF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
										}

										break;


									default :
										if (hmPaidSalaryInner != null) {
											dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											hmTotalisDisplay.put(strSalaryId, hmPaidSalaryInner.get(strSalaryId));
										} else {

											if (hmAllowance.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId))));
												dblDeduction += uF.parseToDouble(hmAllowance.get(strEmpId + "_" + strSalaryId));
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												dblDeduction += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_D")) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"))));
												dblDeduction += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_D"));
											} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
												dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											}
										}
										break;
									}
								
								}
	
							}
	
							hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
							hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
	
						if(!uF.parseToBoolean(strSalCalStatus)) {
							/**
							 * Multiple cal for isDisplay false start
							 * */
		
							Iterator<String> itMultiIsDisplay = hmInnerisDisplay.keySet().iterator();
							while (itMultiIsDisplay.hasNext()) {
								String strSalaryId = itMultiIsDisplay.next();
								int nSalayHead = uF.parseToInt(strSalaryId);
		
								Map<String, String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
								if (hmisDisplay == null) {
									hmisDisplay = new HashMap<String, String>();
								}
		
								String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
								if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")
										&& (hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
									if (hmPaidSalaryInner != null) {
										dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblGrossForPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
									} else {
										double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional, hmContriSalHeadAmt, false);
										if(!hmTotalisDisplay.containsKey(strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
											dblGross += dblMulCalAmt;
											dblGrossForPT += dblMulCalAmt;
											dblGrossTDS += dblMulCalAmt;
										}
									}
								} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")
										&& (hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
									if (hmPaidSalaryInner != null) {
										dblDeduction += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
									} else {
										double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional, hmContriSalHeadAmt, false);
										if(!hmTotalisDisplay.containsKey(strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblMulCalAmt)));
											dblDeduction += dblMulCalAmt;
										}
									}
								}
		
								hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
								hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
							}
		
							/**
							 * Multiple cal isDisplay false start
							 * */
						}
						
						
						/**
						 * Other cal isDisplay start
						 * */
						Iterator<String> itIsDisplayOther = hmInnerisDisplay.keySet().iterator();
						while (itIsDisplayOther.hasNext()) {
							String strSalaryId = itIsDisplayOther.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
							Map<String, String> hmisDisplay = hmInnerisDisplay.get(strSalaryId);
							if (hmisDisplay == null) {
								hmisDisplay = new HashMap<String, String>();
							}
	
							String str_E_OR_D = hmisDisplay.get("EARNING_DEDUCTION");
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
								/**
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */
	
								switch (nSalayHead) {
									/********** TAX *************/
									case PROFESSIONAL_TAX :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											/**
											 * KP Condition
											 * 
											 * */
											// double dblPt =
											// calculateProfessionalTax(con, uF,
											// strD2,dblGrossPT,
											// strFinancialYearEnd, nPayMonth,
											// hmEmpStateMap.get(strEmpId));
											if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
												double dblPt = calculateProfessionalTax(con, uF, strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
												dblDeduction += dblPt;
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
											}
										}
	
										break;
	
									/********** TDS *************/
									case TDS :
										if (hmPaidSalaryInner != null) {
											double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblTDS;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
										} else {
											if(empEffectDtCnt == nEmpwiseEffectDtCnt) {
		//										double dblBasic = uF.parseToDouble(hmTotalisDisplay.get(BASIC + ""));
		//										double dblDA = uF.parseToDouble(hmTotalisDisplay.get(DA + ""));
		//										double dblHRA = uF.parseToDouble(hmTotalisDisplay.get(HRA + ""));
		//
		//										String[] hraSalaryHeads = null;
		//										if (((String) hmHRAExemption.get("SALARY_HEAD_ID")) != null) {
		//											hraSalaryHeads = ((String) hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
		//										}
		//
		//										double dblHraSalHeadsAmount = 0;
		//										for (int j = 0; hraSalaryHeads != null && j < hraSalaryHeads.length; j++) {
		//											dblHraSalHeadsAmount += uF.parseToDouble((String) hmTotalisDisplay.get(hraSalaryHeads[j]));
		//										}
		//
		//										Map<String, String> hmPaidSalaryDetails = hmEmpPaidAmountDetails.get(strEmpId);
		//										if (hmPaidSalaryDetails == null) {
		//											hmPaidSalaryDetails = new HashMap<String, String>();
		//										}
		//
		//										double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_EDU_TAX"));
		//										double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_STD_TAX"));
												double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_FLAT_TDS"));
												String strEmpFlatTDS = hmEmpFlatTDSDeduct.get(strEmpId);
												if(strEmpFlatTDS!=null) {
													dblFlatTDS = uF.parseToDouble(strEmpFlatTDS);	
												}
												if (hmEmpServiceTaxMap.containsKey(strEmpId)) {
													dblGrossTDS = dblGross;
													
		//											double dblServiceTaxAmount = uF.parseToDouble(hmTotalisDisplay.get(SERVICE_TAX + ""));
		//											dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
		//
		//											double dblSwachhaBharatCess = uF.parseToDouble(hmTotalisDisplay.get(SWACHHA_BHARAT_CESS + ""));
		//											dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
		//
		//											double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
		//											dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
													
													double dblCGST = uF.parseToDouble(hmTotalisDisplay.get(CGST + ""));
													dblGrossTDS = dblGrossTDS - dblCGST;
		
													double dblSGST = uF.parseToDouble(hmTotalisDisplay.get(SGST + ""));
													dblGrossTDS = dblGrossTDS - dblSGST;
													
												}
		
		//										/**
		//										 * (dblBasic + dblDA) we use
		//										 * dblHraSalHeadsAmount
		//										 * */
		//										double dblTDS = calculateTDS(con, uF, strD2, strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS,
		//												dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount, nPayMonth, strD1, strFinancialYearStart,
		//												strFinancialYearEnd, strEmpId, hmEmpPay.get("EMP_GENDER"), hmEmpPay.get("EMP_AGE"),
		//												hmEmpStateMap.get(strEmpId), hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap,
		//												hmEmpRentPaidMap, hmPaidSalaryDetails, hmTotalisDisplay, hmSalaryDetails, hmEmpLevelMap, CF,
		//												hmPrevEmpTdsAmount, hmPrevEmpGrossAmount, hmEmpIncomeOtherSourcesMap, hmOtherTaxDetails, hmEmpStateMap);
												double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
														strFinancialYearEnd, strEmpId, hmEmpLevelMap);
												
												/*if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
													List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
													if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
													for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
														int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
														List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
														for(String strPaycycle : alArrearPaycycle) {
															Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
															if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
															
															dblTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+TDS))));
														}
													}	
												}*/
												
												dblDeduction += dblTDS;
		
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
											}
										}
										break;
										
										/********** ESI EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_ESI :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables, hmAnnualVariables, strD1, strD2, strPC, hmTotalESIC);
											dblESI = Math.ceil(dblESI);
											
											dblDeduction += dblESI;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
										}
	
										break;
									/********** /ESI EMPLOYER CONTRIBUTION *************/
	
									/********** ESI EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_ESI :
										if (hmPaidSalaryInner != null) {
											double dblPt = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, hmAnnualVariables, strD1, strD2, strPC, hmTotalESIC);
											
											/*if(uF.parseToInt(strEmpId)==384){
												System.out.println("7018--dblESI 2 ===>> " + dblESI);
											}*/
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblESI += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_ESI))));
												}	
											}
											
											if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
												List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
												if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
													int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
													List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
													for(String strPaycycle : alArrearPaycycle) {
														Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
														if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
														
														dblESI += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+EMPLOYEE_ESI))));
													}
												}	
											}
											
											dblESI = Math.ceil(dblESI);
											
											dblDeduction += dblESI;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
										}
										break;
									/********** /ESI EMPLOYEE CONTRIBUTION *************/
	
									/********** LWF EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_LWF :
										if (hmPaidSalaryInner != null) {
											double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblLWF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										} else {
											double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
											dblDeduction += dblLWF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										}
	
										break;
									/********** /LWF EMPLOYER CONTRIBUTION *************/
	
									/********** LWF EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_LWF :
										if (hmPaidSalaryInner != null) {
											double dblLWF = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
											dblDeduction += dblLWF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										} else {
//											System.out.println("EMPLOYEE_LWF ====>>>> " + EMPLOYEE_LWF);
											double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, hmAnnualVariables, strOrgId);
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()) {
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear) {
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblLWF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_LWF))));
												}	
											}
											
											if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty()) {
												List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
												if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
													int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
													List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
													for(String strPaycycle : alArrearPaycycle) {
														Map<String, String> hmIncrementArrearTotal = hmIncrementArrearCalSalary.get(strEmpId+"_"+nArrearId+"_"+strPaycycle);
														if(hmIncrementArrearTotal == null) hmIncrementArrearTotal = new LinkedHashMap<String, String>();
														
														dblLWF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmIncrementArrearTotal.get(""+EMPLOYEE_LWF))));
													}
												}
											}
											
											dblDeduction += dblLWF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										}
	
										break;
	
									/********** /LWF EMPLOYEE CONTRIBUTION *************/
	
								}
							}
	
							hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
							hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
						/**
						 * Other cal isDisplay end
						 * */
	
						hmTotalSalaryisDisplay.put(strEmpId+"_"+strSalEffectiveDate, hmTotalisDisplay);
						hmEmpTotGrossIsDisplay.put(strEmpId, ""+dblGrossForPT);
						
						// //=========================end code for isdisplay
						// false=======================
	
					}
	
//					System.out.println("hmTotalSalary ===>> " + hmTotalSalary.get("320_01/01/2023"));
					
					request.setAttribute("alEmp", alEmp);
					request.setAttribute("hmEmp", hmEmp);
					request.setAttribute("hmSalaryDetails", hmSalaryDetails);
//					request.setAttribute("hmEmpSalary", hmEmpSalary);
					request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
					request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
					request.setAttribute("hmLoanAmt", hmLoanAmt);
					request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);
					request.setAttribute("hmTotalSalary", hmTotalSalary);
					request.setAttribute("hmTotalESICSalary", hmTotalESICSalary);
					request.setAttribute("hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
	
	//				 System.out.println("hmTotalSalaryisDisplay===>"+hmTotalSalaryisDisplay);
	
					session.setAttribute("AP_alEmp", alEmp);
					session.setAttribute("AP_hmEmp", hmEmp);
					session.setAttribute("AP_hmSalaryDetails", hmSalaryDetails);
//					session.setAttribute("AP_hmEmpSalary", hmEmpSalary);
					session.setAttribute("AP_alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
					session.setAttribute("AP_alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
					session.setAttribute("AP_hmLoanAmt", hmLoanAmt);
					session.setAttribute("AP_hmLoanPoliciesMap", hmLoanPoliciesMap);
					session.setAttribute("AP_hmTotalSalary", hmTotalSalary);
					session.setAttribute("AP_hmTotalESICSalary", hmTotalESICSalary);
					session.setAttribute("AP_hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
	
					session.setAttribute("AP_strD1", strD1);
					session.setAttribute("AP_strD2", strD2);
					session.setAttribute("AP_strPC", strPC);
					session.setAttribute("AP_f_org", getF_org());
					session.setAttribute("AP_strPaycycleDuration", getStrPaycycleDuration());
	
					session.setAttribute("AP_hmEmpStateMap", hmEmpStateMap);
					session.setAttribute("AP_hmEmpLevelMap", hmEmpLevelMap);
					session.setAttribute("AP_hmVariables", hmVariables);
					session.setAttribute("AP_hmAnnualVariables", hmAnnualVariables);
					session.setAttribute("AP_hmOtherTaxDetails", hmOtherTaxDetails);
					session.setAttribute("AP_hmArearAmountMap", hmArearAmountMap);
					
					session.setAttribute("AP_hmEmpArrear", hmEmpArrear); 
					session.setAttribute("AP_hmArrearCalSalary", hmArrearCalSalary); 
					session.setAttribute("AP_hmArrearEarningHead", hmArrearEarningHead); 
					session.setAttribute("AP_hmArrearDeductionHead", hmArrearDeductionHead); 
					session.setAttribute("AP_hmArrearEmployeePF", hmArrearEmployeePF); 
					session.setAttribute("AP_hmArrearEmployerPF", hmArrearEmployerPF); 
					session.setAttribute("AP_hmArrearEmployerESI", hmArrearEmployerESI); 
					session.setAttribute("AP_hmArrearEmployeeLWF", hmArrearEmployeeLWF); 
					
					session.setAttribute("AP_hmIncrementEmpArrear", hmIncrementEmpArrear); 
					session.setAttribute("AP_hmIncrementArrearCalSalary", hmIncrementArrearCalSalary); 
					session.setAttribute("AP_hmIncrementArrearEarningHead", hmIncrementArrearEarningHead); 
					session.setAttribute("AP_hmIncrementArrearDeductionHead", hmIncrementArrearDeductionHead); 
					session.setAttribute("AP_hmIncrementArrearEmployeePF", hmIncrementArrearEmployeePF); 
					session.setAttribute("AP_hmIncrementArrearEmployerPF", hmIncrementArrearEmployerPF); 
					session.setAttribute("AP_hmIncrementArrearEmployerESI", hmIncrementArrearEmployerESI); 
					session.setAttribute("AP_hmIncrementArrearEmployeeLWF", hmIncrementArrearEmployeeLWF);
					session.setAttribute("AP_hmIncrementArrearPaycycle", hmIncrementArrearPaycycle);
					
	//				System.out.println("hmEmpArrear==>"+hmEmpArrear);
	//				System.out.println("hmArrearCalSalary==>"+hmArrearCalSalary);
	//				System.out.println("hmArrearEarningHead==>"+hmArrearEarningHead);
	//				System.out.println("hmArrearDeductionHead==>"+hmArrearDeductionHead);
	//				System.out.println("hmArrearEmployeePF==>"+hmArrearEmployeePF);
	//				System.out.println("hmArrearEmployerPF==>"+hmArrearEmployerPF);
	//				System.out.println("hmArrearEmployerESI==>"+hmArrearEmployerESI);
	//				System.out.println("hmArrearEmployeeLWF==>"+hmArrearEmployeeLWF);
				}
			
				request.setAttribute("strD1", strD1);
				request.setAttribute("strD2", strD2);
	
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		}

	
	
	
	private List<String> getApprovedEmpCount(UtilityFunctions uF, String strD1, String strD2) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<String> alApprovePayStatus = new ArrayList<String>();
		
		try {
			con = db.makeConnection(con);

			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count (*) as emp_ids from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
				+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
				+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
        	if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
        	if(getF_grade()!=null && getF_grade().length>0) {
                sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            }
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
			}
			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}
			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
			}
			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
				+ "and paid_from = ? and paid_to=? group by emp_id)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			String strPendingEmpCount = "0";
			while (rs.next()) {
				strPendingEmpCount = rs.getString("emp_ids");
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select count (*) as emp_ids from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
				+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
				+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
        	if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
        	if(getF_grade()!=null && getF_grade().length>0) {
                sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            }
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "','") + "') ");
			}
			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and eod.depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_service() != null && getF_service().length > 0) {
				sbQuery.append(" and (");
				for (int i = 0; i < getF_service().length; i++) {
					sbQuery.append(" eod.service_id like '%," + getF_service()[i] + ",%'");
					if (i < getF_service().length - 1) {
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" ) ");
			}
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
			}
			if (uF.parseToInt(getF_paymentMode()) > 0) {
				sbQuery.append(" and eod.payment_mode =" + uF.parseToInt(getF_paymentMode()));
			}
			if (getF_strWLocation() != null && getF_strWLocation().length > 0) {
				sbQuery.append(" and eod.wlocation_id in (" + StringUtils.join(getF_strWLocation(), ",") + ") ");
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and eod.wlocation_id in (" + session.getAttribute(WLOCATION_ACCESS) + ")");
			}
			if (uF.parseToInt(getF_org()) > 0) {
				sbQuery.append(" and eod.org_id = " + uF.parseToInt(getF_org()));
			} else if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS) != null) {
				sbQuery.append(" and eod.org_id in (" + session.getAttribute(ORG_ACCESS) + ")");
			}
			sbQuery.append(" and eod.emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
				+ "and paid_from = ? and paid_to=? group by emp_id)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			String strApprovedEmpCount = "0";
			while (rs.next()) {
				strApprovedEmpCount = rs.getString("emp_ids");
			}
			rs.close();
			pst.close();
			
			alApprovePayStatus.add(strApprovedEmpCount);
			alApprovePayStatus.add(strPendingEmpCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return alApprovePayStatus;
	}
	
	
	
	double calculateSGST(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId, Map<String, String> hmTotal,
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

	double calculateCGST(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId, Map<String, String> hmTotal,
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
//			System.out.println("pst ===>> " + pst);
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

	public double calculateTDSA(Connection con, UtilityFunctions uF, String strD1, String strD2, double dblGross, double dblCess1, double dblCess2,
			double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblBasicDA, int nPayMonth, String strPaycycleStart,
			String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge, String strWLocationStateId,
			Map<String, String> hmEmpExemptionsMap, Map<String, String> hmEmpHomeLoanMap, Map<String, String> hmFixedExemptions,
			Map<String, String> hmEmpMertoMap, Map<String, String> hmEmpRentPaidMap, Map<String, String> hmPaidSalaryDetails, Map<String, String> hmTotal,
			Map<String, String> hmSalaryDetails, Map<String, String> hmEmpLevelMap, CommonFunctions CF, int nMonthsLeft,
			Map<String, String> hmEmpIncomeOtherSourcesMap, Map<String, String> hmOtherTaxDetails, Map<String, String> hmEmpStateMap) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblTDSMonth = 0;

		try {

			if (uF.parseToBoolean(hmEmpLevelMap.get(strEmpId + "_FLAT_TDS_DEDEC"))) {
				dblTDSMonth = dblGross * dblFlatTDS / 100;
				// dblTDSMonth += (dblCess1 * 0.01 * dblTDSMonth) + (dblCess2 *
				// 0.01 * dblTDSMonth);
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
				while (rs.next()) {
					dblTDSPaidAmount = uF.parseToDouble(rs.getString("tds"));
				}
				rs.close();
				pst.close();

				// double dblTDSPaidAmount1 = 0;
				int count = 0;

				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ? and salary_head_id not in ("+ REIMBURSEMENT+ ","+ OTHER_REIMBURSEMENT+ ","+ MOBILE_REIMBURSEMENT+ ","+ TRAVEL_REIMBURSEMENT+ ","+ SERVICE_TAX+ ","+ SWACHHA_BHARAT_CESS + "," + KRISHI_KALYAN_CESS + ","+CGST+","+SGST+")");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				double dblGrossPaidAmount = 0;
				while (rs.next()) {
					dblGrossPaidAmount = rs.getDouble("amount");
				}
				rs.close();
				pst.close();

				Map<String, String> hmReimbursementAmt = new HashMap<String, String>();
				if (CF.getIsReceipt()) {
					int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
					Map<String, String> hmOrg = CF.getOrgDetails(con, uF, "" + nEmpOrgId);
					String[] firstArr = CF.getPayCycleFromDate(con, strFinancialYearStart, CF.getStrTimeZone(), CF, "" + nEmpOrgId);
					String[] secondArr = null;
					if (uF.parseToInt(uF.getDateFormat(hmOrg.get("ORG_START_PAYCYCLE"), DATE_FORMAT, "dd")) > 1) {
						secondArr = CF.getPrevPayCycleByOrg(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, "" + nEmpOrgId);
					} else {
						secondArr = CF.getPayCycleFromDate(con, strFinancialYearEnd, CF.getStrTimeZone(), CF, "" + nEmpOrgId);
					}
					pst = con.prepareStatement("select emp_id,sum(reimbursement_amount) as reimbursement_amount "
							+ "from emp_reimbursement where approval_1 =1 and ispaid=true and (ref_document is null or ref_document='' "
							+ "or upper(ref_document) ='NULL') and from_date>=? and to_date<=? and emp_id in (" + strEmpId + ") group by emp_id");
					pst.setDate(1, uF.getDateFormat(firstArr[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(secondArr[1], DATE_FORMAT));
					rs = pst.executeQuery();
					// System.out.println("pst====>"+pst);
					while (rs.next()) {
						hmReimbursementAmt.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("reimbursement_amount")));
					}
					rs.close();
					pst.close();
				}

				dblGrossPaidAmount += uF.parseToDouble(hmReimbursementAmt.get(strEmpId));

				/**
				 * ALL EXEMPTION WILL COME HERE
				 * **/
				double dblInvestment = uF.parseToDouble(hmEmpExemptionsMap.get(strEmpId));
				double dblHomeLoanExemtion = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));

				double dblEEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(EMPLOYEE_EPF + ""));
				double dblVOLEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(VOLUNTARY_EPF + ""));
				double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble(hmTotal.get(EMPLOYEE_EPF + ""));

				double dbl80CC_New = 0;
				double dbl80CC_Old = uF.parseToDouble(hmEmpExemptionsMap.get(strEmpId + "_3"));
				dbl80CC_New = dbl80CC_Old + dblEEEPFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;

				if (dbl80CC_New >= dblDeclaredInvestmentExemption) {
					dbl80CC_New = dblDeclaredInvestmentExemption;
				}

				double dblTotalInvestment = dblInvestment - dbl80CC_Old + dbl80CC_New;
//				double dblHRAExemptions = getHRAExemptionCalculation(con, uF, strD1, hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId,
//						dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap);
				double dblHRAExemptions= CF.getHRAExemptionCalculation(con, uF, nMonthsLeft, hmPaidSalaryDetails, strFinancialYearStart,strFinancialYearEnd, 
						strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap);
				double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions;

				Set<String> set = hmSalaryDetails.keySet();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String strSalaryHeadId = it.next();
					String strSalaryHeadName = hmSalaryDetails.get(strSalaryHeadId);

					if (hmFixedExemptions.containsKey(strSalaryHeadId)) {
						double dblIndividualExemption = uF.parseToDouble(hmFixedExemptions.get(strSalaryHeadId));

						double dblTotalToBePaid = 0;
						if (uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) {
							int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
							double dblCurrentMonthGross = uF.parseToDouble(hmTotal.get("GROSS"));
							dblTotalToBePaid = (nMonthsLeft - 1) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
							dblTotalToBePaid += calculateProfessionalTax(con, uF, strD2, dblCurrentMonthGross, strFinancialYearStart, strFinancialYearEnd, nLastPayMonth, strWLocationStateId, strEmpGender);
						} else {
							dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
						}

						double dblTotalPaid = uF.parseToDouble(hmPaidSalaryDetails.get(strSalaryHeadId));
						double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;
						// double dblExmp = 0;
						if (dblTotalPaidAmount >= dblIndividualExemption) {
							dblExemptions += dblIndividualExemption;
							// dblExmp = dblIndividualExemption;
						} else {
							dblExemptions += dblTotalPaidAmount;
							// dblExmp = dblTotalPaidAmount;
						}
					}
				}

				double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross) + uF.parseToDouble("" + hmEmpIncomeOtherSourcesMap.get(strEmpId));

				double dblTotalTaxableSalary = 0;
				if (dblTotalGrossSalary > dblExemptions) {
					dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
				} else if (dblTotalGrossSalary > 0 && dblExemptions > 0 && dblTotalGrossSalary <= dblExemptions) {
					dblTotalTaxableSalary = 0;
				}

				int countBug = 0;
				double dblTotalTDSPayable = 0.0d;
				double dblUpperDeductionSlabLimit = 0;
				double dblLowerDeductionSlabLimit = 0;
				double dblTotalNetTaxableSalary = 0;

				do {
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
					// System.out.println("pst=====>"+pst);
					double dblDeductionAmount = 0;
					if (rs.next()) {
						dblDeductionAmount = rs.getDouble("deduction_amount");
						dblUpperDeductionSlabLimit = rs.getDouble("_to");
						dblLowerDeductionSlabLimit = rs.getDouble("_from");
					}
					rs.close();
					pst.close();

					if (countBug == 0) {
						dblTotalNetTaxableSalary = dblTotalTaxableSalary;
					}

					if (dblTotalTaxableSalary >= dblUpperDeductionSlabLimit) {
						dblTotalTDSPayable += ((dblDeductionAmount / 100) * (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit));
					} else {
						if (countBug == 0) {
							dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
						}
						dblTotalTDSPayable += ((dblDeductionAmount / 100) * dblTotalNetTaxableSalary);
					}

					dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;

					if (countBug == 15)
						break; // in case of any bug, this condition is used to
								// avoid any stoppage
					countBug++;

				} while (dblTotalNetTaxableSalary > 0);

				// Service tax + Education cess

				/**
				 * @autor Vipin Date: 25-Mar-2014 87A Section for AY 2014-2015
				 * */
				double dblMaxTaxableIncome = uF.parseToDouble(hmOtherTaxDetails.get((String) hmEmpStateMap.get(strEmpId) + "_MAX_TAX_INCOME"));
				double dblRebateAmt = uF.parseToDouble(hmOtherTaxDetails.get((String) hmEmpStateMap.get(strEmpId) + "_REBATE_AMOUNT"));
				double dblRebate = 0;
				if (dblTotalTaxableSalary <= dblMaxTaxableIncome && dblTotalTDSPayable <= dblMaxTaxableIncome) {
					if (dblTotalTDSPayable >= dblRebateAmt) {
						dblRebate = dblRebateAmt;
					} else if (dblTotalTDSPayable > 0 && dblTotalTDSPayable < dblRebateAmt) {
						dblRebate = dblTotalTDSPayable;
					}
				}

				dblTotalTDSPayable = dblTotalTDSPayable - dblRebate;

				double dblCess = dblTotalTDSPayable * (dblCess1 / 100);
				dblCess += dblTotalTDSPayable * (dblCess2 / 100);

				dblTotalTDSPayable += dblCess;

				dblTDSMonth = dblTotalTDSPayable - dblTDSPaidAmount;
				if ((nMonthsLeft - count) > 0) {
					dblTDSMonth = dblTDSMonth / (nMonthsLeft - count);
					// } else {
					// dblTDSMonth = dblTDSMonth;
				}
				if (dblTDSMonth < 0) {
					dblTDSMonth = 0;
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

	public void calculateETDS(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap,
			Map<String, String> hmVariables, boolean isInsert, Map<String, String> hmOtherTaxDetails, Map<String, String> hmEmpLevelMap,Map<String, String> hmAnnualVariables) {
		PreparedStatement pst = null;
		Database db = null;
		try {
			if (con == null) {
				db = new Database();
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}

			double dblEduCess = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_EDU_TAX"));
			double dblSTDCess = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_STD_TAX"));
			// double dblFlatTDS =
			// uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));

			double dblTDSMonth = 0;
			double dblActual = 0;
			double dblEDuTax = 0;
			double dblSTDTax = 0;
			double dblflatTds = 0;

			if (uF.parseToBoolean(hmEmpLevelMap.get(strEmpId + "_FLAT_TDS_DEDEC"))) {
				// dblTDSMonth = dblGross * dblFlatTDS / 100;
				dblActual = uF.parseToDouble(hmTotal.get(TDS + ""));
				dblTDSMonth = dblActual;
				dblflatTds = dblActual;
			} else {
				dblActual = uF.parseToDouble(hmTotal.get(TDS + ""));

				dblTDSMonth = dblActual / (1 + (dblEduCess / 100) + (dblSTDCess / 100));

				dblEDuTax = dblTDSMonth * (dblEduCess / 100);
				dblSTDTax = dblTDSMonth * (dblSTDCess / 100);
				// System.out.println("dblEduCess====>"+(dblEduCess/100)+"------dblSTDCess---------"+(dblSTDCess/100));
				/*
				 * System.out.println("dblEDuTax====>"+dblEDuTax+
				 * "------dblSTDTax---------"
				 * +dblSTDTax+"\\\\\\dblTDSMonth"+dblTDSMonth);
				 * System.out.println
				 * ("dblTDSMonth====>"+dblTDSMonth+"------dblActual---------"
				 * +dblActual);
				 */
			}

			if (isInsert) {
				pst = con.prepareStatement("insert into emp_tds_details (financial_year_start, financial_year_end, tds_amount,"
						+ " edu_tax_amount, std_tax_amount, user_id, entry_timestamp, emp_id, paycycle, _month,flat_tds_amount,actual_tds_amount) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDouble(3, uF.parseToDouble(uF.formatIntoTwoDecimal(dblTDSMonth)));
				pst.setDouble(4, uF.parseToDouble(uF.formatIntoTwoDecimal(dblEDuTax)));
				pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(dblSTDTax)));
				pst.setInt(6, uF.parseToInt((String) session.getAttribute(EMPID)));
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
		} finally {
			if (db != null) {
				db.closeConnection(con);
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

	public void setCF(CommonFunctions CF) {
		this.CF = CF;
	}

	public double getHRAExemptionCalculation(Connection con, UtilityFunctions uF, String strD1, Map<String, String> hmPaidSalaryDetails,
			String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, double dblHRA, double dblBasicDA, Map<String, String> hmEmpMertoMap,
			Map<String, String> hmEmpRentPaidMap) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblHRAExemption = 0;

		try {
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from = ? and financial_year_to =? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			double dblCondition1 = 0;
			double dblCondition2 = 0;
			double dblCondition3 = 0;
			String strHraSalHeads = null;
			while (rs.next()) {
				dblCondition1 = rs.getDouble("condition1");
				dblCondition2 = rs.getDouble("condition2");
				dblCondition3 = rs.getDouble("condition3");
				strHraSalHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String[] hraSalaryHeads = null;
			if (strHraSalHeads != null) {
				hraSalaryHeads = strHraSalHeads.split(",");
			}

			double dblHraSalHeadsAmount = 0;
			for (int i = 0; hraSalaryHeads != null && i < hraSalaryHeads.length; i++) {
				dblHraSalHeadsAmount += uF.parseToDouble((String) hmPaidSalaryDetails.get(hraSalaryHeads[i]));
			}

			boolean isMetro = uF.parseToBoolean(hmEmpMertoMap.get(strEmpId));
			String strHRAPaidAmount = hmPaidSalaryDetails.get(HRA + "");

			String strMonthsLeft = uF.dateDifference(strD1, DATE_FORMAT, strFinancialYearEnd, DATE_FORMAT,CF.getStrTimeZone());
			int nMonthsLeft = uF.parseToInt(strMonthsLeft) / 30;

			double dblBasicToBePaidAmount = nMonthsLeft * dblBasicDA;
			double dblHRAToBePaidAmount = nMonthsLeft * dblHRA;

			double dblTotalBasicDAAmount = dblHraSalHeadsAmount + dblBasicToBePaidAmount;
			double dblTotalHRAAmount = uF.parseToDouble(strHRAPaidAmount) + dblHRAToBePaidAmount;

			double dblTotalRentPaid = uF.parseToDouble(hmEmpRentPaidMap.get(strEmpId));
			double dblRentPaidGreaterThanCondition1 = 0;

			if (dblTotalRentPaid > dblRentPaidGreaterThanCondition1) {
				dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount / 100;
				dblRentPaidGreaterThanCondition1 = dblTotalRentPaid - dblRentPaidGreaterThanCondition1;
			} else if (dblTotalRentPaid > 0) {
				dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount / 100;
			}

			double dblRentPaidCondition23 = 0;
			if (isMetro) {
				dblRentPaidCondition23 = dblCondition2 * dblTotalBasicDAAmount / 100;
			} else {
				dblRentPaidCondition23 = dblCondition3 * dblTotalBasicDAAmount / 100;
			}

			dblHRAExemption = Math.min(dblTotalHRAAmount, dblRentPaidGreaterThanCondition1);
			dblHRAExemption = Math.min(dblHRAExemption, dblRentPaidCondition23);
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
		return dblHRAExemption;
	}

	public double calculateProfessionalTax(Connection con, UtilityFunctions uF, String strD2, double dblGross, String strFinancialYearStart, 
			String strFinancialYearEnd, int nPayMonth, String strWLocationStateId, String strEmpGender) {
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
			rs = pst.executeQuery();
			double dblDeductionAmount = 0;
			double dblDeductionPaycycleAmount = 0;
			while (rs.next()) {
				dblDeductionAmount = rs.getDouble("deduction_amount");
				dblDeductionPaycycleAmount = rs.getDouble("deduction_paycycle");
			}
			rs.close();
			pst.close();

			nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));

			int nFinancialYearEndMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
			nFinancialYearEndMonth = nFinancialYearEndMonth - 1;

			if (nFinancialYearEndMonth == nPayMonth) {
				dblDeductionPayMonth = dblDeductionAmount - (11 * dblDeductionPaycycleAmount);
			} else {
				dblDeductionPayMonth = dblDeductionPaycycleAmount;
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
		return dblDeductionPayMonth;
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
					} else if(nHeadId > 0 && nHeadId == VDA) {
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
			double dblReimbursementCTCOptional, Map<String, String> hmContriSalHeadAmt, boolean deductContriFlag) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblMulCalAmt = 0.0d;
		try {
			
			if(hmAnnualVarPolicyAmount == null) hmAnnualVarPolicyAmount = new HashMap<String, String>();
			if(hmAllowance == null) hmAllowance = new HashMap<String, String>();
			if(hmVariables== null) hmVariables = new HashMap<String, String>();
			
			String strMulCal = hm.get("MULTIPLE_CALCULATION");
//			System.out.println("strMulCal ===>> " + strMulCal);
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
		
					pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) from emp_salary_details " +
						"where emp_id =? and effective_date <= ? and is_approved=true and level_id = ?) and salary_head_id in (" + CTC + ") and salary_head_id in " +
						"(select salary_head_id from salary_details where (is_delete is null or is_delete=false) and org_id in (select org_id from employee_personal_details epd, " +
						"employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.emp_id=?) and level_id = ?) and level_id = ?");
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
									if(deductContriFlag) {
										dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_EPF));
									} else {
										dblAmt = 0;
									}
								}
								if (uF.parseToInt(str.trim()) == EMPLOYER_ESI && !hmTotal.containsKey(""+EMPLOYER_ESI)) {
									if(deductContriFlag) {
										dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_ESI));
									} else {
										dblAmt = 0;
									}
								}
								if (uF.parseToInt(str.trim()) == EMPLOYER_LWF && !hmTotal.containsKey(""+EMPLOYER_LWF)) {
									if(deductContriFlag) {
										dblAmt = uF.parseToDouble(hmContriSalHeadAmt.get(""+EMPLOYER_LWF));
									} else {
										dblAmt = 0;
									}
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
					
//					System.out.println("sbFormula ===>> " + sbFormula.toString());
					String strPercentage = hm.get("SALARY_PERCENTAGE");
//					System.out.println("strPercentage ===>> " + strPercentage);
					
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

	public double getMobileRecoveryCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmMobileRecovery,
			CommonFunctions CF) {
		double dblMobileRecoveryAmount = 0;
		try {
			dblMobileRecoveryAmount = uF.parseToDouble(hmMobileRecovery.get(strEmpId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblMobileRecoveryAmount;
	}

	public double getIndividualOtherDeductionCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth,
			Map<String, String> hmIndividualOtherDeduction, CommonFunctions CF) {
		double dblIndividualOtherDeductionAmount = 0;

		try {
			dblIndividualOtherDeductionAmount = uF.parseToDouble(hmIndividualOtherDeduction.get(strEmpId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblIndividualOtherDeductionAmount;
	}

	public double calculateLOAN(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strEmpId, CommonFunctions CF, Map<String, String> hmLoanAmt, Map<String, Map<String, String>> hmEmpLoan,
			List<String> alLoans, String strD1) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		double dblTotalCalculatedAmount = 0;

		try {

			pst = con.prepareStatement(selectLoanPayroll2);
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblPrincipalAmt = 0;
			double dblBalAmt = 0;
			double dblROI = 0;
			double dblDuration = 0;
			String strApprovedDate = null;
			Map<String, String> hmEmpLoanInner = new HashMap<String, String>();
			while (rs.next()) {
				dblPrincipalAmt = rs.getDouble("amount_paid");
				dblBalAmt = rs.getDouble("balance_amount");
				dblROI = rs.getDouble("loan_interest");
				dblDuration = rs.getDouble("duration_months");

				strApprovedDate = rs.getString("approved_date");
				if (strApprovedDate != null) {
					Calendar calCurrent = GregorianCalendar.getInstance();
					calCurrent.setTime(uF.getCurrentDate(CF.getStrTimeZone()));

					Calendar calApproved = GregorianCalendar.getInstance();
					calApproved.setTime(uF.getDateFormat(strApprovedDate, DBDATE));

					calApproved.add(Calendar.MONTH, (int) dblDuration);

					String strLastDate = calApproved.get(Calendar.DATE) + "/" + (calApproved.get(Calendar.MONTH) + 1) + "/" + calApproved.get(Calendar.YEAR);
					int nBalanceMonths = uF.parseToInt(uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, strLastDate, DATE_FORMAT,CF.getStrTimeZone()));
					nBalanceMonths = (int) nBalanceMonths / 30;

					dblCalculatedAmount = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
					dblCalculatedAmount = dblCalculatedAmount / dblDuration;

					if (dblCalculatedAmount >= dblBalAmt) {
						dblCalculatedAmount = dblBalAmt;
					}
					if (dblCalculatedAmount > dblGross) {
						dblCalculatedAmount = dblGross;
					}
					dblTotalCalculatedAmount += dblCalculatedAmount;
					hmLoanAmt.put(rs.getString("loan_applied_id"), uF.formatIntoTwoDecimal(dblCalculatedAmount));

//					String strEffectDate = "";
//					if(rs.getString("effective_date") != null) {
//						strEffectDate = uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT);
//					}
					hmEmpLoanInner = hmEmpLoan.get(rs.getString("emp_id")+"_"+strD1);
					if (hmEmpLoanInner == null)
						hmEmpLoanInner = new HashMap<String, String>();
					hmEmpLoanInner.put(rs.getString("loan_id"), uF.formatIntoTwoDecimal(dblTotalCalculatedAmount));
					hmEmpLoan.put(rs.getString("emp_id")+"_"+ strD1, hmEmpLoanInner);

					if (!alLoans.contains(rs.getString("loan_id"))) {
						alLoans.add(rs.getString("loan_id"));
					}
				}
			}
			rs.close();
			pst.close();
//			System.out.println("dblTotalCalculatedAmount ===>> " + dblTotalCalculatedAmount);
//			System.out.println("hmEmpLoan ===>> " + hmEmpLoan);
			
			request.setAttribute("hmEmpLoan", hmEmpLoan);
			request.setAttribute("alLoans", alLoans);

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
		return dblTotalCalculatedAmount;
	}

	public double calculateEELWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strWLocationStateId, Map<String, String> hmVariables, String strEmpId, int nPayMonth, 
			Map<String, String> hmAnnualVariables, String strOrgId) {
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
			String strSalaryHeads = null;
			String strMonths = null;
			while (rs.next()) {
				strSalaryHeads = rs.getString("salary_head_id");
				strMonths = rs.getString("months");
			}
			rs.close();
			pst.close();

//			System.out.println("strSalaryHeads ===>> " + strSalaryHeads);
//			System.out.println("strMonths ===>> " + strMonths);
			
			String[] arrMonths = null;
			if (strMonths != null) {
				arrMonths = strMonths.split(",");
			}

			if (ArrayUtils.contains(arrMonths, nPayMonth + "") >= 0) {
				String[] arrSalaryHeads = null;
				if (strSalaryHeads != null) {
					arrSalaryHeads = strSalaryHeads.split(",");
				}

				double dblAmount = 0;
				for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
					dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
				}

				pst = con.prepareStatement(selectERLWFC);
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strWLocationStateId));
				pst.setDouble(4, dblAmount);
				pst.setDouble(5, dblAmount);
				pst.setInt(6, uF.parseToInt(strOrgId));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					dblCalculatedAmount = uF.parseToDouble(rs.getString("eelfw_contribution"));
				}
				rs.close();
				pst.close();
//				System.out.println("dblCalculatedAmount ===>> " + dblCalculatedAmount);
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
		return dblCalculatedAmount;
	}

	public double calculateERLWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strWLocationStateId, int nPayMonth, String strOrgId) {
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
			String strSalaryHeads = null;
			String strMonths = null;
			while (rs.next()) {
				strSalaryHeads = rs.getString("salary_head_id");
				strMonths = rs.getString("months");
			}
			rs.close();
			pst.close();

			String[] arrMonths = null;
			if (strMonths != null) {
				arrMonths = strMonths.split(",");
			}

			if (ArrayUtils.contains(arrMonths, nPayMonth + "") >= 0) {
				String[] arrSalaryHeads = null;
				if (strSalaryHeads != null) {
					arrSalaryHeads = strSalaryHeads.split(",");
				}

				double dblAmount = 0;
				for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
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
				while (rs.next()) {
					dblCalculatedAmount = uF.parseToDouble(rs.getString("erlfw_contribution"));
				}
				rs.close();
				pst.close();
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
		return dblCalculatedAmount;
	}

	public void calculateELWF(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map<String, String> hmVariables, 
			boolean isInsert,Map<String, String> hmAnnualVariables, Map<String, List<Map<String, String>>> hmEmpArrear,
			Map<String, Map<String, String>> hmArrearEmployeeLWF, Map<String, List<Map<String, String>>> hmIncrementEmpArrear,
			Map<String, Map<String, String>> hmIncrementArrearEmployeeLWF,Map<String, List<String>> hmIncrementArrearPaycycle, String strOrgId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmountEELWF = 0;
		double dblCalculatedAmountERLWF = 0;

		Database db = null;
		try {
			if (con == null) {
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
			// System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			String strSalaryHeads = null;
			while (rs.next()) {
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String[] arrSalaryHeads = null;
			if (strSalaryHeads != null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}

			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				if (uF.parseToInt(arrSalaryHeads[i]) != OVER_TIME && hmVariables != null && !hmVariables.containsKey(strEmpId + "_" + arrSalaryHeads[i] + "_E") 
						&& hmAnnualVariables != null && !hmAnnualVariables.containsKey(strEmpId + "_" + arrSalaryHeads[i])) {
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}

			pst = con.prepareStatement("select * from lwf_details where financial_year_start= ? and financial_year_end = ? and state_id=? "
					+ " and ? between min_limit and max_limit and org_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
			pst.setDouble(4, dblAmount);
			pst.setInt(5, uF.parseToInt(hmEmpStateMap.get(strOrgId)));
			// System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			double dblEELWFAmount = 0;
			double dblERLWFAmount = 0;
			double dblMaxAmount = 0;
			String lwfMonth = null;
			while (rs.next()) {
				dblEELWFAmount = rs.getDouble("eelfw_contribution");
				dblERLWFAmount = rs.getDouble("erlfw_contribution");
				dblMaxAmount = rs.getDouble("max_limit");
				lwfMonth = rs.getString("months");
			}
			rs.close();
			pst.close();

			if (dblAmountEligibility >= dblMaxAmount) {
				return;
			}

			List<String> lwfMonthList = null;
			if (lwfMonth != null) {
				lwfMonthList = Arrays.asList(lwfMonth.split(","));
			}

			int month = uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM"));
			if (lwfMonthList == null || !lwfMonthList.contains("" + month)) {
				return;
			}

			dblCalculatedAmountEELWF = dblEELWFAmount;
			dblCalculatedAmountERLWF = dblERLWFAmount;

			if (isInsert) {
				
				if(hmEmpArrear !=null && !hmEmpArrear.isEmpty() && hmArrearEmployeeLWF !=null && !hmArrearEmployeeLWF.isEmpty()) {
					List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
					if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
					for(Map<String,String> hmApplyArear : alArrear) {
						int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
						Map<String, String> hmEmployeeLWF = hmArrearEmployeeLWF.get(strEmpId+"_"+nArrearPaycycle);
						if(hmEmployeeLWF !=null && !hmEmployeeLWF.isEmpty()) {
							dblAmount += uF.parseToDouble(hmEmployeeLWF.get("LWF_MAX_LIMIT"));
							dblCalculatedAmountEELWF += uF.parseToDouble(hmEmployeeLWF.get("LWF_EELWF_CONTRIBUTION"));
							dblCalculatedAmountERLWF += uF.parseToDouble(hmEmployeeLWF.get("LWF_ERLWF_CONTRIBUTION"));
						}
					}
				}
				
				if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty() && hmIncrementArrearEmployeeLWF !=null && !hmIncrementArrearEmployeeLWF.isEmpty()) {
					List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
					if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
					for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
						int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
						List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
						for(String strArrearPaycycle : alArrearPaycycle) {
							Map<String, String> hmIncrementEmployeeLWF = hmIncrementArrearEmployeeLWF.get(strEmpId+"_"+nArrearId+"_"+strArrearPaycycle);
							if(hmIncrementEmployeeLWF !=null && !hmIncrementEmployeeLWF.isEmpty()) {
								dblAmount += uF.parseToDouble(hmIncrementEmployeeLWF.get("LWF_MAX_LIMIT"));
								dblCalculatedAmountEELWF += uF.parseToDouble(hmIncrementEmployeeLWF.get("LWF_EELWF_CONTRIBUTION"));
								dblCalculatedAmountERLWF += uF.parseToDouble(hmIncrementEmployeeLWF.get("LWF_ERLWF_CONTRIBUTION"));
							}
						}
					}
				}
				
				pst = con.prepareStatement("insert into emp_lwf_details (financial_year_start, financial_year_end, salary_head_id, "
						+ "lwf_max_limit, eelwf_contribution, erlwf_contribution, user_id, entry_timestamp, emp_id, paycycle, _month) "
						+ "values (?,?,?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
				pst.setDouble(4, dblAmount);
				pst.setDouble(5, dblCalculatedAmountEELWF);
				pst.setDouble(6, dblCalculatedAmountERLWF);
				pst.setInt(7, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setTimestamp(8, null);
				pst.setInt(9, uF.parseToInt(strEmpId));
				pst.setInt(10, uF.parseToInt(strPaycycle));
				pst.setInt(11, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
				pst.execute();
				pst.close();
				// System.out.println("pst====>"+pst);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.closeConnection(con);
			}

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
	
	
	public double calculateEEESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strWLocationStateId, Map<String, String> hmVariables, String strEmpId, 
			Map<String, String> hmAnnualVariables, String strD1, String strD2, String strPC, Map<String, String> hmTotalESIC) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0.0d;
		try {
			String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(strEmpId));
			String strLevelId = CF.getEmpLevelId(con, strEmpId);
			
			pst = con.prepareStatement("select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id=? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setInt(4, uF.parseToInt(strEmpId));
			pst.setInt(5, uF.parseToInt(strLevelId));
			if(uF.parseToInt(strEmpId) == 461) {
//				System.out.println("pst==>"+pst);
			}
			rs = pst.executeQuery();
			double dblEEESIAmount = 0;
			double dblESIMaxAmount = 0;
			String strSalaryHeads = null;
			String strEligibleSalaryHeads = null;
			while (rs.next()) {
				dblEEESIAmount = rs.getDouble("eesi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strEligibleSalaryHeads = rs.getString("eligible_salary_head_ids");
			}
			rs.close();
			pst.close();

			String[] arrSalaryHeads = null;
			if (strSalaryHeads != null) {
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
			/*
			if(uF.parseToInt(strEmpId) == 116) {
				System.out.println("AP/8630---hmTotalESIC==>"+hmTotalESIC);
			}*/
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				if (alEligibleSalaryHeads.contains(arrSalaryHeads[i].trim())) {
					
			//===start parvez date: 01-208-2022===		
//					dblAmountEligibility += uF.parseToDouble(hmTotalESIC.get(arrSalaryHeads[i].trim()));
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
			//===end parvez date: 01-08-2022===		
					
				}
				
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim())); 
			}
			/*if(uF.parseToInt(strEmpId) == 206) {
				System.out.println("AP/8713---arrSalaryHeads[i].trim()==>"+arrSalaryHeads+"--hmtotal==>"+hmTotal);
			}*/
			
			int nMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			boolean attemptFlag = false;
			boolean deductFlag = false;
			double dblGrossAmt = 0;
			
			
			if(nMonth != ESI_PERIOD_1_START && nMonth >= ESI_PERIOD_1_START && nMonth <= ESI_PERIOD_1_END) {
//				System.out.println("ApPay/8664--nMonth ===>> " + nMonth+"---ESI_PERIOD_1_START=="+ESI_PERIOD_1_START+"--ESI_PERIOD_1_END=="+ESI_PERIOD_1_END);
//				String strPeriod1Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_1_START)+"/"+uF.getDateFormat(strD2, DATE_FORMAT, "yyyy");
				String strPeriod1Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_1_START)+"/"+uF.getDateFormat(strD2, DATE_FORMAT, "yyyy");
				int days = uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd"));
				if(days > 15) {
					strPeriod1Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_1_START-1)+"/"+uF.getDateFormat(strD2, DATE_FORMAT, "yyyy");
				}
				if(strEmpJoiningDate != null && !strEmpJoiningDate.trim().equals("") && !strEmpJoiningDate.trim().equalsIgnoreCase("NULL") && !strEmpJoiningDate.trim().equals("-")) {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					Date date1 = sdf.parse(strPeriod1Date);
			        Date date2 = sdf.parse(strEmpJoiningDate);
			        if(date2.after(date1)) {
			        	strPeriod1Date = strEmpJoiningDate;
		            }
				}
//				System.out.println("strPeriod1Date ===>> " + strPeriod1Date);
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strPeriod1Date, CF.getStrTimeZone(), CF, strEmpOrgId);
				if(strPayCycleDate != null) {
//					System.out.println("strPayCycleDate[0]==>"+strPayCycleDate[0]+"--strPayCycleDate[1]==>"+strPayCycleDate[1]+"--strPayCycleDate[2]==>"+strPayCycleDate[2]);
					pst = con.prepareStatement("select * from emp_esi_details where eesi_contribution > 0 and emp_id=? and paycycle=? and _month=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strPayCycleDate[2]));
					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT, "MM")));
					
					rs = pst.executeQuery();
					if(rs.next()) {
						deductFlag = true;
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paid_from=? and paid_to=? and paycycle=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strPayCycleDate[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPayCycleDate[2]));
					if(uF.parseToInt(strEmpId) == 6360) {
//						System.out.println("pst ===>> " + pst);
					}
					rs = pst.executeQuery();
					if(rs.next()) {
						if(rs.getString("earning_deduction") !=null && rs.getString("earning_deduction").equals("E")) {
							dblGrossAmt += rs.getDouble("amount");
						}
						attemptFlag = true;
					}
					rs.close();
					pst.close();					
				}
			} else if(nMonth != ESI_PERIOD_2_START && (nMonth >= ESI_PERIOD_2_START || nMonth <= ESI_PERIOD_2_END)) {
				/*if(uF.parseToInt(strEmpId) == 400) {
					System.out.println("nMonth ===>> " + nMonth + "---ESI_PERIOD_2_START="+ESI_PERIOD_2_START);
				}*/
				int year = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy"));
				if(nMonth<4) {
					year= year-1;
				}
				String strPeriod2Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_2_START)+"/"+year;
				int days = uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd"));
				if(days > 15) {
					strPeriod2Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_2_START-1)+"/"+year;
				}
//				String strPeriod2Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_2_START)+"/"+uF.getDateFormat(strD2, DATE_FORMAT, "yyyy");
				if(strEmpJoiningDate != null && !strEmpJoiningDate.trim().equals("") && !strEmpJoiningDate.trim().equalsIgnoreCase("NULL") && !strEmpJoiningDate.trim().equals("-")) {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					Date date1 = sdf.parse(strPeriod2Date);
			        Date date2 = sdf.parse(strEmpJoiningDate);
			        if(date2.after(date1)) {
			        	strPeriod2Date = strEmpJoiningDate;
		            }
				}
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strPeriod2Date, CF.getStrTimeZone(), CF, strEmpOrgId);
				if(strPayCycleDate != null) {
//					System.out.println("strPayCycleDate[0]==>"+strPayCycleDate[0]+"--strPayCycleDate[1]==>"+strPayCycleDate[1]+"--strPayCycleDate[2]==>"+strPayCycleDate[2]);
					pst = con.prepareStatement("select * from emp_esi_details where eesi_contribution > 0 and emp_id=? and paycycle=? and _month=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strPayCycleDate[2]));
					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT, "MM")));
					
					rs = pst.executeQuery();
					if(rs.next()) {
						deductFlag = true;
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paid_from=? and paid_to=? and paycycle=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strPayCycleDate[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPayCycleDate[2]));
					
					rs = pst.executeQuery();
					if(rs.next()) {
						if(rs.getString("earning_deduction") !=null && rs.getString("earning_deduction").equals("E")) {
							dblGrossAmt += rs.getDouble("amount");
						}
						attemptFlag = true;
					}
					rs.close();
					pst.close();					
				}
			}
			/*if(uF.parseToInt(strEmpId) == 509) {
				System.out.println("ApPay/8766---attemptFlag==>"+attemptFlag+" --deductFlag==>"+deductFlag + " -- dblGrossAmt ==> " + dblGrossAmt);
			}*/
			if(attemptFlag && deductFlag) { 
				dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
//				if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
//					dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
//				}
			} else if(attemptFlag && !deductFlag && dblGrossAmt==0) {
				if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
					dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
				}
			} else if(attemptFlag && !deductFlag) {
				dblCalculatedAmount = 0;
			} else {
				boolean noDeductFlag = true;
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strD2, CF.getStrTimeZone(), CF, strEmpOrgId);
				pst = con.prepareStatement("select * from emp_esi_details where emp_id=? and _month<? and paycycle<?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, nMonth);
				pst.setInt(3, uF.parseToInt(strPayCycleDate[2]));
				
				rs = pst.executeQuery();
				if(rs.next()) {
					noDeductFlag = false;
				}
				rs.close();
				pst.close();	
				
				if(noDeductFlag || nMonth == ESI_PERIOD_1_START || nMonth == ESI_PERIOD_2_START) {
					/*if(uF.parseToInt(strEmpId)==384 ){
						System.out.println("AP/8855---dblAmountEligibility="+dblAmountEligibility+"---dblESIMaxAmount="+dblESIMaxAmount);
						System.out.println("AP/8856---dblEEESIAmount="+dblEEESIAmount+"---dblAmount="+dblAmount);
						System.out.println("AP/8857---dblCalculatedAmount="+dblCalculatedAmount);
					}*/
					if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
						dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
					}
				}
			}
			
			/*if(uF.parseToInt(strEmpId)==384 ){
				System.out.println("AP/8804---dblAmountEligibility="+dblAmountEligibility+"---dblESIMaxAmount="+dblESIMaxAmount);
				System.out.println("AP/8805---dblEEESIAmount="+dblEEESIAmount+"---dblAmount="+dblAmount);
				System.out.println("AP/8866---dblCalculatedAmount="+dblCalculatedAmount);
			}*/
			
	//===start parvez date: 01-08-2022===		
			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
				dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
				if(uF.parseToInt(strEmpId)==384 ){
					System.out.println("AP/8873---dblCalculatedAmount="+dblCalculatedAmount);
					System.out.println("AP/8874---dblAmountEligibility="+dblAmountEligibility+"---dblESIMaxAmount=="+dblESIMaxAmount);
					
				}
			}
//			dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
	//===end parvez date: 01-08-2022===		
			
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
		return dblCalculatedAmount;
	}

	
	public double calculateERESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strWLocationStateId, String strEmpId, Map<String, String> hmVariables, Map<String, String> hmAnnualVariables, 
			String strD1, String strD2, String strPC, Map<String, String> hmTotalESIC) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0.0d;
		try {
			
			String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(strEmpId));
			String strLevelId = CF.getEmpLevelId(con, strEmpId);
			
			pst = con.prepareStatement("select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id =? " +
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
			while (rs.next()) {
				dblERESIAmount = rs.getDouble("ersi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strEligibleSalaryHeads = rs.getString("eligible_salary_head_ids");
			}
			rs.close();
			pst.close();

			String[] arrSalaryHeads = null;
			if (strSalaryHeads != null) {
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
					dblAmountEligibility += uF.parseToDouble(hmTotalESIC.get(arrSalaryHeads[i].trim()));
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
			}
//			System.out.println("hmTotal==>"+hmTotal);
//			System.out.println("dblAmount==>"+dblAmount);
//			System.out.println("dblAmountEligibility==>"+dblAmountEligibility);
//			System.out.println("dblESIMaxAmount==>"+dblESIMaxAmount);
//			System.out.println("dblERESIAmount==>"+dblERESIAmount);
//			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
//				dblCalculatedAmount = ((dblERESIAmount * dblAmount) / 100);
//			}
			
			int nMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			boolean attemptFlag = false;
			boolean deductFlag = false;
			
			if(nMonth != ESI_PERIOD_1_START && nMonth >= ESI_PERIOD_1_START && nMonth <= ESI_PERIOD_1_END) {
				String strPeriod1Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_1_START)+"/"+uF.getDateFormat(strD2, DATE_FORMAT, "yyyy");
				if(strEmpJoiningDate != null && !strEmpJoiningDate.trim().equals("") && !strEmpJoiningDate.trim().equalsIgnoreCase("NULL") && !strEmpJoiningDate.trim().equals("-")) {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					Date date1 = sdf.parse(strPeriod1Date);
			        Date date2 = sdf.parse(strEmpJoiningDate);
			        if(date2.after(date1)) {
			        	strPeriod1Date = strEmpJoiningDate;
		            }
				}
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strPeriod1Date, CF.getStrTimeZone(), CF, strEmpOrgId);
				if(strPayCycleDate != null) {
//					System.out.println("strPayCycleDate[0]==>"+strPayCycleDate[0]+"--strPayCycleDate[1]==>"+strPayCycleDate[1]+"--strPayCycleDate[2]==>"+strPayCycleDate[2]);
					pst = con.prepareStatement("select * from emp_esi_details where eesi_contribution > 0 and emp_id=? and paycycle=? and _month=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strPayCycleDate[2]));
					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT, "MM")));
					rs = pst.executeQuery();
					if(rs.next()) {
						deductFlag = true;
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paid_from=? and paid_to=? and paycycle=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strPayCycleDate[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPayCycleDate[2]));
					rs = pst.executeQuery();
					if(rs.next()) {
						attemptFlag = true;
					}
					rs.close();
					pst.close();					
				}
			} else if(nMonth != ESI_PERIOD_2_START && (nMonth >= ESI_PERIOD_2_START || nMonth <= ESI_PERIOD_2_END)) {
				int year = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy"));
				if(nMonth<4) {
					year= year-1;
				}
				String strPeriod2Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_2_START)+"/"+year;
				int days = uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd"));
				if(days > 15) {
					strPeriod2Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_2_START-1)+"/"+year;
				}
//				String strPeriod2Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_2_START)+"/"+uF.getDateFormat(strD2, DATE_FORMAT, "yyyy");
				if(strEmpJoiningDate != null && !strEmpJoiningDate.trim().equals("") && !strEmpJoiningDate.trim().equalsIgnoreCase("NULL") && !strEmpJoiningDate.trim().equals("-")) {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					Date date1 = sdf.parse(strPeriod2Date);
			        Date date2 = sdf.parse(strEmpJoiningDate);
			        if(date2.after(date1)) {
			        	strPeriod2Date = strEmpJoiningDate;
		            }
				}
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strPeriod2Date, CF.getStrTimeZone(), CF, strEmpOrgId);
				if(strPayCycleDate != null) {
//					System.out.println("strPayCycleDate[0]==>"+strPayCycleDate[0]+"--strPayCycleDate[1]==>"+strPayCycleDate[1]+"--strPayCycleDate[2]==>"+strPayCycleDate[2]);
					pst = con.prepareStatement("select * from emp_esi_details where eesi_contribution > 0 and emp_id=? and paycycle=? and _month=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strPayCycleDate[2]));
					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT, "MM")));
					rs = pst.executeQuery();
					if(rs.next()) {
						deductFlag = true;
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paid_from=? and paid_to=? and paycycle=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strPayCycleDate[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPayCycleDate[2]));
					rs = pst.executeQuery();
					if(rs.next()) {
						attemptFlag = true;
					}
					rs.close();
					pst.close();					
				}
			}
			
			/*if(attemptFlag && deductFlag) { 
				dblCalculatedAmount = ((dblERESIAmount * dblAmount) / 100);
			} else if(attemptFlag && !deductFlag) {
				dblCalculatedAmount = 0;
			} else {
				boolean noDeductFlag = true;
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strD2, CF.getStrTimeZone(), CF, strEmpOrgId);
				pst = con.prepareStatement("select * from emp_esi_details where emp_id=? and _month<? and paycycle<?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, nMonth);
				pst.setInt(3, uF.parseToInt(strPayCycleDate[2]));
				rs = pst.executeQuery();
				if(rs.next()) {
					noDeductFlag = false;
				}
				rs.close();
				pst.close();	

				if(noDeductFlag || nMonth == ESI_PERIOD_1_START || nMonth == ESI_PERIOD_2_START) {
					if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
						dblCalculatedAmount = ((dblERESIAmount * dblAmount) / 100);
					}
				}
				
			}*/
			
			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
				dblCalculatedAmount = ((dblERESIAmount * dblAmount) / 100);
			}
			
//			System.out.println("dblCalculatedAmount==>"+dblCalculatedAmount);

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
		return dblCalculatedAmount;
	}

	public void calculateEESI(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
		Map<String, String> hmTotal, String strEmpId, String strD1, String strPaycycle, Map<String, String> hmEmpStateMap, Map<String, String> hmVariables, 
		boolean isInsert,Map<String, String> hmAnnualVariables, String strD2, Map<String, List<Map<String, String>>> hmEmpArrear, Map<String, Map<String, String>> hmArrearEmployerESI, 
		Map<String, List<Map<String, String>>> hmIncrementEmpArrear, Map<String, Map<String, String>> hmIncrementArrearEmployerESI,Map<String, List<String>> hmIncrementArrearPaycycle, 
		Map<String, String> hmTotalESIC) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmountEESI = 0.0d;
		double dblCalculatedAmountERSI = 0.0d;

		Database db = null;
		try {
			if (con == null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			
			String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			String strEmpJoiningDate = CF.getEmpJoiningDate(con, uF, uF.parseToInt(strEmpId));
			String strLevelId = CF.getEmpLevelId(con, strEmpId);

			pst = con.prepareStatement("select * from esi_details where financial_year_start=? and financial_year_end=? and state_id=? " +
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
			String strEligibleSalaryHeads = null;
			while (rs.next()) {
				dblEESIAmount = rs.getDouble("eesi_contribution");
				dblERSIAmount = rs.getDouble("ersi_contribution");
				dblMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strEligibleSalaryHeads = rs.getString("eligible_salary_head_ids");
			}
			rs.close();
			pst.close();

			String[] arrSalaryHeads = null;
			if (strSalaryHeads != null) {
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
//					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
					dblAmountEligibility += uF.parseToDouble(hmTotalESIC.get(arrSalaryHeads[i].trim()));
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
			}

			/**
			 * Change on 24-04-2012
			 */
			
			

//			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblMaxAmount) {
//				dblCalculatedAmountEESI = (dblEESIAmount * dblAmount) / 100;
//				dblCalculatedAmountERSI = (dblERSIAmount * dblAmount) / 100;
//			}
			
			int nMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			boolean attemptFlag = false;
			boolean deductFlag = false;
			
			if(nMonth != ESI_PERIOD_1_START && nMonth >= ESI_PERIOD_1_START && nMonth <= ESI_PERIOD_1_END) {
				String strPeriod1Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_1_START)+"/"+uF.getDateFormat(strD2, DATE_FORMAT, "yyyy");
				if(strEmpJoiningDate != null && !strEmpJoiningDate.trim().equals("") && !strEmpJoiningDate.trim().equalsIgnoreCase("NULL") && !strEmpJoiningDate.trim().equals("-")) {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					Date date1 = sdf.parse(strPeriod1Date);
			        Date date2 = sdf.parse(strEmpJoiningDate);
			        if(date2.after(date1)) {
			        	strPeriod1Date = strEmpJoiningDate;
		            }
				}
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strPeriod1Date, CF.getStrTimeZone(), CF, strEmpOrgId);
				if(strPayCycleDate != null) {
//					System.out.println("strPayCycleDate[0]==>"+strPayCycleDate[0]+"--strPayCycleDate[1]==>"+strPayCycleDate[1]+"--strPayCycleDate[2]==>"+strPayCycleDate[2]);
					pst = con.prepareStatement("select * from emp_esi_details where eesi_contribution > 0 and emp_id=? and paycycle=? and _month=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strPayCycleDate[2]));
					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT, "MM")));
					rs = pst.executeQuery();
					if(rs.next()) {
						deductFlag = true;
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paid_from=? and paid_to=? and paycycle=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strPayCycleDate[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPayCycleDate[2]));
					rs = pst.executeQuery();
					if(rs.next()) {
						attemptFlag = true;
					}
					rs.close();
					pst.close();					
				}
			} else if(nMonth != ESI_PERIOD_2_START && (nMonth >= ESI_PERIOD_2_START || nMonth <= ESI_PERIOD_2_END)) {
//				System.out.println("insert ESI_PERIOD_2_START ===>> " );
				int year = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy"));
				if(nMonth<4) {
					year= year-1;
				}
				String strPeriod2Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_2_START)+"/"+year;
				int days = uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd"));
				if(days > 15) {
					strPeriod2Date = uF.zero(uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")))+"/"+uF.zero(ESI_PERIOD_2_START-1)+"/"+year;
				}
				if(strEmpJoiningDate != null && !strEmpJoiningDate.trim().equals("") && !strEmpJoiningDate.trim().equalsIgnoreCase("NULL") && !strEmpJoiningDate.trim().equals("-")) {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					Date date1 = sdf.parse(strPeriod2Date);
			        Date date2 = sdf.parse(strEmpJoiningDate);
			        if(date2.after(date1)) {
			        	strPeriod2Date = strEmpJoiningDate;
		            }
				}
//				System.out.println("insert ESI_PERIOD_2_START strPeriod2Date ===>> " + strPeriod2Date);
				
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strPeriod2Date, CF.getStrTimeZone(), CF, strEmpOrgId);
				if(strPayCycleDate != null) {
//					System.out.println("strPayCycleDate[0]==>"+strPayCycleDate[0]+"--strPayCycleDate[1]==>"+strPayCycleDate[1]+"--strPayCycleDate[2]==>"+strPayCycleDate[2]);
					pst = con.prepareStatement("select * from emp_esi_details where eesi_contribution > 0 and emp_id=? and paycycle=? and _month=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strPayCycleDate[2]));
					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT, "MM")));
//					System.out.println("pst ============>> " + pst);
					rs = pst.executeQuery();
					if(rs.next()) {
						deductFlag = true;
					}
					rs.close();
					pst.close();
					
					pst = con.prepareStatement("select * from payroll_generation where emp_id=? and paid_from=? and paid_to=? and paycycle=?");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setDate(2, uF.getDateFormat(strPayCycleDate[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strPayCycleDate[1], DATE_FORMAT));
					pst.setInt(4, uF.parseToInt(strPayCycleDate[2]));
//					System.out.println("pst ============>> " + pst);
					rs = pst.executeQuery();
					if(rs.next()) {
						attemptFlag = true;
					}
					rs.close();
					pst.close();					
				}
			}
//			System.out.println("deductFlag ===>> " + deductFlag +" --- attemptFlag ===>> " + attemptFlag);
			
			/*if(attemptFlag && deductFlag) { 
//				System.out.println("===============1");
				dblCalculatedAmountEESI = (dblEESIAmount * dblAmount) / 100;
				dblCalculatedAmountERSI = (dblERSIAmount * dblAmount) / 100;
			} else if(attemptFlag && !deductFlag) {
//				System.out.println("===============2");

				boolean noDeductFlag = true;
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, strD2, CF.getStrTimeZone(), CF, strEmpOrgId);
				pst = con.prepareStatement("select * from emp_esi_details where emp_id=? and _month<? and paycycle<?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, nMonth);
				pst.setInt(3, uF.parseToInt(strPayCycleDate[2]));
				rs = pst.executeQuery();
				if(rs.next()) {
					noDeductFlag = false;
				}
				rs.close();
				pst.close();	
				
				if(noDeductFlag || nMonth == ESI_PERIOD_1_START || nMonth == ESI_PERIOD_2_START) {
					if (dblAmountEligibility > 0 && dblAmountEligibility <= dblMaxAmount) {
//						System.out.println("noDeductFlag ===============3");
						dblCalculatedAmountEESI = (dblEESIAmount * dblAmount) / 100;
						dblCalculatedAmountERSI = (dblERSIAmount * dblAmount) / 100;
					}
				} else {
					dblCalculatedAmountEESI = 0;
					dblCalculatedAmountERSI = 0;
				}
			} else {
				if (dblAmountEligibility > 0 && dblAmountEligibility <= dblMaxAmount) {
//					System.out.println("===============3");
					dblCalculatedAmountEESI = (dblEESIAmount * dblAmount) / 100;
					dblCalculatedAmountERSI = (dblERSIAmount * dblAmount) / 100;
				}
//				System.out.println("===============4");
			}*/
			
			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblMaxAmount) {
				dblCalculatedAmountEESI = (dblEESIAmount * dblAmount) / 100;
				dblCalculatedAmountERSI = (dblERSIAmount * dblAmount) / 100;
			}
			
//			System.out.println("dblCalculatedAmountEESI ===>> " + dblCalculatedAmountEESI);
			
			if (isInsert) {
				
				if(hmEmpArrear !=null && !hmEmpArrear.isEmpty() && hmArrearEmployerESI !=null && !hmArrearEmployerESI.isEmpty()) {
					List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
					if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
					for(Map<String,String> hmApplyArear : alArrear) {
						int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
						Map<String, String> hmEmployerESI = hmArrearEmployerESI.get(strEmpId+"_"+nArrearPaycycle);
						if(hmEmployerESI !=null && !hmEmployerESI.isEmpty()) {
							dblAmount += uF.parseToDouble(hmEmployerESI.get("ESI_MAX_LIMIT"));
							dblCalculatedAmountEESI += uF.parseToDouble(hmEmployerESI.get("ESI_EMPLOYEE_CONTRIBUTION"));
							dblCalculatedAmountERSI += uF.parseToDouble(hmEmployerESI.get("ESI_EMPLOYER_CONTRIBUTION"));
						}
					}
				}
				
				if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty() && hmIncrementArrearEmployerESI !=null && !hmIncrementArrearEmployerESI.isEmpty()) {
					List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
					if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
					for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
						int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
						List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
						for(String strArrearPaycycle : alArrearPaycycle) {
							Map<String, String> hmIncrementEmployerESI = hmIncrementArrearEmployerESI.get(strEmpId+"_"+nArrearId+"_"+strArrearPaycycle);
							if(hmIncrementEmployerESI !=null && !hmIncrementEmployerESI.isEmpty()) {
								dblAmount += uF.parseToDouble(hmIncrementEmployerESI.get("ESI_MAX_LIMIT"));
								dblCalculatedAmountEESI += uF.parseToDouble(hmIncrementEmployerESI.get("ESI_EMPLOYEE_CONTRIBUTION"));
								dblCalculatedAmountERSI += uF.parseToDouble(hmIncrementEmployerESI.get("ESI_EMPLOYER_CONTRIBUTION"));
							}
						}
					}
				}
				
				pst = con.prepareStatement("insert into emp_esi_details (financial_year_start, financial_year_end, salary_head_id, esi_max_limit, "
						+ "eesi_contribution, ersi_contribution, user_id, entry_timestamp, emp_id, paycycle, _month) values (?,?,?,?, ?,?,?,?, ?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
				pst.setDouble(4, dblAmount);
				pst.setDouble(5, Math.ceil(dblCalculatedAmountEESI));
				pst.setDouble(6, Math.ceil(dblCalculatedAmountERSI));
				pst.setInt(7, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setTimestamp(8, null);
				pst.setInt(9, uF.parseToInt(strEmpId));
				pst.setInt(10, uF.parseToInt(strPaycycle));
				pst.setInt(11, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM")));
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.closeConnection(con);
			}

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
	
	public double calculateERPF(Connection con, CommonFunctions CF, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart,
			String strFinancialYearEnd, Map<String, String> hmTotal, String strEmpId, String strMonth, String strPaycycle, boolean isInsert,
			Map<String, Map<String, String>> hmArearAmountMap,Map<String, List<Map<String, String>>> hmEmpArrear,
			Map<String, Map<String, String>> hmArrearEmployerPF, Map<String, List<Map<String, String>>> hmIncrementEmpArrear,
			Map<String, Map<String, String>> hmIncrementArrearEmployerPF,Map<String, List<String>> hmIncrementArrearPaycycle) {
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
			if (con == null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			if (hmArearAmountMap == null)
				hmArearAmountMap = new HashMap<String, Map<String, String>>();

			pst = con
					.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? "
							+ "and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, "
							+ "level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  "
							+ "and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
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
			while (rs.next()) {

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

			String[] arrSalaryHeads = null;
			if (strSalaryHeads != null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}

			double dblAmount = 0;
			double dblAmountERPF = 0;
			double dblAmountEEPF = 0;
			double dblAmountERPS = 0;
			double dblAmountERPS1 = 0;
			double dblAmountEREDLI = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}

			Map<String, String> hmArearMap = hmArearAmountMap.get(strEmpId);
			if (hmArearMap == null)
				hmArearMap = new HashMap<String, String>();

			double dblArrearBasic = uF.parseToDouble(hmArearMap.get("BASIC_AMOUNT"));

			dblAmount += dblArrearBasic;

			/**
			 * Changed on 24-04-2012
			 * 
			 */

			if (dblAmount >= dblEPRMaxAmount) {
				dblAmountERPF = dblEPRMaxAmount;
			} else {
				dblAmountERPF = dblAmount;
			}

			if (dblAmount >= dblEPFMaxAmount) {
				dblAmountEEPF = dblEPFMaxAmount;
			} else {
				dblAmountEEPF = dblAmount;
			}

			dblAmountERPS1 = dblAmount;
			if (dblAmount >= dblEPSMaxAmount) {
				dblAmountERPS = dblEPSMaxAmount;
			} else {
				dblAmountERPS = dblAmount;
			}

			if (dblAmount >= dblEDLIMaxAmount) {
				dblAmountEREDLI = dblEDLIMaxAmount;
			} else {
				dblAmountEREDLI = dblAmount;
			}

			if (isInsert) {
				dblEPF = ((dblERPFAmount * dblAmountERPF) / 100);
				dblEPS = ((dblERPSAmount * dblAmountERPS) / 100);

				dblEPS1 = ((dblERPSAmount * dblAmountERPS1) / 100);

				dblEDLI = ((dblERDLIAmount * dblAmountEREDLI) / 100);
				dblEDLIAdmin = ((dblEDLIAdminAmount * dblAmountEREDLI) / 100);
				dblEPFAdmin = ((dblPFAdminAmount * dblAmountEEPF) / 100);
			} else {
				if (erpfContributionchbox) {
					dblEPF = ((dblERPFAmount * dblAmountERPF) / 100);
				}
				if (erpsContributionchbox) {
					dblEPS = ((dblERPSAmount * dblAmountERPS) / 100);
					dblEPS1 = ((dblERPSAmount * dblAmountERPS1) / 100);
				}

				if (erdliContributionchbox) {
					dblEDLI = ((dblERDLIAmount * dblAmountEREDLI) / 100);
				}

				if (edliAdminChargeschbox) {
					dblEDLIAdmin = ((dblEDLIAdminAmount * dblAmountEREDLI) / 100);
				}
				if (pfAdminChargeschbox) {
					dblEPFAdmin = ((dblPFAdminAmount * dblAmountEEPF) / 100);
				}
			}

			if (CF.isEPF_Condition1()) {
				dblEPF += dblEPS1 - dblEPS;
			}

			dblTotalEDLI = dblEDLI + dblEDLIAdmin;
			dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;

			if (isInsert) {
				
				if(hmEmpArrear !=null && !hmEmpArrear.isEmpty() && hmArrearEmployerPF !=null && !hmArrearEmployerPF.isEmpty()) {
					List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
					if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
					for(Map<String,String> hmApplyArear : alArrear) {
						int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
						Map<String, String> hmEmployerPF = hmArrearEmployerPF.get(strEmpId+"_"+nArrearPaycycle);
						if(hmEmployerPF !=null && !hmEmployerPF.isEmpty()) {
							dblAmountERPS += uF.parseToDouble(hmEmployerPF.get("EPS_MAX_LIMIT"));
							dblAmountEREDLI += uF.parseToDouble(hmEmployerPF.get("EDLI_MAX_LIMIT"));
							dblEPF += uF.parseToDouble(hmEmployerPF.get("ERPF_CONTRIBUTION"));
							dblEPS += uF.parseToDouble(hmEmployerPF.get("ERPS_CONTRIBUTION"));
							dblEDLI += uF.parseToDouble(hmEmployerPF.get("ERDLI_CONTRIBUTION"));
							dblEPFAdmin += uF.parseToDouble(hmEmployerPF.get("PF_ADMIN_CHARGES"));
							dblEDLIAdmin += uF.parseToDouble(hmEmployerPF.get("EDLI_ADMIN_CHARGES"));
						}
					}
				}
				
				if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty() && hmIncrementArrearEmployerPF !=null && !hmIncrementArrearEmployerPF.isEmpty()) {
					List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
					if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
					for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
						int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
						List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
						for(String strArrearPaycycle : alArrearPaycycle) {
							Map<String, String> hmIncrementEmployerPF = hmIncrementArrearEmployerPF.get(strEmpId+"_"+nArrearId+"_"+strArrearPaycycle);
							if(hmIncrementEmployerPF !=null && !hmIncrementEmployerPF.isEmpty()) {
								dblAmountERPS += uF.parseToDouble(hmIncrementEmployerPF.get("EPS_MAX_LIMIT"));
								dblAmountEREDLI += uF.parseToDouble(hmIncrementEmployerPF.get("EDLI_MAX_LIMIT"));
								dblEPF += uF.parseToDouble(hmIncrementEmployerPF.get("ERPF_CONTRIBUTION"));
								dblEPS += uF.parseToDouble(hmIncrementEmployerPF.get("ERPS_CONTRIBUTION"));
								dblEDLI += uF.parseToDouble(hmIncrementEmployerPF.get("ERDLI_CONTRIBUTION"));
								dblEPFAdmin += uF.parseToDouble(hmIncrementEmployerPF.get("PF_ADMIN_CHARGES"));
								dblEDLIAdmin += uF.parseToDouble(hmIncrementEmployerPF.get("EDLI_ADMIN_CHARGES"));
							}
						}
					}
				}
				
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
					pst = con.prepareStatement("update emp_epf_details set eps_max_limit=?, edli_max_limit=?, erpf_contribution=?, erps_contribution=?, erdli_contribution=?, pf_admin_charges=?, edli_admin_charges=?  where financial_year_start=? and financial_year_end=? and emp_id=? and paycycle=? and _month=?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmountERPS)));
					pst.setDouble(2, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmountEREDLI)));
					
					double totalErpf = dblEPF+dblEPS;
					pst.setDouble(3,totalErpf);
					pst.setDouble(4,0);
					pst.setDouble(5,dblEDLI);
					pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEPFAdmin)));
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEDLIAdmin)));
					pst.setDate(8, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(10, uF.parseToInt(strEmpId));
					pst.setInt(11, uF.parseToInt(strPaycycle));
					pst.setInt(12, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
//					System.out.println("AP/9503--pst="+pst);
					pst.execute();
					pst.close();
				} else{
					pst = con.prepareStatement("update emp_epf_details set eps_max_limit=?, edli_max_limit=?, erpf_contribution=?, erps_contribution=?, erdli_contribution=?, pf_admin_charges=?, edli_admin_charges=?  where financial_year_start=? and financial_year_end=? and emp_id=? and paycycle=? and _month=?");
					pst.setDouble(1, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmountERPS)));
					pst.setDouble(2, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAmountEREDLI)));
					/*pst.setDouble(3, Math.round(dblEPF));
					pst.setDouble(4, Math.round(dblEPS));
					pst.setDouble(5, Math.round(dblEDLI));*/
					pst.setDouble(3,dblEPF);
					pst.setDouble(4,dblEPS);
					pst.setDouble(5,dblEDLI);
					pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEPFAdmin)));
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEDLIAdmin)));
					pst.setDate(8, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(10, uF.parseToInt(strEmpId));
					pst.setInt(11, uF.parseToInt(strPaycycle));
					pst.setInt(12, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
//					System.out.println("AP/9523---pst="+pst);
					pst.execute();
					pst.close();
				}
		//===end parvez date: 14-01-2022===		
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.closeConnection(con);
			}
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
		return (dblTotalEPF + dblTotalEDLI);
	}

	public double calculateEEPF(Connection con, String strDomain, UtilityFunctions uF, double dblGross, String strFinancialYearStart,
			String strFinancialYearEnd, Map<String, String> hmTotal, Map<String, String> hmVoluntaryPF, String strEmpId, String strMonth, String strPaycycle,
			boolean isInsert, Map<String, Map<String, String>> hmArearAmountMap, Map<String, List<Map<String, String>>> hmEmpArrear,
			Map<String, Map<String, String>> hmArrearEmployeePF, Map<String, List<Map<String, String>>> hmIncrementEmpArrear,
			Map<String, Map<String, String>> hmIncrementArrearEmployeePF,Map<String, List<String>> hmIncrementArrearPaycycle) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		Database db = null;
		try {
			if (con == null) {
				db = new Database();
				db.setRequest(request);
				db.setDomain(strDomain);
				con = db.makeConnection(con);
			}
			if (hmArearAmountMap == null)
				hmArearAmountMap = new HashMap<String, Map<String, String>>();

			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? "
							+ "and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, "
							+ "level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  "
							+ "and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setInt(4, uF.parseToInt(strEmpId));
//			if(uF.parseToInt(strEmpId) == 4581) {
//				System.out.println("pst==>"+pst);
//			} 
			rs = pst.executeQuery();
			double dblEEPFAmount = 0;
			double dblMaxAmount = 0;
			String strSalaryHeads = null;
			while (rs.next()) {
				dblEEPFAmount = rs.getDouble("eepf_contribution");
				dblMaxAmount = rs.getDouble("epf_max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String[] arrSalaryHeads = null;
			if (strSalaryHeads != null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
//			if(uF.parseToInt(strEmpId) == 4581) {
//				System.out.println("hmTotal==>"+hmTotal);
//			}

			double dblAmount = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
				/*if(uF.parseToInt(strEmpId) == 414) {
					System.out.println("arrSalaryHeads[i]==>"+arrSalaryHeads[i]+"--hmTotal.get(arrSalaryHeads[i])==>"+hmTotal.get(arrSalaryHeads[i]));
				}*/
			}

			Map<String, String> hmArearMap = hmArearAmountMap.get(strEmpId);
			if (hmArearMap == null)
				hmArearMap = new HashMap<String, String>();

			double dblArrearBasic = uF.parseToDouble(hmArearMap.get("BASIC_AMOUNT"));
			/*if(uF.parseToInt(strEmpId)==414){
				System.out.println("AP/9708---dblArrearBasic="+dblArrearBasic);
			}*/

			dblAmount += dblArrearBasic;

			/**
			 * Change on 24-04-2012
			 */

			if (dblAmount >= dblMaxAmount) {
				dblAmount = dblMaxAmount;

			}
			
			/*if(uF.parseToInt(strEmpId)==414){
				System.out.println("AP/9720---dblEEPFAmount="+dblEEPFAmount+"---dblAmount="+dblAmount);
//				System.out.println("AP/9685---dblAmount="+dblAmount+"---dblMaxAmount="+dblMaxAmount);
			}*/
			
			dblCalculatedAmount = (dblEEPFAmount * dblAmount) / 100;
			
			/*if(uF.parseToInt(strEmpId)==370){
				System.out.println("ApP/9727---dblCalculatedAmount="+dblCalculatedAmount);
			}*/

			/**
			 * If VPF is to be calculated separately, the the below code needs
			 * to be commented
			 * 
			 * */

			// if(hmVoluntaryPF==null) {
			// hmVoluntaryPF = new HashMap();
			// }
			// dblCalculatedAmount +=
			// uF.parseToDouble(hmVoluntaryPF.get("AMOUNT"));

			if (isInsert) {

				/*
				 * if(hmVoluntaryPF==null) { hmVoluntaryPF = new HashMap(); }
				 * dblCalculatedAmount +=
				 * uF.parseToDouble(hmVoluntaryPF.get("AMOUNT"));
				 */
				double dblEVPF = uF.parseToDouble(hmTotal.get(VOLUNTARY_EPF + ""));
				
				if(hmEmpArrear !=null && !hmEmpArrear.isEmpty() && hmArrearEmployeePF !=null && !hmArrearEmployeePF.isEmpty()) {
					List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
					if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
					for(Map<String,String> hmApplyArear : alArrear) {
						int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
						Map<String, String> hmEmployeePF = hmArrearEmployeePF.get(strEmpId+"_"+nArrearPaycycle);
						if(hmEmployeePF !=null && !hmEmployeePF.isEmpty()) {
							dblAmount += uF.parseToDouble(hmEmployeePF.get("EPF_MAX_LIMIT"));
							dblCalculatedAmount += uF.parseToDouble(hmEmployeePF.get("EPF_EEPF_CONTRIBUTION"));
							dblEVPF += uF.parseToDouble(hmEmployeePF.get("EPF_EVPF_CONTRIBUTION"));
						}
					}
				}
				
				if(hmIncrementEmpArrear !=null && !hmIncrementEmpArrear.isEmpty() && hmIncrementArrearEmployeePF !=null && !hmIncrementArrearEmployeePF.isEmpty()) {
					List<Map<String, String>> alIncrementArrear = hmIncrementEmpArrear.get(strEmpId);
					if(alIncrementArrear == null) alIncrementArrear = new ArrayList<Map<String,String>>();
					for(Map<String,String> hmIncrementApplyArear : alIncrementArrear) {
						int nArrearId = uF.parseToInt(hmIncrementApplyArear.get("ARREAR_ID"));
						List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
						for(String strArrearPaycycle : alArrearPaycycle) {
							Map<String, String> hmIncrementEmployeePF = hmIncrementArrearEmployeePF.get(strEmpId+"_"+nArrearId+"_"+strArrearPaycycle);
							if(hmIncrementEmployeePF !=null && !hmIncrementEmployeePF.isEmpty()) {
								dblAmount += uF.parseToDouble(hmIncrementEmployeePF.get("EPF_MAX_LIMIT"));
								dblCalculatedAmount += uF.parseToDouble(hmIncrementEmployeePF.get("EPF_EEPF_CONTRIBUTION"));
								dblEVPF += uF.parseToDouble(hmIncrementEmployeePF.get("EPF_EVPF_CONTRIBUTION"));
							}
						}
					}
				}

				pst = con.prepareStatement("insert into emp_epf_details (financial_year_start, financial_year_end, salary_head_id, epf_max_limit, " +
					"eepf_contribution, emp_id, paycycle, _month, evpf_contribution) values (?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
				/*pst.setDouble(4, Math.round(dblAmount));
				pst.setDouble(5, Math.round(dblCalculatedAmount));*/
				pst.setDouble(4,dblAmount);
				pst.setDouble(5,dblCalculatedAmount);
				pst.setInt(6, uF.parseToInt(strEmpId));
				pst.setInt(7, uF.parseToInt(strPaycycle));
				pst.setInt(8, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
				//pst.setDouble(9, Math.round(dblEVPF));
				pst.setDouble(9,dblEVPF);
//				System.out.println("pst==>"+pst);
				pst.execute();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.closeConnection(con);
			}

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
		return dblCalculatedAmount;
	}

	double calculateKrishiKalyanCess(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId,
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

	double calculateSwachhaBharatCess(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherTaxDetails,
			Map<String, String> hmEmpServiceTaxMap, CommonFunctions CF) {
		double dblSwachhaBharatCessAmount = 0;
		try {
			if (!hmEmpServiceTaxMap.containsKey(strEmpId))
				return 0;

			double dblSwachhaBharatCess = uF.parseToDouble(hmOtherTaxDetails.get(strStateId + "_SWACHHA_BHARAT_CESS"));
			dblSwachhaBharatCessAmount = (dblGross * dblSwachhaBharatCess) / 100;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblSwachhaBharatCessAmount;
	}

	public double calculateServiceTax(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId, Map<String, String> hmTotal,
			String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherTaxDetails,
			Map<String, String> hmEmpServiceTaxMap, CommonFunctions CF) {
		double dblServiceTaxAmount = 0;
		double dblCess1Amount = 0;
		double dblCess2Amount = 0;
		try {

			if (!hmEmpServiceTaxMap.containsKey(strEmpId))
				return 0;

			double dblServiceTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId + "_SERVICE_TAX"));
			double dblEduTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId + "_EDU_TAX"));
			double dblSTDTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId + "_STD_TAX"));

			dblServiceTaxAmount = (dblGross * dblServiceTax) / 100;
			dblServiceTaxAmount = dblServiceTaxAmount + dblCess1Amount + dblCess2Amount;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblServiceTaxAmount;
	}

	public double getIndividualOtherEarningCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIndividualOtherEarning,
			CommonFunctions CF) {
		double dblIndividualOtherEarningAmount = 0;
		try {
			dblIndividualOtherEarningAmount = uF.parseToDouble(hmIndividualOtherEarning.get(strEmpId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblIndividualOtherEarningAmount;
	}

	public double getOtherReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherReimbursement,
			CommonFunctions CF) {
		double dblOtherReimbursementAmount = 0;
		try {
			dblOtherReimbursementAmount = uF.parseToDouble(hmOtherReimbursement.get(strEmpId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblOtherReimbursementAmount;
	}

	public double getMobileReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmMobileReimbursement,
			CommonFunctions CF) {
		double dblMobileReimbursementAmount = 0;
		try {
			dblMobileReimbursementAmount = uF.parseToDouble(hmMobileReimbursement.get(strEmpId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblMobileReimbursementAmount;
	}

	public double getTravelReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmTravelReimbursement,
			CommonFunctions CF) {
		double dblTravelReimbursementAmount = 0;
		try {
			dblTravelReimbursementAmount = uF.parseToDouble(hmTravelReimbursement.get(strEmpId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblTravelReimbursementAmount;
	}

	public double getReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmReimbursement,
			CommonFunctions CF) {
		double dblReimbursementAmount = 0;
		try {
			dblReimbursementAmount = uF.parseToDouble(hmReimbursement.get(strEmpId));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblReimbursementAmount;
	}

	public double getIncentivesCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap,
			Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIncentives,
			CommonFunctions CF) {
		double dblIncentiveAmount = 0;
		try {
			dblIncentiveAmount = uF.parseToDouble(hmIncentives.get(strEmpId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblIncentiveAmount;
	}

	public double getArearCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal,
			String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, Map<String, String>> hmArearAmountMap, CommonFunctions CF) {
		double dblMonthlyAmount = 0;
		try {
			Map<String, String> hmArearMap = hmArearAmountMap.get(strEmpId);
			if (hmArearMap == null) hmArearMap = new HashMap<String, String>();

			double dblBalanceAmount = uF.parseToDouble(hmArearMap.get("AMOUNT_BALANCE"));
			dblMonthlyAmount = uF.parseToDouble(hmArearMap.get("MONTHLY_AREAR"));

			if ((dblBalanceAmount - dblMonthlyAmount) > 0 && (dblBalanceAmount - dblMonthlyAmount) < 1) {
				dblMonthlyAmount = dblBalanceAmount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblMonthlyAmount;
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
			// System.out.println("pst===>" + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				dblExGratiaAmount = uF.parseToDouble(rs.getString("pay_amount"));
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
		return dblExGratiaAmount;
	}
	
//===updated by parvez date: 15-04-2022===	
	//===start===
	private double getLeaveEncashmentAmtDetails(Connection con, UtilityFunctions uF, CommonFunctions CF, String strEmpId, double dblEnashDays,
			int nTotalNumberOfDaysForCalc, String strD1, String strD2, String strPC, String strLevel, double dblIncrementBasic, double dblIncrementDA) {
		double dblEncashAmount = 0.0d;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("APay/9929---nTotalNumberOfDaysForCalc="+nTotalNumberOfDaysForCalc+"--dblEnashDays="+dblEnashDays);
//			System.out.println("APay/9930---dblIncrementBasic="+dblIncrementBasic+"--dblIncrementDA="+dblIncrementDA);
			pst = con.prepareStatement("select sum(no_days) as no_days,leave_type_id from emp_leave_encashment where emp_id=? "
					+ "and paid_from= ? and paid_to=? and paycycle=? and is_approved=1 and is_paid=false group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strPC));
//			 System.out.println("APay/9937--pst===>>>>>>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmLeaveEncashDays = new HashMap<String, String>();
			StringBuilder sbLeaveTypeId = null;
			while (rs.next()) {
				hmLeaveEncashDays.put(rs.getString("leave_type_id"), rs.getString("no_days"));

				if (sbLeaveTypeId == null) {
					sbLeaveTypeId = new StringBuilder();
					sbLeaveTypeId.append(rs.getString("leave_type_id"));
				} else {
					sbLeaveTypeId.append("," + rs.getString("leave_type_id"));
				}
			}
			rs.close();
			pst.close();

			Map<String, String> hmLeaveSalaryHeads = new HashMap<String, String>();
			Map<String, String> hmLeaveSHeadsPercetage = new HashMap<String, String>();
			if (sbLeaveTypeId != null) {
				pst = con.prepareStatement("select leave_type_id,salary_head_id,percentage from emp_leave_type where leave_type_id in ("
						+ sbLeaveTypeId.toString() + ") and level_id=?");
				pst.setInt(1, uF.parseToInt(strLevel));
				rs = pst.executeQuery();
//				System.out.println("APay/9961---pst="+pst);
				while (rs.next()) {
					hmLeaveSalaryHeads.put(rs.getString("leave_type_id"), rs.getString("salary_head_id"));
					hmLeaveSHeadsPercetage.put(rs.getString("leave_type_id"), rs.getString("percentage"));
				}
				rs.close();
				pst.close();
			}

			Iterator<String> it1 = hmLeaveSalaryHeads.keySet().iterator();
			while (it1.hasNext()) {
				String strLeaveTypeId = it1.next();
				String strSalaryHeads = hmLeaveSalaryHeads.get(strLeaveTypeId);
				List<String> alsalaryHeads = Arrays.asList(strSalaryHeads.split(","));
				if (alsalaryHeads == null) alsalaryHeads = new ArrayList<String>();
//				System.out.println("APay/9976--alsalaryHeads="+alsalaryHeads);

				double dblPercentage = uF.parseToDouble(hmLeaveSHeadsPercetage.get(strLeaveTypeId));
				if (dblPercentage == 0.0d) {
//					System.out.println("APay/9980--dblPercentage="+dblPercentage+"--strLeaveTypeId="+strLeaveTypeId);
					continue;
				}

				dblEnashDays = uF.parseToDouble(hmLeaveEncashDays.get(strLeaveTypeId));

				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
//				Map<String, Map<String, String>> hmInnerActualCTC = new HashMap<String, Map<String, String>>(); //CF.getSalaryCalculation(con, hmInnerisDisplay, uF.parseToInt(strEmpId), dblEnashDays, 0, 0,nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2);
		//===start parvez date: 06-04-2022===		
				Map<String, Map<String, String>> hmInnerActualCTC = CF.getSalaryCalculation(con, uF.parseToInt(strEmpId), dblEnashDays, 0, 0,nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, uF.getDateFormat(strD2, DATE_FORMAT,DBDATE));
		//===end parvez date: 06-04-2022===
//				System.out.println("APay/9991---hmInnerActualCTC="+hmInnerActualCTC);

				double salaryGross = 0;
				double salaryDeduction = 0;
				Iterator<String> it = hmInnerActualCTC.keySet().iterator();
				while (it.hasNext()) {
					String strSalaryId = it.next();

					Map<String, String> hm = hmInnerActualCTC.get(strSalaryId);
					if (hm.get("EARNING_DEDUCTION").equals("E") && alsalaryHeads.contains(strSalaryId)) {
						salaryGross += uF.parseToDouble(hm.get("AMOUNT"));
					} else if (hm.get("EARNING_DEDUCTION").equals("D") && alsalaryHeads.contains(strSalaryId)) {
						salaryDeduction += uF.parseToDouble(hm.get("AMOUNT"));
					}
				}
//				System.out.println("APay/10006--salaryGross="+salaryGross);

				dblEncashAmount += ((salaryGross - salaryDeduction) * dblPercentage) / 100;
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
		return (dblEncashAmount > 0.0d ? dblEncashAmount : 0.0d);
	}
//===end===	

	public double getIncrementCalculationDA(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmBasicSalaryMap,
			Map<String, String> hmDASalaryMap, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, CommonFunctions CF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIncrement = 0;

		try {
			double dblDA = uF.parseToDouble(hmDASalaryMap.get(strEmpId));

			pst = con.prepareStatement("select * from increment_details_da where increment_from <= ? and  ?<= increment_to and due_month like ? ");
			pst.setDouble(1, dblDA);
			pst.setDouble(2, dblDA);
			pst.setString(3, "%" + nPayMonth + ",%");
			rs = pst.executeQuery();
			while (rs.next()) {
				if ("P".equalsIgnoreCase(rs.getString("increment_amount_type"))) {
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
		return dblIncrement;
	}

	public double getIncrementCalculationBasic(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmBasicSalaryMap,
			Map<String, String> hmDASalaryMap, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, CommonFunctions CF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIncrement = 0;

		try {
			double dblBasic = uF.parseToDouble(hmBasicSalaryMap.get(strEmpId));

			pst = con.prepareStatement("select * from increment_details where increment_from <= ? and  ?<= increment_to and due_month =? ");
			pst.setDouble(1, dblBasic);
			pst.setDouble(2, dblBasic);
			pst.setInt(3, nPayMonth);
			rs = pst.executeQuery();
			// System.out.println("pst increment===>"+pst);
			while (rs.next()) {
				dblIncrement = rs.getDouble("increment_amount");
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
		return dblIncrement;
	}

	public void getPrevEmpTdsAmount(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmPrevEmpTdsAmount, Map<String, String> hmPrevEmpGrossAmount) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			pst = con.prepareStatement("select * from prev_earn_deduct_details where financial_start=? and financial_end=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			if (rs.next()) {
				double dblTDSAmt = rs.getDouble("tds_amount");
				double dblGrossAmt = rs.getDouble("gross_amount");

				hmPrevEmpGrossAmount.put(rs.getString("emp_id"), "" + dblGrossAmt);
				hmPrevEmpTdsAmount.put(rs.getString("emp_id"), "" + dblTDSAmt);
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

	public void getAllowanceAmount(Connection con, UtilityFunctions uF, Map<String, String> hmAllowance, String strD1, String strD2, String strPC) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
//			pst = con.prepareStatement("select * from allowance_individual_details where is_approved=1 and pay_paycycle=? and paid_from=? and paid_to=? ");
			pst = con.prepareStatement("select sum(pay_amount) as pay_amount,emp_id,salary_head_id from allowance_individual_details " +
					"where is_approved=1 and pay_paycycle=? and paid_from=? and paid_to=? group by emp_id,salary_head_id");
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmAllowance.put(rs.getString("emp_id") + "_" + rs.getString("salary_head_id"), rs.getString("pay_amount"));
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

	public void getVariableAmount(Connection con, UtilityFunctions uF, Map<String, String> hmVariables, String strPC, String strD1, String strD2) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from otherearning_individual_details where pay_paycycle =? and is_approved = 1 and paid_from=? and paid_to=?");
			pst.setInt(1, uF.parseToInt(strPC));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmVariables.put(rs.getString("emp_id") + "_" + rs.getString("salary_head_id") + "_" + rs.getString("earning_deduction"),rs.getString("pay_amount"));
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

	public Map<String, String> getFixedExemption(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmFixedExemptions = new HashMap<String, String>();

		try {

			pst = con.prepareStatement("select * from exemption_details where exemption_from = ? and exemption_to =? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmFixedExemptions.put(rs.getString("salary_head_id"), rs.getString("exemption_limit"));
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
		return hmFixedExemptions;

	}

	public Map<String, Map<String, String>> getEmpPaidAmountDetails(Connection con, UtilityFunctions uF, String strFinancialYearStart,
			String strFinancialYearEnd) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmEmpPaidAmountDetails = new HashMap<String, Map<String, String>>();

		try {
			pst = con.prepareStatement("select sum(amount) as amount, emp_id, salary_head_id from payroll_generation where "
					+ "financial_year_from_date = ? and financial_year_to_date = ? group by emp_id, salary_head_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			Map<String, String> hmInner = new HashMap<String, String>();
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					hmInner = new HashMap<String, String>();
				}
				hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));

				hmEmpPaidAmountDetails.put(rs.getString("emp_id"), hmInner);

				strEmpIdOld = strEmpIdNew;
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
		return hmEmpPaidAmountDetails;
	}

	public Map<String, String> getEmpIncomeOtherSources(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmEmpExemptionsMap = new HashMap<String, String>();

		try {
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd "
					+ "where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id =13 "
					+ "and isdisplay=false and financial_year_start=? and financial_year_end=? group by emp_id, sd.section_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			double dblInvestmentEmp = 0;
			while (rs.next()) {
				double dblInvestment = rs.getDouble("amount_paid");

				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp + "");
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
		return hmEmpExemptionsMap;
	}

	public Map<String, String> getEmpRentPaid(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmEmpRentPaidMap = new HashMap<String, String>();

		try {
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, exemption_details ed "
					+ "where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to=? and status = true and ed.salary_head_id=? "
					+ "and trail_status = 1 and ed.exemption_from=? and ed.exemption_to=? group by emp_id ");
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
		return hmEmpRentPaidMap;

	}

	public Map<String, String> getEmpHomeLoanExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmEmpHomeLoanMap = new HashMap<String, String>();

		try {

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

//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? "
//					+ "and status = true and  section_id = 11  group by emp_id");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
					" and trail_status = 1 and parent_section=0 and  section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
					"and financial_year_end=?) group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {

				if (uF.parseToDouble(rs.getString("amount_paid")) > dblLoanExemptionLimit) {
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit + "");
				} else {
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
				}
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
		return hmEmpHomeLoanMap;

	}

	public Map<String, String> getEmpInvestmentExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd,
			double dblDeclaredInvestmentExemption) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmEmpExemptionsMap = new HashMap<String, String>();

		try {

			Map<String, String> hmSectionLimitA = new HashMap<String, String>();
			Map<String, String> hmSectionLimitP = new HashMap<String, String>();

			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				if (rs.getString("section_limit_type").equalsIgnoreCase("A")) {
					hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				} else {
					hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				}
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd "
					+ "where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 "
					+ "and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and sd.section_id !=11 "
					+ "and isdisplay=true and parent_section=0 group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			double dblInvestmentLimit = 0;
			double dblInvestmentEmp = 0;
			while (rs.next()) {
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");

				if (hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble(hmSectionLimitA.get(strSectionId));
				} else {
					dblInvestmentLimit = uF.parseToDouble(hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}

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
		return hmEmpExemptionsMap;

	}
	
	public double getAppliedGratuityAmount(Connection con, UtilityFunctions uF, String strPC, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double appliedAmt = 0;
		try {
			
			pst = con.prepareStatement("select sum(applied_amount) as applied_amount from emp_lta_details where is_approved>0 and emp_id=? and paycycle=? group by emp_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strPC));
			rs = pst.executeQuery();
//			System.out.println("ApP/10759---pst===>"+pst);
			while(rs.next()){
				appliedAmt = uF.parseToDouble(rs.getString("applied_amount"));
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
		return appliedAmt;
	}

	private String loadApprovePay(UtilityFunctions uF) {
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		paymentModeList = new FillPayMode().fillPaymentMode();
		
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);

		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}

		getSelectedFilter(uF);

		return LOAD;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		alFilter.add("DURATION");
//		System.out.println("getStrPaycycleDuration()-----"+getStrPaycycleDuration());
		if (getStrPaycycleDuration() != null) {
			String payDuration = "";
			int k = 0;
			for (int i = 0; paycycleDurationList != null && i < paycycleDurationList.size(); i++) {
				if (getStrPaycycleDuration().equals(paycycleDurationList.get(i).getPaycycleDurationId())) {
					if (k == 0) {
						payDuration = paycycleDurationList.get(i).getPaycycleDurationName();
					} else {
						payDuration += ", " + paycycleDurationList.get(i).getPaycycleDurationName();
					}
					k++;
				}
			}
			if (payDuration != null && !payDuration.equals("")) {
				hmFilter.put("DURATION", payDuration);
			} else {
				hmFilter.put("DURATION", "All Duration");
			}
		} else {
			hmFilter.put("DURATION", "All Duration");
		}

		alFilter.add("PAYMENTMODE");
//		System.out.println("getF_paymentMode()-----"+getF_paymentMode());
		if (getF_paymentMode() != null) {
			String strPayMode = "";
			int k = 0;
			for (int i = 0; paymentModeList != null && i < paymentModeList.size(); i++) {
				if (getF_paymentMode().equals(paymentModeList.get(i).getPayModeId())) {
					if (k == 0) {
						strPayMode = paymentModeList.get(i).getPayModeName();
					} else {
						strPayMode += ", " + paymentModeList.get(i).getPayModeName();
					}
					k++;
				}
			}
			if (strPayMode != null && !strPayMode.equals("")) {
				hmFilter.put("PAYMENTMODE", strPayMode);
			} else {
				hmFilter.put("PAYMENTMODE", "All Payment Mode");
			}
		} else {
			hmFilter.put("PAYMENTMODE", "All Payment Mode");
		}

		alFilter.add("ORGANISATION");
		if (getF_org() != null) {
			String strOrg = "";
			int k = 0;
			for (int i = 0; organisationList != null && i < organisationList.size(); i++) {
				if (getF_org().equals(organisationList.get(i).getOrgId())) {
					if (k == 0) {
						strOrg = organisationList.get(i).getOrgName();
					} else {
						strOrg += ", " + organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if (strOrg != null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		
		alFilter.add("PAYCYCLE");
		if (getPaycycle() != null) {
			String strPayCycle = "";
			int k = 0;
			for (int i = 0; paycycleList != null && i < paycycleList.size(); i++) {
				if (getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
					if (k == 0) {
						strPayCycle = paycycleList.get(i).getPaycycleName();
					} else {
						strPayCycle += ", " + paycycleList.get(i).getPaycycleName();
					}
					k++;
				}
			}
			if (strPayCycle != null && !strPayCycle.equals("")) {
				hmFilter.put("PAYCYCLE", strPayCycle);
			} else {
				hmFilter.put("PAYCYCLE", "All Paycycle");
			}
		}
		

		alFilter.add("LOCATION");
		if (getF_strWLocation() != null) {
			String strLocation = "";
			int k = 0;
			for (int i = 0; wLocationList != null && i < wLocationList.size(); i++) {
				for (int j = 0; j < getF_strWLocation().length; j++) {
					if (getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if (k == 0) {
							strLocation = wLocationList.get(i).getwLocationName();
						} else {
							strLocation += ", " + wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if (strLocation != null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}

		alFilter.add("DEPARTMENT");
		if (getF_department() != null) {
			String strDepartment = "";
			int k = 0;
			for (int i = 0; departmentList != null && i < departmentList.size(); i++) {
				for (int j = 0; j < getF_department().length; j++) {
					if (getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if (k == 0) {
							strDepartment = departmentList.get(i).getDeptName();
						} else {
							strDepartment += ", " + departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if (strDepartment != null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}

		alFilter.add("SERVICE");
		if (getF_service() != null) {
			String strService = "";
			int k = 0;
			for (int i = 0; serviceList != null && i < serviceList.size(); i++) {
				for (int j = 0; j < getF_service().length; j++) {
					if (getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if (k == 0) {
							strService = serviceList.get(i).getServiceName();
						} else {
							strService += ", " + serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if (strService != null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All Services");
			}
		} else {
			hmFilter.put("SERVICE", "All Services");
		}

		alFilter.add("LEVEL");
		if (getF_level() != null) {
			String strLevel = "";
			int k = 0;
			for (int i = 0; levelList != null && i < levelList.size(); i++) {
				for (int j = 0; j < getF_level().length; j++) {
					if (getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if (k == 0) {
							strLevel = levelList.get(i).getLevelCodeName();
						} else {
							strLevel += ", " + levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if (strLevel != null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		alFilter.add("GRADE");
		if (getF_grade() != null) {
			String strgrade = "";
			int k = 0;
			for (int i = 0; gradeList != null && i < gradeList.size(); i++) {
				for (int j = 0; j < getF_grade().length; j++) {
					if (getF_grade()[j].equals(gradeList.get(i).getGradeId())) {
						if (k == 0) {
							strgrade = gradeList.get(i).getGradeCode();
						} else {
							strgrade += ", " + gradeList.get(i).getGradeCode();
						}
						k++;
					}
				}
			}
			if (strgrade != null && !strgrade.equals("")) {
				hmFilter.put("GRADE", strgrade);
			} else {
				hmFilter.put("GRADE", "All Grade's");
			}
		} else {
			hmFilter.put("GRADE", "All Grade's");
		}

		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}

	public String getF_paymentMode() {
		return f_paymentMode;
	}

	public void setF_paymentMode(String f_paymentMode) {
		this.f_paymentMode = f_paymentMode;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
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

	public List<FillPayCycleDuration> getPaycycleDurationList() {
		return paycycleDurationList;
	}

	public void setPaycycleDurationList(List<FillPayCycleDuration> paycycleDurationList) {
		this.paycycleDurationList = paycycleDurationList;
	}

	public List<FillPayMode> getPaymentModeList() {
		return paymentModeList;
	}

	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
		this.paymentModeList = paymentModeList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getApprovePC() {
		return approvePC;
	}

	public void setApprovePC(String approvePC) {
		this.approvePC = approvePC;
	}

	public List<String> getStrEmpIds() {
		return strEmpIds;
	}

	public void setStrEmpIds(List<String> strEmpIds) {
		this.strEmpIds = strEmpIds;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	
	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}
	
	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	
	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public String getStrProcess() {
		return strProcess;
	}

	public void setStrProcess(String strProcess) {
		this.strProcess = strProcess;
	}	
	
}

class ApprovePayRunnable implements IConstants {

	ApprovePay objApprovePay;

	Map<String, String> hmTotal;
	Map<String, String> hmTotalESIC;
	Connection con;
	UtilityFunctions uF;
	CommonFunctions CF;
	String strFinancialYearStart;
	String strFinancialYearEnd;
	String strEmpId;
	String strOrgId;
	String[] strApprovePayCycle;
	Map<String, String> hmEmpStateMap;
	Map<String, Map<String, String>> hmCurrencyDetails;
	Map<String, String> hmEmpCurrency;
	Map<String, String> hmVariables;
	HttpServletRequest request;
	HttpServletResponse response;
	double dblTotal;

	Map<String, String> hmOtherTaxDetails;
	Map<String, String> hmEmpLevelMap;
	String strDomain;

	Map<String, Map<String, String>> hmArearAmountMap;
	Map<String, String> hmAnnualVariables;
	
	Map<String, List<Map<String, String>>> hmEmpArrear;
	Map<String, Map<String, String>> hmArrearCalSalary;
	Map<String, List<String>> hmArrearEarningHead;
	Map<String, List<String>> hmArrearDeductionHead;
	Map<String, Map<String, String>> hmArrearEmployeePF;
	Map<String, Map<String, String>> hmArrearEmployerPF;
	Map<String, Map<String, String>> hmArrearEmployerESI;
	Map<String, Map<String, String>> hmArrearEmployeeLWF;
	
	Map<String, List<Map<String, String>>> hmIncrementEmpArrear;
	Map<String, Map<String, String>> hmIncrementArrearCalSalary;
	Map<String, List<String>> hmIncrementArrearEarningHead;
	Map<String, List<String>> hmIncrementArrearDeductionHead;
	Map<String, Map<String, String>> hmIncrementArrearEmployeePF;
	Map<String, Map<String, String>> hmIncrementArrearEmployerPF;
	Map<String, Map<String, String>> hmIncrementArrearEmployerESI;
	Map<String, Map<String, String>> hmIncrementArrearEmployeeLWF;
	Map<String, List<String>> hmIncrementArrearPaycycle;

	public ApprovePayRunnable(ApprovePay objApprovePay, Connection con, UtilityFunctions uF, CommonFunctions CF, String strFinancialYearStart,
			String strFinancialYearEnd, String[] strApprovePayCycle, Map<String, String> hmEmpStateMap, Map<String, Map<String, String>> hmCurrencyDetails,
			Map<String, String> hmEmpCurrency, Map<String, String> hmVariables, HttpServletRequest request, HttpServletResponse response,
			Map<String, String> hmOtherTaxDetails, Map<String, String> hmEmpLevelMap, String strDomain, Map<String, 
			Map<String, String>> hmArearAmountMap,Map<String, String> hmAnnualVariables,
			Map<String, List<Map<String, String>>> hmEmpArrear,Map<String, Map<String, String>> hmArrearCalSalary,
			Map<String, List<String>> hmArrearEarningHead,Map<String, List<String>> hmArrearDeductionHead,
			Map<String, Map<String, String>> hmArrearEmployeePF,Map<String, Map<String, String>> hmArrearEmployerPF,
			Map<String, Map<String, String>> hmArrearEmployerESI,Map<String, Map<String, String>> hmArrearEmployeeLWF,
			Map<String, List<Map<String, String>>> hmIncrementEmpArrear,Map<String, Map<String, String>> hmIncrementArrearCalSalary,
			Map<String, List<String>> hmIncrementArrearEarningHead,Map<String, List<String>> hmIncrementArrearDeductionHead,
			Map<String, Map<String, String>> hmIncrementArrearEmployeePF,Map<String, Map<String, String>> hmIncrementArrearEmployerPF,
			Map<String, Map<String, String>> hmIncrementArrearEmployerESI,Map<String, Map<String, String>> hmIncrementArrearEmployeeLWF,
			Map<String, List<String>> hmIncrementArrearPaycycle) {
		this.objApprovePay = objApprovePay;
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
		this.hmOtherTaxDetails = hmOtherTaxDetails;
		this.hmEmpLevelMap = hmEmpLevelMap;
		this.strDomain = strDomain;
		this.hmArearAmountMap = hmArearAmountMap;
		this.hmAnnualVariables = hmAnnualVariables;
		
		this.hmEmpArrear = hmEmpArrear;
		this.hmArrearCalSalary = hmArrearCalSalary;
		this.hmArrearEarningHead = hmArrearEarningHead;
		this.hmArrearDeductionHead = hmArrearDeductionHead;
		this.hmArrearEmployeePF = hmArrearEmployeePF;
		this.hmArrearEmployerPF = hmArrearEmployerPF;
		this.hmArrearEmployerESI = hmArrearEmployerESI;
		this.hmArrearEmployeeLWF = hmArrearEmployeeLWF;
		
		this.hmIncrementEmpArrear = hmIncrementEmpArrear;
		this.hmIncrementArrearCalSalary = hmIncrementArrearCalSalary;
		this.hmIncrementArrearEarningHead = hmIncrementArrearEarningHead;
		this.hmIncrementArrearDeductionHead = hmIncrementArrearDeductionHead;
		this.hmIncrementArrearEmployeePF = hmIncrementArrearEmployeePF;
		this.hmIncrementArrearEmployerPF = hmIncrementArrearEmployerPF;
		this.hmIncrementArrearEmployerESI = hmIncrementArrearEmployerESI;
		this.hmIncrementArrearEmployeeLWF = hmIncrementArrearEmployeeLWF;
		this.hmIncrementArrearPaycycle = hmIncrementArrearPaycycle;
	}

	public void setData(Map<String, String> hmTotal, Map<String, String> hmTotalESIC, String strEmpId, double dblTotal, String strOrgId) {
		this.hmTotal = hmTotal;
		this.hmTotalESIC = hmTotalESIC;
		this.strEmpId = strEmpId;
		this.dblTotal = dblTotal;
		this.strOrgId = strOrgId;
	}

	public void run1() {
		if (hmTotal != null && hmTotal.containsKey(EMPLOYEE_EPF + "")) {
//			System.out.println("insert epf for "+strEmpId);
			objApprovePay.calculateEEPF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, strApprovePayCycle[1],
				strApprovePayCycle[2], true, hmArearAmountMap,hmEmpArrear,hmArrearEmployeePF,hmIncrementEmpArrear,hmIncrementArrearEmployeePF,hmIncrementArrearPaycycle);
			objApprovePay.calculateERPF(con, CF, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1],
				strApprovePayCycle[2], true, hmArearAmountMap,hmEmpArrear,hmArrearEmployerPF,hmIncrementEmpArrear,hmIncrementArrearEmployerPF,hmIncrementArrearPaycycle);
		}

		if (hmTotal != null && hmTotal.containsKey(EMPLOYEE_ESI + "")) {
			objApprovePay.calculateEESI(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[0],
				strApprovePayCycle[2], hmEmpStateMap, hmVariables, true, hmAnnualVariables, strApprovePayCycle[1],hmEmpArrear,hmArrearEmployerESI
				,hmIncrementEmpArrear,hmIncrementArrearEmployerESI,hmIncrementArrearPaycycle,hmTotalESIC);
		}

		if (hmTotal != null && hmTotal.containsKey(EMPLOYEE_LWF + "")) {
			objApprovePay.calculateELWF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], 
				hmEmpStateMap, hmVariables, true, hmAnnualVariables, hmEmpArrear, hmArrearEmployeeLWF, hmIncrementEmpArrear, hmIncrementArrearEmployeeLWF,hmIncrementArrearPaycycle, strOrgId);
		}

		if (hmTotal != null && hmTotal.containsKey(TDS + "")) {
			objApprovePay.calculateETDS(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1],
					strApprovePayCycle[2], hmEmpStateMap, hmVariables, true, hmOtherTaxDetails, hmEmpLevelMap, hmAnnualVariables);
		}
	}
}