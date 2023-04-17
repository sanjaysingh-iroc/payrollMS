package com.konnect.jpms.requsitions;

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

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayReimbursementCTC extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String f_level;
	String[] f_service;
	
	List<FillOrganisation> orgList; 
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String exportType;
	
	String paycycle;
	List<FillPayCycles> paycycleList;
	
	String bankAccount;
	String bankAccountType;
	List<FillBank> bankList;
	
	String strApprove;
	String[] reimbId; 
	String alertID;
	
	List<String> strEmpCTCId;
	String paidStatus;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/requisitions/PayReimbursementCTC.jsp");
		request.setAttribute(TITLE, "Pay Reimbursement CTC");
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		if(getStrApprove()!=null && getStrApprove().equalsIgnoreCase("PAY")){
			payReimbursement(uF);
		}
		
		if(uF.parseToInt(getBankAccountType()) == 0){
			setBankAccountType("2");
		}
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		if(uF.parseToInt(getF_level())==0 && levelList!=null && levelList.size()>0){
			setF_level(levelList.get(0).getLevelId());
		}
		
		if(uF.parseToInt(getPaidStatus()) == 0){
			setPaidStatus("2");
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			if(strUserType != null && strUserType.equalsIgnoreCase(OTHER_HR) && session.getAttribute(DEPARTMENTID) != null){
				String[] deptArr = {(String)session.getAttribute(DEPARTMENTID)};
				setF_department(deptArr);
			} else{
				setF_department(null);
			}
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		
		if(uF.parseToInt(getPaidStatus()) == 2){
			viewPayReimbursement(uF);
		} else {
			viewPaidReimbursement(uF);
		}

		return loadReimbursements(uF);
	}
	
	private void payReimbursement(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			
			
			
			String[] strPayCycleDates = null;
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			String strPC = strPayCycleDates[2];
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strPayCycleDates[1], CF, uF);
			if (strFinancialYear != null && strFinancialYear.length > 0) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			String P_strFinancialYearStart = (String) session.getAttribute("P_strFinancialYearStart");
			String P_strFinancialYearEnd = (String) session.getAttribute("P_strFinancialYearEnd");
			String P_levelId = (String) session.getAttribute("P_levelId");
			String P_orgId = (String) session.getAttribute("P_orgId");
			String P_paycycleFrom = (String) session.getAttribute("P_paycycleFrom");
			String P_paycycleTo = (String) session.getAttribute("P_paycycleTo");
			String P_paycycle = (String) session.getAttribute("P_paycycle");
			
//			System.out.println("strD1==>"+strD1);
//			System.out.println("strD2==>"+strD2);
//			System.out.println("P_paycycleFrom==>"+P_paycycleFrom);
//			System.out.println("P_paycycleTo==>"+P_paycycleTo);
//			
//			System.out.println("strFinancialYearStart==>"+strFinancialYearStart);
//			System.out.println("strFinancialYearEnd==>"+strFinancialYearEnd);
//			System.out.println("P_strFinancialYearStart==>"+P_strFinancialYearStart);
//			System.out.println("P_strFinancialYearEnd==>"+P_strFinancialYearEnd);
//			System.out.println("strPC==>"+strPC);
//			System.out.println("P_paycycle==>"+P_paycycle);
//			System.out.println("getF_org==>"+getF_org());
//			System.out.println("P_orgId==>"+P_orgId);
//			System.out.println("getF_level==>"+getF_level());
//			System.out.println("P_levelId==>"+P_levelId);
			
			Date stD1 = uF.getDateFormat(strD1, DATE_FORMAT);
			Date stPD1 = uF.getDateFormat(P_paycycleFrom, DATE_FORMAT);

			Date stD2 = uF.getDateFormat(strD2, DATE_FORMAT);
			Date stPD2 = uF.getDateFormat(P_paycycleTo, DATE_FORMAT);

			boolean check = stD1.equals(stPD1);
			boolean check1 = stD2.equals(stPD2);
			
//			System.out.println("check==>"+check);
//			System.out.println("check1==>"+check1);
			
			Date stFYStart = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT);
			Date stPFYStart = uF.getDateFormat(P_strFinancialYearStart, DATE_FORMAT);

			Date stFYEnd = uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT);
			Date stPFYEnd = uF.getDateFormat(P_strFinancialYearEnd, DATE_FORMAT);

			boolean checkFYStart = stFYStart.equals(stPFYStart);
			boolean checkFYEnd = stFYEnd.equals(stPFYEnd);
			

