package com.konnect.jpms.task.tax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TaxAndBillingSetting extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	
	List<FillOrganisation> orgList;
	String strOrg;

	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/task/tax/TaxAndBillingSetting.jsp");
		request.setAttribute(TITLE, "Tax & Billing Setting");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		orgList = new FillOrganisation(request).fillOrganisation();
		if(getStrOrg()==null && orgList!=null && orgList.size()>0){
			setStrOrg((String)session.getAttribute(ORGID));
		}
		
		viewTaxSettings(uF);
		viewBillingHeadSettings(uF);
		viewActualCostCalculation(uF);
//		viewProjectCategory(uF);
		viewForcedTaskSetting(uF);
		getSelectedFilter(uF);
		
		return SUCCESS;
 
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
//				for(int j=0;j<getF_sbu().length;j++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
//				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	   
	private void viewForcedTaskSetting(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select tts.*,od.org_name,od.org_code from task_type_setting tts, org_details od where tts.org_id = od.org_id ");
			if(uF.parseToInt(getStrOrg()) > 0) {
				sbQuery.append(" and tts.org_id = "+uF.parseToInt(getStrOrg())+" ");
			}
			sbQuery.append("order by org_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, List<String>> hmOrgForcedTask = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("task_type_setting_id"));
				alInner.add(CF.getOrgNameById(con, rs.getString("org_id")));
				alInner.add(rs.getString("forced_task"));
				alInner.add(rs.getString("task_request_autoapproved"));
				alInner.add(CF.getEmpNameMapByEmpId(con, rs.getString("added_by")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			
				hmOrgForcedTask.put(rs.getString("org_id"), alInner);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hProjectRateMap ===>>> " + hProjectRateMap);
			request.setAttribute("hmOrgForcedTask", hmOrgForcedTask);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private String viewActualCostCalculation(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ccs.*,od.org_name,od.org_code from cost_calculation_settings ccs, org_details od where ccs.org_id = od.org_id ");
			if(uF.parseToInt(getStrOrg()) > 0) {
				sbQuery.append(" and ccs.org_id = "+uF.parseToInt(getStrOrg())+" ");
			}
			sbQuery.append("order by org_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, List<String>> hmOrgCostCalData = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("cost_calculation_id"));
				alInner.add(CF.getOrgNameById(con, rs.getString("org_id")));
				alInner.add(rs.getString("calculation_type_label"));
				alInner.add(rs.getString("calculation_type")); //3
				alInner.add(rs.getString("days")); //4
				alInner.add(CF.getEmpNameMapByEmpId(con, rs.getString("added_by"))); //5
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat())); //6
				
				hmOrgCostCalData.put(rs.getString("org_id"), alInner);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hProjectRateMap ===>>> " + hProjectRateMap);
			request.setAttribute("hmOrgCostCalData", hmOrgCostCalData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}


	private String viewBillingHeadSettings(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			Map<String, List<List<String>>> hmBillingHead = new LinkedHashMap<String, List<List<String>>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select bhs.*,od.org_name,od.org_code from billing_head_setting bhs, org_details od where bhs.org_id = od.org_id ");
			if(uF.parseToInt(getStrOrg()) > 0) {
				sbQuery.append(" and bhs.org_id = "+uF.parseToInt(getStrOrg())+" ");
			}
			sbQuery.append("order by head_label");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmBillOrgName = new HashMap<String, String>();
			List<List<String>> alTaxHeads = new ArrayList<List<String>>();
			Map<String, String> hmBHDataType = uF.getBillingHeadDataType();
			Map<String, String> hmBHOtherVariables = uF.getBillingHeadOtherVariables();
			while(rs.next()) {
				alTaxHeads = hmBillingHead.get(rs.getString("org_id"));
				if(alTaxHeads == null) alTaxHeads = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("billing_head_id"));
				alInner.add(rs.getString("head_label"));
				
				alInner.add(hmBHDataType.get(rs.getString("head_data_type")));
				alInner.add(uF.showData(hmBHOtherVariables.get(rs.getString("head_other_variable")), "-"));
				alInner.add(rs.getString("org_id"));
				alTaxHeads.add(alInner);
				
				hmBillOrgName.put(rs.getString("org_id"), rs.getString("org_name") + " [" +rs.getString("org_code") +"]");
				
				hmBillingHead.put(rs.getString("org_id"), alTaxHeads);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hProjectRateMap ===>>> " + hProjectRateMap);
			
			request.setAttribute("hmBillOrgName", hmBillOrgName);
			request.setAttribute("hmBillingHead", hmBillingHead);
//			request.setAttribute("hmEmpGradeMap", hmEmpGradeMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
		
	}


	public String viewTaxSettings(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
//			List<String> alInner = new ArrayList<String>();
			
			Map<String, List<List<String>>> hmTaxHead = new LinkedHashMap<String, List<List<String>>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ts.*,od.org_name,od.org_code from tax_setting ts, org_details od where ts.org_id = od.org_id ");
			if(uF.parseToInt(getStrOrg()) > 0) {
				sbQuery.append(" and ts.org_id = "+uF.parseToInt(getStrOrg())+" ");
			}
			sbQuery.append(" order by tax_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmOrgName = new HashMap<String, String>();
			List<List<String>> alTaxHeads = new ArrayList<List<String>>();
			Map<String, String> hmDeductionType = uF.getTaxDeductionType();
			while(rs.next()) {
				alTaxHeads = hmTaxHead.get(rs.getString("org_id"));
				if(alTaxHeads == null) alTaxHeads = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("tax_setting_id"));
				alInner.add(rs.getString("tax_name"));
				alInner.add(rs.getString("tax_percent"));
				alInner.add(hmDeductionType.get(rs.getString("invoice_or_customer")));
				alInner.add(rs.getString("org_id"));
				alTaxHeads.add(alInner);
				
				hmOrgName.put(rs.getString("org_id"), rs.getString("org_name") + " [" +rs.getString("org_code") +"]");
				
				hmTaxHead.put(rs.getString("org_id"), alTaxHeads);
			}
			rs.close();
			pst.close();
			
			
			Map<String, List<List<String>>> hmTaxHeadHistory = new LinkedHashMap<String, List<List<String>>>();
			
			StringBuilder sbHistoryQuery = new StringBuilder();
			sbHistoryQuery.append("select tsh.*,ts.tax_name,ts.tax_name_label,ts.org_id from tax_setting ts, tax_setting_history tsh " +
				" where ts.tax_setting_id = tsh.tax_setting_id ");
			if(uF.parseToInt(getStrOrg()) > 0) {
				sbHistoryQuery.append(" and ts.org_id = "+uF.parseToInt(getStrOrg())+" ");
			}
			sbHistoryQuery.append(" order by ts.tax_name, tsh.effective_date desc");
			pst = con.prepareStatement(sbHistoryQuery.toString());
			rs = pst.executeQuery();
//			Map<String, String> hmOrgNameHistory = new HashMap<String, String>();
			List<List<String>> alTaxHeadsHistory = new ArrayList<List<String>>();
			while(rs.next()) {
				alTaxHeadsHistory = hmTaxHeadHistory.get(rs.getString("org_id"));
				if(alTaxHeadsHistory == null) alTaxHeadsHistory = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("tax_setting_history_id"));
				alInner.add(rs.getString("tax_setting_id"));
				alInner.add(rs.getString("tax_name"));
				alInner.add(rs.getString("tax_percent"));
				alInner.add(hmDeductionType.get(rs.getString("invoice_or_customer")));
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("org_id"));
				alTaxHeadsHistory.add(alInner);
				
//				hmOrgNameHistory.put(rs.getString("org_id"), rs.getString("org_name") + " [" +rs.getString("org_code") +"]");
				
				hmTaxHeadHistory.put(rs.getString("org_id"), alTaxHeadsHistory);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hProjectRateMap ===>>> " + hProjectRateMap);
			
			request.setAttribute("hmTaxHeadHistory", hmTaxHeadHistory);
			request.setAttribute("hmOrgName", hmOrgName);
			request.setAttribute("hmTaxHead", hmTaxHead);
//			request.setAttribute("hmEmpGradeMap", hmEmpGradeMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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
