package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class FillBillingNumber {

	String billID;
	String billNumber;
	
	public FillBillingNumber(String billID, String billNumber) {
		this.billID = billID;
		this.billNumber = billNumber;
	}
	
	HttpServletRequest request;
	public FillBillingNumber(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillBillingNumber() {
		
	}
	
	public List<FillBillingNumber> fillBillDetailsByCustomer(int clientId) {
		
		List<FillBillingNumber> al = new ArrayList<FillBillingNumber>();
		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			/*pst = con.prepareStatement("select promntc_invoice_id,invoice_code from promntc_invoice_details where client_id=? order by promntc_invoice_id desc");*/
//			pst = con.prepareStatement("select * from promntc_invoice_details where client_id=? order by promntc_invoice_id desc");
			/*pst = con.prepareStatement("select pid.promntc_invoice_id,pid.invoice_code,pid.invoice_amount, SUM(pbad.received_amount) as received_amount  from promntc_invoice_details pid "+
								" INNER JOIN promntc_bill_amt_details pbad ON pbad.invoice_id = pid.promntc_invoice_id"+
								" where client_id=? GROUP BY pid.promntc_invoice_id  order by promntc_invoice_id desc");
			pst.setInt(1, clientId);
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillBillingNumber(rs.getString("promntc_invoice_id"), rs.getString("invoice_code")+","+rs.getString("invoice_amount")+","+rs.getString("received_amount")));
//				al.add(new FillBillingNumber(rs.getString("promntc_invoice_id"), rs.getString("invoice_code")+","+rs.getString("invoice_amount")));
			}	
			rs.close();
			pst.close();*/
			
			Map<String, String> invoiceDetails = new HashMap<String, String>();
			List<String> inviceIds = new ArrayList<String>();
			pst = con.prepareStatement("select promntc_invoice_id,invoice_code,invoice_amount from promntc_invoice_details where client_id=? order by promntc_invoice_id desc");
			pst.setInt(1, clientId);
			rs = pst.executeQuery();
			StringBuilder sbInvoiceId = null;
			while(rs.next()) {
				inviceIds.add(rs.getString("promntc_invoice_id"));
				if(sbInvoiceId == null){
					sbInvoiceId = new StringBuilder();
					sbInvoiceId.append(rs.getString("promntc_invoice_id"));
				}else{
					sbInvoiceId.append(","+rs.getString("promntc_invoice_id"));
				}
				
				invoiceDetails.put(rs.getString("promntc_invoice_id")+"_CODE", rs.getString("invoice_code"));
				invoiceDetails.put(rs.getString("promntc_invoice_id")+"_INV_AMT", rs.getString("invoice_amount"));
			}	
			rs.close();
			pst.close();
			
			if(inviceIds!=null && !inviceIds.isEmpty()){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select invoice_ids, received_amount from promntc_bill_amt_details where ");
				for(int i=0; inviceIds!=null && !inviceIds.isEmpty() && i<inviceIds.size(); i++){
					sbQuery.append(" invoice_ids like '%,"+inviceIds.get(i)+",%'");
					if(i<inviceIds.size()-1){
						sbQuery.append(" OR ");
					}
				}
				
				pst2 = con.prepareStatement(sbQuery.toString());
				rs2 = pst2.executeQuery();
				while(rs2.next()) {
					
					String[] strInvId = rs2.getString("invoice_ids").split(",");
					
					for(int j=0; strInvId!=null && j<strInvId.length; j++){
						if(j>=1){
							double recvAmt = uF.parseToDouble(invoiceDetails.get(strInvId[j]+"_RECV_AMT"))+uF.parseToDouble(rs2.getString("received_amount"));
							invoiceDetails.put(strInvId[j]+"_RECV_AMT", recvAmt+"");
						}
					}
				}	
				rs2.close();
				pst2.close();
			}
			
			
			for(int i=0; inviceIds!=null && !inviceIds.isEmpty() && i<inviceIds.size(); i++){
				/*pst2 = con.prepareStatement("select sum(received_amount) as received_amount from promntc_bill_amt_details where invoice_ids like '%"+inviceIds.get(i)+"%'");
				//System.out.println("pst2=="+pst2);
				rs2 = pst2.executeQuery();
				while(rs2.next()) {
					invoiceDetails.put(inviceIds.get(i)+"_RECV_AMT", rs2.getString("received_amount"));
				}	
				rs2.close();
				pst2.close();*/
				
				al.add(new FillBillingNumber(inviceIds.get(i), invoiceDetails.get(inviceIds.get(i)+"_CODE")+","+invoiceDetails.get(inviceIds.get(i)+"_INV_AMT")+","+invoiceDetails.get(inviceIds.get(i)+"_RECV_AMT")));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}

	public String getBillID() {
		return billID;
	}

	public void setBillID(String billID) {
		this.billID = billID;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	
	
}
