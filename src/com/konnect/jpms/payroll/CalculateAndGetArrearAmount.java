package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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

public class CalculateAndGetArrearAmount extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 *   
	 */
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public CommonFunctions CF = null;
	String strUserType;
	String strSessionEmpId;

	String empId;
	List<String> strEmpIds;

	String pageType;
	String effectiveDate;
	List<FillPayCycles> paycycleList;
	String strPaycycleDuration;
	
	private String[] salary_head_id;
	private String[] salary_head_value;
	private String[] isDisplay;
	private String[] hideIsDisplay;
	private String[] emp_salary_id;
	
	private String callFrom;
	private String strArearName;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute(PAGE, "/jsp/payroll/CalculateAndGetArrearAmount.jsp");
		request.setAttribute(TITLE, "Calculated Arrear Amount");
		
//		System.out.println("getEffectiveDate() ==> " + getEffectiveDate());
//		System.out.println("getEmpId() ===>> " + getEmpId());
		
		if (getStrPaycycleDuration() == null || getStrPaycycleDuration().trim().equals("") || getStrPaycycleDuration().trim().equalsIgnoreCase("NULL")) {
			setStrPaycycleDuration("M");
		}
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if (nSalaryStrucuterType == S_GRADE_WISE) {
			viewCalculatedArrearAmountByGrade(uF, "", "", "");
		} else {
			viewCalculatedArrearAmount(uF);
		}
			
		return LOAD;
	}


	private void viewCalculatedArrearAmountByGrade(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
			List<String> alSalHeadIds = hmFeatureUserTypeId.get(F_NON_DEDUCT_SAL_HEAD_IN_ARREAR+"_USER_IDS");
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));

			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			String strEmpOrgId = CF.getEmpOrgId(con, uF, empId);
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, strEmpOrgId);
			if (hmOrg == null) hmOrg = new HashMap<String, String>();

			Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);

			StringBuilder sbQuery = new StringBuilder();

			sbQuery = new StringBuilder();
			sbQuery.append("select * from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
					+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
					+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");
			sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? "
					+ "and paid_from = ? and paid_to=? group by emp_id) order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alEmp = new ArrayList<Map<String, String>>();
			List<String> alEmpIds = new ArrayList<String>();
			Map<String, Map<String, String>> hmEmp = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpGradeMap = new HashMap<String, String>();
			List<String> alGradeId = new ArrayList<String>();
			while (rs.next()) {
				Map<String, String> hmEmpPay = new HashMap<String, String>();
				hmEmpPay.put("EMP_ID", rs.getString("emp_id"));
				hmEmpPay.put("EMPCODE", rs.getString("empcode"));

				/*String strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()
						+ " " : "";*/
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname");
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

				if (rs.getString("service_id") != null) {
					String[] tempService = rs.getString("service_id").split(",");
					if (tempService.length > 0) {
						hmEmpPay.put("EMP_SERVICE_ID", tempService[0]);
					}
				}

				alEmp.add(hmEmpPay);
				hmEmp.put(rs.getString("emp_id"), hmEmpPay);

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

			// System.out.println("alEmp====>"+alEmp);
			int nGradeIdSize = alGradeId.size();
			if (alEmp.size() > 0 && alEmpIds.size() > 0 && nGradeIdSize > 0) {
				String strEmpIds = StringUtils.join(alEmpIds.toArray(), ",");
				String strGradeIds = StringUtils.join(alGradeId.toArray(), ",");
				
				Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
				Map<String, String> hmSalaryDetails = new HashMap<String, String>();
				List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
				List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
				List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
				List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
				
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
//				System.out.println("hmSalaryDetails1==>"+hmSalaryDetails1);

				Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
				Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
				Map<String, String> hmEmpStateMap = new HashMap<String, String>();
				CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);

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
						+ "financial_year_from_date=? and financial_year_to_date =? and paycycle = ? and emp_id in (" + strEmpIds
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

					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();

				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, strEmpOrgId);
				Map<String, String> hmLoanAmt = new HashMap<String, String>();
				List<String> alLoans = new ArrayList<String>();
				Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);

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
				
				pst = con.prepareStatement("select distinct(emp_id) as emp_id from arear_details where is_paid=false " +
						"and arrear_type=1 and emp_id in ("+strEmpIds+") and paycycle<=?");
				pst.setInt(1, uF.parseToInt(strPC));
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
				
				Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
				int nAlEmp = alEmp.size();
				for (int i = 0; i < nAlEmp; i++) {
					Map<String, String> hmEmpPay = alEmp.get(i);
					String strEmpId = hmEmpPay.get("EMP_ID");
					String strOrgId = hmEmpOrgId.get(strEmpId);
					String strSalCalStatus = hmEmpPay.get("EMP_IS_DISABLE_SAL_CALCULATE");
					int nEmpId = uF.parseToInt(strEmpId);
					String strLocation = hmEmpWlocationMap.get(strEmpId);
					String strLevel = hmEmpLevelMap.get(strEmpId);
					String strGrade = hmEmpGradeMap.get(strEmpId);
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

					hmInner = CF.getSalaryCalculationByGrade(con,hmInnerisDisplay, nEmpId, dblTotalPresentDays, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strGrade, uF, CF, strD2,hmSalInner, "0.0d", strSalCalStatus);
					
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
								dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {

								switch (nSalayHead) {
									case OVER_TIME :

										double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
										dblGross += dblOverTime;
										dblGrossTDS += dblOverTime;

										break;

									case LEAVE_ENCASHMENT :
										double leaveEncashmentAmt = 0.0d;//getLeaveEncashmentAmtDetailsByGrade(con, uF, CF, strEmpId, 0.0d, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, strGrade, dblIncrementBasic, dblIncrementDA);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),leaveEncashmentAmt));

										dblGross += leaveEncashmentAmt;
										dblGrossTDS += leaveEncashmentAmt;

										break;

									case BONUS :
										double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
										dblGross += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;

										break;

									case EXGRATIA :

										double dblExGratiaAmount = getExGratiaAmount(con, uF, CF, strEmpId, strD1, strD2, strPC);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
										dblGross += dblExGratiaAmount;
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
										dblGrossTDS += dblArearAmount;

										break;

									case INCENTIVES :
										double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
												strFinancialYearEnd, nPayMonth, hmIncentives, CF);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
										dblGross += dblIncentiveAmount;
										dblGrossTDS += dblIncentiveAmount;
										break;

									case REIMBURSEMENT :
										double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
										dblGross += dblReimbursementAmount;
										break;

									case TRAVEL_REIMBURSEMENT :
										double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
										dblGross += dblTravelReimbursementAmount;
										break;

									case MOBILE_REIMBURSEMENT :
										double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
										dblGross += dblMobileReimbursementAmount;
										break;

									case OTHER_REIMBURSEMENT :
										double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
										dblGross += dblOtherReimbursementAmount;
										break;

									case OTHER_EARNING :
										double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
										dblGross += dblOtherEarningAmount;
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

									default :

										if (uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
											double dblPerkAlignAmount = 0.0d;
											if (hmPerkAlignAmount.containsKey(strSalaryId)) {
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
										} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
										} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
											dblGross += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
										} else if (uF.parseToInt(strSalaryId) != GROSS) {
											boolean isMultipePerWithParticularHead = false;
											if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
												isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
											} 
											if(!isMultipePerWithParticularHead) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
												dblGross += uF.parseToDouble(hm.get("AMOUNT"));
												dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
												dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
											}
										}

										break;
								}

							}

//						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D") && (hm.get("SALARY_AMOUNT_TYPE") != null && !hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {	
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
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									} else {

										Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");

										double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,
												hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null);
										
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
										double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,
												strEmpId, null, null, false, hmArearAmountMap,null,null);
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
												hmLoanAmt, hmEmpLoan, alLoans);
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
										} else if (alSalHeadIds==null || !alSalHeadIds.contains(strSalaryId)) {
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
						double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
						hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
					}
					if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
						double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
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
						if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE") != null
								&& hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
							if (hmPaidSalaryInner != null) {
								dblGross += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional, hmContriSalHeadAmt);
								if(!hmTotal.containsKey(strSalaryId)) {
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
									dblGross += dblMulCalAmt;
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
										double dblPt = calculateProfessionalTax(con, uF, strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
										dblDeduction += dblPt;
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									}

									break;

								/********** TDS *************/
								case TDS :
									if (hmPaidSalaryInner != null) {
										double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblTDS;
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
									} else {
										double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_FLAT_TDS"));

										if (hmEmpServiceTaxMap.containsKey(strEmpId)) {
											dblGrossTDS = dblGross;
											
											double dblCGST = uF.parseToDouble(hmTotal.get(CGST + ""));
											dblGrossTDS = dblGrossTDS - dblCGST;

											double dblSGST = uF.parseToDouble(hmTotal.get(SGST + ""));
											dblGrossTDS = dblGrossTDS - dblSGST;
										}
//
										double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
												strFinancialYearEnd, strEmpId, hmEmpLevelMap, null);

										dblDeduction += dblTDS;

										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
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
												hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
										
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
										double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId,hmAnnualVariables, strD1, strD2, strPC);
										
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
										
										dblDeduction += dblLWF;
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
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

					hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction)));

					hmTotalSalary.put(strEmpId, hmTotal);

					// =========================code for isdisplay false=======================
					Map<String, String> hmTotalisDisplay = new HashMap<String, String>();
					Iterator<String> it2 = hmInnerisDisplay.keySet().iterator();
					dblGross = 0.0d;
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
								dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {

								switch (nSalayHead) {
									/********** OVER TIME *************/
									case OVER_TIME :

										double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
										dblGross += dblOverTime;
										dblGrossTDS += dblOverTime;

										break;

									case BONUS :
										double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
										dblGross += dblBonusAmount;
										dblGrossTDS += dblBonusAmount;

										break;

									case EXGRATIA :
										double dblExGratiaAmount = getExGratiaAmount(con, uF, CF, strEmpId, strD1, strD2, strPC);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
										dblGross += dblExGratiaAmount;
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
										dblGrossTDS += dblArearAmount;

										break;

									case INCENTIVES :
										double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
												strFinancialYearEnd, nPayMonth, hmIncentives, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
										dblGross += dblIncentiveAmount;
										dblGrossTDS += dblIncentiveAmount;

										break;

									case REIMBURSEMENT :
										double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
										dblGross += dblReimbursementAmount;
										break;

									case TRAVEL_REIMBURSEMENT :
										double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
												hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
										dblGross += dblTravelReimbursementAmount;
										break;

									case MOBILE_REIMBURSEMENT :
										double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
												hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
										dblGross += dblMobileReimbursementAmount;
										break;

									case OTHER_REIMBURSEMENT :
										double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
												hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
										dblGross += dblOtherReimbursementAmount;
										break;

									case OTHER_EARNING :
										double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
												strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
										dblGross += dblOtherEarningAmount;
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

									default :

										if (uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
											double dblPerkAlignAmount = 0.0d;
											if (hmPerkAlignAmount.containsKey(strSalaryId)) {
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
										} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
											dblGross += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
											dblGrossPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
											dblGrossTDS += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
										}  else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
											hmTotalisDisplay.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
											dblGross += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
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
												dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
												dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											}
											
										}

										break;
								}

							}

						} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {

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

										double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay,
												hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null);
										
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
										double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd,
												hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap,null,null);
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
										double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
										dblDeduction += dblLoanAmt;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
										if (true) {
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
										} else if (alSalHeadIds==null || !alSalHeadIds.contains(strSalaryId)) {
										/*} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
												&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
												&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {*/
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
								dblGrossPT += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								dblGrossTDS += uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
								hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId))));
							} else {
								double dblMulCalAmt = getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead,strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strGrade, hmisDisplay, hmTotalisDisplay, hmAnnualVarPolicyAmount, dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
								if(!hmTotalisDisplay.containsKey(strSalaryId)) {
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
									dblGross += dblMulCalAmt;
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
										double dblPt = calculateProfessionalTax(con, uF, strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
										dblDeduction += dblPt;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
									}

									break;

								/********** TDS *************/
								case TDS :
									if (hmPaidSalaryInner != null) {
										double dblTDS = uF.parseToDouble(hmPaidSalaryInner.get(strSalaryId));
										dblDeduction += dblTDS;
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
									} else {

										double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_FLAT_TDS"));
//
										if (hmEmpServiceTaxMap.containsKey(strEmpId)) {
											dblGrossTDS = dblGross;
											
											double dblCGST = uF.parseToDouble(hmTotalisDisplay.get(CGST + ""));
											dblGrossTDS = dblGrossTDS - dblCGST;

											double dblSGST = uF.parseToDouble(hmTotalisDisplay.get(SGST + ""));
											dblGrossTDS = dblGrossTDS - dblSGST;
										}
//
										double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
												strFinancialYearEnd, strEmpId, hmEmpLevelMap, null);
										
										dblDeduction += dblTDS;

										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
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
												hmEmpStateMap.get(strEmpId), strEmpId, hmVariables,hmAnnualVariables, strD1, strD2, strPC);
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
										double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, hmAnnualVariables, strD1, strD2, strPC);
										
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

					hmTotalSalaryisDisplay.put(strEmpId, hmTotalisDisplay);
					// //=========================end code for isdisplay
					// false=======================
				}

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



	public void viewCalculatedArrearAmount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);

			Date effectiveDt = uF.getDateFormatUtil(getEffectiveDate(), DATE_FORMAT);
			Date currDt = uF.getCurrentDate(CF.getStrTimeZone());
			String empOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			paycycleList = new FillPayCycles(getStrPaycycleDuration(), request).fillPayCyclesWithEffectiveDate(CF, empOrgId, getEffectiveDate());
			String sbPCCnt = (String) request.getAttribute("sbPCCnt");
//			System.out.println("sbPCCnt ===>> " + sbPCCnt);
			String strPaycycleIds = "0";
			if(sbPCCnt.length()>1) {
				strPaycycleIds = sbPCCnt.substring(0, sbPCCnt.length()-1);
			}
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			StringBuilder sbPaidPaycycleIds = null;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select paycycle from payroll_generation where paycycle in ("+strPaycycleIds+") and emp_id=? group by paycycle order by paycycle"); //and is_paid=true 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getEmpId()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(sbPaidPaycycleIds == null) {
					sbPaidPaycycleIds = new StringBuilder();
					sbPaidPaycycleIds.append(rs.getString("paycycle"));
				} else {
					sbPaidPaycycleIds.append(","+rs.getString("paycycle"));
				}
			}
			rs.close();
			pst.close();
			
//			if(sbPaidPaycycleIds != null) {
//			System.out.println("sbPaidPaycycleIds ===>> " + sbPaidPaycycleIds.toString());
//			} else {
//				System.out.println("sbPaidPaycycleIds is NULL ");
//			}
//			
			String strCurrDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			int intCurrMonth = uF.parseToInt(uF.getDateFormat(strCurrDate, DBDATE, "MM"));
			int intCurrYear = uF.parseToInt(uF.getDateFormat(strCurrDate, DBDATE, "yyyy"));
			String strMonthMinDt = "01/"+intCurrMonth+"/"+intCurrYear;
//			System.out.println("strMonthMinDt ===>> " + strMonthMinDt);
			String strDiff = uF.dateDifference(getEffectiveDate(), DATE_FORMAT, strMonthMinDt, DATE_FORMAT);
//			System.out.println("strDiff ===>> " + strDiff);
			
			if(uF.parseToInt(strDiff) >= 29) {
//				String[] strPrevPaycycle = CF.getPrevPayCycleByOrg(con, getEffectiveDate(), CF.getStrTimeZone(), CF, empOrgId);
//				System.out.println("strPrevPaycycle ===>> " + strPrevPaycycle[0]+" - "+ strPrevPaycycle[1]+" - "+ strPrevPaycycle[2]);
//				String[] strNextPaycycle = CF.getNextPayCycleByOrg(con, getEffectiveDate(), CF.getStrTimeZone(), CF, empOrgId);
//				System.out.println("strNextPaycycle ===>> " + strNextPaycycle[0]+" - "+ strNextPaycycle[1]+" - "+ strNextPaycycle[2]);
//				String[] strPaycycle = CF.getPayCycleByOrg(con, getEffectiveDate(), CF.getStrTimeZone(), empOrgId);
//				System.out.println("strPaycycle ===>> " + strPaycycle[0]+" - "+ strPaycycle[1]+" - "+ strPaycycle[2]);
				
			}
