package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class ViewPaySlips extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	 
	CommonFunctions CF = null; 
	String profileEmpId;
	
	private String financialYear;  
	private String paycycle; 
	private String strMonth; 
	private List<FillPayCycles> paycycleList ;
	private List<FillFinancialYears> financialYearList;
	
	private String alertStatus;
	private String alert_type;
	private String alertID;
	
	public String execute() throws Exception {
		session = request.getSession();
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(USERTYPE);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			request.setAttribute(PAGE, PViewPaySlipE);
		}else{
			request.setAttribute(PAGE, PViewPaySlip);
		}
		
		request.setAttribute(TITLE, TViewPaySlip);
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if (nSalaryStrucuterType == S_GRADE_WISE) {
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
				if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(MY_PAY_ALERT)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,MY_PAY_ALERT,UPDATE_ALERT);
				} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PAY_PERK)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,PAY_PERK,UPDATE_ALERT);
				} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PAY_LTA)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,PAY_LTA,UPDATE_ALERT);
				} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PAY_GRATUITY)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,PAY_GRATUITY,UPDATE_ALERT);
				} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PAY_REIM)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,PAY_REIM,UPDATE_ALERT);
				}
				viewPaySlipsEmpByGrade(strEmpId);	
			} else if(getProfileEmpId()!=null) {
//					viewPaySlipsEmp(getProfileEmpId());
				viewPaySlipsByGrade(getProfileEmpId());
			} else {
				viewPaySlipsByGrade(strEmpId);
			}
		
		} else {
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)) {
				if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(MY_PAY_ALERT)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,MY_PAY_ALERT,UPDATE_ALERT);
				} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PAY_PERK)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,PAY_PERK,UPDATE_ALERT);
				} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PAY_LTA)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,PAY_LTA,UPDATE_ALERT);
				} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PAY_GRATUITY)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,PAY_GRATUITY,UPDATE_ALERT);
				} else if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(PAY_REIM)) {
					CF.updateUserAlerts(CF,request,strEmpId,strDomain,PAY_REIM,UPDATE_ALERT);
				}
				viewPaySlipsEmp(strEmpId);	
			} else if(getProfileEmpId()!=null) {
//					viewPaySlipsEmp(getProfileEmpId());
				viewPaySlips(getProfileEmpId());
			} else {
				viewPaySlips(strEmpId);
			}
		}
		return loadPaySlips();
	}
	
	
	public String viewPaySlipsByGrade(String strEmpId){
		
		//System.out.println(" in viewPaySlipsByGrade");
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			if(getStrMonth()!=null) {
				setStrMonth(getStrMonth());
			} else {
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			
			con = db.makeConnection(con);

			Map hmEmpMap = CF.getEmpNameMap(con,null,null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			Map<String, String> hmOrg = CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			/**
			 * Primary 
			 * */
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
					"and month=? and is_paid = true and (bank_pay_type=1 or bank_pay_type is null) order by emp_id,earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			rs = pst.executeQuery();
//			System.out.println("pst===>>"+pst);
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			double dblNetAmount = 0.0d;
			Map<String, String> hmInner = new HashMap<String, String>();
			Map<String, Map<String, String>> hmSalary = new HashMap<String, Map<String,String>>();
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					dblNetAmount = 0.0d;
					hmInner = new HashMap<String, String>();
				}	
				
				if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
					dblNetAmount = dblNetAmount +  dblAmount;
				}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
					dblNetAmount = dblNetAmount -  dblAmount;
				}
				
				hmInner.put("EMP_ID_PRIMARY", strEmpIdNew);
				hmInner.put("NET_AMOUNT_PRIMARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("PAY_DATE_PRIMARY", uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("FYS_PRIMARY", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
				hmInner.put("FYE_PRIMARY", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
				hmInner.put("MODE_PRIMARY", rs.getString("pay_mode"));
				
				hmInner.put("PCS_PRIMARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("PCE_PRIMARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				
				hmInner.put("MONTH_PRIMARY", rs.getString("month"));
				
				hmInner.put("PC_PRIMARY", rs.getString("paycycle"));
				hmInner.put("SERVICE_ID_PRIMARY", rs.getString("service_id"));
				hmInner.put("IS_PAID_PRIMARY", rs.getString("is_paid"));
				hmInner.put("BANK_PAY_TYPE_PRIMARY", rs.getString("bank_pay_type"));
				
				hmSalary.put(strEmpIdNew, hmInner);
				
				strEmpIdOld = strEmpIdNew;  
			}
			rs.close();
			pst.close();
			
			/**
			 * Secondary
			 * */
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
					"and month=? and is_paid = true and bank_pay_type=2 order by emp_id,earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			rs = pst.executeQuery();
//			System.out.println("pst===>>"+pst);
			strEmpIdNew = null;
			strEmpIdOld = null;
			dblNetAmount = 0.0d;
			hmInner = new HashMap<String, String>();
			Map<String, Map<String, String>> hmSalarySecondary = new HashMap<String, Map<String,String>>();
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					dblNetAmount = 0.0d;
					hmInner = new HashMap<String, String>();
				}	
				
				
				if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
					dblNetAmount = dblNetAmount +  dblAmount;
				}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
					dblNetAmount = dblNetAmount -  dblAmount;
				}
				
				hmInner.put("EMP_ID_SECONDARY", strEmpIdNew);
				hmInner.put("NET_AMOUNT_SECONDARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("PAY_DATE_SECONDARY", uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("FYS_SECONDARY", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
				hmInner.put("FYE_SECONDARY", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
				hmInner.put("MODE_SECONDARY", rs.getString("pay_mode"));
				
				hmInner.put("PCS_SECONDARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("PCE_SECONDARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				
				hmInner.put("MONTH_SECONDARY", rs.getString("month"));
				
				hmInner.put("PC_SECONDARY", rs.getString("paycycle"));
				hmInner.put("SERVICE_ID_SECONDARY", rs.getString("service_id"));
				hmInner.put("IS_PAID_SECONDARY", rs.getString("is_paid"));
				hmInner.put("BANK_PAY_TYPE_SECONDARY", rs.getString("bank_pay_type"));
				
				hmSalarySecondary.put(strEmpIdNew, hmInner);
				
				strEmpIdOld = strEmpIdNew;  
			}
			rs.close();
			pst.close();
			
			Map hmCurrencyMap = CF.getCurrencyDetails(con);
			Map hmEmpCurrencyMap = CF.getEmpCurrency(con);
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			Iterator<String> it = hmSalary.keySet().iterator();
			while(it.hasNext()){
				String str = (String)it.next();
				Map<String, String> hmTemp = (Map<String, String>)hmSalary.get(str);
				if(hmTemp == null) hmTemp = new HashMap<String, String>();
				Map<String, String> hmTempSecondary = (Map<String, String>)hmSalarySecondary.get(str);
				if(hmTempSecondary == null) hmTempSecondary = new HashMap<String, String>();
				
				alInner = new ArrayList<String>();
				alInner.add(uF.showData(hmEmpCode.get(str),""));
				alInner.add((String)hmEmpMap.get(str));
				alInner.add(uF.showData(hmOrg.get(hmEmpOrgId.get(str)),""));
				alInner.add(uF.getDateFormat((String)hmTemp.get("PAY_DATE_PRIMARY"), DATE_FORMAT, CF.getStrReportDateFormat()));
				
				String strPayrollAmt = (String)hmTemp.get("NET_AMOUNT_PRIMARY");
				String strDownload="";
				String strSalary = "";
				if(strPayrollAmt!=null){
					if((String)hmTemp.get("MONTH_PRIMARY")!=null && (String)hmTemp.get("PC_PRIMARY")!=null){
						strDownload="<a href=\"ExportSalarySlip.action?EID="+str+"&SID=0&M="+(String)hmTemp.get("MONTH_PRIMARY")+"&PC="+(String)hmTemp.get("PC_PRIMARY")+"&FYS="+(String)hmTemp.get("FYS_PRIMARY")+"&FYE="+(String)hmTemp.get("FYE_PRIMARY")+"&PD="+(String)hmTemp.get("PAY_DATE_PRIMARY")+"&PCS="+(String)hmTemp.get("PCS_PRIMARY")+"&PCE="+(String)hmTemp.get("PCE_PRIMARY")+"&BPT="+(String)hmTemp.get("BANK_PAY_TYPE_PRIMARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>"; 
					}
					strSalary = strPayrollAmt+" "+getLegend(uF.parseToBoolean((String)hmTemp.get("IS_PAID_PRIMARY")))+"    "+strDownload;
				}				
				
				String strPayrollSecondaryAmt = (String)hmTempSecondary.get("NET_AMOUNT_SECONDARY");
				String strSecondaryDownload="";
				if(strPayrollSecondaryAmt!=null){
					if((String)hmTempSecondary.get("MONTH_SECONDARY")!=null && (String)hmTempSecondary.get("PC_SECONDARY")!=null){
						strSecondaryDownload="<a href=\"ExportSalarySlip.action?EID="+str+"&SID=0&M="+(String)hmTempSecondary.get("MONTH_SECONDARY")+"&PC="+(String)hmTempSecondary.get("PC_SECONDARY")+"&FYS="+(String)hmTempSecondary.get("FYS_SECONDARY")+"&FYE="+(String)hmTempSecondary.get("FYE_SECONDARY")+"&PD="+(String)hmTempSecondary.get("PAY_DATE_SECONDARY")+"&PCS="+(String)hmTempSecondary.get("PCS_SECONDARY")+"&PCE="+(String)hmTempSecondary.get("PCE_SECONDARY")+"&BPT="+(String)hmTempSecondary.get("BANK_PAY_TYPE_SECONDARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
					}
					strSalary += "<br/>"+strPayrollSecondaryAmt+" "+getLegend(uF.parseToBoolean((String)hmTempSecondary.get("IS_PAID_SECONDARY")))+"    "+strSecondaryDownload;
				}
				alInner.add(strSalary);
				
			
				al.add(alInner);
					
				
			}
			
			request.setAttribute("reportList", al);  
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
		return SUCCESS;
		
	}
	
private void viewPaySlipsEmpByGrade(String strEmpId) {
		
	//System.out.println(" in viewPaySlipsEmpByGrade");

		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
		
			/**
			 * Primary Bank type
			 * */
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
					"and emp_id=? and is_paid = true and (bank_pay_type=1 or bank_pay_type is null) order by year desc,month desc, earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			String strMonthOld = null;
			String strMonthNew = null;
			String strPayDateToOld = null;
			String strPayDateToNew = null;
			double dblNetAmount = 0.0d;
			Map hmInner = new LinkedHashMap();
			Map hmSalary = new LinkedHashMap();
			//System.out.println("pst ===>> " + pst);
			while(rs.next()){
				strMonthNew = rs.getString("month");
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					dblNetAmount = 0.0d;
					hmInner = new LinkedHashMap();
				}	
				
				if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){

					if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
						dblNetAmount = dblNetAmount +  dblAmount;
					}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
						dblNetAmount = dblNetAmount -  dblAmount;
					}
				}else{
					strPayDateToNew = rs.getString("paid_to");
					
					if(strPayDateToNew!=null && !strPayDateToNew.equalsIgnoreCase(strPayDateToOld)){
						dblNetAmount = 0.0d;
						hmInner = new LinkedHashMap();
					}
					if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
						dblNetAmount = dblNetAmount +  dblAmount;
					}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
						dblNetAmount = dblNetAmount -  dblAmount;
					}
				}
				
				hmInner.put("NET_AMOUNT_PRIMARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("PAY_DATE_PRIMARY", uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("FYS_PRIMARY", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
				hmInner.put("FYE_PRIMARY", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
				hmInner.put("MODE_PRIMARY", rs.getString("pay_mode"));
				
				hmInner.put("PCS_PRIMARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("PCE_PRIMARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				
				hmInner.put("MONTH_PRIMARY", rs.getString("month"));
				
				hmInner.put("PC_PRIMARY", rs.getString("paycycle"));
				hmInner.put("SERVICE_ID_PRIMARY", rs.getString("service_id"));
				hmInner.put("IS_PAID_PRIMARY", rs.getString("is_paid"));
				hmInner.put("BANK_PAY_TYPE_PRIMARY", rs.getString("bank_pay_type"));
					
				if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
					hmSalary.put(uF.getDateFormat(strMonthNew, "M", "MMMM"), hmInner);
				}else{
					hmSalary.put(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()), hmInner);
				}
				
				strMonthOld = strMonthNew;
				strPayDateToOld = strPayDateToNew;
			}
			rs.close();
			pst.close();
			
			
			/**
			 * Seconday Bank Pay Type
			 * */
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
					"and emp_id=? and is_paid = true and bank_pay_type=2 order by year desc,month desc, earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			strMonthOld = null;
			strMonthNew = null;
			strPayDateToOld = null;
			strPayDateToNew = null;
			dblNetAmount = 0.0d;
			hmInner = new LinkedHashMap();
			//System.out.println("pst ===>> " + pst);
			while(rs.next()){
				strMonthNew = rs.getString("month");
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					dblNetAmount = 0.0d;
//					hmInner = new LinkedHashMap();
					if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
						hmInner = (Map) hmSalary.get(uF.getDateFormat(strMonthNew, "M", "MMMM"));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}else{
						hmInner = (Map) hmSalary.get(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}
				}	
				
				if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
		
					if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
						dblNetAmount = dblNetAmount +  dblAmount;
					}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
						dblNetAmount = dblNetAmount -  dblAmount;
					}
				}else{
					strPayDateToNew = rs.getString("paid_to");
					
					if(strPayDateToNew!=null && !strPayDateToNew.equalsIgnoreCase(strPayDateToOld)){
						dblNetAmount = 0.0d;
//						hmInner = new LinkedHashMap();
						if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
							hmInner = (Map) hmSalary.get(uF.getDateFormat(strMonthNew, "M", "MMMM"));
							if(hmInner == null) hmInner = new LinkedHashMap();
						}else{
							hmInner = (Map) hmSalary.get(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
							if(hmInner == null) hmInner = new LinkedHashMap();
						}
					}
					if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
						dblNetAmount = dblNetAmount +  dblAmount;
					}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
						dblNetAmount = dblNetAmount -  dblAmount;
					}
				}
				
				hmInner.put("NET_AMOUNT_SECONDARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("PAY_DATE_SECONDARY", uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("FYS_SECONDARY", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
				hmInner.put("FYE_SECONDARY", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
				hmInner.put("MODE_SECONDARY", rs.getString("pay_mode"));
				
				hmInner.put("PCS_SECONDARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("PCE_SECONDARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				
				hmInner.put("MONTH_SECONDARY", rs.getString("month"));
				
				hmInner.put("PC_SECONDARY", rs.getString("paycycle"));
				hmInner.put("SERVICE_ID_SECONDARY", rs.getString("service_id"));
				hmInner.put("IS_PAID_SECONDARY", rs.getString("is_paid"));
				hmInner.put("BANK_PAY_TYPE_SECONDARY", rs.getString("bank_pay_type"));
					
				if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
					hmSalary.put(uF.getDateFormat(strMonthNew, "M", "MMMM"), hmInner);
				}else{
					hmSalary.put(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()), hmInner);
				}
				
				strMonthOld = strMonthNew;
				strPayDateToOld = strPayDateToNew;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_reimbursement where paid_date between ? and ? and emp_id =? and ispaid = true order by entry_date");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
//				strMonthNew = uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MMMM");
				strMonthNew = uF.getDateFormat(rs.getString("to_date"), DBDATE, "MMMM");
//				strMonthNew = uF.parseToInt(strMonthNew)+"";
				
				hmInner = (Map) hmSalary.get(strMonthNew);
				if(hmInner==null)hmInner = new LinkedHashMap();
				
				double dblReImburseAmt = uF.parseToDouble((String)hmInner.get("REIMBURSE_AMOUNT"));
				dblReImburseAmt += rs.getDouble("reimbursement_amount");
				
				hmInner.put("REIMBURSE_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReImburseAmt));
				hmInner.put("REIMBURSE_STATUS", 2+"");
				
				hmSalary.put(strMonthNew, hmInner);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_lta_details where paid_date between ? and ? and emp_id =? and is_paid = true order by paid_date");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				strMonthNew = uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MMMM");
//				strMonthNew = uF.parseToInt(strMonthNew)+"";
				
				hmInner = (Map) hmSalary.get(strMonthNew);
				if(hmInner==null)hmInner = new LinkedHashMap();
				
				double dblCTCVariableAmt = uF.parseToDouble((String)hmInner.get("CTC_VARIABLE_AMOUNT"));
				dblCTCVariableAmt += rs.getDouble("applied_amount");
				
				hmInner.put("CTC_VARIABLE_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCTCVariableAmt));
				hmInner.put("CTC_VARIABLE_STATUS", 2+"");
				
				hmSalary.put(strMonthNew, hmInner);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_perks where paid_date between ? and ? and emp_id =? and ispaid = true order by paid_date");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				strMonthNew = uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MMMM");
//				strMonthNew = uF.parseToInt(strMonthNew)+"";
				
				hmInner = (Map) hmSalary.get(strMonthNew);
				if(hmInner==null)hmInner = new LinkedHashMap();
				
				double dblPerkAmt = uF.parseToDouble((String)hmInner.get("PERK_AMOUNT"));
				dblPerkAmt += rs.getDouble("perk_amount");
				
				hmInner.put("PERK_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAmt));
				hmInner.put("PERK_STATUS", 2+"");
				
				hmSalary.put(strMonthNew, hmInner);
			}
			rs.close();
			pst.close();
			
			
			/**
			 * Primary Bank type for Reimbursement CTC (Added by Mayuri)
			 * */
			pst = con.prepareStatement("select * from reimbursement_ctc_pay where financial_year_from=? and financial_year_to=? " +
					"and emp_id=? and (bank_pay_type=1 or bank_pay_type is null) order by pay_year desc,pay_month desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst Primary------------>"+pst);
			rs = pst.executeQuery();
			strMonthOld = null;
			strMonthNew = null;
			strPayDateToOld = null;
			strPayDateToNew = null;
			dblNetAmount = 0.0d;
			
			while(rs.next()){
				strMonthNew = rs.getString("pay_month");
				double dblAmount = rs.getDouble("amount");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					dblNetAmount = 0.0d;
					hmInner = new LinkedHashMap();
				}	
			
				strPayDateToNew = rs.getString("paid_to");
					
				if(strPayDateToNew!=null && !strPayDateToNew.equalsIgnoreCase(strPayDateToOld)){
					dblNetAmount = 0.0d;
					hmInner = new LinkedHashMap();
				}
				dblNetAmount = dblNetAmount +  dblAmount;
			
				hmInner.put("REIMB_CTC_NET_AMOUNT_PRIMARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("REIMB_CTC_PAY_DATE_PRIMARY", uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("REIMB_CTC_FYS_PRIMARY", uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_FYE_PRIMARY", uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_MODE_PRIMARY", rs.getString("payment_mode"));
				hmInner.put("REIMB_CTC_PCS_PRIMARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_PCE_PRIMARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_MONTH_PRIMARY", rs.getString("pay_month"));
				hmInner.put("REIMB_CTC_PC_PRIMARY", rs.getString("paycycle"));
				hmInner.put("REIMB_CTC_BANK_PAY_TYPE_PRIMARY", rs.getString("bank_pay_type"));
					
				hmSalary.put(uF.getDateFormat(strMonthNew, "M", "MMMM"), hmInner);

				strMonthOld = strMonthNew;
				strPayDateToOld = strPayDateToNew;
			}
			rs.close();
			pst.close();
			
			/**
			 * Seconday Bank type for Reimbursement CTC (Added by Mayuri)
			 * */
			pst = con.prepareStatement("select * from reimbursement_ctc_pay where financial_year_from=? and financial_year_to=? " +
					"and emp_id=? and bank_pay_type=2 order by pay_year desc,pay_month desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst Secondary------------>"+pst);
			rs = pst.executeQuery();
			strMonthOld = null;
			strMonthNew = null;
			strPayDateToOld = null;
			strPayDateToNew = null;
			dblNetAmount = 0.0d;
			hmInner = new LinkedHashMap();
			while(rs.next()){
				strMonthNew = rs.getString("pay_month");
				double dblAmount = rs.getDouble("amount");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					dblNetAmount = 0.0d;
					if(rs.getString("payment_mode")!=null && rs.getString("payment_mode").equalsIgnoreCase("M")){
						hmInner = (Map) hmSalary.get(uF.getDateFormat(strMonthNew, "M", "MMMM"));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}else{
						hmInner = (Map) hmSalary.get(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}
				}	
	
				strPayDateToNew = rs.getString("paid_to");
					
				if(strPayDateToNew!=null && !strPayDateToNew.equalsIgnoreCase(strPayDateToOld)){
					dblNetAmount = 0.0d;
					if(rs.getString("payment_mode")!=null && rs.getString("payment_mode").equalsIgnoreCase("M")){
						hmInner = (Map) hmSalary.get(uF.getDateFormat(strMonthNew, "M", "MMMM"));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}else{
						hmInner = (Map) hmSalary.get(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
						if(hmInner == null) hmInner = new LinkedHashMap();
						}
				}
				
				dblNetAmount = dblNetAmount +  dblAmount;
				
				hmInner.put("REIMB_CTC_NET_AMOUNT_SECONDARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("REIMB_CTC_PAY_DATE_SECONDARY", uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("REIMB_CTC_FYS_SECONDARY", uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_FYE_SECONDARY", uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_MODE_SECONDARY", rs.getString("payment_mode"));
				hmInner.put("REIMB_CTC_PCS_SECONDARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_PCE_SECONDARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_MONTH_SECONDARY", rs.getString("pay_month"));
				hmInner.put("REIMB_CTC_PC_SECONDARY", rs.getString("paycycle"));
				hmInner.put("REIMB_CTC_BANK_PAY_TYPE_SECONDARY", rs.getString("bank_pay_type"));
					
				hmSalary.put(uF.getDateFormat(strMonthNew, "M", "MMMM"), hmInner);
				
				strMonthOld = strMonthNew;
				strPayDateToOld = strPayDateToNew;
			}
			rs.close();
			pst.close();
			
			//System.out.println("hmSalary ===>> " + hmSalary);
			Iterator it = hmSalary.keySet().iterator();
			
			while(it.hasNext()){
				String str = null;
				if(it.hasNext()){
					str = (String)it.next();
				}
				
				Map hmTemp = (Map)hmSalary.get(str);
				
				if(hmTemp!=null){
					alInner = new ArrayList();

					alInner.add(str);
					
					String strPayrollAmt = (String)hmTemp.get("NET_AMOUNT_PRIMARY");
					String strDownload="";
					String strSalary = "";
					
					if(strPayrollAmt!=null){
						if((String)hmTemp.get("MONTH_PRIMARY")!=null && (String)hmTemp.get("PC_PRIMARY")!=null){
							strDownload="<a href=\"ExportSalarySlip.action?EID="+strEmpId+"&SID=0&M="+(String)hmTemp.get("MONTH_PRIMARY")+"&PC="+(String)hmTemp.get("PC_PRIMARY")+"&FYS="+(String)hmTemp.get("FYS_PRIMARY")+"&FYE="+(String)hmTemp.get("FYE_PRIMARY")+"&PD="+(String)hmTemp.get("PAY_DATE_PRIMARY")+"&PCS="+(String)hmTemp.get("PCS_PRIMARY")+"&PCE="+(String)hmTemp.get("PCE_PRIMARY")+"&BPT="+(String)hmTemp.get("BANK_PAY_TYPE_PRIMARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>"; 
						}
						strSalary = strPayrollAmt+" "+getLegend(uF.parseToBoolean((String)hmTemp.get("IS_PAID_PRIMARY")))+"    "+strDownload;
					}

					String strPayrollSecondaryAmt = (String)hmTemp.get("NET_AMOUNT_SECONDARY");
					String strSecondaryDownload="";
					if(strPayrollSecondaryAmt!=null){
						if((String)hmTemp.get("MONTH_SECONDARY")!=null && (String)hmTemp.get("PC_SECONDARY")!=null){
							strSecondaryDownload="<a href=\"ExportSalarySlip.action?EID="+strEmpId+"&SID=0&M="+(String)hmTemp.get("MONTH_SECONDARY")+"&PC="+(String)hmTemp.get("PC_SECONDARY")+"&FYS="+(String)hmTemp.get("FYS_SECONDARY")+"&FYE="+(String)hmTemp.get("FYE_SECONDARY")+"&PD="+(String)hmTemp.get("PAY_DATE_SECONDARY")+"&PCS="+(String)hmTemp.get("PCS_SECONDARY")+"&PCE="+(String)hmTemp.get("PCE_SECONDARY")+"&BPT="+(String)hmTemp.get("BANK_PAY_TYPE_SECONDARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
						}
						strSalary += "<br/>"+strPayrollSecondaryAmt+" "+getLegend(uF.parseToBoolean((String)hmTemp.get("IS_PAID_SECONDARY")))+"    "+strSecondaryDownload;
					}
					alInner.add(strSalary);

					
					String strReimburseAmt = (String)hmTemp.get("REIMBURSE_AMOUNT");
					String strReimburseStatus = (String)hmTemp.get("REIMBURSE_STATUS");
					if(strReimburseAmt!=null){
						alInner.add(strReimburseAmt+" "+getLegend(strReimburseStatus));
					}else{
						alInner.add("-");
					}
					

					/**
					 * Added By Mayuri B. <--------------Start------------>
					 * */
					
					String strPayrollAmtRmb = (String)hmTemp.get("REIMB_CTC_NET_AMOUNT_PRIMARY");
					String strDownloadRmb="";
					String strSalaryRmb = "";
					if(strPayrollAmtRmb!=null){
						if((String)hmTemp.get("REIMB_CTC_MONTH_PRIMARY")!=null && (String)hmTemp.get("REIMB_CTC_PC_PRIMARY")!=null){
							strDownloadRmb="<a href=\"ReimbursementCTCPayslip.action?EID="+strEmpId+"&SID=0&M="+(String)hmTemp.get("REIMB_CTC_MONTH_PRIMARY")+"&PC="+(String)hmTemp.get("REIMB_CTC_PC_PRIMARY")+"&FYS="+(String)hmTemp.get("REIMB_CTC_FYS_PRIMARY")+"&FYE="+(String)hmTemp.get("REIMB_CTC_FYE_PRIMARY")+"&PD="+(String)hmTemp.get("REIMB_CTC_PAY_DATE_PRIMARY")+"&PCS="+(String)hmTemp.get("REIMB_CTC_PCS_PRIMARY")+"&PCE="+(String)hmTemp.get("REIMB_CTC_PCE_PRIMARY")+"&BPT="+(String)hmTemp.get("REIMB_CTC_BANK_PAY_TYPE_PRIMARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>"; 
						}
						strSalaryRmb = strPayrollAmtRmb+" "+getLegend(true)+"    "+strDownloadRmb;
					}	
					
					String strPayrollSecondaryAmtRmb = (String)hmTemp.get("REIMB_CTC_NET_AMOUNT_SECONDARY");
					String strSecondaryDownloadRmb="";
					if(strPayrollSecondaryAmtRmb!=null){
						if((String)hmTemp.get("REIMB_CTC_MONTH_SECONDARY")!=null && (String)hmTemp.get("REIMB_CTC_PC_SECONDARY")!=null){
							strSecondaryDownloadRmb="<a href=\"ReimbursementCTCPayslip.action?EID="+strEmpId+"&SID=0&M="+(String)hmTemp.get("REIMB_CTC_MONTH_SECONDARY")+"&PC="+(String)hmTemp.get("REIMB_CTC_PC_SECONDARY")+"&FYS="+(String)hmTemp.get("REIMB_CTC_FYS_SECONDARY")+"&FYE="+(String)hmTemp.get("REIMB_CTC_FYE_SECONDARY")+"&PD="+(String)hmTemp.get("REIMB_CTC_PAY_DATE_SECONDARY")+"&PCS="+(String)hmTemp.get("REIMB_CTC_PCS_SECONDARY")+"&PCE="+(String)hmTemp.get("REIMB_CTC_PCE_SECONDARY")+"&BPT="+(String)hmTemp.get("REIMB_CTC_BANK_PAY_TYPE_SECONDARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
						}
						strSalaryRmb += "<br/>"+strPayrollSecondaryAmtRmb+" "+getLegend(true)+"    "+strSecondaryDownloadRmb;
					}
					alInner.add(strSalaryRmb);

					/**
					 * Added By Mayuri B.  <--------------End------------>
					 * */
					
					String strCTCVariableAmt = (String)hmTemp.get("CTC_VARIABLE_AMOUNT");
					String strCTCVariableStatus = (String)hmTemp.get("CTC_VARIABLE_STATUS");
					if(strCTCVariableAmt!=null){
						alInner.add(strCTCVariableAmt+" "+getLegend(strCTCVariableStatus));
					}else{
						alInner.add("-");
					}

					String strPerkAmt = (String)hmTemp.get("PERK_AMOUNT");
					String strPerkStatus = (String)hmTemp.get("PERK_STATUS");
					if(strPerkAmt!=null){
						alInner.add(strPerkAmt+" "+getLegend(strPerkStatus));
					}else{
						alInner.add("-");
					}
					
					al.add(alInner);
				}
			}
		
			request.setAttribute("reportList", al);

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void viewPaySlipsEmp(String strEmpId) {
		//System.out.println(" in viewPaySlipsEmp");

		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			List<List<String>> al1 = new ArrayList<List<String>>();
			List<String> alInner1 = new ArrayList<String>();
			
			con = db.makeConnection(con);
		
			/**
			 * Primary Bank type
			 * */
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
					"and emp_id=? and is_paid = true and (bank_pay_type=1 or bank_pay_type is null) order by year desc,month desc, earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			String strMonthOld = null;
			String strMonthNew = null;
			String strPayDateToOld = null;
			String strPayDateToNew = null;
			double dblNetAmount = 0.0d;
			Map hmInner = new LinkedHashMap();
			Map hmSalary = new LinkedHashMap();
			while(rs.next()){
				strMonthNew = rs.getString("month");
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					dblNetAmount = 0.0d;
					hmInner = new LinkedHashMap();
				}	
				
				if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){

					if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
						dblNetAmount = dblNetAmount +  dblAmount;
					}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
						dblNetAmount = dblNetAmount -  dblAmount;
					}
				}else{
					strPayDateToNew = rs.getString("paid_to");
					
					if(strPayDateToNew!=null && !strPayDateToNew.equalsIgnoreCase(strPayDateToOld)){
						dblNetAmount = 0.0d;
						hmInner = new LinkedHashMap();
					}
					if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
						dblNetAmount = dblNetAmount +  dblAmount;
					}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
						dblNetAmount = dblNetAmount -  dblAmount;
					}
				}
				
				hmInner.put("NET_AMOUNT_PRIMARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("PAY_DATE_PRIMARY", uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("FYS_PRIMARY", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
				hmInner.put("FYE_PRIMARY", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
				hmInner.put("MODE_PRIMARY", rs.getString("pay_mode"));
				
				hmInner.put("PCS_PRIMARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("PCE_PRIMARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				
				hmInner.put("MONTH_PRIMARY", rs.getString("month"));
				
				hmInner.put("PC_PRIMARY", rs.getString("paycycle"));
				hmInner.put("SERVICE_ID_PRIMARY", rs.getString("service_id"));
				hmInner.put("IS_PAID_PRIMARY", rs.getString("is_paid"));
				hmInner.put("BANK_PAY_TYPE_PRIMARY", rs.getString("bank_pay_type"));
					
				if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")) {
					hmSalary.put(uF.getDateFormat(strMonthNew, "M", "MMMM"), hmInner);
				} else {
					hmSalary.put(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()), hmInner);
				}
				
				strMonthOld = strMonthNew;
				strPayDateToOld = strPayDateToNew;
			}
			rs.close();
			pst.close();
			
			/**
			 * Seconday Bank Pay Type
			 * */
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
					"and emp_id=? and is_paid = true and bank_pay_type=2 order by year desc,month desc, earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			strMonthOld = null;
			strMonthNew = null;
			strPayDateToOld = null;
			strPayDateToNew = null;
			dblNetAmount = 0.0d;
			hmInner = new LinkedHashMap();
			while(rs.next()){
				strMonthNew = rs.getString("month");
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					dblNetAmount = 0.0d;
//					hmInner = new LinkedHashMap();
					if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
						hmInner = (Map) hmSalary.get(uF.getDateFormat(strMonthNew, "M", "MMMM"));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}else{
						hmInner = (Map) hmSalary.get(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}
				}	
				
				if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
		
					if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
						dblNetAmount = dblNetAmount +  dblAmount;
					}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
						dblNetAmount = dblNetAmount -  dblAmount;
					}
				}else{
					strPayDateToNew = rs.getString("paid_to");
					
					if(strPayDateToNew!=null && !strPayDateToNew.equalsIgnoreCase(strPayDateToOld)){
						dblNetAmount = 0.0d;
//						hmInner = new LinkedHashMap();
						if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
							hmInner = (Map) hmSalary.get(uF.getDateFormat(strMonthNew, "M", "MMMM"));
							if(hmInner == null) hmInner = new LinkedHashMap();
						}else{
							hmInner = (Map) hmSalary.get(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
							if(hmInner == null) hmInner = new LinkedHashMap();
						}
					}
					if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
						dblNetAmount = dblNetAmount +  dblAmount;
					}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
						dblNetAmount = dblNetAmount -  dblAmount;
					}
				}
				
				hmInner.put("NET_AMOUNT_SECONDARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("PAY_DATE_SECONDARY", uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("FYS_SECONDARY", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
				hmInner.put("FYE_SECONDARY", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
				hmInner.put("MODE_SECONDARY", rs.getString("pay_mode"));
				
				hmInner.put("PCS_SECONDARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("PCE_SECONDARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				
				hmInner.put("MONTH_SECONDARY", rs.getString("month"));
				
				hmInner.put("PC_SECONDARY", rs.getString("paycycle"));
				hmInner.put("SERVICE_ID_SECONDARY", rs.getString("service_id"));
				hmInner.put("IS_PAID_SECONDARY", rs.getString("is_paid"));
				hmInner.put("BANK_PAY_TYPE_SECONDARY", rs.getString("bank_pay_type"));
					
				if(rs.getString("pay_mode")!=null && rs.getString("pay_mode").equalsIgnoreCase("M")){
					hmSalary.put(uF.getDateFormat(strMonthNew, "M", "MMMM"), hmInner);
				}else{
					hmSalary.put(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()), hmInner);
				}
				
				strMonthOld = strMonthNew;
				strPayDateToOld = strPayDateToNew;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_reimbursement where paid_date between ? and ? and emp_id =? and ispaid = true order by entry_date");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
//				strMonthNew = uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MMMM");
				strMonthNew = uF.getDateFormat(rs.getString("to_date"), DBDATE, "MMMM");
//				strMonthNew = uF.parseToInt(strMonthNew)+"";
				
				hmInner = (Map) hmSalary.get(strMonthNew);
				if(hmInner==null)hmInner = new LinkedHashMap();
				
				double dblReImburseAmt = uF.parseToDouble((String)hmInner.get("REIMBURSE_AMOUNT"));
				dblReImburseAmt += rs.getDouble("reimbursement_amount");
				
				hmInner.put("REIMBURSE_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblReImburseAmt));
				hmInner.put("REIMBURSE_STATUS", 2+"");
				
				hmSalary.put(strMonthNew, hmInner);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_lta_details where paid_date between ? and ? and emp_id =? and is_paid = true order by paid_date");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				strMonthNew = uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MMMM");
//				strMonthNew = uF.parseToInt(strMonthNew)+"";
				
				hmInner = (Map) hmSalary.get(strMonthNew);
				if(hmInner==null)hmInner = new LinkedHashMap();
				
				double dblCTCVariableAmt = uF.parseToDouble((String)hmInner.get("CTC_VARIABLE_AMOUNT"));
				dblCTCVariableAmt += rs.getDouble("applied_amount");
				
				hmInner.put("CTC_VARIABLE_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblCTCVariableAmt));
				hmInner.put("CTC_VARIABLE_STATUS", 2+"");
				
				hmSalary.put(strMonthNew, hmInner);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_perks where paid_date between ? and ? and emp_id =? and ispaid = true order by paid_date");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				strMonthNew = uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MMMM");
//				strMonthNew = uF.parseToInt(strMonthNew)+"";
				
				hmInner = (Map) hmSalary.get(strMonthNew);
				if(hmInner==null)hmInner = new LinkedHashMap();
				
				double dblPerkAmt = uF.parseToDouble((String)hmInner.get("PERK_AMOUNT"));
				dblPerkAmt += rs.getDouble("perk_amount");
				
				hmInner.put("PERK_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblPerkAmt));
				hmInner.put("PERK_STATUS", 2+"");
				
				hmSalary.put(strMonthNew, hmInner);
			}
			rs.close();
			pst.close();
			
			
			/**
			 * Primary Bank type for Reimbursement CTC (Added by Mayuri)
			 * */
			pst = con.prepareStatement("select * from reimbursement_ctc_pay where financial_year_from=? and financial_year_to=? " +
					"and emp_id=? and (bank_pay_type=1 or bank_pay_type is null) order by pay_year desc,pay_month desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("pst Primary------------>"+pst);
			rs = pst.executeQuery();
			strMonthOld = null;
			strMonthNew = null;
			strPayDateToOld = null;
			strPayDateToNew = null;
			dblNetAmount = 0.0d;
			
			while(rs.next()){
				strMonthNew = rs.getString("pay_month");
				double dblAmount = rs.getDouble("amount");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					dblNetAmount = 0.0d;
					hmInner = new LinkedHashMap();
				}	
			
				strPayDateToNew = rs.getString("paid_to");
					
				if(strPayDateToNew!=null && !strPayDateToNew.equalsIgnoreCase(strPayDateToOld)){
					dblNetAmount = 0.0d;
					hmInner = new LinkedHashMap();
				}
				dblNetAmount = dblNetAmount +  dblAmount;
			
				hmInner.put("REIMB_CTC_NET_AMOUNT_PRIMARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("REIMB_CTC_PAY_DATE_PRIMARY", uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("REIMB_CTC_FYS_PRIMARY", uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_FYE_PRIMARY", uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_MODE_PRIMARY", rs.getString("payment_mode"));
				hmInner.put("REIMB_CTC_PCS_PRIMARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_PCE_PRIMARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_MONTH_PRIMARY", rs.getString("pay_month"));
				hmInner.put("REIMB_CTC_PC_PRIMARY", rs.getString("paycycle"));
				hmInner.put("REIMB_CTC_BANK_PAY_TYPE_PRIMARY", rs.getString("bank_pay_type"));
					
				hmSalary.put(uF.getDateFormat(strMonthNew, "M", "MMMM"), hmInner);

				strMonthOld = strMonthNew;
				strPayDateToOld = strPayDateToNew;
			}
			rs.close();
			pst.close();
			
			/**
			 * Seconday Bank type for Reimbursement CTC (Added by Mayuri)
			 * */
			pst = con.prepareStatement("select * from reimbursement_ctc_pay where financial_year_from=? and financial_year_to=? " +
					"and emp_id=? and bank_pay_type=2 order by pay_year desc,pay_month desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strEmpId));
			
			//System.out.println("pst Secondary------------>"+pst);
			
			rs = pst.executeQuery();
			strMonthOld = null;
			strMonthNew = null;
			strPayDateToOld = null;
			strPayDateToNew = null;
			dblNetAmount = 0.0d;
			hmInner = new LinkedHashMap();
			while(rs.next()){
				strMonthNew = rs.getString("pay_month");
				double dblAmount = rs.getDouble("amount");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					dblNetAmount = 0.0d;
					if(rs.getString("payment_mode")!=null && rs.getString("payment_mode").equalsIgnoreCase("M")){
						hmInner = (Map) hmSalary.get(uF.getDateFormat(strMonthNew, "M", "MMMM"));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}else{
						hmInner = (Map) hmSalary.get(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}
				}	
	
				strPayDateToNew = rs.getString("paid_to");
					
				if(strPayDateToNew!=null && !strPayDateToNew.equalsIgnoreCase(strPayDateToOld)){
					dblNetAmount = 0.0d;
					if(rs.getString("payment_mode")!=null && rs.getString("payment_mode").equalsIgnoreCase("M")){
						hmInner = (Map) hmSalary.get(uF.getDateFormat(strMonthNew, "M", "MMMM"));
						if(hmInner == null) hmInner = new LinkedHashMap();
					}else{
						hmInner = (Map) hmSalary.get(uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat()) + " - " +uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
						if(hmInner == null) hmInner = new LinkedHashMap();
						}
				}
				
				dblNetAmount = dblNetAmount +  dblAmount;
				
				hmInner.put("REIMB_CTC_NET_AMOUNT_SECONDARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("REIMB_CTC_PAY_DATE_SECONDARY", uF.getDateFormat(rs.getString("paid_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("REIMB_CTC_FYS_SECONDARY", uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_FYE_SECONDARY", uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_MODE_SECONDARY", rs.getString("payment_mode"));
				hmInner.put("REIMB_CTC_PCS_SECONDARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_PCE_SECONDARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				hmInner.put("REIMB_CTC_MONTH_SECONDARY", rs.getString("pay_month"));
				hmInner.put("REIMB_CTC_PC_SECONDARY", rs.getString("paycycle"));
				hmInner.put("REIMB_CTC_BANK_PAY_TYPE_SECONDARY", rs.getString("bank_pay_type"));
					
				hmSalary.put(uF.getDateFormat(strMonthNew, "M", "MMMM"), hmInner);
				
				strMonthOld = strMonthNew;
				strPayDateToOld = strPayDateToNew;
			}
			rs.close();
			pst.close();
			
			Iterator it = hmSalary.keySet().iterator();
			while(it.hasNext()){
				String str = null;
				if(it.hasNext()){
					str = (String)it.next();
				}
				
				Map hmTemp = (Map)hmSalary.get(str);
				
				if(hmTemp!=null){
					alInner = new ArrayList();
					alInner1 = new ArrayList();
					
					alInner.add(str);
					 alInner1.add(str);
			  	//	Collections.reverse(alInner1);
					 
					String strPayrollAmt = (String)hmTemp.get("NET_AMOUNT_PRIMARY");
					String strDownload="";
					String strSalary = "";
					String strSalary1 = "";

					if(strPayrollAmt!=null){
						if((String)hmTemp.get("MONTH_PRIMARY")!=null && (String)hmTemp.get("PC_PRIMARY")!=null){
							strDownload="<a href=\"ExportSalarySlip.action?EID="+strEmpId+"&SID=0&M="+(String)hmTemp.get("MONTH_PRIMARY")+"&PC="+(String)hmTemp.get("PC_PRIMARY")+"&FYS="+(String)hmTemp.get("FYS_PRIMARY")+"&FYE="+(String)hmTemp.get("FYE_PRIMARY")+"&PD="+(String)hmTemp.get("PAY_DATE_PRIMARY")+"&PCS="+(String)hmTemp.get("PCS_PRIMARY")+"&PCE="+(String)hmTemp.get("PCE_PRIMARY")+"&BPT="+(String)hmTemp.get("BANK_PAY_TYPE_PRIMARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>"; 
						}
						strSalary = strPayrollAmt+" "+getLegend(uF.parseToBoolean((String)hmTemp.get("IS_PAID_PRIMARY")))+"    "+strDownload;
						strSalary1 =strPayrollAmt;
					}

					String strPayrollSecondaryAmt = (String)hmTemp.get("NET_AMOUNT_SECONDARY");
					String strSecondaryDownload="";
					if(strPayrollSecondaryAmt!=null){
						if((String)hmTemp.get("MONTH_SECONDARY")!=null && (String)hmTemp.get("PC_SECONDARY")!=null){
							strSecondaryDownload="<a href=\"ExportSalarySlip.action?EID="+strEmpId+"&SID=0&M="+(String)hmTemp.get("MONTH_SECONDARY")+"&PC="+(String)hmTemp.get("PC_SECONDARY")+"&FYS="+(String)hmTemp.get("FYS_SECONDARY")+"&FYE="+(String)hmTemp.get("FYE_SECONDARY")+"&PD="+(String)hmTemp.get("PAY_DATE_SECONDARY")+"&PCS="+(String)hmTemp.get("PCS_SECONDARY")+"&PCE="+(String)hmTemp.get("PCE_SECONDARY")+"&BPT="+(String)hmTemp.get("BANK_PAY_TYPE_SECONDARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
						}
						strSalary += "<br/>"+strPayrollSecondaryAmt+" "+getLegend(uF.parseToBoolean((String)hmTemp.get("IS_PAID_SECONDARY")))+"    "+strSecondaryDownload;
						strSalary1 +=strPayrollSecondaryAmt;
					}
					alInner.add(strSalary);
					alInner1.add(uF.showData(strSalary1, "0"));

					
					String strReimburseAmt = (String)hmTemp.get("REIMBURSE_AMOUNT");
					String strReimburseStatus = (String)hmTemp.get("REIMBURSE_STATUS");
					if(strReimburseAmt!=null){
						alInner.add(strReimburseAmt+" "+getLegend(strReimburseStatus));
					}else{
						alInner.add("-");
					}
					alInner1.add(uF.showData(strReimburseAmt, "0"));
					
					/**
					 * Added By Mayuri B. <--------------Start------------>
					 * */
					
					String strPayrollAmtRmb = (String)hmTemp.get("REIMB_CTC_NET_AMOUNT_PRIMARY");
					String strDownloadRmb="";
					String strSalaryRmb = "";
					String strSalaryRmb1 = "";

					if(strPayrollAmtRmb!=null){
						if((String)hmTemp.get("REIMB_CTC_MONTH_PRIMARY")!=null && (String)hmTemp.get("REIMB_CTC_PC_PRIMARY")!=null){
							strDownloadRmb="<a href=\"ReimbursementCTCPayslip.action?EID="+strEmpId+"&SID=0&M="+(String)hmTemp.get("REIMB_CTC_MONTH_PRIMARY")+"&PC="+(String)hmTemp.get("REIMB_CTC_PC_PRIMARY")+"&FYS="+(String)hmTemp.get("REIMB_CTC_FYS_PRIMARY")+"&FYE="+(String)hmTemp.get("REIMB_CTC_FYE_PRIMARY")+"&PD="+(String)hmTemp.get("REIMB_CTC_PAY_DATE_PRIMARY")+"&PCS="+(String)hmTemp.get("REIMB_CTC_PCS_PRIMARY")+"&PCE="+(String)hmTemp.get("REIMB_CTC_PCE_PRIMARY")+"&BPT="+(String)hmTemp.get("REIMB_CTC_BANK_PAY_TYPE_PRIMARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>"; 
						}
						strSalaryRmb = strPayrollAmtRmb+" "+getLegend(true)+"    "+strDownloadRmb;
						strSalaryRmb1=strPayrollAmtRmb;
					}	
					
					String strPayrollSecondaryAmtRmb = (String)hmTemp.get("REIMB_CTC_NET_AMOUNT_SECONDARY");
					String strSecondaryDownloadRmb="";

					if(strPayrollSecondaryAmtRmb!=null){
						if((String)hmTemp.get("REIMB_CTC_MONTH_SECONDARY")!=null && (String)hmTemp.get("REIMB_CTC_PC_SECONDARY")!=null){
							strSecondaryDownloadRmb="<a href=\"ReimbursementCTCPayslip.action?EID="+strEmpId+"&SID=0&M="+(String)hmTemp.get("REIMB_CTC_MONTH_SECONDARY")+"&PC="+(String)hmTemp.get("REIMB_CTC_PC_SECONDARY")+"&FYS="+(String)hmTemp.get("REIMB_CTC_FYS_SECONDARY")+"&FYE="+(String)hmTemp.get("REIMB_CTC_FYE_SECONDARY")+"&PD="+(String)hmTemp.get("REIMB_CTC_PAY_DATE_SECONDARY")+"&PCS="+(String)hmTemp.get("REIMB_CTC_PCS_SECONDARY")+"&PCE="+(String)hmTemp.get("REIMB_CTC_PCE_SECONDARY")+"&BPT="+(String)hmTemp.get("REIMB_CTC_BANK_PAY_TYPE_SECONDARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
						}
						strSalaryRmb += "<br/>"+strPayrollSecondaryAmtRmb+" "+getLegend(true)+"    "+strSecondaryDownloadRmb;
						strSalaryRmb1+=strPayrollSecondaryAmtRmb;
					}
					alInner.add(strSalaryRmb);
					alInner1.add(uF.showData(strSalaryRmb1, "0"));
					
					/**
					 * Added By Mayuri B.  <--------------End------------>
					 * */
					
					String strCTCVariableAmt = (String)hmTemp.get("CTC_VARIABLE_AMOUNT");
					String strCTCVariableStatus = (String)hmTemp.get("CTC_VARIABLE_STATUS");
					if(strCTCVariableAmt!=null){
						alInner.add(strCTCVariableAmt+" "+getLegend(strCTCVariableStatus));
					}else{
						alInner.add("-");
					}
					alInner1.add(uF.showData(strCTCVariableAmt, "0"));
					
					String strPerkAmt = (String)hmTemp.get("PERK_AMOUNT");
					String strPerkStatus = (String)hmTemp.get("PERK_STATUS");
					if(strPerkAmt!=null){
						alInner.add(strPerkAmt+" "+getLegend(strPerkStatus));
					}else{
						alInner.add("-");
					}
					alInner1.add(uF.showData(strPerkAmt, "0"));
				
					al1.add(alInner1);
				
					al.add(alInner);
				}
				
			}
			
			request.setAttribute("reportList1", al1);
			request.setAttribute("reportList", al);
			
			StringBuilder sbMonth=new StringBuilder();
			StringBuilder sbPayrollAmount=new StringBuilder();
			StringBuilder sbReimburse=new StringBuilder();
			StringBuilder sbReimburseCTC=new StringBuilder();
			StringBuilder sbCTC=new StringBuilder();
			StringBuilder sbPerk=new StringBuilder();

//*******************data for chart******************
			sbMonth.append("[");
			sbPayrollAmount.append("[");
			sbReimburse.append("[");
			sbReimburseCTC.append("[");
			sbCTC.append("[");
			sbPerk.append("[");
			
			java.util.List couterlist = (java.util.List)request.getAttribute("reportList1"); 
			//System.out.println("couterlist.size==>"+couterlist.size());
			
			if(couterlist!=null){
					
				for (int i=couterlist.size()-1; i>=0; i--) {
					
					java.util.List cinnerlist = (java.util.List)couterlist.get(i); 
					      
						sbMonth.append("'"+cinnerlist.get(0)+"'");
						//System.out.println("cinnerlist.get(0)"+cinnerlist.get(0));
						
						sbPayrollAmount.append(cinnerlist.get(1));
						sbReimburse.append(cinnerlist.get(2));
						sbReimburseCTC.append(cinnerlist.get(3));
						sbCTC.append(cinnerlist.get(4));
						sbPerk.append(cinnerlist.get(5));
						
							if(i<=couterlist.size()-1 && i!=0){
								sbMonth.append(",");
								sbPayrollAmount.append(",");
								sbReimburse.append(",");
								sbReimburseCTC.append(",");
								sbCTC.append(",");
								sbPerk.append(",");
							}
				}
		}
				sbMonth.append("]");
				sbPayrollAmount.append("]");
				sbReimburse.append("]");
				sbReimburseCTC.append("]");
				sbCTC.append("]");
				sbPerk.append("]");
				
				//System.out.println("sbMonth=="+sbMonth);
				request.setAttribute("sbMonth", sbMonth);
				request.setAttribute("sbPayrollAmount", sbPayrollAmount);
				request.setAttribute("sbReimburse", sbReimburse);
				request.setAttribute("sbReimburseCTC", sbReimburseCTC);
				request.setAttribute("sbCTC", sbCTC);
				request.setAttribute("sbPerk", sbPerk);

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadPaySlips(){
		UtilityFunctions uF = new UtilityFunctions();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF);
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		getSelectedFilter(uF);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
			return LOAD;
		} else {
			return "ghrload";
		}
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null && !getFinancialYear().equals("")) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
			alFilter.add("MONTH");
			hmFilter.put("MONTH", uF.getMonth(uF.parseToInt(getStrMonth())));
		}
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private String getLegend(boolean bool){
		if(bool){
			/*return "<img src=\"images1/icons/approved.png\" title=\"Paid\" border=\"0\" style=\"padding:5px 0 0 10px\">";*/
			return "<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Paid\" style=\"color:#54aa0d; padding:5px 0 0 10px\"></i>";
		}else{
			/*return "<img src=\"images1/icons/act_now.png\" title=\"Waiting to be paid\" border=\"0\" style=\"padding:5px 0 0 10px\">";*/
			return "<img src=\"images1/icons/act_now.png\" title=\"Waiting to be paid\" border=\"0\" style=\"padding:5px 0 0 10px\">";
		}
	}
	
	private String getLegend(String strStatus){
		if(strStatus!=null && strStatus.equalsIgnoreCase("2")){
			/*return "<img src=\"images1/icons/approved.png\" title=\"Paid\" border=\"0\" style=\"padding:5px 0 0 10px\">";*/
			return "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" style=\"padding:5px 0 0 10px\" title=\"Paid\"></i>";
		}else if(strStatus!=null && strStatus.equalsIgnoreCase("1")){
			/*return "<img src=\"images1/icons/act_now.png\" title=\"Waiting to be paid\" border=\"0\" style=\"padding:5px 0 0 10px\">";*/
			return "<img src=\"images1/icons/act_now.png\" title=\"Waiting to be paid\" border=\"0\" style=\"padding:5px 0 0 10px\">";
		}else{
			/*return "<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" border=\"0\" style=\"padding:5px 0 0 10px\">";*/
			return "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" style=\"padding:5px 0 0 10px\" title=\"Waiting for approval\"></i>";
		}
	}
	
	public String viewPaySlips(String strEmpId){
		
		//System.out.println(" in viewPaySlips");

		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			if(getStrMonth()!=null){
				setStrMonth(getStrMonth());
			}else{
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			
			con = db.makeConnection(con);

			Map hmEmpMap = CF.getEmpNameMap(con,null,null);
			Map<String, String> hmEmpCode = CF.getEmpCodeMap(con);
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
			
			Map<String, String> hmEmpPanNo =CF.getEmpPANNoMap(con);	//added by parvez
			if(hmEmpPanNo == null) hmEmpPanNo = new HashMap<String, String>();
			
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			Map<String, String> hmOrg = CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			/**
			 * Primary 
			 * */
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
				"and month=? and is_paid = true and (bank_pay_type=1 or bank_pay_type is null) order by emp_id,earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			rs = pst.executeQuery();
//			System.out.println("pst===>>"+pst);
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			double dblNetAmount = 0.0d;
			Map<String, String> hmInner = new HashMap<String, String>();
			Map<String, Map<String, String>> hmSalary = new HashMap<String, Map<String,String>>();
			while(rs.next()) {
				
				strEmpIdNew = rs.getString("emp_id");
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					dblNetAmount = 0.0d;
					hmInner = new HashMap<String, String>();
				}	
				
				if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")) {
					dblNetAmount = dblNetAmount +  dblAmount;
				} else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")) {
					dblNetAmount = dblNetAmount -  dblAmount;
				}
				
				hmInner.put("EMP_ID_PRIMARY", strEmpIdNew);
				hmInner.put("NET_AMOUNT_PRIMARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblNetAmount));
				hmInner.put("PAY_DATE_PRIMARY", uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("FYS_PRIMARY", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
				hmInner.put("FYE_PRIMARY", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
				hmInner.put("MODE_PRIMARY", rs.getString("pay_mode"));
				
				hmInner.put("PCS_PRIMARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("PCE_PRIMARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				
				hmInner.put("MONTH_PRIMARY", rs.getString("month"));
				
				hmInner.put("PC_PRIMARY", rs.getString("paycycle"));
				hmInner.put("SERVICE_ID_PRIMARY", rs.getString("service_id"));
				hmInner.put("IS_PAID_PRIMARY", rs.getString("is_paid"));
				hmInner.put("BANK_PAY_TYPE_PRIMARY", rs.getString("bank_pay_type"));
				
				hmSalary.put(strEmpIdNew, hmInner);
				
				strEmpIdOld = strEmpIdNew;  
			}
			rs.close();
			pst.close();
			
			/**
			 * Secondary
			 * */
			pst = con.prepareStatement("select * from payroll_generation pg where financial_year_from_date=? and financial_year_to_date=? " +
					"and month=? and is_paid = true and bank_pay_type=2 order by emp_id,earning_deduction desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			rs = pst.executeQuery();
//			System.out.println("pst===>>"+pst);
			strEmpIdNew = null;
			strEmpIdOld = null;
			dblNetAmount = 0.0d;
			hmInner = new HashMap<String, String>();
			Map<String, Map<String, String>> hmSalarySecondary = new HashMap<String, Map<String,String>>();
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				String strEarningDeduction = rs.getString("earning_deduction");
				double dblAmount = rs.getDouble("amount");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					dblNetAmount = 0.0d;
					hmInner = new HashMap<String, String>();
				}	
				
				
				if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
					dblNetAmount = dblNetAmount +  dblAmount;
				}else if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("D")){
					dblNetAmount = dblNetAmount -  dblAmount;
				}
				
				hmInner.put("EMP_ID_SECONDARY", strEmpIdNew);
				hmInner.put("NET_AMOUNT_SECONDARY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetAmount));
				hmInner.put("PAY_DATE_SECONDARY", uF.getDateFormat(rs.getString("pay_date"), DBDATE, DATE_FORMAT)); 
				hmInner.put("FYS_SECONDARY", uF.getDateFormat(rs.getString("financial_year_from_date"), DBDATE, DATE_FORMAT));
				hmInner.put("FYE_SECONDARY", uF.getDateFormat(rs.getString("financial_year_to_date"), DBDATE, DATE_FORMAT));
				hmInner.put("MODE_SECONDARY", rs.getString("pay_mode"));
				
				hmInner.put("PCS_SECONDARY", uF.getDateFormat(rs.getString("paid_from"), DBDATE, DATE_FORMAT));
				hmInner.put("PCE_SECONDARY", uF.getDateFormat(rs.getString("paid_to"), DBDATE, DATE_FORMAT));
				
				hmInner.put("MONTH_SECONDARY", rs.getString("month"));
				
				hmInner.put("PC_SECONDARY", rs.getString("paycycle"));
				hmInner.put("SERVICE_ID_SECONDARY", rs.getString("service_id"));
				hmInner.put("IS_PAID_SECONDARY", rs.getString("is_paid"));
				hmInner.put("BANK_PAY_TYPE_SECONDARY", rs.getString("bank_pay_type"));
				
				hmSalarySecondary.put(strEmpIdNew, hmInner);
				
				strEmpIdOld = strEmpIdNew;  
			}
			rs.close();
			pst.close();
			
			Map hmCurrencyMap = CF.getCurrencyDetails(con);
			Map hmEmpCurrencyMap = CF.getEmpCurrency(con);
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			Iterator<String> it = hmSalary.keySet().iterator();
			while(it.hasNext()){
				String str = (String)it.next();
				Map<String, String> hmTemp = (Map<String, String>)hmSalary.get(str);
				if(hmTemp == null) hmTemp = new HashMap<String, String>();
				Map<String, String> hmTempSecondary = (Map<String, String>)hmSalarySecondary.get(str);
				if(hmTempSecondary == null) hmTempSecondary = new HashMap<String, String>();
				
				alInner = new ArrayList<String>();
				alInner.add(uF.showData(hmEmpCode.get(str),""));	//0
				alInner.add((String)hmEmpMap.get(str));				//1
				alInner.add(uF.showData(hmEmpPanNo.get(str),""));			//2
				alInner.add(uF.showData(hmOrg.get(hmEmpOrgId.get(str)),""));	//3
				alInner.add(uF.getDateFormat((String)hmTemp.get("PAY_DATE_PRIMARY"), DATE_FORMAT, CF.getStrReportDateFormat()));
				
				String strPayrollAmt = (String)hmTemp.get("NET_AMOUNT_PRIMARY");
				String strDownload="";
				String strSalary = "";
				if(strPayrollAmt!=null) {
					if((String)hmTemp.get("MONTH_PRIMARY")!=null && (String)hmTemp.get("PC_PRIMARY")!=null){
						strDownload="<a href=\"ExportSalarySlip.action?EID="+str+"&SID=0&M="+(String)hmTemp.get("MONTH_PRIMARY")+"&PC="+(String)hmTemp.get("PC_PRIMARY")+"&FYS="+(String)hmTemp.get("FYS_PRIMARY")+"&FYE="+(String)hmTemp.get("FYE_PRIMARY")+"&PD="+(String)hmTemp.get("PAY_DATE_PRIMARY")+"&PCS="+(String)hmTemp.get("PCS_PRIMARY")+"&PCE="+(String)hmTemp.get("PCE_PRIMARY")+"&BPT="+(String)hmTemp.get("BANK_PAY_TYPE_PRIMARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>"; 
					}
					strSalary = strPayrollAmt+" "+getLegend(uF.parseToBoolean((String)hmTemp.get("IS_PAID_PRIMARY")))+"    "+strDownload;
				}				
				
				String strPayrollSecondaryAmt = (String)hmTempSecondary.get("NET_AMOUNT_SECONDARY");
				String strSecondaryDownload="";
				if(strPayrollSecondaryAmt!=null){
					if((String)hmTempSecondary.get("MONTH_SECONDARY")!=null && (String)hmTempSecondary.get("PC_SECONDARY")!=null){
						strSecondaryDownload="<a href=\"ExportSalarySlip.action?EID="+str+"&SID=0&M="+(String)hmTempSecondary.get("MONTH_SECONDARY")+"&PC="+(String)hmTempSecondary.get("PC_SECONDARY")+"&FYS="+(String)hmTempSecondary.get("FYS_SECONDARY")+"&FYE="+(String)hmTempSecondary.get("FYE_SECONDARY")+"&PD="+(String)hmTempSecondary.get("PAY_DATE_SECONDARY")+"&PCS="+(String)hmTempSecondary.get("PCS_SECONDARY")+"&PCE="+(String)hmTempSecondary.get("PCE_SECONDARY")+"&BPT="+(String)hmTempSecondary.get("BANK_PAY_TYPE_SECONDARY")+"\"><i class=\"fa fa-file-o\" aria-hidden=\"true\"></i></a>";
					}
					strSalary += "<br/>"+strPayrollSecondaryAmt+" "+getLegend(uF.parseToBoolean((String)hmTempSecondary.get("IS_PAID_SECONDARY")))+"    "+strSecondaryDownload;
				}
				alInner.add(strSalary);
				
			
				al.add(alInner);
					
				
			}
			
			request.setAttribute("reportList", al);  
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
		return SUCCESS;
		
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getFinancialYear() {
		return financialYear;
	}


	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
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


	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}
	public String getStrMonth() {
		return strMonth;
	}
	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}


	public String getAlertStatus() {
		return alertStatus;
	}


	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}


	public String getAlert_type() {
		return alert_type;
	}


	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}


	public String getProfileEmpId() {
		return profileEmpId;
	}


	public void setProfileEmpId(String profileEmpId) {
		this.profileEmpId = profileEmpId;
	}


	public String getAlertID() {
		return alertID;
	}


	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

}
