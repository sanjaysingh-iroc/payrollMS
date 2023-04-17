package com.konnect.jpms.offboarding;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class IFrameClearenceForm  implements ServletRequestAware, IStatements {

	HttpSession session;
	HttpServletResponse response;
	String strSessionUserType;
	CommonFunctions CF;
	
	String strEmpId;
	String resignId;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		
		return "success";
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getResignId() {
		return resignId;
	}

	public void setResignId(String resignId) {
		this.resignId = resignId;
	}
	
}
