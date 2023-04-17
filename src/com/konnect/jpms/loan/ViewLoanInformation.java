package com.konnect.jpms.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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

public class ViewLoanInformation extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	String strUserType;
	private static Logger log = Logger.getLogger(LoanApplicationReport.class);
	
	
	public String execute() throws Exception {

		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		String strLoanId = (String)request.getParameter("loanId");
		String strEmpId = (String)request.getParameter("strEmpId");
		viewLoanDetails(strLoanId,strEmpId);
		
					
		return SUCCESS;
	}

	
	public String viewLoanDetails(String strLoanId, String strEmpId){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			if(strEmpId==null){
				strEmpId = (String)session.getAttribute(EMPID);
			}
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmpSalaryDetails1);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			
			rs = pst.executeQuery();
			double dblTotalEarning = 0;
			
			while(rs.next()){
				dblTotalEarning += rs.getDouble("amount");
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmLoanDetails = new HashMap<String, String>();
			
			pst = con.prepareStatement(selectLoanDetails1);
			pst.setInt(1, uF.parseToInt(strLoanId));
			rs = pst.executeQuery();
			boolean  is_check_previous_loan = false;
			while(rs.next()){
				hmLoanDetails.put("LOAN_CODE", rs.getString("loan_code"));
				hmLoanDetails.put("LOAN_AMOUNT", uF.formatIntoComma(dblTotalEarning * rs.getDouble("times_salary") ));
				hmLoanDetails.put("ROI", uF.formatIntoComma(rs.getDouble("loan_interest"))+" %");
				hmLoanDetails.put("DESCRIPTION", rs.getString("loan_description"));
				hmLoanDetails.put("FINE_AMOUNT", uF.formatIntoComma(rs.getDouble("fine_amount")));
				hmLoanDetails.put("TIMES_SALARY", uF.formatIntoTwoDecimal(rs.getDouble("times_salary")));
				hmLoanDetails.put("MIN_SERVICE",  uF.formatIntoComma(rs.getDouble("min_service_years")));
				
				is_check_previous_loan = uF.parseToBoolean(rs.getString("is_check_previous_loan"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmLoanDetails", hmLoanDetails);
			
			if(is_check_previous_loan){
				pst = con.prepareStatement("select * from loan_applied_details where (is_completed is null or is_completed=false) and emp_id=? and loan_id=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, uF.parseToInt(strLoanId));
				rs = pst.executeQuery();
				boolean  isPeviousLoan = false;
				while(rs.next()){
					isPeviousLoan = true;
				}
				rs.close();
				pst.close();
				
				request.setAttribute("IS_PREVIOUS_LOAN", ""+isPeviousLoan);
			}		
			
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