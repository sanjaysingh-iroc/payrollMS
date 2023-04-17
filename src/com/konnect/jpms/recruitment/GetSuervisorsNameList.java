package com.konnect.jpms.recruitment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.opensymphony.xwork2.ActionSupport;

public class GetSuervisorsNameList extends ActionSupport implements
		ServletRequestAware {

//	String orgId;
//	String wLocId;
	String deptId;
	String lvlId;
//	String type;
	String userType;
	String sessionUserId;
	
//	List<FillEmployee> empList;
	List<FillEmployee> supervisorList;
	
	private static final long serialVersionUID = 1L;

	public String execute() {

//		System.out.println("userType ==>>> " + userType);
//		System.out.println("sessionUserId ==>>> " + sessionUserId);
//		System.out.println("deptId ==>>> " + deptId);
//		System.out.println("lvlId ==>>> " + lvlId);
//		empList=getEmployeeList();
//		supervisorList = new FillEmployee().fillTrainingEmployee(f_org, f_strWLocation, f_department, f_service, f_level, f_desig, f_grade);
		supervisorList = new FillEmployee(request).fillSupervisorEmployee(deptId, lvlId, userType, sessionUserId);
		return SUCCESS;

	}

	public List<FillEmployee> getSupervisorList() {
		return supervisorList;
	}

	public void setSupervisorList(List<FillEmployee> supervisorList) {
		this.supervisorList = supervisorList;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getLvlId() {
		return lvlId;
	}

	public void setLvlId(String lvlId) {
		this.lvlId = lvlId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getSessionUserId() {
		return sessionUserId;
	}

	public void setSessionUserId(String sessionUserId) {
		this.sessionUserId = sessionUserId;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}
