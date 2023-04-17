package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ViewEmpTDSProjection extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public CommonFunctions CF;
	public String strUserType;
	public String strSessionEmpId;

	private String strEmpId;
	private String strFinancialYearStart;
	
	private String strFinancialYearEnd;
//	private Map<String, String> hmCurrSalHeadsAndAmt;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());

		if(uF.parseToInt(getStrEmpId()) > 0 && getStrFinancialYearStart() !=null && !getStrFinancialYearStart().trim().equals("") && !getStrFinancialYearStart().trim().equalsIgnoreCase("NULL")
				&& getStrFinancialYearEnd() !=null && !getStrFinancialYearEnd().trim().equals("") && !getStrFinancialYearEnd().trim().equalsIgnoreCase("NULL")) {
			getEmpTDSProjection(uF);
		}

		return LOAD;
	}
	
	public void getEmpTDSProjection(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
//			Start Dattatray Date:22-10-21
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
			
			String dbMonth = hmFeatureUserTypeId.get(F_TDS_CALCULATION_ON_IT_DECLARATION_SUBMISSION_ONLY+"_USER_IDS")!=null ? hmFeatureUserTypeId.get(F_TDS_CALCULATION_ON_IT_DECLARATION_SUBMISSION_ONLY+"_USER_IDS").get(0): "";
			
			StringBuilder strQuery = new StringBuilder();
			String strFYStartMonth = uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "MM");
			String strFYStartYear = uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "yyyy");
			
			String strFYEndMonth = uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT, "MM");
			String strFYEndYear = uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT, "yyyy");
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
			
			java.util.Date dateCurrent =new java.util.Date();
			java.util.Date limitMonthYear = null;
			
//			System.out.println("DB Month : "+uF.parseToInt(dbMonth));
//			System.out.println("Current Month and Year -> "+sdf.format(dateCurrent));
//			System.out.println("DB Month & F Year :"+dbMonth+"-"+strFYStartYear);
			if(uF.parseToBoolean(hmFeatureStatus.get(F_TDS_CALCULATION_ON_IT_DECLARATION_SUBMISSION_ONLY))) {
//				System.out.println("IF CONDITION");
				if (APRIL <= uF.parseToInt(dbMonth) && DECEMBER >= uF.parseToInt(dbMonth)) {
					limitMonthYear = sdf.parse(dbMonth+"-"+strFYStartYear);
				} else if (JANUARY <= uF.parseToInt(dbMonth) && MARCH >= uF.parseToInt(dbMonth)) {
					limitMonthYear = sdf.parse(dbMonth+"-"+strFYEndYear);
				
				}
//				System.out.println("dateCurrent : "+dateCurrent);
//				System.out.println("limitMonthYear : "+limitMonthYear);
				if (dateCurrent.before(limitMonthYear) || dateCurrent.equals(limitMonthYear)) {
					strQuery.append(" (status = true OR status = false) and denied_by IS NULL ");
				}else {
					strQuery.append(" status = true ");
				}
			} else {
//				System.out.println("ELSE CONDITION");
				strQuery.append(" status = true ");
			}
//			End Dattatray Date:22-10-21
			
//			  java.util.Date dateCurrent =new java.util.Date(); java.util.Date
//			  dateFYStart = sdf.parse(dbMonth+"-"+strFYStartYear);
//			  java.util.Date dateFYEnd = sdf.parse(dbMonth+"-"+strFYEndYear);
//			  
//			  System.out.println("DB Month : "+uF.parseToInt(dbMonth));
//			  System.out.println("Current Month and Year -> "+sdf.format(
//			  dateCurrent));
//			  System.out.println("DB Month & F Year :"+dbMonth+"-"+
//			  strFYStartYear);
//			  
//			  if(uF.parseToBoolean(hmFeatureStatus.get(
//			  F_TDS_CALCULATION_ON_IT_DECLARATION_SUBMISSION_ONLY))) { if
//			  (APRIL <= uF.parseToInt(dbMonth) && DECEMBER >=
//			  uF.parseToInt(dbMonth)) { if (dateFYStart.after(dateCurrent)) {
//			  System.out.println("After"); strQuery.append(" status = true ");
//			  } else if (dateCurrent.before(dateFYStart)) {
//			  System.out.println("Before"); strQuery.
//			  append(" (status = true OR status = false) and denied_by IS NULL "
//			  ); } else { System.out.println("Equals"); strQuery.
//			  append(" (status = true OR status = false) and denied_by IS NULL "
//			  ); } } else if (JANUARY <= uF.parseToInt(dbMonth) && MARCH >=
//			  uF.parseToInt(dbMonth)) { if (dateCurrent.after(dateFYEnd)) {
//			  strQuery.append(" status = true "); } else if
//			  (dateCurrent.before(dateFYEnd)) { strQuery.
//			  append(" (status = true OR status = false) and denied_by IS NULL "
//			  ); } else if (dateCurrent.equals(dateFYEnd)) {
//			  strQuery.append(" status = true "); } } }else {
//			  strQuery.append(" status = true "); }
//			 
			
//			System.out.println("strQuery : "+strQuery);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			for(int i=0; i<12;i++) {
				String strDate = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
				alMonth.add(uF.getDateFormat(strDate, DATE_FORMAT, "MM"));
				cal.add(Calendar.MONTH, 1);
			}
			
//			System.out.println("alMonth ===>> " + alMonth);
			
			String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, getStrFinancialYearStart(), getStrFinancialYearEnd());
			int slabType = uF.parseToInt(strSlabType);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			String strCurrId = hmEmpCurrency.get(getStrEmpId());
			Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
			if (hmCurrencyInner == null) hmCurrencyInner = new HashMap<String, String>();
			String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
			request.setAttribute("strCurrSymbol", strCurrSymbol);
			
			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
			
			String strEmpGender = CF.getEmpGender(con, uF, getStrEmpId());
			
			String empStartMonth = null;
			String empJoiningYr = null;
			String empJoinDate = null;
			boolean isJoinDateBetween = false;
			String empEndMonth = null;
			String empEndDate = null;
			boolean isEndDateBetween = false;
			String strSalCalStatus = null;
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
			pst.setInt(1,uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				
				if(rs.getString("joining_date") !=null && !rs.getString("joining_date").trim().equals("") && !rs.getString("joining_date").trim().equalsIgnoreCase("")) {
					empJoinDate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT);
					if(empJoinDate !=null && !empJoinDate.trim().equals("") && !empJoinDate.trim().equalsIgnoreCase("NULL")) {
						isJoinDateBetween = uF.isDateBetween(uF.getDateFormatUtil(getStrFinancialYearStart(), DATE_FORMAT), uF.getDateFormatUtil(getStrFinancialYearEnd(), DATE_FORMAT), uF.getDateFormatUtil(empJoinDate, DATE_FORMAT));
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
						isEndDateBetween = uF.isDateBetween(uF.getDateFormatUtil(getStrFinancialYearStart(), DATE_FORMAT), uF.getDateFormatUtil(getStrFinancialYearEnd(), DATE_FORMAT), uF.getDateFormatUtil(empEndDate, DATE_FORMAT));
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
			pst.setInt(1,uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			System.out.println(" pst==>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmExEmp = new HashMap<String, String>();
			while(rs.next()) {
				hmExEmp.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
			Map<String, Map<String, String>> hmEmpTDSReimbCTC = CF.getEmpTDSReimbursementCTCAmount(con, CF, uF, getStrEmpId(), getStrFinancialYearStart(),getStrFinancialYearEnd());
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

			String strLevelId = CF.getEmpLevelId(con, getStrEmpId());
			Map<String, Map<String, String>> hmEmpSalaryTotal = new HashMap<String, Map<String, String>>();
			Map<String, String> hmEmpSalaryHeadPaidAmt = new HashMap<String, String>();
//			Map<String, String> hmIsPaidEmp = new HashMap<String, String>();
			Map<String, String> hmEmpTDSPaidAmountDetails=new HashMap<String, String>();
			List<String> alSalaryHeadId = new ArrayList<String>();
			// Created By dattatray Date : 03-06-22 Note : and is_paid=true added in query
			// Created By dattatray Date : 10-06-22 Note : and is_paid=true removed in query
			pst = con.prepareStatement("select * from payroll_generation where emp_id=? and financial_year_from_date=? " +
					"and financial_year_to_date=? order by paycycle");
			pst.setInt(1,uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			/*if(uF.parseToInt(strEmpId)==54){
				System.out.println(" TDS pst====>"+pst);
			}*/
			
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
					/*if(uF.parseToInt(getStrEmpId())==320){
						System.out.println("VETDSP/295--dblGross="+hmEmpSalary.get("GROSS")+"---rs.getString(amount)="+rs.getString("amount")+"---dblReimbursement="+dblReimbursement);
						System.out.println("VETDSP/295--dblGross="+dblGross);
					}*/
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
			
//			System.out.println("hmEmpTDSPaidAmountDetails : "+hmEmpTDSPaidAmountDetails);
			double dblPrevOrgGross = 0.0d;
			double dblPrevOrgTDSAmount = 0.0d;
			Map<String, String> hmPrevOrgTDSDetails = new HashMap<String, String>();
			if(isJoinDateBetween) {
				pst = con.prepareStatement("select * from prev_earn_deduct_details where financial_start=? and financial_end=? and emp_id=?");
				pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getStrEmpId()));
				rs = pst.executeQuery();
				if(rs.next()) {
					dblPrevOrgGross = rs.getDouble("gross_amount");
					dblPrevOrgTDSAmount = rs.getDouble("tds_amount");
	
					hmPrevOrgTDSDetails.put(rs.getString("emp_id")+"_GROSS_AMT", ""+rs.getDouble("gross_amount"));
					hmPrevOrgTDSDetails.put(rs.getString("emp_id")+"_FORM16_DOC", rs.getString("document_name"));
					hmPrevOrgTDSDetails.put(rs.getString("emp_id")+"_TDS_AMT_"+uF.zero((uF.parseToInt(empStartMonth)-1)), ""+rs.getDouble("tds_amount"));
					hmPrevOrgTDSDetails.put(rs.getString("emp_id")+"_TDS_AMT", ""+rs.getDouble("tds_amount"));
				}
				rs.close();
				pst.close();
				hmPrevOrgTDSDetails.put(getStrEmpId()+"_JOINING_MONTH", empStartMonth);
				hmPrevOrgTDSDetails.put(getStrEmpId()+"_JOINING_YEAR", empJoiningYr);
			}
			
			request.setAttribute("hmPrevOrgTDSDetails", hmPrevOrgTDSDetails);
//			System.out.println("ViewEmpTDSProjection hmPrevOrgTDSDetails ===>> " + hmPrevOrgTDSDetails);
			
			/**
			 * Calculate projected Salary
			 * */
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
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
			
//			System.out.println("getStrFinancialYearStart ===>> "+ getStrFinancialYearStart() + " -- getHmCurrSalHeadsAndAmt() =======>> " + getHmCurrSalHeadsAndAmt());
			Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmSalaryCal = CF.getSalaryCalculation(con,hmInnerisDisplay,uF.parseToInt(getStrEmpId()), 30, 0, 0, 30, 0, 0, strLevelId, uF, CF,uF.getDateFormatUtil(uF.getCurrentDate(CF.getStrTimeZone()), DATE_FORMAT),hmSalInner, null, strSalCalStatus);
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
			pst.setDate(1,uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(3,uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmTDSProjectedEmp.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<10?"0"+rs.getString("month"):rs.getString("month")), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
			}
			rs.close();
			pst.close();
			
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			String strCurrMonth = uF.getDateFormat(currDate, DATE_FORMAT, "MM");
			Map<String, Map<String, String>> hmIncrementArearAmountMap = CF.getIncrementArearDetails(con, uF, CF, currDate);
			if (hmIncrementArearAmountMap == null)hmIncrementArearAmountMap = new HashMap<String, Map<String, String>>();
			double dblArearAmount = CF.getIncrementArearCalculation(uF, getStrEmpId(), hmIncrementArearAmountMap);
			
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
			double dblCASalCalAttendace = 0;
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
				Map<String, String> hmEmpSalary = hmEmpSalaryTotal.get(getStrEmpId()+"_"+strMonth);
				if(hmEmpSalary != null) {
					
				} else {
					if(isEndDateBetween && endFlag) {
						continue;
					}
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select * from approve_attendance where emp_id=? and EXTRACT(month FROM approve_to)=? " +
						"and financial_year_start=? and financial_year_end=?");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, uF.parseToInt(strMonth));
					pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
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
						Map<String, Map<String, String>> hmInnerCal = CF.getSalaryCalculation(con,hmInnerisDisplayCal,uF.parseToInt(getStrEmpId()), dblPaidDays, 0, 0, (int)dblTotalDays, 0, 0, strLevelId, uF, CF, strDate2, hmSalInner, null, strSalCalStatus);
						
//						System.out.println("hmInnerCal ===>> " + hmInnerCal);
						boolean isPF = false;
						Iterator<String> it11 = hmInnerCal.keySet().iterator();
						while(it11.hasNext()) {
							String strSalaryId = it11.next();
							Map<String,String> hm = hmInnerCal.get(strSalaryId);
							double dblProjectSalHeadAmt = uF.parseToDouble(hmProjectSalAmt.get(strSalaryId));
							if(strMonth!=null && strCurrMonth!=null && strMonth.equals(strCurrMonth) && uF.parseToInt(strSalaryId) == AREARS) {
								dblProjectSalHeadAmt += dblArearAmount;
							} else {
								dblProjectSalHeadAmt += uF.parseToDouble(hm.get("AMOUNT"));
							}
							hmProjectSalAmt.put(strSalaryId,""+dblProjectSalHeadAmt);	
							
							if(hm.get("EARNING_DEDUCTION").equals("E")) {
								if(strMonth!=null && strCurrMonth!=null && strMonth.equals(strCurrMonth) && uF.parseToInt(strSalaryId) == AREARS) {
									totalProjectedGross += dblArearAmount;
								} else {
									totalProjectedGross += uF.parseToDouble(hm.get("AMOUNT"));
								}
							} else if(hm.get("EARNING_DEDUCTION").equals("D") && strSalaryId.equals(EMPLOYEE_EPF+"")) {
								isPF = true;
							}
							if(hraSalaryHeads!=null) {
								List<String> alHrSalHeads = Arrays.asList(hraSalaryHeads);
								if(alHrSalHeads == null) alHrSalHeads = new ArrayList<String>();
								if(alHrSalHeads.contains(strSalaryId)) {
									if(strMonth!=null && strCurrMonth!=null && strMonth.equals(strCurrMonth) && uF.parseToInt(strSalaryId) == AREARS) {
										dblBasicSalCalAttendace += dblArearAmount;
									} else {
										dblBasicSalCalAttendace += uF.parseToDouble(hm.get("AMOUNT"));
									}
								}
							}
							if(strSalaryId.equals(""+HRA)) {
								dblHraSalHeadsCalAttendace += uF.parseToDouble(hm.get("AMOUNT"));
							}
							if(strSalaryId.equals(""+LTA)) {
								dblLTASalCalAttendace += uF.parseToDouble(hm.get("AMOUNT"));
							}
							if(strSalaryId.equals(""+CONVEYANCE_ALLOWANCE)) {
								dblCASalCalAttendace += uF.parseToDouble(hm.get("AMOUNT"));
							}
						}
						
						if(isPF) {
							dblCalAttendacePF += CF.calculateEEPF(con, null, uF, totalProjectedGross, getStrFinancialYearStart(), getStrFinancialYearEnd(), hmProjectSalAmt, getStrEmpId(), null, null);
						}
						totalPTYear += CF.calculateProfessionalTax(con,uF,totalProjectedGross,getStrFinancialYearStart(),getStrFinancialYearEnd(), uF.parseToInt(strMonth), hmEmpStateMap.get(getStrEmpId()), strEmpGender);
						
						nAttendanceApproveMonth++;
					} else {
						Iterator<String> it1 = hmSalaryCal.keySet().iterator();
						while(it1.hasNext()) {
							String strSalaryId = it1.next();
							Map<String,String> hm = hmSalaryCal.get(strSalaryId);
							double dblProjectSalHeadAmt = uF.parseToDouble(hmProjectSalAmt.get(strSalaryId));
							if(strMonth!=null && strCurrMonth!=null && strMonth.equals(strCurrMonth) && uF.parseToInt(strSalaryId) == AREARS) {
								dblProjectSalHeadAmt += dblArearAmount;
							} else {
								dblProjectSalHeadAmt += uF.parseToDouble(hm.get("AMOUNT"));
							}
							hmProjectSalAmt.put(strSalaryId, ""+dblProjectSalHeadAmt);	
							
							if(hm.get("EARNING_DEDUCTION").equals("E")) {
								if(strMonth!=null && strCurrMonth!=null && strMonth.equals(strCurrMonth) && uF.parseToInt(strSalaryId) == AREARS) {
									totalProjectedGross += dblArearAmount;
								} else {
									totalProjectedGross +=uF.parseToDouble(hm.get("AMOUNT"));
								}
							}
						}
						totalPTYear += CF.calculateProfessionalTax(con,uF,totalProjectedGross,getStrFinancialYearStart(),getStrFinancialYearEnd(), uF.parseToInt(strMonth), hmEmpStateMap.get(getStrEmpId()), strEmpGender);
					}
				}
//				System.out.println("11 totalPTYear===>"+totalPTYear);
				
				// Started By Dattatray 08-06-2022 Note: hmTDSProjectedEmp condition committed
				// Started By Dattatray 10-06-2022 Note: hmTDSProjectedEmp condition committed and reverted code
				if(hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+strMonth)!=null) {
					totalTDSYearPaid+=uF.parseToDouble(hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+strMonth));
					monthTDSLEFT--;
