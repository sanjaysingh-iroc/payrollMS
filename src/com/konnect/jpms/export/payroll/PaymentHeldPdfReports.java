package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class PaymentHeldPdfReports implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false; 
	CommonFunctions CF = null;
	String financialYear;  
	String strMonth;
	
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_org;
	String f_service;
	List<FillServices> serviceList;
	
	String paramSelection;

	public void execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return;
		strUserType = (String) session.getAttribute(USERTYPE);
		
		UtilityFunctions uF =new UtilityFunctions();
		getPaymentHeldReport(uF);
		
		return;

	}

	private void getPaymentHeldReport(UtilityFunctions uF) {
		if(getParamSelection().equals("ORG")){
			viewPaymentHeldReportByOrg(uF);
			generateTDSReportBy(uF);
		} else if(getParamSelection().equals("WL")){
			viewPaymentHeldReportByLocation(uF);
			generateTDSReportBy(uF);
		} else if(getParamSelection().equals("DEPART")){
			viewPaymentHeldReportByDepartment(uF);
			generateTDSReportBy(uF);
		} else if(getParamSelection().equals("SBU")){
			viewPaymentHeldReportByService(uF);
			generateTDSReportBy(uF);
		} else {
			viewPaymentHeldReport(uF);
			generateTDSReport(uF);
		}
		
	}
	