//			
			Map<String, Map<String, String>> hmPaycyclewiseArrearAmt = new LinkedHashMap<String, Map<String,String>>();
			Map<String, Map<String, String>> hmPaycyclewiseAttendanceData = new HashMap<String, Map<String,String>>();
			StringBuilder sbSalStructureChangeMonths = null;
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			
			if(sbPaidPaycycleIds != null ) {
				List<String> alPaidPaycycleIds = Arrays.asList(sbPaidPaycycleIds.toString().split(","));
				 Collections.sort(alPaidPaycycleIds);
//				int cnt=0;
				 StringBuilder sbArrearDetailIds = null;
				 double totArrearAmt = 0.0d;
				 double totArrearDays = 0.0d;
				 double totBasicArrearAmt = 0.0d;
				 StringBuilder sbPaycycles = null;
				for(int a=0; a<alPaidPaycycleIds.size(); a++) {
					
					String[] paidPaycycle = CF.getPayCycleDatesOnPaycycleId(con, alPaidPaycycleIds.get(a), empOrgId, CF.getStrTimeZone(), CF, request);
					Map<String, Map<String, String>> hmEmpPaidSalary = getCalculatedArrearAmountPaycyclewise(con, uF, paidPaycycle[0], paidPaycycle[1], paidPaycycle[2], getEmpId());
					if(hmEmpPaidSalary == null) hmEmpPaidSalary = new HashMap<String, Map<String,String>>();
					
					Map<String, String> hmEmpPaidInner = hmEmpPaidSalary.get(getEmpId());
					if(hmEmpPaidInner == null) hmEmpPaidInner = new HashMap<String, String>();
					
//					System.out.println("paidPaycycle ===>> " + paidPaycycle[0]+" - " + paidPaycycle[1]);
					viewApprovePay(uF, paidPaycycle[0], paidPaycycle[1], paidPaycycle[2]);
					LinkedHashMap<String, Map<String, String>> hmTotalSalary = (LinkedHashMap<String, Map<String, String>>) request.getAttribute("hmTotalSalary");
					if(hmTotalSalary == null) hmTotalSalary = new LinkedHashMap<String, Map<String,String>>();
					System.out.println("hmTotalSalary ===>> " + hmTotalSalary);
					
					if(hmTotalSalary != null && hmTotalSalary.size()>0) {
						if(sbPaycycles == null) {
							sbPaycycles = new StringBuilder();
							sbPaycycles.append(alPaidPaycycleIds.get(a));
						} else {
							sbPaycycles.append(","+alPaidPaycycleIds.get(a));
						}
						
						Map<String, String> hmTotal = hmTotalSalary.get(getEmpId());
						if(hmTotal == null) hmTotal = new HashMap<String, String>();
//						System.out.println("hmTotal ===>> " + hmTotal);
//						System.out.println("hmEmpPaidInner ===>> " + hmEmpPaidInner);
						
						Map<String, Map<String, String>> hmEmp = (Map<String, Map<String, String>>) request.getAttribute("hmEmp");
						if(hmEmp == null) hmEmp = new HashMap<String, Map<String,String>>();
						Map<String, String> hmEmpAttendance = hmEmp.get(getEmpId());
						if(hmEmpAttendance == null) hmEmpAttendance = new HashMap<String, String>();
						totArrearDays += uF.parseToDouble(hmEmpAttendance.get("EMP_PAID_DAYS"));
						
						Map<String, String> hmSalaryHeadwiseArrearAmt = new HashMap<String, String>();
						Iterator<String> it = hmTotal.keySet().iterator();
						while (it.hasNext()) {
							String salHeadId = it.next();
							String salHeadAmt = hmTotal.get(salHeadId);
							double dblSalHeadArrearAmt = 0;
//							double dblSalHeadArrearAmt = (uF.parseToDouble(salHeadAmt)>0) ? uF.parseToDouble(salHeadAmt) - uF.parseToDouble(hmEmpPaidInner.get(salHeadId)) : 0;
							if(salHeadId.equals(""+LOAN)) { //salHeadId.equals(""+TDS) ||
								hmSalaryHeadwiseArrearAmt.put(salHeadId, uF.formatIntoTwoDecimalWithOutComma(dblSalHeadArrearAmt));
							} else {
								dblSalHeadArrearAmt = uF.parseToDouble(salHeadAmt) - uF.parseToDouble(hmEmpPaidInner.get(salHeadId));
								if(hmFeatureStatus != null && !uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR)) && (salHeadId.equals(""+EMPLOYEE_EPF) || salHeadId.equals(""+EMPLOYER_EPF) || salHeadId.equals(""+EMPLOYEE_ESI) || salHeadId.equals(""+EMPLOYER_ESI))){
									dblSalHeadArrearAmt = uF.parseToDouble(salHeadAmt);
								} else{
									dblSalHeadArrearAmt = uF.parseToDouble(salHeadAmt) - uF.parseToDouble(hmEmpPaidInner.get(salHeadId));
								}
								hmSalaryHeadwiseArrearAmt.put(salHeadId, uF.formatIntoTwoDecimalWithOutComma(dblSalHeadArrearAmt));
							}
							
							if(uF.parseToInt(salHeadId) == BASIC) {
								totBasicArrearAmt += dblSalHeadArrearAmt;
							}
							if(uF.parseToInt(salHeadId) > 0 && uF.parseToInt(salHeadId) != CTC && uF.parseToInt(salHeadId) != DA1 && uF.parseToInt(salHeadId) != GROSS) { //uF.parseToInt(salHeadId) != TDS &&
								if(hmEmpPaidInner.get(salHeadId+"_E_D") != null && hmEmpPaidInner.get(salHeadId+"_E_D").equals("E")) {
									totArrearAmt += dblSalHeadArrearAmt;
								} else if(hmEmpPaidInner.get(salHeadId+"_E_D") != null && hmEmpPaidInner.get(salHeadId+"_E_D").equals("D")) {
//									totArrearAmt -= dblSalHeadArrearAmt;
								}
							}
							System.out.println("CGA/2247---dblSalHeadArrearAmt=="+dblSalHeadArrearAmt+"---salHeadId=="+salHeadId);
							
							if(getCallFrom() != null && getCallFrom().equalsIgnoreCase("EMPACTIVITY")) {
								if(uF.parseToInt(salHeadId) > 0) {
									pst = con.prepareStatement("insert into arrear_headwise_details (emp_id,month,year,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, " +
										"currency_id, service_id, earning_deduction, paid_from, paid_to, present_days, paid_days, paid_leaves, total_days) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
									pst.setInt(1, uF.parseToInt(getEmpId()));
									pst.setInt(2, uF.parseToInt(uF.getDateFormat(paidPaycycle[1], DATE_FORMAT, "MM")));
									pst.setInt(3, uF.parseToInt(uF.getDateFormat(paidPaycycle[1], DATE_FORMAT, "yyyy")));
									pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(5, uF.parseToInt(salHeadId));
									pst.setDouble(6, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblSalHeadArrearAmt)));
									pst.setInt(7, uF.parseToInt(paidPaycycle[2]));
									pst.setDate(8, uF.getDateFormat(hmEmpAttendance.get("FY_START"), DATE_FORMAT));
									pst.setDate(9, uF.getDateFormat(hmEmpAttendance.get("FY_END"), DATE_FORMAT));
									pst.setInt(10, uF.parseToInt(hmEmpCurrency.get(getEmpId())));
									pst.setInt(11, uF.parseToInt(hmEmpAttendance.get("EMP_SERVICE_ID"))>0 ? uF.parseToInt(hmEmpAttendance.get("EMP_SERVICE_ID")) : 0);
									pst.setString(12, hmEmpPaidInner.get(salHeadId+"_E_D"));
									pst.setDate(13, uF.getDateFormat(paidPaycycle[0], DATE_FORMAT));
									pst.setDate(14, uF.getDateFormat(paidPaycycle[1], DATE_FORMAT));
									pst.setDouble(15, uF.parseToDouble(hmEmpAttendance.get("EMP_PRESENT_DAYS")));
									pst.setDouble(16, uF.parseToDouble(hmEmpAttendance.get("EMP_PAID_DAYS")));
									pst.setDouble(17, uF.parseToDouble(hmEmpAttendance.get("EMP_PAID_LEAVES")));
									pst.setDouble(18, uF.parseToDouble(hmEmpAttendance.get("EMP_TOTAL_DAYS")));
									pst.execute();
									pst.close();
									
									pst = con.prepareStatement("select max(arrear_headwise_id) as arrear_headwise_id from arrear_headwise_details ");
									rs = pst.executeQuery();
									while (rs.next()) {
										if(sbArrearDetailIds == null) {
											sbArrearDetailIds = new StringBuilder();
											sbArrearDetailIds.append(rs.getString("arrear_headwise_id"));
										} else {
											sbArrearDetailIds.append(","+rs.getString("arrear_headwise_id"));
										}
									}
									rs.close();
									pst.close();
								}
							}
						}
						
						/*for(int i=0; i<getSalary_head_id().length; i++) {
							System.out.println("getSalary_head_id()[i] ===>> " + getSalary_head_id()[i]);
							System.out.println("getSalary_head_value()[i] ===>> " + getSalary_head_value()[i]);
							String isDiplaySalaryHead = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
							System.out.println("isDiplaySalaryHead ===>> " + isDiplaySalaryHead);
	//						System.out.println("hmEmpPaidInner.get(getSalary_head_id()[i]) ===>> " + uF.parseToDouble(hmEmpPaidInner.get(getSalary_head_id()[i])));
	//						double dblSalHeadArrearAmt = uF.parseToDouble(getSalary_head_value()[i]) - uF.parseToDouble(hmEmpPaidInner.get(getSalary_head_id()[i]));
	//						hmSalaryHeadwiseArrearAmt.put(getSalary_head_id()[i], uF.formatIntoTwoDecimalWithOutComma(dblSalHeadArrearAmt));
						}*/
						
						hmPaycyclewiseArrearAmt.put(paidPaycycle[2], hmSalaryHeadwiseArrearAmt);
						hmPaycyclewiseAttendanceData.put(paidPaycycle[2], hmEmpAttendance);
					} else {
						int intMonth = uF.parseToInt(uF.getDateFormat(paidPaycycle[1], DATE_FORMAT, "MM"));
						if(sbSalStructureChangeMonths == null) {
							sbSalStructureChangeMonths = new StringBuilder();
							sbSalStructureChangeMonths.append(uF.getMonth(intMonth));
						} else {
							sbSalStructureChangeMonths.append(","+uF.getMonth(intMonth));
						}
					}
				}
				
				if(getCallFrom() != null && getCallFrom().equalsIgnoreCase("EMPACTIVITY")) {
					if(sbPaycycles == null) {
						sbPaycycles = new StringBuilder();
					}
					pst = con.prepareStatement("insert into arear_details (emp_id, arear_amount, effective_date, duration_months, user_id, entry_date, " +
						"total_amount_paid, arear_description, arear_amount_balance, monthly_arear, arear_code, arear_name, basic_amount, arrear_type, " +
						" paycycle_from, paycycle_to,arrear_days,paycycles) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?) "); //arrear_days, , paycycle
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setDouble(2, totArrearAmt);
					pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setInt(4, 1);
					pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDouble(7, 0);
					pst.setString(8, getStrArearName());
					pst.setDouble(9, totArrearAmt);
					pst.setDouble(10, totArrearAmt);
					pst.setString(11, getStrArearName()); 
					pst.setString(12, getStrArearName());
					pst.setDouble(13, 0); //totBasicArrearAmt
					pst.setInt(14, 2);
//					pst.setDouble(15, 0);
					pst.setDate(15, null);
					pst.setDate(16, null);
					pst.setDouble(17, totArrearDays);
					pst.setString(18, sbPaycycles.toString());
//					pst.setInt(18, 0);
//					System.out.println("pst ===>> " + pst);
					pst.executeUpdate();
					pst.close();
					
					int intArearId = 0;
					pst = con.prepareStatement("select max(arear_id) as arear_id from arear_details ");
					rs = pst.executeQuery();
					while (rs.next()) {
						intArearId = rs.getInt("arear_id");
					}
					rs.close();
					pst.close();
					
					if(intArearId > 0 && sbArrearDetailIds != null) {
						pst = con.prepareStatement("update arrear_headwise_details set arear_id= ? where arrear_headwise_id in ("+sbArrearDetailIds.toString()+")");
						pst.setInt(1, intArearId);
						pst.executeUpdate();
						pst.close();
					}
				}
			}
			if(sbSalStructureChangeMonths != null) {
				request.setAttribute("STRUTURE_CHANGED_MSG", "<b>"+sbSalStructureChangeMonths.toString()+"</b> month salary structure does not matched with current salary structure, that's why these months arrear does not calculated.");
			}
			request.setAttribute("hmPaycyclewiseArrearAmt", hmPaycyclewiseArrearAmt);
			request.setAttribute("hmPaycyclewiseAttendanceData", hmPaycyclewiseAttendanceData);
			
//			System.out.println("hmPaycyclewiseArrearAmt =====>> " + hmPaycyclewiseArrearAmt);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private Map<String, Map<String, String>> getCalculatedArrearAmountPaycyclewise(Connection con, UtilityFunctions uF, String strD1, String strD2, String strPC, String strEmpId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmEmpPaidSalary = new HashMap<String, Map<String, String>>();
		try {
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			int strEmpLevelId = CF.getEmpLevelId(strEmpId, request);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pg.salary_head_id, sum(pg.amount) as amount, pg.earning_deduction, pg.emp_id from payroll_generation pg, " +
				"salary_details sd where pg.financial_year_from_date= ? and pg.financial_year_to_date=? and pg.paycycle= ? and pg.emp_id in (" + strEmpId + ") " +
				"and pg.salary_head_id = sd.salary_head_id and sd.level_id = "+strEmpLevelId+" and sd.is_variable = false and sd.is_delete = false group by pg.salary_head_id,pg.earning_deduction, pg.emp_id order by pg.emp_id");
//			pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, earning_deduction, emp_id from payroll_generation where "
//				+ "financial_year_from_date=? and financial_year_to_date=? and paycycle=? and emp_id in (" + strEmpId + ") " +
//				"group by salary_head_id,earning_deduction, emp_id order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPC));
//			System.out.println("pst fun =====>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmEmpPaidInner = hmEmpPaidSalary.get(rs.getString("emp_id"));
				if(hmEmpPaidInner == null) hmEmpPaidInner = new HashMap<String, String>();

				hmEmpPaidInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
				hmEmpPaidInner.put(rs.getString("salary_head_id")+"_E_D", rs.getString("earning_deduction"));
				hmEmpPaidSalary.put(rs.getString("emp_id"), hmEmpPaidInner);
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpPaidSalary fun ===>> " + hmEmpPaidSalary);
			request.setAttribute("hmEmpPaidSalary", hmEmpPaidSalary);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmEmpPaidSalary;
	}
	
	
	
	private void viewApprovePay(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
			List<String> alSalHeadIds = hmFeatureUserTypeId.get(F_NON_DEDUCT_SAL_HEAD_IN_ARREAR+"_USER_IDS");
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if (strFinancialYear != null) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}

			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			int nCurrMonth = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"));
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			String strEmpOrgId = CF.getEmpOrgId(con, uF, getEmpId());
			Map<String, String> hmOrg = CF.getOrgDetails(con, uF, strEmpOrgId);
			if (hmOrg == null) hmOrg = new HashMap<String, String>();

			Date sDate = uF.getDateFormatUtil(strD1, DATE_FORMAT);
			Date eDate = uF.getDateFormatUtil(strD2, DATE_FORMAT);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id>0 "
				+ "and epd.emp_per_id = eod.emp_id and eod.emp_id in ("+getEmpId()+") and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
				+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date>=?) and epd.joining_date<=? ");
			if (getStrPaycycleDuration() != null) {
				sbQuery.append(" and eod.paycycle_duration ='" + getStrPaycycleDuration() + "'");
			}
			sbQuery.append(" and eod.emp_id in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date=? "
				+ "and paid_from=? and paid_to=? group by emp_id) order by emp_fname, emp_lname");
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
			List<Map<String, String>> alEmp = new ArrayList<Map<String, String>>();
			Map<String, Map<String, String>> hmEmp = new HashMap<String, Map<String, String>>();
			while (rs.next()) {
				Map<String, String> hmEmpPay = new HashMap<String, String>();
				hmEmpPay.put("EMP_ID", rs.getString("emp_id"));
				hmEmpPay.put("EMPCODE", rs.getString("empcode"));
				// strMiddleName = (rs.getString("emp_mname") != null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim() + " " : "";
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
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

				hmEmpPay.put("EMP_APPROVE_ATTENDANCE_ID", rs.getString("approve_attendance_id"));
				hmEmpPay.put("EMP_TOTAL_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("total_days"))));
				hmEmpPay.put("EMP_PAID_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_days"))));
				hmEmpPay.put("EMP_PRESENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("present_days"))));
				hmEmpPay.put("EMP_PAID_LEAVES", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_leaves"))));
				hmEmpPay.put("EMP_ABSENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("absent_days"))));

				if (rs.getString("service_id") != null) {
				    rs.getString("service_id").split(",");
					String tempService = rs.getString("service_id").replace(",", "");
					hmEmpPay.put("EMP_SERVICE_ID",tempService.trim());
				}
				hmEmpPay.put("EMP_PAYCYCLE", "Pay Cycle "+strPC+ ", " + uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT_STR)+" - "+uF.getDateFormat(strD2, DATE_FORMAT, DATE_FORMAT_STR));
				hmEmpPay.put("EMP_PAYCYCLE_MONTH", uF.getMonth(nPayMonth));
				hmEmpPay.put("FY_START", strFinancialYearStart);
				hmEmpPay.put("FY_END", strFinancialYearEnd);
				
				alEmp.add(hmEmpPay);
				hmEmp.put(rs.getString("emp_id"), hmEmpPay);

			}
			rs.close();
			pst.close();

//			 System.out.println("alEmp====>"+alEmp);
			
			if (alEmp.size()>0 && uF.parseToInt(getEmpId()) > 0) {
//				String strEmpIds = StringUtils.join(alEmpIds.toArray(), ",");
//				String strGradeIds = StringUtils.join(alGradeId.toArray(), ",");
				String strEmpGradeId = CF.getEmpGradeId(con, getEmpId()) ;
				
				Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
				Map<String, String> hmSalaryDetails = new HashMap<String, String>();
				List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
				List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
				List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
				List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
				
				
				pst = con.prepareStatement("select * from salary_details where level_id in (select level_id from level_details where " +
						"level_id in (select dd.level_id from designation_details dd, grades_details gd where dd.designation_id=gd.designation_id " +
						"and gd.grade_id in("+strEmpGradeId+"))) and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by level_id, earning_deduction desc, salary_head_id, weight");
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
					
					if(uF.parseToInt(rs.getString("salary_head_id")) != GROSS && uF.parseToInt(rs.getString("salary_head_id")) != CTC && uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT_CTC){
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
//				System.out.println("hmSalaryDetails ===========>> " + hmSalaryDetails);

				Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
				Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
				Map<String, String> hmEmpStateMap = new HashMap<String, String>();
				CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);

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
				int strEmpLevelId = CF.getEmpLevelId(getEmpId(), request);
				sbQuery = new StringBuilder();
				sbQuery.append("select pg.salary_head_id, sum(pg.amount) as amount, pg.earning_deduction, pg.emp_id from payroll_generation pg, " +
					"salary_details sd where pg.financial_year_from_date= ? and pg.financial_year_to_date=? and pg.paycycle= ? and pg.emp_id in (" + getEmpId() + ") " +
					"and pg.salary_head_id = sd.salary_head_id and sd.level_id = "+strEmpLevelId+" and sd.is_variable = false and sd.is_delete = false group by pg.salary_head_id,pg.earning_deduction, pg.emp_id order by pg.emp_id");
				
				pst = con.prepareStatement(sbQuery.toString());
//				pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, emp_id from payroll_generation where "
//					+ "financial_year_from_date=? and financial_year_to_date=? and paycycle=? and emp_id in ("+getEmpId()+") group by salary_head_id, emp_id order by emp_id");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPC));
//				System.out.println("pst VAPay ===>> " + pst);
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
//				System.out.println("hmEmpPaidSalary VAPay ===========>> " + hmEmpPaidSalary);
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, strEmpOrgId);
				Map<String, String> hmLoanAmt = new HashMap<String, String>();
				List<String> alLoans = new ArrayList<String>();
				Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);


				Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
				if (hmArearAmountMap == null)hmArearAmountMap = new HashMap<String, Map<String, String>>();
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
				LinkedHashMap<String, Map<String, String>> hmTotalSalaryisDisplay = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> hmInnerTemp = new HashMap<String, String>();
				
				Map<String, List<Map<String, String>>> hmEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
				Map<String, Map<String, String>> hmArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
				
				
				Map<String, String> hmCurrSalHeadsAndAmt = new HashMap<String, String>();
				List<String> alSalHeads = new ArrayList<String>();
				for(int i=0; getSalary_head_id() != null && i<getSalary_head_id().length; i++) {
					String isDiplaySalaryHead = (String) request.getParameter("isDisplay_"+getSalary_head_id()[i]);
//					System.out.println(getSalary_head_id()[i]+" ===>> " + isDiplaySalaryHead);
					alSalHeads.add(getSalary_head_id()[i]);
					if(uF.parseToBoolean(isDiplaySalaryHead)) {
						if(uF.parseToInt(getSalary_head_id()[i]) != CTC && uF.parseToInt(getSalary_head_id()[i]) != DA1 && uF.parseToInt(getSalary_head_id()[i]) != GROSS) {
							hmCurrSalHeadsAndAmt.put(getSalary_head_id()[i], uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(getSalary_head_value()[i])));
						}
					}
				}
//				System.out.println("hmCurrSalHeadsAndAmt ===>> " + hmCurrSalHeadsAndAmt);
//				System.out.println("alSalHeads ===>> " + alSalHeads);
				
				Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
				int nAlEmp = alEmp.size();
//				System.out.println("nAlEmp ===>> " + nAlEmp);
				for (int i = 0; i < nAlEmp; i++) {
					Map<String, String> hmEmpPay = alEmp.get(i);
					String strEmpId = hmEmpPay.get("EMP_ID");
					String strOrgId = hmEmpOrgId.get(strEmpId);
					int nEmpId = uF.parseToInt(strEmpId);
					String strLevel = hmEmpLevelMap.get(strEmpId);
					int nEmpOrgId = uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId));
					String strEmpGender = CF.getEmpGender(con, uF, strEmpId);

					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strLevel);
					if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
					
					int nTotalNumberOfDaysForCalc = (int) uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"));
					double dblTotalPresentDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"));

					Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();

//					System.out.println("call getSalaryCalculation .....");
					hmInner = getSalaryCalculation(con, hmInnerisDisplay, nEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strLevel, uF, CF, strD2, hmSalInner, strEmpOrgId, hmCurrSalHeadsAndAmt);
