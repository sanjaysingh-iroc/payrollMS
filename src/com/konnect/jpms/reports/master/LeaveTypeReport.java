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

import com.konnect.jpms.select.FillColour;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveTypeReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strUserType = null;
	public HttpSession session;
	public CommonFunctions CF = null;
	private List<FillOrganisation> orgList;
	private String strOrg;
	private String strLocation;
	
	private List<FillWLocation> workList;
	private List<FillLeaveType> leaveTypeList;
	private List<FillColour> colourList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;
	
	public String execute() throws Exception { 
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PLeaveTypeReport);
		String strType = request.getParameter("type");
		if(strType!=null && strType.equalsIgnoreCase("level")){
			request.setAttribute(PAGE, PLeaveTypeReportLevelWise);	
//			System.out.println("page====>"+(String)request.getAttribute(PAGE));
		}
		request.setAttribute(TITLE, TViewLeaveType);
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		orgList = new FillOrganisation(request).fillOrganisation();
		
				
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
		
		if(uF.parseToInt(getStrLocation()) == 0 && workList!=null && workList.size()>0){
			setStrLocation(workList.get(0).getwLocationId());
		}
		
		if(strType!=null && strType.equalsIgnoreCase("level")){
			viewLeaveTypeLevelWise(uF);	
		}else{
			viewLeaveType(uF);
		}
		
		getSelectedFilter(uF);
		
		return loadLeaveType();
		
		
	} 
	 
	public String loadLeaveType(){	
		leaveTypeList = new FillLeaveType(request).fillLeave();
		colourList = new FillColour(request).fillColour();
		
		request.setAttribute("leaveTypeList", leaveTypeList);
		request.setAttribute("colourList", colourList);
		
		return LOAD;
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
	

	
	public String viewLeaveType(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			
//			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || 
//					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) ||
//					strUserType.equalsIgnoreCase(HRMANAGER) )) {
//				pst = con.prepareStatement(selectLeaveTypeR);
//			}else{
//				return ACCESS_DENIED;
//			}
			
			pst = con.prepareStatement(selectLeaveTypeR);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			   
			Map hmLeaveTypeMap = new HashMap();
			Map hmLeavePoliciesMap = new HashMap();
//			 System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				if(rs.getInt("leave_type_id")<0){
					continue;
				}
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("leave_type_id"));
				alInner.add(rs.getString("leave_type_code"));
				alInner.add(rs.getString("leave_type_name"));
				alInner.add(rs.getString("leave_type_colour"));
				alInner.add(rs.getString("is_compensatory"));
				alInner.add(""+uF.parseToBoolean(rs.getString("is_leave_opt_holiday")));
				
				hmLeaveTypeMap.put(rs.getString("leave_type_id"), alInner);
				
			}
			rs.close();
			pst.close();

//			System.out.println("hmLeaveTypeMap=====>"+hmLeaveTypeMap);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT e.*,ld.level_id,level_name,level_code from emp_leave_type e, level_details ld where e.level_id = ld.level_id ");
			if(getStrLocation()!=null && getStrOrg()!=null){
				sbQuery.append(" and e.org_id="+uF.parseToInt(getStrOrg()));
				sbQuery.append(" and e.wlocation_id="+uF.parseToInt(getStrLocation()));
			}
			sbQuery.append(" order by leave_type_id");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			String strLeaveTypeOld = null;
			String strLeaveTypeNew = null;
			
			while(rs.next()){
				strLeaveTypeNew = rs.getString("leave_type_id");
				if(strLeaveTypeNew!=null && !strLeaveTypeNew.equalsIgnoreCase(strLeaveTypeOld)){
					alInner = new ArrayList<String>();
				}
				
				
				alInner.add(rs.getString("emp_leave_type_id"));//0
				alInner.add(rs.getString("level_id"));//1
				
				alInner.add(rs.getString("level_code"));//2
				alInner.add(rs.getString("no_of_leave"));//3
				alInner.add(CF.getLeaveStartDate(rs.getString("effective_date_type")));//4
				alInner.add(uF.showYesNo(rs.getString("is_paid")));//5
				alInner.add(uF.showYesNo(rs.getString("is_carryforward")));//6
				
				alInner.add(rs.getString("monthly_limit"));//7
				alInner.add(rs.getString("consecutive_limit"));//8
				alInner.add(uF.showYesNo(rs.getString("is_monthly_carryforward")));//9
				
//				System.out.println("userId ==>"+rs.getString("user_id") +"==>userName==>"+hmEmpName.get(rs.getString("user_id")));
				alInner.add(hmEmpName.get(rs.getString("user_id")));//10
				alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));//11
				
				
				alInner.add(rs.getString("is_holiday_compensation"));//12
				alInner.add(rs.getString("is_weekly_compensation"));//13
				
				alInner.add(rs.getString("level_name"));//14
				
				alInner.add(rs.getString("is_leave_accrual"));	//15			
				alInner.add(uF.showYesNo(rs.getString("is_leave_accrual")));//16
				alInner.add(rs.getString("accrual_type"));//17
				alInner.add(rs.getString("accrual_days"));//18
				alInner.add(""+uF.parseToDouble(rs.getString("no_of_leave_monthly")));//19
				
				hmLeavePoliciesMap.put(rs.getString("leave_type_id"), alInner);
				
				strLeaveTypeOld = strLeaveTypeNew;
			}	
			rs.close();
			pst.close();
