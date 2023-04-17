package com.konnect.jpms.challan;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author konnect
 *
 */
public class LWFUpdateChallanData extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null; 
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String financialYear;
	String strEmpId;
	String[] strMonth;
	String totalMonths;
	String challanType;
	String strSubmit;
	String[] empIds;
	int totalTaxAmount;  
	int totalNumOfEmployee;    
	double totalAmount;
	String paidDate;
	String challanDate;
	String challanNum;
	String cheque_no;
	String operation;
	double totalContribution;
	int employerContribution;
	int employeeContribution;
	String strFinancialYearStart;
	String sessionEmp_Id;
	
	String f_org;
//	String f_state;
	String f_strWLocation;
	String stateId;
	String sbEmp;
	
	
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		sessionEmp_Id=(String) session.getAttribute(EMPID);
		System.out.println("stateid in LWFUpdateChallanData==>"+stateId);
		
		if(getChallanDate()!=null && getOperation()!=null && getOperation().equalsIgnoreCase("del")){
			deleteChallanDetails();
		}else if(getChallanDate()!=null && getChallanNum()!=null && getOperation().equalsIgnoreCase("update")){
			updateChallanNumber();
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("insert")){
			insertUnpaidAmount();
		}else if(getOperation()!=null && getOperation().equalsIgnoreCase("pdf")){
			getOrganizationDetails();
			if(getStateId()!=null && getStateId().equals("21")){
				viewLWFChallanPdfForKaranataka();
				generateLWFPdfKarnataka();
			} else {
				viewLWFChallanPdfReports();                    
				generateLWFTaxChallanPdfReports(); 
			}
			
		}
		
			return SUCCESS;
		
	}
	private void generateLWFPdfKarnataka() {
		try{
			UtilityFunctions uF=new UtilityFunctions();
			Map<String,String> hmLwfContribution=(Map<String,String>)request.getAttribute("hmLwfContribution");
			Map<String, Map<String, String>> hmLwfDetails=(Map<String, Map<String, String>>)request.getAttribute("hmLwfDetails");
			if(hmLwfDetails==null)hmLwfDetails=new HashMap<String, Map<String,String>>();
		
			
			
			Map<String,String> hmInclusive=hmLwfDetails.get("INCLUSIVE"); 
			if(hmInclusive==null) hmInclusive=new HashMap<String, String>();
			Map<String,String> hmExceeding=hmLwfDetails.get("EXCEEDING"); 
			if(hmExceeding==null) hmExceeding=new HashMap<String, String>();
			
			Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");
			
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 14,Font.BOLD);
			Font small = new Font(Font.FontFamily.TIMES_ROMAN,9);
			Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.BOLD);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4);
	        PdfWriter.getInstance(document,buffer);
	        document.open();
	        
	        PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(100);        
	        
			// New Row
			PdfPCell row1 =new PdfPCell(new Paragraph("",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("TO,",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(4);
		    table.addCell(row1);
		    
		    row1 =new PdfPCell(new Paragraph("Date:",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
		 // New Row
		    String strToAddress = "The Welfare Commissioner\n" +
		    		"Karnataka Labour Wefare Board\n" +
		    		"Karmika Kalyan Bhavana\n" +
		    		"#48,2nd, floor Mathikere Main Road\n" +
		    		"Yashvanthapura (Near RTO Office)\n" +
		    		"Bangalore-560022";
			row1 =new PdfPCell(new Paragraph(strToAddress,small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
		    row1 =new PdfPCell(new Paragraph("",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(4);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("sir,",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("Sub:-Contribution for the year           Regarding",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(10.0f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("I am here with submitting the details as below, under per Section 7A read with rule 3A of Karnataka" +
					" Labour Welfare Fund Act 1965,& Rules 1968, EST/Registration No.                 (mention This number while corresponding)",small));
		    row1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(10.0f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("Yours faithfully\n\n\n\n",small));
		    row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("(Employer)",small));
		    row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("FORM-D",smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("(Rule-3-A)",small));
		    row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("KARANATAKA LABOUR WELFARE FUND",smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("STATEMENT OF EMPLOYER'S & EMPLOYEE'S CONTRIBUTION TO BE SENT BY THE EMPLOYER BY 15th JANUARY   ",smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		    // New Row
			row1 =new PdfPCell(new Paragraph("1) Name & Address of the Establishment\nTotal no. of units to be mentioned",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(":",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), ""),small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(3);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("2) Name of the Employer",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(":",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), ""),small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(3);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("3) Total No, of the Employee's Whose Name's stand in the Establishment Register as on 31st December    ",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(":",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph("",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(3);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("4) Employee's Contribution @ Rs. "+uF.parseToInt(hmInclusive.get("EELWF_CONTRIBUTION")),small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(":",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    int eecInclusiveRs=uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT"))*uF.parseToInt(hmInclusive.get("EELWF_CONTRIBUTION"));
			row1 =new PdfPCell(new Paragraph(""+eecInclusiveRs,small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(3);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("5) Employer's Contribution @ Rs. "+uF.parseToInt(hmInclusive.get("ERLWF_CONTRIBUTION")),small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1); 
		    
			row1 =new PdfPCell(new Paragraph(":",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
		    int ercInclusiveRs=uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT"))*uF.parseToInt(hmInclusive.get("ERLWF_CONTRIBUTION"));
			row1 =new PdfPCell(new Paragraph(""+ercInclusiveRs,small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(3);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("6) Total of Items 4 & 5",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(":",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(""+(eecInclusiveRs+ercInclusiveRs),small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(3);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("7) Whether the Contribution is sent by Cheque, Bank Draft, Crossed Demand Draft",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph(":",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph("DD/Cheque No."+getCheque_no()+"        date: ",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(3);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("   In Favour of Welfare Commissioner Bangalore",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph("",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph("Amount Rs."+""+(eecInclusiveRs+ercInclusiveRs)+"            Bank Name",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setLeading(10f, 0f);
		    row1.setColspan(3);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("\n\n",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("Signature of Employer and Seal",small));
		    row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		    // New Row
			row1 =new PdfPCell(new Paragraph("(Please return the form duly filled for 200 )",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		    // New Row
			row1 =new PdfPCell(new Paragraph("----------------------------------------------------------------------------cut hear------------------------------------------------------------------------------------",small));
		    row1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("Please return the form duly filled to the above address,",small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(4);
		    table.addCell(row1);
		    
			row1 =new PdfPCell(new Paragraph("Date:"+uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT),small));
		    row1.setHorizontalAlignment(Element.ALIGN_LEFT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(2);
		    table.addCell(row1);
		    
		 // New Row
			row1 =new PdfPCell(new Paragraph("\n\n\n\nWelfare Commissioner",smallBold));
		    row1.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        row1.setBorder(Rectangle.NO_BORDER);
		    row1.setPadding(2.5f);
		    row1.setColspan(6);
		    table.addCell(row1);
			
			document.add(table);
			document.close();
			
			
			String filename="LWFChallan_"+uF.getDateFormat(getChallanDate(),DBDATE, "MM")+"_"+uF.getDateFormat(getChallanDate(),DBDATE, "yyyy")+".pdf";
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename="+filename);
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out); 
			out.flush();
				
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
	private void viewLWFChallanPdfForKaranataka() {
		
		System.out.println("in viewLWFChallanPdfForKaranataka");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
//		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		
		try{
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
		StringBuilder sb=null;
		
			if (getChallanNum() != null) {
				/*pst = con.prepareStatement("select distinct(month) from challan_details where challan_no=? and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") " +
						"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
				*/
				
				sb.append("select distinct(month) from challan_details where challan_no=? and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") " +
						"and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
			
				pst.setString(1, getChallanNum());
				pst.setBoolean(2, true);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				
				System.out.println("pst 1 in viewLWFChallanPdfForKaranataka"+pst);
			} else {

				/*
				pst = con.prepareStatement("select distinct(month) from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") " +
						"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
			*/
				
				sb=new StringBuilder();
				sb.append(" select distinct(month) from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") " +
						"and is_paid=?");
				
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(2, false);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				
				System.out.println("pst 2 in viewLWFChallanPdfForKaranataka"+pst);

			}
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();   
			List monthList = new ArrayList();
			while (rs.next()) {
				String[] monthsArray = rs.getString("month").split(",");
				for (int i = 0; i < monthsArray.length; i++) {
					if (!monthList.contains(monthsArray[i])) {
						monthList.add(monthsArray[i]);
					}
				}
			}
	        rs.close();
	        pst.close();

			String month = "";
			for (int i = 1; i < monthList.size(); i++) {
				if (i == monthList.size() - 1) {
					month += monthList.get(i);
				} else
					month += monthList.get(i) + ",";
			}
		
			totalNumOfEmployee=0;
		
			List empIdList = new ArrayList();
			
			if (getChallanNum() != null) {
				/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,cheque_no from challan_details where challan_no=? " +
						"and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") and is_paid=? " +
						"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
*/				
				sb=new StringBuilder();
				sb.append("select distinct(emp_id) as emp_id,cheque_no from challan_details where challan_no=? " +
						"and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") and is_paid=?");
				
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
				pst.setString(1, getChallanNum());
				pst.setBoolean(2, true);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				System.out.println("pst 3 in viewLWFChallanPdfForKaranataka"+pst);
			} else {
				/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,cheque_no from challan_details where entry_date=? " +
						"and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") and is_paid=? " +
						"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
			*/
				sb=new StringBuilder();
				sb.append("select distinct(emp_id) as emp_id,cheque_no from challan_details where entry_date=? " +
						"and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(2, false);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				System.out.println("pst 4 in viewLWFChallanPdfForKaranataka"+pst);
			}
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			StringBuilder empid_sb=new StringBuilder(); 
			while (rs.next()) {
				totalNumOfEmployee = totalNumOfEmployee + 1;
				
				String[] arr = rs.getString("emp_id").split(",");
				
				for(int i=0; arr!=null && i<arr.length; i++){
					if(arr[i]!=null && arr[i].length()>0){
						empid_sb.append(arr[i]+",");
					}
				}
					setCheque_no(rs.getString("cheque_no"));
				
			}
	        rs.close();
	        pst.close();
			empid_sb.replace(0, empid_sb.length(), empid_sb.substring(0, empid_sb.length()-1));
			
			
			
			Map<String,String> hmLwfContribution=new HashMap<String, String>();
			
			
			if (getChallanNum() != null) {
				/*pst = con.prepareStatement("select entry_date from challan_details where financial_year_from_date=? "
								+ "and financial_year_to_date=? and challan_no=? and is_paid=? " +
								"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) ");
			*/
				sb=new StringBuilder();
				sb.append("select entry_date from challan_details where financial_year_from_date=? "
								+ "and financial_year_to_date=? and challan_no=? and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, getChallanNum());
				pst.setBoolean(4, true);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
//				System.out.println("pst new=====>"+pst);
				System.out.println("pst 5 in viewLWFChallanPdfForKaranataka"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					setChallanDate(rs.getString("entry_date"));
				}
		        rs.close();
		        pst.close();
				
				
				/*pst = con.prepareStatement("select emp_id from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?))" +
						" and financial_year_start=? and financial_year_end=?" +
						" group by  emp_id");
				*/
				
		        sb=new StringBuilder();
		        sb.append("select emp_id from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=?");
		        if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in (0))");
				}
				sb.append(" and financial_year_start=? and financial_year_end=?" +
						" group by  emp_id");
				
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, getChallanNum());
				pst.setBoolean(4, true);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println("pst 6 in viewLWFChallanPdfForKaranataka"+pst);
				rs = pst.executeQuery();
				
				while (rs.next()) {
					int empMinLimit=uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT"));
					empMinLimit++;
					hmLwfContribution.put("EMP_MIN_LIMIT",""+empMinLimit);
				}
		        rs.close();
		        pst.close();
				
				/*pst = con.prepareStatement("select emp_id from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?))" +
						" and financial_year_start=? and financial_year_end=?" +
						" group by  emp_id");*/
				
				sb=new StringBuilder();
				sb.append("select emp_id from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=?");
				 if(sbEmp!=null && !sbEmp.equals("")){
						sb.append(" and emp_id in ("+sbEmp+"))");
					}else{
						sb.append(" and emp_id in (0))");
					}
				sb.append(" and financial_year_start=? and financial_year_end=?" +
						" group by  emp_id");
				pst=con.prepareStatement(sb.toString());
				 
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, getChallanNum());
				pst.setBoolean(4, true);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println("pst 7 in viewLWFChallanPdfForKaranataka"+pst);

				rs = pst.executeQuery();
				
				while (rs.next()) {
					int erMaxLimit=uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT"));
					erMaxLimit++;
					hmLwfContribution.put("ER_MAX_LIMIT",""+erMaxLimit);
				}
		        rs.close();
		        pst.close();
				
				
				
			} else {
				
				
				/*pst = con.prepareStatement("select emp_id from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?))" +
						" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit<=3000 order by _month");
				*/
				
				sb=new StringBuilder();
				sb.append("select emp_id from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in (0))");
				}
				sb.append(" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit<=3000 order by _month");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(4, false);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println("pst 8 in viewLWFChallanPdfForKaranataka"+pst);

				rs = pst.executeQuery();
				
				while (rs.next()) {
					int empMinLimit=uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT"));
					empMinLimit++;
					hmLwfContribution.put("EMP_MIN_LIMIT",""+empMinLimit);
				}
		        rs.close();
		        pst.close();
				
				/*pst = con.prepareStatement("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)) " +
						" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit>3000 order by _month");
				*/
				sb=new StringBuilder();
				sb.append("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=?");
				
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in (0))");
				}
				sb.append(" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit>3000 order by _month");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(4, false);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println("pst 9 in viewLWFChallanPdfForKaranataka"+pst);

				rs = pst.executeQuery();
				
				while (rs.next()) {
					int erMaxLimit=uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT"));
					erMaxLimit++;
					hmLwfContribution.put("ER_MAX_LIMIT",""+erMaxLimit);
				}
		        rs.close();
		        pst.close();
				
			}		
			
			request.setAttribute("hmLwfContribution", hmLwfContribution);
			
			pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? and org_id=?");
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStateId()));
			pst.setInt(4, uF.parseToInt(getF_org()));
//			System.out.println("pst new=====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmLwfDetails=new HashMap<String, Map<String,String>>();
			while (rs.next()) {
				Map<String, String> hmInner=new HashMap<String, String>();
				hmInner.put("LWF_ID", rs.getString("lwf_id"));
				hmInner.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
				hmInner.put("FINANCIAL_YEAR_START", uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT));
				hmInner.put("FINANCIAL_YEAR_END", uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT));
				hmInner.put("EELWF_CONTRIBUTION", rs.getString("eelfw_contribution"));
				hmInner.put("ERLWF_CONTRIBUTION", rs.getString("erlfw_contribution"));
				hmInner.put("USER_ID", rs.getString("user_id"));
				hmInner.put("ENTRY_TIME", uF.getDateFormat(rs.getString("entry_timestamp"), DBDATE, DATE_FORMAT));
				hmInner.put("MIN_LIMIT", rs.getString("min_limit"));
				hmInner.put("MAX_LIMIT", rs.getString("max_limit"));
				hmInner.put("STATE_ID", rs.getString("state_id"));
				hmInner.put("MONTHS", rs.getString("months"));
				
				if(uF.parseToDouble(rs.getString("max_limit"))<=3000){
					hmLwfDetails.put("INCLUSIVE",hmInner);
				}else{
					hmLwfDetails.put("EXCEEDING",hmInner);
				}
			} 
	        rs.close();
	        pst.close(); 
			request.setAttribute("hmLwfDetails",hmLwfDetails);
			
			pst = con.prepareStatement("select * from org_details where org_id=? ");
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
				hmOrg.put("ORG_ESTABLISH_CODE", rs.getString("establish_code_no"));
			}  
	        rs.close();
	        pst.close();
			request.setAttribute("hmOrg",hmOrg);
			
		
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	private void getOrganizationDetails() {
		UtilityFunctions uF = new UtilityFunctions();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db=new Database();
		db.setRequest(request);
		

		try {
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con,uF.parseToInt(getF_org()));
			
			
			pst = con.prepareStatement("select wlocation_state_id from work_location_info where wlocation_id=?");
			pst.setInt(1, uF.parseToInt(getF_strWLocation()));
			rs = pst.executeQuery();
			if(rs.next()){
				setStateId(rs.getString("wlocation_state_id"));
			}
	        rs.close();
	        pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	public void generateLWFTaxChallanPdfReports(){
		System.out.println("in generateLWFTaxChallanPdfReports");
		
		try {
			
			ByteArrayOutputStream buffer = generatePdfDocument();
			
			response.setContentType("application/pdf");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=LWFChallan.pdf");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out); 
			out.flush();
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
		private ByteArrayOutputStream generatePdfDocument() {
			
			System.out.println("in generatePdfDocument");
			
			String FILE = "//home/user/Desktop/GeneratedLWFChallan.pdf";
			Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 13);
			Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 11);
			Font normalwithbold = new Font(Font.FontFamily.TIMES_ROMAN, 11,Font.BOLD);
			Font small = new Font(Font.FontFamily.TIMES_ROMAN,7);
			Font italicEffect = new Font(Font.FontFamily.TIMES_ROMAN,9,Font.ITALIC); 
			Paragraph centerAligned = new Paragraph();
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			UtilityFunctions uF=new UtilityFunctions();

			Document document = new Document();
			try{
				
					Map<String,String> hmLwfContribution=(Map<String,String>)request.getAttribute("hmLwfContribution");
					
					System.out.println("hmLwfContribution==>"+hmLwfContribution);
					
					Map<String, Map<String, String>> hmLwfDetails=(Map<String, Map<String, String>>)request.getAttribute("hmLwfDetails");
					if(hmLwfDetails==null)hmLwfDetails=new HashMap<String, Map<String,String>>();
					
					
					System.out.println(" hmLwfDetails in generatePdfDocument==>"+hmLwfDetails);
					
					Map<String,String> hmInclusive=hmLwfDetails.get("INCLUSIVE"); 
					if(hmInclusive==null) hmInclusive=new HashMap<String, String>();
					System.out.println(" hmInclusive in generatePdfDocument==>"+hmInclusive);

					Map<String,String> hmExceeding=hmLwfDetails.get("EXCEEDING"); 
					if(hmExceeding==null) hmExceeding=new HashMap<String, String>();
					System.out.println(" hmExceeding in generatePdfDocument==>"+hmExceeding);

					
					Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");
					System.out.println("hmOrg==>"+hmOrg);
					
//					PdfWriter.getInstance(document, new FileOutputStream(FILE));
					PdfWriter.getInstance(document, buffer);
					document.open();
							
					
					Paragraph blankSpace = new Paragraph("  ");
					Paragraph name = new Paragraph("MAHARASHTRA LABOUR WELFARE BOARD",heading);
					name.setAlignment(Element.ALIGN_CENTER);
					Paragraph address = new Paragraph("Hutatma Babu Genu, Mumbai Girni Kamgar Bhavan, Senapati BAPAT MAGR",normal);
					address.setAlignment(Element.ALIGN_CENTER);
					Paragraph contact = new Paragraph("Elphinstone, Mumbai - 400 013.Phone : 4227758, 4306717, 4360738",normal);
					contact.setAlignment(Element.ALIGN_CENTER);
					Paragraph formTitle = new Paragraph("FORM A-1 CUM RETURN",normalwithbold);
					formTitle.setAlignment(Element.ALIGN_CENTER);
					Paragraph rule = new Paragraph("( Vide Rule 3-A )");
					rule.setAlignment(Element.ALIGN_CENTER);
					
					PdfPTable noteTable = new PdfPTable(2);
					noteTable.setWidthPercentage(100);
					int[] noteTableCols = {70,30};
					noteTable.setWidths(noteTableCols);
					
					PdfPCell firstRow1 = new PdfPCell(new Paragraph("NOTE: 1) This Form-Cum Return Is Required To Be Submitted By Every Employer"+
								"Along With The Payment Of Employee’s & Employer’s Six Monthaly ",normal));
					disableColumns(firstRow1);
									
					PdfPCell firstRow2 = new PdfPCell(new Paragraph("for office use only",normal));
					firstRow2.setVerticalAlignment(Element.ALIGN_BOTTOM);
					firstRow2.setHorizontalAlignment(Element.ALIGN_CENTER);
					firstRow2.disableBorderSide(Rectangle.RIGHT);
					firstRow2.disableBorderSide(Rectangle.TOP);
					firstRow2.disableBorderSide(Rectangle.LEFT);
					
					PdfPCell secondRow1 = new PdfPCell(new Paragraph("Contribution Made By Him In Respect Of All Employees Whose Names Stand On The Register"+
								" Of His Establishment As On 30th June / 31st December As Per The Provisions Of"+
								" Section6bb Of The Bombay Labour Walfare Fund Act, 1953",normal));
					secondRow1.setVerticalAlignment(Element.ALIGN_TOP);
					secondRow1.disableBorderSide(Rectangle.LEFT);
					secondRow1.disableBorderSide(Rectangle.TOP);
					secondRow1.disableBorderSide(Rectangle.BOTTOM);
					
					PdfPCell secondRow2 = new PdfPCell(new Paragraph("C"));
					secondRow2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					
					PdfPCell thirdRow1 = new PdfPCell(new Paragraph("2) Section 2 (2) (B) Of Bombay Labour Welfare Fund Act, 1953 “Supervisor”"+
								" Means Who, Being Employed In Asupervisory Capacity, Draws Wages"+
								" Exceeding Three Thousand Five Hundred Rupees Par Mensen Or"+
								" Exercise, Either By The Nature Of The Duties Attached To The Office,"+
								" Or By Reason Of The Powers Vested In Him, Functions Mainly Of A"+
								" Managerial Nature.",normal));
					thirdRow1.setVerticalAlignment(Element.ALIGN_TOP);
					thirdRow1.disableBorderSide(Rectangle.BOTTOM);
					thirdRow1.disableBorderSide(Rectangle.LEFT);
					thirdRow1.disableBorderSide(Rectangle.TOP);
					
					
					PdfPCell thirdRow2 = new PdfPCell(new Paragraph(" "));
					
					noteTable.addCell(firstRow1);
					noteTable.addCell(firstRow2);
					noteTable.addCell(secondRow1);
					noteTable.addCell(secondRow2);
					noteTable.addCell(thirdRow1);
					noteTable.addCell(thirdRow2);
					
					Paragraph rule3 = new Paragraph("3) Eec= Employee’s Contribution, Erc= Employer’s Contribution \n",normal);
					
					PdfPTable establishmentCodeTable = new PdfPTable(2);
					establishmentCodeTable.setWidthPercentage(100);
					int[] establishCols = {25,75};
					establishmentCodeTable.setWidths(establishCols);
					
					PdfPCell establishCode1 = new PdfPCell(new Paragraph("Establishment Code No.",normalwithbold));
					establishCode1.disableBorderSide(Rectangle.BOTTOM);
					establishCode1.disableBorderSide(Rectangle.LEFT);
					establishCode1.disableBorderSide(Rectangle.TOP);
					
					PdfPCell establishCode2 = new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_ESTABLISH_CODE"), ""),normal));
					
					establishmentCodeTable.addCell(establishCode1);
					establishmentCodeTable.addCell(establishCode2);
					
					PdfPTable employementDetails = new PdfPTable(2);
					employementDetails.setWidthPercentage(100);
					int[] employementCols = {45,55};
					employementDetails.setWidths(employementCols);
									
					PdfPCell nameAndAddressOfEstablish1 = new PdfPCell(new Paragraph("1 Name & Address Of The Establishment \n\n\n\n",normal));
					disableColumns(nameAndAddressOfEstablish1);

					PdfPCell nameAndAddressOfEstablish2 = new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), "")+"\n"+uF.showData(hmOrg.get("ORG_ADDRESS"), "")+"\n",normal));
					disableColumns(nameAndAddressOfEstablish2);
													
					PdfPCell nameOfEmployer1 = new PdfPCell(new Paragraph("2 Name Of The Employer \n\n",normal));
					disableColumns(nameOfEmployer1);
									
					PdfPCell nameOfEmployer2 = new PdfPCell(new Paragraph(uF.showData(hmOrg.get("ORG_NAME"), "")+"\n",normal));
					disableColumns(nameOfEmployer2);
									
					PdfPCell establishClass1 = new PdfPCell(new Paragraph("3 Class Of The Establishment", normal));
					disableColumns(establishClass1);
									
					PdfPCell establishClass2 = new PdfPCell(new Paragraph(" "));
					disableColumns(establishClass2);
					
					String year=uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy");
					PdfPCell dateClass1 = new PdfPCell(new Paragraph("on 30 June "+year+" / 31 Dec "+year, normal));
					disableColumns(dateClass1);
					
					PdfPCell dateClass2 = new PdfPCell(new Paragraph(""));
					disableColumns(dateClass2);
									
					employementDetails.addCell(nameAndAddressOfEstablish1);
					employementDetails.addCell(nameAndAddressOfEstablish2);
					employementDetails.addCell(nameOfEmployer1);
					employementDetails.addCell(nameOfEmployer2);
					employementDetails.addCell(establishClass1);
					employementDetails.addCell(establishClass2);
					employementDetails.addCell(dateClass1);
					employementDetails.addCell(dateClass2);
					
					PdfPTable employeeWagesTable = new PdfPTable(7);
					employeeWagesTable.setWidthPercentage(100);
					
					int[] empWagesCols = {40,10,12,10,10,8,10};
					employeeWagesTable.setWidths(empWagesCols);
					
					
					
				    PdfPCell row1Cell1 = new PdfPCell(new Paragraph("Total number of employees whose names stood on the establishment registed as on",normal));
				    disableColumns(row1Cell1);
				    
				    PdfPCell row1Cell2 = new PdfPCell(new Paragraph(" ",normal));
				    disableColumns(row1Cell2);
				    			    
				    PdfPCell row1Cell3 = new PdfPCell(new Paragraph("No.of Employees ",normal));
				    row1Cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row1Cell4 = new PdfPCell(new Paragraph("E.E.C. Rs",normal));
				    row1Cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row1Cell5 = new PdfPCell(new Paragraph("E.R.C.Rs",normal));
				    row1Cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row1Cell6 = new PdfPCell(new Paragraph("Penal Int",normal));
				    row1Cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row1Cell7 = new PdfPCell(new Paragraph("Total Rs.",normal));
				    row1Cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				    
				    PdfPCell row2Cell1 = new PdfPCell(new Paragraph("a. Manager",normal));
				    disableColumns(row2Cell1);
				  
				    PdfPCell row2Cell2 = new PdfPCell(new Paragraph("  ",normal));
				    disableColumns(row2Cell2);
				    
				    PdfPCell row2Cell3 = new PdfPCell(new Paragraph(" - ",normal));
				    row2Cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row2Cell4 = new PdfPCell(new Paragraph(" NIL ",normal));
				    row2Cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row2Cell5 = new PdfPCell(new Paragraph(" NIL ",normal));
				    row2Cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row2Cell6 = new PdfPCell(new Paragraph(" NIL ",normal));
				    row2Cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row2Cell7 = new PdfPCell(new Paragraph(" NIL ",normal));
				    row2Cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				    
				    PdfPCell row3Cell1 = new PdfPCell(new Paragraph("b. Employees working in supervisory capacity drawing wages Rs. 3500/- per month",normal));
				    disableColumns(row3Cell1);
				   
				    PdfPCell row3Cell2 = new PdfPCell(new Paragraph("  ",normal));
				    disableColumns(row3Cell2);
				   
				    PdfPCell row3Cell3 = new PdfPCell(new Paragraph(" - ",normal));
				    row3Cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row3Cell4 = new PdfPCell(new Paragraph("NIL",normal));
				    row3Cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row3Cell5 = new PdfPCell(new Paragraph("NIL",normal));
				    row3Cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row3Cell6 = new PdfPCell(new Paragraph("NIL",normal));
				    row3Cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row3Cell7 = new PdfPCell(new Paragraph("NIL",normal));
				    row3Cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				    
				    
				    PdfPCell row4Cell1 = new PdfPCell(new Paragraph("c. Employees drawing wages upto & inclusive of Rs. "+uF.showData(hmInclusive.get("MAX_LIMIT"), "0")+"/- Per Month. " +
				    		"(EEC@ Rs. "+uF.showData(hmInclusive.get("EELWF_CONTRIBUTION"), "0")+"/- Per Employee & ERC@ Rs."+uF.showData(hmInclusive.get("ERLWF_CONTRIBUTION"), "0")+"/- Per Employee \n\n " +
				    		"d. Employees drawing wages upto & exceeding of Rs. "+uF.showData(hmInclusive.get("MAX_LIMIT"), "0")+"/- Per Month. " +
				    		"(EEC@ Rs. "+uF.showData(hmExceeding.get("EELWF_CONTRIBUTION"), "0")+"/- Per Employee & ERC@ Rs."+uF.showData(hmExceeding.get("ERLWF_CONTRIBUTION"), "0")+"/- Per Employee \n\n 5. T o t a l o f ( a) to (c) above",normal));

				    row4Cell1.setRowspan(4);
				    disableColumns(row4Cell1);
				    
				    PdfPCell row4Cell2 = new PdfPCell(new Paragraph("\n JUNE \n",normal));
				    disableColumns(row4Cell2);
				    int nJune = uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_1")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_2")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_3")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_4")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_5")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_6"));
				    PdfPCell row4Cell3 = new PdfPCell(new Paragraph(""+(nJune > 0 ? ""+ nJune : "-")+" "));
				    row4Cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int eecInclusiveJuneRs = nJune * uF.parseToInt(hmInclusive.get("EELWF_CONTRIBUTION"));
				    PdfPCell row4Cell4 = new PdfPCell(new Paragraph(""+eecInclusiveJuneRs,normal)); //EMP_MIN_LIMIT ER_MAX_LIMIT_
				    row4Cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int ercInclusiveJuneRs = nJune * uF.parseToInt(hmInclusive.get("ERLWF_CONTRIBUTION"));
				    PdfPCell row4Cell5 = new PdfPCell(new Paragraph(""+ercInclusiveJuneRs,normal));
				    row4Cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row4Cell6 = new PdfPCell(new Paragraph("NIL",normal));
				    row4Cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row4Cell7 = new PdfPCell(new Paragraph(""+(eecInclusiveJuneRs+ercInclusiveJuneRs),normal));
				    row4Cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				    
				    PdfPCell row5Cell2 = new PdfPCell(new Paragraph("\n DEC \n",normal));
				    disableColumns(row5Cell2);
				  
				    int nDec = uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_7")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_8")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_9")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_10")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_11")) + uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_12"));
				    PdfPCell row5Cell3 = new PdfPCell(new Paragraph(" "+(nDec > 0 ? ""+ nDec : "-")+" ",normal));
				    row5Cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int eecInclusiveDecRs = nDec * uF.parseToInt(hmInclusive.get("EELWF_CONTRIBUTION"));
				    PdfPCell row5Cell4 = new PdfPCell(new Paragraph(""+eecInclusiveDecRs,normal));
				    row5Cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int ercInclusiveDecRs = nDec * uF.parseToInt(hmInclusive.get("ERLWF_CONTRIBUTION"));
				    PdfPCell row5Cell5 = new PdfPCell(new Paragraph(""+ercInclusiveDecRs,normal));
				    row5Cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row5Cell6 = new PdfPCell(new Paragraph("NIL",normal));
				    row5Cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row5Cell7 = new PdfPCell(new Paragraph(""+(eecInclusiveDecRs+ercInclusiveDecRs),normal));
				    row5Cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				    
				   /* PdfPCell row6Cell1 = new PdfPCell(new Paragraph("d. Employees drawing wages upto & inclusiove of Rs. 3000/- Per Month. (EEC@ Rs. 6/- Per Employee & ERC@ Rs.18/- Per Employee",normal));
				    disableColumns(row6Cell1);*/
				    
				    PdfPCell row6Cell2 = new PdfPCell(new Paragraph("\n JUNE \n",normal));
				    disableColumns(row6Cell2);
				   
				    int nERJune = uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_1")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_2")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_3")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_4")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_5")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_6"));
				    PdfPCell row6Cell3 = new PdfPCell(new Paragraph(" "+(nERJune > 0 ? ""+ nERJune : "-")+" "));
				    row6Cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int eecExceedingJuneRs = nERJune * uF.parseToInt(hmExceeding.get("EELWF_CONTRIBUTION"));
				    PdfPCell row6Cell4 = new PdfPCell(new Paragraph(""+eecExceedingJuneRs));
				    row6Cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int ercExceedingJuneRs = nERJune * uF.parseToInt(hmExceeding.get("ERLWF_CONTRIBUTION"));
				    PdfPCell row6Cell5 = new PdfPCell(new Paragraph(""+ercExceedingJuneRs,normal));
				    row6Cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row6Cell6 = new PdfPCell(new Paragraph("NIL"));
				    row6Cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row6Cell7 = new PdfPCell(new Paragraph(""+(eecExceedingJuneRs+ercExceedingJuneRs)));
				    row6Cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				    
				    PdfPCell row7Cell2 = new PdfPCell(new Paragraph(" \n DEC \n",normal));
				    disableColumns(row7Cell2);
				    
				    int nERDec = uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_7")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_8")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_9")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_10")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_11")) + uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_12"));
				    PdfPCell row7Cell3 = new PdfPCell(new Paragraph(" "+(nERDec > 0 ? ""+ nERDec : "-")+" "));
				    row7Cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int eecExceedingDecRs = nERDec * uF.parseToInt(hmExceeding.get("EELWF_CONTRIBUTION"));
				    PdfPCell row7Cell4 = new PdfPCell(new Paragraph(""+eecExceedingDecRs,normal));
				    row7Cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int ercExceedingDecRs = nERDec * uF.parseToInt(hmExceeding.get("ERLWF_CONTRIBUTION"));
				    PdfPCell row7Cell5 = new PdfPCell(new Paragraph(""+ercExceedingDecRs,normal));
				    row7Cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row7Cell6 = new PdfPCell(new Paragraph("NIL",normal));
				    row7Cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				    PdfPCell row7Cell7 = new PdfPCell(new Paragraph(""+(eecExceedingDecRs+ercExceedingDecRs)));
				    row7Cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				    
				    PdfPCell row8Cell2 = new PdfPCell(new Paragraph("-"));
				    row8Cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				    disableColumns(row8Cell2);
				    
				    PdfPCell row9Cell1 = new PdfPCell(new Paragraph("Total Amount of contribution paid alongwith this Form A-1-Cum-Return",normal));
				    disableColumns(row9Cell1);
				    PdfPCell row9Cell2 = new PdfPCell(new Paragraph(""));
				    disableColumns(row9Cell2);
				    int totalEmp = nJune + nDec + nERJune + nERDec;
				    PdfPCell row9Cell3 = new PdfPCell(new Paragraph(""+totalEmp));
				    row9Cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int totalEEC=eecInclusiveJuneRs+eecInclusiveDecRs+eecExceedingJuneRs+eecExceedingDecRs;
				    PdfPCell row9Cell4 = new PdfPCell(new Paragraph(""+totalEEC));
				    row9Cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int totalERC=ercInclusiveJuneRs+ercInclusiveDecRs+ercExceedingJuneRs+ercExceedingDecRs;
				    PdfPCell row9Cell5 = new PdfPCell(new Paragraph(""+totalERC));
				    row9Cell5.setHorizontalAlignment(Element.ALIGN_CENTER);				    
				    PdfPCell row9Cell6 = new PdfPCell(new Paragraph("NIL"));
				    row9Cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				    int total=eecInclusiveJuneRs+ercInclusiveJuneRs+eecInclusiveDecRs+ercInclusiveDecRs+eecExceedingJuneRs+ercExceedingJuneRs+eecExceedingDecRs+ercExceedingDecRs;
				    PdfPCell row9Cell7 = new PdfPCell(new Paragraph(""+total));
				    row9Cell7.setHorizontalAlignment(Element.ALIGN_CENTER);			    
				    
				    employeeWagesTable.addCell(row1Cell1);
				    employeeWagesTable.addCell(row1Cell2);
				    employeeWagesTable.addCell(row1Cell3);
				    employeeWagesTable.addCell(row1Cell4);
				    employeeWagesTable.addCell(row1Cell5);
				    employeeWagesTable.addCell(row1Cell6);
				    employeeWagesTable.addCell(row1Cell7);
				    
				    employeeWagesTable.addCell(row2Cell1);
				    employeeWagesTable.addCell(row2Cell2);
				    employeeWagesTable.addCell(row2Cell3);
				    employeeWagesTable.addCell(row2Cell4);
				    employeeWagesTable.addCell(row2Cell5);
				    employeeWagesTable.addCell(row2Cell6);
				    employeeWagesTable.addCell(row2Cell7);
				    
				    employeeWagesTable.addCell(row3Cell1);
				    employeeWagesTable.addCell(row3Cell2);
				    employeeWagesTable.addCell(row3Cell3);
				    employeeWagesTable.addCell(row3Cell4);
				    employeeWagesTable.addCell(row3Cell5);
				    employeeWagesTable.addCell(row3Cell6);
				    employeeWagesTable.addCell(row3Cell7);
					
				    employeeWagesTable.addCell(row4Cell1);
				    employeeWagesTable.addCell(row4Cell2);
				    employeeWagesTable.addCell(row4Cell3);
				    employeeWagesTable.addCell(row4Cell4);
				    employeeWagesTable.addCell(row4Cell5);
				    employeeWagesTable.addCell(row4Cell6);
				    employeeWagesTable.addCell(row4Cell7);
					
				    employeeWagesTable.addCell(row5Cell2);
				    employeeWagesTable.addCell(row5Cell3);
				    employeeWagesTable.addCell(row5Cell4);
				    employeeWagesTable.addCell(row5Cell5);
				    employeeWagesTable.addCell(row5Cell6);
				    employeeWagesTable.addCell(row5Cell7);
				    
				   // employeeWagesTable.addCell(row6Cell1);
				    employeeWagesTable.addCell(row6Cell2);
				    employeeWagesTable.addCell(row6Cell3);
				    employeeWagesTable.addCell(row6Cell4);
				    employeeWagesTable.addCell(row6Cell5);
				    employeeWagesTable.addCell(row6Cell6);
				    employeeWagesTable.addCell(row6Cell7);
				    
				    employeeWagesTable.addCell(row7Cell2);
				    employeeWagesTable.addCell(row7Cell3);
				    employeeWagesTable.addCell(row7Cell4);
				    employeeWagesTable.addCell(row7Cell5);
				    employeeWagesTable.addCell(row7Cell6);
				    employeeWagesTable.addCell(row7Cell7);
				    
				    employeeWagesTable.addCell(row9Cell1);
				    employeeWagesTable.addCell(row9Cell2);
				    employeeWagesTable.addCell(row9Cell3);
				    employeeWagesTable.addCell(row9Cell4);
				    employeeWagesTable.addCell(row9Cell5);
				    employeeWagesTable.addCell(row9Cell6);
				    employeeWagesTable.addCell(row9Cell7);
				    
				    
				    PdfPTable totalAndPaymentMode = new PdfPTable(2);
				    totalAndPaymentMode.setWidthPercentage(100);
				    int[] paymentModeCols = {65,35};
				    totalAndPaymentMode.setWidths(paymentModeCols);
				    
				    PdfPCell total1 = new PdfPCell(new Paragraph("6. Mode of Payment: Cash / Cheque No. \n",normal));
				    disableColumns(total1);
				    
				    PdfPCell total2 = new PdfPCell(new Paragraph("Certified that the information/particulars furnished above is/are true to the best of my knowledge & behalf",normal));
				    disableColumns(total2);
				    			    
				    PdfPCell paymentMode1 = new PdfPCell(new Paragraph("IMPORTANT \n 1) Cheque /DD should be drawn to each Estt, Code Number \n" +
				    		"Separately & in favour of Maharashtra Labour Welfare Fund. \n\n" +
				    		"2) Cash payment will be accepted from 10.30 a.m. to 3.00 p.m. \n\n" +
				    		"3) Code no. of the Establishment allotted to you should be quoted \n" +
				    		"at the appropriate place in this form. 4) DD should be payable at \n" +
				    		"BOMBAY only 5) please write the Establishment code number on \n" +
				    		"The back",normal)); 
				    disableColumns(paymentMode1);
				    
				    PdfPCell paymentMode2 = new PdfPCell(new Paragraph("Signature with name & designation of \n" +
				    		"the Authority filing this form-cum-return",normal));
				    disableColumns(paymentMode2);
				    paymentMode2.setVerticalAlignment(Element.ALIGN_BOTTOM);
				    
				    totalAndPaymentMode.addCell(total1);
				    totalAndPaymentMode.addCell(total2);
				    totalAndPaymentMode.addCell(paymentMode1);
				    totalAndPaymentMode.addCell(paymentMode2);
				    
				    				
					document.add(name);
					document.add(address);
					document.add(contact);
					document.add(formTitle);
					document.add(rule);
					document.add(blankSpace);
					document.add(noteTable);
					document.add(rule3);
					document.add(blankSpace);
					document.add(establishmentCodeTable);
					document.add(blankSpace);
					document.add(employementDetails);
					document.add(blankSpace);
					document.add(employeeWagesTable);
					document.add(blankSpace);
					//document.add(blankSpace);
					//document.add(blankSpace);
					//document.add(blankSpace);
					document.add(totalAndPaymentMode);
					document.close();
					
			}catch(Exception e){
				e.printStackTrace();
			}
			return buffer;
				
		}
		
		public void disableColumns (PdfPCell cell){
			cell.disableBorderSide(Rectangle.RIGHT);
			cell.disableBorderSide(Rectangle.BOTTOM);
			cell.disableBorderSide(Rectangle.LEFT);
			cell.disableBorderSide(Rectangle.TOP);
		}
	
	public void deleteChallanDetails()
	{
		System.out.println(" in deleteChallanDetails");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("delete from challan_details WHERE entry_date =? and is_paid=? and challan_type in ("+EMPLOYER_LWF+","+EMPLOYEE_LWF+") " +
				"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) ");
			*/
			
			StringBuilder sb=new StringBuilder();
			sb.append(" delete from challan_details WHERE entry_date =? and is_paid=? and challan_type in ("+EMPLOYER_LWF+","+EMPLOYEE_LWF+") ");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
			pst.setBoolean(2,false);
			//pst.setInt(3, uF.parseToInt(getF_org()));
			//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
			System.out.println("pst in deleteChallan ==>"+pst);
			pst.executeUpdate();
	        pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		} 
		
	}
	public void updateChallanNumber()
	{
		
		System.out.println(" in update challan number function");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		String strFinancialYearEnd = null;
		try{
			if (getFinancialYear() != null) {

				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			} else {

				strPayCycleDates = new FillFinancialYears(request)
						.fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-"
						+ strPayCycleDates[1]);

				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];

			}
			
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("UPDATE challan_details SET is_paid =?,paid_date=?,challan_no=?,cheque_no=?,added_by=? WHERE financial_year_from_date=?" +
						" and financial_year_to_date=? and entry_date =? and challan_type in ("+EMPLOYER_LWF+","+EMPLOYEE_LWF+") and is_paid = false" +
								" and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
			*/
			
			StringBuilder sb=new StringBuilder();
			sb.append("UPDATE challan_details SET is_paid =?,paid_date=?,challan_no=?,cheque_no=?,added_by=? WHERE financial_year_from_date=?" +
						" and financial_year_to_date=? and entry_date =? and challan_type in ("+EMPLOYER_LWF+","+EMPLOYEE_LWF+") and is_paid = false");
			if(sbEmp!=null && !sbEmp.equals("")){
				sb.append(" and emp_id in ("+sbEmp+")");
			}else{
				sb.append(" and emp_id in (0)");
			}
			pst=con.prepareStatement(sb.toString());
			
			pst.setBoolean(1,true);
			pst.setDate(2, uF.getDateFormat(getPaidDate(), DATE_FORMAT));
			pst.setString(3,getChallanNum());
			pst.setString(4,getCheque_no());
			pst.setInt(5,uF.parseToInt(sessionEmp_Id));
			pst.setDate(6, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(7, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(getChallanDate(), DBDATE));
			//pst.setInt(9, uF.parseToInt(getF_org()));
			//pst.setInt(10, uF.parseToInt(getF_strWLocation()));
			
			System.out.println("pst for updateChallanNumber==="+pst);
			
			pst.executeUpdate();
	        pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	public void insertUnpaidAmount()
	{
		System.out.println("in insertUnpaidAmount");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String[] strPayCycleDates = null;
		
		String strFinancialYearEnd = null;
		String months="";
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
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
			
			
//			System.out.println("totalMonths=====>"+totalMonths);
			List<String> alMonthList = Arrays.asList(totalMonths.split(","));
			if(alMonthList == null) alMonthList = new ArrayList<String>();
			
			for(int i=0;i<getEmpIds().length;i++)
			{
				double amount=0;
				for(int j=0;j<alMonthList.size();j++){
					pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where financial_year_from_date=?" +
							" and financial_year_to_date=? and month=? and salary_head_id=? and emp_id=?");
																	
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(alMonthList.get(j)));
					pst.setInt(4,EMPLOYER_LWF);
					pst.setInt(5,uF.parseToInt(getEmpIds()[i]));
					rs = pst.executeQuery();
					while(rs.next()){
						amount=rs.getDouble("amount");
					}
			        rs.close();
			        pst.close();
					
					pst = con.prepareStatement("select * from challan_details where  financial_year_from_date=? and financial_year_to_date=? " +
							"and month like '%,"+alMonthList.get(j)+",%' and emp_id=? and challan_type=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(getEmpIds()[i]));
					pst.setInt(4,EMPLOYER_LWF);
		//				System.out.println("insert pst====>"+pst);
					rs = pst.executeQuery();
					boolean flag=false;
					while(rs.next()){
						flag = true;
					}
			        rs.close();
			        pst.close();
					
					if(amount > 0.0d && !flag){
						pst = con.prepareStatement("insert into challan_details(emp_id,amount,entry_date," +
								"is_print,financial_year_from_date,financial_year_to_date,month,challan_type) values(?,?,?,?,?,?,?,?)");
						pst.setInt(1,uF.parseToInt(getEmpIds()[i]));
						pst.setDouble(2,amount);
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setBoolean(4,true);
						pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setString(7,","+alMonthList.get(j)+",");
						pst.setInt(8,EMPLOYER_LWF);
						pst.executeUpdate();
				        pst.close();
					}
				}
			}
			
			for(int i=0;i<getEmpIds().length;i++)
			{
				double amount=0;
				for(int j=0;j<alMonthList.size();j++){
					pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where financial_year_from_date=?" +
							" and financial_year_to_date=? and month=? and salary_head_id=? and emp_id=?");
																	
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(alMonthList.get(j)));
					pst.setInt(4,EMPLOYEE_LWF);
					pst.setInt(5,uF.parseToInt(getEmpIds()[i]));
					rs = pst.executeQuery();
					while(rs.next()){
						amount=rs.getDouble("amount");
					}
			        rs.close();
			        pst.close();
					
					pst = con.prepareStatement("select * from challan_details where  financial_year_from_date=? and financial_year_to_date=? " +
							"and month like '%,"+alMonthList.get(j)+",%' and emp_id=? and challan_type=?");
					pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
					pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
					pst.setInt(3,uF.parseToInt(getEmpIds()[i]));
					pst.setInt(4,EMPLOYEE_LWF);
		//				System.out.println("insert pst====>"+pst);
					rs = pst.executeQuery();
					boolean flag=false;
					while(rs.next()){
						flag = true;
					}
			        rs.close();
			        pst.close();
				
					if(amount > 0.0d && !flag){
						pst = con.prepareStatement("insert into challan_details(emp_id,amount,entry_date," +
								"is_print,financial_year_from_date,financial_year_to_date,month,challan_type) values(?,?,?,?,?,?,?,?)");
						pst.setInt(1,uF.parseToInt(getEmpIds()[i]));
						pst.setDouble(2,amount);
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setBoolean(4,true);
						pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
						pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
						pst.setString(7,","+alMonthList.get(j)+",");
						pst.setInt(8,EMPLOYEE_LWF);
						pst.executeUpdate();
				        pst.close();
					}
				}
			
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	public void viewLWFChallanPdfReports(){
		
		System.out.println("in viewLWFChallanPdfReports");
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
//		String strFinancialYearStart = null;
		String strFinancialYearEnd = null;
		
		try{
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
		StringBuilder sb=null;
		
			if (getChallanNum() != null) {
				/*pst = con.prepareStatement("select distinct(month) from challan_details where challan_no=? and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") " +
						"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
				*/
				sb=new StringBuilder();
				sb.append("select distinct(month) from challan_details where challan_no=? and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") " +
						"and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
			
				pst.setString(1, getChallanNum());
				pst.setBoolean(2, true);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				System.out.println("pst 1 in viewLWFChallanPdfReports"+pst);
			} else {

				/*pst = con.prepareStatement("select distinct(month) from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") " +
						"and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
				*/
				sb=new StringBuilder();
				sb.append("select distinct(month) from challan_details where entry_date=? and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") " +
						"and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
			    pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(2, false);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				System.out.println("pst 2 in viewLWFChallanPdfReports"+pst);

			}
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();   
			List monthList = new ArrayList();
			while (rs.next()) {
				String[] monthsArray = rs.getString("month").split(",");
				for (int i = 0; i < monthsArray.length; i++) {
					if (!monthList.contains(monthsArray[i])) {
						monthList.add(monthsArray[i]);
					}
				}
			}
	        rs.close();
	        pst.close();

			String month = "";
			for (int i = 1; i < monthList.size(); i++) {
				if (i == monthList.size() - 1) {
					month += monthList.get(i);
				} else
					month += monthList.get(i) + ",";
			}
//			System.out.println("month======>"+month);
			totalNumOfEmployee=0;
		
			List empIdList = new ArrayList();
			if (getChallanNum() != null) {
				
				/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,cheque_no from challan_details where challan_no=? " +
						"and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") and is_paid=? " +
						"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
				*/
				sb=new StringBuilder();
				sb.append("select distinct(emp_id) as emp_id,cheque_no from challan_details where challan_no=? " +
						"and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") and is_paid=?");
				
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				pst.setString(1, getChallanNum());
				pst.setBoolean(2, true);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				System.out.println("pst 3 in viewLWFChallanPdfReports"+pst);

			
			} else {
				/*pst = con.prepareStatement("select distinct(emp_id) as emp_id,cheque_no from challan_details where entry_date=? " +
						"and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") and is_paid=? " +
						"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)");
				*/
				
				sb=new StringBuilder();
				sb.append("select distinct(emp_id) as emp_id,cheque_no from challan_details where entry_date=? " +
						"and challan_type in ("+ EMPLOYER_LWF+ ","+ EMPLOYEE_LWF+ ") and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(2, false);
				//pst.setInt(3, uF.parseToInt(getF_org()));
				//pst.setInt(4, uF.parseToInt(getF_strWLocation()));
				
				System.out.println("pst 4 in viewLWFChallanPdfReports"+pst);

			}
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			StringBuilder empid_sb=new StringBuilder(); 
			while (rs.next()) {
				totalNumOfEmployee = totalNumOfEmployee + 1;
				
				String[] arr = rs.getString("emp_id").split(",");
				
				for(int i=0; arr!=null && i<arr.length; i++){
					if(arr[i]!=null && arr[i].length()>0){
						empid_sb.append(arr[i]+",");
					}
				}
					setCheque_no(rs.getString("cheque_no"));
			}
	        rs.close();
	        pst.close();
	        
			empid_sb.replace(0, empid_sb.length(), empid_sb.substring(0, empid_sb.length()-1));
			
			
			
			Map<String,String> hmLwfContribution=new HashMap<String, String>();
			
			
			if (getChallanNum() != null) {
				/*pst = con.prepareStatement("select entry_date from challan_details where financial_year_from_date=? "
								+ "and financial_year_to_date=? and challan_no=? and is_paid=? " +
								"and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?) ");
			*/
				sb=new StringBuilder();
				sb.append("select entry_date from challan_details where financial_year_from_date=? "
								+ "and financial_year_to_date=? and challan_no=? and is_paid=?");
				
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+")");
				}else{
					sb.append(" and emp_id in (0)");
				}
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, getChallanNum());
				pst.setBoolean(4, true);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
//				System.out.println("pst new=====>"+pst);
				
				System.out.println("pst 5 in viewLWFChallanPdfReports"+pst);

				rs = pst.executeQuery();
				while (rs.next()) {
					setChallanDate(rs.getString("entry_date"));
				}
		        rs.close();
		        pst.close();
				
				
				/*pst = con.prepareStatement("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?))" +
						" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit<=3000 order by _month");
				*/
				
		        sb=new StringBuilder();
		        sb.append("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=?");
		        if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in (0))");
				}
				sb.append(" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit<=3000 order by _month");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, getChallanNum());
				pst.setBoolean(4, true);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				
				System.out.println("pst 6 in viewLWFChallanPdfReports"+pst);
				rs = pst.executeQuery();
				
				while (rs.next()) {
					int empMinLimit=uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_"+rs.getString("_month")));
					empMinLimit++;
					hmLwfContribution.put("EMP_MIN_LIMIT_"+rs.getString("_month"),""+empMinLimit);
				}
		        rs.close();
		        pst.close();
				
				/*pst = con.prepareStatement("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?))" +
						" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit>3000 order by _month");
				*/
				
		        sb=new StringBuilder();
		        sb.append("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and challan_no=? and is_paid=?");
		        if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in (0))");
				}
		        sb.append(" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit>3000 order by _month");
		        pst=con.prepareStatement(sb.toString());
		        
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(3, getChallanNum());
				pst.setBoolean(4, true);
				// pst.setInt(5, uF.parseToInt(getF_org()));
				// pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println("pst 7 in viewLWFChallanPdfReports"+pst);

				rs = pst.executeQuery();
				
				while (rs.next()) {
					int erMaxLimit=uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_"+rs.getString("_month")));
					erMaxLimit++;
					hmLwfContribution.put("ER_MAX_LIMIT_"+rs.getString("_month"),""+erMaxLimit);
				}
		        rs.close();
		        pst.close();
				
				System.out.println("hmLwfContribution in pst==>"+hmLwfContribution.get("ER_MAX_LIMIT_6"));
				
			} else {
				
				
				/*pst = con.prepareStatement("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?))" +
						" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit<=3000 order by _month");
				*/
				
				sb=new StringBuilder();
				sb.append("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in (0))");
				}
				sb.append(" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit<=3000 order by _month");
				
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(4, false);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println("pst 8 in viewLWFChallanPdfReports"+pst);

				rs = pst.executeQuery();
				
				while (rs.next()) {
					int empMinLimit=uF.parseToInt(hmLwfContribution.get("EMP_MIN_LIMIT_"+rs.getString("_month")));
					empMinLimit++;
					hmLwfContribution.put("EMP_MIN_LIMIT_"+rs.getString("_month"),""+empMinLimit);
				}
		        rs.close();
		        pst.close();
				
				/*pst = con.prepareStatement("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=? and emp_id in (select emp_id from employee_official_details where org_id=? and wlocation_id=?)) " +
						" and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit>3000 order by _month");
				*/
				
		        sb=new StringBuilder();
		        sb.append("select emp_id,_month from emp_lwf_details where _month in ("+month+") and emp_id in " +
						"(select emp_id from challan_details where financial_year_from_date=? and financial_year_to_date=? " +
						" and entry_date=? and is_paid=?");
				if(sbEmp!=null && !sbEmp.equals("")){
					sb.append(" and emp_id in ("+sbEmp+"))");
				}else{
					sb.append(" and emp_id in (0))");
				}
				sb.append("and financial_year_start=? and financial_year_end=?" +
						" group by  _month,lwf_max_limit,emp_id having lwf_max_limit>3000 order by _month");
				pst=con.prepareStatement(sb.toString());
				
				pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(getChallanDate(), DBDATE));
				pst.setBoolean(4, false);
				//pst.setInt(5, uF.parseToInt(getF_org()));
				//pst.setInt(6, uF.parseToInt(getF_strWLocation()));
				pst.setDate(5,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(6,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//				System.out.println("pst new=====>"+pst);
				System.out.println("pst 9 in viewLWFChallanPdfReports"+pst);

				rs = pst.executeQuery();
				
				while (rs.next()) {
					int erMaxLimit=uF.parseToInt(hmLwfContribution.get("ER_MAX_LIMIT_"+rs.getString("_month")));
					erMaxLimit++;
					hmLwfContribution.put("ER_MAX_LIMIT_"+rs.getString("_month"),""+erMaxLimit);
				}
		        rs.close();
		        pst.close();
				
			}
			
			System.out.println("hmLwfContribution=====>"+hmLwfContribution);
			request.setAttribute("hmLwfContribution", hmLwfContribution);
			
			pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and state_id=? and org_id=?");
			pst.setDate(1,uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2,uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStateId()));
			pst.setInt(4, uF.parseToInt(getF_org()));
			System.out.println("getStateId==>"+getStateId());
			
			System.out.println("pst 10=====>"+pst);
			
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmLwfDetails=new HashMap<String, Map<String,String>>();
			while (rs.next()) {
				Map<String, String> hmInner=new HashMap<String, String>();
				hmInner.put("LWF_ID", rs.getString("lwf_id"));
				hmInner.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
				hmInner.put("FINANCIAL_YEAR_START", uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT));
				hmInner.put("FINANCIAL_YEAR_END", uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT));
				hmInner.put("EELWF_CONTRIBUTION", rs.getString("eelfw_contribution"));
				hmInner.put("ERLWF_CONTRIBUTION", rs.getString("erlfw_contribution"));
				hmInner.put("USER_ID", rs.getString("user_id"));
				hmInner.put("ENTRY_TIME", uF.getDateFormat(rs.getString("entry_timestamp"), DBDATE, DATE_FORMAT));
				hmInner.put("MIN_LIMIT", rs.getString("min_limit"));
				hmInner.put("MAX_LIMIT", rs.getString("max_limit"));
				hmInner.put("STATE_ID", rs.getString("state_id"));
				hmInner.put("MONTHS", rs.getString("months"));
				
				if(uF.parseToDouble(rs.getString("max_limit"))<=3000){
				
				//if(uF.parseToDouble(rs.getString("min_limit")) == 0 && 0 <=uF.parseToDouble(rs.getString("max_limit"))){//-----commented by asha 
					hmLwfDetails.put("INCLUSIVE",hmInner);
				}else{
					hmLwfDetails.put("EXCEEDING",hmInner);
				}
				
			}  
	        rs.close();
	        pst.close();
			request.setAttribute("hmLwfDetails",hmLwfDetails);
