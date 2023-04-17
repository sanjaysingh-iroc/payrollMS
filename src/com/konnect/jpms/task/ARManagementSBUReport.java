package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ARManagementSBUReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	       
	CommonFunctions CF = null; 
	 
	String[] f_service;
	String[] f_client;
	 
	String strStartDate;
	String strEndDate;
	
	List<FillServices> serviceList;
	List<FillClients> clientList;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
			
			session = request.getSession();
			request.setAttribute(PAGE, "/jsp/task/ARManagementSBUReport.jsp");
			request.setAttribute(TITLE, "AR Management Report");
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			strEmpId =(String) session.getAttribute(EMPID);
			strUserType =(String) session.getAttribute(BASEUSERTYPE);
			
			/*boolean isView  = CF.getAccess(session, request, uF);
			if(!isView){
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}*/
			
		return loadPaySlips(uF);

	}
	
	
	public String loadPaySlips(UtilityFunctions uF) {
		serviceList = new FillServices(request).fillServices("0", uF);
		clientList = new FillClients(request).fillClients(false);
		return LOAD;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

}