//			System.out.println("hmLeavePoliciesMap=====>"+hmLeavePoliciesMap);
			
			request.setAttribute("hmLeavePoliciesMap", hmLeavePoliciesMap);
			request.setAttribute("hmLeaveTypeMap", hmLeaveTypeMap);
			
			
			request.setAttribute("reportList", al);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	

	
	public String viewLeaveTypeLevelWise(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		
		try {

			
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmLeaveTypeMap = CF.getLeaveTypeMap(con);
			
			pst = con.prepareStatement(selectLevel1);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			   
			Map hmLevelMap = new HashMap();
			Map hmLeavePoliciesMap = new HashMap();
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				if(rs.getInt("level_id")<0){
					continue;
				}
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("level_id"));
				alInner.add(rs.getString("level_code"));
				alInner.add(rs.getString("level_name"));
				
				
				hmLevelMap.put(rs.getString("level_id"), alInner);
				
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmLevelMap=====>"+hmLevelMap);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT e.*,ld.level_id,level_name,level_code from emp_leave_type e, level_details ld where e.level_id = ld.level_id ");
			if(getStrLocation()!=null && getStrOrg()!=null){
				sbQuery.append(" and e.org_id="+uF.parseToInt(getStrOrg()));
				sbQuery.append(" and e.wlocation_id="+uF.parseToInt(getStrLocation()));
			}
			sbQuery.append(" order by ld.level_id");
			
//			pst = con.prepareStatement("SELECT * from emp_leave_type e, level_details ld where e.level_id = ld.level_id order by ld.level_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			String strLevelOld = null;
			String strLevelNew = null;
//			System.out.println("pst====>"+pst);
			while(rs.next()){
				strLevelNew = rs.getString("level_id");
				if(strLevelNew!=null && !strLevelNew.equalsIgnoreCase(strLevelOld)){
					alInner = new ArrayList<String>();
				}
				
				if(hmLeaveTypeMap.get(rs.getString("leave_type_id"))==null){
					continue;
				}
				
				alInner.add(rs.getString("emp_leave_type_id"));
				alInner.add(rs.getString("leave_type_id"));
				
				alInner.add(rs.getString("level_code"));
				alInner.add(hmLeaveTypeMap.get(rs.getString("leave_type_id"))); 
				
				alInner.add(rs.getString("no_of_leave"));
				alInner.add(CF.getLeaveStartDate(rs.getString("effective_date_type")));
				alInner.add(uF.showYesNo(rs.getString("is_paid")));
				alInner.add(uF.showYesNo(rs.getString("is_carryforward")));
				
				alInner.add(rs.getString("monthly_limit"));
				alInner.add(rs.getString("consecutive_limit"));
				alInner.add(uF.showYesNo(rs.getString("is_monthly_carryforward")));
				
				
				alInner.add(hmEmpName.get(rs.getString("user_id")));
				alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));
				
				alInner.add(rs.getString("is_holiday_compensation"));
				alInner.add(rs.getString("is_weekly_compensation"));
				
				alInner.add(rs.getString("is_leave_accrual"));				
				alInner.add(uF.showYesNo(rs.getString("is_leave_accrual")));
				alInner.add(rs.getString("accrual_type"));
				alInner.add(rs.getString("accrual_days"));
				alInner.add(rs.getString("no_of_leave_monthly"));
				
				hmLeavePoliciesMap.put(strLevelNew, alInner);
				
				strLevelOld = strLevelNew;
			}	
			rs.close();
			pst.close();
			
			
			
//			System.out.println("hmLeavePoliciesMap====>"+hmLeavePoliciesMap);
			
			request.setAttribute("hmLeavePoliciesMap", hmLeavePoliciesMap);
			request.setAttribute("hmLevelMap", hmLevelMap);
			
			
			request.setAttribute("reportList", al);
			
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

	public List<FillWLocation> getWorkList() {
		return workList;
	}

	public void setWorkList(List<FillWLocation> workList) {
		this.workList = workList;
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
	
}
