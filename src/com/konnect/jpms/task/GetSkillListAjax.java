package com.konnect.jpms.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class GetSkillListAjax extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] skills;
	
	
	List<FillSkills> skillList;
	String serviceId;
	
	
	public String execute(){
		
		skillList = new FillSkills(request).fillSkills(getServiceId());
		
		return SUCCESS;
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}


	public List<FillSkills> getSkillList() {
		return skillList;
	}


	public void setSkillList(List<FillSkills> skillList) {
		this.skillList = skillList;
	}


	public String getServiceId() {
		return serviceId;
	}


	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

}
