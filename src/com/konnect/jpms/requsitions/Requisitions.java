package com.konnect.jpms.requsitions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Requisitions extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	
	private CommonFunctions CF;
	private static Logger log = Logger.getLogger(Requisitions.class);
	
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		
		if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))){
			request.setAttribute(PAGE, PRequisitions);
			request.setAttribute(TITLE, TRequisitions);
		}else if(strUserType!=null){
			request.setAttribute(PAGE, PRequisitionsReport);
			request.setAttribute(TITLE, TRequisitions);
			
			RequisitionReport objRR = new RequisitionReport();
			objRR.setServletRequest(request);
			objRR.CF = CF;
			objRR.session = session;
			objRR.viewReport(uF);
			
		} else{
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}

		
		
		return SUCCESS;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
}
