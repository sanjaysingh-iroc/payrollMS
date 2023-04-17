package com.konnect.jpms.ajax;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillActivity;
import com.konnect.jpms.select.FillDesig;
import com.opensymphony.xwork2.ActionSupport;

public class GetDesigList extends ActionSupport implements ServletRequestAware{

	String strLevel;
	String strDesignation;
	String strDesignationUpdate;
	String count;
	String type;
	String fromPage;
	
	List<FillDesig> desigList;
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		
		if(getStrDesignationUpdate()==null || getStrDesignationUpdate().length() == 0) {
			setStrDesignationUpdate(getStrDesignation());
		}
		
//		System.out.println("count ===> "+count);
		
		if(getStrLevel()!=null && getStrLevel().length()>0) {
			desigList = new FillDesig(request).fillDesigFromLevel(getStrLevel());
		}else {		
			desigList = new ArrayList<FillDesig>();
		}
		
		/*StringBuilder sbDesig = new StringBuilder(); 
		for (int k = 0; desigList != null && k < desigList.size(); k++) {
			sbDesig.append("<option value=" + ((FillDesig) desigList.get(k)).getDesigId() + "> " + ((FillDesig) desigList.get(k)).getDesigCodeName() + "</option>");
		}
		request.setAttribute("sbDesig", sbDesig);*/
		return SUCCESS;
		
	}


	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}

	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}	
}
