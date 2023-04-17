package com.konnect.jpms.payroll.reports;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.konnect.jpms.select.FillEmploymentType;
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

public class PaymentHeldReport extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(PaymentHeldReport.class);
	
	String paramSelection;

	String financialYear;
	String strMonth;

	String strLocation;
	String strDepartment;
	String strSbu;
	String strEmployeType;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	String[] f_employeType;
	
	List<FillOrganisation> orgList;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	List<FillServices> serviceList;
	List<FillEmploymentType> employementTypeList;
	
	String exportType;
	
	public String execute() throws Exception { 
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		 
		request.setAttribute(TITLE, TPaymentHeld);
		request.setAttribute(PAGE, PPaymentHeld);
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
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
		if(getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		if(getStrMonth()==null){
			setStrMonth("1");
		}
		if(getParamSelection() == null){
			setParamSelection("EMP");
		}
		
		return getPaymentHeldReport(uF);

	}
	
	private String getPaymentHeldReport(UtilityFunctions uF) {
//		System.out.println("getParamSelection=="+getParamSelection());
		if(getParamSelection().equals("ORG")){
			viewPaymentHeldReportByOrg(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("WL")){
			viewPaymentHeldReportByLocation(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("DEPART")){
			viewPaymentHeldReportByDepartment(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("SBU")){
			viewPaymentHeldReportByService(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else {
			viewPaymentHeldReport(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReport(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReport(uF);
			}
		}
		
		return loadPaymentHeldReport(uF);
		
	}
	
	private void generatePdfReportBy(UtilityFunctions uF) {
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
//					+ "<td width=\"10%\"><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>"
					+ "<td width=\"70%\" align=\"center\"><font size=\"1\"><b>&nbsp;"+title+"&nbsp;&nbsp;</b></font></td>"
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
				count++;
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
//						+ "<td><font size=\"1\"><b>&nbsp;"+ (count)+ "&nbsp;&nbsp;</b></font></td>"						
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
				String tbl3 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>" 
//						+"<td width=\"10%\"><font size=\"1\">&nbsp;</font></td>"
						+ "<td width=\"70%\" align=\"right\"><font size=\"1\"><strong>Total</strong></font></td>"
						+ "<td align=\"right\" width=\"20%\"><font size=\"1\"><strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetPayTotal)+"</strong></font></td>" +
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
		} finally {
			db.closeConnection(con);
		}

	}
	
	public void generatePdfReport(UtilityFunctions uF) {

		try {

			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			String strYear = (String) request.getAttribute("strYear");
			String strMonth = (String) request.getAttribute("strMonth");

			Map hmEmpPTax = (Map) request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map) request.getAttribute("hmEmpName");
			Map hmEmpPanNo = (Map) request.getAttribute("hmEmpPanNo");

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
					+ uF.getDateFormat(strMonth, "MM", "MMMM")+ " "+ uF.getDateFormat(strYear, "yyyy", "yyyy")+ "</b></font></td></tr></table>";
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
//					+ "<td width=\"10%\"><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"center\"><font size=\"1\"><b>&nbsp;Employee Code&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;Employee Name&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;Pan No&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;Organization&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;Department&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;Level&nbsp;&nbsp;</b></font></td>"
					+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;Net Pay&nbsp;&nbsp;</b></font></td></tr>"
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
				count++;
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
//						+ "<td><font size=\"1\"><b>&nbsp;"+ (count)+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"center\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEmpCode.get(strEmpId), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEmpName.get(strEmpId), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String) hmEmpPanNo.get(strEmpId), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;"+ uF.showData(hmEmpOrgName.get(strEmpId), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String)hmDeptMap.get((String)hmEmpDept.get(strEmpId)), "")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"left\"><font size=\"1\"><b>&nbsp;"+ uF.showData((String)hmLevelMap1.get((String)hmEmpLevelMap.get(strEmpId)), "")+ "&nbsp;&nbsp;</b></font></td>"
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
	
	public void generateExcelReport(UtilityFunctions uF) {
		try{

			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEmpPTax = (Map)request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpPanNo = (Map)request.getAttribute("hmEmpPanNo");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			Map hmEmpLevelMap = (Map)request.getAttribute("hmEmpLevelMap");
			Map hmLevelMap1 = (Map)request.getAttribute("hmLevelMap1");
			Map hmEmpDept = (Map)request.getAttribute("hmEmpDept");
			Map hmDeptMap = (Map)request.getAttribute("hmDeptMap");
			
			Map<String, String> hmEmpOrgName =(Map<String, String>)request.getAttribute("hmEmpOrgName");
			if(hmEmpOrgName == null) hmEmpOrgName = new HashMap<String, String>(); 

			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("Salary held statement for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM")+" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+" ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Pan No",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Organization",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Level",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Net Pay",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
			
			
			Set set = hmEmpPTax.keySet();
			Iterator it = set.iterator();
			int count=0;
			double dblNetPayTotal = 0;
			
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				
				dblNetPayTotal+=uF.parseToDouble((String)hmEmpPTax.get(strEmpId));
				 
				count++;
				
				List<DataStyle> innerList=new ArrayList<DataStyle>();				
//				innerList.add(new DataStyle(""+count,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData((String)hmEmpCode.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData((String)hmEmpName.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData((String)hmEmpPanNo.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData(hmEmpOrgName.get(strEmpId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData((String)hmDeptMap.get((String)hmEmpDept.get(strEmpId)), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData((String)hmLevelMap1.get((String)hmEmpLevelMap.get(strEmpId)), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData((String)hmEmpPTax.get(strEmpId),"0.00"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportData.add(innerList);
			}         
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();
//			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("Total",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetPayTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			reportData.add(innerList);
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
				
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=PaymentHeldReports.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateExcelReportBy(UtilityFunctions uF) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			
			
			con = db.makeConnection(con);


			String  strYear = (String)request.getAttribute("strYear");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEmpPTax = (Map)request.getAttribute("hmEmpPTax");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			
			Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con);
			Map<String, String> hmLevelMap =(Map<String, String>)request.getAttribute("hmLevelMap");
			Map<String, String> hmServicesMap =(Map<String, String>)request.getAttribute("hmServicesMap");
			
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
			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			header.add(new DataStyle("Salary held statement for the month of "+uF.getDateFormat(strMonth, "MM", "MMMM")+" "+uF.getDateFormat(strYear, "yyyy", "yyyy")+" ",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle(title,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Net Pay",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
			
			
			Set set = hmEmpPTax.keySet();
			Iterator it = set.iterator();
			int count=0;
			double dblNetPayTotal = 0;
			
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				
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
				
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				count++;
//				innerList.add(new DataStyle(""+count,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(strName,Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.showData((String)hmEmpPTax.get(strEmpId),"0.00"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				
				reportData.add(innerList);
			}
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();
//			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("Total",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetPayTotal),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook,sheet,header,reportData);
				
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=PaymentHeldReports.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void viewPaymentHeldReportByService(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
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
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,pg.service_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='D' and pg.service_id>0  ");
			
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
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
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
				hmEmpDeduction.put(rs.getString("service_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(amount)as amount,month,year,pg.service_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='E' and pg.service_id>0  ");
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
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
				hmEmpPTax.put(rs.getString("service_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
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
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
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
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpPTax.keySet().iterator();
			int count=0;
			double dblNetPayTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				dblNetPayTotal+=uF.parseToDouble((String)hmEmpPTax.get(strEmpId));
				
				String strName =uF.showData((String)hmServicesMap.get(strEmpId), "");
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData(strName,""));
				alInner.add(uF.showData((String)hmEmpPTax.get(strEmpId), "0"));
				
				reportList.add(alInner);
			}
			if(count>0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetPayTotal));
			}
			request.setAttribute("reportList", reportList);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			
			con = db.makeConnection(con);
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
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
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,level_id from (select sum(amount)as amount,month,year,eod.grade_id " +
					" from employee_personal_details epd, employee_official_details eod,payroll_generation pg where pg.emp_id = eod.emp_id " +
					"and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id and month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and is_paid=false and earning_deduction='D' and eod.grade_id in (select grade_id from " +
					"designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");
			
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
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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

				hmEmpDeduction.put(rs.getString("level_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
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
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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

				hmEmpPTax.put(rs.getString("level_id"),uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
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
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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

				hmEmpDeduction.put(rs.getString("depart_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.depart_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='E' ");
			
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
			
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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

				hmEmpPTax.put(rs.getString("depart_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
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
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpPTax.keySet().iterator();
			int count=0;
			double dblNetPayTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				dblNetPayTotal+=uF.parseToDouble((String)hmEmpPTax.get(strEmpId));
				
				String strName =uF.showData((String)hmDept.get(strEmpId), "");
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData(strName,""));
				alInner.add(uF.showData((String)hmEmpPTax.get(strEmpId), "0"));
				
				reportList.add(alInner);
			}
			if(count>0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetPayTotal));
			}
			request.setAttribute("reportList", reportList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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

				hmEmpDeduction.put(rs.getString("wlocation_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.wlocation_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='E' ");
			
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
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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

				hmEmpPTax.put(rs.getString("wlocation_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
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
			
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpPTax.keySet().iterator();
			int count=0;
			double dblNetPayTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				dblNetPayTotal+=uF.parseToDouble((String)hmEmpPTax.get(strEmpId));
				
				String strName =uF.showData((String)hmWLocation.get(strEmpId), "");
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData(strName,""));
				alInner.add(uF.showData((String)hmEmpPTax.get(strEmpId), "0"));
				
				reportList.add(alInner);
			}
			if(count>0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetPayTotal));
			}
			request.setAttribute("reportList", reportList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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

				hmEmpDeduction.put(rs.getString("org_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(amount)as amount,month,year,eod.org_id from employee_personal_details epd, employee_official_details eod, " +
					" payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id " +
					" and month=? and financial_year_from_date=? and financial_year_to_date=? and is_paid=false and earning_deduction='E' ");
			
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
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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

				hmEmpPTax.put(rs.getString("org_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
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
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmDept", hmDept);
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpPTax.keySet().iterator();
			int count=0;
			double dblNetPayTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				dblNetPayTotal+=uF.parseToDouble((String)hmEmpPTax.get(strEmpId));
				
				String strName =uF.showData((String)hmOrg.get(strEmpId), "");
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData(strName,""));
				alInner.add(uF.showData((String)hmEmpPTax.get(strEmpId), "0"));
				
				reportList.add(alInner);
			}
			if(count>0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetPayTotal));
			}
			request.setAttribute("reportList", reportList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}


	
public String loadPaymentHeldReport(UtilityFunctions uF) {
		
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
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);	
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
//	public String loadPaymentHeldReport(UtilityFunctions uF) {
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
//		getSelectedFilter(uF);
//		
//		return LOAD;
//	}
	
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
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
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
			Map<String, String> hmEmpPanNo =CF.getEmpPANNoMap(con);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap1 = CF.getLevelMap(con);
			Map<String, String> hmEmpDept = CF.getEmpDepartmentMap(con);
			Map<String, String> hmDeptMap = CF.getDeptMap(con);
			
						
			Map<String, String> hmOrg =  CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select earning_deduction, pg.emp_id, month, year, amount,eod.org_id from employee_personal_details epd, employee_official_details eod, payroll_generation pg where pg.emp_id = eod.emp_id and pg.emp_id = epd.emp_per_id and eod.emp_id = epd.emp_per_id");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null && !((String)session.getAttribute(ORG_ACCESS)).equals("")){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
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
			Map hmEmpPTax = new LinkedHashMap();
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

				hmEmpPTax.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				hmEmpOrgName.put(rs.getString("emp_id"), uF.showData(hmOrg.get(rs.getString("org_id")), ""));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Iterator<String> it = hmEmpPTax.keySet().iterator();
			int count=0;
			double dblNetPayTotal = 0;
			while(it.hasNext()){
				String strEmpId = (String)it.next();
				dblNetPayTotal+=uF.parseToDouble((String)hmEmpPTax.get(strEmpId));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+(++count));
				alInner.add(uF.showData((String)hmEmpCode.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmEmpName.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmEmpPanNo.get(strEmpId), ""));
				alInner.add(uF.showData(hmEmpOrgName.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmDeptMap.get((String)hmEmpDept.get(strEmpId)), ""));
				alInner.add(uF.showData((String)hmLevelMap1.get((String)hmEmpLevelMap.get(strEmpId)), ""));
				alInner.add(uF.showData((String)hmEmpPTax.get(strEmpId), "0"));
				
				reportList.add(alInner);
			}
			if(count>0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("Total");
				alInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNetPayTotal));
			}
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("hmEmpOrgName", hmEmpOrgName);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpPTax", hmEmpPTax);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPanNo", hmEmpPanNo);
			request.setAttribute("hmEmpLevelMap", hmEmpLevelMap);
			request.setAttribute("hmLevelMap1", hmLevelMap1);
			request.setAttribute("hmEmpDept", hmEmpDept);
			request.setAttribute("hmDeptMap", hmDeptMap);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
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
	
	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

}
