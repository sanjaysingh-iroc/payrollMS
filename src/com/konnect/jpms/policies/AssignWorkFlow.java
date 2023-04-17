package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.task.tax.FillWorkFlowPolicy;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AssignWorkFlow extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String strOrg;
	String strLocation;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> workList;
	
	List<FillWorkFlowPolicy> wfPolicyList;
	
	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		request.setAttribute(PAGE, "/jsp/policies/AssignWorkFlow.jsp");
		request.setAttribute(TITLE, "Assign Workflow");
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getStrOrg()) == 0  && orgList!=null && orgList.size()>0){
				setStrOrg(orgList.get(0).getOrgId());
			}
			workList = new FillWLocation(request).fillWLocation(getStrOrg(), (String)session.getAttribute(WLOCATION_ACCESS));
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
			workList = new FillWLocation(request).fillWLocation(getStrOrg());
		}
		
		if(uF.parseToInt(getStrLocation()) > 0){
			viewWorkFlow(uF);	
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
		
		
		alFilter.add("LOCATION");
		if(getStrLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;workList!=null && i<workList.size();i++) {
				if(getStrLocation().equals(workList.get(i).getwLocationId())) {
					if(k==0) {
						strLocation=workList.get(i).getwLocationName();
					} else {
						strLocation+=", "+workList.get(i).getwLocationName();
					}
					k++;
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "-");
			}
		} else {
			hmFilter.put("LOCATION", "-");
		}
		
		String selectedFilter = CF.getSelectedFilter(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	


	private void viewWorkFlow(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
			if(hmEmpCodeName == null) hmEmpCodeName = new HashMap<String, String>();
			
			Map<String, List<FillWorkFlowPolicy>> hmOrgWFPolicy = new FillWorkFlowPolicy(request).fillWorkFlowPolicyName();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ld.* from level_details ld, org_details od where ld.org_id = od.org_id and ld.level_id > 0 and ld.org_id =?" +
					" order by ld.level_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmOrgLevels = new HashMap<String, List<List<String>>>();
			while(rs.next()) {
				List<List<String>> alLevels = hmOrgLevels.get(rs.getString("org_id"));
				if(alLevels == null) alLevels = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("level_id")); //0
				alInner.add(rs.getString("level_code")); //1
				alInner.add(rs.getString("level_name")); //2
				alInner.add(rs.getString("org_id")); //3
				
				alLevels.add(alInner);
				hmOrgLevels.put(rs.getString("org_id"), alLevels);
			}
			rs.close();
			pst.close();
			
			List<String> alType = new ArrayList<String>();
			alType.add(WORK_FLOW_REIMBURSEMENTS);
			alType.add(WORK_FLOW_TRAVEL);
			alType.add(WORK_FLOW_PERK);
			alType.add(WORK_FLOW_LEAVE_ENCASH);
			alType.add(WORK_FLOW_LTA);
			alType.add(WORK_FLOW_REQUISITION);
			alType.add(WORK_FLOW_LOAN);
			alType.add(WORK_FLOW_RESIGN);
			alType.add(WORK_FLOW_RECRUITMENT);
			alType.add(WORK_FLOW_RESUME_SHORTLIST);
			alType.add(WORK_FLOW_SELF_REVIEW);
			alType.add(WORK_FLOW_PERSONAL_GOAL);//Created By Dattatray Date:26-08-21 Note : Added WORK_FLOW_PERSONAL_GOAL
			alType.add(WORK_FLOW_LEARNING_REQUEST);//Created By parvez Date:27-09-21 Note : Added WORK_FLOW_LEARNING_VIDEO
			
			Map<String, String> hmType = new HashMap<String, String>();
			hmType.put(WORK_FLOW_REIMBURSEMENTS, "Reimbursements");
			hmType.put(WORK_FLOW_TRAVEL, "Travel");
			hmType.put(WORK_FLOW_PERK, "Perk");
			hmType.put(WORK_FLOW_LEAVE_ENCASH, "Leave Encashment");
			hmType.put(WORK_FLOW_LTA, "LTA");
			hmType.put(WORK_FLOW_REQUISITION, "Requisition");
			hmType.put(WORK_FLOW_LOAN, "Loan");
			hmType.put(WORK_FLOW_RESIGN, "Resign");
			hmType.put(WORK_FLOW_RECRUITMENT, "Recruitment");
			hmType.put(WORK_FLOW_RESUME_SHORTLIST, "Resume Shortlist");
			hmType.put(WORK_FLOW_SELF_REVIEW, "Self Review");
			hmType.put(WORK_FLOW_PERSONAL_GOAL, "Personal Goal");//Created By Dattatray Date:26-08-21 Note : Added
			hmType.put(WORK_FLOW_LEARNING_REQUEST, "Learning Request");//Created By Parvez Date:27-09-21 Note : Added
			request.setAttribute("hmType", hmType);
			
			Map<String, List<List<String>>> hmLevelPolicy = new HashMap<String, List<List<String>>>(); 
			
			Iterator<String> it =hmOrgLevels.keySet().iterator();
			while(it.hasNext()){
				String strOrgId = it.next();
				List<List<String>> alLevels = hmOrgLevels.get(strOrgId);
				if(alLevels == null) alLevels = new ArrayList<List<String>>();
				
				for(int i = 0; alLevels!=null && i < alLevels.size(); i++) {
					List<String> alInner = alLevels.get(i);
					for(int j=0; j<alType.size(); j++) {
						
						Map<String, String> hmLevelWFPolicy = getLevelWFPolicy(con, uF, alInner.get(0),alType.get(j));
						String strPolicies = getOrgWFPolicies(strOrgId, hmLevelWFPolicy.get("POLICY_ID"), hmOrgWFPolicy);
						
						List<List<String>> alOuter = hmLevelPolicy.get(alInner.get(0)+"_"+alType.get(j));
						if(alOuter == null) alOuter = new ArrayList<List<String>>();
						
						List<String> al = new ArrayList<String>();
						al.add(uF.showData(hmLevelWFPolicy.get("POLICY_NAME"), "N/A"));
						al.add(strPolicies);
						
						alOuter.add(al);
						
						hmLevelPolicy.put(alInner.get(0)+"_"+alType.get(j), alOuter);
						
					}
				}
			}
			request.setAttribute("hmOrgName", hmOrgName);
			request.setAttribute("hmOrgLevels", hmOrgLevels);
			request.setAttribute("alType", alType);
			request.setAttribute("hmLevelPolicy", hmLevelPolicy);
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_type elt,leave_type lt where elt.leave_type_id=lt.leave_type_id and is_approval=true " +
					"and elt.org_id=? and elt.wlocation_id=? order by elt.level_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setInt(2, uF.parseToInt(getStrLocation()));
			rs = pst.executeQuery();
			Map<String, List<Map<String,String>>> hmLeaveLevels = new HashMap<String, List<Map<String,String>>>();
			while(rs.next()) {
				List<Map<String,String>> alLevels = hmLeaveLevels.get(rs.getString("level_id"));
				if(alLevels == null) alLevels = new ArrayList<Map<String,String>>();

				Map<String,String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_LEAVE_TYPE_ID", rs.getString("emp_leave_type_id"));
				hmInner.put("LEVEL_ID", rs.getString("level_id"));
				hmInner.put("LEAVE_TYPE_ID", rs.getString("leave_type_id"));
				hmInner.put("LEAVE_TYPE_NAME", rs.getString("leave_type_name"));
				hmInner.put("ORG_ID", rs.getString("org_id"));
				hmInner.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmInner.put("POLICY_ID", rs.getString("policy_id"));
				
				Map<String, String> hmLevelWFPolicy = getLevelWFPolicy1(con, uF, rs.getString("policy_id"));
				String strPolicies = getOrgWFPolicies(rs.getString("org_id"), hmLevelWFPolicy.get("POLICY_ID"), hmOrgWFPolicy);
				hmInner.put("POLICY_NAME",uF.showData(hmLevelWFPolicy.get("POLICY_NAME"), "N/A"));
				hmInner.put("POLICY_LIST",strPolicies);
				hmInner.put("IS_PERIOD",""+uF.parseToBoolean(rs.getString("is_period")));
				
				alLevels.add(hmInner);
				
				hmLeaveLevels.put(rs.getString("level_id"), alLevels);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmLeaveLevels", hmLeaveLevels);
//			System.out.println("hmLeaveLevels=====>"+hmLeaveLevels);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from workflow_policy_period where org_id=? and wlocation_id=? order by min_value");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setInt(2, uF.parseToInt(getStrLocation()));
			rs = pst.executeQuery();
			Map<String, List<Map<String,String>>> hmLeavePeriodLevels = new HashMap<String, List<Map<String,String>>>();
			while(rs.next()) {
				List<Map<String,String>> alLevelPeriods = hmLeavePeriodLevels.get(rs.getString("level_id")+"_"+rs.getString("leave_type_id"));
				if(alLevelPeriods == null) alLevelPeriods = new ArrayList<Map<String,String>>();

				Map<String,String> hmPeriod = new HashMap<String, String>();
				hmPeriod.put("WORKFLOW_POLICY_PERIOD_ID", rs.getString("workflow_policy_period_id"));
				hmPeriod.put("MIN_VALUE", rs.getString("min_value"));
				hmPeriod.put("MAX_VALUE", rs.getString("max_value"));
				hmPeriod.put("POLICY_ID", rs.getString("policy_id"));
				
				Map<String, String> hmLevelWFPolicy = getLevelWFPolicy1(con, uF, rs.getString("policy_id"));
				hmPeriod.put("POLICY_NAME",uF.showData(hmLevelWFPolicy.get("POLICY_NAME"), "N/A"));
				
				hmPeriod.put("POLICY_TYPE", rs.getString("policy_type"));
				hmPeriod.put("LEVEL_ID", rs.getString("level_id"));
				hmPeriod.put("ORG_ID", rs.getString("org_id"));
				hmPeriod.put("WLOCATION_ID", rs.getString("wlocation_id"));
				hmPeriod.put("LEAVE_TYPE_ID", rs.getString("leave_type_id"));
				hmPeriod.put("ADDED_BY", uF.showData(rs.getString("added_by"), ""));
				hmPeriod.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				
				alLevelPeriods.add(hmPeriod);
				
				hmLeavePeriodLevels.put(rs.getString("level_id")+"_"+rs.getString("leave_type_id"), alLevelPeriods);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmLeavePeriodLevels", hmLeavePeriodLevels);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	private Map<String, String> getLevelWFPolicy1(Connection con, UtilityFunctions uF, String policyId) {
		PreparedStatement pst=null;
		ResultSet rs= null;
		
		Map<String, String> hmLevelWFPolicy = new HashMap<String, String>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(policy_count) as policy_id,policy_name from work_flow_member as a,work_flow_policy wfp where " +
					"policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id and policy_count=? order by policy_count");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(policyId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLevelWFPolicy.put("POLICY_ID", rs.getString("policy_id"));
				hmLevelWFPolicy.put("POLICY_NAME", rs.getString("policy_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmLevelWFPolicy;
	}



	private Map<String, String> getLevelWFPolicy(Connection con, UtilityFunctions uF, String levelId, String strType) {
		
		PreparedStatement pst=null;
		ResultSet rs= null;
		
		Map<String, String> hmLevelWFPolicy = new HashMap<String, String>();
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select wd.policy_id,w.policy_name from work_flow_policy_details wd, work_flow_policy w " +
					"where wd.policy_id=w.policy_count and wd.level_id=? and wd.wlocation_id=? and wd.type = '"+strType+"' and policy_type='1'");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(levelId));
			pst.setInt(2, uF.parseToInt(getStrLocation()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLevelWFPolicy.put("POLICY_ID", rs.getString("policy_id"));
				hmLevelWFPolicy.put("POLICY_NAME", rs.getString("policy_name"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
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

	public List<FillWorkFlowPolicy> getWfPolicyList() {
		return wfPolicyList;
	}

	public void setWfPolicyList(List<FillWorkFlowPolicy> wfPolicyList) {
		this.wfPolicyList = wfPolicyList;
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