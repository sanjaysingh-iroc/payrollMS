package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddDeduction extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	private String deductionId;
	private String incomeFrom;
	private String incomeTo;
	private String deductionAmount;
	private static Logger log = Logger.getLogger(AddDeduction.class);
	
	public String execute() {
		
		session = request.getSession();
		request.setAttribute(PAGE, PAddDeduction);
		strEmpId = (String) session.getAttribute("EMPID");
		
		session = request.getSession();
		request.setAttribute(PAGE, PAddDeduction);
		strEmpId = (String) session.getAttribute("EMPID");
		
		String operation = request.getParameter("operation");

		if(operation.equals("A"))
		{
			return insertDeduction();
		}
		else if (operation.equals("U"))
		{
			return updateDeduction();
		}
		else if (operation.equals("D"))
		{
			return deleteDeduction();
		}
		return SUCCESS;
		
	}

	public String loadValidateDeduction() {
		request.setAttribute(PAGE, PAddDeduction);
		request.setAttribute(TITLE, TAddDeduction);
		
		return LOAD;
	}

	public String updateDeduction() {
 
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		setDeductionId(request.getParameter("id"));
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			case 0 : columnName = "income_from"; break;
			case 1 : columnName = "income_to"; break;
			case 2 : columnName = "deduction_amount"; break;
		}
		String updateAllowance = "UPDATE deduction_details SET "+columnName+"=? WHERE deduction_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateAllowance);
			if(columnId==0 || columnId==1 || columnId==2){
				pst.setDouble(1, uF.parseToDouble(request.getParameter("value")));
				pst.setInt(2, uF.parseToInt(request.getParameter("id")));
				pst.execute();
				pst.close();
			}
			request.setAttribute(MESSAGE, "Deduction updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}

	public String insertDeduction() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertDeduction);
			pst.setDouble(1, uF.parseToDouble(getIncomeFrom()));
			pst.setDouble(2, uF.parseToDouble(getIncomeTo()));
			pst.setDouble(3, uF.parseToDouble(getDeductionAmount()));
			pst.setInt(4, uF.parseToInt(strEmpId));

			log.debug("pst 1 =" + pst);

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
			pst = con.prepareStatement(deleteDeduction);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
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

}