//	private void getPaymentHeldReport(UtilityFunctions uF) {
//		if(uF.parseToInt(getF_org())>-1){
//			if(uF.parseToInt(getF_strWLocation())>-1){	
//				
//				if(uF.parseToInt(getF_department())>-1){
//					/*if(uF.parseToInt(getF_level())>-1){
//						viewPaymentHeldReport();
//						generateTDSReport();
//					}else{
//						viewPaymentHeldReportByLevel();
//						generateTDSReportBy();
//					}	*/
//					if(uF.parseToInt(getF_service())>-1){
//						viewPaymentHeldReport();
//						generateTDSReport();
//					}else{
//						viewPaymentHeldReportByService(uF);
//						generateTDSReportBy();
//					}
//					
//				}else{
//					viewPaymentHeldReportByDepartment();
//					generateTDSReportBy();
//				}
//			}else{
//				viewPaymentHeldReportByLocation();
//				generateTDSReportBy();
//			}			
//			
//		}else{
//			if(uF.parseToInt(getF_org())==-1){
//				viewPaymentHeldReportByOrg();
//				generateTDSReportBy();
//			}
//		}
//		
//	}
	

	private void viewPaymentHeldReportByService(UtilityFunctions uF) {

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
			
			sbQuery.append("select sum(amount)as amount,month,year,pg.service_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='D' and pg.service_id>0  ");
			
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
			sbQuery.append(" group by month,year,pg.service_id order by pg.service_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpDeduction = new HashMap();			
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblAmount = rs.getDouble("amount");
				hmEmpDeduction.put(rs.getString("service_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,pg.service_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='E' and pg.service_id>0 ");
			
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
			sbQuery.append(" group by month,year,pg.service_id order by pg.service_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			strMonth=null;
			strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			String strEmpIdOld=null;
			String strEmpIdNew=null;
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblDeduct=hmEmpDeduction!=null ? uF.parseToDouble(""+hmEmpDeduction.get(rs.getString("service_id"))) : 0;
				double dblAmount = rs.getDouble("amount")-dblDeduct;
				hmEmpPTax.put(rs.getString("service_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpPTax==>"+hmEmpPTax);
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

	private void viewPaymentHeldReportByLevel(UtilityFunctions uF) {

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
			
			sbQuery.append("select sum(amount)as amount,month,year,level_id from (select sum(amount)as amount,month,year,eod.grade_id " +
					" from employee_personal_details epd, employee_official_details eod,payroll_generation pg where pg.emp_id = eod.emp_id " +
					"and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id and month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and is_paid=false and earning_deduction='D' and eod.grade_id in (select grade_id from " +
					"designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");
			
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
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month,year,eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
					"where a.grade_id=b.grade_id group by level_id,month,year");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpDeduction = new HashMap();			
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblAmount = rs.getDouble("amount");

				hmEmpDeduction.put(rs.getString("level_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,level_id from (select sum(amount)as amount,month,year,eod.grade_id " +
					" from employee_personal_details epd, employee_official_details eod,payroll_generation pg where pg.emp_id = eod.emp_id " +
					"and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id and month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and is_paid=false and earning_deduction='E' and eod.grade_id in (select grade_id from " +
					"designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");
			
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
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month,year,eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
					"grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
					"where a.grade_id=b.grade_id group by level_id,month,year");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			strMonth=null;
			strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			String strEmpIdOld=null;
			String strEmpIdNew=null;
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblDeduct=hmEmpDeduction!=null ? uF.parseToDouble(""+hmEmpDeduction.get(rs.getString("level_id"))) : 0;
				double dblAmount = rs.getDouble("amount")-dblDeduct;

				hmEmpPTax.put(rs.getString("level_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpPTax==>"+hmEmpPTax);
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

	private void viewPaymentHeldReportByDepartment(UtilityFunctions uF) {

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
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.depart_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='D' ");
			
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
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month,year,eod.depart_id order by eod.depart_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpDeduction = new HashMap();			
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblAmount = rs.getDouble("amount");

				hmEmpDeduction.put(rs.getString("depart_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.depart_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='E' ");
			
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
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month,year,eod.depart_id order by eod.depart_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			strMonth=null;
			strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			String strEmpIdOld=null;
			String strEmpIdNew=null;
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblDeduct=hmEmpDeduction!=null ? uF.parseToDouble(""+hmEmpDeduction.get(rs.getString("depart_id"))) : 0;
				double dblAmount = rs.getDouble("amount")-dblDeduct;

				hmEmpPTax.put(rs.getString("depart_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpPTax==>"+hmEmpPTax);
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

	private void viewPaymentHeldReportByLocation(UtilityFunctions uF) {

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
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.wlocation_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='D' ");
			
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
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month,year,eod.wlocation_id order by eod.wlocation_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpDeduction = new HashMap();			
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblAmount = rs.getDouble("amount");

				hmEmpDeduction.put(rs.getString("wlocation_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.wlocation_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='E' ");
			
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
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month,year,eod.wlocation_id order by eod.wlocation_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			strMonth=null;
			strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			String strEmpIdOld=null;
			String strEmpIdNew=null;
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblDeduct=hmEmpDeduction!=null ? uF.parseToDouble(""+hmEmpDeduction.get(rs.getString("wlocation_id"))) : 0;
				double dblAmount = rs.getDouble("amount")-dblDeduct;

				hmEmpPTax.put(rs.getString("wlocation_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpPTax==>"+hmEmpPTax);
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

	private void viewPaymentHeldReportByOrg(UtilityFunctions uF) {

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
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.org_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='D' ");
			
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
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month,year,eod.org_id order by eod.org_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			String strMonth=null;
			String strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpDeduction = new HashMap();			
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblAmount = rs.getDouble("amount");

				hmEmpDeduction.put(rs.getString("org_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.org_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='E' ");
			
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
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" group by month,year,eod.org_id order by eod.org_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			strMonth=null;
			strYear=null;
			
			rs = pst.executeQuery();
			
			Map hmEmpPTax = new HashMap();
			
			String strEmpIdOld=null;
			String strEmpIdNew=null;
			
			while(rs.next()){				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblDeduct=hmEmpDeduction!=null ? uF.parseToDouble(""+hmEmpDeduction.get(rs.getString("org_id"))) : 0;
				double dblAmount = rs.getDouble("amount")-dblDeduct;

				hmEmpPTax.put(rs.getString("org_id"), uF.formatIntoTwoDecimal(dblAmount));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmpPTax==>"+hmEmpPTax);
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

	private void generateTDSReportBy(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			
			con = db.makeConnection(con);


			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			String strYear = (String) request.getAttribute("strYear");
			String strMonth = (String) request.getAttribute("strMonth");

			Map hmEmpPTax = (Map) request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map) request.getAttribute("hmEmpName");

			if (strFinancialYearStart != null && strFinancialYearEnd != null) {
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart,IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd,IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			
			String strOrg=(String)request.getAttribute("f_org");
			Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");
			if(hmOrg==null) hmOrg=new HashMap<String, String>();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con);
			Map<String, String> hmLevelMap =(Map<String, String>)request.getAttribute("hmLevelMap");
			Map<String, String> hmServicesMap =(Map<String, String>)request.getAttribute("hmServicesMap");


			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Salary held statement for the month of "
					+ uF.getDateFormat(strMonth, "MM", "MMMM")+ " "+ uF.getDateFormat(strYear, "yyyy", "yyyy")+ "</b></font></td></tr>" + "</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);

			document.add(new Paragraph(" "));

			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td align=\"center\"><font size=\"1\"><b>____________________________________________________________________________________________________________________________________________________________________________</b></font></td>"
					+ "</tr></table>";
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase4.add(supList4.get(0));
			document.add(phrase4);
			
			String title="";
			if(getParamSelection() != null && getParamSelection().equals("ORG")){
				title ="Organization";
			}else if(getParamSelection() != null && getParamSelection().equals("WL")){
				title ="Location";
			}else if(getParamSelection() != null && getParamSelection().equals("DEPART")){
				title ="Department";
			}else if(getParamSelection() != null && getParamSelection().equals("SBU")){
				title ="Service";
			}

			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td width=\"10%\"><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>"
					+ "<td width=\"70%\"><font size=\"1\"><b>&nbsp;"+title+"&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"right\" width=\"20%\"><font size=\"1\"><b>&nbsp;Net Pay&nbsp;&nbsp;</b></font></td></tr>"
					+ "</table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase5.add(supList4.get(0));
			document.add(phrase5);

			Set set = hmEmpPTax.keySet();
			Iterator it = set.iterator();
			int count = 0;
			double dblNetPayTotal = 0;
			while (it.hasNext()) {
				String strEmpId = (String) it.next();
				
				dblNetPayTotal+=uF.parseToDouble((String)hmEmpPTax.get(strEmpId));
				
				String strName="";
				if(getParamSelection() != null && getParamSelection().equals("ORG")){
					strName =uF.showData((String)hmOrg.get(strEmpId), "");
				}else if(getParamSelection() != null && getParamSelection().equals("WL")){
					strName =uF.showData((String)hmWLocation.get(strEmpId), "");
				}else if(getParamSelection() != null && getParamSelection().equals("DEPART")){
					strName =uF.showData((String)hmDept.get(strEmpId), "");
				}else if(getParamSelection() != null && getParamSelection().equals("SBU")){
					strName =uF.showData((String)hmServicesMap.get(strEmpId), "");
				}

				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td><font size=\"1\"><b>&nbsp;"+ (++count)+ "&nbsp;&nbsp;</b></font></td>"						
						+ "<td><font size=\"1\"><b>&nbsp;"+ strName+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEmpPTax.get(strEmpId), "0")+ "&nbsp;&nbsp;</b></font></td>" +
						"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);

				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>"
						+ "</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			if (count == 0) {
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td align=\"center\"><font size=\"1\"><b>&nbsp;No Data found</b></font></td>"
						+ "</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>"
						+ "</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}else{ 
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
						/*"<tr><td style=\"border-bottom:solid 1px #000;border-top:solid 1px #000\"></td>" +
						"<td align=\"right\" class=\"padRight20\" style=\"border-bottom:solid 1px #000;border-top:solid 1px #000\"><strong>Total</strong></td>" +
						"<td align=\"right\" class=\"padRight20\" style=\"border-bottom:solid 1px #000;border-top:solid 1px #000\"><strong>"+uF.formatIntoTwoDecimal(dblNetPayTotal)+"</strong></td>" +*/
						
						"<td width=\"10%\"><font size=\"1\">&nbsp;</font></td>"
						+ "<td width=\"70%\" align=\"right\"><font size=\"1\"><strong>Total</strong></font></td>"
						+ "<td align=\"right\" width=\"20%\"><font size=\"1\"><strong>"+uF.formatIntoTwoDecimal(dblNetPayTotal)+"</strong></font></td>" +
						"</tr></table>";
		
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition",
					"attachment; filename=PaymentHeldReports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void generateTDSReport(UtilityFunctions uF) {

		try {

			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			String strYear = (String) request.getAttribute("strYear");
			String strMonth = (String) request.getAttribute("strMonth");

			Map hmEmpPTax = (Map) request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map) request.getAttribute("hmEmpName");

			Map hmEmpLevelMap = (Map)request.getAttribute("hmEmpLevelMap");
			Map hmLevelMap1 = (Map)request.getAttribute("hmLevelMap1");
			Map hmEmpDept = (Map)request.getAttribute("hmEmpDept");
			Map hmDeptMap = (Map)request.getAttribute("hmDeptMap");
			
			Map<String, String> hmEmpOrgName =(Map<String, String>)request.getAttribute("hmEmpOrgName");
			if(hmEmpOrgName == null) hmEmpOrgName = new HashMap<String, String>();
			
			
			if (strFinancialYearStart != null && strFinancialYearEnd != null) {
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart,IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd,IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Salary held statement for the month of "
					+ uF.getDateFormat(strMonth, "MM", "MMMM")+ " "+ uF.getDateFormat(strYear, "yyyy", "yyyy")+ "</b></font></td></tr>" + "</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);

			document.add(new Paragraph(" "));

			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td align=\"center\"><font size=\"1\"><b>____________________________________________________________________________________________________________________________________________________________________________</b></font></td>"
					+ "</tr></table>";
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase4.add(supList4.get(0));
			document.add(phrase4);

			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td width=\"10%\"><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>"
					+ "<td width=\"10%\"><font size=\"1\"><b>&nbsp;Employee Code&nbsp;&nbsp;</b></font></td>"
					+ "<td width=\"20%\"><font size=\"1\"><b>&nbsp;Employee Name&nbsp;&nbsp;</b></font></td>"
					+ "<td width=\"10%\"><font size=\"1\"><b>&nbsp;Organization&nbsp;&nbsp;</b></font></td>"
					+ "<td width=\"10%\"><font size=\"1\"><b>&nbsp;Department&nbsp;&nbsp;</b></font></td>"
					+ "<td width=\"10%\"><font size=\"1\"><b>&nbsp;Level&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"right\" width=\"30%\"><font size=\"1\"><b>&nbsp;Net Pay&nbsp;&nbsp;</b></font></td></tr>"
					+ "</table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase5.add(supList4.get(0));
			document.add(phrase5);

			Set set = hmEmpPTax.keySet();
			Iterator it = set.iterator();
			int count = 0;
			while (it.hasNext()) {
				String strEmpId = (String) it.next();

				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td><font size=\"1\"><b>&nbsp;"+ (++count)+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEmpCode.get(strEmpId), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEmpName.get(strEmpId), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td><font size=\"1\"><b>&nbsp;"+ uF.showData(hmEmpOrgName.get(strEmpId), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td><font size=\"1\"><b>&nbsp;"+ uF.showData((String)hmDeptMap.get((String)hmEmpDept.get(strEmpId)), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td><font size=\"1\"><b>&nbsp;"+ uF.showData((String)hmLevelMap1.get((String)hmEmpLevelMap.get(strEmpId)), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEmpPTax.get(strEmpId), "0")+ "&nbsp;&nbsp;</b></font></td>" +
						"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);

				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>"
						+ "</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			if (count == 0) {
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td align=\"center\"><font size=\"1\"><b>&nbsp;No Employees found</b></font></td>"
						+ "</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
						+ "<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>"
						+ "</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=PaymentHeldReports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void viewPaymentHeldReport(UtilityFunctions uF) {

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
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap1 = CF.getLevelMap(con);
			Map<String, String> hmEmpDept = CF.getEmpDepartmentMap(con);
			Map<String, String> hmDeptMap = CF.getDeptMap(con);
						
			Map<String, String> hmOrg =  CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select earning_deduction, pg.emp_id, month, year, amount,eod.org_id from employee_personal_details epd, employee_official_details eod, payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id");
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
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and pg.service_id="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false order by eod.emp_id,eod.org_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			String strMonth=null;
			String strYear=null;
			rs = pst.executeQuery();
			Map hmEmpPTax = new HashMap();
			String strEmpIdOld=null;
			String strEmpIdNew=null;
			Map<String, String> hmEmpOrgName = new HashMap<String, String>();
			while(rs.next()){
				
				String strEarningDeduction = rs.getString("earning_deduction");
				strMonth = rs.getString("month");
				strYear = rs.getString("year");

				double dblAmount = uF.parseToDouble((String)hmEmpPTax.get(rs.getString("emp_id")));
				if(strEarningDeduction!=null && strEarningDeduction.equalsIgnoreCase("E")){
					dblAmount += rs.getDouble("amount");
				}else{
					dblAmount -= rs.getDouble("amount");
				}

				hmEmpPTax.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(dblAmount));
				hmEmpOrgName.put(rs.getString("emp_id"), uF.showData(hmOrg.get(rs.getString("org_id")), ""));
			}
			rs.close();
			pst.close();
			
			
			
			request.setAttribute("hmEmpOrgName", hmEmpOrgName);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpLevelMap", hmEmpLevelMap);
			request.setAttribute("hmLevelMap1", hmLevelMap1);
			request.setAttribute("hmEmpDept", hmEmpDept);
			request.setAttribute("hmDeptMap", hmDeptMap);
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

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public boolean isEmpUserType() {
		return isEmpUserType;
	}

	public void setEmpUserType(boolean isEmpUserType) {
		this.isEmpUserType = isEmpUserType;
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getParamSelection() {
		return paramSelection;
	}

	public void setParamSelection(String paramSelection) {
		this.paramSelection = paramSelection;
	}

}
