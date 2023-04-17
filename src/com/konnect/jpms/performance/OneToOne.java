package com.konnect.jpms.performance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class OneToOne implements ServletRequestAware, IStatements {
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
	
	private String reviewId;
	private String appFreqId;
	
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
		request.setAttribute(PAGE, "/jsp/performance/Onetoone.jsp");
		request.setAttribute(TITLE, "OneToOne");
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-line-chart\"></i><a href=\"GoalKRATargets.action\" style=\"color: #3c8dbc;\">Performance</a></li>" +
		"<li class=\"active\">Reviews</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		UtilityFunctions uF = new UtilityFunctions();
		
		return LOAD;
	}
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}


}
