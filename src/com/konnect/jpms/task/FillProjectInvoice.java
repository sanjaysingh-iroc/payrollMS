package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillProjectInvoice implements IStatements {

	String invoiceId;
	String invoiceCode;
	
	private FillProjectInvoice(String invoiceId, String invoiceCode) {
		this.invoiceId = invoiceId;
		this.invoiceCode = invoiceCode;
	}
	
	HttpServletRequest request;
	public FillProjectInvoice(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillProjectInvoice() {
	}
	
	
	public List<FillProjectInvoice> fillProjectInvoices() {
		
		List<FillProjectInvoice> al = new ArrayList<FillProjectInvoice>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from promntc_invoice_details");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillProjectInvoice(rs.getString("promntc_invoice_id"), rs.getString("invoice_code")));				
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	
	public List<FillProjectInvoice> fillProjectWiseInvoices(String strProIds) {
		
		List<FillProjectInvoice> al = new ArrayList<FillProjectInvoice>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(strProIds != null && strProIds.length()>0) {
				pst = con.prepareStatement("select * from promntc_invoice_details where pro_id in ("+strProIds+")");
				rs = pst.executeQuery();
				while(rs.next()) {
					al.add(new FillProjectInvoice(rs.getString("promntc_invoice_id"), rs.getString("invoice_code")));				
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
	
}  
