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

import com.konnect.jpms.select.FillPaymentSource;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LoanPayments  extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strUserType;

	private static Logger log = Logger.getLogger(LoanPayments.class);
	
	List <FillPaymentSource> paymentSourceList;
	String paymentSource;
	String strAmount;
	
	String strLoanApplicationId;
	String strLoanId;
	String strEmpId;
	
	String strInstrumentNo;
	String strInstrumentDate;
	
	public String execute() throws Exception {

		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		String strLoanApplId = (String)request.getParameter("loanApplId");
		
		
		paymentSourceList = new FillPaymentSource().fillPaymentSource();
		
		
		if(getStrAmount()!=null){
			insertLoanPaymentDetails(strLoanApplId);
			return "save";
		}
		getLoanBalanceAmount(strLoanApplId);
		
		return SUCCESS;
	}
	
	
	

	
	private void getLoanBalanceAmount(String strLoanApplId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement(selectLoanApplied);
			pst.setInt(1, uF.parseToInt(getStrLoanApplicationId()));
			rs = pst.executeQuery();
			double dblBalance = 0;
			while(rs.next()){
				dblBalance = uF.parseToDouble(rs.getString("balance_amount"));
			}
			rs.close();
			pst.close();
			//dblBalance -= uF.parseToDouble(getStrAmount());
			
			request.setAttribute("loanBalance",""+ dblBalance);
			
		} catch (Exception e) {
			e.printStackTrace(); 
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}





	public String insertLoanPaymentDetails(String strLoanApplId){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement(insertLoanpayment);
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, uF.parseToInt(getStrLoanId()));
			pst.setDouble(3, uF.parseToDouble(getStrAmount()));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(5, getPaymentSource());
			pst.setInt(6, uF.parseToInt(getStrLoanApplicationId()));
			pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setString(8, getStrInstrumentNo());
			if(getStrInstrumentDate()!=null){
				pst.setDate(9, uF.getDateFormat(getStrInstrumentDate(), DATE_FORMAT));
			}else{
				pst.setDate(9, null);
			}
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement(selectLoanApplied);
			pst.setInt(1, uF.parseToInt(getStrLoanApplicationId()));
			rs = pst.executeQuery();
			double dblBalance = 0;
			while(rs.next()){
				dblBalance = uF.parseToDouble(rs.getString("balance_amount"));
			}
			rs.close();
			pst.close();
			
			
			
			dblBalance -= uF.parseToDouble(getStrAmount());
			
			
			pst = con.prepareStatement(updateLoanPayment);
			pst.setDouble(1, dblBalance);
			if(dblBalance<=0){
				pst.setBoolean(2, true);	
			}else{
				pst.setBoolean(2, false);
			}
			pst.setInt(3, uF.parseToInt(getStrLoanApplicationId()));
			pst.execute();
			pst.close();
			
			
			
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

	public List<FillPaymentSource> getPaymentSourceList() {
		return paymentSourceList;
	}

	public void setPaymentSourceList(List<FillPaymentSource> paymentSourceList) {
		this.paymentSourceList = paymentSourceList;
	}

	public String getPaymentSource() {
		return paymentSource;
	}

	public void setPaymentSource(String paymentSource) {
		this.paymentSource = paymentSource;
	}

	public String getStrAmount() {
		return strAmount;
	}

	public void setStrAmount(String strAmount) {
		this.strAmount = strAmount;
	}

	public String getStrLoanApplicationId() {
		return strLoanApplicationId;
	}

	public void setStrLoanApplicationId(String strLoanApplicationId) {
		this.strLoanApplicationId = strLoanApplicationId;
	}

	public String getStrLoanId() {
		return strLoanId;
	}

	public void setStrLoanId(String strLoanId) {
		this.strLoanId = strLoanId;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrInstrumentNo() {
		return strInstrumentNo;
	}

	public void setStrInstrumentNo(String strInstrumentNo) {
		this.strInstrumentNo = strInstrumentNo;
	}

	public String getStrInstrumentDate() {
		return strInstrumentDate;
	}

	public void setStrInstrumentDate(String strInstrumentDate) {
		this.strInstrumentDate = strInstrumentDate;
	}

}
