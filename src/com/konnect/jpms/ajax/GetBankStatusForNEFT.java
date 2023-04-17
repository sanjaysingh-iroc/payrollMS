package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetBankStatusForNEFT extends ActionSupport implements IStatements,ServletRequestAware{
	
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private static final long serialVersionUID = 1L;

	private String bankAccount;
	
	public String execute() {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		/*System.out.println("mealType==>"+getMealType()+"startDate==>"+getStartDate()+"==>endDate==>"+getEndDate());*/
		checkBankStatusForNEFT(uF, getBankAccount());

		return SUCCESS;
	}
	
	private void checkBankStatusForNEFT(UtilityFunctions uF, String bankAccount) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmBankDetails = CF.getBankAccountDetailsMap(con, uF, getBankAccount());
			request.setAttribute("BANK_CODE", hmBankDetails.get("BANK_CODE"));

			String strSelectedBankCode = (String) request.getAttribute("BANK_CODE");
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
			String strBankStatus = "0";
			if(strSelectedBankCode != null && hmFeatureUserTypeId.get(F_PNB_BANK_CODE+"_USER_IDS") != null && hmFeatureUserTypeId.get(F_PNB_BANK_CODE+"_USER_IDS").contains(strSelectedBankCode)) {
				strBankStatus = "1";
			}
			if(strSelectedBankCode != null && hmFeatureUserTypeId.get(F_CANARA_BANK_CODE+"_USER_IDS") != null && hmFeatureUserTypeId.get(F_CANARA_BANK_CODE+"_USER_IDS").contains(strSelectedBankCode)) {
				strBankStatus = "1";
			}
			System.out.println("strBankStatus ===>> " + strBankStatus);
			request.setAttribute("STATUS_MSG", strBankStatus);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}


	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	
	
}