//					System.out.println("2 if strMonth===>"+strMonth+"----totalTDSYearPaid===>"+totalTDSYearPaid);
				}else if(hmEmpSalaryTotal.get(strEmpId+"_"+strMonth)!=null) {
					totalTDSYearPaid+=0.0d;
					monthTDSLEFT--;
//					System.out.println("2 if else strMonth===>"+strMonth+"----totalTDSYearPaid===>"+totalTDSYearPaid);
				} else if(hmTDSProjectedEmp.get(strEmpId+"_"+strMonth)!=null) {
					totalTDSYearPaid+=uF.parseToDouble(hmTDSProjectedEmp.get(strEmpId+"_"+strMonth));
					monthTDSLEFT--;
//					System.out.println("2 else if strMonth===>"+strMonth+"----totalTDSYearPaid===>"+totalTDSYearPaid);
				}
				//Ended By dattatray 08-06-2022
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
				pst = con.prepareStatement("select * from salary_details where level_id=? and earning_deduction='E' and salary_head_id not in("+CTC+") "
					+ "and (is_delete = false or is_delete is null) and (is_contribution is null or is_contribution=false) order by weight");
//			}
			pst.setInt(1, uF.parseToInt(strLevelId)); 
//			System.out.println("pst=================== >>>>> " + pst);
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
//			System.out.println("hmEmpSalaryHeadPaidAmt : "+hmEmpSalaryHeadPaidAmt);
//			System.out.println("hmProjectSalAmt : "+hmProjectSalAmt);
			for(int i = 0; i < nSalaryHeadSize; i++) { 
				Map<String, String> hmSalaryHead = alSalaryHead.get(i);
				if(hmSalaryHead == null) hmSalaryHead = new HashMap<String, String>();
				
				String strSalaryHeadId1 = hmSalaryHead.get("SALARY_HEAD_ID");
				/*if(uF.parseToInt(strEmpId)==51){
					System.out.println("VETDSP/648--strSalaryHeadId1="+strSalaryHeadId1+"---hmEmpSalaryHeadPaidAmt="+hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId1)+"--hmProjectSalAmt="+hmProjectSalAmt.get(strSalaryHeadId1));
					
				}*/
				totalGrossYear += uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId1)) + uF.parseToDouble(hmProjectSalAmt.get(strSalaryHeadId1));
			}
			
			
			pst = con.prepareStatement("select * from deduction_tax_misc_details where trail_status=1 and financial_year_from=? and financial_year_to=? and state_id=?");
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(getStrEmpId())));
//			System.out.println("VETP/654--pst="+pst);
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
			
			Map<String,String> innereducationCessMp=educationCessMp.get(hmEmpStateMap.get(getStrEmpId()));
			if(innereducationCessMp==null) innereducationCessMp=new HashMap<String,String>();
			
			Map hmSectionLimitA = new HashMap();
			Map hmSectionLimitP = new HashMap();
			Map<String, String> hmSectionAdjustedGrossIncomeLimitStatus = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and (slab_type=? or slab_type=2) order by section_code");
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(3, slabType);
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
			
			Map<String, String> hmSectionMap = CF.getSectionMap(con,getStrFinancialYearStart(),getStrFinancialYearEnd());
			
			if(hmFeatureUserTypeId==null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
			
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and "+strQuery+" and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) " +
				"and section_code not in ('HRA')  and isdisplay=true and parent_section=0 and under_section=8 and emp_id in ("+getStrEmpId()+") group by emp_id, sd.section_id order by emp_id"); // and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(5, slabType);
//			System.out.println(" pst==>"+pst);
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
			
			Map<String, Map<String, Map<String, Map<String, String>>>> hmEmpIncludeSubSectionData = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();
			pst = con.prepareStatement("select id.*, sd.section_id, sd.is_adjusted_gross_income_limit, sd.include_sub_section from investment_details id, section_details sd where sd.section_id=id.section_id and " +
				"id.fy_from=? and id.fy_to=? and "+strQuery+" and trail_status=1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and section_code not in ('HRA')  and isdisplay=true and parent_section>0 " +
				"and emp_id in ("+getStrEmpId()+") and sub_section_no>0 and (is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit=false) and (include_sub_section is not null or include_sub_section !='') order by emp_id,sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(5, slabType);
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
				"id.fy_from = ? and id.fy_to = ? and "+strQuery+" and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and " +
				"section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=8 and emp_id in ("+getStrEmpId()+") and sub_section_no>0 and " +
				"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id,sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(5, slabType);
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
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and "+strQuery+" and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) " +
				"and section_code not in ('HRA') and isdisplay=true and parent_section=0 and under_section=9 and emp_id in ("+getStrEmpId()+") group by emp_id, sd.section_id order by emp_id "); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(5, slabType);
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
				"id.fy_from = ? and id.fy_to = ? and "+strQuery+" and trail_status = 1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) " +
				" and section_code not in ('HRA')  and isdisplay=true and parent_section>0 and under_section=9 and emp_id in ("+getStrEmpId()+") and sub_section_no>0 and " +
				"(is_adjusted_gross_income_limit is null or is_adjusted_gross_income_limit = false) order by emp_id,sd.section_id"); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(5, slabType);
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
			
			
			pst = con.prepareStatement("select * from section_details where under_section in (8,9) and financial_year_start=? and financial_year_end=? and (slab_type=? or slab_type=2)");
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(3, slabType);
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
				"and "+strQuery+" and trail_status=1 and sd.financial_year_start=? and sd.financial_year_end=? and (sd.slab_type=? or sd.slab_type=2) and section_code not in ('HRA') and isdisplay=true " +
				"and parent_section>0 and emp_id in ("+getStrEmpId()+") order by emp_id"); // and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(5, slabType);
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
			
			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=? and (slab_type=? or slab_type=2)");
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(3, slabType);
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
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
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
				"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and "+strQuery+" and sd.section_id >=13 and sd.section_id <=17 " +
				"and parent_section = 0 and isdisplay=false and financial_year_start=? and financial_year_end=? and emp_id=? and (sd.slab_type=? or sd.slab_type=2) group by emp_id, sd.section_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			pst.setInt(6, slabType);
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
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
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
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
//				System.out.println(" pst==>"+pst);
			Map<String, String> hmEmpChildCnt = new HashMap<String, String>();
			while(rs.next()) {
				hmEmpChildCnt.put(rs.getString("emp_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			// Created By dattatray Date : 03-06-22 Note : and is_paid=true added in query
			// Created By dattatray Date : 10-06-22 Note : and is_paid=true removed in query
			pst = con.prepareStatement("select count(*) as cnt,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg where financial_year_from_date=? and financial_year_to_date =? " +
				"and emp_id=? and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+") " +
				" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id");
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrEmpId()));
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
				"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and "+strQuery+" and under_section in (4,5) " +
				"and exemption_from=? and exemption_to=? and id.salary_head_id>0 and id.parent_section=0 and emp_id=? and (ed.slab_type=? or ed.slab_type=2) group by emp_id, ed.salary_head_id order by emp_id");
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			pst.setInt(6, slabType);
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
				if(dblInvestmentLimit==0) {
					dblInvestmentLimit = dblInvestment;
				}
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
				
				// Created By dattatray Date : 03-06-22 Note : and is_paid=true added in query
				// Created By dattatray Date : 10-06-22 Note : and is_paid=true removed in query
				pst = con.prepareStatement("select pg.emp_id,sum(erpf_contribution) as erpf_contribution,sum(erps_contribution) as erps_contribution," +
					"sum(erdli_contribution) as erdli_contribution,sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges " +
					"from emp_epf_details eed,payroll_generation pg where eed.emp_id=pg.emp_id and financial_year_start=? and financial_year_end=?" +
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
			
			// Created By dattatray Date : 03-06-22 Note : and is_paid=true added in query
			// Created By dattatray Date : 10-06-22 Note : and is_paid=true removed in query
			pst = con.prepareStatement("select count(*) as month,emp_id from (select distinct(month),emp_id from payroll_generation where emp_id=? and financial_year_from_date=? and financial_year_to_date=? group by emp_id,month) a group by emp_id");
			pst.setInt(1,uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			rs = pst.executeQuery(); 
			Map<String,String> empPaidMonth=new HashMap<String,String>();
			while(rs.next()) {
				empPaidMonth.put(rs.getString("emp_id"), rs.getString("month"));
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select count(*) as month,emp_id from tds_projections where emp_id=? and fy_year_from=? and fy_year_end=? group by emp_id");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
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
			if(uF.parseToInt(empTdsProjectMonth.get(getStrEmpId())) > uF.parseToInt(empPaidMonth.get(getStrEmpId()))) {
				nTdsPayMonth = uF.parseToInt(empTdsProjectMonth.get(getStrEmpId()));
				nTdsPayMonDiff = uF.parseToInt(empTdsProjectMonth.get(getStrEmpId())) - uF.parseToInt(empPaidMonth.get(getStrEmpId()));
			} else {
				nTdsPayMonth = uF.parseToInt(empPaidMonth.get(getStrEmpId()));
				nTdsPayMonDiff = 0;
			}
			nMonthsLeft = (nMonth - nTdsPayMonth) + nTdsPayMonDiff;
			
			ApprovePayroll objApprove = new ApprovePayroll();
			objApprove.session = session;
			objApprove.request = request;
			objApprove.CF = CF;
			
			int nEmpLevelId = CF.getEmpLevelId(getStrEmpId(), request);
			Map<String, String> hmSalaryDetails = CF.getSalaryHeadsMap(con, nEmpLevelId);
			
			 Map<String, String> hmUS10_16InnerPaid = (Map<String, String>)hmUnderSection10_16PaidMap.get(getStrEmpId());
			if(hmUS10_16InnerPaid==null) hmUS10_16InnerPaid = new HashMap<String, String>();
//			System.out.println("hmUS10_16InnerPaid ===>> " + hmUS10_16InnerPaid);
			
//			Map<String, String> hmEmpExemptionsMap = objApprove.getEmpInvestmentExemptions(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd(), 0);
			Map<String, String> hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd());
			Map<String, String> hmEmpRentPaidMap = objApprove.getEmpRentPaid(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd());
			Map<String, String> hmFixedExemptions = objApprove.getFixedExemption(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd());
			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objApprove.getEmpPaidAmountDetails(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd());
			
			Map<String,String> hmPaidSalaryDetails = hmEmpPaidAmountDetails.get(getStrEmpId());
			if(hmPaidSalaryDetails==null)hmPaidSalaryDetails = new HashMap<String,String>();
			
			double dblHraSalHeadsAmount = 0;
			for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
				dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
			}
			
			Map<String,String> empsalaryDetailsEPFMap = hmSalaryCal.get(EMPLOYEE_EPF+"");
			double dblEEPF =0;
			if(empsalaryDetailsEPFMap!=null) {
				dblEEPF = CF.calculateEEPF(con, null, uF, salaryGross, getStrFinancialYearStart(), getStrFinancialYearEnd(), hmTotal, getStrEmpId(), null, null);
			}

			double dblBasicDA = dblHraSalHeadsAmount;
			double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
			double dblLTA = uF.parseToDouble(hmTotal.get(LTA+""));
			double dblCA = uF.parseToDouble(hmTotal.get(CONVEYANCE_ALLOWANCE+""));

//			double dblHomeLoanTaxExempt = uF.parseToDouble(hmEmpHomeLoanMap.get(getStrEmpId()));
//			hmTaxInner.put("dblHomeLoanTaxExempt", dblHomeLoanTaxExempt+"");
			
			double dblEPRFPaid = uF.parseToDouble(hmEmployerPF.get(getStrEmpId()));
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
//			System.out.println("hmEmpMertoMap ===>> " + hmEmpMertoMap + " -- hmEmpRentPaidMap ===>> " + hmEmpRentPaidMap + " -- nAttendanceApproveMonth ===>> " + nAttendanceApproveMonth + " -- dblHraSalHeadsCalAttendace ===>> " + dblHraSalHeadsCalAttendace + " -- dblBasicSalCalAttendace ===>> " + dblBasicSalCalAttendace);
			double dblHRAExemptions = CF.getHRAExemptionCalculation(con, uF, nMonthsLeft, hmPaidSalaryDetails, getStrFinancialYearStart(),
					getStrFinancialYearEnd(), getStrEmpId(), dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap, nAttendanceApproveMonth, dblHraSalHeadsCalAttendace, dblBasicSalCalAttendace);
//				System.out.println("dblHRAExemptions ===>> " + dblHRAExemptions);
			double dblLTAExemptions = getLTACalculation(con, uF, nMonthsLeft, hmPaidSalaryDetails, getStrFinancialYearStart(),
					getStrFinancialYearEnd(), getStrEmpId(), dblLTA, nAttendanceApproveMonth, dblLTASalCalAttendace);
			
			double dblCAExemptions = getCACalculation(con, uF, nMonthsLeft, hmPaidSalaryDetails, getStrFinancialYearStart(),
					getStrFinancialYearEnd(), getStrEmpId(), dblCA, nAttendanceApproveMonth, dblCASalCalAttendace);

			hmUS10_16_SalHeadData.put(HRA+"_PAID", "");
			hmUS10_16_SalHeadData.put(HRA+"_EXEMPT", ""+dblHRAExemptions);
			
			hmUS10_16_SalHeadData.put(LTA+"_PAID", hmUS10_16InnerPaid.get(LTA+""));
//			hmUS10_16_SalHeadData.put(LTA+"_PAID", "");
			//System.out.println("VETDS/1612--LTA AMount="+uF.parseToDouble(hmUS10_16InnerPaid.get(LTA+""))+"--dblLTAExemptions="+dblLTAExemptions);
			
		//===start parvez date: 30-03-2023 Note: condition changed for Quloi==== 	
//			hmUS10_16_SalHeadData.put(LTA+"_EXEMPT", ""+Math.min(uF.parseToDouble(hmUS10_16InnerPaid.get(LTA+"")), dblLTAExemptions));
			hmUS10_16_SalHeadData.put(LTA+"_EXEMPT", ""+uF.parseToDouble(hmUS10_16InnerPaid.get(LTA+"")));
		//===end parvez date: 30-03-2023===	
			
//			System.out.println("dblEEEPFPaid "+ dblEEEPFPaid);
//			System.out.println("dblEPRFPaid "+ dblEEEPFPaid);
//			System.out.println("dblVOLEEPFPaid "+ dblVOLEEPFPaid);
//			System.out.println("dblEEEPFToBePaid "+ dblEEEPFToBePaid);
			double dblEmpPF = dblEEEPFPaid + dblEPRFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;
//			System.out.println("dblEmpPF "+ dblEmpPF);
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
						int nEmpChildCnt = uF.parseToInt(hmEmpChildCnt.get(getStrEmpId())) > 2 ? 2 : uF.parseToInt(hmEmpChildCnt.get(getStrEmpId()));
						double dblEducationAllowanceLimit = ((uF.parseToDouble((String)hmFixedExemptions.get(EDUCATION_ALLOWANCE+""))/12) * nEmpChildCnt) * (uF.parseToInt(hmMonthPaid.get(getStrEmpId())) + nMonthsLeft);
						double dblEducationAllowancePaid = dblTotalToBePaid;
						double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
						hmUS10_16_SalHeadData.put(EDUCATION_ALLOWANCE+"_PAID", ""+dblEducationAllowancePaid);
						hmUS10_16_SalHeadData.put(EDUCATION_ALLOWANCE+"_EXEMPT", ""+dblEducationAllowanceExempt);
//						dblExemptions += dblEducationAllowanceExempt;
//						hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+"");
					} else if(uF.parseToInt(strSalaryHeadId) == MEDICAL_ALLOWANCE) { 
						Map<String, String> hmUS10_16Inner = (Map<String, String>)hmUnderSection10_16Map.get(getStrEmpId());
						if(hmUS10_16Inner==null) hmUS10_16Inner = new HashMap<String, String>();
						double dblMedicalAllowanceExemptLimit = uF.parseToDouble((String)hmUS10_16Inner.get(""+MEDICAL_ALLOWANCE));
//						Map<String, String> hmUS10_16InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(getStrEmpId());
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
							int nMonth1 = uF.parseToInt(hmMonthPaid.get(getStrEmpId()));
							double dblConAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * ((nMonth1+nMonthsLeft) - 1);
							double dblTotalDaysAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) / uF.parseToDouble(hmLastPaycycle.get(getStrEmpId()+"_TOTALDAYS"));
							double dblPaidDaysAmt = dblTotalDaysAmt * uF.parseToDouble(hmLastPaycycle.get(getStrEmpId()+"_PAIDDAYS")); 
							
							dblConAllLimit = dblConAmt + dblPaidDaysAmt;
						} else {
							dblConAllLimit = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * nMonth;
						}
