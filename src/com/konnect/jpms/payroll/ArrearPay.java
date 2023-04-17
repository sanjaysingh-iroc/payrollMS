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
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ArrearPay implements IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	
	Map<String, String> hmPaymentModeMap;
	Map<String, String> hmOrg;
	String strLocation;
	String strLevel;
	String strEmpGender;
	Map<String,Map<String,String>> hmSalInner;
	Map<String, String> hmEmpStateMap;
	Map<String, String> hmOtherTaxDetails;
	Map<String, String> hmEmpServiceTaxMap;
	
	
	public void payArrearByGrade(Connection con, UtilityFunctions uF, String strD1, String strD2, String strPC,
			Map<String, List<Map<String, String>>> hmEmpArrear, Map<String, Map<String, String>> hmArrearCalSalary, 
			Map<String, List<String>> hmArrearEarningHead, Map<String, List<String>> hmArrearDeductionHead, 
			Map<String, Map<String, String>> hmArrearEmployeePF, Map<String, Map<String, String>> hmArrearEmployerPF,
			Map<String, Map<String, String>> hmArrearEmployerESI, Map<String, Map<String, String>> hmArrearEmployeeLWF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("in payArrearByGrade");
			int nEmpId = uF.parseToInt(getStrEmpId());
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			String strOrgId = hmEmpOrgId.get(getStrEmpId());
			if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>();
			if(hmEmpStateMap == null) hmEmpStateMap = new HashMap<String, String>();
			if(hmOtherTaxDetails == null) hmOtherTaxDetails = new HashMap<String, String>();
			if(hmEmpServiceTaxMap == null) hmEmpServiceTaxMap = new HashMap<String, String>();
			
			if(hmEmpArrear == null) hmEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
			if(hmArrearCalSalary==null) hmArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
			if(hmArrearEarningHead == null) hmArrearEarningHead = new LinkedHashMap<String, List<String>>();
			if(hmArrearDeductionHead == null) hmArrearDeductionHead = new LinkedHashMap<String, List<String>>();
			
			pst = con.prepareStatement("select arear_id from arrear_generation where emp_id=? and paycycle<=? group by arear_id");
			pst.setInt(1, nEmpId);
			pst.setInt(2, uF.parseToInt(strPC));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			List<String> alAppliedArrearId = new ArrayList<String>();
			while(rs.next()) {
				alAppliedArrearId.add(rs.getString("arear_id"));
			}
			rs.close();
			pst.close();		
//			System.out.println("alAppliedArrearId==>"+alAppliedArrearId);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from arear_details where is_paid=false and arrear_type=1 and emp_id=? and paycycle<=?");
			if(alAppliedArrearId.size() > 0) {
				String strArrearIds = StringUtils.join(alAppliedArrearId.toArray(),",");
				sbQuery.append(" and arear_id not in("+strArrearIds+")");
			}
			pst.setInt(1, nEmpId);
			pst.setInt(2, uF.parseToInt(strPC));
			rs = pst.executeQuery();
			List<Map<String, String>> alArrear = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String,String> hmApplyArear = new HashMap<String, String>();
				hmApplyArear.put("ARREAR_ID", rs.getString("arear_id"));
				hmApplyArear.put("ARREAR_DAYS", rs.getString("arrear_days"));
				hmApplyArear.put("ARREAR_PAYCYCLE_FROM", uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT));
				hmApplyArear.put("ARREAR_PAYCYCLE_TO", uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT));
				hmApplyArear.put("ARREAR_PAYCYCLE", rs.getString("paycycle"));
				
				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "yyyy")));
				int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
				
//				double dblTotalDays = uF.parseToDouble(uF.dateDifference(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT),DATE_FORMAT, uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT), DATE_FORMAT,CF.getStrTimeZone()));
				hmApplyArear.put("ARREAR_PAYCYCLE_TOTAL_DAYS", ""+nTotalNumberOfDays);
				
				alArrear.add(hmApplyArear);
			}
			rs.close();
			pst.close();
//			System.out.println("alArrear==>"+alArrear);
			hmEmpArrear.put(""+nEmpId, alArrear);			
			
			for(Map<String,String> hmApplyArear : alArrear) {
				double dblArrearDays = uF.parseToDouble(hmApplyArear.get("ARREAR_DAYS"));
				String strArrearPaycycleFrom = hmApplyArear.get("ARREAR_PAYCYCLE_FROM");
				String strArrearPaycycleTo = hmApplyArear.get("ARREAR_PAYCYCLE_TO");
				int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
				
				String strFinancialYearEnd = null;
				String strFinancialYearStart = null;
				String[] strFinancialYear = CF.getFinancialYear(con, strArrearPaycycleTo, CF, uF);
				if (strFinancialYear != null) {
					strFinancialYearStart = strFinancialYear[0];
					strFinancialYearEnd = strFinancialYear[1];
				}
				
				int nPayMonth = uF.parseToInt(uF.getDateFormat(strArrearPaycycleTo, DATE_FORMAT, "MM"));
				
//				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strArrearPaycycleFrom, DATE_FORMAT, "dd")));
//				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strArrearPaycycleFrom, DATE_FORMAT, "MM"))-1);
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strArrearPaycycleFrom, DATE_FORMAT, "yyyy")));
//				int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
				int nTotalNumberOfDays = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE_TOTAL_DAYS"));
				
				Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();

				hmInner = CF.getSalaryCalculationByGrade(con, hmInnerisDisplay, nEmpId, dblArrearDays, 0, 0, nTotalNumberOfDays, 0.0d, 0.0d, getStrLevel(), uF, CF, strArrearPaycycleTo,hmSalInner, "0.0d", null); 
