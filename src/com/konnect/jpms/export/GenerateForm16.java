package com.konnect.jpms.export;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.IStatements;
import com.opensymphony.xwork2.ActionSupport;

public class GenerateForm16 extends ActionSupport implements ServletRequestAware, IStatements {
	
	
	public void generateForm16(){
		
		
		
		String strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
		String strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
		
		
		Map hmExemption = (Map)request.getAttribute("hmExemption");
		Map hmHRAExemption = (Map)request.getAttribute("hmHRAExemption");
		Map hmRentPaid = (Map)request.getAttribute("hmRentPaid");
		Map hmInvestment = (Map)request.getAttribute("hmInvestment");
		Map hmTaxLiability = (Map)request.getAttribute("hmTaxLiability");
		Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
		Map hmEmployeeMap = (Map)request.getAttribute("hmEmployeeMap");
		Map hmPayrollDetails = (Map)request.getAttribute("hmPayrollDetails");
		
		
		
		
	}
	
	
	
	
	

	
	
	
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
}
