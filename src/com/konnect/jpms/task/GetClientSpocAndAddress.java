package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClientAddress;
import com.konnect.jpms.select.FillClientPoc;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetClientSpocAndAddress extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	CommonFunctions CF;
	HttpSession session;
	String clientid;
	
	public String execute() throws Exception {
		UtilityFunctions uF=new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		getProjectOwnerDetails(uF);
		return SUCCESS; 

	}
	
	private void getProjectOwnerDetails(UtilityFunctions uF) {
		
		List<FillClientPoc> clientPocList = new FillClientPoc(request).fillClientPoc(getClientid());
		List<FillClientAddress> clientAddressList =new FillClientAddress(request).fillClientAddress(getClientid(), uF);
		
		StringBuilder sbClientPoc=new StringBuilder();
		sbClientPoc.append("<select name=\"clientPoc\" id=\"clientPoc1\" class=\"validateRequired\" style=\"width:200px !important;\">" +
				"<option value=\"\">Select SPOC</option>");
		for(int i=0;clientPocList!=null && i<clientPocList.size();i++){
			sbClientPoc.append("<option value=\""+clientPocList.get(i).getClientPocId()+"\">"+clientPocList.get(i).getClientPocName()+"</option>");
		}
		sbClientPoc.append("</select>");
		
		StringBuilder sbClientAddress=new StringBuilder();
		sbClientAddress.append("<select name=\"clientAddress\" id=\"clientAddress\" class=\"validateRequired\" style=\"width:200px !important;\">" +
				"<option value=\"\">Select Address</option>");
		for(int i=0;clientAddressList!=null && i<clientAddressList.size();i++){
			sbClientAddress.append("<option value=\""+clientAddressList.get(i).getClientAddressId()+"\">"+clientAddressList.get(i).getClientAddress()+"</option>");
		}
		sbClientAddress.append("</select>");
		
		request.setAttribute("STATUS_MSG",sbClientPoc+"::::"+sbClientAddress);
		
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

}
