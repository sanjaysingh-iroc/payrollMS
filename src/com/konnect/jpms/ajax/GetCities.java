package com.konnect.jpms.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCity;
import com.opensymphony.xwork2.ActionSupport;

public class GetCities extends ActionSupport implements ServletRequestAware{
	private String state;
	private List<FillCity> cityList = null;

	public String execute() throws Exception {
		  
		if (getState() != null && !getState().equals("0")) {
			cityList = new ArrayList<FillCity>();
			cityList = new FillCity(request).fillCity(getState());
			
			return SUCCESS;
		} else {
			cityList = new ArrayList<FillCity>();
			cityList = new FillCity(request).fillCity();
			
			return SUCCESS;
		}
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<FillCity> getCityList() {
		return cityList;
	}

	public void setCityList(List<FillCity> cityList) {
		this.cityList = cityList;
	}

	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	
}
