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
 
public class PrevMobileReimbursements extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null; 
	
	private static Logger log = Logger.getLogger(PrevMobileReimbursements.class);
	
	public String execute() throws Exception {
		
		try {
			
			UtilityFunctions uF = new UtilityFunctions();
			session = request.getSession();
			CF = (CommonFunctions) session.getAttribute(CommonFunctions);
			if(CF==null)return LOGIN;
			
			viewIncentives(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return loadIncentives();

	}
	
	
	public String loadIncentives(){
		
		return LOAD;
	}
	
	public String viewIncentives(UtilityFunctions uF){
		
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
			
			pst = con.prepareStatement("select * from mobile_reimbursement_individual_details where emp_id = ? and is_approved=? order by pay_paycycle desc");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, 1);
			rs = pst.executeQuery();

			Map<String, String> hmMobileReimbursement = new HashMap<String, String>();
			Map<String, String> hmPaycycle = new HashMap<String, String>();
			List<String> alMobileReimbursement = new ArrayList<String>();
			while(rs.next()){
				hmMobileReimbursement.put(rs.getString("pay_paycycle"), rs.getString("pay_amount"));
				hmPaycycle.put(rs.getString("pay_paycycle"), uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
				alMobileReimbursement.add(rs.getString("pay_paycycle"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMobileReimbursement", hmMobileReimbursement);
			request.setAttribute("hmPaycycle", hmPaycycle);
			request.setAttribute("alMobileReimbursement", alMobileReimbursement);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and is_paid = ? and salary_head_id =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setBoolean(2, true);
			pst.setInt(3, MOBILE_REIMBURSEMENT);
			rs = pst.executeQuery();

			Map<String, String> hmPaidMobileReimbursements = new HashMap<String, String>();
			while(rs.next()){
				hmPaidMobileReimbursements.put(rs.getString("paycycle"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPaidMobileReimbursements", hmPaidMobileReimbursements);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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

}
