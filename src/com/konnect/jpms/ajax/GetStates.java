package com.konnect.jpms.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillState;
import com.opensymphony.xwork2.ActionSupport;

public class GetStates extends ActionSupport implements ServletRequestAware {
	
	private String country;
	String type;
	String validReq;
	private List<FillState> stateList = null;
	String strClientState;
	String state;
	String stateTmp;
	
	public String execute() throws Exception {

		if (getCountry() != null && !getCountry().equals("0")) {
			stateList = new ArrayList<FillState>();
			stateList = new FillState(request).fillState(getCountry());
		
//			System.out.println("validReq ===>" + validReq);
//			System.out.println("getType() ===>"+getType());
//			System.out.println("stateList== 0 ===>"+stateList);
			return SUCCESS;
		} else {
			stateList = new ArrayList<FillState>();
			stateList = new FillState(request).fillState();
			
//			System.out.println("stateList== 1 ===>"+stateList);
			
			return SUCCESS;
		}
		
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getValidReq() {
		return validReq;
	}

	public void setValidReq(String validReq) {
		this.validReq = validReq;
	}

	public String getStrClientState() {
		return strClientState;
	}

	public void setStrClientState(String strClientState) {
		this.strClientState = strClientState;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateTmp() {
		return stateTmp;
	}

	public void setStateTmp(String stateTmp) {
		this.stateTmp = stateTmp;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
}
