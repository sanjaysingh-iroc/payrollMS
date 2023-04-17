package com.konnect.jpms.payroll.reports;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class TDSReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(TDSReport.class);
	
	String paramSelection;

	String financialYear;
	String strMonth;

	String strLocation;
	String strDepartment;
	String strSbu;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
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
		
		request.setAttribute(TITLE, TTDSRegister);
		request.setAttribute(PAGE, PReportTDS);
		

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
		
		getTDSReport(uF);

		return loadPTaxReport(uF);
	}
	
	
	private void getTDSReport(UtilityFunctions uF) {
		
		if(getParamSelection().equals("ORG")){
			viewTDSReportByOrg(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("WL")){
			viewTDSReportByLocation(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("DEPART")){
			viewTDSReportByDepartment(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("SBU")){
			viewTDSReportByService(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else {
			viewTDSReport(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReport(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generateTDSReport(uF);
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
			String strYear = (String) request.getAttribute("strYear");
			String strMonth = (String) request.getAttribute("strMonth");

			Map hmEmpTDS = (Map) request.getAttribute("hmEmpTDS");
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map) request.getAttribute("hmEmpName");
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");

			if (strFinancialYearStart != null && strFinancialYearEnd != null) {
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

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
			header.add(new DataStyle("Tax Deducted at Source for the month of " + uF.getDateFormat(strMonth, "MM", "MMMM") + " "
					+ uF.getDateFormat(strYear, "yyyy", "yyyy"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Sr.No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle(title, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Gross Salary", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("TDS", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Education Cess", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Standard Cess", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total TDS", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strEmpId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
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
				
				String strGrossSalary=uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0");
				String strTDS=uF.showData((String)hmInner.get("TDS"), "0");
				String strEduCess=uF.showData((String)hmInner.get("EDU_CESS"), "0");
				String strStdCess=uF.showData((String)hmInner.get("STANDARD_CESS"), "0");
				String strTotalTDS=uF.showData((String)hmInner.get("TOTAL_TDS"), "0");
				
				count++;
				List<DataStyle> innerList = new ArrayList<DataStyle>();
//				innerList.add(new DataStyle("" + count, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(strName, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(strGrossSalary, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(strTDS, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(strEduCess, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(strStdCess, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(strTotalTDS, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));

				reportData.add(innerList);
			}
			
			if(count > 0){
				List<DataStyle> innerList = new ArrayList<DataStyle>();
//				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Total", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblGrossAmountTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblTDSTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblEduCessTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblStdCessTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblTDSAmountTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				
				reportData.add(innerList);
			}

			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=TDSExcelReports.xls");
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
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		try {
			
			con = db.makeConnection(con);
			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			String strYear = (String) request.getAttribute("strYear");
			String strMonth = (String) request.getAttribute("strMonth");

			Map hmEmpTDS = (Map) request.getAttribute("hmEmpTDS");
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map) request.getAttribute("hmEmpName");
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");

			if (strFinancialYearStart != null && strFinancialYearEnd != null) {
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

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

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Tax Deducted at Source for the month of "
					+ uF.getDateFormat(strMonth, "MM", "MMMM") + " " + uF.getDateFormat(strYear, "yyyy", "yyyy") + "</b></font></td></tr>" + "</table>";
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
			
			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
//			"<td width=\"5%\"><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>" +
			"<td><font size=\"1\"><b>&nbsp;" + title + "&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;Gross Salary&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;TDS&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;Education Cess&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;Standard Cess&nbsp;&nbsp;</b></font></td>" +
			"<td align=\"right\"><font size=\"1\"><b>&nbsp;Total TDS&nbsp;&nbsp;</b></font></td>" +
			"</tr></table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase5.add(supList4.get(0));
			document.add(phrase5);
			
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strEmpId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				++count;
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
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
				
				String strGrossSalary=uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0");
				String strTDS=uF.showData((String)hmInner.get("TDS"), "0");
				String strEduCess=uF.showData((String)hmInner.get("EDU_CESS"), "0");
				String strStdCess=uF.showData((String)hmInner.get("STANDARD_CESS"), "0");
				String strTotalTDS=uF.showData((String)hmInner.get("TOTAL_TDS"), "0");
				
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" 
//					+"<td><font size=\"1\">&nbsp;"+(count)+"&nbsp;&nbsp;</font></td>" 
					+"<td><font size=\"1\">&nbsp;"+strName+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\" >&nbsp;"+strGrossSalary+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+strTDS+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\" >&nbsp;"+strEduCess+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+strStdCess+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\" >&nbsp;"+strTotalTDS+"&nbsp;&nbsp;</font></td>" +
				"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
		
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
					"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
					"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			
			if(count > 0){
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" 
//					+"<td>&nbsp;</td>" 
					+"<td style=\"text-align:right;\"><font size=\"2\"><strong>Total</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblGrossAmountTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblTDSTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblEduCessTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblStdCessTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblTDSAmountTotal)+"</strong></font></td>" +
				"</tr></table>";
				
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
				
			}else{
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\"><b>&nbsp;No Employees found</b></font></td>" +
				"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=TDSReports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


public void generateExcelReport(UtilityFunctions uF){
		
		try {


			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEmpTDS = (Map)request.getAttribute("hmEmpTDS");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

		
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("Tax Deducted at Source for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM")+" "+uF.getDateFormat(strYear, "yyyy", "yyyy"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Gross Salary",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("TDS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Education Cess",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Standard Cess",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total TDS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strEmpId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				count++;
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
				String empCode=uF.showData((String)hmEmpCode.get(strEmpId), "");
				String empName=uF.showData((String)hmEmpName.get(strEmpId),"");
				String strGrossSalary=uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0");
				String strTDS=uF.showData((String)hmInner.get("TDS"), "0");
				String strEduCess=uF.showData((String)hmInner.get("EDU_CESS"), "0");
				String strStdCess=uF.showData((String)hmInner.get("STANDARD_CESS"), "0");
				String strTotalTDS=uF.showData((String)hmInner.get("TOTAL_TDS"), "0");
				
				List<DataStyle> innerList=new ArrayList<DataStyle>();
//				innerList.add(new DataStyle(""+(count),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(empCode,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(empName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strGrossSalary,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strTDS,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strEduCess,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strStdCess,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strTotalTDS,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportData.add(innerList);
			}
			
			if(count > 0){
				
				
				List<DataStyle> innerList=new ArrayList<DataStyle>();
//				innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle("Total",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblGrossAmountTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblTDSTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblEduCessTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblStdCessTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dblTDSAmountTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportData.add(innerList);
			}
		
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=TDSExcelReports.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	
}
	
	
public void generateTDSReport(UtilityFunctions uF){
		
		try {

			String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
			String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEmpTDS = (Map)request.getAttribute("hmEmpTDS");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");

			if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();
					
			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>Tax Deducted at Source for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM")+" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+"</b></font></td></tr>" +
					"</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			document.add(new Paragraph(" "));
			
			String tbl4 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
			"<td align=\"center\"><font size=\"1\"><b>____________________________________________________________________________________________________________________________________________________________________________</b></font></td>" +
			"</tr></table>";                       
			List<Element> supList4 = HTMLWorker.parseToList(new StringReader(tbl4), null);
			Phrase phrase4 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase4.add(supList4.get(0));
			document.add(phrase4);
			
			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" 
//				+"<td width=\"5%\"><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>" 
				+"<td><font size=\"1\"><b>&nbsp;Employee Code&nbsp;&nbsp;</b></font></td>" +
					"<td><font size=\"1\"><b>&nbsp;Employee Name&nbsp;&nbsp;</b></font></td>" +
					"<td align=\"right\"><font size=\"1\"><b>&nbsp;Gross Salary&nbsp;&nbsp;</b></font></td>" +
					"<td align=\"right\"><font size=\"1\"><b>&nbsp;TDS&nbsp;&nbsp;</b></font></td>" +
					"<td align=\"right\"><font size=\"1\"><b>&nbsp;Education Cess&nbsp;&nbsp;</b></font></td>" +
					"<td align=\"right\"><font size=\"1\"><b>&nbsp;Standard Cess&nbsp;&nbsp;</b></font></td>" +
					"<td align=\"right\"><font size=\"1\"><b>&nbsp;Total TDS&nbsp;&nbsp;</b></font></td>" +
					"</tr></table>";
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);
			
	
			Phrase phrase5 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase5.add(supList4.get(0));
			document.add(phrase5);
			
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strEmpId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				count++;
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
				String empCode=uF.showData((String)hmEmpCode.get(strEmpId), "");
				String empName=uF.showData((String)hmEmpName.get(strEmpId),"");
				String strGrossSalary=uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0");
				String strTDS=uF.showData((String)hmInner.get("TDS"), "0");
				String strEduCess=uF.showData((String)hmInner.get("EDU_CESS"), "0");
				String strStdCess=uF.showData((String)hmInner.get("STANDARD_CESS"), "0");
				String strTotalTDS=uF.showData((String)hmInner.get("TOTAL_TDS"), "0");
				
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" 
//					+"<td><font size=\"1\">&nbsp;"+(count)+"&nbsp;&nbsp;</font></td>" 
					+"<td><font size=\"1\">&nbsp;"+empCode+"&nbsp;&nbsp;</font></td>" +
				"<td><font size=\"1\">&nbsp;"+empName+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\" >&nbsp;"+strGrossSalary+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+strTDS+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\" >&nbsp;"+strEduCess+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\">&nbsp;"+strStdCess+"&nbsp;&nbsp;</font></td>" +
				"<td align=\"right\"><font size=\"1\" >&nbsp;"+strTotalTDS+"&nbsp;&nbsp;</font></td>" +
				"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
		
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
					"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
					"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			
			if(count > 0){
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" 
//					+"<td>&nbsp;</td>" 
					+"<td>&nbsp;</td>" +
				"<td style=\"text-align:right;\"><font size=\"2\"><strong>Total</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblGrossAmountTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblTDSTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblEduCessTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblStdCessTotal)+"</strong></font></td>" +
				"<td align=\"right\"><font size=\"2\"><strong>"+uF.formatIntoTwoDecimal(dblTDSAmountTotal)+"</strong></font></td>" +
				"</tr></table>";
				
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
				
			}else{
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\"><b>&nbsp;No Employees found</b></font></td>" +
				"</tr></table>";
				List<Element> supList2 = HTMLWorker.parseToList(new StringReader(tbl2), null);
				Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase2.add(supList2.get(0));
				document.add(phrase2);
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" +
				"<td align=\"center\"><font size=\"1\">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</font></td>" +
				"</tr></table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);
			}
			
			document.close();
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=TDSReports.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
		
	}catch (Exception e) {
		e.printStackTrace();
	}
	
}


	private void viewTDSReportByService(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pg.service_id,sum(tds_amount) as tds_amount,sum(edu_tax_amount) as edu_tax_amount,sum(std_tax_amount) as std_tax_amount," +
					"sum(actual_tds_amount) as actual_tds_amount from emp_tds_details etd,payroll_generation pg" +
					" where _month=? and etd.emp_id=pg.emp_id and financial_year_start=? and financial_year_end=? ");
			sbQuery.append("and  month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ? and is_paid=true  " +
					"and etd.emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and salary_head_id = ? and is_paid=true  ");

			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append("  and amount > 0) group by pg.service_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
			pst.setInt(8, uF.parseToInt(getStrMonth()));
			pst.setDate(9, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(10, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(11, TDS);
//			System.out.println("pst==>"+pst);
			String strMonth=null;
			String strYear=null;
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpTDS = new HashMap<String, Map<String, String>>();
			while(rs.next()){
				Map<String, String> hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("TDS", rs.getString("tds_amount"));
				hmEmpInner.put("EDU_CESS", rs.getString("edu_tax_amount"));
				hmEmpInner.put("STANDARD_CESS", rs.getString("std_tax_amount"));
				hmEmpInner.put("TOTAL_TDS", rs.getString("actual_tds_amount"));
				
				hmEmpTDS.put(rs.getString("service_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select pg.service_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod" +
					" where earning_deduction = 'E' and pg.emp_id=eod.emp_id and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true  ");
			sbQuery.append(" and pg.emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? " +
					"and salary_head_id = ? and is_paid=true and amount > 0");
			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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

            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and pg.service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append(") group by pg.service_id,month, year");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmEmpInner = (Map<String, String>)hmEmpTDS.get(rs.getString("service_id"));
				if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("service_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM services order by service_name");
			rs = pst.executeQuery();
			Map<String, String> hmServicesMap = new HashMap<String, String>();
			while (rs.next()) {
				hmServicesMap.put(rs.getString("service_id"), rs.getString("service_name"));
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strServiceId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strServiceId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData(hmServicesMap.get(strServiceId), ""));
				alInner.add(uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TDS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("EDU_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("STANDARD_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TOTAL_TDS"), "0"));
				
				reportList.add(alInner);
			}
			
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+uF.formatIntoTwoDecimal(dblGrossAmountTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblTDSTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblEduCessTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblStdCessTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblTDSAmountTotal)+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			request.setAttribute("hmServicesMap", hmServicesMap);
			
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
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			
			} else {
				
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				 
			}
			

			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.depart_id,sum(tds_amount) as tds_amount,sum(edu_tax_amount) as edu_tax_amount,sum(std_tax_amount) as std_tax_amount," +
					"sum(actual_tds_amount) as actual_tds_amount from emp_tds_details etd,employee_official_details eod" +
					" where _month=? and etd.emp_id=eod.emp_id and financial_year_start=? and financial_year_end=? ");
			sbQuery.append(" and etd.emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and salary_head_id = ? and is_paid=true  ");

			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append("  and amount > 0) group by eod.depart_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			String strMonth=null;
			String strYear=null;
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpTDS = new HashMap<String, Map<String, String>>();
			while(rs.next()){
				Map<String, String> hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("TDS", rs.getString("tds_amount"));
				hmEmpInner.put("EDU_CESS", rs.getString("edu_tax_amount"));
				hmEmpInner.put("STANDARD_CESS", rs.getString("std_tax_amount"));
				hmEmpInner.put("TOTAL_TDS", rs.getString("actual_tds_amount"));
				
				hmEmpTDS.put(rs.getString("depart_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.depart_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod" +
					" where earning_deduction = 'E' and pg.emp_id=eod.emp_id and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true  ");
			sbQuery.append(" and pg.emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? " +
					"and salary_head_id = ? and is_paid=true and amount > 0");
			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and pg.service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append(") group by eod.depart_id,month, year");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmEmpInner = (Map<String, String>)hmEmpTDS.get(rs.getString("depart_id"));
				if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("depart_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strDeptId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strDeptId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData(hmDept.get(strDeptId), ""));
				alInner.add(uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TDS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("EDU_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("STANDARD_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TOTAL_TDS"), "0"));
				
				reportList.add(alInner);
			}
			
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+uF.formatIntoTwoDecimal(dblGrossAmountTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblTDSTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblEduCessTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblStdCessTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblTDSAmountTotal)+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
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
			
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			
			} else {
				
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				 
			}
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.wlocation_id,sum(tds_amount) as tds_amount,sum(edu_tax_amount) as edu_tax_amount,sum(std_tax_amount) as std_tax_amount," +
					"sum(actual_tds_amount) as actual_tds_amount from emp_tds_details etd,employee_official_details eod" +
					" where _month=? and etd.emp_id=eod.emp_id and financial_year_start=? and financial_year_end=? ");
			sbQuery.append(" and etd.emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and salary_head_id = ? and is_paid=true  ");

			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append("  and amount > 0) group by eod.wlocation_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			String strMonth=null;
			String strYear=null;
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpTDS = new HashMap<String, Map<String, String>>();
			while(rs.next()){
				Map<String, String> hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("TDS", rs.getString("tds_amount"));
				hmEmpInner.put("EDU_CESS", rs.getString("edu_tax_amount"));
				hmEmpInner.put("STANDARD_CESS", rs.getString("std_tax_amount"));
				hmEmpInner.put("TOTAL_TDS", rs.getString("actual_tds_amount"));
				
				hmEmpTDS.put(rs.getString("wlocation_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.wlocation_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod" +
					" where earning_deduction = 'E' and pg.emp_id=eod.emp_id and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true  ");
			sbQuery.append(" and pg.emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? " +
					"and salary_head_id = ? and is_paid=true and amount > 0");
			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and pg.service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append(") group by eod.wlocation_id,month, year");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmEmpInner = (Map<String, String>)hmEmpTDS.get(rs.getString("wlocation_id"));
				if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("wlocation_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strLocationId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strLocationId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData(hmWLocation.get(strLocationId), ""));
				alInner.add(uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TDS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("EDU_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("STANDARD_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TOTAL_TDS"), "0"));
				
				reportList.add(alInner);
			}
			
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+uF.formatIntoTwoDecimal(dblGrossAmountTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblTDSTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblEduCessTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblStdCessTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblTDSAmountTotal)+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
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


	private void viewTDSReportByOrg(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			
			} else {
				
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				 
			}
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.org_id,sum(tds_amount) as tds_amount,sum(edu_tax_amount) as edu_tax_amount,sum(std_tax_amount) as std_tax_amount," +
					"sum(actual_tds_amount) as actual_tds_amount from emp_tds_details etd,employee_official_details eod" +
					" where _month=? and etd.emp_id=eod.emp_id and financial_year_start=? and financial_year_end=? ");
			sbQuery.append(" and etd.emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and salary_head_id = ? and is_paid=true  ");

			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append("  and amount > 0) group by eod.org_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			String strMonth=null;
			String strYear=null;
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpTDS = new HashMap<String, Map<String, String>>();
			while(rs.next()){
				Map<String, String> hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("TDS", rs.getString("tds_amount"));
				hmEmpInner.put("EDU_CESS", rs.getString("edu_tax_amount"));
				hmEmpInner.put("STANDARD_CESS", rs.getString("std_tax_amount"));
				hmEmpInner.put("TOTAL_TDS", rs.getString("actual_tds_amount"));
				
				hmEmpTDS.put(rs.getString("org_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select eod.org_id, sum(amount) as amount, month, year from payroll_generation pg,employee_official_details eod" +
					" where earning_deduction = 'E' and pg.emp_id=eod.emp_id and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true  ");
			sbQuery.append(" and pg.emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? " +
					"and salary_head_id = ? and is_paid=true and amount > 0");
			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and pg.service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append(") group by eod.org_id,month, year");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmEmpInner = (Map<String, String>)hmEmpTDS.get(rs.getString("org_id"));
				if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("org_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strOrgId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strOrgId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData(hmOrg.get(strOrgId), ""));
				alInner.add(uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TDS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("EDU_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("STANDARD_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TOTAL_TDS"), "0"));
				
				reportList.add(alInner);
			}
			
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("<strong>Total</strong>");
				alInner.add("<strong>"+uF.formatIntoTwoDecimal(dblGrossAmountTotal)+"</strong>");
				alInner.add("<strong>"+uF.formatIntoTwoDecimal(dblTDSTotal)+"</strong>");
				alInner.add("<strong>"+uF.formatIntoTwoDecimal(dblEduCessTotal)+"</strong>");
				alInner.add("<strong>"+uF.formatIntoTwoDecimal(dblStdCessTotal)+"</strong>");
				alInner.add("<strong>"+uF.formatIntoTwoDecimal(dblTDSAmountTotal)+"</strong>");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			request.setAttribute("hmOrg", hmOrg);
			
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
	
	public String viewTDSReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			
			} else {
				
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				 
			}
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,tds_amount,edu_tax_amount,std_tax_amount,actual_tds_amount from emp_tds_details where _month=? and financial_year_start=? and financial_year_end=? ");
			sbQuery.append(" and emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and salary_head_id = ? and is_paid=true  ");

			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			/*if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}*/
			sbQuery.append("  and amount > 0) order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			String strMonth=null;
			String strYear=null;
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpTDS = new HashMap<String, Map<String, String>>();
			while(rs.next()){
				Map<String, String> hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("TDS", rs.getString("tds_amount"));
				hmEmpInner.put("EDU_CESS", rs.getString("edu_tax_amount"));
				hmEmpInner.put("STANDARD_CESS", rs.getString("std_tax_amount"));
				hmEmpInner.put("TOTAL_TDS", rs.getString("actual_tds_amount"));
				
				hmEmpTDS.put(rs.getString("emp_id"), hmEmpInner);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id, sum(amount) as amount, month, year from payroll_generation where earning_deduction = 'E' and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=true  ");

			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append(" and amount > 0");
			sbQuery.append(" and emp_id in (select emp_id from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? " +
					"and salary_head_id = ? and is_paid=true and amount > 0");
			if((getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
//            if(getF_level()!=null && getF_level().length>0){
//                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//            }
            
            if((getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}

			if(getF_service()!=null && getF_service().length>0){
				sbQuery.append(" and service_id  in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			sbQuery.append(") group by emp_id,month, year");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(getStrMonth()));
			pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(7, TDS);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String, String> hmEmpInner = (Map<String, String>)hmEmpTDS.get(rs.getString("emp_id"));
				if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpTDS.put(rs.getString("emp_id"), hmEmpInner);
				
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpTDS.keySet().iterator();
			int count=0;
			double dblGrossAmountTotal = 0;
			double dblTDSTotal = 0;
			double dblEduCessTotal = 0;
			double dblStdCessTotal = 0;
			double dblTDSAmountTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmEmpTDS.get(strEmpId);
				if(hmInner == null) hmInner = new HashMap<String, String>();
				
				dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
				dblTDSTotal+=uF.parseToDouble((String)hmInner.get("TDS"));
				dblEduCessTotal+=uF.parseToDouble((String)hmInner.get("EDU_CESS"));
				dblStdCessTotal+=uF.parseToDouble((String)hmInner.get("STANDARD_CESS"));
				dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TOTAL_TDS"));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData((String)hmEmpCode.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmEmpName.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TDS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("EDU_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("STANDARD_CESS"), "0"));
				alInner.add(uF.showData((String)hmInner.get("TOTAL_TDS"), "0"));
				
				reportList.add(alInner);
			}
			
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+uF.formatIntoTwoDecimal(dblGrossAmountTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblTDSTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblEduCessTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblStdCessTotal)+"");
				alInner.add(""+uF.formatIntoTwoDecimal(dblTDSAmountTotal)+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpTDS", hmEmpTDS);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
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
