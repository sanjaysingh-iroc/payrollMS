package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

public class GetBillingHeadOtherVariable extends ActionSupport implements ServletRequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<FillBillingHeads> billingHeadOtherVariableList;
	String count;
	String billingHeadDataType;
	
	public String execute() throws Exception {

//		System.out.println("getBillingHeadDataType =======>>>> " + getBillingHeadDataType());
		billingHeadOtherVariableList = new FillBillingHeads().fillBillingHeadOtherVariableListBillingTypewise(getBillingHeadDataType());
		StringBuilder sbBHOtherVariable = new StringBuilder();
		for(FillBillingHeads fillBillingHeadOtherVariableList: billingHeadOtherVariableList) {
			sbBHOtherVariable.append("<option value=\""+fillBillingHeadOtherVariableList.getHeadId()+"\">"+fillBillingHeadOtherVariableList.getHeadName()+"</option>");
		}
		request.setAttribute("sbBHOtherVariable", sbBHOtherVariable.toString());
		
		return SUCCESS;
	}


	public List<FillBillingHeads> getBillingHeadOtherVariableList() {
		return billingHeadOtherVariableList;
	}
	
	public void setBillingHeadOtherVariableList(List<FillBillingHeads> billingHeadOtherVariableList) {
		this.billingHeadOtherVariableList = billingHeadOtherVariableList;
	}
	
	public String getCount() {
		return count;
	}
	
	public void setCount(String count) {
		this.count = count;
	}

	public String getBillingHeadDataType() {
		return billingHeadDataType;
	}

	public void setBillingHeadDataType(String billingHeadDataType) {
		this.billingHeadDataType = billingHeadDataType;
	}


	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}

	
}
