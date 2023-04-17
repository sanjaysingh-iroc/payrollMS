package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveArrear extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	

	/**
	 *   
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;   
	String strUserType;  
	String strSessionEmpId;       
   
	String strFrmD1 = null; 
	String strFrmD2 = null;  
	String strD1 = null;
	String strD2 = null;
	String strPC = null; 
	String approvePC = null;
	String strAlpha = null;

	CommonFunctions CF = null;

	private static Logger log = Logger.getLogger(ApproveArrear.class);
	
	
	String strVeryEmpId;
	public String execute() throws Exception {

		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strVeryEmpId = (String)request.getParameter("strVeryEmpId");
		
		
		String APPROVE = (String) request.getParameter("approve"); 
		strAlpha = (String) request.getParameter("alphaValue");
		setStrAlpha(strAlpha);

		request.setAttribute(TITLE, "Approve Arrear");
		strEmpID = (String) request.getParameter("EMPID");
		String strEmpType = (String) session.getAttribute("USERTYPE");

		String[] strPayCycleDates = null;
	
		if(getApprovePC()!=null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length()>0){
			strPayCycleDates = getApprovePC().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}else if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
			strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		
		if(getWLocation()!=null){
			setWLocation(getWLocation());
		}
		
		if(getLevel()!=null){
			setLevel(getLevel());
		}
		

		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];
		strPC = strPayCycleDates[2];
		
		String referer = request.getHeader("Referer");

		if (referer != null) {
			int index1 = referer.indexOf(request.getContextPath());
			int index2 = request.getContextPath().length();
			referer = referer.substring(index1 + index2 + 1);
		}
		setRedirectUrl(referer);

		
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		
		
			request.setAttribute(PAGE, "/jsp/payroll/ApproveArrear.jsp");
			strEmpID = (String) session.getAttribute(EMPID);
			if(getF_org()==null){
				setF_org((String)session.getAttribute(ORGID));
			}
			if(getStrPaycycleDuration()==null){
				setStrPaycycleDuration("M");
			}
			loadClockEntries(uF);
			
			if (APPROVE != null) {
				
				request.setAttribute(MESSAGE, "Arrear generated");
				ApproveArrearEntries(CF);

				return SUCCESS;
			}
			viewClockEntriesForPayrollArrear(CF, null, strD1, strD2);
			
		
		
		return LOAD;

	}

	
	public String loadClockEntries(UtilityFunctions uF) {
//		paycycleList = new FillPayCycles(getStrPaycycleDuration()).fillPayCycles(CF, getF_org());
		
		String[] arr = null;
		arr = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
		paycycleList=new ArrayList<FillPayCycles>();
		paycycleList.add(new FillPayCycles(arr[0]+"-"+arr[1]+"-"+arr[2], "Pay Cycle " + arr[2] + ", " + uF.getDateFormat(arr[0], DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getDateFormat(arr[1], DATE_FORMAT, CF.getStrReportDateFormat())));
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
		paymentModeList = new FillPayMode().fillPaymentMode();
		
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		return LOAD;
	}

	Map hmEmpRosterLunchDeduction = new HashMap();
	Map<String, Map<String, String>> hmLeavesMap = null;
	Map<String, String> hmLeaves = null;
	
	public String ApproveArrearEntries(CommonFunctions CF) {

		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			Map hmCurrencyDetails = CF.getCurrencyDetails(con);

			/*pst = con.prepareStatement(selectSettings);
			rs = pst.executeQuery();
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			
			while(rs.next()){
				
				if(rs.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_END)){
					strFinancialYearEnd = rs.getString("value");
				}
				if(rs.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_START)){
					strFinancialYearStart = rs.getString("value");
				}
				
			}*/
			
			String[] strApprovePayCycle=null;
			strApprovePayCycle=CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strApprovePayCycle[1], CF, uF);
			if(strFinancialYear!=null){
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			/*String []strApprovePayCycle = null;
			
			if(getApprovePC()!=null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length()>0){
				strApprovePayCycle = getApprovePC().split("-");
				setPaycycle(getApprovePC());
			}else{
				strApprovePayCycle = getPaycycle().split("-");
				setPaycycle(getPaycycle());
			}
			
			
			if(strApprovePayCycle==null){
				strApprovePayCycle = new String[3];
			}*/
			
			Map<String,Map<String, Map<String, String>>> hmTotalSalary = (Map<String,Map<String, Map<String, String>>>)session.getAttribute("AR_hmTotalSalary");
			Map hmEmpNameMap = (Map)session.getAttribute("AR_hmEmpNameMap");
			Map hmSalaryDetails = (Map)session.getAttribute("AR_hmSalaryDetails");
			Map hmEmpSalary = (Map)session.getAttribute("AR_hmEmpSalary");
			Map hmServices = (Map)session.getAttribute("AR_hmServices");
			if(hmServices==null) hmServices=new HashMap();
			List alEmp = (List)session.getAttribute("AR_alEmp");
			List alProcessingEmployee = (List)session.getAttribute("AR_alProcessingEmployee");
			if(alProcessingEmployee==null) alProcessingEmployee=new ArrayList();
			
			Map<String, String> hmPresentDays = (Map)session.getAttribute("AR_hmPresentDays");
			if(hmPresentDays==null) hmPresentDays=new HashMap<String, String>();
			
			Map<String, String> hmPaidDays = (Map)session.getAttribute("AR_hmPaidDays");
			if(hmPaidDays==null) hmPaidDays=new HashMap<String, String>();
			
			Map hmLeaveDays 	= (Map)session.getAttribute("AR_hmLeaveDays");
			if(hmLeaveDays==null) hmLeaveDays=new HashMap();
			Map hmLeaveTypeDays 	= (Map)session.getAttribute("AR_hmLeaveTypeDays");
			if(hmLeaveTypeDays==null) hmLeaveTypeDays=new HashMap();
			
			Map<String, String> hmMonthlyLeaves 	= (Map)session.getAttribute("AR_hmMonthlyLeaves");
			if(hmLeaveTypeDays==null) hmLeaveTypeDays=new HashMap();
			Map<String, String> hmLoanAmt 	= (Map)session.getAttribute("AR_hmLoanAmt");
			if(hmLeaveTypeDays==null) hmLeaveTypeDays=new HashMap();
			Map<String, String> hmEmpPaymentMode 	= (Map)session.getAttribute("AR_hmEmpPaymentMode");
			if(hmEmpPaymentMode==null) hmEmpPaymentMode=new HashMap();
			Map<String, String> hmEmpStateMap 	= (Map)session.getAttribute("AR_hmEmpStateMap");
			if(hmEmpStateMap==null) hmEmpStateMap=new HashMap();
			Map<String, String> hmVariables 	= (Map)session.getAttribute("AR_hmVariables");
			if(hmVariables==null) hmVariables=new HashMap();
			
//			System.out.println("hmEmpPaymentMode=====>"+hmEmpPaymentMode);
			   
			double dbTotalDays = uF.parseToDouble((String)session.getAttribute("AR_strTotalDays"));
			List alEmpSalaryDetailsEarning = (List)session.getAttribute("AR_alEmpSalaryDetailsEarning");
			List alEmpSalaryDetailsDeduction = (List)session.getAttribute("AR_alEmpSalaryDetailsDeduction");
			
			Map<String,String> hmTotalDays=(Map<String, String>)session.getAttribute("AR_hmTotalDays");

			Map<String, String> hmOtherTaxDetails 	= (Map)session.getAttribute("AR_hmOtherTaxDetails");
			Map<String, String> hmEmpLevelMap 	= (Map)session.getAttribute("AR_hmEmpLevelMap");
			Map<String, String> hmEmpArrearDate=(Map)session.getAttribute("AR_hmEmpArrearDate");
//			System.out.println("insert hmEmpArrearDate====>"+hmEmpArrearDate);
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			
			log.debug("hmServices===>"+hmServices);
			
			
			GenerateSalarySlip gs = new GenerateSalarySlip();
			ApproveArrearRunnable objRunnable = new ApproveArrearRunnable(this, gs, con, uF, CF, strFinancialYearStart, strFinancialYearEnd, strApprovePayCycle, hmEmpStateMap, hmCurrencyDetails, hmEmpCurrency, hmVariables, request, response,hmOtherTaxDetails,hmEmpLevelMap);
			
			int count=-1; 
			Set set0 = hmTotalSalary.keySet();
			Iterator it0 = set0.iterator();
			while(it0.hasNext()){
				String strEmpId = (String)it0.next();
				String strOrgId = hmEmpOrgId.get(strEmpId);
				Map hmTotal = (Map)hmTotalSalary.get(strEmpId);
				
				List alServices = (List)hmServices.get(strEmpId);
				if(alServices==null)alServices=new ArrayList();
				
				String arr[] = getChbox();
				int x = ArrayUtils.contains(arr, strEmpId);
				if(x<0)continue;
				
				count = alProcessingEmployee.indexOf(strEmpId);
				
				Map hmLeaves = (Map)hmLeaveDays.get(strEmpId);
				if(hmLeaves==null)hmLeaves = new HashMap();
				
				Map hmLeavesType = (Map)hmLeaveTypeDays.get(strEmpId);
				if(hmLeavesType==null)hmLeavesType = new HashMap();
				
				String arrearDate=hmEmpArrearDate.get(strEmpId);
				String[] tempDate=arrearDate.split("::::");
				String startArrearDate=tempDate[0];
				String endArrearDate=tempDate[1];
				int monthCount=uF.parseToInt(tempDate[2]);
				
				
				
				double dblPresentDays = uF.parseToDouble(hmPresentDays.get(strEmpId));
				double dblPaidLeaveDays = uF.parseToDouble((String)hmLeavesType.get("COUNT"));
				double dblPaidDays = uF.parseToDouble(hmPaidDays.get(strEmpId));
				
				double dblTotal = 0.0;
//				System.out.println("getStrPaycycleDuration()=======>"+getStrPaycycleDuration());
				
				for(int i=0;i<alEmpSalaryDetailsEarning.size(); i++){
					String strSalaryId = (String)alEmpSalaryDetailsEarning.get(i);
					
					// As Bonus is paid independent of paycycle...
//					if(uF.parseToInt(strSalaryId)==BONUS && !uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())){
//						continue;
//					}
					
					
					if(hmTotal!=null && !hmTotal.containsKey(strSalaryId)){
						continue;
					}
					
					Map<String, String> hmArrearInner =(Map<String, String>)hmTotal.get(strSalaryId);
					
					pst = con.prepareStatement("insert into arrear_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
					pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
					pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
					pst.setInt(6, uF.parseToInt(strSalaryId));
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmArrearInner.get("AMOUNT")))));
					pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
					pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
					pst.setInt(12, uF.parseToInt(((alServices.size()>0)?(String)alServices.get(0):"0")));
					pst.setString(13,"E");
					//pst.setString(14,getStrPaycycleDuration());
					pst.setString(14,"M");
					pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
					pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
					pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
					pst.setDouble(18, dblPresentDays);
					pst.setDouble(19, dblPaidDays);
					pst.setDouble(20, dblPaidLeaveDays);
					//pst.setDouble(21, dbTotalDays);
					pst.setDouble(21, uF.parseToDouble(hmTotalDays.get(strEmpId)));
					pst.execute();
					pst.close();
					
					
					dblTotal += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmArrearInner.get("AMOUNT"))));
					
					
					
					
					double dblAmt = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmArrearInner.get("AMOUNT"))));
					
					if(strApprovePayCycle.length>0 && uF.parseToInt(strSalaryId)==REIMBURSEMENT && dblAmt>0){
						
						pst = con.prepareStatement(updateReimbursementPayroll);
						
						pst.setBoolean(1, true);
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strEmpId));
						pst.execute();
						pst.close();
						
					}
					
					
					if(strApprovePayCycle.length>0 && uF.parseToInt(strSalaryId)==TRAVEL_REIMBURSEMENT && dblAmt>0){
						
						pst = con.prepareStatement(updateReimbursementPayroll1);
						
						pst.setBoolean(1, true);
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strEmpId));
						pst.execute();
						pst.close();
						
