package com.konnect.jpms.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class Approvals extends ActionSupport implements IStatements, ServletRequestAware {

	private String userType;
	HttpSession session;
	private static Logger log = Logger.getLogger(Approvals.class);
	
	public String execute() throws Exception {

		session = request.getSession(true);
		userType = (String)session.getAttribute(USERTYPE);
		String strParentId = (String)request.getParameter("NN");
		

		request.setAttribute(TITLE, "Approvals");
		request.setAttribute(PAGE, "/jsp/common/Approvals.jsp");
				
		return loadNavigationInner();
	}

	private String loadNavigationInner() {
		return LOAD;
	}

	
	private HttpServletRequest request;
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
