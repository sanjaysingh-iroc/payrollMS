package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetSBUByOrganization extends ActionSupport implements ServletRequestAware{

	  String strOrg;
	  List<FillServices> sbuList; 
	 

	  private static final long serialVersionUID = 1L;

	  public String execute() {
			
		  sbuList = new FillServices(request).fillServices(getStrOrg(), new UtilityFunctions());
//		  System.out.println("GetSBUByOrganization ...........");
		return SUCCESS;			
	  }


	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	public List<FillServices> getSbuList() {
		return sbuList;
	}

	public void setSbuList(List<FillServices> sbuList) {
		this.sbuList = sbuList;
	}

	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }
	
}
