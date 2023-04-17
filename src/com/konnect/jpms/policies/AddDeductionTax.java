package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddDeductionTax extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	CommonFunctions CF;
	
	private String deductionId;
	private String gender;
	private String ageFrom;
	private String ageTo; 
	private String incomeFrom;
	private String incomeTo; 
	private String deductionAmount;
	private String deductionType;
	private String slabType;
	private List<FillGender> genderList;
	private String financialYear;
	private String strFinancialYearTo;
	private String strFinancialYearFrom;

	private static Logger log = Logger.getLogger(AddDeductionTax.class);
	
	private String operation;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() {
		
		session = request.getSession();
		request.setAttribute(PAGE, PAddDeductionTax);
		strEmpId = (String) session.getAttribute("EMPID");
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		if(operation != null && operation.equals("A")) {
			return insertDeduction();
		} else if (operation != null && operation.equals("E")) {
			return viewDeduction();
		} else if (operation != null && operation.equals("U")) {
			return updateDeduction();
		} else if (operation != null && operation.equals("D")) {
			return deleteDeduction();
		}
		if(operation == null || operation.equals("")) {
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
			pst = con.prepareStatement("select * from deduction_tax_details WHERE deduction_tax_id=?");
			pst.setInt(1, uF.parseToInt(getDeductionId()));
			rs = pst.executeQuery();
//			System.out.println("pst =====>> " + pst);
			while(rs.next()) {
				setAgeFrom(rs.getString("age_from"));
				setAgeTo(rs.getString("age_to"));
				setGender(rs.getString("gender"));
				setIncomeFrom(rs.getString("_from"));
				setIncomeTo(rs.getString("_to"));
				setDeductionAmount(rs.getString("deduction_amount"));
				setDeductionType(rs.getString("deduction_type"));
				setSlabType(rs.getString("slab_type"));
				setOperation("U");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;

	}

	public String loadValidateDeduction() {
		request.setAttribute(PAGE, PAddDeductionTax);
		request.setAttribute(TITLE, TAddDeduction);
		
		genderList = new FillGender().fillGender();
		return LOAD;
	}

	
	public String updateDeduction() {
 
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("update deduction_tax_details set age_from=?, age_to=?, gender=?, _from=?, _to=?, deduction_amount=?, " +
					"deduction_type=?, slab_type = ?, entry_date=?, user_id=? where deduction_tax_id=?");
			pst.setInt(1, uF.parseToInt(getAgeFrom()));
			pst.setInt(2, uF.parseToInt(getAgeTo()));
			pst.setString(3, getGender());
			pst.setDouble(4, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(5, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(6, uF.parseToDouble(getDeductionAmount()));
			pst.setString(7, getDeductionType());
			pst.setInt(8, uF.parseToInt(getSlabType()));
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(10, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(11, uF.parseToInt(getDeductionId()));
			pst.execute();
			pst.close();
			
			//System.out.println("pst="+pst);
			
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
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().equals("") && !getFinancialYear().equals("null")) {
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			}

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertDeductionTax);
			pst.setInt(1, uF.parseToInt(getAgeFrom()));
			pst.setInt(2, uF.parseToInt(getAgeTo()));
			pst.setString(3, getGender());
			pst.setDouble(4, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(5, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(6, uF.parseToDouble(getDeductionAmount()));
			pst.setString(7, getDeductionType());
			pst.setDate(8, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(9, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(12, uF.parseToInt(getSlabType()));
			pst.execute();
			pst.close();
			
	//		System.out.println("pst===="+pst);

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
			pst = con.prepareStatement(deleteDeductionTax);
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

	
	public void validate() {
		UtilityFunctions uF = new UtilityFunctions();
		
		if (getIncomeFrom() != null && getIncomeFrom().length() == 0) {
			addFieldError("incomeFrom", "Income From is required");
		} else if (getIncomeFrom() != null && !uF.isNumber(getIncomeFrom())) {
			addFieldError("incomeFrom", "Income From should be in numbers only.");
		}
		if (getIncomeTo() != null && getIncomeTo().length() == 0) {
			addFieldError("incomeTo", "Income To is required");
		} else if (getIncomeTo() != null && !uF.isNumber(getIncomeTo())) {
			addFieldError("incomeTo", "Income To should be in numbers only.");
		}
		if (getDeductionAmount() != null && getDeductionAmount().length() == 0) {
			addFieldError("deductionAmount", "Deduction Amount is required");
		} else if (getDeductionAmount() != null && !uF.isNumber(getDeductionAmount())) {
			addFieldError("deductionAmount", "Deduction Amount should be in numbers only.");
		}
		loadValidateDeduction();
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAgeFrom() {
		return ageFrom;
	}

	public void setAgeFrom(String ageFrom) {
		this.ageFrom = ageFrom;
	}

	public String getAgeTo() {
		return ageTo;
	}

	public void setAgeTo(String ageTo) {
		this.ageTo = ageTo;
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

	public List<FillGender> getGenderList() {
		return genderList;
	}

	public String getDeductionType() {
		return deductionType;
	}

	public void setDeductionType(String deductionType) {
		this.deductionType = deductionType;
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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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

	public String getSlabType() {
		return slabType;
	}

	public void setSlabType(String slabType) {
		this.slabType = slabType;
	}



	

	

}