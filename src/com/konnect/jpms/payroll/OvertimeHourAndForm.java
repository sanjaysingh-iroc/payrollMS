package com.konnect.jpms.payroll;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class OvertimeHourAndForm extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String paycycle;
	String fromPage;
	
	public String execute() throws Exception {
      
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		  
		System.out.println("in OvertimeHourAndForm=======paycycle"+paycycle+"fromPage==="+fromPage);
		
		
		request.setAttribute(TITLE, "Overtime Hour And Form");
		request.setAttribute(PAGE, "/jsp/payroll/OvertimeHourAndForm.jsp");
		
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(ACCOUNTANT)
			&& !strUserType.equalsIgnoreCase(MANAGER) && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
		 	
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED;
		}
		
//		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
//		sbpageTitleNaviTrail.append("<li><a href=\"ApplyPay.action\"><i class=\"fa fa-user-times\"></i> Pay</a></li>" +
//			"<li class=\"active\" style=\"color: #3c8dbc;\">Apply Pay</li>");
//		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		return LOAD;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}
	public String getFromPage() {
		return fromPage;
	}
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

}
