package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.payroll.reports.DepartmentwiseReport;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class InvoiceBillReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null; 
	String strUserType = null;
	       
	CommonFunctions CF = null; 
	
	private static Logger log = Logger.getLogger(DepartmentwiseReport.class);
	
	/*String f_org;
	List<FillOrganisation> orgList;
	String f_wLocation;
	List<FillWLocation> wLocationList;
	String f_department;
	List<FillDepartment> departmentList;
	String strStartDate;
	String strEndDate;
	
	String f_client;
	List<FillClients> clientList;*/
	
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_level;
	String[] f_project_service;
	String[] f_client;
	
	String selectOne;
	String strStartDate;
	String strEndDate;
	String financialYear;
	String monthFinancialYear;
	String paycycle;
	String strMonth;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillServices> projectServiceList;
	List<FillLevel> levelList;
	List<FillFinancialYears> financialYearList;
	List<FillMonth> monthList;
	List<FillPayCycles> paycycleList; 
	
	List<FillClients> clientList;
	
	String strProType;
	boolean poFlag;
	
	String strInvoiceIds;
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			session = request.getSession();
			request.setAttribute(PAGE, "/jsp/reports/InvoiceBillReport.jsp");
			request.setAttribute(TITLE, "Invoice Bill Report");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strEmpId =(String) session.getAttribute(EMPID);
			strUserType =(String) session.getAttribute(USERTYPE);
			
			/*boolean isView  = CF.getAccess(session, request, uF);
			if(!isView){
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}*/
			