//						System.out.println("update reimbursement===>"+pst);
					}
					
					if(strApprovePayCycle.length>0 && uF.parseToInt(strSalaryId)==MOBILE_REIMBURSEMENT && dblAmt>0){
						
						pst = con.prepareStatement(updateReimbursementPayroll2);
						
						pst.setBoolean(1, true);
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strEmpId));
						pst.execute();
						pst.close();
						
					}
					
					if(strApprovePayCycle.length>0 && uF.parseToInt(strSalaryId)==OTHER_REIMBURSEMENT && dblAmt>0){
						
						pst = con.prepareStatement(updateReimbursementPayroll3);
						
						pst.setBoolean(1, true);
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strEmpId));
						pst.execute();
						pst.close();
						
					}
					
					
				}
				
				for(int i=0;i<alEmpSalaryDetailsDeduction.size(); i++){
					String strSalaryId = (String)alEmpSalaryDetailsDeduction.get(i);


					if(hmTotal!=null && !hmTotal.containsKey(strSalaryId)){
						continue;
					}
					
					Map<String, String> hmArrearInner =(Map<String, String>)hmTotal.get(strSalaryId);
					
					
//					pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id) values (?,?,?,?,?,?,?,?,?,?,?)");
					pst = con.prepareStatement("insert into arrear_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id, service_id, earning_deduction, pay_mode, paid_from, paid_to, payment_mode, present_days, paid_days, paid_leaves, total_days) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(strEmpId));
					pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
					pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
					pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
					pst.setInt(6, uF.parseToInt(strSalaryId));
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmArrearInner.get("AMOUNT")))));
					pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
					pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
					pst.setInt(12, uF.parseToInt(((alServices.size()>0)?(String)alServices.get(0):"0")));
					pst.setString(13,"D");
					pst.setString(14,"M");
					pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
					pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
					pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
					pst.setDouble(18, dblPresentDays);
					pst.setDouble(19, dblPaidDays);
					pst.setDouble(20, dblPaidLeaveDays);
					//pst.setDouble(21, dbTotalDays);
					pst.setDouble(21, uF.parseToDouble(hmTotalDays.get(strEmpId)));
					pst.execute();
					pst.close();
					 
					log.debug("Inserting  D  ==== "+pst);
					log.debug("Inserting  D  ==== "+hmTotal);
					
					dblTotal -= uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmArrearInner.get("AMOUNT"))));
					
					double dblAmt = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmArrearInner.get("AMOUNT"))));
					
					
					if(strApprovePayCycle.length>0 && uF.parseToInt(strSalaryId)==LOAN && dblAmt>0){
						
						pst = con.prepareStatement(selectLoanPyroll2);
						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
						rs = pst.executeQuery();
						
						double dblBalanceAmt = 0;
						int nLoanId = 0;
						int nLoanAppId = 0;
						while(rs.next()){
							
							dblBalanceAmt = rs.getDouble("balance_amount");
							nLoanId = rs.getInt("loan_id");
							nLoanAppId = rs.getInt("loan_applied_id");
							
						
//						dblBalanceAmt = dblBalanceAmt - dblAmt;
						
						
							double dblAmt1 = uF.parseToDouble(hmLoanAmt.get(nLoanAppId+""));
							dblBalanceAmt = dblBalanceAmt - dblAmt1;
							
							
							
							
							pst1 = con.prepareStatement(updateLoanPyroll1);
							pst1.setDouble(1, dblBalanceAmt);
							if(dblBalanceAmt>0){
								pst1.setBoolean(2, false);
							}else{
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
							pst1.setDate(9, uF.getDateFormat(strD1, DATE_FORMAT));
							pst1.execute();
							pst1.close();
							
							
						}
						rs.close();
						pst.close();
					
					}
					
					
					
				}
				
				pst=con.prepareStatement("update emp_arrear_details set is_arrear_paid=false where is_arrear_paid=true and emp_id=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.execute();
				pst.close();
				
				objRunnable.setData(hmTotal, strEmpId, dblTotal, strOrgId);
				Thread t = new Thread(objRunnable);
				t.start();
				
				
				Map<String, String> hmInnerCurrencyDetails = (Map<String, String>)hmCurrencyDetails.get(hmEmpCurrency.get(strEmpId)) ;
				if(hmInnerCurrencyDetails==null)hmInnerCurrencyDetails=new HashMap<String, String>();
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_SALARY_APPROVED, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(strEmpId);
				nF.setStrSalaryAmount(uF.showData(hmInnerCurrencyDetails.get("LONG_CURR"),"")+""+uF.formatIntoTwoDecimal(dblTotal));
				nF.setStrPaycycle(strApprovePayCycle[0]+"-"+strApprovePayCycle[1]);
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				
			}   
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewClockEntriesForPayrollArrear(CommonFunctions CF, String strReqEmpId, String strD1, String strD2) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			Map hmEmpNameMap = CF.getEmpNameMap(con, null, null); 
			Map hmEmpCodeMap = CF.getEmpCodeMap(con);
			Map hmEmpGenderMap = CF.getEmpGenderMap(con);
			Map hmEmpPaymentMode = CF.getEmpPaymentMode(con, uF);
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			Map<String, String> hmBreaks  = new HashMap<String, String>();
			Map<String, String> hmBreakPolicy  = new HashMap<String, String>();
			getBreakDetails(con, uF, hmBreaks, hmBreakPolicy, strD1, strD2);
			
			Map<String, String> hmVariables  = new HashMap<String, String>();
			getVariableAmount(con, uF, hmVariables, strPC);
			
			
			Map<String, String> hmEmpEndDateMap = new HashMap<String, String>();
			Map hmWlocationMap = CF.getWorkLocationMap(con);
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			Map hmEmpJoiningMap = CF.getEmpJoiningDateMap(con, uF, hmEmpEndDateMap); 

			
			
			Map hmBasicSalaryMap = CF.getSpecificSalaryData(con, BASIC);
			Map hmDASalaryMap = CF.getSpecificSalaryData(con, DA);
			
			if(getWLocation()==null && session!=null){
				setWLocation((String)session.getAttribute(WLOCATIONID));
			}

			
			Map<String,Map<String, Map<String, String>>> hmTotalSalary=new LinkedHashMap<String, Map<String,Map<String,String>>>();
			
			Map<String, String> hmTotalDays = new HashMap<String, String>();
			
			
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
			Map hmSalaryDetails = new HashMap();
			/*pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+") and org_id =? order by earning_deduction desc, salary_head_id, weight");
			pst.setInt(1, uF.parseToInt(getF_org()));*/
			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+") and level_id=-1 order by earning_deduction desc, salary_head_id, weight");
			rs = pst.executeQuery();  
			while(rs.next()){
				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbEmp = new StringBuilder();
			pst=con.prepareStatement("select * from emp_arrear_details where is_arrear_paid=true and emp_id in (select emp_id from employee_official_details where org_id=?)");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs=pst.executeQuery();
			Map<String, String> hmEmpArrearDate=new HashMap<String, String>();
			while(rs.next()){
				sbEmp.append(rs.getString("emp_id")+",");
				String sEdate=uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT)+"::::"+uF.getDateFormat(rs.getString("end_date"), DBDATE, DATE_FORMAT)+"::::"+rs.getString("month_count");
				hmEmpArrearDate.put(rs.getString("emp_id"), sEdate);
			}  
			rs.close();
			pst.close();
			
			if(sbEmp.length()>1){
				sbEmp.replace(0, sbEmp.length(), sbEmp.substring(0, sbEmp.length()-1));
			}
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and (employment_end_date is null OR employment_end_date >= ?) and joining_date<= ? and emp_per_id > 0 and emp_per_id in ("+sbEmp.toString()+")");
			
			if(uF.parseToInt(getLevel())>0){
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id = "+uF.parseToInt(getLevel())+")");
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id ="+uF.parseToInt(getF_department()));
			}
			
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id  like '%,"+uF.parseToInt(getF_service())+",%'");
			}
			
			
			if(uF.parseToInt(getWLocation())>0){
				sbQuery.append(" and wlocation_id ="+uF.parseToInt(getWLocation()));
			}
			
			if(getStrPaycycleDuration()!=null){
				sbQuery.append(" and paycycle_duration ='"+getStrPaycycleDuration()+"'");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id ="+uF.parseToInt(getF_org()));
			}

			if(uF.parseToInt(getF_paymentMode())>0){
				sbQuery.append(" and payment_mode ="+uF.parseToInt(getF_paymentMode()));
			}
			
			if((String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			sbQuery.append(" order by emp_fname, emp_lname");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst=========>"+pst);
			rs = pst.executeQuery();
			
			StringBuilder sbEmPId = new StringBuilder();
			
			Map<String, String> hmEmpPaycycleDuration = new HashMap<String, String>();
			List<String> alEmp = new ArrayList<String>();
			while(rs.next()){
				alEmp.add(rs.getString("emp_per_id"));
				sbEmPId.append(rs.getString("emp_per_id")+",");				
				hmEmpPaycycleDuration.put(rs.getString("emp_per_id"), rs.getString("paycycle_duration"));				
			}
			rs.close();
			pst.close();
			
			if(sbEmPId.length()>1){
				sbEmPId.replace(0, sbEmPId.length(), sbEmPId.substring(0, sbEmPId.length()-1));
			}
//			System.out.println("alEmp=======>"+alEmp.toString());
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if(strFinancialYear!=null){
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));

				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
			}
			rs.close();
			pst.close();
			
			
			Map<String,String> hmGrossNet=new HashMap<String, String>();
			
			for(int i=0;alEmp!=null && i<alEmp.size();i++){
				String empId=alEmp.get(i);
				String arrearDate=hmEmpArrearDate.get(empId);
				String[] tempDate=arrearDate.split("::::");
				String startArrearDate=tempDate[0];
				String endArrearDate=tempDate[1];
				int monthCount=uF.parseToInt(tempDate[2]);
				
				
				
				
				/**
				 * 
				 * Prem Motors Break Policies
				 * 
				 * */
				
				int nBreaks = uF.parseToInt(hmBreaks.get(empId+"_-2"));
//				double dblAmt = uF.parseToDouble(hmBreakPolicy.get("-2_"+getF_org()));
//				dblTotalPresentDays -= (nBreaks * dblAmt); 
//				if(nBreaks>0){
//					hmPresentDays1.put(strEmpId, (dblTotalPresentDays)+"");
//				}
				
//				System.out.println("startArrearDate=======>"+startArrearDate);
//				System.out.println("endArrearDate=======>"+endArrearDate);
				double nTotalPresentDays=0;
				int nTotalNumberOfDaysForCalc=0;
				pst=con.prepareStatement("select sum(paid_days) as paid_days,sum(total_days) as total_days from payroll_generation " +
						"where emp_id =? and paid_from>=? and paid_to<=? and generation_id in (select max(generation_id) as generation_id " +
						" from payroll_generation where emp_id=? and paid_from>=? and paid_to<=? group by paycycle,total_days)");
				pst.setInt(1, uF.parseToInt(empId));
				pst.setDate(2, uF.getDateFormat(startArrearDate, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(endArrearDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(empId));
				pst.setDate(5, uF.getDateFormat(startArrearDate, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(endArrearDate, DATE_FORMAT));
//				System.out.println("pst=======>"+pst);
				rs=pst.executeQuery();
				while(rs.next()){
					nTotalPresentDays=rs.getDouble("paid_days");
					nTotalNumberOfDaysForCalc=rs.getInt("total_days");
				}
				rs.close();
				pst.close();
				
				hmTotalDays.put(empId, ""+nTotalPresentDays);
//				System.out.println("empId=====>"+empId+"=======nTotalPresentDays=======>"+nTotalPresentDays+"====nTotalNumberOfDaysForCalc====>"+nTotalNumberOfDaysForCalc);
				int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
				
				double dblIncrementBasic = getIncrementCalculationBasic(con, uF, empId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
				double dblIncrementDA = getIncrementCalculationDA(con, uF, empId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
				
				
				Map<String, Map<String, String>> hmArrearSalaryHeadMap=CF.getSalaryCalculationforArrears(con, empId,nTotalPresentDays, hmEmpLevelMap.get(empId), uF, CF, startArrearDate,endArrearDate,nTotalNumberOfDaysForCalc,nBreaks, dblIncrementBasic, dblIncrementDA,request,monthCount);
				hmTotalSalary.put(empId, hmArrearSalaryHeadMap);
				
				
				Iterator it=hmArrearSalaryHeadMap.keySet().iterator();
				double dblGross=0;
				double dblNet=0;
				double dblDeduction=0;
				while(it.hasNext()){
					String salaryHeadId=(String)it.next();
					Map<String,String> hmInner=hmArrearSalaryHeadMap.get(salaryHeadId);
					if(hmInner==null) hmInner=new HashMap<String, String>();
					
					if(hmInner.get("EARNING_DEDUCTION")!=null && hmInner.get("EARNING_DEDUCTION").equalsIgnoreCase("E")){
						int index = alEarningSalaryDuplicationTracer.indexOf(salaryHeadId);
						
						if(index>=0){
							alEmpSalaryDetailsEarning.remove(index);
							alEarningSalaryDuplicationTracer.remove(index);
							alEmpSalaryDetailsEarning.add(salaryHeadId);
						}else{
							alEmpSalaryDetailsEarning.add(salaryHeadId);
						}
						dblGross+=uF.parseToDouble(hmInner.get("AMOUNT"));
						alEarningSalaryDuplicationTracer.add(salaryHeadId);
					}else if(hmInner.get("EARNING_DEDUCTION")!=null && hmInner.get("EARNING_DEDUCTION").equalsIgnoreCase("D")){
						int index = alDeductionSalaryDuplicationTracer.indexOf(salaryHeadId);
						if(index>=0){
							alEmpSalaryDetailsDeduction.remove(index);
							alDeductionSalaryDuplicationTracer.remove(index);
							alEmpSalaryDetailsDeduction.add(salaryHeadId);
						}else{
							alEmpSalaryDetailsDeduction.add(salaryHeadId);
						}
						alDeductionSalaryDuplicationTracer.add(salaryHeadId);
						dblDeduction+=uF.parseToDouble(hmInner.get("AMOUNT"));
					}
					
				}
				
				dblNet=dblGross-dblDeduction;
				
				hmGrossNet.put(empId+"_GROSS", ""+dblGross);
				hmGrossNet.put(empId+"_NET", ""+dblNet);
				
			}
			
			
			List alEmpIdPayrollG = new ArrayList();
			pst = con.prepareStatement("select distinct(emp_id) from arrear_generation where paycycle=? and salary_head_id not in ("+BONUS+") ");
			pst.setInt(1, uF.parseToInt(strPC));
			
			rs = pst.executeQuery();
			while(rs.next()){
				String strEmpId = rs.getString("emp_id");
				alEmpIdPayrollG.add(strEmpId);
			}
			rs.close();
			pst.close();
			
			
			
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			request.setAttribute("hmTotalSalary", hmTotalSalary);
			request.setAttribute("hmEmpNameMap", hmEmpNameMap);
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
			request.setAttribute("hmEmpPaymentMode", hmEmpPaymentMode);
			request.setAttribute("hmPaymentModeMap", hmPaymentModeMap);
			request.setAttribute("hmTotalDays", hmTotalDays);
			request.setAttribute("hmGrossNet", hmGrossNet);
			request.setAttribute("alEmpIdPayrollG", alEmpIdPayrollG);
			request.setAttribute("hmEmpArrearDate", hmEmpArrearDate);
			request.setAttribute("hmVariables", hmVariables);
			request.setAttribute("hmEmpWlocationMap", hmEmpWlocationMap);
			request.setAttribute("hmEmpStateMap", hmEmpStateMap);
			request.setAttribute("hmEmpMertoMap", hmEmpMertoMap);
			request.setAttribute("hmWlocationMap", hmWlocationMap);
			request.setAttribute("hmOtherTaxDetails", hmOtherTaxDetails);
			request.setAttribute("hmEmpLevelMap", hmEmpLevelMap);
			
			
			session.setAttribute("AR_hmSalaryDetails", hmSalaryDetails);
			session.setAttribute("AR_alEmp", alEmp);
			session.setAttribute("AR_alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			session.setAttribute("AR_alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
			session.setAttribute("AR_hmTotalSalary", hmTotalSalary);
			session.setAttribute("AR_hmEmpNameMap", hmEmpNameMap);
			session.setAttribute("AR_hmEmpCodeMap", hmEmpCodeMap);
			session.setAttribute("AR_hmEmpPaymentMode", hmEmpPaymentMode);
			session.setAttribute("AR_hmPaymentModeMap", hmPaymentModeMap);
			session.setAttribute("AR_hmTotalDays", hmTotalDays);
			session.setAttribute("AR_hmGrossNet", hmGrossNet);
			session.setAttribute("AR_alEmpIdPayrollG", alEmpIdPayrollG);
			session.setAttribute("AR_hmEmpArrearDate", hmEmpArrearDate);
			session.setAttribute("AR_hmEmpStateMap", hmEmpStateMap);
			session.setAttribute("AR_hmEmpMertoMap", hmEmpMertoMap);
			session.setAttribute("AR_hmWlocationMap", hmWlocationMap);
			session.setAttribute("AR_hmOtherTaxDetails", hmOtherTaxDetails);
			session.setAttribute("AR_hmEmpLevelMap", hmEmpLevelMap);
			
//			System.out.println("hmEmpArrearDate=======>"+hmEmpArrearDate);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	
//	public String viewClockEntriesForPayrollArrear(CommonFunctions CF, String strReqEmpId, String strD1, String strD2) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			
//			this.strD1 = strD1;
//			this.strD2 = strD2;
//			
//			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
//			
//			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//			int nTotalNumberOfDays = 0;
//			if(uF.parseToBoolean(CF.getIsPaycycleAdjustment())){
//				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
//				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
//				nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
//			}else{
//				cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//				nTotalNumberOfDays = cal.getActualMaximum(Calendar.DATE);
//			}
//			
//			con = db.makeConnection(con);
//			
//			if(getWLocation()==null && session!=null){
//				setWLocation((String)session.getAttribute(WLOCATIONID));
//			}
//			
//			
//			
//			String strFinancialYearEnd = null;
//			String strFinancialYearStart = null;
//			String []strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
//			if(strFinancialYear!=null){
//				strFinancialYearStart = strFinancialYear[0];
//				strFinancialYearEnd = strFinancialYear[1];
//			}
//			
//			
//			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
//			}
//			
//			double dblInvestmentExemption = 0.0d;
//			double dblStandardHrs = 0.0d;
//			
//			
//			Map hmEmpMertoMap = new HashMap();
//			Map<String, String> hmEmpEndDateMap = new HashMap<String, String>();
//			Map hmWlocationMap = CF.getWorkLocationMap(con);
//			Map hmEmpWlocationMap = new HashMap();
//			Map hmEmpStateMap = new HashMap();
//			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
//			Map hmEmpJoiningMap = CF.getEmpJoiningDateMap(con, uF, hmEmpEndDateMap); 
//			Map hmEmpNameMap = CF.getEmpNameMap(con, null, null); 
//			Map hmEmpCodeMap = CF.getEmpCodeMap(con);
//			Map hmEmpGenderMap = CF.getEmpGenderMap(con);
//			Map hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
//			Map hmEmpExemptionsMap = getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//			Map hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map hmEmpPaidSalary = CF.getPaidSalary(con, strFinancialYearStart, strFinancialYearEnd, strPC);
//			Map hmEmpRentPaidMap = getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd); 
//			Map<String, Map<String, String>> hmOverTimeMap = CF.getOverTimeMap(con, CF);
//			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//
//			Map<String, String> hmLoanPoliciesMap = CF.getLoanPoliciesMap(con, uF, getF_org());
//			Map hmLeaveDays = new HashMap();
//			Map hmLeaveTypeDays = new HashMap();
//			Map<String, String> hmMonthlyLeaves = new HashMap();
//			
//			hmLeaveDays = CF.getLeaveDates(con, strD1, strD2, CF, hmLeaveTypeDays, true, hmMonthlyLeaves);
//			String strTotalDays = nTotalNumberOfDays+"";
//			
//			
//			
//			Map hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF);
//			
//			Map<String, String> hmHolidays = new HashMap<String, String>();
//			Map<String, String> hmHolidayDates = new HashMap<String, String>();
//			CF.getHolidayList(con, strD1, strD2, CF, hmHolidayDates, hmHolidays, hmWeekEnds, true);
//							
//			Map<String, String> hmAttendanceDependent = CF.getAttendanceDependency(con);
//			Map<String, String> hmRosterDependent = CF.getRosterDependency(con);
//			Map hmCurrencyDetails = CF.getCurrencyDetails(con);
//			Map hmEmpCurrency = CF.getEmpCurrency(con);
//			Map hmEmpPaymentMode = CF.getEmpPaymentMode(con, uF);
//			Map hmEmpPaidAmountDetails =  getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map hmFixedExemptions = getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//
//			
//			List<String> alPresentEmpId = new ArrayList<String>();
//			List<String> alPresentDates = new ArrayList<String>();
//			List<String> alPresentWeekEndDates = new ArrayList<String>();
//			List<String> alHalfDaysDueToLatePolicy = new ArrayList<String>();
//			List<String> alServices = new ArrayList<String>();
//			Map<String, List<String>> hmPresentDays = new HashMap<String, List<String>>();
//			Map<String, List<String>> hmPresentWeekEndDays = new HashMap<String, List<String>>();
//			Map<String, List<String>> hmHalfDays = new HashMap<String, List<String>>();
//			Map<String, String> hmPresentDays1 = new HashMap<String, String>();
//			Map<String, String> hmPaidDays = new HashMap<String, String>();
//			Map<String, List<String>> hmServices = new HashMap<String, List<String>>();
//			Map<String, String> hmHoursWorked = new HashMap<String, String>();
//			Map<String, Map<String, String>> hmEmpHoursWorked = new HashMap<String, Map<String, String>>();
//			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
//			
//			Map<String,String> hmEmpRosterHours = new HashMap();
//			Map<String, Map<String,String>> hmEmpOverTimeHours=CF.getEmpOverTimeHours(con,CF,uF,strD1,strD2,strPC);
//			Map<String, Map<String,String>> hmEmpOverTimeLevelPolicy=CF.getEmpOverTimeLevelPolicy(con,CF,uF,strD1,strD2,strPC);
//			
//			List<String> alEmp = new ArrayList<String>();
//			
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and (employment_end_date is null OR employment_end_date >= ?) and joining_date<= ? and emp_per_id > 0 ");
//			
//			if(uF.parseToInt(getLevel())>0){
//				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id = "+uF.parseToInt(getLevel())+")");
//			}
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and depart_id ="+uF.parseToInt(getF_department()));
//			}
//			
//			if(uF.parseToInt(getF_service())>0){
//				sbQuery.append(" and service_id  like '%"+uF.parseToInt(getF_service())+",%'");
//			}
//			
//			if(uF.parseToInt(getWLocation())>0){
//				sbQuery.append(" and wlocation_id ="+uF.parseToInt(getWLocation()));
//			}
//			
//			if(getStrPaycycleDuration()!=null){
//				sbQuery.append(" and paycycle_duration ='"+getStrPaycycleDuration()+"'");
//			}
//			
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and org_id ="+uF.parseToInt(getF_org()));
//			}
//
//			if(uF.parseToInt(getF_paymentMode())>0){
//				sbQuery.append(" and payment_mode ="+uF.parseToInt(getF_paymentMode()));
//			}
//			
//			if((String)session.getAttribute(WLOCATION_ACCESS)!=null){
//				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//			
//			sbQuery.append(" order by emp_fname, emp_lname");
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1,  uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(2,  uF.getDateFormat(strD2, DATE_FORMAT));
//			
//			StringBuilder sbEmPId = new StringBuilder();
//			rs = pst.executeQuery();
//			
//			Map<String, String> hmEmpPaycycleDuration = new HashMap<String, String>();
//			
//			while(rs.next()){
//				alEmp.add(rs.getString("emp_per_id"));
//				sbEmPId.append(rs.getString("emp_per_id")+",");
//				
//				hmEmpPaycycleDuration.put(rs.getString("emp_per_id"), rs.getString("paycycle_duration"));
//				
//			}
//			
//			if(sbEmPId.length()>1){
//				sbEmPId.replace(0, sbEmPId.length(), sbEmPId.substring(0, sbEmPId.length()-1));
//			}
//			
//			if(strReqEmpId!=null){
//				pst = con.prepareStatement("select * from attendance_details, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id =? order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(strReqEmpId));
//			}else if(sbEmPId.length()>1){
//				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and ad.emp_id in ("+sbEmPId.toString()+") order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			}else{
//				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			}
//			
//			Map hmOverLappingHolidays = new HashMap();
//			rs = pst.executeQuery();  
//			double dblOverLappingHolidays = 0;
//			
//			String strPresentEmpIdNew = null;
//			String strPresentEmpIdOld = null;
//			while(rs.next()){
//				
//				strPresentEmpIdNew = rs.getString("emp_id");
//				if(strPresentEmpIdNew!=null && !strPresentEmpIdNew.equalsIgnoreCase(strPresentEmpIdOld)){
//					alPresentEmpId = new ArrayList<String>();
//					alPresentDates = new ArrayList<String>();
//					alPresentWeekEndDates = new ArrayList<String>();
//					alServices = new ArrayList<String>();
//					hmHoursWorked = new HashMap<String, String>();
//					halfDayCountIN = 0;
//					halfDayCountOUT = 0;
//					dblOverLappingHolidays = 0;
//					alHalfDaysDueToLatePolicy = new ArrayList<String>();
//				}
//				
//				if(!alPresentEmpId.contains(strPresentEmpIdNew)){
//					alPresentEmpId.add(strPresentEmpIdNew);
//				}
//				
//				hmEmpRosterHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("emp_id"), rs.getString("actual_hours"));
//				
//				String strDay = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
//				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
//				strDay = strDay.toUpperCase();
//				String strLocation = (String)hmEmpWlocationMap.get(strPresentEmpIdNew);
//				double dblEarlyLate = rs.getDouble("early_late");
//				String strINOUT = rs.getString("in_out");
//				Map hmLeaves = (Map)hmLeaveDays.get(strPresentEmpIdNew);
//				if(hmLeaves==null)hmLeaves = new HashMap();
//				
//				
//				if(!alPresentDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))){
//					/**
//					 * To avoid the over presence data
//					 */
//					String strWeekEnd = (String)hmWeekEnds.get(strDate+"_"+strLocation);
//					
//					if(strWeekEnd==null ){ //&& !hmLeaves.containsKey(strDate)
//						alPresentDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//					}else if(!alPresentWeekEndDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))){
//						alPresentWeekEndDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//					}
//					
//					if(hmHolidayDates.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_"+strLocation)){
//						dblOverLappingHolidays++;
//					}
//					
//				}
//				
//				boolean isRosterDependent = uF.parseToBoolean((String)hmRosterDependent.get(strPresentEmpIdNew));
//				
//				if(isHalfDay(strDate, dblEarlyLate, strINOUT, (String)hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alHalfDaysDueToLatePolicy!=null
//						&& isRosterDependent
//						&& !alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))
//						&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))){
//					alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//				}
//				
//				if(!alServices.contains(rs.getString("service_id"))){  
//					alServices.add(rs.getString("service_id"));
//				} 
//				
//				hmPresentDays.put(strPresentEmpIdNew, alPresentDates);
//				hmPresentWeekEndDays.put(strPresentEmpIdNew, alPresentWeekEndDates);
//				hmHalfDays.put(strPresentEmpIdNew, alHalfDaysDueToLatePolicy);
//				
//				hmServices.put(strPresentEmpIdNew, alServices);
//				
//				if("OUT".equalsIgnoreCase(rs.getString("in_out"))){
//					hmHoursWorked.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id"), rs.getString("hours_worked"));
//				}
//				
//				hmEmpHoursWorked.put(strPresentEmpIdNew, hmHoursWorked);
//				hmOverLappingHolidays.put(strPresentEmpIdNew, dblOverLappingHolidays+"");
//				
//				strPresentEmpIdOld = strPresentEmpIdNew;
//			}
//			
//			log.debug("hmHoursWorked===>"+hmEmpHoursWorked);
//			
//			
//			Map hmEmpSalary = new LinkedHashMap();
//			Map hmEmpSalaryInner = new LinkedHashMap();
//			Map hmSalaryDetails = new HashMap();
//			
//			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
//			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
//
//			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+") and org_id =? order by earning_deduction desc, salary_head_id, weight");
//			pst.setInt(1, uF.parseToInt(getF_org()));
//			
//			rs = pst.executeQuery();  
//			
//			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
//			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
//			
//			while(rs.next()){
//				
//				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
//					int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//					
//					if(index>=0){
//						alEmpSalaryDetailsEarning.remove(index);
//						alEarningSalaryDuplicationTracer.remove(index);
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					}else{
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					}
//					
//					alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
//					int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//					if(index>=0){
//						alEmpSalaryDetailsDeduction.remove(index);
//						alDeductionSalaryDuplicationTracer.remove(index);
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					}else{
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					}
//					alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}
//				
//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
//			}
//			
//			if(uF.parseToInt(getF_org())>0){
//				pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation2); 
//				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setInt(2, uF.parseToInt(getF_org()));
//			}else{
//				pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation1); 
//				pst.setDate(1, uF.getDateFormat(strD2, DATE_FORMAT));	
//			}
//			
//			
//			rs = pst.executeQuery();
//			String strEmpIdNew1 = null;
//			String strEmpIdOld1 = null;
//			while(rs.next()){
//				strEmpIdNew1 = rs.getString("emp_id");
//						
//				if(!alEmp.contains(strEmpIdNew1))continue;		
//						
//						
//				if(strEmpIdNew1!=null && !strEmpIdNew1.equalsIgnoreCase(strEmpIdOld1)){
//					hmEmpSalaryInner = new LinkedHashMap();
//					
//					
//				}
//				
//				Map<String, String> hmEmpInner = new HashMap<String, String>();
//				
//				/*
//				hmEmpInner.put("SALARY_HEAD_ID", rs.getString("salary_id"));
//				hmEmpInner.put("SERVICE_ID", rs.getString("service_id"));
//				hmEmpInner.put("AMOUNT", rs.getString("amount"));
//				hmEmpInner.put("PAY_TYPE", rs.getString("pay_type"));
//				hmEmpInner.put("SALARY_HEAD_NAME", rs.getString("salary_head_name"));
//				hmEmpInner.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
//				hmEmpInner.put("SALARY_HEAD_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
//				hmEmpInner.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
//				hmEmpInner.put("SUB_SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
//				
//				
//				
//				hmEmpSalaryInner.put(rs.getString("salary_id"), hmEmpInner);
////				hmEmpPresentDays.put(strEmpIdNew1, nPresentDays+"");
//				*/
//				
//				if(strEmpIdNew1!=null && strEmpIdNew1.length()>0){
//					hmEmpSalary.put(strEmpIdNew1, hmEmpSalaryInner);
//				}
//				
//				strEmpIdOld1 = strEmpIdNew1;
//
//			}
//
//			Map hmInnerTemp = new HashMap();
//			
//			Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
//			Map<String, String> hmIncentives = CF.getIncentivesMap(con, uF, CF, strPC);
//			Map<String, String> hmIndividualBonus = CF.getIndividualBonusMap(con, uF, CF, strPC);
//			Map<String, String> hmIndividualOtherDeduction = CF.getIndividualOtherDeductionMap(con, uF, CF, strPC);
//			Map<String, String> hmIndividualOtherEarning = CF.getIndividualOtherEarningMap(con, uF, CF, strPC);
//			
//			Map<String, String> hmIndividualOvertime = CF.getIndividualOvertimeMap(con, uF, CF, strPC);			
//			Map<String, String> hmIndividualTravelReimbursement = CF.getIndividualTravelReimbursementMap(con, uF, CF, strPC, strD1, strD2);
//			Map<String, String> hmIndividualMobileReimbursement = CF.getIndividualMobileReimbursementMap(con, uF, CF, strPC, strD1, strD2);
//			Map<String, String> hmIndividualOtherReimbursement = CF.getIndividualOtherReimbursementMap(con, uF, CF, strPC, strD1, strD2);
//			
//			Map<String, String> hmIndividualMobileRecovery = CF.getIndividualMobileRecoveryMap(con, uF, CF, strPC);
//			
//			Map<String, String> hmReimbursement = CF.getReimbursementMap(con, uF, CF, strD1, strD2);
//			Map<String, String> hmVariables  = new HashMap<String, String>();
//			Map<String, String> hmBreaks  = new HashMap<String, String>();
//			Map<String, String> hmBreakPolicy  = new HashMap<String, String>();
//			getVariableAmount(con, uF, hmVariables, strPC);
//			getBreakDetails(con, uF, hmBreaks, hmBreakPolicy, strD1, strD2);
//			
//			Map hmBasicSalaryMap = CF.getSpecificSalaryData(con, BASIC);
//			Map hmDASalaryMap = CF.getSpecificSalaryData(con, DA);
//			Set setLeaves = null;
//			Iterator it = null;
//			
// 			List alProcessingEmployee = new ArrayList();
//			
//			LinkedHashMap hmTotalSalary = new LinkedHashMap();
//			Map<String, String> hmLoanAmt=new HashMap<String, String>();
//			Map<String, Map<String, String>> hmEmpLoan = new HashMap<String, Map<String, String>>();
//			List<String> alLoans = new ArrayList<String>();
//			System.out.println("hmEmpSalary=======>"+hmEmpSalary);
//			Set set0 = hmEmpSalary.keySet();
//			Iterator it0 = set0.iterator();
//			while(it0.hasNext()){
//				String strEmpId = (String)it0.next();
//				int nEmpId = uF.parseToInt(strEmpId);
//				
//				if(!alProcessingEmployee.add(strEmpId)){
//					alProcessingEmployee.add(strEmpId);
//				}
//				
//				log.debug("hmPresentDays====>"+hmPresentDays);
//				
//				List<String> alPresentTemp = (List)hmPresentDays.get(strEmpId);
//				if(alPresentTemp==null)alPresentTemp = new ArrayList<String>();
//				
//				List<String> alHalfDaysDueToLatePolicyTemp = (List)hmHalfDays.get(strEmpId);
//				if(alHalfDaysDueToLatePolicyTemp==null)alHalfDaysDueToLatePolicyTemp = new ArrayList<String>();
//				
//				List<String> alServiceTemp = (List)hmServices.get(strEmpId);
//				if(alServiceTemp==null)alServiceTemp = new ArrayList<String>();
//				
//				double  dblPresent = alPresentTemp.size();
//				
//				
//				dblPresent -=alHalfDaysDueToLatePolicyTemp.size() * 0.5;
//				
//				Map hmLeaves = (Map)hmLeaveDays.get(strEmpId);
//				if(hmLeaves==null)hmLeaves = new HashMap();
//				
//				Map hmLeavesType = (Map)hmLeaveTypeDays.get(strEmpId);
//				if(hmLeavesType==null)hmLeavesType = new HashMap();
//
//
//				EmployeeActivity obj = new EmployeeActivity();
//				obj.setServletRequest(request);
//				
//				setLeaves = hmLeaves.keySet();
//				it = setLeaves.iterator();
//				double nOverlappingHolidaysLeaves = 0;
//				double nOverlappingWeekEndsLeaves = 0;
//				while(it.hasNext()){
//					String strLeaveDate = (String)it.next();
//					String strHolidayDate = (String)hmHolidayDates.get(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+(String)hmEmpWlocationMap.get(strEmpId));
//					
//					String strWeekEnd = (String)hmWeekEnds.get(strLeaveDate+"_"+(String)hmEmpWlocationMap.get(strEmpId));
//					
//					String strLeaveType = (String)hmLeavesType.get(strLeaveDate);
//					
//					if(strLeaveDate!=null && strHolidayDate!=null && "H".equalsIgnoreCase(strLeaveType)){
//						nOverlappingHolidaysLeaves+=0.5;
//					}else if(strLeaveDate!=null && strHolidayDate!=null){
//						nOverlappingHolidaysLeaves++;
//					}
//					
//					if(strLeaveDate!=null && strWeekEnd!=null){
//						nOverlappingWeekEndsLeaves++;
//					}
//					
//					  
//					
//					if(strLeaveDate!=null && alPresentTemp.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)){
//						dblPresent += -1 + 0.5;
//					}
//					
//					
//				}
//				
//				
//				
//				int nHolidays = uF.parseToInt(hmHolidays.get((String)hmEmpWlocationMap.get(strEmpId)));
//				int nWeekEnds = 0;
//				
//				if(hmEmpEndDateMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT))){
//					Map hmWeekEnds1 = CF.getWeekEndDateList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, uF);
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds1,(String)hmEmpWlocationMap.get(strEmpId), strD1, hmEmpEndDateMap.get(strEmpId));
//					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
//					nHolidays = uF.parseToInt(hmHolidays1.get((String)hmEmpWlocationMap.get(strEmpId)));
//					
//				}else if(hmEmpJoiningMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil((String)hmEmpJoiningMap.get(strEmpId), DATE_FORMAT))){
//					Map hmWeekEnds1 = CF.getWeekEndDateList(con, (String)hmEmpJoiningMap.get(strEmpId), strD2, CF, uF);
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds1,(String)hmEmpWlocationMap.get(strEmpId), (String)hmEmpJoiningMap.get(strEmpId), strD2);
//					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con, (String)hmEmpJoiningMap.get(strEmpId), strD2, CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
//					nHolidays = uF.parseToInt(hmHolidays1.get((String)hmEmpWlocationMap.get(strEmpId)));
//					
//				}else{
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,(String)hmEmpWlocationMap.get(strEmpId), strD1, strD2);					
//				}
//				
//				
//				List alWorkingWeekEnds = (List)hmPresentWeekEndDays.get(strEmpId);
//				if(alWorkingWeekEnds==null)alWorkingWeekEnds=new ArrayList();
//				int nWorkingWeekEnds = alWorkingWeekEnds.size();
//				
//				
//				List alOverlappingWeekEndDates = (List)hmPresentWeekEndDays.get(strEmpId);
//				if(alOverlappingWeekEndDates==null)alOverlappingWeekEndDates = new ArrayList();
//				int nOverlappingWeekends = alOverlappingWeekEndDates.size();
//				
//				
//				
//				
//				double dblOverlappingHolidays = uF.parseToDouble((String)hmOverLappingHolidays.get(strEmpId));
//				
//				double dblTotalLeaves =  uF.parseToDouble((String)hmLeavesType.get("COUNT"));
//				double dblActualLeaves =  dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//				
//				double dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves)+"");
//				                              
//				
//				if(dblTotalPresentDays>nTotalNumberOfDays){
//					dblTotalPresentDays = nTotalNumberOfDays;
//					
//				}
//				
//
//				// For ANC all daily employees are calculated Overtime differently
//				if(hmEmpPaycycleDuration.get(strEmpId)!=null && !hmEmpPaycycleDuration.get(strEmpId).equalsIgnoreCase("M")){
//					dblTotalPresentDays = dblPresent + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
//				} 
//				
//				if(uF.parseToBoolean(CF.getIsPaycycleAdjustment())){
//					Calendar calMonth1 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//					calMonth1.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
//					calMonth1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
//					calMonth1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//					
//					Calendar calMonth2 = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//					calMonth2.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "dd")));
//					calMonth2.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"))-1);
//					calMonth2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "yyyy")));
//					
//					int nMonth1 = calMonth1.getActualMaximum(Calendar.DATE);
//					int nMonth2 = calMonth2.getActualMaximum(Calendar.DATE); 
//					
//					dblPresent += (nMonth2 - nMonth1);
//					
//					
//					dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + nWeekEnds + dblTotalLeaves - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds - nOverlappingHolidaysLeaves - nOverlappingWeekEndsLeaves)+"");
//				}
//				
//				
//				
//				int nTotalNumberOfDaysForCalc = nTotalNumberOfDays; 
//				
//				/**   AWD  = Actual Working Days
//				 * */
//				
//				if("AWD".equalsIgnoreCase(CF.getStrOSalaryCalculationType())){
//					dblTotalPresentDays = dblPresent + dblTotalLeaves;
//					
//					if(dblPresent>0){
//						dblPresent = dblPresent + nHolidays ;
//					}else{
//						dblPresent = dblPresent ;
//					}
//					
//					dblTotalPresentDays = dblPresent+ dblTotalLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent)+"");
//					
//					int nWeekEnds1 = uF.getWeekEndDateCount(hmWeekEnds,(String)hmEmpWlocationMap.get(strEmpId), strD1, strD2);
//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nWeekEnds1; 					
//					
//					strTotalDays = nTotalNumberOfDaysForCalc +"";
//					
//				}
//				
//				log.debug(" strEmpId= 123 =>"+strEmpId);
//				log.debug(" nHolidays==>"+nHolidays);
//				log.debug(" hmHolidays==>"+hmHolidays);
//				log.debug(" (String)hmEmpWlocationMap.get(strEmpId)==>"+(String)hmEmpWlocationMap.get(strEmpId));
//				
//				
//				
//				/**
//				 * 
//				 * Prem Motors Break Policies
//				 * 
//				 * */
//				
//				int nBreaks = uF.parseToInt(hmBreaks.get(strEmpId+"_-2"));
//				
//				
//				/**
//				 *   The attendance dependency calculation is for those employees who are not 
//				 *   attendance dependent and will get the full salary irrespective they clocking on.
//				 */
//				
//				boolean isAttendance = uF.parseToBoolean((String)hmAttendanceDependent.get(strEmpId));
//				if(!isAttendance){
//					dblTotalPresentDays = nTotalNumberOfDaysForCalc;
//				}
//				
//				hmPaidDays.put(strEmpId, dblTotalPresentDays+"");
//				
//				double dblIncrementBasic = getIncrementCalculationBasic(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
//				double dblIncrementDA = getIncrementCalculationDA(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
//				
//				
//				
//				Map hmInner = new LinkedHashMap();
//				
//				if((dblIncrementBasic>0 || dblIncrementDA>0) && getApprovePC()!=null){
//					hmInner = CF.getSalaryCalculation(con, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, (String)hmEmpLevelMap.get(strEmpId), uF, CF, strD2);
//					obj.processActivity(1, uF.parseToInt(strEmpId), uF.getDateFormat(strD2, DATE_FORMAT, DBDATE), CF);
//					obj.insertEmpActivity(uF, 1, strEmpId);
//				}else{  
//					hmInner = CF.getSalaryCalculation(con, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, (String)hmEmpLevelMap.get(strEmpId), uF, CF, strD2);
//				}
//				
//				
//				Map hmInnerActualCTC = CF.getSalaryCalculation(con, nEmpId, nTotalNumberOfDaysForCalc, 0, 0, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, (String)hmEmpLevelMap.get(strEmpId), uF, CF, strD2);
//				
//				
//				
//				Map hmPaidSalaryInner = (Map)hmEmpPaidSalary.get(strEmpId);
//				
//				
//				hmHoursWorked = hmEmpHoursWorked.get(strEmpId);
//				if(hmHoursWorked==null)hmHoursWorked = new HashMap<String, String>();
//				Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
//				
//				
//				if(hmIndividualOtherEarning.size()>0 && !hmInner.containsKey(OTHER_EARNING+"")){
//					hmInnerTemp = new HashMap();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInner.put(OTHER_EARNING+"", hmInnerTemp);
//				}
//				
//				if(hmIndividualOtherReimbursement.size()>0 && !hmInner.containsKey(OTHER_REIMBURSEMENT+"")){
//					hmInnerTemp = new HashMap();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInner.put(OTHER_REIMBURSEMENT+"", hmInnerTemp);
//				}
//				if(hmEmpServiceTaxMap.size()>0 && !hmInner.containsKey(SERVICE_TAX+"")){
//					hmInnerTemp = new HashMap();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "E");
//					hmInner.put(SERVICE_TAX+"", hmInnerTemp);					
//				}
//				
//				if(hmIndividualOtherDeduction.size()>0 && !hmInner.containsKey(OTHER_DEDUCTION+"")){
//					hmInnerTemp = new HashMap();
//					hmInnerTemp.put("AMOUNT", "0");
//					hmInnerTemp.put("EARNING_DEDUCTION", "D");
//					hmInner.put(OTHER_DEDUCTION+"", hmInnerTemp);
//				}
//				
//				if(hmInner.size()>0 && hmInner.containsKey(TDS+"")){
//					hmInnerTemp = new HashMap();
//					hmInnerTemp=(Map)hmInner.get(TDS+"");
//					hmInnerTemp.put("AMOUNT", (String)hmInnerTemp.get("AMOUNT"));
//					hmInnerTemp.put("EARNING_DEDUCTION", "D");
//					hmInner.remove(TDS+"");
//					hmInner.put(TDS+"", hmInnerTemp);
//				}
//				
//				
//				log.debug(strEmpId+" hmPaidSalaryInner==>"+hmPaidSalaryInner);
//				log.debug("hmEmpPaidSalary==>"+hmEmpPaidSalary);
//				log.debug("hmInner==>"+hmInner);
//				log.debug("strEmpId==>"+strEmpId);
//				log.debug("dblPresent==>"+dblTotalPresentDays);
//				log.debug("nTotalNumberOfDays==>"+nTotalNumberOfDaysForCalc);
//				
//				
//				Map hm=new HashMap();
//				Map<String, String> hmTotal=new HashMap<String, String>();
//				
//				
//				Set set1 = hmInner.keySet();
//				Iterator it1 = set1.iterator();
//				
//				double dblGrossPT = 0;
//				double dblGross = 0;
//				double dblGrossTDS = 0;
//				double dblDeduction = 0;
//				boolean isDefinedEarningDeduction = false; 
//				
//				while(it1.hasNext()){
//					String strSalaryId = (String)it1.next();
//					int nSalayHead = uF.parseToInt(strSalaryId);
//					
//					hm = (Map)hmInner.get(strSalaryId);
//					if(hm==null){  
//						hm = new HashMap();
//					}
//					isDefinedEarningDeduction = false;
//					String str_E_OR_D = (String)hm.get("EARNING_DEDUCTION");
//					if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E")){
//
//						if(hmPaidSalaryInner!=null){
//							dblGross += uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//							dblGrossPT += uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//							dblGrossTDS += uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//							hmTotal.put(strSalaryId, (String)hmPaidSalaryInner.get(strSalaryId));
//						}else{
//							
//							log.debug("SALARY HEAD===>"+nSalayHead);
//							
//							
//							switch(nSalayHead){
//							/**********  OVER TIME   *************/
////								case OVER_TIME:  
////								 
////									isDefinedEarningDeduction = true;
//////									double dblOverTime = getOverTimeCalculationL(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC, hmOverTimeMap, hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime);
////									double dblOverTime = getOverTimeCalculationHours(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC,  hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime,hmEmpOverTimeHours,hmEmpOverTimeLevelPolicy, hmEmpRosterHours, hmWlocationMap, nTotalNumberOfDays, nWeekEnds,nHolidays,dblPresent,dblTotalPresentDays,hmHolidayDates,hmWeekEnds,hmEmpWlocationMap);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblOverTime)));
////									dblOverTime = Math.round(dblOverTime);
////									dblGross += dblOverTime;
////									dblGrossTDS += dblOverTime;
////									
////								break;
//								
//								case BONUS:
//									// Bonus is paid independent of paycycle -- 
//									
//									if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())){
//										isDefinedEarningDeduction = true;
//										double dblBonusAmount = getBonusCalculation(con, uF, strEmpId, hmEmpLevelMap, hmInnerActualCTC, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap, CF, hmIndividualBonus);
//										hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblBonusAmount)));
//										dblBonusAmount = Math.round(dblBonusAmount);
//										dblGross += dblBonusAmount;
//										dblGrossTDS += dblBonusAmount;
//									}
//									
//								break;
//								
////								case AREARS:
////
////									isDefinedEarningDeduction = true;
////									double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblArearAmount)));
////									dblArearAmount = Math.round(dblArearAmount);
////									dblGross += dblArearAmount;
////									dblGrossTDS += dblArearAmount;
////									
////								break;
//								
////								case INCENTIVES:
////									isDefinedEarningDeduction = true;
////									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIncentiveAmount)));
////									dblIncentiveAmount = Math.round(dblIncentiveAmount);
////									dblGross += dblIncentiveAmount;
////									dblGrossTDS += dblIncentiveAmount;
////								break;
////								
////								case REIMBURSEMENT:
////									isDefinedEarningDeduction = true;
////									double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblReimbursementAmount)));
////									dblReimbursementAmount = Math.round(dblReimbursementAmount);
////									dblGross += dblReimbursementAmount;
////								break;
////								
////								case TRAVEL_REIMBURSEMENT:
////									isDefinedEarningDeduction = true;
////									double dblTravelReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblTravelReimbursementAmount)));
////									dblTravelReimbursementAmount = Math.round(dblTravelReimbursementAmount);
////									dblGross += dblTravelReimbursementAmount;
////								break;
////								
////								case MOBILE_REIMBURSEMENT:
////									isDefinedEarningDeduction = true;
////									double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblMobileReimbursementAmount)));
////									dblMobileReimbursementAmount = Math.round(dblMobileReimbursementAmount);
////									dblGross += dblMobileReimbursementAmount;
////								break;
////								
////								case OTHER_REIMBURSEMENT:
////									isDefinedEarningDeduction = true;
////									double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblOtherReimbursementAmount)));
////									dblOtherReimbursementAmount = Math.round(dblOtherReimbursementAmount);
////									dblGross += dblOtherReimbursementAmount;
////								break;
////								
////								case OTHER_EARNING:
////									isDefinedEarningDeduction = true;
////									double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblOtherEarningAmount)));
////									dblOtherEarningAmount = Math.round(dblOtherEarningAmount);
////									dblGross += dblOtherEarningAmount;
////								break;
////								
////								case SERVICE_TAX:
////									
////									isDefinedEarningDeduction = true;
////									double dblServiceTaxAmount = calculateServiceTax(con, uF, strEmpId, dblGross, (String)hmEmpStateMap.get(strEmpId), hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmOtherTaxDetails, hmEmpServiceTaxMap, CF);
////									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblServiceTaxAmount)));
////									dblServiceTaxAmount = Math.round(dblServiceTaxAmount);
////									dblGross += dblServiceTaxAmount;
////									dblGrossPT += dblServiceTaxAmount;
////									dblGrossTDS += dblServiceTaxAmount;
////									
////									break;	
//								
//								default:
//									if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")){
//										hmTotal.put(strSalaryId, ""+Math.round(uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E"))));
//										dblGross += Math.round(uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E")));
//										dblGrossPT += Math.round(uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E")));
//										dblGrossTDS += Math.round(uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E")));
//									}else if(uF.parseToInt(strSalaryId)!=GROSS){
//										hmTotal.put(strSalaryId, Math.round(uF.parseToDouble((String)hm.get("AMOUNT")))+""); 
//										dblGross += Math.round(uF.parseToDouble((String)hm.get("AMOUNT")));
//										dblGrossPT += Math.round(uF.parseToDouble((String)hm.get("AMOUNT")));
//										dblGrossTDS += Math.round(uF.parseToDouble((String)hm.get("AMOUNT")));;	
//									}
//									
//								break;
//							}
//							
//						}
//						
//					}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")){
//						
//						
//						/**
//						 * 			TAX CALCULATION STARTS HERE
//						 * 
//						 * */
//						
//						switch(nSalayHead){
//						/**********  	 TAX   *************/
////						case PROFESSIONAL_TAX:
////							isDefinedEarningDeduction = true;
////							if(hmPaidSalaryInner!=null){
////								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
////								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblPt)));
////								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
////							}else{
////								
////								
////								/**
////								 * KP Condition
////								 * 
////								 * */ 
////								     
//////								double dblPt = calculateProfessionalTax(con, uF, dblGrossPT, strFinancialYearEnd, nPayMonth, (String)hmEmpStateMap.get(strEmpId));
////							 	double dblPt = calculateProfessionalTax(con, uF, dblGross, strFinancialYearEnd, nPayMonth, (String)hmEmpStateMap.get(strEmpId));
////								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblPt)));
////								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
////							}
////							
////							break;
//						/**********  EPF EMPLOYEE CONTRIBUTION   *************/
//						case EMPLOYEE_EPF:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblPt)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								Map hmVoluntaryPF = (Map)hmInner.get(VOLUNTARY_EPF+"");
//								
//								double dblEEPF = calculateEEPF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblEEPF)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblEEPF)));
//							}
//							
//							break;
//							
//						/**********  EPF EMPLOYER CONTRIBUTION   *************/
//						case EMPLOYER_EPF:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblPt)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblERPF = calculateERPF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblERPF)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblERPF)));
//							}
//							
//							break;
//							
//						
//						/**********  ESI EMPLOYER CONTRIBUTION   *************/
//						case EMPLOYER_ESI:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblPt)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, (String)hmEmpStateMap.get(strEmpId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblESI)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblESI)));
//							}
//							
//							break;
//							
//							
//							/**********  /ESI EMPLOYER CONTRIBUTION   *************/
//							
//							/**********  ESI EMPLOYEE CONTRIBUTION   *************/
//						case EMPLOYEE_ESI:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.ceil(dblPt)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.ceil(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, (String)hmEmpStateMap.get(strEmpId), hmVariables, strEmpId);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.ceil(dblESI)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.ceil(dblESI)));
//							}
//							
//							break;
//							
//							
//							/**********  /ESI EMPLOYEE CONTRIBUTION   *************/	
//							
//							/**********  LWF EMPLOYER CONTRIBUTION   *************/
//						case EMPLOYER_LWF:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblLWF = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, (String)hmEmpStateMap.get(strEmpId), nPayMonth);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//							}
//							
//							break;
//							
//							
//							/**********  /LWF EMPLOYER CONTRIBUTION   *************/
//							
//							/**********  LWF EMPLOYEE CONTRIBUTION   *************/
//						case EMPLOYEE_LWF:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblLWF = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, (String)hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//							}
//							
//							break;
//							
//							/**********  /LWF EMPLOYEE CONTRIBUTION   *************/	
//							
//						case LOAN:
//							
////							System.out.println("Calculating Loan===>");
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblLoan = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblLoan)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLoan)));
//								
//								CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2);
//							}else{
//								
//								double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblLoanAmt)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLoanAmt)));
//								
//								
//								if(true){
//									//dblGrossTDS = dblGross - dblLoanAmt;
//									dblGrossTDS = dblGrossTDS - dblLoanAmt;
//								}
//							}
//							
//							break;
//							
//						case OTHER_DEDUCTION:
//							 
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblIndividualOtherDeductionAmt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblIndividualOtherDeductionAmt)));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIndividualOtherDeductionAmt)));
//							}else{
//								double dblIndividualOtherDeductionAmt = getIndividualOtherDeductionCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherDeduction, CF);
//								dblDeduction += Math.round(dblIndividualOtherDeductionAmt);
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIndividualOtherDeductionAmt)));
//							}
//							
//							break;	
//							
//						
//								
//							
//							
////						case MOBILE_RECOVERY:
////							
////							isDefinedEarningDeduction = true; 
////							if(hmPaidSalaryInner!=null){
////								double dblLoan = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
////								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblLoan)));
////								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLoan)));
////							}else{
////								double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
////								dblDeduction += Math.round(dblIndividualMobileRecoveryAmt);
////								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIndividualMobileRecoveryAmt)));
////							}
////							
////							break;		
//							
//						/**********  TDS   *************/
//						case TDS:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblTDS = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += dblTDS;
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblTDS)));
//							}else{
//
//								double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//								double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//								double dblHRA = uF.parseToDouble((String)hmTotal.get(HRA+""));
//								
//								
//								
//								log.debug(HRA+" hmTotal===>"+hmTotal);
//								
//								log.debug("dblHRA===>"+dblHRA);
//								log.debug("dblBasic===>"+dblBasic);
//								log.debug("dblDA===>"+dblDA);
//								
//								Map hmPaidSalaryDetails =  (Map)hmEmpPaidAmountDetails.get(strEmpId);
//								if(hmPaidSalaryDetails==null){hmPaidSalaryDetails=new HashMap();}
//								
//								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
//								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
//								
//								if(hmEmpServiceTaxMap.containsKey(strEmpId)){
//									dblGrossTDS = dblGross;
//								}
//								
//								double dblTDS = calculateTDS(con, uF, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, (dblBasic + dblDA),
//										nPayMonth,
//										strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId),  (String)hmEmpAgeMap.get(strEmpId), (String)hmEmpStateMap.get(strEmpId),
//										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//										hmTotal, hmSalaryDetails, hmEmpLevelMap, CF);
//								
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTDS)));
//								
//								log.debug("dblTDS==>=>"+dblTDS);
//								
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblTDS)));
//							}
//							break;
//							
//						default:
//							if(hmPaidSalaryInner!=null){
//								dblDeduction += uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								hmTotal.put(strSalaryId, (String)hmPaidSalaryInner.get(strSalaryId));
//							}else{
//								
//								if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_D")){
//									hmTotal.put(strSalaryId, (String)hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
//									dblDeduction += uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
//								}else{
//									hmTotal.put(strSalaryId, (String)hm.get("AMOUNT"));
//									dblDeduction += uF.parseToDouble((String)hm.get("AMOUNT"));	
//								}	
//								
//								
//							}
//							
//							
//							break;
//						}
//					}
//					
//					hmTotal.put("SALARY_HEAD_ID", strSalaryId);
//					hmTotal.put("GROSS", uF.formatIntoTwoDecimal(Math.round(dblGross)));
//				}
//				
//				
//				
//				String strCurrencyId = (String)hmEmpCurrency.get(strEmpId);
//				Map hmCurrency = (Map)hmCurrencyDetails.get(strCurrencyId);
//				if(hmCurrency==null)hmCurrency = new HashMap();
//				
//				hmTotal.put("NET", uF.formatIntoTwoDecimal(Math.round(dblGross - dblDeduction))); 
//				
//				 
//				hmTotalSalary.put(strEmpId, hmTotal);
//			}
//
//			
//			List alEmpIdPayrollG = new ArrayList();
//			pst = con.prepareStatement("select distinct(emp_id) from payroll_generation where paycycle=? and salary_head_id not in ("+BONUS+") ");
//			pst.setInt(1, uF.parseToInt(strPC));
//			
//			rs = pst.executeQuery();
//			while(rs.next()){
//				String strEmpId = rs.getString("emp_id");
//				alEmpIdPayrollG.add(strEmpId);
//			}
//			
//			
//			log.debug("alEmpIdPayrollG===> "+alEmpIdPayrollG);
//			log.debug("hmServices===> "+hmServices);
//			
//			
//			
//			request.setAttribute("hmTotalSalary", hmTotalSalary);
//			request.setAttribute("hmEmpNameMap", hmEmpNameMap);
//			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
//			request.setAttribute("hmLoanPoliciesMap", hmLoanPoliciesMap);
//			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
//			request.setAttribute("hmEmpSalary", hmEmpSalary);
//			request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
//			request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
//			request.setAttribute("alEmpIdPayrollG", alEmpIdPayrollG);
//			request.setAttribute("hmServices", hmServices);
//			request.setAttribute("alEmp", alEmp);
//			request.setAttribute("alProcessingEmployee", alProcessingEmployee);
//			
//			request.setAttribute("hmPaidDays", hmPaidDays);
//			request.setAttribute("hmPresentDays", hmPresentDays1);
//			request.setAttribute("hmLeaveDays", hmLeaveDays);
//			request.setAttribute("hmLeaveTypeDays", hmLeaveTypeDays);
//			request.setAttribute("hmMonthlyLeaves", hmMonthlyLeaves);
//			
//			request.setAttribute("strTotalDays", strTotalDays);
//			request.setAttribute("hmEmpPaymentMode", hmEmpPaymentMode);
//			request.setAttribute("hmPaymentModeMap", hmPaymentModeMap);
//			request.setAttribute("hmLoanAmt", hmLoanAmt);
//			request.setAttribute("hmEmpStateMap", hmEmpStateMap);
//			request.setAttribute("hmVariables", hmVariables);
//			
//			
//			
//			request.setAttribute("strD1", strD1);
//			request.setAttribute("strD2", strD2);
//			
//			session.setAttribute("AP_hmTotalSalary", hmTotalSalary);
//			session.setAttribute("AP_hmEmpNameMap", hmEmpNameMap);
//			session.setAttribute("AP_hmEmpCodeMap", hmEmpCodeMap);
//			session.setAttribute("AP_hmLoanPoliciesMap", hmLoanPoliciesMap);
//			session.setAttribute("AP_hmSalaryDetails", hmSalaryDetails);
//			session.setAttribute("AP_hmEmpSalary", hmEmpSalary);
//			session.setAttribute("AP_alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
//			session.setAttribute("AP_alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
//			session.setAttribute("AP_alEmpIdPayrollG", alEmpIdPayrollG);
//			session.setAttribute("AP_hmServices", hmServices);
//			session.setAttribute("AP_alEmp", alEmp);
//			session.setAttribute("AP_alProcessingEmployee", alProcessingEmployee);
//			
//			session.setAttribute("AP_hmPaidDays", hmPaidDays);
//			session.setAttribute("AP_hmPresentDays", hmPresentDays1);
//			session.setAttribute("AP_hmLeaveDays", hmLeaveDays);
//			session.setAttribute("AP_hmLeaveTypeDays", hmLeaveTypeDays);
//			session.setAttribute("AP_hmMonthlyLeaves", hmMonthlyLeaves);
//			
//			session.setAttribute("AP_strTotalDays", strTotalDays);
//			session.setAttribute("AP_hmEmpPaymentMode", hmEmpPaymentMode);
//			session.setAttribute("AP_hmPaymentModeMap", hmPaymentModeMap);
//			session.setAttribute("AP_hmLoanAmt", hmLoanAmt);
//			session.setAttribute("AP_hmEmpStateMap", hmEmpStateMap);
//			session.setAttribute("AP_hmVariables", hmVariables);
//			
//			session.setAttribute("AP_strD1", strD1);
//			session.setAttribute("AP_strD2", strD2);
//			
//
//			session.setAttribute("AP_hmOtherTaxDetails", hmOtherTaxDetails);
//			
//			session.setAttribute("AP_hmEmpLevelMap", hmEmpLevelMap);
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//	db.closeConnection(con);
//		}
//		return SUCCESS;
//
//	}


	private double getOverTimeCalculationHours(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List alServices, 
			List alPresentDays, List alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, 
			Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays, 
			int nTotalNumberOfDaysForCalc, Map<String, String> hmIndividualOvertime,Map<String, Map<String, String>> hmEmpOverTimeHours,Map<String, Map<String, String>> hmEmpOverTimeLevelPolicy, Map<String, String> hmEmpRosterHours, Map<String, String> hmWlocationMap, int nTotalNumberOfDays, int nWeekEnds,int nHolidays,double dblPresent,double dblTotalPresentDays,Map<String, String> hmHolidayDates,Map hmWeekEnds,Map hmEmpWlocationMap) {
		
		double dblTotalOverTimeAmount = 0.0d;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map hmFixedExemptions = new HashMap();
		
		try {
			
			double dblOTHoursWorked = 0;
			double dblAdditionalHoursWorked = 0;
			String strLevelId = hmEmpLevelMap.get(strEmpId);
			String strOverTimeType = null;
			double dblTotalOverTime = 0;
			double dblTotalHoursWorked = 0;
			double dblOverTimeCalcHours = 0;
			double dblOverTimeCalcDays = 0;
			
			double dblOvertimeFixedAmount = 0;
			
			
			Map<String,String> hmOvertimePolicy=new HashMap<String, String>();
			
			Map<String,String> hmEmpOvertime=hmEmpOverTimeHours.get(strEmpId);
			if(hmEmpOvertime==null) hmEmpOvertime=new HashMap<String, String>();
			Iterator<String> it=hmEmpOvertime.keySet().iterator();
			
			while(it.hasNext()){
				String strDate = it.next();
				double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
				
				
				if(hmHolidayDates!=null && hmHolidayDates.containsKey(uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDateFormat()) )){
					strOverTimeType = "PH";
				}else if(hmWeekEnds!=null && hmWeekEnds.containsKey(strDate+"_"+hmEmpWlocationMap.get(strEmpId))){
					strOverTimeType = "BH";
				}else{
					strOverTimeType = "EH";
				}
						
				hmOvertimePolicy=hmEmpOverTimeLevelPolicy.get(strLevelId+"_"+strOverTimeType);
				if(hmOvertimePolicy==null) hmOvertimePolicy=new HashMap<String, String>();
				
				String salaryHeadId=hmOvertimePolicy.get("SALARY_HEAD_ID");
				List<String> salaryHeadList=null;
				if(salaryHeadId!=null){
					salaryHeadList=Arrays.asList(salaryHeadId.split(","));
				}
				
				if("RH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))){
					dblOverTimeCalcHours = uF.parseToDouble(hmEmpRosterHours.get(strDate+"_"+strEmpId));
				}else if("SWH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))){
					dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
				}else{
					dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
				}
				
				if("MD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))){
					dblOverTimeCalcDays = nTotalNumberOfDays; 
				}else if("AWD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))){
					//dblOverTimeCalcDays = nTotalNumberOfDays - nWeekEnds;
					dblOverTimeCalcDays = dblTotalPresentDays;
				}else{
					dblOverTimeCalcDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));
				}
										
				double dblSubSalaryAmount = 0;
				double dblSubSalaryAmountActualCTC = 0;
				for(int i=0;salaryHeadList!=null && !salaryHeadList.isEmpty() && i<salaryHeadList.size();i++){
												
					Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(salaryHeadList.get(i).trim());
					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
					dblSubSalaryAmount += uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));					
												
					Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(salaryHeadList.get(i).trim());
					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
					dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
					
				}
				
				
				String overtimePaymentType=hmOvertimePolicy.get("OVERTIME_PAYMENT_TYPE");
				String strCalcBasic =hmOvertimePolicy.get("CAL_BASIS");
				double dblAmount = uF.parseToDouble((String)hmOvertimePolicy.get("OVERTIME_PAYMENT_AMOUNT"));
				
				
				if(strCalcBasic!=null && strCalcBasic.equals("FD")){
					if(overtimePaymentType!=null && overtimePaymentType.equals("A")){
						dblTotalOverTimeAmount += dblAmount;
					}else{
						dblTotalOverTimeAmount += dblAmount * dblSubSalaryAmount/ 100;
					}
					
				}else if(dblOverTimeCalcHours>0){
					if(overtimePaymentType!=null && overtimePaymentType.equals("A")){
						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount / ( dblOverTimeCalcDays * dblOverTimeCalcHours);
					}else{
						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount * dblSubSalaryAmount/ (100 * dblOverTimeCalcDays * dblOverTimeCalcHours);								 
					}
				}
				
			}

			
			
			
			dblTotalOverTimeAmount += uF.parseToDouble(hmIndividualOvertime.get(strEmpId));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		//return uF.parseToDouble(uF.formatIntoComma(dblTotalOverTimeAmount));
		return dblTotalOverTimeAmount;
	}

	String paycycle;
	String wLocation;
	String level;
	String f_department;
	String f_service;
	
	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillPayCycleDuration> paycycleDurationList;
	String strPaycycleDuration;
	
	List<FillPayMode> paymentModeList;
	
	
	String f_paymentMode;
	String f_org;
	
	List<FillOrganisation> organisationList;
	
	String redirectUrl;
	String approve;
	String dtMin;
	String dtMax;
	String[] chbox;
	String[] empID;
	String[] paymentMode;

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

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.request = request;

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
	
	
	
	public double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearEnd, int nPayMonth, String strWLocationStateId){
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rst = null, rs = null;
		double dblDeductionPayMonth = 0;
		
		
		try {
			
//			pst = con.prepareStatement("select * from deduction_details_india where income_from<= ? and income_to>= ? and state_id=? order by financial_year_from desc limit 1");
			pst = con.prepareStatement("select * from deduction_details_india where income_from<= ? and income_to>= ? and state_id=? and financial_year_from = (select max(financial_year_from) from deduction_details_india) limit 1");
			
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			
			rs = pst.executeQuery();
			
			
			log.debug("pst====>"+pst);
			
			
			double dblDeductionAmount = 0;
			double dblDeductionPaycycleAmount = 0;
			while(rs.next()){
				dblDeductionAmount = rs.getDouble("deduction_amount");
				dblDeductionPaycycleAmount = rs.getDouble("deduction_paycycle");
			}
			rs.close();
			pst.close();
			
			nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
			int nFinancialYearEndMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
			nFinancialYearEndMonth = nFinancialYearEndMonth - 1;

			if(nFinancialYearEndMonth==nPayMonth){
				dblDeductionPayMonth = dblDeductionAmount - (11*dblDeductionPaycycleAmount);
			}else{
				dblDeductionPayMonth = dblDeductionPaycycleAmount;
			}

			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblDeductionPayMonth;
		
	}
	
	
	public double calculateLOAN(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strEmpId, CommonFunctions CF, Map hmLoanAmt, Map hmEmpLoan, List alLoans){
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		double dblTotalCalculatedAmount = 0;
		
		
		try {
			
			Map hmLoanAmtInner = new HashMap();
			
			pst = con.prepareStatement(selectLoanPayroll2);
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			
			double dblPrincipalAmt = 0;
			double dblBalAmt = 0;
			double dblROI = 0;
			double dblDuration = 0;
			double dblTDSAmt = 0;
			
			String strApprovedDate = null;
			
			
			Map hmEmpLoanInner = new HashMap();
			
			
			while(rs.next()){
				dblPrincipalAmt = rs.getDouble("amount_paid"); 
				dblTDSAmt = rs.getDouble("tds_amount");
				dblBalAmt = rs.getDouble("balance_amount");
				dblROI = rs.getDouble("loan_interest");
				dblDuration = rs.getDouble("duration_months");
				
				strApprovedDate = rs.getString("approved_date");
				
//				dblPrincipalAmt += dblTDSAmt;
				
				if(strApprovedDate!=null){
					
					Calendar calCurrent = GregorianCalendar.getInstance();
					calCurrent.setTime(uF.getCurrentDate(CF.getStrTimeZone()));
					
					int nCurrentMonth = calCurrent.get(Calendar.MONTH);
					
					Calendar calApproved = GregorianCalendar.getInstance();
					calApproved.setTime(uF.getDateFormat(strApprovedDate, DBDATE));
					
					int nApprovedMonth = calApproved.get(Calendar.MONTH);
					calApproved.add(Calendar.MONTH, (int)dblDuration);
					
					int nLastMonth = calApproved.get(Calendar.MONTH);
					String strLastDate = calApproved.get(Calendar.DATE) +"/"+(calApproved.get(Calendar.MONTH)+1)+"/"+calApproved.get(Calendar.YEAR);
					int nBalanceMonths = uF.parseToInt(uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, strLastDate, DATE_FORMAT,CF.getStrTimeZone()));
					nBalanceMonths = (int)nBalanceMonths/30;
					
					
					
					
//					dblCalculatedAmount = uF.getEMI(dblBalAmt, dblROI, nBalanceMonths);
					dblCalculatedAmount = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
					dblCalculatedAmount = dblCalculatedAmount / dblDuration; 
					
					
					if(dblCalculatedAmount>=dblBalAmt){
						dblCalculatedAmount = dblBalAmt;
					}
					if(dblCalculatedAmount>dblGross){
						dblCalculatedAmount = dblGross;
					}
					dblTotalCalculatedAmount +=dblCalculatedAmount;
					hmLoanAmt.put(rs.getString("loan_applied_id"), uF.formatIntoTwoDecimal(dblCalculatedAmount));
					
					
					hmEmpLoanInner = (Map)hmEmpLoan.get(rs.getString("emp_id"));
					if(hmEmpLoanInner==null)hmEmpLoanInner=new HashMap();
					hmEmpLoanInner.put(rs.getString("loan_id"), uF.formatIntoTwoDecimal(dblCalculatedAmount));
					hmEmpLoan.put(rs.getString("emp_id"), hmEmpLoanInner);
					
					if(!alLoans.contains(rs.getString("loan_id"))){
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblTotalCalculatedAmount;
		
	}
	
	public double calculateEEESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strWLocationStateId, Map hmVariables, String strEmpId){
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		try {
			
			pst = con.prepareStatement(selectESI);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			rs = pst.executeQuery();
			
			
			double dblEEESIAmount = 0;
			double dblESIMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()){
				dblEEESIAmount = rs.getDouble("eesi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null){
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			
			
			
			
			
			double dblAmount = 0;
			double dblAmountEligibility = 0; 
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")){
					dblAmountEligibility += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
				}
				dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));	
			}
			
			if(dblAmountEligibility<dblESIMaxAmount){
				dblCalculatedAmount = (( dblEEESIAmount * dblAmount ) / 100);
			}
			
			/*if(dblAmountEligibility<dblESIMaxAmount){
				dblCalculatedAmount = (( dblEEESIAmount * dblAmountEligibility ) / 100);
			}*/
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblCalculatedAmount;
		
	}
	
	public double calculateERESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strWLocationStateId){
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		
		try {
			
			pst = con.prepareStatement(selectERESI);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			rs = pst.executeQuery();
			
			
			
			double dblERESIAmount = 0;
			double dblESIMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()){
				dblERESIAmount = rs.getDouble("ersi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null){
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
			}
			
			if(dblAmount<dblESIMaxAmount){
				dblCalculatedAmount = (( dblERESIAmount * dblAmount ) / 100);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblCalculatedAmount;
	}
	
	
	
	public double calculateEELWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strWLocationStateId, Map hmVariables, String strEmpId, int nPayMonth, String strOrgId){
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		try {
			
			pst = con.prepareStatement(selectLWF);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setInt(4, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			
			double dblERLWFAmount = 0;
			double dblLWFMaxAmount = 0;
			String strSalaryHeads = null;
			String strMonths = null;
			while(rs.next()){
				dblERLWFAmount = rs.getDouble("eelfw_contribution");
				dblLWFMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strMonths  = rs.getString("months");
			}
			rs.close();
			pst.close();

			
			String []arrMonths = null;
			if(strMonths!=null){
				arrMonths = strMonths.split(",");
			}
			
			if(ArrayUtils.contains(arrMonths, nPayMonth+"")>=0){
				
				String []arrSalaryHeads = null;
				if(strSalaryHeads!=null){
					arrSalaryHeads = strSalaryHeads.split(",");
				}
				
				
				double dblAmount = 0;
				for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
					dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
				}
				
				
				pst = con.prepareStatement(selectERLWFC);
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strWLocationStateId));
				pst.setDouble(4, dblAmount);
				pst.setDouble(5, dblAmount);
				pst.setInt(6, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				
				while(rs.next()){
					dblCalculatedAmount = uF.parseToDouble(rs.getString("eelfw_contribution"));
				}
				rs.close();
				pst.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblCalculatedAmount;
	}
	
	public double calculateERLWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strWLocationStateId, int nPayMonth, String strOrgId){
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		
		try {
			
			pst = con.prepareStatement(selectERLWF);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setInt(4, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			double dblERLWFAmount = 0;
			double dblLWFMaxAmount = 0;
			String strSalaryHeads = null;
			String strMonths = null;
			while(rs.next()){
				dblERLWFAmount = rs.getDouble("erlfw_contribution");
				dblLWFMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
				strMonths  = rs.getString("months");
			}
			rs.close();
			pst.close();

			String []arrMonths = null;
			if(strMonths!=null){
				arrMonths = strMonths.split(",");
			}
			
			if(ArrayUtils.contains(arrMonths, nPayMonth+"")>=0){
				String []arrSalaryHeads = null;
				if(strSalaryHeads!=null){
					arrSalaryHeads = strSalaryHeads.split(",");
				}
				
				
				double dblAmount = 0;
				for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
					dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
				}
				
				
				pst = con.prepareStatement(selectERLWFC);
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strWLocationStateId));
				pst.setDouble(4, dblAmount);
				pst.setDouble(5, dblAmount);
				pst.setInt(6, uF.parseToInt(strOrgId));
				rs = pst.executeQuery();
				
				while(rs.next()){
					dblCalculatedAmount = uF.parseToDouble(rs.getString("erlfw_contribution"));
				}
				rs.close();
				pst.close();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblCalculatedAmount;
	}

	public double calculateEEPF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, Map hmVoluntaryPF, String strEmpId, String strMonth, String strPaycycle, boolean isInsert){
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		
		
		try {
			
			pst = con.prepareStatement(selectEEPF);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			rs = pst.executeQuery();
			
			
			
			double dblEEPFAmount = 0;
			double dblMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()){
				dblEEPFAmount = rs.getDouble("eepf_contribution");
				dblMaxAmount = rs.getDouble("epf_max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null){
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				Map<String, String> hmArrearInner =(Map<String, String>)hmTotal.get(arrSalaryHeads[i]);
				if(hmArrearInner==null) hmArrearInner=new HashMap<String, String>();
				dblAmount += uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
			}
			
			/**
			 * Change on 24-04-2012
			 */
			
			if(dblAmount>=dblMaxAmount){
				dblAmount = dblMaxAmount;
				
			}
			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
			
//			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
//			if(dblCalculatedAmount>=dblMaxAmount){
//				dblCalculatedAmount = dblMaxAmount;
//			}
			
			
			
			
			/**
			 * If VPF is to be calculated separately,
			 * the the below code needs to be commented
			 * 
			 * */
			
//			if(hmVoluntaryPF==null){
//				hmVoluntaryPF = new HashMap();
//			}
//			dblCalculatedAmount += uF.parseToDouble((String)hmVoluntaryPF.get("AMOUNT"));
//			
			
			
			
			
			
			if(isInsert){
				
				/*if(hmVoluntaryPF==null){
					hmVoluntaryPF = new HashMap();  
				}
				dblCalculatedAmount += uF.parseToDouble((String)hmVoluntaryPF.get("AMOUNT")); */
				
				Map<String, String> hmArrearInner =(Map<String, String>)hmTotal.get(VOLUNTARY_EPF+"");
				if(hmArrearInner==null) hmArrearInner=new HashMap<String, String>();
				
				double dblEVPF = uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
					
				pst = con.prepareStatement("insert into emp_epf_details (financial_year_start, financial_year_end, salary_head_id, epf_max_limit, eepf_contribution, emp_id, paycycle, _month, evpf_contribution) values (?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
				pst.setDouble(4, Math.round(dblAmount));
				pst.setDouble(5, Math.round(dblCalculatedAmount)); 
				pst.setInt(6, uF.parseToInt(strEmpId));
				pst.setInt(7, uF.parseToInt(strPaycycle));
				pst.setInt(8, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
				pst.setDouble(9, Math.round(dblEVPF));
				pst.execute();
				pst.close();
			}
			
			
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblCalculatedAmount;
		
	}

	public double calculateERPF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal,  String strEmpId, String strMonth, String strPaycycle, boolean isInsert){
		
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
		
		
		try {
			
			pst = con.prepareStatement(selectERPF);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
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
			
			while(rs.next()){
				
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
			if(strSalaryHeads!=null){
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			double dblAmountERPF = 0;
			double dblAmountEEPF = 0;
			double dblAmountERPS = 0;
			double dblAmountERPS1 = 0;
			double dblAmountEREDLI = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				Map<String, String> hmArrearInner =(Map<String, String>)hmTotal.get(arrSalaryHeads[i]);
				if(hmArrearInner==null) hmArrearInner=new HashMap<String, String>();
				dblAmount += uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
			}
			
			
			/**
			 * Changed on 24-04-2012
			 * 
			 */
			
			
			if(dblAmount>=dblEPRMaxAmount){
				dblAmountERPF = dblEPRMaxAmount;
			}else{
				dblAmountERPF = dblAmount;
			}
			
			if(dblAmount>=dblEPFMaxAmount){
				dblAmountEEPF = dblEPFMaxAmount;
			}else{
				dblAmountEEPF = dblAmount;
			}
			
			
			dblAmountERPS1 = dblAmount;
			if(dblAmount>=dblEPSMaxAmount){
				dblAmountERPS = dblEPSMaxAmount;
			}else{
				dblAmountERPS = dblAmount;
			}
			
			if(dblAmount>=dblEDLIMaxAmount){
				dblAmountEREDLI = dblEDLIMaxAmount;
			}else{
				dblAmountEREDLI = dblAmount;
			}
			
			
			
			if(isInsert){
				dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
				dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
				
				dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
				
				
				dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
				dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
				dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
			}else{
				if(erpfContributionchbox){
					dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
				}
				if(erpsContributionchbox){
					dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
					dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
				}
					
				if(erdliContributionchbox){
					dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
				}
				
				if(edliAdminChargeschbox){
					dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
				}
				if(pfAdminChargeschbox){
					dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
				}
			}
			
			
			if(CF.isEPF_Condition1()){
				dblEPF += dblEPS1 - dblEPS;
			}
			
			
			
			dblTotalEDLI = dblEDLI + dblEDLIAdmin;
			dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;
			
			
			if(isInsert){
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
				
//				int pfStartYear = uF.parseToInt(uF.getDateFormat(pfStartDate, DBDATE, "yyyy"));
				boolean isEnableEmployerPf = CF.getFeatureManagementStatus(request, uF, F_ENABLE_ADD_AMOUNT_IN_EMPLOYER_PF_ONLY);
				if(isEnableEmployerPf && uF.parseToInt(uF.getDateFormat(pfStartDate, DBDATE, "yyyy")) >= 2015){
					pst = con.prepareStatement("update emp_epf_details set eps_max_limit=?, edli_max_limit=?, erpf_contribution=?, erps_contribution=?, erdli_contribution=?, pf_admin_charges=?, edli_admin_charges=?  where financial_year_start=? and financial_year_end=? and emp_id=? and paycycle=? and _month=?");
					pst.setDouble(1, Math.round(dblAmountERPS));
					pst.setDouble(2, Math.round(dblAmountEREDLI));
					double totalErpf = dblEPF+dblEPS;
					pst.setDouble(3, Math.ceil(totalErpf));
					pst.setDouble(4, 0);
					pst.setDouble(5, Math.round(dblEDLI));
					pst.setDouble(6, Math.round(dblEPFAdmin));
					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEDLIAdmin)));
					pst.setDate(8, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(10, uF.parseToInt(strEmpId));
					pst.setInt(11, uF.parseToInt(strPaycycle));
					pst.setInt(12, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM"))); 
					pst.execute();
					pst.close();
				} else {
					pst = con.prepareStatement("update emp_epf_details set  eps_max_limit=?, edli_max_limit=?, erpf_contribution=?, erps_contribution=?, erdli_contribution=?, pf_admin_charges=?, edli_admin_charges=?  where financial_year_start=? and financial_year_end=? and emp_id=? and paycycle=? and _month=?");
					pst.setDouble(1, Math.round(dblAmountERPS));
					pst.setDouble(2, Math.round(dblAmountEREDLI));
					pst.setDouble(3, Math.ceil(dblEPF));
					pst.setDouble(4, Math.round(dblEPS));
					pst.setDouble(5, Math.round(dblEDLI));
					pst.setDouble(6, Math.round(dblEPFAdmin));
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return (dblTotalEPF + dblTotalEDLI);
		
	}
	
	
	public void calculateEESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map hmVariables, boolean isInsert){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmountEESI = 0;
		double dblCalculatedAmountERSI = 0;
		
		
		try {
			
			pst = con.prepareStatement(selectEESI);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
			
			rs = pst.executeQuery();
			
			double dblEESIAmount = 0;
			double dblERSIAmount = 0;
			double dblMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()){
				dblEESIAmount = rs.getDouble("eesi_contribution");
				dblERSIAmount = rs.getDouble("ersi_contribution");
				dblMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null){
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				Map<String, String> hmArrearInner =(Map<String, String>)hmTotal.get(arrSalaryHeads[i]);
				if(hmArrearInner==null) hmArrearInner=new HashMap<String, String>();
				
				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")){
					dblAmountEligibility += uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
				}
				dblAmount += uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
			}
			
			/**
			 * Change on 24-04-2012
			 */
			
			if(dblAmountEligibility>=dblMaxAmount){
				return;
			}
			
			dblCalculatedAmountEESI = ( dblEESIAmount * dblAmount ) / 100;
			dblCalculatedAmountERSI = ( dblERSIAmount * dblAmount ) / 100;
			
//			dblCalculatedAmountEESI = ( dblEESIAmount * dblAmountEligibility ) / 100;
//			dblCalculatedAmountERSI = ( dblERSIAmount * dblAmountEligibility ) / 100;
			
			
//			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
//			if(dblCalculatedAmount>=dblMaxAmount){
//				dblCalculatedAmount = dblMaxAmount;
//			}
			
			
			
			
			if(isInsert){
				pst = con.prepareStatement("insert into emp_esi_details (financial_year_start, financial_year_end, salary_head_id, esi_max_limit, eesi_contribution, ersi_contribution, user_id, entry_timestamp, emp_id, paycycle, _month) values (?,?,?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
				pst.setDouble(4, Math.ceil(dblAmount)); 
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
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public void calculateELWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map hmVariables, boolean isInsert, String strOrgId){
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rs = null;
		double dblCalculatedAmountEELWF = 0;
		double dblCalculatedAmountERLWF = 0;
		
		
		try {
			
			pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? and org_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
			pst.setInt(4, uF.parseToInt(hmEmpStateMap.get(strOrgId)));
			rs = pst.executeQuery();
			
			String strSalaryHeads = null;
			while(rs.next()){
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();
			
			String[] arrSalaryHeads = null;
			if(strSalaryHeads!=null){
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			double dblAmountEligibility = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				Map<String, String> hmArrearInner =(Map<String, String>)hmTotal.get(arrSalaryHeads[i]);
				if(hmArrearInner==null) hmArrearInner=new HashMap<String, String>();
				
				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")){
					dblAmountEligibility += uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
				}
				dblAmount += uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
			} 
			
			pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? " +
					" and ? between min_limit and max_limit and org_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
			pst.setDouble(4,dblAmount);
			pst.setInt(5, uF.parseToInt(hmEmpStateMap.get(strOrgId)));
			rs = pst.executeQuery();
			
			double dblEELWFAmount = 0;
			double dblERLWFAmount = 0;
			double dblMaxAmount = 0;
			String lwfMonth=null;
			while(rs.next()){
				dblEELWFAmount = rs.getDouble("eelfw_contribution");
				dblERLWFAmount = rs.getDouble("erlfw_contribution");
				dblMaxAmount = rs.getDouble("max_limit");
				lwfMonth=rs.getString("months");
			}
			rs.close();
			pst.close();			
			
			if(dblAmountEligibility>=dblMaxAmount){
				return;
			}
			
			
			List<String> lwfMonthList=null;
			if(lwfMonth!=null){
				lwfMonthList=Arrays.asList(lwfMonth.split(","));
			}
			
			
			int month=uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM"));
			if(lwfMonthList==null || !lwfMonthList.contains(""+month)){
				return;
			}
			
			dblCalculatedAmountEELWF = dblEELWFAmount ;
			dblCalculatedAmountERLWF = dblERLWFAmount;
			
			
			if(isInsert){
				pst = con.prepareStatement("insert into emp_lwf_details (financial_year_start, financial_year_end, salary_head_id, " +
						"lwf_max_limit, eelwf_contribution, erlwf_contribution, user_id, entry_timestamp, emp_id, paycycle, _month) " +
						"values (?,?,?,?,?,?,?,?,?,?,?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, strSalaryHeads);
				pst.setDouble(4, Math.round(dblAmount)); 
				pst.setDouble(5, Math.round(dblCalculatedAmountEELWF));
				pst.setDouble(6, Math.round(dblCalculatedAmountERLWF));
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
		}
	}

	public double calculateTDS(Connection con, UtilityFunctions uF, double dblGross, double dblCess1, double dblCess2, double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblBasicDA,
		int nPayMonth, String strPaycycleStart, String strFinancialYearStart, String strFinancialYearEnd,String strEmpId, String strGender, String strAge, String strWLocationStateId,
		Map hmEmpExemptionsMap, Map hmEmpHomeLoanMap, Map hmFixedExemptions, Map hmEmpMertoMap, Map hmEmpRentPaidMap, Map hmPaidSalaryDetails, Map hmTotal, Map hmSalaryDetails, Map hmEmpLevelMap, CommonFunctions CF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblTDSMonth = 0;
		
		try {
			 
			/**
			 * TDS Projection
			 * If there is any amount specified for TDS to be deducted in projection table, 
			 * then it will consider that amount as a TDS for that particular month and else
			 * it will calculate the TDS based on the actual calculations.
			 * */
			String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
			int slabType = uF.parseToInt(strSlabType);
			
			pst = con.prepareStatement("select * from tds_projections where emp_id =? and month=? and fy_year_from=? and fy_year_end=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, nPayMonth);
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			if(rs.next()){
				dblTDSMonth = rs.getDouble("amount");
				return dblTDSMonth;
			}
			rs.close();
			pst.close();
			
			if(uF.parseToBoolean((String)hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))){
				
				dblTDSMonth = dblGross * dblFlatTDS / 100;
//				dblTDSMonth += (dblCess1 * 0.01 * dblTDSMonth) + (dblCess2 * 0.01 * dblTDSMonth); 
				
			}else{
				
				pst = con.prepareStatement(selectTDS);
				pst.setInt(1, TDS);
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				double dblTDSPaidAmount = 0;
				while(rs.next()){
					dblTDSPaidAmount = rs.getDouble("tds");
				}
				rs.close();
				pst.close();
				
				
				
				
				
//				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation pg, salary_details sd where pg.salary_head_id=sd.salary_head_id and emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ?");
				pst = con.prepareStatement(selectTDS1);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
				rs = pst.executeQuery();
				
				double dblGrossPaidAmount = 0;
				while(rs.next()){
					dblGrossPaidAmount = rs.getDouble("amount");
				}
				rs.close();
				pst.close();
				
				
				String strMonthsLeft = uF.dateDifference(strPaycycleStart, DATE_FORMAT, strFinancialYearEnd, DATE_FORMAT,CF.getStrTimeZone());
				int nMonthsLeft = Math.round(uF.parseToInt(strMonthsLeft) / 30);
				
				/**
				 * 			ALL EXEMPTION WILL COME HERE
				 * **/
				double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
				double dblHomeLoanExemtion = uF.parseToDouble((String)hmEmpHomeLoanMap.get(strEmpId));
				
				double dblEEEPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYEE_EPF+""));
				double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYEE_EPF+""));
				
				double dblEREPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYER_EPF+""));
				double dblEREPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYER_EPF+""));
				
//				double dblTotalInvestment = dblInvestment + dblEEEPFPaid + dblEEEPFToBePaid+ dblEREPFPaid + dblEREPFToBePaid;
				double dblTotalInvestment = dblInvestment + dblEREPFPaid + dblEREPFToBePaid;
				
//				if(dblTotalInvestment>=dblDeclaredInvestmentExemption){
//					dblTotalInvestment = dblDeclaredInvestmentExemption;
//				}
				
				double dblHRAExemptions = getHRAExemptionCalculation(con, uF, hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap);
				
				double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions; 
				
				Set set = hmSalaryDetails.keySet();
				Iterator it = set.iterator();
				while(it.hasNext()){
					String strSalaryHeadId = (String)it.next();
					String strSalaryHeadName = (String)hmSalaryDetails.get(strSalaryHeadId);
					
					/*if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
						System.out.println(strSalaryHeadId+"  " +strSalaryHeadName+"  hmFixedExemptions="+hmFixedExemptions);
						System.out.println("hmFixedExemptions.containsKey(strSalaryHeadName)="+hmFixedExemptions.containsKey(strSalaryHeadName));
						System.out.println("hmTotal="+hmTotal);
						
					}*/
					
					if(hmFixedExemptions.containsKey(strSalaryHeadName)){
						
						double dblIndividualExemption = uF.parseToDouble((String)hmFixedExemptions.get(strSalaryHeadName));
						
						double dblTotalToBePaid = 0;
						if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX) {
							int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
							double dblCurrentMonthGross = uF.parseToDouble((String)hmTotal.get("GROSS"));
							dblTotalToBePaid = (nMonthsLeft-1) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
							dblTotalToBePaid += calculateProfessionalTax(con, uF, dblCurrentMonthGross, strFinancialYearEnd, nLastPayMonth, strWLocationStateId);
						} else {
							dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
						}
						
						double dblTotalPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(strSalaryHeadId));
						double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;  
						double dblExmp = 0;
						if(dblTotalPaidAmount >= dblIndividualExemption) {
							dblExemptions += dblIndividualExemption;
							dblExmp = dblIndividualExemption;
						} else {
							dblExemptions += dblTotalPaidAmount;
							dblExmp = dblTotalPaidAmount;
						}
						
						log.debug("dblExemption="+dblIndividualExemption);
						log.debug("dblTotalToBePaid="+dblTotalToBePaid);
						log.debug("dblTotalPaid="+dblTotalPaid);
						log.debug("dblTotalPaidAmount="+dblTotalPaidAmount);
						log.debug("dblExemptions="+dblExemptions);
						
						log.debug(strEmpId+"============"+strSalaryHeadName+"===========");
					}
				}
				
				
				double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross);
				
				log.debug("Invest Exmp="+(String)hmEmpExemptionsMap.get(strEmpId));
