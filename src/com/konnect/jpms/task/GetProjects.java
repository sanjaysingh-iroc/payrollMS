package com.konnect.jpms.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillProject;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetProjects extends ActionSupport implements ServletRequestAware{

	String[] f_client;
	List<FillProject> projectList;
	String strClient;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("getStrClient ===>> " + getStrClient());
		if(getStrClient() != null && !getStrClient().equals("")) {
			setF_client(getStrClient().split(","));
		} else {
			setF_client(null);
		}
		
		projectList= new ArrayList<FillProject>();
		if(getF_client()!=null)	{
			projectList= new FillProject(request).fillProjects(getF_client());
		} else {
			projectList= new FillProject(request).fillProjects();
		}
//		System.out.println("in get projects ");
		return SUCCESS;
		 
	}
	

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public List<FillProject> getProjectList() {
		return projectList;
	}
	
	public void setProjectList(List<FillProject> projectList) {
		this.projectList = projectList;
	}
	
	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}

	public String getStrClient() {
		return strClient;
	}

	public void setStrClient(String strClient) {
		this.strClient = strClient;
	}
	
}
