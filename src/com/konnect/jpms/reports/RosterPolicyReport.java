package com.konnect.jpms.reports;

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

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillInOut;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillTimeType;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class RosterPolicyReport  extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	List<FillTimeType> timeTypeList;
	List<FillInOut> in_out_List;
	List<FillApproval> approvalList;
	CommonFunctions CF;
	String strUserType;
	HttpSession session;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;	 
	String strOrg;
	String strWLocation;
	
	private static Logger log = Logger.getLogger(RosterPolicyReport.class);
	
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF= (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, PRosterPolicyReport);
		request.setAttribute(TITLE, TViewRosterPolicy);
		
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
			wLocationList = new FillWLocation(request).fillWLocation(getStrOrg(), (String)session.getAttribute(WLOCATION_ACCESS));
		}else{
			if(uF.parseToInt(getStrOrg()) == 0){
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getStrOrg());
		}
		
		if(uF.parseToInt(getStrWLocation()) == 0 && wLocationList!=null && wLocationList.size()>0){
			setStrWLocation(wLocationList.get(0).getwLocationId());
		}
		
		viewRosterPolicy(CF, uF);
		getSelectedFilter(uF);
		return loadRosterPolicy();
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
		if(getStrWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				if(getStrWLocation().equals(wLocationList.get(i).getwLocationId())) {
					if(k==0) {
						strLocation=wLocationList.get(i).getwLocationName();
					} else {
						strLocation+=", "+wLocationList.get(i).getwLocationName();
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
	

	
	public String loadRosterPolicy(){
		
		timeTypeList = new FillTimeType().fillTimeType();
		in_out_List = new FillInOut().fillInOut();
		approvalList = new FillApproval().fillYesNo();
		request.setAttribute("timeTypeList", timeTypeList);
		request.setAttribute("in_out_List", in_out_List);
		request.setAttribute("approvalList", approvalList);
		
		
		int i=0;
		String timeTypeName, timeTypeId;
		StringBuilder sbTimeTypeList = new StringBuilder();
		sbTimeTypeList.append("{");
	    for(i=0; i<timeTypeList.size()-1;i++ ) {
	    		timeTypeId = (timeTypeList.get(i)).getTimeTypeId();
	    		timeTypeName = timeTypeList.get(i).getTimeTypeName();
	    		sbTimeTypeList.append("\""+ timeTypeId+"\":\""+timeTypeName+"\",");
	    }
	    timeTypeId = (timeTypeList.get(i)).getTimeTypeId();
		timeTypeName = timeTypeList.get(i).getTimeTypeName();
		sbTimeTypeList.append("\""+ timeTypeId+"\":\""+timeTypeName+"\"");	
	    sbTimeTypeList.append("}");
	    request.setAttribute("sbTimeTypeList", sbTimeTypeList.toString());
	    
		int i1=0;
		String in_out_name, in_out_id;
		StringBuilder sbIn_out_List = new StringBuilder();
		sbIn_out_List.append("{");
	    for(i1=0; i1<in_out_List.size()-1;i1++ ) {
	    		in_out_id = (in_out_List.get(i1)).getIn_out_Id();
	    		in_out_name = in_out_List.get(i1).getIn_out_Id();
	    		sbIn_out_List.append("\""+ in_out_id+"\":\""+in_out_name+"\",");
	    }
	    in_out_id = (in_out_List.get(i1)).getIn_out_Id();
		in_out_name = in_out_List.get(i1).getIn_out_Name();
		sbIn_out_List.append("\""+ in_out_id+"\":\""+in_out_name+"\"");	
	    sbIn_out_List.append("}");
		request.setAttribute("sbIn_out_List", sbIn_out_List.toString());
		
		int i11=0;
		String approvalName, approvalId;
		StringBuilder sbApprovalList = new StringBuilder();
		 sbApprovalList.append("{");
		    for(i11=0; i11<approvalList.size()-1;i11++ ) {
		    		approvalId = (approvalList.get(i11)).getApprovalId();
		    		approvalName = approvalList.get(i11).getApprovalName();
		    		sbApprovalList.append("\""+ approvalId+"\":\""+approvalName+"\",");
		    }
		    approvalId = (approvalList.get(i11)).getApprovalId();
 		approvalName = approvalList.get(i11).getApprovalName();
 		sbApprovalList.append("\""+ approvalId+"\":\""+approvalName+"\"");	
	    sbApprovalList.append("}");
		request.setAttribute("sbApprovalList", sbApprovalList.toString());
		
		return LOAD;
	}
	
	public String viewRosterPolicy(CommonFunctions CF, UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			Map hmRosterPolicyReport = new LinkedHashMap();
			Map hmRosterHDPolicyReport = new LinkedHashMap();
			Map hmRosterBreakPolicyReport = new LinkedHashMap();
			
			//pst = con.prepareStatement(selectRosterPolicy);
//			pst=con.prepareStatement("SELECT * FROM roster_policy where org_id =? and wlocation_id=? order by mode, time_type,abs(time_value), roster_policy_id");
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
//			pst.setInt(2, uF.parseToInt(getStrWLocation()));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT * FROM roster_policy where roster_policy_id > 0 ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getStrWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getStrWLocation()));
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" order by mode, time_type,abs(time_value), roster_policy_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			String strModeTypeNew = "";
			String strModeTypeOld = "";
			String strTime = "";
			while(rs.next()){
				
				strModeTypeNew = rs.getString("time_type") + "_" +rs.getString("mode"); 
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("roster_policy_id"));
				if(strModeTypeOld!=null && !strModeTypeOld.equalsIgnoreCase(strModeTypeNew)){
					strTime = "";
				}

				if(Math.abs(rs.getInt("time_value")) > 0){
					alInner.add(uF.parseToInt(strTime)+" - "+Math.abs(rs.getInt("time_value")));
				}else{
					alInner.add(" 0 ");
				}
				
				strTime = Math.abs(rs.getInt("time_value"))+"";
				
				alInner.add(rs.getString("message"));
				alInner.add(rs.getString("time_type"));
				alInner.add(rs.getString("mode"));
				alInner.add(uF.showYesNo(rs.getString("isapproval")));
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("policy_status"));
				
				alInner.add(hmEmpName.get(rs.getString("user_id")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				
//				alInner.add("<a href="+request.getContextPath()+"/RosterPolicy.action?E="+rs.getString("roster_policy_id")+">Edit</a> | <a onclick=\"return confirm('"+ConfirmDelete+"');\" href="+request.getContextPath()+"/RosterPolicy.action?D="+rs.getString("roster_policy_id")+">Delete</a>");
				al.add(alInner);
				
				hmRosterPolicyReport.put(rs.getString("roster_policy_id"), alInner);
				
				strModeTypeOld = strModeTypeNew;
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRosterPolicyReport", hmRosterPolicyReport);
			
			
			
			
//			pst = con.prepareStatement("select * from roster_halfday_policy where org_id =? and wlocation_id=?  order by effective_date desc");
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
//			pst.setInt(2, uF.parseToInt(getStrWLocation()));
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_halfday_policy where roster_hd_policy_id > 0 ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getStrWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getStrWLocation()));
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" order by effective_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("roster_hd_policy_id"));
				
				alInner.add(Math.abs(uF.parseToDouble(rs.getString("time_value")))+"");
				alInner.add(rs.getString("_mode"));
				alInner.add(rs.getString("days"));
				alInner.add(rs.getString("months"));
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("policy_status"));
				
				alInner.add(hmEmpName.get(rs.getString("user_id")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				al.add(alInner);
				
				hmRosterHDPolicyReport.put(rs.getString("roster_hd_policy_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRosterHDPolicyReport", hmRosterHDPolicyReport);
			
//			pst = con.prepareStatement("select * from break_policy bp, leave_break_type lbt where lbt.break_type_id = bp.break_type_id and lbt.org_id =? and bp.wlocation_id=? order by effective_date desc");
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
//			pst.setInt(2, uF.parseToInt(getStrWLocation()));
			sbQuery = new StringBuilder();
			sbQuery.append("select * from break_policy bp, leave_break_type lbt where lbt.break_type_id = bp.break_type_id ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and lbt.org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and lbt.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getStrWLocation())>0){
				sbQuery.append(" and bp.wlocation_id = "+uF.parseToInt(getStrWLocation()));
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and bp.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" order by effective_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("break_policy_id"));
				
				alInner.add(rs.getString("time_value"));
				
				if(rs.getString("_mode")!=null && rs.getString("_mode").indexOf("IN")>=0 && rs.getString("_mode").indexOf("OUT")>=0){
					alInner.add(" comes late or leaves early");
				}else if(rs.getString("_mode")!=null && rs.getString("_mode").indexOf("IN")>=0){
					alInner.add(" comes late ");
				}else if(rs.getString("_mode")!=null && rs.getString("_mode").indexOf("OUT")>=0){
					alInner.add(" leaves early ");
				}else{
					alInner.add(" ");
				}
				
//				alInner.add(rs.getString("_mode"));
				alInner.add(rs.getString("days"));
				alInner.add(rs.getString("months"));
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("policy_status"));
				alInner.add(rs.getString("break_type_name"));
				
				alInner.add(hmEmpName.get(rs.getString("user_id")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
				al.add(alInner);
				
				hmRosterBreakPolicyReport.put(rs.getString("break_policy_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRosterBreakPolicyReport", hmRosterBreakPolicyReport);
			
//			pst = con.prepareStatement("select * from roster_fullday_policy where org_id =? and wlocation_id=?  order by effective_date desc");
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
//			pst.setInt(2, uF.parseToInt(getStrWLocation()));
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_fullday_policy where roster_full_policy_id > 0 ");
			if(uF.parseToInt(getStrOrg())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getStrWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getStrWLocation()));
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" order by effective_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String,List<String>> hmRosterFDPolicyReport = new LinkedHashMap<String,List<String>>();
			while(rs.next()){
				
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("roster_full_policy_id"));
				
				alInner.add(Math.abs(uF.parseToDouble(rs.getString("time_value")))+"");
				alInner.add(rs.getString("_mode"));
				alInner.add(rs.getString("days"));
				alInner.add(rs.getString("months"));
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("policy_status"));
				
				alInner.add(hmEmpName.get(rs.getString("user_id")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				
//				al.add(alInner);
				
				hmRosterFDPolicyReport.put(rs.getString("roster_full_policy_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRosterFDPolicyReport", hmRosterFDPolicyReport);
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from roster_halfday_fullday_hrs_policy where roster_halfday_fullday_hrs_id > 0 ");
			if(uF.parseToInt(getStrOrg())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getStrOrg()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getStrWLocation())>0) {
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getStrWLocation()));
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			sbQuery.append(" order by effective_date desc");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String,List<String>> hmRosterHDFDMinHrsPolicyReport = new LinkedHashMap<String,List<String>>();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("roster_halfday_fullday_hrs_id"));
				alInner.add(rs.getString("exception_type"));
				alInner.add(rs.getString("min_hrs"));
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(hmEmpName.get(rs.getString("added_by")));
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("policy_status"));
				hmRosterHDFDMinHrsPolicyReport.put(rs.getString("roster_halfday_fullday_hrs_id"), alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmRosterHDFDMinHrsPolicyReport", hmRosterHDFDMinHrsPolicyReport);
			
			
			
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

	public String getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

}