//			System.out.println("checkFYStart==>"+checkFYStart);
//			System.out.println("checkFYEnd==>"+checkFYEnd);

			if (check && check1 && checkFYStart && checkFYEnd && uF.parseToInt(strPC) == uF.parseToInt(P_paycycle) 
					&& uF.parseToInt(getF_org()) == uF.parseToInt(P_orgId) && uF.parseToInt(getF_level()) == uF.parseToInt(P_levelId)) {
//				System.out.println("in if ==>");
				
				Map<String, Map<String, String>> hmCurrencyDetails = CF.getCurrencyDetails(con);
				if (hmCurrencyDetails == null) hmCurrencyDetails = new LinkedHashMap<String, Map<String, String>>();
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if (hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			
				Map<String, String> hmReimCTCName = (Map<String, String>)session.getAttribute("P_hmReimCTCName");
				if(hmReimCTCName == null) hmReimCTCName = new HashMap<String, String>();
				Map<String, String> hmEmpName = (Map<String, String>)session.getAttribute("P_hmEmpName");
				if(hmEmpName == null) hmEmpName = new HashMap<String, String>();
				Map<String, String> hmEmpCode = (Map<String, String>)session.getAttribute("P_hmEmpCode");
				if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
				Map<String, String> hmReimCTCAmount = (Map<String, String>)session.getAttribute("P_hmReimCTCAmount");
				if(hmReimCTCAmount == null) hmReimCTCAmount = new HashMap<String, String>();
				Map<String, String> hmReimCTCHead = (Map<String, String>)session.getAttribute("P_hmReimCTCHead");
				if(hmReimCTCHead == null) hmReimCTCHead = new HashMap<String, String>();
				Map<String, Map<String, Map<String, String>>> hmReimbursementCTC = (Map<String, Map<String, Map<String, String>>>)session.getAttribute("P_hmReimbursementCTC");
				if(hmReimbursementCTC == null) hmReimbursementCTC = new HashMap<String, Map<String,Map<String,String>>>();
				Map<String, Map<String, String>> hmEmpAttendance = (Map<String, Map<String, String>>)session.getAttribute("P_hmEmpAttendance");
				if(hmEmpAttendance == null) hmEmpAttendance = new HashMap<String, Map<String, String>>();
				
				Map<String, String> hmOrg = CF.getOrgDetails(con, uF, getF_org());
				Map<String, String> hmStates = CF.getStateMap(con);
				Map<String, String> hmCountry = CF.getCountryMap(con);
				String strBankCode = null;
				String strBankName = null;
				String strBankAddress = null;
				Map<String, String> hmBankBranch = new HashMap<String, String>();
				
				pst = con.prepareStatement("select bd.bank_account_no, bd.branch_id, bd1.bank_name,bd.bank_branch, bd.branch_code, bd.bank_address, bd.bank_city, bd.bank_pincode, bd.bank_state_id, bd.bank_country_id from bank_details bd1, branch_details bd where bd1.bank_id = bd.bank_id");
				rs = pst.executeQuery();
				while(rs.next()){
					if(rs.getInt("branch_id")==uF.parseToInt(getBankAccount())){
						strBankCode = rs.getString("branch_code");
						strBankName = rs.getString("bank_name");
						strBankAddress = rs.getString("bank_address")+"<br/>"+rs.getString("bank_city")+" - "+rs.getString("bank_pincode")+"<br/>"+uF.showData(hmStates.get(rs.getString("bank_state_id")), "")+", "+uF.showData(hmCountry.get(rs.getString("bank_country_id")), "");
					}
					hmBankBranch.put(rs.getString("branch_id"), rs.getString("bank_branch")+"["+rs.getString("branch_code")+"]");
				}
				rs.close();
				pst.close();
				
				int nMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
				int nYear = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy"));
				int nCount = 0;				
				double dblTotalAmount = 0.0d;
				List<String> alReimbEmpCTCPayId = new ArrayList<String>();
				int nEmpCTCSize = getStrEmpCTCId() != null ? getStrEmpCTCId().size() : 0;
				Map<String, String> hmEmpReimCTCAmount = new HashMap<String, String>();
				for(int i = 0; i < nEmpCTCSize; i++){
					String strEmpCTCId = getStrEmpCTCId().get(i);
					String[] temp = strEmpCTCId.split("_");
					String strEmpId = temp[0];
					String strReimCTCId = temp[1];		
					
					Map<String, String> hmEmpPay = hmEmpAttendance.get(strEmpId);
					if(hmEmpPay == null) hmEmpPay = new HashMap<String, String>();
					double dblTotalDays = uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"));
					double dblPaidDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"));
					double dblPresentDays = uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"));
					double dblPaidLeaves = uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"));
					double dblAbsentDays = uF.parseToDouble(hmEmpPay.get("EMP_ABSENT_DAYS"));
					
					Map<String, Map<String, String>> hmReimbursementCTCInner = hmReimbursementCTC.get(strEmpCTCId);
					if(hmReimbursementCTCInner == null) hmReimbursementCTCInner = new HashMap<String, Map<String,String>>();
					
					Iterator<String> it = hmReimCTCHead.keySet().iterator();
					while(it.hasNext()){
						String strReimCTCHeadId = it.next();
						Map<String, String> hmReimbursementHeadInner = hmReimbursementCTCInner.get(strReimCTCHeadId);
						if(hmReimbursementHeadInner == null) hmReimbursementHeadInner = new HashMap<String, String>();
						
						pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod " +
								"where epd.emp_per_id = eod.emp_id and eod.emp_id=?");
						pst.setInt(1, uF.parseToInt(strEmpId));
						rs = pst.executeQuery();
						int nPaymentMode = 0;
						while(rs.next()){
							nPaymentMode = uF.parseToInt(rs.getString("payment_mode"));
						}
						rs.close();
						pst.close();
						
						
						double dblAmount = uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(hmReimbursementHeadInner.get("REIMBURSEMENT_HEAD_AMOUNT"))));
						dblTotalAmount += dblAmount;
						
						pst = con.prepareStatement("insert into reimbursement_ctc_pay (emp_id,pay_month,pay_year,reimbursement_ctc_id," +
								"reimbursement_head_id,amount,paid_from,paid_to,paycycle,financial_year_from,financial_year_to,currency_id," +
								"paid_by,paid_date,payment_mode,statement_id,bank_pay_type,total_days,paid_days,present_days,paid_leaves,absent_days) " +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setInt(2, nMonth);
						pst.setInt(3, nYear);
						pst.setInt(4, uF.parseToInt(strReimCTCId));
						pst.setInt(5, uF.parseToInt(strReimCTCHeadId));
						pst.setDouble(6, dblAmount);
						pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
						pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
						pst.setInt(9, uF.parseToInt(strPC));
						pst.setDate(10, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(11, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setInt(12, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
						pst.setInt(13, uF.parseToInt(strSessionEmpId));
						pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(15, nPaymentMode);
						pst.setInt(16, 0);
						pst.setInt(17, 0);
						pst.setDouble(18, dblTotalDays);
						pst.setDouble(19, dblPaidDays);
						pst.setDouble(20, dblPresentDays);
						pst.setDouble(21, dblPaidLeaves);
						pst.setDouble(22, dblAbsentDays);
//						System.out.println("pst==>"+pst);
						int x = pst.executeUpdate();
						pst.close();
						
						if(x > 0){
							
							pst = con.prepareStatement("select max(reimbursement_ctc_pay_id) as reimbursement_ctc_pay_id from reimbursement_ctc_pay");
							rs = pst.executeQuery();
							while (rs.next()) {
								alReimbEmpCTCPayId.add(rs.getString("reimbursement_ctc_pay_id"));
							}
							rs.close();
							pst.close();
							
							
							if(nPaymentMode == 1){
								double dblAmt = uF.parseToDouble(hmEmpReimCTCAmount.get(strEmpId));
								dblAmt += dblAmount;
								
								hmEmpReimCTCAmount.put(strEmpId,""+dblAmt);
							}							
						}						
					}
				}
				
				Iterator<String> it = hmEmpReimCTCAmount.keySet().iterator();
				StringBuilder sbEmpAmountBankDetails = new StringBuilder();
				while(it.hasNext()){
					String strEmpId = it.next();
					double dblAmount = uF.parseToDouble(hmEmpReimCTCAmount.get(strEmpId));
					
					pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod " +
							"where epd.emp_per_id = eod.emp_id and eod.emp_id=? and eod.payment_mode=1");
					pst.setInt(1, uF.parseToInt(strEmpId));
					rs = pst.executeQuery();							
					while(rs.next()){
						String strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr"),"");
						String strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name")),"");
						if(uF.parseToInt(getBankAccountType()) == 2){
							strBankAccNo = uF.showData(rs.getString("emp_bank_acct_nbr_2"),"");
							strBankBranch = uF.showData(hmBankBranch.get(rs.getString("emp_bank_name2")),"");
						}
						
						sbEmpAmountBankDetails.append("<tr>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+ ++nCount+".</font></td>");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),"")+"</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+strBankAccNo+"</font></td>");
						sbEmpAmountBankDetails.append("<td><font size=\"1\">"+strBankBranch+"</font></td>");
						sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">"+Math.round(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblAmount)))+"</font></td>");
						sbEmpAmountBankDetails.append("</tr>");
						
					}
					rs.close();
					pst.close();
				}
				
				String strContent = null;
				String strName = null;
				
				Map<String, String> hmActivityNode = CF.getActivityNode(con);
				if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
				
				int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
				if(nMonth>0 && alReimbEmpCTCPayId.size() > 0 && sbEmpAmountBankDetails.length() > 0){
					String strReimCTCPayIds = StringUtils.join(alReimbEmpCTCPayId.toArray(),",");
					
					pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' " +
							"and trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
					pst.setInt(1, uF.parseToInt(getF_org()));
					rs = pst.executeQuery();
					while(rs.next()){
						strContent = rs.getString("document_text");
					} 
					rs.close();
					pst.close();
					
					if(strContent!=null && strContent.indexOf("["+strBankCode+"]")>=0){
						strContent = strContent.replace("["+strBankCode+"]", strBankName +"<br/>"+strBankAddress);
					}
					
					if(strContent!=null && strContent.indexOf(DATE)>=0){
						strContent = strContent.replace(DATE, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
					}
					
					if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT)>=0){
						strContent = strContent.replace(PAYROLL_AMOUNT, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblTotalAmount));
					}
					
					if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT_WORDS)>=0){
						String digitTotal="";
				        String strTotalAmt=""+Math.round(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblTotalAmount)));
				        if(strTotalAmt.contains(".")){
				        	strTotalAmt=strTotalAmt.replace(".", ",");
				        	String[] temp=strTotalAmt.split(",");
				        	digitTotal=uF.digitsToWords(uF.parseToInt(temp[0]));
				        	if(uF.parseToInt(temp[1])>0){
				        		int pamt=0;
				        		if(temp[1].length()==1){
				        			pamt=uF.parseToInt(temp[1]+"0");
				        		}else{
				        			pamt=uF.parseToInt(temp[1]);
				        		}
				        		digitTotal+=" and "+uF.digitsToWords(pamt)+" paise";
				        	}
				        }else{
				        	int totalAmt1=(int) Math.round(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblTotalAmount)));
				        	digitTotal=uF.digitsToWords(totalAmt1);
				        }
				        strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, digitTotal);
					}
					
					if(strContent!=null && strContent.indexOf(PAY_MONTH)>=0){
						strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
					}
					
					if(strContent!=null && strContent.indexOf(PAY_YEAR)>=0){
						strContent = strContent.replace(PAY_YEAR, ""+nYear);
					}
					
					if(strContent!=null && strContent.indexOf(LEGAL_ENTITY_NAME)>=0){
						strContent = strContent.replace(LEGAL_ENTITY_NAME, uF.showData(hmOrg.get("ORG_NAME"), ""));
					}
					
					if(strContent!=null && nMonth>0){
						StringBuilder sbEmpBankDetails = new StringBuilder();
						
						sbEmpBankDetails.append("<table width=\"100%\">");
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
						sbEmpBankDetails.append("<td><b>Name</b></td>");
						sbEmpBankDetails.append("<td><b>Account No</b></td>");
						sbEmpBankDetails.append("<td><b>Branch</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
						sbEmpBankDetails.append("</tr>");
						
						sbEmpBankDetails.append(sbEmpAmountBankDetails);
						
						sbEmpBankDetails.append("<tr>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td>&nbsp;</td>");
						sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
						sbEmpBankDetails.append("<td align=\"right\"><b>"+Math.round(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblTotalAmount)))+"</b></td>");
						sbEmpBankDetails.append("</tr>");
						
						sbEmpBankDetails.append("</table>");
						
						strName = "BankStatement_"+nMonth+"_"+nYear;
						
						pst = con.prepareStatement("insert into payroll_bank_statement (statement_name, statement_body, generated_date, " +
								"generated_by, payroll_amount,bank_pay_type) values (?,?,?,?, ?,?)");
						pst.setString(1, strName);
						pst.setString(2, strContent+""+sbEmpBankDetails.toString());
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strSessionEmpId)); 
						pst.setDouble(5, Math.round(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblTotalAmount))));
						pst.setInt(6, uF.parseToInt(getBankAccountType()));
						pst.execute();
						pst.close();
						
						pst  = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
						rs = pst.executeQuery();
						int nMaxStatementId = 0;
						while(rs.next()){
							nMaxStatementId = rs.getInt("statement_id");
						}
						rs.close();
						pst.close();

						pst = con.prepareStatement("update reimbursement_ctc_pay set statement_id=?, bank_pay_type=? where reimbursement_ctc_pay_id in ("+strReimCTCPayIds+")");
						pst.setInt(1, nMaxStatementId);
						pst.setInt(2, uF.parseToInt(getBankAccountType()));
