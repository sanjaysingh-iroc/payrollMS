package com.konnect.jpms.performance;



import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class ExitFeedbackPdf extends ActionSupport implements ServletRequestAware, IStatements,IConstants {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strSessionUserTypeID;
	CommonFunctions CF;
	private int id;
	private int resignId;
	private String userType;
	private String oreinted;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
				
		if (CF == null) return "login";
		
		return SUCCESS;
		
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getResignId() {
		return resignId;
	}

	public void setResignId(int resignId) {
		this.resignId = resignId;
	}
		
}