//					System.out.println("hmInner ===========>> " + hmInner);
					
					if(hmInner != null && hmInner.size()>0) {
						Map<String, String> hmPaidSalaryInner = hmEmpPaidSalary.get(strEmpId);
	
						Map<String, String> hmPerkAlignAmount = new HashMap<String, String>();
						CF.getPerkAlignAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel),hmPerkAlignAmount);
	
						double dblReimbursementCTC = CF.getReimbursementCTCHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
						double dblReimbursementCTCOptional = CF.getReimbursementCTCOptinalHeadTotalAmount(con, uF, nEmpId, strFinancialYearStart, strFinancialYearEnd, strD1, strD2, strPC, nEmpOrgId, uF.parseToInt(strLevel));
						
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
							
							if(!alEmpSalaryDetailsEarning.contains(""+CGST)){
								alEmpSalaryDetailsEarning.add(""+CGST);
								hmSalaryDetails.put(""+CGST, "CGST");
							}
	
							hmInnerTemp = new HashMap<String, String>();
							hmInnerTemp.put("AMOUNT", "0");
							hmInnerTemp.put("EARNING_DEDUCTION", "E");
							hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
							hmInner.put(SGST + "", hmInnerTemp);
							
							if(!alEmpSalaryDetailsEarning.contains(""+SGST)){
								alEmpSalaryDetailsEarning.add(""+SGST);
								hmSalaryDetails.put(""+SGST, "SGST");
							}
						}
	
						if (hmInner.size() > 0 && hmInner.containsKey(TDS + "")) {
//							System.out.println("in TDS 1 ===>> ");
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
						double dblGross = 0;
						double dblGrossTDS = 0;
						double dblDeduction = 0;
						// double dblPerkTDS = 0.0d;
						Set<String> setContriSalHead = new HashSet<String>();
						
						while (it.hasNext()) {
							String strSalaryId = it.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
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
	
								if (hm.get("AMOUNT") != null) {
									dblGross += uF.parseToDouble(hm.get("AMOUNT"));
									dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
									dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
								} else {
	
									switch (nSalayHead) {
										case OVER_TIME :
											double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
											dblGross += dblOverTime;
											dblGrossTDS += dblOverTime;
											break;
	
										case LEAVE_ENCASHMENT :
											double leaveEncashmentAmt = 0.0d;//getLeaveEncashmentAmtDetails(con, uF, CF, strEmpId, 0.0d, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, dblIncrementBasic, dblIncrementDA);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),leaveEncashmentAmt));
	
											dblGross += leaveEncashmentAmt;
											dblGrossTDS += leaveEncashmentAmt;
											break;
	
										case BONUS :
											double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
											dblGross += dblBonusAmount;
											dblGrossTDS += dblBonusAmount;
											break;
	
										case EXGRATIA :
											double dblExGratiaAmount = getExGratiaAmount(con, uF, CF, strEmpId, strD1, strD2, strPC);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
											dblGross += dblExGratiaAmount;
											dblGrossTDS += dblExGratiaAmount;
											break;
	
										case AREARS :
											double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()){
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear){
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblArearAmount += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get("GROSS"))));
												}	
											}
											
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
											dblGross += dblArearAmount;
											dblGrossTDS += dblArearAmount;
											break;
	
										case INCENTIVES :
											double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmIncentives, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
											dblGross += dblIncentiveAmount;
											dblGrossTDS += dblIncentiveAmount;
											break;
	
										case REIMBURSEMENT :
											double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
											dblGross += dblReimbursementAmount;
											break;
	
										case TRAVEL_REIMBURSEMENT :
											double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
											dblGross += dblTravelReimbursementAmount;
											break;
	
										case MOBILE_REIMBURSEMENT :
											double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
											dblGross += dblMobileReimbursementAmount;
											break;
	
										case OTHER_REIMBURSEMENT :
											double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
											dblGross += dblOtherReimbursementAmount;
											break;
	
										case OTHER_EARNING :
											double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
											dblGross += dblOtherEarningAmount;
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
	
										default :
	
											if (uF.parseToBoolean(hm.get("IS_ALIGN_WITH_PERK"))) {
												double dblPerkAlignAmount = 0.0d;
												if (hmPerkAlignAmount.containsKey(strSalaryId)) {
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
												if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)){
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
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
												hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
												dblGross += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											} else if (uF.parseToInt(strSalaryId) != GROSS) {
												boolean isMultipePerWithParticularHead = false;
												if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")){
													isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
												}
												if(!isMultipePerWithParticularHead){
													hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
													dblGross += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
													dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
												}
											}
											break;
									}
	
								}
	
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
								/**
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */
	
								switch (nSalayHead) {
	
									/********** EPF EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_EPF :
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hm.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
	
											Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");
	
//											double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null);
											double dblEEPF = 0;
											if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
												dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null);
											}
//											System.out.println("CGA/3133--dblEEPF=="+dblEEPF);
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
//											System.out.println("CGA/3146--dblEEPF=="+dblEEPF);
											dblDeduction += dblEEPF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
										}
										break;
	
									/********** EPF EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_EPF :
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hm.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
//											double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
											double dblERPF = 0;
											if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
												dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
											}
											dblDeduction += dblERPF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
										}
										break;
	
									case LOAN :
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblLoan = uF.parseToDouble(hm.get("AMOUNT"));
											dblDeduction += dblLoan;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
											CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, strEmpId);
										} else {
											double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
											dblDeduction += dblLoanAmt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
											if (true) {
												dblGrossTDS = dblGrossTDS - dblLoanAmt;
											}
										}
										break;
	
									case MOBILE_RECOVERY :
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblLoan = uF.parseToDouble(hm.get("AMOUNT"));
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
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
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
//											} else if (alSalHeadIds==null || !alSalHeadIds.contains(strSalaryId)) {
											} else if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
													&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
												hmTotal.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
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
//							double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
							double dblERPF = 0;
							if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
								dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, hmArearAmountMap,null,null);
							}
							hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
						}
						if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
//							double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
							double dblESI = 0;
							if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
								dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
							}
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
							if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E") && hm.get("SALARY_AMOUNT_TYPE") != null
									&& hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
								if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
									dblGross += uF.parseToDouble(hm.get("AMOUNT"));
									dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
									dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
									if(!hmTotal.containsKey(strSalaryId)){
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
										dblGross += dblMulCalAmt;
										dblGrossTDS += dblMulCalAmt;
									}
								}
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")
									&& (hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
								if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
									dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));
									hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hm, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables, dblReimbursementCTCOptional, hmContriSalHeadAmt);
									if(!hmTotal.containsKey(strSalaryId)){
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
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */
	
								switch (nSalayHead) {
									/********** TAX *************/
									case PROFESSIONAL_TAX :
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hm.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											/**
											 * KP Condition
											 * 
											 * */
											double dblPt = calculateProfessionalTax(con, uF, strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										}
										break;
	
									/********** TDS *************/
									case TDS :
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblTDS = uF.parseToDouble(hm.get("AMOUNT"));
											dblDeduction += dblTDS;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
										} else {
											double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_FLAT_TDS"));
											if (hmEmpServiceTaxMap.containsKey(strEmpId)) {
												dblGrossTDS = dblGross;
												
												double dblCGST = uF.parseToDouble(hmTotal.get(CGST + ""));
												dblGrossTDS = dblGrossTDS - dblCGST;
	
												double dblSGST = uF.parseToDouble(hmTotal.get(SGST + ""));
												dblGrossTDS = dblGrossTDS - dblSGST;
											}
//											System.out.println("dblGross ===>> " + dblGross + " -- dblGrossTDS ===>> " + dblGrossTDS+" --- hmCurrSalHeadsAndAmt ===>> " + hmCurrSalHeadsAndAmt);
											double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nCurrMonth, strFinancialYearStart,
													strFinancialYearEnd, strEmpId, hmEmpLevelMap, hmCurrSalHeadsAndAmt);
											System.out.println("hmTotal dblTDS ===>> " + dblTDS);
											dblDeduction += dblTDS;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
										}
										break;
										
										/********** ESI EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_ESI :
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hm.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
//											double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
											double dblESI = 0;
											if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
												dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId,hmVariables,hmAnnualVariables, strD1, strD2, strPC);
											}
											
											dblESI = Math.ceil(dblESI);
											
											dblDeduction += dblESI;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
										}
										break;
									/********** /ESI EMPLOYER CONTRIBUTION *************/
	
									/********** ESI EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_ESI :
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hm.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
//											double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, hmAnnualVariables, strD1, strD2, strPC);
											double dblESI = 0;
											if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
												dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, hmAnnualVariables, strD1, strD2, strPC);
											}
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()){
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear){
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblESI += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_ESI))));
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
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblLWF = uF.parseToDouble(hm.get("AMOUNT"));
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
										if (uF.parseToDouble(hm.get("AMOUNT")) > 0) {
											double dblLWF = uF.parseToDouble(hm.get("AMOUNT"));
											dblDeduction += dblLWF;
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										} else {
											double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, hmAnnualVariables, strOrgId);
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()){
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear){
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblLWF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_LWF))));
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
							hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
						/**
						 * Other cal end
						 * */
	
						String strCurrencyId = hmEmpCurrency.get(strEmpId);
						Map<String, String> hmCurrency = hmCurrencyDetails.get(strCurrencyId);
						if (hmCurrency == null)
							hmCurrency = new HashMap<String, String>();
	
						hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction)));
						hmTotalSalary.put(strEmpId, hmTotal);
//						System.out.println(" in aprove pay hmTotalSalary ====>>> " + hmTotalSalary);
						
						/**
						 * code for CTC Variable isdisplay true 
						 * */
						Map<String, String> hmTotalisDisplay = new HashMap<String, String>();
						Iterator<String> it2 = hmInnerisDisplay.keySet().iterator();
						dblGross = 0.0d;
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
								if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
									dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
									dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
									dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
								} else {
	
									switch (nSalayHead) {
										/********** OVER TIME *************/
										case OVER_TIME :
											double dblOverTime = uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOverTime));
											dblGross += dblOverTime;
											dblGrossTDS += dblOverTime;
											break;
	
										case BONUS :
											double dblBonusAmount = uF.parseToDouble(hmIndividualBonus.get(strEmpId));
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblBonusAmount));
											dblGross += dblBonusAmount;
											dblGrossTDS += dblBonusAmount;
											break;
	
										case EXGRATIA :
											double dblExGratiaAmount = getExGratiaAmount(con, uF, CF, strEmpId, strD1, strD2, strPC);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblExGratiaAmount));
											dblGross += dblExGratiaAmount;
											dblGrossTDS += dblExGratiaAmount;
											break;
	
										case AREARS :
											double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()){
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear){
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblArearAmount += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get("GROSS"))));
												}	
											}
											
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblArearAmount));
											dblGross += dblArearAmount;
											dblGrossTDS += dblArearAmount;
											break;
	
										case INCENTIVES :
											double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart,
													strFinancialYearEnd, nPayMonth, hmIncentives, CF);
											hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblIncentiveAmount));
											dblGross += dblIncentiveAmount;
											dblGrossTDS += dblIncentiveAmount;
											break;
	
										case REIMBURSEMENT :
											double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReimbursementAmount));
											dblGross += dblReimbursementAmount;
											break;
	
										case TRAVEL_REIMBURSEMENT :
											double dblTravelReimbursementAmount = getTravelReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTravelReimbursementAmount));
											dblGross += dblTravelReimbursementAmount;
											break;
	
										case MOBILE_REIMBURSEMENT :
											double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMobileReimbursementAmount));
											dblGross += dblMobileReimbursementAmount;
											break;
	
										case OTHER_REIMBURSEMENT :
											double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap,
													hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherReimbursementAmount));
											dblGross += dblOtherReimbursementAmount;
											break;
	
										case OTHER_EARNING :
											double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotalisDisplay,
													strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblOtherEarningAmount));
											dblGross += dblOtherEarningAmount;
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
	
										default :
	
											if (uF.parseToBoolean(hmisDisplay.get("IS_ALIGN_WITH_PERK"))) {
												double dblPerkAlignAmount = 0.0d;
												if (hmPerkAlignAmount.containsKey(strSalaryId)) {
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
												if(hmAllowance.containsKey(strEmpId + "_" + strSalaryId)){
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
											} else if (hmAnnualVariables.containsKey(strEmpId + "_" + strSalaryId)) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId))));
												dblGross += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossPT += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId));
												dblGrossTDS += uF.parseToDouble(hmAnnualVariables.get(strEmpId + "_" + strSalaryId ));
											} else if (hmVariables.containsKey(strEmpId + "_" + strSalaryId + "_E")) {
												hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"))));
												dblGross += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossPT += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
												dblGrossTDS += uF.parseToDouble(hmVariables.get(strEmpId + "_" + strSalaryId + "_E"));
											} else if (uF.parseToInt(strSalaryId) != GROSS) {
												boolean isMultipePerWithParticularHead = false;
												if(hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")){
													isMultipePerWithParticularHead = checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional);
												}
												if(!isMultipePerWithParticularHead){
													hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmisDisplay.get("AMOUNT"))) + "");
													dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
													dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
													dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));;
												}
											}
											break;
									}
	
								}  
	
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
	
								/**
								 * TAX CALCULATION STARTS HERE
								 * 
								 * */
	
								switch (nSalayHead) {
									/********** EPF EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_EPF :
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											Map<String, String> hmVoluntaryPF = hmInnerisDisplay.get(VOLUNTARY_EPF + "");
	
//											double dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null);
											double dblEEPF = 0;
											if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
												dblEEPF = calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, hmVoluntaryPF, strEmpId, null, null, false, hmArearAmountMap,null,null);
											}
//											System.out.println("CGA/3738--dblEEPF=="+dblEEPF);
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()){
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear){
													int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
													
													Map<String, String> hmArrearTotal = hmArrearCalSalary.get(strEmpId+"_"+nArrearPaycycle);
													if(hmArrearTotal == null) hmArrearTotal = new LinkedHashMap<String, String>();
													
													dblEEPF += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmArrearTotal.get(""+EMPLOYEE_EPF))));
												}	
											}
//											System.out.println("CGA/3751--dblEEPF=="+dblEEPF);
											dblDeduction += dblEEPF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
										}
	
										break;
	
									/********** EPF EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_EPF :
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
//											double dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap,null,null);
											double dblERPF = 0;
											if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
												dblERPF = calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, null, null, false, hmArearAmountMap,null,null);
											}
											
											dblDeduction += dblERPF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblERPF));
										}
	
										break;
	
									case LOAN :
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblLoan = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblDeduction += dblLoan;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoan));
											CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2, strEmpId);
										} else {
											double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotalisDisplay, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
											dblDeduction += dblLoanAmt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLoanAmt));
											if (true) {
												dblGrossTDS = dblGrossTDS - dblLoanAmt;
											}
										}
	
										break;
	
									case MOBILE_RECOVERY :
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblLoan = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
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
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
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
//											} else if (alSalHeadIds==null || !alSalHeadIds.contains(strSalaryId)) {
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
	
							hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
							hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
	
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
								if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
									dblGross += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
									dblGrossPT += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
									dblGrossTDS += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional, hmContriSalHeadAmt);
									if(!hmTotalisDisplay.containsKey(strSalaryId)) {
										hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
										dblGross += dblMulCalAmt;
										dblGrossTDS += dblMulCalAmt;
									}
								}
							} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")
									&& (hmisDisplay.get("SALARY_AMOUNT_TYPE") != null && hmisDisplay.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
								if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
									dblDeduction += uF.parseToDouble(hmisDisplay.get("AMOUNT"));
									hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmisDisplay.get("AMOUNT"))));
								} else {
									double dblMulCalAmt = getMultipleCalAmtDetails(con, uF, CF, nSalayHead, strEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, strD1, strD2, strPC, strLevel, hmisDisplay, hmTotal, hmAnnualVarPolicyAmount,dblReimbursementCTC, hmAllowance, hmVariables,dblReimbursementCTCOptional, hmContriSalHeadAmt);
									if(!hmTotalisDisplay.containsKey(strSalaryId)){
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
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
											double dblPt = calculateProfessionalTax(con, uF, strD2, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(strEmpId), strEmpGender);
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										}
	
										break;
	
									/********** TDS *************/
									case TDS :
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblTDS = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblDeduction += dblTDS;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
										} else {
	
											double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(strEmpId) + "_FLAT_TDS"));
	//
											if (hmEmpServiceTaxMap.containsKey(strEmpId)) {
												dblGrossTDS = dblGross;
												
												double dblCGST = uF.parseToDouble(hmTotalisDisplay.get(CGST + ""));
												dblGrossTDS = dblGrossTDS - dblCGST;
	
												double dblSGST = uF.parseToDouble(hmTotalisDisplay.get(SGST + ""));
												dblGrossTDS = dblGrossTDS - dblSGST;
												
											}
	
											double dblTDS = calculateTDS(con, CF, uF, dblGrossTDS, dblFlatTDS, nPayMonth, strFinancialYearStart,
													strFinancialYearEnd, strEmpId, hmEmpLevelMap, hmCurrSalHeadsAndAmt);
//											System.out.println("hmTotalisDisplay dblTDS ===>> " + dblTDS);
											dblDeduction += dblTDS;
	
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTDS));
										}
										break;
										
										/********** ESI EMPLOYER CONTRIBUTION *************/
									case EMPLOYER_ESI :
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
//											double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables, hmAnnualVariables, strD1, strD2, strPC);
											double dblESI = 0;
											if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
												dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, hmVariables, hmAnnualVariables, strD1, strD2, strPC);
											}
											dblESI = Math.ceil(dblESI);
											
											dblDeduction += dblESI;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
										}
	
										break;
									/********** /ESI EMPLOYER CONTRIBUTION *************/
	
									/********** ESI EMPLOYEE CONTRIBUTION *************/
									case EMPLOYEE_ESI :
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblPt = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblDeduction += dblPt;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
										} else {
//											double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, hmAnnualVariables, strD1, strD2, strPC);
											double dblESI = 0;
											if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
												dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, hmAnnualVariables, strD1, strD2, strPC);
											}
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()){
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear){
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
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblLWF = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
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
										if (uF.parseToDouble(hmisDisplay.get("AMOUNT")) > 0) {
											double dblLWF = uF.parseToDouble(hmisDisplay.get("AMOUNT"));
											dblDeduction += dblLWF;
											hmTotalisDisplay.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
										} else {
											double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, hmAnnualVariables, strOrgId);
											
											if(hmEmpArrear !=null && !hmEmpArrear.isEmpty()){
												List<Map<String, String>> alArrear = hmEmpArrear.get(strEmpId);
												if(alArrear == null) alArrear = new ArrayList<Map<String,String>>();
												for(Map<String,String> hmApplyArear : alArrear){
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
	
									/********** LWF EMPLOYEE CONTRIBUTION *************/
	
								}
							}
	
							hmTotalisDisplay.put("SALARY_HEAD_ID", strSalaryId);
							hmTotalisDisplay.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
						}
						/**
						 * Other cal isDisplay end
						 * */
	
						hmTotalSalaryisDisplay.put(strEmpId, hmTotalisDisplay);
						// //=========================end code for isdisplay
						// false=======================
//						System.out.println("hmTotalSalary ===================>> " + hmTotalSalary);
//						System.out.println("dblGross ===================>> " + dblGross);
					}
				}

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

//				 System.out.println("hmTotalSalaryisDisplay===>"+hmTotalSalaryisDisplay);

//				session.setAttribute("AP_alEmp", alEmp);
//				session.setAttribute("AP_hmEmp", hmEmp);
//				session.setAttribute("AP_hmSalaryDetails", hmSalaryDetails);
////				session.setAttribute("AP_hmEmpSalary", hmEmpSalary);
//				session.setAttribute("AP_alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
//				session.setAttribute("AP_alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
//				session.setAttribute("AP_hmLoanAmt", hmLoanAmt);
//				session.setAttribute("AP_hmLoanPoliciesMap", hmLoanPoliciesMap);
//				session.setAttribute("AP_hmTotalSalary", hmTotalSalary);
//				session.setAttribute("AP_hmTotalSalaryisDisplay", hmTotalSalaryisDisplay);
//
//				session.setAttribute("AP_strD1", strD1);
//				session.setAttribute("AP_strD2", strD2);
//				session.setAttribute("AP_strPC", strPC);
////				session.setAttribute("AP_f_org", getF_org());
//				session.setAttribute("AP_strPaycycleDuration", getStrPaycycleDuration());
//
//				session.setAttribute("AP_hmEmpStateMap", hmEmpStateMap);
//				session.setAttribute("AP_hmEmpLevelMap", hmEmpLevelMap);
//				session.setAttribute("AP_hmVariables", hmVariables);
//				session.setAttribute("AP_hmAnnualVariables", hmAnnualVariables);
//				session.setAttribute("AP_hmOtherTaxDetails", hmOtherTaxDetails);
//				session.setAttribute("AP_hmArearAmountMap", hmArearAmountMap);
//				
//				session.setAttribute("AP_hmEmpArrear", hmEmpArrear); 
//				session.setAttribute("AP_hmArrearCalSalary", hmArrearCalSalary); 
//				session.setAttribute("AP_hmArrearEarningHead", hmArrearEarningHead); 
//				session.setAttribute("AP_hmArrearDeductionHead", hmArrearDeductionHead); 
//				session.setAttribute("AP_hmArrearEmployeePF", hmArrearEmployeePF); 
//				session.setAttribute("AP_hmArrearEmployerPF", hmArrearEmployerPF); 
//				session.setAttribute("AP_hmArrearEmployerESI", hmArrearEmployerESI); 
//				session.setAttribute("AP_hmArrearEmployeeLWF", hmArrearEmployeeLWF); 
				
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
	
	
	public Map<String, Map<String, String>> getSalaryCalculation(Connection con, Map<String, Map<String, String>> hmInnerisDisplay, int nEmpId, double dblPresent, int nTotalNumberOfDays, 
		String strLevelId, UtilityFunctions uF, CommonFunctions CF, String strD2, Map<String, Map<String, String>> hmSalaryDetails, String strEmpOrgId, Map<String, String> hmCurrSalHeadsAndAmt) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmSalaryHeadReCalculatedMap = new LinkedHashMap<String, Map<String, String>>();
		try {
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
//				"and isdisplay=true and effective_date<=? group by level_id");
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
				"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
				"and isdisplay=true and effective_date<=?) group by level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst ================>> " + pst);
			String strEffectiveDate = null;
			String strEmpLevelId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strEffectiveDate = rs.getString("effective_date");
				strEmpLevelId = rs.getString("level_id");
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT MAX(effective_date) as effective_date, level_id FROM emp_salary_details WHERE emp_id=? and is_approved=true and " +
				"isdisplay=true and effective_date = (SELECT MAX(effective_date) as effective_date FROM emp_salary_details WHERE emp_id=? and is_approved=true " +
				"and isdisplay=true and effective_date<=?) group by level_id"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
	//		System.out.println("pst ================>> " + pst);
			String strCurrEffectiveDate = null;
			String strEmpCurrLevelId = null;
			rs = pst.executeQuery();
			while(rs.next()){
				strCurrEffectiveDate = rs.getString("effective_date");
				strEmpCurrLevelId = rs.getString("level_id");
			}
			rs.close();
			pst.close();