//						System.out.println("pst==>"+pst);
						pst.executeUpdate();
						pst.close();				
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void viewPaidReimbursement(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			String[] strPayCycleDates = null;
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			con = db.makeConnection(con);
			
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strPayCycleDates[1], CF, uF);
			if (strFinancialYear != null && strFinancialYear.length > 0) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
				
				Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
				if(hmEmpNames == null) hmEmpNames = new HashMap<String, String>();
				Map<String, String> hmEmpCodes = CF.getEmpCodeMap(con);
				if(hmEmpCodes == null) hmEmpCodes = new HashMap<String, String>();
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
				if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from reimbursement_ctc_pay rcp join reimbursement_head_details rhd on rhd.reimbursement_head_id=rcp.reimbursement_head_id " +
						"join reimbursement_ctc_details rcd on rhd.reimbursement_ctc_id=rcd.reimbursement_ctc_id " +
						"where rcp.financial_year_from=? and rcp.financial_year_to=? and rcp.paid_from=? and rcp.paid_to=? " +
						"and rcp.paycycle=? and rhd.level_id=? and rhd.org_id=? ");
				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
				}
				
				if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(uF.parseToInt(getF_level()) > 0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id ="+uF.parseToInt(getF_level())+") ");
	            }
	            if(getF_service()!=null && getF_service().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++){
	                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	                
	            }
	            
	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(")");
				}
				
				sbQuery.append(" order by rcp.emp_id, rcp.reimbursement_ctc_id,rcp.reimbursement_head_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(6, uF.parseToInt(getF_level()));
				pst.setInt(7, uF.parseToInt(getF_org()));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmReimCTCName = new HashMap<String, String>();
				Map<String, String> hmEmpName = new HashMap<String, String>();
				Map<String, String> hmEmpCode = new HashMap<String, String>();
				Map<String, String> hmReimCTCAmount = new HashMap<String, String>();
				Map<String, String> hmReimCTCHead = new LinkedHashMap<String, String>();
				Map<String, Map<String, Map<String, String>>> hmReimbursementCTC = new HashMap<String, Map<String,Map<String,String>>>();
				Map<String, Map<String, String>> hmEmpAttendance = new HashMap<String, Map<String, String>>();
				while(rs.next()){
					if(!hmReimCTCHead.containsKey(rs.getString("reimbursement_head_id"))){
						hmReimCTCHead.put(rs.getString("reimbursement_head_id"),rs.getString("reimbursement_head_name"));
					} 
					
					Map<String, String> hmEmpPay = new HashMap<String, String>();
					hmEmpPay.put("EMP_TOTAL_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("total_days"))));
					hmEmpPay.put("EMP_PAID_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_days"))));
					hmEmpPay.put("EMP_PRESENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("present_days"))));
					hmEmpPay.put("EMP_PAID_LEAVES", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_leaves"))));
					hmEmpPay.put("EMP_ABSENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("absent_days"))));

					hmEmpAttendance.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id"), hmEmpPay);
					
					double dblReimHeadAmount = uF.parseToDouble(rs.getString("amount")); 
					
					double dblAmount = uF.parseToDouble(hmReimCTCAmount.get(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id")));
					dblAmount += dblReimHeadAmount;
					hmReimCTCAmount.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblAmount));
					
					Map<String, Map<String, String>> hmReimbursementCTCInner = hmReimbursementCTC.get(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id"));
					if(hmReimbursementCTCInner == null) hmReimbursementCTCInner = new HashMap<String, Map<String,String>>();
					
					Map<String, String> hmReimbursementHeadInner = new HashMap<String, String>();
					hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursement_head_id"));
					hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblReimHeadAmount));
					
					hmReimbursementCTCInner.put(rs.getString("reimbursement_head_id"), hmReimbursementHeadInner);
					
					hmReimbursementCTC.put(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id"),hmReimbursementCTCInner);
					
					
					if(!hmReimCTCName.containsKey(rs.getString("reimbursement_ctc_id"))){
						hmReimCTCName.put(rs.getString("reimbursement_ctc_id"), rs.getString("reimbursement_name"));
					}
					if(!hmEmpCode.containsKey(rs.getString("emp_id"))){
						hmEmpCode.put(rs.getString("emp_id"), uF.showData(hmEmpCodes.get(rs.getString("emp_id")), ""));
					}
					if(!hmEmpName.containsKey(rs.getString("emp_id"))){
						hmEmpName.put(rs.getString("emp_id"), uF.showData(hmEmpNames.get(rs.getString("emp_id")), ""));
					}
					
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmReimbursementCTC",hmReimbursementCTC);
				request.setAttribute("hmReimCTCName",hmReimCTCName);
				request.setAttribute("hmEmpCode",hmEmpCode);
				request.setAttribute("hmEmpName",hmEmpName);
				request.setAttribute("hmReimCTCAmount",hmReimCTCAmount);
				request.setAttribute("hmReimCTCHead",hmReimCTCHead);
				request.setAttribute("hmEmpAttendance", hmEmpAttendance);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public void viewPayReimbursement(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			
			String[] strPayCycleDates = null;
			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			con = db.makeConnection(con);
			

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			String[] strFinancialYear = CF.getFinancialYear(con, strPayCycleDates[1], CF, uF);
			if (strFinancialYear != null && strFinancialYear.length > 0) {
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			
				
				Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
				if(hmEmpNames == null) hmEmpNames = new HashMap<String, String>();
				Map<String, String> hmEmpCodes = CF.getEmpCodeMap(con);
				if(hmEmpCodes == null) hmEmpCodes = new HashMap<String, String>();
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
				if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 "
						+ "and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? "
						+ "and at.approve_to<=? and (epd.employment_end_date is null OR epd.employment_end_date >= ?) and epd.joining_date<=? ");

				if(uF.parseToInt(getF_level()) > 0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id ="+uF.parseToInt(getF_level())+") ");
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
						+ "and paid_from = ? and paid_to=? group by emp_id) order by emp_fname, emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(8, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				Map<String, Map<String, String>> hmEmpAttendance = new HashMap<String, Map<String, String>>();
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
					
					String strEmpName = rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname");
					hmEmpPay.put("EMP_NAME", strEmpName);
					hmEmpPay.put("EMP_PAYMENT_MODE_ID", rs.getString("payment_mode"));
//					hmEmpPay.put("EMP_PAYMENT_MODE", uF.showData(hmPaymentModeMap.get(rs.getString("payment_mode")), ""));
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
						String[] tempService = rs.getString("service_id").split(",");
						if (tempService.length > 0) {
							hmEmpPay.put("EMP_SERVICE_ID", tempService[0]);
						}
					}

					hmEmpAttendance.put(rs.getString("emp_id"), hmEmpPay);
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select emp_id, reimbursement_ctc_id from reimbursement_ctc_pay where financial_year_from=? " +
						"and financial_year_to=? and paid_from=? and paid_to=? and paycycle=? ");
				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
				}
				
				if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(uF.parseToInt(getF_level()) > 0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id ="+uF.parseToInt(getF_level())+") ");
	            }
	            if(getF_service()!=null && getF_service().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++){
	                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	                
	            }
	            
	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(")");
				}
				
				sbQuery.append(" group by emp_id, reimbursement_ctc_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strPayCycleDates[2]));
				rs = pst.executeQuery();
				List<String> alPaidReimCTC = new ArrayList<String>();
				while(rs.next()){
					alPaidReimCTC.add(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from (select rcd.reimbursement_ctc_id as reimbursementctcid,rhd.reimbursement_head_id as reimbursementheadid," +
						"rhad.amount as reim_head_amount,* from reimbursement_assign_head_details rasd " +
						"join reimbursement_head_details rhd on rhd.reimbursement_head_id=rasd.reimbursement_head_id " +
						"join reimbursement_head_amt_details rhad on rhd.reimbursement_head_id=rhad.reimbursement_head_id " +
						"join reimbursement_ctc_details rcd on rhd.reimbursement_ctc_id=rcd.reimbursement_ctc_id " +
						"where rasd.financial_year_start=? and rasd.financial_year_end=? and rasd.level_id=? and rasd.org_id=? ");
				sbQuery.append(" and rasd.paycycle_from=? and rasd.paycycle_to=? and rasd.paycycle=? and rasd.status=true " +
						"and rasd.trail_status=true and rhd.level_id=? and rhd.org_id=? ");
				sbQuery.append(" and rhad.financial_year_start=? and rhad.financial_year_end=? and rhad.is_attachment= true and rhad.is_optimal=false ");
				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(" and rasd.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
				}
				
				if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(uF.parseToInt(getF_level()) > 0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+uF.parseToInt(getF_level())+" ) ");
	            }
	            if(getF_service()!=null && getF_service().length>0){
	                sbQuery.append(" and (");
	                for(int i=0; i<getF_service().length; i++){
	                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                    
	                    if(i<getF_service().length-1){
	                        sbQuery.append(" OR "); 
	                    }
	                }
	                sbQuery.append(" ) ");
	            }
	            
	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append(")");
				}
				sbQuery.append(") a order by a.emp_id, a.reimbursementctcid,a.reimbursementheadid");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(getF_level()));
				pst.setInt(4, uF.parseToInt(getF_org()));
				pst.setDate(5, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(7, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(8, uF.parseToInt(getF_level()));
				pst.setInt(9, uF.parseToInt(getF_org()));
				pst.setDate(10, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(11, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmReimCTCName = new HashMap<String, String>();
				Map<String, String> hmEmpName = new HashMap<String, String>();
				Map<String, String> hmEmpCode = new HashMap<String, String>();
				Map<String, String> hmReimCTCAmount = new HashMap<String, String>();
				Map<String, String> hmReimCTCHead = new LinkedHashMap<String, String>();
				Map<String, Map<String, Map<String, String>>> hmReimbursementCTC = new HashMap<String, Map<String,Map<String,String>>>();
				while(rs.next()){
					if(alPaidReimCTC.contains(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid"))){
						continue;
					}
					if(!hmEmpAttendance.containsKey(rs.getString("emp_id"))){
						continue;
					}
					
					if(!hmReimCTCHead.containsKey(rs.getString("reimbursementheadid"))){
						hmReimCTCHead.put(rs.getString("reimbursementheadid"),rs.getString("reimbursement_head_name"));
					}
					
					Map<String, String> hmEmpPay = hmEmpAttendance.get(rs.getString("emp_id"));
					if(hmEmpPay == null) hmEmpPay = new HashMap<String, String>();
					double dblTotalDays = uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"));
					double dblPaidDays = uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"));
					double dblPresentDays = uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"));
					double dblPaidLeaves = uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"));
					double dblAbsentDays = uF.parseToDouble(hmEmpPay.get("EMP_ABSENT_DAYS"));
					
					double dblReimHeadAmount1 = uF.parseToDouble(rs.getString("reim_head_amount")) > 0 ? uF.parseToDouble(rs.getString("reim_head_amount")) / 12d : 0.0d;
					double dblReimHeadAmount = 0.0d;
					if(dblTotalDays > 0 && dblPaidDays > 0){
						dblReimHeadAmount = dblReimHeadAmount1 * (dblPaidDays / dblTotalDays);
					}
					
					double dblAmount = uF.parseToDouble(hmReimCTCAmount.get(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid")));
					dblAmount += dblReimHeadAmount;
					hmReimCTCAmount.put(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblAmount));
					
					Map<String, Map<String, String>> hmReimbursementCTCInner = hmReimbursementCTC.get(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid"));
					if(hmReimbursementCTCInner == null) hmReimbursementCTCInner = new HashMap<String, Map<String,String>>();
					
					Map<String, String> hmReimbursementHeadInner = new HashMap<String, String>();
					hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursementheadid"));
					hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_AMT_ID", rs.getString("reimbursement_head_amt_id"));
					hmReimbursementHeadInner.put("REIMBURSEMENT_ASSIGN_HEAD_ID", rs.getString("reim_assign_head_id"));
					hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblReimHeadAmount));
					
					hmReimbursementCTCInner.put(rs.getString("reimbursementheadid"), hmReimbursementHeadInner);
					
					hmReimbursementCTC.put(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid"),hmReimbursementCTCInner);
					
					
					if(!hmReimCTCName.containsKey(rs.getString("reimbursementctcid"))){
						hmReimCTCName.put(rs.getString("reimbursementctcid"), rs.getString("reimbursement_name"));
					}
					if(!hmEmpCode.containsKey(rs.getString("emp_id"))){
						hmEmpCode.put(rs.getString("emp_id"), uF.showData(hmEmpCodes.get(rs.getString("emp_id")), ""));
					}
					if(!hmEmpName.containsKey(rs.getString("emp_id"))){
						hmEmpName.put(rs.getString("emp_id"), uF.showData(hmEmpNames.get(rs.getString("emp_id")), ""));
					}
					
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmReimbursementCTC==>"+hmReimbursementCTC);
//				System.out.println("hmReimCTCName==>"+hmReimCTCName);
//				System.out.println("hmEmpCode==>"+hmEmpCode);
//				System.out.println("hmEmpName==>"+hmEmpName);
//				System.out.println("hmReimCTCAmount==>"+hmReimCTCAmount);
//				System.out.println("hmReimCTCHead==>"+hmReimCTCHead);
				
				request.setAttribute("hmReimbursementCTC",hmReimbursementCTC);
				request.setAttribute("hmReimCTCName",hmReimCTCName);
				request.setAttribute("hmEmpCode",hmEmpCode);
				request.setAttribute("hmEmpName",hmEmpName);
				request.setAttribute("hmReimCTCAmount",hmReimCTCAmount);
				request.setAttribute("hmReimCTCHead",hmReimCTCHead);
				request.setAttribute("hmEmpAttendance", hmEmpAttendance);
				
				session.setAttribute("P_hmReimbursementCTC",hmReimbursementCTC);
				session.setAttribute("P_hmReimCTCName",hmReimCTCName);
				session.setAttribute("P_hmEmpCode",hmEmpCode);
				session.setAttribute("P_hmEmpName",hmEmpName);
				session.setAttribute("P_hmReimCTCAmount",hmReimCTCAmount);
				session.setAttribute("P_hmReimCTCHead",hmReimCTCHead);
				session.setAttribute("P_hmEmpAttendance", hmEmpAttendance);
				
				session.setAttribute("P_strFinancialYearStart", strFinancialYearStart);
				session.setAttribute("P_strFinancialYearEnd", strFinancialYearEnd);
				session.setAttribute("P_levelId", ""+uF.parseToInt(getF_level()));
				session.setAttribute("P_orgId", ""+uF.parseToInt(getF_org()));
				session.setAttribute("P_paycycleFrom", strPayCycleDates[0]);
				session.setAttribute("P_paycycleTo", strPayCycleDates[1]);
				session.setAttribute("P_paycycle", ""+uF.parseToInt(strPayCycleDates[2]));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	public void viewPayReimbursement(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try{
//			String[] strPayCycleDates = null;
//			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
//				strPayCycleDates = getPaycycle().split("-");
//			} else {
//				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
//				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//			}
//			
//			con = db.makeConnection(con);
//			
//			String strFinancialYearStart = null;
//			String strFinancialYearEnd = null;
//			String[] strFinancialYear = CF.getFinancialYear(con, strPayCycleDates[1], CF, uF);
//			if (strFinancialYear != null && strFinancialYear.length > 0) {
//				strFinancialYearStart = strFinancialYear[0];
//				strFinancialYearEnd = strFinancialYear[1];
//			
//				
//				Map<String, String> hmEmpNames = CF.getEmpNameMap(con, null, null);
//				if(hmEmpNames == null) hmEmpNames = new HashMap<String, String>();
//				Map<String, String> hmEmpCodes = CF.getEmpCodeMap(con);
//				if(hmEmpCodes == null) hmEmpCodes = new HashMap<String, String>();
//				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
//				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
//				if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
//				
//				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select emp_id, reimbursement_ctc_id from reimbursement_ctc_pay where financial_year_from=? " +
//						"and financial_year_to=? and paid_from=? and paid_to=? and paycycle=? ");
//				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
//				}
//				
//				if(uF.parseToInt(getF_org())>0){
//					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//				}
//				if(getF_strWLocation()!=null && getF_strWLocation().length>0){
//		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//		        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
//					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//				}
//	            if(getF_department()!=null && getF_department().length>0){
//	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//	            }
//	            if(uF.parseToInt(getF_level()) > 0){
//	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
//	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id ="+uF.parseToInt(getF_level())+") ");
//	            }
//	            if(getF_service()!=null && getF_service().length>0){
//	                sbQuery.append(" and (");
//	                for(int i=0; i<getF_service().length; i++){
//	                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
//	                    
//	                    if(i<getF_service().length-1){
//	                        sbQuery.append(" OR "); 
//	                    }
//	                }
//	                sbQuery.append(" ) ");
//	                
//	            }
//	            
//	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//					sbQuery.append(")");
//				}
//				
//				sbQuery.append(" group by emp_id, reimbursement_ctc_id");
//				pst = con.prepareStatement(sbQuery.toString());
//				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				pst.setInt(5, uF.parseToInt(strPayCycleDates[2]));
//				rs = pst.executeQuery();
//				List<String> alPaidReimCTC = new ArrayList<String>();
//				while(rs.next()){
//					alPaidReimCTC.add(rs.getString("emp_id")+"_"+rs.getString("reimbursement_ctc_id"));
//				}
//				rs.close();
//				pst.close();
//				
//				sbQuery = new StringBuilder();
//				sbQuery.append("select * from (select rcd.reimbursement_ctc_id as reimbursementctcid,rhd.reimbursement_head_id as reimbursementheadid," +
//						"rhad.amount as reim_head_amount,* from reimbursement_assign_head_details rasd " +
//						"join reimbursement_head_details rhd on rhd.reimbursement_head_id=rasd.reimbursement_head_id " +
//						"join reimbursement_head_amt_details rhad on rhd.reimbursement_head_id=rhad.reimbursement_head_id " +
//						"join reimbursement_ctc_details rcd on rhd.reimbursement_ctc_id=rcd.reimbursement_ctc_id " +
//						"where rasd.financial_year_start=? and rasd.financial_year_end=? and rasd.level_id=? and rasd.org_id=? ");
//				sbQuery.append(" and rasd.paycycle_from=? and rasd.paycycle_to=? and rasd.paycycle=? and rasd.status=false " +
//						"and rasd.trail_status=true and rhd.level_id=? and rhd.org_id=? ");
//				sbQuery.append(" and rhad.financial_year_start=? and rhad.financial_year_end=? and rhad.is_attachment= false and rhad.is_optimal=true ");
//				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//					sbQuery.append(" and rasd.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
//				}
//				
//				if(uF.parseToInt(getF_org())>0){
//					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//				}
//				if(getF_strWLocation()!=null && getF_strWLocation().length>0){
//		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//		        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
//					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//				}
//	            if(getF_department()!=null && getF_department().length>0){
//	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//	            }
//	            if(uF.parseToInt(getF_level()) > 0){
//	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
//	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id ="+uF.parseToInt(getF_level())+") ");
//	            }
//	            if(getF_service()!=null && getF_service().length>0){
//	                sbQuery.append(" and (");
//	                for(int i=0; i<getF_service().length; i++){
//	                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
//	                    
//	                    if(i<getF_service().length-1){
//	                        sbQuery.append(" OR "); 
//	                    }
//	                }
//	                sbQuery.append(" ) ");
//	                
//	            }
//	            
//	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//					sbQuery.append(")");
//				}
//				sbQuery.append(" union ");
//				sbQuery.append("select rcd.reimbursement_ctc_id as reimbursementctcid,rhd.reimbursement_head_id as reimbursementheadid," +
//						"rhad.amount as reim_head_amount,* from reimbursement_assign_head_details rasd " +
//						"join reimbursement_head_details rhd on rhd.reimbursement_head_id=rasd.reimbursement_head_id " +
//						"join reimbursement_head_amt_details rhad on rhd.reimbursement_head_id=rhad.reimbursement_head_id " +
//						"join reimbursement_ctc_details rcd on rhd.reimbursement_ctc_id=rcd.reimbursement_ctc_id " +
//						"where rasd.financial_year_start=? and rasd.financial_year_end=? and rasd.level_id=? and rasd.org_id=? ");
//				sbQuery.append(" and rasd.paycycle_from=? and rasd.paycycle_to=? and rasd.paycycle=? and rasd.status=true " +
//						"and rasd.trail_status=true and rhd.level_id=? and rhd.org_id=? ");
//				sbQuery.append(" and rhad.financial_year_start=? and rhad.financial_year_end=? and rhad.is_attachment= true and rhad.is_optimal=false ");
//				if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//					sbQuery.append(" and rasd.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
//				}
//				
//				if(uF.parseToInt(getF_org())>0){
//					sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
//				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//				}
//				if(getF_strWLocation()!=null && getF_strWLocation().length>0){
//		            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//		        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
//					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//				}
//	            if(getF_department()!=null && getF_department().length>0){
//	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//	            }
//	            if(uF.parseToInt(getF_level()) > 0){
//	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd " +
//	                		"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+uF.parseToInt(getF_level())+" ) ");
//	            }
//	            if(getF_service()!=null && getF_service().length>0){
//	                sbQuery.append(" and (");
//	                for(int i=0; i<getF_service().length; i++){
//	                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
//	                    
//	                    if(i<getF_service().length-1){
//	                        sbQuery.append(" OR "); 
//	                    }
//	                }
//	                sbQuery.append(" ) ");
//	                
//	            }
//	            if((getF_service()!=null && getF_service().length>0) || (uF.parseToInt(getF_level()) > 0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
//					sbQuery.append(")");
//				}
//				sbQuery.append(") a order by a.emp_id, a.reimbursementctcid,a.reimbursementheadid");
//				pst = con.prepareStatement(sbQuery.toString());
//				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(getF_level()));
//				pst.setInt(4, uF.parseToInt(getF_org()));
//				pst.setDate(5, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//				pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				pst.setInt(7, uF.parseToInt(strPayCycleDates[2]));
//				pst.setInt(8, uF.parseToInt(getF_level()));
//				pst.setInt(9, uF.parseToInt(getF_org()));
//				pst.setDate(10, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(11, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				
//				pst.setDate(12, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(13, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setInt(14, uF.parseToInt(getF_level()));
//				pst.setInt(15, uF.parseToInt(getF_org()));
//				pst.setDate(16, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//				pst.setDate(17, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				pst.setInt(18, uF.parseToInt(strPayCycleDates[2]));
//				pst.setInt(19, uF.parseToInt(getF_level()));
//				pst.setInt(20, uF.parseToInt(getF_org()));
//				pst.setDate(21, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(22, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst==>"+pst);
//				rs = pst.executeQuery();
//				Map<String, String> hmReimCTCName = new HashMap<String, String>();
//				Map<String, String> hmEmpName = new HashMap<String, String>();
//				Map<String, String> hmEmpCode = new HashMap<String, String>();
//				Map<String, String> hmReimCTCAmount = new HashMap<String, String>();
//				Map<String, String> hmReimCTCHead = new LinkedHashMap<String, String>();
//				Map<String, Map<String, Map<String, String>>> hmReimbursementCTC = new HashMap<String, Map<String,Map<String,String>>>();
//				while(rs.next()){
//					if(alPaidReimCTC.contains(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid"))){
//						continue;
//					}
//					
//					if(!hmReimCTCHead.containsKey(rs.getString("reimbursementheadid"))){
//						hmReimCTCHead.put(rs.getString("reimbursementheadid"),rs.getString("reimbursement_head_name"));
//					}
//					
//					double dblReimHeadAmount = uF.parseToDouble(rs.getString("reim_head_amount")) > 0 ? uF.parseToDouble(rs.getString("reim_head_amount")) / 12d : 0.0d; 
//					
//					double dblAmount = uF.parseToDouble(hmReimCTCAmount.get(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid")));
//					dblAmount += dblReimHeadAmount;
//					hmReimCTCAmount.put(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblAmount));
//					
//					Map<String, Map<String, String>> hmReimbursementCTCInner = hmReimbursementCTC.get(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid"));
//					if(hmReimbursementCTCInner == null) hmReimbursementCTCInner = new HashMap<String, Map<String,String>>();
//					
//					Map<String, String> hmReimbursementHeadInner = new HashMap<String, String>();
//					hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursementheadid"));
//					hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_AMT_ID", rs.getString("reimbursement_head_amt_id"));
//					hmReimbursementHeadInner.put("REIMBURSEMENT_ASSIGN_HEAD_ID", rs.getString("reim_assign_head_id"));
//					hmReimbursementHeadInner.put("REIMBURSEMENT_HEAD_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblReimHeadAmount));
//					
//					hmReimbursementCTCInner.put(rs.getString("reimbursementheadid"), hmReimbursementHeadInner);
//					
//					hmReimbursementCTC.put(rs.getString("emp_id")+"_"+rs.getString("reimbursementctcid"),hmReimbursementCTCInner);
//					
//					
//					if(!hmReimCTCName.containsKey(rs.getString("reimbursementctcid"))){
//						hmReimCTCName.put(rs.getString("reimbursementctcid"), rs.getString("reimbursement_name"));
//					}
//					if(!hmEmpCode.containsKey(rs.getString("emp_id"))){
//						hmEmpCode.put(rs.getString("emp_id"), uF.showData(hmEmpCodes.get(rs.getString("emp_id")), ""));
//					}
//					if(!hmEmpName.containsKey(rs.getString("emp_id"))){
//						hmEmpName.put(rs.getString("emp_id"), uF.showData(hmEmpNames.get(rs.getString("emp_id")), ""));
//					}
//					
//				}
//				rs.close();
//				pst.close();
//				
////				System.out.println("hmReimbursementCTC==>"+hmReimbursementCTC);
////				System.out.println("hmReimCTCName==>"+hmReimCTCName);
////				System.out.println("hmEmpCode==>"+hmEmpCode);
////				System.out.println("hmEmpName==>"+hmEmpName);
////				System.out.println("hmReimCTCAmount==>"+hmReimCTCAmount);
////				System.out.println("hmReimCTCHead==>"+hmReimCTCHead);
//				
//				request.setAttribute("hmReimbursementCTC",hmReimbursementCTC);
//				request.setAttribute("hmReimCTCName",hmReimCTCName);
//				request.setAttribute("hmEmpCode",hmEmpCode);
//				request.setAttribute("hmEmpName",hmEmpName);
//				request.setAttribute("hmReimCTCAmount",hmReimCTCAmount);
//				request.setAttribute("hmReimCTCHead",hmReimCTCHead);
//				
//				session.setAttribute("P_hmReimbursementCTC",hmReimbursementCTC);
//				session.setAttribute("P_hmReimCTCName",hmReimCTCName);
//				session.setAttribute("P_hmEmpCode",hmEmpCode);
//				session.setAttribute("P_hmEmpName",hmEmpName);
//				session.setAttribute("P_hmReimCTCAmount",hmReimCTCAmount);
//				session.setAttribute("P_hmReimCTCHead",hmReimCTCHead);
//				
//				session.setAttribute("P_strFinancialYearStart", strFinancialYearStart);
//				session.setAttribute("P_strFinancialYearEnd", strFinancialYearEnd);
//				session.setAttribute("P_levelId", ""+uF.parseToInt(getF_level()));
//				session.setAttribute("P_orgId", ""+uF.parseToInt(getF_org()));
//				session.setAttribute("P_paycycleFrom", strPayCycleDates[0]);
//				session.setAttribute("P_paycycleTo", strPayCycleDates[1]);
//				session.setAttribute("P_paycycle", ""+uF.parseToInt(strPayCycleDates[2]));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	public String loadReimbursements(UtilityFunctions uF){
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		bankList = new FillBank(request).fillBankAccNoForDocuments(CF,uF,getF_org());
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
				
		getSelectedFilter(uF);
		
		return LOAD;
	}
	 
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("PAID_STATUS");
		if(uF.parseToInt(getPaidStatus())==1){ 
			hmFilter.put("PAID_STATUS", "Paid");
		}else if(uF.parseToInt(getPaidStatus())==2){
			hmFilter.put("PAID_STATUS", "UnPaid");
		}else {
			hmFilter.put("PAID_STATUS", "-");
		}

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					if(k==0) {
						strLevel=levelList.get(i).getLevelCodeName();
					} else {
						strLevel+=", "+levelList.get(i).getLevelCodeName();
					}
					k++;
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
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
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
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

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBankAccountType() {
		return bankAccountType;
	}

	public void setBankAccountType(String bankAccountType) {
		this.bankAccountType = bankAccountType;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getStrApprove() {
		return strApprove;
	}

	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}

	public String[] getReimbId() {
		return reimbId;
	}

	public void setReimbId(String[] reimbId) {
		this.reimbId = reimbId;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public List<String> getStrEmpCTCId() {
		return strEmpCTCId;
	}

	public void setStrEmpCTCId(List<String> strEmpCTCId) {
		this.strEmpCTCId = strEmpCTCId;
	}

	public String getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
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
	
}