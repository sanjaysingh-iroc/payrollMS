package com.konnect.jpms.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddLoan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	CommonFunctions CF=null;
	HttpSession session;
	String orgId;
	String strLevel;
	
	String loanId;
	String loanCode;
	String loanDescription;
	String minServiceYears;
	String loanInterest;
	String fineAmount;
	String timesSalary;
	
	String userscreen; 
	String navigationId;
	String toPage;

	boolean isCheckPreviousLoan;
	
	public String execute() throws Exception { 
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		if (operation!=null && operation.equals("D")) {
			return deleteLoan(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewLoan(strId);
		}
		if (getLoanId()!=null && getLoanId().length()>0) { 
			return updateLoan();
		}
		if(getLoanCode()!=null && getLoanCode().length()>0){
			return insertLoan();
		}
		
		return loadLoan();
		
	}

	public String loadLoan() {
		return LOAD;
	}

	public String insertLoan() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
//			pst = con.prepareStatement(insertLoanDetails);
			pst = con.prepareStatement("insert into loan_details (loan_code, loan_description, min_service_years, loan_interest, fine_amount, times_salary, " +
					"entry_date, user_id, org_id,level_id,is_check_previous_loan) values (?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, getLoanCode());
			pst.setString(2, getLoanDescription());			
			pst.setInt(3, uF.parseToInt(getMinServiceYears()));
			pst.setDouble(4, uF.parseToDouble(getLoanInterest()));
			pst.setDouble(5, uF.parseToDouble(getFineAmount()));
			pst.setDouble(6, uF.parseToDouble(getTimesSalary()));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(9, uF.parseToInt(getOrgId()));
			pst.setInt(10, uF.parseToInt(getStrLevel()));
			pst.setBoolean(11, getIsCheckPreviousLoan());
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+"Loan policy saved successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateLoan() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement(updateLoanDetails);
			pst.setString(1, getLoanCode());
			pst.setString(2, getLoanDescription());
			pst.setInt(3, uF.parseToInt(getMinServiceYears()));
			pst.setDouble(4, uF.parseToDouble(getLoanInterest()));
			pst.setDouble(5, uF.parseToDouble(getFineAmount()));
			pst.setDouble(6, uF.parseToDouble(getTimesSalary()));
			pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(8, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setBoolean(9, getIsCheckPreviousLoan());
			pst.setInt(10, uF.parseToInt(getLoanId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Loan policy updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String viewLoan(String strId) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLoanDetails1);
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setLoanId(rs.getString("loan_id"));
				setLoanCode(rs.getString("loan_code"));
				setLoanDescription(rs.getString("loan_description"));
				setMinServiceYears(rs.getString("min_service_years"));
				setLoanInterest(rs.getString("loan_interest"));
				setFineAmount(rs.getString("fine_amount"));
				setTimesSalary(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("times_salary"))));
				setOrgId(rs.getString("org_id"));
				setStrLevel(rs.getString("level_id"));

				setIsCheckPreviousLoan(uF.parseToBoolean(rs.getString("is_check_previous_loan")));
			}
			rs.close();
			pst.close();			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;
	}
	
	public String deleteLoan(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteLoanDetails);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Loan policy deleted successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
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

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public String getLoanCode() {
		return loanCode;
	}

	public void setLoanCode(String loanCode) {
		this.loanCode = loanCode;
	}

	public String getLoanDescription() {
		return loanDescription;
	}

	public void setLoanDescription(String loanDescription) {
		this.loanDescription = loanDescription;
	}

	public String getMinServiceYears() {
		return minServiceYears;
	}

	public void setMinServiceYears(String minServiceYears) {
		this.minServiceYears = minServiceYears;
	}

	public String getLoanInterest() {
		return loanInterest;
	}

	public void setLoanInterest(String loanInterest) {
		this.loanInterest = loanInterest;
	}

	public String getFineAmount() {
		return fineAmount;
	}

	public void setFineAmount(String fineAmount) {
		this.fineAmount = fineAmount;
	}

	public String getTimesSalary() {
		return timesSalary;
	}

	public void setTimesSalary(String timesSalary) {
		this.timesSalary = timesSalary;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	public boolean getIsCheckPreviousLoan() {
		return isCheckPreviousLoan;
	}

	public void setIsCheckPreviousLoan(boolean isCheckPreviousLoan) {
		this.isCheckPreviousLoan = isCheckPreviousLoan;
	}

}