package com.konnect.jpms.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.recruitment.RequirementApproval;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrgServiceList extends ActionSupport implements ServletRequestAware{

	
	List<FillServices> serviceList;
	String type;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
	
		UtilityFunctions uF = new UtilityFunctions(); 
		String strOId = request.getParameter("OID");
		serviceList = new FillServices(request).fillServices(strOId, uF);
		
		return SUCCESS;
		
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public List<FillServices> getServiceList() {
		return serviceList;
	}
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
