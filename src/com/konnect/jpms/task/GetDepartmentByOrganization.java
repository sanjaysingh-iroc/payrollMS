package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetDepartmentByOrganization extends ActionSupport implements ServletRequestAware{

	  String strOrg;
	  List<FillDepartment> departmentList; 
	 

	  private static final long serialVersionUID = 1L;

	  public String execute() { 
		
		UtilityFunctions uF = new UtilityFunctions();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrg()));
//		System.out.println("GetDepartmentByOrganization ...........");
		return SUCCESS;			
	  }


	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

	
}