//				************* need to add VDA Amount for arrear calculation to end parameter of this function ***********************
//				System.out.println("arrear hmInner==>"+hmInner);
				
				if (hmEmpServiceTaxMap.containsKey(""+nEmpId) && !hmInner.containsKey(CGST + "")) {
					Map<String, String> hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(CGST + "", hmInnerTemp); 
					
//					if(!alEmpSalaryDetailsEarning.contains(""+CGST)) {
//						alEmpSalaryDetailsEarning.add(""+CGST);
//						hmSalaryDetails.put(""+CGST, "CGST");
//					}

					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(SGST + "", hmInnerTemp);
					
//					if(!alEmpSalaryDetailsEarning.contains(""+SGST)) {
//						alEmpSalaryDetailsEarning.add(""+SGST);
//						hmSalaryDetails.put(""+SGST, "SGST");
//					}
				}
				
				ApprovePay approvePay = new ApprovePay();
				approvePay.request = request;
				approvePay.session = session;
				approvePay.CF = CF;
				
				Map<String, String> hmTotal = new LinkedHashMap<String, String>();
				List<String> alEarningHead = new ArrayList<String>();
				List<String> alDeductionHead = new ArrayList<String>();
				Iterator<String> it = hmInner.keySet().iterator();
				double dblGrossPT = 0;
				double dblGross = 0;
				double dblGrossTDS = 0;
				double dblDeduction = 0;
				boolean isEPF = false;
				boolean isESIC = false;
				boolean isLWF = false;
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
					if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")) {
						switch (nSalayHead) {
							case SERVICE_TAX :
								double dblServiceTaxAmount = approvePay.calculateServiceTax(con, uF, getStrEmpId(), dblGross, hmEmpStateMap.get(getStrEmpId()), hmTotal,
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
								
								alEarningHead.add(strSalaryId);

								break;

							case SWACHHA_BHARAT_CESS :
								double dblGrossAmt = dblGross;
								double dblServiceTaxAmt = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
								dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
								double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
								dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;

								double dblSwachhaBharatCess = approvePay.calculateSwachhaBharatCess(con, uF, getStrEmpId(), dblGrossAmt, hmEmpStateMap.get(getStrEmpId()),
										hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));

								dblGross += dblSwachhaBharatCess;
								dblGrossPT += dblSwachhaBharatCess;
								dblGrossTDS += dblSwachhaBharatCess;
								
								alEarningHead.add(strSalaryId);

								break;

							case KRISHI_KALYAN_CESS :
								double dblGrossAmt1 = dblGross;
								double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
								dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
								double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS + ""));
								dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;

								double dblKrishiKalyanCess = approvePay.calculateKrishiKalyanCess(con, uF, getStrEmpId(), dblGrossAmt1, hmEmpStateMap.get(getStrEmpId()),
										hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));

								dblGross += dblKrishiKalyanCess;
								dblGrossPT += dblKrishiKalyanCess;
								dblGrossTDS += dblKrishiKalyanCess;
								
								alEarningHead.add(strSalaryId);

								break;
								
							case CGST :
								double dblCGSTAmount = approvePay.calculateCGST(con, uF, getStrEmpId(), dblGross, hmEmpStateMap.get(getStrEmpId()), hmTotal,
										strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

								dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
								dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
								dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
								
								alEarningHead.add(strSalaryId);

								break;
							
							case SGST :
								double dblGrossAmt2 = dblGross;
								double dblCGSTAmt = uF.parseToDouble(hmTotal.get(CGST + ""));
								dblGrossAmt2 = dblGrossAmt2 - dblCGSTAmt;
								
								double dblSGSTAmount = approvePay.calculateSGST(con, uF, getStrEmpId(), dblGrossAmt2, hmEmpStateMap.get(getStrEmpId()), hmTotal,
										strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

								dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
								dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
								dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
								
								alEarningHead.add(strSalaryId);

								break;

							default :

								if (uF.parseToInt(strSalaryId) != GROSS) {
									boolean isMultipePerWithParticularHead = false;
									if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
										isMultipePerWithParticularHead = approvePay.checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, getStrEmpId(), dblArrearDays, nTotalNumberOfDays, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, getStrLevel(), hm, hmTotal, null,0.0d, null, null,0.0d);
									}
									if(!isMultipePerWithParticularHead) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
										dblGross += uF.parseToDouble(hm.get("AMOUNT"));
										dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
										dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
										
										alEarningHead.add(strSalaryId);
									}
								}

								break;
						}
					} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
						/**
						 * TAX CALCULATION STARTS HERE
						 * 
						 * */

						switch (nSalayHead) {

							/********** EPF EMPLOYEE CONTRIBUTION *************/
							case EMPLOYEE_EPF :
								Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");

								double dblEEPF = approvePay.calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, getStrEmpId(), null, null, false, null,null,null, null, null, null);
								dblDeduction += dblEEPF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
								
								alDeductionHead.add(strSalaryId);
								isEPF = true;

								break;

							default :
								if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
											&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
											&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
									hmTotal.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
									dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));
									
									alDeductionHead.add(strSalaryId);
								}
								break;
						}
					}
//					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}	
				
				/**
				 * Multiple cal start
				 * */
				Map<String, String> hmContriSalHeadAmt = new HashMap<String, String>();
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
					double dblERPF = approvePay.calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, null,null,null, null, null, null);
//					calculateERPF(con, CF, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, strArrearPaycycleTo, hmEmployerPF);
					hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
					double dblESI = approvePay.calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, null, null, strD1, strD2, strPC, hmTotal);//need to add hmTotalESIC
					hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
					double dblLWF = approvePay.calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
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
						double dblMulCalAmt = approvePay.getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead, getStrEmpId(), dblArrearDays, nTotalNumberOfDays, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, getStrLevel(), hm, hmTotal, null,0.0d, null, null,0.0d, hmContriSalHeadAmt);
						if(!hmTotal.containsKey(strSalaryId)) {
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblGross += dblMulCalAmt;
							dblGrossTDS += dblMulCalAmt;
							
							alEarningHead.add(strSalaryId);
						}
					} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")
							&& (hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						double dblMulCalAmt = approvePay.getMultipleCalAmtDetailsByGrade(con, uF, CF, nSalayHead, getStrEmpId(), dblArrearDays, nTotalNumberOfDays, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, getStrLevel(), hm, hmTotal, null,0.0d, null, null,0.0d, hmContriSalHeadAmt);
						if(!hmTotal.containsKey(strSalaryId)) {
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblDeduction += dblMulCalAmt;
							
							alDeductionHead.add(strSalaryId);
						}
					}

//					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
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
								double dblPt = approvePay.calculateProfessionalTax(con, uF, strArrearPaycycleTo, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(getStrEmpId()), strEmpGender);
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
								
								alDeductionHead.add(strSalaryId);

								break;

								/********** ESI EMPLOYEE CONTRIBUTION *************/
							case EMPLOYEE_ESI :
								double dblESI = approvePay.calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(getStrEmpId()), null, getStrEmpId(), null, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, hmTotal);//need to add hmTotalESIC
								dblESI = Math.ceil(dblESI);
								if(uF.parseToInt(strEmpId)==384 ){
									System.out.println("ArP/455---dblESI="+dblESI);
									
								}
									
								dblDeduction += dblESI;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
								
								alDeductionHead.add(strSalaryId);
								
								isESIC = true;

								break;
							/********** /ESI EMPLOYEE CONTRIBUTION *************/	
								
							/********** LWF EMPLOYEE CONTRIBUTION *************/
							case EMPLOYEE_LWF :
								double dblLWF = approvePay.calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(getStrEmpId()), null, getStrEmpId(), nPayMonth, null, strOrgId);
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
								
								alDeductionHead.add(strSalaryId);
								isLWF = true;
								
								break;
							/********** /LWF EMPLOYEE CONTRIBUTION *************/	

						}
					}

//					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				/**
				 * Other cal end
				 * */


				hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction)));
