package com.konnect.jpms.payroll.reports;

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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LWFSalaryReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(LWFSalaryReport.class);
	
	String paramSelection;

	String financialYear;
	String strMonth;

	String strLocation;
	String strDepartment;
	String strSbu;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillOrganisation> orgList;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	List<FillServices> serviceList;
	
	String exportType;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();  
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, TReportLWFSalary);
		request.setAttribute(PAGE, PReportLWFSalary); 
		

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		
		if(getStrMonth()==null){
			setStrMonth("1");
		}
		if(getParamSelection() == null){
			setParamSelection("EMP");
		}
		
		
		getLWFSalaryReport(uF);

		return loadLWFSalaryReport(uF);

	}
	
//	private void getLWFSalaryReport(UtilityFunctions uF) {
//		
//		if(getF_org()==null){
//			setF_org("-1");
//		}
//		
//		if(uF.parseToInt(getF_org())>-1){
//			if(uF.parseToInt(getF_strWLocation())>-1){				
//				if(uF.parseToInt(getF_department())>-1){
//					
//					/*if(uF.parseToInt(getF_level())>-1){
//						viewLWFSalaryReport(uF);
//					}else{
//						viewLWFSalaryReportByLevel(uF);
//					}*/
//					if(uF.parseToInt(getF_service())>-1){
//						viewLWFSalaryReport(uF);
//					}else{
//						viewLWFSalaryReportByService(uF);
//					}
//					
//				}else{
//					viewLWFSalaryReportByDepartment(uF);
//				}
//			}else{
//				viewLWFSalaryReportByLocation(uF);
//			}			
//			
//		}else{
//			if(uF.parseToInt(getF_org())==-1){
//				viewLWFSalaryReportByOrg(uF);
//			}
//		}
//	}
private void getLWFSalaryReport(UtilityFunctions uF) {
	if(getParamSelection().equals("ORG")){
		viewLWFSalaryReportByOrg(uF);
		if(getExportType()!= null && getExportType().equals("excel")){
			generateExcelReportBy(uF);
		} else if(getExportType()!= null && getExportType().equals("pdf")){
			generatePdfReportBy(uF);
		}
	} else if(getParamSelection().equals("WL")){
		viewLWFSalaryReportByLocation(uF);
		if(getExportType()!= null && getExportType().equals("excel")){
			generateExcelReportBy(uF);
		} else if(getExportType()!= null && getExportType().equals("pdf")){
			generatePdfReportBy(uF);
		}
	} else if(getParamSelection().equals("DEPART")){
		viewLWFSalaryReportByDepartment(uF);
		if(getExportType()!= null && getExportType().equals("excel")){
			generateExcelReportBy(uF);
		} else if(getExportType()!= null && getExportType().equals("pdf")){
			generatePdfReportBy(uF);
		}
	} else if(getParamSelection().equals("SBU")){
		viewLWFSalaryReportByService(uF);
		if(getExportType()!= null && getExportType().equals("excel")){
			generateExcelReportBy(uF);
		} else if(getExportType()!= null && getExportType().equals("pdf")){
			generatePdfReportBy(uF);
		}
	} else {
		viewLWFSalaryReport(uF);
		if(getExportType()!= null && getExportType().equals("excel")){
			generateExcelReport(uF);
		} else if(getExportType()!= null && getExportType().equals("pdf")){
			generatePdfReport(uF);
		}
	}
}

private void generateExcelReportBy(UtilityFunctions uF) {
	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		
		con = db.makeConnection(con);

		String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
		String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
		String strMonthYear = (String) request.getAttribute("strMonthYear");

		Map hmEmpName = (Map) request.getAttribute("hmEmpName");
		Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
		List alEmployees = (List) request.getAttribute("alEmployees");
		if (strFinancialYearStart != null && strFinancialYearEnd != null) {
			strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		}

		Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");

		Map<String, String> hmWLocation = CF.getWLocationMap(con, null, null);
		Map<String, String> hmDept = CF.getDeptMap(con);
		Map<String, String> hmLevelMap = (Map<String, String>) request.getAttribute("hmLevelMap");
		Map<String, String> hmServicesMap = (Map<String, String>) request.getAttribute("hmServicesMap");

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

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Payment Held");

		List<DataStyle> header = new ArrayList<DataStyle>();
		header.add(new DataStyle("LWF Salary Report for the month of " + strMonthYear, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",
				BaseColor.LIGHT_GRAY));
		header.add(new DataStyle(title, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("LWF Wages", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Employee Contribution", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Employer Contribution", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

		int count = 0;
		List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
		double dblWagesTotal = 0;
		double dblEmployeeContrTotal = 0;
		double dblEmployerContrAmountTotal = 0;

		for (; count < alEmployees.size(); count++) {
			String strEmpId = (String) alEmployees.get(count);

			String strName = "";
			if(getParamSelection() != null && getParamSelection().equals("ORG")){
				strName =uF.showData((String)hmOrg.get(strEmpId), "");
			}else if(getParamSelection() != null && getParamSelection().equals("WL")){
				strName =uF.showData((String)hmWLocation.get(strEmpId), "");
			}else if(getParamSelection() != null && getParamSelection().equals("DEPART")){
				strName =uF.showData((String)hmDept.get(strEmpId), "");
			}else if(getParamSelection() != null && getParamSelection().equals("SBU")){
				strName =uF.showData((String)hmServicesMap.get(strEmpId), "");
			}

			String wages = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_GE"), "0");
			String employeeContr = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EE"), "0");
			String employerContr = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ER"), "0");

			dblWagesTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_GE"));
			dblEmployeeContrTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EE"));
			dblEmployerContrAmountTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_ER"));

			List<DataStyle> innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle(strName, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(wages, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(employeeContr, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(employerContr, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));

			reportData.add(innerList);

		}

		if(count > 0){
			List<DataStyle> innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("Total", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String) hmEarningSalaryMap.get("Total_GE"), "0"), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String) hmEarningSalaryMap.get("Total_EE"), "0"), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String) hmEarningSalaryMap.get("Total_ER"), "0"), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
			
			reportData.add(innerList);
		}

		ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
		sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		workbook.write(buffer);
		response.setContentType("application/vnd.ms-excel:UTF-8");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition", "attachment; filename=LWFSalaryExcelReports.xls");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeConnection(con);
	}

}


