package com.konnect.jpms.recruitment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillWLocation;
import com.opensymphony.xwork2.ActionSupport;

public class GetLocationOrg extends ActionSupport implements ServletRequestAware{

	  String strOrg;
	  List<FillWLocation> workLocationList;
	 
	  private static final long serialVersionUID = 1L;

	  String fromPage;
	  public String execute() {
			System.out.println("in execute method strOrg"+strOrg);
			
		  workLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		  
		  for(int i=0;i<workLocationList.size();i++)
		  {
			  System.out.println("id: "+workLocationList.get(i).getwLocationId()+"Name: "+workLocationList.get(i).getwLocationName());
		  }
		  
		return SUCCESS;			
	  }

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

}
