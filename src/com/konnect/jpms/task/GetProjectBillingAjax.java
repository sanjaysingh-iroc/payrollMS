package com.konnect.jpms.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetProjectBillingAjax extends ActionSupport implements IStatements, ServletRequestAware,ServletResponseAware {

	String clientId;
	List<FillBillingNumber> billDetailsList;
	
	public String execute(){
		
		UtilityFunctions uF=new UtilityFunctions();
		
		billDetailsList = new FillBillingNumber(request).fillBillDetailsByCustomer(uF.parseToInt(getClientId()));

		StringBuilder sbBillList = new StringBuilder();
		for(int i=0; i<billDetailsList.size(); i++){
			String[] strLbl = ((FillBillingNumber)billDetailsList.get(i)).getBillNumber().split(",");
			sbBillList.append("<option id='option_"+i+"' value='"+((FillBillingNumber)billDetailsList.get(i)).getBillID()+ "' billDetails="+strLbl[1]+ " billDetailsRcvd="+strLbl[2]+ " > "+uF.showData(strLbl[0], "") +"");
//			sbBillList.append("<option id='option_"+i+"' value='"+((FillBillingNumber)billDetailsList.get(i)).getBillID()+ "' billDetails="+strLbl[1]+ " > "+uF.showData(strLbl[0], "") +"");
		}
		
		request.setAttribute("sbBillList", sbBillList);
		return SUCCESS;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		// TODO Auto-generated method stub
		
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
		
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public List<FillBillingNumber> getBillDetailsList() {
		return billDetailsList;
	}

	public void setBillDetailsList(List<FillBillingNumber> billDetailsList) {
		this.billDetailsList = billDetailsList;
	}
	
}
