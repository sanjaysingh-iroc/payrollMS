package com.konnect.jpms.training;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class LearningPlanReasonPopup extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	String lPlanReason;
	CommonFunctions CF=null;
	
	private static Logger log = Logger.getLogger(LearningPlanReasonPopup.class);
	
	public String execute() {
		
		
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null){
		return LOGIN;
		}
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/training/LearningPlanReasonPopup.jsp");
		request.setAttribute(TITLE, "Learning Plan Reason");
		
		return LOAD;

	}

	public String getlPlanReason() {
		return lPlanReason;
	}
	
	public void setlPlanReason(String lPlanReason) {
		this.lPlanReason = lPlanReason;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request=request;
		
	}
	
}