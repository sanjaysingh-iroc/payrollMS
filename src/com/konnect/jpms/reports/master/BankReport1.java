package com.konnect.jpms.reports.master;

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

import com.konnect.jpms.select.FillBank;
import com.konnect.jpms.select.FillCountry;
import com.konnect.jpms.select.FillCurrency;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillTimezones;
import com.konnect.jpms.select.FillWeekDays;
import com.konnect.jpms.select.FillWlocationType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BankReport1 extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	private List<FillCountry> countryList;
	private List<FillState> stateList;
	private List<FillTimezones> timezoneList;
	private List<FillWlocationType> wlocationTypeList;
	private List<FillWeekDays> weeklyOffList;
//	List<FillCity> cityList; 
	private List<FillBank> bankList;
	private List<FillCurrency> currencyList;
	 
	HttpSession session;
	CommonFunctions CF;
	private static Logger log = Logger.getLogger(BankReport1.class);
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/reports/master/BankBranchReport.jsp");
		request.setAttribute(TITLE, "Banks");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		}
		
		
		viewWLocation();			
		return LOAD;
	}
	
	public String viewWLocation(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			
			List<String> alInner = new ArrayList<String>();
			Map<String, List<String>> hmBankData = new HashMap<String, List<String>>();
			Map<String, List<String>> hmBankBranchData = new HashMap<String, List<String>>();
			Map<String, String> hmState = CF.getStateMap(con);
						
			pst = con.prepareStatement("select * from bank_details");
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("bank_id"));
				alInner.add(rs.getString("bank_name"));
				alInner.add(rs.getString("bank_code"));
			
				hmBankData.put(rs.getString("bank_id"), alInner);
				
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement("select *, bd.bank_id as bankid from bank_details bd1 left join branch_details bd on bd.bank_id = bd1.bank_id");
			pst = con.prepareStatement("select * from branch_details order by bank_id, bank_branch");
			rs = pst.executeQuery();
			String strBankIdOld = null;
			String strBankIdNew = null;
			
			while(rs.next()){
				strBankIdNew = rs.getString("bank_id");
				if(strBankIdNew!=null && !strBankIdNew.equalsIgnoreCase(strBankIdOld)){
					alInner = new ArrayList<String>();
				}
				
				
				alInner.add(rs.getString("branch_id"));//0
				alInner.add(uF.showData(rs.getString("bank_branch"), ""));//1
				alInner.add(uF.showData(rs.getString("branch_code"), ""));//2
				alInner.add(uF.showData(rs.getString("bank_address"), ""));//3
				alInner.add(uF.showData(rs.getString("bank_city"), ""));//4
				alInner.add(uF.showData(hmState.get(rs.getString("bank_state_id")), ""));//5
				alInner.add(uF.showData(rs.getString("bank_account_no"), "N/a"));//6
				
				String branchCode = uF.showData("["+rs.getString("branch_code")+"]", "");
				boolean flag  = getBankUsedStatus(con,branchCode);
				alInner.add(""+flag);//7
				
				hmBankBranchData.put(rs.getString("bank_id"), alInner);
				strBankIdOld = strBankIdNew;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmBankBranchData", hmBankBranchData);
			request.setAttribute("hmBankData", hmBankData);
			
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
	
public boolean getBankUsedStatus(Connection con,String branchCode){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		boolean flag = false;
		try {
			
			pst = con.prepareStatement("select * from document_comm_details where document_name like '%"+branchCode+"%'");
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
		
			while(rs.next()){
				flag = true;
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillCurrency> getCurrencyList() {
		return currencyList;
	}

	public void setCurrencyList(List<FillCurrency> currencyList) {
		this.currencyList = currencyList;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	
}
