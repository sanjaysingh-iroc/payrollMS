package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddReceiptDetails extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId = null;
	
	List<FillClients> clientList;
	List<FillBank> bankList;
	List<FillBillingNumber> billDetailsList;
	
	String strClient;
	String bankName;
	String receiptNo;
	String receiptDate;
	String totalAmtReceived;
	String totalProfFees;
	String totalOPE;
	String totCGSTAmt;
	String totSGSTAmt;
	String totIGSTAmt;
	String strTDS;
	String billingId;
	String[] strBillId;
	String[] billAmt;
	String[] billOSAmt;
	String[] receivedAmt;
	String[] profFeesAmt;
	String[] opeAmt;
	String[] strCGST;
	String[] strSGST;
	String[] strIGST;
	String operation;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/task/AddReceiptDetails.jsp");
		request.setAttribute(TITLE, "Add Receipt Details");
		
		clientList = new FillClients(request).fillClients(false);
		bankList = new FillBank(request).fillBankAccNo();
		billDetailsList = new FillBillingNumber(request).fillBillDetailsByCustomer(uF.parseToInt(getStrClient()));
		
		if(getOperation() != null && getOperation().equals("A")){
			insertReceiptDetails(uF);
			return SUCCESS;
		} 
		
		return LOAD;
	}
	
	
	
	private void insertReceiptDetails(UtilityFunctions uF) {
//		System.out.println("insert");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			double totBillAmt = 0;
			double totBalanceAmt = 0;
			StringBuilder invoiceIds = new StringBuilder();
			
			if(getStrBillId() != null && getStrBillId().length != 0){
				
				invoiceIds.append(",");
				for(int i=0; i<getStrBillId().length; i++){
					totBillAmt += uF.parseToDouble(getBillAmt()[i]);
					totBalanceAmt += uF.parseToDouble(getBillOSAmt()[i]);
					invoiceIds.append(getStrBillId()[i]+",");
				}
				
				pst = con.prepareStatement("insert into promntc_bill_amt_details (invoice_amount, received_amount, " +
						"received_by, balance_amount,entry_date,bill_no,professional_fees,ope_amount,invoice_ids,sgst_amount,cgst_amount,igst_amount,bank_id,exchange_rate) " +
						"values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				pst.setDouble(1, totBillAmt);
				pst.setDouble(2, uF.parseToDouble(getTotalAmtReceived()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDouble(4, totBalanceAmt);
				pst.setDate(5, uF.getDateFormat(getReceiptDate(), DATE_FORMAT));
				pst.setString(6, getReceiptNo());
				pst.setDouble(7, uF.parseToDouble(getTotalProfFees()));
				pst.setDouble(8, uF.parseToDouble(getTotalOPE()));
				pst.setString(9, invoiceIds.toString());
				pst.setDouble(10, uF.parseToDouble(getTotSGSTAmt()));
				pst.setDouble(11, uF.parseToDouble(getTotCGSTAmt()));
				pst.setDouble(12, uF.parseToDouble(getTotIGSTAmt()));
				pst.setInt(13, uF.parseToInt(getBankName()));
				pst.setDouble(14, 1);
//				System.out.println("ARD/127--pst="+pst);
				pst.executeUpdate();
				pst.close();
				
				int newBillId = 0;
				pst = con.prepareStatement("select max(bill_id) as bill_id from promntc_bill_amt_details");
				rs = pst.executeQuery();
				while (rs.next()) {
					newBillId = rs.getInt("bill_id");
				}
				rs.close();
				pst.close();
				
				for(int j=0; getProfFeesAmt()!=null && j<getProfFeesAmt().length; j++){
					pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
										"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,invoice_id) values(?,?,?,?, ?,?)");
					pst.setString(1, PROFESSIONAL_FEES);
					pst.setDouble(2, uF.parseToDouble(getProfFeesAmt()[j]));
					pst.setInt(3, newBillId);
					pst.setDouble(4, uF.parseToDouble(getProfFeesAmt()[j]));
					pst.setString(5, HEAD_PARTI);
					pst.setInt(6, uF.parseToInt(getStrBillId()[j]));
					pst.executeUpdate();
					pst.close();
				}
				
				for(int k=0; getOpeAmt()!=null && k<getOpeAmt().length; k++){
					pst = con.prepareStatement("insert into promntc_bill_parti_amt_details(bill_particulars,bill_particulars_amount," +
										"promntc_invoice_bill_id,oc_bill_particulars_amount,head_type,invoice_id) values(?,?,?,?, ?,?)");
					pst.setString(1, OUT_OF_POCKET_EXPENSES);
					pst.setDouble(2, uF.parseToDouble(getOpeAmt()[k]));
					pst.setInt(3, newBillId);
					pst.setDouble(4, uF.parseToDouble(getOpeAmt()[k]));
					pst.setString(5, HEAD_OPE);
					pst.setInt(6, uF.parseToInt(getStrBillId()[k]));
					pst.executeUpdate();
					pst.setInt(6, uF.parseToInt(getStrBillId()[k]));
					pst.close();
				}
			}
			
			request.setAttribute(MESSAGE, SUCCESSM+ "Receipt added successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getStrClient() {
		return strClient;
	}
	
	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public List<FillBank> getBankList() {
		return bankList;
	}

	public void setBankList(List<FillBank> bankList) {
		this.bankList = bankList;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getTotalAmtReceived() {
		return totalAmtReceived;
	}

	public void setTotalAmtReceived(String totalAmtReceived) {
		this.totalAmtReceived = totalAmtReceived;
	}

	public String getTotalOPE() {
		return totalOPE;
	}

	public void setTotalOPE(String totalOPE) {
		this.totalOPE = totalOPE;
	}

	public String getStrTDS() {
		return strTDS;
	}

	public void setStrTDS(String strTDS) {
		this.strTDS = strTDS;
	}

	public String getTotalProfFees() {
		return totalProfFees;
	}

	public void setTotalProfFees(String totalProfFees) {
		this.totalProfFees = totalProfFees;
	}

	public List<FillBillingNumber> getBillDetailsList() {
		return billDetailsList;
	}

	public void setBillDetailsList(List<FillBillingNumber> billDetailsList) {
		this.billDetailsList = billDetailsList;
	}

	public String getBillingId() {
		return billingId;
	}

	public void setBillingId(String billingId) {
		this.billingId = billingId;
	}

	public String[] getStrBillId() {
		return strBillId;
	}

	public void setStrBillId(String[] strBillId) {
		this.strBillId = strBillId;
	}

	public String[] getBillAmt() {
		return billAmt;
	}

	public void setBillAmt(String[] billAmt) {
		this.billAmt = billAmt;
	}

	public String[] getBillOSAmt() {
		return billOSAmt;
	}

	public void setBillOSAmt(String[] billOSAmt) {
		this.billOSAmt = billOSAmt;
	}

	public String[] getReceivedAmt() {
		return receivedAmt;
	}

	public void setReceivedAmt(String[] receivedAmt) {
		this.receivedAmt = receivedAmt;
	}

	public String[] getProfFeesAmt() {
		return profFeesAmt;
	}

	public void setProfFeesAmt(String[] profFeesAmt) {
		this.profFeesAmt = profFeesAmt;
	}

	public String[] getOpeAmt() {
		return opeAmt;
	}

	public void setOpeAmt(String[] opeAmt) {
		this.opeAmt = opeAmt;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getTotCGSTAmt() {
		return totCGSTAmt;
	}

	public void setTotCGSTAmt(String totCGSTAmt) {
		this.totCGSTAmt = totCGSTAmt;
	}

	public String getTotSGSTAmt() {
		return totSGSTAmt;
	}

	public void setTotSGSTAmt(String totSGSTAmt) {
		this.totSGSTAmt = totSGSTAmt;
	}

	public String getTotIGSTAmt() {
		return totIGSTAmt;
	}

	public void setTotIGSTAmt(String totIGSTAmt) {
		this.totIGSTAmt = totIGSTAmt;
	}

	public String[] getStrCGST() {
		return strCGST;
	}

	public void setStrCGST(String[] strCGST) {
		this.strCGST = strCGST;
	}

	public String[] getStrSGST() {
		return strSGST;
	}

	public void setStrSGST(String[] strSGST) {
		this.strSGST = strSGST;
	}

	public String[] getStrIGST() {
		return strIGST;
	}

	public void setStrIGST(String[] strIGST) {
		this.strIGST = strIGST;
	}
	
}
