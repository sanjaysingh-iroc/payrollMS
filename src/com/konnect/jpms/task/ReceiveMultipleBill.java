package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPaymentSource;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ReceiveMultipleBill extends ActionSupport implements ServletRequestAware,IStatements 
{
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private HttpServletRequest request;
	String strSessionEmpId;
	String strProductType =  null;
	String strOrgId;

	String invoiceAmount;
	String paidAmount;
	List<FillPaymentSource> paymentSourceList;
	String paymentSource;
	String paymentDescription;
	String amountReceived;
	String amountDue;
	String[] invoiceId;
	String submit;
	String deductTDS;
	String strInstrumentNo; 
	String strInstrumentDate;
	
//	String hideTdsAmount;
	String hidePreviousYearTaxName;
	String hidePreviousYearTdsAmount;
	String hidePreviousYearTdsPercent;
	
	String balanceAmount;
	String writeoffBalance;
	String exchangeRate;
	String currId;
	String proId;
	String proFreqId;
	String clientId;
	String stateId;
//	String hideOtherDeduction;
//	
//	String writeOffPercent;
	String writeOffDescription;
	
//	String opeAmount;
//	String professionalFees;
//	String serviceTax;
//	String cess2Percent;
//	String cess1Percent;
	 
	String hidewriteOffAmount;
	String hidewriteOffPercent;
	
//	String hideWriteOffProfessExp;
//	String hidewriteOffOperationExp;
//	String hidewriteOffServiceTax;
//	String hidewriteOffCess2Percent;
//	String hidewriteOffCess1Percent;
	
	String deductionType;
	String writeOffType;
	
	String hideTotTaxAmount;
	String hideTotTaxPercent;
	
	String[] strHideTax;
	String[] strTaxAmount;
	String[] strHideTaxPercent;
	
	String[] strHideParticulars;
	String[] strParticularsAmount;
	String[] strHideParticularsPercent;
	
	String[] strHideOPE;
	String[] strOPEAmount;
	String[] strHideOPEPercent;
	
	
	String[] hideTaxName;
	String[] hideTaxAmount;
	String[] hideTaxPercent;
	
	String[] hidewoOPE;
	String[] hidewoOPEAmount;
	String[] hidewoOPEPercent;
	
	String[] hidewoParti;
	String[] hidewoPartiAmount;
	String[] hidewoPartiPercent;
	
	String[] hidewoTax;
	String[] hidewoTaxAmount;
	String[] hidewoTaxPercent;
	
	String invoice_id;
	String pro_id;
	String pro_freq_id;
	String client_id;
	
	public String execute() {
		session=request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		request.setAttribute(PAGE, "/jsp/task/ReceiveMultipleBill.jsp");
		request.setAttribute(TITLE, "Multiple Bill Details");
		
		String strInvoiceId = getInvoice_id();
		String strProId = getPro_id();
		String strProFreqId = getPro_freq_id();
		String strClientId = getClient_id();
		
		if(getSubmit() != null) {
			insertProjectInvoice(uF);
			return SUCCESS;
		}
		
//		System.out.println("getSubmit ===>> " + getSubmit());
//		System.out.println("strInvoiceId ===>> " + strInvoiceId);
//		System.out.println("strProId ===>> " + strProId);
//		System.out.println("strClientId ===>> " + strClientId);
		
		getProjectDetails(uF, strProFreqId, strInvoiceId, strProId, strClientId);
		
		loadProjectInvoice(uF);
    	
		return LOAD;
	}

	
	
	private void insertProjectInvoice(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con  = db.makeConnection(con);
			
			for(int a=0; getInvoiceId() != null && a<getInvoiceId().length; a++) {
					if(getInvoiceId()[a] != null && !getInvoiceId()[a].equals("") && !getInvoiceId()[a].equals("0")) {
//					System.out.println("getInvoiceId() ===>> " + getInvoiceId()[i]);
					setStateId(request.getParameter("stateId_"+getInvoiceId()[a]));
					setProId(request.getParameter("proId_"+getInvoiceId()[a]));
					setProFreqId(request.getParameter("proFreqId_"+getInvoiceId()[a]));
					setCurrId(request.getParameter("currId_"+getInvoiceId()[a]));
					
//					System.out.println("invoiceAmount ===>> " + request.getParameter("invoiceAmount"+getInvoiceId()[a]));
					setInvoiceAmount(request.getParameter("invoiceAmount_"+getInvoiceId()[a]));
					setAmountDue(request.getParameter("amountDue_"+getInvoiceId()[a]));
					setExchangeRate(request.getParameter("exchangeRate_"+getInvoiceId()[a]));
					setAmountReceived(request.getParameter("amountReceived_"+getInvoiceId()[a]));
					setWriteoffBalance(request.getParameter("writeoffBalance_"+getInvoiceId()[a]));
					setDeductTDS(request.getParameter("deductTDS_"+getInvoiceId()[a]));
					setBalanceAmount(request.getParameter("balanceAmount_"+getInvoiceId()[a]));
					
					setPaymentDescription(request.getParameter("paymentDescription_"+getInvoiceId()[a]));
					setPaymentSource(request.getParameter("paymentSource_"+getInvoiceId()[a]));
					setStrInstrumentNo(request.getParameter("strInstrumentNo_"+getInvoiceId()[a]));
					setStrInstrumentDate(request.getParameter("strInstrumentDate_"+getInvoiceId()[a]));
					
					setHidewriteOffAmount(request.getParameter("hidewriteOffAmount_"+getInvoiceId()[a]));
					setHidewriteOffPercent(request.getParameter("hidewriteOffPercent_"+getInvoiceId()[a]));
					
					setWriteOffDescription(request.getParameter("writeOffDescription_"+getInvoiceId()[a]));
					
					setHideTotTaxAmount(request.getParameter("hideTotTaxAmount_"+getInvoiceId()[a]));
					setHideTotTaxPercent(request.getParameter("hideTotTaxPercent_"+getInvoiceId()[a]));
					
					setHidePreviousYearTaxName(request.getParameter("hidePreviousYearTaxName_"+getInvoiceId()[a]));
					setHidePreviousYearTdsAmount(request.getParameter("hidePreviousYearTdsAmount_"+getInvoiceId()[a]));
					setHidePreviousYearTdsPercent(request.getParameter("hidePreviousYearTdsPercent_"+getInvoiceId()[a]));
					
					setStrHideTax(request.getParameterValues("strHideTax_"+getInvoiceId()[a]));
					setStrTaxAmount(request.getParameterValues("strTaxAmount_"+getInvoiceId()[a]));
					setStrHideTaxPercent(request.getParameterValues("strHideTaxPercent_"+getInvoiceId()[a]));
					
					setStrHideParticulars(request.getParameterValues("strHideParticulars_"+getInvoiceId()[a]));
					setStrParticularsAmount(request.getParameterValues("strParticularsAmount_"+getInvoiceId()[a]));
					setStrHideParticularsPercent(request.getParameterValues("strHideParticularsPercent_"+getInvoiceId()[a]));
					
					setStrHideOPE(request.getParameterValues("strHideOPE_"+getInvoiceId()[a]));
					setStrOPEAmount(request.getParameterValues("strOPEAmount_"+getInvoiceId()[a]));
					setStrHideOPEPercent(request.getParameterValues("strHideOPEPercent_"+getInvoiceId()[a]));
					
					setHideTaxName(request.getParameterValues("hideTaxName_"+getInvoiceId()[a]));
					setHideTaxAmount(request.getParameterValues("hideTaxAmount_"+getInvoiceId()[a]));
					setHideTaxPercent(request.getParameterValues("hideTaxPercent_"+getInvoiceId()[a]));
					
					setHidewoOPE(request.getParameterValues("hidewoOPE_"+getInvoiceId()[a]));
					setHidewoOPEAmount(request.getParameterValues("hidewoOPEAmount_"+getInvoiceId()[a]));
					setHidewoOPEPercent(request.getParameterValues("hidewoOPEPercent_"+getInvoiceId()[a]));
					
					setHidewoParti(request.getParameterValues("hidewoParti_"+getInvoiceId()[a]));
					setHidewoPartiAmount(request.getParameterValues("hidewoPartiAmount_"+getInvoiceId()[a]));
					setHidewoPartiPercent(request.getParameterValues("hidewoPartiPercent_"+getInvoiceId()[a]));
					
					setHidewoTax(request.getParameterValues("hidewoTax_"+getInvoiceId()[a]));
					setHidewoTaxAmount(request.getParameterValues("hidewoTaxAmount_"+getInvoiceId()[a]));
					setHidewoTaxPercent(request.getParameterValues("hidewoTaxPercent_"+getInvoiceId()[a]));
					
//					System.out.println("getStrInstrumentDate() ===>> " + getStrInstrumentDate());
					
					pst = con.prepareStatement("select max(bill_id), bill_no from promntc_bill_amt_details group by bill_no");
					rs = pst.executeQuery();
					int nBillNo = 0;
					while(rs.next()) {
						nBillNo = uF.parseToInt(rs.getString("bill_no"));
					}
					rs.close();
					pst.close();
					nBillNo++;
					
					pst = con.prepareStatement("insert into promntc_bill_amt_details (invoice_id, invoice_amount, received_amount, is_tds_deducted, " +
							"payment_description, payment_mode, ins_no, ins_date, received_by, is_write_off, write_off_amount, balance_amount, curr_id, " +
							"exchange_rate, entry_date, bill_no, pro_id, write_off_desc, tds_deducted, pro_freq_id) " +
							"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getInvoiceId()[a]));
					pst.setDouble(2, uF.parseToDouble(getInvoiceAmount()));
					pst.setDouble(3, uF.parseToDouble(getAmountReceived()));
					pst.setBoolean(4, uF.parseToBoolean(getDeductTDS()));
					pst.setString(5, getPaymentDescription());
					pst.setString(6, getPaymentSource());
					pst.setString(7, getStrInstrumentNo());
					pst.setDate(8, uF.getDateFormat(getStrInstrumentDate(), DATE_FORMAT));
					pst.setInt(9, uF.parseToInt(strSessionEmpId));
					pst.setBoolean(10, uF.parseToBoolean(getWriteoffBalance()));
					if(uF.parseToBoolean(getWriteoffBalance())) {
						pst.setDouble(11, uF.parseToDouble(getHidewriteOffAmount()));
					} else {
						pst.setDouble(11, 0);
					}
					pst.setDouble(12, uF.parseToDouble(getBalanceAmount()));
					pst.setInt(13, uF.parseToInt(getCurrId()));
					pst.setDouble(14, uF.parseToDouble(getExchangeRate()));
					pst.setDate(15, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(16, nBillNo+"");
					pst.setInt(17, uF.parseToInt(getProId()));
					pst.setString(18, getWriteOffDescription());
					pst.setDouble(19, uF.parseToDouble(getHideTotTaxAmount()));
					pst.setInt(20, uF.parseToInt(getProFreqId()));
					pst.executeUpdate();
					pst.close();
					
					pst = con.prepareStatement("select max(bill_id) as bill_id from promntc_bill_amt_details");
					rs = pst.executeQuery();
					int newBillId = 0;
					while(rs.next()) {
						newBillId = rs.getInt("bill_id");
					}
					rs.close();
					pst.close();
					
					for(int i=0; getStrHideOPE()!=null && i<getStrHideOPE().length;i++) {
						pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
							"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,tax_percent,amt_receive_type) values(?,?,?,?, ?,?,?)");
						pst.setString(1, getStrHideOPE()[i]);
						pst.setDouble(2, uF.parseToDouble(getStrOPEAmount()[i]));
						pst.setInt(3, newBillId);
						pst.setDouble(4, uF.parseToDouble(getStrOPEAmount()[i]));
						pst.setString(5, HEAD_OPE);
						pst.setDouble(6, uF.parseToDouble(getStrHideOPEPercent()[i]));
						pst.setInt(7, AMT_RECEIVE_BILL_AMT);
						pst.executeUpdate();
						pst.close();
					}
					
					for(int i=0;getStrHideParticulars()!=null && i<getStrHideParticulars().length;i++) {
						pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
							"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,tax_percent,amt_receive_type) values(?,?,?,?, ?,?,?)");
						pst.setString(1, getStrHideParticulars()[i]);
						pst.setDouble(2, uF.parseToDouble(getStrParticularsAmount()[i]));
						pst.setInt(3, newBillId);
						pst.setDouble(4, uF.parseToDouble(getStrParticularsAmount()[i]));
						pst.setString(5, HEAD_PARTI);
						pst.setDouble(6, uF.parseToDouble(getStrHideParticularsPercent()[i]));
						pst.setInt(7, AMT_RECEIVE_BILL_AMT);
						pst.executeUpdate();
						pst.close();
					}
					
					for(int i=0;getStrHideTax()!=null && i<getStrHideTax().length;i++) {
						pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
							"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,tax_percent,amt_receive_type) values(?,?,?,?, ?,?,?)");
						pst.setString(1, getStrHideTax()[i]);
						pst.setDouble(2, uF.parseToDouble(getStrTaxAmount()[i]));
						pst.setInt(3, newBillId);
						pst.setDouble(4, uF.parseToDouble(getStrTaxAmount()[i]));
						pst.setString(5, HEAD_TAX);
						pst.setDouble(6, uF.parseToDouble(getStrHideTaxPercent()[i]));
						pst.setInt(7, AMT_RECEIVE_BILL_AMT);
						pst.executeUpdate();
						pst.close();
					}
					
					for(int i=0;getHideTaxName()!=null && i<getHideTaxName().length;i++) {
						pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
							"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,tax_percent,amt_receive_type) values(?,?,?,?, ?,?,?)");
						pst.setString(1, getHideTaxName()[i]);
						pst.setDouble(2, uF.parseToDouble(getHideTaxAmount()[i]));
						pst.setInt(3, newBillId);
						pst.setDouble(4, uF.parseToDouble(getHideTaxAmount()[i]));
						pst.setString(5, HEAD_TAX);
						pst.setDouble(6, uF.parseToDouble(getHideTaxPercent()[i]));
						pst.setInt(7, AMT_RECEIVE_TAX_DEDUCT);
						pst.executeUpdate();
						pst.close();
					}
					
					pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
						"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,tax_percent,amt_receive_type) values(?,?,?,?, ?,?,?)");
					pst.setString(1, getHidePreviousYearTaxName());
					pst.setDouble(2, uF.parseToDouble(getHidePreviousYearTdsAmount()));
					pst.setInt(3, newBillId);
					pst.setDouble(4, uF.parseToDouble(getHidePreviousYearTdsAmount()));
					pst.setString(5, HEAD_TAX);
					pst.setDouble(6, uF.parseToDouble(getHidePreviousYearTdsPercent()));
					pst.setInt(7, AMT_RECEIVE_TAX_DEDUCT_PREV_YR);
					pst.executeUpdate();
					pst.close();
					
					for(int i=0;getHidewoOPE()!=null && i<getHidewoOPE().length;i++) {
						pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
							"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,tax_percent,amt_receive_type) values(?,?,?,?, ?,?,?)");
						pst.setString(1, getHidewoOPE()[i]);
						pst.setDouble(2, uF.parseToDouble(getHidewoOPEAmount()[i]));
						pst.setInt(3, newBillId);
						pst.setDouble(4, uF.parseToDouble(getHidewoOPEAmount()[i]));
						pst.setString(5, HEAD_OPE);
						pst.setDouble(6, uF.parseToDouble(getHidewoOPEPercent()[i]));
						pst.setInt(7, AMT_RECEIVE_WRITE_OFF);
						pst.executeUpdate();
						pst.close();
					}
					
					for(int i=0;getHidewoParti()!=null && i<getHidewoParti().length;i++) {
						pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
							"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,tax_percent,amt_receive_type) values(?,?,?,?, ?,?,?)");
						pst.setString(1, getHidewoParti()[i]);
						pst.setDouble(2, uF.parseToDouble(getHidewoPartiAmount()[i]));
						pst.setInt(3, newBillId);
						pst.setDouble(4, uF.parseToDouble(getHidewoPartiAmount()[i]));
						pst.setString(5, HEAD_PARTI);
						pst.setDouble(6, uF.parseToDouble(getHidewoPartiPercent()[i]));
						pst.setInt(7, AMT_RECEIVE_WRITE_OFF);
						pst.executeUpdate();
						pst.close();
					}
					
					for(int i=0;getHidewoTax()!=null && i<getHidewoTax().length;i++) {
						pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
							"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,tax_percent,amt_receive_type) values(?,?,?,?, ?,?,?)");
						pst.setString(1, getHidewoTax()[i]);
						pst.setDouble(2, uF.parseToDouble(getHidewoTaxAmount()[i]));
						pst.setInt(3, newBillId);
						pst.setDouble(4, uF.parseToDouble(getHidewoTaxAmount()[i]));
						pst.setString(5, HEAD_TAX);
						pst.setDouble(6, uF.parseToDouble(getHidewoTaxPercent()[i]));
						pst.setInt(7, AMT_RECEIVE_WRITE_OFF);
						pst.executeUpdate();
						pst.close();
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	
//	private void insertProjectInvoice(UtilityFunctions uF) {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		
//		try {
//			
//			con  = db.makeConnection(con);
//			
//			for(int i=0; getInvoiceId() != null && i<getInvoiceId().length; i++) {
//					if(getInvoiceId()[i] != null && !getInvoiceId()[i].equals("") && !getInvoiceId()[i].equals("0")) {
////					System.out.println("getInvoiceId() ===>> " + getInvoiceId()[i]);
//					setStateId(request.getParameter("stateId"+getInvoiceId()[i]));
//					setProId(request.getParameter("proId"+getInvoiceId()[i]));
//					setCurrId(request.getParameter("currId"+getInvoiceId()[i]));
//					
////					System.out.println("invoiceAmount ===>> " + request.getParameter("invoiceAmount"+getInvoiceId()[i]));
//					setInvoiceAmount(request.getParameter("invoiceAmount"+getInvoiceId()[i]));
//					setAmountDue(request.getParameter("amountDue"+getInvoiceId()[i]));
//					setExchangeRate(request.getParameter("exchangeRate"+getInvoiceId()[i]));
//					setAmountReceived(request.getParameter("amountReceived"+getInvoiceId()[i]));
//					setWriteoffBalance(request.getParameter("writeoffBalance"+getInvoiceId()[i]));
//					setDeductTDS(request.getParameter("deductTDS"+getInvoiceId()[i]));
//					setBalanceAmount(request.getParameter("balanceAmount"+getInvoiceId()[i]));
//					setPaymentDescription(request.getParameter("paymentDescription"+getInvoiceId()[i]));
//					setPaymentSource(request.getParameter("paymentSource"+getInvoiceId()[i]));
//					setStrInstrumentNo(request.getParameter("strInstrumentNo"+getInvoiceId()[i]));
//					setStrInstrumentDate(request.getParameter("strInstrumentDate"+getInvoiceId()[i]));
//					setWriteOffPercent(request.getParameter("writeOffPercent"+getInvoiceId()[i]));
//					setHidewriteOffAmount(request.getParameter("hidewriteOffAmount"+getInvoiceId()[i]));
//					setHideWriteOffProfessExp(request.getParameter("hideWriteOffProfessExp"+getInvoiceId()[i]));
//					setHidewriteOffOperationExp(request.getParameter("hidewriteOffOperationExp"+getInvoiceId()[i]));
//					setHidewriteOffServiceTax(request.getParameter("hidewriteOffServiceTax"+getInvoiceId()[i]));
//					setWriteOffDescription(request.getParameter("writeOffDescription"+getInvoiceId()[i]));
//					setHideTdsAmount(request.getParameter("hideTdsAmount"+getInvoiceId()[i]));
//					setHidePreviousYearTdsAmount(request.getParameter("hidePreviousYearTdsAmount"+getInvoiceId()[i]));
//					setHideOtherDeduction(request.getParameter("hideOtherDeduction"+getInvoiceId()[i]));
//					
////					System.out.println("getStrInstrumentDate() ===>> " + getStrInstrumentDate());
//					
//					Map<String, String> hmTaxMiscSetting = CF.getDeductionTaxMISCDetails(con);
//					
//					pst = con.prepareStatement("select max(bill_id), bill_no from promntc_bill_amt_details group by bill_no");
//					rs = pst.executeQuery();
//					int nBillNo = 0;
//					while(rs.next()) {
//						nBillNo = uF.parseToInt(rs.getString("bill_no"));
//					}
//					rs.close();
//					pst.close();
//					nBillNo++;
//					
//					String []strFinancialYear = CF.getFinancialYear(con, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT), CF, uF);
////					Map<String, String> hmOtherTax = getOtherTaxDetails(con, uF, strFinancialYear[0], strFinancialYear[1]);
//					
//					double dblServiceTax = uF.parseToDouble(hmTaxMiscSetting.get("SERVICE_TAX"));
//					double dblCess1 = uF.parseToDouble(hmTaxMiscSetting.get("EDUCATION_TAX"));
//					double dblCess2 = uF.parseToDouble(hmTaxMiscSetting.get("STANDARD_TAX"));
//					
//					double dblServiceTaxAmt = dblServiceTax * uF.parseToDouble(getAmountReceived()) / 100;
//					double dblCess1Amt = dblCess1 * uF.parseToDouble(getAmountReceived()) / 100;
//					double dblCess2Amt = dblCess2 * uF.parseToDouble(getAmountReceived()) / 100;
//					
//					double dblprofessionalFeesAmt = uF.parseToDouble(getAmountReceived()) - (dblServiceTaxAmt+dblCess1Amt+dblCess2Amt);
//					
//	//				double dblExchangeRate = uF.parseToDouble(getExchangeRate());
//	//				double dblDueAmount = uF.parseToDouble(getAmountDue());
//	//				double dblReceivedAmount = uF.parseToDouble(getAmountReceived());
//	//				double dblTDSAmount = uF.parseToDouble(getHideTdsAmount());
//	//				double dblBalanceAmount = 0;
//	//				if(dblExchangeRate>0 && dblReceivedAmount>0 && dblTDSAmount>=0) {
//	//					dblBalanceAmount = dblDueAmount - (dblReceivedAmount / dblExchangeRate) - (dblTDSAmount / dblExchangeRate);
//	//				}
//					
//					pst = con.prepareStatement("insert into promntc_bill_amt_details (invoice_id, invoice_amount, received_amount, tds_deducted, " +
//						"is_tds_deducted, payment_description, payment_mode, ins_no, ins_date, received_by, is_write_off, write_off_amount, " +
//						"balance_amount, curr_id, exchange_rate, entry_date, bill_no, pro_id, service_tax, cess1, cess2, other_deduction, " +
//						"write_off_prof_ex, write_off_op_ex, write_off_service_tax, write_off_desc,write_off_percent, prev_year_tds_deducted," +
//						"professional_fees,write_off_cess1,write_off_cess2,ope_amount) " +
//						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
//					pst.setInt(1, uF.parseToInt(getInvoiceId()[i]));
//					pst.setDouble(2, uF.parseToDouble(getInvoiceAmount()));
//					pst.setDouble(3, uF.parseToDouble(getAmountReceived()));
//					pst.setDouble(4, uF.parseToDouble(getHideTdsAmount()));
//					pst.setBoolean(5, uF.parseToBoolean(getDeductTDS()));
//					pst.setString(6, getPaymentDescription());
//					pst.setString(7, getPaymentSource());
//					pst.setString(8, getStrInstrumentNo());
//					pst.setDate(9, uF.getDateFormat(getStrInstrumentDate(), DATE_FORMAT));
//					pst.setInt(10, uF.parseToInt(strSessionEmpId));
//					
//					pst.setBoolean(11, uF.parseToBoolean(getWriteoffBalance()));
//					if(uF.parseToBoolean(getWriteoffBalance())) {
//						pst.setDouble(12, uF.parseToDouble(getHidewriteOffAmount()));
//					} else {
//						pst.setDouble(12, 0);
//					}
//					
//		//			if(uF.parseToBoolean(getWriteoffBalance())) {
//		//				pst.setDouble(13, 0);
//		//			} else {
//						pst.setDouble(13, uF.parseToDouble(getBalanceAmount()));
//		//			}
//					
//					pst.setInt(14, uF.parseToInt(getCurrId()));
//					pst.setDouble(15, uF.parseToDouble(getExchangeRate()));
//					pst.setDate(16, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setString(17, nBillNo+"");
//					pst.setInt(18, uF.parseToInt(getProId()));
//					
////					pst.setDouble(19, Math.round(dblServiceTaxAmt));
////					pst.setDouble(20, Math.round(dblCess1Amt));
////					pst.setDouble(21, Math.round(dblCess2Amt));
//					pst.setDouble(19, uF.parseToDouble(getServiceTax()));
//					pst.setDouble(20, uF.parseToDouble(getCess1Percent()));
//					pst.setDouble(21, uF.parseToDouble(getCess2Percent()));
//					pst.setDouble(22, uF.parseToDouble(getHideOtherDeduction()));
//					
//					if(uF.parseToBoolean(getWriteoffBalance())) {
//						pst.setDouble(23, uF.parseToDouble(getHideWriteOffProfessExp()));
//						pst.setDouble(24, uF.parseToDouble(getHidewriteOffOperationExp()));
//						pst.setDouble(25, uF.parseToDouble(getHidewriteOffServiceTax()));
//						pst.setString(26, getWriteOffDescription());
//						pst.setDouble(27, uF.parseToDouble(getWriteOffPercent()));
//					} else {
//						pst.setDouble(23, 0);
//						pst.setDouble(24, 0);
//						pst.setDouble(25, 0);
//						pst.setString(26, getWriteOffDescription());
//						pst.setDouble(27, 0);
//					}
//					pst.setDouble(28, uF.parseToDouble(getHidePreviousYearTdsAmount()));
//					pst.setDouble(29, uF.parseToDouble(getProfessionalFees()));
//					pst.setDouble(30, uF.parseToDouble(getHidewriteOffCess1Percent()));
//					pst.setDouble(31, uF.parseToDouble(getHidewriteOffCess2Percent()));
//					pst.setDouble(32, uF.parseToDouble(getOpeAmount()));
////					System.out.println("pst ===>> " + pst);
//					pst.execute();
//					pst.close();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	
	
	private void getProjectDetails(UtilityFunctions uF, String strProFreqId, String strInvoiceId, String strProId, String strClientId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			/*Map<String, String> hmTaxMiscSetting = CF.getTaxMiscSetting(con);
			request.setAttribute("hmTaxMiscSetting", hmTaxMiscSetting);*/
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetailsForMultiBillReceive(con);
			
			Map<String, String> hmClientData = new HashMap<String, String>();
			
			if(strClientId !=null && strClientId.length()>0) {
				pst = con.prepareStatement("select tds_percent,registration_no,client_id from client_details where client_id in ("+strClientId+")");
	//			pst.setInt(1, uF.parseToInt(strClientId));
				rs = pst.executeQuery();
				while(rs.next()) {
					hmClientData.put(rs.getString("client_id")+"_TDS_PERCENT", rs.getString("tds_percent"));
					hmClientData.put(rs.getString("client_id")+"_REG_NO", rs.getString("registration_no"));
				}
				rs.close();
				pst.close();
			}
//			System.out.println("hmClientData ===>> " + hmClientData);
			
			request.setAttribute("hmClientData", hmClientData);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select invoice_id, received_amount, tds_deducted, write_off_amount, exchange_rate from promntc_bill_amt_details where invoice_id > 0 ");
//			if(strInvoiceId != null && strInvoiceId.length()>0) {
//				sbQuery.append(" and invoice_id in ("+strInvoiceId+")");
//			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			double dblAmtReceived = 0;
			double dblAmtTDS = 0;
			double dblAmtWriteOff = 0;
//			int nCurrId = 0;
			Map<String, Double> hmInvoiceRecTdsWOffAmt = new HashMap<String, Double>();
			while(rs.next()) {
				dblAmtReceived = uF.parseToDouble(""+hmInvoiceRecTdsWOffAmt.get(rs.getString("invoice_id")+"_REC_AMT"));
//				dblAmtReceived += uF.parseToDouble(rs.getString("received_amount")) / uF.parseToDouble(rs.getString("exchange_rate"));
				dblAmtReceived += uF.parseToDouble(rs.getString("received_amount"));
				dblAmtTDS = uF.parseToDouble(""+hmInvoiceRecTdsWOffAmt.get(rs.getString("invoice_id")+"_TDS_AMT"));
//				dblAmtTDS += uF.parseToDouble(rs.getString("tds_deducted")) / uF.parseToDouble(rs.getString("exchange_rate"));
				dblAmtTDS += uF.parseToDouble(rs.getString("tds_deducted"));
				
				dblAmtWriteOff = uF.parseToDouble(""+hmInvoiceRecTdsWOffAmt.get(rs.getString("invoice_id")+"_WRITE_OFF_AMT"));
				dblAmtWriteOff += uF.parseToDouble(rs.getString("write_off_amount"));
				
				hmInvoiceRecTdsWOffAmt.put(rs.getString("invoice_id")+"_REC_AMT", dblAmtReceived);
				hmInvoiceRecTdsWOffAmt.put(rs.getString("invoice_id")+"_TDS_AMT", dblAmtTDS);
				hmInvoiceRecTdsWOffAmt.put(rs.getString("invoice_id")+"_WRITE_OFF_AMT", dblAmtWriteOff);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmInvoiceRecTdsWOffAmt ===>> " + hmInvoiceRecTdsWOffAmt);
			setExchangeRate("1"); // default value for currency conversion
			
			
			StringBuilder sbQue = new StringBuilder();
			sbQue.append("select * from promntc_invoice_details where promntc_invoice_id > 0 order by invoice_no");
			pst = con.prepareStatement(sbQue.toString());
			rs = pst.executeQuery();
			
			List<List<String>> invoiceIDList = new ArrayList<List<String>>();
			while(rs.next()) {
				List<String> invoiceDataList = new ArrayList<String>();
				
				Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				invoiceDataList.add(rs.getString("promntc_invoice_id"));
				invoiceDataList.add(rs.getString("pro_id")); //1
				invoiceDataList.add(rs.getString("client_id")); //2
				invoiceDataList.add(rs.getString("invoice_code")); //3
				invoiceDataList.add(rs.getString("oc_invoice_amount")); //4
				double dblBalance = uF.parseToDouble(rs.getString("oc_invoice_amount")) - (uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_REC_AMT")+"") + uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_TDS_AMT")+"") + uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_WRITE_OFF_AMT")+""));
//				System.out.println(rs.getString("promntc_invoice_id") +" --->> oc_invoice_amount ===>> " + uF.parseToDouble(rs.getString("oc_invoice_amount")) + " ===>> " + (uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_REC_AMT")+"") + uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_TDS_AMT")+"") + uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_WRITE_OFF_AMT")+"")));
//				System.out.println(rs.getString("promntc_invoice_id") +" --->> " + dblBalance);
				invoiceDataList.add(uF.formatIntoTwoDecimalWithOutComma(dblBalance)); //5
				invoiceDataList.add("1"); // 6
				invoiceDataList.add(rs.getString("curr_id")); //7
				invoiceDataList.add(uF.showData(hmCurr.get("LONG_CURR"), "")); //8
				invoiceDataList.add(rs.getString("pro_freq_id")); //9
				if(dblBalance > 0) {
					invoiceIDList.add(invoiceDataList);
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("invoiceIDList", invoiceIDList);
			
		
			if(strProFreqId != null && strProFreqId.length()> 0 && strProId != null && strProId.length()> 0 && strInvoiceId != null && strInvoiceId.length()>0) {
				List<String> strInvcIdList = Arrays.asList(strInvoiceId.split(","));
				request.setAttribute("strInvcIdList", strInvcIdList);
				
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select * from promntc_invoice_details where promntc_invoice_id > 0 ");
				sbQuery1.append(" and pro_id in ("+strProId+") and pro_freq_id in ("+strProFreqId+") and promntc_invoice_id in ("+strInvoiceId+")");
	//			}
	//			pst = con.prepareStatement("select * from promntc_invoice_details where pro_id in ("+strProId+") and promntc_invoice_id in ("+strInvoiceId+")");
				pst = con.prepareStatement(sbQuery1.toString());
				rs = pst.executeQuery();
				Map<String, List<String>> hmInvoiceDetails = new HashMap<String, List<String>>();
				List<String> invoiceDataList = new ArrayList<String>();
				List<String> invoiceIdList = new ArrayList<String>();
				Map<String, String> invoiceAdHocType = new HashMap<String, String>();
				StringBuilder sbInvoiceIds = null;
				while(rs.next()) {
					Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
					
					invoiceDataList = hmInvoiceDetails.get(rs.getString("promntc_invoice_id"));
					if(invoiceDataList == null) invoiceDataList = new ArrayList<String>();
					
					invoiceDataList.add(rs.getString("promntc_invoice_id"));
					invoiceDataList.add(rs.getString("pro_id")); //1
					invoiceDataList.add(rs.getString("client_id")); //2
					invoiceDataList.add(rs.getString("invoice_code")); //3
					invoiceDataList.add(rs.getString("oc_invoice_amount")); //4
					double dblBalance = uF.parseToDouble(rs.getString("oc_invoice_amount")) - (uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_REC_AMT")+"") + uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_TDS_AMT")+"") + uF.parseToDouble(hmInvoiceRecTdsWOffAmt.get(rs.getString("promntc_invoice_id")+"_WRITE_OFF_AMT")+""));	
					invoiceDataList.add(uF.formatIntoTwoDecimalWithOutComma(dblBalance)); //5
					invoiceDataList.add("1"); //exchange Rate 6
	//				nCurrId = uF.parseToInt(rs.getString("curr_id"));
					invoiceDataList.add(rs.getString("curr_id")); //7
					invoiceDataList.add(uF.showData(hmCurr.get("LONG_CURR"), "")); //8
					invoiceDataList.add(rs.getString("pro_freq_id")); //9
					if(dblBalance > 0) {
						if(sbInvoiceIds == null) {
							sbInvoiceIds = new StringBuilder();
							sbInvoiceIds.append(rs.getString("promntc_invoice_id"));
						} else {
							sbInvoiceIds.append(","+rs.getString("promntc_invoice_id"));
						}
						invoiceIdList.add(rs.getString("promntc_invoice_id"));
						invoiceAdHocType.put(rs.getString("promntc_invoice_id"), rs.getString("adhoc_billing_type"));
						
						hmInvoiceDetails.put(rs.getString("promntc_invoice_id"), invoiceDataList);
					}
				}
				rs.close();
				pst.close();
				
				
				List<String> invoiceOPEHeads = new ArrayList<String>();
				List<String> invoicePartiHeads = new ArrayList<String>();
				List<String> invoiceTaxHeads = new ArrayList<String>();
				if(sbInvoiceIds != null && sbInvoiceIds.toString().length() > 0) {
					pst = con.prepareStatement("select invoice_particulars from promntc_invoice_amt_details where promntc_invoice_id in ("+sbInvoiceIds+") and head_type = '"+HEAD_OPE+"'");
	//				System.out.println("pst ====>> " + pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!invoiceOPEHeads.contains(rs.getString("invoice_particulars").trim())) {
							invoiceOPEHeads.add(rs.getString("invoice_particulars").trim());
						}
					}
					rs.close();
					pst.close();
				
					pst = con.prepareStatement("select invoice_particulars from promntc_invoice_amt_details where promntc_invoice_id in ("+sbInvoiceIds+") and head_type = '"+HEAD_PARTI+"'");
	//				System.out.println("pst ====>> " + pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!invoicePartiHeads.contains(rs.getString("invoice_particulars").trim())) {
							invoicePartiHeads.add(rs.getString("invoice_particulars").trim());
						}
					}
					rs.close();
					pst.close();
				
					pst = con.prepareStatement("select invoice_particulars from promntc_invoice_amt_details where promntc_invoice_id in ("+sbInvoiceIds+") and head_type = '"+HEAD_TAX+"'");
	//				System.out.println("pst ====>> " + pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!invoiceTaxHeads.contains(rs.getString("invoice_particulars").trim())) {
							invoiceTaxHeads.add(rs.getString("invoice_particulars").trim());
						}
					}
					rs.close();
					pst.close();
				
				}
	//			System.out.println("invoiceOPEHeads ====>> " + invoiceOPEHeads);
	//			System.out.println("invoicePartiHeads ====>> " + invoicePartiHeads);
	//			System.out.println("invoiceTaxHeads ====>> " + invoiceTaxHeads);
				
				request.setAttribute("invoiceOPEHeads", invoiceOPEHeads);
				request.setAttribute("invoicePartiHeads", invoicePartiHeads);
				request.setAttribute("invoiceTaxHeads", invoiceTaxHeads);
				
				
				Map<String, Map<String, List<String>>> hmInvoiceOPEHeads = new LinkedHashMap<String, Map<String, List<String>>>();
				Map<String, Map<String, List<String>>> hmInvoicePartiHeads = new LinkedHashMap<String, Map<String, List<String>>>();
				Map<String, Map<String, List<String>>> hmInvoiceTaxHeads = new LinkedHashMap<String, Map<String, List<String>>>();
				Map<String, Map<String, List<String>>> hmInvoiceProTaxHeads = new LinkedHashMap<String, Map<String, List<String>>>();
				
				if(invoiceIdList != null && !invoiceIdList.isEmpty()) {
					for(int i=0; i<invoiceIdList.size(); i++) {
						Map<String, List<String>> hmInvoicewiseOPEHeads = new HashMap<String, List<String>>();
						Map<String, List<String>> hmInvoicewisePartiHeads = new HashMap<String, List<String>>();
						Map<String, List<String>> hmInvoicewiseTaxHeads = new HashMap<String, List<String>>();
						pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id in ("+invoiceIdList.get(i)+") and head_type = '"+HEAD_OPE+"'");
	//					System.out.println("pst ====>> " + pst);
						rs = pst.executeQuery();
						while(rs.next()) {
							hmInvoicewiseOPEHeads = hmInvoiceOPEHeads.get(rs.getString("promntc_invoice_id"));
							if(hmInvoicewiseOPEHeads == null) hmInvoicewiseOPEHeads = new LinkedHashMap<String, List<String>>();
							
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("promntc_invoice_amt_id"));
							innerList.add(rs.getString("invoice_particulars").trim());
							innerList.add(rs.getString("invoice_particulars_amount"));
							innerList.add(rs.getString("oc_invoice_particulars_amount"));
							innerList.add(rs.getString("head_type"));
							innerList.add(rs.getString("tax_percent"));
							hmInvoicewiseOPEHeads.put(rs.getString("invoice_particulars").trim(), innerList);
							hmInvoiceOPEHeads.put(rs.getString("promntc_invoice_id"), hmInvoicewiseOPEHeads);
						}
						rs.close();
						pst.close();
					
						pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id in ("+invoiceIdList.get(i)+") and head_type = '"+HEAD_PARTI+"'");
	//					System.out.println("pst ====>> " + pst);
						rs = pst.executeQuery();
						while(rs.next()) {
							hmInvoicewisePartiHeads = hmInvoicePartiHeads.get(rs.getString("promntc_invoice_id"));
							if(hmInvoicewisePartiHeads == null) hmInvoicewisePartiHeads = new LinkedHashMap<String, List<String>>();
							
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("promntc_invoice_amt_id"));
							innerList.add(rs.getString("invoice_particulars").trim());
							innerList.add(rs.getString("invoice_particulars_amount"));
							innerList.add(rs.getString("oc_invoice_particulars_amount"));
							innerList.add(rs.getString("head_type"));
							innerList.add(rs.getString("tax_percent"));
							hmInvoicewisePartiHeads.put(rs.getString("invoice_particulars").trim(), innerList);
							
							hmInvoicePartiHeads.put(rs.getString("promntc_invoice_id"), hmInvoicewisePartiHeads);
						}
						rs.close();
						pst.close();
					
						pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id in ("+invoiceIdList.get(i)+") and head_type = '"+HEAD_TAX+"'");
	//					System.out.println("pst ====>> " + pst);
						rs = pst.executeQuery();
						while(rs.next()) {
							hmInvoicewiseTaxHeads = hmInvoiceTaxHeads.get(rs.getString("promntc_invoice_id"));
							if(hmInvoicewiseTaxHeads == null) hmInvoicewiseTaxHeads = new LinkedHashMap<String, List<String>>();
							
							List<String> innerList = new ArrayList<String>();
							innerList.add(rs.getString("promntc_invoice_amt_id"));
							innerList.add(rs.getString("invoice_particulars").trim());
							innerList.add(rs.getString("invoice_particulars_amount"));
							innerList.add(rs.getString("oc_invoice_particulars_amount"));
							innerList.add(rs.getString("head_type"));
							innerList.add(rs.getString("tax_percent"));
							hmInvoicewiseTaxHeads.put(rs.getString("invoice_particulars").trim(), innerList);
							
							hmInvoiceTaxHeads.put(rs.getString("promntc_invoice_id"), hmInvoicewiseTaxHeads);
						}
						rs.close();
						pst.close();
						
						pst = con.prepareStatement("select * from project_tax_setting where pro_id in (select pro_id from promntc_invoice_details where promntc_invoice_id in ("+invoiceIdList.get(i)+")) and invoice_or_customer=2 and status=true order by pro_tax_setting_id");
	//					System.out.println("pst======main==="+pst);
						rs = pst.executeQuery();
						Map<String, List<String>> hmProTaxHeadData = new LinkedHashMap<String, List<String>>();
						while (rs.next()) {
							hmProTaxHeadData = hmInvoiceProTaxHeads.get(invoiceIdList.get(i));
							if(hmProTaxHeadData == null) hmProTaxHeadData = new LinkedHashMap<String, List<String>>();
							
							List<String> alInner = new ArrayList<String>();
							alInner.add(rs.getString("pro_tax_setting_id"));
							alInner.add(rs.getString("tax_name"));
							alInner.add(rs.getString("tax_percent"));
							hmProTaxHeadData.put(rs.getString("pro_tax_setting_id"), alInner);
							hmInvoiceProTaxHeads.put(invoiceIdList.get(i), hmProTaxHeadData);
						}
						rs.close();
						pst.close();
						
						if(invoiceAdHocType != null && invoiceAdHocType.get(invoiceIdList.get(i)) != null && uF.parseToInt(invoiceAdHocType.get(invoiceIdList.get(i))) > 0) {
							pst = con.prepareStatement("select * from tax_setting where org_id=? and invoice_or_customer=2 order by tax_setting_id");
							pst.setInt(1, uF.parseToInt(strOrgId));
				//			System.out.println("pst======main==="+pst);
							rs = pst.executeQuery();
							while (rs.next()) {
								hmProTaxHeadData = hmInvoiceProTaxHeads.get(invoiceIdList.get(i));
								if(hmProTaxHeadData == null) hmProTaxHeadData = new LinkedHashMap<String, List<String>>();
								
								List<String> alInner = new ArrayList<String>();
								alInner.add(rs.getString("tax_setting_id"));
								alInner.add(rs.getString("tax_name"));
								alInner.add(rs.getString("tax_percent"));
								hmProTaxHeadData.put(rs.getString("tax_setting_id"), alInner);
								
								hmInvoiceProTaxHeads.put(invoiceIdList.get(i), hmProTaxHeadData);
							}
							rs.close();
							pst.close();
						}
						
					}
				}
				
	//			System.out.println("hmInvoiceOPEHeads ====>> " + hmInvoiceOPEHeads);
	//			System.out.println("hmInvoicePartiHeads ====>> " + hmInvoicePartiHeads);
	//			System.out.println("hmInvoiceTaxHeads ====>> " + hmInvoiceTaxHeads);
	//			System.out.println("hmInvoiceProTaxHeads ====>> " + hmInvoiceProTaxHeads);
				
				request.setAttribute("hmInvoiceOPEHeads", hmInvoiceOPEHeads);
				request.setAttribute("hmInvoicePartiHeads", hmInvoicePartiHeads);
				request.setAttribute("hmInvoiceTaxHeads", hmInvoiceTaxHeads);
				
				request.setAttribute("hmInvoiceProTaxHeads", hmInvoiceProTaxHeads);
				
				request.setAttribute("invoiceIdList", invoiceIdList);
				request.setAttribute("hmInvoiceDetails", hmInvoiceDetails);
				
				Map<String, String> hmProWStateId = new HashMap<String, String>();
				StringBuilder sbQuery2 = new StringBuilder();
				sbQuery2.append("select pro_id,wlocation_state_id from projectmntnc pm,work_location_info wi where wi.wlocation_id = pm.wlocation_id ");
				if(strProId != null && strProId.length()> 0 && strInvoiceId != null && strInvoiceId.length()>0) {
					sbQuery2.append(" and pro_id in ("+strProId+")");
				}
				pst = con.prepareStatement(sbQuery2.toString());
				rs = pst.executeQuery();
				while(rs.next()) {
					hmProWStateId.put(rs.getString("pro_id"), rs.getString("wlocation_state_id"));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmProWStateId", hmProWStateId);
				
	//			Map<String, String> hmCurr = hmCurrencyMap.get(nCurrId+"");
	//			request.setAttribute("hmCurr", hmCurr);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	/*private void getProjectDetails(UtilityFunctions uF, String strInvoiceId, String strProId, String strClientId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmTaxMiscSetting = CF.getTaxMiscSetting(con);
			request.setAttribute("hmTaxMiscSetting", hmTaxMiscSetting);
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			Map<String, String> hmClientData = new HashMap<String, String>();
			pst = con.prepareStatement("select tds_percent,registration_no,client_id from client_details where client_id in ("+strClientId+")");
//			pst.setInt(1, uF.parseToInt(strClientId));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmClientData.put(rs.getString("client_id")+"_TDS_PERCENT", rs.getString("tds_percent"));
				hmClientData.put(rs.getString("client_id")+"_REG_NO", rs.getString("registration_no"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmClientData ===>> " + hmClientData);
			
			request.setAttribute("hmClientData", hmClientData);
			
			pst = con.prepareStatement("select invoice_id, received_amount, tds_deducted, exchange_rate from promntc_bill_amt_details where invoice_id in ("+strInvoiceId+")");
//			pst.setInt(1, uF.parseToInt(strInvoiceId));
			rs = pst.executeQuery();
			double dblAmtReceived = 0;
			double dblAmtTDS = 0;
			int nCurrId = 0;
			Map<String, Double> hmInvoiceRecAmtTdsAmt = new HashMap<String, Double>();
			while(rs.next()) {
				dblAmtReceived = uF.parseToDouble(""+hmInvoiceRecAmtTdsAmt.get(rs.getString("invoice_id")+"_REC_AMT"));
				dblAmtReceived += uF.parseToDouble(rs.getString("received_amount")) / uF.parseToDouble(rs.getString("exchange_rate"));
				dblAmtTDS = uF.parseToDouble(""+hmInvoiceRecAmtTdsAmt.get(rs.getString("invoice_id")+"_TDS_AMT"));
				dblAmtTDS += uF.parseToDouble(rs.getString("tds_deducted")) / uF.parseToDouble(rs.getString("exchange_rate"));
				
				hmInvoiceRecAmtTdsAmt.put(rs.getString("invoice_id")+"_REC_AMT", dblAmtReceived);
				hmInvoiceRecAmtTdsAmt.put(rs.getString("invoice_id")+"_TDS_AMT", dblAmtTDS);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmInvoiceRecAmtTdsAmt ===>> " + hmInvoiceRecAmtTdsAmt);
			
			setExchangeRate("1"); // default value for currency conversion
			
			pst = con.prepareStatement("select * from promntc_invoice_details where pro_id in ("+strProId+") and promntc_invoice_id in ("+strInvoiceId+")");
//			pst.setInt(1, uF.parseToInt(strProId));
//			pst.setInt(2, uF.parseToInt(strInvoiceId));
			rs = pst.executeQuery();
			Map<String, List<String>> hmInvoiceDetails = new HashMap<String, List<String>>();
			List<String> invoiceDataList = new ArrayList<String>();
			List<String> invoiceIdList = new ArrayList<String>();
			double OPEAmount = 0;
			while(rs.next()) {
				invoiceDataList = hmInvoiceDetails.get(rs.getString("promntc_invoice_id"));
				if(invoiceDataList == null) invoiceDataList = new ArrayList<String>();
				invoiceIdList.add(rs.getString("promntc_invoice_id"));
				invoiceDataList.add(rs.getString("promntc_invoice_id"));
				invoiceDataList.add(rs.getString("pro_id")); //1
				invoiceDataList.add(rs.getString("client_id")); //2
				invoiceDataList.add(rs.getString("invoice_code")); //3
				invoiceDataList.add(rs.getString("invoice_amount")); //4
				double dblBalance = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmInvoiceRecAmtTdsAmt.get(rs.getString("promntc_invoice_id")+"_REC_AMT")+"") - uF.parseToDouble(hmInvoiceRecAmtTdsAmt.get(rs.getString("promntc_invoice_id")+"_TDS_AMT")+"");	
				invoiceDataList.add(""+dblBalance); //5
				invoiceDataList.add("1"); //exchange Rate 6
				nCurrId = uF.parseToInt(rs.getString("curr_id"));
				invoiceDataList.add(rs.getString("curr_id")); //7
				
				hmInvoiceDetails.put(rs.getString("promntc_invoice_id"), invoiceDataList);
//				setBalanceAmount(uF.formatIntoOneDecimalWithOutComma(dblBalance));
//				setAmountDue(uF.formatIntoOneDecimalWithOutComma(dblBalance));
//				setAmountReceived(uF.formatIntoOneDecimalWithOutComma(dblBalance));
//				setCurrId(rs.getString("curr_id"));
				OPEAmount = rs.getDouble("other_amount");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("OPEAmount", ""+OPEAmount);
			
//			System.out.println("hmInvoiceDetails ===>> " + hmInvoiceDetails);
			
			request.setAttribute("invoiceIdList", invoiceIdList);
			request.setAttribute("hmInvoiceDetails", hmInvoiceDetails);
			
			Map<String, String> hmProWStateId = new HashMap<String, String>();
			pst = con.prepareStatement("select pro_id,wlocation_state_id from projectmntnc pm,work_location_info wi where wi.wlocation_id = pm.wlocation_id  and pro_id in ("+strProId+")");
//			pst.setInt(1, uF.parseToInt(strProId));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmProWStateId.put(rs.getString("pro_id"), rs.getString("wlocation_state_id"));
//				setStateId(rs.getString("wlocation_state_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmProWStateId", hmProWStateId);
//			System.out.println("hmProWStateId ===>> " + hmProWStateId);
			
			Map<String, String> hmCurr = hmCurrencyMap.get(nCurrId+"");
			request.setAttribute("hmCurr", hmCurr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}*/
    
	private void loadProjectInvoice(UtilityFunctions uF) {

		paymentSourceList = new FillPaymentSource().fillPaymentMode();
		StringBuilder sb = new StringBuilder("");
		
		sb.append("<option value=\"0\">Other</option>");
		
		for (int i = 0; paymentSourceList != null && i < paymentSourceList.size(); i++) {
			sb.append("<option value=\"" + paymentSourceList.get(i).getPaymentSourceId() + "\">"
				+ paymentSourceList.get(i).getPaymentSourceName() + "</option>");
		}
		request.setAttribute("paymentSource", sb.toString());
	}

	
	

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public List<FillPaymentSource> getPaymentSourceList() {
		return paymentSourceList;
	}

	public void setPaymentSourceList(List<FillPaymentSource> paymentSourceList) {
		this.paymentSourceList = paymentSourceList;
	}

	public String getPaymentSource() {
		return paymentSource;
	}

	public void setPaymentSource(String paymentSource) {
		this.paymentSource = paymentSource;
	}

	public String getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getPaymentDescription() {
		return paymentDescription;
	}

	public void setPaymentDescription(String paymentDescription) {
		this.paymentDescription = paymentDescription;
	}

	public String getAmountReceived() {
		return amountReceived;
	}

	public void setAmountReceived(String amountReceived) {
		this.amountReceived = amountReceived;
	}

	public String getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(String amountDue) {
		this.amountDue = amountDue;
	}

	public String[] getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String[] invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getDeductTDS() {
		return deductTDS;
	}

	public void setDeductTDS(String deductTDS) {
		this.deductTDS = deductTDS;
	}
	
	public String getStrInstrumentNo() {
		return strInstrumentNo;
	}

	public void setStrInstrumentNo(String strInstrumentNo) {
		this.strInstrumentNo = strInstrumentNo;
	}

	public String getStrInstrumentDate() {
		return strInstrumentDate;
	}

	public void setStrInstrumentDate(String strInstrumentDate) {
		this.strInstrumentDate = strInstrumentDate;
	}

	public String getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public String getWriteoffBalance() {
		return writeoffBalance;
	}

	public void setWriteoffBalance(String writeoffBalance) {
		this.writeoffBalance = writeoffBalance;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getCurrId() {
		return currId;
	}

	public void setCurrId(String currId) {
		this.currId = currId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}
	

//	public Map<String, String> getOtherTaxDetails(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd) {
//
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
//		try {
//			
//			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
//				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return hmOtherTaxDetails;
//	}

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getHidePreviousYearTdsAmount() {
		return hidePreviousYearTdsAmount;
	}

	public void setHidePreviousYearTdsAmount(String hidePreviousYearTdsAmount) {
		this.hidePreviousYearTdsAmount = hidePreviousYearTdsAmount;
	}

	public String getHidewriteOffAmount() {
		return hidewriteOffAmount;
	}

	public void setHidewriteOffAmount(String hidewriteOffAmount) {
		this.hidewriteOffAmount = hidewriteOffAmount;
	}

	public String getWriteOffDescription() {
		return writeOffDescription;
	}

	public void setWriteOffDescription(String writeOffDescription) {
		this.writeOffDescription = writeOffDescription;
	}

	public String getHidePreviousYearTaxName() {
		return hidePreviousYearTaxName;
	}

	public void setHidePreviousYearTaxName(String hidePreviousYearTaxName) {
		this.hidePreviousYearTaxName = hidePreviousYearTaxName;
	}

	public String getHidePreviousYearTdsPercent() {
		return hidePreviousYearTdsPercent;
	}

	public void setHidePreviousYearTdsPercent(String hidePreviousYearTdsPercent) {
		this.hidePreviousYearTdsPercent = hidePreviousYearTdsPercent;
	}

	public String getProFreqId() {
		return proFreqId;
	}

	public void setProFreqId(String proFreqId) {
		this.proFreqId = proFreqId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getHidewriteOffPercent() {
		return hidewriteOffPercent;
	}

	public void setHidewriteOffPercent(String hidewriteOffPercent) {
		this.hidewriteOffPercent = hidewriteOffPercent;
	}

	public String getDeductionType() {
		return deductionType;
	}

	public void setDeductionType(String deductionType) {
		this.deductionType = deductionType;
	}

	public String getWriteOffType() {
		return writeOffType;
	}

	public void setWriteOffType(String writeOffType) {
		this.writeOffType = writeOffType;
	}

	public String getHideTotTaxAmount() {
		return hideTotTaxAmount;
	}

	public void setHideTotTaxAmount(String hideTotTaxAmount) {
		this.hideTotTaxAmount = hideTotTaxAmount;
	}

	public String getHideTotTaxPercent() {
		return hideTotTaxPercent;
	}

	public void setHideTotTaxPercent(String hideTotTaxPercent) {
		this.hideTotTaxPercent = hideTotTaxPercent;
	}

	public String[] getStrHideTax() {
		return strHideTax;
	}

	public void setStrHideTax(String[] strHideTax) {
		this.strHideTax = strHideTax;
	}

	public String[] getStrTaxAmount() {
		return strTaxAmount;
	}

	public void setStrTaxAmount(String[] strTaxAmount) {
		this.strTaxAmount = strTaxAmount;
	}

	public String[] getStrHideTaxPercent() {
		return strHideTaxPercent;
	}

	public void setStrHideTaxPercent(String[] strHideTaxPercent) {
		this.strHideTaxPercent = strHideTaxPercent;
	}

	public String[] getStrHideParticulars() {
		return strHideParticulars;
	}

	public void setStrHideParticulars(String[] strHideParticulars) {
		this.strHideParticulars = strHideParticulars;
	}

	public String[] getStrParticularsAmount() {
		return strParticularsAmount;
	}

	public void setStrParticularsAmount(String[] strParticularsAmount) {
		this.strParticularsAmount = strParticularsAmount;
	}

	public String[] getStrHideParticularsPercent() {
		return strHideParticularsPercent;
	}

	public void setStrHideParticularsPercent(String[] strHideParticularsPercent) {
		this.strHideParticularsPercent = strHideParticularsPercent;
	}

	public String[] getStrHideOPE() {
		return strHideOPE;
	}

	public void setStrHideOPE(String[] strHideOPE) {
		this.strHideOPE = strHideOPE;
	}

	public String[] getStrOPEAmount() {
		return strOPEAmount;
	}

	public void setStrOPEAmount(String[] strOPEAmount) {
		this.strOPEAmount = strOPEAmount;
	}

	public String[] getStrHideOPEPercent() {
		return strHideOPEPercent;
	}

	public void setStrHideOPEPercent(String[] strHideOPEPercent) {
		this.strHideOPEPercent = strHideOPEPercent;
	}

	public String[] getHideTaxName() {
		return hideTaxName;
	}

	public void setHideTaxName(String[] hideTaxName) {
		this.hideTaxName = hideTaxName;
	}

	public String[] getHideTaxAmount() {
		return hideTaxAmount;
	}

	public void setHideTaxAmount(String[] hideTaxAmount) {
		this.hideTaxAmount = hideTaxAmount;
	}

	public String[] getHideTaxPercent() {
		return hideTaxPercent;
	}

	public void setHideTaxPercent(String[] hideTaxPercent) {
		this.hideTaxPercent = hideTaxPercent;
	}

	public String[] getHidewoOPE() {
		return hidewoOPE;
	}

	public void setHidewoOPE(String[] hidewoOPE) {
		this.hidewoOPE = hidewoOPE;
	}

	public String[] getHidewoOPEAmount() {
		return hidewoOPEAmount;
	}

	public void setHidewoOPEAmount(String[] hidewoOPEAmount) {
		this.hidewoOPEAmount = hidewoOPEAmount;
	}

	public String[] getHidewoOPEPercent() {
		return hidewoOPEPercent;
	}

	public void setHidewoOPEPercent(String[] hidewoOPEPercent) {
		this.hidewoOPEPercent = hidewoOPEPercent;
	}

	public String[] getHidewoParti() {
		return hidewoParti;
	}

	public void setHidewoParti(String[] hidewoParti) {
		this.hidewoParti = hidewoParti;
	}

	public String[] getHidewoPartiAmount() {
		return hidewoPartiAmount;
	}

	public void setHidewoPartiAmount(String[] hidewoPartiAmount) {
		this.hidewoPartiAmount = hidewoPartiAmount;
	}

	public String[] getHidewoPartiPercent() {
		return hidewoPartiPercent;
	}

	public void setHidewoPartiPercent(String[] hidewoPartiPercent) {
		this.hidewoPartiPercent = hidewoPartiPercent;
	}

	public String[] getHidewoTax() {
		return hidewoTax;
	}

	public void setHidewoTax(String[] hidewoTax) {
		this.hidewoTax = hidewoTax;
	}

	public String[] getHidewoTaxAmount() {
		return hidewoTaxAmount;
	}

	public void setHidewoTaxAmount(String[] hidewoTaxAmount) {
		this.hidewoTaxAmount = hidewoTaxAmount;
	}

	public String[] getHidewoTaxPercent() {
		return hidewoTaxPercent;
	}

	public void setHidewoTaxPercent(String[] hidewoTaxPercent) {
		this.hidewoTaxPercent = hidewoTaxPercent;
	}

	public String getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	public String getPro_freq_id() {
		return pro_freq_id;
	}

	public void setPro_freq_id(String pro_freq_id) {
		this.pro_freq_id = pro_freq_id;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	
}