//				log.debug("HRA Exemp="+getHRAExemptionCalculation(hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap));
				
				log.debug("strMonthsLeft="+strMonthsLeft);
				log.debug("strPaycycleStart="+strPaycycleStart);
				log.debug("strFinalcialYearEnd="+strFinancialYearEnd);
				
				log.debug("dblGrossPaidAmount="+dblGrossPaidAmount);
				log.debug("to be paid="+(nMonthsLeft * dblGross));
				log.debug("dblExemptions="+dblExemptions );
				log.debug("dblTotalGrossSalary="+dblTotalGrossSalary );
				log.debug("dblTotalGrossSalary - Exemp="+(dblTotalGrossSalary - dblExemptions) );
				
				double dblTotalTaxableSalary = 0;
				if(dblTotalGrossSalary>dblExemptions){
					dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
				}else if(dblTotalGrossSalary>0 && dblExemptions>0 && dblTotalGrossSalary<=dblExemptions){
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
					String strDeductionType = null;
					
					if(rs.next()){
						dblDeductionAmount = rs.getDouble("deduction_amount");
						strDeductionType = rs.getString("deduction_type");
						dblUpperDeductionSlabLimit = rs.getDouble("_to");
						dblLowerDeductionSlabLimit = rs.getDouble("_from");
					}
					rs.close();
					pst.close();
					
					if(countBug==0){
						dblTotalNetTaxableSalary = dblTotalTaxableSalary;
					}
					
					if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit){
						dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
						
//						log.debug("dblTotalTaxableSalary 1 ="+((dblDeductionAmount /100) *  dblUpperDeductionSlabLimit ));
						
					}else{
						
						if(countBug==0){
							dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
						}
						
						dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
					
						
					}
					
					dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;
					
					
					if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
					countBug++;
					
				}while(dblTotalNetTaxableSalary>0);
				
				
				// Service tax + Education cess
				
				double dblCess = dblTotalTDSPayable * ( dblCess1/100);
				dblCess += dblTotalTDSPayable * ( dblCess2/100);
				
				dblTotalTDSPayable += dblCess;   
				
					
				dblTDSMonth = dblTotalTDSPayable - dblTDSPaidAmount;
				dblTDSMonth = dblTDSMonth/(nMonthsLeft);
				
				if(dblTDSMonth<0){
					dblTDSMonth = 0;
				}
				
				
				
				
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblTDSMonth;
	}
	
	

	
	public double calculateTDSA(Connection con, UtilityFunctions uF, double dblGross, double dblCess1, double dblCess2, double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblBasicDA,
		int nPayMonth, String strPaycycleStart, String strFinancialYearStart, String strFinancialYearEnd,String strEmpId, String strGender, String strAge, String strWLocationStateId,
		Map hmEmpExemptionsMap, Map hmEmpHomeLoanMap, Map hmFixedExemptions, Map hmEmpMertoMap, Map hmEmpRentPaidMap, Map hmPaidSalaryDetails, Map hmTotal, Map hmSalaryDetails, Map hmEmpLevelMap, CommonFunctions CF, int nMonthsLeft) {
		
		PreparedStatement pst = null, pst1 = null;
		ResultSet rst = null, rs = null, rs1 = null;
		double dblTDSMonth = 0;
		
		try {
			
			if(uF.parseToBoolean((String)hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))){
				dblTDSMonth = dblGross * dblFlatTDS / 100;
//				dblTDSMonth += (dblCess1 * 0.01 * dblTDSMonth) + (dblCess2 * 0.01 * dblTDSMonth); 
			} else {
				
				String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
				int slabType = uF.parseToInt(strSlabType);
				pst = con.prepareStatement(selectTDS);
				pst.setInt(1, TDS);
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
				rs = pst.executeQuery();
				
				double dblTDSPaidAmount = 0;
				while(rs.next()){
					dblTDSPaidAmount = uF.parseToDouble(rs.getString("tds"));
				}
				rs.close();
				pst.close();
				
				pst1= con.prepareStatement(selectTDS2);
				pst1.setInt(1, TDS);
				pst1.setInt(2, uF.parseToInt(strEmpId));
				pst1.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst1.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst1.setDate(5, uF.getDateFormat(strPaycycleStart, DATE_FORMAT));
				rs1 = pst1.executeQuery();
				
				double dblTDSPaidAmount1 = 0;
				int count = 0; 
				while(rs1.next()){
					dblTDSPaidAmount += rs1.getDouble("amount");
					count++;
				}
				rs1.close();
				pst1.close();
				
				
//				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation pg, salary_details sd where pg.salary_head_id=sd.salary_head_id and emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ?");
				pst = con.prepareStatement(selectTDS1);
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				
				rs = pst.executeQuery();
				
				double dblGrossPaidAmount = 0;
				while(rs.next()){
					dblGrossPaidAmount = rs.getDouble("amount");
				}
				rs.close();
				pst.close();
				
				
				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
					
					System.out.println("strPaycycleStart==="+strPaycycleStart);
					System.out.println("strFinancialYearEnd==="+strFinancialYearEnd);
					System.out.println("dblGrossPaidAmount==="+dblGrossPaidAmount);
				}
				
				
				/**
				 * 			ALL EXEMPTION WILL COME HERE
				 * **/
				double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
				double dblHomeLoanExemtion = uF.parseToDouble((String)hmEmpHomeLoanMap.get(strEmpId));
				
				
				double dblEEEPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYEE_EPF+""));
				double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYEE_EPF+""));
				
				double dblEREPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYER_EPF+""));
				double dblEREPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYER_EPF+""));
				
