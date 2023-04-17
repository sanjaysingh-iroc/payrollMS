package com.konnect.jpms.requsitions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillPerkSalary;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetPerkSalary extends ActionSupport implements IStatements, ServletRequestAware {

	private static final long serialVersionUID = 6483180990145887248L;

	HttpSession session;
	private CommonFunctions CF;
	String strSessionEmpId;
	
	String financialYear;
	List<FillPerkSalary> perkSalaryList;	
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		getGetPerkSalary(uF);
		
		return SUCCESS;		
	}
	
	private void getGetPerkSalary(UtilityFunctions uF) {
		try{
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				String[] strFinancialYear = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			perkSalaryList = new FillPerkSalary(request).fillPerkSalary(strSessionEmpId, strFinancialYearStart, strFinancialYearEnd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillPerkSalary> getPerkSalaryList() {
		return perkSalaryList;
	}

	public void setPerkSalaryList(List<FillPerkSalary> perkSalaryList) {
		this.perkSalaryList = perkSalaryList;
	}
}