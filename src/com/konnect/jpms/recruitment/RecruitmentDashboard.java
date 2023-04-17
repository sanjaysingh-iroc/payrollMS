package com.konnect.jpms.recruitment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;

public class RecruitmentDashboard implements ServletRequestAware, IStatements {
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	private String fromPage;
	private String callFrom;
	private String recruitId;
	public String execute() {
		session = request.getSession();
	
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) 
			return "login";
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, "/jsp/recruitment/RecruitmentDashboard.jsp");
		request.setAttribute(TITLE, "Recruitments");
		if(strUserType==null || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(RECRUITER)
			&& !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(CEO))) {
		 	
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
//		System.out.println("getFromPage==>"+getFromPage());
		if(getFromPage() == null || getFromPage().equals("") || getFromPage().equalsIgnoreCase("null")) {
			StringBuilder sbpageTitleNaviTrail = new StringBuilder();
			sbpageTitleNaviTrail.append("<li><i class=\"fa fa-user-circle-o\"></i><a href=\"RecruitmentDashboard.action\" style=\"color: #3c8dbc;\"> Recruitment</a></li>" +
					"<li class=\"active\">Requirements</li>");
			request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		}
		
		if(getFromPage() != null && (getFromPage().equals("WF") || getFromPage().equals("JR"))) {
			return VIEW;
		}
		return LOAD;
	}
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getCallFrom() {
		return callFrom;
	}

	public void setCallFrom(String callFrom) {
		this.callFrom = callFrom;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

}

