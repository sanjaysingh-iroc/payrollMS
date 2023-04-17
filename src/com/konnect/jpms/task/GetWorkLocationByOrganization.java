package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetWorkLocationByOrganization extends ActionSupport implements ServletRequestAware, IStatements {

	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String strOrg;
	List<FillWLocation> workLocationList;

	private static final long serialVersionUID = 1L;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;
		
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			workLocationList = new FillWLocation(request).fillWLocation(getStrOrg(), (String)session.getAttribute(WLOCATION_ACCESS));
		} else {
			workLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		}
//		System.out.println("GetWorkLocationByOrganization ..........."+ getStrOrg());
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


	HttpServletRequest request;
		
	  @Override
	  public void setServletRequest(HttpServletRequest request) {
			this.request = request;
	  }

	
}
