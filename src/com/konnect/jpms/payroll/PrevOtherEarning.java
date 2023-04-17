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

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PrevOtherEarning extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null; 
	
	String SHID;
	
	String strSessionEmpId = null;
	String strBaseUserType = null;
	String strAction = null;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		strSessionEmpId = (String)session.getAttribute(EMPID);//Created By Dattatray 14-06-2022
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);//Created By Dattatray 14-06-2022
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		//Created By Dattatray 14-06-2022
				strAction = request.getServletPath();
				if(strAction!=null) {
					strAction = strAction.replace("/","");
				}
				
		viewOtherEarning(uF);
		
		loadPageVisitAuditTrail();
		System.out.println(strAction);
		
		return LOAD;
	}
	//Created By Dattatray 14-06-2022
		private void loadPageVisitAuditTrail() {
			Connection con=null;
			Database db = new Database();
			db.setRequest(request);
			UtilityFunctions uF=new UtilityFunctions();
			try {
				con = db.makeConnection(con);
				Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getStrEmpId());
				StringBuilder builder = new StringBuilder();
				builder.append("Emp name  : "+hmEmpProfile.get(getStrEmpId()));
				builder.append("\nNote : Previous Variable data checked on popup ");
				
				CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally {
				db.closeConnection(con);
			}
			
		}
	
	public String viewOtherEarning(UtilityFunctions uF){
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
			pst = con.prepareStatement("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where salary_head_id=?");
			pst.setInt(1, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmSalaryHeadMap.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOtherEarning = new HashMap<String, String>();
			Map<String, String> hmPaycycle = new HashMap<String, String>();
			Map<String, String> hmSalaryHead = new HashMap<String, String>();
			List<String> alOtherEarning = new ArrayList<String>();			
			
			pst = con.prepareStatement("select * from otherearning_individual_details where emp_id = ? and is_approved=? and salary_head_id=? order by pay_paycycle desc");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setInt(2, 1);
			pst.setInt(3, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherEarning.put(rs.getString("pay_paycycle"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
				hmPaycycle.put(rs.getString("pay_paycycle"), uF.getDateFormat(rs.getString("paid_from"), DBDATE, CF.getStrReportDateFormat())+" to  "+uF.getDateFormat(rs.getString("paid_to"), DBDATE, CF.getStrReportDateFormat()));
				hmSalaryHead.put(rs.getString("pay_paycycle"),hmSalaryHeadMap.get(rs.getString("salary_head_id")));
				alOtherEarning.add(rs.getString("pay_paycycle"));   
			}
			rs.close();
			pst.close();
			request.setAttribute("hmSalaryHead", hmSalaryHead);
			request.setAttribute("hmSalaryHeadName", hmSalaryHeadMap.get(getSHID()));
			
			request.setAttribute("hmOtherEarning", hmOtherEarning);
			request.setAttribute("hmPaycycle", hmPaycycle);
			request.setAttribute("alOtherEarning", alOtherEarning);
			
			pst = con.prepareStatement("select * from payroll_generation where emp_id = ? and is_paid = ? and salary_head_id =?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setBoolean(2, true);
			pst.setInt(3, uF.parseToInt(getSHID()));
			rs = pst.executeQuery();

			Map<String, String> hmPaidOtherEarning = new HashMap<String, String>();
			while(rs.next()){
				hmPaidOtherEarning.put(rs.getString("paycycle"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmPaidOtherEarning", hmPaidOtherEarning);
			
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

}