package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddHRA extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	
	String strEmpId;
	String strHRAId;
	String strCond1;
	String strCond2;
	String strCond3;
	String strFinancialYearFrom;
	String strFinancialYearTo;
	private static Logger log = Logger.getLogger(AddHRA.class);
	CommonFunctions CF; 
	
	public String execute() {
		
		session = request.getSession();
		request.setAttribute(PAGE, PAddDeduction);
		strEmpId = (String) session.getAttribute("EMPID");
		
		session = request.getSession();
		request.setAttribute(PAGE, PAddDeduction);
		strEmpId = (String) session.getAttribute("EMPID");
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		
		String operation = request.getParameter("operation");

		if(operation.equals("A"))
		{
			return insertHRA();
		}
		else if (operation.equals("U"))
		{
			return updateHRA();
		}
		else if (operation.equals("D"))
		{
			return deleteHRA();
		}
		return SUCCESS;
		
	}

	public String loadValidateHRA() {
		return LOAD;
	}

	public String updateHRA() {
 
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		setStrHRAId(request.getParameter("id"));
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			case 0 : columnName = "condition1"; break;
			case 1 : columnName = "condition2"; break;
			case 2 : columnName = "condition3"; break;
			case 3 : columnName = "financial_year_from"; break;
			case 4 : columnName = "financial_year_to"; break;
			
		}
		String updateAllowance = "UPDATE hra_exemption_details SET "+columnName+"=?,entry_date=?, user_id=? WHERE hra_id=?";
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateAllowance);
			if(columnId==0 || columnId==1 || columnId==2){
				pst.setDouble(1, uF.parseToDouble(request.getParameter("value")));
			}
			
			if(columnId==3 || columnId==4){
				pst.setDate(1, uF.getDateFormat(request.getParameter("value"), DATE_FORMAT));
			}
			
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(4, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			
			request.setAttribute(MESSAGE, "HRA updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}

	public String insertHRA() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(insertHRA);
			pst.setDouble(1, uF.parseToDouble(getStrCond1()));
			pst.setString(2, "P");
			pst.setDouble(3, uF.parseToDouble(getStrCond2()));
			pst.setString(4, "P");
			pst.setDouble(5, uF.parseToDouble(getStrCond3()));
			pst.setString(6, "P");
			pst.setDate(7, uF.getDateFormat(getStrFinancialYearFrom(), DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(getStrFinancialYearTo(), DATE_FORMAT));

			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(10, uF.parseToInt((String)session.getAttribute(EMPID)));
			
			pst.execute();
			pst.close();

			request.setAttribute(MESSAGE, "HRA added successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	public String deleteHRA() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteHRA);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			request.setAttribute(MESSAGE, "HRA policy deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;
	}

	public void validate() {
		
		loadValidateHRA();
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrCond1() {
		return strCond1;
	}

	public void setStrCond1(String strCond1) {
		this.strCond1 = strCond1;
	}

	public String getStrCond2() {
		return strCond2;
	}

	public void setStrCond2(String strCond2) {
		this.strCond2 = strCond2;
	}

	public String getStrCond3() {
		return strCond3;
	}

	public void setStrCond3(String strCond3) {
		this.strCond3 = strCond3;
	}

	public String getStrFinancialYearFrom() {
		return strFinancialYearFrom;
	}

	public void setStrFinancialYearFrom(String strFinancialYearFrom) {
		this.strFinancialYearFrom = strFinancialYearFrom;
	}

	public String getStrFinancialYearTo() {
		return strFinancialYearTo;
	}

	public void setStrFinancialYearTo(String strFinancialYearTo) {
		this.strFinancialYearTo = strFinancialYearTo;
	}

	public String getStrHRAId() {
		return strHRAId;
	}

	public void setStrHRAId(String strHRAId) {
		this.strHRAId = strHRAId;
	}



}
