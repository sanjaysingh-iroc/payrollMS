package com.konnect.jpms.master;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSalaryHeadsByLevel extends ActionSupport implements ServletRequestAware{

	private static final long serialVersionUID = 1L;
	
	String strLevel;
	List<FillSalaryHeads> salaryHeadList;

	public String execute() {
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(getStrLevel()) > 0) {
			salaryHeadList = new FillSalaryHeads(request).fillAllowanceSalaryHeadsByLevel(getStrLevel());
		} else {		
			salaryHeadList = new ArrayList<FillSalaryHeads>();
		}		
		
		return SUCCESS;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}	
}