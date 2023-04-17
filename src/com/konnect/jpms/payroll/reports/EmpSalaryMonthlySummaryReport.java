package com.konnect.jpms.payroll.reports;

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

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpSalaryMonthlySummaryReport  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(EmpSalaryMonthlySummaryReport.class);
	  
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, TReportEmpSalaryYearly);
		request.setAttribute(PAGE, PReportEmpSalarySummaryYearly);
		

		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		} 
		
		
		orgList = new FillOrganisation(request).fillOrganisation();
		if(getF_org()==null){
			setF_org(orgList.get(0).getOrgId());
		}
		wlocationList = new FillWLocation(request).fillWLocation(getF_org());
		if(getWlocation()==null && wlocationList!=null && wlocationList.size()>0){			
//			setWlocation(wlocationList.get(0).getwLocationId());
			setWlocation((String)session.getAttribute(WLOCATIONID));
		}
		
		viewSalaryYearlyReport(uF);

		return loadSalaryYearlyReport();

	}
	
	
	String paycycle;
	String wlocation;
	String f_org;
	List<FillOrganisation> orgList;
	
	List<FillPayCycles> paycycleList; 
	List<FillWLocation> wlocationList;
	
	public String loadSalaryYearlyReport() {
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		
		return LOAD;
	}
	
	
	public String viewSalaryYearlyReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			String[] strPayCycleDates = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;
			String strPC = null;

			if (getPaycycle() != null) {
				
				strPayCycleDates = getPaycycle().split("-");
				strPayCycleStart = strPayCycleDates[0];
				strPayCycleEnd = strPayCycleDates[1];
				strPC = strPayCycleDates[2];
			
			} else {
				
//				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPayCycleStart = strPayCycleDates[0];
				strPayCycleEnd = strPayCycleDates[1];
				strPC = strPayCycleDates[2];
				 
			}
			
			
			String strMonth = uF.getDateFormat(strPayCycleStart, DATE_FORMAT, "MM/yyyy");
			request.setAttribute("strMonth", strMonth);
			
			Map hmEarningSalaryMap = new LinkedHashMap();
			Map hmEarningSalaryTotalMap = new HashMap();
			Map hmDeductionSalaryTotalMap = new HashMap();
			Map hmEmpInner = new HashMap();
			
			List alEarning = new ArrayList();
			List alDeduction = new ArrayList();
			
			
			/*Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "yyyy")));
			
			List alMonth = new ArrayList();
			
			for(int i=0; i<12; i++){
				alMonth.add((cal.get(Calendar.MONTH)+1)+"");
				
				cal.add(Calendar.MONTH, 1);
			}*/
			
			
			con = db.makeConnection(con);
			
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			pst = con.prepareStatement("select * from work_location_info where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getWlocation()));
			rs = pst.executeQuery();
			String strWLocation=null;
			while(rs.next()){
				strWLocation = rs.getString("wlocation_name");
			}
			rs.close();
			pst.close();
			request.setAttribute("strWLocation", strWLocation);
			
			pst = con.prepareStatement("select amount, emp_id, salary_head_id, earning_deduction from payroll_generation where paycycle=? " +
					"and emp_id in (select emp_id from employee_official_details where wlocation_id =? ) and is_paid = true order by emp_id");
			pst.setInt(1, uF.parseToInt(strPC));		
			pst.setInt(2, uF.parseToInt(getWlocation()));
			rs = pst.executeQuery();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			while(rs.next()){
				
				strEmpIdNew = rs.getString("emp_id");
				
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					hmEmpInner = new HashMap();
				}
				
				
				hmEmpInner.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEarningSalaryMap.put(strEmpIdNew, hmEmpInner);
				
				/*
				double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get(rs.getString("month")));
				dblAmount += rs.getDouble("amount");
				hmEarningSalaryTotalMap.put(rs.getString("month"), uF.formatIntoTwoDecimal(dblAmount));*/
				
				
				if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarning.contains(rs.getString("salary_head_id"))){
					alEarning.add(rs.getString("salary_head_id"));
				}
				
				if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeduction.contains(rs.getString("salary_head_id"))){
					alDeduction.add(rs.getString("salary_head_id"));
				}
				
				strEmpIdOld  = strEmpIdNew ;
			}
			rs.close();
			pst.close();
			
			
			
//			System.out.println("hmEarningSalaryMap========>"+hmEarningSalaryMap);
			
			
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			
			request.setAttribute("alEarning", alEarning);
			request.setAttribute("alDeduction", alDeduction);
			
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
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

	public String getWlocation() {
		return wlocation;
	}


	public void setWlocation(String wlocation) {
		this.wlocation = wlocation;
	}


	public List<FillWLocation> getWlocationList() {
		return wlocationList;
	}


	public void setWlocationList(List<FillWLocation> wlocationList) {
		this.wlocationList = wlocationList;
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


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


}