//				System.out.println("hmTotal==>"+hmTotal);
				hmArrearCalSalary.put(getStrEmpId()+"_"+nArrearPaycycle, hmTotal);
				
				hmArrearEarningHead.put(getStrEmpId()+"_"+nArrearPaycycle, alEarningHead);
				
				hmArrearDeductionHead.put(getStrEmpId()+"_"+nArrearPaycycle, alDeductionHead);
				
				if(isEPF) {
					/**
					 * Employee PF
					 * */
					Map<String, String> hmEmployeePF = new HashMap<String, String>();
					calculateEEPF(con, uF, strFinancialYearStart,strFinancialYearEnd, hmTotal,getStrEmpId(),hmEmployeePF);
					
					hmArrearEmployeePF.put(getStrEmpId()+"_"+nArrearPaycycle, hmEmployeePF);
					
					/**
					 * Employer PF
					 * */
					Map<String, String> hmEmployerPF = new HashMap<String, String>();
					calculateERPF(con, CF, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, strArrearPaycycleTo, hmEmployerPF);
					
					hmArrearEmployerPF.put(getStrEmpId()+"_"+nArrearPaycycle, hmEmployerPF);
				}
				if(isESIC) {
					Map<String, String> hmEmployerESI = new HashMap<String, String>();
					calculateEESI(con, uF, strFinancialYearStart, strFinancialYearEnd,hmTotal,getStrEmpId(), strArrearPaycycleFrom, 
							strArrearPaycycleTo, ""+nArrearPaycycle, hmEmpStateMap,hmEmployerESI);
					
					hmArrearEmployerESI.put(getStrEmpId()+"_"+nArrearPaycycle, hmEmployerESI);
				}
				if(isLWF) {
					Map<String, String> hmEmployeeLWF = new HashMap<String, String>();
					calculateELWF(con, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, getStrEmpId(), strArrearPaycycleFrom, strArrearPaycycleTo, hmEmpStateMap,hmEmployeeLWF, strOrgId);
					
					hmArrearEmployeeLWF.put(getStrEmpId()+"_"+nArrearPaycycle, hmEmployeeLWF);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}
	
	
	public void payArrear(Connection con, UtilityFunctions uF, String strD1, String strD2, String strPC, int nCalTotalNumberOfDays, Map<String, List<Map<String, String>>> hmEmpArrear,
		Map<String, Map<String, String>> hmArrearCalSalary, Map<String, List<String>> hmArrearEarningHead, Map<String, List<String>> hmArrearDeductionHead, 
		Map<String, Map<String, String>> hmArrearEmployeePF, Map<String, Map<String, String>> hmArrearEmployerPF, Map<String, Map<String, String>> hmArrearEmployerESI,
		Map<String, Map<String, String>> hmArrearEmployeeLWF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("in payArrear");
			int nEmpId = uF.parseToInt(getStrEmpId()); 
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			String strOrgId = hmEmpOrgId.get(getStrEmpId());
			
			if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>();
			if(hmEmpStateMap == null) hmEmpStateMap = new HashMap<String, String>();
			if(hmOtherTaxDetails == null) hmOtherTaxDetails = new HashMap<String, String>();
			if(hmEmpServiceTaxMap == null) hmEmpServiceTaxMap = new HashMap<String, String>();
			
			if(hmEmpArrear == null) hmEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
			if(hmArrearCalSalary==null) hmArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
			if(hmArrearEarningHead == null) hmArrearEarningHead = new LinkedHashMap<String, List<String>>();
			if(hmArrearDeductionHead == null) hmArrearDeductionHead = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			
			
			pst = con.prepareStatement("select arear_id from arrear_generation where emp_id=? and paycycle<=? group by arear_id");
			pst.setInt(1, nEmpId);
			pst.setInt(2, uF.parseToInt(strPC));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			List<String> alAppliedArrearId = new ArrayList<String>();
			while(rs.next()) {
				alAppliedArrearId.add(rs.getString("arear_id"));
			}
			rs.close();
			pst.close();		
//			System.out.println("alAppliedArrearId==>"+alAppliedArrearId);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from arear_details where is_paid=false and arrear_type=1 and emp_id=? and paycycle<=?");
			if(alAppliedArrearId.size() > 0) {
				String strArrearIds = StringUtils.join(alAppliedArrearId.toArray(),",");
				sbQuery.append(" and arear_id not in("+strArrearIds+")");
			}	
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, nEmpId);
			pst.setInt(2, uF.parseToInt(strPC));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alArrear = new ArrayList<Map<String,String>>();
			while(rs.next()) {
				Map<String,String> hmApplyArear = new HashMap<String, String>();
				hmApplyArear.put("ARREAR_ID", rs.getString("arear_id"));
				hmApplyArear.put("ARREAR_DAYS", rs.getString("arrear_days"));
				hmApplyArear.put("ARREAR_PAYCYCLE_FROM", uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT));
				hmApplyArear.put("ARREAR_PAYCYCLE_TO", uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT));
				hmApplyArear.put("ARREAR_PAYCYCLE", rs.getString("paycycle"));
				
				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "dd")));
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "yyyy")));
				int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
				/*int nTotalNumberOfDays = nCalTotalNumberOfDays;
				if(nCalTotalNumberOfDays == 0){
					nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
				}*/
				
//				double dblTotalDays = uF.parseToDouble(uF.dateDifference(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT),DATE_FORMAT, uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT), DATE_FORMAT,CF.getStrTimeZone()));
				hmApplyArear.put("ARREAR_PAYCYCLE_TOTAL_DAYS", ""+nTotalNumberOfDays);
				
				alArrear.add(hmApplyArear);
			}
			rs.close();
			pst.close();
