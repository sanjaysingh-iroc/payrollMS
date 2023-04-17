package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmpClientListAjax  extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	List<FillClients> clientlist;
	String clientId;
	String clientName;
	UtilityFunctions uF=new UtilityFunctions();
	
	
	
	
	public String execute(){ 
	
		
		String empId = request.getParameter("empId");
		
		if(empId!=null){
			UtilityFunctions uF = new UtilityFunctions();
			clientlist=new FillClients(request).fillClients(uF.parseToInt(empId));
		}else{
			clientlist=new FillClients(request).fillClients(false);
		}
	
		
		return SUCCESS;
		
	}



	public List<FillClients> getClientlist() {
		return clientlist;
	}

	public void setClientlist(List<FillClients> clientlist) {
		this.clientlist = clientlist;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

}
