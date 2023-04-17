package com.konnect.jpms.common;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

public class HelpPage extends ActionSupport implements ServletRequestAware {

	private String callPage;
	public String execute() throws Exception {
//		System.out.println("HelpPage ===>> ");
		return SUCCESS;
	}

	public String getCallPage() {
		return callPage;
	}
	
	public void setCallPage(String callPage) {
		this.callPage = callPage;
	}


	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	
}