//			System.out.println("alArrear==>"+alArrear);
			hmEmpArrear.put(""+nEmpId, alArrear);
			
			for(Map<String,String> hmApplyArear : alArrear) {
				double dblArrearDays = uF.parseToDouble(hmApplyArear.get("ARREAR_DAYS"));
				String strArrearPaycycleFrom = hmApplyArear.get("ARREAR_PAYCYCLE_FROM");
				String strArrearPaycycleTo = hmApplyArear.get("ARREAR_PAYCYCLE_TO");
				int nArrearPaycycle = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE"));
				
				String strFinancialYearEnd = null;
				String strFinancialYearStart = null;
				String[] strFinancialYear = CF.getFinancialYear(con, strArrearPaycycleTo, CF, uF);
				if (strFinancialYear != null) {
					strFinancialYearStart = strFinancialYear[0];
					strFinancialYearEnd = strFinancialYear[1];
				}
				
				int nPayMonth = uF.parseToInt(uF.getDateFormat(strArrearPaycycleTo, DATE_FORMAT, "MM"));
				
//				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strArrearPaycycleFrom, DATE_FORMAT, "dd")));
//				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strArrearPaycycleFrom, DATE_FORMAT, "MM"))-1);
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strArrearPaycycleFrom, DATE_FORMAT, "yyyy")));
//				int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
				int nTotalNumberOfDays = uF.parseToInt(hmApplyArear.get("ARREAR_PAYCYCLE_TOTAL_DAYS"));
			
			//===start parvez date: 24-03-2023===	
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);

				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strArrearPaycycleFrom, strArrearPaycycleTo, CF, uF, hmWeekEndHalfDates, null);
				Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
				CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, strArrearPaycycleFrom, strArrearPaycycleTo, alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEnds, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
				Map<String, Set<String>> holidaysMp = CF.getHolidayList(con, request, uF, strArrearPaycycleFrom, strArrearPaycycleTo);
				
				
				Set<String> weeklyOffSet = null;
				if (alEmpCheckRosterWeektype.contains(getStrEmpId())) {
					weeklyOffSet = hmRosterWeekEndDates.get(getStrEmpId());
					
				} else {
					weeklyOffSet = hmWeekEnds.get(getStrLocation());
				}
				
				if (weeklyOffSet == null)
					weeklyOffSet = new HashSet<String>();

				Set<String> OriginalholidaysSet = holidaysMp.get(getStrLocation());
				if (OriginalholidaysSet == null)
					OriginalholidaysSet = new HashSet<String>();

				Set<String> holidaysSet = new HashSet<String>(OriginalholidaysSet);
				holidaysSet.removeAll(weeklyOffSet);
				
				int nWeekEnds1 = weeklyOffSet.size();
				int nHolidays = holidaysSet.size();
				
				if (hmOrg!=null && "AWD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
					nTotalNumberOfDays = (nTotalNumberOfDays - nWeekEnds1) - nHolidays;
				} else if (hmOrg!=null && "AFD".equalsIgnoreCase(uF.showData(hmOrg.get("ORG_SALARY_CAL_BASIS"), ""))) {
					nTotalNumberOfDays = uF.parseToInt(hmOrg.get("ORG_SALARY_FIX_DAYS"));
				}
				System.out.println("nEmpId=="+nEmpId+"---nTotalNumberOfDays=="+nTotalNumberOfDays);
			//===end parvez date: 24-03-2023===	
				
				Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				/*if(uF.parseToInt(strEmpId) == 361) {
					System.out.println("AR/651---dblArrearDays=="+dblArrearDays+"--nTotalNumberOfDays="+nTotalNumberOfDays);
				}*/

//				System.out.println("AR/660---dblArrearDays=="+dblArrearDays+"--nTotalNumberOfDays="+nTotalNumberOfDays);
				hmInner = CF.getSalaryCalculation(con, hmInnerisDisplay, nEmpId, dblArrearDays, 0, 0, nTotalNumberOfDays, 0.0d, 0.0d, getStrLevel(), uF, CF, strArrearPaycycleTo,hmSalInner, null, null);
//				System.out.println("arrear hmInner==>"+hmInner);
				
				if (hmEmpServiceTaxMap.containsKey(""+nEmpId) && !hmInner.containsKey(CGST + "")) {
					Map<String, String> hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(CGST + "", hmInnerTemp); 
					
//					if(!alEmpSalaryDetailsEarning.contains(""+CGST)) {
//						alEmpSalaryDetailsEarning.add(""+CGST);
//						hmSalaryDetails.put(""+CGST, "CGST");
//					}

					hmInnerTemp = new HashMap<String, String>();
					hmInnerTemp.put("AMOUNT", "0");
					hmInnerTemp.put("EARNING_DEDUCTION", "E");
					hmInnerTemp.put("SALARY_AMOUNT_TYPE", "A");
					hmInner.put(SGST + "", hmInnerTemp);
					
//					if(!alEmpSalaryDetailsEarning.contains(""+SGST)) {
//						alEmpSalaryDetailsEarning.add(""+SGST);
//						hmSalaryDetails.put(""+SGST, "SGST");
//					}
				}
				
				ApprovePay approvePay = new ApprovePay();
				approvePay.request = request;
				approvePay.session = session;
				approvePay.CF = CF;
				
				Map<String, String> hmTotal = new LinkedHashMap<String, String>();
				List<String> alEarningHead = new ArrayList<String>();
				List<String> alDeductionHead = new ArrayList<String>();
				Iterator<String> it = hmInner.keySet().iterator();
				double dblGrossPT = 0;
				double dblGross = 0;
				double dblGrossTDS = 0;
				double dblDeduction = 0;
				boolean isEPF = false;
				boolean isESIC = false;
				boolean isLWF = false;
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
					if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("E")) {
						switch (nSalayHead) {
							case SERVICE_TAX :
								double dblServiceTaxAmount = approvePay.calculateServiceTax(con, uF, getStrEmpId(), dblGross, hmEmpStateMap.get(getStrEmpId()), hmTotal,
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
//								System.out.println("ArP/745--strSalaryId="+strSalaryId+"--dblGross=="+dblGross);
								
								alEarningHead.add(strSalaryId);

								break;

							case SWACHHA_BHARAT_CESS :
								double dblGrossAmt = dblGross;
								double dblServiceTaxAmt = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
								dblGrossAmt = dblGrossAmt - dblServiceTaxAmt;
								double dblKrishiKalynCessAmt = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS + ""));
								dblGrossAmt = dblGrossAmt - dblKrishiKalynCessAmt;

								double dblSwachhaBharatCess = approvePay.calculateSwachhaBharatCess(con, uF, getStrEmpId(), dblGrossAmt, hmEmpStateMap.get(getStrEmpId()),
										hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSwachhaBharatCess));

								dblGross += dblSwachhaBharatCess;
								dblGrossPT += dblSwachhaBharatCess;
								dblGrossTDS += dblSwachhaBharatCess;
								
								alEarningHead.add(strSalaryId);
//								System.out.println("ArP/767--strSalaryId="+strSalaryId+"--dblGross=="+dblGross);

								break;

							case KRISHI_KALYAN_CESS :
								double dblGrossAmt1 = dblGross;
								double dblServiceTaxAmt1 = uF.parseToDouble(hmTotal.get(SERVICE_TAX + ""));
								dblGrossAmt1 = dblGrossAmt1 - dblServiceTaxAmt1;
								double dblSwachhaBharatCessAmt = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS + ""));
								dblGrossAmt1 = dblGrossAmt1 - dblSwachhaBharatCessAmt;

								double dblKrishiKalyanCess = approvePay.calculateKrishiKalyanCess(con, uF, getStrEmpId(), dblGrossAmt1, hmEmpStateMap.get(getStrEmpId()),
										hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblKrishiKalyanCess));

								dblGross += dblKrishiKalyanCess;
								dblGrossPT += dblKrishiKalyanCess;
								dblGrossTDS += dblKrishiKalyanCess;
								
								alEarningHead.add(strSalaryId);