private void generatePdfReportBy(UtilityFunctions uF) {
	Connection con = null;
	Database db = new Database();
	db.setRequest(request);
	try {

		

		con = db.makeConnection(con);
		String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
		String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
		String strMonthYear = (String) request.getAttribute("strMonthYear");

		Map hmEmpName = (Map) request.getAttribute("hmEmpName");
		Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
		List alEmployees = (List) request.getAttribute("alEmployees");
		if (strFinancialYearStart != null && strFinancialYearEnd != null) {
			strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		}

		Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");

		Map<String, String> hmWLocation = CF.getWLocationMap(con, null, null);
		Map<String, String> hmDept = CF.getDeptMap(con);
		Map<String, String> hmLevelMap = (Map<String, String>) request.getAttribute("hmLevelMap");
		Map<String, String> hmServicesMap = (Map<String, String>) request.getAttribute("hmServicesMap");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();

		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>LWF Salary Report for the month of "
				+ strMonthYear + "</b></font></td></tr>" + "</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);

		document.add(new Paragraph(" "));

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

		String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>"
				+ "<td><font size=\"1\"><strong>&nbsp;" + title+ "&nbsp;&nbsp;</strong></font></td>" 
				+ "<td><font size=\"1\"><strong>&nbsp;LWF Wages&nbsp;&nbsp;</strong></font></td>"
				+ "<td><font size=\"1\"><strong>&nbsp;Employee Contribution&nbsp;</strong></font></td>"
				+ "<td><font size=\"1\"><strong>&nbsp;Employer Contribution&nbsp;&nbsp;</strong></font></td>" 
				+ "</tr>" 
				+ "</table>";

		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);

		int count = 0;
		for (; count < alEmployees.size(); count++) {
			String strEmpId = (String) alEmployees.get(count);

			String strName = "";
			if(getParamSelection() != null && getParamSelection().equals("ORG")){
				strName =uF.showData((String)hmOrg.get(strEmpId), "");
			}else if(getParamSelection() != null && getParamSelection().equals("WL")){
				strName =uF.showData((String)hmWLocation.get(strEmpId), "");
			}else if(getParamSelection() != null && getParamSelection().equals("DEPART")){
				strName =uF.showData((String)hmDept.get(strEmpId), "");
			}else if(getParamSelection() != null && getParamSelection().equals("SBU")){
				strName =uF.showData((String)hmServicesMap.get(strEmpId), "");
			}

			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" 
					+ "<td><font size=\"1\">&nbsp;" + strName + "&nbsp;&nbsp;</font></td>"
					+ "<td align=\"right\"><font size=\"1\">&nbsp;" + uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_GE"), "0")+ "&nbsp;&nbsp;</font></td>" 
					+ "<td align=\"right\"><font size=\"1\">&nbsp;"+ uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EE"), "0") + "&nbsp;</font></td>"
					+ "<td align=\"right\"><font size=\"1\">&nbsp;" + uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ER"), "0")+ "&nbsp;&nbsp;</font></td>" 
					+ "</tr>" + "</table>";

			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase2.add(supList2.get(0));
			document.add(phrase2);

		}

		if (count == 0) {
			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>"
					+ "<td align=\"center\"><font size=\"1\">No Employees found</font></td></tr></table>";
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase4.add(supList4.get(0));
			document.add(phrase4);

		} else {
			String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>"
					+ "<td><font size=\"1\">&nbsp;Total&nbsp;</font></td>"
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;" + uF.showData((String) hmEarningSalaryMap.get("Total_GE"), "0")+ "</b>&nbsp;&nbsp;</font></td>" 
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEarningSalaryMap.get("Total_EE"), "0") + "</b>&nbsp;</font></td>"
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;" + uF.showData((String) hmEarningSalaryMap.get("Total_ER"), "0")+ "</b>&nbsp;&nbsp;</font></td>" 
					+ "</tr>" + "</table>";

			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase3.add(supList3.get(0));
			document.add(phrase3);

		}

		document.close();

		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition", "attachment; filename=LWFSalaryReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeConnection(con);
	}

}


