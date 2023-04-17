package com.konnect.jpms.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class UserGuide extends ActionSupport implements IStatements,ServletRequestAware 
{
  
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	private static Logger log = Logger.getLogger(SubmitTicket.class);
	  
	
	public String execute() throws Exception {
		session = request.getSession(true);
		
		
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		
		return LOAD;
  
	}
	
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
}
