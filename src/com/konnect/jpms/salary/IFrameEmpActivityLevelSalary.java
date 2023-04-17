package com.konnect.jpms.salary;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class IFrameEmpActivityLevelSalary implements ServletRequestAware, IStatements {
	HttpSession session;
	CommonFunctions CF;

	String strUserType = null; 
	String strSessionEmpId = null;

	String levelId;
	
	String empId;
	
	public String execute() {
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";
		
		return "success";
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}


}
