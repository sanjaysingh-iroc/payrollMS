package com.konnect.jpms.loan;

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

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LoanPolicyReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	String strUserType;
	HttpSession session;
	List<FillOrganisation> orgList;
	String strOrg;
	
	private static Logger log = Logger.getLogger(LoanPolicyReport.class);
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PLoanPolicyReport);
		request.setAttribute(TITLE, "Loan Policies");
		strUserType = (String) session.getAttribute(USERTYPE);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		viewLoanPolicy(uF);
		getSelectedFilter(uF);
		
		return LOAD; 
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
	

	
	public String viewLoanPolicy(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			Map<String, Map<String, String>> hmLevelMap = new HashMap<String, Map<String,String>>();
			pst = con.prepareStatement(selectLevel1);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("LEVEL_CODE", rs.getString("level_code"));
				hmInner.put("LEVEL_NAME", rs.getString("level_name"));
				hmLevelMap.put(rs.getString("level_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			Map<String, List<List<String>>> hmLoanReport = new HashMap<String, List<List<String>>>();
			
			pst = con.prepareStatement(selectLoanDetails2);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				List<List<String>> alLoanPolicy = hmLoanReport.get(rs.getString("level_id"));
				if(alLoanPolicy==null) alLoanPolicy = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("loan_id"));
				alInner.add(rs.getString("loan_code"));
				alInner.add(rs.getString("loan_description"));
				alInner.add(rs.getString("min_service_years"));
				alInner.add(rs.getString("loan_interest"));
				alInner.add(rs.getString("fine_amount"));
				alInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("times_salary"))));
				alInner.add(hmEmpName.get(rs.getString("user_id")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(uF.showYesNo(rs.getString("is_check_previous_loan")));

				alLoanPolicy.add(alInner);
				
				hmLoanReport.put(rs.getString("level_id"), alLoanPolicy);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmLoanReport", hmLoanReport);
			request.setAttribute("hmLevelMap", hmLevelMap);
			
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