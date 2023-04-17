package com.konnect.jpms.performance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class OneToOneDetails extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	CommonFunctions CF = null;
	String strSessionEmpId = null;
	String fromPage;
	String oneToOneId = null;
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
			
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		request.setAttribute(TITLE, " OneToOneDetails");
		request.setAttribute(PAGE, "/jsp/performance/OnetooneDetails.jsp");
		strSessionEmpId = (String) session.getAttribute(EMPID);
		System.out.println("fromPage==>"+getFromPage()+"==>oneToOneId==>"+getoneToOneId());		
		
		return LOAD;
	}
	private HttpServletRequest request;

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

	public String getoneToOneId() {
		return oneToOneId;
	}

	public void setoneToOneId(String oneToOneId) {
		this.oneToOneId = oneToOneId;
	}
	
	

}
