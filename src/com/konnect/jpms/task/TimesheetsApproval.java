package com.konnect.jpms.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TimesheetsApproval extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strBaseUserType = null;
	String strProductType =  null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String callFrom;
	
	public String execute() throws Exception {
       
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strUserType = (String) session.getAttribute(USERTYPE);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		request.setAttribute(TITLE, "Timesheets");
		request.setAttribute(PAGE, "/jsp/task/TimesheetsApproval.jsp");
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-table\"></i><a href=\"TimesheetsApproval.action\" style=\"color: #3c8dbc;\"> Timesheet</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		if(strBaseUserType==null || (strBaseUserType!=null && strBaseUserType.equalsIgnoreCase(EMPLOYEE))) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
		return LOAD;
	}
	
	private HttpServletRequest request;

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

}
