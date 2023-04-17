package com.konnect.jpms.reports;

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

public class EmployeeServiceRateReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(EmployeeServiceRateReport.class);
	 
	public String execute() throws Exception {
		 
		session = request.getSession();if(session==null)return LOGIN;
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		
		request.setAttribute(PAGE, PReportEmployeeServiceRate);
		request.setAttribute(TITLE, TViewRates);
		
		
			viewEmployeeServiceRate();			
			return loadEmployeeServiceRate();

	}
	
	
	public String loadEmployeeServiceRate(){
		
		return LOAD;
	}
	
	public String viewEmployeeServiceRate(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		

		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			pst = con.prepareStatement(selectEmployeeServiceRateDetails);
			rs = pst.executeQuery();
			String strOldEmpName = null;
			String strNewEmpName = null;
			while(rs.next()){
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				
				strNewEmpName = rs.getString("emp_fname")+" "+rs.getString("emp_lname");
				alInner = new ArrayList<String>();
				if(strNewEmpName!=null && !strNewEmpName.equalsIgnoreCase(strOldEmpName)){
					alInner.add(strNewEmpName);
				}else{
					alInner.add("");
				}
				
				alInner.add(rs.getString("service_name"));
				alInner.add(uF.stringMapping(rs.getString("emptype")));
				
				if(rs.getString("paymode").equalsIgnoreCase("H")){
					alInner.add("-");
					alInner.add(strCurrency+rs.getString("monamount"));
					alInner.add(strCurrency+rs.getString("tuesamount"));
					alInner.add(strCurrency+rs.getString("wedamount"));
					alInner.add(strCurrency+rs.getString("thursamount"));
					alInner.add(strCurrency+rs.getString("friamount"));
					alInner.add(strCurrency+rs.getString("satamount"));
					alInner.add(strCurrency+rs.getString("sunamount"));
				}else{
					alInner.add(strCurrency+rs.getString("fxdamount"));
					alInner.add("-");
					alInner.add("-");
					alInner.add("-");
					alInner.add("-");
					alInner.add("-");
					alInner.add("-");
					alInner.add("-");
				}
				
				
				al.add(alInner);
				strOldEmpName = strNewEmpName;
			}
			rs.close();
			pst.close();
			
			
			
			
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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

}
