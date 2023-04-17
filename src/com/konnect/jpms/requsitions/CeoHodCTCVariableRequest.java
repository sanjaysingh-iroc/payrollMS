package com.konnect.jpms.requsitions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class CeoHodCTCVariableRequest extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String currUserType;
	String callFrom;
	String alertID;
	public String execute() throws Exception {
      
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		  
		request.setAttribute(TITLE, "CTC Request");
		request.setAttribute(PAGE, "/jsp/requisitions/CeoHodCTCVariableRequest.jsp");
		
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(MANAGER))) {
		 	
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

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

}
