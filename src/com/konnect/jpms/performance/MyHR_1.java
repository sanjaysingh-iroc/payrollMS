package com.konnect.jpms.performance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyHR_1 extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = -4581306410273940249L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	CommonFunctions CF;
	
	String strUserType;
	private String callFrom;
	
	private String alertID;
	private String pType;
	public String execute() {
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		strUserType = (String) session.getAttribute(USERTYPE);
				
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/performance/MyHR_1.jsp");
		request.setAttribute(TITLE, "My HR"); //TKRAs	
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-th\"></i><a href=\"MyHR.action\" style=\"color: #3c8dbc;\">My HR</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		System.out.println("LOAD");
		return LOAD;
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getpType() {
		return pType;
	}

	public void setpType(String pType) {
		this.pType = pType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

}