//						double dblConveyanceAllowanceLimit = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblConAllLimit));
						double dblConveyanceAllowancePaid = dblTotalPaidAmount;
						double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblCAExemptions);
						
//						hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
//						dblExemptions += dblConveyanceAllowanceExempt;
						hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_PAID", ""); //+dblConveyanceAllowancePaid
						hmUS10_16_SalHeadData.put(CONVEYANCE_ALLOWANCE+"_EXEMPT", ""+dblConveyanceAllowanceExempt);
						
					} else if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) { 
//						dblExemptions += dblTotalToBePaid;
//						hmTaxInner.put("dblProfessionalTaxExempt", dblTotalToBePaid+"");
						hmUS10_16_SalHeadData.put(PROFESSIONAL_TAX+"_PAID", "");
						hmUS10_16_SalHeadData.put(PROFESSIONAL_TAX+"_EXEMPT", ""+dblTotalToBePaid);
						
					} else if(uF.parseToInt(strSalaryHeadId) == REIMBURSEMENT) {
//						System.out.println("REIMBURSEMENT ===>> " + hmUS10_16InnerPaid.get(REIMBURSEMENT+""));
						hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_PAID", hmUS10_16InnerPaid.get(REIMBURSEMENT+""));
						hmUS10_16_SalHeadData.put(REIMBURSEMENT+"_EXEMPT", hmUS10_16InnerPaid.get(REIMBURSEMENT+""));
						
					} else {
						
					}
				}
			}
			
//			System.out.println("hmUS10_16_SalHeadData ===>> " + hmUS10_16_SalHeadData);
			double dblIncomeFromOther = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(getStrEmpId()));
			double dblLessIncomeFromOther = uF.parseToDouble(hmEmpLessIncomeFromOtherSourcesMap.get(getStrEmpId()));
			
			hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");
			hmTaxInner.put("hmUS10_16_SalHeadData", hmUS10_16_SalHeadData);
			
			double dblUS10_16Exempt = 0.0d;
			Map<String, String> hmUS10_16Inner = (Map<String, String>)hmUnderSection10_16Map.get(getStrEmpId());
	        if(hmUS10_16Inner==null) hmUS10_16Inner = new HashMap<String, String>();
	        
//	        Map<String, String> hmUS10_16InnerPaid= (Map<String, String>)hmUnderSection10_16PaidMap.get(getStrEmpId());
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
			/*if(uF.parseToInt(getStrEmpId())==312){
				System.out.println("VTDSP/1741--hmSectionLimitA ===>" + hmSectionLimitA);
				System.out.println("VTDSP/1742--hmEmpInvestment ===>" + hmEmpInvestment.get(strEmpId));
			}*/
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
					/*if(uF.parseToInt(strEmpId)==312){
						System.out.println("VETDSP/1765--dblVIA1Exempt="+dblVIA1Exempt);
					}*/
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
					/*if(uF.parseToInt(strEmpId)==312){
						System.out.println("VETDSP/1787--else--dblVIA1Exempt="+dblVIA1Exempt);
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
//			System.out.println("totalGrossYear ===>> " + totalGrossYear);
//			System.out.println("dblReimTDSCTC ===>> " + dblReimTDSCTC);

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
			pst = con.prepareStatement("select * from section_details where financial_year_start=? and financial_year_end=? and (slab_type=? or slab_type=2) and " +
				"section_code not in ('HRA') and isdisplay=true and is_adjusted_gross_income_limit=true "); //and sd.section_id !=11
			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			pst.setInt(3, slabType);
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
			
			Map<String, List<Map<String, String>>> hmSubInvestment = hmEmpSubInvestment.get(getStrEmpId());
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
				/*if(uF.parseToInt(getStrEmpId())==312){
					System.out.println("VTDSP/1974--dblVIA1Exempt="+dblVIA1Exempt);
				}*/
			}
//			System.out.println("dblVIA1ExemptIsAdjustedLimit ===>> " + dblVIA1ExemptIsAdjustedLimit);
//			System.out.println("totalGrossYear ===>> " + totalGrossYear);
//			System.out.println("dblAddExemptInAdjustedGrossIncome ===>> " + dblAddExemptInAdjustedGrossIncome);
//			System.out.println("dblVIA1Exempt ===>> " + dblVIA1Exempt);
		
			
			//	double dblNetTaxableIncome = (dblPrevOrgGross + totalGrossYear + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
//			Map<String, String> hmEmpSlabMap = CF.getEmpSlabMap(con, CF);
//			System.out.println("VETDSP/2020---dblPrevOrgGross:==>"+dblPrevOrgGross+"--totalGrossYear="+totalGrossYear+"---dblAddExemptInAdjustedGrossIncome="+dblAddExemptInAdjustedGrossIncome+"---dblIncomeFromOther="+dblIncomeFromOther);
//			System.out.println("VETDSP/2021---dblUS10_16Exempt:==>"+dblUS10_16Exempt+"--dblVIA1Exempt="+dblVIA1Exempt+"---dblVIA2Exempt="+dblVIA2Exempt+"---dblVIA1ExemptIsAdjustedLimit="+dblVIA1ExemptIsAdjustedLimit);
//			System.out.println("VETDSP/1980---totalGrossYear:==>"+totalGrossYear);
//			System.out.println("VETDSP/1981---dblAddExemptInAdjustedGrossIncome:==>"+dblAddExemptInAdjustedGrossIncome);
//			System.out.println("VETDSP/1982---dblIncomeFromOther:==>"+dblIncomeFromOther);
//			System.out.println("VETDSP/1983---dblUS10_16Exempt:==>"+dblUS10_16Exempt);
//			System.out.println("VETDSP/1984---dblVIA1Exempt:==>"+dblVIA1Exempt);
			// Created By dattatray Date : 10-06-22 Note : code reverted
			double dblNetTaxableIncome = (dblPrevOrgGross + totalGrossYear + dblAddExemptInAdjustedGrossIncome + dblIncomeFromOther)- dblUS10_16Exempt - dblVIA1Exempt - dblVIA2Exempt - dblVIA1ExemptIsAdjustedLimit; //- dblHomeLoanTaxExempt
			