//								System.out.println("ArP/788--strSalaryId="+strSalaryId+"--dblGross=="+dblGross);
								break;
								
							case CGST :
								double dblCGSTAmount = approvePay.calculateCGST(con, uF, getStrEmpId(), dblGross, hmEmpStateMap.get(getStrEmpId()), hmTotal,
										strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));

								dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
								dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
								dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCGSTAmount));
								
								alEarningHead.add(strSalaryId);
								

								break;
							
							case SGST :
								double dblGrossAmt2 = dblGross;
								double dblCGSTAmt = uF.parseToDouble(hmTotal.get(CGST + ""));
								dblGrossAmt2 = dblGrossAmt2 - dblCGSTAmt;
								
								double dblSGSTAmount = approvePay.calculateSGST(con, uF, getStrEmpId(), dblGrossAmt2, hmEmpStateMap.get(getStrEmpId()), hmTotal,
										strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));

								dblGross += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
								dblGrossPT += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
								dblGrossTDS += uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblSGSTAmount));
								
								alEarningHead.add(strSalaryId);
//								System.out.println("ArP/819--strSalaryId="+strSalaryId+"--dblGross=="+dblGross);

								break;

							default :

								if (uF.parseToInt(strSalaryId) != GROSS) {
									boolean isMultipePerWithParticularHead = false;
									if(hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P")) {
										isMultipePerWithParticularHead = approvePay.checkMultipleCalPerWithParticularHead(con, uF, CF, nSalayHead, getStrEmpId(), dblArrearDays, nTotalNumberOfDays, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, getStrLevel(), hm, hmTotal, null,0.0d, null, null,0.0d);
									}
									if(!isMultipePerWithParticularHead) {
										hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hm.get("AMOUNT"))));
										dblGross += uF.parseToDouble(hm.get("AMOUNT"));
										dblGrossPT += uF.parseToDouble(hm.get("AMOUNT"));
										dblGrossTDS += uF.parseToDouble(hm.get("AMOUNT"));
//										System.out.println("ArP/835--strSalaryId="+strSalaryId+"--dblGross=="+hm.get("AMOUNT"));
										alEarningHead.add(strSalaryId);
										
									}
								}

								break;
						}
					} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")) {
						/**
						 * TAX CALCULATION STARTS HERE
						 * 
						 * */

						switch (nSalayHead) {

							/********** EPF EMPLOYEE CONTRIBUTION *************/
							case EMPLOYEE_EPF :
								Map<String, String> hmVoluntaryPF = hmInner.get(VOLUNTARY_EPF + "");

//								double dblEEPF = approvePay.calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, getStrEmpId(), null, null, false, null,null,null, null, null, null);
								double dblEEPF = 0;
								if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
									dblEEPF = approvePay.calculateEEPF(con, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, getStrEmpId(), null, null, false, null,null,null, null, null, null);
//									System.out.println("ArP/859--strSalaryId="+strSalaryId+"--dblGross=="+dblGross);
								}
								dblDeduction += dblEEPF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblEEPF));
								
								alDeductionHead.add(strSalaryId);
								isEPF = true;

								break;

							default :
								if (uF.parseToInt(strSalaryId) != PROFESSIONAL_TAX && uF.parseToInt(strSalaryId) != TDS
											&& uF.parseToInt(strSalaryId) != EMPLOYEE_ESI && uF.parseToInt(strSalaryId) != EMPLOYER_ESI
											&& uF.parseToInt(strSalaryId) != EMPLOYEE_LWF && uF.parseToInt(strSalaryId) != EMPLOYER_LWF) {
									hmTotal.put(strSalaryId,uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
									dblDeduction += uF.parseToDouble(hm.get("AMOUNT"));
									
									alDeductionHead.add(strSalaryId);
								}
								break;
						}
					}
//					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				
				/**
				 * Multiple cal start
				 * */
				Map<String, String> hmContriSalHeadAmt = new HashMap<String, String>();
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_EPF)) {
//					double dblERPF = approvePay.calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, null,null,null, null, null, null);
					double dblERPF = 0;
					if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
						dblERPF = approvePay.calculateERPF(con, CF, null, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false, null,null,null, null, null, null);
					}
//					calculateERPF(con, CF, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, strArrearPaycycleTo, hmEmployerPF);
					hmContriSalHeadAmt.put(""+EMPLOYEE_EPF, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblERPF));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_ESI)) {
//					double dblESI = approvePay.calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, null, null, strD1, strD2, strPC, hmTotal);//need to add hmTotalESIC
					double dblESI = 0;
					if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
						dblESI = approvePay.calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), strEmpId, null, null, strD1, strD2, strPC, hmTotal);//need to add hmTotalESIC
					}
					hmContriSalHeadAmt.put(""+EMPLOYER_ESI, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblESI));
				}
				if(setContriSalHead != null && setContriSalHead.contains(""+EMPLOYER_LWF)) {
					double dblLWF = approvePay.calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
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
						double dblMulCalAmt = approvePay.getMultipleCalAmtDetails(con, uF, CF, nSalayHead, getStrEmpId(), dblArrearDays, nTotalNumberOfDays, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, getStrLevel(), hm, hmTotal, null,0.0d, null, null,0.0d, hmContriSalHeadAmt,false);
						if(!hmTotal.containsKey(strSalaryId)) {
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblGross += dblMulCalAmt;
							dblGrossTDS += dblMulCalAmt;
//							System.out.println("ArP/927--strSalaryId="+strSalaryId+"--dblGross=="+dblGross);
							
							alEarningHead.add(strSalaryId);
						}
					} else if (str_E_OR_D != null && str_E_OR_D.equalsIgnoreCase("D")
							&& (hm.get("SALARY_AMOUNT_TYPE") != null && hm.get("SALARY_AMOUNT_TYPE").trim().equalsIgnoreCase("P"))) {
						double dblMulCalAmt = approvePay.getMultipleCalAmtDetails(con, uF, CF, nSalayHead, getStrEmpId(), dblArrearDays, nTotalNumberOfDays, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, getStrLevel(), hm, hmTotal, null,0.0d, null, null,0.0d, hmContriSalHeadAmt,false);
						if(!hmTotal.containsKey(strSalaryId)) {
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblMulCalAmt));
							dblDeduction += dblMulCalAmt;
							
							alDeductionHead.add(strSalaryId);
						}
					}

//					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
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
								double dblPt = approvePay.calculateProfessionalTax(con, uF, strArrearPaycycleTo, dblGross, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpStateMap.get(getStrEmpId()), strEmpGender);
								dblDeduction += dblPt;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPt));
								
								alDeductionHead.add(strSalaryId);

								break;

								/********** ESI EMPLOYEE CONTRIBUTION *************/
							case EMPLOYEE_ESI :
