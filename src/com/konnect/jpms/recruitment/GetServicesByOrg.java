package com.konnect.jpms.recruitment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetServicesByOrg extends ActionSupport implements ServletRequestAware{

	  String strOrg;
	  List<FillServices> serviceslist; 
	 

	  private static final long serialVersionUID = 1L;

	  public String execute() {
			
		  serviceslist = new FillServices(request).fillServices(getStrOrg(), new UtilityFunctions());
		return SUCCESS;			
	  }


	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	public List<FillServices> getServiceslist() {
		return serviceslist;
	}

	public void setServiceslist(List<FillServices> serviceslist) {
		this.serviceslist = serviceslist;
	}


	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

	
}
