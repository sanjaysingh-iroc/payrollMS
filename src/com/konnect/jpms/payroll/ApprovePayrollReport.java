package com.konnect.jpms.payroll;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class ApprovePayrollReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	

	/**
	 *   
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpID;
	String strUserType;
	String strSessionEmpId; 
 
	
	CommonFunctions CF = null;

	private static Logger log = Logger.getLogger(ApprovePayrollReport.class);
	
	
	public String execute() throws Exception {

		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		
		ApprovePayroll objApprovePayroll = new ApprovePayroll();
		objApprovePayroll.setServletRequest(request);
		objApprovePayroll.session = session;
		objApprovePayroll.CF = CF;
		objApprovePayroll.execute();
		
		
		
		return SUCCESS;
		
	}


	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.request = request;

	}
		
}