//								double dblESI = approvePay.calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(getStrEmpId()), null, getStrEmpId(), null, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, hmTotal); //need to add hmTotalESIC
								double dblESI = 0;
								if(hmFeatureStatus != null && uF.parseToBoolean(hmFeatureStatus.get(F_PF_ESI_INCLUDE_IN_ARREAR))){
									dblESI = approvePay.calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal,hmEmpStateMap.get(getStrEmpId()), null, getStrEmpId(), null, strArrearPaycycleFrom, strArrearPaycycleTo, ""+nArrearPaycycle, hmTotal); //need to add hmTotalESIC
								}
								
								dblESI = Math.ceil(dblESI);
								
									
								dblDeduction += dblESI;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblESI));
								
								alDeductionHead.add(strSalaryId);
								
								isESIC = true;

								break;
							/********** /ESI EMPLOYEE CONTRIBUTION *************/	
								
							/********** LWF EMPLOYEE CONTRIBUTION *************/
							case EMPLOYEE_LWF :
								double dblLWF = approvePay.calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmEmpStateMap.get(getStrEmpId()), null, getStrEmpId(), nPayMonth, null, strOrgId);
								dblDeduction += dblLWF;
								hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblLWF));
								
								alDeductionHead.add(strSalaryId);
								isLWF = true;
								
								break;
							/********** /LWF EMPLOYEE CONTRIBUTION *************/	

						}
					}

//					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
					hmTotal.put("GROSS", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross)));
				}
				/**
				 * Other cal end
				 * */

				/*if(uF.parseToInt(strEmpId) == 421) {
					System.out.println("AR/1021---dblGross="+dblGross);
				}*/

				hmTotal.put("NET", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),Math.round(dblGross - dblDeduction)));
				
