package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PrevAnnualVariable extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	CommonFunctions CF = null; 
	
	String strEmpId;
	String SHID;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		UtilityFunctions uF = new UtilityFunctions();
		
		viewPrevAnnualVariable(uF);
			
		return LOAD;
	}
	
	public String viewPrevAnnualVariable(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(getStrEmpId())) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(getStrEmpId()));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			request.setAttribute("strCurrency",strCurrency); 
			
			Map<String, String> hmSalaryHeadMap = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where salary_head_id=?");
			pst.setInt(1, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmSalaryHeadMap.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmAnnualVariable = new HashMap<String, String>();
			Map<String, String> hmPaycycle = new HashMap<String, String>();
			Map<String, String> hmSalaryHead = new HashMap<String, String>();
			List<String> alAnnualVariable = new ArrayList<String>();			
			
			pst = con.prepareStatement("select * from annual_variable_individual_details where emp_id = ? and is_approved=? and salary_head_id=? order by pay_paycycle desc");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, 1);
			pst.setInt(3, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();
			while(rs.next()){
				hmAnnualVariable.put(rs.getString("pay_paycycle"), rs.getString("pay_amount"));
				hmPaycycle.put(rs.getString("pay_paycycle"), uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat())+" to  "+uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
				hmSalaryHead.put(rs.getString("pay_paycycle"),hmSalaryHeadMap.get(rs.getString("salary_head_id")));
				alAnnualVariable.add(rs.getString("pay_paycycle"));   
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSalaryHead", hmSalaryHead);
			request.setAttribute("hmSalaryHeadName", hmSalaryHeadMap.get(getSHID()));
			
			request.setAttribute("hmAnnualVariable", hmAnnualVariable);
			request.setAttribute("hmPaycycle", hmPaycycle);
			request.setAttribute("alAnnualVariable", alAnnualVariable);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and is_paid = ? and salary_head_id =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setBoolean(2, true);
			pst.setInt(3, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();
			Map<String, String> hmPaidAnnualVariable = new HashMap<String, String>();
			while(rs.next()){
				hmPaidAnnualVariable.put(rs.getString("paycycle"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPaidAnnualVariable", hmPaidAnnualVariable);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getSHID() {
		return SHID;
	}

	public void setSHID(String sHID) {
		SHID = sHID;
	}
}
