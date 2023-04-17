package com.konnect.jpms.training;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class CreateNewVersionCoursePopup extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF = null;

	private String courseId;
	private String assignToExist;
	private  String operation;
    
	private static Logger log = Logger.getLogger(CreateNewVersionCoursePopup.class);
	
	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		
		request.setAttribute(PAGE, "/jsp/training/CreateNewVersionCoursePopup.jsp");
		request.setAttribute(TITLE, "Create New Course");
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		String no = request.getParameter("no");
		String yes = request.getParameter("yes");
		
		if(no != null || yes != null){
			if(yes != null){
				setAssignToExist("Yes");
			}else {
				setAssignToExist("No");
			}
			setOperation("E");
			return SUCCESS;
		}
		
		return LOAD;
	}
	
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;

	}
	
	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getAssignToExist() {
		return assignToExist;
	}

	public void setAssignToExist(String assignToExist) {
		this.assignToExist = assignToExist;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
}
