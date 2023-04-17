package com.konnect.jpms.ajax;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDocument;
import com.opensymphony.xwork2.ActionSupport;

public class GetOrgwiseDocumentList extends ActionSupport implements ServletRequestAware{

	List<FillDocument> documentList;
	private String activityId;
	private String strOrgId;

	public String execute() {
		documentList = new FillDocument(request).fillDocumentList(getActivityId(), getStrOrgId());
		return SUCCESS;
	}

	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public List<FillDocument> getDocumentList() {
		return documentList;
	}

	public void setDocumentList(List<FillDocument> documentList) {
		this.documentList = documentList;
	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}
	



	
}
