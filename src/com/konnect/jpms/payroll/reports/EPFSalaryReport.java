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

public class EPFSalaryReport  extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(EPFSalaryReport.class);
	
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
		 
		request.setAttribute(TITLE, TReportEPFSalary);
		request.setAttribute(PAGE, PReportEPFSalary);
		


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
			
		getEPFSalaryReport(uF);
		
		return loadEPFSalaryReport(uF);

	}
	
	
	private void getEPFSalaryReport(UtilityFunctions uF) {
		if(getParamSelection().equals("ORG")){
			viewEPFSalaryReportByOrg(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("WL")){
			viewEPFSalaryReportByLocation(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("DEPART")){
			viewEPFSalaryReportByDepartment(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else if(getParamSelection().equals("SBU")){
			viewEPFSalaryReportByServices(uF);
			if(getExportType()!= null && getExportType().equals("excel")){
				generateExcelReportBy(uF);
			} else if(getExportType()!= null && getExportType().equals("pdf")){
				generatePdfReportBy(uF);
			}
		} else {
			viewEPFSalaryReport(uF);
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
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpPFNumber = (Map) request.getAttribute("hmEmpPFNumber");
			
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");

			Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
			List alEmployees = (List) request.getAttribute("alEmployees");
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
			header.add(new DataStyle("EPF Salary Report for the month of " + strMonthYear, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Sr.No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle(title, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EPF Wages", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EPS Wages", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee PF Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee VPF Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employer PF Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employer EPS Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EDLI", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EDLI Max Limit", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("PF Admin Charges", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EDLI Admin Charges", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

			int count = 0;

			double epfWagesTotal = 0;
			double epsWagesTotal = 0;
			double employeePFAmtTotal = 0;
			double employeeVPFAmtTotal = 0;
			double employerPFAmtTotal = 0;
			double employerEPSAmtTotal = 0;
			double edliTotal = 0;
			double edlimaxlimitTotal = 0;
			double pfAdminChargeTotal = 0;
			double dliAdminChargeTotal = 0;

			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
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

				String epfWages = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EPF_MAX_LIMIT"), "0");
				String epsWages = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EPS_MAX_LIMIT"), "");
				String employeePFAmt = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EEPF_CONTRIBUTION"), "");
				String employeeVPFAmt = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EEVPF_CONTRIBUTION"), "");
				String employerPFAmt = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ERPF_CONTRIBUTION"), "0");
				String employerEPSAmt = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ERPS_CONTRIBUTION"), "0");
				String edli = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ERDLI_CONTRIBUTION"), "");
				String edlimaxlimit = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EDLI_MAX_LIMIT"), "");
				String pfAdminCharge = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EPF_ADMIN_CHARGES"), "0");
				String dliAdminCharge = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EDLI_ADMIN_CHARGES"), "0");

				epfWagesTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EPF_MAX_LIMIT"));
				epsWagesTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EPS_MAX_LIMIT"));
				employeePFAmtTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EEPF_CONTRIBUTION"));
				employeeVPFAmtTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EEVPF_CONTRIBUTION"));
				employerPFAmtTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_ERPF_CONTRIBUTION"));
				employerEPSAmtTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_ERPS_CONTRIBUTION"));
				edliTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_ERDLI_CONTRIBUTION"));
				edlimaxlimitTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EDLI_MAX_LIMIT"));
				pfAdminChargeTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EPF_ADMIN_CHARGES"));
				dliAdminChargeTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EDLI_ADMIN_CHARGES"));

				List<DataStyle> innerList = new ArrayList<DataStyle>();
//				innerList.add(new DataStyle("" + (count + 1), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(strName, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(epfWages, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(epsWages, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(employeePFAmt, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(employeeVPFAmt, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(employerPFAmt, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(employerEPSAmt, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(edli, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(edlimaxlimit, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(pfAdminCharge, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(dliAdminCharge, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));

				reportData.add(innerList);

			}

			if(count > 0){
				List<DataStyle> innerList = new ArrayList<DataStyle>();
//				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Total", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(epfWagesTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(epsWagesTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(employeePFAmtTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(employeeVPFAmtTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(employerPFAmtTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(employerEPSAmtTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(edliTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(edlimaxlimitTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(pfAdminChargeTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoTwoDecimal(dliAdminChargeTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				
				reportData.add(innerList);
			} 
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=EPFSalaryExcelReport.xls");
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
		con = db.makeConnection(con);
		try {

			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			String strMonthYear = (String) request.getAttribute("strMonthYear");

			Map hmEmpName = (Map) request.getAttribute("hmEmpName");
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpPFNumber = (Map) request.getAttribute("hmEmpPFNumber");

			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");

			Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
			List alEmployees = (List) request.getAttribute("alEmployees");
			if (strFinancialYearStart != null && strFinancialYearEnd != null) {
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

			Map<String, String> hmWLocation = CF.getWLocationMap(con, null, null);
			Map<String, String> hmDept = CF.getDeptMap(con);
			Map<String, String> hmLevelMap = (Map<String, String>) request.getAttribute("hmLevelMap");
			Map<String, String> hmServicesMap = (Map<String, String>) request.getAttribute("hmServicesMap");

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>EPF Salary Report for the month of "
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
//					+ "<td width=\"5%\"><font size=\"1\"><b>&nbsp;Sr.No.&nbsp;&nbsp;</b></font></td>"
					+ "<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;"
					+ title
					+ "&nbsp;&nbsp;</b></font></td>"
					+ "<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EPF Wages&nbsp;&nbsp;</b></font></td>"
					+ "<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EPS Wages&nbsp;&nbsp;</b></font></td>"
					+"<td colspan=\"2\"  nowrap=\"nowrap\" align=\"center\">"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td><table><tr><td align=\"center\"  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>Employee</strong></font></td>"
					+ "</tr></table></td></tr><tr><td><table><tr><td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>PF Amount</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>VPF Amount</strong></font></td></tr></table></td></tr></table></td>" +

					"<td colspan=\"2\" align=\"center\">" + "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td><table><tr><td align=\"center\" nowrap=\"nowrap\"><font size=\"1\"><b>Employer</b></font></td>"
					+ "</tr></table></td></tr><tr><td><table><tr><td nowrap=\"nowrap\"><font size=\"1\"><b>PF Amount</b></font></td>"
					+ "<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>EPS Amount</b></font></td></tr></table></td></tr></table></td>"
					+ "<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EDLI&nbsp;</b></font></td>"
					+ "<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EDLI Max Limit&nbsp;&nbsp;</b></font></td>"
					+ "<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;PF Admin Charges&nbsp;&nbsp;</b></font></td>"
					+ "<td nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><b>&nbsp;EDLI Admin Charges&nbsp;&nbsp;</b></font></td></tr>" + "</table>";

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
//					+ "<td width=\"5%\"><font size=\"1\">&nbsp;" + (count + 1)+ "&nbsp;&nbsp;</font></td>" 
					+ "<td nowrap=\"nowrap\"><font size=\"1\">&nbsp;" + strName + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_EPF_MAX_LIMIT")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_EPS_MAX_LIMIT") + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_EEPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_EEVPF_CONTRIBUTION") + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_ERPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_ERPS_CONTRIBUTION") + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_ERDLI_CONTRIBUTION")
						+ "&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_EDLI_MAX_LIMIT")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_EPF_ADMIN_CHARGES") + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_EDLI_ADMIN_CHARGES")
						+ "&nbsp;&nbsp;</font></td></tr>" + "</table>";

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
//						+ "<td width=\"5%\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>" 
						+ "<td><font size=\"1\"><b>&nbsp;Total&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")
						+ "&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")
						+ "&nbsp;&nbsp;</b></font></td></tr>" + "</table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);

			}

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=EPFSalaryReport.pdf");
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
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpPFNumber = (Map) request.getAttribute("hmEmpPFNumber");
			Map hmEmpUanNumber = (Map) request.getAttribute("hmEmpUanNumber");

			Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
			List alEmployees = (List) request.getAttribute("alEmployees");
			if (strFinancialYearStart != null && strFinancialYearEnd != null) {
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Payment Held");

			List<DataStyle> header = new ArrayList<DataStyle>();
			header.add(new DataStyle("EPF Salary Report for the month of " + strMonthYear, Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
//			header.add(new DataStyle("Sr.No.", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("UAN No,", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EPF Acc. No", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EPF Wages", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EPS Wages", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee PF Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employee VPF Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employer PF Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Employer EPS Amount", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EDLI", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EDLI Max Limit", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("PF Admin Charges", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("EDLI Admin Charges", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));

			int count = 0;
			List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();


			double epfWagesTotal = 0;
			double epsWagesTotal = 0;
			double employeePFAmtTotal = 0;
			double employeeVPFAmtTotal = 0;
			double employerPFAmtTotal = 0;
			double employerEPSAmtTotal = 0;
			double edliTotal = 0;
			double edlimaxlimitTotal = 0;
			double pfAdminChargeTotal = 0;
			double dliAdminChargeTotal = 0;

			for (; count < alEmployees.size(); count++) {
				String strEmpId = (String) alEmployees.get(count);

				String empCode = uF.showData((String) hmEmpCode.get(strEmpId), "");
				String empName = uF.showData((String) hmEmpName.get(strEmpId), "");
				String UanNumber = uF.showData((String) hmEmpUanNumber.get(strEmpId), "");
				String epfAccNo = uF.showData((String) hmEmpPFNumber.get(strEmpId), "");
				String epfWages = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EPF_MAX_LIMIT"), "0");
				String epsWages = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EPS_MAX_LIMIT"), "");
				String employeePFAmt = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EEPF_CONTRIBUTION"), "");
				String employeeVPFAmt = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EEVPF_CONTRIBUTION"), "");
				String employerPFAmt = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ERPF_CONTRIBUTION"), "0");
				String employerEPSAmt = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ERPS_CONTRIBUTION"), "0");
				String edli = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_ERDLI_CONTRIBUTION"), "");
				String edlimaxlimit = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EDLI_MAX_LIMIT"), "");
				String pfAdminCharge = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EPF_ADMIN_CHARGES"), "0");
				String dliAdminCharge = uF.showData((String) hmEarningSalaryMap.get(strEmpId + "_EDLI_ADMIN_CHARGES"), "0");

				epfWagesTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EPF_MAX_LIMIT"));
				epsWagesTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EPS_MAX_LIMIT"));
				employeePFAmtTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EEPF_CONTRIBUTION"));
				employeeVPFAmtTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EEVPF_CONTRIBUTION"));
				employerPFAmtTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_ERPF_CONTRIBUTION"));
				employerEPSAmtTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_ERPS_CONTRIBUTION"));
				edliTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_ERDLI_CONTRIBUTION"));
				edlimaxlimitTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EDLI_MAX_LIMIT"));
				pfAdminChargeTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EPF_ADMIN_CHARGES"));
				dliAdminChargeTotal += uF.parseToDouble((String) hmEarningSalaryMap.get(strEmpId + "_EDLI_ADMIN_CHARGES"));

				List<DataStyle> innerList = new ArrayList<DataStyle>();
//				innerList.add(new DataStyle("" + (count + 1), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(empCode, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(empName, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(UanNumber, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(epfAccNo, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(epfWages, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(epsWages, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(employeePFAmt, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(employeeVPFAmt, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(employerPFAmt, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(employerEPSAmt, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(edli, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(edlimaxlimit, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(pfAdminCharge, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(dliAdminCharge, Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));

				reportData.add(innerList);

			}
			if(count > 0){
				List<DataStyle> innerList = new ArrayList<DataStyle>();
//				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE)); 
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle("Total", Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(epfWagesTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(epsWagesTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(employeePFAmtTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(employeeVPFAmtTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(employerPFAmtTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(employerEPSAmtTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(edliTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(edlimaxlimitTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(pfAdminChargeTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0", BaseColor.WHITE));
				innerList.add(new DataStyle(uF.formatIntoOneDecimalWithOutComma(dliAdminChargeTotal), Element.ALIGN_LEFT, "NEW_ROMAN", 10, "0", "0",BaseColor.WHITE));
				
				reportData.add(innerList);
			}
			ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
			sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=EPFSalaryExcelReport.xls");
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
			Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
			Map hmEmpPFNumber = (Map) request.getAttribute("hmEmpPFNumber");
			Map hmEmpUanNumber = (Map) request.getAttribute("hmEmpUanNumber");

			Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
			List alEmployees = (List) request.getAttribute("alEmployees");
			if (strFinancialYearStart != null && strFinancialYearEnd != null) {
				strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
				strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
			}

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			String tbl = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\"><font size=\"2\"><b>EPF Salary Report for the month of "
					+ strMonthYear + "</b></font></td></tr>" + "</table>";
			List<Element> supList = HTMLWorker.parseToList(new StringReader(tbl), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);

			document.add(new Paragraph(" "));

			String tbl1 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>"
//					+ "<td width=\"5%\" nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;Sr.No.&nbsp;&nbsp;</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;Employee Code&nbsp;&nbsp;</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;Employee Name&nbsp;&nbsp;</strong></font></td>"
					+"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;UAN No.&nbsp;&nbsp;</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EPF Acc. No&nbsp;&nbsp;</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EPF Wages&nbsp;&nbsp;</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EPS Wages&nbsp;&nbsp;</strong></font></td>"
					+"<td colspan=\"2\"  nowrap=\"nowrap\" align=\"center\">"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td><table><tr><td align=\"center\"  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>Employee</strong></font></td>"
					+ "</tr></table></td></tr><tr><td><table><tr><td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>PF Amount</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>VPF Amount</strong></font></td></tr></table></td></tr></table></td>"
					+

					"<td colspan=\"2\"  nowrap=\"nowrap\" align=\"center\">"
					+ "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<td><table><tr><td align=\"center\"  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>Employer</strong></font></td>"
					+ "</tr></table></td></tr><tr><td><table><tr><td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>PF Amount</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>EPS Amount</strong></font></td></tr></table></td></tr></table></td>" +

					"<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EDLI&nbsp;</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EDLI Max Limit&nbsp;&nbsp;</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;PF Admin Charges&nbsp;&nbsp;</strong></font></td>"
					+ "<td  nowrap=\"nowrap\" align=\"center\"><font size=\"1\"><strong>&nbsp;EDLI Admin Charges&nbsp;&nbsp;</strong></font></td></tr>"
					+ "</table>";

			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(tbl1), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			int count = 0;
			for (; count < alEmployees.size(); count++) {
				String strEmpId = (String) alEmployees.get(count);
				
				String tbl2 = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"1\"><tr>" 
//					+ "<td width=\"5%\"><font size=\"1\">&nbsp;" + (count + 1)+ "&nbsp;&nbsp;</font></td>" 
					+ "<td><font size=\"1\">&nbsp;" + uF.showData((String) hmEmpCode.get(strEmpId), "")
						+ "&nbsp;&nbsp;</font></td>" + "<td><font size=\"1\" nowrap=\"nowrap\">&nbsp;" + uF.showData((String) hmEmpName.get(strEmpId), "")
						+ "&nbsp;&nbsp;</font></td>" + "<td><font size=\"1\" nowrap=\"nowrap\">&nbsp;" + uF.showData((String) hmEmpUanNumber.get(strEmpId), "")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;" + uF.showData((String) hmEmpPFNumber.get(strEmpId), "")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_EPF_MAX_LIMIT") + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_EPS_MAX_LIMIT")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_EEPF_CONTRIBUTION") + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_EEVPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_ERPF_CONTRIBUTION") + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_ERPS_CONTRIBUTION")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_ERDLI_CONTRIBUTION") + "&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_EDLI_MAX_LIMIT")
						+ "&nbsp;&nbsp;</font></td>" + "<td align=\"right\"><font size=\"1\">&nbsp;"
						+ (String) hmEarningSalaryMap.get(strEmpId + "_EPF_ADMIN_CHARGES") + "&nbsp;&nbsp;</font></td>"
						+ "<td align=\"right\"><font size=\"1\">&nbsp;" + (String) hmEarningSalaryMap.get(strEmpId + "_EDLI_ADMIN_CHARGES")
						+ "&nbsp;&nbsp;</font></td></tr>" + "</table>";

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
//						+ "<td width=\"5%\"><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>"
						+ "<td><font size=\"1\">&nbsp;&nbsp;&nbsp;</font></td>" 
						+ "<td><font size=\"1\"><b>&nbsp;&nbsp;&nbsp;</b></font></td>"
						+ "<td><font size=\"1\"><b>&nbsp;&nbsp;&nbsp;</b></font></td>"
						+ "<td><font size=\"1\"><b>&nbsp;Total&nbsp;</b></font></td>" 
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"+ (String) hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")
						+ "&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")
						+ "&nbsp;&nbsp;</b></font></td>"
						+ "<td align=\"right\"><font size=\"1\"><b>&nbsp;"
						+ (String) hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")
						+ "&nbsp;&nbsp;</b></font></td></tr>" + "</table>";
				List<Element> supList3 = HTMLWorker.parseToList(new StringReader(tbl3), null);
				Phrase phrase3 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
				phrase3.add(supList3.get(0));
				document.add(phrase3);

			}

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=EPFSalaryReport.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void viewEPFSalaryReportByServices(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map hmEmpName = new HashMap();
			Map hmEmpCode = new HashMap();
			Map hmEmpPFNumber = new HashMap();
			Map hmUanNumber = new HashMap();
			
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
			
			pst = con.prepareStatement("select * from employee_personal_details");
			rs = pst.executeQuery();
			while(rs.next()){
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
				hmUanNumber.put(rs.getString("emp_per_id"), rs.getString("uan_no"));
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,pg.service_id,sum(evpf_contribution) as evpf_contribution " +
					"from emp_epf_details eed,payroll_generation pg where eed.emp_id=pg.emp_id and financial_year_start=? and financial_year_end=? and _month=?" +
					" and financial_year_from_date=? and financial_year_to_date=? and month=? and pg.salary_head_id=? ");

			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(" and eed.emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
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
            if(getF_service()!=null && getF_service().length>0){
            	sbQuery.append(" and pg.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
            }
            
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}
			sbQuery.append(" group by pg.service_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(getStrMonth()));
			pst.setInt(7, EMPLOYEE_EPF);
//			System.out.println("pst=======>"+pst);
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
				
				String service_id=rs.getString("service_id");
				
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
			
			pst = con.prepareStatement("SELECT * FROM services order by service_name");
			rs = pst.executeQuery();
			Map<String, String> hmServicesMap = new HashMap<String, String>();
			while (rs.next()) {
				hmServicesMap.put(rs.getString("service_id"), rs.getString("service_name"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strServiceId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+ (count+1));
				alInner.add(uF.showData((String)hmServicesMap.get(strServiceId), ""));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_EPF_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_EPS_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_EEPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_EEVPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_ERPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_ERPS_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_ERDLI_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_EDLI_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_EPF_ADMIN_CHARGES"));
				alInner.add(hmEarningSalaryMap.get(strServiceId+"_EDLI_ADMIN_CHARGES"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
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


//	private void viewEPFSalaryReportByLevel(UtilityFunctions uF) {
//
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//
//		
//		try {
//			
//			Map hmEmpName = new HashMap();
//			Map hmEmpCode = new HashMap();
//			Map hmEmpPFNumber = new HashMap();
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
//			
//			strMonthYear = uF.getDateFormat(strDateStart, DATE_FORMAT, "MMMM - yyyy");	
//			
//			
//			
//			Map<String, String> hmEarningSalaryMap = new HashMap<String, String>();
//			List<String> alEmployees = new ArrayList<String>();
//			
//			con = db.makeConnection(con);
//			
//			pst = con.prepareStatement("select * from employee_personal_details");
//			rs = pst.executeQuery();
//			while(rs.next()){
//				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+" "+rs.getString("emp_lname"));
//				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
//				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
//			}
//			
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
//					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
//					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
//					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,level_id from " +
//					" (select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
//					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
//					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
//					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,eod.grade_id,sum(evpf_contribution) as evpf_contribution " +
//					"from emp_epf_details eed,employee_official_details eod where eed.emp_id=eod.emp_id and financial_year_start=? " +
//					"and financial_year_end=? and _month=? " +
//					" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd " +
//					" where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id)");
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
//				sbQuery.append(" and eod.service_id like '%,"+uF.parseToInt(getF_service())+",%'");
//			}
//			
//			sbQuery.append(" group by eod.grade_id) as a,(select grade_id,ld.level_id from designation_details dd, level_details ld, " +
//					" grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id) as b " +
//					" where a.grade_id=b.grade_id group by level_id");
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(getStrMonth()));
//			
//			rs = pst.executeQuery();
//			
//			double dblEEPFContributionTotal = 0;
//			double dblEEVPFContributionTotal = 0;
//			double dblERPFContributionTotal = 0;
//			double dblERPSContributionTotal = 0;
//			double dblERDLIContributionTotal = 0;
//			double dblEDLIMaxLimitTotal = 0;
//			double dblEPFAdminChargesTotal = 0;
//			double dblEDLIAdminChargesTotal = 0;
//			double dbl_epf_max_limit_Total = 0;
//			double dbl_eps_max_limit_Total = 0;
//			
//			while(rs.next()){
//				
//				dblEEPFContributionTotal += rs.getDouble("eepf_contribution");
//				dblEEVPFContributionTotal += rs.getDouble("evpf_contribution");
//				dblERPFContributionTotal += rs.getDouble("erpf_contribution");
//				dblERPSContributionTotal += rs.getDouble("erps_contribution");
//				dblERDLIContributionTotal += rs.getDouble("erdli_contribution");
//				dblEDLIMaxLimitTotal += rs.getDouble("edli_max_limit");
//				dblEPFAdminChargesTotal += rs.getDouble("pf_admin_charges");
//				dblEDLIAdminChargesTotal += rs.getDouble("edli_admin_charges");
//				dbl_epf_max_limit_Total += rs.getDouble("epf_max_limit");
//				dbl_eps_max_limit_Total += rs.getDouble("eps_max_limit");
//				
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("eps_max_limit")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("evpf_contribution")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erpf_contribution")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("erdli_contribution")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(rs.getDouble("edli_max_limit")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("pf_admin_charges")));
//				hmEarningSalaryMap.put(rs.getString("level_id")+"_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(rs.getDouble("edli_admin_charges")));
//				
//				alEmployees.add(rs.getString("level_id"));
//			}
//			
//			
//			hmEarningSalaryMap.put("Total_EPF_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_epf_max_limit_Total));
//			hmEarningSalaryMap.put("Total_EPS_MAX_LIMIT", uF.formatIntoTwoDecimal(dbl_eps_max_limit_Total));
//			hmEarningSalaryMap.put("Total_EEPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEPFContributionTotal));
//			hmEarningSalaryMap.put("Total_EEVPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblEEVPFContributionTotal));
//			hmEarningSalaryMap.put("Total_ERPF_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPFContributionTotal));
//			hmEarningSalaryMap.put("Total_ERPS_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERPSContributionTotal));
//			hmEarningSalaryMap.put("Total_ERDLI_CONTRIBUTION", uF.formatIntoTwoDecimal(dblERDLIContributionTotal));
//			hmEarningSalaryMap.put("Total_EDLI_MAX_LIMIT", uF.formatIntoTwoDecimal(dblEDLIMaxLimitTotal));
//			hmEarningSalaryMap.put("Total_EPF_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEPFAdminChargesTotal));
//			hmEarningSalaryMap.put("Total_EDLI_ADMIN_CHARGES", uF.formatIntoTwoDecimal(dblEDLIAdminChargesTotal));
//			
//			
//			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
//			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
//			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
//			
//			request.setAttribute("hmEmpName", hmEmpName);
//			request.setAttribute("hmEmpCode", hmEmpCode);
//			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
//			request.setAttribute("strMonthYear", strMonthYear);
//			request.setAttribute("alEmployees", alEmployees);
//						
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
//			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
//			Map<String, String> hmDept =CF.getDeptMap(con); 
//			
//			request.setAttribute("hmWLocation", hmWLocation);
//			request.setAttribute("hmDept", hmDept);
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


	private void viewEPFSalaryReportByDepartment(UtilityFunctions uF) {

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
				
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
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
			
			sbQuery.append(" group by eod.depart_id");
			
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
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strDeptId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+ (count+1));
				alInner.add(uF.showData((String)hmDept.get(strDeptId), ""));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_EPF_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_EPS_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_EEPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_EEVPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_ERPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_ERPS_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_ERDLI_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_EDLI_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_EPF_ADMIN_CHARGES"));
				alInner.add(hmEarningSalaryMap.get(strDeptId+"_EDLI_ADMIN_CHARGES"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
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


	private void viewEPFSalaryReportByLocation(UtilityFunctions uF) {

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
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
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
			
			Map<String, String> hmWLocation =CF.getWLocationMap(con,null, null);
			Map<String, String> hmDept =CF.getDeptMap(con); 
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strLocationId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+ (count+1));
				alInner.add(uF.showData((String)hmWLocation.get(strLocationId), ""));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_EPF_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_EPS_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_EEPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_EEVPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_ERPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_ERPS_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_ERDLI_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_EDLI_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_EPF_ADMIN_CHARGES"));
				alInner.add(hmEarningSalaryMap.get(strLocationId+"_EDLI_ADMIN_CHARGES"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
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


	private void viewEPFSalaryReportByOrg(UtilityFunctions uF) {

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
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(epf_max_limit) as epf_max_limit,sum(eps_max_limit) as eps_max_limit," +
					"sum(eepf_contribution) as eepf_contribution,sum(erpf_contribution) as erpf_contribution," +
					"sum(erps_contribution) as erps_contribution,sum(erdli_contribution) as erdli_contribution,sum(edli_max_limit) as edli_max_limit," +
					"sum(pf_admin_charges) as pf_admin_charges,sum(edli_admin_charges) as edli_admin_charges,org_id,sum(evpf_contribution) as evpf_contribution " +
					"from emp_epf_details eed,employee_official_details eod where eed.emp_id=eod.emp_id and financial_year_start=? " +
					"and financial_year_end=? and _month=? ");

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
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strOrgId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+ (count+1));
				alInner.add(uF.showData((String)hmOrg.get(strOrgId), ""));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_EPF_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_EPS_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_EEPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_EEVPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_ERPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_ERPS_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_ERDLI_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_EDLI_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_EPF_ADMIN_CHARGES"));
				alInner.add(hmEarningSalaryMap.get(strOrgId+"_EDLI_ADMIN_CHARGES"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
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
	
	public String loadEPFSalaryReport(UtilityFunctions uF) {
		
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
	
	public void viewEPFSalaryReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		
		try {
			
			Map hmEmpName = new HashMap();
			Map hmEmpCode = new HashMap();
			Map hmEmpPFNumber = new HashMap();
			Map hmEmpUanNumber = new HashMap();
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
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmpName.put(rs.getString("emp_per_id"), rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
				hmEmpCode.put(rs.getString("emp_per_id"), rs.getString("empcode"));
				hmEmpPFNumber.put(rs.getString("emp_per_id"), rs.getString("emp_pf_no"));	
				hmEmpUanNumber.put(rs.getString("emp_per_id"), rs.getString("uan_no"));	
			}
			rs.close();
			pst.close();
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_epf_details where financial_year_start=?  and financial_year_end=? and _month=? ");

			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
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
            
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrMonth()));
			System.out.println("pst ===>> " + pst);
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
			
			int count=0;
			List<List<String>> reportList = new ArrayList<List<String>>();
			for(; count<alEmployees.size(); count++){
				String strEmpId = (String)alEmployees.get(count);
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(""+ (count+1));
				alInner.add(uF.showData((String)hmEmpCode.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmEmpName.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmEmpUanNumber.get(strEmpId), ""));
				alInner.add(uF.showData((String)hmEmpPFNumber.get(strEmpId), ""));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_EPF_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_EPS_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_EEPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_EEVPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_ERPF_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_ERPS_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_ERDLI_CONTRIBUTION"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_EDLI_MAX_LIMIT"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_EPF_ADMIN_CHARGES"));
				alInner.add(hmEarningSalaryMap.get(strEmpId+"_EDLI_ADMIN_CHARGES"));
				
				reportList.add(alInner);
			}
			if(count > 0){
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("Total");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPS_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EEVPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPF_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERPS_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_ERDLI_CONTRIBUTION")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_MAX_LIMIT")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EPF_ADMIN_CHARGES")+"");
				alInner.add(""+hmEarningSalaryMap.get("Total_EDLI_ADMIN_CHARGES")+"");
				
				reportList.add(alInner);
			}
			
			request.setAttribute("reportList", reportList);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPFNumber", hmEmpPFNumber);
			request.setAttribute("hmEmpUanNumber", hmEmpUanNumber);
			request.setAttribute("strMonthYear", strMonthYear);
			request.setAttribute("alEmployees", alEmployees);
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
//		return SUCCESS;

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
