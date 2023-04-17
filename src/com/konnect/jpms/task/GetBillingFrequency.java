package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

public class GetBillingFrequency extends ActionSupport implements ServletRequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String billingType;
	List<FillBillingType> billingKindList;
	String strBillingKind;
	
	public String execute() throws Exception {

//		System.out.println("getBillingType =======>>>> " + getBillingType());
		billingKindList = new FillBillingType().fillBillingKindListBillTypewise(getBillingType());
		return SUCCESS;
	}


	public String getBillingType() {
		return billingType;
	}
	
	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}
	
	public List<FillBillingType> getBillingKindList() {
		return billingKindList;
	}
	
	public void setBillingKindList(List<FillBillingType> billingKindList) {
		this.billingKindList = billingKindList;
	}

	public String getStrBillingKind() {
		return strBillingKind;
	}

	public void setStrBillingKind(String strBillingKind) {
		this.strBillingKind = strBillingKind;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}

	
}
