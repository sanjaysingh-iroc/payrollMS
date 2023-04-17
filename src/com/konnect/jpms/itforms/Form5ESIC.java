package com.konnect.jpms.itforms;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form5ESIC extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

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
	String strMonth;
	 
	String f_org;
	List<FillOrganisation> orgList;
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	
	List<FillEmployee> empNamesList;
	
	String formType;
	String strHalfYear;

	String strSelectedEmpId;
	String yearType;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/itforms/Form5ESIC.jsp");
		request.setAttribute(TITLE, "Form 5 ESIC");
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		
		if(getStrMonth()==null){
			setStrMonth("1");
		}
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewForm5ESIC();
		
		if(getFormType()!=null && getFormType().equals("pdf")){
			generateFormForm5ESICPdf();
			return "";
		}
		
		return loadForm5ESIC();

	}
	
	
	private void generateFormForm5ESICPdf() {
		try{
			UtilityFunctions uF = new UtilityFunctions();
			
			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
			if(hmEmp == null) hmEmp = new HashMap<String, String>();
			
			Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
			if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
			Map<String, String> hmMonthChallan = (Map<String, String>) request.getAttribute("hmMonthChallan");
			if(hmMonthChallan == null) hmMonthChallan = new LinkedHashMap<String, String>();
			Map<String, String> hmMonthChallanDate = (Map<String, String>) request.getAttribute("hmMonthChallanDate");
			if(hmMonthChallanDate == null) hmMonthChallanDate = new LinkedHashMap<String, String>();
			Map<String, String> hmContribution = (Map<String, String>) request.getAttribute("hmContribution");
			if(hmContribution == null) hmContribution = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
			if(hmEmpName == null) hmEmpName = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpContribution = (Map<String, String>) request.getAttribute("hmEmpContribution");
			if(hmEmpContribution == null) hmEmpContribution = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpWages = (Map<String, String>) request.getAttribute("hmEmpWages");
			if(hmEmpWages == null) hmEmpWages = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpPaidDays = (Map<String, String>) request.getAttribute("hmEmpPaidDays");
			if(hmEmpPaidDays == null) hmEmpPaidDays = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpInsuranceNo = (Map<String, String>) request.getAttribute("hmEmpInsuranceNo");
			if(hmEmpInsuranceNo == null) hmEmpInsuranceNo = new LinkedHashMap<String, String>();
			Map<String, Map<String, String>> hmESIC = (Map<String, Map<String, String>>) request.getAttribute("hmESIC");
			if(hmESIC==null) hmESIC = new HashMap<String, Map<String, String>>();
			
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.TIMES_ROMAN,10);
			Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN,10,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);        
	        
	        //New Row
	        PdfPCell row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("FORM 5",smallBold));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("RETURN OF CONTRIBUTIONS\nEMPLOYEES' STATE INSURANCE CORPORATION",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("(Regulation 26)",italicEffect));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("Name of Branch Office ",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Employer's Code No.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setNoWrap(true);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_ESTABLISH_CODE_NO"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setNoWrap(true);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("Name and Address of the factory or establishment :",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Particulars of the Principal employer(s)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("(a) Name :",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        row1.setIndent(15.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("(b) Designation :",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        row1.setIndent(15.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmEmpCodeDesig.get(hmEmp.get("EMP_ID")), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("(c) Residential Address",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        row1.setIndent(15.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph(uF.showData(hmEmp.get("EMP_ADDRESS"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(4);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Contribution Period from "+uF.showData(getStrHalfYear(), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("                  I furnish below the details of the Employer's and Employee's share of contribution " +
	        		"in respect of the under mentioned insured persons. I hereby declare that the return includes each and every employee, " +
	        		"employed directly or through an immediate employer or in connection with the work of the factory / establishment or " +
	        		"any work...............................connected with the administration of the factory / establishment or purchase " +
	        		"of raw materials, sale or distribution of finished products etc. to whom the ESI Act, 1948 applies, in the contribution " +
	        		"period to which this return relates and that the contributions in respect of employer's and employee's share have been " +
	        		"correctly paid in accordance with the provisions of the Act and Regulations.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        double dblEmpShare = uF.parseToDouble(hmContribution.get(""+EMPLOYEE_ESI)); 
	        double dblERShare = uF.parseToDouble(hmContribution.get(""+EMPLOYER_ESI));
	        double dblTotalShare = dblEmpShare + dblERShare;
	        //new row
	        row1 =new PdfPCell(new Paragraph("Employees's Share "+dblEmpShare,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER); 
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Employer's Share  "+dblERShare,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("Total Contribution "+dblTotalShare,small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Details of Challans : -",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Sl.No.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Month",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Date of Challan",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Amount",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Name of the Bank and Branch",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(2);
	        table.addCell(row1);
	        int j = 10;
	        if(getYearType()!=null && getYearType().equals("1")){
	        	j = 4;
	        }
	        
			for (int i = 1; i <= 6; i++,j++) {
				if(j==13){
					j=1;
				}
				Map<String, String> hmESICInner = (Map<String, String>) hmESIC.get(""+j);
				if(hmESICInner==null)hmESICInner = new HashMap<String, String>();
				
		        //new row
		        row1 =new PdfPCell(new Paragraph(""+i,small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        		        
		        row1 =new PdfPCell(new Paragraph((hmMonthChallan.get(""+j)!=null ? uF.getMonth(j) : ""),small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(uF.showData(hmMonthChallanDate.get(""+j), ""),small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(uF.showData(hmMonthChallan.get(""+j), ""),small));
		        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(uF.showData(hmESICInner.get("CHALLAN_BANK_NAME"),"")+", "+uF.showData(hmESICInner.get("CHALLAN_BRANCH_NAME"),""),small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        row1.setColspan(2);
		        table.addCell(row1);
	        }
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("Place : .........................",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Total amount paid : Rs. "+dblTotalShare,small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //new row
	        row1 =new PdfPCell(new Paragraph("Date : .........................",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //new row
	        row1 =new PdfPCell(new Paragraph("\n\nSignature and Designation of the Employer\n(with Rubber Stamp)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(10.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        	        
	        document.add(table);
	        
	        document.newPage();
	        
	        PdfPTable table1 = new PdfPTable(10);
			table1.setWidthPercentage(100);        
	        
			//new row
	        PdfPCell row2 =new PdfPCell(new Paragraph("",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("Important Instructions : Information to be given in 'Remarks Column (No. 9)\"",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        row2.setIndent(15.0f);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("(i) If any I.P. is appointed for the first time and / or leaves during the " +
	        		"contribution period indicate \"A ............... (date)\" and / or \"L .................. (date)\"",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        row2.setIndent(10.0f);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("(ii) Please indicate Insurance Nos. in ascending order.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        row2.setIndent(10.0f);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("(iii) Figures in Columns 4,5 & 6 shall be in respect of wage periods ended during the contribution period.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        row2.setIndent(10.0f);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("(iv) Invariably strike totals of Columns 4, 5 and 6 of the Return.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        row2.setIndent(10.0f);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("(v) No overwriting shall be made. Any corrections, if made, should be signed by the employer.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        row2.setIndent(10.0f);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("(vi) Every page of this Return should bear full signature and rubber stamp of the employer.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        row2.setIndent(10.0f);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("(vii) Daily wages in Column 7 of the return shall be calculated by dividing figures in Column 5 by figures in Column 4 to two decimal places.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        row2.setIndent(10.0f);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("For *CP ending 31st March, due date is 12th May",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("For CP ending 30th September, due date is 11th November.",small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        table1.addCell(row2);
	        
	        //new row
	        row2 =new PdfPCell(new Paragraph("EMPLOYEE'S STATE INSURANCE CORPORATION",small));
	        row2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("Employer's Name and Address \n"+uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setColspan(10);
	        table1.addCell(row2);
	        
	      //new row
	        row2 =new PdfPCell(new Paragraph("Employer's Code No. ...............................................Period from "+uF.showData(getStrHalfYear(), ""),small));
	        row2.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row2.setBorder(Rectangle.NO_BORDER);
	        row2.setPadding(2.5f);
	        row2.setPaddingTop(8.0f);
	        row2.setPaddingBottom(8.0f);
	        row2.setColspan(10);
	        table1.addCell(row2);
	        
	        document.add(table1);
	        
	        PdfPTable table2 = new PdfPTable(10);
			table2.setWidthPercentage(100);
	        //new row
			PdfPCell row3 =new PdfPCell(new Paragraph("Sl.No.",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("Insurance Number",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("Name of Insured Person",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        row3.setColspan(2);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("No. of days for which wages paid",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("Total amount of wages paid (Rs.)",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("Employee's contribution deducted (Rs.)",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("Average Daily Wages (Rs.)",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("Whether still continues working",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("Remarks",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	      //new row
	        row3 =new PdfPCell(new Paragraph("1",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("2",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("3",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        row3.setColspan(2);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("4",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("5",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("6",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("7",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("8",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("9",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        Iterator<String> it = hmEmpName.keySet().iterator();
	        int i = 0;
	        double dblTotalDays = 0.0d;
	        double dblTotalWages = 0.0d;
	        double dblTotalContribution = 0.0d;
	        double dblTotalAvg = 0.0d;
	        while(it.hasNext()){
	        	String strEmpId = it.next();
	        	String strEmpName = hmEmpName.get(strEmpId);
	        	i++;
	        	 //new row
		        row3 =new PdfPCell(new Paragraph(""+i,small));
		        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        table2.addCell(row3);
		        
		        row3 =new PdfPCell(new Paragraph(""+uF.showData(hmEmpInsuranceNo.get(strEmpId), ""),small)); 
		        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        table2.addCell(row3);
		        
		        row3 =new PdfPCell(new Paragraph(strEmpName,small));
		        row3.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        row3.setColspan(2);
		        table2.addCell(row3);
		        
		        dblTotalDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId));
		        row3 =new PdfPCell(new Paragraph(uF.showData(hmEmpPaidDays.get(strEmpId), "0"),small));
		        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        table2.addCell(row3);
		        
		        dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId));
		        row3 =new PdfPCell(new Paragraph(uF.showData(hmEmpWages.get(strEmpId), "0"),small));
		        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        table2.addCell(row3);
		        
		        dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId));
		        row3 =new PdfPCell(new Paragraph(uF.showData(hmEmpContribution.get(strEmpId), "0"),small));
		        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        table2.addCell(row3);
		        
		        double dblAvg = uF.parseToDouble(hmEmpWages.get(strEmpId)) / uF.parseToDouble(hmEmpPaidDays.get(strEmpId));
		        dblTotalAvg += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblAvg)); 
		        row3 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(dblAvg),small));
		        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        table2.addCell(row3);
		        
		        row3 =new PdfPCell(new Paragraph("",small));
		        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        table2.addCell(row3);
		        
		        row3 =new PdfPCell(new Paragraph("",small));
		        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row3.setPadding(2.5f);
		        row3.setPaddingTop(8.0f);
		        table2.addCell(row3);
	        }
	        
	        //new row
	        row3 =new PdfPCell(new Paragraph("Total",small));
	        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        row3.setColspan(4);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph(""+uF.formatIntoTwoDecimalWithOutComma(dblTotalDays),small));
	        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph(""+uF.formatIntoTwoDecimalWithOutComma(dblTotalWages),small));
	        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph(""+uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution),small));
	        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph(uF.formatIntoTwoDecimalWithOutComma(dblTotalAvg),small));
	        row3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        row3 =new PdfPCell(new Paragraph("",small));
	        row3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row3.setPadding(2.5f);
	        row3.setPaddingTop(8.0f);
	        table2.addCell(row3);
	        
	        document.add(table2);
	        
	        PdfPTable table3 = new PdfPTable(10);
			table3.setWidthPercentage(100);
	        //new row
			PdfPCell row4 =new PdfPCell(new Paragraph("*Date of appointment and leaving the job may be given in remarks column.",small));
	        row4.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(10);
	        table3.addCell(row4);
	        
	      //new row
	        row4 =new PdfPCell(new Paragraph("\n\nSignature of the Employer",small));
	        row4.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(10);
	        table3.addCell(row4);
	        
	      //new row
	        row4 =new PdfPCell(new Paragraph("\n\n(FOR OFFICIAL USE)",small));
	        row4.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(10);
	        table3.addCell(row4);
	        
	      //new row
	        row4 =new PdfPCell(new Paragraph("1. Entitlement position marked.",small));
	        row4.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(10);
	        row4.setIndent(10.0f);
	        table3.addCell(row4);
	        
	      //new row
	        row4 =new PdfPCell(new Paragraph("2. Total of Col. 5 of Return checked and Found correct/correct amount is indicated.",small));
	        row4.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(10);
	        row4.setIndent(10.0f);
	        table3.addCell(row4);
	        
	      //new row
	        row4 =new PdfPCell(new Paragraph("3. Checked the amount of Employer's/Employee's contribution paid which is in order / observation memo enclosed.",small));
	        row4.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(10);
	        row4.setIndent(10.0f);
	        table3.addCell(row4);
	        
	      //new row
	        row4 =new PdfPCell(new Paragraph("\n\nCountersignature ...................................",small));
	        row4.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(10);
	        table3.addCell(row4);
	        
	      //new row
	        row4 =new PdfPCell(new Paragraph("\n\nU.D.C.",small));
	        row4.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(3);
	        table3.addCell(row4);
	        
	        //new row
	        row4 =new PdfPCell(new Paragraph("\n\nHead Clerk",small));
	        row4.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(3);
	        table3.addCell(row4);
	        
	        //new row
	        row4 =new PdfPCell(new Paragraph("\n\nBranch Officer",small));
	        row4.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row4.setBorder(Rectangle.NO_BORDER);
	        row4.setPadding(2.5f);
	        row4.setPaddingTop(8.0f);
	        row4.setColspan(4);
	        table3.addCell(row4);
	        
	        document.add(table3);
	        document.close();
	          
			String filename="Form5ESIC.pdf";
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


	public String loadForm5ESIC(){
		UtilityFunctions uF = new UtilityFunctions();
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		orgList = new FillOrganisation(request).fillOrganisation();
		monthList = new FillMonth().fillSixMonth();
		
		empNamesList=getEmployeeList();
		
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
				hmFilter.put("MONTH", "Select Month");
			}
		} else {
			hmFilter.put("MONTH", "Select Month");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private List<FillEmployee> getEmployeeList() {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
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

public String viewForm5ESIC(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			String strMonth = null;
			
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
				if(getStrMonth().equals("4,5,6,7,8,9")){
					months = "4,5,6,7,8,9";
					setStrHalfYear("01/04/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 30/09/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+"");
					setYearType("1");
				} else {
					months = "10,11,12,1,2,3";
					setStrHalfYear("01/10/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 31/03/"+uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, "yyyy"))+"");
					setYearType("2");
				}
			} else {
				months = "4,5,6,7,8,9";
				setStrHalfYear("01/04/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+" to 30/09/"+uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy"))+"");
				setYearType("1");
			}				
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String[] tmpMonths = months.split(",");
			Map<String, String> hmEmpCodeDesig =CF.getEmpDesigMap(con);			
			String orgId = CF.getEmpOrgId(con, uF, getStrSelectedEmpId());
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(orgId));
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
				hmOrg.put("ORG_ESTABLISH_CODE_NO", rs.getString("establish_code_no"));
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
			sbQuery.append("select paid_date,month,sum(amount) as amount,cheque_no,paid_date,bank_name,bank_branch,mode_tds_deposit" +
					" from challan_details where financial_year_from_date = ? and financial_year_to_date = ? " +
					"and challan_type in ("+EMPLOYEE_ESI+","+EMPLOYER_ESI+") ");
			sbQuery.append(" and (");
            for(int i=0; i<tmpMonths.length; i++){
                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
                
                if(i<tmpMonths.length-1){
                    sbQuery.append(" OR "); 
                }
            }
            sbQuery.append(" ) ");
			sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
					"where eod.emp_id = epd.emp_per_id and org_id=?) group by paid_date,month,cheque_no,paid_date,bank_name,bank_branch,mode_tds_deposit");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(orgId));
//			System.out.println("pst====>"+pst); 
			rs = pst.executeQuery();
			Map<String,String> hmMonthChallanDate = new LinkedHashMap<String, String>();
			Map<String,String> hmMonthChallan = new LinkedHashMap<String, String>();
			Map<String, Map<String, String>> hmESIC = new HashMap<String, Map<String, String>>();
			while(rs.next()){
				String paid_date = uF.getDateFormat(rs.getString("paid_date"), DBDATE,DATE_FORMAT);
				String[] tmpMonths1 = rs.getString("month").substring(1, rs.getString("month").length()-1).split(",");
				for(String strMonth1 : tmpMonths1 ){
					hmMonthChallanDate.put(strMonth1, paid_date);
					hmMonthChallan.put(strMonth1, ""+ Math.round(uF.parseToDouble(rs.getString("amount"))));
					
					Map<String, String> hmESICInner = new HashMap<String, String>();
					hmESICInner.put("CHALLAN_CHEQUE_NO", uF.showData(rs.getString("cheque_no"), ""));
					hmESICInner.put("CHALLAN_PAID_DATE", uF.getDateFormat(rs.getString("paid_date"), DBDATE, CF.getStrReportDateFormat()));
					hmESICInner.put("CHALLAN_BANK_NAME", uF.showData(rs.getString("bank_name"), ""));
					hmESICInner.put("CHALLAN_BRANCH_NAME", uF.showData(rs.getString("bank_branch"), ""));
					hmESICInner.put("CHALLAN_MODE_DEPOSIT", uF.showData(rs.getString("mode_tds_deposit"), "")); 
					
					hmESIC.put(strMonth1, hmESICInner);
					
				}
			}
			rs.close();
			pst.close(); 
//			System.out.println("hmMonthChallanDate====>"+hmMonthChallanDate);
//			System.out.println("hmMonthChallan====>"+hmMonthChallan);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select challan_type,sum(amount) as amount from challan_details where financial_year_from_date = ? and financial_year_to_date = ? " +
					"and challan_type in ("+EMPLOYEE_ESI+","+EMPLOYER_ESI+") ");
			sbQuery.append(" and (");
            for(int i=0; i<tmpMonths.length; i++){
                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
                
                if(i<tmpMonths.length-1){
                    sbQuery.append(" OR "); 
                }
            }
            sbQuery.append(" ) ");
			sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
					"where eod.emp_id = epd.emp_per_id and org_id=?) group by challan_type");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(orgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmContribution = new LinkedHashMap<String, String>();
			while(rs.next()){
				hmContribution.put(rs.getString("challan_type"), ""+ Math.round(uF.parseToDouble(rs.getString("amount"))));
			}
			rs.close();
			pst.close();
//			System.out.println("hmContribution====>"+hmContribution);
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,eesi_contribution,esi_max_limit,emp_fname,emp_mname, emp_lname ,emp_mname,emp_esic_no from " +
					"(select emp_id,sum(eesi_contribution) as eesi_contribution,sum(esi_max_limit) as esi_max_limit from emp_esi_details where financial_year_start = ? and financial_year_end = ?" +
					" and emp_id in (");
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
					"where eod.emp_id = epd.emp_per_id and org_id=?))");
			sbQuery.append(" and _month in (");
            for(int i=0; i<tmpMonths.length; i++){
                if(i == 0){
                	sbQuery.append(tmpMonths[i]); 
                } else {
                	sbQuery.append(","+tmpMonths[i]);
                }
            }
            sbQuery.append(") ");
			sbQuery.append(" group by emp_id order by emp_id) a, " +
					"employee_personal_details epd where a.emp_id=epd.emp_per_id order by emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_ESI);
			pst.setInt(6, uF.parseToInt(orgId));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpContribution = new LinkedHashMap<String, String>();
			Map<String,String> hmEmpName = new LinkedHashMap<String, String>();
			Map<String,String> hmEmpWages = new LinkedHashMap<String, String>();
			Map<String,String> hmEmpInsuranceNo = new LinkedHashMap<String, String>();
			while(rs.next()){
				
				String strMiddleName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
			
				
				String strEmpName1 = rs.getString("emp_fname") + uF.showData(strMiddleName, "")+" " + rs.getString("emp_lname");

				
				hmEmpName.put(rs.getString("emp_id"), strEmpName1);
				hmEmpContribution.put(rs.getString("emp_id"), ""+ Math.round(uF.parseToDouble(rs.getString("eesi_contribution"))));
				hmEmpWages.put(rs.getString("emp_id"), ""+ uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("esi_max_limit"))));
				hmEmpInsuranceNo.put(rs.getString("emp_id"), uF.showData(rs.getString("emp_esic_no"), ""));
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpName====>"+hmEmpName);
//			System.out.println("hmEmpContribution====>"+hmEmpContribution);
//			System.out.println("hmEmpWages====>"+hmEmpWages);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,sum(paid_days) as paid_days from payroll_generation where financial_year_from_date = ? " +
					"and financial_year_to_date = ? and emp_id in (");
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
					"where eod.emp_id = epd.emp_per_id and org_id=?))");
			sbQuery.append(" and month in (");
            for(int i=0; i<tmpMonths.length; i++){
                if(i == 0){
                	sbQuery.append(tmpMonths[i]); 
                } else {
                	sbQuery.append(","+tmpMonths[i]);
                }
            }
            sbQuery.append(") ");
            sbQuery.append("and salary_head_id=? ");
			sbQuery.append(" group by emp_id order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_ESI);
			pst.setInt(6, uF.parseToInt(orgId));
			pst.setInt(7, EMPLOYEE_ESI);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpPaidDays = new LinkedHashMap<String, String>();
			while(rs.next()){
				hmEmpPaidDays.put(rs.getString("emp_id"), rs.getString("paid_days"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpPaidDays====>"+hmEmpPaidDays);
			
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("hmEmp", hmEmp);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("hmMonthChallan", hmMonthChallan);
			request.setAttribute("hmMonthChallanDate", hmMonthChallanDate);
			request.setAttribute("hmContribution", hmContribution);
			request.setAttribute("hmEmpName", hmEmpName); 
			request.setAttribute("hmEmpContribution", hmEmpContribution);
			request.setAttribute("hmEmpWages", hmEmpWages);
			request.setAttribute("hmEmpPaidDays", hmEmpPaidDays);
			request.setAttribute("hmEmpInsuranceNo", hmEmpInsuranceNo);
			request.setAttribute("hmESIC", hmESIC);
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
	
	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
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


	public String getFormType() {
		return formType;
	}


	public void setFormType(String formType) {
		this.formType = formType;
	}


	public String getStrHalfYear() {
		return strHalfYear;
	}


	public void setStrHalfYear(String strHalfYear) {
		this.strHalfYear = strHalfYear;
	}


	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}


	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}


	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}


	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}


	public String getYearType() {
		return yearType;
	}


	public void setYearType(String yearType) {
		this.yearType = yearType;
	}


}
