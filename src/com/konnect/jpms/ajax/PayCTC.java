package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PayCTC extends ActionSupport implements IStatements,
		ServletRequestAware {

	

	

	private String empId;
	private String amount;
	private String salaryHead;
	String strSessionEmpId;  
	String strActualAmount;
	public String getStrActualAmount() {
		return strActualAmount;
	}

	public void setStrActualAmount(String strActualAmount) {
		this.strActualAmount = strActualAmount;
	}

	public String getSalaryHead() {
		return salaryHead;
	}

	public void setSalaryHead(String salaryHead) {
		this.salaryHead = salaryHead;
	}



	HttpSession session;
	CommonFunctions CF;

	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strSessionEmpId = (String) session.getAttribute(EMPID);
		setGratuityAmount();

		return SUCCESS;

	}

	public void setGratuityAmount() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		if (uF.parseToDouble(strActualAmount) < uF.parseToDouble(getAmount())) {
			return;
		}

		try {

			if (getAmount() != null && uF.parseToInt(getAmount()) > 0) {
//				CF.getCurrentPayCycle(CF.getStrTimeZone(), strCurrentDate, CF);
				con = db.makeConnection(con);
				
				

				pst = con.prepareStatement("insert into ctc_salary_head(opening_amount,paid_amount,emp_id,entry_date,user_id," +
						"salary_head_id)values(?,?,?,?,?,?)");
				
				pst.setDouble(1, uF.parseToDouble(getStrActualAmount()));
				pst.setDouble(2, uF.parseToDouble(getAmount()));
				
				pst.setInt(3, uF.parseToInt(getEmpId()));
				pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setInt(6, uF.parseToInt(getSalaryHead()));
				pst.execute();
	            pst.close();
			}

			request.setAttribute("GratuityAmount", getAmount());
			request.setAttribute("EMPID", getEmpId());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}



	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

}