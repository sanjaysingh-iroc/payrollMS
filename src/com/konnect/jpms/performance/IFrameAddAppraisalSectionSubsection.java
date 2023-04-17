package com.konnect.jpms.performance;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class IFrameAddAppraisalSectionSubsection implements ServletRequestAware, SessionAware,
		IStatements {
	Map session;
	CommonFunctions CF;

	String strUserType = null; 
	String strSessionEmpId = null;

	private String id;
	 
	private String MLID;
	private String type;
	private String sysdiv; 
	private String newlvlno;
	private String newsysno;
	private String weightage;
	private String subWeightage;
	private String linkDiv;
	private String divCount;
	private String linkType;
	private String oreinted;
	private String appFreqId;
	private String fromPage;
	public String execute() {
		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return "login";
		
//		request.setAttribute(PAGE, "/jsp/performance/AddAppraisalLevelAndSystem.jsp");
//		request.setAttribute(TITLE, "Add New Appraisal Level");
//		System.out.println("getLinkType =====>>>>> "+getLinkType()); 
		
				return "success";
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMLID() {
		return MLID;
	}

	public void setMLID(String mLID) {
		MLID = mLID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSysdiv() {
		return sysdiv;
	}

	public void setSysdiv(String sysdiv) {
		this.sysdiv = sysdiv;
	}

	public String getNewlvlno() {
		return newlvlno;
	}

	public void setNewlvlno(String newlvlno) {
		this.newlvlno = newlvlno;
	}

	public String getNewsysno() {
		return newsysno;
	}

	public void setNewsysno(String newsysno) {
		this.newsysno = newsysno;
	}

	public String getWeightage() {
		return weightage;
	}

	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}

	public String getSubWeightage() {
		return subWeightage;
	}

	public void setSubWeightage(String subWeightage) {
		this.subWeightage = subWeightage;
	}

	public String getLinkDiv() {
		return linkDiv;
	}

	public void setLinkDiv(String linkDiv) {
		this.linkDiv = linkDiv;
	}

	public String getDivCount() {
		return divCount;
	}

	public void setDivCount(String divCount) {
		this.divCount = divCount;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public String getOreinted() {
		return oreinted;
	}


	public void setOreinted(String oreinted) {
		this.oreinted = oreinted;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}


	public String getAppFreqId() {
		return appFreqId;
	}


	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}


	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
}