//			double dblNetTaxableIncome = totalSalaryPaaidYear;//Created By dattatray 08-06-2022
			/*if(uF.parseToInt(getStrEmpId())==312){
//				System.out.println(" dblNetTaxableIncome ===>> " + dblNetTaxableIncome);
				System.out.println("VETDSP/1988--dblPrevOrgGross="+dblPrevOrgGross+"--totalGrossYear="+totalGrossYear+"---dblAddExemptInAdjustedGrossIncome="+dblAddExemptInAdjustedGrossIncome+"--dblIncomeFromOther="+dblIncomeFromOther
						+"---dblUS10_16Exempt="+dblUS10_16Exempt+"--dblVIA1Exempt="+dblVIA1Exempt+"---dblVIA2Exempt="+dblVIA2Exempt+"---dblVIA1ExemptIsAdjustedLimit="+dblVIA1ExemptIsAdjustedLimit);
			}*/
			
			
			int countBug = 0;
			double dblTotalTDSPayable = 0.0d;
			double dblUpperDeductionSlabLimit = 0;
			double dblLowerDeductionSlabLimit = 0;
			double dblTotalNetTaxableSalary = 0; 
			do {
				pst = con.prepareStatement(selectDeduction);
				pst.setDouble(1, uF.parseToDouble(hmEmpAgeMap.get(getStrEmpId())));
				pst.setDouble(2, uF.parseToDouble(hmEmpAgeMap.get(getStrEmpId())));
				pst.setString(3, hmEmpGenderMap.get(getStrEmpId()));
				pst.setDate(4, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
				pst.setDouble(6, dblNetTaxableIncome);
				pst.setDouble(7, dblUpperDeductionSlabLimit);
				pst.setInt(8, slabType);
//				System.out.println("VETDSP/2052---pst ===>> " + pst);
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
				
//				System.out.println("VETDS/2020---dblNetTaxableIncome ===>> " + dblNetTaxableIncome);
//				System.out.println("VETDS/2070---dblDeductionAmount ===>> " + dblDeductionAmount);
//				System.out.println("VETDS/2022---dblUpperDeductionSlabLimit ===>> " + dblUpperDeductionSlabLimit);
//				System.out.println("VETDS/2023---dblLowerDeductionSlabLimit ===>> " + dblLowerDeductionSlabLimit);
				
				if(dblNetTaxableIncome >= dblUpperDeductionSlabLimit) {
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
					
				} else {
					if(countBug==0) {
//						System.out.println("VETDSP/2030---else---if");
						dblTotalNetTaxableSalary = dblNetTaxableIncome - dblLowerDeductionSlabLimit;
					}
//					System.out.println("VETDS/2082---dblTotalTDSPayable ===>> " + dblTotalTDSPayable);
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
				}
				
				dblTotalNetTaxableSalary = dblNetTaxableIncome - dblUpperDeductionSlabLimit;
				
				if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
				countBug++;
				
			} while(dblTotalNetTaxableSalary>0);
			
//			System.out.println("=====dblTotalTDSPayable========="+dblTotalTDSPayable);
			dblTotalTDSPayable = dblTotalTDSPayable; // - dblPrevOrgTDSAmount
//			System.out.println("=====dblTotalTDSPayable========="+dblTotalTDSPayable);
			
			hmTaxInner.put("TAX_LIABILITY", dblTotalTDSPayable+"");
			
			double dblMaxTaxableIncome = uF.parseToDouble(innereducationCessMp.get("MAX_TAX_INCOME"));
//			System.out.println("VETDSP/2105---dblMaxTaxableIncome=="+dblMaxTaxableIncome);
			double dblRebateAmt = uF.parseToDouble(innereducationCessMp.get("REBATE_AMOUNT"));
			double dblRebate = 0;
			if(dblNetTaxableIncome <= dblMaxTaxableIncome && dblTotalTDSPayable <= dblMaxTaxableIncome) {
				if(dblTotalTDSPayable >= dblRebateAmt) {
//					System.out.println("VETDSP/2109---dblRebateAmt ===>> " + dblRebateAmt);
					dblRebate = dblRebateAmt;
				} else if(dblTotalTDSPayable > 0 && dblTotalTDSPayable < dblRebateAmt) {
//					System.out.println("VETDSP/2112---dblTotalTDSPayable=="+dblTotalTDSPayable);
					dblRebate = dblTotalTDSPayable;
				}
			}
			
//			System.out.println("VETDSP/2118---dblRebate ===>> " + dblRebate+"---dblPrevOrgTDSAmount=="+dblPrevOrgTDSAmount);
			
			dblTotalTDSPayable = dblTotalTDSPayable - (dblRebate + dblPrevOrgTDSAmount);
			
			hmTaxInner.put("TAX_REBATE", dblRebate+"");
			
			double dblCess1=uF.parseToDouble(innereducationCessMp.get(O_EDUCATION_CESS));
			
			double dblCess1Amount = dblTotalTDSPayable * (dblCess1/100);
			
			hmTaxInner.put("CESS1", dblCess1+"");
			hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
			
			double dblCess2=uF.parseToDouble(innereducationCessMp.get(O_STANDARD_CESS));
			double dblCess2Amount = dblTotalTDSPayable * (dblCess2/100);
			hmTaxInner.put("CESS2", dblCess2+"");
			hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
			
//			System.out.println("VETDSP/2129---dblCess1Amount====>"+dblCess1Amount+"--dblCess2Amount="+dblCess2Amount+"---dblTotalTDSPayable="+dblTotalTDSPayable);
			dblTotalTDSPayable += (dblCess1Amount + dblCess2Amount);
			
			hmTaxInner.put("TOTAL_TAX_LIABILITY", ""+dblTotalTDSPayable); 
			
			//System.out.println("VETDSP/2082---dblTotalTDSPayable====>"+dblTotalTDSPayable+"--totalTDSYearPaid="+totalTDSYearPaid);
			
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
				
				if(hmEmpTDSPaidAmountDetails.get(getStrEmpId()+"_"+(String)alMonth.get(i))!=null) {
					
				} else if(hmEmpSalaryTotal.get(getStrEmpId()+"_"+(String) alMonth.get(i))!=null) {
					
				} else if(hmTDSProjectedEmp.get(getStrEmpId()+"_"+(String)alMonth.get(i))!=null) {
					
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
			
			
//			System.out.println("totalTDSYearPaid==>"+totalTDSYearPaid);
//			System.out.println("VETDSP/2117--dblTDS====>"+dblTDS);
//			System.out.println("nMonthsLeft====>"+nMonthsLeft);
//			System.out.println("totalTDSYearPaid=====>"+totalTDSYearPaid);
//			System.out.println("VETDSP/2120--nMonthRemain=====>"+nMonthRemain);
			
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat((cal.get(Calendar.MONTH) + 1)+"", "MM", "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "yyyy")));
			
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
				if(hmEmpTDSPaidAmountDetails.get(getStrEmpId()+"_"+(String)alMonth.get(i))!=null) {
					dblTDS1 = uF.parseToDouble((String)hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+(String)alMonth.get(i)));
					dblTotal += dblTDS1;
					hmTDSPaidEmp.put(strEmpId+"_"+(String)alMonth.get(i), uF.formatIntoTwoDecimal(dblTDS1));
//					if(uF.parseToInt(strEmpId)==54 && uF.parseToInt((String)alMonth.get(i))==10){
//						System.out.println("VETDSP/2226---alMonth=="+alMonth.get(i)+"--dblTDS1="+dblTDS1);
//					}
//					System.out.println("VETDSP/2159---dblTDS=====>"+dblTDS+"===dblTDS1=====>"+dblTDS1);
				} else if(hmEmpSalaryTotal.get(strEmpId+"_"+(String)alMonth.get(i))!=null) {
					
				} else if(hmTDSProjectedEmp.get(getStrEmpId()+"_"+(String)alMonth.get(i))!=null) {
//					System.out.println("VETDSP/2163---else if 2");
					hmTDSProjectedEmp1.put(strEmpId+"_"+(String)alMonth.get(i), hmTDSProjectedEmp.get(strEmpId+"_"+(String)alMonth.get(i)));
					hmTDSEmp.put(strEmpId+"_"+(String)alMonth.get(i), hmTDSProjectedEmp.get(strEmpId+"_"+(String)alMonth.get(i)));
					
				} else {
					dblTDS1 = dblTDS;
//					System.out.println("VETDSP/2168---dblTDS=====>"+dblTDS+"===dblTDS1=====>"+dblTDS1);
					hmTDSRemainMonth.put(uF.getMonth(uF.parseToInt((String)alMonth.get(i))), uF.formatIntoTwoDecimalWithOutComma(dblTDS1));
					
					hmTDSEmp.put(strEmpId+"_"+(String)alMonth.get(i), uF.formatIntoTwoDecimalWithOutComma(dblTDS1));
//					if(uF.parseToInt(strEmpId)==54 && uF.parseToInt((String)alMonth.get(i))==11){
//						System.out.println("VETDSP/2243---alMonth=="+alMonth.get(i)+"--dblTDS1="+dblTDS1);
//					}
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
			
//			System.out.println("VETDSP/2204--hmTDSEmp="+hmTDSEmp);
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
	
	
	
	
public Map<String, String> getEmpHomeLoanExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmEmpHomeLoanMap = new HashMap<String,String>();
		
		try {
			
			String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, getStrFinancialYearStart(), getStrFinancialYearEnd());
			int slabType = uF.parseToInt(strSlabType);
			
			pst = con.prepareStatement("select * from section_details where section_id = 11 and financial_year_start=? and financial_year_end=? and (slab_type=? or slab_type=2)");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, slabType);
			rs = pst.executeQuery();
			double dblLoanExemptionLimit = 0;
			while (rs.next()) {
				dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
			" and trail_status=1 and parent_section=0 and section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
			"and financial_year_end=? and (slab_type=? or slab_type=2)) group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, slabType);
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

//	private void getEmpTDSProjection(UtilityFunctions uF) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		try {
//			con = db.makeConnection(con);
//			
//			Calendar cal = Calendar.getInstance();
//			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "dd")));
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "MM"))-1);
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "yyyy")));
//			
//			List<String> alMonth = new ArrayList<String>();
//			List<Date> alDate = new ArrayList<Date>();
//			for(int i=0; i<12;i++) {
//				String strDate = cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
//				alMonth.add(uF.getDateFormat(strDate, DATE_FORMAT, "MM"));
//				
//				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
//				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
//				int nMonth = (cal.get(Calendar.MONTH) + 1);
//				String strDateStart =  (nMonthStart < 10 ? "0"+nMonthStart : nMonthStart)+"/"+(nMonth < 10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);				
//				alDate.add(uF.getDateFormat(strDateStart, DATE_FORMAT));
//				
//				cal.add(Calendar.MONTH, 1);
//			}
//			
//			String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
//			int i1 = 0;
//			String strFYStartPaycycleDate = null;
//			String strFYEndPaycycleDate = null;
//			for(Date ad : alDate) {
//				i1++;
//				String strDateStart = uF.getDateFormat(""+ad, DBDATE, DATE_FORMAT);
//				String[] strPayCycleDates22 = CF.getPayCycleFromDate(con, strDateStart, CF.getStrTimeZone(), CF, strEmpOrgId);
//				
//				if(i1 == 1) {
//					strFYStartPaycycleDate = strPayCycleDates22[0];
//				} else if(i1 == 12) {
//					strFYEndPaycycleDate = strPayCycleDates22[1];
//				}
//			}
//			
//			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
//			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
//			String strCurrId = hmEmpCurrency.get(getStrEmpId());
//			Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
//			if (hmCurrencyInner == null) hmCurrencyInner = new HashMap<String, String>();
//			String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
//			request.setAttribute("strCurrSymbol", strCurrSymbol);
//			
//			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
//			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
//			
//			String empStartMonth = null;
//			String empJoinDate = null;
//			boolean isJoinDateBetween = false;
//			String empEndMonth = null;
//			String empEndDate = null;
//			boolean isEndDateBetween = false;
//			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
//			pst.setInt(1,uF.parseToInt(getStrEmpId()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				
//				if(rs.getString("joining_date") !=null && !rs.getString("joining_date").trim().equals("") && !rs.getString("joining_date").trim().equalsIgnoreCase("")) {
//					empJoinDate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT);
//					if(empJoinDate !=null && !empJoinDate.trim().equals("") && !empJoinDate.trim().equalsIgnoreCase("NULL")) {
//						isJoinDateBetween = uF.isDateBetween(uF.getDateFormatUtil(getStrFinancialYearStart(), DATE_FORMAT), uF.getDateFormatUtil(getStrFinancialYearEnd(), DATE_FORMAT), uF.getDateFormatUtil(empJoinDate, DATE_FORMAT));
//						if(isJoinDateBetween) {
//							empStartMonth = uF.getDateFormat(empJoinDate+"", DATE_FORMAT, "MM");
//						}
//					}
//				}
////				System.out.println(" empStartMonth==>"+empStartMonth+"-----empJoinDate====>"+rs.getString("joining_date")+"---isJoinDateBetween==>"+isJoinDateBetween);
//				
//				if(rs.getString("employment_end_date") !=null && !rs.getString("employment_end_date").trim().equals("") && !rs.getString("employment_end_date").trim().equalsIgnoreCase("")) {
//					empEndDate = uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT);
//					if(empEndDate !=null && !empEndDate.trim().equals("") && !empEndDate.trim().equalsIgnoreCase("NULL")) {
//						isEndDateBetween = uF.isDateBetween(uF.getDateFormatUtil(getStrFinancialYearStart(), DATE_FORMAT), uF.getDateFormatUtil(getStrFinancialYearEnd(), DATE_FORMAT), uF.getDateFormatUtil(empEndDate, DATE_FORMAT));
//						if(isEndDateBetween) {
//							empEndMonth = uF.getDateFormat(empEndDate+"", DATE_FORMAT, "MM");
//						}
//					}
//				}
////				System.out.println(" empEndDate==>"+rs.getString("employment_end_date")+"-----empEndMonth====>"+empEndMonth+"--isEndDateBetween==>"+isEndDateBetween);
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=? and is_alive=false and employment_end_date between ? and ? order by emp_per_id");
//			pst.setInt(1,uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
////			System.out.println(" pst==>"+pst);
//			rs = pst.executeQuery();
//			Map<String, String> hmExEmp = new HashMap<String, String>();
//			while(rs.next()) {
//				hmExEmp.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, Map<String, String>> hmEmpTDSReimbCTC = CF.getEmpTDSReimbursementCTCAmount(con, CF, uF, getStrEmpId(), getStrFinancialYearStart(),getStrFinancialYearEnd());
//			if(hmEmpTDSReimbCTC == null) hmEmpTDSReimbCTC = new HashMap<String, Map<String, String>>();
//			request.setAttribute("hmEmpTDSReimbCTC", hmEmpTDSReimbCTC);
//			
//			double dblReimTDSCTC = 0.0d;
//			Iterator<String> itRCTC = hmEmpTDSReimbCTC.keySet().iterator();
//			while(itRCTC.hasNext()) {
//				String strReimCTCId = itRCTC.next();
//				Map<String, String> hmEmpTDSReimbCTCInner = hmEmpTDSReimbCTC.get(strReimCTCId);
//				if(hmEmpTDSReimbCTCInner == null) hmEmpTDSReimbCTCInner = new HashMap<String, String>();
//				
//				dblReimTDSCTC += uF.parseToDouble(hmEmpTDSReimbCTCInner.get("REIMBURSEMENT_TDS_AMOUNT"));
//			}
//
//			String strLevelId = CF.getEmpLevelId(con, getStrEmpId());
//			Map<String, Map<String, String>> hmEmpSalaryTotal = new HashMap<String, Map<String, String>>();
//			Map<String, String> hmEmpSalaryHeadPaidAmt = new HashMap<String, String>();
////			Map<String, String> hmIsPaidEmp = new HashMap<String, String>();
//			Map<String, String> hmEmpTDSPaidAmountDetails=new HashMap<String, String>();
//			List<String> alSalaryHeadId = new ArrayList<String>();
//			pst = con.prepareStatement("select * from payroll_generation where emp_id=? and financial_year_from_date=? " +
//					"and financial_year_to_date=? order by paycycle");
//			pst.setInt(1,uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
////			System.out.println("pst====>"+pst);
//			rs = pst.executeQuery(); 
//			double totalPTYear = 0.0d;
//			Map<String,String> hmLeaveEncashmet = new HashMap<String, String>();
//			while(rs.next()) {
////				hmIsPaidEmp.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<=9?"0"+rs.getString("month"):rs.getString("month")),rs.getString("emp_id"));
//				if(rs.getString("salary_head_id").equals(TDS+"")) {
//					hmEmpTDSPaidAmountDetails.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<=9?"0"+rs.getString("month"):rs.getString("month")),rs.getString("amount"));
//					continue;
//				}
//				if(rs.getString("salary_head_id").equals(""+REIMBURSEMENT) || rs.getString("salary_head_id").equals(""+MOBILE_REIMBURSEMENT) || rs.getString("salary_head_id").equals(""+TRAVEL_REIMBURSEMENT) || rs.getString("salary_head_id").equals(""+OTHER_REIMBURSEMENT) || rs.getString("salary_head_id").equals(""+SERVICE_TAX) || rs.getString("salary_head_id").equals(""+SWACHHA_BHARAT_CESS) || rs.getString("salary_head_id").equals(""+KRISHI_KALYAN_CESS)) {
//					continue;
//				}
//				Map<String, String> hmEmpSalary = hmEmpSalaryTotal.get(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<10?"0"+rs.getString("month"):rs.getString("month")));
//				double dblReimbursement = 0.0d;
//				if(hmEmpSalary==null) {
//					hmEmpSalary=new HashMap<String, String>();
////					dblReimbursement = uF.parseToDouble(hmReimbursementAmt.get(rs.getString("emp_id"))); 
//				}
//				
//				hmEmpSalary.put(rs.getString("salary_head_id"), rs.getString("amount")); 
//				
//				double dblSalPaidAmt = uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(rs.getString("salary_head_id")));
//				dblSalPaidAmt += uF.parseToDouble(rs.getString("amount"));
//				hmEmpSalaryHeadPaidAmt.put(rs.getString("salary_head_id"), ""+dblSalPaidAmt);				
//				
//				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
//					double dblGross = uF.parseToDouble(hmEmpSalary.get("GROSS")) + uF.parseToDouble(rs.getString("amount")) + dblReimbursement;
//					hmEmpSalary.put("GROSS", dblGross+"");
//				}
//				hmEmpSalaryTotal.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<10?"0"+rs.getString("month"):rs.getString("month")), hmEmpSalary);
//				
//				if(!alSalaryHeadId.contains(rs.getString("salary_head_id"))) {
//					alSalaryHeadId.add(rs.getString("salary_head_id"));
//				}
//				if(uF.parseToInt(rs.getString("salary_head_id")) == PROFESSIONAL_TAX) {
//					totalPTYear +=uF.parseToDouble(rs.getString("amount"));
//				}
//				
//				if(uF.parseToInt(rs.getString("salary_head_id")) == LEAVE_ENCASHMENT && hmExEmp.containsKey(rs.getString("emp_id"))) {
//					hmLeaveEncashmet.put(rs.getString("emp_id"), rs.getString("amount"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			/**
//			 * Calculate projected Salary
//			 * */
//			pst = con.prepareStatement("select salary_head_id,EXTRACT(month FROM paid_to) as month,pay_amount " +
//					"from annual_variable_individual_details where emp_id=? and paid_from >=? and paid_to <=? and is_approved=1");
//			pst.setInt(1,uF.parseToInt(getStrEmpId()));
//			pst.setDate(2,uF.getDateFormat(strFYStartPaycycleDate, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFYEndPaycycleDate, DATE_FORMAT));
////			System.out.println("pst==>"+pst);
//			rs = pst.executeQuery();
//			Map<String, String> hmProjectedAnnualVariable = new HashMap<String, String>();
//			while(rs.next()) {
//				hmProjectedAnnualVariable.put(rs.getString("salary_head_id")+"_"+(uF.parseToInt(rs.getString("month"))<10 ? "0"+rs.getString("month") : rs.getString("month")), rs.getString("pay_amount"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmProjectedAnnualVariable==>"+hmProjectedAnnualVariable);
//			
//			pst = con.prepareStatement("select salary_head_id,EXTRACT(month FROM paid_to) as month,pay_amount " +
//					"from otherearning_individual_details where emp_id=? and paid_from >=? and paid_to <=? and is_approved=1");
//			pst.setInt(1,uF.parseToInt(getStrEmpId()));
//			pst.setDate(2,uF.getDateFormat(strFYStartPaycycleDate, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFYEndPaycycleDate, DATE_FORMAT));
////			System.out.println("pst==>"+pst);
//			rs = pst.executeQuery();
//			Map<String, String> hmProjectedVariable = new HashMap<String, String>();
//			while(rs.next()) {
//				hmProjectedVariable.put(rs.getString("salary_head_id")+"_"+(uF.parseToInt(rs.getString("month"))<10 ? "0"+rs.getString("month") : rs.getString("month")), rs.getString("pay_amount"));
//			}
//			rs.close();
//			pst.close();
////			System.out.println("hmProjectedVariable==>"+hmProjectedVariable);
//			
//			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map hmHRAExemption = new HashMap();
//			while(rs.next()) {
//				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
//				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
//				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
//				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, String> hmEmpMertoMap = new HashMap<String, String>();
//			Map<String, String> hmEmpWlocationMap = new HashMap<String, String>();
//			Map<String, String> hmEmpStateMap = new HashMap<String, String>();
//			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
//			
//			Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmInner = CF.getSalaryCalculation(con,hmInnerisDisplay,uF.parseToInt(getStrEmpId()), 30, 0, 0, 30, 0, 0, strLevelId, uF, CF,uF.getDateFormatUtil(uF.getCurrentDate(CF.getStrTimeZone()), DATE_FORMAT));
//			Iterator<String> it = hmInner.keySet().iterator();
//			double salaryGross=0;
//			Map<String,String> hmTotal=new HashMap<String,String>();
//			while(it.hasNext()) {
//				String strSalaryId = it.next();
//				
//				Map<String,String> hm = hmInner.get(strSalaryId);
//				hmTotal.put(strSalaryId, hm.get("AMOUNT"));
//				if(hm.get("EARNING_DEDUCTION").equals("E")) {
//					salaryGross+=uF.parseToDouble(hm.get("AMOUNT"));
//				}
//			}
//			
//			String[] hraSalaryHeads = null;
//			if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null) {
//				hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//			}
//			
//			Map<String, String> hmTDSProjectedEmp = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from tds_projections where fy_year_from=? and fy_year_end=? and emp_id=?");
//			pst.setDate(1,uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setInt(3,uF.parseToInt(getStrEmpId()));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmTDSProjectedEmp.put(rs.getString("emp_id")+"_"+(uF.parseToInt(rs.getString("month"))<10?"0"+rs.getString("month"):rs.getString("month")), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, Map<String, Map<String, String>>> hmAttendanceSal = new HashMap<String, Map<String,Map<String, String>>>();
//			boolean startFlag = false;
//			boolean endFlag = false;
//			int monthTDSLEFT=12;
//			int nMonth = 0;
//			int nAttendanceApproveMonth = 0;
//			double dblCalAttendacePF = 0.0d;
//			double totalProjectedGross = 0;
//			double totalProjectedPT = 0;
//			double totalTDSYearPaid = 0;
//			double dblHraSalHeadsCalAttendace = 0;
//			double dblBasicSalCalAttendace = 0; 
//			Map<String,String> hmProjectSalAmt = new HashMap<String,String>();
//			for(int i=0; i<alMonth.size(); i++) {
//				String strMonth = alMonth.get(i); 
//				
//				if(isJoinDateBetween && empStartMonth!=null && empStartMonth.equals(strMonth)) {
//					startFlag = true;
//				}
//				
//				if(isJoinDateBetween && !startFlag) {
//					continue;
//				}
//				Map<String, String> hmEmpSalary = hmEmpSalaryTotal.get(getStrEmpId()+"_"+strMonth);
//				if(hmEmpSalary != null) {
//					
//				} else {
//					if(isEndDateBetween && endFlag) {
//						continue;
//					}
//					pst = con.prepareStatement("select * from approve_attendance where emp_id=? and EXTRACT(month FROM  approve_to)=?");
//					pst.setInt(1, uF.parseToInt(getStrEmpId()));
//					pst.setInt(2, uF.parseToInt(strMonth));
//					rs = pst.executeQuery();
//					boolean isAttendance = false;
//					double dblPaidDays = 0.0d;
//					double dblTotalDays = 0.0d;
//					String strDate2 = null;
//					while(rs.next()) {
//						isAttendance = true;
//						dblPaidDays = uF.parseToDouble(rs.getString("paid_days"));
//						dblTotalDays = uF.parseToDouble(rs.getString("total_days"));
//						strDate2 = uF.getDateFormat(rs.getString("approve_to"), DBDATE, DATE_FORMAT);
//					}
//					rs.close();
//					pst.close();
////					System.out.println("strMonth====>"+strMonth+"---isAttendance====>"+isAttendance+"---dblPaidDays====>"+dblPaidDays+"---strDate2==>"+strDate2);
//					if(isAttendance) {
//						Map<String, Map<String, String>> hmInnerisDisplayCal = new HashMap<String, Map<String, String>>();
//						Map<String, Map<String, String>> hmInnerCal = CF.getSalaryCalculation(con,hmInnerisDisplayCal,uF.parseToInt(getStrEmpId()), dblPaidDays, 0, 0, (int)dblTotalDays, 0, 0, strLevelId, uF, CF,strDate2);
//						
//						boolean isPF = false;
//						Iterator<String> it11 = hmInnerCal.keySet().iterator();
//						while(it11.hasNext()) {
//							String strSalaryId = it11.next();
//							
//							Map<String,String> hm = hmInnerCal.get(strSalaryId);
//							double dblPSalAmt = uF.parseToDouble(hm.get("AMOUNT"));
//							
//							if(hmProjectedAnnualVariable.containsKey(strSalaryId+"_"+strMonth)) {
//								dblPSalAmt = uF.parseToDouble(hmProjectedAnnualVariable.get(strSalaryId+"_"+strMonth));
//							} else if(hmProjectedVariable.containsKey(strSalaryId+"_"+strMonth)) {
//								dblPSalAmt = uF.parseToDouble(hmProjectedVariable.get(strSalaryId+"_"+strMonth));
//							}
//							
//							double dblProjectSalHeadAmt = uF.parseToDouble(hmProjectSalAmt.get(strSalaryId));
//							dblProjectSalHeadAmt += dblPSalAmt;
//							hmProjectSalAmt.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblProjectSalHeadAmt));	
//							
//							if(hm.get("EARNING_DEDUCTION").equals("E")) {
//								totalProjectedGross+=uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPSalAmt));
//							} else if(hm.get("EARNING_DEDUCTION").equals("D") && strSalaryId.equals(EMPLOYEE_EPF+"")) {
//								isPF = true;
//							}
//							if(hraSalaryHeads!=null) {
//								List<String> alHrSalHeads = Arrays.asList(hraSalaryHeads);
//								if(alHrSalHeads == null) alHrSalHeads = new ArrayList<String>();
//								if(alHrSalHeads.contains(strSalaryId)) {
//									dblBasicSalCalAttendace += dblPSalAmt;
//								}
//							}
//							if(strSalaryId.equals(""+HRA)) {
//								dblHraSalHeadsCalAttendace += dblPSalAmt;
//							}
//						}
//						
//						if(isPF) {
//							dblCalAttendacePF += CF.calculateEEPF(con, null, uF, totalProjectedGross, getStrFinancialYearStart(), getStrFinancialYearEnd(), hmProjectSalAmt, getStrEmpId(), null, null);
//						}
//						totalPTYear += CF.calculateProfessionalTax(con,uF,totalProjectedGross,getStrFinancialYearEnd(), uF.parseToInt(strMonth), hmEmpStateMap.get(getStrEmpId()));
//						
//						nAttendanceApproveMonth++;
//					} else {
//						Iterator<String> it1 = hmInner.keySet().iterator();
//						while(it1.hasNext()) {
//							String strSalaryId = it1.next();
//							Map<String,String> hm = hmInner.get(strSalaryId);
//							double dblPSalAmt = uF.parseToDouble(hm.get("AMOUNT"));
//							
//							if(hmProjectedAnnualVariable.containsKey(strSalaryId+"_"+strMonth)) {
//								dblPSalAmt = uF.parseToDouble(hmProjectedAnnualVariable.get(strSalaryId+"_"+strMonth));
//							} else if(hmProjectedVariable.containsKey(strSalaryId+"_"+strMonth)) {
//								dblPSalAmt = uF.parseToDouble(hmProjectedVariable.get(strSalaryId+"_"+strMonth));
//							}
//							
//							double dblProjectSalHeadAmt = uF.parseToDouble(hmProjectSalAmt.get(strSalaryId));
//							dblProjectSalHeadAmt += dblPSalAmt;
//							hmProjectSalAmt.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblProjectSalHeadAmt));
//							
//							if(hm.get("EARNING_DEDUCTION").equals("E")) {
//								totalProjectedGross +=uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPSalAmt));
//							}
//						}						
//						totalPTYear += CF.calculateProfessionalTax(con,uF,totalProjectedGross,getStrFinancialYearEnd(), uF.parseToInt(strMonth), hmEmpStateMap.get(getStrEmpId()));
//					}
//					
//				}
//				
//				if(hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+strMonth)!=null) {
//					totalTDSYearPaid+=uF.parseToDouble(hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+strMonth));
//					monthTDSLEFT--;
////					System.out.println("2 if strMonth===>"+strMonth+"----totalTDSYearPaid===>"+totalTDSYearPaid);
//				}else if(hmTDSProjectedEmp.get(strEmpId+"_"+strMonth)!=null) {
//					totalTDSYearPaid+=uF.parseToDouble(hmTDSProjectedEmp.get(strEmpId+"_"+strMonth));
//					monthTDSLEFT--;
////					System.out.println("2 else if strMonth===>"+strMonth+"----totalTDSYearPaid===>"+totalTDSYearPaid);
//				}
//				
//				if(isEndDateBetween && empEndMonth!=null && empEndMonth.equals(strMonth)) {
//					endFlag = true;
//				}
//				
//				nMonth++;
//			}
//			
//			/**
//			 * Calculate projected Salary End
//			 * */
//			
////			System.out.println("alSalaryHeadId===>"+alSalaryHeadId);
////			System.out.println("hmEmpSalaryTotal===>"+hmEmpSalaryTotal);
////			System.out.println("hmEmpTDSPaidAmountDetails===>"+hmEmpTDSPaidAmountDetails);
////			System.out.println("hmProjectSalAmt===>"+hmProjectSalAmt);
////			System.out.println("hmTDSProjectedEmp===>"+hmTDSProjectedEmp);
//
////			if(alSalaryHeadId.size() > 0) {
////				String strSalaryHeadIds = StringUtils.join(alSalaryHeadId.toArray(),",");
////				pst = con.prepareStatement("select * from salary_details where salary_head_id in ("+strSalaryHeadIds+") and level_id=? " +
////						"and earning_deduction='E' order by weight");
////			} else {
//				pst = con.prepareStatement("select * from salary_details where level_id=? and earning_deduction='E' " +
//						"and salary_head_id not in("+CTC+") order by weight");
////			}
//			pst.setInt(1, uF.parseToInt(strLevelId)); 
////			System.out.println("pst===>"+pst);
//			rs = pst.executeQuery();
//			List<Map<String, String>> alSalaryHead = new ArrayList<Map<String,String>>();
//			List<String> alSalHeadIds = new ArrayList<String>();
//			while(rs.next()) {
//				if(!alSalHeadIds.contains(rs.getString("salary_head_id"))) {
//					alSalHeadIds.add(rs.getString("salary_head_id"));
//					
//					Map<String, String> hmSalaryHead = new HashMap<String, String>();
//					hmSalaryHead.put("SALARY_HEAD_ID", rs.getString("salary_head_id")); 
//					hmSalaryHead.put("SALARY_HEAD_NAME", rs.getString("salary_head_name")); 
//					
//					alSalaryHead.add(hmSalaryHead);
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, String> hmTaxInner = new HashMap<String, String>();
//			int nSalaryHeadSize = alSalaryHead.size();
//			double totalGrossYear = 0.0d;
//			for(int i = 0; i < nSalaryHeadSize; i++) { 
//				Map<String, String> hmSalaryHead = alSalaryHead.get(i);
//				if(hmSalaryHead == null) hmSalaryHead = new HashMap<String, String>();
//				
//				String strSalaryHeadId1 = hmSalaryHead.get("SALARY_HEAD_ID");
////				totalGrossYear += uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId1)) + uF.parseToDouble(hmProjectSalAmt.get(strSalaryHeadId1));
//				totalGrossYear += uF.parseToDouble(hmEmpSalaryHeadPaidAmt.get(strSalaryHeadId1)) + uF.parseToDouble(uF.formatIntoZeroWithOutComma(uF.parseToDouble(hmProjectSalAmt.get(strSalaryHeadId1))));
//			}
//			
////			System.out.println("alSalaryHead===>"+alSalaryHead);
////			System.out.println("totalGrossYear===>"+totalGrossYear);
//			
//			hmTaxInner.put("CONYEYANCE_PAID_AMT", hmEmpSalaryHeadPaidAmt.get(""+CONVEYANCE_ALLOWANCE));
//			hmTaxInner.put("CONYEYANCE_PROJECTED_AMT", hmProjectSalAmt.get(""+CONVEYANCE_ALLOWANCE));
//			
//			pst = con.prepareStatement("select * from deduction_tax_misc_details where trail_status=1 " +
//					"and financial_year_from=? and financial_year_to=? and state_id=?");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(getStrEmpId())));
//			rs = pst.executeQuery();
//			double dblFlatTDS = 0.0d;
//			Map<String,Map<String,String> > educationCessMp=new HashMap<String,Map<String,String> >();
//			while(rs.next()) {
//				Map<String,String> innereducationCessMp=new HashMap<String,String>();
//
//				innereducationCessMp.put(O_STANDARD_CESS,rs.getString("standard_tax"));
//				innereducationCessMp.put(O_EDUCATION_CESS,rs.getString("education_tax"));
//				innereducationCessMp.put(O_FLAT_TDS,rs.getString("flat_tds"));
//				
//				innereducationCessMp.put("MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
//				innereducationCessMp.put("REBATE_AMOUNT", rs.getString("rebate_amt"));
//				innereducationCessMp.put("SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
//				innereducationCessMp.put("KRISHI_KALYAN_CESS", rs.getString("KRISHI_KALYAN_CESS"));
//
//				educationCessMp.put(rs.getString("state_id"), innereducationCessMp);
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String,String> innereducationCessMp=educationCessMp.get(hmEmpStateMap.get(getStrEmpId()));
//			if(innereducationCessMp==null) innereducationCessMp=new HashMap<String,String>();
//			
//			Map hmSectionLimitA = new HashMap();
//			Map hmSectionLimitP = new HashMap();			
//			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? order by section_code");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			while (rs.next()) {
//				if(rs.getString("section_limit_type").equalsIgnoreCase("A")) {
//					hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
//				}else{
//					hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String, String> hmSectionMap = CF.getSectionMap(con,getStrFinancialYearStart(),getStrFinancialYearEnd());
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
//					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 " +
//					"and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and sd.section_id !=11 " +
//					"and isdisplay=true and parent_section=0 and under_section=8 and emp_id in ("+getStrEmpId()+") group by emp_id, sd.section_id order by emp_id");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map hmInvestment = new HashMap();
//			Map<String, String> hmEmpExemptionsCH1Map = new HashMap<String, String>();
//			Map<String, Map<String, String>> hmEmpInvestment = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmEmpActualInvestment = new HashMap<String, Map<String, String>>();
//			double dblInvestmentLimit = 0;
//			double dblInvestmentEmp = 0;
//			while(rs.next()) {
//				String strSectionId = rs.getString("section_id");
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				if(hmSectionLimitA.containsKey(strSectionId)) {
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
//				}else{
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
//					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
//				}
//				Map<String,String> hmInvest=hmEmpInvestment.get(rs.getString("emp_id"));
//				if(hmInvest==null) hmInvest=new HashMap<String, String>();
//				
//				if(dblInvestment>=dblInvestmentLimit) {
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
//					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
//				}else{
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH1Map.get(rs.getString("emp_id"))) + dblInvestment;
//					hmEmpExemptionsCH1Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//					hmInvest.put(strSectionId, ""+dblInvestment);
//				}
//				hmEmpInvestment.put(rs.getString("emp_id"), hmInvest);
//				
//				Map<String,String> hmActualInvest=hmEmpActualInvestment.get(rs.getString("emp_id"));
//				if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
//				hmActualInvest.put(strSectionId, ""+dblInvestment);
//				
//				hmEmpActualInvestment.put(rs.getString("emp_id"), hmActualInvest);
//				
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
//					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 " +
//					"and sd.financial_year_start=? and sd.financial_year_end=? and section_code not in ('HRA') and sd.section_id !=11 " +
//					"and isdisplay=true and parent_section=0 and under_section=9 and emp_id in ("+getStrEmpId()+") group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			Map<String, Map<String, String>> hmEmpInvestment1 = new HashMap<String, Map<String, String>>();
//			Map<String, String> hmEmpExemptionsCH2Map = new HashMap<String, String>();
//			Map<String, Map<String, String>> hmEmpActualInvestment1 = new HashMap<String, Map<String, String>>();
//			dblInvestmentLimit = 0;
//			dblInvestmentEmp = 0;
//			while(rs.next()) {
//				String strSectionId = rs.getString("section_id");
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				if(hmSectionLimitA.containsKey(strSectionId)) {
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
//				}else{
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
//					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
//				}
//				Map<String,String> hmInvest=hmEmpInvestment1.get(rs.getString("emp_id"));
//				if(hmInvest==null) hmInvest=new HashMap<String, String>();
//				
//				if(dblInvestment>=dblInvestmentLimit) {
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
//					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//					hmInvest.put(strSectionId, ""+dblInvestmentLimit);
//				}else{
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsCH2Map.get(rs.getString("emp_id"))) + dblInvestment;
//					hmEmpExemptionsCH2Map.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//					hmInvest.put(strSectionId, ""+dblInvestment);
//				}
//				hmEmpInvestment1.put(rs.getString("emp_id"), hmInvest);
//				
//				Map<String,String> hmActualInvest=hmEmpActualInvestment1.get(rs.getString("emp_id"));
//				if(hmActualInvest==null) hmActualInvest=new HashMap<String, String>();
//				hmActualInvest.put(strSectionId, ""+dblInvestment);
//				
//				hmEmpActualInvestment1.put(rs.getString("emp_id"), hmActualInvest);
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select * from section_details where under_section in (8,9) and financial_year_start=? and financial_year_end=? ");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			List<String> chapter1SectionList = new ArrayList<String>();
//			List<String> chapter2SectionList = new ArrayList<String>();
//			while(rs.next()) {
//				if(uF.parseToInt(rs.getString("under_section"))==8) {
//					chapter1SectionList.add(rs.getString("section_id"));
//				} else {
//					chapter2SectionList.add(rs.getString("section_id"));
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select * from investment_details id, section_details sd  where sd.section_id=id.section_id " +
//					"and id.fy_from = ? and id.fy_to = ? and status = true and trail_status = 1 and sd.financial_year_start=? " +
//					"and sd.financial_year_end=? and section_code not in ('HRA') and sd.section_id !=11 and isdisplay=true " +
//					"and parent_section>0 and emp_id in ("+getStrEmpId()+") order by emp_id");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery();
////			System.out.println(" pst==>"+pst);
//			
//			Map<String, Map<String, List<Map<String, String>>>> hmEmpSubInvestment = new HashMap<String, Map<String, List<Map<String, String>>>>();
////			Map<String, List<Map<String, String>>> hmSubInvestment = new HashMap<String, List<Map<String, String>>>();	
//			dblInvestmentLimit = 0;
//			dblInvestmentEmp = 0;
//			
//			while(rs.next()) {
//				String strSectionId = rs.getString("parent_section");
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				Map<String, List<Map<String, String>>> hmSubInvestment =hmEmpSubInvestment.get(rs.getString("emp_id"));
//				if(hmSubInvestment ==null)hmSubInvestment = new HashMap<String, List<Map<String,String>>>();
//				
//				List<Map<String, String>> alSubInvestment =hmSubInvestment.get(rs.getString("parent_section"));
//				if(alSubInvestment ==null)alSubInvestment = new ArrayList<Map<String, String>>();
//				
//				Map<String, String> hm = new HashMap<String, String>();
//				hm.put("SECTION_ID", rs.getString("parent_section"));
//				hm.put("SECTION_NAME", rs.getString("child_section"));
//				hm.put("INVESTMENT_ID", rs.getString("investment_id"));
//				hm.put("PAID_AMOUNT", ""+dblInvestment);
//				hm.put("STATUS", rs.getString("status"));
//				
//				alSubInvestment.add(hm);
//				
//				hmSubInvestment.put(rs.getString("parent_section"), alSubInvestment);
//				hmEmpSubInvestment.put(rs.getString("emp_id"), hmSubInvestment);
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select * from exemption_details where exemption_from=? and exemption_to=?");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery();
////				System.out.println(" pst==>"+pst);
//			Map hmExemption = new HashMap();
//			while(rs.next()) {
////					hmExemption.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
//				hmExemption.put(rs.getString("salary_head_id"), rs.getString("exemption_limit"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery();
////				System.out.println(" pst==>"+pst);
//			double dblInvestmentExemption = 0.0d;
//			if (rs.next()) {
//				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String,String> hmEmpIncomeFromOtherSourcesMap = new HashMap<String,String>();			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd " +
//					"where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and sd.section_id =13 " +
//					"and isdisplay=false and financial_year_start=? and financial_year_end=? and emp_id=? group by emp_id, sd.section_id order by emp_id");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setInt(5, uF.parseToInt(getStrEmpId()));
////				System.out.println("pst========"+pst);
//			rs = pst.executeQuery();			
//			double dblInvestmentIncomeSourcesEmp = 0;		
//			while (rs.next()) {
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				dblInvestmentIncomeSourcesEmp = uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(rs.getString("emp_id"))) + dblInvestment;
//				hmEmpIncomeFromOtherSourcesMap.put(rs.getString("emp_id"), dblInvestmentIncomeSourcesEmp+"");
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select distinct(pg.emp_id),pg.month,pg.year,pg.paycycle,pg.paid_days,pg.total_days from (" +
//					"select max(paycycle) as paycycle,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
//					"where financial_year_from_date=? and financial_year_to_date =? and is_paid = true " +
//					"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+") " +
//					"and earning_deduction='E' and emp_id=? group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id ) a,payroll_generation pg " +
//					"where a.emp_id=pg.emp_id and a.paycycle=pg.paycycle and pg.emp_id in (select emp_per_id from employee_personal_details " +
//					"where is_alive=false and employment_end_date between ? and ?) order by pg.emp_id");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
//			pst.setDate(4, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(5, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
////				System.out.println(" pst==>"+pst);
//			rs = pst.executeQuery();
//			Map<String, String> hmLastPaycycle = new HashMap<String, String>();
//			while(rs.next()) {
//				hmLastPaycycle.put(rs.getString("emp_id"), rs.getString("emp_id"));
//				hmLastPaycycle.put(rs.getString("emp_id")+"_MONTH", rs.getString("month"));
//				hmLastPaycycle.put(rs.getString("emp_id")+"_YEAR", rs.getString("year"));
//				hmLastPaycycle.put(rs.getString("emp_id")+"_PAYCYCLE", rs.getString("paycycle"));
//				hmLastPaycycle.put(rs.getString("emp_id")+"_PAIDDAYS", rs.getString("paid_days"));
//				hmLastPaycycle.put(rs.getString("emp_id")+"_TOTALDAYS", rs.getString("total_days"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("SELECT count(*) as cnt,emp_id FROM emp_family_members WHERE member_type='CHILD' and emp_id=? group by emp_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
//			rs = pst.executeQuery();
////				System.out.println(" pst==>"+pst);
//			Map<String, String> hmEmpChildCnt = new HashMap<String, String>();
//			while(rs.next()) {
//				hmEmpChildCnt.put(rs.getString("emp_id"), rs.getString("cnt"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select count(*) as cnt,emp_id from (select distinct(paycycle),emp_id from payroll_generation pg " +
//					"where financial_year_from_date=? and financial_year_to_date =? and emp_id=? and is_paid = true " +
//					"and salary_head_id not in("+REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+") " +
//					" and earning_deduction='E' group by paycycle,emp_id order by emp_id,paycycle) a group by emp_id");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(getStrEmpId()));
//			rs = pst.executeQuery();
////		System.out.println(" pst==>"+pst);
//			Map<String, String> hmMonthPaid = new HashMap<String, String>();
//			while(rs.next()) {
//				hmMonthPaid.put(rs.getString("emp_id"), rs.getString("cnt"));
//			}
//			rs.close();
//			pst.close();
//			
//			Map<String,String> hmEmpUnderSection10Map = new HashMap<String,String>();			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, ed.salary_head_id, emp_id from investment_details id, exemption_details ed " +
//					"where ed.salary_head_id=id.salary_head_id and id.fy_from = ? and id.fy_to = ? and trail_status = 1 and status = true and under_section=4 " +
//					"and exemption_from=? and exemption_to=? and id.salary_head_id>0 and id.parent_section=0 and emp_id=? group by emp_id, ed.salary_head_id order by emp_id");
//			pst.setDate(1, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			pst.setInt(5, uF.parseToInt(getStrEmpId()));
//			System.out.println("pst========"+pst);
//			rs = pst.executeQuery();			
//			double dblUnderSection10Emp = 0;	
//			dblInvestmentLimit = 0.0d;
//			Map<String,Map<String,String>> hmUnderSection10Map = new HashMap<String,Map<String,String>>();
//			Map<String,Map<String,String>> hmUnderSection10PaidMap = new HashMap<String,Map<String,String>>();
//			while (rs.next()) {
//				String strsalaryheadid = rs.getString("salary_head_id");
//				double dblInvestment = rs.getDouble("amount_paid");				
//				
////					if(hmSectionLimitA.containsKey(strSectionId)) {
////						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
////					}else{
////						dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
////						dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
////					}
//				
//				dblInvestmentLimit = uF.parseToDouble((String)hmExemption.get(strsalaryheadid));
//				
//				Map<String, String> hmInner1= (Map<String, String>)hmUnderSection10Map.get(rs.getString("emp_id"));
//				if(hmInner1==null) hmInner1=new HashMap<String, String>();
//				
//				double dblAllowanceExempt = Math.min(dblInvestment, dblInvestmentLimit);
//				
//				hmInner1.put(rs.getString("salary_head_id"), ""+dblAllowanceExempt);				
//				hmUnderSection10Map.put(rs.getString("emp_id"), hmInner1);
//				
//				dblUnderSection10Emp = uF.parseToDouble(hmEmpUnderSection10Map.get(rs.getString("emp_id"))) + dblAllowanceExempt;
//				hmEmpUnderSection10Map.put(rs.getString("emp_id"), dblUnderSection10Emp+"");
//				
//				Map<String, String> hmInner11 = (Map<String, String>)hmUnderSection10PaidMap.get(rs.getString("emp_id"));
//				if(hmInner11 == null) hmInner11 = new HashMap<String, String>();
//				
//				hmInner11.put(rs.getString("salary_head_id"), ""+dblInvestment);				
//				hmUnderSection10PaidMap.put(rs.getString("emp_id"), hmInner11);
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select count(*) as month,emp_id from (select distinct(month),emp_id from payroll_generation where emp_id=? and financial_year_from_date=? and financial_year_to_date=? group by emp_id,month) a group by emp_id");
//			pst.setInt(1,uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery(); 
//			Map<String,String> empPaidMonth=new HashMap<String,String>();
//			while(rs.next()) {
//				empPaidMonth.put(rs.getString("emp_id"), rs.getString("month"));
//				
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select count(*) as month,emp_id from tds_projections where emp_id=? and fy_year_from=? and fy_year_end=? group by emp_id");
//			pst.setInt(1,uF.parseToInt(getStrEmpId()));
//			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//			rs = pst.executeQuery(); 
//			Map<String,String> empTdsProjectMonth=new HashMap<String,String>();
//			while(rs.next()) {
//				empTdsProjectMonth.put(rs.getString("emp_id"), rs.getString("month"));
//				
//			}
//			rs.close();
//			pst.close();
//
//			int nTdsPayMonth = 0;
//			int nTdsPayMonDiff = 0;
//			int nMonthsLeft = 0;
//			if(uF.parseToInt(empTdsProjectMonth.get(getStrEmpId())) > uF.parseToInt(empPaidMonth.get(getStrEmpId()))) {
//				nTdsPayMonth = uF.parseToInt(empTdsProjectMonth.get(getStrEmpId()));
//				nTdsPayMonDiff = uF.parseToInt(empTdsProjectMonth.get(getStrEmpId())) - uF.parseToInt(empPaidMonth.get(getStrEmpId()));
//			} else {
//				nTdsPayMonth = uF.parseToInt(empPaidMonth.get(getStrEmpId()));
//				nTdsPayMonDiff = 0;
//			}
//			
//			nMonthsLeft = nMonth - nTdsPayMonth;
//			
//			
//			
//			ApprovePayroll objApprove = new ApprovePayroll();
//			objApprove.session = session;
//			objApprove.request = request;
//			objApprove.CF = CF;
//			
//			Map<String, String> hmSalaryDetails = CF.getSalaryHeadsMap(con);
//			Map<String, String> hmEmpExemptionsMap = objApprove.getEmpInvestmentExemptions(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd(), 0);
//			Map<String, String> hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd());
//			Map<String, String> hmEmpRentPaidMap = objApprove.getEmpRentPaid(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd());
//			Map<String, String> hmFixedExemptions = objApprove.getFixedExemption(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd());
//			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objApprove.getEmpPaidAmountDetails(con, uF, getStrFinancialYearStart(), getStrFinancialYearEnd());
//			
//			Map<String,String> hmPaidSalaryDetails=hmEmpPaidAmountDetails.get(getStrEmpId());
//			if(hmPaidSalaryDetails==null)hmPaidSalaryDetails=new HashMap<String,String>();
//			
//			double dblHraSalHeadsAmount = 0;
//			for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++) {
//				dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
//			}
//			
//			Map<String,String>empsalaryDetailsEPFMap=hmInner.get(EMPLOYEE_EPF+"");
//			double dblEEPF =0;
//			if(empsalaryDetailsEPFMap!=null) {
//				dblEEPF = CF.calculateEEPF(con, null, uF, salaryGross, getStrFinancialYearStart(), getStrFinancialYearEnd(), hmTotal, getStrEmpId(), null, null);
//			}
//			double dblBasicDA = dblHraSalHeadsAmount;
//			double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
//			double dblInvestment = uF.parseToDouble(hmEmpExemptionsMap.get(getStrEmpId()));
//			double dbl80CC_Old = uF.parseToDouble(hmEmpExemptionsMap.get(getStrEmpId() + "_3"));
//			double dblHomeLoanExemtion = uF.parseToDouble(hmEmpHomeLoanMap.get(getStrEmpId()));
//			hmTaxInner.put("dblHomeLoanTaxExempt", dblHomeLoanExemtion+"");
//			double dblEEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(EMPLOYEE_EPF + ""));
//			double dblVOLEEPFPaid = uF.parseToDouble(hmPaidSalaryDetails.get(VOLUNTARY_EPF + ""));
//			double dblEEEPFToBePaid = (nMonthsLeft - nAttendanceApproveMonth) * dblEEPF;
//			dblEEEPFToBePaid += dblCalAttendacePF;
//			double dbl80CC_New = dbl80CC_Old + dblEEEPFPaid + dblVOLEEPFPaid+ dblEEEPFToBePaid;
//			double dblHRAExemptions= CF.getHRAExemptionCalculation(con, uF,
//					nMonthsLeft, hmPaidSalaryDetails, getStrFinancialYearStart(),
//					getStrFinancialYearEnd(), getStrEmpId(), dblHRA, dblBasicDA,
//					hmEmpMertoMap, hmEmpRentPaidMap, nAttendanceApproveMonth, dblHraSalHeadsCalAttendace, dblBasicSalCalAttendace);
//			hmTaxInner.put("dblHRAExemptions", dblHRAExemptions+"");
//
//			if (dbl80CC_New >= dblInvestmentExemption) {
//				dbl80CC_New = dblInvestmentExemption;
//			}
//			double dblInvestmentExempt = dbl80CC_New;
//			hmTaxInner.put("dblInvestment", dblInvestmentExempt+"");
//			
//			double dblEmpPF = dblEEEPFPaid + dblVOLEEPFPaid+ dblEEEPFToBePaid;
//			hmTaxInner.put("dblEmpPF", dblEmpPF+"");
//			
//			double dblTotalInvestment = dblInvestment - dbl80CC_Old + dbl80CC_New;
//			double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment	+ dblHRAExemptions;
////			System.out.println("salaryGross==>"+salaryGross+"---nMonthsLeft====>"+nMonthsLeft+"--nAttendanceApproveMonth==>"+nAttendanceApproveMonth);
////			System.out.println("dblEEPF==>"+dblEEPF+"--dblEEEPFToBePaid====>"+dblEEEPFToBePaid+"--dblEEEPFPaid==>"+dblEEEPFPaid+"--dblVOLEEPFPaid==>"+dblVOLEEPFPaid);
////			System.out.println("main dblExemptions====>"+dblExemptions);
//			Set<String> set = hmSalaryDetails.keySet();
//			it = set.iterator();
//			while (it.hasNext()) {
//				String strSalaryHeadId = it.next();
//				String strSalaryHeadName = hmSalaryDetails.get(strSalaryHeadId);
//				
//				if(uF.parseToInt(strSalaryHeadId) == HRA) {
//					continue;
//				}
//
//				if (hmFixedExemptions.containsKey(strSalaryHeadId)) {
//					double dblIndividualExemption = uF.parseToDouble(hmFixedExemptions.get(strSalaryHeadId));
//					
//					double dblTotalToBePaid = 0;
//					if (uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) {
//						dblTotalToBePaid = totalPTYear;
//					} else {
//						if(nMonthsLeft > 0 && nMonthsLeft == nAttendanceApproveMonth) {
//							dblTotalToBePaid = nMonthsLeft * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
//						} else if(nMonthsLeft > 0 && nMonthsLeft > nAttendanceApproveMonth) {
//							dblTotalToBePaid = (nMonthsLeft - nAttendanceApproveMonth) * uF.parseToDouble(hmTotal.get(strSalaryHeadId));
//						} else {
//							dblTotalToBePaid = 0.0d;	
//						}
//						
////						System.out.println("nMonthsLeft====>"+nMonthsLeft+"--nAttendanceApproveMonth====>"+nAttendanceApproveMonth+"--uF.parseToDouble(hmTotal.get(strSalaryHeadId)====>"+uF.parseToDouble(hmTotal.get(strSalaryHeadId)));
//						if(nAttendanceApproveMonth > 0) {
//							Iterator<String> itAttCal = hmAttendanceSal.keySet().iterator();
//							while(itAttCal.hasNext()) {
//								String strMonth = itAttCal.next();
//								Map<String, Map<String, String>> hmInnerCal = hmAttendanceSal.get(strMonth);
//								if(hmInnerCal == null) hmInnerCal = new HashMap<String, Map<String, String>>();
//								Iterator<String> it11 = hmInnerCal.keySet().iterator();
//								while(it11.hasNext()) {
//									String strSalaryId = it11.next();
//									
//									if(uF.parseToInt(strSalaryId) == uF.parseToInt(strSalaryHeadId)) {
//										Map<String,String> hm = hmInnerCal.get(strSalaryId);
//										dblTotalToBePaid += uF.parseToDouble(hm.get("AMOUNT"));
//									}
//								}
//							}
//						}
//					}
//
//					double dblTotalPaid = uF.parseToDouble(hmPaidSalaryDetails.get(strSalaryHeadId));
//					double dblTotalPaidAmount = dblTotalToBePaid+ dblTotalPaid;
//					
//					if(uF.parseToInt(strSalaryHeadId) == EDUCATION_ALLOWANCE) {
//						int nEmpChildCnt = uF.parseToInt(hmEmpChildCnt.get(getStrEmpId())) > 2 ? 2 : uF.parseToInt(hmEmpChildCnt.get(getStrEmpId()));
//						double dblEducationAllowanceLimit = ((uF.parseToDouble((String)hmFixedExemptions.get(EDUCATION_ALLOWANCE+""))/12) * nEmpChildCnt) * (uF.parseToInt(hmMonthPaid.get(getStrEmpId())) + nMonthsLeft);
//						double dblEducationAllowancePaid = dblTotalToBePaid;
//						double dblEducationAllowanceExempt = Math.min(dblEducationAllowancePaid, dblEducationAllowanceLimit);
//						dblExemptions += dblEducationAllowanceExempt;
//						hmTaxInner.put("dblEducationAllowanceExempt", dblEducationAllowanceExempt+"");
//					} else if(uF.parseToInt(strSalaryHeadId) == MEDICAL_ALLOWANCE) { 
//						Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(getStrEmpId());
//						if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
//						double dblMedicalAllowanceExemptLimit =uF.parseToDouble((String)hmUS10Inner.get(""+MEDICAL_ALLOWANCE));
//						
//						Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10PaidMap.get(getStrEmpId());
//						if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
//						double dblMedicalAllowancePaid =uF.parseToDouble((String)hmUS10InnerPaid.get(""+MEDICAL_ALLOWANCE));
//						double dblMedicalAllowanceExempt = Math.min(dblMedicalAllowancePaid, dblMedicalAllowanceExemptLimit);
//						dblExemptions += dblMedicalAllowanceExempt;
//						hmTaxInner.put("dblMedicalAllowanceExempt", dblMedicalAllowanceExempt+"");
//					} else if(uF.parseToInt(strSalaryHeadId) == CONVEYANCE_ALLOWANCE) { 
//						double dblConAllLimit = 0.0d;
//						if(empEndDate !=null && !empEndDate.trim().equals("") && !empEndDate.trim().equalsIgnoreCase("NULL")) {
//							int nMonth1 = uF.parseToInt(hmMonthPaid.get(getStrEmpId()));
//							double dblConAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * ((nMonth1+nMonthsLeft+nTdsPayMonDiff) - 1);
//							double dblTotalDaysAmt = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) / uF.parseToDouble(hmLastPaycycle.get(getStrEmpId()+"_TOTALDAYS"));
//							double dblPaidDaysAmt = dblTotalDaysAmt * uF.parseToDouble(hmLastPaycycle.get(getStrEmpId()+"_PAIDDAYS")); 
//							
//							dblConAllLimit = dblConAmt + dblPaidDaysAmt;
//						} else {
////								dblConAllLimit = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * (uF.parseToInt(hmMonthPaid.get(getStrEmpId()))+nMonthsLeft+nTdsPayMonDiff);
//							dblConAllLimit = (uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12) * nMonth;
////								System.out.println("(String)hmExemption.get(CONVEYANCE_ALLOWANCE)====>"+(String)hmExemption.get(CONVEYANCE_ALLOWANCE+"")+"---uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE))/12===>"+(uF.parseToDouble((String)hmExemption.get(CONVEYANCE_ALLOWANCE+""))/12));
////								System.out.println("nMonth=====>"+nMonth+"---nMonthsLeft====>"+nMonthsLeft+"----hmMonthPaid.get(getStrEmpId())===>"+hmMonthPaid.get(getStrEmpId())+"dblConAllLimit====>"+dblConAllLimit+"----nTdsPayMonDiff====>"+nTdsPayMonDiff);
//						}
//						double dblConveyanceAllowanceLimit = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblConAllLimit));
////						double dblConveyanceAllowancePaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(CONVEYANCE_ALLOWANCE+""));
//						double dblConveyanceAllowancePaid = dblTotalPaidAmount;
//						double dblConveyanceAllowanceExempt = Math.min(dblConveyanceAllowancePaid, dblConveyanceAllowanceLimit);
//						
//						hmTaxInner.put("dblConveyanceAllowanceExempt", dblConveyanceAllowanceExempt+"");
////							System.out.println("dblConveyanceAllowanceLimit====>"+dblConveyanceAllowanceLimit);
////							System.out.println("dblConveyanceAllowancePaid====>"+dblConveyanceAllowancePaid);
////							System.out.println("dblConveyanceAllowanceExempt====>"+dblConveyanceAllowanceExempt);
//						dblExemptions += dblConveyanceAllowanceExempt;
//					} else if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) { 
//						dblExemptions += dblTotalToBePaid;
//						hmTaxInner.put("dblProfessionalTaxExempt", dblTotalToBePaid+"");
//					} else if(uF.parseToInt(strSalaryHeadId) == LTA) { 	
//						Map<String, String> hmUS10Inner= (Map<String, String>)hmUnderSection10Map.get(getStrEmpId());
//						if(hmUS10Inner==null) hmUS10Inner=new HashMap<String, String>();
//						Map<String, String> hmUS10InnerPaid= (Map<String, String>)hmUnderSection10PaidMap.get(getStrEmpId());
//						if(hmUS10InnerPaid==null) hmUS10InnerPaid=new HashMap<String, String>();
//						double dblLTAExemptLimit =uF.parseToDouble((String)hmUS10Inner.get(""+LTA));
//						double dblLTAPaid =uF.parseToDouble((String)hmUS10InnerPaid.get(""+LTA));
//						double dblLTAExempt = Math.min(dblLTAPaid, dblLTAExemptLimit);
//						hmTaxInner.put("dblLTAExempt", dblLTAExempt+"");
////							System.out.println("dblLTAExemptLimit====>"+dblLTAExemptLimit);
////							System.out.println("dblLTAPaid====>"+dblLTAPaid);
////							System.out.println("dblLTAExempt====>"+dblLTAExempt);
//					} else {							
//						if (dblTotalPaidAmount >= dblIndividualExemption) {
//							dblExemptions += dblIndividualExemption;
//						} else {
//							dblExemptions += dblTotalPaidAmount;
//						}
//					}
////					System.out.println("strSalaryHeadId====>"+strSalaryHeadId+"=====strSalaryHeadName====>"+strSalaryHeadName);
////					System.out.println("dblTotalPaid====>"+dblTotalPaid);
////					System.out.println("dblTotalToBePaid====>"+dblTotalToBePaid);
////					System.out.println("dblExemptions====>"+dblExemptions);
////					System.out.println("dblIndividualExemption====>"+dblIndividualExemption);
//
//				}
//			}
//			
//			double dblOtherExemptions = uF.parseToDouble(hmLeaveEncashmet.get(getStrEmpId()));
//			dblExemptions += dblOtherExemptions;
//			hmTaxInner.put("dblOtherExemptions", dblOtherExemptions+"");
//			
//			double dblIncomeFromOther=uF.parseToDouble(hmEmpIncomeFromOtherSourcesMap.get(getStrEmpId()));
//			hmTaxInner.put("dblIncomeFromOther", dblIncomeFromOther+"");
//			
//			totalGrossYear += dblReimTDSCTC;
//			
//			request.setAttribute("totalGrossYear", ""+totalGrossYear);
//			totalGrossYear = (totalGrossYear + dblIncomeFromOther) - dblExemptions;
//			
//			int countBug = 0;
//			double dblTotalTDSPayable = 0.0d;
//			double dblUpperDeductionSlabLimit = 0;
//			double dblLowerDeductionSlabLimit = 0;
//			double dblTotalNetTaxableSalary = 0; 
//			do{
//				pst = con.prepareStatement(selectDeduction);
//				pst.setDouble(1, uF.parseToDouble(hmEmpAgeMap.get(getStrEmpId())));
//				pst.setDouble(2, uF.parseToDouble(hmEmpAgeMap.get(getStrEmpId())));
//				pst.setString(3, hmEmpGenderMap.get(getStrEmpId()));
//				pst.setDate(4, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
//				pst.setDate(5, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
//				pst.setDouble(6, totalGrossYear);
//				pst.setDouble(7, dblUpperDeductionSlabLimit);  
////					if(strEmpId.equalsIgnoreCase("17")) {
////						System.out.println("pst====>"+pst); 
////					}
//				rs = pst.executeQuery();
//				double dblDeductionAmount = 0;
//				String strDeductionType = null;
//				if(rs.next()) {
//					dblDeductionAmount = rs.getDouble("deduction_amount");
//					strDeductionType = rs.getString("deduction_type");
//					dblUpperDeductionSlabLimit = rs.getDouble("_to");
//					dblLowerDeductionSlabLimit = rs.getDouble("_from");
//				}
//				rs.close();
//				pst.close();
//				
//				if(countBug==0) {
//					dblTotalNetTaxableSalary = totalGrossYear;
//				}
//				
//				if(totalGrossYear>=dblUpperDeductionSlabLimit) {
//					dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
//					
////						if(strEmpId.equalsIgnoreCase("17")) {
////							System.out.println("=====IF=========");
////							System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
////							System.out.println("dblUpperDeductionSlabLimit=====>"+dblUpperDeductionSlabLimit);
////							System.out.println("dblLowerDeductionSlabLimit=====>"+dblLowerDeductionSlabLimit);
////							System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
////							System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
////						}
//					
//				}else{
//					
//					if(countBug==0) {
//						dblTotalNetTaxableSalary = totalGrossYear - dblLowerDeductionSlabLimit;
//					}
//					
//					dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
//				
////						if(strEmpId.equalsIgnoreCase("17")) {
////							System.out.println("=====ELSE=========");
////							
////							System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
////							System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
////							System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
////						}
//				}
//				
//				dblTotalNetTaxableSalary = totalGrossYear - dblUpperDeductionSlabLimit;
////					System.out.println("=====dblTotalNetTaxableSalary========="+dblTotalNetTaxableSalary);
//				
//				if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
//				countBug++;
//				
//			}while(dblTotalNetTaxableSalary>0);
//			  
////				System.out.println("=====dblTotalTDSPayable========="+dblTotalTDSPayable);
//			
//			hmTaxInner.put("TAX_LIABILITY", dblTotalTDSPayable+"");
//			
//			double dblMaxTaxableIncome = uF.parseToDouble(innereducationCessMp.get("MAX_TAX_INCOME"));
//			double dblRebateAmt = uF.parseToDouble(innereducationCessMp.get("REBATE_AMOUNT"));
//			double dblRebate = 0;
//			if(totalGrossYear <= dblMaxTaxableIncome && dblTotalTDSPayable <= dblMaxTaxableIncome) {
//				if(dblTotalTDSPayable>=dblRebateAmt) {
//					dblRebate = dblRebateAmt;
//				}else if(dblTotalTDSPayable > 0 && dblTotalTDSPayable < dblRebateAmt) {
//					dblRebate = dblTotalTDSPayable;
//				}
//			}
//			
//			dblTotalTDSPayable = dblTotalTDSPayable - dblRebate;
//			
//			hmTaxInner.put("TAX_REBATE", dblRebate+"");
//			
//			double dblCess1=uF.parseToDouble(innereducationCessMp.get(O_EDUCATION_CESS));
//			double dblCess1Amount = dblTotalTDSPayable * (dblCess1/100);
//			hmTaxInner.put("CESS1", dblCess1+"");
//			hmTaxInner.put("CESS1_AMOUNT", dblCess1Amount+"");
//			
//			double dblCess2=uF.parseToDouble(innereducationCessMp.get(O_STANDARD_CESS));
//			double dblCess2Amount = dblTotalTDSPayable * (dblCess2/100);
//			hmTaxInner.put("CESS2", dblCess2+"");
//			hmTaxInner.put("CESS2_AMOUNT", dblCess2Amount+"");
//			
//			dblTotalTDSPayable += (dblCess1Amount + dblCess2Amount);
//			
//			hmTaxInner.put("TOTAL_TAX_LIABILITY", ""+dblTotalTDSPayable); 
//			
//			dblTotalTDSPayable = dblTotalTDSPayable - totalTDSYearPaid;
//			
//			hmTaxInner.put("TOTAL_TDS_PAID", ""+totalTDSYearPaid); 
//			
//			endFlag = false;
//			startFlag = false;
//			int nMonthRemain = 0;
//			for(int i=0; i<alMonth.size(); i++) {
//				
////					if(isJoinDateBetween && empStartMonth!=null && empStartMonth.equals((String)alMonth.get(i).trim())) {
//				if(isJoinDateBetween && uF.parseToInt(empStartMonth)==uF.parseToInt((String)alMonth.get(i).trim())) {
//					startFlag = true;
//				}
//				
//				if(isJoinDateBetween && !startFlag) {
//					continue;
//				}
//				
//				if(isEndDateBetween && endFlag) {
//					continue;
//				}
//				
//				if(hmEmpTDSPaidAmountDetails.get(getStrEmpId()+"_"+(String)alMonth.get(i))!=null) {
//					
//				}else if(hmTDSProjectedEmp.get(getStrEmpId()+"_"+(String)alMonth.get(i))!=null) {
//					
//				}else{
//					nMonthRemain++;
//				}
//				cal.add(Calendar.MONTH, 1);
//				
////					if(isEndDateBetween && empEndMonth!=null && empEndMonth.equals((String)alMonth.get(i))) {
//				if(isEndDateBetween && uF.parseToInt(empEndMonth) == uF.parseToInt((String)alMonth.get(i).trim())) {
//					endFlag = true;
//				}
//			}
//			
//			double dblTDS = nMonthRemain > 0 ? (dblTotalTDSPayable/nMonthRemain) : 0;
////			System.out.println("dblTotalTDSPayable====>"+dblTotalTDSPayable);
////			System.out.println("dblTDS====>"+dblTDS);
////			System.out.println("nMonthsLeft====>"+nMonthsLeft);
////			System.out.println("totalTDSYearPaid=====>"+totalTDSYearPaid);
////			System.out.println("nMonthRemain=====>"+nMonthRemain);
//			
//			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat((cal.get(Calendar.MONTH) + 1)+"", "MM", "MM"))-1);
//			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT, "yyyy")));
//			
//			if(dblTDS<0) {
//				dblTDS = 0.0d;
//			}
//			
//			double dblTotal = 0;
//			double dblGrossTotal = 0;
//			boolean isProjectedMonth = false;
//			double dblTDS1 = 0;
//			endFlag = false;
//			startFlag = false;
//			Map<String, String> hmTDSRemainMonth = new LinkedHashMap<String, String>();
//			for(int i=0; i<alMonth.size(); i++) {
////					if(isJoinDateBetween && empStartMonth!=null && empStartMonth.equals((String)alMonth.get(i).trim())) {
//				if(isJoinDateBetween && uF.parseToInt(empStartMonth)==uF.parseToInt((String)alMonth.get(i).trim())) {
//					startFlag = true;
//				}
//				
//				if(isJoinDateBetween && !startFlag) {
//					continue;
//				}
//				
//				if(isEndDateBetween && endFlag) {
//					continue;
//				}
//				
////					if(alMonth.get(i).equals(uF.getDateFormat(""+currDate, DBDATE, "MM"))) {
////						dblTDS1 = 0;
////					}
////					System.out.println("(String)alMonth.get(i)=====>"+(String)alMonth.get(i));
////					System.out.println("hmEmpTDSPaidAmountDetails=====>"+hmEmpTDSPaidAmountDetails.get(strEmpId+"_"+uF.parseToInt((String)alMonth.get(i))));
////					System.out.println("hmTDSProjectedEmp=====>"+hmTDSProjectedEmp.get(strEmpId+"_"+(String)alMonth.get(i)));
//				if(hmEmpTDSPaidAmountDetails.get(getStrEmpId()+"_"+(String)alMonth.get(i))!=null) {
//					
//				}else if(hmTDSProjectedEmp.get(getStrEmpId()+"_"+(String)alMonth.get(i))!=null) {
//					
//				}else{
//					dblTDS1 = dblTDS;
////					System.out.println("dblTDS=====>"+dblTDS+"===dblTDS1=====>"+dblTDS1);
//					hmTDSRemainMonth.put(uF.getMonth(uF.parseToInt((String)alMonth.get(i))), uF.formatIntoTwoDecimalWithOutComma(dblTDS1));
//				}
//				cal.add(Calendar.MONTH, 1);
//				
////					if(isEndDateBetween && empEndMonth!=null && empEndMonth.equals((String)alMonth.get(i))) {
//				if(isEndDateBetween && uF.parseToInt(empEndMonth) == uF.parseToInt((String)alMonth.get(i).trim())) {
//					endFlag = true;
//				}
//			}
////			System.out.println("totalGrossYear====>"+totalGrossYear);
////			System.out.println("hmTDSRemainMonth====>"+hmTDSRemainMonth);
//			request.setAttribute("alSalaryHead", alSalaryHead);
//			request.setAttribute("hmEmpSalaryHeadPaidAmt", hmEmpSalaryHeadPaidAmt);
//			request.setAttribute("hmProjectSalAmt", hmProjectSalAmt);
//			request.setAttribute("hmTaxInner", hmTaxInner);
//			
//			request.setAttribute("hmEmpInvestment", hmEmpInvestment);
//			request.setAttribute("hmEmpSubInvestment", hmEmpSubInvestment);  
//			request.setAttribute("hmSectionMap", hmSectionMap);
//			request.setAttribute("hmEmpInvestment1", hmEmpInvestment1);
//			request.setAttribute("hmUnderSection10Map", hmUnderSection10Map);
//			
//			request.setAttribute("chapter1SectionList", chapter1SectionList);
//			request.setAttribute("chapter2SectionList", chapter2SectionList);
//			
//			request.setAttribute("hmEmpActualInvestment", hmEmpActualInvestment);
//			request.setAttribute("hmEmpActualInvestment1", hmEmpActualInvestment1);
//			
//			request.setAttribute("hmTDSRemainMonth", hmTDSRemainMonth);
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
//	
//public Map<String, String> getEmpHomeLoanExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String,String> hmEmpHomeLoanMap = new HashMap<String,String>();
//		
//		try {
//			
//			pst = con.prepareStatement("select * from section_details where section_id = 11 and financial_year_start=? and financial_year_end=?");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			double dblLoanExemptionLimit = 0;
//			while (rs.next()) {
//				dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from =? and fy_to =? and status = true" +
//							" and trail_status = 1 and parent_section=0 and  section_id in (select section_id from section_details where section_id = 11 and financial_year_start=? " +
//							"and financial_year_end=?) group by emp_id");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				
//				if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit) {
//					hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
//				}else{
//					hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
//				}
//			}
//			rs.close();
//			pst.close();
//		} catch (Exception e) {
//			e.printStackTrace();
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
//		return hmEmpHomeLoanMap;
//	
//	}


	public double getLTACalculation(Connection con, UtilityFunctions uF, int nMonthsLeft, Map<String, String> hmPaidSalaryDetails,
			String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, double dblLTA, 
			int nAttendanceApproveMonth, double dblLTASalCalAttendace) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblLTAExemption = 0;
		try {
	
			String strLTAPaidAmount = hmPaidSalaryDetails.get(LTA + "");
//			System.out.println("VETDS/3698---strLTAPaidAmount=="+strLTAPaidAmount+"--dblLTA=="+dblLTA+"--dblLTASalCalAttendace=="+dblLTASalCalAttendace);
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
	
	
	public double getCACalculation(Connection con, UtilityFunctions uF, int nMonthsLeft, Map<String, String> hmPaidSalaryDetails,
			String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, double dblCA, 
			int nAttendanceApproveMonth, double dblCASalCalAttendace) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCAExemption = 0;
		try {
	
			String strCAPaidAmount = hmPaidSalaryDetails.get(CONVEYANCE_ALLOWANCE+ "");
			// System.out.println("nMonthsLeft=="+nMonthsLeft);
			double dblCAToBePaidAmount = (nMonthsLeft - nAttendanceApproveMonth) * dblCA;
			dblCAToBePaidAmount += dblCASalCalAttendace;
	
			double dblTotalCAAmount = uF.parseToDouble(strCAPaidAmount) + dblCAToBePaidAmount;
	
			dblCAExemption = dblTotalCAAmount;
	
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
		return dblCAExemption;
	}


	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrFinancialYearStart() {
		return strFinancialYearStart;
	}

	public void setStrFinancialYearStart(String strFinancialYearStart) {
		this.strFinancialYearStart = strFinancialYearStart;
	}

	public String getStrFinancialYearEnd() {
		return strFinancialYearEnd;
	}

	public void setStrFinancialYearEnd(String strFinancialYearEnd) {
		this.strFinancialYearEnd = strFinancialYearEnd;
	}

	
}