package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class AddGratuity extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	
	public String execute() throws Exception {

		String operation = request.getParameter("operation");
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		if (operation.equals("D")) {
			return deleteGratuity();
		}
		if (operation.equals("U")) { 
				return updateGratuity();
		}
		if (operation.equals("A")) {
				return insertGratuity();
		}
		
		return SUCCESS;
		
	}

	public String loadValidateGratuity() {
		return LOAD;
	}

	public String insertGratuity() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(insertGratuity);
			
			pst.setDouble(1, uF.parseToDouble(getStrServiceFrom()));
			pst.setDouble(2, uF.parseToDouble(getStrServiceTo()));
			pst.setInt(3, uF.parseToInt(getStrGratuityDays()));
			pst.setDouble(4, uF.parseToDouble(getStrMaxGratuityAmount()));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(6, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.execute();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
	public String updateGratuity() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int columnId = Integer.parseInt(request.getParameter("columnId"));
		String columnName=null;
		
		switch(columnId) {
			
			case 0 : columnName = "service_from"; break;
			case 1 : columnName = "service_to"; break;
			case 2 : columnName = "gratuity_days"; break;
			case 3 : columnName = "max_amount" ; break;
			case 4 : columnName = "salary_cal_basis" ; break;
			
		}
		String updateGratuity = "UPDATE gratuity_details SET "+columnName+"=?, entry_date=?, user_id=? WHERE gratuity_id=?";
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(updateGratuity);
			if(columnId==0 || columnId==1 || columnId==3){
				pst.setDouble(1, uF.parseToDouble(request.getParameter("value")));
			} else if(columnId==2) { 
				pst.setInt(1, uF.parseToInt(request.getParameter("value")));
			} else if(columnId==4) {
				String val = (String)request.getParameter("value");
				pst.setString(1, val!=null ? val.trim() : "");
			}
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(4, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace(); 
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return UPDATE;

	}
	
	public String deleteGratuity() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteGratuity);
			pst.setInt(1, uF.parseToInt(request.getParameter("id")));
			pst.execute();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	String strServiceFrom;
	String strServiceTo;
	String strGratuityDays;
	String strMaxGratuityAmount;
	
	
	public void validate() {
		loadValidateGratuity();

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public String getStrServiceFrom() {
		return strServiceFrom;
	}

	public void setStrServiceFrom(String strServiceFrom) {
		this.strServiceFrom = strServiceFrom;
	}

	public String getStrServiceTo() {
		return strServiceTo;
	}

	public void setStrServiceTo(String strServiceTo) {
		this.strServiceTo = strServiceTo;
	}

	public String getStrGratuityDays() {
		return strGratuityDays;
	}

	public void setStrGratuityDays(String strGratuityDays) {
		this.strGratuityDays = strGratuityDays;
	}

	public String getStrMaxGratuityAmount() {
		return strMaxGratuityAmount;
	}

	public void setStrMaxGratuityAmount(String strMaxGratuityAmount) {
		this.strMaxGratuityAmount = strMaxGratuityAmount;
	}
}