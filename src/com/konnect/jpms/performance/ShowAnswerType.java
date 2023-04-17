package com.konnect.jpms.performance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class ShowAnswerType extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8588089110618591746L;
	HttpSession session;
	String strSessionEmpId; 
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF; 
	private String ansType;

	public String execute() {
		
		session = request.getSession(); 
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		
		return SUCCESS;
	}
	

	public String getAnsType() {
		return ansType;
	}

	public void setAnsType(String ansType) {
		this.ansType = ansType;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

}
