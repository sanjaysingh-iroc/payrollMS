package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.reports.EmployeeLeaveBreakdown;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveEncashmentReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	String strSessionEmpId = null; 
	 
	List<FillOrganisation> orgList;	
	List<FillLevel> levelList;
	private String level;
	String f_org;
	List<FillWLocation> workLocationList;
	String location;
	String paycycle;
	List<FillPayCycles> paycycleList;
//	List<FillFinancialYears> financialYearList;
//	String financialYear;

	private static Logger log = Logger.getLogger(EmployeeLeaveBreakdown.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions(); 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
				
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/payroll/reports/LeaveEncashmentReport.jsp");
		request.setAttribute(TITLE, "Leave Encashment Report");
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		Database db=new Database();
		db.setRequest(request);
		Connection con=null;
		con=db.makeConnection(con);
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(), CF, getF_org());
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
//		if (getFinancialYear() == null) {
//			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
//			setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
//		}
//		
//		String[] strDate=getFinancialYear().split("-");
		
		viewEmployeeLeaveBreakdown(uF,strPayCycleDates[0],strPayCycleDates[1],strPayCycleDates[2]);			
		return loadManagerLeaveApproval(uF);

	}
	
	
	public String loadManagerLeaveApproval(UtilityFunctions uF){	
//		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		paycycleList = new FillPayCycles().fillPayCycles(CF, getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			workLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		return "load";
	}
	
	public String viewEmployeeLeaveBreakdown(UtilityFunctions uF,String strD1,String strD2,String strPC){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, strUserType, strSessionEmpId);
			Map<String, String> hmEmpCodeMap = CF.getEmpCodeMap(con);

			Map<String, String> hmLeaveEncashment = CF.getLeaveEncashment(con, uF, strD1, strD2, strPC);

			
			pst = con.prepareStatement("select * from payroll_generation where paycycle=? and salary_head_id=? and amount>0");
			pst.setInt(1,uF.parseToInt(strPC));
			pst.setInt(2,LEAVE_ENCASHMENT);
			Map<String, String> hmLeaveEncashmentAmt = new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmLeaveEncashmentAmt.put(rs.getString("emp_id"), rs.getString("amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);

			request.setAttribute("hmEmployeeNameMap", hmEmployeeNameMap);
			request.setAttribute("hmLeaveEncashment", hmLeaveEncashment);
			request.setAttribute("hmLeaveEncashmentAmt", hmLeaveEncashmentAmt);
			  

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


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}


	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}


	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}


	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}


//	public List<FillFinancialYears> getFinancialYearList() {
//		return financialYearList;
//	}
//
//
//	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
//		this.financialYearList = financialYearList;
//	}
//
//
//	public String getFinancialYear() {
//		return financialYear;
//	}
//
//
//	public void setFinancialYear(String financialYear) {
//		this.financialYear = financialYear;
//	}
//	
	

}
