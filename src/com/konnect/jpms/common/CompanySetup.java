package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p>
 * Validate a user login.
 * </p>
 */
public class CompanySetup extends ActionSupport implements IStatements,ServletRequestAware, ServletResponseAware 
{
  
	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;  
	 
	private static Logger log = Logger.getLogger(CompanySetup.class);
	
	
	public String execute() throws Exception {
		session = request.getSession(true);
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		strUserType = (String)session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, PSetUpCompany);
		request.setAttribute(TITLE, TSetupCompany);
		
		
		if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) 
				|| strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO))){
			setUpCompany();
		}else{
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		
		
		
		return LOAD;
  
	}
	
	public void setUpCompany(){
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response= response;
		
	}

}