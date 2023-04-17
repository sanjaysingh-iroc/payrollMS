package com.konnect.jpms.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class GetTeamLeadListAjax extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String serviceId;
	String skill;
	String skills;
	
	HttpSession session;
	CommonFunctions CF = null;
	public String getSkill() {
		return skill;
	}
	public void setSkill(String skill) {
		this.skill = skill;
	}
	List<FillEmployee> teamleadNamesList;
	List<FillSkills> SkillList;
	
	
	
	public List<FillSkills> getSkillList() {
		return SkillList;
	}
	public void setSkillList(List<FillSkills> skillList) {
		SkillList = skillList;
	}
	public String execute(){
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//		if (CF==null){
//			CF = new CommonFunctions();
//		}
		if(CF==null)return LOGIN;
		
//		System.out.println("getSkills() from teamleadListajax=======>"+getSkills());
		
		String arr[] = getSkills().split(":");

		teamleadNamesList =new FillEmployee(request).fillEmployeeNameBySkills(arr, CF);
		return SUCCESS;
	}
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	
	
	public List<FillEmployee> getTeamleadNamesList() {
		return teamleadNamesList;
	}
	public void setTeamleadNamesList(List<FillEmployee> teamleadNamesList) {
		this.teamleadNamesList = teamleadNamesList;
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
	public String getSkills() {
		return skills;
	}
	public void setSkills(String skills) {
		this.skills = skills;
	}

}