//			System.out.println("strCurrEffectiveDate ===>> " + strCurrEffectiveDate + " -- strEffectiveDate ===>> " + strEffectiveDate);
//			System.out.println("strEmpLevelId ===>> " + strEmpLevelId + " -- strEmpCurrLevelId ===>> " + strEmpCurrLevelId);
			
			Date dtDate = uF.getDateFormatUtil(strEffectiveDate, DBDATE);
			Date dtCurrDate = uF.getDateFormatUtil(strCurrEffectiveDate, DBDATE);
			
			if(dtDate.equals(dtCurrDate) && uF.parseToInt(strEmpLevelId) == uF.parseToInt(strEmpCurrLevelId)) {
				sbQuery = new StringBuilder();
				sbQuery.append("select * from (select *, 1 as aa from emp_salary_details where emp_id=? and effective_date=(select max(effective_date) from emp_salary_details " +
					"where emp_id=? and effective_date<=? and is_approved=true) and salary_head_id not in ("+CTC+","+TDS+","+DA1+","+GROSS+") and is_approved=true " +
					"and salary_head_id in (select salary_head_id from salary_details where (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) and org_id in (select org_id from " +
					"employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.emp_id=?) and level_id=?) order by earning_deduction desc, " +
					"salary_head_id, emp_salary_id) ac " +
					" union " +
					"(select *, 2 as aa from emp_salary_details where emp_id=? and effective_date=(select max(effective_date) from emp_salary_details where emp_id=? " +
					"and effective_date<=? and is_approved=true) and salary_head_id in ("+TDS+") and salary_head_id not in ("+CTC+") and salary_head_id in " +
					"(select salary_head_id from salary_details where (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) and org_id in (select org_id from employee_personal_details epd, " +
					"employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.emp_id=?) and level_id=?) order by earning_deduction desc, salary_head_id, emp_salary_id) " +
					"order by aa, earning_deduction desc, salary_head_id, emp_salary_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, nEmpId);
				pst.setInt(2, nEmpId);
				pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(4, nEmpId);
				pst.setInt(5, uF.parseToInt(strEmpLevelId));
				pst.setInt(6, nEmpId);
				pst.setInt(7, nEmpId);
				pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
				pst.setInt(9, nEmpId);
				pst.setInt(10, uF.parseToInt(strEmpLevelId));
//				System.out.println("------------- pst ========>> " + pst);
				rs = pst.executeQuery();
				int salHeadCnt = 0;
				int salHeadPaidButNotExistCnt = 0;
				StringBuilder sbSalHeads = new StringBuilder();
				StringBuilder sbSalHeadsIsDisplay = new StringBuilder();
				while (rs.next()) {
					String strSalaryHeadId = rs.getString("salary_head_id");
					sbSalHeads.append(strSalaryHeadId+",");
					if(hmCurrSalHeadsAndAmt.containsKey(strSalaryHeadId) && rs.getBoolean("isdisplay")) {
						salHeadCnt++;
						sbSalHeadsIsDisplay.append(strSalaryHeadId+",");
					} else if(!hmCurrSalHeadsAndAmt.containsKey(strSalaryHeadId) && rs.getBoolean("isdisplay")) {
						salHeadPaidButNotExistCnt++;
					}
	//				String strAmount = rs.getString("amount");
					String strAmount = hmCurrSalHeadsAndAmt.get(strSalaryHeadId);
					double dblAmount = uF.parseToDouble(strAmount);
	
					Map<String, String> hmInnerSal = hmSalaryDetails.get(strSalaryHeadId);
					if (hmInnerSal == null) hmInnerSal = new HashMap<String, String>();
	
					String strSalPercentage = hmInnerSal.get("SALARY_HEAD_AMOUNT");
					String strSalAmountType = hmInnerSal.get("SALARY_AMOUNT_TYPE");
					String isCTCVariable = hmInnerSal.get("IS_CTC_VARIABLE");
					String strMultipleCalculation = hmInnerSal.get("MULTIPLE_CALCULATION");
					String isAlignWithPerk = hmInnerSal.get("IS_ALIGN_WITH_PERK");
					String isDefaultCalAllowance = hmInnerSal.get("IS_DEFAULT_CAL_ALLOWANCE");
	
					String strEarningDeduction = hmInnerSal.get("EARNING_DEDUCTION");
					String strSalaryType = hmInnerSal.get("SALARY_TYPE");
	
					if (strSalaryType != null && strSalaryType.equalsIgnoreCase("F")) {
						// dblAmount = dblAmount;
					} else if (strSalaryType != null && strSalaryType.equalsIgnoreCase("D")) {
						dblAmount = dblAmount * dblPresent;
					} else {
						dblAmount = dblAmount * (dblPresent / nTotalNumberOfDays);
					}
	
					if (strEarningDeduction != null) {
						Map<String, String> hmSalaryInner = new HashMap<String, String>();
						hmSalaryInner.put("EARNING_DEDUCTION", strEarningDeduction);
						hmSalaryInner.put("AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblAmount));
						hmSalaryInner.put("SALARY_AMOUNT_TYPE", strSalAmountType);
						hmSalaryInner.put("MULTIPLE_CALCULATION", strMultipleCalculation);
						hmSalaryInner.put("IS_ALIGN_WITH_PERK", isAlignWithPerk);
						hmSalaryInner.put("SALARY_PERCENTAGE", strSalPercentage);
						hmSalaryInner.put("IS_DEFAULT_CAL_ALLOWANCE", isDefaultCalAllowance);
	
						if (rs.getBoolean("isdisplay") && uF.parseToBoolean(isCTCVariable)) {
							hmInnerisDisplay.put(strSalaryHeadId, hmSalaryInner);
						} else if (rs.getBoolean("isdisplay") && !uF.parseToBoolean(isCTCVariable)) {
							hmSalaryHeadReCalculatedMap.put(strSalaryHeadId, hmSalaryInner);
						}
					}
				}
				rs.close();
				pst.close();
//				System.out.println(strD2 + " -- sbSalHeads ===>> " + sbSalHeads);
//				System.out.println(strD2 + " -- sbSalHeadsIsDisplay ===>> " + sbSalHeadsIsDisplay);
//				System.out.println("hmCurrSalHeadsAndAmt.size() ===>> " + hmCurrSalHeadsAndAmt.size() + " -- salHeadCnt ===>> " + salHeadCnt+" -- salHeadPaidButNotExistCnt ===>> " + salHeadPaidButNotExistCnt);
				if(hmCurrSalHeadsAndAmt.size() == salHeadCnt && salHeadPaidButNotExistCnt == 0) {
					return hmSalaryHeadReCalculatedMap;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
//		return hmSalaryHeadReCalculatedMap;
		return null;
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
		String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmCurrSalHeadsAndAmt) {
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

//			System.out.println("dblGross ===>> " + dblGross + " -- dblFlatTDS ===>> " + dblFlatTDS +" -- hmCurrSalHeadsAndAmt ===>> " + hmCurrSalHeadsAndAmt); 
			if (uF.parseToBoolean(hmEmpLevelMap.get(strEmpId + "_FLAT_TDS_DEDEC"))) {
				dblTDSMonth = dblGross * dblFlatTDS / 100;
//				System.out.println("dblTDSMonth ===>> " + dblTDSMonth);
			} else {
				if(CF.getIsTDSAutoApprove()) {
//					ViewEmpTDSProjection empTDSProjection = new ViewEmpTDSProjection();
//					empTDSProjection.request = request;
//					empTDSProjection.session = session;
//					empTDSProjection.CF = CF;
//					empTDSProjection.setStrEmpId(strEmpId);
//					empTDSProjection.setStrFinancialYearStart(strFinancialYearStart);
//					empTDSProjection.setStrFinancialYearEnd(strFinancialYearEnd);
//					empTDSProjection.getEmpTDSProjection(uF,strFinancialYearStart, strFinancialYearEnd, strEmpId, hmCurrSalHeadsAndAmt);
					getEmpTDSProjection(uF,strFinancialYearStart, strFinancialYearEnd, strEmpId, hmCurrSalHeadsAndAmt);
//					empTDSProjection.setHmCurrSalHeadsAndAmt(hmCurrSalHeadsAndAmt);
					
					Map<String, String> hmTDSRemainMonth = (Map<String, String>)request.getAttribute("hmTDSRemainMonth");
					if(hmTDSRemainMonth == null) hmTDSRemainMonth = new LinkedHashMap<String, String>();
					
					String strMonth = uF.getMonth(nPayMonth);
//					System.out.println("strMonth==>"+strMonth+"--hmTDSRemainMonth==>"+hmTDSRemainMonth);
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
		
					pst = con.prepareStatement("select * from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) "
							+ "from emp_salary_details where emp_id =? and effective_date <= ? and is_approved=true and level_id = ?) and salary_head_id in (" + CTC + ") "
							+ "and salary_head_id in (select salary_head_id from salary_details where (is_delete is null or is_delete=false) "
							+ "and org_id in (select org_id from employee_personal_details epd, employee_official_details eod "
							+ "where epd.emp_per_id=eod.emp_id and eod.emp_id=?) and level_id = ?) and level_id = ?");
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
			List<String> alLoans) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		double dblTotalCalculatedAmount = 0;

		try {

			pst = con.prepareStatement(selectLoanPayroll2);
			pst.setInt(1, uF.parseToInt(strEmpId));
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

					hmEmpLoanInner = hmEmpLoan.get(rs.getString("emp_id"));
					if (hmEmpLoanInner == null)
						hmEmpLoanInner = new HashMap<String, String>();
					hmEmpLoanInner.put(rs.getString("loan_id"), uF.formatIntoTwoDecimal(dblTotalCalculatedAmount));
					hmEmpLoan.put(rs.getString("emp_id"), hmEmpLoanInner);

					if (!alLoans.contains(rs.getString("loan_id"))) {
						alLoans.add(rs.getString("loan_id"));
					}
				}
			}
			rs.close();
			pst.close();
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
					dblCalculatedAmount = uF.parseToDouble(rs.getString("eelfw_contribution"));
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
			pst.setInt(3, uF.parseToInt(strOrgId));
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
			Map<String, String> hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap,
			Map<String, String> hmVariables, boolean isInsert,Map<String, String> hmAnnualVariables,Map<String, List<Map<String, String>>> hmEmpArrear,
			Map<String, Map<String, String>> hmArrearEmployeeLWF, String strOrgId) {
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
			Map<String, String> hmAnnualVariables, String strD1, String strD2, String strPC) {
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
//			if(uF.parseToInt(strEmpId) == 322) {
//				System.out.println("pst==>"+pst);
//			}
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
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				if (alEligibleSalaryHeads.contains(arrSalaryHeads[i].trim())) {
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
					if(uF.parseToInt(strEmpId) == 322) {
//						System.out.println("arrSalaryHeads[i].trim()==>"+arrSalaryHeads[i].trim()+"--hmtotal==>"+hmTotal.get(arrSalaryHeads[i].trim()));
					}
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim())); 
			}
//			if(uF.parseToInt(strEmpId) == 322) {
//				System.out.println("hmTotal==>"+hmTotal);
//				System.out.println("dblAmount==>"+dblAmount);
//				System.out.println("dblAmountEligibility==>"+dblAmountEligibility);
//				System.out.println("dblESIMaxAmount==>"+dblESIMaxAmount);
//				System.out.println("dblEEESIAmount==>"+dblEEESIAmount);
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
//					if(uF.parseToInt(strEmpId) == 322) {
//						System.out.println("pst==>"+pst);
//					}
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
//					if(uF.parseToInt(strEmpId) == 322) {
//						System.out.println("pst==>"+pst);
//					}
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
//					if(uF.parseToInt(strEmpId) == 322) {
//						System.out.println("pst==>"+pst);
//					}
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
//					if(uF.parseToInt(strEmpId) == 322) {
//						System.out.println("pst==>"+pst);
//					}
					rs = pst.executeQuery();
					if(rs.next()) {
						attemptFlag = true;
					}
					rs.close();
					pst.close();					
				}
			}
//			if(uF.parseToInt(strEmpId) == 322) {
//				System.out.println("attemptFlag==>"+attemptFlag+"--deductFlag==>"+deductFlag);
//			}
			/*if(attemptFlag && deductFlag) { 
				dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
			} else if(attemptFlag && !deductFlag) {
				dblCalculatedAmount = 0;
			} else {
				if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
					dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
				}
			}*/
			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
				dblCalculatedAmount = ((dblEEESIAmount * dblAmount) / 100);
			}
//			if(uF.parseToInt(strEmpId) == 322) {
//				System.out.println("dblCalculatedAmount==>"+dblCalculatedAmount);
//			}
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
			Map<String, String> hmTotal, String strWLocationStateId, String strEmpId, Map<String, String> hmVariables, Map<String, String> hmAnnualVariables, String strD1, String strD2, String strPC) {
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
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
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
				if (dblAmountEligibility > 0 && dblAmountEligibility <= dblESIMaxAmount) {
					dblCalculatedAmount = ((dblERESIAmount * dblAmount) / 100);
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
			Map<String, String> hmTotal, String strEmpId, String strD1, String strPaycycle, Map<String, String> hmEmpStateMap,
			Map<String, String> hmVariables, boolean isInsert,Map<String, String> hmAnnualVariables, 
			String strD2,Map<String, List<Map<String, String>>> hmEmpArrear,
			Map<String, Map<String, String>> hmArrearEmployerESI) {

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
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i].trim()));
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
				dblCalculatedAmountEESI = (dblEESIAmount * dblAmount) / 100;
				dblCalculatedAmountERSI = (dblERSIAmount * dblAmount) / 100;
			} else if(attemptFlag && !deductFlag) {
				dblCalculatedAmountEESI = 0;
				dblCalculatedAmountERSI = 0;
			} else {
				if (dblAmountEligibility > 0 && dblAmountEligibility <= dblMaxAmount) {
					dblCalculatedAmountEESI = (dblEESIAmount * dblAmount) / 100;
					dblCalculatedAmountERSI = (dblERSIAmount * dblAmount) / 100;
				}
			}*/
			
			if (dblAmountEligibility > 0 && dblAmountEligibility <= dblMaxAmount) {
				dblCalculatedAmountEESI = (dblEESIAmount * dblAmount) / 100;
				dblCalculatedAmountERSI = (dblERSIAmount * dblAmount) / 100;
			}
			
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
			Map<String, Map<String, String>> hmArrearEmployerPF) {
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
				
		//===start parvez date: 15-01-2022===
				
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
					pst.execute();
					pst.close();
				} else{
					pst = con.prepareStatement("update emp_epf_details set  eps_max_limit=?, edli_max_limit=?, erpf_contribution=?, erps_contribution=?, erdli_contribution=?, pf_admin_charges=?, edli_admin_charges=?  where financial_year_start=? and financial_year_end=? and emp_id=? and paycycle=? and _month=?");
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
					pst.execute();
					pst.close();
				}
		//===end parvez date: 15-01-2022===		
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
			boolean isInsert, Map<String, Map<String, String>> hmArearAmountMap,
			Map<String, List<Map<String, String>>> hmEmpArrear,
			Map<String, Map<String, String>> hmArrearEmployeePF) {
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
//				if(uF.parseToInt(strEmpId) == 4581) {
//					System.out.println("arrSalaryHeads[i]==>"+arrSalaryHeads[i]+"--hmTotal.get(arrSalaryHeads[i])==>"+hmTotal.get(arrSalaryHeads[i]));
//				}
			}

			Map<String, String> hmArearMap = hmArearAmountMap.get(strEmpId);
			if (hmArearMap == null)
				hmArearMap = new HashMap<String, String>();

			double dblArrearBasic = uF.parseToDouble(hmArearMap.get("BASIC_AMOUNT"));

			dblAmount += dblArrearBasic;

			/**
			 * Change on 24-04-2012
			 */

			if (dblAmount >= dblMaxAmount) {
				dblAmount = dblMaxAmount;

			}
			dblCalculatedAmount = (dblEEPFAmount * dblAmount) / 100;

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

				pst = con.prepareStatement("insert into emp_epf_details (financial_year_start, " +
						"financial_year_end, salary_head_id, epf_max_limit, eepf_contribution, " +
						"emp_id, paycycle, _month, evpf_contribution) values (?,?,?,?,?,?,?,?,?)");
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

	private double getLeaveEncashmentAmtDetails(Connection con, UtilityFunctions uF, CommonFunctions CF, String strEmpId, double dblEnashDays,
			int nTotalNumberOfDaysForCalc, String strD1, String strD2, String strPC, String strLevel, double dblIncrementBasic, double dblIncrementDA) {
		double dblEncashAmount = 0.0d;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select sum(no_days) as no_days,leave_type_id from emp_leave_encashment where emp_id=? "
					+ "and paid_from= ? and paid_to=? and paycycle=? and is_approved=1 and is_paid=false group by leave_type_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strPC));
			// System.out.println("pst===>>>>>>"+pst);
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

				double dblPercentage = uF.parseToDouble(hmLeaveSHeadsPercetage.get(strLeaveTypeId));
				if (dblPercentage == 0.0d) {
					continue;
				}

				dblEnashDays = uF.parseToDouble(hmLeaveEncashDays.get(strLeaveTypeId));

				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmInnerActualCTC = new HashMap<String, Map<String, String>>(); //CF.getSalaryCalculation(con, hmInnerisDisplay, uF.parseToInt(strEmpId), dblEnashDays, 0, 0,nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, strLevel, uF, CF, strD2);

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

	
	public void getEmpTDSProjection(UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, Map<String, String> hmCurrSalHeadsAndAmt) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			for(int i=0; i<12;i++) {
				String strDate = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
				alMonth.add(uF.getDateFormat(strDate, DATE_FORMAT, "MM"));
				cal.add(Calendar.MONTH, 1);
			}
			
//			System.out.println("alMonth ===>> " + alMonth);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			String strCurrId = hmEmpCurrency.get(strEmpId);
			Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
			if (hmCurrencyInner == null) hmCurrencyInner = new HashMap<String, String>();
			String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
			request.setAttribute("strCurrSymbol", strCurrSymbol);
			
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
			
			String strEmpGender = CF.getEmpGender(con, uF, strEmpId);
			
			String empStartMonth = null;
			String empJoiningYr = null;
			String empJoinDate = null;
			boolean isJoinDateBetween = false;
			String empEndMonth = null;
			String empEndDate = null;
			boolean isEndDateBetween = false;
			String strSalCalStatus = null;
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
			pst.setInt(1,uF.parseToInt(strEmpId)); 
			rs = pst.executeQuery();
			while(rs.next()) {
				
				if(rs.getString("joining_date") !=null && !rs.getString("joining_date").trim().equals("") && !rs.getString("joining_date").trim().equalsIgnoreCase("")) {
					empJoinDate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT);
					if(empJoinDate !=null && !empJoinDate.trim().equals("") && !empJoinDate.trim().equalsIgnoreCase("NULL")) {
						isJoinDateBetween = uF.isDateBetween(uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT), uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT), uF.getDateFormatUtil(empJoinDate, DATE_FORMAT));
						if(isJoinDateBetween) {
							empStartMonth = uF.getDateFormat(empJoinDate+"", DATE_FORMAT, "MM");
							empJoiningYr = uF.getDateFormat(empJoinDate+"", DATE_FORMAT, "yyyy");
						}
					}
				}
//				System.out.println(" empStartMonth==>"+empStartMonth+"-----empJoinDate====>"+rs.getString("joining_date")+"---isJoinDateBetween==>"+isJoinDateBetween);
				
				if(rs.getString("employment_end_date") !=null && !rs.getString("employment_end_date").trim().equals("") && !rs.getString("employment_end_date").trim().equalsIgnoreCase("")) {
					empEndDate = uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT);
					if(empEndDate !=null && !empEndDate.trim().equals("") && !empEndDate.trim().equalsIgnoreCase("NULL")) {
						isEndDateBetween = uF.isDateBetween(uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT), uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT), uF.getDateFormatUtil(empEndDate, DATE_FORMAT));
						if(isEndDateBetween) {
							empEndMonth = uF.getDateFormat(empEndDate+"", DATE_FORMAT, "MM");
						}
					}
				}
				strSalCalStatus = rs.getString("is_disable_sal_calculate");
//				System.out.println(" empEndDate==>"+rs.getString("employment_end_date")+"-----empEndMonth====>"+empEndMonth+"--isEndDateBetween==>"+isEndDateBetween);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=? and is_alive=false and employment_end_date between ? and ? order by emp_per_id");
			pst.setInt(1,uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmExEmp = new HashMap<String, String>();
			while(rs.next()) {
				hmExEmp.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
			Map<String, Map<String, String>> hmEmpTDSReimbCTC = CF.getEmpTDSReimbursementCTCAmount(con, CF, uF, strEmpId, strFinancialYearStart,strFinancialYearEnd);
			if(hmEmpTDSReimbCTC == null) hmEmpTDSReimbCTC = new HashMap<String, Map<String, String>>();
			request.setAttribute("hmEmpTDSReimbCTC", hmEmpTDSReimbCTC);
			
			double dblReimTDSCTC = 0.0d;
			Iterator<String> itRCTC = hmEmpTDSReimbCTC.keySet().iterator();
			while(itRCTC.hasNext()) {
				String strReimCTCId = itRCTC.next();
				Map<String, String> hmEmpTDSReimbCTCInner = hmEmpTDSReimbCTC.get(strReimCTCId);
				if(hmEmpTDSReimbCTCInner == null) hmEmpTDSReimbCTCInner = new HashMap<String, String>();
				
				dblReimTDSCTC += uF.parseToDouble(hmEmpTDSReimbCTCInner.get("REIMBURSEMENT_TDS_AMOUNT"));
			}

			String strLevelId = CF.getEmpLevelId(con, strEmpId);
			Map<String, Map<String, String>> hmEmpSalaryTotal = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpSalaryHeadPaidAmt = new HashMap<String, String>();
//			Map<String, String> hmIsPaidEmp = new HashMap<String, String>();
			Map<String, String> hmEmpTDSPaidAmountDetails=new HashMap<String, String>();
			List<String> alSalaryHeadId = new ArrayList<String>();
			pst = con.prepareStatement("select * from payroll_generation where emp_id=? and financial_year_from_date=? " +
					"and financial_year_to_date=? order by paycycle");
			pst.setInt(1,uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery(); 
			double totalPTYear = 0.0d;
			Map<String,String> hmLeaveEncashmet = new HashMap<String, String>();
			while(rs.next()) {
//				hmIsPaidEmp.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<=9?"0"+rs.getString("month"):rs.getString("month")),rs.getString("emp_id"));
				if(rs.getString("salary_head_id").equals(TDS+"")) {
					hmEmpTDSPaidAmountDetails.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<=9?"0"+rs.getString("month"):rs.getString("month")),rs.getString("amount"));
					continue;
				}
				if(rs.getString("salary_head_id").equals(""+REIMBURSEMENT) || rs.getString("salary_head_id").equals(""+MOBILE_REIMBURSEMENT) 
						|| rs.getString("salary_head_id").equals(""+TRAVEL_REIMBURSEMENT) || rs.getString("salary_head_id").equals(""+OTHER_REIMBURSEMENT) 
						|| rs.getString("salary_head_id").equals(""+SERVICE_TAX) || rs.getString("salary_head_id").equals(""+SWACHHA_BHARAT_CESS) 
						|| rs.getString("salary_head_id").equals(""+KRISHI_KALYAN_CESS) || rs.getString("salary_head_id").equals(""+CGST) || rs.getString("salary_head_id").equals(""+SGST)) {
					continue;
				}
				Map<String, String> hmEmpSalary = hmEmpSalaryTotal.get(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<10?"0"+rs.getString("month"):rs.getString("month")));
				double dblReimbursement = 0.0d;
				if(hmEmpSalary==null) {
					hmEmpSalary=new HashMap<String, String>();
//					dblReimbursement = uF.parseToDouble(hmReimbursementAmt.get(rs.getString("emp_id"))); 
				}
				
				hmEmpSalary.put(rs.getString("salary_head_id"), rs.getString("amount")); 
				
				double dblSalPaidAmt = uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(rs.getString("salary_head_id")));
				dblSalPaidAmt += uF.parseToDouble(rs.getString("amount"));
				hmEmpSalaryHeadPaidAmt.put(rs.getString("salary_head_id"), ""+dblSalPaidAmt);				
				
				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
					double dblGross = uF.parseToDouble(hmEmpSalary.get("GROSS")) + uF.parseToDouble(rs.getString("amount")) + dblReimbursement;
					hmEmpSalary.put("GROSS", dblGross+"");
				}
				hmEmpSalaryTotal.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<10?"0"+rs.getString("month"):rs.getString("month")), hmEmpSalary);
				
				if(!alSalaryHeadId.contains(rs.getString("salary_head_id"))) {
					alSalaryHeadId.add(rs.getString("salary_head_id"));
				}
				if(uF.parseToInt(rs.getString("salary_head_id")) == PROFESSIONAL_TAX) {
					totalPTYear +=uF.parseToDouble(rs.getString("amount"));
				}
				
				if(uF.parseToInt(rs.getString("salary_head_id")) == LEAVE_ENCASHMENT && hmExEmp.containsKey(rs.getString("emp_id"))) {
					hmLeaveEncashmet.put(rs.getString("emp_id"), rs.getString("amount"));
				}
			}
			rs.close();
			pst.close();
			
			
			double dblPrevOrgGross = 0.0d;
			double dblPrevOrgTDSAmount = 0.0d;
			Map<String, String> hmPrevOrgTDSDetails = new HashMap<String, String>();
			if(isJoinDateBetween) {
				pst = con.prepareStatement("select * from prev_earn_deduct_details where financial_start=? and financial_end=? and emp_id=?");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strEmpId));
				rs = pst.executeQuery();
				if(rs.next()) {
					dblPrevOrgGross = rs.getDouble("gross_amount");
					dblPrevOrgTDSAmount = rs.getDouble("tds_amount");
	
					hmPrevOrgTDSDetails.put(rs.getString("emp_id")+"_GROSS_AMT", ""+rs.getDouble("gross_amount"));
					hmPrevOrgTDSDetails.put(rs.getString("emp_id")+"_FORM16_DOC", rs.getString("document_name"));
					hmPrevOrgTDSDetails.put(rs.getString("emp_id")+"_TDS_AMT_"+uF.zero((uF.parseToInt(empStartMonth)-1)), ""+rs.getDouble("tds_amount"));
				}
				rs.close();
				pst.close();
				hmPrevOrgTDSDetails.put(strEmpId+"_JOINING_MONTH", empStartMonth);
				hmPrevOrgTDSDetails.put(strEmpId+"_JOINING_YEAR", empJoiningYr);
			}
			
			request.setAttribute("hmPrevOrgTDSDetails", hmPrevOrgTDSDetails);
