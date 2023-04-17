package com.konnect.jpms.itforms;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

public class Form6ESIC  extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

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
	
	
	String formType;
	String strHalfYear;

	String strSelectedEmpId;
	String yearType;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/itforms/Form6ESIC.jsp");
		request.setAttribute(TITLE, "Form 6 ESIC");
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		
		if(getStrMonth()==null){
			setStrMonth("1");
		}
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewForm6ESIC();
		
		if(getFormType()!=null && getFormType().equals("pdf")){
			generateForm6ESICPdf();
			return "";
		}
		
		return loadForm6ESIC();

	}
	
	
	private void generateForm6ESICPdf() {
		try{
			UtilityFunctions uF = new UtilityFunctions();
			
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
			if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
			Map<String, String> hmEmpDepartment = (Map<String, String>) request.getAttribute("hmEmpDepartment");
			if(hmEmpDepartment == null) hmEmpDepartment = new HashMap<String, String>();
			Map<String, String> hmDept = (Map<String, String>) request.getAttribute("hmDept");
			if(hmDept == null) hmDept = new HashMap<String, String>();
			Map<String, String> hmMonthChallanDate = (Map<String, String>) request.getAttribute("hmMonthChallanDate");
			if(hmMonthChallanDate == null) hmMonthChallanDate = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
			if(hmEmpName == null) hmEmpName = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpContribution = (Map<String, String>) request.getAttribute("hmEmpContribution");
			if(hmEmpContribution == null) hmEmpContribution = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpWages = (Map<String, String>) request.getAttribute("hmEmpWages");
			if(hmEmpWages == null) hmEmpWages = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpPaidDays = (Map<String, String>) request.getAttribute("hmEmpPaidDays");
			if(hmEmpPaidDays == null) hmEmpPaidDays = new LinkedHashMap<String, String>();
			
			Map<String, String> hmEmployerContribution = (Map<String, String>) request.getAttribute("hmEmployerContribution");
			if(hmEmployerContribution == null) hmEmployerContribution = new LinkedHashMap<String, String>();
			Map<String, String> hmEmpJoinLeftDate = (Map<String, String>) request.getAttribute("hmEmpJoinLeftDate");
			if(hmEmpJoinLeftDate == null) hmEmpJoinLeftDate = new HashMap<String, String>();
			
			Map<String, String> hmEmpInsuranceNo = (Map<String, String>) request.getAttribute("hmEmpInsuranceNo");
			if(hmEmpInsuranceNo == null) hmEmpInsuranceNo = new LinkedHashMap<String, String>();
			
			List<String> alEmpList = (List<String>) request.getAttribute("alEmpList");
			if(alEmpList == null) alEmpList = new ArrayList<String>();
			
			
			
			
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.TIMES_ROMAN,10);
			Font verysmall = new Font(Font.FontFamily.TIMES_ROMAN,8);
			Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN,10,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);        
	        
	        //New Row
	        PdfPCell row =new PdfPCell(new Paragraph("",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setBorder(Rectangle.NO_BORDER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(6);
	        table.addCell(row);
	        
	        //New Row
	        row =new PdfPCell(new Paragraph("FORM 6",smallBold));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setBorder(Rectangle.NO_BORDER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(6);
	        table.addCell(row);
	        
	        //new row
	        row =new PdfPCell(new Paragraph("REGISTER OF EMPLOYEES\nEMPLOYEES' STATE INSURANCE CORPORATION",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setBorder(Rectangle.NO_BORDER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(6);
	        table.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("(Regulation 32)",italicEffect));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setBorder(Rectangle.NO_BORDER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(6);
	        table.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Contribution Period : From "+uF.showData(getStrHalfYear(), ""),small));
	        row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);  
	        row.setColspan(6);
	        table.addCell(row);

	        document.add(table);
	        
	        int j = 10;
	        if(getYearType()!=null && getYearType().equals("1")){
	        	j = 4;
	        }
					
			// new table
	        PdfPTable table1 = new PdfPTable(11);
			table1.setWidthPercentage(100);
			
			//new row
			row =new PdfPCell(new Paragraph("Sl.No.",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setRowspan(2);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Insurance No.",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setRowspan(2);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Name of Insured Person",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(2);
	        row.setRowspan(2);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("*Name of dispensary to which attached",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setRowspan(2);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Occupation",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setRowspan(2);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Deptt. and shift, if any",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setRowspan(2);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("If appointed or left service during the contribution period, date of appointment/leaving service",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setRowspan(2);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Month "+uF.getMonth(j),small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(3);
	        table1.addCell(row);
	        
	        //new row
	        row =new PdfPCell(new Paragraph("No. of days for which wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Total amount of wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Employees' share of contribution",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("1",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("2",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("3",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(2);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("3(A)",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("4",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("5",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("6",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("7",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("8",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("9",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        double dblTotalWages = 0.0d;
	        double dblTotalContribution = 0.0d;
	        Map<String,String> hmEmpTotalPaidDays = new HashMap<String, String>();
	        Map<String,String> hmEmpTotalWages = new HashMap<String, String>();
	        Map<String,String> hmEmpTotalContribution = new HashMap<String, String>();
	        for (int i = 0; i < alEmpList.size(); i++){
	        	String strEmpId = alEmpList.get(i);
	        	//new row
		        row =new PdfPCell(new Paragraph(""+(i+1),small));
		        row.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
		        
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpInsuranceNo.get(strEmpId), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
		        
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpName.get(strEmpId), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setColspan(2);
		        table1.addCell(row);
		        
		        row =new PdfPCell(new Paragraph("",small));
		        row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
		        
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpCodeDesig.get(strEmpId), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
		        
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmDept.get(hmEmpDepartment.get(strEmpId)), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
		        
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpJoinLeftDate.get(strEmpId), ""),verysmall));
		        row.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
		        
		        hmEmpTotalPaidDays.put(strEmpId, ""+uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j)));
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
		        
		        dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        hmEmpTotalWages.put(strEmpId, ""+uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j)));
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpWages.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
		        
		        dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        hmEmpTotalContribution.put(strEmpId, ""+uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j)));
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpContribution.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        table1.addCell(row);
	        }
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP);
	        row.setColspan(9);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM );
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Employers' Share",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(10);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Grand Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(10);
	        table1.addCell(row);
	        
	        double dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
	        row =new PdfPCell(new Paragraph(dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Paid on",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(10);
	        table1.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(""+uF.showData(hmMonthChallanDate.get(""+j), ""),verysmall));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table1.addCell(row);
	        
	        document.add(table1);
				
	        document.newPage();
	        
	     // new table
	        PdfPTable tableOuter = new PdfPTable(3);
	        tableOuter.setWidthPercentage(100);
			
	        PdfPTable table2 = new PdfPTable(3);
			table2.setWidthPercentage(100);
			
			j++;
			//new row
			row =new PdfPCell(new Paragraph("Month "+uF.getMonth(j),small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(3);
	        table2.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("No. of days for which wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Total amount of wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Employees' share of contribution",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	        //new row 
	        row =new PdfPCell(new Paragraph("10",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("11",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("12",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	        dblTotalWages = 0.0d;
	        dblTotalContribution = 0.0d;
	        for (int i = 0; i < alEmpList.size(); i++){
	        	String strEmpId = alEmpList.get(i);
	        	//new row
	        	double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
	        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
	        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table2.addCell(row);
		        
		        dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        
		        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
		        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);		        
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpWages.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table2.addCell(row);
		        
		        dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
		        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);	
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpContribution.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table2.addCell(row);
	        }
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP);
	        table2.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM );
	        table2.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Employers' Share",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table2.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Grand Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table2.addCell(row);
	        
	        dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
	        row =new PdfPCell(new Paragraph(dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Paid on",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table2.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(""+uF.showData(hmMonthChallanDate.get(""+j), ""),verysmall));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table2.addCell(row);
	        
	        PdfPCell rowOuter =new PdfPCell(table2);
	        rowOuter.setBorder(Rectangle.NO_BORDER);
	        tableOuter.addCell(rowOuter);
	        

	     // new table
	        PdfPTable table3 = new PdfPTable(3);
			table3.setWidthPercentage(100);
			
			j++;
			//new row
			row =new PdfPCell(new Paragraph("Month "+uF.getMonth(j),small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(3);
	        table3.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("No. of days for which wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Total amount of wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Employees' share of contribution",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	        //new row 
	        row =new PdfPCell(new Paragraph("13",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("14",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("15",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	        dblTotalWages = 0.0d;
	        dblTotalContribution = 0.0d;
	        for (int i = 0; i < alEmpList.size(); i++){
	        	String strEmpId = alEmpList.get(i);
	        	//new row
	        	double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
	        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
	        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table3.addCell(row);
		        
		        dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        
		        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
		        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);		 
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpWages.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table3.addCell(row);
		        
		        dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
		        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpContribution.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table3.addCell(row);
	        }
	      //new row
	        row =new PdfPCell(new Paragraph("Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP);
	        table3.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM );
	        table3.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Employers' Share",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table3.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Grand Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table3.addCell(row);
	        
	        dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
	        row =new PdfPCell(new Paragraph(dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Paid on",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table3.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(""+uF.showData(hmMonthChallanDate.get(""+j), ""),verysmall));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table3.addCell(row);
	        
	        rowOuter =new PdfPCell(table3);
	        rowOuter.setBorder(Rectangle.NO_BORDER);
	        tableOuter.addCell(rowOuter);
	        
	     // new table
	        PdfPTable table4 = new PdfPTable(3);
			table4.setWidthPercentage(100);
			
			j++;
			if(j==13){
				j=1;
			}
			//new row
			row =new PdfPCell(new Paragraph("Month "+uF.getMonth(j),small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(3);
	        table4.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("No. of days for which wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Total amount of wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Employees' share of contribution",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	        //new row 
	        row =new PdfPCell(new Paragraph("16",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("17",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("18",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	        dblTotalWages = 0.0d;
	        dblTotalContribution = 0.0d;
	        for (int i = 0; i < alEmpList.size(); i++){
	        	String strEmpId = alEmpList.get(i);
	        	//new row
	        	double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
	        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
	        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table4.addCell(row);
		        
		        dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        
		        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
		        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);		 
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpWages.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table4.addCell(row);
		        
		        dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
		        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpContribution.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table4.addCell(row);
	        }
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP);
	        table4.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM );
	        table4.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Employers' Share",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table4.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Grand Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table4.addCell(row);
	        
	        dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
	        row =new PdfPCell(new Paragraph(dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Paid on",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table4.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(""+uF.showData(hmMonthChallanDate.get(""+j), ""),verysmall));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table4.addCell(row);
	        
	        rowOuter =new PdfPCell(table4);
	        rowOuter.setBorder(Rectangle.NO_BORDER);
	        tableOuter.addCell(rowOuter);
	        
	        document.add(tableOuter);
	        
	        document.newPage();
	        
	     // new table
	        PdfPTable tableOuter1 = new PdfPTable(3);
	        tableOuter1.setWidthPercentage(100);
			
	        PdfPTable table5 = new PdfPTable(3);
			table5.setWidthPercentage(100);
			
			j++;
			if(j==14){
				j=2;
			}
			//new row
			row =new PdfPCell(new Paragraph("Month "+uF.getMonth(j),small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(3);
	        table5.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("No. of days for which wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table5.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Total amount of wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table5.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Employees' share of contribution",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table5.addCell(row);
	        
	        //new row 
	        row =new PdfPCell(new Paragraph("19",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table5.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("20",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table5.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("21",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table5.addCell(row);
	        
	        dblTotalWages = 0.0d;
	        dblTotalContribution = 0.0d;
	        for (int i = 0; i < alEmpList.size(); i++){
	        	String strEmpId = alEmpList.get(i);
	        	//new row
	        	double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
	        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
	        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table5.addCell(row);
		        
		        dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        
		        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
		        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);		 
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpWages.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table5.addCell(row);
		        
		        dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
		        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpContribution.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table5.addCell(row);
	        }
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP);
	        table5.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM );
	        table5.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table5.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Employers' Share",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table5.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table5.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Grand Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table5.addCell(row);
	        
	        dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
	        row =new PdfPCell(new Paragraph(dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table5.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Paid on",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table5.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(""+uF.showData(hmMonthChallanDate.get(""+j), ""),verysmall));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table5.addCell(row);
	        
	        rowOuter =new PdfPCell(table5);
	        rowOuter.setBorder(Rectangle.NO_BORDER);
	        tableOuter1.addCell(rowOuter);
	        

	     // new table
	        PdfPTable table6 = new PdfPTable(3);
			table6.setWidthPercentage(100);
			
			j++;
			if(j==15){
				j=3;
			}
			//new row
			row =new PdfPCell(new Paragraph("Month "+uF.getMonth(j),small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(3);
	        table6.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("No. of days for which wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table6.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Total amount of wages paid/payable",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table6.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Employees' share of contribution",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table6.addCell(row);
	        
	        //new row 
	        row =new PdfPCell(new Paragraph("22",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table6.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("23",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table6.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("24",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table6.addCell(row);
	        
	        dblTotalWages = 0.0d;
	        dblTotalContribution = 0.0d;
	        for (int i = 0; i < alEmpList.size(); i++){
	        	String strEmpId = alEmpList.get(i);
	        	//new row
	        	double dblPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
	        	dblPaidDays += uF.parseToDouble(hmEmpPaidDays.get(strEmpId+"_"+j));
	        	hmEmpTotalPaidDays.put(strEmpId, ""+dblPaidDays);
		        row =new PdfPCell(new Paragraph(""+uF.showData(hmEmpPaidDays.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table6.addCell(row);
		        
		        dblTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        
		        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
		        dblEmpTotalWages += uF.parseToDouble(hmEmpWages.get(strEmpId+"_"+j));
		        hmEmpTotalWages.put(strEmpId, ""+dblEmpTotalWages);		 
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpWages.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table6.addCell(row);
		        
		        dblTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
		        dblEmpTotalContribution += uF.parseToDouble(hmEmpContribution.get(strEmpId+"_"+j));
		        hmEmpTotalContribution.put(strEmpId, ""+dblEmpTotalContribution);
		        row =new PdfPCell(new Paragraph(uF.showData(hmEmpContribution.get(strEmpId+"_"+j), ""),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table6.addCell(row);
	        }
	      //new row
	        row =new PdfPCell(new Paragraph("Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP);
	        table6.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalWages > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalWages) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT | Rectangle.TOP | Rectangle.LEFT | Rectangle.BOTTOM );
	        table6.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(dblTotalContribution > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table6.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Employers' Share",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table6.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(uF.parseToDouble(hmEmployerContribution.get(""+j)) > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmployerContribution.get(""+j))) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table6.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Grand Total",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table6.addCell(row);
	        
	        dblGrandTotal = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalContribution)) + uF.parseToDouble(hmEmployerContribution.get(""+j));
	        row =new PdfPCell(new Paragraph(dblGrandTotal > 0.00d ? uF.formatIntoTwoDecimalWithOutComma(dblGrandTotal) : "",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table6.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Paid on",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.RIGHT);
	        row.setColspan(2);
	        table6.addCell(row);
	        
	        row =new PdfPCell(new Paragraph(""+uF.showData(hmMonthChallanDate.get(""+j), ""),verysmall));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table6.addCell(row);
	        
	        rowOuter =new PdfPCell(table6);
	        rowOuter.setBorder(Rectangle.NO_BORDER);
	        tableOuter1.addCell(rowOuter);
	        
	     // new table
	        PdfPTable table7 = new PdfPTable(4);
			table7.setWidthPercentage(100);
			
			//new row
			row =new PdfPCell(new Paragraph("Summary",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(4);
	        table7.addCell(row);
	        
	      //new row
	        row =new PdfPCell(new Paragraph("Total No. of days for which wages paid/payable in Contribution period",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table7.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Total amount of wages paid/payable in Contribution period (Rs.)",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table7.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Total Employee's share of Contribution in Contribution period (Rs.)",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table7.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("Daily wage\n(26/25)\n(Rs.)",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setFixedHeight(115.0f);
	        table7.addCell(row);
	        
	        //new row 
	        row =new PdfPCell(new Paragraph("25",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table7.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("26",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table7.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("27",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table7.addCell(row);
	        
	        row =new PdfPCell(new Paragraph("28",small));
	        row.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        table7.addCell(row);
	        for (int i = 0; i < alEmpList.size(); i++){
	        	String strEmpId = alEmpList.get(i);
	        	//new row
	        	double dblTotalPaidDays = uF.parseToDouble(hmEmpTotalPaidDays.get(strEmpId));
	        	dblTotalPaidDays = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblTotalPaidDays));
		        row =new PdfPCell(new Paragraph(""+dblTotalPaidDays,small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table7.addCell(row);
		        
		        double dblEmpTotalWages = uF.parseToDouble(hmEmpTotalWages.get(strEmpId));
		        dblEmpTotalWages = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEmpTotalWages));
		        row =new PdfPCell(new Paragraph(""+dblEmpTotalWages,small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table7.addCell(row);
		        
		        double dblEmpTotalContribution = uF.parseToDouble(hmEmpTotalContribution.get(strEmpId));
		        dblEmpTotalContribution = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblEmpTotalContribution));
		        row =new PdfPCell(new Paragraph(""+dblEmpTotalContribution,small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table7.addCell(row);
		        
		        double dblDailyAvg = dblEmpTotalWages / dblTotalPaidDays;
		        row =new PdfPCell(new Paragraph(""+uF.formatIntoTwoDecimalWithOutComma(dblDailyAvg),small));
		        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row.setPadding(2.5f);
		        row.setPaddingTop(8.0f);
		        row.setFixedHeight(20.5f);
		        table7.addCell(row);
	        }
	        	        
	      //new row
	        row =new PdfPCell(new Paragraph("",small));
	        row.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setBorder(Rectangle.LEFT); 
	        row.setColspan(4);
	        table7.addCell(row);
	        	        
	        rowOuter =new PdfPCell(table7);
	        rowOuter.setBorder(Rectangle.NO_BORDER);
	        tableOuter1.addCell(rowOuter);
	        
	        document.add(tableOuter1);
	        
	        //New Table 
	        PdfPTable table8 = new PdfPTable(6);
			table8.setWidthPercentage(100);        
	        
	        //New Row
	        row =new PdfPCell(new Paragraph("Note: The figures in Columns 7 to 24 shall be in respect of wage periods ending in a particular calendar month.",small));
	        row.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row.setBorder(Rectangle.NO_BORDER);
	        row.setPadding(2.5f);
	        row.setPaddingTop(8.0f);
	        row.setColspan(6);
	        table8.addCell(row);
	        document.add(table8);
	        
	        document.close();
	          
			String filename="Form6ESIC.pdf";
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


	public String loadForm6ESIC(){
		UtilityFunctions uF = new UtilityFunctions();
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		orgList = new FillOrganisation(request).fillOrganisation();
		monthList = new FillMonth().fillSixMonth();
		
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
	
	
	public String viewForm6ESIC(){
		
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
			Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);	
			Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
			Map<String, String> hmDept = CF.getDeptMap(con);
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(getF_org()));
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
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select paid_date,month,sum(amount) as amount from challan_details where financial_year_from_date = ? and financial_year_to_date = ? " +
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
					"where eod.emp_id = epd.emp_per_id and org_id=?) group by paid_date,month");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmMonthChallanDate = new LinkedHashMap<String, String>();
			while(rs.next()){
				String paid_date = uF.getDateFormat(rs.getString("paid_date"), DBDATE,DATE_FORMAT);
				String[] tmpMonths1 = rs.getString("month").substring(1, rs.getString("month").length()-1).split(",");
				for(String strMonth1 : tmpMonths1 ){
					hmMonthChallanDate.put(strMonth1, paid_date);
				}
			} 
			rs.close();
			pst.close();
//			System.out.println("hmMonthChallanDate====>"+hmMonthChallanDate);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,eesi_contribution,esi_max_limit,emp_fname,emp_mname,emp_lname,emp_mname,_month,emp_esic_no from " +
					"(select emp_id,sum(eesi_contribution) as eesi_contribution,sum(esi_max_limit) as esi_max_limit,_month from emp_esi_details where financial_year_start = ? and financial_year_end = ?" +
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
			sbQuery.append(" group by emp_id,_month order by emp_id,_month) a, " +
					"employee_personal_details epd where a.emp_id=epd.emp_per_id order by emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_ESI);
			pst.setInt(6, uF.parseToInt(getF_org()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpContribution = new LinkedHashMap<String, String>();
			Map<String,String> hmEmpName = new LinkedHashMap<String, String>();
			Map<String,String> hmEmpWages = new LinkedHashMap<String, String>();
			List<String> alEmpList = new ArrayList<String>();
			Map<String,String> hmEmpInsuranceNo = new LinkedHashMap<String, String>();
			int j = 0;
			String strEmpids = "";
			while(rs.next()){
				if(!alEmpList.contains(rs.getString("emp_id"))){
					alEmpList.add(rs.getString("emp_id"));
					if(j == 0){
						strEmpids = rs.getString("emp_id");
					} else {
						strEmpids += ","+rs.getString("emp_id");
					}
					j++;
				}
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				
				String strEmpName = rs.getString("emp_fname") + uF.showData(strMiddleName, "")+" " + rs.getString("emp_lname");
			
				
				hmEmpName.put(rs.getString("emp_id"),strEmpName);
				hmEmpContribution.put(rs.getString("emp_id")+"_"+rs.getString("_month"), ""+ Math.round(uF.parseToDouble(rs.getString("eesi_contribution"))));
				hmEmpWages.put(rs.getString("emp_id")+"_"+rs.getString("_month"), ""+ uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("esi_max_limit"))));
				hmEmpInsuranceNo.put(rs.getString("emp_id"), uF.showData(rs.getString("emp_esic_no"), ""));
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpName====>"+hmEmpName);
//			System.out.println("hmEmpContribution====>"+hmEmpContribution);
//			System.out.println("hmEmpWages====>"+hmEmpWages);
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(ersi_contribution) as ersi_contribution,_month from emp_esi_details where financial_year_start = ? and financial_year_end = ?" +
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
			sbQuery.append(" group by _month order by _month");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYER_ESI);
			pst.setInt(6, uF.parseToInt(getF_org()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmployerContribution = new LinkedHashMap<String, String>();
			while(rs.next()){
				hmEmployerContribution.put(rs.getString("_month"), ""+ Math.round(uF.parseToDouble(rs.getString("ersi_contribution"))));
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmployerContribution====>"+hmEmployerContribution);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_id,month,sum(paid_days) as paid_days from payroll_generation where financial_year_from_date = ? " +
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
			sbQuery.append(" group by emp_id,month order by emp_id,month");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, EMPLOYEE_ESI);
			pst.setInt(6, uF.parseToInt(getF_org()));
			pst.setInt(7, EMPLOYEE_ESI);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmEmpPaidDays = new LinkedHashMap<String, String>();
			while(rs.next()){
				hmEmpPaidDays.put(rs.getString("emp_id")+"_"+rs.getString("month"), rs.getString("paid_days"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpPaidDays====>"+hmEmpPaidDays);
			Map<String, String> hmEmpJoinLeftDate = new HashMap<String, String>();
			if(strEmpids!=null && !strEmpids.equals("")){
				pst = con.prepareStatement("select emp_per_id,joining_date,employment_end_date from employee_personal_details where emp_per_id in ("+strEmpids+")");
				rs = pst.executeQuery();
				while (rs.next()){
					String strJoinLeftDate = uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT);
					if(rs.getString("employment_end_date")!=null){
						strJoinLeftDate = uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT);
					}
					hmEmpJoinLeftDate.put(rs.getString("emp_per_id"), strJoinLeftDate);
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("alEmpList", alEmpList);
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("hmEmpCodeDesig", hmEmpCodeDesig);
			request.setAttribute("hmEmpDepartment", hmEmpDepartment);
			request.setAttribute("hmDept", hmDept);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("hmMonthChallanDate", hmMonthChallanDate);
			request.setAttribute("hmEmpName", hmEmpName); 
			request.setAttribute("hmEmpContribution", hmEmpContribution);
			request.setAttribute("hmEmpWages", hmEmpWages);
			request.setAttribute("hmEmpPaidDays", hmEmpPaidDays);
			request.setAttribute("hmEmployerContribution", hmEmployerContribution);
			request.setAttribute("hmEmpJoinLeftDate", hmEmpJoinLeftDate);
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
