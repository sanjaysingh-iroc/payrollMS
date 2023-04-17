package com.konnect.jpms.leave;

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

public class LeaveBreakTypeReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String strUserType = null;
	HttpSession session;
	CommonFunctions CF = null;
	List<FillOrganisation> orgList;
	String strOrg;
	String strLocation;
	private static Logger log = Logger.getLogger(LeaveBreakTypeReport.class);
	
	List<FillWLocation> workList;
	
	public String execute() throws Exception { 
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, PLeaveTypeBreakReport);
		String strType = request.getParameter("type");
		if(strType!=null && strType.equalsIgnoreCase("level")){
			request.setAttribute(PAGE, PLeaveTypeReportLevelWise);	
		}
		
		request.setAttribute(TITLE, TViewLeaveBreakType);
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String) session.getAttribute(USERTYPE);
		
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		orgList = new FillOrganisation(request).fillOrganisation();
		
		if(getStrOrg()==null && orgList!=null && orgList.size()>0){
			setStrOrg(orgList.get(0).getOrgId());
			workList = new FillWLocation(request).fillWLocation(orgList.get(0).getOrgId().trim());
		}else{
			workList = new FillWLocation(request).fillWLocation(getStrOrg().trim());
		}
		
		if(getStrLocation()==null && workList!=null && workList.size()>0){
			setStrLocation(workList.get(0).getwLocationId());
		}
		
		if(strType!=null && strType.equalsIgnoreCase("level")){
			viewLeaveTypeLevelWise(uF);
			if(getStrLocation()==null){
				setStrLocation(getStrLocation());
			}
		}else{
			viewLeaveType(uF);
			if(getStrLocation()==null){
				setStrLocation(getStrLocation());
			}
		}
		
		return loadLeaveType();
		
		
	} 
	 
	List<FillLeaveType> leaveTypeList;
	List<FillColour> colourList;
	
	public String loadLeaveType(){	
		
		UtilityFunctions uF = new UtilityFunctions();
		leaveTypeList = new FillLeaveType(request).fillLeave();
		colourList = new FillColour(request).fillColour();
		
		request.setAttribute("leaveTypeList", leaveTypeList);
		request.setAttribute("colourList", colourList);
		
		/*int leaveTypeId;
		String leaveTypeName;
		int i=0;
		StringBuilder sbLeaveTypeList = new StringBuilder();
		sbLeaveTypeList.append("{");
		for(i=0; i<leaveTypeList.size()-1;i++ ) {
			leaveTypeId = uF.parseToInt((leaveTypeList.get(i)).getLeaveTypeId());
			leaveTypeName = leaveTypeList.get(i).getLeavetypeName();
			sbLeaveTypeList.append("\""+ leaveTypeId+"\":\""+leaveTypeName+"\",");
		}
		
		if(i>0){
			leaveTypeId = uF.parseToInt((leaveTypeList.get(i)).getLeaveTypeId());
			leaveTypeName = leaveTypeList.get(i).getLeavetypeName();
			sbLeaveTypeList.append("\""+ leaveTypeId+"\":\""+leaveTypeName+"\",");	//no comma for last record
			sbLeaveTypeList.append("}");
			
		}*/
		
		
		
		
		/*
		String colourValue;
		String colourName;
		StringBuilder sbcolourlList = new StringBuilder();
		sbcolourlList.append("{");
		
		
		for(i=0; i<colourList.size()-1;i++ ) {
			colourValue = (colourList.get(i)).getColourValue();
			colourName = colourList.get(i).getColourName();
			sbcolourlList.append("\""+ colourValue+"\":\""+colourName+"\",");
		}
		
		colourValue = (colourList.get(i)).getColourValue();
		colourName = colourList.get(i).getColourName();
		sbcolourlList.append("\""+ colourValue+"\":\""+colourName+"\" ");	//no comma for last record
		sbcolourlList.append("}");
		
		
		request.setAttribute("sbLeaveTypeList", sbLeaveTypeList.toString());*/
//		request.setAttribute("sbcolourlList", sbcolourlList.toString());
//		request.setAttribute("colourList", colourList);
		
		
		return LOAD;
	}
	
	public String viewLeaveType(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			
		
			
//			if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || 
//					strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) ||
//					strUserType.equalsIgnoreCase(HRMANAGER) )) {
//				pst = con.prepareStatement(selectLeaveTypeR);
//			}else{
//				return ACCESS_DENIED;
//			}
			
			pst = con.prepareStatement(selectBreakTypeR);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			   
			Map hmLeaveTypeMap = new HashMap();
			Map hmLeavePoliciesMap = new HashMap();
			
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("break_type_id"));
				alInner.add(rs.getString("break_type_code"));
				alInner.add(rs.getString("break_type_name"));
				alInner.add(rs.getString("break_type_colour"));
				
				hmLeaveTypeMap.put(rs.getString("break_type_id"), alInner);
				
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectBreakTypes);
//			pst.setInt(1, uF.parseToInt(getStrOrg()));
			pst.setInt(1, uF.parseToInt(getStrLocation()));
			rs = pst.executeQuery();
			String strLeaveTypeOld = null;
			String strLeaveTypeNew = null;
			
			while(rs.next()){
				strLeaveTypeNew = rs.getString("break_type_id");
				if(strLeaveTypeNew!=null && !strLeaveTypeNew.equalsIgnoreCase(strLeaveTypeOld)){
					alInner = new ArrayList<String>();
				}
				
				
				alInner.add(rs.getString("emp_break_type_id"));
				alInner.add(rs.getString("level_id"));
				alInner.add(rs.getString("level_code"));
				alInner.add(rs.getString("no_of_break_monthly"));
				alInner.add(uF.showYesNo(rs.getString("is_carryforward")));
				alInner.add(uF.showYesNo(rs.getString("is_monthly_carryforward")));
				alInner.add(hmEmpName.get(rs.getString("user_id")));
				alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));

				
				hmLeavePoliciesMap.put(rs.getString("break_type_id"), alInner);
				
				strLeaveTypeOld = strLeaveTypeNew;
			}
			rs.close();
			pst.close();	
			
			
			request.setAttribute("hmLeavePoliciesMap", hmLeavePoliciesMap);
			request.setAttribute("hmLeaveTypeMap", hmLeaveTypeMap);
			request.setAttribute("reportList", al);
			
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
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			pst = con.prepareStatement(selectLevel1);
			pst.setInt(1, uF.parseToInt(getStrOrg()));
			   
			Map hmLevelMap = new HashMap();
			Map hmLeavePoliciesMap = new HashMap();
			
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
			
			
			Map<String, String> hmBreakTypeMap = CF.getBreakTypeMap(con);
			
			
			pst = con.prepareStatement(selectBreakTypesLevel);
			rs = pst.executeQuery();
			String strLevelOld = null;
			String strLevelNew = null;
			
			while(rs.next()){
				strLevelNew = rs.getString("level_id");
				if(strLevelNew!=null && !strLevelNew.equalsIgnoreCase(strLevelOld)){
					alInner = new ArrayList<String>();
				}
				
				if(hmBreakTypeMap.get(rs.getString("break_type_id"))==null){
					continue;
				}
				
				alInner.add(rs.getString("emp_break_type_id"));
				alInner.add(rs.getString("break_type_id"));
				
//				alInner.add(rs.getString("level_code"));
				alInner.add(hmBreakTypeMap.get(rs.getString("break_type_id")));
				
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
				
				hmLeavePoliciesMap.put(strLevelNew, alInner);
				
				strLevelOld = strLevelNew;
			}
			rs.close();
			pst.close();	
			
			
			
			request.setAttribute("hmLeavePoliciesMap", hmLeavePoliciesMap);
			request.setAttribute("hmLevelMap", hmLevelMap);
			
			
			request.setAttribute("reportList", al);
			
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
	
}