//			System.out.println("ViewEmpTDSProjection hmPrevOrgTDSDetails ===>> " + hmPrevOrgTDSDetails);
			
			/**
			 * Calculate projected Salary
			 * */
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
			
			Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
			Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpStateMap = new HashMap<String, String>();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
			pst = con.prepareStatement("select * from salary_details where level_id in (select level_id from level_details where " +
					"level_id =?) and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by level_id, earning_deduction desc, salary_head_id, weight");
			pst.setInt(1, uF.parseToInt(strLevelId));
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
			}
			rs.close();
			pst.close();
			
			Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strLevelId);
			if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
			
//			System.out.println("getStrFinancialYearStart ===>> "+ strFinancialYearStart + " -- hmCurrSalHeadsAndAmt =======>> " + hmCurrSalHeadsAndAmt);
			Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmSalaryCal = CF.getSalaryCalculationForArrear(con,hmInnerisDisplay,uF.parseToInt(strEmpId), 30, 0, 0, 30, 0, 0, strLevelId, uF, CF,uF.getDateFormatUtil(uF.getCurrentDate(CF.getStrTimeZone()), DATE_FORMAT),hmSalInner, null, strSalCalStatus, hmCurrSalHeadsAndAmt);
//			System.out.println("hmSalaryCal =======>> " + hmSalaryCal);
			Iterator<String> it = hmSalaryCal.keySet().iterator();
			double salaryGross=0;
			Map<String,String> hmTotal=new HashMap<String,String>();
			while(it.hasNext()) {
				String strSalaryId = it.next();
				
				Map<String,String> hm = hmSalaryCal.get(strSalaryId);
				hmTotal.put(strSalaryId, hm.get("AMOUNT"));
				if(hm.get("EARNING_DEDUCTION").equals("E")) {
					salaryGross+=uF.parseToDouble(hm.get("AMOUNT"));
				}
			}
			
			String[] hraSalaryHeads = null;
			if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
				hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
			}
			
			Map<String, String> hmTDSProjectedEmp = new HashMap<String, String>();
			pst = con.prepareStatement("select * from tds_projections where fy_year_from=? and fy_year_end=? and emp_id=?");
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3,uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmTDSProjectedEmp.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<10?"0"+rs.getString("month"):rs.getString("month")), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
			}
			rs.close();
			pst.close();
			
			Map<String, Map<String, Map<String, String>>> hmAttendanceSal = new HashMap<String, Map<String,Map<String, String>>>();
			boolean startFlag = false;
			boolean endFlag = false;
			int monthTDSLEFT=12;
			int nMonth = 0;
			int nAttendanceApproveMonth = 0;
			double dblCalAttendacePF = 0.0d;
			double totalProjectedGross = 0;
			double totalProjectedPT = 0;
			double totalTDSYearPaid = 0;
			double dblHraSalHeadsCalAttendace = 0;
			double dblLTASalCalAttendace = 0;
			double dblBasicSalCalAttendace = 0; 
			Map<String,String> hmProjectSalAmt = new HashMap<String,String>();
			for(int i=0; i<alMonth.size(); i++) {
				String strMonth = alMonth.get(i); 
				
				if(isJoinDateBetween && empStartMonth!=null && empStartMonth.equals(strMonth)) {
					startFlag = true;
				}
				
				if(isJoinDateBetween && !startFlag) {
					continue;
				}
				Map<String, String> hmEmpSalary = hmEmpSalaryTotal.get(strEmpId+"_"+strMonth);
				if(hmEmpSalary != null) {
					
				} else {
					if(isEndDateBetween && endFlag) {
						continue;
					}
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from approve_attendance where emp_id=? and EXTRACT(month FROM approve_to)=? " +
						"and financial_year_start=? and financial_year_end=?");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(strMonth));
					pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//					System.out.println("approve_attendance pst ===>> " + pst);
					rs = pst.executeQuery();
					boolean isAttendance = false;
					double dblPaidDays = 0.0d;
					double dblTotalDays = 0.0d;
					String strDate2 = null;
					while(rs.next()) {
						isAttendance = true;
						dblPaidDays = uF.parseToDouble(rs.getString("paid_days"));
						dblTotalDays = uF.parseToDouble(rs.getString("total_days"));
						strDate2 = uF.getDateFormat(rs.getString("approve_to"), DBDATE, DATE_FORMAT);
					}
					rs.close();
					pst.close();
					
//					System.out.println("strMonth====>"+strMonth+"---isAttendance====>"+isAttendance+"---dblPaidDays====>"+dblPaidDays+"---strDate2==>"+strDate2);
					if(isAttendance) {
						Map<String, Map<String, String>> hmInnerisDisplayCal = new HashMap<String, Map<String, String>>();
						Map<String, Map<String, String>> hmInnerCal = CF.getSalaryCalculationForArrear(con,hmInnerisDisplayCal,uF.parseToInt(strEmpId), dblPaidDays, 0, 0, (int)dblTotalDays, 0, 0, strLevelId, uF, CF, strDate2, hmSalInner, null, strSalCalStatus, hmCurrSalHeadsAndAmt);
						
//						System.out.println("hmInnerCal ===>> " + hmInnerCal);
						boolean isPF = false;
						Iterator<String> it11 = hmInnerCal.keySet().iterator();
						while(it11.hasNext()) {
							String strSalaryId = it11.next();
							Map<String,String> hm = hmInnerCal.get(strSalaryId);
							double dblProjectSalHeadAmt = uF.parseToDouble(hmProjectSalAmt.get(strSalaryId));
							dblProjectSalHeadAmt += uF.parseToDouble(hm.get("AMOUNT"));
							hmProjectSalAmt.put(strSalaryId,""+dblProjectSalHeadAmt);	
							
							if(hm.get("EARNING_DEDUCTION").equals("E")) {
								totalProjectedGross+=uF.parseToDouble(hm.get("AMOUNT"));
							} else if(hm.get("EARNING_DEDUCTION").equals("D") && strSalaryId.equals(EMPLOYEE_EPF+"")) {
								isPF = true;
							}
							if(hraSalaryHeads!=null) {
								List<String> alHrSalHeads = Arrays.asList(hraSalaryHeads);
								if(alHrSalHeads == null) alHrSalHeads = new ArrayList<String>();
								if(alHrSalHeads.contains(strSalaryId)) {
									dblBasicSalCalAttendace += uF.parseToDouble(hm.get("AMOUNT"));
								}
							}
							if(strSalaryId.equals(""+HRA)) {
								dblHraSalHeadsCalAttendace += uF.parseToDouble(hm.get("AMOUNT"));
							}
							if(strSalaryId.equals(""+LTA)) {
								dblLTASalCalAttendace += uF.parseToDouble(hm.get("AMOUNT"));
							}
						}
						
						if(isPF) {
							dblCalAttendacePF += CF.calculateEEPF(con, null, uF, totalProjectedGross, strFinancialYearStart, strFinancialYearEnd, hmProjectSalAmt, strEmpId, null, null);
						}
						totalPTYear += CF.calculateProfessionalTax(con,uF,totalProjectedGross,strFinancialYearStart,strFinancialYearEnd, uF.parseToInt(strMonth), hmEmpStateMap.get(strEmpId), strEmpGender);
						
						nAttendanceApproveMonth++;
					} else {
						Iterator<String> it1 = hmSalaryCal.keySet().iterator();
						while(it1.hasNext()) {
							String strSalaryId = it1.next();
							Map<String,String> hm = hmSalaryCal.get(strSalaryId);
							double dblProjectSalHeadAmt = uF.parseToDouble(hmProjectSalAmt.get(strSalaryId));
							dblProjectSalHeadAmt += uF.parseToDouble(hm.get("AMOUNT"));
							hmProjectSalAmt.put(strSalaryId, ""+dblProjectSalHeadAmt);	
							
							if(hm.get("EARNING_DEDUCTION").equals("E")) {
								totalProjectedGross +=uF.parseToDouble(hm.get("AMOUNT"));
							}
						}
						totalPTYear += CF.calculateProfessionalTax(con,uF,totalProjectedGross,strFinancialYearStart,strFinancialYearEnd, uF.parseToInt(strMonth), hmEmpStateMap.get(strEmpId), strEmpGender);
					}
					
				}
//				System.out.println("11 totalPTYear===>"+totalPTYear);
				if(hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+strMonth)!=null) {
					totalTDSYearPaid+=uF.parseToDouble(hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+strMonth));
					monthTDSLEFT--;
//					System.out.println("2 if strMonth===>"+strMonth+"----totalTDSYearPaid===>"+totalTDSYearPaid);
				} else if(hmEmpSalaryTotal.get(strEmpId+"_"+strMonth)!=null) {
					totalTDSYearPaid+=0.0d;
					monthTDSLEFT--;
//					System.out.println("2 if strMonth===>"+strMonth+"----totalTDSYearPaid===>"+totalTDSYearPaid);
				} else if(hmTDSProjectedEmp.get(strEmpId+"_"+strMonth)!=null) {
					totalTDSYearPaid+=uF.parseToDouble(hmTDSProjectedEmp.get(strEmpId+"_"+strMonth));
					monthTDSLEFT--;
//					System.out.println("2 else if strMonth===>"+strMonth+"----totalTDSYearPaid===>"+totalTDSYearPaid);
				}
				
				if(isEndDateBetween && empEndMonth!=null && empEndMonth.equals(strMonth)) {
					endFlag = true;
				}
				
				nMonth++;
			}
			
			/**
			 * Calculate projected Salary End
			 * */
			
//			System.out.println("alSalaryHeadId===>"+alSalaryHeadId);
//			System.out.println("hmEmpSalaryTotal===>"+hmEmpSalaryTotal);
//			System.out.println("hmEmpTDSPaidAmountDetails===>"+hmEmpTDSPaidAmountDetails);
//			System.out.println("hmProjectSalAmt===>"+hmProjectSalAmt);
//			System.out.println("hmTDSProjectedEmp===>"+hmTDSProjectedEmp);

//			if(alSalaryHeadId.size() > 0) {
//				String strSalaryHeadIds = StringUtils.join(alSalaryHeadId.toArray(),",");
//				pst = con.prepareStatement("select * from salary_details where salary_head_id in ("+strSalaryHeadIds+") and level_id=? " +
//						"and earning_deduction='E' order by weight");
//			} else {
				pst = con.prepareStatement("select * from salary_details where level_id=? and earning_deduction='E' " +
					"and salary_head_id not in("+CTC+") and is_delete != true and (is_contribution is null or is_contribution=false) order by weight");
//			}
			pst.setInt(1, uF.parseToInt(strLevelId)); 
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alSalaryHead = new ArrayList<Map<String,String>>();
			List<String> alSalHeadIds = new ArrayList<String>();
			while(rs.next()) {
				if(!alSalHeadIds.contains(rs.getString("salary_head_id"))) {
					alSalHeadIds.add(rs.getString("salary_head_id"));
					
					Map<String, String> hmSalaryHead = new HashMap<String, String>();
					hmSalaryHead.put("SALARY_HEAD_ID", rs.getString("salary_head_id")); 
					hmSalaryHead.put("SALARY_HEAD_NAME", rs.getString("salary_head_name")); 
					alSalaryHead.add(hmSalaryHead);
				}
			}
			rs.close();
			pst.close();
			
			
			Map hmTaxInner = new HashMap();
			Map<String, String> hmUS10_16_SalHeadData = new HashMap<String, String>();
			int nSalaryHeadSize = alSalaryHead.size();
			double totalGrossYear = 0.0d;
			for(int i = 0; i < nSalaryHeadSize; i++) { 
				Map<String, String> hmSalaryHead = alSalaryHead.get(i);
				if(hmSalaryHead == null) hmSalaryHead = new HashMap<String, String>();
				
				String strSalaryHeadId1 = hmSalaryHead.get("SALARY_HEAD_ID");
				totalGrossYear += uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId1)) + uF.parseToDouble(hmProjectSalAmt.get(strSalaryHeadId1));
			}
			
			pst = con.prepareStatement("select * from deduction_tax_misc_details where trail_status=1 and financial_year_from=? and financial_year_to=? and state_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
			rs = pst.executeQuery();
			double dblFlatTDS = 0.0d;
			Map<String,Map<String,String> > educationCessMp=new HashMap<String,Map<String,String> >();
			while(rs.next()) {
				Map<String,String> innereducationCessMp=new HashMap<String,String>();

				innereducationCessMp.put(O_STANDARD_CESS,rs.getString("standard_tax"));
				innereducationCessMp.put(O_EDUCATION_CESS,rs.getString("education_tax"));
				innereducationCessMp.put(O_FLAT_TDS,rs.getString("flat_tds"));
				
				innereducationCessMp.put("MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				innereducationCessMp.put("REBATE_AMOUNT", rs.getString("rebate_amt"));
				innereducationCessMp.put("SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				innereducationCessMp.put("KRISHI_KALYAN_CESS", rs.getString("KRISHI_KALYAN_CESS"));
				
				innereducationCessMp.put("CGST", rs.getString("cgst"));
				innereducationCessMp.put("SGST", rs.getString("sgst"));

				educationCessMp.put(rs.getString("state_id"), innereducationCessMp);
			}
			rs.close();
			pst.close();
			
			Map<String,String> innereducationCessMp=educationCessMp.get(hmEmpStateMap.get(strEmpId));
			if(innereducationCessMp==null) innereducationCessMp=new HashMap<String,String>();
			
			Map hmSectionLimitA = new HashMap();
			Map hmSectionLimitP = new HashMap();
			Map<String, String> hmSectionAdjustedGrossIncomeLimitStatus = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			while (rs.next()) {
				if(rs.getString("section_limit_type").equalsIgnoreCase("A")) {
					hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
					hmSectionLimitA.put(rs.getString("section_id")+"_CEILING_AMT", rs.getBoolean("is_ceiling_applicable") ? rs.getDouble("ceiling_amount") : "0");
				} else {
					hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
					hmSectionLimitP.put(rs.getString("section_id")+"_CEILING_AMT", rs.getBoolean("is_ceiling_applicable") ? rs.getDouble("ceiling_amount") : "0");
				}
				hmSectionAdjustedGrossIncomeLimitStatus.put(rs.getString("section_id"), rs.getString("is_adjusted_gross_income_limit"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmSectionAdjustedGrossIncomeLimitStatus ===>> " + hmSectionAdjustedGrossIncomeLimitStatus);
			
			Map<String, String> hmSectionMap = CF.getSectionMap(con,strFinancialYearStart,strFinancialYearEnd);
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? " +
				"and section_code not in ('HRA')  and isdisplay=true and parent_section=0 and under_section=8 and emp_id in ("+strEmpId+") group by emp_id, sd.section_id order by emp_id"); // and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmInvestment = new HashMap();
			Map<String, String> hmEmpExemptionsCH1Map = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpInvestment = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmEmpInvestmentIsAdjustedLimit = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmpActualInvestment = new HashMap<String, Map<String, String>>();
			double dblInvestmentLimit = 0;
			double dblInvestmentCeilingLimit = 0;
			double dblInvestmentEmp = 0;
			List<String> alSectionIds = new ArrayList<String>();
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
				
				Map<String,String> hmInvest=hmEmpInvestment.get(rs.getString("emp_id"));
				if(hmInvest==null) hmInvest=new HashMap<String, String>();
				
				if(dblInvestment >= dblInvestmentLimit && dblInvestmentLimit>=0) {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				} else {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestment);
				}
				hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
				
				Map<String,String> hmActualInvest=hmEmpActualInvestment.get(rs.getString("emp_id"));
				if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
				hmActualInvest.put(strSectionId, ""+dblInvestment);
				
				hmEmpActualInvestment.put(rs.getString("emp_id"), hmActualInvest);
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpActualInvestment ===>> " + hmEmpActualInvestment);
//			System.out.println("hmEmpInvestment ===>> " + hmEmpInvestment);
			
			
			Map<String, Map<String, Map<String, Map<String, String>>>> hmEmpIncludeSubSectionData = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
			pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
				"id.fy_from=? and id.fy_to=? and status=true and trail_status=1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA')  and isdisplay=true and parent_section>0 " +
				"and emp_id in ("+strEmpId+") and sub_section_no>0 and (is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit=false) and (include_sub_section is not null or include_sub_section !='') order by emp_id,sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
				if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
				
				Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
				if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("SUB_SEC_NO", rs.getString("sub_section_no"));
				hmInner.put("SUB_SEC_AMT_PAID", rs.getString("amount_paid"));
				hmInner.put("SUB_SEC_LIMIT_TYPE", rs.getString("sub_section_limit_type"));
				hmInner.put("SUB_SEC_AMT", rs.getString("sub_section_amt"));
				hmOuter.put(rs.getString("sub_section_no"), hmInner);
				
				hmIncludeSubSectionData.put(strSectionId, hmOuter);
				
				hmEmpIncludeSubSectionData.put(rs.getString("emp_id"), hmIncludeSubSectionData);
			}
			rs.close();
			pst.close();
			
			
			
			Map<String, Map<String, String>> hmEmpSubSecMinusAmt = new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
				"id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and " +
				"section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=8 and emp_id in ("+strEmpId+") and sub_section_no>0 and " +
				"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id,sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			String oldSectionId = null;
			while(rs.next()) {
				
				String strSectionId = rs.getString("section_id");
				String strSubSecNo = rs.getString("sub_section_no");
				double dblInvestment = rs.getDouble("amount_paid");
				String strSubSecLimitType = rs.getString("sub_section_limit_type");
//				System.out.println("strSubSecLimitType ===>> "  +strSubSecLimitType);
				double dblSubSecLimit = rs.getDouble("sub_section_amt");
//				System.out.println("dblInvestment ===>> "  +dblInvestment);
//				System.out.println("dblSubSecLimit ===>> "  +dblSubSecLimit);
				if(rs.getString("include_sub_section") != null && rs.getString("include_sub_section").length()>2) {
					List<String> al = Arrays.asList(rs.getString("include_sub_section").substring(1, rs.getString("include_sub_section").length()-1).split(","));
					Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
					if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
					
					Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
					if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
					
					double dblTotSubSecInvestment = 0;
					Iterator<String> itSubSec = hmOuter.keySet().iterator();
					while (itSubSec.hasNext()) {
						String strSubSecno = itSubSec.next();
						if(al.contains(strSubSecno)) {
							continue;
						}
						Map<String, String> hmInner = hmOuter.get(strSubSecno);
						dblTotSubSecInvestment += uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
					}
					if(al.contains(strSubSecNo) && dblTotSubSecInvestment>0) {
						continue;
					}
//					---------------
					Map<String,String> hmSubSecMinusAmt = hmEmpSubSecMinusAmt.get(rs.getString("emp_id"));
					if(hmSubSecMinusAmt==null) hmSubSecMinusAmt = new HashMap<String, String>();
					
					Map<String,String> hmInvest = hmEmpInvestment.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
					double dblSubSecMinusInvestment =0;
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
						dblSubSecMinusInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
						dblSubSecMinusInvestment = uF.parseToDouble(hmSubSecMinusAmt.get(strSectionId));
					}
//					--------------------
					
					double dblSubSecMinActAmt=0;
					for(int i=0; i<al.size(); i++) {
						String strSubSNo = al.get(i);
						if(uF.parseToInt(strSubSNo)>0) {
							Map<String, String> hmInner = hmOuter.get(strSubSNo);
							double dblAppliedAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
							double dblSubSecLimitAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT"));
							strSubSecLimitType = hmInner.get("SUB_SEC_LIMIT_TYPE");
							if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
								dblSubSecLimitAmt = (dblAppliedAmt * dblSubSecLimitAmt) / 100;
							}
							dblSubSecMinActAmt += Math.min(dblAppliedAmt, dblSubSecLimitAmt);
						}
					}
					
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						if(dblInvestment>0 && dblSubSecMinActAmt>0 && dblSubSecMinActAmt>dblSubSecMinusInvestment) {
							double dblMinAmt = dblSubSecMinActAmt - dblSubSecMinusInvestment;
							double dblAmt = dblSubSecLimit - dblInvestment;
							if(dblMinAmt>dblAmt) {
								dblSubSecMinusInvestment += dblAmt;
								dblSecInvestment += Math.min((dblInvestment + dblAmt), dblSubSecLimit);
							} else {
								dblSubSecMinusInvestment += dblMinAmt;
								dblSecInvestment += Math.min((dblInvestment + dblMinAmt), dblSubSecLimit);
							}
						} else {
							dblSecInvestment += dblInvestment;
						}
					}
//					System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
					
					hmInvest.put(strSectionId, ""+dblSecInvestment);
					hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
					
					hmSubSecMinusAmt.put(strSectionId, ""+dblSubSecMinusInvestment);
					hmEmpSubSecMinusAmt.put(rs.getString("emp_id"), hmSubSecMinusAmt);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
					
					
				} else {
					Map<String,String> hmInvest = hmEmpInvestment.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
	//				System.out.println("oldSectionId ===>> "  +oldSectionId);
	//				System.out.println("strSectionId ===>> "  +strSectionId);
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
	//					System.out.println("in if dblSecInvestment =====>> "  +dblSecInvestment);
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
	//					System.out.println("in else dblSecInvestment =====>> "  +dblSecInvestment);
					}
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						dblSecInvestment += dblInvestment;
					}
	//				System.out.println("dblSecInvestment =====>> "  +dblSecInvestment);
					
					hmInvest.put(strSectionId, ""+dblSecInvestment);
					hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpActualInvestment 11111 ===>> " + hmEmpActualInvestment);
