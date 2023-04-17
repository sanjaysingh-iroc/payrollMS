package com.konnect.jpms.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCity;
import com.konnect.jpms.select.FillSalaryHeads;
import com.opensymphony.xwork2.ActionSupport;

public class GetSalaryHead extends ActionSupport implements ServletRequestAware {
	private String levelId;
	private List<FillSalaryHeads> salaryHeadList;

	public String execute() throws Exception {

		  
		if (getLevelId() != null && !getLevelId().equals("0")) {
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(getLevelId());			
		} else {
//			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
			salaryHeadList = new ArrayList<FillSalaryHeads>();			
		}
		return SUCCESS;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}	
}