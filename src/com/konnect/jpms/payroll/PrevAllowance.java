package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillProductionLine;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PrevAllowance extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId;
	String strUserType = null;
	CommonFunctions CF = null; 
	
	String SHID;
	String productionLineId;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		boolean isProductionLine = CF.getIsProductionLine();
		request.setAttribute("isProductionLine", ""+isProductionLine);
		
		if(isProductionLine){
			viewAllowanceWithProductionLine(uF);
		} else {
			viewAllowance(uF);
		}		
		return LOAD;
	}
	
	private void viewAllowanceWithProductionLine(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(getStrEmpId())) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(getStrEmpId()));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			request.setAttribute("strCurrency",strCurrency); 
			
			String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());
			
			pst = con.prepareStatement("select * FROM production_line_details where production_line_id=? and org_id=? " +
					"and production_line_id in (select production_line_id from production_line_heads where level_id=? " +
					"and salary_heads like '%,"+getSHID()+",%')");
			pst.setInt(1, uF.parseToInt(getProductionLineId()));
			pst.setInt(2, uF.parseToInt(strEmpOrgId));
			pst.setInt(3, uF.parseToInt(strEmpLevelId));
			rs = pst.executeQuery();
			boolean isSalaryHeadProdLine = false;
			while (rs.next()) {
				request.setAttribute("ProductionLineName", uF.showData(rs.getString("production_line_name"), ""));
				isSalaryHeadProdLine = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("isSalaryHeadProdLine", ""+isSalaryHeadProdLine);
			
			Map<String, String> hmSalaryHeadMap = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where salary_head_id=?");
			pst.setInt(1, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmSalaryHeadMap.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmAllowance = new HashMap<String, String>();
			Map<String, String> hmPaycycle = new HashMap<String, String>();
			Map<String, String> hmSalaryHead = new HashMap<String, String>();
			List<String> alAllowance = new ArrayList<String>();
			Map<String, String> hmAllowanceStatus = new HashMap<String, String>();
			
			if(isSalaryHeadProdLine){
				pst = con.prepareStatement("select * from allowance_individual_details where emp_id = ? and is_approved=? " +
						"and salary_head_id=? and production_line_id=? order by pay_paycycle desc");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, 1);
				pst.setInt(3, uF.parseToInt(getSHID()));
				pst.setInt(4, uF.parseToInt(getProductionLineId()));
			} else {
				pst = con.prepareStatement("select * from allowance_individual_details where emp_id = ? and is_approved=? " +
						"and salary_head_id=? order by pay_paycycle desc");
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
				pst.setInt(2, 1);
				pst.setInt(3, uF.parseToInt(getSHID()));
			}
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmAllowance.put(rs.getString("pay_paycycle"), rs.getString("pay_amount"));
				hmPaycycle.put(rs.getString("pay_paycycle"), uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat())+" to  "+uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
				hmSalaryHead.put(rs.getString("pay_paycycle"),hmSalaryHeadMap.get(rs.getString("salary_head_id")));
				hmAllowanceStatus.put(rs.getString("pay_paycycle"), rs.getString("is_approved"));
				
				alAllowance.add(rs.getString("pay_paycycle"));   
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSalaryHead", hmSalaryHead);
			request.setAttribute("hmSalaryHeadName", hmSalaryHeadMap.get(getSHID()));
			
			request.setAttribute("hmAllowance", hmAllowance);
			request.setAttribute("hmPaycycle", hmPaycycle);
			request.setAttribute("alAllowance", alAllowance);
			request.setAttribute("hmAllowanceStatus", hmAllowanceStatus);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and is_paid = ? and salary_head_id =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setBoolean(2, true);
			pst.setInt(3, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();
			Map<String, String> hmPaidAllowance = new HashMap<String, String>();
			while(rs.next()){
				hmPaidAllowance.put(rs.getString("paycycle"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPaidAllowance", hmPaidAllowance);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String viewAllowance(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(getStrEmpId())) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(getStrEmpId()));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			request.setAttribute("strCurrency",strCurrency); 
			
			Map<String, String> hmSalaryHeadMap = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT distinct(salary_head_id),salary_head_name FROM salary_details");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmSalaryHeadMap.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmAllowance = new HashMap<String, String>();
			Map<String, String> hmPaycycle = new HashMap<String, String>();
			Map<String, String> hmSalaryHead = new HashMap<String, String>();
			List<String> alAllowance = new ArrayList<String>();			
			
			pst = con.prepareStatement("select * from allowance_individual_details where emp_id = ? and is_approved=? and salary_head_id=? order by pay_paycycle desc");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, 1);
			pst.setInt(3, uF.parseToInt(getSHID()));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmAllowance.put(rs.getString("pay_paycycle"), rs.getString("pay_amount"));
				hmPaycycle.put(rs.getString("pay_paycycle"), uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat())+" to  "+uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
				hmSalaryHead.put(rs.getString("pay_paycycle"),hmSalaryHeadMap.get(rs.getString("salary_head_id")));
				alAllowance.add(rs.getString("pay_paycycle"));   
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSalaryHead", hmSalaryHead);
			request.setAttribute("hmSalaryHeadName", hmSalaryHeadMap.get(getSHID()));
			
			request.setAttribute("hmAllowance", hmAllowance);
			request.setAttribute("hmPaycycle", hmPaycycle);
			request.setAttribute("alAllowance", alAllowance);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and is_paid = ? and salary_head_id =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setBoolean(2, true);
			pst.setInt(3, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();

			Map<String, String> hmPaidAllowance = new HashMap<String, String>();
			while(rs.next()){
				hmPaidAllowance.put(rs.getString("paycycle"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPaidAllowance", hmPaidAllowance);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getSHID() {
		return SHID;
	}

	public void setSHID(String sHID) {
		SHID = sHID;
	}

	public String getProductionLineId() {
		return productionLineId;
	}

	public void setProductionLineId(String productionLineId) {
		this.productionLineId = productionLineId;
	}	
}