//			System.out.println("hmEmpInvestment 11111 ===>> " + hmEmpInvestment);
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? " +
				"and section_code not in ('HRA') and isdisplay=true and parent_section=0 and under_section=9 and emp_id in ("+strEmpId+") group by emp_id, sd.section_id order by emp_id "); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map<String, Map<String, String>> hmEmpInvestment1 = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpExemptionsCH2Map = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
			dblInvestmentLimit = 0;
			dblInvestmentCeilingLimit = 0;
			dblInvestmentEmp = 0;
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
//					System.out.println("dblInvestmentCeilingLimit ===>> " + dblInvestmentCeilingLimit);
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				} else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
					dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
					if(dblInvestmentCeilingLimit>0) {
						dblInvestmentLimit = dblInvestmentCeilingLimit;
					}
				}
				
				Map<String,String> hmInvest=hmEmpInvestment1.get(rs.getString("emp_id"));
				if(hmInvest==null) hmInvest=new HashMap<String, String>();
//				System.out.println("dblInvestment ===>> " + dblInvestment);
//				System.out.println("dblInvestmentLimit ===>> " + dblInvestmentLimit);
				if(dblInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
				} else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
					hmInvest.put(strSectionId, ""+dblInvestment);
				}
				hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
				
				Map<String,String> hmActualInvest = hmEmpActualInvestment1.get(rs.getString("emp_id"));
				if(hmActualInvest==null) hmActualInvest = new HashMap<String, String>();
				hmActualInvest.put(strSectionId, ""+dblInvestment);
				
				hmEmpActualInvestment1.put(rs.getString("emp_id"), hmActualInvest);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpInvestment1 ====================>> " + hmEmpInvestment1);
//			System.out.println("hmEmpActualInvestment1 ===============>> " + hmEmpActualInvestment1);
			
			pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
				"id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and " +
				"section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=9 and emp_id in ("+strEmpId+") and sub_section_no>0 and " +
				"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id,sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//				System.out.println(" pst==>"+pst);
//				Map<String, Map<String, String>> hmEmpInvestment = new HashMap<String, Map<String, String>>();
//				Map<String, Map<String, String>> hmEmpActualInvestment = new HashMap<String, Map<String, String>>();
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			oldSectionId = null;
			while(rs.next()) {
				String strSectionId = rs.getString("section_id");
				String strSubSecNo = rs.getString("sub_section_no");
				double dblInvestment = rs.getDouble("amount_paid");
				String strSubSecLimitType = rs.getString("sub_section_limit_type");
//					System.out.println("strSubSecLimitType ===>> "  +strSubSecLimitType);
				double dblSubSecLimit = rs.getDouble("sub_section_amt");
//					System.out.println("dblInvestment ===>> "  +dblInvestment);
//					System.out.println("dblSubSecLimit ===>> "  +dblSubSecLimit);
				
				if(rs.getString("include_sub_section") != null && rs.getString("include_sub_section").length()>2) {
					List<String> al = Arrays.asList(rs.getString("include_sub_section").substring(1, rs.getString("include_sub_section").length()-1).split(","));
					Map<String, Map<String, Map<String, String>>> hmIncludeSubSectionData = hmEmpIncludeSubSectionData.get(rs.getString("emp_id"));
					if(hmIncludeSubSectionData==null) hmIncludeSubSectionData = new HashMap<String, Map<String, Map<String, String>>>();
					
					Map<String, Map<String, String>> hmOuter = hmIncludeSubSectionData.get(strSectionId);
					if(hmOuter==null) hmOuter = new HashMap<String, Map<String, String>>();
					
					double dblTotSubSecInvestment = 0;
					Iterator<String> itSubSec = hmOuter.keySet().iterator();
					while (itSubSec.hasNext()) {
						String strSubSecno = itSubSec.next();
						if(al.contains(strSubSecno)) {
							continue;
						}
						Map<String, String> hmInner = hmOuter.get(strSubSecno);
						dblTotSubSecInvestment += uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
					}
					if(al.contains(strSubSecNo) && dblTotSubSecInvestment>0) {
						continue;
					}
					
//					---------------
					Map<String,String> hmSubSecMinusAmt = hmEmpSubSecMinusAmt.get(rs.getString("emp_id"));
					if(hmSubSecMinusAmt==null) hmSubSecMinusAmt = new HashMap<String, String>();
					
					Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
					double dblSubSecMinusInvestment =0;
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
						dblSubSecMinusInvestment = 0;
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
						dblSubSecMinusInvestment = uF.parseToDouble(hmSubSecMinusAmt.get(strSectionId));
					}
//					--------------------
					
					double dblSubSecMinActAmt=0;
					for(int i=0; i<al.size(); i++) {
						String strSubSNo = al.get(i);
						if(uF.parseToInt(strSubSNo)>0) {
							Map<String, String> hmInner = hmOuter.get(strSubSNo);
							double dblAppliedAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT_PAID"));
							double dblSubSecLimitAmt = uF.parseToDouble(hmInner.get("SUB_SEC_AMT"));
							strSubSecLimitType = hmInner.get("SUB_SEC_LIMIT_TYPE");
							if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
								dblSubSecLimitAmt = (dblAppliedAmt * dblSubSecLimitAmt) / 100;
							}
							dblSubSecMinActAmt += Math.min(dblAppliedAmt, dblSubSecLimitAmt);
						}
					}
					
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						if(dblInvestment>0 && dblSubSecMinActAmt>0 && dblSubSecMinActAmt>dblSubSecMinusInvestment) {
							double dblMinAmt = dblSubSecMinActAmt - dblSubSecMinusInvestment;
							double dblAmt = dblSubSecLimit - dblInvestment;
							if(dblMinAmt>dblAmt) {
								dblSubSecMinusInvestment += dblAmt;
								dblSecInvestment += Math.min((dblInvestment + dblAmt), dblSubSecLimit);;
							} else {
								dblSubSecMinusInvestment += dblMinAmt;
								dblSecInvestment += Math.min((dblInvestment + dblMinAmt), dblSubSecLimit);;
							}
						} else {
							dblSecInvestment += dblInvestment;
						}
					}
//					System.out.println(strSectionId+" -- dblSecInvestment =====>> "  +dblSecInvestment);
					
					
					if(hmSectionLimitA.containsKey(strSectionId)) {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
						dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					}
					
					if(dblSecInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
						hmInvest.put(strSectionId, ""+dblInvestmentLimit);
					} else {
						hmInvest.put(strSectionId, ""+dblSecInvestment);
					}
					hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