//			viewInvoiceBillReport(uF);
			
			checkProjectOwner(uF);
			
			getReportAllHeads(uF);
			
			viewSalesRegisterReport(uF);
			viewMonthlyReceiptReport(uF);
			
			viewSalesReceiptReport(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return loadPaySlips(uF);

	}
	
	
	private void checkProjectOwner(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try{
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 12-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strEmpId+",%' ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
		//===end parvez date: 12-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			
			if(poFlag && uF.parseToInt(getStrProType()) == 0){
				setStrProType("2");
			}
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void viewSalesReceiptReport(UtilityFunctions uF) {

		Map<String, List<List<String>>> hmReceiptReportData = (Map<String, List<List<String>>>) request.getAttribute("hmReceiptReportData");
		if(hmReceiptReportData == null) hmReceiptReportData = new HashMap<String, List<List<String>>>();
		
		Map<String, List<List<String>>> hmSalesReportData = (Map<String, List<List<String>>>) request.getAttribute("hmSalesReportData");
		if(hmSalesReportData == null) hmSalesReportData = new HashMap<String, List<List<String>>>();
		
		List<String> receiptPartiNameList = (List<String>) request.getAttribute("receiptPartiNameList");
		List<String> pyTaxPartiNameList = (List<String>) request.getAttribute("pyTaxPartiNameList");
		List<String> taxPartiNameList = (List<String>) request.getAttribute("taxPartiNameList");
		List<String> receiptwoPartiNameList = (List<String>) request.getAttribute("receiptwoPartiNameList");
		
		Iterator<String> it = hmSalesReportData.keySet().iterator();
		
		List<List<String>> salesReceiptReportList = new ArrayList<List<String>>();
		int cnt=0;
		while (it.hasNext()) {
			cnt++;
			String invoiceId = it.next();
//			System.out.println("invoiceId ===>>>> " + invoiceId);
			List<List<String>> salesReportList = hmSalesReportData.get(invoiceId);
			List<List<String>> receiptReportList = hmReceiptReportData.get(invoiceId);
			
//			System.out.println("salesReportList ===>>>> " + salesReportList);
//			System.out.println("receiptReportList ===>>>> " + receiptReportList);

			List<String> salesInnerList = salesReportList.get(0);
			
			List<String> receiptInnerList = new ArrayList<String>();
			if(receiptReportList != null && receiptReportList.size()>0) {
				receiptInnerList = receiptReportList.get(0);
			}
			
			List<String> innerList = new ArrayList<String>();
			innerList.add("" + cnt);
			for(int i=1; salesInnerList != null && i<salesInnerList.size(); i++) {
				innerList.add(salesInnerList.get(i));
			}
//			innerList.add(salesInnerList.get(1));
//			innerList.add(salesInnerList.get(2));
//			innerList.add(salesInnerList.get(3));
//			innerList.add(salesInnerList.get(4));
//			innerList.add(salesInnerList.get(5));
//			innerList.add(salesInnerList.get(6));
//			innerList.add(salesInnerList.get(7));
//			innerList.add(salesInnerList.get(8));
//			innerList.add(salesInnerList.get(9));
//			innerList.add(salesInnerList.get(10));
//			innerList.add(salesInnerList.get(11));
//			innerList.add(salesInnerList.get(12));
			
			if(!receiptInnerList.isEmpty() && receiptInnerList.size()> 0) {
				for(int i=0; receiptInnerList != null && i<receiptInnerList.size(); i++) {
					innerList.add(receiptInnerList.get(i));
				}
//				innerList.add(receiptInnerList.get(0));
//				innerList.add(receiptInnerList.get(1));
//				innerList.add(receiptInnerList.get(2));
//				innerList.add(receiptInnerList.get(3));
//				innerList.add(receiptInnerList.get(4));
//				innerList.add(receiptInnerList.get(5));
//				innerList.add(receiptInnerList.get(6));
//				innerList.add(receiptInnerList.get(7));
//				innerList.add(receiptInnerList.get(8));
//				innerList.add(receiptInnerList.get(9));
//				innerList.add(receiptInnerList.get(10));
//				innerList.add(receiptInnerList.get(11));
//				innerList.add(receiptInnerList.get(12));
//				innerList.add(receiptInnerList.get(13));
//				innerList.add(receiptInnerList.get(14));
//				innerList.add(receiptInnerList.get(15));
//				innerList.add(receiptInnerList.get(16));
//				innerList.add(receiptInnerList.get(17));
//				innerList.add(receiptInnerList.get(18));
//				innerList.add(receiptInnerList.get(19));
//				innerList.add(receiptInnerList.get(20));
//				innerList.add(receiptInnerList.get(21));
			} else {
				innerList.add("");
				innerList.add("");
				innerList.add("");
				innerList.add("");
				for(int i=0; receiptPartiNameList != null && i<receiptPartiNameList.size(); i++) {
					innerList.add("");
				}
				for(int i=0; pyTaxPartiNameList != null && i<pyTaxPartiNameList.size(); i++) {
					innerList.add("");
				}
				for(int i=0; taxPartiNameList != null && i<taxPartiNameList.size(); i++) {
					innerList.add("");
				}
				innerList.add("");
				innerList.add("");
				innerList.add("");
				for(int i=0; receiptwoPartiNameList != null && i<receiptwoPartiNameList.size(); i++) {
					innerList.add("");
				}
				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
			}
			salesReceiptReportList.add(innerList);
			
			if(receiptReportList != null && receiptReportList.size()>1) {
				
				for(int i=1; i<receiptReportList.size(); i++) {
					List<String> innerList1 = new ArrayList<String>();
					for(int j=0; salesInnerList != null && j<salesInnerList.size(); j++) {
						innerList1.add("");
					}
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
//					innerList1.add("");
					
					List<String> receiptInnerList1 = receiptReportList.get(i);
					for(int j=0; receiptInnerList1 != null && j<receiptInnerList1.size(); j++) {
						innerList1.add(receiptInnerList1.get(j));
					}
//					innerList1.add(receiptInnerList1.get(0));
//					innerList1.add(receiptInnerList1.get(1));
//					innerList1.add(receiptInnerList1.get(2));
//					innerList1.add(receiptInnerList1.get(3));
//					innerList1.add(receiptInnerList1.get(4));
//					innerList1.add(receiptInnerList1.get(5));
//					innerList1.add(receiptInnerList1.get(6));
//					innerList1.add(receiptInnerList1.get(7));
//					innerList1.add(receiptInnerList1.get(8));
//					innerList1.add(receiptInnerList1.get(9));
//					innerList1.add(receiptInnerList1.get(10));
//					innerList1.add(receiptInnerList1.get(11));
//					innerList1.add(receiptInnerList1.get(12));
//					innerList1.add(receiptInnerList1.get(13));
//					innerList1.add(receiptInnerList1.get(14));
//					innerList1.add(receiptInnerList1.get(15));
//					innerList1.add(receiptInnerList1.get(16));
//					innerList1.add(receiptInnerList1.get(17));
//					innerList1.add(receiptInnerList1.get(18));
//					innerList1.add(receiptInnerList1.get(19));
//					innerList1.add(receiptInnerList1.get(20));
//					innerList1.add(receiptInnerList1.get(21));
				
					salesReceiptReportList.add(innerList1);
				}
			}
		}
		
//		System.out.println("salesReceiptReportList ===>>>> " + salesReceiptReportList);
		request.setAttribute("salesReceiptReportList", salesReceiptReportList);
		
	}



//	private void viewMonthlyReceiptReport(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//			con = db.makeConnection(con);
//			
//			if(getSelectOne() != null && getSelectOne().equals("1") && getStrStartDate() == null && getStrEndDate() == null) {
//				
//				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
//				
//				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
//				
//				Calendar calendar = Calendar.getInstance();
//				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
//				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
//				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
//
//				Date date = calendar.getTime();
//				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
//				String endDate=DATE_FORMAT.format(date);
//				
//				setStrStartDate(startdate);
//				setStrEndDate(endDate);
//			} else if(getSelectOne() != null && getSelectOne().equals("2")) {
//				String[] strFinancialYears = null;
//				if (getFinancialYear() != null) {
//					strFinancialYears = getFinancialYear().split("-");
//					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
//					
//					setStrStartDate(strFinancialYears[0]);
//					setStrEndDate(strFinancialYears[1]);
//				} else {
//					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
//					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
//					
//					setStrStartDate(strFinancialYears[0]);
//					setStrEndDate(strFinancialYears[1]);
//				}
//			} else if(getSelectOne() != null && getSelectOne().equals("3")) {
//				
//				int nselectedMonth = uF.parseToInt(getStrMonth());
////				int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
//				int nFYSMonth = 0;
//				String[] strFinancialYears = null;
//				if (getFinancialYear() != null) {
//					strFinancialYears = getFinancialYear().split("-");
//					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
//					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
//				} else {
//					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
//					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
//					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
//				}
//				
//				Calendar cal = GregorianCalendar.getInstance();
//				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
//				if(nselectedMonth>=nFYSMonth){
//					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
//				}else{
//					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
//				}
//				
//				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
//				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
//				
//				setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
//				setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
//				
//				
//			} else if(getSelectOne() != null && getSelectOne().equals("4")) {
//				String[] strPayCycleDates = null;
//				if (getPaycycle() != null) {
//					strPayCycleDates = getPaycycle().split("-");
//					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//					
//					setStrStartDate(strPayCycleDates[0]);
//					setStrEndDate(strPayCycleDates[1]);
//				} else {
//					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
//					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//					
//					setStrStartDate(strPayCycleDates[0]);
//					setStrEndDate(strPayCycleDates[1]);
//				}
//			}
//			
//
//			StringBuilder sbQuery=new StringBuilder();
//			sbQuery.append("select a.*,p.service,p.level_id from (select pbad.*, pid.invoice_code,pid.other_amount,pid.is_cancel from " +
//					"promntc_bill_amt_details pbad, promntc_invoice_details pid where pbad.invoice_id=pid.promntc_invoice_id) a, " +
//					"projectmntnc p where a.pro_id = p.pro_id and p.pro_id>0 ");
//			
//			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
//				sbQuery.append(" and a.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
//			}
//			 
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and wlocation_id in (select wlocation_id from work_location_info where org_id = "+uF.parseToInt(getF_org())+")");
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
////				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
//				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
//			if(getF_department() != null && getF_department().length>0) {
//				sbQuery.append(" and department_id in ("+StringUtils.join(getF_department(), ",")+") ");
//			}
//			if(getF_project_service() != null && getF_project_service().length>0) {
//				String services = uF.getConcateData(getF_project_service());
//				sbQuery.append(" and service in ("+services+") ");
//			}
//			if(getF_client() != null && getF_client().length>0) {
//				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			
//			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
//				sbQuery.append(" ) ");
//			}
//			
////			if(getStrStartDate() != null && !getStrStartDate().equals("From Date") && getStrEndDate() != null && !getStrEndDate().equals("To Date")) {
////				sbQuery.append(" and to_date(a.entry_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
////			}
//			
//			sbQuery.append(" order by a.bill_id desc ");
//			
//			
//			pst=con.prepareStatement(sbQuery.toString());
////			System.out.println("pst======>"+pst);
//			rs=pst.executeQuery();
//			List<Map<String, String>> invoiceBillList = new ArrayList<Map<String,String>>();
//			while(rs.next()) {
//				Map<String, String> hmInvoice = new HashMap<String, String>();
//				
//				hmInvoice.put("SERVICE_ID", rs.getString("service"));
//				hmInvoice.put("LEVEL_ID", rs.getString("level_id"));
//				hmInvoice.put("PROJECT_INVOICE_ID", rs.getString("invoice_id"));
//				
//				hmInvoice.put("BILL_ID", rs.getString("bill_id"));
//				hmInvoice.put("INVOICE_CODE", rs.getString("invoice_code"));
//				hmInvoice.put("INVOICE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("invoice_amount")));
//				hmInvoice.put("PARTICULARS_TOTAL_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("professional_fees")));
//				hmInvoice.put("CESS1_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("cess1")));
//				hmInvoice.put("CESS2_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("cess2")));
//				hmInvoice.put("SERVICE_TAX_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("service_tax")));
//				hmInvoice.put("OPE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("other_amount")));
//				hmInvoice.put("INVOICE_IS_CANCEL", rs.getBoolean("is_cancel") ? "Canceled" : "");
//				
//				hmInvoice.put("BILL_RECEIVED_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("received_amount")));				
//				hmInvoice.put("BILL_TDS_DEDUCTED", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("tds_deducted")));
//				hmInvoice.put("BILL_PREV_YEAR_TDS_DEDUCTED", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("prev_year_tds_deducted")));
//				hmInvoice.put("BILL_IS_TDS_DEDUCTED", rs.getString("is_tds_deducted"));
//				hmInvoice.put("BILL_PAYMENT_DESCRIPTION", rs.getString("payment_description"));
//				hmInvoice.put("BILL_PAYMENT_MODE", rs.getString("payment_mode"));
//				hmInvoice.put("BILL_INS_NO", rs.getString("ins_no"));
//				hmInvoice.put("BILL_INS_DATE", rs.getString("ins_date"));
//				hmInvoice.put("BILL_RECEIVED_BY", rs.getString("received_by"));
//				hmInvoice.put("BILL_WRITE_OFF_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("write_off_amount")));
//				hmInvoice.put("BILL_BALANCE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("balance_amount")));
//				hmInvoice.put("BILL_IS_WRITE_OFF", rs.getString("is_write_off"));
//				hmInvoice.put("BILL_CURR_ID", rs.getString("curr_id"));
//				hmInvoice.put("BILL_EXCHANGE_RATE", rs.getString("exchange_rate"));
//				hmInvoice.put("BILL_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInvoice.put("BILL_NO", rs.getString("bill_no"));
//				hmInvoice.put("BILL_PRO_ID", rs.getString("pro_id"));
//				hmInvoice.put("OTHER_DEDUCTION", rs.getString("other_deduction"));
//				hmInvoice.put("BILL_WRITE_OFF_PROF_EX", rs.getString("write_off_prof_ex"));
//				hmInvoice.put("BILL_WRITE_OFF_OP_EX", rs.getString("write_off_op_ex"));
//				hmInvoice.put("BILL_WRITE_OFF_SERVICE_TAX", rs.getString("write_off_service_tax"));
//				
//				
//				invoiceBillList.add(hmInvoice);
//			} 
//			rs.close();
//			pst.close();
//			
//			Map<String, List<List<String>>> hmReceiptReportData = new HashMap<String, List<List<String>>>();
//			List<List<String>> reportList = new ArrayList<List<String>>();
//			for(int i=0; invoiceBillList!=null && i<invoiceBillList.size();i++) {
//				Map<String, String> hmInvoice = (Map<String, String>)invoiceBillList.get(i);
//				String invoiceId = hmInvoice.get("PROJECT_INVOICE_ID");
//				reportList = hmReceiptReportData.get(invoiceId);
//				if(reportList == null) reportList = new ArrayList<List<String>>();
//				
//				List<String> innerList = new ArrayList<String>();				
//				
//				innerList.add(uF.showData(hmInvoice.get("BILL_ID"),""));
//				innerList.add(uF.showData(uF.getMonth(uF.parseToInt(uF.getDateFormat(hmInvoice.get("BILL_ENTRY_DATE"),CF.getStrReportDateFormat(), "MM"))),""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_ENTRY_DATE"),""));
////				innerList.add(uF.showData(CF.getServiceNameById(con, hmInvoice.get("SERVICE_ID")), "-"));
////				innerList.add(uF.showData(hmInvoice.get("INVOICE_CODE"),""));
//				innerList.add(uF.showData(hmInvoice.get("INVOICE_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("PARTICULARS_TOTAL_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("OPE_AMOUNT"),""));
//				
//				innerList.add(uF.showData(hmInvoice.get("SERVICE_TAX_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("CESS2_AMOUNT"),"")); 
//				innerList.add(uF.showData(hmInvoice.get("CESS1_AMOUNT"),""));
//				
//				double totTdsAmt = uF.parseToDouble(hmInvoice.get("BILL_TDS_DEDUCTED")) + uF.parseToDouble(hmInvoice.get("BILL_PREV_YEAR_TDS_DEDUCTED"));
//				innerList.add(uF.formatIntoOneDecimalWithOutComma(totTdsAmt));
//				
//				innerList.add(uF.showData(hmInvoice.get("BILL_TDS_DEDUCTED"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_PREV_YEAR_TDS_DEDUCTED"), ""));
//				
//				double tdsDefult = uF.parseToDouble(hmInvoice.get("BILL_TDS_DEDUCTED")) - uF.parseToDouble(hmInvoice.get("BILL_PREV_YEAR_TDS_DEDUCTED"));
//				innerList.add(uF.formatIntoOneDecimalWithOutComma(tdsDefult));
//				innerList.add(uF.showData(hmInvoice.get("BILL_RECEIVED_AMOUNT"), ""));
//				innerList.add("-"); //debtors
//				
//				//innerList.add(uF.showData(hmInvoice.get("OTHER_DEDUCTION"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_AMOUNT"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_PROF_EX"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_OP_EX"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_SERVICE_TAX"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_CESS_2"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_CESS_1"), ""));
//				innerList.add(uF.showData(uF.getMonth(uF.parseToInt(uF.getDateFormat(hmInvoice.get("BILL_ENTRY_DATE"),CF.getStrReportDateFormat(), "MM"))),""));
////				double totWriteOffAmt = uF.parseToDouble(hmInvoice.get("BILL_WRITE_OFF_AMOUNT")) + uF.parseToDouble(hmInvoice.get("BILL_WRITE_OFF_PROF_EX")) 
////					+ uF.parseToDouble(hmInvoice.get("BILL_WRITE_OFF_OP_EX")) + uF.parseToDouble(hmInvoice.get("BILL_WRITE_OFF_SERVICE_TAX"));
////				innerList.add(uF.formatIntoOneDecimalWithOutComma(totWriteOffAmt));
//				reportList.add(innerList);
//				hmReceiptReportData.put(invoiceId, reportList);
//			}
//			
////			System.out.println("hmReceiptReportData ===>>> " + hmReceiptReportData);
//			request.setAttribute("hmReceiptReportData", hmReceiptReportData);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//
//	}
	
	
	
	private void viewMonthlyReceiptReport(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			if(getSelectOne() != null && getSelectOne().equals("1") && (getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
				
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String endDate=DATE_FORMAT.format(date);
				
				setStrStartDate(startdate);
				setStrEndDate(endDate);
			} else if(getSelectOne() != null && getSelectOne().equals("2")) {
				String[] strFinancialYears = null;
				if (getFinancialYear() != null) {
					strFinancialYears = getFinancialYear().split("-");
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				}
			} else if(getSelectOne() != null && getSelectOne().equals("3")) {
				
				int nselectedMonth = uF.parseToInt(getStrMonth());
//				int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
				int nFYSMonth = 0;
				int nFYSDay = 0;
				String[] strFinancialYears = null;
//				System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
				if (getMonthFinancialYear() != null) {
					strFinancialYears = getMonthFinancialYear().split("-");
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				}
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				cal.set(Calendar.DATE, nFYSDay);
				if(nselectedMonth>=nFYSMonth){
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
				}else{
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
				}
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				
				setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				
			} else if(getSelectOne() != null && getSelectOne().equals("4")) {
				String[] strPayCycleDates = null;
				if (getPaycycle() != null) {
					strPayCycleDates = getPaycycle().split("-");
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				} else {
					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				}
			}
			

			StringBuilder sbQuery=new StringBuilder();
//			sbQuery.append("select a.*,p.service,p.level_id from (select pbad.*, pid.invoice_code,pid.other_amount,pid.is_cancel from " +
//					"promntc_bill_amt_details pbad, promntc_invoice_details pid where pbad.invoice_id=pid.promntc_invoice_id) a, " +
//					"projectmntnc p where a.pro_id = p.pro_id and p.pro_id>0 ");
//			
//			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
//				sbQuery.append(" and a.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
//			}
//			 
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and wlocation_id in (select wlocation_id from work_location_info where org_id = "+uF.parseToInt(getF_org())+")");
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
//				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
//			if(getF_department() != null && getF_department().length>0) {
//				sbQuery.append(" and department_id in ("+StringUtils.join(getF_department(), ",")+") ");
//			}
//			if(getF_project_service() != null && getF_project_service().length>0) {
//				String services = uF.getConcateData(getF_project_service());
//				sbQuery.append(" and service in ("+services+") ");
//			}
//			if(getF_client() != null && getF_client().length>0) {
//				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			
//			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
//				sbQuery.append(" ) ");
//			}
			
			List<Map<String, String>> invoiceBillList = new ArrayList<Map<String,String>>();
			
			if(getStrInvoiceIds() != null && getStrInvoiceIds().length()>0) {
				sbQuery.append("select pbad.*, pid.invoice_code,pid.other_amount,pid.is_cancel from promntc_bill_amt_details pbad, " +
					"promntc_invoice_details pid where pbad.invoice_id = pid.promntc_invoice_id and pid.promntc_invoice_id in ("+getStrInvoiceIds()+") ");
				if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
					sbQuery.append(" and to_date(pid.entry_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
				}
				sbQuery.append(" order by bill_id desc ");
				pst=con.prepareStatement(sbQuery.toString());
	//			System.out.println("pst======>"+pst);
				rs=pst.executeQuery();
				while(rs.next()) {
					Map<String, String> hmInvoice = new HashMap<String, String>();
					
	//				hmInvoice.put("SERVICE_ID", rs.getString("service"));
	//				hmInvoice.put("LEVEL_ID", rs.getString("level_id"));
					hmInvoice.put("PROJECT_INVOICE_ID", rs.getString("invoice_id"));
					
					hmInvoice.put("BILL_ID", rs.getString("bill_id"));
					hmInvoice.put("INVOICE_CODE", rs.getString("invoice_code"));
					hmInvoice.put("INVOICE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("invoice_amount")));
					hmInvoice.put("PARTICULARS_TOTAL_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("professional_fees")));
	//				hmInvoice.put("CESS1_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("cess1")));
	//				hmInvoice.put("CESS2_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("cess2")));
	//				hmInvoice.put("SERVICE_TAX_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("service_tax")));
					hmInvoice.put("OPE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("other_amount")));
					hmInvoice.put("INVOICE_IS_CANCEL", rs.getBoolean("is_cancel") ? "Canceled" : "");
					
					hmInvoice.put("BILL_RECEIVED_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("received_amount")));				
					hmInvoice.put("BILL_TDS_DEDUCTED", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("tds_deducted")));
					hmInvoice.put("BILL_PREV_YEAR_TDS_DEDUCTED", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("prev_year_tds_deducted")));
					hmInvoice.put("BILL_IS_TDS_DEDUCTED", rs.getString("is_tds_deducted"));
					hmInvoice.put("BILL_PAYMENT_DESCRIPTION", rs.getString("payment_description"));
					hmInvoice.put("BILL_PAYMENT_MODE", rs.getString("payment_mode"));
					hmInvoice.put("BILL_INS_NO", rs.getString("ins_no"));
					hmInvoice.put("BILL_INS_DATE", rs.getString("ins_date"));
					hmInvoice.put("BILL_RECEIVED_BY", rs.getString("received_by"));
					hmInvoice.put("BILL_WRITE_OFF_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("write_off_amount")));
					hmInvoice.put("BILL_BALANCE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("balance_amount")));
					hmInvoice.put("BILL_IS_WRITE_OFF", rs.getString("is_write_off"));
					hmInvoice.put("BILL_CURR_ID", rs.getString("curr_id"));
					hmInvoice.put("BILL_EXCHANGE_RATE", rs.getString("exchange_rate"));
					hmInvoice.put("BILL_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
					hmInvoice.put("BILL_NO", rs.getString("bill_no"));
					hmInvoice.put("BILL_PRO_ID", rs.getString("pro_id"));
					hmInvoice.put("OTHER_DEDUCTION", rs.getString("other_deduction"));
	//				hmInvoice.put("BILL_WRITE_OFF_PROF_EX", rs.getString("write_off_prof_ex"));
	//				hmInvoice.put("BILL_WRITE_OFF_OP_EX", rs.getString("write_off_op_ex"));
	//				hmInvoice.put("BILL_WRITE_OFF_SERVICE_TAX", rs.getString("write_off_service_tax"));
					
					invoiceBillList.add(hmInvoice);
				} 
				rs.close();
				pst.close();
			}
			
			
			
			Map<String, Map<String, List<String>>> hmPartiDetails = new HashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmPYTaxPartiDetails = new HashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmTaxPartiDetails = new HashMap<String, Map<String, List<String>>>();
			Map<String, Map<String, List<String>>> hmWOPartiDetails = new HashMap<String, Map<String, List<String>>>();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_PARTI+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_BILL_AMT +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmPartiDetails.get(rs.getString("promntc_invoice_bill_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bill_particulars_amount"));
				innerList.add(rs.getString("oc_bill_particulars_amount"));
				hmPartiwiseData.put(rs.getString("bill_particulars").trim(), innerList);
				hmPartiDetails.put(rs.getString("promntc_invoice_bill_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_OPE+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_BILL_AMT +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmPartiDetails.get(rs.getString("promntc_invoice_bill_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bill_particulars_amount"));
				innerList.add(rs.getString("oc_bill_particulars_amount"));
				hmPartiwiseData.put(rs.getString("bill_particulars").trim(), innerList);
				hmPartiDetails.put(rs.getString("promntc_invoice_bill_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_TAX+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_BILL_AMT +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmPartiDetails.get(rs.getString("promntc_invoice_bill_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bill_particulars_amount"));
				innerList.add(rs.getString("oc_bill_particulars_amount"));
				hmPartiwiseData.put(rs.getString("bill_particulars").trim(), innerList);
				hmPartiDetails.put(rs.getString("promntc_invoice_bill_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_TAX+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_TAX_DEDUCT_PREV_YR +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmPYTaxPartiDetails.get(rs.getString("promntc_invoice_bill_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bill_particulars_amount"));
				innerList.add(rs.getString("oc_bill_particulars_amount"));
				hmPartiwiseData.put(rs.getString("bill_particulars").trim(), innerList);
				hmPYTaxPartiDetails.put(rs.getString("promntc_invoice_bill_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_TAX+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_TAX_DEDUCT +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmTaxPartiDetails.get(rs.getString("promntc_invoice_bill_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bill_particulars_amount"));
				innerList.add(rs.getString("oc_bill_particulars_amount"));
				hmPartiwiseData.put(rs.getString("bill_particulars").trim(), innerList);
				hmTaxPartiDetails.put(rs.getString("promntc_invoice_bill_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_PARTI+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_WRITE_OFF +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmWOPartiDetails.get(rs.getString("promntc_invoice_bill_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bill_particulars_amount"));
				innerList.add(rs.getString("oc_bill_particulars_amount"));
				hmPartiwiseData.put(rs.getString("bill_particulars").trim(), innerList);
				hmWOPartiDetails.put(rs.getString("promntc_invoice_bill_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_OPE+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_WRITE_OFF +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmWOPartiDetails.get(rs.getString("promntc_invoice_bill_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bill_particulars_amount"));
				innerList.add(rs.getString("oc_bill_particulars_amount"));
				hmPartiwiseData.put(rs.getString("bill_particulars").trim(), innerList);
				hmWOPartiDetails.put(rs.getString("promntc_invoice_bill_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_TAX+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_WRITE_OFF +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmWOPartiDetails.get(rs.getString("promntc_invoice_bill_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("bill_particulars_amount"));
				innerList.add(rs.getString("oc_bill_particulars_amount"));
				hmPartiwiseData.put(rs.getString("bill_particulars").trim(), innerList);
				hmWOPartiDetails.put(rs.getString("promntc_invoice_bill_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			
			Map<String, List<List<String>>> hmReceiptReportData = new HashMap<String, List<List<String>>>();
			List<String> receiptPartiNameList = (List<String>) request.getAttribute("receiptPartiNameList");
			List<String> pyTaxPartiNameList = (List<String>) request.getAttribute("pyTaxPartiNameList");
			List<String> taxPartiNameList = (List<String>) request.getAttribute("taxPartiNameList");
			List<String> receiptwoPartiNameList = (List<String>) request.getAttribute("receiptwoPartiNameList");
			int receiptReportSize = 0;
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(int i=0; invoiceBillList!=null && i<invoiceBillList.size();i++) {
				Map<String, String> hmInvoice = (Map<String, String>)invoiceBillList.get(i);
				String invoiceId = hmInvoice.get("PROJECT_INVOICE_ID");
				
				Map<String, String> hmCurr = hmCurrencyMap.get(hmInvoice.get("BILL_CURR_ID"));
				
				reportList = hmReceiptReportData.get(invoiceId);
				if(reportList == null) reportList = new ArrayList<List<String>>();
				
				Map<String, List<String>> hmPartiData1 = hmPartiDetails.get(hmInvoice.get("BILL_ID"));
				if(hmPartiData1 == null) hmPartiData1 = new HashMap<String, List<String>>();
				
				Map<String, List<String>> hmPYTaxPartiData1 = hmPYTaxPartiDetails.get(hmInvoice.get("BILL_ID"));
				if(hmPYTaxPartiData1 == null) hmPYTaxPartiData1 = new HashMap<String, List<String>>();
				
				Map<String, List<String>> hmTaxPartiData1 = hmTaxPartiDetails.get(hmInvoice.get("BILL_ID"));
				if(hmTaxPartiData1 == null) hmTaxPartiData1 = new HashMap<String, List<String>>();
				
				Map<String, List<String>> hmWOPartiData1 = hmWOPartiDetails.get(hmInvoice.get("BILL_ID"));
				if(hmWOPartiData1 == null) hmWOPartiData1 = new HashMap<String, List<String>>();
				
				List<String> innerList = new ArrayList<String>();				
				
				innerList.add(uF.showData(hmInvoice.get("BILL_NO"),""));
				innerList.add(uF.showData(uF.getMonth(uF.parseToInt(uF.getDateFormat(hmInvoice.get("BILL_ENTRY_DATE"),CF.getStrReportDateFormat(), "MM"))),""));
				innerList.add(uF.showData(hmInvoice.get("BILL_ENTRY_DATE"),""));
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmInvoice.get("INVOICE_AMOUNT"),""));
				
				for(int j=0; receiptPartiNameList!=null && j<receiptPartiNameList.size(); j++) {
		        	List<String> innerList1 = hmPartiData1.get(receiptPartiNameList.get(j));
		        	if(innerList1 != null && !innerList1.isEmpty()) {
		        		innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(innerList1.get(1), "0"));
		        	} else {
		        		innerList.add("-");
		        	}
			    }
				for(int j=0; pyTaxPartiNameList!=null && j<pyTaxPartiNameList.size(); j++) {
		        	List<String> innerList1 = hmPYTaxPartiData1.get(pyTaxPartiNameList.get(j));
		        	if(innerList1 != null && !innerList1.isEmpty()) {
		        		innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(innerList1.get(1), "0"));
		        	} else {
		        		innerList.add("-");
		        	}
			    }
				for(int j=0; taxPartiNameList!=null && j<taxPartiNameList.size(); j++) {
		        	List<String> innerList1 = hmTaxPartiData1.get(taxPartiNameList.get(j));
		        	if(innerList1 != null && !innerList1.isEmpty()) {
		        		innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(innerList1.get(1), "0"));
		        	} else {
		        		innerList.add("-");
		        	}
			    }
				
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmInvoice.get("BILL_RECEIVED_AMOUNT"), ""));
				innerList.add("-"); //DEBTORS
				
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmInvoice.get("BILL_WRITE_OFF_AMOUNT"), ""));
				
				for(int j=0; receiptwoPartiNameList!=null && j<receiptwoPartiNameList.size(); j++) {
		        	List<String> innerList1 = hmWOPartiData1.get(receiptwoPartiNameList.get(j));
		        	if(innerList1 != null && !innerList1.isEmpty()) {
		        		innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(innerList1.get(1), "0"));
		        	} else {
		        		innerList.add("-");
		        	}
			    }
//				innerList.add(uF.showData(hmInvoice.get("PARTICULARS_TOTAL_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("OPE_AMOUNT"),""));
//				
//				innerList.add(uF.showData(hmInvoice.get("SERVICE_TAX_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("CESS2_AMOUNT"),"")); 
//				innerList.add(uF.showData(hmInvoice.get("CESS1_AMOUNT"),""));
//				
//				double totTdsAmt = uF.parseToDouble(hmInvoice.get("BILL_TDS_DEDUCTED")) + uF.parseToDouble(hmInvoice.get("BILL_PREV_YEAR_TDS_DEDUCTED"));
//				innerList.add(uF.formatIntoOneDecimalWithOutComma(totTdsAmt));
//				
//				innerList.add(uF.showData(hmInvoice.get("BILL_TDS_DEDUCTED"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_PREV_YEAR_TDS_DEDUCTED"), ""));
//				
//				double tdsDefult = uF.parseToDouble(hmInvoice.get("BILL_TDS_DEDUCTED")) - uF.parseToDouble(hmInvoice.get("BILL_PREV_YEAR_TDS_DEDUCTED"));
//				innerList.add(uF.formatIntoOneDecimalWithOutComma(tdsDefult));
//				innerList.add(uF.showData(hmInvoice.get("BILL_RECEIVED_AMOUNT"), ""));
//				innerList.add("-"); //debtors
//				
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_AMOUNT"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_PROF_EX"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_OP_EX"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_SERVICE_TAX"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_CESS_2"), ""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_CESS_1"), ""));
				innerList.add(uF.showData(uF.getMonth(uF.parseToInt(uF.getDateFormat(hmInvoice.get("BILL_ENTRY_DATE"),CF.getStrReportDateFormat(), "MM"))),""));
				reportList.add(innerList);
				receiptReportSize = innerList.size();
				
				hmReceiptReportData.put(invoiceId, reportList);
			}
			request.setAttribute("hmReceiptReportData", hmReceiptReportData);
			request.setAttribute("receiptReportSize", receiptReportSize+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	private void getReportAllHeads(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			
			List<String> partiNameList = new ArrayList<String>();
			List<String> bothPartiNameList = new ArrayList<String>();
			
			bothPartiNameList.add("Sr. No.");
			bothPartiNameList.add("Status");
			bothPartiNameList.add("Date");
			bothPartiNameList.add("Month");
			bothPartiNameList.add("Cost Center");
			bothPartiNameList.add("Customer"); //Particulars
			bothPartiNameList.add("Narration");
			bothPartiNameList.add("Bill No.");
			bothPartiNameList.add("Gross Total");
			
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where head_type='"+HEAD_PARTI+"' and invoice_particulars is not null ");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!partiNameList.contains(rs.getString("invoice_particulars").trim())) { 
					partiNameList.add(rs.getString("invoice_particulars").trim());
					bothPartiNameList.add(rs.getString("invoice_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where head_type='"+HEAD_OPE+"' and invoice_particulars is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!partiNameList.contains(rs.getString("invoice_particulars").trim())) { 
					partiNameList.add(rs.getString("invoice_particulars").trim());
					bothPartiNameList.add(rs.getString("invoice_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where head_type='"+HEAD_TAX+"' and invoice_particulars is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!partiNameList.contains(rs.getString("invoice_particulars").trim())) { 
					partiNameList.add(rs.getString("invoice_particulars").trim());
					bothPartiNameList.add(rs.getString("invoice_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			List<String> receiptPartiNameList = new ArrayList<String>();
			List<String> pyTaxPartiNameList = new ArrayList<String>();
			List<String> taxPartiNameList = new ArrayList<String>();
			List<String> receiptwoPartiNameList = new ArrayList<String>();
			bothPartiNameList.add("Receipt No.");
			bothPartiNameList.add("Month");
			bothPartiNameList.add("Date");
//			bothPartiNameList.add("Cost Center");
//			bothPartiNameList.add("Invoice No.");
			bothPartiNameList.add("Gross Total");
			
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_PARTI+"'  and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_BILL_AMT +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!receiptPartiNameList.contains(rs.getString("bill_particulars").trim()) && !rs.getString("bill_particulars").trim().equals("")) { 
					receiptPartiNameList.add(rs.getString("bill_particulars").trim());
					bothPartiNameList.add(rs.getString("bill_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_OPE+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_BILL_AMT +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!receiptPartiNameList.contains(rs.getString("bill_particulars").trim()) && !rs.getString("bill_particulars").trim().equals("")) { 
					receiptPartiNameList.add(rs.getString("bill_particulars").trim());
					bothPartiNameList.add(rs.getString("bill_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_TAX+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_BILL_AMT +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!receiptPartiNameList.contains(rs.getString("bill_particulars").trim()) && !rs.getString("bill_particulars").trim().equals("")) { 
					receiptPartiNameList.add(rs.getString("bill_particulars").trim());
					bothPartiNameList.add(rs.getString("bill_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_TAX+"' and amt_receive_type = "+ AMT_RECEIVE_TAX_DEDUCT_PREV_YR +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("bill_particulars") != null && !rs.getString("bill_particulars").trim().equals("") && !pyTaxPartiNameList.contains(rs.getString("bill_particulars").trim())) { 
					pyTaxPartiNameList.add(rs.getString("bill_particulars").trim());
					bothPartiNameList.add(rs.getString("bill_particulars").trim()+ "<br/>(Previous Year)");
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_TAX+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_TAX_DEDUCT +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!taxPartiNameList.contains(rs.getString("bill_particulars").trim()) && !rs.getString("bill_particulars").trim().equals("")) { 
					taxPartiNameList.add(rs.getString("bill_particulars").trim());
					bothPartiNameList.add(rs.getString("bill_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			bothPartiNameList.add("TOTAL NET <br/>(Cheque Amt)");
			bothPartiNameList.add("DEBTORS");
			bothPartiNameList.add("W/OFF Amount");
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_PARTI+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_WRITE_OFF +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!receiptwoPartiNameList.contains(rs.getString("bill_particulars").trim()) && !rs.getString("bill_particulars").trim().equals("")) { 
					receiptwoPartiNameList.add(rs.getString("bill_particulars").trim());
					bothPartiNameList.add(rs.getString("bill_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_OPE+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_WRITE_OFF +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!receiptwoPartiNameList.contains(rs.getString("bill_particulars").trim()) && !rs.getString("bill_particulars").trim().equals("")) { 
					receiptwoPartiNameList.add(rs.getString("bill_particulars").trim());
					bothPartiNameList.add(rs.getString("bill_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_bill_parti_amt_details where head_type='"+HEAD_TAX+"' and bill_particulars is not null and amt_receive_type = "+ AMT_RECEIVE_WRITE_OFF +"");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!receiptwoPartiNameList.contains(rs.getString("bill_particulars").trim()) && !rs.getString("bill_particulars").trim().equals("")) { 
					receiptwoPartiNameList.add(rs.getString("bill_particulars").trim());
					bothPartiNameList.add(rs.getString("bill_particulars").trim());
				}
			}
			rs.close();
			pst.close();
			
			bothPartiNameList.add("Month Of Entry");
			
			
			request.setAttribute("bothPartiNameList", bothPartiNameList);
			request.setAttribute("partiNameList", partiNameList);
			request.setAttribute("receiptPartiNameList", receiptPartiNameList);
			request.setAttribute("pyTaxPartiNameList", pyTaxPartiNameList);
			request.setAttribute("taxPartiNameList", taxPartiNameList);
			request.setAttribute("receiptwoPartiNameList", receiptwoPartiNameList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private void viewSalesRegisterReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			if(getSelectOne() != null && getSelectOne().equals("1") && (getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
				
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String endDate=DATE_FORMAT.format(date);
				
				setStrStartDate(startdate);
				setStrEndDate(endDate);
			} else if(getSelectOne() != null && getSelectOne().equals("2")) {
				String[] strFinancialYears = null;
				if (getFinancialYear() != null) {
					strFinancialYears = getFinancialYear().split("-");
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				}
			} else if(getSelectOne() != null && getSelectOne().equals("3")) {
				
				int nselectedMonth = uF.parseToInt(getStrMonth());
//				int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
				int nFYSMonth = 0;
				int nFYSDay = 0;
				String[] strFinancialYears = null;
//				System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
				if (getMonthFinancialYear() != null) {
					strFinancialYears = getMonthFinancialYear().split("-");
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				}
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				cal.set(Calendar.DATE, nFYSDay);
				if(nselectedMonth>=nFYSMonth){
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
				}else{
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
				}
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				
				setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				
			} else if(getSelectOne() != null && getSelectOne().equals("4")) {
				String[] strPayCycleDates = null;
				if (getPaycycle() != null) {
					strPayCycleDates = getPaycycle().split("-");
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				} else {
					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				}
			}
			
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmProServiceName = CF.getProjectServicesMap(con, false);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select pid.*,p.service,p.level_id,p.description from promntc_invoice_details pid, projectmntnc p where pid.pro_id = p.pro_id and pid.pro_id>0 ");
			
		//===start parvez date: 12-10-2022===	
			/*if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
				sbQuery.append(" and p.project_owner="+uF.parseToInt(strEmpId));
			}*/
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
				sbQuery.append(" and p.project_owners like '%,"+strEmpId+",%' ");
			}
		//===end parvez date: 12-10-2022===	
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and invoice_generated_by = "+ uF.parseToInt(strEmpId) +" ");
			}
			
			/*if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0)) {
				sbQuery.append(" and pid.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
			}*/
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and p.org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			/*if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and wlocation_id in (select wlocation_id from work_location_info where org_id = "+uF.parseToInt(getF_org())+")");
			}*/
			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
				sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery.append(" and p.service in ("+services+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and p.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			/*if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0)) {
				sbQuery.append(" ) ");
			}*/
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" order by pid.promntc_invoice_id desc ");
			
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> invoiceBillList = new ArrayList<Map<String,String>>();
			StringBuilder sbInvoiceIds = null;
			while(rs.next()) {
				
				if(sbInvoiceIds == null) {
					sbInvoiceIds = new StringBuilder();
					sbInvoiceIds.append(rs.getString("promntc_invoice_id"));
				} else {
					sbInvoiceIds.append(","+rs.getString("promntc_invoice_id"));
				}
				Map<String, String> hmInvoice = new HashMap<String, String>();
				
				hmInvoice.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoice.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("INVOICE_DATE_DB", rs.getString("invoice_generated_date"));
				hmInvoice.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmInvoice.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("PROJECT_ID", rs.getString("pro_id"));
				hmInvoice.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoice.put("PROJECT_DESCRIPTION", rs.getString("description"));
				hmInvoice.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmInvoice.put("SPOC_ID", rs.getString("spoc_id"));
				hmInvoice.put("ADDRESS_ID", rs.getString("address_id"));
				hmInvoice.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmInvoice.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInvoice.put("DEPART_ID", rs.getString("depart_id"));
				hmInvoice.put("SERVICE_ID", rs.getString("service"));
				hmInvoice.put("LEVEL_ID", rs.getString("level_id"));
				hmInvoice.put("INVOICE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("invoice_amount")));
				hmInvoice.put("PARTICULARS_TOTAL_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("particulars_total_amount")));
				hmInvoice.put("OPE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("other_amount")));
				hmInvoice.put("INVOICE_IS_CANCEL", rs.getBoolean("is_cancel") ? "Cancelled" : "");
				hmInvoice.put("INVOICE_CURR_ID", rs.getString("curr_id"));
				hmInvoice.put("CLIENT_ID", rs.getString("client_id"));
				
				invoiceBillList.add(hmInvoice);
			} 
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select pid.* from promntc_invoice_details pid where pid.pro_id = 0 and (invoice_type = "+ADHOC_INVOICE+" or" +
					" invoice_type = "+ADHOC_PRORETA_INVOICE+" )");
			if((strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) || (isPoFlag() && uF.parseToInt(getStrProType()) == 2)) {
				sbQuery.append(" and invoice_generated_by = "+ uF.parseToInt(strEmpId) +" ");
			}
			if(uF.parseToInt(getF_org())>0 && (getF_strWLocation()==null || getF_strWLocation().length == 0)) {
				//System.out.println("Org id====>"+uF.parseToInt(getF_org()));
				//String loctionIds = CF.getOrgLocationIds(con, uF, getF_org());
				//System.out.println("loctionIds=====>"+loctionIds);
				/*if(loctionIds != null && loctionIds.trim().length()>0) {
					sbQuery.append(" and wlocation_id in ("+loctionIds+")");
				}*/
				sbQuery.append(" and wlocation_id in (select wlocation_id from work_location_info where org_id ="+getF_org()+")");
				
			} else if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			/*if(getF_project_service() != null && getF_project_service().length>0) {
				sbQuery.append(" and service_id in ("+StringUtils.join(getF_project_service(), ",")+") ");
			}*/
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" order by pid.promntc_invoice_id desc ");
			pst=con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getF_org()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbInvoiceIds == null) {
					sbInvoiceIds = new StringBuilder();
					sbInvoiceIds.append(rs.getString("promntc_invoice_id"));
				} else {
					sbInvoiceIds.append(","+rs.getString("promntc_invoice_id"));
				}
				Map<String, String> hmInvoice = new HashMap<String, String>();
				
				hmInvoice.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
				hmInvoice.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("INVOICE_DATE_DB", rs.getString("invoice_generated_date"));
				hmInvoice.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
				hmInvoice.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("PROJECT_ID", rs.getString("pro_id"));
				hmInvoice.put("INVOICE_CODE", rs.getString("invoice_code"));
				hmInvoice.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
				hmInvoice.put("OTHER_DESCRIPTION", rs.getString("other_description"));
				hmInvoice.put("SPOC_ID", rs.getString("spoc_id"));
				hmInvoice.put("ADDRESS_ID", rs.getString("address_id"));
				hmInvoice.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
				hmInvoice.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInvoice.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInvoice.put("DEPART_ID", rs.getString("depart_id"));
				hmInvoice.put("SERVICE_ID", rs.getString("service_id"));
//				hmInvoice.put("LEVEL_ID", );
				hmInvoice.put("INVOICE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("invoice_amount")));
				hmInvoice.put("PARTICULARS_TOTAL_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("particulars_total_amount")));
				hmInvoice.put("OPE_AMOUNT", uF.formatIntoOneDecimalWithOutComma(rs.getDouble("other_amount")));
				hmInvoice.put("INVOICE_IS_CANCEL", rs.getBoolean("is_cancel") ? "Cancelled" : "");
				hmInvoice.put("INVOICE_CURR_ID", rs.getString("curr_id"));
				hmInvoice.put("CLIENT_ID", rs.getString("client_id"));
				
				invoiceBillList.add(hmInvoice);
			} 
			rs.close();
			pst.close();
			if(sbInvoiceIds == null) {
				sbInvoiceIds = new StringBuilder();
			}
			
			setStrInvoiceIds(sbInvoiceIds.toString());
			
//			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd where pmt.client_id=cd.client_id ");
//			rs = pst.executeQuery();
//			Map<String, Map<String,String>> hmProjectDetails = new HashMap<String, Map<String,String>>();
//			while (rs.next()) {
//				Map<String,String> hmProject = new HashMap<String, String>();
//				hmProject.put("CLIENT_ID", rs.getString("client_id"));
//				hmProject.put("CLIENT_NAME", rs.getString("client_name"));
//				hmProject.put("CLIENT_INDUSTRY", rs.getString("client_industry"));
//				hmProject.put("CLIENT_ADDRESS", rs.getString("client_address"));				
//				
//				hmProject.put("PRO_ID", rs.getString("pro_id"));
//				hmProject.put("PRO_NAME", rs.getString("pro_name"));
//				hmProject.put("PRIORITY", rs.getString("priority"));
//				hmProject.put("DESCRIPTION", rs.getString("description"));
//				hmProject.put("ACTIVITY", rs.getString("activity"));
//				hmProject.put("SERVICE", rs.getString("service"));
//				hmProject.put("WLOCATION_ID", rs.getString("wlocation_id"));
//				hmProject.put("DEPARTMENT_ID", rs.getString("department_id"));
//				hmProject.put("CURRENCY_ID", rs.getString("curr_id"));
//				
//				hmProjectDetails.put(rs.getString("pro_id"), hmProject);
//				
//			}
//			rs.close();
//			pst.close();
			
			
			
			Map<String, Map<String, List<String>>> hmPartiDetails = new HashMap<String, Map<String, List<String>>>();
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where head_type='"+HEAD_PARTI+"' and invoice_particulars is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmPartiDetails.get(rs.getString("promntc_invoice_id"));
//				List<List<String>> outerList = hmPartiDetails.get(rs.getString("promntc_invoice_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				hmPartiwiseData.put(rs.getString("invoice_particulars").trim(), innerList);
				hmPartiDetails.put(rs.getString("promntc_invoice_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where head_type='"+HEAD_OPE+"' and invoice_particulars is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmPartiDetails.get(rs.getString("promntc_invoice_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				hmPartiwiseData.put(rs.getString("invoice_particulars").trim(), innerList);
				hmPartiDetails.put(rs.getString("promntc_invoice_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where head_type='"+HEAD_TAX+"' and invoice_particulars is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, List<String>> hmPartiwiseData = hmPartiDetails.get(rs.getString("promntc_invoice_id"));
				if(hmPartiwiseData == null) hmPartiwiseData = new HashMap<String, List<String>>();
				
        		List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("invoice_particulars_amount"));
				innerList.add(rs.getString("oc_invoice_particulars_amount"));
				hmPartiwiseData.put(rs.getString("invoice_particulars").trim(), innerList);
				hmPartiDetails.put(rs.getString("promntc_invoice_id"), hmPartiwiseData);
			}
			rs.close();
			pst.close();
			
			
			List<String> partiNameList = (List<String>)request.getAttribute("partiNameList");
			
			Map<String, List<List<String>>> hmSalesReportData = new HashMap<String, List<List<String>>>();
			List<List<String>> reportList = new ArrayList<List<String>>();
			int salesReportSize = 0;
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			for(int i=0;invoiceBillList != null && i<invoiceBillList.size();i++) {
				Map<String, String> hmInvoice = (Map<String, String>)invoiceBillList.get(i);
				String invoiceId = hmInvoice.get("PROJECT_INVOICE_ID");
				
				Map<String, String> hmCurr = hmCurrencyMap.get(hmInvoice.get("INVOICE_CURR_ID"));
				
				reportList = hmSalesReportData.get(invoiceId);
				if(reportList == null) reportList = new ArrayList<List<String>>();
				
				Map<String, List<String>> hmPartiData1 = hmPartiDetails.get(hmInvoice.get("PROJECT_INVOICE_ID"));
				
//				Map<String,String> hmProject = hmProjectDetails.get(hmInvoice.get("PROJECT_ID"));
//				if(hmProject == null) hmProject = new HashMap<String, String>();
				
				List<String> innerList = new ArrayList<String>();				
				innerList.add(""+(i+1));
				innerList.add(uF.showData(hmInvoice.get("INVOICE_IS_CANCEL"),""));
				innerList.add(uF.showData(hmInvoice.get("INVOICE_DATE"),""));
				if(hmInvoice.get("INVOICE_DATE") != null && !hmInvoice.get("INVOICE_DATE").equals("-")) {
					innerList.add(uF.showData(uF.getMonth(uF.parseToInt(uF.getDateFormat(hmInvoice.get("INVOICE_DATE"),CF.getStrReportDateFormat(), "MM"))),""));
				} else {
					innerList.add("-");
				}
				innerList.add(uF.showData(hmProServiceName.get(hmInvoice.get("SERVICE_ID")), "-"));
				innerList.add(uF.showData(hmProjectName.get(hmInvoice.get("PROJECT_ID")),""));
				innerList.add(uF.showData(hmInvoice.get("PROJECT_DESCRIPTION"),""));
				innerList.add(uF.showData(hmInvoice.get("INVOICE_CODE"),""));
				innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmInvoice.get("INVOICE_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("PARTICULARS_TOTAL_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("OPE_AMOUNT"),""));
				for(int j=0; partiNameList!=null && j<partiNameList.size(); j++) {
		        	List<String> innerList1 = hmPartiData1.get(partiNameList.get(j));
		        	if(innerList1 != null && !innerList1.isEmpty()) {
		        		innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(innerList1.get(1), "0"));
		        	} else {
		        		innerList.add("-");
		        	}
			    }
				
				reportList.add(innerList);
				salesReportSize = innerList.size(); 
				hmSalesReportData.put(invoiceId, reportList);
			}
//			System.out.println("hmSalesReportData ===>> " + hmSalesReportData);
			
			request.setAttribute("hmSalesReportData", hmSalesReportData);
			request.setAttribute("salesReportSize", salesReportSize+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
	
//	private void viewInvoiceBillReport(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		try {
//			con = db.makeConnection(con);
//			
//			if(getStrStartDate()==null && getStrEndDate()==null){
//				
//				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
//				
//				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
//				
//				Calendar calendar = Calendar.getInstance();
//				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
//				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
//				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
//
//				Date date = calendar.getTime();
//				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
//				String endDate=DATE_FORMAT.format(date);
//				
//				setStrStartDate(startdate);
//				setStrEndDate(endDate);
//			}
//			
//			
//			StringBuilder sbQuery=new StringBuilder();
//			sbQuery.append("select * from promntc_bill_amt_details pbad,promntc_invoice_details pid where pbad.invoice_id=pid.promntc_invoice_id and pid.pro_id>0 ");
//			
////			if(uF.parseToInt(getF_org())>0 || uF.parseToInt(getF_wLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_client())>0){
////				sbQuery.append(" and pid.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
////			}
////			 
////			if(uF.parseToInt(getF_org())>0){
////				sbQuery.append(" and wlocation_id in (select wlocation_id from work_location_info where org_id="+uF.parseToInt(getF_org())+")");
////			}
////			if(uF.parseToInt(getF_wLocation())>0){
////				sbQuery.append(" and wlocation_id="+uF.parseToInt(getF_wLocation()));
////			}
////			if(uF.parseToInt(getF_department())>0){
////				sbQuery.append(" and department_id="+uF.parseToInt(getF_department()));
////			}
////			if(uF.parseToInt(getF_client())>0){
////				sbQuery.append(" and client_id="+uF.parseToInt(getF_client()));
////			}
////			
////			if(uF.parseToInt(getF_org())>0 || uF.parseToInt(getF_wLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_client())>0){
////				sbQuery.append(" ) ");
////			}
//			
//			if(getStrStartDate() != null && !getStrStartDate().equals("From Date") && getStrEndDate() != null && !getStrEndDate().equals("To Date")) {
//				sbQuery.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
//			}
//			
//			sbQuery.append(" order by pid.promntc_invoice_id desc ");
//			
//			
//			pst=con.prepareStatement(sbQuery.toString());
////			System.out.println("pst======>"+pst);
//			rs=pst.executeQuery();
//			List<Map<String, String>> invoiceBillList=new ArrayList<Map<String,String>>();
//			while(rs.next()){
//				Map<String, String> hmInvoice=new HashMap<String, String>();
//				
//				hmInvoice.put("PROJECT_INVOICE_ID", rs.getString("promntc_invoice_id"));
//				hmInvoice.put("INVOICE_DATE", uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInvoice.put("INVOICE_GENERATED_BY", rs.getString("invoice_generated_by"));
//				hmInvoice.put("INVOICE_FROM_DATE", uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInvoice.put("INVOICE_TO_DATE", uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInvoice.put("PROJECT_ID", rs.getString("pro_id"));
//				hmInvoice.put("INVOICE_CODE", rs.getString("invoice_code"));
//				hmInvoice.put("PROJECT_DESCRIPTION", rs.getString("project_description"));
//				hmInvoice.put("OTHER_DESCRIPTION", rs.getString("other_description"));
//				hmInvoice.put("SPOC_ID", rs.getString("spoc_id"));
//				hmInvoice.put("ADDRESS_ID", rs.getString("address_id"));
//				hmInvoice.put("PRO_OWNER_ID", rs.getString("pro_owner_id"));
//				hmInvoice.put("FINANCIAL_START_DATE", uF.getDateFormat(rs.getString("financial_start_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInvoice.put("FINANCIAL_END_DATE", uF.getDateFormat(rs.getString("financial_end_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInvoice.put("WLOCATION_ID", rs.getString("wlocation_id"));
//				hmInvoice.put("DEPART_ID", rs.getString("depart_id"));
//				hmInvoice.put("INVOICE_AMOUNT", rs.getString("invoice_amount"));
//				hmInvoice.put("PARTICULARS_TOTAL_AMOUNT", rs.getString("particulars_total_amount"));
//				hmInvoice.put("OPE_AMOUNT", rs.getString("other_amount"));
//				hmInvoice.put("INVOICE_IS_CANCEL", rs.getString("is_cancel"));
//				
//				hmInvoice.put("BILL_ID", rs.getString("bill_id"));
//				hmInvoice.put("BILL_RECEIVED_AMOUNT", rs.getString("received_amount"));				
//				hmInvoice.put("BILL_TDS_DEDUCTED", rs.getString("tds_deducted"));
//				hmInvoice.put("BILL_IS_TDS_DEDUCTED", rs.getString("is_tds_deducted"));
//				hmInvoice.put("BILL_PAYMENT_DESCRIPTION", rs.getString("payment_description"));
//				hmInvoice.put("BILL_PAYMENT_MODE", rs.getString("payment_mode"));
//				hmInvoice.put("BILL_INS_NO", rs.getString("ins_no"));
//				hmInvoice.put("BILL_INS_DATE", rs.getString("ins_date"));
//				hmInvoice.put("BILL_RECEIVED_BY", rs.getString("received_by"));
//				hmInvoice.put("BILL_WRITE_OFF_AMOUNT", rs.getString("write_off_amount"));
//				hmInvoice.put("BILL_BALANCE_AMOUNT", rs.getString("balance_amount"));
//				hmInvoice.put("BILL_IS_WRITE_OFF", rs.getString("is_write_off"));
//				hmInvoice.put("BILL_CURR_ID", rs.getString("curr_id"));
//				hmInvoice.put("BILL_EXCHANGE_RATE", rs.getString("exchange_rate"));
//				hmInvoice.put("BILL_ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
//				hmInvoice.put("BILL_NO", rs.getString("bill_no"));
//				hmInvoice.put("BILL_PRO_ID", rs.getString("pro_id"));
//				hmInvoice.put("BILL_SERVICE_TAX", rs.getString("service_tax"));
//				hmInvoice.put("BILL_CESS1", rs.getString("cess1"));
//				hmInvoice.put("BILL_CESS2", rs.getString("cess2"));
//				 
//				
//				invoiceBillList.add(hmInvoice);
//			}
//			rs.close();
//			pst.close(); 
//			
//			
//			pst = con.prepareStatement("select * from projectmntnc pmt,client_details cd where pmt.client_id=cd.client_id ");
//			rs = pst.executeQuery();
//			Map<String, Map<String,String>> hmProjectDetails=new HashMap<String, Map<String,String>>();
//			while (rs.next()) {
//				Map<String,String> hmProject=new HashMap<String, String>();
//				hmProject.put("CLIENT_ID", rs.getString("client_id"));
//				hmProject.put("CLIENT_NAME", rs.getString("client_name"));
//				hmProject.put("CLIENT_INDUSTRY", rs.getString("client_industry"));
//				hmProject.put("CLIENT_ADDRESS", rs.getString("client_address"));				
//				
//				hmProject.put("PRO_ID", rs.getString("pro_id"));
//				hmProject.put("PRO_NAME", rs.getString("pro_name"));
//				hmProject.put("PRIORITY", rs.getString("priority"));
//				hmProject.put("DESCRIPTION", rs.getString("description"));
//				hmProject.put("ACTIVITY", rs.getString("activity"));
//				hmProject.put("SERVICE", rs.getString("service"));
//				hmProject.put("WLOCATION_ID", rs.getString("wlocation_id"));
//				hmProject.put("DEPARTMENT_ID", rs.getString("department_id"));
//				hmProject.put("CURRENCY_ID", rs.getString("curr_id"));
//				
//				hmProjectDetails.put(rs.getString("pro_id"), hmProject);
//				
//			}
//			rs.close();
//			pst.close();
//			
////			pst = con.prepareStatement(" select * from promntc_invoice_amt_details piad, promntc_bill_amt_details pbad where piad.promntc_invoice_id=pbad.invoice_id");
//			pst = con.prepareStatement(" select * from promntc_invoice_amt_details");
//			rs = pst.executeQuery();
//			Map<String, List<List<String>>> hmOuterAmt=new HashMap<String,List<List<String>>>();
//			while (rs.next()) {
//				if(rs.getString("invoice_particulars") != null && (rs.getString("invoice_particulars").trim().equals("STAX") || rs.getString("invoice_particulars").trim().equals("EDUCESS") || rs.getString("invoice_particulars").trim().equals("SHSCESS"))) {
//					List<List<String>> outerList=hmOuterAmt.get(rs.getString("promntc_invoice_id"));
//					if(outerList==null) outerList=new ArrayList<List<String>>();
//					
//	        		List<String> innerList=new ArrayList<String>();
//					innerList.add(rs.getString("promntc_invoice_amt_id"));
//					innerList.add(rs.getString("invoice_particulars"));
//					innerList.add(rs.getString("invoice_particulars_amount"));
//					innerList.add(rs.getString("promntc_invoice_id"));
//					
//					outerList.add(innerList);
//					
//					hmOuterAmt.put(rs.getString("promntc_invoice_id"),outerList);
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			
//			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
//			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
//			getExcelFirstHeaderList(alInnerExport);
//			reportListExport.add(alInnerExport);
//			
//			
//			List<List<String>> reportList=new ArrayList<List<String>>();			
//			for(int i=0;invoiceBillList!=null && i<invoiceBillList.size();i++){
//				Map<String, String> hmInvoice=(Map<String, String>)invoiceBillList.get(i);
//				
//				Map<String,String> hmProject=hmProjectDetails.get(hmInvoice.get("PROJECT_ID"));
//				if(hmProject == null) hmProject = new HashMap<String, String>();
//				
//				List<List<String>> outerAmtList=hmOuterAmt.get(hmInvoice.get("PROJECT_INVOICE_ID"));
//				 String sTax="0";
//			     String eduCess="0";
//			     String shsCess="0";
//			     
//			    
//		        for(int j=0;outerAmtList!=null && j<outerAmtList.size();j++){
//		        	List<String> innerList=outerAmtList.get(j);
//		        	if(innerList.get(1)!=null && innerList.get(1).trim().equalsIgnoreCase("STAX")){
//		        		 sTax=uF.showData(innerList.get(2), "0");
//		        	}else if(innerList.get(1)!=null && innerList.get(1).trim().equalsIgnoreCase("EDUCESS")){
//		     	        eduCess=uF.showData(innerList.get(2), "0");
//		        	}else if(innerList.get(1)!=null && innerList.get(1).trim().equalsIgnoreCase("SHSCESS")){
//		     	        shsCess=uF.showData(innerList.get(2), "0");
//		        	}
//		        }
//				
//				List<String> innerList=new ArrayList<String>();				
//				innerList.add(""+(i+1));
//				innerList.add(uF.showData(hmInvoice.get("INVOICE_DATE"),""));
//				innerList.add(uF.showData(uF.getDateFormat(hmInvoice.get("INVOICE_DATE"),CF.getStrReportDateFormat(), "MM"),""));
//				innerList.add("");
//				innerList.add(uF.showData(hmProject.get("PRO_NAME"),""));
//				innerList.add(uF.showData(hmProject.get("DESCRIPTION"),""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_NO"),""));
//				innerList.add(uF.showData(hmInvoice.get("INVOICE_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("PARTICULARS_TOTAL_AMOUNT"),""));
//				innerList.add(uF.showData(hmInvoice.get("OPE_AMOUNT"),""));
//				innerList.add(uF.showData(sTax,""));
//				innerList.add(uF.showData(eduCess,"")); 
//				innerList.add(uF.showData(shsCess,"")); 
//				innerList.add(uF.showData(hmInvoice.get("BILL_INS_NO"),""));
//				innerList.add(uF.showData(uF.getDateFormat(hmInvoice.get("BILL_ENTRY_DATE"),CF.getStrReportDateFormat(), "MM"),""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_ENTRY_DATE"),""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_RECEIVED_AMOUNT"),""));
//				innerList.add("");
//				innerList.add("");
//				innerList.add(uF.showData(hmInvoice.get("BILL_SERVICE_TAX"),""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_CESS1"),""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_CESS2"),""));
//				innerList.add(uF.showData(hmInvoice.get("BILL_TDS_DEDUCTED"), ""));
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				
//				innerList.add("");
//				innerList.add(uF.showData(hmInvoice.get("BILL_WRITE_OFF_AMOUNT"), ""));
//				/*innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");
//				innerList.add("");*/
//								
//				reportList.add(innerList);
//				
//				
//				alInnerExport=new ArrayList<DataStyle>();
//				alInnerExport.add(new DataStyle(uF.showData(""+(i+1), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("INVOICE_DATE"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(uF.getDateFormat(hmInvoice.get("INVOICE_DATE"),CF.getStrReportDateFormat(), "MM"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmProject.get("PRO_NAME"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmProject.get("DESCRIPTION"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("INVOICE_CODE"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("INVOICE_AMOUNT"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("PARTICULARS_TOTAL_AMOUNT"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("OPE_AMOUNT"),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(sTax, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(eduCess, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
//				alInnerExport.add(new DataStyle(uF.showData(shsCess, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("BILL_INS_NO"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(uF.getDateFormat(hmInvoice.get("BILL_ENTRY_DATE"),CF.getStrReportDateFormat(), "MM"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("BILL_ENTRY_DATE"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("BILL_RECEIVED_AMOUNT"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("BILL_SERVICE_TAX"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("BILL_CESS1"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("BILL_CESS2"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("BILL_TDS_DEDUCTED"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData(hmInvoice.get("BILL_WRITE_OFF_AMOUNT"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				/*alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				alInnerExport.add(new DataStyle(uF.showData("", ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));*/
//				
//				reportListExport.add(alInnerExport);
//			}
//			
//			request.setAttribute("reportList", reportList);
//			request.setAttribute("reportListExport",reportListExport);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}

// }


	private void getExcelFirstHeaderList(List<DataStyle> alInnerExport) {
		
		alInnerExport.add(new DataStyle("Invoice Bill Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Month",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Cost Center",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Particulars",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Narration",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Bill No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Gross Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Professional Fees",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("OPE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Service Tax Charged",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Cess 2% on S. Tax",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Cess 1% on S. Tax",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Receipt No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Month",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Gross",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Professional Fees",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("OPE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Service Tax Charged",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Cess 2% on S. Tax",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Cess 1% on S. Tax",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Original TDS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("TDS Financial Year",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("TDS Last Financial Year",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("TDS DEFULTs",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Other Deductions",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("TOTAL NET(Cheque Amt)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	
	   	alInnerExport.add(new DataStyle("DEBTORS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Total W/OFF",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	/*alInnerExport.add(new DataStyle("WRITE OFF PROF FEES",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("OPE",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Service Tax",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Cess 2% on S. Tax",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Cess 1% on S. Tax",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Month of Entry",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));*/
	   	
	}


	public String loadPaySlips(UtilityFunctions uF){
//		orgList = new FillOrganisation(request).fillOrganisation();
//		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//		departmentList = new FillDepartment(request).fillDepartment();
//		clientList = new FillClients(request).fillClients();
		
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		projectServiceList = new FillServices(request).fillProjectServices();
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		clientList = new FillClients(request).fillAllClients(false);
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		if(isPoFlag()){
			alFilter.add("PROJECT_TYPE");
			if(getStrProType()!=null) {
				String strProType="";
				if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
					strProType = "My Projects";
				}
				if(strProType!=null && !strProType.equals("")) {
					hmFilter.put("PROJECT_TYPE", strProType);
				} else {
					hmFilter.put("PROJECT_TYPE", "All Projects");
				}
			} else {
				hmFilter.put("PROJECT_TYPE", "All Projects");
			}
		}
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
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
		
		
		/*alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
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
		}*/
		
		
		alFilter.add("PROJECT_SERVICE");
		if(getF_project_service()!=null) {
			String strProjectService="";
			int k=0;
			for(int i=0;projectServiceList!=null && i<projectServiceList.size();i++) {
				for(int j=0;j<getF_project_service().length;j++) {
					if(getF_project_service()[j].equals(projectServiceList.get(i).getServiceId())) {
						if(k==0) {
							strProjectService=projectServiceList.get(i).getServiceName();
						} else {
							strProjectService+=", "+projectServiceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strProjectService!=null && !strProjectService.equals("")) {
				hmFilter.put("PROJECT_SERVICE", strProjectService);
			} else {
				hmFilter.put("PROJECT_SERVICE", "All Services");
			}
		} else {
			hmFilter.put("PROJECT_SERVICE", "All Services");
		}
		
		alFilter.add("CLIENT");
		if(getF_client()!=null) {
			String strClient="";
			int k=0;
			for(int i=0; clientList!=null && i<clientList.size();i++) {
				for(int j=0;j<getF_client().length;j++) {
					if(getF_client()[j].equals(clientList.get(i).getClientId())) {
						if(k==0) {
							strClient=clientList.get(i).getClientName();
						} else {
							strClient+=", "+clientList.get(i).getClientName();
						}
						k++;
					}
				}
			}
			if(strClient!=null && !strClient.equals("")) {
				hmFilter.put("CLIENT", strClient);
			} else {
				hmFilter.put("CLIENT", "All Clients");
			}
		} else {
			hmFilter.put("CLIENT", "All Clients");
		}
		
		
		
		if(getSelectOne()!= null && !getSelectOne().equals("")) {
			alFilter.add("PERIOD");
			
			String strSelectOne="";
			if(uF.parseToInt(getSelectOne()) == 1) {
				strSelectOne="From - To";
			} else if(uF.parseToInt(getSelectOne()) == 2) {
				strSelectOne="Financial Year";
			} else if(uF.parseToInt(getSelectOne()) == 3) {
				strSelectOne="Month";
			} else if(uF.parseToInt(getSelectOne()) == 4) {
				strSelectOne="Paycycle";
			}
			if(strSelectOne!=null && !strSelectOne.equals("")) {
				hmFilter.put("PERIOD", strSelectOne);
			}
			
		}
		
		if(uF.parseToInt(getSelectOne()) == 1) {
			alFilter.add("FROMTO");
			String strtDate = "";
			String endDate = "";
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("") && !getStrStartDate().equals("")) {
				strtDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			if(getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("") && !getStrEndDate().equals("")) {
				endDate = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			hmFilter.put("FROMTO", strtDate +" - "+ endDate);
			
		} else if(uF.parseToInt(getSelectOne()) == 2) {
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			}
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} else if(uF.parseToInt(getSelectOne()) == 3) {
			alFilter.add("MONTH");
			int nselectedMonth = uF.parseToInt(getStrMonth());
			String strMonth = uF.getMonth(nselectedMonth);
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			int nFYSMonth = 0;
			int nFYSDay = 0;
			String[] strFinancialYears = null;
//			System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
			if (getMonthFinancialYear() != null) {
				strFinancialYears = getMonthFinancialYear().split("-");
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			cal.set(Calendar.DATE, nFYSDay);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
			}
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			
			hmFilter.put("MONTH", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + strMonth);
		} else if(uF.parseToInt(getSelectOne()) == 4) {
			alFilter.add("PAYCYCLE");
			String strPaycycle = "";
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			}
			hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_project_service() {
		return f_project_service;
	}

	public void setF_project_service(String[] f_project_service) {
		this.f_project_service = f_project_service;
	}

	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}

	public String getSelectOne() {
		return selectOne;
	}

	public void setSelectOne(String selectOne) {
		this.selectOne = selectOne;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
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

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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

	public List<FillServices> getProjectServiceList() {
		return projectServiceList;
	}

	public void setProjectServiceList(List<FillServices> projectServiceList) {
		this.projectServiceList = projectServiceList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getStrProType() {
		return strProType;
	}

	public void setStrProType(String strProType) {
		this.strProType = strProType;
	}

	public boolean isPoFlag() {
		return poFlag;
	}

	public void setPoFlag(boolean poFlag) {
		this.poFlag = poFlag;
	}

	public String getStrInvoiceIds() {
		return strInvoiceIds;
	}

	public void setStrInvoiceIds(String strInvoiceIds) {
		this.strInvoiceIds = strInvoiceIds;
	}

	public String getMonthFinancialYear() {
		return monthFinancialYear;
	}

	public void setMonthFinancialYear(String monthFinancialYear) {
		this.monthFinancialYear = monthFinancialYear;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
