package com.konnect.jpms.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectPartialInvoicePopup extends ActionSupport implements ServletRequestAware, IStatements {
   
	/**
	 *
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;
	String divid; 
	
	String proOPEAmt;
	String pro_freq_id;
	String pro_id;
	String invoice_format_id;
	String pro_amount;
	String invoice_amount;
	String balancePercentage;
	
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		

		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF=new UtilityFunctions();
		
		double remainAmt=uF.parseToDouble(getPro_amount())-uF.parseToDouble(getInvoice_amount());
		double percentage = 0.0d;
		if(uF.parseToDouble(getPro_amount()) > 0) {
			percentage = (uF.parseToDouble(getInvoice_amount())/uF.parseToDouble(getPro_amount()))*100;
			percentage=100-percentage;
		}
		setBalancePercentage(uF.formatIntoTwoDecimalWithOutComma(percentage));
		request.setAttribute("remainAmt", ""+remainAmt);
//		System.out.println("getInvoice_format_id() ===>> " +getInvoice_format_id());
		
		return LOAD;

	}	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getDivid() {
		return divid;
	}

	public void setDivid(String divid) {
		this.divid = divid;
	}

	public String getPro_id() {
		return pro_id;
	}

	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}

	public String getPro_amount() {
		return pro_amount;
	}

	public void setPro_amount(String pro_amount) {
		this.pro_amount = pro_amount;
	}

	public String getInvoice_amount() {
		return invoice_amount;
	}

	public void setInvoice_amount(String invoice_amount) {
		this.invoice_amount = invoice_amount;
	}

	public String getBalancePercentage() {
		return balancePercentage;
	}

	public void setBalancePercentage(String balancePercentage) {
		this.balancePercentage = balancePercentage;
	}

	public String getInvoice_format_id() {
		return invoice_format_id;
	}

	public void setInvoice_format_id(String invoice_format_id) {
		this.invoice_format_id = invoice_format_id;
	}

	public String getPro_freq_id() {
		return pro_freq_id;
	}

	public void setPro_freq_id(String pro_freq_id) {
		this.pro_freq_id = pro_freq_id;
	}

	public String getProOPEAmt() {
		return proOPEAmt;
	}

	public void setProOPEAmt(String proOPEAmt) {
		this.proOPEAmt = proOPEAmt;
	}

}
