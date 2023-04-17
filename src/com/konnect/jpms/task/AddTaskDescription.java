package com.konnect.jpms.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class AddTaskDescription extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	HttpSession session;
	CommonFunctions CF;
	String count;
	String proId;
	String taskDescription;
	String divId;
	String fromPage;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
//			System.out.println("getProId() ==>"+ getProId());
			return LOAD;
	}
	
	
	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getDivId() {
		return divId;
	}

	public void setDivId(String divId) {
		this.divId = divId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
		
	}
}
