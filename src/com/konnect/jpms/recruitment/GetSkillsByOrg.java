package com.konnect.jpms.recruitment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSkills;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSkillsByOrg extends ActionSupport implements ServletRequestAware{

	  String strOrg;
	  List<FillSkills> skillslist;
	  List<FillSkills> essentialSkillsList; 

	  private static final long serialVersionUID = 1L;

	  public String execute() {
		UtilityFunctions uF = new UtilityFunctions();
//		skillslist = new FillSkills(request).fillSkillsOrg(uF.parseToInt(getStrOrg()));
//		System.out.println("getStrOrg====>"+getStrOrg());
		skillslist = new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(getStrOrg()));
		essentialSkillsList = new FillSkills(request).fillSkillsWithIdOnOrg(uF.parseToInt(getStrOrg()));
		return SUCCESS;			
	  }

	  HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }  

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	public List<FillSkills> getSkillslist() {
		return skillslist;
	}


	public void setSkillslist(List<FillSkills> skillslist) {
		this.skillslist = skillslist;
	}

	


	public List<FillSkills> getEssentialSkillsList() {
		return essentialSkillsList;
	}


	public void setEssentialSkillsList(List<FillSkills> essentialSkillsList) {
		this.essentialSkillsList = essentialSkillsList;
	}


}
