package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class ViewAndUpdateExchangeValue extends ActionSupport implements IStatements, ServletRequestAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2179294534357956579L;
	String strSessionEmpId;
	HttpSession session;
	CommonFunctions CF;
	
	String currencyType;
	String proCurrency;
	 
	String exchangeValue;
	String strReportCurrId;
	String strBillingCurrId;
	
	String type;
	String operation;
	
	public String execute() throws Exception { 
		 
		session = request.getSession();
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		System.out.println("getType() ===>>>> " + getType());
		System.out.println("currencyType ===>>>> " + currencyType);
		System.out.println("strBillingCurrId ===>>>> " + strBillingCurrId);
		System.out.println("strReportCurrId ===>>>> " + strReportCurrId);
		
		if(getType()!=null && getType().equals("V")) {
			getExchangeValue(uF, currencyType, proCurrency);
		} else if(getType()!=null && getType().equals("ExV")) {
			getBExchangeValue(uF, strBillingCurrId, strReportCurrId);
		} else {
			updateExchangeValue(uF, currencyType, proCurrency);
			setType("V");
		}
		
		return SUCCESS;
	
	}
	

	private void getExchangeValue(UtilityFunctions uF, String currencyType, String proCurrency) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			List<String> exchangeCurrList = new ArrayList<String>();
			pst = con.prepareStatement("select curr_to_value,updated_by,update_date from currencies_details where curr_from_id = ? and curr_to_id=?");
			pst.setInt(1, uF.parseToInt(currencyType));
			pst.setInt(2, uF.parseToInt(proCurrency));
			System.out.println("pst ===>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProCurrData = CF.getCurrencyDetailsById(con, uF, proCurrency);
			Map<String, String> hmInvoiceCurrData = CF.getCurrencyDetailsById(con, uF, currencyType);
			
			exchangeCurrList.add(uF.showData(hmProCurrData.get("SHORT_CURR"), ""));
			exchangeCurrList.add(uF.showData(hmProCurrData.get("LONG_CURR"), ""));
			
			exchangeCurrList.add(uF.showData(hmInvoiceCurrData.get("SHORT_CURR"), ""));
			exchangeCurrList.add(uF.showData(hmInvoiceCurrData.get("LONG_CURR"), ""));
			
			while(rs.next()) {
				exchangeCurrList.add(uF.showData(rs.getString("curr_to_value"), "0"));
				exchangeCurrList.add(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("updated_by")), ""));
				exchangeCurrList.add(uF.showData(uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()), ""));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("exchangeCurrList", exchangeCurrList);
			
//			List<String> exchangeCurrList = new ArrayList<String>();
//			pst = con.prepareStatement("select short_currency,long_currency,inr_value,updated_by,update_date from currency_details where currency_id = ?");
//			pst.setInt(1, uF.parseToInt(currencyType));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				exchangeCurrList.add(uF.showData(rs.getString("short_currency"), ""));
//				exchangeCurrList.add(uF.showData(rs.getString("long_currency"), ""));
//				exchangeCurrList.add(uF.showData(rs.getString("inr_value"), ""));
//				exchangeCurrList.add(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("updated_by")), ""));
//				exchangeCurrList.add(uF.showData(uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()), ""));
//			}
//			
//			request.setAttribute("exchangeCurrList", exchangeCurrList);

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getBExchangeValue(UtilityFunctions uF, String strBillingCurrId, String strReportCurrId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			List<String> exchangeCurrList = new ArrayList<String>();
			pst = con.prepareStatement("select curr_to_value from currencies_details where curr_from_id = ? and curr_to_id=?");
			pst.setInt(1, uF.parseToInt(strReportCurrId));
			pst.setInt(2, uF.parseToInt(strBillingCurrId));
			System.out.println("pst BEx ===>>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				exchangeCurrList.add(uF.showData(rs.getString("curr_to_value"), "0"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("exchangeCurrList", exchangeCurrList);

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void updateExchangeValue(UtilityFunctions uF, String currencyType, String proCurrency) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("update currencies_details set curr_to_value=?, updated_by=?, update_date=? where curr_from_id=? and curr_to_id=?");
			pst.setDouble(1, uF.parseToDouble(getExchangeValue()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(currencyType));
			pst.setInt(5, uF.parseToInt(proCurrency));
			pst.executeUpdate();
			pst.close();
			
			List<String> exchangeCurrList = new ArrayList<String>();
			pst = con.prepareStatement("select curr_to_value,updated_by,update_date from currencies_details where curr_from_id = ? and curr_to_id=?");
			pst.setInt(1, uF.parseToInt(currencyType));
			pst.setInt(2, uF.parseToInt(proCurrency));
//			System.out.println("pst ===>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmProCurrData = CF.getCurrencyDetailsById(con, uF, proCurrency);
			Map<String, String> hmInvoiceCurrData = CF.getCurrencyDetailsById(con, uF, currencyType);
			
			exchangeCurrList.add(uF.showData(hmProCurrData.get("SHORT_CURR"), ""));
			exchangeCurrList.add(uF.showData(hmProCurrData.get("LONG_CURR"), ""));
			
			exchangeCurrList.add(uF.showData(hmInvoiceCurrData.get("SHORT_CURR"), ""));
			exchangeCurrList.add(uF.showData(hmInvoiceCurrData.get("LONG_CURR"), ""));
			
			while(rs.next()) {
				exchangeCurrList.add(uF.showData(rs.getString("curr_to_value"), "0"));
				exchangeCurrList.add(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("updated_by")), ""));
				exchangeCurrList.add(uF.showData(uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()), ""));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("exchangeCurrList", exchangeCurrList);
			
//			List<String> exchangeCurrList = new ArrayList<String>();
//			pst = con.prepareStatement("select short_currency,long_currency,inr_value,updated_by,update_date from currency_details where currency_id = ?");
//			pst.setInt(1, uF.parseToInt(currencyType));
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				exchangeCurrList.add(uF.showData(rs.getString("short_currency"), ""));
//				exchangeCurrList.add(uF.showData(rs.getString("long_currency"), ""));
//				exchangeCurrList.add(uF.showData(rs.getString("inr_value"), ""));
//				exchangeCurrList.add(uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("updated_by")), ""));
//				exchangeCurrList.add(uF.showData(uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()), ""));
//			}
//			
//			request.setAttribute("exchangeCurrList", exchangeCurrList);

		} catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getExchangeValue() {
		return exchangeValue;
	}

	public void setExchangeValue(String exchangeValue) {
		this.exchangeValue = exchangeValue;
	}

	public String getStrReportCurrId() {
		return strReportCurrId;
	}

	public void setStrReportCurrId(String strReportCurrId) {
		this.strReportCurrId = strReportCurrId;
	}

	public String getStrBillingCurrId() {
		return strBillingCurrId;
	}

	public void setStrBillingCurrId(String strBillingCurrId) {
		this.strBillingCurrId = strBillingCurrId;
	}

	public String getProCurrency() {
		return proCurrency;
	}

	public void setProCurrency(String proCurrency) {
		this.proCurrency = proCurrency;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}