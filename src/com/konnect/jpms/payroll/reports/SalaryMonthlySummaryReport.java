package com.konnect.jpms.payroll.reports;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class SalaryMonthlySummaryReport extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(SalaryMonthlySummaryReport.class);
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel; 
	
	String paycycle;
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillPayCycles> paycycleList;
	List<FillOrganisation> orgList; 
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String exportType;
	
	public String execute() throws Exception { 
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, "Monthly Salary Summary");
		request.setAttribute(PAGE, PReportSalarySummaryYearly);
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied); 
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		if(uF.parseToInt(getF_org()) == 0){
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
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

		request.setAttribute("salaryStructure", CF.getStrSalaryStructure());
		int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
		if(nSalaryStrucuterType == S_GRADE_WISE){
			viewSalaryYearlyReportByGrade(uF);
			
			if(getExportType()!= null && getExportType().equalsIgnoreCase("pdf")){
				generateEmpSalaryYearlyPdfReportByGrade(uF);
			} else if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
				generateEmpSalaryYearlyExcelReportByGrade(uF);
			}
		} else {
			viewSalaryYearlyReport(uF);
			
			if(getExportType()!= null && getExportType().equalsIgnoreCase("pdf")){
				generateEmpSalaryYearlyPdfReport(uF);
			} else if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
				generateEmpSalaryYearlyExcelReport(uF);
			}
		}

		return loadSalaryYearlyReport(uF);
	}
	
	private void generateEmpSalaryYearlyExcelReportByGrade(UtilityFunctions uF) {
		try {
			String  strTotalEmp = (String)request.getAttribute("strTotalEmp");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			
			
			double dblE = uF.parseToDouble((String)hmEarningSalaryTotalMap.get("TOTAL"));
			double dblD = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get("TOTAL"));
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Monthly Salary Summary");			
			
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			List<DataStyle> header=new ArrayList<DataStyle>();
			
//			header.add(new DataStyle(uF.showData(org_name, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Grand Summary Details",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Summary Report for the Month & Year : "+uF.showData(strMonth, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Employee: "+uF.showData(strTotalEmp, "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Gross: "+uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Deduction: "+uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Net: "+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblE - dblD)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("Earnings",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("Amount",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			Set setE = hmEarningSalaryMap.keySet();
			Iterator itE = setE.iterator();
			while(itE.hasNext()){
				String strSalaryHeadId = (String)itE.next();
				
				innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)hmEarningSalaryMap.get(strSalaryHeadId),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				reportData.add(innerList);
			}
			
			innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("Total Earnings",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("Deductions",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("Amount",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			Set setD = hmDeductionSalaryMap.keySet();
			Iterator itD = setD.iterator();
			while(itD.hasNext()){
				String strSalaryHeadId = (String)itD.next();
				
				innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)hmDeductionSalaryMap.get(strSalaryHeadId),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				reportData.add(innerList);
			}
			
			innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("Total Deduction",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			

			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			//sheetDesign.generateExcelSheet(workbook,sheet,header,reportData);
		
			sheetDesign.generateExcelSheetforMonthlySalarySummary(workbook,sheet,header,hmEarningSalaryMap,hmDeductionSalaryMap,hmSalaryHeadMap,hmEarningSalaryTotalMap,hmDeductionSalaryTotalMap);
		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=SalaryMonthlySummary.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void generateEmpSalaryYearlyPdfReportByGrade(UtilityFunctions uF) {

		try {

			CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

			String  strTotalEmp = (String)request.getAttribute("strTotalEmp");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			


			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			StringBuilder sb = new StringBuilder();
			
			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td align=\"center\"><font size=\"5\"><b>"+CF.getStrOrgName() + "</b></font></td></tr>");
			sb.append("<tr><td align=\"center\"><font size=\"2\"><b>Monthly Salary Register: "+uF.showData(strMonth, "") + "</b></font></td></tr>" + "</table>");
			List<Element> supList = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			
			double dblE = uF.parseToDouble((String)hmEarningSalaryTotalMap.get("TOTAL"));
			double dblD = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get("TOTAL"));
			
			sb = new StringBuilder();

			sb.append("" +
			"<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"+
			"<tr><td colspan=\"3\" align=\"left\" ><b>Grand Summary Details</b></td>"+
			"<td colspan=\"3\" align=\"right\" ><b>Summary Report for the Month & Year : "+strMonth+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"<tr><td colspan=\"6\" ><b>Total Employee: "+uF.showData(strTotalEmp, "0") +"</b></td></tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"<tr><td colspan=\"2\" ><b>Total Gross: "+uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"<td colspan=\"2\" ><b>Total Deduction: "+uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"<td colspan=\"2\" align=\"right\" ><b>Total Net: "+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblE - dblD)) +"</b></td></tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"</table>"+
					
					"");
			
			
			

			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			
			sb = new StringBuilder();
			
			
			sb.append("<table cellpadding=\"5\" cellspacing=\"5\"><tr><td valign=\"top\">" +
					"<table cellpadding=\"0\" cellspacing=\"0\" >"+
					"<tr><td colspan=\"2\" ></td></tr>"+
			"<tr>"+
				"<td><b>Earnings</b></td>"+
				"<td align=\"right\"><b>Amount</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>");
			
			
			
			
			Set setE = hmEarningSalaryMap.keySet();
			Iterator itE = setE.iterator();
			while(itE.hasNext()){
				String strSalaryHeadId = (String)itE.next();
				
				
				sb.append("" +"<tr>"+
				"<td>"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), "")+"</td>"+
				"<td align=\"right\">"+ (String)hmEarningSalaryMap.get(strSalaryHeadId) +"</td>"+
			"</tr>");
			
				
				
			}
			sb.append(""+
			
			"<tr>"+
				"<td><b>Total Earnings</b></td>"+
				"<td align=\"right\"><b> "+uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>"+
		"</table>"+
					"");
			
			
			
			sb.append("</td><td valign=\"top\">");
			
			
			sb.append("" +
					"<table cellpadding=\"0\" cellspacing=\"0\">"+
					"<tr><td colspan=\"2\" ></td></tr>"+
			"<tr>"+
				"<td><b>Deductions</b></td>"+
				"<td align=\"right\"><b>Amount</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>");
			
			
			
			
			Set setD = hmDeductionSalaryMap.keySet();
			Iterator itD = setD.iterator();
			while(itD.hasNext()){
				String strSalaryHeadId = (String)itD.next();
				
				
				sb.append("" +"<tr>"+
				"<td>"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), "")+"</td>"+
				"<td align=\"right\">"+ (String)hmDeductionSalaryMap.get(strSalaryHeadId) +"</td>"+
			"</tr>");
			
				
				
			}
			sb.append(""+
			
			"<tr>"+
				"<td><b>Total Deduction</b></td>"+
				"<td align=\"right\"><b> "+uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>"+
		"</table>"+
					"");
			
			
			sb.append("</td></tr></table>");
			
			