//				double dblTotalInvestment = dblInvestment + dblEEEPFPaid + dblEEEPFToBePaid+ dblEREPFPaid + dblEREPFToBePaid;
				double dblTotalInvestment = dblInvestment + dblEREPFPaid + dblEREPFToBePaid;
				
				
//				if(dblTotalInvestment>=dblDeclaredInvestmentExemption){
//					dblTotalInvestment = dblDeclaredInvestmentExemption;
//				}
				
				double dblHRAExemptions = getHRAExemptionCalculation(con, uF, hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap);
				
				double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions; 
				
				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
					System.out.println("dblExemptions==="+dblExemptions);

					System.out.println("dblHRAExemptions==="+dblHRAExemptions);
					System.out.println("dblHomeLoanExemtion==="+dblHomeLoanExemtion);
					System.out.println("dblTotalInvestment==="+dblTotalInvestment);
					
					System.out.println("dblInvestment==="+dblInvestment);
					System.out.println("dblEEEPFPaid==="+dblEEEPFPaid);
					System.out.println("dblEEEPFToBePaid==="+dblEEEPFToBePaid);
					System.out.println("dblEREPFPaid==="+dblEREPFPaid);
					System.out.println("dblEREPFToBePaid==="+dblEREPFToBePaid);
					
				}
				
				
				Set set = hmSalaryDetails.keySet();
				Iterator it = set.iterator();
				while(it.hasNext()){
					String strSalaryHeadId = (String)it.next();
					String strSalaryHeadName = (String)hmSalaryDetails.get(strSalaryHeadId);
					
					if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
						System.out.println(strSalaryHeadId+"  " +strSalaryHeadName+"  hmFixedExemptions="+hmFixedExemptions);
						System.out.println("hmFixedExemptions.containsKey(strSalaryHeadName)="+hmFixedExemptions.containsKey(strSalaryHeadName));
						System.out.println("hmTotal="+hmTotal);
						
					}
					
					if(hmFixedExemptions.containsKey(strSalaryHeadName)){
						
						double dblIndividualExemption = uF.parseToDouble((String)hmFixedExemptions.get(strSalaryHeadName));
						
						double dblTotalToBePaid = 0;
						if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX){
							int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
							double dblCurrentMonthGross = uF.parseToDouble((String)hmTotal.get("GROSS"));
							dblTotalToBePaid = (nMonthsLeft-1) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
							dblTotalToBePaid += calculateProfessionalTax(con, uF, dblCurrentMonthGross, strFinancialYearEnd, nLastPayMonth, strWLocationStateId);
						}else{
							dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
						}
						
						double dblTotalPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(strSalaryHeadId));
						double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;  
						double dblExmp = 0;
						if(dblTotalPaidAmount >= dblIndividualExemption){
							dblExemptions += dblIndividualExemption;
							dblExmp = dblIndividualExemption;
						}else{
							dblExemptions += dblTotalPaidAmount;
							dblExmp = dblTotalPaidAmount;
						}
						
						
						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
							System.out.println(strSalaryHeadId+" == dblExmp==="+dblExmp+" dblExemptions=="+dblExemptions);
							System.out.println("dblTotalPaid=="+dblTotalPaid+" dblTotalToBePaid==="+dblTotalToBePaid);

						}
						
						log.debug("dblExemption="+dblIndividualExemption);
						log.debug("dblTotalToBePaid="+dblTotalToBePaid);
						log.debug("dblTotalPaid="+dblTotalPaid);
						log.debug("dblTotalPaidAmount="+dblTotalPaidAmount);
						log.debug("dblExemptions="+dblExemptions);
						
						log.debug(strEmpId+"============"+strSalaryHeadName+"===========");
						
					}
				}
				
				double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross);
				log.debug("Invest Exmp="+(String)hmEmpExemptionsMap.get(strEmpId));
