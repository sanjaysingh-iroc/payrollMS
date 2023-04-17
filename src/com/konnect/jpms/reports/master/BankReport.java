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

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BankReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(BankReport.class);
	
	HttpSession session;
	CommonFunctions CF;
	
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, PBank);
		request.setAttribute(TITLE, TBank);
		session = request.getSession();
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null){
			return LOGIN;
		} 
		
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		viewBank();			
		return loadBank();

	}
	
	
	public String loadBank(){	
		return "load";
	}
	
	public String viewBank(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			List<String> alInner1 = new ArrayList<String>();
			Map hmBankReport = new HashMap();
			Map hmBankReport1 = new HashMap();
			
			
			con = db.makeConnection(con);
			
			
//			Map hmCountryMap = CF.getCountryMap(con);
			Map<String, String> hmStateMap = CF.getStateMap(con);
			
			pst = con.prepareStatement(selectBank);
			rs = pst.executeQuery();
			
			int nBankIdNew = 0;
			int nBankIdOld = 0;
			
			while(rs.next()){
				
				nBankIdNew = rs.getInt("bank_id");
				if(nBankIdNew>0 && nBankIdNew!=nBankIdOld){
					alInner1 = new ArrayList<String>();
				}
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("bank_id"));
				
				
				alInner.add(rs.getString("bank_name"));
				alInner.add(rs.getString("bank_code"));
				alInner.add(rs.getString("bank_address"));
				
				alInner.add(rs.getString("bank_branch"));
				alInner.add(rs.getString("bank_city"));
				alInner.add(uF.showData(hmStateMap.get(rs.getString("bank_state_id")), "-"));
				
				alInner.add(rs.getString("bank_account_no"));
				
				
				
				alInner1.add(rs.getString("bank_branch")+" 1");
				alInner1.add(rs.getString("bank_code")+" 2");
				alInner1.add(rs.getString("bank_branch")+" 3");
				alInner1.add(rs.getString("bank_code")+" 4");
				alInner1.add(rs.getString("bank_branch")+" 5");
				alInner1.add(rs.getString("bank_code")+" 6");
				alInner1.add(rs.getString("bank_branch")+" 7");
				alInner1.add(rs.getString("bank_code")+" 8");
				
				
				
				al.add(alInner);
				                                    
				
				hmBankReport.put(rs.getString("bank_id"), alInner);
				
				
				hmBankReport1.put(rs.getString("bank_id"), alInner1);
				nBankIdOld = nBankIdNew;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", al);
			request.setAttribute("hmBankReport", hmBankReport);
			request.setAttribute("hmBankReport1", hmBankReport1);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
			return ERROR;
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
