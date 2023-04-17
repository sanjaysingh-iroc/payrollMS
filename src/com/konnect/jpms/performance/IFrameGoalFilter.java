package com.konnect.jpms.performance;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class IFrameGoalFilter  implements ServletRequestAware, SessionAware,
		IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null; 
	String strSessionEmpId = null;

	private String strOrg;
	private String location;
//	String sysdiv; 
	
	
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
//		System.out.println("strOrg =====> " + strOrg);
//		System.out.println("location =====> " + location);
		request.setAttribute(PAGE, "/jsp/performance/GetGoalEmployeeList.jsp");
		request.setAttribute(TITLE, "Employee List");
//		System.out.println("getLinkType =====>>>>> "+getLinkType()); 
			return "success";
	}

	
	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}


	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}

}
