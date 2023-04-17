package com.konnect.jpms.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.opensymphony.xwork2.ActionSupport;

public class GetDepartment extends ActionSupport implements ServletRequestAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String wLocation;
	private List<FillDepartment> deptList = null;

	public String execute() throws Exception {

		if (getwLocation() != null && !getwLocation().equals("0")) {
			deptList = new ArrayList<FillDepartment>();
			deptList = new FillDepartment(request).fillDepartment();
			
			return SUCCESS;
		} else {
			deptList = new ArrayList<FillDepartment>();
			deptList = new FillDepartment(request).fillDepartment();
			
			return SUCCESS;
		}
	}

	public String getwLocation() {
		return wLocation;
	}

	public void setwLocation(String wLocation) {
		this.wLocation = wLocation;
	}

	public List<FillDepartment> getDeptList() {
		return deptList;
	}

	public void setDeptList(List<FillDepartment> deptList) {
		this.deptList = deptList;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	
}
