package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class ReceiveBill extends ActionSupport implements ServletRequestAware,IStatements 
{
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	private HttpServletRequest request;
	String strSessionEmpId;
	String strOrgId;

	String invoiceAmount;
	String paidAmount;
	List<FillPaymentSource> paymentSourceList;
	String paymentSource;
	String paymentDescription;
	String amountReceived;
	String amountDue;
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
	String invoiceId;
	String proId;
	String proFreqId;
	String clientId;
	
	String stateId; 
//	String hideOtherDeduction;
	
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
	
	String hideTotTaxAmount;
	String hideTotTaxPercent;
	
	String proType;
	
	public String execute() {
		session=request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strOrgId = (String)session.getAttribute(ORGID);
		
		UtilityFunctions uF=new UtilityFunctions();
		
//		String strInvoiceId = (String)request.getParameter("invoice_id");
//		String strProId = (String)request.getParameter("pro_id");
//		String strClientId = (String)request.getParameter("client_id");
		
		if(getSubmit()!=null) {
			insertProjectInvoice(uF);
			return SUCCESS;
		}
		
		getProjectDetails(uF);
		
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
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
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
			
			
			for(int i=0;getStrHideOPE()!=null && i<getStrHideOPE().length;i++) {
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getProjectDetails(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
//			Map<String, String> hmTaxMiscSetting = CF.getTaxMiscSetting(con);
//			request.setAttribute("hmTaxMiscSetting", hmTaxMiscSetting);
			
			pst = con.prepareStatement("select tds_percent, registration_no from client_details where client_id = ?");
			pst.setInt(1, uF.parseToInt(getClientId()));
			rs = pst.executeQuery();
			String registrationNo = null;
			double tdsPercent = 0;
			while(rs.next()) {
				tdsPercent = rs.getDouble("tds_percent");
				registrationNo = rs.getString("registration_no");
			}
			rs.close();
			pst.close();
			request.setAttribute("tdsPercent", ""+tdsPercent);
			
			pst = con.prepareStatement("select received_amount, tds_deducted, write_off_amount, exchange_rate from promntc_bill_amt_details where invoice_id=?");
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
			rs = pst.executeQuery();
			
			double dblAmtReceived = 0;
			double dblAmtTDS = 0;
			double dblAmtWritOff = 0;
			int nCurrId = 0;
			
			while(rs.next()) {
//				dblAmtReceived += uF.parseToDouble(rs.getString("received_amount")) / uF.parseToDouble(rs.getString("exchange_rate"));
//				dblAmtTDS += uF.parseToDouble(rs.getString("tds_deducted")) / uF.parseToDouble(rs.getString("exchange_rate"));
				dblAmtReceived += uF.parseToDouble(rs.getString("received_amount"));
				dblAmtTDS += uF.parseToDouble(rs.getString("tds_deducted"));
				dblAmtWritOff += uF.parseToDouble(rs.getString("write_off_amount"));
			}
			rs.close();
			pst.close();
			
			setExchangeRate("1"); // default value for currency conversion
			
			
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id=? and head_type = '"+HEAD_PARTI+"' and " +
				"promntc_invoice_amt_id not in (select distinct(parent_parti_id) as parent_parti_id from promntc_invoice_amt_details) " +
				"order by promntc_invoice_amt_id");
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
//			System.out.println("pst======main===" + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmInvoiceFixHeadData = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("promntc_invoice_amt_id"));
				alInner.add(rs.getString("invoice_particulars"));
				alInner.add(rs.getString("invoice_particulars_amount"));
				alInner.add(rs.getString("oc_invoice_particulars_amount"));
				alInner.add(rs.getString("head_type"));
				alInner.add(rs.getString("tax_percent"));
				hmInvoiceFixHeadData.put(rs.getString("promntc_invoice_amt_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmInvoiceFixHeadData", hmInvoiceFixHeadData);
//			System.out.println("hmInvoiceFixHeadData ======>> " + hmInvoiceFixHeadData);
			
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id=? and head_type = '"+HEAD_OPE+"' order by promntc_invoice_amt_id");
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
//			System.out.println("pst======main===" + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmInvoiceOPEHeadData = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("promntc_invoice_amt_id"));
				alInner.add(rs.getString("invoice_particulars"));
				alInner.add(rs.getString("invoice_particulars_amount"));
				alInner.add(rs.getString("oc_invoice_particulars_amount"));
				alInner.add(rs.getString("head_type"));
				alInner.add(rs.getString("tax_percent"));
				hmInvoiceOPEHeadData.put(rs.getString("promntc_invoice_amt_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmInvoiceOPEHeadData", hmInvoiceOPEHeadData);
//			System.out.println("hmInvoiceOPEHeadData ======>> " + hmInvoiceOPEHeadData);
			
			pst = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id=? and head_type = '"+HEAD_TAX+"' order by promntc_invoice_amt_id");
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
//			System.out.println("pst======main===" + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmInvoiceTaxHeadData = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("promntc_invoice_amt_id"));
				alInner.add(rs.getString("invoice_particulars"));
				alInner.add(rs.getString("invoice_particulars_amount"));
				alInner.add(rs.getString("oc_invoice_particulars_amount"));
				alInner.add(rs.getString("head_type"));
				alInner.add(rs.getString("tax_percent"));
				hmInvoiceTaxHeadData.put(rs.getString("promntc_invoice_amt_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmInvoiceTaxHeadData", hmInvoiceTaxHeadData);
//			System.out.println("hmInvoiceTaxHeadData ======>> " + hmInvoiceTaxHeadData);
			
			pst = con.prepareStatement("select * from project_tax_setting where pro_id=? and invoice_or_customer=2 and status=true order by pro_tax_setting_id");
			pst.setInt(1, uF.parseToInt(getProId()));
//			System.out.println("pst======main==="+pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmProTaxHeadData = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_tax_setting_id"));
				alInner.add(rs.getString("tax_name"));
				alInner.add(rs.getString("tax_percent"));
				hmProTaxHeadData.put(rs.getString("pro_tax_setting_id"), alInner);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmProTaxHeadData ======>> " + hmProTaxHeadData);
			
			String adHocType = "";
			pst = con.prepareStatement("select * from promntc_invoice_details where pro_id = ? and promntc_invoice_id = ? and pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			pst.setInt(2, uF.parseToInt(getInvoiceId()));
			pst.setInt(3, uF.parseToInt(getProFreqId()));
			rs = pst.executeQuery();
//			double OPEAmount = 0;
			while(rs.next()) {
				nCurrId = uF.parseToInt(rs.getString("curr_id"));
				setInvoiceAmount(rs.getString("oc_invoice_amount"));
				adHocType = rs.getString("adhoc_billing_type");
				
//				setProId(rs.getString("pro_id"));
//				setInvoiceId(rs.getString("promntc_invoice_id"));
//				OPEAmount = rs.getDouble("oc_other_amount");
				
				double dblBalance = uF.parseToDouble(rs.getString("oc_invoice_amount")) - (dblAmtReceived + dblAmtTDS + dblAmtWritOff);
//				setBalanceAmount(uF.formatIntoOneDecimalWithOutComma(dblBalance));
				setAmountDue(uF.formatIntoTwoDecimalWithOutComma(dblBalance));
				setAmountReceived(uF.formatIntoTwoDecimalWithOutComma(dblBalance));
				setCurrId(rs.getString("curr_id"));
			}
			rs.close();
			pst.close();
			
			if(adHocType != null && uF.parseToInt(adHocType) > 0) {
				pst = con.prepareStatement("select * from tax_setting where org_id=? and invoice_or_customer=2 order by tax_setting_id");
				pst.setInt(1, uF.parseToInt(strOrgId));
	//			System.out.println("pst======main==="+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<String> alInner = new ArrayList<String>();
					alInner.add(rs.getString("tax_setting_id"));
					alInner.add(rs.getString("tax_name"));
					alInner.add(rs.getString("tax_percent"));
					hmProTaxHeadData.put(rs.getString("tax_setting_id"), alInner);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmProTaxHeadData", hmProTaxHeadData);
			
//			request.setAttribute("OPEAmount", ""+OPEAmount);
			
			pst = con.prepareStatement("select wlocation_state_id from projectmntnc pm,work_location_info wi where wi.wlocation_id = pm.wlocation_id  and pro_id = ?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setStateId(rs.getString("wlocation_state_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmCurr = hmCurrencyMap.get(nCurrId+"");
			request.setAttribute("hmCurr", hmCurr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
    
	private void loadProjectInvoice(UtilityFunctions uF) {

		paymentSourceList = new FillPaymentSource().fillPaymentMode();
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

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
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

	public String getWriteOffDescription() {
		return writeOffDescription;
	}

	public void setWriteOffDescription(String writeOffDescription) {
		this.writeOffDescription = writeOffDescription;
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


	public String getHidePreviousYearTdsPercent() {
		return hidePreviousYearTdsPercent;
	}


	public void setHidePreviousYearTdsPercent(String hidePreviousYearTdsPercent) {
		this.hidePreviousYearTdsPercent = hidePreviousYearTdsPercent;
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

	public String getHidePreviousYearTaxName() {
		return hidePreviousYearTaxName;
	}

	public void setHidePreviousYearTaxName(String hidePreviousYearTaxName) {
		this.hidePreviousYearTaxName = hidePreviousYearTaxName;
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

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

}
