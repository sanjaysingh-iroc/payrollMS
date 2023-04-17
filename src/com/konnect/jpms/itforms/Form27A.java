package com.konnect.jpms.itforms;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form27A extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	
	String strSubmit; 
	String financialYear;
	String strSelectedEmpId;
	String strMonth;
	String strQuarter;
	
	List<FillFinancialYears> financialYearList; 
	List<FillEmployee> empNamesList;
	List<FillMonth> monthList;
	
	String f_strWLocation;
	String f_level;
	String f_org;
	
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillLevel> levelList;
	
	String formType;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
				
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/itforms/Form27A.jsp");
		request.setAttribute(TITLE, "Form 27A");
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewForm27A(uF);
		
		if(getFormType()!=null && getFormType().equals("pdf")){
			generateForm27APdf(uF);
			return "";
		}

		return loadForm27A(uF);
	}
	
	
	private void generateForm27APdf(UtilityFunctions uF) {
		try{
			
			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
			if(hmEmp == null) hmEmp = new HashMap<String, String>();
			
			Map<String, String> hmStates = (Map<String, String>) request.getAttribute("hmStates");
			if(hmStates == null) hmStates = new HashMap<String, String>();
			Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
			if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
			Map<String, String> hmOtherDetailsMap = (Map<String, String>) request.getAttribute("hmOtherDetailsMap");
			if(hmOtherDetailsMap == null) hmOtherDetailsMap = new HashMap<String, String>();
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.TIMES_ROMAN,10);
			Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN,10,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			String empCount = (String) request.getAttribute("empCount");
			String challanAmt = (String) request.getAttribute("challanAmt");
			String paidAmt = (String) request.getAttribute("paidAmt");
			String paidTDSAmt = (String) request.getAttribute("paidTDSAmt");
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4.rotate());
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);        
	        
	        //New Row
	        PdfPCell row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("FORM NO. 27A",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Form for furnishing information with the statement of deduction / collection of tax at " +
	        		"source ( tick whichever is applicable ) filed on computer media for the period\n(From "+uF.showData(getStrQuarter(), "")+" (dd/mm/yyyy)#",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        PdfPTable innerTable = new PdfPTable(6);
	        innerTable.setWidthPercentage(100);
	        
	        
	        //New Row
	        PdfPCell innerRow =new PdfPCell(new Paragraph("1 (a) Tax Deduction Account No.",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("DEDUCTOR_TAN"), " "),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("(d) Financial Year",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy")+" - "+uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT, "yy"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        
	        //new row
	        innerTable = new PdfPTable(6);
	        innerTable.setWidthPercentage(100);
	        
	        //inner New Row
	        innerRow =new PdfPCell(new Paragraph("   (b) Permanent Account No.",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmOtherDetailsMap.get("DEDUCTOR_PAN"), " "),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("(e) Assessment year",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph((uF.parseToInt(uF.getDateFormat(strFinancialYearStart,DATE_FORMAT, "yyyy"))+1) +" - "+ (uF.parseToInt(uF.getDateFormat(strFinancialYearEnd,DATE_FORMAT, "yy"))+1),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
	        innerTable = new PdfPTable(6);
	        innerTable.setWidthPercentage(100);
	        
	        //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  (c) Form No.",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("24Q",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("(f) Previous receipt number",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("NA",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
	        innerTable = new PdfPTable(6);
	        innerTable.setWidthPercentage(100);
	        
	        //inner New Row
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(3);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("   (In case return/statement has been filed earlier)",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(3);
	        innerTable.addCell(innerRow);
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new Row
	        row1 =new PdfPCell(new Paragraph("2 Particulars of the deductor / collector",small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("3 Name of the person responsible for deduction / collection of tax",small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //new row
	        innerTable = new PdfPTable(3);
	        innerTable.setWidthPercentage(100);
	        
	        //inner New Row
	        innerRow =new PdfPCell(new Paragraph("(a) Name",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), ""),small)); 
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("(b) Type of deductor*",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("COMPANY",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("(c) Branch / division (if any)",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("(d) Address",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small)); 
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Flat No.",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Name of the premises/building",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Road / street / lane",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Area / location",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Town / City / District",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_CITY"), ""),small)); 
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  State",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmStates.get(hmOrg.get("ORG_STATE_ID")), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Pin code",small)); 
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_PINCODE"), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Telephone No.",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_CONTACT"), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  E-mail",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_EMAIL"), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        innerTable = new PdfPTable(3);
	        innerTable.setWidthPercentage(100);
	        
	        //inner New Row
	        innerRow =new PdfPCell(new Paragraph("(a) Name",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_NAME"), ""),small)); 
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("(b) Address",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_ADDRESS"), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Flat No.",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Name of the premises/building",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Road / street / lane",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Area / location",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Town / City / District",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_CITY_ID"), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  State",small)); 
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmStates.get(hmEmp.get("EMP_STATE_ID")), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Pin code",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_PIN_CODE"), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  Telephone No.",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_CONTACT_NO"), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("  E-mail",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_EMAIL"), ""),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerTable.addCell(innerRow);
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("4 Control totals",small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new Row
	        
	        innerTable = new PdfPTable(6);
	        innerTable.setWidthPercentage(100);
	        
	        //inner New Row
	        innerRow =new PdfPCell(new Paragraph("Sr. No.",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("Return Type\n(Regular / Correction type)",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("No. of deductee / party records",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("Amount paid\n(Rs.)",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("Tax deducted / collected\n(Rs.)",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("Tax deposited\n(Total challan amount)\n(Rs.)",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("1",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("REGULAR",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph(uF.showData(empCount, "0"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(paidAmt, "0"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph(uF.showData(paidTDSAmt, "0"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(challanAmt, "0"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph("Total",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph(uF.showData(empCount, "0"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(paidAmt, "0"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	      //inner New Row
	        innerRow =new PdfPCell(new Paragraph(uF.showData(paidTDSAmt, "0"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph(uF.showData(challanAmt, "0"),small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_CENTER);
	        innerRow.setPadding(1.0f);
	        innerTable.addCell(innerRow);
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(5);
	        table.addCell(row1);
	        
	      //new Row
	        row1 =new PdfPCell(new Paragraph("",small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        table.addCell(row1);
	        
	        // new Row
	        row1 =new PdfPCell(new Paragraph("5 Total Number of Annexures enclosed",small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        innerTable = new PdfPTable(3);
	        innerTable.setWidthPercentage(100);	        
	        //inner New Row	                
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(2);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setBorder(Rectangle.NO_BORDER);
	        innerRow.setPadding(1.0f);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        // new Row
	        row1 =new PdfPCell(new Paragraph("6 Other Information",small)); 
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        innerTable = new PdfPTable(3);
	        innerTable.setWidthPercentage(100);	        
	        //inner New Row	                
	        innerRow =new PdfPCell(new Paragraph("",small));
	        innerRow.setHorizontalAlignment(Element.ALIGN_LEFT);
	        innerRow.setPadding(1.0f);
	        innerRow.setColspan(3);
	        innerRow.setFixedHeight(2.0f);
	        innerTable.addCell(innerRow);
	        
	        row1 =new PdfPCell(innerTable); 
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("VERIFICATION",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("I , "+uF.showData(hmEmp.get("EMP_NAME"), "")+" , hereby certify that all the particulars furnished above are correct and complete.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Place: ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);  
	        row1.setPadding(2.5f);  
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Signature of person responsible for deducting / collecting tax at source",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Date: ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT);
	        row1.setPadding(2.5f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Name and designation of person responsible for deducting / collecting tax at source "+uF.showData(hmEmp.get("EMP_NAME"), "")+ " ("+uF.showData(hmEmpCodeDesig.get(hmEmp.get("EMP_ID")), "")+")",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(4);  
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("* Mention type of deductor - Government or Others",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("# dd/mm/yyyy :- date/month/year",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        document.add(table);
	        
	        document.close();
	          
			String filename="Form27A.pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, String> getOtherDetailsMap(Connection con,int empid) {
		Map<String, String> hmOtherDetailsMap = new HashMap<String, String>();

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

//			pst = con.prepareStatement("select wlocation_tan_no,wlocation_pan_no from work_location_info where wlocation_id=" +
//					"(select wlocation_id from employee_official_details where emp_id=?)");
//			pst.setInt(1,empid);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmOtherDetailsMap.put("DEDUCTOR_TAN", rs.getString("wlocation_tan_no"));
//				hmOtherDetailsMap.put("DEDUCTOR_PAN", rs.getString("wlocation_pan_no"));
//
//			}
			
			pst = con.prepareStatement("select org_tan_no,org_pan_no from org_details where org_id in (select org_id from employee_official_details where emp_id=?)");
			pst.setInt(1,empid);
			rs = pst.executeQuery();
			while (rs.next()) { 
				hmOtherDetailsMap.put("DEDUCTOR_TAN", rs.getString("org_tan_no"));
				hmOtherDetailsMap.put("DEDUCTOR_PAN", rs.getString("org_pan_no"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_pan_no from employee_personal_details where emp_per_id=?");
			pst.setInt(1, empid);
			rs = pst.executeQuery();
			while (rs.next()) {
				hmOtherDetailsMap.put("EMPLOYEE_PAN",rs.getString("emp_pan_no"));

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return hmOtherDetailsMap;
	} 


	private List<FillEmployee> getEmployeeList(UtilityFunctions uF) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}			
			
			sbQuery.append(" order by epd.emp_fname");
			
			
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}


	public String loadForm27A(UtilityFunctions uF){
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillQuarterlyMonth();
		
		orgList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		empNamesList=getEmployeeList(uF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

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
		if(getStrMonth()!=null) {
			String strMonth="";
			for(int i=0;monthList!=null && i<monthList.size();i++) {
				if(getStrMonth().equals(monthList.get(i).getMonthId())) {
					strMonth=monthList.get(i).getMonthName();
				}
			}
			if(strMonth!=null && !strMonth.equals("")) {
				hmFilter.put("MONTH", strMonth);
			} else {
				hmFilter.put("MONTH", "-");
			}
		} else {
			hmFilter.put("MONTH", "-");
		}
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
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
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				if(getF_strWLocation().equals(wLocationList.get(i).getwLocationId())) {
					strLocation=wLocationList.get(i).getwLocationName();
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
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
		
		alFilter.add("EMP");
		if(getStrSelectedEmpId()!=null) {
			String strEmpName="";
			for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
				if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
					strEmpName=empNamesList.get(i).getEmployeeCode();
				}
			}
			if(strEmpName!=null && !strEmpName.equals("")) {
				hmFilter.put("EMP", strEmpName);
			} else {
				hmFilter.put("EMP", "Select Employee");
			}
		} else {
			hmFilter.put("EMP", "Select Employee");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewForm27A(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
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
			String months = null;
			if(getStrMonth()!=null){
				if(getStrMonth().equals("1,2,3")){
					months = "1,2,3";
					setStrQuarter("01/01/"+uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy"))+" to 31/03/"+uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy"))+"");
				} else if(getStrMonth().equals("4,5,6")){
					months = "4,5,6";
					setStrQuarter("01/04/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 30/06/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+"");
				} else if(getStrMonth().equals("7,8,9")){
					months = "7,8,9";
					setStrQuarter("01/07/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 31/09/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+"");
				} else {
					months = "10,11,12";
					setStrQuarter("01/10/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 31/12/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+"");
				}
			} else {
				months = "1,2,3";
				setStrQuarter("01/01/"+uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy"))+" to 31/03/"+uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy"))+"");
			}			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String[] tmpMonths = months.split(",");
			
			Map<String, String> hmStates = CF.getStateMap(con);
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);			
			Map<String, String> hmOtherDetailsMap = getOtherDetailsMap(con,uF.parseToInt(getStrSelectedEmpId()));
			String orgId = CF.getEmpOrgId(con, uF, getStrSelectedEmpId());
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(orgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmOrg=new HashMap<String, String>();
			while (rs.next()) {
				hmOrg.put("ORG_ID", rs.getString("org_id"));
				hmOrg.put("ORG_NAME", rs.getString("org_name"));
				hmOrg.put("ORG_LOGO", rs.getString("org_logo"));
				hmOrg.put("ORG_ADDRESS", rs.getString("org_address"));
				hmOrg.put("ORG_PINCODE", rs.getString("org_pincode"));
				hmOrg.put("ORG_CONTACT", rs.getString("org_contact1"));
				hmOrg.put("ORG_EMAIL", rs.getString("org_email"));
				hmOrg.put("ORG_STATE_ID", rs.getString("org_state_id"));
				hmOrg.put("ORG_COUNTRY_ID", rs.getString("org_country_id"));
				hmOrg.put("ORG_CITY", rs.getString("org_city"));
				hmOrg.put("ORG_CODE", rs.getString("org_code"));
				hmOrg.put("ORG_DISPLAY_PAYCYCLE", rs.getString("display_paycycle"));
				hmOrg.put("ORG_DURATION_PAYCYCLE", rs.getString("duration_paycycle"));
				hmOrg.put("ORG_SALARY_CAL_BASIS", rs.getString("salary_cal_basis"));
				hmOrg.put("ORG_START_PAYCYCLE",uF.getDateFormat(rs.getString("start_paycycle"), DBDATE, DATE_FORMAT) );
			}
			rs.close();
			pst.close();  
			
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmEmp = new HashMap<String, String>();
			while(rs.next()){
				hmEmp.put("EMP_ID", rs.getString("emp_per_id"));
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				
				hmEmp.put("EMP_NAME", rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
				hmEmp.put("EMP_ADDRESS", uF.showData(rs.getString("emp_address1"), ""));
				hmEmp.put("EMP_CITY_ID", rs.getString("emp_city_id")); 
				hmEmp.put("EMP_PIN_CODE", rs.getString("emp_pincode")); 
				if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0){
					hmEmp.put("EMP_EMAIL", rs.getString("emp_email_sec"));
				}else{
					hmEmp.put("EMP_EMAIL", rs.getString("emp_email"));
				}
				hmEmp.put("EMP_STATE_ID", rs.getString("emp_state_id_tmp")); 
				hmEmp.put("EMP_CONTACT_NO", rs.getString("emp_contactno")); 
			}
			rs.close();
			pst.close(); 
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
			sbQuery.append(" and (");
            for(int i=0; i<tmpMonths.length; i++){
                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
                
                if(i<tmpMonths.length-1){
                    sbQuery.append(" OR "); 
                }
            }
            sbQuery.append(" ) ");
			sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
					"where eod.emp_id = epd.emp_per_id and org_id=?)");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
			pst.setInt(4, uF.parseToInt(orgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			String empIds = null;
			while(rs.next()){
				if(empIds == null){
					empIds = rs.getString("emp_id");
				} else {
					empIds += ","+rs.getString("emp_id");
				}
			}
			rs.close();
			pst.close(); 
			
//			System.out.println("emp_ids====>"+empIds);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select count(emp_id) as cnt, sum(amount) as amount from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
			sbQuery.append(" and (");
            for(int i=0; i<tmpMonths.length; i++){
                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
                
                if(i<tmpMonths.length-1){
                    sbQuery.append(" OR "); 
                }
            }
            sbQuery.append(" ) ");
			sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
					"where eod.emp_id = epd.emp_per_id and org_id=?)");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, TDS);
			pst.setInt(4, uF.parseToInt(orgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			int empCount = 0;
			String challanAmt = "0";
			while(rs.next()){
				empCount = rs.getInt("cnt");
				challanAmt = rs.getString("amount");
			}
			rs.close();
			pst.close(); 
						
			String paidAmt = "0";
			String paidTDSAmt = "0";
			if(empIds !=null){
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(amount) as amount from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ? " +
						" and salary_head_id not in ("+REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+","+CGST+","+SGST+")");
				sbQuery.append(" and (");
	            for(int i=0; i<tmpMonths.length; i++){
	                sbQuery.append(" month ="+tmpMonths[i]);
	                
	                if(i<tmpMonths.length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
				sbQuery.append(" and is_paid=true and emp_id in ("+empIds+") and salary_head_id in (select salary_head_id from salary_details where earning_deduction = 'E')");
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					paidAmt = rs.getString("amount");
				}
				rs.close();
				pst.close(); 
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(amount) as amount from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ? ");
				sbQuery.append(" and (");
	            for(int i=0; i<tmpMonths.length; i++){
	                sbQuery.append(" month ="+tmpMonths[i]);
	                
	                if(i<tmpMonths.length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
				sbQuery.append(" and is_paid=true and emp_id in ("+empIds+") and salary_head_id =? ");
				
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, TDS);
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					paidTDSAmt = rs.getString("amount");
				}
				rs.close();
				pst.close(); 
			}
//			System.out.println("empCount====>"+empCount);
//			System.out.println("challanAmt====>"+challanAmt);
//			System.out.println("paidAmt====>"+paidAmt);
//			System.out.println("paidTDSAmt====>"+paidTDSAmt);
			
			request.setAttribute("empCount", ""+empCount);
			request.setAttribute("challanAmt", challanAmt);
			request.setAttribute("paidAmt", paidAmt);
			request.setAttribute("paidTDSAmt", paidTDSAmt);
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("hmEmp", hmEmp);
			request.setAttribute("hmStates", hmStates);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("hmOtherDetailsMap", hmOtherDetailsMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}


	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}


	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}


	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}


	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}


	public List<FillMonth> getMonthList() {
		return monthList;
	}


	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}


	public String getStrQuarter() {
		return strQuarter;
	}


	public void setStrQuarter(String strQuarter) {
		this.strQuarter = strQuarter;
	}


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
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


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getFormType() {
		return formType;
	}


	public void setFormType(String formType) {
		this.formType = formType;
	}

	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

}
