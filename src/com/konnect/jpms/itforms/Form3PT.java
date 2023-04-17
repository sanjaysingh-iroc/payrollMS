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
import com.konnect.jpms.select.FillGender;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillState;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form3PT extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	
	private String strSubmit;
	private String financialYear;
	private String strMonth;
	private String formType;
	private String f_org;
	private String f_state;
	private String strGender;
	
	List<FillOrganisation> orgList;
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	List<FillState> stateList;	
	List<FillGender> genderList;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/itforms/Form3PT.jsp");
		request.setAttribute(TITLE, "Form 3");
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getStrMonth() == null || getStrMonth().trim().equals("") || getStrMonth().trim().equalsIgnoreCase("NULL")){
			setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
		}
		
		if(getF_state() == null || getF_state().trim().equals("") || getF_state().trim().equalsIgnoreCase("NULL")){
			setF_state("4");
		}
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrGender() == null || getStrGender().trim().equals("") || getStrGender().trim().equalsIgnoreCase("NULL")){
			setStrGender("M");
		}
		
		viewForm3PT(uF);
		
		if(getFormType()!=null && getFormType().equals("pdf")){
			generateForm3PTPdf(uF);
			return null;
		}
		
		return loadForm3PT(uF);
	}
		
	private void generateForm3PTPdf(UtilityFunctions uF) {
		try{
			
			Map<String, Map<String, String>> hmPTSlab = (Map<String, Map<String, String>>) request.getAttribute("hmPTSlab"); 
			if(hmPTSlab == null) hmPTSlab = new LinkedHashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmPTDetails =  (Map<String, Map<String, String>>) request.getAttribute("hmPTDetails"); 
			if(hmPTDetails == null) hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmOrg =  (Map<String, String>) request.getAttribute("hmOrg"); 
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			String paidfrom = (String) request.getAttribute("paidfrom");
			String paidto = (String) request.getAttribute("paidto");
			String strQuarter = (String) request.getAttribute("strQuarter");
			String strChalanNo = (String) request.getAttribute("strChalanNo");
			
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
	        
	        //new row
	        PdfPCell rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("FORM III\nPart I-A\nReturn-cum-Chalan\nFor the Profession Tax Officer",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("B.S.T.R.C. No., if any.",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);  
	        rowCell.setColspan(6);
	        table.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("The Maharashtra State Tax on Professions, Trades, Callings And Employments\nAct, 1975 AND Rule 11,11-A, 11-B, 11-C",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table.addCell(rowCell);
	        	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("0028, Other Taxes on Income and Expenditure- Taxes on Professions, Trades,\nCallings and Employments- Taxes on Employments",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);  
	        rowCell.setColspan(6);
	        table.addCell(rowCell);

	        document.add(table);
	        
	        PdfPTable table1 = new PdfPTable(8);
			table1.setWidthPercentage(100);
			
			//new row
			rowCell =new PdfPCell(new Paragraph("Employees whose monthly Salaries, Wages",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Rate of tax per month",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("No. of Employees",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        table1.addCell(rowCell);
		        
	        rowCell =new PdfPCell(new Paragraph("Amount of Tax deducted",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        table1.addCell(rowCell);
	        
	        Iterator<String> it = hmPTSlab.keySet().iterator();
	        int i = 0;
	        double dblTaxAmt = 0.0d;
	        while(it.hasNext()){
	        	String strAmount = it.next();
	        	Map<String, String> hmPTSlabDetails = hmPTSlab.get(strAmount);
	        	Map<String, String> hmSalPT = hmPTDetails.get(strAmount);
	        	if (hmSalPT == null) hmSalPT = new HashMap<String, String>();
	        	
	        	String strMsg = "";
	        	String strRate = "";
	        	String strTotalAmt = uF.showData(hmSalPT.get("TOTAL_AMOUNT"), "");
	        	dblTaxAmt += uF.parseToDouble(hmSalPT.get("TOTAL_AMOUNT"));
	        	if(i==0){
	        		strMsg = "Do not exceed Rs. "+ hmPTSlabDetails.get("INCOME_TO");
	        		strRate = "Nil";
	        		strTotalAmt = "Nil";
	        	} else if(i == hmPTSlab.size()-1){
	        		strMsg = "Exceeds Rs."+hmPTSlabDetails.get("INCOME_FROM");
	        		double dblAnnualamt = uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_AMOUNT"));
	        		strRate = "Rs."+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_AMOUNT"))) +" per annum to be paid in the following" +
	        				" manner:-\n(a)Rs."+hmPTSlabDetails.get("DEDUCTION_PAYCYCLE")+" per month except in the month of February\n" +
	        				"(b)Rs."+(uF.parseToDouble(hmPTSlabDetails.get("DEDUCTION_PAYCYCLE"))+100)+" per month of February";
	        	} else {
	        		strMsg = "Exceed Rs. "+hmPTSlabDetails.get("INCOME_FROM")+" but do not exceed Rs. "+hmPTSlabDetails.get("INCOME_TO");
	        		strRate = "Rs."+hmPTSlabDetails.get("DEDUCTION_PAYCYCLE");
	        	}
	        	
	        	//new row
				rowCell =new PdfPCell(new Paragraph(strMsg,small));
		        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
		        rowCell.setPadding(2.5f);
		        rowCell.setPaddingTop(8.0f);
		        rowCell.setColspan(4);
		        table1.addCell(rowCell);
		        
		        rowCell =new PdfPCell(new Paragraph(strRate,small));
		        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		        rowCell.setPadding(2.5f);
		        rowCell.setPaddingTop(8.0f);
		        rowCell.setColspan(2);
		        table1.addCell(rowCell);
		        
		        rowCell =new PdfPCell(new Paragraph(uF.showData(hmSalPT.get("EMP_COUNT"), ""),small));
		        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		        rowCell.setPadding(2.5f);
		        rowCell.setPaddingTop(8.0f);
		        table1.addCell(rowCell);
			        
		        rowCell =new PdfPCell(new Paragraph(strTotalAmt,small));
		        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
		        rowCell.setPadding(2.5f);
		        rowCell.setPaddingTop(8.0f);
		        table1.addCell(rowCell);
		        
		        i++;
	        	
	        }
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Tax Amount",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Interest Amount",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Less-Excess tax paid, if any, in the previous",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Year/Quarter/Month",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(uF.showData(strQuarter, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Net Amount Payable",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Total Amount Paid (in words)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Profession Tax Registration Certificate No.",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Period From\n"+uF.showData(paidfrom, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Period To\n"+uF.showData(paidto, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table1.addCell(rowCell);
	      
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Name and Address",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table1.addCell(rowCell);
	        
		    document.add(table1);
		    
		    PdfPTable table2 = new PdfPTable(6);
			table2.setWidthPercentage(100);
			
			//new row
	        rowCell =new PdfPCell(new Paragraph("The above statements are true to the best of my knowledge and belief.",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table2.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Date: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table2.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Place: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table2.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Signature & Designation",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table2.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("For the Treasury Use Only",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table2.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Received Rs. (in words)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table2.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Rupees (in Figures)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table2.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        table2.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Date of Entry: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table2.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Chalan No.: "+uF.showData(strChalanNo, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table2.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        table2.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table2.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Treasurer",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2); 
	        table2.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Accountant",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table2.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Treasury Officer/Agent or Manager",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table2.addCell(rowCell);
	        
	        document.add(table2);
	        
	        document.newPage();
	        
	        PdfPTable table3 = new PdfPTable(6);
			table3.setWidthPercentage(100);        
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table3.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("FORM III\nPart II\nReturn-cum-Chalan\nFor the Treasury",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table3.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("B.S.T.R.C. No., if any.",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);  
	        rowCell.setColspan(6);
	        table3.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("The Maharashtra State Tax on Professions, Trades, Callings And Employments\nAct, 1975 AND Rule 11,11-A, 11-B, 11-C",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table3.addCell(rowCell);
	        	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("0028, Other Taxes on Income and Expenditure- Taxes on Professions, Trades,",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);  
	        rowCell.setColspan(6);
	        table3.addCell(rowCell);

	        document.add(table3);
	        
	        PdfPTable table4 = new PdfPTable(8);
			table4.setWidthPercentage(100);
			
			//new row
			rowCell =new PdfPCell(new Paragraph("Rate of Tax No. of",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.BOTTOM);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Amount of Tax",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.BOTTOM);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Tax Amount",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Interest Amount",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Less-Excess tax paid, if any, in the previous",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Year/Quarter/Month",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(uF.showData(strQuarter, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Net Amount Payable",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Total Amount Paid (in words)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Profession Tax Registration Certificate No.\n",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Period From\n"+uF.showData(paidfrom, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Period To\n"+uF.showData(paidto, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table4.addCell(rowCell);
	      
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Name and Address",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table4.addCell(rowCell);
	        
		    document.add(table4);
		    
		    PdfPTable table5 = new PdfPTable(6);
			table5.setWidthPercentage(100);
			
			//new row
	        rowCell =new PdfPCell(new Paragraph("The above statements are true to the best of my knowledge and belief.",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table5.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Date: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table5.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Place: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table5.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Signature & Designation",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table5.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("For the Treasury Use Only",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table5.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Received Rs. (in words)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table5.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Rupees (in Figures)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table5.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        table5.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Date of Entry: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table5.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Chalan No.: "+uF.showData(strChalanNo, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table5.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        table5.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table5.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Treasurer",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2); 
	        table5.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Accountant",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table5.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Treasury Officer/Agent or Manager",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table5.addCell(rowCell);
	        
	        document.add(table5);
	        
	        document.newPage();
	        
	        PdfPTable table6 = new PdfPTable(6);
			table6.setWidthPercentage(100);        
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table6.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("FORM III\nPart II\nReturn-cum-Chalan\nFor the Payer",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table6.addCell(rowCell);
	        
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("B.S.T.R.C. No., if any.",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);  
	        rowCell.setColspan(6);
	        table6.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("The Maharashtra State Tax on Professions, Trades, Callings And Employments\nAct, 1975 AND Rule 11,11-A, 11-B, 11-C",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table6.addCell(rowCell);
	        	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("0028, Other Taxes on Income and Expenditure- Taxes on Professions, Trades,",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);  
	        rowCell.setColspan(6);
	        table6.addCell(rowCell);

	        document.add(table6);
	        
	        PdfPTable table7 = new PdfPTable(8);
			table7.setWidthPercentage(100);
			
			//new row
			rowCell =new PdfPCell(new Paragraph("Rate of Tax No. of",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.BOTTOM);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Amount of Tax",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.BOTTOM);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Tax Amount",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(""+dblTaxAmt,small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Interest Amount",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Less-Excess tax paid, if any, in the previous",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Year/Quarter/Month",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(uF.showData(strQuarter, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Net Amount Payable",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Total Amount Paid (in words)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Profession Tax Registration Certificate No.\n",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Period From\n"+uF.showData(paidfrom, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Period To\n"+uF.showData(paidto, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table7.addCell(rowCell);
	      
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Name and Address",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(4);
	        table7.addCell(rowCell);
	        
		    document.add(table7);
		    
		    PdfPTable table8 = new PdfPTable(6);
			table8.setWidthPercentage(100);
			
			//new row
	        rowCell =new PdfPCell(new Paragraph("The above statements are true to the best of my knowledge and belief.",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table8.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("Date: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table8.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Place: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table8.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Signature & Designation",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table8.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("For the Treasury Use Only",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table8.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Received Rs. (in words)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table8.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Rupees (in Figures)",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table8.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        table8.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Date of Entry: ",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(3);
	        table8.addCell(rowCell);
			
	        rowCell =new PdfPCell(new Paragraph("Chalan No.: "+uF.showData(strChalanNo, ""),small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table8.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("",small));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        table8.addCell(rowCell);
	        
	        //new row
	        rowCell =new PdfPCell(new Paragraph("",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(6);
	        table8.addCell(rowCell);
	        
	      //new row
	        rowCell =new PdfPCell(new Paragraph("Treasurer",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2); 
	        table8.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Accountant",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table8.addCell(rowCell);
	        
	        rowCell =new PdfPCell(new Paragraph("Treasury Officer/Agent or Manager",smallBold));
	        rowCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        rowCell.setBorder(Rectangle.NO_BORDER);
	        rowCell.setPadding(2.5f);
	        rowCell.setPaddingTop(8.0f);
	        rowCell.setColspan(2);
	        table8.addCell(rowCell);
	        
	        document.add(table8);
	        
	        document.close();
	          
			String filename="Form3PT.pdf";
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


	public String loadForm3PT(UtilityFunctions uF){
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		orgList = new FillOrganisation(request).fillOrganisation();
		stateList = new FillState(request).fillWLocationStates();
		monthList = new FillMonth().fillMonth();
		genderList=new FillGender().fillGender();
		
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
		
		alFilter.add("STATE");
		if(getF_state()!=null) {
			String strState="";
			for(int i=0;stateList!=null && i<stateList.size();i++) {
				if(getF_state().equals(stateList.get(i).getStateId())) {
					strState=stateList.get(i).getStateName();
				}
			}
			if(strState!=null && !strState.equals("")) {
				hmFilter.put("STATE", strState);
			} else {
				hmFilter.put("STATE", "Select State");
			}
		} else {
			hmFilter.put("STATE", "Select State");
		}
		
		alFilter.add("GENDER");
		if(getStrGender()!=null) {
			String strGender="";
			for(int i=0;genderList!=null && i<genderList.size();i++) {
				if(getStrGender().equals(genderList.get(i).getGenderId())) {
					strGender=genderList.get(i).getGenderName();
				}
			}
			if(strGender!=null && !strGender.equals("")) {
				hmFilter.put("GENDER", strGender);
			} else {
				hmFilter.put("GENDER", "Select Gender");
			}
		} else {
			hmFilter.put("GENDER", "Select Gender");
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
	
	

	public String viewForm3PT(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")) {
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
			String stMnth = uF.parseToInt(getStrMonth())<10 ? "0"+getStrMonth() : getStrMonth();
			String strDate = "01/"+stMnth+"/"+uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy");
			if (uF.parseToInt(getStrMonth()) >=1 && uF.parseToInt(getStrMonth()) <= 3){
				strDate = "01/"+stMnth+"/"+uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy");
			}
//			System.out.println("strDate=====>"+strDate);
			String strQuarter = "";
			if (uF.parseToInt(getStrMonth()) >=4 && uF.parseToInt(getStrMonth()) <= 6){
				strQuarter = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy")+"/Q1/"+uF.getMonth(uF.parseToInt(getStrMonth()));
			} else if (uF.parseToInt(getStrMonth()) >=7 && uF.parseToInt(getStrMonth()) <= 9){
				strQuarter = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy")+"/Q2/"+uF.getMonth(uF.parseToInt(getStrMonth()));
			} else if (uF.parseToInt(getStrMonth()) >=10 && uF.parseToInt(getStrMonth()) <= 12){
				strQuarter = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT,"yyyy")+"/Q3/"+uF.getMonth(uF.parseToInt(getStrMonth()));
			} else {
				strQuarter = uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT,"yyyy")+"/Q4/"+uF.getMonth(uF.parseToInt(getStrMonth()));
			}
			request.setAttribute("strQuarter", strQuarter);
			
			String[]  strPaidDates = CF.getPayCycleFromDate(con, strDate, CF.getStrTimeZone(), CF, getF_org());
			request.setAttribute("paidfrom", strPaidDates[0]);
			request.setAttribute("paidto", strPaidDates[1]);
			
			pst = con.prepareStatement("select * from org_details where org_id = ? ");
			pst.setInt(1, uF.parseToInt(getF_org()));
			rs = pst.executeQuery();
			Map<String, String> hmOrg = new HashMap<String, String>();
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
			
			pst = con.prepareStatement("select * from deduction_details_india where state_id=? and financial_year_from=? and financial_year_to=? and gender =?");
			pst.setInt(1, uF.parseToInt(getF_state()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(4, getStrGender());
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmPTSlab=new LinkedHashMap<String, Map<String, String>>();
			String strPtAmt = "";
			while (rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("DEDUCTION_ID", rs.getString("deduction_id"));
				hmInner.put("INCOME_FROM", rs.getString("income_from"));
				hmInner.put("INCOME_TO", rs.getString("income_to"));
				hmInner.put("STATE_ID", rs.getString("state_id"));
				hmInner.put("DEDUCTION_AMOUNT", rs.getString("deduction_amount"));
				hmInner.put("DEDUCTION_PAYCYCLE", rs.getString("deduction_paycycle"));
				
				hmPTSlab.put(rs.getString("deduction_paycycle"), hmInner);
				if(strPtAmt.equals("")){
					strPtAmt = rs.getString("deduction_paycycle");
				} else {
					strPtAmt += ","+ rs.getString("deduction_paycycle");
				}
			}
			rs.close();
			pst.close();  	
			
//			System.out.println("hmPTSlab=====>"+hmPTSlab);
//			System.out.println("strPtAmt=====>"+strPtAmt);
			
			if(strPtAmt!=null && !strPtAmt.equals("")){
				String strChalanNo = "";
				pst = con.prepareStatement("select distinct(challan_no) from challan_details where financial_year_from_date = ? " +
						"and financial_year_to_date = ? and challan_type=?  and month like '%,"+getStrMonth()+",%' and is_paid=true " +
						"and emp_id in (select eod.emp_id from employee_official_details eod,employee_personal_details epd " +
						"where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.wlocation_id in (select wlocation_id " +
						"from work_location_info where wlocation_state_id=?) and epd.emp_gender=?)");
				pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, PROFESSIONAL_TAX);
				pst.setInt(4, uF.parseToInt(getF_org()));
				pst.setInt(5, uF.parseToInt(getF_state()));
				pst.setString(6, getStrGender());
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					strChalanNo = rs.getString("challan_no");
				}
				rs.close();
				pst.close();
				request.setAttribute("strChalanNo", strChalanNo);
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select count(eod.emp_id) as empcnt, amount,sum(amount) as totalamt from payroll_generation pg,employee_official_details eod " +
						"where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id =? and is_paid=true " +
						"and pg.emp_id=eod.emp_id and org_id=? and amount in ("+strPtAmt+",300) ");
				sbQuery.append(" and pg.emp_id in (select emp_id from challan_details where financial_year_from_date = ? and financial_year_to_date = ? and challan_type=? ");
				sbQuery.append(" and month like '%,"+getStrMonth()+",%'");
				sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_official_details eod,employee_personal_details epd " +
						"where epd.emp_per_id=eod.emp_id and eod.org_id=? and eod.wlocation_id in (select wlocation_id from work_location_info " +
						"where wlocation_state_id=?) and epd.emp_gender=? )) and eod.wlocation_id in (select wlocation_id from work_location_info " +
						"where wlocation_state_id=?) ");
				sbQuery.append(" group by amount");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(getStrMonth()));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(4, PROFESSIONAL_TAX);
				pst.setInt(5, uF.parseToInt(getF_org()));
				pst.setDate(6,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(7,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(8, PROFESSIONAL_TAX);
				pst.setInt(9, uF.parseToInt(getF_org()));
				pst.setInt(10, uF.parseToInt(getF_state()));
				pst.setString(11, getStrGender());
				pst.setInt(12, uF.parseToInt(getF_state()));
//				System.out.println("pst=====>"+pst);
				Map<String, Map<String, String>> hmPTDetails = new LinkedHashMap<String, Map<String, String>>();
				rs = pst.executeQuery();
				while (rs.next()){
					Map<String, String> hmInner = new HashMap<String, String>();
					hmInner.put("EMP_COUNT", rs.getString("empcnt"));
					hmInner.put("AMOUNT", rs.getString("amount"));
					hmInner.put("TOTAL_AMOUNT", rs.getString("totalamt"));
					
					hmPTDetails.put(rs.getString("amount"), hmInner);
				}
				rs.close();
				pst.close();	
				request.setAttribute("hmPTDetails", hmPTDetails);
//				System.out.println("hmPTDetails=====>"+hmPTDetails);
				
			}
			
			request.setAttribute("hmPTSlab", hmPTSlab);
			request.setAttribute("hmOrg", hmOrg);
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

	public List<FillState> getStateList() {
		return stateList;
	}


	public void setStateList(List<FillState> stateList) {
		this.stateList = stateList;
	}


	public String getF_state() {
		return f_state;
	}


	public void setF_state(String f_state) {
		this.f_state = f_state;
	}


	public String getStrGender() {
		return strGender;
	}


	public void setStrGender(String strGender) {
		this.strGender = strGender;
	}


	public List<FillGender> getGenderList() {
		return genderList;
	}


	public void setGenderList(List<FillGender> genderList) {
		this.genderList = genderList;
	}

}