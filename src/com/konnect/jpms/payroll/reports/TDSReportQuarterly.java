package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TDSReportQuarterly  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(TDSReport.class);
	
	String financialYear;
	String strQuarterlyMonth;
	
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthsList;
	
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_org;
	List<FillOrganisation> orgList;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	String f_service;
	List<FillServices> serviceList;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, TTDSRegister);
		request.setAttribute(PAGE, "/jsp/payroll/reports/TDSReportQuarterly.jsp");
		

		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		if(strQuarterlyMonth==null){
			strQuarterlyMonth="4,5,6";
		}
		
		getTDSReport(uF);

		return loadPTaxReport(uF);
	}
	
	
	private void getTDSReport(UtilityFunctions uF) {
//		viewTDSReport(uF);
		if(uF.parseToInt(getF_org())>-1){
			if(uF.parseToInt(getF_strWLocation())>-1){				
				if(uF.parseToInt(getF_department())>-1){
					
					/*if(uF.parseToInt(getF_level())>-1){
						viewTDSReport(uF);
					}else{
						viewTDSReportByLevel(uF);
					}*/
					if(uF.parseToInt(getF_service())>-1){
						viewTDSReport(uF);
					}else{
						viewTDSReportByService(uF);
					}	
					
				}else{
					viewTDSReportByDepartment(uF);
				}
			}else{
				viewTDSReportByLocation(uF);
			}			
			
		}else{
			if(uF.parseToInt(getF_org())==-1){
				viewTDSReportByOrg(uF);
			}
		}
	}


	private void viewTDSReportByService(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pg.service_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					"where month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? " +
					"  and is_paid=true and pg.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			sbQuery.append("  group by pg.service_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
						
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpTDS = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("TDS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpTDS.put(rs.getString("service_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
						
			sbQuery = new StringBuilder();
			sbQuery.append("select pg.service_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? " +
					" and is_paid=true and pg.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}		
			
			sbQuery.append("  group by pg.service_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpTDS.get(rs.getString("service_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("service_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			pst = con.prepareStatement("SELECT * FROM services order by service_name");
			rs = pst.executeQuery();
			Map<String, String> hmServicesMap = new HashMap<String, String>();
			while (rs.next()) {
				hmServicesMap.put(rs.getString("service_id"), rs.getString("service_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmServicesMap", hmServicesMap);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	
		
	}


	private void viewTDSReportByLevel(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select  sum(amount) as amount, month, year,level_id from " +
					" (select sum(amount) as amount, month, year,grade_id from payroll_generation pg,employee_official_details eod " +
					" where month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? " +
					"  and is_paid=true and pg.emp_id=eod.emp_id and eod.grade_id in (select grade_id from designation_details dd, " +
					"level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month, year,grade_id)as a," +
					" (select grade_id,ld.level_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
					"where a.grade_id=b.grade_id group by month, year,level_id");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
						
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpTDS = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("TDS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpTDS.put(rs.getString("level_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
						
			sbQuery = new StringBuilder();
			
			sbQuery.append("select  sum(amount) as amount, month, year,level_id from " +
					" (select sum(amount) as amount, month, year,grade_id from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=?  " +
					"  and is_paid=true and pg.emp_id=eod.emp_id and eod.grade_id in (select grade_id from designation_details dd, " +
					"level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month, year,grade_id)as a," +
					" (select grade_id,ld.level_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
					"where a.grade_id=b.grade_id group by month, year,level_id");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpTDS.get(rs.getString("level_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("level_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			pst = con.prepareStatement("SELECT * FROM level_details order by level_id");
			rs = pst.executeQuery();
			Map<String, String> hmLevelMap = new HashMap<String, String>();
			while (rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmLevelMap", hmLevelMap);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void viewTDSReportByDepartment(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			

			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.depart_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					"where month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? " +
					"  and is_paid=true and pg.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append("  group by eod.depart_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
						
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpTDS = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("TDS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpTDS.put(rs.getString("depart_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
						
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.depart_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? " +
					" and is_paid=true and pg.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}		
			
			sbQuery.append("  group by eod.depart_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpTDS.get(rs.getString("depart_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("depart_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void viewTDSReportByLocation(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.wlocation_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					"where month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? " +
					" and is_paid=true and pg.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append("  group by eod.wlocation_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
						
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpTDS = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("TDS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpTDS.put(rs.getString("wlocation_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.wlocation_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? " +
					"  and is_paid=true and pg.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}		
			
			sbQuery.append("  group by eod.wlocation_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpTDS.get(rs.getString("wlocation_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("wlocation_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}

	}


	private void viewTDSReportByOrg(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.org_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					"where month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? " +
					"  and is_paid=true and pg.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append("  group by eod.org_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
			
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpTDS = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("TDS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpTDS.put(rs.getString("org_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.org_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? " +
					"  and is_paid=true and pg.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
			}		
			
			sbQuery.append("  group by eod.org_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpTDS.get(rs.getString("org_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("org_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOrg", hmOrg);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	public String loadPTaxReport(UtilityFunctions uF) {
		
		
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthsList = new FillMonth().fillQuarterlyMonth();
		
		orgList = new FillOrganisation(request).fillOrganisation();
		orgList.add(new FillOrganisation("0","All Organization"));
		
		Collections.sort(orgList, new Comparator<FillOrganisation>() {

			@Override
			public int compare(FillOrganisation o1, FillOrganisation o2) {
				return o1.getOrgId().compareTo(o2.getOrgId());
			}
		});
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			wLocationList.add(new FillWLocation("-1","Work Location Wise"));
			wLocationList.add(new FillWLocation("0","All Work Location"));
			
			
		}else{
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			wLocationList.add(new FillWLocation("0","All Work Location"));
		}
		
		Collections.sort(wLocationList, new Comparator<FillWLocation>() {

			@Override
			public int compare(FillWLocation o1, FillWLocation o2) {
				return o1.getwLocationId().compareTo(o2.getwLocationId());
			}
		});
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1){
			departmentList = new FillDepartment(request).fillDepartment();
			departmentList.add(new FillDepartment("-1","Department Wise"));
			departmentList.add(new FillDepartment("0","All Departments"));
			
			
		}else{
			departmentList = new FillDepartment(request).fillDepartment();
			departmentList.add(new FillDepartment("0","All Departments"));
		}
		
		Collections.sort(departmentList, new Comparator<FillDepartment>() {

			@Override
			public int compare(FillDepartment o1, FillDepartment o2) {
				return o1.getDeptId().compareTo(o2.getDeptId());
			}
		});
		
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1 && getF_department()!=null && uF.parseToInt(getF_department())>-1){
			levelList = new FillLevel(request).fillLevel();
			levelList.add(new FillLevel("-1","Level Wise"));
			levelList.add(new FillLevel("0","All Levels"));
			
			
			
		}else{		
			levelList = new FillLevel(request).fillLevel();
			levelList.add(new FillLevel("0","All Levels"));
		}
		
		Collections.sort(levelList, new Comparator<FillLevel>() {

			@Override
			public int compare(FillLevel o1, FillLevel o2) {
				return o1.getLevelId().compareTo(o2.getLevelId());
			}
		});
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1 && getF_department()!=null && uF.parseToInt(getF_department())>-1){
			serviceList = new FillServices(request).fillServices(getF_org(), uF);
			serviceList.add(new FillServices("-1","Service Wise"));
			serviceList.add(new FillServices("0","All Service"));
			 
		}else{		
			serviceList = new FillServices(request).fillServices(getF_org(), uF);
			serviceList.add(new FillServices("0","All Service"));
		}
		
		Collections.sort(serviceList, new Comparator<FillServices>() {

			@Override
			public int compare(FillServices o1, FillServices o2) {
				return o1.getServiceId().compareTo(o2.getServiceId());
			}
		});
		
		return LOAD;
	}
	
	public String viewTDSReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			
			
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			//pst = con.prepareStatement("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ?");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id, amount, month, year from payroll_generation where month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ? and is_paid=true  ");

			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(" and emp_id in ( select emp_id from employee_official_details where emp_id>0 ");
			}
			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id="+uF.parseToInt(getF_org()));
			}
			
			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(")");
			}

			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id ="+uF.parseToInt(getF_service()));
			}
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
			
			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpTDS = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("TDS_AMOUNT", rs.getString("amount"));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpTDS.put(rs.getString("emp_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			//pst = con.prepareStatement("select emp_id, sum(amount) as amount, month, year from payroll_generation where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? group by emp_id,month, year");
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id, sum(amount) as amount, month, year from payroll_generation where earning_deduction = 'E' and month in("+getStrQuarterlyMonth()+") and financial_year_from_date=? and financial_year_to_date=? and is_paid=true  ");

			
			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(" and emp_id in ( select emp_id from employee_official_details where emp_id>0 ");
			}
				
			
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id="+uF.parseToInt(getF_org()));
			}
			
			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(")");
			}

			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append("  group by emp_id,month, year");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpTDS.get(rs.getString("emp_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("emp_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
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
	
	/*public String viewTDSReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		

		try {
			
			
			Map hmEmpName = CF.getEmpNameMap(null, null);
			Map hmEmpCode = CF.getEmpCodeMap();
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears().fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ?");
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, TDS);
			
			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpTDS = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("TDS_AMOUNT", rs.getString("amount"));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpTDS.put(rs.getString("emp_id"), hmEmpInner);
			}
			
			
			pst = con.prepareStatement("select emp_id, sum(amount) as amount, month, year from payroll_generation where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? group by emp_id,month, year");
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpTDS.get(rs.getString("emp_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("emp_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return SUCCESS;

	}*/

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;


	}


	public String getFinancialYear() {
		return financialYear;
	}


	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}


//	public String getStrMonth() {
//		return strMonth;
//	}
//
//
//	public void setStrMonth(String strMonth) {
//		this.strMonth = strMonth;
//	}


	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}


//	public List<FillMonth> getMonthList() {
//		return monthList;
//	}
//
//
//	public void setMonthList(List<FillMonth> monthList) {
//		this.monthList = monthList;
//	}


	public String getStrQuarterlyMonth() {
		return strQuarterlyMonth;
	}


	public void setStrQuarterlyMonth(String strQuarterlyMonth) {
		this.strQuarterlyMonth = strQuarterlyMonth;
	}


	public List<FillMonth> getMonthsList() {
		return monthsList;
	}


	public void setMonthsList(List<FillMonth> monthsList) {
		this.monthsList = monthsList;
	}


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String getF_department() {
		return f_department;
	}


	public void setF_department(String f_department) {
		this.f_department = f_department;
	}


	public String getF_level() {
		return f_level;
	}


	public void setF_level(String f_level) {
		this.f_level = f_level;
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


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getF_service() {
		return f_service;
	}


	public void setF_service(String f_service) {
		this.f_service = f_service;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

}

