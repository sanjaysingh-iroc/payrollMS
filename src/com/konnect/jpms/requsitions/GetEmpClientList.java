package com.konnect.jpms.requsitions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmpClientList  extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {

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
			getClientList(empId);
		}
		
		
		return SUCCESS;
		
	}



	private void getClientList(String empId) {
		UtilityFunctions uF = new UtilityFunctions();
		clientlist=new FillClients(request).fillClients(uF.parseToInt(empId));
		
		StringBuilder sb = new StringBuilder();
//		sb.append("<select name=\"strClient\" class=\"validate[required]\" onchange=\"getContent('typeP', 'GetEmpClientProject.action?empId="+empId+"&client_id='+this.value)\">");
		sb.append("<select name=\"strClient\" class=\"validateRequired\" onchange=\"getContent('typeP', 'GetProjectClientTask.action?strEmpId="+empId+"&client_id='+this.value+'&type=R')\">");
		sb.append("<option value=\"\">Select Client</option>");
		for(int i=0;clientlist!=null && i<clientlist.size();i++){
			sb.append("<option value=\""+clientlist.get(i).getClientId()+"\">"+clientlist.get(i).getClientName()+"</option>");
		}		
		sb.append("</select>");
		request.setAttribute("STATUS_MSG", sb.toString());
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
