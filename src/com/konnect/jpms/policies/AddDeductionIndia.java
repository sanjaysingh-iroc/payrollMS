package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddDeductionIndia extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	private String deductionId;
	private String incomeFrom;
	private String incomeTo;
	private String deductionAmount;
	private String deductionAmountPaycycle;
	private List<FillState> stateList;
	private String state;
	private String strFinancialYearTo;
	private String strFinancialYearFrom;

	CommonFunctions CF;
	
	private String operation;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	private String financialYear;
	private String gender;
	private List<FillGender> genderList;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, PAddDeduction);
		strEmpId = (String) session.getAttribute("EMPID");
		
//		request.setAttribute(PAGE, PAddDeduction);
		strEmpId = (String) session.getAttribute("EMPID");
		
		if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
			String strFinancialYears[] = getFinancialYear().split("-");
			setStrFinancialYearFrom(strFinancialYears[0]);
			setStrFinancialYearTo(strFinancialYears[1]);
//			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		loadValidateDeduction();
		
		if(operation != null && operation.equals("A")) {
			return insertDeduction();
		} else if (operation != null && operation.equals("E")) {
			return viewDeduction();
			
		} else if (operation != null && operation.equals("U")) {
			return updateDeduction();
			
		} else if (operation != null && operation.equals("D")) {
			return deleteDeduction();
			
		}
		if(getOperation() == null) {
			setOperation("A");
		}
		return LOAD;
		
	}

	
	private String viewDeduction() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from deduction_details_india WHERE deduction_id=?");
			pst.setInt(1, uF.parseToInt(getDeductionId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				setIncomeFrom(rs.getString("income_from"));
				setIncomeTo(rs.getString("income_to"));
				setDeductionAmountPaycycle(rs.getString("deduction_paycycle"));
				setDeductionAmount(rs.getString("deduction_amount"));
				setState(rs.getString("state_id"));
				setStrFinancialYearFrom(uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, DATE_FORMAT));
				setStrFinancialYearTo(uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, DATE_FORMAT));
				setOperation("U");
				setGender(rs.getString("gender"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}


	public String loadValidateDeduction() {
		request.setAttribute(PAGE, PAddDeductionIndia);
		request.setAttribute(TITLE, TAddDeduction);
		
		stateList = new FillState(request).fillWLocationStates();
		genderList = new FillGender().fillGender();
		return LOAD;
	}
	
	public String updateDeduction() {
 
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String updateAllowance = "UPDATE deduction_details_india SET income_from=?,income_to=?,deduction_paycycle=?,deduction_amount=?," +
				"state_id=?,financial_year_from=?,financial_year_to=?,entry_date=?,user_id=?,gender=? WHERE deduction_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateAllowance);
			pst.setDouble(1, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(2, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(3, uF.parseToDouble(getDeductionAmountPaycycle()));
			pst.setDouble(4, uF.parseToDouble(getDeductionAmount()));
			pst.setInt(5, uF.parseToInt(getState()));
			pst.setDate(6, uF.getDateFormat(getStrFinancialYearFrom(), DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(getStrFinancialYearTo(), DATE_FORMAT));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(9, uF.parseToInt(strEmpId));
			pst.setString(10, getGender());
			pst.setInt(11, uF.parseToInt(getDeductionId()));
			pst.executeUpdate();
			pst.close();
			
			request.setAttribute(MESSAGE, "Deduction updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String insertDeduction() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertDeductionIndia);
			pst.setDouble(1, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(2, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(3, uF.parseToDouble(getDeductionAmountPaycycle()));
			pst.setDouble(4, uF.parseToDouble(getDeductionAmount()));
			pst.setInt(5, uF.parseToInt(getState()));
			pst.setInt(6, uF.parseToInt(strEmpId));
			pst.setDate(7, uF.getDateFormat(getStrFinancialYearFrom(), DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(getStrFinancialYearTo(), DATE_FORMAT));
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(10, getGender());
			pst.execute();
			pst.close();

			request.setAttribute(MESSAGE, "Deduction added successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteDeduction() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteDeductionIndia);
			pst.setInt(1, uF.parseToInt(getDeductionId()));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "Deduction deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
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

	public String getDeductionId() {
		return deductionId;
	}

	public void setDeductionId(String deductionId) {
		this.deductionId = deductionId;
	}

	public String getIncomeFrom() {
		return incomeFrom;
	}

	public void setIncomeFrom(String incomeFrom) {
		this.incomeFrom = incomeFrom;
	}

	public String getIncomeTo() {
		return incomeTo;
	}

	public void setIncomeTo(String incomeTo) {
		this.incomeTo = incomeTo;
	}

	public String getDeductionAmount() {
		return deductionAmount;
	}

	public void setDeductionAmount(String deductionAmount) {
		this.deductionAmount = deductionAmount;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<FillState> getStateList() {
		return stateList;
	}

	public String getDeductionAmountPaycycle() {
		return deductionAmountPaycycle;
	}

	public void setDeductionAmountPaycycle(String deductionAmountPaycycle) {
		this.deductionAmountPaycycle = deductionAmountPaycycle;
	}

	public String getStrFinancialYearTo() {
		return strFinancialYearTo;
	}

	public void setStrFinancialYearTo(String strFinancialYearTo) {
		this.strFinancialYearTo = strFinancialYearTo;
	}

	public String getStrFinancialYearFrom() {
		return strFinancialYearFrom;
	}

	public void setStrFinancialYearFrom(String strFinancialYearFrom) {
		this.strFinancialYearFrom = strFinancialYearFrom;
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

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}


	public List<FillGender> getGenderList() {
		return genderList;
	}


	public void setGenderList(List<FillGender> genderList) {
		this.genderList = genderList;
	}
	
}