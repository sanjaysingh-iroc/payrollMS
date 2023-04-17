package com.konnect.jpms.training;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class GetWorkLocationList extends ActionSupport implements ServletRequestAware,IStatements {

	private static final long serialVersionUID = 1L;
	
	private String orgId;
	
	private List<FillWLocation> workList;
	
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF = null;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
//		System.out.println("orgId =====> "+ orgId);
		workList=new FillWLocation(request).fillWLocation(orgId);
//		System.out.println("workList =====> "+ workList);
		return SUCCESS;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
