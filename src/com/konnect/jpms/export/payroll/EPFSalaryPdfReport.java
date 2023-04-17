package com.konnect.jpms.export.payroll;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
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

public class EPFSalaryPdfReport implements ServletRequestAware,ServletResponseAware, IStatements {

	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	String financialYear;
	String strMonth;
	UtilityFunctions uF = new UtilityFunctions();
	
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_org;
	String f_service;
	List<FillServices> serviceList;
	
	public void execute()
	{
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		getEPFSalaryReport();
		
		return;
		
		
	}
	private void getEPFSalaryReport() {
		if(getF_org()==null){
			setF_org("-1");
		}				
		if(uF.parseToInt(getF_org())>-1){
			if(uF.parseToInt(getF_strWLocation())>-1){				
				if(uF.parseToInt(getF_department())>-1){
					
					/*if(uF.parseToInt(getF_level())>-1){
						viewEPFSalaryReport();
						generateEPFSalaryReport();
					}else{
						viewEPFSalaryReportByLevel(uF);
						generateEPFSalaryReportBy();
					}	*/	
					
					if(uF.parseToInt(getF_service())>-1){
						viewEPFSalaryReport();
						generateEPFSalaryReport();
					}else{						
						viewEPFSalaryReportByServices(uF);
						generateEPFSalaryReportBy();
					}
					
				}else{
					viewEPFSalaryReportByDepartment(uF);
					generateEPFSalaryReportBy();
				}
			}else{
				viewEPFSalaryReportByLocation(uF);
				generateEPFSalaryReportBy();
			}			
			
		}else{
			if(uF.parseToInt(getF_org())==-1){
				viewEPFSalaryReportByOrg();
				generateEPFSalaryReportBy();
			}
		}
		
		
	}
	private void viewEPFSalaryReportByServices(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		
		try {
			
			Map hmEmpName = new HashMap();
			Map hmEmpCode = new HashMap();
			Map hmEmpPFNumber = new HashMap();
			
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
			
			
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			Map hmEPFDetailsMap = new HashMap();
//			CF.getEPFDetailsMap(con,hmEPFDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()){
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,eod.service_id,sum(evpf_contribution) as evpf_contribution " +
					"from emp_epf_details eed,employee_official_details eod where eed.emp_id=eod.emp_id and financial_year_start=? " +
					"and financial_year_end=? and _month=? ");

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
			
			sbQuery.append(" group by eod.service_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			double dblEEPFContributionTotal = 0;
			double dblEEVPFContributionTotal = 0;
			double dblERPFContributionTotal = 0;
			double dblERPSContributionTotal = 0;
			double dblERDLIContributionTotal = 0;
			double dblEDLIMaxLimitTotal = 0;
			double dblEPFAdminChargesTotal = 0;
			double dblEDLIAdminChargesTotal = 0;
			double dbl_epf_max_limit_Total = 0;
			double dbl_eps_max_limit_Total = 0;
			
			while(rs.next()){
				
				dblEEPFContributionTotal += rs.getDouble("eepf_contribution");
				dblEEVPFContributionTotal += rs.getDouble("evpf_contribution");
				dblERPFContributionTotal += rs.getDouble("erpf_contribution");
				dblERPSContributionTotal += rs.getDouble("erps_contribution");
				dblERDLIContributionTotal += rs.getDouble("erdli_contribution");
				dblEDLIMaxLimitTotal += rs.getDouble("edli_max_limit");
				dblEPFAdminChargesTotal += rs.getDouble("pf_admin_charges");
				dblEDLIAdminChargesTotal += rs.getDouble("edli_admin_charges");
				dbl_epf_max_limit_Total += rs.getDouble("epf_max_limit");
				dbl_eps_max_limit_Total += rs.getDouble("eps_max_limit");
				
				String service_id=rs.getString("service_id").substring(0,rs.getString("service_id").length()-1);
				
				hmEarningSalaryMap.put(service_id+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
				hmEarningSalaryMap.put(service_id+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("eps_max_limit")));
				hmEarningSalaryMap.put(service_id+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
				hmEarningSalaryMap.put(service_id+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("evpf_contribution")));
				hmEarningSalaryMap.put(service_id+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erpf_contribution")));
				hmEarningSalaryMap.put(service_id+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
				hmEarningSalaryMap.put(service_id+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erdli_contribution")));
				hmEarningSalaryMap.put(service_id+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("edli_max_limit")));
				hmEarningSalaryMap.put(service_id+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("pf_admin_charges")));
				hmEarningSalaryMap.put(service_id+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("edli_admin_charges")));
				
				alEmployees.add(service_id);
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_epf_max_limit_Total));
			hmEarningSalaryMap.put("Total_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_eps_max_limit_Total));
			hmEarningSalaryMap.put("Total_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEPFContributionTotal));
			hmEarningSalaryMap.put("Total_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEVPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPSContributionTotal));
			hmEarningSalaryMap.put("Total_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERDLIContributionTotal));
			hmEarningSalaryMap.put("Total_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(dblEDLIMaxLimitTotal));
			hmEarningSalaryMap.put("Total_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEPFAdminChargesTotal));
			hmEarningSalaryMap.put("Total_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEDLIAdminChargesTotal));
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
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
	private void viewEPFSalaryReportByLevel(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {
			
			Map hmEmpName = new HashMap();
			Map hmEmpCode = new HashMap();
			Map hmEmpPFNumber = new HashMap();
			
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
			
			
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			
			con = db.makeConnection(con);
//			Map hmEPFDetailsMap = new HashMap();
//			CF.getEPFDetailsMap(con,hmEPFDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()){
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,level_id from " +
					" (select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,eod.grade_id,sum(evpf_contribution) as evpf_contribution " +
					"from emp_epf_details eed,employee_official_details eod where eed.emp_id=eod.emp_id and financial_year_start=? " +
					"and financial_year_end=? and _month=? " +
					" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd " +
					" where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");

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
				sbQuery.append(" and eod.service_id like '%,"+uF.parseToInt(getF_service())+",%'");
			}
			
			
			sbQuery.append(" group by eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
					" where a.grade_id=b.grade_id group by level_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			double dblEEPFContributionTotal = 0;
			double dblEEVPFContributionTotal = 0;
			double dblERPFContributionTotal = 0;
			double dblERPSContributionTotal = 0;
			double dblERDLIContributionTotal = 0;
			double dblEDLIMaxLimitTotal = 0;
			double dblEPFAdminChargesTotal = 0;
			double dblEDLIAdminChargesTotal = 0;
			double dbl_epf_max_limit_Total = 0;
			double dbl_eps_max_limit_Total = 0;
			
			while(rs.next()){
				
				dblEEPFContributionTotal += rs.getDouble("eepf_contribution");
				dblEEVPFContributionTotal += rs.getDouble("evpf_contribution");
				dblERPFContributionTotal += rs.getDouble("erpf_contribution");
				dblERPSContributionTotal += rs.getDouble("erps_contribution");
				dblERDLIContributionTotal += rs.getDouble("erdli_contribution");
				dblEDLIMaxLimitTotal += rs.getDouble("edli_max_limit");
				dblEPFAdminChargesTotal += rs.getDouble("pf_admin_charges");
				dblEDLIAdminChargesTotal += rs.getDouble("edli_admin_charges");
				dbl_epf_max_limit_Total += rs.getDouble("epf_max_limit");
				dbl_eps_max_limit_Total += rs.getDouble("eps_max_limit");
				
				hmEarningSalaryMap.put(rs.getString("level_id")+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("eps_max_limit")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("evpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erdli_contribution")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("edli_max_limit")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("pf_admin_charges")));
				hmEarningSalaryMap.put(rs.getString("level_id")+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("edli_admin_charges")));
				
				alEmployees.add(rs.getString("level_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_epf_max_limit_Total));
			hmEarningSalaryMap.put("Total_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_eps_max_limit_Total));
			hmEarningSalaryMap.put("Total_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEPFContributionTotal));
			hmEarningSalaryMap.put("Total_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEVPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPSContributionTotal));
			hmEarningSalaryMap.put("Total_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERDLIContributionTotal));
			hmEarningSalaryMap.put("Total_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(dblEDLIMaxLimitTotal));
			hmEarningSalaryMap.put("Total_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEPFAdminChargesTotal));
			hmEarningSalaryMap.put("Total_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEDLIAdminChargesTotal));
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
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
	private void viewEPFSalaryReportByDepartment(UtilityFunctions uF2) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {
			
			Map hmEmpName = new HashMap();
			Map hmEmpCode = new HashMap();
			Map hmEmpPFNumber = new HashMap();
			
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
			
			
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			

			con = db.makeConnection(con);

//			Map hmEPFDetailsMap = new HashMap();
//			CF.getEPFDetailsMap(con,hmEPFDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()){
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,eod.depart_id,sum(evpf_contribution) as evpf_contribution " +
					"from emp_epf_details eed,employee_official_details eod where eed.emp_id=eod.emp_id and financial_year_start=? " +
					"and financial_year_end=? and _month=? ");

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
				sbQuery.append(" and eod.service_id like '%,"+uF.parseToInt(getF_service())+",%'");
			}
			sbQuery.append(" group by eod.depart_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			
			double dblEEPFContributionTotal = 0;
			double dblEEVPFContributionTotal = 0;
			double dblERPFContributionTotal = 0;
			double dblERPSContributionTotal = 0;
			double dblERDLIContributionTotal = 0;
			double dblEDLIMaxLimitTotal = 0;
			double dblEPFAdminChargesTotal = 0;
			double dblEDLIAdminChargesTotal = 0;
			double dbl_epf_max_limit_Total = 0;
			double dbl_eps_max_limit_Total = 0;
			
			while(rs.next()){
				
				dblEEPFContributionTotal += rs.getDouble("eepf_contribution");
				dblEEVPFContributionTotal += rs.getDouble("evpf_contribution");
				dblERPFContributionTotal += rs.getDouble("erpf_contribution");
				dblERPSContributionTotal += rs.getDouble("erps_contribution");
				dblERDLIContributionTotal += rs.getDouble("erdli_contribution");
				dblEDLIMaxLimitTotal += rs.getDouble("edli_max_limit");
				dblEPFAdminChargesTotal += rs.getDouble("pf_admin_charges");
				dblEDLIAdminChargesTotal += rs.getDouble("edli_admin_charges");
				dbl_epf_max_limit_Total += rs.getDouble("epf_max_limit");
				dbl_eps_max_limit_Total += rs.getDouble("eps_max_limit");
				
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("eps_max_limit")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("evpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erdli_contribution")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("edli_max_limit")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("pf_admin_charges")));
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("edli_admin_charges")));
				
				alEmployees.add(rs.getString("depart_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_epf_max_limit_Total));
			hmEarningSalaryMap.put("Total_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_eps_max_limit_Total));
			hmEarningSalaryMap.put("Total_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEPFContributionTotal));
			hmEarningSalaryMap.put("Total_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEVPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPSContributionTotal));
			hmEarningSalaryMap.put("Total_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERDLIContributionTotal));
			hmEarningSalaryMap.put("Total_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(dblEDLIMaxLimitTotal));
			hmEarningSalaryMap.put("Total_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEPFAdminChargesTotal));
			hmEarningSalaryMap.put("Total_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEDLIAdminChargesTotal));
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
						
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	private void viewEPFSalaryReportByLocation(UtilityFunctions uF2) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			Map hmEmpName = new HashMap();
			Map hmEmpCode = new HashMap();
			Map hmEmpPFNumber = new HashMap();
			
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
			
			
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
//			Map hmEPFDetailsMap = new HashMap();
//			CF.getEPFDetailsMap(con,hmEPFDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()){
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,eod.wlocation_id,sum(evpf_contribution) as evpf_contribution " +
					"from emp_epf_details eed,employee_official_details eod where eed.emp_id=eod.emp_id and financial_year_start=? " +
					"and financial_year_end=? and _month=? ");

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
				sbQuery.append(" and eod.service_id like '%,"+uF.parseToInt(getF_service())+",%'");
			}
			
			sbQuery.append(" group by eod.wlocation_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			rs = pst.executeQuery();
			
			double dblEEPFContributionTotal = 0;
			double dblEEVPFContributionTotal = 0;
			double dblERPFContributionTotal = 0;
			double dblERPSContributionTotal = 0;
			double dblERDLIContributionTotal = 0;
			double dblEDLIMaxLimitTotal = 0;
			double dblEPFAdminChargesTotal = 0;
			double dblEDLIAdminChargesTotal = 0;
			double dbl_epf_max_limit_Total = 0;
			double dbl_eps_max_limit_Total = 0;
			
			while(rs.next()){
				
				dblEEPFContributionTotal += rs.getDouble("eepf_contribution");
				dblEEVPFContributionTotal += rs.getDouble("evpf_contribution");
				dblERPFContributionTotal += rs.getDouble("erpf_contribution");
				dblERPSContributionTotal += rs.getDouble("erps_contribution");
				dblERDLIContributionTotal += rs.getDouble("erdli_contribution");
				dblEDLIMaxLimitTotal += rs.getDouble("edli_max_limit");
				dblEPFAdminChargesTotal += rs.getDouble("pf_admin_charges");
				dblEDLIAdminChargesTotal += rs.getDouble("edli_admin_charges");
				dbl_epf_max_limit_Total += rs.getDouble("epf_max_limit");
				dbl_eps_max_limit_Total += rs.getDouble("eps_max_limit");
				
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("eps_max_limit")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("evpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erdli_contribution")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("edli_max_limit")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("pf_admin_charges")));
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("edli_admin_charges")));
				
				alEmployees.add(rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_epf_max_limit_Total));
			hmEarningSalaryMap.put("Total_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_eps_max_limit_Total));
			hmEarningSalaryMap.put("Total_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEPFContributionTotal));
			hmEarningSalaryMap.put("Total_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEVPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPSContributionTotal));
			hmEarningSalaryMap.put("Total_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERDLIContributionTotal));
			hmEarningSalaryMap.put("Total_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(dblEDLIMaxLimitTotal));
			hmEarningSalaryMap.put("Total_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEPFAdminChargesTotal));
			hmEarningSalaryMap.put("Total_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEDLIAdminChargesTotal));
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
						
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	private void generateEPFSalaryReportBy() {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {


			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strMonthYear = (String)request.getAttribute("strMonthYear");


			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpPFNumber = (Map)request.getAttribute("hmEmpPFNumber");
			
			Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			List alEmployees = (List)request.getAttribute("alEmployees");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}


			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con);
			Map<String, String> hmLevelMap =(Map<String, String>)request.getAttribute("hmLevelMap");
			Map<String, String> hmServicesMap =(Map<String, String>)request.getAttribute("hmServicesMap");
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();
					
			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>EPF Salary Report for the month of "+strMonthYear+"</b></font></td></tr>" +
					"</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			
			
			
			document.add(new Paragraph(" "));
			
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
				  	
			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
					"<td width=\"5%\"><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;"+title+"&nbsp;&nbsp;</b></font></td>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EPF Wages&nbsp;&nbsp;</b></font></td>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EPS Wages&nbsp;&nbsp;</b></font></td>" +
					
					/*"<td nowrap=\"nowrap\" align=\"center\" colspan=\"2\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>Employee</b></font></td>" +
					"</tr><tr><td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>PF Amount</b></font></td>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>VPF Amount</b></font></td>"+
					"</tr></table></td>" +*/
					"<td colspan=\"2\"  nowrap=\"nowrap\" align=\"center\">" +
					"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
					"<td><table><tr><td align=\"center\"  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>Employee</strong></font></td>" +
			"</tr></table></td></tr><tr><td><table><tr><td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>PF Amount</strong></font></td>" +
			"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>VPF Amount</strong></font></td></tr></table></td></tr></table></td>" +
					
					"<td colspan=\"2\" align=\"center\">" +
					"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
					"<td><table><tr><td align=\"center\" nowrap=\"nowrap\"><font size=\"1\"><b>Employer</b></font></td>" +
			"</tr></table></td></tr><tr><td><table><tr><td nowrap=\"nowrap\"><font size=\"1\"><b>PF Amount</b></font></td>" +
			"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>EPS Amount</b></font></td></tr></table></td></tr></table></td>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EDLI&nbsp;</b></font></td>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EDLI Max Limit&nbsp;&nbsp;</b></font></td>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;PF Admin Charges&nbsp;&nbsp;</b></font></td>" +
					"<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EDLI Admin Charges&nbsp;&nbsp;</b></font></td></tr>" +
			"</table>";
			
			 	  	  	
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			
	
			
			int count=0;
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
			
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
				"<td width=\"5%\"><font size=\"1\">&nbsp;"+(count+1)+"&nbsp;&nbsp;</font></td>" +
				"<td nowrap=\"nowrap\"><font size=\"1\">&nbsp;"+strName+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EPF_MAX_LIMIT")+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EPS_MAX_LIMIT")+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EEPF_CONTRIBUTION")+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EEVPF_CONTRIBUTION")+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_ERPF_CONTRIBUTION")+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_ERPS_CONTRIBUTION")+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_ERDLI_CONTRIBUTION")+"&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EDLI_MAX_LIMIT")+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EPF_ADMIN_CHARGES")+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EDLI_ADMIN_CHARGES")+"&nbsp;&nbsp;</font></td></tr>" +
				"</table>";
		
		 	  	  	
			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase2.add(supList2.get(0));
			document.add(phrase2);
				}
				
				if(count==0)
				{String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
					"<td align=\"center\"><font size=\"1\">No Employees found</font></td></tr></table>";
				List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
				Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase4.add(supList4.get(0));
				document.add(phrase4);
				}else{
				
			String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
			"<td width=\"5%\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>" +
			"<td><font size=\"1\"><b>&nbsp;&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")+"&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")+"&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")+"&nbsp;&nbsp;</b></font></td></tr>" +
			"</table>";
			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase3.add(supList3.get(0));
			document.add(phrase3);
			
		}
	
	
		
		
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=EPFSalaryReport.pdf");
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
	private void viewEPFSalaryReportByOrg() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			Map hmEmpName = new HashMap();
			Map hmEmpCode = new HashMap();
			Map hmEmpPFNumber = new HashMap();
			
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
			
			
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			

//			Map hmEPFDetailsMap = new HashMap();
//			CF.getEPFDetailsMap(con,hmEPFDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()){
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
			}
			rs.close();
			pst.close();
			
			/*pst = con.prepareStatement("select sum(amount) as amount, emp_id from payroll_generation where pay_date between ? and ? and earning_deduction = 'E' group by emp_id");
			pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));*/
			
			
			
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,org_id,sum(evpf_contribution) as evpf_contribution " +
					"from emp_epf_details eed,employee_official_details eod where eed.emp_id=eod.emp_id and financial_year_start=? " +
					"and financial_year_end=? and _month=? ");

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
				sbQuery.append(" and eod.service_id like '%,"+uF.parseToInt(getF_service())+",%'");
			}
			
			sbQuery.append(" group by eod.org_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			
			rs = pst.executeQuery();
			
			double dblEEPFContributionTotal = 0;
			double dblEEVPFContributionTotal = 0;
			double dblERPFContributionTotal = 0;
			double dblERPSContributionTotal = 0;
			double dblERDLIContributionTotal = 0;
			double dblEDLIMaxLimitTotal = 0;
			double dblEPFAdminChargesTotal = 0;
			double dblEDLIAdminChargesTotal = 0;
			double dbl_epf_max_limit_Total = 0;
			double dbl_eps_max_limit_Total = 0;
			
			while(rs.next()){
				
				dblEEPFContributionTotal += rs.getDouble("eepf_contribution");
				dblEEVPFContributionTotal += rs.getDouble("evpf_contribution");
				dblERPFContributionTotal += rs.getDouble("erpf_contribution");
				dblERPSContributionTotal += rs.getDouble("erps_contribution");
				dblERDLIContributionTotal += rs.getDouble("erdli_contribution");
				dblEDLIMaxLimitTotal += rs.getDouble("edli_max_limit");
				dblEPFAdminChargesTotal += rs.getDouble("pf_admin_charges");
				dblEDLIAdminChargesTotal += rs.getDouble("edli_admin_charges");
				dbl_epf_max_limit_Total += rs.getDouble("epf_max_limit");
				dbl_eps_max_limit_Total += rs.getDouble("eps_max_limit");
				
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("eps_max_limit")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("evpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erdli_contribution")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("edli_max_limit")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("pf_admin_charges")));
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("edli_admin_charges")));
				
				alEmployees.add(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_epf_max_limit_Total));
			hmEarningSalaryMap.put("Total_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_eps_max_limit_Total));
			hmEarningSalaryMap.put("Total_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEPFContributionTotal));
			hmEarningSalaryMap.put("Total_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEVPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPSContributionTotal));
			hmEarningSalaryMap.put("Total_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERDLIContributionTotal));
			hmEarningSalaryMap.put("Total_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(dblEDLIMaxLimitTotal));
			hmEarningSalaryMap.put("Total_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEPFAdminChargesTotal));
			hmEarningSalaryMap.put("Total_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEDLIAdminChargesTotal));
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
			
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
	public void generateEPFSalaryReport(){
		
		try {



			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strMonthYear = (String)request.getAttribute("strMonthYear");


			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpPFNumber = (Map)request.getAttribute("hmEmpPFNumber");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			List alEmployees = (List)request.getAttribute("alEmployees");
			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}


		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();
				
		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>EPF Salary Report for the month of "+strMonthYear+"</b></font></td></tr>" +
				"</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);
		
		
		
		
		document.add(new Paragraph(" "));
			  	
		String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
				"<td width=\"5%\" nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;Sr.No.&nbsp;&nbsp;</strong></font></td>" +
				"<td width=\"20%\" nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;Employee Code&nbsp;&nbsp;</strong></font></td>" +
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;Employee Name&nbsp;&nbsp;</strong></font></td>" +
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EPF Acc. No&nbsp;&nbsp;</strong></font></td>" +
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EPF Wages&nbsp;&nbsp;</strong></font></td>" +
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EPS Wages&nbsp;&nbsp;</strong></font></td>" +
				
				/*"<td  nowrap=\"nowrap\" align=\"center\" colspan=\"2\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +				
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>Employee</strong></font></td>" +
				"</tr><tr><td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>PF Amount</strong></font></td>" +
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>VPF Amount</strong></font></td>"+
				"</tr></table></td>" +*/
				
				"<td colspan=\"2\"  nowrap=\"nowrap\" align=\"center\">" +
				"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td><table><tr><td align=\"center\"  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>Employee</strong></font></td>" +
		"</tr></table></td></tr><tr><td><table><tr><td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>PF Amount</strong></font></td>" +
		"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>VPF Amount</strong></font></td></tr></table></td></tr></table></td>" +
				
				"<td colspan=\"2\"  nowrap=\"nowrap\" align=\"center\">" +
				"<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td><table><tr><td align=\"center\"  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>Employer</strong></font></td>" +
		"</tr></table></td></tr><tr><td><table><tr><td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>PF Amount</strong></font></td>" +
		"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>EPS Amount</strong></font></td></tr></table></td></tr></table></td>" +
		
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EDLI&nbsp;</strong></font></td>" +
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EDLI Max Limit&nbsp;&nbsp;</strong></font></td>" +
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;PF Admin Charges&nbsp;&nbsp;</strong></font></td>" +
				"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EDLI Admin Charges&nbsp;&nbsp;</strong></font></td></tr>" +
		"</table>";
		
		 	  	  	
		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);
		

		
		int count=0;
		for(; count<alEmployees.size(); count++){
			String strEmpId = (String)alEmployees.get(count);
		
			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
			"<td width=\"5%\"><font size=\"1\">&nbsp;"+(count+1)+"&nbsp;&nbsp;</font></td>" +
			"<td width=\"20%\"><font size=\"1\">&nbsp;"+uF.showData((String)hmEmpCode.get(strEmpId), "")+"&nbsp;&nbsp;</font></td>" +
			"<td><font size=\"1\" nowrap=\"nowrap\">&nbsp;"+uF.showData((String)hmEmpName.get(strEmpId), "")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+uF.showData((String)hmEmpPFNumber.get(strEmpId), "")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EPF_MAX_LIMIT")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EPS_MAX_LIMIT")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EEPF_CONTRIBUTION")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EEVPF_CONTRIBUTION")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_ERPF_CONTRIBUTION")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_ERPS_CONTRIBUTION")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_ERDLI_CONTRIBUTION")+"&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EDLI_MAX_LIMIT")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EPF_ADMIN_CHARGES")+"&nbsp;&nbsp;</font></td>" +
			"<td align=\"right\"><font size=\"1\">&nbsp;"+(String)hmEarningSalaryMap.get(strEmpId+"_EDLI_ADMIN_CHARGES")+"&nbsp;&nbsp;</font></td></tr>" +
	"</table>";
	
	 	  	  	
	List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
	Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
	phrase2.add(supList2.get(0));
	document.add(phrase2);
		}
		
		if(count==0)
		{String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
			"<td align=\"center\"><font size=\"1\">No Employees found</font></td></tr></table>";
		List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
		Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase4.add(supList4.get(0));
		document.add(phrase4);
		}else{
		
	String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" +
	"<td width=\"5%\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>" +
	"<td width=\"20%\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>" +
	"<td><font size=\"1\"><b>&nbsp;&nbsp;&nbsp;</b></font></td>" +
	"<td><font size=\"1\"><b>&nbsp;&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")+"&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")+"&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")+"&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")+"&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")+"&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")+"&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")+"&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")+"&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")+"&nbsp;&nbsp;</b></font></td>" +
	"<td align=\"right\"><font size=\"1\"><b>&nbsp;"+(String)hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")+"&nbsp;&nbsp;</b></font></td></tr>" +
	"</table>";
	List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
	Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
	phrase3.add(supList3.get(0));
	document.add(phrase3);
	
		}
	
	
		
		
		
		document.close();
		
		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition","attachment; filename=EPFSalaryReport.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}
	public void viewEPFSalaryReport() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		
		try {
			
			Map hmEmpName = new HashMap();
			Map hmEmpCode = new HashMap();
			Map hmEmpPFNumber = new HashMap();
			
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
			
			
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			

//			Map hmEPFDetailsMap = new HashMap();
//			CF.getEPFDetailsMap(con,hmEPFDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()){
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
			}
			rs.close();
			pst.close();
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_epf_details where financial_year_start=?  and financial_year_end=? and _month=? ");

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
			
			if(uF.parseToInt(getF_service())>0){
				sbQuery.append(" and service_id like '%,"+uF.parseToInt(getF_service())+",%'");
			}
			
			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || uF.parseToInt(getF_org())>0){
				sbQuery.append(" )");
			}
			
			
			
			//sbQuery.append(" group by emp_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			
			
			rs = pst.executeQuery();
			
			
			double dblEEPFContributionTotal = 0;
			double dblEEVPFContributionTotal = 0;
			double dblERPFContributionTotal = 0;
			double dblERPSContributionTotal = 0;
			double dblERDLIContributionTotal = 0;
			double dblEDLIMaxLimitTotal = 0;
			double dblEPFAdminChargesTotal = 0;
			double dblEDLIAdminChargesTotal = 0;
			double dbl_epf_max_limit_Total = 0;
			double dbl_eps_max_limit_Total = 0;
			
			while(rs.next()){
				
				dblEEPFContributionTotal += rs.getDouble("eepf_contribution");
				dblEEVPFContributionTotal += rs.getDouble("evpf_contribution");
				dblERPFContributionTotal += rs.getDouble("erpf_contribution");
				dblERPSContributionTotal += rs.getDouble("erps_contribution");
				dblERDLIContributionTotal += rs.getDouble("erdli_contribution");
				dblEDLIMaxLimitTotal += rs.getDouble("edli_max_limit");
				dblEPFAdminChargesTotal += rs.getDouble("pf_admin_charges");
				dblEDLIAdminChargesTotal += rs.getDouble("edli_admin_charges");
				dbl_epf_max_limit_Total += rs.getDouble("epf_max_limit");
				dbl_eps_max_limit_Total += rs.getDouble("eps_max_limit");
				
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("eps_max_limit")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("evpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erpf_contribution")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erdli_contribution")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("edli_max_limit")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("pf_admin_charges")));
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("edli_admin_charges")));
				
				alEmployees.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_epf_max_limit_Total));
			hmEarningSalaryMap.put("Total_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_eps_max_limit_Total));
			hmEarningSalaryMap.put("Total_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEPFContributionTotal));
			hmEarningSalaryMap.put("Total_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEVPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPFContributionTotal));
			hmEarningSalaryMap.put("Total_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPSContributionTotal));
			hmEarningSalaryMap.put("Total_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERDLIContributionTotal));
			hmEarningSalaryMap.put("Total_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(dblEDLIMaxLimitTotal));
			hmEarningSalaryMap.put("Total_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEPFAdminChargesTotal));
			hmEarningSalaryMap.put("Total_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEDLIAdminChargesTotal));
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			
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