//			System.out.println("hmLwfDetails====>"+hmLwfDetails);
			System.out.println("getF_org in pst "+getF_org());
			
			pst = con.prepareStatement("select * from org_details where org_id=? ");
			pst.setInt(1, uF.parseToInt(getF_org()));
			System.out.println("pst 11"+pst);
			
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
				hmOrg.put("ORG_ESTABLISH_CODE", rs.getString("establish_code_no"));
			}  
	        rs.close();
	        pst.close();
			request.setAttribute("hmOrg",hmOrg);
		
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String[] getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String[] strMonth) {
		this.strMonth = strMonth;
	}

	public String getTotalMonths() {
		return totalMonths;
	}

	public void setTotalMonths(String totalMonths) {
		this.totalMonths = totalMonths;
	}

	public String getChallanType() {
		return challanType;
	}

	public void setChallanType(String challanType) {
		this.challanType = challanType;
	}

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}

	public String[] getEmpIds() {
		return empIds;
	}

	public void setEmpIds(String[] empIds) {
		this.empIds = empIds;
	}

	public String getChallanDate() {
		return challanDate;
	}

	public void setChallanDate(String challanDate) {
		this.challanDate = challanDate;
	}

	public String getChallanNum() {
		return challanNum;
	}

	public void setChallanNum(String challanNum) {
		this.challanNum = challanNum;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCheque_no() {
		return cheque_no;
	}
	public void setCheque_no(String cheque_no) {
		this.cheque_no = cheque_no;
	}

	private HttpServletResponse response;
	private HttpServletRequest request;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;

	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}
	
	public String getSbEmp() {
		return sbEmp;
	}
	public void setSbEmp(String sbEmp) {
		this.sbEmp = sbEmp;
	}

}
