package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAmountType;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddIncrementDA extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	private String incrementId;
	private String incomeFrom;
	private String incomeTo;
	private String incrementAmount;
	private String []dueMonth;
	List<FillMonth> monthList;
	List<FillAmountType> amountTypeList;
	String strAmountType;
	
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(AddIncrementDA.class);
	
	public String execute() { 
		 
		session = request.getSession();
		strEmpId = (String) session.getAttribute(EMPID); 
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");
		
		
		loadValidateIncrement();
		
		if (operation!=null && operation.equals("D")) {
			return deleteIncrement(strId);
		}
		if (operation!=null && operation.equals("E")) {
			return viewIncrement(strId);
		}
		
		if (getIncrementId()!=null && getIncrementId().length()>0) {
				return updateIncrement();
		}
		if (getIncomeFrom()!=null && getIncomeFrom().length()>0) {
				return insertIncrement();
		}
		
		
		return LOAD;
		
		
	}

	public String loadValidateIncrement() {
		monthList = new FillMonth().fillMonth();
		amountTypeList = new FillAmountType().fillAmountType1();
		
		
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
			pst = con.prepareStatement(selectIncrementDAV);
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				setIncomeFrom(rs.getString("increment_from"));
				setIncomeTo(rs.getString("increment_to"));
				setIncrementAmount(rs.getString("increment_amount"));
				setStrAmountType(rs.getString("increment_amount_type"));
				setIncrementId(rs.getString("increment_id"));
				
				String strMonth[] = rs.getString("due_month").split(",");
				
				setDueMonth(strMonth);
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

		String updateIncrement = "UPDATE increment_details_da SET increment_from=?, increment_to=?, increment_amount=?, increment_amount_type=?, due_month=?, entry_date=?, user_id=? WHERE increment_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateIncrement);
			pst.setDouble(1, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(2, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(3, uF.parseToDouble(getIncrementAmount()));
			pst.setString(4, getStrAmountType());
			
			StringBuilder sb = new StringBuilder();
			for(int i=0; getDueMonth()!=null && i<getDueMonth().length;i++){
				sb.append(getDueMonth()[i]+",");
			}
			
			pst.setString(5, sb.toString());
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(8, uF.parseToInt(getIncrementId()));
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
			pst = con.prepareStatement(insertIncrementDA);
			pst.setDouble(1, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(2, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(3, uF.parseToDouble(getIncrementAmount()));
			pst.setString(4, getStrAmountType());
			StringBuilder sb = new StringBuilder();
			for(int i=0; getDueMonth()!=null && i<getDueMonth().length;i++){
				sb.append(getDueMonth()[i]+",");
			}
			
			pst.setString(5, sb.toString());
			pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
			
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
			pst = con.prepareStatement(deleteIncrementDA);
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

	public String[] getDueMonth() {
		return dueMonth;
	}

	public void setDueMonth(String []dueMonth) {
		this.dueMonth = dueMonth;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillAmountType> getAmountTypeList() {
		return amountTypeList;
	}

	public void setAmountTypeList(List<FillAmountType> amountTypeList) {
		this.amountTypeList = amountTypeList;
	}

	public String getStrAmountType() {
		return strAmountType;
	}

	public void setStrAmountType(String strAmountType) {
		this.strAmountType = strAmountType;
	}

}
