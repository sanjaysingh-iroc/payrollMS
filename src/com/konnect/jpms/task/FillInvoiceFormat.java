package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillInvoiceFormat  implements IStatements{

	String invoiceFormatName;
	String invoiceFormatId;
	
	
	
	public FillInvoiceFormat(String invoiceFormatId, String invoiceFormatName) {
		this.invoiceFormatId = invoiceFormatId;
		this.invoiceFormatName = invoiceFormatName;
	}
	
	HttpServletRequest request;
	public FillInvoiceFormat(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillInvoiceFormat() {
	}
	
	
	public List<FillInvoiceFormat> fillFillInvoiceFormat() {
		
		List<FillInvoiceFormat> al = new ArrayList<FillInvoiceFormat>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select invoice_formats_id,invoice_format_name from invoice_formats where is_delete = false");
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillInvoiceFormat(rs.getString("invoice_formats_id"), rs.getString("invoice_format_name")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}
	
	
	public List<FillInvoiceFormat> fillFillInvoiceFormatWithDefault() {
		
		List<FillInvoiceFormat> al = new ArrayList<FillInvoiceFormat>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select invoice_formats_id,invoice_format_name from invoice_formats where is_delete = false");
			rs = pst.executeQuery();
			al.add(new FillInvoiceFormat("0", "Tabular"));
			while(rs.next()) {
				al.add(new FillInvoiceFormat(rs.getString("invoice_formats_id"), rs.getString("invoice_format_name")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}
	
	
	public List<FillInvoiceFormat> fillFillInvoiceFormatWithoutDefault() {
		
		List<FillInvoiceFormat> al = new ArrayList<FillInvoiceFormat>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select invoice_formats_id,invoice_format_name from invoice_formats where is_delete = false");
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillInvoiceFormat(rs.getString("invoice_formats_id"), rs.getString("invoice_format_name")));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}


	public String getInvoiceFormatName() {
		return invoiceFormatName;
	}

	public void setInvoiceFormatName(String invoiceFormatName) {
		this.invoiceFormatName = invoiceFormatName;
	}

	public String getInvoiceFormatId() {
		return invoiceFormatId;
	}

	public void setInvoiceFormatId(String invoiceFormatId) {
		this.invoiceFormatId = invoiceFormatId;
	}
	
}