//				log.debug("HRA Exemp="+getHRAExemptionCalculation(hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap));
				
				log.debug("strPaycycleStart="+strPaycycleStart);
				log.debug("strFinalcialYearEnd="+strFinancialYearEnd);
				
				log.debug("dblGrossPaidAmount="+dblGrossPaidAmount);
				log.debug("to be paid="+(nMonthsLeft * dblGross));
				log.debug("dblExemptions="+dblExemptions );
				log.debug("dblTotalGrossSalary="+dblTotalGrossSalary );
				log.debug("dblTotalGrossSalary - Exemp="+(dblTotalGrossSalary - dblExemptions) );
				
				double dblTotalTaxableSalary = 0;
				if(dblTotalGrossSalary>dblExemptions){
					dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
				}else if(dblTotalGrossSalary>0 && dblExemptions>0 && dblTotalGrossSalary<=dblExemptions){
					dblTotalTaxableSalary = 0;
				}
				
				
				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
					System.out.println("dblTotalGrossSalary==="+dblTotalGrossSalary);
					System.out.println("dblExemptions==="+dblExemptions);
					System.out.println("dblTotalTaxableSalary==="+dblTotalTaxableSalary);
					System.out.println("(nMonthsLeft * dblGross)==="+(nMonthsLeft * dblGross));
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
					
					if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
						System.out.println("pst=====>"+pst);
					}
					
					double dblDeductionAmount = 0;
					String strDeductionType = null;
					
					if(rs.next()){
						dblDeductionAmount = rs.getDouble("deduction_amount");
						strDeductionType = rs.getString("deduction_type");
						dblUpperDeductionSlabLimit = rs.getDouble("_to");
						dblLowerDeductionSlabLimit = rs.getDouble("_from");
					}
					rs.close();
					pst.close();
					
					if(countBug==0){
						dblTotalNetTaxableSalary = dblTotalTaxableSalary;
					}
					
					if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit){
						dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
						
//						log.debug("dblTotalTaxableSalary 1 ="+((dblDeductionAmount /100) *  dblUpperDeductionSlabLimit ));
						
						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
							System.out.println("=====IF=========");
							System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
							System.out.println("dblUpperDeductionSlabLimit=====>"+dblUpperDeductionSlabLimit);
							System.out.println("dblLowerDeductionSlabLimit=====>"+dblLowerDeductionSlabLimit);
							System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
							System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
						}
						
					}else{
						
						if(countBug==0){
							dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
						}
						
						dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
					
						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
							System.out.println("=====ELSE=========");
							
							System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
							System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
							System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
						}
					}
					
					dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;

					if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
					countBug++;
					
				}while(dblTotalNetTaxableSalary>0);
				
				// Service tax + Education cess
				
				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
					System.out.println("dblTotalTDSPayable=======>"+dblTotalTDSPayable);
				}
				
				double dblCess = dblTotalTDSPayable * ( dblCess1/100);
				dblCess += dblTotalTDSPayable * ( dblCess2/100);
				
				dblTotalTDSPayable += dblCess;   
				
				dblTDSMonth = dblTotalTDSPayable - dblTDSPaidAmount;
				dblTDSMonth = dblTDSMonth/(nMonthsLeft - count);
				
				if(dblTDSMonth<0){
					dblTDSMonth = 0;
				}
				
				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
					
					System.out.println("dblCess1=======>"+dblCess1);
					System.out.println("dblCess2=======>"+dblCess2);
					System.out.println("dblCess2=======>"+dblCess2);
					System.out.println("dblTotalTDSPayable=======>"+dblTotalTDSPayable);
					System.out.println("dblTDSPaidAmount=======>"+dblTDSPaidAmount);
					System.out.println("nMonthsLeft=======>"+nMonthsLeft);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(rs1 !=null){
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst1 !=null){
				try {
					pst1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblTDSMonth;
	}
	
	
	public Map getEmpInvestmentExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, double dblDeclaredInvestmentExemption){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmEmpExemptionsMap = new HashMap<String,String>();
		
		try {
			
			Map hmSectionLimitA = new HashMap();
			Map hmSectionLimitP = new HashMap();
			
			Map hmSectionLimitEmp = new HashMap();
			
			pst = con.prepareStatement(selectSection);
			rs = pst.executeQuery();
			
			
			while (rs.next()) {
				
				if(rs.getString("section_limit_type").equalsIgnoreCase("A")){
					hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				}else{
					hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
				}
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and fy_from = ? and fy_to = ? and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') group by emp_id, sd.section_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			rs = pst.executeQuery();
			
			
			double dblInvestmentLimit = 0;
			double dblInvestmentEmp = 0;
//			String strEmpIdNew = null;
//			String strEmpIdOld = null;
			
			while (rs.next()) {
//				double dblInvestment = rs.getDouble("amount_paid");
//				if(dblInvestment>=dblDeclaredInvestmentExemption){
//					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblDeclaredInvestmentExemption+"");
//				}else{
//					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestment+"");
//				}
				
				
//				strEmpIdNew = rs.getString("emp_id");
				
				/*if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					dblInvestmentEmp = 0;
				}
				*/
				
				String strSectionId = rs.getString("section_id");
				double dblInvestment = rs.getDouble("amount_paid");
				
				
				
				if(hmSectionLimitA.containsKey(strSectionId)){
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
				}else{
					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
				}
				
				
				
				if(dblInvestment>=dblInvestmentLimit){
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
				}else{
					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestment;
					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
				}
				
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpExemptionsMap;
	
	}
	
	
	public Map getEmpHomeLoanExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmEmpHomeLoanMap = new HashMap<String,String>();
		
		try {
			
			pst = con.prepareStatement("select * from section_details where section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') ");
			rs = pst.executeQuery();
			double dblLoanExemptionLimit = 0;
			while (rs.next()) {
				dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
			}
			rs.close();
			pst.close();
			
			
			
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? and status = true and  section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') group by emp_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit){
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
				}else{
					hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
				}
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpHomeLoanMap;
	
	}
	
	public Map getEmpRentPaid(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> hmEmpRentPaidMap = new HashMap<String,String>();
		
		try {
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and fy_from = ? and fy_to = ? and agreed_date between ? and ? and section_code in ('HRA') group by emp_id ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
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
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpRentPaidMap;
	
	}
	
	
//	public Map getPaidSalary(String strFinancialYearStart, String strFinancialYearEnd){
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
//				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
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
	
	
	
	
	/*public Map getHRAPaid(String strFinancialYearStart, String strFinancialYearEnd){
		
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
	
	public Map getBasicPaid(String strFinancialYearStart, String strFinancialYearEnd){
		
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
	
	
	public Map getEmpPaidAmountDetails(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
		
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
			Map hmInner = new HashMap();
			while (rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				
				
				if(strEmpIdNew !=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmInner = new HashMap();
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
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmEmpPaidAmountDetails;
	
	}
	
	
	
	
	public double getHRAExemptionCalculation(Connection con, UtilityFunctions uF, Map hmPaidSalaryDetails, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, double dblHRA, double dblBasicDA, Map hmEmpMertoMap, Map hmEmpRentPaidMap){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblHRAExemption = 0;
		
		try {
			
			boolean isMetro = uF.parseToBoolean((String)hmEmpMertoMap.get(strEmpId));
			
			String strBasicPaidAmount = (String)hmPaidSalaryDetails.get(BASIC+"");
			String strHRAPaidAmount = (String)hmPaidSalaryDetails.get(HRA+"");
			
			
			log.debug("<===            ===>");
			log.debug(HRA+" hmEmpPaidHRA==>"+hmPaidSalaryDetails);
			log.debug("(String)hmEmpPaidHRA.get(strEmpId)==>"+(String)hmPaidSalaryDetails.get(strEmpId));
			
			
			
			String strMonthsLeft = uF.dateDifference(strD1, DATE_FORMAT, strFinancialYearEnd, DATE_FORMAT,CF.getStrTimeZone());
			int nMonthsLeft = uF.parseToInt(strMonthsLeft) / 30;
			
			double dblBasicToBePaidAmount = nMonthsLeft * dblBasicDA;
			double dblHRAToBePaidAmount = nMonthsLeft * dblHRA;
			
			
			double dblTotalBasicDAAmount = uF.parseToDouble(strBasicPaidAmount) + dblBasicToBePaidAmount;
			double dblTotalHRAAmount = uF.parseToDouble(strHRAPaidAmount) + dblHRAToBePaidAmount;
			
			double dblTotalRentPaid = uF.parseToDouble((String)hmEmpRentPaidMap.get(strEmpId));
			
			
			if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
				System.out.println("dblTotalBasicDAAmount="+dblTotalBasicDAAmount);
				System.out.println("dblTotalHRAAmount="+dblTotalHRAAmount);
				System.out.println("dblTotalRentPaid="+dblTotalRentPaid);
			}
			
			
			
			
			
			log.debug("dblTotalHRAAmount==>"+dblTotalHRAAmount);
			log.debug("strHRAPaidAmount==>"+strHRAPaidAmount);
			log.debug("dblHRAToBePaidAmount==>"+dblHRAToBePaidAmount);
			
			log.debug("dblTotalBasicDAAmount==>"+dblTotalBasicDAAmount);
			log.debug("strBasicPaidAmount==>"+strBasicPaidAmount);
			log.debug("dblBasicToBePaidAmount==>"+dblBasicToBePaidAmount);
			log.debug("dblBasicDA==>"+dblBasicDA);
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from = ? and financial_year_to =? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			
			double dblCondition1= 0;
			double dblCondition2= 0;
			double dblCondition3= 0;
			
			while (rs.next()) {
				dblCondition1= rs.getDouble("condition1");
				dblCondition2= rs.getDouble("condition2");
				dblCondition3= rs.getDouble("condition3");
			}
			rs.close();
			pst.close();
			
//			double dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
			double dblRentPaidGreaterThanCondition1 = 0;
			
			log.debug("dblRentPaidGreaterThanCondition1==>"+dblRentPaidGreaterThanCondition1);
			log.debug("dblCondition1==>"+dblCondition1);
			log.debug("dblTotalBasicDAAmount==>"+dblTotalBasicDAAmount);
			
			
			if(dblTotalRentPaid>dblRentPaidGreaterThanCondition1){
				
				dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
				
				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
					System.out.println("==========IF==========");
					System.out.println("dblCondition1="+dblCondition1);
					System.out.println("dblTotalBasicDAAmount="+dblTotalBasicDAAmount);
				}
				
				
				
				dblRentPaidGreaterThanCondition1 = dblTotalRentPaid - dblRentPaidGreaterThanCondition1;
				
				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
					System.out.println("==========IF==========");
					System.out.println("dblTotalRentPaid="+dblTotalRentPaid);
					System.out.println("dblRentPaidGreaterThanCondition1="+dblRentPaidGreaterThanCondition1);
				}
				
			}else if(dblTotalRentPaid>0){
				dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
				
				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
					System.out.println("==========ELSE==========");
					System.out.println("dblCondition1="+dblCondition1);
					System.out.println("dblTotalBasicDAAmount="+dblTotalBasicDAAmount);
					System.out.println("dblRentPaidGreaterThanCondition1="+dblRentPaidGreaterThanCondition1);
				}
			}
			
			
			double dblRentPaidCondition23 = 0;
			
			if(isMetro){
				dblRentPaidCondition23 = dblCondition2 * dblTotalBasicDAAmount /100;
			}else{
				dblRentPaidCondition23 = dblCondition3 * dblTotalBasicDAAmount /100;
			}
			
			dblHRAExemption = Math.min(dblTotalHRAAmount, dblRentPaidGreaterThanCondition1);
			dblHRAExemption = Math.min(dblHRAExemption, dblRentPaidCondition23);
			
			
			if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
				System.out.println("dblTotalHRAAmount===>"+dblTotalHRAAmount);
				System.out.println("dblRentPaidGreaterThanCondition1===>"+dblRentPaidGreaterThanCondition1);
				System.out.println("dblRentPaidCondition23===>"+dblRentPaidCondition23);
				System.out.println("dblHRAExemption===>"+dblHRAExemption);
			}
			
//			
			
			log.debug("<===            ===>");
			log.debug("dblTotalHRAAmount==>"+dblTotalHRAAmount);
			log.debug("dblRentPaidGreaterThanCondition1==>"+dblRentPaidGreaterThanCondition1);
			log.debug("dblRentPaidCondition23==>"+dblRentPaidCondition23);
			log.debug("<===            ===>");
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblHRAExemption;
	
	}
	
	public Map getFixedExemption(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map hmFixedExemptions = new HashMap();
		
		try {
			
			pst = con.prepareStatement("select * from exemption_details where exemption_from = ? and exemption_to =? ");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			
			double dblCondition1= 0;
			double dblCondition2= 0;
			double dblCondition3= 0;
			
			while (rs.next()) {
				hmFixedExemptions.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmFixedExemptions;
	
	}
	
	
	public double getOverTimeCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List alServices, List alPresentDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, Map<String, Map<String, String>> hmOverTimeMap, Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays){
		
		double dblOverTime = 0.0d;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map hmFixedExemptions = new HashMap();
		
		try {
			
					
			double dblOTHoursWorked = 0;
			double dblAdditionalHoursWorked = 0;
			String strLevelId = hmEmpLevelMap.get(strEmpId);
			String strOverTimeType = null;
			
			for(int j=0; j<alPresentDays.size(); j++){
				String strDate = (String)alPresentDays.get(j);
				
				
				log.debug("hmHolidays===>"+hmHolidays);
				log.debug("strDate===>"+strDate);
				log.debug("strOverTimeType===>"+strOverTimeType);
				
				if(hmHolidays!=null && hmHolidays.containsKey(strDate)){
					strOverTimeType = "PH";
				}else{
					strOverTimeType = "EH";
				}
				
				
				Map hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
				if(hmTemp==null) hmTemp=new HashMap();
				
				String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
				String strPaymentType = (String)hmTemp.get("PAYMENT_TYPE");
				double dblAmount = uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
				
				Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(strSalarySubHead);
				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
				double dblSubSalaryAmount = uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
				
				
				
				Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
				double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
				
				for(int k=0; k<alServices.size(); k++){
					String strService = (String)alServices.get(k);
					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));

					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")){
						
						/**
						 *  IF condition is for additional hours worked during working days
						 * 
						 * **/
						
						
						if(hrsWorked > dblStandardHours){
							dblOTHoursWorked = (hrsWorked - dblStandardHours);
							
							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
								dblOverTime += (dblAmount * dblSubSalaryAmount * dblOTHoursWorked)/ (dblStandardHours * 100);
							}else if(strPaymentType!=null) {
								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
							}
							
						}
					
					}else{
						
						/**
						 *  Else condition is for pubic holidays
						 * 
						 * **/
						
						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
							
						}else if(strPaymentType!=null) {
							dblOverTime += dblAmount ;
						}
						
					}
					
					log.debug(strDate+"_"+strService+"===>"+hmHoursWorked.get(strDate+"_"+strService)); 
					
				}
			}
			
			log.debug("dblOTHoursWorked===>"+dblOTHoursWorked);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblOverTime;
	}
	
	public double getOverTimeCalculationL(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List alServices, List alPresentDays, List alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, Map<String, Map<String, String>> hmOverTimeMap, Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays, int nTotalNumberOfDaysForCalc, Map<String, String> hmIndividualOvertime){
		
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
			
			
			
			
			
			for(int j=0; j<alPresentDays.size(); j++){
				String strDate = (String)alPresentDays.get(j);
				
				if(hmHolidays!=null && hmHolidays.containsKey(strDate)){
					strOverTimeType = "PH";
				}else{
					strOverTimeType = "EH";
				}
				
				
				Map hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
				if(hmTemp==null) hmTemp=new HashMap();
				
				String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
				String strPaymentType = (String)hmTemp.get("PAYMENT_TYPE");
//				double dblAmount = uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
				double dblAmount = 0;
				/**
				 * Loop For all amounts
				 * 
				 * **/
				
				List<String> headIDList = null;
				if(strSalarySubHead!=null){
					headIDList=Arrays.asList(strSalarySubHead.split(","));
				}
				
				double dblSubSalaryAmount = 0;
				double dblSubSalaryAmountActualCTC = 0;
				for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++){
					dblAmount += uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
					
					Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(headIDList.get(i).trim());
					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
					dblSubSalaryAmount += uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
					
					
					
					Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(headIDList.get(i).trim());
					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
					dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
					
					
				}
				
				for(int k=0; k<alServices.size(); k++){
					String strService = (String)alServices.get(k);
					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));

					dblTotalHoursWorked +=  hrsWorked;
					
					
					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")){
						
						/**
						 *  IF condition is for additional hours worked during working days
						 * 
						 * **/
						
						
						if(hrsWorked > dblStandardHours && dblStandardHours>0){
							dblOTHoursWorked = (hrsWorked - dblStandardHours);
							
							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
							}else if(strPaymentType!=null) {
								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
							}
							
						}
						
					}else{
						
						/**
						 *  Else condition is for pubic holidays
						 * 
						 * **/
				
						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
							
						}else if(strPaymentType!=null) {
							dblOverTime += dblAmount ;
							dblOvertimeFixedAmount += dblAmount ;
						}
						
					}
				}
			}
			
			
			
			
			
			
			
			Map hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_BH");
			if(hmTemp==null) hmTemp=new HashMap();
			
			
			for(int j=0; hmTemp.size()>0 && j<alPresentWeekEndDays.size(); j++){
				String strDate = (String)alPresentWeekEndDays.get(j);
				strOverTimeType = "BH";
				
				hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
				if(hmTemp==null) hmTemp=new HashMap();
				
				String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
				String strPaymentType = (String)hmTemp.get("PAYMENT_TYPE");

				
				double dblAmount = 0;
				/**
				 * Loop For all amounts
				 * 
				 * **/
				
				List<String> headIDList= null;
				if(strSalarySubHead!=null){
					headIDList=Arrays.asList(strSalarySubHead.split(","));
				}
				
				
				double dblSubSalaryAmount = 0;
				double dblSubSalaryAmountActualCTC = 0;
				for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++){
					dblAmount += uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
					
					Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(headIDList.get(i).trim());
					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
					dblSubSalaryAmount += uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
					
					
					Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(headIDList.get(i).trim());
					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
					dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
					
					
				}
				
				
				for(int k=0; k<alServices.size(); k++){
					String strService = (String)alServices.get(k);
					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));

					
					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")){
						dblTotalHoursWorked +=  hrsWorked;
						/**
						 *  IF condition is for additional hours worked during working days
						 * 
						 * **/
						
						
						if(hrsWorked > dblStandardHours && dblStandardHours>0){
							dblOTHoursWorked = (hrsWorked - dblStandardHours);
							
							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
							}else if(strPaymentType!=null) {
								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
							}
							
						}
						
					}else{
						
						/**
						 *  Else condition is for pubic holidays
						 * 
						 * **/
				
						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
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
			if(dblStdOvertimeHours==0){
				dblStdOvertimeHours = dblStandardHours;
			}
			
			
			
			
			
			
			
			hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
			if(hmTemp==null) hmTemp=new HashMap();
			String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
//			Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
//			if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//			double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
			
			/**
			 * Loop For all amounts
			 * 
			 * **/
			
			List<String> headIDList=null;
			if(strSalarySubHead!=null){
				headIDList=Arrays.asList(strSalarySubHead.split(","));
			}
			double dblSubSalaryAmountActualCTC = 0;
			for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++){
								
				Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(headIDList.get(i).trim());
				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
				dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
				
				
			}
			
			double dblOverTimeHours = (dblTotalHoursWorked - ( alPresentDays.size() * dblStandardHours));
			if(dblStdOvertimeHours>0){
				dblOverTime = dblOverTimeHours * dblSubSalaryAmountActualCTC / (dblStdOvertimeHours * nTotalNumberOfDaysForCalc);
				dblOverTime += dblOvertimeFixedAmount;	
			}
			
			dblOverTime += uF.parseToDouble(hmIndividualOvertime.get(strEmpId)) ;

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblOverTime;
	}
	
	public double getBonusCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, Map<String, String>> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmEmpJoiningMap, CommonFunctions CF, Map<String, String> hmIndividualBonus){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblBonusCalculatedAmount = 0;
		
		try {
			

			String strJoiningDate = hmEmpJoiningMap.get(strEmpId);
			String strCurrentDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
			
			
			String strDays = uF.dateDifference(strJoiningDate, DATE_FORMAT, strCurrentDate, DBDATE,CF.getStrTimeZone());
			int nDays = uF.parseToInt(strDays);
			
			
			
			Map hmTemp = (Map)hmTotal.get(BASIC+"");
			if(hmTemp==null)hmTemp = new HashMap();
			double dblBasic = uF.parseToDouble((String)hmTemp.get("AMOUNT"));
			hmTemp = (Map)hmTotal.get(DA+"");
			if(hmTemp==null)hmTemp = new HashMap();
			double dblDA = uF.parseToDouble((String)hmTemp.get("AMOUNT"));
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
			String strEffectiveFY = null;
			String strSalaryCalculation = null;
			
			if(rs.next()){
				dblMinimumBonus = rs.getDouble("bonus_minimum");
				dblMaximumBonus = rs.getDouble("bonus_maximum");
				dblBonusAmount = rs.getDouble("bonus_amount");
				dblMinimumBonusDays = rs.getDouble("bonus_minimum_days");
				strBonusType = rs.getString("bonus_type");
				strBonusPeriod = rs.getString("bonus_period");
				strBonusSalary = rs.getString("salary_head_id");
				strEffectiveFY = rs.getString("salary_effective_year");
				strSalaryCalculation = rs.getString("salary_calculation");
			}
			rs.close();
			pst.close();
			
			
			String []arrMonth = null;
			boolean isBonusCalculation = false;
			
			if(strBonusPeriod!=null){
				strBonusPeriod = strBonusPeriod.replaceAll("\\[", "");
				strBonusPeriod = strBonusPeriod.replaceAll("\\]", "");
				strBonusPeriod = strBonusPeriod.replaceAll(", ", ",");
				arrMonth = strBonusPeriod.split(",");
				
				if(arrMonth!=null && ArrayUtils.contains(arrMonth, nPayMonth+"")>=0){
					isBonusCalculation = true;
				}
			}
			
			if(strBonusSalary!=null){
				int index = strBonusSalary.lastIndexOf(",");
				strBonusSalary = strBonusSalary.substring(0, index);
			}
			
			
			
			
			
			String []arrSalary = null;
			if(strBonusSalary!=null){
				arrSalary = strBonusSalary.split(",");
			}
			
			double dblAmount = 0;
			double dblSalaryAmount = 0;
			
			
			if(uF.parseToInt(strSalaryCalculation)==2){ // 2 is for cumulative
				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where salary_head_id in ("+strBonusSalary+") and emp_id = ? and financial_year_from_date=? and financial_year_to_date=? and paid_to<=? and paid_to>=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				
				if(uF.parseToInt(strSalaryCalculation)==2){// 2 is for previous year
					
					Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
					cal.add(Calendar.YEAR, -1);
					String strPrevDate = uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.DATE) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
					String []arrFinancialYear = CF.getFinancialYear(con, strPrevDate, CF, uF);
					pst.setDate(2, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strPrevDate, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT)); // This condittion  needs to be modified if Bonus is paid 2nd or 3rd time.
				}else{
					String []arrFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
					pst.setDate(2, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT)); // This condittion  needs to be modified if Bonus is paid 2nd or 3rd time.
				}
				
				while(rs.next()){
					dblSalaryAmount = uF.parseToDouble(rs.getString("amount"));
				}
				rs.close();
				pst.close();
			}else{
				
				/**
				 * If current salary is considered for previous months, then the condition needs to be added here.
				 * */
				for(int i=0; arrSalary!=null && i<arrSalary.length; i++){
					
					hmTemp = (Map)hmTotal.get(arrSalary[i]);
					if(hmTemp==null)hmTemp = new HashMap();
					dblSalaryAmount  += uF.parseToDouble((String)hmTemp.get("AMOUNT"));
				}
			}
			
			
			
			
			
			if(isBonusCalculation && nDays>=dblMinimumBonusDays){
			
				double dblCalculatedAmount = 0;
				if("A".equalsIgnoreCase(strBonusType)){
					dblAmount = dblBonusAmount;
				}else{
	//				dblAmount = (dblBonusAmount * (dblBasic + dblDA)) / 100;
					dblAmount = (dblBonusAmount * dblSalaryAmount) / 100;
					
				}
	//			dblCalculatedAmount = 12 * dblAmount;
				dblCalculatedAmount = dblAmount;
				
				if(dblTotalGross<=dblMinimumBonus){
					dblBonusCalculatedAmount = dblTotalGross;
				}else if(dblMinimumBonus<=dblTotalGross  && dblTotalGross<= dblMaximumBonus){
					dblBonusCalculatedAmount = dblMinimumBonus;
				}
				
				
				if(dblCalculatedAmount> dblMaximumBonus){
					dblBonusCalculatedAmount = dblMaximumBonus;
				}else{
					dblBonusCalculatedAmount = dblCalculatedAmount;
				}
				
				
	//			if(arrMonth!=null && arrMonth.length>0){
	//				dblBonusCalculatedAmount = dblBonusCalculatedAmount / arrMonth.length;
	//			}
			}
			
			dblBonusCalculatedAmount += uF.parseToDouble(hmIndividualBonus.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblBonusCalculatedAmount;
	}
	
	public double getArearCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, Map<String, String>> hmArearAmountMap, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblMonthlyAmount = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			
			
			
			Map hmArearMap = (Map)hmArearAmountMap.get(strEmpId);
			if(hmArearMap==null)hmArearMap=new HashMap();
			
			
			double dblBalanceAmount = uF.parseToDouble((String)hmArearMap.get("AMOUNT_BALANCE"));
			dblMonthlyAmount = uF.parseToDouble((String)hmArearMap.get("MONTHLY_AREAR"));
			
			if((dblBalanceAmount-dblMonthlyAmount) >0 && (dblBalanceAmount-dblMonthlyAmount) < 1){
				dblMonthlyAmount = dblBalanceAmount;
			}
				
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblMonthlyAmount;
	}
	
	
	public double getIncentivesCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIncentives, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIncentiveAmount = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			dblIncentiveAmount = uF.parseToDouble(hmIncentives.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblIncentiveAmount;
	}
	
	public double getIndividualOtherDeductionCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIndividualOtherDeduction, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIndividualOtherDeductionAmount = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			dblIndividualOtherDeductionAmount = uF.parseToDouble(hmIndividualOtherDeduction.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblIndividualOtherDeductionAmount;
	}
	
	public double calculateServiceTax(Connection con, UtilityFunctions uF, String strEmpId, double dblGross, String strStateId, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherTaxDetails, Map<String, String> hmEmpServiceTaxMap, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblServiceTaxAmount = 0;
		double dblCess1Amount = 0;
		double dblCess2Amount = 0;
		
		try {
			
			if(!hmEmpServiceTaxMap.containsKey(strEmpId))return 0;
			
			double dblServiceTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_SERVICE_TAX"));
			double dblEduTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_EDU_TAX"));
			double dblSTDTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_STD_TAX"));
			
			dblServiceTaxAmount = (dblGross * dblServiceTax)/100;
			dblCess1Amount = (dblServiceTaxAmount * dblEduTax)/100;
			dblCess2Amount = (dblServiceTaxAmount * dblSTDTax)/100;
			
			dblServiceTaxAmount = dblServiceTaxAmount + dblCess1Amount + dblCess2Amount;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblServiceTaxAmount;
	}
	
	public double getIndividualOtherEarningCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIndividualOtherEarning, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIndividualOtherEarningAmount = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			dblIndividualOtherEarningAmount = uF.parseToDouble(hmIndividualOtherEarning.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblIndividualOtherEarningAmount;
	}

	public double getReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmReimbursement, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblReimbursementAmount = 0;
		
		try {
			

			
			
			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			dblReimbursementAmount = uF.parseToDouble(hmReimbursement.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblReimbursementAmount;
	}
	
	
	public double getMobileReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmMobileReimbursement, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblMobileReimbursementAmount = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			dblMobileReimbursementAmount = uF.parseToDouble(hmMobileReimbursement.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblMobileReimbursementAmount;
	}
	
	public double getTravelReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmTravelReimbursement, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblTravelReimbursementAmount = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			dblTravelReimbursementAmount = uF.parseToDouble(hmTravelReimbursement.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblTravelReimbursementAmount;
	}

	
	public double getOtherReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherReimbursement, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblOtherReimbursementAmount = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			dblOtherReimbursementAmount = uF.parseToDouble(hmOtherReimbursement.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblOtherReimbursementAmount;
	}


	public double getMobileRecoveryCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmMobileRecovery, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblMobileRecoveryAmount = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
			double dblTotalGross = dblBasic + dblDA;
			
			dblMobileRecoveryAmount = uF.parseToDouble(hmMobileRecovery.get(strEmpId)) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblMobileRecoveryAmount;
	}

	public double getIncrementCalculationBasic(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmBasicSalaryMap, Map<String, String> hmDASalaryMap, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIncrement = 0;
		
		try {
			

			double dblBasic = uF.parseToDouble((String)hmBasicSalaryMap.get(strEmpId));
//			double dblDA = uF.parseToDouble((String)hmDASalaryMap.get(strEmpId));
//			double dblTotalGross = dblBasic + dblDA;
			
			
			pst = con.prepareStatement("select * from increment_details where increment_from <= ? and  ?<= increment_to and due_month =? ");
			pst.setDouble(1, dblBasic);
			pst.setDouble(2, dblBasic);
			pst.setInt(3, nPayMonth);
			rs = pst.executeQuery();
			while(rs.next()){
				dblIncrement = rs.getDouble("increment_amount");
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dblIncrement;
	}
	
	
	
	public double getIncrementCalculationDA(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmBasicSalaryMap, Map<String, String> hmDASalaryMap, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, CommonFunctions CF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblIncrement = 0;
		
		try {
			

			double dblDA = uF.parseToDouble((String)hmDASalaryMap.get(strEmpId));
//			double dblTotalGross = dblBasic + dblDA;
			
			
			pst = con.prepareStatement("select * from increment_details_da where increment_from <= ? and  ?<= increment_to and due_month like ? ");
			pst.setDouble(1, dblDA);
			pst.setDouble(2, dblDA);
			pst.setString(3, "%"+nPayMonth+",%");
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				if("P".equalsIgnoreCase(rs.getString("increment_amount_type"))){
					double dblIncr = rs.getDouble("increment_amount");
					dblIncrement = dblIncr * dblDA / 100;
				}else{
					dblIncrement = rs.getDouble("increment_amount");
				}
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
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


	public String getWLocation() {
		return wLocation;
	}


	public void setWLocation(String wLocation) {
		this.wLocation = wLocation;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}

	
	public void getVariableAmount(Connection con, UtilityFunctions uF, Map hmVariables, String strPC){
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			pst = con.prepareStatement("select * from otherearning_individual_details where pay_paycycle = ? and is_approved = 1");
			pst.setInt(1, uF.parseToInt(strPC)); 
			rs = pst.executeQuery();
			while(rs.next()){
				hmVariables.put(rs.getString("emp_id")+"_"+rs.getString("salary_head_id")+"_"+rs.getString("earning_deduction"), rs.getString("pay_amount"));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 
	public void getBreakDetails(Connection con, UtilityFunctions uF, Map hmBreaks, Map hmBreakPolicy, String strD1, String strD2){
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			pst = con.prepareStatement("select count(*) as cnt, emp_id, break_type_id from break_application_register where _date between ? and ? and break_type_id in (select break_type_id from leave_break_type) group by emp_id, break_type_id");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmBreaks.put(rs.getString("emp_id")+"_"+rs.getString("break_type_id"), rs.getString("cnt"));
			}
			rs.close();
			pst.close();
			
			if(hmBreaks!=null && hmBreaks.size()>0){
				pst = con.prepareStatement("select * from leave_break_type");
				rs = pst.executeQuery();
				while(rs.next()){
					hmBreakPolicy.put(rs.getString("break_type_id")+"_"+rs.getString("org_id"), rs.getString("ded_amount"));
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
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
	
	public boolean isHalfDay(String strDate, double dblEarlyLate, String strINOUT, String strLocationId, UtilityFunctions uF, Connection con){
		boolean isHalfDay = false;
		
		PreparedStatement pst=null;
		ResultSet rs = null;
		
		try {
			
			if(dblEarlyLate==0)return false;
			
			double  dblValue = dblEarlyLate * 60;
			int days=0;
			
			
			if("IN".equalsIgnoreCase(strINOUT)){
				pst = con.prepareStatement("select * from roster_halfday_policy where time_value < ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			}else{
				pst = con.prepareStatement("select * from roster_halfday_policy where -time_value > ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
				pst.setDouble(1, uF.convertHoursIntoMinutes(dblValue));
				pst.setString(2, strINOUT);
				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(strLocationId));
			}

			rs = pst.executeQuery();
			while(rs.next()){
				if("IN".equalsIgnoreCase(strINOUT)){
					halfDayCountIN++;
				}else{
					halfDayCountOUT++;
				}
				days = rs.getInt("days");
			}
			rs.close();
			pst.close();
			
			if(days==halfDayCountIN && halfDayCountIN>0){
				halfDayCountIN=0;
				isHalfDay = true;
			}
			
			if(days==halfDayCountOUT && halfDayCountOUT>0){
				halfDayCountOUT=0;
				isHalfDay = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return isHalfDay;
	}


	public String getF_department() {
		return f_department;
	}
	public void setF_department(String f_department) {
		this.f_department = f_department;
	}
	public String getF_service() {
		return f_service;
	}
	public void setF_service(String f_service) {
		this.f_service = f_service;
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
	public String getStrPC() {
		return strPC;
	}
	public void setStrPC(String strPC) {
		this.strPC = strPC;
	}


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


	public void calculateETDS(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map hmVariables, boolean isInsert,Map<String, String> hmOtherTaxDetails,Map<String, String> hmEmpLevelMap) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			
			double dblEduCess = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
			double dblSTDCess = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
			double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
			
			double dblTDSMonth = 0;
			double dblActual=0;
			double dblEDuTax=0;
			double dblSTDTax=0;
			double dblflatTds=0;
			
			Map<String, String> hmArrearInner =(Map<String, String>)hmTotal.get(TDS+"");
			if(hmArrearInner==null) hmArrearInner=new HashMap<String, String>();
			
			if(uF.parseToBoolean((String)hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))){
//				dblTDSMonth = dblGross * dblFlatTDS / 100;
				
				dblActual= uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
				dblTDSMonth=dblActual;
				dblflatTds=dblActual;
			}else{				
				dblActual= uF.parseToDouble((String)hmArrearInner.get("AMOUNT"));
								
				dblTDSMonth=dblActual/(1+(dblEduCess/100)+(dblSTDCess/100));
				
				dblEDuTax = dblTDSMonth * (dblEduCess/100);
				dblSTDTax = dblTDSMonth * (dblSTDCess/100);
			}
			
			if(isInsert){
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
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void setCF(CommonFunctions CF){
		this.CF=CF;
	}
}




class ApproveArrearRunnable implements Runnable, IConstants{
	
	ApproveArrear objApproveArrear;
	GenerateSalarySlip gs;
	
	Map hmTotal;
	Connection con;
	UtilityFunctions uF;
	CommonFunctions CF;
	String strFinancialYearStart;
	String strFinancialYearEnd;
	String strEmpId;
	String strOrgId;
	String []strApprovePayCycle;
	Map hmEmpStateMap;
	Map hmCurrencyDetails;
	Map hmEmpCurrency;
	Map hmVariables;
	HttpServletRequest request;
	HttpServletResponse response;
	double dblTotal;
	
	Map<String, String> hmOtherTaxDetails;
	Map<String, String> hmEmpLevelMap;
	
	public ApproveArrearRunnable(ApproveArrear objApproveArrear, GenerateSalarySlip gs, Connection con, UtilityFunctions uF, CommonFunctions CF, String strFinancialYearStart, String strFinancialYearEnd, String []strApprovePayCycle, Map hmEmpStateMap, Map hmCurrencyDetails, Map hmEmpCurrency, Map hmVariables, HttpServletRequest request, HttpServletResponse response,Map<String, String> hmOtherTaxDetails,Map<String, String> hmEmpLevelMap){
		this.objApproveArrear = objApproveArrear;
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
	}
	
	
	public void setData(Map hmTotal, String strEmpId, double dblTotal, String strOrgId) {
		this.hmTotal = hmTotal;
		this.strEmpId = strEmpId;
		this.dblTotal = dblTotal;
		this.strOrgId = strOrgId;
	}
	
	public void run(){

		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_EPF+"")){
			objApproveArrear.calculateEEPF(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true);
			objApproveArrear.calculateERPF(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true);
		}
		
		
		
		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_ESI+"")){
			objApproveArrear.calculateEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true);
		}
		
		
		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_LWF+"")){
			objApproveArrear.calculateELWF(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true, strOrgId);
		}
		
		if(hmTotal!=null && hmTotal.containsKey(TDS+"")){
			objApproveArrear.calculateETDS(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true,hmOtherTaxDetails,hmEmpLevelMap);
		}
		
	}
	
}