//			System.out.println("sb===>"+sb.toString());
			
//			
//			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
//			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
//			phrase2.add(supList2.get(0));
//			document.add(phrase2);


			

			
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase6.add(supList6.get(0));
			document.add(phrase6);

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=SalaryYearlySummary.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public String viewSalaryYearlyReportByGrade(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			con = db.makeConnection(con);
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
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
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPayCycleStart = strPayCycleDates[0];
				strPayCycleEnd = strPayCycleDates[1];
				strPC = strPayCycleDates[2];
			}
			
			String strMonth = uF.getDateFormat(strPayCycleStart, DATE_FORMAT, "MM/yyyy");			
			
			String strEmpIds = getEmpPayrollHistory(con,uF,strPayCycleDates);
			System.out.println("EmpId===>"+strEmpIds);
			String strTotalEmp = (String) request.getAttribute("strTotalEmp");
			
			Map<String, String> hmEarningSalaryMap = new LinkedHashMap<String, String>();
			Map<String, String> hmEarningSalaryTotalMap = new HashMap<String, String>();
			Map<String, String> hmDeductionSalaryMap = new LinkedHashMap<String, String>();
			Map<String, String> hmDeductionSalaryTotalMap = new HashMap<String, String>();
			
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(amount) as amount, salary_head_id, earning_deduction from payroll_generation where paycycle=? ");
				sbQuery.append(" and emp_id in ("+strEmpIds+") ");
				sbQuery.append(" and is_paid = true group by salary_head_id, earning_deduction order by earning_deduction desc, salary_head_id ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));		
	//			System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				String strMonthNew = null;
				String strMonthOld = null;
				while(rs.next()){
					
					strMonthNew = rs.getString("salary_head_id");
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
						
						double dblAmt =  uF.parseToDouble((String)hmEarningSalaryMap.get(rs.getString("salary_head_id")));
						dblAmt += rs.getDouble("amount");
						hmEarningSalaryMap.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmt));
						
						
						double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get("TOTAL"));
						dblAmount += rs.getDouble("amount");
						hmEarningSalaryTotalMap.put("TOTAL", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
						
						
					}else if("D".equalsIgnoreCase(rs.getString("earning_deduction"))){
						
						
						
						double dblAmt =  uF.parseToDouble((String)hmDeductionSalaryMap.get(rs.getString("salary_head_id")));
						dblAmt += rs.getDouble("amount");
						hmDeductionSalaryMap.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmt));
						
						
						double dblAmount = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get("TOTAL"));
						dblAmount += rs.getDouble("amount");
						hmDeductionSalaryTotalMap.put("TOTAL", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
						
					}
					
					strMonthOld  = strMonthNew ;
				}
				rs.close();
				pst.close();
			}
		
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmDeductionSalaryMap", hmDeductionSalaryMap);
			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
			request.setAttribute("hmDeductionSalaryTotalMap", hmDeductionSalaryTotalMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strTotalEmp", strTotalEmp);
			
			
			
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
	
	private void generateEmpSalaryYearlyExcelReport(UtilityFunctions uF) {
		try {
			String  strTotalEmp = (String)request.getAttribute("strTotalEmp");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");
			
			
			double dblE = uF.parseToDouble((String)hmEarningSalaryTotalMap.get("TOTAL"));
			double dblD = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get("TOTAL"));
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Monthly Salary Summary");			
			
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			List<DataStyle> header=new ArrayList<DataStyle>();
			
//			header.add(new DataStyle(uF.showData(org_name, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Grand Summary Details",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Summary Report for the Month & Year : "+uF.showData(strMonth, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Employee: "+uF.showData(strTotalEmp, "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Gross: "+uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Deduction: "+uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle("Total Net: "+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblE - dblD)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			List<DataStyle> innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("Earnings",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("Amount",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			Set setE = hmEarningSalaryMap.keySet();
			Iterator itE = setE.iterator();
			while(itE.hasNext()){
				String strSalaryHeadId = (String)itE.next();
				
				innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)hmEarningSalaryMap.get(strSalaryHeadId),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				reportData.add(innerList);
			}
			
			innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("Total Earnings",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("Deductions",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle("Amount",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			
			Set setD = hmDeductionSalaryMap.keySet();
			Iterator itD = setD.iterator();
			while(itD.hasNext()){
				String strSalaryHeadId = (String)itD.next();
				
				innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)hmDeductionSalaryMap.get(strSalaryHeadId),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				reportData.add(innerList);
			}
			
			innerList=new ArrayList<DataStyle>();	
			innerList.add(new DataStyle("Total Deduction",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			innerList.add(new DataStyle(uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			reportData.add(innerList);
			

			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			//sheetDesign.generateExcelSheet(workbook,sheet,header,reportData);
			sheetDesign.generateExcelSheetforMonthlySalarySummary(workbook,sheet,header,hmEarningSalaryMap,hmDeductionSalaryMap,hmSalaryHeadMap,hmEarningSalaryTotalMap,hmDeductionSalaryTotalMap);
		
		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=SalaryMonthlySummary.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public String loadSalaryYearlyReport(UtilityFunctions uF) {
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	public void generateEmpSalaryYearlyPdfReport(UtilityFunctions uF) {

		try {

			CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

			String  strTotalEmp = (String)request.getAttribute("strTotalEmp");
			String  strMonth = (String)request.getAttribute("strMonth");

			Map hmEarningSalaryMap = (Map)request.getAttribute("hmEarningSalaryMap");
			Map hmDeductionSalaryMap = (Map)request.getAttribute("hmDeductionSalaryMap");
			Map hmEarningSalaryTotalMap = (Map)request.getAttribute("hmEarningSalaryTotalMap");
			Map hmDeductionSalaryTotalMap = (Map)request.getAttribute("hmDeductionSalaryTotalMap");
			Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
			Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
			Map hmEmpName = (Map)request.getAttribute("hmEmpName");


			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, buffer);
			document.open();

			StringBuilder sb = new StringBuilder();
			
			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td align=\"center\"><font size=\"5\"><b>"+CF.getStrOrgName() + "</b></font></td></tr>");
			sb.append("<tr><td align=\"center\"><font size=\"2\"><b>Monthly Salary Register: "+uF.showData(strMonth, "") + "</b></font></td></tr>" + "</table>");
			List<Element> supList = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase.add(supList.get(0));
			document.add(phrase);
			
			
			double dblE = uF.parseToDouble((String)hmEarningSalaryTotalMap.get("TOTAL"));
			double dblD = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get("TOTAL"));
			
			sb = new StringBuilder();

			sb.append("" +
			"<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"+
			"<tr><td colspan=\"3\" align=\"left\" ><b>Grand Summary Details</b></td>"+
			"<td colspan=\"3\" align=\"right\" ><b>Summary Report for the Month & Year : "+strMonth+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"<tr><td colspan=\"6\" ><b>Total Employee: "+uF.showData(strTotalEmp, "0") +"</b></td></tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"<tr><td colspan=\"2\" ><b>Total Gross: "+uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"<td colspan=\"2\" ><b>Total Deduction: "+uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"<td colspan=\"2\" align=\"right\" ><b>Total Net: "+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblE - dblD)) +"</b></td></tr>"+
			"<tr><td colspan=\"6\" ></td></tr>"+
			"</table>"+
					
					"");
			
			
			List<Element> supList1 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase1 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase1.add(supList1.get(0));
			document.add(phrase1);

			
			sb = new StringBuilder();
			
			sb.append("<table cellpadding=\"5\" cellspacing=\"5\"><tr><td valign=\"top\">" +
					"<table cellpadding=\"0\" cellspacing=\"0\" >"+
					"<tr><td colspan=\"2\" ></td></tr>"+
			"<tr>"+
				"<td><b>Earnings</b></td>"+
				"<td align=\"right\"><b>Amount</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>");
			
			Set setE = hmEarningSalaryMap.keySet();
			Iterator itE = setE.iterator();
			while(itE.hasNext()){
				String strSalaryHeadId = (String)itE.next();
				sb.append("" +"<tr>"+
					"<td>"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), "")+"</td>"+
					"<td align=\"right\">"+ (String)hmEarningSalaryMap.get(strSalaryHeadId) +"</td>"+
					"</tr>");
			
			}
			sb.append(""+
			
			"<tr>"+
				"<td><b>Total Earnings</b></td>"+
				"<td align=\"right\"><b> "+uF.showData((String)hmEarningSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>"+
		"</table>"+
					"");
			
			
			
			sb.append("</td><td valign=\"top\">");
			
			
			sb.append("" +
					"<table cellpadding=\"0\" cellspacing=\"0\">"+
					"<tr><td colspan=\"2\" ></td></tr>"+
			"<tr>"+
				"<td><b>Deductions</b></td>"+
				"<td align=\"right\"><b>Amount</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>");
			
			
			
			
			Set setD = hmDeductionSalaryMap.keySet();
			Iterator itD = setD.iterator();
			while(itD.hasNext()){
				String strSalaryHeadId = (String)itD.next();
				
				
				sb.append("" +"<tr>"+
				"<td>"+uF.showData((String)hmSalaryHeadMap.get(strSalaryHeadId), "")+"</td>"+
				"<td align=\"right\">"+ (String)hmDeductionSalaryMap.get(strSalaryHeadId) +"</td>"+
			"</tr>");
			
				
				
			}
			sb.append(""+
			
			"<tr>"+
				"<td><b>Total Deduction</b></td>"+
				"<td align=\"right\"><b> "+uF.showData((String)hmDeductionSalaryTotalMap.get("TOTAL"), "0")+"</b></td>"+
			"</tr>"+
			"<tr><td colspan=\"2\" ></td></tr>"+
		"</table>"+
					"");
			
			
			sb.append("</td></tr></table>");
			
			
//			System.out.println("sb===>"+sb.toString());
			
//			
//			List<Element> supList2 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
//			Phrase phrase2 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
//			phrase2.add(supList2.get(0));
//			document.add(phrase2);


			

			
			List<Element> supList6 = HTMLWorker.parseToList(new StringReader(sb.toString()), null);
			Phrase phrase6 = new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 11));
			phrase6.add(supList6.get(0));
			document.add(phrase6);

			document.close();

			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition", "attachment; filename=SalaryYearlySummary.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public String viewSalaryYearlyReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			con = db.makeConnection(con);
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			
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
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPayCycleStart = strPayCycleDates[0];
				strPayCycleEnd = strPayCycleDates[1];
				strPC = strPayCycleDates[2];
				 
			}
			
			String strMonth = uF.getDateFormat(strPayCycleStart, DATE_FORMAT, "MM/yyyy");
			
			
			String strEmpIds = getEmpPayrollHistory(con,uF,strPayCycleDates);
			String strTotalEmp = (String) request.getAttribute("strTotalEmp");
			
			
			Map<String, String> hmEarningSalaryMap = new LinkedHashMap<String, String>();
			Map<String, String> hmEarningSalaryTotalMap = new HashMap<String, String>();
			Map<String, String> hmDeductionSalaryMap = new LinkedHashMap<String, String>();
			Map<String, String> hmDeductionSalaryTotalMap = new HashMap<String, String>();
			
			if(strEmpIds !=null && !strEmpIds.equals("") && strEmpIds.length() > 0){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(amount) as amount, salary_head_id, earning_deduction from payroll_generation where paycycle=? ");
				sbQuery.append(" and emp_id in ("+strEmpIds+") ");
				sbQuery.append(" and is_paid = true group by salary_head_id, earning_deduction order by earning_deduction desc, salary_head_id ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(strPC));		
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				String strMonthNew = null;
				String strMonthOld = null;
				while(rs.next()){
					
					strMonthNew = rs.getString("salary_head_id");
					
					if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
						
						double dblAmt =  uF.parseToDouble((String)hmEarningSalaryMap.get(rs.getString("salary_head_id")));
						dblAmt += rs.getDouble("amount");
						hmEarningSalaryMap.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmt));
						
						
						double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get("TOTAL"));
						dblAmount += rs.getDouble("amount");
						hmEarningSalaryTotalMap.put("TOTAL", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
						
						
					}else if("D".equalsIgnoreCase(rs.getString("earning_deduction"))){
						
						
						
						double dblAmt =  uF.parseToDouble((String)hmDeductionSalaryMap.get(rs.getString("salary_head_id")));
						dblAmt += rs.getDouble("amount");
						hmDeductionSalaryMap.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmt));
						
						
						double dblAmount = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get("TOTAL"));
						dblAmount += rs.getDouble("amount");
						hmDeductionSalaryTotalMap.put("TOTAL", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
						
					}
					
					strMonthOld  = strMonthNew ;
				}
				rs.close();
				pst.close();
			}
		
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmDeductionSalaryMap", hmDeductionSalaryMap);
			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
			request.setAttribute("hmDeductionSalaryTotalMap", hmDeductionSalaryTotalMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strTotalEmp", strTotalEmp);
			
			
			
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
	
	private String getEmpPayrollHistory(Connection con, UtilityFunctions uF,String[] strPayCycleDates) {
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder sbEmp = null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_history where paycycle_from =? and paycycle_to=? and paycycle= ? ");
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
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
	        if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        
	        if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
//			System.out.println("pst====>"+pst);     
			rs = pst.executeQuery();
			Set<String> empSetlist = new HashSet<String>();
			Map<String, Map<String, String>> hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
			}
	//		System.out.println("1 empSetlist====>"+empSetlist.toString());
			
			sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pg.emp_id) as emp_id from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true and paid_from= ? and paid_to=? and paycycle= ?");
			
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
	        
	        if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        
	        if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
	        sbQuery.append(" and pg.emp_id not in (select emp_id from payroll_history where paycycle_from =? and paycycle_to=? and paycycle= ?) ");
			sbQuery.append(" order by pg.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			pst.setDate(4, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strPayCycleDates[2]));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("2 empSetlist====>"+empSetlist.toString());
			
			Iterator<String> it = empSetlist.iterator();
			while(it.hasNext()){
				String strEmp = it.next();
				if(sbEmp == null){
					sbEmp = new StringBuilder();
					sbEmp.append(strEmp);
				} else {
					sbEmp.append(","+strEmp);
				}
			}
			request.setAttribute("strTotalEmp", ""+empSetlist.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		return sbEmp!=null ? sbEmp.toString() : null;
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




	public List<FillServices> getServiceList() {
		return serviceList;
	}




	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}




	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}




	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}


}
