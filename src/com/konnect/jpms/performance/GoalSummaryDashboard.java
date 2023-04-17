package com.konnect.jpms.performance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GoalSummaryDashboard implements ServletRequestAware, IStatements {
	public HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strUserType;
	String strBaseUserType;
	String strUserTypeId;
	public CommonFunctions CF;
	
	private String dataType;
	private String fromPage;
	private String currUserType;
	
	private String alertStatus;
	private String alert_type;
	private String alertID;
	private String pType;
	
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
		UtilityFunctions uF = new UtilityFunctions();
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
		if(getFromPage() == null || getFromPage().equals("") || getFromPage().equalsIgnoreCase("null" )){
			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-line-chart\"></i><a href=\"GoalKRATargetDashboard.action\" style=\"color: #3c8dbc;\"> Performance</a></li>" +
			"<li class=\"active\">Goals</li>");
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
			boolean isView  = CF.getAccess(session, request, uF);
			if(!isView) {
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}
		}
		request.setAttribute(PAGE, "/jsp/performance/GoalSummaryDashboard.jsp");
		request.setAttribute(TITLE, "Goals");
			
		
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		if(getFromPage()!= null && getFromPage().equals("GKTS") ){
			return LOAD;
		}
		return VIEW;
	}
	
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}


	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
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
	
}
