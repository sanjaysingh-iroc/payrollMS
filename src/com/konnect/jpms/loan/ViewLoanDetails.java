package com.konnect.jpms.loan;

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

public class ViewLoanDetails  extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strUserType;
	private static Logger log = Logger.getLogger(LoanApplicationReport.class);
	
	public String execute() throws Exception {

		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		 
		String strLoanApplId = (String)request.getParameter("loanApplId");
		
		viewEmployeeLoanDetails(strLoanApplId);
		
					
		return SUCCESS;
	}
	
	
	

	
	public String viewEmployeeLoanDetails(String strLoanApplId){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
  
			
			//Map<String, String> hmEmpNamMap = CF.getEmpNameMap(null, null);
			
			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmLoanDetails = new HashMap<String, String>();
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			pst = con.prepareStatement(selectLoanDetails);
			pst.setInt(1, uF.parseToInt(strLoanApplId));
			rs = pst.executeQuery();
			while(rs.next()){
			
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				hmLoanDetails.put("LOAN_CODE", rs.getString("loan_code"));
				hmLoanDetails.put("APPLIED_ON", uF.getDateFormat(rs.getString("applied_date"), DBDATE, CF.getStrReportDateFormat()));
				hmLoanDetails.put("APPROVED_ON", uF.getDateFormat(rs.getString("approved_date"), DBDATE, CF.getStrReportDateFormat()));
				hmLoanDetails.put("LOAN_AMOUNT", strCurrency+ uF.formatIntoTwoDecimal(rs.getDouble("amount_paid")));
				hmLoanDetails.put("TDS", strCurrency+ uF.formatIntoTwoDecimal(rs.getDouble("tds_amount")));
				hmLoanDetails.put("ROI", uF.formatIntoComma(rs.getDouble("loan_interest")));
				hmLoanDetails.put("DURATION", uF.formatIntoComma(rs.getDouble("duration_months")));
				hmLoanDetails.put("BALANCE_AMOUNT", strCurrency+ uF.formatIntoTwoDecimal(rs.getDouble("balance_amount")));
				
				if(uF.parseToBoolean(rs.getString("is_completed"))){
					hmLoanDetails.put("STATUS", "<div style=\"float: left; font-family: digital; color: green; position: fixed; margin-top: 15%; -moz-transform: rotate(-35deg); font-size: 60px; margin-left: 5%;\">Completed</div>");
				}
				
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectLoanPayments);
			pst.setInt(1, uF.parseToInt(strLoanApplId));
			rs = pst.executeQuery();

			List<List<String>> alLoanDetails = new ArrayList<List<String>>();
			List<String> alLoanInner = new ArrayList<String>();
			
			double dblAmountPaid = 0;
			while(rs.next()){
				
				alLoanInner = new ArrayList<String>();
				
				
				dblAmountPaid += uF.parseToDouble(rs.getString("amount_paid"));
				
				alLoanInner.add(uF.showData(uF.getDateFormat(rs.getString("paid_date"), DBDATE, CF.getStrReportDateFormat()), ""));
				alLoanInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount_paid"))));
				
				if(rs.getString("pay_source")!=null && rs.getString("pay_source").equalsIgnoreCase("C")){
					alLoanInner.add("Cash");
				}else if(rs.getString("pay_source")!=null && rs.getString("pay_source").equalsIgnoreCase("Q")){
					alLoanInner.add("Cheque<br/>Dated:"+uF.getDateFormat(rs.getString("ins_date"), DBDATE, CF.getStrReportDateFormat())+" bearing no:"+rs.getString("ins_no"));
				}else if(rs.getString("pay_source")!=null && rs.getString("pay_source").equalsIgnoreCase("B")){
					alLoanInner.add("Bonus");
				}else if(rs.getString("pay_source")!=null && rs.getString("pay_source").equalsIgnoreCase("S")){
					alLoanInner.add("Salary");
				}else  if(rs.getString("pay_source")!=null && rs.getString("pay_source").equalsIgnoreCase("L")){
					alLoanInner.add("LTA");
				}else{
					alLoanInner.add(rs.getString("pay_source"));
				}
				alLoanDetails.add(alLoanInner);
			}
			rs.close();
			pst.close(); 
			hmLoanDetails.put("PAID_AMOUNT", strCurrency+ uF.formatIntoComma(dblAmountPaid));
			
			
			request.setAttribute("alLoanDetails", alLoanDetails);
			request.setAttribute("hmLoanDetails", hmLoanDetails);
			
			
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

}
