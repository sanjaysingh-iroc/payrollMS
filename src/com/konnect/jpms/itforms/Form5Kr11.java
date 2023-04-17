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
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form5Kr11 extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	
	String financialYear;
	String f_org;
	String f_strWLocation;
	
	List<FillFinancialYears> financialYearList; 
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	
	String formType;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/itforms/Form5Kr11.jsp");
		request.setAttribute(TITLE, "Form 5 Rule 11");
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		loadForm5Kr11();
		viewForm5Kr11();
		
		if(getFormType()!=null && getFormType().equals("pdf")){
			generateForm5Kr11Pdf();
			return "";
		}
		
		return LOAD;

	}
	
	
	private void generateForm5Kr11Pdf() {
		try{
			UtilityFunctions uF = new UtilityFunctions();
			
			String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
			String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
			
			Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmWorkLocation = (Map<String, Map<String, String>>) request.getAttribute("hmWorkLocation");
			if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmLocation = hmWorkLocation .get(getF_strWLocation());
			if(hmLocation == null) hmLocation = new HashMap<String, String>();
			
			
			Map<String, String> hmMonthChallan = (Map<String, String>) request.getAttribute("hmMonthChallan");
			if(hmMonthChallan == null) hmMonthChallan = new LinkedHashMap<String, String>();
			Map<String, String> hmMonthChallanDate = (Map<String, String>) request.getAttribute("hmMonthChallanDate");
			if(hmMonthChallanDate == null) hmMonthChallanDate = new LinkedHashMap<String, String>();
			Map<String, String> hmMonthChallanNo = (Map<String, String>) request.getAttribute("hmMonthChallanNo");
			if(hmMonthChallanNo == null) hmMonthChallanNo = new LinkedHashMap<String, String>();
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.TIMES_ROMAN,8);
			Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN,8,Font.BOLD);
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
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("[See Rule 11]",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("                     Return of tax payable by employer under sub-section(1) of Section 6 of the Karnataka Tax on Professions, Trades, Callings and Employment Act 1976.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setLeading(15f, 0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         1. Return of tax payable for the year ending on "+uF.showData(uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, CF.getStrReportDateFormat()), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         2. Name of the employer "+uF.showData(hmOrg.get("ORG_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         3. Address: "+uF.showData(hmOrg.get("ORG_ADDRESS"), "")+"-"+uF.showData(hmOrg.get("ORG_PINCODE"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         4. Registration Certificate No.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         5. Tax paid during the year as under",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("            _______________________________________________________________________________________________________________",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);

	        	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("1. Form 5 substitued by Notification No. FD8 CPT 95, dated 7-8-1995, w.e.f. 8-8-1995 (GSR 102).",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
//	        //new Table
//	        PdfPTable tableInner = new PdfPTable(6);
//	        tableInner.setWidthPercentage(100);   
	        
			 //New Row
	        row1 =new PdfPCell(new Paragraph("SI No.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Month",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Tax Deducted",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Tax Paid",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Balance Tax",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("Paid Under Challan No. & Date",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("1",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("2",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("3",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("4",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("5",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("6",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
	        
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        table.addCell(row1);
			int j = 4;
	        for (int i = 1; i<=12; i++, j++){
	        	if(j==13){
					j=1;
				}
	        	 //New Row
		        row1 =new PdfPCell(new Paragraph(""+i,small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(uF.getMonth(j),small));
		        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph("",small));
		        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(uF.showData(hmMonthChallan.get(""+j), ""),small));
		        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph("",small));
		        row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
		        
		        row1 =new PdfPCell(new Paragraph(uF.showData(hmMonthChallanNo.get(""+j), "")+ " - "+uF.showData(hmMonthChallanDate.get(""+j), ""),small));
		        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
		        row1.setPadding(2.5f);
		        row1.setPaddingTop(8.0f);
		        table.addCell(row1);
	        }  
//	        table.addCell(tableInner); //end inner table
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         6. Total Tax payable for the year ending",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         7. Tax paid as per monthly statement",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         8. Balance tax payable",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("         9. Balance tax paid under challan No.                      Date.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        StringBuilder sbMsg = new StringBuilder();
	        sbMsg.append("I certify that all the employees who are liable to pay the tax in my employee during the period of return have been covered by the foregoing particulars.");
	        sbMsg.append("I also certify that the necessary revision in the amount of tax deductable from the salary or wages of the employees on account of variation in the salary " +
	        		"or wages earned by them has been made where necessary.");
	        row1 =new PdfPCell(new Paragraph(sbMsg.toString(),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setLeading(15f, 0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("I, Shri                                solemnly declare that the above stateents are true to the best of my knowledge and belief.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setLeading(15f, 0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Place: "+uF.showData(hmLocation.get("WL_NAME"), ""),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);

	        row1 =new PdfPCell(new Paragraph("Signature\n\n(Employer)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("Date: "+ uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT),small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);

	        row1 =new PdfPCell(new Paragraph("Status:",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("(For OFFICIAL USE)",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        //New Row
	        row1 =new PdfPCell(new Paragraph("The return is accepted on verification",small));
	        row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Tax assessed",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);

	        row1 =new PdfPCell(new Paragraph("Rs.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Tax paid",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);

	        row1 =new PdfPCell(new Paragraph("Rs.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Balance",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);

	        row1 =new PdfPCell(new Paragraph("Rs.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);

	        row1 =new PdfPCell(new Paragraph("\n\nAssessing Authority.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setColspan(3);
	        table.addCell(row1);
	        
	      //New Row
	        row1 =new PdfPCell(new Paragraph("Note: Where the return is not accepitable separate order of assessment should be passed.",small));
	        row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
	        row1.setPadding(2.5f);
	        row1.setPaddingTop(8.0f);
	        row1.setLeading(15f, 0f);
	        row1.setColspan(6);
	        table.addCell(row1);
	        
	        
	        document.add(table);
	        
	        document.close();
	          
			String filename="Form5Kr11.pdf";
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


	public String loadForm5Kr11(){
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		orgList = new FillOrganisation(request).fillOrganisation();
		orgList = new FillOrganisation(request).fillOrganisation();
		if(getF_org()==null && orgList!=null && orgList.size()>0){
			setF_org(orgList.get(0).getOrgId());
		}
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		if(getF_strWLocation()!=null && (wLocationList!=null && wLocationList.size()>0)){
			boolean flag = false;
			for(int i = 0 ; i < wLocationList.size(); i++ ){
				if(wLocationList.get(i).getwLocationId().equals(getF_strWLocation())){
					flag = true;
				}
			}
			
			if(!flag){
				setF_strWLocation(wLocationList.get(0).getwLocationId());
			}
			
		}else if(getF_strWLocation()==null && wLocationList!=null && wLocationList.size()>0){
			setF_strWLocation(wLocationList.get(0).getwLocationId());
		}
		return LOAD;
	}
	
	

public String viewForm5Kr11(){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			String strMonth = null;
			
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
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con,uF.parseToInt(getF_org()));
			request.setAttribute("hmWorkLocation",hmWorkLocation);
			
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
				hmOrg.put("ORG_ESTABLISH_CODE_NO", rs.getString("establish_code_no"));
			}
			rs.close();
			pst.close();  
			
			String[] tmpMonths = new String[] {"1","2","3","4","5","6","7","8","9","10","11","12"};
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select paid_date,month,sum(amount) as amount,challan_no from challan_details where financial_year_from_date = ? and financial_year_to_date = ? " +
					"and challan_type in ("+PROFESSIONAL_TAX+") ");
			sbQuery.append(" and (");
            for(int i=0; i<tmpMonths.length; i++){
                sbQuery.append(" month like '%,"+tmpMonths[i]+",%'");
                
                if(i<tmpMonths.length-1){
                    sbQuery.append(" OR "); 
                }
            }
            sbQuery.append(" ) ");
			sbQuery.append(" and is_paid=true and emp_id in (select eod.emp_id from employee_personal_details epd,employee_official_details eod " +
					"where eod.emp_id = epd.emp_per_id and org_id=?) group by paid_date,month,challan_no");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getF_org()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			Map<String,String> hmMonthChallanDate = new LinkedHashMap<String, String>();
			Map<String,String> hmMonthChallan = new LinkedHashMap<String, String>();
			Map<String,String> hmMonthChallanNo = new LinkedHashMap<String, String>();
			while(rs.next()){
				String paid_date = uF.getDateFormat(rs.getString("paid_date"), DBDATE,DATE_FORMAT);
				String[] tmpMonths1 = rs.getString("month").substring(1, rs.getString("month").length()-1).split(",");
				for(String strMonth1 : tmpMonths1 ){
					hmMonthChallanDate.put(strMonth1, paid_date);
					hmMonthChallan.put(strMonth1, ""+ Math.round(uF.parseToDouble(rs.getString("amount"))));
					hmMonthChallanNo.put(strMonth1, ""+ rs.getString("challan_no"));
				}
			}
			rs.close();
			pst.close(); 
//			System.out.println("hmMonthChallanDate====>"+hmMonthChallanDate);
//			System.out.println("hmMonthChallan====>"+hmMonthChallan);
//			System.out.println("hmMonthChallanNo====>"+hmMonthChallanNo);
			
			request.setAttribute("hmOrg", hmOrg);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmMonthChallan", hmMonthChallan);
			request.setAttribute("hmMonthChallanDate", hmMonthChallanDate);
			request.setAttribute("hmMonthChallanNo", hmMonthChallanNo);
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


	public String getFinancialYear() {
		return financialYear;
	}


	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
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


	public String getFormType() {
		return formType;
	}


	public void setFormType(String formType) {
		this.formType = formType;
	}
	


}