//					hmInvest.put(strSectionId, ""+dblSecInvestment);
//					hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
					
					hmSubSecMinusAmt.put(strSectionId, ""+dblSubSecMinusInvestment);
					hmEmpSubSecMinusAmt.put(rs.getString("emp_id"), hmSubSecMinusAmt);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
					
				} else {
					
					Map<String,String> hmInvest = hmEmpInvestment1.get(rs.getString("emp_id"));
					if(hmInvest==null) hmInvest = new HashMap<String, String>();
					double dblSecInvestment =0;
	//					System.out.println("oldSectionId ===>> "  +oldSectionId);
	//					System.out.println("strSectionId ===>> "  +strSectionId);
					if((oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) && hmInvest.keySet().contains(strSectionId)) {
						dblSecInvestment = 0;
	//						System.out.println("in if dblSecInvestment =====>> "  +dblSecInvestment);
					} else {
						dblSecInvestment = uF.parseToDouble(hmInvest.get(strSectionId));
	//						System.out.println("in else dblSecInvestment =====>> "  +dblSecInvestment);
					}
					if(strSubSecLimitType != null && strSubSecLimitType.equals("%")) {
						dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
					}
					if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
						dblSecInvestment += dblSubSecLimit;
					} else {
						dblSecInvestment += dblInvestment;
					}
	
					if(hmSectionLimitA.containsKey(strSectionId)) {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId+"_CEILING_AMT"));
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					} else {
						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
						dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
						dblInvestmentCeilingLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId+"_CEILING_AMT"));
						if(dblInvestmentCeilingLimit>0) {
							dblInvestmentLimit = dblInvestmentCeilingLimit;
						}
					}
					
					if(dblSecInvestment>=dblInvestmentLimit && dblInvestmentLimit>=0) {
						hmInvest.put(strSectionId, ""+dblInvestmentLimit);
					} else {
						hmInvest.put(strSectionId, ""+dblSecInvestment);
					}
					hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
					
					if(oldSectionId == null || uF.parseToInt(oldSectionId) != uF.parseToInt(strSectionId)) {
						oldSectionId = strSectionId;
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpInvestment1 ===============>> " + hmEmpInvestment1);
			
			
			pst = con.prepareStatement("select * from section_details where under_section in (8,9) and financial_year_start=? and financial_year_end=? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			List<String> chapter1SectionList = new ArrayList<String>();
			List<String> chapter2SectionList = new ArrayList<String>();
			Map<String, String> hmSectionPFApplicable = new HashMap<String, String>();
			while(rs.next()) {
				if(uF.parseToInt(rs.getString("under_section"))==8) {
					chapter1SectionList.add(rs.getString("section_id"));
				} else {
					chapter2SectionList.add(rs.getString("section_id"));
				}
				hmSectionPFApplicable.put(rs.getString("section_id"), rs.getString("is_pf_applicable"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from=? and id.fy_to=? " +
				"and status=true and trail_status=1 and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and isdisplay=true " +
				"and parent_section>0 and emp_id in ("+strEmpId+") order by emp_id"); // and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst ==> "+pst);
			
			Map<String, Map<String, List<Map<String, String>>>> hmEmpSubInvestment = new HashMap<String, Map<String, List<Map<String, String>>>>();
//			Map<String, List<Map<String, String>>> hmSubInvestment = new HashMap<String, List<Map<String, String>>>();	
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			
			while(rs.next()) {
				String strSectionId = rs.getString("parent_section");
				double dblInvestment = rs.getDouble("amount_paid");
				
				Map<String, List<Map<String, String>>> hmSubInvestment =hmEmpSubInvestment.get(rs.getString("emp_id"));
				if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
				
				List<Map<String, String>> alSubInvestment =hmSubInvestment.get(rs.getString("parent_section"));
				if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
				
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("SECTION_ID", rs.getString("parent_section"));
				hm.put("SECTION_NAME", rs.getString("child_section"));
				hm.put("INVESTMENT_ID", rs.getString("investment_id"));
				hm.put("PAID_AMOUNT", ""+dblInvestment);
				hm.put("SUB_SEC_NO", rs.getString("sub_section_no"));
				hm.put("SUB_SECTION_AMOUNT", ""+rs.getDouble("sub_section_amt"));
				hm.put("SUB_SECTION_LIMIT_TYPE", rs.getString("sub_section_limit_type"));
				hm.put("STATUS", rs.getString("status"));
				
				alSubInvestment.add(hm);
				
				hmSubInvestment.put(rs.getString("parent_section"), alSubInvestment);
				hmEmpSubInvestment.put(rs.getString("emp_id"), hmSubInvestment);
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpSubInvestment ==========>> " + hmEmpSubInvestment);
			
			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//				System.out.println(" pst==>"+pst);
			Map hmExemption = new HashMap();
			Map<String, Map<String, List<String>>> hmExemptionDataUnderSection = new HashMap<String, Map<String, List<String>>>();
			while(rs.next()) {
//					hmExemption.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
				hmExemption.put(rs.getString("salary_head_id"), rs.getString("exemption_limit"));
				Map<String, List<String>> hmExemptionData = hmExemptionDataUnderSection.get(rs.getString("under_section"));
				if(hmExemptionData == null) hmExemptionData = new LinkedHashMap<String, List<String>>();
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("exemption_id"));
				innerList.add(rs.getString("exemption_code"));
				innerList.add(rs.getString("exemption_name"));
				innerList.add(rs.getString("exemption_limit"));
				innerList.add(rs.getString("exemption_from"));
				innerList.add(rs.getString("exemption_to"));
				innerList.add(rs.getString("salary_head_id"));
				innerList.add(rs.getString("under_section"));
				hmExemptionData.put(rs.getString("exemption_id"), innerList);
				hmExemptionDataUnderSection.put(rs.getString("under_section"), hmExemptionData);
			}
			rs.close();
			pst.close();
			
			
			/*pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//				System.out.println(" pst==>"+pst);
			double dblInvestmentExemption = 0.0d;
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();*/
			
			Map<String,String> hmEmpIncomeFromOtherSourcesMap = new HashMap<String,String>();
			Map<String,String> hmEmpLessIncomeFromOtherSourcesMap = new HashMap<String,String>();
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id >=13 and sd.section_id <=17 " +
				"and parent_section = 0 and isdisplay=false and financial_year_start=? and financial_year_end=? and emp_id=? group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(strEmpId));
//			System.out.println("pst ========>> " + pst);
			rs = pst.executeQuery();			
			double dblInvestmentIncomeSourcesEmp = 0;		
			while (rs.next()) {
				double dblInvestment = rs.getDouble("amount_paid");
				dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
				
				if(rs.getInt("section_id") == 15 || rs.getInt("section_id") == 16) {
					dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpLessIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpLessIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpIncomeFromOtherSourcesMap ===>> " + hmEmpIncomeFromOtherSourcesMap);
			
			pst = con.prepareStatement("select distinct(pg.emp_id),pg.month,pg.year,pg.paycycle,pg.paid_days,pg.total_days from (" +
				"select max(paycycle) as paycycle,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
				"where financial_year_from_date=? and financial_year_to_date =? and is_paid=true " +
				"and salary_head_id not in ("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				"and earning_deduction='E' and emp_id=? group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id) a,payroll_generation pg " +
				"where a.emp_id=pg.emp_id and a.paycycle=pg.paycycle and pg.emp_id in (select emp_per_id from employee_personal_details " +
				"where is_alive=false and employment_end_date between ? and ?) order by pg.emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmLastPaycycle = new HashMap<String, String>();
			while(rs.next()) {
				hmLastPaycycle.put(rs.getString("emp_id"), rs.getString("emp_id"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_MONTH", rs.getString("month"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_YEAR", rs.getString("year"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_PAYCYCLE", rs.getString("paycycle"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_PAIDDAYS", rs.getString("paid_days"));
				hmLastPaycycle.put(rs.getString("emp_id")+"_TOTALDAYS", rs.getString("total_days"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT count(*) as cnt,emp_id FROM emp_family_members WHERE member_type='CHILD' and emp_id=? group by emp_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
//				System.out.println(" pst==>"+pst);
			Map<String, String> hmEmpChildCnt = new HashMap<String, String>();
			while(rs.next()) {
				hmEmpChildCnt.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as cnt,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg where financial_year_from_date=? and financial_year_to_date =? " +
				"and emp_id=? and is_paid = true and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
//				System.out.println(" pst==>"+pst);
			Map<String, String> hmMonthPaid = new HashMap<String, String>();
			while(rs.next()) {
				hmMonthPaid.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmEmpUnderSection10Map = new HashMap<String,String>();			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, ed.salary_head_id, emp_id from investment_details id, exemption_details ed " +
				"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and under_section in (4,5) " +
				"and exemption_from=? and exemption_to=? and id.salary_head_id>0 and id.parent_section=0 and emp_id=? group by emp_id, ed.salary_head_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(strEmpId));
//			System.out.println("pst========"+pst);
			rs = pst.executeQuery();			
			double dblUnderSection10Emp = 0;	
			dblInvestmentLimit = 0.0d;
			Map<String,Map<String,String>> hmUnderSection10_16Map = new HashMap<String,Map<String,String>>();
			Map<String,Map<String,String>> hmUnderSection10_16PaidMap = new HashMap<String,Map<String,String>>();
			while (rs.next()) {
				String strsalaryheadid = rs.getString("salary_head_id");
				double dblInvestment = rs.getDouble("amount_paid");				
//				if(dblInvestment == 0) {
//					dblInvestment = uF.parseToDouble((String)hmEmpPayrollDetails.get(strsalaryheadid));
//				}
				dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
				
				Map<String, String> hmInner= (Map<String, String>)hmUnderSection10_16Map.get(rs.getString("emp_id"));
				if(hmInner==null) hmInner=new HashMap<String, String>();
				
				double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
				
				hmInner.put(rs.getString("salary_head_id"), ""+dblAllowanceExempt);				
				hmUnderSection10_16Map.put(rs.getString("emp_id"), hmInner);   
				
				Map<String, String> hmInner11 = (Map<String, String>)hmUnderSection10_16PaidMap.get(rs.getString("emp_id"));
				if(hmInner11 == null) hmInner11 = new HashMap<String, String>();
				
				hmInner11.put(rs.getString("salary_head_id"), ""+dblInvestment);				
				hmUnderSection10_16PaidMap.put(rs.getString("emp_id"), hmInner11);
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmEmployerPF = new HashMap<String, String>();
			boolean IsAddEmployerPFInTDSCal = CF.getFeatureManagementStatus(request, uF, F_ADD_EMPLYOER_PF_IN_TDS_CALCLATION);
			if(IsAddEmployerPFInTDSCal) {
				pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? and org_id " +
					"in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, "
					+ "level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id "
					+ "and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strEmpId));
				pst.setInt(4, uF.parseToInt(strEmpId));
				rs = pst.executeQuery();
				Map<String, String> hmEPFPolicy = new HashMap<String, String>(); 
				while (rs.next()) {
					hmEPFPolicy.put("IS_ERPF_CONTRIBUTION", rs.getString("is_erpf_contribution"));
					hmEPFPolicy.put("IS_ERPS_CONTRIBUTION", rs.getString("is_erps_contribution"));
					hmEPFPolicy.put("IS_PF_ADMIN_CHARGES", rs.getString("is_pf_admin_charges"));
					hmEPFPolicy.put("IS_EDLI_ADMIN_CHARGES", rs.getString("is_edli_admin_charges"));
					hmEPFPolicy.put("IS_ERDLI_CONTRIBUTION", rs.getString("is_erdli_contribution"));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select pg.emp_id,sum(erpf_contribution) as erpf_contribution,sum(erps_contribution) as erps_contribution," +
					"sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges " +
					"from emp_epf_details eed,payroll_generation pg where eed.emp_id=pg.emp_id and financial_year_start=? and financial_year_end=? " +
					"and _month=month and financial_year_from_date=? and financial_year_to_date=? and pg.salary_head_id=? and pg.emp_id=? group by pg.emp_id");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, EMPLOYEE_EPF);
				pst.setInt(6, uF.parseToInt(strEmpId));
				rs = pst.executeQuery();
//				System.out.println(" pst==>"+pst);
		 		while(rs.next()) {
					double dblERPF = hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERPF_CONTRIBUTION")) ? rs.getDouble("erpf_contribution") : 0.0d;
					dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERPS_CONTRIBUTION")) ? rs.getDouble("erps_contribution") : 0.0d;
					dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_ERDLI_CONTRIBUTION")) ? rs.getDouble("erdli_contribution") : 0.0d;
					dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_PF_ADMIN_CHARGES")) ? rs.getDouble("pf_admin_charges") : 0.0d;
					dblERPF += hmEPFPolicy!=null && uF.parseToBoolean(hmEPFPolicy.get("IS_EDLI_ADMIN_CHARGES")) ? rs.getDouble("edli_admin_charges") : 0.0d;
					
					hmEmployerPF.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(Math.round(dblERPF)));
				}
				rs.close();
				pst.close();
			}
			
			pst = con.prepareStatement("select count(*) as month,emp_id from (select distinct(month),emp_id from payroll_generation where emp_id=? and financial_year_from_date=? and financial_year_to_date=? group by emp_id,month) a group by emp_id");
			pst.setInt(1,uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery(); 
			Map<String,String> empPaidMonth=new HashMap<String,String>();
			while(rs.next()) {
				empPaidMonth.put(rs.getString("emp_id"), rs.getString("month"));
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as month,emp_id from tds_projections where emp_id=? and fy_year_from=? and fy_year_end=? group by emp_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery(); 
			Map<String,String> empTdsProjectMonth = new HashMap<String,String>();
			while(rs.next()) {
				empTdsProjectMonth.put(rs.getString("emp_id"), rs.getString("month"));
			}
			rs.close();
			pst.close();

			int nTdsPayMonth = 0;
			int nTdsPayMonDiff = 0;
			int nMonthsLeft = 0;
			if(uF.parseToInt(empTdsProjectMonth.get(strEmpId)) > uF.parseToInt(empPaidMonth.get(strEmpId))) {
				nTdsPayMonth = uF.parseToInt(empTdsProjectMonth.get(strEmpId));
				nTdsPayMonDiff = uF.parseToInt(empTdsProjectMonth.get(strEmpId)) - uF.parseToInt(empPaidMonth.get(strEmpId));
			} else {
				nTdsPayMonth = uF.parseToInt(empPaidMonth.get(strEmpId));
				nTdsPayMonDiff = 0;
			}
			nMonthsLeft = (nMonth - nTdsPayMonth) + nTdsPayMonDiff;
			
			ApprovePayroll objApprove = new ApprovePayroll();
			objApprove.session = session;
			objApprove.request = request;
			objApprove.CF = CF;
			
			int nEmpLevelId = CF.getEmpLevelId(strEmpId, request);
			Map<String, String> hmSalaryDetails = CF.getSalaryHeadsMap(con, nEmpLevelId);
			
			 Map<String, String> hmUS10_16InnerPaid = (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
			if(hmUS10_16InnerPaid==null) hmUS10_16InnerPaid = new HashMap<String, String>();
//			System.out.println("hmUS10_16InnerPaid ===>> " + hmUS10_16InnerPaid);
			
//			Map<String, String> hmEmpExemptionsMap = objApprove.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, 0);
			Map<String, String> hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmEmpRentPaidMap = objApprove.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmFixedExemptions = objApprove.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objApprove.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String,String> hmPaidSalaryDetails = hmEmpPaidAmountDetails.get(strEmpId);
			if(hmPaidSalaryDetails==null)hmPaidSalaryDetails = new HashMap<String,String>();
			
			double dblHraSalHeadsAmount = 0;
			for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
				dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
			}
			
			Map<String,String> empsalaryDetailsEPFMap = hmSalaryCal.get(EMPLOYEE_EPF+"");
			double dblEEPF =0;
			if(empsalaryDetailsEPFMap!=null) {
				dblEEPF = CF.calculateEEPF(con, null, uF, salaryGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null);
			}

			double dblBasicDA = dblHraSalHeadsAmount;
			double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
			double dblLTA = uF.parseToDouble(hmTotal.get(LTA+""));

//			double dblHomeLoanTaxExempt = uF.parseToDouble(hmEmpHomeLoanMap.get(strEmpId));
//			hmTaxInner.put("dblHomeLoanTaxExempt", dblHomeLoanTaxExempt+"");
			
			double dblEPRFPaid = uF.parseToDouble(hmEmployerPF.get(strEmpId));
//			System.out.println("dblEPRFPaid ===>> " + dblEPRFPaid);
			if(hmPaidSalaryDetails.containsKey(EMPLOYER_EPF+"")) {
				dblEPRFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYER_EPF+""));
			}
//			System.out.println("dblEPRFPaid after ===>> " + dblEPRFPaid);
			double dblEEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(EMPLOYEE_EPF+""));
			double dblVOLEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(VOLUNTARY_EPF+""));
			double dblEEEPFToBePaid = (nMonthsLeft - nAttendanceApproveMonth) * dblEEPF;
//			dblERPF
			dblEEEPFToBePaid += dblCalAttendacePF;
//			System.out.println("dblHRA ===>> " + dblHRA + " -- dblBasicDA ===>> " + dblBasicDA);
			double dblHRAExemptions = CF.getHRAExemptionCalculation(con, uF, nMonthsLeft, hmPaidSalaryDetails, strFinancialYearStart,
					strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap, nAttendanceApproveMonth, dblHraSalHeadsCalAttendace, dblBasicSalCalAttendace);
//				System.out.println("dblHRAExemptions ===>> " + dblHRAExemptions);
			double dblLTAExemptions = getLTACalculation(con, uF, nMonthsLeft, hmPaidSalaryDetails, strFinancialYearStart,
					strFinancialYearEnd, strEmpId, dblLTA, nAttendanceApproveMonth, dblLTASalCalAttendace);

			hmUS10_16_SalHeadData.put(HRA+"_PAID", "");
			hmUS10_16_SalHeadData.put(HRA+"_EXEMPT", ""+dblHRAExemptions);
			
			hmUS10_16_SalHeadData.put(LTA+"_PAID", hmUS10_16InnerPaid.get(LTA+""));
//			hmUS10_16_SalHeadData.put(LTA+"_PAID", "");
			hmUS10_16_SalHeadData.put(LTA+"_EXEMPT", ""+Math.min(uF.parseToDouble(hmUS10_16InnerPaid.get(LTA+"")), dblLTAExemptions));
			
			double dblEmpPF = dblEEEPFPaid + dblEPRFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;
			hmTaxInner.put("dblEmpPF", dblEmpPF+"");
			
//			double dblExemptions = dblHomeLoanExemtion;
			Set<String> set = hmSalaryDetails.keySet();
			it = set.iterator();
			while (it.hasNext()) {
				String strSalaryHeadId = it.next();
				String strSalaryHeadName = hmSalaryDetails.get(strSalaryHeadId);
				
				if(uF.parseToInt(strSalaryHeadId) == HRA) {
					continue;
				}

				if (hmFixedExemptions.containsKey(strSalaryHeadId)) {
					double dblIndividualExemption = uF.parseToDouble(hmFixedExemptions.get(strSalaryHeadId));
					
					double dblTotalToBePaid = 0;
					if (uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) {
						dblTotalToBePaid = totalPTYear;
//						System.out.println("hmFixedExemptions totalPTYear====>"+totalPTYear);
					} else {
						if(nMonthsLeft > 0 && nMonthsLeft == nAttendanceApproveMonth) {
							dblTotalToBePaid = nMonthsLeft * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
						} else if(nMonthsLeft > 0 && nMonthsLeft > nAttendanceApproveMonth) {
							dblTotalToBePaid = (nMonthsLeft - nAttendanceApproveMonth) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
						} else {
							dblTotalToBePaid = 0.0d;	
						}
						
//						System.out.println("nMonthsLeft====>"+nMonthsLeft+"--nAttendanceApproveMonth====>"+nAttendanceApproveMonth+"--uF.parseToDouble(hmTotal.get(strSalaryHeadId)====>"+uF.parseToDouble(hmTotal.get(strSalaryHeadId)));
						if(nAttendanceApproveMonth > 0) {
							Iterator<String> itAttCal = hmAttendanceSal.keySet().iterator();
							while(itAttCal.hasNext()) {
								String strMonth = itAttCal.next();
								Map<String, Map<String, String>> hmInnerCal = hmAttendanceSal.get(strMonth);
								if(hmInnerCal == null) hmInnerCal = new HashMap<String, Map<String, String>>();
								Iterator<String> it11 = hmInnerCal.keySet().iterator();
								while(it11.hasNext()) {
									String strSalaryId = it11.next();
									if(uF.parseToInt(strSalaryId) == uF.parseToInt(strSalaryHeadId)) {
										Map<String,String> hm = hmInnerCal.get(strSalaryId);
										dblTotalToBePaid += uF.parseToDouble(hm.get("AMOUNT"));
									}
								}
							}
						}
					}

					double dblTotalPaid = uF.parseToDouble(hmPaidSalaryDetails.get(strSalaryHeadId));
					double dblTotalPaidAmount = dblTotalToBePaid+ dblTotalPaid;
					
					if(uF.parseToInt(strSalaryHeadId) == EDUCATION_ALLOWANCE) {
						int nEmpChildCnt = uF.parseToInt(hmEmpChildCnt.get(strEmpId)) > 2 ? 2 : uF.parseToInt(hmEmpChildCnt.get(strEmpId));
						double dblEducationAllowanceLimit = ((uF.parseToDouble((String)hmFixedExemptions.get(EDUCATION_ALLOWANCE+""))/12) * nEmpChildCnt) * (uF.parseToInt(hmMonthPaid.get(strEmpId)) + nMonthsLeft);
						double dblEducationAllowancePaid = dblTotalToBePaid;
						double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
						hmUS10_16_SalHeadData.put(EDUCATION_ALLOWANCE+"_PAID", ""+dblEducationAllowancePaid);
						hmUS10_16_SalHeadData.put(EDUCATION_ALLOWANCE+"_EXEMPT", ""+dblEducationAllowanceExempt);
//						dblExemptions += dblEducationAllowanceExempt;
//						hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+"");
					} else if(uF.parseToInt(strSalaryHeadId) == MEDICAL_ALLOWANCE) { 
						Map<String, String> hmUS10_16Inner = (Map<String, String>)hmUnderSection10_16Map.get(strEmpId);
						if(hmUS10_16Inner==null) hmUS10_16Inner = new HashMap<String, String>();
						double dblMedicalAllowanceExemptLimit = uF.parseToDouble((String)hmUS10_16Inner.get(""+MEDICAL_ALLOWANCE));
//						Map<String, String> hmUS10_16InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
//						if(hmUS10_16InnerPaid==null) hmUS10_16InnerPaid=new HashMap<String, String>();
						
						double dblMedicalAllowancePaid = uF.parseToDouble((String)hmUS10_16InnerPaid.get(""+MEDICAL_ALLOWANCE));
						double dblMedicalAllowanceExempt = Math.min(dblMedicalAllowancePaid, dblMedicalAllowanceExemptLimit);
//						dblExemptions += dblMedicalAllowanceExempt;
						hmUS10_16_SalHeadData.put(MEDICAL_ALLOWANCE+"_PAID", ""+dblMedicalAllowancePaid);
						hmUS10_16_SalHeadData.put(MEDICAL_ALLOWANCE+"_EXEMPT", ""+dblMedicalAllowanceExempt);
//						hmTaxInner.put("dblMedicalAllowanceExempt", dblMedicalAllowanceExempt+"");
						
					} else if(uF.parseToInt(strSalaryHeadId) == CONVEYANCE_ALLOWANCE) {
						double dblConAllLimit = 0.0d;
						if(empEndDate !=null && !empEndDate.trim().equals("") && !empEndDate.trim().equalsIgnoreCase("NULL")) {
							int nMonth1 = uF.parseToInt(hmMonthPaid.get(strEmpId));
							double dblConAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * ((nMonth1+nMonthsLeft) - 1);
							double dblTotalDaysAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) / uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_TOTALDAYS"));
							double dblPaidDaysAmt = dblTotalDaysAmt * uF.parseToDouble(hmLastPaycycle.get(strEmpId+"_PAIDDAYS")); 
							
							dblConAllLimit = dblConAmt + dblPaidDaysAmt;
						} else {
							dblConAllLimit = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * nMonth;
						}
						double dblConveyanceAllowanceLimit = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblConAllLimit));
						double dblConveyanceAllowancePaid = dblTotalPaidAmount;
						double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblConveyanceAllowanceLimit);
						
//						hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
//						dblExemptions += dblConveyanceAllowanceExempt;
						hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_PAID", ""+dblConveyanceAllowancePaid);
						hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_EXEMPT", ""+dblConveyanceAllowanceExempt);
						
					} else if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) { 
//						dblExemptions += dblTotalToBePaid;
//						hmTaxInner.put("dblProfessionalTaxExempt", dblTotalToBePaid+"");
						hmUS10_16_SalHeadData.put(PROFESSIONAL_TAX+"_PAID", "");
						hmUS10_16_SalHeadData.put(PROFESSIONAL_TAX+"_EXEMPT", ""+dblTotalToBePaid);
					} else if(uF.parseToInt(strSalaryHeadId) == LTA) {
					} else {							
					}
				}
			}
			
			double dblIncomeFromOther = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(strEmpId));
			double dblLessIncomeFromOther = uF.parseToDouble(hmEmpLessIncomeFromOtherSourcesMap.get(strEmpId));
			
			hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");
			hmTaxInner.put("hmUS10_16_SalHeadData", hmUS10_16_SalHeadData);
			
			double dblUS10_16Exempt = 0.0d;
			Map<String, String> hmUS10_16Inner = (Map<String, String>)hmUnderSection10_16Map.get(strEmpId);
	        if(hmUS10_16Inner==null) hmUS10_16Inner = new HashMap<String, String>();
	        
//	        Map<String, String> hmUS10_16InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(strEmpId);
//			if(hmUS10_16InnerPaid==null) hmUS10_16InnerPaid=new HashMap<String, String>();
			
			Iterator<String> itUnderSection = hmExemptionDataUnderSection.keySet().iterator();
	        while (itUnderSection.hasNext()) {
	        	String underSectionId = itUnderSection.next();
		        Map<String, List<String>> hmUS10_16ExemptionData = hmExemptionDataUnderSection.get(underSectionId);
		        Iterator<String> itUS10Examption = hmUS10_16ExemptionData.keySet().iterator();
		        while (itUS10Examption.hasNext()) {
					String exemptionId = itUS10Examption.next();
					List<String> innerList = hmUS10_16ExemptionData.get(exemptionId);
					double dblAmtExempt = uF.parseToDouble(hmUS10_16Inner.get(innerList.get(6)));
					if(uF.parseToInt(innerList.get(6)) == HRA || uF.parseToInt(innerList.get(6)) == CONVEYANCE_ALLOWANCE || uF.parseToInt(innerList.get(6)) == EDUCATION_ALLOWANCE || uF.parseToInt(innerList.get(6)) == PROFESSIONAL_TAX || uF.parseToInt(innerList.get(6)) == LTA) {
						dblAmtExempt = uF.parseToDouble(hmUS10_16_SalHeadData.get(innerList.get(6)+"_EXEMPT"));
					}
					if(uF.parseToInt(innerList.get(6)) == 0) {
						dblAmtExempt = uF.parseToDouble(innerList.get(3));
					}
					dblUS10_16Exempt += dblAmtExempt;
		        }
	        }
	        
	        double dblVIA2Exempt = 0.0d;
			double dblVIA1Exempt = 0.0d;
			double dblAddExemptInAdjustedGrossIncome = 0.0d;
			
			dblInvestmentLimit = 0;
			
//			Here we Calculate VI A1
			/**
			 * Change by RAHUL PATIL on 12Sep18 based on Crave Infotech Case found
			 * */
//			System.out.println("hmEmpInvestment(strEmpId) 55 ================>> " + hmEmpInvestment.get(strEmpId));
			Map<String,String> hmInvest = hmEmpInvestment.get(strEmpId);
			if(hmInvest == null) hmInvest = new HashMap<String, String>();
			Iterator<String> it1 = hmInvest.keySet().iterator();
			List<String> alSectionId = new ArrayList<String>();
			while(it1.hasNext()) {
				String strSectionId = it1.next();
				
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						alSectionId.add(strSectionId);
						dblVIA1Invest += dblEmpPF;
					}
					if(dblInvestmentLimit>=0) {
						dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
					}
					if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId))) {
						dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
					}
					dblVIA1Exempt += dblVIA1Invest;
					/*} else {
						dblVIA1Exempt += dblVIA1Invest;
					}*/
//					System.out.println("strSectionId ===>> " + strSectionId + " --- dblVIA1Exempt ===>> " + dblVIA1Exempt + " --- dblInvestmentLimit ===>> " + dblInvestmentLimit);
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest.get(strSectionId));
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						alSectionId.add(strSectionId);
						dblVIA1Invest += dblEmpPF;
					}
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
					if(dblInvestmentLimit>=0) {
						dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
					}
					if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId))) {
						dblAddExemptInAdjustedGrossIncome += dblVIA1Invest;
					}
					dblVIA1Exempt += dblVIA1Invest;
					/*} else {
						dblVIA1Exempt += dblVIA1Invest;
					}*/
				}
			}
			/*if(alSectionId == null || alSectionId.size()==0) {
				dblVIA1Exempt += dblEmpPF;
			}*/
			
//			System.out.println("dblVIA1Exempt ----- =========>> " + dblVIA1Exempt);
			
//			Here we Calculate VI A2 
			/**
			 * Change by RAHUL PATIL on 12Sep18 based on Craveinfotech Case found
			 * */
//			System.out.println("hmEmpInvestment1.get(strEmpId) 55 ================>> " + hmEmpInvestment1.get(strEmpId));
			Map<String,String> hmInvest2 = hmEmpInvestment1.get(strEmpId);
			if(hmInvest2 == null) hmInvest2 = new HashMap<String, String>();
			Iterator<String> it2 = hmInvest2.keySet().iterator();
			while(it2.hasNext()) {
				String strSectionId = it2.next();
				if(uF.parseToBoolean(hmSectionAdjustedGrossIncomeLimitStatus.get(strSectionId))) {
					dblAddExemptInAdjustedGrossIncome += uF.parseToDouble(hmInvest2.get(strSectionId));;
				}
				dblVIA2Exempt += uF.parseToDouble(hmInvest2.get(strSectionId));
			}
			
			/**
			 * Add Reimbursement TDS CTC amount in gross salary
			 * */			
			totalGrossYear += dblReimTDSCTC;
			
			request.setAttribute("totalGrossYear", ""+totalGrossYear);
//			double dblNetTaxableIncome = dblGross1 - dblUS10_16Exempt - dblVIA1Exempt - dblHomeLoanTaxExempt - dblVIA2Exempt;
//			System.out.println("totalGrossYear ===>> " + totalGrossYear);
//			System.out.println("dblIncomeFromOther ===>> " + dblIncomeFromOther+" -- dblLessIncomeFromOther ===>> " + dblLessIncomeFromOther);
//			System.out.println("dblAddExemptInAdjustedGrossIncome ===>> " + dblAddExemptInAdjustedGrossIncome);
//			System.out.println("dblUS10_16Exempt ===>> " + dblUS10_16Exempt);
//			System.out.println("dblVIA1Exempt ===>> " + dblVIA1Exempt);
//			System.out.println("dblVIA2Exempt ===>> " + dblVIA2Exempt);
			double dblAdjustedGrossTotalIncome = (dblPrevOrgGross + totalGrossYear + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther) - dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblLessIncomeFromOther; //- dblHomeLoanTaxExempt
//			System.out.println("dblAdjustedGrossTotalIncome ===>> " + dblAdjustedGrossTotalIncome);
			request.setAttribute("dblAdjustedGrossTotalIncome", ""+dblAdjustedGrossTotalIncome);
			
			//
			pst = con.prepareStatement("select * from section_details where financial_year_start=? and financial_year_end=? and " +
				"section_code not in ('HRA') and isdisplay=true and is_adjusted_gross_income_limit=true "); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			dblInvestmentLimit = 0;
			dblInvestmentEmp = 0;
			Map<String, Map<String, String>> hmSectionwiseSubSecIsAdjustedStatus = new LinkedHashMap<String, Map<String, String>>();
			while(rs.next()) {
				Map<String, String> hmSubSecIsAdjustedStatus = new HashMap<String, String>();
				hmSubSecIsAdjustedStatus.put("UNDER_SECTION", rs.getString("under_section"));
				hmSubSecIsAdjustedStatus.put("SUB_SEC_1_IS_ADJUSTED_STATUS", rs.getString("sub_section_1_is_adjust_gross_income_limit"));
				hmSubSecIsAdjustedStatus.put("SUB_SEC_2_IS_ADJUSTED_STATUS", rs.getString("sub_section_2_is_adjust_gross_income_limit"));
				hmSubSecIsAdjustedStatus.put("SUB_SEC_3_IS_ADJUSTED_STATUS", rs.getString("sub_section_3_is_adjust_gross_income_limit"));
				hmSubSecIsAdjustedStatus.put("SUB_SEC_4_IS_ADJUSTED_STATUS", rs.getString("sub_section_4_is_adjust_gross_income_limit"));
				hmSubSecIsAdjustedStatus.put("SUB_SEC_5_IS_ADJUSTED_STATUS", rs.getString("sub_section_5_is_adjust_gross_income_limit"));
				
				hmSectionwiseSubSecIsAdjustedStatus.put(rs.getString("section_id"), hmSubSecIsAdjustedStatus);
			}
			rs.close();
			pst.close();
//			System.out.println(" hmSectionwiseSubSecIsAdjustedStatus ===>> " + hmSectionwiseSubSecIsAdjustedStatus);
			
			Map<String, List<Map<String, String>>> hmSubInvestment = hmEmpSubInvestment.get(strEmpId);
			if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
			
			Map<String,String> hmInvest11 = new HashMap<String, String>();
			
			Iterator<String> itSubSec = hmSectionwiseSubSecIsAdjustedStatus.keySet().iterator();
			Map<String, Map<String, String>> hmSectionwiseSubSecAdjusted10PerLimitAmt = new HashMap<String, Map<String,String>>();
			while (itSubSec.hasNext()) {
				String strSectionId = itSubSec.next();
				int intUnderSec = 0;
				Map<String, String> hmSubSecIsAdjustedStatus = hmSectionwiseSubSecIsAdjustedStatus.get(strSectionId);
				double dblSecInvestment =0;
				if(hmInvest11.keySet().contains(strSectionId)) {
					dblSecInvestment = 0;
				} else {
					dblSecInvestment = uF.parseToDouble(hmInvest11.get(strSectionId));
				}
				List<Map<String, String>> alSubInvestment = hmSubInvestment.get(strSectionId);
				if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
				Map<String, String> hmSubSecAdjusted10PerLimitAmt = hmSectionwiseSubSecAdjusted10PerLimitAmt.get(strSectionId);
				if(hmSubSecAdjusted10PerLimitAmt==null)hmSubSecAdjusted10PerLimitAmt = new HashMap<String, String>();
				
				for(int i=0; i<alSubInvestment.size(); i++) {
					Map<String, String> hm = alSubInvestment.get(i);
					boolean blnIsAdjustedLimit = false;
					intUnderSec = uF.parseToInt(hm.get("UNDER_SECTION"));
//					hm.put("SECTION_ID", rs.getString("parent_section"));
//					hm.put("SECTION_NAME", rs.getString("child_section"));
//					hm.put("INVESTMENT_ID", rs.getString("investment_id"));
					double dblInvestment = uF.parseToDouble(hm.get("PAID_AMOUNT"));
//					hm.put("SUB_SEC_NO", rs.getString("sub_section_no"));
					double dblSubSecLimit = uF.parseToDouble(hm.get("SUB_SECTION_AMOUNT"));
//					hm.put("SUB_SECTION_LIMIT_TYPE", rs.getString("sub_section_limit_type"));
					if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==1 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_1_IS_ADJUSTED_STATUS"))) {
						blnIsAdjustedLimit = true;
					} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==2 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_2_IS_ADJUSTED_STATUS"))) {
						blnIsAdjustedLimit = true;
					} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==3 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_3_IS_ADJUSTED_STATUS"))) {
						blnIsAdjustedLimit = true;
					} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==4 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_4_IS_ADJUSTED_STATUS"))) {
						blnIsAdjustedLimit = true;
					} else if(uF.parseToInt(hm.get("SUB_SEC_NO")) ==5 && uF.parseToBoolean(hmSubSecIsAdjustedStatus.get("SUB_SEC_5_IS_ADJUSTED_STATUS"))) {
						blnIsAdjustedLimit = true;
					}
					
					if(blnIsAdjustedLimit) {
						double dbl10PerOfAdjustedIncome = (dblAdjustedGrossTotalIncome * 10) / 100;
//						System.out.println(dblSubSecLimit+ " -- dbl10PerOfAdjustedIncome ===>> " + dbl10PerOfAdjustedIncome);
						if(hm.get("SUB_SECTION_LIMIT_TYPE") != null && hm.get("SUB_SECTION_LIMIT_TYPE").equals("%")) {
							dblSubSecLimit = (dbl10PerOfAdjustedIncome * dblSubSecLimit) / 100;
						}
//						System.out.println("dblSubSecLimit ===>> " +dblSubSecLimit +" -- dblInvestment ===>> " + dblInvestment);
						if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
							dblSecInvestment += dblSubSecLimit;
						} else {
							dblSecInvestment += dblInvestment;
						}
						hmSubSecAdjusted10PerLimitAmt.put(hm.get("SUB_SEC_NO"), ""+dblSubSecLimit);
					} else {
						if(hm.get("SUB_SECTION_LIMIT_TYPE") != null && hm.get("SUB_SECTION_LIMIT_TYPE").equals("%")) {
							dblSubSecLimit = (dblInvestment * dblSubSecLimit) / 100;
						}
						if(dblSubSecLimit>0 && dblInvestment>dblSubSecLimit) {
							dblSecInvestment += dblSubSecLimit;
						} else {
							dblSecInvestment += dblInvestment;
						}
					}
				}
				
				hmSectionwiseSubSecAdjusted10PerLimitAmt.put(strSectionId, hmSubSecAdjusted10PerLimitAmt);
				
				hmInvest11.put(strSectionId, ""+dblSecInvestment);
				if(intUnderSec == 8) {
					hmEmpInvestment.put(rs.getString("emp_id"), hmInvest11);
				} else if(intUnderSec == 9) {
					hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest11);
				}
				
			}
