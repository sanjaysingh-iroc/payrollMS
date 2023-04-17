package com.konnect.jpms.policies;

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

public class UpdateAnnualVariablePolicy extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType = null;
	String strSessionEmpId = null;
	private String strBaseUserType;
	private String strAction = null;
	
	String levelId;
	String salaryHeadId;
	String amount; 
	String strOrg;
	String financialYear;
	String empId;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strSessionEmpId = (String)session.getAttribute(EMPID);//Created By Dattatray 13-6-2022
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);//Created By Dattatray 13-6-2022
		
		UtilityFunctions uF = new UtilityFunctions();
		//Created By Dattatray 13-6-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}
		
		updateAnnualVariablePolicy(uF);
		return SUCCESS;
	}  

	//Created By Dattatray 13-6-2022
	private void loadPageVisitAuditTrail(UtilityFunctions uF) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getEmpId());
			StringBuilder builder = new StringBuilder();
			if(uF.parseToInt(getEmpId())>0) {
				builder.append("Emp name : "+hmEmpProfile.get(getEmpId()));
				builder.append("\nAmmount updated : "+uF.parseToDouble(getAmount()));
				
			}
			
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
	}
	
	public String updateAnnualVariablePolicy(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				String[] strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}

			con = db.makeConnection(con);
			pst = con.prepareStatement("update annual_variable_details set variable_amount=?,updated_by=?,updated_date=? " +
					"where salary_head_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? and emp_id=?");
			pst.setDouble(1, uF.parseToDouble(getAmount()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(getSalaryHeadId()));
			pst.setInt(5, uF.parseToInt(getLevelId()));
			pst.setInt(6, uF.parseToInt(getStrOrg()));
			pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(9, uF.parseToInt(getEmpId()));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x == 0){
				pst = con.prepareStatement("insert into annual_variable_details(salary_head_id,variable_amount,level_id,org_id," +
						"financial_year_start,financial_year_end,added_by,added_date,emp_id) values(?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, uF.parseToInt(getSalaryHeadId()));
				pst.setDouble(2, uF.parseToDouble(getAmount()));
				pst.setInt(3, uF.parseToInt(getLevelId()));
				pst.setInt(4, uF.parseToInt(getStrOrg()));
				pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(7, uF.parseToInt(strSessionEmpId));
				pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(9, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
			} 
			
			pst = con.prepareStatement("select variable_amount from annual_variable_details where salary_head_id=? and level_id=? and org_id=? " +
					"and financial_year_start=? and financial_year_end=? and emp_id=?");
			pst.setInt(1, uF.parseToInt(getSalaryHeadId()));
			pst.setInt(2, uF.parseToInt(getLevelId()));
			pst.setInt(3, uF.parseToInt(getStrOrg()));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getEmpId()));
			rs =pst.executeQuery();
			while(rs.next()) {
				request.setAttribute("AMOUNT", rs.getString("variable_amount"));
			}
			rs.close();
			pst.close();
			loadPageVisitAuditTrail(uF);//Created by dattatray 13-06-2022			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}
}