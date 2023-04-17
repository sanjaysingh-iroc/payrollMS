package com.konnect.jpms.task.tax;

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

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TimesheetSetting extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	
	String strOrg;
	String strLocation;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> workList;
	
	List<FillWorkFlowPolicy> wfPolicyList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception {
		
		request.setAttribute(PAGE, "/jsp/task/tax/TimesheetSetting.jsp");
		request.setAttribute(TITLE, "Timesheet Setting");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		orgList = new FillOrganisation(request).fillOrganisation();
		if(getStrOrg()==null && orgList!=null && orgList.size()>0) {
			setStrOrg((String)session.getAttribute(ORGID));
		}
		workList = new FillWLocation(request).fillWLocation(getStrOrg());
		
		if(uF.parseToInt(getStrLocation()) > 0){
			viewOrgLevels(uF);
		}
		
		getSelectedFilter(uF);
		
		return SUCCESS;
 
	}
	
	   
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++) {
				if(getStrOrg().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		alFilter.add("LOCATION");
		if(getStrLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;workList!=null && i<workList.size();i++) {
				if(getStrLocation().equals(workList.get(i).getwLocationId())) {
					strLocation=workList.get(i).getwLocationName();
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	public String viewOrgLevels(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			
			Map<String, List<FillWorkFlowPolicy>> hmOrgWFPolicy = new FillWorkFlowPolicy(request).fillWorkFlowPolicyName();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ld.* from level_details ld, org_details od where ld.org_id = od.org_id and ld.level_id > 0 ");
			if(uF.parseToInt(getStrOrg()) > 0) {
				sbQuery.append(" and ld.org_id = "+uF.parseToInt(getStrOrg())+" ");
			}
			sbQuery.append(" order by ld.level_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			
			Map<String, List<List<String>>> hmOrgLevels = new HashMap<String, List<List<String>>>();
			List<List<String>> alLevels = new ArrayList<List<String>>();
			while(rs.next()) {
				alLevels = hmOrgLevels.get(rs.getString("org_id"));
				if(alLevels == null) alLevels = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("level_id")); //0
				alInner.add(rs.getString("level_code")); //1
				alInner.add(rs.getString("level_name")); //2
				alInner.add(rs.getString("org_id")); //3
				Map<String, String> hmLevelWFPolicy = getLevelWFPolicy(con, uF, rs.getString("level_id"));
				alInner.add(uF.showData(hmLevelWFPolicy.get("POLICY_NAME"), "N/A")); //4
				
				String strPolicies = getOrgWFPolicies(rs.getString("org_id"), hmLevelWFPolicy.get("POLICY_ID"), hmOrgWFPolicy);
				alInner.add(strPolicies); //5
				alLevels.add(alInner);
				hmOrgLevels.put(rs.getString("org_id"), alLevels);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOrgName", hmOrgName);
			request.setAttribute("hmOrgLevels", hmOrgLevels);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	
	private Map<String, String> getLevelWFPolicy(Connection con, UtilityFunctions uF, String levelId) {
		
		PreparedStatement pst=null;
		ResultSet rs= null;
		
		Map<String, String> hmLevelWFPolicy = new HashMap<String, String>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select wd.policy_id,w.policy_name from work_flow_policy_details wd, work_flow_policy w " +
					"where wd.policy_id=w.policy_count and wd.level_id=? and wd.wlocation_id=? and wd.type = '"+WORK_FLOW_TIMESHEET+"' and policy_type='1' limit 1");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(levelId));
			pst.setInt(2, uF.parseToInt(getStrLocation()));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLevelWFPolicy.put("POLICY_ID", rs.getString("policy_id"));
				hmLevelWFPolicy.put("POLICY_NAME", rs.getString("policy_name"));
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmLevelWFPolicy;
	}



	private String getOrgWFPolicies(String orgId, String policyId, Map<String, List<FillWorkFlowPolicy>> hmOrgWFPolicy) {
		
		wfPolicyList = hmOrgWFPolicy.get(orgId+"_"+getStrLocation());
		StringBuilder sbWFPoliciesList = new StringBuilder();
		for(int i=0; wfPolicyList!=null && i<wfPolicyList.size(); i++) {
			if(policyId !=null && wfPolicyList.get(i).getPolicyId().equals(policyId)) {
				sbWFPoliciesList.append("<option value='"+wfPolicyList.get(i).getPolicyId()+"' selected>"+wfPolicyList.get(i).getPolicyName()+"</option>");
			} else {
				sbWFPoliciesList.append("<option value='"+wfPolicyList.get(i).getPolicyId()+"'>"+wfPolicyList.get(i).getPolicyName()+"</option>");
			}
		}
		return sbWFPoliciesList.toString();
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

	public List<FillWorkFlowPolicy> getWfPolicyList() {
		return wfPolicyList;
	}

	public void setWfPolicyList(List<FillWorkFlowPolicy> wfPolicyList) {
		this.wfPolicyList = wfPolicyList;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
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
