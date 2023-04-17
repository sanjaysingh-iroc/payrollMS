package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PTaxExcelReports extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	String financialYear;
	String strMonth;
	
	String strUserType; 
	String strSessionEmpId;
	
	HttpSession session;
	CommonFunctions CF;
	UtilityFunctions uF = new UtilityFunctions();
	
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_org;
	String f_service;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		
		/*setStrMonth("3");
		setFinancialYear("01/04/2011-31/03/2012");*/
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		getPTaxPDFReport();
		
		return "";

	}
	
	
	private void getPTaxPDFReport() {
		
		if(uF.parseToInt(getF_org())>-1){
			//viewPTaxReport(uF);					
			if(uF.parseToInt(getF_strWLocation())>-1){				
				if(uF.parseToInt(getF_department())>-1){
					
					/*if(uF.parseToInt(getF_level())>-1){
						viewPTaxReport();
						generatePTaxExcelReport();
					}else{
						viewPTaxReportByLevel(uF);
						generatePTaxExcelReportBy();
					}*/
					if(uF.parseToInt(getF_service())>-1){
						viewPTaxReport();
						generatePTaxExcelReport();
					}else{
						viewPTaxReportByService(uF);
						generatePTaxExcelReportBy();
					}
					
				}else{
					viewPTaxReportByDepartment(uF);
					generatePTaxExcelReportBy();
				}
			}else{
				viewPTaxReportByLocation(uF);
				generatePTaxExcelReportBy();
			}			
			
		}else{
			if(uF.parseToInt(getF_org())==-1){
				viewPTaxReportByOrg();
				generatePTaxExcelReportBy();
			}
		}
		
		
	}


	private void viewPTaxReportByService(UtilityFunctions uF) {

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
					"where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? and is_paid=true " +
					" and pg.emp_id=eod.emp_id ");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, PROFESSIONAL_TAX);
						
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("PTAX_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpPTax.put(rs.getString("service_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
						
			sbQuery = new StringBuilder();
			sbQuery.append("select pg.service_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true " +
					" and pg.emp_id=eod.emp_id ");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpPTax.get(rs.getString("service_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				hmEmpPTax.put(rs.getString("service_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
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
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void viewPTaxReportByLevel(UtilityFunctions uF2) {

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
					" where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? " +
					" and is_paid=true  and pg.emp_id=eod.emp_id and eod.grade_id in (select grade_id from designation_details dd, " +
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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, PROFESSIONAL_TAX);
						
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("PTAX_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpPTax.put(rs.getString("level_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
						
			sbQuery = new StringBuilder();
			
			sbQuery.append("select  sum(amount) as amount, month, year,level_id from " +
					" (select sum(amount) as amount, month, year,grade_id from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=?  " +
					" and is_paid=true  and pg.emp_id=eod.emp_id and eod.grade_id in (select grade_id from designation_details dd, " +
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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpPTax.get(rs.getString("level_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				hmEmpPTax.put(rs.getString("level_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
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
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void viewPTaxReportByDepartment(UtilityFunctions uF2) {

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
					"where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? and is_paid=true " +
					" and pg.emp_id=eod.emp_id ");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, PROFESSIONAL_TAX);
						
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("PTAX_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpPTax.put(rs.getString("depart_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
						
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.depart_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true " +
					" and pg.emp_id=eod.emp_id ");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpPTax.get(rs.getString("depart_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				hmEmpPTax.put(rs.getString("depart_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void viewPTaxReportByLocation(UtilityFunctions uF2) {

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
					"where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? and is_paid=true " +
					" and pg.emp_id=eod.emp_id ");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, PROFESSIONAL_TAX);
						
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("PTAX_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpPTax.put(rs.getString("wlocation_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.wlocation_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true " +
					" and pg.emp_id=eod.emp_id ");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpPTax.get(rs.getString("wlocation_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				hmEmpPTax.put(rs.getString("wlocation_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void generatePTaxExcelReportBy() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEmpPTax = (Map)request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con);
			Map<String, String> hmLevelMap =(Map<String, String>)request.getAttribute("hmLevelMap");
			Map<String, String> hmServicesMap =(Map<String, String>)request.getAttribute("hmServicesMap");
			
			String title="";
			if(uF.parseToInt(getF_org())==-1){
				title ="Organization";
			}else if(uF.parseToInt(getF_strWLocation())==-1){
				title ="Location";
			}else if(uF.parseToInt(getF_department())==-1){
				title ="Department";
			}else if(uF.parseToInt(getF_level())==-1){
				title ="Level";
			}else if(uF.parseToInt(getF_service())==-1){
				title ="Service";
			}

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("PTax Register for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM") +" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+" ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle(title,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Salary",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		
			Set set = hmEmpPTax.keySet();
			Iterator it = set.iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblPTaxAmountTotal = 0;
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map hmInner = (Map)hmEmpPTax.get(strEmpId);
				if(hmInner==null)hmInner=new HashMap();
				
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblPTaxAmountTotal+=uF.parseToDouble((String)hmInner.get("PTAX_AMOUNT"));
				
				String strName="";
				if(uF.parseToInt(getF_org())==-1){
					strName =uF.showData((String)hmOrg.get(strEmpId), "");
				}else if(uF.parseToInt(getF_strWLocation())==-1){
					strName =uF.showData((String)hmWLocation.get(strEmpId), "");
				}else if(uF.parseToInt(getF_department())==-1){
					strName =uF.showData((String)hmDept.get(strEmpId), "");
				}else if(uF.parseToInt(getF_level())==-1){
					strName =uF.showData((String)hmLevelMap.get(strEmpId), "");
				}else if(uF.parseToInt(getF_service())==-1){
					strName =uF.showData((String)hmServicesMap.get(strEmpId), "");
				}
				String salary=uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0");
				String Amount=uF.showData((String)hmInner.get("PTAX_AMOUNT"),"0");
				
				
				count++;
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(""+count,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(salary,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(Amount,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportData.add(innerList);
				
			}
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("Total",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(dblGrossAmountTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(dblPTaxAmountTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			reportData.add(innerList);
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=PTaxExcelReports.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		
	}catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeConnection(con);
	}
	
}


	private void viewPTaxReportByOrg() {

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
					"where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? and is_paid=true " +
					" and pg.emp_id=eod.emp_id ");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, PROFESSIONAL_TAX);
			
			
			
			/*pst = con.prepareStatement("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ?");
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, PROFESSIONAL_TAX);*/
			
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("PTAX_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpPTax.put(rs.getString("org_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
						
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.org_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod " +
					" where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true " +
					" and pg.emp_id=eod.emp_id ");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpPTax.get(rs.getString("org_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				hmEmpPTax.put(rs.getString("org_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
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
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	public void generatePTaxExcelReport(){
		
		try {

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEmpPTax = (Map)request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("PTax Register for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM") +" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+" ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Salary",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		
		
			Set set = hmEmpPTax.keySet();
			Iterator it = set.iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblPTaxAmountTotal = 0;
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map hmInner = (Map)hmEmpPTax.get(strEmpId);
				if(hmInner==null)hmInner=new HashMap();
				
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblPTaxAmountTotal+=uF.parseToDouble((String)hmInner.get("PTAX_AMOUNT"));
				
				String empCode=uF.showData((String)hmEmpCode.get(strEmpId), "");
				String empName=uF.showData((String)hmEmpName.get(strEmpId),"");
				String salary=uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0");
				String Amount=uF.showData((String)hmInner.get("PTAX_AMOUNT"),"0");
				
				
				count++;
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(""+count,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(empCode,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(empName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(salary,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(Amount,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportData.add(innerList);
			}
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(dblGrossAmountTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(dblPTaxAmountTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			reportData.add(innerList);
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=PTaxExcelReports.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	
	public void viewPTaxReport() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
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
			sbQuery.append("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ? and is_paid=true");

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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, PROFESSIONAL_TAX);
			 
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("PTAX_AMOUNT", rs.getString("amount"));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				hmEmpPTax.put(rs.getString("emp_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id, sum(amount) as amount, month, year from payroll_generation where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true");

			
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
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			rs = pst.executeQuery();
			
			
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpPTax.get(rs.getString("emp_id"));
				if(hmEmpInner==null)hmEmpInner = new HashMap();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
				hmEmpPTax.put(rs.getString("emp_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private HttpServletResponse response;
	private HttpServletRequest request;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		
	}
	public String getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	public String getStrMonth() {
		return strMonth;
	}
	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}
	public String getStrUserType() {
		return strUserType;
	}
	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}
	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}
	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
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


	public String getF_service() {
		return f_service;
	}


	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	
}
