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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class PrevBonus extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null; 
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		viewBonus(uF);
			
		return loadBonus();
	}
	
	
	public String loadBonus(){
		
		return LOAD;
	}
	
	public String viewBonus(UtilityFunctions uF){
		
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
			
			pst = con.prepareStatement("select * from bonus_individual_details where emp_id = ? and is_approved=? order by pay_paycycle desc");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, 1);
			rs = pst.executeQuery();

			Map<String, String> hmBonus = new HashMap<String, String>();
			Map<String, String> hmPaycycle = new HashMap<String, String>();
			List<String> alBonus = new ArrayList<String>();
			while(rs.next()){
				hmBonus.put(rs.getString("pay_paycycle"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
				hmPaycycle.put(rs.getString("pay_paycycle"), uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat())+" to  "+uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
				alBonus.add(rs.getString("pay_paycycle"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmBonus", hmBonus);
			request.setAttribute("hmPaycycle", hmPaycycle);
			request.setAttribute("alBonus", alBonus);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and is_paid = ? and salary_head_id =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setBoolean(2, true);
			pst.setInt(3, BONUS);
			rs = pst.executeQuery();
			Map<String, String> hmPaidBonus = new HashMap<String, String>();
			while(rs.next()){
				hmPaidBonus.put(rs.getString("paycycle"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPaidBonus", hmPaidBonus);
			
			
			
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

}
