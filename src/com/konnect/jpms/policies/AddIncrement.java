package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddIncrement extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	private String incrementId;
	private String incomeFrom;
	private String incomeTo;
	private String incrementAmount;
	private String dueMonth;
	List<FillMonth> monthList;
	CommonFunctions CF; 
	String orgId;
	private static Logger log = Logger.getLogger(AddIncrement.class);
	
	public String execute() { 
		
		session = request.getSession();
		strEmpId = (String) session.getAttribute(EMPID); 
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		System.out.println("1");
		
		loadValidateIncrement();
		System.out.println("2");
		
		if (operation!=null && operation.equals("D")) {
			System.out.println("3");
			return deleteIncrement(strId);
		}
		System.out.println("4");
		if (operation!=null && operation.equals("E")) {
			return viewIncrement(strId);
		}
		System.out.println("5");
		if (getIncrementId()!=null && getIncrementId().length()>0) {
				return updateIncrement();
		}
		System.out.println("6");
		if (getIncomeFrom()!=null && getIncomeFrom().length()>0) {
				return insertIncrement();
		}
		System.out.println("7");
		
		return LOAD;
		
		
	}

	public String loadValidateIncrement() {
		monthList = new FillMonth().fillMonth();
		return LOAD;
	}

	
	public String viewIncrement(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectIncrementV);
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				setIncomeFrom(rs.getString("increment_from"));
				setIncomeTo(rs.getString("increment_to"));
				setIncrementAmount(rs.getString("increment_amount"));
				setIncrementId(rs.getString("increment_id"));
				setDueMonth(rs.getString("due_month"));
				setOrgId(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return LOAD;
	}
	
	public String updateIncrement() {
 
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		String updateIncrement = "UPDATE increment_details SET increment_from=?, increment_to=?, increment_amount=?, due_month=?, entry_date=?, user_id=? WHERE increment_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateIncrement);
			pst.setDouble(1, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(2, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(3, uF.parseToDouble(getIncrementAmount()));
			pst.setInt(4, uF.parseToInt(getDueMonth()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(7, uF.parseToInt(getIncrementId()));
			pst.execute();
			pst.close();

			session.setAttribute(MESSAGE, SUCCESSM+"Increment policy updated successfully."+END);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String insertIncrement() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertIncrement);
			pst.setDouble(1, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(2, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(3, uF.parseToDouble(getIncrementAmount()));
			pst.setInt(4, uF.parseToInt(getDueMonth()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(7, uF.parseToInt(getOrgId()));
			
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Increment policy saved successfully."+END);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteIncrement(String strId) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteIncrement);
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Increment policy deleted successfully."+END);
			
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
		if (getIncrementAmount() != null && getIncrementAmount().length() == 0) {
			addFieldError("incrementAmount", "Increment Amount is required");
		} else if (getIncrementAmount() != null && !uF.isNumber(getIncrementAmount())) {
			addFieldError("incrementAmount", "Increment Amount should be in numbers only.");
		}
		loadValidateIncrement();
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getIncrementId() {
		return incrementId;
	}

	public void setIncrementId(String incrementId) {
		this.incrementId = incrementId;
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

	public String getIncrementAmount() {
		return incrementAmount;
	}

	public void setIncrementAmount(String incrementAmount) {
		this.incrementAmount = incrementAmount;
	}

	public String getDueMonth() {
		return dueMonth;
	}

	public void setDueMonth(String dueMonth) {
		this.dueMonth = dueMonth;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

}