//			System.out.println("hmSectionwiseSubSecAdjusted10PerLimitAmt ===>> " +hmSectionwiseSubSecAdjusted10PerLimitAmt);
			
			request.setAttribute("hmSectionwiseSubSecAdjusted10PerLimitAmt", hmSectionwiseSubSecAdjusted10PerLimitAmt);
//			System.out.println("hmInvest11 ===>> " + hmInvest11);
			
			double dblVIA1ExemptIsAdjustedLimit = 0.0d;
			
			Iterator<String> it11 = hmInvest11.keySet().iterator();
			while(it11.hasNext()) {
				String strSectionId = it11.next();
				
				if(hmSectionLimitA.containsKey(strSectionId)) {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						alSectionId.add(strSectionId);
						dblVIA1Invest += dblEmpPF;
					}
//					System.out.println("strSectionId ===>> " + strSectionId + " -- dblInvestmentLimit ===>> " + dblInvestmentLimit);
					if(dblInvestmentLimit>=0) {
						dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
					}
					dblVIA1ExemptIsAdjustedLimit += dblVIA1Invest;
				} else {
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					double dblVIA1Invest = uF.parseToDouble(hmInvest11.get(strSectionId));
					if(uF.parseToBoolean(hmSectionPFApplicable.get(strSectionId))) {
						alSectionId.add(strSectionId);
						dblVIA1Invest += dblEmpPF;
					}
					dblInvestmentLimit = dblVIA1Invest * dblInvestmentLimit / 100;
					if(dblInvestmentLimit>=0) {
						dblVIA1Invest = Math.min(dblVIA1Invest, dblInvestmentLimit);
					}
					dblVIA1ExemptIsAdjustedLimit += dblVIA1Invest;
				}
			}
			if(alSectionId == null || alSectionId.size()==0) {
				dblVIA1Exempt += dblEmpPF;
			}
//			System.out.println("dblVIA1ExemptIsAdjustedLimit ===>> " + dblVIA1ExemptIsAdjustedLimit);
//			System.out.println("totalGrossYear ===>> " + totalGrossYear);
//			System.out.println("dblAddExemptInAdjustedGrossIncome ===>> " + dblAddExemptInAdjustedGrossIncome);
//			System.out.println("dblVIA1Exempt ===>> " + dblVIA1Exempt);
			//double dblNetTaxableIncome = (dblPrevOrgGross + totalGrossYear + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
//			Map<String, String> hmEmpSlabMap = CF.getEmpSlabMap(con, CF);
			String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
			int slabType = uF.parseToInt(strSlabType);
			//System.out.println("strSlabType:"+slabType);
			double dblNetTaxableIncome  = 0.0d;
			if(slabType == 0)
			 dblNetTaxableIncome = (dblPrevOrgGross + totalGrossYear + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
			if(slabType == 1)
				dblNetTaxableIncome = dblPrevOrgGross + totalGrossYear;
			
			
//			System.out.println(" dblNetTaxableIncome ===>> " + dblNetTaxableIncome);
			
			int countBug = 0;
			double dblTotalTDSPayable = 0.0d;
			double dblUpperDeductionSlabLimit = 0;
			double dblLowerDeductionSlabLimit = 0;
			double dblTotalNetTaxableSalary = 0; 
			do {
				pst = con.prepareStatement(selectDeduction);
				pst.setDouble(1, uF.parseToDouble(hmEmpAgeMap.get(strEmpId)));
				pst.setDouble(2, uF.parseToDouble(hmEmpAgeMap.get(strEmpId)));
				pst.setString(3, hmEmpGenderMap.get(strEmpId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDouble(6, dblNetTaxableIncome);
				pst.setDouble(7, dblUpperDeductionSlabLimit);
				pst.setInt(8, slabType);
				rs = pst.executeQuery();
				double dblDeductionAmount = 0;
				String strDeductionType = null;
				if(rs.next()) {
					dblDeductionAmount = rs.getDouble("deduction_amount");
					strDeductionType = rs.getString("deduction_type");
					dblUpperDeductionSlabLimit = rs.getDouble("_to");
					dblLowerDeductionSlabLimit = rs.getDouble("_from");
				}
				rs.close();
				pst.close();
				
				if(countBug==0) {
					dblTotalNetTaxableSalary = dblNetTaxableIncome;
				}
				
//				System.out.println("dblNetTaxableIncome ===>> " + dblNetTaxableIncome);
//				System.out.println("dblDeductionAmount ===>> " + dblDeductionAmount);
//				System.out.println("dblUpperDeductionSlabLimit ===>> " + dblUpperDeductionSlabLimit);
//				System.out.println("dblLowerDeductionSlabLimit ===>> " + dblLowerDeductionSlabLimit);
				
				if(dblNetTaxableIncome >= dblUpperDeductionSlabLimit) {
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
				} else {
					if(countBug==0) {
						dblTotalNetTaxableSalary = dblNetTaxableIncome - dblLowerDeductionSlabLimit;
					}
					
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
				}
				
				dblTotalNetTaxableSalary = dblNetTaxableIncome - dblUpperDeductionSlabLimit;
//				System.out.println("=====dblTotalNetTaxableSalary========="+dblTotalNetTaxableSalary);
				
				if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
				countBug++;
				
			} while(dblTotalNetTaxableSalary>0);
			
//			System.out.println("=====dblTotalTDSPayable========="+dblTotalTDSPayable);
			dblTotalTDSPayable = dblTotalTDSPayable - dblPrevOrgTDSAmount;
//			System.out.println("=====dblTotalTDSPayable========="+dblTotalTDSPayable);
			
			hmTaxInner.put("TAX_LIABILITY", dblTotalTDSPayable+"");
			
			double dblMaxTaxableIncome = uF.parseToDouble(innereducationCessMp.get("MAX_TAX_INCOME"));
			double dblRebateAmt = uF.parseToDouble(innereducationCessMp.get("REBATE_AMOUNT"));
			double dblRebate = 0;
			if(dblNetTaxableIncome <= dblMaxTaxableIncome && dblTotalTDSPayable <= dblMaxTaxableIncome) {
				if(dblTotalTDSPayable >= dblRebateAmt) {
					dblRebate = dblRebateAmt;
				} else if(dblTotalTDSPayable > 0 && dblTotalTDSPayable < dblRebateAmt) {
					dblRebate = dblTotalTDSPayable;
				}
			}
			
			dblTotalTDSPayable = dblTotalTDSPayable - dblRebate;
			
			hmTaxInner.put("TAX_REBATE", dblRebate+"");
			
			double dblCess1=uF.parseToDouble(innereducationCessMp.get(O_EDUCATION_CESS));
			double dblCess1Amount = dblTotalTDSPayable * (dblCess1/100);
			hmTaxInner.put("CESS1", dblCess1+"");
			hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
			
			double dblCess2=uF.parseToDouble(innereducationCessMp.get(O_STANDARD_CESS));
			double dblCess2Amount = dblTotalTDSPayable * (dblCess2/100);
			hmTaxInner.put("CESS2", dblCess2+"");
			hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
			
			dblTotalTDSPayable += (dblCess1Amount + dblCess2Amount);
			
			hmTaxInner.put("TOTAL_TAX_LIABILITY", ""+dblTotalTDSPayable); 
//			System.out.println("1 dblTotalTDSPayable====>"+dblTotalTDSPayable);
			
			dblTotalTDSPayable = dblTotalTDSPayable - totalTDSYearPaid;
			
			hmTaxInner.put("TOTAL_TDS_PAID", ""+totalTDSYearPaid); 
			
			endFlag = false;
			startFlag = false;
			int nMonthRemain = 0;
			for(int i=0; i<alMonth.size(); i++) {
				
//					if(isJoinDateBetween && empStartMonth!=null && empStartMonth.equals((String)alMonth.get(i).trim())) {
				if(isJoinDateBetween && uF.parseToInt(empStartMonth)==uF.parseToInt((String)alMonth.get(i).trim())) {
					startFlag = true;
				}
				
				if(isJoinDateBetween && !startFlag) {
					continue;
				}
				
				if(isEndDateBetween && endFlag) {
					continue;
				}
				
				if(hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+(String)alMonth.get(i))!=null) {
					
				} else if(hmEmpSalaryTotal.get(strEmpId+"_"+(String) alMonth.get(i))!=null) {
					
				} else if(hmTDSProjectedEmp.get(strEmpId+"_"+(String)alMonth.get(i))!=null) {
					
				} else{
					nMonthRemain++;
				}
				cal.add(Calendar.MONTH, 1);
				
//					if(isEndDateBetween && empEndMonth!=null && empEndMonth.equals((String)alMonth.get(i))) {
				if(isEndDateBetween && uF.parseToInt(empEndMonth) == uF.parseToInt((String)alMonth.get(i).trim())) {
					endFlag = true;
				}
			}
			
			double dblTDS = nMonthRemain > 0 ? (dblTotalTDSPayable/nMonthRemain) : 0;
//			System.out.println("dblTotalTDSPayable====>"+dblTotalTDSPayable);
//			System.out.println("totalTDSYearPaid==>"+totalTDSYearPaid);
//			System.out.println("dblTDS====>"+dblTDS);
//			System.out.println("nMonthsLeft====>"+nMonthsLeft);
//			System.out.println("totalTDSYearPaid=====>"+totalTDSYearPaid);
//			System.out.println("nMonthRemain=====>"+nMonthRemain);
			
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat((cal.get(Calendar.MONTH) + 1)+"", "MM", "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			if(dblTDS<0) {
				dblTDS = 0.0d;
			}
			
			double dblTotal = 0;
			double dblGrossTotal = 0;
			boolean isProjectedMonth = false;
			double dblTDS1 = 0;
			endFlag = false;
			startFlag = false;
			Map<String, String> hmTDSRemainMonth = new LinkedHashMap<String, String>();
			Map<String, String> hmTDSEmp = new HashMap<String, String>();
			Map<String, String> hmTDSPaidEmp = new HashMap<String, String>();			
			Map<String, String> hmTDSProjectedEmp1 = new HashMap<String, String>();
			for(int i=0; i<alMonth.size(); i++) {
				if(isJoinDateBetween && uF.parseToInt(empStartMonth)==uF.parseToInt((String)alMonth.get(i).trim())) {
					startFlag = true;
				}
				
				if(isJoinDateBetween && !startFlag) {
					continue;
				}
				
				if(isEndDateBetween && endFlag) {
					continue;
				}
				
//					System.out.println("(String)alMonth.get(i)=====>"+(String)alMonth.get(i));
//					System.out.println("hmEmpTDSPaidAmountDetails=====>"+hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+uF.parseToInt((String)alMonth.get(i))));
//					System.out.println("hmTDSProjectedEmp=====>"+hmTDSProjectedEmp.get(strEmpId+"_"+(String)alMonth.get(i)));
				if(hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+(String)alMonth.get(i))!=null) {
					dblTDS1 = uF.parseToDouble((String)hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+(String)alMonth.get(i)));
					dblTotal += dblTDS1;
					hmTDSPaidEmp.put(strEmpId+"_"+(String)alMonth.get(i), uF.formatIntoTwoDecimal(dblTDS1));
				} else if(hmEmpSalaryTotal.get(strEmpId+"_"+(String)alMonth.get(i))!=null) {
					
				}else if(hmTDSProjectedEmp.get(strEmpId+"_"+(String)alMonth.get(i))!=null) {
					hmTDSProjectedEmp1.put(strEmpId+"_"+(String)alMonth.get(i), hmTDSProjectedEmp.get(strEmpId+"_"+(String)alMonth.get(i)));
					hmTDSEmp.put(strEmpId+"_"+(String)alMonth.get(i), hmTDSProjectedEmp.get(strEmpId+"_"+(String)alMonth.get(i)));
				}else{
					dblTDS1 = dblTDS;
//					System.out.println("dblTDS=====>"+dblTDS+"===dblTDS1=====>"+dblTDS1);
					hmTDSRemainMonth.put(uF.getMonth(uF.parseToInt((String)alMonth.get(i))), uF.formatIntoTwoDecimalWithOutComma(dblTDS1));
					
					hmTDSEmp.put(strEmpId+"_"+(String)alMonth.get(i), uF.formatIntoTwoDecimalWithOutComma(dblTDS1));
				}
				cal.add(Calendar.MONTH, 1);
				
//					if(isEndDateBetween && empEndMonth!=null && empEndMonth.equals((String)alMonth.get(i))) {
				if(isEndDateBetween && uF.parseToInt(empEndMonth) == uF.parseToInt((String)alMonth.get(i).trim())) {
					endFlag = true;
				}
			}
//			System.out.println("totalGrossYear====>"+totalGrossYear);
//			System.out.println("hmTDSRemainMonth====>"+hmTDSRemainMonth);
			request.setAttribute("alSalaryHead", alSalaryHead);
			request.setAttribute("hmEmpSalaryHeadPaidAmt", hmEmpSalaryHeadPaidAmt);
			request.setAttribute("hmProjectSalAmt", hmProjectSalAmt);
			request.setAttribute("hmTaxInner", hmTaxInner);
			
			request.setAttribute("hmEmpInvestment", hmEmpInvestment);
			request.setAttribute("hmEmpSubInvestment", hmEmpSubInvestment);  
			request.setAttribute("hmSectionMap", hmSectionMap);
			request.setAttribute("hmEmpInvestment1", hmEmpInvestment1);
			request.setAttribute("hmUnderSection10_16Map", hmUnderSection10_16Map);
			request.setAttribute("hmUnderSection10_16PaidMap", hmUnderSection10_16PaidMap);
			request.setAttribute("hmExemptionDataUnderSection", hmExemptionDataUnderSection);
			
			request.setAttribute("chapter1SectionList", chapter1SectionList);
			request.setAttribute("chapter2SectionList", chapter2SectionList);
			request.setAttribute("hmSectionPFApplicable", hmSectionPFApplicable);
			
			request.setAttribute("hmEmpActualInvestment", hmEmpActualInvestment);
			request.setAttribute("hmEmpActualInvestment1", hmEmpActualInvestment1);
			request.setAttribute("hmSectionLimitA", hmSectionLimitA);
			request.setAttribute("hmSectionLimitP", hmSectionLimitP);
			
			request.setAttribute("hmTDSRemainMonth", hmTDSRemainMonth);
			
			request.setAttribute("hmTDSEmp1", hmTDSEmp);
			request.setAttribute("hmTDSPaidEmp1", hmEmpTDSPaidAmountDetails);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public double getLTACalculation(Connection con, UtilityFunctions uF, int nMonthsLeft, Map<String, String> hmPaidSalaryDetails,
			String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, double dblLTA, 
			int nAttendanceApproveMonth, double dblLTASalCalAttendace) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblLTAExemption = 0;
		try {
	
			String strLTAPaidAmount = hmPaidSalaryDetails.get(LTA + "");
			// System.out.println("nMonthsLeft=="+nMonthsLeft);
			double dblLTAToBePaidAmount = (nMonthsLeft - nAttendanceApproveMonth) * dblLTA;
			dblLTAToBePaidAmount += dblLTASalCalAttendace;
	
			double dblTotalLTAAmount = uF.parseToDouble(strLTAPaidAmount) + dblLTAToBePaidAmount;
	
			dblLTAExemption = dblTotalLTAAmount;
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return dblLTAExemption;
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

	public List<String> getStrEmpIds() {
		return strEmpIds;
	}

	public void setStrEmpIds(List<String> strEmpIds) {
		this.strEmpIds = strEmpIds;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getStrPaycycleDuration() {
		return strPaycycleDuration;
	}

	public void setStrPaycycleDuration(String strPaycycleDuration) {
		this.strPaycycleDuration = strPaycycleDuration;
	}

	public String[] getSalary_head_id() {
		return salary_head_id;
	}

	public void setSalary_head_id(String[] salary_head_id) {
		this.salary_head_id = salary_head_id;
	}

	public String[] getSalary_head_value() {
		return salary_head_value;
	}

	public void setSalary_head_value(String[] salary_head_value) {
		this.salary_head_value = salary_head_value;
	}

	public String[] getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(String[] isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String[] getHideIsDisplay() {
		return hideIsDisplay;
	}

	public void setHideIsDisplay(String[] hideIsDisplay) {
		this.hideIsDisplay = hideIsDisplay;
	}

	public String[] getEmp_salary_id() {
		return emp_salary_id;
	}

	public void setEmp_salary_id(String[] emp_salary_id) {
		this.emp_salary_id = emp_salary_id;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getStrArearName() {
		return strArearName;
	}

	public void setStrArearName(String strArearName) {
		this.strArearName = strArearName;
	}
	
}



class CalculateAndGetArrearAmountRunnable implements IConstants {

	CalculateAndGetArrearAmount objCalArrearPay;

	Map<String, String> hmTotal;
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


	public CalculateAndGetArrearAmountRunnable(CalculateAndGetArrearAmount objCalArrearPay, Connection con, UtilityFunctions uF, CommonFunctions CF, String strFinancialYearStart,
			String strFinancialYearEnd, String[] strApprovePayCycle, Map<String, String> hmEmpStateMap, Map<String, Map<String, String>> hmCurrencyDetails,
			Map<String, String> hmEmpCurrency, Map<String, String> hmVariables, HttpServletRequest request, HttpServletResponse response, Map<String, String> hmOtherTaxDetails, 
			Map<String, String> hmEmpLevelMap, String strDomain, Map<String, Map<String, String>> hmArearAmountMap, Map<String, String> hmAnnualVariables,
			Map<String, List<Map<String, String>>> hmEmpArrear, Map<String, Map<String, String>> hmArrearCalSalary, Map<String, List<String>> hmArrearEarningHead, 
			Map<String, List<String>> hmArrearDeductionHead, Map<String, Map<String, String>> hmArrearEmployeePF, Map<String, Map<String, String>> hmArrearEmployerPF, 
			Map<String, Map<String, String>> hmArrearEmployerESI,Map<String, Map<String, String>> hmArrearEmployeeLWF) {
		this.objCalArrearPay = objCalArrearPay;
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
	}

	public void setData(Map<String, String> hmTotal, String strEmpId, double dblTotal, String strOrgId) {
		this.hmTotal = hmTotal;
		this.strEmpId = strEmpId;
		this.dblTotal = dblTotal;
		this.strOrgId = strOrgId;

	}

	public void run1() {
		if (hmTotal != null && hmTotal.containsKey(EMPLOYEE_EPF + "")) {
//			System.out.println("insert epf for "+strEmpId);
			objCalArrearPay.calculateEEPF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, strApprovePayCycle[1],
				strApprovePayCycle[2], true, hmArearAmountMap, hmEmpArrear, hmArrearEmployeePF);
			objCalArrearPay.calculateERPF(con, CF, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1],
				strApprovePayCycle[2], true, hmArearAmountMap, hmEmpArrear, hmArrearEmployerPF);
		}

		if (hmTotal != null && hmTotal.containsKey(EMPLOYEE_ESI + "")) {
			objCalArrearPay.calculateEESI(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[0],
				strApprovePayCycle[2], hmEmpStateMap, hmVariables, true, hmAnnualVariables, strApprovePayCycle[1], hmEmpArrear, hmArrearEmployerESI);
		}

		if (hmTotal != null && hmTotal.containsKey(EMPLOYEE_LWF + "")) {
			objCalArrearPay.calculateELWF(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1],
				strApprovePayCycle[2], hmEmpStateMap, hmVariables, true, hmAnnualVariables, hmEmpArrear, hmArrearEmployeeLWF, strOrgId);
		}

		if (hmTotal != null && hmTotal.containsKey(TDS + "")) {
			objCalArrearPay.calculateETDS(con, strDomain, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1],
				strApprovePayCycle[2], hmEmpStateMap, hmVariables, true, hmOtherTaxDetails, hmEmpLevelMap, hmAnnualVariables);
		}
	}
}