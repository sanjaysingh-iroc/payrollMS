package com.konnect.jpms.ajax;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillState;
import com.opensymphony.xwork2.ActionSupport;

public class GetStateDetails extends ActionSupport implements ServletRequestAware{
	
	private static final long serialVersionUID = 1L;
	
	private String country_id;
	private String type;
	private List<FillState> stateList;
	
	public String execute() {
		
//		System.out.println("type ===> "+type);
		stateList= new FillState(request).fillState(getCountry_id());
//		System.out.println("stateList ===> "+stateList);
		return SUCCESS;
		
	}


	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}


	public String getCountry_id() {
		return country_id;
	}


	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}


	public List<FillState> getStateList() {
		return stateList;
	}


	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

}
