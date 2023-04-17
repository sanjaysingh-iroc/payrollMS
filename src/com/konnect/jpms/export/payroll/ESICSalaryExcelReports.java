package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ESICSalaryExcelReports implements ServletRequestAware,ServletResponseAware, IStatements {

	
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
	UtilityFunctions uF = new UtilityFunctions();
	public void execute(){
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		getESICSalaryReport(uF);
		
		return;
	}
	
	private void getESICSalaryReport(UtilityFunctions uF) {
		/*if(uF.parseToInt(getF_org())>0){
			viewESICSalaryReport();
			generateESICSalaryReport();
		}else{
			viewESICSalaryReportByOrg();
			generateESICSalaryReportBy();
		}*/
		
		if(uF.parseToInt(getF_org())>-1){ 
			if(uF.parseToInt(getF_strWLocation())>-1){				
				if(uF.parseToInt(getF_department())>-1){
					
					/*if(uF.parseToInt(getF_level())>-1){
						viewESICSalaryReport(uF);
						generateESICSalaryReport();
					}else{
						viewESICSalaryReportByLevel(uF);
						generateESICSalaryReportBy();
					}*/
					if(uF.parseToInt(getF_service())>-1){
						viewESICSalaryReport(uF);
						generateESICSalaryReport();
					}else{
						viewESICSalaryReportByService(uF);
						generateESICSalaryReportBy();
					}
					
				}else{
					viewESICSalaryReportByDepartment(uF);
					generateESICSalaryReportBy();
				}
			}else{
				viewESICSalaryReportByLocation(uF);
				generateESICSalaryReportBy();
			}			
			
		}else{
			if(uF.parseToInt(getF_org())==-1){
				viewESICSalaryReportByOrg(uF);
				generateESICSalaryReportBy();
			}
		}
	}
	private void viewESICSalaryReportByService(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		
		try {
			
			
			String strMonthYear = null;
			
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
			
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
			}
			
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			
			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");	
			
			String strYear=uF.getDateFormat(strDateStart, DATE_FORMAT, "yyyy");
			
			Map hmEarningSalaryMap = new HashMap();
			List alEmployees = new ArrayList();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmESIDetailsMap = new HashMap();
			CF.getESIDetailsMap(con,uF,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd,uF.parseToInt(getF_level()));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(eesi_contribution) as eesi_contribution,sum(ersi_contribution) as ersi_contribution, a.service_id " +
					"from (select pg.emp_id,pg.service_id from payroll_generation pg,employee_official_details eod " +
					"where month=? and year=? and is_paid=true and financial_year_from_date=? and financial_year_to_date=? " +
					"and pg.emp_id=eod.emp_id and salary_head_id=? and amount>0 group by pg.service_id,pg.emp_id) a," +
					"emp_esi_details eed,employee_official_details eod  where  _month=? and financial_year_start=? and financial_year_end=? " +
					"and a.emp_id=eed.emp_id and eod.emp_id=eed.emp_id and a.emp_id=eod.emp_id ");

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
			sbQuery.append(" group by a.service_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_ESI);
			pst.setInt(6, uF.parseToInt(getStrMonth()));
			pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst); 
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			while(rs.next()){
				double eesi_contribution = uF.parseToDouble(rs.getString("eesi_contribution"));
				dblEEContributionTotal += eesi_contribution;
				hmEarningSalaryMap.put(rs.getString("service_id")+"_EE", uF.formatIntoTwoDecimal(eesi_contribution));
				
				double ersi_contribution = uF.parseToDouble(rs.getString("ersi_contribution"));
				dblERContributionTotal += ersi_contribution;
				hmEarningSalaryMap.put(rs.getString("service_id")+"_ER", uF.formatIntoTwoDecimal(ersi_contribution));
			}
			rs.close();
			pst.close();
			
			
			String salaryHeadId=(String)hmESIDetailsMap.get("SALARY_HEAD_ID");
			salaryHeadId=salaryHeadId!=null && !salaryHeadId.equals("") ?salaryHeadId.substring(0,salaryHeadId.length()-1) : "";
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount,pg.service_id from payroll_generation pg,employee_official_details eod " +
					" where month=? and year=? and is_paid=true  and financial_year_from_date=? " +
					" and financial_year_to_date=? and pg.emp_id=eod.emp_id and salary_head_id in ("+salaryHeadId+") and amount>0  ");

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
			sbQuery.append(" and pg.emp_id in( select emp_id from payroll_generation where salary_head_id in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and amount>0) group by pg.service_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblAmountTotal = 0;
			while(rs.next()){
				double dblAmount = uF.parseToDouble(rs.getString("amount"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("service_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("service_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
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
	private void viewESICSalaryReportByLevel(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {
			
			
			String strMonthYear = null;
			
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
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
			}
			
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			
			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");	
			
			String strYear=uF.getDateFormat(strDateStart, DATE_FORMAT, "yyyy");
			
			
			
			Map hmEarningSalaryMap = new HashMap();
			List alEmployees = new ArrayList();
			

			Map hmESIDetailsMap = new HashMap();
			CF.getESIDetailsMap(con,uF,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd,uF.parseToInt(getF_level()));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount,level_id from (select sum(amount) as amount,eod.grade_id from payroll_generation pg," +
					"employee_official_details eod where  month=? and year=? and is_paid=true and financial_year_from_date=? " +
					" and financial_year_to_date=? " +
					" and pg.emp_id=eod.emp_id  and salary_head_id=? and amount>0  and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");

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
			sbQuery.append("  group by eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
					" where a.grade_id=b.grade_id group by level_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYER_ESI);
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblERContributionTotal = 0;
			
			while(rs.next()){
				double dblAmount = uF.parseToDouble(rs.getString("amount"));
				dblERContributionTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("level_id")+"_ER", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount,level_id from (select sum(amount) as amount,eod.grade_id from payroll_generation pg," +
					"employee_official_details eod where  month=? and year=? and is_paid=true and financial_year_from_date=? " +
					" and financial_year_to_date=? " +
					" and pg.emp_id=eod.emp_id  and salary_head_id=? and amount>0  and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");

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
			sbQuery.append(" group by eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
					" where a.grade_id=b.grade_id group by level_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_ESI);
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			while(rs.next()){
				double dblAmount = uF.parseToDouble(rs.getString("amount"));
				dblEEContributionTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("level_id")+"_EE", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
			}
			rs.close();
			pst.close();
			
			String salaryHeadId=(String)hmESIDetailsMap.get("SALARY_HEAD_ID");
			salaryHeadId=salaryHeadId!=null && !salaryHeadId.equals("") ?salaryHeadId.substring(0,salaryHeadId.length()-1) : "";
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount,level_id from (select sum(amount) as amount,eod.grade_id from payroll_generation pg," +
					"employee_official_details eod where  month=? and year=? and is_paid=true and financial_year_from_date=? " +
					" and financial_year_to_date=? " +
					" and pg.emp_id=eod.emp_id  and salary_head_id in ("+salaryHeadId+") and amount>0   and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");

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
			sbQuery.append("  and pg.emp_id in( select emp_id from payroll_generation where salary_head_id in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and amount>0) group by eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
					" where a.grade_id=b.grade_id group by level_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblAmountTotal = 0;
			while(rs.next()){
				double dblAmount = uF.parseToDouble(rs.getString("amount"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("level_id")+"_GE", uF.formatIntoTwoDecimalWithOutComma(dblAmount));
				alEmployees.add(rs.getString("level_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimalWithOutComma(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimalWithOutComma(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimalWithOutComma(dblERContributionTotal));
			
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
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


	private void viewESICSalaryReportByDepartment(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		
		try {
			
			
			String strMonthYear = null;
			
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
			
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
			}
			
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			
			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");	
			
			String strYear=uF.getDateFormat(strDateStart, DATE_FORMAT, "yyyy");
			
			Map hmEarningSalaryMap = new HashMap();
			List alEmployees = new ArrayList();
			

			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmESIDetailsMap = new HashMap();
			CF.getESIDetailsMap(con,uF,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd,uF.parseToInt(getF_level()));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(eesi_contribution) as eesi_contribution,sum(ersi_contribution) as ersi_contribution,eod.depart_id " +
					"from emp_esi_details eed,employee_official_details eod where _month=? and financial_year_start=? " +
					"and financial_year_end=? and eed.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			sbQuery.append("and eed.emp_id in(select emp_id from payroll_generation where  month=? and year=? and is_paid=true " +
					"and financial_year_from_date=? and financial_year_to_date=? and amount>0 ");
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(")");
			sbQuery.append(" group by eod.depart_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(strYear));
			pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			while(rs.next()){
				double eesi_contribution = uF.parseToDouble(rs.getString("eesi_contribution"));
				dblEEContributionTotal += eesi_contribution;
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EE", uF.formatIntoTwoDecimal(eesi_contribution));
				
				double ersi_contribution = uF.parseToDouble(rs.getString("ersi_contribution"));
				dblERContributionTotal += ersi_contribution;
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_ER", uF.formatIntoTwoDecimal(ersi_contribution));
			}
			rs.close();
			pst.close();
			
			String salaryHeadId=(String)hmESIDetailsMap.get("SALARY_HEAD_ID");
			salaryHeadId=salaryHeadId!=null && !salaryHeadId.equals("") ?salaryHeadId.substring(0,salaryHeadId.length()-1) : "";
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount,eod.depart_id from payroll_generation pg,employee_official_details eod " +
					" where month=? and year=? and is_paid=true  and financial_year_from_date=? " +
					" and financial_year_to_date=? and pg.emp_id=eod.emp_id and salary_head_id in ("+salaryHeadId+") and amount>0  ");

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
			sbQuery.append(" and pg.emp_id in( select emp_id from payroll_generation where salary_head_id in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and amount>0) group by eod.depart_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblAmountTotal = 0;
			while(rs.next()){
				double dblAmount = uF.parseToDouble(rs.getString("amount"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("depart_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void viewESICSalaryReportByLocation(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		
		try {
			
			
			String strMonthYear = null;
			
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
			
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
			}
			
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			
			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");
			
			String strYear=uF.getDateFormat(strDateStart, DATE_FORMAT, "yyyy");	
			
			
			
			Map hmEarningSalaryMap = new HashMap();
			List alEmployees = new ArrayList();
			
			
			con = db.makeConnection(con);
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmESIDetailsMap = new HashMap();
			CF.getESIDetailsMap(con,uF,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd,uF.parseToInt(getF_level()));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(eesi_contribution) as eesi_contribution,sum(ersi_contribution) as ersi_contribution,eod.wlocation_id " +
					"from emp_esi_details eed,employee_official_details eod where _month=? and financial_year_start=? " +
					"and financial_year_end=? and eed.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			sbQuery.append("and eed.emp_id in(select emp_id from payroll_generation where  month=? and year=? and is_paid=true " +
					"and financial_year_from_date=? and financial_year_to_date=? and amount>0 ");
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(")");	
			sbQuery.append(" group by eod.wlocation_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(strYear));
			pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			while(rs.next()){
				double eesi_contribution = uF.parseToDouble(rs.getString("eesi_contribution"));
				dblEEContributionTotal += eesi_contribution;
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EE", uF.formatIntoTwoDecimal(eesi_contribution));
				
				double ersi_contribution = uF.parseToDouble(rs.getString("ersi_contribution"));
				dblERContributionTotal += ersi_contribution;
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_ER", uF.formatIntoTwoDecimal(ersi_contribution));
			}
			rs.close();
			pst.close();
			
			String salaryHeadId=(String)hmESIDetailsMap.get("SALARY_HEAD_ID");
			salaryHeadId=salaryHeadId!=null && !salaryHeadId.equals("") ?salaryHeadId.substring(0,salaryHeadId.length()-1) : "";
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount,eod.wlocation_id from payroll_generation pg,employee_official_details eod " +
					" where month=? and year=? and is_paid=true  and financial_year_from_date=? " +
					" and financial_year_to_date=? and pg.emp_id=eod.emp_id  and salary_head_id in ("+salaryHeadId+") and amount>0 ");

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
			sbQuery.append(" and pg.emp_id in( select emp_id from payroll_generation where salary_head_id in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and amount>0) group by eod.wlocation_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			double dblAmountTotal = 0;
			while(rs.next()){
				double dblAmount = uF.parseToDouble(rs.getString("amount"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	private void viewESICSalaryReportByOrg(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		
		try {
			
			
			String strMonthYear = null;
			
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
			
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
			}
			
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			
			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");
			
			String strYear=uF.getDateFormat(strDateStart, DATE_FORMAT, "yyyy");	
			
			
			
			Map hmEarningSalaryMap = new HashMap();
			List alEmployees = new ArrayList();
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmESIDetailsMap = new HashMap();
			CF.getESIDetailsMap(con,uF,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd,uF.parseToInt(getF_level()));
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(eesi_contribution) as eesi_contribution,sum(ersi_contribution) as ersi_contribution,eod.org_id " +
					"from emp_esi_details eed,employee_official_details eod where _month=? and financial_year_start=? " +
					"and financial_year_end=? and eed.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			sbQuery.append("and eed.emp_id in(select emp_id from payroll_generation where  month=? and year=? and is_paid=true " +
					"and financial_year_from_date=? and financial_year_to_date=? and amount>0 ");
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(")");		
			sbQuery.append(" group by eod.org_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(strYear));
			pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			while(rs.next()){		
				double eesi_contribution = uF.parseToDouble(rs.getString("eesi_contribution"));
				dblEEContributionTotal += eesi_contribution;				
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EE", uF.formatIntoTwoDecimal(eesi_contribution));
				
				double ersi_contribution = uF.parseToDouble(rs.getString("ersi_contribution"));
				dblERContributionTotal += ersi_contribution;				
				hmEarningSalaryMap.put(rs.getString("org_id")+"_ER", uF.formatIntoTwoDecimal(ersi_contribution));
			}
			rs.close();
			pst.close();
			
			String salaryHeadId=(String)hmESIDetailsMap.get("SALARY_HEAD_ID");
			salaryHeadId=salaryHeadId!=null && !salaryHeadId.equals("") ?salaryHeadId.substring(0,salaryHeadId.length()-1) : "";
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount,eod.org_id from payroll_generation pg,employee_official_details eod " +
					" where month=? and year=? and is_paid=true  and financial_year_from_date=? " +
					" and financial_year_to_date=?  and pg.emp_id=eod.emp_id and salary_head_id in ("+salaryHeadId+") and amount>0  ");

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
			sbQuery.append(" and pg.emp_id in( select emp_id from payroll_generation where salary_head_id in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and amount>0) group by eod.org_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			double dblAmountTotal = 0;
			
			while(rs.next()){
				
				double dblAmount = uF.parseToDouble(rs.getString("amount"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("org_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
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
	private void generateESICSalaryReportBy() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strMonthYear = (String)request.getAttribute("strMonthYear");

			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			List alEmployees = (List)request.getAttribute("alEmployees");
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
			header.add(new DataStyle("ESIC Salary Report for the month of "+strMonthYear,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle(title,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("ESIC Wages",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Contr.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employer Contr.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		

		
			int count=0;
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			double dblWagesTotal = 0;
			double dblEmployeeContrTotal = 0;
			double dblEmployerContrAmountTotal = 0;
			
			for(; count<alEmployees.size(); count++){
				String strEmpId = (String)alEmployees.get(count);
				
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
			
				String wages=uF.showData((String)hmEarningSalaryMap.get(strEmpId+"_GE"),"0");
				String employeeContr=uF.showData((String)hmEarningSalaryMap.get(strEmpId+"_EE"),"0");
				String employerContr=uF.showData((String)hmEarningSalaryMap.get(strEmpId+"_ER"),"0");
				
				dblWagesTotal+= uF.parseToDouble((String)hmEarningSalaryMap.get(strEmpId+"_GE"));
				dblEmployeeContrTotal += uF.parseToDouble((String)hmEarningSalaryMap.get(strEmpId+"_EE"));
				dblEmployerContrAmountTotal += uF.parseToDouble((String)hmEarningSalaryMap.get(strEmpId+"_ER"));
				
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(""+(count+1),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(wages,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(employeeContr,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(employerContr,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportData.add(innerList);
				
			}
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("Total",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblWagesTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblEmployeeContrTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblEmployerContrAmountTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String)hmEarningSalaryMap.get("Total_ER"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String)hmEarningSalaryMap.get("Total_EE"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String)hmEarningSalaryMap.get("Total_ER"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
		
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=ESICSalaryExcelReports.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}finally{
		db.closeConnection(con);
	}
	
}
	public void generateESICSalaryReport(){
		
		try {


			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strMonthYear = (String)request.getAttribute("strMonthYear");

			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			List alEmployees = (List)request.getAttribute("alEmployees");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}
			Map<String,String> hmEmpPaidDays = (Map<String, String>)request.getAttribute("hmEmpPaidDays");
			if(hmEmpPaidDays==null) hmEmpPaidDays = new HashMap<String, String>();

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("ESIC Salary Report for the month of "+strMonthYear,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Paid Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("ESIC Acc. No",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("ESIC Wages",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Contr.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employer Contr.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	  	
	
		
		int count=0;
		List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>(); 
		double dblWagesTotal = 0;
		double dblEmployeeContrTotal = 0;
		double dblEmployerContrAmountTotal = 0;
		for(; count<alEmployees.size(); count++){
			String strEmpId = (String)alEmployees.get(count);
		
			String empName=uF.showData((String)hmEmpName.get(strEmpId), "");
			String paidDays=uF.showData(hmEmpPaidDays.get(strEmpId),"0");
			String esicAccno="";
			String wages=uF.showData((String)hmEarningSalaryMap.get(strEmpId+"_GE"),"0");
			String employeeContr=uF.showData((String)hmEarningSalaryMap.get(strEmpId+"_EE"),"0");
			String employerContr=uF.showData((String)hmEarningSalaryMap.get(strEmpId+"_ER"),"0");
			
//			dblWagesTotal+= uF.parseToDouble((String)hmEarningSalaryMap.get(strEmpId+"_GE"));
//			dblEmployeeContrTotal += uF.parseToDouble((String)hmEarningSalaryMap.get(strEmpId+"_EE"));
//			dblEmployerContrAmountTotal += uF.parseToDouble((String)hmEarningSalaryMap.get(strEmpId+"_ER"));
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();
			innerList.add(new DataStyle(""+(count+1),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(empName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(paidDays,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(esicAccno,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(wages,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(employeeContr,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(employerContr,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			reportData.add(innerList);
				
		}
		
		List<DataStyle> innerList=new ArrayList<DataStyle>();
		innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		innerList.add(new DataStyle("Total",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//		innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblWagesTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//		innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblEmployeeContrTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//		innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblEmployerContrAmountTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		innerList.add(new DataStyle(uF.showData((String)hmEarningSalaryMap.get("Total_ER"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		innerList.add(new DataStyle(uF.showData((String)hmEarningSalaryMap.get("Total_EE"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		innerList.add(new DataStyle(uF.showData((String)hmEarningSalaryMap.get("Total_ER"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		
		reportData.add(innerList);
		
		ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
		sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		workbook.write(buffer);
		response.setContentType("application/vnd.ms-excel:UTF-8");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=ESICSalaryExcelReports.xls");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	public void viewESICSalaryReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		
		try {
			
			
			String strMonthYear = null;
			
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
			
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"));
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
			}
			
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			
			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");	
			
			String strYear=uF.getDateFormat(strDateStart, DATE_FORMAT, "yyyy");
			
			
			
			Map hmEarningSalaryMap = new HashMap();
			List alEmployees = new ArrayList();
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmESIDetailsMap = new HashMap();
			CF.getESIDetailsMap(con,uF, hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd,uF.parseToInt(getF_level()));
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(eesi_contribution) as eesi_contribution,sum(ersi_contribution) as ersi_contribution, emp_id " +
					"from emp_esi_details where  _month=? and financial_year_start=? and financial_year_end=?");

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
			sbQuery.append("and emp_id in(select emp_id from payroll_generation where  month=? and year=? and is_paid=true " +
					"and financial_year_from_date=? and financial_year_to_date=?");
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id ="+uF.parseToInt(getF_service()));
			}
			sbQuery.append(")");
			sbQuery.append(" group by emp_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(strYear));
			pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblERContributionTotal = 0;
			double dblEEContributionTotal = 0;
			while(rs.next()){				
				double eesi_contribution = uF.parseToDouble(rs.getString("eesi_contribution"));
				dblEEContributionTotal += eesi_contribution;				
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EE", uF.formatIntoTwoDecimal(eesi_contribution));
				
				double ersi_contribution = uF.parseToDouble(rs.getString("ersi_contribution"));
				dblERContributionTotal += ersi_contribution;
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_ER", uF.formatIntoTwoDecimal(ersi_contribution));
			}
			rs.close();
			pst.close();
			
			String salaryHeadId=(String)hmESIDetailsMap.get("SALARY_HEAD_ID");
			salaryHeadId=salaryHeadId!=null && !salaryHeadId.equals("") ?salaryHeadId.substring(0,salaryHeadId.length()-1) : "";
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount) as amount, emp_id,paid_days from payroll_generation where  month=? and year=?  and is_paid=true  and financial_year_from_date=? " +
					" and financial_year_to_date=? and salary_head_id in ("+salaryHeadId+") and amount>0  ");

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
			sbQuery.append(" and emp_id in( select emp_id from payroll_generation where salary_head_id in ("+EMPLOYER_ESI+","+EMPLOYEE_ESI+") and amount>0) group by emp_id,paid_days");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);  
			rs = pst.executeQuery();
			double dblAmountTotal = 0;
			Map<String,String> hmEmpPaidDays = new HashMap<String, String>();
			while(rs.next()){
				
				double dblAmount = uF.parseToDouble(rs.getString("amount"));

				dblAmountTotal += dblAmount;
				
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("emp_id"));
				
				double dblPaidDays = uF.parseToDouble(rs.getString("paid_days"));
				hmEmpPaidDays.put(rs.getString("emp_id"),""+dblPaidDays);
				
			}
			rs.close();
			pst.close();
			
			
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmEmpPaidDays", hmEmpPaidDays);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
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
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response=response;
	}
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
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
