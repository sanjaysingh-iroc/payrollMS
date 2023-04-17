package com.konnect.jpms.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;
import com.konnect.jpms.util.UtilityFunctions;
public class GetProjectListAjax extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {

	String emp_id;
	String p_id;
	String clientId;
	List<FillProjectList> projectdetailslist;
	String projectID;
	String projectName;
	String fromPage;		//===created by parvez date: 18-02-2022===
	
	public String execute(){
		
		
		UtilityFunctions uF=new UtilityFunctions();	
		
		if(getFromPage() != null && getFromPage().equalsIgnoreCase("AID")){
			projectdetailslist=new FillProjectList(request).fillProjectDetailsByCustomer(uF.parseToInt(getClientId()));
			
			
		}else{
			projectdetailslist=new FillProjectList(request).fillProjectDetailsByEmp(uF.parseToInt(emp_id), false, uF.parseToInt(getClientId()));
		}
//		projectdetailslist=new FillProjectList(request).fillProjectDetailsByEmp(uF.parseToInt(emp_id), false, uF.parseToInt(getClientId()));
		
		return SUCCESS;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getP_id() {
		return p_id;
	}

	public void setP_id(String p_id) {
		this.p_id = p_id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

//===start parvez date: 18-02-2022===	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
//===end parvez date: 18-02-2022===
	
}
