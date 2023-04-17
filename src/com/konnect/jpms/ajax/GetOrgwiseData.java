package com.konnect.jpms.ajax;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrgwiseData extends ActionSupport implements ServletRequestAware,IStatements{

	HttpSession session;
	CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;
	
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillOrganisation> organisationList;
	String strOrgId;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getStrOrgId(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getStrOrgId());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getStrOrgId()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrgId()));
		serviceList = new FillServices(request).fillServices(getStrOrgId(),uF);
		
		return SUCCESS;
		
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}
	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	public List<FillServices> getServiceList() {
		return serviceList;
	}
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}
	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}
	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}
	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	public String getStrOrgId() {
		return strOrgId;
	}
	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}
	
}
