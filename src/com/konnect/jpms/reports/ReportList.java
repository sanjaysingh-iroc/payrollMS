package com.konnect.jpms.reports;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class ReportList extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	private static Logger log = Logger.getLogger(ReportList.class);
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, PReportList);
		request.setAttribute(TITLE, TReportList);
					
		return LOAD;
	}
		
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
