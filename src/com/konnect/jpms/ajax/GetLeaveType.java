package com.konnect.jpms.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLeaveType extends ActionSupport implements ServletRequestAware{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6120802476753816720L;
	private String leaveType;
	private String orgId;

	String wLocationId;
	String levelId;
	String type;
	
	private List<FillLeaveType> leaveTypeList ;

	public String execute() throws Exception {
		UtilityFunctions uF=new UtilityFunctions();
//		System.out.println("getType()====>"+getType()+"----getOrgId()====>"+getOrgId()+"----getwLocationId()====>"+getwLocationId()+"----getLevelId()====>"+getLevelId());
		if (getType() !=null && getType().trim().equalsIgnoreCase("O") && uF.parseToInt(getOrgId()) > 0) {
			leaveTypeList = new FillLeaveType(request).fillLeave(uF.parseToInt(getOrgId()));
			return SUCCESS;
		} else if ((getType() == null || getType().trim().equals("")) &&uF.parseToInt(getOrgId()) > 0 && uF.parseToInt(getwLocationId()) > 0 && uF.parseToInt(getLevelId()) > 0) {
			leaveTypeList = new FillLeaveType(request).fillLeaveByLevel(uF.parseToInt(getOrgId()),uF.parseToInt(getwLocationId()),uF.parseToInt(getLevelId()));
			return SUCCESS;
		}else if (getLeaveType() != null && !getLeaveType().equals("0")) {
			leaveTypeList = new ArrayList<FillLeaveType>();
			leaveTypeList = new FillLeaveType(request).fillLeave(getLeaveType());
			
			return SUCCESS;
		} else {
			leaveTypeList = new ArrayList<FillLeaveType>();
			leaveTypeList = new FillLeaveType(request).fillLeave();
			
			return SUCCESS;
		}
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public String getLeaveType() {
		return leaveType;
	}
	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public List<FillLeaveType> getUserList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}
	public String getwLocationId() {
		return wLocationId;
	}
	public void setwLocationId(String wLocationId) {
		this.wLocationId = wLocationId;
	}
	public String getLevelId() {
		return levelId;
	}
	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
