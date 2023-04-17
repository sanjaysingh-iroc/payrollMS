package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class WorkFlowPolicyReport extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	CommonFunctions CF;
	
	String submit;
	String policy_type;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> workList;
	
	String strOrg;
	String strLocation;

	String userscreen;
	String navigationId;
	String toPage;
	
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		

		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);

		request.setAttribute(PAGE, "/jsp/policies/WorkFlowPolicyReport.jsp");
		request.setAttribute(TITLE, "Workflow Policy");
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("getStrOrg() ===>> " + getStrOrg());
//		System.out.println("getStrLocation() ===>> " + getStrLocation());
		
		if(uF.parseToInt(getStrOrg()) == 0){
			setStrOrg((String) session.getAttribute(ORGID));
		}
		
		orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		workList = new FillWLocation(request).fillWLocation(getStrOrg(), (String)session.getAttribute(WLOCATION_ACCESS));
		
		if(uF.parseToInt(getStrLocation()) > 0){
			viewWorkFlowPolicyReport();
			getMemberPositions();
			getGroupDetails();
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
	

	
private void getGroupDetails() {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();
	
	try {
		con=db.makeConnection(con);
		Map<String,String> hmMemberGroup = new LinkedHashMap<String, String>();
		StringBuilder sbQuery=new StringBuilder();
		sbQuery.append("select group_id,group_name from work_flow_member where wlocation_id=? and org_id=? " +
				"and is_default=false group by group_id,group_name order by group_name");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(getStrLocation()));
		pst.setInt(2, uF.parseToInt(getStrOrg()));
		rs=pst.executeQuery();
		while(rs.next()){
			hmMemberGroup.put(rs.getString("group_id"),rs.getString("group_name"));
		}					
		request.setAttribute("hmMemberGroup", hmMemberGroup);
		
		sbQuery=new StringBuilder();
		sbQuery.append("select group_id from work_flow_policy where trial_status=1 ");
		if(uF.parseToInt(getStrOrg())>0){
			sbQuery.append(" and org_id = '"+uF.parseToInt(getStrOrg())+"' ");
		}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
			List<String> alOrg = Arrays.asList(((String)session.getAttribute(ORG_ACCESS)).trim().split(","));
			StringBuilder sb = null;
			for(int i = 0; alOrg!=null && i < alOrg.size(); i++){
				if(uF.parseToInt(alOrg.get(i)) > 0){
					if(sb == null){
						sb = new StringBuilder();
						sb.append("'"+alOrg.get(i)+"'");
					} else {
						sb.append(",'"+alOrg.get(i)+"'");
					}
				}
			}
			if(sb!=null){
				sbQuery.append(" and org_id in ("+sb.toString()+")");
			}
		}
		if(uF.parseToInt(getStrLocation())>0){
            sbQuery.append(" and location_id ='"+uF.parseToInt(getStrLocation())+"'");
        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
        	List<String> alWloc = Arrays.asList(((String)session.getAttribute(WLOCATION_ACCESS)).trim().split(","));
			StringBuilder sb = null;
			for(int i = 0; alWloc!=null && i < alWloc.size(); i++){
				if(uF.parseToInt(alWloc.get(i)) > 0){
					if(sb == null){
						sb = new StringBuilder();
						sb.append("'"+alWloc.get(i)+"'");
					} else {
						sb.append(",'"+alWloc.get(i)+"'");
					}
				}
			}
			if(sb!=null){
				sbQuery.append(" and location_id in ("+sb.toString()+")");
			}
		}
		sbQuery.append(" group by group_id");
//		System.out.println("pst=====>"+pst);
		rs=pst.executeQuery();
		Map<String,String> hmGroupID = new HashMap<String, String>();
		while(rs.next()){
			hmGroupID.put(rs.getString("group_id"),rs.getString("group_id")); 
		}
		rs.close();
		pst.close();
		
		request.setAttribute("hmGroupID", hmGroupID);
				
	} catch (Exception e) {
		e.printStackTrace(); 
	}finally{
		
		db.closeStatements(pst);
		db.closeResultSet(rs);
		db.closeConnection(con);
	}
}
private void getMemberPositions() {
	
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	
	
	try {
		con=db.makeConnection(con);
		Map<String,String> hmWorkFlowMember = new HashMap<String, String>();
		pst = con.prepareStatement("select * from work_flow_member");
		rs=pst.executeQuery();
		while(rs.next()){
			hmWorkFlowMember.put(rs.getString("work_flow_member_id"),rs.getString("work_flow_mem"));
		}	
	
		pst = con.prepareStatement("select * from work_flow_policy where policy_type='1' and trial_status=1 order by policy_count,member_position");
		rs = pst.executeQuery();
		Map<String,String> hmRegularPolicy = new HashMap<String, String>();		
	
		String strMemberPosition = null;
		while (rs.next()) {
			
			String memberStep=hmRegularPolicy.get(rs.getString("policy_count"));
			
			if(memberStep==null){
				memberStep=hmWorkFlowMember.get(rs.getString("work_flow_member_id").trim());
				strMemberPosition=rs.getString("member_position");
			}else{
				if(strMemberPosition!=null && strMemberPosition.equals(rs.getString("member_position"))){
					memberStep+=" or "+hmWorkFlowMember.get(rs.getString("work_flow_member_id").trim());
				}else{
					strMemberPosition=rs.getString("member_position");
					memberStep+=" <i class=\"fa fa-long-arrow-right\"></i> "+hmWorkFlowMember.get(rs.getString("work_flow_member_id").trim());
				}
			}
			
			hmRegularPolicy.put(rs.getString("policy_count"), memberStep);
		}
		rs.close();
		pst.close();
		request.setAttribute("hmRegularPolicy", hmRegularPolicy);
		
	} catch (Exception e) {
		e.printStackTrace(); 
	}finally{
		
		db.closeStatements(pst);
		db.closeResultSet(rs);
		db.closeConnection(con);
	}
}
public void viewWorkFlowPolicyReport(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		Map<String,String> hmOrg=getOrganization();
		Map<String,String> hmLocationName=getLocationName();
		
		
		try {

			con = db.makeConnection(con);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from (select max(work_flow_policy_id)as work_flow_policy_id,policy_count " +
					" from work_flow_policy where trial_status=1 group by policy_count)as a ,work_flow_policy b " +
					" where a.work_flow_policy_id=b.work_flow_policy_id");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = '"+uF.parseToInt(getStrOrg())+"' ");
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				List<String> alOrg = Arrays.asList(((String)session.getAttribute(ORG_ACCESS)).trim().split(","));
				StringBuilder sb = null;
				for(int i = 0; alOrg!=null && i < alOrg.size(); i++){
					if(uF.parseToInt(alOrg.get(i)) > 0){
						if(sb == null){
							sb = new StringBuilder();
							sb.append("'"+alOrg.get(i)+"'");
						} else {
							sb.append(",'"+alOrg.get(i)+"'");
						}
					}
				}
				if(sb!=null){
					sbQuery.append(" and org_id in ("+sb.toString()+")");
				}
			}
			if(uF.parseToInt(getStrLocation())>0){
	            sbQuery.append(" and location_id ='"+uF.parseToInt(getStrLocation())+"'");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
	        	List<String> alWloc = Arrays.asList(((String)session.getAttribute(WLOCATION_ACCESS)).trim().split(","));
				StringBuilder sb = null;
				for(int i = 0; alWloc!=null && i < alWloc.size(); i++){
					if(uF.parseToInt(alWloc.get(i)) > 0){
						if(sb == null){
							sb = new StringBuilder();
							sb.append("'"+alWloc.get(i)+"'");
						} else {
							sb.append(",'"+alWloc.get(i)+"'");
						}
					}
				}
				if(sb!=null){
					sbQuery.append(" and location_id in ("+sb.toString()+")");
				}
			}
			pst = con.prepareStatement(sbQuery.toString()); 
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery(); 
			Map<String, List<List<String>>> hmReportList = new LinkedHashMap<String, List<List<String>>>();
			while(rs.next()){
				List<List<String>> reportList = hmReportList.get(rs.getString("group_id"));
				if (reportList == null)
					reportList = new ArrayList<List<String>>();
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("work_flow_policy_id"));
				alInner.add(rs.getString("work_flow_member_id"));
				alInner.add(rs.getString("member_position"));
				alInner.add(rs.getString("policy_type"));
				alInner.add(rs.getString("trial_status"));
				alInner.add(rs.getString("added_by"));
				alInner.add(rs.getString("added_date")!=null?uF.getDateFormat(rs.getString("added_date"), DBDATE, DATE_FORMAT):"-");
				alInner.add(rs.getString("policy_count"));
				
				alInner.add(rs.getString("policy_name"));
				alInner.add(rs.getString("effective_date")!=null?uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT):"-");
				alInner.add(rs.getString("org_id")!=null ? hmOrg.get(rs.getString("org_id").trim()) : "");
				alInner.add(rs.getString("location_id")!=null ? hmLocationName.get(rs.getString("location_id").trim()) : "");
				
				alInner.add(rs.getString("policy_status"));
				
				reportList.add(alInner);
				
				hmReportList.put(rs.getString("group_id"), reportList);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmReportList", hmReportList);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			request.setAttribute("hmEmpName", hmEmpName);
			
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}
	

	private Map<String, String> getLocationName() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
				
		Map<String,String> hmLocationName=new HashMap<String, String>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from work_location_info");
			rs=pst.executeQuery();
			
			while(rs.next()){
				hmLocationName.put(rs.getString("wlocation_id"),rs.getString("wlocation_name"));
			}			
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return hmLocationName;
	}
	private Map<String, String> getOrganization() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		Map<String,String> hmOrg=new HashMap<String, String>();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select org_id,org_name from org_details");
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"),rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return hmOrg;
	}


	public String getSubmit() {
		return submit;
	}
	
	public void setSubmit(String submit) {
		this.submit = submit;
	}
	
	public String getPolicy_type() {
		return policy_type;
	}
	
	public void setPolicy_type(String policy_type) {
		this.policy_type = policy_type;
	}
	
	public List<FillOrganisation> getOrgList() {
		return orgList;
	}
	
	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}
	
	public List<FillWLocation> getWorkList() {
		return workList;
	}
	
	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
	}

	public String getStrOrg() {
		return strOrg;
	}
	
	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}
	
	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
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

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

}
