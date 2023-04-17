package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteGeneratedProjectInvoice extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strSessionEmpId;
	
	String invoiceId;
	String proId; 
	String type;
	
	String proType;
	
	public String execute() {
	
		session = request.getSession();		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
//		System.out.println("invoiceId ====>> " + invoiceId);
//		System.out.println("getType() ====>> " + getType());
		if(getType() != null && getType().equals("D")) {
			deleteGeneratedProjectInvoice();
			return SUCCESS;
		} else if(getType() != null && getType().equals("C")) {
			cancelGeneratedProjectInvoice();
			return "CSUCCESS";
		}
		return SUCCESS;
	}

	private void cancelGeneratedProjectInvoice() {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null, pst1 = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null, rs1 = null;
		
		try {
			
			con = db.makeConnection(con);
			
			pst1 = con.prepareStatement("update promntc_invoice_details set invoice_cancel = true where promntc_invoice_id=? and pro_id=?");
			pst1.setInt(1, uF.parseToInt(getInvoiceId()));
			pst1.setInt(2, uF.parseToInt(getProId()));
			pst1.executeUpdate();
			pst1.close();
			
			pst1 = con.prepareStatement("select * from promntc_invoice_details where promntc_invoice_id=? and pro_id=?");
			pst1.setInt(1, uF.parseToInt(getInvoiceId()));
			pst1.setInt(2, uF.parseToInt(getProId()));
			rs = pst1.executeQuery();
			while(rs.next()) {
				pst = con.prepareStatement("insert into promntc_invoice_details(invoice_generated_date,invoice_generated_by,invoice_from_date," +
						"invoice_to_date,invoice_paycycle,pro_id,invoice_code,project_description,other_description,spoc_id,address_id,pro_owner_id," +
						"financial_start_date,financial_end_date,wlocation_id,depart_id,particulars_total_amount,invoice_amount,curr_id,is_cancel," +
						"other_amount,other_particular,bank_branch_id,invoice_type,client_id,service_id,bill_type,standard_tax,education_tax," +
						"service_tax,reference_no_desc,oc_particulars_total_amount,oc_invoice_amount,oc_other_amount,adhoc_billing_type,invoice_cancel)" +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, uF.parseToInt(strSessionEmpId));
				pst.setDate(3, uF.getDateFormat(rs.getString("invoice_from_date"), DBDATE));
				pst.setDate(4, uF.getDateFormat(rs.getString("invoice_to_date"), DBDATE));
				pst.setInt(5, uF.parseToInt(rs.getString("invoice_paycycle")));
				pst.setInt(6, uF.parseToInt(rs.getString("pro_id")));
				pst.setString(7, rs.getString("invoice_code"));
				pst.setString(8, rs.getString("project_description"));
				pst.setString(9, rs.getString("other_description"));
				pst.setInt(10, uF.parseToInt(rs.getString("spoc_id")));
				pst.setInt(11, uF.parseToInt(rs.getString("address_id")));
				pst.setInt(12, uF.parseToInt(rs.getString("pro_owner_id")));
				pst.setDate(13, uF.getDateFormat(rs.getString("financial_start_date"), DBDATE));
				pst.setDate(14, uF.getDateFormat(rs.getString("financial_end_date"), DBDATE));
				pst.setInt(15, uF.parseToInt(rs.getString("wlocation_id")));
				pst.setInt(16, uF.parseToInt(rs.getString("depart_id")));
				pst.setDouble(17, (0 - rs.getDouble("particulars_total_amount")));
				pst.setDouble(18, (0 - rs.getDouble("invoice_amount")));
				pst.setInt(19, uF.parseToInt(rs.getString("curr_id")));
				pst.setBoolean(20, true);
				pst.setDouble(21, (0 - rs.getDouble("other_amount")));
				pst.setString(22, rs.getString("other_particular"));
				pst.setInt(23, uF.parseToInt(rs.getString("bank_branch_id")));
				pst.setInt(24, uF.parseToInt(rs.getString("invoice_type")));
				pst.setInt(25, uF.parseToInt(rs.getString("client_id")));
				pst.setInt(26, uF.parseToInt(rs.getString("service_id")));
				pst.setString(27, rs.getString("bill_type"));
				pst.setDouble(28, rs.getDouble("standard_tax"));
				pst.setDouble(29, rs.getDouble("education_tax"));
				pst.setDouble(30, rs.getDouble("service_tax"));
				pst.setString(31, rs.getString("reference_no_desc"));
				pst.setDouble(32, (0 - rs.getDouble("oc_particulars_total_amount")));
				pst.setDouble(33, (0 - rs.getDouble("oc_invoice_amount")));
				pst.setDouble(34, (0 - rs.getDouble("oc_other_amount")));
				pst.setInt(35, uF.parseToInt(rs.getString("adhoc_billing_type")));
				pst.setBoolean(36, true);
				pst.execute();
				pst.close();
			}
			rs.close();
			pst1.close();
			
			
			String newInvoiceId = null;
			pst = con.prepareStatement("select max(promntc_invoice_id) as promntc_invoice_id from promntc_invoice_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				newInvoiceId = rs.getString("promntc_invoice_id");
			}
			rs.close();
			pst.close();
			
			pst1 = con.prepareStatement("select * from promntc_invoice_amt_details where promntc_invoice_id=?");
			pst1.setInt(1, uF.parseToInt(getInvoiceId()));
			rs = pst1.executeQuery();
			while(rs.next()) {
				pst = con.prepareStatement("insert into promntc_invoice_amt_details(invoice_particulars,invoice_particulars_amount,promntc_invoice_id," +
						"emp_id,days_hours,_rate,resource_name,day_or_hour,oc_invoice_particulars_amount,task_id,head_type,tax_percent)" +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst.setString(1, rs.getString("invoice_particulars"));
				pst.setDouble(2, (0 - rs.getDouble("invoice_particulars_amount")));
				pst.setInt(3, uF.parseToInt(newInvoiceId));
				pst.setInt(4, uF.parseToInt(rs.getString("emp_id")));
				pst.setDouble(5, rs.getDouble("days_hours"));
				pst.setDouble(6, rs.getDouble("_rate"));
				pst.setString(7, rs.getString("resource_name"));
				pst.setInt(8, uF.parseToInt(rs.getString("day_or_hour")));
				pst.setDouble(9, (0 - rs.getDouble("oc_invoice_particulars_amount")));
				pst.setInt(10, uF.parseToInt(rs.getString("task_id")));
				pst.setString(11, rs.getString("head_type"));
				pst.setDouble(12, rs.getDouble("tax_percent"));
				pst.execute();
				pst.close();
			}
			rs.close();
			pst1.close();
			
//			System.out.println("pst ====>"+pst);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void deleteGeneratedProjectInvoice() {
		
		Database db = new Database();
		db.setRequest(request);
		PreparedStatement pst = null;
		Connection con = null;
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			
			String invoiceCode = null;
			pst = con.prepareStatement("select invoice_code from promntc_invoice_details where promntc_invoice_id=? and pro_id=?");
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
			pst.setInt(2, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				invoiceCode = rs.getString("invoice_code");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("delete from  promntc_invoice_details where promntc_invoice_id=? and pro_id=?");
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
			pst.setInt(2, uF.parseToInt(getProId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from  promntc_invoice_amt_details where promntc_invoice_id=?");
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
			pst.execute();
			pst.close();
			
			String otherInvoiceId = null;
			pst = con.prepareStatement("select promntc_invoice_id from promntc_invoice_details where invoice_code=? and pro_id=?");
			pst.setString(1, invoiceCode);
			pst.setInt(2, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				otherInvoiceId = rs.getString("promntc_invoice_id");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("delete from  promntc_invoice_details where promntc_invoice_id=? and pro_id=?");
			pst.setInt(1, uF.parseToInt(otherInvoiceId));
			pst.setInt(2, uF.parseToInt(getProId()));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from  promntc_invoice_amt_details where promntc_invoice_id=?");
			pst.setInt(1, uF.parseToInt(otherInvoiceId));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from communication_1 where align_with_id=? and align_with = ?");
			pst.setInt(1, uF.parseToInt(getInvoiceId()));
			pst.setInt(2, INVOICE);
			pst.execute();
			pst.close();
			
			request.setAttribute("STATUS_MSG", "<b><font color=\"red\">This invoice is deleted</font></b>");
			
		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "<b><font color=\"orange\">This invoice is not deleted. Pls try again</font></b>");
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	
}
