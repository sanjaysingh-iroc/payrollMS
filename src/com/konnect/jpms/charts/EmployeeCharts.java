package com.konnect.jpms.charts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeCharts extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -66624223770884531L;
	HttpSession session;
	
	public String execute() {
		
		System.out.println("Inside execute of EmployeeCharts");
		session = request.getSession(true);
		request.setAttribute(PAGE, PEmployeeCharts);
		request.setAttribute(TITLE, TEmployeeCharts);
		return SUCCESS;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
}