private void generateExcelReport(UtilityFunctions uF) {

	try {

		String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
		String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
		String strMonthYear = (String) request.getAttribute("strMonthYear");

		Map hmEmpName = (Map) request.getAttribute("hmEmpName");
		Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
		List alEmployees = (List) request.getAttribute("alEmployees");
		if (strFinancialYearStart != null && strFinancialYearEnd != null) {
			strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		}
		Map<String, String> hmEmpPaidDays = (Map<String, String>) request.getAttribute("hmEmpPaidDays");
		if (hmEmpPaidDays == null) hmEmpPaidDays = new HashMap<String, String>();
		
		Map<String,String> hmEmpCode = (Map<String, String>) request.getAttribute("hmEmpCode");
		if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
		Map<String, String> hmEmpESICNo = (Map<String, String>) request.getAttribute("hmEmpESICNo");
		if(hmEmpESICNo == null) hmEmpESICNo = new HashMap<String, String>();

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Payment Held");

		List<DataStyle> header = new ArrayList<DataStyle>();
		header.add(new DataStyle("LWF Salary Report for the month of " + strMonthYear, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("LWF Wages", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Employee Contribution", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
		header.add(new DataStyle("Employer Contribution", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

		int count = 0;
		List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
		for (; count < alEmployees.size(); count++) {
			String strEmpId = (String) alEmployees.get(count);

			String empCode = uF.showData((String) hmEmpCode.get(strEmpId), "");
			String empName = uF.showData((String) hmEmpName.get(strEmpId), "");
			String wages = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_GE"), "0");
			String employeeContr = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EE"), "0");
			String employerContr = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ER"), "0");

			List<DataStyle> innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle(empCode, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(empName, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(wages, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(employeeContr, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(employerContr, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));

			reportData.add(innerList);

		}

		if(count > 0){
			List<DataStyle> innerList = new ArrayList<DataStyle>();
			innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle("Total", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String) hmEarningSalaryMap.get("Total_GE"), "0"), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String) hmEarningSalaryMap.get("Total_EE"), "0"), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String) hmEarningSalaryMap.get("Total_ER"), "0"), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
	
			reportData.add(innerList);
		}
		ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
		sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		workbook.write(buffer);
		response.setContentType("application/vnd.ms-excel:UTF-8");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition", "attachment; filename=LWFSalaryExcelReports.xls");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();

	} catch (Exception e) {
		e.printStackTrace();
	}

}


private void generatePdfReport(UtilityFunctions uF) {

	try {

		String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
		String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
		String strMonthYear = (String) request.getAttribute("strMonthYear");

		Map hmEmpName = (Map) request.getAttribute("hmEmpName");
		Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
		List alEmployees = (List) request.getAttribute("alEmployees");
		if (strFinancialYearStart != null && strFinancialYearEnd != null) {
			strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		}
		Map<String, String> hmEmpPaidDays = (Map<String, String>) request.getAttribute("hmEmpPaidDays");
		if (hmEmpPaidDays == null) hmEmpPaidDays = new HashMap<String, String>();
		
		Map<String,String> hmEmpCode = (Map<String, String>) request.getAttribute("hmEmpCode");
		if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
		Map<String, String> hmEmpESICNo = (Map<String, String>) request.getAttribute("hmEmpESICNo");
		if(hmEmpESICNo == null) hmEmpESICNo = new HashMap<String, String>();

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, buffer);
		document.open();

		String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>LWF Salary Report for the month of "
				+ strMonthYear + "</b></font></td></tr>" + "</table>";
		List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
		Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase.add(supList.get(0));
		document.add(phrase);

		document.add(new Paragraph(" "));

		String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>"
				+ "<td><font size=\"1\"><strong>&nbsp;Employee Code&nbsp;&nbsp;</strong></font></td>"
				+ "<td><font size=\"1\"><strong>&nbsp;Employee Name&nbsp;&nbsp;</strong></font></td>"
				+ "<td><font size=\"1\"><strong>&nbsp;LWF Wages&nbsp;&nbsp;</strong></font></td>"
				+ "<td><font size=\"1\"><strong>&nbsp;Employee Contribution&nbsp;</strong></font></td>"
				+ "<td><font size=\"1\"><strong>&nbsp;Employer Contribution&nbsp;&nbsp;</strong></font></td>" + "</tr>" + "</table>";

		List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
		Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
		phrase1.add(supList1.get(0));
		document.add(phrase1);

		int count = 0;
		for (; count < alEmployees.size(); count++) {
			String strEmpId = (String) alEmployees.get(count);

			String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" 
				+ "<td><font size=\"1\">&nbsp;" + uF.showData((String) hmEmpCode.get(strEmpId), "")+ "&nbsp;&nbsp;</font></td>"
				+ "<td><font size=\"1\">&nbsp;" + uF.showData((String) hmEmpName.get(strEmpId), "")+ "&nbsp;&nbsp;</font></td>"
				+ "<td align=\"right\"><font size=\"1\">&nbsp;" + uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_GE"), "0")+ "&nbsp;&nbsp;</font></td>" 
				+ "<td align=\"right\"><font size=\"1\">&nbsp;"+ uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EE"), "0") + "&nbsp;</font></td>"
				+ "<td align=\"right\"><font size=\"1\">&nbsp;" + uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ER"), "0")+ "&nbsp;&nbsp;</font></td>" 
				+ "</tr>" + "</table>";

			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase2.add(supList2.get(0));
			document.add(phrase2);

		}

		if (count == 0) {
			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>"
					+ "<td align=\"center\"><font size=\"1\">No Employees found</font></td></tr></table>";
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase4.add(supList4.get(0));
			document.add(phrase4);

		} else {
			String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>"
					+ "<td align=\"right\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>"
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;Total&nbsp;</b></font></td>" 
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEarningSalaryMap.get("Total_GE"), "0") + "</b>&nbsp;&nbsp;</font></td>"
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;" + uF.showData((String) hmEarningSalaryMap.get("Total_EE"), "0")+ "</b>&nbsp;</font></td>" 
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEarningSalaryMap.get("Total_ER"), "0") + "</b>&nbsp;&nbsp;</font></td>" + "</tr>" + "</table>";

			List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
			Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase3.add(supList3.get(0));
			document.add(phrase3);

		}

		document.close();

		response.setContentType("application/pdf");
		response.setContentLength(buffer.size());
		response.setHeader("Content-Disposition", "attachment; filename=LWFSalaryReports.pdf");
		ServletOutputStream out = response.getOutputStream();
		buffer.writeTo(out);
		out.flush();
		buffer.close();
		out.close();

	} catch (Exception e) {
		e.printStackTrace();
	}

}

	private void viewLWFSalaryReportByService(UtilityFunctions uF) {

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
			
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			
			
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
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
//			Map hmESIDetailsMap = new HashMap();
//			CF.getESIDetailsMap(con,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(lwf_max_limit) as lwf_max_limit,sum(eelwf_contribution) as eelwf_contribution,sum(erlwf_contribution) as erlwf_contribution, a.service_id " +
					"from (select pg.emp_id,pg.service_id from payroll_generation pg,employee_official_details eod " +
					"where month=? and year=? and is_paid=true and financial_year_from_date=? and financial_year_to_date=? " +
					"and pg.emp_id=eod.emp_id and salary_head_id=? and amount>0 ");
			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and pg.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}			
			sbQuery.append(" group by pg.service_id,pg.emp_id) a," +
					"emp_lwf_details eed,employee_official_details eod  where  _month=? and financial_year_start=? and financial_year_end=? " +
					"and a.emp_id=eed.emp_id and eod.emp_id=eed.emp_id and a.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }		
			sbQuery.append(" group by a.service_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setInt(2, uF.parseToInt(strYear));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_LWF);
			pst.setInt(6, uF.parseToInt(getStrMonth()));
			pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			System.out.println("pst==>"+pst); 
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			double dblAmountTotal = 0;
			while(rs.next()){
				double eelwf_contribution = uF.parseToDouble(rs.getString("eelwf_contribution"));
				dblEEContributionTotal += eelwf_contribution;
				hmEarningSalaryMap.put(rs.getString("service_id")+"_EE", uF.formatIntoTwoDecimal(eelwf_contribution));
				
				double erlwf_contribution = uF.parseToDouble(rs.getString("erlwf_contribution"));
				dblERContributionTotal += erlwf_contribution;
				hmEarningSalaryMap.put(rs.getString("service_id")+"_ER", uF.formatIntoTwoDecimal(erlwf_contribution));
				
				double dblAmount = uF.parseToDouble(rs.getString("lwf_max_limit"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("service_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("service_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			pst = con.prepareStatement("SELECT * FROM services order by service_name");
			rs = pst.executeQuery();
			Map<String, String> hmServicesMap = new HashMap<String, String>();
			while (rs.next()) {
				hmServicesMap.put(rs.getString("service_id"), rs.getString("service_name"));
			}
			rs.close();
			pst.close();
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strServiceId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>(); 
				alInner.add(uF.showData(hmServicesMap.get(strServiceId), "0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strServiceId+"_GE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strServiceId+"_EE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strServiceId+"_ER"),"0"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("Total");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_GE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_EE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_ER"),"0")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
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


//	private void viewLWFSalaryReportByLevel(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//
//		
//		try {
//			
//			
//			String strMonthYear = null;
//			
//			String[] strPayCycleDates = null;
//			String strFinancialYearStart = null;
//			String strFinancialYearEnd = null;
//
//			if (getFinancialYear() != null) {
//				
//				strPayCycleDates = getFinancialYear().split("-");
//				strFinancialYearStart = strPayCycleDates[0];
//				strFinancialYearEnd = strPayCycleDates[1];
//			
//			} else {
//				
//				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
//				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
//				
//				strFinancialYearStart = strPayCycleDates[0];
//				strFinancialYearEnd = strPayCycleDates[1];
//				 
//			}
//			
//			
//			int nselectedMonth = uF.parseToInt(getStrMonth());
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
//			
//			
//			
//			
//			Calendar cal = GregorianCalendar.getInstance();
//			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
//			if(nselectedMonth>=nFYSMonth){
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
//			}else{
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy")));
//			}
//			
//			
//			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
//			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
//			
//			String strDateStart =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
//			String strDateEnd =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
//			
//			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");	
//			
//			String strYear=uF.getDateFormat(strDateStart, DATE_FORMAT, "yyyy");
//			
//			
//			
//			Map hmEarningSalaryMap = new HashMap();
//			List alEmployees = new ArrayList();
//			
//			
//			con = db.makeConnection(con);
//			
//			Map hmEmpName = CF.getEmpNameMap(con,null, null);
//			Map hmESIDetailsMap = new HashMap();
//			CF.getESIDetailsMap(con,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd);
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select sum(amount) as amount,level_id from (select sum(amount) as amount,eod.grade_id from payroll_generation pg," +
//					"employee_official_details eod where  month=? and year=? and is_paid=true and financial_year_from_date=? " +
//					" and financial_year_to_date=? " +
//					" and pg.emp_id=eod.emp_id  and salary_head_id=? and amount>0  and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
//					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");
//
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
//			}
//			if(uF.parseToInt(getF_strWLocation())>0){
//				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
//			}
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
//			}
//			if(uF.parseToInt(getF_service())>0){
//				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
//			}			
//			sbQuery.append(" group by eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
//					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
//					" where a.grade_id=b.grade_id group by level_id");
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
//			pst.setInt(2, uF.parseToInt(strYear));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(5, EMPLOYER_LWF);
//			System.out.println("pst==>"+pst);
//			rs = pst.executeQuery();
//			double dblERContributionTotal = 0;
//			
//			while(rs.next()){
//				double dblAmount = uF.parseToDouble(rs.getString("amount"));
//				dblERContributionTotal += dblAmount;
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_ER", uF.formatIntoTwoDecimal(dblAmount));
//			}
//			
//			sbQuery = new StringBuilder();
//			sbQuery.append("select sum(amount) as amount,level_id from (select sum(amount) as amount,eod.grade_id from payroll_generation pg," +
//					"employee_official_details eod where  month=? and year=? and is_paid=true and financial_year_from_date=? " +
//					" and financial_year_to_date=? " +
//					" and pg.emp_id=eod.emp_id  and salary_head_id=? and amount>0  and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
//					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");
//
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
//			}
//			if(uF.parseToInt(getF_strWLocation())>0){
//				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
//			}
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
//			}
//			if(uF.parseToInt(getF_service())>0){
//				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
//			}			
//			sbQuery.append(" group by eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
//					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
//					" where a.grade_id=b.grade_id group by level_id");
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
//			pst.setInt(2, uF.parseToInt(strYear));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(5, EMPLOYEE_LWF);
//			System.out.println("pst==>"+pst);
//			rs = pst.executeQuery();
//			double dblEEContributionTotal = 0;
//			while(rs.next()){
//				double dblAmount = uF.parseToDouble(rs.getString("amount"));
//				dblEEContributionTotal += dblAmount;
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_EE", uF.formatIntoTwoDecimal(dblAmount));
//			}
//			
//			String salaryHeadId=(String)hmESIDetailsMap.get("SALARY_HEAD_ID");
//			salaryHeadId=salaryHeadId!=null && !salaryHeadId.equals("") ?salaryHeadId.substring(0,salaryHeadId.length()-1) : "";
//			sbQuery = new StringBuilder();
//			sbQuery.append("select sum(amount) as amount,level_id from (select sum(amount) as amount,eod.grade_id from payroll_generation pg," +
//					"employee_official_details eod where  month=? and year=? and is_paid=true and financial_year_from_date=? " +
//					" and financial_year_to_date=? " +
//					" and pg.emp_id=eod.emp_id  and salary_head_id in ("+salaryHeadId+") and amount>0  and eod.grade_id in (select grade_id from designation_details dd, level_details ld, " +
//					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");
//
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
//			}
//			if(uF.parseToInt(getF_strWLocation())>0){
//				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
//			}
//			if(uF.parseToInt(getF_department())>0){
//				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
//			}
//
//			if(uF.parseToInt(getF_service())>0){
//				sbQuery.append(" and pg.service_id ="+uF.parseToInt(getF_service()));
//			}			
//			sbQuery.append(" and pg.emp_id in( select emp_id from payroll_generation where salary_head_id in ("+EMPLOYER_LWF+","+EMPLOYEE_LWF+") and amount>0) group by eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
//					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
//					" where a.grade_id=b.grade_id group by level_id");
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(getStrMonth()));
//			pst.setInt(2, uF.parseToInt(strYear));
//			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
//			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
//			rs = pst.executeQuery();
//			double dblAmountTotal = 0;
//			while(rs.next()){
//				double dblAmount = uF.parseToDouble(rs.getString("amount"));
//				dblAmountTotal += dblAmount;
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
//				alEmployees.add(rs.getString("level_id"));
//			}
//			
//			
//			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
//			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
//			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
//			
//			
//			
//			
//			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
//			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
//			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
//			
//			request.setAttribute("hmEmpName", hmEmpName);
//			request.setAttribute("strMonthYear", strMonthYear);
//			request.setAttribute("alEmployees", alEmployees);
//			
//			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
//			Map<String, String> hmDept =CF.getDeptMap(con); 
//			
//			request.setAttribute("hmWLocation", hmWLocation);
//			request.setAttribute("hmDept", hmDept);
//			
//			pst = con.prepareStatement("SELECT * FROM level_details order by level_id");
//			rs = pst.executeQuery();
//			Map<String, String> hmLevelMap = new HashMap<String, String>();
//			while (rs.next()) {
//				hmLevelMap.put(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name"));
//			}
//			
//			request.setAttribute("hmLevelMap", hmLevelMap);
//			
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		} finally {
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs);
//			db.closeConnection(con);
//		}
//
//	}


	private void viewLWFSalaryReportByDepartment(UtilityFunctions uF) {

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
			
			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);

//			Map hmESIDetailsMap = new HashMap();
//			CF.getESIDetailsMap(con,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(lwf_max_limit) as lwf_max_limit,sum(eelwf_contribution) as eelwf_contribution,sum(erlwf_contribution) as erlwf_contribution,eod.depart_id " +
					"from emp_lwf_details eed,employee_official_details eod where _month=? and financial_year_start=? " +
					"and financial_year_end=? and eed.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			sbQuery.append("and eed.emp_id in(select emp_id from payroll_generation where  month=? and year=? and is_paid=true " +
					"and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? and amount>0 ");
			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id in ("+StringUtils.join(getF_service(), ",")+") ");
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
			pst.setInt(8, EMPLOYEE_LWF);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			double dblAmountTotal = 0;
			while(rs.next()){
				double eelwf_contribution = uF.parseToDouble(rs.getString("eelwf_contribution"));
				dblEEContributionTotal += eelwf_contribution;
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_EE", uF.formatIntoTwoDecimal(eelwf_contribution));
				
				double erlwf_contribution = uF.parseToDouble(rs.getString("erlwf_contribution"));
				dblERContributionTotal += erlwf_contribution;
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_ER", uF.formatIntoTwoDecimal(erlwf_contribution));
				
				double dblAmount = uF.parseToDouble(rs.getString("lwf_max_limit"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("depart_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("depart_id"));
			}
			rs.close();
			pst.close();
			
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strDeptId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(hmDept.get(strDeptId), "0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strDeptId+"_GE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strDeptId+"_EE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strDeptId+"_ER"),"0"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("Total");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_GE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_EE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_ER"),"0")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
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


	private void viewLWFSalaryReportByLocation(UtilityFunctions uF) {

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
			
			
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			

			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
//			Map hmESIDetailsMap = new HashMap();
//			CF.getESIDetailsMap(con,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(lwf_max_limit) as lwf_max_limit,sum(eelwf_contribution) as eelwf_contribution,sum(erlwf_contribution) as erlwf_contribution,eod.wlocation_id " +
					"from emp_lwf_details eed,employee_official_details eod where _month=? and financial_year_start=? " +
					"and financial_year_end=? and eed.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			sbQuery.append("and eed.emp_id in(select emp_id from payroll_generation where  month=? and year=? and is_paid=true " +
					"and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? and amount>0 ");
			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id in ("+StringUtils.join(getF_service(), ",")+") ");
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
			pst.setInt(8, EMPLOYEE_LWF);
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			double dblAmountTotal = 0;
			while(rs.next()){
				double eesi_contribution = uF.parseToDouble(rs.getString("eelwf_contribution"));
				dblEEContributionTotal += eesi_contribution;
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_EE", uF.formatIntoTwoDecimal(eesi_contribution));
				
				double ersi_contribution = uF.parseToDouble(rs.getString("erlwf_contribution"));
				dblERContributionTotal += ersi_contribution;
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_ER", uF.formatIntoTwoDecimal(ersi_contribution));
				
				double dblAmount = uF.parseToDouble(rs.getString("lwf_max_limit"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("wlocation_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("wlocation_id"));
			}
			rs.close();
			pst.close();
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strLocationId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(hmWLocation.get(strLocationId), "0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strLocationId+"_GE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strLocationId+"_EE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strLocationId+"_ER"),"0"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("Total");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_GE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_EE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_ER"),"0")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
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


	private void viewLWFSalaryReportByOrg(UtilityFunctions uF) {

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
			
			
			
			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
			List<String> alEmployees = new ArrayList<String>();
			

			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con,null, null);
//			Map<String, String> hmESIDetailsMap = new HashMap<String, String>();
//			CF.getESIDetailsMap(con,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(lwf_max_limit) as lwf_max_limit,sum(eelwf_contribution) as eelwf_contribution,sum(erlwf_contribution) as erlwf_contribution,eod.org_id " +
					"from emp_lwf_details eed,employee_official_details eod where _month=? and financial_year_start=? " +
					"and financial_year_end=? and eed.emp_id=eod.emp_id ");

			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			sbQuery.append("and eod.emp_id in(select emp_id from payroll_generation where  month=? and year=? and is_paid=true " +
					"and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? and amount>0 ");
			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id in ("+StringUtils.join(getF_service(), ",")+") ");
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
			pst.setInt(8, EMPLOYEE_LWF);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			double dblAmountTotal = 0;
			while(rs.next()){		
				double eelwf_contribution = uF.parseToDouble(rs.getString("eelwf_contribution"));
				dblEEContributionTotal += eelwf_contribution;				
				hmEarningSalaryMap.put(rs.getString("org_id")+"_EE", uF.formatIntoTwoDecimal(eelwf_contribution));
				
				double erlwf_contribution = uF.parseToDouble(rs.getString("erlwf_contribution"));
				dblERContributionTotal += erlwf_contribution;				
				hmEarningSalaryMap.put(rs.getString("org_id")+"_ER", uF.formatIntoTwoDecimal(erlwf_contribution));
				
				double dblAmount = uF.parseToDouble(rs.getString("lwf_max_limit"));
				dblAmountTotal += dblAmount;
				hmEarningSalaryMap.put(rs.getString("org_id")+"_GE", uF.formatIntoTwoDecimal(dblAmount));
				alEmployees.add(rs.getString("org_id"));
			}
			rs.close();
			pst.close();
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strOrgId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(hmOrg.get(strOrgId), "0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strOrgId+"_GE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strOrgId+"_EE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strOrgId+"_ER"),"0"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("Total");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_GE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_EE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_ER"),"0")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			request.setAttribute("hmOrg", hmOrg);
			
			
			 
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	

	public String loadLWFSalaryReport(UtilityFunctions uF) {

		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();

		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);

		getSelectedFilter(uF);

		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

//	public String loadLWFSalaryReport(UtilityFunctions uF) {
//		
//		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
//		monthList = new FillMonth().fillMonth();
//		
//		orgList = new FillOrganisation(request).fillOrganisation();
//		orgList.add(new FillOrganisation("0","All Organization"));
//		
//		Collections.sort(orgList, new Comparator<FillOrganisation>() {
//
//			@Override
//			public int compare(FillOrganisation o1, FillOrganisation o2) {
//				return o1.getOrgId().compareTo(o2.getOrgId());
//			}
//		});
//		
//		if(getF_org()!=null && uF.parseToInt(getF_org())>-1){
//			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//			wLocationList.add(new FillWLocation("-1","Work Location Wise"));
//			wLocationList.add(new FillWLocation("0","All Work Location"));
//			
//			
//			
//		}else{
//			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//			wLocationList.add(new FillWLocation("0","All Work Location"));
//		}
//		
//		Collections.sort(wLocationList, new Comparator<FillWLocation>() {
//
//			@Override
//			public int compare(FillWLocation o1, FillWLocation o2) {
//				return o1.getwLocationId().compareTo(o2.getwLocationId());
//			}
//		});
//		
//		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1){
//			departmentList = new FillDepartment(request).fillDepartment();
//			departmentList.add(new FillDepartment("-1","Department Wise"));
//			departmentList.add(new FillDepartment("0","All Departments"));
//			
//			
//		}else{
//			departmentList = new FillDepartment(request).fillDepartment();
//			departmentList.add(new FillDepartment("0","All Departments"));
//		}
//		
//		Collections.sort(departmentList, new Comparator<FillDepartment>() {
//
//			@Override
//			public int compare(FillDepartment o1, FillDepartment o2) {
//				return o1.getDeptId().compareTo(o2.getDeptId());
//			}
//		});
//		
//		
//		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1 && getF_department()!=null && uF.parseToInt(getF_department())>-1){
//			levelList = new FillLevel(request).fillLevel();
//			levelList.add(new FillLevel("-1","Level Wise"));
//			levelList.add(new FillLevel("0","All Levels"));
//			
//			
//			
//		}else{		
//			levelList = new FillLevel(request).fillLevel();
//			levelList.add(new FillLevel("0","All Levels"));
//		}
//		
//		Collections.sort(levelList, new Comparator<FillLevel>() {
//
//			@Override
//			public int compare(FillLevel o1, FillLevel o2) {
//				return o1.getLevelId().compareTo(o2.getLevelId());
//			}
//		});
//		
//		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1 && getF_department()!=null && uF.parseToInt(getF_department())>-1){
//			serviceList = new FillServices(request).fillServices(getF_org(), uF);
//			serviceList.add(new FillServices("-1","Service Wise"));
//			serviceList.add(new FillServices("0","All Service"));
//			 
//		}else{		
//			serviceList = new FillServices(request).fillServices(getF_org(), uF);
//			serviceList.add(new FillServices("0","All Service"));
//		}
//		
//		Collections.sort(serviceList, new Comparator<FillServices>() {
//
//			@Override
//			public int compare(FillServices o1, FillServices o2) {
//				return o1.getServiceId().compareTo(o2.getServiceId());
//			}
//		});
//		
//		
//		return LOAD;
//	}
	
	
	public String viewLWFSalaryReport(UtilityFunctions uF) {

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
			
			
			
			Map<String,String> hmEarningSalaryMap = new HashMap<String,String>();
			List<String> alEmployees = new ArrayList<String>();
			
			con = db.makeConnection(con);
			Map<String,String> hmEmpName = CF.getEmpNameMap(con,null, null);
			if(hmEmpName == null) hmEmpName = new HashMap<String, String>();
			Map<String,String> hmEmpCode = CF.getEmpCodeMap(con);
			if(hmEmpCode == null) hmEmpCode = new HashMap<String, String>();
//			Map hmESIDetailsMap = new HashMap();
//			CF.getESIDetailsMap(con,hmESIDetailsMap, strFinancialYearStart, strFinancialYearEnd);
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_lwf_details where  _month=? and financial_year_start=? and financial_year_end=?");

			if((getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            
            if((getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}
			sbQuery.append("and emp_id in(select emp_id from payroll_generation where  month=? and year=? and is_paid=true " +
					"and financial_year_from_date=? and financial_year_to_date=? and salary_head_id=? and amount>0 ");
			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append(")");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setInt(5, uF.parseToInt(strYear));
			pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(8, EMPLOYEE_LWF);
			
			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			double dblEEContributionTotal = 0;
			double dblERContributionTotal = 0;
			double dblAmountTotal = 0;
			while(rs.next()){
				
				double dblEEAmount = uF.parseToDouble(rs.getString("eelwf_contribution"));
				dblEEContributionTotal += dblEEAmount;
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_EE", uF.formatIntoTwoDecimal(dblEEAmount));			
				
				double dblERAmount = uF.parseToDouble(rs.getString("erlwf_contribution"));
				dblERContributionTotal += dblERAmount;				
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_ER", uF.formatIntoTwoDecimal(dblERAmount));
				
				double dblGSAmount = uF.parseToDouble(rs.getString("lwf_max_limit"));
				dblAmountTotal += dblGSAmount;
				hmEarningSalaryMap.put(rs.getString("emp_id")+"_GE", uF.formatIntoTwoDecimal(dblGSAmount));
				
				alEmployees.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			hmEarningSalaryMap.put("Total_GE", uF.formatIntoTwoDecimal(dblAmountTotal));
			hmEarningSalaryMap.put("Total_EE", uF.formatIntoTwoDecimal(dblEEContributionTotal));
			hmEarningSalaryMap.put("Total_ER", uF.formatIntoTwoDecimal(dblERContributionTotal));
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 

			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strEmpId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(hmEmpCode.get(strEmpId), ""));
				alInner.add(uF.showData(hmEmpName.get(strEmpId), ""));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strEmpId+"_GE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strEmpId+"_EE"),"0"));
				alInner.add(uF.showData(hmEarningSalaryMap.get(strEmpId+"_ER"),"0"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_GE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_EE"),"0")+"");
				alInner.add(""+uF.showData(hmEarningSalaryMap.get("Total_ER"),"0")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			request.setAttribute("hmOrg", hmOrg);
			
			
			
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

	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}


	public String getParamSelection() {
		return paramSelection;
	}


	public void setParamSelection(String paramSelection) {
		this.paramSelection = paramSelection;
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


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String[] getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}


	public String[] getF_service() {
		return f_service;
	}


	public void setF_service(String[] f_service) {
		this.f_service = f_service;
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


	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}


	public List<FillMonth> getMonthList() {
		return monthList;
	}


	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}


	public String getExportType() {
		return exportType;
	}


	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

}
