package com.konnect.jpms.performance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GoalKRATargets_1 implements ServletRequestAware, IStatements {

	public HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strUserType;
	String strBaseUserType;
	String strUserTypeId;
	public CommonFunctions CF;
	 
	private String callFrom;
	private String alertStatus;
	private String alert_type;
	private String alertID;
	private String pType;
	private String empId;
	
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/GoalKRATargets_1.jsp");
		request.setAttribute(TITLE, "Goals, KRAs, Targets");
		request.setAttribute("EmpId", empId);
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-line-chart\"></i><a href=\"GoalKRATargets_1.action\" style=\"color: #3c8dbc;\">Performance</a></li>" +
		"<li class=\"active\">Goals, KRAs, Targets</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		request.setAttribute("callFrom",callFrom);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		return LOAD;
	}
	
	public HttpServletRequest request;

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

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getpType() {
		return pType;
	}

	public void setpType(String pType) {
		this.pType = pType;
	}
	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	

}