//				System.out.println(" hmTotal ===????> " + hmTotal);
				
				hmArrearCalSalary.put(getStrEmpId()+"_"+nArrearPaycycle, hmTotal);
				
				hmArrearEarningHead.put(getStrEmpId()+"_"+nArrearPaycycle, alEarningHead);
				
				hmArrearDeductionHead.put(getStrEmpId()+"_"+nArrearPaycycle, alDeductionHead);
				
				if(isEPF) {
					/**
					 * Employee PF
					 * */
					Map<String, String> hmEmployeePF = new HashMap<String, String>();
					calculateEEPF(con, uF, strFinancialYearStart,strFinancialYearEnd, hmTotal,getStrEmpId(),hmEmployeePF);
					
					hmArrearEmployeePF.put(getStrEmpId()+"_"+nArrearPaycycle, hmEmployeePF);
					
					/**
					 * Employer PF
					 * */
					Map<String, String> hmEmployerPF = new HashMap<String, String>();
					calculateERPF(con, CF, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, getStrEmpId(), hmEmployerPF);
					
					hmArrearEmployerPF.put(getStrEmpId()+"_"+nArrearPaycycle, hmEmployerPF);
				}
				if(isESIC) {
					Map<String, String> hmEmployerESI = new HashMap<String, String>();
					calculateEESI(con, uF, strFinancialYearStart, strFinancialYearEnd,hmTotal,getStrEmpId(), strArrearPaycycleFrom, 
							strArrearPaycycleTo, ""+nArrearPaycycle, hmEmpStateMap,hmEmployerESI);
					
					hmArrearEmployerESI.put(getStrEmpId()+"_"+nArrearPaycycle, hmEmployerESI);
				}
				if(isLWF) {
					Map<String, String> hmEmployeeLWF = new HashMap<String, String>();
					calculateELWF(con, uF, strFinancialYearStart, strFinancialYearEnd, hmTotal, getStrEmpId(), strArrearPaycycleFrom, strArrearPaycycleTo, hmEmpStateMap,hmEmployeeLWF,strOrgId);
					
					hmArrearEmployeeLWF.put(getStrEmpId()+"_"+nArrearPaycycle, hmEmployeeLWF);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}
	
	
	
	public void payIncrementArrear(Connection con, UtilityFunctions uF, String strD1, String strD2, String strPC, Map<String, List<Map<String, String>>> hmIncrementEmpArrear,
			Map<String, Map<String, String>> hmIncrementArrearCalSalary, Map<String, List<String>> hmIncrementArrearEarningHead, Map<String, List<String>> hmIncrementArrearDeductionHead, 
			Map<String, Map<String, String>> hmIncrementArrearEmployeePF, Map<String, Map<String, String>> hmIncrementArrearEmployerPF, Map<String, Map<String, String>> hmIncrementArrearEmployerESI,
			Map<String, Map<String, String>> hmIncrementArrearEmployeeLWF, Map<String, List<String>> hmIncrementArrearPaycycle) {
			PreparedStatement pst = null;
			ResultSet rs = null;
			try { 
//				System.out.println("in payArrear ........................");
				int nEmpId = uF.parseToInt(getStrEmpId());
				Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
				String strOrgId = hmEmpOrgId.get(getStrEmpId());
				if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>();
				if(hmEmpStateMap == null) hmEmpStateMap = new HashMap<String, String>();
				if(hmOtherTaxDetails == null) hmOtherTaxDetails = new HashMap<String, String>();
				if(hmEmpServiceTaxMap == null) hmEmpServiceTaxMap = new HashMap<String, String>();
				
				if(hmIncrementEmpArrear == null) hmIncrementEmpArrear = new LinkedHashMap<String, List<Map<String,String>>>();
				if(hmIncrementArrearCalSalary==null) hmIncrementArrearCalSalary = new LinkedHashMap<String, Map<String,String>>();
				if(hmIncrementArrearEarningHead == null) hmIncrementArrearEarningHead = new LinkedHashMap<String, List<String>>();
				if(hmIncrementArrearDeductionHead == null) hmIncrementArrearDeductionHead = new LinkedHashMap<String, List<String>>();
				
				pst = con.prepareStatement("select arear_id from arrear_generation where emp_id=? and paycycle<=? group by arear_id");
				pst.setInt(1, nEmpId);
				pst.setInt(2, uF.parseToInt(strPC));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				List<String> alAppliedArrearId = new ArrayList<String>();
				while(rs.next()) {
					alAppliedArrearId.add(rs.getString("arear_id"));
				}
				rs.close();
				pst.close();		
//				System.out.println("alAppliedArrearId==>"+alAppliedArrearId);
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from arear_details where is_paid=false and arrear_type=2 and is_approved=1 and emp_id=? and effective_date <=?");
				if(alAppliedArrearId.size() > 0) {
					String strArrearIds = StringUtils.join(alAppliedArrearId.toArray(),",");
					sbQuery.append(" and arear_id not in("+strArrearIds+")");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, nEmpId);
//				pst.setInt(2, uF.parseToInt(strPC));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//				System.out.println("pst===>> " + pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alArrear = new ArrayList<Map<String,String>>();
				while(rs.next()) {
					Map<String,String> hmApplyArear = new HashMap<String, String>();
					hmApplyArear.put("ARREAR_ID", rs.getString("arear_id"));
//					hmApplyArear.put("ARREAR_DAYS", rs.getString("arrear_days"));
//					hmApplyArear.put("ARREAR_PAYCYCLE_FROM", uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT));
//					hmApplyArear.put("ARREAR_PAYCYCLE_TO", uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT));
//					hmApplyArear.put("ARREAR_PAYCYCLE", rs.getString("paycycle"));
//					
//					Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//					cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "dd")));
//					cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "MM"))-1);
//					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, "yyyy")));
//					int nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
//					hmApplyArear.put("ARREAR_PAYCYCLE_TOTAL_DAYS", ""+nTotalNumberOfDays);
					
					alArrear.add(hmApplyArear);
				}
				rs.close();
				pst.close();
//				System.out.println("alArrear==>"+alArrear);
				hmIncrementEmpArrear.put(""+nEmpId, alArrear);
//				System.out.println("hmIncrementEmpArrear ===>>>> " + hmIncrementEmpArrear);
				
				for(Map<String,String> hmApplyArear : alArrear) {
					int nArrearId = uF.parseToInt(hmApplyArear.get("ARREAR_ID"));
					
					pst = con.prepareStatement("select * from arrear_headwise_details where arear_id=?");
					pst.setInt(1, nArrearId);
					rs = pst.executeQuery();
					Map<String, Map<String, Map<String, String>>> hmPaycycleInner = new LinkedHashMap<String, Map<String, Map<String, String>>>();
//					Map<String, Map<String, String>> hmInner = new LinkedHashMap<String, Map<String, String>>();
					Map<String, List<String>> hmArrearDataPaycylcewise = new HashMap<String, List<String>>();
					while(rs.next()) {
						Map<String, Map<String, String>> hmInner = hmPaycycleInner.get(rs.getString("paycycle"));
						if(hmInner == null) hmInner = new LinkedHashMap<String, Map<String, String>>();
						
						Map<String, String> hmSalaryInner = new HashMap<String, String>();
						hmSalaryInner.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
						hmSalaryInner.put("AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), rs.getDouble("amount")));
						hmSalaryInner.put("PAYCYCLE_START_DATE", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
						hmSalaryInner.put("PAYCYCLE_END_DATE", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
						hmSalaryInner.put("FY_START_DATE", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
						hmSalaryInner.put("FY_END_DATE", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
						hmInner.put(rs.getString("salary_head_id"), hmSalaryInner);
						
						List<String> alInner = new ArrayList<String>();
						alInner.add(rs.getString("paid_days"));
						alInner.add(rs.getString("paid_leaves"));
						alInner.add(rs.getString("present_days"));
						alInner.add(rs.getString("total_days"));
						hmArrearDataPaycylcewise.put(rs.getString("paycycle"), alInner);
						
						hmPaycycleInner.put(rs.getString("paycycle"), hmInner);
					}
					rs.close();
					pst.close();
					
					Iterator<String> it1 = hmPaycycleInner.keySet().iterator();
					while (it1.hasNext()) {
						String strPaycycle = it1.next();
//						System.out.println("strPaycycle ========>> " + strPaycycle);
						
						Map<String, String> hmTotal = new LinkedHashMap<String, String>();
						List<String> alEarningHead = new ArrayList<String>();
						List<String> alDeductionHead = new ArrayList<String>();
						
						Map<String, Map<String, String>> hmInner = hmPaycycleInner.get(strPaycycle);
						Iterator<String> it = hmInner.keySet().iterator();
						double dblGrossPT = 0;
						double dblGross = 0;
						double dblGrossTDS = 0;
						double dblDeduction = 0;
						boolean isEPF = false;
						boolean isESIC = false;
						boolean isLWF = false;
						Set<String> setContriSalHead = new HashSet<String>();
						String strPaycycleStrtDate = null;
						String strPaycycleEndDate = null;
						String strFYStrtDate = null;
						String strFYEndDate = null;
						while (it.hasNext()) {
							String strSalaryId = it.next();
							int nSalayHead = uF.parseToInt(strSalaryId);
	
							Map<String, String> hm = hmInner.get(strSalaryId);
							if (hm == null) hm = new HashMap<String, String>();
							if(strPaycycleStrtDate == null && hm.get("PAYCYCLE_START_DATE") != null) {
								strPaycycleStrtDate = hm.get("PAYCYCLE_START_DATE");
								strPaycycleEndDate = hm.get("PAYCYCLE_END_DATE");
								strFYStrtDate = hm.get("FY_START_DATE");
								strFYEndDate = hm.get("FY_END_DATE");
							}
							
//							System.out.println("strFYStrtDate ========>> " + strFYStrtDate);
//							System.out.println("strFYEndDate ========>> " + strFYEndDate);
							
							if(nSalayHead == EMPLOYEE_EPF) {
								isEPF = true;
							}
							if(nSalayHead == EMPLOYEE_ESI) {
								isESIC = true;
							}
							if(nSalayHead == EMPLOYEE_LWF) {
								isLWF = true;
							}
							
							if(hm.get("EARNING_DEDUCTION") != null && hm.get("EARNING_DEDUCTION").equals("E")) {
								alEarningHead.add(strSalaryId);
							} else if(hm.get("EARNING_DEDUCTION") != null && hm.get("EARNING_DEDUCTION").equals("D")) {
								alDeductionHead.add(strSalaryId);
							}
							hmTotal.put(strSalaryId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hm.get("AMOUNT"))));
						}
	
//						System.out.println("hmTotal==>"+hmTotal);
						
						hmIncrementArrearCalSalary.put(getStrEmpId()+"_"+nArrearId+"_"+strPaycycle, hmTotal);
						
						hmIncrementArrearEarningHead.put(getStrEmpId()+"_"+nArrearId+"_"+strPaycycle, alEarningHead);
						
						hmIncrementArrearDeductionHead.put(getStrEmpId()+"_"+nArrearId+"_"+strPaycycle, alDeductionHead);
						
						if(isEPF) {
							/**
							 * Employee PF
							 * */
							Map<String, String> hmEmployeePF = new HashMap<String, String>();
//							System.out.println("strFYStrtDate ===============>> " + strFYStrtDate);
//							System.out.println("strFYEndDate ===============>> " + strFYEndDate);
							calculateEEPF(con, uF, strFYStrtDate,strFYEndDate, hmTotal,getStrEmpId(),hmEmployeePF);
							
							hmIncrementArrearEmployeePF.put(getStrEmpId()+"_"+nArrearId+"_"+strPaycycle, hmEmployeePF);
							
							/**
							 * Employer PF
							 * */
							Map<String, String> hmEmployerPF = new HashMap<String, String>();
							calculateERPF(con, CF, uF, strFYStrtDate, strFYEndDate, hmTotal, getStrEmpId(), hmEmployerPF);
							
							hmIncrementArrearEmployerPF.put(getStrEmpId()+"_"+nArrearId+"_"+strPaycycle, hmEmployerPF);
						}
						if(isESIC) {
							Map<String, String> hmEmployerESI = new HashMap<String, String>();
							calculateEESI(con, uF, strFYStrtDate, strFYEndDate,hmTotal,getStrEmpId(), strPaycycleStrtDate, strPaycycleEndDate, strPaycycle, hmEmpStateMap,hmEmployerESI);
							
							hmIncrementArrearEmployerESI.put(getStrEmpId()+"_"+nArrearId+"_"+strPaycycle, hmEmployerESI);
						}
						if(isLWF) {
							Map<String, String> hmEmployeeLWF = new HashMap<String, String>();
							calculateELWF(con, uF, strFYStrtDate, strFYEndDate, hmTotal, getStrEmpId(), strPaycycleStrtDate, strPaycycleEndDate, hmEmpStateMap,hmEmployeeLWF,strOrgId);
							
							hmIncrementArrearEmployeeLWF.put(getStrEmpId()+"_"+nArrearId+"_"+strPaycycle, hmEmployeeLWF);
						}
						
						List<String> alArrearPaycycle = hmIncrementArrearPaycycle.get(""+nArrearId);
						if(alArrearPaycycle == null) alArrearPaycycle = new ArrayList<String>();
						alArrearPaycycle.add(strPaycycle);
						List<String> alInner = hmArrearDataPaycylcewise.get(strPaycycle);
						
						hmIncrementArrearPaycycle.put(""+nArrearId, alArrearPaycycle);
						hmIncrementArrearPaycycle.put(nArrearId+"_"+strPaycycle+"_OTHER_INFO", alInner);
						
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(rs!=null) {
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(pst!=null) {
					try {
						pst.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			} 
		}
	
	
	
	
	
	public void calculateELWF(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, String strEmpId, 
			String strArrearPaycycleFrom, String strArrearPaycycleTo, Map<String, String> hmEmpStateMap, Map<String, String> hmEmployeeLWF, String strOrgId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmountEELWF = 0;
		double dblCalculatedAmountERLWF = 0;

		try {
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
				if (uF.parseToInt(arrSalaryHeads[i]) != OVER_TIME) {
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

			int month = uF.parseToInt(uF.getDateFormat(strArrearPaycycleTo, DATE_FORMAT, "MM"));
			if (lwfMonthList == null || !lwfMonthList.contains("" + month)) {
				return;
			}

			dblCalculatedAmountEELWF = dblEELWFAmount;
			dblCalculatedAmountERLWF = dblERLWFAmount;

			hmEmployeeLWF.put("LWF_SALARY_HEADS", strSalaryHeads);
			hmEmployeeLWF.put("LWF_MAX_LIMIT", ""+dblAmount);
			hmEmployeeLWF.put("LWF_EELWF_CONTRIBUTION", ""+dblCalculatedAmountEELWF);
			hmEmployeeLWF.put("LWF_ERLWF_CONTRIBUTION", ""+dblCalculatedAmountERLWF);


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
	
	public void calculateEESI(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strEmpId, String strD1, String strD2, String strPaycycle, Map<String, String> hmEmpStateMap,
			Map<String, String> hmEmployerESI) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmountEESI = 0.0d;
		double dblCalculatedAmountERSI = 0.0d;

		try {
			
			
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
			
			hmEmployerESI.put("ESI_SALARY_HEADS", strSalaryHeads);
			hmEmployerESI.put("ESI_MAX_LIMIT", ""+dblAmount);
			hmEmployerESI.put("ESI_EMPLOYEE_CONTRIBUTION", ""+Math.ceil(dblCalculatedAmountEESI));
			hmEmployerESI.put("ESI_EMPLOYER_CONTRIBUTION", ""+Math.ceil(dblCalculatedAmountERSI));

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
	
	public double calculateEEPF(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal, String strEmpId, Map<String, String> hmEmployeePF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		try {

			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? "
				+ "and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id " +
				"from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and " +
				"dd.level_id = ld.level_id and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
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
			
			double dblAmount = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
//				if(uF.parseToInt(strEmpId) == 4581) {
//					System.out.println("arrSalaryHeads[i]==>"+arrSalaryHeads[i]+"--hmTotal.get(arrSalaryHeads[i])==>"+hmTotal.get(arrSalaryHeads[i]));
//				}
			}

			/**
			 * Change on 24-04-2012
			 */

			if (dblAmount >= dblMaxAmount) {
				dblAmount = dblMaxAmount;

			}
			dblCalculatedAmount = (dblEEPFAmount * dblAmount) / 100;
			
			double dblEVPF = uF.parseToDouble(hmTotal.get(VOLUNTARY_EPF + ""));
				
			hmEmployeePF.put("EPF_SALARY_HEADS", strSalaryHeads);
			hmEmployeePF.put("EPF_MAX_LIMIT", ""+dblAmount);
			hmEmployeePF.put("EPF_EEPF_CONTRIBUTION", ""+dblCalculatedAmount);
			hmEmployeePF.put("EPF_EVPF_CONTRIBUTION", ""+dblEVPF);

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
	
	public void calculateERPF(Connection con, CommonFunctions CF, UtilityFunctions uF, String strFinancialYearStart,
			String strFinancialYearEnd, Map<String, String> hmTotal, String strEmpId, Map<String, String> hmEmployerPF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblEPS1 = 0;
		double dblEPS = 0;
		double dblEPF = 0;
		double dblEDLI = 0;

		double dblEPFAdmin = 0;
		double dblEDLIAdmin = 0;

//		double dblTotalEPF = 0;
//		double dblTotalEDLI = 0;
		Database db = null;
		try {

			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? "
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

			dblEPF = ((dblERPFAmount * dblAmountERPF) / 100);
			dblEPS = ((dblERPSAmount * dblAmountERPS) / 100);

			dblEPS1 = ((dblERPSAmount * dblAmountERPS1) / 100);

			dblEDLI = ((dblERDLIAmount * dblAmountEREDLI) / 100);
			dblEDLIAdmin = ((dblEDLIAdminAmount * dblAmountEREDLI) / 100);
			dblEPFAdmin = ((dblPFAdminAmount * dblAmountEEPF) / 100);
			

			if (CF.isEPF_Condition1()) {
				dblEPF += dblEPS1 - dblEPS;
			}

//			dblTotalEDLI = dblEDLI + dblEDLIAdmin;
//			dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;
				
			
			hmEmployerPF.put("EPS_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(dblAmountERPS));
			hmEmployerPF.put("EDLI_MAX_LIMIT", uF.formatIntoTwoDecimalWithOutComma(dblAmountEREDLI));
			hmEmployerPF.put("ERPF_CONTRIBUTION", ""+dblEPF);
			hmEmployerPF.put("ERPS_CONTRIBUTION", ""+dblEPS);
			hmEmployerPF.put("ERDLI_CONTRIBUTION", ""+dblEDLI);
			hmEmployerPF.put("PF_ADMIN_CHARGES", uF.formatIntoTwoDecimalWithOutComma(dblEPFAdmin));
			hmEmployerPF.put("EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimalWithOutComma(dblEDLIAdmin));

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


	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrEmpGender() {
		return strEmpGender;
	}

	public void setStrEmpGender(String strEmpGender) {
		this.strEmpGender = strEmpGender;
	}


	
}