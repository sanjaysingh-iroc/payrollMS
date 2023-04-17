package com.konnect.jpms.task;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class IFrameProjectAdHocInvoiceFormat_1 implements ServletRequestAware, SessionAware,
		IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null; 
	String strSessionEmpId = null;

	public String execute() {
		strUserType = (String) session.get(BASEUSERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
//		System.out.println("getLinkType =====>>>>> "+getLinkType()); 
				return "success";
	}

	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}

}
