package com.konnect.jpms.recruitment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDesig;
import com.opensymphony.xwork2.ActionSupport;

public class GetCandidateDesigList extends ActionSupport implements ServletRequestAware{

	String strLevel;
	String strdesig;
	
	public String getStrdesig() {
		return strdesig;
	}


	public void setStrdesig(String strdesig) {
		this.strdesig = strdesig;
	}


	List<FillDesig> desigList;
	
	public List<FillDesig> getDesigList() {
		return desigList;
	}


	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}


	private static final long serialVersionUID = 1L;

	public String execute() {
				
	
		
		if(getStrLevel()!=null && getStrLevel().length()>0) {
			
			desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
		
			
		}else {		
			desigList = new FillDesig(request).fillDesig();
		}
		
		return SUCCESS;
		
	}


	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}


	


	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}	
}
