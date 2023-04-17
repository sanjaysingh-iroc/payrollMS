//package com.konnect.jpms.payroll;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.TimeZone;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.apache.log4j.Logger;
//import org.apache.struts2.interceptor.ServletRequestAware;
//import org.apache.struts2.interceptor.ServletResponseAware;
//
//import com.konnect.jpms.employee.EmployeeActivity;
//import com.konnect.jpms.export.GenerateSalarySlip;
//import com.konnect.jpms.select.FillBank;
//import com.konnect.jpms.select.FillDepartment;
//import com.konnect.jpms.select.FillLevel;
//import com.konnect.jpms.select.FillOrganisation;
//import com.konnect.jpms.select.FillPayCycleDuration;
//import com.konnect.jpms.select.FillPayCycles;
//import com.konnect.jpms.select.FillPayMode;
//import com.konnect.jpms.select.FillServices;
//import com.konnect.jpms.select.FillWLocation;
//import com.konnect.jpms.util.ArrayUtils;
//import com.konnect.jpms.util.CommonFunctions;
//import com.konnect.jpms.util.Database;
//import com.konnect.jpms.util.IConstants;
//import com.konnect.jpms.util.IStatements;
//import com.konnect.jpms.util.Notifications;
//import com.konnect.jpms.util.UtilityFunctions;
//import com.opensymphony.xwork2.ActionSupport;
//
//
//public class ApprovePayroll1 extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
//	
//
//	/**
//	 *   
//	 */  
//	private static final long serialVersionUID = 1L;
//	HttpSession session;
//	String strEmpID;   
//	String strUserType; 
//	String strSessionEmpId;      
//   
//	String strFrmD1 = null; 
//	String strFrmD2 = null;  
//	String strD1 = null;
//	String strD2 = null;
//	String strPC = null; 
//	String approvePC = null;
//	String strAlpha = null;
//
//	CommonFunctions CF = null;
//
//	private static Logger log = Logger.getLogger(ApprovePayroll1.class);
//	
//	
//	String strVeryEmpId;
//	public String execute() throws Exception {
//
//		UtilityFunctions uF = new UtilityFunctions();
//		session = request.getSession();
//		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
//		if (CF == null)
//			return LOGIN;
//
//		
//		strUserType = (String) session.getAttribute(USERTYPE);
//		strSessionEmpId = (String) session.getAttribute(EMPID);
//		strVeryEmpId = (String)request.getParameter("strVeryEmpId");
//		
//		
//		String APPROVE = (String) request.getParameter("approve"); 
//		strAlpha = (String) request.getParameter("alphaValue");
//		setStrAlpha(strAlpha);
//
//		request.setAttribute(TITLE, "Approve Compensation");
//		strEmpID = (String) request.getParameter("EMPID");
//		String strEmpType = (String) session.getAttribute("USERTYPE");
//
//		String[] strPayCycleDates = null;
//	
//		
//		
//		
//		
//		
//		if(getApprovePC()!=null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length()>0){
//			strPayCycleDates = getApprovePC().split("-");
//			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//		}else if (getPaycycle() != null) {
//			strPayCycleDates = getPaycycle().split("-");
////			strPayCycleDates = CF.getPrevPayCycle(strPayCycleDates[1], CF.getStrTimeZone(), CF);
//			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//		} else {
//			strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
//			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//		}
//		
//		
//		if(getWLocation()!=null){
//			setWLocation(getWLocation());
//		}
//		
//		if(getLevel()!=null){
//			setLevel(getLevel());
//		}
//		
//
//		strD1 = strPayCycleDates[0];
//		strD2 = strPayCycleDates[1];
//		strPC = strPayCycleDates[2];
//		
//		
//		
//		
//		
////		System.out.println("strPC="+strPC +" strD1="+strD1+"   strD2="+strD2);
//		
//		String referer = request.getHeader("Referer");
//
//		if (referer != null) {
//			int index1 = referer.indexOf(request.getContextPath());
//			int index2 = request.getContextPath().length();
//			referer = referer.substring(index1 + index2 + 1);
//		}
//		setRedirectUrl(referer);
//
//		
//		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
//		
//		
//		
//			request.setAttribute(PAGE, PApprovePayroll);
//			strEmpID = (String) session.getAttribute(EMPID);
//			if(getF_org()==null){
//				setF_org((String)session.getAttribute(ORGID));
//			}
//			if(getStrPaycycleDuration()==null){
//				setStrPaycycleDuration("M");
//			}
//			loadClockEntries(uF);
//			
////			viewClockEntriesForPayrollApproval(CF, null, strD1, strD2);
//			if (APPROVE != null) {
//
//				request.setAttribute(PAGE, PReportClockManager);
//				request.setAttribute(MESSAGE, "Payroll generated");
//				approvePayrollEntries(CF);
//				
////				com.konnect.jpms.export.PaySlips p = new com.konnect.jpms.export.PaySlips();
////				p.setServletRequest(request);
////				p.execute(getDtMin(), getDtMax());
//
//				return SUCCESS;
//			}
//			viewClockEntriesForPayrollApproval(CF, null, strD1, strD2);
//			
//		
//		
//		return LOAD;
//
//	}
//
//	
//	public String loadClockEntries(UtilityFunctions uF) {
//		//paycycleList = new FillPayCycles(getStrPaycycleDuration()).fillPayCycles(CF, getF_org());
//		paycycleList = new FillPayCycles(getStrPaycycleDuration()).fillPayCycles(CF);
////		wLocationList = new FillWLocation().fillWLocation();
//		departmentList = new FillDepartment(request).fillDepartment();
//		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
//		serviceList = new FillServices(request).fillServices();
//		paycycleDurationList = new FillPayCycleDuration().fillPayCycleDuration();
//		paymentModeList = new FillPayMode().fillPaymentMode();
//		organisationList = new FillOrganisation(request).fillOrganisation();
//		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//		
//		return LOAD;
//	}
//
////	List alWorkedDates = new ArrayList();
//	Map hmEmpRosterLunchDeduction = new HashMap();
//	Map<String, Map<String, String>> hmLeavesMap = null;
//	Map<String, String> hmLeaves = null;
//	
//	public String approvePayrollEntries(CommonFunctions CF) {
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
//			
//			
//			 
//			con = db.makeConnection(con);
//
//			Map hmCurrencyDetails = CF.getCurrencyDetails(con);
//			pst = con.prepareStatement(selectSettings);
//			rs = pst.executeQuery();
//			String strFinancialYearEnd = null;
//			String strFinancialYearStart = null;
//			
//			while(rs.next()){
//				
//				if(rs.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_END)){
//					strFinancialYearEnd = rs.getString("value");
//				}
//				if(rs.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_START)){
//					strFinancialYearStart = rs.getString("value");
//				}
//				
//			}
//			rs.close();
//			pst.close();
//			
//			String []strApprovePayCycle = null;
//			
//			if(getApprovePC()!=null && !getApprovePC().equalsIgnoreCase("NULL") && getApprovePC().length()>0){
//				strApprovePayCycle = getApprovePC().split("-");
//				setPaycycle(getApprovePC());
//			}else{
//				strApprovePayCycle = getPaycycle().split("-");
//				setPaycycle(getPaycycle());
//			}
//			
//			
//			if(strApprovePayCycle==null){
//				strApprovePayCycle = new String[3];
//			}
//			
//			
//			/*LinkedHashMap hmTotalSalary = (LinkedHashMap)request.getAttribute("hmTotalSalary");
//			Map hmEmpNameMap = (Map)request.getAttribute("hmEmpNameMap");
//			Map hmSalaryDetails = (Map)request.getAttribute("hmSalaryDetails");
//			Map hmEmpSalary = (Map)request.getAttribute("hmEmpSalary");
//			Map hmServices = (Map)request.getAttribute("hmServices");
//			List alEmp = (List)request.getAttribute("alEmp");
//			List alProcessingEmployee = (List)request.getAttribute("alProcessingEmployee");
//			Map<String, String> hmPresentDays = (Map)request.getAttribute("hmPresentDays");
//			Map<String, String> hmPaidDays = (Map)request.getAttribute("hmPaidDays");
//			Map hmLeaveDays 	= (Map)request.getAttribute("hmLeaveDays");
//			Map hmLeaveTypeDays 	= (Map)request.getAttribute("hmLeaveTypeDays");
//			Map<String, String> hmMonthlyLeaves 	= (Map)request.getAttribute("hmMonthlyLeaves");
//			Map<String, String> hmLoanAmt 	= (Map)request.getAttribute("hmLoanAmt");
//			Map<String, String> hmEmpPaymentMode 	= (Map)request.getAttribute("hmEmpPaymentMode");
//			Map<String, String> hmEmpStateMap 	= (Map)request.getAttribute("hmEmpStateMap");
//			Map<String, String> hmVariables 	= (Map)request.getAttribute("hmVariables");
//			
//			double dbTotalDays = uF.parseToDouble((String)request.getAttribute("strTotalDays"));
//			List alEmpSalaryDetailsEarning = (List)request.getAttribute("alEmpSalaryDetailsEarning");
//			List alEmpSalaryDetailsDeduction = (List)request.getAttribute("alEmpSalaryDetailsDeduction");
//
//			*/
//			
//			
//			
//			
//			
//			LinkedHashMap hmTotalSalary = (LinkedHashMap)session.getAttribute("AP_hmTotalSalary");
//			Map hmEmpNameMap = (Map)session.getAttribute("AP_hmEmpNameMap");
//			Map hmSalaryDetails = (Map)session.getAttribute("AP_hmSalaryDetails");
//			Map hmEmpSalary = (Map)session.getAttribute("AP_hmEmpSalary");
//			Map hmServices = (Map)session.getAttribute("AP_hmServices");
//			List alEmp = (List)session.getAttribute("AP_alEmp");
//			List alProcessingEmployee = (List)session.getAttribute("AP_alProcessingEmployee");
//			Map<String, String> hmPresentDays = (Map)session.getAttribute("AP_hmPresentDays");
//			Map<String, String> hmPaidDays = (Map)session.getAttribute("AP_hmPaidDays");
//			Map hmLeaveDays 	= (Map)session.getAttribute("AP_hmLeaveDays");
//			Map hmLeaveTypeDays 	= (Map)session.getAttribute("AP_hmLeaveTypeDays");
//			Map<String, String> hmMonthlyLeaves 	= (Map)session.getAttribute("AP_hmMonthlyLeaves");
//			Map<String, String> hmLoanAmt 	= (Map)session.getAttribute("AP_hmLoanAmt");
//			Map<String, String> hmEmpPaymentMode 	= (Map)session.getAttribute("AP_hmEmpPaymentMode");
//			Map<String, String> hmEmpStateMap 	= (Map)session.getAttribute("AP_hmEmpStateMap");
//			Map<String, String> hmOtherTaxDetails 	= (Map)session.getAttribute("AP_hmOtherTaxDetails");
//			Map<String, String> hmVariables 	= (Map)session.getAttribute("AP_hmVariables"); 
//			
//			Map<String, String> hmEmpLevelMap 	= (Map)session.getAttribute("AP_hmEmpLevelMap"); 
//			
//			double dbTotalDays = uF.parseToDouble((String)session.getAttribute("AP_strTotalDays"));
//			List alEmpSalaryDetailsEarning = (List)session.getAttribute("AP_alEmpSalaryDetailsEarning");
//			List alEmpSalaryDetailsDeduction = (List)session.getAttribute("AP_alEmpSalaryDetailsDeduction");
//			
//			
//			
//			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
//			
//			
////			System.out.println("=====================   APPROVING   =====================");
////			System.out.println("hmTotalSalary====>"+hmTotalSalary);
////			System.out.println("hmSalaryDetails====>"+hmSalaryDetails);
////			System.out.println("alEmpSalaryDetailsEarning====>"+alEmpSalaryDetailsEarning);
//			
//			
//			log.debug("hmServices===>"+hmServices);
//			
//			GenerateSalarySlip gs = new GenerateSalarySlip();
//			ApprovePayrollRunnable1 objRunnable = new ApprovePayrollRunnable1(this, gs, con, uF, CF, strFinancialYearStart, strFinancialYearEnd, strApprovePayCycle, hmEmpStateMap, hmCurrencyDetails, hmEmpCurrency, hmVariables, request, response,hmOtherTaxDetails,hmEmpLevelMap);
//			
//			
//			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
//			int count=-1; 
//			Set set0 = hmTotalSalary.keySet();
//			Iterator it0 = set0.iterator();
//			while(it0.hasNext()){
//				String strEmpId = (String)it0.next();
//				String strOrgId = hmEmpOrgId.get(strEmpId);
//				Map hmTotal = (Map)hmTotalSalary.get(strEmpId);
//				
//				List alServices = (List)hmServices.get(strEmpId);
//				if(alServices==null)alServices=new ArrayList();
//				
//				String arr[] = getChbox();
//				int x = ArrayUtils.contains(arr, strEmpId);
//				if(x<0)continue;
//				
////				count = alEmp.indexOf(strEmpId);
//				count = alProcessingEmployee.indexOf(strEmpId);
//				
//				
//				
////				System.out.println("alProcessingEmployee===>"+alProcessingEmployee);
////				System.out.println(count+"  alEmp==>"+alEmp);
//				
//				
//				Map hmLeaves = (Map)hmLeaveDays.get(strEmpId);
//				if(hmLeaves==null)hmLeaves = new HashMap();
//				
//				Map hmLeavesType = (Map)hmLeaveTypeDays.get(strEmpId);
//				if(hmLeavesType==null)hmLeavesType = new HashMap();
//				
//				
//				
//				double dblPresentDays = uF.parseToDouble(hmPresentDays.get(strEmpId));
//				double dblPaidLeaveDays = uF.parseToDouble((String)hmLeavesType.get("COUNT"));
//				double dblPaidDays = uF.parseToDouble(hmPaidDays.get(strEmpId));
//				
//				double dblTotal = 0.0;
//				
//				for(int i=0;i<alEmpSalaryDetailsEarning.size(); i++){
//					String strSalaryId = (String)alEmpSalaryDetailsEarning.get(i);
//					
//					
//					// As Bonus is paid independent of paycycle...
//					if(uF.parseToInt(strSalaryId)==BONUS && !uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())){
//						continue;
//					}
//					
//					
//					if(hmTotal!=null && !hmTotal.containsKey(strSalaryId)){
//						continue;
//					}
//					
//					pst = con.prepareStatement(insertPayrollGeneration);
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
//					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
//					pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
//					pst.setInt(6, uF.parseToInt(strSalaryId));
//					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotal.get(strSalaryId)))));
//					pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
//					pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//					pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//					pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
//					pst.setInt(12, uF.parseToInt(((alServices.size()>0)?(String)alServices.get(0):"0")));
//					pst.setString(13,"E");
//					pst.setString(14,getStrPaycycleDuration()); 
//					pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
//					pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
//					pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
//					
//					
//					pst.setDouble(18, dblPresentDays);
//					pst.setDouble(19, dblPaidDays);
//					pst.setDouble(20, dblPaidLeaveDays);
//					pst.setDouble(21, dbTotalDays);
//					
//					pst.execute();
//					
//					
//					dblTotal += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotal.get(strSalaryId))));
//					
//					
//					
//					
//					double dblAmt = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotal.get(strSalaryId))));
//					
//					if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==REIMBURSEMENT && dblAmt>0){
//						
//						pst = con.prepareStatement(updateReimbursementPayroll);
//						
//						pst.setBoolean(1, true);
//						pst.setInt(2, uF.parseToInt(strSessionEmpId));
//						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setInt(4, uF.parseToInt(strEmpId));
////						pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
////						pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//						pst.execute();
//						
////						System.out.println("update reimbursement===>"+pst);
//					}
//					
//					
//					if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==TRAVEL_REIMBURSEMENT && dblAmt>0){
//						
//						pst = con.prepareStatement(updateReimbursementPayroll1);
//						
//						pst.setBoolean(1, true);
//						pst.setInt(2, uF.parseToInt(strSessionEmpId));
//						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setInt(4, uF.parseToInt(strEmpId));
////						pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
////						pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//						pst.execute();
//						
////						System.out.println("update reimbursement===>"+pst);
//					}
//					
//					if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==MOBILE_REIMBURSEMENT && dblAmt>0){
//						
//						pst = con.prepareStatement(updateReimbursementPayroll2);
//						
//						pst.setBoolean(1, true);
//						pst.setInt(2, uF.parseToInt(strSessionEmpId));
//						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setInt(4, uF.parseToInt(strEmpId));
////						pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
////						pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//						pst.execute();
//						
////						System.out.println("update reimbursement===>"+pst);
//					}
//					
//					if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==OTHER_REIMBURSEMENT && dblAmt>0){
//						
//						pst = con.prepareStatement(updateReimbursementPayroll3);
//						
//						pst.setBoolean(1, true);
//						pst.setInt(2, uF.parseToInt(strSessionEmpId));
//						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setInt(4, uF.parseToInt(strEmpId));
////						pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
////						pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
//						pst.execute();
//						
////						System.out.println("update reimbursement===>"+pst);
//					}
//					
//					
//				}
//				
//				for(int i=0;i<alEmpSalaryDetailsDeduction.size(); i++){
//					String strSalaryId = (String)alEmpSalaryDetailsDeduction.get(i);
//
//
//					if(hmTotal!=null && !hmTotal.containsKey(strSalaryId)){
//						continue;
//					}
//					
////					pst = con.prepareStatement("insert into payroll_generation (emp_id,month,year,pay_date,entry_date,salary_head_id,amount,paycycle, financial_year_from_date, financial_year_to_date, currency_id) values (?,?,?,?,?,?,?,?,?,?,?)");
//					pst = con.prepareStatement(insertPayrollGeneration);
//					
//					pst.setInt(1, uF.parseToInt(strEmpId));
//					pst.setInt(2, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM")));
//					pst.setInt(3, uF.parseToInt(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "yyyy")));
//					pst.setDate(4, uF.getCurrentDate(CF.getStrReportDateFormat()));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrReportDateFormat()));
//					pst.setInt(6, uF.parseToInt(strSalaryId));
//					pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotal.get(strSalaryId)))));
//					pst.setInt(8, uF.parseToInt(strApprovePayCycle[2]));
//					pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//					pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//					pst.setInt(11, uF.parseToInt(hmEmpCurrency.get(strEmpId)));
//					pst.setInt(12, uF.parseToInt(((alServices.size()>0)?(String)alServices.get(0):"0")));
//					pst.setString(13,"D");
//					pst.setString(14,getStrPaycycleDuration());
//					pst.setDate(15, uF.getDateFormat(strApprovePayCycle[0], DATE_FORMAT));
//					pst.setDate(16, uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT));
//					pst.setInt(17, uF.parseToInt(hmEmpPaymentMode.get(strEmpId)));
//					pst.setDouble(18, dblPresentDays);
//					pst.setDouble(19, dblPaidDays);
//					pst.setDouble(20, dblPaidLeaveDays);
//					pst.setDouble(21, dbTotalDays);
//					pst.execute();
//					 
//					log.debug("Inserting  D  ==== "+pst);
//					log.debug("Inserting  D  ==== "+hmTotal);
//					
//					dblTotal -= uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotal.get(strSalaryId))));
//					
//					double dblAmt = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotal.get(strSalaryId))));
//					
//					if(getApprovePC()!=null && uF.parseToInt(strSalaryId)==LOAN && dblAmt>0){
//						
//						pst = con.prepareStatement(selectLoanPyroll);
//						pst.setInt(1, uF.parseToInt(strEmpId));
//						pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//						rs = pst.executeQuery();
//						
//						double dblBalanceAmt = 0;
//						int nLoanId = 0;
//						int nLoanAppId = 0;
//						while(rs.next()){
//							
//							dblBalanceAmt = rs.getDouble("balance_amount");
//							nLoanId = rs.getInt("loan_id");
//							nLoanAppId = rs.getInt("loan_applied_id");
//							
//						
////						dblBalanceAmt = dblBalanceAmt - dblAmt;
//						
//						
////						System.out.println("hmLoanAmt==>"+hmLoanAmt);
//						
////						Set set = hmLoanAmt.keySet();
////						Iterator it = set.iterator();
////						while(it.hasNext()){
////							String strLoanId = (String)it.next();
//							double dblAmt1 = uF.parseToDouble(hmLoanAmt.get(nLoanAppId+""));
//							dblBalanceAmt = dblBalanceAmt - dblAmt1;
//							
//							/*pst = con.prepareStatement(updateLoanPyroll);
//							
//							
//							pst.setDouble(1, dblBalanceAmt);
//							if(dblBalanceAmt>0){
//								pst.setBoolean(2, false);
//							}else{
//								pst.setBoolean(2, true);	
//							}
//							pst.setInt(3, uF.parseToInt(strEmpId));
//							pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//							pst.execute();*/
//							
//							
//							pst = con.prepareStatement(updateLoanPyroll1);
//							
//							
//							pst.setDouble(1, dblBalanceAmt);
//							if(dblBalanceAmt>0){
//								pst.setBoolean(2, false);
//							}else{
//								pst.setBoolean(2, true);	
//							}
//							pst.setInt(3, nLoanAppId);
//							pst.execute();
//							
////							System.out.println("Loan====updateLoanPyroll1=====+>"+pst);
//							
//							
//							pst = con.prepareStatement(insertLoanPyroll);
//							pst.setInt(1, uF.parseToInt(strEmpId));
//							pst.setInt(2, nLoanId);
//							pst.setDouble(3, dblAmt1);
//							pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
//							pst.setString(5, "S");   
//							pst.setInt(6, nLoanAppId);
//							pst.setDate(7, uF.getDateFormat(strD1, DATE_FORMAT));
//							pst.setDate(8, uF.getDateFormat(strD2, DATE_FORMAT));
//							pst.execute();
//							
//							
////							System.out.println("Loan====insertLoanPyroll=====+>"+pst);
//							
//						}
//					
//					}
//					
//					
//					
//				}
//				
//				
//				
//				objRunnable.setData(hmTotal, strEmpId, dblTotal, strOrgId);
//				Thread t = new Thread(objRunnable);
//				t.start();
//				
//				/**
//				 * 
//				 * UPDATE INFO for challans 
//				 * 
//				 * **/
//				
//				
//				
//				/*if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_EPF+"")){
//					calculateEEPF(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true);
//					calculateERPF(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true);
//				}
//				
//				if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_ESI+"")){
//					calculateEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, true);
//				}*/
//				
//				
//				
//		/*		pst = con.prepareStatement(selectLeaveRegisterPyroll);
//				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//				rs = pst.executeQuery();
//				Map<String, String> hmAccruedLeaves = new HashMap<String, String>();
//				Map<String, String> hmMonthLeaves = new HashMap<String, String>();
//				Map<String, String> hmBalanceLeaves = new HashMap<String, String>();
//				while(rs.next()){
//					hmAccruedLeaves.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued_leaves"));
//					hmMonthLeaves.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("monthly_leaves"));
//					hmBalanceLeaves.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_balance"));
//				}*/
//				
////				System.out.println("hmMonthlyLeaves=="+hmMonthlyLeaves);
////				System.out.println("hmBalanceLeaves=="+hmBalanceLeaves);
//				
//				
//				/*Set set = hmMonthlyLeaves.keySet();
//				Iterator it = set.iterator();
//				while(it.hasNext()){
//					String str = (String)it.next();
//					String []arr1 = null;   
//					String employeeId = null;
//					String leaveId = null;
//					if(str!=null){   
//						arr1 = str.split("_");
//						employeeId = arr1[0];
//						leaveId = arr1[1];
//					}
//					
//					double dblAccruedLeaves = uF.parseToDouble(hmAccruedLeaves.get(employeeId+"_"+leaveId));
//					double dblThisMonthLeaves = uF.parseToDouble(hmMonthlyLeaves.get(employeeId+"_"+leaveId));
//					double dblMonthLeaves = uF.parseToDouble(hmMonthLeaves.get(employeeId+"_"+leaveId));
//					double dblBalanceLeaves = uF.parseToDouble(hmBalanceLeaves.get(employeeId+"_"+leaveId));
//					
//					
//					double dblBalance = 0;
//					
//					dblBalance = dblBalanceLeaves-dblThisMonthLeaves;
//					if(dblBalance<0){
//						dblBalance = 0;
//					}
//					
//					pst = con.prepareStatement("update leave_register set leave_balance =? , monthly_leaves=? where emp_id = ? and leave_type_id =? and from_date<= ? and to_date>= ? ");
//					pst.setDouble(1, (dblAccruedLeaves - dblMonthLeaves - dblThisMonthLeaves));
//					pst.setDouble(2, (dblMonthLeaves + dblThisMonthLeaves));
//					pst.setInt(3, uF.parseToInt(employeeId));
//					pst.setInt(4, uF.parseToInt(leaveId));
//					pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.execute();
//					
////					System.out.println("pst=="+pst);
//					
//				}*/
//				
//				
//				
//				
//				
//				
//				Map<String, String> hmInnerCurrencyDetails = (Map<String, String>)hmCurrencyDetails.get(hmEmpCurrency.get(strEmpId)) ;
//				if(hmInnerCurrencyDetails==null)hmInnerCurrencyDetails=new HashMap<String, String>();
//				
//				String strDomain = request.getServerName().split("\\.")[0];
//				Notifications nF = new Notifications(N_NEW_SALARY_APPROVED, CF);
//				nF.setDomain(strDomain); 
//				db.setRequest(request);
//				nF.setStrEmpId(strEmpId);
//				nF.setStrSalaryAmount(uF.showData(hmInnerCurrencyDetails.get("LONG_CURR"),"")+""+uF.formatIntoTwoDecimal(dblTotal));
//				nF.setStrPaycycle(strApprovePayCycle[0]+"-"+strApprovePayCycle[1]);
//				nF.sendNotifications();
//				
//				/*
//				GenerateSalarySlip gs = new GenerateSalarySlip();
//				
//				gs.setStrEmpId(strEmpId);
////				gs.setStrServiceId(((alServices.size()>0)?(String)alServices.get(0):"0"));
//				gs.setStrServiceId(0+"");
//				gs.setStrMonth(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM"));
//				gs.setStrPC(strApprovePayCycle[2]);
//				gs.setStrFYS(strFinancialYearStart);
//				gs.setStrFYE(strFinancialYearEnd);				
//				gs.setAttachment(true);				
//				gs.setServletRequest(request);
//				gs.setServletResponse(response);
//				gs.execute();*/
//				
//				
//				
//			}   
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//
//	}
//
//	public String viewClockEntriesForPayrollApproval(CommonFunctions CF, String strReqEmpId, String strD1, String strD2) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//			
//			this.strD1 = strD1;
//			this.strD2 = strD2;
//			
//			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
//			
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
//			
//			
//			
//			
//			
//			
////			int nTotalNumberOfDays = uF.parseToInt(uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
//			
//			
//			con = db.makeConnection(con);
//			
//			if(getWLocation()==null && session!=null){
//				setWLocation((String)session.getAttribute(WLOCATIONID));
//			}
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
//			 
////			System.out.println("pst===>"+pst);
//			
//			
//			
////			String strFinancialYearEnd = null;
////			String strFinancialYearStart = null;
//			double dblInvestmentExemption = 0.0d;
//			double dblStandardHrs = 0.0d;
//			
//			/*
//			pst = con.prepareStatement(selectSettings);
//			rs = pst.executeQuery();
//			
//			while(rs.next()){
//				
//				if(rs.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_END)){
//					strFinancialYearEnd = rs.getString("value");
//				}
//				if(rs.getString("options").equalsIgnoreCase(O_FINANCIAL_YEAR_START)){
//					strFinancialYearStart = rs.getString("value");
//				}
//				
//				if(rs.getString("options").equalsIgnoreCase(O_INVESTMENT_EXEMPTION)){
//					dblInvestmentExemption = uF.parseToDouble(rs.getString("value"));
//				}
//				if(rs.getString("options").equalsIgnoreCase(O_EDUCATION_CESS)){
//					dblCess1 = uF.parseToDouble(rs.getString("value"));
//				}
//				if(rs.getString("options").equalsIgnoreCase(O_STANDARD_CESS)){
//					dblCess2 = uF.parseToDouble(rs.getString("value"));
//				}
//				if(rs.getString("options").equalsIgnoreCase(O_STANDARD_FULL_TIME_HOURS)){
//					dblStandardHrs = uF.parseToDouble(rs.getString("value"));
//				}
//				if(rs.getString("options").equalsIgnoreCase(O_FLAT_TDS)){
//					dblFlatTDS = uF.parseToDouble(rs.getString("value"));
//				}
//				
//				if(getStrPaycycleDuration()==null && rs.getString("options").equalsIgnoreCase(O_PAYCYCLE_DURATION)){
//					setStrPaycycleDuration(rs.getString("value"));
//				}
//				
//				
//			}
//			*/
//			pst = con.prepareStatement(selectOrgV);
//			pst.setInt(1, uF.parseToInt(getF_org()));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				if(rs.getString("duration_paycycle")!=null){
//					setStrPaycycleDuration(rs.getString("duration_paycycle"));
//				}
//			}
//			
//			
//			Map hmEmpMertoMap = new HashMap();
//			Map<String, String> hmEmpEndDateMap = new HashMap<String, String>();
//			Map hmWlocationMap = CF.getWorkLocationMap(con);
//			Map hmEmpWlocationMap = new HashMap();
//			Map hmEmpStateMap = new HashMap();
//			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
//			Map hmEmpJoiningMap = CF.getEmpJoiningDateMap(con, uF, hmEmpEndDateMap); 
//			Map hmEmpNameMap = CF.getEmpNameMap(con,null, null); 
//			Map hmEmpCodeMap = CF.getEmpCodeMap(con);
//			Map hmEmpGenderMap = CF.getEmpGenderMap(con);
//			Map hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
//			Map hmEmpExemptionsMap = getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//			Map hmEmpHomeLoanMap = getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map hmEmpPaidSalary = CF.getPaidSalary(con, strFinancialYearStart, strFinancialYearEnd, strPC);
//			Map hmEmpRentPaidMap = getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd); 
////			Map<String, Map<String, String>> hmOverTimeMap = CF.getOverTimeMap(con, CF);
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
//			Map<String, String> hmHolidays = new HashMap<String, String>();
//			Map<String, String> hmHolidayDates = new HashMap<String, String>();
//			CF.getHolidayList(con,request,strD1, strD2, CF, hmHolidayDates, hmHolidays, true);
//			
//			/*Calendar cal1  = GregorianCalendar.getInstance();
//			cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
//			cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
//			String strDateNew = uF.getDateFormat("01/"+(cal1.get(Calendar.MONTH)+1)+"/"+cal1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//			
//			System.out.println("strDateNew=="+strDateNew);
//			System.out.println("strD1=="+strD1);
//			System.out.println("strD2=="+strD2);
//			*/
////			Map hmWeekEnds = CF.getWeekEndDateList(con, strDateNew, strD2, CF, uF);
//			Map hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF, null, null);
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
////			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? and emp_per_id > 0 ");
//			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and (employment_end_date is null OR employment_end_date >= ?) and joining_date<= ? and emp_per_id > 0 ");
//			
//			
//			if(uF.parseToInt(getLevel())>0){
//				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id = "+uF.parseToInt(getLevel())+")");
//			}
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and depart_id ="+uF.parseToInt(getF_department()));
//			}
//			
//			if(uF.parseToInt(getF_service())>0){
//				sbQuery.append(" and service_id  like '%,"+uF.parseToInt(getF_service())+",%'");
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
//			
//			//sbQuery.append(" order by emp_fname, emp_lname");
//			sbQuery.append(" order by empcode");
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
////				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and emp_id =? order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst = con.prepareStatement("select * from attendance_details, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  and ad.emp_id =? order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(strReqEmpId));
//			}else if(sbEmPId.length()>1){
////				pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and emp_id in ("+sbEmPId.toString()+") order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and in_out = 'OUT' and ad.emp_id in ("+sbEmPId.toString()+") order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			}else{
//				//pst = con.prepareStatement("select * from attendance_details where to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
//				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			}
//				
//			
//			
//			Map hmOverLappingHolidays = new HashMap();
//			rs = pst.executeQuery();  
//			double dblOverLappingHolidays = 0;
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
//				
//				
//				hmEmpRosterHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("emp_id"), rs.getString("actual_hours"));
//				
//				
//				String strDay = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
//				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT);
//				strDay = strDay.toUpperCase();
//				String strLocation = (String)hmEmpWlocationMap.get(strPresentEmpIdNew);
//				double dblEarlyLate = rs.getDouble("early_late");
//				String strINOUT = rs.getString("in_out");
//				
//				
//				if(!alPresentDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))){
//					/**
//					 * To avoid the over presence data
//					 */
//					
////					Map hmLeaves = (Map)hmLeaveDays.get(strPresentEmpIdNew);
////					if(hmLeaves==null)hmLeaves = new HashMap();
//					
////					String strWeekEnd = (String)hmWeekEnds.get(strDay+"_"+strLocation);
//					String strWeekEnd = (String)hmWeekEnds.get(strDate+"_"+strLocation);
////					String strWeekEnd = null;
//					
//					
////					
////					if(strPresentEmpIdNew.equalsIgnoreCase("460")){
////						System.out.println("strWeekEnd="+strWeekEnd);
////						System.out.println("strLocation="+strLocation);
////						System.out.println("strDate="+strDate);
////						System.out.println("hmWeekEnds="+hmWeekEnds);
////					}
//					
//					
//					if(strWeekEnd==null ){ //&& !hmLeaves.containsKey(strDate)
//						alPresentDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//					}else if(!alPresentWeekEndDates.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))){
//						alPresentWeekEndDates.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//					}
//					
//					
//					
//					if(hmHolidayDates.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_"+strLocation)){
//						dblOverLappingHolidays++;
//					}
//					
//				}
//
//				
//				
//				Map hmLeaves = (Map)hmLeaveDays.get(strPresentEmpIdNew);
//				if(hmLeaves==null)hmLeaves = new HashMap();
//				
//				boolean isRosterDependent = uF.parseToBoolean((String)hmRosterDependent.get(strPresentEmpIdNew));
//				
////				System.out.println(rs.getString("emp_id")+" dblEarlyLate==="+dblEarlyLate+" "+rs.getString("in_out_timestamp"));
//				
//				if(isHalfDay(strDate, dblEarlyLate, strINOUT, (String)hmEmpWlocationMap.get(strPresentEmpIdNew), uF, con) && alHalfDaysDueToLatePolicy!=null
//						&& isRosterDependent
//						&& !alHalfDaysDueToLatePolicy.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))
//						&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT))){
//					alHalfDaysDueToLatePolicy.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT));
//				}
//				
//				/*if("30".equalsIgnoreCase(strPresentEmpIdNew)){
//					System.out.println("strPresentEmpIdNew=="+strPresentEmpIdNew+" strDate=="+strDate+" dblEarlyLate=="+dblEarlyLate+" strINOUT=="+strINOUT+" alHalfDaysDueToLatePolicy===>"+alHalfDaysDueToLatePolicy);
//				}*/
//				
//				
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
//				hmEmpHoursWorked.put(strPresentEmpIdNew, hmHoursWorked);
//				hmOverLappingHolidays.put(strPresentEmpIdNew, dblOverLappingHolidays+"");
//				
////				System.out.println("hmHoursWorked===>"+hmEmpHoursWorked);
//				
//				strPresentEmpIdOld = strPresentEmpIdNew;
//			}
//			
//			
//			
//			log.debug("hmHoursWorked===>"+hmEmpHoursWorked);
//			
//			
//			Map hmEmpSalary = new LinkedHashMap();
//			Map hmEmpSalaryInner = new LinkedHashMap();
//			Map hmSalaryDetails = new HashMap();
////			Map hmEmpPresentDays = new HashMap();
//			
//			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
//			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
//			
//
////			pst = con.prepareStatement("select * from salary_details order by weight");
//			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+") and org_id =? order by earning_deduction desc, salary_head_id, weight");
//			pst.setInt(1, uF.parseToInt(getF_org()));
//			
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
//					
////					System.out.println("earning_deduction====>"+rs.getString("earning_deduction"));
////					System.out.println("alEmpSalaryDetailsEarning====>"+alEmpSalaryDetailsEarning);
////					System.out.println("alEarningSalaryDuplicationTracer====>"+alEarningSalaryDuplicationTracer);
////					System.out.println("index====>"+index);
////					System.out.println("salary_head_id====>"+rs.getString("salary_head_id"));
//					
//					
//					if(index>=0){
//						alEmpSalaryDetailsEarning.remove(index);
//						alEarningSalaryDuplicationTracer.remove(index);
////						alEmpSalaryDetailsEarning.add(index, rs.getString("salary_head_id"));
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
////						alEmpSalaryDetailsDeduction.add(index, rs.getString("salary_head_id"));
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
//			
////			pst = con.prepareStatement(selectEmployeeSalaryHeadCalculation);
////			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			
//			
//			
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
//			
//			 
//			
////			System.out.println("----------_>"+pst);
//			String strEmpIdNew1 = null;
//			String strEmpIdOld1 = null;
//			while(rs.next()){
////				strEmpIdNew1 = rs.getString("empl_id");
//				strEmpIdNew1 = rs.getString("emp_id");
//						
//				if(!alEmp.contains(strEmpIdNew1))continue;		
//						
//						
//				if(strEmpIdNew1!=null && !strEmpIdNew1.equalsIgnoreCase(strEmpIdOld1)){
//					hmEmpSalaryInner = new LinkedHashMap();
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
//				if(strEmpIdNew1!=null && strEmpIdNew1.length()>0){
//					hmEmpSalary.put(strEmpIdNew1, hmEmpSalaryInner);
//				}
//				
//				
//				strEmpIdOld1 = strEmpIdNew1;
//				
////				if(!hmSalaryDetails.containsKey(rs.getString("salary_id"))){
//					//hmSalaryDetails.put(rs.getString("salary_id"), rs.getString("salary_head_name"));
////				}
//			}
//
//			Map<String, String> hmIncentives = CF.getIncentivesMap(con, uF, CF, strPC);
//			Map<String, String> hmIndividualBonus = CF.getIndividualBonusMap(con, uF, CF, strPC);
//			Map<String, String> hmIndividualOtherDeduction = CF.getIndividualOtherDeductionMap(con, uF, CF, strPC);
//			Map<String, String> hmIndividualOvertime = CF.getIndividualOvertimeMap(con, uF, CF, strPC);
//			
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
//			
//			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
//			Set set0 = hmEmpSalary.keySet();
//			Iterator it0 = set0.iterator();
//			while(it0.hasNext()){
//				String strEmpId = (String)it0.next();
//				int nEmpId = uF.parseToInt(strEmpId);
//				String strOrgId = hmEmpOrgId.get(strEmpId);
//				
//				if(!alProcessingEmployee.add(strEmpId)){
//					alProcessingEmployee.add(strEmpId);
//				}
//				
//				log.debug("hmPresentDays====>"+hmPresentDays);
//				
//				
////				System.out.println("hmPresentDays====>"+hmPresentDays);
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
//				if("1177".equalsIgnoreCase(strEmpId)){
//					System.out.println(strEmpId+" alHalfDaysDueToLatePolicyTemp="+alHalfDaysDueToLatePolicyTemp);
//				}
//				
//				
//				
//				Map hmLeaves = (Map)hmLeaveDays.get(strEmpId);
//				if(hmLeaves==null)hmLeaves = new HashMap();
//				
//				Map hmLeavesType = (Map)hmLeaveTypeDays.get(strEmpId);
//				if(hmLeavesType==null)hmLeavesType = new HashMap();
//
//				
////				System.out.println("strEmpId= 123 ==>"+strEmpId);
////				
////				System.out.println("hmLeaves===>"+hmLeaves);
////				System.out.println("hmLeavesType===>"+hmLeavesType);
//
//				EmployeeActivity obj = new EmployeeActivity();
//				obj.setServletRequest(request);
//				
////				Map hmBasicSalaryMap = CF.getSpecificSalaryData(BASIC);
////				Map hmDASalaryMap = CF.getSpecificSalaryData(DA);
//				
//				
//				setLeaves = hmLeaves.keySet();
//				it = setLeaves.iterator();
////				int nLeaves = 0;
//				double nOverlappingHolidaysLeaves = 0;
//				while(it.hasNext()){
//					String strLeaveDate = (String)it.next();
//					String strHolidayDate = (String)hmHolidayDates.get(uF.getDateFormat(strLeaveDate, DATE_FORMAT, CF.getStrReportDateFormat())+"_"+(String)hmEmpWlocationMap.get(strEmpId));
//					
//					String strLeaveType = (String)hmLeavesType.get(strLeaveDate);
//					
//					
////					System.out.println("strLeaveType===>"+strLeaveType);
////					System.out.println("strLeaveDate===>"+strLeaveDate);
////					System.out.println("hmLeavesType===>"+hmLeavesType);
////					System.out.println("alPresentTemp===>"+alPresentTemp);
//					
//					
////					if(strLeaveDate!=null && !strLeaveDate.equals(strHolidayDate) && !alPresentTemp.contains(strLeaveDate)){
////						nLeaves++;
////					}
//					
//					
//					
//					/*if(strEmpId.equalsIgnoreCase("656")){
//						
//						System.out.println("strLeaveDate="+strLeaveDate);
//						System.out.println("strHolidayDate="+strHolidayDate);
//						
//					}*/
//						
//						
////					if(strLeaveDate!=null && strLeaveDate.equals(strHolidayDate)){
////						nOverlappingHolidaysLeaves++;
////					}
//					
//					if(strLeaveDate!=null && strHolidayDate!=null){
//						nOverlappingHolidaysLeaves++;
//					}
//					
//					if(strLeaveDate!=null && alPresentTemp.contains(strLeaveDate) && "H".equalsIgnoreCase(strLeaveType)){
////					if(strLeaveDate!=null && "H".equalsIgnoreCase(strLeaveType)){
//						dblPresent += -1 + 0.5;
//					}
//					
//					
//				}
//				
//				
//				
//				int nHolidays = uF.parseToInt(hmHolidays.get((String)hmEmpWlocationMap.get(strEmpId)));
////				int nWeekEnds = uF.getWeekEndCount(hmWeekEnds,(String)hmEmpWlocationMap.get(strEmpId), strD1, strD2);
////				int nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,(String)hmEmpWlocationMap.get(strEmpId), strD1, strD2);
//				int nWeekEnds = 0;
//				
//				
//				
//				
//				
//				if(hmEmpEndDateMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil(hmEmpEndDateMap.get(strEmpId), DATE_FORMAT))){
//					Map hmWeekEnds1 = CF.getWeekEndDateList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, uF, null, null);
////					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds1,(String)hmEmpWlocationMap.get(strEmpId), strD1, hmEmpEndDateMap.get(strEmpId));
//					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con,request,strD1, hmEmpEndDateMap.get(strEmpId), CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
//					nHolidays = uF.parseToInt(hmHolidays1.get((String)hmEmpWlocationMap.get(strEmpId)));
////					System.out.println("end date nWeekEnds====>"+nWeekEnds);
////					System.out.println("end date hmWeekEnds====>"+hmWeekEnds);
////					System.out.println("end date strEmpId=====>"+strEmpId);
////					System.out.println("end date nHolidays=====>"+nHolidays);
//					
//				}else if(hmEmpJoiningMap.containsKey(strEmpId) && uF.isDateBetween(uF.getDateFormatUtil(strD1, DATE_FORMAT), uF.getDateFormatUtil(strD2, DATE_FORMAT), uF.getDateFormatUtil((String)hmEmpJoiningMap.get(strEmpId), DATE_FORMAT))){
//					Map hmWeekEnds1 = CF.getWeekEndDateList(con, (String)hmEmpJoiningMap.get(strEmpId), strD2, CF, uF, null, null);
////					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds1,(String)hmEmpWlocationMap.get(strEmpId), (String)hmEmpJoiningMap.get(strEmpId), strD2);
//					
//					Map<String, String> hmHolidays1 = new HashMap<String, String>();
//					Map<String, String> hmHolidayDates1 = new HashMap<String, String>();
//					CF.getHolidayList(con,request,(String)hmEmpJoiningMap.get(strEmpId), strD2, CF, hmHolidayDates1, hmHolidays1, hmWeekEnds1, true);
//					nHolidays = uF.parseToInt(hmHolidays1.get((String)hmEmpWlocationMap.get(strEmpId)));
//					
////					System.out.println("start date nWeekEnds====>"+nWeekEnds);
////					System.out.println("start date hmWeekEnds====>"+hmWeekEnds);
////					System.out.println("start date strEmpId=====>"+strEmpId);
////					System.out.println("start date nHolidays=====>"+nHolidays);
//					
//				}else{
////					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,(String)hmEmpWlocationMap.get(strEmpId), strD1, strD2);
//					
//					
////					System.out.println("nWeekEnds====>"+nWeekEnds);
////					System.out.println("hmWeekEnds====>"+hmWeekEnds);
////					System.out.println("strEmpId=====>"+strEmpId);
////					System.out.println("strD1=====>"+strD1);
////					System.out.println("strD2====>"+strD2);
//					
//					
////					hmHolidays = new HashMap<String, String>();
////					hmHolidayDates = new HashMap<String, String>();
////					CF.getHolidayList(strD1, strD2, CF, hmHolidayDates, hmHolidays, hmWeekEnds, true);
////					nHolidays = uF.parseToInt(hmHolidays.get((String)hmEmpWlocationMap.get(strEmpId)));
//				}
//				
//				
//				if(hmEmpEndDateMap.containsKey(strEmpId)){
//					Map hmWeekEnds1 = CF.getWeekEndDateList(con, strD1, hmEmpEndDateMap.get(strEmpId), CF, uF, null, null);
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds1,(String)hmEmpWlocationMap.get(strEmpId), strD1, hmEmpEndDateMap.get(strEmpId));
//				}else{
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,(String)hmEmpWlocationMap.get(strEmpId), strD1, strD2);
//				}
//				
//				
//				/*int nWeekEnds = 0;
//				if(strEmpId.equalsIgnoreCase("460")){
//					nWeekEnds = uF.getWeekEndDateCount(hmWeekEnds,(String)hmEmpWlocationMap.get(strEmpId), strD1, strD2);
//				}*/
//				
//				
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
//				double dblActualLeaves =  dblTotalLeaves - nOverlappingHolidaysLeaves;
//				
//				
//				
////				double dblTotalPresentDays = dblPresent + dblActualLeaves + nHolidays + nWeekEnds;
//				double dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
//				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds - nOverlappingHolidaysLeaves)+"");
//				
//				
//				
//				
//
//
////				
////				if(strEmpId.equalsIgnoreCase("460")){
////					
////					System.out.println("dblPresent 1="+alPresentTemp.size());
////					System.out.println("dblPresent="+dblPresent);
////					System.out.println("nHolidays="+nHolidays);
////					System.out.println("dblOverlappingHolidays="+dblOverlappingHolidays);
////					System.out.println("nWeekEnds="+nWeekEnds);
////					System.out.println("nOverlappingWeekends="+nOverlappingWeekends);
////					System.out.println("dblTotalLeaves="+dblTotalLeaves);
////					System.out.println("nOverlappingHolidaysLeaves="+nOverlappingHolidaysLeaves);
////				}
////				
////				
//				
//				
////				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays +nWeekEnds)+"");
//				
//
//				
//				
//				/*
//				
//				
//
//				// hard coded condition and logic needs to be implemented for this condition. --Vipin 21-01-2013
//				if(dblPresent>=22){
//					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds)+"");
//				}else if(dblPresent>=20){
//					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +4 + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +4- nOverlappingHolidaysLeaves)+"");
//				}else if(dblPresent>=14){
//					dblTotalPresentDays = dblPresent +3 + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent  +3- nOverlappingHolidaysLeaves)+"");
//				}else if(dblPresent>=8){
//					dblTotalPresentDays = dblPresent +2 + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent +2- nOverlappingHolidaysLeaves)+"");
//				}else if(dblPresent>=4){
//					dblTotalPresentDays = dblPresent  +1 + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent +1- nOverlappingHolidaysLeaves)+"");
//				}else{
//					dblTotalPresentDays = dblPresent  + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent  - nOverlappingHolidaysLeaves)+"");
//				} 
//				*/
//				
//				// FOR KP
//				 
//				/*// hard coded condition and logic needs to be implemented for this condition. --Vipin 21-01-2013
//				if(dblPresent>15){
//					dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds- nOverlappingHolidaysLeaves)+"");
//				}else if(dblPresent>=8){
//					dblTotalPresentDays = dblPresent +5 + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent +5- nOverlappingHolidaysLeaves)+"");
//				}else if(dblPresent>=4){
//					dblTotalPresentDays = dblPresent  +1 + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent +1- nOverlappingHolidaysLeaves)+"");
//				}else{
//					dblTotalPresentDays = dblPresent  + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent - nOverlappingHolidaysLeaves )+"");
//				}
//				
//				*/
//				
//				
//				
//				
//				
//				//comment for solar start  
//				
////				Calendar calW = GregorianCalendar.getInstance();
////				Calendar calW1 = GregorianCalendar.getInstance();
////				Calendar calW2 = GregorianCalendar.getInstance();
////				calW.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "dd")));
////				calW.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "MM"))-1);
////				calW.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strD1, DATE_FORMAT, "yyyy")));
////			
////				int nDiff = uF.parseToInt(uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT));
////				int nDeductionW = 0;
////				List alPW = (List)hmPresentDays.get(strEmpId);
////				if(alPW==null)alPW=new ArrayList();
////				
////				List alPWE = (List)hmPresentWeekEndDays.get(strEmpId);
////				if(alPWE==null)alPWE=new ArrayList();
////				
////				
////							
////				
////				
////				for(int i=0; i<nDiff; i++){
////					String strDW = uF.getDateFormat(calW.get(Calendar.DATE)+"/"+(calW.get(Calendar.MONTH)+1)+"/"+calW.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
////					String strDH = uF.getDateFormat(calW.get(Calendar.DATE)+"/"+(calW.get(Calendar.MONTH)+1)+"/"+calW.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
////					calW1 = (Calendar)calW.clone();
////					  
////					
////					
////					
////					
////					if(hmWeekEnds.containsKey(strDW+"_"+(String)hmEmpWlocationMap.get(strEmpId)) && i>0){
////						  
////						calW1.add(Calendar.DATE, -1);
////						strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
////						
////						if(!hmWeekEnds.containsKey(strDW+"_"+(String)hmEmpWlocationMap.get(strEmpId)) && !alPW.contains(strDW) && !hmLeaves.containsKey(strDW)){
////							
////							calW1.add(Calendar.DATE, 2);
////							strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
////							
////							
////							if(hmWeekEnds.containsKey(strDW+"_"+(String)hmEmpWlocationMap.get(strEmpId))){
////								
////								calW1.add(Calendar.DATE, 1);
////								strDW = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
////								
////								if(!alPW.contains(strDW) && !hmLeaves.containsKey(strDW)){
////									nDeductionW+=2;
////									
////									
////									if(strEmpId.equalsIgnoreCase("652")){
////										System.out.println("strDW====1==="+strDW+"   "+nDeductionW);
////										
////									}
////								}
////								
////							}else{
////								
////								if(!alPW.contains(strDW) && !hmLeaves.containsKey(strDW)){
////									nDeductionW+=1;
////									
////									if(strEmpId.equalsIgnoreCase("652")){
////										System.out.println("strDW====1==="+strDW+"   "+nDeductionW);
////										
////									}
////								}
////							}
////							
////						}
////						
////						
////					}
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					
////					/*calW1 = (Calendar)calW.clone();
////					if(hmHolidayDates.containsKey(strDH+"_"+(String)hmEmpWlocationMap.get(strEmpId)) && alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && i>0){
////						calW1.add(Calendar.DATE, -1);
////						strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
////						
////						if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))  && !hmLeaves.containsKey(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && !hmHolidayDates.containsKey(strDH+"_"+(String)hmEmpWlocationMap.get(strEmpId))){
////							calW1.add(Calendar.DATE, 1);
////							strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
////							if(hmHolidayDates!=null && hmHolidayDates.containsKey(strDH+"_"+(String)hmEmpWlocationMap.get(strEmpId)) && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))){
////								nDeductionW+=1;
////						}else if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && !hmLeaves.containsKey(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT)) && hmHolidayDates!=null && hmHolidayDates.containsKey(strDH+"_"+(String)hmEmpWlocationMap.get(strEmpId))){
////							calW1.add(Calendar.DATE, -1);
////							strDH = uF.getDateFormat(calW1.get(Calendar.DATE)+"/"+(calW1.get(Calendar.MONTH)+1)+"/"+calW1.get(Calendar.YEAR), DATE_FORMAT, CF.getStrReportDateFormat());
////							if(alPW!=null && !alPW.contains(uF.getDateFormat(strDH, CF.getStrReportDateFormat(), DATE_FORMAT))){
////								nDeductionW+=1;
////							}  
////						}
////					}
////					
////					
////					}*/
////					
////					
////					calW2 = (Calendar)calW.clone();
////					strDH = uF.getDateFormat(calW2.get(Calendar.DATE)+"/"+(calW2.get(Calendar.MONTH)+1)+"/"+calW2.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
////					
////					if(hmLeaves.containsKey(strDH) && hmWeekEnds.containsKey(strDH+"_"+(String)hmEmpWlocationMap.get(strEmpId))){
////						dblOverlappingHolidays++;
////						if(strEmpId.equalsIgnoreCase("652")){
////							System.out.println("strDH= 5 ="+strDH);
////						}
////					}
////					
////					
////					
////					calW.add(Calendar.DATE, 1);
////				
////					
////			}
////				  
////				if(dblPresent>0 || dblActualLeaves>0){
////					if(dblPresent>0 && (dblPresent+dblActualLeaves)<6){
////						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + dblActualLeaves ;
////						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nOverlappingHolidaysLeaves )+"");
////						
////					}else if((dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves)>nDeductionW){
////						dblTotalPresentDays = dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds + dblActualLeaves - nDeductionW ;
////						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays +nWeekEnds- nOverlappingHolidaysLeaves - nDeductionW)+"");
////					}else{
////						dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + dblActualLeaves ;
////						hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nOverlappingHolidaysLeaves )+"");
////					}
////					
////				}else{
////					dblTotalPresentDays = 0 ;
////					hmPresentDays1.put(strEmpId, 0+"");
////				}
//				
//				//comment for solar end
//				  
//				/*if(strEmpId.equalsIgnoreCase("652")){ 
//					System.out.println("strEmpId=="+strEmpId);
//					System.out.println("dblPresent=="+dblPresent);
//					System.out.println("dblTotalPresentDays=="+dblTotalPresentDays);
//					System.out.println("nHolidays=="+nHolidays);
//					System.out.println("dblOverlappingHolidays=="+dblOverlappingHolidays);
//					System.out.println("nWeekEnds=="+nWeekEnds);
//					System.out.println("dblActualLeaves=="+dblActualLeaves);
//					System.out.println("nDeductionW=="+nDeductionW);
//					System.out.println("hmLeaves=="+hmLeaves);
//					System.out.println("hmWeekEnds=="+hmWeekEnds);
//				}*/
//					  			
//				
//				/*
//				if(true){ // if daily calculation employees
//					dblTotalPresentDays = dblPresent + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent + nWorkingWeekEnds)+"");
//				}
//				*/
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
//					dblTotalPresentDays = dblPresent + nHolidays -dblOverlappingHolidays + nWeekEnds + dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent+nHolidays -dblOverlappingHolidays + nWeekEnds )+"");
//				}
//				
//				
////				System.out.println("strEmpId=="+strEmpId+" alPresentWeekEndDates=="+alPresentWeekEndDates);
//				
//				
//				
//				
//				int nTotalNumberOfDaysForCalc = nTotalNumberOfDays; 
//				
//				/**   AWD  = Actual Working Days
//				 * */
//				
//				if("AWD".equalsIgnoreCase(CF.getStrOSalaryCalculationType())){
//					dblTotalPresentDays = dblPresent + dblActualLeaves;
//					
//					if(dblPresent>0){
//						dblPresent = dblPresent + nHolidays ;
//					}else{
//						dblPresent = dblPresent ;
//					}
//					
//					dblTotalPresentDays = dblPresent+ dblActualLeaves;
//					hmPresentDays1.put(strEmpId, (dblPresent)+"");
//					
////					strTotalDays = (nTotalNumberOfDays - nHolidays - nWeekEnds)+"";
////					System.out.println("hmLeaveTypeDays==="+hmLeavesType);
////					System.out.println("dblTotalLeaves==="+dblTotalLeaves+" dblActualLeaves="+dblActualLeaves+" nOverlappingHolidaysLeaves==="+nOverlappingHolidaysLeaves);
//					
////					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nHolidays - nWeekEnds; 
//					nTotalNumberOfDaysForCalc = nTotalNumberOfDays - nWeekEnds; 
//					
//					
//					strTotalDays = nTotalNumberOfDaysForCalc +"";
//				}
//				
//				
////				System.out.println("dblTotalLeaves=>"+dblTotalLeaves);
////				System.out.println("nOverlappingHolidaysLeaves=>"+nOverlappingHolidaysLeaves);
////				System.out.println("nPresent=>"+dblPresent);
////				System.out.println("dblActualLeaves=>"+dblActualLeaves);
////				System.out.println("nHolidays=>"+nHolidays);
////				System.out.println("nWeekEnds=>"+nWeekEnds);
////				System.out.println("dblTotalPresentDays=>"+dblTotalPresentDays);
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
////				double dblAmt = uF.parseToDouble(hmBreakPolicy.get("-2_"+getF_org()));
////				dblTotalPresentDays -= (nBreaks * dblAmt); 
////				if(nBreaks>0){
////					hmPresentDays1.put(strEmpId, (dblTotalPresentDays)+"");
////				}
//				
//				
//				
//				
//				
//				/**
//				 *   The attendance dependency calculation is for those employees who are not 
//				 *   attendance dependent and will get the full salary irrespective they clocking on.
//				 */
//				
//				boolean isAttendance = uF.parseToBoolean((String)hmAttendanceDependent.get(strEmpId));
//				if(!isAttendance){
////					dblTotalPresentDays = nTotalNumberOfDays;
//					dblTotalPresentDays = nTotalNumberOfDaysForCalc;
//				}
//				
//				hmPaidDays.put(strEmpId, dblTotalPresentDays+"");
//				
//				
//				
//				
//				
//				
//				
//				
//				
////				hmPresentDays1.put(strEmpId, (dblPresent+nHolidays+nWeekEnds)+"");
//				
////				double dblIncrement = 0;
//				
//				
//				
//				double dblIncrementBasic = getIncrementCalculationBasic(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
//				double dblIncrementDA = getIncrementCalculationDA(con, uF, strEmpId, hmBasicSalaryMap, hmDASalaryMap, strFinancialYearStart, strFinancialYearEnd, nPayMonth, CF);
//				
//				
//				
//				Map hmInner = new HashMap();
//				
//				if((dblIncrementBasic>0 || dblIncrementDA>0) && getApprovePC()!=null){
////					hmInner = CF.getSalaryCalculation(nEmpId, dblTotalPresentDays, nTotalNumberOfDaysForCalc, 0, (String)hmEmpLevelMap.get(strEmpId), uF, CF);
//					hmInner = CF.getSalaryCalculation(con, nEmpId, dblTotalPresentDays, 0, nBreaks, nTotalNumberOfDaysForCalc, dblIncrementBasic, dblIncrementDA, (String)hmEmpLevelMap.get(strEmpId), uF, CF, strD2);
//					obj.processActivity(con, 1, uF.parseToInt(strEmpId), uF.getDateFormat(strD2, DATE_FORMAT, DBDATE), CF, uF);
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
//				/*
//				if(strEmpId.equalsIgnoreCase("36")){
//					System.out.println("hmPaidSalaryInner=>"+hmPaidSalaryInner);
//				}*/
//				
//				hmHoursWorked = hmEmpHoursWorked.get(strEmpId);
//				if(hmHoursWorked==null)hmHoursWorked = new HashMap<String, String>();
//				
//				
//				
//				
//				Map<String, Map<String, String>> hmArearAmountMap = CF.getArearDetails(con, uF, CF, strD2);
//				if(hmArearAmountMap == null) hmArearAmountMap = new HashMap<String, Map<String,String>>();
////				System.out.println("hmPresentDays1====>"+hmPresentDays1);
////				System.out.println("nTotalPresentDays====>"+dblTotalPresentDays);
//				
////				log.debug(strEmpId+" hmPaidSalaryInner==>"+hmPaidSalaryInner);
////				log.debug("hmEmpPaidSalary==>"+hmEmpPaidSalary);
////				log.debug("hmInner==>"+hmInner);
////				log.debug("strEmpId==>"+strEmpId);
////				log.debug("dblPresent==>"+dblTotalPresentDays);
////				log.debug("nTotalNumberOfDays==>"+nTotalNumberOfDaysForCalc);
//				
//				
////				System.out.println("hmInner==>"+hmInner);
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
//				
//				
//				while(it1.hasNext()){
//					String strSalaryId = (String)it1.next();
//					int nSalayHead = uF.parseToInt(strSalaryId);
//					
////					System.out.println("strSalaryId= 0000 ==>"+strSalaryId);
//					
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
//								case OVER_TIME:
//								 
//									isDefinedEarningDeduction = true;
//									//double dblOverTime = getOverTimeCalculationL(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC, hmOverTimeMap, hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime);
//									double dblOverTime = getOverTimeCalculationHours(con, uF, strEmpId, hmHoursWorked, alServiceTemp, alPresentTemp,alPresentWeekEndDates, hmInner, hmInnerActualCTC,  hmEmpLevelMap, dblStandardHrs, hmHolidays, nTotalNumberOfDaysForCalc, hmIndividualOvertime,hmEmpOverTimeHours,hmEmpOverTimeLevelPolicy, hmEmpRosterHours, hmWlocationMap, nTotalNumberOfDays, nWeekEnds,nHolidays,dblPresent,dblTotalPresentDays,hmHolidayDates,hmWeekEnds,hmEmpWlocationMap);
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(dblOverTime));
//									dblGross += dblOverTime;
//									dblGrossTDS += dblOverTime;
//									dblOverTime = uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblOverTime)));
//									
//									
//								break;
//								
//								case BONUS:
//									// Bonus is paid independent of paycycle -- 
//									
//									if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())){
//										isDefinedEarningDeduction = true;
//										double dblBonusAmount = getBonusCalculation(con, uF, strEmpId, hmEmpLevelMap, hmInnerActualCTC, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap, CF, hmIndividualBonus);
//										hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(dblBonusAmount));
//										dblGross += dblBonusAmount;
//										dblGrossTDS += dblBonusAmount;
//										dblBonusAmount = uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblBonusAmount)));
//										
//									}
//									
//								break;
//								
//								case AREARS:
//
//									isDefinedEarningDeduction = true;
//									double dblArearAmount = getArearCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmArearAmountMap, CF);
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(dblArearAmount));
//									dblGross += dblArearAmount;
//									dblGrossTDS += dblArearAmount;
//									
//									dblArearAmount = uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblArearAmount)));
//								
//								break;
//								
//								case INCENTIVES:
//									isDefinedEarningDeduction = true;
//									double dblIncentiveAmount = getIncentivesCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(dblIncentiveAmount));
//									dblGross += dblIncentiveAmount;
//									dblGrossTDS += dblIncentiveAmount;
//									dblIncentiveAmount = uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblIncentiveAmount)));
//									
//								break;
//								
//								case REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
//									double dblReimbursementAmount = getReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(dblReimbursementAmount));
//									dblGross += dblReimbursementAmount;
//									dblReimbursementAmount = uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblReimbursementAmount)));
//									
//								break;
//								
//								case TRAVEL_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
//									double dblTravelReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(dblTravelReimbursementAmount));
//									dblGross += dblTravelReimbursementAmount;
//									dblTravelReimbursementAmount = uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTravelReimbursementAmount)));
//									
//								break;
//								
//								case MOBILE_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
//									double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(dblMobileReimbursementAmount));
//									dblGross += dblMobileReimbursementAmount;
//									dblMobileReimbursementAmount = uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblMobileReimbursementAmount)));
//									
//								break;
//								
//								case OTHER_REIMBURSEMENT:
//									isDefinedEarningDeduction = true;
//									double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
//									hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(dblOtherReimbursementAmount));
//									dblGross += dblOtherReimbursementAmount;
//									dblOtherReimbursementAmount = uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblOtherReimbursementAmount)));
//									
//								break;
//								
//								default:
//									if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")){
//										hmTotal.put(strSalaryId, (String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
//										dblGross += uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
//										dblGrossPT +=uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
//										dblGrossTDS += uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
//									}else if(uF.parseToInt(strSalaryId)!=GROSS){
//										hmTotal.put(strSalaryId, (String)hm.get("AMOUNT"));
//										dblGross += uF.parseToDouble((String)hm.get("AMOUNT"));
//										dblGrossPT +=uF.parseToDouble((String)hm.get("AMOUNT"));
//										dblGrossTDS +=uF.parseToDouble((String)hm.get("AMOUNT"));	
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
//						case PROFESSIONAL_TAX:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblPt));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
//							}else{
//								
//								
//								/**
//								 * KP Condition
//								 * 
//								 * */
//								
//								//double dblPt = calculateProfessionalTax(con, uF, dblGrossPT, strFinancialYearEnd, nPayMonth, (String)hmEmpStateMap.get(strEmpId));
//							 	double dblPt = calculateProfessionalTax(con, uF, dblGross, strFinancialYearEnd, nPayMonth, (String)hmEmpStateMap.get(strEmpId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblPt));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
//							}
//							
//							break;
//						/**********  EPF EMPLOYEE CONTRIBUTION   *************/
//						case EMPLOYEE_EPF:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblPt));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								Map hmVoluntaryPF = (Map)hmInner.get(VOLUNTARY_EPF+"");
//								
//								double dblEEPF = calculateEEPF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmVoluntaryPF, strEmpId, null, null, false);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblEEPF));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblEEPF)));
//							}
//							
//							break;
//							
//							/**********  VPF EMPLOYEE CONTRIBUTION   *************/
//						case VOLUNTARY_EPF:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblPt));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								Map hmVoluntaryPF = (Map)hmInner.get(VOLUNTARY_EPF+"");
//								
//								if(hmVoluntaryPF==null){
//									hmVoluntaryPF = new HashMap();
//								}
//								double dblEVPF = uF.parseToDouble((String)hmVoluntaryPF.get("AMOUNT"));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblEVPF));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblEVPF)));
//							}
//							
//							break;	
//							
//						/**********  EPF EMPLOYER CONTRIBUTION   *************/
//						case EMPLOYER_EPF:
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblPt = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblPt));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.ceil(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblERPF = calculateERPF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, null, null, false);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblERPF));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.ceil(dblERPF)));
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
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblPt));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblESI = calculateERESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, (String)hmEmpStateMap.get(strEmpId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblESI));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.ceil(dblESI)));
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
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblPt));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.ceil(dblPt)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblESI = calculateEEESI(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, (String)hmEmpStateMap.get(strEmpId), hmVariables, strEmpId);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblESI));
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
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblLWF));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblLWF = calculateERLWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, (String)hmEmpStateMap.get(strEmpId), nPayMonth, strOrgId);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblLWF));
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
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblLWF));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLWF)));
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblLWF = calculateEELWF(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, (String)hmEmpStateMap.get(strEmpId), hmVariables, strEmpId, nPayMonth, strOrgId);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblLWF));
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
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblLoan));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLoan)));
//								
//								CF.getLoanPayrollDetails(con, uF, alLoans, hmEmpLoan, strD1, strD2);
//							}else{
//								
//								log.debug(strEmpId+"===dblGross===>"+dblGross);
//								
//								double dblLoanAmt = calculateLOAN(con, uF, dblGross, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, CF, hmLoanAmt, hmEmpLoan, alLoans);
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblLoanAmt));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLoanAmt)));
//							}
//							
//							break;
//							
//						case OTHER_DEDUCTION:
//							
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblLoan = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblLoan));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLoan)));
//							}else{
//								double dblIndividualOtherDeductionAmt = getIndividualOtherDeductionCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherDeduction, CF);
//								dblDeduction += dblIndividualOtherDeductionAmt;
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIndividualOtherDeductionAmt)));
//							}
//							
//							break;	
//							
//						case MOBILE_RECOVERY:
//							
//							isDefinedEarningDeduction = true;
//							if(hmPaidSalaryInner!=null){
//								double dblLoan = uF.parseToDouble((String)hmPaidSalaryInner.get(strSalaryId));
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblLoan));
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblLoan)));
//							}else{
//								double dblIndividualMobileRecoveryAmt = getMobileRecoveryCalculation(con, uF, strEmpId, hmEmpLevelMap, hmTotal, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileRecovery, CF);
//								dblDeduction += dblIndividualMobileRecoveryAmt;
//								hmTotal.put(strSalaryId, uF.formatIntoTwoDecimal(Math.round(dblIndividualMobileRecoveryAmt)));
//							}
//							
//							break;		
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
//								
//								double dblTDS = calculateTDS(con, uF, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, (dblBasic + dblDA),
//										nPayMonth,
//										strD1, strFinancialYearStart, strFinancialYearEnd, strEmpId, (String)hmEmpGenderMap.get(strEmpId),  (String)hmEmpAgeMap.get(strEmpId), (String)hmEmpStateMap.get(strEmpId),
//										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//										hmTotal, hmSalaryDetails, hmEmpLevelMap, CF);
//								
//								dblDeduction += uF.parseToDouble(uF.formatIntoTwoDecimal(dblTDS));
//								
//								log.debug("dblTDS==>=>"+dblTDS);
//								/*
//								if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//									System.out.println("dblTDS==>=>"+dblTDS);
//								}*/
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
//								/*if(nSalayHead==VOLUNTARY_EPF){
//									continue;
//								}*/
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
//					/*if(!isDefinedEarningDeduction){
//						if(hmPaidSalaryInner!=null){
//							hmTotal.put(strSalaryId, (String)hmPaidSalaryInner.get(strSalaryId));
//						}else{
//							
//							if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_E")){
//								hmTotal.put(strSalaryId, (String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
//								dblGross += uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
//								dblGrossTDS += uF.parseToDouble((String)hmVariables.get(strEmpId+"_"+strSalaryId+"_E"));
//								
//								
//								
//							}else if(hmVariables.containsKey(strEmpId+"_"+strSalaryId+"_D")){
//								hmTotal.put(strSalaryId, (String)hmVariables.get(strEmpId+"_"+strSalaryId+"_D"));
//								
//								dblDeduction += uF.parseToDouble((String)hm.get("AMOUNT"));
//								
//								
//							}else{
//								hmTotal.put(strSalaryId, (String)hm.get("AMOUNT"));
//								if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E")){
//									dblGross += uF.parseToDouble((String)hm.get("AMOUNT"));
//									dblGrossTDS += uF.parseToDouble((String)hm.get("AMOUNT"));	
//								}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")){
//									dblDeduction += uF.parseToDouble((String)hm.get("AMOUNT"));
//								}
//							}
//						}
//					}*/
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
//				
//				
//				
//				
//				
//				hmTotal.put("NET", uF.formatIntoTwoDecimal(Math.round(dblGross - dblDeduction))); 
//				
//				 
//				hmTotalSalary.put(strEmpId, hmTotal);
//			}
//
//			//			System.out.println("hmTotalSalary===>"+hmTotalSalary);
//			
//			List alEmpIdPayrollG = new ArrayList();
//			pst = con.prepareStatement("select distinct(emp_id) from payroll_generation where paycycle=? and salary_head_id not in ("+BONUS+") ");
//			pst.setInt(1, uF.parseToInt(strPC));
//			
//			rs = pst.executeQuery();
//			while(rs.next()){
////				String strEmpId = rs.getString("emp_id");
//				alEmpIdPayrollG.add(rs.getString("emp_id"));
//			}
//			
//			
////			System.out.println("pst==>"+getPaycycle().split("-")[2]);
////			System.out.println("pst==>"+pst);
////			System.out.println("alEmpIdPayrollG==>"+alEmpIdPayrollG);
//			
//			
//			log.debug("alEmpIdPayrollG===> "+alEmpIdPayrollG);
//			log.debug("hmServices===> "+hmServices);
//			
//			
//			
////			System.out.println("hmTotalSalary="+hmTotalSalary);
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
//			
//			
//			
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
//			session.setAttribute("AP_hmOtherTaxDetails", hmOtherTaxDetails);
//			
//			session.setAttribute("AP_strD1", strD1);
//			session.setAttribute("AP_strD2", strD2);
//			
//			session.setAttribute("AP_hmEmpLevelMap", hmEmpLevelMap);
//			
//			
////			System.out.println("alProcessingEmployee==="+alProcessingEmployee);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//		return SUCCESS;
//
//	}
//
//
//	private double getOverTimeCalculationHours(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List alServices, 
//			List alPresentDays, List alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, 
//			Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays, 
//			int nTotalNumberOfDaysForCalc, Map<String, String> hmIndividualOvertime,Map<String, Map<String, String>> hmEmpOverTimeHours,Map<String, Map<String, String>> hmEmpOverTimeLevelPolicy, Map<String, String> hmEmpRosterHours, Map<String, String> hmWlocationMap, int nTotalNumberOfDays, int nWeekEnds,int nHolidays,double dblPresent,double dblTotalPresentDays,Map<String, String> hmHolidayDates,Map hmWeekEnds,Map hmEmpWlocationMap) {
//		
//		double dblTotalOverTimeAmount = 0.0d;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map hmFixedExemptions = new HashMap();
//		
//		try {
//			
//			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
//			String strLevelId = hmEmpLevelMap.get(strEmpId);
//			String strOverTimeType = null;
//			double dblTotalOverTime = 0;
//			double dblTotalHoursWorked = 0;
//			double dblOverTimeCalcHours = 0;
//			double dblOverTimeCalcDays = 0;
//			
//			double dblOvertimeFixedAmount = 0;
//			
//			
//			Map<String,String> hmOvertimePolicy=new HashMap<String, String>();
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
//				hmOvertimePolicy=hmEmpOverTimeLevelPolicy.get(strLevelId+"_"+strOverTimeType);
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
//				if("MD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))){
//					dblOverTimeCalcDays = nTotalNumberOfDays; 
//				}else if("AWD".equals(hmOvertimePolicy.get("DAY_CALCULATION"))){
//					//dblOverTimeCalcDays = nTotalNumberOfDays - nWeekEnds;
//					dblOverTimeCalcDays = dblTotalPresentDays;
//				}else{
//					dblOverTimeCalcDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));
//				}
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
//				if(strCalcBasic!=null && strCalcBasic.equals("FD")){
//					if(overtimePaymentType!=null && overtimePaymentType.equals("A")){
//						dblTotalOverTimeAmount += dblAmount;
//					}else{
//						dblTotalOverTimeAmount += dblAmount * dblSubSalaryAmount/ 100;
//					}
//					
//				}else if(dblOverTimeCalcHours>0){
//					if(overtimePaymentType!=null && overtimePaymentType.equals("A")){
//						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount / ( dblOverTimeCalcDays * dblOverTimeCalcHours);
//					}else{
//						dblTotalOverTimeAmount += dblOvertimeHours * dblAmount * dblSubSalaryAmount/ (100 * dblOverTimeCalcDays * dblOverTimeCalcHours);								 
//					}
//				}
//				
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
//
//	String paycycle;
//	String wLocation;
//	String level;
//	String f_department;
//	String f_service;
//	
//	List<FillPayCycles> paycycleList;
//	List<FillDepartment> departmentList;
//	List<FillLevel> levelList;
//	List<FillServices> serviceList;
//	List<FillWLocation> wLocationList;
//	List<FillPayCycleDuration> paycycleDurationList;
//	String strPaycycleDuration;
//	
//	List<FillPayMode> paymentModeList;
//	
//	
//	String f_paymentMode;
//	String f_org;
//	
//	List<FillOrganisation> organisationList;
//	
//	String redirectUrl;
//	String approve;
//	String dtMin;
//	String dtMax;
//	String[] chbox;
//	String[] empID;
//	String[] paymentMode;
//
//	public String getApprove() {
//		return approve;
//	}
//
//	public void setApprove(String approve) {
//		this.approve = approve;
//	}   
//
//	public String getDtMin() {
//		return dtMin;
//	}
//
//	public void setDtMin(String dtMin) {
//		this.dtMin = dtMin;
//	}
//
//	public String getDtMax() {
//		return dtMax;
//	}
//
//	public void setDtMax(String dtMax) {
//		this.dtMax = dtMax;
//	}
//
//	public String[] getChbox() {
//		return chbox;
//	}
//
//	public void setChbox(String[] chbox) {
//		this.chbox = chbox;
//	}
//
//	public String[] getEmpID() {
//		return empID;
//	}
//
//	public void setEmpID(String[] empID) {
//		this.empID = empID;
//	}
//
//	private HttpServletRequest request;
//	@Override
//	public void setServletRequest(HttpServletRequest request) {
//		this.request = request;
//
//	}
//
//	private HttpServletResponse response;
//	@Override
//	public void setServletResponse(HttpServletResponse response) {
//		this.request = request;
//
//	}
//	
//	public String getRedirectUrl() {
//		return redirectUrl;
//	}
//
//	public void setRedirectUrl(String redirectUrl) {
//		this.redirectUrl = redirectUrl;
//	}
//
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
//
//	public String getPaycycle() {
//		return paycycle;
//	}
//
//	public void setPaycycle(String paycycle) {
//		this.paycycle = paycycle;
//	}
//
//	public List<FillPayCycles> getPaycycleList() {
//		return paycycleList;
//	}
//
//	public void setStrAlpha(String strAlpha) {
//		this.strAlpha = strAlpha;
//	}
//
//
//	public String getApprovePC() {
//		return approvePC;
//	}
//
//
//	public void setApprovePC(String approvePC) {
//		this.approvePC = approvePC;
//	}
//	
//	
//	
//	public double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearEnd, int nPayMonth, String strWLocationStateId){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rst = null, rs = null;
//		double dblDeductionPayMonth = 0;
//		
//		
//		try {
//			
////			pst = con.prepareStatement("select * from deduction_details_india where income_from<= ? and income_to>= ? and state_id=? order by financial_year_from desc limit 1");
//			pst = con.prepareStatement("select * from deduction_details_india where income_from<= ? and income_to>= ? and state_id=? and financial_year_from = (select max(financial_year_from) from deduction_details_india) limit 1");
//			
//			pst.setDouble(1, dblGross);
//			pst.setDouble(2, dblGross);
//			pst.setInt(3, uF.parseToInt(strWLocationStateId));
//			
//			rs = pst.executeQuery();
//			
//			
//			log.debug("pst====>"+pst);
//			
//			
//			double dblDeductionAmount = 0;
//			double dblDeductionPaycycleAmount = 0;
//			while(rs.next()){
//				dblDeductionAmount = rs.getDouble("deduction_amount");
//				dblDeductionPaycycleAmount = rs.getDouble("deduction_paycycle");
//			}
//			nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
//			
//			int nFinancialYearEndMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
//			nFinancialYearEndMonth = nFinancialYearEndMonth - 1;
//
//			if(nFinancialYearEndMonth==nPayMonth){
//				dblDeductionPayMonth = dblDeductionAmount - (11*dblDeductionPaycycleAmount);
//			}else{
//				dblDeductionPayMonth = dblDeductionPaycycleAmount;
//			}
//
//			/*
//			System.out.println("this.strD2="+strD2);
//			System.out.println("nPayMonth1="+uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
//			
//			
//				System.out.println("nFinancialYearEndMonth="+nFinancialYearEndMonth);
//				System.out.println("nPayMonth="+nPayMonth);
//				System.out.println("dblDeductionPayMonth="+dblDeductionPayMonth);
//				System.out.println("strFinancialYearEnd="+strFinancialYearEnd);
//			
//			
//			
//			log.debug("strFinancialYearEnd="+strFinancialYearEnd);
//			log.debug("nFinancialYearEndMonth="+nFinancialYearEndMonth);
//			log.debug("nPayMonth="+nPayMonth);
//			log.debug("dblDeductionPayMonth="+dblDeductionPayMonth);
//			log.debug("dblDeductionAmount="+dblDeductionAmount);
//			log.debug("dblDeductionPaycycleAmount="+dblDeductionPaycycleAmount);
//			*/
//
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		}
//		return dblDeductionPayMonth;
//		
//	}
//	
//	
//	public double calculateLOAN(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strEmpId, CommonFunctions CF, Map hmLoanAmt, Map hmEmpLoan, List alLoans){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rs = null;
//		double dblCalculatedAmount = 0;
//		double dblTotalCalculatedAmount = 0;
//		
//		
//		try {
//			
//			Map hmLoanAmtInner = new HashMap();
//			
//			pst = con.prepareStatement(selectLoanPayroll1);
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			rs = pst.executeQuery();
//			
////			System.out.println("pst===>"+pst);
//			
//			double dblPrincipalAmt = 0;
//			double dblBalAmt = 0;
//			double dblROI = 0;
//			double dblDuration = 0;
//			
//			String strApprovedDate = null;
//			
//			
//			Map hmEmpLoanInner = new HashMap();
//			
//			
//			while(rs.next()){
//				dblPrincipalAmt = rs.getDouble("amount_paid");
//				dblBalAmt = rs.getDouble("balance_amount");
//				dblROI = rs.getDouble("loan_interest");
//				dblDuration = rs.getDouble("duration_months");
//				
//				strApprovedDate = rs.getString("approved_date");
//				
//				
//				if(strApprovedDate!=null){
//					
//					Calendar calCurrent = GregorianCalendar.getInstance();
//					calCurrent.setTime(uF.getCurrentDate(CF.getStrTimeZone()));
//					
//					int nCurrentMonth = calCurrent.get(Calendar.MONTH);
//					
//					Calendar calApproved = GregorianCalendar.getInstance();
//					calApproved.setTime(uF.getDateFormat(strApprovedDate, DBDATE));
//					
//					int nApprovedMonth = calApproved.get(Calendar.MONTH);
//					calApproved.add(Calendar.MONTH, (int)dblDuration);
//					
//					int nLastMonth = calApproved.get(Calendar.MONTH);
//					String strLastDate = calApproved.get(Calendar.DATE) +"/"+(calApproved.get(Calendar.MONTH)+1)+"/"+calApproved.get(Calendar.YEAR);
//					int nBalanceMonths = uF.parseToInt(uF.dateDifference(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, strLastDate, DATE_FORMAT,CF.getStrTimeZone()));
//					nBalanceMonths = (int)nBalanceMonths/30;
//					
//					
//					
//					
////					dblCalculatedAmount = uF.getEMI(dblBalAmt, dblROI, nBalanceMonths);
//					dblCalculatedAmount = uF.getEMI(dblPrincipalAmt, dblROI, dblDuration);
//					dblCalculatedAmount = dblCalculatedAmount / dblDuration; 
//					
//					
//					if(dblCalculatedAmount>=dblBalAmt){
//						dblCalculatedAmount = dblBalAmt;
//					}
//					if(dblCalculatedAmount>dblGross){
//						dblCalculatedAmount = dblGross;
//					}
//					dblTotalCalculatedAmount +=dblCalculatedAmount;
//					hmLoanAmt.put(rs.getString("loan_applied_id"), uF.formatIntoTwoDecimal(dblCalculatedAmount));
//					
//					
//					hmEmpLoanInner = (Map)hmEmpLoan.get(rs.getString("emp_id"));
//					if(hmEmpLoanInner==null)hmEmpLoanInner=new HashMap();
//					hmEmpLoanInner.put(rs.getString("loan_id"), uF.formatIntoTwoDecimal(dblCalculatedAmount));
//					hmEmpLoan.put(rs.getString("emp_id"), hmEmpLoanInner);
//					
//					if(!alLoans.contains(rs.getString("loan_id"))){
//						alLoans.add(rs.getString("loan_id"));
//					}
//					
//				}
//			}
//			
//			request.setAttribute("hmEmpLoan", hmEmpLoan);
//			request.setAttribute("alLoans", alLoans);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		}
//		return dblTotalCalculatedAmount;
//		
//	}
//	
//	public double calculateEEESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strWLocationStateId, Map hmVariables, String strEmpId){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rs = null;
//		double dblCalculatedAmount = 0;
//		
//		try {
//			
//			pst = con.prepareStatement(selectESI);
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(strWLocationStateId));
//			rs = pst.executeQuery();
//			
////			System.out.println("pst====>"+pst);
//			
//			double dblEEESIAmount = 0;
//			double dblESIMaxAmount = 0;
//			String strSalaryHeads = null;
//			while(rs.next()){
//				dblEEESIAmount = rs.getDouble("eesi_contribution");
//				dblESIMaxAmount = rs.getDouble("max_limit");
//				strSalaryHeads = rs.getString("salary_head_id");
//			}
//
//			String []arrSalaryHeads = null;
//			if(strSalaryHeads!=null){
//				arrSalaryHeads = strSalaryHeads.split(",");
//			}
//			
//			
//			
//			
//			
//			
//			
//			double dblAmount = 0;
//			double dblAmountEligibility = 0; 
//			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")){
//					dblAmountEligibility += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//				}
//				dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));	
//			}
//			
//			if(dblAmountEligibility<dblESIMaxAmount){
//				dblCalculatedAmount = (( dblEEESIAmount * dblAmount ) / 100);
//			}
//			
//			/*if(dblAmountEligibility<dblESIMaxAmount){
//				dblCalculatedAmount = (( dblEEESIAmount * dblAmountEligibility ) / 100);
//			}*/
//			
//			
//			
////			System.out.println("strSalaryHeads="+strSalaryHeads);
////			System.out.println("dblCalculatedAmount="+dblCalculatedAmount);
////			System.out.println("dblAmount="+dblAmount);
////			System.out.println("dblESIMaxAmount="+dblESIMaxAmount);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		}
//		return dblCalculatedAmount;
//		
//	}
//	
//	public double calculateERESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strWLocationStateId){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rs = null;
//		double dblCalculatedAmount = 0;
//		
//		
//		try {
//			
//			pst = con.prepareStatement(selectERESI);
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(strWLocationStateId));
//			rs = pst.executeQuery();
//			
//			
//			
//			double dblERESIAmount = 0;
//			double dblESIMaxAmount = 0;
//			String strSalaryHeads = null;
//			while(rs.next()){
//				dblERESIAmount = rs.getDouble("ersi_contribution");
//				dblESIMaxAmount = rs.getDouble("max_limit");
//				strSalaryHeads = rs.getString("salary_head_id");
//			}
//
//			String []arrSalaryHeads = null;
//			if(strSalaryHeads!=null){
//				arrSalaryHeads = strSalaryHeads.split(",");
//			}
//			
//			
//			double dblAmount = 0;
//			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//				dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//			}
//			
//			if(dblAmount<dblESIMaxAmount){
//				dblCalculatedAmount = (( dblERESIAmount * dblAmount ) / 100);
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		}
//		return dblCalculatedAmount;
//	}
//	
//	
//	
//	public double calculateEELWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strWLocationStateId, Map hmVariables, String strEmpId, int nPayMonth, String strOrgId){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rs = null;
//		double dblCalculatedAmount = 0;
//		
//		try {
//			
//			pst = con.prepareStatement(selectLWF);
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(strWLocationStateId));
//			pst.setInt(4, uF.parseToInt(strOrgId));
//			rs = pst.executeQuery();
//			
//			double dblERLWFAmount = 0;
//			double dblLWFMaxAmount = 0;
//			String strSalaryHeads = null;
//			String strMonths = null;
//			while(rs.next()){
//				dblERLWFAmount = rs.getDouble("eelfw_contribution");
//				dblLWFMaxAmount = rs.getDouble("max_limit");
//				strSalaryHeads = rs.getString("salary_head_id");
//				strMonths  = rs.getString("months");
//			}
//
//			
//			String []arrMonths = null;
//			if(strMonths!=null){
//				arrMonths = strMonths.split(",");
//			}
//			
//			if(ArrayUtils.contains(arrMonths, nPayMonth+"")>=0){
//				
//				String []arrSalaryHeads = null;
//				if(strSalaryHeads!=null){
//					arrSalaryHeads = strSalaryHeads.split(",");
//				}
//				
//				
//				double dblAmount = 0;
//				for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//					dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//				}
//				
//				
//				pst = con.prepareStatement(selectERLWFC);
//				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(strWLocationStateId));
//				pst.setDouble(4, dblAmount);
//				pst.setDouble(5, dblAmount);
//				pst.setInt(6, uF.parseToInt(strOrgId));
//				rs = pst.executeQuery();
//				
//				while(rs.next()){
//					dblCalculatedAmount = uF.parseToDouble(rs.getString("eelfw_contribution"));
//				}
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblCalculatedAmount;
//	}
//	
//	public double calculateERLWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strWLocationStateId, int nPayMonth, String strOrgId){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rs = null;
//		double dblCalculatedAmount = 0;
//		
//		
//		try {
//			
//			pst = con.prepareStatement(selectERLWF);
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(strWLocationStateId));
//			pst.setInt(4, uF.parseToInt(strOrgId));
//			rs = pst.executeQuery();
//			double dblERLWFAmount = 0;
//			double dblLWFMaxAmount = 0;
//			String strSalaryHeads = null;
//			String strMonths = null;
//			while(rs.next()){
//				dblERLWFAmount = rs.getDouble("erlfw_contribution");
//				dblLWFMaxAmount = rs.getDouble("max_limit");
//				strSalaryHeads = rs.getString("salary_head_id");
//				strMonths  = rs.getString("months");
//			}
//
//			String []arrMonths = null;
//			if(strMonths!=null){
//				arrMonths = strMonths.split(",");
//			}
//			
//			if(ArrayUtils.contains(arrMonths, nPayMonth+"")>=0){
//				String []arrSalaryHeads = null;
//				if(strSalaryHeads!=null){
//					arrSalaryHeads = strSalaryHeads.split(",");
//				}
//				
//				
//				double dblAmount = 0;
//				for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//					dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//				}
//				
//				
//				pst = con.prepareStatement(selectERLWFC);
//				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt(strWLocationStateId));
//				pst.setDouble(4, dblAmount);
//				pst.setDouble(5, dblAmount);
//				pst.setInt(6, uF.parseToInt(strOrgId));
//				rs = pst.executeQuery();
//				
//				while(rs.next()){
//					dblCalculatedAmount = uF.parseToDouble(rs.getString("erlfw_contribution"));
//				}
//			}
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		}
//		return dblCalculatedAmount;
//	}
//
//	public double calculateEEPF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, Map hmVoluntaryPF, String strEmpId, String strMonth, String strPaycycle, boolean isInsert){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rs = null;
//		double dblCalculatedAmount = 0;
//		
//		
//		try {
//			
//			pst = con.prepareStatement(selectEEPF);
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			
//			rs = pst.executeQuery();
//			
//			
//			
//			double dblEEPFAmount = 0;
//			double dblMaxAmount = 0;
//			String strSalaryHeads = null;
//			while(rs.next()){
//				dblEEPFAmount = rs.getDouble("eepf_contribution");
//				dblMaxAmount = rs.getDouble("epf_max_limit");
//				strSalaryHeads = rs.getString("salary_head_id");
//			}
//
//			String []arrSalaryHeads = null;
//			if(strSalaryHeads!=null){
//				arrSalaryHeads = strSalaryHeads.split(",");
//			}
//			
//			
//			double dblAmount = 0;
//			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//				dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//			}
//			
//			/**
//			 * Change on 24-04-2012
//			 */
//			
//			if(dblAmount>=dblMaxAmount){
//				dblAmount = dblMaxAmount;
//				
//			}
//			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
//			
////			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
////			if(dblCalculatedAmount>=dblMaxAmount){
////				dblCalculatedAmount = dblMaxAmount;
////			}
//			
////			System.out.println("dblCalculatedAmount===>"+dblCalculatedAmount);
//			
//			
//			
//			/**
//			 * If VPF is to be calculated separately,
//			 * the the below code needs to be commented
//			 * 
//			 * */
//			
////			if(hmVoluntaryPF==null){
////				hmVoluntaryPF = new HashMap();
////			}
////			dblCalculatedAmount += uF.parseToDouble((String)hmVoluntaryPF.get("AMOUNT"));
////			
//			
//			
//			
//			
//			
//			if(isInsert){
//				
//				double dblEVPF = uF.parseToDouble((String)hmTotal.get(VOLUNTARY_EPF+""));
//				
//					
//				pst = con.prepareStatement("insert into emp_epf_details (financial_year_start, financial_year_end, salary_head_id, epf_max_limit, eepf_contribution, emp_id, paycycle, _month, evpf_contribution) values (?,?,?,?,?,?,?,?,?)");
//				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setString(3, strSalaryHeads);
//				pst.setDouble(4, Math.round(dblAmount));
//				pst.setDouble(5, Math.round(dblCalculatedAmount)); 
//				pst.setInt(6, uF.parseToInt(strEmpId));
//				pst.setInt(7, uF.parseToInt(strPaycycle));
//				pst.setInt(8, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
//				pst.setDouble(9, Math.round(dblEVPF));
//				pst.execute();
//			}
//			
//			
//			
//			
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		}
//		return dblCalculatedAmount;
//		
//	}
//
//	public double calculateERPF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal,  String strEmpId, String strMonth, String strPaycycle, boolean isInsert){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		
//		double dblEPS1 = 0;
//		double dblEPS = 0;
//		double dblEPF = 0;
//		double dblEDLI = 0;
//		
//		double dblEPFAdmin = 0;
//		double dblEDLIAdmin = 0;
//		
//		double dblTotalEPF = 0;
//		double dblTotalEDLI = 0;
//		
//		
//		try {
//			
//			pst = con.prepareStatement(selectERPF);
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			
//			rs = pst.executeQuery();
//			
//			
//			
//			
//			double dblERPFAmount = 0;
//			double dblERPSAmount = 0;
//			double dblERDLIAmount = 0;
//			double dblPFAdminAmount = 0;
//			double dblEDLIAdminAmount = 0;
//			double dblEPFMaxAmount = 0;
//			double dblEPRMaxAmount = 0;
//			double dblEPSMaxAmount = 0;
//			double dblEDLIMaxAmount = 0;
//			String strSalaryHeads = null;
//			
//			
//			boolean erpfContributionchbox = false;
//			boolean erpsContributionchbox = false;
//			boolean pfAdminChargeschbox = false;
//			boolean edliAdminChargeschbox = false;
//			boolean erdliContributionchbox = false;
//			
//			while(rs.next()){
//				
//				dblERPFAmount = rs.getDouble("erpf_contribution");
//				dblERPSAmount = rs.getDouble("erps_contribution");
//				dblERDLIAmount = rs.getDouble("erdli_contribution");
//				dblPFAdminAmount = rs.getDouble("pf_admin_charges");
//				dblEDLIAdminAmount = rs.getDouble("edli_admin_charges");
//				
//				dblEPRMaxAmount = rs.getDouble("erpf_max_limit");
//				dblEPFMaxAmount = rs.getDouble("epf_max_limit");
//				dblEPSMaxAmount = rs.getDouble("eps_max_limit");
//				dblEDLIMaxAmount = rs.getDouble("edli_max_limit");
//				
//				strSalaryHeads = rs.getString("salary_head_id");
//				
//				
//				erpfContributionchbox = rs.getBoolean("is_erpf_contribution");
//				erpsContributionchbox = rs.getBoolean("is_erps_contribution");
//				pfAdminChargeschbox = rs.getBoolean("is_pf_admin_charges");
//				edliAdminChargeschbox = rs.getBoolean("is_edli_admin_charges");
//				erdliContributionchbox = rs.getBoolean("is_erdli_contribution");
//			}
//
//			String []arrSalaryHeads = null;
//			if(strSalaryHeads!=null){
//				arrSalaryHeads = strSalaryHeads.split(",");
//			}
//			
//			
//			double dblAmount = 0;
//			double dblAmountERPF = 0;
//			double dblAmountEEPF = 0;
//			double dblAmountERPS = 0;
//			double dblAmountERPS1 = 0;
//			double dblAmountEREDLI = 0;
//			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//				dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//			}
//			
//			
//			/**
//			 * Changed on 24-04-2012
//			 * 
//			 */
//			
//			
//			
////			System.out.println("strEmpID=========>"+strEmpID);
////			
////			
////			System.out.println("dblAmount===="+dblAmount);
////			System.out.println("dblEPFMaxAmount===="+dblEPFMaxAmount);
////			
//			
//			if(dblAmount>=dblEPRMaxAmount){
//				dblAmountERPF = dblEPRMaxAmount;
//			}else{
//				dblAmountERPF = dblAmount;
//			}
//			
//			if(dblAmount>=dblEPFMaxAmount){
//				dblAmountEEPF = dblEPFMaxAmount;
//			}else{
//				dblAmountEEPF = dblAmount;
//			}
//			
//			
//			dblAmountERPS1 = dblAmount;
//			if(dblAmount>=dblEPSMaxAmount){
//				dblAmountERPS = dblEPSMaxAmount;
//			}else{
//				dblAmountERPS = dblAmount;
//			}
//			
//			if(dblAmount>=dblEDLIMaxAmount){
//				dblAmountEREDLI = dblEDLIMaxAmount;
//			}else{
//				dblAmountEREDLI = dblAmount;
//			}
//			
//			
//			
//			if(isInsert){
//				dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
//				dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
//				
//				dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
//				
//				dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
//				dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
//				dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
//			}else{
//				if(erpfContributionchbox){
//					dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
//				}
//				if(erpsContributionchbox){
//					dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
//					dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
//				}
//					
//				if(erdliContributionchbox){
//					dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
//				}
//				
//				if(edliAdminChargeschbox){
//					dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
//				}
//				if(pfAdminChargeschbox){
//					dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
//				}
//			}
//			
//			if(CF.isEPF_Condition1()){
//				dblEPF += dblEPS1 - dblEPS;
//			}
//			
//			dblTotalEDLI = dblEDLI + dblEDLIAdmin;
//			dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;
//			
//			
//			if(isInsert){
//				pst = con.prepareStatement("update emp_epf_details set  eps_max_limit=?, edli_max_limit=?, erpf_contribution=?, erps_contribution=?, erdli_contribution=?, pf_admin_charges=?, edli_admin_charges=?  where financial_year_start=? and financial_year_end=? and emp_id=? and paycycle=? and _month=?");
//				pst.setDouble(1, Math.round(dblAmountERPS));
//				pst.setDouble(2, Math.round(dblAmountEREDLI));
//				pst.setDouble(3, Math.ceil(dblEPF));
//				pst.setDouble(4, Math.round(dblEPS));
//				pst.setDouble(5, Math.round(dblEDLI));
//				pst.setDouble(6, Math.round(dblEPFAdmin));
//				// pst.setDouble(7, uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEDLIAdmin))); // only liftshift
//				pst.setDouble(7, Math.round(dblEDLIAdmin));
//				pst.setDate(8, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(9, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setInt(10, uF.parseToInt(strEmpId));
//				pst.setInt(11, uF.parseToInt(strPaycycle));
//				pst.setInt(12, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM"))); 
//				pst.execute();
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//			
//		}
//		return (dblTotalEPF + dblTotalEDLI);
//		
//	}
//	
//	
//	public void calculateEESI(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map hmVariables, boolean isInsert){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rs = null;
//		double dblCalculatedAmountEESI = 0;
//		double dblCalculatedAmountERSI = 0;
//		
//		
//		try {
//			
//			pst = con.prepareStatement(selectEESI);
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
//			
//			rs = pst.executeQuery();
//			
//			double dblEESIAmount = 0;
//			double dblERSIAmount = 0;
//			double dblMaxAmount = 0;
//			String strSalaryHeads = null;
//			while(rs.next()){
//				dblEESIAmount = rs.getDouble("eesi_contribution");
//				dblERSIAmount = rs.getDouble("ersi_contribution");
//				dblMaxAmount = rs.getDouble("max_limit");
//				strSalaryHeads = rs.getString("salary_head_id");
//			}
//
//			String []arrSalaryHeads = null;
//			if(strSalaryHeads!=null){
//				arrSalaryHeads = strSalaryHeads.split(",");
//			}
//			
//			
//			double dblAmount = 0;
//			double dblAmountEligibility = 0;
//			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")){
//					dblAmountEligibility += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//				}
//				dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//			}
//			
//			/**
//			 * Change on 24-04-2012
//			 */
//			
//			if(dblAmountEligibility>=dblMaxAmount){
//				return;
//			}
//			
//			dblCalculatedAmountEESI = ( dblEESIAmount * dblAmount ) / 100;
//			dblCalculatedAmountERSI = ( dblERSIAmount * dblAmount ) / 100;
//			
////			dblCalculatedAmountEESI = ( dblEESIAmount * dblAmountEligibility ) / 100;
////			dblCalculatedAmountERSI = ( dblERSIAmount * dblAmountEligibility ) / 100;
//			
//			
////			dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
////			if(dblCalculatedAmount>=dblMaxAmount){
////				dblCalculatedAmount = dblMaxAmount;
////			}
//			
//			
//			
//			
//			if(isInsert){
//				pst = con.prepareStatement("insert into emp_esi_details (financial_year_start, financial_year_end, salary_head_id, esi_max_limit, eesi_contribution, ersi_contribution, user_id, entry_timestamp, emp_id, paycycle, _month) values (?,?,?,?,?,?,?,?,?,?,?)");
//				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setString(3, strSalaryHeads);
//				pst.setDouble(4, Math.ceil(dblAmount)); 
//				pst.setDouble(5, Math.ceil(dblCalculatedAmountEESI));
//				pst.setDouble(6, Math.ceil(dblCalculatedAmountERSI));
//				pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
//				pst.setTimestamp(8, null);
//				pst.setInt(9, uF.parseToInt(strEmpId));
//				pst.setInt(10, uF.parseToInt(strPaycycle));
//				pst.setInt(11, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
//				pst.execute();
//				
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//	}
//
//	
//	public void calculateELWF(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map hmVariables, boolean isInsert, String strOrgId){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rs = null;
//		double dblCalculatedAmountEELWF = 0;
//		double dblCalculatedAmountERLWF = 0;
//		
//		
//		try {
//			
//			pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? and org_id=?");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
//			pst.setInt(4, uF.parseToInt(hmEmpStateMap.get(strOrgId)));
////			System.out.println("pst====>"+pst);
//			rs = pst.executeQuery();
//			
//			String strSalaryHeads = null;
//			while(rs.next()){
//				strSalaryHeads = rs.getString("salary_head_id");
//			}
//			
//			String[] arrSalaryHeads = null;
//			if(strSalaryHeads!=null){
//				arrSalaryHeads = strSalaryHeads.split(",");
//			}
//			
//			
//			double dblAmount = 0;
//			double dblAmountEligibility = 0;
//			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
//				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")){
//					dblAmountEligibility += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//				}
//				dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
//			} 
//			
//			pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? " +
//					" and ? between min_limit and max_limit and org_id=? ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(hmEmpStateMap.get(strEmpId)));
//			pst.setDouble(4,dblAmount);
//			pst.setInt(5, uF.parseToInt(hmEmpStateMap.get(strOrgId)));
////			System.out.println("pst====>"+pst);
//			rs = pst.executeQuery();
//			
//			double dblEELWFAmount = 0;
//			double dblERLWFAmount = 0;
//			double dblMaxAmount = 0;
//			String lwfMonth=null;
//			while(rs.next()){
//				dblEELWFAmount = rs.getDouble("eelfw_contribution");
//				dblERLWFAmount = rs.getDouble("erlfw_contribution");
//				dblMaxAmount = rs.getDouble("max_limit");
//				lwfMonth=rs.getString("months");
//			}			
//			
//			if(dblAmountEligibility>=dblMaxAmount){
//				return;
//			}
//			
//			
//			List<String> lwfMonthList=null;
//			if(lwfMonth!=null){
//				lwfMonthList=Arrays.asList(lwfMonth.split(","));
//			}
//			
//			
//			int month=uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM"));
//			if(lwfMonthList==null || !lwfMonthList.contains(""+month)){
//				return;
//			}
//			
//			dblCalculatedAmountEELWF = dblEELWFAmount ;
//			dblCalculatedAmountERLWF = dblERLWFAmount;
//			
//			
//			if(isInsert){
//				pst = con.prepareStatement("insert into emp_lwf_details (financial_year_start, financial_year_end, salary_head_id, " +
//						"lwf_max_limit, eelwf_contribution, erlwf_contribution, user_id, entry_timestamp, emp_id, paycycle, _month) " +
//						"values (?,?,?,?,?,?,?,?,?,?,?)");
//				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setString(3, strSalaryHeads);
//				pst.setDouble(4, Math.round(dblAmount)); 
//				pst.setDouble(5, Math.round(dblCalculatedAmountEELWF));
//				pst.setDouble(6, Math.round(dblCalculatedAmountERLWF));
//				pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
//				pst.setTimestamp(8, null);
//				pst.setInt(9, uF.parseToInt(strEmpId));
//				pst.setInt(10, uF.parseToInt(strPaycycle));
//				pst.setInt(11, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
//				pst.execute();
////				System.out.println("pst====>"+pst);
//				
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//	}
//
//	public double calculateTDS(Connection con, UtilityFunctions uF, double dblGross, double dblCess1, double dblCess2, double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblBasicDA,
//		int nPayMonth, String strPaycycleStart, String strFinancialYearStart, String strFinancialYearEnd,String strEmpId, String strGender, String strAge, String strWLocationStateId,
//		Map hmEmpExemptionsMap, Map hmEmpHomeLoanMap, Map hmFixedExemptions, Map hmEmpMertoMap, Map hmEmpRentPaidMap, Map hmPaidSalaryDetails, Map hmTotal, Map hmSalaryDetails, Map hmEmpLevelMap, CommonFunctions CF){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rst = null, rs = null;
//		double dblTDSMonth = 0;
//		
//		
//		try {
//			 
//			
//			
//			if(uF.parseToBoolean((String)hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))){
//				
//				dblTDSMonth = dblGross * dblFlatTDS / 100;
////				dblTDSMonth += (dblCess1 * 0.01 * dblTDSMonth) + (dblCess2 * 0.01 * dblTDSMonth); 
//				
//			}else{
//				
//				/**
//				 * TDS Projection
//				 * If there is any amount specified for TDS to be deducted in projection table, 
//				 * then it will consider that amount as a TDS for that particular month and else
//				 * it will calculate the TDS based on the actual calculations.
//				 * */
//				String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
//				int slabType = uF.parseToInt(strSlabType);
//				
//				pst = con.prepareStatement("select * from tds_projections where emp_id =? and month=? and fy_year_from=? and fy_year_end=?");
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setInt(2, nPayMonth);
//				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				rs = pst.executeQuery();
//				if(rs.next()){
//					dblTDSMonth = rs.getDouble("amount");
//					return dblTDSMonth;
//				}
//				
//				
//				pst = con.prepareStatement(selectTDS);
//				pst.setInt(1, TDS);
//				pst.setInt(2, uF.parseToInt(strEmpId));
//				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				rs = pst.executeQuery();
//				double dblTDSPaidAmount = 0;
//				while(rs.next()){
//					dblTDSPaidAmount = rs.getDouble("tds");
//				}
//				
//				
////				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation pg, salary_details sd where pg.salary_head_id=sd.salary_head_id and emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ?");
//				pst = con.prepareStatement(selectTDS1);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				
//				rs = pst.executeQuery();
//				
//				double dblGrossPaidAmount = 0;
//				while(rs.next()){
//					dblGrossPaidAmount = rs.getDouble("amount");
//				}
//				
//				
//				String strMonthsLeft = uF.dateDifference(strPaycycleStart, DATE_FORMAT, strFinancialYearEnd, DATE_FORMAT,CF.getStrTimeZone());
//				int nMonthsLeft = Math.round(uF.parseToInt(strMonthsLeft) / 30);
//				
//				/*
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					
//					System.out.println("strPaycycleStart==="+strPaycycleStart);
//					System.out.println("strFinancialYearEnd==="+strFinancialYearEnd);
//					System.out.println("strMonthsLeft==="+strMonthsLeft);
//					System.out.println("dblGrossPaidAmount==="+dblGrossPaidAmount);
//				}*/
//				
//				
//				/**
//				 * 			ALL EXEMPTION WILL COME HERE
//				 * **/
//				double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
//				double dblHomeLoanExemtion = uF.parseToDouble((String)hmEmpHomeLoanMap.get(strEmpId));
//				
//				
//				double dblEEEPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYEE_EPF+""));
//				double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYEE_EPF+""));
//				
//				double dblEREPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYER_EPF+""));
//				double dblEREPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYER_EPF+""));
//				
//				
//				
////				double dblTotalInvestment = dblInvestment + dblEEEPFPaid + dblEEEPFToBePaid+ dblEREPFPaid + dblEREPFToBePaid;
//				double dblTotalInvestment = dblInvestment + dblEREPFPaid + dblEREPFToBePaid;
//				
//				
////				if(dblTotalInvestment>=dblDeclaredInvestmentExemption){
////					dblTotalInvestment = dblDeclaredInvestmentExemption;
////				}
//				
//				
//				double dblHRAExemptions = getHRAExemptionCalculation(con, uF, hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap);
//				
//				double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions; 
//				
//				
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("dblExemptions==="+dblExemptions);
//
//					System.out.println("dblHRAExemptions==="+dblHRAExemptions);
//					System.out.println("dblHomeLoanExemtion==="+dblHomeLoanExemtion);
//					System.out.println("dblTotalInvestment==="+dblTotalInvestment);
//					
//					System.out.println("dblInvestment==="+dblInvestment);
//					System.out.println("dblEEEPFPaid==="+dblEEEPFPaid);
//					System.out.println("dblEEEPFToBePaid==="+dblEEEPFToBePaid);
//					System.out.println("dblEREPFPaid==="+dblEREPFPaid);
//					System.out.println("dblEREPFToBePaid==="+dblEREPFToBePaid);
//					
//				}
//				
//				
////				System.out.println("dblInvestment=========+>"+dblInvestment);
////				System.out.println("dblTotalInvestment=========+>"+dblTotalInvestment);
////				System.out.println("dblHRAExemptions=========+>"+dblHRAExemptions);
////				System.out.println("dblExemptions=========+>"+dblExemptions);   
//				
//				
//				Set set = hmSalaryDetails.keySet();
//				Iterator it = set.iterator();
//				while(it.hasNext()){
//					String strSalaryHeadId = (String)it.next();
//					String strSalaryHeadName = (String)hmSalaryDetails.get(strSalaryHeadId);
//					
//					if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//						System.out.println(strSalaryHeadId+"  " +strSalaryHeadName+"  hmFixedExemptions="+hmFixedExemptions);
//						System.out.println("hmFixedExemptions.containsKey(strSalaryHeadName)="+hmFixedExemptions.containsKey(strSalaryHeadName));
//						System.out.println("hmTotal="+hmTotal);
//						
//					}
//					
//					if(hmFixedExemptions.containsKey(strSalaryHeadName)){
//						
//						double dblIndividualExemption = uF.parseToDouble((String)hmFixedExemptions.get(strSalaryHeadName));
//						
//						
////						System.out.println(" dblIndividualExemption=========+>"+dblIndividualExemption);
//						
//						double dblTotalToBePaid = 0;
//						if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX){
//							int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
//							double dblCurrentMonthGross = uF.parseToDouble((String)hmTotal.get("GROSS"));
//							dblTotalToBePaid = (nMonthsLeft-1) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
//							dblTotalToBePaid += calculateProfessionalTax(con, uF, dblCurrentMonthGross, strFinancialYearEnd, nLastPayMonth, strWLocationStateId);
//						}else{
//							dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
//						}
//						
//						double dblTotalPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(strSalaryHeadId));
//						double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;  
//						double dblExmp = 0;
//						if(dblTotalPaidAmount >= dblIndividualExemption){
//							dblExemptions += dblIndividualExemption;
//							dblExmp = dblIndividualExemption;
//						}else{
//							dblExemptions += dblTotalPaidAmount;
//							dblExmp = dblTotalPaidAmount;
//						}
//						
////						System.out.println(" dblTotalPaidAmount=========+>"+dblTotalPaidAmount);
////						System.out.println(" dblIndividualExemption=========+>"+dblIndividualExemption);
////						System.out.println(" dblExemptions=========+>"+dblExemptions);
//						
//						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//							System.out.println(strSalaryHeadId+" == dblExmp==="+dblExmp+" dblExemptions=="+dblExemptions);
//							System.out.println("dblTotalPaid=="+dblTotalPaid+" dblTotalToBePaid==="+dblTotalToBePaid);
//
//						}
//						
//						log.debug("dblExemption="+dblIndividualExemption);
//						log.debug("dblTotalToBePaid="+dblTotalToBePaid);
//						log.debug("dblTotalPaid="+dblTotalPaid);
//						log.debug("dblTotalPaidAmount="+dblTotalPaidAmount);
//						log.debug("dblExemptions="+dblExemptions);
//						
//						log.debug(strEmpId+"============"+strSalaryHeadName+"===========");
//						
//					}
//				}
//				
//				double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross);
//				
//				
//				log.debug("Invest Exmp="+(String)hmEmpExemptionsMap.get(strEmpId));
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
//				double dblTotalTaxableSalary = 0;
//				if(dblTotalGrossSalary>dblExemptions){
//					dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
//				}else if(dblTotalGrossSalary>0 && dblExemptions>0 && dblTotalGrossSalary<=dblExemptions){
//					dblTotalTaxableSalary = 0;
//				}
//				
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("dblTotalGrossSalary==="+dblTotalGrossSalary);
//					System.out.println("dblExemptions==="+dblExemptions);
//					System.out.println("dblTotalTaxableSalary==="+dblTotalTaxableSalary);
//					System.out.println("(nMonthsLeft * dblGross)==="+(nMonthsLeft * dblGross));
//				}
//				
//				int countBug = 0;
//				double dblTotalTDSPayable = 0.0d;
//				double dblUpperDeductionSlabLimit = 0;
//				double dblLowerDeductionSlabLimit = 0;
//				double dblTotalNetTaxableSalary = 0; 
//					
//				do{
//					
//					pst = con.prepareStatement(selectDeduction);
//					pst.setDouble(1, uF.parseToDouble(strAge));
//					pst.setDouble(2, uF.parseToDouble(strAge));
//					pst.setString(3, strGender);
//					pst.setDate(4, uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT));
//					pst.setDate(5, uF.getDateFormat(CF.getStrFinancialYearTo(), DATE_FORMAT));
//					pst.setDouble(6, dblTotalTaxableSalary);
//					pst.setDouble(7, dblUpperDeductionSlabLimit);
//					pst.setInt(8, slabType);
//					rs = pst.executeQuery();
//					
////					System.out.println("pst=====>"+pst);  
//					
//					if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
////						System.out.println("pst=====>"+pst);
//					}
//					
//					double dblDeductionAmount = 0;
//					String strDeductionType = null;
//					
//					if(rs.next()){
//						dblDeductionAmount = rs.getDouble("deduction_amount");
//						strDeductionType = rs.getString("deduction_type");
//						dblUpperDeductionSlabLimit = rs.getDouble("_to");
//						dblLowerDeductionSlabLimit = rs.getDouble("_from");
//					}
//					
//					if(countBug==0){
//						dblTotalNetTaxableSalary = dblTotalTaxableSalary;
//					}
//					
//					
//					
//					
//					if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit){
//						dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
//						
////						log.debug("dblTotalTaxableSalary 1 ="+((dblDeductionAmount /100) *  dblUpperDeductionSlabLimit ));
//						
//						
//						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//							System.out.println("=====IF=========");
//							System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
//							System.out.println("dblUpperDeductionSlabLimit=====>"+dblUpperDeductionSlabLimit);
//							System.out.println("dblLowerDeductionSlabLimit=====>"+dblLowerDeductionSlabLimit);
//							System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
//							System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
//						}
//						
//						
//					}else{
//						
//						if(countBug==0){
//							dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
//						}
//						
//						dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
//					
//						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//							System.out.println("=====ELSE=========");
//							
//							System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
//							System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
//							System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
//						
//						}
//					}
//					
//					dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;
//					
//					
////					log.debug("dblTotalTaxableSalary="+dblTotalTaxableSalary);
////					log.debug("dblUpperDeductionSlabLimit="+dblUpperDeductionSlabLimit);
////					log.debug("dblTotalTDSPayable="+dblTotalTDSPayable);
////					log.debug("dblTotalTaxableSalary="+dblTotalTaxableSalary);
////					log.debug("dblDeductionAmount="+dblDeductionAmount);
////					log.debug("dblTotalNetTaxableSalary="+dblTotalNetTaxableSalary);
//					
//					
//					
////					log.debug("dblExemptions="+dblExemptions);
////					log.debug("dblTotalGrossSalary="+dblTotalGrossSalary);
////					log.debug("dblTotalTDSPayable="+dblTotalTDSPayable);
////					log.debug("dbGrossSalarySlab="+dbGrossSalarySlab);
////					log.debug("dblTDSMonth="+dblTDSMonth);
//
//					if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
//					countBug++;
//					
//				}while(dblTotalNetTaxableSalary>0);
//				
//				
//				// Service tax + Education cess
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("dblTotalTDSPayable=======>"+dblTotalTDSPayable);
//				}
//				
//				
//				
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
//					
//				dblTDSMonth = dblTotalTDSPayable - dblTDSPaidAmount;
//				dblTDSMonth = dblTDSMonth/(nMonthsLeft);
//				
//				if(dblTDSMonth<0){
//					dblTDSMonth = 0;
//				}
//				
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					
//					System.out.println("dblCess1=======>"+dblCess1);
//					System.out.println("dblCess2=======>"+dblCess2);
//					System.out.println("dblCess2=======>"+dblCess2);
//					System.out.println("dblTotalTDSPayable=======>"+dblTotalTDSPayable);
//					System.out.println("dblTDSPaidAmount=======>"+dblTDSPaidAmount);
//					System.out.println("nMonthsLeft=======>"+nMonthsLeft);
//				}
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
//		}
//		return dblTDSMonth;
//	}
//	
//	
//
//	
//	public double calculateTDSA(Connection con, UtilityFunctions uF, double dblGross, double dblCess1, double dblCess2, double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblBasicDA,
//		int nPayMonth, String strPaycycleStart, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge, String strWLocationStateId,
//		Map hmEmpExemptionsMap, Map hmEmpHomeLoanMap, Map hmFixedExemptions, Map hmEmpMertoMap, Map hmEmpRentPaidMap, Map hmPaidSalaryDetails, Map hmTotal, Map hmSalaryDetails, Map hmEmpLevelMap, CommonFunctions CF, int nMonthsLeft){
//		
//		PreparedStatement pst = null, pst1 = null;
//		ResultSet rst = null, rs = null;
//		double dblTDSMonth = 0;
//		
//		try {
//			
//			if(uF.parseToBoolean((String)hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))){
//				
//				dblTDSMonth = dblGross * dblFlatTDS / 100;
////				dblTDSMonth += (dblCess1 * 0.01 * dblTDSMonth) + (dblCess2 * 0.01 * dblTDSMonth); 
//				
//			} else {
//				
//				String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
//				int slabType = uF.parseToInt(strSlabType);
//				
//				pst = con.prepareStatement(selectTDS);
//				pst.setInt(1, TDS);
//				pst.setInt(2, uF.parseToInt(strEmpId));
//				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				
//				rs = pst.executeQuery();
//				
//				double dblTDSPaidAmount = 0;
//				while(rs.next()){
//					dblTDSPaidAmount = uF.parseToDouble(rs.getString("tds"));
//				}
//				
//				pst1= con.prepareStatement(selectTDS2);
//				pst1.setInt(1, TDS);
//				pst1.setInt(2, uF.parseToInt(strEmpId));
//				pst1.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst1.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst1.setDate(5, uF.getDateFormat(strPaycycleStart, DATE_FORMAT));
//				ResultSet rs1 = pst1.executeQuery();
//				
//				double dblTDSPaidAmount1 = 0;
//				int count = 0; 
//				while(rs1.next()){
//					dblTDSPaidAmount += rs1.getDouble("amount");
//					count++;
//				}
//				
////				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation pg, salary_details sd where pg.salary_head_id=sd.salary_head_id and emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ?");
//				pst = con.prepareStatement(selectTDS1);
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				
//				rs = pst.executeQuery();
//				
//				double dblGrossPaidAmount = 0;
//				while(rs.next()){
//					dblGrossPaidAmount = rs.getDouble("amount");
//				}
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					
//					System.out.println("strPaycycleStart==="+strPaycycleStart);
//					System.out.println("strFinancialYearEnd==="+strFinancialYearEnd);
//					System.out.println("dblGrossPaidAmount==="+dblGrossPaidAmount);
//				}
//				
//				
//				/**
//				 * 			ALL EXEMPTION WILL COME HERE
//				 * **/
//				double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
//				double dblHomeLoanExemtion = uF.parseToDouble((String)hmEmpHomeLoanMap.get(strEmpId));
//				
//				double dblEEEPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYEE_EPF+""));
//				double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYEE_EPF+""));
//				
//				double dblEREPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYER_EPF+""));
//				double dblEREPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYER_EPF+""));
//				
////				double dblTotalInvestment = dblInvestment + dblEEEPFPaid + dblEEEPFToBePaid+ dblEREPFPaid + dblEREPFToBePaid;
//				double dblTotalInvestment = dblInvestment + dblEREPFPaid + dblEREPFToBePaid;
//				
////				if(dblTotalInvestment>=dblDeclaredInvestmentExemption){
////					dblTotalInvestment = dblDeclaredInvestmentExemption;
////				}
//				
//				double dblHRAExemptions = getHRAExemptionCalculation(con, uF, hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap);
//				
//				double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions; 
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("dblExemptions==="+dblExemptions);
//
//					System.out.println("dblHRAExemptions==="+dblHRAExemptions);
//					System.out.println("dblHomeLoanExemtion==="+dblHomeLoanExemtion);
//					System.out.println("dblTotalInvestment==="+dblTotalInvestment);
//					
//					System.out.println("dblInvestment==="+dblInvestment);
//					System.out.println("dblEEEPFPaid==="+dblEEEPFPaid);
//					System.out.println("dblEEEPFToBePaid==="+dblEEEPFToBePaid);
//					System.out.println("dblEREPFPaid==="+dblEREPFPaid);
//					System.out.println("dblEREPFToBePaid==="+dblEREPFToBePaid);
//				}
//				
////				System.out.println("dblInvestment=========+>"+dblInvestment);
////				System.out.println("dblTotalInvestment=========+>"+dblTotalInvestment);
////				System.out.println("dblHRAExemptions=========+>"+dblHRAExemptions);
////				System.out.println("dblExemptions=========+>"+dblExemptions);   
//				
//				Set set = hmSalaryDetails.keySet();
//				Iterator it = set.iterator();
//				while(it.hasNext()){
//					String strSalaryHeadId = (String)it.next();
//					String strSalaryHeadName = (String)hmSalaryDetails.get(strSalaryHeadId);
//					
//					if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//						System.out.println(strSalaryHeadId+"  " +strSalaryHeadName+"  hmFixedExemptions="+hmFixedExemptions);
//						System.out.println("hmFixedExemptions.containsKey(strSalaryHeadName)="+hmFixedExemptions.containsKey(strSalaryHeadName));
//						System.out.println("hmTotal="+hmTotal);
//						
//					}
//					
//					if(hmFixedExemptions.containsKey(strSalaryHeadName)){
//						
//						double dblIndividualExemption = uF.parseToDouble((String)hmFixedExemptions.get(strSalaryHeadName));
//						
////						System.out.println(" dblIndividualExemption=========+>"+dblIndividualExemption);
//						
//						double dblTotalToBePaid = 0;
//						if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX){
//							int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "MM"));
//							double dblCurrentMonthGross = uF.parseToDouble((String)hmTotal.get("GROSS"));
//							dblTotalToBePaid = (nMonthsLeft-1) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
//							dblTotalToBePaid += calculateProfessionalTax(con, uF, dblCurrentMonthGross, strFinancialYearEnd, nLastPayMonth, strWLocationStateId);
//						}else{
//							dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
//						}
//						
//						double dblTotalPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(strSalaryHeadId));
//						double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;  
//						double dblExmp = 0;
//						if(dblTotalPaidAmount >= dblIndividualExemption){
//							dblExemptions += dblIndividualExemption;
//							dblExmp = dblIndividualExemption;
//						}else{
//							dblExemptions += dblTotalPaidAmount;
//							dblExmp = dblTotalPaidAmount;
//						}
//						
////						System.out.println(" dblTotalPaidAmount=========+>"+dblTotalPaidAmount);
////						System.out.println(" dblIndividualExemption=========+>"+dblIndividualExemption);
////						System.out.println(" dblExemptions=========+>"+dblExemptions);
//						
//						
//						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//							System.out.println(strSalaryHeadId+" == dblExmp==="+dblExmp+" dblExemptions=="+dblExemptions);
//							System.out.println("dblTotalPaid=="+dblTotalPaid+" dblTotalToBePaid==="+dblTotalToBePaid);
//
//						}
//						
//						log.debug("dblExemption="+dblIndividualExemption);
//						log.debug("dblTotalToBePaid="+dblTotalToBePaid);
//						log.debug("dblTotalPaid="+dblTotalPaid);
//						log.debug("dblTotalPaidAmount="+dblTotalPaidAmount);
//						log.debug("dblExemptions="+dblExemptions);
//						
//						log.debug(strEmpId+"============"+strSalaryHeadName+"===========");
//						
//					}
//				}
//				
//				double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross);
//				
//				log.debug("Invest Exmp="+(String)hmEmpExemptionsMap.get(strEmpId));
////				log.debug("HRA Exemp="+getHRAExemptionCalculation(hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, hmEmpMertoMap, hmEmpRentPaidMap));
//				
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
//				double dblTotalTaxableSalary = 0;
//				if(dblTotalGrossSalary>dblExemptions){
//					dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
//				}else if(dblTotalGrossSalary>0 && dblExemptions>0 && dblTotalGrossSalary<=dblExemptions){
//					dblTotalTaxableSalary = 0;
//				}
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("dblTotalGrossSalary==="+dblTotalGrossSalary);
//					System.out.println("dblExemptions==="+dblExemptions);
//					System.out.println("dblTotalTaxableSalary==="+dblTotalTaxableSalary);
//					System.out.println("(nMonthsLeft * dblGross)==="+(nMonthsLeft * dblGross));
//				}
//				
//				int countBug = 0;
//				double dblTotalTDSPayable = 0.0d;
//				double dblUpperDeductionSlabLimit = 0;
//				double dblLowerDeductionSlabLimit = 0;
//				double dblTotalNetTaxableSalary = 0; 
//					
//				do{
//					
//					pst = con.prepareStatement(selectDeduction);
//					pst.setDouble(1, uF.parseToDouble(strAge));
//					pst.setDouble(2, uF.parseToDouble(strAge));
//					pst.setString(3, strGender);
//					pst.setDate(4, uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT));
//					pst.setDate(5, uF.getDateFormat(CF.getStrFinancialYearTo(), DATE_FORMAT));
//					pst.setDouble(6, dblTotalTaxableSalary);
//					pst.setDouble(7, dblUpperDeductionSlabLimit);
//					pst.setInt(8, slabType);
//					rs = pst.executeQuery();
//					
////					System.out.println("pst=====>"+pst);  
//					
//					if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//						System.out.println("pst=====>"+pst);
//					}
//					
//					double dblDeductionAmount = 0;
//					String strDeductionType = null;
//					
//					if(rs.next()){
//						dblDeductionAmount = rs.getDouble("deduction_amount");
//						strDeductionType = rs.getString("deduction_type");
//						dblUpperDeductionSlabLimit = rs.getDouble("_to");
//						dblLowerDeductionSlabLimit = rs.getDouble("_from");
//					}
//					
//					if(countBug==0){
//						dblTotalNetTaxableSalary = dblTotalTaxableSalary;
//					}
//					
//					if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit){
//						dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
//						
////						log.debug("dblTotalTaxableSalary 1 ="+((dblDeductionAmount /100) *  dblUpperDeductionSlabLimit ));
//						
//						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//							System.out.println("=====IF=========");
//							System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
//							System.out.println("dblUpperDeductionSlabLimit=====>"+dblUpperDeductionSlabLimit);
//							System.out.println("dblLowerDeductionSlabLimit=====>"+dblLowerDeductionSlabLimit);
//							System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
//							System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
//						}
//						
//					}else{
//						
//						if(countBug==0){
//							dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
//						}
//						
//						dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
//					
//						if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//							System.out.println("=====ELSE=========");
//							
//							System.out.println("dblTotalTDSPayable=====>"+dblTotalTDSPayable);
//							System.out.println("dblDeductionAmount=====>"+dblDeductionAmount);
//							System.out.println("dblTotalNetTaxableSalary=====>"+dblTotalNetTaxableSalary);
//						
//						}
//					}
//					
//					dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;
//					
//					
////					log.debug("dblTotalTaxableSalary="+dblTotalTaxableSalary);
////					log.debug("dblUpperDeductionSlabLimit="+dblUpperDeductionSlabLimit);
////					log.debug("dblTotalTDSPayable="+dblTotalTDSPayable);
////					log.debug("dblTotalTaxableSalary="+dblTotalTaxableSalary);
////					log.debug("dblDeductionAmount="+dblDeductionAmount);
////					log.debug("dblTotalNetTaxableSalary="+dblTotalNetTaxableSalary);
//					
////					log.debug("dblExemptions="+dblExemptions);
////					log.debug("dblTotalGrossSalary="+dblTotalGrossSalary);
////					log.debug("dblTotalTDSPayable="+dblTotalTDSPayable);
////					log.debug("dbGrossSalarySlab="+dbGrossSalarySlab);
////					log.debug("dblTDSMonth="+dblTDSMonth);
//
//					if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
//					countBug++;
//					
//				}while(dblTotalNetTaxableSalary>0);
//				
//				
//				// Service tax + Education cess
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("dblTotalTDSPayable=======>"+dblTotalTDSPayable);
//				}
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
//					
//				dblTDSMonth = dblTotalTDSPayable - dblTDSPaidAmount;
//				dblTDSMonth = dblTDSMonth/(nMonthsLeft - count);
//				
//				if(dblTDSMonth<0){
//					dblTDSMonth = 0;
//				}
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					
//					System.out.println("dblCess1=======>"+dblCess1);
//					System.out.println("dblCess2=======>"+dblCess2);
//					System.out.println("dblCess2=======>"+dblCess2);
//					System.out.println("dblTotalTDSPayable=======>"+dblTotalTDSPayable);
//					System.out.println("dblTDSPaidAmount=======>"+dblTDSPaidAmount);
//					System.out.println("nMonthsLeft=======>"+nMonthsLeft);
//				}
////				
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
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblTDSMonth;
//	}
//	
//	
//	public Map getEmpInvestmentExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, double dblDeclaredInvestmentExemption){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String,String> hmEmpExemptionsMap = new HashMap<String,String>();
//		
//		try {
//			
//			Map hmSectionLimitA = new HashMap();
//			Map hmSectionLimitP = new HashMap();
//			
//			Map hmSectionLimitEmp = new HashMap();
//			
//			pst = con.prepareStatement(selectSection);
//			rs = pst.executeQuery();
//			
//			
//			while (rs.next()) {
//				
//				if(rs.getString("section_limit_type").equalsIgnoreCase("A")){
//					hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
//				}else{
//					hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
//				}
//			}
//			
//			
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and fy_from = ? and fy_to = ? and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') group by emp_id, sd.section_id order by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
////			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
////			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			
//			rs = pst.executeQuery();
//			
//			
//			double dblInvestmentLimit = 0;
//			double dblInvestmentEmp = 0;
////			String strEmpIdNew = null;
////			String strEmpIdOld = null;
//			
//			while (rs.next()) {
////				double dblInvestment = rs.getDouble("amount_paid");
////				if(dblInvestment>=dblDeclaredInvestmentExemption){
////					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblDeclaredInvestmentExemption+"");
////				}else{
////					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestment+"");
////				}
//				
//				
////				strEmpIdNew = rs.getString("emp_id");
//				
//				/*if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
//					dblInvestmentEmp = 0;
//				}
//				*/
//				
//				String strSectionId = rs.getString("section_id");
//				double dblInvestment = rs.getDouble("amount_paid");
//				
//				
//				
//				if(hmSectionLimitA.containsKey(strSectionId)){
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
//				}else{
//					dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
//					dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
//				}
//				
//				
//				
//				if(dblInvestment>=dblInvestmentLimit){
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
//					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//				}else{
//					dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestment;
//					hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
//				}
//				
//			}
//			
//			
//			
////			System.out.println("hmEmpExemptionsMap=="+hmEmpExemptionsMap);
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return hmEmpExemptionsMap;
//	
//	}
//	
//	
//	public Map getEmpHomeLoanExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String,String> hmEmpHomeLoanMap = new HashMap<String,String>();
//		
//		try {
//			
//			pst = con.prepareStatement("select * from section_details where section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') ");
//			rs = pst.executeQuery();
//			double dblLoanExemptionLimit = 0;
//			while (rs.next()) {
//				dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
//			}
//			
//			
//			
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? and status = true and  section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') group by emp_id");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				
//				if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit){
//					hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
//				}else{
//					hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return hmEmpHomeLoanMap;
//	
//	}
//	
//	public Map getEmpRentPaid(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String,String> hmEmpRentPaidMap = new HashMap<String,String>();
//		
//		try {
//			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and fy_from = ? and fy_to = ? and agreed_date between ? and ? and section_code in ('HRA') group by emp_id ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmEmpRentPaidMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return hmEmpRentPaidMap;
//	
//	}
//	
//	
////	public Map getPaidSalary(String strFinancialYearStart, String strFinancialYearEnd){
////		
////		Connection con=null;
////		PreparedStatement pst = null;
////		ResultSet rs = null;
////		Database db = new Database();
////		Map<String,Map<String,String>> hmEmpPaidSalary = new HashMap<String,Map<String,String>>();
////		UtilityFunctions uF = new UtilityFunctions();
////		
////		try {
////			con = db.makeConnection(con);
////			pst = con.prepareStatement("select * from payroll_generation where paycycle =? and financial_year_from_date=? and financial_year_to_date =? order by emp_id");
////			pst.setInt(1, uF.parseToInt(strPC));
////			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
////			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
////			
////			rs = pst.executeQuery();
////			
////			String strEmpIdNew=null;
////			String strEmpIdOld=null;
////			
////			Map hmInner = new HashMap();
////			while (rs.next()) {
////				
////				strEmpIdNew = rs.getString("emp_id");
////			
////				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
////					hmInner = new HashMap();
////				}
////				
////				hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
////			
////				hmEmpPaidSalary.put(rs.getString("emp_id"), hmInner);
////				
////				strEmpIdOld = strEmpIdNew;
////				
////			}
////			
////		} catch (Exception e) {
////			e.printStackTrace();
////		}finally{
////			db.closeConnection(con);
////			db.closeStatements(pst);
////			db.closeResultSet(rs);
////		}
////		return hmEmpPaidSalary;
////	
////	}
//	
//	
//	
//	
//	/*public Map getHRAPaid(String strFinancialYearStart, String strFinancialYearEnd){
//		
//		Connection con=null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		Map<String, String> hmEmpPaidHRA = new HashMap<String, String>();
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select sum(amount) as hra, emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date =? group by emp_id");
//			pst.setInt(1, HRA);
//			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			
//			while (rs.next()) {
//				hmEmpPaidHRA.put(rs.getString("emp_id"), rs.getString("hra"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//		return hmEmpPaidHRA;
//	
//	}
//	
//	public Map getBasicPaid(String strFinancialYearStart, String strFinancialYearEnd){
//		
//		Connection con=null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		Map<String, String> hmEmpPaidBasic = new HashMap<String, String>();
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("select sum(amount) as basic, emp_id from payroll_generation where salary_head_id = ? and financial_year_from_date=? and financial_year_to_date =? group by emp_id");
//			pst.setInt(1, BASIC);
//			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			
//			while (rs.next()) {
//				hmEmpPaidBasic.put(rs.getString("emp_id"), rs.getString("basic"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//		}
//		return hmEmpPaidBasic;
//	
//	}*/
//	
//	
//	public Map getEmpPaidAmountDetails(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String, Map<String, String>> hmEmpPaidAmountDetails = new HashMap<String, Map<String, String>>();
//		
//		try {
//			pst = con.prepareStatement("select sum(amount) as amount, emp_id, salary_head_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ? group by emp_id, salary_head_id order by emp_id");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			
//			
//			String strEmpIdNew = null;
//			String strEmpIdOld = null;
//			Map hmInner = new HashMap();
//			while (rs.next()) {
//				strEmpIdNew = rs.getString("emp_id");
//				
//				
//				if(strEmpIdNew !=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
//					hmInner = new HashMap();
//				}
//				hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
//				
//				hmEmpPaidAmountDetails.put(rs.getString("emp_id"), hmInner);
//				
//				strEmpIdOld  = strEmpIdNew;
//				
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return hmEmpPaidAmountDetails;
//	
//	}
//	
//	
//	
//	
//	public double getHRAExemptionCalculation(Connection con, UtilityFunctions uF, Map hmPaidSalaryDetails, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, double dblHRA, double dblBasicDA, Map hmEmpMertoMap, Map hmEmpRentPaidMap){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblHRAExemption = 0;
//		
//		try {
//			
//			boolean isMetro = uF.parseToBoolean((String)hmEmpMertoMap.get(strEmpId));
//			
//			String strBasicPaidAmount = (String)hmPaidSalaryDetails.get(BASIC+"");
//			String strHRAPaidAmount = (String)hmPaidSalaryDetails.get(HRA+"");
//			
//			
//			log.debug("<===            ===>");
//			log.debug(HRA+" hmEmpPaidHRA==>"+hmPaidSalaryDetails);
//			log.debug("(String)hmEmpPaidHRA.get(strEmpId)==>"+(String)hmPaidSalaryDetails.get(strEmpId));
//			
//			
//			
//			String strMonthsLeft = uF.dateDifference(strD1, DATE_FORMAT, strFinancialYearEnd, DATE_FORMAT,CF.getStrTimeZone());
//			int nMonthsLeft = uF.parseToInt(strMonthsLeft) / 30;
//			
//			double dblBasicToBePaidAmount = nMonthsLeft * dblBasicDA;
//			double dblHRAToBePaidAmount = nMonthsLeft * dblHRA;
//			
//			
//			double dblTotalBasicDAAmount = uF.parseToDouble(strBasicPaidAmount) + dblBasicToBePaidAmount;
//			double dblTotalHRAAmount = uF.parseToDouble(strHRAPaidAmount) + dblHRAToBePaidAmount;
//			
//			double dblTotalRentPaid = uF.parseToDouble((String)hmEmpRentPaidMap.get(strEmpId));
//			
//			
//			if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//				System.out.println("dblTotalBasicDAAmount="+dblTotalBasicDAAmount);
//				System.out.println("dblTotalHRAAmount="+dblTotalHRAAmount);
//				System.out.println("dblTotalRentPaid="+dblTotalRentPaid);
//			}
//			
//			
//			
//			
//			
//			log.debug("dblTotalHRAAmount==>"+dblTotalHRAAmount);
//			log.debug("strHRAPaidAmount==>"+strHRAPaidAmount);
//			log.debug("dblHRAToBePaidAmount==>"+dblHRAToBePaidAmount);
//			
//			log.debug("dblTotalBasicDAAmount==>"+dblTotalBasicDAAmount);
//			log.debug("strBasicPaidAmount==>"+strBasicPaidAmount);
//			log.debug("dblBasicToBePaidAmount==>"+dblBasicToBePaidAmount);
//			log.debug("dblBasicDA==>"+dblBasicDA);
//			
//			
////			
////			System.out.println("dblTotalHRAAmount==>"+dblTotalHRAAmount);
////			System.out.println("strHRAPaidAmount==>"+strHRAPaidAmount);
////			System.out.println("dblHRAToBePaidAmount==>"+dblHRAToBePaidAmount);
////			
////			System.out.println("dblTotalBasicDAAmount==>"+dblTotalBasicDAAmount);
////			System.out.println("strBasicPaidAmount==>"+strBasicPaidAmount);
////			System.out.println("dblBasicToBePaidAmount==>"+dblBasicToBePaidAmount);
////			System.out.println("dblBasicDA==>"+dblBasicDA);
//			
//			
//			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from = ? and financial_year_to =? ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			
//			double dblCondition1= 0;
//			double dblCondition2= 0;
//			double dblCondition3= 0;
//			
//			while (rs.next()) {
//				dblCondition1= rs.getDouble("condition1");
//				dblCondition2= rs.getDouble("condition2");
//				dblCondition3= rs.getDouble("condition3");
//			}
//			
////			double dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
//			double dblRentPaidGreaterThanCondition1 = 0;
//			
//			log.debug("dblRentPaidGreaterThanCondition1==>"+dblRentPaidGreaterThanCondition1);
//			log.debug("dblCondition1==>"+dblCondition1);
//			log.debug("dblTotalBasicDAAmount==>"+dblTotalBasicDAAmount);
//			
//			
//			if(dblTotalRentPaid>dblRentPaidGreaterThanCondition1){
//				
//				dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("==========IF==========");
//					System.out.println("dblCondition1="+dblCondition1);
//					System.out.println("dblTotalBasicDAAmount="+dblTotalBasicDAAmount);
//				}
//				
//				
//				
//				dblRentPaidGreaterThanCondition1 = dblTotalRentPaid - dblRentPaidGreaterThanCondition1;
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("==========IF==========");
//					System.out.println("dblTotalRentPaid="+dblTotalRentPaid);
//					System.out.println("dblRentPaidGreaterThanCondition1="+dblRentPaidGreaterThanCondition1);
//				}
//				
//			}else if(dblTotalRentPaid>0){
//				dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
//				
//				if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//					System.out.println("==========ELSE==========");
//					System.out.println("dblCondition1="+dblCondition1);
//					System.out.println("dblTotalBasicDAAmount="+dblTotalBasicDAAmount);
//					System.out.println("dblRentPaidGreaterThanCondition1="+dblRentPaidGreaterThanCondition1);
//				}
//			}
//			
//			
//			double dblRentPaidCondition23 = 0;
//			
//			if(isMetro){
//				dblRentPaidCondition23 = dblCondition2 * dblTotalBasicDAAmount /100;
//			}else{
//				dblRentPaidCondition23 = dblCondition3 * dblTotalBasicDAAmount /100;
//			}
//			
//			dblHRAExemption = Math.min(dblTotalHRAAmount, dblRentPaidGreaterThanCondition1);
//			dblHRAExemption = Math.min(dblHRAExemption, dblRentPaidCondition23);
//			
//			
//			if(strEmpId.equalsIgnoreCase(strVeryEmpId)){
//				System.out.println("dblTotalHRAAmount===>"+dblTotalHRAAmount);
//				System.out.println("dblRentPaidGreaterThanCondition1===>"+dblRentPaidGreaterThanCondition1);
//				System.out.println("dblRentPaidCondition23===>"+dblRentPaidCondition23);
//				System.out.println("dblHRAExemption===>"+dblHRAExemption);
//			}
//			
////			
//			
//			log.debug("<===            ===>");
//			log.debug("dblTotalHRAAmount==>"+dblTotalHRAAmount);
//			log.debug("dblRentPaidGreaterThanCondition1==>"+dblRentPaidGreaterThanCondition1);
//			log.debug("dblRentPaidCondition23==>"+dblRentPaidCondition23);
//			log.debug("<===            ===>");
//			
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblHRAExemption;
//	
//	}
//	
//	public Map getFixedExemption(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map hmFixedExemptions = new HashMap();
//		
//		try {
//			
//			pst = con.prepareStatement("select * from exemption_details where exemption_from = ? and exemption_to =? ");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			
//			double dblCondition1= 0;
//			double dblCondition2= 0;
//			double dblCondition3= 0;
//			
//			while (rs.next()) {
//				hmFixedExemptions.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return hmFixedExemptions;
//	
//	}
//	
//	
//	public double getOverTimeCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List alServices, List alPresentDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, Map<String, Map<String, String>> hmOverTimeMap, Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays){
//		
//		double dblOverTime = 0.0d;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map hmFixedExemptions = new HashMap();
//		
//		try {
//			
//					
//			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
//			String strLevelId = hmEmpLevelMap.get(strEmpId);
//			String strOverTimeType = null;
//			
//			for(int j=0; j<alPresentDays.size(); j++){
//				String strDate = (String)alPresentDays.get(j);
//				
//				
//				log.debug("hmHolidays===>"+hmHolidays);
//				log.debug("strDate===>"+strDate);
//				log.debug("strOverTimeType===>"+strOverTimeType);
//				
//				if(hmHolidays!=null && hmHolidays.containsKey(strDate)){
//					strOverTimeType = "PH";
//				}else{
//					strOverTimeType = "EH";
//				}
//				
//				
//				
////				System.out.println("hmHolidays="+hmHolidays);
////				System.out.println("strOverTimeType="+strOverTimeType);
////				System.out.println("hmOverTimeMap="+hmOverTimeMap);
//				
//				Map hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
//				if(hmTemp==null) hmTemp=new HashMap();
//				
//				String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
//				String strPaymentType = (String)hmTemp.get("PAYMENT_TYPE");
//				double dblAmount = uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
//				
//				Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(strSalarySubHead);
//				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//				double dblSubSalaryAmount = uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
//				
//				
//				
//				Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
//				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//				double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//				
//				
//				
//				
//				
//				
//				
//				
//				for(int k=0; k<alServices.size(); k++){
//					String strService = (String)alServices.get(k);
//					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));
//
//					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")){
//						
//						/**
//						 *  IF condition is for additional hours worked during working days
//						 * 
//						 * **/
//						
//						
//						
//						
////						System.out.println("OT dblOverTime D ==>"+dblOverTime);
//						
//						if(hrsWorked > dblStandardHours){
//							dblOTHoursWorked = (hrsWorked - dblStandardHours);
//							
//							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//								dblOverTime += (dblAmount * dblSubSalaryAmount * dblOTHoursWorked)/ (dblStandardHours * 100);
//							}else if(strPaymentType!=null) {
//								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
//							}
//							
//						}
//						
//						
//						/*if(strEmpId!=null && strEmpId.equalsIgnoreCase("718")){
//							System.out.println("OT strDate==>"+strDate);
//							System.out.println("OT dblOverTime==>"+dblOverTime);
//							System.out.println("OT strPaymentType==>"+strPaymentType);
//							
//							System.out.println("OT dblAmount==>"+dblAmount);
//							System.out.println("OT dblSubSalaryAmount==>"+dblSubSalaryAmount+" ActualCTC="+dblSubSalaryAmountActualCTC);
//							System.out.println("OT dblOTHoursWorked==>"+dblOTHoursWorked);
//							System.out.println("OT dblStandardHours==>"+dblStandardHours);
//							
//							System.out.println("OT hrsWorked==>"+hrsWorked+" dblStandardHours="+dblStandardHours);
//							System.out.println("OT hmTemp==>"+hmTemp);
//						}
//						*/
//					}else{
//						
//						/**
//						 *  Else condition is for pubic holidays
//						 * 
//						 * **/
//				
//						
//						
//						
//						
//						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
//							
//							
////							System.out.println("====== A =======");
////							
////							System.out.println("dblSubSalaryAmount====="+dblSubSalaryAmount);
////							System.out.println("dblAmount====="+dblAmount);
////							System.out.println("dblOverTime====="+dblOverTime);
//							
//						}else if(strPaymentType!=null) {
//							dblOverTime += dblAmount ;
//						}
//						
//					}
//					
//					log.debug(strDate+"_"+strService+"===>"+hmHoursWorked.get(strDate+"_"+strService)); 
//					
//				}
//			}
//			
//			log.debug("dblOTHoursWorked===>"+dblOTHoursWorked);
//			
//			/*
//			if(strEmpId!=null && strEmpId.equalsIgnoreCase("718")){
//				System.out.println("dblOTHoursWorked====="+dblOTHoursWorked);
//				System.out.println("dblOverTime====="+dblOverTime);
//			}
//			*/  
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblOverTime;
//	}
//	
//	public double getOverTimeCalculationL(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List alServices, List alPresentDays, List alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, Map<String, Map<String, String>> hmOverTimeMap, Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays, int nTotalNumberOfDaysForCalc, Map<String, String> hmIndividualOvertime){
//		
//		double dblOverTime = 0.0d;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map hmFixedExemptions = new HashMap();
//		
//		try {
//			
//					
//			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
//			String strLevelId = hmEmpLevelMap.get(strEmpId);
//			String strOverTimeType = null;
//			
//			double dblTotalHoursWorked = 0;
//			
//			
//			double dblOvertimeFixedAmount = 0;
//			
//			
//			
//			
//			
//			for(int j=0; j<alPresentDays.size(); j++){
//				String strDate = (String)alPresentDays.get(j);
//				
//				if(hmHolidays!=null && hmHolidays.containsKey(strDate)){
//					strOverTimeType = "PH";
//				}else{
//					strOverTimeType = "EH";
//				}
//				
//				
//				Map hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
//				if(hmTemp==null) hmTemp=new HashMap();
//				
//				String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
//				String strPaymentType = (String)hmTemp.get("PAYMENT_TYPE");
////				double dblAmount = uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
//				double dblAmount = 0;
//				/**
//				 * Loop For all amounts
//				 * 
//				 * **/
//				
//				List<String> headIDList = null;
//				if(strSalarySubHead!=null){
//					headIDList=Arrays.asList(strSalarySubHead.split(","));
//				}
//				
//				double dblSubSalaryAmount = 0;
//				double dblSubSalaryAmountActualCTC = 0;
//				for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++){
//					dblAmount += uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
//					
//					Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(headIDList.get(i).trim());
//					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//					dblSubSalaryAmount += uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
//					
//					
//					
//					Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(headIDList.get(i).trim());
//					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//					dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//					
//					
//				}
//				
//				
//				/*Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(strSalarySubHead);
//				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//				double dblSubSalaryAmount = uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
//				
//				
//				
//				Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
//				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//				double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));*/
//				
//				
//				
//				
//				for(int k=0; k<alServices.size(); k++){
//					String strService = (String)alServices.get(k);
//					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));
//
//					dblTotalHoursWorked +=  hrsWorked;
//					
//					
//					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")){
//						
//						/**
//						 *  IF condition is for additional hours worked during working days
//						 * 
//						 * **/
//						
//						
//						if(hrsWorked > dblStandardHours && dblStandardHours>0){
//							dblOTHoursWorked = (hrsWorked - dblStandardHours);
//							
//							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
//							}else if(strPaymentType!=null) {
//								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
//							}
//							
//						}
//						
//					}else{
//						
//						/**
//						 *  Else condition is for pubic holidays
//						 * 
//						 * **/
//				
//						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
//							
//						}else if(strPaymentType!=null) {
//							dblOverTime += dblAmount ;
//							dblOvertimeFixedAmount += dblAmount ;
//						}
//						
//					}
//				}
//			}
//			
//			
//			
//			
//			
//			
//			
//			Map hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_BH");
//			if(hmTemp==null) hmTemp=new HashMap();
//			
//			/*
//			if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")){
//				System.out.println("hmOverTimeMap="+hmOverTimeMap);
//				System.out.println("LEVEL_==="+"LEVEL_"+strLevelId+"_TYPE_BH");
//				System.out.println("hmTemp="+hmTemp);
//				
//				System.out.println("alPresentWeekEndDays="+alPresentWeekEndDays);
//			}
//			*/
//			
//			for(int j=0; hmTemp.size()>0 && j<alPresentWeekEndDays.size(); j++){
//				String strDate = (String)alPresentWeekEndDays.get(j);
//				strOverTimeType = "BH";
//				
//				hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
//				if(hmTemp==null) hmTemp=new HashMap();
//				
//				String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
//				String strPaymentType = (String)hmTemp.get("PAYMENT_TYPE");
//				//double dblAmount = uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
//				
//				
//				
//				/*Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(strSalarySubHead);
//				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//				double dblSubSalaryAmount = uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
//				
//				Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
//				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//				double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));*/
//				
//				
//				
//				double dblAmount = 0;
//				/**
//				 * Loop For all amounts
//				 * 
//				 * **/
//				
//				List<String> headIDList= null;
//				if(strSalarySubHead!=null){
//					headIDList=Arrays.asList(strSalarySubHead.split(","));
//				}
//				
//				
//				double dblSubSalaryAmount = 0;
//				double dblSubSalaryAmountActualCTC = 0;
//				for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++){
//					dblAmount += uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
//					
//					Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(headIDList.get(i).trim());
//					if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//					dblSubSalaryAmount += uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
//					
//					
//					Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(headIDList.get(i).trim());
//					if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//					dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//					
//					
//				}
//				
//				
//				
//				
//
//				/*
//				if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")){
//					System.out.println("alServices="+alServices);
//					System.out.println("hmTemp="+hmTemp);
//				}
//				*/  
//				
//				for(int k=0; k<alServices.size(); k++){
//					String strService = (String)alServices.get(k);
//					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));
//
//					/*
//					if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")){
//						System.out.println("strService="+strService);
//						System.out.println("strDate="+strDate);
//						System.out.println("hmHoursWorked="+hmHoursWorked);
//						System.out.println("hrsWorked="+hrsWorked);
//					}
//					*/
//					
//					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")){
//						dblTotalHoursWorked +=  hrsWorked;
//						/**
//						 *  IF condition is for additional hours worked during working days
//						 * 
//						 * **/
//						
//						
//						if(hrsWorked > dblStandardHours && dblStandardHours>0){
//							dblOTHoursWorked = (hrsWorked - dblStandardHours);
//							
//							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
//							}else if(strPaymentType!=null) {
//								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
//							}
//							
//						}
//						
//					}else{
//						
//						/**
//						 *  Else condition is for pubic holidays
//						 * 
//						 * **/
//				
//						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//							dblTotalHoursWorked +=  hrsWorked;
//							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
//							
//						}else if(strPaymentType!=null) {
//							dblOverTime += dblAmount ;
//							dblOvertimeFixedAmount += dblAmount ;
//						}
//						
//					}
//				}
//			}
//			
//			
//			
//			double dblStdOvertimeHours = uF.parseToDouble(hmEmpLevelMap.get(strEmpId+"_SOH"));
//			if(dblStdOvertimeHours==0){
//				dblStdOvertimeHours = dblStandardHours;
//			}
//			
//			
//			
//			
//			
//			
//			
//			hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
//			if(hmTemp==null) hmTemp=new HashMap();
//			String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
////			Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
////			if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
////			double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//			
//			/**
//			 * Loop For all amounts
//			 * 
//			 * **/
//			
//			List<String> headIDList=null;
//			if(strSalarySubHead!=null){
//				headIDList=Arrays.asList(strSalarySubHead.split(","));
//			}
//			double dblSubSalaryAmountActualCTC = 0;
//			for(int i=0;headIDList!=null && !headIDList.isEmpty() && i<headIDList.size();i++){
//								
//				Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(headIDList.get(i).trim());
//				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//				dblSubSalaryAmountActualCTC  += uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//				
//				
//			}
//			
//			
//			
//
//			/*
//			if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")){
//				
//				System.out.println("dblTotalHoursWorked="+dblTotalHoursWorked);
//				System.out.println("alPresentDays.size()="+alPresentDays.size());
//				System.out.println("dblStandardHours="+dblStandardHours);
//				System.out.println("dblStdOvertimeHours="+dblStdOvertimeHours);
//				System.out.println("dblSubSalaryAmountActualCTC="+dblSubSalaryAmountActualCTC);
//				System.out.println("nTotalNumberOfDaysForCalc="+nTotalNumberOfDaysForCalc);
//				System.out.println("dblOvertimeFixedAmount="+dblOvertimeFixedAmount);
//			}*/
//			
//			double dblOverTimeHours = (dblTotalHoursWorked - ( alPresentDays.size() * dblStandardHours));
//			if(dblStdOvertimeHours>0){
//				dblOverTime = dblOverTimeHours * dblSubSalaryAmountActualCTC / (dblStdOvertimeHours * nTotalNumberOfDaysForCalc);
//				dblOverTime += dblOvertimeFixedAmount;	
//			}
//			
//			dblOverTime += uF.parseToDouble(hmIndividualOvertime.get(strEmpId)) ;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblOverTime;
//	}
//	
//	/*public double getOverTimeCalculationL(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmHoursWorked, List alServices, List alPresentDays, List alPresentWeekEndDays, Map<String, Map<String, String>> hmCalculatedSalary, Map<String, Map<String, String>> hmActualSalaryCTC, Map<String, Map<String, String>> hmOverTimeMap, Map<String, String> hmEmpLevelMap, double dblStandardHours, Map<String, String> hmHolidays, int nTotalNumberOfDaysForCalc, Map<String, String> hmIndividualOvertime){
//		
//		double dblOverTime = 0.0d;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map hmFixedExemptions = new HashMap();
//		
//		try {
//			
//					
//			double dblOTHoursWorked = 0;
//			double dblAdditionalHoursWorked = 0;
//			String strLevelId = hmEmpLevelMap.get(strEmpId);
//			String strOverTimeType = null;
//			
//			double dblTotalHoursWorked = 0;
//			
//			
//			double dblOvertimeFixedAmount = 0;
//			
//			
//			
//			
//			
//			for(int j=0; j<alPresentDays.size(); j++){
//				String strDate = (String)alPresentDays.get(j);
//				
//				if(hmHolidays!=null && hmHolidays.containsKey(strDate)){
//					strOverTimeType = "PH";
//				}else{
//					strOverTimeType = "EH";
//				}
//				
//				
//				Map hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
//				if(hmTemp==null) hmTemp=new HashMap();
//				
//				String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
//				String strPaymentType = (String)hmTemp.get("PAYMENT_TYPE");
//				double dblAmount = uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
//				
//				*//**
//				 * Loop For all amounts
//				 * 
//				 * **//*
//				
//				
//				
//				
//				
//				Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(strSalarySubHead);
//				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//				double dblSubSalaryAmount = uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
//				
//				Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
//				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//				double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//				
//				for(int k=0; k<alServices.size(); k++){
//					String strService = (String)alServices.get(k);
//					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));
//
//					dblTotalHoursWorked +=  hrsWorked;
//					
//					
//					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")){
//						
//						*//**
//						 *  IF condition is for additional hours worked during working days
//						 * 
//						 * **//*
//						
//						
//						if(hrsWorked > dblStandardHours){
//							dblOTHoursWorked = (hrsWorked - dblStandardHours);
//							
//							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
//							}else if(strPaymentType!=null) {
//								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
//							}
//							
//						}
//						
//					}else{
//						
//						*//**
//						 *  Else condition is for pubic holidays
//						 * 
//						 * **//*
//				
//						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
//							
//						}else if(strPaymentType!=null) {
//							dblOverTime += dblAmount ;
//							dblOvertimeFixedAmount += dblAmount ;
//						}
//						
//					}
//				}
//			}
//			
//			
//			
//			
//			
//			
//			
//			Map hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_BH");
//			if(hmTemp==null) hmTemp=new HashMap();
//			
//			
//			if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")){
//				System.out.println("hmOverTimeMap="+hmOverTimeMap);
//				System.out.println("LEVEL_==="+"LEVEL_"+strLevelId+"_TYPE_BH");
//				System.out.println("hmTemp="+hmTemp);
//				
//				System.out.println("alPresentWeekEndDays="+alPresentWeekEndDays);
//			}
//			
//			
//			for(int j=0; hmTemp.size()>0 && j<alPresentWeekEndDays.size(); j++){
//				String strDate = (String)alPresentWeekEndDays.get(j);
//				strOverTimeType = "BH";
//				
//				hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
//				if(hmTemp==null) hmTemp=new HashMap();
//				
//				String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
//				String strPaymentType = (String)hmTemp.get("PAYMENT_TYPE");
//				double dblAmount = uF.parseToDouble((String)hmTemp.get("PAYMENT_AMOUNT"));
//				
//				Map hmSubSalaryDetails = (Map)hmCalculatedSalary.get(strSalarySubHead);
//				if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap();
//				double dblSubSalaryAmount = uF.parseToDouble((String)hmSubSalaryDetails.get("AMOUNT"));
//				
//				Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
//				if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//				double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//
//				
//				if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")){
//					System.out.println("alServices="+alServices);
//					System.out.println("hmTemp="+hmTemp);
//				}
//				  
//				
//				for(int k=0; k<alServices.size(); k++){
//					String strService = (String)alServices.get(k);
//					double hrsWorked = uF.parseToDouble(hmHoursWorked.get(strDate+"_"+strService));
//
//					
//					if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")){
//						System.out.println("strService="+strService);
//						System.out.println("strDate="+strDate);
//						System.out.println("hmHoursWorked="+hmHoursWorked);
//						System.out.println("hrsWorked="+hrsWorked);
//					}
//					
//					
//					if(strOverTimeType!=null && strOverTimeType.equalsIgnoreCase("EH")){
//						dblTotalHoursWorked +=  hrsWorked;
//						*//**
//						 *  IF condition is for additional hours worked during working days
//						 * 
//						 * **//*
//						
//						
//						if(hrsWorked > dblStandardHours){
//							dblOTHoursWorked = (hrsWorked - dblStandardHours);
//							
//							if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//								dblOverTime += (dblAmount * dblSubSalaryAmountActualCTC * dblOTHoursWorked)/ (dblStandardHours * 100 * 9);
//							}else if(strPaymentType!=null) {
//								dblOverTime += (dblAmount * dblOTHoursWorked) / dblStandardHours;
//							}
//							
//						}
//						
//					}else{
//						
//						*//**
//						 *  Else condition is for pubic holidays
//						 * 
//						 * **//*
//				
//						if(strPaymentType!=null && strPaymentType.equalsIgnoreCase("P")){
//							dblTotalHoursWorked +=  hrsWorked;
//							dblOverTime += (dblAmount * dblSubSalaryAmount)/ 100;
//							
//						}else if(strPaymentType!=null) {
//							dblOverTime += dblAmount ;
//							dblOvertimeFixedAmount += dblAmount ;
//						}
//						
//					}
//				}
//			}
//			
//			
//			
//			double dblStdOvertimeHours = uF.parseToDouble(hmEmpLevelMap.get(strEmpId+"_SOH"));
//			if(dblStdOvertimeHours==0){
//				dblStdOvertimeHours = dblStandardHours;
//			}
//			
//			
//			
//			
//			
//			
//			
//			hmTemp = (Map)hmOverTimeMap.get("LEVEL_"+strLevelId+"_TYPE_"+strOverTimeType);
//			if(hmTemp==null) hmTemp=new HashMap();
//			String strSalarySubHead = (String)hmTemp.get("SALARY_HEAD_ID");
//			Map hmSubSalaryDetailsActualCTC = (Map)hmActualSalaryCTC.get(strSalarySubHead);
//			if(hmSubSalaryDetailsActualCTC==null)hmSubSalaryDetailsActualCTC =new HashMap();
//			double dblSubSalaryAmountActualCTC = uF.parseToDouble((String)hmSubSalaryDetailsActualCTC.get("AMOUNT"));
//			
//
//			
//			if(strEmpId!=null && strEmpId.equalsIgnoreCase("9261")){
//				
//				System.out.println("dblTotalHoursWorked="+dblTotalHoursWorked);
//				System.out.println("alPresentDays.size()="+alPresentDays.size());
//				System.out.println("dblStandardHours="+dblStandardHours);
//				System.out.println("dblStdOvertimeHours="+dblStdOvertimeHours);
//				System.out.println("dblSubSalaryAmountActualCTC="+dblSubSalaryAmountActualCTC);
//				System.out.println("nTotalNumberOfDaysForCalc="+nTotalNumberOfDaysForCalc);
//				System.out.println("dblOvertimeFixedAmount="+dblOvertimeFixedAmount);
//			}
//			
//			double dblOverTimeHours = (dblTotalHoursWorked - ( alPresentDays.size() * dblStandardHours));
//			dblOverTime = dblOverTimeHours * dblSubSalaryAmountActualCTC / (dblStdOvertimeHours * nTotalNumberOfDaysForCalc);
//			dblOverTime += dblOvertimeFixedAmount;
//			
//			
//			dblOverTime += uF.parseToDouble(hmIndividualOvertime.get(strEmpId)) ;
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblOverTime;
//	}*/
//	
//
//	
//	public double getBonusCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, Map<String, String>> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmEmpJoiningMap, CommonFunctions CF, Map<String, String> hmIndividualBonus){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblBonusCalculatedAmount = 0;
//		
//		try {
//			
//
//			String strJoiningDate = hmEmpJoiningMap.get(strEmpId);
//			String strCurrentDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
//			
////			System.out.println("strJoiningDate===>"+strJoiningDate);
////			System.out.println("strCurrentDate===>"+strCurrentDate);
//			
//			String strDays = uF.dateDifference(strJoiningDate, DATE_FORMAT, strCurrentDate, DBDATE,CF.getStrTimeZone());
//			int nDays = uF.parseToInt(strDays);
//			
//			
//			
//			Map hmTemp = (Map)hmTotal.get(BASIC+"");
//			if(hmTemp==null)hmTemp = new HashMap();
//			double dblBasic = uF.parseToDouble((String)hmTemp.get("AMOUNT"));
//			hmTemp = (Map)hmTotal.get(DA+"");
//			if(hmTemp==null)hmTemp = new HashMap();
//			double dblDA = uF.parseToDouble((String)hmTemp.get("AMOUNT"));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			
//			String strLevelId = hmEmpLevelMap.get(strEmpId);
//			
//			pst = con.prepareStatement(selectBonus1);
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(strLevelId));
//			rs = pst.executeQuery();
//			
//			
//			double dblMinimumBonus = 0.0;
//			double dblMaximumBonus = 0.0;
//			double dblBonusAmount = 0.0;
//			double dblMinimumBonusDays = 0.0;
//			String strBonusType = null;
//			String strBonusPeriod = null;
//			String strBonusSalary = null;
//			String strEffectiveFY = null;
//			String strSalaryCalculation = null;
//			
//			if(rs.next()){
//				dblMinimumBonus = rs.getDouble("bonus_minimum");
//				dblMaximumBonus = rs.getDouble("bonus_maximum");
//				dblBonusAmount = rs.getDouble("bonus_amount");
//				dblMinimumBonusDays = rs.getDouble("bonus_minimum_days");
//				strBonusType = rs.getString("bonus_type");
//				strBonusPeriod = rs.getString("bonus_period");
//				strBonusSalary = rs.getString("salary_head_id");
//				strEffectiveFY = rs.getString("salary_effective_year");
//				strSalaryCalculation = rs.getString("salary_calculation");
//			}
//			
//			
//			String []arrMonth = null;
//			boolean isBonusCalculation = false;
//			
//			if(strBonusPeriod!=null){
//				strBonusPeriod = strBonusPeriod.replaceAll("\\[", "");
//				strBonusPeriod = strBonusPeriod.replaceAll("\\]", "");
//				strBonusPeriod = strBonusPeriod.replaceAll(", ", ",");
//				arrMonth = strBonusPeriod.split(",");
//				
//				if(arrMonth!=null && ArrayUtils.contains(arrMonth, nPayMonth+"")>=0){
//					isBonusCalculation = true;
//				}
//			}
//			
//			if(strBonusSalary!=null){
//				int index = strBonusSalary.lastIndexOf(",");
//				strBonusSalary = strBonusSalary.substring(0, index);
//			}
//			
//			
//			
//			
//			
//			String []arrSalary = null;
//			if(strBonusSalary!=null){
//				arrSalary = strBonusSalary.split(",");
//			}
//			
//			double dblAmount = 0;
//			double dblSalaryAmount = 0;
//			
//			
//			if(uF.parseToInt(strSalaryCalculation)==2){ // 2 is for cumulative
//				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where salary_head_id in ("+strBonusSalary+") and emp_id = ? and financial_year_from_date=? and financial_year_to_date=? and paid_to<=? and paid_to>=?");
//				pst.setInt(1, uF.parseToInt(strEmpId));
//				
//				if(uF.parseToInt(strSalaryCalculation)==2){// 2 is for previous year
//					
//					Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
//					cal.add(Calendar.YEAR, -1);
//					String strPrevDate = uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.DATE) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
//					String []arrFinancialYear = CF.getFinancialYear(con, strPrevDate, CF, uF);
//					pst.setDate(2, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
//					pst.setDate(3, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
//					pst.setDate(4, uF.getDateFormat(strPrevDate, DATE_FORMAT));
//					pst.setDate(5, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT)); // This condittion  needs to be modified if Bonus is paid 2nd or 3rd time.
//				}else{
//					String []arrFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
//					pst.setDate(2, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT));
//					pst.setDate(3, uF.getDateFormat(arrFinancialYear[1], DATE_FORMAT));
//					pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//					pst.setDate(5, uF.getDateFormat(arrFinancialYear[0], DATE_FORMAT)); // This condittion  needs to be modified if Bonus is paid 2nd or 3rd time.
//				}
//				
//				while(rs.next()){
//					dblSalaryAmount = uF.parseToDouble(rs.getString("amount"));
//				}
//			}else{
//				
//				/**
//				 * If current salary is considered for previous months, then the condition needs to be added here.
//				 * */
//				for(int i=0; arrSalary!=null && i<arrSalary.length; i++){
//					
//					hmTemp = (Map)hmTotal.get(arrSalary[i]);
//					if(hmTemp==null)hmTemp = new HashMap();
//					dblSalaryAmount  += uF.parseToDouble((String)hmTemp.get("AMOUNT"));
//				}
//			}
//			
//			
//			
//			
//			
//			if(isBonusCalculation && nDays>=dblMinimumBonusDays){
//			
//				double dblCalculatedAmount = 0;
//				if("A".equalsIgnoreCase(strBonusType)){
//					dblAmount = dblBonusAmount;
//				}else{
//	//				dblAmount = (dblBonusAmount * (dblBasic + dblDA)) / 100;
//					dblAmount = (dblBonusAmount * dblSalaryAmount) / 100;
//					
//				}
//	//			dblCalculatedAmount = 12 * dblAmount;
//				dblCalculatedAmount = dblAmount;
//				
//				if(dblTotalGross<=dblMinimumBonus){
//					dblBonusCalculatedAmount = dblTotalGross;
//				}else if(dblMinimumBonus<=dblTotalGross  && dblTotalGross<= dblMaximumBonus){
//					dblBonusCalculatedAmount = dblMinimumBonus;
//				}
//				
//				
//				if(dblCalculatedAmount> dblMaximumBonus){
//					dblBonusCalculatedAmount = dblMaximumBonus;
//				}else{
//					dblBonusCalculatedAmount = dblCalculatedAmount;
//				}
//				
//				
//	//			if(arrMonth!=null && arrMonth.length>0){
//	//				dblBonusCalculatedAmount = dblBonusCalculatedAmount / arrMonth.length;
//	//			}
//			}
//			
//			dblBonusCalculatedAmount += uF.parseToDouble(hmIndividualBonus.get(strEmpId)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblBonusCalculatedAmount;
//	}
//	
//	public double getArearCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, Map<String, String>> hmArearAmountMap, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblMonthlyAmount = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			
//			
//			
//			Map hmArearMap = (Map)hmArearAmountMap.get(strEmpId);
//			if(hmArearMap==null)hmArearMap=new HashMap();
//			
//			
//			double dblBalanceAmount = uF.parseToDouble((String)hmArearMap.get("AMOUNT_BALANCE"));
//			dblMonthlyAmount = uF.parseToDouble((String)hmArearMap.get("MONTHLY_AREAR"));
//			
//			if((dblBalanceAmount-dblMonthlyAmount) >0 && (dblBalanceAmount-dblMonthlyAmount) < 1){
//				dblMonthlyAmount = dblBalanceAmount;
//			}
//				
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblMonthlyAmount;
//	}
//	
//	
//	public double getIncentivesCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIncentives, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblIncentiveAmount = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			dblIncentiveAmount = uF.parseToDouble(hmIncentives.get(strEmpId)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblIncentiveAmount;
//	}
//	
//	public double getIndividualOtherDeductionCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmIndividualOtherDeduction, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblIndividualOtherDeductionAmount = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			dblIndividualOtherDeductionAmount = uF.parseToDouble(hmIndividualOtherDeduction.get(strEmpId)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblIndividualOtherDeductionAmount;
//	}
//
//	public double getReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmReimbursement, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblReimbursementAmount = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			dblReimbursementAmount = uF.parseToDouble(hmReimbursement.get(strEmpId)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblReimbursementAmount;
//	}
//	
//	
//	public double getMobileReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmMobileReimbursement, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblMobileReimbursementAmount = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			dblMobileReimbursementAmount = uF.parseToDouble(hmMobileReimbursement.get(strEmpId)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblMobileReimbursementAmount;
//	}
//	
//	public double getTravelReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmTravelReimbursement, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblTravelReimbursementAmount = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			dblTravelReimbursementAmount = uF.parseToDouble(hmTravelReimbursement.get(strEmpId)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblTravelReimbursementAmount;
//	}
//
//	
//	public double getOtherReimbursementCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmOtherReimbursement, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblOtherReimbursementAmount = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			dblOtherReimbursementAmount = uF.parseToDouble(hmOtherReimbursement.get(strEmpId)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblOtherReimbursementAmount;
//	}
//
//
//	public double getMobileRecoveryCalculation(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmEmpLevelMap, Map<String, String> hmTotal, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, Map<String, String> hmMobileRecovery, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblMobileRecoveryAmount = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmTotal.get(BASIC+""));
//			double dblDA = uF.parseToDouble((String)hmTotal.get(DA+""));
//			double dblTotalGross = dblBasic + dblDA;
//			
//			dblMobileRecoveryAmount = uF.parseToDouble(hmMobileRecovery.get(strEmpId)) ;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblMobileRecoveryAmount;
//	}
//
//	public double getIncrementCalculationBasic(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmBasicSalaryMap, Map<String, String> hmDASalaryMap, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblIncrement = 0;
//		
//		try {
//			
//
//			double dblBasic = uF.parseToDouble((String)hmBasicSalaryMap.get(strEmpId));
////			double dblDA = uF.parseToDouble((String)hmDASalaryMap.get(strEmpId));
////			double dblTotalGross = dblBasic + dblDA;
//			
//			
//			pst = con.prepareStatement("select * from increment_details where increment_from <= ? and  ?<= increment_to and due_month =? ");
//			pst.setDouble(1, dblBasic);
//			pst.setDouble(2, dblBasic);
//			pst.setInt(3, nPayMonth);
//			rs = pst.executeQuery();
//			
//			
////			System.out.println("pst increment===>"+pst);
//			
//			while(rs.next()){
//				dblIncrement = rs.getDouble("increment_amount");
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblIncrement;
//	}
//	
//	
//	
//	public double getIncrementCalculationDA(Connection con, UtilityFunctions uF, String strEmpId, Map<String, String> hmBasicSalaryMap, Map<String, String> hmDASalaryMap, String strFinancialYearStart, String strFinancialYearEnd, int nPayMonth, CommonFunctions CF){
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		double dblIncrement = 0;
//		
//		try {
//			
//
//			double dblDA = uF.parseToDouble((String)hmDASalaryMap.get(strEmpId));
////			double dblTotalGross = dblBasic + dblDA;
//			
//			
//			pst = con.prepareStatement("select * from increment_details_da where increment_from <= ? and  ?<= increment_to and due_month like ? ");
//			pst.setDouble(1, dblDA);
//			pst.setDouble(2, dblDA);
//			pst.setString(3, "%"+nPayMonth+",%");
//			rs = pst.executeQuery();
//			
//			while(rs.next()){
//				
//				if("P".equalsIgnoreCase(rs.getString("increment_amount_type"))){
//					double dblIncr = rs.getDouble("increment_amount");
//					dblIncrement = dblIncr * dblDA / 100;
//				}else{
//					dblIncrement = rs.getDouble("increment_amount");
//				}
//				
//				
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}
//		return dblIncrement;
//	}
//
//	public List<FillLevel> getLevelList() {
//		return levelList;
//	}
//
//
//	public void setLevelList(List<FillLevel> levelList) {
//		this.levelList = levelList;
//	}
//
//
//	public void setPaycycleList(List<FillPayCycles> paycycleList) {
//		this.paycycleList = paycycleList;
//	}
//
//
//	public List<FillWLocation> getwLocationList() {
//		return wLocationList;
//	}
//
//
//	public void setwLocationList(List<FillWLocation> wLocationList) {
//		this.wLocationList = wLocationList;
//	}
//
//
//	public String getWLocation() {
//		return wLocation;
//	}
//
//
//	public void setWLocation(String wLocation) {
//		this.wLocation = wLocation;
//	}
//
//
//	public String getLevel() {
//		return level;
//	}
//
//
//	public void setLevel(String level) {
//		this.level = level;
//	}
//
//	
//	public void getVariableAmount(Connection con, UtilityFunctions uF, Map hmVariables, String strPC){
//		
//		PreparedStatement pst = null;
//		ResultSet rs  = null;
//		
//		try {
//			pst = con.prepareStatement("select * from otherearning_individual_details where pay_paycycle = ? and is_approved = 1");
//			pst.setInt(1, uF.parseToInt(strPC)); 
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmVariables.put(rs.getString("emp_id")+"_"+rs.getString("salary_head_id")+"_"+rs.getString("earning_deduction"), rs.getString("pay_amount"));
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	 
//	public void getBreakDetails(Connection con, UtilityFunctions uF, Map hmBreaks, Map hmBreakPolicy, String strD1, String strD2){
//		
//		PreparedStatement pst = null;
//		ResultSet rs  = null;
//		
//		try {
//			pst = con.prepareStatement("select count(*) as cnt, emp_id, break_type_id from break_application_register where _date between ? and ? and break_type_id in (select break_type_id from leave_break_type) group by emp_id, break_type_id");
//			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmBreaks.put(rs.getString("emp_id")+"_"+rs.getString("break_type_id"), rs.getString("cnt"));
//			}
//			
//			if(hmBreaks!=null && hmBreaks.size()>0){
//				pst = con.prepareStatement("select * from leave_break_type");
//				rs = pst.executeQuery();
//				while(rs.next()){
//					hmBreakPolicy.put(rs.getString("break_type_id")+"_"+rs.getString("org_id"), rs.getString("ded_amount"));
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	int halfDayCountIN=0;
//	int halfDayCountOUT=0;
//	
//	public boolean isHalfDay(String strDate, double dblEarlyLate, String strINOUT, String strLocationId, UtilityFunctions uF, Connection con){
//		boolean isHalfDay = false;
//		
//		PreparedStatement pst=null;
//		
//		try {
//			
//			if(dblEarlyLate==0)return false;
//			
//			double  dblValue = dblEarlyLate * 60;
//			int days=0;
//			
//			
//			if("IN".equalsIgnoreCase(strINOUT)){
//				pst = con.prepareStatement("select * from roster_halfday_policy where time_value < ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
//				pst.setDouble(1, uF.convertHoursIntoMinutes(dblValue));
//				pst.setString(2, strINOUT);
//				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
//				pst.setInt(4, uF.parseToInt(strLocationId));
//			}else{
//				pst = con.prepareStatement("select * from roster_halfday_policy where -time_value > ? and _mode=? and effective_date <= ? and policy_status=1 and wlocation_id=? order by time_value limit 1");
//				pst.setDouble(1, uF.convertHoursIntoMinutes(dblValue));
//				pst.setString(2, strINOUT);
//				pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
//				pst.setInt(4, uF.parseToInt(strLocationId));
//			}
//			
//			
//			ResultSet rs = pst.executeQuery();
//			while(rs.next()){
//				if("IN".equalsIgnoreCase(strINOUT)){
//					halfDayCountIN++;
//				}else{
//					halfDayCountOUT++;
//				}
//				days = rs.getInt("days");
//			}
//			
//			
////			System.out.println("pst==="+pst);
////			System.out.println("halfDayCountOUT==="+halfDayCountOUT);
////			System.out.println("halfDayCountIN==="+halfDayCountIN);
//			
//			if(days==halfDayCountIN && halfDayCountIN>0){
//				halfDayCountIN=0;
//				isHalfDay = true;
//			}
//			
//			if(days==halfDayCountOUT && halfDayCountOUT>0){
//				halfDayCountOUT=0;
//				isHalfDay = true;
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return isHalfDay;
//	}
//
//
//	public String getF_department() {
//		return f_department;
//	}
//	public void setF_department(String f_department) {
//		this.f_department = f_department;
//	}
//	public String getF_service() {
//		return f_service;
//	}
//	public void setF_service(String f_service) {
//		this.f_service = f_service;
//	}
//	public List<FillDepartment> getDepartmentList() {
//		return departmentList;
//	}
//	public void setDepartmentList(List<FillDepartment> departmentList) {
//		this.departmentList = departmentList;
//	}
//	public List<FillServices> getServiceList() {
//		return serviceList;
//	}
//	public void setServiceList(List<FillServices> serviceList) {
//		this.serviceList = serviceList;
//	}
//	public List<FillPayCycleDuration> getPaycycleDurationList() {
//		return paycycleDurationList;
//	}
//	public void setPaycycleDurationList(List<FillPayCycleDuration> paycycleDurationList) {
//		this.paycycleDurationList = paycycleDurationList;
//	}
//	public String getStrPaycycleDuration() {
//		return strPaycycleDuration;
//	}
//	public void setStrPaycycleDuration(String strPaycycleDuration) {
//		this.strPaycycleDuration = strPaycycleDuration;
//	}
//	public List<FillPayMode> getPaymentModeList() {
//		return paymentModeList;
//	}
//	public void setPaymentModeList(List<FillPayMode> paymentModeList) {
//		this.paymentModeList = paymentModeList;
//	}
//	public String getF_paymentMode() {
//		return f_paymentMode;
//	}
//	public void setF_paymentMode(String f_paymentMode) {
//		this.f_paymentMode = f_paymentMode;
//	}
//	public String[] getPaymentMode() {
//		return paymentMode;
//	}
//	public void setPaymentMode(String[] paymentMode) {
//		this.paymentMode = paymentMode;
//	}
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
//
//
//	public String getF_org() {
//		return f_org;
//	}
//
//
//	public void setF_org(String f_org) {
//		this.f_org = f_org;
//	}
//
//
//	public List<FillOrganisation> getOrganisationList() {
//		return organisationList;
//	}
//
//
//	public void setOrganisationList(List<FillOrganisation> organisationList) {
//		this.organisationList = organisationList;
//	}
//
//
//	public void calculateETDS(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, Map hmTotal, String strEmpId, String strMonth, String strPaycycle, Map<String, String> hmEmpStateMap, Map hmVariables, boolean isInsert,Map<String, String> hmOtherTaxDetails,Map<String, String> hmEmpLevelMap) {
//		
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		
//		try {
//			
//			
//			double dblEduCess = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_EDU_TAX"));
//			double dblSTDCess = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_STD_TAX"));
//			double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get((String)hmEmpStateMap.get(strEmpId)+"_FLAT_TDS"));
//			
//			double dblTDSMonth = 0;
//			double dblActual=0;
//			double dblEDuTax=0;
//			double dblSTDTax=0;
//			double dblflatTds=0;
//			
//			if(uF.parseToBoolean((String)hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))){
////				dblTDSMonth = dblGross * dblFlatTDS / 100;
//				dblActual= uF.parseToDouble((String)hmTotal.get(TDS+""));
//				dblTDSMonth=dblActual;
//				dblflatTds=dblActual;
//			}else{					
//				dblActual= uF.parseToDouble((String)hmTotal.get(TDS+""));
//								
//				dblTDSMonth=dblActual/(1+(dblEduCess/100)+(dblSTDCess/100));
//				
//				dblEDuTax = dblTDSMonth * (dblEduCess/100);
//				dblSTDTax = dblTDSMonth * (dblSTDCess/100);
////				System.out.println("dblEduCess====>"+(dblEduCess/100)+"------dblSTDCess---------"+(dblSTDCess/100));
//				/*System.out.println("dblEDuTax====>"+dblEDuTax+"------dblSTDTax---------"+dblSTDTax+"\\\\\\dblTDSMonth"+dblTDSMonth);
//				System.out.println("dblTDSMonth====>"+dblTDSMonth+"------dblActual---------"+dblActual);*/
//			}
//			
//			if(isInsert){
//				pst = con.prepareStatement("insert into emp_tds_details (financial_year_start, financial_year_end, tds_amount," +
//						" edu_tax_amount, std_tax_amount, user_id, entry_timestamp, emp_id, paycycle, _month,flat_tds_amount,actual_tds_amount) " +
//						"values (?,?,?,?,?,?,?,?,?,?,?,?)");
//				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				pst.setDouble(3, uF.parseToDouble(uF.formatIntoTwoDecimal(dblTDSMonth))); 
//				pst.setDouble(4, uF.parseToDouble(uF.formatIntoTwoDecimal(dblEDuTax)));
//				pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(dblSTDTax)));
//				pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
//				pst.setTimestamp(7, null);
//				pst.setInt(8, uF.parseToInt(strEmpId));
//				pst.setInt(9, uF.parseToInt(strPaycycle));
//				pst.setInt(10, uF.parseToInt(uF.getDateFormat(strMonth, DATE_FORMAT, "MM")));
//				pst.setDouble(11, uF.parseToDouble(uF.formatIntoTwoDecimal(dblflatTds)));
//				pst.setDouble(12, uF.parseToDouble(uF.formatIntoTwoDecimal(dblActual))); 
//				pst.execute();
//			}
//			
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);			
//		}
//		
//	}
//}
//
//
//
//  
//class ApprovePayrollRunnable1 implements Runnable, IConstants{
//	
//	ApprovePayroll1 objApprovePayroll;
//	GenerateSalarySlip gs;
//	
//	Map hmTotal;
//	Connection con;
//	UtilityFunctions uF;
//	CommonFunctions CF;
//	String strFinancialYearStart;
//	String strFinancialYearEnd;
//	String strEmpId;
//	String strOrgId;
//	String []strApprovePayCycle;
//	Map hmEmpStateMap;
//	Map hmCurrencyDetails;
//	Map hmEmpCurrency;
//	Map hmVariables;
//	HttpServletRequest request;
//	HttpServletResponse response;
//	double dblTotal;
//	Map<String, String> hmOtherTaxDetails;
//	Map<String, String> hmEmpLevelMap;
//	
//	public ApprovePayrollRunnable1(ApprovePayroll1 objApprovePayroll, GenerateSalarySlip gs, Connection con, UtilityFunctions uF, CommonFunctions CF, String strFinancialYearStart, String strFinancialYearEnd, String []strApprovePayCycle, Map hmEmpStateMap, Map hmCurrencyDetails, Map hmEmpCurrency, Map hmVariables, HttpServletRequest request, HttpServletResponse response,Map<String, String> hmOtherTaxDetails,Map<String, String> hmEmpLevelMap){
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
//	}
//	
//	public void setData(Map hmTotal, String strEmpId, double dblTotal, String strOrgId){
//		this.hmTotal = hmTotal;
//		this.strEmpId = strEmpId;
//		this.dblTotal = dblTotal;
//		this.strOrgId = strOrgId;
//	}
//	
//	public void run(){
//
//		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_EPF+"")){
//			objApprovePayroll.calculateEEPF(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, null, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true);
//			objApprovePayroll.calculateERPF(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], true);
//		}
//		
//		
//		
//		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_ESI+"")){
//			objApprovePayroll.calculateEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true);
//		}
//		
//		
//		if(hmTotal!=null && hmTotal.containsKey(EMPLOYEE_LWF+"")){
//			objApprovePayroll.calculateELWF(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true, strOrgId);
//		}
//		
//		if(hmTotal!=null && hmTotal.containsKey(TDS+"")){
//			objApprovePayroll.calculateETDS(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strEmpId, strApprovePayCycle[1], strApprovePayCycle[2], hmEmpStateMap, hmVariables, true,hmOtherTaxDetails,hmEmpLevelMap);
//		}
//		/*
//		
//		
//		try {
//			gs.setStrEmpId(strEmpId);
////			gs.setStrServiceId(((alServices.size()>0)?(String)alServices.get(0):"0"));
//			gs.setStrServiceId(0+"");
//			gs.setStrMonth(uF.getDateFormat(strApprovePayCycle[1], DATE_FORMAT, "MM"));
//			gs.setStrPC(strApprovePayCycle[2]);
//			gs.setStrFYS(strFinancialYearStart);
//			gs.setStrFYE(strFinancialYearEnd);				
//			gs.setAttachment(true);				
//			gs.setServletRequest(request);
//			gs.setServletResponse(response);
//			gs.execute();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}*/
//		
//	}
//}
